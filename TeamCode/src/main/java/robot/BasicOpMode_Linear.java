/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package robot;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/*
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name="Tele op test", group="Linear OpMode")

public class BasicOpMode_Linear extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;


    Servo backClaw; // back claw
    CRServo LeftGeko; // front left gecko
    CRServo RightGeko; // front right gecko
    CRServo wrist; // wrist pivot point
    Servo leftmisumi    ; // left misumi slide
    Servo rightmisumi; // right misumi slide

    Servo BackLeftMisumi;//back left misumi

    Servo BackRightMisumi;//back right misumi

//    double cp;
//    double dd = gamepad2.right_stick_y;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor rightRear = hardwareMap.get(DcMotor.class, "rightRear");



        DcMotor MLeftBackMisumi = hardwareMap.get(DcMotor.class, "lback");//left back misumi motor
        DcMotor MRightckMisumi = hardwareMap.get(DcMotor.class,"rback");//right back misumi motor
        DcMotor LeftLinkage = hardwareMap.get(DcMotor.class, "Lli");//lef linkage
        DcMotor RightLinkage = hardwareMap.get(DcMotor.class,"Rli");//right linkage
        // Init servos
        backClaw = hardwareMap.get(Servo.class, "bc");

        wrist = hardwareMap.get(CRServo.class, "wrist");

        RightGeko = hardwareMap.get(CRServo.class, "fr");
        LeftGeko = hardwareMap.get(CRServo.class, "fl");

        leftmisumi = hardwareMap.get(Servo.class, "lm");//front slide
        rightmisumi = hardwareMap.get(Servo.class, "rm");

        BackLeftMisumi = hardwareMap.get(Servo.class, "BLM");
        BackRightMisumi = hardwareMap.get(Servo.class, "BRM");


        MLeftBackMisumi.setDirection(DcMotorSimple.Direction.REVERSE);
        MLeftBackMisumi.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        MLeftBackMisumi.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        MRightckMisumi.setDirection(DcMotorSimple.Direction.REVERSE);
        MRightckMisumi.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        MRightckMisumi.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        LeftLinkage.setDirection(DcMotorSimple.Direction.REVERSE);
        LeftLinkage.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LeftLinkage.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RightLinkage.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLinkage.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RightLinkage.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;

            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
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


            if(gamepad2.x){
                backClaw.setPosition(0.35);
            }
            if(gamepad2.b){
                backClaw.setPosition(0.5);
            }



//            cp    = Range.clip(dd, -0.5, 0.5) ;
//            LeftLinkage.setPower(cp);
//            RightLinkage.setPower(-cp);


            if(gamepad2.y){
                leftmisumi.setPosition(-0.3);
            }
            while(gamepad2.y){
                LeftGeko.setPower(1);
                RightGeko.setPower(-1);
            }
            while(gamepad2.a){
                LeftGeko.setPower(-1);
                RightGeko.setPower(1);
            }
            while(gamepad2.dpad_right){
                LeftLinkage.setPower(1);
                RightLinkage.setPower(-1);
            }
            while(gamepad2.dpad_left){
                LeftLinkage.setPower(-1);
                RightLinkage.setPower(1);
            }
            while(gamepad2.dpad_up){
                BackLeftMisumi.setPosition(1);
                BackRightMisumi.setPosition(-1);
            }
            while(gamepad2.dpad_down){
                BackLeftMisumi.setPosition(-1);
                BackRightMisumi.setPosition(1);
            }


        }
    }
}
