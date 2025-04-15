/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.layout.portlet;

import com.liferay.layout.portlet.PortletManager;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;

import org.osgi.service.component.annotations.Component;

/**
 * @author Istvan Sajtos
 */
@Component(
	property = "jakarta.portlet.name=" + LoginPortletKeys.FORGOT_PASSWORD,
	service = PortletManager.class
)
public class ForgotPasswordPortletManager implements PortletManager {

	@Override
	public boolean isVisible(Layout layout) {
		return FeatureFlagManagerUtil.isEnabled("LPD-6378");
	}

}