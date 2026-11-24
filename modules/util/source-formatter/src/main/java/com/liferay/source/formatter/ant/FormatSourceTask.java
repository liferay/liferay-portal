/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.ant;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatter;
import com.liferay.source.formatter.SourceFormatterArgs;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * @author Raymond Augé
 */
public class FormatSourceTask extends Task {

	public void addFileset(FileSet fileSet) {
		_fileSets.add(fileSet);
	}

	@Override
	public void execute() throws BuildException {
		if (!_fileSets.isEmpty()) {
			_collectFromFileSets();
		}

		try {
			SourceFormatter sourceFormatter = new SourceFormatter(
				_sourceFormatterArgs);

			sourceFormatter.format();

			List<String> modifiedFileNames =
				sourceFormatter.getModifiedFileNames();

			Project project = getProject();

			project.addIdReference(
				SourceFormatterArgs.OUTPUT_KEY_MODIFIED_FILES,
				modifiedFileNames);
		}
		catch (Exception exception) {
			throw new BuildException(exception);
		}
	}

	public void setAutoFix(boolean autoFix) {
		_sourceFormatterArgs.setAutoFix(autoFix);
	}

	public void setBaseDir(String baseDirName) {
		_sourceFormatterArgs.setBaseDirName(baseDirName);
	}

	public void setCheckCategoryNames(List<String> checkCategoryNames) {
		_sourceFormatterArgs.setCheckCategoryNames(checkCategoryNames);
	}

	public void setCheckNames(List<String> checkNames) {
		_sourceFormatterArgs.setCheckNames(checkNames);
	}

	public void setFailOnAutoFix(boolean failOnAutoFix) {
		_sourceFormatterArgs.setFailOnAutoFix(failOnAutoFix);
	}

	public void setFailOnHasWarning(boolean failOnHasWarning) {
		_sourceFormatterArgs.setFailOnHasWarning(failOnHasWarning);
	}

	public void setFileExtensions(List<String> fileExtensions) {
		_sourceFormatterArgs.setFileExtensions(fileExtensions);
	}

	public void setFileNames(String fileNames) {
		_sourceFormatterArgs.setFileNames(
			Arrays.asList(StringUtil.split(fileNames)));
	}

	public void setFormatCurrentBranch(boolean formatCurrentBranch) {
		_sourceFormatterArgs.setFormatCurrentBranch(formatCurrentBranch);
	}

	public void setFormatLatestAuthor(boolean formatLatestAuthor) {
		_sourceFormatterArgs.setFormatLatestAuthor(formatLatestAuthor);
	}

	public void setFormatLocalChanges(boolean formatLocalChanges) {
		_sourceFormatterArgs.setFormatLocalChanges(formatLocalChanges);
	}

	public void setGitWorkingBranchName(String gitWorkingBranchName) {
		_sourceFormatterArgs.setGitWorkingBranchName(gitWorkingBranchName);
	}

	public void setIncludeSubrepositories(boolean includeSubrepositories) {
		_sourceFormatterArgs.setIncludeSubrepositories(includeSubrepositories);
	}

	public void setJavaParserEnabled(boolean javaParserEnabled) {
		_sourceFormatterArgs.setJavaParserEnabled(javaParserEnabled);
	}

	public void setMaxLineLength(int maxLineLength) {
		_sourceFormatterArgs.setMaxLineLength(maxLineLength);
	}

	public void setOutputFileName(String outputFileName) {
		_sourceFormatterArgs.setOutputFileName(outputFileName);
	}

	public void setPrintErrors(boolean printErrors) {
		_sourceFormatterArgs.setPrintErrors(printErrors);
	}

	public void setProcessorThreadCount(int processorThreadCount) {
		_sourceFormatterArgs.setProcessorThreadCount(processorThreadCount);
	}

	public void setShowDebugInformation(boolean showDebugInformation) {
		_sourceFormatterArgs.setShowDebugInformation(showDebugInformation);
	}

	public void setValidateAllowedFilePaths(boolean validateAllowedFilePaths) {
		_sourceFormatterArgs.setValidateAllowedFilePaths(
			validateAllowedFilePaths);
	}

	public void setValidateCommitMessages(boolean validateCommitMessages) {
		_sourceFormatterArgs.setValidateCommitMessages(validateCommitMessages);
	}

	private void _collectFromFileSets() {
		List<String> fileNames = new ArrayList<>();

		for (FileSet fileSet : _fileSets) {
			DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(
				getProject());

			File basedir = directoryScanner.getBasedir();

			String[] includedFiles = directoryScanner.getIncludedFiles();

			for (int i = 0; i < includedFiles.length; i++) {
				File file = new File(basedir, includedFiles[i]);

				includedFiles[i] = file.getAbsolutePath();
			}

			Collections.addAll(fileNames, includedFiles);
		}

		_sourceFormatterArgs.setFileNames(fileNames);
	}

	private final Set<FileSet> _fileSets = new HashSet<>();
	private final SourceFormatterArgs _sourceFormatterArgs =
		new SourceFormatterArgs();

}