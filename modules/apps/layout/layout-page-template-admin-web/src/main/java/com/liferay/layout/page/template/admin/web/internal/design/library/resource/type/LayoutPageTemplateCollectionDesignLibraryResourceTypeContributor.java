/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.design.library.resource.type;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Georgel Pop
 */
@Component(
	property = "service.ranking:Integer=50",
	service = DesignLibraryResourceTypeContributor.class
)
public class LayoutPageTemplateCollectionDesignLibraryResourceTypeContributor
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

		Map<String, Object> baseModuleProps = _getBaseModuleProps(
			httpServletRequest, depotEntry.getGroup(), backURL);

		return ListUtil.fromArray(
			_getDesignLibraryResourceCreationItem(
				httpServletRequest, "add-content-page-template",
				"new-content-page-template", "page-template", baseModuleProps),
			_getDesignLibraryResourceCreationItem(
				httpServletRequest, "add-page-template-set",
				"new-page-template-set", "set", baseModuleProps));
	}

	@Override
	public String getDefaultActionId() {
		return "view";
	}

	@Override
	public String getEntryClassName() {
		return LayoutPageTemplateCollection.class.getName();
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
				).setBackURL(
					backURL
				).setTabs1(
					"page-templates"
				).setParameter(
					"layoutPageTemplateCollectionExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"view", "view", LanguageUtil.get(httpServletRequest, "view"),
				null, null, "link"),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						LayoutPageTemplateAdminPortletKeys.
							LAYOUT_PAGE_TEMPLATES,
						0, 0, PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/layout_page_template_admin" +
						"/edit_layout_page_template_collection"
				).setRedirect(
					backURL
				).setParameter(
					"layoutPageTemplateCollectionExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				null, null, "link"),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	@Override
	public String getIcon() {
		return "page-template";
	}

	@Override
	public String getKey() {
		return "page-template-set";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "page-template-set");
	}

	@Override
	public String getType() {
		return String.valueOf(LayoutPageTemplateCollectionTypeConstants.BASIC);
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return _portletResourcePermission.contains(
			permissionChecker, depotEntry.getGroupId(),
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return hasAddPermission(permissionChecker, depotEntry);
	}

	private Map<String, Object> _getBaseModuleProps(
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL) {

		LiferayPortletURL addLayoutPageTemplateCollectionPortletURL =
			(LiferayPortletURL)PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup,
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES, 0, 0,
				PortletRequest.RESOURCE_PHASE);

		addLayoutPageTemplateCollectionPortletURL.setResourceID(
			"/layout_page_template_admin/add_layout_page_template_collection");

		return HashMapBuilder.<String, Object>put(
			"addLayoutPageTemplateEntryURL",
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					httpServletRequest, depotGroup,
					LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES, 0,
					0, PortletRequest.ACTION_PHASE)
			).setActionName(
				"/layout_page_template_admin/add_layout_page_template_entry"
			).setRedirect(
				backURL
			).buildString()
		).put(
			"addPageTemplateSetURL",
			addLayoutPageTemplateCollectionPortletURL.toString()
		).put(
			"namespace",
			PortalUtil.getPortletNamespace(
				LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES)
		).put(
			"pageTemplateSets",
			JSONUtil.toJSONArray(
				_layoutPageTemplateCollectionLocalService.
					getLayoutPageTemplateCollections(
						depotGroup.getGroupId(),
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				layoutPageTemplateCollection -> JSONUtil.put(
					"id",
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId()
				).put(
					"name", layoutPageTemplateCollection.getName()
				),
				_log)
		).build();
	}

	private DesignLibraryResourceCreationItem _getDesignLibraryResourceCreationItem(
		HttpServletRequest httpServletRequest, String id, String languageKey,
		String mode, Map<String, Object> baseModuleProps) {

		return new DesignLibraryResourceCreationItem(
			id, LanguageUtil.get(httpServletRequest, languageKey),
			"{AddLayoutPageTemplateEntryDesignLibraryModalContent} from " +
				"layout-page-template-admin-web",
			HashMapBuilder.<String, Object>putAll(
				baseModuleProps
			).put(
				"mode", mode
			).build());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateCollectionDesignLibraryResourceTypeContributor.class);

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Reference(
		target = "(resource.name=" + LayoutPageTemplateConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}