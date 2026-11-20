/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_2_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Objects;

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
public class LayoutPageTemplateStructureRelUpgradeProcessTest
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

		_fragmentEntry = _addFragmentEntry(_group.getGroupId());
	}

	@Test
	@TestInfo("LPD-108786")
	public void testUpgrade() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		FragmentEntry companyGroupFragmentEntry = _addFragmentEntry(
			companyGroup.getGroupId());

		String expectedFragmentEntryRendererKey = RandomTestUtil.randomString();

		long layoutPageTemplateStructureRelId =
			_addMasterLayoutPageTemplateEntryWithLegacyDropZone(
				companyGroupFragmentEntry.getFragmentEntryKey(),
				expectedFragmentEntryRendererKey,
				_fragmentEntry.getFragmentEntryKey());

		runUpgrade();

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				getLayoutPageTemplateStructureRel(
					layoutPageTemplateStructureRelId);

		JSONObject itemConfigJSONObject = _getDropZoneItemConfigJSONObject(
			JSONFactoryUtil.createJSONObject(
				layoutPageTemplateStructureRel.getData()));

		Assert.assertFalse(itemConfigJSONObject.has("fragmentEntryKeys"));

		JSONArray fragmentEntriesJSONArray = itemConfigJSONObject.getJSONArray(
			"fragmentEntries");

		JSONObject companyGroupFragmentEntryJSONObject =
			fragmentEntriesJSONArray.getJSONObject(0);

		Assert.assertEquals(
			companyGroupFragmentEntry.getExternalReferenceCode(),
			companyGroupFragmentEntryJSONObject.getString("fragmentEntryERC"));
		Assert.assertFalse(
			companyGroupFragmentEntryJSONObject.has(
				"fragmentEntryRendererKey"));
		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			companyGroupFragmentEntryJSONObject.getString(
				"fragmentEntryScopeERC"));

		JSONObject rendererFragmentJSONObject =
			fragmentEntriesJSONArray.getJSONObject(1);

		Assert.assertFalse(rendererFragmentJSONObject.has("fragmentEntryERC"));
		Assert.assertEquals(
			expectedFragmentEntryRendererKey,
			rendererFragmentJSONObject.getString("fragmentEntryRendererKey"));
		Assert.assertFalse(
			rendererFragmentJSONObject.has("fragmentEntryScopeERC"));

		JSONObject fragmentEntryJSONObject =
			fragmentEntriesJSONArray.getJSONObject(2);

		Assert.assertEquals(
			_fragmentEntry.getExternalReferenceCode(),
			fragmentEntryJSONObject.getString("fragmentEntryERC"));
		Assert.assertFalse(
			fragmentEntryJSONObject.has("fragmentEntryRendererKey"));
		Assert.assertFalse(
			fragmentEntryJSONObject.has("fragmentEntryScopeERC"));

		Assert.assertEquals(3, fragmentEntriesJSONArray.length());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		long layoutPageTemplateStructureRelId =
			_addMasterLayoutPageTemplateEntryWithLegacyDropZone(
				_fragmentEntry.getFragmentEntryKey());

		return _layoutPageTemplateStructureRelLocalService.
			getLayoutPageTemplateStructureRel(layoutPageTemplateStructureRelId);
	}

	@Override
	protected CTService<?> getCTService() {
		return _layoutPageTemplateStructureRelLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.layout.page.template.internal.upgrade.v6_2_2." +
				"LayoutPageTemplateStructureRelUpgradeProcess");

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry(_group.getGroupId());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			(LayoutPageTemplateStructureRel)ctModel;

		layoutPageTemplateStructureRel.setData(
			_injectLegacyFragmentEntryKeys(
				layoutPageTemplateStructureRel.getData(),
				fragmentEntry.getFragmentEntryKey()));

		return _layoutPageTemplateStructureRelLocalService.
			updateLayoutPageTemplateStructureRel(
				layoutPageTemplateStructureRel);
	}

	private FragmentEntry _addFragmentEntry(long groupId) throws Exception {
		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(), StringPool.BLANK,
				ServiceContextTestUtil.getServiceContext(groupId));

		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), fragmentCollection.getGroupId(),
			fragmentCollection.getFragmentCollectionId(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), StringPool.BLANK, false,
			StringPool.BLANK, null, 0, false, false,
			FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				fragmentCollection.getGroupId()));
	}

	private long _addMasterLayoutPageTemplateEntryWithLegacyDropZone(
			String... fragmentEntryKeys)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layoutPageTemplateEntry.getPlid());

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				fetchLayoutPageTemplateStructureRel(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId(),
					_segmentsExperienceLocalService.
						fetchDefaultSegmentsExperienceId(
							layoutPageTemplateEntry.getPlid()));

		layoutPageTemplateStructureRel.setData(
			_injectLegacyFragmentEntryKeys(
				layoutPageTemplateStructureRel.getData(), fragmentEntryKeys));

		layoutPageTemplateStructureRel =
			_layoutPageTemplateStructureRelLocalService.
				updateLayoutPageTemplateStructureRel(
					layoutPageTemplateStructureRel);

		return layoutPageTemplateStructureRel.
			getLayoutPageTemplateStructureRelId();
	}

	private JSONObject _getDropZoneItemConfigJSONObject(
		JSONObject dataJSONObject) {

		JSONObject itemsJSONObject = dataJSONObject.getJSONObject("items");

		for (String itemId : itemsJSONObject.keySet()) {
			JSONObject itemJSONObject = itemsJSONObject.getJSONObject(itemId);

			if (Objects.equals(
					LayoutDataItemTypeConstants.TYPE_DROP_ZONE,
					itemJSONObject.getString("type"))) {

				return itemJSONObject.getJSONObject("config");
			}
		}

		return null;
	}

	private String _injectLegacyFragmentEntryKeys(
			String data, String... fragmentEntryKeys)
		throws Exception {

		JSONObject dataJSONObject = JSONFactoryUtil.createJSONObject(data);

		JSONObject itemConfigJSONObject = _getDropZoneItemConfigJSONObject(
			dataJSONObject);

		itemConfigJSONObject.put(
			"fragmentEntryKeys", JSONUtil.putAll((Object[])fragmentEntryKeys));

		return dataJSONObject.toString();
	}

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}