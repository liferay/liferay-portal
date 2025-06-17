/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.pmd;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ci.AutoBalanceTestCase;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.SourceLanguage;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class PMDTest extends AutoBalanceTestCase {

	@Before
	public void setUp() throws IOException {
		try (FileReader fileReader = new FileReader(
				new File(_PROJECT_DIR, "build.properties"))) {

			_buildProperties.load(fileReader);
		}

		_pmdTask.setProject(new Project());
		_pmdTask.setShortFilenames(true);
		_pmdTask.setThreads(
			GetterUtil.get(_buildProperties.getProperty("pmd.threads"), 2));

		FileSet fileSet = new FileSet();

		fileSet.setDir(new File(_PROJECT_DIR));
		fileSet.setIncludes(_buildProperties.getProperty("pmd.java.includes"));
		fileSet.setExcludes(_buildProperties.getProperty("pmd.java.excludes"));

		if (isCIMode()) {
			DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(
				_pmdTask.getProject());

			String[] slicedIncludedFiles = slice(
				directoryScanner.getIncludedFiles());

			fileSet = new FileSet();

			fileSet.setDir(new File(_PROJECT_DIR));
			fileSet.setIncludes(
				StringUtil.merge(slicedIncludedFiles, StringPool.COMMA));
		}

		_pmdTask.addFileset(fileSet);

		Formatter formatter = new Formatter();

		_logFilePath = Files.createTempFile(null, null);

		formatter.setToFile(_logFilePath.toFile());

		formatter.setType("text");

		_pmdTask.addFormatter(formatter);

		_pmdTask.setRuleSetFiles(
			StringUtil.replace(
				_buildProperties.getProperty("pmd.java.ruleset"), "${sdk.dir}",
				_PROJECT_DIR + "/tools/sdk"));
	}

	@After
	public void tearDown() throws IOException {
		Files.delete(_logFilePath);
	}

	@Ignore
	@Test
	public void testPMDJava() throws IOException {
		SourceLanguage sourceLanguage = new SourceLanguage();

		sourceLanguage.setName("java");
		sourceLanguage.setVersion(
			_buildProperties.getProperty("ant.build.javac.source"));

		_pmdTask.addConfiguredSourceLanguage(sourceLanguage);

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				ClassTypeResolver.class.getName(), Level.SEVERE)) {

			_pmdTask.execute();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			if (!logEntries.isEmpty()) {
				AssertionError assertionError = new AssertionError(
					"PMD Java log error");

				for (LogEntry logEntry : logEntries) {
					assertionError.addSuppressed(
						new Throwable(
							logEntry.getMessage(), logEntry.getThrowable()));
				}

				throw assertionError;
			}

			List<String> list = Files.readAllLines(
				_logFilePath, Charset.defaultCharset());

			Assert.assertTrue(list.toString(), list.isEmpty());
		}
	}

	private static final String _PROJECT_DIR = "../../../";

	private final Properties _buildProperties = new Properties();
	private Path _logFilePath;
	private final PMDTask _pmdTask = new PMDTask();

}