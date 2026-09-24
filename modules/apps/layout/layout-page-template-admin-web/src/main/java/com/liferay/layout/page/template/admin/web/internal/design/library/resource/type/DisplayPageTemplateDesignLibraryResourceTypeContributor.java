/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.admin.web.internal.util.MappingTypesUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

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
	public List<DesignLibraryResourceCreationItem> getCreationItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		Group depotGroup = depotEntry.getGroup();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return Collections.singletonList(
			new DesignLibraryResourceCreationItem(
				"add-display-page-template",
				LanguageUtil.get(
					httpServletRequest, "new-display-page-template"),
				"{AddDisplayPageTemplateDesignLibraryModalContent} from " +
					"layout-page-template-admin-web",
				HashMapBuilder.<String, Object>put(
					"addDisplayPageURL",
					PortletURLBuilder.create(
						PortalUtil.getControlPanelPortletURL(
							httpServletRequest, depotGroup,
							LayoutPageTemplateAdminPortletKeys.
								LAYOUT_PAGE_TEMPLATES,
							0, 0, PortletRequest.ACTION_PHASE)
					).setActionName(
						"/layout_page_template_admin/add_display_page"
					).setRedirect(
						backURL
					).buildString()
				).put(
					"mappingTypes",
					MappingTypesUtil.getMappingTypesJSONArray(
						depotGroup.getGroupId(), _infoItemServiceRegistry,
						themeDisplay.getLocale(),
						themeDisplay.getPermissionChecker())
				).put(
					"namespace",
					PortalUtil.getPortletNamespace(
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES)
				).build()));
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

		Group depotGroup = depotEntry.getGroup();

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES,
						0, 0, PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/layout_page_template_admin/edit_display_page"
				).setRedirect(
					backURL
				).setParameter(
					"displayPageTemplateExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				null, "get", "link"),
			new FDSActionDropdownItem(
				"{actions.copy.href}", "copy", "copy",
				LanguageUtil.get(httpServletRequest, "duplicate"), "post",
				"copy", "async"),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES,
						0, 0, PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/layout_page_template_admin/view_display_page_permissions"
				).setParameter(
					"displayPageTemplateExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"password-policies", "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), null,
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	@Override
	public String getIcon() {
		return "web-content";
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

		return _portletResourcePermission.contains(
			permissionChecker, depotEntry.getGroupId(), ActionKeys.VIEW);
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference(
		target = "(resource.name=" + LayoutPageTemplateConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}