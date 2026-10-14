/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.exception.DuplicateCommercePriceListAccountRelException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListAccountRelLocalServiceBaseImpl;
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
 * @author Ethan Bustad
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.price.list.model.CommercePriceListAccountRel",
	service = AopService.class
)
public class CommercePriceListAccountRelLocalServiceImpl
	extends CommercePriceListAccountRelLocalServiceBaseImpl {

	@Override
	public CommercePriceListAccountRel addCommercePriceListAccountRel(
			long userId, long commercePriceListId, long commerceAccountId,
			int order, ServiceContext serviceContext)
		throws PortalException {

		CommercePriceListAccountRel commercePriceListAccountRel =
			commercePriceListAccountRelPersistence.fetchByCAI_CPI(
				commerceAccountId, commercePriceListId);

		if (commercePriceListAccountRel != null) {
			throw new DuplicateCommercePriceListAccountRelException();
		}

		commercePriceListAccountRel =
			commercePriceListAccountRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commercePriceListAccountRel.setCompanyId(user.getCompanyId());
		commercePriceListAccountRel.setUserId(user.getUserId());
		commercePriceListAccountRel.setUserName(user.getFullName());

		commercePriceListAccountRel.setCommerceAccountId(commerceAccountId);
		commercePriceListAccountRel.setCommercePriceListId(commercePriceListId);
		commercePriceListAccountRel.setOrder(order);
		commercePriceListAccountRel.setExpandoBridgeAttributes(serviceContext);

		commercePriceListAccountRel =
			commercePriceListAccountRelPersistence.update(
				commercePriceListAccountRel);

		_reindexCommercePriceList(commercePriceListId);

		return commercePriceListAccountRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			CommercePriceListAccountRel commercePriceListAccountRel)
		throws PortalException {

		commercePriceListAccountRelPersistence.remove(
			commercePriceListAccountRel);

		_expandoRowLocalService.deleteRows(
			commercePriceListAccountRel.getCommercePriceListAccountRelId());

		_reindexCommercePriceList(
			commercePriceListAccountRel.getCommercePriceListId());

		return commercePriceListAccountRel;
	}

	@Override
	public CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws PortalException {

		CommercePriceListAccountRel commercePriceListAccountRel =
			commercePriceListAccountRelPersistence.findByPrimaryKey(
				commercePriceListAccountRelId);

		return commercePriceListAccountRelLocalService.
			deleteCommercePriceListAccountRel(commercePriceListAccountRel);
	}

	@Override
	public void deleteCommercePriceListAccountRels(long commercePriceListId)
		throws PortalException {

		List<CommercePriceListAccountRel> commercePriceListAccountRels =
			commercePriceListAccountRelPersistence.findByCommercePriceListId(
				commercePriceListId);

		for (CommercePriceListAccountRel commercePriceListAccountRel :
				commercePriceListAccountRels) {

			commercePriceListAccountRelLocalService.
				deleteCommercePriceListAccountRel(commercePriceListAccountRel);
		}
	}

	@Override
	public void deleteCommercePriceListAccountRelsByCommercePriceListId(
			long commercePriceListId)
		throws PortalException {

		List<CommercePriceListAccountRel> commercePriceListAccountRels =
			commercePriceListAccountRelPersistence.findByCommercePriceListId(
				commercePriceListId);

		for (CommercePriceListAccountRel commercePriceListAccountRel :
				commercePriceListAccountRels) {

			commercePriceListAccountRelLocalService.
				deleteCommercePriceListAccountRel(commercePriceListAccountRel);
		}
	}

	@Override
	public CommercePriceListAccountRel fetchCommercePriceListAccountRel(
		long commerceAccountId, long commercePriceListId) {

		return commercePriceListAccountRelPersistence.fetchByCAI_CPI(
			commerceAccountId, commercePriceListId);
	}

	@Override
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		long commercePriceListId) {

		return commercePriceListAccountRelPersistence.findByCommercePriceListId(
			commercePriceListId);
	}

	@Override
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceListAccountRel> orderByComparator) {

		return commercePriceListAccountRelPersistence.findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public List<CommercePriceListAccountRel> getCommercePriceListAccountRels(
		long commercePriceListId, String name, int start, int end) {

		return commercePriceListAccountRelFinder.findByCommercePriceListId(
			commercePriceListId, name, start, end);
	}

	@Override
	public int getCommercePriceListAccountRelsCount(long commercePriceListId) {
		return commercePriceListAccountRelPersistence.
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