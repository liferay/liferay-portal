/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateStructureServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.GetterUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"json.web.service.context.name=layout",
		"json.web.service.context.path=LayoutPageTemplateStructure"
	},
	service = AopService.class
)
public class LayoutPageTemplateStructureServiceImpl
	extends LayoutPageTemplateStructureServiceBaseImpl {

	@Override
	public LayoutPageTemplateStructure updateLayoutPageTemplateStructureData(
			long groupId, long plid, long segmentsExperienceId, String data)
		throws PortalException {

		if (GetterUtil.getBoolean(
				ModelResourcePermissionUtil.contains(
					getPermissionChecker(), groupId, Layout.class.getName(),
					plid, ActionKeys.UPDATE)) ||
			_layoutPermission.containsLayoutRestrictedUpdatePermission(
				getPermissionChecker(), plid)) {

			return layoutPageTemplateStructureLocalService.
				updateLayoutPageTemplateStructureData(
					getUserId(), groupId, plid, segmentsExperienceId, data);
		}

		throw new PrincipalException.MustHavePermission(
			getUserId(), Layout.class.getName(), plid, ActionKeys.UPDATE);
	}

	@Reference
	private LayoutPermission _layoutPermission;

}