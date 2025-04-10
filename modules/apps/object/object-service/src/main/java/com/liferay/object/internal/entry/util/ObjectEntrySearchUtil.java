/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.entry.util;

import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.Locale;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntrySearchUtil {

	public static String getLanguageId() throws PortalException {
		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		if (locale == null) {
			locale = LocaleThreadLocal.getSiteDefaultLocale();
		}

		if (locale == null) {
			User user = GuestOrUserUtil.getGuestOrUser(
				CompanyThreadLocal.getCompanyId());

			locale = user.getLocale();
		}

		return LocaleUtil.toLanguageId(locale);
	}

	public static Predicate getLeftJoinLocalizationTablePredicate(
			DynamicObjectDefinitionLocalizationTable
				dynamicObjectDefinitionLocalizationTable,
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable)
		throws PortalException {

		if (dynamicObjectDefinitionLocalizationTable == null) {
			return null;
		}

		return dynamicObjectDefinitionLocalizationTable.getForeignKeyColumn(
		).eq(
			dynamicObjectDefinitionTable.getPrimaryKeyColumn()
		).and(
			dynamicObjectDefinitionLocalizationTable.getLanguageIdColumn(
			).eq(
				getLanguageId()
			)
		);
	}

	public static Predicate getObjectFieldPredicate(
		String businessType, Column<?, ?> column, String dbType,
		String search) {

		if (column == null) {
			return null;
		}

		Column<?, Object> objectColumn = (Column<?, Object>)column;

		if (businessType.equals(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) &&
			!Validator.isNumber(search)) {

			return objectColumn.in(
				DSLQueryFactoryUtil.select(
					DLFileEntryTable.INSTANCE.fileEntryId
				).from(
					DLFileEntryTable.INSTANCE
				).where(
					DLFileEntryTable.INSTANCE.title.like(
						StringBundler.concat(
							StringPool.PERCENT, search, StringPool.PERCENT))
				));
		}

		if (dbType.equals(ObjectFieldConstants.DB_TYPE_BIG_DECIMAL) ||
			dbType.equals(ObjectFieldConstants.DB_TYPE_DOUBLE)) {

			BigDecimal searchBigDecimal = BigDecimal.valueOf(
				GetterUtil.getDouble(search));

			if (searchBigDecimal.compareTo(BigDecimal.ZERO) != 0) {
				return objectColumn.eq(searchBigDecimal);
			}
		}
		else if (dbType.equals(ObjectFieldConstants.DB_TYPE_CLOB) ||
				 dbType.equals(ObjectFieldConstants.DB_TYPE_STRING)) {

			return DSLFunctionFactoryUtil.lower(
				(Column<?, String>)column
			).like(
				StringUtil.quote(StringUtil.toLowerCase(search), "%")
			);
		}
		else if (dbType.equals(ObjectFieldConstants.DB_TYPE_INTEGER) ||
				 dbType.equals(ObjectFieldConstants.DB_TYPE_LONG)) {

			long searchLong = GetterUtil.getLong(search);

			if (searchLong != 0L) {
				return objectColumn.eq(searchLong);
			}
		}

		return null;
	}

	public static Column<?, Long> getPrimaryKeyColumn(
		String pkObjectFieldDBColumnName, Table<?> table) {

		Column<?, Long> primaryKeyColumn = (Column<?, Long>)table.getColumn(
			pkObjectFieldDBColumnName);

		if (primaryKeyColumn == null) {
			primaryKeyColumn = ObjectEntryTable.INSTANCE.objectEntryId;
		}

		return primaryKeyColumn;
	}

	public static Predicate getRelatedModelsPredicate(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService, String search,
		Table<?> table) {

		if ((objectDefinition == null) || Validator.isNull(search) ||
			(table == null)) {

			return null;
		}

		ObjectField titleObjectField = objectFieldLocalService.fetchObjectField(
			objectDefinition.getTitleObjectFieldId());

		if (titleObjectField == null) {
			titleObjectField = objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), "id");
		}

		Predicate objectFieldPredicate = getObjectFieldPredicate(
			titleObjectField.getBusinessType(),
			objectFieldLocalService.getColumn(
				objectDefinition.getObjectDefinitionId(),
				titleObjectField.getName()),
			titleObjectField.getDBType(), search);

		long searchLong = GetterUtil.getLong(search);

		if (searchLong == 0) {
			return objectFieldPredicate;
		}

		Column<?, Long> primaryKeyColumn = getPrimaryKeyColumn(
			objectDefinition.getPKObjectFieldDBColumnName(), table);

		Predicate primaryKeyPredicate = primaryKeyColumn.eq(searchLong);

		if (objectFieldPredicate == null) {
			return primaryKeyPredicate;
		}

		return objectFieldPredicate.or(
			primaryKeyPredicate
		).withParentheses();
	}

	public static Predicate getUniqueCompositeKeyObjectFieldPredicate(
		Column<?, Object> column, String dbType, Object value) {

		if (dbType.equals(ObjectFieldConstants.DB_TYPE_INTEGER) ||
			dbType.equals(ObjectFieldConstants.DB_TYPE_LONG)) {

			return column.eq(GetterUtil.getLong(value));
		}
		else if (dbType.equals(ObjectFieldConstants.DB_TYPE_STRING)) {
			if (value == null) {
				return column.isNull();
			}

			return column.eq(String.valueOf(value));
		}

		return null;
	}

}