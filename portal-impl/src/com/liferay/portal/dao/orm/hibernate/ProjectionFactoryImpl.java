/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.ProjectionFactory;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.Type;

/**
 * @author Brian Wing Shun Chan
 */
public class ProjectionFactoryImpl implements ProjectionFactory {

	@Override
	public Projection alias(Projection projection, String alias) {
		return null;
	}

	@Override
	public Projection avg(String propertyName) {
		return null;
	}

	@Override
	public Projection count(String propertyName) {
		return null;
	}

	@Override
	public Projection countDistinct(String propertyName) {
		return null;
	}

	@Override
	public Projection distinct(Projection projection) {
		return null;
	}

	@Override
	public Projection groupProperty(String propertyName) {
		return null;
	}

	@Override
	public Projection max(String propertyName) {
		return null;
	}

	@Override
	public Projection min(String propertyName) {
		return null;
	}

	@Override
	public ProjectionList projectionList() {
		return null;
	}

	@Override
	public Projection property(String propertyName) {
		return null;
	}

	@Override
	public Projection rowCount() {
		return null;
	}

	@Override
	public Projection sqlGroupProjection(
		String sql, String groupBy, String[] columnAliases, Type[] types) {

		return null;
	}

	@Override
	public Projection sqlProjection(
		String sql, String[] columnAliases, Type[] types) {

		return null;
	}

	@Override
	public Projection sum(String propertyName) {
		return null;
	}

}