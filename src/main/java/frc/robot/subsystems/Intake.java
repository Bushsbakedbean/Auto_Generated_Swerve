// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intake extends SubsystemBase {

  CANSparkMax LeftIntakeMotor;
  CANSparkMax RightIntakeMotor;
  CANSparkMax FrontIntakeMotor;
  TalonFX RearFlyMotor;
  TalonFX FrontFlyMotor;

  public double LeftIntakeVoltage;
  public double RightIntakeVoltage;
  public double FrontIntakeVoltage;
  public double RearFlyVoltage;
  public double FrontFlyVoltage;

  /** Creates a new Intake. */
  public Intake() {

    LeftIntakeMotor = new CANSparkMax(Constants.CAN_IDs.LeftIntakeID, MotorType.kBrushless);
    RightIntakeMotor = new CANSparkMax(Constants.CAN_IDs.RightIntakeID, MotorType.kBrushless);
    FrontIntakeMotor = new CANSparkMax(Constants.CAN_IDs.FrontIntakeID, MotorType.kBrushless);
    RearFlyMotor = new TalonFX(Constants.CAN_IDs.RearFlyID);
    FrontFlyMotor = new TalonFX(Constants.CAN_IDs.FrontFlyID);

  }

  @Override
  public void periodic() {

    LeftIntakeVoltage = LeftIntakeMotor.getBusVoltage();
    RightIntakeVoltage = RightIntakeMotor.getBusVoltage();
    FrontIntakeVoltage = FrontIntakeMotor.getBusVoltage();
    FrontFlyVoltage = FrontFlyMotor.getSupplyVoltage().getValueAsDouble();
    RearFlyVoltage = RearFlyMotor.getSupplyVoltage().getValueAsDouble();

    SmartDashboard.putNumber("Left Intake Voltage", LeftIntakeVoltage);
    SmartDashboard.putNumber("Right Intake Voltage", RightIntakeVoltage);
    SmartDashboard.putNumber("Front Intake Voltage", FrontIntakeVoltage);
    SmartDashboard.putNumber("Front Fly Voltage", FrontFlyVoltage);
    SmartDashboard.putNumber("Back Fly Voltage", RearFlyVoltage);
    
  }

  public void RunIntake(double speed) {
    
    //LeftIntakeMotor.set(speed);
    //RightIntakeMotor.set(speed);
    //FrontIntakeMotor.set(speed);
    FrontFlyMotor.set(speed);
    RearFlyMotor.set(speed);

  }

  public Boolean AreIntakeMotorsGood() {

    if (
    LeftIntakeVoltage < 9 ||
    RightIntakeVoltage < 9 ||
    FrontIntakeVoltage < 9 ||
    FrontFlyVoltage < 9 ||
    RearFlyVoltage < 9 ) {

      return false;

    } else {

      return true;

    }

  }

}
