/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.CheckNoninstanceablePortletThreadLocal;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.LiferayRenderResponse;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.RenderRequestFactory;
import com.liferay.portlet.RenderResponseFactory;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/add_portlet"
	},
	service = MVCActionCommand.class
)
public class AddPortletMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _processAddPortlet(
			actionRequest, actionResponse
		).put(
			"error",
			() -> {
				if (SessionErrors.contains(
						actionRequest, "fragmentEntryContentInvalid")) {

					return true;
				}

				return null;
			}
		);

		SessionMessages.add(actionRequest, "fragmentEntryLinkAdded");

		return jsonObject;
	}

	private JSONObject _addFragmentEntryLinkToLayoutData(
			ActionRequest actionRequest, long fragmentEntryLinkId)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		String parentItemId = ParamUtil.getString(
			actionRequest, "parentItemId");
		int position = ParamUtil.getInteger(actionRequest, "position");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		JSONObject layoutDataJSONObject =
			LayoutStructureUtil.updateLayoutPageTemplateData(
				themeDisplay.getScopeGroupId(), segmentsExperienceId,
				themeDisplay.getPlid(),
				layoutStructure -> {
					LayoutStructureItem layoutStructureItem =
						layoutStructure.addFragmentStyledLayoutStructureItem(
							fragmentEntryLinkId, parentItemId, position);

					jsonObject.put(
						"addedItemId", layoutStructureItem.getItemId());
				});

		return jsonObject.put("layoutData", layoutDataJSONObject);
	}

	private String _getPortletInstanceId(String namespace, String portletId) {
		Portlet portlet = _portletLocalService.getPortletById(portletId);

		if (portlet.isInstanceable()) {
			return namespace;
		}

		return StringPool.BLANK;
	}

	private JSONObject _processAddPortlet(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CheckNoninstanceablePortletThreadLocal.
					setCheckNoninstanceablePortletWithSafeCloseable(true)) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			String portletId = PortletIdCodec.decodePortletName(
				ParamUtil.getString(actionRequest, "portletId"));

			PortletPermissionUtil.check(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
				portletId, ActionKeys.ADD_TO_PAGE);

			long segmentsExperienceId = ParamUtil.getLong(
				actionRequest, "segmentsExperienceId");

			String namespace = StringUtil.randomId();

			String instanceId = _getPortletInstanceId(namespace, portletId);

			JSONObject editableValueJSONObject =
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						StringPool.BLANK, StringPool.BLANK);

			editableValueJSONObject.put(
				"instanceId", instanceId
			).put(
				"portletId", portletId
			);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, serviceContext.getUserId(),
					serviceContext.getScopeGroupId(), 0, 0,
					segmentsExperienceId, themeDisplay.getPlid(),
					StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
					StringPool.BLANK, editableValueJSONObject.toString(),
					namespace, 0, null, FragmentConstants.TYPE_PORTLET,
					serviceContext);

			JSONObject jsonObject = _addFragmentEntryLinkToLayoutData(
				actionRequest, fragmentEntryLink.getFragmentEntryLinkId());

			long portletItemId = ParamUtil.getLong(
				actionRequest, "portletItemId");

			PortletItem portletItem = null;

			if (portletItemId != 0) {
				portletItem = _portletItemLocalService.fetchPortletItem(
					portletItemId);
			}

			if (portletItem != null) {
				PortletPreferences portletPreferences =
					_portletPreferencesLocalService.getPreferences(
						themeDisplay.getCompanyId(), portletItemId,
						PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, 0, portletId);

				_portletPreferencesLocalService.addPortletPreferences(
					themeDisplay.getCompanyId(),
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, themeDisplay.getPlid(),
					PortletIdCodec.encode(portletId, instanceId), null,
					PortletPreferencesFactoryUtil.toXML(portletPreferences));
			}

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(actionRequest);

			Portlet portlet = _portletLocalService.getPortletById(portletId);

			InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
				portlet, httpServletRequest.getServletContext());

			LiferayRenderRequest liferayRenderRequest =
				RenderRequestFactory.create(
					httpServletRequest, portlet, invokerPortlet,
					actionRequest.getPortletContext(),
					actionRequest.getWindowState(),
					actionRequest.getPortletMode(),
					actionRequest.getPreferences(), themeDisplay.getPlid());

			httpServletRequest.setAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST, liferayRenderRequest);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			LiferayRenderResponse liferayRenderResponse =
				RenderResponseFactory.create(
					httpServletResponse, liferayRenderRequest);

			httpServletRequest.setAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE, liferayRenderResponse);

			LayoutStructure layoutStructure =
				LayoutStructureUtil.getLayoutStructure(
					themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
					fragmentEntryLink.getSegmentsExperienceId());

			return jsonObject.put(
				"fragmentEntryLink",
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					fragmentEntryLink, httpServletRequest, httpServletResponse,
					layoutStructure));
		}
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortletItemLocalService _portletItemLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}