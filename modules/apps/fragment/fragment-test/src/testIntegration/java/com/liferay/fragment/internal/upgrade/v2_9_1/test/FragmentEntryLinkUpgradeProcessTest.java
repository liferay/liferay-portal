/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v2_9_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	public void testUpgrade() throws Exception {
		JSONObject emptyJSONObject = _jsonFactory.createJSONObject();

		JSONObject editableValuesJSONObject1 = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
			emptyJSONObject
		).put(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
			emptyJSONObject
		).put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			emptyJSONObject
		);

		FragmentEntryLink draftLayoutFragmentEntryLink1 =
			_addFragmentEntryLinkToDraftLayout(editableValuesJSONObject1);

		JSONObject editableValuesJSONObject2 = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString())
		).put(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString())
		).put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString())
		).put(
			RandomTestUtil.randomString(),
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString())
		).put(
			RandomTestUtil.randomString(), emptyJSONObject
		);

		FragmentEntryLink draftLayoutFragmentEntryLink2 =
			_addFragmentEntryLinkToDraftLayout(editableValuesJSONObject2);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_layout.getGroupId(), _layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 2, fragmentEntryLinks.size());

		FragmentEntryLink publishedLayoutFragmentEntryLink1 =
			_getPublishedLayoutFragmentEntryLink(
				editableValuesJSONObject1,
				draftLayoutFragmentEntryLink1.getFragmentEntryLinkId());
		FragmentEntryLink publishedLayoutFragmentEntryLink2 =
			_getPublishedLayoutFragmentEntryLink(
				editableValuesJSONObject2,
				draftLayoutFragmentEntryLink2.getFragmentEntryLinkId());

		runUpgrade();

		_assertFragmentEntryLinkEditableValues(
			emptyJSONObject,
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				draftLayoutFragmentEntryLink1.getFragmentEntryLinkId()),
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				publishedLayoutFragmentEntryLink1.getFragmentEntryLinkId()));
		_assertFragmentEntryLinkEditableValues(
			editableValuesJSONObject2,
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				draftLayoutFragmentEntryLink2.getFragmentEntryLinkId()),
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				publishedLayoutFragmentEntryLink2.getFragmentEntryLinkId()));
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			"{}", _draftLayout, _segmentsExperienceId);
	}

	@Override
	protected CTService<?> getCTService() {
		return _fragmentEntryLinkLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(2, 9, 1));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		FragmentEntryLink fragmentEntryLink = (FragmentEntryLink)ctModel;

		return _fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())
			).toString());
	}

	private FragmentEntryLink _addFragmentEntryLinkToDraftLayout(
			JSONObject editableValuesJSONObject)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				editableValuesJSONObject.toString(), _draftLayout,
				_segmentsExperienceId);

		_assertFragmentEntryLinkEditableValues(
			editableValuesJSONObject, fragmentEntryLink);

		return fragmentEntryLink;
	}

	private void _assertFragmentEntryLinkEditableValues(
			JSONObject expectedJSONObject,
			FragmentEntryLink... fragmentEntryLinks)
		throws Exception {

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			Assert.assertTrue(
				fragmentEntryLink.getEditableValues(),
				JSONUtil.equals(
					expectedJSONObject,
					fragmentEntryLink.getEditableValuesJSONObject()));
		}
	}

	private FragmentEntryLink _getPublishedLayoutFragmentEntryLink(
			JSONObject expectedJSONObject, long originalFragmentEntryLinkId)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_group.getGroupId(), originalFragmentEntryLinkId,
				_layout.getPlid());

		_assertFragmentEntryLinkEditableValues(
			expectedJSONObject, fragmentEntryLink);

		return fragmentEntryLink;
	}

	@Inject(
		filter = "(&(component.name=com.liferay.fragment.internal.upgrade.registry.FragmentServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private Layout _draftLayout;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private MultiVMPool _multiVMPool;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}