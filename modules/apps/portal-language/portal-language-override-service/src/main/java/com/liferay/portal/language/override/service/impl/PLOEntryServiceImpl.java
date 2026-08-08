/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.language.override.constants.PLOActionKeys;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.base.PLOEntryServiceBaseImpl;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Thiago Buarque
 */
@Component(
	property = {
		"json.web.service.context.name=portallanguageoverride",
		"json.web.service.context.path=PLOEntry"
	},
	service = AopService.class
)
public class PLOEntryServiceImpl extends PLOEntryServiceBaseImpl {

	@Override
	public PLOEntry addOrUpdatePLOEntry(
			String externalReferenceCode, long userId, String key,
			String languageId, String value)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryLocalService.addOrUpdatePLOEntry(
			externalReferenceCode, permissionChecker.getCompanyId(), userId,
			key, languageId, value);
	}

	@Override
	public void deletePLOEntries(String key) throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		ploEntryLocalService.deletePLOEntries(
			permissionChecker.getCompanyId(), key);
	}

	@Override
	public PLOEntry deletePLOEntry(String key, String languageId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryLocalService.deletePLOEntry(
			permissionChecker.getCompanyId(), key, languageId);
	}

	@Override
	public PLOEntry deletePLOEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryLocalService.deletePLOEntryByExternalReferenceCode(
			externalReferenceCode, permissionChecker.getCompanyId());
	}

	@Override
	public List<PLOEntry> getPLOEntries() throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryPersistence.findByCompanyId(
			permissionChecker.getCompanyId());
	}

	@Override
	public List<PLOEntry> getPLOEntries(
			int start, int end, OrderByComparator<PLOEntry> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryLocalService.getPLOEntries(
			permissionChecker.getCompanyId(), start, end, orderByComparator);
	}

	@Override
	public List<PLOEntry> getPLOEntries(
			String keywords, int start, int end,
			OrderByComparator<PLOEntry> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryLocalService.getPLOEntries(
			permissionChecker.getCompanyId(), keywords, start, end,
			orderByComparator);
	}

	@Override
	public int getPLOEntriesCount() throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryPersistence.countByCompanyId(
			permissionChecker.getCompanyId());
	}

	@Override
	public int getPLOEntriesCount(String keywords) throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryLocalService.getPLOEntriesCount(
			permissionChecker.getCompanyId(), keywords);
	}

	@Override
	public PLOEntry getPLOEntryByExternalReferenceCode(
			String externalReferenceCode)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		return ploEntryLocalService.getPLOEntryByExternalReferenceCode(
			externalReferenceCode, permissionChecker.getCompanyId());
	}

	@Override
	public void importPLOEntries(String languageId, Properties properties)
		throws IOException, PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		ploEntryLocalService.importPLOEntries(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			languageId, properties);
	}

	@Override
	public void setPLOEntries(String key, Map<Locale, String> localizationMap)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		PortalPermissionUtil.check(
			permissionChecker, PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES);

		ploEntryLocalService.setPLOEntries(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			key, localizationMap);
	}

}