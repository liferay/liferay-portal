/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.display.context.LayoutsAdminDisplayContext;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.layout.admin.web.internal.servlet.taglib.util.LayoutActionDropdownItemsProvider;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.layout.util.template.LayoutConverterRegistry;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.translation.security.permission.TranslationPermission;
import com.liferay.translation.url.provider.TranslationURLProvider;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/get_layout_actions"
	},
	service = MVCResourceCommand.class
)
public class GetLayoutActionsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long plid = ParamUtil.getLong(resourceRequest, "plid");

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("actions", Collections.emptyList()));

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			_layoutConverterRegistry, themeDisplay, _translationPermission);

		LayoutsAdminDisplayContext layoutsAdminDisplayContext =
			new LayoutsAdminDisplayContext(
				_itemSelector, layoutActionsHelper, _layoutLocalService,
				_layoutSetPrototypeHelper,
				_portal.getLiferayPortletRequest(resourceRequest),
				_portal.getLiferayPortletResponse(resourceResponse));

		LayoutActionDropdownItemsProvider layoutActionDropdownItemsProvider =
			new LayoutActionDropdownItemsProvider(
				_portal.getHttpServletRequest(resourceRequest),
				layoutActionsHelper, layoutsAdminDisplayContext,
				_portal.getLiferayPortletResponse(resourceResponse),
				_translationURLProvider);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"actions",
				layoutActionDropdownItemsProvider.getActionDropdownItems(
					layout, false)));
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private LayoutConverterRegistry _layoutConverterRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	@Reference
	private Portal _portal;

	@Reference
	private TranslationPermission _translationPermission;

	@Reference
	private TranslationURLProvider _translationURLProvider;

}