/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.design.library;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.exception.PortalException;
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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 * @author Thiago Buarque
 */
@Component(
	property = "service.ranking:Integer=100",
	service = DesignLibraryResourceTypeContributor.class
)
public class StyleBookDesignLibraryResourceTypeContributor
	implements DesignLibraryResourceTypeContributor {

	@Override
	public String getColor() {
		return "purple";
	}

	@Override
	public List<DesignLibraryResourceCreationItem> getCreationItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return Collections.singletonList(
			new DesignLibraryResourceCreationItem(
				"add-style-book",
				LanguageUtil.get(httpServletRequest, "new-style-book"),
				"{AddStyleBookEntryDesignLibraryModalContent} from " +
					"style-book-web",
				HashMapBuilder.<String, Object>put(
					"addStyleBookEntryURL",
					_getAddStyleBookEntryURL(
						httpServletRequest, depotEntry, backURL)
				).put(
					"frontendTokenDefinitionProviders",
					_getFrontendTokenDefinitionProviders(httpServletRequest)
				).put(
					"namespace",
					PortalUtil.getPortletNamespace(
						StyleBookPortletKeys.STYLE_BOOK)
				).build()));
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
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				_getEditStyleBookEntryURL(
					httpServletRequest, depotEntry, backURL),
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
	public String getIcon() {
		return "book";
	}

	@Override
	public String getKey() {
		return "style-book";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "style-book");
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return _portletResourcePermission.contains(
			permissionChecker, depotEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return hasAddPermission(permissionChecker, depotEntry);
	}

	private String _getAddStyleBookEntryURL(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotEntry.getGroup(),
				StyleBookPortletKeys.STYLE_BOOK, 0, 0,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/style_book/add_style_book_entry"
		).setRedirect(
			backURL
		).setParameter(
			"backURLTitle", _getDepotGroupName(httpServletRequest, depotEntry)
		).buildString();
	}

	private String _getDepotGroupName(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry)
		throws PortalException {

		Group depotGroup = depotEntry.getGroup();
		ThemeDisplay themeDisplay = _getThemeDisplay(httpServletRequest);

		return depotGroup.getName(themeDisplay.getLocale());
	}

	private String _getEditStyleBookEntryURL(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotEntry.getGroup(),
				StyleBookPortletKeys.STYLE_BOOK, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/style_book/edit_style_book_entry"
		).setRedirect(
			backURL
		).setParameter(
			"backURLTitle", _getDepotGroupName(httpServletRequest, depotEntry)
		).setParameter(
			"styleBookEntryId", "{embedded.id}"
		).buildString();
	}

	private List<Map<String, Object>> _getFrontendTokenDefinitionProviders(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay = _getThemeDisplay(httpServletRequest);

		return StyleBookUtil.getFrontendTokenDefinitionProviders(
			themeDisplay.getCompanyId(), themeDisplay.getLocale());
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