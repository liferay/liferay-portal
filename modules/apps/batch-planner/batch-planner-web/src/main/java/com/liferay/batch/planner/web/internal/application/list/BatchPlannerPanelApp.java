/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.batch.planner.constants.BatchPlannerPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Beslic
 */
@Component(
	property = {
		"panel.app.order:Integer=200",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_DEVELOPER_INTEGRATION
	},
	service = PanelApp.class
)
public class BatchPlannerPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "database";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return List.of(
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "import-and-export"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setTabs1(
					"batch-planner-plans"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "import-and-export")),
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "templates"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/batch_planner/view_batch_planner_plan_templates"
				).setTabs1(
					"batch-planner-plan-templates"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "templates")));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return BatchPlannerPortletKeys.BATCH_PLANNER;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("COMMERCE-8087")) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + BatchPlannerPortletKeys.BATCH_PLANNER + ")"
	)
	private Portlet _portlet;

}