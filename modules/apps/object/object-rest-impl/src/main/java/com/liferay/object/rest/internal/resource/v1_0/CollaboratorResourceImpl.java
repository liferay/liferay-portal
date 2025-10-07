/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.headless.object.dto.v1_0.Collaborator;
import com.liferay.headless.object.util.v1_0.CollaboratorUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.service.SharingEntryService;

import jakarta.ws.rs.core.Context;

/**
 * @author Mikel Lorza
 */
public class CollaboratorResourceImpl extends BaseCollaboratorResourceImpl {

	public CollaboratorResourceImpl(
		ClassNameLocalService classNameLocalService,
		DTOConverter<SharingEntry, Collaborator> collaboratorDTOConverter,
		DTOConverterRegistry dtoConverterRegistry,
		GroupLocalService groupLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		SharingEntryService sharingEntryService,
		SharingEntryLocalService sharingEntryLocalService,
		UserGroupLocalService userGroupLocalService,
		UserLocalService userLocalService) {

		_classNameLocalService = classNameLocalService;
		_collaboratorDTOConverter = collaboratorDTOConverter;
		_dtoConverterRegistry = dtoConverterRegistry;
		_groupLocalService = groupLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_sharingEntryService = sharingEntryService;
		_sharingEntryLocalService = sharingEntryLocalService;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public void deleteObjectEntryCollaboratorByTypeCollaborator(
			Long objectEntryId, String type, Long collaboratorId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		CollaboratorUtil.deleteCollaborator(
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaboratorId,
			_sharingEntryService, type);
	}

	@Override
	public void
			deleteScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		CollaboratorUtil.deleteCollaborator(
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaboratorId,
			_sharingEntryService, type);
	}

	@Override
	public Collaborator getObjectEntryCollaboratorByTypeCollaborator(
			Long objectEntryId, String type, Long collaboratorId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		return CollaboratorUtil.getCollaborator(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaboratorId,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			_sharingEntryService, type, contextUriInfo, contextUser);
	}

	@Override
	public Page<Collaborator> getObjectEntryCollaboratorsPage(
			Long objectEntryId, Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		return CollaboratorUtil.getCollaborators(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), _collaboratorDTOConverter,
			_dtoConverterRegistry, objectEntry.getGroupId(), pagination,
			_sharingEntryLocalService, _sharingEntryService, contextUriInfo,
			contextUser);
	}

	@Override
	public Collaborator
			getScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		return CollaboratorUtil.getCollaborator(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaboratorId,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			_sharingEntryService, type, contextUriInfo, contextUser);
	}

	@Override
	public Page<Collaborator>
			getScopeScopeKeyByExternalReferenceCodeCollaboratorsPage(
				String scopeKey, String externalReferenceCode,
				Pagination pagination)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		return CollaboratorUtil.getCollaborators(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), _collaboratorDTOConverter,
			_dtoConverterRegistry, objectEntry.getGroupId(), pagination,
			_sharingEntryLocalService, _sharingEntryService, contextUriInfo,
			contextUser);
	}

	@Override
	public Page<Collaborator> postObjectEntryCollaboratorsPage(
			Long objectEntryId, Collaborator[] collaborators)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		return CollaboratorUtil.addOrUpdateCollaborators(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborators,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), _sharingEntryService, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	@Override
	public Page<Collaborator>
			postScopeScopeKeyByExternalReferenceCodeCollaboratorsPage(
				String scopeKey, String externalReferenceCode,
				Collaborator[] collaborators)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		return CollaboratorUtil.addOrUpdateCollaborators(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborators,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), _sharingEntryService, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	@Override
	public Collaborator putObjectEntryCollaboratorByTypeCollaborator(
			Long objectEntryId, String type, Long collaboratorId,
			Collaborator collaborator)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		return CollaboratorUtil.addOrUpdateCollaborator(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborator, collaboratorId,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), _sharingEntryService, type,
			_userGroupLocalService, contextUriInfo, contextUser,
			_userLocalService);
	}

	@Override
	public Collaborator
			putScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId, Collaborator collaborator)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			throw new UnsupportedOperationException();
		}

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		return CollaboratorUtil.addOrUpdateCollaborator(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborator, collaboratorId,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), _sharingEntryService, type,
			_userGroupLocalService, contextUriInfo, contextUser,
			_userLocalService);
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	private final ClassNameLocalService _classNameLocalService;
	private final DTOConverter<SharingEntry, Collaborator>
		_collaboratorDTOConverter;
	private final DTOConverterRegistry _dtoConverterRegistry;
	private final GroupLocalService _groupLocalService;

	@Context
	private ObjectDefinition _objectDefinition;

	private final ObjectEntryLocalService _objectEntryLocalService;
	private final SharingEntryLocalService _sharingEntryLocalService;
	private final SharingEntryService _sharingEntryService;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;

}