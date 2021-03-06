package org.geogebra.desktop.geogebra3D.input3D.leonar3do;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.Input3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.settings.EuclidianSettings3D;


/**
 * controller with specific methods from leonar3do input system
 * @author mathieu
 *
 */
public class InputLeo3D extends Input3D {

	
	private LeoSocket leoSocket;
	

	private double[] mousePosition;
	
	private double[] mouseOrientation;
	
	private boolean isRightPressed, isLeftPressed;
	
	private double[][] glassesPosition1;
	
	private double eyeSeparation;
	
	/**
	 * constructor
	 */
	public InputLeo3D() {
		
		// 3D mouse position
		mousePosition = new double[3];
		
		// 3D mouse orientation
		mouseOrientation = new double[4];
		
		// glasses position
		glassesPosition1 = new double[2][];
		for (int i = 0 ; i < 2 ; i++){
			glassesPosition1[i] = new double[3];
		}
		
		// screen dimensions
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenHalfWidth = gd.getDisplayMode().getWidth()/2;
		//screenHalfHeight = gd.getDisplayMode().getHeight()/2;		
		//App.debug("screen:"+screenWidth+"x"+screenHeight);
		
		//App.error("height/2="+gd.getDisplayMode().getHeight()/2);
		
		
		leoSocket = new LeoSocket();
	}
	
	
	@Override
	public boolean update() {
	
		boolean updateOccured = false;
		
		// update from last message
		if (leoSocket.gotMessage){
			
			// mouse position
			mousePosition[0] = leoSocket.birdX * screenHalfWidth;
			mousePosition[1] = leoSocket.birdY * screenHalfWidth;
			mousePosition[2] = leoSocket.birdZ * screenHalfWidth;
			
			/*
			App.debug("\norientation"
			+"\nx="+leoSocket.birdOrientationX
			+"\ny="+leoSocket.birdOrientationY
			+"\nz="+leoSocket.birdOrientationZ
			+"\nw="+leoSocket.birdOrientationW
			+"\nagnle="+(2*Math.acos(leoSocket.birdOrientationW)*180/Math.PI)+"");
			*/
			
			// mouse position
			mouseOrientation[0] = leoSocket.birdOrientationX;
			mouseOrientation[1] = leoSocket.birdOrientationY;
			mouseOrientation[2] = leoSocket.birdOrientationZ;
			mouseOrientation[3] = leoSocket.birdOrientationW;

			
			// right button
			isRightPressed = (leoSocket.bigButton > 0.5);
			
			// left button
			isLeftPressed = (leoSocket.smallButton > 0.5);
			
		
			
			
			
			// eye separation
			eyeSeparation = (leoSocket.leftEyeX - leoSocket.rightEyeX) * screenHalfWidth;

			// glasses position
			glassesPosition1[0][0] = leoSocket.leftEyeX * screenHalfWidth;
			glassesPosition1[0][1] = leoSocket.leftEyeY * screenHalfWidth;
			glassesPosition1[0][2] = leoSocket.leftEyeZ * screenHalfWidth;
			glassesPosition1[1][0] = leoSocket.rightEyeX * screenHalfWidth;
			glassesPosition1[1][1] = leoSocket.rightEyeY * screenHalfWidth;
			glassesPosition1[1][2] = leoSocket.rightEyeZ * screenHalfWidth;

			/*
			App.debug("\nleft eye"
					+"\nx="+leftEyePosition[0]
					+"\ny="+leftEyePosition[1]
					+"\nz="+leftEyePosition[2]
				    +
					"\nright eye"
					+"\nx="+rightEyePosition[0]
					+"\ny="+rightEyePosition[1]
					+"\nz="+rightEyePosition[2]);
					
			App.debug("\nleft-right="+(rightEyePosition[0]-leftEyePosition[0])+"\nheight="+GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight());
			*/
			
			/*
			App.debug("\nbuttons"
					+"\nbig = "+leoSocket.bigButton
					+"\nright = "+isRightPressed
					);
					*/
			
			updateOccured = true;
		}
		
		// request next message
		leoSocket.getLeoData();		
		
		return updateOccured;
		
	}
	
	@Override
	public double[] getInputPosition(){
		return mousePosition;
	}
	
	@Override
	public double[] getInputOrientation(){
		return mouseOrientation;
	}
	
	@Override
	public boolean isRightPressed(){
		return isRightPressed;
	}
	
	@Override
	public boolean isLeftPressed(){
		return isLeftPressed;
	}

	@Override
	public boolean isThirdButtonPressed() {
		return false;
	}

	@Override
	public boolean isButtonPressed() {
		return isRightPressed() || isLeftPressed();
	}

	@Override
	public double[] getGlassesPosition(int i){
		return glassesPosition1[i];
	}
	
	@Override
	public double getEyeSeparation(){
		return eyeSeparation;
	}
	
	@Override
	public boolean useInputDepthForHitting(){
		return true;
	}
	
	@Override
	public boolean useMouseRobot(){
		return true;
	}
	
//	@Override
//	public float getMouse2DX(){
//		return 0f;
//	}
//	
//	@Override
//	public float getMouse2DY(){
//		return 0f;
//	}
//	
//	@Override
//	public float getMouse2DFactor(){
//		return 1f;
//	}
	
	@Override
	public DeviceType getDeviceType(){
		return DeviceType.PEN;
	}
	
	@Override
	public boolean hasMouse(EuclidianView3D view3D, Coords mouse3DPosition){
		return view3D.hasMouse2D();
	}

	@Override
	public boolean hasMouse(EuclidianView3D view3D) {
		return view3D.hasMouse2D();
	}

	@Override
	public boolean currentlyUseMouse2D(){
		return false;
	}
	
	@Override
	public void setHasCompletedGrabbingDelay(boolean flag) {
		// not used for leo
	}
	
	@Override
	public boolean hasCompletedGrabbingDelay(){
		return false;
	}

	@Override
	public void setPositionXYOnPanel(double[] absolutePos, Coords panelPos, 
			double screenHalfWidth, double screenHalfHeight, int panelPositionX, int panelPositionY,
			int panelDimW, int panelDimH) {
		panelPos.setX(absolutePos[0] + screenHalfWidth - panelPositionX
				- panelDimW / 2);
		panelPos.setY(absolutePos[1] - screenHalfHeight + panelPositionY
				+ panelDimH / 2);				

	}
	
	@Override
	public boolean useScreenZOffset(){
		return true;
	}
	
	@Override
	public boolean isStereoBuffered() {
		return false;
	}
	
	@Override
	public boolean useInterlacedPolarization(){
		return true;
	}
	
	@Override
	public boolean useCompletingDelay(){
		return false;
	}
	
	@Override
	public boolean hasMouseDirection() {
		return false;
	}

	@Override
	public double[] getInputDirection() {
		return null;
	}

	@Override
	public boolean useQuaternionsForRotate() {
		return true;
	}

	@Override
	public boolean wantsStereo() {
		return true;
	}

	@Override
	public double getDefaultRotationOz() {
		return EuclidianView3D.ANGLE_ROT_OZ;
	}

	@Override
	public double getDefaultRotationXOY() {
		return EuclidianView3D.ANGLE_ROT_XOY;
	}

	@Override
	public boolean shouldStoreStereoToXML() {
		return false;
	}

	@Override
	public boolean needsGrayBackground() {
		return false;
	}

	@Override
	public boolean useHeadTracking() {
		return true;
	}

	@Override
	public boolean useHandGrabbing() {
		return false;
	}

	@Override
	public OutOfField getOutOfField() {
		return OutOfField.NEVER;
	}

	@Override
	public void exit() {
		// not used here
	}

	@Override
	public void setPositionOnScreen() {
		// nothing to do
	}

	@Override
	public void setPositionOffScreen() {
		// nothing to do
	}

	@Override
	public boolean isZSpace() {
		return false;
	}

	@Override
	public void setSpecificSettings(EuclidianSettings3D settings) {
		// nothing to do
	}

}
