package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name="test mode", group="Linear Opmode")
//@Disabled
public class winterBreakTest extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeft, backLeft, frontRight, backRight = null;
    IMU imu;
    DistanceSensor frontDistanceSensor;
    DistanceSensor backDistanceSensor;
    ColorSensor colorSensor;

    public void HardwareMap() {
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).

        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight  = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

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

    public void runEncoders(DcMotor frontLeft, DcMotor backLeft, DcMotor frontRight, DcMotor backRight) {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
            //drive via gamepad control
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double rotate = gamepad1.right_stick_x;

            setDrive(drive * 0.3, strafe * 0.3, rotate * 0.3);

            //keeps bot from slamming into walls (front/back only)
            if (frontDistanceSensor.getDistance(DistanceUnit.CM) < 10)
                setDrive(-0.2, 0, 0);
            else if (backDistanceSensor.getDistance(DistanceUnit.CM) < 10)
                setDrive(0.2, 0, 0);

            //telemetry for [all] motor powers
            telemetry.addData("Drive Status", "FL: " + frontLeft.getPower());
            telemetry.addData("Drive Status", "BL: " + backLeft.getPower());
            telemetry.addData("Drive Status", "FR: " + frontRight.getPower());
            telemetry.addData("Drive Status", "BR: " + backRight.getPower());

            //telemetry for colorSensor
            telemetry.addData("Red", "Red: " + colorSensor.red());
            telemetry.addData("Green","Green: " + colorSensor.green());
            telemetry.addData("Blue", "Blue: " + colorSensor.blue());

            //telemetry for distanceSensor
            telemetry.addData("Distance Status", "Distance from back: " + backDistanceSensor.getDistance(DistanceUnit.CM));

            telemetry.update();
        }
    }
}
