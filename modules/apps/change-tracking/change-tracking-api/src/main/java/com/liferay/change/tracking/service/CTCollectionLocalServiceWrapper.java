/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTCollectionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTCollectionLocalService
 * @generated
 */
public class CTCollectionLocalServiceWrapper
	implements CTCollectionLocalService,
			   ServiceWrapper<CTCollectionLocalService> {

	public CTCollectionLocalServiceWrapper() {
		this(null);
	}

	public CTCollectionLocalServiceWrapper(
		CTCollectionLocalService ctCollectionLocalService) {

		_ctCollectionLocalService = ctCollectionLocalService;
	}

	/**
	 * Adds the ct collection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollection the ct collection
	 * @return the ct collection that was added
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection addCTCollection(
		com.liferay.change.tracking.model.CTCollection ctCollection) {

		return _ctCollectionLocalService.addCTCollection(ctCollection);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollection addCTCollection(
			String externalReferenceCode, long companyId, long userId,
			long ctRemoteId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.addCTCollection(
			externalReferenceCode, companyId, userId, ctRemoteId, name,
			description);
	}

	@Override
	public java.util.Map
		<Long,
		 java.util.List<com.liferay.change.tracking.conflict.ConflictInfo>>
				checkConflicts(
					com.liferay.change.tracking.model.CTCollection ctCollection)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.checkConflicts(ctCollection);
	}

	@Override
	public java.util.Map
		<Long,
		 java.util.List<com.liferay.change.tracking.conflict.ConflictInfo>>
				checkConflicts(
					long companyId,
					java.util.List<com.liferay.change.tracking.model.CTEntry>
						ctEntries,
					long fromCTCollectionId, String fromCTCollectionName,
					long toCTCollectionId, String toCTCollectionName)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.checkConflicts(
			companyId, ctEntries, fromCTCollectionId, fromCTCollectionName,
			toCTCollectionId, toCTCollectionName);
	}

	@Override
	public java.util.Map
		<Long,
		 java.util.List<com.liferay.change.tracking.conflict.ConflictInfo>>
				checkConflicts(
					long companyId, long[] ctEntryIds, long fromCTCollectionId,
					String fromCTCollectionName, long toCTCollectionId,
					String toCTCollectionName)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.checkConflicts(
			companyId, ctEntryIds, fromCTCollectionId, fromCTCollectionName,
			toCTCollectionId, toCTCollectionName);
	}

	/**
	 * Creates a new ct collection with the primary key. Does not add the ct collection to the database.
	 *
	 * @param ctCollectionId the primary key for the new ct collection
	 * @return the new ct collection
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection createCTCollection(
		long ctCollectionId) {

		return _ctCollectionLocalService.createCTCollection(ctCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCompanyCTCollections(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ctCollectionLocalService.deleteCompanyCTCollections(companyId);
	}

	@Override
	public void deleteCTAutoResolutionInfo(long ctAutoResolutionInfoId) {
		_ctCollectionLocalService.deleteCTAutoResolutionInfo(
			ctAutoResolutionInfoId);
	}

	/**
	 * Deletes the ct collection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollection the ct collection
	 * @return the ct collection that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection deleteCTCollection(
			com.liferay.change.tracking.model.CTCollection ctCollection)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.deleteCTCollection(ctCollection);
	}

	/**
	 * Deletes the ct collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionId the primary key of the ct collection
	 * @return the ct collection that was removed
	 * @throws PortalException if a ct collection with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection deleteCTCollection(
			long ctCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.deleteCTCollection(ctCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void discardCTEntry(
			long ctCollectionId,
			java.util.List<com.liferay.change.tracking.model.CTEntry> ctEntries,
			boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ctCollectionLocalService.discardCTEntry(
			ctCollectionId, ctEntries, force);
	}

	@Override
	public void discardCTEntry(
			long ctCollectionId, long modelClassNameId, long modelClassPK,
			boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ctCollectionLocalService.discardCTEntry(
			ctCollectionId, modelClassNameId, modelClassPK, force);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctCollectionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctCollectionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctCollectionLocalService.dynamicQuery();
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

		return _ctCollectionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionModelImpl</code>.
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

		return _ctCollectionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionModelImpl</code>.
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

		return _ctCollectionLocalService.dynamicQuery(
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

		return _ctCollectionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctCollectionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollection fetchCTCollection(
		long ctCollectionId) {

		return _ctCollectionLocalService.fetchCTCollection(ctCollectionId);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollection
		fetchCTCollectionByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _ctCollectionLocalService.
			fetchCTCollectionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct collection with the matching UUID and company.
	 *
	 * @param uuid the ct collection's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection
		fetchCTCollectionByUuidAndCompanyId(String uuid, long companyId) {

		return _ctCollectionLocalService.fetchCTCollectionByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctCollectionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ct collection with the primary key.
	 *
	 * @param ctCollectionId the primary key of the ct collection
	 * @return the ct collection
	 * @throws PortalException if a ct collection with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection getCTCollection(
			long ctCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getCTCollection(ctCollectionId);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollection
			getCTCollectionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getCTCollectionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct collection with the matching UUID and company.
	 *
	 * @param uuid the ct collection's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct collection
	 * @throws PortalException if a matching ct collection could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection
			getCTCollectionByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getCTCollectionByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the ct collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of ct collections
	 */
	@Override
	public java.util.List<com.liferay.change.tracking.model.CTCollection>
		getCTCollections(int start, int end) {

		return _ctCollectionLocalService.getCTCollections(start, end);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTCollection>
		getCTCollections(
			long companyId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.change.tracking.model.CTCollection>
					orderByComparator) {

		return _ctCollectionLocalService.getCTCollections(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTCollection>
		getCTCollections(
			long companyId, int[] statuses, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.change.tracking.model.CTCollection>
					orderByComparator) {

		return _ctCollectionLocalService.getCTCollections(
			companyId, statuses, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTCollection>
		getCTCollections(
			long companyId, long userId, int[] statuses, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.change.tracking.model.CTCollection>
					orderByComparator) {

		return _ctCollectionLocalService.getCTCollections(
			companyId, userId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ct collections.
	 *
	 * @return the number of ct collections
	 */
	@Override
	public int getCTCollectionsCount() {
		return _ctCollectionLocalService.getCTCollectionsCount();
	}

	@Override
	public java.util.List
		<com.liferay.change.tracking.mapping.CTMappingTableInfo>
			getCTMappingTableInfos(long ctCollectionId) {

		return _ctCollectionLocalService.getCTMappingTableInfos(ctCollectionId);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTCollection>
			getExclusivePublishedCTCollections(
				long modelClassNameId, long modelClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getExclusivePublishedCTCollections(
			modelClassNameId, modelClassPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _ctCollectionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctCollectionLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctCollectionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTEntry>
			getRelatedCTEntries(long ctCollectionId, long[] ctEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getRelatedCTEntries(
			ctCollectionId, ctEntryIds);
	}

	@Override
	public java.util.Map
		<Long, java.util.List<com.liferay.change.tracking.model.CTEntry>>
				getRelatedCTEntriesMap(
					long ctCollectionId,
					java.util.List<com.liferay.change.tracking.model.CTEntry>
						ctEntries)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getRelatedCTEntriesMap(
			ctCollectionId, ctEntries);
	}

	@Override
	public java.util.Map
		<Long, java.util.List<com.liferay.change.tracking.model.CTEntry>>
				getRelatedCTEntriesMap(
					long ctCollectionId, long modelClassNameId,
					long modelClassPK)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getRelatedCTEntriesMap(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	@Override
	public java.util.Map
		<Long, java.util.List<com.liferay.change.tracking.model.CTEntry>>
				getRelatedCTEntriesMap(long ctCollectionId, long[] ctEntryIds)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.getRelatedCTEntriesMap(
			ctCollectionId, ctEntryIds);
	}

	@Override
	public boolean hasUnapprovedChanges(long ctCollectionId)
		throws java.sql.SQLException {

		return _ctCollectionLocalService.hasUnapprovedChanges(ctCollectionId);
	}

	@Override
	public boolean isCTEntryEnclosed(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		return _ctCollectionLocalService.isCTEntryEnclosed(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	@Override
	public void moveCTEntries(
			long fromCTCollectionId, long toCTCollectionId,
			java.util.List<com.liferay.change.tracking.model.CTEntry> ctEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ctCollectionLocalService.moveCTEntries(
			fromCTCollectionId, toCTCollectionId, ctEntries);
	}

	@Override
	public void moveCTEntry(
			long fromCTCollectionId, long toCTCollectionId,
			long modelClassNameId, long modelClassPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ctCollectionLocalService.moveCTEntry(
			fromCTCollectionId, toCTCollectionId, modelClassNameId,
			modelClassPK);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollection undoCTCollection(
			long ctCollectionId, long userId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.undoCTCollection(
			ctCollectionId, userId, name, description);
	}

	/**
	 * Updates the ct collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollection the ct collection
	 * @return the ct collection that was updated
	 */
	@Override
	public com.liferay.change.tracking.model.CTCollection updateCTCollection(
		com.liferay.change.tracking.model.CTCollection ctCollection) {

		return _ctCollectionLocalService.updateCTCollection(ctCollection);
	}

	@Override
	public com.liferay.change.tracking.model.CTCollection updateCTCollection(
			long userId, long ctCollectionId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCollectionLocalService.updateCTCollection(
			userId, ctCollectionId, name, description);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctCollectionLocalService.getBasePersistence();
	}

	@Override
	public CTCollectionLocalService getWrappedService() {
		return _ctCollectionLocalService;
	}

	@Override
	public void setWrappedService(
		CTCollectionLocalService ctCollectionLocalService) {

		_ctCollectionLocalService = ctCollectionLocalService;
	}

	private CTCollectionLocalService _ctCollectionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2120225261