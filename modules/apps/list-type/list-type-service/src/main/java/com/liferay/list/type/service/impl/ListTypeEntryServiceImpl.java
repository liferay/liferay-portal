/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.impl;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.base.ListTypeEntryServiceBaseImpl;
import com.liferay.list.type.service.persistence.ListTypeDefinitionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"json.web.service.context.name=listtype",
		"json.web.service.context.path=ListTypeEntry"
	},
	service = AopService.class
)
public class ListTypeEntryServiceImpl extends ListTypeEntryServiceBaseImpl {

	@Override
	public ListTypeEntry addListTypeEntry(
			String externalReferenceCode, long listTypeDefinitionId, String key,
			Map<Locale, String> nameMap, boolean system)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionPersistence.findByPrimaryKey(
				listTypeDefinitionId);

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(),
			listTypeDefinition.getListTypeDefinitionId(), ActionKeys.UPDATE);

		return listTypeEntryLocalService.addListTypeEntry(
			externalReferenceCode, getUserId(), listTypeDefinitionId, key,
			nameMap, system);
	}

	@Override
	public ListTypeEntry deleteListTypeEntry(long listTypeEntryId)
		throws PortalException {

		ListTypeEntry listTypeEntry = listTypeEntryPersistence.findByPrimaryKey(
			listTypeEntryId);

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeEntry.getListTypeDefinitionId(),
			ActionKeys.UPDATE);

		return listTypeEntryLocalService.deleteListTypeEntry(listTypeEntryId);
	}

	@Override
	public ListTypeEntry fetchListTypeEntry(
			long listTypeDefinitionId, String key)
		throws PortalException {

		ListTypeEntry listTypeEntry =
			listTypeEntryLocalService.fetchListTypeEntry(
				listTypeDefinitionId, key);

		if (listTypeEntry != null) {
			_listTypeDefinitionModelResourcePermission.check(
				getPermissionChecker(), listTypeDefinitionId, ActionKeys.VIEW);
		}

		return listTypeEntry;
	}

	@Override
	public List<ListTypeEntry> getListTypeEntries(
			long listTypeDefinitionId, int start, int end)
		throws PortalException {

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeDefinitionId, ActionKeys.VIEW);

		return listTypeEntryLocalService.getListTypeEntries(
			listTypeDefinitionId, start, end);
	}

	@Override
	public int getListTypeEntriesCount(long listTypeDefinitionId)
		throws PortalException {

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeDefinitionId, ActionKeys.VIEW);

		return listTypeEntryLocalService.getListTypeEntriesCount(
			listTypeDefinitionId);
	}

	@Override
	public ListTypeEntry getListTypeEntry(long listTypeEntryId)
		throws PortalException {

		ListTypeEntry listTypeEntry = listTypeEntryPersistence.findByPrimaryKey(
			listTypeEntryId);

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeEntry.getListTypeDefinitionId(),
			ActionKeys.VIEW);

		return listTypeEntryLocalService.getListTypeEntry(listTypeEntryId);
	}

	@Override
	public ListTypeEntry getListTypeEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long listTypeDefinitionId)
		throws PortalException {

		ListTypeEntry listTypeEntry =
			listTypeEntryLocalService.getListTypeEntryByExternalReferenceCode(
				externalReferenceCode, companyId, listTypeDefinitionId);

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeEntry.getListTypeDefinitionId(),
			ActionKeys.VIEW);

		return listTypeEntry;
	}

	@Override
	public ListTypeEntry getOrAddEmptyListTypeEntry(
			long userId, long listTypeDefinitionId, String key)
		throws PortalException {

		ListTypeEntry listTypeEntry = listTypeEntryService.fetchListTypeEntry(
			listTypeDefinitionId, key);

		if (listTypeEntry != null) {
			return listTypeEntry;
		}

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionPersistence.findByPrimaryKey(
				listTypeDefinitionId);

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(),
			listTypeDefinition.getListTypeDefinitionId(), ActionKeys.UPDATE);

		return listTypeEntryLocalService.getOrAddEmptyListTypeEntry(
			userId, listTypeDefinitionId, key);
	}

	@Override
	public ListTypeEntry updateListTypeEntry(
			String externalReferenceCode, long listTypeEntryId,
			Map<Locale, String> nameMap)
		throws PortalException {

		ListTypeEntry listTypeEntry = listTypeEntryPersistence.findByPrimaryKey(
			listTypeEntryId);

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeEntry.getListTypeDefinitionId(),
			ActionKeys.UPDATE);

		return listTypeEntryLocalService.updateListTypeEntry(
			externalReferenceCode, listTypeEntryId, nameMap);
	}

	@Reference(
		target = "(model.class.name=com.liferay.list.type.model.ListTypeDefinition)"
	)
	private ModelResourcePermission<ListTypeDefinition>
		_listTypeDefinitionModelResourcePermission;

	@Reference
	private ListTypeDefinitionPersistence _listTypeDefinitionPersistence;

}