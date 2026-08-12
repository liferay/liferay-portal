/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.TextExtractor;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class TextExtractorPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class<?> clazz = TextExtractorPerformanceTest.class;

		Properties properties = PropertiesUtil.load(
			clazz.getResourceAsStream(
				"dependencies/text-extractor-performance.properties"),
			"UTF-8");

		_executorService = Executors.newFixedThreadPool(
			GetterUtil.getInteger(
				properties.getProperty("text.extractor.thread.count")));
		_filesCount = GetterUtil.getInteger(
			properties.getProperty("text.extractor.files.count"));
		_iterations = GetterUtil.getInteger(
			properties.getProperty("text.extractor.iterations"));
		_fileSize = GetterUtil.getInteger(
			properties.getProperty("text.extractor.file.size"));

		String logFile = properties.getProperty("text.extractor.log.file");

		if (Validator.isNotNull(logFile)) {
			_logFilePath = Paths.get(logFile);
		}
	}

	@AfterClass
	public static void tearDownClass() {
		_executorService.shutdown();
	}

	@Test
	public void testCsv() throws Exception {
		_test(
			"csv",
			outputStream -> {
				int size = _writeStrings(
					outputStream, "id,name,value,description\n");

				while (size < _fileSize) {
					size += _writeStrings(
						outputStream, _generateRandomString(), StringPool.COMMA,
						_generateRandomString(), StringPool.COMMA,
						_generateRandomString(), StringPool.COMMA,
						_generateRandomString(), StringPool.NEW_LINE);
				}
			});
	}

	@Test
	public void testHtml() throws Exception {
		String[] tags = {
			"p", "h1", "h2", "h3", "h4", "blockquote", "pre", "code", "div",
			"span", "em", "strong", "b", "i"
		};

		_test(
			"html",
			outputStream -> {
				int size = _writeStrings(
					outputStream,
					"<!DOCTYPE html>\n<html><head><title>Test</title>",
					"</head>\n<body>\n");

				while (size < _fileSize) {
					String tag = tags[_random.nextInt(tags.length)];

					size += _writeStrings(
						outputStream, "<", tag, ">", _generateRandomString(),
						"</", tag, ">\n");
				}

				_writeStrings(outputStream, "</body></html>\n");
			});
	}

	@Test
	public void testJson() throws Exception {
		_test(
			"json",
			outputStream -> {
				int size = _writeStrings(outputStream, "{\n");

				for (int i = 0; size < _fileSize; i++) {
					if (i > 0) {
						size += _writeStrings(outputStream, ",\n");
					}

					size += _writeStrings(
						outputStream,
						StringBundler.concat(
							"{\"id\": ", i, ", \"text\": \"",
							_generateRandomString(), "\"}"));
				}

				_writeStrings(outputStream, "\n}\n");
			});
	}

	@Test
	public void testRtf() throws Exception {
		_test(
			"rtf",
			outputStream -> {
				int size = _writeStrings(
					outputStream,
					"{\\rtf1\\ansi\\deff0{\\fonttbl{\\f0 Arial;}}\n");

				while (size < _fileSize) {
					size += _writeStrings(
						outputStream, _generateRandomString(), "\\par\n");
				}

				_writeStrings(outputStream, "}");
			});
	}

	@Test
	public void testTxt() throws Exception {
		_test(
			"txt",
			outputStream -> {
				int size = 0;

				while (size < _fileSize) {
					size += _writeStrings(
						outputStream, _generateRandomString(),
						StringPool.NEW_LINE);
				}
			});
	}

	@Test
	public void testXml() throws Exception {
		_test(
			"xml",
			outputStream -> {
				int size = _writeStrings(
					outputStream,
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<docs>\n");

				while (size < _fileSize) {
					size += _writeStrings(
						outputStream, "<doc id=\"", _generateRandomString(),
						"\">", _generateRandomString(), "</doc>\n");
				}

				_writeStrings(outputStream, "</docs>\n");
			});
	}

	private String _generateRandomString() {
		StringBundler sb = new StringBundler(1024);

		for (int i = 0; i < sb.capacity(); i++) {
			sb.append(_CHARS[_random.nextInt(_CHARS.length)]);
		}

		return sb.toString();
	}

	private void _test(
			String format,
			UnsafeConsumer<OutputStream, Exception> unsafeConsumer)
		throws Exception {

		List<Path> filePaths = new ArrayList<>();

		Path tempPath = Files.createTempDirectory(null);

		try {
			for (int i = 0; i < _filesCount; i++) {
				Path filePath = tempPath.resolve(
					StringBundler.concat(i, StringPool.PERIOD, format));

				try (OutputStream outputStream = new BufferedOutputStream(
						Files.newOutputStream(filePath))) {

					unsafeConsumer.accept(outputStream);

					filePaths.add(filePath);
				}
			}

			for (int i = 1; i <= _iterations; i++) {
				try (PerformanceTimer performanceTimer = new PerformanceTimer(
						_logFilePath, Long.MAX_VALUE,
						StringBundler.concat(
							format, " (Iteration ", i, ", ", _filesCount,
							" files x ", _fileSize, " bytes)"))) {

					List<Future<Void>> futures = new ArrayList<>(
						filePaths.size());

					for (Path filePath : filePaths) {
						Future<Void> future = _executorService.submit(
							() -> {
								try (InputStream inputStream =
										Files.newInputStream(filePath)) {

									_textExtractor.extractText(inputStream, -1);
								}

								return null;
							});

						futures.add(future);
					}

					for (Future<Void> future : futures) {
						future.get();
					}
				}
			}
		}
		finally {
			for (Path filePath : filePaths) {
				Files.deleteIfExists(filePath);
			}

			Files.deleteIfExists(tempPath);
		}
	}

	private int _writeStrings(OutputStream outputStream, String... strings)
		throws Exception {

		int size = 0;

		for (String string : strings) {
			byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

			outputStream.write(bytes);

			size += bytes.length;
		}

		return size;
	}

	private static final char[] _CHARS = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
		'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z', '_', '-', ' '
	};

	private static ExecutorService _executorService;
	private static int _filesCount;
	private static int _fileSize;
	private static int _iterations;
	private static Path _logFilePath;
	private static final Random _random = new Random(42L);

	@Inject
	private TextExtractor _textExtractor;

}