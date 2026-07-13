/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.asset.display.page.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.importer.CPFileImporter;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AssetCategoryAssetDisplayPageFriendlyURLResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = CompanyLocalServiceUtil.getCompany(_group.getCompanyId());

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				_company.getCompanyId());

		CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), commerceCurrency.getCode());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_company.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId());

		InputStream inputStream =
			AssetCategoryAssetDisplayPageFriendlyURLResolverTest.class.
				getResourceAsStream("dependencies/layouts.json");

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			StringUtil.read(inputStream));

		_cpFileImporter.createLayouts(
			jsonArray,
			AssetCategoryAssetDisplayPageFriendlyURLResolverTest.class.
				getClassLoader(),
			null, _serviceContext);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetLayoutFriendlyURLCompositeWithFlatEntry()
		throws Exception {

		AssetCategory assetCategory = _addAssetCategory();

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId());

		_assertResolves(friendlyURLEntry);
	}

	@Test
	public void testGetLayoutFriendlyURLCompositeWithHierarchicalEntry()
		throws Exception {

		// Reproduce the state left by the LPD-70396 backfill: the friendly URL
		// entry is reparented from the flat default to the vocabulary, so the
		// legacy flat lookup at PARENT_CLASS_PK_DEFAULT no longer finds it

		AssetCategory assetCategory = _addAssetCategory();

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId());

		String languageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		_friendlyURLEntryLocalService.updateFriendlyURLEntry(
			friendlyURLEntry.getFriendlyURLEntryId(),
			_portal.getClassNameId(AssetCategory.class),
			assetCategory.getVocabularyId(), assetCategory.getCategoryId(),
			languageId,
			Collections.singletonMap(
				languageId, friendlyURLEntry.getUrlTitle(languageId)),
			_serviceContext);

		_assertResolves(friendlyURLEntry);
	}

	private AssetCategory _addAssetCategory() throws Exception {

		// The Commerce category resolver looks up the friendly URL entry in the
		// company group, so the category must live there for /g/ to resolve

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), _serviceContext);

		return _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _company.getGroupId(),
			RandomTestUtil.randomString(), _assetVocabulary.getVocabularyId(),
			_serviceContext);
	}

	private void _assertResolves(FriendlyURLEntry friendlyURLEntry)
		throws Exception {

		String urlTitle = friendlyURLEntry.getUrlTitle(
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

		String friendlyURL = _friendlyURLResolver.getURLSeparator() + urlTitle;

		Assert.assertNotNull(
			_friendlyURLResolver.getLayoutFriendlyURLComposite(
				_company.getCompanyId(), _group.getGroupId(), false,
				friendlyURL, Collections.<String, String[]>emptyMap(),
				Collections.<String, Object>singletonMap(
					WebKeys.LOCALE, LocaleUtil.getSiteDefault())));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Company _company;

	@Inject
	private CPFileImporter _cpFileImporter;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.commerce.product.content.web.internal.asset.display.page.portlet.AssetCategoryAssetDisplayPageFriendlyURLResolver"
	)
	private FriendlyURLResolver _friendlyURLResolver;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}