/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldSetEntry;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.item.provider.RepeatableFieldsInfoItemFormProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.MappingContentUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_collection_mapping_fields"
	},
	service = MVCResourceCommand.class
)
public class GetCollectionMappingFieldsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String itemSubtype = ParamUtil.getString(
			resourceRequest, "itemSubtype");

		String itemType = _infoSearchClassMapperRegistry.getClassName(
			ParamUtil.getString(resourceRequest, "itemType"));

		String itemSubtypeLabel = StringPool.BLANK;

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, itemType);

		if (infoItemFormVariationsProvider != null) {
			InfoItemFormVariation infoItemFormVariation =
				infoItemFormVariationsProvider.getInfoItemFormVariation(
					themeDisplay.getScopeGroupId(), itemSubtype);

			if (infoItemFormVariation != null) {
				itemSubtypeLabel = infoItemFormVariation.getLabel(
					themeDisplay.getLocale());
			}
		}

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, itemType);

		try {
			String fieldName = ParamUtil.getString(
				resourceRequest, "fieldName");

			InfoForm infoForm = infoItemFormProvider.getInfoForm();

			String itemTypeLabel = infoForm.getLabel(themeDisplay.getLocale());

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"itemSubtypeLabel", itemSubtypeLabel
				).put(
					"itemTypeLabel", itemTypeLabel
				).put(
					"mappingFields",
					_getMappingFieldsJSONArray(
						fieldName, itemSubtype, themeDisplay.getScopeGroupId(),
						itemType, themeDisplay.getLocale())
				));
		}
		catch (Exception exception) {
			_log.error("Unable to get collection mapping fields", exception);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					_language.get(
						themeDisplay.getRequest(),
						"an-unexpected-error-occurred")));
		}
	}

	private JSONArray _getMappingFieldsJSONArray(
			String fieldName, String formVariationKey, long groupId,
			String itemClassName, Locale locale)
		throws Exception {

		if (Validator.isNull(fieldName)) {
			return MappingContentUtil.getMappingFieldsJSONArray(
				formVariationKey, groupId, _infoItemServiceRegistry,
				itemClassName, locale);
		}

		RepeatableFieldsInfoItemFormProvider<?>
			repeatableFieldsInfoItemFormProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					RepeatableFieldsInfoItemFormProvider.class, itemClassName);

		if (repeatableFieldsInfoItemFormProvider == null) {
			return _jsonFactory.createJSONArray();
		}

		List<InfoField<?>> infoFields = new ArrayList<>();

		InfoForm infoForm =
			repeatableFieldsInfoItemFormProvider.getRepeatableFieldsInfoForm(
				formVariationKey);

		InfoFieldSetEntry infoFieldSetEntry = infoForm.getInfoFieldSetEntry(
			fieldName);

		if (infoFieldSetEntry instanceof InfoFieldSet) {
			InfoFieldSet infoFieldSet = (InfoFieldSet)infoFieldSetEntry;

			infoFields.addAll(infoFieldSet.getAllInfoFields());
		}
		else {
			infoFields.add(infoForm.getInfoField(fieldName));
		}

		JSONArray defaultFieldSetFieldsJSONArray =
			_jsonFactory.createJSONArray();

		JSONArray fieldSetsJSONArray = JSONUtil.put(
			JSONUtil.put("fields", defaultFieldSetFieldsJSONArray));

		for (InfoField<?> infoField : infoFields) {
			JSONObject infoFieldJSONObject =
				MappingContentUtil.getInfoFieldJSONObject(infoField, locale);

			infoFieldJSONObject.remove("repeatable");

			defaultFieldSetFieldsJSONArray.put(
				infoFieldJSONObject.put("label", infoField.getLabel(locale)));
		}

		return fieldSetsJSONArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetCollectionMappingFieldsMVCResourceCommand.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}