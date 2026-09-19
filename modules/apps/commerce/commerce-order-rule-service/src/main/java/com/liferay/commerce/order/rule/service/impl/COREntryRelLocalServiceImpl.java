/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupTable;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.model.COREntryRelTable;
import com.liferay.commerce.order.rule.model.COREntryTable;
import com.liferay.commerce.order.rule.service.base.COREntryRelLocalServiceBaseImpl;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelTable;
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
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.order.rule.model.COREntryRel",
	service = AopService.class
)
public class COREntryRelLocalServiceImpl
	extends COREntryRelLocalServiceBaseImpl {

	@Override
	public COREntryRel addCOREntryRel(
			long userId, String className, long classPK, long corEntryId)
		throws PortalException {

		COREntryRel corEntryRel = corEntryRelPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		corEntryRel.setCompanyId(user.getCompanyId());
		corEntryRel.setUserId(user.getUserId());
		corEntryRel.setUserName(user.getFullName());

		corEntryRel.setClassNameId(
			_classNameLocalService.getClassNameId(className));
		corEntryRel.setClassPK(classPK);
		corEntryRel.setCOREntryId(corEntryId);

		corEntryRel = corEntryRelPersistence.update(corEntryRel);

		_reindexCOREntry(corEntryId);

		return corEntryRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public COREntryRel deleteCOREntryRel(COREntryRel corEntryRel)
		throws PortalException {

		corEntryRelPersistence.remove(corEntryRel);

		_reindexCOREntry(corEntryRel.getCOREntryId());

		return corEntryRel;
	}

	@Override
	public COREntryRel deleteCOREntryRel(long corEntryRelId)
		throws PortalException {

		COREntryRel corEntryRel = corEntryRelPersistence.findByPrimaryKey(
			corEntryRelId);

		return corEntryRelLocalService.deleteCOREntryRel(corEntryRel);
	}

	@Override
	public void deleteCOREntryRels(long corEntryId) throws PortalException {
		List<COREntryRel> corEntryRels =
			corEntryRelPersistence.findByCOREntryId(corEntryId);

		for (COREntryRel corEntryRel : corEntryRels) {
			corEntryRelLocalService.deleteCOREntryRel(corEntryRel);
		}
	}

	@Override
	public void deleteCOREntryRels(String className, long corEntryId)
		throws PortalException {

		List<COREntryRel> corEntryRels = corEntryRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), corEntryId);

		for (COREntryRel corEntryRel : corEntryRels) {
			corEntryRelLocalService.deleteCOREntryRel(corEntryRel);
		}
	}

	@Override
	public COREntryRel fetchCOREntryRel(
		String className, long classPK, long corEntryId) {

		return corEntryRelPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			corEntryId);
	}

	@Override
	public List<COREntryRel> getAccountEntryCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(COREntryRelTable.INSTANCE),
				AccountEntryTable.INSTANCE,
				AccountEntryTable.INSTANCE.accountEntryId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, AccountEntry.class.getName(), keywords,
				AccountEntryTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getAccountEntryCOREntryRelsCount(
		long corEntryId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					COREntryRelTable.INSTANCE.COREntryRelId),
				AccountEntryTable.INSTANCE,
				AccountEntryTable.INSTANCE.accountEntryId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, AccountEntry.class.getName(), keywords,
				AccountEntryTable.INSTANCE.name));
	}

	@Override
	public List<COREntryRel> getAccountGroupCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(COREntryRelTable.INSTANCE),
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, AccountGroup.class.getName(), keywords,
				AccountGroupTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getAccountGroupCOREntryRelsCount(
		long corEntryId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					COREntryRelTable.INSTANCE.COREntryRelId),
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, AccountGroup.class.getName(), keywords,
				AccountGroupTable.INSTANCE.name));
	}

	@Override
	public List<COREntryRel> getCOREntryRels(long corEntryId) {
		return corEntryRelPersistence.findByCOREntryId(corEntryId);
	}

	@Override
	public List<COREntryRel> getCOREntryRels(
		long corEntryId, int start, int end,
		OrderByComparator<COREntryRel> orderByComparator) {

		return corEntryRelPersistence.findByCOREntryId(
			corEntryId, start, end, orderByComparator);
	}

	@Override
	public int getCOREntryRelsCount(long corEntryId) {
		return corEntryRelPersistence.countByCOREntryId(corEntryId);
	}

	@Override
	public List<COREntryRel> getCommerceChannelCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(COREntryRelTable.INSTANCE),
				CommerceChannelTable.INSTANCE,
				CommerceChannelTable.INSTANCE.commerceChannelId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, CommerceChannel.class.getName(), keywords,
				CommerceChannelTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceChannelCOREntryRelsCount(
		long corEntryId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					COREntryRelTable.INSTANCE.COREntryRelId),
				CommerceChannelTable.INSTANCE,
				CommerceChannelTable.INSTANCE.commerceChannelId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, CommerceChannel.class.getName(), keywords,
				CommerceChannelTable.INSTANCE.name));
	}

	@Override
	public List<COREntryRel> getCommerceOrderTypeCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(COREntryRelTable.INSTANCE),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, CommerceOrderType.class.getName(), keywords,
				CommerceOrderTypeTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceOrderTypeCOREntryRelsCount(
		long corEntryId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					COREntryRelTable.INSTANCE.COREntryRelId),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					COREntryRelTable.INSTANCE.classPK),
				corEntryId, CommerceOrderType.class.getName(), keywords,
				CommerceOrderTypeTable.INSTANCE.name));
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Table innerJoinTable, Predicate innerJoinPredicate,
		Long corEntryId, String className, String keywords,
		Expression<String> keywordsPredicateExpression) {

		JoinStep joinStep = fromStep.from(
			COREntryRelTable.INSTANCE
		).innerJoinON(
			COREntryTable.INSTANCE,
			COREntryTable.INSTANCE.COREntryId.eq(
				COREntryRelTable.INSTANCE.COREntryId)
		).innerJoinON(
			innerJoinTable, innerJoinPredicate
		);

		return joinStep.where(
			() -> COREntryRelTable.INSTANCE.COREntryId.eq(
				corEntryId
			).and(
				COREntryRelTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(className))
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

	private void _reindexCOREntry(long corEntryId) throws PortalException {
		Indexer<COREntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			COREntry.class);

		indexer.reindex(COREntry.class.getName(), corEntryId);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}