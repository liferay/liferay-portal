/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.drop.zone.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class DropZoneFragmentEntryLinkListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testOnUpdateFragmentEntryLinkConfigurationValues()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.updateDisplaySettings(
			group.getGroupId(),
			ListUtil.fromArray(LocaleUtil.US, LocaleUtil.SPAIN), LocaleUtil.US);

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		Layout draftLayout = layout.fetchDraftLayout();

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			draftLayout, group);

		_updateConfigurationValues(
			draftLayout, fragmentEntryLink, group, JSONUtil.put("en_US", "1"));

		Assert.assertEquals(
			1, _getFragmentDropZoneItemsCount(fragmentEntryLink));

		String childItemId = _addDropZoneChildLayoutStructureItem(
			fragmentEntryLink);

		_updateConfigurationValues(
			draftLayout, fragmentEntryLink, group,
			JSONUtil.put(
				"en_US", "1"
			).put(
				"es_ES", "3"
			));

		Assert.assertEquals(
			3, _getFragmentDropZoneItemsCount(fragmentEntryLink));
		Assert.assertTrue(
			_hasDropZoneChildLayoutStructureItem(
				childItemId, fragmentEntryLink));

		_updateConfigurationValues(
			draftLayout, fragmentEntryLink, group,
			JSONUtil.put(
				"en_US", "3"
			).put(
				"es_ES", "1"
			));

		Assert.assertEquals(
			3, _getFragmentDropZoneItemsCount(fragmentEntryLink));
		Assert.assertTrue(
			_hasDropZoneChildLayoutStructureItem(
				childItemId, fragmentEntryLink));
	}

	private String _addDropZoneChildLayoutStructureItem(
			FragmentEntryLink fragmentEntryLink)
		throws Exception {

		LayoutStructure layoutStructure = _getLayoutStructure(
			fragmentEntryLink);

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		List<String> childrenItemIds = layoutStructureItem.getChildrenItemIds();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				childrenItemIds.get(0), 0);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), fragmentEntryLink.getGroupId(),
				fragmentEntryLink.getPlid(),
				fragmentEntryLink.getSegmentsExperienceId(),
				layoutStructure.toString());

		return containerStyledLayoutStructureItem.getItemId();
	}

	private FragmentEntryLink _addFragmentEntryLink(
			Layout draftLayout, Group group)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomString(), StringUtil.randomString(),
				StringPool.BLANK, _HTML, StringPool.BLANK, false,
				_getConfiguration(), null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, fragmentEntry.getCss(),
			fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(),
			ScopeUtil.getItemScopeExternalReferenceCode(
				fragmentEntry.getGroupId(), draftLayout.getGroupId()),
			fragmentEntry.getHtml(), fragmentEntry.getJs(), draftLayout,
			fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(), null,
			0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));
	}

	private String _getConfiguration() {
		return JSONUtil.put(
			"fieldSets",
			JSONUtil.putAll(
				JSONUtil.put(
					"fields",
					JSONUtil.putAll(
						JSONUtil.put(
							"dataType", "int"
						).put(
							"defaultValue", "1"
						).put(
							"label", "number-of-panels"
						).put(
							"localizable", true
						).put(
							"name", _FIELD_NAME
						).put(
							"type", "text"
						))))
		).toString();
	}

	private int _getFragmentDropZoneItemsCount(
		FragmentEntryLink fragmentEntryLink) {

		int count = 0;

		LayoutStructure layoutStructure = _getLayoutStructure(
			fragmentEntryLink);

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		for (String childrenItemId : layoutStructureItem.getChildrenItemIds()) {
			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if ((childLayoutStructureItem instanceof
					FragmentDropZoneLayoutStructureItem) &&
				!layoutStructure.isItemMarkedForDeletion(childrenItemId)) {

				count++;
			}
		}

		return count;
	}

	private LayoutStructure _getLayoutStructure(
		FragmentEntryLink fragmentEntryLink) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					fragmentEntryLink.getGroupId(),
					fragmentEntryLink.getPlid());

		return LayoutStructure.of(
			layoutPageTemplateStructure.getData(
				fragmentEntryLink.getSegmentsExperienceId()));
	}

	private boolean _hasDropZoneChildLayoutStructureItem(
		String childItemId, FragmentEntryLink fragmentEntryLink) {

		LayoutStructure layoutStructure = _getLayoutStructure(
			fragmentEntryLink);

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLink.getFragmentEntryLinkId());

		for (String childrenItemId : layoutStructureItem.getChildrenItemIds()) {
			LayoutStructureItem childLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			List<String> grandChildrenItemIds =
				childLayoutStructureItem.getChildrenItemIds();

			if (grandChildrenItemIds.contains(childItemId) &&
				!layoutStructure.isItemMarkedForDeletion(childItemId)) {

				return true;
			}
		}

		return false;
	}

	private void _updateConfigurationValues(
			Layout draftLayout, FragmentEntryLink fragmentEntryLink,
			Group group, JSONObject valueJSONObject)
		throws Exception {

		fragmentEntryLink =
			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				TestPropsValues.getUserId(),
				fragmentEntryLink.getFragmentEntryLinkId(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(_FIELD_NAME, valueJSONObject)
				).toString(),
				false);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(group.getCompanyId()), group,
				draftLayout);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setRequest(mockHttpServletRequest);
		themeDisplay.setResponse(new MockHttpServletResponse());

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		_fragmentEntryLinkListener.
			onUpdateFragmentEntryLinkConfigurationValues(fragmentEntryLink);

		ServiceContextThreadLocal.popServiceContext();
	}

	private static final String _FIELD_NAME = "numberOfPanels";

	private static final String _HTML =
		"[#list 1..configuration.numberOfPanels as i]" +
			"<lfr-drop-zone id=\"column-${i}\"></lfr-drop-zone>[/#list]";

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject(
		filter = "component.name=com.liferay.fragment.entry.processor.drop.zone.listener.DropZoneFragmentEntryLinkListener"
	)
	private FragmentEntryLinkListener _fragmentEntryLinkListener;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}