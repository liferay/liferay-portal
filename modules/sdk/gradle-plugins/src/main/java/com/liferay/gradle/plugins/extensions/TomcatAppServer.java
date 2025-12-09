/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.extensions;

import com.liferay.gradle.plugins.internal.util.GradleUtil;

import org.gradle.api.Project;

/**
 * @author Andrea Di Giorgi
 */
public class TomcatAppServer extends AppServer {

	public TomcatAppServer(Project project) {
		super("tomcat", project);
	}

	public String getManagerPassword() {
		return GradleUtil.toString(_managerPassword);
	}

	public String getManagerUserName() {
		return GradleUtil.toString(_managerUserName);
	}

	public String getZipName() {
		return GradleUtil.toString(_zipName);
	}

	public void setManagerPassword(Object managerPassword) {
		_managerPassword = managerPassword;
	}

	public void setManagerUserName(Object managerUserName) {
		_managerUserName = managerUserName;
	}

	public void setZipName(Object zipName) {
		_zipName = zipName;
	}

	private Object _managerPassword;
	private Object _managerUserName;
	private Object _zipName;

}