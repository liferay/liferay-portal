/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action;

import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminConfigurationDisplayContext;
import com.liferay.content.dashboard.web.internal.util.ContentDashboardUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 + * @author David Arques
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"mvc.command.name=/content_dashboard/edit_content_dashboard_configuration"
	},
	service = MVCRenderCommand.class
)
public class EditContentDashboardConfigurationMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			ContentDashboardAdminConfigurationDisplayContext.class.getName(),
			new ContentDashboardAdminConfigurationDisplayContext(
				_assetVocabularyLocalService,
				ContentDashboardUtil.getAssetVocabularyIds(renderRequest),
				_groupLocalService,
				_portal.getHttpServletRequest(renderRequest), renderResponse));

		return "/edit_content_dashboard_configuration.jsp";
	}

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}