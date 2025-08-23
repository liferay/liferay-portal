/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.search.spi.model.index.contributor;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

/**
 * @author Roselaine Marques
 */
public class LayoutPageTemplateCollectionModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<LayoutPageTemplateCollection> {

	public LayoutPageTemplateCollectionModelIndexerWriterContributor(
		DynamicQueryBatchIndexingActionableFactory
			dynamicQueryBatchIndexingActionableFactory,
		LayoutPageTemplateCollectionLocalService
			layoutPageTemplateCollectionLocalService) {

		_dynamicQueryBatchIndexingActionableFactory =
			dynamicQueryBatchIndexingActionableFactory;
		_layoutPageTemplateCollectionLocalService =
			layoutPageTemplateCollectionLocalService;
	}

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		batchIndexingActionable.setPerformActionMethod(
			(LayoutPageTemplateCollection layoutPageTemplateCollection) ->
				batchIndexingActionable.addDocuments(
					modelIndexerWriterDocumentHelper.getDocument(
						layoutPageTemplateCollection)));
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return _dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				_layoutPageTemplateCollectionLocalService.
					getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		return layoutPageTemplateCollection.getCompanyId();
	}

	private final DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;
	private final LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

}