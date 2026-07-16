/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.plugin.op.connect;

import java.util.Collections;
import java.util.List;

import jenkins.model.Jenkins;

/**
 * @author Michael Hashimoto
 */
public class OPConnectUtil {

	public static String getAccessToken() {
		OPConnectDescriptor opConnectDescriptor = _getOPConnectDescriptor();

		if (opConnectDescriptor == null) {
			return null;
		}

		return opConnectDescriptor.getAccessToken();
	}

	public static List<String> getIgnoredValues() {
		OPConnectDescriptor opConnectDescriptor = _getOPConnectDescriptor();

		if (opConnectDescriptor == null) {
			return Collections.emptyList();
		}

		return opConnectDescriptor.getIgnoredValues();
	}

	public static long getRefreshIntervalMinutes() {
		OPConnectDescriptor opConnectDescriptor = _getOPConnectDescriptor();

		if (opConnectDescriptor == null) {
			return 0;
		}

		return opConnectDescriptor.getRefreshIntervalMinutes();
	}

	public static List<String> getSecretValues() {
		OPConnectDescriptor opConnectDescriptor = _getOPConnectDescriptor();

		if (opConnectDescriptor == null) {
			return Collections.emptyList();
		}

		return opConnectDescriptor.getSecretValues();
	}

	public static void refreshSecretValues() {
		OPConnectDescriptor opConnectDescriptor = _getOPConnectDescriptor();

		if (opConnectDescriptor != null) {
			opConnectDescriptor.refreshSecretValues();
		}
	}

	private static OPConnectDescriptor _getOPConnectDescriptor() {
		Jenkins jenkins = Jenkins.getInstanceOrNull();

		if (jenkins == null) {
			return null;
		}

		return jenkins.getDescriptorByType(OPConnectDescriptor.class);
	}

}