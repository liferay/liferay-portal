/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.impl;

import com.liferay.exportimport.kernel.empty.model.EmptyModelManager;
import com.liferay.exportimport.kernel.empty.model.EmptyModelManagerUtil;
import com.liferay.list.type.exception.ListTypeDefinitionNameException;
import com.liferay.list.type.exception.RequiredListTypeDefinitionException;
import com.liferay.list.type.internal.definition.util.ListTypeDefinitionUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.list.type.service.base.ListTypeDefinitionLocalServiceBaseImpl;
import com.liferay.list.type.service.persistence.ListTypeEntryPersistence;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = "model.class.name=com.liferay.list.type.model.ListTypeDefinition",
	service = AopService.class
)
public class ListTypeDefinitionLocalServiceImpl
	extends ListTypeDefinitionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ListTypeDefinition addListTypeDefinition(
			String externalReferenceCode, long userId,
			Map<Locale, String> nameMap, boolean system,
			List<ListTypeEntry> listTypeEntries, ServiceContext serviceContext)
		throws PortalException {

		ListTypeDefinitionUtil.validateInvokerBundle(
			"Only allowed bundles can add system list type definitions",
			system);

		_validateName(nameMap, LocaleUtil.getSiteDefault());

		ListTypeDefinition listTypeDefinition = _addListTypeDefinition(
			externalReferenceCode, userId, nameMap, system,
			WorkflowConstants.STATUS_APPROVED);

		_addOrUpdateListTypeEntries(
			userId, listTypeDefinition.getListTypeDefinitionId(),
			listTypeEntries);

		_updateResourcePermissions(listTypeDefinition, serviceContext);

		return listTypeDefinition;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ListTypeDefinition deleteListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws PortalException {

		ListTypeDefinitionUtil.validateInvokerBundle(
			"Only allowed bundles can delete system list type definitions",
			listTypeDefinition.isSystem());

		int count =
			_objectFieldLocalService.getObjectFieldsCountByListTypeDefinitionId(
				listTypeDefinition.getListTypeDefinitionId());

		if (count > 0) {
			throw new RequiredListTypeDefinitionException();
		}

		_resourceLocalService.deleteResource(
			listTypeDefinition, ResourceConstants.SCOPE_INDIVIDUAL);

		listTypeDefinition = listTypeDefinitionPersistence.remove(
			listTypeDefinition);

		_listTypeEntryPersistence.removeByListTypeDefinitionId(
			listTypeDefinition.getListTypeDefinitionId());

		return listTypeDefinition;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ListTypeDefinition deleteListTypeDefinition(
			long listTypeDefinitionId)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionPersistence.findByPrimaryKey(
				listTypeDefinitionId);

		return deleteListTypeDefinition(listTypeDefinition);
	}

	@Indexable(type = IndexableType.REINDEX)
	public ListTypeDefinition getOrAddEmptyListTypeDefinition(
			String externalReferenceCode, long companyId, long userId,
			boolean system)
		throws PortalException {

		return _emptyModelManager.getOrAddEmptyModel(
			ListTypeDefinition.class, companyId,
			() -> _addListTypeDefinition(
				externalReferenceCode, userId,
				Collections.singletonMap(
					LocaleUtil.getDefault(), externalReferenceCode),
				system, WorkflowConstants.STATUS_EMPTY),
			externalReferenceCode,
			this::fetchListTypeDefinitionByExternalReferenceCode,
			this::getListTypeDefinitionByExternalReferenceCode,
			ListTypeDefinition.class.getName());
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ListTypeDefinition updateListTypeDefinition(
			String externalReferenceCode, long listTypeDefinitionId,
			long userId, Map<Locale, String> nameMap,
			List<ListTypeEntry> listTypeEntries, ServiceContext serviceContext)
		throws PortalException {

		_validateName(nameMap, LocaleUtil.getSiteDefault());

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionPersistence.findByPrimaryKey(
				listTypeDefinitionId);

		if (!listTypeDefinition.isSystem() ||
			ObjectDefinitionUtil.isInvokerBundleAllowed()) {

			listTypeDefinition.setExternalReferenceCode(externalReferenceCode);
		}

		listTypeDefinition.setNameMap(nameMap);
		listTypeDefinition.setStatus(
			EmptyModelManagerUtil.solveEmptyModel(
				externalReferenceCode, listTypeDefinition.getModelClassName(),
				listTypeDefinition.getCompanyId(), 0,
				listTypeDefinition.getStatus(),
				() -> WorkflowConstants.STATUS_APPROVED));

		listTypeDefinition = listTypeDefinitionPersistence.update(
			listTypeDefinition);

		_addOrUpdateListTypeEntries(
			userId, listTypeDefinitionId, listTypeEntries);

		_updateResourcePermissions(listTypeDefinition, serviceContext);

		return listTypeDefinition;
	}

	@Override
	public void updateUserId(long companyId, long oldUserId, long newUserId)
		throws PortalException {

		for (ListTypeDefinition listTypeDefinition :
				listTypeDefinitionPersistence.findByC_U(companyId, oldUserId)) {

			listTypeDefinition.setUserId(newUserId);

			listTypeDefinitionPersistence.update(listTypeDefinition);
		}
	}

	private ListTypeDefinition _addListTypeDefinition(
			String externalReferenceCode, long userId,
			Map<Locale, String> nameMap, boolean system, int status)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionPersistence.create(
				counterLocalService.increment());

		listTypeDefinition.setExternalReferenceCode(externalReferenceCode);

		User user = _userLocalService.getUser(userId);

		listTypeDefinition.setCompanyId(user.getCompanyId());
		listTypeDefinition.setUserId(user.getUserId());
		listTypeDefinition.setUserName(user.getFullName());

		listTypeDefinition.setNameMap(nameMap);
		listTypeDefinition.setSystem(system);
		listTypeDefinition.setStatus(status);

		listTypeDefinition = listTypeDefinitionPersistence.update(
			listTypeDefinition);

		_resourceLocalService.addResources(
			listTypeDefinition.getCompanyId(), 0,
			listTypeDefinition.getUserId(), ListTypeDefinition.class.getName(),
			listTypeDefinition.getListTypeDefinitionId(), false, true, true);

		return listTypeDefinition;
	}

	private void _addOrUpdateListTypeEntries(
			long userId, long listTypeDefinitionId,
			List<ListTypeEntry> listTypeEntries)
		throws PortalException {

		List<ListTypeEntry> existingListTypeEntries = new ArrayList<>(
			_listTypeEntryLocalService.getListTypeEntries(
				listTypeDefinitionId));

		for (ListTypeEntry listTypeEntry : listTypeEntries) {
			ListTypeEntry existingListTypeEntry = null;

			if (listTypeEntry.getListTypeEntryId() > 0) {
				existingListTypeEntry =
					_listTypeEntryPersistence.fetchByPrimaryKey(
						listTypeEntry.getListTypeEntryId());
			}

			if ((existingListTypeEntry == null) &&
				Validator.isNotNull(listTypeEntry.getExternalReferenceCode())) {

				existingListTypeEntry =
					_listTypeEntryLocalService.
						fetchListTypeEntryByExternalReferenceCode(
							listTypeEntry.getExternalReferenceCode(),
							listTypeEntry.getCompanyId(), listTypeDefinitionId);
			}

			if ((existingListTypeEntry == null) &&
				Validator.isNotNull(listTypeEntry.getKey())) {

				existingListTypeEntry = _listTypeEntryPersistence.fetchByLTDI_K(
					listTypeDefinitionId, listTypeEntry.getKey());
			}

			if (existingListTypeEntry == null) {
				_listTypeEntryLocalService.addListTypeEntry(
					listTypeEntry.getExternalReferenceCode(), userId,
					listTypeDefinitionId, listTypeEntry.getKey(),
					listTypeEntry.getNameMap(), listTypeEntry.isSystem());

				continue;
			}

			_listTypeEntryLocalService.updateListTypeEntry(
				listTypeEntry.getExternalReferenceCode(),
				existingListTypeEntry.getListTypeEntryId(),
				listTypeEntry.getNameMap());

			existingListTypeEntries.removeIf(
				listTypeEntry1 -> StringUtil.equals(
					listTypeEntry1.getKey(), listTypeEntry.getKey()));
		}

		for (ListTypeEntry listTypeEntry : existingListTypeEntries) {
			_listTypeEntryLocalService.deleteListTypeEntry(
				listTypeEntry.getListTypeEntryId());
		}
	}

	private void _updateResourcePermissions(
			ListTypeDefinition listTypeDefinition,
			ServiceContext serviceContext)
		throws PortalException {

		if (serviceContext == null) {
			return;
		}

		ModelPermissions modelPermissions =
			serviceContext.getModelPermissions();

		if (modelPermissions == null) {
			return;
		}

		_permissionService.checkPermission(
			0, listTypeDefinition.getModelClassName(),
			String.valueOf(listTypeDefinition.getListTypeDefinitionId()));

		Collection<String> roleNames = modelPermissions.getRoleNames();

		for (ResourcePermission resourcePermission :
				_resourcePermissionLocalService.getResourcePermissions(
					listTypeDefinition.getCompanyId(),
					listTypeDefinition.getModelClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(
						listTypeDefinition.getListTypeDefinitionId()))) {

			Role role = _roleLocalService.fetchRole(
				resourcePermission.getRoleId());

			if ((role == null) || roleNames.contains(role.getName())) {
				continue;
			}

			for (ResourceAction resourceAction :
					_resourceActionLocalService.getResourceActions(
						listTypeDefinition.getModelClassName())) {

				_resourcePermissionLocalService.removeResourcePermission(
					listTypeDefinition.getCompanyId(),
					listTypeDefinition.getModelClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(
						listTypeDefinition.getListTypeDefinitionId()),
					role.getRoleId(), resourceAction.getActionId());
			}
		}

		_resourcePermissionLocalService.updateResourcePermissions(
			listTypeDefinition.getCompanyId(), 0,
			listTypeDefinition.getModelClassName(),
			String.valueOf(listTypeDefinition.getListTypeDefinitionId()),
			modelPermissions);
	}

	private void _validateName(
			Map<Locale, String> nameMap, Locale defaultLocale)
		throws PortalException {

		if ((nameMap == null) || Validator.isNull(nameMap.get(defaultLocale))) {
			throw new ListTypeDefinitionNameException(
				"Name is null for locale " + defaultLocale.getDisplayName());
		}
	}

	@Reference
	private EmptyModelManager _emptyModelManager;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ListTypeEntryPersistence _listTypeEntryPersistence;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PermissionService _permissionService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}