/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.exception.DefaultSegmentsExperienceException;
import com.liferay.segments.exception.DuplicateSegmentsExperienceExternalReferenceCodeException;
import com.liferay.segments.exception.DuplicateSegmentsExperienceKeyException;
import com.liferay.segments.exception.LockedSegmentsExperimentException;
import com.liferay.segments.exception.RequiredSegmentsExperienceException;
import com.liferay.segments.exception.SegmentsExperienceNameException;
import com.liferay.segments.exception.SegmentsExperiencePriorityException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.persistence.SegmentsExperiencePersistence;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Collections;
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
public class SegmentsExperienceLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_plid = layout.getPlid();
	}

	@Test
	@TestInfo("LPD-97443")
	public void testAddDefaultSegmentsExperienceLockedLayout()
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(_plid);

		Layout draftLayout = layout.fetchDraftLayout();

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		_user = UserTestUtil.addUser();

		_lockManager.lock(
			_user.getUserId(), Layout.class.getName(), draftLayout.getPlid(),
			null, false, Time.HOUR);

		try {
			try {
				_segmentsExperienceLocalService.addDefaultSegmentsExperience(
					null, TestPropsValues.getUserId(), draftLayout.getPlid(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

				Assert.fail();
			}
			catch (LockedLayoutException lockedLayoutException) {
			}

			StartupHelperUtil.setUpgrading(true);

			try {
				Assert.assertNotNull(
					_segmentsExperienceLocalService.
						addDefaultSegmentsExperience(
							null, TestPropsValues.getUserId(),
							draftLayout.getPlid(),
							ServiceContextTestUtil.getServiceContext(
								_group.getGroupId())));
			}
			finally {
				StartupHelperUtil.setUpgrading(false);
			}
		}
		finally {
			_lockManager.unlock(Layout.class.getName(), draftLayout.getPlid());
		}
	}

	@Test
	public void testAddSegmentsExperienceActive() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());
		Map<Locale, String> nameMap = RandomTestUtil.randomLocaleStringMap();

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid, nameMap,
				true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			segmentsEntry.getExternalReferenceCode(),
			segmentsExperience.getSegmentsEntryERC());
		Assert.assertTrue(
			Validator.isNull(segmentsExperience.getSegmentsEntryScopeERC()));

		Assert.assertEquals(_plid, segmentsExperience.getPlid());
		Assert.assertEquals(nameMap, segmentsExperience.getNameMap());
		Assert.assertTrue(segmentsExperience.isActive());
		Assert.assertEquals(
			StringPool.BLANK, segmentsExperience.getTypeSettings());

		Assert.assertEquals(
			2,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _plid, true));
	}

	@Test
	public void testAddSegmentsExperienceActiveWithoutPriority()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(-1, segmentsExperience.getPriority());

		Assert.assertEquals(
			2,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _plid, true));
	}

	@Test
	public void testAddSegmentsExperienceInactive() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());
		Map<Locale, String> nameMap = RandomTestUtil.randomLocaleStringMap();

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid, nameMap,
				false, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			segmentsEntry.getExternalReferenceCode(),
			segmentsExperience.getSegmentsEntryERC());
		Assert.assertTrue(
			Validator.isNull(segmentsExperience.getSegmentsEntryScopeERC()));

		Assert.assertEquals(_plid, segmentsExperience.getPlid());
		Assert.assertEquals(nameMap, segmentsExperience.getNameMap());
		Assert.assertFalse(segmentsExperience.isActive());
		Assert.assertEquals(
			StringPool.BLANK, segmentsExperience.getTypeSettings());

		Assert.assertEquals(
			1,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _plid, false));
	}

	@Test
	public void testAddSegmentsExperienceInactiveWithoutPriority()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(), false,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(-1, segmentsExperience.getPriority());

		Assert.assertEquals(
			1,
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				_group.getGroupId(), _plid, false));
	}

	@Test
	public void testAddSegmentsExperiencesWithTheSameSegmentsEntry()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsTestUtil.addSegmentsExperience(
			_group.getGroupId(), segmentsEntry.getExternalReferenceCode(), null,
			_plid);
		SegmentsTestUtil.addSegmentsExperience(
			_group.getGroupId(), segmentsEntry.getExternalReferenceCode(), null,
			_plid);

		List<SegmentsExperience> segmentsExperiences =
			_segmentsExperienceLocalService.getSegmentsExperiences(
				_group.getGroupId(), _plid, true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			segmentsExperiences.toString(), 3, segmentsExperiences.size());
	}

	@Test
	public void testAddSegmentsExperienceToPageTemplate() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(), LayoutPageTemplateEntryTypeConstants.BASIC,
				WorkflowConstants.STATUS_DRAFT);

		try {
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, layoutPageTemplateEntry.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Segments experiences cannot be added to layout " +
					layoutPageTemplateEntry.getPlid() +
						" because it belongs to a page template",
				illegalArgumentException.getMessage());
		}

		layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId());

		try {
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, layoutPageTemplateEntry.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Segments experiences cannot be added to layout " +
					layoutPageTemplateEntry.getPlid() +
						" because it is not a content page",
				illegalArgumentException.getMessage());
		}
	}

	@Test(
		expected = DuplicateSegmentsExperienceExternalReferenceCodeException.class
	)
	public void testAddSegmentsExperienceWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceLocalService.addSegmentsExperience(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), segmentsEntry.getExternalReferenceCode(), null,
			_plid, RandomTestUtil.randomLocaleStringMap(), false,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		_segmentsExperienceLocalService.addSegmentsExperience(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), segmentsEntry.getExternalReferenceCode(), null,
			_plid, RandomTestUtil.randomLocaleStringMap(), false,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = DuplicateSegmentsExperienceKeyException.class)
	public void testAddSegmentsExperienceWithExistingSegmentsExperienceKey()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		String segmentsExperienceKey = StringUtil.randomString();

		_segmentsExperienceLocalService.addSegmentsExperience(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(), null,
			segmentsExperienceKey, _plid,
			RandomTestUtil.randomLocaleStringMap(), 1, true,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		_segmentsExperienceLocalService.addSegmentsExperience(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(), null,
			segmentsExperienceKey, _plid,
			RandomTestUtil.randomLocaleStringMap(), 2, true,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = SegmentsExperiencePriorityException.class)
	public void testAddSegmentsExperienceWithInvalidPriority()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		_segmentsExperienceLocalService.addSegmentsExperience(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(), null, _plid,
			RandomTestUtil.randomLocaleStringMap(),
			segmentsExperience.getPriority(), true, new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = SegmentsExperienceNameException.class)
	public void testAddSegmentsExperienceWithoutName() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceLocalService.addSegmentsExperience(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			segmentsEntry.getExternalReferenceCode(), null, _plid,
			Collections.emptyMap(), RandomTestUtil.randomBoolean(),
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	@TestInfo("LPD-90839")
	public void testAddSegmentsExperienceWithPriorityCompaction()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomInt(100, 199), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(1, segmentsExperience.getPriority());

		segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(),
				-RandomTestUtil.randomInt(100, 199), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(-1, segmentsExperience.getPriority());
	}

	@Test
	public void testAddSegmentsExperienceWithTypeSetting() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"property", "value"
				).build(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		UnicodeProperties actualTypeSettingsUnicodeProperties =
			segmentsExperience.getTypeSettingsUnicodeProperties();

		Assert.assertEquals(
			"value",
			actualTypeSettingsUnicodeProperties.getProperty("property"));
	}

	@Test
	public void testAppendSegmentsExperience() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());
		Map<Locale, String> nameMap = RandomTestUtil.randomLocaleStringMap();
		boolean active = RandomTestUtil.randomBoolean();

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid, nameMap,
				active,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			segmentsEntry.getExternalReferenceCode(),
			segmentsExperience.getSegmentsEntryERC());
		Assert.assertTrue(
			Validator.isNull(segmentsExperience.getSegmentsEntryScopeERC()));

		Assert.assertEquals(_plid, segmentsExperience.getPlid());
		Assert.assertEquals(nameMap, segmentsExperience.getNameMap());
		Assert.assertEquals(active, segmentsExperience.isActive());
		Assert.assertEquals(1, segmentsExperience.getPriority());
		Assert.assertEquals(
			StringPool.BLANK, segmentsExperience.getTypeSettings());
	}

	@Test
	public void testAppendSegmentsExperienceWithoutTypeSettings()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			StringPool.BLANK, segmentsExperience.getTypeSettings());
	}

	@Test
	public void testDeleteSegmentsExperience() throws Exception {
		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience.getSegmentsExperienceId());

		Assert.assertNull(
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience.getSegmentsExperienceId()));
	}

	@Test
	public void testDeleteSegmentsExperienceByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceLocalService.addSegmentsExperience(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), segmentsEntry.getExternalReferenceCode(), null,
			_plid, RandomTestUtil.randomLocaleStringMap(), false,
			new UnicodeProperties(true),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			externalReferenceCode, _group.getGroupId());

		Assert.assertNull(
			_segmentsExperienceLocalService.
				fetchSegmentsExperienceByExternalReferenceCode(
					externalReferenceCode, _group.getGroupId()));
	}

	@Test(
		expected = RequiredSegmentsExperienceException.MustNotDeleteSegmentsExperienceReferencedBySegmentsExperiments.class
	)
	public void testDeleteSegmentsExperienceReferencedBySegmentsExperiments()
		throws PortalException {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		SegmentsExperiment segmentsExperiment =
			SegmentsTestUtil.addSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid());

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience.getSegmentsExperienceId());
	}

	@Test
	public void testDeleteSegmentsExperienceWithHighestPriority()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(1, segmentsExperience1.getPriority());

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null, _plid,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(2, segmentsExperience2.getPriority());

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience1.getSegmentsExperienceId());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertEquals(1, segmentsExperience2.getPriority());
	}

	@Test
	public void testDeleteSegmentsExperienceWithMidrangeNegativePriority()
		throws Exception {

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, _plid, RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, _plid, RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		SegmentsExperience segmentsExperience3 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, _plid, RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience2.getSegmentsExperienceId());

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());

		Assert.assertEquals(-1, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertNull(segmentsExperience2);

		segmentsExperience3 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience3.getSegmentsExperienceId());

		Assert.assertEquals(-2, segmentsExperience3.getPriority());
	}

	@Test
	public void testDeleteSegmentsExperienceWithMidrangePriority()
		throws Exception {

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		SegmentsExperience segmentsExperience3 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		SegmentsExperience segmentsExperience4 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		SegmentsExperience segmentsExperience5 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			segmentsExperience2.getSegmentsExperienceId());

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());

		Assert.assertEquals(1, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertNull(segmentsExperience2);

		segmentsExperience3 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience3.getSegmentsExperienceId());

		Assert.assertEquals(2, segmentsExperience3.getPriority());

		segmentsExperience4 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience4.getSegmentsExperienceId());

		Assert.assertEquals(3, segmentsExperience4.getPriority());

		segmentsExperience5 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience5.getSegmentsExperienceId());

		Assert.assertEquals(4, segmentsExperience5.getPriority());
	}

	@Test
	public void testFetchDefaultSegmentsExperienceId() throws PortalException {
		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_plid);

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			defaultSegmentsExperienceId);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.addDefaultSegmentsExperience(
				null, TestPropsValues.getUserId(), _plid,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			segmentsExperience.getSegmentsExperienceId(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_plid));
	}

	@Test
	public void testFetchDefaultSegmentsExperienceIdWithNonexistingDefaultExperience()
		throws PortalException {

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_plid);

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			defaultSegmentsExperienceId);

		Assert.assertEquals(
			SegmentsExperienceConstants.ID_DEFAULT,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_plid));
	}

	@Test
	public void testFetchDefaultSegmentsExperienceIdWithNonexistingPlid()
		throws PortalException {

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_plid);

		_segmentsExperienceLocalService.deleteSegmentsExperience(
			defaultSegmentsExperienceId);

		Assert.assertEquals(
			SegmentsExperienceConstants.ID_DEFAULT,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				0));
	}

	@Test
	public void testFetchSegmentsExperience() throws Exception {
		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		Assert.assertEquals(
			segmentsExperience,
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_group.getGroupId(), _plid, segmentsExperience.getPriority()));
		Assert.assertNotNull(
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_group.getGroupId(), _plid,
				segmentsExperience.getPriority() + 1));
	}

	@Test(expected = DefaultSegmentsExperienceException.class)
	public void testUpdateDefaultSegmentsExperienceWithSegmentsEntry()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				_plid);

		_segmentsExperienceLocalService.updateSegmentsExperience(
			TestPropsValues.getUserId(),
			segmentsExperience.getSegmentsExperienceId(),
			segmentsEntry.getExternalReferenceCode(), null,
			segmentsExperience.getNameMap(), true);
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testUpdatePrioritySegmentsExperienceWithSegmentsExperimentInStatusRunning()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		SegmentsExperiment segmentsExperiment =
			SegmentsTestUtil.addSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceId(), _plid);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
			TestPropsValues.getUserId(),
			segmentsExperience.getSegmentsExperienceId(), -1);
	}

	@Test
	public void testUpdateSegmentsExperience() throws Exception {
		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());
		Map<Locale, String> nameMap = RandomTestUtil.randomLocaleStringMap();
		boolean active = RandomTestUtil.randomBoolean();

		SegmentsExperience updatedSegmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				TestPropsValues.getUserId(),
				segmentsExperience.getSegmentsExperienceId(),
				segmentsEntry.getExternalReferenceCode(), null, nameMap, active,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"property", "value"
				).build());

		Assert.assertEquals(
			segmentsEntry.getExternalReferenceCode(),
			updatedSegmentsExperience.getSegmentsEntryERC());

		Assert.assertTrue(
			Validator.isNull(segmentsExperience.getSegmentsEntryScopeERC()));

		Assert.assertEquals(nameMap, updatedSegmentsExperience.getNameMap());
		Assert.assertEquals(active, updatedSegmentsExperience.isActive());

		UnicodeProperties actualTypeSettingsUnicodeProperties =
			updatedSegmentsExperience.getTypeSettingsUnicodeProperties();

		Assert.assertEquals(
			"value",
			actualTypeSettingsUnicodeProperties.getProperty("property"));
	}

	@Test
	public void testUpdateSegmentsExperiencePriority() throws Exception {
		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);
		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		int priority1 = segmentsExperience1.getPriority();
		int priority2 = segmentsExperience2.getPriority();

		SegmentsExperience movedSegmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience1.getSegmentsExperienceId(),
				segmentsExperience2.getPriority());

		segmentsExperience1 =
			_segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());

		segmentsExperience2 =
			_segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertEquals(movedSegmentsExperience, segmentsExperience1);
		Assert.assertEquals(priority1, segmentsExperience2.getPriority());
		Assert.assertEquals(priority2, segmentsExperience1.getPriority());

		SegmentsExperience segmentsExperience3 =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		Assert.assertEquals(-3, segmentsExperience3.getPriority());

		segmentsExperience1 =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience1.getSegmentsExperienceId(),
				-RandomTestUtil.randomInt(100, 199));

		Assert.assertEquals(-3, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertEquals(-1, segmentsExperience2.getPriority());

		segmentsExperience3 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience3.getSegmentsExperienceId());

		Assert.assertEquals(-2, segmentsExperience3.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperienceToZero() throws Exception {
		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		Assert.assertEquals(-1, segmentsExperience.getPriority());

		segmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience.getSegmentsExperienceId(), 1);

		Assert.assertEquals(1, segmentsExperience.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperienceToZeroWithAboveSegmentsExperiences()
		throws Exception {

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(1, segmentsExperience1.getPriority());

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(2, segmentsExperience2.getPriority());

		SegmentsExperience segmentsExperience3 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, _plid, RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(segmentsExperience3.getPriority(), -1);

		segmentsExperience3 =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience3.getSegmentsExperienceId(),
				segmentsExperience3.getPriority() + 1);

		Assert.assertEquals(1, segmentsExperience3.getPriority());

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());

		Assert.assertEquals(2, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertEquals(3, segmentsExperience2.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperienceToZeroWithBelowSegmentsExperiences()
		throws Exception {

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		Assert.assertEquals(-1, segmentsExperience1.getPriority());

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		Assert.assertEquals(-2, segmentsExperience2.getPriority());

		segmentsExperience1 =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience1.getSegmentsExperienceId(),
				segmentsExperience1.getPriority() + 1);

		Assert.assertEquals(1, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertEquals(-1, segmentsExperience2.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperienceUnderZero() throws Exception {
		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, _plid, RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(-1, segmentsExperience1.getPriority());

		segmentsExperience1 =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience1.getSegmentsExperienceId(),
				segmentsExperience1.getPriority() + 1);

		Assert.assertEquals(1, segmentsExperience1.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperienceUnderZeroWithAboveSegmentsExperiences()
		throws Exception {

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(1, segmentsExperience1.getPriority());

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(2, segmentsExperience2.getPriority());

		segmentsExperience1 =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience1.getSegmentsExperienceId(),
				segmentsExperience1.getPriority() - 1);

		Assert.assertEquals(-1, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertEquals(1, segmentsExperience2.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperienceUnderZeroWithBelowSegmentsExperiences()
		throws Exception {

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(1, segmentsExperience1.getPriority());

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, _plid, RandomTestUtil.randomLocaleStringMap(), true,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(-1, segmentsExperience2.getPriority());

		segmentsExperience1 =
			_segmentsExperienceLocalService.updateSegmentsExperiencePriority(
				TestPropsValues.getUserId(),
				segmentsExperience1.getSegmentsExperienceId(),
				segmentsExperience1.getPriority() - 1);

		Assert.assertEquals(-1, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertEquals(-2, segmentsExperience2.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperienceWithoutTypeSettings()
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(), null, null,
				_plid, RandomTestUtil.randomLocaleStringMap(), true,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"property", "value"
				).build(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperience updatedSegmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				TestPropsValues.getUserId(),
				segmentsExperience.getSegmentsExperienceId(), null, null,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean());

		UnicodeProperties actualTypeSettingsUnicodeProperties =
			updatedSegmentsExperience.getTypeSettingsUnicodeProperties();

		Assert.assertEquals(
			"value",
			actualTypeSettingsUnicodeProperties.getProperty("property"));
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testUpdateSegmentsExperienceWithSegmentsExperimentInStatusRunning()
		throws Exception {

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(_group.getGroupId(), _plid);

		SegmentsExperiment segmentsExperiment =
			SegmentsTestUtil.addSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceId(), _plid);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		_segmentsExperienceLocalService.updateSegmentsExperience(
			TestPropsValues.getUserId(),
			segmentsExperience.getSegmentsExperienceId(),
			segmentsEntry.getExternalReferenceCode(), null,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomBoolean());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LockManager _lockManager;

	private long _plid;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperiencePersistence _segmentsExperiencePersistence;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@DeleteAfterTestRun
	private User _user;

}