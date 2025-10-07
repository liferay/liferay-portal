/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class MappingContentUtil {

	public static JSONArray getEditableMappingFieldsJSONArray(
			String formVariationKey, long groupId,
			InfoItemServiceRegistry infoItemServiceRegistry,
			String itemClassName, Locale locale)
		throws Exception {

		return _getMappingFieldsJSONArray(
			formVariationKey, groupId, true, infoItemServiceRegistry,
			itemClassName, locale);
	}

	public static JSONObject getInfoFieldJSONObject(
		InfoField<?> infoField, Locale locale) {

		return JSONUtil.put(
			"key", infoField.getUniqueId()
		).put(
			"label",
			() -> {
				if (infoField.isMultivalued() || infoField.isRepeatable()) {
					return LanguageUtil.format(
						locale, "x-repeatable", infoField.getLabel(locale),
						false);
				}

				return infoField.getLabel(locale);
			}
		).put(
			"localizable", infoField.isLocalizable()
		).put(
			"name", infoField.getName()
		).put(
			"repeatable", infoField.isMultivalued() || infoField.isRepeatable()
		).put(
			"required", infoField.isRequired()
		).put(
			"type",
			() -> {
				InfoFieldType infoFieldType = infoField.getInfoFieldType();

				return infoFieldType.getName();
			}
		).put(
			"typeLabel",
			() -> {
				InfoFieldType infoFieldType = infoField.getInfoFieldType();

				return infoFieldType.getLabel(locale);
			}
		);
	}

	public static JSONArray getMappingFieldsJSONArray(
			String formVariationKey, long groupId,
			InfoItemServiceRegistry infoItemServiceRegistry,
			String itemClassName, Locale locale)
		throws Exception {

		return _getMappingFieldsJSONArray(
			formVariationKey, groupId, false, infoItemServiceRegistry,
			itemClassName, locale);
	}

	private static JSONObject _getInfoFieldSetJSONObject(
		boolean includeEditableInfoFields, InfoFieldSet infoFieldSet,
		Locale locale) {

		JSONArray fieldSetFieldsJSONArray = JSONFactoryUtil.createJSONArray();

		for (InfoFieldSetEntry infoFieldSetEntry :
				infoFieldSet.getInfoFieldSetEntries()) {

			if (infoFieldSetEntry instanceof InfoField) {
				InfoField<?> infoField = (InfoField<?>)infoFieldSetEntry;

				if (!includeEditableInfoFields || infoField.isEditable()) {
					fieldSetFieldsJSONArray.put(
						getInfoFieldJSONObject(infoField, locale));
				}
			}
			else if (infoFieldSetEntry instanceof InfoFieldSet) {
				JSONObject jsonObject = _getInfoFieldSetJSONObject(
					includeEditableInfoFields, (InfoFieldSet)infoFieldSetEntry,
					locale);

				if (jsonObject != null) {
					fieldSetFieldsJSONArray.put(jsonObject);
				}
			}
		}

		if (fieldSetFieldsJSONArray.length() > 0) {
			return JSONUtil.put(
				"fields", fieldSetFieldsJSONArray
			).put(
				"label", infoFieldSet.getLabel(locale)
			).put(
				"name", infoFieldSet.getName()
			).put(
				"relationship", infoFieldSet.isRelationship()
			);
		}

		return null;
	}

	private static JSONArray _getMappingFieldsJSONArray(
			String formVariationKey, long groupId,
			boolean includeEditableInfoFields,
			InfoItemServiceRegistry infoItemServiceRegistry,
			String itemClassName, Locale locale)
		throws Exception {

		String className = InfoSearchClassMapperRegistryUtil.getClassName(
			itemClassName);

		InfoItemFormProvider<?> infoItemFormProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, className);

		if (infoItemFormProvider == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get info item form provider for class " +
						className);
			}

			return JSONFactoryUtil.createJSONArray();
		}

		JSONArray defaultFieldSetFieldsJSONArray =
			JSONFactoryUtil.createJSONArray();

		JSONArray fieldSetsJSONArray = JSONUtil.put(
			JSONUtil.put("fields", defaultFieldSetFieldsJSONArray));

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			formVariationKey, groupId);

		for (InfoFieldSetEntry infoFieldSetEntry :
				infoForm.getInfoFieldSetEntries()) {

			if (infoFieldSetEntry instanceof InfoField) {
				InfoField<?> infoField = (InfoField<?>)infoFieldSetEntry;

				if (!includeEditableInfoFields || infoField.isEditable()) {
					defaultFieldSetFieldsJSONArray.put(
						getInfoFieldJSONObject(infoField, locale));
				}
			}
			else if (infoFieldSetEntry instanceof InfoFieldSet) {
				JSONObject infoFieldSetJSONObject = _getInfoFieldSetJSONObject(
					includeEditableInfoFields, (InfoFieldSet)infoFieldSetEntry,
					locale);

				if (infoFieldSetJSONObject != null) {
					fieldSetsJSONArray.put(infoFieldSetJSONObject);
				}
			}
		}

		return fieldSetsJSONArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MappingContentUtil.class);

}