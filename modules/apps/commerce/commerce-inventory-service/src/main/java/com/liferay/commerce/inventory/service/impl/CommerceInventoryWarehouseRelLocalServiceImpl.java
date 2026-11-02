/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupTable;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseRelException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRelTable;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseTable;
import com.liferay.commerce.inventory.service.base.CommerceInventoryWarehouseRelLocalServiceBaseImpl;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
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
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel",
	service = AopService.class
)
public class CommerceInventoryWarehouseRelLocalServiceImpl
	extends CommerceInventoryWarehouseRelLocalServiceBaseImpl {

	@Override
	public CommerceInventoryWarehouseRel addCommerceInventoryWarehouseRel(
			long userId, String className, long classPK,
			long commerceInventoryWarehouseId)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		_validate(classNameId, classPK, commerceInventoryWarehouseId);

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			commerceInventoryWarehouseRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commerceInventoryWarehouseRel.setCompanyId(user.getCompanyId());
		commerceInventoryWarehouseRel.setUserId(user.getUserId());
		commerceInventoryWarehouseRel.setUserName(user.getFullName());

		commerceInventoryWarehouseRel.setClassNameId(classNameId);
		commerceInventoryWarehouseRel.setClassPK(classPK);
		commerceInventoryWarehouseRel.setCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);

		commerceInventoryWarehouseRel =
			commerceInventoryWarehouseRelPersistence.update(
				commerceInventoryWarehouseRel);

		_reindexCommerceInventoryWarehouse(commerceInventoryWarehouseId);

		return commerceInventoryWarehouseRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceInventoryWarehouseRel deleteCommerceInventoryWarehouseRel(
			CommerceInventoryWarehouseRel commerceInventoryWarehouseRel)
		throws PortalException {

		commerceInventoryWarehouseRelPersistence.remove(
			commerceInventoryWarehouseRel);

		_reindexCommerceInventoryWarehouse(
			commerceInventoryWarehouseRel.getCommerceInventoryWarehouseId());

		return commerceInventoryWarehouseRel;
	}

	@Override
	public CommerceInventoryWarehouseRel deleteCommerceInventoryWarehouseRel(
			long commerceInventoryWarehouseRelId)
		throws PortalException {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			commerceInventoryWarehouseRelPersistence.findByPrimaryKey(
				commerceInventoryWarehouseRelId);

		return commerceInventoryWarehouseRelLocalService.
			deleteCommerceInventoryWarehouseRel(commerceInventoryWarehouseRel);
	}

	@Override
	public void deleteCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId)
		throws PortalException {

		List<CommerceInventoryWarehouseRel> commerceInventoryWarehouseRels =
			commerceInventoryWarehouseRelPersistence.
				findByCommerceInventoryWarehouseId(
					commerceInventoryWarehouseId);

		for (CommerceInventoryWarehouseRel commerceInventoryWarehouseRel :
				commerceInventoryWarehouseRels) {

			commerceInventoryWarehouseRelLocalService.
				deleteCommerceInventoryWarehouseRel(
					commerceInventoryWarehouseRel);
		}
	}

	@Override
	public void deleteCommerceInventoryWarehouseRels(
			String className, long commerceInventoryWarehouseId)
		throws PortalException {

		List<CommerceInventoryWarehouseRel> commerceInventoryWarehouseRels =
			commerceInventoryWarehouseRelPersistence.findByC_C(
				_classNameLocalService.getClassNameId(className),
				commerceInventoryWarehouseId);

		for (CommerceInventoryWarehouseRel commerceInventoryWarehouseRel :
				commerceInventoryWarehouseRels) {

			commerceInventoryWarehouseRelLocalService.
				deleteCommerceInventoryWarehouseRel(
					commerceInventoryWarehouseRel);
		}
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
		getAccountEntryCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId, String keywords, int start,
			int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceInventoryWarehouseRelTable.INSTANCE),
				AccountEntryTable.INSTANCE,
				AccountEntryTable.INSTANCE.accountEntryId.eq(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK),
				commerceInventoryWarehouseId, AccountEntry.class.getName(),
				keywords, AccountEntryTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getAccountEntryCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceInventoryWarehouseRelTable.INSTANCE.
						commerceInventoryWarehouseRelId),
				AccountEntryTable.INSTANCE,
				AccountEntryTable.INSTANCE.accountEntryId.eq(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK),
				commerceInventoryWarehouseId, AccountEntry.class.getName(),
				keywords, AccountEntryTable.INSTANCE.name));
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
		getAccountGroupCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId, String keywords, int start,
			int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceInventoryWarehouseRelTable.INSTANCE),
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK),
				commerceInventoryWarehouseId, AccountGroup.class.getName(),
				keywords, AccountGroupTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getAccountGroupCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceInventoryWarehouseRelTable.INSTANCE.
						commerceInventoryWarehouseRelId),
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK),
				commerceInventoryWarehouseId, AccountGroup.class.getName(),
				keywords, AccountGroupTable.INSTANCE.name));
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
		getCommerceInventoryWarehouseRels(
			String className, long commerceInventoryWarehouseId) {

		return commerceInventoryWarehouseRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className),
			commerceInventoryWarehouseId);
	}

	@Override
	public List<CommerceInventoryWarehouseRel>
		getCommerceOrderTypeCommerceInventoryWarehouseRels(
			long commerceInventoryWarehouseId, String keywords, int start,
			int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceInventoryWarehouseRelTable.INSTANCE),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK),
				commerceInventoryWarehouseId, CommerceOrderType.class.getName(),
				keywords, CommerceOrderTypeTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceOrderTypeCommerceInventoryWarehouseRelsCount(
		long commerceInventoryWarehouseId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceInventoryWarehouseRelTable.INSTANCE.
						commerceInventoryWarehouseRelId),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommerceInventoryWarehouseRelTable.INSTANCE.classPK),
				commerceInventoryWarehouseId, CommerceOrderType.class.getName(),
				keywords, CommerceOrderTypeTable.INSTANCE.name));
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Table innerJoinTable, Predicate innerJoinPredicate,
		Long commerceInventoryWarehouseId, String className, String keywords,
		Expression<String> keywordsPredicateExpression) {

		JoinStep joinStep = fromStep.from(
			CommerceInventoryWarehouseRelTable.INSTANCE
		).innerJoinON(
			CommerceInventoryWarehouseTable.INSTANCE,
			CommerceInventoryWarehouseTable.INSTANCE.
				commerceInventoryWarehouseId.eq(
					CommerceInventoryWarehouseRelTable.INSTANCE.
						commerceInventoryWarehouseId)
		).innerJoinON(
			innerJoinTable, innerJoinPredicate
		);

		return joinStep.where(
			() ->
				CommerceInventoryWarehouseRelTable.INSTANCE.
					commerceInventoryWarehouseId.eq(
						commerceInventoryWarehouseId
					).and(
						CommerceInventoryWarehouseRelTable.INSTANCE.classNameId.
							eq(_classNameLocalService.getClassNameId(className))
					).and(
						() -> {
							if (Validator.isNull(keywords)) {
								return null;
							}

							return Predicate.withParentheses(
								_customSQL.getKeywordsPredicate(
									DSLFunctionFactoryUtil.lower(
										keywordsPredicateExpression),
									_customSQL.keywords(keywords, true)));
						}
					));
	}

	private void _reindexCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId)
		throws PortalException {

		Indexer<CommerceInventoryWarehouse> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CommerceInventoryWarehouse.class);

		indexer.reindex(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouseId);
	}

	private void _validate(
			long classNameId, long classPK, long commerceInventoryWarehouseId)
		throws PortalException {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			commerceInventoryWarehouseRelPersistence.fetchByC_C_CIWI(
				classNameId, classPK, commerceInventoryWarehouseId);

		if (commerceInventoryWarehouseRel != null) {
			throw new DuplicateCommerceInventoryWarehouseRelException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}