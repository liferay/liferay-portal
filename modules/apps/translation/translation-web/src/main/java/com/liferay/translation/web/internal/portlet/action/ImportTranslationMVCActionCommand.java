/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.portlet.action;

import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.service.TranslationEntryService;
import com.liferay.translation.snapshot.TranslationSnapshot;
import com.liferay.translation.snapshot.TranslationSnapshotProvider;
import com.liferay.translation.url.provider.TranslationURLProvider;
import com.liferay.translation.web.internal.display.context.ImportTranslationResultsDisplayContext;
import com.liferay.translation.web.internal.helper.TranslationRequestHelper;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia Garcia
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TranslationPortletKeys.TRANSLATION,
		"mvc.command.name=/translation/import_translation"
	},
	service = MVCActionCommand.class
)
public class ImportTranslationMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			UploadPortletRequest uploadPortletRequest =
				_portal.getUploadPortletRequest(actionRequest);

			_checkExceededSizeLimit(uploadPortletRequest);

			_checkContentType(uploadPortletRequest.getContentType("file"));

			TranslationRequestHelper translationRequestHelper =
				new TranslationRequestHelper(
					_infoItemServiceRegistry, actionRequest,
					_segmentsExperienceLocalService);
			List<Map<String, String>> failureMessages = new LinkedList<>();
			List<String> successMessages = new ArrayList<>();
			String fileName = uploadPortletRequest.getFileName("file");

			_processUploadedFiles(
				actionRequest, uploadPortletRequest,
				translationRequestHelper.getGroupId(),
				translationRequestHelper.getModelClassName(),
				translationRequestHelper.getModelClassPK(), successMessages,
				failureMessages, themeDisplay.getLocale());

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				hideDefaultSuccessMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}

			String title = ParamUtil.getString(actionRequest, "title");

			actionRequest.setAttribute(
				WebKeys.REDIRECT,
				PortletURLBuilder.createRenderURL(
					_portal.getLiferayPortletResponse(actionResponse)
				).setMVCRenderCommandName(
					"/translation/import_translation_results"
				).setRedirect(
					ParamUtil.getString(actionRequest, "redirect")
				).setParameter(
					"classNameId", translationRequestHelper.getClassNameId()
				).setParameter(
					"classPK", translationRequestHelper.getModelClassPK()
				).setParameter(
					"fileName", fileName
				).setParameter(
					"groupId", translationRequestHelper.getGroupId()
				).setParameter(
					"title", title
				).buildString());

			HttpServletRequest httpServletRequest =
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(actionRequest));

			HttpSession httpSession = httpServletRequest.getSession();

			int workflowAction = ParamUtil.getInteger(
				actionRequest, "workflowAction",
				WorkflowConstants.ACTION_PUBLISH);

			httpSession.setAttribute(
				ImportTranslationResultsDisplayContext.class.getName(),
				new ImportTranslationResultsDisplayContext(
					translationRequestHelper.getClassNameId(),
					translationRequestHelper.getModelClassPK(),
					themeDisplay.getCompanyId(),
					translationRequestHelper.getGroupId(), failureMessages,
					fileName, successMessages, title, workflowAction,
					_workflowDefinitionLinkLocalService));
		}
		catch (Exception exception) {
			try {
				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				actionResponse.sendRedirect(
					PortletURLBuilder.create(
						_translationURLProvider.getImportTranslationURL(
							ParamUtil.getLong(actionRequest, "groupId"),
							ParamUtil.getLong(actionRequest, "classNameId"),
							ParamUtil.getLong(actionRequest, "classPK"),
							RequestBackedPortletURLFactoryUtil.create(
								actionRequest))
					).setRedirect(
						ParamUtil.getString(actionRequest, "redirect")
					).buildString());

				if (exception instanceof XLIFFFileException) {
					hideDefaultErrorMessage(actionRequest);
				}
			}
			catch (PortalException portalException) {
				throw new IOException(portalException);
			}
		}
	}

	private static String _getMustHaveValidIdMessage(String className) {
		if (className.equals(Layout.class.getName())) {
			return "the-translation-file-does-not-correspond-to-this-page";
		}

		return "the-translation-file-does-not-correspond-to-this-web-content";
	}

	private void _checkContentType(String contentType)
		throws XLIFFFileException {

		if (!Objects.equals(ContentTypes.APPLICATION_ZIP, contentType) &&
			!Objects.equals(contentType, "application/x-xliff+xml") &&
			!Objects.equals(contentType, "application/xliff+xml")) {

			throw new XLIFFFileException.MustBeValid(
				"Unsupported content type: " + contentType);
		}
	}

	private void _checkExceededSizeLimit(HttpServletRequest httpServletRequest)
		throws PortalException {

		UploadException uploadException =
			(UploadException)httpServletRequest.getAttribute(
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
	}

	private InfoItemReference _getInfoItemReference(
		String className, long classPK) {

		if (classPK == 0) {
			return null;
		}

		return new InfoItemReference(className, classPK);
	}

	private void _importXLIFFInputStream(
			ActionRequest actionRequest, long groupId, String className,
			long classPK, InputStream inputStream)
		throws IOException, PortalException {

		TranslationSnapshot translationSnapshot =
			_translationSnapshotProvider.getTranslationSnapshot(
				groupId, _getInfoItemReference(className, classPK), inputStream,
				true);

		InfoItemFieldValues infoItemFieldValues =
			translationSnapshot.getInfoItemFieldValues();

		try {
			_translationEntryService.addOrUpdateTranslationEntry(
				groupId,
				_language.getLanguageId(translationSnapshot.getSourceLocale()),
				_language.getLanguageId(translationSnapshot.getTargetLocale()),
				infoItemFieldValues.getInfoItemReference(), infoItemFieldValues,
				ServiceContextFactory.getInstance(actionRequest));
		}
		catch (InfoItemPermissionException infoItemPermissionException) {
			throw new XLIFFFileException.MustHaveValidModel(
				infoItemPermissionException.getMessage());
		}
	}

	private void _processUploadedFiles(
			ActionRequest actionRequest,
			UploadPortletRequest uploadPortletRequest, long groupId,
			String className, long classPK, List<String> successMessages,
			List<Map<String, String>> failureMessages, Locale locale)
		throws IOException, PortalException {

		Map<String, FileItem[]> multipartParameterMap =
			uploadPortletRequest.getMultipartParameterMap();

		for (Map.Entry<String, FileItem[]> entry :
				multipartParameterMap.entrySet()) {

			for (FileItem fileItem : entry.getValue()) {
				_processXLIFFTranslation(
					actionRequest, groupId, className, classPK,
					new Translation(
						() -> MimeTypesUtil.getContentType(
							fileItem.getFileName()),
						fileItem.getFileName(), fileItem::getInputStream),
					successMessages, failureMessages, locale);
			}
		}
	}

	private void _processXLIFFInputStream(
			ActionRequest actionRequest, long groupId, String className,
			long classPK, String container, String fileName,
			List<String> successMessages,
			List<Map<String, String>> failureMessages, InputStream inputStream,
			Locale locale)
		throws IOException, PortalException {

		try {
			_importXLIFFInputStream(
				actionRequest, groupId, className, classPK, inputStream);

			successMessages.add(fileName);
		}
		catch (XLIFFFileException xliffFileException) {
			failureMessages.add(
				HashMapBuilder.put(
					"container", container
				).put(
					"errorMessage",
					() -> {
						Function<String, String> exceptionMessageFunction =
							_exceptionMessageFunctions.getOrDefault(
								xliffFileException.getClass(),
								s -> "the-xliff-file-is-invalid");

						return _language.get(
							locale, exceptionMessageFunction.apply(className));
					}
				).put(
					"fileName", fileName
				).build());
		}
	}

	private void _processXLIFFTranslation(
			ActionRequest actionRequest, long groupId, String className,
			long classPK, Translation translation, List<String> successMessages,
			List<Map<String, String>> failureMessages, Locale locale)
		throws IOException, PortalException {

		if (Objects.equals(
				translation.getContentType(), ContentTypes.APPLICATION_ZIP)) {

			try (InputStream inputStream1 = translation.getInputStream()) {
				ZipReader zipReader = _zipReaderFactory.getZipReader(
					inputStream1);

				try {
					for (String entry : zipReader.getEntries()) {
						try (InputStream inputStream2 =
								zipReader.getEntryAsInputStream(entry)) {

							_processXLIFFInputStream(
								actionRequest, groupId, className, classPK,
								translation.getFileName(), entry,
								successMessages, failureMessages, inputStream2,
								locale);
						}
					}
				}
				finally {
					zipReader.close();
				}
			}
		}
		else {
			try (InputStream inputStream = translation.getInputStream()) {
				_processXLIFFInputStream(
					actionRequest, groupId, className, classPK,
					StringPool.BLANK, translation.getFileName(),
					successMessages, failureMessages, inputStream, locale);
			}
		}
	}

	private static final Map
		<Class<? extends Exception>, Function<String, String>>
			_exceptionMessageFunctions =
				HashMapBuilder.
					<Class<? extends Exception>, Function<String, String>>put(
						XLIFFFileException.MustBeSupportedLanguage.class,
						s ->
							"the-xliff-file-has-an-unavailable-language-" +
								"translation"
					).put(
						XLIFFFileException.MustBeValid.class,
						s -> "the-file-is-an-invalid-xliff-file"
					).put(
						XLIFFFileException.MustBeWellFormed.class,
						s -> "the-xliff-file-does-not-have-all-needed-fields"
					).put(
						XLIFFFileException.MustHaveCorrectEncoding.class,
						s ->
							"the-translation-file-has-an-incorrect-encoding." +
								"the-supported-encoding-format-is-utf-8"
					).put(
						XLIFFFileException.MustHaveValidId.class,
						ImportTranslationMVCActionCommand::
							_getMustHaveValidIdMessage
					).put(
						XLIFFFileException.MustHaveValidModel.class,
						s ->
							"the-xliff-file-contains-a-translation-for-an-" +
								"invalid-model"
					).put(
						XLIFFFileException.MustHaveValidParameter.class,
						s -> "the-xliff-file-has-invalid-parameters"
					).put(
						XLIFFFileException.MustNotHaveMoreThanOne.class,
						s -> "the-xliff-file-is-invalid"
					).build();

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private TranslationEntryService _translationEntryService;

	@Reference
	private TranslationSnapshotProvider _translationSnapshotProvider;

	@Reference
	private TranslationURLProvider _translationURLProvider;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

	private static class Translation {

		public Translation(
			Supplier<String> contentTypeSupplier, String fileName,
			UnsafeSupplier<InputStream, IOException>
				inputStreamUnsafeSupplier) {

			_contentTypeSupplier = contentTypeSupplier;
			_fileName = fileName;
			_inputStreamUnsafeSupplier = inputStreamUnsafeSupplier;
		}

		public String getContentType() {
			return _contentTypeSupplier.get();
		}

		public String getFileName() {
			return _fileName;
		}

		public InputStream getInputStream() throws IOException {
			return _inputStreamUnsafeSupplier.get();
		}

		private final Supplier<String> _contentTypeSupplier;
		private final String _fileName;
		private final UnsafeSupplier<InputStream, IOException>
			_inputStreamUnsafeSupplier;

	}

}