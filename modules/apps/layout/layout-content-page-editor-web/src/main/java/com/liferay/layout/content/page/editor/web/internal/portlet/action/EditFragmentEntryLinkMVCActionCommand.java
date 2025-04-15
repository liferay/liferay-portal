/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/edit_fragment_entry_link"
	},
	service = MVCActionCommand.class
)
public class EditFragmentEntryLinkMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fragmentEntryLinkId = ParamUtil.getLong(
			actionRequest, "fragmentEntryLinkId");

		String editableValues = ParamUtil.getString(
			actionRequest, "editableValues");

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.updateFragmentEntryLink(
				fragmentEntryLinkId, editableValues);

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.onUpdateFragmentEntryLink(
				fragmentEntryLink);
		}

		hideDefaultSuccessMessage(actionRequest);

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				fragmentEntryLink.getSegmentsExperienceId());

		return JSONUtil.put(
			"fragmentEntryLink",
			_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
				fragmentEntryLink, _portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse),
				layoutStructure));
	}

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Reference
	private Portal _portal;

}