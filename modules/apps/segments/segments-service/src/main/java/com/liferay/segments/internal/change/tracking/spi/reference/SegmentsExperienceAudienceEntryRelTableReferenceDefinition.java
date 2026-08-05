/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.change.tracking.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRelTable;
import com.liferay.segments.model.SegmentsExperienceTable;
import com.liferay.segments.service.persistence.SegmentsExperienceAudienceEntryRelPersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = TableReferenceDefinition.class)
public class SegmentsExperienceAudienceEntryRelTableReferenceDefinition
	implements TableReferenceDefinition
		<SegmentsExperienceAudienceEntryRelTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<SegmentsExperienceAudienceEntryRelTable>
			childTableReferenceInfoBuilder) {
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<SegmentsExperienceAudienceEntryRelTable>
			parentTableReferenceInfoBuilder) {

		SegmentsExperienceAudienceEntryRelTable
			segmentsExperienceAudienceEntryRelTable =
				SegmentsExperienceAudienceEntryRelTable.INSTANCE;

		parentTableReferenceInfoBuilder.groupedModel(
			segmentsExperienceAudienceEntryRelTable
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				SegmentsExperienceTable.INSTANCE
			).innerJoinON(
				segmentsExperienceAudienceEntryRelTable,
				segmentsExperienceAudienceEntryRelTable.segmentsExperienceERC.
					eq(
						SegmentsExperienceTable.INSTANCE.externalReferenceCode
					).and(
						segmentsExperienceAudienceEntryRelTable.groupId.eq(
							SegmentsExperienceTable.INSTANCE.groupId)
					)
			)
		);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _segmentsExperienceAudienceEntryRelPersistence;
	}

	@Override
	public SegmentsExperienceAudienceEntryRelTable getTable() {
		return SegmentsExperienceAudienceEntryRelTable.INSTANCE;
	}

	@Reference
	private SegmentsExperienceAudienceEntryRelPersistence
		_segmentsExperienceAudienceEntryRelPersistence;

}