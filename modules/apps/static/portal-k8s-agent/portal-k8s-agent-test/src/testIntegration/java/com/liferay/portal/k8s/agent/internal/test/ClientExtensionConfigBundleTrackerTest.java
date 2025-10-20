/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ZipFileTestUtil;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Gregory Amerson
 */
@RunWith(Arquillian.class)
public class ClientExtensionConfigBundleTrackerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_bundle = FrameworkUtil.getBundle(
			ClientExtensionConfigBundleTrackerTest.class);

		_bundleContext = _bundle.getBundleContext();
	}

	@Test
	public void testClientExtensionConfigBundleTracker() throws Exception {
		Bundle bundle = _installTestBundle("bundle1");

		try {
			bundle.start();

			_assertConfigurationPids(
				bundle.getBundleId(), 3,
				Arrays.asList(
					_FACTORY_PID + "~able/liferay.com",
					_FACTORY_PID + "~baker/liferay.com",
					_FACTORY_PID + "~charlie/liferay.com"));

			bundle.stop();

			_assertConfigurationPids(
				bundle.getBundleId(), 0, Collections.emptyList());
		}
		finally {
			bundle.uninstall();
		}
	}

	@Test
	public void testClientExtensionConfigBundleTrackerVirtualInstance()
		throws Exception {

		String webId = "able.liferay.com";

		Bundle bundle = null;
		Company company = null;

		try {
			company = _companyLocalService.addCompany(
				null, webId, webId, webId, 0, true, true, null, null, null,
				null, null, null);

			Assert.assertNotNull(company);

			bundle = _installTestBundle("bundle2");

			bundle.start();

			List<String> pids = Arrays.asList(
				_FACTORY_PID + "~able/able.liferay.com",
				_FACTORY_PID + "~baker/liferay.com");

			_assertConfigurationPids(bundle.getBundleId(), 2, pids);

			_assertVirtualInstancePids(pids);

			bundle.stop();

			_assertConfigurationPids(
				bundle.getBundleId(), 0, Collections.emptyList());
		}
		finally {
			if (bundle != null) {
				bundle.uninstall();
			}

			if (company != null) {
				_companyLocalService.deleteCompany(company);
			}
		}
	}

	private void _assertConfigurationPids(
			long bundleId, int expectedCount, List<String> expectedPids)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat("(.cx.config.bundle.id=", bundleId, ")"));

		if (expectedCount == 0) {
			Assert.assertNull(configurations);

			return;
		}

		Assert.assertNotNull(configurations);
		Assert.assertEquals(
			Arrays.toString(configurations), expectedPids.size(),
			configurations.length);

		Arrays.sort(
			configurations,
			(configuration1, configuration2) -> {
				String pid1 = configuration1.getPid();

				return pid1.compareTo(configuration2.getPid());
			});

		for (int i = 0; i < expectedPids.size(); i++) {
			Assert.assertEquals(
				expectedPids.get(i), configurations[i].getPid());
		}
	}

	private void _assertVirtualInstancePids(List<String> virtualInstanceIds)
		throws Exception {

		for (String virtualInstanceId : virtualInstanceIds) {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(.cx.config.key=", virtualInstanceId, ")"));

			Assert.assertNotNull(configurations);
			Assert.assertEquals(
				Arrays.toString(configurations), 1, configurations.length);
		}
	}

	private Bundle _installTestBundle(String dirName) throws Exception {
		return _bundleContext.installBundle(
			RandomTestUtil.randomString(),
			ZipFileTestUtil.toInputStream(
				StringBundler.concat(
					"com/liferay/portal/k8s/agent/internal/test/dependencies/",
					dirName, StringPool.SLASH),
				_bundle, _zipWriterFactory.getZipWriter()));
	}

	private static final String _FACTORY_PID =
		"com.liferay.client.extension.type.configuration.CETConfiguration";

	private Bundle _bundle;
	private BundleContext _bundleContext;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}