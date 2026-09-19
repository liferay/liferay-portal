/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchConfigurator;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;
import com.liferay.users.admin.internal.search.spi.model.index.contributor.UserModelIndexerWriterContributor;
import com.liferay.users.admin.internal.search.spi.model.result.contributor.UserModelSummaryContributor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(service = ModelSearchConfigurator.class)
public class UserModelSearchConfigurator
	implements ModelSearchConfigurator<User> {

	@Override
	public String getClassName() {
		return User.class.getName();
	}

	@Override
	public String[] getDefaultSelectedFieldNames() {
		return new String[] {
			Field.ASSET_TAG_NAMES, Field.COMPANY_ID, Field.ENTRY_CLASS_NAME,
			Field.ENTRY_CLASS_PK, Field.GROUP_ID, Field.MODIFIED_DATE,
			Field.SCOPE_GROUP_ID, Field.UID, Field.USER_ID
		};
	}

	@Override
	public ModelIndexerWriterContributor<User>
		getModelIndexerWriterContributor() {

		return _modelIndexWriterContributor;
	}

	@Override
	public ModelSummaryContributor getModelSummaryContributor() {
		return _modelSummaryContributor;
	}

	@Override
	public boolean isSearchResultPermissionFilterSuppressed() {
		return true;
	}

	@Activate
	protected void activate() {
		_modelIndexWriterContributor = new UserModelIndexerWriterContributor(
			_indexerDocumentBuilder, _indexWriterHelper, _userLocalService);
		_modelSummaryContributor = new UserModelSummaryContributor(
			_localization);
	}

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference(
		target = "(indexer.class.name=com.liferay.portal.kernel.model.Contact)"
	)
	private IndexerDocumentBuilder _indexerDocumentBuilder;

	@Reference
	private Localization _localization;

	private ModelIndexerWriterContributor<User> _modelIndexWriterContributor;
	private ModelSummaryContributor _modelSummaryContributor;

	@Reference
	private UserLocalService _userLocalService;

}