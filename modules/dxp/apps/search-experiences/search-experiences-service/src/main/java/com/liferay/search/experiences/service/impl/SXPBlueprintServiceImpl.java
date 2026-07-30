/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.search.experiences.constants.SXPActionKeys;
import com.liferay.search.experiences.constants.SXPConstants;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.base.SXPBlueprintServiceBaseImpl;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	property = {
		"json.web.service.context.name=sxp",
		"json.web.service.context.path=SXPBlueprint",
		"jsonws.web.service.parameter.type.whitelist.class.names=com.liferay.search.experiences.util.comparator.SXPBlueprintModifiedDateComparator",
		"jsonws.web.service.parameter.type.whitelist.class.names=com.liferay.search.experiences.util.comparator.SXPBlueprintTitleComparator"
	},
	service = AopService.class
)
public class SXPBlueprintServiceImpl extends SXPBlueprintServiceBaseImpl {

	@Override
	public SXPBlueprint addSXPBlueprint(
			String externalReferenceCode, String configurationJSON,
			Map<Locale, String> descriptionMap, String elementInstancesJSON,
			String schemaVersion, Map<Locale, String> titleMap,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null, SXPActionKeys.ADD_SXP_BLUEPRINT);

		return sxpBlueprintLocalService.addSXPBlueprint(
			externalReferenceCode, getUserId(), configurationJSON,
			descriptionMap, elementInstancesJSON, schemaVersion, titleMap,
			serviceContext);
	}

	@Override
	public SXPBlueprint deleteSXPBlueprint(long sxpBlueprintId)
		throws PortalException {

		_sxpBlueprintModelResourcePermission.check(
			getPermissionChecker(), sxpBlueprintId, ActionKeys.DELETE);

		return sxpBlueprintLocalService.deleteSXPBlueprint(sxpBlueprintId);
	}

	@Override
	public SXPBlueprint fetchSXPBlueprint(long sxpBlueprintId)
		throws PortalException {

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.fetchByPrimaryKey(
			sxpBlueprintId);

		if (sxpBlueprint != null) {
			_sxpBlueprintModelResourcePermission.check(
				getPermissionChecker(), sxpBlueprint, ActionKeys.VIEW);
		}

		return sxpBlueprint;
	}

	@Override
	public SXPBlueprint fetchSXPBlueprintByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.fetchByERC_C(
			externalReferenceCode, companyId);

		if (sxpBlueprint != null) {
			_sxpBlueprintModelResourcePermission.check(
				getPermissionChecker(), sxpBlueprint, ActionKeys.VIEW);
		}

		return sxpBlueprint;
	}

	@Override
	public SXPBlueprint getSXPBlueprint(long sxpBlueprintId)
		throws PortalException {

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.findByPrimaryKey(
			sxpBlueprintId);

		_sxpBlueprintModelResourcePermission.check(
			getPermissionChecker(), sxpBlueprint,
			SXPActionKeys.APPLY_SXP_BLUEPRINT);

		return sxpBlueprint;
	}

	@Override
	public SXPBlueprint getSXPBlueprintByExternalReferenceCode(
			long companyId, String externalReferenceCode)
		throws PortalException {

		SXPBlueprint sxpBlueprint = sxpBlueprintPersistence.findByERC_C(
			externalReferenceCode, companyId);

		_sxpBlueprintModelResourcePermission.check(
			getPermissionChecker(), sxpBlueprint,
			SXPActionKeys.APPLY_SXP_BLUEPRINT);

		return sxpBlueprint;
	}

	@Override
	public SXPBlueprint updateSXPBlueprint(
			String externalReferenceCode, long sxpBlueprintId,
			String configurationJSON, Map<Locale, String> descriptionMap,
			String elementInstancesJSON, String schemaVersion,
			Map<Locale, String> titleMap, ServiceContext serviceContext)
		throws PortalException {

		_sxpBlueprintModelResourcePermission.check(
			getPermissionChecker(), sxpBlueprintId, ActionKeys.UPDATE);

		return sxpBlueprintLocalService.updateSXPBlueprint(
			externalReferenceCode, getUserId(), sxpBlueprintId,
			configurationJSON, descriptionMap, elementInstancesJSON,
			schemaVersion, titleMap, serviceContext);
	}

	@Reference(target = "(resource.name=" + SXPConstants.RESOURCE_NAME + ")")
	private volatile PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.search.experiences.model.SXPBlueprint)"
	)
	private volatile ModelResourcePermission<SXPBlueprint>
		_sxpBlueprintModelResourcePermission;

}