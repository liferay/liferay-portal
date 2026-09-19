/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountCommerceAccountGroupRelException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel;
import com.liferay.commerce.discount.service.base.CommerceDiscountCommerceAccountGroupRelLocalServiceBaseImpl;
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
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.discount.model.CommerceDiscountCommerceAccountGroupRel",
	service = AopService.class
)
public class CommerceDiscountCommerceAccountGroupRelLocalServiceImpl
	extends CommerceDiscountCommerceAccountGroupRelLocalServiceBaseImpl {

	@Override
	public CommerceDiscountCommerceAccountGroupRel
			addCommerceDiscountCommerceAccountGroupRel(
				long userId, long commerceDiscountId,
				long commerceAccountGroupId, ServiceContext serviceContext)
		throws PortalException {

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				commerceDiscountCommerceAccountGroupRelPersistence.
					fetchByCDI_CAGI(commerceDiscountId, commerceAccountGroupId);

		if (commerceDiscountCommerceAccountGroupRel != null) {
			throw new DuplicateCommerceDiscountCommerceAccountGroupRelException();
		}

		commerceDiscountCommerceAccountGroupRel =
			commerceDiscountCommerceAccountGroupRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commerceDiscountCommerceAccountGroupRel.setCompanyId(
			user.getCompanyId());
		commerceDiscountCommerceAccountGroupRel.setUserId(user.getUserId());
		commerceDiscountCommerceAccountGroupRel.setUserName(user.getFullName());

		commerceDiscountCommerceAccountGroupRel.setCommerceDiscountId(
			commerceDiscountId);
		commerceDiscountCommerceAccountGroupRel.setCommerceAccountGroupId(
			commerceAccountGroupId);

		commerceDiscountCommerceAccountGroupRel =
			commerceDiscountCommerceAccountGroupRelPersistence.update(
				commerceDiscountCommerceAccountGroupRel);

		_reindexCommerceDiscount(commerceDiscountId);

		return commerceDiscountCommerceAccountGroupRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceDiscountCommerceAccountGroupRel
			deleteCommerceDiscountCommerceAccountGroupRel(
				CommerceDiscountCommerceAccountGroupRel
					commerceDiscountCommerceAccountGroupRel)
		throws PortalException {

		commerceDiscountCommerceAccountGroupRelPersistence.remove(
			commerceDiscountCommerceAccountGroupRel);

		_expandoRowLocalService.deleteRows(
			commerceDiscountCommerceAccountGroupRel.
				getCommerceDiscountCommerceAccountGroupRelId());

		_reindexCommerceDiscount(
			commerceDiscountCommerceAccountGroupRel.getCommerceDiscountId());

		return commerceDiscountCommerceAccountGroupRel;
	}

	@Override
	public CommerceDiscountCommerceAccountGroupRel
			deleteCommerceDiscountCommerceAccountGroupRel(
				long commerceDiscountCommerceAccountGroupRelId)
		throws PortalException {

		CommerceDiscountCommerceAccountGroupRel
			commerceDiscountCommerceAccountGroupRel =
				commerceDiscountCommerceAccountGroupRelPersistence.
					findByPrimaryKey(commerceDiscountCommerceAccountGroupRelId);

		return commerceDiscountCommerceAccountGroupRelLocalService.
			deleteCommerceDiscountCommerceAccountGroupRel(
				commerceDiscountCommerceAccountGroupRel);
	}

	@Override
	public void
			deleteCommerceDiscountCommerceAccountGroupRelsByCommerceDiscountId(
				long commerceDiscountId)
		throws PortalException {

		List<CommerceDiscountCommerceAccountGroupRel>
			commerceDiscountCommerceAccountGroupRels =
				commerceDiscountCommerceAccountGroupRelPersistence.
					findByCommerceDiscountId(commerceDiscountId);

		for (CommerceDiscountCommerceAccountGroupRel
				commerceDiscountCommerceAccountGroupRel :
					commerceDiscountCommerceAccountGroupRels) {

			commerceDiscountCommerceAccountGroupRelLocalService.
				deleteCommerceDiscountCommerceAccountGroupRel(
					commerceDiscountCommerceAccountGroupRel);
		}
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public void
		deleteCommerceDiscountCommerceAccountGroupRelsBycommerceAccountGroupId(
			long commerceAccountGroupId) {

		commerceDiscountCommerceAccountGroupRelPersistence.
			removeByCommerceAccountGroupId(commerceAccountGroupId);
	}

	@Override
	public CommerceDiscountCommerceAccountGroupRel
		fetchCommerceDiscountCommerceAccountGroupRel(
			long commerceDiscountId, long commerceAccountGroupId) {

		return commerceDiscountCommerceAccountGroupRelPersistence.
			fetchByCDI_CAGI(commerceDiscountId, commerceAccountGroupId);
	}

	@Override
	public List<CommerceDiscountCommerceAccountGroupRel>
		getCommerceDiscountCommerceAccountGroupRels(
			long commerceDiscountId, int start, int end,
			OrderByComparator<CommerceDiscountCommerceAccountGroupRel>
				orderByComparator) {

		return commerceDiscountCommerceAccountGroupRelPersistence.
			findByCommerceDiscountId(
				commerceDiscountId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceDiscountCommerceAccountGroupRel>
		getCommerceDiscountCommerceAccountGroupRels(
			long commerceDiscountId, String name, int start, int end) {

		return commerceDiscountCommerceAccountGroupRelFinder.
			findByCommerceDiscountId(commerceDiscountId, name, start, end);
	}

	@Override
	public int getCommerceDiscountCommerceAccountGroupRelsCount(
		long commerceDiscountId) {

		return commerceDiscountCommerceAccountGroupRelPersistence.
			countByCommerceDiscountId(commerceDiscountId);
	}

	@Override
	public int getCommerceDiscountCommerceAccountGroupRelsCount(
		long commerceDiscountId, String name) {

		return commerceDiscountCommerceAccountGroupRelFinder.
			countByCommerceDiscountId(commerceDiscountId, name);
	}

	private void _reindexCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		Indexer<CommerceDiscount> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceDiscount.class);

		indexer.reindex(CommerceDiscount.class.getName(), commerceDiscountId);
	}

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private UserLocalService _userLocalService;

}