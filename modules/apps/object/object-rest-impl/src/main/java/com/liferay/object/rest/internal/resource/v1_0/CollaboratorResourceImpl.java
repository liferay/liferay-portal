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
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;
import com.liferay.sharing.service.SharingEntryService;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Context;

/**
 * @author Mikel Lorza
 */
public class CollaboratorResourceImpl extends BaseCollaboratorResourceImpl {

	public CollaboratorResourceImpl(
		ClassNameLocalService classNameLocalService,
		DTOConverter<SharingEntry, Collaborator> collaboratorDTOConverter,
		ConfigurationProvider configurationProvider,
		DTOConverterRegistry dtoConverterRegistry,
		GroupLocalService groupLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		SharingEntryLocalService sharingEntryLocalService,
		SharingEntryService sharingEntryService,
		TicketLocalService ticketLocalService,
		UserGroupLocalService userGroupLocalService,
		UserLocalService userLocalService) {

		_classNameLocalService = classNameLocalService;
		_collaboratorDTOConverter = collaboratorDTOConverter;
		_configurationProvider = configurationProvider;
		_dtoConverterRegistry = dtoConverterRegistry;
		_groupLocalService = groupLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_sharingEntryLocalService = sharingEntryLocalService;
		_sharingEntryService = sharingEntryService;
		_ticketLocalService = ticketLocalService;
		_userGroupLocalService = userGroupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	public void deleteObjectEntryCollaboratorByEmailAddress(
			Long objectEntryId, String emailAddress)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		CollaboratorUtil.deleteCollaboratorByEmailAddress(
			objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), contextCompany.getCompanyId(),
			emailAddress, _sharingEntryService, _ticketLocalService,
			contextUser, _userLocalService);
	}

	@Override
	public void deleteObjectEntryCollaboratorByTypeCollaborator(
			Long objectEntryId, String type, Long collaboratorId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		CollaboratorUtil.deleteCollaborator(
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaboratorId,
			contextCompany.getCompanyId(), _sharingEntryService, type);
	}

	@Override
	public void
			deleteScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress(
				String scopeKey, String externalReferenceCode,
				String emailAddress)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		CollaboratorUtil.deleteCollaboratorByEmailAddress(
			objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), contextCompany.getCompanyId(),
			emailAddress, _sharingEntryService, _ticketLocalService,
			contextUser, _userLocalService);
	}

	@Override
	public void
			deleteScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		CollaboratorUtil.deleteCollaborator(
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaboratorId,
			contextCompany.getCompanyId(), _sharingEntryService, type);
	}

	@Override
	public Collaborator getObjectEntryCollaboratorByEmailAddress(
			Long objectEntryId, String emailAddress)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		return CollaboratorUtil.getCollaboratorByEmailAddress(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), contextCompany.getCompanyId(),
			_collaboratorDTOConverter, _dtoConverterRegistry, emailAddress,
			_sharingEntryService, _ticketLocalService, contextUriInfo,
			contextUser, _userLocalService);
	}

	@Override
	public Collaborator getObjectEntryCollaboratorByTypeCollaborator(
			Long objectEntryId, String type, Long collaboratorId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		return CollaboratorUtil.getCollaborator(
			contextAcceptLanguage,
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaboratorId,
			contextCompany.getCompanyId(), _collaboratorDTOConverter,
			_dtoConverterRegistry, _sharingEntryService, type, contextUriInfo,
			contextUser);
	}

	@Override
	public Page<Collaborator> getObjectEntryCollaboratorsPage(
			Long objectEntryId, Pagination pagination)
		throws Exception {

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
			getScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress(
				String scopeKey, String externalReferenceCode,
				String emailAddress)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		return CollaboratorUtil.getCollaboratorByEmailAddress(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), contextCompany.getCompanyId(),
			_collaboratorDTOConverter, _dtoConverterRegistry, emailAddress,
			_sharingEntryService, _ticketLocalService, contextUriInfo,
			contextUser, _userLocalService);
	}

	@Override
	public Collaborator
			getScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId)
		throws Exception {

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
			contextCompany.getCompanyId(), _collaboratorDTOConverter,
			_dtoConverterRegistry, _sharingEntryService, type, contextUriInfo,
			contextUser);
	}

	@Override
	public Page<Collaborator>
			getScopeScopeKeyByExternalReferenceCodeCollaboratorsPage(
				String scopeKey, String externalReferenceCode,
				Pagination pagination)
		throws Exception {

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

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		_initThemeDisplay(objectEntry.getGroupId());

		return CollaboratorUtil.addOrUpdateCollaborators(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborators,
			contextCompany.getCompanyId(), _configurationProvider,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), contextHttpServletRequest,
			_sharingEntryService, _ticketLocalService, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	@Override
	public Page<Collaborator>
			postScopeScopeKeyByExternalReferenceCodeCollaboratorsPage(
				String scopeKey, String externalReferenceCode,
				Collaborator[] collaborators)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		_initThemeDisplay(objectEntry.getGroupId());

		return CollaboratorUtil.addOrUpdateCollaborators(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborators,
			contextCompany.getCompanyId(), _configurationProvider,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), contextHttpServletRequest,
			_sharingEntryService, _ticketLocalService, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	@Override
	public Collaborator putObjectEntryCollaboratorByEmailAddress(
			Long objectEntryId, String emailAddress, Collaborator collaborator)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		_initThemeDisplay(objectEntry.getGroupId());

		return CollaboratorUtil.addOrUpdateCollaboratorByEmailAddress(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborator,
			contextCompany.getCompanyId(), _configurationProvider,
			_collaboratorDTOConverter, _dtoConverterRegistry, emailAddress,
			objectEntry.getGroupId(), contextHttpServletRequest,
			_sharingEntryService, _ticketLocalService, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	@Override
	public Collaborator putObjectEntryCollaboratorByTypeCollaborator(
			Long objectEntryId, String type, Long collaboratorId,
			Collaborator collaborator)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		_initThemeDisplay(objectEntry.getGroupId());

		return CollaboratorUtil.addOrUpdateCollaborator(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborator, collaboratorId,
			contextCompany.getCompanyId(), _configurationProvider,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), contextHttpServletRequest,
			_sharingEntryService, _ticketLocalService, type, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	@Override
	public Collaborator
			putScopeScopeKeyByExternalReferenceCodeCollaboratorByEmailAddress(
				String scopeKey, String externalReferenceCode,
				String emailAddress, Collaborator collaborator)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		_initThemeDisplay(objectEntry.getGroupId());

		return CollaboratorUtil.addOrUpdateCollaboratorByEmailAddress(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborator,
			contextCompany.getCompanyId(), _configurationProvider,
			_collaboratorDTOConverter, _dtoConverterRegistry, emailAddress,
			objectEntry.getGroupId(), contextHttpServletRequest,
			_sharingEntryService, _ticketLocalService, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	@Override
	public Collaborator
			putScopeScopeKeyByExternalReferenceCodeCollaboratorByTypeCollaborator(
				String scopeKey, String externalReferenceCode, String type,
				Long collaboratorId, Collaborator collaborator)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			externalReferenceCode,
			CollaboratorUtil.getGroupId(
				contextCompany.getCompanyId(), _groupLocalService, scopeKey),
			_objectDefinition.getObjectDefinitionId());

		_initThemeDisplay(objectEntry.getGroupId());

		return CollaboratorUtil.addOrUpdateCollaborator(
			contextAcceptLanguage, objectEntry.getModelClassName(),
			_classNameLocalService.getClassNameId(
				objectEntry.getModelClassName()),
			objectEntry.getObjectEntryId(), collaborator, collaboratorId,
			contextCompany.getCompanyId(), _configurationProvider,
			_collaboratorDTOConverter, _dtoConverterRegistry,
			objectEntry.getGroupId(), contextHttpServletRequest,
			_sharingEntryService, _ticketLocalService, type, contextUriInfo,
			contextUser, _userGroupLocalService, _userLocalService);
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition) {
		_objectDefinition = objectDefinition;
	}

	private void _initThemeDisplay(long groupId) throws Exception {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return;
		}

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		themeDisplay = (ThemeDisplay)contextHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setCompany(contextCompany);
		themeDisplay.setScopeGroupId(groupId);
		themeDisplay.setUser(contextUser);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final DTOConverter<SharingEntry, Collaborator>
		_collaboratorDTOConverter;
	private final ConfigurationProvider _configurationProvider;
	private final DTOConverterRegistry _dtoConverterRegistry;
	private final GroupLocalService _groupLocalService;

	@Context
	private ObjectDefinition _objectDefinition;

	private final ObjectEntryLocalService _objectEntryLocalService;
	private final SharingEntryLocalService _sharingEntryLocalService;
	private final SharingEntryService _sharingEntryService;
	private final TicketLocalService _ticketLocalService;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserLocalService _userLocalService;

}