/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.search.spi.model.index.contributor;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.internal.configuration.DLIndexerConfiguration;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.security.io.InputStreamSanitizer;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentHelper;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.RelatedEntryIndexer;
import com.liferay.portal.kernel.search.RelatedEntryIndexerRegistry;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextExtractor;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.search.ml.embedding.text.TextEmbeddingDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.document.library.internal.configuration.DLIndexerConfiguration",
	property = "indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = ModelDocumentContributor.class
)
public class DLFileEntryModelDocumentContributor
	implements ModelDocumentContributor<DLFileEntry> {

	@Override
	public void contribute(Document document, DLFileEntry dlFileEntry) {
		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Indexing document file entry " + dlFileEntry);
			}

			Locale defaultLocale = _portal.getSiteDefaultLocale(
				dlFileEntry.getGroupId());

			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			_addFile(
				document, Field.getLocalizedName(defaultLocale, Field.CONTENT),
				dlFileEntry, dlFileVersion);

			document.addKeyword(
				Field.CLASS_TYPE_ID, dlFileEntry.getFileEntryTypeId());
			document.addText(
				Field.DEFAULT_LANGUAGE_ID,
				LocaleUtil.toLanguageId(defaultLocale));
			document.addText(Field.DESCRIPTION, dlFileEntry.getDescription());
			document.addText(
				Field.getLocalizedName(defaultLocale, Field.DESCRIPTION),
				dlFileEntry.getDescription());
			document.addKeyword(Field.FOLDER_ID, dlFileEntry.getFolderId());
			document.addKeyword(Field.HIDDEN, dlFileEntry.isInHiddenFolder());
			document.addKeyword(Field.STATUS, dlFileVersion.getStatus());

			String title = dlFileEntry.getTitle();

			if (dlFileEntry.isInTrash()) {
				title = _trashHelper.getOriginalTitle(title);
			}

			document.addText(Field.TITLE, title);
			document.addText(
				Field.getLocalizedName(defaultLocale, Field.TITLE), title);

			document.addKeyword(
				Field.TREE_PATH,
				StringUtil.split(dlFileEntry.getTreePath(), CharPool.SLASH));

			document.addKeyword(
				"dataRepositoryId", dlFileEntry.getDataRepositoryId());
			document.addKeyword("extension", dlFileEntry.getExtension());
			document.addKeyword(
				"fileEntryTypeId", dlFileEntry.getFileEntryTypeId());
			document.addTextSortable(
				"fileExtension", dlFileEntry.getExtension());
			document.addText("fileName", dlFileEntry.getFileName());
			document.addTextSortable(
				"mimeType",
				StringUtil.replace(
					dlFileEntry.getMimeType(), CharPool.FORWARD_SLASH,
					CharPool.UNDERLINE));
			document.addKeyword("readCount", dlFileEntry.getReadCount());
			document.addDate("reviewDate", dlFileEntry.getReviewDate());
			document.addNumber("size", dlFileEntry.getSize());
			document.addNumber(
				"versionCount", GetterUtil.getDouble(dlFileEntry.getVersion()));

			_processDDMIndexer(document, dlFileVersion);

			if (dlFileEntry.isInHiddenFolder()) {
				List<RelatedEntryIndexer> relatedEntryIndexers =
					_relatedEntryIndexerRegistry.getRelatedEntryIndexers(
						dlFileEntry.getClassName());

				if (ListUtil.isNotEmpty(relatedEntryIndexers)) {
					for (RelatedEntryIndexer relatedEntryIndexer :
							relatedEntryIndexers) {

						relatedEntryIndexer.addRelatedEntryFields(
							document, new LiferayFileEntry(dlFileEntry));

						DocumentHelper documentHelper = new DocumentHelper(
							document);

						documentHelper.setAttachmentOwnerKey(
							_portal.getClassNameId(dlFileEntry.getClassName()),
							dlFileEntry.getClassPK());

						document.addKeyword(Field.RELATED_ENTRY, true);
					}
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Document file entry " + dlFileEntry +
						" indexed successfully");
			}
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlIndexerConfiguration = ConfigurableUtil.createConfigurable(
			DLIndexerConfiguration.class, properties);
	}

	private void _addFile(
		Document document, String fieldName, DLFileEntry dlFileEntry,
		DLFileVersion dlFileVersion) {

		if (!_isIndexContent(dlFileEntry)) {
			return;
		}

		try {
			String text = _extractText(dlFileEntry, dlFileVersion);

			if (text != null) {
				document.addText(fieldName, text);

				_textEmbeddingDocumentContributor.contribute(
					document, dlFileEntry,
					StringBundler.concat(
						dlFileEntry.getTitle(), StringPool.PERIOD,
						StringPool.SPACE, text));
			}
		}
		catch (IOException | PortalException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private String _extractText(
			DLFileEntry dlFileEntry, DLFileVersion dlFileVersion)
		throws IOException, PortalException {

		int dlFileIndexingMaxSize = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.DL_FILE_INDEXING_MAX_SIZE));

		String indexVersionLabel = dlFileVersion.getStoreFileName() + ".index";

		if (_dlIndexerConfiguration.cacheTextExtraction() &&
			_dlStore.hasFile(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), indexVersionLabel)) {

			String string = StreamUtil.toString(
				_dlStore.getFileAsStream(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					indexVersionLabel));

			if (string.length() <= dlFileIndexingMaxSize) {
				if (string.isEmpty()) {
					return null;
				}

				return string;
			}

			_dlStore.deleteFile(
				dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
				dlFileEntry.getName(), indexVersionLabel);
		}

		String text = null;

		try {
			text = _textExtractor.extractText(
				_inputStreamSanitizer.sanitize(
					dlFileVersion.getContentStream(false)),
				dlFileIndexingMaxSize);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get input stream", portalException);
			}
		}

		if (_dlIndexerConfiguration.cacheTextExtraction() &&
			!_isReadOnlyCtCollection()) {

			byte[] bytes = null;

			if (text == null) {
				bytes = new byte[0];
			}
			else {
				bytes = text.getBytes(StandardCharsets.UTF_8);
			}

			_dlStore.addFile(
				DLStoreRequest.builder(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName()
				).versionLabel(
					indexVersionLabel
				).build(),
				bytes);
		}

		return text;
	}

	private boolean _isIndexContent(DLFileEntry dlFileEntry) {
		String[] ignoreExtensions = _prefsProps.getStringArray(
			PropsKeys.DL_FILE_INDEXING_IGNORE_EXTENSIONS, StringPool.COMMA);

		return !ArrayUtil.contains(
			ignoreExtensions, StringPool.PERIOD + dlFileEntry.getExtension());
	}

	private boolean _isReadOnlyCtCollection() throws PortalException {
		if (CTCollectionThreadLocal.isProductionMode()) {
			return false;
		}

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			CTCollectionThreadLocal.getCTCollectionId());

		return ctCollection.isReadOnly();
	}

	private void _processDDMIndexer(
		Document document, DLFileVersion dlFileVersion) {

		List<DLFileEntryMetadata> dlFileEntryMetadatas =
			_dlFileEntryMetadataLocalService.getFileVersionFileEntryMetadatas(
				dlFileVersion.getFileVersionId());

		Locale locale = LocaleUtil.getSiteDefault();

		StringBundler sb = new StringBundler(dlFileEntryMetadatas.size() * 2);

		for (DLFileEntryMetadata dlFileEntryMetadata : dlFileEntryMetadatas) {
			try {
				DDMStructure ddmStructure =
					_ddmStructureLocalService.getStructure(
						dlFileEntryMetadata.getDDMStructureId());

				DDMFormValues ddmFormValues =
					_ddmStorageEngineManager.getDDMFormValues(
						dlFileEntryMetadata.getDDMStorageId(),
						ddmStructure.getDDMForm());

				if (ddmFormValues != null) {
					sb.append(
						_ddmIndexer.extractIndexableAttributes(
							ddmStructure, ddmFormValues, locale));

					sb.append(StringPool.SPACE);

					_ddmIndexer.addAttributes(
						document, ddmStructure, ddmFormValues);
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to retrieve metadata values", exception);
				}
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		document.addText("ddmContent", sb.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryModelDocumentContributor.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private DDMIndexer _ddmIndexer;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	private volatile DLIndexerConfiguration _dlIndexerConfiguration;

	@Reference
	private DLStore _dlStore;

	@Reference
	private InputStreamSanitizer _inputStreamSanitizer;

	@Reference
	private Portal _portal;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private RelatedEntryIndexerRegistry _relatedEntryIndexerRegistry;

	@Reference
	private TextEmbeddingDocumentContributor _textEmbeddingDocumentContributor;

	@Reference
	private TextExtractor _textExtractor;

	@Reference
	private TrashHelper _trashHelper;

}