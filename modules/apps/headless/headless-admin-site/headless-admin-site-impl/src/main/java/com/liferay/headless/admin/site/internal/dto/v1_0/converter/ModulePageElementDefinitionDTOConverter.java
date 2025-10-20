/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ModulePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.ModuleViewport;
import com.liferay.headless.admin.site.dto.v1_0.ModuleViewportDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ViewportIdUtil;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.ColumnLayoutStructureItem",
	service = DTOConverter.class
)
public class ModulePageElementDefinitionDTOConverter
	implements DTOConverter
		<ColumnLayoutStructureItem, ModulePageElementDefinition> {

	@Override
	public String getContentType() {
		return ModulePageElementDefinition.class.getSimpleName();
	}

	@Override
	public ModulePageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			ColumnLayoutStructureItem columnLayoutStructureItem)
		throws Exception {

		return new ModulePageElementDefinition() {
			{
				setModuleViewports(
					() -> _toModuleViewports(columnLayoutStructureItem));
				setSize(columnLayoutStructureItem::getSize);
				setType(Type.MODULE);
			}
		};
	}

	private ModuleViewport _toModuleViewport(
		Map<String, JSONObject> columnViewportConfigurationJSONObjects,
		ModuleViewport.Id moduleViewportId) {

		String viewportId = ViewportIdUtil.toInternalValue(
			moduleViewportId.getValue());

		if (!columnViewportConfigurationJSONObjects.containsKey(viewportId)) {
			return null;
		}

		JSONObject columnViewportConfigurationJSONObject =
			columnViewportConfigurationJSONObjects.get(viewportId);

		if (JSONUtil.isEmpty(columnViewportConfigurationJSONObject)) {
			return null;
		}

		return new ModuleViewport() {
			{
				setId(() -> moduleViewportId);
				setModuleViewportDefinition(
					() -> _toModuleViewportDefinition(
						columnViewportConfigurationJSONObject));
			}
		};
	}

	private ModuleViewportDefinition _toModuleViewportDefinition(
		JSONObject columnViewportConfigurationJSONObject) {

		return new ModuleViewportDefinition() {
			{
				setSize(
					() -> {
						if (!columnViewportConfigurationJSONObject.has(
								"size")) {

							return null;
						}

						return columnViewportConfigurationJSONObject.getInt(
							"size");
					});
			}
		};
	}

	private ModuleViewport[] _toModuleViewports(
		ColumnLayoutStructureItem columnLayoutStructureItem) {

		Map<String, JSONObject> columnViewportConfigurationJSONObjects =
			columnLayoutStructureItem.getViewportConfigurationJSONObjects();

		if (MapUtil.isEmpty(columnViewportConfigurationJSONObjects)) {
			return null;
		}

		List<ModuleViewport> moduleViewports = new ArrayList<>() {
			{
				ModuleViewport moduleViewport = _toModuleViewport(
					columnViewportConfigurationJSONObjects,
					ModuleViewport.Id.LANDSCAPE_MOBILE);

				if (moduleViewport != null) {
					add(moduleViewport);
				}

				moduleViewport = _toModuleViewport(
					columnViewportConfigurationJSONObjects,
					ModuleViewport.Id.PORTRAIT_MOBILE);

				if (moduleViewport != null) {
					add(moduleViewport);
				}

				moduleViewport = _toModuleViewport(
					columnViewportConfigurationJSONObjects,
					ModuleViewport.Id.TABLET);

				if (moduleViewport != null) {
					add(moduleViewport);
				}
			}
		};

		return moduleViewports.toArray(new ModuleViewport[0]);
	}

}