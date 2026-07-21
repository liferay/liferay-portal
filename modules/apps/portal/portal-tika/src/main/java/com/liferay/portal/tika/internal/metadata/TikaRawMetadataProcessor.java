/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.internal.metadata;

import com.drew.imaging.png.PngMetadataReader;
import com.drew.metadata.png.PngDirectory;

import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tika.internal.configuration.helper.TikaConfigurationHelper;
import com.liferay.portal.tika.internal.util.ProcessConfigUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Miguel Pastor
 * @author Alexander Chow
 * @author Shuyang Zhou
 */
@Component(service = RawMetadataProcessor.class)
public class TikaRawMetadataProcessor implements RawMetadataProcessor {

	@Override
	public Map<String, String[]> getRawMetadata(
			String mimeType, InputStream inputStream)
		throws PortalException {

		Metadata metadata = _extractMetadata(mimeType, inputStream);

		if (metadata == null) {
			return new HashMap<>();
		}

		Map<String, String[]> rawMetadata = new HashMap<>();

		for (String name : metadata.names()) {
			rawMetadata.put(name, metadata.getValues(name));
		}

		return rawMetadata;
	}

	private Metadata _extractMetadata(
		String mimeType, InputStream inputStream) {

		if (Objects.equals(mimeType, ContentTypes.IMAGE_PNG)) {
			return _getPNGMetadata(inputStream);
		}

		return _getMetadata(mimeType, inputStream);
	}

	private Metadata _getMetadata(String mimeType, InputStream inputStream) {
		Parser parser = new AutoDetectParser(
			_tikaConfigurationHelper.getTikaConfig());

		if (_tikaConfigurationHelper.useForkProcess(mimeType)) {
			File file = FileUtil.createTempFile();

			try {
				FileUtil.write(file, inputStream);

				if (file.length() == 0) {
					return null;
				}

				ExtractMetadataProcessCallable extractMetadataProcessCallable =
					new ExtractMetadataProcessCallable(file, parser);

				ProcessChannel<Metadata> processChannel =
					_processExecutor.execute(
						ProcessConfigUtil.getProcessConfig(),
						extractMetadataProcessCallable);

				Future<Metadata> future =
					processChannel.getProcessNoticeableFuture();

				return _postProcessMetadata(mimeType, future.get());
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
			finally {
				file.delete();
			}
		}

		try {
			return _postProcessMetadata(
				mimeType,
				ExtractMetadataProcessCallable._extractMetadata(
					inputStream, parser));
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private Metadata _getPNGMetadata(InputStream inputStream) {
		Metadata metadata = new Metadata();

		try {
			com.drew.metadata.Metadata inputStreamMetadata =
				PngMetadataReader.readMetadata(inputStream);

			PngDirectory pngDirectory =
				inputStreamMetadata.getFirstDirectoryOfType(PngDirectory.class);

			if (pngDirectory == null) {
				return metadata;
			}

			if (pngDirectory.containsTag(PngDirectory.TAG_IMAGE_HEIGHT) &&
				pngDirectory.containsTag(PngDirectory.TAG_IMAGE_WIDTH)) {

				metadata.set(
					TIFF.IMAGE_LENGTH,
					pngDirectory.getInt(PngDirectory.TAG_IMAGE_HEIGHT));
				metadata.set(
					TIFF.IMAGE_WIDTH,
					pngDirectory.getInt(PngDirectory.TAG_IMAGE_WIDTH));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return metadata;
	}

	private Metadata _postProcessMetadata(String mimeType, Metadata metadata) {
		if (mimeType.equals(ContentTypes.IMAGE_SVG_XML) && (metadata != null)) {
			String contentType = metadata.get(HttpHeaders.CONTENT_TYPE);

			if (contentType.startsWith(ContentTypes.TEXT_PLAIN)) {
				metadata.set(
					HttpHeaders.CONTENT_TYPE,
					StringUtil.replace(
						contentType, ContentTypes.TEXT_PLAIN,
						ContentTypes.IMAGE_SVG_XML));
			}
		}

		if (mimeType.endsWith(ContentTypes.APPLICATION_JAVASCRIPT)) {
			String contentType = metadata.get(HttpHeaders.CONTENT_TYPE);

			if (contentType.startsWith(ContentTypes.TEXT_PLAIN)) {
				metadata.set(
					HttpHeaders.CONTENT_TYPE,
					StringUtil.replace(
						contentType, ContentTypes.TEXT_PLAIN,
						ContentTypes.APPLICATION_JAVASCRIPT));
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Extracted metadata: " + metadata);
		}

		return metadata;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TikaRawMetadataProcessor.class);

	@Reference
	private ProcessExecutor _processExecutor;

	@Reference
	private TikaConfigurationHelper _tikaConfigurationHelper;

	private static class ExtractMetadataProcessCallable
		implements ProcessCallable<Metadata> {

		@Override
		public Metadata call() throws ProcessException {
			Logger logger = Logger.getLogger(
				"org.apache.tika.parsers.PDFParser");

			logger.setLevel(Level.SEVERE);

			try (InputStream inputStream = new FileInputStream(_file)) {
				return _extractMetadata(inputStream, _parser);
			}
			catch (IOException ioException) {
				throw new ProcessException(ioException);
			}
		}

		private static Metadata _extractMetadata(
				InputStream inputStream, Parser parser)
			throws IOException {

			Metadata metadata = new Metadata();

			ParseContext parseContext = new ParseContext();

			parseContext.set(Parser.class, parser);

			try {
				parser.parse(
					inputStream, new DefaultHandler(), metadata, parseContext);
			}
			catch (SAXException | TikaException exception) {
				throw new IOException(exception);
			}

			// Remove potential security risks

			metadata.remove(XMPDM.ABS_PEAK_AUDIO_FILE_PATH.getName());
			metadata.remove(XMPDM.RELATIVE_PEAK_AUDIO_FILE_PATH.getName());

			return metadata;
		}

		private ExtractMetadataProcessCallable(File file, Parser parser) {
			_file = file;
			_parser = parser;
		}

		private static final long serialVersionUID = 1L;

		private final File _file;
		private final Parser _parser;

	}

}