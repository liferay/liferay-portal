/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.impl;

import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.price.list.exception.DuplicateCommercePriceListOrderTypeRelException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRelTable;
import com.liferay.commerce.price.list.service.base.CommercePriceListOrderTypeRelLocalServiceBaseImpl;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel",
	service = AopService.class
)
public class CommercePriceListOrderTypeRelLocalServiceImpl
	extends CommercePriceListOrderTypeRelLocalServiceBaseImpl {

	@Override
	public CommercePriceListOrderTypeRel addCommercePriceListOrderTypeRel(
			long userId, long commercePriceListId, long commerceOrderTypeId,
			int priority, ServiceContext serviceContext)
		throws PortalException {

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			commercePriceListOrderTypeRelPersistence.fetchByCPI_COTI(
				commercePriceListId, commerceOrderTypeId);

		if (commercePriceListOrderTypeRel != null) {
			throw new DuplicateCommercePriceListOrderTypeRelException();
		}

		commercePriceListOrderTypeRel =
			commercePriceListOrderTypeRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commercePriceListOrderTypeRel.setCompanyId(user.getCompanyId());
		commercePriceListOrderTypeRel.setUserId(user.getUserId());
		commercePriceListOrderTypeRel.setUserName(user.getFullName());

		commercePriceListOrderTypeRel.setCommercePriceListId(
			commercePriceListId);
		commercePriceListOrderTypeRel.setCommerceOrderTypeId(
			commerceOrderTypeId);
		commercePriceListOrderTypeRel.setPriority(priority);
		commercePriceListOrderTypeRel.setExpandoBridgeAttributes(
			serviceContext);

		commercePriceListOrderTypeRel =
			commercePriceListOrderTypeRelPersistence.update(
				commercePriceListOrderTypeRel);

		_reindexCommercePriceList(commercePriceListId);

		return commercePriceListOrderTypeRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePriceListOrderTypeRel deleteCommercePriceListOrderTypeRel(
			CommercePriceListOrderTypeRel commercePriceListOrderTypeRel)
		throws PortalException {

		commercePriceListOrderTypeRelPersistence.remove(
			commercePriceListOrderTypeRel);

		_expandoRowLocalService.deleteRows(
			commercePriceListOrderTypeRel.getCommercePriceListOrderTypeRelId());

		_reindexCommercePriceList(
			commercePriceListOrderTypeRel.getCommercePriceListId());

		return commercePriceListOrderTypeRel;
	}

	@Override
	public CommercePriceListOrderTypeRel deleteCommercePriceListOrderTypeRel(
			long commercePriceListOrderTypeRelId)
		throws PortalException {

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			commercePriceListOrderTypeRelPersistence.findByPrimaryKey(
				commercePriceListOrderTypeRelId);

		return commercePriceListOrderTypeRelLocalService.
			deleteCommercePriceListOrderTypeRel(commercePriceListOrderTypeRel);
	}

	@Override
	public void deleteCommercePriceListOrderTypeRels(long commercePriceListId)
		throws PortalException {

		for (CommercePriceListOrderTypeRel commercePriceListOrderTypeRel :
				commercePriceListOrderTypeRelPersistence.
					findByCommercePriceListId(commercePriceListId)) {

			commercePriceListOrderTypeRelLocalService.
				deleteCommercePriceListOrderTypeRel(
					commercePriceListOrderTypeRel);
		}
	}

	@Override
	public CommercePriceListOrderTypeRel fetchCommercePriceListOrderTypeRel(
		long commercePriceListId, long commerceOrderTypeId) {

		return commercePriceListOrderTypeRelPersistence.fetchByCPI_COTI(
			commercePriceListId, commerceOrderTypeId);
	}

	@Override
	public CommercePriceListOrderTypeRel getCommercePriceListOrderTypeRel(
			long commercePriceListOrderTypeRelId)
		throws PortalException {

		return commercePriceListOrderTypeRelPersistence.findByPrimaryKey(
			commercePriceListOrderTypeRelId);
	}

	@Override
	public List<CommercePriceListOrderTypeRel>
		getCommercePriceListOrderTypeRels(long commercePriceListId) {

		return commercePriceListOrderTypeRelPersistence.
			findByCommercePriceListId(commercePriceListId);
	}

	@Override
	public List<CommercePriceListOrderTypeRel>
			getCommercePriceListOrderTypeRels(
				long commercePriceListId, String name, int start, int end,
				OrderByComparator<CommercePriceListOrderTypeRel>
					orderByComparator)
		throws PortalException {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePriceListOrderTypeRelTable.INSTANCE),
				commercePriceListId, name
			).orderBy(
				CommercePriceListOrderTypeRelTable.INSTANCE, orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public int getCommercePriceListOrderTypeRelsCount(
			long commercePriceListId, String name)
		throws PortalException {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommercePriceListOrderTypeRelTable.INSTANCE.
						commercePriceListOrderTypeRelId),
				commercePriceListId, name));
	}

	private GroupByStep _getGroupByStep(
			FromStep fromStep, Long commercePriceListId, String keywords)
		throws PortalException {

		JoinStep joinStep = fromStep.from(
			CommercePriceListOrderTypeRelTable.INSTANCE
		).innerJoinON(
			CommerceOrderTypeTable.INSTANCE,
			CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
				CommercePriceListOrderTypeRelTable.INSTANCE.commerceOrderTypeId)
		);

		return joinStep.where(
			() -> {
				Predicate predicate =
					CommercePriceListOrderTypeRelTable.INSTANCE.
						commercePriceListId.eq(commercePriceListId);

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

	private void _reindexCommercePriceList(long commercePriceListId)
		throws PortalException {

		Indexer<CommercePriceList> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommercePriceList.class);

		indexer.reindex(CommercePriceList.class.getName(), commercePriceListId);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private UserLocalService _userLocalService;

}