/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.exportimport.kernel.exception.LARFileSizeException;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;

import org.apache.commons.fileupload.FileUploadBase;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Renan Vasconcelos
 */
public abstract class BaseMVCActionCommand
	extends com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand {

	public void addTempFileEntry(ActionRequest actionRequest, String folderName)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			portal.getUploadPortletRequest(actionRequest);

		_checkExceededSizeLimit(uploadPortletRequest);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		deleteTempFileEntry(groupId, folderName);

		try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
				"file")) {

			String sourceFileName = uploadPortletRequest.getFileName("file");

			layoutService.addTempFileEntry(
				groupId, folderName, sourceFileName, inputStream,
				uploadPortletRequest.getContentType("file"));
		}
		catch (Exception exception) {
			UploadException uploadException =
				(UploadException)actionRequest.getAttribute(
					WebKeys.UPLOAD_EXCEPTION);

			if (uploadException != null) {
				Throwable throwable = uploadException.getCause();

				if (throwable instanceof FileUploadBase.IOFileUploadException) {
					if (_log.isInfoEnabled()) {
						_log.info("Temporary upload was cancelled");
					}
				}

				if (uploadException.isExceededFileSizeLimit()) {
					throw new FileSizeException(throwable);
				}

				if (uploadException.isExceededUploadRequestSizeLimit()) {
					throw new UploadRequestSizeException(throwable);
				}
			}
			else {
				throw exception;
			}
		}
	}

	public void deleteTempFileEntry(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String folderName)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONObject jsonObject = jsonFactory.createJSONObject();

		try {
			String fileName = ParamUtil.getString(actionRequest, "fileName");

			layoutService.deleteTempFileEntry(
				themeDisplay.getScopeGroupId(), folderName, fileName);

			jsonObject.put("deleted", Boolean.TRUE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			String errorMessage = themeDisplay.translate(
				"an-unexpected-error-occurred-while-deleting-the-file");

			jsonObject.put(
				"deleted", Boolean.FALSE
			).put(
				"errorMessage", errorMessage
			);
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	public void deleteTempFileEntry(long groupId, String folderName)
		throws PortalException {

		String[] tempFileNames = layoutService.getTempFileNames(
			groupId, folderName);

		for (String tempFileEntryName : tempFileNames) {
			layoutService.deleteTempFileEntry(
				groupId, folderName, tempFileEntryName);
		}
	}

	public void handleUploadException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String folderName, Exception exception)
		throws Exception {

		HttpServletResponse httpServletResponse = portal.getHttpServletResponse(
			actionResponse);

		httpServletResponse.setContentType(ContentTypes.TEXT_HTML);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		deleteTempFileEntry(themeDisplay.getScopeGroupId(), folderName);

		JSONObject jsonObject = staging.getExceptionMessagesJSONObject(
			themeDisplay.getLocale(), exception,
			(ExportImportConfiguration)null);

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected LayoutService layoutService;

	@Reference
	protected Portal portal;

	@Reference
	protected Staging staging;

	private void _checkExceededSizeLimit(HttpServletRequest httpServletRequest)
		throws Exception {

		UploadException uploadException =
			(UploadException)httpServletRequest.getAttribute(
				WebKeys.UPLOAD_EXCEPTION);

		if (uploadException != null) {
			Throwable throwable = uploadException.getCause();

			if (uploadException.isExceededFileSizeLimit() ||
				uploadException.isExceededUploadRequestSizeLimit()) {

				throw new LARFileSizeException(throwable);
			}

			throw new PortalException(throwable);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseMVCActionCommand.class);

}