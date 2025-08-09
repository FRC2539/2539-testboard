// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
// import org.littletonrobotics.junction.Logger;
// import org.littletonrobotics.junction.networktables.NT4Publisher;
// import org.littletonrobotics.junction.wpilog.WPILOGReader;
// import org.littletonrobotics.junction.wpilog.WPILOGWriter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
// import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.Lights;
import frc.robot.subsystems.Lights.LEDSegment;

public class Robot extends LoggedRobot {
  private Command m_autonomousCommand;

  public final RobotContainer m_robotContainer;

  public Robot() {
    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run(); 
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {
    // Indicate if the battery is at voltage
        if (RobotController.getBatteryVoltage() > 12.3){
          //System.out.println("lights battery voltage: " + RobotController.getBatteryVoltage());
          LEDSegment.BatteryIndicator.setColor(Lights.green.dim(1));
        } else {
          LEDSegment.BatteryIndicator.setFadeAnimation(Lights.green.dim(0.25), 1);
        };

        // // Verify that all absolute encoders are connected
        // if (m_robotContainer.armSubsystem.isEncoderConnected())
        //     LEDSegment.PivotEncoderIndicator.setColor(LightsSubsystem.white.dim(1));
        // else LEDSegment.PivotEncoderIndicator.fullClear();

        // Indicate once the driver station is connected
        if (DriverStation.isDSAttached())
            LEDSegment.DriverstationIndicator.setColor(Lights.orange.dim(1));
        else LEDSegment.DriverstationIndicator.fullClear();

        // Passive Main LED Mode
        LEDSegment.MainStrip.setFadeAnimation(Lights.orange, 0.5);
  }

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {
    //System.out.println(m_robotContainer.placer.isPieceSeated());
  }

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationPeriodic() {}
}
