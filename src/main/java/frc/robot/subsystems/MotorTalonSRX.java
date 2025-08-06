package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class MotorTalonSRX extends SubsystemBase {
    /** Creates a new Digital Sensor */
  //DigitalInput m_proximitySensor;  

  /** Creates a new SparkMax brushless motor */
  TalonSRX m_motor1;
  TalonSRX m_motor2;

  /** Creates a new DriveSubsystem. */
  public MotorTalonSRX() {
    //m_proximitySensor = new DigitalInput(0);
    m_motor1 = new TalonSRX(Constants.MotorOne.MOTOR_ID);
    m_motor2 = new TalonSRX(Constants.MotorTwo.MOTOR_ID);
  }

  /** Run motor at half speed during command */
  public Command runMotorCommand() {
    return runEnd(
      () -> m_motor1.set(ControlMode.PercentOutput, 0.5), 
      () -> m_motor1.set(ControlMode.PercentOutput, 0)
    );
  }

  public Command setFalconVoltage(int speed) {
    return runOnce(
        () -> {
          m_motor1.set(ControlMode.PercentOutput, speed);
        });
  }

  // public boolean isSensorActive() {
  //   return m_proximitySensor.get();
  // }

  public void setSpeed1(double speed) {
    m_motor1.set(ControlMode.PercentOutput, speed);
    System.out.println("left output: " + speed);
  }

  public void setSpeed2(double speed) {
    m_motor2.set(ControlMode.PercentOutput, speed);
    System.out.println("right output: " + speed);
  }

@Override
public void periodic() {
  // This method will be called once per scheduler run

}

}
