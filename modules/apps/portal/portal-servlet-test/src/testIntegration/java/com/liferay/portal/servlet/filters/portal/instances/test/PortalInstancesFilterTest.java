/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.portal.instances.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.portal.instances.PortalInstancesFilter;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jorge Díaz
 */
@RunWith(Arquillian.class)
public class PortalInstancesFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExistingVirtualHost() throws Exception {
		String hostName = StringUtil.toLowerCase(RandomTestUtil.randomString());

		LayoutSet layoutSet = _layoutSetLocalService.updateVirtualHosts(
			TestPropsValues.getGroupId(), false,
			TreeMapBuilder.put(
				hostName, StringPool.BLANK
			).build());

		VirtualHost virtualHost = _virtualHostLocalService.getVirtualHost(
			hostName);

		try {
			_test(virtualHost.getCompanyId(), hostName, layoutSet, null);
		}
		finally {
			_virtualHostLocalService.deleteVirtualHost(virtualHost);
		}
	}

	@Test
	public void testNonexistingVirtualHostWithStrictAccessDisabled()
		throws Exception {

		long companyId = PortalInstancePool.getDefaultCompanyId();

		LayoutSet layoutSet = null;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			Group group = GroupLocalServiceUtil.fetchGroup(
				companyId, PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

			if (group != null) {
				layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
					group.getGroupId(), false);
			}
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"VIRTUAL_HOSTS_STRICT_ACCESS", false)) {

			_test(
				companyId,
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				layoutSet, null);
		}
	}

	@Test
	public void testNonexistingVirtualHostWithStrictAccessEnabled()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalInstancesFilter.class.getName(), LoggerTestUtil.ERROR);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"VIRTUAL_HOSTS_STRICT_ACCESS", true)) {

			String hostName = StringUtil.toLowerCase(
				RandomTestUtil.randomString());

			_test(null, hostName, null, true);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				NoSuchVirtualHostException.class, throwable.getClass());

			Assert.assertEquals(hostName, throwable.getMessage());
		}
	}

	private void _test(
			Long companyId, String hostname, LayoutSet layoutSet,
			Boolean unknownVirtualHost)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					CompanyConstants.SYSTEM)) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.addHeader("Host", hostname);

			_portalInstancesFilter.doFilterTry(
				mockHttpServletRequest, new MockHttpServletResponse());

			Assert.assertEquals(
				companyId,
				mockHttpServletRequest.getAttribute(WebKeys.COMPANY_ID));

			if (companyId == null) {
				Assert.assertEquals(
					CompanyConstants.SYSTEM,
					(long)CompanyThreadLocal.getCompanyId());
			}
			else {
				Assert.assertEquals(
					companyId, CompanyThreadLocal.getCompanyId());
			}

			Assert.assertEquals(
				unknownVirtualHost,
				mockHttpServletRequest.getAttribute(
					WebKeys.UNKNOWN_VIRTUAL_HOST));
			Assert.assertEquals(
				layoutSet,
				mockHttpServletRequest.getAttribute(
					WebKeys.VIRTUAL_HOST_LAYOUT_SET));
		}
	}

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private static VirtualHostLocalService _virtualHostLocalService;

	private final PortalInstancesFilter _portalInstancesFilter =
		new PortalInstancesFilter();

}