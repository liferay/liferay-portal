/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.search;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = ModelSearchConfigurator.class)
public class FragmentCollectionModelSearchConfigurator
	implements ModelSearchConfigurator<FragmentCollection> {

	@Override
	public String getClassName() {
		return FragmentCollection.class.getName();
	}

	@Override
	public String[] getDefaultSelectedFieldNames() {
		return new String[] {
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.UID
		};
	}

	@Override
	public ModelIndexerWriterContributor<FragmentCollection>
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
			_fragmentCollectionLocalService::
				getIndexableActionableDynamicQuery);
	}

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	private ModelIndexerWriterContributor<FragmentCollection>
		_modelIndexWriterContributor;

}