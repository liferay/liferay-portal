/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FragmentCollection. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentCollectionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCollectionLocalService
 * @generated
 */
public class FragmentCollectionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentCollectionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the fragment collection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollection the fragment collection
	 * @return the fragment collection that was added
	 */
	public static FragmentCollection addFragmentCollection(
		FragmentCollection fragmentCollection) {

		return getService().addFragmentCollection(fragmentCollection);
	}

	public static FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentCollection(
			externalReferenceCode, userId, groupId, name, description,
			serviceContext);
	}

	public static FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentCollection(
			externalReferenceCode, userId, groupId, fragmentCollectionKey, name,
			description, marketplace, serviceContext);
	}

	/**
	 * Creates a new fragment collection with the primary key. Does not add the fragment collection to the database.
	 *
	 * @param fragmentCollectionId the primary key for the new fragment collection
	 * @return the new fragment collection
	 */
	public static FragmentCollection createFragmentCollection(
		long fragmentCollectionId) {

		return getService().createFragmentCollection(fragmentCollectionId);
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
	 * Deletes the fragment collection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollection the fragment collection
	 * @return the fragment collection that was removed
	 * @throws PortalException
	 */
	public static FragmentCollection deleteFragmentCollection(
			FragmentCollection fragmentCollection)
		throws PortalException {

		return getService().deleteFragmentCollection(fragmentCollection);
	}

	/**
	 * Deletes the fragment collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection that was removed
	 * @throws PortalException if a fragment collection with the primary key could not be found
	 */
	public static FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws PortalException {

		return getService().deleteFragmentCollection(fragmentCollectionId);
	}

	public static FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFragmentCollection(
			externalReferenceCode, groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCollectionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCollectionModelImpl</code>.
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

	public static FragmentCollection fetchFragmentCollection(
		long fragmentCollectionId) {

		return getService().fetchFragmentCollection(fragmentCollectionId);
	}

	public static FragmentCollection fetchFragmentCollection(
		long groupId, String fragmentCollectionKey) {

		return getService().fetchFragmentCollection(
			groupId, fragmentCollectionKey);
	}

	public static FragmentCollection
		fetchFragmentCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchFragmentCollectionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment collection matching the UUID and group.
	 *
	 * @param uuid the fragment collection's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	public static FragmentCollection fetchFragmentCollectionByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchFragmentCollectionByUuidAndGroupId(
			uuid, groupId);
	}

	public static String generateFragmentCollectionKey(
		long groupId, String name) {

		return getService().generateFragmentCollectionKey(groupId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds) {

		return getService().getExportableFragmentCollections(
			fragmentCollectionIds);
	}

	public static List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, int start, int end,
			OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getExportableFragmentCollectionsByGroupId(
			groupIds, start, end, orderByComparator);
	}

	public static List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, String name, int start, int end,
			OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getExportableFragmentCollectionsByGroupId(
			groupIds, name, start, end, orderByComparator);
	}

	public static int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds) {

		return getService().getExportableFragmentCollectionsCount(
			fragmentCollectionIds);
	}

	public static int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds) {

		return getService().getExportableFragmentCollectionsCountByGroupId(
			groupIds);
	}

	public static int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name) {

		return getService().getExportableFragmentCollectionsCountByGroupId(
			groupIds, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns the fragment collection with the primary key.
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection
	 * @throws PortalException if a fragment collection with the primary key could not be found
	 */
	public static FragmentCollection getFragmentCollection(
			long fragmentCollectionId)
		throws PortalException {

		return getService().getFragmentCollection(fragmentCollectionId);
	}

	public static FragmentCollection
			getFragmentCollectionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentCollectionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment collection matching the UUID and group.
	 *
	 * @param uuid the fragment collection's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment collection
	 * @throws PortalException if a matching fragment collection could not be found
	 */
	public static FragmentCollection getFragmentCollectionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getFragmentCollectionByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the fragment collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @return the range of fragment collections
	 */
	public static List<FragmentCollection> getFragmentCollections(
		int start, int end) {

		return getService().getFragmentCollections(start, end);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId) {

		return getService().getFragmentCollections(groupId);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem) {

		return getService().getFragmentCollections(groupId, includeSystem);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, includeSystem, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end) {

		return getService().getFragmentCollections(groupId, start, end);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, name, includeSystem, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, name, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds) {

		return getService().getFragmentCollections(groupIds);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, marketplace, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, name, marketplace, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, name, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment collections
	 * @param companyId the primary key of the company
	 * @return the matching fragment collections, or an empty list if no matches were found
	 */
	public static List<FragmentCollection>
		getFragmentCollectionsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getFragmentCollectionsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of fragment collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment collections
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of fragment collections
	 * @param end the upper bound of the range of fragment collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching fragment collections, or an empty list if no matches were found
	 */
	public static List<FragmentCollection>
		getFragmentCollectionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollectionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of fragment collections.
	 *
	 * @return the number of fragment collections
	 */
	public static int getFragmentCollectionsCount() {
		return getService().getFragmentCollectionsCount();
	}

	public static int getFragmentCollectionsCount(long groupId) {
		return getService().getFragmentCollectionsCount(groupId);
	}

	public static int getFragmentCollectionsCount(
		long groupId, boolean includeSystem) {

		return getService().getFragmentCollectionsCount(groupId, includeSystem);
	}

	public static int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem) {

		return getService().getFragmentCollectionsCount(
			groupId, name, includeSystem);
	}

	public static int getFragmentCollectionsCount(long[] groupIds) {
		return getService().getFragmentCollectionsCount(groupIds);
	}

	public static int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace) {

		return getService().getFragmentCollectionsCount(groupIds, marketplace);
	}

	public static int getFragmentCollectionsCount(
		long[] groupIds, String name) {

		return getService().getFragmentCollectionsCount(groupIds, name);
	}

	public static int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace) {

		return getService().getFragmentCollectionsCount(
			groupIds, name, marketplace);
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

	public static String getUniqueFragmentCollectionName(
		long groupId, String name) {

		return getService().getUniqueFragmentCollectionName(groupId, name);
	}

	/**
	 * Updates the fragment collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCollection the fragment collection
	 * @return the fragment collection that was updated
	 */
	public static FragmentCollection updateFragmentCollection(
		FragmentCollection fragmentCollection) {

		return getService().updateFragmentCollection(fragmentCollection);
	}

	public static FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws PortalException {

		return getService().updateFragmentCollection(
			fragmentCollectionId, name, description);
	}

	public static FragmentCollectionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentCollectionLocalService>
		_serviceSnapshot = new Snapshot<>(
			FragmentCollectionLocalServiceUtil.class,
			FragmentCollectionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:480371942