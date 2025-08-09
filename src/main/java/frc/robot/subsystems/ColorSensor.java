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

    private final Color kCoralTarget = new Color(0.198, 0.443, 0.358); //new Color(67, 122, 68); //234,232,231  (R: 67, G: 122, B: 66)
    private final Color kAlgaeTarget = new Color(0.149, .541, .309); //88,180,177 (R: 38, G: 138, B: 79)


    public ColorSensor() {
        m_colorSensor.configureColorSensor(ColorSensorV3.ColorSensorResolution.kColorSensorRes16bit, ColorSensorV3.ColorSensorMeasurementRate.kColorRate1000ms,ColorSensorV3.GainFactor.kGain18x);
        m_colorMatcher.addColorMatch(kCoralTarget);
        m_colorMatcher.addColorMatch(kAlgaeTarget);

    }

    public String hasPiece() {
        Color detectedColor = m_colorSensor.getColor();
        ColorMatchResult matchResult = m_colorMatcher.matchClosestColor(detectedColor);
        System.out.println("mcolor: "+matchResult.color.toString() +" color:  (R: " + (int) Math.round(detectedColor.red * 255) + ", G: " + (int) Math.round(detectedColor.green * 255) + ", B: " + (int) Math.round(detectedColor.blue * 255) + ") conf: " + matchResult.confidence);

        if (m_colorSensor.getProximity() > 130) {
            if (matchResult.color.equals(kCoralTarget) && matchResult.confidence > 0.88) {
                LEDSegment.BuckleIndicator.setStrobeAnimation(Lights.white,.1);
                //System.out.println("coral color: " + matchResult.color + " conf: " + matchResult.confidence);
                return "coral: (R: " + (int) Math.round(detectedColor.red * 255) + ", G: " + (int) Math.round(detectedColor.green * 255) + ", B: " + (int) Math.round(detectedColor.blue * 255) + ")";
            } else if (matchResult.color.equals(kAlgaeTarget) && matchResult.confidence > 0.88) {
                LEDSegment.BuckleIndicator.setStrobeAnimation(Lights.green,.1);
                //System.out.println("algae color: " + matchResult.color + " conf: " + matchResult.confidence);
                return "algae: (R: " + (int) Math.round(detectedColor.red * 255) + ", G: " + (int) Math.round(detectedColor.green * 255) + ", B: " + (int) Math.round(detectedColor.blue * 255) + ")";
            } else {
                LEDSegment.BuckleIndicator.setColor(new Lights.Color(
                    (int) (detectedColor.red * 255 *2),
                    (int) (detectedColor.green * 255 *2),
                    (int) (detectedColor.blue * 255 *2)
                ).dim(1));
                return "unknown: (R: " + (int) Math.round(detectedColor.red * 255) + ", G: " + (int) Math.round(detectedColor.green * 255) + ", B: " + (int) Math.round(detectedColor.blue * 255) + ")";
            }
        } else {
            // No piece is present
            LEDSegment.BuckleIndicator.setColor(Lights.black); // Turn off the LED
            return "none";
        }
    }

    public boolean isObjectPresent() {
        if (m_colorSensor.getProximity() > 130){
            LEDSegment.BuckleIndicator.setColor(Lights.red);
        } else{
            LEDSegment.BuckleIndicator.setColor(Lights.black);
        }
        System.out.println("prox: "+m_colorSensor.getProximity());
        return m_colorSensor.getProximity() > 130;
    }
}