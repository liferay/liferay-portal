/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.credential;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.opensaml.integration.internal.BaseSamlTestCase;
import com.liferay.saml.opensaml.integration.internal.util.KeyStoreUtil;

import java.security.KeyStore;
import java.security.PrivateKey;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;

/**
 * @author Mika Koivisto
 */
public class CredentialResolverTest extends BaseSamlTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testResolveIdpCredential() throws Exception {
		prepareIdentityProvider(IDP_ENTITY_ID);
		_testResolveCredential(IDP_ENTITY_ID);
	}

	@Test
	public void testResolveNonexistingCredential() throws Exception {
		EntityIdCriterion entityIDCriterion = new EntityIdCriterion("na");

		CriteriaSet criteriaSet = new CriteriaSet();

		criteriaSet.add(entityIDCriterion);

		Credential credential = credentialResolver.resolveSingle(criteriaSet);

		Assert.assertNull(credential);
	}

	@Test
	public void testResolveSpCredential() throws Exception {
		prepareServiceProvider(SP_ENTITY_ID);
		_testResolveCredential(SP_ENTITY_ID);
	}

	@Test
	public void testResolveSpEncryptionCredential() throws Exception {
		prepareServiceProvider(SP_ENTITY_ID);

		KeyStore keyStore = fileSystemKeyStoreManagerImpl.getKeyStore();

		String keyStoreCredentialPassword =
			samlProviderConfiguration.keyStoreCredentialPassword();

		KeyStore.PrivateKeyEntry privateKeyEntry =
			(KeyStore.PrivateKeyEntry)keyStore.getEntry(
				SP_ENTITY_ID,
				new KeyStore.PasswordProtection(
					keyStoreCredentialPassword.toCharArray()));

		String keyStoreEncryptionCredentialPassword =
			RandomTestUtil.randomString();

		keyStore.setEntry(
			KeyStoreUtil.getAlias(SP_ENTITY_ID, UsageType.ENCRYPTION),
			privateKeyEntry,
			new KeyStore.PasswordProtection(
				keyStoreEncryptionCredentialPassword.toCharArray()));

		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));

		Mockito.when(
			secretResolver.resolve(COMPANY_ID, keyReferenceString)
		).thenReturn(
			keyStoreEncryptionCredentialPassword
		);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(COMPANY_ID)) {

			_testResolveSpEncryptionCredential(
				keyReferenceString, privateKeyEntry.getPrivateKey());
			_testResolveSpEncryptionCredential(
				keyStoreEncryptionCredentialPassword,
				privateKeyEntry.getPrivateKey());
		}
	}

	private void _testResolveCredential(String spEntityId) throws Exception {
		EntityIdCriterion entityIDCriterion = new EntityIdCriterion(spEntityId);

		CriteriaSet criteriaSet = new CriteriaSet();

		criteriaSet.add(entityIDCriterion);

		Credential credential = credentialResolver.resolveSingle(criteriaSet);

		Assert.assertNotNull(credential);
	}

	private void _testResolveSpEncryptionCredential(
			String keyStoreEncryptionCredentialPassword, PrivateKey privateKey)
		throws Exception {

		Mockito.when(
			samlProviderConfiguration.keyStoreEncryptionCredentialPassword()
		).thenReturn(
			keyStoreEncryptionCredentialPassword
		);

		Credential credential = credentialResolver.resolveSingle(
			new CriteriaSet(
				new EntityIdCriterion(SP_ENTITY_ID),
				new UsageCriterion(UsageType.ENCRYPTION)));

		Assert.assertEquals(privateKey, credential.getPrivateKey());
		Assert.assertEquals(UsageType.ENCRYPTION, credential.getUsageType());
	}

}