package frc.robot;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain.SwerveDriveState;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.DoubleArrayPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

public class Telemetry {
 
    private final double MaxSpeed;

    /**
     * Construct a telemetry object, with the specified max speed of the robot
     * 
     * @param maxSpeed Maximum speed in meters per second
     */
    public Telemetry(double maxSpeed) {
        MaxSpeed = maxSpeed;
    }

    /* What to publish over networktables for telemetry */
    NetworkTableInstance inst = NetworkTableInstance.getDefault();

    /* Robot pose for field positioning */
    NetworkTable table = inst.getTable("Pose");
    DoubleArrayPublisher fieldPub = table.getDoubleArrayTopic("robotPose").publish();
    StringPublisher fieldTypePub = table.getStringTopic(".type").publish();

    /* Robot speeds for general checking */
    NetworkTable driveStats = inst.getTable("Drive");
    DoublePublisher velocityX = driveStats.getDoubleTopic("Velocity X").publish();
    DoublePublisher velocityY = driveStats.getDoubleTopic("Velocity Y").publish();
    DoublePublisher speed = driveStats.getDoubleTopic("Speed").publish();
    DoublePublisher odomPeriod = driveStats.getDoubleTopic("Odometry Period").publish();
    
    /* Keep a reference of the last pose to calculate the speeds */
    Pose2d m_lastPose = new Pose2d();
    double lastTime = Utils.getCurrentTimeSeconds();

    /* Mechanisms to represent the swerve module states */
    Mechanism2d[] m_moduleMechanisms = new Mechanism2d[] {
            new Mechanism2d(1, 1),
            new Mechanism2d(1, 1),
            new Mechanism2d(1, 1),
            new Mechanism2d(1, 1),
    };
    /* A direction and length changing ligament for speed representation */
    MechanismLigament2d[] m_moduleSpeeds = new MechanismLigament2d[] {
            m_moduleMechanisms[0].getRoot("RootSpeed", 0.5, 0.5).append(new MechanismLigament2d("Speed", 0.5, 0)),
            m_moduleMechanisms[1].getRoot("RootSpeed", 0.5, 0.5).append(new MechanismLigament2d("Speed", 0.5, 0)),
            m_moduleMechanisms[2].getRoot("RootSpeed", 0.5, 0.5).append(new MechanismLigament2d("Speed", 0.5, 0)),
            m_moduleMechanisms[3].getRoot("RootSpeed", 0.5, 0.5).append(new MechanismLigament2d("Speed", 0.5, 0)),
    };
    /* A direction changing and length constant ligament for module direction */
    MechanismLigament2d[] m_moduleDirections = new MechanismLigament2d[] {
            m_moduleMechanisms[0].getRoot("RootDirection", 0.5, 0.5)
                    .append(new MechanismLigament2d("Direction", 0.1, 0, 0, new Color8Bit(Color.kWhite))),
            m_moduleMechanisms[1].getRoot("RootDirection", 0.5, 0.5)
                    .append(new MechanismLigament2d("Direction", 0.1, 0, 0, new Color8Bit(Color.kWhite))),
            m_moduleMechanisms[2].getRoot("RootDirection", 0.5, 0.5)
                    .append(new MechanismLigament2d("Direction", 0.1, 0, 0, new Color8Bit(Color.kWhite))),
            m_moduleMechanisms[3].getRoot("RootDirection", 0.5, 0.5)
                    .append(new MechanismLigament2d("Direction", 0.1, 0, 0, new Color8Bit(Color.kWhite))),
    };

    /* Accept the swerve drive state and telemeterize it to smartdashboard */
    public void telemeterize(SwerveDriveState state) {
        /* Telemeterize the pose */
        Pose2d pose = state.Pose;
        fieldTypePub.set("Field2d");
        fieldPub.set(new double[] {
                pose.getX(),
                pose.getY(),
                pose.getRotation().getDegrees()
        });

        /* Telemeterize the robot's general speeds */
        double currentTime = Utils.getCurrentTimeSeconds();
        double diffTime = currentTime - lastTime;
        lastTime = currentTime;
        Translation2d distanceDiff = pose.minus(m_lastPose).getTranslation();
        m_lastPose = pose;

        Translation2d velocities = distanceDiff.div(diffTime);

        speed.set(velocities.getNorm());
        velocityX.set(velocities.getX());
        velocityY.set(velocities.getY());
        odomPeriod.set(state.OdometryPeriod);

        /* Telemeterize the module's states */
        for (int i = 0; i < 4; ++i) {
            m_moduleSpeeds[i].setAngle(state.ModuleStates[i].angle);
            m_moduleDirections[i].setAngle(state.ModuleStates[i].angle);
            m_moduleSpeeds[i].setLength(state.ModuleStates[i].speedMetersPerSecond / (2 * MaxSpeed));

            SmartDashboard.putData("Module " + i, m_moduleMechanisms[i]);
        }

    }

    public Pose2d returnPose() {

        return m_lastPose;

    }
 
    BooleanSupplier CheckIfFinished(double X, double Y, double Angle) {

        return () -> Math.abs(m_lastPose.getX() - X) <= Constants.POSETOLERANCE
        && Math.abs(m_lastPose.getY() - Y) <= Constants.POSETOLERANCE
        && Math.abs(returnPose().getRotation().getDegrees() - Angle) <= Constants.ANGLETOLERANCE;
  
    }

    BooleanSupplier CheckIfFinished(double X, double Y, double Angle, double Tolerance) {

        return () -> Math.abs(m_lastPose.getX() - X) <= Tolerance
        && Math.abs(m_lastPose.getY() - Y) <= Tolerance
        && Math.abs(returnPose().getRotation().getDegrees() - Angle) <= Constants.ANGLETOLERANCE;
  
    }

    BooleanSupplier CheckIfFinished(double X, double Y) {

        return () -> Math.abs(m_lastPose.getX() - X) <= Constants.POSETOLERANCE
        && Math.abs(m_lastPose.getY() - Y) <= Constants.POSETOLERANCE;
  
     }
 
}
