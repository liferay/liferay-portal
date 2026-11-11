/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.odata.filter.expression.field.predicate.provider;

import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRelTable;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.odata.filter.expression.field.predicate.provider.FieldPredicateProvider;
import com.liferay.object.rest.internal.util.BinaryExpressionConverterUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.odata.filter.expression.BinaryExpression;

import java.util.List;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "field.predicate.provider.key=taxonomyCategoryIds",
	service = FieldPredicateProvider.class
)
public class TaxonomyCategoryIdsFieldPredicateProvider
	implements FieldPredicateProvider {

	@Override
	public Predicate getBinaryExpressionPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, long objectDefinitionId,
		BinaryExpression.Operation operation, Object right) {

		return _getTaxonomyCategoryIdsPredicate(
			objectDefinitionColumnSupplier, objectDefinitionId,
			BinaryExpressionConverterUtil.getExpressionPredicate(
				AssetEntryAssetCategoryRelTable.INSTANCE.assetCategoryId,
				operation, (Long)right));
	}

	@Override
	public Predicate getContainsPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, long objectDefinitionId, Object fieldValue) {

		throw new UnsupportedOperationException(
			"Unsupported method getContainsPredicate for taxonomyCategoryIds");
	}

	@Override
	public Predicate getInPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, long objectDefinitionId, List<Object> rights) {

		return _getTaxonomyCategoryIdsPredicate(
			objectDefinitionColumnSupplier, objectDefinitionId,
			AssetEntryAssetCategoryRelTable.INSTANCE.assetCategoryId.in(
				TransformUtil.transformToArray(
					rights, assetCategoryId -> (Long)assetCategoryId,
					Long.class)));
	}

	@Override
	public Predicate getIsNotEmptyPredicate(
		String fieldName, long objectDefinitionId,
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier) {

		return _getTaxonomyCategoryIdsPredicate(
			objectDefinitionColumnSupplier, objectDefinitionId, null);
	}

	@Override
	public Predicate getStartsWithPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, long objectDefinitionId, Object fieldValue) {

		throw new UnsupportedOperationException(
			"Unsupported method getStartsWithPredicate for " +
				"taxonomyCategoryIds");
	}

	private Predicate _getClassNameIdPredicate(long objectDefinitionId) {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId);

		if (objectDefinition == null) {
			return null;
		}

		return AssetEntryTable.INSTANCE.classNameId.eq(
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()));
	}

	private Predicate _getTaxonomyCategoryIdsPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		long objectDefinitionId, Expression<Boolean> valueExpression) {

		Column<?, ?> column = objectDefinitionColumnSupplier.apply("id");

		return column.in(
			DSLQueryFactoryUtil.select(
				AssetEntryTable.INSTANCE.classPK
			).from(
				AssetEntryTable.INSTANCE
			).innerJoinON(
				AssetEntryAssetCategoryRelTable.INSTANCE,
				AssetEntryTable.INSTANCE.entryId.eq(
					AssetEntryAssetCategoryRelTable.INSTANCE.assetEntryId
				).and(
					valueExpression
				).and(
					_getClassNameIdPredicate(objectDefinitionId)
				)
			));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}