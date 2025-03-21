/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.upload.test;

import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UniqueFileNameProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Manuel de la Peña
 */
public class TestUploadHandler {

	public TestUploadHandler(
		TestUploadPortlet testUploadPortlet,
		UniqueFileNameProvider uniqueFileNameProvider) {

		_testUploadPortlet = testUploadPortlet;
		_uniqueFileNameProvider = uniqueFileNameProvider;
	}

	public void upload(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(portletRequest);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		try {
			UploadException uploadException =
				(UploadException)portletRequest.getAttribute(
					WebKeys.UPLOAD_EXCEPTION);

			if (uploadException != null) {
				Throwable throwable = uploadException.getCause();

				if (uploadException.isExceededFileSizeLimit()) {
					throw new FileSizeException(throwable);
				}

				if (uploadException.isExceededLiferayFileItemSizeLimit()) {
					throw new LiferayFileItemException(throwable);
				}

				if (uploadException.isExceededUploadRequestSizeLimit()) {
					throw new UploadRequestSizeException(throwable);
				}

				throw new PortalException(throwable);
			}

			JSONObject imageJSONObject = _getImageJSONObject(portletRequest);

			String randomId = ParamUtil.getString(
				uploadPortletRequest, "randomId");

			imageJSONObject.put("randomId", randomId);

			jsonObject.put(
				"file", imageJSONObject
			).put(
				"success", Boolean.TRUE
			);

			JSONPortletResponseUtil.writeJSON(
				portletRequest, portletResponse, jsonObject);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
		catch (PortalException portalException) {
			_handleUploadException(
				portletRequest, portletResponse, portalException, jsonObject);
		}
	}

	private JSONObject _getImageJSONObject(PortletRequest portletRequest)
		throws PortalException {

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(portletRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject imageJSONObject = JSONFactoryUtil.createJSONObject();

		try {
			imageJSONObject.put(
				"attributeDataImageId",
				EditorConstants.ATTRIBUTE_DATA_IMAGE_ID);

			String parameterName = TestUploadPortlet.PARAMETER_NAME;

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					parameterName)) {

				TestFileEntry testFileEntry = new TestFileEntry(
					_uniqueFileNameProvider.provide(
						uploadPortletRequest.getFileName(parameterName),
						curFileName -> _hasFileEntry(
							themeDisplay.getScopeGroupId(), curFileName)),
					DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					themeDisplay.getScopeGroupId(), inputStream);

				_testUploadPortlet.put(testFileEntry);

				imageJSONObject.put(
					"fileEntryId", testFileEntry.getFileEntryId()
				).put(
					"groupId", testFileEntry.getGroupId()
				).put(
					"title", testFileEntry.getTitle()
				).put(
					"type", "document"
				).put(
					"url",
					PortletFileRepositoryUtil.getPortletFileEntryURL(
						themeDisplay, testFileEntry, StringPool.BLANK)
				).put(
					"uuid", testFileEntry.getUuid()
				);

				return imageJSONObject;
			}
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private void _handleUploadException(
		PortletRequest portletRequest, PortletResponse portletResponse,
		PortalException portalException, JSONObject jsonObject) {

		jsonObject.put("success", Boolean.FALSE);

		if (portalException instanceof AntivirusScannerException ||
			portalException instanceof FileNameException ||
			portalException instanceof FileSizeException ||
			portalException instanceof UploadRequestSizeException) {

			String errorMessage = StringPool.BLANK;
			int errorType = 0;

			if (portalException instanceof AntivirusScannerException) {
				errorType =
					ServletResponseConstants.SC_FILE_ANTIVIRUS_EXCEPTION;
				AntivirusScannerException antivirusScannerException =
					(AntivirusScannerException)portalException;

				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				errorMessage = themeDisplay.translate(
					antivirusScannerException.getMessageKey());
			}
			else if (portalException instanceof FileNameException) {
				errorType = ServletResponseConstants.SC_FILE_NAME_EXCEPTION;
			}
			else if (portalException instanceof FileSizeException) {
				errorType = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
			}
			else {
				errorType =
					ServletResponseConstants.SC_UPLOAD_REQUEST_SIZE_EXCEPTION;
			}

			jsonObject.put(
				"error",
				JSONUtil.put(
					"errorType", errorType
				).put(
					"message", errorMessage
				));
		}

		try {
			JSONPortletResponseUtil.writeJSON(
				portletRequest, portletResponse, jsonObject);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private boolean _hasFileEntry(long groupId, String fileName) {
		FileEntry fileEntry = new TestFileEntry(
			fileName, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, groupId,
			null);

		TestFileEntry testFileEntry = _testUploadPortlet.get(
			fileEntry.toString());

		if (testFileEntry == null) {
			return false;
		}

		return true;
	}

	private final TestUploadPortlet _testUploadPortlet;
	private final UniqueFileNameProvider _uniqueFileNameProvider;

}