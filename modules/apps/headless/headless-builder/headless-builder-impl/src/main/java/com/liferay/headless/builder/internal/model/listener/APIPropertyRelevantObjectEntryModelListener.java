/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.model.listener;

import com.liferay.headless.builder.internal.helper.ObjectEntryHelper;
import com.liferay.headless.builder.internal.helper.ValidationHelper;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.listener.RelevantObjectEntryModelListener;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio Jiménez del Coso
 */
@Component(service = RelevantObjectEntryModelListener.class)
public class APIPropertyRelevantObjectEntryModelListener
	extends BaseModelListener<ObjectEntry>
	implements RelevantObjectEntryModelListener {

	@Override
	public String getObjectDefinitionExternalReferenceCode() {
		return "L_API_PROPERTY";
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		_validate(objectEntry);
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		_validate(objectEntry);
	}

	private boolean _isValidAPIProperty(
			long apiSchemaId, String objectFieldExternalReferenceCode,
			String objectRelationshipName)
		throws Exception {

		ObjectEntry apiSchemaObjectEntry =
			_objectEntryLocalService.getObjectEntry(apiSchemaId);

		Map<String, Serializable> apiSchemaValues =
			apiSchemaObjectEntry.getValues();

		String mainObjectDefinitionERC = (String)apiSchemaValues.get(
			"mainObjectDefinitionERC");

		if (mainObjectDefinitionERC == null) {
			return false;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					mainObjectDefinitionERC,
					apiSchemaObjectEntry.getCompanyId());

		if ((objectRelationshipName != null) &&
			!StringUtil.equals(objectRelationshipName, "")) {

			objectDefinition = _objectEntryHelper.getPropertyObjectDefinition(
				objectDefinition,
				ListUtil.fromArray(objectRelationshipName.split(",")));

			if (!ValidationHelper.isSupported(objectDefinition)) {
				throw new ObjectEntryValuesException.InvalidObjectField(
					null,
					"An API property must belong to a modifiable object " +
						"definition",
					"an-api-property-must-belong-to-a-modifiable-object-" +
						"definition");
			}
		}

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectFieldExternalReferenceCode,
			objectDefinition.getObjectDefinitionId());

		if (objectField == null) {
			return false;
		}

		return true;
	}

	private void _validate(ObjectEntry objectEntry) {
		try {
			Map<String, Serializable> values = objectEntry.getValues();

			long apiSchemaId = GetterUtil.getLong(
				values.get("r_apiSchemaToAPIProperties_l_apiSchemaId"));

			if (!_validationHelper.isValidObjectEntry(
					"L_API_SCHEMA", apiSchemaId)) {

				throw new ObjectEntryValuesException.InvalidObjectField(
					null, "An API property must be related to an API schema",
					"an-api-property-must-be-related-to-an-api-schema");
			}

			String objectFieldERC = (String)values.get("objectFieldERC");
			String objectRelationshipNames = (String)values.get(
				"objectRelationshipNames");

			String type = (String)values.get("type");

			if (Objects.equals(type, "record")) {
				if (!FeatureFlagManagerUtil.isEnabled("LPD-10964")) {
					throw new UnsupportedOperationException(
						"Record is not supported");
				}

				if (!Validator.isBlank(objectFieldERC)) {
					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"A record API property cannot have an object field " +
							"external reference code",
						"a-record-api-property-cannot-have-an-object-field-" +
							"external-reference-code");
				}

				if (!Validator.isBlank(objectRelationshipNames)) {
					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"A record API property cannot have an object " +
							"relationship names value",
						"a-record-api-property-cannot-have-an-object-" +
							"relationship-names-value");
				}
			}
			else {
				if (Validator.isNull(objectFieldERC)) {
					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"A field API property cannot have an empty object " +
							"field external reference code",
						"a-field-api-property-cannot-have-an-empty-object-" +
							"field-external-reference-code");
				}

				if (!_isValidAPIProperty(
						apiSchemaId, objectFieldERC, objectRelationshipNames)) {

					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"An API property must be related to an existing " +
							"object field",
						"an-api-property-must-be-related-to-an-existing-" +
							"object-field");
				}
			}

			long parentAPIPropertyId = GetterUtil.getLong(
				values.get("r_apiPropertyToAPIProperties_l_apiPropertyId"));

			if (parentAPIPropertyId != 0) {
				if (!_validationHelper.isValidObjectEntry(
						"L_API_PROPERTY", parentAPIPropertyId)) {

					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"An API property must be related to an API property",
						"an-api-property-must-be-related-to-an-api-property");
				}

				ObjectEntry relatedAPIPropertyObjectEntry =
					_objectEntryLocalService.getObjectEntry(
						parentAPIPropertyId);

				Map<String, Serializable> apiPropertyValues =
					relatedAPIPropertyObjectEntry.getValues();

				if (!Objects.equals(
						apiPropertyValues.get(
							"r_apiSchemaToAPIProperties_l_apiSchemaId"),
						apiSchemaId)) {

					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"A related API property must belong to the same API " +
							"schema",
						"a-related-api-property-must-belong-to-the-same-api-" +
							"schema");
				}

				if (Objects.equals(values.get("type"), "field") &&
					Objects.equals(apiPropertyValues.get("type"), "field")) {

					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"A field API property must be related to a record " +
							"API property",
						"a-field-api-property-must-be-related-to-a-record-" +
							"api-property");
				}

				if (Objects.equals(values.get("type"), "record") &&
					Objects.equals(apiPropertyValues.get("type"), "field")) {

					throw new ObjectEntryValuesException.InvalidObjectField(
						null,
						"A record API property must be related to another " +
							"record API property",
						"a-record-api-property-must-be-related-to-another-" +
							"record-api-property");
				}
			}

			int count = _objectEntryLocalService.getValuesListCount(
				new Long[] {objectEntry.getGroupId()},
				objectEntry.getCompanyId(), objectEntry.getUserId(),
				objectEntry.getObjectDefinitionId(),
				_filterFactory.create(
					StringBundler.concat(
						"id ne '", objectEntry.getObjectEntryId(),
						"' and name eq '", values.get("name"), "' and ",
						"r_apiPropertyToAPIProperties_l_apiPropertyId eq '",
						parentAPIPropertyId, "' and ",
						"r_apiSchemaToAPIProperties_l_apiSchemaId eq '",
						apiSchemaId, "'"),
					_objectDefinitionLocalService.getObjectDefinition(
						objectEntry.getObjectDefinitionId())),
				false, null);

			if (count > 0) {
				throw new ObjectEntryValuesException.InvalidObjectField(
					null, "API property name must be unique",
					"api-property-name-must-be-unique");
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryHelper _objectEntryHelper;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ValidationHelper _validationHelper;

}