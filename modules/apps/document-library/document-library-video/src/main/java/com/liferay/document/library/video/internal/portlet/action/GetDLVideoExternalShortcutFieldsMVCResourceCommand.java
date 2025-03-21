/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.portlet.action;

import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.resolver.DLVideoExternalShortcutResolver;
import com.liferay.document.library.video.internal.constants.DLVideoConstants;
import com.liferay.document.library.video.internal.constants.DLVideoPortletKeys;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLVideoPortletKeys.DL_VIDEO,
		"mvc.command.name=/document_library_video/get_dl_video_external_shortcut_fields"
	},
	service = MVCResourceCommand.class
)
public class GetDLVideoExternalShortcutFieldsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		DLVideoExternalShortcut dlVideoExternalShortcut =
			_dlVideoExternalShortcutResolver.resolve(
				ParamUtil.getString(
					resourceRequest, "dlVideoExternalShortcutURL"));

		if (dlVideoExternalShortcut != null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					DLVideoConstants.DDM_FIELD_NAME_DESCRIPTION,
					GetterUtil.getString(
						dlVideoExternalShortcut.getDescription())
				).put(
					DLVideoConstants.DDM_FIELD_NAME_HTML,
					GetterUtil.getString(
						dlVideoExternalShortcut.renderHTML(
							_portal.getHttpServletRequest(resourceRequest)))
				).put(
					DLVideoConstants.DDM_FIELD_NAME_THUMBNAIL_URL,
					GetterUtil.getString(
						dlVideoExternalShortcut.getThumbnailURL())
				).put(
					DLVideoConstants.DDM_FIELD_NAME_TITLE,
					GetterUtil.getString(dlVideoExternalShortcut.getTitle())
				).put(
					DLVideoConstants.DDM_FIELD_NAME_URL,
					GetterUtil.getString(dlVideoExternalShortcut.getURL())
				));
		}
	}

	@Reference
	private DLVideoExternalShortcutResolver _dlVideoExternalShortcutResolver;

	@Reference
	private Portal _portal;

}