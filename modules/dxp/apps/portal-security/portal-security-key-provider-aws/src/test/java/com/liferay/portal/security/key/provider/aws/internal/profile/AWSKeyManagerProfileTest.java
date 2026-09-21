/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.profile;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.provider.aws.internal.configuration.AWSKMSSystemCryptoProviderConfiguration;
import com.liferay.portal.security.key.provider.aws.internal.configuration.AWSSecretsManagerSystemSecretProviderConfiguration;
import com.liferay.portal.security.key.provider.aws.internal.profile.configuration.AWSKeyManagerProfileConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Christopher Kian
 */
public class AWSKeyManagerProfileTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_awsKeyManagerProfile, "_awsKeyManagerProfileConfiguration",
			_awsKeyManagerProfileConfiguration);
		ReflectionTestUtil.setFieldValue(
			_awsKeyManagerProfile, "_configurationAdmin", _configurationAdmin);
	}

	@Test
	public void testCryptoProviderIds() {
		Assert.assertEquals(
			"aws-company-crypto",
			_awsKeyManagerProfile.getCompanyDEKProviderId());
		Assert.assertEquals(
			"aws-company-crypto",
			_awsKeyManagerProfile.getCompanyKEKProviderId());
		Assert.assertEquals(
			"aws-system-crypto",
			_awsKeyManagerProfile.getSystemDEKProviderId());
		Assert.assertEquals(
			"aws-system-crypto",
			_awsKeyManagerProfile.getSystemKEKProviderId());
	}

	@Test
	public void testDBSecretLayerRoutesToDBProviders() {
		Mockito.when(
			_awsKeyManagerProfileConfiguration.vaultLayer()
		).thenReturn(
			"db"
		);

		Assert.assertEquals(
			"db-company-secret",
			_awsKeyManagerProfile.getCompanySecretProviderId());
		Assert.assertEquals(
			"db-system-secret",
			_awsKeyManagerProfile.getSystemSecretProviderId());
	}

	@Test
	public void testDefaultSecretLayerRoutesToAWSProviders() {
		Mockito.when(
			_awsKeyManagerProfileConfiguration.vaultLayer()
		).thenReturn(
			"aws"
		);

		Assert.assertEquals(
			"aws-company-secret",
			_awsKeyManagerProfile.getCompanySecretProviderId());
		Assert.assertEquals(
			"aws-system-secret",
			_awsKeyManagerProfile.getSystemSecretProviderId());
	}

	@Test
	public void testGetProfileId() {
		Assert.assertEquals("aws", _awsKeyManagerProfile.getProfileId());
	}

	@Test
	public void testInitializeDisablesSecretProvidersForDBLayer()
		throws Exception {

		Map<String, Dictionary<String, Object>> capturedProperties =
			_mockConfigurationAdmin();

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsAccountId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsRegion()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.vaultLayer()
		).thenReturn(
			"db"
		);

		_awsKeyManagerProfile.initialize();

		Dictionary<String, Object> cryptoProperties = capturedProperties.get(
			AWSKMSSystemCryptoProviderConfiguration.class.getName());

		Assert.assertEquals(Boolean.TRUE, cryptoProperties.get("enabled"));

		Dictionary<String, Object> secretProperties = capturedProperties.get(
			AWSSecretsManagerSystemSecretProviderConfiguration.class.getName());

		Assert.assertEquals(Boolean.FALSE, secretProperties.get("enabled"));
	}

	@Test
	public void testInitializeEnablesAllProviders() throws Exception {
		Map<String, Dictionary<String, Object>> capturedProperties =
			_mockConfigurationAdmin();

		String awsAccountId = RandomTestUtil.randomString();

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsAccountId()
		).thenReturn(
			awsAccountId
		);

		String awsRegion = RandomTestUtil.randomString();

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsRegion()
		).thenReturn(
			awsRegion
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.vaultLayer()
		).thenReturn(
			"aws"
		);

		_awsKeyManagerProfile.initialize();

		Dictionary<String, Object> cryptoProperties = capturedProperties.get(
			AWSKMSSystemCryptoProviderConfiguration.class.getName());

		Assert.assertEquals(awsAccountId, cryptoProperties.get("awsAccountId"));
		Assert.assertEquals(awsRegion, cryptoProperties.get("awsRegion"));
		Assert.assertEquals(Boolean.TRUE, cryptoProperties.get("enabled"));

		Dictionary<String, Object> secretProperties = capturedProperties.get(
			AWSSecretsManagerSystemSecretProviderConfiguration.class.getName());

		Assert.assertEquals(Boolean.TRUE, secretProperties.get("enabled"));
	}

	@Test
	public void testInitializeStrictModeEnforcesFIPS() throws Exception {
		Map<String, Dictionary<String, Object>> capturedProperties =
			_mockConfigurationAdmin();

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsAccountId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsRegion()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.keyARNTemplate()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.strictMode()
		).thenReturn(
			true
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.vaultLayer()
		).thenReturn(
			"aws"
		);

		_keyOrigin = "AWS_KMS";

		_awsKeyManagerProfile.initialize();

		Dictionary<String, Object> cryptoProperties = capturedProperties.get(
			AWSKMSSystemCryptoProviderConfiguration.class.getName());

		Assert.assertEquals(Boolean.TRUE, cryptoProperties.get("fipsEnforced"));
		Assert.assertEquals(
			Boolean.TRUE, cryptoProperties.get("useFIPSEndpoint"));
	}

	@Test(expected = CryptoException.class)
	public void testInitializeStrictModeRejectsUnapprovedKeyOrigin()
		throws Exception {

		_mockConfigurationAdmin();

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsAccountId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.awsRegion()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.keyARNTemplate()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_awsKeyManagerProfileConfiguration.strictMode()
		).thenReturn(
			true
		);

		_keyOrigin = "EXTERNAL";

		_awsKeyManagerProfile.initialize();
	}

	private Map<String, Dictionary<String, Object>> _mockConfigurationAdmin()
		throws Exception {

		Map<String, Dictionary<String, Object>> capturedProperties =
			new HashMap<>();

		Mockito.when(
			_configurationAdmin.getConfiguration(
				Mockito.anyString(), Mockito.eq("?"))
		).thenAnswer(
			invocation -> {
				String pid = invocation.getArgument(0);

				Configuration configuration = Mockito.mock(Configuration.class);

				Mockito.doAnswer(
					updateInvocation -> {
						capturedProperties.put(
							pid, updateInvocation.getArgument(0));

						return null;
					}
				).when(
					configuration
				).update(
					Mockito.any(Dictionary.class)
				);

				return configuration;
			}
		);

		return capturedProperties;
	}

	private final AWSKeyManagerProfile _awsKeyManagerProfile =
		new AWSKeyManagerProfile() {

			@Override
			protected String getKeyOrigin(
				String awsRegion, String keyARN, boolean useFIPSEndpoint) {

				return _keyOrigin;
			}

			@Override
			protected void validateCredentials() {
			}

		};

	private final AWSKeyManagerProfileConfiguration
		_awsKeyManagerProfileConfiguration = Mockito.mock(
			AWSKeyManagerProfileConfiguration.class);
	private final ConfigurationAdmin _configurationAdmin = Mockito.mock(
		ConfigurationAdmin.class);
	private String _keyOrigin = "AWS_KMS";

}