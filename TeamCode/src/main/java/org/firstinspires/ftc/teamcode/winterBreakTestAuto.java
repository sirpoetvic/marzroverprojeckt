package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.util.HashMap;

@Autonomous(name="test autonomous")
//@Disabled
public class winterBreakTestAuto extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx frontLeft, backLeft, frontRight, backRight;
    IMU imu;
    private DistanceSensor frontDistanceSensor;
    private DistanceSensor backDistanceSensor;
    private ColorSensor colorSensor;

    //encoder information
    private double ticksPerRevolution = 537.6;
    //9.8 cm
    private double wheelDiameter = 9.8;


    public void HardwareMap() {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        frontRight  = hardwareMap.get(DcMotorEx.class, "frontRight");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");
        frontDistanceSensor = hardwareMap.get(DistanceSensor.class, "frontDistanceSensor");
        backDistanceSensor = hardwareMap.get(DistanceSensor.class, "backDistanceSensor");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");

    }

    public void setPower(double frontLeftPower, double backLeftPower, double frontRightPower, double backRightPower) {
        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
    }


    public void setDrive(double drive, double strafe, double rotate) {
        double frontLeftPower = drive + rotate + strafe;
        double backLeftPower = drive + rotate - strafe;
        double frontRightPower = drive - rotate - strafe;
        double backRightPower = drive - rotate + strafe;
        setPower(frontLeftPower, backLeftPower, frontRightPower, backRightPower);
    }

    public int distToTicks(int distanceInCM) {
        return (int) ((distanceInCM * ticksPerRevolution) / (wheelDiameter * Math.PI));
    }

    public int ticksToDist(int ticks) {
        return (int) ((ticks * wheelDiameter * Math.PI) / ticksPerRevolution);
    }

    public void runEncoders(DcMotorEx frontLeft, DcMotorEx backLeft, DcMotorEx frontRight, DcMotorEx backRight) {
        frontLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        HardwareMap();
        runEncoders(frontLeft, backLeft, frontRight, backRight);

        waitForStart();
        runtime.reset();

        if (opModeIsActive()) {

            frontLeft.setTargetPosition(distToTicks(10));
            backLeft.setTargetPosition(distToTicks(10));
            frontRight.setTargetPosition(distToTicks(10));
            backRight.setTargetPosition(distToTicks(10));

            //telemetry for [all] motor powers
            telemetry.addData("FL: ", frontLeft.getPower());
            telemetry.addData("BL: ", backLeft.getPower());
            telemetry.addData("FR: ", frontRight.getPower());
            telemetry.addData("BR: ", backRight.getPower());

            //telemetry for colorSensor
            telemetry.addData("Red: ", colorSensor.red());
            telemetry.addData("Green", colorSensor.green());
            telemetry.addData("Blue", colorSensor.blue());

            //telemetry for distance sensors
            telemetry.addData("Distance from front: ", frontDistanceSensor.getDistance(DistanceUnit.CM));
            telemetry.addData("Distance from back: ", backDistanceSensor.getDistance(DistanceUnit.CM));

            telemetry.update();
        }
    }
}
