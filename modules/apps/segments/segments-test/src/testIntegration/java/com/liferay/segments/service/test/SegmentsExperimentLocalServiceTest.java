/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.exception.DuplicateSegmentsExperimentException;
import com.liferay.segments.exception.LockedSegmentsExperimentException;
import com.liferay.segments.exception.RunSegmentsExperimentException;
import com.liferay.segments.exception.SegmentsExperimentConfidenceLevelException;
import com.liferay.segments.exception.SegmentsExperimentGoalException;
import com.liferay.segments.exception.SegmentsExperimentNameException;
import com.liferay.segments.exception.SegmentsExperimentRelSplitException;
import com.liferay.segments.exception.SegmentsExperimentStatusException;
import com.liferay.segments.exception.WinnerSegmentsExperienceException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentModel;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.List;
import java.util.Map;

import org.junit.After;
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
public class SegmentsExperimentLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		ServiceContextThreadLocal.pushServiceContext(new ServiceContext());
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddSegmentsExperiment() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment expectedSegmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperiment actualSegmentsExperiment =
			_segmentsExperimentLocalService.getSegmentsExperiment(
				expectedSegmentsExperiment.getSegmentsExperimentId());

		Assert.assertNotNull(actualSegmentsExperiment);
		Assert.assertEquals(
			expectedSegmentsExperiment.getSegmentsExperimentKey(),
			actualSegmentsExperiment.getSegmentsExperimentKey());
		Assert.assertEquals(
			expectedSegmentsExperiment.getSegmentsExperienceId(),
			actualSegmentsExperiment.getSegmentsExperienceId());
		Assert.assertEquals(
			expectedSegmentsExperiment.getSegmentsEntryId(),
			actualSegmentsExperiment.getSegmentsEntryId());
		Assert.assertEquals(
			expectedSegmentsExperiment.getName(),
			actualSegmentsExperiment.getName());
		Assert.assertEquals(
			expectedSegmentsExperiment.getDescription(),
			actualSegmentsExperiment.getDescription());
		Assert.assertEquals(
			expectedSegmentsExperiment.getStatus(),
			actualSegmentsExperiment.getStatus());
		Assert.assertEquals(0, actualSegmentsExperiment.getStatus());
		Assert.assertEquals(
			expectedSegmentsExperiment.getTypeSettings(),
			actualSegmentsExperiment.getTypeSettings());

		List<SegmentsExperimentRel> segmentsExperimentRels =
			_segmentsExperimentRelLocalService.getSegmentsExperimentRels(
				actualSegmentsExperiment.getSegmentsExperimentId());

		Assert.assertEquals(
			segmentsExperimentRels.toString(), 1,
			segmentsExperimentRels.size());

		SegmentsExperimentRel segmentsExperimentRel =
			segmentsExperimentRels.get(0);

		Assert.assertEquals(
			actualSegmentsExperiment.getSegmentsExperienceId(),
			segmentsExperimentRel.getSegmentsExperienceId());
	}

	@Test(expected = SegmentsExperimentNameException.class)
	public void testAddSegmentsExperimentWithEmptyName() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		_segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), StringPool.BLANK,
			RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = DuplicateSegmentsExperimentException.class)
	public void testAddSegmentsExperimentWithExistingExperimentInDraft()
		throws Exception {

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		_segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = DuplicateSegmentsExperimentException.class)
	public void testAddSegmentsExperimentWithExistingExperimentInPaused()
		throws Exception {

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_PAUSED);

		_segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = DuplicateSegmentsExperimentException.class)
	public void testAddSegmentsExperimentWithExistingExperimentInRunning()
		throws Exception {

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentLocalService.addSegmentsExperiment(
				segmentsExperience.getSegmentsExperienceId(),
				segmentsExperience.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = SegmentsExperimentGoalException.class)
	public void testAddSegmentsExperimentWithInvalidGoal() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		_segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test(expected = SegmentsExperimentNameException.class)
	public void testAddSegmentsExperimentWithNullName() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		_segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid(), null, RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testDeleteLayoutWithSegmentsExperiments() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(
			segmentsExperience);

		_layoutLocalService.deleteLayout(segmentsExperiment.getPlid());

		Assert.assertNull(
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceKey(),
				segmentsExperience.getPlid()));
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testDeleteSegmentsExperimentInStatusRunning() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(
			segmentsExperience);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperimentLocalService.deleteSegmentsExperiment(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceKey(),
			segmentsExperience.getPlid());
	}

	@Test
	public void testDeleteSegmentsExperiments() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		_addSegmentsExperiment(segmentsExperience);

		_segmentsExperimentLocalService.deleteSegmentsExperiment(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceKey(),
			segmentsExperience.getPlid());

		Assert.assertNull(
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceKey(),
				segmentsExperience.getPlid()));
	}

	@Test
	public void testDeleteSegmentsExperimentsWithVariantSegmentsExperience()
		throws Exception {

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(
			segmentsExperience);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience variantSegmentsExperience1 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				false, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		variantSegmentsExperience1 = _publishSegmentsExperience(
			variantSegmentsExperience1);

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience1.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		SegmentsExperience variantSegmentsExperience2 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				false, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		variantSegmentsExperience2 = _publishSegmentsExperience(
			variantSegmentsExperience2);

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience2.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.deleteSegmentsExperiment(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceKey(),
			segmentsExperience.getPlid());

		Assert.assertNull(
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				variantSegmentsExperience1.getSegmentsExperienceId()));
		Assert.assertNull(
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				variantSegmentsExperience2.getSegmentsExperienceId()));
	}

	@Test
	public void testFetchSegmentsExperiment() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperiment.getSegmentsExperienceId());

		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.
				fetchSegmentsEntryByExternalReferenceCode(
					segmentsExperience.getSegmentsEntryERC(),
					segmentsExperience.getGroupId());

		segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				segmentsExperiment.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), false,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotNull(
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceKey(),
				segmentsExperience.getPlid()));
		Assert.assertNotNull(
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperiment.getSegmentsExperienceKey(),
				segmentsExperience.getPlid()));
	}

	@Test
	public void testGetSegmentsEntrySegmentsExperiments() throws Exception {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
				null, _layout.getPlid());

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
				null, _layout.getPlid());

		SegmentsExperiment segmentsExperiment1 = _addSegmentsExperiment(
			segmentsExperience1);

		SegmentsExperiment segmentsExperiment2 = _addSegmentsExperiment(
			segmentsExperience2);

		List<SegmentsExperiment> segmentsExperiments =
			_segmentsExperimentLocalService.getSegmentsEntrySegmentsExperiments(
				segmentsEntry.getExternalReferenceCode(),
				segmentsEntry.getGroupId());

		Assert.assertEquals(
			segmentsExperiments.toString(), 2, segmentsExperiments.size());

		long[] segmentsExperimentIds = TransformUtil.transformToLongArray(
			segmentsExperiments,
			SegmentsExperimentModel::getSegmentsExperimentId);

		Assert.assertTrue(
			ArrayUtil.containsAll(
				segmentsExperimentIds,
				new long[] {
					segmentsExperiment1.getSegmentsExperimentId(),
					segmentsExperiment2.getSegmentsExperimentId()
				}));
	}

	@Test
	public void testGetSegmentsExperiments() throws Exception {
		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		SegmentsTestUtil.addSegmentsExperiment(
			_group.getGroupId(), defaultSegmentsExperienceId,
			_layout.getPlid());

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _layout.getPlid());

		_addSegmentsExperiment(segmentsExperience1);

		SegmentsExperiment expectedSegmentsExperiment = _addSegmentsExperiment(
			segmentsExperience2);

		SegmentsExperiment actualSegmentsExperiment =
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				_layout.getGroupId(),
				segmentsExperience2.getSegmentsExperienceKey(),
				_layout.getPlid());

		Assert.assertEquals(
			expectedSegmentsExperiment.getSegmentsExperimentId(),
			actualSegmentsExperiment.getSegmentsExperimentId());
	}

	@Test
	public void testHasSegmentsExperiment() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperiment.getSegmentsExperienceId());

		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.
				fetchSegmentsEntryByExternalReferenceCode(
					segmentsExperience.getSegmentsEntryERC(),
					segmentsExperience.getGroupId());

		segmentsExperience =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				segmentsExperiment.getPlid(),
				RandomTestUtil.randomLocaleStringMap(), false,
				new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertNotNull(
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperience.getSegmentsExperienceKey(),
				segmentsExperience.getPlid()));
		Assert.assertNotNull(
			_segmentsExperimentLocalService.fetchSegmentsExperiment(
				_group.getGroupId(),
				segmentsExperiment.getSegmentsExperienceKey(),
				segmentsExperience.getPlid()));
	}

	@Test
	public void testRunSegmentsExperiment() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience variantSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				segmentsExperiment.getGroupId(), segmentsExperiment.getPlid());

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		double confidenceLevel = 0.95;

		Map<Long, Double> segmentsExperienceIdSplitMap = HashMapBuilder.put(
			segmentsExperiment.getSegmentsExperienceId(), 0.70
		).put(
			variantSegmentsExperience.getSegmentsExperienceId(), 0.30
		).build();

		segmentsExperiment =
			_segmentsExperimentLocalService.runSegmentsExperiment(
				segmentsExperiment.getSegmentsExperimentId(), confidenceLevel,
				segmentsExperienceIdSplitMap,
				SegmentsExperimentConstants.Type.AB.name());

		Assert.assertEquals(
			SegmentsExperimentConstants.STATUS_RUNNING,
			segmentsExperiment.getStatus());

		Assert.assertEquals(
			confidenceLevel, segmentsExperiment.getConfidenceLevel(), 0.001);

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.fetchSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperiment.getSegmentsExperienceKey());
		SegmentsExperimentRel variantSegmentsExperimentRel =
			_segmentsExperimentRelLocalService.fetchSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				variantSegmentsExperience.getSegmentsExperienceKey());

		Assert.assertEquals(
			segmentsExperienceIdSplitMap.get(
				segmentsExperimentRel.getSegmentsExperienceId()),
			segmentsExperimentRel.getSplit(), 0.001);
		Assert.assertEquals(
			segmentsExperienceIdSplitMap.get(
				variantSegmentsExperimentRel.getSegmentsExperienceId()),
			variantSegmentsExperimentRel.getSplit(), 0.001);
	}

	@Test(expected = RunSegmentsExperimentException.class)
	public void testRunSegmentsExperimentWithClickGoalAndEmptyTarget()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.CLICK_RATE.getLabel(),
			StringPool.BLANK);

		_segmentsExperimentLocalService.runSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(), 0.95,
			HashMapBuilder.put(
				segmentsExperiment.getSegmentsExperienceId(), 0.70
			).put(
				() -> {
					SegmentsExperience variantSegmentsExperience =
						SegmentsTestUtil.addSegmentsExperience(
							segmentsExperiment.getGroupId(),
							segmentsExperiment.getPlid());

					return variantSegmentsExperience.getSegmentsExperienceId();
				},
				0.30
			).build(),
			SegmentsExperimentConstants.Type.AB.name());
	}

	@Test(expected = RunSegmentsExperimentException.class)
	public void testRunSegmentsExperimentWithControlVariant() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.runSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(), 0.95,
			HashMapBuilder.put(
				segmentsExperiment.getSegmentsExperienceId(), 1.00
			).build(),
			SegmentsExperimentConstants.Type.AB.name());
	}

	@Test(expected = SegmentsExperimentConfidenceLevelException.class)
	public void testRunSegmentsExperimentWithInvalidConfidenceLevel()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience variantSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				segmentsExperiment.getGroupId(), segmentsExperiment.getPlid());

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.runSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(), 1.2,
			HashMapBuilder.put(
				segmentsExperiment.getSegmentsExperienceId(), 0.70
			).put(
				variantSegmentsExperience.getSegmentsExperienceId(), 0.30
			).build(),
			SegmentsExperimentConstants.Type.AB.name());
	}

	@Test(expected = SegmentsExperimentRelSplitException.class)
	public void testRunSegmentsExperimentWithInvalidSplit() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience variantSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				segmentsExperiment.getGroupId(), segmentsExperiment.getPlid());

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		double confidenceLevel = 0.95;

		_segmentsExperimentLocalService.runSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(), confidenceLevel,
			HashMapBuilder.put(
				segmentsExperiment.getSegmentsExperienceId(), 0.70
			).put(
				variantSegmentsExperience.getSegmentsExperienceId(), 0.40
			).build(),
			SegmentsExperimentConstants.Type.AB.name());
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testRunSegmentsExperimentWithRunningStatus() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience variantSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				segmentsExperiment.getGroupId(), segmentsExperiment.getPlid());

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		double confidenceLevel = 0.95;

		_segmentsExperimentLocalService.runSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(), confidenceLevel,
			HashMapBuilder.put(
				segmentsExperiment.getSegmentsExperienceId(), 0.70
			).put(
				variantSegmentsExperience.getSegmentsExperienceId(), 0.30
			).build(),
			SegmentsExperimentConstants.Type.AB.name());
	}

	@Test(expected = LockedSegmentsExperimentException.class)
	public void testUpdateSegmentsExperimentNameInStatusRunning()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperimentLocalService.updateSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(),
			RandomTestUtil.randomString(), null, null, null);
	}

	@Test
	public void testUpdateSegmentsExperimentStatusToCompletedWithWinnerSegmentsExperienceAndControlSegmentsExperienceWithPriorityMinusTwo()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				1, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience1 = _publishSegmentsExperience(segmentsExperience1);

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				-1, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience2 = _publishSegmentsExperience(segmentsExperience2);

		SegmentsExperience segmentsExperience3 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				-2, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience3 = _publishSegmentsExperience(segmentsExperience3);

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(
			segmentsExperience2);

		SegmentsExperience variantSegmentsExperience =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				false,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		variantSegmentsExperience = _publishSegmentsExperience(
			variantSegmentsExperience);

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperiment.setStatus(
			SegmentsExperimentConstants.STATUS_FINISHED_WINNER);

		segmentsExperiment =
			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			SegmentsExperimentConstants.STATUS_COMPLETED);

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());

		Assert.assertTrue(segmentsExperience1.isActive());
		Assert.assertEquals(1, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertFalse(segmentsExperience2.isActive());
		Assert.assertEquals(-3, segmentsExperience2.getPriority());

		segmentsExperience3 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience3.getSegmentsExperienceId());

		Assert.assertTrue(segmentsExperience3.isActive());
		Assert.assertEquals(-2, segmentsExperience3.getPriority());

		variantSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				variantSegmentsExperience.getSegmentsExperienceId());

		Assert.assertTrue(variantSegmentsExperience.isActive());
		Assert.assertEquals(-1, variantSegmentsExperience.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperimentStatusToCompletedWithWinnerSegmentsExperienceAndControlSegmentsExperienceWithPriorityZero()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				1, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience1 = _publishSegmentsExperience(segmentsExperience1);

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				2, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience2 = _publishSegmentsExperience(segmentsExperience2);

		SegmentsExperience segmentsExperience3 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				-1, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience3 = _publishSegmentsExperience(segmentsExperience3);

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment(
			segmentsExperience1);

		SegmentsExperience variantSegmentsExperience =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				false,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		variantSegmentsExperience = _publishSegmentsExperience(
			variantSegmentsExperience);

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperiment.setStatus(
			SegmentsExperimentConstants.STATUS_FINISHED_WINNER);

		segmentsExperiment =
			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			SegmentsExperimentConstants.STATUS_COMPLETED);

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());

		Assert.assertFalse(segmentsExperience1.isActive());
		Assert.assertEquals(-2, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertTrue(segmentsExperience2.isActive());
		Assert.assertEquals(2, segmentsExperience2.getPriority());

		segmentsExperience3 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience3.getSegmentsExperienceId());

		Assert.assertTrue(segmentsExperience3.isActive());
		Assert.assertEquals(-1, segmentsExperience3.getPriority());

		variantSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				variantSegmentsExperience.getSegmentsExperienceId());

		Assert.assertTrue(variantSegmentsExperience.isActive());
		Assert.assertEquals(1, variantSegmentsExperience.getPriority());
	}

	@Test
	public void testUpdateSegmentsExperimentStatusToCompletedWithWinnerSegmentsExperienceAndDefaultControlSegmentsExperience()
		throws Exception {

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		SegmentsExperience segmentsExperience1 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				1, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience1 = _publishSegmentsExperience(segmentsExperience1);

		SegmentsExperience segmentsExperience2 =
			_segmentsExperienceLocalService.addSegmentsExperience(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				-1, true, new UnicodeProperties(true),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperience2 = _publishSegmentsExperience(segmentsExperience2);

		SegmentsExperience defaultSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_group.getGroupId(), SegmentsExperienceConstants.KEY_DEFAULT,
				_layout.getPlid());

		SegmentsExperiment segmentsExperiment =
			SegmentsTestUtil.addSegmentsExperiment(
				_group.getGroupId(),
				defaultSegmentsExperience.getSegmentsExperienceId(),
				_layout.getPlid());

		SegmentsExperience variantSegmentsExperience =
			_segmentsExperienceLocalService.appendSegmentsExperience(
				TestPropsValues.getUserId(), _group.getGroupId(),
				segmentsEntry.getExternalReferenceCode(), null,
				_draftLayout.getPlid(), RandomTestUtil.randomLocaleStringMap(),
				false,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		variantSegmentsExperience = _publishSegmentsExperience(
			variantSegmentsExperience);

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		segmentsExperiment.setStatus(
			SegmentsExperimentConstants.STATUS_FINISHED_WINNER);

		segmentsExperiment =
			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			SegmentsExperimentConstants.STATUS_COMPLETED);

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getSegmentsExperienceId());

		Assert.assertTrue(segmentsExperience1.isActive());
		Assert.assertEquals(1, segmentsExperience1.getPriority());

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getSegmentsExperienceId());

		Assert.assertTrue(segmentsExperience2.isActive());
		Assert.assertEquals(-1, segmentsExperience2.getPriority());

		variantSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				variantSegmentsExperience.getSegmentsExperienceId());

		Assert.assertTrue(variantSegmentsExperience.isActive());
		Assert.assertEquals(0, variantSegmentsExperience.getPriority());
	}

	@Test(expected = WinnerSegmentsExperienceException.class)
	public void testUpdateSegmentsExperimentStatusToFinishedWithNonexistingWinnerSegmentsExperience()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			RandomTestUtil.nextLong(),
			SegmentsExperimentConstants.STATUS_FINISHED_WINNER);
	}

	@Test
	public void testUpdateSegmentsExperimentStatusToFinishedWithWinnerSegmentsExperience()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience variantSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				segmentsExperiment.getGroupId(), segmentsExperiment.getPlid());

		variantSegmentsExperience.setActive(false);

		variantSegmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				variantSegmentsExperience);

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		segmentsExperiment =
			_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
				segmentsExperiment.getSegmentsExperimentId(),
				variantSegmentsExperience.getSegmentsExperienceId(),
				SegmentsExperimentConstants.STATUS_FINISHED_WINNER);

		Assert.assertEquals(
			segmentsExperiment.getWinnerSegmentsExperienceId(),
			variantSegmentsExperience.getSegmentsExperienceId());
		Assert.assertEquals(
			segmentsExperiment.getWinnerSegmentsExperienceKey(),
			variantSegmentsExperience.getSegmentsExperienceKey());
	}

	@Test(expected = SegmentsExperimentStatusException.class)
	public void testUpdateSegmentsExperimentStatusToFinishedWithoutWinnerSegmentsExperience()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperience variantSegmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				segmentsExperiment.getGroupId(), segmentsExperiment.getPlid());

		variantSegmentsExperience.setActive(false);

		variantSegmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				variantSegmentsExperience);

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			variantSegmentsExperience.getSegmentsExperienceId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_FINISHED_WINNER);
	}

	@Test(expected = SegmentsExperimentStatusException.class)
	public void testUpdateSegmentsExperimentToRunningWithExistingExperimentInRunning()
		throws Exception {

		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		SegmentsExperiment segmentsExperiment1 = _addSegmentsExperiment(
			segmentsExperience);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment1.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_TERMINATED);

		segmentsExperiment1 =
			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment1);

		SegmentsExperiment segmentsExperiment2 = _addSegmentsExperiment(
			segmentsExperience);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment2.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);

		segmentsExperiment1.setStatus(SegmentsExperimentConstants.STATUS_DRAFT);

		segmentsExperiment1 =
			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment1);

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment1.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_RUNNING);
	}

	@Test(expected = SegmentsExperimentGoalException.class)
	public void testUpdateSegmentsExperimentWithInvalidGoal() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		String invalidGoal =
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel() +
				"_INVALID";

		_segmentsExperimentLocalService.updateSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			invalidGoal, StringPool.BLANK);
	}

	@Test(expected = SegmentsExperimentNameException.class)
	public void testUpdateSegmentsExperimentWithInvalidName() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(), StringPool.BLANK,
			RandomTestUtil.randomString(),
			SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
			StringPool.BLANK);
	}

	@Test(expected = SegmentsExperimentStatusException.class)
	public void testUpdateSegmentsExperimentWithInvalidStatus()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(), Integer.MIN_VALUE);
	}

	@Test(expected = SegmentsExperimentStatusException.class)
	public void testUpdateSegmentsExperimentWithInvalidStatusTransition()
		throws Exception {

		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			SegmentsExperimentConstants.STATUS_TERMINATED);
	}

	@Test
	public void testUpdateSegmentsExperimentWithValidGoal() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperiment updatedSegmentsExperiment =
			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment.getSegmentsExperimentId(),
				segmentsExperiment.getName(),
				segmentsExperiment.getDescription(),
				SegmentsExperimentConstants.Goal.MAX_SCROLL_DEPTH.getLabel(),
				StringPool.BLANK);

		UnicodeProperties typeSettingsUnicodeProperties =
			updatedSegmentsExperiment.getTypeSettingsProperties();

		String goal = typeSettingsUnicodeProperties.getProperty("goal");

		Assert.assertEquals(
			SegmentsExperimentConstants.Goal.MAX_SCROLL_DEPTH.getLabel(), goal);
	}

	@Test
	public void testUpdateSegmentsExperimentWithValidName() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		String name = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString();

		SegmentsExperiment updatedSegmentsExperiment =
			_segmentsExperimentLocalService.updateSegmentsExperiment(
				segmentsExperiment.getSegmentsExperimentId(), name, description,
				SegmentsExperimentConstants.Goal.BOUNCE_RATE.getLabel(),
				StringPool.BLANK);

		Assert.assertEquals(name, updatedSegmentsExperiment.getName());
		Assert.assertEquals(
			description, updatedSegmentsExperiment.getDescription());
	}

	@Test
	public void testUpdateSegmentsExperimentWithValidStatus() throws Exception {
		SegmentsExperiment segmentsExperiment = _addSegmentsExperiment();

		SegmentsExperiment updatedSegmentsExperiment =
			_segmentsExperimentLocalService.updateSegmentsExperimentStatus(
				segmentsExperiment.getSegmentsExperimentId(),
				SegmentsExperimentConstants.STATUS_RUNNING);

		Assert.assertEquals(
			SegmentsExperimentConstants.STATUS_RUNNING,
			updatedSegmentsExperiment.getStatus());
	}

	private SegmentsExperience _addSegmentsExperience() throws Exception {
		return _publishSegmentsExperience(
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), _draftLayout.getPlid()));
	}

	private SegmentsExperiment _addSegmentsExperiment() throws Exception {
		SegmentsExperience segmentsExperience = _addSegmentsExperience();

		return _addSegmentsExperiment(segmentsExperience);
	}

	private SegmentsExperiment _addSegmentsExperiment(
			SegmentsExperience segmentsExperience)
		throws Exception {

		return SegmentsTestUtil.addSegmentsExperiment(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceId(),
			segmentsExperience.getPlid());
	}

	private SegmentsExperience _publishSegmentsExperience(
			SegmentsExperience segmentsExperience)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _draftLayout.getPlid());

		_layoutPageTemplateStructureRelLocalService.
			addLayoutPageTemplateStructureRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layoutPageTemplateStructure.getLayoutPageTemplateStructureId(),
				segmentsExperience.getSegmentsExperienceId(),
				layoutPageTemplateStructure.getDefaultSegmentsExperienceData(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		return _segmentsExperienceLocalService.fetchSegmentsExperience(
			_group.getGroupId(), segmentsExperience.getSegmentsExperienceKey(),
			_layout.getPlid());
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Layout _draftLayout;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Inject
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}