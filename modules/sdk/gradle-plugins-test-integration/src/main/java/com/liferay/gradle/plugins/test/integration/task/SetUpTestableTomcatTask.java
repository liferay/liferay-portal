/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.test.integration.task;

import com.liferay.gradle.plugins.test.integration.internal.util.GradleUtil;
import com.liferay.gradle.plugins.test.integration.internal.util.StringUtil;
import com.liferay.gradle.util.FileUtil;
import com.liferay.gradle.util.OSDetector;
import com.liferay.gradle.util.Validator;
import com.liferay.gradle.util.copy.ExcludeExistingFileAction;
import com.liferay.gradle.util.copy.StripPathSegmentsAction;

import groovy.xml.DOMBuilder;
import groovy.xml.XmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.VersionNumber;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class SetUpTestableTomcatTask
	extends DefaultTask implements ManagerSpec, ModuleFrameworkBaseDirSpec {

	public SetUpTestableTomcatTask() {
		_zipUrl = new Callable<String>() {

			@Override
			public String call() throws Exception {
				File dir = getDir();

				String dirName = dir.getName();

				int start = StringUtil.indexOfDigit(dirName);

				if (start < 0) {
					return null;
				}

				VersionNumber versionNumber = VersionNumber.parse(
					dirName.substring(start));

				if (versionNumber == VersionNumber.UNKNOWN) {
					return null;
				}

				StringBuilder sb = new StringBuilder();

				sb.append("http://archive.apache.org/dist/tomcat/tomcat-");
				sb.append(versionNumber.getMajor());
				sb.append("/v");
				sb.append(versionNumber);
				sb.append("/bin/apache-tomcat-");
				sb.append(versionNumber);
				sb.append(".zip");

				return sb.toString();
			}

		};
	}

	@Input
	@Optional
	public String getAspectJAgent() {
		return GradleUtil.toString(_aspectJAgent);
	}

	@Input
	@Optional
	public String getAspectJConfiguration() {
		return GradleUtil.toString(_aspectJConfiguration);
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getBinDir() {
		return new File(getDir(), "bin");
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getDir() {
		return GradleUtil.toFile(getProject(), _dir);
	}

	@Input
	@Optional
	public String getJaCoCoAgentConfiguration() {
		return GradleUtil.toString(_jaCoCoAgentConfiguration);
	}

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getJaCoCoAgentFile() {
		return GradleUtil.toFile(getProject(), _jaCoCoAgentFile);
	}

	@Input
	@Override
	public String getManagerPassword() {
		return GradleUtil.toString(_managerPassword);
	}

	@Input
	@Override
	public String getManagerUserName() {
		return GradleUtil.toString(_managerUserName);
	}

	@Internal
	@Override
	public File getModuleFrameworkBaseDir() {
		return GradleUtil.toFile(getProject(), _moduleFrameworkBaseDir);
	}

	@Input
	public String getZipUrl() {
		return GradleUtil.toString(_zipUrl);
	}

	@Input
	public boolean isDebugLogging() {
		return _debugLogging;
	}

	@Input
	public boolean isOverwriteTestModules() {
		return _overwriteTestModules;
	}

	public void setAspectJAgent(Object aspectJAgent) {
		_aspectJAgent = aspectJAgent;
	}

	public void setAspectJConfiguration(Object aspectJConfiguration) {
		_aspectJConfiguration = aspectJConfiguration;
	}

	public void setDebugLogging(boolean debugLogging) {
		_debugLogging = debugLogging;
	}

	public void setDir(Object dir) {
		_dir = dir;
	}

	public void setJaCoCoAgentConfiguration(Object jaCoCoAgentConfiguration) {
		_jaCoCoAgentConfiguration = jaCoCoAgentConfiguration;
	}

	public void setJaCoCoAgentFile(Object jaCoCoAgentFile) {
		_jaCoCoAgentFile = jaCoCoAgentFile;
	}

	@Override
	public void setManagerPassword(Object managerPassword) {
		_managerPassword = managerPassword;
	}

	@Override
	public void setManagerUserName(Object managerUserName) {
		_managerUserName = managerUserName;
	}

	@Override
	public void setModuleFrameworkBaseDir(Object moduleFrameworkBaseDir) {
		_moduleFrameworkBaseDir = moduleFrameworkBaseDir;
	}

	public void setOverwriteTestModules(boolean overwriteTestModules) {
		_overwriteTestModules = overwriteTestModules;
	}

	@TaskAction
	public void setUpTestableTomcat() throws Exception {
		_setUpFilePermissions();
		_setUpLogging();
		_setUpManager();
		_setUpOsgiModules();
		_setUpSetEnv();
	}

	public void setZipUrl(Object zipUrl) {
		_zipUrl = zipUrl;
	}

	private boolean _contains(String fileName, String s) throws Exception {
		File file = new File(getDir(), fileName);

		String fileContent = new String(Files.readAllBytes(file.toPath()));

		return fileContent.contains(s);
	}

	private PrintWriter _getAppendPrintWriter(String fileName)
		throws IOException {

		File file = new File(getDir(), fileName);

		return new PrintWriter(
			Files.newBufferedWriter(
				file.toPath(), StandardCharsets.UTF_8,
				StandardOpenOption.APPEND, StandardOpenOption.WRITE));
	}

	private void _setUpAspectJ() throws Exception {
		String aspectJAgent = getAspectJAgent();

		if (Validator.isNotNull(aspectJAgent) &&
			!_contains("bin/setenv.sh", aspectJAgent)) {

			try (PrintWriter printWriter = _getAppendPrintWriter(
					"bin/setenv.sh")) {

				printWriter.println();

				printWriter.println("if [ \"${1}\" = \"aspectj\" ]");
				printWriter.println("then");
				printWriter.print("\tCATALINA_OPTS=\"${CATALINA_OPTS} ");
				printWriter.print(aspectJAgent);
				printWriter.print(
					" -Dorg.aspectj.weaver.loadtime.configuration=");

				String aspectJConfiguration = getAspectJConfiguration();

				if (Validator.isNotNull(aspectJConfiguration)) {
					printWriter.print(aspectJConfiguration);
				}

				printWriter.println("\"");
				printWriter.println("");
				printWriter.println("\tshift");
				printWriter.println("fi");
			}
		}
	}

	private void _setUpFilePermissions() {
		if (OSDetector.isWindows()) {
			return;
		}

		File binDir = getBinDir();

		for (File file : binDir.listFiles()) {
			if (!file.isFile()) {
				continue;
			}

			String fileName = file.getName();

			if (!fileName.endsWith(".sh")) {
				continue;
			}

			file.setExecutable(true);
		}
	}

	private void _setUpJaCoCo() throws Exception {
		File jaCoCoAgentFile = getJaCoCoAgentFile();
		File targetJaCoCoAgentFile = new File(getDir(), "bin/jacocoagent.jar");

		if ((jaCoCoAgentFile != null) && !targetJaCoCoAgentFile.exists()) {
			Files.copy(
				jaCoCoAgentFile.toPath(), targetJaCoCoAgentFile.toPath());
		}

		String jaCoCoJvmArg =
			"-javaagent:" + targetJaCoCoAgentFile.getAbsolutePath();

		if (_jaCoCoAgentConfiguration != null) {
			jaCoCoJvmArg += _jaCoCoAgentConfiguration;
		}

		if (!_contains("bin/setenv.sh", jaCoCoJvmArg)) {
			try (PrintWriter printWriter = _getAppendPrintWriter(
					"bin/setenv.sh")) {

				printWriter.println();

				printWriter.println("if [ \"$1\" = \"jacoco\" ]");
				printWriter.println("then");
				printWriter.print("    JACOCO_OPTS=\"");
				printWriter.print(jaCoCoJvmArg);
				printWriter.println("\"");
				printWriter.println(
					"    CATALINA_OPTS=\"${CATALINA_OPTS} ${JACOCO_OPTS}\"");
				printWriter.println("    shift");
				printWriter.println("fi");
			}
		}
	}

	private void _setUpJpda() throws Exception {
		if (!_contains("bin/setenv.sh", "JPDA_ADDRESS")) {
			try (PrintWriter printWriter = _getAppendPrintWriter(
					"bin/setenv.sh")) {

				printWriter.println();

				printWriter.println("JPDA_ADDRESS=\"8000\"");
			}
		}
	}

	private void _setUpLogging() throws Exception {
		if (!isDebugLogging() ||
			_contains("conf/Logging.properties", "org.apache.catalina.level")) {

			return;
		}

		try (PrintWriter printWriter = _getAppendPrintWriter(
				"conf/Logging.properties")) {

			printWriter.println("org.apache.catalina.level=ALL");

			printWriter.println();

			printWriter.println(
				"org.apache.catalina.loader.WebappClassLoader.level=INFO");
			printWriter.println(
				"org.apache.catalina.loader.WebappLoader.level=INFO");
			printWriter.println(
				"org.apache.catalina.startup.ClassLoaderFactory.level=INFO");
		}
	}

	private void _setUpManager() throws Exception {
		final File managerDir = new File(getDir(), "webapps/manager");

		if (!managerDir.exists()) {
			final Project project = getProject();

			final File zipFile = FileUtil.get(project, getZipUrl());

			project.copy(
				new Action<CopySpec>() {

					@Override
					public void execute(CopySpec copySpec) {
						copySpec.eachFile(new StripPathSegmentsAction(2));
						copySpec.from(project.zipTree(zipFile));
						copySpec.include(
							"apache-tomcat-*/webapps/manager/**/*");
						copySpec.into(managerDir.getParentFile());
						copySpec.setIncludeEmptyDirs(false);
					}

				});
		}

		Document document = null;

		File tomcatUsersXmlFile = new File(getDir(), "conf/tomcat-users.xml");

		try (InputStreamReader inputStreamReader = new InputStreamReader(
				new FileInputStream(tomcatUsersXmlFile))) {

			document = DOMBuilder.parse(inputStreamReader);
		}

		Element tomcatUsersElement = document.getDocumentElement();

		Set<String> existentRoleNames = new HashSet<>();
		boolean tomcatManagerUserExists = false;

		NodeList nodeList = tomcatUsersElement.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element element = (Element)node;

			String elementName = element.getNodeName();

			if (elementName.equals("role")) {
				String roleName = element.getAttribute("rolename");

				existentRoleNames.add(roleName);
			}
			else if (elementName.equals("user")) {
				String userName = element.getAttribute("username");

				if (userName.equals(getManagerUserName())) {
					tomcatManagerUserExists = true;
				}
			}
		}

		boolean tomcatUsersXmlFileModified = false;

		for (String roleName : _TOMCAT_USERS_ROLE_NAMES) {
			if (existentRoleNames.contains(roleName)) {
				continue;
			}

			Element element = document.createElement("role");

			element.setAttribute("rolename", roleName);

			tomcatUsersElement.appendChild(element);

			tomcatUsersXmlFileModified = true;
		}

		if (!tomcatManagerUserExists) {
			Element element = document.createElement("user");

			element.setAttribute("password", getManagerPassword());
			element.setAttribute(
				"roles",
				"tomcat,manager-gui,manager-script,manager-jmx,manager-status");
			element.setAttribute("username", getManagerUserName());

			tomcatUsersElement.appendChild(element);

			tomcatUsersXmlFileModified = true;
		}

		if (tomcatUsersXmlFileModified) {
			Path timestampTomcatUserXmlFilePath = Paths.get(
				tomcatUsersXmlFile.toString() + "." +
					_timestampDateFormat.format(new Date()));

			Files.copy(
				tomcatUsersXmlFile.toPath(), timestampTomcatUserXmlFilePath);

			try (FileOutputStream fileOutputStream = new FileOutputStream(
					tomcatUsersXmlFile)) {

				XmlUtil.serialize(tomcatUsersElement, fileOutputStream);
			}
		}
	}

	private void _setUpOsgiModules() {
		Project project = getProject();

		project.copy(
			new Action<CopySpec>() {

				@Override
				public void execute(CopySpec copySpec) {
					File moduleFrameworkBaseDir = getModuleFrameworkBaseDir();

					File modulesDir = new File(
						moduleFrameworkBaseDir, "modules");

					if (!isOverwriteTestModules()) {
						copySpec.eachFile(
							new ExcludeExistingFileAction(modulesDir));
					}

					copySpec.from(new File(moduleFrameworkBaseDir, "test"));
					copySpec.into(modulesDir);
				}

			});
	}

	private void _setUpSetEnv() throws Exception {
		_setUpJaCoCo();

		_setUpAspectJ();
		_setUpJpda();
	}

	private static final String[] _TOMCAT_USERS_ROLE_NAMES = {
		"manager-gui", "manager-jmx", "manager-script", "manager-status",
		"tomcat"
	};

	private Object _aspectJAgent;
	private Object _aspectJConfiguration;
	private boolean _debugLogging;
	private Object _dir;
	private Object _jaCoCoAgentConfiguration;
	private Object _jaCoCoAgentFile;
	private Object _managerPassword;
	private Object _managerUserName;
	private Object _moduleFrameworkBaseDir;
	private boolean _overwriteTestModules;
	private final DateFormat _timestampDateFormat = new SimpleDateFormat(
		"yyyyMMddkkmmssSSS");
	private Object _zipUrl;

}