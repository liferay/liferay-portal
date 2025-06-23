/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.internal.change.tracking.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.portal.kernel.model.ClassNameTable;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototypeTable;
import com.liferay.portal.kernel.model.LayoutSetTable;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetPrototypePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(service = TableReferenceDefinition.class)
public class LayoutSetPrototypeTableReferenceDefinition
	implements TableReferenceDefinition<LayoutSetPrototypeTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<LayoutSetPrototypeTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.referenceInnerJoin(
			fromStep -> fromStep.from(
				GroupTable.INSTANCE
			).innerJoinON(
				LayoutSetPrototypeTable.INSTANCE,
				LayoutSetPrototypeTable.INSTANCE.companyId.eq(
					GroupTable.INSTANCE.companyId
				).and(
					LayoutSetPrototypeTable.INSTANCE.layoutSetPrototypeId.eq(
						GroupTable.INSTANCE.classPK)
				)
			).innerJoinON(
				ClassNameTable.INSTANCE,
				ClassNameTable.INSTANCE.value.eq(
					LayoutSetPrototype.class.getName()
				).and(
					ClassNameTable.INSTANCE.classNameId.eq(
						GroupTable.INSTANCE.classNameId)
				)
			)
		).referenceInnerJoin(
			fromStep -> fromStep.from(
				LayoutSetTable.INSTANCE
			).innerJoinON(
				LayoutSetPrototypeTable.INSTANCE,
				LayoutSetPrototypeTable.INSTANCE.companyId.eq(
					LayoutSetTable.INSTANCE.companyId)
			).innerJoinON(
				GroupTable.INSTANCE,
				GroupTable.INSTANCE.companyId.eq(
					LayoutSetPrototypeTable.INSTANCE.companyId
				).and(
					GroupTable.INSTANCE.classPK.eq(
						LayoutSetPrototypeTable.INSTANCE.layoutSetPrototypeId)
				).and(
					GroupTable.INSTANCE.groupId.eq(
						LayoutSetTable.INSTANCE.groupId)
				)
			).innerJoinON(
				ClassNameTable.INSTANCE,
				ClassNameTable.INSTANCE.value.eq(
					LayoutSetPrototype.class.getName()
				).and(
					ClassNameTable.INSTANCE.classNameId.eq(
						GroupTable.INSTANCE.classNameId)
				)
			)
		).resourcePermissionReference(
			LayoutSetPrototypeTable.INSTANCE.layoutSetPrototypeId,
			LayoutSetPrototype.class
		);
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<LayoutSetPrototypeTable>
			parentTableReferenceInfoBuilder) {
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutSetPrototypePersistence;
	}

	@Override
	public LayoutSetPrototypeTable getTable() {
		return LayoutSetPrototypeTable.INSTANCE;
	}

	@Reference
	private LayoutSetPrototypePersistence _layoutSetPrototypePersistence;

}