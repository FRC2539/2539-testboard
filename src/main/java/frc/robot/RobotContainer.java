// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.lib.controller.LogitechController;
//import frc.lib.controller.ThrustmasterJoystick;
import frc.robot.subsystems.LightsSubsystem;
import frc.robot.subsystems.MotorTalonSRX;
import frc.robot.subsystems.MotorSpark;
// import frc.robot.subsystems.ColorSensor;
// import frc.robot.subsystems.Lights;
import frc.robot.Constants;



public class RobotContainer {
    
    private final LogitechController operatorController = new LogitechController(0);
    private final LogitechController operatorControllerTwo = new LogitechController(1);
    private final LightsSubsystem lights = new LightsSubsystem();
    //private final DriveTrain m_driveTrain = new DriveTrain();
    private final MotorTalonSRX motors = new MotorTalonSRX();
    // private final MotorSpark sparkmotors = new MotorSpark(); // disabled: Spark Max motors not connected
    // private final ColorSensor colors = new ColorSensor();


    

    //public final Auto auto;

    public RobotContainer() {
        System.out.println("pre configure ");
        configureBindings();

    }           

    private void configureBindings() {
        //operatorController.getA().whileTrue(motors.setSpeed1(1.0)).whileFalse(motors.setSpeed1(0.0));
        //operatorController.getB().whileTrue(motors.setSpeed2(1.0)).whileFalse(motors.setSpeed2(0.0));

        // operatorController.getA().onTrue(new RunCommand(() -> {
        //     motors.setSpeed2();
        // }, motors));

        // operatorController.getB().onTrue(new RunCommand(() -> {
        //     motors.stopSpeed();
        // }, motors));

        // operatorController.getB().whileTrue(new RunCommand(() -> {
        //     boolean objectPresent = colors.isObjectPresent();
        //     System.out.println("Has Piece: " + objectPresent);
        // }, colors));

        System.out.println("pre set default ");

        motors.setDefaultCommand(
        new RunCommand(
            () -> {
                double leftX = operatorController.getLeftYAxis().getAsDouble();
                double rightX = operatorController.getRightYAxis().getAsDouble();
                motors.setSpeedLeft(leftX);
                motors.setSpeedRight(rightX);
            },
            motors
        )
        );
    }


}
