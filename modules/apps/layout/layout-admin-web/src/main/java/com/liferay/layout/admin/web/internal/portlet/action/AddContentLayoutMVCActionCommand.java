/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.handler.LayoutExceptionRequestHandlerUtil;
import com.liferay.layout.admin.web.internal.security.permission.resource.LayoutPageTemplateEntryPermission;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/add_content_layout"
	},
	service = MVCActionCommand.class
)
public class AddContentLayoutMVCActionCommand
	extends BaseAddLayoutMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		long layoutPageTemplateEntryId = ParamUtil.getLong(
			actionRequest, "layoutPageTemplateEntryId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");
		long parentLayoutId = ParamUtil.getLong(
			actionRequest, "parentLayoutId");
		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(),
			ParamUtil.getString(actionRequest, "name")
		).build();
		UnicodeProperties typeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "TypeSettingsProperties--");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Layout.class.getName(), actionRequest);

		Layout layout = null;

		try {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntry(layoutPageTemplateEntryId);

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getLayoutPrototypeId() > 0)) {

				LayoutPrototype layoutPrototype =
					_layoutPrototypeService.getLayoutPrototype(
						layoutPageTemplateEntry.getLayoutPrototypeId());

				serviceContext.setAttribute(
					"layoutPrototypeUuid", layoutPrototype.getUuid());

				layout = _layoutService.addLayout(
					null, groupId, privateLayout, parentLayoutId, nameMap,
					new HashMap<>(), new HashMap<>(), new HashMap<>(),
					new HashMap<>(), LayoutConstants.TYPE_PORTLET,
					typeSettingsUnicodeProperties.toString(), false,
					new HashMap<>(), serviceContext);

				// Force propagation from page template to page. See LPS-48430.

				_sites.mergeLayoutPrototypeLayout(layout.getGroup(), layout);
			}
			else {
				if (layoutPageTemplateEntryId > 0) {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)actionRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					LayoutPageTemplateEntryPermission.check(
						themeDisplay.getPermissionChecker(),
						layoutPageTemplateEntryId, ActionKeys.VIEW);
				}

				long masterLayoutPlid = 0;

				if (layoutPageTemplateEntry != null) {
					Layout layoutPageTemplateEntryLayout =
						_layoutLocalService.fetchLayout(
							layoutPageTemplateEntry.getPlid());

					if (layoutPageTemplateEntryLayout != null) {
						masterLayoutPlid =
							layoutPageTemplateEntryLayout.getMasterLayoutPlid();
					}
				}

				layout = _layoutService.addLayout(
					null, groupId, privateLayout, parentLayoutId,
					portal.getClassNameId(LayoutPageTemplateEntry.class),
					layoutPageTemplateEntryId, nameMap, new HashMap<>(),
					new HashMap<>(), new HashMap<>(), new HashMap<>(),
					LayoutConstants.TYPE_CONTENT,
					typeSettingsUnicodeProperties.toString(), false, false,
					new HashMap<>(), masterLayoutPlid, serviceContext);
			}

			String redirectURL = getRedirectURL(
				actionRequest, actionResponse, layout);

			if (layout.isTypeContent()) {
				redirectURL = getContentRedirectURL(actionRequest, layout);
			}

			MultiSessionMessages.add(actionRequest, "layoutAdded", layout);

			ActionUtil.addFriendlyURLWarningSessionMessages(
				_portal.getHttpServletRequest(actionRequest), layout,
				_layoutSetPrototypeHelper);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("redirectURL", redirectURL));
		}
		catch (CTTransactionException ctTransactionException) {
			hideDefaultErrorMessage(actionRequest);

			LiferayPortletResponse liferayPortletResponse =
				portal.getLiferayPortletResponse(actionResponse);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL", liferayPortletResponse.createRenderURL()));

			throw ctTransactionException;
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
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPrototypeService _layoutPrototypeService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	@Reference
	private Portal _portal;

	@Reference
	private Sites _sites;

}