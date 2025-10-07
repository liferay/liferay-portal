/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSpaceConstants;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.site.cms.site.initializer.internal.util.SpaceSummaryHeaderUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class ViewSpaceContentsSummarySectionDisplayContext
	extends BaseContentsSectionDisplayContext {

	public ViewSpaceContentsSummarySectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService, long groupId,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal) {

		super(
			depotEntryLocalService, groupLocalService, httpServletRequest,
			language, objectDefinitionService,
			objectDefinitionSettingLocalService,
			objectEntryFolderModelResourcePermission, portal);

		_groupId = groupId;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
	}

	@Override
	public String getAPIURL() {
		return HttpComponentsUtil.addParameters(
			super.getAPIURL(), "page", CMSSpaceConstants.SPACE_SUMMARY_PAGE,
			"pageSize", CMSSpaceConstants.SPACE_SUMMARY_PAGE_SIZE, "sort",
			"dateModified:desc");
	}

	public Map<String, Object> getHeaderProps() throws Exception {
		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					getRootObjectEntryFolderExternalReferenceCode(), _groupId,
					themeDisplay.getCompanyId());

		return SpaceSummaryHeaderUtil.getSpaceSummaryHeaderProps(
			httpServletRequest, "view-all-content", Collections.emptyMap(),
			Collections.emptyMap(), "content",
			ActionUtil.getBaseViewFolderURL(themeDisplay) +
				objectEntryFolder.getObjectEntryFolderId());
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

	private final long _groupId;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}