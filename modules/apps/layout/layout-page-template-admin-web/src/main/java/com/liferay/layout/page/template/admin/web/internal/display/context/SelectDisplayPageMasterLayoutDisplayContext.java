/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class SelectDisplayPageMasterLayoutDisplayContext {

	public SelectDisplayPageMasterLayoutDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<LayoutPageTemplateEntry> getMasterLayoutPageTemplateEntries() {
		List<LayoutPageTemplateEntry> masterLayoutPageTemplateEntries =
			new ArrayList<>();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				createLayoutPageTemplateEntry(0);

		layoutPageTemplateEntry.setName(
			LanguageUtil.get(_httpServletRequest, "blank"));
		layoutPageTemplateEntry.setStatus(WorkflowConstants.STATUS_APPROVED);

		masterLayoutPageTemplateEntries.add(layoutPageTemplateEntry);

		masterLayoutPageTemplateEntries.addAll(
			LayoutPageTemplateEntryServiceUtil.getLayoutPageTemplateEntries(
				_themeDisplay.getScopeGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		return masterLayoutPageTemplateEntries;
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}