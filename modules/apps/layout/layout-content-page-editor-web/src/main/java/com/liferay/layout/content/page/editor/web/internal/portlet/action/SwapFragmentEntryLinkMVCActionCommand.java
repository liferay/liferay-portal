/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.exception.NoninstanceablePortletException;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.CheckNoninstanceablePortletThreadLocal;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/swap_fragment_entry_link"
	},
	service = MVCActionCommand.class
)
public class SwapFragmentEntryLinkMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

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

	private FragmentEntryLink _addFragmentEntryLink(ActionRequest actionRequest)
		throws PortalException {

		String fragmentEntryKey = ParamUtil.getString(
			actionRequest, "fragmentEntryKey");

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(fragmentEntryKey);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		if (fragmentRenderer != null) {
			DefaultFragmentRendererContext defaultFragmentRendererContext =
				new DefaultFragmentRendererContext(null);

			return _fragmentEntryLinkService.addFragmentEntryLink(
				null, serviceContext.getScopeGroupId(), null, null, null,
				segmentsExperienceId, serviceContext.getPlid(),
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				_jsonFactory.toString(
					fragmentRenderer.getConfigurationJSONObject(
						defaultFragmentRendererContext)),
				StringPool.BLANK, StringPool.BLANK, 0, fragmentEntryKey,
				fragmentRenderer.getType(), serviceContext);
		}

		FragmentEntry fragmentEntry =
			_fragmentEntryLinkManager.getFragmentEntry(
				ParamUtil.getLong(actionRequest, "groupId"), fragmentEntryKey,
				serviceContext.getLocale());

		if ((fragmentEntry == null) && (fragmentRenderer == null)) {
			throw new NoSuchEntryException();
		}

		String contributedRendererKey = null;

		if (fragmentEntry.getFragmentEntryId() == 0) {
			contributedRendererKey = fragmentEntryKey;
		}

		return _fragmentEntryLinkService.addFragmentEntryLink(
			null, serviceContext.getScopeGroupId(), null,
			fragmentEntry.getExternalReferenceCode(),
			ScopeUtil.getItemScopeExternalReferenceCode(
				fragmentEntry.getGroupId(), serviceContext.getScopeGroupId()),
			segmentsExperienceId, serviceContext.getPlid(),
			fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.getConfiguration(), null,
			StringPool.BLANK, 0, contributedRendererKey,
			fragmentEntry.getType(), serviceContext);
	}

	private FragmentEntryLink _cloneFragmentEntryLink(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			actionRequest);

		if (fragmentEntryLink == null) {
			return null;
		}

		JSONObject editableValuesJSONObject =
			_fragmentEntryLinkManager.mergeEditableValuesJSONObject(
				_jsonFactory.createJSONObject(
					fragmentEntryLink.getEditableValues()),
				_jsonFactory.createJSONObject(
					ParamUtil.getString(actionRequest, "editableValues")));

		fragmentEntryLink = _fragmentEntryLinkService.updateFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId(),
			editableValuesJSONObject.toString());

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				fragmentEntryLink.getCompanyId(),
				_portal.getHttpServletRequest(actionRequest),
				_portal.getHttpServletResponse(actionResponse),
				LocaleUtil.getMostRelevantLocale(),
				FragmentEntryLinkConstants.EDIT,
				fragmentEntryLink.getGroupId());

		String processedHTML =
			_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, fragmentEntryProcessorContext);

		JSONObject newEditableValuesJSONObject =
			_fragmentEntryLinkManager.mergeEditableValuesJSONObject(
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						processedHTML,
						fragmentEntryLink.getConfigurationJSONObject()),
				editableValuesJSONObject);

		fragmentEntryLink = _fragmentEntryLinkService.updateFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId(),
			newEditableValuesJSONObject.toString());

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.
				onUpdateFragmentEntryLinkConfigurationValues(fragmentEntryLink);
		}

		return fragmentEntryLink;
	}

	private JSONObject _processAddFragmentEntryLink(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try (SafeCloseable safeCloseable =
				CheckNoninstanceablePortletThreadLocal.
					setCheckNoninstanceablePortletWithSafeCloseable(true)) {

			FragmentEntryLink fragmentEntryLink = _cloneFragmentEntryLink(
				actionRequest, actionResponse);

			if (fragmentEntryLink == null) {
				return jsonObject;
			}

			long fragmentEntryLinkId = ParamUtil.getLong(
				actionRequest, "fragmentEntryLinkId");

			_fragmentEntryLinkService.updateDeleted(fragmentEntryLinkId, true);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			long segmentsExperienceId = ParamUtil.getLong(
				actionRequest, "segmentsExperienceId");

			LayoutStructureUtil.updateLayoutPageTemplateData(
				themeDisplay.getScopeGroupId(), segmentsExperienceId,
				themeDisplay.getPlid(),
				layoutStructure -> {
					FragmentStyledLayoutStructureItem
						fragmentStyledLayoutStructureItem =
							(FragmentStyledLayoutStructureItem)
								layoutStructure.
									getLayoutStructureItemByFragmentEntryLinkId(
										fragmentEntryLinkId);

					if (fragmentStyledLayoutStructureItem == null) {
						return;
					}

					fragmentStyledLayoutStructureItem.setFragmentEntryLinkId(
						fragmentEntryLink.getFragmentEntryLinkId());
				});

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					fragmentEntryLink);
			}

			LayoutStructure layoutStructure =
				LayoutStructureUtil.getLayoutStructure(
					themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
					fragmentEntryLink.getSegmentsExperienceId());

			return jsonObject.put(
				"fragmentEntryLink",
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					fragmentEntryLink,
					_portal.getHttpServletRequest(actionRequest),
					_portal.getHttpServletResponse(actionResponse),
					layoutStructure)
			).put(
				"layoutData", layoutStructure.toJSONObject()
			);
		}
	}

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}