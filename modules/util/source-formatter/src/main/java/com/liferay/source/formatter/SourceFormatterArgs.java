/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.portal.tools.ToolsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Raymond Augé
 */
public class SourceFormatterArgs {

	public static final boolean AUTO_FIX = true;

	public static final String BASE_DIR_NAME = "./";

	public static final boolean CHECK_VULNERABILITIES = false;

	public static final int COMMIT_COUNT = 0;

	public static final boolean FAIL_ON_AUTO_FIX = false;

	public static final boolean FAIL_ON_HAS_WARNING = true;

	public static final boolean FORMAT_CURRENT_BRANCH = false;

	public static final boolean FORMAT_LATEST_AUTHOR = false;

	public static final boolean FORMAT_LOCAL_CHANGES = false;

	public static final String GIT_WORKING_BRANCH_NAME = "master";

	public static final boolean INCLUDE_GENERATED_FILES = false;

	public static final boolean INCLUDE_SUBREPOSITORIES = false;

	public static final boolean JAVA_PARSER_ENABLED = true;

	public static final int MAX_DIR_LEVEL = ToolsUtil.PORTAL_MAX_DIR_LEVEL;

	public static final int MAX_LINE_LENGTH = 80;

	public static final String OUTPUT_FILE_NAME = null;

	public static final String OUTPUT_KEY_MODIFIED_FILES =
		"source.formatter.modified.files";

	public static final boolean PRINT_ERRORS = true;

	public static final int PROCESSOR_THREAD_COUNT = 5;

	public static final boolean SHOW_DEBUG_INFORMATION = false;

	public static final boolean USE_CI_GITHUB_ACCESS_TOKEN = false;

	public static final boolean VALIDATE_ALLOWED_FILE_PATHS = false;

	public static final boolean VALIDATE_COMMIT_MESSAGES = false;

	public void addRecentChangesFileNames(
		Collection<String> fileNames, String baseDirName) {

		for (String fileName : fileNames) {
			if (baseDirName != null) {
				_recentChangesFileNames.add(_baseDirName.concat(fileName));
			}
			else {
				_recentChangesFileNames.add(fileName);
			}
		}
	}

	public String getBaseDirName() {
		return _baseDirName;
	}

	public List<String> getCheckCategoryNames() {
		return _checkCategoryNames;
	}

	public List<String> getCheckNames() {
		return _checkNames;
	}

	public int getCommitCount() {
		return _commitCount;
	}

	public List<String> getCurrentBranchAddedFileNames() {
		return _currentBranchAddedFileNames;
	}

	public List<String> getCurrentBranchRenamedFileNames() {
		return _currentBranchRenamedFileNames;
	}

	public List<String> getFileExtensions() {
		return _fileExtensions;
	}

	public List<String> getFileNames() {
		return _fileNames;
	}

	public String getGitWorkingBranchName() {
		return _gitWorkingBranchName;
	}

	public int getMaxDirLevel() {
		return _maxDirLevel;
	}

	public int getMaxLineLength() {
		return _maxLineLength;
	}

	public String getOutputFileName() {
		return _outputFileName;
	}

	public int getProcessorThreadCount() {
		return _processorThreadCount;
	}

	public Set<String> getRecentChangesFileNames() {
		return _recentChangesFileNames;
	}

	public List<String> getSkipCheckNames() {
		return _skipCheckNames;
	}

	public List<String> getSourceFormatterProperties() {
		return _sourceFormatterProperties;
	}

	public boolean isAutoFix() {
		return _autoFix;
	}

	public boolean isCheckVulnerabilities() {
		return _checkVulnerabilities;
	}

	public boolean isFailOnAutoFix() {
		return _failOnAutoFix;
	}

	public boolean isFailOnHasWarning() {
		return _failOnHasWarning;
	}

	public boolean isFormatCurrentBranch() {
		return _formatCurrentBranch;
	}

	public boolean isFormatLatestAuthor() {
		return _formatLatestAuthor;
	}

	public boolean isFormatLocalChanges() {
		return _formatLocalChanges;
	}

	public boolean isIncludeGeneratedFiles() {
		return _includeGeneratedFiles;
	}

	public boolean isIncludeSubrepositories() {
		return _includeSubrepositories;
	}

	public boolean isJavaParserEnabled() {
		return _javaParserEnabled;
	}

	public boolean isPrintErrors() {
		return _printErrors;
	}

	public boolean isShowDebugInformation() {
		return _showDebugInformation;
	}

	public boolean isUseCiGithubAccessToken() {
		return _useCiGithubAccessToken;
	}

	public boolean isValidateAllowedFilePaths() {
		return _validateAllowedFilePaths;
	}

	public boolean isValidateCommitMessages() {
		return _validateCommitMessages;
	}

	public void setAutoFix(boolean autoFix) {
		_autoFix = autoFix;
	}

	public void setBaseDirName(String baseDirName) {
		if (_fileNames != null) {
			throw new RuntimeException("File names are already initialized");
		}

		if (!baseDirName.endsWith("/")) {
			baseDirName += "/";
		}

		_baseDirName = baseDirName;
	}

	public void setCheckCategoryNames(List<String> checkCategoryNames) {
		_checkCategoryNames = checkCategoryNames;
	}

	public void setCheckNames(List<String> checkNames) {
		_checkNames = checkNames;
	}

	public void setCheckVulnerabilities(boolean checkVulnerabilities) {
		_checkVulnerabilities = checkVulnerabilities;
	}

	public void setCommitCount(int commitCount) {
		_commitCount = commitCount;
	}

	public void setCurrentBranchAddedFileNames(
		List<String> currentBranchAddedFileNames) {

		_currentBranchAddedFileNames = currentBranchAddedFileNames;
	}

	public void setCurrentBranchRenamedFileNames(
		List<String> currentBranchRenamedFileNames) {

		_currentBranchRenamedFileNames = currentBranchRenamedFileNames;
	}

	public void setFailOnAutoFix(boolean failOnAutoFix) {
		_failOnAutoFix = failOnAutoFix;
	}

	public void setFailOnHasWarning(boolean failOnHasWarning) {
		_failOnHasWarning = failOnHasWarning;
	}

	public void setFileExtensions(List<String> fileExtensions) {
		_fileExtensions = fileExtensions;
	}

	public void setFileNames(List<String> fileNames) {
		if (_fileNames != null) {
			throw new RuntimeException("File names are already initialized");
		}

		if (_baseDirName != BASE_DIR_NAME) {
			throw new RuntimeException("Base directory was already set");
		}

		_baseDirName = "";
		_fileNames = fileNames;
	}

	public void setFormatCurrentBranch(boolean formatCurrentBranch) {
		_formatCurrentBranch = formatCurrentBranch;
	}

	public void setFormatLatestAuthor(boolean formatLatestAuthor) {
		_formatLatestAuthor = formatLatestAuthor;
	}

	public void setFormatLocalChanges(boolean formatLocalChanges) {
		_formatLocalChanges = formatLocalChanges;
	}

	public void setGitWorkingBranchName(String gitWorkingBranchName) {
		_gitWorkingBranchName = gitWorkingBranchName;
	}

	public void setIncludeGeneratedFiles(boolean includeGeneratedFiles) {
		_includeGeneratedFiles = includeGeneratedFiles;
	}

	public void setIncludeSubrepositories(boolean includeSubrepositories) {
		_includeSubrepositories = includeSubrepositories;
	}

	public void setJavaParserEnabled(boolean javaParserEnabled) {
		_javaParserEnabled = javaParserEnabled;
	}

	public void setMaxDirLevel(int maxDirLevel) {
		_maxDirLevel = maxDirLevel;
	}

	public void setMaxLineLength(int maxLineLength) {
		_maxLineLength = maxLineLength;
	}

	public void setOutputFileName(String outputFileName) {
		_outputFileName = outputFileName;
	}

	public void setPrintErrors(boolean printErrors) {
		_printErrors = printErrors;
	}

	public void setProcessorThreadCount(int processorThreadCount) {
		_processorThreadCount = processorThreadCount;
	}

	public void setShowDebugInformation(boolean showDebugInformation) {
		_showDebugInformation = showDebugInformation;
	}

	public void setSkipCheckNames(List<String> skipCheckNames) {
		_skipCheckNames = skipCheckNames;
	}

	public void setSourceFormatterProperties(
		List<String> sourceFormatterProperties) {

		_sourceFormatterProperties = sourceFormatterProperties;
	}

	public void setUseCiGithubAccessToken(boolean useCiGithubAccessToken) {
		_useCiGithubAccessToken = useCiGithubAccessToken;
	}

	public void setValidateAllowedFilePaths(boolean validateAllowedFilePaths) {
		_validateAllowedFilePaths = validateAllowedFilePaths;
	}

	public void setValidateCommitMessages(boolean validateCommitMessages) {
		_validateCommitMessages = validateCommitMessages;
	}

	private boolean _autoFix = AUTO_FIX;
	private String _baseDirName = BASE_DIR_NAME;
	private List<String> _checkCategoryNames = new ArrayList<>();
	private List<String> _checkNames = new ArrayList<>();
	private boolean _checkVulnerabilities = CHECK_VULNERABILITIES;
	private int _commitCount = COMMIT_COUNT;
	private List<String> _currentBranchAddedFileNames = new ArrayList<>();
	private List<String> _currentBranchRenamedFileNames = new ArrayList<>();
	private boolean _failOnAutoFix = FAIL_ON_AUTO_FIX;
	private boolean _failOnHasWarning = FAIL_ON_HAS_WARNING;
	private List<String> _fileExtensions = new ArrayList<>();
	private List<String> _fileNames;
	private boolean _formatCurrentBranch = FORMAT_CURRENT_BRANCH;
	private boolean _formatLatestAuthor = FORMAT_LATEST_AUTHOR;
	private boolean _formatLocalChanges = FORMAT_LOCAL_CHANGES;
	private String _gitWorkingBranchName = GIT_WORKING_BRANCH_NAME;
	private boolean _includeGeneratedFiles = INCLUDE_GENERATED_FILES;
	private boolean _includeSubrepositories = INCLUDE_SUBREPOSITORIES;
	private boolean _javaParserEnabled = JAVA_PARSER_ENABLED;
	private int _maxDirLevel = MAX_DIR_LEVEL;
	private int _maxLineLength = MAX_LINE_LENGTH;
	private String _outputFileName = OUTPUT_FILE_NAME;
	private boolean _printErrors = PRINT_ERRORS;
	private int _processorThreadCount = PROCESSOR_THREAD_COUNT;
	private final Set<String> _recentChangesFileNames = new HashSet<>();
	private boolean _showDebugInformation = SHOW_DEBUG_INFORMATION;
	private List<String> _skipCheckNames = new ArrayList<>();
	private List<String> _sourceFormatterProperties = new ArrayList<>();
	private boolean _useCiGithubAccessToken = USE_CI_GITHUB_ACCESS_TOKEN;
	private boolean _validateAllowedFilePaths = VALIDATE_ALLOWED_FILE_PATHS;
	private boolean _validateCommitMessages = VALIDATE_COMMIT_MESSAGES;

}