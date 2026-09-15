/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.elements.PoshiElement;
import com.liferay.poshi.core.elements.PoshiNodeFactory;
import com.liferay.poshi.core.script.PoshiScriptParserException;
import com.liferay.poshi.core.util.FileUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.DebugUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public class PoshiSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws IOException {
		List<String> fileNames = getFileNames(new String[0], getIncludes());

		Iterator<String> iterator = fileNames.iterator();

		while (iterator.hasNext()) {
			String fileName = iterator.next();

			if (!fileName.endsWith(".jar") && !fileName.endsWith(".lar") &&
				!fileName.endsWith(".war") && !fileName.endsWith(".zip")) {

				continue;
			}

			if (((fileName.contains("/modules/") &&
				  !fileName.contains("/modules/apps/static/") &&
				  !fileName.contains("/modules/sdk/") &&
				  !fileName.contains("/modules/util/")) ||
				 fileName.contains("/portal-web/")) &&
				(fileName.contains("/test/") || fileName.contains("/tests/")) &&
				fileName.contains("/dependencies/") &&
				!fileName.contains("/testIntegration/")) {

				processMessage(
					fileName,
					"Do not add archive files for tests, they must be " +
						"expanded");
			}

			iterator.remove();
		}

		return fileNames;
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected String parse(
			File file, String fileName, String content,
			Set<String> modifiedMessages)
		throws Exception {

		if (SourceUtil.isXML(content) || fileName.endsWith(".path")) {
			return content;
		}

		_populateFunctionAndMacroFiles();

		System.out.flush();

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		PrintStream printStream = new PrintStream(unsyncByteArrayOutputStream);

		System.setOut(printStream);

		PoshiElement poshiElement = null;

		try {
			poshiElement = (PoshiElement)PoshiNodeFactory.newPoshiNode(
				content, FileUtil.getURL(file));
		}
		finally {
			System.out.flush();

			FileOutputStream fileOutputStream = new FileOutputStream(
				FileDescriptor.out);

			printStream = new PrintStream(fileOutputStream);

			System.setOut(printStream);
		}

		PoshiScriptParserException.throwExceptions(
			SourceUtil.getAbsolutePath(file));

		String newContent = poshiElement.toPoshiScript();

		if (!content.equals(newContent)) {
			modifiedMessages.add(file.toString() + " (PoshiParser)");

			SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

			if (sourceFormatterArgs.isShowDebugInformation()) {
				DebugUtil.printContentModifications(
					"PoshiParser", fileName, content, newContent);
			}
		}

		return newContent;
	}

	private synchronized void _populateFunctionAndMacroFiles()
		throws Exception {

		if (_populated) {
			return;
		}

		List<File> poshiDirs = new ArrayList<>();

		if (isPortalSource()) {
			poshiDirs.add(getPortalDir());
		}
		else {
			poshiDirs.addAll(PoshiContext.getPoshiDirs());
		}

		for (File poshiDir : poshiDirs) {
			Files.walkFileTree(
				poshiDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path dirPath,
							BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (ArrayUtil.contains(
								_SKIP_DIR_NAMES,
								String.valueOf(dirPath.getFileName()))) {

							return FileVisitResult.SKIP_SUBTREE;
						}

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
						Path filePath,
						BasicFileAttributes basicFileAttributes) {

						String absolutePath = SourceUtil.getAbsolutePath(
							filePath);

						if (absolutePath.endsWith(".function")) {
							PoshiContext.setFunctionFileNames(
								absolutePath.replaceFirst(
									".+/(.+)\\.function", "$1"));
						}
						else if (absolutePath.endsWith(".macro")) {
							PoshiContext.setMacroFileNames(
								absolutePath.replaceFirst(
									".+/(.+)\\.macro", "$1"));
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}

		_populated = true;
	}

	private static final String[] _INCLUDES = {
		"**/*.function", "**/*.jar", "**/*.lar", "**/*.macro", "**/*.path",
		"**/*.testcase", "**/*.war", "**/*.zip"
	};

	private static final String[] _SKIP_DIR_NAMES = {
		".git", ".gradle", ".idea", ".m2", ".releng", ".settings", "bin",
		"build", "classes", "node_modules", "node_modules_cache", "poshi",
		"sdk", "third-party"
	};

	private static boolean _populated;

}