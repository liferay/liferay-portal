/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.layout.display.page.internal.info.item.test;

import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.analytics.reports.layout.display.page.internal.test.MockObject;
import com.liferay.analytics.reports.layout.display.page.internal.test.layout.display.page.MockObjectLayoutDisplayPageObjectProvider;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class LayoutDisplayPageObjectProviderAnalyticsReportsInfoItemTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		_className = ClassNameLocalServiceUtil.addClassName(
			MockObject.class.getName());
	}

	@AfterClass
	public static void tearDownClass() {
		ClassNameLocalServiceUtil.deleteClassName(_className);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetAuthorName() throws Exception {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId());

		Assert.assertEquals(
			StringPool.BLANK,
			_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
				getAuthorName(mockObjectLayoutDisplayPageObjectProvider));
	}

	@Test
	public void testGetAuthorUserId() throws Exception {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId());

		Assert.assertEquals(
			0L,
			_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
				getAuthorUserId(mockObjectLayoutDisplayPageObjectProvider));
	}

	@Test
	public void testGetAvailableLocales() throws Exception {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId(), _group.getGroupId());

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.SPAIN),
			LocaleUtil.SPAIN);

		Assert.assertEquals(
			Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.SPAIN),
			_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
				getAvailableLocales(mockObjectLayoutDisplayPageObjectProvider));
	}

	@Test
	public void testGetCanonicalURL() throws Exception {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId(), _group.getGroupId());

		Bundle bundle = FrameworkUtil.getBundle(
			LayoutDisplayPageObjectProviderAnalyticsReportsInfoItemTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<LayoutDisplayPageProvider<?>> serviceRegistration =
			bundleContext.registerService(
				(Class<LayoutDisplayPageProvider<?>>)
					(Class<?>)LayoutDisplayPageProvider.class,
				new LayoutDisplayPageProvider<MockObject>() {

					@Override
					public String getClassName() {
						return MockObject.class.getName();
					}

					@Override
					public LayoutDisplayPageObjectProvider
						getLayoutDisplayPageObjectProvider(
							InfoItemReference infoItemReference) {

						return mockObjectLayoutDisplayPageObjectProvider;
					}

					@Override
					public LayoutDisplayPageObjectProvider
						getLayoutDisplayPageObjectProvider(
							long groupId, String urlTitle) {

						return mockObjectLayoutDisplayPageObjectProvider;
					}

					@Override
					public String getURLSeparator() {
						return "||separator||";
					}

				},
				new HashMapDictionary<String, String>());

		try {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(), _className.getClassNameId(), 0, null,
					true, WorkflowConstants.STATUS_APPROVED);

			_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_className.getClassNameId(), 0,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(_group.getGroupId());

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setAttribute(
				WebKeys.CURRENT_COMPLETE_URL, StringPool.BLANK);

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setCompany(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()));

			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			themeDisplay.setLayoutSet(layout.getLayoutSet());

			themeDisplay.setRequest(mockHttpServletRequest);
			themeDisplay.setScopeGroupId(_group.getGroupId());
			themeDisplay.setSiteGroupId(_group.getGroupId());

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			serviceContext.setRequest(mockHttpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			String canonicalURL =
				_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
					getCanonicalURL(
						mockObjectLayoutDisplayPageObjectProvider,
						LocaleUtil.US);

			Assert.assertTrue(canonicalURL.contains("||separator||"));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetDefaultLocale() throws Exception {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId(), _group.getGroupId());

		GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(),
			Arrays.asList(LocaleUtil.BRAZIL, LocaleUtil.SPAIN),
			LocaleUtil.SPAIN);

		Assert.assertEquals(
			LocaleUtil.SPAIN,
			_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
				getDefaultLocale(mockObjectLayoutDisplayPageObjectProvider));
	}

	@Test
	public void testGetPublishDate() {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId(), _group.getGroupId());

		Bundle bundle = FrameworkUtil.getBundle(
			LayoutDisplayPageObjectProviderAnalyticsReportsInfoItemTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Date createDate = new Date();

		ServiceRegistration<InfoItemFieldValuesProvider<?>>
			serviceRegistration = bundleContext.registerService(
				(Class<InfoItemFieldValuesProvider<?>>)
					(Class<?>)InfoItemFieldValuesProvider.class,
				MockInfoItemFieldValuesProvider.builder(
				).createDate(
					createDate
				).build(),
				new HashMapDictionary<String, String>());

		try {
			Assert.assertEquals(
				createDate,
				_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
					getPublishDate(mockObjectLayoutDisplayPageObjectProvider));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetPublishDateWithDisplayPage() throws Exception {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId(), _group.getGroupId());

		Bundle bundle = FrameworkUtil.getBundle(
			LayoutDisplayPageObjectProviderAnalyticsReportsInfoItemTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<InfoItemFieldValuesProvider<?>>
			serviceRegistration = bundleContext.registerService(
				(Class<InfoItemFieldValuesProvider<?>>)
					(Class<?>)InfoItemFieldValuesProvider.class,
				MockInfoItemFieldValuesProvider.builder(
				).createDate(
					new Date()
				).build(),
				new HashMapDictionary<String, String>());

		try {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				DisplayPageTemplateTestUtil.addDisplayPageTemplate(
					_group.getGroupId(), _className.getClassNameId(), 0, null,
					true, WorkflowConstants.STATUS_APPROVED);

			AssetDisplayPageEntry assetDisplayPageEntry =
				_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
					TestPropsValues.getUserId(), _group.getGroupId(),
					_className.getClassNameId(), 0,
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
					AssetDisplayPageConstants.TYPE_SPECIFIC,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			Assert.assertEquals(
				assetDisplayPageEntry.getCreateDate(),
				_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
					getPublishDate(mockObjectLayoutDisplayPageObjectProvider));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetTitle() throws Exception {
		MockObjectLayoutDisplayPageObjectProvider
			mockObjectLayoutDisplayPageObjectProvider =
				new MockObjectLayoutDisplayPageObjectProvider(
					_className.getClassNameId(), _group.getGroupId());

		Bundle bundle = FrameworkUtil.getBundle(
			LayoutDisplayPageObjectProviderAnalyticsReportsInfoItemTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String title = RandomTestUtil.randomString();

		ServiceRegistration<InfoItemFieldValuesProvider<?>>
			serviceRegistration = bundleContext.registerService(
				(Class<InfoItemFieldValuesProvider<?>>)
					(Class<?>)InfoItemFieldValuesProvider.class,
				MockInfoItemFieldValuesProvider.builder(
				).title(
					title
				).build(),
				new HashMapDictionary<String, String>());

		try {
			Assert.assertEquals(
				title,
				_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem.
					getTitle(
						mockObjectLayoutDisplayPageObjectProvider,
						LocaleUtil.ENGLISH));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static ClassName _className;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "model.class.name=com.liferay.layout.display.page.LayoutDisplayPageObjectProvider"
	)
	private AnalyticsReportsInfoItem<LayoutDisplayPageObjectProvider>
		_layoutDisplayPageObjectProviderAnalyticsReportsInfoItem;

	private static class MockInfoItemFieldValuesProvider
		implements InfoItemFieldValuesProvider<MockObject> {

		public static Builder builder() {
			return new Builder();
		}

		public MockInfoItemFieldValuesProvider(Date createDate, String title) {
			_createDate = createDate;
			_title = title;
		}

		@Override
		public InfoItemFieldValues getInfoItemFieldValues(
			MockObject mockObject) {

			return InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						DateInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"createDate"
					).labelInfoLocalizedValue(
						null
					).build(),
					_createDate)
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						StringPool.BLANK
					).name(
						"title"
					).labelInfoLocalizedValue(
						null
					).build(),
					_title)
			).build();
		}

		public static class Builder {

			public MockInfoItemFieldValuesProvider build() {
				return new MockInfoItemFieldValuesProvider(_createDate, _title);
			}

			public Builder createDate(Date createDate) {
				_createDate = createDate;

				return this;
			}

			public Builder title(String title) {
				_title = title;

				return this;
			}

			private Date _createDate;
			private String _title;

		}

		private final Date _createDate;
		private final String _title;

	}

}