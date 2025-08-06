package frc.robot.subsystems;
import com.revrobotics.spark.SparkMax;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class MotorSpark extends SubsystemBase {
    private final SparkMax sparkOne;
    private final SparkMax sparkTwo;

    public MotorSpark() {
        sparkOne = new SparkMax(Constants.SparkMotorOne.MOTOR_ID, MotorType.kBrushed);
        sparkOne.setCANTimeout(250);
        SparkMaxConfig SparkOneMaxConfig = new SparkMaxConfig();
        SparkOneMaxConfig.voltageCompensation(Constants.SparkMotorOne.ROLLER_MOTOR_VOLTAGE_COMP);
        SparkOneMaxConfig.smartCurrentLimit(Constants.SparkMotorOne.ROLLER_MOTOR_CURRENT_LIMIT);
        sparkOne.configure(SparkOneMaxConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        sparkTwo = new SparkMax(Constants.SparkMotorTwo.MOTOR_ID, MotorType.kBrushed);
        sparkTwo.setCANTimeout(250);
        SparkMaxConfig SparkTwoMaxConfig = new SparkMaxConfig();
        SparkTwoMaxConfig.voltageCompensation(Constants.SparkMotorTwo.ROLLER_MOTOR_VOLTAGE_COMP);
        SparkTwoMaxConfig.smartCurrentLimit(Constants.SparkMotorTwo.ROLLER_MOTOR_CURRENT_LIMIT);
        sparkOne.configure(SparkTwoMaxConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
      }

      @Override
      public void periodic() {
      }
    
      /** This is a method that makes the roller spin */
    //   public void runSpark1(double forward, double reverse) {
    //     sparkOne.set(forward - reverse);
    //   }

        public void setSpeed1(double speed) {
            sparkOne.set(speed);
            System.out.println("spark left output: " + speed);
        }

        public void setSpeed2(double speed) {
            sparkOne.set(speed);
            System.out.println("spark right output: " + speed);
        }
}
