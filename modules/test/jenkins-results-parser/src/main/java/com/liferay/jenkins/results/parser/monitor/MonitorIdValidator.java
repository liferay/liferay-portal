/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Brittney Nguyen
 */
public class MonitorIdValidator {

	public static void validate(List<Monitor> monitors) {
		Set<String> ids = new HashSet<>();

		for (Monitor monitor : monitors) {
			String id = monitor.getId();

			if (!ids.add(id)) {
				throw new IllegalArgumentException(
					"Duplicate monitor ID: " + id);
			}
		}
	}

}