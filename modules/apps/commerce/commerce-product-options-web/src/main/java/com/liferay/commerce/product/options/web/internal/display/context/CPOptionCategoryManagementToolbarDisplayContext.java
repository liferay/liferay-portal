/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.display.context;

import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class CPOptionCategoryManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public CPOptionCategoryManagementToolbarDisplayContext(
			CPOptionCategoryDisplayContext cpOptionCategoryDisplayContext,
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			cpOptionCategoryDisplayContext.getSearchContainer());

		_cpOptionCategoryDisplayContext = cpOptionCategoryDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteEntries");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public String getComponentId() {
		return "cpOptionCategoriesManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName",
					"/cp_specification_options/edit_cp_option_category",
					"redirect", currentURLObj.toString());
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "add-specification-group"));
			}
		).build();
	}

	@Override
	public String getInfoPanelId() {
		if (_cpOptionCategoryDisplayContext.isShowInfoPanel()) {
			return "infoPanelId";
		}

		return null;
	}

	@Override
	public String getSearchContainerId() {
		return "cpOptionCategories";
	}

	@Override
	public Boolean isShowCreationMenu() {
		try {
			return _cpOptionCategoryDisplayContext.hasPermission(
				CPActionKeys.ADD_COMMERCE_PRODUCT_OPTION_CATEGORY);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"modified-date", "group", "priority"};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPOptionCategoryManagementToolbarDisplayContext.class);

	private final CPOptionCategoryDisplayContext
		_cpOptionCategoryDisplayContext;

}