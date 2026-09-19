/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.validator;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 */
public class DDMFormValidationException extends PortalException {

	public DDMFormValidationException() {
	}

	public DDMFormValidationException(String msg) {
		super(msg);
	}

	public DDMFormValidationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public DDMFormValidationException(Throwable throwable) {
		super(throwable);
	}

	public static class MustNotDuplicateFieldName
		extends DDMFormValidationException {

		public MustNotDuplicateFieldName(Set<String> duplicatedFieldNames) {
			super(
				String.format(
					"Field names %s were defined more than once",
					duplicatedFieldNames));

			_duplicatedFieldNames = duplicatedFieldNames;
		}

		/**
		 * @deprecated As of Athanasius (7.3.x), replaced by {@link
		 *             #MustNotDuplicateFieldName(Set)}
		 */
		@Deprecated
		public MustNotDuplicateFieldName(String fieldName) {
			this(SetUtil.fromArray(fieldName));
		}

		public Set<String> getDuplicatedFieldNames() {
			return _duplicatedFieldNames;
		}

		/**
		 * @deprecated As of Athanasius (7.3.x), replaced by {@link
		 *             #getDuplicatedFieldNames()}
		 */
		@Deprecated
		public String getFieldName() {
			String[] fieldNames = _duplicatedFieldNames.toArray(new String[0]);

			if (fieldNames.length == 0) {
				return null;
			}

			return fieldNames[0];
		}

		private final Set<String> _duplicatedFieldNames;

	}

	public static class MustNotDuplicateFieldReference
		extends DDMFormValidationException {

		public MustNotDuplicateFieldReference(
			Set<String> duplicatedFieldReferences) {

			super(
				String.format(
					"Field references %s were defined more than once",
					duplicatedFieldReferences));

			_duplicatedFieldReferences = duplicatedFieldReferences;
		}

		public Set<String> getDuplicatedFieldReferences() {
			return _duplicatedFieldReferences;
		}

		private final Set<String> _duplicatedFieldReferences;

	}

	public static class MustSetAvailableLocales
		extends DDMFormValidationException {

		public MustSetAvailableLocales() {
			super(
				"The available locales property was not set for the DDM form");
		}

	}

	public static class MustSetDefaultLocale
		extends DDMFormValidationException {

		public MustSetDefaultLocale() {
			super("The default locale property was not set for the DDM form");
		}

	}

	public static class MustSetDefaultLocaleAsAvailableLocale
		extends DDMFormValidationException {

		public MustSetDefaultLocaleAsAvailableLocale(Locale defaultLocale) {
			super(
				String.format(
					"The default locale %s must be set to a valid available " +
						"locale",
					defaultLocale));

			_defaultLocale = defaultLocale;
		}

		public Locale getDefaultLocale() {
			return _defaultLocale;
		}

		private final Locale _defaultLocale;

	}

	public static class MustSetFieldType extends DDMFormValidationException {

		public MustSetFieldType(String fieldName) {
			super(
				String.format(
					"The field type was never set for the DDM form field " +
						"with the field name %s",
					fieldName));

			_fieldName = fieldName;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private String _fieldName;

	}

	public static class MustSetFieldsForForm
		extends DDMFormValidationException {

		public MustSetFieldsForForm() {
			super("At least one field must be set");
		}

	}

	public static class MustSetOptionsForField
		extends DDMFormValidationException {

		public MustSetOptionsForField(String fieldName) {
			super(
				String.format(
					"At least one option must be set for field name %s",
					fieldName));

			_fieldName = fieldName;
		}

		public MustSetOptionsForField(String fieldLabel, String fieldName) {
			super(
				String.format(
					"At least one option must be set for field name %s",
					fieldName));

			_fieldLabel = fieldLabel;
			_fieldName = fieldName;
		}

		public String getFieldLabel() {
			return _fieldLabel;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private String _fieldLabel;
		private String _fieldName;

	}

	public static class MustSetValidAvailableLocalesForProperty
		extends DDMFormValidationException {

		public MustSetValidAvailableLocalesForProperty(
			String fieldName, String property) {

			super(
				String.format(
					"Invalid available locales set for the property '%s' of " +
						"field name %s",
					property, fieldName));

			_fieldName = fieldName;
			_property = property;
		}

		public String getFieldName() {
			return _fieldName;
		}

		public String getProperty() {
			return _property;
		}

		private String _fieldName;
		private String _property;

	}

	public static class MustSetValidCharactersForFieldName
		extends DDMFormValidationException {

		public MustSetValidCharactersForFieldName(String fieldName) {
			super(
				String.format(
					"Invalid characters entered for field name %s", fieldName));

			_fieldName = fieldName;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private String _fieldName;

	}

	public static class MustSetValidCharactersForFieldType
		extends DDMFormValidationException {

		public MustSetValidCharactersForFieldType(String fieldType) {
			super(
				String.format(
					"Invalid characters entered for field type %s", fieldType));

			_fieldType = fieldType;
		}

		public String getFieldType() {
			return _fieldType;
		}

		private final String _fieldType;

	}

	public static class MustSetValidDefaultLocaleForProperty
		extends DDMFormValidationException {

		public MustSetValidDefaultLocaleForProperty(
			String fieldName, String property) {

			super(
				String.format(
					"Invalid default locale set for the property '%s' of " +
						"field name %s",
					property, fieldName));

			_fieldName = fieldName;
			_property = property;
		}

		public String getFieldName() {
			return _fieldName;
		}

		public String getProperty() {
			return _property;
		}

		private String _fieldName;
		private String _property;

	}

	public static class MustSetValidFormRuleExpression
		extends DDMFormValidationException {

		public MustSetValidFormRuleExpression(
			String expressionType, String expression, Throwable throwable) {

			super(
				String.format(
					"Invalid form rule %s expression set: \"%s\"",
					expressionType, expression),
				throwable);

			_expression = expression;
		}

		public String getExpression() {
			return _expression;
		}

		private final String _expression;

	}

	public static class MustSetValidIndexType
		extends DDMFormValidationException {

		public MustSetValidIndexType(String fieldName) {
			super(
				String.format(
					"Invalid index type set for field name %s", fieldName));

			_fieldName = fieldName;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private String _fieldName;

	}

	public static class MustSetValidType extends DDMFormValidationException {

		public MustSetValidType(String fieldType) {
			super(
				String.format("Invalid type set for field type %s", fieldType));

			_fieldType = fieldType;
		}

		public String getFieldType() {
			return _fieldType;
		}

		private final String _fieldType;

	}

	public static class MustSetValidValidationExpression
		extends MustSetValidDDMFormFieldExpression {

		public MustSetValidValidationExpression(
			String fieldName, String expression) {

			super(fieldName, "validation", expression);
		}

		public String getValidationExpression() {
			return expression;
		}

		public String getValidationExpressionArgument() {
			Matcher matcher = _validationExpressionPattern.matcher(expression);

			if (matcher.find()) {
				return matcher.group(3);
			}

			return StringPool.BLANK;
		}

		private static final Pattern _validationExpressionPattern =
			Pattern.compile("(contains|match)\\((.+), \"(.+)\"\\)");

	}

	public static class MustSetValidVisibilityExpression
		extends MustSetValidDDMFormFieldExpression {

		public MustSetValidVisibilityExpression(
			String fieldName, String expression) {

			super(fieldName, "visibility", expression);
		}

		public String getVisibilityExpression() {
			return expression;
		}

	}

	private static class MustSetValidDDMFormFieldExpression
		extends DDMFormValidationException {

		public MustSetValidDDMFormFieldExpression(
			String fieldName, String expressionType, String expression) {

			super(
				String.format(
					"Invalid %s expression set for field %s: %s",
					expressionType, fieldName, expression));

			this.fieldName = fieldName;
			this.expression = expression;
		}

		public String getExpression() {
			return expression;
		}

		public String getFieldName() {
			return fieldName;
		}

		protected String expression;
		protected String fieldName;

	}

}