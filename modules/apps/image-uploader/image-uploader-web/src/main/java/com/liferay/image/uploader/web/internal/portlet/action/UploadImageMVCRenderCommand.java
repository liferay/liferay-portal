/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.image.uploader.web.internal.portlet.action;

import com.liferay.image.uploader.web.internal.constants.ImageUploaderPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Levente Hudák
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ImageUploaderPortletKeys.IMAGE_UPLOADER,
		"mvc.command.name=/image_uploader/upload_image"
	},
	service = MVCRenderCommand.class
)
public class UploadImageMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return "/upload_image.jsp";
	}

}