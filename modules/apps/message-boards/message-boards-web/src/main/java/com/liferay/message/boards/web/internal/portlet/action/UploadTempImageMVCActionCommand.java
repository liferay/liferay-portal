/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.web.internal.upload.BaseMBUploadFileEntryHandler;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.upload.UploadHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/upload_temp_image"
	},
	service = MVCActionCommand.class
)
public class UploadTempImageMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	protected void activate() {
		_tempImageMBUploadFileEntryHandler =
			new TempImageMBUploadFileEntryHandler(
				_dlValidator, _mbMessageService);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_tempImageMBUploadFileEntryHandler,
			_itemSelectorUploadResponseHandler, actionRequest, actionResponse);
	}

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference
	private MBMessageService _mbMessageService;

	private TempImageMBUploadFileEntryHandler
		_tempImageMBUploadFileEntryHandler;

	@Reference
	private UploadHandler _uploadHandler;

	private static class TempImageMBUploadFileEntryHandler
		extends BaseMBUploadFileEntryHandler {

		public TempImageMBUploadFileEntryHandler(
			DLValidator dlValidator, MBMessageService mbMessageService) {

			super(dlValidator, mbMessageService);
		}

		@Override
		protected String getParameterName() {
			return "imageSelectorFileName";
		}

	}

}