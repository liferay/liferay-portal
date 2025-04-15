/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.impl.ThemeLocalServiceImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class FragmentCollectionContributorPropagationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		_bundleContext = bundle.getBundleContext();
	}

	@After
	public void tearDown() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();

		if (_company != null) {
			try {
				_companyLocalService.deleteCompany(_company);
			}
			catch (PortalException portalException) {
			}
		}
	}

	@Test
	public void testPropagateContributedFragmentEntryThemeNotRegistered()
		throws Exception {

		Group defaultGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		Layout layout = _layoutLocalService.fetchFirstLayout(
			defaultGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false);

		LayoutSet layoutSet = layout.getLayoutSet();

		String originalCSS = layoutSet.getCss();
		String originalColorSchemeId = layoutSet.getColorSchemeId();
		String originalThemeId = layoutSet.getThemeId();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ThemeLocalServiceImpl.class.getName(), LoggerTestUtil.WARN)) {

			_layoutSetLocalService.updateLookAndFeel(
				defaultGroup.getGroupId(), "not_registered_theme",
				StringPool.BLANK, StringPool.BLANK);

			String originalHTML =
				"<div>" + RandomTestUtil.randomString() + "</div>";

			String fragmentCollectionContributorKey =
				RandomTestUtil.randomString();

			String fragmentEntryKey = StringBundler.concat(
				fragmentCollectionContributorKey, StringPool.DASH,
				RandomTestUtil.randomString());

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(),
					defaultGroup.getGroupId(), 0, 0,
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(layout.getPlid()),
					layout.getPlid(), StringPool.BLANK, originalHTML,
					StringPool.BLANK, StringPool.BLANK, null, StringPool.BLANK,
					0, fragmentEntryKey, FragmentConstants.TYPE_COMPONENT,
					ServiceContextTestUtil.getServiceContext(
						defaultGroup.getGroupId()));

			Assert.assertEquals(originalHTML, fragmentEntryLink.getHtml());

			TestFragmentEntryProcessor testFragmentEntryProcessor =
				new TestFragmentEntryProcessor(fragmentEntryKey);

			_serviceRegistrations.add(
				_bundleContext.registerService(
					FragmentEntryProcessor.class, testFragmentEntryProcessor,
					MapUtil.singletonDictionary(
						"fragment.entry.processor.priority", 1)));

			String modifiedHTML =
				"<div>" + RandomTestUtil.randomString() + "</div>";

			_serviceRegistrations.add(
				_bundleContext.registerService(
					FragmentCollectionContributor.class,
					new TestFragmentCollectionContributor(
						fragmentCollectionContributorKey,
						HashMapBuilder.put(
							FragmentConstants.TYPE_COMPONENT,
							_getFragmentEntry(
								fragmentEntryKey, modifiedHTML,
								FragmentConstants.TYPE_COMPONENT)
						).build()),
					MapUtil.singletonDictionary(
						"fragment.collection.key",
						fragmentCollectionContributorKey)));

			Map<Long, String> companyIdsMap =
				testFragmentEntryProcessor.getCompanyIdsMap();

			Assert.assertEquals(
				companyIdsMap.get(TestPropsValues.getCompanyId()),
				modifiedHTML);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			for (LogEntry logEntry : logEntries) {
				Assert.assertEquals(
					"No theme found for specified theme id " +
						"not_registered_theme. Returning the default theme.",
					logEntry.getMessage());
			}
		}
		finally {
			try {
				_layoutSetLocalService.updateLookAndFeel(
					defaultGroup.getGroupId(), originalThemeId,
					originalColorSchemeId, originalCSS);
			}
			catch (Exception exception) {
			}
		}
	}

	@Test
	public void testPropagateContributedFragmentEntryThroughMultipleCompanies()
		throws Exception {

		_company = CompanyTestUtil.addCompany();

		int companiesCount = _companyLocalService.getCompaniesCount();

		Assert.assertTrue(companiesCount > 1);

		String fragmentCollectionContributorKey = RandomTestUtil.randomString();

		String fragmentEntryKey = StringBundler.concat(
			fragmentCollectionContributorKey, StringPool.DASH,
			RandomTestUtil.randomString());

		_addFragmentEntryLinks(fragmentEntryKey);

		TestFragmentEntryProcessor testFragmentEntryProcessor =
			new TestFragmentEntryProcessor(fragmentEntryKey);

		_serviceRegistrations.add(
			_bundleContext.registerService(
				FragmentEntryProcessor.class, testFragmentEntryProcessor,
				MapUtil.singletonDictionary(
					"fragment.entry.processor.priority", 1)));

		String modifiedHTML =
			"<div>" + RandomTestUtil.randomString() + "</div>";

		ServiceContext originalServiceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			_setUpServiceContext();

			_serviceRegistrations.add(
				_bundleContext.registerService(
					FragmentCollectionContributor.class,
					new TestFragmentCollectionContributor(
						fragmentCollectionContributorKey,
						HashMapBuilder.put(
							FragmentConstants.TYPE_COMPONENT,
							_getFragmentEntry(
								fragmentEntryKey, modifiedHTML,
								FragmentConstants.TYPE_COMPONENT)
						).build()),
					MapUtil.singletonDictionary(
						"fragment.collection.key",
						fragmentCollectionContributorKey)));

			_assertCompanyContext(TestPropsValues.getCompanyId());
		}
		finally {
			ServiceContextThreadLocal.pushServiceContext(
				originalServiceContext);
		}

		Map<Long, String> companyIdsMap =
			testFragmentEntryProcessor.getCompanyIdsMap();

		Assert.assertEquals(
			companyIdsMap.toString(), companiesCount, companyIdsMap.size());

		_companyLocalService.forEachCompanyId(
			companyId -> Assert.assertEquals(
				companyIdsMap.get(companyId), modifiedHTML));
	}

	private void _addFragmentEntryLinks(String fragmentEntryKey)
		throws Exception {

		String originalHTML =
			"<div>" + RandomTestUtil.randomString() + "</div>";

		_companyLocalService.forEachCompanyId(
			companyId -> {
				User user = _userLocalService.fetchGuestUser(companyId);

				Group group = _groupLocalService.getGroup(
					companyId, GroupConstants.GUEST);

				Layout layout = _layoutLocalService.fetchFirstLayout(
					group.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false);

				long segmentsExperienceId =
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(layout.getPlid());

				FragmentEntryLink fragmentEntryLink =
					_fragmentEntryLinkLocalService.addFragmentEntryLink(
						null, user.getUserId(), group.getGroupId(), 0, 0,
						segmentsExperienceId, layout.getPlid(),
						StringPool.BLANK, originalHTML, StringPool.BLANK,
						StringPool.BLANK, null, StringPool.BLANK, 0,
						fragmentEntryKey, FragmentConstants.TYPE_COMPONENT,
						ServiceContextTestUtil.getServiceContext(
							group.getGroupId()));

				Assert.assertEquals(originalHTML, fragmentEntryLink.getHtml());
			});
	}

	private void _assertCompanyContext(long companyId) {
		Assert.assertTrue(
			Objects.equals(companyId, CompanyThreadLocal.getCompanyId()));

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Assert.assertNotNull(serviceContext);
		Assert.assertEquals(companyId, serviceContext.getCompanyId());
		_assertHttpServletRequest(companyId, serviceContext.getRequest());
		_assertThemeDisplay(companyId, serviceContext.getThemeDisplay());
	}

	private void _assertHttpServletRequest(
		long companyId, HttpServletRequest httpServletRequest) {

		Assert.assertNotNull(httpServletRequest);
		Assert.assertEquals(
			companyId,
			GetterUtil.getLong(
				httpServletRequest.getAttribute(WebKeys.COMPANY_ID)));

		_assertThemeDisplay(
			companyId,
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY));

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		Assert.assertNotNull(layout);
		Assert.assertEquals(companyId, layout.getCompanyId());

		User user = (User)httpServletRequest.getAttribute(WebKeys.USER);

		Assert.assertNotNull(user);

		User guestUser = _userLocalService.fetchGuestUser(companyId);

		Assert.assertEquals(guestUser.getUserId(), user.getUserId());

		Assert.assertEquals(
			user.getUserId(),
			GetterUtil.getLong(
				httpServletRequest.getAttribute(WebKeys.USER_ID)));
	}

	private void _assertThemeDisplay(
		long companyId, ThemeDisplay themeDisplay) {

		Assert.assertNotNull(themeDisplay);
		Assert.assertNotNull(themeDisplay.getCompany());
		Assert.assertEquals(companyId, themeDisplay.getCompanyId());
		Assert.assertNotNull(themeDisplay.getRequest());
		Assert.assertNotNull(themeDisplay.getResponse());
		Assert.assertNotNull(themeDisplay.getUser());
	}

	private FragmentEntry _getFragmentEntry(String key, String html, int type) {
		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.createFragmentEntry(0L);

		fragmentEntry.setFragmentEntryKey(key);
		fragmentEntry.setName(RandomTestUtil.randomString());
		fragmentEntry.setCss(null);
		fragmentEntry.setHtml(html);
		fragmentEntry.setJs(null);
		fragmentEntry.setConfiguration(null);
		fragmentEntry.setType(type);

		return fragmentEntry;
	}

	private void _setUpServiceContext() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group group = _groupLocalService.getGroup(
			company.getCompanyId(), GroupConstants.GUEST);

		User user = _userLocalService.fetchGuestUser(company.getCompanyId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, company.getCompanyId());
		httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(_portal.getSiteDefaultLocale(group));

		LayoutSet layoutSet = group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setPlid(layout.getPlid());
		themeDisplay.setRealUser(user);
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(user);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		httpServletRequest.setAttribute(WebKeys.USER, user);
		httpServletRequest.setAttribute(WebKeys.USER_ID, user.getUserId());

		serviceContext.setRequest(httpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		_assertCompanyContext(company.getCompanyId());
	}

	private BundleContext _bundleContext;
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	private class TestFragmentEntryProcessor implements FragmentEntryProcessor {

		public TestFragmentEntryProcessor(String fragmentEntryKey) {
			_fragmentEntryKey = fragmentEntryKey;
		}

		public Map<Long, String> getCompanyIdsMap() {
			return _companyIdsMap;
		}

		@Override
		public String processFragmentEntryLinkHTML(
				FragmentEntryLink fragmentEntryLink, String html,
				FragmentEntryProcessorContext fragmentEntryProcessorContext)
			throws PortalException {

			if (!Objects.equals(
					_fragmentEntryKey, fragmentEntryLink.getRendererKey())) {

				return html;
			}

			_assertCompanyContext(fragmentEntryLink.getCompanyId());

			_assertHttpServletRequest(
				fragmentEntryLink.getCompanyId(),
				fragmentEntryProcessorContext.getHttpServletRequest());

			Assert.assertNotNull(
				fragmentEntryProcessorContext.getHttpServletResponse());

			_companyIdsMap.put(fragmentEntryLink.getCompanyId(), html);

			return html;
		}

		private final Map<Long, String> _companyIdsMap = new HashMap<>();
		private final String _fragmentEntryKey;

	}

}