package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class MotorTalonSRX extends SubsystemBase {
    /** Creates a new Digital Sensor */
  //DigitalInput m_proximitySensor;  

  /** Creates a new SparkMax brushless motor */
  TalonFX m_motor1;
  TalonFX m_motor2;
  TalonSRX m_motor3;
  TalonSRX m_motor4;


  /** Creates a new DriveSubsystem. */
  public MotorTalonSRX() {
    //m_proximitySensor = new DigitalInput(0);
    m_motor1 = new TalonFX(Constants.MotorOne.MOTOR_ID);
    m_motor1.setNeutralMode(NeutralModeValue.Coast);

    m_motor2 = new TalonFX(Constants.MotorTwo.MOTOR_ID);  
    m_motor3 = new TalonSRX(2);
    m_motor4 = new TalonSRX(3);
    

    
  }

  /** Run motor at half speed during command */
  public Command runMotorCommand() {
    return runEnd(
      () -> m_motor1.setControl(new DutyCycleOut(0.5)), 
      () -> m_motor1.setControl(new DutyCycleOut(0))
    );
  }

  // public Command setFalconVoltage(int speed) {
  //   return runOnce(
  //       () -> {
  //         m_motor1.set(ControlMode.PercentOutput, speed);
  //       });
  // }


  
  

  // public boolean isSensorActive() {
  //   return m_proximitySensor.get();
  // }

  public void setSpeed1(double speed) {
    m_motor1.setControl(new DutyCycleOut(speed*-1));
    //System.out.println("left output: " + speed);
  }

  public void setSpeed2() {
    m_motor1.setControl(new DutyCycleOut(-.15));
    //System.out.println("right output: " + speed);
  }

  public void stopSpeed() {
    m_motor1.setControl(new DutyCycleOut(0));
    //System.out.println("right output: " + speed);
  }

  // public void setSpeed3(double speed) {
  //   m_motor3.set(speed);
  //   //System.out.println("right output: " + speed);
  // }
  // public void setSpeed4(double speed) {
  //   m_motor4.set(speed);
  //   //System.out.println("right output: " + speed);
  // }

@Override
public void periodic() {
  // This method will be called once per scheduler run

}

}
