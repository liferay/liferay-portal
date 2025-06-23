/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.impl;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.base.AccountRoleLocalServiceBaseImpl;
import com.liferay.account.service.persistence.AccountEntryPersistence;
import com.liferay.exportimport.kernel.incomplete.model.IncompleteModelManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SortFactory;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.util.PortalInstances;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.account.model.AccountRole",
	service = AopService.class
)
public class AccountRoleLocalServiceImpl
	extends AccountRoleLocalServiceBaseImpl {

	@Override
	public AccountRole addAccountRole(
			String externalReferenceCode, long userId, long accountEntryId,
			String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap)
		throws PortalException {

		Role role = _roleLocalService.addRole(
			externalReferenceCode, userId, AccountRole.class.getName(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, name, titleMap,
			descriptionMap, RoleConstants.TYPE_ACCOUNT, null, null);

		AccountRole accountRole = fetchAccountRoleByRoleId(role.getRoleId());

		if (accountRole != null) {
			accountRole.setAccountEntryId(accountEntryId);

			return updateAccountRole(accountRole);
		}

		accountRole = createAccountRole(counterLocalService.increment());

		accountRole.setExternalReferenceCode(role.getExternalReferenceCode());
		accountRole.setCompanyId(role.getCompanyId());
		accountRole.setAccountEntryId(accountEntryId);
		accountRole.setRoleId(role.getRoleId());

		role.setClassPK(accountRole.getAccountRoleId());

		role = _roleLocalService.updateRole(role);

		_resourceLocalService.addResources(
			role.getCompanyId(), 0, userId, AccountRole.class.getName(),
			accountRole.getAccountRoleId(), false, false, false);

		return accountRoleLocalService.addAccountRole(accountRole);
	}

	@Override
	public void associateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryPersistence.findByPrimaryKey(
			accountEntryId);

		AccountRole accountRole = getAccountRole(accountRoleId);

		_userGroupRoleLocalService.addUserGroupRoles(
			userId, accountEntry.getAccountEntryGroupId(),
			new long[] {accountRole.getRoleId()});
	}

	@Override
	public void associateUser(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException {

		for (long accountRoleId : accountRoleIds) {
			associateUser(accountEntryId, accountRoleId, userId);
		}
	}

	@Override
	public AccountRole deleteAccountRole(AccountRole accountRole)
		throws PortalException {

		accountRole = super.deleteAccountRole(accountRole);

		_resourceLocalService.deleteResource(
			accountRole.getCompanyId(), AccountRole.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, accountRole.getAccountRoleId());

		Role role = _roleLocalService.fetchRole(accountRole.getRoleId());

		if (role != null) {
			_userGroupRoleLocalService.deleteUserGroupRolesByRoleId(
				accountRole.getRoleId());

			_roleLocalService.deleteRole(accountRole.getRoleId());
		}

		return accountRole;
	}

	@Override
	public AccountRole deleteAccountRole(long accountRoleId)
		throws PortalException {

		return deleteAccountRole(getAccountRole(accountRoleId));
	}

	@Override
	public void deleteAccountRolesByCompanyId(long companyId)
		throws PortalException {

		if (!PortalInstances.isCurrentCompanyInDeletionProcess()) {
			throw new UnsupportedOperationException(
				"Deleting account roles by company must be called when " +
					"deleting a company");
		}

		for (AccountRole accountRole :
				accountRolePersistence.findByCompanyId(companyId)) {

			accountRoleLocalService.deleteAccountRole(accountRole);
		}
	}

	@Override
	public AccountRole fetchAccountRoleByRoleId(long roleId) {
		return accountRolePersistence.fetchByRoleId(roleId);
	}

	@Override
	public AccountRole getAccountRoleByRoleId(long roleId)
		throws PortalException {

		return accountRolePersistence.findByRoleId(roleId);
	}

	@Override
	public List<AccountRole> getAccountRoles(long accountEntryId, long userId)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryPersistence.findByPrimaryKey(
			accountEntryId);

		return TransformUtil.transform(
			ListUtil.filter(
				_userGroupRoleLocalService.getUserGroupRoles(
					userId, accountEntry.getAccountEntryGroupId()),
				userGroupRole -> {
					try {
						Role role = userGroupRole.getRole();

						return role.getType() == RoleConstants.TYPE_ACCOUNT;
					}
					catch (PortalException portalException) {
						_log.error(portalException);

						return false;
					}
				}),
			userGroupRole -> getAccountRoleByRoleId(userGroupRole.getRoleId()));
	}

	@Override
	public List<AccountRole> getAccountRolesByAccountEntryIds(
		long companyId, long[] accountEntryIds) {

		return accountRolePersistence.findByC_A(companyId, accountEntryIds);
	}

	@Override
	public List<AccountRole> getAccountRolesByAccountEntryIds(
		long[] accountEntryIds) {

		return accountRolePersistence.findByAccountEntryId(accountEntryIds);
	}

	@Override
	public AccountRole getOrAddIncompleteAccountRole(
			String externalReferenceCode, long companyId, long userId,
			long accountEntryId, String name)
		throws Exception {

		return _incompleteModelManager.getOrAddIncompleteModel(
			AccountRole.class, companyId, externalReferenceCode,
			this::fetchAccountRoleByExternalReferenceCode,
			this::getAccountRoleByExternalReferenceCode,
			() -> {
				Role role = _roleLocalService.getOrAddIncompleteRole(
					externalReferenceCode, companyId, userId,
					AccountRole.class.getName(),
					AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, name,
					RoleConstants.TYPE_ACCOUNT);

				AccountRole accountRole = getAccountRoleByRoleId(
					role.getRoleId());

				accountRole.setAccountEntryId(accountEntryId);

				return updateAccountRole(accountRole);
			});
	}

	@Override
	public boolean hasUserAccountRole(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryPersistence.findByPrimaryKey(
			accountEntryId);

		AccountRole accountRole = getAccountRole(accountRoleId);

		return _userGroupRoleLocalService.hasUserGroupRole(
			userId, accountEntry.getAccountEntryGroupId(),
			accountRole.getRoleId());
	}

	@Override
	public BaseModelSearchResult<AccountRole> searchAccountRoles(
		long companyId, long[] accountEntryIds, String keywords,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<?> orderByComparator) {

		SearchResponse searchResponse = _searcher.search(
			_getSearchRequest(
				companyId, accountEntryIds, keywords, params, start, end,
				orderByComparator));

		SearchHits searchHits = searchResponse.getSearchHits();

		return new BaseModelSearchResult<AccountRole>(
			TransformUtil.transform(
				searchHits.getSearchHits(),
				searchHit -> {
					Document document = searchHit.getDocument();

					long accountRoleId = document.getLong(Field.ENTRY_CLASS_PK);

					AccountRole accountRole = fetchAccountRole(accountRoleId);

					if (accountRole == null) {
						Indexer<AccountRole> indexer =
							IndexerRegistryUtil.getIndexer(AccountRole.class);

						indexer.delete(
							document.getLong(Field.COMPANY_ID),
							document.getString(Field.UID));
					}

					return accountRole;
				}),
			searchResponse.getTotalHits());
	}

	@Override
	public void setUserAccountRoles(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException {

		List<AccountRole> removeAccountRoles = new ArrayList<>();

		List<AccountRole> currentAccountRoles = getAccountRoles(
			accountEntryId, userId);

		for (AccountRole accountRole : currentAccountRoles) {
			if (!ArrayUtil.contains(
					accountRoleIds, accountRole.getAccountRoleId())) {

				removeAccountRoles.add(accountRole);
			}
		}

		associateUser(accountEntryId, accountRoleIds, userId);

		for (AccountRole accountRole : removeAccountRoles) {
			unassociateUser(
				accountEntryId, accountRole.getAccountRoleId(), userId);
		}
	}

	@Override
	public void unassociateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryPersistence.findByPrimaryKey(
			accountEntryId);

		AccountRole accountRole = getAccountRole(accountRoleId);

		_userGroupRoleLocalService.deleteUserGroupRoles(
			userId, accountEntry.getAccountEntryGroupId(),
			new long[] {accountRole.getRoleId()});
	}

	private SearchRequest _getSearchRequest(
		long companyId, long[] accountEntryIds, String keywords,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<?> orderByComparator) {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
			AccountRole.class.getName()
		).emptySearchEnabled(
			true
		).highlightEnabled(
			false
		).withSearchContext(
			searchContext -> {
				searchContext.setCompanyId(companyId);

				if (!Validator.isBlank(keywords)) {
					searchContext.setKeywords(keywords);
				}

				searchContext.setEnd(end);
				searchContext.setStart(start);

				if (orderByComparator != null) {
					searchContext.setSorts(
						_sortFactory.getSort(
							AccountRole.class,
							orderByComparator.getOrderByFields()[0],
							orderByComparator.isAscending() ? "asc" : "desc"));
				}

				if (ArrayUtil.isNotEmpty(accountEntryIds)) {
					searchContext.setAttribute(
						"accountEntryIds", accountEntryIds);
				}

				if (MapUtil.isEmpty(params)) {
					return;
				}

				String[] excludedRoleNames = (String[])params.get(
					"excludedRoleNames");

				if (ArrayUtil.isNotEmpty(excludedRoleNames)) {
					searchContext.setAttribute(
						"excludedRoleNames", excludedRoleNames);
				}

				long permissionUserId = GetterUtil.getLong(
					params.get("permissionUserId"));

				if (permissionUserId != GetterUtil.DEFAULT_LONG) {
					searchContext.setUserId(permissionUserId);
				}
			}
		);

		return searchRequestBuilder.build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountRoleLocalServiceImpl.class);

	@Reference
	private AccountEntryPersistence _accountEntryPersistence;

	@Reference
	private IncompleteModelManager _incompleteModelManager;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private SortFactory _sortFactory;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}