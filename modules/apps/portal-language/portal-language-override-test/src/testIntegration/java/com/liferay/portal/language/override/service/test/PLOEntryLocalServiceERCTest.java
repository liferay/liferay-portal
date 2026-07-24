/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class PLOEntryLocalServiceERCTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_companyId = TestPropsValues.getCompanyId();
		_userId = TestPropsValues.getUserId();
	}

	@Test
	public void testAddGeneratesExternalReferenceCode() throws Exception {
		PLOEntry ploEntry = _addOrUpdate(
			StringPool.BLANK, "test-key-" + RandomTestUtil.randomString(),
			"en_US", "value");

		try {
			Assert.assertTrue(
				Validator.isNotNull(ploEntry.getExternalReferenceCode()));
		}
		finally {
			_ploEntryLocalService.deletePLOEntry(ploEntry);
		}
	}

	@Test
	public void testGetAndDeleteByExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdate(
			externalReferenceCode, "test-key-" + RandomTestUtil.randomString(),
			"en_US", "value");

		PLOEntry fetchedPLOEntry =
			_ploEntryLocalService.getPLOEntryByExternalReferenceCode(
				_companyId, externalReferenceCode);

		Assert.assertEquals(
			ploEntry.getPloEntryId(), fetchedPLOEntry.getPloEntryId());

		_ploEntryLocalService.deletePLOEntryByExternalReferenceCode(
			_companyId, externalReferenceCode);

		Assert.assertNull(
			_ploEntryLocalService.fetchPLOEntry(ploEntry.getPloEntryId()));
	}

	@Test
	public void testUpdateByKeyAndLanguageIdKeepsExistingERC()
		throws Exception {

		String key = "test-key-" + RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdate("erc-a", key, "en_US", "value1");

		// A different incoming external reference code with the same key and
		// language ID must keep the existing external reference code

		PLOEntry updatedPLOEntry = _addOrUpdate(
			"erc-b", key, "en_US", "value2");

		try {
			Assert.assertEquals(
				ploEntry.getPloEntryId(), updatedPLOEntry.getPloEntryId());
			Assert.assertEquals(
				"erc-a", updatedPLOEntry.getExternalReferenceCode());
			Assert.assertEquals("value2", updatedPLOEntry.getValue());
		}
		finally {
			_ploEntryLocalService.deletePLOEntry(updatedPLOEntry);
		}
	}

	@Test
	public void testUpdateByMatchingExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();
		String key = "test-key-" + RandomTestUtil.randomString();

		PLOEntry ploEntry = _addOrUpdate(
			externalReferenceCode, key, "en_US", "value1");

		PLOEntry updatedPLOEntry = _addOrUpdate(
			externalReferenceCode, key, "en_US", "value2");

		try {
			Assert.assertEquals(
				ploEntry.getPloEntryId(), updatedPLOEntry.getPloEntryId());
			Assert.assertEquals("value2", updatedPLOEntry.getValue());
		}
		finally {
			_ploEntryLocalService.deletePLOEntry(updatedPLOEntry);
		}
	}

	private PLOEntry _addOrUpdate(
			String externalReferenceCode, String key, String languageId,
			String value)
		throws Exception {

		return _ploEntryLocalService.addOrUpdatePLOEntry(
			externalReferenceCode, _companyId, _userId, key, languageId, value);
	}

	private long _companyId;

	@Inject
	private PLOEntryLocalService _ploEntryLocalService;

	private long _userId;

}