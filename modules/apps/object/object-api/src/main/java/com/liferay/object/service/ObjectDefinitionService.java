/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for ObjectDefinition. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectDefinitionServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectDefinitionService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectDefinitionServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object definition remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectDefinitionServiceUtil} if injection and service tracking are not available.
	 */
	public ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, String className, boolean enableComments,
			boolean enableFormContainer, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			Map<Locale, String> labelMap, String name, String panelAppOrder,
			String panelCategoryKey, Map<Locale, String> pluralLabelMap,
			boolean portlet, String scope, String storageType,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<ObjectField> objectFields,
			List<WorkflowDefinitionLink> workflowDefinitionLinks)
		throws PortalException;

	public ObjectDefinition addObjectDefinition(
			String externalReferenceCode, long objectFolderId,
			boolean modifiable, String scope, boolean system)
		throws PortalException;

	public ObjectDefinition addSystemObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			String className, boolean enableComments,
			boolean enableFormContainer, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			Map<Locale, String> labelMap, String name, String panelAppOrder,
			String panelCategoryKey, Map<Locale, String> pluralLabelMap,
			boolean portlet, String scope,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<ObjectField> objectFields,
			List<WorkflowDefinitionLink> workflowDefinitionLinks)
		throws PortalException;

	public ObjectDefinition deleteObjectDefinition(long objectDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition fetchObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getCMSObjectDefinitions(
		long companyId, String[] objectFolderExternalReferenceCodes);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition getObjectDefinition(long objectDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectDefinition getObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectDefinitions(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectDefinition> getObjectDefinitions(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectDefinitionsCount() throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectDefinitionsCount(long companyId) throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public ObjectDefinition publishCustomObjectDefinition(
			long objectDefinitionId)
		throws PortalException;

	public ObjectDefinition publishSystemObjectDefinition(
			long objectDefinitionId)
		throws PortalException;

	public ObjectDefinition updateCustomObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long accountEntryRestrictedObjectFieldId,
			long descriptionObjectFieldId, long objectFolderId,
			long titleObjectFieldId, boolean accountEntryRestricted,
			boolean active, String className, boolean enableCategorization,
			boolean enableComments, boolean enableFormContainer,
			boolean enableFriendlyURLCustomization, boolean enableIndexSearch,
			boolean enableLocalization, boolean enableObjectEntryDraft,
			boolean enableObjectEntryHistory, boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			Map<Locale, String> labelMap, String name, String panelAppOrder,
			String panelCategoryKey, boolean portlet,
			Map<Locale, String> pluralLabelMap, String scope, int status,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<WorkflowDefinitionLink> workflowDefinitionLinks)
		throws PortalException;

	public ObjectDefinition updateExternalReferenceCode(
			long objectDefinitionId, String externalReferenceCode)
		throws PortalException;

	public ObjectDefinition updateSystemObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long objectFolderId, long titleObjectFieldId,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<WorkflowDefinitionLink> workflowDefinitionLinks)
		throws PortalException;

	public ObjectDefinition updateTitleObjectFieldId(
			long objectDefinitionId, long titleObjectFieldId)
		throws PortalException;

}