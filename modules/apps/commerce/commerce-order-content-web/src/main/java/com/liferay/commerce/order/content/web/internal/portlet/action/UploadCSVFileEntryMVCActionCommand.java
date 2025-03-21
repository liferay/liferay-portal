/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_OPEN_ORDER_CONTENT,
		"mvc.command.name=/commerce_open_order_content/upload_csv_file_entry"
	},
	service = MVCActionCommand.class
)
public class UploadCSVFileEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_csvUploadFileEntryHandler, _csvUploadResponseHandler,
			actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadCSVFileEntryMVCActionCommand.class);

	private final CSVUploadFileEntryHandler _csvUploadFileEntryHandler =
		new CSVUploadFileEntryHandler();
	private final CSVUploadResponseHandler _csvUploadResponseHandler =
		new CSVUploadResponseHandler();

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private File _file;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class CSVUploadFileEntryHandler implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			String fileName = uploadPortletRequest.getFileName(_parameterName);

			_dlValidator.validateFileSize(
				themeDisplay.getScopeGroupId(), fileName,
				uploadPortletRequest.getContentType(_parameterName),
				uploadPortletRequest.getSize(_parameterName));

			String extension = _file.getExtension(fileName);

			if (!extension.equals("csv")) {
				throw new FileExtensionException.InvalidExtension(
					"Invalid extension for file name " + fileName);
			}

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					_parameterName)) {

				return _addFileEntry(
					fileName,
					uploadPortletRequest.getContentType(_parameterName),
					inputStream,
					(ThemeDisplay)uploadPortletRequest.getAttribute(
						WebKeys.THEME_DISPLAY));
			}
		}

		private FileEntry _addFileEntry(
				String fileName, String contentType, InputStream inputStream,
				ThemeDisplay themeDisplay)
			throws PortalException {

			String uniqueFileName = _uniqueFileNameProvider.provide(
				fileName, curFileName -> _exists(curFileName, themeDisplay));

			Company company = themeDisplay.getCompany();

			return TempFileEntryUtil.addTempFileEntry(
				company.getGroupId(), themeDisplay.getUserId(), _tempFolderName,
				uniqueFileName, inputStream, contentType);
		}

		private boolean _exists(String fileName, ThemeDisplay themeDisplay) {
			try {
				FileEntry tempFileEntry = TempFileEntryUtil.getTempFileEntry(
					themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
					_tempFolderName, fileName);

				if (tempFileEntry != null) {
					return true;
				}

				return false;
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return false;
			}
		}

		private final String _parameterName = "imageSelectorFileName";
		private final String _tempFolderName =
			CSVUploadFileEntryHandler.class.getName();

	}

	private class CSVUploadResponseHandler implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onFailure(
					portletRequest, portalException);

			if (portalException instanceof FileExtensionException) {
				jsonObject.put(
					"error",
					JSONUtil.put(
						"errorType",
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION
					).put(
						"message", ".csv"
					));
			}
			else {
				throw portalException;
			}

			return jsonObject;
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return _itemSelectorUploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);
		}

	}

}