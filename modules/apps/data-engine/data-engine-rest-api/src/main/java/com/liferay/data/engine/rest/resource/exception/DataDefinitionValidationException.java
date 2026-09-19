/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.exception;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Leonardo Barros
 */
public class DataDefinitionValidationException extends PortalException {

	public DataDefinitionValidationException() {
	}

	public DataDefinitionValidationException(String msg) {
		super(msg);
	}

	public DataDefinitionValidationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public DataDefinitionValidationException(Throwable throwable) {
		super(throwable);
	}

	public static class MustNotDuplicateFieldName
		extends DataDefinitionValidationException {

		public MustNotDuplicateFieldName(Set<String> duplicatedFieldNames) {
			super(
				String.format(
					"Field names %s were defined more than once",
					duplicatedFieldNames));

			_duplicatedFieldNames = duplicatedFieldNames;
		}

		public Set<String> getDuplicatedFieldNames() {
			return _duplicatedFieldNames;
		}

		private final Set<String> _duplicatedFieldNames;

	}

	public static class MustNotDuplicateFieldReference
		extends DataDefinitionValidationException {

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

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public static class MustNotRemoveNativeField
		extends DataDefinitionValidationException {

		public MustNotRemoveNativeField(Set<String> removedFieldNames) {
			super(
				String.format(
					"Native fields %s were removed", removedFieldNames));

			_removedFieldNames = removedFieldNames;
		}

		public Set<String> getRemovedFieldNames() {
			return _removedFieldNames;
		}

		private final Set<String> _removedFieldNames;

	}

	public static class MustSetAvailableLocales
		extends DataDefinitionValidationException {

		public MustSetAvailableLocales() {
			super(
				"The available locales property was not set for the data " +
					"definition");
		}

	}

	public static class MustSetDefaultLocale
		extends DataDefinitionValidationException {

		public MustSetDefaultLocale() {
			super(
				"The default locale property was not set for the data " +
					"definition");
		}

	}

	public static class MustSetDefaultLocaleAsAvailableLocale
		extends DataDefinitionValidationException {

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

	public static class MustSetFieldType
		extends DataDefinitionValidationException {

		public MustSetFieldType(String fieldName) {
			super(
				String.format(
					"The field type was never set for the data definition " +
						"field with the field name %s",
					fieldName));

			_fieldName = fieldName;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private final String _fieldName;

	}

	public static class MustSetFields
		extends DataDefinitionValidationException {

		public MustSetFields() {
			super("There are no fields for the data definition");
		}

	}

	public static class MustSetOptionsForField
		extends DataDefinitionValidationException {

		/**
		 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
		 * 			#MustSetOptionsForField(String, String)}
		 */
		@Deprecated
		public MustSetOptionsForField(String fieldName) {
			super(
				String.format(
					"At least one option must be set for field %s", fieldName));

			_fieldName = fieldName;

			_fieldLabel = fieldName;
		}

		public MustSetOptionsForField(String fieldLabel, String fieldName) {
			super(
				String.format(
					"At least one option must be set for field %s",
					fieldLabel));

			_fieldLabel = fieldLabel;
			_fieldName = fieldName;
		}

		public String getFieldLabel() {
			return _fieldLabel;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private final String _fieldLabel;
		private final String _fieldName;

	}

	public static class MustSetValidAvailableLocalesForProperty
		extends DataDefinitionValidationException {

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

		private final String _fieldName;
		private final String _property;

	}

	public static class MustSetValidCharactersForFieldName
		extends DataDefinitionValidationException {

		public MustSetValidCharactersForFieldName(String fieldName) {
			super(
				String.format(
					"Invalid characters entered for field name %s", fieldName));

			_fieldName = fieldName;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private final String _fieldName;

	}

	public static class MustSetValidCharactersForFieldType
		extends DataDefinitionValidationException {

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

	public static class MustSetValidContentType
		extends DataDefinitionValidationException {

		public MustSetValidContentType(String contentType) {
			super(String.format("Invalid content type %s", contentType));

			_contentType = contentType;
		}

		public String getContentType() {
			return _contentType;
		}

		private final String _contentType;

	}

	public static class MustSetValidDefaultLocaleForProperty
		extends DataDefinitionValidationException {

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

		private final String _fieldName;
		private final String _property;

	}

	public static class MustSetValidIndexType
		extends DataDefinitionValidationException {

		public MustSetValidIndexType(String fieldName) {
			super(
				String.format(
					"Invalid index type set for field %s", fieldName));

			_fieldName = fieldName;
		}

		public String getFieldName() {
			return _fieldName;
		}

		private final String _fieldName;

	}

	public static class MustSetValidName
		extends DataDefinitionValidationException {

		public MustSetValidName(String message) {
			super(message);
		}

	}

	public static class MustSetValidRuleExpression
		extends DataDefinitionValidationException {

		public MustSetValidRuleExpression(String expression, String message) {
			super(message);

			_expression = expression;
		}

		public String getExpression() {
			return _expression;
		}

		private final String _expression;

	}

	public static class MustSetValidType
		extends DataDefinitionValidationException {

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
		extends MustSetValidFieldExpression {

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
		extends MustSetValidFieldExpression {

		public MustSetValidVisibilityExpression(
			String fieldName, String expression) {

			super(fieldName, "visibility", expression);
		}

		public String getVisibilityExpression() {
			return expression;
		}

	}

	private static class MustSetValidFieldExpression
		extends DataDefinitionValidationException {

		public MustSetValidFieldExpression(
			String fieldName, String expression, String message) {

			super(message);

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