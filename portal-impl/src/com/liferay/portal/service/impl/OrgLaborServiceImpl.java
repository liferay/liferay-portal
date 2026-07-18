/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.base.OrgLaborServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class OrgLaborServiceImpl extends OrgLaborServiceBaseImpl {

	@Override
	public OrgLabor addOrgLabor(
			long organizationId, long typeId, int sunOpen, int sunClose,
			int monOpen, int monClose, int tueOpen, int tueClose, int wedOpen,
			int wedClose, int thuOpen, int thuClose, int friOpen, int friClose,
			int satOpen, int satClose)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.UPDATE);

		return orgLaborLocalService.addOrgLabor(
			organizationId, typeId, sunOpen, sunClose, monOpen, monClose,
			tueOpen, tueClose, wedOpen, wedClose, thuOpen, thuClose, friOpen,
			friClose, satOpen, satClose);
	}

	@Override
	public void deleteOrgLabor(long orgLaborId) throws PortalException {
		OrgLabor orgLabor = orgLaborPersistence.findByPrimaryKey(orgLaborId);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), orgLabor.getOrganizationId(),
			ActionKeys.UPDATE);

		orgLaborLocalService.deleteOrgLabor(orgLaborId);
	}

	@Override
	public OrgLabor getOrgLabor(long orgLaborId) throws PortalException {
		OrgLabor orgLabor = orgLaborPersistence.findByPrimaryKey(orgLaborId);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), orgLabor.getOrganizationId(),
			ActionKeys.VIEW);

		return orgLabor;
	}

	@Override
	public List<OrgLabor> getOrgLabors(long organizationId)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.VIEW);

		return orgLaborPersistence.findByOrganizationId(organizationId);
	}

	@Override
	public OrgLabor updateOrgLabor(
			long orgLaborId, long typeId, int sunOpen, int sunClose,
			int monOpen, int monClose, int tueOpen, int tueClose, int wedOpen,
			int wedClose, int thuOpen, int thuClose, int friOpen, int friClose,
			int satOpen, int satClose)
		throws PortalException {

		OrgLabor orgLabor = orgLaborPersistence.findByPrimaryKey(orgLaborId);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), orgLabor.getOrganizationId(),
			ActionKeys.UPDATE);

		return orgLaborLocalService.updateOrgLabor(
			orgLaborId, typeId, sunOpen, sunClose, monOpen, monClose, tueOpen,
			tueClose, wedOpen, wedClose, thuOpen, thuClose, friOpen, friClose,
			satOpen, satClose);
	}

}