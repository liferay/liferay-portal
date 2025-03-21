/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.portlet.action;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.constants.AssetPublisherWebKeys;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
		"mvc.command.name=/asset_publisher/select_structure_field"
	},
	service = MVCRenderCommand.class
)
public class SelectStructureFieldMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			AssetPublisherWebKeys.ASSET_PUBLISHER_WEB_HELPER,
			_assetPublisherWebHelper);

		return "/select_structure_field.jsp";
	}

	@Reference
	private AssetPublisherWebHelper _assetPublisherWebHelper;

}