/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search;

import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.spi.reindexer.BulkReindexer;

import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = BulkReindexer.class
)
public class UserBulkReindexer implements BulkReindexer {

	@Override
	public void reindex(long companyId, Collection<Long> classPKs) {
		int size = classPKs.size();

		int maxParameters = Math.min(
			DBManagerUtil.getDBInMaxParameters(),
			DBManagerUtil.getDBMaxParameters());

		if (size <= maxParameters) {
			_reindex(companyId, classPKs);

			return;
		}

		List<Long> classPKsList = ListUtil.fromCollection(classPKs);

		int start = 0;
		int end = maxParameters;

		while (start < size) {
			_reindex(companyId, ListUtil.subList(classPKsList, start, end));

			end += maxParameters;
			start += maxParameters;
		}
	}

	@Reference(
		target = "(indexer.class.name=com.liferay.portal.kernel.model.User)"
	)
	protected Indexer<User> indexer;

	@Reference
	protected UserLocalService userLocalService;

	private void _reindex(long companyId, Collection<Long> classPKs) {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			userLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.in("userId", classPKs)));
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(User user) -> {
				if (!user.isGuestUser()) {
					try {
						return indexer.getDocument(user);
					}
					catch (PortalException portalException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to index user " + user.getUserId(),
								portalException);
						}
					}
				}

				return null;
			});

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserBulkReindexer.class);

}