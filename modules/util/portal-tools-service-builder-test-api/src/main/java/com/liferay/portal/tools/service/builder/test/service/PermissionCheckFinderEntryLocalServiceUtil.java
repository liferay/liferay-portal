/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PermissionCheckFinderEntry. This utility wraps
 * <code>com.liferay.portal.tools.service.builder.test.service.impl.PermissionCheckFinderEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PermissionCheckFinderEntryLocalService
 * @generated
 */
public class PermissionCheckFinderEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.tools.service.builder.test.service.impl.PermissionCheckFinderEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static PermissionCheckFinderEntry addPermissionCheckFinderEntry(
			long companyId, long groupId, int integer, String name, String type,
			long userId)
		throws PortalException {

		return getService().addPermissionCheckFinderEntry(
			companyId, groupId, integer, name, type, userId);
	}

	/**
	 * Adds the permission check finder entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PermissionCheckFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param permissionCheckFinderEntry the permission check finder entry
	 * @return the permission check finder entry that was added
	 */
	public static PermissionCheckFinderEntry addPermissionCheckFinderEntry(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return getService().addPermissionCheckFinderEntry(
			permissionCheckFinderEntry);
	}

	/**
	 * Creates a new permission check finder entry with the primary key. Does not add the permission check finder entry to the database.
	 *
	 * @param permissionCheckFinderEntryId the primary key for the new permission check finder entry
	 * @return the new permission check finder entry
	 */
	public static PermissionCheckFinderEntry createPermissionCheckFinderEntry(
		long permissionCheckFinderEntryId) {

		return getService().createPermissionCheckFinderEntry(
			permissionCheckFinderEntryId);
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
	 * Deletes the permission check finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PermissionCheckFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry that was removed
	 * @throws PortalException if a permission check finder entry with the primary key could not be found
	 */
	public static PermissionCheckFinderEntry deletePermissionCheckFinderEntry(
			long permissionCheckFinderEntryId)
		throws PortalException {

		return getService().deletePermissionCheckFinderEntry(
			permissionCheckFinderEntryId);
	}

	/**
	 * Deletes the permission check finder entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PermissionCheckFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param permissionCheckFinderEntry the permission check finder entry
	 * @return the permission check finder entry that was removed
	 */
	public static PermissionCheckFinderEntry deletePermissionCheckFinderEntry(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return getService().deletePermissionCheckFinderEntry(
			permissionCheckFinderEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.PermissionCheckFinderEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.PermissionCheckFinderEntryModelImpl</code>.
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

	public static PermissionCheckFinderEntry fetchPermissionCheckFinderEntry(
		long permissionCheckFinderEntryId) {

		return getService().fetchPermissionCheckFinderEntry(
			permissionCheckFinderEntryId);
	}

	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId) {

		return getService().filterFindByGroupId(groupId);
	}

	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds) {

		return getService().filterFindByGroupId(groupIds);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * Returns a range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry>
		getPermissionCheckFinderEntries(int start, int end) {

		return getService().getPermissionCheckFinderEntries(start, end);
	}

	/**
	 * Returns the number of permission check finder entries.
	 *
	 * @return the number of permission check finder entries
	 */
	public static int getPermissionCheckFinderEntriesCount() {
		return getService().getPermissionCheckFinderEntriesCount();
	}

	/**
	 * Returns the permission check finder entry with the primary key.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry
	 * @throws PortalException if a permission check finder entry with the primary key could not be found
	 */
	public static PermissionCheckFinderEntry getPermissionCheckFinderEntry(
			long permissionCheckFinderEntryId)
		throws PortalException {

		return getService().getPermissionCheckFinderEntry(
			permissionCheckFinderEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the permission check finder entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PermissionCheckFinderEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param permissionCheckFinderEntry the permission check finder entry
	 * @return the permission check finder entry that was updated
	 */
	public static PermissionCheckFinderEntry updatePermissionCheckFinderEntry(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return getService().updatePermissionCheckFinderEntry(
			permissionCheckFinderEntry);
	}

	public static PermissionCheckFinderEntryLocalService getService() {
		return _service;
	}

	public static void setService(
		PermissionCheckFinderEntryLocalService service) {

		_service = service;
	}

	private static volatile PermissionCheckFinderEntryLocalService _service;

}