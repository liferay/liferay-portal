/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.maven;

import com.liferay.source.formatter.SourceFormatter;
import com.liferay.source.formatter.SourceFormatterArgs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Formats Liferay sources.
 *
 * @author Raymond Augé
 * @goal format
 */
public class FormatSourceMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException {
		try {
			@SuppressWarnings("rawtypes")
			Map pluginContext = getPluginContext();

			SourceFormatter sourceFormatter = new SourceFormatter(
				_sourceFormatterArgs);

			sourceFormatter.format();

			pluginContext.put(
				SourceFormatterArgs.OUTPUT_KEY_MODIFIED_FILES,
				sourceFormatter.getModifiedFileNames());
		}
		catch (Exception exception) {
			throw new MojoExecutionException(exception.getMessage(), exception);
		}
	}

	/**
	 * @parameter
	 */
	public void setAutoFix(boolean autoFix) {
		_sourceFormatterArgs.setAutoFix(autoFix);
	}

	/**
	 * @parameter
	 */
	public void setBaseDir(String baseDir) {
		_sourceFormatterArgs.setBaseDirName(baseDir);
	}

	/**
	 * @parameter
	 */
	public void setCheckCategoryNames(List<String> checkCategoryNames) {
		_sourceFormatterArgs.setCheckCategoryNames(checkCategoryNames);
	}

	/**
	 * @parameter
	 */
	public void setCheckNames(List<String> checkNames) {
		_sourceFormatterArgs.setCheckNames(checkNames);
	}

	/**
	 * @parameter
	 */
	public void setFailOnAutoFix(boolean failOnAutoFix) {
		_sourceFormatterArgs.setFailOnAutoFix(failOnAutoFix);
	}

	/**
	 * @parameter
	 */
	public void setFailOnHasWarning(boolean failOnHasWarning) {
		_sourceFormatterArgs.setFailOnHasWarning(failOnHasWarning);
	}

	/**
	 * @parameter
	 */
	public void setFileExtensions(List<String> fileExtensions) {
		_sourceFormatterArgs.setFileExtensions(fileExtensions);
	}

	/**
	 * @parameter
	 */
	public void setFileNames(String[] fileNames) {
		_sourceFormatterArgs.setFileNames(Arrays.asList(fileNames));
	}

	/**
	 * @parameter
	 */
	public void setFormatCurrentBranch(boolean formatCurrentBranch) {
		_sourceFormatterArgs.setFormatCurrentBranch(formatCurrentBranch);
	}

	/**
	 * @parameter
	 */
	public void setFormatLatestAuthor(boolean formatLatestAuthor) {
		_sourceFormatterArgs.setFormatLatestAuthor(formatLatestAuthor);
	}

	/**
	 * @parameter
	 */
	public void setFormatLocalChanges(boolean formatLocalChanges) {
		_sourceFormatterArgs.setFormatLocalChanges(formatLocalChanges);
	}

	/**
	 * @parameter
	 */
	public void setGitWorkingBranchName(String gitWorkingBranchName) {
		_sourceFormatterArgs.setGitWorkingBranchName(gitWorkingBranchName);
	}

	/**
	 * @parameter
	 */
	public void setIncludeSubrepositories(boolean includeSubrepositories) {
		_sourceFormatterArgs.setIncludeSubrepositories(includeSubrepositories);
	}

	/**
	 * @parameter
	 */
	public void setJavaParserEnabled(boolean javaParserEnabled) {
		_sourceFormatterArgs.setJavaParserEnabled(javaParserEnabled);
	}

	/**
	 * @parameter
	 */
	public void setMaxLineLength(int maxLineLength) {
		_sourceFormatterArgs.setMaxLineLength(maxLineLength);
	}

	/**
	 * @parameter
	 */
	public void setOutputFileName(String outputFileName) {
		_sourceFormatterArgs.setOutputFileName(outputFileName);
	}

	/**
	 * @parameter
	 */
	public void setPrintErrors(boolean printErrors) {
		_sourceFormatterArgs.setPrintErrors(printErrors);
	}

	/**
	 * @parameter
	 */
	public void setProcessorThreadCount(int processorThreadCount) {
		_sourceFormatterArgs.setProcessorThreadCount(processorThreadCount);
	}

	/**
	 * @parameter
	 */
	public void setShowDebugInformation(boolean showDebugInformation) {
		_sourceFormatterArgs.setShowDebugInformation(showDebugInformation);
	}

	/**
	 * @parameter
	 */
	public void setValidateAllowedFilePaths(boolean validateAllowedFilePaths) {
		_sourceFormatterArgs.setValidateAllowedFilePaths(
			validateAllowedFilePaths);
	}

	/**
	 * @parameter
	 */
	public void setValidateCommitMessages(boolean validateCommitMessages) {
		_sourceFormatterArgs.setValidateCommitMessages(validateCommitMessages);
	}

	private final SourceFormatterArgs _sourceFormatterArgs =
		new SourceFormatterArgs();

}