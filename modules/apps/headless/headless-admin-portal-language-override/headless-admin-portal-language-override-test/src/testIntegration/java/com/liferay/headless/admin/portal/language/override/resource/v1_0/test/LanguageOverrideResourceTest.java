/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.portal.language.override.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.portal.language.override.client.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.portal.language.override.client.pagination.Page;
import com.liferay.headless.admin.portal.language.override.client.resource.v1_0.LanguageOverrideResource;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
public class LanguageOverrideResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_languageOverrideResource = LanguageOverrideResource.builder(
		).authentication(
			"test@liferay.com", "test"
		).locale(
			null
		).parameter(
			"nestedFields", "creator"
		).build();
	}

	@Test
	public void testGetByExternalReferenceCodeReturnsCreator()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_languageOverrideResource.putLanguageOverrideByExternalReferenceCode(
			externalReferenceCode,
			_toLanguageOverride(
				"test-" + RandomTestUtil.randomString(), "en_US", "value"));

		LanguageOverride languageOverride =
			_languageOverrideResource.
				getLanguageOverrideByExternalReferenceCode(
					externalReferenceCode);

		Assert.assertNotNull(languageOverride.getCreator());
		Assert.assertNotNull(languageOverride.getDateCreated());

		_languageOverrideResource.deleteLanguageOverrideByExternalReferenceCode(
			externalReferenceCode);
	}

	@Test
	public void testGetLanguageOverridesPageWithSearch() throws Exception {
		String key = "test-" + RandomTestUtil.randomString();

		String externalReferenceCode = RandomTestUtil.randomString();

		_languageOverrideResource.putLanguageOverrideByExternalReferenceCode(
			externalReferenceCode, _toLanguageOverride(key, "en_US", "value"));

		Page<LanguageOverride> page =
			_languageOverrideResource.getLanguageOverridesPage(
				key, null, null, null);

		boolean found = false;

		for (LanguageOverride languageOverride : page.getItems()) {
			if (key.equals(languageOverride.getKey())) {
				found = true;
			}
		}

		Assert.assertTrue(found);

		_languageOverrideResource.deleteLanguageOverrideByExternalReferenceCode(
			externalReferenceCode);
	}

	@Test
	public void testPutCreatesThenUpdatesByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		String key = "test-" + RandomTestUtil.randomString();

		LanguageOverride languageOverride =
			_languageOverrideResource.
				putLanguageOverrideByExternalReferenceCode(
					externalReferenceCode,
					_toLanguageOverride(key, "en_US", "value1"));

		Assert.assertEquals("value1", languageOverride.getValue());

		LanguageOverride updatedLanguageOverride =
			_languageOverrideResource.
				putLanguageOverrideByExternalReferenceCode(
					externalReferenceCode,
					_toLanguageOverride(key, "en_US", "value2"));

		Assert.assertEquals(
			languageOverride.getId(), updatedLanguageOverride.getId());
		Assert.assertEquals("value2", updatedLanguageOverride.getValue());

		_languageOverrideResource.deleteLanguageOverrideByExternalReferenceCode(
			externalReferenceCode);
	}

	private LanguageOverride _toLanguageOverride(
		String keyString, String languageIdString, String valueString) {

		LanguageOverride languageOverride = new LanguageOverride();

		languageOverride.setKey(keyString);
		languageOverride.setLanguageId(languageIdString);
		languageOverride.setValue(valueString);

		return languageOverride;
	}

	private LanguageOverrideResource _languageOverrideResource;

}