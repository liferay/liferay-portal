/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

/**
 * @author Carolina Barbosa
 */
public class UploadAttachmentMVCActionCommand extends BaseMVCActionCommand {

	public UploadAttachmentMVCActionCommand(
		UploadFileEntryHandler uploadFileEntryHandler,
		UploadResponseHandler uploadResponseHandler,
		UploadHandler uploadHandler) {

		_uploadFileEntryHandler = uploadFileEntryHandler;
		_uploadResponseHandler = uploadResponseHandler;
		_uploadHandler = uploadHandler;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_uploadFileEntryHandler, _uploadResponseHandler, actionRequest,
			actionResponse);

		hideDefaultSuccessMessage(actionRequest);
	}

	private final UploadFileEntryHandler _uploadFileEntryHandler;
	private final UploadHandler _uploadHandler;
	private final UploadResponseHandler _uploadResponseHandler;

}