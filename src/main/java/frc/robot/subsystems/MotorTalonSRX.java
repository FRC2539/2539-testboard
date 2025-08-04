package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class MotorTalonSRX extends SubsystemBase {
    /** Creates a new Digital Sensor */
  DigitalInput m_proximitySensor;

  /** Creates a new SparkMax brushless motor */
  TalonSRX m_motor;

  /** Creates a new DriveSubsystem. */
  public MotorTalonSRX() {
    m_proximitySensor = new DigitalInput(0);
    m_motor = new TalonSRX(0);
  }

  /** Run motor at half speed during command */
  public Command runMotorCommand() {
    return runEnd(
      () -> m_motor.set(ControlMode.PercentOutput, 0.5), 
      () -> m_motor.set(ControlMode.PercentOutput, 0)
    );
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public boolean isSensorActive() {
    return m_proximitySensor.get();
  }

  public void setRawMotorSpeed(double speed) {
    m_motor.set(ControlMode.PercentOutput, speed);
  }
}
