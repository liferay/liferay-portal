/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.friendly.url.exception.FriendlyURLCategoryException;
import com.liferay.friendly.url.exception.FriendlyURLLengthException;
import com.liferay.friendly.url.exception.FriendlyURLLocalizationUrlTitleException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.HashMap;
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
 * @author Adolfo Pérez
 */
@LanguageIds(
	availableLanguageIds = {"en_US", "es_ES", "pt_BR", "zh_CN"},
	defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class FriendlyURLEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetVocabulary = AssetTestUtil.addVocabulary(_group.getGroupId());

		_assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), _assetVocabulary.getVocabularyId());

		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() {
		_friendlyURLEntryLocalService.deleteGroupFriendlyURLEntries(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(User.class));
		_friendlyURLEntryLocalService.deleteGroupFriendlyURLEntries(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(User.class));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testAddFriendlyURLEntryAllowsSameUrlTitleForDifferentParentClassPK()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(User.class);
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, 1L, TestPropsValues.getUserId(),
			defaultLanguageId,
			Collections.singletonMap(defaultLanguageId, urlTitle),
			_getServiceContext());

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(), classNameId, 2L, _user.getUserId(),
				defaultLanguageId,
				Collections.singletonMap(defaultLanguageId, urlTitle),
				_getServiceContext());

		Assert.assertEquals(urlTitle, friendlyURLEntry.getUrlTitle());
		Assert.assertEquals(2L, friendlyURLEntry.getParentClassPK());
	}

	@Test
	public void testAddFriendlyURLEntryKeepsOldLocalizedValues()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(User.class);

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			Collections.singletonMap(
				_language.getLanguageId(LocaleUtil.US), "url-title-en"),
			_getServiceContext());

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			Collections.singletonMap(
				_language.getLanguageId(LocaleUtil.CHINA), "url-title-zh"),
			_getServiceContext());

		FriendlyURLEntry finalFriendlyURL =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
				"url-title-en", _getServiceContext());

		Assert.assertEquals(
			"url-title-en",
			finalFriendlyURL.getUrlTitle(
				_language.getLanguageId(LocaleUtil.US)));
		Assert.assertEquals(
			"url-title-zh",
			finalFriendlyURL.getUrlTitle(
				_language.getLanguageId(LocaleUtil.CHINA)));
	}

	@Test(expected = DuplicateFriendlyURLEntryException.class)
	@TestInfo("LPD-90910")
	public void testAddFriendlyURLEntryRejectsDuplicateUrlTitleForSameParentClassPK()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(User.class);
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, 1L, TestPropsValues.getUserId(),
			defaultLanguageId,
			Collections.singletonMap(defaultLanguageId, urlTitle),
			_getServiceContext());

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, 1L, _user.getUserId(),
			defaultLanguageId,
			Collections.singletonMap(defaultLanguageId, urlTitle),
			_getServiceContext());
	}

	@Test
	public void testAddFriendlyURLEntryReusesOwnedUrlTitles() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);
		String urlTitle = "existing-url-title";

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, _getServiceContext());

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			"existing-url-title-2", _getServiceContext());

		FriendlyURLEntry finalFriendlyURL =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
				urlTitle, _getServiceContext());

		Assert.assertEquals(urlTitle, finalFriendlyURL.getUrlTitle());
	}

	@Test
	public void testAddFriendlyURLEntryWithAssetCategories() throws Exception {
		ServiceContext serviceContext = _getServiceContext();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "cat1",
			assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "cat2",
			assetVocabulary.getVocabularyId(), serviceContext);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(User.class),
				TestPropsValues.getUserId(),
				Collections.singletonMap(
					_language.getLanguageId(LocaleUtil.US), "url-title-en"),
				serviceContext);

		Assert.assertEquals(
			"cat1/cat2/url-title-en",
			friendlyURLEntry.getCategorizedUrlTitle(
				_language.getLanguageId(LocaleUtil.US)));
	}

	@Test
	public void testAddFriendlyURLEntryWithLocalizedAssetCategories()
		throws Exception {

		ServiceContext serviceContext = _getServiceContext();

		_addLocalizedAssetCategories(serviceContext);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(User.class),
				TestPropsValues.getUserId(),
				HashMapBuilder.put(
					_language.getLanguageId(LocaleUtil.US), "url-title-en"
				).put(
					_language.getLanguageId(new Locale("es", "ES")),
					"url-title-es"
				).build(),
				serviceContext);

		Assert.assertEquals(
			"cat1-en/cat2-en/url-title-en",
			friendlyURLEntry.getCategorizedUrlTitle(
				_language.getLanguageId(LocaleUtil.US)));
		Assert.assertEquals(
			"cat1-es/cat2-es/url-title-es",
			friendlyURLEntry.getCategorizedUrlTitle(
				_language.getLanguageId(new Locale("es", "ES"))));
	}

	@Test(expected = FriendlyURLCategoryException.class)
	public void testAddFriendlyURLEntryWithSlashAndAssetCategories()
		throws Exception {

		ServiceContext serviceContext = _getServiceContext();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "cat1",
			assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "cat2",
			assetVocabulary.getVocabularyId(), serviceContext);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(User.class),
			TestPropsValues.getUserId(),
			Collections.singletonMap(
				_language.getLanguageId(LocaleUtil.US), "url/title/en"),
			serviceContext);
	}

	@Test
	public void testAddUnlocalizedFriendlyURLEntryWithLocalizedAssetCategories()
		throws Exception {

		ServiceContext serviceContext = _getServiceContext();

		_addLocalizedAssetCategories(serviceContext);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(User.class),
				TestPropsValues.getUserId(),
				HashMapBuilder.put(
					_language.getLanguageId(LocaleUtil.US), "url-title-en"
				).build(),
				serviceContext);

		Assert.assertEquals(
			"cat1-en/cat2-en/url-title-en",
			friendlyURLEntry.getCategorizedUrlTitle(
				_language.getLanguageId(LocaleUtil.US)));
		Assert.assertEquals(
			"cat1-es/cat2-es/url-title-en",
			friendlyURLEntry.getCategorizedUrlTitle(
				_language.getLanguageId(new Locale("es", "ES"))));
	}

	@Test
	public void testDeleteFriendlyURLLocalizationEntry() throws Exception {
		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(User.class),
				TestPropsValues.getUserId(),
				HashMapBuilder.put(
					_language.getLanguageId(LocaleUtil.CHINA), "url-title-zh"
				).put(
					_language.getLanguageId(LocaleUtil.US), "url-title-en"
				).build(),
				_getServiceContext());

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntry.getFriendlyURLEntryId(),
			_language.getLanguageId(LocaleUtil.CHINA));

		Assert.assertNotNull(
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				friendlyURLEntry.getFriendlyURLEntryId()));

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntry.getFriendlyURLEntryId(),
			_language.getLanguageId(LocaleUtil.US));

		Assert.assertNull(
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				friendlyURLEntry.getFriendlyURLEntryId()));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testFetchFriendlyURLEntryByParentClassPKDoesNotMatchAcrossParents()
		throws Exception {

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(AssetCategory.class),
			_assetVocabulary.getVocabularyId(), _assetCategory.getCategoryId(),
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			Collections.singletonMap(
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), urlTitle),
			_getServiceContext());

		Assert.assertNull(
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(AssetCategory.class),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				urlTitle));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testFetchFriendlyURLEntryByParentClassPKReturnsEntry()
		throws Exception {

		String urlTitle = _getRandomURLTitle();

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(AssetCategory.class),
				_assetVocabulary.getVocabularyId(),
				_assetCategory.getCategoryId(),
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
				Collections.singletonMap(
					LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
					urlTitle),
				_getServiceContext());

		Assert.assertEquals(
			friendlyURLEntry,
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(AssetCategory.class),
				_assetVocabulary.getVocabularyId(), urlTitle));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testFetchFriendlyURLEntryLocalizationByLanguageIdAndParentClassPKDoesNotMatchAcrossLanguages()
		throws Exception {

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(AssetCategory.class),
			_assetVocabulary.getVocabularyId(), _assetCategory.getCategoryId(),
			_language.getLanguageId(LocaleUtil.CHINA),
			Collections.singletonMap(
				_language.getLanguageId(LocaleUtil.CHINA), urlTitle),
			_getServiceContext());

		Assert.assertNull(
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(AssetCategory.class),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_language.getLanguageId(LocaleUtil.US), urlTitle));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testFetchFriendlyURLEntryLocalizationByLanguageIdAndParentClassPKReturnsLocalization()
		throws Exception {

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(AssetCategory.class),
			_assetVocabulary.getVocabularyId(), _assetCategory.getCategoryId(),
			_language.getLanguageId(LocaleUtil.CHINA),
			Collections.singletonMap(
				_language.getLanguageId(LocaleUtil.CHINA), urlTitle),
			_getServiceContext());

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(AssetCategory.class),
				_assetVocabulary.getVocabularyId(),
				_language.getLanguageId(LocaleUtil.CHINA), urlTitle);

		Assert.assertEquals(
			_assetCategory.getCategoryId(),
			friendlyURLEntryLocalization.getClassPK());
	}

	@Test
	@TestInfo("LPD-90910")
	public void testFetchFriendlyURLEntryLocalizationByParentClassPKDoesNotMatchAcrossParents()
		throws Exception {

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(AssetCategory.class),
			_assetVocabulary.getVocabularyId(), _assetCategory.getCategoryId(),
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			Collections.singletonMap(
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), urlTitle),
			_getServiceContext());

		Assert.assertNull(
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(AssetCategory.class),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				urlTitle));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testFetchFriendlyURLEntryLocalizationByParentClassPKReturnsLocalization()
		throws Exception {

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(AssetCategory.class),
			_assetVocabulary.getVocabularyId(), _assetCategory.getCategoryId(),
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			Collections.singletonMap(
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()), urlTitle),
			_getServiceContext());

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(AssetCategory.class),
				_assetVocabulary.getVocabularyId(), urlTitle);

		Assert.assertEquals(
			_assetCategory.getCategoryId(),
			friendlyURLEntryLocalization.getClassPK());
	}

	@Test
	@TestInfo("LPD-90910")
	public void testGetUniqueUrlTitleMapGeneratesUniqueTitlesPerLocale()
		throws Exception {

		String urlTitle1 = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(User.class),
			TestPropsValues.getUserId(), urlTitle1, _getServiceContext());

		String urlTitle2 = _getRandomURLTitle();

		Map<String, String> uniqueUrlTitleMap =
			_friendlyURLEntryLocalService.getUniqueUrlTitleMap(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(User.class),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_user.getUserId(),
				HashMapBuilder.put(
					LocaleUtil.CHINA, urlTitle1
				).put(
					LocaleUtil.US, urlTitle2
				).build());

		String urlTitle3 = uniqueUrlTitleMap.get(
			_language.getLanguageId(LocaleUtil.CHINA));

		Assert.assertTrue(urlTitle3, urlTitle3.endsWith("-1"));

		Assert.assertEquals(
			urlTitle2,
			uniqueUrlTitleMap.get(_language.getLanguageId(LocaleUtil.US)));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testGetUniqueUrlTitleMapGeneratesUniqueTitlesWhenDuplicated()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(
			AssetCategory.class);
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String urlTitle1 = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId,
			_assetVocabulary.getVocabularyId(), _assetCategory.getCategoryId(),
			defaultLanguageId,
			Collections.singletonMap(defaultLanguageId, urlTitle1),
			_getServiceContext());

		Map<String, String> uniqueUrlTitleMap =
			_friendlyURLEntryLocalService.getUniqueUrlTitleMap(
				_group.getGroupId(), classNameId,
				_assetVocabulary.getVocabularyId(),
				_assetCategory.getCategoryId() + 1,
				Collections.singletonMap(LocaleUtil.US, urlTitle1));

		String urlTitle2 = uniqueUrlTitleMap.get(
			LocaleUtil.toLanguageId(LocaleUtil.US));

		Assert.assertTrue(urlTitle2, urlTitle2.endsWith("-1"));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testGetUniqueUrlTitleMapSkipsBlankTitles() throws Exception {
		String urlTitle = _getRandomURLTitle();

		Map<String, String> urlTitleMap =
			_friendlyURLEntryLocalService.getUniqueUrlTitleMap(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(User.class),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				TestPropsValues.getUserId(),
				HashMapBuilder.put(
					LocaleUtil.CHINA, StringPool.BLANK
				).put(
					LocaleUtil.US, urlTitle
				).build());

		Assert.assertEquals(urlTitleMap.toString(), 1, urlTitleMap.size());
		Assert.assertEquals(
			urlTitle, urlTitleMap.get(_language.getLanguageId(LocaleUtil.US)));
	}

	@Test
	public void testGetUniqueUrlTitleNormalizesUrlTitle() throws Exception {
		String urlTitle = "url title with spaces";

		Assert.assertEquals(
			"url-title-with-spaces",
			_friendlyURLEntryLocalService.getUniqueUrlTitle(
				_group.getGroupId(),
				_classNameLocalService.getClassNameId(User.class),
				TestPropsValues.getUserId(), urlTitle, null));
	}

	@Test
	public void testGetUniqueUrlTitleResolvesConflicts() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);
		String urlTitle = "existing-url-title";

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, _getServiceContext());

		Assert.assertEquals(
			"existing-url-title-1",
			_friendlyURLEntryLocalService.getUniqueUrlTitle(
				_group.getGroupId(), classNameId, _user.getUserId(), urlTitle,
				null));
	}

	@Test
	@TestInfo("LPD-90910")
	public void testGetUniqueUrlTitleReturnsSameTitleAcrossDifferentParentClassPK()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(
			AssetCategory.class);
		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId,
			_assetVocabulary.getVocabularyId(), _assetCategory.getCategoryId(),
			defaultLanguageId,
			Collections.singletonMap(defaultLanguageId, urlTitle),
			_getServiceContext());

		String uniqueUrlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
			_group.getGroupId(), classNameId,
			FriendlyURLEntryConstants.
				FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
			_assetCategory.getCategoryId(), urlTitle,
			_language.getLanguageId(LocaleUtil.getDefault()));

		Assert.assertEquals(urlTitle, uniqueUrlTitle);
	}

	@Test
	public void testGetUniqueUrlTitleReusesOwnedUrlTitles() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);
		String urlTitle = "existing-url-title";

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, _getServiceContext());

		String uniqueUrlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, null);

		Assert.assertEquals(urlTitle, uniqueUrlTitle);
	}

	@Test
	public void testGetUniqueUrlTitleShortensToMaxLength() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength + 1);

		String uniqueUrlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, null);

		Assert.assertEquals(maxLength, uniqueUrlTitle.length());
	}

	@Test
	public void testGetUniqueUrlTitleWithNonasciiCharsShortensToMaxLength()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength - 1);

		urlTitle += "あ";

		String uniqueUrlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, null);

		Assert.assertEquals(maxLength - 1, uniqueUrlTitle.length());
	}

	@Test
	public void testValidateAllowsDuplicatesInDifferentLanguagesForDifferentClassPK()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(User.class);

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			HashMapBuilder.put(
				_language.getLanguageId(LocaleUtil.getDefault()), urlTitle
			).build(),
			_getServiceContext());

		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(), classNameId, _user.getUserId(),
			_language.getLanguageId(LocaleUtil.BRAZIL), urlTitle);
	}

	@Test(expected = DuplicateFriendlyURLEntryException.class)
	public void testValidateDuplicateLocalizedUrlTitleForDifferentClassPK()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(User.class);

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			HashMapBuilder.put(
				_language.getLanguageId(LocaleUtil.getDefault()), urlTitle
			).build(),
			_getServiceContext());

		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(), classNameId, _user.getUserId(),
			_language.getLanguageId(LocaleUtil.getDefault()), urlTitle);
	}

	@Test
	public void testValidateDuplicateLocalizedUrlTitleForSameClassPK()
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(User.class);

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			HashMapBuilder.put(
				_language.getLanguageId(LocaleUtil.getDefault()), urlTitle
			).build(),
			_getServiceContext());

		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			_language.getLanguageId(LocaleUtil.BRAZIL), urlTitle);
	}

	@Test(expected = DuplicateFriendlyURLEntryException.class)
	public void testValidateDuplicateUrlTitle() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, _getServiceContext());

		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(), classNameId, urlTitle);
	}

	@Test(expected = DuplicateFriendlyURLEntryException.class)
	public void testValidateUrlTitleNotOwnedByModel() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, _getServiceContext());

		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(), classNameId, _user.getUserId(), urlTitle);
	}

	@Test
	public void testValidateUrlTitleOwnedByModel() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);

		String urlTitle = _getRandomURLTitle();

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle, _getServiceContext());

		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(), classNameId, TestPropsValues.getUserId(),
			urlTitle);
	}

	@Test(expected = FriendlyURLLengthException.class)
	public void testValidateUrlTitleWithInvalidLength() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(User.class);

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String urlTitle = StringUtil.randomString(maxLength + 1);

		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(), classNameId, urlTitle);
	}

	@Test(
		expected = FriendlyURLLocalizationUrlTitleException.MustNotHaveTrailingSlash.class
	)
	public void testValidateUrlTitleWithInvalidSlash() throws Exception {
		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(User.class),
			TestPropsValues.getUserId(), "test/");
	}

	@Test
	public void testValidateUrlTitleWithMaxLength() throws Exception {
		_friendlyURLEntryLocalService.validate(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(User.class),
			_getRandomURLTitle());
	}

	private void _addLocalizedAssetCategories(ServiceContext serviceContext)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			HashMapBuilder.put(
				LocaleUtil.US, "cat1-en"
			).put(
				new Locale("es", "ES"), "cat1-es"
			).build(),
			new HashMap<>(), assetVocabulary.getVocabularyId(), false, null,
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			HashMapBuilder.put(
				LocaleUtil.US, "cat2-en"
			).put(
				new Locale("es", "ES"), "cat2-es"
			).build(),
			new HashMap<>(), assetVocabulary.getVocabularyId(), false, null,
			serviceContext);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});
	}

	private String _getRandomURLTitle() {
		return StringUtil.toLowerCase(
			StringUtil.randomString(
				ModelHintsUtil.getMaxLength(
					FriendlyURLEntryLocalization.class.getName(), "urlTitle")));
	}

	private ServiceContext _getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	private AssetCategory _assetCategory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@DeleteAfterTestRun
	private User _user;

}