/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.check.FileCheck;
import com.liferay.source.formatter.check.GradleFileCheck;
import com.liferay.source.formatter.check.JavaTermCheck;
import com.liferay.source.formatter.check.SourceCheck;
import com.liferay.source.formatter.check.UpgradeCatchAllJavaTermOrderCheck;
import com.liferay.source.formatter.check.configuration.SourceCheckConfiguration;
import com.liferay.source.formatter.check.configuration.SourceChecksResult;
import com.liferay.source.formatter.check.configuration.SourceFormatterConfiguration;
import com.liferay.source.formatter.check.configuration.SourceFormatterSuppressions;
import com.liferay.source.formatter.checkstyle.util.CheckstyleUtil;
import com.liferay.source.formatter.parser.GradleFile;
import com.liferay.source.formatter.parser.GradleFileParser;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.ParseException;
import com.liferay.source.formatter.processor.SourceProcessor;
import com.liferay.source.formatter.util.CheckType;
import com.liferay.source.formatter.util.DebugUtil;
import com.liferay.source.formatter.util.SourceFormatterCheckUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;

import java.io.File;

import java.lang.reflect.Constructor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class SourceChecksUtil {

	public static Set<SourceCheck> getSourceChecks(
			SourceFormatterConfiguration sourceFormatterConfiguration,
			String sourceProcessorName, Map<String, Properties> propertiesMap,
			List<String> filterCheckNames,
			List<String> filterCheckCategoryNames, List<String> skipCheckNames,
			boolean portalSource, boolean subrepository,
			boolean includeModuleChecks)
		throws Exception {

		Set<SourceCheck> sourceChecks = _getSourceChecks(
			sourceFormatterConfiguration, sourceProcessorName, propertiesMap,
			filterCheckNames, filterCheckCategoryNames, skipCheckNames,
			portalSource, subrepository, includeModuleChecks);

		sourceChecks.addAll(
			_getSourceChecks(
				sourceFormatterConfiguration, "all", propertiesMap,
				filterCheckNames, filterCheckCategoryNames, skipCheckNames,
				includeModuleChecks, subrepository, includeModuleChecks));

		return sourceChecks;
	}

	public static SourceChecksResult processSourceChecks(
			File file, String fileName, String absolutePath, String content,
			SourceProcessor sourceProcessor, Set<String> modifiedMessages,
			boolean modulesFile, List<SourceCheck> sourceChecks,
			SourceFormatterSuppressions sourceFormatterSuppressions,
			boolean showDebugInformation)
		throws Exception {

		SourceChecksResult sourceChecksResult = new SourceChecksResult(content);

		if (ListUtil.isEmpty(sourceChecks)) {
			return sourceChecksResult;
		}

		GradleFile gradleFile = null;
		JavaClass javaClass = null;
		List<JavaClass> anonymousClasses = null;

		for (SourceCheck sourceCheck : sourceChecks) {
			if (!sourceCheck.isEnabled(absolutePath) ||
				(sourceCheck.isModuleSourceCheck() && !modulesFile)) {

				continue;
			}

			Class<?> clazz = sourceCheck.getClass();

			if (sourceFormatterSuppressions.isSuppressed(
					clazz.getSimpleName(), absolutePath)) {

				continue;
			}

			long startTime = System.currentTimeMillis();

			if (sourceCheck instanceof FileCheck) {
				sourceChecksResult = _processFileCheck(
					sourceProcessor, sourceChecksResult, (FileCheck)sourceCheck,
					fileName, absolutePath);
			}
			else if (sourceCheck instanceof GradleFileCheck) {
				if (gradleFile == null) {
					gradleFile = GradleFileParser.parse(
						fileName, sourceChecksResult.getContent());
				}

				sourceChecksResult = _processGradleFileCheck(
					sourceChecksResult, (GradleFileCheck)sourceCheck,
					gradleFile, fileName, absolutePath);
			}
			else if (!((sourceCheck instanceof
						UpgradeCatchAllJavaTermOrderCheck) &&
					   !fileName.endsWith(".java"))) {

				if (javaClass == null) {
					try {
						file = new File(absolutePath);

						FileText fileText = new FileText(
							file, CheckstyleUtil.getLines(content));

						DetailAST detailAST = JavaParser.parseFileText(
							fileText, JavaParser.Options.WITH_COMMENTS);
						FileContents fileContents = new FileContents(fileText);

						javaClass = JavaClassParser.parseJavaClass(
							sourceChecksResult.getContent(), detailAST,
							fileContents);

						anonymousClasses =
							JavaClassParser.parseAnonymousClasses(
								fileName, sourceChecksResult.getContent(),
								javaClass.getPackageName(),
								javaClass.getImportNames(), detailAST,
								fileContents);
					}
					catch (ParseException parseException) {
						sourceChecksResult.addSourceFormatterMessage(
							new SourceFormatterMessage(
								fileName, parseException.getMessage(),
								CheckType.SOURCE_CHECK,
								JavaClassParser.class.getSimpleName(), null,
								-1));

						continue;
					}
				}

				sourceChecksResult = _processJavaTermCheck(
					sourceProcessor, sourceChecksResult,
					(JavaTermCheck)sourceCheck, javaClass, anonymousClasses,
					fileName, absolutePath);
			}

			sourceChecksResult.setMostRecentProcessedSourceCheck(sourceCheck);

			if (showDebugInformation) {
				long endTime = System.currentTimeMillis();

				DebugUtil.increaseProcessingTime(
					clazz.getSimpleName(), endTime - startTime);
			}

			if (content.equals(sourceChecksResult.getContent())) {
				continue;
			}

			StringBundler sb = new StringBundler(7);

			sb.append(file.toString());
			sb.append(CharPool.SPACE);
			sb.append(CharPool.OPEN_PARENTHESIS);

			CheckType checkType = CheckType.SOURCE_CHECK;

			sb.append(checkType.getValue());

			sb.append(CharPool.COLON);
			sb.append(clazz.getSimpleName());
			sb.append(CharPool.CLOSE_PARENTHESIS);

			modifiedMessages.add(sb.toString());

			if (showDebugInformation) {
				DebugUtil.printContentModifications(
					clazz.getSimpleName(), fileName, content,
					sourceChecksResult.getContent());
			}

			return sourceChecksResult;
		}

		return sourceChecksResult;
	}

	private static JSONObject _getAttributesJSONObject(
		Map<String, Properties> propertiesMap, String checkName,
		SourceCheckConfiguration sourceCheckConfiguration) {

		JSONObject attributesJSONObject = new JSONObjectImpl();

		JSONObject configurationAttributesJSONObject =
			sourceCheckConfiguration.getAttributesJSONObject();

		if (configurationAttributesJSONObject.length() != 0) {
			attributesJSONObject.put(
				SourceFormatterCheckUtil.CONFIGURATION_FILE_LOCATION,
				configurationAttributesJSONObject);
		}

		attributesJSONObject = SourceFormatterCheckUtil.addPropertiesAttributes(
			attributesJSONObject, propertiesMap,
			SourceFormatterUtil.GIT_LIFERAY_PORTAL_BRANCH,
			SourceFormatterUtil.JAKARTA_USED_BRANCH,
			SourceFormatterUtil.UPGRADE_FROM_VERSION,
			SourceFormatterUtil.UPGRADE_TO_LIFERAY_VERSION,
			SourceFormatterUtil.UPGRADE_TO_RELEASE_VERSION);

		return SourceFormatterCheckUtil.addPropertiesAttributes(
			attributesJSONObject, propertiesMap, CheckType.SOURCE_CHECK,
			checkName);
	}

	private static Set<SourceCheck> _getSourceChecks(
			SourceFormatterConfiguration sourceFormatterConfiguration,
			String sourceProcessorName, Map<String, Properties> propertiesMap,
			List<String> filterCheckNames,
			List<String> filterCheckCategoryNames, List<String> skipCheckNames,
			boolean portalSource, boolean subrepository,
			boolean includeModuleChecks)
		throws Exception {

		Set<SourceCheck> sourceChecks = new TreeSet<>(
			new Comparator<SourceCheck>() {

				@Override
				public int compare(
					SourceCheck sourceCheck1, SourceCheck sourceCheck2) {

					int compare =
						sourceCheck2.getWeight() - sourceCheck1.getWeight();

					if (compare != 0) {
						return compare;
					}

					return sourceCheck1.hashCode() - sourceCheck2.hashCode();
				}

			});

		List<SourceCheckConfiguration> sourceCheckConfigurations =
			sourceFormatterConfiguration.getSourceCheckConfigurations(
				sourceProcessorName);

		if (sourceCheckConfigurations == null) {
			return sourceChecks;
		}

		JSONObject excludesJSONObject =
			SourceFormatterCheckUtil.getExcludesJSONObject(propertiesMap);

		for (SourceCheckConfiguration sourceCheckConfiguration :
				sourceCheckConfigurations) {

			String sourceCheckCategory = SourceFormatterUtil.getSimpleName(
				sourceCheckConfiguration.getCategory());
			String sourceCheckName = SourceFormatterUtil.getSimpleName(
				sourceCheckConfiguration.getName());

			if (((sourceCheckCategory.equals("JakartaTransform") ||
				  sourceCheckCategory.startsWith("Upgrade")) &&
				 !filterCheckCategoryNames.contains(sourceCheckCategory)) ||
				((!filterCheckCategoryNames.isEmpty() ||
				  !filterCheckNames.isEmpty()) &&
				 !filterCheckCategoryNames.contains(sourceCheckCategory) &&
				 !filterCheckNames.contains(sourceCheckName))) {

				continue;
			}

			sourceCheckName =
				"com.liferay.source.formatter.check." + sourceCheckName;

			Class<?> sourceCheckClass = null;

			try {
				sourceCheckClass = Class.forName(sourceCheckName);
			}
			catch (ClassNotFoundException classNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(classNotFoundException);
				}

				SourceFormatterUtil.printError(
					"sourcechecks.xml",
					"sourcechecks.xml: Class " + sourceCheckName +
						" cannot be found");

				continue;
			}

			Constructor<?> declaredConstructor =
				sourceCheckClass.getDeclaredConstructor();

			Object instance = declaredConstructor.newInstance();

			if (!(instance instanceof SourceCheck)) {
				continue;
			}

			SourceCheck sourceCheck = (SourceCheck)instance;

			if ((!portalSource && !subrepository &&
				 sourceCheck.isLiferaySourceCheck()) ||
				(!includeModuleChecks && sourceCheck.isModuleSourceCheck())) {

				continue;
			}

			Class<?> clazz = sourceCheck.getClass();

			if (skipCheckNames.contains(clazz.getSimpleName())) {
				continue;
			}

			JSONObject attributesJSONObject = _getAttributesJSONObject(
				propertiesMap, clazz.getSimpleName(), sourceCheckConfiguration);

			if (attributesJSONObject.length() != 0) {
				sourceCheck.setAttributes(attributesJSONObject.toString());
			}

			if (excludesJSONObject.length() != 0) {
				sourceCheck.setExcludes(excludesJSONObject.toString());
			}

			sourceCheck.setWeight(sourceCheckConfiguration.getWeight());

			sourceChecks.add(sourceCheck);
		}

		return sourceChecks;
	}

	private static SourceChecksResult _processFileCheck(
			SourceProcessor sourceProcessor,
			SourceChecksResult sourceChecksResult, FileCheck fileCheck,
			String fileName, String absolutePath)
		throws Exception {

		sourceChecksResult.setContent(
			fileCheck.process(
				sourceProcessor, fileName, absolutePath,
				sourceChecksResult.getContent()));

		for (SourceFormatterMessage sourceFormatterMessage :
				fileCheck.getSourceFormatterMessages(fileName)) {

			sourceChecksResult.addSourceFormatterMessage(
				sourceFormatterMessage);
		}

		return sourceChecksResult;
	}

	private static SourceChecksResult _processGradleFileCheck(
			SourceChecksResult sourceChecksResult,
			GradleFileCheck gradleFileCheck, GradleFile gradleFile,
			String fileName, String absolutePath)
		throws Exception {

		String content = gradleFileCheck.process(
			fileName, absolutePath, gradleFile,
			sourceChecksResult.getContent());

		sourceChecksResult.setContent(content);

		for (SourceFormatterMessage sourceFormatterMessage :
				gradleFileCheck.getSourceFormatterMessages(fileName)) {

			sourceChecksResult.addSourceFormatterMessage(
				sourceFormatterMessage);
		}

		return sourceChecksResult;
	}

	private static SourceChecksResult _processJavaTermCheck(
			SourceProcessor sourceProcessor,
			SourceChecksResult sourceChecksResult, JavaTermCheck javaTermCheck,
			JavaClass javaClass, List<JavaClass> anonymousClasses,
			String fileName, String absolutePath)
		throws Exception {

		sourceChecksResult.setContent(
			javaTermCheck.process(
				sourceProcessor, fileName, absolutePath, javaClass,
				sourceChecksResult.getContent()));

		for (SourceFormatterMessage sourceFormatterMessage :
				javaTermCheck.getSourceFormatterMessages(fileName)) {

			sourceChecksResult.addSourceFormatterMessage(
				sourceFormatterMessage);
		}

		for (JavaClass anonymousClass : anonymousClasses) {
			sourceChecksResult.setContent(
				javaTermCheck.process(
					sourceProcessor, fileName, absolutePath, anonymousClass,
					sourceChecksResult.getContent()));

			for (SourceFormatterMessage sourceFormatterMessage :
					javaTermCheck.getSourceFormatterMessages(fileName)) {

				sourceChecksResult.addSourceFormatterMessage(
					sourceFormatterMessage);
			}
		}

		return sourceChecksResult;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SourceChecksUtil.class);

}