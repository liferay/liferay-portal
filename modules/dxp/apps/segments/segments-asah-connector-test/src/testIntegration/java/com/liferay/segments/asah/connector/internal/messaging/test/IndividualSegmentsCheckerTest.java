/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.messaging.test;

import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.context.Context;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.provider.SegmentsEntryProvider;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class IndividualSegmentsCheckerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = _companyLocalService.fetchCompany(
			TestPropsValues.getCompanyId());
	}

	@Before
	public void setUp() throws Exception {
		_user = TestPropsValues.getUser();
	}

	@After
	public void tearDown() throws Exception {
		_segmentsEntryLocalService.deleteSegmentsEntries(_company.getGroupId());
	}

	@Test
	public void testCheckIndividualSegmentsEntries() throws Exception {
		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), "123456789", true,
						"http://localhost:" +
							PortalUtil.getPortalServerPort(false));
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.segments.asah.connector.internal." +
						"configuration.SegmentsAsahConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"anonymousUserSegmentsCacheExpirationTime", "60"
					).build())) {

			String guestUserUuid = StringUtil.randomString();

			_prepareAsahFaroBackendClient(_user.getUuid(), guestUserUuid);

			UnsafeRunnable<Exception> unsafeRunnable =
				_checkIndividualSegmentsSchedulerJobConfiguration.
					getJobExecutorUnsafeRunnable();

			unsafeRunnable.run();

			SegmentsEntry segmentsEntry = _getSegmentsEntry();

			Context context = new Context();

			context.put("segmentsAnonymousUserId", guestUserUuid);

			Assert.assertArrayEquals(
				new long[] {segmentsEntry.getSegmentsEntryId()},
				_segmentsEntryProvider.getSegmentsEntryIds(
					_company.getGroupId(), User.class.getName(),
					_user.getUserId(), context));

			Assert.assertEquals(StringPool.BLANK, segmentsEntry.getCriteria());
			Assert.assertEquals(
				"Test Segment 1",
				segmentsEntry.getName(LocaleUtil.getSiteDefault()));

			List<SegmentsEntryRel> segmentsEntryRels =
				_segmentsEntryRelLocalService.getSegmentsEntryRels(
					segmentsEntry.getSegmentsEntryId());

			Assert.assertEquals(
				segmentsEntryRels.toString(), 1, segmentsEntryRels.size());

			SegmentsEntryRel segmentsEntryRel = segmentsEntryRels.get(0);

			Assert.assertEquals(
				_user.getUserId(), segmentsEntryRel.getClassPK());
		}
	}

	@Test
	public void testCheckIndividualSegmentsEntriesWhenAnalyticsCloudFails()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), "123456789", true,
						"http://localhost:" +
							PortalUtil.getPortalServerPort(false));
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.segments.asah.connector.internal." +
						"configuration.SegmentsAsahConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"anonymousUserSegmentsCacheExpirationTime", "60"
					).build())) {

			User user = UserTestUtil.addUser();

			_prepareAsahFaroBackendClient(user.getUuid());

			UnsafeRunnable<Exception> unsafeRunnable =
				_checkIndividualSegmentsSchedulerJobConfiguration.
					getJobExecutorUnsafeRunnable();

			unsafeRunnable.run();

			long segmentsEntryRelId = _getSegmentsEntryRelId(
				_getSegmentsEntry(), user);

			_prepareAsahFaroBackendClient(
				() -> {
					throw new IllegalStateException();
				});

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.segments.asah.connector.internal.scheduler." +
						"CheckIndividualSegmentsSchedulerJobConfiguration",
					LoggerTestUtil.ERROR)) {

				unsafeRunnable.run();

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				SegmentsEntry segmentsEntry = _getSegmentsEntry();

				Assert.assertEquals(
					"Unable to retrieve individuals for individual segment " +
						segmentsEntry.getSegmentsEntryKey(),
					logEntry.getMessage());
			}

			Assert.assertEquals(
				segmentsEntryRelId,
				_getSegmentsEntryRelId(_getSegmentsEntry(), user));
		}
	}

	@Test
	public void testCheckIndividualSegmentsEntriesWhenMembershipChanges()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), "123456789", true,
						"http://localhost:" +
							PortalUtil.getPortalServerPort(false));
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.segments.asah.connector.internal." +
						"configuration.SegmentsAsahConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"anonymousUserSegmentsCacheExpirationTime", "60"
					).build())) {

			User user1 = UserTestUtil.addUser();
			User user2 = UserTestUtil.addUser();

			_prepareAsahFaroBackendClient(user1.getUuid(), user2.getUuid());

			UnsafeRunnable<Exception> unsafeRunnable =
				_checkIndividualSegmentsSchedulerJobConfiguration.
					getJobExecutorUnsafeRunnable();

			unsafeRunnable.run();

			SegmentsEntry segmentsEntry = _getSegmentsEntry();

			Assert.assertNotNull(
				_segmentsEntryRelLocalService.fetchSegmentsEntryRel(
					segmentsEntry.getSegmentsEntryId(),
					_portal.getClassNameId(User.class), user1.getUserId()));

			long segmentsEntryRelId = _getSegmentsEntryRelId(
				segmentsEntry, user2);

			User user3 = UserTestUtil.addUser();

			_prepareAsahFaroBackendClient(user2.getUuid(), user3.getUuid());

			unsafeRunnable.run();

			segmentsEntry = _getSegmentsEntry();

			Assert.assertEquals(
				segmentsEntryRelId,
				_getSegmentsEntryRelId(segmentsEntry, user2));
			Assert.assertNotNull(
				_segmentsEntryRelLocalService.fetchSegmentsEntryRel(
					segmentsEntry.getSegmentsEntryId(),
					_portal.getClassNameId(User.class), user3.getUserId()));
			Assert.assertNull(
				_segmentsEntryRelLocalService.fetchSegmentsEntryRel(
					segmentsEntry.getSegmentsEntryId(),
					_portal.getClassNameId(User.class), user1.getUserId()));
		}
	}

	@Test
	public void testCheckIndividualSegmentsEntriesWhenNotSyncedWithAnalyticsCloud()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), StringPool.BLANK);
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.segments.asah.connector.internal." +
						"configuration.SegmentsAsahConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"anonymousUserSegmentsCacheExpirationTime", "60"
					).build())) {

			UnsafeRunnable<Exception> unsafeRunnable =
				_checkIndividualSegmentsSchedulerJobConfiguration.
					getJobExecutorUnsafeRunnable();

			unsafeRunnable.run();

			List<SegmentsEntry> segmentsEntries =
				_segmentsEntryLocalService.getSegmentsEntriesBySource(
					SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
					new int[] {
						SegmentsEntryConstants.TYPE_BATCH,
						SegmentsEntryConstants.TYPE_DEFAULT
					},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				segmentsEntries.toString(), 0, segmentsEntries.size());
		}
	}

	@Test
	public void testCheckIndividualSegmentsEntriesWhenUnchanged()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), "123456789", true,
						"http://localhost:" +
							PortalUtil.getPortalServerPort(false));
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.segments.asah.connector.internal." +
						"configuration.SegmentsAsahConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"anonymousUserSegmentsCacheExpirationTime", "60"
					).build())) {

			_prepareAsahFaroBackendClient(
				_user.getUuid(), StringUtil.randomString());

			UnsafeRunnable<Exception> unsafeRunnable =
				_checkIndividualSegmentsSchedulerJobConfiguration.
					getJobExecutorUnsafeRunnable();

			unsafeRunnable.run();

			SegmentsEntry segmentsEntry = _getSegmentsEntry();

			long mvccVersion = segmentsEntry.getMvccVersion();

			long[] segmentsEntryRelIds = ListUtil.toLongArray(
				_segmentsEntryRelLocalService.getSegmentsEntryRels(
					segmentsEntry.getSegmentsEntryId()),
				SegmentsEntryRel::getSegmentsEntryRelId);

			Assert.assertEquals(
				Arrays.toString(segmentsEntryRelIds), 1,
				segmentsEntryRelIds.length);

			unsafeRunnable.run();

			segmentsEntry = _getSegmentsEntry();

			Assert.assertEquals(mvccVersion, segmentsEntry.getMvccVersion());
			Assert.assertArrayEquals(
				segmentsEntryRelIds,
				ListUtil.toLongArray(
					_segmentsEntryRelLocalService.getSegmentsEntryRels(
						segmentsEntry.getSegmentsEntryId()),
					SegmentsEntryRel::getSegmentsEntryRelId));
		}
	}

	@Test
	public void testIndividualSegmentsDeleteSegmentsEntries() throws Exception {
		SegmentsTestUtil.addSegmentsEntry(
			"1234567", "Test Segment 1", null, null,
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			ServiceContextTestUtil.getServiceContext(_company.getGroupId()));
		SegmentsTestUtil.addSegmentsEntry(
			"2345678", "Test Segment 2", null, null,
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			ServiceContextTestUtil.getServiceContext(_company.getGroupId()));

		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntriesBySource(
				SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
				new int[] {
					SegmentsEntryConstants.TYPE_BATCH,
					SegmentsEntryConstants.TYPE_DEFAULT
				},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			segmentsEntries.toString(), 2, segmentsEntries.size());

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(), "123456789", true,
						"http://localhost:" +
							PortalUtil.getPortalServerPort(false));
			ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.segments.asah.connector.internal." +
						"configuration.SegmentsAsahConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"anonymousUserSegmentsCacheExpirationTime", "60"
					).build())) {

			_prepareAsahFaroBackendClient(
				_user.getUuid(), StringUtil.randomString());

			UnsafeRunnable<Exception> unsafeRunnable =
				_checkIndividualSegmentsSchedulerJobConfiguration.
					getJobExecutorUnsafeRunnable();

			unsafeRunnable.run();

			SegmentsEntry segmentsEntry = _getSegmentsEntry();

			Assert.assertEquals(
				"Test Segment 1",
				segmentsEntry.getName(LocaleUtil.getSiteDefault()));
		}
	}

	private SegmentsEntry _getSegmentsEntry() {
		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntriesBySource(
				SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
				new int[] {
					SegmentsEntryConstants.TYPE_BATCH,
					SegmentsEntryConstants.TYPE_DEFAULT
				},
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());

		return segmentsEntries.get(0);
	}

	private long _getSegmentsEntryRelId(
		SegmentsEntry segmentsEntry, User user) {

		SegmentsEntryRel segmentsEntryRel =
			_segmentsEntryRelLocalService.fetchSegmentsEntryRel(
				segmentsEntry.getSegmentsEntryId(),
				_portal.getClassNameId(User.class), user.getUserId());

		return segmentsEntryRel.getSegmentsEntryRelId();
	}

	private void _prepareAsahFaroBackendClient(String... individualPKs) {
		_prepareAsahFaroBackendClient(
			() -> {
				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				for (String individualPK : individualPKs) {
					jsonArray.put(
						JSONUtil.put(
							"dataSourceIndividualPKs",
							JSONUtil.putAll(
								JSONUtil.put(
									"dataSourceId", "123456789"
								).put(
									"individualPKs",
									JSONUtil.putAll(individualPK)
								))
						).put(
							"individualSegmentIds", JSONUtil.putAll("1234567")
						));
				}

				return JSONUtil.put(
					"_embedded", JSONUtil.put("individuals", jsonArray)
				).put(
					"page",
					JSONUtil.put(
						"number", 0
					).put(
						"size", 100
					).put(
						"totalElements", individualPKs.length
					).put(
						"totalPages", 1
					)
				).put(
					"total", 0
				).toString();
			});
	}

	private void _prepareAsahFaroBackendClient(
		UnsafeSupplier<String, Exception> unsafeSupplier) {

		Object asahFaroBackendClient = ReflectionTestUtil.getFieldValue(
			_checkIndividualSegmentsSchedulerJobConfiguration,
			"_asahFaroBackendClient");

		ReflectionTestUtil.setFieldValue(
			asahFaroBackendClient, "_http",
			new MockHttp(
				HashMapBuilder.<String, UnsafeSupplier<String, Exception>>put(
					"/api/1.0/individual-segments",
					() -> JSONUtil.put(
						"_embedded",
						JSONUtil.put(
							"individual-segments",
							JSONUtil.putAll(
								JSONUtil.put(
									"id", "1234567"
								).put(
									"name", "Test Segment 1"
								)))
					).put(
						"page",
						JSONUtil.put(
							"number", 0
						).put(
							"size", 100
						).put(
							"totalElements", 1
						).put(
							"totalPages", 1
						)
					).put(
						"total", 0
					).toString()
				).put(
					"/api/1.0/individual-segments/1234567/individuals",
					unsafeSupplier
				).build()));
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.segments.asah.connector.internal.scheduler.CheckIndividualSegmentsSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration
		_checkIndividualSegmentsSchedulerJobConfiguration;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject(filter = "segments.entry.provider.source=ASAH_FARO_BACKEND")
	private SegmentsEntryProvider _segmentsEntryProvider;

	@Inject
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}