/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.search;

import com.liferay.layout.page.template.internal.search.spi.model.index.contributor.LayoutPageTemplateEntryModelIndexerWriterContributor;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Pablo Montero
 */
@Component(service = ModelSearchConfigurator.class)
public class LayoutPageTemplateEntryModelSearchConfigurator
	implements ModelSearchConfigurator<LayoutPageTemplateEntry> {

	@Override
	public String getClassName() {
		return LayoutPageTemplateEntry.class.getName();
	}

	@Override
	public long getCompanyId() {
		return ModelSearchConfigurator.super.getCompanyId();
	}

	@Override
	public String[] getDefaultSelectedFieldNames() {
		return new String[] {
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.UID
		};
	}

	@Override
	public ModelIndexerWriterContributor<LayoutPageTemplateEntry>
		getModelIndexerWriterContributor() {

		return _modelIndexWriterContributor;
	}

	@Activate
	protected void activate() {
		_modelIndexWriterContributor =
			new LayoutPageTemplateEntryModelIndexerWriterContributor(
				_dynamicQueryBatchIndexingActionableFactory,
				_layoutPageTemplateEntryLocalService);
	}

	@Reference
	private DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	private ModelIndexerWriterContributor<LayoutPageTemplateEntry>
		_modelIndexWriterContributor;

}