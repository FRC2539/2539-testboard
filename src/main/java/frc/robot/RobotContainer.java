// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.lib.controller.LogitechController;
//import frc.lib.controller.ThrustmasterJoystick;
import frc.robot.subsystems.MotorTalonSRX;
import frc.robot.subsystems.MotorSpark;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.Lights.LightsControlModule;




public class RobotContainer {
    
    private final LogitechController operatorController = new LogitechController(0);
    private final LogitechController operatorControllerTwo = new LogitechController(1);
    //private final MotorTalonSRX motorOne = new MotorTalonSRX(Constants.MotorOne.MOTOR_ID);
    //private final DriveTrain m_driveTrain = new DriveTrain();
    private final MotorTalonSRX motors = new MotorTalonSRX();
    private final MotorSpark sparkmotors = new MotorSpark();
    private final ColorSensor colors = new ColorSensor();
    private Lights LightsSubsystem = new Lights();

    

    //public final Auto auto;

    public RobotContainer() {
        configureBindings();

    }           

    private void configureBindings() {
        //operatorController.getA().whileTrue(motors.setSpeed1(1.0)).whileFalse(motors.setSpeed1(0.0));
        //operatorController.getB().whileTrue(motors.setSpeed2(1.0)).whileFalse(motors.setSpeed2(0.0));

        operatorController.getA().whileTrue(new RunCommand(() -> {
            String pieceType = colors.hasPiece();
            //System.out.println("Detected Piece: " + pieceType);
        }, colors));

        operatorController.getB().whileTrue(new RunCommand(() -> {
            boolean objectPresent = colors.isObjectPresent();
            System.out.println("Has Piece: " + objectPresent);
        }, colors));

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
            },
            motors
        )
        );
        
        LightsControlModule.Supplier_batteryVoltage(() -> RobotController.getBatteryVoltage());
        LightsControlModule.Supplier_opControllerLeftX(() -> operatorController.getLeftXAxis().get());
        LightsControlModule.Supplier_opControllerLeftY(() -> operatorController.getLeftYAxis().get());
        LightsControlModule.Supplier_opControllerRightX(() -> operatorController.getRightXAxis().get());
        LightsControlModule.Supplier_opControllerRightY(() -> operatorController.getRightYAxis().get());
    }


}
