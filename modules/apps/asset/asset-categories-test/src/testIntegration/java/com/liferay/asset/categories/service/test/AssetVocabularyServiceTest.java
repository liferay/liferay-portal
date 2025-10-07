/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.DuplicateVocabularyException;
import com.liferay.asset.kernel.exception.DuplicateVocabularyExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.exception.VocabularyNameException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
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
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class AssetVocabularyServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_locale = LocaleThreadLocal.getSiteDefaultLocale();
	}

	@After
	public void tearDown() throws Exception {
		LocaleThreadLocal.setSiteDefaultLocale(_locale);
	}

	@Test(expected = DuplicateVocabularyException.class)
	public void testAddDuplicateVocabulary() throws Exception {
		AssetTestUtil.addVocabulary(_group.getGroupId(), "test");
		AssetTestUtil.addVocabulary(_group.getGroupId(), "test");
	}

	@Test(expected = VocabularyNameException.class)
	public void testAddEmptyNameVocabulary() throws Exception {
		AssetTestUtil.addVocabulary(_group.getGroupId(), StringPool.BLANK);
	}

	@Test
	public void testAddVocabulary() throws Exception {
		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), RandomTestUtil.randomString());

		_testAddVocabulary(
			String.valueOf(vocabulary.getPrimaryKey()), RoleConstants.GUEST);
		_testAddVocabulary(
			String.valueOf(vocabulary.getPrimaryKey()), RoleConstants.OWNER);
		_testAddVocabulary(
			String.valueOf(vocabulary.getPrimaryKey()),
			RoleConstants.SITE_MEMBER);
	}

	@Test
	public void testAddVocabularyLongTitlesAreTrimmed() throws Exception {
		int nameMaxLength = ModelHintsUtil.getMaxLength(
			AssetVocabulary.class.getName(), "name");

		String title = RandomTestUtil.randomString(nameMaxLength);

		AssetVocabulary vocabulary = _assetVocabularyLocalService.addVocabulary(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			StringPool.BLANK, StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, title + RandomTestUtil.randomString(10)
			).put(
				LocaleUtil.US, title + RandomTestUtil.randomString(10)
			).build(),
			null, null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringUtil.toLowerCase(title.trim()), vocabulary.getName());

		_assertAssetCategoryLongTitlesAreTrimmed(vocabulary, title);
	}

	@Test
	public void testAddVocabularyWithExternalReferenceCode() throws Exception {
		String externalReferenceCode = StringUtil.randomString();

		String title = RandomTestUtil.randomString();

		String description = RandomTestUtil.randomString();

		AssetVocabulary vocabulary = _assetVocabularyLocalService.addVocabulary(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), StringPool.BLANK, StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, title + "_ES"
			).put(
				LocaleUtil.US, title + "_US"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, description + "_ES"
			).put(
				LocaleUtil.US, description + "_US"
			).build(),
			null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			externalReferenceCode, vocabulary.getExternalReferenceCode());

		vocabulary =
			_assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(
			externalReferenceCode, vocabulary.getExternalReferenceCode());
	}

	@Test
	public void testAddVocabularyWithoutExternalReferenceCode()
		throws Exception {

		AssetVocabulary vocabulary1 = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		String externalReferenceCode = vocabulary1.getExternalReferenceCode();

		Assert.assertEquals(externalReferenceCode, vocabulary1.getUuid());

		AssetVocabulary vocabulary2 =
			_assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId());

		Assert.assertEquals(vocabulary1, vocabulary2);
	}

	@Test
	public void testDeleteVocabulary() throws Exception {
		int initialAssetCategoriesCount = searchCount();
		int initialResourceActionsCount =
			ResourceActionLocalServiceUtil.getResourceActionsCount(
				AssetVocabulary.class.getName());

		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory category = AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary.getVocabularyId());

		AssetTestUtil.addCategory(
			_group.getGroupId(), vocabulary.getVocabularyId(),
			category.getCategoryId());

		Assert.assertEquals(initialAssetCategoriesCount + 2, searchCount());

		_assetVocabularyLocalService.deleteVocabulary(
			vocabulary.getVocabularyId());

		Assert.assertEquals(initialAssetCategoriesCount, searchCount());
		Assert.assertEquals(
			initialResourceActionsCount,
			ResourceActionLocalServiceUtil.getResourceActionsCount(
				AssetVocabulary.class.getName()));
		Assert.assertNull(
			_assetCategoryLocalService.fetchAssetCategory(
				category.getCategoryId()));
		Assert.assertNull(
			_assetVocabularyLocalService.fetchAssetVocabulary(
				vocabulary.getVocabularyId()));
	}

	@Test(expected = DuplicateVocabularyExternalReferenceCodeException.class)
	public void testDuplicateVocabularyExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();
		String title = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString();

		_assetVocabularyLocalService.addVocabulary(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), StringPool.BLANK, StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, title + "_ES"
			).put(
				LocaleUtil.US, title + "_US"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, description + "_ES"
			).put(
				LocaleUtil.US, description + "_US"
			).build(),
			null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_assetVocabularyLocalService.addVocabulary(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), StringPool.BLANK, StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, title + "_ES"
			).put(
				LocaleUtil.US, title + "_US"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, description + "_ES"
			).put(
				LocaleUtil.US, description + "_US"
			).build(),
			null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	public void testFetchGroupVocabulary() throws Exception {
		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		Assert.assertNotNull(
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "audience"));
		Assert.assertNotNull(
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "stage"));
		Assert.assertNotNull(
			_assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(), "topic"));

		Assert.assertNull(
			_assetVocabularyLocalService.fetchGroupVocabulary(
				_group.getGroupId(), "topic"));
	}

	@Test
	public void testGetGroupVocabulariesPaginatedWithNoViewableVocabularies()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		_assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			List<AssetVocabulary> assetVocabularies =
				_assetVocabularyService.getGroupVocabularies(
					_group.getGroupId(), false, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			Assert.assertTrue(ListUtil.isEmpty(assetVocabularies));
		}
		finally {
			UserLocalServiceUtil.deleteUser(user);
		}
	}

	@Test
	public void testGetGroupVocabulariesPaginatedWithNoViewableVocabulariesDoesNotCreateDefaultVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		_assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			List<AssetVocabulary> assetVocabularies =
				_assetVocabularyService.getGroupVocabularies(
					_group.getGroupId(), true, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			Assert.assertTrue(ListUtil.isEmpty(assetVocabularies));
		}
		finally {
			UserLocalServiceUtil.deleteUser(user);
		}
	}

	@Test
	public void testGetGroupVocabulariesPaginatedWithNoVocabularies()
		throws Exception {

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				_group.getGroupId(), false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertTrue(ListUtil.isEmpty(assetVocabularies));
	}

	@Test
	public void testGetGroupVocabulariesPaginatedWithNoVocabulariesCreatesDefaultVocabulary()
		throws Exception {

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				_group.getGroupId(), true, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			assetVocabularies.toString(), 1, assetVocabularies.size());
	}

	@Test
	public void testGetGroupVocabulariesWithNoViewableVocabularies()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		_assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			List<AssetVocabulary> assetVocabularies =
				_assetVocabularyService.getGroupVocabularies(
					_group.getGroupId(), false);

			Assert.assertTrue(ListUtil.isEmpty(assetVocabularies));
		}
		finally {
			UserLocalServiceUtil.deleteUser(user);
		}
	}

	@Test
	public void testGetGroupVocabulariesWithNoViewableVocabulariesDoesNotCreateDefaultVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		_assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		User user = UserTestUtil.addUser();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			List<AssetVocabulary> assetVocabularies =
				_assetVocabularyService.getGroupVocabularies(
					_group.getGroupId(), true);

			Assert.assertTrue(ListUtil.isEmpty(assetVocabularies));
		}
		finally {
			UserLocalServiceUtil.deleteUser(user);
		}
	}

	@Test
	public void testGetGroupVocabulariesWithNoVocabularies() throws Exception {
		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				_group.getGroupId(), false);

		Assert.assertTrue(ListUtil.isEmpty(assetVocabularies));
	}

	@Test
	public void testGetGroupVocabulariesWithNoVocabulariesCreatesDefaultVocabulary()
		throws Exception {

		List<AssetVocabulary> assetVocabularies =
			_assetVocabularyService.getGroupVocabularies(
				_group.getGroupId(), true);

		Assert.assertEquals(
			assetVocabularies.toString(), 1, assetVocabularies.size());
	}

	@Test
	public void testGetGroupVocabulary() throws Exception {
		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), "test");

		AssetVocabulary newVocabulary =
			_assetVocabularyLocalService.getGroupVocabulary(
				_group.getGroupId(), "test");

		Assert.assertEquals(
			vocabulary.getVocabularyId(), newVocabulary.getVocabularyId());
	}

	@Test
	public void testGetOrAddEmptyVocabulary() throws Exception {

		// Lazy referencing disabled

		try {
			_assetVocabularyService.getOrAddEmptyVocabulary(
				RandomTestUtil.randomString(), _group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchVocabularyException noSuchVocabularyException) {
			Assert.assertNotNull(noSuchVocabularyException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			AssetVocabulary vocabulary =
				_assetVocabularyService.getOrAddEmptyVocabulary(
					RandomTestUtil.randomString(), _group.getGroupId());

			Assert.assertNotNull(vocabulary);

			// Without permissions

			User user = UserTestUtil.addGroupUser(
				_group, RoleConstants.SITE_MEMBER);

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(user);

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, permissionChecker)) {

				_assetVocabularyService.getOrAddEmptyVocabulary(
					RandomTestUtil.randomString(), user.getGroupId());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}

			// Without permissions, existing vocabulary

			String externalReferenceCode = RandomTestUtil.randomString();

			_assetVocabularyLocalService.addVocabulary(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.SPAIN, RandomTestUtil.randomString()
				).build(),
				null, null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			vocabulary = _assetVocabularyService.getOrAddEmptyVocabulary(
				externalReferenceCode, _group.getGroupId());

			Assert.assertNotNull(vocabulary);
		}
	}

	@Test
	public void testLocalizedSiteAddDefaultVocabulary() throws Exception {
		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.SPAIN);

		AssetVocabulary vocabulary =
			_assetVocabularyLocalService.addDefaultVocabulary(
				_group.getGroupId());

		Assert.assertEquals(
			LanguageUtil.get(
				LocaleUtil.US, PropsValues.ASSET_VOCABULARY_DEFAULT),
			vocabulary.getTitle(LocaleUtil.US, true));
	}

	@Test
	public void testLocalizedSiteAddLocalizedVocabulary() throws Exception {
		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.SPAIN);

		String title = RandomTestUtil.randomString();

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.SPAIN, title + "_ES"
		).put(
			LocaleUtil.US, title + "_US"
		).build();

		String description = RandomTestUtil.randomString();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			LocaleUtil.SPAIN, description + "_ES"
		).put(
			LocaleUtil.US, description + "_US"
		).build();

		AssetVocabulary vocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(), StringPool.BLANK,
			titleMap, descriptionMap, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			StringUtil.toLowerCase(titleMap.get(LocaleUtil.SPAIN)),
			vocabulary.getName());
		Assert.assertEquals(
			titleMap.get(LocaleUtil.SPAIN),
			vocabulary.getTitle(LocaleUtil.GERMANY, true));
		Assert.assertEquals(
			titleMap.get(LocaleUtil.SPAIN),
			vocabulary.getTitle(LocaleUtil.SPAIN, true));
		Assert.assertEquals(
			titleMap.get(LocaleUtil.US),
			vocabulary.getTitle(LocaleUtil.US, true));
		Assert.assertEquals(
			descriptionMap.get(LocaleUtil.SPAIN),
			vocabulary.getDescription(LocaleUtil.GERMANY, true));
		Assert.assertEquals(
			descriptionMap.get(LocaleUtil.SPAIN),
			vocabulary.getDescription(LocaleUtil.SPAIN, true));
		Assert.assertEquals(
			descriptionMap.get(LocaleUtil.US),
			vocabulary.getDescription(LocaleUtil.US, true));
	}

	@Test
	public void testLocalizedSiteAddVocabulary() throws Exception {
		LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.SPAIN);

		String title = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		AssetVocabulary vocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			title, serviceContext);

		Assert.assertEquals(title, vocabulary.getTitle(LocaleUtil.US, true));
		Assert.assertEquals(
			StringUtil.toLowerCase(title), vocabulary.getName());
	}

	@Test
	public void testUpdateVocabularyLongTitlesAreTrimmed() throws Exception {
		String name = RandomTestUtil.randomString();

		AssetVocabulary vocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), name);

		Assert.assertEquals(
			StringUtil.toLowerCase(name.trim()), vocabulary.getName());

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			AssetVocabulary.class.getName(), "name");

		String title = RandomTestUtil.randomString(nameMaxLength);

		vocabulary = _assetVocabularyLocalService.updateVocabulary(
			vocabulary.getVocabularyId(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN, title + RandomTestUtil.randomString(10)
			).put(
				LocaleUtil.US, title + RandomTestUtil.randomString(10)
			).build(),
			null, vocabulary.getSettings(), vocabulary.getVisibilityType());

		Assert.assertEquals(
			StringUtil.toLowerCase(name.trim()), vocabulary.getName());

		_assertAssetCategoryLongTitlesAreTrimmed(vocabulary, title);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected int searchCount() throws Exception {
		Indexer<AssetCategory> indexer = IndexerRegistryUtil.getIndexer(
			AssetCategory.class);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext();

		searchContext.setGroupIds(new long[] {_group.getGroupId()});

		Hits results = indexer.search(searchContext);

		return results.getLength();
	}

	private void _assertAssetCategoryLongTitlesAreTrimmed(
		AssetVocabulary assetVocabulary, String title) {

		Map<Locale, String> titleMap = assetVocabulary.getTitleMap();

		for (Map.Entry<Locale, String> entry : titleMap.entrySet()) {
			Assert.assertEquals(title, entry.getValue());
		}
	}

	private void _testAddVocabulary(String primKey, String roleName)
		throws Exception {

		Role role = _roleLocalService.getRole(_group.getCompanyId(), roleName);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				_group.getCompanyId(), AssetVocabulary.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, primKey, role.getRoleId());

		Assert.assertTrue(resourcePermission.hasActionId(ActionKeys.VIEW));
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private AssetVocabularyService _assetVocabularyService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Locale _locale;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}