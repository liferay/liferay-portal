/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.internal.spi.reference;

import com.liferay.change.tracking.sample.model.CTSChildTable;
import com.liferay.change.tracking.sample.model.CTSGrandParentTable;
import com.liferay.change.tracking.sample.service.persistence.CTSChildPersistence;
import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = TableReferenceDefinition.class)
public class CTSChildTableReferenceDefinition
	implements TableReferenceDefinition<CTSChildTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<CTSChildTable>
			childTableReferenceInfoBuilder) {
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<CTSChildTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.referenceInnerJoin(
			fromStep -> fromStep.from(
				CTSGrandParentTable.INSTANCE
			).innerJoinON(
				CTSChildTable.INSTANCE,
				CTSChildTable.INSTANCE.ctsGrandParentId.eq(
					CTSGrandParentTable.INSTANCE.ctsGrandParentId)
			)
		).referenceInnerJoin(
			fromStep -> {
				CTSChildTable aliasCTSChildTable = CTSChildTable.INSTANCE.as(
					"aliasCTSChildTable");

				return fromStep.from(
					aliasCTSChildTable
				).innerJoinON(
					CTSChildTable.INSTANCE,
					CTSChildTable.INSTANCE.parentCTSChildId.eq(
						aliasCTSChildTable.ctsChildId)
				);
			}
		);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctsChildPersistence;
	}

	@Override
	public CTSChildTable getTable() {
		return CTSChildTable.INSTANCE;
	}

	@Reference
	private CTSChildPersistence _ctsChildPersistence;

}