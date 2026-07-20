/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.seo.studio.web.internal.test.BaseTestCase;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@FeatureFlag("LPD-44511")
@RunWith(Arquillian.class)
public class CheckSEOStudioDomainsSchedulerJobConfigurationTest
	extends BaseTestCase {

	@Test
	public void testCheckSEOStudioDomains() throws Exception {
		seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(true);

		Date nextScanDate = new Date(System.currentTimeMillis() - Time.MINUTE);

		_updateSEOStudioDomainNextScanDate(
			nextScanDate, seoStudioDomainObjectEntry);

		_checkSEOStudioDomains();

		List<ObjectEntry> seoStudioScanRunObjectEntries =
			_getSEOStudioScanRunObjectEntries(seoStudioDomainObjectEntry);

		Assert.assertEquals(
			seoStudioScanRunObjectEntries.toString(), 1,
			seoStudioScanRunObjectEntries.size());

		ObjectEntry seoStudioScanRunObjectEntry =
			seoStudioScanRunObjectEntries.get(0);

		Map<String, Serializable> scanRunValues =
			objectEntryLocalService.getValues(
				seoStudioScanRunObjectEntry.getObjectEntryId());

		Assert.assertTrue(
			Validator.isNotNull(
				MapUtil.getString(scanRunValues, "scheduledScanKey")));
		Assert.assertEquals(
			"running", MapUtil.getString(scanRunValues, "state"));
		Assert.assertEquals(
			"scheduled", MapUtil.getString(scanRunValues, "triggeredBy"));

		List<ObjectEntry> seoStudioScanObjectEntries =
			getSEOStudioScanObjectEntries(seoStudioScanRunObjectEntry);

		Assert.assertEquals(
			seoStudioScanObjectEntries.toString(), 2,
			seoStudioScanObjectEntries.size());

		for (ObjectEntry seoStudioScanObjectEntry :
				seoStudioScanObjectEntries) {

			Assert.assertEquals(
				"queued",
				MapUtil.getString(
					objectEntryLocalService.getValues(
						seoStudioScanObjectEntry.getObjectEntryId()),
					"state"));
		}

		Map<String, Serializable> domainValues =
			objectEntryLocalService.getValues(
				seoStudioDomainObjectEntry.getObjectEntryId());

		Date updatedNextScanDate = (Date)domainValues.get("nextScanDate");

		Assert.assertTrue(updatedNextScanDate.after(new Date()));

		_updateSEOStudioDomainNextScanDate(
			nextScanDate, seoStudioDomainObjectEntry);

		_checkSEOStudioDomains();

		seoStudioScanRunObjectEntries = _getSEOStudioScanRunObjectEntries(
			seoStudioDomainObjectEntry);

		Assert.assertEquals(
			seoStudioScanRunObjectEntries.toString(), 1,
			seoStudioScanRunObjectEntries.size());

		seoStudioScanObjectEntries = getSEOStudioScanObjectEntries(
			seoStudioScanRunObjectEntry);

		Assert.assertEquals(
			seoStudioScanObjectEntries.toString(), 2,
			seoStudioScanObjectEntries.size());

		_updateSEOStudioScanState(seoStudioScanObjectEntries.get(0), "running");
		_updateSEOStudioScanState(
			seoStudioScanObjectEntries.get(0), "completed");

		_checkSEOStudioDomains();

		Assert.assertEquals(
			"running",
			MapUtil.getString(
				objectEntryLocalService.getValues(
					seoStudioScanRunObjectEntry.getObjectEntryId()),
				"state"));

		_updateSEOStudioScanState(seoStudioScanObjectEntries.get(1), "running");
		_updateSEOStudioScanState(
			seoStudioScanObjectEntries.get(1), "completed");

		_checkSEOStudioDomains();

		Assert.assertEquals(
			"completed",
			MapUtil.getString(
				objectEntryLocalService.getValues(
					seoStudioScanRunObjectEntry.getObjectEntryId()),
				"state"));

		_testCheckSEOStudioDomainsDoesNotCreateScanRun(
			false, new Date(System.currentTimeMillis() - Time.MINUTE));
		_testCheckSEOStudioDomainsDoesNotCreateScanRun(
			true, new Date(System.currentTimeMillis() + Time.MINUTE));
	}

	private ObjectEntry _addSEOStudioDomainObjectEntry(boolean autoScanEnabled)
		throws Exception {

		return addSEOStudioDomainObjectEntry(
			autoScanEnabled, RandomTestUtil.randomString(),
			JSONUtil.put(
				"engines",
				JSONUtil.put(
					"crawler", JSONUtil.put("enabled", true)
				).put(
					"pageSpeed", JSONUtil.put("enabled", true)
				)
			).toString());
	}

	private void _checkSEOStudioDomains() throws Exception {
		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		jobExecutorUnsafeRunnable.run();
	}

	private List<ObjectEntry> _getSEOStudioScanRunObjectEntries(
			ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		return getRelatedObjectEntries(
			seoStudioDomainObjectEntry, "seoStudioDomainToSEOStudioScanRuns");
	}

	private void _testCheckSEOStudioDomainsDoesNotCreateScanRun(
			boolean autoScanEnabled, Date nextScanDate)
		throws Exception {

		seoStudioDomainObjectEntry = _addSEOStudioDomainObjectEntry(
			autoScanEnabled);

		_updateSEOStudioDomainNextScanDate(
			nextScanDate, seoStudioDomainObjectEntry);

		_checkSEOStudioDomains();

		Assert.assertNull(
			fetchSEOStudioScanRunObjectEntry(seoStudioDomainObjectEntry));
	}

	private void _updateSEOStudioDomainNextScanDate(
			Date nextScanDate, ObjectEntry seoStudioDomainObjectEntry)
		throws Exception {

		partialUpdateObjectEntry(
			seoStudioDomainObjectEntry,
			HashMapBuilder.<String, Serializable>put(
				"nextScanDate", nextScanDate
			).build());
	}

	private void _updateSEOStudioScanState(
			ObjectEntry seoStudioScanObjectEntry, String state)
		throws Exception {

		partialUpdateObjectEntry(
			seoStudioScanObjectEntry,
			HashMapBuilder.<String, Serializable>put(
				"state", state
			).build());
	}

	@Inject(
		filter = "component.name=com.liferay.seo.studio.web.internal.scheduler.CheckSEOStudioDomainsSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

}