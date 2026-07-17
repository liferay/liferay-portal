/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jürgen Kappler
 */
public class RowLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)
				layoutStructure.addLayoutStructureItem(
					layoutStructureItemImporterContext.getItemId(pageElement),
					LayoutDataItemTypeConstants.TYPE_ROW,
					layoutStructureItemImporterContext.getParentItemId(),
					layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return rowStyledLayoutStructureItem;
		}

		if (definitionMap.get("cssClasses") instanceof List<?> cssClasses) {
			rowStyledLayoutStructureItem.setCssClasses(
				new HashSet<>((List<String>)cssClasses));
		}

		Object customCSS = definitionMap.get("customCSS");

		if (customCSS != null) {
			rowStyledLayoutStructureItem.setCustomCSS(
				String.valueOf(customCSS));
		}

		if (definitionMap.get("customCSSViewports") instanceof
				List<?> customCSSViewports) {

			for (Map<String, Object> customCSSViewport :
					(List<Map<String, Object>>)customCSSViewports) {

				rowStyledLayoutStructureItem.setCustomCSSViewport(
					(String)customCSSViewport.get("id"),
					(String)customCSSViewport.get("customCSS"));
			}
		}

		rowStyledLayoutStructureItem.setGutters(
			(Boolean)definitionMap.get("gutters"));

		Object indexed = definitionMap.get("indexed");

		if (indexed != null) {
			rowStyledLayoutStructureItem.setIndexed(
				GetterUtil.getBoolean(indexed));
		}

		String name = GetterUtil.getString(definitionMap.get("name"), null);

		if (name != null) {
			rowStyledLayoutStructureItem.setName(name);
		}

		rowStyledLayoutStructureItem.setNumberOfColumns(
			(Integer)definitionMap.get("numberOfColumns"));

		if (definitionMap.containsKey("reverseOrder")) {
			rowStyledLayoutStructureItem.setModulesPerRow(
				(Integer)definitionMap.get("modulesPerRow"));
			rowStyledLayoutStructureItem.setReverseOrder(
				(Boolean)definitionMap.get("reverseOrder"));
		}

		if (definitionMap.get("verticalAlignment") instanceof
				String verticalAlignment) {

			rowStyledLayoutStructureItem.setVerticalAlignment(
				verticalAlignment);
		}

		if (definitionMap.get("rowViewports") instanceof List<?> rowViewports) {
			for (Map<String, Object> rowViewport :
					(List<Map<String, Object>>)rowViewports) {

				_processRowViewportDefinition(
					rowStyledLayoutStructureItem,
					(Map<String, Object>)rowViewport.get(
						"rowViewportDefinition"),
					(String)rowViewport.get("id"));
			}
		}
		else {
			if (definitionMap.get("rowViewportConfig") instanceof
					Map<?, ?> rowViewportConfigurations) {

				Map<String, Object> rowViewportConfigurationsMap =
					(Map<String, Object>)rowViewportConfigurations;

				for (Map.Entry<String, Object> entry :
						rowViewportConfigurationsMap.entrySet()) {

					_processRowViewportDefinition(
						rowStyledLayoutStructureItem,
						(Map<String, Object>)entry.getValue(), entry.getKey());
				}
			}
		}

		Map<String, Object> fragmentStyleMap =
			(Map<String, Object>)definitionMap.get("fragmentStyle");

		if (fragmentStyleMap != null) {
			JSONObject jsonObject = JSONUtil.put(
				"styles",
				toStylesJSONObject(
					layoutStructureItemImporterContext, fragmentStyleMap));

			rowStyledLayoutStructureItem.updateItemConfig(jsonObject);
		}

		if (definitionMap.get("fragmentViewports") instanceof
				List<?> fragmentViewports) {

			for (Map<String, Object> fragmentViewport :
					(List<Map<String, Object>>)fragmentViewports) {

				JSONObject jsonObject = JSONUtil.put(
					(String)fragmentViewport.get("id"),
					toFragmentViewportStylesJSONObject(fragmentViewport));

				rowStyledLayoutStructureItem.updateItemConfig(jsonObject);
			}
		}

		return rowStyledLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.ROW;
	}

	private void _processRowViewportDefinition(
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem,
		Map<String, Object> rowViewportDefinitionMap, String rowViewportId) {

		rowStyledLayoutStructureItem.setViewportConfiguration(
			rowViewportId,
			JSONUtil.put(
				"modulesPerRow",
				() -> {
					if (!rowViewportDefinitionMap.containsKey(
							"modulesPerRow")) {

						return null;
					}

					return GetterUtil.getInteger(
						rowViewportDefinitionMap.get("modulesPerRow"));
				}
			).put(
				"reverseOrder",
				() -> {
					if (!rowViewportDefinitionMap.containsKey("reverseOrder")) {
						return null;
					}

					return GetterUtil.getBoolean(
						rowViewportDefinitionMap.get("reverseOrder"));
				}
			).put(
				"verticalAlignment",
				() -> {
					if (!rowViewportDefinitionMap.containsKey(
							"verticalAlignment")) {

						return null;
					}

					return GetterUtil.getString(
						rowViewportDefinitionMap.get("verticalAlignment"));
				}
			));
	}

}