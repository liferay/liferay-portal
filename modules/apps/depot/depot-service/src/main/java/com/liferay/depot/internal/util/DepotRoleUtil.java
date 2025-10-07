/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.internal.instance.lifecycle.DepotRolesPortalInstanceLifecycleListener;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Shuyang Zhou
 */
public class DepotRoleUtil {

	public static final String[] DEPOT_ROLE_NAMES = {
		DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
		DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
		DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
		DepotRolesConstants.ASSET_LIBRARY_MEMBER,
		DepotRolesConstants.ASSET_LIBRARY_OWNER
	};

	public static Map<Locale, String> getDescriptionMap(
		long companyId, Language language, String name) {

		Map<Locale, String> descriptionMap = new HashMap<>();

		for (Locale locale : language.getAvailableLocales()) {
			String description = _getDescription(companyId, locale, name);

			if (description != null) {
				descriptionMap.put(locale, description);
			}
		}

		return descriptionMap;
	}

	public static Map<Locale, String> getTitleMap(
		long companyId, Language language, String name) {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			return null;
		}

		Map<Locale, String> titleMap = new HashMap<>();

		for (Locale locale : language.getAvailableLocales()) {
			String title = _getTitle(locale, name);

			if (title != null) {
				titleMap.put(locale, title);
			}
		}

		return titleMap;
	}

	private static String _getDescription(
		long companyId, Locale locale, String name) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, DepotRolesPortalInstanceLifecycleListener.class);

		if (Objects.equals(
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR, name)) {

			if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
				return ResourceBundleUtil.getString(
					resourceBundle,
					"space-administrators-are-super-users-of-their-space-but-" +
						"cannot-make-other-users-into-space-administrators");
			}

			return ResourceBundleUtil.getString(
				resourceBundle,
				"asset-library-administrators-are-super-users-of-their-asset-" +
					"library-but-cannot-make-other-users-into-asset-library-" +
						"administrators");
		}
		else if (Objects.equals(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER, name)) {

			if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
				return ResourceBundleUtil.getString(
					resourceBundle,
					"all-users-who-belong-to-a-space-have-this-role-within-" +
						"that-space");
			}

			return ResourceBundleUtil.getString(
				resourceBundle,
				"all-users-who-belong-to-an-asset-library-have-this-role-" +
					"within-that-asset-library");
		}
		else if (Objects.equals(
					DepotRolesConstants.ASSET_LIBRARY_OWNER, name)) {

			if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
				return ResourceBundleUtil.getString(
					resourceBundle,
					"space-owners-are-super-users-of-their-space-and-can-" +
						"assign-space-roles-to-users");
			}

			return ResourceBundleUtil.getString(
				resourceBundle,
				"asset-library-owners-are-super-users-of-their-asset-library-" +
					"and-can-assign-asset-library-roles-to-users");
		}

		return null;
	}

	private static String _getTitle(Locale locale, String name) {
		String title = _titleKeys.get(name);

		if (Validator.isNull(title)) {
			return name;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, DepotRolesPortalInstanceLifecycleListener.class);

		return ResourceBundleUtil.getString(resourceBundle, title);
	}

	private static final Map<String, String> _titleKeys;

	static {
		_titleKeys = Collections.unmodifiableMap(
			HashMapBuilder.put(
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
				"space-administrator"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
				"space-connected-site-member"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
				"space-content-reviewer"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_MEMBER, "space-member"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_OWNER, "space-owner"
			).build());
	}

}