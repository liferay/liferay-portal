/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sam Ziemer
 */
public class StructuresSectionDisplayContext extends BaseSectionDisplayContext {

	public StructuresSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest) {

		super(cmsSiteInitializerConfiguration, httpServletRequest);
	}

	@Override
	public CreationMenu getCreationMenu() {
		String href = StringPool.BLANK;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			Layout layout = LayoutLocalServiceUtil.getLayoutByFriendlyURL(
				themeDisplay.getScopeGroupId(), false, "/structure-builder");

			href = PortalUtil.getLayoutFullURL(layout, themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(159176);
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "content"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(159176);
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "file"));
			}
		).build();
	}

	@Override
	public String[] getEntryClassNames() {
		return cmsSiteInitializerConfiguration.structuresClassNames();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StructuresSectionDisplayContext.class);

}