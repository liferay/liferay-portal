/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Hashimoto
 */
public class HostFactory {

	public static Host newHost(String name) {
		return _hosts.computeIfAbsent(name, DefaultHost::new);
	}

	private static final Map<String, Host> _hosts = new ConcurrentHashMap<>();

}