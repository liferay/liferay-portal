/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.filter.factory;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.filter.parser.ObjectDefinitionFilterParser;
import com.liferay.object.rest.odata.entity.v1_0.provider.EntityModelProvider;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;

import jakarta.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Paulo Albuquerque
 */
public abstract class BaseFilterFactory<T> implements FilterFactory<T> {

	@Override
	public final T create(
		EntityModel entityModel, String filterString,
		ObjectDefinition objectDefinition) {

		if (Validator.isNull(filterString)) {
			return null;
		}

		return _create(
			entityModel,
			objectDefinitionFilterParser.parse(
				entityModel, filterString, objectDefinition),
			objectDefinition);
	}

	@Override
	public final T create(
		Expression filterExpression, ObjectDefinition objectDefinition) {

		if (filterExpression == null) {
			return null;
		}

		return _create(
			getEntityModel(objectDefinition), filterExpression,
			objectDefinition);
	}

	@Override
	public final T create(
		String filterString, ObjectDefinition objectDefinition) {

		try {
			return create(
				getEntityModel(objectDefinition), filterString,
				objectDefinition);
		}
		catch (InvalidFilterException invalidFilterException) {
			throw invalidFilterException;
		}
		catch (Exception exception) {
			throw new ServerErrorException(500, exception);
		}
	}

	public abstract ExpressionVisitor<?> getExpressionVisitor(
		EntityModel entityModel, ObjectDefinition objectDefinition);

	protected EntityModel getEntityModel(ObjectDefinition objectDefinition) {
		return entityModelProvider.getEntityModel(objectDefinition);
	}

	@Reference
	protected EntityModelProvider entityModelProvider;

	@Reference
	protected ObjectDefinitionFilterParser objectDefinitionFilterParser;

	@Reference
	protected ObjectFieldLocalService objectFieldLocalService;

	private T _create(
		EntityModel entityModel, Expression filterExpression,
		ObjectDefinition objectDefinition) {

		try {
			return (T)filterExpression.accept(
				getExpressionVisitor(entityModel, objectDefinition));
		}
		catch (ExpressionVisitException expressionVisitException) {
			throw new InvalidFilterException(
				expressionVisitException.getMessage(),
				expressionVisitException);
		}
		catch (InvalidFilterException invalidFilterException) {
			throw invalidFilterException;
		}
		catch (Exception exception) {
			throw new ServerErrorException(500, exception);
		}
	}

}