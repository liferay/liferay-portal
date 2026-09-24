/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

import java.util.Arrays;

/**
 * @author Christopher Kian
 */
public class CompanyKeyCacheEntry {

	public CompanyKeyCacheEntry(
		long expirationTime, byte[] keyBytes, String keyString) {

		if (keyBytes == null) {
			throw new IllegalArgumentException("Key bytes are null");
		}

		_expirationTime = expirationTime;
		_keyBytes = Arrays.copyOf(keyBytes, keyBytes.length);
		_keyString = keyString;
	}

	public synchronized void destroy() {
		Arrays.fill(_keyBytes, (byte)0);

		_destroyed = true;
	}

	public synchronized byte[] getKeyBytes() {
		if (_destroyed) {
			return null;
		}

		return Arrays.copyOf(_keyBytes, _keyBytes.length);
	}

	public String getKeyString() {
		return _keyString;
	}

	public boolean isExpired(long time) {
		if (time >= _expirationTime) {
			return true;
		}

		return false;
	}

	private boolean _destroyed;
	private final long _expirationTime;
	private final byte[] _keyBytes;
	private final String _keyString;

}