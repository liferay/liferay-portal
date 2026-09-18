/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.gcs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adam Brandizzi
 */
public class GCSStoreTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_gcsStore = new GCSStore();

		ReflectionTestUtil.setFieldValue(
			_gcsStore, "_secretResolver",
			(SecretResolver)(companyId, value) -> value);

		Mockito.when(
			ConfigurableUtil.createConfigurable(
				Mockito.eq(GCSStoreConfiguration.class), Mockito.any(Map.class))
		).thenReturn(
			_gcsStoreConfiguration
		);

		Mockito.when(
			_gcsStoreConfiguration.retryDelayMultiplier()
		).thenReturn(
			1.5
		);

		Mockito.when(
			_gcsStoreConfiguration.rpcTimeoutMultiplier()
		).thenReturn(
			1.0
		);
	}

	@After
	public void tearDown() {
		_configurableUtilMockedStatic.close();
	}

	@Test
	public void testActivate1() throws Exception {
		byte[] serviceAccountKeyBytes = FileUtil.getBytes(
			getClass(), "dependencies/service-account-key.json");

		_mockServiceAccountKey(new String(serviceAccountKeyBytes));

		_gcsStore.activate(
			SystemBundleUtil.getBundleContext(), Collections.emptyMap());

		_assertGoogleCredentials(
			ServiceAccountCredentials.fromStream(
				new ByteArrayInputStream(serviceAccountKeyBytes)));
	}

	@Test
	public void testActivate2() throws Exception {
		_mockServiceAccountKey(null);

		_gcsStore.activate(
			SystemBundleUtil.getBundleContext(), Collections.emptyMap());

		_assertGoogleCredentials(
			ServiceAccountCredentials.getApplicationDefault());
	}

	private void _assertGoogleCredentials(GoogleCredentials googleCredentials) {
		Assert.assertEquals(
			ReflectionTestUtil.getFieldValue(_gcsStore, "_googleCredentials"),
			googleCredentials);
	}

	private void _mockServiceAccountKey(String serviceAccountKey) {
		Mockito.when(
			_gcsStoreConfiguration.serviceAccountKey()
		).thenReturn(
			serviceAccountKey
		);
	}

	private final MockedStatic<ConfigurableUtil> _configurableUtilMockedStatic =
		Mockito.mockStatic(ConfigurableUtil.class);
	private GCSStore _gcsStore;

	@Mock
	private GCSStoreConfiguration _gcsStoreConfiguration;

}