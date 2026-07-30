/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.override.exception.PLOEntryExternalReferenceCodeException;
import com.liferay.portal.language.override.exception.PLOEntryImportException;
import com.liferay.portal.language.override.exception.PLOEntryKeyException;
import com.liferay.portal.language.override.exception.PLOEntryLanguageIdException;
import com.liferay.portal.language.override.exception.PLOEntryValueException;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.model.PLOEntryTable;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class PLOEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddOrUpdatePLOEntry() throws Exception {
		_testAddOrUpdatePLOEntry();
		_testAddOrUpdatePLOEntryGetsDefaultERC();
		_testAddOrUpdatePLOEntryOnERCConflict();
		_testAddOrUpdatePLOEntryOnMatchingERC();
		_testAddOrUpdatePLOEntryOnNewERC();
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
	public void testDeleteByExternalReferenceCode() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		String externalReferenceCode = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdatePLOEntry(
			externalReferenceCode, RandomTestUtil.randomString(), "en_US",
			RandomTestUtil.randomString());

		_ploEntryLocalService.deletePLOEntryByExternalReferenceCode(
			externalReferenceCode, companyId);

		Assert.assertNull(
			_ploEntryLocalService.fetchPLOEntry(ploEntry.getPloEntryId()));
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
	public void testGetPLOEntries() throws Exception {
		_testGetPLOEntriesIgnoresKeyCase();
		_testGetPLOEntriesIgnoresValueCase();
		_testGetPLOEntriesMatchesSubstring();
		_testGetPLOEntriesOrdersByKey();
		_testGetPLOEntriesSplitsKeywords();
		_testGetPLOEntriesTreatsUnderlineAsWildcard();
	}

	@Test
	public void testGetPLOEntryByExternalReferenceCode() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		String externalReferenceCode = RandomTestUtil.randomString();

		PLOEntry ploEntry1 = _addOrUpdatePLOEntry(
			externalReferenceCode, RandomTestUtil.randomString(), "en_US",
			RandomTestUtil.randomString());

		PLOEntry ploEntry2 =
			_ploEntryLocalService.getPLOEntryByExternalReferenceCode(
				externalReferenceCode, companyId);

		Assert.assertEquals(
			ploEntry1.getPloEntryId(), ploEntry2.getPloEntryId());
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

	private PLOEntry _addOrUpdatePLOEntry(
			String externalReferenceCode, String key, String languageId,
			String value)
		throws PortalException {

		return _ploEntryLocalService.addOrUpdatePLOEntry(
			externalReferenceCode, TestPropsValues.getCompanyId(),
			TestPropsValues.getUserId(), key, languageId, value);
	}

	private PLOEntry _addPLOEntry(String key, String value) throws Exception {
		return _addOrUpdatePLOEntry(
			key, LanguageUtil.getLanguageId(LocaleUtil.getDefault()), value);
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

	private void _assertGetPLOEntries(
			String keywords, PLOEntry... expectedPLOEntries)
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		List<PLOEntry> ploEntries = _ploEntryLocalService.getPLOEntries(
			companyId, keywords, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			ploEntries.toString(), expectedPLOEntries.length,
			ploEntries.size());

		for (PLOEntry expectedPLOEntry : expectedPLOEntries) {
			Assert.assertTrue(
				ploEntries.toString(),
				ListUtil.exists(
					ploEntries,
					ploEntry ->
						ploEntry.getPloEntryId() ==
							expectedPLOEntry.getPloEntryId()));
		}

		Assert.assertEquals(
			expectedPLOEntries.length,
			_ploEntryLocalService.getPLOEntriesCount(companyId, keywords));
	}

	private void _assertOrder(
		List<PLOEntry> ploEntries, PLOEntry... expectedPLOEntries) {

		List<Long> expectedPloEntryIds = new ArrayList<>();

		for (PLOEntry expectedPLOEntry : expectedPLOEntries) {
			expectedPloEntryIds.add(expectedPLOEntry.getPloEntryId());
		}

		List<Long> ploEntryIds = new ArrayList<>();

		for (PLOEntry ploEntry : ploEntries) {
			if (expectedPloEntryIds.contains(ploEntry.getPloEntryId())) {
				ploEntryIds.add(ploEntry.getPloEntryId());
			}
		}

		Assert.assertEquals(
			ploEntries.toString(), expectedPloEntryIds, ploEntryIds);
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

	private OrderByComparator<PLOEntry> _createOrderByComparator(
		boolean ascending) {

		return OrderByComparatorFactoryUtil.create(
			PLOEntryTable.INSTANCE.getTableName(),
			PLOEntryTable.INSTANCE.key.getName(), ascending);
	}

	private void _testAddOrUpdatePLOEntry() throws Exception {
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
			PLOEntryExternalReferenceCodeException.MustNotExceedMaximumLength.
				class,
			() -> {
				int externalReferenceCodeMaxLength =
					ModelHintsUtil.getMaxLength(
						PLOEntry.class.getName(), "externalReferenceCode");

				_addOrUpdatePLOEntry(
					RandomTestUtil.randomString(
						externalReferenceCodeMaxLength + 1),
					RandomTestUtil.randomString(), languageId,
					RandomTestUtil.randomString());
			});
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

	private void _testAddOrUpdatePLOEntryGetsDefaultERC() throws Exception {
		PLOEntry ploEntry = _addOrUpdatePLOEntry(
			StringPool.BLANK, "test-key-" + RandomTestUtil.randomString(),
			"en_US", "value");

		Assert.assertEquals(
			String.valueOf(ploEntry.getPloEntryId()),
			ploEntry.getExternalReferenceCode());

		String externalReferenceCode = RandomTestUtil.randomString();

		ploEntry = _addOrUpdatePLOEntry(
			externalReferenceCode, "test-key-" + RandomTestUtil.randomString(),
			"en_US", "value");

		Assert.assertEquals(
			externalReferenceCode, ploEntry.getExternalReferenceCode());
	}

	private void _testAddOrUpdatePLOEntryOnERCConflict() throws Exception {
		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();
		String key1 = RandomTestUtil.randomString();
		String key2 = RandomTestUtil.randomString();

		PLOEntry ploEntry1 = _addOrUpdatePLOEntry(
			externalReferenceCode1, key1, "en_US", "value1");
		PLOEntry ploEntry2 = _addOrUpdatePLOEntry(
			externalReferenceCode2, key2, "en_US", "value2");

		_assertException(
			PLOEntryKeyException.MustNotBeDuplicate.class,
			() -> _addOrUpdatePLOEntry(
				externalReferenceCode1, key2, "en_US", "value3"));

		ploEntry1 = _ploEntryLocalService.getPLOEntry(
			ploEntry1.getPloEntryId());

		Assert.assertEquals(key1, ploEntry1.getKey());
		Assert.assertEquals("value1", ploEntry1.getValue());

		ploEntry2 = _ploEntryLocalService.getPLOEntry(
			ploEntry2.getPloEntryId());

		Assert.assertEquals(key2, ploEntry2.getKey());
		Assert.assertEquals("value2", ploEntry2.getValue());
	}

	private void _testAddOrUpdatePLOEntryOnMatchingERC() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdatePLOEntry(
			externalReferenceCode, RandomTestUtil.randomString(), "en_US",
			"value1");

		String key = RandomTestUtil.randomString();

		PLOEntry updatedPLOEntry = _addOrUpdatePLOEntry(
			externalReferenceCode, key, "en_CA", "value2");

		Assert.assertEquals(
			ploEntry.getPloEntryId(), updatedPLOEntry.getPloEntryId());
		Assert.assertEquals(key, updatedPLOEntry.getKey());
		Assert.assertEquals("en_CA", updatedPLOEntry.getLanguageId());
		Assert.assertEquals("value2", updatedPLOEntry.getValue());

		long mvccVersion = updatedPLOEntry.getMvccVersion();
		Date modifiedDate = updatedPLOEntry.getModifiedDate();

		PLOEntry unchangedPLOEntry = _addOrUpdatePLOEntry(
			externalReferenceCode, key, "en_CA", "value2");

		Assert.assertEquals(
			ploEntry.getPloEntryId(), unchangedPLOEntry.getPloEntryId());
		Assert.assertEquals(mvccVersion, unchangedPLOEntry.getMvccVersion());
		Assert.assertEquals(modifiedDate, unchangedPLOEntry.getModifiedDate());
	}

	private void _testAddOrUpdatePLOEntryOnNewERC() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		String key = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdatePLOEntry(key, "en_US", "value1");

		String externalReferenceCode = ploEntry.getExternalReferenceCode();

		String newExternalReferenceCode = RandomTestUtil.randomString();

		PLOEntry updatedPLOEntry = _addOrUpdatePLOEntry(
			newExternalReferenceCode, key, "en_US", "value2");

		Assert.assertEquals(
			ploEntry.getPloEntryId(), updatedPLOEntry.getPloEntryId());
		Assert.assertEquals(
			newExternalReferenceCode,
			updatedPLOEntry.getExternalReferenceCode());
		Assert.assertEquals("value2", updatedPLOEntry.getValue());

		PLOEntry fetchedPLOEntry =
			_ploEntryLocalService.getPLOEntryByExternalReferenceCode(
				newExternalReferenceCode, companyId);

		Assert.assertEquals(
			ploEntry.getPloEntryId(), fetchedPLOEntry.getPloEntryId());

		Assert.assertNull(
			_ploEntryLocalService.fetchPLOEntryByExternalReferenceCode(
				externalReferenceCode, companyId));
	}

	private void _testGetPLOEntriesIgnoresKeyCase() throws Exception {
		String key = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addPLOEntry(key, RandomTestUtil.randomString());

		_assertGetPLOEntries(StringUtil.toUpperCase(key), ploEntry);
		_assertGetPLOEntries(StringUtil.toLowerCase(key), ploEntry);
	}

	private void _testGetPLOEntriesIgnoresValueCase() throws Exception {
		String value = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addPLOEntry(RandomTestUtil.randomString(), value);

		_assertGetPLOEntries(StringUtil.toUpperCase(value), ploEntry);
		_assertGetPLOEntries(StringUtil.toLowerCase(value), ploEntry);
	}

	private void _testGetPLOEntriesMatchesSubstring() throws Exception {
		String key = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addPLOEntry(
			"prefix-" + key + "-suffix", RandomTestUtil.randomString());

		_assertGetPLOEntries(key, ploEntry);
	}

	private void _testGetPLOEntriesOrdersByKey() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		String keyPrefix = RandomTestUtil.randomString();

		PLOEntry ploEntryC = _addPLOEntry(
			keyPrefix + "-c", RandomTestUtil.randomString());
		PLOEntry ploEntryA = _addPLOEntry(
			keyPrefix + "-a", RandomTestUtil.randomString());
		PLOEntry ploEntryB = _addPLOEntry(
			keyPrefix + "-b", RandomTestUtil.randomString());

		_assertOrder(
			_ploEntryLocalService.getPLOEntries(
				companyId, keyPrefix, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				_createOrderByComparator(false)),
			ploEntryC, ploEntryB, ploEntryA);
		_assertOrder(
			_ploEntryLocalService.getPLOEntries(
				companyId, keyPrefix, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				_createOrderByComparator(true)),
			ploEntryA, ploEntryB, ploEntryC);

		_assertOrder(
			_ploEntryLocalService.getPLOEntries(
				companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				_createOrderByComparator(false)),
			ploEntryC, ploEntryB, ploEntryA);
		_assertOrder(
			_ploEntryLocalService.getPLOEntries(
				companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				_createOrderByComparator(true)),
			ploEntryA, ploEntryB, ploEntryC);

		_assertOrder(
			_ploEntryLocalService.getPLOEntries(
				companyId, keyPrefix, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null),
			ploEntryA, ploEntryB, ploEntryC);
	}

	private void _testGetPLOEntriesSplitsKeywords() throws Exception {
		String prefix = RandomTestUtil.randomString();

		String key1 = prefix + "-first";
		String key2 = prefix + "-second";

		PLOEntry ploEntry1 = _addPLOEntry(key1, RandomTestUtil.randomString());
		PLOEntry ploEntry2 = _addPLOEntry(key2, RandomTestUtil.randomString());

		_assertGetPLOEntries(key1 + " " + key2, ploEntry1, ploEntry2);
	}

	private void _testGetPLOEntriesTreatsUnderlineAsWildcard()
		throws Exception {

		String key = RandomTestUtil.randomString();

		PLOEntry ploEntry1 = _addPLOEntry(
			key + "-a_b", RandomTestUtil.randomString());
		PLOEntry ploEntry2 = _addPLOEntry(
			key + "-axb", RandomTestUtil.randomString());

		_assertGetPLOEntries(key + "-a_b", ploEntry1, ploEntry2);
	}

	@Inject
	private Language _language;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

}