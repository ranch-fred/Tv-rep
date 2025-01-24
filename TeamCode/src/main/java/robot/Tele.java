package robot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

//4:45 1/23/25 if i get this to work arjun owes me pizza for a week
@TeleOp(name = "TeleOp")
public class Tele extends LinearOpMode {
    public static double kP_i = 0.02;
    public static double kP_o = 0.01;

    public enum States {
        HOME, // where everything is idle and retracted
        INTAKE,
        SPEC_HEIGHT,
        SAMPLE_HEIGHT
    }
    States currentState = States.HOME;

    public static double intakeTarget = 0;
    public static double outtakeTarget = 0;

    public static double intakeOut = 490; //change
    public static double intakeIn = 0;

    public static double outtakeIn = 0;
    public static double outtakeSpec = 2600; //change
    public static double outtakeSamp = 3000;//change

    Servo bc; // back claw
    CRServo fl; // front left gecko
    CRServo fr; // front right gecko
    CRServo wrist; // wrist pivot point
    Servo lm; // left misumi slide
    Servo rm; // right misumi slide

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor rightRear = hardwareMap.get(DcMotor.class, "rightRear");

        // Initialize slide motors
        DcMotor back = hardwareMap.get(DcMotor.class, "back");
        DcMotor li = hardwareMap.get(DcMotor.class, "lr");
        // Init servos
        bc = hardwareMap.get(Servo.class, "bc");

        wrist = hardwareMap.get(CRServo.class, "wrist");

        fr = hardwareMap.get(CRServo.class, "fr");
        fl = hardwareMap.get(CRServo.class, "fl");

        lm = hardwareMap.get(Servo.class, "lm");
        rm = hardwareMap.get(Servo.class, "rm");


        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);

        back.setDirection(DcMotorSimple.Direction.REVERSE);
        back.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        li.setDirection(DcMotorSimple.Direction.REVERSE);
        li.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        li.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Ensure motor resists motion when idle
        li.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        back.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();


        if (isStopRequested()) return;

        while (opModeIsActive()) {
            switch (currentState) {
                case HOME:
                    intakeTarget = intakeIn;
                    outtakeTarget = outtakeIn;

                    controlIntake(gamepad2);
                    controlClaw(gamepad2);

                    if (gamepad2.y) {
                        currentState = States.SPEC_HEIGHT;
                    }

                    if (gamepad2.left_bumper) {
                        currentState = States.INTAKE;
                    }

                    if (gamepad2.right_bumper){
                        currentState = States.SAMPLE_HEIGHT;
                    }
                    break;

                case SPEC_HEIGHT:
                    outtakeTarget = outtakeSpec;
                    controlClaw(gamepad2);
                    controlWrist(gamepad2);
                    if (gamepad2.a) {
                        currentState = States.HOME;
                    }
                    break;

                case SAMPLE_HEIGHT:
                    outtakeTarget = outtakeSamp;
                    controlClaw(gamepad2);
                    controlWrist(gamepad2);
                    if (gamepad2.a){
                        currentState = States.HOME;
                    }

                case INTAKE:
                    intakeTarget = intakeOut;
                    controlIntake(gamepad2);
                    controlSample(gamepad2);

                    if (gamepad2.a) {
                        currentState = States.HOME;
                    }
                    break;

            }

            // drive
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x; // this is strafing
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double leftFrontPower = (y + x + rx) / denominator;
            double leftRearPower = (y - x + rx) / denominator;
            double rightFrontPower = (y - x - rx) / denominator;
            double rightRearPower = (y + x - rx) / denominator;

            leftFront.setPower(leftFrontPower);
            leftRear.setPower(leftRearPower);
            rightFront.setPower(rightFrontPower);
            rightRear.setPower(rightRearPower);

            back.setPower(outtakePid(outtakeTarget, back.getCurrentPosition()));
            li.setPower(intakePid(intakeTarget, li.getCurrentPosition()));

            telemetry.addData("Back Slides Position: ", back.getCurrentPosition());
            telemetry.addData("Intake Slides Position: ", li.getCurrentPosition());
            telemetry.addData("State: ", currentState);
            telemetry.update();
        }
    }

    public double intakePid(double target, double current) {
        return (target - current) * kP_i;
    }

    public double outtakePid(double target, double current) {
        return (target - current) * kP_o;
    }


    // method cult
    public void transferClaw() {
        bc.setPosition(-1.0);
    }

    public void specClaw() {
        bc.setPosition(0.5);
    }

    public void sampClaw(){
        bc.setPosition(1.0);
    }

    public void intakeSample() {
        fl.setPower(-1.0);
        fr.setPower(1.0);
    }

    public void outtakeSample() {
        fl.setPower(1.0);
        fr.setPower(-1.0);
    }

    public void idleSample() {
        fl.setPower(0);
        fr.setPower(0);
    }


    public void leftWrist() {
        wrist.setPower(0.5);
    }

    public void rightWrist() {
        wrist.setPower(-0.5);
    }

    public void idleWrist() {
        wrist.setPower(0);
    }


    public void upIntake() {
        lm.setPosition(0); // to be tuned cuz idk what the actual positions r
        rm.setPosition(0); // to be tuned cuz idk what the actual positions r
    }

    public void downIntake() {
        lm.setPosition(0.5); // to be tuned cuz idk what the actual positions r
        rm.setPosition(0.5); // to be tuned cuz idk what the actual positions r
        }



    public void controlClaw(Gamepad gamepad) {
        if (gamepad2.b) {
            transferClaw();
        } else if (gamepad2.x) {
            specClaw();
        }
        else if (gamepad2.dpad_left){
          sampClaw();
        }
    }

    public void controlSample(Gamepad gamepad) {
        if (gamepad2.right_stick_button) {
            intakeSample();
        } else if (gamepad2.left_stick_button) {
            outtakeSample();
        } else {
            idleSample();
        }
    }

    public void controlWrist(Gamepad gamepad) {
        if (gamepad2.dpad_left) {
            leftWrist();
        } else if (gamepad2.dpad_right) {
            rightWrist();
        } else {
            idleWrist();
        }
    }

    public void controlIntake(Gamepad gamepad) { //check
        if (gamepad2.dpad_up) {
            upIntake();
        } else if (gamepad2.dpad_down) {
            downIntake();
        }

    }
}
