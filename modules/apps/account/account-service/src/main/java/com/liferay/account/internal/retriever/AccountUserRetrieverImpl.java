/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.retriever;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.retriever.AccountUserRetriever;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.SortFieldBuilder;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = AccountUserRetriever.class)
public class AccountUserRetrieverImpl implements AccountUserRetriever {

	@Override
	public long getAccountUsersCount(long accountEntryId) {
		return _accountEntryUserRelLocalService.
			getAccountEntryUserRelsCountByAccountEntryId(accountEntryId);
	}

	@Override
	public BaseModelSearchResult<User> searchAccountRoleUsers(
			long accountEntryId, long accountRoleId, String keywords, int start,
			int end, OrderByComparator<User> orderByComparator)
		throws PortalException {

		AccountEntry accountEntry = _accountEntryLocalService.getAccountEntry(
			accountEntryId);

		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"userGroupRole",
				() -> {
					AccountRole accountRole =
						_accountRoleLocalService.getAccountRole(accountRoleId);

					return (Object)new Long[] {
						accountEntry.getAccountEntryGroupId(),
						accountRole.getRoleId()
					};
				}
			).build();

		List<User> users = _userLocalService.search(
			accountEntry.getCompanyId(), keywords,
			WorkflowConstants.STATUS_APPROVED, params, start, end,
			orderByComparator);

		int total = _userLocalService.searchCount(
			accountEntry.getCompanyId(), keywords,
			WorkflowConstants.STATUS_APPROVED, params);

		return new BaseModelSearchResult<>(users, total);
	}

	@Override
	public BaseModelSearchResult<User> searchAccountUsers(
			long[] accountEntryIds, String keywords,
			LinkedHashMap<String, Serializable> params, int status, int cur,
			int delta, String sortField, boolean reverse)
		throws PortalException {

		if (params == null) {
			params = new LinkedHashMap<>();
		}

		params.put("accountEntryIds", accountEntryIds);

		UserSearchRequestBuilder userSearchRequestBuilder =
			new UserSearchRequestBuilder(
				params, cur, delta, keywords, reverse, status, sortField);

		SearchHits searchHits = _searcher.search(
			userSearchRequestBuilder.build()
		).getSearchHits();

		if (searchHits == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Search hits is null");
			}

			return new BaseModelSearchResult<>(
				Collections.<User>emptyList(), 0);
		}

		List<User> users = TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				long userId = document.getLong("userId");

				User user = _userLocalService.fetchUser(userId);

				if (user == null) {
					Indexer<User> indexer = IndexerRegistryUtil.getIndexer(
						User.class);

					indexer.delete(
						document.getLong(Field.COMPANY_ID),
						document.getString(Field.UID));
				}

				return user;
			});

		return new BaseModelSearchResult<>(users, searchHits.getTotalHits());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AccountUserRetrieverImpl.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private AccountRoleLocalService _accountRoleLocalService;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Searcher _searcher;

	@Reference
	private SortFieldBuilder _sortFieldBuilder;

	@Reference
	private Sorts _sorts;

	@Reference
	private UserLocalService _userLocalService;

	private class UserSearchRequestBuilder {

		public UserSearchRequestBuilder(
			Map<String, Serializable> attributes, int cur, int delta,
			String keywords, boolean reverse, int status, String sortField) {

			_attributes = attributes;
			_cur = cur;
			_delta = delta;
			_keywords = keywords;
			_reverse = reverse;
			_status = status;
			_sortField = sortField;
		}

		public SearchRequest build() {
			SearchRequestBuilder searchRequestBuilder =
				_searchRequestBuilderFactory.builder();

			searchRequestBuilder.entryClassNames(
				User.class.getName()
			).withSearchContext(
				searchContext -> {
					boolean andSearch = false;

					if (Validator.isNull(_keywords)) {
						andSearch = true;
					}
					else {
						searchContext.setKeywords(_keywords);
					}

					searchContext.setAndSearch(andSearch);
					searchContext.setAttributes(
						HashMapBuilder.<String, Serializable>put(
							Field.STATUS, _status
						).put(
							"city", _keywords
						).put(
							"country", _keywords
						).put(
							"firstName", _keywords
						).put(
							"fullName", _keywords
						).put(
							"lastName", _keywords
						).put(
							"middleName", _keywords
						).put(
							"params", new LinkedHashMap<>()
						).put(
							"region", _keywords
						).put(
							"screenName", _keywords
						).put(
							"street", _keywords
						).put(
							"zip", _keywords
						).putAll(
							_attributes
						).build());
					searchContext.setCompanyId(
						CompanyThreadLocal.getCompanyId());

					PermissionChecker permissionChecker =
						PermissionThreadLocal.getPermissionChecker();

					if (permissionChecker != null) {
						searchContext.setUserId(permissionChecker.getUserId());
					}
				}
			).emptySearchEnabled(
				true
			).highlightEnabled(
				false
			);

			if (_cur != QueryUtil.ALL_POS) {
				searchRequestBuilder.from(_cur);
				searchRequestBuilder.size(_delta);
			}

			if (Validator.isNotNull(_sortField)) {
				SortOrder sortOrder = SortOrder.ASC;

				if (_reverse) {
					sortOrder = SortOrder.DESC;
				}

				FieldSort fieldSort = _sorts.field(
					_sortFieldBuilder.getSortField(User.class, _sortField),
					sortOrder);

				searchRequestBuilder.sorts(fieldSort);
			}

			return searchRequestBuilder.build();
		}

		private Map<String, Serializable> _attributes = new HashMap<>();
		private final int _cur;
		private final int _delta;
		private final String _keywords;
		private final boolean _reverse;
		private final String _sortField;
		private final int _status;

	}

}