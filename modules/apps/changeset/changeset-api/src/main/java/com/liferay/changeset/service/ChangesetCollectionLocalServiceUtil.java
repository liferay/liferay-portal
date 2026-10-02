/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service;

import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ChangesetCollection. This utility wraps
 * <code>com.liferay.changeset.service.impl.ChangesetCollectionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ChangesetCollectionLocalService
 * @generated
 */
public class ChangesetCollectionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.changeset.service.impl.ChangesetCollectionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the changeset collection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollection the changeset collection
	 * @return the changeset collection that was added
	 */
	public static ChangesetCollection addChangesetCollection(
		ChangesetCollection changesetCollection) {

		return getService().addChangesetCollection(changesetCollection);
	}

	public static ChangesetCollection addChangesetCollection(
			long userId, long groupId, String name, String description)
		throws PortalException {

		return getService().addChangesetCollection(
			userId, groupId, name, description);
	}

	/**
	 * Creates a new changeset collection with the primary key. Does not add the changeset collection to the database.
	 *
	 * @param changesetCollectionId the primary key for the new changeset collection
	 * @return the new changeset collection
	 */
	public static ChangesetCollection createChangesetCollection(
		long changesetCollectionId) {

		return getService().createChangesetCollection(changesetCollectionId);
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
	 * Deletes the changeset collection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollection the changeset collection
	 * @return the changeset collection that was removed
	 */
	public static ChangesetCollection deleteChangesetCollection(
		ChangesetCollection changesetCollection) {

		return getService().deleteChangesetCollection(changesetCollection);
	}

	/**
	 * Deletes the changeset collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollectionId the primary key of the changeset collection
	 * @return the changeset collection that was removed
	 * @throws PortalException if a changeset collection with the primary key could not be found
	 */
	public static ChangesetCollection deleteChangesetCollection(
			long changesetCollectionId)
		throws PortalException {

		return getService().deleteChangesetCollection(changesetCollectionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetCollectionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetCollectionModelImpl</code>.
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

	public static ChangesetCollection fetchChangesetCollection(
		long changesetCollectionId) {

		return getService().fetchChangesetCollection(changesetCollectionId);
	}

	public static ChangesetCollection fetchChangesetCollection(
		long groupId, String name) {

		return getService().fetchChangesetCollection(groupId, name);
	}

	public static ChangesetCollection fetchOrAddChangesetCollection(
			long groupId, String name)
		throws PortalException {

		return getService().fetchOrAddChangesetCollection(groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the changeset collection with the primary key.
	 *
	 * @param changesetCollectionId the primary key of the changeset collection
	 * @return the changeset collection
	 * @throws PortalException if a changeset collection with the primary key could not be found
	 */
	public static ChangesetCollection getChangesetCollection(
			long changesetCollectionId)
		throws PortalException {

		return getService().getChangesetCollection(changesetCollectionId);
	}

	/**
	 * Returns a range of all the changeset collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of changeset collections
	 * @param end the upper bound of the range of changeset collections (not inclusive)
	 * @return the range of changeset collections
	 */
	public static List<ChangesetCollection> getChangesetCollections(
		int start, int end) {

		return getService().getChangesetCollections(start, end);
	}

	/**
	 * Returns the number of changeset collections.
	 *
	 * @return the number of changeset collections
	 */
	public static int getChangesetCollectionsCount() {
		return getService().getChangesetCollectionsCount();
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
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the changeset collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetCollection the changeset collection
	 * @return the changeset collection that was updated
	 */
	public static ChangesetCollection updateChangesetCollection(
		ChangesetCollection changesetCollection) {

		return getService().updateChangesetCollection(changesetCollection);
	}

	public static ChangesetCollectionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ChangesetCollectionLocalService>
		_serviceSnapshot = new Snapshot<>(
			ChangesetCollectionLocalServiceUtil.class,
			ChangesetCollectionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1070033204