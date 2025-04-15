/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.VersionableContentDashboardItem;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtypeFactory;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ResourceRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class GetContentDashboardItemVersionsResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetContentDashboardItemVersions() {
		int versionsCount = 15;

		ServiceRegistration<ContentDashboardItemFactory> serviceRegistration =
			_getServiceRegistration(versionsCount);

		try {
			MockLiferayResourceRequest mockLiferayResourceRequest =
				_getMockLiferayPortletResourceRequest();

			mockLiferayResourceRequest.setParameter(
				"className", TestItem.class.getName());
			mockLiferayResourceRequest.setParameter("classPK", "0");

			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcResourceCommand,
				"_getContentDashboardItemVersionsJSONObject",
				new Class<?>[] {ResourceRequest.class},
				mockLiferayResourceRequest);

			Assert.assertNotNull(jsonObject);

			String viewVersionsURL = jsonObject.getString("viewVersionsURL");

			Assert.assertEquals(_VIEW_VERSIONS_URL, viewVersionsURL);

			_assertVersions(jsonObject, 10, versionsCount);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetContentDashboardItemVersionsForInvalidClassName() {
		ServiceRegistration<ContentDashboardItemFactory> serviceRegistration =
			_getServiceRegistration(15);

		try {
			MockLiferayResourceRequest mockLiferayResourceRequest =
				_getMockLiferayPortletResourceRequest();

			mockLiferayResourceRequest.setParameter(
				"className", RandomTestUtil.randomString());

			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcResourceCommand,
				"_getContentDashboardItemVersionsJSONObject",
				new Class<?>[] {ResourceRequest.class},
				mockLiferayResourceRequest);

			Assert.assertNotNull(jsonObject);
			Assert.assertEquals(0, jsonObject.length());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetContentDashboardItemVersionsWithLimit() {
		int versionsCount = 15;

		ServiceRegistration<ContentDashboardItemFactory> serviceRegistration =
			_getServiceRegistration(versionsCount);

		try {
			int maxDisplayVersions = 5;

			MockLiferayResourceRequest mockLiferayResourceRequest =
				_getMockLiferayPortletResourceRequest();

			mockLiferayResourceRequest.setParameter(
				"className", TestItem.class.getName());
			mockLiferayResourceRequest.setParameter("classPK", "0");
			mockLiferayResourceRequest.setParameter(
				"maxDisplayVersions", String.valueOf(maxDisplayVersions));

			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcResourceCommand,
				"_getContentDashboardItemVersionsJSONObject",
				new Class<?>[] {ResourceRequest.class},
				mockLiferayResourceRequest);

			Assert.assertNotNull(jsonObject);

			String viewVersionsURL = jsonObject.getString("viewVersionsURL");

			Assert.assertEquals(_VIEW_VERSIONS_URL, viewVersionsURL);

			_assertVersions(jsonObject, maxDisplayVersions, versionsCount);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetContentDashboardItemVersionsWithoutViewVersionURL() {
		int versionsCount = 5;

		ServiceRegistration<ContentDashboardItemFactory> serviceRegistration =
			_getServiceRegistration(versionsCount);

		try {
			MockLiferayResourceRequest mockLiferayResourceRequest =
				_getMockLiferayPortletResourceRequest();

			mockLiferayResourceRequest.setParameter(
				"className", TestItem.class.getName());
			mockLiferayResourceRequest.setParameter("classPK", "0");

			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcResourceCommand,
				"_getContentDashboardItemVersionsJSONObject",
				new Class<?>[] {ResourceRequest.class},
				mockLiferayResourceRequest);

			Assert.assertNotNull(jsonObject);

			String viewVersionsURL = jsonObject.getString("viewVersionsURL");

			Assert.assertEquals(StringPool.BLANK, viewVersionsURL);

			_assertVersions(jsonObject, versionsCount, versionsCount);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _assertVersions(
		JSONObject jsonObject, int maxDisplayVersions, int versionsCount) {

		JSONArray versionsJSONArray = jsonObject.getJSONArray("versions");

		Assert.assertNotNull(versionsJSONArray);

		Assert.assertEquals(
			versionsJSONArray.toString(), maxDisplayVersions,
			versionsJSONArray.length());

		for (int i = 0; i < versionsJSONArray.length(); i++) {
			JSONObject versionJSONObject = versionsJSONArray.getJSONObject(i);

			Assert.assertEquals(
				versionsCount - i, versionJSONObject.getInt("version"));
		}
	}

	private MockLiferayResourceRequest _getMockLiferayPortletResourceRequest() {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST,
			new MockHttpServletRequest());

		return mockLiferayResourceRequest;
	}

	private ServiceRegistration<ContentDashboardItemFactory>
		_getServiceRegistration(int versionsCount) {

		Bundle bundle = FrameworkUtil.getBundle(
			GetContentDashboardItemVersionsResourceCommandTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			ContentDashboardItemFactory.class,
			new TestItemContentDashboardItemFactory(versionsCount), null);
	}

	private static final String _VIEW_VERSIONS_URL = "view_versions_url";

	@Inject(
		filter = "mvc.command.name=/content_dashboard/get_content_dashboard_item_versions"
	)
	private MVCResourceCommand _mvcResourceCommand;

	private static class TestItem {
	}

	private static class TestItemContentDashboardItem
		implements VersionableContentDashboardItem<TestItem> {

		public TestItemContentDashboardItem(int versionsCount) {
			_versionsCount = versionsCount;
		}

		@Override
		public List<ContentDashboardItemVersion>
			getAllContentDashboardItemVersions(
				HttpServletRequest httpServletRequest) {

			List<ContentDashboardItemVersion> contentDashboardItemVersions =
				new ArrayList<>();

			for (int i = _versionsCount; i > 0; i--) {
				contentDashboardItemVersions.add(
					new ContentDashboardItemVersion(
						null, null, null, RandomTestUtil.randomString(), null,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(), String.valueOf(i)));
			}

			return contentDashboardItemVersions;
		}

		@Override
		public List<AssetCategory> getAssetCategories() {
			return null;
		}

		@Override
		public List<AssetCategory> getAssetCategories(long vocabularyId) {
			return null;
		}

		@Override
		public List<AssetTag> getAssetTags() {
			return null;
		}

		@Override
		public List<Locale> getAvailableLocales() {
			return null;
		}

		@Override
		public List<ContentDashboardItemAction> getContentDashboardItemActions(
			HttpServletRequest httpServletRequest,
			ContentDashboardItemAction.Type... types) {

			return null;
		}

		@Override
		public ContentDashboardItemSubtype getContentDashboardItemSubtype() {
			return null;
		}

		@Override
		public Date getCreateDate() {
			return null;
		}

		@Override
		public ContentDashboardItemAction getDefaultContentDashboardItemAction(
			HttpServletRequest httpServletRequest) {

			return null;
		}

		@Override
		public Locale getDefaultLocale() {
			return null;
		}

		@Override
		public String getDescription(Locale locale) {
			return null;
		}

		@Override
		public long getId() {
			return 123456;
		}

		@Override
		public InfoItemReference getInfoItemReference() {
			return null;
		}

		@Override
		public List<ContentDashboardItemVersion>
			getLatestContentDashboardItemVersions(Locale locale) {

			return null;
		}

		@Override
		public Date getModifiedDate() {
			return null;
		}

		@Override
		public String getScopeName(Locale locale) {
			return null;
		}

		@Override
		public List<SpecificInformation<?>> getSpecificInformationList(
			Locale locale) {

			return Collections.emptyList();
		}

		@Override
		public String getTitle(Locale locale) {
			return null;
		}

		@Override
		public String getTypeLabel(Locale locale) {
			return null;
		}

		@Override
		public long getUserId() {
			return 0;
		}

		@Override
		public String getUserName() {
			return null;
		}

		@Override
		public String getViewVersionsURL(
			HttpServletRequest httpServletRequest) {

			return _VIEW_VERSIONS_URL;
		}

		@Override
		public boolean isViewable(HttpServletRequest httpServletRequest) {
			return true;
		}

		private final int _versionsCount;

	}

	private static class TestItemContentDashboardItemFactory
		implements ContentDashboardItemFactory<TestItem> {

		public TestItemContentDashboardItemFactory(int versionsCount) {
			_versionsCount = versionsCount;
		}

		@Override
		public ContentDashboardItem<TestItem> create(long classPK) {
			return new TestItemContentDashboardItem(_versionsCount);
		}

		@Override
		public ContentDashboardItemSubtypeFactory
			getContentDashboardItemSubtypeFactory() {

			return null;
		}

		private final int _versionsCount;

	}

}