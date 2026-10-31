/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.tools.java.parser.JavaParser;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.checkstyle.util.CheckstyleLogger;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.util.DebugUtil;
import com.liferay.source.formatter.util.PortalJSONObjectUtil;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class JavaSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws IOException {
		String[] includes = getIncludes();

		if (ArrayUtil.isEmpty(includes)) {
			return new ArrayList<>();
		}

		Collection<String> fileNames = null;

		if (isPortalSource() || isSubrepository()) {
			fileNames = _getPortalJavaFiles(includes);
		}
		else {
			fileNames = _getPluginJavaFiles(includes);
		}

		return new ArrayList<>(fileNames);
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected File format(
			File file, String fileName, String absolutePath, String content)
		throws Exception {

		file = super.format(file, fileName, absolutePath, content);

		_processCheckstyle(file);

		return file;
	}

	@Override
	protected String parse(
			File file, String fileName, String content,
			Set<String> modifiedMessages)
		throws Exception {

		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		if (!sourceFormatterArgs.isJavaParserEnabled()) {
			return content;
		}

		String newContent = JavaParser.parse(
			file, content, sourceFormatterArgs.getMaxLineLength(), false);

		if (!content.equals(newContent)) {
			modifiedMessages.add(file.toString() + " (JavaParser)");

			if (sourceFormatterArgs.isShowDebugInformation()) {
				DebugUtil.printContentModifications(
					"JavaParser", fileName, content, newContent);
			}
		}

		return newContent;
	}

	@Override
	protected void postFormat() throws Exception {
		_processCheckstyle(_ungeneratedFiles.toArray(new File[0]));

		_ungeneratedFiles.clear();

		for (SourceFormatterMessage sourceFormatterMessage :
				_sourceFormatterMessages) {

			String fileName = sourceFormatterMessage.getFileName();

			processMessage(fileName, sourceFormatterMessage);

			printError(fileName, sourceFormatterMessage.toString());
		}

		PortalJSONObjectUtil.deleteTempPortalJSONObjectFile();
	}

	@Override
	protected void preFormat() throws CheckstyleException {
		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		_checkstyleLogger = new CheckstyleLogger(
			sourceFormatterArgs.getBaseDirName());
		_checkstyleConfiguration = CheckstyleUtil.getConfiguration(
			"checkstyle.xml", getPropertiesMap(), sourceFormatterArgs);
	}

	private String[] _getPluginExcludes(String pluginDirectoryName) {
		return new String[] {
			pluginDirectoryName + "**/model/*Clp.java",
			pluginDirectoryName + "**/model/impl/*BaseImpl.java",
			pluginDirectoryName + "**/model/impl/*Model.java",
			pluginDirectoryName + "**/model/impl/*ModelImpl.java",
			pluginDirectoryName + "**/service/**/service/*Service.java",
			pluginDirectoryName + "**/service/**/service/*ServiceClp.java",
			pluginDirectoryName + "**/service/**/service/*ServiceFactory.java",
			pluginDirectoryName + "**/service/**/service/*ServiceUtil.java",
			pluginDirectoryName + "**/service/**/service/*ServiceWrapper.java",
			pluginDirectoryName + "**/service/**/service/ClpSerializer.java",
			pluginDirectoryName +
				"**/service/**/service/messaging/*ClpMessageListener.java",
			pluginDirectoryName +
				"**/service/**/service/persistence/*Finder.java",
			pluginDirectoryName +
				"**/service/**/service/persistence/*Util.java",
			pluginDirectoryName + "**/service/base/*ServiceBaseImpl.java",
			pluginDirectoryName + "**/service/base/*ServiceClpInvoker.java",
			pluginDirectoryName + "**/service/http/*JSONSerializer.java",
			pluginDirectoryName + "**/service/http/*ServiceHttp.java",
			pluginDirectoryName + "**/service/http/*ServiceJSON.java",
			pluginDirectoryName + "**/service/http/*ServiceSoap.java",
			pluginDirectoryName + "**/tools/templates/**"
		};
	}

	private Collection<String> _getPluginJavaFiles(String[] includes)
		throws IOException {

		Collection<String> fileNames = new TreeSet<>();

		String[] excludes = _getPluginExcludes(StringPool.BLANK);

		fileNames.addAll(getFileNames(excludes, includes));

		return fileNames;
	}

	private Collection<String> _getPortalJavaFiles(String[] includes)
		throws IOException {

		Collection<String> fileNames = new TreeSet<>();

		String[] excludes = {
			"**/*_IW.java", "**/*_jsp.java", "**/counter/service/**",
			"**/model/impl/*Model.java", "**/model/impl/*ModelImpl.java",
			"**/portal/service/**", "**/portal-web/test/**/*Test.java",
			"**/test/*-generated/**"
		};

		for (String directoryName : getPluginsInsideModulesDirectoryNames()) {
			excludes = ArrayUtil.append(
				excludes, _getPluginExcludes("**" + directoryName));
		}

		fileNames.addAll(getFileNames(excludes, includes));

		excludes = new String[] {
			"**/tools/ext_tmpl/**", "**/*_IW.java",
			"**/test/**/*PersistenceTest.java"
		};
		includes = new String[] {
			"**/com/liferay/portal/kernel/service/ServiceContext*.java",
			"**/model/BaseModel.java", "**/model/impl/BaseModelImpl.java",
			"**/portal-test/**/portal/service/**/*.java",
			"**/portal-test-integration/**/portal/service/**/*.java",
			"**/service/*.java", "**/service/configuration/**/*.java",
			"**/service/http/*HttpTest.java", "**/service/http/*SoapTest.java",
			"**/service/http/TunnelUtil.java", "**/service/impl/*.java",
			"**/service/jms/*.java", "**/service/permission/*.java",
			"**/service/persistence/BasePersistence.java",
			"**/service/persistence/BatchSession*.java",
			"**/service/persistence/*FinderImpl.java",
			"**/service/persistence/*Query.java",
			"**/service/persistence/impl/*.java",
			"**/portal-impl/test/**/*.java", "**/util-bridges/**/*.java"
		};

		fileNames.addAll(getFileNames(excludes, includes));

		return fileNames;
	}

	private void _processCheckstyle(File file) throws Exception {
		File[] files = null;

		synchronized (this) {
			_ungeneratedFiles.add(file);

			if (_ungeneratedFiles.size() == CheckstyleUtil.BATCH_SIZE) {
				files = _ungeneratedFiles.toArray(new File[0]);

				_ungeneratedFiles.clear();
			}
		}

		_processCheckstyle(files);
	}

	private void _processCheckstyle(File[] files) throws Exception {
		if (ArrayUtil.isEmpty(files)) {
			return;
		}

		Set<SourceFormatterMessage> sourceFormatterMessages = processCheckstyle(
			_checkstyleConfiguration, _checkstyleLogger, files);

		synchronized (this) {
			_sourceFormatterMessages.addAll(sourceFormatterMessages);
		}
	}

	private static final String[] _INCLUDES = {"**/*.java"};

	private Configuration _checkstyleConfiguration;
	private CheckstyleLogger _checkstyleLogger;
	private final Set<SourceFormatterMessage> _sourceFormatterMessages =
		new TreeSet<>();
	private final List<File> _ungeneratedFiles = new ArrayList<>();

}