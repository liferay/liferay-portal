/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.impl;

import com.liferay.list.type.constants.ListTypeActionKeys;
import com.liferay.list.type.constants.ListTypeConstants;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.base.ListTypeDefinitionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

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
		"json.web.service.context.path=ListTypeDefinition"
	},
	service = AopService.class
)
public class ListTypeDefinitionServiceImpl
	extends ListTypeDefinitionServiceBaseImpl {

	@Override
	public ListTypeDefinition addListTypeDefinition(
			String externalReferenceCode, Map<Locale, String> nameMap,
			boolean system, List<ListTypeEntry> listTypeEntries,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			ListTypeActionKeys.ADD_LIST_TYPE_DEFINITION);

		return listTypeDefinitionLocalService.addListTypeDefinition(
			externalReferenceCode, getUserId(), nameMap, system,
			listTypeEntries, serviceContext);
	}

	@Override
	public ListTypeDefinition deleteListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws PortalException {

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(),
			listTypeDefinition.getListTypeDefinitionId(), ActionKeys.DELETE);

		return listTypeDefinitionLocalService.deleteListTypeDefinition(
			listTypeDefinition);
	}

	@Override
	public ListTypeDefinition deleteListTypeDefinition(
			long listTypeDefinitionId)
		throws PortalException {

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeDefinitionId, ActionKeys.DELETE);

		return listTypeDefinitionLocalService.deleteListTypeDefinition(
			listTypeDefinitionId);
	}

	@Override
	public ListTypeDefinition fetchListTypeDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (listTypeDefinition != null) {
			_listTypeDefinitionModelResourcePermission.check(
				getPermissionChecker(),
				listTypeDefinition.getListTypeDefinitionId(), ActionKeys.VIEW);
		}

		return listTypeDefinition;
	}

	@Override
	public ListTypeDefinition getListTypeDefinition(long listTypeDefinitionId)
		throws PortalException {

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeDefinitionId, ActionKeys.VIEW);

		return listTypeDefinitionPersistence.findByPrimaryKey(
			listTypeDefinitionId);
	}

	@Override
	public ListTypeDefinition getListTypeDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionLocalService.
				getListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(),
			listTypeDefinition.getListTypeDefinitionId(), ActionKeys.VIEW);

		return listTypeDefinition;
	}

	@Override
	public List<ListTypeDefinition> getListTypeDefinitions(int start, int end) {
		return listTypeDefinitionPersistence.findAll(start, end);
	}

	@Override
	public int getListTypeDefinitionsCount() {
		return listTypeDefinitionPersistence.countAll();
	}

	@Override
	public ListTypeDefinition getOrAddEmptyListTypeDefinition(
			String externalReferenceCode, boolean system)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionService.
				fetchListTypeDefinitionByExternalReferenceCode(
					externalReferenceCode, permissionChecker.getCompanyId());

		if (listTypeDefinition != null) {
			return listTypeDefinition;
		}

		_portletResourcePermission.check(
			permissionChecker, null,
			ListTypeActionKeys.ADD_LIST_TYPE_DEFINITION);

		return listTypeDefinitionLocalService.getOrAddEmptyListTypeDefinition(
			externalReferenceCode, permissionChecker.getCompanyId(),
			permissionChecker.getUserId(), system);
	}

	@Override
	public ListTypeDefinition updateListTypeDefinition(
			String externalReferenceCode, long listTypeDefinitionId,
			Map<Locale, String> nameMap, List<ListTypeEntry> listTypeEntries,
			ServiceContext serviceContext)
		throws PortalException {

		_listTypeDefinitionModelResourcePermission.check(
			getPermissionChecker(), listTypeDefinitionId, ActionKeys.UPDATE);

		return listTypeDefinitionLocalService.updateListTypeDefinition(
			externalReferenceCode, listTypeDefinitionId, getUserId(), nameMap,
			listTypeEntries, serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.list.type.model.ListTypeDefinition)"
	)
	private ModelResourcePermission<ListTypeDefinition>
		_listTypeDefinitionModelResourcePermission;

	@Reference(
		target = "(resource.name=" + ListTypeConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}