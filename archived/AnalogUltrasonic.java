import edu.wpi.first.wpilibj.AnalogInput;

/**
 * A class that represents an ultrasonic sensor. 
 * The data is read by reading the analog voltage output of the sensor, through an {@code AnalogInput}.<br>
 * <br>
 * The voltage-distance calculations are based on the formula for a <em>MaxBotix HRLV-MaxSonar-EZ,</em><br>
 * {@code D=V/(Vin/5120)} where D is the distance in millimeters, V is the voltage reading from the sensor
 * and Vin is the supply voltage to the sensor.
 * @author Tyler
 *
 */
public class AnalogUltrasonic {
	
	final AnalogInput sensor;
	public final double voltagePerMillimeter;
	
	/**
	 * Instantiates a new instance on a specified port, with the default supply voltage of 5.0V.
	 * @param port - The Analog Input port number the sensor is connected to
	 */
	public AnalogUltrasonic(int port) {
		this(port, 5.0);
	}
	
	/**
	 * Instantiates a new instance on a specified port with a specified supply voltage.
	 * @param port - The Analog Input port number the sensor is connected to
	 * @param supplyVoltage - The voltage supplied to the sensor
	 */
	public AnalogUltrasonic(int port, double supplyVoltage) {
		sensor = new AnalogInput(port);
		voltagePerMillimeter = supplyVoltage / (1024 * 5);
	}
	
	/**
	 * Retrieves the distance reading from the sensor, in <b>millimeters</b>.
	 * @return The distance reading of the sensor in millimeters
	 */
	public double getDistance() {
		return sensor.getAverageVoltage() / voltagePerMillimeter;
	}
	
	/**
	 * Retrieves the distance reading from the sensor, in <b>meters</b>.
	 * @return The distance reading of the sensor in meters
	 */
	public double getDistanceMeters() {
		return getDistance() / 1000;
	}
	
	/**
	 * Retrieves the distance reading from the sensor, in <b>inches</b>.
	 * @return The distance reading of the sensor in inches
	 */
	public double getDistanceInches() {
		return getDistance() / 25.4;
	}
	
	/**
	 * Retrieves the internal {@code AnalogInput}.
	 * @return The internal AnalogInput used to read the voltage
	 */
	public AnalogInput getAnalogInput() {
		return sensor;
	}
	
	/**
	 * Retrieves the ratio of volts in output to millimeters in distance.
	 * @return The voltage per millimeter of the sensor
	 */
	public double getVoltagePerMillimeter() {
		return voltagePerMillimeter;
	}
}
