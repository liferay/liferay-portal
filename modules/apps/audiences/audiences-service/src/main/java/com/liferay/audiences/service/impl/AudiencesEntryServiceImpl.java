/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.impl;

import com.liferay.audiences.constants.AudiencesActionKeys;
import com.liferay.audiences.constants.AudiencesConstants;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.base.AudiencesEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=audiences",
		"json.web.service.context.path=AudiencesEntry"
	},
	service = AopService.class
)
public class AudiencesEntryServiceImpl extends AudiencesEntryServiceBaseImpl {

	@Override
	public AudiencesEntry addAudiencesEntry(
			String externalReferenceCode, String json, String name)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryLocalService.addAudiencesEntry(
			externalReferenceCode, getUserId(), json, name);
	}

	@Override
	public AudiencesEntry deleteAudiencesEntry(long audiencesEntryId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryLocalService.deleteAudiencesEntry(
			audiencesEntryId);
	}

	@Override
	public List<AudiencesEntry> getAudiencesEntries(
			long companyId, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<AudiencesEntry> getAudiencesEntries(
			long companyId, String name, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryPersistence.findByC_LikeN(
			companyId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public int getAudiencesEntriesCount(long companyId) throws PortalException {
		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getAudiencesEntriesCount(long companyId, String name)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryPersistence.countByC_LikeN(
			companyId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public AudiencesEntry getAudiencesEntry(long audiencesEntryId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryPersistence.findByPrimaryKey(audiencesEntryId);
	}

	@Override
	public AudiencesEntry updateAudiencesEntry(
			long audiencesEntryId, String externalReferenceCode, String json,
			String name)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), 0,
			AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES);

		return audiencesEntryLocalService.updateAudiencesEntry(
			externalReferenceCode, getUserId(), audiencesEntryId, json, name);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference(
		target = "(resource.name=" + AudiencesConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}