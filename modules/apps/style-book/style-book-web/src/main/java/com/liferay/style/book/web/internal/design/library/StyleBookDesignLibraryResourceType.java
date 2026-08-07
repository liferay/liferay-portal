/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.design.library;

import com.liferay.design.library.resource.type.DesignLibraryResourceType;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(
	property = "service.ranking:Integer=100",
	service = DesignLibraryResourceType.class
)
public class StyleBookDesignLibraryResourceType
	implements DesignLibraryResourceType {

	@Override
	public String getColor() {
		return "--purple";
	}

	@Override
	public String getCreationItemsModule() {
		return "{getStyleBookCreationItems} from style-book-web";
	}

	@Override
	public Map<String, Object> getCreationItemsProps(
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL) {

		ThemeDisplay themeDisplay = _getThemeDisplay(httpServletRequest);

		return HashMapBuilder.<String, Object>put(
			"addStyleBookEntryURL",
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					httpServletRequest, depotGroup,
					StyleBookPortletKeys.STYLE_BOOK, 0, 0,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/style_book/add_style_book_entry"
			).setRedirect(
				backURL
			).setParameter(
				"backURLTitle", depotGroup.getName(themeDisplay.getLocale())
			).buildString()
		).put(
			"frontendTokenDefinitionProviders",
			StyleBookUtil.getFrontendTokenDefinitionProviders(
				themeDisplay.getCompanyId(), themeDisplay.getLocale())
		).put(
			"namespace",
			PortalUtil.getPortletNamespace(StyleBookPortletKeys.STYLE_BOOK)
		).build();
	}

	@Override
	public String getDefaultActionId() {
		return "edit";
	}

	@Override
	public String getEntryClassName() {
		return StyleBookEntry.class.getName();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL) {

		ThemeDisplay themeDisplay = _getThemeDisplay(httpServletRequest);

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						StyleBookPortletKeys.STYLE_BOOK, 0, 0,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/style_book/edit_style_book_entry"
				).setRedirect(
					backURL
				).setParameter(
					"backURLTitle", depotGroup.getName(themeDisplay.getLocale())
				).setParameter(
					"styleBookEntryId", "{embedded.id}"
				).buildString(),
				"pencil", "edit",
				LanguageUtil.get(
					httpServletRequest, "edit-in-style-book-editor"),
				null, "get", "link"),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "style-book");
	}

	@Override
	public String getSymbol() {
		return "book";
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, Group depotGroup) {

		return _portletResourcePermission.contains(
			permissionChecker, depotGroup.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, Group depotGroup) {

		return hasAddPermission(permissionChecker, depotGroup);
	}

	private ThemeDisplay _getThemeDisplay(
		HttpServletRequest httpServletRequest) {

		return (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Reference(
		target = "(resource.name=" + StyleBookConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}