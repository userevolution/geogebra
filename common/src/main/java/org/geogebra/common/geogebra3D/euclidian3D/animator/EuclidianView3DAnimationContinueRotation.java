package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;

/**
 * animation for continue rotation
 *
 */
public class EuclidianView3DAnimationContinueRotation extends EuclidianView3DAnimation {

	private static final double MAX_ROT_SPEED = 0.1;

	private double animatedRotSpeed, animatedRotTimeStart, aOld, bOld;

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 * @param delay delay occurring between user interaction and animation start
	 * @param rotSpeed rotation speed
	 */
	EuclidianView3DAnimationContinueRotation(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			double delay, double rotSpeed) {

		super(view3D, animator);
		double rotSpeed2 = rotSpeed;
		// if speed is too large, use max speed
		if (rotSpeed2 > MAX_ROT_SPEED) {
			rotSpeed2 = MAX_ROT_SPEED;
		} else if (rotSpeed2 < -MAX_ROT_SPEED) {
			rotSpeed2 = -MAX_ROT_SPEED;
		}
		view3D.getSettings().setRotSpeed(0);
		animatedRotSpeed = -rotSpeed2;
		animatedRotTimeStart = -delay;
	}

	public void setupForStart() {
		animatedRotTimeStart += view3D.getApplication().getMillisecondTime();
		aOld = view3D.getAngleA();
		bOld = view3D.getAngleB();
	}

	public AnimationType getType() {
		return AnimationType.CONTINUE_ROTATION;
	}

	public void animate() {
		double da = (view3D.getApplication().getMillisecondTime() - animatedRotTimeStart) * animatedRotSpeed;
		view3D.setRotXYinDegrees(aOld + da, bOld);
		view3D.updateRotationAndScaleMatrices();
		view3D.setGlobalMatrices();
		view3D.setViewChangedByRotate();
	}

}
