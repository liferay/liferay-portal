/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.tools.GitException;
import com.liferay.portal.tools.GitUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.configuration.ConfigurationLoader;
import com.liferay.source.formatter.check.configuration.SourceCheckConfiguration;
import com.liferay.source.formatter.check.configuration.SourceFormatterConfiguration;
import com.liferay.source.formatter.check.configuration.SourceFormatterSuppressions;
import com.liferay.source.formatter.check.configuration.SuppressionsLoader;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.exception.SourceMismatchException;
import com.liferay.source.formatter.processor.BNDRunSourceProcessor;
import com.liferay.source.formatter.processor.BNDSourceProcessor;
import com.liferay.source.formatter.processor.CETSourceProcessor;
import com.liferay.source.formatter.processor.CIMergeAndGitRepoSourceProcessor;
import com.liferay.source.formatter.processor.CQLSourceProcessor;
import com.liferay.source.formatter.processor.CSPSourceProcessor;
import com.liferay.source.formatter.processor.CSSSourceProcessor;
import com.liferay.source.formatter.processor.CodeownersSourceProcessor;
import com.liferay.source.formatter.processor.ConfigSourceProcessor;
import com.liferay.source.formatter.processor.DTDSourceProcessor;
import com.liferay.source.formatter.processor.DockerfileSourceProcessor;
import com.liferay.source.formatter.processor.FTLSourceProcessor;
import com.liferay.source.formatter.processor.GradleSourceProcessor;
import com.liferay.source.formatter.processor.GroovySourceProcessor;
import com.liferay.source.formatter.processor.HTMLSourceProcessor;
import com.liferay.source.formatter.processor.JSONSourceProcessor;
import com.liferay.source.formatter.processor.JSPSourceProcessor;
import com.liferay.source.formatter.processor.JSSourceProcessor;
import com.liferay.source.formatter.processor.JakartaTransformSourceProcessor;
import com.liferay.source.formatter.processor.JavaSourceProcessor;
import com.liferay.source.formatter.processor.LDIFSourceProcessor;
import com.liferay.source.formatter.processor.LFRBuildSourceProcessor;
import com.liferay.source.formatter.processor.LibrarySourceProcessor;
import com.liferay.source.formatter.processor.ListSourceProcessor;
import com.liferay.source.formatter.processor.MarkdownSourceProcessor;
import com.liferay.source.formatter.processor.PackageinfoSourceProcessor;
import com.liferay.source.formatter.processor.PoshiSourceProcessor;
import com.liferay.source.formatter.processor.PropertiesSourceProcessor;
import com.liferay.source.formatter.processor.PythonSourceProcessor;
import com.liferay.source.formatter.processor.SHSourceProcessor;
import com.liferay.source.formatter.processor.SQLSourceProcessor;
import com.liferay.source.formatter.processor.SourceProcessor;
import com.liferay.source.formatter.processor.SoySourceProcessor;
import com.liferay.source.formatter.processor.TFSourceProcessor;
import com.liferay.source.formatter.processor.TLDSourceProcessor;
import com.liferay.source.formatter.processor.TSSourceProcessor;
import com.liferay.source.formatter.processor.TXTSourceProcessor;
import com.liferay.source.formatter.processor.UpgradeSourceProcessor;
import com.liferay.source.formatter.processor.XMLSourceProcessor;
import com.liferay.source.formatter.processor.YMLSourceProcessor;
import com.liferay.source.formatter.util.CheckType;
import com.liferay.source.formatter.util.DebugUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.JIRAUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class SourceFormatter {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		try {
			SourceFormatterArgs sourceFormatterArgs = new SourceFormatterArgs();

			sourceFormatterArgs.setAutoFix(
				ArgumentsUtil.getBoolean(
					arguments, "source.auto.fix",
					SourceFormatterArgs.AUTO_FIX));

			String baseDirName = ArgumentsUtil.getString(
				arguments, "source.base.dir",
				SourceFormatterArgs.BASE_DIR_NAME);

			sourceFormatterArgs.setBaseDirName(baseDirName);

			sourceFormatterArgs.setCheckCategoryNames(
				ListUtil.fromString(
					ArgumentsUtil.getString(
						arguments, "source.check.category.names", null),
					StringPool.COMMA));
			sourceFormatterArgs.setCheckNames(
				ListUtil.fromString(
					ArgumentsUtil.getString(
						arguments, "source.check.names", null),
					StringPool.COMMA));
			sourceFormatterArgs.setCheckVulnerabilities(
				ArgumentsUtil.getBoolean(
					arguments, "check.vulnerabilities",
					SourceFormatterArgs.CHECK_VULNERABILITIES));
			sourceFormatterArgs.setFailOnAutoFix(
				ArgumentsUtil.getBoolean(
					arguments, "source.fail.on.auto.fix",
					SourceFormatterArgs.FAIL_ON_AUTO_FIX));
			sourceFormatterArgs.setFailOnHasWarning(
				ArgumentsUtil.getBoolean(
					arguments, "source.fail.on.has.warning",
					SourceFormatterArgs.FAIL_ON_HAS_WARNING));
			sourceFormatterArgs.setFormatCurrentBranch(
				ArgumentsUtil.getBoolean(
					arguments, "format.current.branch",
					SourceFormatterArgs.FORMAT_CURRENT_BRANCH));
			sourceFormatterArgs.setFormatLatestAuthor(
				ArgumentsUtil.getBoolean(
					arguments, "format.latest.author",
					SourceFormatterArgs.FORMAT_LATEST_AUTHOR));
			sourceFormatterArgs.setFormatLocalChanges(
				ArgumentsUtil.getBoolean(
					arguments, "format.local.changes",
					SourceFormatterArgs.FORMAT_LOCAL_CHANGES));
			sourceFormatterArgs.setUseCiGithubAccessToken(
				ArgumentsUtil.getBoolean(
					arguments, "use.ci.github.access.token",
					SourceFormatterArgs.USE_CI_GITHUB_ACCESS_TOKEN));
			sourceFormatterArgs.setGitWorkingBranchName(
				ArgumentsUtil.getString(
					arguments, "git.working.branch.name",
					SourceFormatterArgs.GIT_WORKING_BRANCH_NAME));

			int commitCount = ArgumentsUtil.getInteger(
				arguments, "commit.count", SourceFormatterArgs.COMMIT_COUNT);

			sourceFormatterArgs.setCommitCount(commitCount);

			if (commitCount > 0) {
				sourceFormatterArgs.addRecentChangesFileNames(
					GitUtil.getModifiedFileNames(baseDirName, commitCount),
					baseDirName);
			}
			else if (sourceFormatterArgs.isFormatCurrentBranch()) {
				sourceFormatterArgs.addRecentChangesFileNames(
					GitUtil.getCurrentBranchFileNames(
						baseDirName,
						sourceFormatterArgs.getGitWorkingBranchName(), false),
					baseDirName);
				sourceFormatterArgs.setCurrentBranchAddedFileNames(
					GitUtil.getCurrentBranchAddedFileNames(
						sourceFormatterArgs.getBaseDirName(),
						sourceFormatterArgs.getGitWorkingBranchName()));
				sourceFormatterArgs.setCurrentBranchRenamedFileNames(
					GitUtil.getCurrentBranchRenamedFileNames(
						sourceFormatterArgs.getBaseDirName(),
						sourceFormatterArgs.getGitWorkingBranchName()));
			}
			else if (sourceFormatterArgs.isFormatLatestAuthor()) {
				sourceFormatterArgs.addRecentChangesFileNames(
					GitUtil.getLatestAuthorFileNames(baseDirName, false),
					baseDirName);
			}
			else if (sourceFormatterArgs.isFormatLocalChanges()) {
				sourceFormatterArgs.addRecentChangesFileNames(
					GitUtil.getLocalChangesFileNames(baseDirName, false),
					baseDirName);
			}

			String[] fileNames = StringUtil.split(
				ArgumentsUtil.getString(
					arguments, "source.files", StringPool.BLANK),
				StringPool.COMMA);

			if (ArrayUtil.isNotEmpty(fileNames)) {
				sourceFormatterArgs.setFileNames(Arrays.asList(fileNames));
			}
			else {
				String fileExtensionsString = ArgumentsUtil.getString(
					arguments, "source.file.extensions", StringPool.BLANK);

				String[] fileExtensions = StringUtil.split(
					fileExtensionsString, StringPool.COMMA);

				sourceFormatterArgs.setFileExtensions(
					Arrays.asList(fileExtensions));
			}

			sourceFormatterArgs.setIncludeGeneratedFiles(
				ArgumentsUtil.getBoolean(
					arguments, "include.generated.files",
					SourceFormatterArgs.INCLUDE_GENERATED_FILES));

			boolean includeSubrepositories = ArgumentsUtil.getBoolean(
				arguments, "include.subrepositories",
				SourceFormatterArgs.INCLUDE_SUBREPOSITORIES);

			for (String recentChangesFileName :
					sourceFormatterArgs.getRecentChangesFileNames()) {

				if (recentChangesFileName.endsWith("ci-merge")) {
					includeSubrepositories = true;

					break;
				}
			}

			sourceFormatterArgs.setIncludeSubrepositories(
				includeSubrepositories);

			sourceFormatterArgs.setJavaParserEnabled(
				ArgumentsUtil.getBoolean(
					arguments, "java.parser.enabled",
					SourceFormatterArgs.JAVA_PARSER_ENABLED));
			sourceFormatterArgs.setMaxLineLength(
				ArgumentsUtil.getInteger(
					arguments, "max.line.length",
					SourceFormatterArgs.MAX_LINE_LENGTH));
			sourceFormatterArgs.setMaxDirLevel(
				Math.max(
					ToolsUtil.PORTAL_MAX_DIR_LEVEL,
					StringUtil.count(baseDirName, CharPool.SLASH) + 1));
			sourceFormatterArgs.setOutputFileName(
				ArgumentsUtil.getString(
					arguments, "output.file.name",
					SourceFormatterArgs.OUTPUT_FILE_NAME));
			sourceFormatterArgs.setPrintErrors(
				ArgumentsUtil.getBoolean(
					arguments, "source.print.errors",
					SourceFormatterArgs.PRINT_ERRORS));
			sourceFormatterArgs.setProcessorThreadCount(
				ArgumentsUtil.getInteger(
					arguments, "processor.thread.count",
					SourceFormatterArgs.PROCESSOR_THREAD_COUNT));
			sourceFormatterArgs.setShowDebugInformation(
				ArgumentsUtil.getBoolean(
					arguments, "show.debug.information",
					SourceFormatterArgs.SHOW_DEBUG_INFORMATION));

			String[] skipCheckNames = StringUtil.split(
				ArgumentsUtil.getString(
					arguments, "skip.check.names", StringPool.BLANK),
				StringPool.COMMA);

			if (ArrayUtil.isNotEmpty(skipCheckNames)) {
				sourceFormatterArgs.setSkipCheckNames(
					Arrays.asList(skipCheckNames));
			}

			String[] sourceFormatterProperties = StringUtil.split(
				ArgumentsUtil.getString(
					arguments, "source.formatter.properties", StringPool.BLANK),
				"\\n");

			if (ArrayUtil.isNotEmpty(sourceFormatterProperties)) {
				sourceFormatterArgs.setSourceFormatterProperties(
					Arrays.asList(sourceFormatterProperties));
			}

			sourceFormatterArgs.setValidateCommitMessages(
				ArgumentsUtil.getBoolean(
					arguments, "validate.commit.messages",
					SourceFormatterArgs.VALIDATE_COMMIT_MESSAGES));

			SourceFormatter sourceFormatter = new SourceFormatter(
				sourceFormatterArgs);

			sourceFormatter.format();
		}
		catch (Exception exception) {
			if (exception instanceof GitException) {
				System.out.println(exception.getMessage());
			}
			else {
				CheckstyleException checkstyleException =
					_getNestedCheckstyleException(exception);

				if (checkstyleException != null) {
					checkstyleException.printStackTrace();
				}
				else {
					exception.printStackTrace();
				}
			}

			System.exit(1);
		}
	}

	public SourceFormatter(SourceFormatterArgs sourceFormatterArgs) {
		_sourceFormatterArgs = sourceFormatterArgs;

		System.setProperty("java.awt.headless", "true");
	}

	public void format() throws Exception {
		System.setProperty(
			"javax.xml.parsers.SAXParserFactory",
			"org.apache.xerces.jaxp.SAXParserFactoryImpl");

		_init();

		if (_sourceFormatterArgs.isValidateCommitMessages()) {
			_validateCommitMessages();
		}

		_validatePullModeChanges();

		if (!_sourceFormatterArgs.isJavaParserEnabled()) {
			System.out.println(
				StringBundler.concat(
					"WARNING: Setting property \"java.parser.enabled\" to ",
					"\"false\" may prevent certain Java/JSP checks from ",
					"working properly."));
		}

		_sourceProcessors.add(new BNDRunSourceProcessor());
		_sourceProcessors.add(new BNDSourceProcessor());
		_sourceProcessors.add(new CIMergeAndGitRepoSourceProcessor());
		_sourceProcessors.add(new CodeownersSourceProcessor());
		_sourceProcessors.add(new ConfigSourceProcessor());
		_sourceProcessors.add(new CQLSourceProcessor());
		_sourceProcessors.add(new CSPSourceProcessor());
		_sourceProcessors.add(new CSSSourceProcessor());
		_sourceProcessors.add(new DockerfileSourceProcessor());
		_sourceProcessors.add(new DTDSourceProcessor());
		_sourceProcessors.add(new FTLSourceProcessor());
		_sourceProcessors.add(new GradleSourceProcessor());
		_sourceProcessors.add(new GroovySourceProcessor());
		_sourceProcessors.add(new HTMLSourceProcessor());
		_sourceProcessors.add(new JakartaTransformSourceProcessor());
		_sourceProcessors.add(new JavaSourceProcessor());
		_sourceProcessors.add(new JSONSourceProcessor());
		_sourceProcessors.add(new JSPSourceProcessor());
		_sourceProcessors.add(new JSSourceProcessor());
		_sourceProcessors.add(new LDIFSourceProcessor());
		_sourceProcessors.add(new LFRBuildSourceProcessor());
		_sourceProcessors.add(new LibrarySourceProcessor());
		_sourceProcessors.add(new ListSourceProcessor());
		_sourceProcessors.add(new MarkdownSourceProcessor());
		_sourceProcessors.add(new PackageinfoSourceProcessor());
		_sourceProcessors.add(new PoshiSourceProcessor());
		_sourceProcessors.add(new PropertiesSourceProcessor());
		_sourceProcessors.add(new PythonSourceProcessor());
		_sourceProcessors.add(new SHSourceProcessor());
		_sourceProcessors.add(new SoySourceProcessor());
		_sourceProcessors.add(new SQLSourceProcessor());
		_sourceProcessors.add(new TFSourceProcessor());
		_sourceProcessors.add(new TLDSourceProcessor());
		_sourceProcessors.add(new TSSourceProcessor());
		_sourceProcessors.add(new TXTSourceProcessor());
		_sourceProcessors.add(new UpgradeSourceProcessor());
		_sourceProcessors.add(new XMLSourceProcessor());
		_sourceProcessors.add(new YMLSourceProcessor());

		_sourceProcessors.add(new CETSourceProcessor());

		ExecutorService executorService = Executors.newFixedThreadPool(
			_sourceProcessors.size());

		List<Future<Void>> futures = new ArrayList<>(_sourceProcessors.size());

		for (final SourceProcessor sourceProcessor : _sourceProcessors) {
			Future<Void> future = executorService.submit(
				new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						_runSourceProcessor(sourceProcessor);

						return null;
					}

				});

			futures.add(future);
		}

		ExecutionException executionException1 = null;

		for (Future<Void> future : futures) {
			try {
				future.get();
			}
			catch (ExecutionException executionException2) {
				if (executionException1 == null) {
					executionException1 = executionException2;
				}
				else {
					executionException1.addSuppressed(executionException2);
				}
			}
		}

		executorService.shutdown();

		while (!executorService.isTerminated()) {
			Thread.sleep(20);
		}

		if (_sourceFormatterArgs.isShowDebugInformation()) {
			DebugUtil.printSourceFormatterInformation();
		}

		if (executionException1 != null) {
			throw executionException1;
		}

		if (!_sourceFormatterMessages.isEmpty() ||
			!_sourceMismatchExceptions.isEmpty()) {

			String outputFileName = _sourceFormatterArgs.getOutputFileName();

			if (outputFileName != null) {
				File file = null;

				int pos = outputFileName.lastIndexOf(File.separator);

				if (pos != -1) {
					File directory = new File(outputFileName.substring(0, pos));

					if (directory.exists()) {
						file = new File(outputFileName);
					}
				}

				if (file == null) {
					file = new File(
						_sourceFormatterArgs.getBaseDirName() + outputFileName);
				}

				FileUtil.write(file, _getOutputFileContent());
			}
		}

		if ((_sourceFormatterArgs.isFailOnAutoFix() &&
			 !_sourceMismatchExceptions.isEmpty()) ||
			(_sourceFormatterArgs.isFailOnHasWarning() &&
			 !_sourceFormatterMessages.isEmpty())) {

			throw new Exception(_getExceptionMessage());
		}
	}

	public List<String> getModifiedFileNames() {
		return _modifiedFileNames;
	}

	public SourceFormatterArgs getSourceFormatterArgs() {
		return _sourceFormatterArgs;
	}

	public Set<SourceFormatterMessage> getSourceFormatterMessages() {
		return _sourceFormatterMessages;
	}

	public List<SourceMismatchException> getSourceMismatchExceptions() {
		return _sourceMismatchExceptions;
	}

	private static CheckstyleException _getNestedCheckstyleException(
		Exception exception) {

		Throwable throwable = exception;

		while (true) {
			if (throwable == null) {
				return null;
			}

			if (throwable instanceof CheckstyleException) {
				return (CheckstyleException)throwable;
			}

			throwable = throwable.getCause();
		}
	}

	private Set<String> _addDependentFileName(
		Set<String> dependentFileNames, String fileName) {

		File file = new File(_sourceFormatterArgs.getBaseDirName() + fileName);

		if (file.exists()) {
			dependentFileNames.add(
				_sourceFormatterArgs.getBaseDirName() + fileName);
		}

		return dependentFileNames;
	}

	private Set<String> _addDependentFileName(
		Set<String> dependentFileNames, String fileName,
		String dependentFileName) {

		String dirName = fileName.substring(
			0, fileName.lastIndexOf(CharPool.SLASH));

		while (true) {
			String dependentFilePathName = dirName + "/" + dependentFileName;

			File file = new File(dependentFilePathName);

			if (file.exists()) {
				dependentFileNames.add(dependentFilePathName);

				return dependentFileNames;
			}

			int pos = dirName.lastIndexOf(CharPool.SLASH);

			if (pos == -1) {
				return dependentFileNames;
			}

			dirName = dirName.substring(0, pos);
		}
	}

	private void _addDependentFileNames() throws Exception {
		Set<String> recentChangesFileNames =
			_sourceFormatterArgs.getRecentChangesFileNames();

		if (recentChangesFileNames == null) {
			return;
		}

		Set<String> dependentFileNames = new HashSet<>();

		boolean buildPropertiesAdded = false;
		boolean tagJavaFilesAdded = false;

		for (String recentChangesFileName : recentChangesFileNames) {
			if (!buildPropertiesAdded &&
				recentChangesFileName.endsWith(".lfrbuild-portal")) {

				dependentFileNames = _addDependentFileName(
					dependentFileNames, "build.properties");

				buildPropertiesAdded = true;
			}

			if (recentChangesFileName.contains("/modules/apps/archived/")) {
				dependentFileNames.addAll(
					SourceFormatterUtil.filterFileNames(
						_allFileNames, new String[0],
						new String[] {
							"**/source-formatter.properties",
							"**/test.properties"
						},
						_sourceFormatterExcludes, false));
			}

			if (recentChangesFileName.endsWith(".java") &&
				recentChangesFileName.contains("/upgrade/")) {

				dependentFileNames = _addDependentFileName(
					dependentFileNames, recentChangesFileName, "bnd.bnd");
			}
			else if (recentChangesFileName.endsWith("ServiceImpl.java")) {
				dependentFileNames = _addDependentFileName(
					dependentFileNames, recentChangesFileName, "service.xml");
			}
			else if (!tagJavaFilesAdded &&
					 recentChangesFileName.endsWith(".tld")) {

				dependentFileNames.addAll(
					SourceFormatterUtil.filterFileNames(
						_allFileNames, new String[0],
						new String[] {"**/*Tag.java"}, _sourceFormatterExcludes,
						false));

				tagJavaFilesAdded = true;
			}
			else if (recentChangesFileName.endsWith(
						"/modules/source-formatter.properties")) {

				dependentFileNames.addAll(
					SourceFormatterUtil.filterFileNames(
						_allFileNames, new String[0],
						new String[] {"**/build.gradle"},
						_sourceFormatterExcludes, false));
			}
			else if (recentChangesFileName.endsWith(
						_sourceFormatterArgs.getBaseDirName() +
							"release.properties")) {

				dependentFileNames.add(
					_sourceFormatterArgs.getBaseDirName() +
						"/modules/sdk/ant-bnd/src/main/java/com/liferay/ant" +
							"/bnd/social/SocialAnalyzerPlugin.java");
				dependentFileNames.add(
					_sourceFormatterArgs.getBaseDirName() +
						"/portal-impl/src/com/liferay/portal/util" +
							"/EntityResolver.java");
				dependentFileNames.add(
					_sourceFormatterArgs.getBaseDirName() +
						"/portal-impl/src/com/liferay/portlet/social/util" +
							"/SocialConfigurationImpl.java");
			}
			else if (_isFrontendPackageChanges(recentChangesFileName)) {
				dependentFileNames.addAll(
					SourceFormatterUtil.filterFileNames(
						_allFileNames, new String[0],
						new String[] {"**/package.json"},
						_sourceFormatterExcludes, false));
			}
			else if (_isRootTestPropertiesChanges(recentChangesFileName)) {
				dependentFileNames.addAll(
					SourceFormatterUtil.filterFileNames(
						_allFileNames, new String[0],
						new String[] {"**/test.properties"},
						_sourceFormatterExcludes, false));
			}
		}

		if (_sourceFormatterArgs.isFormatCurrentBranch()) {
			if (!buildPropertiesAdded) {
				List<String> fileNames = GitUtil.getCurrentBranchFileNames(
					_sourceFormatterArgs.getBaseDirName(),
					_sourceFormatterArgs.getGitWorkingBranchName(), true);

				for (String fileName : fileNames) {
					if (!buildPropertiesAdded &&
						fileName.endsWith(".lfrbuild-portal")) {

						dependentFileNames = _addDependentFileName(
							dependentFileNames, "build.properties");

						break;
					}
				}
			}

			List<String> deletedFileNames =
				GitUtil.getCurrentBranchDeletedFileNames(
					_sourceFormatterArgs.getBaseDirName(),
					_sourceFormatterArgs.getGitWorkingBranchName());

			if (!deletedFileNames.isEmpty()) {
				for (String deletedFileName : deletedFileNames) {
					if (deletedFileName.endsWith("/test.properties")) {
						dependentFileNames.addAll(
							SourceFormatterUtil.filterFileNames(
								_allFileNames, new String[0],
								new String[] {"**/test.properties"},
								_sourceFormatterExcludes, false));

						break;
					}
				}

				dependentFileNames.addAll(
					SourceFormatterUtil.filterFileNames(
						_allFileNames, new String[0],
						new String[] {
							"**/source-formatter.properties",
							"**/source-formatter-suppressions.xml"
						},
						_sourceFormatterExcludes, false));
			}

			if (_isFeatureFlagChanges()) {
				File portalDir = SourceFormatterUtil.getPortalDir(
					_sourceFormatterArgs.getBaseDirName(),
					_sourceFormatterArgs.getMaxLineLength());

				dependentFileNames.add(
					portalDir + "/portal-impl/src/portal.properties");
			}
		}

		_sourceFormatterArgs.addRecentChangesFileNames(
			dependentFileNames, null);
	}

	private boolean _containsDir(String dirName) {
		File directory = SourceFormatterUtil.getFile(
			_sourceFormatterArgs.getBaseDirName(), dirName,
			_sourceFormatterArgs.getMaxDirLevel());

		if (directory != null) {
			return true;
		}

		return false;
	}

	private void _excludeWorkingDirCheckoutPrivateApps(File portalDir)
		throws Exception {

		File file = new File(portalDir, "working.dir.properties");

		if (!file.exists()) {
			return;
		}

		Properties properties = _getProperties(file);

		for (Object key : properties.keySet()) {
			String s = (String)key;

			if (s.matches("working.dir.checkout.private.apps.(\\w)+.dirs")) {
				List<String> dirs = ListUtil.fromString(
					properties.getProperty(s), StringPool.COMMA);

				for (String dir : dirs) {
					_sourceFormatterExcludes.addDefaultExcludeSyntaxPatterns(
						_getExcludeSyntaxPatterns("**/" + dir + "/**"));
				}
			}
		}
	}

	private List<String> _getCheckNames() {
		List<String> checkNames = new ArrayList<>();

		for (String sourceProcessorName :
				_sourceFormatterConfiguration.getSourceProcessorNames()) {

			for (SourceCheckConfiguration sourceCheckConfiguration :
					_sourceFormatterConfiguration.getSourceCheckConfigurations(
						sourceProcessorName)) {

				checkNames.add(sourceCheckConfiguration.getName());
			}
		}

		return checkNames;
	}

	private String _getExceptionMessage() {
		int size =
			_sourceFormatterMessages.size() + _sourceMismatchExceptions.size();

		StringBundler sb = new StringBundler(size * 4);

		int index = 1;

		if (_sourceFormatterArgs.isFailOnHasWarning()) {
			for (SourceFormatterMessage sourceFormatterMessage :
					_sourceFormatterMessages) {

				sb.append(index);
				sb.append(": ");
				sb.append(sourceFormatterMessage.toString());
				sb.append("\n");

				index = index + 1;
			}
		}

		if (_sourceFormatterArgs.isFailOnAutoFix()) {
			for (SourceMismatchException sourceMismatchException :
					_sourceMismatchExceptions) {

				String message = sourceMismatchException.getMessage();

				if (Objects.nonNull(message)) {
					sb.append(index);
					sb.append(": ");
					sb.append(message);
					sb.append("\n");

					index = index + 1;
				}
			}
		}

		return StringBundler.concat(
			"Found ", index - 1, " formatting issues:\n", sb);
	}

	private List<ExcludeSyntaxPattern> _getExcludeSyntaxPatterns(
		String sourceFormatterExcludes) {

		List<ExcludeSyntaxPattern> excludeSyntaxPatterns = new ArrayList<>();

		List<String> excludes = ListUtil.fromString(
			sourceFormatterExcludes, StringPool.COMMA);

		for (String exclude : excludes) {
			excludeSyntaxPatterns.add(
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, exclude));
		}

		// See the source-format task in built-test-batch.xml for more
		// information

		String systemExcludes = System.getProperty("source.formatter.excludes");

		excludes = ListUtil.fromString(GetterUtil.getString(systemExcludes));

		for (String exclude : excludes) {
			excludeSyntaxPatterns.add(
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, exclude));
		}

		return excludeSyntaxPatterns;
	}

	private String _getOutputFileContent() {
		JSONObject jsonObject = new JSONObjectImpl();

		JSONArray modifiedFilesJSONArray = new JSONArrayImpl();

		for (SourceMismatchException sourceMismatchException :
				_sourceMismatchExceptions) {

			modifiedFilesJSONArray.put(sourceMismatchException.getFileName());
		}

		jsonObject.put("modifiedFileNames", modifiedFilesJSONArray);

		JSONArray checksJSONArray = new JSONArrayImpl();

		JSONObject checkJSONObject = null;
		String currentCheckName = null;
		JSONArray violationsJSONArray = null;

		Set<SourceFormatterMessage> sortedSourceFormatterMessages =
			new TreeSet<>(new SourceFormatterMessageCheckNameComparator());

		sortedSourceFormatterMessages.addAll(_sourceFormatterMessages);

		int violationsCount = 0;

		for (SourceFormatterMessage sourceFormatterMessage :
				sortedSourceFormatterMessages) {

			String checkName = sourceFormatterMessage.getCheckName();

			if (checkName == null) {
				continue;
			}

			if (!Objects.equals(checkName, currentCheckName)) {
				if (currentCheckName != null) {
					checkJSONObject.put("violations", violationsJSONArray);

					checksJSONArray.put(checkJSONObject);
				}

				checkJSONObject = new JSONObjectImpl();

				checkJSONObject.put("name", checkName);

				String documentationURLString =
					sourceFormatterMessage.getDocumentationURLString();

				if (documentationURLString != null) {
					checkJSONObject.put(
						"documentationURLString",
						sourceFormatterMessage.getDocumentationURLString());
				}

				violationsJSONArray = new JSONArrayImpl();

				currentCheckName = checkName;
			}

			JSONObject violationJSONObject = new JSONObjectImpl();

			violationJSONObject.put(
				"fileName", sourceFormatterMessage.getFileName()
			).put(
				"lineNumber", sourceFormatterMessage.getLineNumber()
			).put(
				"message", sourceFormatterMessage.getMessage()
			);

			violationsCount++;

			violationsJSONArray.put(violationJSONObject);
		}

		if (checkJSONObject != null) {
			checkJSONObject.put("violations", violationsJSONArray);

			checksJSONArray.put(checkJSONObject);

			jsonObject.put("checks", checksJSONArray);
		}

		jsonObject.put("violationsCount", violationsCount);

		return JSONUtil.toString(jsonObject);
	}

	private List<String> _getPluginsInsideModulesDirectoryNames() {
		List<String> pluginsInsideModulesDirectoryNames = new ArrayList<>();

		List<String> pluginBuildFileNames = SourceFormatterUtil.filterFileNames(
			_allFileNames, new String[0],
			new String[] {
				"**/modules/apps/**/build.xml",
				"**/modules/dxp/apps/**/build.xml",
				"**/modules/private/apps/**/build.xml"
			},
			_sourceFormatterExcludes, true);

		for (String pluginBuildFileName : pluginBuildFileNames) {
			pluginBuildFileName = StringUtil.replace(
				pluginBuildFileName, CharPool.BACK_SLASH, CharPool.SLASH);

			String absolutePath = SourceUtil.getAbsolutePath(
				pluginBuildFileName);

			int x = absolutePath.indexOf("/modules/apps/");

			if (x == -1) {
				x = absolutePath.indexOf("/modules/dxp/apps/");
			}

			if (x == -1) {
				x = absolutePath.indexOf("/modules/private/apps/");
			}

			int y = absolutePath.lastIndexOf(StringPool.SLASH);

			pluginsInsideModulesDirectoryNames.add(
				absolutePath.substring(x, y + 1));
		}

		return pluginsInsideModulesDirectoryNames;
	}

	private String _getPortalBranchName() {
		for (Map.Entry<String, Properties> entry : _propertiesMap.entrySet()) {
			Properties properties = entry.getValue();

			if (properties.containsKey(
					SourceFormatterUtil.GIT_LIFERAY_PORTAL_BRANCH)) {

				return properties.getProperty(
					SourceFormatterUtil.GIT_LIFERAY_PORTAL_BRANCH);
			}
		}

		return null;
	}

	private String _getProjectPathPrefix() throws Exception {
		if (!_subrepository) {
			return null;
		}

		String fileName = "gradle.properties";

		for (int i = 0; i < _sourceFormatterArgs.getMaxDirLevel(); i++) {
			File file = new File(
				_sourceFormatterArgs.getBaseDirName() + fileName);

			if (file.exists()) {
				Properties properties = new Properties();

				properties.load(new FileInputStream(file));

				if (properties.containsKey("project.path.prefix")) {
					return properties.getProperty("project.path.prefix");
				}
			}

			fileName = "../" + fileName;
		}

		return null;
	}

	private Properties _getProperties(File file) throws Exception {
		Properties properties = new Properties();

		if (file.exists()) {
			properties.load(new FileInputStream(file));
		}

		return properties;
	}

	private List<String> _getPropertyValues(String key) {
		List<String> propertyValues = new ArrayList<>();

		for (Map.Entry<String, Properties> entry : _propertiesMap.entrySet()) {
			Properties properties = entry.getValue();

			if (properties.containsKey(key)) {
				propertyValues.addAll(
					ListUtil.fromString(
						properties.getProperty(key), StringPool.COMMA));
			}
		}

		return propertyValues;
	}

	private void _init() throws Exception {
		_sourceFormatterExcludes.addDefaultExcludeSyntaxPatterns(
			ListUtil.fromArray(
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/.git/**"),
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/.gradle/**"),
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/.idea/**"),
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/.m2/**"),
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/.settings/**"),
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/bin/**"),
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/classes/**"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.GLOB, "**/liferay-theme.json"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.GLOB, "**/npm-shrinkwrap.json"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.GLOB, "**/package-lock.json"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.GLOB, "**/test-classes/**"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.GLOB, "**/test-coverage/**"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.GLOB, "**/test-results/**"),
				new ExcludeSyntaxPattern(ExcludeSyntax.GLOB, "**/tmp/**"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.GLOB, "**/node_modules_cache/**"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.REGEX,
					".*/frontend-theme-unstyled/.*/_unstyled/css/clay/.+"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.REGEX,
					".*/frontend-theme-unstyled/.*/_unstyled/images/(aui|" +
						"clay|lexicon)/.+"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.REGEX,
					".*/tests?/.*/?dependencies/.+\\.(jar|lar|war|zip)/.+"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.REGEX,
					"^((?!/frontend-js-node-shims/src/).)*/node_modules/.*"),
				new ExcludeSyntaxPattern(
					ExcludeSyntax.REGEX, "^((?!/src/).)*/build/.*")));

		_portalSource = _containsDir("portal-impl");

		if (_portalSource) {
			_excludeWorkingDirCheckoutPrivateApps(
				SourceFormatterUtil.getPortalDir(
					_sourceFormatterArgs.getBaseDirName(),
					_sourceFormatterArgs.getMaxLineLength()));
		}

		_propertiesMap = new HashMap<>();

		// Find properties file in any parent directory

		String parentDirName = _sourceFormatterArgs.getBaseDirName();

		for (int i = 0; i < _sourceFormatterArgs.getMaxDirLevel(); i++) {
			_readProperties(new File(parentDirName + _PROPERTIES_FILE_NAME));

			parentDirName += "../";
		}

		_allFileNames = SourceFormatterUtil.scanForFileNames(
			_sourceFormatterArgs.getBaseDirName(), new String[0],
			new String[] {
				"**/*.*", "**/CODEOWNERS", "**/Dockerfile", "**/ci-merge",
				"**/packageinfo"
			},
			_sourceFormatterExcludes,
			_sourceFormatterArgs.isIncludeSubrepositories());

		// Find properties file in any child directory

		List<String> modulePropertiesFileNames =
			SourceFormatterUtil.filterFileNames(
				_allFileNames, new String[0],
				new String[] {"**/" + _PROPERTIES_FILE_NAME},
				_sourceFormatterExcludes, true);

		for (String modulePropertiesFileName : modulePropertiesFileNames) {
			_readProperties(new File(modulePropertiesFileName));
		}

		for (Properties properties : _propertiesMap.values()) {
			if (GetterUtil.getBoolean(properties.get("liferay.source"))) {
				_portalSource = true;

				break;
			}
		}

		if (!_portalSource && _containsDir("modules/private/apps")) {

			// Grab and read properties from portal branch

			String propertiesContent = SourceFormatterUtil.getGitContent(
				_PROPERTIES_FILE_NAME, _getPortalBranchName());

			_readProperties(
				propertiesContent,
				SourceUtil.getAbsolutePath(
					_sourceFormatterArgs.getBaseDirName()));
		}

		for (String sourceFormatterProperty :
				_sourceFormatterArgs.getSourceFormatterProperties()) {

			_readProperties(
				sourceFormatterProperty,
				SourceUtil.getAbsolutePath(
					_sourceFormatterArgs.getBaseDirName()));
		}

		_addDependentFileNames();

		_pluginsInsideModulesDirectoryNames =
			_getPluginsInsideModulesDirectoryNames();

		_subrepository = _isSubrepository();

		_projectPathPrefix = _getProjectPathPrefix();

		_sourceFormatterSuppressions = SuppressionsLoader.loadSuppressions(
			_sourceFormatterArgs.getBaseDirName(),
			SourceFormatterUtil.getSuppressionsFiles(
				_sourceFormatterArgs.getBaseDirName(), _allFileNames,
				_sourceFormatterExcludes,
				_sourceFormatterArgs.getMaxDirLevel()),
			_propertiesMap);

		_sourceFormatterConfiguration = ConfigurationLoader.loadConfiguration(
			"sourcechecks.xml");

		if (_sourceFormatterArgs.isShowDebugInformation()) {
			DebugUtil.addCheckNames(CheckType.SOURCE_CHECK, _getCheckNames());
		}
	}

	private boolean _isFeatureFlagChanges() throws Exception {
		String currentBranchDiff = GitUtil.getCurrentBranchDiff(
			_sourceFormatterArgs.getBaseDirName(),
			_sourceFormatterArgs.getGitWorkingBranchName());

		for (String line : StringUtil.split(currentBranchDiff, "\n")) {
			if ((line.startsWith(StringPool.MINUS) ||
				 line.startsWith(StringPool.PLUS)) &&
				(line.contains("feature.flag") ||
				 line.contains("FeatureFlagManagerUtil.isEnabled(") ||
				 line.contains("Liferay-Site-Initializer-Feature-Flag:") ||
				 line.contains("Liferay.FeatureFlags['") ||
				 line.contains("\"featureFlag\": \""))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isFrontendPackageChanges(String recentChangesFileName) {
		if (recentChangesFileName.endsWith(
				"/modules/apps/frontend-js/frontend-js-react-web" +
					"/package.json") ||
			recentChangesFileName.endsWith(
				"/modules/apps/frontend-js/frontend-js-spa-web/package.json") ||
			recentChangesFileName.endsWith(
				"/modules/apps/frontend-taglib/frontend-taglib-clay" +
					"/package.json")) {

			return true;
		}

		return false;
	}

	private boolean _isRootTestPropertiesChanges(String recentChangesFileName) {
		File portalDir = SourceFormatterUtil.getPortalDir(
			_sourceFormatterArgs.getBaseDirName(),
			_sourceFormatterArgs.getMaxLineLength());

		if (portalDir == null) {
			return false;
		}

		return recentChangesFileName.endsWith(portalDir + "/test.properties");
	}

	private boolean _isSubrepository() throws Exception {
		if (_portalSource) {
			return false;
		}

		String baseDirAbsolutePath = SourceUtil.getAbsolutePath(
			_sourceFormatterArgs.getBaseDirName());

		File baseDir = new File(baseDirAbsolutePath);

		for (int i = 0; i < _SUBREPOSITORY_MAX_DIR_LEVEL; i++) {
			if ((baseDir == null) || !baseDir.exists()) {
				return false;
			}

			File gradlePropertiesFile = new File(baseDir, "gradle.properties");
			File gradlewFile = new File(baseDir, "gradlew");

			if (gradlePropertiesFile.exists() && gradlewFile.exists()) {
				String content = FileUtil.read(gradlePropertiesFile);

				if (content.contains("project.path.prefix=")) {
					return true;
				}
			}

			baseDir = baseDir.getParentFile();
		}

		return false;
	}

	private void _readProperties(File propertiesFile) throws Exception {
		Properties properties = _getProperties(propertiesFile);

		if (properties.isEmpty()) {
			return;
		}

		String propertiesFileLocation = SourceUtil.getAbsolutePath(
			propertiesFile);

		int pos = propertiesFileLocation.lastIndexOf(StringPool.SLASH);

		propertiesFileLocation = propertiesFileLocation.substring(0, pos);

		_readProperties(properties, propertiesFileLocation);
	}

	private void _readProperties(
		Properties properties, String propertiesFileLocation) {

		String value = properties.getProperty("source.formatter.excludes");

		if (value != null) {
			if (FileUtil.exists(propertiesFileLocation + "portal-impl")) {
				_sourceFormatterExcludes.addDefaultExcludeSyntaxPatterns(
					_getExcludeSyntaxPatterns(value));
			}
			else {
				_sourceFormatterExcludes.addExcludeSyntaxPatterns(
					propertiesFileLocation, _getExcludeSyntaxPatterns(value));
			}

			properties.remove("source.formatter.excludes");
		}

		Properties existingProperties = _propertiesMap.get(
			propertiesFileLocation);

		if (existingProperties == null) {
			_propertiesMap.put(propertiesFileLocation, properties);
		}
		else {
			existingProperties.putAll(properties);

			_propertiesMap.put(propertiesFileLocation, existingProperties);
		}
	}

	private void _readProperties(String content, String propertiesFileLocation)
		throws Exception {

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		if (properties.isEmpty()) {
			return;
		}

		_readProperties(properties, propertiesFileLocation);
	}

	private void _runSourceProcessor(SourceProcessor sourceProcessor)
		throws Exception {

		sourceProcessor.setAllFileNames(_allFileNames);
		sourceProcessor.setPluginsInsideModulesDirectoryNames(
			_pluginsInsideModulesDirectoryNames);
		sourceProcessor.setPortalSource(_portalSource);
		sourceProcessor.setProjectPathPrefix(_projectPathPrefix);
		sourceProcessor.setPropertiesMap(_propertiesMap);
		sourceProcessor.setSourceFormatterArgs(_sourceFormatterArgs);
		sourceProcessor.setSourceFormatterConfiguration(
			_sourceFormatterConfiguration);
		sourceProcessor.setSourceFormatterExcludes(_sourceFormatterExcludes);
		sourceProcessor.setSourceFormatterSuppressions(
			_sourceFormatterSuppressions);
		sourceProcessor.setSubrepository(_subrepository);

		sourceProcessor.format();

		_modifiedFileNames.addAll(sourceProcessor.getModifiedFileNames());
		_sourceFormatterMessages.addAll(
			sourceProcessor.getSourceFormatterMessages());
		_sourceMismatchExceptions.addAll(
			sourceProcessor.getSourceMismatchExceptions());
	}

	private void _validateCommitMessages() throws Exception {
		List<String> commitMessages = GitUtil.getCurrentBranchCommitMessages(
			_sourceFormatterArgs.getBaseDirName(),
			_sourceFormatterArgs.getGitWorkingBranchName());

		JIRAUtil.validateJIRAProjectNames(
			commitMessages, _getPropertyValues("jira.project.keys"));

		for (String commitMessage : commitMessages) {
			String[] parts = commitMessage.split(":", 2);

			if ((parts[1].contains("This reverts commit") &&
				 parts[1].startsWith("Reapply \"")) ||
				parts[1].startsWith("Revert \"Revert")) {

				throw new Exception(
					StringBundler.concat(
						"Found formatting issue in SHA ", parts[0], ":\n",
						"Illegal nested revert, i.e. revert of a revert."));
			}

			for (String keyword :
					_getPropertyValues("git.commit.vulnerability.keywords")) {

				Pattern pattern = Pattern.compile(
					"\\b_*(" + keyword + ")_*\\b", Pattern.CASE_INSENSITIVE);

				Matcher matcher = pattern.matcher(parts[1]);

				if (matcher.find()) {
					throw new Exception(
						StringBundler.concat(
							"Found formatting issue in SHA ", parts[0], ":\n",
							"The commit message contains the word \"", keyword,
							"\", which could reveal potential security ",
							"vulnerabilities. Please see the vulnerability ",
							"keywords that are specified in source-formatter.",
							"properties in the liferay-portal repository."));
				}
			}
		}
	}

	private void _validatePullModeChanges() throws Exception {
		if (!_sourceFormatterArgs.isFormatCurrentBranch()) {
			return;
		}

		File portalDir = SourceFormatterUtil.getPortalDir(
			_sourceFormatterArgs.getBaseDirName(),
			_sourceFormatterArgs.getMaxLineLength());

		if (portalDir == null) {
			return;
		}

		List<String> pullModeGitRepoDirLocations = new ArrayList<>();

		List<String> gitRepoFileNames = SourceFormatterUtil.scanForFileNames(
			portalDir.getCanonicalPath(), new String[] {"**/*.gitrepo"});

		for (String gitRepoFileName : gitRepoFileNames) {
			int x = gitRepoFileName.indexOf("/modules/");

			if (x == -1) {
				continue;
			}

			String content = FileUtil.read(new File(gitRepoFileName));

			if (content.contains("mode = pull")) {
				int y = gitRepoFileName.lastIndexOf("/");

				pullModeGitRepoDirLocations.add(
					gitRepoFileName.substring(x + 1, y));
			}
		}

		if (pullModeGitRepoDirLocations.isEmpty()) {
			return;
		}

		List<String> fileNames = GitUtil.getCurrentBranchFileNames(
			_sourceFormatterArgs.getBaseDirName(),
			_sourceFormatterArgs.getGitWorkingBranchName(), true);

		for (String fileName : fileNames) {
			if (fileName.endsWith("/.gitrepo") ||
				fileName.endsWith("/ci-merge")) {

				continue;
			}

			for (String pullModeGitRepoDirLocation :
					pullModeGitRepoDirLocations) {

				if (fileName.startsWith(pullModeGitRepoDirLocation + "/")) {
					throw new Exception(
						StringBundler.concat(
							"Found formatting issue:\n",
							"Illegal change to a pull-only subdirectory ",
							pullModeGitRepoDirLocation));
				}
			}
		}
	}

	private static final String _PROPERTIES_FILE_NAME =
		"source-formatter.properties";

	private static final int _SUBREPOSITORY_MAX_DIR_LEVEL = 3;

	private List<String> _allFileNames;
	private final List<String> _modifiedFileNames =
		new CopyOnWriteArrayList<>();
	private List<String> _pluginsInsideModulesDirectoryNames;
	private boolean _portalSource;
	private String _projectPathPrefix;
	private Map<String, Properties> _propertiesMap;
	private final SourceFormatterArgs _sourceFormatterArgs;
	private SourceFormatterConfiguration _sourceFormatterConfiguration;
	private final SourceFormatterExcludes _sourceFormatterExcludes =
		new SourceFormatterExcludes();
	private final Set<SourceFormatterMessage> _sourceFormatterMessages =
		new ConcurrentSkipListSet<>();
	private SourceFormatterSuppressions _sourceFormatterSuppressions;
	private final List<SourceMismatchException> _sourceMismatchExceptions =
		new CopyOnWriteArrayList<>();
	private final List<SourceProcessor> _sourceProcessors = new ArrayList<>();
	private boolean _subrepository;

	private static class SourceFormatterMessageCheckNameComparator
		implements Comparator<SourceFormatterMessage> {

		@Override
		public int compare(
			SourceFormatterMessage sourceFormatterMessage1,
			SourceFormatterMessage sourceFormatterMessage2) {

			String checkName1 = sourceFormatterMessage1.getCheckName();
			String checkName2 = sourceFormatterMessage2.getCheckName();

			if ((checkName1 != null) && (checkName2 != null) &&
				!checkName1.equals(checkName2)) {

				return checkName1.compareTo(checkName2);
			}

			return sourceFormatterMessage1.compareTo(sourceFormatterMessage2);
		}

	}

}