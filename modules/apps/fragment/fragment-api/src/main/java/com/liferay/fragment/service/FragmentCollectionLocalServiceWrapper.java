/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link FragmentCollectionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCollectionLocalService
 * @generated
 */
public class FragmentCollectionLocalServiceWrapper
	implements FragmentCollectionLocalService,
			   ServiceWrapper<FragmentCollectionLocalService> {

	public FragmentCollectionLocalServiceWrapper() {
		this(null);
	}

	public FragmentCollectionLocalServiceWrapper(
		FragmentCollectionLocalService fragmentCollectionLocalService) {

		_fragmentCollectionLocalService = fragmentCollectionLocalService;
	}

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
	@Override
	public FragmentCollection addFragmentCollection(
		FragmentCollection fragmentCollection) {

		return _fragmentCollectionLocalService.addFragmentCollection(
			fragmentCollection);
	}

	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.addFragmentCollection(
			externalReferenceCode, userId, groupId, name, description,
			serviceContext);
	}

	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long userId, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.addFragmentCollection(
			externalReferenceCode, userId, groupId, fragmentCollectionKey, name,
			description, marketplace, serviceContext);
	}

	/**
	 * Creates a new fragment collection with the primary key. Does not add the fragment collection to the database.
	 *
	 * @param fragmentCollectionId the primary key for the new fragment collection
	 * @return the new fragment collection
	 */
	@Override
	public FragmentCollection createFragmentCollection(
		long fragmentCollectionId) {

		return _fragmentCollectionLocalService.createFragmentCollection(
			fragmentCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public FragmentCollection deleteFragmentCollection(
			FragmentCollection fragmentCollection)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.deleteFragmentCollection(
			fragmentCollection);
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
	@Override
	public FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.deleteFragmentCollection(
			fragmentCollectionId);
	}

	@Override
	public FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.deleteFragmentCollection(
			externalReferenceCode, groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _fragmentCollectionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _fragmentCollectionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _fragmentCollectionLocalService.dynamicQuery();
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

		return _fragmentCollectionLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _fragmentCollectionLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _fragmentCollectionLocalService.dynamicQuery(
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

		return _fragmentCollectionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _fragmentCollectionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public FragmentCollection fetchFragmentCollection(
		long fragmentCollectionId) {

		return _fragmentCollectionLocalService.fetchFragmentCollection(
			fragmentCollectionId);
	}

	@Override
	public FragmentCollection fetchFragmentCollection(
		long groupId, String fragmentCollectionKey) {

		return _fragmentCollectionLocalService.fetchFragmentCollection(
			groupId, fragmentCollectionKey);
	}

	@Override
	public FragmentCollection fetchFragmentCollectionByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _fragmentCollectionLocalService.
			fetchFragmentCollectionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment collection matching the UUID and group.
	 *
	 * @param uuid the fragment collection's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment collection, or <code>null</code> if a matching fragment collection could not be found
	 */
	@Override
	public FragmentCollection fetchFragmentCollectionByUuidAndGroupId(
		String uuid, long groupId) {

		return _fragmentCollectionLocalService.
			fetchFragmentCollectionByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public String generateFragmentCollectionKey(long groupId, String name) {
		return _fragmentCollectionLocalService.generateFragmentCollectionKey(
			groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _fragmentCollectionLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds) {

		return _fragmentCollectionLocalService.getExportableFragmentCollections(
			fragmentCollectionIds);
	}

	@Override
	public java.util.List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
				orderByComparator) {

		return _fragmentCollectionLocalService.
			getExportableFragmentCollectionsByGroupId(
				groupIds, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
				orderByComparator) {

		return _fragmentCollectionLocalService.
			getExportableFragmentCollectionsByGroupId(
				groupIds, name, start, end, orderByComparator);
	}

	@Override
	public int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds) {

		return _fragmentCollectionLocalService.
			getExportableFragmentCollectionsCount(fragmentCollectionIds);
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(long[] groupIds) {
		return _fragmentCollectionLocalService.
			getExportableFragmentCollectionsCountByGroupId(groupIds);
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name) {

		return _fragmentCollectionLocalService.
			getExportableFragmentCollectionsCountByGroupId(groupIds, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _fragmentCollectionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	/**
	 * Returns the fragment collection with the primary key.
	 *
	 * @param fragmentCollectionId the primary key of the fragment collection
	 * @return the fragment collection
	 * @throws PortalException if a fragment collection with the primary key could not be found
	 */
	@Override
	public FragmentCollection getFragmentCollection(long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.getFragmentCollection(
			fragmentCollectionId);
	}

	@Override
	public FragmentCollection getFragmentCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.
			getFragmentCollectionByExternalReferenceCode(
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
	@Override
	public FragmentCollection getFragmentCollectionByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.
			getFragmentCollectionByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		int start, int end) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			start, end);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId) {

		return _fragmentCollectionLocalService.getFragmentCollections(groupId);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupId, includeSystem);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupId, includeSystem, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupId, start, end);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupId, name, includeSystem, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds) {

		return _fragmentCollectionLocalService.getFragmentCollections(groupIds);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupIds, marketplace, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupIds, name, marketplace, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionLocalService.getFragmentCollections(
			groupIds, name, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment collections matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment collections
	 * @param companyId the primary key of the company
	 * @return the matching fragment collections, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<FragmentCollection>
		getFragmentCollectionsByUuidAndCompanyId(String uuid, long companyId) {

		return _fragmentCollectionLocalService.
			getFragmentCollectionsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<FragmentCollection>
		getFragmentCollectionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
				orderByComparator) {

		return _fragmentCollectionLocalService.
			getFragmentCollectionsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of fragment collections.
	 *
	 * @return the number of fragment collections
	 */
	@Override
	public int getFragmentCollectionsCount() {
		return _fragmentCollectionLocalService.getFragmentCollectionsCount();
	}

	@Override
	public int getFragmentCollectionsCount(long groupId) {
		return _fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupId);
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, boolean includeSystem) {

		return _fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupId, includeSystem);
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem) {

		return _fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupId, name, includeSystem);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds) {
		return _fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace) {

		return _fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds, marketplace);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds, String name) {
		return _fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds, name);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace) {

		return _fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds, name, marketplace);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _fragmentCollectionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _fragmentCollectionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public String getUniqueFragmentCollectionName(long groupId, String name) {
		return _fragmentCollectionLocalService.getUniqueFragmentCollectionName(
			groupId, name);
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
	@Override
	public FragmentCollection updateFragmentCollection(
		FragmentCollection fragmentCollection) {

		return _fragmentCollectionLocalService.updateFragmentCollection(
			fragmentCollection);
	}

	@Override
	public FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionLocalService.updateFragmentCollection(
			fragmentCollectionId, name, description);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _fragmentCollectionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<FragmentCollection> getCTPersistence() {
		return _fragmentCollectionLocalService.getCTPersistence();
	}

	@Override
	public Class<FragmentCollection> getModelClass() {
		return _fragmentCollectionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FragmentCollection>, R, E>
				updateUnsafeFunction)
		throws E {

		return _fragmentCollectionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public FragmentCollectionLocalService getWrappedService() {
		return _fragmentCollectionLocalService;
	}

	@Override
	public void setWrappedService(
		FragmentCollectionLocalService fragmentCollectionLocalService) {

		_fragmentCollectionLocalService = fragmentCollectionLocalService;
	}

	private FragmentCollectionLocalService _fragmentCollectionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1260433563