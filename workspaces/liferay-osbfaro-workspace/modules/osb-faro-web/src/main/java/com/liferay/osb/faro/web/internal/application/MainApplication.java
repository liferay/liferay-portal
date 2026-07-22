/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.main.BlockedKeywordsFaroController;
import com.liferay.osb.faro.web.internal.controller.main.CatalogFaroController;
import com.liferay.osb.faro.web.internal.controller.main.ChannelFaroController;
import com.liferay.osb.faro.web.internal.controller.main.DefinitionsFaroController;
import com.liferay.osb.faro.web.internal.controller.main.GlobalPreferencesFaroController;
import com.liferay.osb.faro.web.internal.controller.main.IssueFaroController;
import com.liferay.osb.faro.web.internal.controller.main.MainFaroController;
import com.liferay.osb.faro.web.internal.controller.main.NotificationFaroController;
import com.liferay.osb.faro.web.internal.controller.main.OAuth2FaroController;
import com.liferay.osb.faro.web.internal.controller.main.PageExperienceFaroController;
import com.liferay.osb.faro.web.internal.controller.main.PreferencesFaroController;
import com.liferay.osb.faro.web.internal.controller.main.ProjectFaroController;
import com.liferay.osb.faro.web.internal.controller.main.ReportFaroController;
import com.liferay.osb.faro.web.internal.controller.main.UserFaroController;

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

		controllers.add(_blockedKeywordsFaroController);
		controllers.add(_catalogFaroController);
		controllers.add(_channelFaroController);
		controllers.add(_definitionsFaroController);
		controllers.add(_globalPreferencesFaroController);
		controllers.add(_issueFaroController);
		controllers.add(_mainFaroController);
		controllers.add(_notificationFaroController);
		controllers.add(_oAuth2FaroController);
		controllers.add(_pageExperienceFaroController);
		controllers.add(_preferencesFaroController);
		controllers.add(_projectFaroController);
		controllers.add(_reportFaroController);
		controllers.add(_userFaroController);

		return controllers;
	}

	@Reference
	private BlockedKeywordsFaroController _blockedKeywordsFaroController;

	@Reference
	private CatalogFaroController _catalogFaroController;

	@Reference
	private ChannelFaroController _channelFaroController;

	@Reference
	private DefinitionsFaroController _definitionsFaroController;

	@Reference
	private GlobalPreferencesFaroController _globalPreferencesFaroController;

	@Reference
	private IssueFaroController _issueFaroController;

	@Reference
	private MainFaroController _mainFaroController;

	@Reference
	private NotificationFaroController _notificationFaroController;

	@Reference
	private OAuth2FaroController _oAuth2FaroController;

	@Reference
	private PageExperienceFaroController _pageExperienceFaroController;

	@Reference
	private PreferencesFaroController _preferencesFaroController;

	@Reference
	private ProjectFaroController _projectFaroController;

	@Reference
	private ReportFaroController _reportFaroController;

	@Reference
	private UserFaroController _userFaroController;

}