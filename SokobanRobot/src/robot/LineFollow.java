package robot;

import java.util.Date;

//import javax.microedition.lcdui.Display;

// Two Guys one Cake
// SOS Dessert Alert
// Cake at Baker street 221b

// Rask's best cake bakers

//Bakers hate that trick: Selfmade cake!

//Hot cake for hot chicks, cum and enjoy!

// Wolle Kuchen kaufen?
// Wolle Rosen kaufen? do you 

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.RegulatedMotor;

public class LineFollow {

	// Sensors
	static final LightSensor lightRight = new LightSensor(SensorPort.S1);
	static final LightSensor lightLeft = new LightSensor(SensorPort.S2);
	static final LightSensor lightFront = new LightSensor(SensorPort.S3);
	static final RegulatedMotor rightMotor = Motor.A;
	static final RegulatedMotor leftMotor = Motor.B;
	
	// LightSensor Min/Max Values
	static int lightValueMin = 100;
	static int lightValueMax = 0;
	static int desiredValue = -1;
	static int lightValueMinFront = 100;
	static int lightValueMaxFront = 0;
	
	// Constants								// Big Robot Values
	static final int DEFAULT_SPEED = 400;		// 200
	static final int TURN_ANGLE_90 = 160;		// 180, 150
	static final int TURN_ANGLE_180 = 270;		// 330
	static final int LIGHT_FRONT_BLACK = 85;	// 85
	static final double DIFFERENCE_SCALAR = 0.8;// 0.8
	
	public static void main(String[] args) {
		// initialize
		rightMotor.setAcceleration(5980);
		rightMotor.setAcceleration(5980);
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.setSpeed(DEFAULT_SPEED);
		initializeLightSensors();
		//startCalibration();
		
//		LCD.drawString("Min Left:  " + lightLeft.getLow(), 0, 0);
//		LCD.drawString("Max Left:  " + lightLeft.getHigh(), 0, 1);
//		LCD.drawString("Min Right: " + lightRight.getLow(), 0, 2);
//		LCD.drawString("Max Right: " + lightRight.getHigh(), 0, 3);
//		LCD.drawString("Min Front: " + lightFront.getLow(), 0, 4);
//		LCD.drawString("Max Front: " + lightFront.getHigh(), 0, 5);
//		
//		// Turn until black line is hit, so that robot is oriented correctly
//		rightMotor.backward();
//		leftMotor.forward();
//		
//		while(lightFront.getLightValue() > 60) { // Hardcoded Margin Zone
//			//LCD.drawString("Val Front: " + lightFront.getLightValue() + "  ", 0, 6);
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		leftMotor.stop(true);
//		rightMotor.stop();
//		//LCD.drawString("stop my code", 0, 7);
		
		
		
		LCD.drawString("Moved forward 1 field!", 0, 7);
		
		while(true) {
			moveForward(2, false);
			turnLeft();
			moveForward(1, false);
			turnRight();
			moveForward(1, false);
			turnRight();
			moveForward(1, false);
			turnRight();
			moveForward(3, false);
		}
	}
	
	private static void initializeLightSensors() {
		// Turn around, right direction
		// initialize Light Sensors while turning
		lightRight.setHigh(0);
		lightRight.setLow(1023);
		lightLeft.setHigh(0);
		lightLeft.setLow(1023);
		lightFront.setHigh(0);
		lightFront.setLow(1023);
	}
	
	private static void calibrateLightSensors() {
		int tmpVal = lightRight.getNormalizedLightValue();
		if(tmpVal > lightRight.getHigh()) lightRight.setHigh(tmpVal);
		if(tmpVal < lightRight.getLow()) lightRight.setLow(tmpVal);
		
		tmpVal = lightLeft.getNormalizedLightValue();
		if(tmpVal > lightLeft.getHigh()) lightLeft.setHigh(tmpVal);
		if(tmpVal < lightLeft.getLow()) lightLeft.setLow(tmpVal);
		
		tmpVal = lightFront.getNormalizedLightValue();
		if(tmpVal > lightFront.getHigh()) lightFront.setHigh(tmpVal);
		if(tmpVal < lightFront.getLow()) lightFront.setLow(tmpVal);
	}
	
	private static void startCalibration() {
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.rotate(-580, true);
		leftMotor.rotate(580, true);
		while(rightMotor.isMoving() || leftMotor.isMoving()) {
			calibrateLightSensors();
		}
	}
	
	private static void moveForward(int numOfFields, boolean hasCan) {
		calibrateLightSensors();
		for(int i = 0; i < numOfFields; i++) {
			long currentTime = System.currentTimeMillis();
			rightMotor.backward();
			leftMotor.backward();
			
			while(lightFront.getLightValue() < LIGHT_FRONT_BLACK)
			{
				calibrateLightSensors();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
			}
			
		
			//Sound.beep();
			
			
			while(lightFront.getLightValue() > 60 ||  (System.currentTimeMillis() - currentTime)< 1000) { // Hardcoded Margin Zone
				calibrateLightSensors();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
			}
		}
		rightMotor.stop(true);
		leftMotor.stop();
	}
	
	private static void turnLeft() {
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.rotate(TURN_ANGLE_90, true);
		rightMotor.rotate(-TURN_ANGLE_90);
	}
	
	private static void turnRight() {
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.rotate(TURN_ANGLE_90, true);
		leftMotor.rotate(-TURN_ANGLE_90);
	}
	
	private static void turnAround() {
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.rotate(TURN_ANGLE_180, true);
		rightMotor.rotate(-TURN_ANGLE_180);
	}
	// negative difference should make robot turn left
	// positive difference should turn robot to the right
	private static void controlSpeed(int difference) {		
		difference *= DIFFERENCE_SCALAR;
		
		leftMotor.setSpeed(DEFAULT_SPEED + difference);
		rightMotor.setSpeed(DEFAULT_SPEED - difference);
	}
	
}


