/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.upload.BaseImageEditorUploadFileEntryHandler;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/upload_style_book_entry_preview"
	},
	service = MVCActionCommand.class
)
public class UploadStyleBookEntryPreviewMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_styleBookEntryPreviewImageEditorUploadFileEntryHandler,
			_itemSelectorUploadResponseHandler, actionRequest, actionResponse);
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference(
		target = "(resource.name=" + StyleBookConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	private final StyleBookEntryPreviewImageEditorUploadFileEntryHandler
		_styleBookEntryPreviewImageEditorUploadFileEntryHandler =
			new StyleBookEntryPreviewImageEditorUploadFileEntryHandler();

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class StyleBookEntryPreviewImageEditorUploadFileEntryHandler
		extends BaseImageEditorUploadFileEntryHandler {

		@Override
		protected void checkPermissions(
				UploadPortletRequest uploadPortletRequest)
			throws PrincipalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_portletResourcePermission.check(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup(),
				StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);
		}

		@Override
		protected DLAppService getDLAppService() {
			return _dlAppService;
		}

		@Override
		protected String getFolderName() {
			return StyleBookEntryPreviewImageEditorUploadFileEntryHandler.class.
				getName();
		}

		@Override
		protected UniqueFileNameProvider getUniqueFileNameProvider() {
			return _uniqueFileNameProvider;
		}

	}

}