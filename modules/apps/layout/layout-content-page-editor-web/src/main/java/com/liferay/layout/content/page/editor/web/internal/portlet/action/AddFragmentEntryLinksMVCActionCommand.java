/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.exception.FormContainerParentItemRequiredException;
import com.liferay.layout.content.page.editor.web.internal.exception.NoninstanceablePortletException;
import com.liferay.layout.content.page.editor.web.internal.manager.FormItemManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.util.CheckNoninstanceablePortletThreadLocal;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/add_fragment_entry_links"
	},
	service = MVCActionCommand.class
)
public class AddFragmentEntryLinksMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _processAddFragmentEntryLinks(
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

		if (exception instanceof FormContainerParentItemRequiredException) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			errorMessage = _language.get(
				themeDisplay.getLocale(),
				"this-form-component-can-only-be-placed-inside-a-mapped-form-" +
					"container");
		}
		else if (exception.getCause() instanceof
					NoninstanceablePortletException) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			NoninstanceablePortletException noninstanceablePortletException =
				(NoninstanceablePortletException)exception.getCause();

			Portlet portlet = _portletLocalService.getPortletById(
				themeDisplay.getCompanyId(),
				noninstanceablePortletException.getPortletId());

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(actionRequest);

			HttpSession httpSession = httpServletRequest.getSession();

			errorMessage = _language.format(
				themeDisplay.getRequest(),
				"the-fragment-could-not-be-added-because-it-contains-a-" +
					"widget-x-that-can-only-appear-once-on-the-page",
				new String[] {
					_portal.getPortletTitle(
						portlet, httpSession.getServletContext(),
						themeDisplay.getLocale())
				});
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

	private JSONObject _processAddFragmentEntryLinks(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CheckNoninstanceablePortletThreadLocal.
					setCheckNoninstanceablePortletWithSafeCloseable(true)) {

			String fragmentEntryKey = ParamUtil.getString(
				actionRequest, "fragmentEntryKey");

			FragmentComposition fragmentComposition =
				_fragmentCollectionContributorRegistry.getFragmentComposition(
					fragmentEntryKey);

			if (fragmentComposition == null) {
				long groupId = ParamUtil.getLong(actionRequest, "groupId");

				fragmentComposition =
					_fragmentCompositionService.fetchFragmentComposition(
						groupId, fragmentEntryKey);
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long segmentsExperienceId = ParamUtil.getLong(
				actionRequest, "segmentsExperienceId");

			LayoutStructure layoutStructure =
				LayoutStructureUtil.getLayoutStructure(
					themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
					segmentsExperienceId);

			String parentItemId = ParamUtil.getString(
				actionRequest, "parentItemId");

			int position = ParamUtil.getInteger(actionRequest, "position");

			List<FragmentEntryLink> fragmentEntryLinks =
				_layoutsImporter.importPageElement(
					themeDisplay.getUserId(), themeDisplay.getLayout(),
					layoutStructure, parentItemId,
					fragmentComposition.getData(), position, false,
					segmentsExperienceId);

			_formItemManager.checkFormContainerParentItemRequired(
				fragmentEntryLinks, layoutStructure, parentItemId);

			for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
				for (FragmentEntryLinkListener fragmentEntryLinkListener :
						_fragmentEntryLinkListenerRegistry.
							getFragmentEntryLinkListeners()) {

					fragmentEntryLinkListener.onAddFragmentEntryLink(
						fragmentEntryLink);
				}
			}

			JSONObject fragmentEntryLinksJSONObject =
				_jsonFactory.createJSONObject();

			layoutStructure = LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(parentItemId);

			for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
				fragmentEntryLinksJSONObject.put(
					String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
					_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
						fragmentEntryLink,
						_portal.getHttpServletRequest(actionRequest),
						_portal.getHttpServletResponse(actionResponse),
						layoutStructure));
			}

			return JSONUtil.put(
				"addedItemId",
				() -> {
					List<String> childrenItemIds =
						layoutStructureItem.getChildrenItemIds();

					return childrenItemIds.get(position);
				}
			).put(
				"fragmentEntryLinks", fragmentEntryLinksJSONObject
			).put(
				"layoutData", layoutStructure.toJSONObject()
			);
		}
	}

	@Reference
	private FormItemManager _formItemManager;

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutsImporter _layoutsImporter;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}