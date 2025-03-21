/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.frontend.taglib.form.navigator;

import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.constants.AssetListFormConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.web.internal.constants.AssetListWebKeys;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "form.navigator.entry.order:Integer=400",
	service = FormNavigatorEntry.class
)
public class AssetListScopeFormNavigatorEntry
	extends BaseAssetListFormNavigatorEntry {

	@Override
	public String getKey() {
		return AssetListFormConstants.ENTRY_KEY_SCOPE;
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			AssetListWebKeys.ITEM_SELECTOR, _itemSelector);

		super.include(httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isVisible(User user, AssetListEntry assetListEntry) {
		if (assetListEntry == null) {
			return false;
		}

		if (assetListEntry.getType() ==
				AssetListEntryTypeConstants.TYPE_DYNAMIC) {

			return true;
		}

		return false;
	}

	@Override
	protected String getJspPath() {
		return "/asset_list/scope.jsp";
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.asset.list.web)")
	private ServletContext _servletContext;

}