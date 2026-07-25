/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.internal.extract;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncBufferedInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.TextExtractor;
import com.liferay.portal.tika.internal.configuration.helper.TikaConfigurationHelper;
import com.liferay.portal.tika.internal.util.ProcessConfigUtil;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.Charset;

import java.util.Objects;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.txt.UniversalEncodingDetector;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author Shuyang Zhou
 */
@Component(service = TextExtractor.class)
public class TextExtractorImpl implements TextExtractor {

	@Override
	public String extractText(InputStream inputStream, int maxStringLength) {
		if (maxStringLength == 0) {
			return StringPool.BLANK;
		}

		String text = null;

		try {
			Tika tika = new Tika(_tikaConfigurationHelper.getTikaConfig());

			tika.setMaxStringLength(maxStringLength);

			if (!inputStream.markSupported()) {
				inputStream = new UnsyncBufferedInputStream(inputStream);
			}

			if (_tikaConfigurationHelper.useForkProcess(
					tika.detect(inputStream))) {

				InputStream finalInputStream = inputStream;

				ProcessChannel<String> processChannel =
					_processExecutor.execute(
						ProcessConfigUtil.getProcessConfig(),
						new ExtractTextProcessCallable(
							tika.getParser(), tika.getDetector(),
							tika.getMaxStringLength(),
							StreamUtil.toByteArray(finalInputStream)));

				Future<String> future =
					processChannel.getProcessNoticeableFuture();

				text = future.get();
			}
			else {
				text = _parseToString(
					tika.getParser(), tika.getDetector(),
					tika.getMaxStringLength(), inputStream);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Extracted text: " + text);
		}

		return text;
	}

	private static String _parseToString(
			Parser parser, Detector detector, int maxStringLength,
			InputStream inputStream)
		throws IOException, TikaException {

		inputStream.mark(1);

		try {
			if (inputStream.read() == -1) {
				return StringPool.BLANK;
			}
		}
		finally {
			inputStream.reset();
		}

		UniversalEncodingDetector universalEncodingDetector =
			new UniversalEncodingDetector();

		Metadata metadata = new Metadata();

		Charset charset = universalEncodingDetector.detect(
			inputStream, metadata);

		String contentEncoding = StringPool.BLANK;

		if (charset != null) {
			contentEncoding = charset.name();
		}

		if (!contentEncoding.equals(StringPool.BLANK)) {
			metadata.set("Content-Encoding", contentEncoding);
			metadata.set(
				"Content-Type", "text/plain; charset=" + contentEncoding);
		}

		WriteOutContentHandler writeOutContentHandler =
			new WriteOutContentHandler(maxStringLength);

		try {
			ParseContext parseContext = new ParseContext();

			parseContext.set(
				EmbeddedDocumentExtractor.class,
				new ParsingEmbeddedDocumentExtractor(parseContext) {

					@Override
					public void parseEmbedded(
							InputStream inputStream,
							ContentHandler contentHandler, Metadata metadata,
							boolean outputHtml)
						throws IOException, SAXException {

						if (!inputStream.markSupported()) {
							inputStream = new UnsyncBufferedInputStream(
								inputStream);
						}

						MediaType mediaType = detector.detect(
							inputStream, new Metadata());

						if (Objects.equals(
								ContentTypes.IMAGE_PNG, mediaType.toString())) {

							return;
						}

						super.parseEmbedded(
							inputStream, contentHandler, metadata, outputHtml);
					}

				});
			parseContext.set(Parser.class, parser);

			parser.parse(
				inputStream, new BodyContentHandler(writeOutContentHandler),
				metadata, parseContext);
		}
		catch (SAXException saxException) {
			if (!WriteLimitReachedException.isWriteLimitReached(saxException)) {
				throw new TikaException(
					saxException.getMessage(), saxException);
			}
		}
		finally {
			inputStream.close();
		}

		return writeOutContentHandler.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TextExtractorImpl.class);

	@Reference
	private ProcessExecutor _processExecutor;

	@Reference
	private TikaConfigurationHelper _tikaConfigurationHelper;

	private static class ExtractTextProcessCallable
		implements ProcessCallable<String> {

		@Override
		public String call() throws ProcessException {
			if (ArrayUtil.isEmpty(_data)) {
				return StringPool.BLANK;
			}

			Logger logger = Logger.getLogger(
				"org.apache.tika.parsers.PDFParser");

			logger.setLevel(Level.SEVERE);

			try {
				return _parseToString(
					_parser, _detector, _maxStringLength,
					new UnsyncByteArrayInputStream(_data));
			}
			catch (Exception exception) {
				throw new ProcessException(exception);
			}
		}

		private ExtractTextProcessCallable(
			Parser parser, Detector detector, int maxStringLength,
			byte[] data) {

			_parser = parser;
			_detector = detector;
			_maxStringLength = maxStringLength;
			_data = data;
		}

		private static final long serialVersionUID = 1L;

		private final byte[] _data;
		private final Detector _detector;
		private final int _maxStringLength;
		private final Parser _parser;

	}

}