/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutUtil {

	public static JSONObject getMappedLayoutJSONObject(
			long companyId, String externalReferenceCode, Scope scope,
			long scopeGroupId)
		throws PortalException {

		Long groupId = ItemScopeUtil.getGroupId(companyId, scope, scopeGroupId);

		if (groupId == null) {
			LogUtil.logOptionalReference(
				Layout.class.getName(), externalReferenceCode, scope,
				scopeGroupId);

			return JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"scopeExternalReferenceCode",
				ItemScopeUtil.getItemScopeExternalReferenceCode(
					scope, scopeGroupId)
			);
		}

		Layout layout =
			LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
				externalReferenceCode, groupId);

		if (layout == null) {
			LogUtil.logOptionalReference(
				Layout.class.getName(), externalReferenceCode, scope,
				scopeGroupId);

			return JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"scopeExternalReferenceCode",
				ItemScopeUtil.getItemScopeExternalReferenceCode(
					scope, scopeGroupId)
			);
		}

		return JSONUtil.put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"groupId", String.valueOf(layout.getGroupId())
		).put(
			"layoutId", String.valueOf(layout.getLayoutId())
		).put(
			"layoutUuid", layout.getUuid()
		).put(
			"privateLayout", layout.isPrivateLayout()
		).put(
			"scopeExternalReferenceCode",
			ItemScopeUtil.getItemScopeExternalReferenceCode(scope, scopeGroupId)
		).put(
			"title", layout.getName(LocaleUtil.getMostRelevantLocale())
		);
	}

}