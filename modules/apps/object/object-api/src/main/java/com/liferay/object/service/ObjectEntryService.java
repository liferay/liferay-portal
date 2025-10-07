/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for ObjectEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectEntryServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectEntryServiceUtil} if injection and service tracking are not available.
	 */
	public ObjectEntry addObjectEntry(
			long groupId, long objectDefinitionId, long objectEntryFolderId,
			String defaultLanguageId, Map<String, Serializable> values,
			ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId,
			long objectEntryFolderId, Map<String, Serializable> values,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws PortalException;

	public ObjectEntry deleteObjectEntry(long objectEntryId)
		throws PortalException;

	public ObjectEntry expireObjectEntry(
			long objectEntryId, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchObjectEntry(long objectEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getManyToManyObjectEntries(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ModelResourcePermission<ObjectEntry> getModelResourcePermission(
			long objectDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry getObjectEntry(long objectEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry getObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getOneToManyObjectEntries(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, boolean related,
			String search, int start, int end, Sort[] sorts)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, Predicate predicate,
			long primaryKey, boolean related, String search)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry getOrAddEmptyObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasModelResourcePermission(
			ObjectEntry objectEntry, String actionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasModelResourcePermission(
			User user, long objectEntryId, String actionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPortletResourcePermission(
			long groupId, long objectDefinitionId, String actionId)
		throws PortalException;

	public ObjectEntry moveObjectEntryToTrash(
			ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntry partialUpdateObjectEntry(
			long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntry restoreObjectEntryFromTrash(
			ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException;

	public void subscribeObjectEntry(long groupId, long objectEntryId)
		throws PortalException;

	public void unsubscribeObjectEntry(long objectEntryId)
		throws PortalException;

	public ObjectEntry updateObjectEntry(
			long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException;

	public void validate(
			long groupId, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			ServiceContext serviceContext)
		throws PortalException;

}