/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.base.CommercePriceListDiscountRelLocalServiceBaseImpl;
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
 * @see CommercePriceListDiscountRelLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.commerce.price.list.model.CommercePriceListDiscountRel",
	service = AopService.class
)
public class CommercePriceListDiscountRelLocalServiceImpl
	extends CommercePriceListDiscountRelLocalServiceBaseImpl {

	@Override
	public CommercePriceListDiscountRel addCommercePriceListDiscountRel(
			long userId, long commercePriceListId, long commerceDiscountId,
			int order, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			commercePriceListDiscountRelPersistence.create(
				counterLocalService.increment());

		commercePriceListDiscountRel.setCompanyId(user.getCompanyId());
		commercePriceListDiscountRel.setUserId(user.getUserId());
		commercePriceListDiscountRel.setUserName(user.getFullName());
		commercePriceListDiscountRel.setCommerceDiscountId(commerceDiscountId);
		commercePriceListDiscountRel.setCommercePriceListId(
			commercePriceListId);
		commercePriceListDiscountRel.setOrder(order);
		commercePriceListDiscountRel.setExpandoBridgeAttributes(serviceContext);

		_reindexPriceList(commercePriceListId);

		return commercePriceListDiscountRelPersistence.update(
			commercePriceListDiscountRel);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceListDiscountRel deleteCommercePriceListDiscountRel(
			CommercePriceListDiscountRel commercePriceListDiscountRel)
		throws PortalException {

		commercePriceListDiscountRelPersistence.remove(
			commercePriceListDiscountRel);

		_expandoRowLocalService.deleteRows(
			commercePriceListDiscountRel.getCommercePriceListDiscountRelId());

		_reindexPriceList(
			commercePriceListDiscountRel.getCommercePriceListId());

		return commercePriceListDiscountRel;
	}

	@Override
	public CommercePriceListDiscountRel deleteCommercePriceListDiscountRel(
			long commercePriceListDiscountRelId)
		throws PortalException {

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			commercePriceListDiscountRelPersistence.findByPrimaryKey(
				commercePriceListDiscountRelId);

		return commercePriceListDiscountRelLocalService.
			deleteCommercePriceListDiscountRel(commercePriceListDiscountRel);
	}

	@Override
	public void deleteCommercePriceListDiscountRels(long commercePriceListId)
		throws PortalException {

		for (CommercePriceListDiscountRel commercePriceListDiscountRel :
				commercePriceListDiscountRelPersistence.
					findByCommercePriceListId(commercePriceListId)) {

			commercePriceListDiscountRelLocalService.
				deleteCommercePriceListDiscountRel(
					commercePriceListDiscountRel);
		}
	}

	@Override
	public CommercePriceListDiscountRel fetchCommercePriceListDiscountRel(
		long commerceDiscountId, long commercePriceListId) {

		return commercePriceListDiscountRelPersistence.fetchByCDI_CPI(
			commerceDiscountId, commercePriceListId);
	}

	@Override
	public List<CommercePriceListDiscountRel> getCommercePriceListDiscountRels(
		long commercePriceListId) {

		return commercePriceListDiscountRelPersistence.
			findByCommercePriceListId(commercePriceListId);
	}

	@Override
	public List<CommercePriceListDiscountRel> getCommercePriceListDiscountRels(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceListDiscountRel> orderByComparator) {

		return commercePriceListDiscountRelPersistence.
			findByCommercePriceListId(
				commercePriceListId, start, end, orderByComparator);
	}

	@Override
	public int getCommercePriceListDiscountRelsCount(long commercePriceListId) {
		return commercePriceListDiscountRelPersistence.
			countByCommercePriceListId(commercePriceListId);
	}

	private void _reindexPriceList(long commercePriceListId)
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