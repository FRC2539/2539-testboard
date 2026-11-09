package frc.robot.subsystems;

import java.awt.Color;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.RGBWColor;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystems.LightsSubsystem.LEDSegment;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MotorTalonSRX extends SubsystemBase {
    /** Creates a new Digital Sensor */
  //DigitalInput m_proximitySensor;  

  /** Creates a new SparkMax brushless motor */
  TalonFX m_motor1;
  TalonFX m_motor2;


  /** Creates a new DriveSubsystem. */
  public MotorTalonSRX() {
    //m_proximitySensor = new DigitalInput(0);
    m_motor1 = new TalonFX(Constants.MotorOne.MOTOR_ID);
    m_motor1.setNeutralMode(NeutralModeValue.Coast);

    m_motor2 = new TalonFX(Constants.MotorTwo.MOTOR_ID);  
    
    m_motor2.setNeutralMode(NeutralModeValue.Coast);

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

  public void setSpeedLeft(double speed) {
    m_motor1.setControl(new DutyCycleOut(speed*-1));

  }

  public void setSpeedRight(double speed) {
    m_motor2.setControl(new DutyCycleOut(speed*-1));

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
  double velocity = m_motor1.getVelocity().getValueAsDouble();
 
    double[] thresholds = {2, 14, 24, 38, 52, 64, 78, 92};
    LEDSegment[] buckles = {
        LEDSegment.Buckle1, LEDSegment.Buckle2, LEDSegment.Buckle3, LEDSegment.Buckle4,
        LEDSegment.Buckle5, LEDSegment.Buckle6, LEDSegment.Buckle7, LEDSegment.Buckle8
    };

    for (int i = 0; i < thresholds.length; i++) {
        if (velocity >= thresholds[i]) {
            buckles[i].setSolidColor(new RGBWColor(255, 0, 0, 0));
        } else if (velocity <= -thresholds[i]) {
            buckles[i].setSolidColor(new RGBWColor(0, 255, 0, 0));
        } else {
            buckles[i].setSolidColor(new RGBWColor(0, 0, 0, 0));
        }
    }
  
    double velocity2 = m_motor2.getVelocity().getValueAsDouble();
 
    double[] thresholds2 = {2, 14, 24, 38, 52, 64, 78, 92};
    LEDSegment[] buckles2 = {
        LEDSegment.Buckle1, LEDSegment.Buckle2, LEDSegment.Buckle3, LEDSegment.Buckle4,
        LEDSegment.Buckle5, LEDSegment.Buckle6, LEDSegment.Buckle7, LEDSegment.Buckle8
    };

    for (int i = 0; i < thresholds2.length; i++) {
        if (velocity2 >= thresholds2[i]) {
            buckles2[i].setSolidColor(new RGBWColor(255, 100, 100, 0));
        } else if (velocity2 <= -thresholds[i]) {
            buckles2[i].setSolidColor(new RGBWColor(100, 255, 100, 0));
        } else {
            //buckles2[i].setSolidColor(new RGBWColor(0, 0, 0, 0));
        }
    }
  


  SmartDashboard.putNumber("Motor 1 Velocity (RPS)", velocity);
  SmartDashboard.putNumber("Motor 1 Duty Cycle", m_motor1.getDutyCycle().getValueAsDouble());
}

}
