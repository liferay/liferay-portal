/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.view.table;

import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.DateFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.DateTimeFDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaField;
import com.liferay.frontend.data.set.view.table.StringFDSTableSchemaField;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.setting.util.ObjectFieldSettingUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectView;
import com.liferay.object.model.ObjectViewColumn;
import com.liferay.object.model.ObjectViewColumnModel;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntriesTableFDSView extends BaseTableFDSView {

	public ObjectEntriesTableFDSView(
		FDSTableSchemaBuilderFactory fdsTableSchemaBuilderFactory,
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectViewLocalService objectViewLocalService,
		UserLocalService userLocalService) {

		_fdsTableSchemaBuilderFactory = fdsTableSchemaBuilderFactory;
		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectViewLocalService = objectViewLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		ObjectView defaultObjectView =
			_objectViewLocalService.fetchDefaultObjectView(
				_objectDefinition.getObjectDefinitionId());

		if (defaultObjectView == null) {
			_addAllObjectFields(fdsTableSchemaBuilder, locale);

			return fdsTableSchemaBuilder.build();
		}

		for (ObjectViewColumn objectViewColumn :
				ListUtil.sort(
					defaultObjectView.getObjectViewColumns(),
					Comparator.comparingInt(
						ObjectViewColumnModel::getPriority))) {

			ObjectField objectField = null;

			try {
				objectField = _objectFieldLocalService.getObjectField(
					_objectDefinition.getObjectDefinitionId(),
					objectViewColumn.getObjectFieldName());
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}

			String label = _getLabel(
				objectViewColumn.getLabel(locale, false),
				objectField.getLabel(locale, false));

			if ((objectField == null) || objectField.isMetadata()) {
				_addMetadataObjectField(
					fdsTableSchemaBuilder, label,
					objectViewColumn.getObjectFieldName());
			}
			else {
				if (Validator.isNull(label)) {
					label = _getLabel(
						objectViewColumn.getLabel(
							objectViewColumn.getDefaultLanguageId()),
						objectField.getLabel(
							objectField.getDefaultLanguageId()));
				}

				_addObjectField(fdsTableSchemaBuilder, label, objectField);
			}
		}

		return fdsTableSchemaBuilder.build();
	}

	private void _addAllObjectFields(
		FDSTableSchemaBuilder fdsTableSchemaBuilder, Locale locale) {

		Map<String, String> systemObjectFieldLabels = new HashMap<>();

		for (ObjectField systemObjectField :
				_objectFieldLocalService.getObjectFields(
					_objectDefinition.getObjectDefinitionId(), true)) {

			systemObjectFieldLabels.put(
				systemObjectField.getName(),
				systemObjectField.getLabel(locale, false));
		}

		if (_objectDefinition.isDefaultStorageType()) {
			_addMetadataObjectField(
				fdsTableSchemaBuilder, systemObjectFieldLabels.get("id"), "id");
		}
		else {
			_addMetadataObjectField(
				fdsTableSchemaBuilder,
				systemObjectFieldLabels.get("externalReferenceCode"),
				"externalReferenceCode");
		}

		for (ObjectField objectField :
				_objectFieldLocalService.getObjectFields(
					_objectDefinition.getObjectDefinitionId())) {

			_addObjectField(
				fdsTableSchemaBuilder, objectField.getLabel(locale, true),
				objectField);
		}

		_addMetadataObjectField(
			fdsTableSchemaBuilder, systemObjectFieldLabels.get("status"),
			"status");
		_addMetadataObjectField(
			fdsTableSchemaBuilder, systemObjectFieldLabels.get("creator"),
			"creator");
	}

	private void _addFDSTableSchemaField(
		String businessType, String contentRenderer, String dbType,
		FDSTableSchemaBuilder fdsTableSchemaBuilder, String fieldName,
		String label, boolean localizeLabel,
		List<ObjectFieldSetting> objectFieldSettings, boolean sortable) {

		FDSTableSchemaField fdsTableSchemaField = new FDSTableSchemaField();

		fdsTableSchemaField.setLocalizeLabel(localizeLabel);

		if (Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) ||
			Objects.equals(dbType, ObjectFieldConstants.DB_TYPE_BIG_DECIMAL) ||
			Objects.equals(dbType, ObjectFieldConstants.DB_TYPE_CLOB) ||
			Objects.equals(dbType, ObjectFieldConstants.DB_TYPE_DOUBLE) ||
			Objects.equals(dbType, ObjectFieldConstants.DB_TYPE_STRING)) {

			StringFDSTableSchemaField stringFDSTableSchemaField =
				new StringFDSTableSchemaField();

			if (Objects.equals(
					businessType,
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				stringFDSTableSchemaField.setContentRenderer("link");
			}
			else if (Objects.equals(
						businessType,
						ObjectFieldConstants.BUSINESS_TYPE_DECIMAL) ||
					 Objects.equals(
						 businessType,
						 ObjectFieldConstants.
							 BUSINESS_TYPE_PRECISION_DECIMAL)) {

				stringFDSTableSchemaField.setContentRenderer(
					"decimalDataRenderer");
			}
			else if (Objects.equals(
						businessType,
						ObjectFieldConstants.
							BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

				stringFDSTableSchemaField.setContentRenderer(
					"multiselectPicklistDataRenderer");
			}

			stringFDSTableSchemaField.setFieldName(
				fieldName
			).setLabel(
				label
			).setLocalizeLabel(
				localizeLabel
			);

			stringFDSTableSchemaField.setTruncate(true);

			fdsTableSchemaBuilder.add(stringFDSTableSchemaField);

			fdsTableSchemaField = stringFDSTableSchemaField;
		}
		else if (Objects.equals(
					businessType, ObjectFieldConstants.BUSINESS_TYPE_DATE)) {

			DateFDSTableSchemaField dateFDSTableSchemaField =
				new DateFDSTableSchemaField();

			dateFDSTableSchemaField.setFieldName(fieldName);
			dateFDSTableSchemaField.setFormat(
				_getFormatJSONObject(businessType, null));
			dateFDSTableSchemaField.setLabel(
				label
			).setLocalizeLabel(
				localizeLabel
			);

			fdsTableSchemaBuilder.add(dateFDSTableSchemaField);

			fdsTableSchemaField = dateFDSTableSchemaField;
		}
		else if (Objects.equals(
					businessType,
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			DateTimeFDSTableSchemaField dateTimeFDSTableSchemaField =
				new DateTimeFDSTableSchemaField();

			dateTimeFDSTableSchemaField.setFieldName(fieldName);

			User user = null;

			try {
				user = _userLocalService.getUser(
					PrincipalThreadLocal.getUserId());
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}

			dateTimeFDSTableSchemaField.setFormat(
				_getFormatJSONObject(
					businessType,
					ObjectFieldSettingUtil.getTimeZoneId(
						objectFieldSettings, user)));
			dateTimeFDSTableSchemaField.setLabel(
				label
			).setLocalizeLabel(
				localizeLabel
			);

			fdsTableSchemaBuilder.add(dateTimeFDSTableSchemaField);

			fdsTableSchemaField = dateTimeFDSTableSchemaField;
		}
		else {
			fdsTableSchemaField.setFieldName(
				fieldName
			).setLabel(
				label
			);

			fdsTableSchemaBuilder.add(fdsTableSchemaField);

			if (Objects.equals(dbType, ObjectFieldConstants.DB_TYPE_BOOLEAN)) {
				fdsTableSchemaField.setContentRenderer("boolean");
			}
		}

		if (Validator.isNotNull(contentRenderer)) {
			fdsTableSchemaField.setContentRenderer(contentRenderer);
		}

		if (!Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION) &&
			!Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT) &&
			!Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT) &&
			!Objects.equals(dbType, ObjectFieldConstants.DB_TYPE_BLOB) &&
			sortable) {

			fdsTableSchemaField.setSortable(true);
		}

		fdsTableSchemaBuilder.add(fdsTableSchemaField);
	}

	private void _addMetadataObjectField(
		FDSTableSchemaBuilder fdsTableSchemaBuilder, String fieldLabel,
		String fieldName) {

		if (Objects.equals(fieldName, "createDate")) {
			_addFDSTableSchemaField(
				"DateTime", null, "Date", fdsTableSchemaBuilder, "dateCreated",
				_getLabel(fieldLabel, "create-date"), true, null, true);
		}
		else if (Objects.equals(fieldName, "creator")) {
			_addFDSTableSchemaField(
				null, null, null, fdsTableSchemaBuilder, fieldName + ".name",
				_getLabel(fieldLabel, "author"), true, null, true);
		}
		else if (Objects.equals(fieldName, "externalReferenceCode")) {
			_addFDSTableSchemaField(
				null, "actionLink", null, fdsTableSchemaBuilder, fieldName,
				_getLabel(fieldLabel, "external-reference-code"), true, null,
				true);
		}
		else if (Objects.equals(fieldName, "id")) {
			_addFDSTableSchemaField(
				null, "actionLink", null, fdsTableSchemaBuilder, "id",
				_getLabel(fieldLabel, "id"), true, null, true);
		}
		else if (Objects.equals(fieldName, "modifiedDate")) {
			_addFDSTableSchemaField(
				"DateTime", null, "Date", fdsTableSchemaBuilder, "dateModified",
				_getLabel(fieldLabel, "modified-date"), true, null, true);
		}
		else if (Objects.equals(fieldName, "status")) {
			_addFDSTableSchemaField(
				null, "statusDataRenderer", null, fdsTableSchemaBuilder,
				fieldName, _getLabel(fieldLabel, "status"), true, null, true);
		}
	}

	private void _addObjectField(
		FDSTableSchemaBuilder fdsTableSchemaBuilder, String label,
		ObjectField objectField) {

		if (objectField.isMetadata()) {
			return;
		}

		if (Validator.isNull(objectField.getRelationshipType())) {
			_addFDSTableSchemaField(
				objectField.getBusinessType(), null, objectField.getDBType(),
				fdsTableSchemaBuilder,
				_getFieldName(
					objectField.getBusinessType(), objectField.getName()),
				label, false, objectField.getObjectFieldSettings(),
				objectField.isIndexed());
		}
		else if (Objects.equals(
					objectField.getRelationshipType(),
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.
					fetchObjectRelationshipByObjectFieldId2(
						objectField.getObjectFieldId());

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					objectRelationship.getObjectDefinitionId1());

			ObjectField titleObjectField =
				_objectFieldLocalService.fetchObjectField(
					objectDefinition.getTitleObjectFieldId());

			if (titleObjectField == null) {
				_addFDSTableSchemaField(
					objectField.getBusinessType(), null,
					objectField.getDBType(), fdsTableSchemaBuilder,
					objectField.getName(), label, false,
					objectField.getObjectFieldSettings(), false);
			}
			else {
				_addFDSTableSchemaField(
					titleObjectField.getBusinessType(),
					_getContentRenderer(titleObjectField.getName()),
					titleObjectField.getDBType(), fdsTableSchemaBuilder,
					_getFieldName(
						titleObjectField.getBusinessType(),
						StringBundler.concat(
							StringUtil.replaceLast(
								objectField.getName(), "Id", ""),
							StringPool.PERIOD, titleObjectField.getName())),
					label, false, titleObjectField.getObjectFieldSettings(),
					false);
			}
		}
	}

	private String _getContentRenderer(String fieldName) {
		if (Objects.equals(fieldName, "status")) {
			return "status";
		}

		return null;
	}

	private String _getFieldName(String businessType, String fieldName) {
		if (fieldName.contains(".creator")) {
			return StringUtil.replaceLast(fieldName, "creator", "creator.name");
		}

		if (Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			return fieldName + ".link";
		}

		if (Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return fieldName + ".name";
		}

		if (Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			return fieldName + "RawText";
		}

		return fieldName;
	}

	private JSONObject _getFormatJSONObject(
		String businessType, String timeZone) {

		JSONObject formatJSONObject = JSONUtil.put(
			"day", "numeric"
		).put(
			"month", "short"
		).put(
			"year", "numeric"
		);

		if (StringUtil.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {

			formatJSONObject.put(
				"hour", "numeric"
			).put(
				"minute", "numeric"
			).put(
				"timeZone", timeZone
			);
		}

		return formatJSONObject;
	}

	private String _getLabel(String label, String defaultLabel) {
		if (Validator.isNotNull(label)) {
			return label;
		}

		return defaultLabel;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntriesTableFDSView.class);

	private final FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;
	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectViewLocalService _objectViewLocalService;
	private final UserLocalService _userLocalService;

}