/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.bulk.rest.dto.v1_0.TaxonomyCategoryBulkSelection;
import com.liferay.bulk.rest.internal.util.DocumentBulkSelectionUtil;
import com.liferay.bulk.rest.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.bulk.selection.BulkSelectionFactoryRegistry;
import com.liferay.bulk.selection.BulkSelectionInputParameters;
import com.liferay.bulk.selection.BulkSelectionRunner;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alejandro Tardín
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/taxonomy-category.properties",
	scope = ServiceScope.PROTOTYPE, service = TaxonomyCategoryResource.class
)
@CTAware
public class TaxonomyCategoryResourceImpl
	extends BaseTaxonomyCategoryResourceImpl {

	@Override
	public void patchTaxonomyCategoryBatch(
			TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection)
		throws Exception {

		_update(true, taxonomyCategoryBulkSelection);
	}

	@Override
	public void putTaxonomyCategoryBatch(
			TaxonomyCategoryBulkSelection documentSelection)
		throws Exception {

		_update(false, documentSelection);
	}

	private void _update(
			boolean append,
			TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection)
		throws Exception {

		BulkSelection<?> bulkSelection = DocumentBulkSelectionUtil.create(
			_bulkSelectionFactoryRegistry,
			taxonomyCategoryBulkSelection.getDocumentBulkSelection());

		_bulkSelectionRunner.run(
			contextUser, bulkSelection.toAssetEntryBulkSelection(),
			_editCategoriesBulkSelectionAction,
			HashMapBuilder.<String, Serializable>put(
				BulkSelectionInputParameters.ASSET_ENTRY_BULK_SELECTION, true
			).put(
				"append", append
			).put(
				"toAddCategoryIds",
				taxonomyCategoryBulkSelection.getTaxonomyCategoryIdsToAdd()
			).put(
				"toRemoveCategoryIds",
				taxonomyCategoryBulkSelection.getTaxonomyCategoryIdsToRemove()
			).build());
	}

	@Reference
	private BulkSelectionFactoryRegistry _bulkSelectionFactoryRegistry;

	@Reference
	private BulkSelectionRunner _bulkSelectionRunner;

	@Reference(target = "(bulk.selection.action.key=edit.categories)")
	private BulkSelectionAction<AssetEntry> _editCategoriesBulkSelectionAction;

}