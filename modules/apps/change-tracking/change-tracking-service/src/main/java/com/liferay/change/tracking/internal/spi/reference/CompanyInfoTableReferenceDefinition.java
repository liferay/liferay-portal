/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.spi.reference;

import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.portal.kernel.model.CompanyInfoTable;
import com.liferay.portal.kernel.model.CompanyTable;
import com.liferay.portal.kernel.model.ImageTable;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.CompanyInfoPersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author István András Dézsi
 */
@Component(service = TableReferenceDefinition.class)
public class CompanyInfoTableReferenceDefinition
	implements TableReferenceDefinition<CompanyInfoTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<CompanyInfoTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.singleColumnReference(
			CompanyInfoTable.INSTANCE.logoId, ImageTable.INSTANCE.imageId);
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<CompanyInfoTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.singleColumnReference(
			CompanyInfoTable.INSTANCE.companyId,
			CompanyTable.INSTANCE.companyId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _companyInfoPersistence;
	}

	@Override
	public CompanyInfoTable getTable() {
		return CompanyInfoTable.INSTANCE;
	}

	@Reference
	private CompanyInfoPersistence _companyInfoPersistence;

}