/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.main.BlockedKeywordsController;
import com.liferay.osb.faro.web.internal.controller.main.ChannelController;
import com.liferay.osb.faro.web.internal.controller.main.DefinitionsController;
import com.liferay.osb.faro.web.internal.controller.main.GlobalPreferencesController;
import com.liferay.osb.faro.web.internal.controller.main.IssueController;
import com.liferay.osb.faro.web.internal.controller.main.MainController;
import com.liferay.osb.faro.web.internal.controller.main.NotificationController;
import com.liferay.osb.faro.web.internal.controller.main.OAuth2Controller;
import com.liferay.osb.faro.web.internal.controller.main.PreferencesController;
import com.liferay.osb.faro.web.internal.controller.main.ProjectController;
import com.liferay.osb.faro.web.internal.controller.main.ReportController;
import com.liferay.osb.faro.web.internal.controller.main.UserController;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@ApplicationPath("/" + FaroConstants.APPLICATION_MAIN)
@Component(
	property = {
		"jaxrs.application=true",
		"osgi.http.whiteboard.filter.dispatcher=FORWARD",
		"osgi.http.whiteboard.filter.dispatcher=REQUEST"
	},
	service = Application.class
)
public class MainApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		Set<Object> controllers = new HashSet<>();

		controllers.add(_blockedKeywordsController);
		controllers.add(_channelController);
		controllers.add(_definitionsController);
		controllers.add(_globalPreferencesController);
		controllers.add(_issueController);
		controllers.add(_mainController);
		controllers.add(_notificationController);
		controllers.add(_oAuth2Controller);
		controllers.add(_preferencesController);
		controllers.add(_projectController);
		controllers.add(_reportController);
		controllers.add(_userController);

		return controllers;
	}

	@Reference
	private BlockedKeywordsController _blockedKeywordsController;

	@Reference
	private ChannelController _channelController;

	@Reference
	private DefinitionsController _definitionsController;

	@Reference
	private GlobalPreferencesController _globalPreferencesController;

	@Reference
	private IssueController _issueController;

	@Reference
	private MainController _mainController;

	@Reference
	private NotificationController _notificationController;

	@Reference
	private OAuth2Controller _oAuth2Controller;

	@Reference
	private PreferencesController _preferencesController;

	@Reference
	private ProjectController _projectController;

	@Reference
	private ReportController _reportController;

	@Reference
	private UserController _userController;

}