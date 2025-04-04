/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.upgrade.client;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author David Truong
 */
public class AppServer {

	public static AppServer getAppServer(
		File liferayHomeDir, String appServerName) {

		if (appServerName.equals("jboss")) {
			return new AppServer(
				_getAppServerDirName(liferayHomeDir, "jboss-eap"),
				_getJBossExtraLibDirNames(), "/modules/com/liferay/portal/main",
				"/standalone/deployments/ROOT.war", appServerName);
		}

		if (appServerName.equals("tomcat")) {
			return new AppServer(
				_getAppServerDirName(liferayHomeDir, "tomcat"), "/bin", "/lib",
				"/webapps/ROOT", appServerName);
		}

		if (appServerName.equals("weblogic")) {
			return new AppServer(
				_getAppServerDirName(liferayHomeDir, "weblogic"),
				"/wlserver/modules", "/domains/liferay/lib",
				"/domains/liferay/autodeploy/ROOT", appServerName);
		}

		if (appServerName.equals("wildfly")) {
			return new AppServer(
				_getAppServerDirName(liferayHomeDir, "wildfly"),
				_getJBossExtraLibDirNames(), "/modules/com/liferay/portal/main",
				"/standalone/deployments/ROOT.war", appServerName);
		}

		return null;
	}

	public AppServer(
		String dirName, String extraLibDirNames, String globalLibDirName,
		String portalDirName, String serverDetectorServerId) {

		_setDirName(dirName);

		_extraLibDirNames = extraLibDirNames;
		_globalLibDirName = globalLibDirName;
		_portalDirName = portalDirName;
		_serverDetectorServerId = serverDetectorServerId;
	}

	public File getDir() {
		return _dir;
	}

	public String getExtraLibDirNames() {
		return _extraLibDirNames;
	}

	public List<File> getExtraLibDirs() {
		List<File> extraLibDirs = new ArrayList<>();

		if ((_extraLibDirNames != null) && !_extraLibDirNames.isEmpty()) {
			for (String extraLibDirName : _extraLibDirNames.split(",")) {
				extraLibDirs.add(new File(_dir, extraLibDirName));
			}
		}

		return extraLibDirs;
	}

	public File getGlobalLibDir() {
		return new File(_dir, _globalLibDirName);
	}

	public String getGlobalLibDirName() {
		return _globalLibDirName;
	}

	public File getPortalClassesDir() {
		return new File(getPortalDir(), "/WEB-INF/classes");
	}

	public File getPortalDir() {
		return new File(_dir, _portalDirName);
	}

	public String getPortalDirName() {
		return _portalDirName;
	}

	public File getPortalLibDir() {
		return new File(getPortalDir(), "/WEB-INF/lib");
	}

	public File getPortalShieldedContainerLibDir() {
		return new File(getPortalDir(), "/WEB-INF/shielded-container-lib");
	}

	public String getServerDetectorServerId() {
		return _serverDetectorServerId;
	}

	public void setDirName(String dirName) {
		_setDirName(dirName);
	}

	public void setExtraLibDirNames(String extraLibDirNames) {
		_extraLibDirNames = extraLibDirNames;
	}

	public void setGlobalLibDirName(String globalLibDirName) {
		_globalLibDirName = globalLibDirName;
	}

	public void setPortalDirName(String portalDirName) {
		_portalDirName = portalDirName;
	}

	private static String _getAppServerDirName(
		File liferayHomeDir, String dirName) {

		if (!liferayHomeDir.isDirectory()) {
			return dirName;
		}

		File[] files = liferayHomeDir.listFiles();

		if (files == null) {
			return dirName;
		}

		for (File file : files) {
			String fileName = file.getName();

			if (file.isDirectory() &&
				(Objects.equals(file.getName(), dirName) ||
				 fileName.startsWith(dirName + "-"))) {

				try {
					return file.getCanonicalPath();
				}
				catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		return dirName;
	}

	private static String _getJBossExtraLibDirNames() {
		StringBuilder sb = new StringBuilder();

		String extraLibDirPrefix = "/modules/system/layers/base/";

		sb.append(extraLibDirPrefix);

		sb.append("jakarta/mail,");
		sb.append(extraLibDirPrefix);
		sb.append("jakarta/persistence,");
		sb.append(extraLibDirPrefix);
		sb.append("jakarta/servlet,");
		sb.append(extraLibDirPrefix);
		sb.append("jakarta/transaction");

		return sb.toString();
	}

	private void _setDirName(String dirName) {
		try {
			_dir = new File(dirName);

			if (!_dir.isAbsolute()) {
				_dir = _dir.getCanonicalFile();
			}
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private File _dir;
	private String _extraLibDirNames;
	private String _globalLibDirName;
	private String _portalDirName;
	private final String _serverDetectorServerId;

}