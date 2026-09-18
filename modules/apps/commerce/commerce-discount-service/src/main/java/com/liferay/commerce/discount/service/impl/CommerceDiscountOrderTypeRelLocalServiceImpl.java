/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.impl;

import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountOrderTypeRelException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel;
import com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRelTable;
import com.liferay.commerce.discount.service.base.CommerceDiscountOrderTypeRelLocalServiceBaseImpl;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.commerce.discount.model.CommerceDiscountOrderTypeRel",
	service = AopService.class
)
public class CommerceDiscountOrderTypeRelLocalServiceImpl
	extends CommerceDiscountOrderTypeRelLocalServiceBaseImpl {

	@Override
	public CommerceDiscountOrderTypeRel addCommerceDiscountOrderTypeRel(
			long userId, long commerceDiscountId, long commerceOrderTypeId,
			int priority, ServiceContext serviceContext)
		throws PortalException {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelPersistence.fetchByCDI_COTI(
				commerceDiscountId, commerceOrderTypeId);

		if (commerceDiscountOrderTypeRel != null) {
			throw new DuplicateCommerceDiscountOrderTypeRelException();
		}

		commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commerceDiscountOrderTypeRel.setCompanyId(user.getCompanyId());
		commerceDiscountOrderTypeRel.setUserId(user.getUserId());
		commerceDiscountOrderTypeRel.setUserName(user.getFullName());

		commerceDiscountOrderTypeRel.setCommerceDiscountId(commerceDiscountId);
		commerceDiscountOrderTypeRel.setCommerceOrderTypeId(
			commerceOrderTypeId);
		commerceDiscountOrderTypeRel.setPriority(priority);
		commerceDiscountOrderTypeRel.setExpandoBridgeAttributes(serviceContext);

		commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelPersistence.update(
				commerceDiscountOrderTypeRel);

		_reindexCommerceDiscount(commerceDiscountId);

		return commerceDiscountOrderTypeRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceDiscountOrderTypeRel deleteCommerceDiscountOrderTypeRel(
			CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel)
		throws PortalException {

		commerceDiscountOrderTypeRelPersistence.remove(
			commerceDiscountOrderTypeRel);

		_expandoRowLocalService.deleteRows(
			commerceDiscountOrderTypeRel.getCommerceDiscountOrderTypeRelId());

		_reindexCommerceDiscount(
			commerceDiscountOrderTypeRel.getCommerceDiscountId());

		return commerceDiscountOrderTypeRel;
	}

	@Override
	public CommerceDiscountOrderTypeRel deleteCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelPersistence.findByPrimaryKey(
				commerceDiscountOrderTypeRelId);

		return commerceDiscountOrderTypeRelLocalService.
			deleteCommerceDiscountOrderTypeRel(commerceDiscountOrderTypeRel);
	}

	@Override
	public void deleteCommerceDiscountOrderTypeRels(long commerceDiscountId) {
		commerceDiscountOrderTypeRelPersistence.removeByCommerceDiscountId(
			commerceDiscountId);
	}

	@Override
	public CommerceDiscountOrderTypeRel fetchCommerceDiscountOrderTypeRel(
		long commerceDiscountId, long commerceOrderTypeId) {

		return commerceDiscountOrderTypeRelPersistence.fetchByCDI_COTI(
			commerceDiscountId, commerceOrderTypeId);
	}

	@Override
	public CommerceDiscountOrderTypeRel getCommerceDiscountOrderTypeRel(
			long commerceDiscountOrderTypeRelId)
		throws PortalException {

		return commerceDiscountOrderTypeRelPersistence.findByPrimaryKey(
			commerceDiscountOrderTypeRelId);
	}

	@Override
	public List<CommerceDiscountOrderTypeRel> getCommerceDiscountOrderTypeRels(
		long commerceDiscountId) {

		return commerceDiscountOrderTypeRelPersistence.findByCommerceDiscountId(
			commerceDiscountId);
	}

	@Override
	public List<CommerceDiscountOrderTypeRel> getCommerceDiscountOrderTypeRels(
			long commerceDiscountId, String name, int start, int end,
			OrderByComparator<CommerceDiscountOrderTypeRel> orderByComparator)
		throws PortalException {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceDiscountOrderTypeRelTable.INSTANCE),
				commerceDiscountId, name
			).orderBy(
				CommerceDiscountOrderTypeRelTable.INSTANCE, orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceDiscountOrderTypeRelsCount(
			long commerceDiscountId, String name)
		throws PortalException {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceDiscountOrderTypeRelTable.INSTANCE.
						commerceDiscountOrderTypeRelId),
				commerceDiscountId, name));
	}

	@Override
	public CommerceDiscountOrderTypeRel updatePriority(
			long commerceDiscountOrderTypeRelId, int priority)
		throws PortalException {

		CommerceDiscountOrderTypeRel commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelPersistence.findByPrimaryKey(
				commerceDiscountOrderTypeRelId);

		commerceDiscountOrderTypeRel.setPriority(priority);

		commerceDiscountOrderTypeRel =
			commerceDiscountOrderTypeRelPersistence.update(
				commerceDiscountOrderTypeRel);

		_reindexCommerceDiscount(
			commerceDiscountOrderTypeRel.getCommerceDiscountId());

		return commerceDiscountOrderTypeRel;
	}

	private GroupByStep _getGroupByStep(
			FromStep fromStep, Long commerceDiscountId, String keywords)
		throws PortalException {

		JoinStep joinStep = fromStep.from(
			CommerceDiscountOrderTypeRelTable.INSTANCE
		).innerJoinON(
			CommerceOrderTypeTable.INSTANCE,
			CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
				CommerceDiscountOrderTypeRelTable.INSTANCE.commerceOrderTypeId)
		);

		return joinStep.where(
			() -> {
				Predicate predicate =
					CommerceDiscountOrderTypeRelTable.INSTANCE.
						commerceDiscountId.eq(commerceDiscountId);

				if (Validator.isNotNull(keywords)) {
					predicate = predicate.and(
						Predicate.withParentheses(
							_customSQL.getKeywordsPredicate(
								DSLFunctionFactoryUtil.lower(
									CommerceOrderTypeTable.INSTANCE.name),
								_customSQL.keywords(keywords, true))));
				}

				return predicate;
			});
	}

	private void _reindexCommerceDiscount(long commerceDiscountId)
		throws PortalException {

		Indexer<CommerceDiscount> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceDiscount.class);

		indexer.reindex(CommerceDiscount.class.getName(), commerceDiscountId);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private UserLocalService _userLocalService;

}