/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.model.listener;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.exception.CommerceAccountOrdersException;
import com.liferay.commerce.internal.search.CommerceOrderBatchReindexer;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShippingOptionAccountEntryRelLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.indexer.IndexerWriter;
import com.liferay.portal.util.PortalInstances;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 * @author Alessio Antonio Rendina
 */
@Component(service = ModelListener.class)
public class AccountEntryModelListener extends BaseModelListener<AccountEntry> {

	@Override
	public void onAfterUpdate(
			AccountEntry originalAccountEntry, AccountEntry accountEntry)
		throws ModelListenerException {

		if (StringUtil.equals(
				originalAccountEntry.getName(), accountEntry.getName())) {

			return;
		}

		_commerceOrderBatchReindexer.reindex(
			accountEntry.getAccountEntryId(), accountEntry.getCompanyId());
	}

	@Override
	public void onBeforeRemove(AccountEntry accountEntry)
		throws ModelListenerException {

		if (PortalInstances.isCurrentCompanyInDeletionProcess()) {
			try {
				for (CommerceOrder commerceOrder :
						_commerceOrderLocalService.
							getCommerceOrdersByCommerceAccountId(
								accountEntry.getAccountEntryId(),
								QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

					_commerceOrderLocalService.deleteCommerceOrder(
						commerceOrder);
				}
			}
			catch (PortalException portalException) {
				throw new ModelListenerException(portalException);
			}
		}
		else {
			int accountOrders =
				_commerceOrderLocalService.
					getCommerceOrdersCountByCommerceAccountId(
						accountEntry.getAccountEntryId());

			if (accountOrders > 0) {
				throw new CommerceAccountOrdersException();
			}
		}

		_commerceShippingOptionAccountEntryRelLocalService.
			deleteCommerceShippingOptionAccountEntryRelsByAccountEntryId(
				accountEntry.getAccountEntryId());
	}

	@Activate
	protected void activate() {
		_commerceOrderBatchReindexer = new CommerceOrderBatchReindexer(
			_indexerDocumentBuilder, _indexerWriter);
	}

	private CommerceOrderBatchReindexer _commerceOrderBatchReindexer;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceShippingOptionAccountEntryRelLocalService
		_commerceShippingOptionAccountEntryRelLocalService;

	@Reference(
		target = "(indexer.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private IndexerDocumentBuilder _indexerDocumentBuilder;

	@Reference(
		target = "(indexer.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private IndexerWriter<CommerceOrder> _indexerWriter;

}