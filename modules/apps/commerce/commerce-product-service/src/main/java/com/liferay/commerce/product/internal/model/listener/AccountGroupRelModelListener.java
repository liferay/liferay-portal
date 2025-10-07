/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.model.listener;

import com.liferay.account.model.AccountGroupRel;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.ClassNameLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian I. Kim
 */
@Component(service = ModelListener.class)
public class AccountGroupRelModelListener
	extends BaseModelListener<AccountGroupRel> {

	@Override
	public void onAfterRemove(AccountGroupRel accountGroupRel)
		throws ModelListenerException {

		if (accountGroupRel.getClassNameId() ==
				_classNameLocalService.getClassNameId(
					CPDefinition.class.getName())) {

			_reindexCPDefinition(accountGroupRel.getClassPK());
		}
	}

	private void _reindexCPDefinition(long cpDefinitionId) {
		try {
			Indexer<CPDefinition> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(CPDefinition.class);

			indexer.reindex(CPDefinition.class.getName(), cpDefinitionId);
		}
		catch (SearchException searchException) {
			throw new ModelListenerException(searchException);
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

}