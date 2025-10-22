/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.internal.change.tracking.hibernate;

import com.liferay.portal.change.tracking.sql.CTSQLTransformer;
import com.liferay.portal.kernel.module.service.Snapshot;

import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * @author Preston Crary
 */
public class CTSQLInterceptor implements StatementInspector {

	@Override
	public String inspect(String sql) {
		if (_enabled) {
			CTSQLTransformer ctSQLTransformer = _ctSQLTransformerSnapshot.get();

			return ctSQLTransformer.transform(sql);
		}

		return sql;
	}

	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	private static final Snapshot<CTSQLTransformer> _ctSQLTransformerSnapshot =
		new Snapshot<>(CTSQLInterceptor.class, CTSQLTransformer.class);

	private boolean _enabled;

}