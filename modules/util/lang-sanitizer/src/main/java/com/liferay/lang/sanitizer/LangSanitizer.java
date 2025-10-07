/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.lang.sanitizer;

import com.liferay.lang.sanitizer.util.ArgumentsUtil;
import com.liferay.lang.sanitizer.util.EscapeUtil;
import com.liferay.petra.string.StringBundler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;

/**
 * @author Seiphon Wang
 */
public class LangSanitizer {

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();

		LangSanitizer langSanitizer = new LangSanitizer();

		langSanitizer.sanitize(
			ArgumentsUtil.getValue(args, "source.base.dir", "./"));

		long endTime = System.currentTimeMillis();

		Collections.sort(_sanitizedMessage, new SanitizedMessageComparator());

		for (int i = 0; i < _sanitizedMessage.size(); i++) {
			System.out.println((i + 1) + ": " + _sanitizedMessage.get(i));
		}

		System.out.println(
			"Total time: " + ((endTime - startTime) / 1000) + "s");
	}

	public LangSanitizer() throws Exception {
		ClassLoader classLoader = LangSanitizer.class.getClassLoader();

		_policy = Policy.getInstance(
			classLoader.getResourceAsStream("sanitizer-configuration.xml"));
	}

	public void sanitize(String baseDirName) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		List<Future<List<SanitizedMessage>>> futures =
			new CopyOnWriteArrayList<>();

		for (File file : _getPropertiesFiles(baseDirName)) {
			Future<List<SanitizedMessage>> future = executorService.submit(
				new Callable<List<SanitizedMessage>>() {

					@Override
					public List<SanitizedMessage> call() throws Exception {
						return _sanitizeProperties(file);
					}

				});

			futures.add(future);
		}

		for (Future<List<SanitizedMessage>> future : futures) {
			_sanitizedMessage.addAll(future.get());
		}

		executorService.shutdown();

		while (!executorService.isTerminated()) {
			Thread.sleep(20);
		}
	}

	private String _getMessage(String sentence1, String sentence2) {
		String[] words1 = sentence1.split("\\s+");
		String[] words2 = sentence2.split("\\s+");

		List<String> differentWords = new ArrayList<>();

		for (String word : words1) {
			List<String> wordList = Arrays.asList(words2);

			if (!wordList.contains(word)) {
				differentWords.add(word);
			}
		}

		Set<String> escapedCharacters = EscapeUtil.getEscapedCharacters();

		for (String character : escapedCharacters) {
			String unexpectedChar = character.substring(
				0, character.length() - 1);

			for (String word : differentWords) {
				if (word.contains(unexpectedChar) &&
					!word.contains(character)) {

					return "A ';' is expected after " + unexpectedChar;
				}
			}
		}

		for (String word : words2) {
			List<String> wordList = Arrays.asList(words1);

			if (!wordList.contains(word)) {
				differentWords.add(word);
			}
		}

		return String.join(", ", differentWords);
	}

	private List<File> _getPropertiesFiles(String baseDirName)
		throws Exception {

		List<File> files = new ArrayList<>();

		Files.walkFileTree(
			Paths.get(baseDirName),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					List<String> skipDirNames = Arrays.asList(_SKIP_DIR_NAMES);

					if (dirName.startsWith(".") ||
						skipDirNames.contains(dirName)) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path file, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(file.getFileName());

					if (fileName.endsWith(".properties") &&
						(fileName.startsWith("bundle") ||
						 fileName.startsWith("Language"))) {

						files.add(file.toFile());
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return files;
	}

	private SanitizedMessage _sanitizeContent(
			File file, String key, String originalValue)
		throws Exception {

		AntiSamy antiSamy = new AntiSamy();

		String sanitizedValue = null;

		try {
			CleanResults cleanResults = antiSamy.scan(originalValue, _policy);

			sanitizedValue = EscapeUtil.unescape(cleanResults.getCleanHTML());
		}
		catch (ScanException scanException) {
			String errorCode = "INVALID_CHARACTER_ERR";
			String errorMessage = scanException.getMessage();

			if (errorMessage.contains(errorCode)) {
				StringBundler sb = new StringBundler(3);

				sb.append(
					errorMessage.substring(
						errorMessage.indexOf(errorCode) + errorCode.length() +
							2));
				sb.append(" Please check: ");
				sb.append(
					_getMessage(
						originalValue, EscapeUtil.escapeTag(originalValue)));

				errorMessage = sb.toString();
			}

			return new SanitizedMessage(
				file.getAbsolutePath(), key, errorMessage, originalValue,
				EscapeUtil.escapeTag(originalValue));
		}

		if (!sanitizedValue.equals(
				EscapeUtil.formatTag(EscapeUtil.unescape(originalValue)))) {

			return new SanitizedMessage(
				file.getAbsolutePath(), key,
				_getMessage(
					originalValue, EscapeUtil.unescapeTag(sanitizedValue)),
				originalValue, EscapeUtil.unescapeTag(sanitizedValue));
		}

		return null;
	}

	private List<SanitizedMessage> _sanitizeProperties(File file)
		throws Exception {

		List<SanitizedMessage> sanitizedMessages = new ArrayList<>();

		Properties properties = new Properties();

		if (file.exists()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				properties.load(fileInputStream);
			}
		}

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			SanitizedMessage sanitizedMessage = _sanitizeContent(
				file, (String)entry.getKey(), (String)entry.getValue());

			if (sanitizedMessage != null) {
				sanitizedMessages.add(sanitizedMessage);
			}
		}

		return sanitizedMessages;
	}

	private static final String[] _SKIP_DIR_NAMES = {
		"bin", "build", "classes", "dependencies", "node_modules",
		"node_modules_cache", "sql", "test-classes", "test-coverage",
		"test-results", "tmp"
	};

	private static final List<SanitizedMessage> _sanitizedMessage =
		new CopyOnWriteArrayList<>();

	private final Policy _policy;

	private static class SanitizedMessage
		implements Comparable<SanitizedMessage> {

		public SanitizedMessage(
			String fileName, String languageKey, String message,
			String originalContent, String santizedContent) {

			_fileName = fileName;
			_languageKey = languageKey;
			_message = message;
			_originalContent = originalContent;
			_santizedContent = santizedContent;
		}

		@Override
		public int compareTo(SanitizedMessage sanitizedMessage) {
			if (!_fileName.equals(sanitizedMessage.getFileName())) {
				return _fileName.compareTo(sanitizedMessage.getFileName());
			}

			if (!_languageKey.equals(sanitizedMessage.getLanguageKey())) {
				return _languageKey.compareTo(
					sanitizedMessage.getLanguageKey());
			}

			return 0;
		}

		public String getFileName() {
			return _fileName;
		}

		public String getLanguageKey() {
			return _languageKey;
		}

		@Override
		public String toString() {
			StringBundler sb = new StringBundler(14);

			sb.append("File: ");
			sb.append(_fileName);
			sb.append(System.lineSeparator());
			sb.append("\tKey: ");
			sb.append(_languageKey);
			sb.append(System.lineSeparator());
			sb.append("\tOriginal Content: ");
			sb.append(_originalContent);
			sb.append(System.lineSeparator());
			sb.append("\tSanitized Content: ");
			sb.append(_santizedContent);

			if (Objects.nonNull(_message)) {
				sb.append(System.lineSeparator());
				sb.append("\tMessage: ");
				sb.append(_message);
			}

			return sb.toString();
		}

		private final String _fileName;
		private final String _languageKey;
		private final String _message;
		private final String _originalContent;
		private final String _santizedContent;

	}

	private static class SanitizedMessageComparator
		implements Comparator<SanitizedMessage> {

		@Override
		public int compare(
			SanitizedMessage sanitizedMessage1,
			SanitizedMessage sanitizedMessage2) {

			return sanitizedMessage1.compareTo(sanitizedMessage2);
		}

	}

}