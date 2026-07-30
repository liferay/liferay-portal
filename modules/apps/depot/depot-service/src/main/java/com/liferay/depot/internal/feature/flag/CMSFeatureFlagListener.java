/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.feature.flag;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalService;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "feature.flag.key=LPD-17564", service = FeatureFlagListener.class
)
public class CMSFeatureFlagListener implements FeatureFlagListener {

	@Override
	public void onValue(
		long companyId, String featureFlagKey, boolean enabled) {

		if (!enabled || !Objects.equals(featureFlagKey, "LPD-17564")) {
			return;
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			for (String name : DepotRolesConstants.DEPOT_ROLE_NAMES) {
				Role role = _roleLocalService.fetchRole(companyId, name);

				if (role == null) {
					continue;
				}

				Map<Locale, String> titleMap = DepotRoleUtil.getTitleMap(
					_language, name);

				if (Objects.equals(titleMap, role.getTitleMap())) {
					continue;
				}

				role.setTitleMap(titleMap);

				_roleLocalService.updateRole(role);
			}
		}
	}

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

}