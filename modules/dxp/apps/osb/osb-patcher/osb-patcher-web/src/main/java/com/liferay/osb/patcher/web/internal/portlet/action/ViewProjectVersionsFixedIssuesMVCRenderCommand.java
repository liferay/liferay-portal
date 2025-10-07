/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.exception.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/view_project_versions_fixed_issues"
	},
	service = MVCRenderCommand.class
)
public class ViewProjectVersionsFixedIssuesMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long patcherProjectVersionId = ParamUtil.getLong(
			renderRequest, "patcherProjectVersionId");

		try {
			_patcherProjectVersionLocalService.getPatcherProjectVersion(
				patcherProjectVersionId);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchPatcherProjectVersionException) {
				SessionErrors.add(renderRequest, exception.getClass());

				return "/osb_patcher/views/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/osb_patcher/views/view_tickets.jsp";
	}

	@Reference
	private PatcherProjectVersionLocalService
		_patcherProjectVersionLocalService;

}