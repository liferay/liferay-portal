/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.internal.scheduler;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.search.IndexStatusManagerThreadLocal;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.redirect.internal.configuration.RedirectConfiguration;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 * @author Alicia García
 */
@Component(
	configurationPid = "com.liferay.redirect.internal.configuration.RedirectConfiguration",
	service = SchedulerJobConfiguration.class
)
public class CheckRedirectNotFoundEntriesSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			Map<Long, List<String>> deletedUidsMap = new HashMap<>();

			boolean indexReadOnly =
				IndexStatusManagerThreadLocal.isIndexReadOnly();

			try {
				IndexStatusManagerThreadLocal.setIndexReadOnly(true);

				_deleteMaximumOverflowRedirectNotFoundEntries(deletedUidsMap);

				_deleteOldRedirectNotFoundEntries(deletedUidsMap);
			}
			finally {
				IndexStatusManagerThreadLocal.setIndexReadOnly(indexReadOnly);

				for (Map.Entry<Long, List<String>> entry :
						deletedUidsMap.entrySet()) {

					_indexWriterHelper.deleteDocuments(
						entry.getKey(), entry.getValue(), false);
				}
			}
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_redirectConfiguration = ConfigurableUtil.createConfigurable(
			RedirectConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			_redirectConfiguration.checkRedirectNotFoundEntriesInterval(),
			TimeUnit.HOUR);
	}

	private void _deleteMaximumOverflowRedirectNotFoundEntries(
			Map<Long, List<String>> deletedUidsMap)
		throws Exception {

		int redirectNotFoundEntriesCount =
			_redirectNotFoundEntryLocalService.
				getRedirectNotFoundEntriesCount();

		int maximumNumberOfRedirectNotFoundEntries =
			_redirectConfiguration.maximumNumberOfRedirectNotFoundEntries();

		if (redirectNotFoundEntriesCount <
				maximumNumberOfRedirectNotFoundEntries) {

			return;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			_redirectNotFoundEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.setLimit(
				maximumNumberOfRedirectNotFoundEntries,
				redirectNotFoundEntriesCount));
		actionableDynamicQuery.setAddOrderCriteriaMethod(
			dynamicQuery -> dynamicQuery.addOrder(
				OrderFactoryUtil.desc("modifiedDate")));
		actionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<RedirectNotFoundEntry>)
				redirectNotFoundEntry -> _deleteRedirectNotFoundEntry(
					redirectNotFoundEntry, deletedUidsMap));

		actionableDynamicQuery.performActions();
	}

	private void _deleteOldRedirectNotFoundEntries(
			Map<Long, List<String>> deletedUidsMap)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			_redirectNotFoundEntryLocalService.getActionableDynamicQuery();

		int redirectNotFoundEntryMaxAge =
			_redirectConfiguration.redirectNotFoundEntryMaxAge();

		Date thresholdDate = new Date(
			System.currentTimeMillis() -
				(redirectNotFoundEntryMaxAge * Time.DAY));

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.lt("modifiedDate", thresholdDate)));

		actionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod<RedirectNotFoundEntry>)
				redirectNotFoundEntry -> _deleteRedirectNotFoundEntry(
					redirectNotFoundEntry, deletedUidsMap));

		actionableDynamicQuery.performActions();
	}

	private void _deleteRedirectNotFoundEntry(
			RedirectNotFoundEntry redirectNotFoundEntry,
			Map<Long, List<String>> deletedUidsMap)
		throws PortalException {

		long companyId = redirectNotFoundEntry.getCompanyId();
		String uid = _uidFactory.getUID(redirectNotFoundEntry);

		_redirectNotFoundEntryLocalService.deleteRedirectNotFoundEntry(
			redirectNotFoundEntry);

		List<String> uids = deletedUidsMap.computeIfAbsent(
			companyId, key -> new ArrayList<>());

		uids.add(uid);
	}

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	private volatile RedirectConfiguration _redirectConfiguration;

	@Reference
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

	private TriggerConfiguration _triggerConfiguration;

	@Reference
	private UIDFactory _uidFactory;

}