/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.change.tracking.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.document.library.kernel.model.DLFileEntryTable;
import com.liferay.fragment.model.FragmentCollectionTable;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLinkTable;
import com.liferay.fragment.model.FragmentEntryTable;
import com.liferay.fragment.service.persistence.FragmentEntryPersistence;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = TableReferenceDefinition.class)
public class FragmentEntryTableReferenceDefinition
	implements TableReferenceDefinition<FragmentEntryTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<FragmentEntryTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.singleColumnReference(
			FragmentEntryTable.INSTANCE.externalReferenceCode,
			FragmentEntryLinkTable.INSTANCE.fragmentEntryERC
		).singleColumnReference(
			FragmentEntryTable.INSTANCE.previewFileEntryId,
			DLFileEntryTable.INSTANCE.fileEntryId
		).resourcePermissionReference(
			FragmentEntryTable.INSTANCE.fragmentEntryId, FragmentEntry.class
		);
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<FragmentEntryTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.groupedModel(
			FragmentEntryTable.INSTANCE
		).singleColumnReference(
			FragmentEntryTable.INSTANCE.fragmentCollectionId,
			FragmentCollectionTable.INSTANCE.fragmentCollectionId
		).referenceInnerJoin(
			fromStep -> {
				FragmentEntryTable parentFragmentEntryTable =
					FragmentEntryTable.INSTANCE.as("parentFragmentEntryTable");

				return fromStep.from(
					parentFragmentEntryTable
				).innerJoinON(
					FragmentEntryTable.INSTANCE,
					FragmentEntryTable.INSTANCE.headId.eq(
						parentFragmentEntryTable.fragmentEntryId
					).and(
						FragmentEntryTable.INSTANCE.head.eq(false)
					)
				);
			}
		);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _fragmentEntryPersistence;
	}

	@Override
	public FragmentEntryTable getTable() {
		return FragmentEntryTable.INSTANCE;
	}

	@Reference
	private FragmentEntryPersistence _fragmentEntryPersistence;

}