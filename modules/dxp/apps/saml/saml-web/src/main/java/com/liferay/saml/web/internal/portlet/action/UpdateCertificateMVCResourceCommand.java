/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.web.internal.util.SamlTempFileEntryUtil;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;
import java.io.InputStream;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/update_certificate"
	},
	service = MVCResourceCommand.class
)
public class UpdateCertificateMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin()) {
			throw new PrincipalException();
		}

		String cmd = ParamUtil.get(
			resourceRequest, Constants.CMD, Constants.GET_TEMP);

		if (cmd.equals(Constants.ADD_TEMP)) {
			_addTempFile(resourceRequest, resourceResponse);
		}
		else if (cmd.equals(Constants.DELETE_TEMP)) {
			_deleteTempFile(resourceRequest, resourceResponse, themeDisplay);
		}
		else if (cmd.equals(Constants.GET_TEMP)) {
			_includeTempFileName(
				resourceRequest, resourceResponse, themeDisplay.getUser());
		}
	}

	private void _addTempFile(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		_uploadHandler.upload(
			_certificateUploadFileEntryHandler,
			_certificateUploadResponseHandler, resourceRequest,
			resourceResponse);
	}

	private void _deleteTempFile(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			ThemeDisplay themeDisplay)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			SamlTempFileEntryUtil.deleteTempFileEntry(
				themeDisplay.getUser(),
				ParamUtil.getString(resourceRequest, "fileName"));

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
			resourceRequest, resourceResponse, jsonObject);
	}

	private void _includeTempFileName(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse,
			User user)
		throws Exception {

		String selectUploadedFile = ParamUtil.getString(
			resourceRequest, "selectUploadedFile");

		if (Validator.isNotNull(selectUploadedFile)) {
			try {
				FileEntry tempFileEntry =
					SamlTempFileEntryUtil.getTempFileEntry(
						user, selectUploadedFile);

				if (tempFileEntry != null) {
					JSONPortletResponseUtil.writeJSON(
						resourceRequest, resourceResponse,
						JSONUtil.put(selectUploadedFile));

					return;
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException.toString(), portalException);
				}
			}
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, JSONUtil.putAll());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateCertificateMVCResourceCommand.class);

	private final CertificateUploadFileEntryHandler
		_certificateUploadFileEntryHandler =
			new CertificateUploadFileEntryHandler();
	private final CertificateUploadResponseHandler
		_certificateUploadResponseHandler =
			new CertificateUploadResponseHandler();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private UploadHandler _uploadHandler;

	private class CertificateUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (!permissionChecker.isCompanyAdmin()) {
				throw new PrincipalException();
			}

			FileEntry fileEntry = null;

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"file")) {

				fileEntry = SamlTempFileEntryUtil.addTempFileEntry(
					permissionChecker.getUser(),
					uploadPortletRequest.getFileName("file"), inputStream,
					uploadPortletRequest.getContentType("file"));
			}
			catch (PortalException portalException) {
				throw new IOException(portalException);
			}

			try {
				_validateFile(fileEntry);

				return fileEntry;
			}
			catch (Exception exception) {
				TempFileEntryUtil.deleteTempFileEntry(
					fileEntry.getFileEntryId());

				if (exception instanceof RuntimeException) {
					throw (RuntimeException)exception;
				}

				throw new PortalException(exception);
			}
		}

		private void _validateFile(FileEntry fileEntry)
			throws CertificateException, KeyStoreException {

			try (InputStream inputStream = fileEntry.getContentStream()) {
				KeyStore keyStore = KeyStore.getInstance("PKCS12");

				keyStore.load(inputStream, null);
			}
			catch (IOException ioException) {
				throw new KeyStoreException(ioException);
			}
			catch (KeyStoreException | NoSuchAlgorithmException |
				   PortalException exception) {

				throw new SystemException(exception);
			}
		}

	}

	private class CertificateUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", themeDisplay.getLocale(), getClass());

			String errorMessage = StringPool.BLANK;

			if (portalException instanceof PrincipalException) {
				errorMessage = _language.format(
					resourceBundle,
					"you-must-be-an-admin-to-complete-this-action", null);
			}
			else if (portalException.getCause() instanceof
						CertificateException) {

				errorMessage = _language.format(
					resourceBundle,
					"there-was-a-problem-reading-one-or-more-certificates-in-" +
						"the-keystore",
					null);
			}
			else if (portalException.getCause() instanceof KeyStoreException) {
				errorMessage = _language.format(
					resourceBundle,
					"the-file-is-not-a-pkcs12-formatted-keystore", null);
			}
			else {
				errorMessage = _language.format(
					resourceBundle, "an-unexpected-error-occurred", null);
			}

			JSONObject exceptionMessagesJSONObject = JSONUtil.put(
				"message", errorMessage);

			return exceptionMessagesJSONObject.put("status", 499);
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return JSONUtil.put(
				"groupId", fileEntry.getGroupId()
			).put(
				"name", fileEntry.getTitle()
			).put(
				"title", uploadPortletRequest.getFileName("file")
			).put(
				"uuid", fileEntry.getUuid()
			);
		}

	}

}