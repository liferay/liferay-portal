/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.change.tracking.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.depot.model.DepotEntryPinTable;
import com.liferay.depot.model.DepotEntryTable;
import com.liferay.depot.service.persistence.DepotEntryPinPersistence;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = TableReferenceDefinition.class)
public class DepotEntryPinTableReferenceDefinition
	implements TableReferenceDefinition<DepotEntryPinTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<DepotEntryPinTable>
			childTableReferenceInfoBuilder) {
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<DepotEntryPinTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.singleColumnReference(
			DepotEntryPinTable.INSTANCE.depotEntryId,
			DepotEntryTable.INSTANCE.depotEntryId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _depotEntryPinPersistence;
	}

	@Override
	public DepotEntryPinTable getTable() {
		return DepotEntryPinTable.INSTANCE;
	}

	@Reference
	private DepotEntryPinPersistence _depotEntryPinPersistence;

}