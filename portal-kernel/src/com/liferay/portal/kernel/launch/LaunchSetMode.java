/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.launch;

import com.liferay.portal.kernel.util.Constants;

import java.util.Objects;

/**
 * @author Carlos Correa
 */
public enum LaunchSetMode {

	EDIT(Constants.EDIT), PREVIEW(Constants.PREVIEW);

	public static LaunchSetMode parse(String value) {
		for (LaunchSetMode launchSetMode : values()) {
			if (Objects.equals(launchSetMode.getValue(), value)) {
				return launchSetMode;
			}
		}

		return null;
	}

	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return _value;
	}

	private LaunchSetMode(String value) {
		_value = value;
	}

	private final String _value;

}