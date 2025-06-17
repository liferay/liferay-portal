/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalRelease {

	public static boolean isQuarterlyRelease(String portalVersion) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(portalVersion)) {
			return false;
		}

		Matcher matcher = _quarterlyReleaseVersionPattern.matcher(
			portalVersion);

		return matcher.matches();
	}

	public PortalRelease(URL bundlesBaseURL, String portalVersion) {
		if (bundlesBaseURL == null) {
			throw new RuntimeException("Bundles base URL is null");
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalVersion)) {
			throw new RuntimeException("Portal version is null or empty");
		}

		_portalVersion = portalVersion;

		String bundlesBaseURLString = bundlesBaseURL.toString();

		if (bundlesBaseURLString.endsWith("/")) {
			bundlesBaseURLString = bundlesBaseURLString.substring(
				0, bundlesBaseURLString.length() - 1);
		}

		_bundlesBaseURL = _getRemoteURL(bundlesBaseURLString);

		_initializeURLs();
	}

	public URL getBundlesBaseLocalURL() {
		return _getLocalURL(_bundlesBaseURL.toString());
	}

	public URL getBundlesBaseURL() {
		return _getRemoteURL(_bundlesBaseURL.toString());
	}

	public JSONObject getJSONObject() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"bundles_base_url", String.valueOf(_bundlesBaseURL)
		).put(
			"portal_version", _portalVersion
		);

		return jsonObject;
	}

	public URL getPluginsWarZipLocalURL() {
		return _getLocalURL(_pluginsWarZipURLString);
	}

	public URL getPluginsWarZipURL() {
		return _getRemoteURL(_pluginsWarZipURLString);
	}

	public URL getPortalBundleGlassFishLocalURL() {
		return _getLocalURL(_portalBundleGlassFishURLString);
	}

	public URL getPortalBundleGlassFishURL() {
		return _getRemoteURL(_portalBundleGlassFishURLString);
	}

	public URL getPortalBundleJBossLocalURL() {
		return _getLocalURL(_portalBundleJBossURLString);
	}

	public URL getPortalBundleJBossURL() {
		return _getRemoteURL(_portalBundleJBossURLString);
	}

	public URL getPortalBundleTomcatLocalURL() {
		return _getLocalURL(_portalBundleTomcatURLString);
	}

	public URL getPortalBundleTomcatURL() {
		return _getRemoteURL(_portalBundleTomcatURLString);
	}

	public URL getPortalBundleWildFlyLocalURL() {
		return _getLocalURL(_portalBundleWildFlyURLString);
	}

	public URL getPortalBundleWildFlyURL() {
		return _getRemoteURL(_portalBundleWildFlyURLString);
	}

	public URL getPortalDependenciesZipLocalURL() {
		return _getLocalURL(_portalDependenciesZipURLString);
	}

	public URL getPortalDependenciesZipURL() {
		return _getRemoteURL(_portalDependenciesZipURLString);
	}

	public URL getPortalOSGiZipLocalURL() {
		return _getLocalURL(_portalOSGiZipURLString);
	}

	public URL getPortalOSGiZipURL() {
		return _getRemoteURL(_portalOSGiZipURLString);
	}

	public URL getPortalSQLZipLocalURL() {
		return _getLocalURL(_portalSQLZipURLString);
	}

	public URL getPortalSQLZipURL() {
		return _getRemoteURL(_portalSQLZipURLString);
	}

	public URL getPortalToolsZipLocalURL() {
		return _getLocalURL(_portalToolsZipURLString);
	}

	public URL getPortalToolsZipURL() {
		return _getRemoteURL(_portalToolsZipURLString);
	}

	public String getPortalUpstreamBranchName() {
		try {
			String portalUpstreamBranchName =
				JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getBuildProperties(),
					"portal.branch.name", getPortalVersionPropertyOption());

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					portalUpstreamBranchName)) {

				return portalUpstreamBranchName;
			}
		}
		catch (IOException ioException) {
		}

		Matcher matcher = _portalVersionPattern.matcher(_portalVersion);

		if (!matcher.find()) {
			return getQuarterlyReleaseBranchName();
		}

		StringBuilder sb = new StringBuilder();

		String majorVersion = matcher.group("majorVersion");

		if (majorVersion.equals("6")) {
			sb.append("ee-");
		}

		sb.append(majorVersion);
		sb.append(".");
		sb.append(matcher.group("minorVersion"));
		sb.append(".x");

		return sb.toString();
	}

	public String getPortalVersion() {
		return _portalVersion;
	}

	public String getPortalVersionPropertyOption() {
		Matcher matcher = _portalVersionPattern.matcher(_portalVersion);

		if (!matcher.find()) {
			return _portalVersion;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(matcher.group("majorVersion"));
		sb.append(".");
		sb.append(matcher.group("minorVersion"));
		sb.append(".");
		sb.append(matcher.group("fixVersion"));

		String dxpVersion = matcher.group("dxpVersion");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(dxpVersion)) {
			sb.append("-dxp-");
			sb.append(dxpVersion);
		}

		String updateVersion = matcher.group("updateVersion");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(updateVersion)) {
			sb.append(".");

			String updatePrefix = matcher.group("updatePrefix");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(updatePrefix)) {
				sb.append(updatePrefix);
			}

			sb.append(updateVersion);
		}

		return sb.toString();
	}

	public URL getPortalWarLocalURL() {
		return _getLocalURL(_portalWarURLString);
	}

	public URL getPortalWarURL() {
		return _getRemoteURL(_portalWarURLString);
	}

	public String getQuarterlyReleaseBranchName() {
		Matcher matcher = _quarterlyReleaseVersionPattern.matcher(
			_portalVersion);

		if (matcher.find()) {
			return "release-" + matcher.group("branchVersion");
		}

		return "master";
	}

	public void setPluginsWarZipURL(URL pluginsWarZipURL) {
		if (pluginsWarZipURL == null) {
			_pluginsWarZipURLString = null;

			return;
		}

		_pluginsWarZipURLString = JenkinsResultsParserUtil.getLocalURL(
			pluginsWarZipURL.toString());
	}

	public void setPortalBundleGlassFishURL(URL portalBundleGlassFishURL) {
		if (portalBundleGlassFishURL == null) {
			_portalBundleGlassFishURLString = null;

			return;
		}

		_portalBundleGlassFishURLString = JenkinsResultsParserUtil.getLocalURL(
			portalBundleGlassFishURL.toString());
	}

	public void setPortalBundleJBossURL(URL portalBundleJBossURL) {
		if (portalBundleJBossURL == null) {
			_portalBundleJBossURLString = null;

			return;
		}

		_portalBundleJBossURLString = JenkinsResultsParserUtil.getLocalURL(
			portalBundleJBossURL.toString());
	}

	public void setPortalBundleTomcatURL(URL portalBundleTomcatURL) {
		if (portalBundleTomcatURL == null) {
			_portalBundleTomcatURLString = null;

			return;
		}

		_portalBundleTomcatURLString = JenkinsResultsParserUtil.getLocalURL(
			portalBundleTomcatURL.toString());
	}

	public void setPortalBundleWildFlyURL(URL portalBundleWildFlyURL) {
		if (portalBundleWildFlyURL == null) {
			_portalBundleWildFlyURLString = null;

			return;
		}

		_portalBundleWildFlyURLString = JenkinsResultsParserUtil.getLocalURL(
			portalBundleWildFlyURL.toString());
	}

	public void setPortalDependenciesZipURL(URL portalDependenciesZipURL) {
		if (portalDependenciesZipURL == null) {
			_portalDependenciesZipURLString = null;

			return;
		}

		_portalDependenciesZipURLString = JenkinsResultsParserUtil.getLocalURL(
			portalDependenciesZipURL.toString());
	}

	public void setPortalOSGiZipURL(URL portalOSGiZipURL) {
		if (portalOSGiZipURL == null) {
			_portalOSGiZipURLString = null;

			return;
		}

		_portalOSGiZipURLString = JenkinsResultsParserUtil.getLocalURL(
			portalOSGiZipURL.toString());
	}

	public void setPortalSQLZipURL(URL portalSQLZipURL) {
		if (portalSQLZipURL == null) {
			_portalSQLZipURLString = null;

			return;
		}

		_portalSQLZipURLString = JenkinsResultsParserUtil.getLocalURL(
			portalSQLZipURL.toString());
	}

	public void setPortalToolsZipURL(URL portalToolsZipURL) {
		if (portalToolsZipURL == null) {
			_portalToolsZipURLString = null;

			return;
		}

		_portalToolsZipURLString = JenkinsResultsParserUtil.getLocalURL(
			portalToolsZipURL.toString());
	}

	public void setPortalWarURL(URL portalWarURL) {
		if (portalWarURL == null) {
			_portalWarURLString = null;

			return;
		}

		_portalWarURLString = JenkinsResultsParserUtil.getLocalURL(
			portalWarURL.toString());
	}

	protected static URL getBundlesBaseURL(String portalVersion) {
		_initalizePortalVersionsMap();

		synchronized (_portalVersionsMap) {
			for (Map.Entry<String, URL> entry : _portalVersionsMap.entrySet()) {
				String key = entry.getKey();

				if (key.equals(portalVersion) ||
					key.startsWith(portalVersion + "-")) {

					return entry.getValue();
				}
			}
		}

		return null;
	}

	protected static URL getBundlesBaseURL(URL bundleURL) {
		Matcher bundleURLMatcher = _bundleURLPattern.matcher(
			bundleURL.toString());

		if (!bundleURLMatcher.find()) {
			throw new RuntimeException("Invalid URL " + bundleURL);
		}

		try {
			return new URL(bundleURLMatcher.group("bundlesBaseURL"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	protected static String getPortalVersion(String portalVersion) {
		_initalizePortalVersionsMap();

		synchronized (_portalVersionsMap) {
			if (_portalVersionsMap.containsKey(portalVersion)) {
				return portalVersion;
			}

			for (Map.Entry<String, URL> entry : _portalVersionsMap.entrySet()) {
				String key = entry.getKey();

				if (key.startsWith(portalVersion + "-")) {
					return key;
				}
			}

			return null;
		}
	}

	protected static String getPortalVersion(URL bundleURL) {
		Matcher bundleURLMatcher = _bundleURLPattern.matcher(
			bundleURL.toString());

		if (!bundleURLMatcher.find()) {
			throw new RuntimeException("Invalid URL " + bundleURL);
		}

		String bundleFileName = bundleURLMatcher.group("bundleFileName");

		Matcher bundleFileNameMatcher = _bundleFileNamePattern.find(
			bundleFileName);

		if (bundleFileNameMatcher != null) {
			return bundleFileNameMatcher.group("portalVersion");
		}

		String bundlesBaseURLString = bundleURLMatcher.group("bundlesBaseURL");

		Matcher bundlesBaseURLMatcher = _bundlesBaseURLPattern.find(
			bundlesBaseURLString);

		if (bundlesBaseURLMatcher == null) {
			throw new RuntimeException(
				"Invalid bundle file name " + bundleFileName);
		}

		return bundlesBaseURLMatcher.group("portalVersion");
	}

	protected PortalRelease(JSONObject jsonObject) {
		try {
			_bundlesBaseURL = new URL(jsonObject.getString("bundles_base_url"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}

		_portalVersion = jsonObject.getString("portal_version");

		_initializeURLs();
	}

	protected PortalRelease(String portalVersion) {
		_portalVersion = getPortalVersion(portalVersion);

		_bundlesBaseURL = _getRemoteURL(
			String.valueOf(getBundlesBaseURL(portalVersion)));

		_initializeURLs();
	}

	protected PortalRelease(URL bundleURL) {
		_bundlesBaseURL = _getRemoteURL(
			String.valueOf(getBundlesBaseURL(bundleURL)));
		_portalVersion = getPortalVersion(bundleURL);

		_initializeURLs();
	}

	private static void _initalizePortalVersionsMap() {
		synchronized (_portalVersionsMap) {
			if (!_portalVersionsMap.isEmpty()) {
				return;
			}

			try {
				Properties buildProperties =
					JenkinsResultsParserUtil.getBuildProperties();

				for (String propertyName :
						buildProperties.stringPropertyNames()) {

					Matcher tomcatBundlePropertyMatcher =
						_tomcatBundlePropertyPattern.matcher(propertyName);

					if (!tomcatBundlePropertyMatcher.find()) {
						continue;
					}

					String propertyValue = JenkinsResultsParserUtil.getProperty(
						buildProperties, propertyName);

					Matcher tomcatBundleURLMatcher =
						_tomcatBundleURLPattern.matcher(propertyValue);

					if (!tomcatBundleURLMatcher.matches()) {
						continue;
					}

					URL bundlesBaseURL = new URL(
						JenkinsResultsParserUtil.combine(
							"https://",
							tomcatBundleURLMatcher.group("hostname"), "/",
							tomcatBundleURLMatcher.group("urlPath")));

					String portalVersion = tomcatBundlePropertyMatcher.group(
						"portalVersion");

					_portalVersionsMap.put(portalVersion, bundlesBaseURL);
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	private URL _getLocalURL(String urlString) {
		if (urlString == null) {
			return null;
		}

		try {
			return new URL(
				JenkinsResultsParserUtil.getLocalURL(
					urlString.replaceAll("([^:])//", "$1/")));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private URL _getRemoteURL(String urlString) {
		if (urlString == null) {
			return null;
		}

		try {
			return new URL(
				JenkinsResultsParserUtil.getRemoteURL(
					urlString.replaceAll("([^:])//", "$1/")));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private String _getURLString(
		String bundlesBaseURLContent, Pattern pattern) {

		Matcher matcher = pattern.matcher(bundlesBaseURLContent);

		if (!matcher.find()) {
			return null;
		}

		return getBundlesBaseURL() + "/" + matcher.group("fileName");
	}

	private String _getURLStringFromBuildProperties(String basePropertyName) {
		try {
			String urlString = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(), basePropertyName,
				getPortalVersionPropertyOption());

			String portalBuildProfile = "portal";

			Matcher matcher = _portalVersionPattern.matcher(_portalVersion);

			if (matcher.find()) {
				String fixVersion = matcher.group("fixVersion");

				if (fixVersion.length() >= 2) {
					portalBuildProfile = "dxp";
				}
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(urlString)) {
				urlString = JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getBuildProperties(),
					basePropertyName, getPortalUpstreamBranchName(),
					portalBuildProfile);
			}

			if (!JenkinsResultsParserUtil.isNullOrEmpty(urlString)) {
				return urlString;
			}

			return null;
		}
		catch (IOException ioException) {
			return null;
		}
	}

	private void _initializeURLs() {
		String pluginsWarZipURLString = _getURLStringFromBuildProperties(
			"plugins.war.zip.url");

		if (JenkinsResultsParserUtil.isURL(pluginsWarZipURLString)) {
			_pluginsWarZipURLString = pluginsWarZipURLString;
		}

		String portalBundleGlassFishURLString =
			_getURLStringFromBuildProperties("portal.bundle.glassfish");

		if (JenkinsResultsParserUtil.isURL(portalBundleGlassFishURLString)) {
			_portalBundleGlassFishURLString = portalBundleGlassFishURLString;
		}

		String portalBundleJBossURLString = _getURLStringFromBuildProperties(
			"portal.bundle.jboss");

		if (JenkinsResultsParserUtil.isURL(portalBundleJBossURLString)) {
			_portalBundleJBossURLString = portalBundleJBossURLString;
		}

		String portalBundleTomcatURLString = _getURLStringFromBuildProperties(
			"portal.bundle.tomcat");

		if (JenkinsResultsParserUtil.isURL(portalBundleTomcatURLString)) {
			_portalBundleTomcatURLString = portalBundleTomcatURLString;
		}

		String portalBundleWildFlyURLString = _getURLStringFromBuildProperties(
			"portal.bundle.wildfly");

		if (JenkinsResultsParserUtil.isURL(portalBundleWildFlyURLString)) {
			_portalBundleWildFlyURLString = portalBundleWildFlyURLString;
		}

		String portalDependenciesZipURLString =
			_getURLStringFromBuildProperties("portal.dependencies.zip.url");

		if (JenkinsResultsParserUtil.isURL(portalDependenciesZipURLString)) {
			_portalDependenciesZipURLString = portalDependenciesZipURLString;
		}

		String portalOSGiZipURLString = _getURLStringFromBuildProperties(
			"portal.osgi.zip.url");

		if (JenkinsResultsParserUtil.isURL(portalOSGiZipURLString)) {
			_portalOSGiZipURLString = portalOSGiZipURLString;
		}

		String portalSQLZipURLString = _getURLStringFromBuildProperties(
			"portal.sql.zip.url");

		if (JenkinsResultsParserUtil.isURL(portalSQLZipURLString)) {
			_portalSQLZipURLString = portalSQLZipURLString;
		}

		String portalToolsZipURLString = _getURLStringFromBuildProperties(
			"portal.tools.zip.url");

		if (JenkinsResultsParserUtil.isURL(portalToolsZipURLString)) {
			_portalToolsZipURLString = portalToolsZipURLString;
		}

		String portalWarURLString = _getURLStringFromBuildProperties(
			"portal.war.url");

		if (JenkinsResultsParserUtil.isURL(portalWarURLString)) {
			_portalWarURLString = portalWarURLString;
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				_portalBundleTomcatURLString)) {

			return;
		}

		String bundlesBaseURLContent = null;

		try {
			bundlesBaseURLContent = JenkinsResultsParserUtil.toString(
				getBundlesBaseURL() + "/", false, 0, 5, 0);
		}
		catch (IOException ioException) {
			return;
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(bundlesBaseURLContent)) {
			return;
		}

		_portalBundleGlassFishURLString = _getURLString(
			bundlesBaseURLContent, _portalBundleGlassFishFileNamePattern);
		_portalBundleJBossURLString = _getURLString(
			bundlesBaseURLContent, _portalBundleJBossFileNamePattern);
		_portalBundleTomcatURLString = _getURLString(
			bundlesBaseURLContent, _portalBundleTomcatFileNamePattern);
		_portalBundleWildFlyURLString = _getURLString(
			bundlesBaseURLContent, _portalBundleWildFlyFileNamePattern);
		_portalDependenciesZipURLString = _getURLString(
			bundlesBaseURLContent, _portalDependenciesZipFileNamePattern);
		_portalOSGiZipURLString = _getURLString(
			bundlesBaseURLContent, _portalOSGiZipFileNamePattern);
		_portalSQLZipURLString = _getURLString(
			bundlesBaseURLContent, _portalSQLZipFileNamePattern);
		_portalToolsZipURLString = _getURLString(
			bundlesBaseURLContent, _portalToolsZipFileNamePattern);
		_portalWarURLString = _getURLString(
			bundlesBaseURLContent, _portalWarFileNamePattern);
	}

	private static final String _PORTAL_VERSION_REGEX =
		"(?<portalVersion>\\d\\.([u\\d\\.]+)(-ee)?(-dxp-\\d+)?" +
			"(\\-(ep|ga|rc|sp)\\d+)?)";

	private static final String _QUARTERLY_RELEASE_VERSION_REGEX =
		"(?<portalVersion>(?<branchVersion>\\d{4}.[Qq]\\d+).\\d+)";

	private static final MultiPattern _bundleFileNamePattern = new MultiPattern(
		".+\\-" + _PORTAL_VERSION_REGEX + ".*\\.(7z|tar.gz|zip)",
		".+\\-" + _QUARTERLY_RELEASE_VERSION_REGEX + ".*\\.(7z|tar.gz|zip)");
	private static final MultiPattern _bundlesBaseURLPattern = new MultiPattern(
		"https?://.+/" + _PORTAL_VERSION_REGEX,
		"https?://.+/" + _QUARTERLY_RELEASE_VERSION_REGEX);
	private static final Pattern _bundleURLPattern = Pattern.compile(
		"(?<bundlesBaseURL>https?://.+)/(?<bundleFileName>[^\\/]+" +
			"\\.(7z|tar.gz|zip))");
	private static final Pattern _portalBundleGlassFishFileNamePattern =
		Pattern.compile(
			"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-glassfish-[^\\\"]+" +
				"\\.(7z|tar.gz|zip))\\\"");
	private static final Pattern _portalBundleJBossFileNamePattern =
		Pattern.compile(
			"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-jboss-[^\\\"]+" +
				"\\.(7z|tar.gz|zip))\\\"");
	private static final Pattern _portalBundleTomcatFileNamePattern =
		Pattern.compile(
			"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-tomcat-[^\\\"]+" +
				"\\.(7z|tar.gz|zip))\\\"");
	private static final Pattern _portalBundleWildFlyFileNamePattern =
		Pattern.compile(
			"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-wildfly-[^\\\"]+" +
				"\\.(7z|tar.gz|zip))\\\"");
	private static final Pattern _portalDependenciesZipFileNamePattern =
		Pattern.compile(
			"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-dependencies-" +
				"[^\\\"]+\\.zip)\\\"");
	private static final Pattern _portalOSGiZipFileNamePattern =
		Pattern.compile(
			"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-osgi-[^\\\"]+" +
				"\\.zip)\\\"");
	private static final Pattern _portalSQLZipFileNamePattern = Pattern.compile(
		"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-sql-[^\\\"]+" +
			"\\.zip)\\\"");
	private static final Pattern _portalToolsZipFileNamePattern =
		Pattern.compile(
			"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+-tools-[^\\\"]+" +
				"\\.zip)\\\"");
	private static final Pattern _portalVersionPattern = Pattern.compile(
		"(?<majorVersion>\\d)\\.(?<minorVersion>\\d)\\.(?<fixVersion>\\d+)" +
			"([-\\.](?<updatePrefix>u)?(?<updateVersion>\\d+))?" +
				"(-dxp-(?<dxpVersion>\\d+))?.*");
	private static final Map<String, URL> _portalVersionsMap = new HashMap<>();
	private static final Pattern _portalWarFileNamePattern = Pattern.compile(
		"href=\\\"[^\\\"]*(?<fileName>liferay-[^\\\"]+\\.war)\\\"");
	private static final Pattern _quarterlyReleaseVersionPattern =
		Pattern.compile(_QUARTERLY_RELEASE_VERSION_REGEX);
	private static final Pattern _tomcatBundlePropertyPattern = Pattern.compile(
		"portal.bundle.tomcat\\[(?<portalVersion>.+)\\]");
	private static final Pattern _tomcatBundleURLPattern = Pattern.compile(
		"https?://(mirrors.lax.liferay.com/)?" +
			"(?<hostname>releases.liferay.com)/(?<urlPath>(dxp|portal)/" +
				"(?<portalVersion>[^/]+))/[^/]+");

	private final URL _bundlesBaseURL;
	private String _pluginsWarZipURLString;
	private String _portalBundleGlassFishURLString;
	private String _portalBundleJBossURLString;
	private String _portalBundleTomcatURLString;
	private String _portalBundleWildFlyURLString;
	private String _portalDependenciesZipURLString;
	private String _portalOSGiZipURLString;
	private String _portalSQLZipURLString;
	private String _portalToolsZipURLString;
	private final String _portalVersion;
	private String _portalWarURLString;

}