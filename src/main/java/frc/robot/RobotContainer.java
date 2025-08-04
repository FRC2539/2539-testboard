// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import frc.lib.controller.LogitechController;
//import frc.lib.controller.ThrustmasterJoystick;
import frc.robot.subsystems.MotorTalonSRX;

public class RobotContainer {
    
    private final LogitechController operatorController = new LogitechController(2);
    //private final WPI_TalonSRX motorOne = new WPI_TalonSRX(Constants.MotorOne.MOTOR_ID);
    private final MotorTalonSRX motorOne = new MotorTalonSRX();
    //public final Auto auto;

    public RobotContainer() {
        configureBindings();

    }           

    private void configureBindings() {
        operatorController.getA().whileTrue(motorOne.runMotorCommand());
        
    }


}
