/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.layout.portlet;

import com.liferay.layout.portlet.PortletManager;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roselaine Marques
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
	service = PortletManager.class
)
public class MBPortletManager implements PortletManager {

	@Override
	public boolean isDeprecated() {
		return true;
	}

	@Override
	public boolean isVisible(Layout layout) {
		return FeatureFlagManagerUtil.isEnabled(
			layout.getCompanyId(), "LPD-105225");
	}

}