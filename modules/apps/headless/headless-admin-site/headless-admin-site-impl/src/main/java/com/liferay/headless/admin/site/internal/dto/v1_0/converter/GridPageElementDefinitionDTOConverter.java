/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.GridViewport;
import com.liferay.headless.admin.site.dto.v1_0.GridViewportDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportStyleUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ImageValueUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ViewportIdUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
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
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.util.structure.RowStyledLayoutStructureItem"
	},
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

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		return new GridPageElementDefinition() {
			{
				setBackgroundImageValue(
					() -> ImageValueUtil.toBackgroundImageValue(
						companyId, dtoConverterContext,
						_infoItemServiceRegistry,
						rowStyledLayoutStructureItem.
							getBackgroundImageJSONObject(),
						scopeGroupId));
				setCssClasses(
					() -> {
						Set<String> cssClasses =
							rowStyledLayoutStructureItem.getCssClasses();

						if (SetUtil.isEmpty(cssClasses)) {
							return null;
						}

						return ArrayUtil.toStringArray(cssClasses);
					});
				setGridViewports(
					() -> _toGridViewports(rowStyledLayoutStructureItem));
				setGutters(rowStyledLayoutStructureItem::isGutters);
				setIndexed(rowStyledLayoutStructureItem::isIndexed);
				setName(rowStyledLayoutStructureItem::getName);
				setNumberOfModules(
					rowStyledLayoutStructureItem::getNumberOfColumns);
				setReverseOrder(rowStyledLayoutStructureItem::isReverseOrder);
				setType(() -> PageElementDefinition.Type.GRID);
			}
		};
	}

	private JSONObject _getViewportJSONObject(
		GridViewport.Id gridViewportId, JSONObject jsonObject) {

		if (Objects.equals(gridViewportId, GridViewport.Id.DESKTOP)) {
			return jsonObject;
		}

		String viewportId = ViewportIdUtil.toInternalValue(
			gridViewportId.getValue());

		if (!jsonObject.has(viewportId)) {
			return null;
		}

		return jsonObject.getJSONObject(viewportId);
	}

	private GridViewport _toGridViewport(
		GridViewport.Id gridViewportId, JSONObject jsonObject) {

		JSONObject viewportJSONObject = _getViewportJSONObject(
			gridViewportId, jsonObject);

		if (JSONUtil.isEmpty(viewportJSONObject)) {
			return null;
		}

		return new GridViewport() {
			{
				setCustomCSS(() -> viewportJSONObject.getString("customCSS"));
				setFragmentViewportStyle(
					() -> FragmentViewportStyleUtil.toFragmentViewportStyle(
						viewportJSONObject.getJSONObject("styles")));
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
					GridViewport.Id.DESKTOP,
					rowStyledLayoutStructureItem.getItemConfigJSONObject());

				if (gridViewport != null) {
					add(gridViewport);
				}

				gridViewport = _toGridViewport(
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

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}