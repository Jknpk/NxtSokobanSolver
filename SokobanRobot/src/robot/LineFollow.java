package robot;

//import javax.microedition.lcdui.Display;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.RegulatedMotor;
import lejos.util.PIDController;

public class LineFollow {

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
	
	static PIDController pid = new PIDController(0, 10);
	
	// Constants
	static int speed = 200;
	static final int blackPuffer = 4;
	
	public static void main(String[] args) {
		// initialize
		
		rightMotor.setSpeed(speed);
		leftMotor.setSpeed(speed);
		initializeLightSensors();
		startCalibration();
		
		LCD.drawString("Min Left:  " + lightLeft.getLow(), 0, 0);
		LCD.drawString("Max Left:  " + lightLeft.getHigh(), 0, 1);
		LCD.drawString("Min Right: " + lightRight.getLow(), 0, 2);
		LCD.drawString("Max Right: " + lightRight.getHigh(), 0, 3);
		LCD.drawString("Min Front: " + lightFront.getLow(), 0, 4);
		LCD.drawString("Max Front: " + lightFront.getHigh(), 0, 5);
		
		// Turn until black line is hit, so that robot is oriented correctly
		rightMotor.backward();
		leftMotor.forward();
		
		while(lightFront.getLightValue() > 60) { // Hardcoded Margin Zone
			//LCD.drawString("Val Front: " + lightFront.getLightValue() + "  ", 0, 6);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		leftMotor.stop(true);
		rightMotor.stop();
		//LCD.drawString("stop my code", 0, 7);
		
		moveForward(1, false);
		LCD.drawString("Moved forward 1 field!", 0, 7);
		
		while(true) {
			
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
		rightMotor.setSpeed(speed);
		leftMotor.setSpeed(speed);
		rightMotor.rotate(-580, true);
		leftMotor.rotate(580, true);
		while(rightMotor.isMoving() || leftMotor.isMoving()) {
			calibrateLightSensors();
		}
	}
	
	private static void moveForward(int numOfFields, boolean hasCan) {
		// Start motors
		// find line
		// follow line
		// stop motors when line is hit
		
		for(int i = 0; i < numOfFields; i++) {
			
			// get a bit forward to pass current line
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			
//			rightMotor.rotate(-150, true);
//			leftMotor.rotate(-150);
			
			rightMotor.backward();
			leftMotor.backward();
			
			while(lightFront.getLightValue() < 80)
			{
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
			}
			
			while(lightFront.getLightValue() > 60) { // Hardcoded Margin Zone
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
			}
		}
		rightMotor.stop();
		leftMotor.stop();
	}
	
	// negative difference should make robot turn left
	// positive difference should turn robot to the right
	private static void controlSpeed(int difference) {
		
		int pidVal = pid.doPID(difference);
		
		//difference *= 2;
		
		if(pidVal > speed) 
			pidVal = speed;
		else if (pidVal < -speed)
			pidVal = -speed;
		
		leftMotor.setSpeed(speed + pidVal);
		rightMotor.setSpeed(speed - pidVal);
	}
}
