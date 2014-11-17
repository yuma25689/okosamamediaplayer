package okosama.app;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MotionObserver implements SensorEventListener {

	// 15�x�ȏ�ς�邲�ƂɁA�ʒm������̂Ƃ���
	final int SENSOR_CHANGE_NOTIFY_VALUE = 15;
	
	public class MagneticFieldValue
	{
		/**
		 * @return the azimuth
		 */
		public double getAzimuth() {
			return azimuth;
		}

		/**
		 * @param azimuth the azimuth to set
		 */
		public void setAzimuth(double azimuth) {
			this.azimuth = azimuth;
		}

		/**
		 * @return the pitch
		 */
		public double getPitch() {
			return pitch;
		}

		/**
		 * @param pitch the pitch to set
		 */
		public void setPitch(double pitch) {
			this.pitch = pitch;
		}

		/**
		 * @return the roll
		 */
		public double getRoll() {
			return roll;
		}

		/**
		 * @param roll the roll to set
		 */
		public void setRoll(double roll) {
			this.roll = roll;
		}

		double azimuth = 0;
		double pitch = 0;	// �c��]
		double roll = 0;	// ����]
		
		MagneticFieldValue()
		{
		}
		MagneticFieldValue(double a, double p, double r )
		{
			azimuth = a;
			pitch = p;
			roll = r;
		}
		public void setMagnetic( double a, double p, double r )
		{
			azimuth = a;
			pitch = p;
			roll = r;
		}
	}
	
	// �Ō�ɒʒm�����p�x�B
	MagneticFieldValue lastNotifyMagnetic = null;//new MagneticFieldValue();
	// ���݂̊p�x
	MagneticFieldValue nowMagnetic = new MagneticFieldValue();
	
	/**
	 * @return the nowMagnetic
	 */
	public MagneticFieldValue getNowMagnetic() {
		return nowMagnetic;
	}

	protected final static double RAD2DEG = 180/Math.PI;
	
	Activity mActivity;
	SensorManager sensorManager;
	
	float[] rotationMatrix = new float[9];
	float[] gravity = new float[3];
	float[] geomagnetic = new float[3];
	float[] attitude = new float[3];

	/**
	 * ���[�V�����Z���T�[�̏�����
	 */
	public void init(Activity act)
	{
		mActivity = act;
		sensorManager = (SensorManager)mActivity.getSystemService(Context.SENSOR_SERVICE);
		
		sensorManager.registerListener(
			this,
			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
			SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(
			this,
			sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
			SensorManager.SENSOR_DELAY_UI);		
	}
	
	/**
	 * ���[�V�����Z���T�[�̉��
	 */
	public void release()
	{
		if( sensorManager != null )
		{
			sensorManager.unregisterListener(this);
		}
	}

	/**
	 * �Z���T�[�̐��x���ύX���ꂽ�Ƃ�
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()){
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomagnetic = event.values.clone();
			break;
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone();
			break;
		}

		if(geomagnetic != null && gravity != null){
			
			SensorManager.getRotationMatrix(
				rotationMatrix, null, 
				gravity, geomagnetic);
			
			SensorManager.getOrientation(
				rotationMatrix, 
				attitude);
			
			nowMagnetic.setMagnetic(
				attitude[0] * RAD2DEG,
				attitude[1] * RAD2DEG,
				attitude[2] * RAD2DEG
			);

		}
				
	}
	
	

}
