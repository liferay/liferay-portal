/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.scheduler;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.seo.studio.web.internal.scan.SEOStudioScanCreator;
import com.liferay.seo.studio.web.internal.util.SEOStudioScanScheduleUtil;

import java.io.Serializable;

import java.text.Format;

import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jonathan McCann
 */
@Component(service = SchedulerJobConfiguration.class)
public class CheckSEOStudioDomainsSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeConsumer<Long, Exception>
		getCompanyJobExecutorUnsafeConsumer() {

		return companyId -> _checkSEOStudioDomains(companyId);
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _checkSEOStudioDomains(companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			5, TimeUnit.MINUTE);
	}

	private void _checkSEOStudioDomains(long companyId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-44511")) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_SEO_STUDIO_DOMAIN", companyId);

		if (objectDefinition == null) {
			return;
		}

		Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZoneUtil.getTimeZone("UTC"));

		List<Long> primaryKeys = _objectEntryLocalService.getPrimaryKeys(
			new Long[] {0L}, companyId, 0,
			objectDefinition.getObjectDefinitionId(),
			_filterFactory.create(
				StringBundler.concat(
					"(autoScanEnabled eq true) and (nextScanDate le ",
					format.format(new Date()), ")"),
				objectDefinition),
			false, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (long primaryKey : primaryKeys) {
			try {
				_createSEOStudioScans(primaryKey);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to create scans for SEO Studio domain " +
						primaryKey,
					exception);
			}
		}
	}

	private void _createSEOStudioScans(long seoStudioDomainId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			seoStudioDomainId);

		long userId = objectEntry.getUserId();

		Map<String, Serializable> values = objectEntry.getValues();

		_seoStudioScanCreator.createScans(
			(Date)values.get("nextScanDate"), seoStudioDomainId, "scheduled",
			userId);

		Date nextScanDate = SEOStudioScanScheduleUtil.getNextScanDate(
			Instant.now(), GetterUtil.getInteger(values.get("scanDayOfMonth")),
			GetterUtil.getString(values.get("scanDayOfWeek")),
			GetterUtil.getString(values.get("scanFrequency")),
			GetterUtil.getString(values.get("scanTime")),
			GetterUtil.getString(values.get("scanTimeZone")));

		if ((nextScanDate == null) && _log.isWarnEnabled()) {
			_log.warn(
				"Unable to compute the next scan date for SEO Studio domain " +
					seoStudioDomainId);
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(objectEntry.getCompanyId());
		serviceContext.setUserId(userId);

		_objectEntryLocalService.partialUpdateObjectEntry(
			userId, seoStudioDomainId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"nextScanDate", nextScanDate
			).build(),
			serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CheckSEOStudioDomainsSchedulerJobConfiguration.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private SEOStudioScanCreator _seoStudioScanCreator;

}