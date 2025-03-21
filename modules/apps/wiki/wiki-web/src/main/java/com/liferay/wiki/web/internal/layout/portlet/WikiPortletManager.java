/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.layout.portlet;

import com.liferay.layout.portlet.PortletManager;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.wiki.constants.WikiPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY
	},
	service = PortletManager.class
)
public class WikiPortletManager implements PortletManager {

	@Override
	public boolean isDeprecated() {
		return true;
	}

	@Override
	public boolean isVisible(Layout layout) {
		return FeatureFlagManagerUtil.isEnabled(
			layout.getCompanyId(), "LPD-35013");
	}

}