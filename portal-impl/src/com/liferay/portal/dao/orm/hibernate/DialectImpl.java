/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.Dialect;

import org.hibernate.dialect.pagination.LimitHandler;

/**
 * @author Brian Wing Shun Chan
 */
public class DialectImpl implements Dialect {

	public DialectImpl(org.hibernate.dialect.Dialect dialect) {
		_dialect = dialect;
	}

	public org.hibernate.dialect.Dialect getWrappedDialect() {
		return _dialect;
	}

	@Override
	public boolean supportsLimit() {
		LimitHandler limitHandler = _dialect.getLimitHandler();

		return limitHandler.supportsLimit();
	}

	@Override
	public String toString() {
		return StringBundler.concat("{_dialect=", _dialect, "}");
	}

	private final org.hibernate.dialect.Dialect _dialect;

}