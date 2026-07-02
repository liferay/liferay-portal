/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.query.processor.custom.user.attributes.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.util.AssetEntryQueryProcessor;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockPortletPreferences;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class CustomUserAttributesAssetEntryQueryProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_user = UserTestUtil.addUser();

		ExpandoBridge expandoBridge = _user.getExpandoBridge();

		expandoBridge.addAttribute(_CUSTOM_USER_ATTRIBUTE_NAME, false);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_companyGroup.getGroupId(), TestPropsValues.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(
				_companyGroup.getGroupId(), _CUSTOM_USER_ATTRIBUTE_NAME);

		if (assetVocabulary != null) {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
		}

		ExpandoTable expandoTable = _expandoTableLocalService.fetchDefaultTable(
			TestPropsValues.getCompanyId(), User.class.getName());

		if (expandoTable == null) {
			return;
		}

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), _CUSTOM_USER_ATTRIBUTE_NAME);

		if (expandoColumn != null) {
			_expandoColumnLocalService.deleteColumn(expandoColumn);
		}
	}

	@Test
	@TestInfo("LPD-94663")
	public void testProcessAssetEntryQuery() throws Exception {
		String userCustomFieldValue = RandomTestUtil.randomString();

		_setCustomUserAttributeValue(userCustomFieldValue);

		AssetVocabulary assetVocabulary1 = _addVocabulary(
			_CUSTOM_USER_ATTRIBUTE_NAME);

		AssetCategory assetCategory = _addCategory(
			userCustomFieldValue, assetVocabulary1.getVocabularyId());

		_addCategory(
			userCustomFieldValue + RandomTestUtil.randomString(),
			assetVocabulary1.getVocabularyId());

		AssetVocabulary assetVocabulary2 = _addVocabulary(
			RandomTestUtil.randomString());

		_addCategory(userCustomFieldValue, assetVocabulary2.getVocabularyId());

		Assert.assertArrayEquals(
			new long[] {assetCategory.getCategoryId()},
			_processAssetEntryQuery());
	}

	@Test
	@TestInfo({"LPD-94663", "LPS-130127"})
	public void testProcessAssetEntryQueryWithCategoryInDifferentVocabulary()
		throws Exception {

		String userCustomFieldValue = RandomTestUtil.randomString();

		_setCustomUserAttributeValue(userCustomFieldValue);

		AssetVocabulary assetVocabulary1 = _addVocabulary(
			_CUSTOM_USER_ATTRIBUTE_NAME);

		_addCategory(
			RandomTestUtil.randomString(), assetVocabulary1.getVocabularyId());

		AssetVocabulary assetVocabulary2 = _addVocabulary(
			RandomTestUtil.randomString());

		_addCategory(userCustomFieldValue, assetVocabulary2.getVocabularyId());

		Assert.assertArrayEquals(new long[0], _processAssetEntryQuery());
	}

	@Test
	@TestInfo({"LPD-94663", "LPS-171296"})
	public void testProcessAssetEntryQueryWithTranslatedCategory()
		throws Exception {

		String userCustomFieldValue = RandomTestUtil.randomString();

		_setCustomUserAttributeValue(userCustomFieldValue);

		AssetVocabulary assetVocabulary = _addVocabulary(
			_CUSTOM_USER_ATTRIBUTE_NAME);

		AssetCategory assetCategory = _addCategory(
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), userCustomFieldValue
			).put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).build(),
			assetVocabulary.getVocabularyId());

		_addCategory(
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).put(
				LocaleUtil.SPAIN, userCustomFieldValue
			).build(),
			assetVocabulary.getVocabularyId());

		Locale themeDisplayLocale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.SPAIN);

		try {
			Assert.assertArrayEquals(
				new long[] {assetCategory.getCategoryId()},
				_processAssetEntryQuery());
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(themeDisplayLocale);
		}
	}

	private AssetCategory _addCategory(
			Map<Locale, String> titleMap, long vocabularyId)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _companyGroup.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, titleMap,
			Collections.emptyMap(), vocabularyId, false, null, _serviceContext);
	}

	private AssetCategory _addCategory(String name, long vocabularyId)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _companyGroup.getGroupId(), name,
			vocabularyId, _serviceContext);
	}

	private AssetVocabulary _addVocabulary(String name) throws Exception {
		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _companyGroup.getGroupId(), name,
			_serviceContext);
	}

	private long[] _processAssetEntryQuery() throws Exception {
		MockPortletPreferences mockPortletPreferences =
			new MockPortletPreferences();

		mockPortletPreferences.setValue(
			"customUserAttributes", _CUSTOM_USER_ATTRIBUTE_NAME);

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		_assetEntryQueryProcessor.processAssetEntryQuery(
			_user, mockPortletPreferences, assetEntryQuery);

		return assetEntryQuery.getAllCategoryIds();
	}

	private void _setCustomUserAttributeValue(String value) {
		ExpandoBridge expandoBridge = _user.getExpandoBridge();

		expandoBridge.setAttribute(_CUSTOM_USER_ATTRIBUTE_NAME, value, false);
	}

	private static final String _CUSTOM_USER_ATTRIBUTE_NAME =
		RandomTestUtil.randomString();

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject(
		filter = "component.name=com.liferay.asset.entry.query.processor.custom.user.attributes.internal.CustomUserAttributesAssetEntryQueryProcessor"
	)
	private AssetEntryQueryProcessor _assetEntryQueryProcessor;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Group _companyGroup;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}