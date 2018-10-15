package org.geogebra.web.shared;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author csilla
 * 
 *         material design switch component
 *
 */
public class ComponentSwitch extends FlowPanel {
	private SimplePanel track;
	private SimplePanel thumb;
	private boolean isSwitchOn;

	/**
	 * 
	 */
	public ComponentSwitch(boolean switchOn) {
		this.addStyleName("switch");
		this.isSwitchOn = switchOn;
		this.addStyleName(switchOn ? "on" : "off");
		track = new SimplePanel();
		track.addStyleName("track");
		thumb = new SimplePanel();
		thumb.addStyleName("thumb");
		this.add(track);
		this.add(thumb);
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				setSwitchOn(!isSwitchOn());
				updateSwitchStyle();
			}
		});
	}

	/**
	 * @return true if switch is on
	 */
	public boolean isSwitchOn() {
		return isSwitchOn;
	}

	/**
	 * @param isSwitchOn
	 *            true if switch is on
	 */
	public void setSwitchOn(boolean isSwitchOn) {
		this.isSwitchOn = isSwitchOn;
	}

	/**
	 * update style of switch depending on its status (on/off)
	 */
	public void updateSwitchStyle() {
		if (isSwitchOn()) {
			this.removeStyleName("off");
			this.addStyleName("on");
		} else {
			this.removeStyleName("on");
			this.addStyleName("off");
		}
	}
}
