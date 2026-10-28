/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.change.tracking.spi.reference;

import com.liferay.asset.list.model.AssetListEntryAssetEntryRelTable;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRelTable;
import com.liferay.asset.list.model.AssetListEntryTable;
import com.liferay.asset.list.service.persistence.AssetListEntryAudienceEntryRelPersistence;
import com.liferay.change.tracking.spi.reference.TableReferenceDefinition;
import com.liferay.change.tracking.spi.reference.builder.ChildTableReferenceInfoBuilder;
import com.liferay.change.tracking.spi.reference.builder.ParentTableReferenceInfoBuilder;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = TableReferenceDefinition.class)
public class AssetListEntryAudienceEntryRelTableReferenceDefinition
	implements TableReferenceDefinition<AssetListEntryAudienceEntryRelTable> {

	@Override
	public void defineChildTableReferences(
		ChildTableReferenceInfoBuilder<AssetListEntryAudienceEntryRelTable>
			childTableReferenceInfoBuilder) {

		childTableReferenceInfoBuilder.referenceInnerJoin(
			fromStep -> fromStep.from(
				AssetListEntryAssetEntryRelTable.INSTANCE
			).innerJoinON(
				AssetListEntryAudienceEntryRelTable.INSTANCE,
				AssetListEntryAudienceEntryRelTable.INSTANCE.assetListEntryId.
					eq(
						AssetListEntryAssetEntryRelTable.INSTANCE.
							assetListEntryId
					).and(
						AssetListEntryAudienceEntryRelTable.INSTANCE.
							audienceEntryERC.eq(
								AssetListEntryAssetEntryRelTable.INSTANCE.
									audienceEntryERC)
					)
			));
	}

	@Override
	public void defineParentTableReferences(
		ParentTableReferenceInfoBuilder<AssetListEntryAudienceEntryRelTable>
			parentTableReferenceInfoBuilder) {

		parentTableReferenceInfoBuilder.groupedModel(
			AssetListEntryAudienceEntryRelTable.INSTANCE
		).singleColumnReference(
			AssetListEntryAudienceEntryRelTable.INSTANCE.assetListEntryId,
			AssetListEntryTable.INSTANCE.assetListEntryId
		);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetListEntryAudienceEntryRelPersistence;
	}

	@Override
	public AssetListEntryAudienceEntryRelTable getTable() {
		return AssetListEntryAudienceEntryRelTable.INSTANCE;
	}

	@Reference
	private AssetListEntryAudienceEntryRelPersistence
		_assetListEntryAudienceEntryRelPersistence;

}