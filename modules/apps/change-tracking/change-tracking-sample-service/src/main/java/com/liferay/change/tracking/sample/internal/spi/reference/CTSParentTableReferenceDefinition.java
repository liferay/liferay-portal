/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.internal.spi.reference;

import com.liferay.change.tracking.sample.model.CTSChildTable;
import com.liferay.change.tracking.sample.model.CTSGrandParentTable;
import com.liferay.change.tracking.sample.model.CTSParentTable;
import com.liferay.change.tracking.sample.service.persistence.CTSParentPersistence;
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
public class CTSParentTableReferenceDefinition
	implements TableReferenceDefinition<CTSParentTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<CTSParentTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.referenceInnerJoin(
			fromStep -> fromStep.from(
				CTSChildTable.INSTANCE
			).innerJoinON(
				CTSParentTable.INSTANCE,
				CTSParentTable.INSTANCE.ctsGrandParentId.eq(
					CTSChildTable.INSTANCE.ctsGrandParentId
				).and(
					CTSParentTable.INSTANCE.name.eq(
						CTSChildTable.INSTANCE.ctsParentName)
				)
			));
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<CTSParentTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.referenceInnerJoin(
			fromStep -> fromStep.from(
				CTSGrandParentTable.INSTANCE
			).innerJoinON(
				CTSParentTable.INSTANCE,
				CTSParentTable.INSTANCE.ctsGrandParentId.eq(
					CTSGrandParentTable.INSTANCE.ctsGrandParentId)
			));
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctsParentPersistence;
	}

	@Override
	public CTSParentTable getTable() {
		return CTSParentTable.INSTANCE;
	}

	@Reference
	private CTSParentPersistence _ctsParentPersistence;

}