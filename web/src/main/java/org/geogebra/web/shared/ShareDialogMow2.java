package org.geogebra.web.shared;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         Joint share dialog for mow (group + link sharing)
 *
 */
public class ShareDialogMow2 extends DialogBoxW
		implements FastClickHandler, SetLabels, SaveListener {
	private AppW appW;
	private FlowPanel dialogContent;
	private Label selGroupLbl;
	private FlowPanel groupPanel;
	private ScrollPanel scrollPanel;
	private FlowPanel noGroupPanel;
	private Label noGroupsLbl;
	private Label noGroupsHelpLbl;
	private FlowPanel shareByLinkPanel;
	private Label linkShareOnOffLbl;
	private Label linkShareHelpLbl;
	private ComponentSwitch shareSwitch;
	private boolean isShareLinkOn = false;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton saveBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public ShareDialogMow2(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		buildGui();
	}

	/**
	 * @return true if share by link is active
	 */
	public boolean isShareLinkOn() {
		return isShareLinkOn;
	}

	/**
	 * @param isShareLinkOn
	 *            true if share by link is active
	 */
	public void setShareLinkOn(boolean isShareLinkOn) {
		this.isShareLinkOn = isShareLinkOn;
	}

	private void buildGui() {
		addStyleName("shareDialogMow");
		dialogContent = new FlowPanel();
		// get list of groups of user
		ArrayList<String> groupNames = app.getLoginOperation().getModel()
				.getUserGroups();
		// user has no groups
		if (/* groupNames.isEmpty() */ false) {
			buildNoGroupPanel();
		}
		// show groups of user
		else {
			buildGroupPanel(groupNames);
		}
		buildShareByLinkPanel();
		buildButtonPanel();
		add(dialogContent);
		setLabels();
	}

	private void buildNoGroupPanel() {
		noGroupPanel = new FlowPanel();
		noGroupPanel.addStyleName("noGroupPanel");
		SimplePanel groupImgHolder = new SimplePanel();
		groupImgHolder.addStyleName("groupImgHolder");
		NoDragImage groupImg = new NoDragImage(
				SharedResources.INSTANCE.groups(), 48);
		groupImgHolder.add(groupImg);
		noGroupPanel.add(groupImgHolder);
		noGroupsLbl = new Label();
		noGroupsLbl.setStyleName("noGroupsLbl");
		noGroupsHelpLbl = new Label();
		noGroupsHelpLbl.setStyleName("noGroupsHelpLbl");
		noGroupPanel.add(noGroupsLbl);
		noGroupPanel.add(noGroupsHelpLbl);
		dialogContent.add(noGroupPanel);
	}

	private void buildGroupPanel(ArrayList<String> groupNames) {
		selGroupLbl = new Label();
		selGroupLbl.setStyleName("selGrLbl");
		dialogContent.add(selGroupLbl);
		groupPanel = new FlowPanel();
		groupPanel.addStyleName("groupPanel");
		scrollPanel = new ScrollPanel();
		groupPanel.add(scrollPanel);
		FlowPanel groups = new FlowPanel();
		// ONLY FOR TESTING -> needs to be removed
		for (int i = 0; i < 40; i++) {
			groups.add(new GroupButtonMow("group group group " + i));
		}

		for (String group : groupNames) {
			groups.add(new GroupButtonMow(group));
		}
		scrollPanel.add(groups);
		dialogContent.add(groupPanel);
	}

	private void buildShareByLinkPanel() {
		shareByLinkPanel = new FlowPanel();
		shareByLinkPanel.addStyleName("shareByLink");
		NoDragImage linkImg = new NoDragImage(
				SharedResources.INSTANCE.mow_link_black(), 24);
		linkImg.addStyleName("linkImg");
		shareByLinkPanel.add(linkImg);
		FlowPanel textPanel = new FlowPanel();
		textPanel.addStyleName("textPanel");
		linkShareOnOffLbl = new Label();
		linkShareOnOffLbl.setStyleName("linkShareOnOff");
		linkShareHelpLbl = new Label();
		linkShareHelpLbl.setStyleName("linkShareHelp");
		textPanel.add(linkShareOnOffLbl);
		textPanel.add(linkShareHelpLbl);
		shareByLinkPanel.add(textPanel);
		shareSwitch = new ComponentSwitch(false);
		shareByLinkPanel.add(shareSwitch);
		dialogContent.add(shareByLinkPanel);
	}

	private void buildButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = addButton("Cancel");
		saveBtn = addButton("Save");
		dialogContent.add(buttonPanel);
	}

	private StandardButton addButton(String transKey) {
		StandardButton btn = new StandardButton(
				appW.getLocalization().getMenu(transKey), appW);
		btn.addFastClickHandler(this);
		buttonPanel.add(btn);
		return btn;
	}

	@Override
	public void setLabels() {
		getCaption().setText(app.getLocalization()
				.getMenu("Share"));
		if (selGroupLbl != null) {
			selGroupLbl
				.setText(app.getLocalization().getMenu("shareGroupHelpText"));
		}
		cancelBtn.setText(app.getLocalization().getMenu("Cancel"));
		saveBtn.setText(appW.getLocalization().getMenu("Save"));
		if (noGroupsLbl != null && noGroupsHelpLbl != null) {
			noGroupsLbl.setText(app.getLocalization().getMenu("NoGroups"));
			noGroupsHelpLbl
					.setText(app.getLocalization().getMenu("NoGroupShareTxt"));
		}
		linkShareOnOffLbl
				.setText(isShareLinkOn() ? "linkShareOn" : "linkShareOff");
		linkShareHelpLbl
				.setText(
						app.getLocalization().getMenu("ShareLinkHelpTxt"));
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == saveBtn) {
			// TODO share functionality
			hide();
		}
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}
}
