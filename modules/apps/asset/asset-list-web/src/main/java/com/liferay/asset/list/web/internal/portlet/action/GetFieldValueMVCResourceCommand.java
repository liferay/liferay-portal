/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeField;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.Serializable;

import java.text.DateFormat;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/get_field_value"
	},
	service = MVCResourceCommand.class
)
public class GetFieldValueMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			String className = ParamUtil.getString(
				resourceRequest, "className");

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className);

			ClassTypeReader classTypeReader =
				assetRendererFactory.getClassTypeReader();

			long classTypeId = ParamUtil.getLong(
				resourceRequest, "classTypeId");

			ClassType classType = classTypeReader.getClassType(
				classTypeId, themeDisplay.getLocale());

			String fieldName = ParamUtil.getString(resourceRequest, "name");

			ClassTypeField classTypeField = classType.getClassTypeField(
				fieldName);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				resourceRequest);

			Fields fields = (Fields)serviceContext.getAttribute(
				Fields.class.getName() + classTypeField.getClassTypeId());

			if (fields == null) {
				String fieldsNamespace = ParamUtil.getString(
					resourceRequest, "fieldsNamespace");

				fields = DDMUtil.getFields(
					classTypeField.getClassTypeId(), fieldsNamespace,
					serviceContext);
			}

			Field field = fields.get(fieldName);

			Serializable fieldValue = field.getValue(
				themeDisplay.getLocale(), 0);

			JSONObject jsonObject = _jsonFactory.createJSONObject();

			if (fieldValue != null) {
				jsonObject.put("success", true);
			}
			else {
				jsonObject.put("success", false);

				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse, jsonObject);

				return;
			}

			jsonObject.put(
				"displayValue", _getDisplayFieldValue(field, themeDisplay)
			).put(
				"value",
				() -> {
					if (fieldValue instanceof Boolean) {
						return (Boolean)fieldValue;
					}

					if (fieldValue instanceof Date) {
						DateFormat dateFormat =
							DateFormatFactoryUtil.getSimpleDateFormat(
								"yyyyMM ddHHmmss");

						return dateFormat.format(fieldValue);
					}

					if (fieldValue instanceof Double) {
						return (Double)fieldValue;
					}

					if (fieldValue instanceof Float) {
						return (Float)fieldValue;
					}

					if (fieldValue instanceof Integer) {
						return (Integer)fieldValue;
					}

					if (fieldValue instanceof Number) {
						return String.valueOf(fieldValue);
					}

					return (String)fieldValue;
				}
			);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private String _getDisplayFieldValue(Field field, ThemeDisplay themeDisplay)
		throws Exception {

		String fieldValue = String.valueOf(
			DDMUtil.getDisplayFieldValue(
				themeDisplay, field.getValue(themeDisplay.getLocale(), 0),
				field.getType()));

		DDMStructure ddmStructure = field.getDDMStructure();

		DDMFormField ddmFormField = ddmStructure.getDDMFormField(
			field.getName());

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		LocalizedValue localizedValue = ddmFormFieldOptions.getOptionLabels(
			String.valueOf(fieldValue));

		if (localizedValue != null) {
			return localizedValue.getString(themeDisplay.getLocale());
		}

		return fieldValue;
	}

	@Reference
	private JSONFactory _jsonFactory;

}