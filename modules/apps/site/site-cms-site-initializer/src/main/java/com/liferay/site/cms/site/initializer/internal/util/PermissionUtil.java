/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class PermissionUtil {

	public static Map<String, Object> getDefaultPermissionAdditionalProps(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		return HashMapBuilder.<String, Object>put(
			"actions",
			() -> HashMapBuilder.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_CMS_BASIC_WEB_CONTENT",
								themeDisplay.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_CMS_BASIC_DOCUMENT",
								themeDisplay.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				"OBJECT_ENTRY_FOLDERS",
				() -> {
					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, ObjectEntryFolder.class.getName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							ObjectEntryFolder.class.getName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).build()
		).put(
			"roles",
			() -> TransformUtil.transformToArray(
				RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
					themeDisplay.getCompanyId(), null,
					Arrays.asList(
						RoleConstants.ADMINISTRATOR,
						DepotRolesConstants.ASSET_LIBRARY_OWNER),
					null, null,
					new int[] {
						RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_DEPOT
					},
					0, 0, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				role -> HashMapBuilder.put(
					"key", role.getName()
				).put(
					"name", role.getTitle(themeDisplay.getLocale())
				).put(
					"type", String.valueOf(role.getType())
				).build(),
				Map.class)
		).build();
	}

}