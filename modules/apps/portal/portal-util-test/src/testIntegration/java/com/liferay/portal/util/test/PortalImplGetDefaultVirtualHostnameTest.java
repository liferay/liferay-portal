/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.VirtualHostLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class PortalImplGetDefaultVirtualHostnameTest
	extends BasePortalImplURLTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-90275")
	public void testGetDefaultVirtualHostname() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"VIRTUAL_HOSTS_DEFAULT_SITE_NAME", group.getName())) {

			_testGetDefaultVirtualHostname(false, StringPool.BLANK);

			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				group.getGroupId(), false);

			String companyFallbackVirtualHostname =
				layoutSet.getCompanyFallbackVirtualHostname();

			Assert.assertNotNull(companyFallbackVirtualHostname);

			_testGetDefaultVirtualHostname(
				true, companyFallbackVirtualHostname);
		}
	}

	@Test
	public void testGetDefaultVirtualHostnameAfterCompanyVirtualHostUpdate()
		throws Exception {

		TreeMap<String, String> hostnames = new TreeMap<>();

		for (VirtualHost virtualHost :
				_virtualHostLocalService.getVirtualHosts(
					company.getCompanyId(), 0)) {

			hostnames.put(
				virtualHost.getHostname(), virtualHost.getLanguageId());
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"VIRTUAL_HOSTS_DEFAULT_SITE_NAME", group.getGroupKey())) {

			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				group.getGroupId(), false);

			Assert.assertEquals(
				company.getVirtualHostname(),
				portal.getDefaultVirtualHostname(true, layoutSet));

			String virtualHostname = "a-" + RandomTestUtil.randomString();

			_virtualHostLocalService.updateVirtualHosts(
				company.getCompanyId(), 0,
				TreeMapBuilder.putAll(
					hostnames
				).put(
					virtualHostname, StringPool.BLANK
				).build());

			Assert.assertEquals(
				virtualHostname,
				portal.getDefaultVirtualHostname(
					true,
					_layoutSetLocalService.getLayoutSet(
						layoutSet.getLayoutSetId())));
		}
		finally {
			_virtualHostLocalService.updateVirtualHosts(
				company.getCompanyId(), 0, hostnames);
		}
	}

	private void _testGetDefaultVirtualHostname(
			boolean companyFallback, String expectedDefaultVirtualHostname)
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		_virtualHostLocalService.updateVirtualHosts(
			company.getCompanyId(), layoutSet.getLayoutSetId(),
			new TreeMap<>());

		Assert.assertEquals(
			expectedDefaultVirtualHostname,
			portal.getDefaultVirtualHostname(
				companyFallback,
				_layoutSetLocalService.getLayoutSet(
					layoutSet.getLayoutSetId())));

		String defaultVirtualHostname = "z-" + RandomTestUtil.randomString();

		_virtualHostLocalService.updateVirtualHosts(
			company.getCompanyId(), layoutSet.getLayoutSetId(),
			TreeMapBuilder.put(
				"a-" + RandomTestUtil.randomString(),
				LocaleUtil.toLanguageId(LocaleUtil.US)
			).put(
				defaultVirtualHostname, StringPool.BLANK
			).build());

		Assert.assertEquals(
			defaultVirtualHostname,
			portal.getDefaultVirtualHostname(
				false,
				_layoutSetLocalService.getLayoutSet(
					layoutSet.getLayoutSetId())));

		_virtualHostLocalService.updateVirtualHosts(
			company.getCompanyId(), layoutSet.getLayoutSetId(),
			TreeMapBuilder.put(
				RandomTestUtil.randomString(),
				LocaleUtil.toLanguageId(LocaleUtil.US)
			).build());

		Assert.assertEquals(
			expectedDefaultVirtualHostname,
			portal.getDefaultVirtualHostname(
				companyFallback,
				_layoutSetLocalService.getLayoutSet(
					layoutSet.getLayoutSetId())));
	}

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private VirtualHostLocalService _virtualHostLocalService;

}