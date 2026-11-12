/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.frontend.taglib.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.multi.factor.authentication.spi.checker.setup.SetupMFAChecker;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class MFASetupUserAccountScreenNavigationEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		Bundle bundle = FrameworkUtil.getBundle(
			MFASetupUserAccountScreenNavigationEntryTest.class);

		_bundleContext = bundle.getBundleContext();

		_serviceRegistration = _bundleContext.registerService(
			SetupMFAChecker.class, new TestSetupMFAChecker(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", _company.getCompanyId()
			).build());

		_screenNavigationEntry = _getScreenNavigationEntry(
			TestSetupMFAChecker.class.getName());
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testIsVisibleWhenMFACompanyConfigurationIsEnabledAndEmailOTPIsDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				mfaCompanyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.web." +
							"internal.company.configuration." +
								"MFACompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build());
			CompanyConfigurationTemporarySwapper
				mfaEmailOTPConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.email.otp." +
							"configuration.MFAEmailOTPConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			Assert.assertTrue(_screenNavigationEntry.isVisible(null, null));
		}
	}

	@Test
	public void testIsVisibleWhenMFAIsDisabled() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				mfaCompanyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.web." +
							"internal.company.configuration." +
								"MFACompanyConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build());
			CompanyConfigurationTemporarySwapper
				mfaEmailOTPConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.email.otp." +
							"configuration.MFAEmailOTPConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", false
						).build())) {

			Assert.assertFalse(_screenNavigationEntry.isVisible(null, null));
		}
	}

	private ScreenNavigationEntry<User> _getScreenNavigationEntry(
			String entryKey)
		throws Exception {

		ServiceReference<?>[] serviceReferences =
			_bundleContext.getServiceReferences(
				ScreenNavigationEntry.class.getName(), null);

		if (serviceReferences == null) {
			return null;
		}

		for (ServiceReference<?> serviceReference : serviceReferences) {
			ScreenNavigationEntry<User> screenNavigationEntry =
				(ScreenNavigationEntry<User>)_bundleContext.getService(
					serviceReference);

			if (entryKey.equals(screenNavigationEntry.getEntryKey())) {
				return screenNavigationEntry;
			}
		}

		return null;
	}

	private BundleContext _bundleContext;
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ScreenNavigationEntry<User> _screenNavigationEntry;
	private ServiceRegistration<SetupMFAChecker> _serviceRegistration;

	private static class TestSetupMFAChecker implements SetupMFAChecker {

		@Override
		public void includeSetup(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId) {
		}

		@Override
		public boolean isAvailable(long userId) {
			return true;
		}

		@Override
		public void removeExistingSetup(long userId) {
		}

		@Override
		public boolean setUp(
			HttpServletRequest httpServletRequest, long userId) {

			return false;
		}

	}

}