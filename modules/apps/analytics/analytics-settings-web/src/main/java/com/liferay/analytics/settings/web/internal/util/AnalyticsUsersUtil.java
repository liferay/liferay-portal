/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.web.internal.util;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.constants.SearchContextAttributes;

import java.util.Collections;
import java.util.List;

/**
 * @author Rachael Koestartyo
 */
public class AnalyticsUsersUtil {

	public static int getCompanyUsersCount(long companyId) {
		if (!_isIndexerEnabled()) {
			int activeUsersCount = UserLocalServiceUtil.getUsersCount(
				companyId, WorkflowConstants.STATUS_APPROVED);

			int analyticsAdministratorsCount = 0;

			Role analyticsAdministratorRole =
				_fetchAnalyticsAdministratorRole();

			if (analyticsAdministratorRole != null) {
				try {
					analyticsAdministratorsCount =
						UserLocalServiceUtil.getRoleUsersCount(
							analyticsAdministratorRole.getRoleId(),
							WorkflowConstants.STATUS_APPROVED);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to get analytics administrators count",
							exception);
					}
				}
			}

			return activeUsersCount - analyticsAdministratorsCount;
		}

		SearchContext searchContext = _createSearchContext();

		searchContext.setCompanyId(companyId);

		_populateSearchContext(searchContext);

		return _getUsersCount(searchContext);
	}

	public static int getOrganizationsAndUserGroupsUsersCount(
		long[] organizationIds, long[] userGroupIds) {

		if (ArrayUtil.isEmpty(organizationIds) &&
			ArrayUtil.isEmpty(userGroupIds)) {

			return 0;
		}

		if (!_isIndexerEnabled()) {
			return UserLocalServiceUtil.getOrganizationsAndUserGroupsUsersCount(
				organizationIds, userGroupIds);
		}

		SearchContext searchContext = _createSearchContext();

		searchContext.setAttribute("selectedOrganizationIds", organizationIds);
		searchContext.setAttribute("selectedUserGroupIds", userGroupIds);
		searchContext.setCompanyId(CompanyThreadLocal.getCompanyId());

		_populateSearchContext(searchContext);

		return _getUsersCount(searchContext);
	}

	public static List<ExpandoColumn> getUserExpandoColumns(long companyId) {
		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchTable(
			companyId,
			ClassNameLocalServiceUtil.getClassNameId(User.class.getName()),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable != null) {
			return ExpandoColumnLocalServiceUtil.getColumns(
				expandoTable.getTableId());
		}

		return Collections.emptyList();
	}

	private static SearchContext _createSearchContext() {
		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH, Boolean.TRUE);
		searchContext.setEnd(0);
		searchContext.setEntryClassNames(new String[] {User.class.getName()});
		searchContext.setStart(0);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);

		return searchContext;
	}

	private static Role _fetchAnalyticsAdministratorRole() {
		return RoleLocalServiceUtil.fetchRole(
			CompanyThreadLocal.getCompanyId(),
			RoleConstants.ANALYTICS_ADMINISTRATOR);
	}

	private static int _getUsersCount(SearchContext searchContext) {
		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		try {
			Hits hits = indexer.search(searchContext);

			return hits.getLength();
		}
		catch (SearchException searchException) {
			_log.error(searchException);

			return 0;
		}
	}

	private static boolean _isIndexerEnabled() {
		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		return indexer.isIndexerEnabled();
	}

	private static void _populateSearchContext(SearchContext searchContext) {
		Role role = _fetchAnalyticsAdministratorRole();

		if (role != null) {
			searchContext.setAttribute(
				"excludedRoleIds", new long[] {role.getRoleId()});
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsUsersUtil.class);

}