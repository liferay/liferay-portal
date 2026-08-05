/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.change.tracking.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationTable;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationPersistence;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.segments.model.SegmentsExperienceTable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = TableReferenceDefinition.class)
public class
	LayoutPageTemplateStructureRelElementVariationTableReferenceDefinition
		implements TableReferenceDefinition
			<LayoutPageTemplateStructureRelElementVariationTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder
			<LayoutPageTemplateStructureRelElementVariationTable>
				childTableReferenceInfoBuilder) {
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder
			<LayoutPageTemplateStructureRelElementVariationTable>
				parentTableReferenceInfoBuilder) {

		LayoutPageTemplateStructureRelElementVariationTable
			layoutPageTemplateStructureRelElementVariationTable =
				LayoutPageTemplateStructureRelElementVariationTable.INSTANCE;

		parentTableReferenceInfoBuilder.groupedModel(
			layoutPageTemplateStructureRelElementVariationTable
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				LayoutTable.INSTANCE
			).innerJoinON(
				layoutPageTemplateStructureRelElementVariationTable,
				layoutPageTemplateStructureRelElementVariationTable.groupId.eq(
					LayoutTable.INSTANCE.groupId
				).and(
					layoutPageTemplateStructureRelElementVariationTable.plid.eq(
						LayoutTable.INSTANCE.plid)
				)
			)
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				SegmentsExperienceTable.INSTANCE
			).innerJoinON(
				layoutPageTemplateStructureRelElementVariationTable,
				layoutPageTemplateStructureRelElementVariationTable.
					segmentsExperienceERC.eq(
						SegmentsExperienceTable.INSTANCE.externalReferenceCode
					).and(
						layoutPageTemplateStructureRelElementVariationTable.
							groupId.eq(SegmentsExperienceTable.INSTANCE.groupId)
					)
			)
		);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutPageTemplateStructureRelElementVariationPersistence;
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationTable getTable() {
		return LayoutPageTemplateStructureRelElementVariationTable.INSTANCE;
	}

	@Reference
	private LayoutPageTemplateStructureRelElementVariationPersistence
		_layoutPageTemplateStructureRelElementVariationPersistence;

}