/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.scheduler;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.spi.reindexer.BulkReindexer;
import com.liferay.segments.configuration.SegmentsConfiguration;
import com.liferay.segments.internal.helper.IndexerHelper;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	configurationPid = "com.liferay.segments.configuration.SegmentsConfiguration",
	service = SchedulerJobConfiguration.class
)
public class SegmentsEntryRelIndexerSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			ActionableDynamicQuery actionableDynamicQuery =
				_segmentsEntryLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property activeProperty = PropertyFactoryUtil.forName(
						"active");

					dynamicQuery.add(activeProperty.eq(true));
				});

			Map<Long, Set<Long>> classPKsMap = new HashMap<>();

			actionableDynamicQuery.setPerformActionMethod(
				(SegmentsEntry segmentsEntry) -> _reindex(
					classPKsMap, segmentsEntry));

			actionableDynamicQuery.performActions();

			for (Map.Entry<Long, Set<Long>> entry : classPKsMap.entrySet()) {
				Set<Long> classPKs = entry.getValue();
				long companyId = entry.getKey();

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Start indexing ", classPKs.size(),
							" users of company ", companyId));
				}

				_bulkReindexer.reindex(companyId, classPKs);

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							classPKs.size(), " users of company ", companyId,
							" were indexed successfully"));
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
		SegmentsConfiguration segmentsConfiguration =
			ConfigurableUtil.createConfigurable(
				SegmentsConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			segmentsConfiguration.segmentsPreviewCheckInterval(),
			TimeUnit.MINUTE);

		_indexerHelper = new IndexerHelper(
			_indexer, _portal, _segmentsEntryLocalService,
			_segmentsEntryProviderRegistry, _segmentsEntryRelLocalService);
	}

	private void _reindex(
		Map<Long, Set<Long>> classPKsMap, SegmentsEntry segmentsEntry) {

		try {
			Set<Long> newClassPKs = _indexerHelper.getNewClassPKs(
				segmentsEntry.getSegmentsEntryId());

			_indexerHelper.updateDatabase(
				segmentsEntry.getSegmentsEntryId(), newClassPKs);

			Set<Long> indexableClassPKs = _indexerHelper.getIndexableClassPKs(
				segmentsEntry.getCompanyId(), newClassPKs,
				segmentsEntry.getSegmentsEntryId());

			Set<Long> classPKs = classPKsMap.computeIfAbsent(
				segmentsEntry.getCompanyId(), companyId -> new HashSet<>());

			classPKs.addAll(indexableClassPKs);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Adding ", indexableClassPKs.size(),
						" class PKs of segment entry ",
						segmentsEntry.getSegmentsEntryId(), " to be indexed"));
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to reindex segments entry " +
					segmentsEntry.getSegmentsEntryId(),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsEntryRelIndexerSchedulerJobConfiguration.class);

	@Reference(
		target = "(indexer.class.name=com.liferay.portal.kernel.model.User)"
	)
	private BulkReindexer _bulkReindexer;

	@Reference(
		target = "(indexer.class.name=com.liferay.portal.kernel.model.User)"
	)
	private Indexer<User> _indexer;

	private IndexerHelper _indexerHelper;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsEntryProviderRegistry _segmentsEntryProviderRegistry;

	@Reference
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	private TriggerConfiguration _triggerConfiguration;

}