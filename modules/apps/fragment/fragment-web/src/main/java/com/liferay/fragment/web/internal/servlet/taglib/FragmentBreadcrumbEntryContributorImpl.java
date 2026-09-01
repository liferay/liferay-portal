/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.servlet.taglib;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
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

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "service.ranking:Integer=100",
	service = BreadcrumbEntryContributor.class
)
public class FragmentBreadcrumbEntryContributorImpl
	implements BreadcrumbEntryContributor {

	@Override
	public List<BreadcrumbEntry> getBreadcrumbEntries(
		List<BreadcrumbEntry> originalBreadcrumbEntries,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		if (!Objects.equals(
				FragmentPortletKeys.FRAGMENT,
				portletDisplay.getPortletName()) ||
			!DesignLibraryUtil.isDesignLibraryScope(
				themeDisplay.getScopeGroup())) {

			return originalBreadcrumbEntries;
		}

		FragmentEntry fragmentEntry = _getFragmentEntry(httpServletRequest);

		FragmentCollection fragmentCollection = _getFragmentCollection(
			fragmentEntry, httpServletRequest);

		if (fragmentCollection == null) {
			return originalBreadcrumbEntries;
		}

		List<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

		breadcrumbEntries.add(
			_createFragmentCollectionBreadcrumbEntry(
				fragmentCollection, httpServletRequest));

		if (fragmentEntry != null) {
			breadcrumbEntries.add(
				_createFragmentEntryBreadcrumbEntry(fragmentEntry));
		}

		breadcrumbEntries.addAll(originalBreadcrumbEntries);

		return breadcrumbEntries;
	}

	private BreadcrumbEntry _createFragmentCollectionBreadcrumbEntry(
		FragmentCollection fragmentCollection,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(fragmentCollection.getName());
		breadcrumbEntry.setURL(
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					httpServletRequest, themeDisplay.getScopeGroup(),
					FragmentPortletKeys.FRAGMENT, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"fragmentCollectionId",
				fragmentCollection.getFragmentCollectionId()
			).buildString());

		return breadcrumbEntry;
	}

	private BreadcrumbEntry _createFragmentEntryBreadcrumbEntry(
		FragmentEntry fragmentEntry) {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(fragmentEntry.getName());

		return breadcrumbEntry;
	}

	private FragmentCollection _getFragmentCollection(
		FragmentEntry fragmentEntry, HttpServletRequest httpServletRequest) {

		if (fragmentEntry == null) {
			return _getFragmentCollection(httpServletRequest);
		}

		return _fragmentCollectionLocalService.fetchFragmentCollection(
			fragmentEntry.getFragmentCollectionId());
	}

	private FragmentCollection _getFragmentCollection(
		HttpServletRequest httpServletRequest) {

		String portletNamespace = _portal.getPortletNamespace(
			FragmentPortletKeys.FRAGMENT);

		long fragmentCollectionId = ParamUtil.getLong(
			httpServletRequest, portletNamespace + "fragmentCollectionId",
			ParamUtil.getLong(httpServletRequest, "fragmentCollectionId"));

		if (fragmentCollectionId > 0) {
			return _fragmentCollectionLocalService.fetchFragmentCollection(
				fragmentCollectionId);
		}

		String externalReferenceCode = ParamUtil.getString(
			httpServletRequest,
			portletNamespace + "fragmentCollectionExternalReferenceCode",
			ParamUtil.getString(
				httpServletRequest, "fragmentCollectionExternalReferenceCode"));

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _fragmentCollectionLocalService.
			fetchFragmentCollectionByExternalReferenceCode(
				externalReferenceCode, themeDisplay.getScopeGroupId());
	}

	private FragmentEntry _getFragmentEntry(
		HttpServletRequest httpServletRequest) {

		long fragmentEntryId = ParamUtil.getLong(
			httpServletRequest,
			_portal.getPortletNamespace(FragmentPortletKeys.FRAGMENT) +
				"fragmentEntryId",
			ParamUtil.getLong(httpServletRequest, "fragmentEntryId"));

		if (fragmentEntryId == 0) {
			return null;
		}

		return _fragmentEntryLocalService.fetchFragmentEntry(fragmentEntryId);
	}

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private Portal _portal;

}