/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectFolderItem;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ObjectFolderItem. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectFolderItemLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectFolderItemLocalService
 * @generated
 */
public class ObjectFolderItemLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectFolderItemLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectFolderItem addObjectFolderItem(
			long userId, long objectDefinitionId, long objectFolderId,
			int positionX, int positionY)
		throws PortalException {

		return getService().addObjectFolderItem(
			userId, objectDefinitionId, objectFolderId, positionX, positionY);
	}

	/**
	 * Adds the object folder item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItem the object folder item
	 * @return the object folder item that was added
	 */
	public static ObjectFolderItem addObjectFolderItem(
		ObjectFolderItem objectFolderItem) {

		return getService().addObjectFolderItem(objectFolderItem);
	}

	/**
	 * Creates a new object folder item with the primary key. Does not add the object folder item to the database.
	 *
	 * @param objectFolderItemId the primary key for the new object folder item
	 * @return the new object folder item
	 */
	public static ObjectFolderItem createObjectFolderItem(
		long objectFolderItemId) {

		return getService().createObjectFolderItem(objectFolderItemId);
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
	 * Deletes the object folder item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItemId the primary key of the object folder item
	 * @return the object folder item that was removed
	 * @throws PortalException if a object folder item with the primary key could not be found
	 */
	public static ObjectFolderItem deleteObjectFolderItem(
			long objectFolderItemId)
		throws PortalException {

		return getService().deleteObjectFolderItem(objectFolderItemId);
	}

	public static ObjectFolderItem deleteObjectFolderItem(
			long objectDefinitionId, long objectFolderId)
		throws PortalException {

		return getService().deleteObjectFolderItem(
			objectDefinitionId, objectFolderId);
	}

	/**
	 * Deletes the object folder item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItem the object folder item
	 * @return the object folder item that was removed
	 * @throws PortalException
	 */
	public static ObjectFolderItem deleteObjectFolderItem(
			ObjectFolderItem objectFolderItem)
		throws PortalException {

		return getService().deleteObjectFolderItem(objectFolderItem);
	}

	public static void deleteObjectFolderItemByObjectDefinitionId(
		long objectDefinitionId) {

		getService().deleteObjectFolderItemByObjectDefinitionId(
			objectDefinitionId);
	}

	public static void deleteObjectFolderItemByObjectFolderId(
		long objectFolderId) {

		getService().deleteObjectFolderItemByObjectFolderId(objectFolderId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderItemModelImpl</code>.
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

	public static ObjectFolderItem fetchObjectFolderItem(
		long objectFolderItemId) {

		return getService().fetchObjectFolderItem(objectFolderItemId);
	}

	/**
	 * Returns the object folder item with the matching UUID and company.
	 *
	 * @param uuid the object folder item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder item, or <code>null</code> if a matching object folder item could not be found
	 */
	public static ObjectFolderItem fetchObjectFolderItemByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectFolderItemByUuidAndCompanyId(
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
	 * Returns the object folder item with the primary key.
	 *
	 * @param objectFolderItemId the primary key of the object folder item
	 * @return the object folder item
	 * @throws PortalException if a object folder item with the primary key could not be found
	 */
	public static ObjectFolderItem getObjectFolderItem(long objectFolderItemId)
		throws PortalException {

		return getService().getObjectFolderItem(objectFolderItemId);
	}

	/**
	 * Returns the object folder item with the matching UUID and company.
	 *
	 * @param uuid the object folder item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder item
	 * @throws PortalException if a matching object folder item could not be found
	 */
	public static ObjectFolderItem getObjectFolderItemByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectFolderItemByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object folder items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object folder items
	 * @param end the upper bound of the range of object folder items (not inclusive)
	 * @return the range of object folder items
	 */
	public static List<ObjectFolderItem> getObjectFolderItems(
		int start, int end) {

		return getService().getObjectFolderItems(start, end);
	}

	public static List<ObjectFolderItem> getObjectFolderItemsByObjectFolderId(
		long objectFolderId) {

		return getService().getObjectFolderItemsByObjectFolderId(
			objectFolderId);
	}

	/**
	 * Returns the number of object folder items.
	 *
	 * @return the number of object folder items
	 */
	public static int getObjectFolderItemsCount() {
		return getService().getObjectFolderItemsCount();
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

	public static ObjectFolderItem updateObjectFolderItem(
			long objectDefinitionId, long objectFolderId, int positionX,
			int positionY)
		throws PortalException {

		return getService().updateObjectFolderItem(
			objectDefinitionId, objectFolderId, positionX, positionY);
	}

	/**
	 * Updates the object folder item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItem the object folder item
	 * @return the object folder item that was updated
	 */
	public static ObjectFolderItem updateObjectFolderItem(
		ObjectFolderItem objectFolderItem) {

		return getService().updateObjectFolderItem(objectFolderItem);
	}

	public static void updateObjectFolderObjectFolderItem(
			long objectDefinitionId, long newObjectFolderId,
			long oldObjectFolderId)
		throws PortalException {

		getService().updateObjectFolderObjectFolderItem(
			objectDefinitionId, newObjectFolderId, oldObjectFolderId);
	}

	public static ObjectFolderItemLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectFolderItemLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectFolderItemLocalServiceUtil.class,
			ObjectFolderItemLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1312927508