/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class ViewSpaceContentsAbstractSectionDisplayContext
	extends BaseContentsSectionDisplayContext {

	public ViewSpaceContentsAbstractSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService, long groupId,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal) {

		super(
			depotEntryLocalService, groupLocalService, httpServletRequest,
			language, objectDefinitionService,
			objectDefinitionSettingLocalService,
			objectEntryFolderModelResourcePermission, portal);

		_groupId = groupId;
	}

	@Override
	public String getAPIURL() {
		return HttpComponentsUtil.addParameters(
			super.getAPIURL(), "page", _PAGE, "pageSize", _PAGE_SIZE);
	}

	public Map<String, Object> getHeaderProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"label", language.get(httpServletRequest, "view-all-content")
		).put(
			"title", language.get(httpServletRequest, "content")
		).put(
			"url",
			StringBundler.concat(
				themeDisplay.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/e/space-contents/",
				portal.getClassNameId(DepotEntry.class), StringPool.SLASH,
				_groupId)
		).build();
	}

	@Override
	protected String getCMSSectionFilterString() {
		return String.format(
			"cmsRoot eq true and cmsSection eq 'contents' and groupIds/any" +
				"(g:g eq %s)",
			_groupId);
	}

	@Override
	protected String getEmptyStateDescriptionKey() {
		return "create-and-manage-content-within-this-space";
	}

	private static final int _PAGE = 1;

	private static final int _PAGE_SIZE = 6;

	private final long _groupId;

}