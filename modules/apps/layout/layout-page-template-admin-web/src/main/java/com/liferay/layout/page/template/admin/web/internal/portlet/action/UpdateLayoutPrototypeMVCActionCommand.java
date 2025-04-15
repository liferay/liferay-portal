/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.handler.LayoutPageTemplateEntryExceptionRequestHandlerUtil;
import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/update_layout_prototype"
	},
	service = MVCActionCommand.class
)
public class UpdateLayoutPrototypeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long layoutPrototypeId = ParamUtil.getLong(
				actionRequest, "layoutPrototypeId");

			Map<Locale, String> nameMap = HashMapBuilder.put(
				actionRequest.getLocale(),
				ParamUtil.getString(actionRequest, "name")
			).build();

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				LayoutPrototype.class.getName(), actionRequest);

			_layoutPrototypeService.updateLayoutPrototype(
				layoutPrototypeId, nameMap, new HashMap<>(), true,
				serviceContext);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					ParamUtil.getString(actionRequest, "redirect")));
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable, throwable);
			}

			if (throwable instanceof LayoutPageTemplateEntryNameException) {
				LayoutPageTemplateEntryNameException
					layoutPageTemplateEntryNameException =
						(LayoutPageTemplateEntryNameException)throwable;

				LayoutPageTemplateEntryExceptionRequestHandlerUtil.
					handlePortalException(
						actionRequest, actionResponse,
						layoutPageTemplateEntryNameException);
			}
			else {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"error",
						() -> {
							ThemeDisplay themeDisplay =
								(ThemeDisplay)actionRequest.getAttribute(
									WebKeys.THEME_DISPLAY);

							return _language.get(
								themeDisplay.getRequest(),
								"an-unexpected-error-occurred");
						}));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateLayoutPrototypeMVCActionCommand.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutPrototypeService _layoutPrototypeService;

}