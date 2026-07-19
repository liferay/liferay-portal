/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.DuplicateAccountGroupRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.model.AccountGroupRelTable;
import com.liferay.account.model.AccountGroupTable;
import com.liferay.account.service.base.AccountGroupRelLocalServiceBaseImpl;
import com.liferay.account.service.persistence.AccountEntryPersistence;
import com.liferay.account.service.persistence.AccountGroupPersistence;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.account.model.AccountGroupRel",
	service = AopService.class
)
public class AccountGroupRelLocalServiceImpl
	extends AccountGroupRelLocalServiceBaseImpl {

	@Override
	public AccountGroupRel addAccountGroupRel(
			long accountGroupId, String className, long classPK)
		throws PortalException {

		long classNameId = _classNameLocalService.getClassNameId(className);

		AccountGroupRel accountGroupRel =
			accountGroupRelPersistence.fetchByA_C_C(
				accountGroupId, classNameId, classPK);

		if (accountGroupRel != null) {
			throw new DuplicateAccountGroupRelException();
		}

		if (Objects.equals(AccountEntry.class.getName(), className) &&
			(classPK != AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT) &&
			(classPK != AccountConstants.ACCOUNT_ENTRY_ID_GUEST)) {

			_accountEntryPersistence.findByPrimaryKey(classPK);
		}

		accountGroupRel = createAccountGroupRel(
			counterLocalService.increment());

		AccountGroup accountGroup = _accountGroupPersistence.findByPrimaryKey(
			accountGroupId);

		User user = null;

		try {
			user = GuestOrUserUtil.getGuestOrUser(accountGroup.getCompanyId());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		if (user == null) {
			user = _userLocalService.getGuestUser(accountGroup.getCompanyId());
		}

		accountGroupRel.setCompanyId(user.getCompanyId());
		accountGroupRel.setUserId(user.getUserId());
		accountGroupRel.setUserName(user.getFullName());

		accountGroupRel.setCreateDate(new Date());
		accountGroupRel.setModifiedDate(new Date());
		accountGroupRel.setAccountGroupId(accountGroupId);
		accountGroupRel.setClassNameId(classNameId);
		accountGroupRel.setClassPK(classPK);

		return addAccountGroupRel(accountGroupRel);
	}

	@Override
	public void addAccountGroupRels(
			long accountGroupId, String className, long[] classPKs)
		throws PortalException {

		for (long classPK : classPKs) {
			addAccountGroupRel(accountGroupId, className, classPK);
		}
	}

	@Override
	public void deleteAccountGroupRels(
			long accountGroupId, String className, long[] classPKs)
		throws PortalException {

		for (long classPK : classPKs) {
			accountGroupRelPersistence.removeByA_C_C(
				accountGroupId,
				_classNameLocalService.getClassNameId(className), classPK);
		}
	}

	@Override
	public void deleteAccountGroupRels(String className, long[] classPKs) {
		for (long classPK : classPKs) {
			accountGroupRelPersistence.removeByC_C(
				_classNameLocalService.getClassNameId(className), classPK);
		}
	}

	@Override
	public void deleteAccountGroupRelsByAccountGroupId(long accountGroupId) {
		accountGroupRelPersistence.removeByAccountGroupId(accountGroupId);
	}

	@Override
	public AccountGroupRel fetchAccountGroupRel(
		long accountGroupId, String className, long classPK) {

		return accountGroupRelPersistence.fetchByA_C_C(
			accountGroupId, _classNameLocalService.getClassNameId(className),
			classPK);
	}

	@Override
	public List<AccountGroupRel> getAccountGroupRels(
		long accountGroupId, String className) {

		return accountGroupRelPersistence.findByA_C(
			accountGroupId, _classNameLocalService.getClassNameId(className));
	}

	@Override
	public List<AccountGroupRel> getAccountGroupRels(
		long[] accountGroupIds, String className, long classPK, String keywords,
		int start, int end) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
				AccountGroupRelTable.INSTANCE
			).from(
				AccountGroupRelTable.INSTANCE
			).innerJoinON(
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					AccountGroupRelTable.INSTANCE.accountGroupId)
			).where(
				_getPredicate(accountGroupIds, className, classPK, keywords)
			).limit(
				start, end
			));
	}

	@Override
	public List<AccountGroupRel> getAccountGroupRels(
		String className, long classPK) {

		return accountGroupRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<AccountGroupRel> getAccountGroupRels(
		String className, long classPK, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return accountGroupRelPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK, start,
			end, orderByComparator);
	}

	@Override
	public List<AccountGroupRel> getAccountGroupRelsByAccountGroupId(
		long accountGroupId) {

		return accountGroupRelPersistence.findByAccountGroupId(accountGroupId);
	}

	@Override
	public List<AccountGroupRel> getAccountGroupRelsByAccountGroupId(
		long accountGroupId, int start, int end,
		OrderByComparator<AccountGroupRel> orderByComparator) {

		return accountGroupRelPersistence.findByAccountGroupId(
			accountGroupId, start, end, orderByComparator);
	}

	@Override
	public int getAccountGroupRelsCount(
		long[] accountGroupIds, String className, long classPK,
		String keywords) {

		return dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				AccountGroupRelTable.INSTANCE
			).innerJoinON(
				AccountGroupTable.INSTANCE,
				AccountGroupTable.INSTANCE.accountGroupId.eq(
					AccountGroupRelTable.INSTANCE.accountGroupId)
			).where(
				_getPredicate(accountGroupIds, className, classPK, keywords)
			));
	}

	@Override
	public int getAccountGroupRelsCount(String className, long classPK) {
		return accountGroupRelPersistence.countByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public long getAccountGroupRelsCountByAccountGroupId(long accountGroupId) {
		return accountGroupRelPersistence.countByAccountGroupId(accountGroupId);
	}

	@Override
	public int getAccountGroupRelsCountByClassName(
		long accountGroupId, String className) {

		return accountGroupRelPersistence.countByA_C(
			accountGroupId, _classNameLocalService.getClassNameId(className));
	}

	private Predicate _getPredicate(
		long[] accountGroupIds, String className, long classPK,
		String keywords) {

		Predicate predicate = AccountGroupRelTable.INSTANCE.classNameId.eq(
			_classNameLocalService.getClassNameId(className)
		).and(
			AccountGroupRelTable.INSTANCE.classPK.eq(classPK)
		).and(
			() -> {
				if (ArrayUtil.isEmpty(accountGroupIds)) {
					return null;
				}

				return AccountGroupRelTable.INSTANCE.accountGroupId.in(
					ArrayUtil.toArray(accountGroupIds));
			}
		);

		if (Validator.isNotNull(keywords)) {
			return Predicate.withParentheses(
				predicate.and(
					_customSQL.getKeywordsPredicate(
						DSLFunctionFactoryUtil.lower(
							AccountGroupTable.INSTANCE.name),
						_customSQL.keywords(keywords, true))));
		}

		return predicate;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountGroupRelLocalServiceImpl.class);

	@Reference
	private AccountEntryPersistence _accountEntryPersistence;

	@Reference
	private AccountGroupPersistence _accountGroupPersistence;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private UserLocalService _userLocalService;

}