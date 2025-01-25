package robot;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Timer;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous(name = "Sample Side Auto")
public class Auto extends OpMode {



    Servo bc; // back claw
    CRServo fl; // front left gecko
    CRServo fr; // front right gecko
    CRServo wrist; // wrist pivot point
    Servo lm; // left misumi slide
    Servo rm; // right misumi slide

    Servo Blm;//back left misumi

    Servo Brm;//back right misumi








    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;


    private int pathState;

    private final Pose startPose = new Pose(9, 70, Math.toRadians(0));

    private final Pose scorePre = new Pose(34.8, 70, Math.toRadians(0));

    private final Pose g1 = new Pose(27, 121, Math.toRadians(0));

    private final Pose g2 = new Pose(27, 130, Math.toRadians(0));

    private final Pose g3 = new Pose(27, 131, Math.toRadians(32));

    private final Pose basket = new Pose(16,129,315);

    private final Pose park = new Pose(65, 100, Math.toRadians(90));

    private Path preloadS, end;
    private PathChain samp1, basket1, samp2, basket2, samp3,basket3;
    public void buildPaths() {


        preloadS = new Path(
                new BezierLine(
                        new Point(startPose),
                        new Point(scorePre)));
        preloadS.setLinearHeadingInterpolation(startPose.getHeading(), scorePre.getHeading());


        samp1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(scorePre),
                                new Point(g1)))
                .setLinearHeadingInterpolation(scorePre.getHeading(), g1.getHeading())
                .build();

        basket1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(g1),
                                new Point(basket)))
                .setLinearHeadingInterpolation(g1.getHeading(), basket.getHeading())
                .build();


        samp2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(basket),
                                new Point(g2)))
                .setLinearHeadingInterpolation(basket.getHeading(), g2.getHeading())
                .build();


        basket2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(g2),
                                new Point(basket)))
                .setLinearHeadingInterpolation(g2.getHeading(), basket.getHeading())
                .build();


        samp3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(basket),
                                new Point(g3)))
                .setLinearHeadingInterpolation(basket.getHeading(), g3.getHeading())
                .build();


        basket3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(g3),
                                new Point(basket)))
                .setLinearHeadingInterpolation(g3.getHeading(), basket.getHeading())
                .build();


        end = new Path(
                new BezierCurve(
                        new Point(basket),
                        new Point(55.7,127.8,Point.CARTESIAN),
                        new Point(park)));
        end.setLinearHeadingInterpolation(basket.getHeading(), park.getHeading());
    }

    public void autonomousPathUpdate() {
        switch (pathState) {

            case 0:

                follower.followPath(preloadS);
                setPathState(1);
                break;

            case 1:
                if(follower.atParametricEnd()) {
                    follower.followPath(samp1,true);

                    setPathState(2);
                }
                break;

            case 2:
                if(follower.atParametricEnd()) {

                    follower.followPath(basket1,true);
                    setPathState(3);
                }
                break;

            case 3:

                if(follower.atParametricEnd()) {
                    follower.followPath(samp2,true);
                    setPathState(4);
                }
                break;

            case 4:

                if(follower.atParametricEnd()) {
                    follower.followPath(basket2,true);
                    setPathState(5);
                }
                break;

            case 5:

                if(follower.atParametricEnd()) {
                    follower.followPath(samp3,true);
                    setPathState(6);
                }
                break;

            case 6:

                if(follower.atParametricEnd()) {
                    follower.followPath(basket3,true);
                    setPathState(7);
                }
                break;

            case 7:
                if(follower.atParametricEnd()) {

                    follower.followPath(end,true);
                    setPathState(-1);
                }
                break;

        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {

        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("t-value", follower.getCurrentTValue());
        telemetry.addData("Trans error mag", follower.getTranslationalError().getMagnitude());
        telemetry.addData("Heading Error mag", follower.headingError);
        telemetry.addData("Velocity mag", follower.getVelocityMagnitude());
        telemetry.update();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();



        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();

    }


    @Override
    public void init_loop() {

    }




    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void stop() {
    }
}