/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.configuration.FragmentEntryVersionConfiguration;
import com.liferay.fragment.configuration.FragmentServiceConfiguration;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.exception.DuplicateFragmentEntryExternalReferenceCodeException;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.fragment.util.comparator.FragmentEntryCreateDateComparator;
import com.liferay.fragment.util.comparator.FragmentEntryNameComparator;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());

		_updatedFragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());
	}

	@Test
	public void testAddFragmentEntry() throws Exception {
		_testAddFragmentEntry();
		_testAddFragmentEntryWithExistingExternalReferenceCode();
		_testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsConfigurationPreviewFileEntryIdTypeAndStatus();
		_testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsPreviewFileEntryIdAndStatus();
		_testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsPreviewFileEntryIdTypeAndStatus();
		_testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsTypeAndStatus();
		_testAddFragmentEntryWithFragmentEntryKeyNamePreviewFileEntryIdTypeAndStatus();
		_testAddFragmentEntryWithHtmlWithAmpersand();
		_testAddFragmentEntryWithInputType();
		_testAddFragmentEntryWithNameCssHtmlJsAndStatus();
		_testAddFragmentEntryWithNameCssHtmlJsPreviewFileEntryIdAndStatus();
		_testAddFragmentEntryWithNameCssHtmlJsPreviewFileEntryIdTypeAndStatus();
		_testAddFragmentEntryWithNameCssHtmlJsTypeAndStatus();
	}

	@Test
	public void testCopyFragmentEntry() throws Exception {
		_testCopyFragmentEntry();
		_testCopyFragmentEntryToDifferentCollection();
		_testCopyFragmentEntryWithInputType();
	}

	@Test
	public void testDeleteFragmentEntry() throws Exception {
		_testDeleteFragmentEntry();
		_testDeleteFragmentEntryByExternalReferenceCode();
		_testDeleteFragmentEntryDraftByExternalReferenceCode();
		_testDeleteFragmentEntryNonexistentByExternalReferenceCode();
	}

	@Test
	public void testFetchFragmentEntry() throws Exception {
		_testFetchFragmentEntryByFragmentEntryId();
		_testFetchFragmentEntryByGroupIdAndFragmentEntryKey();
	}

	@Test
	public void testGenerateFragmentEntryKey() throws Exception {
		_fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), "test-key",
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			"{fieldSets: []}", null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		String fragmentEntryKey =
			_fragmentEntryLocalService.generateFragmentEntryKey(
				_group.getGroupId(), "Test Key");

		Assert.assertEquals("test-key-0", fragmentEntryKey);
	}

	@Test
	public void testGetFragmentEntries() throws Exception {
		_testGetFragmentEntriesByFragmentCollectionId();
		_testGetFragmentEntriesByGroupIdAndFragmentCollectionIdOrderByCreateDateComparator();
		_testGetFragmentEntriesByGroupIdAndFragmentCollectionIdOrderByNameComparator();
		_testGetFragmentEntriesByGroupIdFragmentCollectionIdAndNameOrderByCreateDateComparator();
		_testGetFragmentEntriesByGroupIdFragmentCollectionIdAndNameOrderByNameComparator();
		_testGetFragmentEntriesByGroupIdFragmentCollectionIdAndStatus();
	}

	@Test
	public void testGetFragmentEntriesByUuidAndCompanyId() throws Exception {
		Group group = GroupTestUtil.addGroup();

		FragmentEntry fragmentEntry1 = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		List<FragmentEntry> fragmentEntries1 =
			_fragmentEntryLocalService.getFragmentEntriesByUuidAndCompanyId(
				fragmentEntry1.getUuid(), _group.getCompanyId());

		Assert.assertEquals(
			fragmentEntries1.toString(), 1, fragmentEntries1.size());
		Assert.assertEquals(fragmentEntry1, fragmentEntries1.get(0));

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(group.getGroupId());

		FragmentEntry fragmentEntry2 = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		List<FragmentEntry> fragmentEntries2 =
			_fragmentEntryLocalService.getFragmentEntriesByUuidAndCompanyId(
				fragmentEntry2.getUuid(), group.getCompanyId());

		Assert.assertEquals(
			fragmentEntries2.toString(), 1, fragmentEntries2.size());
		Assert.assertEquals(fragmentEntry2, fragmentEntries2.get(0));

		GroupLocalServiceUtil.deleteGroup(group);
	}

	@Test
	public void testGetFragmentEntriesCount() throws Exception {
		int originalCount = _fragmentEntryLocalService.getFragmentEntriesCount(
			_fragmentCollection.getFragmentCollectionId());

		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());
		FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		int actualCount = _fragmentEntryLocalService.getFragmentEntriesCount(
			_fragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(originalCount + 2, actualCount);
	}

	@Test
	public void testMoveFragmentEntry() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		FragmentCollection targetFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		fragmentEntry = _fragmentEntryLocalService.moveFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			targetFragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			targetFragmentCollection.getFragmentCollectionId(),
			fragmentEntry.getFragmentCollectionId());
	}

	@Test
	@TestInfo({"LPD-75909", "LPD-98882"})
	public void testUpdateFragmentEntry() throws Exception {
		_testUpdateFragmentEntryFragmentCollectionId();
		_testUpdateFragmentEntryName();
		_testUpdateFragmentEntryNameCssHtmlJsConfigurationAndStatus();
		_testUpdateFragmentEntryNameCssHtmlJsConfigurationPreviewFileEntryIdAndStatus();
		_testUpdateFragmentEntryPreviewFileEntryId();
		_testUpdateFragmentEntryVersions();
		_testUpdateFragmentEntryVersionsWithMaximumVersionsPerEntryDisabled();
		_testUpdateFragmentEntryWithCacheable();
		_testUpdateFragmentEntryWithHtmlWithAmpersand();
		_testUpdateFragmentEntryWithPreviewFileEntryId();
		_testUpdateFragmentEntryWithPropagateChanges();
	}

	private DepotEntry _addDesignLibraryDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
	}

	private FragmentEntry _addFragmentEntry(long groupId) throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(groupId);

		return FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
	}

	private FragmentEntryLink _addFragmentEntryLink(
			FragmentEntry fragmentEntry, Layout layout,
			long segmentsExperienceId)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					fragmentEntry.getGroupId(), layout.getGroupId()),
				fragmentEntry.getHtml(), fragmentEntry.getJs(), layout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				null, 0, segmentsExperienceId);

		Assert.assertEquals(
			fragmentEntry.getHtml(), fragmentEntryLink.getHtml());

		return fragmentEntryLink;
	}

	private void _assertCopyFragmentEntry(
		FragmentEntry fragmentEntry, FragmentEntry copyFragmentEntry) {

		Assert.assertEquals(
			fragmentEntry.getGroupId(), copyFragmentEntry.getGroupId());
		Assert.assertEquals(
			fragmentEntry.getName() + " (Copy)", copyFragmentEntry.getName());
		Assert.assertEquals(fragmentEntry.getCss(), copyFragmentEntry.getCss());
		Assert.assertEquals(
			fragmentEntry.getHtml(), copyFragmentEntry.getHtml());
		Assert.assertEquals(fragmentEntry.getJs(), copyFragmentEntry.getJs());
		Assert.assertEquals(
			fragmentEntry.getStatus(), copyFragmentEntry.getStatus());
		Assert.assertEquals(
			fragmentEntry.getType(), copyFragmentEntry.getType());
	}

	private void _assertFragmentEntryLinksHTML(
		String expectedHtml, long... fragmentEntryLinkIds) {

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLinkId);

			Assert.assertEquals(expectedHtml, fragmentEntryLink.getHtml());
		}
	}

	private void _assertUpdateFragmentEntryVersions(
		FragmentEntry fragmentEntry, String expectedName, int expectedSize) {

		List<FragmentEntryVersion> fragmentEntryVersions =
			_fragmentEntryLocalService.getVersions(fragmentEntry);

		Assert.assertEquals(
			fragmentEntryVersions.toString(), expectedSize,
			fragmentEntryVersions.size());

		if (expectedName != null) {
			for (FragmentEntryVersion fragmentEntryVersion :
					fragmentEntryVersions) {

				Assert.assertEquals(
					expectedName, fragmentEntryVersion.getName());
			}
		}
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _testAddFragmentEntry() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertTrue(
			Validator.isNotNull(fragmentEntry.getExternalReferenceCode()));
	}

	private void _testAddFragmentEntryWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_fragmentEntryLocalService.addFragmentEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertThrows(
			DuplicateFragmentEntryExternalReferenceCodeException.class,
			() -> _fragmentEntryLocalService.addFragmentEntry(
				externalReferenceCode, TestPropsValues.getUserId(),
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId())));
	}

	private void _testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsConfigurationPreviewFileEntryIdTypeAndStatus()
		throws Exception {

		String fragmentEntryKey = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		String configuration = _read("configuration-valid-complete.json");
		long previewFileEntryId = RandomTestUtil.randomLong();
		int type = FragmentConstants.TYPE_COMPONENT;
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), fragmentEntryKey,
				name, css, html, js, false, configuration, null,
				previewFileEntryId, false, false, type, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringUtil.toLowerCase(fragmentEntryKey),
			fragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(configuration, fragmentEntry.getConfiguration());
		Assert.assertEquals(
			previewFileEntryId, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(type, fragmentEntry.getType());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsPreviewFileEntryIdAndStatus()
		throws Exception {

		String fragmentEntryKey = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		long previewFileEntryId = RandomTestUtil.randomLong();
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), fragmentEntryKey,
				name, css, html, js, false, StringPool.BLANK, null,
				previewFileEntryId, false, false,
				FragmentConstants.TYPE_COMPONENT, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringUtil.toLowerCase(fragmentEntryKey),
			fragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(
			previewFileEntryId, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsPreviewFileEntryIdTypeAndStatus()
		throws Exception {

		String fragmentEntryKey = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		long previewFileEntryId = RandomTestUtil.randomLong();
		int type = FragmentConstants.TYPE_COMPONENT;
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), fragmentEntryKey,
				name, css, html, js, false, StringPool.BLANK, null,
				previewFileEntryId, false, false, type, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringUtil.toLowerCase(fragmentEntryKey),
			fragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(
			previewFileEntryId, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(type, fragmentEntry.getType());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithFragmentEntryKeyNameCssHtmlJsTypeAndStatus()
		throws Exception {

		String fragmentEntryKey = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		int type = FragmentConstants.TYPE_COMPONENT;
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), fragmentEntryKey,
				name, css, html, js, false, StringPool.BLANK, null, 0, false,
				false, type, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringUtil.toLowerCase(fragmentEntryKey),
			fragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(type, fragmentEntry.getType());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithFragmentEntryKeyNamePreviewFileEntryIdTypeAndStatus()
		throws Exception {

		String fragmentEntryKey = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		long previewFileEntryId = RandomTestUtil.randomLong();
		int type = FragmentConstants.TYPE_COMPONENT;
		int status = WorkflowConstants.STATUS_DRAFT;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), fragmentEntryKey,
				name, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				false, StringPool.BLANK, null, previewFileEntryId, false, false,
				type, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(
			StringUtil.toLowerCase(fragmentEntryKey),
			fragmentEntry.getFragmentEntryKey());
		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(
			previewFileEntryId, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(type, fragmentEntry.getType());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithHtmlWithAmpersand() throws Exception {
		String html = "<H1>A&B&amp;C</H1>";

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK, html, StringPool.BLANK, false,
				StringPool.BLANK, null, RandomTestUtil.randomLong(), false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(html, fragmentEntry.getHtml());
	}

	private void _testAddFragmentEntryWithInputType() throws Exception {
		String fragmentEntryKey = RandomTestUtil.randomString();

		String typeOptions = JSONUtil.put(
			"fieldTypes", JSONUtil.put("string")
		).toString();

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), fragmentEntryKey,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, "{fieldSets: []}", null, 0, false, false,
				FragmentConstants.TYPE_INPUT, typeOptions,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(typeOptions, fragmentEntry.getTypeOptions());
	}

	private void _testAddFragmentEntryWithNameCssHtmlJsAndStatus()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), name, css, html, js, false,
				StringPool.BLANK, null, RandomTestUtil.randomLong(), false,
				false, FragmentConstants.TYPE_COMPONENT, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithNameCssHtmlJsPreviewFileEntryIdAndStatus()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		long previewFileEntryId = RandomTestUtil.randomLong();
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), name, css, html, js, false,
				StringPool.BLANK, null, previewFileEntryId, false, false,
				FragmentConstants.TYPE_COMPONENT, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(
			previewFileEntryId, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithNameCssHtmlJsPreviewFileEntryIdTypeAndStatus()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		long previewFileEntryId = RandomTestUtil.randomLong();
		int type = FragmentConstants.TYPE_COMPONENT;
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), name, css, html, js, false,
				StringPool.BLANK, null, previewFileEntryId, false, false,
				FragmentConstants.TYPE_COMPONENT, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(
			previewFileEntryId, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(type, fragmentEntry.getType());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testAddFragmentEntryWithNameCssHtmlJsTypeAndStatus()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		int type = FragmentConstants.TYPE_COMPONENT;
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), name, css, html, js, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null, status,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(type, fragmentEntry.getType());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testCopyFragmentEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntry copyFragmentEntry =
			_fragmentEntryLocalService.copyFragmentEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentEntry.getFragmentEntryId(),
				fragmentEntry.getFragmentCollectionId(), serviceContext);

		_assertCopyFragmentEntry(fragmentEntry, copyFragmentEntry);

		Assert.assertEquals(
			fragmentEntry.getFragmentCollectionId(),
			copyFragmentEntry.getFragmentCollectionId());
		Assert.assertEquals(
			StringBundler.concat(
				fragmentEntry.getName(), " (",
				_language.get(LocaleUtil.getSiteDefault(), "copy"), ")"),
			copyFragmentEntry.getName());
	}

	private void _testCopyFragmentEntryToDifferentCollection()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection targetFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntry copyFragmentEntry =
			_fragmentEntryLocalService.copyFragmentEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentEntry.getFragmentEntryId(),
				targetFragmentCollection.getFragmentCollectionId(),
				serviceContext);

		_assertCopyFragmentEntry(fragmentEntry, copyFragmentEntry);

		Assert.assertEquals(
			targetFragmentCollection.getFragmentCollectionId(),
			copyFragmentEntry.getFragmentCollectionId());
	}

	private void _testCopyFragmentEntryWithInputType() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String typeOptions = JSONUtil.put(
			"fieldTypes", JSONUtil.put("string")
		).toString();

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_INPUT, typeOptions,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		FragmentEntry copyFragmentEntry =
			_fragmentEntryLocalService.copyFragmentEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentEntry.getFragmentEntryId(),
				fragmentEntry.getFragmentCollectionId(), serviceContext);

		_assertCopyFragmentEntry(fragmentEntry, copyFragmentEntry);

		Assert.assertEquals(
			fragmentEntry.getFragmentCollectionId(),
			copyFragmentEntry.getFragmentCollectionId());

		Assert.assertEquals(typeOptions, copyFragmentEntry.getTypeOptions());
	}

	private void _testDeleteFragmentEntry() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		_fragmentEntryLocalService.deleteFragmentEntry(
			fragmentEntry.getFragmentEntryId());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));

		fragmentEntry = FragmentEntryTestUtil.addFragmentEntryByStatus(
			_fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_APPROVED);

		FragmentEntry draftFragmentEntry = _fragmentEntryLocalService.getDraft(
			fragmentEntry.getFragmentEntryId());

		_fragmentEntryLocalService.deleteFragmentEntry(
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getGroupId());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				draftFragmentEntry.getFragmentEntryId()));
	}

	private void _testDeleteFragmentEntryByExternalReferenceCode()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_fragmentEntryLocalService.deleteFragmentEntry(
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getGroupId());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));
	}

	private void _testDeleteFragmentEntryDraftByExternalReferenceCode()
		throws Exception {

		FragmentEntry fragmentEntry =
			FragmentEntryTestUtil.addFragmentEntryByStatus(
				_fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_DRAFT);

		_fragmentEntryLocalService.deleteFragmentEntry(
			fragmentEntry.getExternalReferenceCode(),
			fragmentEntry.getGroupId());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));
	}

	private void _testDeleteFragmentEntryNonexistentByExternalReferenceCode()
		throws Exception {

		Assert.assertThrows(
			NoSuchEntryException.class,
			() -> _fragmentEntryLocalService.deleteFragmentEntry(
				RandomTestUtil.randomString(), _group.getGroupId()));
	}

	private void _testFetchFragmentEntryByFragmentEntryId() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		Assert.assertNotNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));
	}

	private void _testFetchFragmentEntryByGroupIdAndFragmentEntryKey()
		throws Exception {

		String fragmentEntryKey = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(), fragmentEntryKey,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, "{fieldSets: []}", null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		Assert.assertEquals(fragmentEntry, fragmentEntry);
	}

	private void _testGetFragmentEntriesByFragmentCollectionId()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		List<FragmentEntry> originalFragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId());

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());

		List<FragmentEntry> actualFragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				fragmentCollection.getFragmentCollectionId());

		Assert.assertEquals(
			actualFragmentEntries.toString(),
			originalFragmentEntries.size() + 2, actualFragmentEntries.size());
	}

	private void _testGetFragmentEntriesByGroupIdAndFragmentCollectionIdOrderByCreateDateComparator()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		LocalDateTime localDateTime = LocalDateTime.now();

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry",
			Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AA Fragment Entry",
			Timestamp.valueOf(localDateTime));

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentEntryCreateDateComparator.getInstance(true));

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		fragmentEntries = _fragmentEntryLocalService.getFragmentEntries(
			_group.getGroupId(), fragmentCollection.getFragmentCollectionId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			FragmentEntryCreateDateComparator.getInstance(false));

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	private void _testGetFragmentEntriesByGroupIdAndFragmentCollectionIdOrderByNameComparator()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry");

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AA Fragment Entry");

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				FragmentEntryNameComparator.getInstance(true));

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());

		fragmentEntries = _fragmentEntryLocalService.getFragmentEntries(
			_group.getGroupId(), fragmentCollection.getFragmentCollectionId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			FragmentEntryNameComparator.getInstance(false));

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());
	}

	private void _testGetFragmentEntriesByGroupIdFragmentCollectionIdAndNameOrderByCreateDateComparator()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		LocalDateTime localDateTime = LocalDateTime.now();

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry",
			Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AA Fragment",
			Timestamp.valueOf(localDateTime));

		localDateTime = localDateTime.plus(1, ChronoUnit.SECONDS);

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry",
			Timestamp.valueOf(localDateTime));

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), "Entry",
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				FragmentEntryCreateDateComparator.getInstance(true));

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		fragmentEntries = _fragmentEntryLocalService.getFragmentEntries(
			_group.getGroupId(), fragmentCollection.getFragmentCollectionId(),
			"Entry", QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			FragmentEntryCreateDateComparator.getInstance(false));

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	private void _testGetFragmentEntriesByGroupIdFragmentCollectionIdAndNameOrderByNameComparator()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AB Fragment Entry");

		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AA Fragment");
		FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId(), "AC Fragment Entry");

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), "Entry",
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				FragmentEntryNameComparator.getInstance(true));

		FragmentEntry firstFragmentEntry = fragmentEntries.get(0);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			firstFragmentEntry.getName());

		fragmentEntries = _fragmentEntryLocalService.getFragmentEntries(
			_group.getGroupId(), fragmentCollection.getFragmentCollectionId(),
			"Entry", QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			FragmentEntryNameComparator.getInstance(false));

		FragmentEntry lastFragmentEntry = fragmentEntries.get(
			fragmentEntries.size() - 1);

		Assert.assertEquals(
			fragmentEntries.toString(), fragmentEntry.getName(),
			lastFragmentEntry.getName());
	}

	private void _testGetFragmentEntriesByGroupIdFragmentCollectionIdAndStatus()
		throws Exception {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		List<FragmentEntry> originalFragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_DRAFT);

		FragmentEntryTestUtil.addFragmentEntryByStatus(
			fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_APPROVED);
		FragmentEntryTestUtil.addFragmentEntryByStatus(
			fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_DRAFT);
		FragmentEntryTestUtil.addFragmentEntryByStatus(
			fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_DRAFT);

		List<FragmentEntry> actualFragmentEntries =
			_fragmentEntryLocalService.getFragmentEntries(
				_group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(
			actualFragmentEntries.toString(),
			originalFragmentEntries.size() + 2, actualFragmentEntries.size());
	}

	private void _testUpdateFragmentEntryFragmentCollectionId()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), "<H1>A</H1>",
				RandomTestUtil.randomString(), false, StringPool.BLANK, null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getUserId(), fragmentEntry.getFragmentEntryId(),
			_updatedFragmentCollection.getFragmentCollectionId(),
			fragmentEntry.getName(), fragmentEntry.getCss(),
			fragmentEntry.getHtml(), fragmentEntry.getJs(), false, null,
			fragmentEntry.getIcon(), fragmentEntry.getPreviewFileEntryId(),
			false, fragmentEntry.getTypeOptions(),
			WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(
			_updatedFragmentCollection.getFragmentCollectionId(),
			fragmentEntry.getFragmentCollectionId());
	}

	private void _testUpdateFragmentEntryName() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString());

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), "Fragment Name Updated");

		Assert.assertEquals("Fragment Name Updated", fragmentEntry.getName());
	}

	private void _testUpdateFragmentEntryNameCssHtmlJsConfigurationAndStatus()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		String configuration = _read("configuration-valid-complete.json");
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), name, css, html, js,
			fragmentEntry.isCacheable(), configuration, fragmentEntry.getIcon(),
			0, false, fragmentEntry.getTypeOptions(), status);

		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(configuration, fragmentEntry.getConfiguration());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testUpdateFragmentEntryNameCssHtmlJsConfigurationPreviewFileEntryIdAndStatus()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String css = RandomTestUtil.randomString();
		String html = RandomTestUtil.randomString();
		String js = RandomTestUtil.randomString();
		String configuration = _read("configuration-valid-complete.json");
		long previewFileEntryId = RandomTestUtil.randomLong();
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), name, css, html, js,
			fragmentEntry.isCacheable(), configuration, fragmentEntry.getIcon(),
			previewFileEntryId, false, fragmentEntry.getTypeOptions(), status);

		Assert.assertEquals(name, fragmentEntry.getName());
		Assert.assertEquals(css, fragmentEntry.getCss());
		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());
		Assert.assertEquals(configuration, fragmentEntry.getConfiguration());
		Assert.assertEquals(
			previewFileEntryId, fragmentEntry.getPreviewFileEntryId());
		Assert.assertEquals(status, fragmentEntry.getStatus());
	}

	private void _testUpdateFragmentEntryPreviewFileEntryId() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		long previewFileEntryId = fragmentEntry.getPreviewFileEntryId();

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), previewFileEntryId + 1);

		Assert.assertEquals(
			previewFileEntryId + 1, fragmentEntry.getPreviewFileEntryId());
	}

	private void _testUpdateFragmentEntryVersions() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				fragmentEntryVersionConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentEntryVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"maximumVersionsPerEntry", 2
						).build())) {

			FragmentEntry fragmentEntry =
				FragmentEntryTestUtil.addFragmentEntry(
					_fragmentCollection.getFragmentCollectionId());

			String name = RandomTestUtil.randomString();

			_updateFragmentEntry(fragmentEntry, name);
			_updateFragmentEntry(fragmentEntry, name);

			_assertUpdateFragmentEntryVersions(fragmentEntry, name, 2);

			try (CompanyConfigurationTemporarySwapper
					ctSettingsConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							CTSettingsConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"enabled", true
							).build())) {

				CTCollection ctCollection =
					_ctCollectionLocalService.addCTCollection(
						null, TestPropsValues.getCompanyId(),
						TestPropsValues.getUserId(), 0,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString());

				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								ctCollection.getCtCollectionId())) {

					String ctCollectionFragmentEntryName =
						RandomTestUtil.randomString();

					_updateFragmentEntry(
						fragmentEntry, ctCollectionFragmentEntryName);
					_updateFragmentEntry(
						fragmentEntry, ctCollectionFragmentEntryName);

					_assertUpdateFragmentEntryVersions(
						fragmentEntry, ctCollectionFragmentEntryName, 2);
				}
			}

			_assertUpdateFragmentEntryVersions(fragmentEntry, name, 2);
		}
	}

	private void _testUpdateFragmentEntryVersionsWithMaximumVersionsPerEntryDisabled()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				fragmentEntryVersionConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentEntryVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"maximumVersionsPerEntry", 0
						).build())) {

			FragmentEntry fragmentEntry =
				FragmentEntryTestUtil.addFragmentEntry(
					_fragmentCollection.getFragmentCollectionId());

			_updateFragmentEntry(fragmentEntry, RandomTestUtil.randomString());

			_assertUpdateFragmentEntryVersions(fragmentEntry, null, 2);

			try (CompanyConfigurationTemporarySwapper
					ctSettingsConfigurationTemporarySwapper =
						new CompanyConfigurationTemporarySwapper(
							TestPropsValues.getCompanyId(),
							CTSettingsConfiguration.class.getName(),
							HashMapDictionaryBuilder.<String, Object>put(
								"enabled", true
							).build())) {

				CTCollection ctCollection =
					_ctCollectionLocalService.addCTCollection(
						null, TestPropsValues.getCompanyId(),
						TestPropsValues.getUserId(), 0,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString());

				try (SafeCloseable safeCloseable =
						CTCollectionThreadLocal.
							setCTCollectionIdWithSafeCloseable(
								ctCollection.getCtCollectionId())) {

					_updateFragmentEntry(
						fragmentEntry, RandomTestUtil.randomString());

					_assertUpdateFragmentEntryVersions(fragmentEntry, null, 3);
				}
			}

			_assertUpdateFragmentEntryVersions(fragmentEntry, null, 2);
		}
	}

	private void _testUpdateFragmentEntryWithCacheable() throws Exception {
		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), true, fragmentEntry.getConfiguration(),
			fragmentEntry.getIcon(), fragmentEntry.getPreviewFileEntryId(),
			fragmentEntry.isReadOnly(), fragmentEntry.getTypeOptions(),
			fragmentEntry.getStatus());

		Assert.assertTrue(fragmentEntry.isCacheable());
	}

	private void _testUpdateFragmentEntryWithHtmlWithAmpersand()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), "<H1>A</H1>",
				RandomTestUtil.randomString(), false, StringPool.BLANK, null,
				RandomTestUtil.randomLong(), false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		String html = "<H1>A&B&amp;C</H1>";

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), html, fragmentEntry.getJs(),
			fragmentEntry.isCacheable(), null, fragmentEntry.getIcon(),
			fragmentEntry.getPreviewFileEntryId(), false,
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(html, fragmentEntry.getHtml());
	}

	private void _testUpdateFragmentEntryWithPreviewFileEntryId()
		throws Exception {

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());

		long previewFileEntryId = fragmentEntry.getPreviewFileEntryId();

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.isCacheable(),
			fragmentEntry.getConfiguration(), fragmentEntry.getIcon(),
			previewFileEntryId + 1, fragmentEntry.isReadOnly(),
			fragmentEntry.getTypeOptions(), fragmentEntry.getStatus());

		Assert.assertEquals(
			previewFileEntryId + 1, fragmentEntry.getPreviewFileEntryId());
	}

	private void _testUpdateFragmentEntryWithPropagateChanges()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			TestPropsValues.getCompanyId());

		FragmentEntry companyGroupFragmentEntry = _addFragmentEntry(
			companyGroup.getGroupId());

		FragmentEntryLink companyGroupFragmentEntryLink = _addFragmentEntryLink(
			companyGroupFragmentEntry, draftLayout, segmentsExperienceId);

		DepotEntry connectedDepotEntry = _addDesignLibraryDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			connectedDepotEntry.getDepotEntryId(), _group.getGroupId());

		FragmentEntry connectedDepotFragmentEntry = _addFragmentEntry(
			connectedDepotEntry.getGroupId());

		FragmentEntryLink connectedDepotFragmentEntryLink =
			_addFragmentEntryLink(
				connectedDepotFragmentEntry, draftLayout, segmentsExperienceId);

		DepotEntry disconnectedDepotEntry = _addDesignLibraryDepotEntry();

		FragmentEntry disconnectedDepotFragmentEntry = _addFragmentEntry(
			disconnectedDepotEntry.getGroupId());

		FragmentEntryLink disconnectedDepotFragmentEntryLink =
			_addFragmentEntryLink(
				disconnectedDepotFragmentEntry, draftLayout,
				segmentsExperienceId);

		FragmentEntry siteFragmentEntry = _addFragmentEntry(
			_group.getGroupId());

		FragmentEntryLink siteFragmentEntryLink = _addFragmentEntryLink(
			siteFragmentEntry, draftLayout, segmentsExperienceId);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		String originalHTML = disconnectedDepotFragmentEntry.getHtml();

		String updatedHTML = RandomTestUtil.randomString();

		_updateFragmentEntriesWithPropagateChanges(
			updatedHTML, companyGroupFragmentEntry, connectedDepotFragmentEntry,
			disconnectedDepotFragmentEntry, siteFragmentEntry);

		_assertFragmentEntryLinksHTML(
			updatedHTML, companyGroupFragmentEntryLink.getFragmentEntryLinkId(),
			connectedDepotFragmentEntryLink.getFragmentEntryLinkId(),
			siteFragmentEntryLink.getFragmentEntryLinkId());

		_assertFragmentEntryLinksHTML(
			originalHTML,
			disconnectedDepotFragmentEntryLink.getFragmentEntryLinkId());
	}

	private void _updateFragmentEntriesWithPropagateChanges(
			String html, FragmentEntry... fragmentEntries)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"propagateChanges", true
						).build())) {

			for (FragmentEntry fragmentEntry : fragmentEntries) {
				FragmentEntry updatedFragmentEntry =
					_fragmentEntryLocalService.updateFragmentEntry(
						TestPropsValues.getUserId(),
						fragmentEntry.getFragmentEntryId(),
						fragmentEntry.getFragmentCollectionId(),
						fragmentEntry.getName(), fragmentEntry.getCss(), html,
						fragmentEntry.getJs(), false,
						fragmentEntry.getConfiguration(),
						fragmentEntry.getIcon(),
						fragmentEntry.getPreviewFileEntryId(), false,
						fragmentEntry.getTypeOptions(),
						WorkflowConstants.STATUS_APPROVED);

				Assert.assertEquals(html, updatedFragmentEntry.getHtml());
			}
		}
	}

	private void _updateFragmentEntry(FragmentEntry fragmentEntry, String name)
		throws Exception {

		_fragmentEntryLocalService.updateFragmentEntry(
			TestPropsValues.getUserId(), fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), name, StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, StringPool.BLANK, 0, false, StringPool.BLANK,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private FragmentCollection _updatedFragmentCollection;

}