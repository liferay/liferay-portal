/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.text;

import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.internal.configuration.DLIndexerConfiguration;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.security.io.InputStreamSanitizer;
import com.liferay.document.library.text.DLFileEntryTextProvider;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.TextExtractor;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jhosseph Gonzalez
 */
@Component(
	configurationPid = "com.liferay.document.library.internal.configuration.DLIndexerConfiguration",
	service = DLFileEntryTextProvider.class
)
public class DLFileEntryTextProviderImpl implements DLFileEntryTextProvider {

	@Override
	public String getText(DLFileEntry dlFileEntry) {
		if (!_isIndexContent(dlFileEntry)) {
			return null;
		}

		try {
			return _getText(dlFileEntry, dlFileEntry.getFileVersion());
		}
		catch (Throwable throwable) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to extract text from file entry " +
						dlFileEntry.getFileEntryId());
			}

			if (_log.isDebugEnabled()) {
				_log.debug(throwable);
			}

			return null;
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		modified(properties);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_dlIndexerConfiguration = ConfigurableUtil.createConfigurable(
			DLIndexerConfiguration.class, properties);
	}

	private String _getText(
			DLFileEntry dlFileEntry, DLFileVersion dlFileVersion)
		throws IOException, PortalException {

		int dlFileIndexingMaxSize = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.DL_FILE_INDEXING_MAX_SIZE));

		String indexVersionLabel = dlFileVersion.getStoreFileName() + ".index";

		if (_dlIndexerConfiguration.cacheTextExtraction()) {
			try {
				String string = StreamUtil.toString(
					_dlStore.getFileAsStream(
						dlFileEntry.getCompanyId(),
						dlFileEntry.getDataRepositoryId(),
						dlFileEntry.getName(), indexVersionLabel));

				if (string.length() <= dlFileIndexingMaxSize) {
					if (string.isEmpty()) {
						return null;
					}

					return string;
				}

				_dlStore.deleteFile(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					indexVersionLabel);
			}
			catch (NoSuchFileException noSuchFileException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get cached text extraction" +
							noSuchFileException);
				}
			}
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
				).className(
					dlFileEntry.getModelClassName()
				).classPK(
					dlFileEntry.getFileEntryId()
				).sourceFileName(
					dlFileEntry.getFileName()
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

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryTextProviderImpl.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	private volatile DLIndexerConfiguration _dlIndexerConfiguration;

	@Reference
	private DLStore _dlStore;

	@Reference
	private InputStreamSanitizer _inputStreamSanitizer;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private TextExtractor _textExtractor;

}