/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Veronica Gonzalez
 */
public class ViewOverdueReviewsSectionDisplayContext
	extends BaseSectionDisplayContext {

	public ViewOverdueReviewsSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService, Portal portal,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry,
		FDSItemsActions viewOverdueReviewsSectionFDSItemsActions,
		SystemFDSEntry viewOverdueReviewsSectionSystemFDSEntry) {

		super(
			depotEntryLocalService, dlConfiguration, groupLocalService,
			httpServletRequest, language, objectDefinitionService, portal,
			translationInfoItemFieldValuesExporterRegistry);

		_viewOverdueReviewsSectionFDSItemsActions =
			viewOverdueReviewsSectionFDSItemsActions;
		_viewOverdueReviewsSectionSystemFDSEntry =
			viewOverdueReviewsSectionSystemFDSEntry;
	}

	@Override
	public String getAdditionalAPIURLParameters() {
		return _viewOverdueReviewsSectionSystemFDSEntry.
			getAdditionalAPIURLParameters(httpServletRequest);
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

	@Override
	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		addBreadcrumbItem(
			jsonArray, false,
			portal.getLayoutFullURL(
				LayoutLocalServiceUtil.getLayoutByFriendlyURL(
					themeDisplay.getScopeGroupId(), false, "/dashboard"),
				themeDisplay),
			language.get(httpServletRequest, "dashboard"));
		addBreadcrumbItem(
			jsonArray, true, null,
			language.get(httpServletRequest, "overdue-reviews"));

		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", jsonArray
		).put(
			"hideSpace", true
		).build();
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.<DropdownItem>fromArray(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"date-time"
			).setLabel(
				language.get(httpServletRequest, "update-review-date")
			).build(
				"update-review-date"
			));
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			language.get(httpServletRequest, "there-are-no-overdue-reviews")
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title", language.get(httpServletRequest, "no-overdue-reviews")
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return _viewOverdueReviewsSectionFDSItemsActions.
			getFDSActionDropdownItems(httpServletRequest);
	}

	@Override
	protected String getCMSSectionFilterString() {
		throw new UnsupportedOperationException(
			"ViewOverdueReviewsSectionSystemFDSEntry must calculate this");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewOverdueReviewsSectionDisplayContext.class);

	private final FDSItemsActions _viewOverdueReviewsSectionFDSItemsActions;
	private final SystemFDSEntry _viewOverdueReviewsSectionSystemFDSEntry;

}