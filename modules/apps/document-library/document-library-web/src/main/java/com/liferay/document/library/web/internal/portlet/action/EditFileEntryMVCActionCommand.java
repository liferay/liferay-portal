/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.configuration.DLFileEntryMimeTypeConfiguration;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.exception.DLStorageQuotaExceededException;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.DuplicateFolderNameException;
import com.liferay.document.library.kernel.exception.FileEntryDisplayDateException;
import com.liferay.document.library.kernel.exception.FileEntryExpirationDateException;
import com.liferay.document.library.kernel.exception.FileEntryLockException;
import com.liferay.document.library.kernel.exception.FileEntryReviewDateException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.exception.InvalidFileEntryTypeException;
import com.liferay.document.library.kernel.exception.InvalidFileVersionException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.exception.RequiredFileException;
import com.liferay.document.library.kernel.exception.SourceFileNameException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLTrashService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.web.internal.exception.FileNameExtensionException;
import com.liferay.document.library.web.internal.settings.DLPortletInstanceSettings;
import com.liferay.dynamic.data.mapping.exception.StorageFieldRequiredException;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.capabilities.TrashCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.trash.service.TrashEntryService;
import com.liferay.upload.UploadResponseHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.fileupload.FileUploadBase;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Sergio González
 * @author Manuel de la Peña
 * @author Levente Hudák
 * @author Kenneth Chang
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/edit_file_entry",
		"mvc.command.name=/document_library/upload_multiple_file_entries"
	},
	service = MVCActionCommand.class
)
public class EditFileEntryMVCActionCommand extends BaseMVCActionCommand {

	public static final String TEMP_FOLDER_NAME =
		EditFileEntryMVCActionCommand.class.getName();

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

		FileEntry fileEntry = null;

		PortletConfig portletConfig = getPortletConfig(actionRequest);

		try {
			UploadException uploadException =
				(UploadException)actionRequest.getAttribute(
					WebKeys.UPLOAD_EXCEPTION);

			if (uploadException != null) {
				Throwable throwable = uploadException.getCause();

				if (cmd.equals(Constants.ADD_TEMP)) {
					if (throwable instanceof
							FileUploadBase.IOFileUploadException) {

						if (_log.isInfoEnabled()) {
							_log.info("Temporary upload was cancelled");
						}
					}
				}
				else {
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
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.ADD_DYNAMIC) ||
					 cmd.equals(Constants.UPDATE) ||
					 cmd.equals(Constants.UPDATE_AND_CHECKIN)) {

				UploadPortletRequest uploadPortletRequest =
					_portal.getUploadPortletRequest(actionRequest);

				String sourceFileName = uploadPortletRequest.getFileName(
					"file");

				ServiceContext serviceContext = _createServiceContext(
					uploadPortletRequest);

				try (AutoCloseable autoCloseable = _pushServiceContext(
						serviceContext)) {

					fileEntry = _updateFileEntry(
						portletConfig, actionRequest, actionResponse,
						uploadPortletRequest, serviceContext);
				}
				catch (PortalException portalException) {
					if (!cmd.equals(Constants.ADD_DYNAMIC) &&
						Validator.isNotNull(sourceFileName)) {

						SessionErrors.add(
							actionRequest, RequiredFileException.class);
					}

					throw portalException;
				}
			}
			else if (cmd.equals(Constants.ADD_MULTIPLE)) {
				_addMultipleFileEntries(
					portletConfig, actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.ADD_TEMP)) {
				_addTempFileEntry(actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteFileEntry(actionRequest, false);
			}
			else if (cmd.equals(Constants.DELETE_TEMP)) {
				_deleteTempFileEntry(actionRequest, actionResponse);
			}
			else if (cmd.equals(Constants.CANCEL_CHECKOUT)) {
				_cancelFileEntriesCheckOut(actionRequest);
			}
			else if (cmd.equals(Constants.CHECKIN)) {
				_checkInFileEntries(actionRequest);
			}
			else if (cmd.equals(Constants.CHECKOUT)) {
				_checkOutFileEntries(actionRequest);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteFileEntry(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				_restoreTrashEntries(actionRequest);
			}
			else if (cmd.equals(Constants.REVERT)) {
				_revertFileEntry(actionRequest);
			}

			if (cmd.equals(Constants.ADD_TEMP) ||
				cmd.equals(Constants.DELETE_TEMP)) {

				hideDefaultSuccessMessage(actionRequest);

				actionResponse.setRenderParameter("mvcPath", "/null.jsp");
			}
			else if (cmd.equals(Constants.PREVIEW)) {
				SessionMessages.add(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.KEY_SUFFIX_FORCE_SEND_REDIRECT);

				hideDefaultSuccessMessage(actionRequest);

				actionResponse.setRenderParameter(
					"mvcRenderCommandName",
					"/document_library/edit_file_entry");
			}
			else {
				String redirect = ParamUtil.getString(
					actionRequest, "redirect");
				int workflowAction = ParamUtil.getInteger(
					actionRequest, "workflowAction",
					WorkflowConstants.ACTION_SAVE_DRAFT);

				if ((fileEntry != null) &&
					(workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT)) {

					redirect = _getSaveAndContinueRedirect(
						portletConfig, actionRequest, fileEntry, redirect);

					sendRedirect(actionRequest, actionResponse, redirect);
				}
				else {
					redirect = ParamUtil.getString(actionRequest, "redirect");

					if (Validator.isNotNull(redirect)) {
						String portletResource =
							HttpComponentsUtil.getParameter(
								redirect, "portletResource", false);

						if (cmd.equals(Constants.ADD) && (fileEntry != null)) {
							String namespace = _portal.getPortletNamespace(
								portletResource);

							if (Validator.isNotNull(portletResource)) {
								redirect = HttpComponentsUtil.addParameter(
									redirect, namespace + "className",
									DLFileEntry.class.getName());
								redirect = HttpComponentsUtil.addParameter(
									redirect, namespace + "classPK",
									fileEntry.getFileEntryId());
							}
						}

						if (Validator.isNotNull(portletResource) ||
							cmd.equals(Constants.ADD_DYNAMIC)) {

							hideDefaultSuccessMessage(actionRequest);
						}

						sendRedirect(
							actionRequest, actionResponse,
							_portal.escapeRedirect(redirect));
					}
				}
			}

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource) ||
				cmd.equals(Constants.ADD_DYNAMIC)) {

				hideDefaultSuccessMessage(actionRequest);
			}
		}
		catch (Exception exception) {
			_handleUploadException(
				actionRequest, actionResponse, cmd, exception);
		}
	}

	private void _addMultipleFileEntries(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException, PortalException {

		List<KeyValuePair> validFileNameKVPs = new ArrayList<>();
		List<KeyValuePair> invalidFileNameKVPs = new ArrayList<>();

		String[] selectedFileNames = ParamUtil.getParameterValues(
			actionRequest, "selectedFileName", new String[0], false);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DLFileEntry.class.getName(), actionRequest);

		_setUpDDMFormValues(serviceContext);

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		long repositoryId = ParamUtil.getLong(
			uploadPortletRequest, "repositoryId");

		boolean neverExpireDefaultValue = false;

		if (RepositoryUtil.isExternalRepository(repositoryId)) {
			neverExpireDefaultValue = true;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = _userLocalService.getUser(themeDisplay.getUserId());

		for (String selectedFileName : selectedFileNames) {
			_addMultipleFileEntries(
				portletConfig, actionRequest, selectedFileName,
				validFileNameKVPs, invalidFileNameKVPs, neverExpireDefaultValue,
				user, uploadPortletRequest, serviceContext);
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (KeyValuePair validFileNameKVP : validFileNameKVPs) {
			jsonArray.put(
				JSONUtil.put(
					"added", Boolean.TRUE
				).put(
					"fileName", validFileNameKVP.getKey()
				).put(
					"originalFileName", validFileNameKVP.getValue()
				));
		}

		for (KeyValuePair invalidFileNameKVP : invalidFileNameKVPs) {
			String fileName = invalidFileNameKVP.getKey();
			String errorMessage = invalidFileNameKVP.getValue();

			jsonArray.put(
				JSONUtil.put(
					"added", Boolean.FALSE
				).put(
					"errorMessage", errorMessage
				).put(
					"fileName", fileName
				).put(
					"originalFileName", fileName
				));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonArray);
	}

	private void _addMultipleFileEntries(
			PortletConfig portletConfig, ActionRequest actionRequest,
			String selectedFileName, List<KeyValuePair> validFileNameKVPs,
			List<KeyValuePair> invalidFileNameKVPs,
			Boolean neverExpireDefaultValue, User user,
			UploadPortletRequest uploadPortletRequest,
			ServiceContext serviceContext)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long repositoryId = ParamUtil.getLong(actionRequest, "repositoryId");
		long folderId = ParamUtil.getLong(actionRequest, "folderId");
		String description = ParamUtil.getString(actionRequest, "description");
		String changeLog = ParamUtil.getString(actionRequest, "changeLog");

		FileEntry tempFileEntry = null;

		try {
			tempFileEntry = TempFileEntryUtil.getTempFileEntry(
				themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
				TEMP_FOLDER_NAME, selectedFileName);

			String originalSelectedFileName =
				TempFileEntryUtil.getOriginalTempFileName(
					tempFileEntry.getFileName());

			String uniqueFileName = DLUtil.getUniqueFileName(
				tempFileEntry.getGroupId(), folderId, originalSelectedFileName,
				true);

			String uniqueFileTitle = DLUtil.getUniqueTitle(
				tempFileEntry.getGroupId(), folderId,
				FileUtil.stripExtension(originalSelectedFileName));

			Date displayDate = _getDisplayDate(
				uploadPortletRequest, neverExpireDefaultValue,
				user.getTimeZone());

			_dlAppService.addFileEntry(
				null, repositoryId, folderId, uniqueFileName,
				tempFileEntry.getMimeType(), uniqueFileTitle, StringPool.BLANK,
				description, changeLog, tempFileEntry.getContentStream(),
				tempFileEntry.getSize(), displayDate,
				_getExpirationDate(
					uploadPortletRequest, displayDate, neverExpireDefaultValue,
					user.getTimeZone()),
				_getReviewDate(
					uploadPortletRequest, neverExpireDefaultValue,
					user.getTimeZone()),
				serviceContext);

			validFileNameKVPs.add(
				new KeyValuePair(uniqueFileName, selectedFileName));
		}
		catch (Exception exception) {
			String errorMessage = _getAddMultipleFileEntriesErrorMessage(
				portletConfig, actionRequest, exception);

			if (_log.isDebugEnabled()) {
				_log.debug(errorMessage, exception);
			}

			invalidFileNameKVPs.add(
				new KeyValuePair(selectedFileName, errorMessage));
		}
		finally {
			if (tempFileEntry != null) {
				TempFileEntryUtil.deleteTempFileEntry(
					tempFileEntry.getFileEntryId());
			}
		}
	}

	private void _addPublishedDocumentMessage(
		ActionRequest actionRequest, FileVersion fileVersion,
		ThemeDisplay themeDisplay) {

		if (!fileVersion.isScheduled()) {
			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}

			return;
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			themeDisplay.getLocale(), themeDisplay.getTimeZone());

		SessionMessages.add(
			httpServletRequest, "scheduledDocument_requestProcessedSuccess",
			_language.format(
				_portal.getHttpServletRequest(actionRequest),
				"x-will-be-published-on-x",
				new Object[] {
					"<strong>" + HtmlUtil.escape(fileVersion.getTitle()) +
						"</strong>",
					dateTimeFormat.format(fileVersion.getDisplayDate())
				}));

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _addTempFileEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortalException {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long folderId = ParamUtil.getLong(uploadPortletRequest, "folderId");
		String sourceFileName = uploadPortletRequest.getFileName("file");

		try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
				"file")) {

			String tempFileName = TempFileEntryUtil.getTempFileName(
				sourceFileName);

			String mimeType = uploadPortletRequest.getContentType("file");

			FileEntry fileEntry = _dlAppService.addTempFileEntry(
				themeDisplay.getScopeGroupId(), folderId, TEMP_FOLDER_NAME,
				tempFileName, inputStream, mimeType);

			JSONObject jsonObject = _multipleUploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
	}

	private void _addUrlTitleChangedMessage(
		ActionRequest actionRequest, String originalUrlTitle,
		long fileEntryId) {

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_classNameLocalService.getClassNameId(FileEntry.class),
				fileEntryId);

		if (friendlyURLEntry == null) {
			return;
		}

		String currentUrlTitle = friendlyURLEntry.getUrlTitle();

		if (Validator.isNull(originalUrlTitle) ||
			currentUrlTitle.equals(
				_friendlyURLNormalizer.normalizeWithPeriodsAndSlashes(
					originalUrlTitle))) {

			return;
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		SessionMessages.add(
			httpServletRequest, "friendlyURLChanged_requestProcessedWarning",
			_language.format(
				httpServletRequest,
				"the-friendly-url-x-was-changed-to-x-to-ensure-uniqueness",
				new Object[] {
					"<strong>" + HtmlUtil.escapeURL(originalUrlTitle) +
						"</strong>",
					"<strong>" + currentUrlTitle + "</strong>"
				}));
	}

	private void _cancelFileEntriesCheckOut(ActionRequest actionRequest)
		throws PortalException {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			_dlAppService.cancelCheckOut(fileEntryId);
		}
		else {
			long[] fileEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIdsFileEntry");

			for (long curFileEntryId : fileEntryIds) {
				_dlAppService.cancelCheckOut(curFileEntryId);
			}
		}
	}

	private void _checkInFileEntries(ActionRequest actionRequest)
		throws PortalException {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		DLVersionNumberIncrease dlVersionNumberIncrease =
			DLVersionNumberIncrease.valueOf(
				actionRequest.getParameter("versionIncrease"),
				DLVersionNumberIncrease.AUTOMATIC);

		String changeLog = ParamUtil.getString(actionRequest, "changeLog");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		if (fileEntryId > 0) {
			_dlAppService.checkInFileEntry(
				fileEntryId, dlVersionNumberIncrease, changeLog,
				serviceContext);
		}
		else {
			long[] fileEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIdsFileEntry");

			for (long curFileEntryId : fileEntryIds) {
				_dlAppService.checkInFileEntry(
					curFileEntryId, dlVersionNumberIncrease, changeLog,
					serviceContext);
			}
		}
	}

	private void _checkOutFileEntries(ActionRequest actionRequest)
		throws PortalException {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		if (fileEntryId > 0) {
			_dlAppService.checkOutFileEntry(fileEntryId, serviceContext);
		}
		else {
			long[] fileEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIdsFileEntry");

			for (long curFileEntryId : fileEntryIds) {
				_dlAppService.checkOutFileEntry(curFileEntryId, serviceContext);
			}
		}
	}

	private ServiceContext _createServiceContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DLFileEntry.class.getName(), httpServletRequest);

		_setUpDDMFormValues(serviceContext);

		serviceContext.setAttribute(
			"updateAutoTags",
			ParamUtil.getBoolean(httpServletRequest, "updateAutoTags"));

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		if (!cmd.equals(Constants.ADD_DYNAMIC)) {
			return serviceContext;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();
		Group group = themeDisplay.getScopeGroup();

		if (layout.isPublicLayout() ||
			(layout.isTypeControlPanel() && !group.hasPrivateLayouts())) {

			return serviceContext;
		}

		ModelPermissions modelPermissions =
			serviceContext.getModelPermissions();

		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(
				modelPermissions.getActionIds(
					RoleConstants.PLACEHOLDER_DEFAULT_GROUP_ROLE),
				_RESTRICTED_GUEST_PERMISSIONS, DLFileEntry.class.getName()));

		return serviceContext;
	}

	private void _deleteFileEntry(
			ActionRequest actionRequest, boolean moveToTrash)
		throws PortalException {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId == 0) {
			return;
		}

		String version = ParamUtil.getString(actionRequest, "version");

		if (Validator.isNotNull(version)) {
			_dlAppService.deleteFileVersion(fileEntryId, version);

			return;
		}

		if (!moveToTrash) {
			_dlAppService.deleteFileEntry(fileEntryId);

			return;
		}

		FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

		if (!fileEntry.isRepositoryCapabilityProvided(TrashCapability.class)) {
			hideDefaultSuccessMessage(actionRequest);

			return;
		}

		fileEntry = _dlTrashService.moveFileEntryToTrash(fileEntryId);

		addDeleteSuccessData(
			actionRequest,
			HashMapBuilder.<String, Object>put(
				"trashedModels",
				ListUtil.fromArray((TrashedModel)fileEntry.getModel())
			).build());
	}

	private void _deleteTempFileEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long folderId = ParamUtil.getLong(actionRequest, "folderId");
		String fileName = ParamUtil.getString(actionRequest, "fileName");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			_dlAppService.deleteTempFileEntry(
				themeDisplay.getScopeGroupId(), folderId, TEMP_FOLDER_NAME,
				fileName);

			jsonObject.put("deleted", Boolean.TRUE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			String errorMessage = _language.get(
				themeDisplay.getLocale(),
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

	private String _getAddMultipleFileEntriesErrorMessage(
			PortletConfig portletConfig, ActionRequest actionRequest,
			Exception exception)
		throws PortalException {

		String errorMessage = null;

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (exception instanceof AntivirusScannerException) {
			AntivirusScannerException antivirusScannerException =
				(AntivirusScannerException)exception;

			errorMessage = _language.get(
				themeDisplay.getLocale(),
				antivirusScannerException.getMessageKey());
		}
		else if (exception instanceof AssetCategoryException) {
			AssetCategoryException assetCategoryException =
				(AssetCategoryException)exception;

			AssetVocabulary assetVocabulary =
				assetCategoryException.getVocabulary();

			String vocabularyTitle = StringPool.BLANK;

			if (assetVocabulary != null) {
				vocabularyTitle = assetVocabulary.getTitle(
					themeDisplay.getLocale());
			}

			if (assetCategoryException.getType() ==
					AssetCategoryException.AT_LEAST_ONE_CATEGORY) {

				errorMessage = _language.format(
					themeDisplay.getLocale(),
					"please-select-at-least-one-category-for-x",
					vocabularyTitle);
			}
			else if (assetCategoryException.getType() ==
						AssetCategoryException.TOO_MANY_CATEGORIES) {

				errorMessage = _language.format(
					themeDisplay.getLocale(),
					"you-cannot-select-more-than-one-category-for-x",
					vocabularyTitle);
			}
		}
		else if (exception instanceof DDMFormValuesValidationException) {
			errorMessage = _language.get(
				_portal.getHttpServletRequest(actionRequest),
				"field-validation-failed");
		}
		else if (exception instanceof DLStorageQuotaExceededException) {
			errorMessage = _language.format(
				themeDisplay.getLocale(),
				"you-have-exceeded-the-x-storage-quota-for-this-instance",
				_language.formatStorageSize(
					PropsValues.DATA_LIMIT_DL_STORAGE_MAX_SIZE,
					themeDisplay.getLocale()));
		}
		else if (exception instanceof DuplicateFileEntryException) {
			errorMessage = _language.get(
				themeDisplay.getLocale(),
				"the-folder-you-selected-already-has-an-entry-with-this-" +
					"name.-please-select-a-different-folder");
		}
		else if (exception instanceof FileEntryDisplayDateException) {
			errorMessage = _language.get(
				_portal.getHttpServletRequest(actionRequest),
				"please-enter-a-valid-publish-date");
		}
		else if (exception instanceof FileEntryExpirationDateException) {
			errorMessage = _language.get(
				_portal.getHttpServletRequest(actionRequest),
				"please-enter-a-valid-expiration-date");
		}
		else if (exception instanceof FileEntryReviewDateException) {
			errorMessage = _language.get(
				_portal.getHttpServletRequest(actionRequest),
				"please-enter-a-valid-review-date");
		}
		else if (exception instanceof FileExtensionException) {
			errorMessage = _language.format(
				themeDisplay.getLocale(),
				"please-enter-a-file-with-a-valid-extension-x",
				StringUtil.merge(
					_getAllowedFileExtensions(portletConfig, actionRequest)));
		}
		else if (exception instanceof FileMimeTypeException) {
			DLFileEntryMimeTypeConfiguration dlFileEntryMimeTypeConfiguration =
				_configurationProvider.getCompanyConfiguration(
					DLFileEntryMimeTypeConfiguration.class,
					themeDisplay.getCompanyId());

			errorMessage = _language.format(
				themeDisplay.getLocale(),
				"please-enter-a-file-with-a-valid-mime-type-x",
				StringUtil.merge(
					dlFileEntryMimeTypeConfiguration.fileMimeTypes(),
					StringPool.COMMA_AND_SPACE));
		}
		else if (exception instanceof FileNameException) {
			errorMessage = _language.get(
				themeDisplay.getLocale(),
				"please-enter-a-file-with-a-valid-file-name");
		}
		else if (exception instanceof FileSizeException) {
			FileSizeException fileSizeException = (FileSizeException)exception;

			errorMessage = _language.format(
				themeDisplay.getLocale(),
				"please-enter-a-file-with-a-valid-file-size-no-larger-than-x",
				_language.formatStorageSize(
					fileSizeException.getMaxSize(), themeDisplay.getLocale()));
		}
		else if (exception instanceof InvalidFileEntryTypeException) {
			errorMessage = _language.get(
				_portal.getHttpServletRequest(actionRequest),
				"the-document-type-you-selected-is-not-valid-for-this-folder");
		}
		else {
			errorMessage = _language.get(
				_portal.getHttpServletRequest(actionRequest),
				"an-unexpected-error-occurred-while-saving-your-document");
		}

		return errorMessage;
	}

	private String[] _getAllowedFileExtensions(
			PortletConfig portletConfig, PortletRequest portletRequest)
		throws PortalException {

		String portletName = portletConfig.getPortletName();

		if (!portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
			return _dlConfiguration.fileExtensions();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		DLPortletInstanceSettings dlPortletInstanceSettings =
			DLPortletInstanceSettings.getInstance(
				themeDisplay.getLayout(), portletDisplay.getId());

		Set<String> extensions = new HashSet<>();

		String[] mimeTypes = dlPortletInstanceSettings.getMimeTypes();

		for (String mimeType : mimeTypes) {
			extensions.addAll(MimeTypesUtil.getExtensions(mimeType));
		}

		return extensions.toArray(new String[0]);
	}

	private HttpServletRequest _getDDMStructureHttpServletRequest(
		HttpServletRequest httpServletRequest, long structureId) {

		DynamicServletRequest dynamicServletRequest = new DynamicServletRequest(
			httpServletRequest, new HashMap<>());

		String namespace = String.valueOf(structureId) + StringPool.UNDERLINE;

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String parameterName = entry.getKey();

			if (StringUtil.startsWith(parameterName, namespace)) {
				dynamicServletRequest.setParameterValues(
					parameterName.substring(namespace.length()),
					entry.getValue());
			}
		}

		return dynamicServletRequest;
	}

	private Date _getDisplayDate(
			UploadPortletRequest uploadPortletRequest, boolean addDynamic,
			TimeZone timeZone)
		throws PortalException {

		if (addDynamic || !PropsValues.SCHEDULER_ENABLED) {
			return null;
		}

		int displayDateMonth = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateMonth");
		int displayDateDay = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateDay");
		int displayDateYear = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateYear");
		int displayDateHour = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateHour");
		int displayDateMinute = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateMinute");
		int displayDateAmPm = ParamUtil.getInteger(
			uploadPortletRequest, "displayDateAmPm");

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		return _portal.getDate(
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, timeZone, FileEntryDisplayDateException.class);
	}

	private Date _getExpirationDate(
			UploadPortletRequest uploadPortletRequest, Date displayDate,
			boolean neverExpireDefaultValue, TimeZone timeZone)
		throws PortalException {

		boolean neverExpire = ParamUtil.getBoolean(
			uploadPortletRequest, "neverExpire", neverExpireDefaultValue);

		if (!PropsValues.SCHEDULER_ENABLED || neverExpire) {
			return null;
		}

		int expirationDateMonth = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateMonth");
		int expirationDateDay = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateDay");
		int expirationDateYear = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateYear");
		int expirationDateHour = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateHour");
		int expirationDateMinute = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateMinute");
		int expirationDateAmPm = ParamUtil.getInteger(
			uploadPortletRequest, "expirationDateAmPm");

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		Date expirationDate = _portal.getDate(
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, timeZone,
			FileEntryExpirationDateException.class);

		if ((expirationDate != null) && expirationDate.before(new Date())) {
			throw new FileEntryExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}

		if ((displayDate != null) && (expirationDate != null) &&
			displayDate.after(expirationDate)) {

			throw new FileEntryExpirationDateException(
				StringBundler.concat(
					"Expiration date ", expirationDate,
					" is prior to display date ", displayDate));
		}

		return expirationDate;
	}

	private Date _getReviewDate(
			UploadPortletRequest uploadPortletRequest,
			boolean neverReviewDefaultValue, TimeZone timeZone)
		throws PortalException {

		boolean neverReview = ParamUtil.getBoolean(
			uploadPortletRequest, "neverReview", neverReviewDefaultValue);

		if (!PropsValues.SCHEDULER_ENABLED || neverReview) {
			return null;
		}

		int reviewDateMonth = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateMonth");
		int reviewDateDay = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateDay");
		int reviewDateYear = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateYear");
		int reviewDateHour = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateHour");
		int reviewDateMinute = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateMinute");
		int reviewDateAmPm = ParamUtil.getInteger(
			uploadPortletRequest, "reviewDateAmPm");

		if (reviewDateAmPm == Calendar.PM) {
			reviewDateHour += 12;
		}

		return _portal.getDate(
			reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
			reviewDateMinute, timeZone, FileEntryReviewDateException.class);
	}

	private String _getSaveAndContinueRedirect(
			PortletConfig portletConfig, ActionRequest actionRequest,
			FileEntry fileEntry, String redirect)
		throws PortletException {

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, portletConfig.getPortletName(),
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcRenderCommandName", "/document_library/edit_file_entry");
		portletURL.setParameter(Constants.CMD, Constants.UPDATE, false);
		portletURL.setParameter("redirect", redirect, false);
		portletURL.setParameter(
			"groupId", String.valueOf(fileEntry.getGroupId()), false);
		portletURL.setParameter(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId()), false);
		portletURL.setParameter(
			"version", String.valueOf(fileEntry.getVersion()), false);
		portletURL.setWindowState(actionRequest.getWindowState());

		return portletURL.toString();
	}

	private void _handleUploadException(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String cmd, Exception exception)
		throws Exception {

		if (_log.isWarnEnabled()) {
			_log.warn(exception);
		}

		if (exception instanceof AssetCategoryException ||
			exception instanceof AssetTagException) {

			SessionErrors.add(actionRequest, exception.getClass(), exception);
		}
		else if (exception instanceof AntivirusScannerException ||
				 exception instanceof DDMFormValuesValidationException ||
				 exception instanceof DLStorageQuotaExceededException ||
				 exception instanceof DuplicateFileEntryException ||
				 exception instanceof DuplicateFolderNameException ||
				 exception instanceof FileExtensionException ||
				 exception instanceof FileMimeTypeException ||
				 exception instanceof FileNameException ||
				 exception instanceof FileSizeException ||
				 exception instanceof LiferayFileItemException ||
				 exception instanceof NoSuchFolderException ||
				 exception instanceof SourceFileNameException ||
				 exception instanceof StorageFieldRequiredException ||
				 exception instanceof UploadRequestSizeException) {

			if (!cmd.equals(Constants.ADD_DYNAMIC) &&
				!cmd.equals(Constants.ADD_MULTIPLE) &&
				!cmd.equals(Constants.ADD_TEMP)) {

				if (exception instanceof AntivirusScannerException ||
					exception instanceof FileSizeException) {

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
				exception instanceof DDMFormValuesValidationException ||
				exception instanceof DLStorageQuotaExceededException ||
				exception instanceof DuplicateFileEntryException ||
				exception instanceof FileExtensionException ||
				exception instanceof FileMimeTypeException ||
				exception instanceof FileNameException ||
				exception instanceof FileSizeException ||
				exception instanceof UploadRequestSizeException) {

				JSONObject jsonObject =
					_multipleUploadResponseHandler.onFailure(
						actionRequest, (PortalException)exception);

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse, jsonObject);

				if (cmd.equals(Constants.ADD_DYNAMIC)) {
					hideDefaultErrorMessage(actionRequest);
				}
			}

			if (exception instanceof AntivirusScannerException ||
				exception instanceof FileSizeException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else {
				SessionErrors.add(actionRequest, exception.getClass());
			}
		}
		else if (exception instanceof DuplicateLockException ||
				 exception instanceof FileEntryLockException.MustOwnLock ||
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
				"mvcPath", "/document_library/error.jsp");
		}
		else {
			SessionErrors.add(actionRequest, exception.getClass());

			if (cmd.equals(Constants.ADD_DYNAMIC)) {
				JSONObject jsonObject;

				if (exception instanceof PortalException) {
					jsonObject = _multipleUploadResponseHandler.onFailure(
						actionRequest, (PortalException)exception);
				}
				else {
					jsonObject = _multipleUploadResponseHandler.onFailure(
						actionRequest, new PortalException(exception));
				}

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse, jsonObject);

				hideDefaultErrorMessage(actionRequest);
			}

			throw exception;
		}
	}

	private AutoCloseable _pushServiceContext(ServiceContext serviceContext) {
		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return ServiceContextThreadLocal::popServiceContext;
	}

	private void _restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long[] restoreTrashEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreTrashEntryId : restoreTrashEntryIds) {
			_trashEntryService.restoreEntry(restoreTrashEntryId);
		}
	}

	private void _revertFileEntry(ActionRequest actionRequest)
		throws Exception {

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");
		String version = ParamUtil.getString(actionRequest, "version");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DLFileEntry.class.getName(), actionRequest);

		_dlAppService.revertFileEntry(fileEntryId, version, serviceContext);
	}

	private void _setUpDDMFormValues(ServiceContext serviceContext)
		throws PortalException {

		long fileEntryTypeId = ParamUtil.getLong(
			serviceContext, "fileEntryTypeId", -1);

		if (fileEntryTypeId == -1) {
			return;
		}

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(fileEntryTypeId);

		for (DDMStructure ddmStructure :
				DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType)) {

			String className =
				com.liferay.dynamic.data.mapping.kernel.DDMFormValues.class.
					getName();

			DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(
				_getDDMStructureHttpServletRequest(
					serviceContext.getRequest(), ddmStructure.getStructureId()),
				ddmStructure.getDDMForm());

			serviceContext.setAttribute(
				className + StringPool.POUND + ddmStructure.getStructureId(),
				_ddmBeanTranslator.translate(ddmFormValues));
		}
	}

	private FileEntry _updateFileEntry(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse,
			UploadPortletRequest uploadPortletRequest,
			ServiceContext serviceContext)
		throws IOException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String cmd = ParamUtil.getString(uploadPortletRequest, Constants.CMD);

		long fileEntryId = ParamUtil.getLong(
			uploadPortletRequest, "fileEntryId");

		long repositoryId = ParamUtil.getLong(
			uploadPortletRequest, "repositoryId");

		long folderId = ParamUtil.getLong(uploadPortletRequest, "folderId");

		if (ParamUtil.getLong(uploadPortletRequest, "newFolderId") > 0) {
			folderId = ParamUtil.getLong(uploadPortletRequest, "newFolderId");
		}

		String sourceFileName = ParamUtil.getString(
			uploadPortletRequest, "fileName",
			uploadPortletRequest.getFileName("file"));
		String title = ParamUtil.getString(uploadPortletRequest, "title");

		String urlTitle = ParamUtil.getString(uploadPortletRequest, "urlTitle");

		String description = ParamUtil.getString(
			uploadPortletRequest, "description");
		String changeLog = ParamUtil.getString(
			uploadPortletRequest, "changeLog");

		DLVersionNumberIncrease dlVersionNumberIncrease =
			DLVersionNumberIncrease.valueOf(
				uploadPortletRequest.getParameter("versionIncrease"),
				DLVersionNumberIncrease.AUTOMATIC);

		boolean updateVersionDetails = ParamUtil.getBoolean(
			uploadPortletRequest, "updateVersionDetails");

		if (!updateVersionDetails) {
			dlVersionNumberIncrease = DLVersionNumberIncrease.AUTOMATIC;
		}

		if (cmd.equals(Constants.ADD_DYNAMIC)) {
			title = uploadPortletRequest.getFileName("file");
		}

		try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
				"file")) {

			String contentType = uploadPortletRequest.getContentType("file");
			long size = uploadPortletRequest.getSize("file");

			if ((cmd.equals(Constants.ADD) ||
				 cmd.equals(Constants.ADD_DYNAMIC)) &&
				(size == 0)) {

				contentType = MimeTypesUtil.getContentType(sourceFileName);

				if (Validator.isNotNull(contentType)) {
					contentType = MimeTypesUtil.getContentType(title);
				}
			}

			contentType = ParamUtil.getString(
				uploadPortletRequest, "contentType", contentType);

			if (cmd.equals(Constants.ADD) ||
				cmd.equals(Constants.ADD_DYNAMIC) || (size > 0)) {

				String portletName = portletConfig.getPortletName();

				if (portletName.equals(DLPortletKeys.MEDIA_GALLERY_DISPLAY)) {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					DLPortletInstanceSettings dlPortletInstanceSettings =
						DLPortletInstanceSettings.getInstance(
							themeDisplay.getLayout(), portletDisplay.getId());

					int count = Arrays.binarySearch(
						dlPortletInstanceSettings.getMimeTypes(), contentType);

					if (count < 0) {
						throw new FileMimeTypeException(contentType);
					}
				}
			}

			User user = _userLocalService.getUser(themeDisplay.getUserId());

			boolean addDynamic = false;

			if (cmd.equals(Constants.ADD_DYNAMIC) ||
				RepositoryUtil.isExternalRepository(repositoryId)) {

				addDynamic = true;
			}

			Date displayDate = _getDisplayDate(
				uploadPortletRequest, addDynamic, user.getTimeZone());

			Date expirationDate = _getExpirationDate(
				uploadPortletRequest, displayDate, addDynamic,
				user.getTimeZone());

			Date reviewDate = _getReviewDate(
				uploadPortletRequest, addDynamic, user.getTimeZone());

			FileEntry fileEntry = null;

			if (cmd.equals(Constants.ADD)) {

				// Add file entry

				_validateFileName(
					sourceFileName,
					FileUtil.getExtension(
						uploadPortletRequest.getFileName("file")));

				fileEntry = _dlAppService.addFileEntry(
					null, repositoryId, folderId, sourceFileName, contentType,
					title, urlTitle, description, changeLog, inputStream, size,
					displayDate, expirationDate, reviewDate, serviceContext);
			}
			else if (cmd.equals(Constants.ADD_DYNAMIC)) {

				// Add file entry

				String uniqueFileName = DLUtil.getUniqueFileName(
					themeDisplay.getScopeGroupId(), folderId, sourceFileName,
					true);

				String uniqueFileTitle = DLUtil.getUniqueTitle(
					themeDisplay.getScopeGroupId(), folderId,
					FileUtil.stripExtension(sourceFileName));

				fileEntry = _dlAppService.addFileEntry(
					null, repositoryId, folderId, uniqueFileName, contentType,
					uniqueFileTitle, StringPool.BLANK, description, changeLog,
					inputStream, size, displayDate, expirationDate, reviewDate,
					serviceContext);

				JSONObject jsonObject = JSONUtil.put(
					"fileEntryId", fileEntry.getFileEntryId());

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse, jsonObject);
			}
			else {
				fileEntry = _dlAppService.getFileEntry(fileEntryId);

				if (Validator.isNotNull(
						uploadPortletRequest.getFileName("file"))) {

					_validateFileName(
						sourceFileName,
						FileUtil.getExtension(
							uploadPortletRequest.getFileName("file")));
				}
				else {
					_validateFileName(sourceFileName, fileEntry.getExtension());
				}

				if (cmd.equals(Constants.UPDATE_AND_CHECKIN)) {

					// Update file entry and checkin

					fileEntry = _dlAppService.updateFileEntryAndCheckIn(
						fileEntryId, sourceFileName, contentType, title,
						urlTitle, description, changeLog,
						dlVersionNumberIncrease, inputStream, size, displayDate,
						expirationDate, reviewDate, serviceContext);
				}
				else {

					// Update file entry

					fileEntry = _dlAppService.updateFileEntry(
						fileEntryId, sourceFileName, contentType, title,
						urlTitle, description, changeLog,
						dlVersionNumberIncrease, inputStream, size, displayDate,
						expirationDate, reviewDate, serviceContext);
				}
			}

			_addPublishedDocumentMessage(
				actionRequest, fileEntry.getLatestFileVersion(), themeDisplay);

			if (Validator.isNotNull(urlTitle)) {
				_addUrlTitleChangedMessage(
					actionRequest, urlTitle, fileEntry.getFileEntryId());
			}

			return fileEntry;
		}
	}

	private void _validateFileName(String sourceFileName, String extension)
		throws PortalException {

		if (Validator.isNotNull(extension) &&
			(Validator.isNull(sourceFileName) ||
			 Validator.isNull(FileUtil.getExtension(sourceFileName)))) {

			throw new FileNameExtensionException(
				"The file name cannot be empty or without extension");
		}

		if (Validator.isNotNull(extension) &&
			!extension.equals(FileUtil.getExtension(sourceFileName))) {

			throw new FileExtensionException.MismatchExtension();
		}
	}

	private static final String[] _RESTRICTED_GUEST_PERMISSIONS = {};

	private static final Log _log = LogFactoryUtil.getLog(
		EditFileEntryMVCActionCommand.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DDMBeanTranslator _ddmBeanTranslator;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DLAppService _dlAppService;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLTrashService _dlTrashService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference(target = "(upload.response.handler=multiple)")
	private UploadResponseHandler _multipleUploadResponseHandler;

	@Reference
	private Portal _portal;

	@Reference
	private TrashEntryService _trashEntryService;

	@Reference
	private UserLocalService _userLocalService;

}