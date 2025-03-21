/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.web.internal.helper.PublicationHelper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/get_share_publication_link"
	},
	service = MVCResourceCommand.class
)
public class GetSharePublicationLinkMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId");

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctCollectionId);

		boolean shareable = ParamUtil.getBoolean(resourceRequest, "shareable");

		if (ctCollection.isShareable() != shareable) {
			ctCollection.setShareable(shareable);

			ctCollection = _ctCollectionLocalService.updateCTCollection(
				ctCollection);
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"shareable", ctCollection.isShareable()
			).put(
				"sharePublicationLink",
				_publicationHelper.getShareURL(
					ctCollection.getCtCollectionId(), resourceRequest)
			));
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private PublicationHelper _publicationHelper;

}