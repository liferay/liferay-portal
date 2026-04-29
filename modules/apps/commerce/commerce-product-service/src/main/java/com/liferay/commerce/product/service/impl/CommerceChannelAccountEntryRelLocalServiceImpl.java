/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.impl;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryTable;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.exception.DuplicateCommerceChannelAccountEntryRelException;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRelTable;
import com.liferay.commerce.product.service.base.CommerceChannelAccountEntryRelLocalServiceBaseImpl;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ClassNameLocalService;
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
	property = "model.class.name=com.liferay.commerce.product.model.CommerceChannelAccountEntryRel",
	service = AopService.class
)
public class CommerceChannelAccountEntryRelLocalServiceImpl
	extends CommerceChannelAccountEntryRelLocalServiceBaseImpl {

	@Override
	public CommerceChannelAccountEntryRel addCommerceChannelAccountEntryRel(
			long userId, long accountEntryId, String className, long classPK,
			long commerceChannelId, boolean overrideEligibility,
			double priority, int type)
		throws PortalException {

		int commerceChannelAccountEntryRelsCount = 0;
		long classNameId = _classNameLocalService.getClassNameId(className);

		if (CommerceChannelAccountEntryRelConstants.TYPE_USER != type) {
			commerceChannelAccountEntryRelsCount =
				commerceChannelAccountEntryRelPersistence.countByA_C_T(
					accountEntryId, commerceChannelId, type);
		}
		else {
			commerceChannelAccountEntryRelsCount =
				commerceChannelAccountEntryRelPersistence.countByA_C_C_C_T(
					accountEntryId, classNameId, classPK, commerceChannelId,
					type);
		}

		if (commerceChannelAccountEntryRelsCount > 0) {
			throw new DuplicateCommerceChannelAccountEntryRelException();
		}

		User user = _userLocalService.getUser(userId);

		long commerceChannelAccountEntryRelId = counterLocalService.increment();

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.create(
				commerceChannelAccountEntryRelId);

		commerceChannelAccountEntryRel.setCompanyId(user.getCompanyId());
		commerceChannelAccountEntryRel.setUserId(user.getUserId());
		commerceChannelAccountEntryRel.setUserName(user.getFullName());
		commerceChannelAccountEntryRel.setAccountEntryId(accountEntryId);
		commerceChannelAccountEntryRel.setClassNameId(classNameId);
		commerceChannelAccountEntryRel.setClassPK(classPK);
		commerceChannelAccountEntryRel.setCommerceChannelId(commerceChannelId);
		commerceChannelAccountEntryRel.setOverrideEligibility(
			overrideEligibility);
		commerceChannelAccountEntryRel.setPriority(priority);
		commerceChannelAccountEntryRel.setType(type);

		commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.update(
				commerceChannelAccountEntryRel);

		if ((className != null) && className.equals(Address.class.getName()) &&
			(commerceChannelAccountEntryRel.getCommerceChannelId() == 0)) {

			_updateDefaultAccountEntryAddress(
				commerceChannelAccountEntryRel.getAccountEntryId(),
				commerceChannelAccountEntryRel.getClassPK(),
				commerceChannelAccountEntryRel.getType());
		}

		return commerceChannelAccountEntryRel;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CommerceChannelAccountEntryRel deleteCommerceChannelAccountEntryRel(
			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel)
		throws PortalException {

		commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.remove(
				commerceChannelAccountEntryRel);

		if ((_classNameLocalService.getClassNameId(Address.class.getName()) ==
				commerceChannelAccountEntryRel.getClassNameId()) &&
			(commerceChannelAccountEntryRel.getCommerceChannelId() == 0)) {

			_updateDefaultAccountEntryAddress(
				commerceChannelAccountEntryRel.getAccountEntryId(), 0,
				commerceChannelAccountEntryRel.getType());
		}

		return commerceChannelAccountEntryRel;
	}

	@Override
	public CommerceChannelAccountEntryRel deleteCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId)
		throws PortalException {

		return commerceChannelAccountEntryRelLocalService.
			deleteCommerceChannelAccountEntryRel(
				commerceChannelAccountEntryRelPersistence.findByPrimaryKey(
					commerceChannelAccountEntryRelId));
	}

	@Override
	public void deleteCommerceChannelAccountEntryRels(
			String className, long classPK)
		throws PortalException {

		List<CommerceChannelAccountEntryRel> commerceChannelAccountEntryRels =
			commerceChannelAccountEntryRelPersistence.findByC_C(
				_classNameLocalService.getClassNameId(className), classPK);

		for (CommerceChannelAccountEntryRel commerceChannelAccountEntryRel :
				commerceChannelAccountEntryRels) {

			commerceChannelAccountEntryRelLocalService.
				deleteCommerceChannelAccountEntryRel(
					commerceChannelAccountEntryRel);
		}
	}

	@Override
	public void deleteCommerceChannelAccountEntryRelsByAccountEntryId(
			long accountEntryId)
		throws PortalException {

		List<CommerceChannelAccountEntryRel> commerceChannelAccountEntryRels =
			commerceChannelAccountEntryRelPersistence.findByAccountEntryId(
				accountEntryId);

		for (CommerceChannelAccountEntryRel commerceChannelAccountEntryRel :
				commerceChannelAccountEntryRels) {

			commerceChannelAccountEntryRelLocalService.
				deleteCommerceChannelAccountEntryRel(
					commerceChannelAccountEntryRel);
		}
	}

	@Override
	public void deleteCommerceChannelAccountEntryRelsByCommerceChannelId(
			long commerceChannelId)
		throws PortalException {

		List<CommerceChannelAccountEntryRel> commerceChannelAccountEntryRels =
			commerceChannelAccountEntryRelPersistence.findByCommerceChannelId(
				commerceChannelId);

		for (CommerceChannelAccountEntryRel commerceChannelAccountEntryRel :
				commerceChannelAccountEntryRels) {

			commerceChannelAccountEntryRelLocalService.
				deleteCommerceChannelAccountEntryRel(
					commerceChannelAccountEntryRel);
		}
	}

	@Override
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long accountEntryId, long commerceChannelId, int type) {

		List<CommerceChannelAccountEntryRel> commerceChannelAccountEntryRels =
			commerceChannelAccountEntryRelPersistence.dslQuery(
				DSLQueryFactoryUtil.select(
					CommerceChannelAccountEntryRelTable.INSTANCE
				).from(
					CommerceChannelAccountEntryRelTable.INSTANCE
				).where(
					_getPredicate(
						accountEntryId,
						CommerceChannelAccountEntryRelTable.INSTANCE.
							accountEntryId,
						commerceChannelId,
						CommerceChannelAccountEntryRelTable.INSTANCE.
							commerceChannelId,
						type, CommerceChannelAccountEntryRelTable.INSTANCE.type)
				).orderBy(
					CommerceChannelAccountEntryRelTable.INSTANCE.
						commerceChannelId.descending(),
					CommerceChannelAccountEntryRelTable.INSTANCE.priority.
						descending()
				).limit(
					0, 1
				));

		if (commerceChannelAccountEntryRels.isEmpty()) {
			return null;
		}

		return commerceChannelAccountEntryRels.get(0);
	}

	@Override
	public CommerceChannelAccountEntryRel fetchCommerceChannelAccountEntryRel(
		long accountEntryId, String className, long classPK,
		long commerceChannelId, int type) {

		return commerceChannelAccountEntryRelPersistence.fetchByA_C_C_C_T(
			accountEntryId, _classNameLocalService.getClassNameId(className),
			classPK, commerceChannelId, type);
	}

	@Override
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long accountEntryId, int type, int start, int end,
			OrderByComparator<CommerceChannelAccountEntryRel>
				orderByComparator) {

		return commerceChannelAccountEntryRelPersistence.findByA_T(
			accountEntryId, type, start, end, orderByComparator);
	}

	@Override
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			long commerceChannelId, String name, int type, int start, int end) {

		return dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(
					CommerceChannelAccountEntryRelTable.INSTANCE
				).from(
					CommerceChannelAccountEntryRelTable.INSTANCE
				),
				commerceChannelId, name, type
			).limit(
				start, end
			));
	}

	@Override
	public List<CommerceChannelAccountEntryRel>
		getCommerceChannelAccountEntryRels(
			String className, long classPK, long commerceChannelId, int type) {

		return commerceChannelAccountEntryRelPersistence.findByC_C_C_T(
			_classNameLocalService.getClassNameId(className), classPK,
			commerceChannelId, type);
	}

	@Override
	public int getCommerceChannelAccountEntryRelsCount(
		long accountEntryId, int type) {

		return commerceChannelAccountEntryRelPersistence.countByA_T(
			accountEntryId, type);
	}

	@Override
	public int getCommerceChannelAccountEntryRelsCount(
		long commerceChannelId, String name, int type) {

		return dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.count(
				).from(
					CommerceChannelAccountEntryRelTable.INSTANCE
				),
				commerceChannelId, name, type));
	}

	@Override
	public CommerceChannelAccountEntryRel updateCommerceChannelAccountEntryRel(
			long commerceChannelAccountEntryRelId, long commerceChannelId,
			long classPK, boolean overrideEligibility, double priority)
		throws PortalException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.findByPrimaryKey(
				commerceChannelAccountEntryRelId);

		CommerceChannelAccountEntryRel oldCommerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.fetchByA_C_C_C_T(
				commerceChannelAccountEntryRel.getAccountEntryId(),
				commerceChannelAccountEntryRel.getClassNameId(), classPK,
				commerceChannelId, commerceChannelAccountEntryRel.getType());

		if (oldCommerceChannelAccountEntryRel != null) {
			long oldCommerceChannelAccountEntryRelId =
				oldCommerceChannelAccountEntryRel.
					getCommerceChannelAccountEntryRelId();

			if (oldCommerceChannelAccountEntryRelId !=
					commerceChannelAccountEntryRel.
						getCommerceChannelAccountEntryRelId()) {

				throw new DuplicateCommerceChannelAccountEntryRelException();
			}
		}

		commerceChannelAccountEntryRel.setClassPK(classPK);
		commerceChannelAccountEntryRel.setCommerceChannelId(commerceChannelId);
		commerceChannelAccountEntryRel.setOverrideEligibility(
			overrideEligibility);
		commerceChannelAccountEntryRel.setPriority(priority);

		commerceChannelAccountEntryRel =
			commerceChannelAccountEntryRelPersistence.update(
				commerceChannelAccountEntryRel);

		if ((_classNameLocalService.getClassNameId(Address.class.getName()) ==
				commerceChannelAccountEntryRel.getClassNameId()) &&
			(commerceChannelAccountEntryRel.getCommerceChannelId() == 0)) {

			_updateDefaultAccountEntryAddress(
				commerceChannelAccountEntryRel.getAccountEntryId(),
				commerceChannelAccountEntryRel.getClassPK(),
				commerceChannelAccountEntryRel.getType());
		}

		return commerceChannelAccountEntryRel;
	}

	private GroupByStep _getGroupByStep(
		JoinStep joinStep, long commerceChannelId, String name, int type) {

		Predicate predicate =
			CommerceChannelAccountEntryRelTable.INSTANCE.commerceChannelId.eq(
				commerceChannelId
			).and(
				CommerceChannelAccountEntryRelTable.INSTANCE.type.eq(type)
			);

		if (!Validator.isBlank(name)) {
			joinStep = joinStep.leftJoinOn(
				AccountEntryTable.INSTANCE,
				CommerceChannelAccountEntryRelTable.INSTANCE.accountEntryId.eq(
					AccountEntryTable.INSTANCE.accountEntryId));

			predicate = predicate.and(
				AccountEntryTable.INSTANCE.name.like(name));
		}

		return joinStep.where(predicate);
	}

	private Predicate _getPredicate(
		long accountEntryId,
		Column<CommerceChannelAccountEntryRelTable, Long> accountEntryIdColumn,
		long commerceChannelId,
		Column<CommerceChannelAccountEntryRelTable, Long>
			commerceChannelIdColumn,
		int type,
		Column<CommerceChannelAccountEntryRelTable, Integer> typeColumn) {

		return accountEntryIdColumn.eq(
			accountEntryId
		).and(
			commerceChannelIdColumn.in(new Long[] {commerceChannelId, 0L})
		).and(
			typeColumn.eq(type)
		);
	}

	private void _updateDefaultAccountEntryAddress(
			long accountEntryId, long addressId, int type)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryId);

		if (accountEntry == null) {
			return;
		}

		if ((type ==
				CommerceChannelAccountEntryRelConstants.TYPE_BILLING_ADDRESS) &&
			(accountEntry.getDefaultBillingAddressId() != addressId)) {

			_accountEntryLocalService.updateDefaultBillingAddressId(
				accountEntry.getAccountEntryId(), addressId);
		}
		else if ((type ==
					CommerceChannelAccountEntryRelConstants.
						TYPE_SHIPPING_ADDRESS) &&
				 (accountEntry.getDefaultShippingAddressId() != addressId)) {

			_accountEntryLocalService.updateDefaultShippingAddressId(
				accountEntry.getAccountEntryId(), addressId);
		}
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private UserLocalService _userLocalService;

}