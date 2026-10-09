/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.query;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.petra.sql.dsl.query.sort.OrderByInfo;
import com.liferay.petra.sql.dsl.spi.expression.DefaultAlias;
import com.liferay.petra.sql.dsl.spi.query.sort.DefaultOrderByExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Preston Crary
 */
public interface DefaultOrderByStep extends DefaultLimitStep, OrderByStep {

	@Override
	public default LimitStep orderBy(OrderByExpression... orderByExpressions) {
		if ((orderByExpressions == null) || (orderByExpressions.length == 0)) {
			return this;
		}

		return new OrderBy(this, orderByExpressions);
	}

	@Override
	public default LimitStep orderBy(Table<?> table, OrderByInfo orderByInfo) {
		if (orderByInfo == null) {
			return this;
		}

		String[] orderByFields = orderByInfo.getOrderByFields();

		List<OrderByExpression> orderByExpressions = new ArrayList<>(
			orderByFields.length);

		for (String field : orderByFields) {
			Column<?, ?> column = table.getColumn(field);

			if (column == null) {
				continue;
			}

			if (field.equals(column.getName())) {
				orderByExpressions.add(
					new DefaultOrderByExpression(
						column, orderByInfo.isAscending(field)));
			}
			else {
				orderByExpressions.add(
					new DefaultOrderByExpression(
						new DefaultAlias<>(column, field),
						orderByInfo.isAscending(field)));
			}
		}

		if (orderByExpressions.isEmpty()) {
			return this;
		}

		return new OrderBy(
			this, orderByExpressions.toArray(new OrderByExpression[0]));
	}

}