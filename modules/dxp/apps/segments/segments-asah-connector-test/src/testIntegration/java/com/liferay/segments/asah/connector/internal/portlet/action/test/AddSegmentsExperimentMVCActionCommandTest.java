/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.portlet.action.test;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.exception.DuplicateSegmentsExperimentException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.portlet.ActionRequest;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class AddSegmentsExperimentMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test(expected = DuplicateSegmentsExperimentException.class)
	public void testAddDuplicatedSegmentsExperiment() throws Exception {
		SegmentsExperimentConstants.Goal goal =
			SegmentsExperimentConstants.Goal.BOUNCE_RATE;

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			RandomTestUtil.randomString());

		SegmentsTestUtil.addSegmentsExperiment(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceId(),
			_layout.getPlid());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				RandomTestUtil.randomString(), goal.getLabel(),
				RandomTestUtil.randomString(), segmentsExperience.getPlid(),
				segmentsExperience.getSegmentsExperienceId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsURL", "http://localhost:8080/"
						).build())) {

			ReflectionTestUtil.invoke(
				_mvcActionCommand, "_addSegmentsExperiment",
				new Class<?>[] {ActionRequest.class},
				mockLiferayPortletActionRequest);
		}
	}

	@Test
	public void testAddSegmentsExperiment() throws Exception {
		String liferayAnalyticsURL = "http://localhost:8080/";

		String description = RandomTestUtil.randomString();

		SegmentsExperimentConstants.Goal goal =
			SegmentsExperimentConstants.Goal.BOUNCE_RATE;

		String name = RandomTestUtil.randomString();

		String segmentsEntryName = RandomTestUtil.randomString();

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			segmentsEntryName);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				description, goal.getLabel(), name,
				segmentsExperience.getPlid(),
				segmentsExperience.getSegmentsExperienceId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsFaroBackendURL",
							"http://localhost:8086"
						).put(
							"liferayAnalyticsURL", liferayAnalyticsURL
						).build())) {

			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_addSegmentsExperiment",
				new Class<?>[] {ActionRequest.class},
				mockLiferayPortletActionRequest);

			JSONObject segmentsExperimentJSONObject =
				(JSONObject)jsonObject.get("segmentsExperiment");

			Assert.assertEquals(
				0.0, segmentsExperimentJSONObject.getDouble("confidenceLevel"),
				0);
			Assert.assertEquals(
				description,
				segmentsExperimentJSONObject.getString("description"));

			SegmentsExperiment segmentsExperiment =
				_segmentsExperimentLocalService.fetchSegmentsExperiment(
					segmentsExperience.getGroupId(),
					segmentsExperience.getSegmentsExperienceKey(),
					segmentsExperience.getPlid());

			Assert.assertEquals(
				liferayAnalyticsURL + "/tests/overview/" +
					segmentsExperiment.getSegmentsExperimentKey(),
				segmentsExperimentJSONObject.getString("detailsURL"));

			Assert.assertTrue(
				segmentsExperimentJSONObject.getBoolean("editable"));
			Assert.assertEquals(
				String.valueOf(
					JSONUtil.put(
						"label", "Bounce Rate"
					).put(
						"value", goal.getLabel()
					)),
				String.valueOf(
					segmentsExperimentJSONObject.getJSONObject("goal")));
			Assert.assertEquals(
				name, segmentsExperimentJSONObject.getString("name"));
			Assert.assertEquals(
				segmentsEntryName,
				segmentsExperimentJSONObject.getString("segmentsEntryName"));
			Assert.assertEquals(
				String.valueOf(segmentsExperience.getSegmentsExperienceId()),
				segmentsExperimentJSONObject.getString("segmentsExperienceId"));
			Assert.assertEquals(
				String.valueOf(segmentsExperiment.getSegmentsExperimentId()),
				segmentsExperimentJSONObject.getString("segmentsExperimentId"));
			Assert.assertEquals(
				String.valueOf(
					JSONUtil.put(
						"label", "Draft"
					).put(
						"value",
						SegmentsExperimentConstants.Status.DRAFT.getValue()
					)),
				String.valueOf(
					segmentsExperimentJSONObject.getJSONObject("status")));
		}
	}

	@Test
	public void testAddSegmentsExperimentWithExistingTerminatedSegmentsExperiment()
		throws Exception {

		SegmentsExperimentConstants.Goal goal =
			SegmentsExperimentConstants.Goal.BOUNCE_RATE;

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			RandomTestUtil.randomString());

		SegmentsExperiment segmentsExperiment =
			SegmentsTestUtil.addSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceId(),
				_layout.getPlid());

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_TERMINATED);

		String name = RandomTestUtil.randomString();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				RandomTestUtil.randomString(), goal.getLabel(), name,
				segmentsExperience.getPlid(),
				segmentsExperience.getSegmentsExperienceId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsFaroBackendURL",
							"http://localhost:8086"
						).put(
							"liferayAnalyticsURL", "http://localhost:8080/"
						).build())) {

			Object asahFaroBackendClient = ReflectionTestUtil.getFieldValue(
				_mvcActionCommand, "_asahFaroBackendClient");

			ReflectionTestUtil.setFieldValue(
				asahFaroBackendClient, "_http",
				new MockHttp(
					Collections.singletonMap(
						"/api/1.0/experiments/" +
							segmentsExperiment.getSegmentsExperimentKey(),
						() -> JSONUtil.put(
							"id", "123456"
						).toString())));

			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_addSegmentsExperiment",
				new Class<?>[] {ActionRequest.class},
				mockLiferayPortletActionRequest);

			JSONObject segmentsExperimentJSONObject =
				(JSONObject)jsonObject.get("segmentsExperiment");

			segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					segmentsExperimentJSONObject.getLong(
						"segmentsExperienceId"));

			segmentsExperiment =
				_segmentsExperimentLocalService.fetchSegmentsExperiment(
					_group.getGroupId(),
					segmentsExperience.getSegmentsExperienceKey(),
					_layout.getPlid());

			Assert.assertNotNull(segmentsExperiment);
			Assert.assertEquals(name, segmentsExperiment.getName());
		}
	}

	@Test
	public void testAddSegmentsExperimentWithSecondarySegmentsExperienceSelected()
		throws Exception {

		String liferayAnalyticsURL = "http://localhost:8080/";

		String description = RandomTestUtil.randomString();

		SegmentsExperimentConstants.Goal goal =
			SegmentsExperimentConstants.Goal.BOUNCE_RATE;

		String name = RandomTestUtil.randomString();

		String segmentsEntryName = RandomTestUtil.randomString();

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			segmentsEntryName);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				description, goal.getLabel(), name,
				segmentsExperience.getPlid(), RandomTestUtil.nextLong());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsFaroBackendURL",
							"http://localhost:8086"
						).put(
							"liferayAnalyticsURL", liferayAnalyticsURL
						).build())) {

			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "_addSegmentsExperiment",
				new Class<?>[] {ActionRequest.class},
				mockLiferayPortletActionRequest);

			JSONObject segmentsExperimentJSONObject =
				(JSONObject)jsonObject.get("segmentsExperiment");

			Assert.assertEquals(
				0.0, segmentsExperimentJSONObject.getDouble("confidenceLevel"),
				0);
			Assert.assertEquals(
				description,
				segmentsExperimentJSONObject.getString("description"));

			segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					segmentsExperimentJSONObject.getLong(
						"segmentsExperienceId"));

			SegmentsExperiment segmentsExperiment =
				_segmentsExperimentLocalService.fetchSegmentsExperiment(
					_group.getGroupId(),
					segmentsExperience.getSegmentsExperienceKey(),
					_layout.getPlid());

			Assert.assertEquals(
				liferayAnalyticsURL + "/tests/overview/" +
					segmentsExperiment.getSegmentsExperimentKey(),
				segmentsExperimentJSONObject.getString("detailsURL"));

			Assert.assertTrue(
				segmentsExperimentJSONObject.getBoolean("editable"));
			Assert.assertEquals(
				String.valueOf(
					JSONUtil.put(
						"label", "Bounce Rate"
					).put(
						"value", goal.getLabel()
					)),
				String.valueOf(
					segmentsExperimentJSONObject.getJSONObject("goal")));
			Assert.assertEquals(
				name, segmentsExperimentJSONObject.getString("name"));
			Assert.assertEquals(
				String.valueOf(segmentsExperiment.getSegmentsExperimentId()),
				segmentsExperimentJSONObject.getString("segmentsExperimentId"));
			Assert.assertEquals(
				String.valueOf(
					JSONUtil.put(
						"label", "Draft"
					).put(
						"value",
						SegmentsExperimentConstants.Status.DRAFT.getValue()
					)),
				String.valueOf(
					segmentsExperimentJSONObject.getJSONObject("status")));
		}
	}

	private SegmentsExperience _addSegmentsExperience(String segmentsEntryName)
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), RandomTestUtil.randomString(),
			segmentsEntryName, RandomTestUtil.randomString());

		return SegmentsTestUtil.addSegmentsExperience(
			segmentsEntry.getSegmentsEntryId(), _layout.getPlid(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String description, String goal, String name, long plid,
			long segmentsExperienceId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter(
			"plid", String.valueOf(plid));
		mockLiferayPortletActionRequest.setParameter(
			"description", description);
		mockLiferayPortletActionRequest.setParameter("goal", goal);
		mockLiferayPortletActionRequest.setParameter("name", name);
		mockLiferayPortletActionRequest.setParameter(
			"segmentsExperienceId", String.valueOf(segmentsExperienceId));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "mvc.command.name=/segments_experiment/add_segments_experiment"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

}