package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib.controller.LogitechController;
import frc.robot.Constants;

public class MotorTalonSRX extends SubsystemBase {
    /** Creates a new Digital Sensor */
  DigitalInput m_proximitySensor;

  private final LogitechController operatorController = new LogitechController(0);

  /** Creates a new SparkMax brushless motor */
  TalonSRX m_motor;

  /** Creates a new DriveSubsystem. */
  public MotorTalonSRX() {
    m_proximitySensor = new DigitalInput(0);
    m_motor = new TalonSRX(Constants.MotorOne.MOTOR_ID);
  }

  /** Run motor at half speed during command */
  public Command runMotorCommand() {
    return runEnd(
      () -> m_motor.set(ControlMode.PercentOutput, 0.5), 
      () -> m_motor.set(ControlMode.PercentOutput, 0)
    );
  }

  public Command setFalconVoltage(int speed) {
    return runOnce(
        () -> {
          m_motor.set(ControlMode.PercentOutput, speed);
        });
  }

  public boolean isSensorActive() {
    return m_proximitySensor.get();
  }

  public Command setSpeed1() {
    return runOnce(
        () -> {
          m_motor.set(ControlMode.PercentOutput, getLeftControllerXAxis());
          System.out.println("left output: "+getLeftControllerXAxis());
        });
  }

  public double getLeftControllerXAxis() {
    return operatorController.getLeftXAxis().getAsDouble();
}

@Override
public void periodic() {
  // This method will be called once per scheduler run
  setSpeed1();
}

}
