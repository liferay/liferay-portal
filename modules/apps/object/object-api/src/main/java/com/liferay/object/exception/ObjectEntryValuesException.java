/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.object.model.ObjectState;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Marco Leo
 */
public class ObjectEntryValuesException extends PortalException {

	public List<Object> getArguments() {
		return _arguments;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	public static class ExceedsIntegerSize extends ObjectEntryValuesException {

		public ExceedsIntegerSize(int maxLength, String objectFieldName) {
			super("Object entry value exceeds integer field allowed size");

			_maxLength = maxLength;
			_objectFieldName = objectFieldName;
		}

		public int getMaxLength() {
			return _maxLength;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final int _maxLength;
		private final String _objectFieldName;

	}

	public static class ExceedsLongMaxSize extends ObjectEntryValuesException {

		public ExceedsLongMaxSize(long maxValue, String objectFieldName) {
			super("Object entry value exceeds maximum long field allowed size");

			_maxValue = maxValue;
			_objectFieldName = objectFieldName;
		}

		public long getMaxValue() {
			return _maxValue;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final long _maxValue;
		private final String _objectFieldName;

	}

	public static class ExceedsLongMinSize extends ObjectEntryValuesException {

		public ExceedsLongMinSize(long minValue, String objectFieldName) {
			super(
				"Object entry value falls below minimum long field allowed " +
					"size");

			_minValue = minValue;
			_objectFieldName = objectFieldName;
		}

		public long getMinValue() {
			return _minValue;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final long _minValue;
		private final String _objectFieldName;

	}

	public static class ExceedsLongSize extends ObjectEntryValuesException {

		public ExceedsLongSize(int maxLength, String objectFieldName) {
			super("Object entry value exceeds long field allowed size");

			_maxLength = maxLength;
			_objectFieldName = objectFieldName;
		}

		public int getMaxLength() {
			return _maxLength;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final int _maxLength;
		private final String _objectFieldName;

	}

	public static class ExceedsMaxFileSize extends ObjectEntryValuesException {

		public ExceedsMaxFileSize(long maxFileSize, String objectFieldName) {
			super(
				Arrays.asList(maxFileSize, objectFieldName),
				String.format(
					"File exceeds the maximum permitted size of %s MB for " +
						"object field \"%s\"",
					maxFileSize, objectFieldName),
				"file-exceeds-the-maximum-permitted-size-of-x-mb-for-object-" +
					"field-x");

			_maxFileSize = maxFileSize;
			_objectFieldName = objectFieldName;
		}

		public long getMaxFileSize() {
			return _maxFileSize;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final long _maxFileSize;
		private final String _objectFieldName;

	}

	public static class ExceedsTextMaxLength
		extends ObjectEntryValuesException {

		public ExceedsTextMaxLength(int maxLength, String objectFieldName) {
			super(
				String.format(
					"Object entry value exceeds the maximum length of %s " +
						"characters for object field \"%s\"",
					maxLength, objectFieldName));

			_maxLength = maxLength;
			_objectFieldName = objectFieldName;
		}

		public int getMaxLength() {
			return _maxLength;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final int _maxLength;
		private final String _objectFieldName;

	}

	public static class InvalidFileExtension
		extends ObjectEntryValuesException {

		public InvalidFileExtension(
			String fileExtension, String objectFieldName) {

			super(
				StringBundler.concat(
					"The file extension \"", fileExtension,
					"\" is invalid for object field \"", objectFieldName,
					"\""));

			_fileExtension = fileExtension;
			_objectFieldName = objectFieldName;
		}

		public String getFileExtension() {
			return _fileExtension;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final String _fileExtension;
		private final String _objectFieldName;

	}

	public static class InvalidObjectField extends ObjectEntryValuesException {

		public InvalidObjectField(
			List<Object> arguments, String message, String messageKey) {

			super(arguments, message, messageKey);
		}

	}

	public static class InvalidObjectStateTransition
		extends ObjectEntryValuesException {

		public InvalidObjectStateTransition(
			String sourceObjectName, ObjectState sourceObjectState,
			String targetObjectName, ObjectState targetObjectState) {

			super(
				Arrays.asList(sourceObjectName, targetObjectName),
				String.format(
					"Object state ID %d cannot be transitioned to object " +
						"state ID %d",
					sourceObjectState.getObjectStateId(),
					targetObjectState.getObjectStateId()),
				"object-state-x-cannot-be-transitioned-to-object-state-x");

			_sourceObjectState = sourceObjectState;
			_targetObjectState = targetObjectState;
		}

		public ObjectState getSourceObjectState() {
			return _sourceObjectState;
		}

		public ObjectState getTargetObjectState() {
			return _targetObjectState;
		}

		private final ObjectState _sourceObjectState;
		private final ObjectState _targetObjectState;

	}

	public static class InvalidValue extends ObjectEntryValuesException {

		public InvalidValue(String objectFieldName) {
			super(
				String.format(
					"The value is invalid for object field \"%s\"",
					objectFieldName));

			_objectFieldName = objectFieldName;
		}

		public InvalidValue(String message, String objectFieldName) {
			super(message);

			_objectFieldName = objectFieldName;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final String _objectFieldName;

	}

	public static class ListTypeEntry extends ObjectEntryValuesException {

		public ListTypeEntry(String objectFieldName) {
			super(
				String.format(
					"Object field name \"%s\" is not mapped to a valid list " +
						"type entry",
					objectFieldName));

			_objectFieldName = objectFieldName;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private final String _objectFieldName;

	}

	public static class MustNotBeDuplicate extends ObjectEntryValuesException {

		public MustNotBeDuplicate(String value) {
			super("Duplicate value " + value);
		}

	}

	public static class NoSuchRelatedObjectEntry
		extends ObjectEntryValuesException {

		public NoSuchRelatedObjectEntry(String relationshipObjectFieldName) {
			super(
				Collections.singletonList(relationshipObjectFieldName),
				String.format(
					"The value for %s does not exist",
					relationshipObjectFieldName),
				"the-value-for-x-does-not-exist");
		}

	}

	public static class OneToOneConstraintViolation
		extends ObjectEntryValuesException {

		public OneToOneConstraintViolation(
			String columnName, long columnValue, String tableName) {

			super(
				String.format(
					"One to one constraint violation for %s.%s with value %s",
					tableName, columnName, columnValue));
		}

	}

	public static class Required extends ObjectEntryValuesException {

		public Required(String objectFieldName) {
			super(
				String.format(
					"No value was provided for required object field \"%s\"",
					objectFieldName));

			_objectFieldName = objectFieldName;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		private String _objectFieldName;

	}

	public static class RequiredLanguageId extends ObjectEntryValuesException {

		public RequiredLanguageId(String languageId, String objectFieldName) {
			super(
				String.format(
					"No value was provided for the language ID \"%s\" in the " +
						"required object field \"%s\".",
					languageId, objectFieldName));
		}

	}

	public static class UniqueValueConstraintViolation
		extends ObjectEntryValuesException {

		public UniqueValueConstraintViolation(
			String columnName, Serializable columnValue,
			String objectFieldLabel, String tableName, Throwable throwable) {

			super(
				Arrays.asList(objectFieldLabel),
				String.format(
					"Unique value constraint violation for %s.%s with value %s",
					tableName, columnName, columnValue),
				"the-x-is-already-in-use", throwable);
		}

	}

	private ObjectEntryValuesException(
		List<Object> arguments, String message, String messageKey) {

		super(message);

		_arguments = arguments;
		_messageKey = messageKey;
	}

	private ObjectEntryValuesException(
		List<Object> arguments, String message, String messageKey,
		Throwable throwable) {

		super(message, throwable);

		_arguments = arguments;
		_messageKey = messageKey;
	}

	private ObjectEntryValuesException(String message) {
		super(message);
	}

	private List<Object> _arguments;
	private String _messageKey;

}