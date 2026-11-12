/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.jdbc.util;

import com.liferay.petra.lang.HashUtil;

import java.util.Objects;

/**
 * @author Shuyang Zhou
 */
public class DBInfo {

	public DBInfo(String name, int majorVersion, int minorVersion) {
		_name = name;
		_majorVersion = majorVersion;
		_minorVersion = minorVersion;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DBInfo)) {
			return false;
		}

		DBInfo dbInfo = (DBInfo)object;

		if ((_majorVersion == dbInfo._majorVersion) &&
			(_minorVersion == dbInfo._minorVersion) &&
			Objects.equals(_name, dbInfo._name)) {

			return true;
		}

		return false;
	}

	public int getMajorVersion() {
		return _majorVersion;
	}

	public int getMinorVersion() {
		return _minorVersion;
	}

	public String getName() {
		return _name;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _majorVersion);

		hash = HashUtil.hash(hash, _minorVersion);

		return HashUtil.hash(hash, _name);
	}

	private final int _majorVersion;
	private final int _minorVersion;
	private final String _name;

}