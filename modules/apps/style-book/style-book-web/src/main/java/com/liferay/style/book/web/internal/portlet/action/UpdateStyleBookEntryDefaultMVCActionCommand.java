/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/update_style_book_entry_default"
	},
	service = MVCActionCommand.class
)
public class UpdateStyleBookEntryDefaultMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long styleBookEntryId = ParamUtil.getLong(
			actionRequest, "styleBookEntryId");

		if (styleBookEntryId > 0) {
			boolean defaultStyleBookEntry = ParamUtil.getBoolean(
				actionRequest, "defaultStyleBookEntry");

			_styleBookEntryService.updateDefaultStyleBookEntry(
				styleBookEntryId, defaultStyleBookEntry);

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		StyleBookEntry styleBookEntry = null;

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-30204")) {

			styleBookEntry =
				_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
					themeDisplay.getScopeGroupId(),
					ParamUtil.getString(actionRequest, "themeId"));
		}
		else {
			styleBookEntry = DefaultStyleBookEntryUtil.getDefaultStyleBookEntry(
				themeDisplay.getLayout());
		}

		if (styleBookEntry != null) {
			_styleBookEntryService.updateDefaultStyleBookEntry(
				styleBookEntry.getStyleBookEntryId(), false);
		}
	}

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}