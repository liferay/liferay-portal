/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.util;

import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Georgel Pop
 */
public class PageTemplateSetsUtil {

	public static JSONArray getPageTemplateSetsJSONArray(
		long groupId,
		LayoutPageTemplateCollectionLocalService
			layoutPageTemplateCollectionLocalService) {

		return JSONUtil.toJSONArray(
			layoutPageTemplateCollectionLocalService.
				getLayoutPageTemplateCollections(
					groupId, LayoutPageTemplateCollectionTypeConstants.BASIC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			layoutPageTemplateCollection -> JSONUtil.put(
				"id",
				layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
			).put(
				"name", layoutPageTemplateCollection.getName()
			),
			_log);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PageTemplateSetsUtil.class);

}