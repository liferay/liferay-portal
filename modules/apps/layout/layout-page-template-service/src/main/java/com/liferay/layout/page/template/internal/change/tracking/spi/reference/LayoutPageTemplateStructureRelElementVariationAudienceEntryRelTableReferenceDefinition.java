/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.change.tracking.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationTable;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = TableReferenceDefinition.class)
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTableReferenceDefinition
		implements TableReferenceDefinition
			<LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder
			<LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable>
				childTableReferenceInfoBuilder) {
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder
			<LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable>
				parentTableReferenceInfoBuilder) {

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelTable =
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable.INSTANCE;
		LayoutPageTemplateStructureRelElementVariationTable
			layoutPageTemplateStructureRelElementVariationTable =
				LayoutPageTemplateStructureRelElementVariationTable.INSTANCE;

		parentTableReferenceInfoBuilder.groupedModel(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelTable
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				layoutPageTemplateStructureRelElementVariationTable
			).innerJoinON(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelTable,
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelTable.layoutPageTemplateStructureRelElementVariationERC.
					eq(
						layoutPageTemplateStructureRelElementVariationTable.
							externalReferenceCode
					).and(
						layoutPageTemplateStructureRelElementVariationAudienceEntryRelTable.groupId.
							eq(
								layoutPageTemplateStructureRelElementVariationTable.groupId)
					)
			)
		);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence;
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable
		getTable() {

		return LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable.INSTANCE;
	}

	@Reference
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence;

}