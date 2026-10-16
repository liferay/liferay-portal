/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.configuration.definition;

import com.liferay.portal.kernel.settings.TypedSettings;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.wiki.configuration.WikiGroupServiceConfigurationOverride;

/**
 * @author Iván Zaera
 */
public class WikiGroupServiceConfigurationOverrideImpl
	implements WikiGroupServiceConfigurationOverride {

	public WikiGroupServiceConfigurationOverrideImpl(
		TypedSettings typedSettings) {

		_typedSettings = typedSettings;
	}

	@Override
	public boolean enableRss() {
		if (!PortalUtil.isRSSFeedsEnabled()) {
			return false;
		}

		return _typedSettings.getBooleanValue("enableRss");
	}

	private final TypedSettings _typedSettings;

}