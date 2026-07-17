/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class FormRelationshipLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		FormRelationshipStyledLayoutStructureItem
			formRelationshipStyledLayoutStructureItem =
				(FormRelationshipStyledLayoutStructureItem)
					layoutStructure.
						addFormRelationshipStyledLayoutStructureItem(
							layoutStructureItemImporterContext.getItemId(
								pageElement),
							layoutStructureItemImporterContext.
								getParentItemId(),
							layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return formRelationshipStyledLayoutStructureItem;
		}

		if (definitionMap.containsKey("buttonLabel")) {
			formRelationshipStyledLayoutStructureItem.setButtonLabelJSONObject(
				_getLocalizedValuesJSONObject("buttonLabel", definitionMap));
		}

		Object contentType = definitionMap.get("contentType");

		if (contentType != null) {
			formRelationshipStyledLayoutStructureItem.setContentType(
				String.valueOf(contentType));
		}

		if (definitionMap.get("cssClasses") instanceof List<?> cssClasses) {
			formRelationshipStyledLayoutStructureItem.setCssClasses(
				new HashSet<>((List<String>)cssClasses));
		}

		Object customCSS = definitionMap.get("customCSS");

		if (customCSS != null) {
			formRelationshipStyledLayoutStructureItem.setCustomCSS(
				String.valueOf(customCSS));
		}

		if (definitionMap.get("customCSSViewports") instanceof
				List<?> customCSSViewports) {

			for (Map<String, Object> customCSSViewport :
					(List<Map<String, Object>>)customCSSViewports) {

				formRelationshipStyledLayoutStructureItem.setCustomCSSViewport(
					(String)customCSSViewport.get("id"),
					(String)customCSSViewport.get("customCSS"));
			}
		}

		Map<String, Object> fragmentStyleMap =
			(Map<String, Object>)definitionMap.get("fragmentStyle");

		if (fragmentStyleMap != null) {
			JSONObject jsonObject = JSONUtil.put(
				"styles",
				toStylesJSONObject(
					layoutStructureItemImporterContext, fragmentStyleMap));

			formRelationshipStyledLayoutStructureItem.updateItemConfig(
				jsonObject);
		}

		if (definitionMap.get("fragmentViewports") instanceof
				List<?> fragmentViewports) {

			for (Map<String, Object> fragmentViewport :
					(List<Map<String, Object>>)fragmentViewports) {

				JSONObject jsonObject = JSONUtil.put(
					(String)fragmentViewport.get("id"),
					toFragmentViewportStylesJSONObject(fragmentViewport));

				formRelationshipStyledLayoutStructureItem.updateItemConfig(
					jsonObject);
			}
		}

		Object name = definitionMap.get("name");

		if (name != null) {
			formRelationshipStyledLayoutStructureItem.setName(
				String.valueOf(name));
		}

		Object repeatable = definitionMap.get("repeatable");

		if (repeatable != null) {
			formRelationshipStyledLayoutStructureItem.setRepeatable(
				GetterUtil.getBoolean(repeatable));
		}

		return formRelationshipStyledLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.FORM_RELATIONSHIP;
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

}