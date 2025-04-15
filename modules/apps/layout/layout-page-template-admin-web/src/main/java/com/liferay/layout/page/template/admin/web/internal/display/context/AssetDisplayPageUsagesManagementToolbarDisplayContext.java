/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Rubén Pulido
 */
public class AssetDisplayPageUsagesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public AssetDisplayPageUsagesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<AssetDisplayPageEntry> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteAssetDisplayPageEntry");
				dropdownItem.putData(
					"deleteAssetDisplayPageEntryMessage",
					_getDeleteAssetDisplayPageEntryMessage());
				dropdownItem.putData(
					"deleteAssetDisplayPageEntryURL",
					_getPortletURL(
						"/layout_page_template_admin" +
							"/delete_asset_display_page_entry"));
				dropdownItem.setLabel(
					_getDeleteAssetDisplayPageEntryDropdownItemLabel());
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "updateAssetDisplayPageEntry");
				dropdownItem.putData(
					"updateAssetDisplayPageEntryURL",
					_getPortletURL(
						"/layout_page_template_admin" +
							"/update_asset_display_page_entry"));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "unassign"));
			}
		).build();
	}

	@Override
	public String getComponentId() {
		return "assetDisplayPageUsagesManagementToolbar";
	}

	@Override
	public String getDefaultEventHandler() {
		return "assetDisplayPageUsagesManagementToolbarDefaultEventHandler";
	}

	public LayoutPageTemplateEntry getDefaultLayoutPageTemplateEntry() {
		if (_defaultLayoutPageTemplateEntry != null) {
			return _defaultLayoutPageTemplateEntry;
		}

		_defaultLayoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchDefaultLayoutPageTemplateEntry(
					_themeDisplay.getScopeGroupId(), _getClassNameId(),
					_getClassTypeId());

		return _defaultLayoutPageTemplateEntry;
	}

	@Override
	public String getSearchContainerId() {
		return "assetDisplayPageEntries";
	}

	@Override
	public Boolean isShowSearch() {
		return false;
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"modified-date"};
	}

	private long _getClassNameId() {
		if (Validator.isNotNull(_classNameId)) {
			return _classNameId;
		}

		_classNameId = ParamUtil.getLong(httpServletRequest, "classNameId");

		return _classNameId;
	}

	private long _getClassTypeId() {
		if (Validator.isNotNull(_classTypeId)) {
			return _classTypeId;
		}

		_classTypeId = ParamUtil.getLong(httpServletRequest, "classTypeId");

		return _classTypeId;
	}

	private String _getDeleteAssetDisplayPageEntryDropdownItemLabel() {
		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			getDefaultLayoutPageTemplateEntry();

		if (defaultLayoutPageTemplateEntry == null) {
			return LanguageUtil.get(httpServletRequest, "assign-to-default");
		}

		return LanguageUtil.format(
			httpServletRequest, "assign-to-default-(x)",
			defaultLayoutPageTemplateEntry.getName(), false);
	}

	private String _getDeleteAssetDisplayPageEntryMessage() {
		LayoutPageTemplateEntry defaultLayoutPageTemplateEntry =
			getDefaultLayoutPageTemplateEntry();

		if (defaultLayoutPageTemplateEntry == null) {
			return LanguageUtil.get(
				httpServletRequest,
				"are-you-sure-you-want-to-use-the-default-display-page-" +
					"template-for-this");
		}

		return LanguageUtil.format(
			httpServletRequest,
			"are-you-sure-you-want-to-use-the-default-display-page-template-" +
				"(x)-for-this",
			defaultLayoutPageTemplateEntry.getName(), false);
	}

	private String _getPortletURL(String actionName) {
		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			actionName
		).setRedirect(
			_themeDisplay.getURLCurrent()
		).buildString();
	}

	private Long _classNameId;
	private Long _classTypeId;
	private LayoutPageTemplateEntry _defaultLayoutPageTemplateEntry;
	private final ThemeDisplay _themeDisplay;

}