/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.upgrade.registry;

import com.liferay.portal.search.similar.results.web.internal.constants.SimilarResultsPortletKeys;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portlet.display.template.upgrade.BaseUpgradePortletPreferences;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

/**
 * @author Joshua Cords
 */
@Component(service = UpgradeStepRegistrator.class)
public class SearchSimiliarResultsWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "0.0.2",
			new BaseUpgradePortletPreferences() {

				@Override
				protected String[] getPortletIds() {
					return new String[] {
						SimilarResultsPortletKeys.SIMILAR_RESULTS +
							"_INSTANCE_%"
					};
				}

				@Override
				protected void upgradePreferences(
						long companyId, long ownerId, int ownerType, long plid,
						String portletId, PortletPreferences portletPreferences)
					throws Exception {
				}

			});
	}

}