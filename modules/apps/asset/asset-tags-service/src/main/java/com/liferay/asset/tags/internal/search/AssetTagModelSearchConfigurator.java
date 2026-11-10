/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.internal.search;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 * @author Lucas Marques
 */
@Component(service = ModelSearchConfigurator.class)
public class AssetTagModelSearchConfigurator
	implements ModelSearchConfigurator<AssetTag> {

	@Override
	public String getClassName() {
		return AssetTag.class.getName();
	}

	@Override
	public String[] getDefaultSelectedFieldNames() {
		return new String[] {
			Field.COMPANY_ID, Field.ENTRY_CLASS_PK, Field.ENTRY_CLASS_NAME,
			Field.GROUP_ID, Field.UID
		};
	}

	@Override
	public ModelIndexerWriterContributor<AssetTag>
		getModelIndexerWriterContributor() {

		return _modelIndexWriterContributor;
	}

	@Override
	public boolean isPermissionAware() {
		return false;
	}

	@Activate
	protected void activate() {
		_modelIndexWriterContributor = new ModelIndexerWriterContributor<>(
			_assetTagLocalService::getIndexableActionableDynamicQuery);
	}

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	private ModelIndexerWriterContributor<AssetTag>
		_modelIndexWriterContributor;

}