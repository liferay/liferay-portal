/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PermissionCheckFinderEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PermissionCheckFinderEntryLocalService
 * @generated
 */
public class PermissionCheckFinderEntryLocalServiceWrapper
	implements PermissionCheckFinderEntryLocalService,
			   ServiceWrapper<PermissionCheckFinderEntryLocalService> {

	public PermissionCheckFinderEntryLocalServiceWrapper() {
		this(null);
	}

	public PermissionCheckFinderEntryLocalServiceWrapper(
		PermissionCheckFinderEntryLocalService
			permissionCheckFinderEntryLocalService) {

		_permissionCheckFinderEntryLocalService =
			permissionCheckFinderEntryLocalService;
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry addPermissionCheckFinderEntry(
				long companyId, long groupId, int integer, String name,
				String type, long userId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _permissionCheckFinderEntryLocalService.
			addPermissionCheckFinderEntry(
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry addPermissionCheckFinderEntry(
			com.liferay.portal.tools.service.builder.test.model.
				PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return _permissionCheckFinderEntryLocalService.
			addPermissionCheckFinderEntry(permissionCheckFinderEntry);
	}

	/**
	 * Creates a new permission check finder entry with the primary key. Does not add the permission check finder entry to the database.
	 *
	 * @param permissionCheckFinderEntryId the primary key for the new permission check finder entry
	 * @return the new permission check finder entry
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry createPermissionCheckFinderEntry(
			long permissionCheckFinderEntryId) {

		return _permissionCheckFinderEntryLocalService.
			createPermissionCheckFinderEntry(permissionCheckFinderEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _permissionCheckFinderEntryLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry deletePermissionCheckFinderEntry(
				long permissionCheckFinderEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _permissionCheckFinderEntryLocalService.
			deletePermissionCheckFinderEntry(permissionCheckFinderEntryId);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry deletePermissionCheckFinderEntry(
			com.liferay.portal.tools.service.builder.test.model.
				PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return _permissionCheckFinderEntryLocalService.
			deletePermissionCheckFinderEntry(permissionCheckFinderEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _permissionCheckFinderEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _permissionCheckFinderEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _permissionCheckFinderEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _permissionCheckFinderEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _permissionCheckFinderEntryLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _permissionCheckFinderEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _permissionCheckFinderEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _permissionCheckFinderEntryLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _permissionCheckFinderEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry fetchPermissionCheckFinderEntry(
			long permissionCheckFinderEntryId) {

		return _permissionCheckFinderEntryLocalService.
			fetchPermissionCheckFinderEntry(permissionCheckFinderEntryId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			PermissionCheckFinderEntry> filterFindByGroupId(long groupId) {

		return _permissionCheckFinderEntryLocalService.filterFindByGroupId(
			groupId);
	}

	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			PermissionCheckFinderEntry> filterFindByGroupId(long[] groupIds) {

		return _permissionCheckFinderEntryLocalService.filterFindByGroupId(
			groupIds);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _permissionCheckFinderEntryLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _permissionCheckFinderEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _permissionCheckFinderEntryLocalService.
			getOSGiServiceIdentifier();
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
	@Override
	public java.util.List
		<com.liferay.portal.tools.service.builder.test.model.
			PermissionCheckFinderEntry> getPermissionCheckFinderEntries(
				int start, int end) {

		return _permissionCheckFinderEntryLocalService.
			getPermissionCheckFinderEntries(start, end);
	}

	/**
	 * Returns the number of permission check finder entries.
	 *
	 * @return the number of permission check finder entries
	 */
	@Override
	public int getPermissionCheckFinderEntriesCount() {
		return _permissionCheckFinderEntryLocalService.
			getPermissionCheckFinderEntriesCount();
	}

	/**
	 * Returns the permission check finder entry with the primary key.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry
	 * @throws PortalException if a permission check finder entry with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry getPermissionCheckFinderEntry(
				long permissionCheckFinderEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _permissionCheckFinderEntryLocalService.
			getPermissionCheckFinderEntry(permissionCheckFinderEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _permissionCheckFinderEntryLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.portal.tools.service.builder.test.model.
		PermissionCheckFinderEntry updatePermissionCheckFinderEntry(
			com.liferay.portal.tools.service.builder.test.model.
				PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return _permissionCheckFinderEntryLocalService.
			updatePermissionCheckFinderEntry(permissionCheckFinderEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _permissionCheckFinderEntryLocalService.getBasePersistence();
	}

	@Override
	public PermissionCheckFinderEntryLocalService getWrappedService() {
		return _permissionCheckFinderEntryLocalService;
	}

	@Override
	public void setWrappedService(
		PermissionCheckFinderEntryLocalService
			permissionCheckFinderEntryLocalService) {

		_permissionCheckFinderEntryLocalService =
			permissionCheckFinderEntryLocalService;
	}

	private PermissionCheckFinderEntryLocalService
		_permissionCheckFinderEntryLocalService;

}