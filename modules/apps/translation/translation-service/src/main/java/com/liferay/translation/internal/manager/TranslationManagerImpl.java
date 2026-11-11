/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.internal.manager;

import com.liferay.document.library.kernel.exception.FileMimeTypeException;
import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.exception.XLIFFFileException;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;
import com.liferay.translation.manager.Translation;
import com.liferay.translation.manager.TranslationManager;
import com.liferay.translation.service.TranslationEntryService;
import com.liferay.translation.snapshot.TranslationSnapshot;
import com.liferay.translation.snapshot.TranslationSnapshotProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 * @author Roberto Díaz
 */
@Component(service = TranslationManager.class)
public class TranslationManagerImpl implements TranslationManager {

	@Override
	public String getTitle(String className, long classPK, Locale locale) {
		try {
			ObjectValuePair<InfoItemReference, String>
				infoItemReferenceSuffixObjectValuePair =
					_getInfoItemReferenceSuffixObjectValuePair(
						className, classPK, locale);

			InfoItemReference infoItemReference =
				infoItemReferenceSuffixObjectValuePair.getKey();

			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			InfoItemObjectProvider<Object> infoItemObjectProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					infoItemReference.getClassName(),
					infoItemIdentifier.getInfoItemServiceFilter());

			Object object = infoItemObjectProvider.getInfoItem(
				infoItemIdentifier);

			if (object == null) {
				return null;
			}

			return _getTitle(infoItemReference.getClassName(), object, locale) +
				infoItemReferenceSuffixObjectValuePair.getValue();
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}
	}

	@Override
	public File getXLIFFFile(
			String className, long classPK, String xliffMimeType, Locale locale,
			String sourceLanguageId, String targetLanguageId)
		throws IOException, PortalException {

		_validateLanguageId(sourceLanguageId);
		_validateLanguageId(targetLanguageId);

		String fileName = _getXLIFFFileName(
			className, classPK, locale, sourceLanguageId, targetLanguageId);

		File file = new File(_createTempDirectory(), fileName);

		try (OutputStream outputStream = new FileOutputStream(file)) {
			StreamUtil.transfer(
				_getXLIFFInputStream(
					className, classPK, sourceLanguageId, targetLanguageId,
					xliffMimeType),
				outputStream);
		}

		return file;
	}

	@Override
	public File getXLIFFZipFile(
			String className, long[] classPKs, String xliffMimeType,
			Locale locale, String sourceLanguageId, String[] targetLanguageIds)
		throws IOException, PortalException {

		if (classPKs == null) {
			throw new XLIFFFileException.MustHaveValidParameter("classPKs");
		}

		if (targetLanguageIds == null) {
			throw new XLIFFFileException.MustHaveValidParameter(
				"targetLanguageIds");
		}

		_validateLanguageId(sourceLanguageId);

		for (String targetLanguageId : targetLanguageIds) {
			_validateLanguageId(targetLanguageId);
		}

		String fileName = StringBundler.concat(
			StringUtil.removeSubstrings(
				_getPrefixName(className, classPKs, locale),
				PropsValues.DL_CHAR_BLACKLIST),
			StringPool.DASH, sourceLanguageId, ".zip");

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter(
			new File(_createTempDirectory(), fileName));

		for (long classPK : classPKs) {
			for (String targetLanguageId : targetLanguageIds) {
				zipWriter.addEntry(
					_getXLIFFFileName(
						className, classPK, locale, sourceLanguageId,
						targetLanguageId),
					_getXLIFFInputStream(
						className, classPK, sourceLanguageId, targetLanguageId,
						xliffMimeType));
			}
		}

		return zipWriter.getFile();
	}

	@Override
	public void processXLIFFTranslation(
			long groupId, String className, long classPK,
			Translation translation, List<String> successMessages,
			List<Map<String, String>> failureMessages, Locale locale,
			ServiceContext serviceContext)
		throws IOException, PortalException {

		if (Objects.equals(
				translation.getContentType(), ContentTypes.APPLICATION_ZIP)) {

			try (InputStream inputStream1 = translation.getInputStream()) {
				try (ZipReader zipReader = _zipReaderFactory.getZipReader(
						inputStream1)) {

					for (String entry : zipReader.getEntries()) {
						try (InputStream inputStream2 =
								zipReader.getEntryAsInputStream(entry)) {

							_processXLIFFInputStream(
								groupId, className, classPK,
								translation.getFileName(), entry,
								successMessages, failureMessages, inputStream2,
								locale, serviceContext);
						}
					}
				}
			}
		}
		else {
			try (InputStream inputStream = translation.getInputStream()) {
				_processXLIFFInputStream(
					groupId, className, classPK, StringPool.BLANK,
					translation.getFileName(), successMessages, failureMessages,
					inputStream, locale, serviceContext);
			}
		}
	}

	private static String _getMustHaveValidIdMessage(String className) {
		if (className.equals(Layout.class.getName())) {
			return "the-translation-file-does-not-correspond-to-this-page";
		}

		return "the-translation-file-does-not-correspond-to-this-web-content";
	}

	private File _createTempDirectory() throws IOException {
		File directory = FileUtil.createTempFile();

		if (!directory.mkdir()) {
			throw new IOException(
				"Unable to create directory " + directory.getPath());
		}

		return directory;
	}

	private InfoItemReference _getInfoItemReference(
		String className, long classPK) {

		if (classPK == 0) {
			return null;
		}

		return new InfoItemReference(className, classPK);
	}

	private ObjectValuePair<InfoItemReference, String>
		_getInfoItemReferenceSuffixObjectValuePair(
			String className, long classPK, Locale locale) {

		if (!Objects.equals(className, SegmentsExperience.class.getName())) {
			return new ObjectValuePair<>(
				new InfoItemReference(className, classPK), StringPool.BLANK);
		}

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(classPK);

		if (segmentsExperience == null) {
			return new ObjectValuePair<>(
				new InfoItemReference(className, classPK), StringPool.BLANK);
		}

		return new ObjectValuePair<>(
			new InfoItemReference(
				Layout.class.getName(), segmentsExperience.getPlid()),
			StringBundler.concat(
				StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
				segmentsExperience.getName(locale),
				StringPool.CLOSE_PARENTHESIS));
	}

	private String _getPrefixName(
		String className, long[] classPKs, Locale locale) {

		if (classPKs.length > 1) {
			String title = _language.get(locale, "model.resource." + className);

			return title + StringPool.SPACE +
				_language.get(locale, "translations");
		}

		String title = getTitle(className, classPKs[0], locale);

		if (title != null) {
			return title;
		}

		title = _language.get(locale, "model.resource." + className);

		return title + StringPool.SPACE + classPKs[0];
	}

	private String _getStringValue(
		InfoFieldValue<Object> infoFieldValue, Locale locale) {

		Object value = infoFieldValue.getValue();

		if (value instanceof InfoLocalizedValue) {
			InfoLocalizedValue<Object> infoLocalizedValue =
				(InfoLocalizedValue<Object>)value;

			String stringValue = (String)infoLocalizedValue.getValue(locale);

			if (Validator.isNotNull(stringValue)) {
				return stringValue;
			}

			Locale defaultLocale = infoLocalizedValue.getDefaultLocale();

			if (defaultLocale == null) {
				return null;
			}

			return (String)infoLocalizedValue.getValue(defaultLocale);
		}

		return (String)value;
	}

	private String _getTitle(String className, Object object, Locale locale) {
		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		InfoFieldValue<Object> titleInfoFieldValue =
			infoItemFieldValuesProvider.getInfoFieldValue(object, "title");

		if (titleInfoFieldValue != null) {
			String value = _getStringValue(titleInfoFieldValue, locale);

			if (Validator.isNotNull(value)) {
				return value;
			}
		}

		InfoFieldValue<Object> nameInfoFieldValue =
			infoItemFieldValuesProvider.getInfoFieldValue(object, "name");

		if (nameInfoFieldValue != null) {
			return _getStringValue(nameInfoFieldValue, locale);
		}

		return null;
	}

	private String _getXLIFFFileName(
		String className, long classPK, Locale locale, String sourceLanguageId,
		String targetLanguageId) {

		String title = getTitle(className, classPK, locale);

		if (Validator.isNull(title)) {
			title =
				_language.get(locale, "model.resource." + className) +
					StringPool.SPACE + classPK;
		}

		return StringBundler.concat(
			StringPool.FORWARD_SLASH,
			StringUtil.removeSubstrings(title, PropsValues.DL_CHAR_BLACKLIST),
			StringPool.DASH, sourceLanguageId, StringPool.DASH,
			targetLanguageId, ".xlf");
	}

	private InputStream _getXLIFFInputStream(
			String className, long classPK, String sourceLanguageId,
			String targetLanguageId, String xliffMimeType)
		throws IOException, PortalException {

		if (Validator.isBlank(xliffMimeType)) {
			throw new FileMimeTypeException("Unknown xliff mime type");
		}

		TranslationInfoItemFieldValuesExporter
			translationInfoItemFieldValuesExporter =
				_translationInfoItemFieldValuesExporterRegistry.
					getTranslationInfoItemFieldValuesExporter(xliffMimeType);

		if (translationInfoItemFieldValuesExporter == null) {
			throw new FileMimeTypeException(
				"Unknown xliff mime type: " + xliffMimeType);
		}

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		Object object = infoItemObjectProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(classPK));

		return translationInfoItemFieldValuesExporter.exportInfoItemFieldValues(
			infoItemFieldValuesProvider.getInfoItemFieldValues(object),
			LocaleUtil.fromLanguageId(sourceLanguageId),
			LocaleUtil.fromLanguageId(targetLanguageId));
	}

	private void _importXLIFFInputStream(
			long groupId, String className, long classPK,
			InputStream inputStream, ServiceContext serviceContext)
		throws IOException, PortalException {

		TranslationSnapshot translationSnapshot =
			_translationSnapshotProvider.getTranslationSnapshot(
				groupId, _getInfoItemReference(className, classPK), inputStream,
				true);

		InfoItemFieldValues infoItemFieldValues =
			translationSnapshot.getInfoItemFieldValues();

		Collection<InfoFieldValue<Object>> infoFieldValues =
			infoItemFieldValues.getInfoFieldValues();

		if (infoFieldValues.isEmpty()) {
			return;
		}

		try {
			_translationEntryService.addOrUpdateTranslationEntry(
				groupId,
				_language.getLanguageId(translationSnapshot.getSourceLocale()),
				_language.getLanguageId(translationSnapshot.getTargetLocale()),
				infoItemFieldValues.getInfoItemReference(), infoItemFieldValues,
				serviceContext);
		}
		catch (InfoItemPermissionException infoItemPermissionException) {
			throw new XLIFFFileException.MustHaveValidModel(
				infoItemPermissionException.getMessage());
		}
	}

	private void _processXLIFFInputStream(
			long groupId, String className, long classPK, String container,
			String fileName, List<String> successMessages,
			List<Map<String, String>> failureMessages, InputStream inputStream,
			Locale locale, ServiceContext serviceContext)
		throws IOException, PortalException {

		try {
			_importXLIFFInputStream(
				groupId, className, classPK, inputStream, serviceContext);

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
								key -> "the-xliff-file-is-invalid");

						return _language.get(
							locale, exceptionMessageFunction.apply(className));
					}
				).put(
					"fileName", fileName
				).build());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private void _validateLanguageId(String languageId) throws PortalException {
		if (!_language.isAvailableLocale(languageId)) {
			throw new XLIFFFileException.MustBeSupportedLanguage(languageId);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TranslationManagerImpl.class);

	private static final Map
		<Class<? extends Exception>, Function<String, String>>
			_exceptionMessageFunctions =
				HashMapBuilder.
					<Class<? extends Exception>, Function<String, String>>put(
						XLIFFFileException.MustBeSupportedLanguage.class,
						key ->
							"the-xliff-file-has-an-unavailable-language-" +
								"translation"
					).put(
						XLIFFFileException.MustBeValid.class,
						key -> "the-file-is-an-invalid-xliff-file"
					).put(
						XLIFFFileException.MustBeWellFormed.class,
						key -> "the-xliff-file-does-not-have-all-needed-fields"
					).put(
						XLIFFFileException.MustHaveCorrectEncoding.class,
						key ->
							"the-translation-file-has-an-incorrect-encoding." +
								"the-supported-encoding-format-is-utf-8"
					).put(
						XLIFFFileException.MustHaveValidId.class,
						TranslationManagerImpl::_getMustHaveValidIdMessage
					).put(
						XLIFFFileException.MustHaveValidModel.class,
						key ->
							"the-xliff-file-contains-a-translation-for-an-" +
								"invalid-model"
					).put(
						XLIFFFileException.MustHaveValidParameter.class,
						key -> "the-xliff-file-has-invalid-parameters"
					).put(
						XLIFFFileException.MustNotHaveMoreThanOne.class,
						key -> "the-xliff-file-is-invalid"
					).build();

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private TranslationEntryService _translationEntryService;

	@Reference
	private TranslationInfoItemFieldValuesExporterRegistry
		_translationInfoItemFieldValuesExporterRegistry;

	@Reference
	private TranslationSnapshotProvider _translationSnapshotProvider;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}