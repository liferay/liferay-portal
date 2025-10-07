/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.RowPageElementDefinition;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.constants.StyledLayoutStructureConstants;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SetUtil;

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

		RowPageElementDefinition rowPageElementDefinition =
			(RowPageElementDefinition)pageElement.getPageElementDefinition();

		if (rowPageElementDefinition == null) {
			return rowStyledLayoutStructureItem;
		}

		rowStyledLayoutStructureItem.setCssClasses(
			SetUtil.fromArray(rowPageElementDefinition.getCssClasses()));
		rowStyledLayoutStructureItem.setCustomCSS(
			rowPageElementDefinition.getCustomCSS());
		rowStyledLayoutStructureItem.setGutters(
			GetterUtil.getBoolean(
				rowPageElementDefinition.getGutters(), Boolean.TRUE));
		rowStyledLayoutStructureItem.setIndexed(
			GetterUtil.getBoolean(
				rowPageElementDefinition.getIndexed(), Boolean.TRUE));
		rowStyledLayoutStructureItem.setModulesPerRow(
			GetterUtil.getInteger(rowPageElementDefinition.getModulesPerRow()));
		rowStyledLayoutStructureItem.setName(
			rowPageElementDefinition.getName());
		rowStyledLayoutStructureItem.setNumberOfColumns(
			GetterUtil.getInteger(
				rowPageElementDefinition.getNumberOfColumns()));
		rowStyledLayoutStructureItem.setReverseOrder(
			GetterUtil.getBoolean(rowPageElementDefinition.getReverseOrder()));
		rowStyledLayoutStructureItem.setVerticalAlignment(
			GetterUtil.getString(
				rowPageElementDefinition.getVerticalAlignment(),
				StyledLayoutStructureConstants.VERTICAL_ALIGNMENT_TOP));

		return rowStyledLayoutStructureItem;
	}

}