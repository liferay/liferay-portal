/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.headless.admin.site.dto.v1_0.GridPageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.GridViewport;
import com.liferay.headless.admin.site.dto.v1_0.GridViewportDefinition;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ViewportIdUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.layout.converter.VerticalAlignmentConverter;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.constants.StyledLayoutStructureConstants;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class RowLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement)
		throws Exception {

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)
				layoutStructure.addLayoutStructureItem(
					pageElement.getExternalReferenceCode(),
					LayoutDataItemTypeConstants.TYPE_ROW,
					LayoutStructureUtil.getParentExternalReferenceCode(
						pageElement, layoutStructure),
					pageElement.getPosition());

		GridPageElementDefinition gridPageElementDefinition =
			(GridPageElementDefinition)pageElement.getPageElementDefinition();

		if (gridPageElementDefinition == null) {
			return rowStyledLayoutStructureItem;
		}

		rowStyledLayoutStructureItem.setCssClasses(
			_getCssClasses(gridPageElementDefinition.getCssClasses()));
		rowStyledLayoutStructureItem.setCustomCSS(
			gridPageElementDefinition.getCustomCSS());
		rowStyledLayoutStructureItem.setGutters(
			GetterUtil.getBoolean(
				gridPageElementDefinition.getGutters(), Boolean.TRUE));
		rowStyledLayoutStructureItem.setIndexed(
			GetterUtil.getBoolean(
				gridPageElementDefinition.getIndexed(), Boolean.TRUE));
		rowStyledLayoutStructureItem.setModulesPerRow(
			GetterUtil.getInteger(
				gridPageElementDefinition.getModulesPerRow(), 1));
		rowStyledLayoutStructureItem.setName(
			gridPageElementDefinition.getName());
		rowStyledLayoutStructureItem.setNumberOfColumns(
			GetterUtil.getInteger(
				gridPageElementDefinition.getNumberOfModules(), 1));

		GridViewport[] gridViewports =
			gridPageElementDefinition.getGridViewports();

		if (ArrayUtil.isEmpty(gridViewports)) {
			rowStyledLayoutStructureItem.setViewportConfiguration(
				ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
				JSONUtil.put("modulesPerRow", 1));
		}
		else {
			_setViewportConfiguration(
				JSONUtil.put("modulesPerRow", 1),
				GridViewport.Id.LANDSCAPE_MOBILE, gridViewports,
				rowStyledLayoutStructureItem);
			_setViewportConfiguration(
				JSONFactoryUtil.createJSONObject(),
				GridViewport.Id.PORTRAIT_MOBILE, gridViewports,
				rowStyledLayoutStructureItem);
			_setViewportConfiguration(
				JSONFactoryUtil.createJSONObject(), GridViewport.Id.TABLET,
				gridViewports, rowStyledLayoutStructureItem);
		}

		rowStyledLayoutStructureItem.setVerticalAlignment(
			VerticalAlignmentConverter.convertToInternalValue(
				GetterUtil.getString(
					gridPageElementDefinition.getVerticalAlignmentAsString(),
					StyledLayoutStructureConstants.VERTICAL_ALIGNMENT_TOP)));

		return rowStyledLayoutStructureItem;
	}

	private LinkedHashSet<String> _getCssClasses(String[] cssClasses) {
		if (cssClasses == null) {
			return null;
		}

		return new LinkedHashSet<>(Arrays.asList(cssClasses));
	}

	private GridViewport _getGridViewport(
		GridViewport.Id gridViewportId, GridViewport[] gridViewports) {

		for (GridViewport gridViewport : gridViewports) {
			if (Objects.equals(gridViewportId, gridViewport.getId())) {
				return gridViewport;
			}
		}

		return null;
	}

	private void _setViewportConfiguration(
		JSONObject defaultViewportJSONObject, GridViewport.Id gridViewportId,
		GridViewport[] gridViewports,
		RowStyledLayoutStructureItem rowStyledLayoutStructureItem) {

		GridViewport gridViewport = _getGridViewport(
			gridViewportId, gridViewports);

		String viewportId = ViewportIdUtil.toInternalValue(
			gridViewportId.getValue());

		if (gridViewport != null) {
			rowStyledLayoutStructureItem.setViewportConfiguration(
				viewportId, _toViewportJSONObject(gridViewport));
		}
		else {
			rowStyledLayoutStructureItem.setViewportConfiguration(
				viewportId, defaultViewportJSONObject);
		}
	}

	private JSONObject _toViewportJSONObject(GridViewport gridViewport) {
		if (gridViewport == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		GridViewportDefinition gridViewportDefinition =
			gridViewport.getGridViewportDefinition();

		if (Validator.isNull(gridViewport.getCustomCSS()) &&
			((gridViewportDefinition == null) ||
			 ((gridViewportDefinition.getModulesPerRow() == null) &&
			  (gridViewportDefinition.getVerticalAlignment() == null)))) {

			return JSONFactoryUtil.createJSONObject();
		}

		return JSONUtil.put(
			"customCSS", gridViewport::getCustomCSS
		).put(
			"modulesPerRow",
			() -> {
				if (gridViewportDefinition == null) {
					return null;
				}

				return gridViewportDefinition.getModulesPerRow();
			}
		).put(
			"verticalAlignment",
			() -> {
				if (gridViewportDefinition == null) {
					return null;
				}

				GridViewportDefinition.VerticalAlignment verticalAlignment =
					gridViewportDefinition.getVerticalAlignment();

				if (verticalAlignment == null) {
					return null;
				}

				return VerticalAlignmentConverter.convertToInternalValue(
					verticalAlignment.getValue());
			}
		);
	}

}