/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Jürgen Kappler
 */
public class ViewAllSectionDisplayContext extends BaseSectionDisplayContext {

	public ViewAllSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal, FDSCreationMenu viewAllSectionFDSCreationMenu,
		FDSItemsActions viewAllSectionFDSItemsActions,
		SystemFDSEntry viewAllSectionSystemFDSEntry,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		super(
			depotEntryLocalService, dlConfiguration, groupLocalService,
			httpServletRequest, language, objectDefinitionService,
			objectDefinitionSettingLocalService,
			objectEntryFolderModelResourcePermission, portal,
			translationInfoItemFieldValuesExporterRegistry);

		_httpServletRequest = httpServletRequest;

		_viewAllSectionFDSCreationMenu = viewAllSectionFDSCreationMenu;
		_viewAllSectionFDSItemsActions = viewAllSectionFDSItemsActions;
		_viewAllSectionSystemFDSEntry = viewAllSectionSystemFDSEntry;
	}

	@Override
	public String getAdditionalAPIURLParameters() {
		return _viewAllSectionSystemFDSEntry.getAdditionalAPIURLParameters(
			httpServletRequest);
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		List<DropdownItem> fdsBulkActionDropdownItems =
			super.getBulkActionDropdownItems();

		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				StringPool.BLANK, "download", "download",
				LanguageUtil.get(_httpServletRequest, "download"), null, null,
				null));
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
				StringPool.BLANK, "password-policies", "permissions",
				LanguageUtil.get(_httpServletRequest, "permissions"), null,
				null, null));
		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				StringPool.BLANK, "password-policies",
				"edit-default-permissions-by-role",
				LanguageUtil.get(
					httpServletRequest, "edit-default-permissions-by-role"),
				null, null, null));
		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				StringPool.BLANK, "password-policies",
				"edit-permissions-by-role",
				LanguageUtil.get(
					httpServletRequest, "edit-permissions-by-role"),
				null, null, null));
		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				StringPool.BLANK, "password-policies",
				"reset-to-default-permissions",
				LanguageUtil.get(
					httpServletRequest, "reset-to-default-permissions"),
				null, null, null));

		return fdsBulkActionDropdownItems;
	}

	@Override
	public CreationMenu getCreationMenu() {
		return _viewAllSectionFDSCreationMenu.getCreationMenu(
			httpServletRequest);
	}

	@Override
	public List<DropdownItem> getCreationMenuDropdownItems() {
		throw new UnsupportedOperationException(
			"ViewAllSectionCreationMenu must calculate this");
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest,
				"click-new-or-drag-and-drop-your-files-here")
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-assets-yet")
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return _viewAllSectionFDSItemsActions.getFDSActionDropdownItems(
			httpServletRequest);
	}

	@Override
	protected String getCMSSectionFilterString() {
		throw new UnsupportedOperationException(
			"ViewAllSectionSystemFDSEntry must calculate this");
	}

	private final HttpServletRequest _httpServletRequest;
	private final FDSCreationMenu _viewAllSectionFDSCreationMenu;
	private final FDSItemsActions _viewAllSectionFDSItemsActions;
	private final SystemFDSEntry _viewAllSectionSystemFDSEntry;

}