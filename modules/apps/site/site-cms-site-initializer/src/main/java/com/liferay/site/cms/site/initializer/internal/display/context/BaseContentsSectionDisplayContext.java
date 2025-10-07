/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Roberto Díaz
 */
public abstract class BaseContentsSectionDisplayContext
	extends BaseSectionDisplayContext {

	public BaseContentsSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal) {

		super(
			depotEntryLocalService, null, groupLocalService, httpServletRequest,
			language, objectDefinitionService,
			objectDefinitionSettingLocalService,
			objectEntryFolderModelResourcePermission, portal);
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		List<DropdownItem> fdsBulkActionDropdownItems =
			super.getBulkActionDropdownItems();

		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "pencil", "edit-categories",
				LanguageUtil.get(httpServletRequest, "edit-categories"), "post",
				"edit-categories", null));
		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "pencil", "edit-tags",
				LanguageUtil.get(httpServletRequest, "edit-tags"), "post",
				"edit-tags", null));
		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				"#", "password-policies", "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), null, null,
				null));
		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				"#", "password-policies", "default-permissions",
				LanguageUtil.get(httpServletRequest, "default-permissions"),
				null, null, null));

		return fdsBulkActionDropdownItems;
	}

	@Override
	public List<DropdownItem> getCreationMenuDropdownItems() {
		return ActionUtil.getContentsSectionCreationMenuDropdownItems(
			httpServletRequest, null);
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			language.get(httpServletRequest, getEmptyStateDescriptionKey())
		).put(
			"image", "/states/cms_empty_state_content.svg"
		).put(
			"title", language.get(httpServletRequest, "no-content-yet")
		).build();
	}

	protected abstract String getEmptyStateDescriptionKey();

	@Override
	protected String[] getObjectFolderExternalReferenceCodes() {
		return new String[] {
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES
		};
	}

	@Override
	protected String getRootObjectEntryFolderExternalReferenceCode() {
		return ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS;
	}

}