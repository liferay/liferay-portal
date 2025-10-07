/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.manager;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Set;

/**
 * @author Georgel Pop
 */
public interface ContentManager {

	public Set<LayoutDisplayPageObjectProvider<?>>
		getFragmentEntryLinkMappedLayoutDisplayPageObjectProviders(
			FragmentEntryLink fragmentEntryLink);

	public Set<LayoutDisplayPageObjectProvider<?>>
		getLayoutMappedLayoutDisplayPageObjectProviders(String layoutData);

	public Set<LayoutDisplayPageObjectProvider<?>>
			getMappedLayoutDisplayPageObjectProviders(long groupId, long plid)
		throws PortalException;

	public JSONArray getPageContentsJSONArray(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long plid,
			long segmentsExperienceId)
		throws PortalException;

	public List<String> getRestrictedItemIds(
		HttpServletRequest httpServletRequest, LayoutStructure layoutStructure,
		ThemeDisplay themeDisplay);

	public void updateLayoutClassedModelUsage(
		FragmentEntryLink fragmentEntryLink);

	public void updateLayoutClassedModelUsage(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel);

}