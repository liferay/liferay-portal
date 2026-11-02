/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.impl;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.shipping.engine.fixed.exception.DuplicateCommerceShippingFixedOptionQualifierException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifierTable;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionTable;
import com.liferay.commerce.shipping.engine.fixed.service.base.CommerceShippingFixedOptionQualifierLocalServiceBaseImpl;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.model.CommerceTermEntryTable;
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
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier",
	service = AopService.class
)
public class CommerceShippingFixedOptionQualifierLocalServiceImpl
	extends CommerceShippingFixedOptionQualifierLocalServiceBaseImpl {

	@Override
	public CommerceShippingFixedOptionQualifier
			addCommerceShippingFixedOptionQualifier(
				long userId, String className, long classPK,
				long commerceShippingFixedOptionId)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		_validate(classNameId, classPK, commerceShippingFixedOptionId);

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier =
				commerceShippingFixedOptionQualifierPersistence.create(
					counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commerceShippingFixedOptionQualifier.setCompanyId(user.getCompanyId());
		commerceShippingFixedOptionQualifier.setUserId(user.getUserId());
		commerceShippingFixedOptionQualifier.setUserName(user.getFullName());

		commerceShippingFixedOptionQualifier.setClassNameId(classNameId);
		commerceShippingFixedOptionQualifier.setClassPK(classPK);
		commerceShippingFixedOptionQualifier.setCommerceShippingFixedOptionId(
			commerceShippingFixedOptionId);

		return commerceShippingFixedOptionQualifierPersistence.update(
			commerceShippingFixedOptionQualifier);
	}

	@Override
	public void deleteCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId)
		throws PortalException {

		List<CommerceShippingFixedOptionQualifier>
			commerceShippingFixedOptionQualifiers =
				commerceShippingFixedOptionQualifierPersistence.
					findByCommerceShippingFixedOptionId(
						commerceShippingFixedOptionId);

		for (CommerceShippingFixedOptionQualifier
				commerceShippingFixedOptionQualifier :
					commerceShippingFixedOptionQualifiers) {

			commerceShippingFixedOptionQualifierLocalService.
				deleteCommerceShippingFixedOptionQualifier(
					commerceShippingFixedOptionQualifier);
		}
	}

	@Override
	public void deleteCommerceShippingFixedOptionQualifiers(
			String className, long commerceShippingFixedOptionId)
		throws PortalException {

		List<CommerceShippingFixedOptionQualifier>
			commerceShippingFixedOptionQualifiers =
				commerceShippingFixedOptionQualifierPersistence.findByC_C(
					_classNameLocalService.getClassNameId(className),
					commerceShippingFixedOptionId);

		for (CommerceShippingFixedOptionQualifier
				commerceShippingFixedOptionQualifier :
					commerceShippingFixedOptionQualifiers) {

			commerceShippingFixedOptionQualifierLocalService.
				deleteCommerceShippingFixedOptionQualifier(
					commerceShippingFixedOptionQualifier);
		}
	}

	@Override
	public List<CommerceShippingFixedOptionQualifier>
		getCommerceOrderTypeCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId, String keywords, int start,
			int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceShippingFixedOptionQualifierTable.INSTANCE),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommerceShippingFixedOptionQualifierTable.INSTANCE.classPK),
				commerceShippingFixedOptionId,
				CommerceOrderType.class.getName(), keywords,
				CommerceOrderTypeTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
		long commerceShippingFixedOptionId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceShippingFixedOptionQualifierTable.INSTANCE.
						commerceShippingFixedOptionQualifierId),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommerceShippingFixedOptionQualifierTable.INSTANCE.classPK),
				commerceShippingFixedOptionId,
				CommerceOrderType.class.getName(), keywords,
				CommerceOrderTypeTable.INSTANCE.name));
	}

	@Override
	public List<CommerceShippingFixedOptionQualifier>
		getCommerceShippingFixedOptionQualifiers(
			String className, long commerceShippingFixedOptionId) {

		return commerceShippingFixedOptionQualifierPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className),
			commerceShippingFixedOptionId);
	}

	@Override
	public List<CommerceShippingFixedOptionQualifier>
		getCommerceTermEntryCommerceShippingFixedOptionQualifiers(
			long commerceShippingFixedOptionId, String keywords, int start,
			int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceShippingFixedOptionQualifierTable.INSTANCE),
				CommerceTermEntryTable.INSTANCE,
				CommerceTermEntryTable.INSTANCE.commerceTermEntryId.eq(
					CommerceShippingFixedOptionQualifierTable.INSTANCE.classPK),
				commerceShippingFixedOptionId,
				CommerceTermEntry.class.getName(), keywords,
				CommerceTermEntryTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
		long commerceShippingFixedOptionId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommerceShippingFixedOptionQualifierTable.INSTANCE.
						commerceShippingFixedOptionQualifierId),
				CommerceTermEntryTable.INSTANCE,
				CommerceTermEntryTable.INSTANCE.commerceTermEntryId.eq(
					CommerceShippingFixedOptionQualifierTable.INSTANCE.classPK),
				commerceShippingFixedOptionId,
				CommerceTermEntry.class.getName(), keywords,
				CommerceTermEntryTable.INSTANCE.name));
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Table innerJoinTable, Predicate innerJoinPredicate,
		Long commerceShippingFixedOptionId, String className, String keywords,
		Expression<String> keywordsPredicateExpression) {

		JoinStep joinStep = fromStep.from(
			CommerceShippingFixedOptionQualifierTable.INSTANCE
		).innerJoinON(
			CommerceShippingFixedOptionTable.INSTANCE,
			CommerceShippingFixedOptionTable.INSTANCE.
				commerceShippingFixedOptionId.eq(
					CommerceShippingFixedOptionQualifierTable.INSTANCE.
						commerceShippingFixedOptionId)
		).innerJoinON(
			innerJoinTable, innerJoinPredicate
		);

		return joinStep.where(
			() ->
				CommerceShippingFixedOptionQualifierTable.INSTANCE.
					commerceShippingFixedOptionId.eq(
						commerceShippingFixedOptionId
					).and(
						CommerceShippingFixedOptionQualifierTable.INSTANCE.
							classNameId.eq(
								_classNameLocalService.getClassNameId(
									className))
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

	private void _validate(
			long classNameId, long classPK, long commerceShippingFixedOptionId)
		throws PortalException {

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier =
				commerceShippingFixedOptionQualifierPersistence.fetchByC_C_C(
					classNameId, classPK, commerceShippingFixedOptionId);

		if (commerceShippingFixedOptionQualifier != null) {
			throw new DuplicateCommerceShippingFixedOptionQualifierException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}