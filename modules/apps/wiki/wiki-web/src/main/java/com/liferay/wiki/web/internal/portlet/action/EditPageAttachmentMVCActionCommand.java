/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.InvalidFileVersionException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.exception.SourceFileNameException;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.service.WikiPageService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/wiki/edit_page_attachment"
	},
	service = MVCActionCommand.class
)
public class EditPageAttachmentMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			UploadException uploadException =
				(UploadException)actionRequest.getAttribute(
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
			else if (cmd.equals(Constants.ADD_TEMP)) {
				_addTempAttachment(actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteAttachment(actionRequest, false);
			}
			else if (cmd.equals(Constants.DELETE_TEMP)) {
				_deleteTempAttachment(actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.EMPTY_TRASH)) {
				emptyTrash(actionRequest);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteAttachment(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				restoreEntries(actionRequest);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				if (Validator.isNotNull(redirect)) {
					actionResponse.sendRedirect(redirect);
				}
			}
		}
		catch (NoSuchNodeException | NoSuchPageException | PrincipalException
					exception) {

			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/wiki/error.jsp");
		}
		catch (Exception exception) {
			_handleUploadException(
				actionRequest, actionResponse, cmd, exception);
		}
	}

	private void _addTempAttachment(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_tempAttachmentWikiUploadFileEntryHandler, _uploadResponseHandler,
			actionRequest, actionResponse);
	}

	private void _deleteAttachment(
			ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		TrashedModel trashedModel = deleteAttachment(
			actionRequest, moveToTrash);

		if (moveToTrash && (trashedModel != null)) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					Constants.CMD, Constants.REMOVE
				).put(
					"trashedModels", ListUtil.fromArray(trashedModel)
				).build());
		}
	}

	private void _deleteTempAttachment(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String fileName = ParamUtil.getString(actionRequest, "fileName");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_wikiPageService.deleteTempFileEntry(
				nodeId, WikiConstants.TEMP_FOLDER_NAME, fileName);

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

	/**
	 * TODO: Remove. This should extend from EditFileEntryAction once it is
	 * modularized.
	 */
	private String[] _getAllowedFileExtensions() throws Exception {
		return _dlConfiguration.fileExtensions();
	}

	/**
	 * TODO: Remove. This should extend from EditFileEntryAction once it is
	 * modularized.
	 */
	private void _handleUploadException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String cmd, Exception exception)
		throws Exception {

		if (exception instanceof AssetCategoryException ||
			exception instanceof AssetTagException) {

			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}
		else if (exception instanceof AntivirusScannerException ||
				 exception instanceof DuplicateFileEntryException ||
				 exception instanceof DuplicateFolderNameException ||
				 exception instanceof FileExtensionException ||
				 exception instanceof FileMimeTypeException ||
				 exception instanceof FileNameException ||
				 exception instanceof FileSizeException ||
				 exception instanceof LiferayFileItemException ||
				 exception instanceof NoSuchFolderException ||
				 exception instanceof SourceFileNameException ||
				 exception instanceof UploadRequestSizeException) {

			if (!cmd.equals(Constants.ADD_DYNAMIC) &&
				!cmd.equals(Constants.ADD_MULTIPLE) &&
				!cmd.equals(Constants.ADD_TEMP)) {

				if (exception instanceof AntivirusScannerException) {
					SessionErrors.add(
						actionRequest, exception.getClass(), exception);
				}
				else {
					SessionErrors.add(actionRequest, exception.getClass());
				}

				return;
			}
			else if (cmd.equals(Constants.ADD_TEMP)) {
				hideDefaultErrorMessage(actionRequest);
			}

			if (exception instanceof AntivirusScannerException ||
				exception instanceof DuplicateFileEntryException ||
				exception instanceof FileExtensionException ||
				exception instanceof FileNameException ||
				exception instanceof FileSizeException ||
				exception instanceof UploadRequestSizeException) {

				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(actionResponse);

				httpServletResponse.setContentType(ContentTypes.TEXT_HTML);
				httpServletResponse.setStatus(HttpServletResponse.SC_OK);

				String errorMessage = StringPool.BLANK;
				int errorType = 0;

				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				if (exception instanceof AntivirusScannerException) {
					AntivirusScannerException antivirusScannerException =
						(AntivirusScannerException)exception;

					errorMessage = themeDisplay.translate(
						antivirusScannerException.getMessageKey());

					errorType =
						ServletResponseConstants.SC_FILE_ANTIVIRUS_EXCEPTION;
				}

				if (exception instanceof DuplicateFileEntryException) {
					errorMessage = themeDisplay.translate(
						"please-enter-a-unique-document-name");
					errorType =
						ServletResponseConstants.SC_DUPLICATE_FILE_EXCEPTION;
				}
				else if (exception instanceof FileExtensionException) {
					errorMessage = themeDisplay.translate(
						"please-enter-a-file-with-a-valid-extension-x",
						StringUtil.merge(_getAllowedFileExtensions()));
					errorType =
						ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION;
				}
				else if (exception instanceof FileNameException) {
					errorMessage = themeDisplay.translate(
						"please-enter-a-file-with-a-valid-file-name");
					errorType = ServletResponseConstants.SC_FILE_NAME_EXCEPTION;
				}
				else if (exception instanceof FileSizeException) {
					FileSizeException fileSizeException =
						(FileSizeException)exception;

					errorMessage = themeDisplay.translate(
						"please-enter-a-file-with-a-valid-file-size-no-" +
							"larger-than-x",
						_language.formatStorageSize(
							fileSizeException.getMaxSize(),
							themeDisplay.getLocale()));

					errorType = ServletResponseConstants.SC_FILE_SIZE_EXCEPTION;
				}

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"message", errorMessage
					).put(
						"status", errorType
					));
			}

			if (exception instanceof AntivirusScannerException) {
				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else {
				SessionErrors.add(actionRequest, exception.getClass());
			}
		}
		else if (exception instanceof DuplicateLockException ||
				 exception instanceof InvalidFileVersionException ||
				 exception instanceof NoSuchFileEntryException ||
				 exception instanceof PrincipalException) {

			if (exception instanceof DuplicateLockException) {
				DuplicateLockException duplicateLockException =
					(DuplicateLockException)exception;

				SessionErrors.add(
					actionRequest, duplicateLockException.getClass(),
					duplicateLockException.getLock());
			}
			else {
				SessionErrors.add(actionRequest, exception.getClass());
			}

			actionResponse.setRenderParameter(
				"mvcPath", "/html/porltet/document_library/error.jsp");
		}
		else {
			Throwable throwable = exception.getCause();

			if (throwable instanceof DuplicateFileEntryException) {
				SessionErrors.add(
					actionRequest, DuplicateFileEntryException.class);
			}
			else {
				throw exception;
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditPageAttachmentMVCActionCommand.class);

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private DLValidator _dlValidator;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private final TempAttachmentWikiUploadFileEntryHandler
		_tempAttachmentWikiUploadFileEntryHandler =
			new TempAttachmentWikiUploadFileEntryHandler();

	@Reference
	private UploadHandler _uploadHandler;

	@Reference(target = "(upload.response.handler=multiple)")
	private UploadResponseHandler _uploadResponseHandler;

	@Reference
	private WikiPageService _wikiPageService;

	private class TempAttachmentWikiUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_dlValidator.validateFileSize(
				themeDisplay.getScopeGroupId(),
				uploadPortletRequest.getFileName(_PARAMETER_NAME),
				uploadPortletRequest.getContentType(_PARAMETER_NAME),
				uploadPortletRequest.getSize(_PARAMETER_NAME));

			long nodeId = ParamUtil.getLong(
				uploadPortletRequest.getPortletRequest(), "nodeId");

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					_PARAMETER_NAME)) {

				return _wikiPageService.addTempFileEntry(
					nodeId, WikiConstants.TEMP_FOLDER_NAME,
					TempFileEntryUtil.getTempFileName(
						uploadPortletRequest.getFileName(_PARAMETER_NAME)),
					inputStream,
					uploadPortletRequest.getContentType(_PARAMETER_NAME));
			}
		}

		private static final String _PARAMETER_NAME = "file";

	}

}