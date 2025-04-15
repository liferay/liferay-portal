/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.display.context;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.web.internal.constants.AnalyticsSettingsWebKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Riccardo Ferrari
 */
public class AnalyticsSettingsDisplayContext extends BaseDisplayContext {

	public AnalyticsSettingsDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		super(httpServletRequest, httpServletResponse);

		_analyticsConfiguration =
			(AnalyticsConfiguration)httpServletRequest.getAttribute(
				AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION);
	}

	public String getLiferayAnalyticsURL() {
		return _analyticsConfiguration.liferayAnalyticsURL();
	}

	public String getToken() {
		return _analyticsConfiguration.token();
	}

	public boolean isConnected() {
		return !Validator.isBlank(_analyticsConfiguration.token());
	}

	public boolean isWizardMode() {
		return _analyticsConfiguration.wizardMode();
	}

	private final AnalyticsConfiguration _analyticsConfiguration;

}