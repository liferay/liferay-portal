/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.exception.DuplicateCommerceOrderTypeRelException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeRel;
import com.liferay.commerce.model.CommerceOrderTypeRelTable;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelTable;
import com.liferay.commerce.service.base.CommerceOrderTypeRelLocalServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CommerceOrderTypeRel",
	service = AopService.class
)
public class CommerceOrderTypeRelLocalServiceImpl
	extends CommerceOrderTypeRelLocalServiceBaseImpl {

	@Override
	public CommerceOrderTypeRel addCommerceOrderTypeRel(
			long userId, String className, long classPK,
			long commerceOrderTypeId, ServiceContext serviceContext)
		throws PortalException {

		_validate(className, classPK, commerceOrderTypeId);

		CommerceOrderTypeRel commerceOrderTypeRel =
			commerceOrderTypeRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commerceOrderTypeRel.setCompanyId(user.getCompanyId());
		commerceOrderTypeRel.setUserId(user.getUserId());
		commerceOrderTypeRel.setUserName(user.getFullName());

		commerceOrderTypeRel.setClassNameId(
			_classNameLocalService.getClassNameId(className));
		commerceOrderTypeRel.setClassPK(classPK);
		commerceOrderTypeRel.setCommerceOrderTypeId(commerceOrderTypeId);
		commerceOrderTypeRel.setExpandoBridgeAttributes(serviceContext);

		commerceOrderTypeRel = commerceOrderTypeRelPersistence.update(
			commerceOrderTypeRel);

		_reindexCommerceOrderType(commerceOrderTypeId);

		return commerceOrderTypeRel;
	}

	@Override
	public CommerceOrderTypeRel deleteCommerceOrderTypeRel(
			long commerceOrderTypeRelId)
		throws PortalException {

		CommerceOrderTypeRel commerceOrderTypeRel =
			commerceOrderTypeRelPersistence.findByPrimaryKey(
				commerceOrderTypeRelId);

		commerceOrderTypeRelPersistence.remove(commerceOrderTypeRel);

		_reindexCommerceOrderType(
			commerceOrderTypeRel.getCommerceOrderTypeId());

		return commerceOrderTypeRel;
	}

	@Override
	public void deleteCommerceOrderTypeRels(long commerceOrderTypeId)
		throws PortalException {

		List<CommerceOrderTypeRel> commerceOrderTypeRels =
			commerceOrderTypeRelPersistence.findByCommerceOrderTypeId(
				commerceOrderTypeId);

		for (CommerceOrderTypeRel commerceOrderTypeRel :
				commerceOrderTypeRels) {

			commerceOrderTypeRelLocalService.deleteCommerceOrderTypeRel(
				commerceOrderTypeRel.getCommerceOrderTypeRelId());
		}
	}

	@Override
	public void deleteCommerceOrderTypeRels(
			String className, long commerceOrderTypeId)
		throws PortalException {

		List<CommerceOrderTypeRel> commerceOrderTypeRels =
			commerceOrderTypeRelPersistence.findByC_C(
				_classNameLocalService.getClassNameId(className),
				commerceOrderTypeId);

		for (CommerceOrderTypeRel commerceOrderTypeRel :
				commerceOrderTypeRels) {

			commerceOrderTypeRelLocalService.deleteCommerceOrderTypeRel(
				commerceOrderTypeRel.getCommerceOrderTypeRelId());
		}
	}

	@Override
	public List<CommerceOrderTypeRel> getCommerceOrderTypeCommerceChannelRels(
			long commerceOrderTypeId, String keywords, int start, int end)
		throws PortalException {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceOrderTypeRelTable.INSTANCE),
				CommerceChannelTable.INSTANCE,
				CommerceChannelTable.INSTANCE.commerceChannelId.eq(
					CommerceOrderTypeRelTable.INSTANCE.classPK),
				commerceOrderTypeId, CommerceChannel.class.getName(), keywords,
				CommerceChannelTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceOrderTypeCommerceChannelRelsCount(
			long commerceOrderTypeId, String keywords)
		throws PortalException {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceOrderTypeRelTable.INSTANCE.commerceOrderTypeRelId),
				CommerceChannelTable.INSTANCE,
				CommerceChannelTable.INSTANCE.commerceChannelId.eq(
					CommerceOrderTypeRelTable.INSTANCE.classPK),
				commerceOrderTypeId, CommerceChannel.class.getName(), keywords,
				CommerceChannelTable.INSTANCE.name));
	}

	@Override
	public CommerceOrderTypeRel getCommerceOrderTypeRel(
			long commerceOrderTypeRelId)
		throws PortalException {

		return commerceOrderTypeRelPersistence.findByPrimaryKey(
			commerceOrderTypeRelId);
	}

	private GroupByStep _getGroupByStep(
			FromStep fromStep, Table innerJoinTable,
			Predicate innerJoinPredicate, Long commerceOrderTypeId,
			String className, String keywords,
			Expression<String> keywordsPredicateExpression)
		throws PortalException {

		JoinStep joinStep = fromStep.from(
			CommerceOrderTypeRelTable.INSTANCE
		).innerJoinON(
			CommerceOrderTypeTable.INSTANCE,
			CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
				CommerceOrderTypeRelTable.INSTANCE.commerceOrderTypeId)
		).innerJoinON(
			innerJoinTable, innerJoinPredicate
		);

		return joinStep.where(
			() -> {
				Predicate predicate =
					CommerceOrderTypeRelTable.INSTANCE.commerceOrderTypeId.eq(
						commerceOrderTypeId
					).and(
						CommerceOrderTypeRelTable.INSTANCE.classNameId.eq(
							_classNameLocalService.getClassNameId(className))
					);

				if (Validator.isNotNull(keywords)) {
					predicate = predicate.and(
						Predicate.withParentheses(
							_customSQL.getKeywordsPredicate(
								DSLFunctionFactoryUtil.lower(
									keywordsPredicateExpression),
								_customSQL.keywords(keywords, true))));
				}

				return predicate;
			});
	}

	private void _reindexCommerceOrderType(long commerceOrderTypeId)
		throws PortalException {

		Indexer<CommerceOrderType> indexer =
			_indexerRegistry.nullSafeGetIndexer(CommerceOrderType.class);

		indexer.reindex(CommerceOrderType.class.getName(), commerceOrderTypeId);
	}

	private void _validate(
			String className, long classPK, long commerceOrderTypeId)
		throws PortalException {

		int commerceOrderTypeRelsCount =
			commerceOrderTypeRelPersistence.countByC_C_C(
				_classNameLocalService.getClassNameId(className), classPK,
				commerceOrderTypeId);

		if (commerceOrderTypeRelsCount > 0) {
			throw new DuplicateCommerceOrderTypeRelException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}