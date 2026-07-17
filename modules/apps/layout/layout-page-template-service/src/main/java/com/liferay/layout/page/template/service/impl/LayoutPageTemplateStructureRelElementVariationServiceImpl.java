/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateStructureRelElementVariationServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation",
	service = AopService.class
)
public class LayoutPageTemplateStructureRelElementVariationServiceImpl
	extends LayoutPageTemplateStructureRelElementVariationServiceBaseImpl {

	@Override
	public LayoutPageTemplateStructureRelElementVariation
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long groupId, boolean active,
				String hide, Map<Locale, String> htmlMap,
				Map<Locale, String> jsMap, String name, long plid,
				String segmentsExperienceERC, String targetElement,
				String[] audienceEntryERCs, ServiceContext serviceContext)
		throws PortalException {

		_layoutModelResourcePermission.check(
			getPermissionChecker(), plid, ActionKeys.UPDATE);

		return layoutPageTemplateStructureRelElementVariationLocalService.
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, getUserId(), groupId, active, hide,
				htmlMap, jsMap, name, plid, segmentsExperienceERC,
				targetElement, audienceEntryERCs, serviceContext);
	}

	@Override
	public void deleteLayoutPageTemplateStructureRelElementVariation(
			String externalReferenceCode, long groupId, long plid)
		throws PortalException {

		_layoutModelResourcePermission.check(
			getPermissionChecker(), plid, ActionKeys.UPDATE);

		layoutPageTemplateStructureRelElementVariationLocalService.
			deleteLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId);
	}

	@Override
	public List<LayoutPageTemplateStructureRelElementVariation>
			getLayoutPageTemplateStructureRelElementVariations(long plid)
		throws PortalException {

		_layoutModelResourcePermission.check(
			getPermissionChecker(), plid, ActionKeys.VIEW);

		return layoutPageTemplateStructureRelElementVariationLocalService.
			getLayoutPageTemplateStructureRelElementVariations(plid);
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation
			updateLayoutPageTemplateStructureRelElementVariation(
				String externalReferenceCode, long groupId, long plid,
				boolean active)
		throws PortalException {

		_layoutModelResourcePermission.check(
			getPermissionChecker(), plid, ActionKeys.UPDATE);

		return layoutPageTemplateStructureRelElementVariationLocalService.
			updateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, groupId, active);
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Layout)"
	)
	private ModelResourcePermission<Layout> _layoutModelResourcePermission;

}