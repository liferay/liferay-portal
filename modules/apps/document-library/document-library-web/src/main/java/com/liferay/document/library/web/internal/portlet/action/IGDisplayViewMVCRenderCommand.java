/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/", "mvc.command.name=/image_gallery_display/view"
	},
	service = MVCRenderCommand.class
)
public class IGDisplayViewMVCRenderCommand extends BaseFolderMVCRenderCommand {

	@Override
	protected DLTrashHelper getDLTrashHelper() {
		return _dlTrashHelper;
	}

	@Override
	protected String getPath() {
		return "/image_gallery_display/view.jsp";
	}

	@Reference
	private DLTrashHelper _dlTrashHelper;

}