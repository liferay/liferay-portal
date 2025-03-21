/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.odata.entity;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.normalizer.Normalizer;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizerRegistry;

import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(service = EntityModelFieldMapper.class)
public class EntityModelFieldMapper {

	public Map<String, EntityField> getCustomFieldEntityFields(
		EntityModel entityModel) {

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		ComplexEntityField customFieldEntityField =
			(ComplexEntityField)entityFieldsMap.get("customField");

		if (customFieldEntityField == null) {
			return Collections.emptyMap();
		}

		return customFieldEntityField.getEntityFieldsMap();
	}

	public ExpandoColumn getExpandoColumn(String entityFieldName) {
		long expandoColumnId = getExpandoColumnId(entityFieldName);

		try {
			return _expandoColumnLocalService.getColumn(expandoColumnId);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to find Expando Column with id " + expandoColumnId,
				portalException);

			return null;
		}
	}

	public String getExpandoColumnEntityFieldName(ExpandoColumn expandoColumn) {
		return StringBundler.concat(
			StringPool.UNDERLINE, expandoColumn.getColumnId(),
			StringPool.UNDERLINE,
			Normalizer.normalizeIdentifier(expandoColumn.getName()));
	}

	public long getExpandoColumnId(String entityFieldName) {
		String[] split = StringUtil.split(
			entityFieldName, StringPool.UNDERLINE);

		if (split.length < 2) {
			return 0;
		}

		return Long.valueOf(split[1]);
	}

	public List<Field> getFields(
		EntityModel entityModel, PortletRequest portletRequest) {

		List<Field> fields = new ArrayList<>();

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		entityFieldsMap.forEach(
			(entityFieldName, entityField) -> fields.addAll(
				getFields(entityModel, entityField, portletRequest)));

		Collections.sort(fields);

		return fields;
	}

	protected List<Field> getFields(
		EntityModel entityModel, EntityField entityField,
		PortletRequest portletRequest) {

		Locale locale = _portal.getLocale(portletRequest);

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		EntityField.Type entityFieldType = entityField.getType();

		if (entityFieldType == EntityField.Type.COMPLEX) {
			ComplexEntityField complexEntityField =
				(ComplexEntityField)entityField;

			return _getComplexFields(
				entityModel.getName(), entityField.getName(),
				complexEntityField.getEntityFieldsMap(), locale, portletRequest,
				resourceBundle);
		}

		SegmentsFieldCustomizer segmentsFieldCustomizer =
			_segmentsFieldCustomizerRegistry.getSegmentsFieldCustomizer(
				entityModel.getName(), entityField.getName());

		if ((entityFieldType == EntityField.Type.ID) &&
			(segmentsFieldCustomizer == null)) {

			return Collections.emptyList();
		}

		return Collections.singletonList(
			_getField(
				entityField.getName(), _getType(entityField.getType()),
				portletRequest, resourceBundle, segmentsFieldCustomizer));
	}

	private List<Field> _getComplexFields(
		String entityModelName, String complexEntityFieldName,
		Map<String, EntityField> entityFieldsMap, Locale locale,
		PortletRequest portletRequest, ResourceBundle resourceBundle) {

		if (complexEntityFieldName.equals("customField")) {
			return _getCustomFields(entityFieldsMap, locale);
		}

		List<Field> complexFields = new ArrayList<>();

		entityFieldsMap.forEach(
			(entityFieldName, entityField) -> {
				SegmentsFieldCustomizer segmentsFieldCustomizer =
					_segmentsFieldCustomizerRegistry.getSegmentsFieldCustomizer(
						entityModelName, entityField.getName());

				if ((entityField.getType() == EntityField.Type.ID) &&
					(segmentsFieldCustomizer == null)) {

					return;
				}

				complexFields.add(
					_getField(
						"customContext/" + entityField.getName(),
						_getType(entityField.getType()), portletRequest,
						resourceBundle, segmentsFieldCustomizer));
			});

		return complexFields;
	}

	private List<Field> _getCustomFields(
		Map<String, EntityField> entityFieldsMap, Locale locale) {

		List<Field> customFields = new ArrayList<>();

		entityFieldsMap.forEach(
			(entityFieldName, entityField) -> {
				ExpandoColumn expandoColumn =
					_expandoColumnLocalService.fetchExpandoColumn(
						getExpandoColumnId(entityFieldName));

				if (expandoColumn == null) {
					return;
				}

				String label = expandoColumn.getDisplayName(locale);

				customFields.add(
					new Field(
						"customField/" + entityFieldName, label,
						_getType(entityField.getType()),
						_getExpandoColumnFieldOptions(expandoColumn), null));
			});

		return customFields;
	}

	private List<Field.Option> _getExpandoColumnFieldOptions(
		ExpandoColumn expandoColumn) {

		List<Field.Option> fieldOptions = new ArrayList<>();

		if (expandoColumn.getType() == ExpandoColumnConstants.DOUBLE_ARRAY) {
			for (double value : (double[])expandoColumn.getDefaultValue()) {
				fieldOptions.add(
					new Field.Option(
						String.valueOf(value), String.valueOf(value)));
			}
		}
		else if (expandoColumn.getType() ==
					ExpandoColumnConstants.FLOAT_ARRAY) {

			for (float value : (float[])expandoColumn.getDefaultValue()) {
				fieldOptions.add(
					new Field.Option(
						String.valueOf(value), String.valueOf(value)));
			}
		}
		else if (expandoColumn.getType() ==
					ExpandoColumnConstants.INTEGER_ARRAY) {

			for (int value : (int[])expandoColumn.getDefaultValue()) {
				fieldOptions.add(
					new Field.Option(
						String.valueOf(value), String.valueOf(value)));
			}
		}
		else if (expandoColumn.getType() == ExpandoColumnConstants.LONG_ARRAY) {
			for (long value : (long[])expandoColumn.getDefaultValue()) {
				fieldOptions.add(
					new Field.Option(
						String.valueOf(value), String.valueOf(value)));
			}
		}
		else if (expandoColumn.getType() ==
					ExpandoColumnConstants.SHORT_ARRAY) {

			for (short value : (short[])expandoColumn.getDefaultValue()) {
				fieldOptions.add(
					new Field.Option(
						String.valueOf(value), String.valueOf(value)));
			}
		}
		else if (expandoColumn.getType() ==
					ExpandoColumnConstants.STRING_ARRAY) {

			for (String value : (String[])expandoColumn.getDefaultValue()) {
				fieldOptions.add(new Field.Option(value, value));
			}
		}

		return fieldOptions;
	}

	private Field _getField(
		String fieldName, String fieldType, PortletRequest portletRequest,
		ResourceBundle resourceBundle,
		SegmentsFieldCustomizer segmentsFieldCustomizer) {

		if (segmentsFieldCustomizer != null) {
			return new Field(
				segmentsFieldCustomizer.getIcon(), fieldName,
				segmentsFieldCustomizer.getLabel(
					fieldName, resourceBundle.getLocale()),
				fieldType,
				segmentsFieldCustomizer.getOptions(resourceBundle.getLocale()),
				segmentsFieldCustomizer.getSelectEntity(portletRequest));
		}

		String fieldLabel = _language.get(
			resourceBundle, "field." + CamelCaseUtil.fromCamelCase(fieldName));

		return new Field(fieldName, fieldLabel, fieldType);
	}

	private String _getType(EntityField.Type entityFieldType) {
		if (entityFieldType == EntityField.Type.BOOLEAN) {
			return "boolean";
		}
		else if (entityFieldType == EntityField.Type.COLLECTION) {
			return "collection";
		}
		else if (entityFieldType == EntityField.Type.DATE) {
			return "date";
		}
		else if (entityFieldType == EntityField.Type.DATE_TIME) {
			return "date-time";
		}
		else if (entityFieldType == EntityField.Type.DOUBLE) {
			return "double";
		}
		else if (entityFieldType == EntityField.Type.ID) {
			return "id";
		}
		else if (entityFieldType == EntityField.Type.INTEGER) {
			return "integer";
		}

		return "string";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EntityModelFieldMapper.class);

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsFieldCustomizerRegistry _segmentsFieldCustomizerRegistry;

}