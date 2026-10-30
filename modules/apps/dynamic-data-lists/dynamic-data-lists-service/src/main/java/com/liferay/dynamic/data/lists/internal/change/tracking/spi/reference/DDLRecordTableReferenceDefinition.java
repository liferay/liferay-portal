/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.internal.change.tracking.spi.reference;

import com.liferay.asset.kernel.model.AssetEntryTable;
import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.dynamic.data.lists.model.DDLFormRecord;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSetTable;
import com.liferay.dynamic.data.lists.model.DDLRecordTable;
import com.liferay.dynamic.data.lists.model.DDLRecordVersionTable;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordPersistence;
import com.liferay.dynamic.data.mapping.model.DDMFieldTable;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkTable;
import com.liferay.portal.kernel.model.ClassNameTable;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = TableReferenceDefinition.class)
public class DDLRecordTableReferenceDefinition
	implements TableReferenceDefinition<DDLRecordTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<DDLRecordTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.assetEntryReference(
			DDLRecordTable.INSTANCE.recordId, DDLRecord.class
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				AssetEntryTable.INSTANCE
			).innerJoinON(
				DDLRecordTable.INSTANCE,
				DDLRecordTable.INSTANCE.recordId.eq(
					AssetEntryTable.INSTANCE.classPK)
			).innerJoinON(
				ClassNameTable.INSTANCE,
				ClassNameTable.INSTANCE.value.eq(
					DDLFormRecord.class.getName()
				).and(
					ClassNameTable.INSTANCE.classNameId.eq(
						AssetEntryTable.INSTANCE.classNameId)
				)
			)
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				DDMFieldTable.INSTANCE
			).innerJoinON(
				DDLRecordTable.INSTANCE,
				DDLRecordTable.INSTANCE.DDMStorageId.eq(
					DDMFieldTable.INSTANCE.storageId)
			).innerJoinON(
				DDLRecordVersionTable.INSTANCE,
				DDLRecordVersionTable.INSTANCE.recordId.eq(
					DDLRecordTable.INSTANCE.recordId)
			)
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				DDMStorageLinkTable.INSTANCE
			).innerJoinON(
				DDLRecordTable.INSTANCE,
				DDLRecordTable.INSTANCE.DDMStorageId.eq(
					DDMStorageLinkTable.INSTANCE.classPK)
			).innerJoinON(
				DDLRecordVersionTable.INSTANCE,
				DDLRecordVersionTable.INSTANCE.recordId.eq(
					DDLRecordTable.INSTANCE.recordId)
			)
		).singleColumnReference(
			DDLRecordTable.INSTANCE.recordId,
			DDLRecordVersionTable.INSTANCE.recordId
		);
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<DDLRecordTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.groupedModel(
			DDLRecordTable.INSTANCE
		).singleColumnReference(
			DDLRecordTable.INSTANCE.recordSetId,
			DDLRecordSetTable.INSTANCE.recordSetId
		);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddlRecordPersistence;
	}

	@Override
	public DDLRecordTable getTable() {
		return DDLRecordTable.INSTANCE;
	}

	@Reference
	private DDLRecordPersistence _ddlRecordPersistence;

}