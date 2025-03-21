/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.plugin;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Plugin;
import com.liferay.portal.kernel.plugin.License;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.plugin.Screenshot;
import com.liferay.portal.kernel.plugin.Version;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 */
public class PluginPackageUtil {

	public static void endPluginPackageInstallation(String preliminaryContext) {
		_installedPluginPackages.unregisterPluginPackageInstallation(
			preliminaryContext);
	}

	public static PluginPackage getInstalledPluginPackage(String context) {
		return _installedPluginPackages.getPluginPackage(context);
	}

	public static List<PluginPackage> getInstalledPluginPackages() {
		return _installedPluginPackages.getSortedPluginPackages();
	}

	public static boolean isCurrentVersionSupported(List<String> versions) {
		Version currentVersion = Version.getInstance(ReleaseInfo.getVersion());

		for (String version : versions) {
			Version supportedVersion = Version.getInstance(version);

			if (supportedVersion.includes(currentVersion)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isInstalled(String context) {
		PluginPackage pluginPackage = _installedPluginPackages.getPluginPackage(
			context);

		if (pluginPackage != null) {
			return true;
		}

		return false;
	}

	public static PluginPackage readPluginPackageProperties(
		String displayName, Properties properties) {

		int pos = displayName.indexOf("-portlet");

		String pluginType = Plugin.TYPE_PORTLET;

		if (pos == -1) {
			pos = displayName.indexOf("-client-extension");

			pluginType = _TYPE_CLIENT_EXTENSION;
		}

		if (pos == -1) {
			pos = displayName.indexOf("-ext");

			pluginType = _TYPE_EXT;
		}

		if (pos == -1) {
			pos = displayName.indexOf("-hook");

			pluginType = Plugin.TYPE_HOOK;
		}

		if (pos == -1) {
			pos = displayName.indexOf("-layouttpl");

			pluginType = Plugin.TYPE_LAYOUT_TEMPLATE;
		}

		if (pos == -1) {
			pos = displayName.indexOf("-theme");

			pluginType = Plugin.TYPE_THEME;
		}

		if (pos == -1) {
			pos = displayName.indexOf("-web");

			pluginType = Plugin.TYPE_WEB;
		}

		if (pos == -1) {
			return null;
		}

		String displayPrefix = displayName.substring(0, pos);

		String moduleGroupId = GetterUtil.getString(
			properties.getProperty("module-group-id"));
		String moduleArtifactId = displayPrefix + "-" + pluginType;

		String moduleVersion = GetterUtil.getString(
			properties.getProperty("module-version"));

		if (Validator.isNull(moduleVersion)) {
			int moduleVersionPos = pos + pluginType.length() + 2;

			if (displayName.length() > moduleVersionPos) {
				moduleVersion = displayName.substring(moduleVersionPos);
			}
			else {
				String moduleIncrementalVersion = GetterUtil.getString(
					properties.getProperty("module-incremental-version"));

				if (Validator.isNull(moduleIncrementalVersion)) {
					moduleVersion = ReleaseInfo.getVersion();
				}
				else {
					moduleVersion =
						ReleaseInfo.getVersion() + "." +
							moduleIncrementalVersion;
				}
			}
		}

		String moduleId = StringBundler.concat(
			moduleGroupId, "/", moduleArtifactId, "/", moduleVersion, "/war");

		String pluginName = GetterUtil.getString(
			properties.getProperty("name"));

		String deploymentContext = GetterUtil.getString(
			properties.getProperty("recommended-deployment-context"),
			moduleArtifactId);

		List<String> types = new ArrayList<>();

		types.add(pluginType);

		List<License> licenses = new ArrayList<>();

		String[] licensesArray = StringUtil.split(
			properties.getProperty("licenses"));

		for (String curLicenses : licensesArray) {
			License license = new License();

			license.setName(curLicenses.trim());
			license.setOsiApproved(true);

			licenses.add(license);
		}

		List<String> liferayVersions = new ArrayList<>();

		String[] liferayVersionsArray = StringUtil.split(
			properties.getProperty("liferay-versions"));

		for (String liferayVersion : liferayVersionsArray) {
			liferayVersions.add(liferayVersion.trim());
		}

		if (liferayVersions.isEmpty()) {
			liferayVersions.add(ReleaseInfo.getVersion() + "+");
		}

		List<String> tags = new ArrayList<>();

		String[] tagsArray = StringUtil.split(properties.getProperty("tags"));

		for (String tag : tagsArray) {
			tags.add(tag.trim());
		}

		String shortDescription = GetterUtil.getString(
			properties.getProperty("short-description"));
		String longDescription = GetterUtil.getString(
			properties.getProperty("long-description"));
		String changeLog = GetterUtil.getString(
			properties.getProperty("change-log"));
		String pageURL = GetterUtil.getString(
			properties.getProperty("page-url"));
		List<String> requiredDeploymentContexts = ListUtil.fromArray(
			StringUtil.split(
				properties.getProperty("required-deployment-contexts")));

		PluginPackage pluginPackage = new PluginPackageImpl(moduleId);

		pluginPackage.setName(pluginName);
		pluginPackage.setRecommendedDeploymentContext(deploymentContext);
		//pluginPackage.setModifiedDate(null);
		pluginPackage.setAuthor(
			GetterUtil.getString(properties.getProperty("author")));
		pluginPackage.setTypes(types);
		pluginPackage.setLicenses(licenses);
		pluginPackage.setLiferayVersions(liferayVersions);
		pluginPackage.setTags(tags);
		pluginPackage.setShortDescription(shortDescription);
		pluginPackage.setLongDescription(longDescription);
		pluginPackage.setChangeLog(changeLog);
		//pluginPackage.setScreenshots(null);
		pluginPackage.setPageURL(pageURL);
		//pluginPackage.setDeploymentSettings(null);
		pluginPackage.setRequiredDeploymentContexts(requiredDeploymentContexts);

		return pluginPackage;
	}

	public static PluginPackage readPluginPackageServletContext(
			ServletContext servletContext)
		throws DocumentException, IOException {

		PluginPackage pluginPackage =
			(PluginPackage)servletContext.getAttribute(
				PluginPackage.class.getName());

		if (pluginPackage == null) {
			pluginPackage = _readPluginPackageServletContext(servletContext);

			servletContext.setAttribute(
				PluginPackage.class.getName(), pluginPackage);
		}

		return pluginPackage;
	}

	public static PluginPackage readPluginPackageXml(String xml)
		throws DocumentException {

		Document document = SAXReaderUtil.read(xml);

		return _readPluginPackageXml(document.getRootElement());
	}

	public static void registerInstalledPluginPackage(
			PluginPackage pluginPackage)
		throws PortalException {

		_installedPluginPackages.addPluginPackage(pluginPackage);
	}

	public static void unregisterInstalledPluginPackage(
			PluginPackage pluginPackage)
		throws PortalException {

		_installedPluginPackages.removePluginPackage(pluginPackage);
	}

	public static void updateInstallingPluginPackage(
		String preliminaryContext, PluginPackage pluginPackage) {

		_installedPluginPackages.unregisterPluginPackageInstallation(
			preliminaryContext);
		_installedPluginPackages.registerPluginPackageInstallation(
			pluginPackage);
	}

	private static Date _readDate(String text) {
		if (Validator.isNotNull(text)) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				Time.RFC822_FORMAT, LocaleUtil.US);

			try {
				return dateFormat.parse(text);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to parse date " + text, exception);
				}
			}
		}

		return new Date();
	}

	private static String _readHtml(String text) {
		return GetterUtil.getString(text);
	}

	private static List<License> _readLicenseList(
		Element parentElement, String name) {

		List<License> licenses = new ArrayList<>();

		for (Element licenseElement : parentElement.elements(name)) {
			License license = new License();

			license.setName(licenseElement.getText());

			Attribute osiApproved = licenseElement.attribute("osi-approved");

			if (osiApproved != null) {
				license.setOsiApproved(
					GetterUtil.getBoolean(osiApproved.getText()));
			}

			Attribute url = licenseElement.attribute("url");

			if (url != null) {
				license.setUrl(url.getText());
			}

			licenses.add(license);
		}

		return licenses;
	}

	private static List<String> _readList(Element parentElement, String name) {
		List<String> list = new ArrayList<>();

		if (parentElement == null) {
			return list;
		}

		for (Element element : parentElement.elements(name)) {
			String text = StringUtil.toLowerCase(
				StringUtil.trim(element.getText()));

			list.add(text);
		}

		return list;
	}

	private static PluginPackage _readPluginPackageServletContext(
			ServletContext servletContext)
		throws DocumentException, IOException {

		String servletContextName = servletContext.getServletContextName();

		if (_log.isDebugEnabled()) {
			if (servletContextName == null) {
				_log.debug("Reading plugin package for the root context");
			}
			else {
				_log.debug("Reading plugin package for " + servletContextName);
			}
		}

		PluginPackage pluginPackage = null;

		String xml = StreamUtil.toString(
			servletContext.getResourceAsStream(
				"/WEB-INF/liferay-plugin-package.xml"));

		if (xml != null) {
			pluginPackage = readPluginPackageXml(xml);
		}
		else {
			String propertiesString = StreamUtil.toString(
				servletContext.getResourceAsStream(
					"/WEB-INF/liferay-plugin-package.properties"));

			if (propertiesString != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Reading plugin package from " +
							"liferay-plugin-package.properties");
				}

				Properties properties = PropertiesUtil.load(propertiesString);

				String displayName = servletContextName;

				if (displayName.startsWith(StringPool.SLASH)) {
					displayName = displayName.substring(1);
				}

				pluginPackage = readPluginPackageProperties(
					displayName, properties);
			}

			if (pluginPackage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Reading plugin package from MANIFEST.MF");
				}

				pluginPackage = _readPluginPackageServletManifest(
					servletContext);
			}
		}

		pluginPackage.setContext(servletContextName);

		return pluginPackage;
	}

	private static PluginPackage _readPluginPackageServletManifest(
			ServletContext servletContext)
		throws IOException {

		Attributes attributes = null;

		String servletContextName = servletContext.getServletContextName();

		InputStream inputStream = servletContext.getResourceAsStream(
			"/META-INF/MANIFEST.MF");

		if (inputStream != null) {
			Manifest manifest = new Manifest(inputStream);

			attributes = manifest.getMainAttributes();
		}
		else {
			attributes = new Attributes();
		}

		String artifactGroupId = attributes.getValue(
			"Implementation-Vendor-Id");

		if (Validator.isNull(artifactGroupId)) {
			artifactGroupId = attributes.getValue("Implementation-Vendor");
		}

		if (Validator.isNull(artifactGroupId)) {
			artifactGroupId = GetterUtil.getString(
				attributes.getValue("Bundle-Vendor"), servletContextName);
		}

		String artifactId = attributes.getValue("Implementation-Title");

		if (Validator.isNull(artifactId)) {
			artifactId = GetterUtil.getString(
				attributes.getValue("Bundle-Name"), servletContextName);
		}

		String version = attributes.getValue("Implementation-Version");

		if (Validator.isNull(version)) {
			version = GetterUtil.getString(
				attributes.getValue("Bundle-Version"), Version.UNKNOWN);
		}

		if (version.equals(Version.UNKNOWN) && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Plugin package on context ", servletContextName,
					" cannot be tracked because this WAR does not contain a ",
					"liferay-plugin-package.xml file"));
		}

		PluginPackage pluginPackage = new PluginPackageImpl(
			StringBundler.concat(
				artifactGroupId, StringPool.SLASH, artifactId, StringPool.SLASH,
				version, "/war"));

		pluginPackage.setName(artifactId);

		String shortDescription = attributes.getValue("Bundle-Description");

		if (Validator.isNotNull(shortDescription)) {
			pluginPackage.setShortDescription(shortDescription);
		}

		String pageURL = attributes.getValue("Bundle-DocURL");

		if (Validator.isNotNull(pageURL)) {
			pluginPackage.setPageURL(pageURL);
		}

		return pluginPackage;
	}

	private static PluginPackage _readPluginPackageXml(
		Element pluginPackageElement) {

		String name = pluginPackageElement.elementText("name");

		if (_log.isDebugEnabled()) {
			_log.debug("Reading pluginPackage definition " + name);
		}

		PluginPackage pluginPackage = new PluginPackageImpl(
			GetterUtil.getString(
				pluginPackageElement.elementText("module-id")));

		List<String> liferayVersions = _readList(
			pluginPackageElement.element("liferay-versions"),
			"liferay-version");

		List<String> types = _readList(
			pluginPackageElement.element("types"), "type");

		if (types.contains("layout-template")) {
			types.remove("layout-template");

			types.add(Plugin.TYPE_LAYOUT_TEMPLATE);
		}

		pluginPackage.setName(_readText(name));
		pluginPackage.setRecommendedDeploymentContext(
			_readText(
				pluginPackageElement.elementText(
					"recommended-deployment-context")));
		pluginPackage.setRequiredDeploymentContexts(
			_readList(
				pluginPackageElement.element("required-deployment-contexts"),
				"required-deployment-context"));
		pluginPackage.setModifiedDate(
			_readDate(pluginPackageElement.elementText("modified-date")));
		pluginPackage.setAuthor(
			_readText(pluginPackageElement.elementText("author")));
		pluginPackage.setTypes(types);
		pluginPackage.setLicenses(
			_readLicenseList(
				pluginPackageElement.element("licenses"), "license"));
		pluginPackage.setLiferayVersions(liferayVersions);
		pluginPackage.setTags(
			_readList(pluginPackageElement.element("tags"), "tag"));
		pluginPackage.setShortDescription(
			_readText(pluginPackageElement.elementText("short-description")));
		pluginPackage.setLongDescription(
			_readHtml(pluginPackageElement.elementText("long-description")));
		pluginPackage.setChangeLog(
			_readHtml(pluginPackageElement.elementText("change-log")));
		pluginPackage.setScreenshots(
			_readScreenshots(pluginPackageElement.element("screenshots")));
		pluginPackage.setPageURL(
			_readText(pluginPackageElement.elementText("page-url")));
		pluginPackage.setDeploymentSettings(
			_readProperties(
				pluginPackageElement.element("deployment-settings"),
				"setting"));

		return pluginPackage;
	}

	private static Properties _readProperties(
		Element parentElement, String name) {

		Properties properties = new Properties();

		if (parentElement == null) {
			return properties;
		}

		for (Element element : parentElement.elements(name)) {
			properties.setProperty(
				element.attributeValue("name"),
				element.attributeValue("value"));
		}

		return properties;
	}

	private static List<Screenshot> _readScreenshots(Element parentElement) {
		List<Screenshot> screenshots = new ArrayList<>();

		if (parentElement == null) {
			return screenshots;
		}

		for (Element screenshotElement : parentElement.elements("screenshot")) {
			Screenshot screenshot = new Screenshot();

			screenshot.setThumbnailURL(
				screenshotElement.elementText("thumbnail-url"));
			screenshot.setLargeImageURL(
				screenshotElement.elementText("large-image-url"));

			screenshots.add(screenshot);
		}

		return screenshots;
	}

	private static String _readText(String text) {
		return HtmlParserUtil.extractText(GetterUtil.getString(text));
	}

	private static final String _TYPE_CLIENT_EXTENSION = "client-extension";

	private static final String _TYPE_EXT = "ext";

	private static final Log _log = LogFactoryUtil.getLog(
		PluginPackageUtil.class);

	private static final LocalPluginPackageRepository _installedPluginPackages =
		new LocalPluginPackageRepository();

}