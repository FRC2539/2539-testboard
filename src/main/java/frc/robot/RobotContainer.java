// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.lib.controller.LogitechController;
//import frc.lib.controller.ThrustmasterJoystick;
import frc.robot.subsystems.MotorTalonSRX;
import frc.robot.subsystems.MotorSpark;



public class RobotContainer {
    
    private final LogitechController operatorController = new LogitechController(0);
    private final LogitechController operatorControllerTwo = new LogitechController(1);
    //private final MotorTalonSRX motorOne = new MotorTalonSRX(Constants.MotorOne.MOTOR_ID);
    //private final DriveTrain m_driveTrain = new DriveTrain();
    private final MotorTalonSRX motors = new MotorTalonSRX();
    private final MotorSpark sparkmotors = new MotorSpark();
    //public final Auto auto;

    public RobotContainer() {
        configureBindings();

    }           

    private void configureBindings() {
        //operatorController.getA().whileTrue(motors.setSpeed1(1.0)).whileFalse(motors.setSpeed1(0.0));
        //operatorController.getB().whileTrue(motors.setSpeed2(1.0)).whileFalse(motors.setSpeed2(0.0));

        motors.setDefaultCommand(
        new RunCommand(
            () -> {
                double leftX = operatorController.getLeftYAxis().getAsDouble(); 
                double rightX = operatorController.getRightYAxis().getAsDouble();
                motors.setSpeed1(leftX);
                motors.setSpeed2(rightX);

                double sparkleftX = operatorControllerTwo.getLeftYAxis().getAsDouble(); 
                double sparkrightX = operatorControllerTwo.getRightYAxis().getAsDouble();
                sparkmotors.setSpeed1(sparkleftX);
                sparkmotors.setSpeed2(sparkrightX);
                //System.out.println("Left Y-axis raw value: " + leftX);
                //System.out.println("Right Y-axis raw value: " + rightX);
                //motors.setSpeed1(operatorController.getLeftXAxis().getRaw());
                //motors.setSpeed2(operatorController.getRightXAxis().getRaw());
            },
            motors
        )
        );
    }


}
