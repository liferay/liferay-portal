/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.headless.delivery.dto.v1_0.ContextReference;
import com.liferay.headless.delivery.dto.v1_0.LocalizationConfig;
import com.liferay.headless.delivery.dto.v1_0.MessageFormSubmissionResult;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.converter.AlignConverter;
import com.liferay.layout.converter.ContentDisplayConverter;
import com.liferay.layout.converter.FlexWrapConverter;
import com.liferay.layout.converter.JustifyConverter;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.util.LayoutPageTemplateEntryUtil;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class FormLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					layoutStructureItemImporterContext.getItemId(pageElement),
					layoutStructureItemImporterContext.getParentItemId(),
					layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return formStyledLayoutStructureItem;
		}

		if (definitionMap.get("cssClasses") instanceof List<?> cssClasses) {
			formStyledLayoutStructureItem.setCssClasses(
				new HashSet<>((List<String>)cssClasses));
		}

		Object customCSS = definitionMap.get("customCSS");

		if (customCSS != null) {
			formStyledLayoutStructureItem.setCustomCSS(
				String.valueOf(customCSS));
		}

		if (definitionMap.get("customCSSViewports") instanceof
				List<?> customCSSViewports) {

			for (Map<String, Object> customCSSViewport :
					(List<Map<String, Object>>)customCSSViewports) {

				formStyledLayoutStructureItem.setCustomCSSViewport(
					(String)customCSSViewport.get("id"),
					(String)customCSSViewport.get("customCSS"));
			}
		}

		Map<String, Object> sourceMap = (Map<String, Object>)definitionMap.get(
			"formConfig");

		if (sourceMap != null) {
			Map<String, Object> itemReferenceMap =
				(Map<String, Object>)sourceMap.get("formReference");

			if (Objects.equals(
					ContextReference.ContextSource.DISPLAY_PAGE_ITEM.getValue(),
					(String)itemReferenceMap.get("contextSource"))) {

				LayoutPageTemplateEntry layoutPageTemplateEntry =
					_getLayoutPageTemplateEntry(
						layoutStructureItemImporterContext);

				if (layoutPageTemplateEntry != null) {
					formStyledLayoutStructureItem.setClassNameId(
						layoutPageTemplateEntry.getClassNameId());
					formStyledLayoutStructureItem.setClassTypeId(
						LayoutPageTemplateEntryUtil.getClassTypeId(
							layoutPageTemplateEntry));
				}

				formStyledLayoutStructureItem.setFormConfig(
					FormStyledLayoutStructureItem.
						FORM_CONFIG_DISPLAY_PAGE_ITEM_TYPE);
			}
			else {
				formStyledLayoutStructureItem.setClassNameId(
					PortalUtil.getClassNameId(
						(String)itemReferenceMap.get("className")));

				Integer classType = (Integer)itemReferenceMap.get("classType");

				if (classType != null) {
					formStyledLayoutStructureItem.setClassTypeId(classType);
				}

				formStyledLayoutStructureItem.setFormConfig(
					FormStyledLayoutStructureItem.FORM_CONFIG_OTHER_ITEM_TYPE);
			}

			if (sourceMap.get("formType") instanceof String formType) {
				formStyledLayoutStructureItem.setFormType(formType);
			}

			JSONObject localizationConfigJSONObject =
				_getLocalizationConfigJSONObject(sourceMap);

			if (localizationConfigJSONObject != null) {
				formStyledLayoutStructureItem.setLocalizationConfigJSONObject(
					localizationConfigJSONObject);
			}

			Object numberOfSteps = sourceMap.get("numberOfSteps");

			if (numberOfSteps != null) {
				formStyledLayoutStructureItem.setNumberOfSteps(
					GetterUtil.getInteger(numberOfSteps));
			}

			JSONObject successMessageJSONObject = _getSuccessMessageJSONObject(
				layoutStructureItemImporterContext, sourceMap);

			if (successMessageJSONObject != null) {
				formStyledLayoutStructureItem.setSuccessMessageJSONObject(
					successMessageJSONObject);
			}
		}

		Map<String, Object> fragmentStyleMap =
			(Map<String, Object>)definitionMap.get("fragmentStyle");

		if (fragmentStyleMap != null) {
			JSONObject jsonObject = JSONUtil.put(
				"styles",
				toStylesJSONObject(
					layoutStructureItemImporterContext, fragmentStyleMap));

			formStyledLayoutStructureItem.updateItemConfig(jsonObject);
		}

		if (definitionMap.get("fragmentViewports") instanceof
				List<?> fragmentViewports) {

			for (Map<String, Object> fragmentViewport :
					(List<Map<String, Object>>)fragmentViewports) {

				JSONObject jsonObject = JSONUtil.put(
					(String)fragmentViewport.get("id"),
					toFragmentViewportStylesJSONObject(fragmentViewport));

				formStyledLayoutStructureItem.updateItemConfig(jsonObject);
			}
		}

		Object indexed = definitionMap.get("indexed");

		if (indexed != null) {
			formStyledLayoutStructureItem.setIndexed(
				GetterUtil.getBoolean(indexed));
		}

		Map<String, Object> formLayout = (Map<String, Object>)definitionMap.get(
			"layout");

		if (formLayout != null) {
			String align = String.valueOf(
				formLayout.getOrDefault("align", StringPool.BLANK));

			if (Validator.isNotNull(align)) {
				formStyledLayoutStructureItem.setAlign(
					AlignConverter.convertToInternalValue(align));
			}

			String contentDisplay = String.valueOf(
				formLayout.getOrDefault("contentDisplay", StringPool.BLANK));

			if (Validator.isNotNull(contentDisplay)) {
				formStyledLayoutStructureItem.setContentDisplay(
					ContentDisplayConverter.convertToInternalValue(
						contentDisplay));
			}

			String flexWrap = String.valueOf(
				formLayout.getOrDefault("flexWrap", StringPool.BLANK));

			if (Validator.isNotNull(flexWrap)) {
				formStyledLayoutStructureItem.setFlexWrap(
					FlexWrapConverter.convertToInternalValue(flexWrap));
			}

			String justify = String.valueOf(
				formLayout.getOrDefault("justify", StringPool.BLANK));

			if (Validator.isNotNull(justify)) {
				formStyledLayoutStructureItem.setJustify(
					JustifyConverter.convertToInternalValue(justify));
			}

			String widthType = StringUtil.toLowerCase(
				(String)formLayout.get("widthType"));

			if (widthType != null) {
				formStyledLayoutStructureItem.setWidthType(widthType);
			}
		}

		String name = GetterUtil.getString(definitionMap.get("name"), null);

		if (name != null) {
			formStyledLayoutStructureItem.setName(name);
		}

		return formStyledLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.FORM;
	}

	private LayoutPageTemplateEntry _getLayoutPageTemplateEntry(
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		Layout layout = layoutStructureItemImporterContext.getLayout();

		if (!layout.isTypeAssetDisplay()) {
			return null;
		}

		if (layout.isDraftLayout()) {
			LayoutLocalService layoutLocalService =
				layoutStructureItemImporterContext.getLayoutLocalService();

			layout = layoutLocalService.fetchLayout(layout.getClassPK());
		}

		if (layout == null) {
			return null;
		}

		LayoutPageTemplateEntryLocalService
			layoutPageTemplateEntryLocalService =
				layoutStructureItemImporterContext.
					getLayoutPageTemplateEntryLocalService();

		return layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByPlid(layout.getPlid());
	}

	private JSONObject _getLocalizationConfigJSONObject(
		Map<String, Object> sourceMap) {

		Map<String, Object> localizationConfigResultMap =
			(Map<String, Object>)sourceMap.get("localizationConfig");

		if (MapUtil.isEmpty(localizationConfigResultMap)) {
			return null;
		}

		return JSONUtil.put(
			"unlocalizedFieldsMessage",
			() -> {
				if (!localizationConfigResultMap.containsKey(
						"unlocalizedFieldsMessage")) {

					return null;
				}

				return _getLocalizedValuesJSONObject(
					"unlocalizedFieldsMessage", localizationConfigResultMap);
			}
		).put(
			"unlocalizedFieldsState",
			() -> {
				Map.Entry<String, Object> unlocalizedFieldsStateEntry =
					MapUtil.getEntry(
						localizationConfigResultMap, "unlocalizedFieldsState");

				if (unlocalizedFieldsStateEntry == null) {
					return null;
				}

				if (Objects.equals(
						unlocalizedFieldsStateEntry.getValue(),
						LocalizationConfig.UnlocalizedFieldsState.DISABLED)) {

					return "disabled";
				}

				return "read-only";
			}
		);
	}

	private JSONObject _getLocalizedValuesJSONObject(
		String key, Map<String, Object> propertiesMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Map<String, Object> map = (Map<String, Object>)propertiesMap.get(key);

		if (MapUtil.isEmpty(map)) {
			return jsonObject;
		}

		Map<String, Object> localizedMap = (Map<String, Object>)map.get(
			"value_i18n");

		if (localizedMap == null) {
			return jsonObject;
		}

		for (Map.Entry<String, Object> entry : localizedMap.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}

		return jsonObject;
	}

	private JSONObject _getSuccessMessageJSONObject(
		LayoutStructureItemImporterContext layoutStructureItemImporterContext,
		Map<String, Object> sourceMap) {

		Map<String, Object> formSuccessSubmissionResultMap =
			(Map<String, Object>)sourceMap.get("formSuccessSubmissionResult");

		if (MapUtil.isEmpty(formSuccessSubmissionResultMap)) {
			return null;
		}

		Map.Entry<String, Object> messageTypeEntry = MapUtil.getEntry(
			formSuccessSubmissionResultMap, "messageType");

		if (messageTypeEntry != null) {
			JSONObject messageJSONObject = _setNotificationText(
				JSONUtil.put(
					"message",
					_getLocalizedValuesJSONObject(
						"message", formSuccessSubmissionResultMap)),
				formSuccessSubmissionResultMap);

			if (Objects.equals(
					String.valueOf(messageTypeEntry.getValue()),
					MessageFormSubmissionResult.MessageType.EMBEDDED.
						getValue())) {

				return messageJSONObject.put("type", "embedded");
			}

			return messageJSONObject.put("type", "none");
		}

		if (formSuccessSubmissionResultMap.get("itemReference") instanceof
				Map<?, ?> itemReference) {

			return _setNotificationText(
				JSONUtil.put(
					"layout",
					getLayoutFromItemReferenceJSONObject(
						(Map<String, Object>)itemReference,
						layoutStructureItemImporterContext)),
				formSuccessSubmissionResultMap
			).put(
				"type", "page"
			);
		}

		if (formSuccessSubmissionResultMap.containsKey("defaultDisplayPage")) {
			JSONObject displayPageTemplateJSONObject =
				toDisplayPageFormSubmissionResultJSONObject(
					formSuccessSubmissionResultMap,
					layoutStructureItemImporterContext);

			return _setNotificationText(
				displayPageTemplateJSONObject, formSuccessSubmissionResultMap);
		}

		if (formSuccessSubmissionResultMap.containsKey("url")) {
			return JSONUtil.put(
				"type", "url"
			).put(
				"url",
				_getLocalizedValuesJSONObject(
					"url", formSuccessSubmissionResultMap)
			);
		}

		return null;
	}

	private JSONObject _setNotificationText(
		JSONObject jsonObject,
		Map<String, Object> formSuccessSubmissionResultMap) {

		return jsonObject.put(
			"notificationText",
			() -> {
				JSONObject notificationTextJSONObject =
					_getLocalizedValuesJSONObject(
						"notificationTextFragmentInlineValue",
						formSuccessSubmissionResultMap);

				if (!JSONUtil.isEmpty(notificationTextJSONObject)) {
					return notificationTextJSONObject;
				}

				return null;
			}
		).put(
			"showNotification",
			() -> {
				Map.Entry<String, Object> showNotificationEntry =
					MapUtil.getEntry(
						formSuccessSubmissionResultMap, "showNotification");

				if (showNotificationEntry == null) {
					return null;
				}

				return GetterUtil.getBoolean(showNotificationEntry.getValue());
			}
		);
	}

}