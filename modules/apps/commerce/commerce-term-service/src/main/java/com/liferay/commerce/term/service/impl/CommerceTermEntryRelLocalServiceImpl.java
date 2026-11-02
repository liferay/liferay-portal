/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.impl;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.term.exception.DuplicateCommerceTermEntryRelException;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.model.CommerceTermEntryRelTable;
import com.liferay.commerce.term.model.CommerceTermEntryTable;
import com.liferay.commerce.term.service.base.CommerceTermEntryRelLocalServiceBaseImpl;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.term.model.CommerceTermEntryRel",
	service = AopService.class
)
public class CommerceTermEntryRelLocalServiceImpl
	extends CommerceTermEntryRelLocalServiceBaseImpl {

	@Override
	public CommerceTermEntryRel addCommerceTermEntryRel(
			long userId, String className, long classPK,
			long commerceTermEntryId)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		_validate(classNameId, classPK, commerceTermEntryId);

		CommerceTermEntryRel commerceTermEntryRel =
			commerceTermEntryRelPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commerceTermEntryRel.setCompanyId(user.getCompanyId());
		commerceTermEntryRel.setUserId(user.getUserId());
		commerceTermEntryRel.setUserName(user.getFullName());

		commerceTermEntryRel.setClassNameId(classNameId);
		commerceTermEntryRel.setClassPK(classPK);
		commerceTermEntryRel.setCommerceTermEntryId(commerceTermEntryId);

		commerceTermEntryRel = commerceTermEntryRelPersistence.update(
			commerceTermEntryRel);

		_reindexCommerceTermEntry(commerceTermEntryId);

		return commerceTermEntryRel;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceTermEntryRel deleteCommerceTermEntryRel(
			CommerceTermEntryRel commerceTermEntryRel)
		throws PortalException {

		commerceTermEntryRelPersistence.remove(commerceTermEntryRel);

		_reindexCommerceTermEntry(
			commerceTermEntryRel.getCommerceTermEntryId());

		return commerceTermEntryRel;
	}

	@Override
	public CommerceTermEntryRel deleteCommerceTermEntryRel(
			long commerceTermEntryRelId)
		throws PortalException {

		CommerceTermEntryRel commerceTermEntryRel =
			commerceTermEntryRelPersistence.findByPrimaryKey(
				commerceTermEntryRelId);

		return commerceTermEntryRelLocalService.deleteCommerceTermEntryRel(
			commerceTermEntryRel);
	}

	@Override
	public void deleteCommerceTermEntryRels(long commerceTermEntryId)
		throws PortalException {

		List<CommerceTermEntryRel> commerceTermEntryRels =
			commerceTermEntryRelPersistence.findByCommerceTermEntryId(
				commerceTermEntryId);

		for (CommerceTermEntryRel commerceTermEntryRel :
				commerceTermEntryRels) {

			commerceTermEntryRelLocalService.deleteCommerceTermEntryRel(
				commerceTermEntryRel);
		}
	}

	@Override
	public void deleteCommerceTermEntryRels(
			String className, long commerceTermEntryId)
		throws PortalException {

		List<CommerceTermEntryRel> commerceTermEntryRels =
			commerceTermEntryRelPersistence.findByC_C(
				_classNameLocalService.getClassNameId(className),
				commerceTermEntryId);

		for (CommerceTermEntryRel commerceTermEntryRel :
				commerceTermEntryRels) {

			commerceTermEntryRelLocalService.deleteCommerceTermEntryRel(
				commerceTermEntryRel);
		}
	}

	@Override
	public List<CommerceTermEntryRel> getCommerceOrderTypeCommerceTermEntryRels(
		long commerceTermEntryId, String keywords, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceTermEntryRelTable.INSTANCE),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommerceTermEntryRelTable.INSTANCE.classPK),
				commerceTermEntryId, CommerceOrderType.class.getName(),
				keywords, CommerceOrderTypeTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceOrderTypeCommerceTermEntryRelsCount(
		long commerceTermEntryId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceTermEntryRelTable.INSTANCE.commerceTermEntryRelId),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommerceTermEntryRelTable.INSTANCE.classPK),
				commerceTermEntryId, CommerceOrderType.class.getName(),
				keywords, CommerceOrderTypeTable.INSTANCE.name));
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Table innerJoinTable, Predicate innerJoinPredicate,
		Long commerceTermEntryId, String className, String keywords,
		Expression<String> keywordsPredicateExpression) {

		JoinStep joinStep = fromStep.from(
			CommerceTermEntryRelTable.INSTANCE
		).innerJoinON(
			CommerceTermEntryTable.INSTANCE,
			CommerceTermEntryTable.INSTANCE.commerceTermEntryId.eq(
				CommerceTermEntryRelTable.INSTANCE.commerceTermEntryId)
		).innerJoinON(
			innerJoinTable, innerJoinPredicate
		);

		return joinStep.where(
			() -> CommerceTermEntryRelTable.INSTANCE.commerceTermEntryId.eq(
				commerceTermEntryId
			).and(
				CommerceTermEntryRelTable.INSTANCE.classNameId.eq(
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

	private void _reindexCommerceTermEntry(long commerceTermEntryId)
		throws PortalException {

		Indexer<CommerceTermEntry> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CommerceTermEntry.class);

		indexer.reindex(CommerceTermEntry.class.getName(), commerceTermEntryId);
	}

	private void _validate(
			long classNameId, long classPK, long commerceTermEntryId)
		throws PortalException {

		CommerceTermEntryRel commerceTermEntryRel =
			commerceTermEntryRelPersistence.fetchByC_C_C(
				classNameId, classPK, commerceTermEntryId);

		if (commerceTermEntryRel != null) {
			throw new DuplicateCommerceTermEntryRelException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}