/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.toolbar;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Provides elements to be rendered in the portlet toolbar. To obtain those
 * elements, it delegates the task to the {@link
 * PortletToolbarContributorLocator} instances registered in OSGI.
 *
 * @author Sergio González
 */
public class PortletToolbar {

	public static final PortletToolbar INSTANCE = new PortletToolbar();

	public List<Menu> getPortletTitleMenus(
		String portletId, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		if ((portletRequest == null) || (portletResponse == null) ||
			Validator.isNull(portletId)) {

			return Collections.emptyList();
		}

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(portletRequest));

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		List<Menu> portletTitleMenus = new ArrayList<>();

		for (PortletToolbarContributorLocator portletToolbarContributorLocator :
				_portletToolbarContributorLocators) {

			List<PortletToolbarContributor> portletToolbarContributors =
				portletToolbarContributorLocator.getPortletToolbarContributors(
					portletId, portletRequest);

			if (portletToolbarContributors == null) {
				continue;
			}

			for (PortletToolbarContributor portletToolbarContributor :
					portletToolbarContributors) {

				if (Objects.equals(layoutMode, Constants.EDIT) &&
					!portletToolbarContributor.isShowInEditMode()) {

					continue;
				}

				List<Menu> curPortletTitleMenus =
					portletToolbarContributor.getPortletTitleMenus(
						portletRequest, portletResponse);

				if (ListUtil.isEmpty(curPortletTitleMenus)) {
					continue;
				}

				portletTitleMenus.addAll(curPortletTitleMenus);
			}
		}

		return portletTitleMenus;
	}

	private PortletToolbar() {
	}

	private static final ServiceTrackerList<PortletToolbarContributorLocator>
		_portletToolbarContributorLocators = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(),
			PortletToolbarContributorLocator.class);

}