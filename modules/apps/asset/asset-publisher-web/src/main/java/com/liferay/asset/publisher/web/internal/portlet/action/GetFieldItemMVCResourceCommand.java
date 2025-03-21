/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.action;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.ASSET_PUBLISHER,
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.HIGHEST_RATED_ASSETS,
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.MOST_VIEWED_ASSETS,
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.RECENT_CONTENT,
		"jakarta.portlet.name=" + AssetPublisherPortletKeys.RELATED_ASSETS,
		"mvc.command.name=/asset_publisher/get_field_item"
	},
	service = MVCResourceCommand.class
)
public class GetFieldItemMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		include(
			resourceRequest, resourceResponse,
			"/select_structure_field_item.jsp");
	}

}