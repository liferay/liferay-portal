/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.encryptor;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.CompanyKeyException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.security.Key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Christopher Kian
 */
public class CompanyKeyResolverUtilTest {

	@Test
	public void testGenerateKey() throws Exception {
		Encryptor encryptor = Mockito.mock(Encryptor.class);
		Key key = Mockito.mock(Key.class);

		Mockito.when(
			encryptor.generateKey()
		).thenReturn(
			key
		);

		String keyString = RandomTestUtil.randomString();

		Mockito.when(
			encryptor.serializeKey(key)
		).thenReturn(
			keyString
		);

		try (AutoCloseable autoCloseable1 = _setEncryptor(encryptor);
			AutoCloseable autoCloseable2 = _setCompanyKeyResolver(null)) {

			Assert.assertEquals(
				keyString, CompanyKeyResolverUtil.generateKey(_COMPANY_ID));

			try (SafeCloseable safeCloseable =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"FIPS_ENABLED", true)) {

				String fipsKeyString = CompanyKeyResolverUtil.generateKey(
					_COMPANY_ID);

				Assert.assertEquals(
					_FIPS_KEY_PREFIX + keyString + "}", fipsKeyString);
				Assert.assertTrue(
					CompanyKeyResolverUtil.isFIPSKey(fipsKeyString));
				Assert.assertFalse(
					CompanyKeyResolverUtil.isWrappedKey(fipsKeyString));
			}
		}
	}

	@Test
	public void testIsFIPSKey() {
		Assert.assertFalse(
			CompanyKeyResolverUtil.isFIPSKey(RandomTestUtil.randomString()));
		Assert.assertFalse(
			CompanyKeyResolverUtil.isFIPSKey(
				_FIPS_KEY_PREFIX + RandomTestUtil.randomString()));
		Assert.assertTrue(
			CompanyKeyResolverUtil.isFIPSKey(
				_FIPS_KEY_PREFIX + RandomTestUtil.randomString() + "}"));
		Assert.assertFalse(
			CompanyKeyResolverUtil.isFIPSKey(
				_WRAPPED_KEY_PREFIX + RandomTestUtil.randomString()));
		Assert.assertFalse(CompanyKeyResolverUtil.isFIPSKey(null));
	}

	@Test
	public void testIsWrappedKey() {
		Assert.assertFalse(
			CompanyKeyResolverUtil.isWrappedKey(RandomTestUtil.randomString()));
		Assert.assertFalse(
			CompanyKeyResolverUtil.isWrappedKey(
				_FIPS_KEY_PREFIX + RandomTestUtil.randomString() + "}"));
		Assert.assertTrue(
			CompanyKeyResolverUtil.isWrappedKey(
				_FIPS_KEY_PREFIX + _WRAPPED_KEY_PREFIX + "}"));
		Assert.assertTrue(
			CompanyKeyResolverUtil.isWrappedKey(_WRAPPED_KEY_PREFIX));
		Assert.assertFalse(CompanyKeyResolverUtil.isWrappedKey(null));
	}

	@Test
	public void testRotateKey() throws Exception {
		Encryptor encryptor = Mockito.mock(Encryptor.class);
		Key key = Mockito.mock(Key.class);

		Mockito.when(
			encryptor.generateKey()
		).thenReturn(
			key
		);

		String keyString = RandomTestUtil.randomString();

		Mockito.when(
			encryptor.serializeKey(key)
		).thenReturn(
			keyString
		);

		try (AutoCloseable autoCloseable1 = _setEncryptor(encryptor);
			AutoCloseable autoCloseable2 = _setCompanyKeyResolver(null);
			MockedStatic<FIPSApplicationStateMachineUtil>
				fipsApplicationStateMachineUtilMockedStatic =
					Mockito.mockStatic(FIPSApplicationStateMachineUtil.class);
			SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			List<String> keyStrings = new ArrayList<>();

			LogEntry logEntry = _rotateKey(
				RandomTestUtil.randomString(), keyStrings::add,
				RandomTestUtil.randomString());

			Assert.assertEquals(
				Collections.singletonList(_FIPS_KEY_PREFIX + keyString + "}"),
				keyStrings);

			Assert.assertEquals(LoggerTestUtil.INFO, logEntry.getPriority());
			Assert.assertEquals(
				"Rotated the key for company " + _COMPANY_ID,
				logEntry.getMessage());

			logEntry = _rotateKey(
				RandomTestUtil.randomString(), rotatedKeyString -> false,
				RandomTestUtil.randomString());

			Assert.assertEquals(LoggerTestUtil.INFO, logEntry.getPriority());
			Assert.assertEquals(
				"Another node already rotated the key for company " +
					_COMPANY_ID,
				logEntry.getMessage());

			Exception exception = new Exception();

			logEntry = _rotateKey(
				RandomTestUtil.randomString(),
				rotatedKeyString -> {
					throw exception;
				},
				RandomTestUtil.randomString());

			Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());
			Assert.assertSame(exception, logEntry.getThrowable());

			logEntry = _rotateKey(
				_WRAPPED_KEY_PREFIX + RandomTestUtil.randomString(),
				rotatedKeyString -> true, RandomTestUtil.randomString());

			Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				CompanyKeyException.class, throwable.getClass());

			fipsApplicationStateMachineUtilMockedStatic.verifyNoInteractions();

			logEntry = _rotateKey(
				RandomTestUtil.randomString(),
				rotatedKeyString -> {
					throw exception;
				},
				PropsValues.COMPANY_DEFAULT_WEB_ID);

			Assert.assertSame(exception, logEntry.getThrowable());

			ArgumentCaptor<Throwable> argumentCaptor = ArgumentCaptor.forClass(
				Throwable.class);

			fipsApplicationStateMachineUtilMockedStatic.verify(
				() -> FIPSApplicationStateMachineUtil.error(
					Mockito.eq("Default company key rotation"),
					argumentCaptor.capture()));

			throwable = argumentCaptor.getValue();

			Assert.assertSame(exception, throwable.getCause());
		}
	}

	@Test
	public void testUnwrapKey() throws Exception {
		Encryptor encryptor = Mockito.mock(Encryptor.class);
		Key key = Mockito.mock(Key.class);
		String keyString = RandomTestUtil.randomString();

		Mockito.when(
			encryptor.deserializeKey(keyString)
		).thenReturn(
			key
		);

		try (AutoCloseable autoCloseable1 = _setEncryptor(encryptor);
			AutoCloseable autoCloseable2 = _setCompanyKeyResolver(null)) {

			Assert.assertSame(
				key,
				CompanyKeyResolverUtil.unwrapKey(
					_COMPANY_ID, _FIPS_KEY_PREFIX + keyString + "}"));
			Assert.assertSame(
				key, CompanyKeyResolverUtil.unwrapKey(_COMPANY_ID, keyString));
		}

		keyString = _WRAPPED_KEY_PREFIX + RandomTestUtil.randomString();

		try (AutoCloseable autoCloseable = _setCompanyKeyResolver(null)) {
			CompanyKeyResolverUtil.unwrapKey(_COMPANY_ID, keyString);

			Assert.fail();
		}
		catch (CompanyKeyException companyKeyException) {
		}

		CompanyKeyResolver companyKeyResolver = Mockito.mock(
			CompanyKeyResolver.class);

		Mockito.when(
			companyKeyResolver.unwrapKey(_COMPANY_ID, keyString)
		).thenReturn(
			key
		);

		try (AutoCloseable autoCloseable = _setCompanyKeyResolver(
				companyKeyResolver)) {

			Assert.assertSame(
				key,
				CompanyKeyResolverUtil.unwrapKey(
					_COMPANY_ID, _FIPS_KEY_PREFIX + keyString + "}"));
			Assert.assertSame(
				key, CompanyKeyResolverUtil.unwrapKey(_COMPANY_ID, keyString));
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			for (String legacyKeyString :
					new String[] {RandomTestUtil.randomString(), keyString}) {

				try {
					CompanyKeyResolverUtil.unwrapKey(
						_COMPANY_ID, legacyKeyString);

					Assert.fail();
				}
				catch (CompanyKeyException companyKeyException) {
					String message = companyKeyException.getMessage();

					Assert.assertTrue(
						message.contains(String.valueOf(_COMPANY_ID)));
				}
			}
		}
	}

	@Test
	public void testWrapKey() throws Exception {
		Encryptor encryptor = Mockito.mock(Encryptor.class);
		Key key = Mockito.mock(Key.class);
		String keyString = RandomTestUtil.randomString();

		Mockito.when(
			encryptor.serializeKey(key)
		).thenReturn(
			keyString
		);

		try (AutoCloseable autoCloseable1 = _setEncryptor(encryptor);
			AutoCloseable autoCloseable2 = _setCompanyKeyResolver(null)) {

			Assert.assertEquals(
				keyString, CompanyKeyResolverUtil.wrapKey(_COMPANY_ID, key));
		}

		CompanyKeyResolver companyKeyResolver = Mockito.mock(
			CompanyKeyResolver.class);

		Mockito.when(
			companyKeyResolver.isEnabled(_COMPANY_ID)
		).thenReturn(
			false
		);

		try (AutoCloseable autoCloseable1 = _setEncryptor(encryptor);
			AutoCloseable autoCloseable2 = _setCompanyKeyResolver(
				companyKeyResolver)) {

			Assert.assertEquals(
				keyString, CompanyKeyResolverUtil.wrapKey(_COMPANY_ID, key));
		}

		keyString = _WRAPPED_KEY_PREFIX + RandomTestUtil.randomString();

		Mockito.when(
			companyKeyResolver.isEnabled(_COMPANY_ID)
		).thenReturn(
			true
		);

		Mockito.when(
			companyKeyResolver.wrapKey(_COMPANY_ID, key)
		).thenReturn(
			keyString
		);

		try (AutoCloseable autoCloseable = _setCompanyKeyResolver(
				companyKeyResolver)) {

			Assert.assertEquals(
				keyString, CompanyKeyResolverUtil.wrapKey(_COMPANY_ID, key));
		}
	}

	private LogEntry _rotateKey(
		String keyString,
		UnsafeFunction<String, Boolean, Throwable> unsafeFunction,
		String webId) {

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			company.getKey()
		).thenReturn(
			keyString
		);

		Mockito.when(
			company.getWebId()
		).thenReturn(
			webId
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				CompanyKeyResolverUtil.class.getName(), LoggerTestUtil.WARN)) {

			logCapture.resetPriority(LoggerTestUtil.INFO);

			CompanyKeyResolverUtil.rotateKey(company, unsafeFunction);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			return logEntries.get(0);
		}
	}

	private AutoCloseable _setCompanyKeyResolver(
		CompanyKeyResolver companyKeyResolver) {

		Snapshot<CompanyKeyResolver> snapshot = Mockito.mock(Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			companyKeyResolver
		);

		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			CompanyKeyResolverUtil.class, "_companyKeyResolverSnapshot",
			snapshot);
	}

	private AutoCloseable _setEncryptor(Encryptor encryptor) {
		Snapshot<Encryptor> snapshot = Mockito.mock(Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			encryptor
		);

		return ReflectionTestUtil.setFieldValueWithAutoCloseable(
			EncryptorUtil.class, "_encryptorSnapshot", snapshot);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _FIPS_KEY_PREFIX = "${fipsKey:";

	private static final String _WRAPPED_KEY_PREFIX = "${wrappedKey:";

}