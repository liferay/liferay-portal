/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Veronica Gonzalez
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.OVERDUE_REVIEWS_SECTION,
	service = FDSItemsActions.class
)
public class ViewOverdueReviewsSectionFDSItemsActions
	implements FDSItemsActions {

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return Arrays.asList(
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"date"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "update-review-date")
			).setPermissionKey(
				"update"
			).build(
				"update-review-date"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&redirect=",
					URLCodec.encodeURL(themeDisplay.getURLCurrent()))
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).build(
				"edit"
			));
	}

}