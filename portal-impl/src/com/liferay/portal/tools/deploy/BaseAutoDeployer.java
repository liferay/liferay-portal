/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.deploy;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.deploy.DeployUtil;
import com.liferay.portal.kernel.deploy.auto.AutoDeployException;
import com.liferay.portal.kernel.deploy.auto.AutoDeployer;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.plugin.License;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.servlet.PluginContextListener;
import com.liferay.portal.kernel.servlet.PortalClassLoaderServlet;
import com.liferay.portal.kernel.servlet.PortalDelegateServlet;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.SecurePluginContextListener;
import com.liferay.portal.kernel.servlet.SecureServlet;
import com.liferay.portal.kernel.servlet.SerializableSessionAttributeListener;
import com.liferay.portal.kernel.servlet.filters.invoker.InvokerFilter;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.tools.WebXMLBuilder;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 */
public class BaseAutoDeployer implements AutoDeployer {

	public static final String DEPLOY_TO_PREFIX = "DEPLOY_TO__";

	public BaseAutoDeployer(String pluginType) {
		_pluginType = pluginType;
	}

	@Override
	public int autoDeploy(AutoDeploymentContext autoDeploymentContext)
		throws AutoDeployException {

		try {
			File srcFile = autoDeploymentContext.getFile();
			PluginPackage pluginPackage =
				autoDeploymentContext.getPluginPackage();

			if (pluginPackage == null) {
				pluginPackage = _readPluginPackage(srcFile);

				autoDeploymentContext.setPluginPackage(pluginPackage);
			}

			if (_log.isInfoEnabled()) {
				_log.info("Deploying " + srcFile.getName());
			}

			String specifiedContext = autoDeploymentContext.getContext();

			String displayName = specifiedContext;

			String preliminaryContext = specifiedContext;

			// The order of priority of the context is: 1.) the specified
			// context, 2.) if the file name starts with DEPLOY_TO_PREFIX, use
			// the file name after the prefix, or 3.) the recommended deployment
			// context as specified in liferay-plugin-package.properties, or 4.)
			// the file name.

			String srcFileName = srcFile.getName();

			if (Validator.isNull(specifiedContext) &&
				srcFileName.startsWith(DEPLOY_TO_PREFIX)) {

				displayName = srcFileName.substring(
					DEPLOY_TO_PREFIX.length(), srcFileName.length() - 4);

				preliminaryContext = displayName;
			}

			if (preliminaryContext == null) {
				preliminaryContext = _getDisplayName(srcFile);
			}

			if (pluginPackage != null) {
				if (!PluginPackageUtil.isCurrentVersionSupported(
						pluginPackage.getLiferayVersions())) {

					throw new AutoDeployException(
						srcFile.getName() +
							" does not support this version of Liferay");
				}

				if (displayName == null) {
					displayName =
						pluginPackage.getRecommendedDeploymentContext();
				}

				if (Validator.isNull(displayName)) {
					displayName = _getDisplayName(srcFile);
				}

				pluginPackage.setContext(displayName);

				PluginPackageUtil.updateInstallingPluginPackage(
					preliminaryContext, pluginPackage);
			}

			String deployDir = null;

			if (Validator.isNotNull(displayName)) {
				deployDir = displayName + ".war";
			}
			else {
				deployDir = srcFile.getName();
				displayName = _getDisplayName(srcFile);
			}

			String destDir = autoDeploymentContext.getDestDir();

			File deployDirFile = new File(destDir + "/" + deployDir);

			try {
				PluginPackage previousPluginPackage = _readPluginPackage(
					deployDirFile);

				if ((pluginPackage != null) &&
					(previousPluginPackage != null)) {

					String name = pluginPackage.getName();
					String previousVersion = previousPluginPackage.getVersion();
					String version = pluginPackage.getVersion();

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Updating ", name, " from version ",
								previousVersion, " to version ", version));
					}

					if (pluginPackage.isPreviousVersionThan(
							previousPluginPackage)) {

						if (_log.isInfoEnabled()) {
							_log.info(
								StringBundler.concat(
									"Not updating ", name, " because version ",
									previousVersion, " is newer than version ",
									version));
						}

						return AutoDeployer.CODE_SKIP_NEWER_VERSION;
					}
				}

				File mergeDirFile = new File(
					srcFile.getParent() + "/merge/" + srcFile.getName());

				if (srcFile.isDirectory()) {
					_deployDirectory(
						srcFile, mergeDirFile, deployDirFile, displayName,
						pluginPackage);
				}
				else {
					boolean deployed = _deployFile(
						srcFile, mergeDirFile, deployDirFile, displayName,
						pluginPackage);

					if (!deployed) {
						String context = preliminaryContext;

						if (pluginPackage != null) {
							context = pluginPackage.getContext();
						}

						PluginPackageUtil.endPluginPackageInstallation(context);
					}
				}

				return AutoDeployer.CODE_DEFAULT;
			}
			catch (Exception exception) {
				if (pluginPackage != null) {
					PluginPackageUtil.endPluginPackageInstallation(
						pluginPackage.getContext());
				}

				throw exception;
			}
		}
		catch (Exception exception) {
			throw new AutoDeployException(exception);
		}
	}

	@Override
	public void close() throws IOException {
		UnsafeConsumer.accept(
			tempDirPaths, DeployUtil::deletePath, IOException.class);
	}

	@Override
	public void copyDependencyXml(String fileName, String targetDir)
		throws Exception {

		copyDependencyXml(fileName, targetDir, null);
	}

	@Override
	public void copyDependencyXml(
			String fileName, String targetDir, Map<String, String> filterMap)
		throws Exception {

		copyDependencyXml(fileName, targetDir, filterMap, false);
	}

	@Override
	public void copyDependencyXml(
			String fileName, String targetDir, Map<String, String> filterMap,
			boolean overwrite)
		throws Exception {

		DeployUtil.copyDependencyXml(
			fileName, targetDir, fileName, filterMap, overwrite);
	}

	@Override
	public void copyXmls(
			File srcFile, String displayName, PluginPackage pluginPackage)
		throws Exception {

		copyDependencyXml("web.xml", srcFile + "/WEB-INF");
	}

	public void deployDirectory(
			File srcFile, String displayName, PluginPackage pluginPackage)
		throws Exception {

		_deployDirectory(srcFile, null, null, displayName, pluginPackage);
	}

	public String getExtraContent(
			double webXmlVersion, File srcFile, String displayName)
		throws Exception {

		if (displayName.startsWith(StringPool.FORWARD_SLASH)) {
			displayName = displayName.substring(1);
		}

		StringBundler sb = new StringBundler(69);

		sb.append("<display-name>");
		sb.append(displayName);
		sb.append("</display-name>");

		if (webXmlVersion < 2.4) {
			sb.append("<context-param>");
			sb.append("<param-name>liferay-invoker-enabled</param-name>");
			sb.append("<param-value>false</param-value>");
			sb.append("</context-param>");
		}

		if (PropsValues.SESSION_VERIFY_SERIALIZABLE_ATTRIBUTE) {
			sb.append("<listener>");
			sb.append("<listener-class>");
			sb.append(SerializableSessionAttributeListener.class.getName());
			sb.append("</listener-class>");
			sb.append("</listener>");
		}

		boolean hasTaglib = false;

		if (Validator.isNotNull(auiTaglibDTD) ||
			Validator.isNotNull(portletTaglibDTD) ||
			Validator.isNotNull(portletExtTaglibDTD) ||
			Validator.isNotNull(securityTaglibDTD) ||
			Validator.isNotNull(themeTaglibDTD) ||
			Validator.isNotNull(uiTaglibDTD) ||
			Validator.isNotNull(utilTaglibDTD)) {

			hasTaglib = true;
		}

		if (hasTaglib && (webXmlVersion > 2.3)) {
			sb.append("<jsp-config>");
		}

		if (Validator.isNotNull(auiTaglibDTD)) {
			sb.append("<taglib>");
			sb.append("<taglib-uri>http://liferay.com/tld/aui</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-aui.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
		}

		if (Validator.isNotNull(portletTaglibDTD)) {
			sb.append("<taglib>");
			sb.append("<taglib-uri>http://java.sun.com/portlet_2_0");
			sb.append("</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-portlet_2_0.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
			sb.append("<taglib>");
			sb.append("<taglib-uri>");
			sb.append("http://xmlns.jcp.org/portlet_3_0");
			sb.append("</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-portlet_3_0.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
		}

		if (Validator.isNotNull(portletExtTaglibDTD)) {
			sb.append("<taglib>");
			sb.append("<taglib-uri>");
			sb.append("http://liferay.com/tld/portlet");
			sb.append("</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-portlet-ext.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
		}

		if (Validator.isNotNull(securityTaglibDTD)) {
			sb.append("<taglib>");
			sb.append("<taglib-uri>");
			sb.append("http://liferay.com/tld/security");
			sb.append("</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-security.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
		}

		if (Validator.isNotNull(themeTaglibDTD)) {
			sb.append("<taglib>");
			sb.append("<taglib-uri>http://liferay.com/tld/theme</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-theme.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
		}

		if (Validator.isNotNull(uiTaglibDTD)) {
			sb.append("<taglib>");
			sb.append("<taglib-uri>http://liferay.com/tld/ui</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-ui.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
		}

		if (Validator.isNotNull(utilTaglibDTD)) {
			sb.append("<taglib>");
			sb.append("<taglib-uri>http://liferay.com/tld/util</taglib-uri>");
			sb.append("<taglib-location>");
			sb.append("/WEB-INF/tld/liferay-util.tld");
			sb.append("</taglib-location>");
			sb.append("</taglib>");
		}

		if (hasTaglib && (webXmlVersion > 2.3)) {
			sb.append("</jsp-config>");
		}

		return sb.toString();
	}

	public String getIgnoreFiltersContent(File srcFile) throws Exception {
		boolean ignoreFiltersEnabled = true;

		Properties properties = _getPluginPackageProperties(srcFile);

		if (properties != null) {
			ignoreFiltersEnabled = GetterUtil.getBoolean(
				properties.getProperty("ignore-filters-enabled"), true);
		}

		if (ignoreFiltersEnabled) {
			return FileUtil.read(
				DeployUtil.getResourcePath(
					tempDirPaths, "ignore-filters-web.xml"));
		}

		return StringPool.BLANK;
	}

	public String getPluginType() {
		return _pluginType;
	}

	@Override
	public Map<String, String> processPluginPackageProperties(
			File srcFile, String displayName, PluginPackage pluginPackage)
		throws Exception {

		if (pluginPackage == null) {
			return null;
		}

		Properties properties = _getPluginPackageProperties(srcFile);

		if ((properties == null) || properties.isEmpty()) {
			return null;
		}

		Map<String, String> filterMap = _getPluginPackageXmlFilterMap(
			pluginPackage);

		if (filterMap == null) {
			return null;
		}

		copyDependencyXml(
			"liferay-plugin-package.xml", srcFile + "/WEB-INF", filterMap,
			true);

		return filterMap;
	}

	protected String auiTaglibDTD;
	protected String portletExtTaglibDTD;
	protected String portletTaglibDTD;
	protected String securityTaglibDTD;
	protected final Set<Path> tempDirPaths = new HashSet<>();
	protected String themeTaglibDTD;
	protected String uiTaglibDTD;
	protected String utilTaglibDTD;

	private void _copyJars(File srcFile) throws Exception {
		for (String jar : _jars) {
			String jarFullName = DeployUtil.getResourcePath(tempDirPaths, jar);

			String jarName = jarFullName.substring(
				jarFullName.lastIndexOf("/") + 1);

			if (!FileUtil.exists(jarFullName)) {
				DeployUtil.getResourcePath(tempDirPaths, jarName);
			}

			FileUtil.copyFile(jarFullName, srcFile + "/WEB-INF/lib/" + jarName);
		}
	}

	private void _copyPortalDependencies(File srcFile) throws Exception {
		Properties properties = _getPluginPackageProperties(srcFile);

		if (properties == null) {
			return;
		}

		// jars

		String[] portalJars = StringUtil.split(
			properties.getProperty(
				"portal-dependency-jars",
				properties.getProperty("portal.dependency.jars")));

		for (String portalJar : portalJars) {
			portalJar = portalJar.trim();

			if (_log.isDebugEnabled()) {
				_log.debug("Copy portal JAR " + portalJar);
			}

			try {
				String portalJarPath =
					PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR +
						portalJar;

				FileUtil.copyFile(
					portalJarPath, srcFile + "/WEB-INF/lib/" + portalJar);
			}
			catch (Exception exception) {
				_log.error("Unable to copy portal JAR " + portalJar, exception);
			}
		}

		// tlds

		String[] portalTlds = StringUtil.split(
			properties.getProperty(
				"portal-dependency-tlds",
				properties.getProperty("portal.dependency.tlds")));

		for (String portalTld : portalTlds) {
			portalTld = portalTld.trim();

			if (_log.isDebugEnabled()) {
				_log.debug("Copy portal TLD " + portalTld);
			}

			try {
				String portalTldPath = DeployUtil.getResourcePath(
					tempDirPaths, portalTld);

				FileUtil.copyFile(
					portalTldPath, srcFile + "/WEB-INF/tld/" + portalTld);
			}
			catch (Exception exception) {
				_log.error("Unable to copy portal TLD " + portalTld, exception);
			}
		}
	}

	private void _copyProperties(File srcFile, PluginPackage pluginPackage)
		throws Exception {

		File servicePropertiesFile = new File(
			srcFile.getAbsolutePath() + "/WEB-INF/classes/service.properties");

		if (!servicePropertiesFile.exists()) {
			return;
		}

		File portletPropertiesFile = new File(
			srcFile.getAbsolutePath() + "/WEB-INF/classes/portlet.properties");

		if (portletPropertiesFile.exists()) {
			return;
		}

		String pluginPackageName = null;

		if (pluginPackage != null) {
			pluginPackageName = pluginPackage.getName();
		}
		else {
			pluginPackageName = srcFile.getName();
		}

		FileUtil.write(
			portletPropertiesFile, "plugin.package.name=" + pluginPackageName);
	}

	private void _copyTlds(File srcFile) throws Exception {
		if (Validator.isNotNull(auiTaglibDTD)) {
			FileUtil.copyFile(
				auiTaglibDTD, srcFile + "/WEB-INF/tld/liferay-aui.tld");
		}

		if (Validator.isNotNull(portletTaglibDTD)) {
			FileUtil.copyFile(
				portletTaglibDTD,
				srcFile + "/WEB-INF/tld/liferay-portlet_3_0.tld");
			FileUtil.copyFile(
				DeployUtil.getResourcePath(
					tempDirPaths, "liferay-portlet_2_0.tld"),
				srcFile + "/WEB-INF/tld/liferay-portlet_2_0.tld");
		}

		if (Validator.isNotNull(portletExtTaglibDTD)) {
			FileUtil.copyFile(
				portletExtTaglibDTD,
				srcFile + "/WEB-INF/tld/liferay-portlet-ext.tld");
		}

		if (Validator.isNotNull(securityTaglibDTD)) {
			FileUtil.copyFile(
				securityTaglibDTD,
				srcFile + "/WEB-INF/tld/liferay-security.tld");
		}

		if (Validator.isNotNull(themeTaglibDTD)) {
			FileUtil.copyFile(
				themeTaglibDTD, srcFile + "/WEB-INF/tld/liferay-theme.tld");
		}

		if (Validator.isNotNull(uiTaglibDTD)) {
			FileUtil.copyFile(
				uiTaglibDTD, srcFile + "/WEB-INF/tld/liferay-ui.tld");
		}

		if (Validator.isNotNull(utilTaglibDTD)) {
			FileUtil.copyFile(
				utilTaglibDTD, srcFile + "/WEB-INF/tld/liferay-util.tld");
		}
	}

	private void _deployDirectory(
			File srcFile, File mergeDir, File deployDir, String displayName,
			PluginPackage pluginPackage)
		throws Exception {

		_rewriteFiles(srcFile);

		_mergeDirectory(mergeDir, srcFile);

		processPluginPackageProperties(srcFile, displayName, pluginPackage);

		_copyJars(srcFile);
		_copyProperties(srcFile, pluginPackage);
		_copyTlds(srcFile);
		copyXmls(srcFile, displayName, pluginPackage);
		_copyPortalDependencies(srcFile);

		File webXml = new File(srcFile + "/WEB-INF/web.xml");

		_updateWebXml(webXml, srcFile, displayName);

		if (deployDir == null) {
			return;
		}

		FileUtil.copyDirectory(srcFile, deployDir);
	}

	private boolean _deployFile(
			File srcFile, File mergeDir, File deployDir, String displayName,
			PluginPackage pluginPackage)
		throws Exception {

		Path tempDirPath = Files.createTempDirectory(
			Paths.get(SystemProperties.get(SystemProperties.TMP_DIR)), null);

		File tempDir = tempDirPath.toFile();

		FileUtil.unzip(srcFile, tempDir);

		_deployDirectory(
			tempDir, mergeDir, deployDir, displayName, pluginPackage);

		FileUtil.deltree(tempDir);

		return true;
	}

	private String _getDisplayName(File srcFile) {
		String displayName = srcFile.getName();

		if (StringUtil.endsWith(displayName, ".war") ||
			StringUtil.endsWith(displayName, ".xml")) {

			displayName = displayName.substring(0, displayName.length() - 4);
		}

		return displayName;
	}

	private String _getInvokerFilterContent() {
		return StringBundler.concat(
			_getInvokerFilterContent("ASYNC"),
			_getInvokerFilterContent("ERROR"),
			_getInvokerFilterContent("FORWARD"),
			_getInvokerFilterContent("INCLUDE"),
			_getInvokerFilterContent("REQUEST"));
	}

	private String _getInvokerFilterContent(String dispatcher) {
		StringBundler sb = new StringBundler(24);

		sb.append("<filter>");
		sb.append("<filter-name>Invoker Filter - ");
		sb.append(dispatcher);
		sb.append("</filter-name>");
		sb.append("<filter-class>");
		sb.append(InvokerFilter.class.getName());
		sb.append("</filter-class>");
		sb.append("<async-supported>true</async-supported>");
		sb.append("<init-param>");
		sb.append("<param-name>dispatcher</param-name>");
		sb.append("<param-value>");
		sb.append(dispatcher);
		sb.append("</param-value>");
		sb.append("</init-param>");
		sb.append("</filter>");

		sb.append("<filter-mapping>");
		sb.append("<filter-name>Invoker Filter - ");
		sb.append(dispatcher);
		sb.append("</filter-name>");
		sb.append("<url-pattern>/*</url-pattern>");
		sb.append("<dispatcher>");
		sb.append(dispatcher);
		sb.append("</dispatcher>");
		sb.append("</filter-mapping>");

		return sb.toString();
	}

	private String _getPluginPackageLicensesXml(List<License> licenses) {
		if (licenses.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((5 * licenses.size()) + 2);

		for (int i = 0; i < licenses.size(); i++) {
			License license = licenses.get(i);

			if (i == 0) {
				sb.append("\r\n");
			}

			sb.append("\t\t<license osi-approved=\"");
			sb.append(license.isOsiApproved());
			sb.append("\">");
			sb.append(license.getName());
			sb.append("</license>\r\n");

			if ((i + 1) == licenses.size()) {
				sb.append("\t");
			}
		}

		return sb.toString();
	}

	private String _getPluginPackageLiferayVersionsXml(
		List<String> liferayVersions) {

		if (liferayVersions.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((liferayVersions.size() * 3) + 2);

		for (int i = 0; i < liferayVersions.size(); i++) {
			String liferayVersion = liferayVersions.get(i);

			if (i == 0) {
				sb.append("\r\n");
			}

			sb.append("\t\t<liferay-version>");
			sb.append(liferayVersion);
			sb.append("</liferay-version>\r\n");

			if ((i + 1) == liferayVersions.size()) {
				sb.append("\t");
			}
		}

		return sb.toString();
	}

	private Properties _getPluginPackageProperties(File srcFile)
		throws Exception {

		File propertiesFile = new File(
			srcFile + "/WEB-INF/liferay-plugin-package.properties");

		if (!propertiesFile.exists()) {
			return null;
		}

		String propertiesString = FileUtil.read(propertiesFile);

		return PropertiesUtil.load(propertiesString);
	}

	private String _getPluginPackageRequiredDeploymentContextsXml(
		List<String> requiredDeploymentContexts) {

		if (requiredDeploymentContexts.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(
			(requiredDeploymentContexts.size() * 3) + 2);

		for (int i = 0; i < requiredDeploymentContexts.size(); i++) {
			String requiredDeploymentContext = requiredDeploymentContexts.get(
				i);

			if (i == 0) {
				sb.append("\r\n");
			}

			sb.append("\t\t<required-deployment-context>");
			sb.append(requiredDeploymentContext);
			sb.append("</required-deployment-context>\r\n");

			if ((i + 1) == requiredDeploymentContexts.size()) {
				sb.append("\t");
			}
		}

		return sb.toString();
	}

	private String _getPluginPackageTagsXml(List<String> tags) {
		if (tags.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler((tags.size() * 3) + 2);

		for (int i = 0; i < tags.size(); i++) {
			String tag = tags.get(i);

			if (i == 0) {
				sb.append("\r\n");
			}

			sb.append("\t\t<tag>");
			sb.append(tag);
			sb.append("</tag>\r\n");

			if ((i + 1) == tags.size()) {
				sb.append("\t");
			}
		}

		return sb.toString();
	}

	private Map<String, String> _getPluginPackageXmlFilterMap(
		PluginPackage pluginPackage) {

		List<String> pluginTypes = pluginPackage.getTypes();

		String pluginType = pluginTypes.get(0);

		if (!pluginType.equals(getPluginType())) {
			return null;
		}

		return HashMapBuilder.put(
			"author", _wrapCDATA(pluginPackage.getAuthor())
		).put(
			"change_log", _wrapCDATA(pluginPackage.getChangeLog())
		).put(
			"licenses",
			_getPluginPackageLicensesXml(pluginPackage.getLicenses())
		).put(
			"liferay_versions",
			_getPluginPackageLiferayVersionsXml(
				pluginPackage.getLiferayVersions())
		).put(
			"long_description", _wrapCDATA(pluginPackage.getLongDescription())
		).put(
			"module_artifact_id", pluginPackage.getArtifactId()
		).put(
			"module_group_id", pluginPackage.getGroupId()
		).put(
			"module_version", pluginPackage.getVersion()
		).put(
			"page_url", pluginPackage.getPageURL()
		).put(
			"plugin_name", _wrapCDATA(pluginPackage.getName())
		).put(
			"plugin_type", pluginType
		).put(
			"plugin_type_name",
			TextFormatter.format(pluginType, TextFormatter.J)
		).put(
			"recommended_deployment_context",
			pluginPackage.getRecommendedDeploymentContext()
		).put(
			"required_deployment_contexts",
			_getPluginPackageRequiredDeploymentContextsXml(
				pluginPackage.getRequiredDeploymentContexts())
		).put(
			"short_description", _wrapCDATA(pluginPackage.getShortDescription())
		).put(
			"tags", _getPluginPackageTagsXml(pluginPackage.getTags())
		).build();
	}

	private void _mergeDirectory(File mergeDir, File targetDir)
		throws Exception {

		if ((mergeDir == null) || !mergeDir.exists()) {
			return;
		}

		FileUtil.copyDirectory(mergeDir, targetDir);
	}

	private PluginPackage _readPluginPackage(File file) {
		if (!file.exists()) {
			return null;
		}

		InputStream inputStream = null;
		ZipFile zipFile = null;

		try {
			boolean parseProps = false;

			if (file.isDirectory()) {
				String path = file.getPath();

				File pluginPackageXmlFile = new File(
					StringBundler.concat(
						file.getParent(), "/merge/", file.getName(),
						"/WEB-INF/liferay-plugin-package.xml"));

				if (pluginPackageXmlFile.exists()) {
					inputStream = new FileInputStream(pluginPackageXmlFile);
				}
				else {
					pluginPackageXmlFile = new File(
						path + "/WEB-INF/liferay-plugin-package.xml");

					if (pluginPackageXmlFile.exists()) {
						inputStream = new FileInputStream(pluginPackageXmlFile);
					}
				}

				File pluginPackagePropertiesFile = new File(
					StringBundler.concat(
						file.getParent(), "/merge/", file.getName(),
						"/WEB-INF/liferay-plugin-package.properties"));

				if ((inputStream == null) &&
					pluginPackagePropertiesFile.exists()) {

					inputStream = new FileInputStream(
						pluginPackagePropertiesFile);

					parseProps = true;
				}
				else {
					pluginPackagePropertiesFile = new File(
						path + "/WEB-INF/liferay-plugin-package.properties");

					if ((inputStream == null) &&
						pluginPackagePropertiesFile.exists()) {

						inputStream = new FileInputStream(
							pluginPackagePropertiesFile);

						parseProps = true;
					}
				}
			}
			else {
				zipFile = new ZipFile(file);

				File pluginPackageXmlFile = new File(
					StringBundler.concat(
						file.getParent(), "/merge/", file.getName(),
						"/WEB-INF/liferay-plugin-package.xml"));

				if (pluginPackageXmlFile.exists()) {
					inputStream = new FileInputStream(pluginPackageXmlFile);
				}
				else {
					ZipEntry zipEntry = zipFile.getEntry(
						"WEB-INF/liferay-plugin-package.xml");

					if (zipEntry != null) {
						inputStream = zipFile.getInputStream(zipEntry);
					}
				}

				File pluginPackagePropertiesFile = new File(
					StringBundler.concat(
						file.getParent(), "/merge/", file.getName(),
						"/WEB-INF/liferay-plugin-package.properties"));

				if ((inputStream == null) &&
					pluginPackagePropertiesFile.exists()) {

					inputStream = new FileInputStream(
						pluginPackagePropertiesFile);

					parseProps = true;
				}
				else {
					ZipEntry zipEntry = zipFile.getEntry(
						"WEB-INF/liferay-plugin-package.properties");

					if ((inputStream == null) && (zipEntry != null)) {
						inputStream = zipFile.getInputStream(zipEntry);

						parseProps = true;
					}
				}
			}

			if (inputStream == null) {
				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							file.getPath(), " does not have a ",
							"WEB-INF/liferay-plugin-package.xml or ",
							"WEB-INF/liferay-plugin-package.properties"));
				}

				return null;
			}

			if (parseProps) {
				String propertiesString = StringUtil.read(inputStream);

				Properties properties = PropertiesUtil.load(propertiesString);

				return PluginPackageUtil.readPluginPackageProperties(
					_getDisplayName(file), properties);
			}

			String xml = StringUtil.read(inputStream);

			// LEP-1921

			int pos = xml.indexOf(CharPool.LESS_THAN);

			if (pos > 0) {
				xml = xml.substring(pos);
			}

			return PluginPackageUtil.readPluginPackageXml(xml);
		}
		catch (Exception exception) {
			_log.error(file.getPath() + ": " + exception.toString(), exception);
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException ioException) {
					if (_log.isDebugEnabled()) {
						_log.debug(ioException);
					}
				}
			}

			if (zipFile != null) {
				try {
					zipFile.close();
				}
				catch (IOException ioException) {
					if (_log.isDebugEnabled()) {
						_log.debug(ioException);
					}
				}
			}
		}

		return null;
	}

	private void _rewriteFiles(File srcDir) throws Exception {
		String[] fileNames = FileUtil.listFiles(srcDir + "/WEB-INF/");

		for (String fileName : fileNames) {
			String shortFileName = GetterUtil.getString(
				FileUtil.getShortFileName(fileName));

			// LEP-6415

			if (StringUtil.equalsIgnoreCase(shortFileName, "mule-config.xml")) {
				continue;
			}

			String ext = GetterUtil.getString(FileUtil.getExtension(fileName));

			if (!StringUtil.equalsIgnoreCase(ext, "xml")) {
				continue;
			}

			// Make sure to rewrite any XML files to include external entities
			// into same file. See LEP-3142.

			File file = new File(srcDir + "/WEB-INF/" + fileName);

			try {
				Document doc = UnsecureSAXReaderUtil.read(file);

				String content = doc.formattedString(StringPool.TAB, true);

				FileUtil.write(file, content);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to format ", file, ": ",
							exception.getMessage()));
				}
			}
		}
	}

	private String _secureWebXml(
			String content, boolean hasCustomServletListener,
			boolean securityManagerEnabled)
		throws Exception {

		if (!hasCustomServletListener && !securityManagerEnabled) {
			return content;
		}

		Document document = UnsecureSAXReaderUtil.read(content);

		Element rootElement = document.getRootElement();

		List<String> listenerClasses = new ArrayList<>();

		List<Element> listenerElements = rootElement.elements("listener");

		for (Element listenerElement : listenerElements) {
			String listenerClass = GetterUtil.getString(
				listenerElement.elementText("listener-class"));

			if (listenerClass.equals(PluginContextListener.class.getName()) ||
				listenerClass.equals(
					SecurePluginContextListener.class.getName())) {

				continue;
			}

			listenerClasses.add(listenerClass);

			listenerElement.detach();
		}

		Element contextParamElement = rootElement.addElement("context-param");

		Element paramNameElement = contextParamElement.addElement("param-name");

		paramNameElement.addText("portalListenerClasses");

		Element paramValueElement = contextParamElement.addElement(
			"param-value");

		paramValueElement.addText(StringUtil.merge(listenerClasses));

		if (!securityManagerEnabled) {
			return document.compactString();
		}

		List<Element> servletElements = rootElement.elements("servlet");

		for (Element servletElement : servletElements) {
			Element servletClassElement = servletElement.element(
				"servlet-class");

			String servletClass = GetterUtil.getString(
				servletClassElement.getText());

			if (servletClass.equals(PortalClassLoaderServlet.class.getName()) ||
				servletClass.equals(PortalDelegateServlet.class.getName()) ||
				servletClass.equals(PortletServlet.class.getName())) {

				continue;
			}

			servletClassElement.setText(SecureServlet.class.getName());

			Element initParamElement = servletElement.addElement("init-param");

			paramNameElement = initParamElement.addElement("param-name");

			paramNameElement.addText("servlet-class");

			paramNameElement = initParamElement.addElement("param-value");

			paramNameElement.addText(servletClass);
		}

		return document.compactString();
	}

	private String _updateLiferayWebXml(
			double webXmlVersion, File srcFile, String webXmlContent)
		throws Exception {

		boolean liferayWebXmlEnabled = true;

		Properties properties = _getPluginPackageProperties(srcFile);

		if (properties != null) {
			liferayWebXmlEnabled = GetterUtil.getBoolean(
				properties.getProperty("liferay-web-xml-enabled"), true);
		}

		webXmlContent = WebXMLBuilder.organizeWebXML(webXmlContent);

		int x = webXmlContent.indexOf("<filter>");
		int y = webXmlContent.lastIndexOf("</filter-mapping>");

		String webXmlFiltersContent = StringPool.BLANK;

		if ((x == -1) || (y == -1)) {
			x = webXmlContent.lastIndexOf("</display-name>") + 15;

			y = x;
		}
		else {
			if (liferayWebXmlEnabled && (webXmlVersion > 2.3)) {
				webXmlFiltersContent = webXmlContent.substring(x, y + 17);

				y = y + 17;
			}
			else {
				x = y + 17;
				y = y + 17;
			}
		}

		if (webXmlVersion < 2.4) {
			return webXmlContent.substring(0, x) + webXmlContent.substring(y);
		}

		if (!webXmlFiltersContent.isEmpty()) {
			String liferayWebXmlContent = FileUtil.read(
				DeployUtil.getResourcePath(tempDirPaths, "web.xml"));

			int z = liferayWebXmlContent.indexOf("</web-app>");

			liferayWebXmlContent =
				liferayWebXmlContent.substring(0, z) + webXmlFiltersContent +
					liferayWebXmlContent.substring(z);

			liferayWebXmlContent = WebXMLBuilder.organizeWebXML(
				liferayWebXmlContent);

			FileUtil.write(
				srcFile + "/WEB-INF/liferay-web.xml", liferayWebXmlContent);
		}

		return webXmlContent.substring(0, x) + _getInvokerFilterContent() +
			webXmlContent.substring(y);
	}

	private void _updateWebXml(File webXml, File srcFile, String displayName)
		throws Exception {

		// Check version

		String content = FileUtil.read(webXml);

		content = WebXMLBuilder.organizeWebXML(content);

		int x = content.indexOf("<display-name>");

		if (x != -1) {
			int y = content.indexOf("</display-name>", x);

			y = content.indexOf(">", y) + 1;

			content = content.substring(0, x) + content.substring(y);
		}

		Document document = UnsecureSAXReaderUtil.read(content);

		Element rootElement = document.getRootElement();

		double webXmlVersion = GetterUtil.getDouble(
			rootElement.attributeValue("version"), 2.3);

		if (webXmlVersion <= 2.3) {
			throw new AutoDeployException(
				webXml.getName() +
					" must be updated to the Servlet 2.4 specification");
		}

		// Plugin context listener

		StringBundler sb = new StringBundler(5);

		sb.append("<listener>");
		sb.append("<listener-class>");

		boolean hasCustomServletListener = false;

		List<Element> listenerElements = rootElement.elements("listener");

		for (Element listenerElement : listenerElements) {
			String listenerClass = GetterUtil.getString(
				listenerElement.elementText("listener-class"));

			if (listenerClass.equals(PluginContextListener.class.getName()) ||
				listenerClass.equals(
					SerializableSessionAttributeListener.class.getName()) ||
				listenerClass.equals(
					SecurePluginContextListener.class.getName())) {

				continue;
			}

			hasCustomServletListener = true;

			break;
		}

		boolean securityManagerEnabled = false;

		Properties properties = _getPluginPackageProperties(srcFile);

		if (properties != null) {
			securityManagerEnabled = GetterUtil.getBoolean(
				properties.getProperty("security-manager-enabled"));
		}

		if (hasCustomServletListener || securityManagerEnabled) {
			sb.append(SecurePluginContextListener.class.getName());
		}
		else {
			sb.append(PluginContextListener.class.getName());
		}

		sb.append("</listener-class>");
		sb.append("</listener>");

		String pluginContextListenerContent = sb.toString();

		// Merge content

		String extraContent = getExtraContent(
			webXmlVersion, srcFile, displayName);

		int pos = content.indexOf("<listener>");

		if (pos == -1) {
			pos = content.indexOf("</web-app>");
		}

		String newContent = StringBundler.concat(
			content.substring(0, pos), pluginContextListenerContent,
			extraContent, content.substring(pos));

		// Update liferay-web.xml

		newContent = _updateLiferayWebXml(webXmlVersion, srcFile, newContent);

		// Update web.xml

		newContent = _secureWebXml(
			newContent, hasCustomServletListener, securityManagerEnabled);

		newContent = WebXMLBuilder.organizeWebXML(newContent);

		FileUtil.write(webXml, newContent, true);

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Modifying Servlet ", webXmlVersion, " ", webXml));
		}
	}

	private String _wrapCDATA(String string) {
		return StringPool.CDATA_OPEN + string + StringPool.CDATA_CLOSE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseAutoDeployer.class);

	private static final List<String> _jars = Arrays.asList(
		"util-bridges.jar", "util-java.jar");

	private final String _pluginType;

	private static class JarFileNameFilter implements FilenameFilter {

		@Override
		public boolean accept(File file, String fileName) {
			if (fileName.startsWith(_prefix) && fileName.endsWith(".jar")) {
				return true;
			}

			return false;
		}

		private JarFileNameFilter(String prefix) {
			_prefix = prefix;
		}

		private final String _prefix;

	}

}