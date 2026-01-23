/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.exception.AssetTagNameException;
import com.liferay.asset.kernel.exception.DuplicateTagException;
import com.liferay.asset.kernel.exception.NoSuchTagException;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.asset.util.AssetHelper;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Michael C. Han
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class AssetTagLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test(expected = DuplicateTagException.class)
	public void testAddDuplicateTags() throws Exception {
		_addAssetTags(new String[] {"tag", "tag"});
	}

	@Test
	public void testAddMultipleTags() throws PortalException {
		_testAddMultipleTags(Arrays.asList("tag1", "tag2"));
	}

	@Test
	public void testAddMultipleTagsWithCaseSensitive() throws PortalException {
		_testAddMultipleTags(Arrays.asList("tag1", "Tag1", "tAg1", "TAG1"));
	}

	@Test
	public void testAddTag() throws PortalException {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "tag",
			_serviceContext);

		Assert.assertEquals("tag", assetTag.getName());
	}

	@Test(expected = AssetTagNameException.class)
	public void testAddTagWithEmptyName() throws Exception {
		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			StringPool.BLANK, _serviceContext);
	}

	@Test(expected = AssetTagException.class)
	public void testAddTagWithInvalidCharacters() throws Exception {
		String stringWithInvalidCharacters = String.valueOf(
			AssetHelper.INVALID_CHARACTERS);

		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			stringWithInvalidCharacters, _serviceContext);
	}

	@Test
	public void testAddTagWithMultipleWords() throws PortalException {
		AssetTag tag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "tag name",
			_serviceContext);

		Assert.assertEquals("tag name", tag.getName());
	}

	@Test(expected = AssetTagNameException.class)
	public void testAddTagWithNullName() throws Exception {
		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), null,
			_serviceContext);
	}

	@Test(expected = AssetTagNameException.class)
	public void testAddTagWithOnlySpacesInName() throws Exception {
		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			StringPool.SPACE, _serviceContext);
	}

	@Test
	public void testAddTagWithPermittedSpecialCharacter()
		throws PortalException {

		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "-_^()!$",
			_serviceContext);
	}

	@Test
	public void testAddTagWithSingleWord() throws PortalException {
		int originalTagsCount = _assetTagLocalService.getAssetTagsCount();

		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "tag",
			_serviceContext);

		Assert.assertEquals(
			originalTagsCount + 1, _assetTagLocalService.getAssetTagsCount());
	}

	@Test
	public void testAddUTF8FormattedTags() throws PortalException {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "標籤名稱",
			_serviceContext);

		Assert.assertEquals("標籤名稱", assetTag.getName());
	}

	@Test
	public void testCheckTagsWithCaseSensitive() throws PortalException {
		String[] tagNames = {"TAG1", "tAG1", "TAG1 duplicate"};

		List<AssetTag> assetTags = _assetTagLocalService.checkTags(
			TestPropsValues.getUserId(), _group, tagNames);

		Assert.assertEquals(
			assetTags.toString(), tagNames.length, assetTags.size());

		for (int i = 0; i < assetTags.size(); i++) {
			AssetTag assetTag = assetTags.get(i);

			Assert.assertEquals(_group.getGroupId(), assetTag.getGroupId());
			Assert.assertEquals(tagNames[i], assetTag.getName());
			Assert.assertEquals(
				TestPropsValues.getUserId(), assetTag.getUserId());
		}
	}

	@Test
	public void testDeleteTag() throws Exception {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "Tag",
			_serviceContext);

		_serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		_organization = OrganizationLocalServiceUtil.addOrganization(
			null, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(),
			OrganizationConstants.TYPE_ORGANIZATION, 0, 0,
			_listTypeLocalService.getListTypeId(
				assetTag.getCompanyId(),
				ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
				ListTypeConstants.ORGANIZATION_STATUS),
			RandomTestUtil.randomString(), true, _serviceContext);

		TestAssetIndexer testAssetIndexer = new TestAssetIndexer();

		testAssetIndexer.setExpectedValues(
			Organization.class.getName(), _organization.getOrganizationId());

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				Indexer.class, testAssetIndexer,
				MapUtil.singletonDictionary(
					"service.ranking", Integer.MAX_VALUE));

		try {
			_assetTagLocalService.deleteTag(assetTag);

			Assert.assertNull(
				_assetTagLocalService.fetchAssetTag(assetTag.getTagId()));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testFetchTagWithCaseSensitive() throws PortalException {
		List<AssetTag> assetTags = _addAssetTags(new String[] {"tag", "TAG"});

		for (AssetTag assetTag : assetTags) {
			AssetTag actualAssetTag = _assetTagLocalService.fetchTag(
				_group.getGroupId(), assetTag.getName());

			Assert.assertNotNull(actualAssetTag);
			Assert.assertEquals(assetTag.getTagId(), actualAssetTag.getTagId());
		}

		Assert.assertNull(
			_assetTagLocalService.fetchTag(_group.getGroupId(), "Tag"));
	}

	@Test
	public void testGetTagIdsFilterByGroupIdWithCaseSensitive()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		try {
			List<AssetTag> assetTags = _addAssetTags(
				new String[] {"tAg1", "TAG1"});

			_assetTagLocalService.addTag(
				null, TestPropsValues.getUserId(), group.getGroupId(), "tAg1",
				_serviceContext);

			for (AssetTag assetTag : assetTags) {
				Assert.assertArrayEquals(
					new long[] {assetTag.getTagId()},
					_assetTagLocalService.getTagIds(
						new long[] {_group.getGroupId()}, assetTag.getName()));
			}

			Assert.assertArrayEquals(
				new long[0],
				_assetTagLocalService.getTagIds(
					new long[] {group.getGroupId()}, "TAG1"));
		}
		finally {
			GroupTestUtil.deleteGroup(group);
		}
	}

	@Test
	public void testGetTagIdsWithCaseSensitive() throws Exception {
		Group group = GroupTestUtil.addGroup();

		try {
			AssetTag expectedAssetTag1 = _assetTagLocalService.addTag(
				null, TestPropsValues.getUserId(), _group.getGroupId(), "tAg1",
				_serviceContext);
			AssetTag expectedAssetTag2 = _assetTagLocalService.addTag(
				null, TestPropsValues.getUserId(), group.getGroupId(), "tAg1",
				_serviceContext);
			AssetTag expectedAssetTag3 = _assetTagLocalService.addTag(
				null, TestPropsValues.getUserId(), _group.getGroupId(), "TAG1",
				_serviceContext);

			Assert.assertTrue(
				ArrayUtil.containsAll(
					_assetTagLocalService.getTagIds("tAg1"),
					new long[] {
						expectedAssetTag1.getTagId(),
						expectedAssetTag2.getTagId()
					}));
			Assert.assertArrayEquals(
				new long[] {expectedAssetTag3.getTagId()},
				_assetTagLocalService.getTagIds("TAG1"));
			Assert.assertArrayEquals(
				new long[0], _assetTagLocalService.getTagIds("Tag1"));
		}
		finally {
			GroupTestUtil.deleteGroup(group);
		}
	}

	@Test
	public void testGetTagSizeWithCaseInsensitive() throws Exception {
		String[] tagNames = {"tag1", "Tag1"};

		_addJournalArticle(tagNames);
		_addJournalArticle(tagNames);

		_addJournalArticle(new String[] {"TAG1"});

		long classNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class.getName());

		Assert.assertEquals(
			2,
			_assetTagLocalService.getTagsSize(
				_group.getGroupId(), classNameId, "tag1"));
		Assert.assertEquals(
			2,
			_assetTagLocalService.getTagsSize(
				_group.getGroupId(), classNameId, "Tag1"));
		Assert.assertEquals(
			1,
			_assetTagLocalService.getTagsSize(
				_group.getGroupId(), classNameId, "TAG1"));
	}

	@Test
	public void testGetTagsWithCaseInsensitive() throws Exception {
		String[] expectedTagNames = {"tag1", "Tag1"};

		_addAssetTags(expectedTagNames);

		Arrays.sort(expectedTagNames);

		_addBlogsEntry(expectedTagNames);
		_addJournalArticle(expectedTagNames);

		_assertGetTags(
			BlogsEntry.class.getName(), expectedTagNames.length,
			expectedTagNames, "tag1");
		_assertGetTags(
			BlogsEntry.class.getName(), expectedTagNames.length,
			expectedTagNames, "TAG1");
		_assertGetTags(
			BlogsEntry.class.getName(), 1, expectedTagNames, "TAG1", 0, 1);
		_assertGetTags(
			BlogsEntry.class.getName(), 1, expectedTagNames, "Tag1", 0, 1);
		_assertGetTags(
			JournalArticle.class.getName(), expectedTagNames.length,
			expectedTagNames, "tag1");
		_assertGetTags(
			JournalArticle.class.getName(), expectedTagNames.length,
			expectedTagNames, "TAG1");
		_assertGetTags(
			JournalArticle.class.getName(), 1, expectedTagNames, "TAG1", 0, 1);
		_assertGetTags(
			JournalArticle.class.getName(), 1, expectedTagNames, "Tag1", 0, 1);
	}

	@Test
	public void testGetTagsWithDifferentGroups() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		long classNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class.getName());

		try {
			_assetTagLocalService.addTag(
				null, TestPropsValues.getUserId(), group1.getGroupId(), "tag1",
				_serviceContext);
			_assetTagLocalService.addTag(
				null, TestPropsValues.getUserId(), group2.getGroupId(), "tag2",
				_serviceContext);

			List<AssetTag> assetTags = _assetTagLocalService.getTags(
				group1.getGroupId(), classNameId, null);

			Assert.assertEquals(assetTags.toString(), 1, assetTags.size());
		}
		finally {
			GroupTestUtil.deleteGroup(group1);
			GroupTestUtil.deleteGroup(group2);
		}
	}

	@Test
	public void testGetTagWithCaseSensitive() throws PortalException {
		List<AssetTag> assetTags = _addAssetTags(new String[] {"tag", "TAG"});

		for (AssetTag assetTag : assetTags) {
			AssetTag actualAssetTag = _assetTagLocalService.getTag(
				_group.getGroupId(), assetTag.getName());

			Assert.assertNotNull(actualAssetTag);
			Assert.assertEquals(assetTag.getTagId(), actualAssetTag.getTagId());
		}

		expectedException.expect(NoSuchTagException.class);

		_assetTagLocalService.getTag(_group.getGroupId(), "Tag");
	}

	@Test
	public void testIncrementAssetCountWhenUpdatingAssetEntry()
		throws PortalException {

		_testIncrementAssetCountWhenUpdatingAssetEntry(new String[] {"tag1"});
	}

	@Test
	public void testIncrementAssetCountWhenUpdatingAssetEntryWithCaseSensitive()
		throws PortalException {

		_testIncrementAssetCountWhenUpdatingAssetEntry(
			new String[] {"tag1", "Tag1", "TAG1"});
	}

	@Test(expected = AssetTagException.class)
	public void testIncrementAssetCountWithAssetTagNameGreaterThan75()
		throws PortalException {

		_assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(100), _serviceContext);
	}

	@Test
	public void testSearchTagsWithCaseInsensitive() throws PortalException {
		String[] tagNames = {"tag1", "Tag1", "TAG1"};

		_addAssetTags(tagNames);

		for (String tagName : tagNames) {
			BaseModelSearchResult<AssetTag> baseModelSearchResult =
				_assetTagLocalService.searchTags(
					new long[] {_group.getGroupId()}, tagName,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertNotNull(baseModelSearchResult);

			Assert.assertEquals(3, baseModelSearchResult.getLength());

			Assert.assertTrue(
				ArrayUtil.containsAll(
					TransformUtil.transformToArray(
						baseModelSearchResult.getBaseModels(),
						AssetTag::getName, String.class),
					tagNames));
		}
	}

	@Test
	public void testSearchWithCaseInsensitive() throws PortalException {
		String[] tagNames = {"tag1", "Tag1", "TAG1"};

		_addAssetTags(tagNames);

		for (String tagName : tagNames) {
			List<AssetTag> assetTags = _assetTagLocalService.search(
				new long[] {_group.getGroupId()}, tagName, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

			Assert.assertEquals(assetTags.toString(), 3, assetTags.size());

			Assert.assertTrue(
				ArrayUtil.containsAll(
					TransformUtil.transformToArray(
						assetTags, AssetTag::getName, String.class),
					tagNames));
		}
	}

	@Test
	public void testUpdateTagWithCaseSensitive() throws PortalException {
		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(), "tag1",
			_serviceContext);

		String tagName = "updated TAG1";

		AssetTag actualAssetTag = _assetTagLocalService.updateTag(
			assetTag.getExternalReferenceCode(), TestPropsValues.getUserId(),
			assetTag.getTagId(), tagName, _serviceContext);

		Assert.assertEquals(tagName, actualAssetTag.getName());
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private List<AssetTag> _addAssetTags(String[] tagNames)
		throws PortalException {

		List<AssetTag> assetTags = new ArrayList<>();

		for (String tagName : tagNames) {
			assetTags.add(
				_assetTagLocalService.addTag(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					tagName, _serviceContext));
		}

		return assetTags;
	}

	private void _addBlogsEntry(String[] tagNames) throws Exception {
		_serviceContext.setAssetTagNames(tagNames);

		BlogsTestUtil.addEntryWithWorkflow(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), true,
			_serviceContext);
	}

	private void _addJournalArticle(String[] tagNames) throws Exception {
		_serviceContext.setAssetTagNames(tagNames);

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, _serviceContext);
	}

	private void _assertGetTags(
		String className, long expectedLength, String[] expectedTagNames,
		String name) {

		_assertGetTags(
			className, expectedLength, expectedTagNames, name,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	private void _assertGetTags(
		String className, long expectedLength, String[] expectedTagNames,
		String name, int start, int end) {

		List<AssetTag> actualAssetTags = _assetTagLocalService.getTags(
			_group.getGroupId(),
			_classNameLocalService.getClassNameId(className), name, start, end);

		Assert.assertEquals(
			actualAssetTags.toString(), expectedLength, actualAssetTags.size());
		Assert.assertTrue(
			ArrayUtil.containsAll(
				expectedTagNames,
				TransformUtil.transformToArray(
					actualAssetTags, AssetTag::getName, String.class)));
	}

	private void _testAddMultipleTags(List<String> tagNames)
		throws PortalException {

		int originalTagsCount = _assetTagLocalService.getAssetTagsCount();

		for (String tagName : tagNames) {
			AssetTag assetTag = _assetTagLocalService.addTag(
				null, TestPropsValues.getUserId(), _group.getGroupId(), tagName,
				_serviceContext);

			Assert.assertEquals(tagName, assetTag.getName());
		}

		int actualTagsCount = _assetTagLocalService.getAssetTagsCount();

		Assert.assertEquals(
			originalTagsCount + tagNames.size(), actualTagsCount);
	}

	private void _testIncrementAssetCountWhenUpdatingAssetEntry(
			String[] tagNames)
		throws PortalException {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		assetEntry = _assetEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), assetEntry.getGroupId(),
			assetEntry.getClassName(), assetEntry.getClassPK(), null, tagNames);

		List<AssetTag> assetTags = assetEntry.getTags();

		Assert.assertEquals(
			TransformUtil.transform(
				assetTags, AssetTag::getName
			).toString(),
			tagNames.length, assetTags.size());

		for (AssetTag assetTag : assetEntry.getTags()) {
			Assert.assertEquals(1, assetTag.getAssetCount());
		}
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@DeleteAfterTestRun
	private Organization _organization;

	private ServiceContext _serviceContext;

}