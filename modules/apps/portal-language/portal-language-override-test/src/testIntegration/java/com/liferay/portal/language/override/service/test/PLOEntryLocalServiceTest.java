/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.override.exception.PLOEntryImportException;
import com.liferay.portal.language.override.exception.PLOEntryKeyException;
import com.liferay.portal.language.override.exception.PLOEntryLanguageIdException;
import com.liferay.portal.language.override.exception.PLOEntryValueException;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class PLOEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddOrUpdatePLOEntry() throws Exception {
		String newKey = RandomTestUtil.randomString();

		_assertTranslationValue(newKey, null);

		String languageId = LanguageUtil.getLanguageId(LocaleUtil.getDefault());

		PLOEntry ploEntry = _addOrUpdatePLOEntry(
			newKey, languageId, RandomTestUtil.randomString());

		_assertTranslationValue(newKey, ploEntry.getValue());

		String existingKey = "available-languages";

		Assert.assertNotNull(
			LanguageResources.getMessage(LocaleUtil.getDefault(), existingKey));

		ploEntry = _addOrUpdatePLOEntry(
			existingKey, languageId, RandomTestUtil.randomString());

		_assertTranslationValue(existingKey, ploEntry.getValue());

		newKey = RandomTestUtil.randomString();

		_addOrUpdatePLOEntry(newKey, "en_CA", RandomTestUtil.randomString());

		_assertTranslationValue(newKey, null);

		_assertException(
			PLOEntryKeyException.MustBeShorter.class,
			() -> {
				int keyMaxLength = ModelHintsUtil.getMaxLength(
					PLOEntry.class.getName(), "key");

				_addOrUpdatePLOEntry(
					RandomTestUtil.randomString(keyMaxLength + 1), languageId,
					RandomTestUtil.randomString());
			});
		_assertException(
			PLOEntryKeyException.MustNotBeNull.class,
			() -> _addOrUpdatePLOEntry(
				StringPool.BLANK, languageId, RandomTestUtil.randomString()));
		_assertException(
			PLOEntryLanguageIdException.MustBeAvailable.class,
			() -> _addOrUpdatePLOEntry(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString()));
		_assertException(
			PLOEntryValueException.MustNotBeNull.class,
			() -> _addOrUpdatePLOEntry(
				RandomTestUtil.randomString(), languageId, StringPool.BLANK));

		ploEntry = _addOrUpdatePLOEntry(
			RandomTestUtil.randomString(), "en", RandomTestUtil.randomString());

		Assert.assertEquals("en_US", ploEntry.getLanguageId());
	}

	@Test
	public void testAddOrUpdatePLOEntryRollback() throws Throwable {
		String key = RandomTestUtil.randomString();

		Locale locale = LocaleUtil.getDefault();

		try {
			TransactionInvokerUtil.invoke(
				TransactionConfig.Factory.create(
					Propagation.REQUIRED, new Class<?>[] {Exception.class}),
				() -> {
					_addOrUpdatePLOEntry(
						key, LanguageUtil.getLanguageId(locale),
						RandomTestUtil.randomString());

					throw new Exception(
						"Unable to add portal language override entry");
				});

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Unable to add portal language override entry",
				exception.getMessage());

			Assert.assertEquals(key, _language.get(locale, key));
		}
	}

	@Test
	public void testDeletePLOEntry() throws PortalException {
		String key = RandomTestUtil.randomString();
		Locale locale = LocaleUtil.getDefault();
		String value = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdatePLOEntry(
			key, LanguageUtil.getLanguageId(locale), value);

		Assert.assertEquals(value, _language.get(locale, key));

		_ploEntryLocalService.deletePLOEntry(ploEntry.getPloEntryId());

		Assert.assertEquals(key, _language.get(locale, key));
	}

	@Test
	public void testDeletePLOEntryRollback() throws Throwable {
		String key = RandomTestUtil.randomString();
		Locale locale = LocaleUtil.getDefault();
		String value = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdatePLOEntry(
			key, LanguageUtil.getLanguageId(locale), value);

		try {
			TransactionInvokerUtil.invoke(
				TransactionConfig.Factory.create(
					Propagation.REQUIRED, new Class<?>[] {Exception.class}),
				() -> {
					_ploEntryLocalService.deletePLOEntry(
						ploEntry.getPloEntryId());

					throw new Exception(
						"Unable to add portal language override entry");
				});

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				"Unable to add portal language override entry",
				exception.getMessage());

			Assert.assertEquals(value, _language.get(locale, key));
		}
	}

	@Test
	public void testImportPLOEntries() throws IOException, PortalException {
		String key1 = RandomTestUtil.randomString();
		String key2 = RandomTestUtil.randomString();
		String value1 = RandomTestUtil.randomString();
		String value2 = RandomTestUtil.randomString();

		Properties properties = new Properties();

		properties.setProperty(key1, value1);
		properties.setProperty(key2, value2);

		_ploEntryLocalService.importPLOEntries(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			LanguageUtil.getLanguageId(LocaleUtil.US), properties);

		Assert.assertEquals(value1, _language.get(LocaleUtil.US, key1));
		Assert.assertEquals(value2, _language.get(LocaleUtil.US, key2));
	}

	@Test
	public void testImportPLOEntriesRollback()
		throws IOException, PortalException {

		String key1 = "good-key";
		String key2 = "key-with-empty-value";

		Properties properties = new Properties();

		properties.setProperty(key1, RandomTestUtil.randomString());
		properties.setProperty(key2, StringPool.BLANK);
		properties.setProperty(StringPool.BLANK, RandomTestUtil.randomString());

		try {
			_ploEntryLocalService.importPLOEntries(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				LanguageUtil.getLanguageId(LocaleUtil.US), properties);

			Assert.fail();
		}
		catch (PLOEntryImportException.InvalidTranslations
					ploEntryImportException) {

			Throwable[] throwables = ploEntryImportException.getSuppressed();

			Assert.assertEquals(
				Arrays.toString(throwables), 2, throwables.length);

			List<Class<?>> expectedClasses = Arrays.asList(
				new Class<?>[] {
					PLOEntryValueException.MustNotBeNull.class,
					PLOEntryKeyException.MustNotBeNull.class
				});

			for (Throwable throwable : throwables) {
				Assert.assertTrue(
					expectedClasses.contains(throwable.getClass()));
			}
		}

		Assert.assertEquals(key1, _language.get(LocaleUtil.US, key1));
		Assert.assertEquals(key2, _language.get(LocaleUtil.US, key2));
	}

	private PLOEntry _addOrUpdatePLOEntry(
			String key, String languageId, String value)
		throws PortalException {

		return _ploEntryLocalService.addOrUpdatePLOEntry(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), key,
			languageId, value);
	}

	private void _assertException(
			Class<? extends PortalException> exceptionClass,
			UnsafeRunnable<? extends PortalException> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertTrue(
				exceptionClass.isAssignableFrom(portalException.getClass()));
		}
	}

	private void _assertTranslationValue(String key, String value) {
		Assert.assertEquals(
			value, LanguageResources.getMessage(LocaleUtil.getDefault(), key));
		Assert.assertEquals(
			value,
			ResourceBundleUtil.getString(
				LanguageResources.getResourceBundle(LocaleUtil.getDefault()),
				key));
	}

	@Inject
	private Language _language;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

}