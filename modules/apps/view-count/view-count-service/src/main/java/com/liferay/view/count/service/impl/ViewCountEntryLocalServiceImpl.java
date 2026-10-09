/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.service.impl;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.NumberIncrement;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.mass.delete.MassDeleteCacheThreadLocal;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.SQLStateAcceptor;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.view.count.configuration.ViewCountConfiguration;
import com.liferay.view.count.model.ViewCountEntry;
import com.liferay.view.count.model.ViewCountEntryTable;
import com.liferay.view.count.model.listener.ViewCountEntryModelListener;
import com.liferay.view.count.service.ViewCountEntryLocalService;
import com.liferay.view.count.service.base.ViewCountEntryLocalServiceBaseImpl;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	configurationPid = "com.liferay.view.count.configuration.ViewCountConfiguration",
	property = "model.class.name=com.liferay.view.count.model.ViewCountEntry",
	service = AopService.class
)
@CTAware
public class ViewCountEntryLocalServiceImpl
	extends ViewCountEntryLocalServiceBaseImpl implements ViewCountManager {

	@Override
	public void deleteViewCount(
		long companyId, long classNameId, long classPK) {

		Map<Long, List<ViewCountEntry>> partitionViewCountEntries =
			MassDeleteCacheThreadLocal.getMassDeleteCache(
				StringBundler.concat(
					ViewCountEntryLocalServiceImpl.class.getName(),
					".deleteViewCount#", companyId, classNameId),
				() -> MapUtil.toPartitionMap(
					viewCountEntryPersistence.findByC_CN(
						companyId, classNameId),
					ViewCountEntry::getClassPK));

		if (partitionViewCountEntries == null) {
			ViewCountEntryPK viewCountEntryPK = new ViewCountEntryPK(
				companyId, classNameId, classPK);

			ViewCountEntry viewCountEntry =
				viewCountEntryPersistence.fetchByPrimaryKey(viewCountEntryPK);

			if (viewCountEntry != null) {
				viewCountEntryPersistence.remove(viewCountEntry);
			}

			return;
		}

		List<ViewCountEntry> viewCountEntries =
			partitionViewCountEntries.remove(classPK);

		ListUtil.isNotEmptyForEach(
			viewCountEntries,
			viewCountEntry -> viewCountEntryPersistence.remove(viewCountEntry));
	}

	@Override
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {
			IdentifiableOSGiService.class, PersistedModelLocalService.class,
			ViewCountEntryLocalService.class, ViewCountManager.class
		};
	}

	@Override
	public long getViewCount(long companyId, long classNameId, long classPK) {
		if (isViewCountEnabled(classNameId)) {
			return _getViewCount(companyId, classNameId, classPK);
		}

		return 0;
	}

	@Override
	public Table<?> getViewCountEntryTable() {
		return ViewCountEntryTable.INSTANCE;
	}

	@BufferedIncrement(incrementClass = NumberIncrement.class)
	@Override
	@Retry(
		acceptor = SQLStateAcceptor.class,
		properties = {
			@Property(
				name = SQLStateAcceptor.SQLSTATE,
				value = SQLStateAcceptor.SQLSTATE_INTEGRITY_CONSTRAINT_VIOLATION + "," + SQLStateAcceptor.SQLSTATE_TRANSACTION_ROLLBACK
			)
		}
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void incrementViewCount(
		long companyId, long classNameId, long classPK, int increment) {

		if (!isViewCountEnabled(classNameId)) {
			return;
		}

		viewCountEntryFinder.incrementViewCount(
			companyId, classNameId, classPK, increment);

		ClassName className = _classNameLocalService.fetchClassName(
			classNameId);

		if (className == null) {
			return;
		}

		ViewCountEntryModelListener viewCountIncrementListener =
			_serviceTrackerMap.getService(className.getValue());

		if (viewCountIncrementListener == null) {
			return;
		}

		try {
			viewCountIncrementListener.onAfterIncrement(
				fetchViewCountEntry(
					new ViewCountEntryPK(companyId, classNameId, classPK)));
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	@Transactional(enabled = false)
	public boolean isViewCountEnabled() {
		return _enabled;
	}

	@Override
	@Transactional(enabled = false)
	public boolean isViewCountEnabled(long classNameId) {
		if (!isViewCountEnabled()) {
			return false;
		}

		ClassName className = _classNameLocalService.fetchByClassNameId(
			classNameId);

		return !_disabledClassNames.contains(className.getValue());
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ViewCountEntryModelListener.class, null,
			ServiceReferenceMapperFactory.createFromFunction(
				bundleContext, ViewCountEntryModelListener::getModelClassName));
	}

	@Deactivate
	@Override
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		ViewCountConfiguration viewCountConfiguration =
			ConfigurableUtil.createConfigurable(
				ViewCountConfiguration.class, properties);

		Set<String> disabledClassNames = new HashSet<>();

		for (String className : viewCountConfiguration.disabledClassNames()) {
			if (Validator.isNull(className)) {
				continue;
			}

			disabledClassNames.add(className);
		}

		_disabledClassNames = disabledClassNames;

		_enabled = viewCountConfiguration.enabled();
	}

	private long _getViewCount(long companyId, long classNameId, long classPK) {
		Map<Long, Long> indexedViewCounts =
			ReindexCacheThreadLocal.getScopeReindexCache(
				ViewCountEntryLocalServiceImpl.class.getName(),
				String.valueOf(classNameId),
				() -> viewCountEntryPersistence.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						ViewCountEntryTable.INSTANCE
					).where(
						ViewCountEntryTable.INSTANCE.companyId.eq(companyId)
					),
					false),
				() -> viewCountEntryPersistence.dslQueryCount(
					DSLQueryFactoryUtil.count(
					).from(
						ViewCountEntryTable.INSTANCE
					).where(
						ViewCountEntryTable.INSTANCE.companyId.eq(
							companyId
						).and(
							ViewCountEntryTable.INSTANCE.classNameId.eq(
								classNameId)
						)
					),
					false),
				count -> {
					Map<Long, Long> localIndexedViewCounts = new HashMap<>();

					if (count == 0) {
						return localIndexedViewCounts;
					}

					DSLQuery dslQuery = DSLQueryFactoryUtil.select(
						ViewCountEntryTable.INSTANCE.classPK,
						ViewCountEntryTable.INSTANCE.viewCount
					).from(
						ViewCountEntryTable.INSTANCE
					).where(
						ViewCountEntryTable.INSTANCE.companyId.eq(
							companyId
						).and(
							ViewCountEntryTable.INSTANCE.classNameId.eq(
								classNameId)
						)
					);

					for (Object[] values :
							(List<Object[]>)viewCountEntryPersistence.dslQuery(
								dslQuery, false)) {

						localIndexedViewCounts.put(
							(Long)values[0], (Long)values[1]);
					}

					return localIndexedViewCounts;
				});

		if (indexedViewCounts == null) {
			ViewCountEntry viewCountEntry =
				viewCountEntryPersistence.fetchByPrimaryKey(
					new ViewCountEntryPK(companyId, classNameId, classPK));

			if (viewCountEntry == null) {
				return 0;
			}

			return viewCountEntry.getViewCount();
		}

		Long viewCount = indexedViewCounts.get(classPK);

		if (viewCount == null) {
			return 0;
		}

		return viewCount;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewCountEntryLocalServiceImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	private volatile Set<String> _disabledClassNames;
	private volatile boolean _enabled;
	private ServiceTrackerMap<String, ViewCountEntryModelListener>
		_serviceTrackerMap;

}