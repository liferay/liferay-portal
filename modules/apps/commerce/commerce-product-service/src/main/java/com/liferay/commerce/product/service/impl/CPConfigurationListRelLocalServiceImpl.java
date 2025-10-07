/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupTable;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.product.exception.CPConfigurationListMasterException;
import com.liferay.commerce.product.exception.DuplicateCPConfigurationListRelException;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.model.CPConfigurationListRelTable;
import com.liferay.commerce.product.model.CPConfigurationListTable;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.base.CPConfigurationListRelLocalServiceBaseImpl;
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
 * @author Danny Situ
 */
@Component(
	property = "model.class.name=com.liferay.commerce.product.model.CPConfigurationListRel",
	service = AopService.class
)
public class CPConfigurationListRelLocalServiceImpl
	extends CPConfigurationListRelLocalServiceBaseImpl {

	@Override
	public CPConfigurationListRel addCPConfigurationListRel(
			long userId, String className, long classPK,
			long cpConfigurationListId)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		_validate(classNameId, classPK, cpConfigurationListId);

		CPConfigurationListRel cpConfigurationListRel =
			cpConfigurationListRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		cpConfigurationListRel.setCompanyId(user.getCompanyId());
		cpConfigurationListRel.setUserId(user.getUserId());
		cpConfigurationListRel.setUserName(user.getFullName());

		cpConfigurationListRel.setClassNameId(classNameId);
		cpConfigurationListRel.setClassPK(classPK);
		cpConfigurationListRel.setCPConfigurationListId(cpConfigurationListId);

		cpConfigurationListRel = cpConfigurationListRelPersistence.update(
			cpConfigurationListRel);

		_reindexCPConfigurationList(cpConfigurationListId);

		return cpConfigurationListRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPConfigurationListRel deleteCPConfigurationListRel(
			CPConfigurationListRel cpConfigurationListRel)
		throws PortalException {

		cpConfigurationListRelPersistence.remove(cpConfigurationListRel);

		return cpConfigurationListRel;
	}

	@Override
	public CPConfigurationListRel deleteCPConfigurationListRel(
			long cpConfigurationListRelId)
		throws PortalException {

		CPConfigurationListRel cpConfigurationListRel =
			cpConfigurationListRelPersistence.findByPrimaryKey(
				cpConfigurationListRelId);

		cpConfigurationListRel =
			cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
				cpConfigurationListRel);

		_reindexCPConfigurationList(
			cpConfigurationListRel.getCPConfigurationListId());

		return cpConfigurationListRel;
	}

	@Override
	public void deleteCPConfigurationListRels(long cpConfigurationListId)
		throws PortalException {

		List<CPConfigurationListRel> cpConfigurationListRels =
			cpConfigurationListRelPersistence.findByCPConfigurationListId(
				cpConfigurationListId);

		for (CPConfigurationListRel cpConfigurationListRel :
				cpConfigurationListRels) {

			cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
				cpConfigurationListRel);
		}
	}

	@Override
	public void deleteCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException {

		List<CPConfigurationListRel> cpConfigurationListRels =
			cpConfigurationListRelPersistence.findByC_C(
				_classNameLocalService.getClassNameId(className),
				cpConfigurationListId);

		for (CPConfigurationListRel cpConfigurationListRel :
				cpConfigurationListRels) {

			cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
				cpConfigurationListRel);
		}
	}

	@Override
	public CPConfigurationListRel fetchCPConfigurationListRel(
		String className, long classPK, long cpConfigurationListId) {

		return cpConfigurationListRelPersistence.fetchByC_C_C(
			_classNameLocalService.getClassNameId(className), classPK,
			cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationListRel> getAccountEntryCPConfigurationListRels(
		long cpConfigurationListId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CPConfigurationListRelTable.INSTANCE),
				AccountEntryTable.INSTANCE,
				AccountEntryTable.INSTANCE.accountEntryId.eq(
					CPConfigurationListRelTable.INSTANCE.classPK),
				cpConfigurationListId, AccountEntry.class.getName(), keywords,
				AccountEntryTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getAccountEntryCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CPConfigurationListRelTable.INSTANCE.
						CPConfigurationListRelId),
				AccountEntryTable.INSTANCE,
				AccountEntryTable.INSTANCE.accountEntryId.eq(
					CPConfigurationListRelTable.INSTANCE.classPK),
				cpConfigurationListId, AccountEntry.class.getName(), keywords,
				AccountEntryTable.INSTANCE.name));
	}

	@Override
	public List<CPConfigurationListRel> getAccountGroupCPConfigurationListRels(
		long cpConfigurationListId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CPConfigurationListRelTable.INSTANCE),
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					CPConfigurationListRelTable.INSTANCE.classPK),
				cpConfigurationListId, AccountGroup.class.getName(), keywords,
				AccountGroupTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getAccountGroupCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CPConfigurationListRelTable.INSTANCE.
						CPConfigurationListRelId),
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					CPConfigurationListRelTable.INSTANCE.classPK),
				cpConfigurationListId, AccountGroup.class.getName(), keywords,
				AccountGroupTable.INSTANCE.name));
	}

	@Override
	public List<CPConfigurationListRel>
		getCommerceOrderTypeCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CPConfigurationListRelTable.INSTANCE),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CPConfigurationListRelTable.INSTANCE.classPK),
				cpConfigurationListId, CommerceOrderType.class.getName(),
				keywords, CommerceOrderTypeTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceOrderTypeCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CPConfigurationListRelTable.INSTANCE.
						CPConfigurationListRelId),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CPConfigurationListRelTable.INSTANCE.classPK),
				cpConfigurationListId, CommerceOrderType.class.getName(),
				keywords, CommerceOrderTypeTable.INSTANCE.name));
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
		long cpConfigurationListId) {

		return cpConfigurationListRelPersistence.findByCPConfigurationListId(
			cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
		long cpConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationListRel> orderByComparator) {

		return cpConfigurationListRelPersistence.findByCPConfigurationListId(
			cpConfigurationListId, start, end, orderByComparator);
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
		String className, long cpConfigurationListId) {

		return cpConfigurationListRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className),
			cpConfigurationListId);
	}

	@Override
	public List<CPConfigurationListRel> getCPConfigurationListRels(
		String className, long cpConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationListRel> orderByComparator) {

		return cpConfigurationListRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className),
			cpConfigurationListId, start, end, orderByComparator);
	}

	@Override
	public int getCPConfigurationListRelsCount(long cpConfigurationListId) {
		return cpConfigurationListRelPersistence.countByCPConfigurationListId(
			cpConfigurationListId);
	}

	@Override
	public int getCPConfigurationListRelsCount(
		String className, long cpConfigurationListId) {

		return cpConfigurationListRelPersistence.countByC_C(
			_classNameLocalService.getClassNameId(className),
			cpConfigurationListId);
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Table innerJoinTable, Predicate innerJoinPredicate,
		Long cpConfigurationListId, String className, String keywords,
		Expression<String> keywordsPredicateExpression) {

		JoinStep joinStep = fromStep.from(
			CPConfigurationListRelTable.INSTANCE
		).innerJoinON(
			CPConfigurationListTable.INSTANCE,
			CPConfigurationListTable.INSTANCE.CPConfigurationListId.eq(
				CPConfigurationListRelTable.INSTANCE.CPConfigurationListId)
		).innerJoinON(
			innerJoinTable, innerJoinPredicate
		);

		return joinStep.where(
			() -> CPConfigurationListRelTable.INSTANCE.CPConfigurationListId.eq(
				cpConfigurationListId
			).and(
				CPConfigurationListRelTable.INSTANCE.classNameId.eq(
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

	private void _reindexCPConfigurationList(long cpConfigurationListId)
		throws PortalException {

		Indexer<CPConfigurationList> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPConfigurationList.class);

		indexer.reindex(
			CPConfigurationList.class.getName(), cpConfigurationListId);
	}

	private void _validate(
			long classNameId, long classPK, long cpConfigurationListId)
		throws PortalException {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.getCPConfigurationList(
				cpConfigurationListId);

		if (cpConfigurationList.isMaster()) {
			throw new CPConfigurationListMasterException();
		}

		CPConfigurationListRel cpConfigurationListRel =
			cpConfigurationListRelPersistence.fetchByC_C_C(
				classNameId, classPK, cpConfigurationListId);

		if (cpConfigurationListRel != null) {
			throw new DuplicateCPConfigurationListRelException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}