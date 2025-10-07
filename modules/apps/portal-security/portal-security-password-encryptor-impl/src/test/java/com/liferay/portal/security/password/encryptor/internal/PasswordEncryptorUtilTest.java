/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;

/**
 * @author Tomas Polesovsky
 */
public class PasswordEncryptorUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		bundleContext.registerService(
			PasswordEncryptor.class, new DefaultPasswordEncryptor(),
			MapUtil.singletonDictionary(
				"type", PasswordEncryptor.TYPE_DEFAULT));
		bundleContext.registerService(
			PasswordEncryptor.class, new BCryptPasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_BCRYPT));
		bundleContext.registerService(
			PasswordEncryptor.class, new CryptPasswordEncryptor(),
			MapUtil.singletonDictionary(
				"type", PasswordEncryptor.TYPE_UFC_CRYPT));
		bundleContext.registerService(
			PasswordEncryptor.class, new NullPasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_NONE));
		bundleContext.registerService(
			PasswordEncryptor.class, new PBKDF2PasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_PBKDF2));
		bundleContext.registerService(
			PasswordEncryptor.class, new SSHAPasswordEncryptor(),
			MapUtil.singletonDictionary("type", PasswordEncryptor.TYPE_SSHA));
		bundleContext.registerService(
			PasswordEncryptor.class, new TestCustomPasswordEncryptor(),
			MapUtil.singletonDictionary(
				"type", _TYPE_CUSTOM_PASSWORD_ENCRYPTOR));
	}

	@Test
	public void testCustomPasswordEncryptorWithParameters() throws Exception {
		_runTests(
			_TYPE_CUSTOM_PASSWORD_ENCRYPTOR + "/ARGUMENT", "password",
			"password:ARGUMENT", _TYPE_CUSTOM_PASSWORD_ENCRYPTOR);
	}

	@Test
	public void testEncryptBCrypt() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_BCRYPT, "password",
			"$2a$10$/ST7LsB.7AAHsn/tlK6hr.nudQaBbJhPX9KfRSSzsn.1ij45lVzaK",
			PasswordEncryptor.TYPE_BCRYPT);
	}

	@Test
	public void testEncryptBCryptWith10Rounds() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_BCRYPT + "/10", "password",
			"$2a$10$JX0uYSs6pSrp05TlQxkmz.hkKGK6Av.KkNCzAYOFugO3qxjAiZleO",
			PasswordEncryptor.TYPE_BCRYPT);
	}

	@Test
	public void testEncryptBCryptWith12Rounds() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_BCRYPT + "/12", "password",
			"$2a$12$2dD/NrqCEBlVgFEkkFCbzOll2a9vrdl8tTTqGosm26wJK1eCtsjnO",
			PasswordEncryptor.TYPE_BCRYPT);
	}

	@Test
	public void testEncryptCRYPT() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_UFC_CRYPT, "password", "SNbUMVY9kKQpY",
			PasswordEncryptor.TYPE_UFC_CRYPT);
	}

	@Test
	public void testEncryptFailure() throws Exception {
		_testEncryptFailure(
			"Some Nonexistent Algorithm", StringPool.BLANK, StringPool.BLANK);

		_testEncryptFailure(null, null, null);

		_testEncryptFailure(null, null, StringPool.BLANK);

		_testEncryptFailure(null, StringPool.BLANK, null);

		_testEncryptFailure(StringPool.BLANK, null, null);

		_testEncryptFailure(StringPool.BLANK, null, StringPool.BLANK);

		_testEncryptFailure(StringPool.BLANK, StringPool.BLANK, null);

		_testEncryptFailure(null, StringPool.BLANK, StringPool.BLANK);

		_testEncryptFailure(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK);

		_testEncryptFailure(
			PasswordEncryptor.TYPE_SHA, "password",
			"W6ph5Mm5Pz8GgiULbPgzG37mj9g=");
	}

	@Test
	public void testEncryptMD2() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_MD2, "password", "8DiBqIxuORNfDsxg79YJuQ==",
			PasswordEncryptor.TYPE_MD2);
	}

	@Test
	public void testEncryptMD5() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_MD5, "password", "X03MO1qnZdYdgyfeuILPmQ==",
			PasswordEncryptor.TYPE_MD5);
	}

	@Test
	public void testEncryptNONE() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_NONE, "password", "password",
			PasswordEncryptor.TYPE_NONE);
	}

	@Test
	public void testEncryptPBKDF2() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1", "password",
			"AAAAoAAB9ADJZ16OuMAPPHe2CUbP0HPyXvagoKHumh7iHU3c",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With8ByteSalt() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1", "password",
			"AAAAoAAK/IBDrUHgboU2XfC7pqk97rPAQEuRTknBTxehNard",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With16ByteSalt() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1", "password",
			"AAAAoAAK/IAwGhXn0y8iEgAAAAAAAAAA4zLIf9Yqr/EvCcKm3UJw4gc2KBQ=",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With50000Rounds() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/50000", "password",
			"AAAAoAAAw1B+jxO3UiVsWdBk4B9xGd/Ko3GKHW2afYhuit49",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With50000RoundsAnd128Key() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/128/50000",
			"password", "AAAAoAAAw1AbW1e1Str9wSLWIX5X9swLn+j5/5+m6auSPdva",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With720000RoundsAnd128Key() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/128/720000",
			"password", "AAAAoAAB9ADyaBP3fTtsBh8YlRn1CU7VLYR/mnH7ADMNMz2o",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2With1300000RoundsAnd128Key() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1/128/1300000",
			"password", "AAAAoAAK/IAeIrZ3b+oVv6of6xGPeOIsIhRyCH+h/VX2qMgK",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA1");
	}

	@Test
	public void testEncryptPBKDF2WithHmacSHA256() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA256", "password",
			"AAAAoAAB9ADJZ16OuMAPPFrcmsgO+rKUjwKOeJbdcZ6PKNxE",
			PasswordEncryptor.TYPE_PBKDF2 + "WithHmacSHA256");
	}

	@Test
	public void testEncryptSHA() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_SHA, "password",
			"W6ph5Mm5Pz8GgiULbPgzG37mj9g=", PasswordEncryptor.TYPE_SHA);
	}

	@Test
	public void testEncryptSHA1() throws Exception {
		_runTests("SHA-1", "password", "W6ph5Mm5Pz8GgiULbPgzG37mj9g=", "SHA-1");
	}

	@Test
	public void testEncryptSHA256() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_SHA_256, "password",
			"XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=",
			PasswordEncryptor.TYPE_SHA_256);
	}

	@Test
	public void testEncryptSHA384() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_SHA_384, "password",
			"qLZLq9CsqRpZvbt3YbQh1PK7OCgNOnW6DyHyvrxFWD1EbFmGYMlM5oDEfRnDB4On",
			PasswordEncryptor.TYPE_SHA_384);
	}

	@Test
	public void testEncryptSSHA() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_SSHA, "password",
			"2EWEKeVpSdd79PkTX5vaGXH5uQ028Smy/H1NmA==",
			PasswordEncryptor.TYPE_SSHA);
	}

	@Test
	public void testEncryptUFCCRYPT() throws Exception {
		_runTests(
			PasswordEncryptor.TYPE_UFC_CRYPT, "password", "2lrTlR/pWPUOQ",
			PasswordEncryptor.TYPE_UFC_CRYPT);
	}

	@Test(expected = PwdEncryptorException.MustSetLegacyAlgorithmProperty.class)
	public void testEncryptWithLegacyAlgorithm() throws Exception {
		_testEncryptWithLegacyAlgorithm(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private void _runTests(
			String algorithm, String plainPassword, String encryptedPassword,
			String prependedAlgorithm)
		throws Exception {

		_testEncrypt(algorithm);

		_testEncrypt(
			plainPassword,
			StringBundler.concat(
				CharPool.OPEN_CURLY_BRACE, prependedAlgorithm,
				CharPool.CLOSE_CURLY_BRACE, encryptedPassword));

		_testEncryptWithLegacyAlgorithm(
			algorithm, plainPassword, encryptedPassword);
	}

	private void _testEncrypt(String algorithm) throws Exception {
		String plainPassword = "password";

		String expectedPassword = PasswordEncryptorUtil.encrypt(
			algorithm, plainPassword, null);

		_testEncrypt(plainPassword, expectedPassword);
	}

	private void _testEncrypt(String plainPassword, String expectedPassword)
		throws Exception {

		Assert.assertEquals(
			expectedPassword,
			PasswordEncryptorUtil.encrypt(plainPassword, expectedPassword));
	}

	private void _testEncryptFailure(
		String algorithm, String plainTextPassword, String encryptedPassword) {

		try {
			PasswordEncryptorUtil.encrypt(
				algorithm, plainTextPassword, encryptedPassword);

			Assert.fail();
		}
		catch (Exception exception) {
		}
	}

	private void _testEncryptWithLegacyAlgorithm(
			String legacyAlgorithm, String plainPassword,
			String expectedPassword)
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY", legacyAlgorithm)) {

			Assert.assertEquals(
				expectedPassword,
				PasswordEncryptorUtil.encrypt(plainPassword, expectedPassword));
		}
	}

	private static final String _TYPE_CUSTOM_PASSWORD_ENCRYPTOR =
		"CUSTOM_PASSWORD_ENCRYPTOR";

	private static class TestCustomPasswordEncryptor
		implements PasswordEncryptor {

		@Override
		public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword, boolean upgradeHashSecurity) {

			if (encryptedPassword != null) {
				return plainTextPassword +
					encryptedPassword.substring(encryptedPassword.indexOf(':'));
			}

			return plainTextPassword + ':' +
				algorithm.substring(algorithm.indexOf('/') + 1);
		}

	}

}