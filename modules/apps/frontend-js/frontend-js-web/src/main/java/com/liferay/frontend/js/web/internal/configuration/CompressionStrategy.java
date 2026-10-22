/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.configuration;

import java.util.Objects;

/**
 * @author Iván Zaera Avellón
 */
public enum CompressionStrategy {

	DO_NOT_COMPRESS("do-not-compress"),
	USE_BEST_SPEED_COMPRESSION("use-best-speed-compression"),
	USE_INTERMEDIATE_COMPRESSION("use-intermediate-compression"),
	USE_MAXIMUM_COMPRESSION("use-maximum-compression"),
	USE_SYSTEM_DEFAULTS("use-system-defaults");

	public static CompressionStrategy fromValue(
		String value, CompressionStrategy defaultCompressionStrategy) {

		for (CompressionStrategy compressionStrategy : values()) {
			if (Objects.equals(compressionStrategy.getValue(), value)) {
				return compressionStrategy;
			}
		}

		return defaultCompressionStrategy;
	}

	public String getValue() {
		return _value;
	}

	private CompressionStrategy(String value) {
		_value = value;
	}

	private final String _value;

}