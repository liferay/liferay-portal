/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessConfig;

import java.io.Serializable;

import java.net.URL;

/**
 * @author Tina Tian
 */
public class PersistedProcess implements Serializable {

	public PersistedProcess(
		URL bundleURL, String[] processCallableClassNames,
		ProcessConfig processConfig, String processName) {

		_bundleURL = bundleURL;
		_processCallableClassNames = processCallableClassNames;
		_processConfig = processConfig;
		_processName = processName;
	}

	public URL getBundleURL() {
		return _bundleURL;
	}

	public String[] getProcessCallableClassNames() {
		return _processCallableClassNames;
	}

	public ProcessConfig getProcessConfig() {
		return _processConfig;
	}

	public String getProcessName() {
		return _processName;
	}

	private static final long serialVersionUID = 1L;

	private final URL _bundleURL;
	private final String[] _processCallableClassNames;
	private final ProcessConfig _processConfig;
	private final String _processName;

}