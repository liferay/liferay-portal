/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.source.formatter.SourceFormatter;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Hugo Huijser
 */
public abstract class BaseSourceProcessorTestCase {

	@BeforeClass
	public static void setUpClass() {
		StringBundler sb = new StringBundler(5);

		sb.append(SystemProperties.get(SystemProperties.TMP_DIR));
		sb.append(StringPool.SLASH);
		sb.append(StringUtil.randomString());
		sb.append(StringPool.SLASH);

		_temporaryRootFolder = new File(sb.toString());

		sb.append(_DIR_NAME);

		_temporaryFolder = new File(sb.toString());
	}

	@AfterClass
	public static void tearDownClass() throws IOException {
		FileUtils.deleteDirectory(_temporaryRootFolder);
	}

	protected SourceFormatterArgs getSourceFormatterArgs() {
		SourceFormatterArgs sourceFormatterArgs = new SourceFormatterArgs();

		sourceFormatterArgs.setAutoFix(true);
		sourceFormatterArgs.setFailOnAutoFix(false);
		sourceFormatterArgs.setFailOnHasWarning(false);
		sourceFormatterArgs.setPrintErrors(false);

		return sourceFormatterArgs;
	}

	protected void test(
			SourceProcessorTestParameters sourceProcessorTestParameters)
		throws Exception {

		File newFile = _generateTempFile(
			sourceProcessorTestParameters.getFileName());

		for (String dependentFileName :
				sourceProcessorTestParameters.getDependentFileNames()) {

			_generateTempFile(dependentFileName);
		}

		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		sourceFormatterArgs.setFileNames(
			Collections.singletonList(newFile.getAbsolutePath()));

		SourceFormatter sourceFormatter = new SourceFormatter(
			sourceFormatterArgs);

		sourceFormatter.format();

		List<String> modifiedFileNames = sourceFormatter.getModifiedFileNames();

		if (modifiedFileNames.isEmpty()) {
			throw new IllegalArgumentException(
				"The file name " + newFile.getAbsolutePath() +
					" does not end with a valid extension");
		}

		String actualFormattedContent = null;

		String expectedFileName =
			sourceProcessorTestParameters.getExpectedFileName();

		if (expectedFileName != null) {
			actualFormattedContent = FileUtil.read(
				new File(
					_temporaryFolder,
					StringUtil.replace(expectedFileName, ".test", ".")));

			expectedFileName = StringBundler.concat(
				_DIR_NAME, "/expected/", expectedFileName);
		}
		else {
			expectedFileName = StringBundler.concat(
				_DIR_NAME, "/expected/",
				sourceProcessorTestParameters.getFileName());
		}

		URL expectedURL = classLoader.getResource(expectedFileName);

		List<SourceFormatterMessage> sourceFormatterMessages =
			ListUtil.fromCollection(
				sourceFormatter.getSourceFormatterMessages());

		List<String> expectedMessages =
			sourceProcessorTestParameters.getExpectedMessages();

		if (!(sourceFormatterMessages.isEmpty() &&
			  expectedMessages.isEmpty())) {

			if (expectedURL != null) {
				_checkExpectedContent(
					actualFormattedContent, expectedFileName,
					modifiedFileNames);
			}

			List<String> checkCategoryNames =
				sourceFormatterArgs.getCheckCategoryNames();

			if (checkCategoryNames.contains("Upgrade")) {
				sourceFormatterMessages = ListUtil.sort(
					sourceFormatterMessages,
					Comparator.comparing(SourceFormatterMessage::getMessage));
			}

			_checkExpectedMessages(
				expectedMessages, newFile, sourceFormatterMessages,
				sourceProcessorTestParameters);
		}
		else {
			_checkExpectedContent(
				actualFormattedContent, expectedFileName, modifiedFileNames);
		}
	}

	protected void test(String fileName) throws Exception {
		test(SourceProcessorTestParameters.create(fileName));
	}

	protected void test(String fileName, String expectedMessage)
		throws Exception {

		test(
			SourceProcessorTestParameters.create(
				fileName
			).addExpectedMessage(
				expectedMessage, -1
			));
	}

	protected void test(String fileName, String expectedMessage, int lineNumber)
		throws Exception {

		test(
			SourceProcessorTestParameters.create(
				fileName
			).addExpectedMessage(
				expectedMessage, lineNumber
			));
	}

	protected void test(String fileName, String[] expectedMessages)
		throws Exception {

		Arrays.sort(expectedMessages);

		test(
			SourceProcessorTestParameters.create(
				fileName
			).addExpectedMessages(
				expectedMessages
			));
	}

	protected final ClassLoader classLoader =
		BaseSourceProcessorTestCase.class.getClassLoader();

	private static void _checkExpectedMessages(
		List<String> expectedMessages, File newFile,
		List<SourceFormatterMessage> sourceFormatterMessages,
		SourceProcessorTestParameters sourceProcessorTestParameters) {

		Assert.assertEquals(
			sourceFormatterMessages.toString(), expectedMessages.size(),
			sourceFormatterMessages.size());

		for (int i = 0; i < sourceFormatterMessages.size(); i++) {
			SourceFormatterMessage sourceFormatterMessage =
				sourceFormatterMessages.get(i);

			Assert.assertEquals(
				expectedMessages.get(i), sourceFormatterMessage.getMessage());

			int lineNumber = sourceFormatterMessage.getLineNumber();

			if (lineNumber > -1) {
				List<Integer> lineNumbers =
					sourceProcessorTestParameters.getLineNumbers();

				if (Collections.frequency(lineNumbers, -1) ==
						lineNumbers.size()) {

					continue;
				}

				Assert.assertEquals(
					String.valueOf(lineNumbers.get(i)),
					String.valueOf(lineNumber));
			}

			String absolutePath = StringUtil.replace(
				newFile.getAbsolutePath(), CharPool.BACK_SLASH, CharPool.SLASH);

			Assert.assertEquals(
				absolutePath, sourceFormatterMessage.getFileName());
		}
	}

	private void _checkExpectedContent(
			String actualFormattedContent, String expectedFileName,
			List<String> modifiedFileNames)
		throws Exception {

		if (actualFormattedContent == null) {
			actualFormattedContent = FileUtil.read(
				new File(modifiedFileNames.get(0)));
		}

		URL expectedURL = classLoader.getResource(expectedFileName);

		if (expectedURL == null) {
			throw new FileNotFoundException(expectedFileName);
		}

		String expectedFormattedContent = IOUtils.toString(
			expectedURL, StringPool.UTF8);

		expectedFormattedContent = StringUtil.replace(
			expectedFormattedContent, StringPool.RETURN_NEW_LINE,
			StringPool.NEW_LINE);

		Assert.assertEquals(expectedFormattedContent, actualFormattedContent);
	}

	private File _generateTempFile(String fileName) throws Exception {
		int pos = fileName.lastIndexOf(CharPool.PERIOD);

		if (pos == -1) {
			throw new IllegalArgumentException(
				"The file name " + fileName +
					" does not end with a valid extension");
		}

		String originalExtension = fileName.substring(pos + 1);

		String extension = originalExtension;

		fileName = fileName.substring(0, pos);

		if (originalExtension.startsWith("test")) {
			extension = extension.substring(4);
		}

		String fullFileName = StringBundler.concat(
			_DIR_NAME, StringPool.SLASH, fileName, ".", originalExtension);

		URL url = classLoader.getResource(fullFileName);

		if (url == null) {
			throw new FileNotFoundException(fullFileName);
		}

		File newFile = new File(_temporaryFolder, fileName + "." + extension);

		try (InputStream inputStream = url.openStream()) {
			FileUtils.copyInputStreamToFile(inputStream, newFile);
		}

		return newFile;
	}

	private static final String _DIR_NAME =
		"com/liferay/source/formatter/dependencies";

	private static File _temporaryFolder;
	private static File _temporaryRootFolder;

}