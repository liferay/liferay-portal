/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalHotfixRelease {

	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		PortalFixpackRelease portalFixpackRelease = getPortalFixpackRelease();

		if (portalFixpackRelease != null) {
			jsonObject.put(
				"portalFixpackURL",
				String.valueOf(portalFixpackRelease.getPortalFixpackURL()));
		}

		jsonObject.put(
			"portalHotfixURL", String.valueOf(getPortalHotfixReleaseURL()));

		PortalRelease portalRelease = getPortalRelease();

		if (portalRelease != null) {
			jsonObject.put(
				"portalReleaseURL",
				String.valueOf(portalRelease.getPortalBundleTomcatURL()));
		}

		return jsonObject;
	}

	public Set<String> getModifiedPackageNames() {
		Set<String> packageNames = _getJSONPackageNames();

		if (packageNames == null) {
			packageNames = _getXMLPackageNames();
		}

		if ((packageNames == null) || packageNames.isEmpty()) {
			return new HashSet<>();
		}

		Set<String> modifiedPackageNames = new HashSet<>();

		for (String packageName : packageNames) {
			if (JenkinsResultsParserUtil.isNullOrEmpty(packageName)) {
				continue;
			}

			if (packageName.contains("/")) {
				packageName = packageName.substring(
					packageName.lastIndexOf("/") + 1);
			}

			Matcher matcher = _packageNamePattern.matcher(packageName);

			if (!matcher.find()) {
				continue;
			}

			modifiedPackageNames.add(matcher.group("packageName"));
		}

		return modifiedPackageNames;
	}

	public PortalFixpackRelease getPortalFixpackRelease() {
		return _portalFixpackRelease;
	}

	public String getPortalHotfixReleaseName() {
		String portalHotfixReleaseURLString = String.valueOf(
			_portalHotfixReleaseURL);

		Matcher matcher = _hotfixURLPattern.matcher(
			portalHotfixReleaseURLString);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("hotfixName");
	}

	public URL getPortalHotfixReleaseURL() {
		return _portalHotfixReleaseURL;
	}

	public String getPortalHotfixReleaseVersion() {
		String portalHotfixReleaseURLString = String.valueOf(
			_portalHotfixReleaseURL);

		Matcher matcher = _hotfixURLPattern.matcher(
			portalHotfixReleaseURLString);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("hotfixVersion");
	}

	public PortalRelease getPortalRelease() {
		return _portalRelease;
	}

	protected PortalHotfixRelease(JSONObject jsonObject) {
		_portalHotfixReleaseURL = _getURL(
			jsonObject.getString("portalHotfixURL"));

		if (_portalHotfixReleaseURL == null) {
			throw new RuntimeException("Unable to get Portal Hotfix URL");
		}

		URL portalFixpackURL = _getURL(
			jsonObject.optString("portalFixpackURL"));

		if (portalFixpackURL != null) {
			_portalFixpackRelease =
				PortalReleaseFactory.newPortalFixpackRelease(portalFixpackURL);
		}
		else {
			_portalFixpackRelease = null;
		}

		URL portalReleaseURL = _getURL(
			jsonObject.optString("portalReleaseURL"));

		if (portalReleaseURL != null) {
			_portalRelease = PortalReleaseFactory.newPortalRelease(
				portalReleaseURL);
		}
		else {
			_portalRelease = null;
		}
	}

	protected PortalHotfixRelease(
		URL portalHotfixReleaseURL, PortalFixpackRelease portalFixpackRelease,
		PortalRelease portalRelease) {

		_portalHotfixReleaseURL = _getURL(
			String.valueOf(portalHotfixReleaseURL));
		_portalFixpackRelease = portalFixpackRelease;
		_portalRelease = portalRelease;
	}

	private Element _getFixpackDocumentationElement() {
		synchronized (_portalHotfixReleaseURL) {
			if (_fixpackDocumentationElement != null) {
				return _fixpackDocumentationElement;
			}

			File tempDir = new File(
				JenkinsResultsParserUtil.getDistinctTimeStamp());

			try {
				tempDir.mkdirs();

				File hotfixFile = new File(tempDir, "hotfix.zip");

				JenkinsResultsParserUtil.toFile(
					getPortalHotfixReleaseURL(), hotfixFile);

				JenkinsResultsParserUtil.unzip(hotfixFile, tempDir);

				File fixpackDocumentationFile = new File(
					tempDir, "fixpack_documentation.xml");

				if (!fixpackDocumentationFile.exists()) {
					return null;
				}

				Document document = Dom4JUtil.parse(
					JenkinsResultsParserUtil.read(fixpackDocumentationFile));

				_fixpackDocumentationElement = document.getRootElement();

				return _fixpackDocumentationElement;
			}
			catch (Exception exception) {
				return null;
			}
			finally {
				JenkinsResultsParserUtil.delete(tempDir);
			}
		}
	}

	private JSONObject _getFixpackDocumentationJSONObject() {
		synchronized (_portalHotfixReleaseURL) {
			if (_fixpackDocumentationJSONObject != null) {
				return _fixpackDocumentationJSONObject;
			}

			File tempDir = new File(
				JenkinsResultsParserUtil.getDistinctTimeStamp());

			try {
				tempDir.mkdirs();

				File hotfixFile = new File(tempDir, "hotfix.zip");

				JenkinsResultsParserUtil.toFile(
					getPortalHotfixReleaseURL(), hotfixFile);

				JenkinsResultsParserUtil.unzip(hotfixFile, tempDir);

				File fixpackDocumentationFile = new File(
					tempDir, "fixpack_documentation.json");

				if (!fixpackDocumentationFile.exists()) {
					return null;
				}

				_fixpackDocumentationJSONObject = new JSONObject(
					JenkinsResultsParserUtil.read(fixpackDocumentationFile));

				return _fixpackDocumentationJSONObject;
			}
			catch (Exception exception) {
				return null;
			}
			finally {
				JenkinsResultsParserUtil.delete(tempDir);
			}
		}
	}

	private Set<String> _getJSONPackageNames() {
		JSONObject fixpackDocumentationJSONObject =
			_getFixpackDocumentationJSONObject();

		if (fixpackDocumentationJSONObject == null) {
			return null;
		}

		Set<String> packageNames = new HashSet<>();

		JSONObject filesJSONObject =
			fixpackDocumentationJSONObject.getJSONObject("files");

		JSONArray jarFilesJSONArray = filesJSONObject.getJSONArray("jar_files");

		for (int i = 0; i < jarFilesJSONArray.length(); i++) {
			JSONObject jarFileJSONObject = jarFilesJSONArray.getJSONObject(i);

			packageNames.add(jarFileJSONObject.optString("new_name"));
		}

		JSONArray lpkgFilesJSONArray = filesJSONObject.getJSONArray(
			"lpkg_files");

		for (int i = 0; i < lpkgFilesJSONArray.length(); i++) {
			JSONObject lpkgFilesJSONObject = lpkgFilesJSONArray.getJSONObject(
				i);

			JSONArray modifiedJarsJSONArray = lpkgFilesJSONObject.getJSONArray(
				"modified_jars");

			for (int j = 0; j < modifiedJarsJSONArray.length(); j++) {
				JSONObject modifiedJarJSONObject =
					modifiedJarsJSONArray.getJSONObject(j);

				packageNames.add(modifiedJarJSONObject.optString("new_name"));
			}
		}

		return packageNames;
	}

	private URL _getURL(String urlString) {
		if (!JenkinsResultsParserUtil.isURL(urlString)) {
			return null;
		}

		Matcher matcher = _mirrorsURLPattern.matcher(urlString);

		if (matcher.matches()) {
			urlString = "https://" + matcher.group("urlPath");
		}

		try {
			return new URL(urlString);
		}
		catch (MalformedURLException malformedURLException) {
			return null;
		}
	}

	private Set<String> _getXMLPackageNames() {
		Element fixpackDocumentationElement = _getFixpackDocumentationElement();

		if (fixpackDocumentationElement == null) {
			return null;
		}

		Set<String> packageNames = new HashSet<>();

		Element checksumsElement = fixpackDocumentationElement.element(
			"checksums");

		List<Element> fileChecksumElements = checksumsElement.elements(
			"file-checksum");

		for (Element fileChecksumElement : fileChecksumElements) {
			packageNames.add(fileChecksumElement.attributeValue("file"));
		}

		return packageNames;
	}

	private static final Pattern _hotfixURLPattern = Pattern.compile(
		"https?://.+/(?<hotfixName>liferay-(hotfix|security-de|security-dxp|" +
			"dxp-\\d{4}.q\\d+.\\d+-hotfix)-" +
				"(?<hotfixVersion>\\d+)(-\\d{6}-\\d)?(-)?(\\d{4})?)");
	private static final Pattern _mirrorsURLPattern = Pattern.compile(
		"https?://(mirrors(.lax.liferay.com)?/)?(?<urlPath>.+)");
	private static final Pattern _packageNamePattern = Pattern.compile(
		"(?<packageName>[\\.\\w]+|[\\-\\w]+)(-\\d.*)?\\.jar");

	private Element _fixpackDocumentationElement;
	private JSONObject _fixpackDocumentationJSONObject;
	private final PortalFixpackRelease _portalFixpackRelease;
	private final URL _portalHotfixReleaseURL;
	private final PortalRelease _portalRelease;

}