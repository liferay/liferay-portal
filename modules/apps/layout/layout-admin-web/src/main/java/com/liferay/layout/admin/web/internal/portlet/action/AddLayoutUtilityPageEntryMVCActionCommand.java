/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.handler.LayoutUtilityPageEntryPortalExceptionRequestHandlerUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/add_layout_utility_page_entry"
	},
	service = MVCActionCommand.class
)
public class AddLayoutUtilityPageEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			LayoutUtilityPageEntry layoutUtilityPageEntry =
				_addLayoutUtilityPageEntry(actionRequest);

			JSONObject jsonObject = JSONUtil.put(
				"redirectURL",
				_getRedirectURL(actionRequest, layoutUtilityPageEntry));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);

			MultiSessionMessages.add(
				actionRequest, "layoutUtilityPageEntryAdded");
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			LayoutUtilityPageEntryPortalExceptionRequestHandlerUtil.
				handlePortalException(
					actionRequest, actionResponse, portalException);
		}
	}

	private LayoutUtilityPageEntry _addLayoutUtilityPageEntry(
			ActionRequest actionRequest)
		throws Exception {

		String name = ParamUtil.getString(actionRequest, "name");
		String type = ParamUtil.getString(actionRequest, "type");
		long masterLayoutPlid = ParamUtil.getLong(
			actionRequest, "masterLayoutPlid");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		return _layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
			null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			0, 0, false, name, type, masterLayoutPlid, serviceContext);
	}

	private String _getRedirectURL(
			ActionRequest actionRequest,
			LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws PortalException {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutUtilityPageEntry.getPlid());

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return HttpComponentsUtil.addParameters(
			_portal.getLayoutFullURL(draftLayout, themeDisplay), "p_l_back_url",
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					actionRequest, LayoutAdminPortletKeys.GROUP_PAGES,
					PortletRequest.RENDER_PHASE)
			).setTabs1(
				"utility-pages"
			).buildString(),
			"p_l_back_url_title",
			_language.get(themeDisplay.getLocale(), "pages"), "p_l_mode",
			Constants.EDIT);
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Reference
	private Portal _portal;

}