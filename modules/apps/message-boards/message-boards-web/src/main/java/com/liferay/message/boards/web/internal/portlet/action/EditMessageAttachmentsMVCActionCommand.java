/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.web.internal.upload.BaseMBUploadFileEntryHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/edit_message_attachments"
	},
	service = MVCActionCommand.class
)
public class EditMessageAttachmentsMVCActionCommand
	extends BaseMVCActionCommand {

	@Activate
	protected void activate() {
		_tempAttachmentMBUploadFileEntryHandler =
			new TempAttachmentMBUploadFileEntryHandler(
				_dlValidator, _mbMessageService);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD_TEMP)) {
				_addTempAttachment(actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteAttachment(actionRequest, false);
			}
			else if (cmd.equals(Constants.DELETE_TEMP)) {
				_deleteTempAttachment(actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.EMPTY_TRASH)) {
				_emptyTrash(actionRequest);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteAttachment(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				_restoreEntries(actionRequest);
			}

			if (Validator.isNotNull(cmd)) {
				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (PrincipalException principalException) {
			SessionErrors.add(actionRequest, principalException.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/message_boards/error.jsp");
		}
	}

	private void _addTempAttachment(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_tempAttachmentMBUploadFileEntryHandler,
			_multipleUploadResponseHandler, actionRequest, actionResponse);
	}

	private void _deleteAttachment(
			ActionRequest actionRequest, boolean moveToTrash)
		throws PortalException {

		long messageId = ParamUtil.getLong(actionRequest, "messageId");

		String fileName = ParamUtil.getString(actionRequest, "fileName");

		if (moveToTrash) {
			_mbMessageService.moveMessageAttachmentToTrash(messageId, fileName);
		}
		else {
			_mbMessageService.deleteMessageAttachment(messageId, fileName);
		}
	}

	private void _deleteTempAttachment(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long categoryId = ParamUtil.getLong(uploadPortletRequest, "categoryId");
		String fileName = ParamUtil.getString(actionRequest, "fileName");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_mbMessageService.deleteTempAttachment(
				themeDisplay.getScopeGroupId(), categoryId,
				MBMessageConstants.TEMP_FOLDER_NAME, fileName);

			jsonObject.put("deleted", Boolean.TRUE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			jsonObject.put("deleted", Boolean.FALSE);

			String errorMessage = themeDisplay.translate(
				"an-unexpected-error-occurred-while-deleting-the-file");

			jsonObject.put("errorMessage", errorMessage);
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private void _emptyTrash(ActionRequest actionRequest) throws Exception {
		long messageId = ParamUtil.getLong(actionRequest, "messageId");

		_mbMessageService.emptyMessageAttachments(messageId);
	}

	private void _restoreEntries(ActionRequest actionRequest) throws Exception {
		long messageId = ParamUtil.getLong(actionRequest, "messageId");

		String fileName = ParamUtil.getString(actionRequest, "fileName");

		_mbMessageService.restoreMessageAttachmentFromTrash(
			messageId, fileName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditMessageAttachmentsMVCActionCommand.class);

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private MBMessageService _mbMessageService;

	@Reference(target = "(upload.response.handler=multiple)")
	private UploadResponseHandler _multipleUploadResponseHandler;

	@Reference
	private Portal _portal;

	private TempAttachmentMBUploadFileEntryHandler
		_tempAttachmentMBUploadFileEntryHandler;

	@Reference
	private UploadHandler _uploadHandler;

	private static class TempAttachmentMBUploadFileEntryHandler
		extends BaseMBUploadFileEntryHandler {

		public TempAttachmentMBUploadFileEntryHandler(
			DLValidator dlValidator, MBMessageService mbMessageService) {

			super(dlValidator, mbMessageService);
		}

		@Override
		protected String getParameterName() {
			return "file";
		}

	}

}