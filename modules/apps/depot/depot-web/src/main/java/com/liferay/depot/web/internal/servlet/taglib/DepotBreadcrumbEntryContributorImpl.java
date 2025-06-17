/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.servlet.taglib;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.web.internal.constants.DepotPortletKeys;
import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntryContributor;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia Garcia
 */
@Component(service = BreadcrumbEntryContributor.class)
public class DepotBreadcrumbEntryContributorImpl
	implements BreadcrumbEntryContributor {

	@Override
	public List<BreadcrumbEntry> getBreadcrumbEntries(
		List<BreadcrumbEntry> originalBreadcrumbEntries,
		HttpServletRequest httpServletRequest) {

		long depotEntryId = ParamUtil.getLong(
			httpServletRequest, "depotEntryId");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if ((depotEntryId == 0) && !scopeGroup.isDepot()) {
			return originalBreadcrumbEntries;
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (Objects.equals(
				ItemSelectorPortletKeys.ITEM_SELECTOR,
				portletDisplay.getPortletName()) ||
			themeDisplay.isStatePopUp()) {

			return originalBreadcrumbEntries;
		}

		List<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

		try {
			breadcrumbEntries.add(
				_getAssetLibrariesBreadcrumbEntry(
					themeDisplay.getControlPanelGroup(), httpServletRequest));

			breadcrumbEntries.add(
				_getAssetLibraryBreadcrumbEntry(
					_getDepotEntry(scopeGroup.getGroupId(), depotEntryId),
					httpServletRequest));

			if (originalBreadcrumbEntries.isEmpty() &&
				!Objects.equals(
					DepotPortletKeys.DEPOT_ADMIN,
					portletDisplay.getPortletName())) {

				breadcrumbEntries.add(
					_getPortletBreadcrumbEntry(
						httpServletRequest, scopeGroup,
						_getTitle(
							httpServletRequest, themeDisplay.getLanguageId(),
							portletDisplay)));
			}
			else if (!originalBreadcrumbEntries.isEmpty()) {
				BreadcrumbEntry breadcrumbEntry = originalBreadcrumbEntries.get(
					0);

				String title = _language.get(httpServletRequest, "home");

				if (Objects.equals(breadcrumbEntry.getTitle(), title)) {
					breadcrumbEntry.setTitle(
						portletDisplay.getPortletDisplayName());
				}
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		breadcrumbEntries.addAll(originalBreadcrumbEntries);

		BreadcrumbEntry breadcrumbEntry = breadcrumbEntries.get(
			breadcrumbEntries.size() - 1);

		breadcrumbEntry.setBrowsable(false);
		breadcrumbEntry.setURL(null);

		return breadcrumbEntries;
	}

	private BreadcrumbEntry _getAssetLibrariesBreadcrumbEntry(
		Group group, HttpServletRequest httpServletRequest) {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(
			_language.get(httpServletRequest, "category.asset-libraries"));

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			httpServletRequest, group, DepotPortletKeys.DEPOT_ADMIN, 0, 0,
			PortletRequest.RENDER_PHASE);

		breadcrumbEntry.setURL(portletURL.toString());

		return breadcrumbEntry;
	}

	private BreadcrumbEntry _getAssetLibraryBreadcrumbEntry(
			DepotEntry depotEntry, HttpServletRequest httpServletRequest)
		throws PortalException {

		Group group = depotEntry.getGroup();

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(
			group.getDescriptiveName(_portal.getLocale(httpServletRequest)));
		breadcrumbEntry.setURL(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest, group, DepotPortletKeys.DEPOT_ADMIN, 0,
					0, PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/depot/view_depot_dashboard"
			).setParameter(
				"depotEntryId", depotEntry.getDepotEntryId()
			).buildString());

		return breadcrumbEntry;
	}

	private DepotEntry _getDepotEntry(long groupId, long depotEntryId)
		throws PortalException {

		if (depotEntryId == 0) {
			return _depotEntryService.getGroupDepotEntry(groupId);
		}

		return _depotEntryService.getDepotEntry(depotEntryId);
	}

	private BreadcrumbEntry _getPortletBreadcrumbEntry(
		HttpServletRequest httpServletRequest, Group scopeGroup, String title) {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(_language.get(httpServletRequest, title));
		breadcrumbEntry.setURL(
			_groupURLProvider.getGroupURL(
				scopeGroup,
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST)));

		return breadcrumbEntry;
	}

	private String _getTitle(
		HttpServletRequest httpServletRequest, String languageId,
		PortletDisplay portletDisplay) {

		String title = portletDisplay.getPortletDisplayName();

		if (Validator.isNotNull(title)) {
			return title;
		}

		if (Validator.isNotNull(portletDisplay.getId())) {
			title = _portal.getPortletTitle(portletDisplay.getId(), languageId);

			if (Validator.isNotNull(title)) {
				return title;
			}
		}

		return _language.get(httpServletRequest, "home");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotBreadcrumbEntryContributorImpl.class);

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}