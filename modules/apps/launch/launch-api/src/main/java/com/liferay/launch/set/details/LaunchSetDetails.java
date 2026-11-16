/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.set.details;

/**
 * @author Lourdes Fernández Besada
 */
public class LaunchSetDetails {

	public LaunchSetDetails(long launchSetId, String name) {
		_launchSetId = launchSetId;
		_name = name;
	}

	public long getLaunchSetId() {
		return _launchSetId;
	}

	public String getName() {
		return _name;
	}

	private final long _launchSetId;
	private final String _name;

}