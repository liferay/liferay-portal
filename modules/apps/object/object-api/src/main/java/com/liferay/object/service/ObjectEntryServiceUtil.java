/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for ObjectEntry. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectEntryService
 * @generated
 */
public class ObjectEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectEntry addObjectEntry(
			long groupId, long objectDefinitionId, long objectEntryFolderId,
			String defaultLanguageId, Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addObjectEntry(
			groupId, objectDefinitionId, objectEntryFolderId, defaultLanguageId,
			values, serviceContext);
	}

	public static ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId,
			long objectEntryFolderId, Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId,
			objectEntryFolderId, values, serviceContext);
	}

	public static void checkModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws PortalException {

		getService().checkModelResourcePermission(
			objectDefinitionId, objectEntryId, actionId);
	}

	public static ObjectEntry deleteObjectEntry(long objectEntryId)
		throws PortalException {

		return getService().deleteObjectEntry(objectEntryId);
	}

	public static ObjectEntry deleteObjectEntry(
			String externalReferenceCode, long companyId, long groupId)
		throws PortalException {

		return getService().deleteObjectEntry(
			externalReferenceCode, companyId, groupId);
	}

	public static ObjectEntry expireObjectEntry(
			long userId, long objectEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().expireObjectEntry(
			userId, objectEntryId, serviceContext);
	}

	public static ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		return getService().fetchManyToOneObjectEntry(
			groupId, objectRelationshipId, primaryKey);
	}

	public static ObjectEntry fetchObjectEntry(long objectEntryId)
		throws PortalException {

		return getService().fetchObjectEntry(objectEntryId);
	}

	public static List<ObjectEntry> getManyToManyObjectEntries(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search, int start, int end)
		throws PortalException {

		return getService().getManyToManyObjectEntries(
			groupId, objectRelationshipId, primaryKey, related, reverse, search,
			start, end);
	}

	public static int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws PortalException {

		return getService().getManyToManyObjectEntriesCount(
			groupId, objectRelationshipId, primaryKey, related, reverse,
			search);
	}

	public static com.liferay.portal.kernel.security.permission.resource.
		ModelResourcePermission<ObjectEntry> getModelResourcePermission(
				long objectDefinitionId)
			throws PortalException {

		return getService().getModelResourcePermission(objectDefinitionId);
	}

	public static ObjectEntry getObjectEntry(long objectEntryId)
		throws PortalException {

		return getService().getObjectEntry(objectEntryId);
	}

	public static ObjectEntry getObjectEntry(
			String externalReferenceCode, long objectDefinitionId)
		throws PortalException {

		return getService().getObjectEntry(
			externalReferenceCode, objectDefinitionId);
	}

	public static ObjectEntry getObjectEntry(
			String externalReferenceCode, long companyId, long groupId)
		throws PortalException {

		return getService().getObjectEntry(
			externalReferenceCode, companyId, groupId);
	}

	public static List<ObjectEntry> getOneToManyObjectEntries(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, String search, int start, int end)
		throws PortalException {

		return getService().getOneToManyObjectEntries(
			groupId, objectRelationshipId, primaryKey, related, search, start,
			end);
	}

	public static int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, String search)
		throws PortalException {

		return getService().getOneToManyObjectEntriesCount(
			groupId, objectRelationshipId, primaryKey, related, search);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static boolean hasModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws PortalException {

		return getService().hasModelResourcePermission(
			objectDefinitionId, objectEntryId, actionId);
	}

	public static boolean hasModelResourcePermission(
			ObjectEntry objectEntry, String actionId)
		throws PortalException {

		return getService().hasModelResourcePermission(objectEntry, actionId);
	}

	public static boolean hasModelResourcePermission(
			com.liferay.portal.kernel.model.User user, long objectEntryId,
			String actionId)
		throws PortalException {

		return getService().hasModelResourcePermission(
			user, objectEntryId, actionId);
	}

	public static boolean hasPortletResourcePermission(
			long groupId, long objectDefinitionId, String actionId)
		throws PortalException {

		return getService().hasPortletResourcePermission(
			groupId, objectDefinitionId, actionId);
	}

	public static ObjectEntry partialUpdateObjectEntry(
			long objectEntryId, Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().partialUpdateObjectEntry(
			objectEntryId, values, serviceContext);
	}

	public static ObjectEntry updateObjectEntry(
			long objectEntryId, Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateObjectEntry(
			objectEntryId, values, serviceContext);
	}

	public static void validate(
			long groupId, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().validate(
			groupId, objectEntry, objectValidationRuleExternalReferenceCodes,
			serviceContext);
	}

	public static ObjectEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectEntryService> _serviceSnapshot =
		new Snapshot<>(ObjectEntryServiceUtil.class, ObjectEntryService.class);

}