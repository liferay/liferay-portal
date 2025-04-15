/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FormItemManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureService;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
		"mvc.command.name=/layout_content_page_editor/add_stepper_fragment_entry_link"
	},
	service = MVCActionCommand.class
)
public class AddStepperFragmentEntryLinkMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	protected FragmentEntryLink addFragmentEntryLink(
			ActionRequest actionRequest)
		throws PortalException {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		String fragmentEntryKey = ParamUtil.getString(
			actionRequest, "fragmentEntryKey");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		FragmentEntry fragmentEntry =
			_fragmentEntryLinkManager.getFragmentEntry(
				groupId, fragmentEntryKey, serviceContext.getLocale());

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(fragmentEntryKey);

		if ((fragmentEntry == null) && (fragmentRenderer == null)) {
			throw new NoSuchEntryException();
		}

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		if (fragmentEntry != null) {
			String contributedRendererKey = null;

			if (fragmentEntry.getFragmentEntryId() == 0) {
				contributedRendererKey = fragmentEntryKey;
			}

			return _fragmentEntryLinkService.addFragmentEntryLink(
				null, serviceContext.getScopeGroupId(), 0,
				fragmentEntry.getFragmentEntryId(), segmentsExperienceId,
				serviceContext.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(), null, StringPool.BLANK, 0,
				contributedRendererKey, fragmentEntry.getType(),
				serviceContext);
		}

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(null);

		return _fragmentEntryLinkService.addFragmentEntryLink(
			null, serviceContext.getScopeGroupId(), 0, 0, segmentsExperienceId,
			serviceContext.getPlid(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK,
			fragmentRenderer.getConfiguration(defaultFragmentRendererContext),
			StringPool.BLANK, StringPool.BLANK, 0, fragmentEntryKey,
			fragmentRenderer.getType(), serviceContext);
	}

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _processAddFragmentEntryLink(
			actionRequest, actionResponse);

		SessionMessages.add(actionRequest, "fragmentEntryLinkAdded");

		return jsonObject;
	}

	@Override
	protected JSONObject processException(
		ActionRequest actionRequest, Exception exception) {

		if (exception instanceof LockedLayoutException) {
			return processLockedLayoutException(actionRequest);
		}

		String errorMessage = "an-unexpected-error-occurred";

		if (exception instanceof FragmentEntryContentException) {
			errorMessage = exception.getMessage();
		}
		else if (exception instanceof NoSuchEntryException) {
			errorMessage =
				"the-fragment-can-no-longer-be-added-because-it-has-been-" +
					"deleted";
		}

		return JSONUtil.put(
			"error",
			_language.get(
				_portal.getHttpServletRequest(actionRequest), errorMessage));
	}

	private JSONObject _processAddFragmentEntryLink(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		String parentItemId = ParamUtil.getString(
			actionRequest, "parentItemId");
		int numberOfSteps = ParamUtil.getInteger(
			actionRequest, "numberOfSteps");
		int position = ParamUtil.getInteger(actionRequest, "position");

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		FragmentEntryLink stepperFragmentEntryLink = addFragmentEntryLink(
			actionRequest);

		addedFragmentEntryLinks.add(stepperFragmentEntryLink);

		FormItemManager.LayoutStructureItemChanges layoutStructureItemChanges =
			new FormItemManager.LayoutStructureItemChanges();

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				stepperFragmentEntryLink.getFragmentEntryLinkId(), parentItemId,
				position);

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
					stepperFragmentEntryLink.getFragmentEntryLinkId());
		}

		layoutStructureItemChanges.addAddedLayoutStructureItems(
			fragmentStyledLayoutStructureItem);

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

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		addedFragmentEntryLinks.add(
			_formItemManager.updateNumberOfStepps(
				httpServletRequest, httpServletResponse, numberOfSteps,
				stepperFragmentEntryLink));

		return _formItemManager.getLayoutStructureItemChangesJSONObject(
			addedFragmentEntryLinks, httpServletRequest, httpServletResponse,
			_jsonFactory.createJSONObject(), layoutStructure,
			layoutStructureItemChanges);
	}

	@Reference
	private FormItemManager _formItemManager;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateStructureService
		_layoutPageTemplateStructureService;

	@Reference
	private Portal _portal;

}