/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.staging.StagingGroupHelper;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kapler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"panel.app.order:Integer=400",
		"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_DESIGN
	},
	service = PanelApp.class
)
public class LayoutPageTemplatesPanelApp extends BasePanelApp {

	@Override
	public String getLabel(Locale locale) {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return super.getLabel(locale);
		}

		Group scopeGroup = _groupLocalService.fetchGroup(
			serviceContext.getScopeGroupId());

		if ((scopeGroup != null) && scopeGroup.isCompany()) {
			return _language.get(locale, "widget-page-templates");
		}

		return super.getLabel(locale);
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (_stagingGroupHelper.isLocalLiveGroup(group) ||
			_stagingGroupHelper.isRemoteLiveGroup(group)) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES + ")"
	)
	private Portlet _portlet;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}