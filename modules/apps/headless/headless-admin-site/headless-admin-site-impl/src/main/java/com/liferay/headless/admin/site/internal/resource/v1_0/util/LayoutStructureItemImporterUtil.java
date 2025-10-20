/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.CollectionItemLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.CollectionLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.ColumnLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.ContainerLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.DropZoneLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.FormLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.FormStepContainerLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.FormStepItemLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.FragmentDropZoneLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.FragmentLayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.LayoutStructureItemImporter;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.RowLayoutStructureItemImporter;

import java.util.EnumMap;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutStructureItemImporterUtil {

	public static LayoutStructureItemImporter getLayoutStructureItemImporter(
		PageElementDefinition pageElementDefinition) {

		return _layoutStructureItemImporters.get(
			pageElementDefinition.getType());
	}

	private static final EnumMap
		<PageElementDefinition.Type, LayoutStructureItemImporter>
			_layoutStructureItemImporters;

	static {
		_layoutStructureItemImporters = new EnumMap<>(
			PageElementDefinition.Type.class);

		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.COLLECTION,
			new CollectionLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.COLLECTION_ITEM,
			new CollectionItemLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.CONTAINER,
			new ContainerLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.DROP_ZONE,
			new DropZoneLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.FORM,
			new FormLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.FORM_STEP,
			new FormStepItemLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.FORM_STEP_CONTAINER,
			new FormStepContainerLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.FRAGMENT,
			new FragmentLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.FRAGMENT_DROP_ZONE,
			new FragmentDropZoneLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.GRID,
			new RowLayoutStructureItemImporter());
		_layoutStructureItemImporters.put(
			PageElementDefinition.Type.MODULE,
			new ColumnLayoutStructureItemImporter());
	}

}