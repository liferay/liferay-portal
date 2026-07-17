/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jürgen Kappler
 */
public class ColumnLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		ColumnLayoutStructureItem columnLayoutStructureItem =
			(ColumnLayoutStructureItem)
				layoutStructure.addColumnLayoutStructureItem(
					layoutStructureItemImporterContext.getItemId(pageElement),
					layoutStructureItemImporterContext.getParentItemId(),
					layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return columnLayoutStructureItem;
		}

		columnLayoutStructureItem.setSize((Integer)definitionMap.get("size"));

		if (definitionMap.get("columnViewports") instanceof
				List<?> columnViewports) {

			for (Map<String, Object> columnViewport :
					(List<Map<String, Object>>)columnViewports) {

				_processColumnViewportDefinition(
					columnLayoutStructureItem,
					(Map<String, Object>)columnViewport.get(
						"columnViewportDefinition"),
					(String)columnViewport.get("id"));
			}
		}
		else {
			if (definitionMap.get("columnViewportConfig") instanceof
					Map<?, ?> columnViewportConfigurations) {

				Map<String, Object> columnViewportConfigurationsMap =
					(Map<String, Object>)columnViewportConfigurations;

				for (Map.Entry<String, Object> entry :
						columnViewportConfigurationsMap.entrySet()) {

					_processColumnViewportDefinition(
						columnLayoutStructureItem,
						(Map<String, Object>)entry.getValue(), entry.getKey());
				}
			}
		}

		return columnLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.COLUMN;
	}

	private void _processColumnViewportDefinition(
		ColumnLayoutStructureItem columnLayoutStructureItem,
		Map<String, Object> columnViewportDefinitionMap,
		String columnViewportId) {

		columnLayoutStructureItem.setViewportConfiguration(
			columnViewportId,
			JSONUtil.put(
				"size",
				() -> {
					Map.Entry<String, Object> sizeEntry = MapUtil.getEntry(
						columnViewportDefinitionMap, "size");

					if (sizeEntry == null) {
						return null;
					}

					return GetterUtil.getInteger(sizeEntry.getValue());
				}));
	}

}