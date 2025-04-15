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
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class CPSpecificationOptionManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public CPSpecificationOptionManagementToolbarDisplayContext(
			CPSpecificationOptionDisplayContext
				cpSpecificationOptionDisplayContext,
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			cpSpecificationOptionDisplayContext.getSearchContainer());

		_cpSpecificationOptionDisplayContext =
			cpSpecificationOptionDisplayContext;
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
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "cpSpecificationOptionsManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName",
					"/cp_specification_options/edit_cp_specification_option");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "add-specification-label"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() ->
				Validator.isNotNull(getNavigation()) &&
				!Objects.equals(getNavigation(), "all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());
				labelItem.setDismissible(true);
				labelItem.setLabel(
					LanguageUtil.get(httpServletRequest, getNavigation()));
			}
		).build();
	}

	@Override
	public String getInfoPanelId() {
		if (_cpSpecificationOptionDisplayContext.isShowInfoPanel()) {
			return "infoPanelId";
		}

		return null;
	}

	@Override
	public String getSearchContainerId() {
		return "cpSpecificationOptions";
	}

	@Override
	public Boolean isShowCreationMenu() {
		try {
			return _cpSpecificationOptionDisplayContext.hasPermission(
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
	protected String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(
			httpServletRequest, "use-in-faceted-navigation");
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "yes", "no"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"group", "label", "modified-date", "priority"};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionManagementToolbarDisplayContext.class);

	private final CPSpecificationOptionDisplayContext
		_cpSpecificationOptionDisplayContext;

}