/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.exception.DuplicateCommercePriceListChannelRelException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListChannelRelLocalServiceBaseImpl;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 * @see CommercePriceListChannelRelLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.commerce.price.list.model.CommercePriceListChannelRel",
	service = AopService.class
)
public class CommercePriceListChannelRelLocalServiceImpl
	extends CommercePriceListChannelRelLocalServiceBaseImpl {

	@Override
	public CommercePriceListChannelRel addCommercePriceListChannelRel(
			long userId, long commercePriceListId, long commerceChannelId,
			int order, ServiceContext serviceContext)
		throws PortalException {

		CommercePriceListChannelRel commercePriceListChannelRel =
			commercePriceListChannelRelPersistence.fetchByCCI_CPI(
				commerceChannelId, commercePriceListId);

		if (commercePriceListChannelRel != null) {
			throw new DuplicateCommercePriceListChannelRelException();
		}

		commercePriceListChannelRel =
			commercePriceListChannelRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commercePriceListChannelRel.setCompanyId(user.getCompanyId());
		commercePriceListChannelRel.setUserId(user.getUserId());
		commercePriceListChannelRel.setUserName(user.getFullName());

		commercePriceListChannelRel.setCommerceChannelId(commerceChannelId);
		commercePriceListChannelRel.setCommercePriceListId(commercePriceListId);
		commercePriceListChannelRel.setOrder(order);
		commercePriceListChannelRel.setExpandoBridgeAttributes(serviceContext);

		commercePriceListChannelRel =
			commercePriceListChannelRelPersistence.update(
				commercePriceListChannelRel);

		_reindexCommercePriceList(commercePriceListId);

		return commercePriceListChannelRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceListChannelRel deleteCommercePriceListChannelRel(
			CommercePriceListChannelRel commercePriceListChannelRel)
		throws PortalException {

		commercePriceListChannelRelPersistence.remove(
			commercePriceListChannelRel);

		_expandoRowLocalService.deleteRows(
			commercePriceListChannelRel.getCommercePriceListChannelRelId());

		_reindexCommercePriceList(
			commercePriceListChannelRel.getCommercePriceListId());

		return commercePriceListChannelRel;
	}

	@Override
	public CommercePriceListChannelRel deleteCommercePriceListChannelRel(
			long commercePriceListChannelRelId)
		throws PortalException {

		CommercePriceListChannelRel commercePriceListChannelRel =
			commercePriceListChannelRelPersistence.findByPrimaryKey(
				commercePriceListChannelRelId);

		return commercePriceListChannelRelLocalService.
			deleteCommercePriceListChannelRel(commercePriceListChannelRel);
	}

	@Override
	public void deleteCommercePriceListChannelRels(long commercePriceListId)
		throws PortalException {

		List<CommercePriceListChannelRel> commercePriceListChannelRels =
			commercePriceListChannelRelPersistence.findByCommercePriceListId(
				commercePriceListId);

		for (CommercePriceListChannelRel commercePriceListChannelRel :
				commercePriceListChannelRels) {

			commercePriceListChannelRelLocalService.
				deleteCommercePriceListChannelRel(commercePriceListChannelRel);
		}
	}

	@Override
	public CommercePriceListChannelRel fetchCommercePriceListChannelRel(
		long commerceChannelId, long commercePriceListId) {

		return commercePriceListChannelRelPersistence.fetchByCCI_CPI(
			commerceChannelId, commercePriceListId);
	}

	@Override
	public List<CommercePriceListChannelRel> getCommercePriceListChannelRels(
		long commercePriceListId) {

		return commercePriceListChannelRelPersistence.findByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public List<CommercePriceListChannelRel> getCommercePriceListChannelRels(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceListChannelRel> orderByComparator) {

		return commercePriceListChannelRelPersistence.findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceListChannelRel> getCommercePriceListChannelRels(
		long commercePriceListId, String name, int start, int end) {

		return commercePriceListChannelRelFinder.findByCommercePriceListId(
			commercePriceListId, name, start, end);
	}

	@Override
	public int getCommercePriceListChannelRelsCount(long commercePriceListId) {
		return commercePriceListChannelRelPersistence.
			countByCommercePriceListId(commercePriceListId);
	}

	private void _reindexCommercePriceList(long commercePriceListId)
		throws PortalException {

		Indexer<CommercePriceList> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceList.class);

		indexer.reindex(CommercePriceList.class.getName(), commercePriceListId);
	}

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private UserLocalService _userLocalService;

}