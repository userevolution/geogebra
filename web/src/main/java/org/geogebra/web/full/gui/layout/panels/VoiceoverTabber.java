package org.geogebra.web.full.gui.layout.panels;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.gui.AccessibilityManagerNoGui;
import org.geogebra.common.gui.SliderInput;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;
import org.geogebra.web.html5.util.sliderPanel.SliderW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Set of hidden widgets for navigating the construction with Voiceover (iOS)
 * 
 * @author Zbynek
 */
public class VoiceoverTabber {

	private Widget canvas;
	private AppW app;
	private double[] oldVal;
	private HTML focusTrap;
	private HTML focusTrapShift;

	/**
	 * @param app
	 *            app
	 * @param canvas
	 *            canvas
	 */
	public VoiceoverTabber(AppW app, Widget canvas) {
		this.app = app;
		this.canvas = canvas;
	}

	private Widget getCanvas() {
		return canvas;
	}

	/**
	 * Add dummy divs for handling focus change with swipe in VoiceOver.
	 * 
	 * @param p
	 *            euclidian panel
	 */
	public void add(final EuclidianPanel p) {
		final AppW app1 = app;
		if (getCanvas() != null) {
			getCanvas().addDomHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					app1.getAccessibilityManager().setTabOverGeos(true);
				}
			}, FocusEvent.getType());
		}
		final SliderW[] range = new SliderW[3];
		for (int i = 0; i < range.length; i++) {
			range[i] = makeSlider(i);
		}
		final Label simpleButton = new Label("button");
		hide(simpleButton);
		setRole(simpleButton, "button");
		simpleButton.getElement().setTabIndex(5000);
		simpleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				app1.handleSpaceKey();
			}
		});
		focusTrap = makeFocusTrap(false, range, simpleButton);
		focusTrapShift = makeFocusTrap(true, range, simpleButton);
		focusTrapShift.setVisible(false);
		p.add(focusTrapShift);
		for (SliderW r : range) {
			p.add(r);
		}
		p.add(simpleButton);
		simpleButton.setVisible(false);
		p.add(focusTrap);
	}

	private static void setRole(Label simpleButton, String string) {
		simpleButton.getElement().setAttribute("role", string);
	}

	private static void hide(Widget ui) {
		Style style = ui.getElement().getStyle();
		style.setOpacity(.01);
		style.setPosition(Position.FIXED);
		style.setWidth(1, Unit.PX);
		style.setHeight(1, Unit.PX);
		style.setOverflow(Overflow.HIDDEN);
	}

	private SliderW makeSlider(final int index) {
		final SliderW range = new SliderW(0, 10);
		hide(range);
		range.getElement().addClassName("slider");
		range.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				onSliderChange(range, index, event.getValue());

			}
		});
		range.getElement().setTabIndex(5000);
		range.setVisible(false);

		range.addDomHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				updateSelection(range, index);
			}
		}, FocusEvent.getType());
		return range;
	}

	/**
	 * @param range
	 *            slider
	 * @param index
	 *            slider index
	 * @param val
	 *            slider value
	 */
	protected void onSliderChange(SliderW range, int index, double val) {
		GeoElement sel = AccessibilityManagerNoGui.getSelectedGeo(app);
		if (sel != null && sel.isGeoNumeric()) {
			((GeoNumeric) sel).setValue(val);
			((GeoNumeric) sel).updateRepaint();
			range.getElement().focus();
			updateValueText(range, (GeoNumeric) sel);
			return;
		}
		double step = range.getValue() - oldVal[index];
		Log.error(range.getValue() + "/" + oldVal[index]);
		String unit = "";
		oldVal[index] += step;
		if (sel != null && sel.isGeoPoint()) {
			app.getGlobalKeyDispatcher().handleArrowKeyMovement(
					app.getSelectionManager().getSelectedGeos(),
					index == 0 ? step : 0, index == 1 ? step : 0,
					index == 2 ? step : 0, 1);
		} else {
			app.getAccessibilityManager().sliderChange(step);
			unit = "degrees";
		}
		updateValueText(range, range.getValue(), unit);
	}

	private HTML makeFocusTrap(final boolean backward, final SliderW[] range,
			final Label simpleButton) {
		HTML focusTrapN = new HTML(SafeHtmlUtils.fromTrustedString(
				"<div>select " + (backward ? "previous" : "next") + "</div>"));
		hide(focusTrapN);

		focusTrapN.getElement().setTabIndex(5000);

		ImageWrapper.nativeon(focusTrapN.getElement(), "focus",
				new ImageLoadCallback() {
					@Override
					public void onLoad() {
						onFocusTrap(backward, range, simpleButton);

					}
				});
		return focusTrapN;
	}

	/**
	 * @param backward
	 *            whether backward selection was triggered
	 * @param range
	 *            sliders
	 * @param simpleButton
	 *            button
	 */
	protected void onFocusTrap(boolean backward, SliderW[] range,
			Label simpleButton) {
		focusTrapShift.setVisible(true);
		app.getAccessibilityManager().setTabOverGeos(true);
		app.getGlobalKeyDispatcher().handleTab(false, backward);
		if (app.getAccessibilityManager().getSpaceAction() != null) {
			simpleButton
					.setText(app.getAccessibilityManager().getSpaceAction());
			setRole(simpleButton, "button");
			simpleButton.setVisible(true);			
			for (SliderW r : range) {
				r.setVisible(false);
			}
			forceFocus(simpleButton.getElement());
		} else if (app.getAccessibilityManager().getSliderAction() != null) {
			SliderInput slider = app.getAccessibilityManager()
					.getSliderAction();
			updateRange(range[0], slider);
			range[0].setVisible(true);
			AriaHelper.setLabel(range[0], slider.getDescription());
			simpleButton.setVisible(false);
			for (int i = 1; i < range.length; i++) {
				range[i].setVisible(false);
			}
			forceFocus(range[0].getElement());
		} else {
			GeoElement sel = AccessibilityManagerNoGui.getSelectedGeo(app);
			int dim = 0;
			if (sel instanceof GeoNumeric) {
				dim = 1;
			} else if (sel != null && sel.isGeoElement3D()) {
				dim = 3;
			} else if (sel instanceof GeoPointND) {
				dim = 2;
			}
			simpleButton.setVisible(false);
			for (int i = 0; i < range.length; i++) {
				updateSelection(range[i], i);
				range[i].setVisible(dim > i);
			}

			if (dim > 0) {
				SliderW nextRange = range[backward ? dim - 1 : 0];
				forceFocus(nextRange.getElement());
			} else if (sel != null) {
				setRole(simpleButton, "");
				simpleButton.setText(sel.getAuralText());
				simpleButton.setVisible(true);
				forceFocus(simpleButton.getElement());
			} else {
				if (backward) {
					focusTrapShift.setVisible(false);
				}
			}
		}
	}

	private void updateRange(SliderW range, SliderInput slider) {
		range.setMinimum(slider.getMin());
		range.setMaximum(slider.getMax());
		range.setStep(1);
		double val = getInitialValue(slider);
		range.setValue(val);
		oldVal[0] = val;
		updateValueText(range, val, "degrees");
	}

	private double getInitialValue(SliderInput slider) {
		if (slider == SliderInput.ROTATE_Z) {
			return app.getEuclidianView3D().getAngleA();
		}
		if (slider == SliderInput.TILT) {
			return app.getEuclidianView3D().getAngleB();
		}
		return 0;
	}

	/**
	 * @param range
	 *            slider
	 * @param sel
	 *            selected number
	 */
	protected void updateValueText(SliderW range, GeoNumeric sel) {
		range.getElement().setAttribute("aria-valuetext",
				sel.toValueString(StringTemplate.screenReader));
	}

	private void updateValueText(SliderW range, double sel, String unit) {
		range.getElement().setAttribute("aria-valuetext",
				app.getKernel().format(sel, StringTemplate.screenReader) + " "
						+ unit);
	}

	/**
	 * Deferred focus for element
	 * 
	 * @param element
	 *            element
	 */
	protected void forceFocus(final Element element) {
		// element.focus();
		app.invokeLater(new Runnable() {

			@Override
			public void run() {
				element.focus();
			}

		});
	}

	/**
	 * @param range
	 *            slider
	 * @param index
	 *            slider index
	 */
	protected void updateSelection(SliderW range, int index) {
		GeoElement sel = AccessibilityManagerNoGui.getSelectedGeo(app);
		if (sel == null) {
			if (app.getAccessibilityManager().getSliderAction() != null) {
				updateRange(range,
						app.getAccessibilityManager().getSliderAction());
			}
			return;
		}
		if (sel.isGeoNumeric()) {
			range.setMinimum(((GeoNumeric) sel).getIntervalMin());
			range.setMaximum(((GeoNumeric) sel).getIntervalMax());
			range.setStep(((GeoNumeric) sel).getAnimationStep());
			range.setValue(((GeoNumeric) sel).getValue());
			updateValueText(range, (GeoNumeric) sel);
		}
		if (sel.isGeoPoint()) {
			String[] labels = { "x coordinate of", "y coordinate of",
					"z coordinate of" };
			AriaHelper.setLabel(range,
					labels[index] + sel.getNameDescription());
			range.setMinimum(
					Math.floor(app.getActiveEuclidianView().getXmin()));
			range.setMaximum(Math.ceil(app.getActiveEuclidianView().getXmax()));
			range.setStep(sel.getAnimationStep());
			double coord = ((GeoPointND) sel).getInhomCoords().get(index + 1);
			this.oldVal = Cloner
					.clone(((GeoPointND) sel).getInhomCoords().get());
			range.setValue(coord);
			updateValueText(range, coord, "");
		} else {
			AriaHelper.setLabel(range, sel.getNameDescription());
		}
	}

}
