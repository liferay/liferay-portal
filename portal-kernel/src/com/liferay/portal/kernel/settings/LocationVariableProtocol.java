/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.settings;

import java.util.Objects;

/**
 * @author Drew Brokke
 */
public enum LocationVariableProtocol {

	FILE("file"), LANGUAGE("language"), RESOURCE("resource"),
	SERVER_PROPERTY("server-property");

	public static boolean isProtocol(String string) {
		for (LocationVariableProtocol locationVariableProtocol :
				LocationVariableProtocol.values()) {

			if (locationVariableProtocol.equals(string)) {
				return true;
			}
		}

		return false;
	}

	public boolean equals(String protocolString) {
		return Objects.equals(_value, protocolString);
	}

	private LocationVariableProtocol(String value) {
		_value = value;
	}

	private final String _value;

}