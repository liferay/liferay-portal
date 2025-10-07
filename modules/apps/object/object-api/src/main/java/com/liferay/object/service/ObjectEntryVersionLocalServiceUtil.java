/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ObjectEntryVersion. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectEntryVersionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectEntryVersionLocalService
 * @generated
 */
public class ObjectEntryVersionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryVersionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectEntryVersion addObjectEntryVersion(
			com.liferay.object.model.ObjectEntry objectEntry)
		throws PortalException {

		return getService().addObjectEntryVersion(objectEntry);
	}

	/**
	 * Adds the object entry version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was added
	 */
	public static ObjectEntryVersion addObjectEntryVersion(
		ObjectEntryVersion objectEntryVersion) {

		return getService().addObjectEntryVersion(objectEntryVersion);
	}

	public static void checkObjectEntryVersions(long companyId)
		throws PortalException {

		getService().checkObjectEntryVersions(companyId);
	}

	/**
	 * Creates a new object entry version with the primary key. Does not add the object entry version to the database.
	 *
	 * @param objectEntryVersionId the primary key for the new object entry version
	 * @return the new object entry version
	 */
	public static ObjectEntryVersion createObjectEntryVersion(
		long objectEntryVersionId) {

		return getService().createObjectEntryVersion(objectEntryVersionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the object entry version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version that was removed
	 * @throws PortalException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryVersionId)
		throws PortalException {

		return getService().deleteObjectEntryVersion(objectEntryVersionId);
	}

	public static ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		return getService().deleteObjectEntryVersion(objectEntryId, version);
	}

	/**
	 * Deletes the object entry version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was removed
	 */
	public static ObjectEntryVersion deleteObjectEntryVersion(
		ObjectEntryVersion objectEntryVersion) {

		return getService().deleteObjectEntryVersion(objectEntryVersion);
	}

	public static void deleteObjectEntryVersionByObjectDefinitionId(
		Long objectDefinitionId) {

		getService().deleteObjectEntryVersionByObjectDefinitionId(
			objectDefinitionId);
	}

	public static void deleteObjectEntryVersions(long objectEntryId) {
		getService().deleteObjectEntryVersions(objectEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static ObjectEntryVersion expireObjectEntryVersion(
			long userId, com.liferay.object.model.ObjectEntry objectEntry,
			int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().expireObjectEntryVersion(
			userId, objectEntry, version, serviceContext);
	}

	public static ObjectEntryVersion expireObjectEntryVersion(
			long userId, ObjectEntryVersion objectEntryVersion)
		throws PortalException {

		return getService().expireObjectEntryVersion(
			userId, objectEntryVersion);
	}

	public static void expireObjectEntryVersions(
			long userId, com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		getService().expireObjectEntryVersions(
			userId, objectEntry, serviceContext);
	}

	public static ObjectEntryVersion fetchLatestApprovedObjectEntryVersion(
		long objectEntryId,
		OrderByComparator<ObjectEntryVersion> orderByComparator) {

		return getService().fetchLatestApprovedObjectEntryVersion(
			objectEntryId, orderByComparator);
	}

	public static ObjectEntryVersion fetchObjectEntryVersion(
		long objectEntryVersionId) {

		return getService().fetchObjectEntryVersion(objectEntryVersionId);
	}

	public static ObjectEntryVersion fetchObjectEntryVersion(
		long objectEntryId, int version) {

		return getService().fetchObjectEntryVersion(objectEntryId, version);
	}

	/**
	 * Returns the object entry version with the matching UUID and company.
	 *
	 * @param uuid the object entry version's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object entry version, or <code>null</code> if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion fetchObjectEntryVersionByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectEntryVersionByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object entry version with the primary key.
	 *
	 * @param objectEntryVersionId the primary key of the object entry version
	 * @return the object entry version
	 * @throws PortalException if a object entry version with the primary key could not be found
	 */
	public static ObjectEntryVersion getObjectEntryVersion(
			long objectEntryVersionId)
		throws PortalException {

		return getService().getObjectEntryVersion(objectEntryVersionId);
	}

	public static ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		return getService().getObjectEntryVersion(objectEntryId, version);
	}

	/**
	 * Returns the object entry version with the matching UUID and company.
	 *
	 * @param uuid the object entry version's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object entry version
	 * @throws PortalException if a matching object entry version could not be found
	 */
	public static ObjectEntryVersion getObjectEntryVersionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectEntryVersionByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object entry versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry versions
	 * @param end the upper bound of the range of object entry versions (not inclusive)
	 * @return the range of object entry versions
	 */
	public static List<ObjectEntryVersion> getObjectEntryVersions(
		int start, int end) {

		return getService().getObjectEntryVersions(start, end);
	}

	public static List<ObjectEntryVersion> getObjectEntryVersions(
		long objectEntryId) {

		return getService().getObjectEntryVersions(objectEntryId);
	}

	public static List<ObjectEntryVersion> getObjectEntryVersions(
		long objectEntryId, int start, int end) {

		return getService().getObjectEntryVersions(objectEntryId, start, end);
	}

	/**
	 * Returns the number of object entry versions.
	 *
	 * @return the number of object entry versions
	 */
	public static int getObjectEntryVersionsCount() {
		return getService().getObjectEntryVersionsCount();
	}

	public static int getObjectEntryVersionsCount(long objectEntryId) {
		return getService().getObjectEntryVersionsCount(objectEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean isLatestObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		return getService().isLatestObjectEntryVersion(objectEntryId, version);
	}

	public static ObjectEntryVersion updateLatestObjectEntryVersion(
			com.liferay.object.model.ObjectEntry objectEntry)
		throws PortalException {

		return getService().updateLatestObjectEntryVersion(objectEntry);
	}

	/**
	 * Updates the object entry version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryVersion the object entry version
	 * @return the object entry version that was updated
	 */
	public static ObjectEntryVersion updateObjectEntryVersion(
		ObjectEntryVersion objectEntryVersion) {

		return getService().updateObjectEntryVersion(objectEntryVersion);
	}

	public static ObjectEntryVersionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectEntryVersionLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectEntryVersionLocalServiceUtil.class,
			ObjectEntryVersionLocalService.class);

}