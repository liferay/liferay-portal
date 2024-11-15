/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.client.dto.v1_0.Language;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LanguageIds;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@LanguageIds(
	availableLanguageIds = {"en_US", "es_ES", "fr_FR", "zh_CN"},
	defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class LanguageResourceTest extends BaseLanguageResourceTestCase {

	@Override
	@Test
	public void testGetAssetLibraryLanguagesPage() throws Exception {
		Page<Language> page = languageResource.getAssetLibraryLanguagesPage(
			testGetAssetLibraryLanguagesPage_getAssetLibraryId());

		Assert.assertEquals(
			_getAvailableLocalesSize(
				testGetAssetLibraryLanguagesPage_getAssetLibraryId()),
			page.getTotalCount());

		GroupTestUtil.updateDisplaySettings(
			testDepotEntry.getGroupId(),
			Arrays.asList(
				LocaleUtil.US, LocaleUtil.CHINA, LocaleUtil.FRANCE,
				LocaleUtil.SPAIN),
			LocaleUtil.US);

		page = languageResource.getAssetLibraryLanguagesPage(
			testGetAssetLibraryLanguagesPage_getAssetLibraryId());

		Assert.assertEquals(
			LocaleUtil.US,
			LocaleUtil.fromLanguageId(
				page.fetchFirstItem(
				).getId()));
		Assert.assertTrue(
			page.fetchFirstItem(
			).getMarkedAsDefault());
		Assert.assertEquals(4, page.getTotalCount());
		assertValid(page);
	}

	@Override
	@Test
	public void testGetSiteLanguagesPage() throws Exception {
		Page<Language> page = languageResource.getSiteLanguagesPage(
			testGetSiteLanguagesPage_getSiteId());

		Assert.assertEquals(
			_getAvailableLocalesSize(testGetSiteLanguagesPage_getSiteId()),
			page.getTotalCount());

		GroupTestUtil.updateDisplaySettings(
			testGetSiteLanguagesPage_getSiteId(), Arrays.asList(LocaleUtil.US),
			LocaleUtil.US);

		page = languageResource.getSiteLanguagesPage(
			testGetSiteLanguagesPage_getSiteId());

		Assert.assertEquals(
			LocaleUtil.US,
			LocaleUtil.fromLanguageId(
				page.fetchFirstItem(
				).getId()));
		Assert.assertTrue(
			page.fetchFirstItem(
			).getMarkedAsDefault());
		Assert.assertEquals(1, page.getTotalCount());
		assertValid(page);
	}

	@Override
	@Test
	public void testGraphQLGetSiteLanguagesPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"languages",
			HashMapBuilder.<String, Object>put(
				"siteKey", "\"" + testGetSiteLanguagesPage_getSiteId() + "\""
			).build(),
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject languagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/languages");

		Assert.assertEquals(
			_getAvailableLocalesSize(testGetSiteLanguagesPage_getSiteId()),
			languagesJSONObject.get("totalCount"));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	private int _getAvailableLocalesSize(Long siteId) {
		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(siteId);

		return availableLocales.size();
	}

}