/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.exception.LockedSegmentsEntryException;
import com.liferay.segments.exception.RequiredSegmentsEntryException;
import com.liferay.segments.exception.SegmentsEntryKeyException;
import com.liferay.segments.exception.SegmentsEntryNameException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;
import com.liferay.segments.service.SegmentsEntryRoleLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Arques
 */
@RunWith(Arquillian.class)
public class SegmentsEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_groups.add(_group);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
	}

	@Test
	public void testAddSegmentsEntry() throws Exception {
		_testAddSegmentsEntry();
		_testAddSegmentsEntryWithoutSource();
		_testAddSegmentsEntryWithReferredSource();
		_testAddSegmentsEntryWithoutName();
		_testAddSegmentsEntryWithExistingKey();
		_testAddSegmentsEntryWithExistingKeyInAncestorGroup();
	}

	@Test
	public void testAddSegmentsEntryClassPKs() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		long[] classPKs = {
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong()
		};

		_segmentsEntryLocalService.addSegmentsEntryClassPKs(
			segmentsEntry.getSegmentsEntryId(), classPKs,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		long[] actualClassPKs = TransformUtil.transformToLongArray(
			_segmentsEntryRelLocalService.getSegmentsEntryRels(
				segmentsEntry.getSegmentsEntryId()),
			SegmentsEntryRel::getClassPK);

		Assert.assertTrue(ArrayUtil.containsAll(actualClassPKs, classPKs));
	}

	@Test
	public void testDeleteSegmentsEntries() throws PortalException {
		int count = 5;

		for (int i = 0; i < count; i++) {
			SegmentsTestUtil.addSegmentsEntry(_group.getGroupId());
		}

		Assert.assertEquals(
			count,
			_segmentsEntryLocalService.getSegmentsEntriesCount(
				_group.getGroupId()));

		_segmentsEntryLocalService.deleteSegmentsEntries(_group.getGroupId());

		Assert.assertEquals(
			0,
			_segmentsEntryLocalService.getSegmentsEntriesCount(
				_group.getGroupId()));
	}

	@Test
	public void testDeleteSegmentsEntry() throws Exception {
		_testDeleteSegmentsEntry();
		_testDeleteSegmentsEntryWithSegmentsEntryRels();
		_testDeleteSegmentsEntryReferencedBySegmentsExperiences();
	}

	@Test
	public void testDeleteSegmentsEntryClassPKs() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		long[] classPKs = {
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong()
		};

		_segmentsEntryLocalService.addSegmentsEntryClassPKs(
			segmentsEntry.getSegmentsEntryId(), classPKs,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsEntryLocalService.deleteSegmentsEntryClassPKs(
			segmentsEntry.getSegmentsEntryId(), classPKs);

		Assert.assertEquals(
			0,
			_segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
				segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testGetSegmentsEntries() throws Exception {
		_testGetSegmentsEntriesCountWithIncludeAncestorSegmentsEntries();
		_testGetSegmentsEntriesWithIncludeAncestorSegmentsEntries();
	}

	@Test
	public void testSearchSegmentsEntries() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				segmentsEntry.getCompanyId(), segmentsEntry.getGroupId(),
				segmentsEntry.getNameCurrentValue(), new LinkedHashMap<>(), 0,
				1, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry, segmentsEntries.get(0));
	}

	@Test
	public void testSearchSegmentsEntriesWithCompanyId()
		throws PortalException {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				segmentsEntry.getCompanyId(), _group.getGroupId(),
				segmentsEntry.getNameCurrentValue(), new LinkedHashMap<>(), 0,
				1, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry, segmentsEntries.get(0));
	}

	@Test
	public void testSearchSegmentsEntriesWithCompanyIdAndExcludedSegmentsEntryIds()
		throws PortalException {

		List<SegmentsEntry> initialSegmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntries(0, 100);

		long[] excludedSegmentsEntryIds =
			new long[initialSegmentsEntries.size() + 1];

		for (int i = 0; i < initialSegmentsEntries.size(); i++) {
			SegmentsEntry segmentsEntry = initialSegmentsEntries.get(i);

			excludedSegmentsEntryIds[i] = segmentsEntry.getSegmentsEntryId();
		}

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		excludedSegmentsEntryIds[initialSegmentsEntries.size()] =
			segmentsEntry1.getSegmentsEntryId();

		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				_group.getCompanyId(), _group.getGroupId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"excludedSegmentsEntryIds", excludedSegmentsEntryIds
				).build(),
				0, 10, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry2, segmentsEntries.get(0));
	}

	@Test
	public void testSearchSegmentsEntriesWithCompanyIdAndExcludedSources()
		throws PortalException {

		List<SegmentsEntry> initialSegmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntries(0, 100);

		long[] excludedSegmentsEntryIds =
			new long[initialSegmentsEntries.size()];

		for (int i = 0; i < initialSegmentsEntries.size(); i++) {
			SegmentsEntry segmentsEntry = initialSegmentsEntries.get(i);

			excludedSegmentsEntryIds[i] = segmentsEntry.getSegmentsEntryId();
		}

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			CriteriaSerializer.serialize(new Criteria()),
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				segmentsEntry.getCompanyId(), _group.getGroupId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"excludedSegmentsEntryIds", excludedSegmentsEntryIds
				).put(
					"excludedSources",
					new String[] {
						SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND
					}
				).build(),
				0, 1, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry, segmentsEntries.get(0));
	}

	@Test
	public void testSearchSegmentsEntriesWithCompanyIdAndRoleIds()
		throws PortalException {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsEntryRoleLocalService.addSegmentsEntryRole(
			segmentsEntry.getSegmentsEntryId(), _role.getRoleId(),
			ServiceContextTestUtil.getServiceContext());

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				segmentsEntry.getCompanyId(), _group.getGroupId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"roleIds", new long[] {_role.getRoleId()}
				).build(),
				0, 1, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry, segmentsEntries.get(0));
	}

	@Test
	public void testSearchSegmentsEntriesWithExcludedSegmentsEntryIds()
		throws PortalException {

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());
		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				segmentsEntry1.getCompanyId(), segmentsEntry1.getGroupId(),
				null,
				LinkedHashMapBuilder.<String, Object>put(
					"excludedSegmentsEntryIds",
					new long[] {segmentsEntry1.getSegmentsEntryId()}
				).build(),
				0, 1, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry2, segmentsEntries.get(0));
	}

	@Test
	public void testSearchSegmentsEntriesWithExcludedSources()
		throws PortalException {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			CriteriaSerializer.serialize(new Criteria()),
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				segmentsEntry.getCompanyId(), segmentsEntry.getGroupId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"excludedSources",
					new String[] {
						SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND
					}
				).build(),
				0, 1, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry, segmentsEntries.get(0));
	}

	@Test
	public void testSearchSegmentsEntriesWithRoleIds() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsEntryRoleLocalService.addSegmentsEntryRole(
			segmentsEntry.getSegmentsEntryId(), _role.getRoleId(),
			ServiceContextTestUtil.getServiceContext());

		BaseModelSearchResult<SegmentsEntry> baseModelSearchResult =
			_segmentsEntryLocalService.searchSegmentsEntries(
				segmentsEntry.getCompanyId(), segmentsEntry.getGroupId(), null,
				LinkedHashMapBuilder.<String, Object>put(
					"roleIds", new long[] {_role.getRoleId()}
				).build(),
				0, 1, null);

		List<SegmentsEntry> segmentsEntries =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			segmentsEntries.toString(), 1, segmentsEntries.size());
		Assert.assertEquals(segmentsEntry, segmentsEntries.get(0));
	}

	@Test
	public void testUpdateSegmentsEntry() throws Exception {
		_testUpdateSegmentsEntry();
		_testUpdateSegmentsEntryUsedByDraftSegmentsExperiment();
		_testUpdateSegmentsEntryUsedByRunningSegmentsExperiment();
		_testUpdateSegmentsEntryWithAsahFaroBackendSource();
		_testUpdateSegmentsEntryWithExistingKey();
		_testUpdateSegmentsEntryWithOutdatedReferredSource();
		_testUpdateSegmentsEntryWithReferredSource();
	}

	private SegmentsExperiment _addSegmentsExperiment(
			SegmentsEntry segmentsEntry)
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
				null, 0);

		return SegmentsTestUtil.addSegmentsExperiment(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceId(),
			0);
	}

	private void _testAddSegmentsEntry() throws Exception {
		String segmentsEntryKey = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString();
		String criteria = CriteriaSerializer.serialize(new Criteria());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), segmentsEntryKey, name, description, criteria);

		Assert.assertEquals(
			StringUtil.toUpperCase(segmentsEntryKey.trim()),
			segmentsEntry.getSegmentsEntryKey());
		Assert.assertEquals(
			name, segmentsEntry.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			description, segmentsEntry.getDescription(LocaleUtil.getDefault()));
		Assert.assertTrue(segmentsEntry.isActive());
		Assert.assertEquals(criteria, segmentsEntry.getCriteria());
		Assert.assertEquals(
			SegmentsEntryConstants.SOURCE_DEFAULT, segmentsEntry.getSource());

		Assert.assertEquals(
			1,
			_segmentsEntryLocalService.getSegmentsEntriesCount(
				_group.getGroupId()));
	}

	private void _testAddSegmentsEntryWithExistingKey() throws Exception {
		String segmentsEntryKey = RandomTestUtil.randomString();

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), segmentsEntryKey,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		AssertUtils.assertFailure(
			SegmentsEntryKeyException.class, null,
			() -> SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId(), segmentsEntryKey,
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
	}

	private void _testAddSegmentsEntryWithExistingKeyInAncestorGroup()
		throws Exception {

		String segmentsEntryKey = RandomTestUtil.randomString();

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), segmentsEntryKey,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		_groups.add(0, childGroup);

		AssertUtils.assertFailure(
			SegmentsEntryKeyException.class, null,
			() -> SegmentsTestUtil.addSegmentsEntry(
				childGroup.getGroupId(), segmentsEntryKey,
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
	}

	private void _testAddSegmentsEntryWithoutName() throws Exception {
		Locale defaultLocale = LocaleUtil.getDefault();

		AssertUtils.assertFailure(
			SegmentsEntryNameException.class,
			"Name is null for locale " + defaultLocale.getDisplayName(),
			() -> SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId(), RandomTestUtil.randomString(),
				StringPool.BLANK, StringPool.BLANK,
				CriteriaSerializer.serialize(new Criteria())));
	}

	private void _testAddSegmentsEntryWithoutSource() throws Exception {
		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.addSegmentsEntry(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(),
				CriteriaSerializer.serialize(new Criteria()), null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			SegmentsEntryConstants.SOURCE_DEFAULT, segmentsEntry.getSource());
	}

	private void _testAddSegmentsEntryWithReferredSource() throws Exception {
		Criteria criteria = new Criteria();

		_segmentsEntrySegmentsCriteriaContributor.contribute(
			criteria,
			String.format(
				"(segmentsEntryIds eq '%s') and (segmentsEntryIds eq '%s')",
				RandomTestUtil.nextLong(), RandomTestUtil.nextLong()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Assert.assertEquals(
			SegmentsEntryConstants.SOURCE_REFERRED, segmentsEntry.getSource());
	}

	private void _testDeleteSegmentsEntry() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsEntryLocalService.deleteSegmentsEntry(
			segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			0,
			_segmentsEntryLocalService.getSegmentsEntriesCount(
				_group.getGroupId()));
	}

	private void _testDeleteSegmentsEntryReferencedBySegmentsExperiences()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceLocalService.addSegmentsExperience(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(), null, 0,
			RandomTestUtil.randomLocaleStringMap(), RandomTestUtil.randomInt(),
			false, new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		AssertUtils.assertFailure(
			RequiredSegmentsEntryException.
				MustNotDeleteSegmentsEntryReferencedBySegmentsExperiences.class,
			String.format(
				"Segments entry %s cannot be deleted because it is " +
					"referenced by one or more segments experiences",
				segmentsEntry.getSegmentsEntryId()),
			() -> _segmentsEntryLocalService.deleteSegmentsEntry(
				segmentsEntry.getSegmentsEntryId()));
	}

	private void _testDeleteSegmentsEntryWithSegmentsEntryRels()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		int count = 5;

		for (int i = 0; i < count; i++) {
			_segmentsEntryRelLocalService.addSegmentsEntryRel(
				segmentsEntry.getSegmentsEntryId(), 0, i, serviceContext);
		}

		List<SegmentsEntryRel> segmentsEntryRels =
			_segmentsEntryRelLocalService.getSegmentsEntryRels(
				segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRels.toString(), count, segmentsEntryRels.size());

		_segmentsEntryLocalService.deleteSegmentsEntry(segmentsEntry);

		segmentsEntryRels = _segmentsEntryRelLocalService.getSegmentsEntryRels(
			segmentsEntry.getSegmentsEntryId());

		Assert.assertEquals(
			segmentsEntryRels.toString(), 0, segmentsEntryRels.size());
	}

	private void _testGetSegmentsEntriesCountWithIncludeAncestorSegmentsEntries()
		throws Exception {

		SegmentsTestUtil.addSegmentsEntry(_group.getGroupId());

		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		_groups.add(0, childGroup);

		int segmentsEntriesCount =
			_segmentsEntryLocalService.getSegmentsEntriesCount(
				childGroup.getGroupId());

		Assert.assertTrue(segmentsEntriesCount > 0);
	}

	private void _testGetSegmentsEntriesWithIncludeAncestorSegmentsEntries()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		Group childGroup = GroupTestUtil.addGroup(_group.getGroupId());

		_groups.add(0, childGroup);

		List<SegmentsEntry> segmentsEntries =
			_segmentsEntryLocalService.getSegmentsEntries(
				childGroup.getGroupId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertTrue(segmentsEntries.contains(segmentsEntry));
	}

	private void _testUpdateSegmentsEntry() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		String segmentsEntryKey = RandomTestUtil.randomString();

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		Map<Locale, String> descriptionMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		String criteria = CriteriaSerializer.serialize(new Criteria());

		SegmentsEntry updatedSegmentsEntry =
			_segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(), segmentsEntryKey, nameMap,
				descriptionMap, false, criteria,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			StringUtil.toUpperCase(segmentsEntryKey.trim()),
			updatedSegmentsEntry.getSegmentsEntryKey());
		Assert.assertEquals(
			nameMap.get(LocaleUtil.getDefault()),
			updatedSegmentsEntry.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			descriptionMap.get(LocaleUtil.getDefault()),
			updatedSegmentsEntry.getDescription(LocaleUtil.getDefault()));
		Assert.assertFalse(updatedSegmentsEntry.isActive());
		Assert.assertEquals(criteria, updatedSegmentsEntry.getCriteria());

		Assert.assertEquals(
			1,
			_segmentsEntryLocalService.getSegmentsEntriesCount(
				_group.getGroupId()));
	}

	private void _testUpdateSegmentsEntryUsedByDraftSegmentsExperiment()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_addSegmentsExperiment(segmentsEntry);

		_segmentsEntryLocalService.updateSegmentsEntry(
			segmentsEntry.getSegmentsEntryId(),
			segmentsEntry.getSegmentsEntryKey(), segmentsEntry.getNameMap(),
			segmentsEntry.getDescriptionMap(), segmentsEntry.isActive(),
			segmentsEntry.getCriteria(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _testUpdateSegmentsEntryUsedByRunningSegmentsExperiment()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(
			segmentsEntry);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		AssertUtils.assertFailure(
			LockedSegmentsEntryException.class, null,
			() -> _segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(),
				segmentsEntry.getSegmentsEntryKey(), segmentsEntry.getNameMap(),
				segmentsEntry.getDescriptionMap(), segmentsEntry.isActive(),
				segmentsEntry.getCriteria(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId())));
	}

	private void _testUpdateSegmentsEntryWithAsahFaroBackendSource()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			CriteriaSerializer.serialize(new Criteria()),
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsEntry updatedSegmentsEntry =
			_segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(),
				segmentsEntry.getSegmentsEntryKey(), segmentsEntry.getNameMap(),
				segmentsEntry.getDescriptionMap(), segmentsEntry.isActive(),
				segmentsEntry.getCriteria(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
			updatedSegmentsEntry.getSource());
	}

	private void _testUpdateSegmentsEntryWithExistingKey() throws Exception {
		String segmentsEntryKey = RandomTestUtil.randomString();

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), segmentsEntryKey,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		AssertUtils.assertFailure(
			SegmentsEntryKeyException.class, null,
			() -> _segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(), segmentsEntryKey,
				segmentsEntry.getNameMap(), segmentsEntry.getDescriptionMap(),
				segmentsEntry.isActive(), segmentsEntry.getCriteria(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId())));
	}

	private void _testUpdateSegmentsEntryWithOutdatedReferredSource()
		throws Exception {

		Criteria criteria = new Criteria();

		_segmentsEntrySegmentsCriteriaContributor.contribute(
			criteria,
			String.format(
				"(segmentsEntryIds eq '%s')", RandomTestUtil.nextLong()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		SegmentsEntry updatedSegmentsEntry =
			_segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(),
				segmentsEntry.getSegmentsEntryKey(), segmentsEntry.getNameMap(),
				segmentsEntry.getDescriptionMap(), false,
				CriteriaSerializer.serialize(new Criteria()),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			SegmentsEntryConstants.SOURCE_DEFAULT,
			updatedSegmentsEntry.getSource());
	}

	private void _testUpdateSegmentsEntryWithReferredSource() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		Criteria criteria = new Criteria();

		_segmentsEntrySegmentsCriteriaContributor.contribute(
			criteria,
			String.format(
				"(segmentsEntryIds eq '%s') and (segmentsEntryIds eq '%s')",
				RandomTestUtil.nextLong(), RandomTestUtil.nextLong()),
			Criteria.Conjunction.AND);

		SegmentsEntry updatedSegmentsEntry =
			_segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(),
				segmentsEntry.getSegmentsEntryKey(), segmentsEntry.getNameMap(),
				segmentsEntry.getDescriptionMap(), false,
				CriteriaSerializer.serialize(criteria),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			SegmentsEntryConstants.SOURCE_REFERRED,
			updatedSegmentsEntry.getSource());
	}

	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	@Inject
	private SegmentsEntryRoleLocalService _segmentsEntryRoleLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=segments",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor
		_segmentsEntrySegmentsCriteriaContributor;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

}