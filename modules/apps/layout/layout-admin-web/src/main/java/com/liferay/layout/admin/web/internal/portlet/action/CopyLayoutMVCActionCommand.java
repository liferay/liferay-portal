/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.handler.LayoutExceptionRequestHandlerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/copy_layout"
	},
	service = MVCActionCommand.class
)
public class CopyLayoutMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");

		String name = ParamUtil.getString(actionRequest, "name");

		Map<Locale, String> nameMap = HashMapBuilder.put(
			themeDisplay.getLocale(), name
		).put(
			LocaleUtil.getSiteDefault(),
			() -> {
				if (!Objects.equals(
						themeDisplay.getLocale(),
						LocaleUtil.getSiteDefault())) {

					return name;
				}

				return null;
			}
		).build();

		boolean copyPermissions = ParamUtil.getBoolean(
			actionRequest, "copyPermissions");
		long sourcePlid = ParamUtil.getLong(actionRequest, "sourcePlid");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Layout.class.getName(), actionRequest);

		try {
			Layout targetLayout = _layoutService.copyLayout(
				groupId, privateLayout, nameMap, false, false, copyPermissions,
				sourcePlid, serviceContext);

			Layout sourceLayout = _layoutLocalService.fetchLayout(sourcePlid);

			targetLayout = _layoutLocalService.copyLayoutContent(
				sourceLayout, targetLayout);

			Layout draftLayout = targetLayout.fetchDraftLayout();

			if (draftLayout != null) {
				_layoutLocalService.copyLayoutContent(
					targetLayout, draftLayout);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNull(redirect)) {
				redirect = PortletURLBuilder.createRenderURL(
					_portal.getLiferayPortletResponse(actionResponse)
				).setNavigation(
					privateLayout ? "private-pages" : "public-pages"
				).setParameter(
					"privateLayout", privateLayout
				).setParameter(
					"selPlid", sourceLayout.getParentPlid()
				).buildString();
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("redirectURL", redirect));
		}
		catch (Exception exception) {
			SessionErrors.add(actionRequest, "layoutNameInvalid");

			hideDefaultErrorMessage(actionRequest);

			LayoutExceptionRequestHandlerUtil.handleException(
				actionRequest, actionResponse, exception);
		}
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

}