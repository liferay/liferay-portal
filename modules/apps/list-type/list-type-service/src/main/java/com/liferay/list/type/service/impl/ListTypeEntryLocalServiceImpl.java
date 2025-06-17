/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.impl;

import com.liferay.list.type.exception.DuplicateListTypeEntryException;
import com.liferay.list.type.exception.DuplicateListTypeEntryExternalReferenceCodeException;
import com.liferay.list.type.exception.ListTypeEntryKeyException;
import com.liferay.list.type.exception.ListTypeEntryNameException;
import com.liferay.list.type.exception.ListTypeEntrySystemException;
import com.liferay.list.type.exception.NoSuchListTypeEntryException;
import com.liferay.list.type.internal.definition.util.ListTypeDefinitionUtil;
import com.liferay.list.type.internal.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.base.ListTypeEntryLocalServiceBaseImpl;
import com.liferay.list.type.service.persistence.ListTypeDefinitionPersistence;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = "model.class.name=com.liferay.list.type.model.ListTypeEntry",
	service = AopService.class
)
public class ListTypeEntryLocalServiceImpl
	extends ListTypeEntryLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ListTypeEntry addListTypeEntry(
			String externalReferenceCode, long userId,
			long listTypeDefinitionId, String key, Map<Locale, String> nameMap,
			boolean system)
		throws PortalException {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionPersistence.findByPrimaryKey(
				listTypeDefinitionId);

		if (FeatureFlagManagerUtil.isEnabled(
				listTypeDefinition.getCompanyId(), "LPD-24055")) {

			if (listTypeDefinition.isSystem()) {
				ListTypeEntryUtil.validateInvokerBundle(
					"Only allowed bundles can add system list type entries",
					system);
			}
			else if (system) {
				throw new ListTypeEntrySystemException(
					"System list type entries cannot be added to custom list " +
						"type definitions");
			}
		}
		else {
			ListTypeDefinitionUtil.validateInvokerBundle(
				"Only allowed bundles can add system list type entries",
				listTypeDefinition.isSystem());

			system = listTypeDefinition.isSystem();
		}

		User user = _userLocalService.getUser(userId);

		_validateExternalReferenceCode(
			externalReferenceCode, user.getCompanyId(), listTypeDefinitionId,
			0);

		_validateKey(listTypeDefinitionId, key);
		_validateName(nameMap);

		return _addListTypeEntry(
			externalReferenceCode, user, listTypeDefinitionId, key, nameMap,
			WorkflowConstants.STATUS_APPROVED, system);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ListTypeEntry deleteListTypeEntry(ListTypeEntry listTypeEntry)
		throws PortalException {

		if (FeatureFlagManagerUtil.isEnabled(
				listTypeEntry.getCompanyId(), "LPD-24055")) {

			ListTypeEntryUtil.validateInvokerBundle(
				"Only allowed bundles can delete system list type entries",
				listTypeEntry.isSystem());
		}
		else {
			ListTypeDefinition listTypeDefinition =
				_listTypeDefinitionPersistence.findByPrimaryKey(
					listTypeEntry.getListTypeDefinitionId());

			ListTypeDefinitionUtil.validateInvokerBundle(
				"Only allowed bundles can delete system list type entries",
				listTypeDefinition.isSystem());
		}

		return listTypeEntryPersistence.remove(listTypeEntry);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public ListTypeEntry deleteListTypeEntry(long listTypeEntryId)
		throws PortalException {

		ListTypeEntry listTypeEntry = listTypeEntryPersistence.findByPrimaryKey(
			listTypeEntryId);

		return deleteListTypeEntry(listTypeEntry);
	}

	@Override
	public void deleteListTypeEntryByListTypeDefinitionId(
			long listTypeDefinitionId)
		throws PortalException {

		for (ListTypeEntry listTypeEntry :
				listTypeEntryPersistence.findByListTypeDefinitionId(
					listTypeDefinitionId)) {

			listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
		}
	}

	@Override
	public ListTypeEntry fetchListTypeEntry(
		long listTypeDefinitionId, String key) {

		return listTypeEntryPersistence.fetchByLTDI_K(
			listTypeDefinitionId, key);
	}

	@Override
	public ListTypeEntry fetchListTypeEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId,
		long listTypeDefinitionId) {

		return listTypeEntryPersistence.fetchByERC_C_LTDI(
			externalReferenceCode, companyId, listTypeDefinitionId);
	}

	@Override
	public List<ListTypeEntry> getListTypeEntries(long listTypeDefinitionId) {
		return listTypeEntryPersistence.findByListTypeDefinitionId(
			listTypeDefinitionId);
	}

	@Override
	public List<ListTypeEntry> getListTypeEntries(
		long listTypeDefinitionId, int start, int end) {

		return listTypeEntryPersistence.findByListTypeDefinitionId(
			listTypeDefinitionId, start, end);
	}

	@Override
	public List<ListTypeEntry> getListTypeEntries(
		long listTypeDefinitionId, int start, int end,
		OrderByComparator<ListTypeEntry> orderByComparator) {

		return listTypeEntryPersistence.findByListTypeDefinitionId(
			listTypeDefinitionId, start, end, orderByComparator);
	}

	@Override
	public List<ListTypeEntry> getListTypeEntries(
		long[] listTypeDefinitionIds) {

		return listTypeEntryPersistence.findByListTypeDefinitionId(
			listTypeDefinitionIds);
	}

	@Override
	public int getListTypeEntriesCount(long listTypeDefinitionId) {
		return listTypeEntryPersistence.countByListTypeDefinitionId(
			listTypeDefinitionId);
	}

	@Override
	public ListTypeEntry getListTypeEntry(long listTypeDefinitionId, String key)
		throws PortalException {

		return listTypeEntryPersistence.findByLTDI_K(listTypeDefinitionId, key);
	}

	@Override
	public ListTypeEntry getListTypeEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long listTypeDefinitionId)
		throws PortalException {

		return listTypeEntryPersistence.findByERC_C_LTDI(
			externalReferenceCode, companyId, listTypeDefinitionId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ListTypeEntry getOrAddIncompleteListTypeEntry(
			long userId, long listTypeDefinitionId, String key)
		throws PortalException {

		ListTypeEntry listTypeEntry = fetchListTypeEntry(
			listTypeDefinitionId, key);

		if (listTypeEntry != null) {
			return listTypeEntry;
		}

		if (!LazyReferencingThreadLocal.isEnabled()) {
			throw new NoSuchListTypeEntryException();
		}

		return _addListTypeEntry(
			null, _userLocalService.getUser(userId), listTypeDefinitionId, key,
			null, WorkflowConstants.STATUS_INCOMPLETE, false);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ListTypeEntry updateListTypeEntry(
			String externalReferenceCode, long listTypeEntryId,
			Map<Locale, String> nameMap)
		throws PortalException {

		_validateName(nameMap);

		ListTypeEntry listTypeEntry = listTypeEntryPersistence.findByPrimaryKey(
			listTypeEntryId);

		listTypeEntry.setNameMap(nameMap);

		if (listTypeEntry.getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			listTypeEntry.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				listTypeEntry.getCompanyId(), "LPD-24055")) {

			ListTypeDefinition listTypeDefinition =
				_listTypeDefinitionPersistence.findByPrimaryKey(
					listTypeEntry.getListTypeDefinitionId());

			if (listTypeDefinition.isSystem() &&
				!ObjectDefinitionUtil.isInvokerBundleAllowed()) {

				return listTypeEntryPersistence.update(listTypeEntry);
			}
		}
		else if (listTypeEntry.isSystem() &&
				 !ObjectDefinitionUtil.isInvokerBundleAllowed()) {

			return listTypeEntryPersistence.update(listTypeEntry);
		}

		_validateExternalReferenceCode(
			externalReferenceCode, listTypeEntry.getCompanyId(),
			listTypeEntry.getListTypeDefinitionId(), listTypeEntryId);

		listTypeEntry.setExternalReferenceCode(externalReferenceCode);

		return listTypeEntryPersistence.update(listTypeEntry);
	}

	@Override
	public void updateUserId(long companyId, long oldUserId, long newUserId)
		throws PortalException {

		for (ListTypeEntry listTypeEntry :
				listTypeEntryPersistence.findByC_U(companyId, oldUserId)) {

			listTypeEntry.setUserId(newUserId);

			listTypeEntryPersistence.update(listTypeEntry);
		}
	}

	private ListTypeEntry _addListTypeEntry(
		String externalReferenceCode, User user, long listTypeDefinitionId,
		String key, Map<Locale, String> nameMap, int status, boolean system) {

		ListTypeEntry listTypeEntry = listTypeEntryPersistence.create(
			counterLocalService.increment());

		listTypeEntry.setExternalReferenceCode(externalReferenceCode);
		listTypeEntry.setCompanyId(user.getCompanyId());
		listTypeEntry.setUserId(user.getUserId());
		listTypeEntry.setUserName(user.getFullName());
		listTypeEntry.setListTypeDefinitionId(listTypeDefinitionId);
		listTypeEntry.setKey(key);
		listTypeEntry.setNameMap(nameMap);
		listTypeEntry.setSystem(system);
		listTypeEntry.setStatus(status);

		return listTypeEntryPersistence.update(listTypeEntry);
	}

	private void _validateExternalReferenceCode(
		String externalReferenceCode, long companyId, long listTypeDefinitionId,
		long listTypeEntryId) {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		ListTypeEntry listTypeEntry =
			listTypeEntryPersistence.fetchByERC_C_LTDI(
				externalReferenceCode, companyId, listTypeDefinitionId);

		if ((listTypeEntry != null) &&
			(listTypeEntry.getListTypeEntryId() != listTypeEntryId)) {

			throw new DuplicateListTypeEntryExternalReferenceCodeException(
				"Duplicate external reference code " + externalReferenceCode);
		}
	}

	private void _validateKey(long listTypeDefinitionId, String key)
		throws PortalException {

		_listTypeDefinitionPersistence.findByPrimaryKey(listTypeDefinitionId);

		if (Validator.isNull(key)) {
			throw new ListTypeEntryKeyException("Key is null");
		}

		char[] keyCharArray = key.toCharArray();

		for (char c : keyCharArray) {
			if (!Validator.isChar(c) && !Validator.isDigit(c)) {
				throw new ListTypeEntryKeyException(
					"Key must only contain letters and digits");
			}
		}

		ListTypeEntry listTypeEntry = listTypeEntryPersistence.fetchByLTDI_K(
			listTypeDefinitionId, key);

		if (listTypeEntry != null) {
			throw new DuplicateListTypeEntryException("Duplicate key " + key);
		}
	}

	private void _validateName(Map<Locale, String> nameMap)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		if ((nameMap == null) || Validator.isNull(nameMap.get(locale))) {
			throw new ListTypeEntryNameException(
				"Name is null for locale " + locale.getDisplayName());
		}
	}

	@Reference
	private ListTypeDefinitionPersistence _listTypeDefinitionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}