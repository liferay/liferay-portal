/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.handler;

import com.liferay.info.exception.InfoFormValidationException;
import com.liferay.info.exception.NoSuchFormVariationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.object.exception.ObjectEntryCountException;
import com.liferay.object.exception.ObjectEntryExpirationDateException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.ObjectFieldSettingLocalServiceUtil;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ObjectEntryInfoItemExceptionRequestHandler {

	public static void handleInfoFormException(
			Exception exception, long groupId,
			InfoItemFormProvider<?> infoItemFormProvider,
			ObjectDefinition objectDefinition)
		throws InfoFormException {

		if (exception instanceof ModelListenerException) {
			ModelListenerException modelListenerException =
				(ModelListenerException)exception;

			Throwable throwable = modelListenerException.getCause();

			if (throwable instanceof ObjectValidationRuleEngineException) {
				ObjectValidationRuleEngineException
					objectValidationRuleEngineException =
						(ObjectValidationRuleEngineException)throwable;

				List<ObjectValidationRuleResult> objectValidationRuleResults =
					objectValidationRuleEngineException.
						getObjectValidationRuleResults();

				if (ListUtil.isEmpty(objectValidationRuleResults)) {
					throw new InfoFormException();
				}

				InfoFormValidationException.RuleValidation
					infoFormValidationExceptionRuleValidation =
						new InfoFormValidationException.RuleValidation(
							throwable.getLocalizedMessage());

				for (ObjectValidationRuleResult objectValidationRuleResult :
						objectValidationRuleResults) {

					infoFormValidationExceptionRuleValidation.
						addCustomValidation(
							_getInfoFieldUniqueId(
								groupId, infoItemFormProvider, objectDefinition,
								objectValidationRuleResult.
									getObjectFieldName()),
							objectValidationRuleResult.getErrorMessage());
				}

				throw infoFormValidationExceptionRuleValidation;
			}

			throw new InfoFormException();
		}

		if (exception instanceof ObjectEntryCountException) {
			ObjectEntryCountException objectEntryCountException =
				(ObjectEntryCountException)exception;

			throw new InfoFormValidationException.ExceedsMaxEntries(
				objectEntryCountException.getObjectDefinitionLabel(),
				objectEntryCountException.getMessageKey());
		}

		if (exception instanceof ObjectEntryExpirationDateException) {
			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				"expirationDate");

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			ObjectEntryExpirationDateException
				objectEntryExpirationDateException =
					(ObjectEntryExpirationDateException)exception;

			throw new InfoFormValidationException.InvalidExpirationDate(
				infoFieldUniqueId,
				objectEntryExpirationDateException.getMessageKey());
		}

		if (exception instanceof
				ObjectEntryValuesException.ExceedsIntegerSize) {

			ObjectEntryValuesException.ExceedsIntegerSize
				objectEntryValuesException =
					(ObjectEntryValuesException.ExceedsIntegerSize)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.ExceedsMaxLength(
				infoFieldUniqueId, objectEntryValuesException.getMaxLength());
		}

		if (exception instanceof
				ObjectEntryValuesException.ExceedsLongMaxSize) {

			ObjectEntryValuesException.ExceedsLongMaxSize
				objectEntryValuesException =
					(ObjectEntryValuesException.ExceedsLongMaxSize)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.ExceedsMaxValue(
				infoFieldUniqueId, objectEntryValuesException.getMaxValue());
		}

		if (exception instanceof
				ObjectEntryValuesException.ExceedsLongMinSize) {

			ObjectEntryValuesException.ExceedsLongMinSize
				objectEntryValuesException =
					(ObjectEntryValuesException.ExceedsLongMinSize)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.ExceedsMinValue(
				infoFieldUniqueId, objectEntryValuesException.getMinValue());
		}

		if (exception instanceof ObjectEntryValuesException.ExceedsLongSize) {
			ObjectEntryValuesException.ExceedsLongSize
				objectEntryValuesException =
					(ObjectEntryValuesException.ExceedsLongSize)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.ExceedsMaxLength(
				infoFieldUniqueId, objectEntryValuesException.getMaxLength());
		}

		if (exception instanceof
				ObjectEntryValuesException.ExceedsMaxFileSize) {

			ObjectEntryValuesException.ExceedsMaxFileSize
				objectEntryValuesException =
					(ObjectEntryValuesException.ExceedsMaxFileSize)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.FileSize(
				infoFieldUniqueId,
				objectEntryValuesException.getMaxFileSize() + " MB");
		}

		if (exception instanceof
				ObjectEntryValuesException.ExceedsTextMaxLength) {

			ObjectEntryValuesException.ExceedsTextMaxLength
				objectEntryValuesException =
					(ObjectEntryValuesException.ExceedsTextMaxLength)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.ExceedsMaxLength(
				infoFieldUniqueId, objectEntryValuesException.getMaxLength());
		}

		if (exception instanceof
				ObjectEntryValuesException.InvalidFileExtension) {

			ObjectEntryValuesException.InvalidFileExtension
				objectEntryValuesException =
					(ObjectEntryValuesException.InvalidFileExtension)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.InvalidFileExtension(
				infoFieldUniqueId,
				_getAcceptedFileExtensions(
					objectDefinition.getObjectDefinitionId(),
					objectEntryValuesException.getObjectFieldName()));
		}

		if (exception instanceof ObjectEntryValuesException.ListTypeEntry) {
			ObjectEntryValuesException.ListTypeEntry
				objectEntryValuesException =
					(ObjectEntryValuesException.ListTypeEntry)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.InvalidInfoFieldValue(
				infoFieldUniqueId);
		}

		if (exception instanceof ObjectEntryValuesException.Required) {
			ObjectEntryValuesException.Required objectEntryValuesException =
				(ObjectEntryValuesException.Required)exception;

			String infoFieldUniqueId = _getInfoFieldUniqueId(
				groupId, infoItemFormProvider, objectDefinition,
				objectEntryValuesException.getObjectFieldName());

			if (infoFieldUniqueId == null) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.RequiredInfoField(
				infoFieldUniqueId);
		}

		if (exception instanceof
				ObjectEntryValuesException.UniqueValueConstraintViolation) {

			ObjectEntryValuesException.UniqueValueConstraintViolation
				objectEntryValuesException =
					(ObjectEntryValuesException.UniqueValueConstraintViolation)
						exception;

			List<Object> arguments = objectEntryValuesException.getArguments();

			if (ListUtil.isEmpty(arguments) || (arguments.size() > 1)) {
				throw new InfoFormException();
			}

			throw new InfoFormValidationException.
				UniqueValueConstraintViolation(
					String.valueOf(arguments.get(0)));
		}

		if (_log.isDebugEnabled()) {
			_log.debug(exception);
		}

		throw new InfoFormException();
	}

	private static String _getAcceptedFileExtensions(
		long objectDefinitionId, String objectFieldName) {

		ObjectField objectField = ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinitionId, objectFieldName);

		ObjectFieldSetting objectFieldSetting =
			ObjectFieldSettingLocalServiceUtil.fetchObjectFieldSetting(
				objectField.getObjectFieldId(), "acceptedFileExtensions");

		if (objectFieldSetting == null) {
			return StringPool.BLANK;
		}

		return objectFieldSetting.getValue();
	}

	private static String _getInfoFieldUniqueId(
		long groupId, InfoItemFormProvider<?> infoItemFormProvider,
		ObjectDefinition objectDefinition, String objectFieldName) {

		try {
			InfoForm infoForm = infoItemFormProvider.getInfoForm(
				String.valueOf(objectDefinition.getObjectDefinitionId()),
				groupId);

			InfoField<?> infoField = infoForm.getInfoField(objectFieldName);

			if (infoField != null) {
				return infoField.getUniqueId();
			}
		}
		catch (NoSuchFormVariationException noSuchFormVariationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFormVariationException);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryInfoItemExceptionRequestHandler.class);

}