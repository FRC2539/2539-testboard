package frc.robot.subsystems;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdle.VBatOutputMode;
import com.ctre.phoenix.led.CANdleConfiguration;
import com.ctre.phoenix.led.ColorFlowAnimation;
import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.ctre.phoenix.led.FireAnimation;
import com.ctre.phoenix.led.LarsonAnimation;
import com.ctre.phoenix.led.LarsonAnimation.BounceMode;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.SingleFadeAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
// import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import org.littletonrobotics.junction.networktables.LoggedNetworkBoolean;

public class Lights extends SubsystemBase{
    public static final class LightsConstants {
        public static final int CANDLE_PORT = 13;

        public static final int SENSOR_PORT = 0;
    }

    private BooleanSupplier algaeMode = () -> false;

    private static final CANdle candle;

    private static final boolean isReal = true;
  
  static LoggedNetworkBoolean timerEnabled =
      new LoggedNetworkBoolean("Lights Timer Warnings", true);

    static {
        if (RobotBase.isReal() && isReal) {
            candle = new CANdle(LightsConstants.CANDLE_PORT, "roboRIO");
        } else {
            candle = null;
        }
        // Establish the timer for later use
        LightsControlModule.RobotStatusTimer = new Timer();
    }

    // #region Color Defs
    
    // Team colors
    public static final Color orange = new Color(255, 25, 0);
    public static final Color black = new Color(0, 0, 0);

    // Game piece colors
    public static final Color yellow = new Color(242, 60, 0);
    public static final Color purple = new Color(184, 0, 185);

    // Indicator colors
    public static final Color white = new Color(255, 230, 220);
    public static final Color green = new Color(56, 209, 0);
    public static final Color blue = new Color(8, 32, 255);
    public static final Color red = new Color(255, 0, 0);
    public static final Color gray = new Color(75, 75, 75);
    public static final Color brown = new Color(170, 130, 50);
    
    // #endregion

    public Lights() {
        if (candle != null) {
            CANdleConfiguration candleConfiguration = new CANdleConfiguration();
            candleConfiguration.statusLedOffWhenActive = true;
            candleConfiguration.disableWhenLOS = false;
            candleConfiguration.stripType = LEDStripType.RGB;
            candleConfiguration.brightnessScalar = 1.0;
            candleConfiguration.vBatOutputMode = VBatOutputMode.Modulated;
            candle.configAllSettings(candleConfiguration, 100);
        }

        setDefaultCommand(defaultCommand());
    }

    public static void setBrightness(double percent) {
        if (candle != null) {
            candle.configBrightnessScalar(percent, 100);
        }
    }

    public Command defaultCommand() {
        return run(() -> {
            // LEDSegment.BatteryIndicator.fullClear();
            // LEDSegment.DriverstationIndicator.fullClear();
            // LEDSegment.ExtraAIndicator.fullClear();
            // LEDSegment.ExtraBIndicator.fullClear();
            // LEDSegment.PivotEncoderIndicator.fullClear();
            LightsControlModule.update();
        })
        .ignoringDisable(true);
    }

    public void setAlgaeModeSupplier(BooleanSupplier algaeMode) {
        this.algaeMode = algaeMode;
    }

    public Command clearSegmentCommand(LEDSegment segment) {
        return runOnce(
                () -> {
                    segment.clearAnimation();
                    segment.disableLEDs();
                });
    }

    // Condensed storage for animations, called when setting animations
    public static class LightsControlModule {
        public enum RobotStatus {
        Disabled,
        Teleop,
        Autonomous,
        Test
        }

        enum mode {
        disabled,
        paused,
        manual,
        strobe,
        fade,
        waiting,
        flow,
        disabledLoaded,
        autoLoaded,
        fire,
        autoFire,
        rainbow,
        intake,
        brownOut,
        alignLeft,
        alignLeftNear,
        alignLeftFar,
        alignRight,
        alignRightNear,
        alignRightFar,
        alignCenter,
        alignCenterNear,
        alignCenterFar,
        timeRemainingA,
        timeRemainingB,
        timeRemainingC,
        testProgress,
        matchProgress
        }

        static mode lightMode = mode.disabled;

        static double alignToleranceMin = 5;
        static double alignToleranceMax = 100;

        // #region Robot Status
        static Timer RobotStatusTimer;
        static RobotStatus robotStatus = RobotStatus.Disabled;

        public static void setRobotStatus(RobotStatus newStatus) {
            robotStatus = newStatus;
            // Restart the timer for the new status
            RobotStatusTimer.reset();
            RobotStatusTimer.start();
        }
        // #endregion
        // #region Suppliers
        static BooleanSupplier hasPiece = () -> false;
        static BooleanSupplier isAligning = () -> false;
        static IntSupplier alignMode = () -> 0;
        static DoubleSupplier batteryVoltage = () -> 0;
        static DoubleSupplier opControllerLeftX = () -> 0;
        static DoubleSupplier opControllerLeftY = () -> 0;
        static DoubleSupplier opControllerRightX = () -> 0;
        static DoubleSupplier opControllerRightY = () -> 0;
        static DoubleSupplier opControllerLeftMagnitude =
            () -> Math.hypot(opControllerLeftX.getAsDouble(), opControllerLeftY.getAsDouble());
        static DoubleSupplier opControllerRightMagnitude =
            () -> Math.hypot(opControllerRightX.getAsDouble(), opControllerRightY.getAsDouble());

        public static void Supplier_hasPiece(BooleanSupplier sup) {
        hasPiece = sup;
        }

        public static void Supplier_isAligning(BooleanSupplier sup) {
        isAligning = sup;
        }

        public static void Supplier_alignMode(IntSupplier sup) {
        alignMode = sup;
        }

        public static void Supplier_batteryVoltage(DoubleSupplier sup) {
        batteryVoltage = sup;
        }

        public static void Supplier_opControllerLeftX(DoubleSupplier sup) {
        opControllerLeftX = sup;
        }

        public static void Supplier_opControllerLeftY(DoubleSupplier sup) {
        opControllerLeftY = sup;
        }

        public static void Supplier_opControllerRightX(DoubleSupplier sup) {
        opControllerRightX = sup;
        }

        public static void Supplier_opControllerRightY(DoubleSupplier sup) {
        opControllerRightY = sup;
        }
        // #endregion

        enum cardinalDirection {
        North,
        East,
        South,
        West
        }

        static cardinalDirection getJoystickCardinal(double x, double y) {
        if (y >= -x) {
            if (y >= x) return cardinalDirection.North;
            return cardinalDirection.East;
        } else {
            if (y >= x) return cardinalDirection.West;
            return cardinalDirection.South;
        }
        }

        public static void update() {
        // #region Emote Wheel
        System.out.println(opControllerRightX.getAsDouble());
        if (opControllerLeftMagnitude.getAsDouble() > 0.01) {
            cardinalDirection dir =
                getJoystickCardinal(opControllerLeftX.getAsDouble(), opControllerLeftY.getAsDouble());
            switch (dir) {
            case North:
                rainbow();
                break;
            case East:
                testProgress();
                break;
            case South:
                intake();
                break;
            case West:
                disabledLoaded();
                break;
            }
            return;
        }
        if (opControllerRightMagnitude.getAsDouble() > 0.01) {
            cardinalDirection dir =
                getJoystickCardinal(opControllerRightX.getAsDouble(), opControllerRightY.getAsDouble());
            switch (dir) {
            case North:
                matchProgress();
                break;
            case East:
                autoFire();
                break;
            case South:
                matchProgress();
                break;
            case West:
                break;
            }
            return;
        }
        // #endregion
        // #region Disabled Logic
        if (robotStatus == RobotStatus.Disabled) {
            if (batteryVoltage.getAsDouble() <= 11) {
            brownOut();
            return;
            }
            double seconds = RobotStatusTimer.get();
            if (seconds > 300) {
            rainbow();
            return;
            }
            // Has Piece
            if (hasPiece.getAsBoolean()) {
            fire();
            return;
            }
            flow();
            return;
        }
        // #endregion
        // #region Enabled Logic
        if (robotStatus == RobotStatus.Teleop) {
            // Align Mode
            if (isAligning.getAsBoolean()) {
            /*
            int alignModeInt = alignMode.getAsInt();
            if (alignModeInt == Superstructure.Align.leftAlign.ordinal()) {
                alignLeft(10); // TODO: get distance to tag
                return;
            }
            if (alignModeInt == Superstructure.Align.rightAlign.ordinal()) {
                alignRight(10); // TODO: get distance to tag
                return;
            }
            if (alignModeInt == Superstructure.Align.algaeAlign.ordinal()) {
                alignCenter(10); // TODO: get distance to tag
                return;
            }
            */
            // Idle
            fade();
            return;
            }
            // HasPiece
            if (hasPiece.getAsBoolean()) {
            strobe();
            return;
            }

            // Brown Out
            if (batteryVoltage.getAsDouble() <= 11) {
            brownOut();
            return;
            }

            // Idle
            double seconds = RobotStatusTimer.get(); // Teleop length = 2:15, which is 135 seconds
            if (!Lights.timerEnabled.get() || seconds < 120) {
            fire();
            return;
            } else if (seconds < 125) {
            timeRemainingA();
            return;
            } else if (seconds < 130) {
            timeRemainingB();
            return;
            } else if (seconds < 135) {
            timeRemainingC();
            return;
            } else {
            rainbow();
            return;
            }
        }
        // #endregion
        // #region Autonomous Logic
        if (robotStatus == RobotStatus.Autonomous) {
            // Brown Out
            if (batteryVoltage.getAsDouble() <= 11) {
            brownOut();
            return;
            }

            // Has Piece
            if (hasPiece.getAsBoolean()) {
            autoLoaded();
            return;
            }

            // Idle
            double seconds = RobotStatusTimer.get(); // Teleop length = 30 seconds
            if (!Lights.timerEnabled.get() || seconds < 15) {
            autoFire();
            return;
            } else if (seconds < 20) {
            timeRemainingA();
            return;
            } else if (seconds < 25) {
            timeRemainingB();
            return;
            } else if (seconds < 30) {
            timeRemainingC();
            return;
            } else {
            timeRemainingB();
            return;
            }
        }
        // #endregion
        autoFire();
        }

        // #region Mode Methods
        public static void clearAnimation() {
        if (lightMode == mode.paused) return;
        lightMode = mode.paused;

        LEDSegment.MainStrip.clearAnimation();
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void fullClear() {
        if (lightMode == mode.paused) return;
        lightMode = mode.paused;

        LEDSegment.MainStrip.fullClear();
        LEDSegment.MainStripLeft.fullClear();
        LEDSegment.MainStripRight.fullClear();
        }

        public static void strobe() {
        if (lightMode == mode.strobe) return;
        lightMode = mode.strobe;

        LEDSegment.MainStrip.setStrobeAnimation(white, 0.3);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void fade() {
        if (lightMode == mode.fade) return;
        lightMode = mode.fade;

        LEDSegment.MainStrip.setFadeAnimation(orange, 0.5);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void waiting() {
        if (lightMode == mode.waiting) return;
        lightMode = mode.waiting;

        LEDSegment.MainStrip.setFadeAnimation(white, 0.75);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void flow() {
        if (lightMode == mode.flow) return;
        lightMode = mode.flow;

        LEDSegment.MainStrip.setFlowAnimation(orange, 0.75);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void disabledLoaded() {
        if (lightMode == mode.disabledLoaded) return;
        lightMode = mode.disabledLoaded;

        LEDSegment.MainStrip.setFlowAnimation(green, 0.75);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void autoLoaded() {
        if (lightMode == mode.autoLoaded) return;
        lightMode = mode.autoLoaded;

        LEDSegment.MainStrip.setStrobeAnimation(orange, 0.3);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void fire() {
        if (lightMode == mode.fire) return;
        lightMode = mode.fire;

        LEDSegment.MainStrip.clearAnimation();
        LEDSegment.MainStripLeft.setFireAnimation(0.2);
        LEDSegment.MainStripRight.setFireAnimation(0.2);
        }

        public static void autoFire() {
        if (lightMode == mode.autoFire) return;
        lightMode = mode.autoFire;

        LEDSegment.MainStrip.clearAnimation();
        LEDSegment.MainStripLeft.setFireOverdriveAnimation(0.4);
        LEDSegment.MainStripLeft.setFireOverdriveAnimation(0.4);
        }

        public static void rainbow() {
        if (lightMode == mode.rainbow) return;
        lightMode = mode.rainbow;

        LEDSegment.MainStrip.setRainbowAnimation(0.8);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void intake() {
        if (lightMode == mode.intake) return;
        lightMode = mode.intake;

        LEDSegment.MainStrip.setStrobeAnimation(yellow, 0.25);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void brownOut() {
        if (lightMode == mode.brownOut) return;
        lightMode = mode.brownOut;

        LEDSegment.MainStrip.setFadeAnimation(brown, 0.8);
        LEDSegment.MainStripLeft.clearAnimation();
        LEDSegment.MainStripRight.clearAnimation();
        }

        public static void alignLeft(double distance) {

        if (distance < alignToleranceMin) {
            if (lightMode != mode.alignLeftNear) {
            LEDSegment.MainStrip.setColor(green);
            LEDSegment.MainStripLeft.clearAnimation();
            LEDSegment.MainStripRight.clearAnimation();
            lightMode = mode.alignLeftNear;
            }
        } else if (distance < alignToleranceMax) {
            if (lightMode != mode.alignLeft) {
            LEDSegment.MainStripRight.progressCount = 0;
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.setStrobeAnimation(blue, 0.25);
            LEDSegment.MainStripRight.setColor(red);
            lightMode = mode.alignLeft;
            }
            updateProgressBar(LEDSegment.MainStripRight, distance);
        } else {
            if (lightMode != mode.alignLeftFar) {
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.setStrobeAnimation(yellow, 0.15);
            LEDSegment.MainStripRight.setColor(red);
            lightMode = mode.alignLeftFar;
            }
        }
        }

        public static void alignRight(double distance) {
        if (distance < alignToleranceMin) {
            if (lightMode != mode.alignRightNear) {
            LEDSegment.MainStrip.setColor(green);
            LEDSegment.MainStripLeft.clearAnimation();
            LEDSegment.MainStripRight.clearAnimation();
            lightMode = mode.alignRightNear;
            }
        } else if (distance < alignToleranceMax) {
            if (lightMode != mode.alignRight) {
            LEDSegment.MainStripLeft.progressCount = 0;
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.setColor(red);
            LEDSegment.MainStripRight.setStrobeAnimation(blue, 0.3);
            lightMode = mode.alignRight;
            }
            updateProgressBar(LEDSegment.MainStripLeft, distance);
        } else {
            if (lightMode != mode.alignRightFar) {
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.setColor(red);
            LEDSegment.MainStripRight.setStrobeAnimation(yellow, 0.15);
            lightMode = mode.alignRightFar;
            }
        }
        }

        public static void alignCenter(double distance) {
        if (lightMode != mode.alignCenter) LEDSegment.MainStrip.progressCount = 0;

        // The idea here: blink according to the directon we want to drive
        // Functionality is not correctly implemented at this time
        // This might be harder, because we have to calculate a normal for the targetPosition

        if (distance < alignToleranceMin) {
            if (lightMode != mode.alignCenterNear) {
            LEDSegment.MainStrip.setColor(green);
            LEDSegment.MainStripLeft.clearAnimation();
            LEDSegment.MainStripRight.clearAnimation();
            lightMode = mode.alignCenterNear;
            }
        } else if (distance < alignToleranceMax) {
            if (lightMode != mode.alignCenter) {
            LEDSegment.MainStripLeft.progressCount = 0;
            LEDSegment.MainStripRight.progressCount = 0;
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.setColor(yellow);
            LEDSegment.MainStripRight.setColor(yellow);
            lightMode = mode.alignCenter;
            }
            updateProgressBar(LEDSegment.MainStripLeft, distance);
            updateProgressBar(LEDSegment.MainStripRight, distance);
        } else {
            if (lightMode != mode.alignCenterFar) {
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.setColor(red);
            LEDSegment.MainStripRight.setColor(red);
            lightMode = mode.alignCenterFar;
            }
        }
        }

        public static void timeRemainingA() {
        if (lightMode == mode.timeRemainingA) return;

        LEDSegment.MainStrip.setColor(green);
        LEDSegment.MainStripLeft.setFlowAnimation(green, 0.2);
        LEDSegment.MainStripRight.setFlowAnimation(green, 0.2);
        }

        public static void timeRemainingB() {
        if (lightMode == mode.timeRemainingB) return;

        LEDSegment.MainStrip.setColor(yellow);
        LEDSegment.MainStripLeft.setFlowAnimation(yellow, 0.35);
        LEDSegment.MainStripRight.setFlowAnimation(yellow, 0.35);
        }

        public static void timeRemainingC() {
        if (lightMode == mode.timeRemainingC) return;

        LEDSegment.MainStrip.setColor(red);
        LEDSegment.MainStripLeft.setStrobeAnimation(red, 0.5);
        LEDSegment.MainStripRight.setStrobeAnimation(red, 0.5);
        }

        public static void testProgress() {
        if (lightMode != mode.testProgress) {
            LEDSegment.MainStripLeft.progressCount = 0;
            LEDSegment.MainStripRight.progressCount = 0;
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.setColor(red);
            LEDSegment.MainStripRight.setColor(red);
            lightMode = mode.testProgress;
        }
        double distance = 150 * opControllerLeftX.getAsDouble();
        updateProgressBar(LEDSegment.MainStripLeft, distance);
        updateProgressBar(LEDSegment.MainStripRight, distance);
        }

        public static void matchProgress() {
        if (lightMode != mode.testProgress) {
            LEDSegment.MainStrip.clearAnimation();
            LEDSegment.MainStripLeft.clearAnimation();
            LEDSegment.MainStripRight.clearAnimation();
        }
        double seconds = RobotStatusTimer.get();
        double multiplier = 0;
        if (robotStatus == RobotStatus.Teleop) {
            multiplier = seconds / 135;
            LEDSegment.MainStrip.setColor(
                new Color(
                    (int) Math.round(green.red * (1 - multiplier) + red.red * multiplier),
                    (int) Math.round(green.green * (1 - multiplier) + red.green * multiplier),
                    (int) Math.round(green.blue * (1 - multiplier) + red.blue * multiplier)));
            return;
        }
        if (robotStatus == RobotStatus.Autonomous) {
            multiplier = seconds / 30;
            LEDSegment.MainStrip.setColor(
                new Color(
                    (int) Math.round(green.red * (1 - multiplier) + red.red * multiplier),
                    (int) Math.round(green.green * (1 - multiplier) + red.green * multiplier),
                    (int) Math.round(green.blue * (1 - multiplier) + red.blue * multiplier)));
            return;
        }
        LEDSegment.MainStrip.setColor(green);
        }

        static void updateProgressBar(LEDSegment segment, double distance) {
        // This needs to be tested again. I believe that one of the sides wasn't decreasing, but this may have been fixed after the robot was dismantled.
        int delta = findProgressDelta(segment, distance);
        if (delta == 0) return;
        if (!segment.reverseMode) {
            int currentLocation =
                segment.startIndex + segment.progressCount; // Current Location = the light after the
            // last active light
            if (delta > 0) {
                // Turn on forwards
                Color color = segment.progressOnColor;
                candle.setLEDs(color.red, color.green, color.blue, 0, currentLocation, delta);
            } else {
                // Turn off backward 
                Color color = segment.progressOffColor;
                candle.setLEDs(color.red, color.green, color.blue, 0, currentLocation - (-delta), (-delta));
            }
        } else {
            int currentLocation =
                segment.startIndex
                    + segment.segmentSize
                    - 1
                    - segment.progressCount; // Current Location = the light after the last active light
            if (delta > 0) {
                // Turn on backwards
                Color color = segment.progressOnColor;
                candle.setLEDs(color.red, color.green, color.blue, 0, currentLocation - delta + 1, delta);
            } else {
                // Turn off forwards
                Color color = segment.progressOffColor;
                candle.setLEDs(color.red, color.green, color.blue, 0, currentLocation + 1, (-delta));
            }
        }
    
        segment.progressCount += delta;    
    }

        static int findProgressDelta(LEDSegment segment, double value) {
        // Clamp within the distance range
        value = Math.max(alignToleranceMin, Math.min(alignToleranceMax, value)) - alignToleranceMin;
        // How many lights need to change
        int delta = (int) Math.ceil(value / segment.progressValueDistance) - segment.progressCount;
        return delta;
        }
        // #endregion
    }

    public static enum LEDSegment {
        BatteryIndicator(0, 2, 0, false),
        DriverstationIndicator(2, 2, 1, false),
        ExtraAIndicator(4, 1, -1, false),
        ExtraBIndicator(5, 1, -1, false),
        PivotEncoderIndicator(6, 1, -1, false),
        AllianceIndicator(7, 1, -1, false),
        BuckleIndicator(0,8,0, false),
        MainStrip(8, 127, 2, false),
        MainStripLeft(8, 53, 3, false, red, yellow),
        MainStripRight(61, 73, 4, true, red, yellow);

        public final int startIndex;
        public final int segmentSize;
        public final int animationSlot;
        public final boolean reverseMode;

        public int progressCount = 0;
        public final double progressValueDistance;
        public final Color progressOffColor;
        public final Color progressOnColor;

        private LEDSegment(int startIndex, int segmentSize, int animationSlot, boolean reverseMode) {
        this.startIndex = startIndex;
        this.segmentSize = segmentSize;
        this.animationSlot = animationSlot;
        this.reverseMode = reverseMode;
        this.progressValueDistance = 100 / segmentSize;
        this.progressOffColor = null;
        this.progressOnColor = null;
        }

        private LEDSegment(
            int startIndex,
            int segmentSize,
            int animationSlot,
            boolean reverseMode,
            Color progressOffColor,
            Color progressOnColor) {
        this.startIndex = startIndex;
        this.segmentSize = segmentSize;
        this.animationSlot = animationSlot;
        this.reverseMode = reverseMode;
        this.progressValueDistance = 100 / segmentSize;
        this.progressOffColor = progressOffColor;
        this.progressOnColor = progressOnColor;
        }

        public void setColor(Color color) {
        if (candle != null) {
            clearAnimation();
            candle.setLEDs(color.red, color.green, color.blue, 0, startIndex, segmentSize);
        }
        }

        private void setAnimation(Animation animation) {
        if (candle != null) {
            candle.animate(animation, animationSlot);
        }
        }

        public void fullClear() {
        if (candle != null) {
            clearAnimation();
            disableLEDs();
        }
        }

        public void clearAnimation() {
        if (candle != null) {
            candle.clearAnimation(animationSlot);
        }
        }

        public void disableLEDs() {
        if (candle != null) {
            setColor(black);
        }
        }

        public void setFlowAnimation(Color color, double speed) {
        setAnimation(
            new ColorFlowAnimation(
                color.red,
                color.green,
                color.blue,
                0,
                speed,
                segmentSize,
                (!reverseMode)
                    ? Direction.Forward
                    : Direction.Backward, // transmute reverseMode to Direction value
                startIndex));
        }

        public void setFadeAnimation(Color color, double speed) {
        setAnimation(
            new SingleFadeAnimation(
                color.red, color.green, color.blue, 0, speed, segmentSize, startIndex));
        }

        public void setBandAnimation(Color color, int size, double speed, BounceMode bounceMode) {
        setAnimation(
            new LarsonAnimation(
                color.red,
                color.green,
                color.blue,
                0,
                speed,
                segmentSize,
                bounceMode,
                size,
                startIndex));
        }

        public void setBandAnimation(Color color, int size, double speed) {
        setBandAnimation(color, size, speed, BounceMode.Front);
        }

        public void setStrobeAnimation(Color color, double speed) {
        setAnimation(
            new StrobeAnimation(
                color.red, color.green, color.blue, 0, speed, segmentSize, startIndex));
        }

        public void setRainbowAnimation(double speed) {
        setAnimation(new RainbowAnimation(1, speed, segmentSize, reverseMode, startIndex));
        }

        public void setFireAnimation(double speed) {
        setAnimation(new FireAnimation(1, speed, segmentSize, 0.5, 0.3, reverseMode, startIndex));
        }

        public void setFireOverdriveAnimation(double speed) {
        setAnimation(new FireAnimation(1, speed, segmentSize, 0.75, 0.6, reverseMode, startIndex));
        }
    }

    public static class Color {
        public int red;
        public int green;
        public int blue;

        public Color(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        /**
         * Highly imperfect way of dimming the LEDs. It does not maintain color or accurately adjust
         * perceived brightness.
         *
         * @param dimFactor
         * @return The dimmed color
         */
        public Color dim(double dimFactor) {
            int newRed = (int) (ensureRange(red * dimFactor, 0, 200));
            int newGreen = (int) (ensureRange(green * dimFactor, 0, 200));
            int newBlue = (int) (ensureRange(blue * dimFactor, 0, 200));

            return new Color(newRed, newGreen, newBlue);
        }
    }

    private static double ensureRange(double value, double low, double upper) {
        return Math.max(low, Math.min(upper, value));
    }

    public static void disableLEDs() {
        setBrightness(0);
    }

    public static void enableLEDs() {
        setBrightness(.5);
    }
}
