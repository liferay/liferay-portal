/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.scheduler;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.asah.connector.cache.AsahSegmentsEntryCache;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClient;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClientImpl;
import com.liferay.segments.asah.connector.internal.client.model.Individual;
import com.liferay.segments.asah.connector.internal.client.model.IndividualSegment;
import com.liferay.segments.asah.connector.internal.client.model.Results;
import com.liferay.segments.asah.connector.internal.client.util.OrderByField;
import com.liferay.segments.asah.connector.internal.configuration.SegmentsAsahConfiguration;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(
	configurationPid = "com.liferay.segments.asah.connector.internal.configuration.SegmentsAsahConfiguration",
	service = SchedulerJobConfiguration.class
)
public class CheckIndividualSegmentsSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return this::_checkIndividualSegments;
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_asahFaroBackendClient = new AsahFaroBackendClientImpl(
			_analyticsSettingsManager, _http);

		modified(properties);
	}

	@Deactivate
	protected void deactivate() {
		_asahFaroBackendClient = null;
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		SegmentsAsahConfiguration segmentsAsahConfiguration =
			ConfigurableUtil.createConfigurable(
				SegmentsAsahConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			segmentsAsahConfiguration.checkInterval(), TimeUnit.MINUTE);
	}

	private void _addOrUpdateSegmentsEntry(
		long companyId, IndividualSegment individualSegment) {

		try {
			ServiceContext serviceContext = _getServiceContext(companyId);

			SegmentsEntry segmentsEntry =
				_segmentsEntryLocalService.fetchSegmentsEntry(
					serviceContext.getScopeGroupId(),
					individualSegment.getId());

			Locale locale = _portal.getSiteDefaultLocale(
				serviceContext.getScopeGroupId());

			Map<Locale, String> nameMap = Collections.singletonMap(
				locale, individualSegment.getName());

			if (segmentsEntry == null) {
				_segmentsEntryLocalService.addSegmentsEntry(
					individualSegment.getExternalReferenceCode(),
					individualSegment.getId(), nameMap, Collections.emptyMap(),
					true, null, SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
					_getType(individualSegment), serviceContext);

				return;
			}

			if (segmentsEntry.isActive() &&
				(Validator.isNull(
					individualSegment.getExternalReferenceCode()) ||
				 StringUtil.equals(
					 segmentsEntry.getExternalReferenceCode(),
					 individualSegment.getExternalReferenceCode())) &&
				StringUtil.equals(
					segmentsEntry.getName(locale),
					individualSegment.getName()) &&
				(segmentsEntry.getType() == _getType(individualSegment))) {

				return;
			}

			_segmentsEntryLocalService.updateSegmentsEntry(
				individualSegment.getExternalReferenceCode(),
				segmentsEntry.getSegmentsEntryId(), individualSegment.getId(),
				nameMap, Collections.emptyMap(), true, null,
				_getType(individualSegment), serviceContext);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to process individual segment " +
					individualSegment.getId(),
				portalException);
		}
	}

	private void _checkIndividualSegmentMemberships(SegmentsEntry segmentsEntry)
		throws Exception {

		Set<Long> userIds = null;

		try {
			userIds = _getUserIds(segmentsEntry);
		}
		catch (RuntimeException runtimeException) {
			_log.error(
				"Unable to retrieve individuals for individual segment " +
					segmentsEntry.getSegmentsEntryKey(),
				runtimeException);

			return;
		}

		_updateSegmentsEntryRels(segmentsEntry, userIds);
	}

	private void _checkIndividualSegments() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				if (_analyticsSettingsManager.isAnalyticsEnabled(companyId)) {
					_checkIndividualSegments(companyId);
					_checkIndividualSegmentsMemberships();
				}
			});
	}

	private void _checkIndividualSegments(long companyId) {
		Results<IndividualSegment> individualSegmentResults = new Results<>();

		try {
			individualSegmentResults =
				_asahFaroBackendClient.getIndividualSegmentResults(
					companyId, 1, _DELTA,
					Collections.singletonList(
						OrderByField.desc("dateModified")));

			_deleteSegmentsEntries(individualSegmentResults);
		}
		catch (RuntimeException runtimeException) {
			_log.error(
				"Unable to retrieve individual segments", runtimeException);

			return;
		}
		catch (PortalException portalException) {
			_log.error("Unable to delete segments", portalException);

			return;
		}

		int totalElements = individualSegmentResults.getTotal();

		if (_log.isDebugEnabled()) {
			_log.debug(totalElements + " active individual segments found");
		}

		if (totalElements == 0) {
			return;
		}

		List<IndividualSegment> individualSegments =
			individualSegmentResults.getItems();

		individualSegments.forEach(
			individualSegment -> _addOrUpdateSegmentsEntry(
				companyId, individualSegment));
	}

	private void _checkIndividualSegmentsMemberships() throws Exception {
		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntriesBySource(
				SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
				new int[] {
					SegmentsEntryConstants.TYPE_BATCH,
					SegmentsEntryConstants.TYPE_REAL_TIME
				},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (SegmentsEntry segmentsEntry : segmentsEntries) {
			_checkIndividualSegmentMemberships(segmentsEntry);
		}
	}

	private void _deleteSegmentsEntries(
			Results<IndividualSegment> individualSegmentResults)
		throws PortalException {

		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntriesBySource(
				SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
				new int[] {
					SegmentsEntryConstants.TYPE_BATCH,
					SegmentsEntryConstants.TYPE_REAL_TIME
				},
				0, _DELTA, null);

		if (individualSegmentResults.getTotal() > 0) {
			segmentsEntries = ListUtil.filter(
				segmentsEntries,
				segmentsEntry -> !ListUtil.exists(
					individualSegmentResults.getItems(),
					individualSegment -> StringUtil.equals(
						individualSegment.getId(),
						segmentsEntry.getSegmentsEntryKey())));
		}

		for (SegmentsEntry segmentsEntry : segmentsEntries) {
			_segmentsEntryLocalService.deleteSegmentsEntry(segmentsEntry);
		}
	}

	private String _getIndividualPK(
		String dataSourceId, Individual individual) {

		for (Individual.DataSourceIndividualPK dataSourceIndividualPK :
				individual.getDataSourceIndividualPKs()) {

			if (!Objects.equals(
					dataSourceId, dataSourceIndividualPK.getDataSourceId())) {

				continue;
			}

			for (String individualPK :
					dataSourceIndividualPK.getIndividualPKs()) {

				return individualPK;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a user corresponding to individual " +
					individual.getId());
		}

		return null;
	}

	private ServiceContext _getServiceContext(long companyId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		Company company = _companyLocalService.getCompany(companyId);

		serviceContext.setScopeGroupId(company.getGroupId());

		User user = company.getGuestUser();

		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	private int _getType(IndividualSegment individualSegment) {
		if (Objects.equals(
				individualSegment.getSegmentType(),
				IndividualSegment.Type.REAL_TIME.name())) {

			return SegmentsEntryConstants.TYPE_REAL_TIME;
		}

		return SegmentsEntryConstants.TYPE_BATCH;
	}

	private Long _getUserId(
		long companyId, String dataSourceId, Individual individual) {

		for (Individual.DataSourceIndividualPK dataSourceIndividualPK :
				individual.getDataSourceIndividualPKs()) {

			if (Objects.equals(
					dataSourceId, dataSourceIndividualPK.getDataSourceId())) {

				for (String individualUuid :
						dataSourceIndividualPK.getIndividualPKs()) {

					User user = _userLocalService.fetchUserByUuidAndCompanyId(
						individualUuid, companyId);

					if (user != null) {
						return user.getUserId();
					}
				}

				break;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				"Unable to find a user corresponding to individual " +
					individual.getId());
		}

		return null;
	}

	private Set<Long> _getUserIds(SegmentsEntry segmentsEntry)
		throws Exception {

		Results<Individual> individualResults =
			_asahFaroBackendClient.getIndividualResults(
				segmentsEntry.getCompanyId(),
				segmentsEntry.getSegmentsEntryKey(), 1, _DELTA,
				Collections.singletonList(OrderByField.desc("dateModified")));

		int totalElements = individualResults.getTotal();

		if (_log.isDebugEnabled()) {
			_log.debug(
				totalElements + " individuals found for individual segment " +
					segmentsEntry.getSegmentsEntryKey());
		}

		Set<Long> userIds = new HashSet<>();

		if (totalElements == 0) {
			return userIds;
		}

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				segmentsEntry.getCompanyId());

		int totalPages = (int)Math.ceil((double)totalElements / _DELTA);

		int curPage = 1;

		while (true) {
			List<Individual> individuals = individualResults.getItems();

			individuals.forEach(
				individual -> {
					Long userId = _getUserId(
						segmentsEntry.getCompanyId(),
						analyticsConfiguration.liferayAnalyticsDataSourceId(),
						individual);

					if (userId != null) {
						userIds.add(userId);

						return;
					}

					List<String> individualSegmentIds =
						individual.getIndividualSegmentIds();

					if (ListUtil.isEmpty(individualSegmentIds)) {
						return;
					}

					String individualPK = _getIndividualPK(
						analyticsConfiguration.liferayAnalyticsDataSourceId(),
						individual);

					if (individualPK == null) {
						return;
					}

					try {
						_putSegmentsEntryIdsCache(
							segmentsEntry.getCompanyId(), individualPK,
							individualSegmentIds);
					}
					catch (PortalException portalException) {
						_log.error(
							"Unable to cache segments entry IDs for " +
								"individual " + individual.getId(),
							portalException);
					}
				});

			curPage++;

			if (curPage > totalPages) {
				break;
			}

			individualResults = _asahFaroBackendClient.getIndividualResults(
				segmentsEntry.getCompanyId(),
				segmentsEntry.getSegmentsEntryKey(), curPage, _DELTA,
				Collections.singletonList(OrderByField.desc("dateModified")));
		}

		return userIds;
	}

	private void _putSegmentsEntryIdsCache(
			long companyId, String userId, List<String> individualSegmentIds)
		throws PortalException {

		ServiceContext serviceContext = _getServiceContext(companyId);

		List<Long> segmentsEntryIds = TransformUtil.transform(
			individualSegmentIds,
			individualSegmentId -> {
				SegmentsEntry curSegmentsEntry =
					_segmentsEntryLocalService.fetchSegmentsEntry(
						serviceContext.getScopeGroupId(), individualSegmentId);

				if (curSegmentsEntry != null) {
					return curSegmentsEntry.getSegmentsEntryId();
				}

				return null;
			});

		_asahSegmentsEntryCache.putSegmentsEntryIds(
			userId, ArrayUtil.toLongArray(segmentsEntryIds));
	}

	private void _updateSegmentsEntryRels(
		SegmentsEntry segmentsEntry, Set<Long> userIds) {

		try {
			Set<Long> addUserIds = new HashSet<>(userIds);
			Set<Long> deleteUserIds = new HashSet<>();

			long segmentsEntryId = segmentsEntry.getSegmentsEntryId();

			Set<Long> oldUserIds = SetUtil.fromList(
				TransformUtil.transform(
					_segmentsEntryRelLocalService.getSegmentsEntryRels(
						segmentsEntryId),
					SegmentsEntryRel::getClassPK));

			for (Long oldUserId : oldUserIds) {
				if (addUserIds.remove(oldUserId)) {
					continue;
				}

				deleteUserIds.add(oldUserId);
			}

			if (!deleteUserIds.isEmpty()) {
				_segmentsEntryRelLocalService.deleteSegmentsEntryRels(
					segmentsEntryId, _portal.getClassNameId(User.class),
					ArrayUtil.toLongArray(deleteUserIds));
			}

			if (!addUserIds.isEmpty()) {
				_segmentsEntryLocalService.addSegmentsEntryClassPKs(
					segmentsEntryId, ArrayUtil.toLongArray(addUserIds),
					_getServiceContext(segmentsEntry.getCompanyId()));
			}
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to process individuals " + userIds, portalException);
		}
	}

	private static final int _DELTA = 100;

	private static final Log _log = LogFactoryUtil.getLog(
		CheckIndividualSegmentsSchedulerJobConfiguration.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	private volatile AsahFaroBackendClient _asahFaroBackendClient;

	@Reference
	private AsahSegmentsEntryCache _asahSegmentsEntryCache;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Http _http;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	private volatile TriggerConfiguration _triggerConfiguration;

	@Reference
	private UserLocalService _userLocalService;

}