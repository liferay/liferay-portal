/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/view_list_items"
	},
	service = MVCRenderCommand.class
)
public class ViewListItemsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String collectionType = ParamUtil.getString(
			renderRequest, "collectionType");

		if (Objects.equals(
				collectionType,
				InfoListProviderItemSelectorReturnType.class.getName())) {

			return "/view_info_collection_provider_items.jsp";
		}

		if (Objects.equals(
				collectionType,
				InfoListItemSelectorReturnType.class.getName())) {

			return "/view_asset_list_items.jsp";
		}

		return "/error.jsp";
	}

}