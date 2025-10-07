/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.GitUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.processor.SourceProcessor;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class IllegalImportsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		content = StringUtil.replace(
			content,
			new String[] {
				"com.liferay.portal.PortalException",
				"com.liferay.portal.SystemException",
				"com.liferay.util.LocalizationUtil"
			},
			new String[] {
				"com.liferay.portal.kernel.exception.PortalException",
				"com.liferay.portal.kernel.exception.SystemException",
				"com.liferay.portal.kernel.util.LocalizationUtil"
			});

		if (isAttributeValue(
				_ENFORCE_JAVA_UTIL_FUNCTION_IMPORTS_KEY, absolutePath)) {

			content = StringUtil.replace(
				content,
				new String[] {
					"com.liferay.portal.kernel.util.Function",
					"com.liferay.portal.kernel.util.Supplier"
				},
				new String[] {
					"java.util.function.Function", "java.util.function.Supplier"
				});
		}

		if (!isExcludedPath(RUN_OUTSIDE_PORTAL_EXCLUDES, absolutePath) &&
			!isExcludedPath(_PROXY_EXCLUDES, absolutePath) &&
			content.contains("import java.lang.reflect.Proxy;")) {

			addMessage(
				fileName, "Use ProxyUtil instead of java.lang.reflect.Proxy");
		}

		if (content.contains("import edu.emory.mathcs.backport.java")) {
			addMessage(
				fileName, "Illegal import: edu.emory.mathcs.backport.java");
		}

		if (content.contains("import jodd.util.StringPool")) {
			addMessage(fileName, "Illegal import: jodd.util.StringPool");
		}

		if (!isExcludedPath(RUN_OUTSIDE_PORTAL_EXCLUDES, absolutePath) &&
			!isExcludedPath(_SECURE_RANDOM_EXCLUDES, absolutePath) &&
			content.contains("java.security.SecureRandom") &&
			!content.contains("javax.crypto.KeyGenerator")) {

			addMessage(
				fileName,
				"Use SecureRandomUtil or com.liferay.portal.kernel.security." +
					"SecureRandom instead of java.security.SecureRandom, see " +
						"LPS-39508");
		}

		if (content.contains(
				"com.liferay.portal.kernel.util.UnmodifiableList")) {

			addMessage(
				fileName,
				"Use java.util.Collections.unmodifiableList instead of " +
					"com.liferay.portal.kernel.util.UnmodifiableList, see " +
						"LPS-45027");
		}

		if (isPortalSource() && absolutePath.contains("/portal-kernel/") &&
			content.contains("import jakarta.servlet.jsp.")) {

			addMessage(
				fileName,
				"Never import jakarta.servlet.jsp.* from portal-kernel, see " +
					"LPS-47682");
		}

		if (content.contains("org.testng.Assert")) {
			addMessage(
				fileName,
				"Use org.junit.Assert instead of org.testng.Assert, see " +
					"LPS-55690");
		}

		if (content.contains(".supportsBatchUpdates()") &&
			!fileName.endsWith("AutoBatchPreparedStatementUtil.java")) {

			addMessage(
				fileName,
				"Use AutoBatchPreparedStatementUtil instead of " +
					"DatabaseMetaData.supportsBatchUpdates, see LPS-60473");
		}

		if (!fileName.endsWith("TypeConvertorUtil.java") &&
			content.contains("org.apache.commons.beanutils.PropertyUtils")) {

			addMessage(
				fileName,
				"Do not use org.apache.commons.beanutils.PropertyUtils, see " +
					"LPS-62786");
		}

		if (content.contains("Configurable.createConfigurable(") &&
			!fileName.endsWith("ConfigurableUtil.java")) {

			addMessage(
				fileName,
				"Use ConfigurableUtil.createConfigurable instead of " +
					"Configurable.createConfigurable, see LPS-64056");
		}

		if (fileName.endsWith("ResourceCommand.java") &&
			content.contains("ServletResponseUtil.sendFile(")) {

			addMessage(
				fileName,
				"Use PortletResponseUtil.sendFile instead of " +
					"ServletResponseUtil.sendFile, see LPS-65229");
		}

		if (content.contains("java.util.WeakHashMap")) {
			addMessage(
				fileName,
				"Do not use java.util.WeakHashMap because it is not " +
					"thread-safe, see LPS-70963");
		}

		if (isAttributeValue(_ENFORCE_COOKIES_MANAGER_UTIL_KEY, absolutePath) &&
			content.contains("com.liferay.portal.kernel.util.CookieKeys")) {

			addMessage(
				fileName,
				"Use com.liferay.portal.kernel.cookies.CookiesManagerUtil " +
					"instead of com.liferay.portal.kernel.util.CookieKeys, " +
						"see LPS-164101");
		}

		if (!isExcludedPath(RUN_OUTSIDE_PORTAL_EXCLUDES, absolutePath) &&
			content.contains("org.slf4j.Logger")) {

			addMessage(
				fileName,
				"Use com.liferay.portal.kernel.log.Log instead of " +
					"org.slf4j.Logger");
		}

		if (!absolutePath.contains("/modules/integrations/") &&
			!absolutePath.contains("/modules/sdk/")) {

			if (isAttributeValue(_AVOID_OPTIONAL_KEY, absolutePath) &&
				content.contains("java.util.Optional") &&
				!_isAllowedFileName(
					absolutePath,
					getAttributeValues(
						_ALLOWED_OPTIONAL_FILE_NAMES_KEY, absolutePath))) {

				addMessage(
					fileName, "Do not use java.util.Optional, see LPS-170503");
			}

			if (isAttributeValue(_AVOID_STREAM_KEY, absolutePath) &&
				content.contains("java.util.stream") &&
				!_isAllowedFileName(
					absolutePath,
					getAttributeValues(
						_ALLOWED_STREAM_FILE_NAMES_KEY, absolutePath))) {

				addMessage(
					fileName,
					"Do not use java.util.stream.Stream, see LPS-170503");
			}
		}

		SourceProcessor sourceProcessor = getSourceProcessor();

		SourceFormatterArgs sourceFormatterArgs =
			sourceProcessor.getSourceFormatterArgs();

		String currentBranchFileDiff = GitUtil.getCurrentBranchFileDiff(
			sourceFormatterArgs.getBaseDirName(),
			sourceFormatterArgs.getGitWorkingBranchName(), absolutePath);

		for (String line : StringUtil.splitLines(currentBranchFileDiff)) {
			if (!line.startsWith(StringPool.PLUS)) {
				continue;
			}

			for (String replacedTaglib :
					getAttributeValues(_REPLACED_TAGLIBS_KEY, absolutePath)) {

				String[] replacedTaglibArray = StringUtil.split(
					replacedTaglib, "->");

				if (replacedTaglibArray.length != 2) {
					continue;
				}

				if (line.contains(replacedTaglibArray[0])) {
					addMessage(
						fileName,
						StringBundler.concat(
							"Use ", replacedTaglibArray[1], " instead of ",
							replacedTaglibArray[0]));

					break;
				}
			}

			if (line.contains("org.jsoup.")) {
				addMessage(fileName, "Do not use org.jsoup, see LPD-42623");
			}
		}

		return content;
	}

	private boolean _isAllowedFileName(
		String absolutePath, List<String> allowedFileNames) {

		for (String allowedFileName : allowedFileNames) {
			if (absolutePath.endsWith(allowedFileName)) {
				return true;
			}
		}

		return false;
	}

	private static final String _ALLOWED_OPTIONAL_FILE_NAMES_KEY =
		"allowedOptionalFileNames";

	private static final String _ALLOWED_STREAM_FILE_NAMES_KEY =
		"allowedStreamFileNames";

	private static final String _AVOID_OPTIONAL_KEY = "avoidOptional";

	private static final String _AVOID_STREAM_KEY = "avoidStream";

	private static final String _ENFORCE_COOKIES_MANAGER_UTIL_KEY =
		"enforceCookiesManagerUtil";

	private static final String _ENFORCE_JAVA_UTIL_FUNCTION_IMPORTS_KEY =
		"enforceJavaUtilFunctionImports";

	private static final String _PROXY_EXCLUDES = "proxy.excludes";

	private static final String _REPLACED_TAGLIBS_KEY = "replacedTaglibs";

	private static final String _SECURE_RANDOM_EXCLUDES =
		"secure.random.excludes";

}