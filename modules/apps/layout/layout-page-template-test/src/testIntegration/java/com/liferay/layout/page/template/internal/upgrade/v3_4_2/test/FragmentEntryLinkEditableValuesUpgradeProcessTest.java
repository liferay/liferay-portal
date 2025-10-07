/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v3_4_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkEditableValuesUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-separator");

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	public void testUpgrade() throws Exception {
		_assertUpgrade(
			_jsonFactory.createJSONObject(), _jsonFactory.createJSONObject());

		JSONObject jsonObject = JSONUtil.put(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_assertUpgrade(jsonObject, jsonObject);

		String expected = RandomTestUtil.randomString();

		_assertUpgrade(
			JSONUtil.put("bottomSpacing", expected),
			JSONUtil.put("bottomSpacing", expected));
		_assertUpgrade(
			JSONUtil.put("bottomSpacing", expected),
			JSONUtil.put("verticalSpace", expected));
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			_fragmentEntry.getFragmentEntryId(), _segmentsExperienceId,
			_layout.getPlid(), _fragmentEntry.getCss(),
			_fragmentEntry.getHtml(), _fragmentEntry.getJs(),
			_fragmentEntry.getConfiguration(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())
			).toString(),
			StringPool.BLANK, 0, _fragmentEntry.getFragmentEntryKey(),
			_fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	protected CTService<?> getCTService() {
		return _fragmentEntryLinkLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

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

	private void _assertEditableValues(
			JSONObject expectedJSONObject, FragmentEntryLink fragmentEntryLink)
		throws Exception {

		JSONObject editablesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject configurationJSONObject = editablesJSONObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertTrue(
			configurationJSONObject.toString(),
			JSONUtil.equals(expectedJSONObject, configurationJSONObject));
	}

	private void _assertFragmentEntryLink(
			JSONObject expectedJSONObject, long fragmentEntryLinkId,
			Layout layout, long segmentsExperienceId)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLinkId);

		Assert.assertEquals(layout.getPlid(), fragmentEntryLink.getPlid());
		Assert.assertEquals(
			segmentsExperienceId, fragmentEntryLink.getSegmentsExperienceId());

		_assertEditableValues(expectedJSONObject, fragmentEntryLink);
	}

	private void _assertUpgrade(
			JSONObject expectedJSONObject, JSONObject jsonObject)
		throws Exception {

		FragmentEntryLink draftFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					jsonObject
				).toString(),
				_fragmentEntry.getCss(), _fragmentEntry.getConfiguration(),
				_fragmentEntry.getFragmentEntryId(), _fragmentEntry.getHtml(),
				_fragmentEntry.getJs(), _draftLayout,
				_fragmentEntry.getFragmentEntryKey(), _segmentsExperienceId,
				_fragmentEntry.getType());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_layout.getGroupId(),
				draftFragmentEntryLink.getFragmentEntryLinkId(),
				_layout.getPlid());

		Assert.assertNotNull(fragmentEntryLink);

		runUpgrade();

		_assertFragmentEntryLink(
			expectedJSONObject, draftFragmentEntryLink.getFragmentEntryLinkId(),
			_draftLayout, _segmentsExperienceId);
		_assertFragmentEntryLink(
			expectedJSONObject, fragmentEntryLink.getFragmentEntryLinkId(),
			_layout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()));
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.page.template.internal.upgrade.v3_4_2." +
			"FragmentEntryLinkEditableValuesUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private Layout _draftLayout;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	private FragmentEntry _fragmentEntry;

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