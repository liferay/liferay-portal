/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.publisher.test.util.AssetPublisherTestUtil;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockPortletPreferences;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests basic capabilities of the Asset Publisher and its integration with
 * Asset Categories and Tags services including
 *
 * <ul>
 * <li>
 * Adding vocabularies and categories
 * </li>
 * <li>
 * Adding asset entries
 * </li>
 * <li>
 * Searching for asset entries using category and tag filters
 * </li>
 * </ul>
 *
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class AssetPublisherServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetEntries = addAssetEntries(
			_NO_ASSET_CATEGORY_IDS, _NO_ASSET_TAG_NAMES, 5, true);
		_permissionChecker = PermissionCheckerFactoryUtil.create(
			TestPropsValues.getUser());
	}

	@Test
	public void testGetAssetEntries() throws Exception {
		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences();

		Assert.assertEquals(
			_assetEntries,
			_assetPublisherHelper.getAssetEntries(
				_getMockLiferayPortletActionRequest(), portletPreferences,
				_permissionChecker, new long[] {_group.getGroupId()}, false,
				false));
	}

	@Test
	public void testGetAssetEntriesFilteredByAssetCategoryIds()
		throws Exception {

		addAssetVocabulary();

		long[] allAssetCategoryIds = {
			_assetCategoryIds[0], _assetCategoryIds[1], _assetCategoryIds[2]
		};

		List<AssetEntry> expectedAssetEntries = addAssetEntries(
			allAssetCategoryIds, _NO_ASSET_TAG_NAMES, 2, true);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences();

		List<AssetEntry> assetEntries = _assetPublisherHelper.getAssetEntries(
			mockLiferayPortletActionRequest, portletPreferences,
			_permissionChecker, new long[] {_group.getGroupId()}, false, false);

		Assert.assertEquals(
			assetEntries.toString(),
			_assetEntries.size() + expectedAssetEntries.size(),
			assetEntries.size());

		List<AssetEntry> filteredAssetEntries =
			_assetPublisherHelper.getAssetEntries(
				_getMockLiferayPortletActionRequest(), portletPreferences,
				_permissionChecker, new long[] {_group.getGroupId()},
				allAssetCategoryIds, _NO_ASSET_TAG_NAMES, false, false);

		Assert.assertEquals(expectedAssetEntries, filteredAssetEntries);
	}

	@Test
	public void testGetAssetEntriesFilteredByAssetCategoryIdsAndAssetTagNames()
		throws Exception {

		addAssetVocabulary();

		long[] allCategoyIds = {
			_assetCategoryIds[0], _assetCategoryIds[1], _assetCategoryIds[2],
			_assetCategoryIds[3]
		};

		String[] allAssetTagNames = {_ASSET_TAG_NAMES[0], _ASSET_TAG_NAMES[1]};

		List<AssetEntry> expectedAssetEntries = addAssetEntries(
			allCategoyIds, allAssetTagNames, 2, true);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences();

		List<AssetEntry> assetEntries = _assetPublisherHelper.getAssetEntries(
			mockLiferayPortletActionRequest, portletPreferences,
			_permissionChecker, new long[] {_group.getGroupId()}, false, false);

		Assert.assertEquals(
			assetEntries.toString(),
			_assetEntries.size() + expectedAssetEntries.size(),
			assetEntries.size());

		List<AssetEntry> filteredAssetEntries =
			_assetPublisherHelper.getAssetEntries(
				mockLiferayPortletActionRequest, portletPreferences,
				_permissionChecker, new long[] {_group.getGroupId()},
				allCategoyIds, allAssetTagNames, false, false);

		Assert.assertEquals(expectedAssetEntries, filteredAssetEntries);
	}

	@Test
	public void testGetAssetEntriesFilteredByAssetTagNames() throws Exception {
		String[] allAssetTagNames = {_ASSET_TAG_NAMES[0], _ASSET_TAG_NAMES[1]};

		List<AssetEntry> expectedAssetEntries = addAssetEntries(
			_NO_ASSET_CATEGORY_IDS, allAssetTagNames, 2, true);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		PortletPreferences portletPreferences =
			getAssetPublisherPortletPreferences();

		List<AssetEntry> assetEntries = _assetPublisherHelper.getAssetEntries(
			mockLiferayPortletActionRequest, portletPreferences,
			_permissionChecker, new long[] {_group.getGroupId()}, false, false);

		Assert.assertEquals(
			assetEntries.toString(),
			_assetEntries.size() + expectedAssetEntries.size(),
			assetEntries.size());

		List<AssetEntry> filteredAssetEntries =
			_assetPublisherHelper.getAssetEntries(
				mockLiferayPortletActionRequest, portletPreferences,
				_permissionChecker, new long[] {_group.getGroupId()},
				_NO_ASSET_CATEGORY_IDS, allAssetTagNames, false, false);

		Assert.assertEquals(expectedAssetEntries, filteredAssetEntries);
	}

	protected void addAssetCategories(long vocabularyId) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		for (String assetCategoryName : _ASSET_CATEGORY_NAMES) {
			AssetCategory assetCategory =
				AssetCategoryLocalServiceUtil.addCategory(
					TestPropsValues.getUserId(),
					serviceContext.getScopeGroupId(), assetCategoryName,
					vocabularyId, serviceContext);

			_assetCategoryIds = ArrayUtil.append(
				_assetCategoryIds, assetCategory.getCategoryId());
		}
	}

	protected List<AssetEntry> addAssetEntries(
			long[] assetCategoryIds, String[] assetTagNames, int count,
			boolean manualMode)
		throws Exception {

		List<AssetEntry> assetEntries = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			JournalArticle article = JournalTestUtil.addArticle(
				_group.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(100));

			JournalArticleLocalServiceUtil.updateAsset(
				TestPropsValues.getUserId(), article, assetCategoryIds,
				assetTagNames, null, null);

			AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
				JournalArticle.class.getName(), article.getResourcePrimKey());

			assetEntries.add(assetEntry);

			if (manualMode) {
				_assetEntryXmls = ArrayUtil.append(
					_assetEntryXmls,
					AssetPublisherTestUtil.getAssetEntryXml(assetEntry));
			}
		}

		return assetEntries;
	}

	protected void addAssetVocabulary() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		AssetVocabulary assetVocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		addAssetCategories(assetVocabulary.getVocabularyId());
	}

	protected PortletPreferences getAssetPublisherPortletPreferences()
		throws Exception {

		PortletPreferences portletPreferences = new MockPortletPreferences();

		portletPreferences.setValues("assetEntryXml", _assetEntryXmls);

		return portletPreferences;
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		return ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
			_group, LayoutTestUtil.addTypePortletLayout(_group.getGroupId()));
	}

	private static final String[] _ASSET_CATEGORY_NAMES = {
		"Athletic", "Barcelona", "RealMadrid", "Sevilla", "Sporting"
	};

	private static final String[] _ASSET_TAG_NAMES = {
		"basketball", "football", "tennis"
	};

	private static final long[] _NO_ASSET_CATEGORY_IDS = new long[0];

	private static final String[] _NO_ASSET_TAG_NAMES = new String[0];

	private long[] _assetCategoryIds = new long[0];
	private List<AssetEntry> _assetEntries = new ArrayList<>();
	private String[] _assetEntryXmls = new String[0];

	@Inject
	private AssetPublisherHelper _assetPublisherHelper;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _permissionChecker;

}