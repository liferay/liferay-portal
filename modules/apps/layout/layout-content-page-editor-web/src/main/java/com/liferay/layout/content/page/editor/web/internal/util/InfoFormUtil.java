/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.CategoriesInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Jürgen Kappler
 */
public class InfoFormUtil {

	public static JSONObject getConfigurationJSONObject(
		InfoForm infoForm, Locale locale) {

		if (infoForm == null) {
			return JSONUtil.put(
				"fieldSets",
				JSONUtil.put(
					JSONUtil.put("fields", JSONFactoryUtil.createJSONArray())));
		}

		JSONArray defaultFieldSetFieldsJSONArray =
			JSONFactoryUtil.createJSONArray();

		JSONArray fieldSetsJSONArray = JSONFactoryUtil.createJSONArray();

		for (InfoFieldSetEntry infoFieldSetEntry :
				infoForm.getInfoFieldSetEntries()) {

			if (infoFieldSetEntry instanceof InfoField) {
				InfoField<?> infoField = (InfoField<?>)infoFieldSetEntry;

				InfoFieldType infoFieldType = infoField.getInfoFieldType();

				if (!_isValidInfoFieldType(infoFieldType)) {
					continue;
				}

				defaultFieldSetFieldsJSONArray.put(
					_getFieldSetFieldJSONObject(
						infoField, infoFieldType, locale));
			}
			else if (infoFieldSetEntry instanceof InfoFieldSet) {
				InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

				JSONArray fieldSetFieldsJSONArray =
					JSONFactoryUtil.createJSONArray();

				for (InfoField<?> infoField : infoFieldSet.getAllInfoFields()) {
					InfoFieldType infoFieldType = infoField.getInfoFieldType();

					if (!_isValidInfoFieldType(infoFieldType)) {
						continue;
					}

					fieldSetFieldsJSONArray.put(
						_getFieldSetFieldJSONObject(
							infoField, infoFieldType, locale));
				}

				if (!JSONUtil.isEmpty(fieldSetFieldsJSONArray)) {
					fieldSetsJSONArray.put(
						JSONUtil.put(
							"description",
							() -> {
								InfoLocalizedValue<String>
									descriptionInfoLocalizedValue =
										infoFieldSet.
											getDescriptionInfoLocalizedValue();

								if (descriptionInfoLocalizedValue == null) {
									return null;
								}

								return descriptionInfoLocalizedValue.getValue(
									locale);
							}
						).put(
							"fields", fieldSetFieldsJSONArray
						).put(
							"label",
							() -> {
								InfoLocalizedValue<String>
									labelInfoLocalizedValue =
										infoFieldSet.
											getLabelInfoLocalizedValue();

								if (labelInfoLocalizedValue == null) {
									return null;
								}

								return labelInfoLocalizedValue.getValue(locale);
							}
						).put(
							"name", infoFieldSet.getName()
						));
				}
			}
		}

		if (!JSONUtil.isEmpty(defaultFieldSetFieldsJSONArray)) {
			fieldSetsJSONArray.put(
				JSONUtil.put("fields", defaultFieldSetFieldsJSONArray));
		}

		return JSONUtil.put("fieldSets", fieldSetsJSONArray);
	}

	private static String _getDataType(InfoFieldType infoFieldType) {
		if (infoFieldType instanceof BooleanInfoFieldType) {
			return "object";
		}
		else if (infoFieldType instanceof NumberInfoFieldType) {
			return "double";
		}

		return "string";
	}

	private static JSONObject _getFieldSetFieldJSONObject(
		InfoField infoField, InfoFieldType infoFieldType, Locale locale) {

		JSONObject jsonObject = JSONUtil.put(
			"dataType", _getDataType(infoFieldType)
		).put(
			"label", infoField.getLabel(locale)
		).put(
			"name", infoField.getName()
		).put(
			"type", _getType(infoFieldType)
		);

		if (infoFieldType instanceof CategoriesInfoFieldType) {
			jsonObject.put(
				"typeOptions",
				JSONUtil.put(
					"dependency",
					() -> {
						KeyValuePair dependencyKeyValuePair =
							(KeyValuePair)infoField.getAttribute(
								CategoriesInfoFieldType.DEPENDENCY);

						if (dependencyKeyValuePair == null) {
							return null;
						}

						return JSONUtil.put(
							dependencyKeyValuePair.getKey(),
							dependencyKeyValuePair.getValue());
					}
				).put(
					"infoItemSelectorURL",
					(String)infoField.getAttribute(
						CategoriesInfoFieldType.INFO_ITEM_SELECTOR_URL)
				).put(
					"modalSize", "md"
				));
		}
		else if (infoFieldType instanceof SelectInfoFieldType) {
			List<OptionInfoFieldType> optionInfoFieldTypes = new ArrayList<>();

			if (infoField.getAttribute(SelectInfoFieldType.OPTIONS) != null) {
				optionInfoFieldTypes.addAll(
					(List<OptionInfoFieldType>)infoField.getAttribute(
						SelectInfoFieldType.OPTIONS));
			}

			try {
				jsonObject.put(
					"defaultValue",
					() -> {
						for (OptionInfoFieldType optionInfoFieldType :
								optionInfoFieldTypes) {

							if (optionInfoFieldType.isActive()) {
								return String.valueOf(
									optionInfoFieldType.getValue());
							}
						}

						return null;
					}
				).put(
					"typeOptions",
					JSONUtil.put(
						"inline",
						() -> GetterUtil.getBoolean(
							infoField.getAttribute(SelectInfoFieldType.INLINE))
					).put(
						"multiSelect", false
					).put(
						"validValues",
						JSONUtil.toJSONArray(
							optionInfoFieldTypes,
							optionInfoFieldType -> JSONUtil.put(
								"label",
								String.valueOf(
									optionInfoFieldType.getLabel(locale))
							).put(
								"value",
								String.valueOf(optionInfoFieldType.getValue())
							))
					)
				);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}
		else if (infoFieldType instanceof MultiselectInfoFieldType) {
			List<OptionInfoFieldType> optionInfoFieldTypes = new ArrayList<>();

			if (infoField.getAttribute(MultiselectInfoFieldType.OPTIONS) !=
					null) {

				optionInfoFieldTypes.addAll(
					(List<OptionInfoFieldType>)infoField.getAttribute(
						MultiselectInfoFieldType.OPTIONS));
			}

			try {
				jsonObject.put(
					"defaultValue",
					() -> {
						JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

						for (OptionInfoFieldType optionInfoFieldType :
								optionInfoFieldTypes) {

							if (!optionInfoFieldType.isActive()) {
								continue;
							}

							jsonArray.put(
								String.valueOf(optionInfoFieldType.getValue()));
						}

						return jsonArray;
					}
				).put(
					"typeOptions",
					JSONUtil.put(
						"inline",
						() -> GetterUtil.getBoolean(
							infoField.getAttribute(SelectInfoFieldType.INLINE))
					).put(
						"multiSelect", true
					).put(
						"validValues",
						JSONUtil.toJSONArray(
							optionInfoFieldTypes,
							optionInfoFieldType -> JSONUtil.put(
								"label",
								String.valueOf(
									optionInfoFieldType.getLabel(locale))
							).put(
								"value",
								String.valueOf(optionInfoFieldType.getValue())
							))
					)
				);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return jsonObject;
	}

	private static String _getType(InfoFieldType infoFieldType) {
		if (infoFieldType instanceof CategoriesInfoFieldType) {
			return "itemSelector";
		}
		else if (infoFieldType instanceof BooleanInfoFieldType) {
			return "checkbox";
		}
		else if (infoFieldType instanceof MultiselectInfoFieldType ||
				 infoFieldType instanceof SelectInfoFieldType) {

			return "select";
		}

		return "text";
	}

	private static boolean _isValidInfoFieldType(InfoFieldType infoFieldType) {
		if (infoFieldType instanceof CategoriesInfoFieldType ||
			infoFieldType instanceof MultiselectInfoFieldType ||
			infoFieldType instanceof SelectInfoFieldType ||
			infoFieldType instanceof TextInfoFieldType) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(InfoFormUtil.class);

}