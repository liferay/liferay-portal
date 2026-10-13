/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.File;

import java.net.URL;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Kevin Lee
 */
@RunWith(Arquillian.class)
public class DXPFreeTierCKEditorLicenseTest extends BaseLicenseTestCase {

	public static void assume() {
		Assume.assumeTrue(isReleaseBundle() && !isCMS());
	}

	@BeforeClass
	public static void setUpClass() {
		_disableKeyValidatorSafeCloseable = disableValidateWithSafeCloseable();
		_setVersionSafeCloseable = setVersionWithSafeCloseable("2026.Q1.0 LTS");
	}

	@AfterClass
	public static void tearDownClass() {
		_disableKeyValidatorSafeCloseable.close();
		_setVersionSafeCloseable.close();
	}

	@Before
	public void setUp() throws Exception {
		_safeCloseable = resetLicenseDataWithSafeCloseble();
	}

	@After
	public void tearDown() throws Exception {
		_safeCloseable.close();

		ConfigurationTestUtil.deleteConfiguration(_CKEDITOR_CONFIG_ID);

		if (_CKEDITOR_CONFIG_FILE.exists()) {
			_CKEDITOR_CONFIG_FILE.delete();
		}
	}

	@Test
	public void testCustomKey() throws Exception {
		_testLicenseKey(RandomTestUtil.randomString(), false);
	}

	@Test
	public void testCustomKeyWithConfigFile() throws Exception {
		_testLicenseKey(RandomTestUtil.randomString(), true);
	}

	@Test
	public void testPrivateKey() throws Exception {
		_testLicenseKey(_getCKEditorPrivateLicenseKey(), false);
	}

	@Test
	public void testPrivateKeyWithConfigFile() throws Exception {
		_testLicenseKey(_getCKEditorPrivateLicenseKey(), true);
	}

	@Test
	public void testPublicKey() throws Exception {
		_testLicenseKey(_getCKEditorPublicLicenseKey(), false);
	}

	private void _assertCKEditorConfiguration(
			String licenseKey, boolean fileExisted)
		throws Exception {

		if (licenseKey != null) {
			Configuration configuration = _configurationAdmin.getConfiguration(
				_CKEDITOR_CONFIG_ID);

			Dictionary<String, Object> properties =
				configuration.getProperties();

			Assert.assertEquals(licenseKey, properties.get("licenseKey"));
		}
		else {
			Assert.assertNull(
				_configurationAdmin.listConfigurations(
					"(service.pid=" + _CKEDITOR_CONFIG_ID + ")"));
		}

		Assert.assertSame(fileExisted, _CKEDITOR_CONFIG_FILE.exists());

		if (fileExisted) {
			String content = FileUtil.read(_CKEDITOR_CONFIG_FILE);

			Assert.assertTrue(content, content.contains(licenseKey));
		}
	}

	private String _getCKEditorPrivateLicenseKey() {
		return getProperty("ckeditor.private.license.key");
	}

	private String _getCKEditorPublicLicenseKey() throws Exception {
		Bundle bundle = BundleUtil.getBundle(
			SystemBundleUtil.getBundleContext(), _CK_EDITOR_BUNDLE_NAME);

		Enumeration<URL> enumeration = bundle.findEntries(
			"/OSGI-INF/metatype", _CKEDITOR_CONFIG_ID + ".xml", false);

		if ((enumeration == null) || !enumeration.hasMoreElements()) {
			throw new IllegalStateException("Missing CKEditor configuration");
		}

		List<URL> list = Collections.list(enumeration);

		String content = new String(URLUtil.toByteArray(list.get(0)));

		int start = content.indexOf("default=\"");

		return content.substring(start + 9, content.indexOf("\"", start + 9));
	}

	private void _testLicenseKey(String licenseKey, boolean useConfigFile)
		throws Exception {

		assertLicensePropertiesNotExisted(getPortalProductId());

		_assertCKEditorConfiguration(null, false);

		_updateConfiguration(licenseKey, useConfigFile);

		_assertCKEditorConfiguration(licenseKey, useConfigFile);

		File binaryFile = deployFreeTierPortalLicense(Time.HOUR);

		assertLicensePropertiesExisted(getPortalProductId());

		if (licenseKey.equals(_getCKEditorPrivateLicenseKey())) {
			ConfigurationTestUtil.updateConfiguration(
				_CKEDITOR_CONFIG_ID, this::assertPortalLicenseRegistered);

			_assertCKEditorConfiguration(null, false);

			_updateConfiguration(licenseKey, useConfigFile);

			_assertCKEditorConfiguration(licenseKey, useConfigFile);

			resetCheckInterval();

			ConfigurationTestUtil.updateConfiguration(
				_CKEDITOR_CONFIG_ID, this::assertPortalLicenseRegistered);

			_assertCKEditorConfiguration(null, false);
		}
		else {
			assertPortalLicenseRegistered();

			_assertCKEditorConfiguration(licenseKey, useConfigFile);
		}

		binaryFile.delete();

		checkLicense(getPortalProductId());

		assertLicensePropertiesNotExisted(getPortalProductId());

		resetLifecycleAction();

		assertPortalLicenseNotRegistered();

		deployEnterprisePortalLicense(Time.HOUR);

		assertLicensePropertiesExisted(getPortalProductId());

		if (licenseKey.equals(_getCKEditorPrivateLicenseKey())) {
			_assertCKEditorConfiguration(null, false);

			ConfigurationTestUtil.updateConfiguration(
				_CKEDITOR_CONFIG_ID, this::assertPortalLicenseRegistered);

			_assertCKEditorConfiguration(_getCKEditorPrivateLicenseKey(), true);
		}
		else if (licenseKey.equals(_getCKEditorPublicLicenseKey())) {
			_assertCKEditorConfiguration(licenseKey, useConfigFile);

			ConfigurationTestUtil.updateConfiguration(
				_CKEDITOR_CONFIG_ID, this::assertPortalLicenseRegistered);

			_assertCKEditorConfiguration(_getCKEditorPrivateLicenseKey(), true);
		}
		else {
			_assertCKEditorConfiguration(licenseKey, useConfigFile);

			assertPortalLicenseRegistered();

			_assertCKEditorConfiguration(licenseKey, useConfigFile);
		}
	}

	private void _updateConfiguration(String licenseKey, boolean useConfigFile)
		throws Exception {

		if (useConfigFile) {
			ConfigurationTestUtil.saveConfiguration(
				_CKEDITOR_CONFIG_ID,
				HashMapDictionaryBuilder.<String, Object>put(
					"felix.fileinstall.filename",
					_CKEDITOR_CONFIG_ID + ".config"
				).put(
					"licenseKey", licenseKey
				).build());

			FileUtil.write(
				_CKEDITOR_CONFIG_FILE, "licenseKey=\"" + licenseKey + "\"");
		}
		else {
			ConfigurationTestUtil.saveConfiguration(
				_CKEDITOR_CONFIG_ID,
				HashMapDictionaryBuilder.<String, Object>put(
					"licenseKey", licenseKey
				).build());
		}
	}

	private static final String _CK_EDITOR_BUNDLE_NAME =
		"com.liferay.frontend.editor.ckeditor.web";

	private static final File _CKEDITOR_CONFIG_FILE = new File(
		PropsValues.MODULE_FRAMEWORK_CONFIGS_DIR,
		DXPFreeTierCKEditorLicenseTest._CKEDITOR_CONFIG_ID + ".config");

	private static final String _CKEDITOR_CONFIG_ID =
		"com.liferay.frontend.editor.ckeditor.web.internal.configuration." +
			"CKEditor5Configuration";

	private static SafeCloseable _disableKeyValidatorSafeCloseable;
	private static SafeCloseable _setVersionSafeCloseable;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private SafeCloseable _safeCloseable;

}