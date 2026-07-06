/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

/**
 * @author Brittney Nguyen
 */
public class CheckFactory {

	public static Check newCheck(CheckConfig checkConfig) {
		throw new IllegalArgumentException(
			"Unknown check type: " + checkConfig.getType());
	}

}