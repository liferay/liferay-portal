/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.AccountGroupNameException;
import com.liferay.account.exception.NoSuchGroupException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.base.AccountGroupLocalServiceBaseImpl;
import com.liferay.account.service.persistence.AccountGroupRelPersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.account.model.AccountGroup",
	service = AopService.class
)
public class AccountGroupLocalServiceImpl
	extends AccountGroupLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AccountGroup addAccountGroup(
			String externalReferenceCode, long userId, String description,
			String name, ServiceContext serviceContext)
		throws PortalException {

		_validateName(name);

		long accountGroupId = counterLocalService.increment();

		AccountGroup accountGroup = accountGroupPersistence.create(
			accountGroupId);

		User user = _userLocalService.getUser(userId);

		accountGroup.setExternalReferenceCode(externalReferenceCode);
		accountGroup.setCompanyId(user.getCompanyId());
		accountGroup.setUserId(user.getUserId());
		accountGroup.setUserName(user.getFullName());

		accountGroup.setDefaultAccountGroup(false);
		accountGroup.setDescription(description);
		accountGroup.setName(name);
		accountGroup.setType(AccountConstants.ACCOUNT_GROUP_TYPE_STATIC);
		accountGroup.setExpandoBridgeAttributes(serviceContext);

		if (LazyReferencingThreadLocal.isIncompleteModel()) {
			accountGroup.setStatus(WorkflowConstants.STATUS_INCOMPLETE);
		}
		else {
			accountGroup.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		accountGroup = accountGroupPersistence.update(accountGroup);

		_resourceLocalService.addResources(
			user.getCompanyId(), 0, user.getUserId(),
			AccountGroup.class.getName(), accountGroupId, false, false, false);

		return accountGroup;
	}

	@Override
	public AccountGroup checkGuestAccountGroup(long companyId)
		throws PortalException {

		AccountGroup accountGroup = accountGroupPersistence.fetchByC_D_First(
			companyId, true, null);

		if (accountGroup != null) {
			return accountGroup;
		}

		accountGroup = createAccountGroup(counterLocalService.increment());

		accountGroup.setCompanyId(companyId);

		User user = _userLocalService.getGuestUser(companyId);

		accountGroup.setUserId(user.getUserId());
		accountGroup.setUserName(user.getFullName());

		accountGroup.setDefaultAccountGroup(true);
		accountGroup.setDescription(
			"This account group is used for guest users.");
		accountGroup.setName(AccountConstants.ACCOUNT_GROUP_NAME_GUEST);

		_resourceLocalService.addResources(
			user.getCompanyId(), 0, user.getUserId(),
			AccountGroup.class.getName(), accountGroup.getAccountGroupId(),
			false, false, false);

		return accountGroupPersistence.update(accountGroup);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public AccountGroup deleteAccountGroup(AccountGroup accountGroup)
		throws PortalException {

		accountGroupPersistence.remove(accountGroup);

		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelPersistence.findByAccountGroupId(
				accountGroup.getAccountGroupId());

		for (AccountGroupRel accountGroupRel : accountGroupRels) {
			_accountGroupRelPersistence.remove(accountGroupRel);
		}

		_resourceLocalService.deleteResource(
			accountGroup.getCompanyId(), AccountGroup.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			accountGroup.getAccountGroupId());

		return accountGroup;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public AccountGroup deleteAccountGroup(long accountGroupId)
		throws PortalException {

		return accountGroupLocalService.deleteAccountGroup(
			accountGroupLocalService.getAccountGroup(accountGroupId));
	}

	@Override
	public long[] getAccountGroupIds(long accountEntryId) {
		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelPersistence.findByC_C(
				_classNameLocalService.getClassNameId(
					AccountEntry.class.getName()),
				accountEntryId);

		if (accountGroupRels.isEmpty()) {
			return new long[0];
		}

		return ArrayUtil.sortedUnique(
			TransformUtil.transformToLongArray(
				accountGroupRels, AccountGroupRel::getAccountGroupId));
	}

	@Override
	public List<AccountGroup> getAccountGroups(
		long companyId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return accountGroupPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<AccountGroup> getAccountGroups(
		long companyId, String name, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		if (Validator.isNull(name)) {
			return accountGroupPersistence.findByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return accountGroupPersistence.findByC_LikeN(
			companyId, StringUtil.quote(name, StringPool.PERCENT), start, end,
			orderByComparator);
	}

	@Override
	public List<AccountGroup> getAccountGroupsByAccountEntryId(
		long accountEntryId, int start, int end) {

		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelPersistence.findByC_C(
				_classNameLocalService.getClassNameId(
					AccountEntry.class.getName()),
				accountEntryId, start, end, null);

		if (accountGroupRels.isEmpty()) {
			return new ArrayList<>();
		}

		return accountGroupPersistence.findByAccountGroupId(
			TransformUtil.transformToLongArray(
				accountGroupRels, AccountGroupRel::getAccountGroupId));
	}

	@Override
	public List<AccountGroup> getAccountGroupsByAccountGroupId(
		long[] accountGroupIds) {

		return accountGroupPersistence.findByAccountGroupId(accountGroupIds);
	}

	@Override
	public int getAccountGroupsCount(long companyId) {
		return accountGroupPersistence.countByCompanyId(companyId);
	}

	@Override
	public long getAccountGroupsCount(long companyId, String name) {
		if (Validator.isNull(name)) {
			return accountGroupPersistence.countByCompanyId(companyId);
		}

		return accountGroupPersistence.countByC_LikeN(
			companyId, StringUtil.quote(name, StringPool.PERCENT));
	}

	@Override
	public int getAccountGroupsCountByAccountEntryId(long accountEntryId) {
		return _accountGroupRelPersistence.countByC_C(
			_classNameLocalService.getClassNameId(AccountEntry.class.getName()),
			accountEntryId);
	}

	@Override
	public AccountGroup getDefaultAccountGroup(long companyId) {
		return accountGroupPersistence.fetchByC_D_First(companyId, true, null);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AccountGroup getOrAddIncompleteAccountGroup(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws Exception {

		AccountGroup accountGroup = fetchAccountGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (accountGroup != null) {
			return accountGroup;
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchGroupException(
				StringBundler.concat(
					"Unable to find account group with external reference code",
					externalReferenceCode, " and company ", companyId));
		}

		if (Validator.isNull(name)) {
			name = externalReferenceCode;
		}

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setIncompleteModelWithSafeCloseable(
					true)) {

			return accountGroupLocalService.addAccountGroup(
				externalReferenceCode, userId, StringPool.BLANK, name,
				new ServiceContext());
		}
	}

	@Override
	public boolean hasDefaultAccountGroup(long companyId) {
		int count = accountGroupPersistence.countByC_D(companyId, true);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public BaseModelSearchResult<AccountGroup> searchAccountGroups(
		long companyId, String keywords, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return searchAccountGroups(
			companyId, keywords, null, start, end, orderByComparator);
	}

	@Override
	public BaseModelSearchResult<AccountGroup> searchAccountGroups(
		long companyId, String keywords, LinkedHashMap<String, Object> params,
		int start, int end, OrderByComparator<AccountGroup> orderByComparator) {

		try {
			SearchContext searchContext = _buildSearchContext(
				companyId, start, end, orderByComparator);

			searchContext.setKeywords(keywords);

			if (MapUtil.isNotEmpty(params)) {
				long[] accountEntryIds = (long[])params.get("accountEntryIds");

				if (ArrayUtil.isNotEmpty(accountEntryIds)) {
					searchContext.setAttribute(
						"accountEntryIds", accountEntryIds);
				}

				long permissionUserId = GetterUtil.getLong(
					params.get("permissionUserId"));

				if (permissionUserId != GetterUtil.DEFAULT_LONG) {
					searchContext.setUserId(permissionUserId);
				}
			}

			return _searchAccountGroups(searchContext);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AccountGroup updateAccountGroup(
			String externalReferenceCode, long accountGroupId,
			String description, String name, ServiceContext serviceContext)
		throws PortalException {

		_validateName(name);

		AccountGroup accountGroup = accountGroupPersistence.fetchByPrimaryKey(
			accountGroupId);

		accountGroup.setExternalReferenceCode(externalReferenceCode);
		accountGroup.setDescription(description);
		accountGroup.setName(name);
		accountGroup.setExpandoBridgeAttributes(serviceContext);

		if (accountGroup.getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			accountGroup.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		return accountGroupPersistence.update(accountGroup);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AccountGroup updateExternalReferenceCode(
			AccountGroup accountGroup, String externalReferenceCode)
		throws PortalException {

		if (Objects.equals(
				accountGroup.getExternalReferenceCode(),
				externalReferenceCode)) {

			return accountGroup;
		}

		accountGroup.setExternalReferenceCode(externalReferenceCode);

		return updateAccountGroup(accountGroup);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AccountGroup updateExternalReferenceCode(
			long accountGroupId, String externalReferenceCode)
		throws PortalException {

		return updateExternalReferenceCode(
			getAccountGroup(accountGroupId), externalReferenceCode);
	}

	private SearchContext _buildSearchContext(
		long companyId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setEnd(end);

		if (orderByComparator != null) {
			String[] orderByFields = orderByComparator.getOrderByFields();

			if (ArrayUtil.isNotEmpty(orderByFields)) {
				searchContext.setSorts(
					SortFactoryUtil.getSort(
						AccountGroup.class, orderByFields[0],
						orderByComparator.isAscending() ? "asc" : "desc"));
			}
		}

		searchContext.setStart(start);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private BaseModelSearchResult<AccountGroup> _searchAccountGroups(
			SearchContext searchContext)
		throws PortalException {

		Indexer<AccountGroup> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			AccountGroup.class);

		for (int i = 0; i < 10; i++) {
			Hits hits = indexer.search(searchContext, _SELECTED_FIELD_NAMES);

			List<AccountGroup> accountGroups = TransformUtil.transform(
				hits.toList(),
				document -> {
					long accountGroupId = GetterUtil.getLong(
						document.get(Field.ENTRY_CLASS_PK));

					AccountGroup accountGroup =
						accountGroupPersistence.fetchByPrimaryKey(
							accountGroupId);

					if (accountGroup == null) {
						long companyId = GetterUtil.getLong(
							document.get(Field.COMPANY_ID));

						indexer.delete(companyId, document.getUID());
					}

					return accountGroup;
				});

			if (accountGroups != null) {
				return new BaseModelSearchResult<>(
					accountGroups, hits.getLength());
			}
		}

		throw new SearchException(
			"Unable to fix the search index after 10 attempts");
	}

	private void _validateName(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new AccountGroupNameException("Name is null");
		}
	}

	private static final String[] _SELECTED_FIELD_NAMES = {
		Field.ENTRY_CLASS_PK, Field.COMPANY_ID
	};

	@Reference
	private AccountGroupRelPersistence _accountGroupRelPersistence;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}