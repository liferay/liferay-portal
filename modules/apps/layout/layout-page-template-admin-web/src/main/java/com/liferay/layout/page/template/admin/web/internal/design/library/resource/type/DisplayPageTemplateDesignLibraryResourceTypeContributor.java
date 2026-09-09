/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(
	property = "service.ranking:Integer=300",
	service = DesignLibraryResourceTypeContributor.class
)
public class DisplayPageTemplateDesignLibraryResourceTypeContributor
	implements DesignLibraryResourceTypeContributor {

	@Override
	public String getColor() {
		return "blue";
	}

	@Override
	public String getDefaultActionId() {
		return "edit";
	}

	@Override
	public String getEntryClassName() {
		return LayoutPageTemplateEntry.class.getName();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return Collections.emptyList();
	}

	@Override
	public String getIcon() {
		return "page-template";
	}

	@Override
	public String getKey() {
		return "display-page-template";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "display-page-template");
	}

	@Override
	public String getType() {
		return String.valueOf(
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return _portletResourcePermission.contains(
			permissionChecker, depotEntry.getGroupId(),
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return hasAddPermission(permissionChecker, depotEntry);
	}

	@Reference(
		target = "(resource.name=" + LayoutPageTemplateConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}