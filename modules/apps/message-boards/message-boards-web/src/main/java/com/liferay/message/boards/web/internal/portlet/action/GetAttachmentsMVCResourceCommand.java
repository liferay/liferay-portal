/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/get_attachments"
	},
	service = MVCResourceCommand.class
)
public class GetAttachmentsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		MBMessage message = ActionUtil.getMessage(resourceRequest);

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(resourceResponse);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"active",
				_getAttachmentsJSONArray(
					message, message.getAttachmentsFileEntries(),
					resourceRequest, resourceResponse)
			).put(
				"deleted",
				_getAttachmentsJSONArray(
					message, message.getDeletedAttachmentsFileEntries(),
					resourceRequest, resourceResponse)
			));
	}

	private JSONArray _getAttachmentsJSONArray(
			MBMessage message, List<FileEntry> attachmentsFileEntries,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (FileEntry fileEntry : attachmentsFileEntries) {
			jsonArray.put(
				JSONUtil.put(
					"deleteURL",
					_getDeleteURL(
						message, resourceRequest, resourceResponse, fileEntry)
				).put(
					"id", fileEntry.getFileEntryId()
				).put(
					"size",
					_language.formatStorageSize(
						fileEntry.getSize(), resourceRequest.getLocale())
				).put(
					"title", fileEntry.getTitle()
				));
		}

		return jsonArray;
	}

	private String _getDeleteCommand(ResourceRequest resourceRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (_trashHelper.isTrashEnabled(themeDisplay.getScopeGroupId())) {
			return Constants.MOVE_TO_TRASH;
		}

		return Constants.DELETE;
	}

	private PortletURL _getDeleteURL(
			MBMessage message, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse, FileEntry fileEntry)
		throws Exception {

		return PortletURLBuilder.createActionURL(
			resourceResponse
		).setActionName(
			"/message_boards/edit_message_attachments"
		).setCMD(
			_getDeleteCommand(resourceRequest)
		).setParameter(
			"fileName", HtmlUtil.unescape(fileEntry.getTitle())
		).setParameter(
			"messageId", message.getMessageId()
		).buildPortletURL();
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}