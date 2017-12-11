package robot;

import java.io.File;
import java.io.FileInputStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.RegulatedMotor;
import lejos.util.Delay;

enum Orientation{Top, Right, Down, Left };
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
	
	// Constants								// GREAT RESULTS!!!			// Big Robot Values
	static final int TURN_SPEED = 200;
	static final int DEFAULT_SPEED = 400;		// 400						// 200
	static final int TURN_ANGLE_90 = 126;		// 157 160						// 180, 150
	static final int TURN_ANGLE_180 = 250;		// 315						// 330
	static final int LIGHT_FRONT_BLACK = 80;	// 85						// 85
	static final double DIFFERENCE_SCALAR = 0.9;// 0.8						// 0.8
	static final int WAIT_MILLISECONDS = 2;
	static final int LIGHT_EQUAL = 2;
	
	public static void main(String[] args) {
		// initialize
		
				
		rightMotor.setAcceleration(4000);	//5980
		rightMotor.setAcceleration(4000);	//5980
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.setSpeed(DEFAULT_SPEED);
		initializeLightSensors();
		
		
		String inputFile = "solution.txt";
		//String inputFile = "test.txt";
		String route = readRoute(inputFile);
		Orientation robotOrientation = Orientation.Top;
		Sound.beep();
		Sound.beep();
		
		LCD.drawString(route, 1, 1, false);
		Button.waitForAnyPress();
		
		int i = 0;
		int j = 0;
		char currentChar = ' ';
		char nextChar = ' ';
/*
		while(true)
		{
			
			Delay.msDelay(100);
			calibrateLightSensors();
			LCD.drawString(lightFront.getLightValue() + "          ", 2, 1, false);
			if(i == -1) { break;}
		}
	*/	
		
		while (true) {
			
			try {
				currentChar = route.charAt(i);
			} catch (Exception e) {
				break;
			}
			j = 0;
			while (true) {
				j++;
				try {
					nextChar = route.charAt(i + j);
				} catch (Exception e) {
					j--;
					break;
				}
				if (nextChar != currentChar) {
					j--;
					break;
				}
			}

			//System.out.println(j + 1 + " times " + currentChar);
			
			boolean hasCan = false;
			if(currentChar > 64 && currentChar < 91) {
				// Upper case
				hasCan = true;
			}
			
			String lowChar = "" + currentChar;
			lowChar = lowChar.toLowerCase();
			char currentLowChar = lowChar.charAt(0);
			
			robotOrientation = executeTurn(currentLowChar, robotOrientation, j + 1 , hasCan);
			if (robotOrientation == null) {
				// Shouldn't be the case
				System.exit(0);
			}
			
			
			i = i + j + 1;
		}
		
		Sound.beep();
		
		
		
		
		
		/*for(char c : route.toCharArray()) {
			// execute turn			
			
			
			
			

		}
		*/
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
		
		//LCD.drawString("Moved forward 1 field!", 0, 7);
		
		/*while(true) {
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
		*/
		
		
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
		/*Thread t = null;
		if(hasCan) {
			t = new Thread(new Mp3Player());
			t.start();	
		}
		*/
		calibrateLightSensors();
		for(int i = 0; i < numOfFields; i++) {
			long currentTime = System.currentTimeMillis();
			rightMotor.backward();
			leftMotor.backward();
			
			while(lightFront.getLightValue() < LIGHT_FRONT_BLACK)
			{
				calibrateLightSensors();
				try {
					Thread.sleep(WAIT_MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
			}
		
			
			while(lightFront.getLightValue() > LIGHT_FRONT_BLACK ||  (System.currentTimeMillis() - currentTime)< 1000) { // Hardcoded Margin Zone
				calibrateLightSensors();
				try {
					Thread.sleep(WAIT_MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
			}
		}
		
		if(hasCan) {
			// Push can forward on final position
			rightMotor.rotate(-240, true);
			leftMotor.rotate(-240, true);
			while(rightMotor.isMoving() || leftMotor.isMoving()) {
				Delay.msDelay(5);
			}
			moveBackward();
			//t.interrupt();
		}
		
		
		rightMotor.stop(true);
		leftMotor.stop();
	}
	
	private static void turnLeft() {
		//Button.waitForAnyPress();
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.rotate(TURN_ANGLE_90, true);
		rightMotor.rotate(-TURN_ANGLE_90);
		//Button.waitForAnyPress();
		
		leftMotor.setSpeed(TURN_SPEED);
		rightMotor.setSpeed(TURN_SPEED);
		
		rightMotor.backward();
		leftMotor.forward();
		
		while(lightFront.getLightValue() > LIGHT_FRONT_BLACK ) { // Hardcoded Margin Zone
		//while(lightLeft.getLightValue() > LIGHT_FRONT_BLACK ) { // Hardcoded Margin Zone
			try {
				Thread.sleep(WAIT_MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
		}
		
//		while(abs(lightLeft.getLightValue() - lightRight.getLightValue()) > LIGHT_EQUAL ) { // Hardcoded Margin Zone
//			try {
//				Thread.sleep(WAIT_MILLISECONDS);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
//		}
		
		rightMotor.stop(true);
		leftMotor.stop();
		
		rightMotor.rotate(6, true);
		leftMotor.rotate(-6);
	}
	
	private static int abs(int i) {
		if(i < 0)
			i = i*-1;
		return i;
	}

	private static void turnRight() {
		//Button.waitForAnyPress();
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.rotate(TURN_ANGLE_90, true);
		leftMotor.rotate(-TURN_ANGLE_90);
		//Button.waitForAnyPress();
		
		leftMotor.setSpeed(TURN_SPEED);
		rightMotor.setSpeed(TURN_SPEED);
		
		rightMotor.forward();
		leftMotor.backward();
		
		while(lightFront.getLightValue() > LIGHT_FRONT_BLACK ) { // Hardcoded Margin Zone
		//while(lightRight.getLightValue() > LIGHT_FRONT_BLACK ) { // Hardcoded Margin Zone
			
			try {
				Thread.sleep(WAIT_MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
		}
		
//		while(abs(lightLeft.getLightValue() - lightRight.getLightValue()) > LIGHT_EQUAL ) { // Hardcoded Margin Zone
//			try {
//				Thread.sleep(WAIT_MILLISECONDS);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
//		}
		
		rightMotor.stop(true);
		leftMotor.stop();
		
		rightMotor.rotate(-6, true);
		leftMotor.rotate(6);
	}
	
	private static void turnAround() {
		//Button.waitForAnyPress();
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.setSpeed(DEFAULT_SPEED);
		leftMotor.rotate(TURN_ANGLE_180, true);
		rightMotor.rotate(-TURN_ANGLE_180);
		//Button.waitForAnyPress();
		
		leftMotor.setSpeed(TURN_SPEED);
		rightMotor.setSpeed(TURN_SPEED);
		
		rightMotor.backward();
		leftMotor.forward();
		
		while(lightFront.getLightValue() > LIGHT_FRONT_BLACK ) { // Hardcoded Margin Zone
		//while(lightLeft.getLightValue() > LIGHT_FRONT_BLACK ) { // Hardcoded Margin Zone
			try {
				Thread.sleep(WAIT_MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
		}
		
//		while(abs(lightLeft.getLightValue() - lightRight.getLightValue()) > LIGHT_EQUAL ) { // Hardcoded Margin Zone
//			try {
//				Thread.sleep(WAIT_MILLISECONDS);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//controlSpeed(lightLeft.getLightValue()-lightRight.getLightValue());
//		}
		
		rightMotor.stop(true);
		leftMotor.stop();
		
		rightMotor.rotate(6, true);
		leftMotor.rotate(-6);
		
	}
	
	
	
	
	private static void moveBackward() {
		
		long currentTime = System.currentTimeMillis();
		leftMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.setSpeed(DEFAULT_SPEED);
		rightMotor.forward();
		leftMotor.forward();
		
		while(lightFront.getLightValue() > LIGHT_FRONT_BLACK) { // Hardcoded Margin Zone
		
			try {
				Thread.sleep(WAIT_MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//controlSpeed(lightRight.getLightValue()-lightLeft.getLightValue());
		}
		
		rightMotor.stop(true);
		leftMotor.stop();
		
		rightMotor.rotate(-40, true);
		leftMotor.rotate(-40, false);
		while(rightMotor.isMoving() || leftMotor.isMoving()) {
			Delay.msDelay(5);
		}
		
	}
	
	
	
	
	
	// negative difference should make robot turn left
	// positive difference should turn robot to the right
	private static void controlSpeed(int difference) {		
		difference *= DIFFERENCE_SCALAR;
		
		leftMotor.setSpeed(DEFAULT_SPEED + difference);
		rightMotor.setSpeed(DEFAULT_SPEED - difference);
	}
	
	
	
	private static String readRoute(String inputFile) {
		String returnString = "";

	    StringBuffer fileContent = new StringBuffer("");
	    FileInputStream fis;
	    try {

	        fis = new FileInputStream(new File(inputFile));

	        byte[] buffer = new byte[1024];
	        int n;
	        while ((n = fis.read(buffer)) != -1) 
	        { 
	          fileContent.append(new String(buffer, 0, n)); 
	        }
	    }catch(Exception e) {
	    	LCD.drawString("ERROR", 5, 5, false);
	    }
	    returnString = fileContent.toString();
		return returnString;
	}
	
	
	
	
	private static Orientation executeTurn(char c, Orientation currentOrientation, int numOfFields , boolean hasCan) {
		if(c == 'u') {
			switch(currentOrientation) {
				case Right:
					// Turn 90 degrees left
					turnLeft();
					break;
				case Down:
					// Turn 180 degrees
					turnAround();
					break;
				case Left:
					// Turn 90 degrees right
					turnRight();
					break;
				default: 
					break;
			}
			
			// move forward
			moveForward(numOfFields, hasCan);
			return Orientation.Top;
		}
		else if(c == 'r') {
			switch(currentOrientation) {
			case Top:
				// Turn 90 degrees right
				turnRight();
				break;
			case Down:
				// Turn 90 degrees left
				turnLeft();
				break;
			case Left:
				// Turn 180 degrees
				turnAround();
				break;
			default: 
				break;
		}		
		// move forward
		moveForward(numOfFields, hasCan);
		return Orientation.Right;
		}
		else if(c == 'd') {
			switch(currentOrientation) {
			case Top:
				// Turn 180 degrees 
				turnAround();
				break;
			case Right:
				// Turn 90 degrees right
				turnRight();
				break;
			case Left:
				// Turn 90 degrees left
				turnLeft();
				break;
			default: 
				break;
		}		
		// move forward
		moveForward(numOfFields, hasCan);
		return Orientation.Down;
		}
		
		else if(c == 'l') {
			switch(currentOrientation) {
			case Top:
				// Turn 90 degrees left
				turnLeft();
				break;
			case Right:
				// Turn 180 degrees
				turnAround();
				break;
			case Down:
				// Turn 90 degrees right
				turnRight();
				break;
			default: 
				break;
		}		
		// move forward
		moveForward(numOfFields, hasCan);
		return Orientation.Left;
		}
		else {
			//System.exit(3); // unsupported character
			return null;
		}
	}
}