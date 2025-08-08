package frc.robot.subsystems;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.Lights.LEDSegment;

public class ColorSensor extends SubsystemBase {

    private final ColorSensorV3 m_colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
    private final ColorMatch m_colorMatcher = new ColorMatch();

    private final Color kWhiteTarget = Color.kWhite;

    public ColorSensor() {
        m_colorMatcher.addColorMatch(kWhiteTarget);
        System.out.println("Sensor color: " + m_colorSensor.getColor().toString() + " prox: "+m_colorSensor.getProximity());
    }

    public String hasPiece() {
        Color detectedColor = m_colorSensor.getColor();
        ColorMatchResult matchResult = m_colorMatcher.matchClosestColor(detectedColor);
        
        if (matchResult.color.equals(kWhiteTarget) && matchResult.confidence > 0.95) {
            LEDSegment.ExtraAIndicator.setColor(Lights.white.dim(1));
            return "coral";
        } else {
            LEDSegment.ExtraAIndicator.setColor(Lights.green.dim(1));
            return "algae";
        }
    }

    public boolean isObjectPresent() {
        if (m_colorSensor.getProximity() > 2000){
            LEDSegment.ExtraAIndicator.setColor(Lights.red);
        }
        return m_colorSensor.getProximity() > 2000;
    }
}