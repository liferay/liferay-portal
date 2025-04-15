/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FormItemManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureService;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/move_stepper_fragment_entry_link"
	},
	service = MVCActionCommand.class
)
public class MoveStepperFragmentEntryLinkMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fragmentEntryLinkId = ParamUtil.getLong(
			actionRequest, "fragmentEntryLinkId");
		String itemId = ParamUtil.getString(actionRequest, "itemId");
		int numberOfSteps = ParamUtil.getInteger(
			actionRequest, "numberOfSteps");
		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		String parentItemId = ParamUtil.getString(
			actionRequest, "parentItemId");
		int position = ParamUtil.getInteger(actionRequest, "position");

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		FormItemManager.LayoutStructureItemChanges layoutStructureItemChanges =
			new FormItemManager.LayoutStructureItemChanges();

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(parentItemId);

		if (Objects.equals(
				formStyledLayoutStructureItem.getFormType(), "simple")) {

			formStyledLayoutStructureItem.setFormType("multistep");
			formStyledLayoutStructureItem.setNumberOfSteps(numberOfSteps);

			layoutStructureItemChanges =
				_formItemManager.changeToMultistepFormType(
					addedFragmentEntryLinks, formStyledLayoutStructureItem,
					_portal.getHttpServletRequest(actionRequest),
					_portal.getHttpServletResponse(actionResponse),
					themeDisplay.getLayout(), layoutStructure, numberOfSteps,
					segmentsExperienceId,
					ServiceContextFactory.getInstance(actionRequest),
					fragmentEntryLinkId);

			position = 0;
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(itemId);

		LayoutStructureItem parentLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructureItem.getParentItemId());

		List<String> childrenItemIds =
			parentLayoutStructureItem.getChildrenItemIds();

		layoutStructureItemChanges.addMovedLayoutStructureItems(
			layoutStructureItem.clone(), childrenItemIds.indexOf(itemId));

		layoutStructure.moveLayoutStructureItem(itemId, parentItemId, position);

		_layoutPageTemplateStructureService.
			updateLayoutPageTemplateStructureData(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId, layoutStructure.toString());

		for (FragmentEntryLink addedFragmentEntryLink :
				addedFragmentEntryLinks) {

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					addedFragmentEntryLink);
			}
		}

		addedFragmentEntryLinks.add(
			_formItemManager.updateNumberOfStepps(
				_portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse), numberOfSteps,
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLinkId)));

		return _formItemManager.getLayoutStructureItemChangesJSONObject(
			addedFragmentEntryLinks,
			_portal.getHttpServletRequest(actionRequest),
			_portal.getHttpServletResponse(actionResponse),
			_jsonFactory.createJSONObject(), layoutStructure,
			layoutStructureItemChanges);
	}

	@Reference
	private FormItemManager _formItemManager;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutPageTemplateStructureService
		_layoutPageTemplateStructureService;

	@Reference
	private Portal _portal;

}