/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.handler.LayoutPageTemplateEntryExceptionRequestHandlerUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
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
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

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
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/add_master_layout"
	},
	service = MVCActionCommand.class
)
public class AddMasterLayoutMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutPageTemplateCollectionId = ParamUtil.getLong(
			actionRequest, "layoutPageTemplateCollectionId");

		String name = ParamUtil.getString(actionRequest, "name");

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
					null, serviceContext.getScopeGroupId(),
					layoutPageTemplateCollectionId, null, name,
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
					WorkflowConstants.STATUS_DRAFT, serviceContext);

			JSONObject jsonObject = JSONUtil.put(
				"redirectURL",
				getRedirectURL(actionRequest, layoutPageTemplateEntry));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			SessionErrors.add(
				actionRequest, "layoutPageTemplateEntryNameInvalid");

			hideDefaultErrorMessage(actionRequest);

			LayoutPageTemplateEntryExceptionRequestHandlerUtil.
				handlePortalException(
					actionRequest, actionResponse, portalException);
		}
	}

	protected String getRedirectURL(
			ActionRequest actionRequest,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws PortalException {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return HttpComponentsUtil.addParameters(
			_portal.getLayoutFullURL(draftLayout, themeDisplay), "p_l_back_url",
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					actionRequest,
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
					PortletRequest.RENDER_PHASE)
			).setTabs1(
				"master-layouts"
			).buildString(),
			"p_l_back_url_title",
			_language.get(themeDisplay.getLocale(), "page-templates"),
			"p_l_mode", Constants.EDIT);
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

}