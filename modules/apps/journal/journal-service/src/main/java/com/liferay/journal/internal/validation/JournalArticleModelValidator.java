/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.validation;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.exception.StorageFieldNameException;
import com.liferay.dynamic.data.mapping.exception.StorageFieldRequiredException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.content.processor.ExportImportContentProcessorRegistryUtil;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.journal.configuration.JournalFileUploadsConfiguration;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.exception.ArticleContentException;
import com.liferay.journal.exception.ArticleExpirationDateException;
import com.liferay.journal.exception.ArticleIdException;
import com.liferay.journal.exception.ArticleSmallImageNameException;
import com.liferay.journal.exception.ArticleSmallImageSizeException;
import com.liferay.journal.exception.ArticleTitleException;
import com.liferay.journal.exception.DuplicateArticleExternalReferenceCodeException;
import com.liferay.journal.exception.DuplicateArticleIdException;
import com.liferay.journal.exception.InvalidDDMStructureException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.service.persistence.JournalArticlePersistence;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalHelper;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.validation.ModelValidationResults;
import com.liferay.portal.validation.ModelValidator;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalFileUploadsConfiguration",
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = ModelValidator.class
)
public class JournalArticleModelValidator
	implements ModelValidator<JournalArticle> {

	public void validate(
			long companyId, long groupId, long classNameId,
			Map<Locale, String> titleMap, String content, long ddmStructureId,
			String ddmTemplateKey, Date displayDate, Date expirationDate,
			boolean smallImage, String smallImageURL, File smallImageFile,
			byte[] smallImageBytes, ServiceContext serviceContext)
		throws PortalException {

		Locale articleDefaultLocale = LocaleUtil.fromLanguageId(
			_localization.getDefaultLanguageId(content));

		if (!ExportImportThreadLocal.isImportInProcess()) {
			if (!_language.isAvailableLocale(groupId, articleDefaultLocale)) {
				LocaleException localeException = new LocaleException(
					LocaleException.TYPE_CONTENT,
					StringBundler.concat(
						"The locale ", articleDefaultLocale.getLanguage(),
						" is not available in site with groupId", groupId));

				localeException.setSourceAvailableLocales(
					Collections.singleton(articleDefaultLocale));
				localeException.setTargetAvailableLocales(
					_language.getAvailableLocales(groupId));

				throw localeException;
			}

			if ((expirationDate != null) && expirationDate.before(new Date())) {
				throw new ArticleExpirationDateException(
					"Expiration date " + expirationDate + " is in the past");
			}

			if ((displayDate != null) && (expirationDate != null) &&
				displayDate.after(expirationDate)) {

				throw new ArticleExpirationDateException(
					StringBundler.concat(
						"Expiration date ", expirationDate,
						" is prior to display date ", displayDate));
			}
		}

		if ((classNameId == JournalArticleConstants.CLASS_NAME_ID_DEFAULT) &&
			(titleMap.isEmpty() ||
			 Validator.isNull(titleMap.get(articleDefaultLocale)))) {

			throw new ArticleTitleException("Title is null");
		}

		int titleMaxLength = ModelHintsUtil.getMaxLength(
			JournalArticleLocalization.class.getName(), "title");

		for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
			String title = entry.getValue();

			if (Validator.isNull(title) || (title.length() <= titleMaxLength)) {
				continue;
			}

			throw new ArticleTitleException.MustNotExceedMaximumLength(
				title, titleMaxLength);
		}

		validateContent(content);

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		if (!ExportImportThreadLocal.isImportInProcess()) {
			validateDDMStructureFields(
				ddmStructure, classNameId, content, articleDefaultLocale);
		}

		if (Validator.isNotNull(ddmTemplateKey)) {
			DDMTemplate ddmTemplate = _ddmTemplateLocalService.getTemplate(
				_portal.getSiteGroupId(groupId),
				_classNameLocalService.getClassNameId(DDMStructure.class),
				ddmTemplateKey, true);

			if (ddmTemplate.getClassPK() != ddmStructure.getStructureId()) {
				throw new NoSuchTemplateException(
					"{templateKey=" + ddmTemplateKey + "}");
			}
		}

		if (!smallImage || Validator.isNotNull(smallImageURL) ||
			(smallImageFile == null) || (smallImageBytes == null)) {

			return;
		}

		String smallImageName = smallImageFile.getName();

		boolean validSmallImageExtension = false;

		for (String imageExtension :
				_journalFileUploadsConfiguration.imageExtensions()) {

			if (StringPool.STAR.equals(imageExtension) ||
				StringUtil.endsWith(smallImageName, imageExtension)) {

				validSmallImageExtension = true;

				break;
			}
		}

		if (!validSmallImageExtension) {
			throw new ArticleSmallImageNameException(
				"Invalid image extension " +
					FileUtil.getExtension(smallImageName));
		}

		long smallImageMaxSize =
			_journalFileUploadsConfiguration.smallImageMaxSize();

		if ((smallImageMaxSize > 0) &&
			(smallImageBytes.length > smallImageMaxSize)) {

			throw new ArticleSmallImageSizeException(
				smallImageBytes.length + " exceeds " + smallImageMaxSize);
		}
	}

	public void validate(String articleId) throws PortalException {
		if (Validator.isNull(articleId) ||
			(articleId.indexOf(CharPool.COMMA) != -1) ||
			(articleId.indexOf(CharPool.SPACE) != -1)) {

			throw new ArticleIdException("Invalid article ID: " + articleId);
		}
	}

	public void validate(
			String externalReferenceCode, long companyId, long groupId,
			long classNameId, String articleId, boolean autoArticleId,
			double version, Map<Locale, String> titleMap, String content,
			long ddmStructureId, String ddmTemplateKey, Date displayDate,
			Date expirationDate, boolean smallImage, String smallImageURL,
			File smallImageFile, byte[] smallImageBytes,
			ServiceContext serviceContext)
		throws PortalException {

		_validateExternalReferenceCode(externalReferenceCode, groupId);

		if (!autoArticleId) {
			validate(articleId);
		}

		if (!ExportImportThreadLocal.isImportInProcess() || autoArticleId) {
			List<JournalArticle> articles =
				_journalArticlePersistence.findByG_A(groupId, articleId);

			if (!articles.isEmpty()) {
				throw new DuplicateArticleIdException(
					StringBundler.concat(
						"{groupId=", groupId, ", articleId=", articleId,
						", version=", version, "}"));
			}
		}

		validate(
			companyId, groupId, classNameId, titleMap, content, ddmStructureId,
			ddmTemplateKey, displayDate, expirationDate, smallImage,
			smallImageURL, smallImageFile, smallImageBytes, serviceContext);
	}

	public void validateContent(String content) throws PortalException {
		if (Validator.isNull(content)) {
			throw new ArticleContentException("Content is null");
		}

		try {
			SAXReaderUtil.read(content);
		}
		catch (DocumentException documentException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Invalid content:\n" + content);
			}

			throw new ArticleContentException(
				"Unable to read content with an XML parser", documentException);
		}
	}

	public void validateDDMStructureFields(
			DDMStructure ddmStructure, long classNameId, Fields fields,
			Locale defaultlocale)
		throws PortalException {

		for (Field field : fields) {
			if (!ddmStructure.hasField(field.getName())) {
				throw new StorageFieldNameException(
					"No field exists for {name=" + field.getName() + "}");
			}

			if (ddmStructure.getFieldRequired(field.getName()) &&
				Validator.isNull(field.getValue(defaultlocale)) &&
				(classNameId ==
					JournalArticleConstants.CLASS_NAME_ID_DEFAULT)) {

				throw new StorageFieldRequiredException(
					StringBundler.concat(
						"Required field ", field.getName(),
						" is not present for structure ",
						ddmStructure.getNameCurrentValue(), " for locale ",
						defaultlocale));
			}
		}
	}

	public void validateDDMStructureFields(
			DDMStructure ddmStructure, long classNameId, String content,
			Locale defaultlocale)
		throws PortalException {

		Fields fields = _journalConverter.getDDMFields(ddmStructure, content);

		validateDDMStructureFields(
			ddmStructure, classNameId, fields, defaultlocale);
	}

	public void validateDDMStructureId(
			long groupId, long folderId, long ddmStructureId)
		throws PortalException {

		int restrictionType = _journalHelper.getRestrictionType(folderId);

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		List<DDMStructure> folderDDMStructures =
			_journalFolderLocalService.getDDMStructures(
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						groupId, false, true),
				folderId, restrictionType);

		for (DDMStructure folderDDMStructure : folderDDMStructures) {
			if (folderDDMStructure.getStructureId() ==
					ddmStructure.getStructureId()) {

				return;
			}
		}

		throw new InvalidDDMStructureException(
			StringBundler.concat(
				"Invalid structure ", ddmStructure.getStructureId(),
				" for folder ", folderId));
	}

	@Override
	public ModelValidationResults validateModel(JournalArticle article) {
		String ddmTemplateKey = article.getDDMTemplateKey();
		boolean smallImage = article.isSmallImage();
		String smallImageURL = article.getSmallImageURL();

		byte[] smallImageBytes = null;
		File smallImageFile = null;

		if (smallImage) {
			Image image = _imageLocalService.fetchImage(
				article.getSmallImageId());

			if (image != null) {
				smallImageBytes = image.getTextObj();

				if (smallImageBytes != null) {
					try {
						smallImageFile = FileUtil.createTempFile(
							image.getType());

						FileUtil.write(smallImageFile, smallImageBytes, false);
					}
					catch (IOException ioException) {
						if (_log.isDebugEnabled()) {
							_log.debug(ioException);
						}

						smallImageBytes = null;
					}
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}

		String content = article.getContent();

		try {
			validate(
				article.getCompanyId(), article.getGroupId(),
				article.getClassNameId(), article.getTitleMap(), content,
				article.getDDMStructureId(), ddmTemplateKey,
				article.getDisplayDate(), article.getExpirationDate(),
				smallImage, smallImageURL, smallImageFile, smallImageBytes,
				serviceContext);
		}
		catch (PortalException portalException) {
			ModelValidationResults.FailureBuilder failureBuilder =
				ModelValidationResults.failure();

			return failureBuilder.exceptionFailure(
				portalException.getMessage(), portalException
			).getResults();
		}

		try {
			validateReferences(
				article.getGroupId(), article.getFolderId(),
				article.getDDMStructureId(), ddmTemplateKey,
				article.getLayoutUuid(), smallImage, smallImageURL,
				smallImageBytes, article.getSmallImageId(),
				article.getSmallImageSource(), content);
		}
		catch (ExportImportContentValidationException
					exportImportContentValidationException) {

			exportImportContentValidationException.setStagedModelClassName(
				JournalArticle.class.getName());
			exportImportContentValidationException.setStagedModelPrimaryKeyObj(
				article.getArticleId());

			ModelValidationResults.FailureBuilder failureBuilder =
				ModelValidationResults.failure();

			return failureBuilder.exceptionFailure(
				exportImportContentValidationException.getMessage(),
				exportImportContentValidationException
			).getResults();
		}
		catch (PortalException portalException) {
			ModelValidationResults.FailureBuilder failureBuilder =
				ModelValidationResults.failure();

			return failureBuilder.exceptionFailure(
				portalException.getMessage(), portalException
			).getResults();
		}

		return ModelValidationResults.success();
	}

	public void validateReferences(
			long groupId, long folderId, long ddmStructureId,
			String ddmTemplateKey, String layoutUuid, boolean smallImage,
			String smallImageURL, byte[] smallImageBytes, long smallImageId,
			int smallImageSource, String content)
		throws PortalException {

		if (folderId != 0) {
			_journalFolderLocalService.getFolder(folderId);
		}

		_ddmStructureLocalService.getDDMStructure(ddmStructureId);

		if (Validator.isNotNull(ddmTemplateKey)) {
			DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
				_portal.getSiteGroupId(groupId),
				_classNameLocalService.getClassNameId(
					DDMStructure.class.getName()),
				ddmTemplateKey, true);

			if (ddmTemplate == null) {
				throw new NoSuchTemplateException();
			}
		}

		if (smallImage && Validator.isNull(smallImageURL) &&
			ArrayUtil.isEmpty(smallImageBytes) &&
			(smallImageSource ==
				JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER)) {

			Image image = _imageLocalService.fetchImage(smallImageId);

			if (image != null) {
				smallImageBytes = image.getTextObj();
			}

			if ((image == null) || (smallImageBytes == null)) {
				throw new NoSuchImageException(
					"Small image ID " + smallImageId);
			}
		}

		if (smallImage &&
			(smallImageSource ==
				JournalArticleConstants.
					SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA)) {

			try {
				_dlAppLocalService.getFileEntry(smallImageId);
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				throw new NoSuchImageException(
					"Small image ID " + smallImageId, noSuchFileEntryException);
			}
		}

		ExportImportContentProcessor<String> exportImportContentProcessor =
			ExportImportContentProcessorRegistryUtil.
				getExportImportContentProcessor(JournalArticle.class.getName());

		if (smallImage && Validator.isNotNull(smallImageURL)) {
			exportImportContentProcessor.validateContentReferences(
				groupId, smallImageURL);
		}

		exportImportContentProcessor.validateContentReferences(
			groupId, content);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_journalFileUploadsConfiguration = ConfigurableUtil.createConfigurable(
			JournalFileUploadsConfiguration.class, properties);
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		List<JournalArticle> articles = _journalArticlePersistence.findByG_ERC(
			groupId, externalReferenceCode);

		if (!articles.isEmpty()) {
			throw new DuplicateArticleExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate journal article external reference code ",
					externalReferenceCode, " in group ", groupId));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleModelValidator.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private JournalArticlePersistence _journalArticlePersistence;

	@Reference
	private JournalConverter _journalConverter;

	private JournalFileUploadsConfiguration _journalFileUploadsConfiguration;

	@Reference
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}