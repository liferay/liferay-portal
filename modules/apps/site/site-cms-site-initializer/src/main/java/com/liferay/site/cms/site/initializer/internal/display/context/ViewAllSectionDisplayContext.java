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
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
		ObjectDefinitionService objectDefinitionService, Portal portal,
		FDSCreationMenu viewAllSectionFDSCreationMenu,
		FDSItemsActions viewAllSectionFDSItemsActions,
		SystemFDSEntry viewAllSectionSystemFDSEntry,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		super(
			depotEntryLocalService, dlConfiguration, groupLocalService,
			httpServletRequest, language, objectDefinitionService, portal,
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
	public Map<String, Object> getAdditionalProps() {
		Map<String, Object> additionalProps = super.getAdditionalProps();

		try {
			additionalProps.put("breadcrumbProps", getBreadcrumbProps());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return additionalProps;
	}

	public Map<String, Object> getAllQuickFiltersProps() {
		return HashMapBuilder.<String, Object>put(
			"freeTier", LicenseManagerUtil.isFreeTier()
		).build();
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		return SectionDisplayContextUtil.getAllSectionBulkActionDropdownItems(
			httpServletRequest);
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

	@Override
	protected boolean isFolderSearchEnabled() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewAllSectionDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final FDSCreationMenu _viewAllSectionFDSCreationMenu;
	private final FDSItemsActions _viewAllSectionFDSItemsActions;
	private final SystemFDSEntry _viewAllSectionSystemFDSEntry;

}