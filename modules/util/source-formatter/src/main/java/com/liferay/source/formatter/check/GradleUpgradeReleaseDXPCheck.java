/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.GradleBuildFile;
import com.liferay.source.formatter.util.GradleDependency;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Kevin Lee
 */
public class GradleUpgradeReleaseDXPCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		String upgradeToVersion = getAttributeValue(
			SourceFormatterUtil.UPGRADE_TO_RELEASE_VERSION, absolutePath);

		if (Validator.isNull(upgradeToVersion)) {
			return content;
		}

		String artifactName = "release\\.dxp\\.api";

		content = content.replaceAll(
			StringBundler.concat(
				"(\"com\\.liferay\\.portal:", artifactName, ":)[^\"\\s]+\""),
			StringBundler.concat("$1", upgradeToVersion, CharPool.QUOTE));
		content = content.replaceAll(
			StringBundler.concat(
				"(name\\s*:\\s*\"", artifactName,
				"\"\\s*,\\s*version\\s*:\\s*\")[^\"]+\""),
			StringBundler.concat("$1", upgradeToVersion, CharPool.QUOTE));
		content = content.replaceAll(
			StringBundler.concat(
				"(version\\s*:\\s*\")[^\"]+(\"\\s*,\\s*name\\s*:\\s*\"",
				artifactName, "\")"),
			StringBundler.concat("$1", upgradeToVersion, "$2"));

		return _formatDependencies(content, upgradeToVersion);
	}

	private String _formatDependencies(
		String content, String upgradeToVersion) {

		Map<String, Set<String>> releaseDXPDependencies =
			_getReleaseDXPDependencies(upgradeToVersion);

		if (releaseDXPDependencies == null) {
			return content;
		}

		GradleBuildFile gradleBuildFile = new GradleBuildFile(content);

		List<GradleDependency> gradleDependencies =
			gradleBuildFile.getGradleDependencies();

		Iterator<GradleDependency> iterator = gradleDependencies.iterator();

		boolean hasCompileOrImplementation = false;
		boolean hasTest = false;

		while (iterator.hasNext()) {
			GradleDependency gradleDependency = iterator.next();

			Set<String> names = releaseDXPDependencies.get(
				gradleDependency.getGroup());

			if ((names == null) ||
				!names.contains(gradleDependency.getName())) {

				iterator.remove();

				continue;
			}

			String configuration = gradleDependency.getConfiguration();

			if (configuration.startsWith("compile") ||
				configuration.startsWith("implementation")) {

				hasCompileOrImplementation = true;
			}

			if (configuration.startsWith("test")) {
				hasTest = true;
			}
		}

		if (gradleDependencies.isEmpty()) {
			return content;
		}

		gradleBuildFile.deleteGradleDependencies(gradleDependencies);

		if (hasCompileOrImplementation) {
			gradleBuildFile.insertGradleDependency(
				"compileOnly", "com.liferay.portal", "release.dxp.api",
				upgradeToVersion);
		}

		if (hasTest) {
			gradleBuildFile.insertGradleDependency(
				"testImplementation", "com.liferay.portal", "release.dxp.api",
				upgradeToVersion);
		}

		return gradleBuildFile.getSource();
	}

	private Map<String, Set<String>> _getReleaseDXPDependencies(
		String upgradeToVersion) {

		if (Objects.equals(upgradeToVersion, _upgradeToVersion)) {
			return _releaseDXPDependencies;
		}

		try {
			URL url = new URL(_getReleaseDXPPomURL(upgradeToVersion));

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)url.openConnection();

			httpURLConnection.setConnectTimeout(10000);
			httpURLConnection.setReadTimeout(10000);
			httpURLConnection.setRequestMethod(HttpMethods.GET);

			String content = StringUtil.read(
				httpURLConnection.getInputStream());

			if (Objects.equals(content, StringPool.BLANK)) {
				return null;
			}

			_upgradeToVersion = upgradeToVersion;
			_releaseDXPDependencies = _readReleaseDXPDependencies(content);
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}

		return _releaseDXPDependencies;
	}

	private String _getReleaseDXPPomURL(String upgradeToVersion) {
		String baseURL =
			"https://repository-cdn.liferay.com/nexus/content/repositories" +
				"/liferay-public-releases/com/liferay/portal/release.dxp.bom/";

		return StringBundler.concat(
			baseURL, upgradeToVersion, "/release.dxp.bom-", upgradeToVersion,
			".pom");
	}

	private Map<String, Set<String>> _readReleaseDXPDependencies(
		String content) {

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return null;
		}

		Element rootElement = document.getRootElement();

		Element dependencyManagementElement = rootElement.element(
			"dependencyManagement");

		Element dependenciesElement = dependencyManagementElement.element(
			"dependencies");

		List<Element> dependencyElements = dependenciesElement.elements(
			"dependency");

		Map<String, Set<String>> dependencies = new HashMap<>();

		for (Element dependencyElement : dependencyElements) {
			Element groupIdElement = dependencyElement.element("groupId");
			Element artifactIdElement = dependencyElement.element("artifactId");

			String groupId = groupIdElement.getText();
			String artifactId = artifactIdElement.getText();

			dependencies.putIfAbsent(groupId, new HashSet<>());

			Set<String> artifactIds = dependencies.get(groupId);

			artifactIds.add(artifactId);
		}

		return dependencies;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GradleUpgradeReleaseDXPCheck.class);

	private Map<String, Set<String>> _releaseDXPDependencies;
	private String _upgradeToVersion;

}