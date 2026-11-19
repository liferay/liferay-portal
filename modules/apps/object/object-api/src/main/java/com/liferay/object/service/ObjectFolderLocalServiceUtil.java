/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectFolder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ObjectFolder. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectFolderLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectFolderLocalService
 * @generated
 */
public class ObjectFolderLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectFolderLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the object folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolder the object folder
	 * @return the object folder that was added
	 */
	public static ObjectFolder addObjectFolder(ObjectFolder objectFolder) {
		return getService().addObjectFolder(objectFolder);
	}

	public static ObjectFolder addObjectFolder(
			String externalReferenceCode, long userId,
			Map<java.util.Locale, String> labelMap, String name)
		throws PortalException {

		return getService().addObjectFolder(
			externalReferenceCode, userId, labelMap, name);
	}

	/**
	 * Creates a new object folder with the primary key. Does not add the object folder to the database.
	 *
	 * @param objectFolderId the primary key for the new object folder
	 * @return the new object folder
	 */
	public static ObjectFolder createObjectFolder(long objectFolderId) {
		return getService().createObjectFolder(objectFolderId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyObjectFolders(long companyId)
		throws PortalException {

		getService().deleteCompanyObjectFolders(companyId);
	}

	/**
	 * Deletes the object folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderId the primary key of the object folder
	 * @return the object folder that was removed
	 * @throws PortalException if a object folder with the primary key could not be found
	 */
	public static ObjectFolder deleteObjectFolder(long objectFolderId)
		throws PortalException {

		return getService().deleteObjectFolder(objectFolderId);
	}

	/**
	 * Deletes the object folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolder the object folder
	 * @return the object folder that was removed
	 * @throws PortalException
	 */
	public static ObjectFolder deleteObjectFolder(ObjectFolder objectFolder)
		throws PortalException {

		return getService().deleteObjectFolder(objectFolder);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderModelImpl</code>.
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

	public static ObjectFolder fetchDefaultObjectFolder(long companyId) {
		return getService().fetchDefaultObjectFolder(companyId);
	}

	public static ObjectFolder fetchObjectFolder(long objectFolderId) {
		return getService().fetchObjectFolder(objectFolderId);
	}

	public static ObjectFolder fetchObjectFolder(long companyId, String name) {
		return getService().fetchObjectFolder(companyId, name);
	}

	public static ObjectFolder fetchObjectFolderByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchObjectFolderByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the object folder with the matching UUID and company.
	 *
	 * @param uuid the object folder's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder, or <code>null</code> if a matching object folder could not be found
	 */
	public static ObjectFolder fetchObjectFolderByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectFolderByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static ObjectFolder getDefaultObjectFolder(long companyId)
		throws PortalException {

		return getService().getDefaultObjectFolder(companyId);
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
	 * Returns the object folder with the primary key.
	 *
	 * @param objectFolderId the primary key of the object folder
	 * @return the object folder
	 * @throws PortalException if a object folder with the primary key could not be found
	 */
	public static ObjectFolder getObjectFolder(long objectFolderId)
		throws PortalException {

		return getService().getObjectFolder(objectFolderId);
	}

	public static ObjectFolder getObjectFolder(long companyId, String name)
		throws PortalException {

		return getService().getObjectFolder(companyId, name);
	}

	public static ObjectFolder getObjectFolderByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getObjectFolderByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the object folder with the matching UUID and company.
	 *
	 * @param uuid the object folder's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder
	 * @throws PortalException if a matching object folder could not be found
	 */
	public static ObjectFolder getObjectFolderByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectFolderByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object folders
	 * @param end the upper bound of the range of object folders (not inclusive)
	 * @return the range of object folders
	 */
	public static List<ObjectFolder> getObjectFolders(int start, int end) {
		return getService().getObjectFolders(start, end);
	}

	/**
	 * Returns the number of object folders.
	 *
	 * @return the number of object folders
	 */
	public static int getObjectFoldersCount() {
		return getService().getObjectFoldersCount();
	}

	public static ObjectFolder getOrAddDefaultObjectFolder(long companyId)
		throws PortalException {

		return getService().getOrAddDefaultObjectFolder(companyId);
	}

	public static ObjectFolder getOrAddEmptyObjectFolder(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyObjectFolder(
			externalReferenceCode, companyId, userId);
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

	/**
	 * Updates the object folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolder the object folder
	 * @return the object folder that was updated
	 */
	public static ObjectFolder updateObjectFolder(ObjectFolder objectFolder) {
		return getService().updateObjectFolder(objectFolder);
	}

	public static ObjectFolder updateObjectFolder(
			String externalReferenceCode, long objectFolderId,
			Map<java.util.Locale, String> labelMap)
		throws PortalException {

		return getService().updateObjectFolder(
			externalReferenceCode, objectFolderId, labelMap);
	}

	public static ObjectFolderLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectFolderLocalService> _serviceSnapshot =
		new Snapshot<>(
			ObjectFolderLocalServiceUtil.class, ObjectFolderLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:725352069