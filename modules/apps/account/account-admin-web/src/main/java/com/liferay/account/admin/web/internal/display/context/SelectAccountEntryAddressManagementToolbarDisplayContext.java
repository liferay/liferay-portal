/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.admin.web.internal.display.AddressDisplay;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Pei-Jung Lan
 */
public class SelectAccountEntryAddressManagementToolbarDisplayContext
	extends ViewAccountEntryAddressesManagementToolbarDisplayContext {

	public SelectAccountEntryAddressManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<AddressDisplay> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"addAccountEntryDefaultAddressURL",
			PortletURLBuilder.createRenderURL(
				liferayPortletResponse
			).setMVCRenderCommandName(
				"/account_admin/edit_account_entry_address"
			).setBackURL(
				ParamUtil.getString(httpServletRequest, "redirect")
			).setParameter(
				"accountEntryId",
				ParamUtil.getString(httpServletRequest, "accountEntryId")
			).setParameter(
				"defaultType", ParamUtil.getString(httpServletRequest, "type")
			).buildString()
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return null;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	public Boolean isShowCreationMenu() {
		return true;
	}

	@Override
	protected String[] getNavigationKeys() {
		return null;
	}

}