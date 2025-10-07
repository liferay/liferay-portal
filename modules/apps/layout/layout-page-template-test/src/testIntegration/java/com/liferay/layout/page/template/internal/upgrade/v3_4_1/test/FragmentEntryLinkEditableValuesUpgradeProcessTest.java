/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v3_4_1.test;

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
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	public void testUpgradeBasicComponentSeparator() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-separator");

		String expected = RandomTestUtil.randomString();

		_assertUpgrade(
			JSONUtil.put("separatorColor", expected), fragmentEntry,
			JSONUtil.put("borderColor", expected));
	}

	@Test
	public void testUpgradeBasicComponentVideo() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-video");

		String expectedVideoHeight = RandomTestUtil.randomString();
		String expectedVideoWidth = RandomTestUtil.randomString();

		_assertUpgrade(
			JSONUtil.put(
				"videoHeight", expectedVideoHeight
			).put(
				"videoWidth", expectedVideoWidth
			),
			fragmentEntry,
			JSONUtil.put(
				"height", expectedVideoHeight
			).put(
				"width", expectedVideoWidth
			));
	}

	@Test
	public void testUpgradeBorderRadius() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		_assertUpgrade(
			_jsonFactory.createJSONObject(), fragmentEntry,
			_jsonFactory.createJSONObject());
		_assertUpgrade(
			_jsonFactory.createJSONObject(), fragmentEntry,
			JSONUtil.put("borderRadius", RandomTestUtil.randomString()));
		_assertUpgrade(
			JSONUtil.put("borderRadius", "0.375rem"), fragmentEntry,
			JSONUtil.put("borderRadius", "lg"));
		_assertUpgrade(
			JSONUtil.put("borderRadius", StringPool.BLANK), fragmentEntry,
			JSONUtil.put("borderRadius", "none"));
		_assertUpgrade(
			JSONUtil.put("borderRadius", "0.1875rem"), fragmentEntry,
			JSONUtil.put("borderRadius", "sm"));
	}

	@Test
	public void testUpgradeMarginBottom() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		String expectedMarginBottom = String.valueOf(
			RandomTestUtil.randomInt());

		JSONObject expectedJSONObject = JSONUtil.put(
			"marginBottom", expectedMarginBottom);

		_assertUpgrade(
			expectedJSONObject, fragmentEntry,
			JSONUtil.put(
				"bottomSpacing", expectedMarginBottom
			).put(
				"marginBottom", String.valueOf(RandomTestUtil.randomInt())
			));
		_assertUpgrade(
			expectedJSONObject, fragmentEntry,
			JSONUtil.put("marginBottom", expectedMarginBottom));
	}

	@Test
	public void testUpgradeShadow() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		_assertUpgrade(
			_jsonFactory.createJSONObject(), fragmentEntry,
			JSONUtil.put("boxShadow", RandomTestUtil.randomString()));
		_assertUpgrade(
			JSONUtil.put("shadow", "0 1rem 3rem rgba(0, 0, 0, .175)"),
			fragmentEntry, JSONUtil.put("boxShadow", "lg"));
		_assertUpgrade(
			JSONUtil.put("shadow", "0 .125rem .25rem rgba(0, 0, 0, .075)"),
			fragmentEntry, JSONUtil.put("boxShadow", "sm"));
	}

	@Test
	public void testUpgradeTextAlign() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		String expectedTextAlign = RandomTestUtil.randomString();

		JSONObject expectedJSONObject = JSONUtil.put(
			"textAlign", expectedTextAlign);

		_assertUpgrade(
			expectedJSONObject, fragmentEntry,
			JSONUtil.put("buttonAlign", expectedTextAlign));
		_assertUpgrade(
			expectedJSONObject, fragmentEntry,
			JSONUtil.put("contentAlign", expectedTextAlign));
		_assertUpgrade(
			expectedJSONObject, fragmentEntry,
			JSONUtil.put("imageAlign", expectedTextAlign));
		_assertUpgrade(
			expectedJSONObject, fragmentEntry,
			JSONUtil.put("textAlign", expectedTextAlign));
	}

	@Test
	public void testUpgradeTextColor() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		_assertUpgrade(
			JSONUtil.put("textColor", "#DA1414"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "danger")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#272833"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "dark")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#393A4A"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "gray-dark")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#2E5AAC"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "info")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#F1F2F5"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "light")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#F7F8F9"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "lighter")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#0B5FFF"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "primary")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#6B6C7E"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "secondary")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#287D3C"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "success")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#B95000"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "warning")));
		_assertUpgrade(
			JSONUtil.put("textColor", "#FFFFFF"), fragmentEntry,
			JSONUtil.put("textColor", JSONUtil.put("cssClass", "white")));

		String expectedTextColor = RandomTestUtil.randomString();

		_assertUpgrade(
			JSONUtil.put("textColor", expectedTextColor), fragmentEntry,
			JSONUtil.put(
				"textColor", JSONUtil.put("cssClass", expectedTextColor)));

		JSONObject expectedJSONObject = JSONUtil.put(
			"textColor",
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		_assertUpgrade(expectedJSONObject, fragmentEntry, expectedJSONObject);
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			fragmentEntry.getFragmentEntryId(), _segmentsExperienceId,
			_layout.getPlid(), fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString())
			).toString(),
			StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(),
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
			JSONObject expectedJSONObject, FragmentEntry fragmentEntry,
			JSONObject jsonObject)
		throws Exception {

		FragmentEntryLink draftFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					jsonObject
				).toString(),
				fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), _draftLayout,
				fragmentEntry.getFragmentEntryKey(), _segmentsExperienceId,
				fragmentEntry.getType());

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
		"com.liferay.layout.page.template.internal.upgrade.v3_4_1." +
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