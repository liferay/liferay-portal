/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.GitUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.check.util.JSPSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.checkstyle.util.CheckstyleLogger;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.checkstyle.util.JSPCheckstyleUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class JSPSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws Exception {
		String[] excludes = {
			"**/null.jsp", "**/templates/*.tpl", "**/tools/**", "**/*.xml.tpl"
		};

		List<String> fileNames = getFileNames(excludes, getIncludes());

		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		if (fileNames.isEmpty() ||
			((sourceFormatterArgs.getCommitCount() == 0) &&
			 !sourceFormatterArgs.isFormatCurrentBranch() &&
			 !sourceFormatterArgs.isFormatLatestAuthor() &&
			 !sourceFormatterArgs.isFormatLocalChanges())) {

			return fileNames;
		}

		List<String> allJSPFileNames = getFileNames(
			excludes, getIncludes(), true);

		Map<String, String> contentsMap = JSPSourceUtil.getContentsMap(
			allJSPFileNames);

		Map<String, String> deletedContentsMap = _getDeletedContentsMap(
			excludes);

		if (deletedContentsMap.isEmpty()) {
			return JSPSourceUtil.addIncludedAndReferencedFileNames(
				fileNames, new HashSet<String>(), contentsMap, ".*");
		}

		contentsMap.putAll(deletedContentsMap);
		fileNames.addAll(deletedContentsMap.keySet());

		fileNames = JSPSourceUtil.addIncludedAndReferencedFileNames(
			fileNames, new HashSet<String>(), contentsMap, ".*");

		fileNames.removeAll(deletedContentsMap.keySet());

		return fileNames;
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected File format(
			File file, String fileName, String absolutePath, String content)
		throws Exception {

		// When executing 'format-source-current-branch',
		// 'format-source-latest-author', or 'format-source-local-changes', we
		// add included and referenced file names in order to detect unused
		// imports or variable names. As a result, file names that are excluded
		// via source-formatter.properties#source.formatter.excludes are also
		// added to the list. Here we make sure we do not format files that
		// should be excluded.

		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		if ((sourceFormatterArgs.getCommitCount() > 0) ||
			sourceFormatterArgs.isFormatCurrentBranch() ||
			sourceFormatterArgs.isFormatLatestAuthor() ||
			sourceFormatterArgs.isFormatLocalChanges()) {

			List<String> fileNames = SourceFormatterUtil.filterFileNames(
				Arrays.asList(fileName), new String[0], new String[] {"*.*"},
				getSourceFormatterExcludes(), false);

			if (fileNames.isEmpty()) {
				return file;
			}
		}

		file = super.format(file, fileName, absolutePath, content);

		_processCheckstyle(absolutePath, FileUtil.read(file));

		return file;
	}

	@Override
	protected void postFormat() throws CheckstyleException, IOException {
		_processCheckstyle();

		for (SourceFormatterMessage sourceFormatterMessage :
				_sourceFormatterMessages) {

			String fileName = sourceFormatterMessage.getFileName();

			processMessage(fileName, sourceFormatterMessage);

			printError(fileName, sourceFormatterMessage.toString());
		}
	}

	@Override
	protected void preFormat() throws CheckstyleException {
		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		_checkstyleLogger = new CheckstyleLogger(
			sourceFormatterArgs.getBaseDirName());
		_checkstyleConfiguration = CheckstyleUtil.getConfiguration(
			"checkstyle-jsp.xml", getPropertiesMap(), sourceFormatterArgs);
	}

	private Map<String, String> _getDeletedContentsMap(String[] excludes)
		throws Exception {

		List<String> fileNames = Collections.emptyList();
		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		if (sourceFormatterArgs.getCommitCount() > 0) {
			fileNames = GitUtil.getModifiedFileNames(
				sourceFormatterArgs.getBaseDirName(),
				sourceFormatterArgs.getCommitCount(), true);
		}
		else if (sourceFormatterArgs.isFormatCurrentBranch()) {
			fileNames = GitUtil.getCurrentBranchFileNames(
				sourceFormatterArgs.getBaseDirName(),
				sourceFormatterArgs.getGitWorkingBranchName(), true);
		}
		else if (sourceFormatterArgs.isFormatLatestAuthor()) {
			fileNames = GitUtil.getLatestAuthorFileNames(
				sourceFormatterArgs.getBaseDirName(), true);
		}
		else if (sourceFormatterArgs.isFormatLocalChanges()) {
			fileNames = GitUtil.getLocalChangesFileNames(
				sourceFormatterArgs.getBaseDirName(), true);
		}

		if (fileNames.isEmpty()) {
			return Collections.emptyMap();
		}

		List<String> deletedFileNames = new ArrayList<>();

		for (String fileName : fileNames) {
			File file = new File(SourceUtil.getAbsolutePath(fileName));

			if (Files.exists(file.toPath())) {
				continue;
			}

			deletedFileNames.add(fileName);
		}

		if (deletedFileNames.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, String> contentsMap = new HashMap<>();

		List<String> filteredFileNames = SourceFormatterUtil.filterFileNames(
			deletedFileNames, excludes, getIncludes(),
			getSourceFormatterExcludes(), true);

		for (String filteredFileName : filteredFileNames) {
			String content = GitUtil.getFileContent(filteredFileName);
			String fileName = StringUtil.replace(
				sourceFormatterArgs.getBaseDirName() + filteredFileName,
				CharPool.BACK_SLASH, CharPool.SLASH);

			contentsMap.put(fileName, content);
		}

		return contentsMap;
	}

	private void _processCheckstyle() throws CheckstyleException, IOException {
		if (_ungeneratedFileContents.isEmpty()) {
			return;
		}

		_sourceFormatterMessages.addAll(
			processCheckstyle(
				_checkstyleConfiguration, _checkstyleLogger,
				_ungeneratedFileContents));

		_ungeneratedFileContents.clear();
	}

	private synchronized void _processCheckstyle(
			String absolutePath, String content)
		throws CheckstyleException, IOException {

		String javaContent = JSPCheckstyleUtil.getJavaContent(
			absolutePath, content);

		if (javaContent != null) {
			_ungeneratedFileContents.add(
				new String[] {absolutePath, javaContent});

			if (_ungeneratedFileContents.size() == CheckstyleUtil.BATCH_SIZE) {
				_processCheckstyle();
			}
		}
	}

	private static final String[] _INCLUDES = {
		"**/*.jsp", "**/*.jspf", "**/*.jspx", "**/*.tag", "**/*.tpl", "**/*.vm"
	};

	private Configuration _checkstyleConfiguration;
	private CheckstyleLogger _checkstyleLogger;
	private final Set<SourceFormatterMessage> _sourceFormatterMessages =
		new TreeSet<>();
	private final List<String[]> _ungeneratedFileContents = new ArrayList<>();

}