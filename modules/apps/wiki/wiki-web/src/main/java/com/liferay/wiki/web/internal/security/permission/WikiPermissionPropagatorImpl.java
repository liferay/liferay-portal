/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.security.permission;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.propagator.BasePermissionPropagator;
import com.liferay.portal.kernel.security.permission.propagator.PermissionPropagator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import jakarta.portlet.ActionRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Hugo Huijser
 * @author Angelo Jefferson
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY
	},
	service = PermissionPropagator.class
)
public class WikiPermissionPropagatorImpl extends BasePermissionPropagator {

	@Override
	public void propagateRolePermissions(
			ActionRequest actionRequest, String className, String primKey,
			long[] roleIds)
		throws PortalException {

		if (!className.equals(WikiNode.class.getName())) {
			return;
		}

		long nodeId = GetterUtil.getLong(primKey);

		List<WikiPage> wikiPages = _wikiPageLocalService.getPages(
			nodeId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (WikiPage wikiPage : wikiPages) {
			for (long roleId : roleIds) {
				propagateRolePermissions(
					actionRequest, roleId, WikiNode.class.getName(), nodeId,
					WikiPage.class.getName(), wikiPage.getResourcePrimKey());
			}
		}
	}

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}