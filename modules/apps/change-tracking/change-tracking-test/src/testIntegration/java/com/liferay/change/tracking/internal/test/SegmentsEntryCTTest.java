/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class SegmentsEntryCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, JournalArticleCTTest.class.getName(), null);

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddSegmentsEntry() throws Exception {
		SegmentsEntry segmentsEntry = null;

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId());

			Assert.assertEquals(
				segmentsEntry,
				_segmentsEntryLocalService.fetchSegmentsEntry(
					segmentsEntry.getSegmentsEntryId()));

			try (SafeCloseable safeCloseable2 =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				Assert.assertNull(
					_segmentsEntryLocalService.fetchSegmentsEntry(
						segmentsEntry.getSegmentsEntryId()));
			}
		}

		CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
			_ctCollection.getCtCollectionId(),
			_classNameLocalService.getClassNameId(SegmentsEntry.class),
			segmentsEntry.getSegmentsEntryId());

		Assert.assertNotNull(ctEntry);

		Assert.assertEquals(
			CTConstants.CT_CHANGE_TYPE_ADDITION, ctEntry.getChangeType());
	}

	@Test
	public void testModifySegmentsEntry() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(),
			StringBundler.concat(
				RandomTestUtil.randomString(), StringPool.SPACE,
				RandomTestUtil.randomString())
		).build();

		try (SafeCloseable safeCloseable1 =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			segmentsEntry = _segmentsEntryLocalService.updateSegmentsEntry(
				segmentsEntry.getSegmentsEntryId(),
				segmentsEntry.getSegmentsEntryKey(), nameMap,
				segmentsEntry.getDescriptionMap(), segmentsEntry.isActive(),
				segmentsEntry.getCriteria(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}

		_ctCollectionService.publishCTCollection(
			TestPropsValues.getUserId(), _ctCollection.getCtCollectionId());

		Assert.assertEquals(nameMap, segmentsEntry.getNameMap());
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTCollectionService _ctCollectionService;

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	private CTCollection _ctCollection;
	private Group _group;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}