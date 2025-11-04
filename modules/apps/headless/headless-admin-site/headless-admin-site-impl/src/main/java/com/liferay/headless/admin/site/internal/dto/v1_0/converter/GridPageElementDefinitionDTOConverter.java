/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.GridViewport;
import com.liferay.headless.admin.site.dto.v1_0.GridViewportDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ViewportIdUtil;
import com.liferay.layout.converter.VerticalAlignmentConverter;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.RowStyledLayoutStructureItem",
	service = DTOConverter.class
)
public class GridPageElementDefinitionDTOConverter
	implements DTOConverter
		<RowStyledLayoutStructureItem, GridPageElementDefinition> {

	@Override
	public String getContentType() {
		return GridPageElementDefinition.class.getSimpleName();
	}

	@Override
	public GridPageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			RowStyledLayoutStructureItem rowStyledLayoutStructureItem)
		throws Exception {

		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if (scopeGroupId == null) {
			throw new UnsupportedOperationException();
		}

		GridPageElementDefinition gridPageElementDefinition =
			new GridPageElementDefinition();

		gridPageElementDefinition.setCssClasses(
			() -> {
				Set<String> cssClasses =
					rowStyledLayoutStructureItem.getCssClasses();

				if (SetUtil.isEmpty(cssClasses)) {
					return null;
				}

				return ArrayUtil.toStringArray(cssClasses);
			});
		gridPageElementDefinition.setCustomCSS(
			() -> {
				String customCSS = rowStyledLayoutStructureItem.getCustomCSS();

				if (Validator.isNotNull(customCSS)) {
					return customCSS;
				}

				return null;
			});
		gridPageElementDefinition.setGridViewports(
			() -> _toGridViewports(rowStyledLayoutStructureItem));
		gridPageElementDefinition.setGutters(
			rowStyledLayoutStructureItem::isGutters);
		gridPageElementDefinition.setIndexed(
			rowStyledLayoutStructureItem::isIndexed);
		gridPageElementDefinition.setModulesPerRow(
			rowStyledLayoutStructureItem::getModulesPerRow);
		gridPageElementDefinition.setName(
			rowStyledLayoutStructureItem::getName);
		gridPageElementDefinition.setNumberOfModules(
			rowStyledLayoutStructureItem::getNumberOfColumns);
		gridPageElementDefinition.setReverseOrder(
			rowStyledLayoutStructureItem::isReverseOrder);
		gridPageElementDefinition.setVerticalAlignment(
			() -> {
				String itemVerticalAlignment =
					rowStyledLayoutStructureItem.getVerticalAlignment();

				if (Validator.isNull(itemVerticalAlignment)) {
					return null;
				}

				return GridPageElementDefinition.VerticalAlignment.create(
					VerticalAlignmentConverter.convertToExternalValue(
						itemVerticalAlignment));
			});

		return gridPageElementDefinition;
	}

	private GridViewport _toGridViewport(
		GridViewport.Id gridViewportId, JSONObject jsonObject) {

		String viewportId = ViewportIdUtil.toInternalValue(
			gridViewportId.getValue());

		if (!jsonObject.has(viewportId)) {
			return null;
		}

		JSONObject viewportJSONObject = jsonObject.getJSONObject(viewportId);

		if (JSONUtil.isEmpty(viewportJSONObject)) {
			return null;
		}

		return new GridViewport() {
			{
				setCustomCSS(() -> viewportJSONObject.getString("customCSS"));
				setGridViewportDefinition(
					() -> _toGridViewportDefinition(viewportJSONObject));
				setId(() -> gridViewportId);
			}
		};
	}

	private GridViewportDefinition _toGridViewportDefinition(
		JSONObject rowViewportConfigurationJSONObject) {

		return new GridViewportDefinition() {
			{
				setModulesPerRow(
					() -> {
						if (!rowViewportConfigurationJSONObject.has(
								"modulesPerRow")) {

							return null;
						}

						return rowViewportConfigurationJSONObject.getInt(
							"modulesPerRow");
					});
				setVerticalAlignment(
					() -> {
						String itemVerticalAlignment =
							rowViewportConfigurationJSONObject.getString(
								"verticalAlignment");

						if (Validator.isNull(itemVerticalAlignment)) {
							return null;
						}

						return VerticalAlignment.create(
							VerticalAlignmentConverter.convertToExternalValue(
								itemVerticalAlignment));
					});
			}
		};
	}

	private GridViewport[] _toGridViewports(
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem) {

		List<GridViewport> gridViewports = new ArrayList<>() {
			{
				GridViewport gridViewport = _toGridViewport(
					GridViewport.Id.LANDSCAPE_MOBILE,
					rowStyledLayoutStructureItem.getItemConfigJSONObject());

				if (gridViewport != null) {
					add(gridViewport);
				}

				gridViewport = _toGridViewport(
					GridViewport.Id.PORTRAIT_MOBILE,
					rowStyledLayoutStructureItem.getItemConfigJSONObject());

				if (gridViewport != null) {
					add(gridViewport);
				}

				gridViewport = _toGridViewport(
					GridViewport.Id.TABLET,
					rowStyledLayoutStructureItem.getItemConfigJSONObject());

				if (gridViewport != null) {
					add(gridViewport);
				}
			}
		};

		return gridViewports.toArray(new GridViewport[0]);
	}

}