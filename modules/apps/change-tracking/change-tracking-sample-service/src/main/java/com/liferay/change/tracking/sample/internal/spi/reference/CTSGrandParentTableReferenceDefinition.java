/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.internal.spi.reference;

import com.liferay.change.tracking.sample.model.CTSChildTable;
import com.liferay.change.tracking.sample.model.CTSGrandParentTable;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentPersistence;
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
public class CTSGrandParentTableReferenceDefinition
	implements TableReferenceDefinition<CTSGrandParentTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<CTSGrandParentTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.referenceInnerJoin(
			fromStep -> fromStep.from(
				CTSChildTable.INSTANCE
			).innerJoinON(
				CTSGrandParentTable.INSTANCE,
				CTSGrandParentTable.INSTANCE.ctsGrandParentId.eq(
					CTSChildTable.INSTANCE.ctsGrandParentId)
			));
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<CTSGrandParentTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.parentColumnReference(
			CTSGrandParentTable.INSTANCE.ctsGrandParentId,
			CTSGrandParentTable.INSTANCE.parentCTSGrandParentId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctsGrandParentPersistence;
	}

	@Override
	public CTSGrandParentTable getTable() {
		return CTSGrandParentTable.INSTANCE;
	}

	@Reference
	private CTSGrandParentPersistence _ctsGrandParentPersistence;

}