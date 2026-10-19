/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
@Sync
public class RestoreCollectionDisplayConfigMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessAction() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		JSONObject collectionJSONObject = _getCollectionJSONObject(
			_KEY_RECENT_CONTENT);

		String itemId = ContentLayoutTestUtil.addCollectionDisplayToLayout(
			collectionJSONObject, draftLayout, _layoutStructureProvider, null,
			null, 0, segmentsExperienceId);

		JSONObject editableValuesJSONObject = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put("targetCollections", JSONUtil.putAll(itemId)));

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			draftLayout, editableValuesJSONObject.toString(),
			segmentsExperienceId);

		JSONObject disconnectedEditableValuesJSONObject = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(
				"filterKey", StringPool.BLANK
			).put(
				"targetCollections", JSONFactoryUtil.createJSONArray()
			));

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				draftLayout,
				JSONUtil.put(
					"collection",
					_getCollectionJSONObject(_KEY_HIGHEST_RATED_ASSETS)),
				itemId);

		mockLiferayPortletActionRequest.setParameter(
			"editableValuesChanges",
			JSONUtil.put(
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				disconnectedEditableValuesJSONObject
			).toString());

		_updateCollectionDisplayConfigMVCActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertEquals(
			disconnectedEditableValuesJSONObject.toString(),
			_getEditableValues(fragmentEntryLink.getFragmentEntryLinkId()));

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			draftLayout, JSONUtil.put("collection", collectionJSONObject),
			itemId);

		mockLiferayPortletActionRequest.setParameter(
			"filterFragmentEntryLinks",
			JSONUtil.putAll(
				JSONUtil.put(
					"editableValues", editableValuesJSONObject
				).put(
					"fragmentEntryLinkId",
					String.valueOf(fragmentEntryLink.getFragmentEntryLinkId())
				)
			).toString());

		_restoreCollectionDisplayConfigMVCActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertEquals(
			editableValuesJSONObject.toString(),
			_getEditableValues(fragmentEntryLink.getFragmentEntryLinkId()));
	}

	private FragmentEntryLink _addFragmentEntryLink(
			Layout draftLayout, String editableValues,
			long segmentsExperienceId)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), draftLayout.getGroupId(), 0,
				StringUtil.randomString(), StringUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), false,
				JSONUtil.put(
					"fieldSets",
					JSONUtil.put(
						JSONUtil.put(
							"fields",
							JSONUtil.put(
								JSONUtil.put(
									"dataType", "array"
								).put(
									"name", "targetCollections"
								).put(
									"type", "targetCollectionDisplay"
								))))
				).toString(),
				null, 0, false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					draftLayout.getGroupId()));

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			editableValues, fragmentEntry.getCss(),
			fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(),
			ScopeUtil.getItemScopeExternalReferenceCode(
				fragmentEntry.getGroupId(), draftLayout.getGroupId()),
			fragmentEntry.getHtml(), fragmentEntry.getJs(), draftLayout,
			fragmentEntry.getFragmentEntryKey(), segmentsExperienceId,
			fragmentEntry.getType());
	}

	private JSONObject _getCollectionJSONObject(String key) {
		return JSONUtil.put(
			"itemType", AssetEntry.class.getName()
		).put(
			"key", key
		).put(
			"type", InfoListProviderItemSelectorReturnType.class.getName()
		);
	}

	private String _getEditableValues(long fragmentEntryLinkId)
		throws Exception {

		_entityCache.clearCache();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLinkId);

		return fragmentEntryLink.getEditableValues();
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Layout draftLayout, JSONObject itemConfigJSONObject, String itemId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(draftLayout.getCompanyId()),
				draftLayout.getGroup(), draftLayout);

		mockLiferayPortletActionRequest.setParameter(
			"itemConfig", itemConfigJSONObject.toString());
		mockLiferayPortletActionRequest.setParameter("itemId", itemId);

		return mockLiferayPortletActionRequest;
	}

	private static final String _KEY_HIGHEST_RATED_ASSETS =
		"com.liferay.asset.internal.info.collection.provider." +
			"HighestRatedAssetsInfoCollectionProvider";

	private static final String _KEY_RECENT_CONTENT =
		"com.liferay.asset.internal.info.collection.provider." +
			"RecentContentInfoCollectionProvider";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/restore_collection_display_config"
	)
	private MVCActionCommand _restoreCollectionDisplayConfigMVCActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/update_collection_display_config"
	)
	private MVCActionCommand _updateCollectionDisplayConfigMVCActionCommand;

}