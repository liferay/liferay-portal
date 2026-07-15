/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.servlet.taglib;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.design.library.web.internal.constants.DesignLibraryConstants;
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(service = BreadcrumbEntryContributor.class)
public class DesignLibraryDepotEntryBreadcrumbEntryContributorImpl
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

		if (((depotEntryId == 0) && !scopeGroup.isDepot()) ||
			themeDisplay.isStatePopUp()) {

			return originalBreadcrumbEntries;
		}

		try {
			DepotEntry depotEntry = _getDepotEntry(
				depotEntryId, scopeGroup.getGroupId());

			if (depotEntry.getType() != DepotConstants.TYPE_DESIGN_LIBRARY) {
				return originalBreadcrumbEntries;
			}

			List<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

			breadcrumbEntries.add(
				_getDesignLibrariesBreadcrumbEntry(
					httpServletRequest, themeDisplay));

			breadcrumbEntries.add(
				_getDesignLibraryBreadcrumbEntry(
					depotEntry, httpServletRequest, themeDisplay));

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			if (originalBreadcrumbEntries.isEmpty()) {
				BreadcrumbEntry portletBreadcrumbEntry = new BreadcrumbEntry();

				portletBreadcrumbEntry.setTitle(
					_getTitle(
						httpServletRequest, themeDisplay.getLanguageId(),
						portletDisplay));

				breadcrumbEntries.add(portletBreadcrumbEntry);
			}
			else {
				BreadcrumbEntry firstBreadcrumbEntry =
					originalBreadcrumbEntries.get(0);

				if (Objects.equals(
						firstBreadcrumbEntry.getTitle(),
						_language.get(httpServletRequest, "home"))) {

					firstBreadcrumbEntry.setTitle(
						portletDisplay.getPortletDisplayName());
				}
			}

			breadcrumbEntries.addAll(originalBreadcrumbEntries);

			BreadcrumbEntry lastBreadcrumbEntry = breadcrumbEntries.get(
				breadcrumbEntries.size() - 1);

			lastBreadcrumbEntry.setBrowsable(false);
			lastBreadcrumbEntry.setURL(null);

			return breadcrumbEntries;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return originalBreadcrumbEntries;
		}
	}

	private DepotEntry _getDepotEntry(long depotEntryId, long groupId)
		throws PortalException {

		if (depotEntryId == 0) {
			return _depotEntryService.getGroupDepotEntry(groupId);
		}

		return _depotEntryService.getDepotEntry(depotEntryId);
	}

	private BreadcrumbEntry _getDesignLibrariesBreadcrumbEntry(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(
			_language.get(httpServletRequest, "design-libraries"));

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			httpServletRequest, themeDisplay.getControlPanelGroup(),
			DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN, 0, 0,
			PortletRequest.RENDER_PHASE);

		breadcrumbEntry.setURL(portletURL.toString());

		return breadcrumbEntry;
	}

	private BreadcrumbEntry _getDesignLibraryBreadcrumbEntry(
			DepotEntry depotEntry, HttpServletRequest httpServletRequest,
			ThemeDisplay themeDisplay)
		throws PortalException {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		Group group = depotEntry.getGroup();

		breadcrumbEntry.setTitle(
			group.getDescriptiveName(_portal.getLocale(httpServletRequest)));

		breadcrumbEntry.setURL(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest, themeDisplay.getControlPanelGroup(),
					DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/design_library/design_library_resources"
			).setParameter(
				DesignLibraryConstants.DESIGN_LIBRARY_ENTRY_ID_KEY,
				depotEntry.getDepotEntryId()
			).buildString());

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
		DesignLibraryDepotEntryBreadcrumbEntryContributorImpl.class);

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}