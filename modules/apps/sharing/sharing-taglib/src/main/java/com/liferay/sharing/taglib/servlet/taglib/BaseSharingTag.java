/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.taglib.servlet.taglib;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Tardín
 */
public abstract class BaseSharingTag extends IncludeTag {

	@Override
	public int doStartTag() {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SharingConfigurationFactory sharingConfigurationFactory =
			_sharingConfigurationFactorySnapshot.get();

		SharingConfiguration sharingConfiguration =
			sharingConfigurationFactory.getGroupSharingConfiguration(
				themeDisplay.getSiteGroup());

		if (!sharingConfiguration.isEnabled()) {
			return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	}

	private static final Snapshot<SharingConfigurationFactory>
		_sharingConfigurationFactorySnapshot = new Snapshot<>(
			BaseSharingTag.class, SharingConfigurationFactory.class);

}