/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.document.conversion.internal;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

import com.liferay.document.library.document.conversion.internal.background.task.OpenOfficeConversionPreviewBackgroundTaskExecutor;
import com.liferay.document.library.document.conversion.internal.configuration.OpenOfficeConfiguration;
import com.liferay.document.library.kernel.document.conversion.DocumentConversion;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Alexander Chow
 */
@Component(
	configurationPid = "com.liferay.document.library.document.conversion.internal.configuration.OpenOfficeConfiguration",
	service = DocumentConversion.class
)
public class DocumentConversionImpl implements DocumentConversion {

	@Override
	public File convert(
			String id, InputStream inputStream, String sourceExtension,
			String targetExtension)
		throws IOException {

		if (!isEnabled()) {
			return null;
		}

		sourceExtension = _fixExtension(sourceExtension);

		targetExtension = _fixExtension(targetExtension);

		_validate(targetExtension, id);

		String fileName = getFilePath(id, targetExtension);

		File file = new File(fileName);

		if (_openOfficeConfiguration.cacheEnabled() && file.exists()) {
			return file;
		}

		DocumentFormatRegistry documentFormatRegistry =
			new LiferayDocumentFormatRegistry();

		DocumentFormat inputDocumentFormat =
			documentFormatRegistry.getFormatByFileExtension(sourceExtension);
		DocumentFormat outputDocumentFormat =
			documentFormatRegistry.getFormatByFileExtension(targetExtension);

		if (inputDocumentFormat == null) {
			throw new SystemException(
				"Conversion is not supported from ." + sourceExtension);
		}
		else if (!inputDocumentFormat.isImportable()) {
			throw new SystemException(
				"Conversion is not supported from " +
					inputDocumentFormat.getName());
		}
		else if (outputDocumentFormat == null) {
			throw new SystemException(
				StringBundler.concat(
					"Conversion is not supported from ",
					inputDocumentFormat.getName(), " to .", targetExtension));
		}
		else if (!inputDocumentFormat.isExportableTo(outputDocumentFormat)) {
			throw new SystemException(
				StringBundler.concat(
					"Conversion is not supported from ",
					inputDocumentFormat.getName(), " to ",
					outputDocumentFormat.getName()));
		}

		if (sourceExtension.equals("html")) {
			DocumentHTMLProcessor documentHTMLProcessor =
				new DocumentHTMLProcessor();

			inputStream = documentHTMLProcessor.process(inputStream);
		}

		try {
			_convert(
				inputDocumentFormat, inputStream, file, outputDocumentFormat);
		}
		catch (TimeoutException timeoutException) {
			throw new IOException(timeoutException);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new IOException(exception);
		}

		return file;
	}

	@Override
	public void generatePreviews() {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				try {
					String jobName = "generatePreviews-".concat(
						PortalUUIDUtil.generate());

					_backgroundTaskManager.addBackgroundTask(
						UserConstants.USER_ID_DEFAULT,
						BackgroundTaskConstants.GROUP_ID_DEFAULT, jobName,
						OpenOfficeConversionPreviewBackgroundTaskExecutor.class.
							getName(),
						HashMapBuilder.<String, Serializable>put(
							BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS,
							true
						).build(),
						new ServiceContext());
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}
				}
			});
	}

	@Override
	public String[] getConversions(String extension) {
		extension = _fixExtension(extension);

		String[] conversions1 = ConversionsHolder.getConversions(extension);

		if (conversions1 == null) {
			return _DEFAULT_CONVERSIONS;
		}

		if (!ArrayUtil.contains(conversions1, extension)) {
			return conversions1;
		}

		List<String> conversions2 = new ArrayList<>();

		for (String conversion : conversions1) {
			if (conversion.equals(extension)) {
				continue;
			}

			conversions2.add(conversion);
		}

		return conversions2.toArray(new String[0]);
	}

	@Override
	public String getFilePath(String id, String targetExtension) {
		return StringBundler.concat(
			SystemProperties.get(SystemProperties.TMP_DIR),
			"/liferay/document_conversion/", id, StringPool.PERIOD,
			targetExtension);
	}

	@Override
	public boolean isComparableVersion(String extension) {
		boolean enabled = false;

		String periodAndExtension = StringPool.PERIOD.concat(extension);

		for (String comparableFileExtension : _COMPARABLE_FILE_EXTENSIONS) {
			if (StringPool.STAR.equals(comparableFileExtension) ||
				periodAndExtension.equals(comparableFileExtension)) {

				enabled = true;

				break;
			}
		}

		if (!enabled) {
			return false;
		}

		if (extension.equals("css") || extension.equals("htm") ||
			extension.equals("html") || extension.equals("js") ||
			extension.equals("txt") || extension.equals("xml")) {

			return true;
		}

		try {
			if (isEnabled() && isConvertBeforeCompare(extension)) {
				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Override
	public boolean isConvertBeforeCompare(String extension) {
		if (extension.equals("txt")) {
			return false;
		}

		for (String conversion : getConversions(extension)) {
			if (conversion.equals("txt")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isEnabled() {
		return _openOfficeConfiguration.serverEnabled();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_executorService = _portalExecutorManager.getPortalExecutor(
			DocumentConversionImpl.class.getName());

		_openOfficeConfiguration = ConfigurableUtil.createConfigurable(
			OpenOfficeConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate() {
		if (_executorService != null) {
			_executorService.shutdownNow();
		}

		if ((_openOfficeConnection != null) &&
			_openOfficeConnection.isConnected()) {

			_openOfficeConnection.disconnect();
		}
	}

	private void _convert(
			DocumentFormat inputDocumentFormat, InputStream inputStream,
			File file, DocumentFormat outputDocumentFormat)
		throws Exception {

		long start = System.currentTimeMillis();

		Future<?> future = _executorService.submit(
			() -> {
				try (UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
						new UnsyncByteArrayOutputStream()) {

					DocumentConverter documentConverter =
						_getDocumentConverter();

					documentConverter.convert(
						inputStream, inputDocumentFormat,
						unsyncByteArrayOutputStream, outputDocumentFormat);

					FileUtil.write(
						file, unsyncByteArrayOutputStream.unsafeGetByteArray(),
						0, unsyncByteArrayOutputStream.size());

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Converted from ",
								inputDocumentFormat.getName(), " to ",
								outputDocumentFormat.getName(), " in ",
								System.currentTimeMillis() - start, " ms"));
					}
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
				finally {
					try {
						inputStream.close();
					}
					catch (IOException ioException) {
						_log.error("Unable to close input stream", ioException);
					}
				}
			});

		try {
			long timeout = Math.max(
				PropsValues.
					DL_FILE_ENTRY_PREVIEW_GENERATION_TIMEOUT_GHOSTSCRIPT,
				PropsValues.DL_FILE_ENTRY_PREVIEW_GENERATION_TIMEOUT_PDFBOX);

			future.get(timeout, TimeUnit.SECONDS);
		}
		catch (TimeoutException timeoutException) {
			String errorMessage =
				"Timeout when converting for " + file.getPath();

			if (future.cancel(true)) {
				errorMessage += " resulted in a canceled timeout for " + future;
			}

			_log.error(errorMessage);

			throw timeoutException;
		}
	}

	private String _fixExtension(String extension) {
		if (extension.equals("htm")) {
			extension = "html";
		}

		return extension;
	}

	private DocumentConverter _getDocumentConverter() {
		if ((_openOfficeConnection != null) && (_documentConverter != null)) {
			return _documentConverter;
		}

		String host = _openOfficeConfiguration.serverHost();
		int port = _openOfficeConfiguration.serverPort();

		if (_isRemoteOpenOfficeHost(host)) {
			_openOfficeConnection = new SocketOpenOfficeConnection(host, port);

			_documentConverter = new StreamOpenOfficeDocumentConverter(
				_openOfficeConnection);
		}
		else {
			_openOfficeConnection = new SocketOpenOfficeConnection(port);

			_documentConverter = new OpenOfficeDocumentConverter(
				_openOfficeConnection);
		}

		return _documentConverter;
	}

	private boolean _isRemoteOpenOfficeHost(String host) {
		if (Validator.isNotNull(host) && !host.equals(_LOCALHOST_IP) &&
			!host.startsWith(_LOCALHOST)) {

			return true;
		}

		return false;
	}

	private void _validate(String targetExtension, String id) {
		if (!Validator.isFileExtension(targetExtension)) {
			throw new SystemException("Invalid extension: " + targetExtension);
		}

		if (!Validator.isFileName(id)) {
			throw new SystemException("Invalid file name: " + id);
		}
	}

	private static final String[] _COMPARABLE_FILE_EXTENSIONS =
		PropsValues.DL_COMPARABLE_FILE_EXTENSIONS;

	private static final String[] _DEFAULT_CONVERSIONS = new String[0];

	private static final String _LOCALHOST = "localhost";

	private static final String _LOCALHOST_IP = "127.0.0.1";

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentConversionImpl.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private CompanyLocalService _companyLocalService;

	private DocumentConverter _documentConverter;
	private volatile ExecutorService _executorService;
	private volatile OpenOfficeConfiguration _openOfficeConfiguration;
	private OpenOfficeConnection _openOfficeConnection;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private static class ConversionsHolder {

		public static String[] getConversions(String extension) {
			return _conversionsMap.get(extension);
		}

		private static void _populateConversionsMap(String documentFamily) {
			Filter filter = new Filter(documentFamily);

			DocumentFormatRegistry documentFormatRegistry =
				new LiferayDocumentFormatRegistry();

			String[] sourceExtensions = PropsUtil.getArray(
				PropsKeys.OPENOFFICE_CONVERSION_SOURCE_EXTENSIONS, filter);
			String[] targetExtensions = PropsUtil.getArray(
				PropsKeys.OPENOFFICE_CONVERSION_TARGET_EXTENSIONS, filter);

			for (String sourceExtension : sourceExtensions) {
				DocumentFormat sourceDocumentFormat =
					documentFormatRegistry.getFormatByFileExtension(
						sourceExtension);

				if (sourceDocumentFormat == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Invalid source extension " + sourceExtension);
					}

					continue;
				}

				List<String> conversions = new SortedArrayList<>();

				for (String targetExtension : targetExtensions) {
					DocumentFormat targetDocumentFormat =
						documentFormatRegistry.getFormatByFileExtension(
							targetExtension);

					if (targetDocumentFormat == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Invalid target extension " +
									targetDocumentFormat);
						}

						continue;
					}

					if (sourceDocumentFormat.isExportableTo(
							targetDocumentFormat)) {

						conversions.add(targetExtension);
					}
				}

				if (conversions.isEmpty()) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"There are no conversions supported from " +
								sourceExtension);
					}
				}
				else {
					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Conversions supported from ", sourceExtension,
								" to ", conversions));
					}

					_conversionsMap.put(
						sourceExtension, conversions.toArray(new String[0]));
				}
			}
		}

		private static final Map<String, String[]> _conversionsMap =
			new HashMap<>();

		static {
			_populateConversionsMap("drawing");
			_populateConversionsMap("presentation");
			_populateConversionsMap("spreadsheet");
			_populateConversionsMap("text");
		}

	}

}