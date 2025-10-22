/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Order;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Session;

import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DynamicQueryImpl implements DynamicQuery {

	@Override
	public DynamicQuery add(Criterion criterion) {
		return this;
	}

	@Override
	public DynamicQuery addOrder(Order order) {
		return this;
	}

	@Override
	public void compile(Session session) {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List list() {
		return list(true);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List list(boolean unmodifiable) {
		return Collections.emptyList();
	}

	@Override
	public void setLimit(int start, int end) {
	}

	@Override
	public DynamicQuery setProjection(Projection projection) {
		return setProjection(projection, true);
	}

	@Override
	public DynamicQuery setProjection(
		Projection projection, boolean useColumnAlias) {

		return this;
	}

}