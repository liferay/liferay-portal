/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class FormStepContainerLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		FormStepContainerStyledLayoutStructureItem
			formStepContainerStyledLayoutStructureItem =
				(FormStepContainerStyledLayoutStructureItem)
					layoutStructure.
						addFormStepContainerStyledLayoutStructureItem(
							layoutStructureItemImporterContext.getItemId(
								pageElement),
							layoutStructureItemImporterContext.
								getParentItemId(),
							layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return formStepContainerStyledLayoutStructureItem;
		}

		if (definitionMap.get("cssClasses") instanceof List<?> cssClasses) {
			formStepContainerStyledLayoutStructureItem.setCssClasses(
				new HashSet<>((List<String>)cssClasses));
		}

		Object customCSS = definitionMap.get("customCSS");

		if (customCSS != null) {
			formStepContainerStyledLayoutStructureItem.setCustomCSS(
				String.valueOf(customCSS));
		}

		if (definitionMap.get("customCSSViewports") instanceof
				List<?> customCSSViewports) {

			for (Map<String, Object> customCSSViewport :
					(List<Map<String, Object>>)customCSSViewports) {

				formStepContainerStyledLayoutStructureItem.setCustomCSSViewport(
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

			formStepContainerStyledLayoutStructureItem.updateItemConfig(
				jsonObject);
		}

		if (definitionMap.get("fragmentViewports") instanceof
				List<?> fragmentViewports) {

			for (Map<String, Object> fragmentViewport :
					(List<Map<String, Object>>)fragmentViewports) {

				JSONObject jsonObject = JSONUtil.put(
					(String)fragmentViewport.get("id"),
					toFragmentViewportStylesJSONObject(fragmentViewport));

				formStepContainerStyledLayoutStructureItem.updateItemConfig(
					jsonObject);
			}
		}

		return formStepContainerStyledLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.FORM_STEP_CONTAINER;
	}

}