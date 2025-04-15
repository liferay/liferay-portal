/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.processor.test;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.processor.SegmentsExperienceRequestProcessor;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SegmentsExperimentSegmentsExperienceRequestProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testGetSegmentsExperienceIds() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(),
					new long[] {segmentsExperience.getSegmentsExperienceId()});

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsFromAnotherLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"segmentsExperienceId",
				String.valueOf(
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(layout.getPlid())));

			long defaultSegmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						segmentsExperience.getPlid());

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					mockHttpServletRequest, new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(),
					new long[] {defaultSegmentsExperienceId});

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);
			Assert.assertEquals(
				defaultSegmentsExperienceId, segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithCookie() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.setCookies(
				new Cookie(
					"ab_test_variant_id_" + _layout.getPlid(),
					segmentsExperiment.getSegmentsExperienceKey()));

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					mockHttpServletRequest, new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(), new long[0]);

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithNonexistentSegmentsEntryId()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"segmentsExperienceId",
				String.valueOf(RandomTestUtil.randomLong()));

			long defaultSegmentsExperienceId =
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						segmentsExperience.getPlid());

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(),
					new long[] {defaultSegmentsExperienceId});

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(
				defaultSegmentsExperienceId, segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithoutLiferayAnalyticsEnableAllGroupIds()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", false
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(),
					new long[] {12345L});

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(12345L, segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithoutSegmentsExperienceIds()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(), new long[0]);

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 0,
				segmentsExperienceIds.length);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithSegmentsEntryIds()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					_getMockHttpServletRequest(), new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(),
					new long[] {segmentsEntry.getSegmentsEntryId()},
					new long[] {segmentsExperience.getSegmentsExperienceId()});

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithSegmentsExperienceId()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId());

			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.addSegmentsExperience(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
					RandomTestUtil.randomLocaleStringMap(), true,
					new UnicodeProperties(true),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"segmentsExperienceId",
				new String[] {
					String.valueOf(segmentsExperience.getSegmentsExperienceId())
				});

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					mockHttpServletRequest, new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(), new long[0]);

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithSegmentsExperienceKey()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId());

			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.addSegmentsExperience(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
					RandomTestUtil.randomLocaleStringMap(), true,
					new UnicodeProperties(true),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"segmentsExperienceKey",
				new String[] {segmentsExperience.getSegmentsExperienceKey()});

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					mockHttpServletRequest, new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(), new long[0]);

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperienceIds[0]);
		}
	}

	@Test
	public void testGetSegmentsExperienceIdsWithSegmentsExperimentKey()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.randomLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							RandomTestUtil.randomString()
						).build())) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"segmentsExperimentKey",
				new String[] {segmentsExperiment.getSegmentsExperimentKey()});

			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessor.getSegmentsExperienceIds(
					mockHttpServletRequest, new MockHttpServletResponse(),
					_group.getGroupId(), _layout.getPlid(), new long[0]);

			Assert.assertEquals(
				Arrays.toString(segmentsExperienceIds), 1,
				segmentsExperienceIds.length);

			Assert.assertEquals(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperienceIds[0]);
		}
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSignedIn(true);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "component.name=com.liferay.segments.experiment.web.internal.processor.SegmentsExperimentSegmentsExperienceRequestProcessor"
	)
	private SegmentsExperienceRequestProcessor
		_segmentsExperienceRequestProcessor;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Inject
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}