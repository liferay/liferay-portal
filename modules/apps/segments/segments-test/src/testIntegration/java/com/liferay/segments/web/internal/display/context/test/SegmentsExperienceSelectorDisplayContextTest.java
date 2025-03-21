/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Writer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class SegmentsExperienceSelectorDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		Bundle bundle = FrameworkUtil.getBundle(
			SegmentsExperienceSelectorDisplayContextTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_mockReactRenderer = new MockReactRenderer();

		_serviceRegistration = bundleContext.registerService(
			ReactRenderer.class, _mockReactRenderer,
			HashMapDictionaryBuilder.<String, Object>put(
				"service.ranking", Integer.MAX_VALUE
			).build());

		Collection<ServiceReference<ProductNavigationControlMenuEntry>>
			serviceReferences = bundleContext.getServiceReferences(
				ProductNavigationControlMenuEntry.class,
				"(product.navigation.control.menu.category.key=exp)");

		Assert.assertEquals(
			serviceReferences.toString(), 1, serviceReferences.size());

		Iterator<ServiceReference<ProductNavigationControlMenuEntry>> iterator =
			serviceReferences.iterator();

		_productNavigationControlMenuEntry = bundleContext.getService(
			iterator.next());
	}

	@After
	public void tearDown() throws Exception {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetDataWithDefaultSegmentsExperience() throws Exception {
		SegmentsExperience expectedDefaultSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()));

		Assert.assertNotNull(expectedDefaultSegmentsExperience);

		Map<String, Object> actualData =
			_getDataFromRenderingSegmentsExperienceSelector(
				expectedDefaultSegmentsExperience.getSegmentsExperienceId());

		Assert.assertNotNull(actualData);

		JSONArray actualSegmentsExperiencesJSONArray =
			(JSONArray)actualData.get("segmentsExperiences");

		Assert.assertEquals(1, actualSegmentsExperiencesJSONArray.length());

		JSONObject actualSegmentsExperienceJSONObject =
			actualSegmentsExperiencesJSONArray.getJSONObject(0);

		Assert.assertTrue(
			actualSegmentsExperienceJSONObject.getBoolean("active"));
		Assert.assertEquals(
			expectedDefaultSegmentsExperience.getSegmentsEntryId(),
			actualSegmentsExperienceJSONObject.getLong("segmentsEntryId"));
		Assert.assertEquals(
			SegmentsEntryConstants.getDefaultSegmentsEntryName(
				LocaleUtil.ENGLISH),
			actualSegmentsExperienceJSONObject.getString("segmentsEntryName"));
		Assert.assertEquals(
			expectedDefaultSegmentsExperience.getSegmentsExperienceId(),
			actualSegmentsExperienceJSONObject.getLong("segmentsExperienceId"));
		Assert.assertEquals(
			expectedDefaultSegmentsExperience.getName(LocaleUtil.ENGLISH),
			actualSegmentsExperienceJSONObject.getString(
				"segmentsExperienceName"));

		JSONObject actualSelectedSegmentsExperienceJSONObject =
			(JSONObject)actualData.get("selectedSegmentsExperience");

		Assert.assertEquals(
			expectedDefaultSegmentsExperience.getSegmentsExperienceId(),
			actualSelectedSegmentsExperienceJSONObject.getLong(
				"segmentsExperienceId"));

		Assert.assertEquals(
			"Active",
			actualSegmentsExperienceJSONObject.getString("statusLabel"));
		Assert.assertNotNull(
			actualSegmentsExperienceJSONObject.getString("url"));
	}

	@Test
	public void testGetDataWithDefaultSegmentsExperienceAndSelectedAnonExistingSegmentsExperience()
		throws Exception {

		SegmentsExperience expectedDefaultSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()));

		Map<String, Object> actualData =
			_getDataFromRenderingSegmentsExperienceSelector(123456);

		Assert.assertNotNull(actualData);

		JSONObject actualSelectedSegmentsExperienceJSONObject =
			(JSONObject)actualData.get("selectedSegmentsExperience");

		Assert.assertEquals(
			expectedDefaultSegmentsExperience.getSegmentsExperienceId(),
			actualSelectedSegmentsExperienceJSONObject.getLong(
				"segmentsExperienceId"));
	}

	@Test
	public void testGetDataWithDefaultSegmentsExperienceAndSelectedExperienceDoesNotBelongToLayout()
		throws Exception {

		SegmentsExperience expectedDefaultSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), layout.getPlid());

		Map<String, Object> actualData =
			_getDataFromRenderingSegmentsExperienceSelector(
				segmentsExperience.getSegmentsExperienceId());

		Assert.assertNotNull(actualData);

		JSONObject actualSelectedSegmentsExperienceJSONObject =
			(JSONObject)actualData.get("selectedSegmentsExperience");

		Assert.assertEquals(
			expectedDefaultSegmentsExperience.getSegmentsExperienceId(),
			actualSelectedSegmentsExperienceJSONObject.getLong(
				"segmentsExperienceId"));
	}

	@Test
	public void testGetDataWithSegmentsExperience() throws Exception {
		SegmentsExperience expectedSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		SegmentsEntry expectedSegmentsEntry =
			_segmentsEntryLocalService.fetchSegmentsEntry(
				expectedSegmentsExperience.getSegmentsEntryId());

		Assert.assertNotNull(expectedSegmentsEntry);

		Map<String, Object> actualData =
			_getDataFromRenderingSegmentsExperienceSelector(
				expectedSegmentsExperience.getSegmentsExperienceId());

		Assert.assertNotNull(actualData);

		JSONArray actualSegmentsExperiencesJSONArray =
			(JSONArray)actualData.get("segmentsExperiences");

		Assert.assertEquals(2, actualSegmentsExperiencesJSONArray.length());

		JSONObject actualSegmentsExperienceJSONObject =
			actualSegmentsExperiencesJSONArray.getJSONObject(1);

		Assert.assertFalse(
			actualSegmentsExperienceJSONObject.getBoolean("active"));
		Assert.assertEquals(
			expectedSegmentsExperience.getSegmentsEntryId(),
			actualSegmentsExperienceJSONObject.getLong("segmentsEntryId"));
		Assert.assertEquals(
			expectedSegmentsEntry.getName(LocaleUtil.ENGLISH),
			actualSegmentsExperienceJSONObject.getString("segmentsEntryName"));
		Assert.assertEquals(
			expectedSegmentsExperience.getSegmentsExperienceId(),
			actualSegmentsExperienceJSONObject.getLong("segmentsExperienceId"));
		Assert.assertEquals(
			expectedSegmentsExperience.getName(LocaleUtil.ENGLISH),
			actualSegmentsExperienceJSONObject.getString(
				"segmentsExperienceName"));

		JSONObject actualSelectedSegmentsExperienceJSONObject =
			(JSONObject)actualData.get("selectedSegmentsExperience");

		Assert.assertEquals(
			expectedSegmentsExperience.getSegmentsExperienceId(),
			actualSelectedSegmentsExperienceJSONObject.getLong(
				"segmentsExperienceId"));

		Assert.assertEquals(
			"Inactive",
			actualSegmentsExperienceJSONObject.getString("statusLabel"));
		Assert.assertNotNull(
			actualSegmentsExperienceJSONObject.getString("url"));
	}

	private Map<String, Object> _getDataFromRenderingSegmentsExperienceSelector(
			long selectedSegmentsExperienceId)
		throws Exception {

		_productNavigationControlMenuEntry.includeIcon(
			_getMockHttpServletRequest(selectedSegmentsExperienceId),
			new MockHttpServletResponse());

		return _mockReactRenderer.getData();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			long segmentsExperienceId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"segmentsExperienceId", String.valueOf(segmentsExperienceId));
		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080/");
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.ENGLISH);
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;
	private MockReactRenderer _mockReactRenderer;
	private ProductNavigationControlMenuEntry
		_productNavigationControlMenuEntry;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceRegistration<?> _serviceRegistration;

	private class MockReactRenderer implements ReactRenderer {

		public Map<String, Object> getData() {
			return _data;
		}

		@Override
		public void renderReact(
				ComponentDescriptor componentDescriptor,
				Map<String, Object> data, HttpServletRequest httpServletRequest,
				Writer writer)
			throws IOException {

			_data = data;
		}

		private Map<String, Object> _data;

	}

}