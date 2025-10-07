/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Albertinin Mourato Santos
 */
@Component(service = FragmentRenderer.class)
public class AddSpaceMembersFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected String getLabelKey() {
		return "add-members";
	}

	@Override
	protected String getModuleName() {
		return "AddSpaceMembers";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long assetLibraryId = ParamUtil.getLong(
			httpServletRequest, "assetLibraryId");

		String assetLibraryName = StringPool.BLANK;
		long creatorUserId = 0;
		String externalReferenceCode = StringPool.BLANK;
		DepotEntry depotEntry = _depotEntryLocalService.fetchDepotEntry(
			assetLibraryId);

		if (depotEntry != null) {
			Group group = _groupLocalService.fetchGroup(
				depotEntry.getGroupId());

			assetLibraryName = group.getDescriptiveName(
				themeDisplay.getLocale());
			creatorUserId = group.getCreatorUserId();
			externalReferenceCode = group.getExternalReferenceCode();
		}

		return HashMapBuilder.<String, Object>put(
			"assetLibraryCreatorUserId", creatorUserId
		).put(
			"assetLibraryId", assetLibraryId
		).put(
			"assetLibraryName", assetLibraryName
		).put(
			"baseAssetLibraryURL", ActionUtil.getBaseSpaceURL(themeDisplay)
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"hasAssignMembersPermission", true
		).put(
			"learnResources",
			LearnMessageUtil.getReactDataJSONObject("site-cms-site-initializer")
		).build();
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}