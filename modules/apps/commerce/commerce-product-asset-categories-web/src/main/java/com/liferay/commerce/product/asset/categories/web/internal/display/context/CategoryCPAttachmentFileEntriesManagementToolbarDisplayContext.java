/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryServiceUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class CategoryCPAttachmentFileEntriesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public CategoryCPAttachmentFileEntriesManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			_createSearchContainer(
				httpServletRequest, liferayPortletRequest, portletURL));
	}

	@Override
	public CreationMenu getCreationMenu() {
		AssetCategory assetCategory =
			(AssetCategory)httpServletRequest.getAttribute(
				WebKeys.ASSET_CATEGORY);

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCRenderCommandName(
						"/commerce_product_asset_categories" +
							"/edit_asset_category_cp_attachment_file_entry"
					).setRedirect(
						currentURLObj
					).setParameter(
						"categoryId", assetCategory.getCategoryId()
					).buildPortletURL());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-image"));
			}
		).build();
	}

	public SearchContainer<?> getSearchContainer() {
		return searchContainer;
	}

	@Override
	public String getSearchContainerId() {
		return "cpAttachmentFileEntries";
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String getDisplayStyle() {
		return "list";
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list"};
	}

	private static SearchContainer<CPAttachmentFileEntry>
			_createSearchContainer(
				HttpServletRequest httpServletRequest,
				LiferayPortletRequest liferayPortletRequest,
				PortletURL portletURL)
		throws PortalException {

		SearchContainer<CPAttachmentFileEntry>
			cpAttachmentFileEntrySearchContainer = new SearchContainer<>(
				liferayPortletRequest, portletURL, null, null);

		AssetCategory assetCategory =
			(AssetCategory)httpServletRequest.getAttribute(
				WebKeys.ASSET_CATEGORY);

		cpAttachmentFileEntrySearchContainer.setResultsAndTotal(
			() -> CPAttachmentFileEntryServiceUtil.getCPAttachmentFileEntries(
				PortalUtil.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(),
				CPAttachmentFileEntryConstants.TYPE_IMAGE,
				WorkflowConstants.STATUS_ANY,
				cpAttachmentFileEntrySearchContainer.getStart(),
				cpAttachmentFileEntrySearchContainer.getEnd()),
			CPAttachmentFileEntryServiceUtil.getCPAttachmentFileEntriesCount(
				PortalUtil.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(),
				CPAttachmentFileEntryConstants.TYPE_IMAGE,
				WorkflowConstants.STATUS_ANY));

		return cpAttachmentFileEntrySearchContainer;
	}

}