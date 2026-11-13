/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.odata.filter.expression.field.predicate.provider;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;

import java.util.List;
import java.util.function.Function;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Alejandro Tardín
 */
@ProviderType
public interface FieldPredicateProvider {

	public Predicate getBinaryExpressionPredicate(
			Object left,
			Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
			long objectDefinitionId, BinaryExpression.Operation operation,
			Object right)
		throws ExpressionVisitException;

	public Predicate getContainsPredicate(
			String fieldName, Object fieldValue,
			Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
			long objectDefinitionId)
		throws ExpressionVisitException;

	public Predicate getInPredicate(
			Object left,
			Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
			long objectDefinitionId, List<Object> rights)
		throws ExpressionVisitException;

	public Predicate getIsNotEmptyPredicate(
			String fieldName,
			Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
			long objectDefinitionId)
		throws ExpressionVisitException;

	public Predicate getStartsWithPredicate(
			String fieldName, Object fieldValue,
			Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
			long objectDefinitionId)
		throws ExpressionVisitException;

}