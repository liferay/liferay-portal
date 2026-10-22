/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.frontend.hashed.files;

import java.util.Objects;

/**
 * @author Iván Zaera Avellón
 */
public enum CachingStrategy {

	DO_NOT_USE_HASHES("do-not-use-hashes"),
	USE_ONE_HASH_PER_FILE("use-one-hash-per-file"),
	USE_ONE_HASH_PER_WEB_CONTEXT("use-one-hash-per-web-context");

	public static CachingStrategy fromValue(String value) {
		for (CachingStrategy cachingStrategy : values()) {
			if (value.equals(cachingStrategy.getValue())) {
				return cachingStrategy;
			}
		}

		throw new IllegalArgumentException(value);
	}

	public static CachingStrategy fromValue(
		String value, CachingStrategy defaultCachingStrategy) {

		for (CachingStrategy cachingStrategy : values()) {
			if (Objects.equals(cachingStrategy.getValue(), value)) {
				return cachingStrategy;
			}
		}

		return defaultCachingStrategy;
	}

	public String getValue() {
		return _value;
	}

	private CachingStrategy(String value) {
		_value = value;
	}

	private final String _value;

}