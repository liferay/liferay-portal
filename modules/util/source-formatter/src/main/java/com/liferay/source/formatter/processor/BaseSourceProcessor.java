/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.nio.CharsetDecoderUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.check.SourceCheck;
import com.liferay.source.formatter.check.configuration.SourceChecksResult;
import com.liferay.source.formatter.check.configuration.SourceFormatterConfiguration;
import com.liferay.source.formatter.check.configuration.SourceFormatterSuppressions;
import com.liferay.source.formatter.check.util.SourceChecksUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.checkstyle.Checker;
import com.liferay.source.formatter.checkstyle.util.CheckstyleLogger;
import com.liferay.source.formatter.exception.SourceMismatchException;
import com.liferay.source.formatter.exception.UpgradeCatchAllException;
import com.liferay.source.formatter.util.DebugUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.tools.ant.types.selectors.SelectorUtils;

/**
 * @author Brian Wing Shun Chan
 * @author Igor Spasic
 * @author Wesley Gong
 * @author Hugo Huijser
 */
public abstract class BaseSourceProcessor implements SourceProcessor {

	@Override
	public final void format() throws Exception {
		List<String> fileNames = getFileNames();

		if (_sourceFormatterArgs.isShowDebugInformation()) {
			Class<?> clazz = getClass();

			DebugUtil.addProcessorFileCount(
				clazz.getSimpleName(), fileNames.size());
		}

		if (fileNames.isEmpty()) {
			return;
		}

		preFormat();

		_sourceChecks = _getSourceChecks(fileNames);

		ExecutorService executorService = Executors.newFixedThreadPool(
			_sourceFormatterArgs.getProcessorThreadCount());

		List<Future<Void>> futures = new ArrayList<>(fileNames.size());

		for (final String fileName : fileNames) {
			Future<Void> future = executorService.submit(
				new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						_performTask(fileName);

						return null;
					}

				});

			futures.add(future);
		}

		for (Future<Void> future : futures) {
			future.get();
		}

		executorService.shutdown();

		postFormat();
	}

	public final List<String> getFileNames() throws Exception {
		List<String> fileNames = _sourceFormatterArgs.getFileNames();

		if (fileNames != null) {
			return SourceFormatterUtil.filterFileNames(
				fileNames, new String[0], getIncludes(),
				new SourceFormatterExcludes(), false);
		}

		return doGetFileNames();
	}

	@Override
	public String[] getIncludes() {
		return filterIncludes(doGetIncludes());
	}

	@Override
	public List<String> getModifiedFileNames() {
		return _modifiedFileNames;
	}

	@Override
	public SourceFormatterArgs getSourceFormatterArgs() {
		return _sourceFormatterArgs;
	}

	@Override
	public Set<SourceFormatterMessage> getSourceFormatterMessages() {
		Set<SourceFormatterMessage> sourceFormatterMessages = new TreeSet<>();

		for (Map.Entry<String, Set<SourceFormatterMessage>> entry :
				_sourceFormatterMessagesMap.entrySet()) {

			sourceFormatterMessages.addAll(entry.getValue());
		}

		return sourceFormatterMessages;
	}

	@Override
	public List<SourceMismatchException> getSourceMismatchExceptions() {
		return _sourceMismatchExceptions;
	}

	@Override
	public boolean isPortalSource() {
		return _portalSource;
	}

	@Override
	public boolean isSubrepository() {
		return _subrepository;
	}

	@Override
	public void setAllFileNames(List<String> allFileNames) {
		_allFileNames = allFileNames;
	}

	@Override
	public void setPluginsInsideModulesDirectoryNames(
		List<String> pluginsInsideModulesDirectoryNames) {

		_pluginsInsideModulesDirectoryNames =
			pluginsInsideModulesDirectoryNames;
	}

	@Override
	public void setPortalSource(boolean portalSource) {
		_portalSource = portalSource;
	}

	@Override
	public void setProjectPathPrefix(String projectPathPrefix) {
		_projectPathPrefix = projectPathPrefix;
	}

	@Override
	public void setPropertiesMap(Map<String, Properties> propertiesMap) {
		_propertiesMap = propertiesMap;
	}

	@Override
	public void setSourceFormatterArgs(
		SourceFormatterArgs sourceFormatterArgs) {

		_sourceFormatterArgs = sourceFormatterArgs;
	}

	@Override
	public void setSourceFormatterConfiguration(
		SourceFormatterConfiguration sourceFormatterConfiguration) {

		_sourceFormatterConfiguration = sourceFormatterConfiguration;
	}

	@Override
	public void setSourceFormatterExcludes(
		SourceFormatterExcludes sourceFormatterExcludes) {

		_sourceFormatterExcludes = sourceFormatterExcludes;
	}

	@Override
	public void setSourceFormatterSuppressions(
		SourceFormatterSuppressions sourceFormatterSuppressions) {

		_sourceFormatterSuppressions = sourceFormatterSuppressions;
	}

	@Override
	public void setSubrepository(boolean subrepository) {
		_subrepository = subrepository;
	}

	protected abstract List<String> doGetFileNames() throws Exception;

	protected abstract String[] doGetIncludes();

	protected String[] filterIncludes(String[] includes) {
		List<String> fileExtensions = _sourceFormatterArgs.getFileExtensions();

		if (fileExtensions.isEmpty()) {
			return includes;
		}

		String[] filteredIncludes = new String[0];

		for (String include : includes) {
			for (String fileExtension : fileExtensions) {
				if (include.endsWith(fileExtension)) {
					filteredIncludes = ArrayUtil.append(
						filteredIncludes, include);
				}
			}
		}

		return filteredIncludes;
	}

	protected File format(
			File file, String fileName, String absolutePath, String content)
		throws Exception {

		Set<String> modifiedContents = new HashSet<>();
		Set<String> modifiedMessages = new TreeSet<>();

		String originalReturnCharacter = StringPool.NEW_LINE;

		if (content.contains(StringPool.RETURN_NEW_LINE)) {
			originalReturnCharacter = StringPool.RETURN_NEW_LINE;
		}
		else if (content.contains(StringPool.RETURN)) {
			originalReturnCharacter = StringPool.RETURN;
		}

		String preFormattedContent = preFormat(
			file, fileName, content, modifiedMessages, originalReturnCharacter);

		preFormattedContent = format(
			file, fileName, absolutePath, preFormattedContent,
			preFormattedContent, new ArrayList<>(_sourceChecks),
			modifiedContents, modifiedMessages, 0);

		String newContent = postFormat(
			preFormattedContent, originalReturnCharacter);

		return processFormattedFile(
			file, fileName, content, newContent, modifiedMessages);
	}

	protected String format(
			File file, String fileName, String absolutePath, String content,
			String originalContent, List<SourceCheck> sourceChecks,
			Set<String> modifiedContents, Set<String> modifiedMessages,
			int count)
		throws Exception {

		_sourceFormatterMessagesMap.remove(fileName);

		String newContent = content;

		newContent = parse(file, fileName, newContent, modifiedMessages);

		SourceChecksResult sourceChecksResult = _processSourceChecks(
			file, fileName, absolutePath, newContent, sourceChecks,
			modifiedMessages);

		newContent = sourceChecksResult.getContent();

		if ((newContent == null) || content.equals(newContent)) {
			return newContent;
		}

		if (!modifiedContents.add(newContent)) {
			_sourceFormatterMessagesMap.remove(fileName);

			processMessage(fileName, "Infinite loop in SourceFormatter");

			return originalContent;
		}

		if (newContent.length() > content.length()) {
			count++;

			if (count > 10000) {
				_sourceFormatterMessagesMap.remove(fileName);

				processMessage(fileName, "Infinite loop in SourceFormatter");

				return originalContent;
			}
		}
		else {
			count = 0;
		}

		SourceCheck sourceCheck =
			sourceChecksResult.getMostRecentProcessedSourceCheck();

		if (sourceCheck != null) {
			sourceChecks.remove(sourceCheck);

			sourceChecks.add(0, sourceCheck);
		}

		return format(
			file, fileName, absolutePath, newContent, originalContent,
			sourceChecks, modifiedContents, modifiedMessages, count);
	}

	protected List<String> getAllFileNames() {
		return _allFileNames;
	}

	protected File getFile(String fileName, int level) {
		return SourceFormatterUtil.getFile(
			_sourceFormatterArgs.getBaseDirName(), fileName, level);
	}

	protected List<String> getFileNames(String[] excludes, String[] includes)
		throws IOException {

		return getFileNames(excludes, includes, false);
	}

	protected List<String> getFileNames(
			String[] excludes, String[] includes, boolean forceIncludeAllFiles)
		throws IOException {

		if (!forceIncludeAllFiles &&
			SetUtil.isNotEmpty(
				_sourceFormatterArgs.getRecentChangesFileNames())) {

			return SourceFormatterUtil.filterRecentChangesFileNames(
				_sourceFormatterArgs.getRecentChangesFileNames(), excludes,
				includes, _sourceFormatterExcludes);
		}

		return SourceFormatterUtil.filterFileNames(
			_allFileNames, excludes, includes, _sourceFormatterExcludes,
			forceIncludeAllFiles);
	}

	protected List<String> getPluginsInsideModulesDirectoryNames() {
		return _pluginsInsideModulesDirectoryNames;
	}

	protected File getPortalDir() {
		File portalImplDir = SourceFormatterUtil.getFile(
			_sourceFormatterArgs.getBaseDirName(), "portal-impl",
			_sourceFormatterArgs.getMaxDirLevel());

		if (portalImplDir == null) {
			return null;
		}

		return portalImplDir.getParentFile();
	}

	protected Map<String, Properties> getPropertiesMap() {
		return _propertiesMap;
	}

	protected Set<SourceCheck> getSourceChecks() {
		return _sourceChecks;
	}

	protected SourceFormatterExcludes getSourceFormatterExcludes() {
		return _sourceFormatterExcludes;
	}

	protected SourceFormatterSuppressions getSourceFormatterSuppressions() {
		return _sourceFormatterSuppressions;
	}

	protected boolean hasGeneratedTag(String content) {
		if (SourceUtil.containsUnquoted(content, "@generated") ||
			SourceUtil.containsUnquoted(content, "$ANTLR") ||
			SourceUtil.containsUnquoted(
				content, "# This is a generated file.") ||
			SourceUtil.containsUnquoted(content, "## Autogenerated")) {

			return true;
		}

		return false;
	}

	protected String parse(
			File file, String fileName, String content,
			Set<String> modifiedMessages)
		throws Exception {

		return content;
	}

	protected void postFormat() throws Exception {
	}

	protected String postFormat(
		String content, String originalReturnCharacter) {

		List<String> checkCategoryNames =
			_sourceFormatterArgs.getCheckCategoryNames();

		if (checkCategoryNames.isEmpty() &&
			(isPortalSource() || isSubrepository()) &&
			originalReturnCharacter.equals(StringPool.NEW_LINE)) {

			return content;
		}

		return StringUtil.replace(
			content, CharPool.NEW_LINE, originalReturnCharacter);
	}

	protected void preFormat() throws Exception {
	}

	protected String preFormat(
			File file, String fileName, String content,
			Set<String> modifiedMessages, String originalReturnCharacter)
		throws Exception {

		List<String> checkCategoryNames =
			_sourceFormatterArgs.getCheckCategoryNames();

		if (checkCategoryNames.isEmpty() &&
			(isPortalSource() || isSubrepository())) {

			_checkUTF8(file, fileName);
		}

		if (originalReturnCharacter.equals(StringPool.NEW_LINE)) {
			return content;
		}

		String newContent = StringUtil.replace(
			content, originalReturnCharacter, StringPool.NEW_LINE);

		if (checkCategoryNames.isEmpty() &&
			(isPortalSource() || isSubrepository())) {

			modifiedMessages.add(file.toString() + " (ReturnCharacter)");
		}

		return newContent;
	}

	protected void printError(String fileName, String message) {
		if (_sourceFormatterArgs.isPrintErrors()) {
			SourceFormatterUtil.printError(fileName, message);
		}
	}

	protected Set<SourceFormatterMessage> processCheckstyle(
			Configuration configuration, CheckstyleLogger checkstyleLogger,
			Object object)
		throws CheckstyleException, IOException {

		synchronized (BaseSourceProcessor.class) {
			Checker checker = new Checker(
				configuration, checkstyleLogger, checkstyleLogger,
				getSourceFormatterSuppressions());

			if (object instanceof File[]) {
				checker.process(Arrays.asList((File[])object));
			}
			else if (object instanceof List<?>) {
				checker.processFileContents((List<String[]>)object);
			}

			return checker.getSourceFormatterMessages();
		}
	}

	protected File processFormattedFile(
			File file, String fileName, String content, String newContent,
			Set<String> modifiedMessages)
		throws IOException {

		if (!content.equals(newContent)) {
			if (_sourceFormatterArgs.isPrintErrors()) {
				for (String modifiedMessage : modifiedMessages) {
					SourceFormatterUtil.printError(fileName, modifiedMessage);
				}
			}

			if (_sourceFormatterArgs.isAutoFix()) {
				if (newContent != null) {
					FileUtil.write(file, newContent);
				}
				else {
					file.delete();
				}
			}

			_sourceMismatchExceptions.add(
				new SourceMismatchException(fileName, content, newContent));
		}

		if (_sourceFormatterArgs.isPrintErrors()) {
			Set<SourceFormatterMessage> sourceFormatterMessages =
				_sourceFormatterMessagesMap.get(fileName);

			if (sourceFormatterMessages != null) {
				for (SourceFormatterMessage sourceFormatterMessage :
						sourceFormatterMessages) {

					SourceFormatterUtil.printError(
						fileName, sourceFormatterMessage.toString());
				}
			}
		}

		_modifiedFileNames.add(file.getAbsolutePath());

		return file;
	}

	protected void processMessage(
		String fileName, SourceFormatterMessage sourceFormatterMessage) {

		Set<SourceFormatterMessage> sourceFormatterMessages =
			_sourceFormatterMessagesMap.get(fileName);

		if (sourceFormatterMessages == null) {
			sourceFormatterMessages = new TreeSet<>();
		}

		sourceFormatterMessages.add(sourceFormatterMessage);

		_sourceFormatterMessagesMap.put(fileName, sourceFormatterMessages);
	}

	protected void processMessage(String fileName, String message) {
		processMessage(fileName, new SourceFormatterMessage(fileName, message));
	}

	private void _checkUTF8(File file, String fileName) throws Exception {
		byte[] bytes = FileUtil.getBytes(file);

		try {
			CharsetDecoder charsetDecoder =
				CharsetDecoderUtil.getCharsetDecoder(
					StringPool.UTF8, CodingErrorAction.REPORT);

			charsetDecoder.decode(ByteBuffer.wrap(bytes));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			processMessage(fileName, "UTF-8");
		}
	}

	private boolean _containsModuleFile(List<String> fileNames) {
		if (_subrepository) {
			return true;
		}

		if (!_portalSource) {
			return false;
		}

		for (String fileName : fileNames) {
			if (!_isMatchPath(fileName)) {
				continue;
			}

			if (_isModulesFile(SourceUtil.getAbsolutePath(fileName), true)) {
				return true;
			}
		}

		return false;
	}

	private void _format(String fileName) throws Exception {
		if (!_isMatchPath(fileName)) {
			return;
		}

		fileName = StringUtil.replace(
			fileName, CharPool.BACK_SLASH, CharPool.SLASH);

		String absolutePath = SourceUtil.getAbsolutePath(fileName);

		File file = new File(absolutePath);

		String content = FileUtil.read(file, false);

		if (!_sourceFormatterArgs.isIncludeGeneratedFiles() &&
			hasGeneratedTag(content)) {

			return;
		}

		format(file, fileName, absolutePath, content);
	}

	private Set<SourceCheck> _getSourceChecks(List<String> fileNames)
		throws Exception {

		Class<?> clazz = getClass();

		Set<SourceCheck> sourceChecks = SourceChecksUtil.getSourceChecks(
			_sourceFormatterConfiguration, clazz.getSimpleName(),
			getPropertiesMap(), _sourceFormatterArgs.getCheckNames(),
			_sourceFormatterArgs.getCheckCategoryNames(),
			_sourceFormatterArgs.getSkipCheckNames(), _portalSource,
			_subrepository, _containsModuleFile(fileNames));

		for (SourceCheck sourceCheck : sourceChecks) {
			_initSourceCheck(sourceCheck);
		}

		return sourceChecks;
	}

	private void _initSourceCheck(SourceCheck sourceCheck) {
		sourceCheck.setAllFileNames(_allFileNames);
		sourceCheck.setBaseDirName(_sourceFormatterArgs.getBaseDirName());
		sourceCheck.setFileExtensions(_sourceFormatterArgs.getFileExtensions());
		sourceCheck.setFilterCheckNames(_sourceFormatterArgs.getCheckNames());
		sourceCheck.setMaxDirLevel(_sourceFormatterArgs.getMaxDirLevel());
		sourceCheck.setMaxLineLength(_sourceFormatterArgs.getMaxLineLength());
		sourceCheck.setPluginsInsideModulesDirectoryNames(
			_pluginsInsideModulesDirectoryNames);
		sourceCheck.setPortalSource(_portalSource);
		sourceCheck.setProjectPathPrefix(_projectPathPrefix);
		sourceCheck.setSourceFormatterExcludes(_sourceFormatterExcludes);
		sourceCheck.setSubrepository(_subrepository);
	}

	private boolean _isMatchPath(String fileName) {
		for (String pattern : getIncludes()) {
			if (SelectorUtils.matchPath(_normalizePattern(pattern), fileName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isModulesFile(String absolutePath) {
		return _isModulesFile(absolutePath, false);
	}

	private boolean _isModulesFile(
		String absolutePath, boolean includePlugins) {

		if (_subrepository ||
			absolutePath.contains(
				SourceFormatterUtil.SOURCE_FORMATTER_TEST_PATH)) {

			return true;
		}

		if (!_portalSource) {
			return false;
		}

		if (includePlugins) {
			return absolutePath.contains("/modules/");
		}

		try {
			for (String directoryName : _pluginsInsideModulesDirectoryNames) {
				if (absolutePath.contains(directoryName)) {
					return false;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return absolutePath.contains("/modules/");
	}

	private String _normalizePattern(String originalPattern) {
		String pattern = StringUtil.replace(
			originalPattern, CharPool.SLASH, File.separatorChar);

		pattern = StringUtil.replace(
			pattern, CharPool.BACK_SLASH, File.separatorChar);

		if (pattern.endsWith(File.separator)) {
			pattern += SelectorUtils.DEEP_TREE_MATCH;
		}

		return pattern;
	}

	private void _performTask(String fileName) throws Exception {
		try {
			if (!_sourceFormatterArgs.isShowDebugInformation()) {
				_format(fileName);

				return;
			}

			DebugUtil.startTask();

			_format(fileName);

			DebugUtil.finishTask();
		}
		catch (UpgradeCatchAllException upgradeCatchAllException) {
			throw upgradeCatchAllException;
		}
		catch (Throwable throwable) {
			throw new RuntimeException(
				"Unable to format " + fileName, throwable);
		}
	}

	private SourceChecksResult _processSourceChecks(
			File file, String fileName, String absolutePath, String content,
			List<SourceCheck> sourceChecks, Set<String> modifiedMessages)
		throws Exception {

		SourceChecksResult sourceChecksResult =
			SourceChecksUtil.processSourceChecks(
				file, fileName, absolutePath, content, this, modifiedMessages,
				_isModulesFile(absolutePath), sourceChecks,
				_sourceFormatterSuppressions,
				_sourceFormatterArgs.isShowDebugInformation());

		for (SourceFormatterMessage sourceFormatterMessage :
				sourceChecksResult.getSourceFormatterMessages()) {

			processMessage(fileName, sourceFormatterMessage);
		}

		return sourceChecksResult;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSourceProcessor.class);

	private List<String> _allFileNames;
	private final List<String> _modifiedFileNames =
		new CopyOnWriteArrayList<>();
	private List<String> _pluginsInsideModulesDirectoryNames;
	private boolean _portalSource;
	private String _projectPathPrefix;
	private Map<String, Properties> _propertiesMap;
	private Set<SourceCheck> _sourceChecks;
	private SourceFormatterArgs _sourceFormatterArgs;
	private SourceFormatterConfiguration _sourceFormatterConfiguration;
	private SourceFormatterExcludes _sourceFormatterExcludes;
	private final Map<String, Set<SourceFormatterMessage>>
		_sourceFormatterMessagesMap = new ConcurrentHashMap<>();
	private SourceFormatterSuppressions _sourceFormatterSuppressions;
	private final List<SourceMismatchException> _sourceMismatchExceptions =
		new ArrayList<>();
	private boolean _subrepository;

}