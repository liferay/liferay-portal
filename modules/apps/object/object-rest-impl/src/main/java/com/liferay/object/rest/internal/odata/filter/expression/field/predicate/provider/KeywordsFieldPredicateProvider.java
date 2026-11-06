/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.odata.filter.expression.field.predicate.provider;

import com.liferay.asset.kernel.model.AssetEntries_AssetTagsTable;
import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.asset.kernel.model.AssetTagTable;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.odata.filter.expression.field.predicate.provider.FieldPredicateProvider;
import com.liferay.object.rest.internal.util.BinaryExpressionConverterUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.filter.expression.BinaryExpression;

import java.util.List;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "field.predicate.provider.key=keywords",
	service = FieldPredicateProvider.class
)
public class KeywordsFieldPredicateProvider implements FieldPredicateProvider {

	@Override
	public Predicate getBinaryExpressionPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, long objectDefinitionId,
		BinaryExpression.Operation operation, Object right) {

		return _getKeywordsPredicate(
			objectDefinitionColumnSupplier, objectDefinitionId,
			BinaryExpressionConverterUtil.getExpressionPredicate(
				DSLFunctionFactoryUtil.lower(AssetTagTable.INSTANCE.name),
				operation, StringUtil.toLowerCase((String)right)));
	}

	@Override
	public Predicate getContainsPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, Object fieldValue) {

		return _getKeywordsPredicate(
			objectDefinitionColumnSupplier, 0,
			DSLFunctionFactoryUtil.lower(
				AssetTagTable.INSTANCE.name
			).like(
				StringUtil.toLowerCase(
					StringPool.PERCENT + fieldValue + StringPool.PERCENT)
			));
	}

	@Override
	public Predicate getInPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, List<Object> rights) {

		return _getKeywordsPredicate(
			objectDefinitionColumnSupplier, 0,
			DSLFunctionFactoryUtil.lower(
				AssetTagTable.INSTANCE.name
			).in(
				TransformUtil.transformToArray(
					rights,
					right -> StringUtil.toLowerCase(String.valueOf(right)),
					String.class)
			));
	}

	@Override
	public Predicate getIsNotEmptyPredicate(
		String fieldName,
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier) {

		Column<?, ?> column = objectDefinitionColumnSupplier.apply("id");

		return column.in(
			DSLQueryFactoryUtil.select(
				AssetEntryTable.INSTANCE.classPK
			).from(
				AssetEntryTable.INSTANCE
			).innerJoinON(
				AssetEntries_AssetTagsTable.INSTANCE,
				AssetEntryTable.INSTANCE.entryId.eq(
					AssetEntries_AssetTagsTable.INSTANCE.entryId)
			).innerJoinON(
				AssetTagTable.INSTANCE,
				AssetTagTable.INSTANCE.tagId.eq(
					AssetEntries_AssetTagsTable.INSTANCE.tagId)
			));
	}

	@Override
	public Predicate getStartsWithPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, Object fieldValue) {

		return _getKeywordsPredicate(
			objectDefinitionColumnSupplier, 0,
			DSLFunctionFactoryUtil.lower(
				AssetTagTable.INSTANCE.name
			).like(
				StringUtil.toLowerCase(fieldValue + StringPool.PERCENT)
			));
	}

	private Predicate _getClassNameIdPredicate(long objectDefinitionId) {
		if (objectDefinitionId <= 0) {
			return null;
		}

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

	private Predicate _getKeywordsPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		long objectDefinitionId, Expression<Boolean> valueExpression) {

		Column<?, ?> column = objectDefinitionColumnSupplier.apply("id");

		return column.in(
			DSLQueryFactoryUtil.select(
				AssetEntryTable.INSTANCE.classPK
			).from(
				AssetEntryTable.INSTANCE
			).innerJoinON(
				AssetEntries_AssetTagsTable.INSTANCE,
				AssetEntryTable.INSTANCE.entryId.eq(
					AssetEntries_AssetTagsTable.INSTANCE.entryId
				).and(
					_getClassNameIdPredicate(objectDefinitionId)
				)
			).innerJoinON(
				AssetTagTable.INSTANCE,
				AssetTagTable.INSTANCE.tagId.eq(
					AssetEntries_AssetTagsTable.INSTANCE.tagId
				).and(
					valueExpression
				)
			));
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}