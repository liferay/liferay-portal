/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Shuyang Zhou
 * @author Stefano Motta
 */
public class DepotRoleUtil {

	public static List<Role> filter(DepotEntry depotEntry, List<Role> roles) {
		if (depotEntry == null) {
			return roles;
		}

		return filter(
			roles, DepotRolesConstants.getSubtype(depotEntry.getType()));
	}

	public static List<Role> filter(List<Role> roles, String subtype) {
		if (Validator.isNull(subtype) ||
			!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-96750")) {

			return roles;
		}

		if (Objects.equals(
				subtype, DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY) ||
			Objects.equals(subtype, DepotRolesConstants.SUBTYPE_PROJECT)) {

			return ListUtil.filter(
				roles, role -> Objects.equals(role.getSubtype(), subtype));
		}

		return ListUtil.filter(
			roles,
			role -> {
				String roleSubtype = role.getSubtype();

				return Validator.isNull(roleSubtype) ||
					   Objects.equals(roleSubtype, subtype);
			});
	}

	public static List<Role> filter(long groupId, List<Role> roles) {
		return filter(
			DepotEntryLocalServiceUtil.fetchGroupDepotEntry(groupId), roles);
	}

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

	public static String getSubtype(long groupId) {
		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			return null;
		}

		return DepotRolesConstants.getSubtype(depotEntry.getType());
	}

	public static Map<Locale, String> getTitleMap(
		Language language, String name) {

		Map<Locale, String> titleMap = new HashMap<>();

		for (Locale locale : language.getAvailableLocales()) {
			String title = _getTitle(locale, name);

			if (title != null) {
				titleMap.put(locale, title);
			}
		}

		return titleMap;
	}

	public static Map<Locale, String> getTitleMap(
		long companyId, Language language, String name) {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			return null;
		}

		return getTitleMap(language, name);
	}

	private static String _getDescription(
		long companyId, Locale locale, String name) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, DepotRoleUtil.class);

		if (Objects.equals(
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR, name)) {

			if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
				return ResourceBundleUtil.getString(
					resourceBundle,
					"depot-administrators-are-super-users-of-their-depot-but-" +
						"cannot-make-other-users-into-depot-administrators");
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
					"all-users-who-belong-to-a-depot-have-this-role-within-" +
						"that-depot");
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
					"depot-owners-are-super-users-of-their-depot-and-can-" +
						"assign-depot-roles-to-users");
			}

			return ResourceBundleUtil.getString(
				resourceBundle,
				"asset-library-owners-are-super-users-of-their-asset-library-" +
					"and-can-assign-asset-library-roles-to-users");
		}
		else if (Objects.equals(
					DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR, name)) {

			return ResourceBundleUtil.getString(
				resourceBundle,
				"design-library-administrators-are-super-users-of-their-" +
					"design-library-but-cannot-make-other-users-into-design-" +
						"library-administrators");
		}
		else if (Objects.equals(
					DepotRolesConstants.DESIGN_LIBRARY_MEMBER, name)) {

			return ResourceBundleUtil.getString(
				resourceBundle,
				"all-users-who-belong-to-a-design-library-have-this-role-" +
					"within-that-design-library");
		}
		else if (Objects.equals(
					DepotRolesConstants.DESIGN_LIBRARY_OWNER, name)) {

			return ResourceBundleUtil.getString(
				resourceBundle,
				"design-library-owners-are-super-users-of-their-design-" +
					"library-and-can-assign-design-library-roles-to-users");
		}

		return null;
	}

	private static String _getTitle(Locale locale, String name) {
		String title = _titleKeys.get(name);

		if (Validator.isNull(title)) {
			return name;
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, DepotRoleUtil.class);

		return ResourceBundleUtil.getString(resourceBundle, title);
	}

	private static final Map<String, String> _titleKeys;

	static {
		_titleKeys = Collections.unmodifiableMap(
			HashMapBuilder.put(
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
				"depot-administrator"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
				"depot-connected-site-member"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
				"depot-content-reviewer"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_MEMBER, "depot-member"
			).put(
				DepotRolesConstants.ASSET_LIBRARY_OWNER, "depot-owner"
			).put(
				DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
				"design-library-administrator"
			).put(
				DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
				"design-library-content-reviewer"
			).put(
				DepotRolesConstants.DESIGN_LIBRARY_MEMBER,
				"design-library-member"
			).put(
				DepotRolesConstants.DESIGN_LIBRARY_OWNER, "design-library-owner"
			).build());
	}

}