/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.impl;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceOrderTypeTable;
import com.liferay.commerce.payment.exception.DuplicateCommercePaymentMethodGroupRelQualifierException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifierTable;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelTable;
import com.liferay.commerce.payment.service.base.CommercePaymentMethodGroupRelQualifierLocalServiceBaseImpl;
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
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
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
	property = "model.class.name=com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier",
	service = AopService.class
)
public class CommercePaymentMethodGroupRelQualifierLocalServiceImpl
	extends CommercePaymentMethodGroupRelQualifierLocalServiceBaseImpl {

	@Override
	public CommercePaymentMethodGroupRelQualifier
			addCommercePaymentMethodGroupRelQualifier(
				long userId, String className, long classPK,
				long commercePaymentMethodGroupRelId)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		_validate(classNameId, classPK, commercePaymentMethodGroupRelId);

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				commercePaymentMethodGroupRelQualifierPersistence.create(
					counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		commercePaymentMethodGroupRelQualifier.setCompanyId(
			user.getCompanyId());
		commercePaymentMethodGroupRelQualifier.setUserId(user.getUserId());
		commercePaymentMethodGroupRelQualifier.setUserName(user.getFullName());

		commercePaymentMethodGroupRelQualifier.setClassNameId(classNameId);
		commercePaymentMethodGroupRelQualifier.setClassPK(classPK);
		commercePaymentMethodGroupRelQualifier.
			setCommercePaymentMethodGroupRelId(commercePaymentMethodGroupRelId);

		return commercePaymentMethodGroupRelQualifierPersistence.update(
			commercePaymentMethodGroupRelQualifier);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommercePaymentMethodGroupRelQualifier
			deleteCommercePaymentMethodGroupRelQualifier(
				CommercePaymentMethodGroupRelQualifier
					commercePaymentMethodGroupRelQualifier)
		throws PortalException {

		commercePaymentMethodGroupRelQualifierPersistence.remove(
			commercePaymentMethodGroupRelQualifier);

		return commercePaymentMethodGroupRelQualifier;
	}

	@Override
	public CommercePaymentMethodGroupRelQualifier
			deleteCommercePaymentMethodGroupRelQualifier(
				long commercePaymentMethodGroupRelQualifierId)
		throws PortalException {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				commercePaymentMethodGroupRelQualifierPersistence.
					findByPrimaryKey(commercePaymentMethodGroupRelQualifierId);

		return commercePaymentMethodGroupRelQualifierLocalService.
			deleteCommercePaymentMethodGroupRelQualifier(
				commercePaymentMethodGroupRelQualifier);
	}

	@Override
	public void deleteCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		List<CommercePaymentMethodGroupRelQualifier>
			commercePaymentMethodGroupRelQualifiers =
				commercePaymentMethodGroupRelQualifierPersistence.
					findByCommercePaymentMethodGroupRelId(
						commercePaymentMethodGroupRelId);

		for (CommercePaymentMethodGroupRelQualifier
				commercePaymentMethodGroupRelQualifier :
					commercePaymentMethodGroupRelQualifiers) {

			commercePaymentMethodGroupRelQualifierLocalService.
				deleteCommercePaymentMethodGroupRelQualifier(
					commercePaymentMethodGroupRelQualifier);
		}
	}

	@Override
	public void deleteCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId)
		throws PortalException {

		List<CommercePaymentMethodGroupRelQualifier>
			commercePaymentMethodGroupRelQualifiers =
				commercePaymentMethodGroupRelQualifierPersistence.findByC_C(
					_classNameLocalService.getClassNameId(className),
					commercePaymentMethodGroupRelId);

		for (CommercePaymentMethodGroupRelQualifier
				commercePaymentMethodGroupRelQualifier :
					commercePaymentMethodGroupRelQualifiers) {

			commercePaymentMethodGroupRelQualifierLocalService.
				deleteCommercePaymentMethodGroupRelQualifier(
					commercePaymentMethodGroupRelQualifier);
		}
	}

	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
		getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId, String keywords, int start,
			int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
						classPK),
				commercePaymentMethodGroupRelId,
				CommerceOrderType.class.getName(), keywords,
				CommerceOrderTypeTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceOrderTypeCommercePaymentMethodGroupRelQualifiersCount(
		long commercePaymentMethodGroupRelId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
						commercePaymentMethodGroupRelQualifierId),
				CommerceOrderTypeTable.INSTANCE,
				CommerceOrderTypeTable.INSTANCE.commerceOrderTypeId.eq(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
						classPK),
				commercePaymentMethodGroupRelId,
				CommerceOrderType.class.getName(), keywords,
				CommerceOrderTypeTable.INSTANCE.name));
	}

	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
		getCommercePaymentMethodGroupRelQualifiers(
			String className, long commercePaymentMethodGroupRelId) {

		return commercePaymentMethodGroupRelQualifierPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className),
			commercePaymentMethodGroupRelId);
	}

	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
		getCommerceTermEntryCommercePaymentMethodGroupRelQualifiers(
			long commercePaymentMethodGroupRelId, String keywords, int start,
			int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE),
				CommerceTermEntryTable.INSTANCE,
				CommerceTermEntryTable.INSTANCE.commerceTermEntryId.eq(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
						classPK),
				commercePaymentMethodGroupRelId,
				CommerceTermEntry.class.getName(), keywords,
				CommerceTermEntryTable.INSTANCE.name
			).limit(
				start, end
			));
	}

	@Override
	public int getCommerceTermEntryCommercePaymentMethodGroupRelQualifiersCount(
		long commercePaymentMethodGroupRelId, String keywords) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
						commercePaymentMethodGroupRelQualifierId),
				CommerceTermEntryTable.INSTANCE,
				CommerceTermEntryTable.INSTANCE.commerceTermEntryId.eq(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
						classPK),
				commercePaymentMethodGroupRelId,
				CommerceTermEntry.class.getName(), keywords,
				CommerceTermEntryTable.INSTANCE.name));
	}

	private GroupByStep _getGroupByStep(
		FromStep fromStep, Table innerJoinTable, Predicate innerJoinPredicate,
		Long commercePaymentMethodGroupRelId, String className, String keywords,
		Expression<String> keywordsPredicateExpression) {

		JoinStep joinStep = fromStep.from(
			CommercePaymentMethodGroupRelQualifierTable.INSTANCE
		).innerJoinON(
			CommercePaymentMethodGroupRelTable.INSTANCE,
			CommercePaymentMethodGroupRelTable.INSTANCE.
				commercePaymentMethodGroupRelId.eq(
					CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
						commercePaymentMethodGroupRelId)
		).innerJoinON(
			innerJoinTable, innerJoinPredicate
		);

		return joinStep.where(
			() ->
				CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
					commercePaymentMethodGroupRelId.eq(
						commercePaymentMethodGroupRelId
					).and(
						CommercePaymentMethodGroupRelQualifierTable.INSTANCE.
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
			long classNameId, long classPK,
			long commercePaymentMethodGroupRelId)
		throws PortalException {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				commercePaymentMethodGroupRelQualifierPersistence.fetchByC_C_C(
					classNameId, classPK, commercePaymentMethodGroupRelId);

		if (commercePaymentMethodGroupRelQualifier != null) {
			throw new DuplicateCommercePaymentMethodGroupRelQualifierException();
		}
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}