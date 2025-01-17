package org.firstinspires.ftc.teamcode;

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

@TeleOp(name="test mode", group="Linear Opmode")
//@Disabled
public class winterBreakTest extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx frontLeft, backLeft, frontRight, backRight;
    IMU imu;
    private DistanceSensor frontDistanceSensor;
    private DistanceSensor backDistanceSensor;
    private ColorSensor colorSensor;
    private DriveMode[] driveModesArray = {DriveMode.DRIVE, DriveMode.SAFE_DRIVE, DriveMode.B_DRIVE, DriveMode.SAFE_B_DRIVE};
    private int driveModeCycle = 0;
    private DriveMode currentMode = driveModesArray[driveModeCycle];
    //adjust this number to be higher/lower (val between 0 and 1) for lower speed
    private final double safeDrive = 0.2;
    private boolean aPressed = false;

    public enum DriveMode {
        DRIVE,
        SAFE_DRIVE,
        B_DRIVE,
        SAFE_B_DRIVE
    }

    public void driveSwitch() {
        driveModeCycle = (driveModeCycle + 1) % 4;
    }

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

    public void setDriveMode() {
        //drive via gamepad control
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        switch(currentMode) {
            case DRIVE:
                setDrive(drive, strafe, rotate);
                break;
            case SAFE_DRIVE:
                setDrive(drive * safeDrive, strafe * safeDrive, rotate * safeDrive);
                break;
            case B_DRIVE:
                setDrive(-drive, -strafe, rotate);
                break;
            case SAFE_B_DRIVE:
                setDrive(-drive * safeDrive, -strafe * safeDrive, rotate * safeDrive);
                break;
            default:
                setDrive(drive, strafe, rotate);
        }
        //note: rotate is the same, regardless of forward/backward movement pos)
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

        while (opModeIsActive()) {
            setDriveMode();

            //cycle through driveModes via a button
            if(gamepad1.a && !aPressed)
                driveSwitch();
            aPressed = gamepad1.a;

            currentMode = driveModesArray[driveModeCycle];

            //keeps bot from slamming into walls (front/back only)
            if (frontDistanceSensor.getDistance(DistanceUnit.CM) < 10)
                setDrive(-0.2, 0, 0);
            else if (backDistanceSensor.getDistance(DistanceUnit.CM) < 10)
                setDrive(0.2, 0, 0);

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

            //telemetry for driveMode
            telemetry.addData("Drive Mode:", currentMode.toString());
            telemetry.addData("Drive Mode Cycle: ", + driveModeCycle);

            telemetry.update();
        }
    }
}
