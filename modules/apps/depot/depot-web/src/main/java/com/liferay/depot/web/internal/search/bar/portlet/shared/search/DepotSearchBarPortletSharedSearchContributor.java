/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.search.bar.portlet.shared.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.search.web.constants.SearchBarPortletKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SearchBarPortletKeys.SEARCH_BAR,
		"service.ranking:Integer=100"
	},
	service = PortletSharedSearchContributor.class
)
public class DepotSearchBarPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		PortletSharedSearchContributor
			defaultSearchBarPortletSharedSearchContributor =
				_defaultSearchBarPortletSharedSearchContributorSnapshot.get();

		if (defaultSearchBarPortletSharedSearchContributor == null) {
			return;
		}

		defaultSearchBarPortletSharedSearchContributor.contribute(
			portletSharedSearchSettings);
	}

	private static final Snapshot<PortletSharedSearchContributor>
		_defaultSearchBarPortletSharedSearchContributorSnapshot =
			new Snapshot<>(
				DepotSearchBarPortletSharedSearchContributor.class,
				PortletSharedSearchContributor.class,
				StringBundler.concat(
					"(&(jakarta.portlet.name=", SearchBarPortletKeys.SEARCH_BAR,
					")(!(component.name=",
					DepotSearchBarPortletSharedSearchContributor.class.
						getName(),
					")))"),
				true);

}