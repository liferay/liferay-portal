/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTEntryLocalService
 * @generated
 */
public class CTEntryLocalServiceWrapper
	implements CTEntryLocalService, ServiceWrapper<CTEntryLocalService> {

	public CTEntryLocalServiceWrapper() {
		this(null);
	}

	public CTEntryLocalServiceWrapper(CTEntryLocalService ctEntryLocalService) {
		_ctEntryLocalService = ctEntryLocalService;
	}

	/**
	 * Adds the ct entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntry the ct entry
	 * @return the ct entry that was added
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry addCTEntry(
		com.liferay.change.tracking.model.CTEntry ctEntry) {

		return _ctEntryLocalService.addCTEntry(ctEntry);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry addCTEntry(
			String externalReferenceCode, long ctCollectionId,
			long modelClassNameId,
			com.liferay.portal.kernel.model.change.tracking.CTModel<?> ctModel,
			long userId, int changeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.addCTEntry(
			externalReferenceCode, ctCollectionId, modelClassNameId, ctModel,
			userId, changeType);
	}

	/**
	 * Creates a new ct entry with the primary key. Does not add the ct entry to the database.
	 *
	 * @param ctEntryId the primary key for the new ct entry
	 * @return the new ct entry
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry createCTEntry(
		long ctEntryId) {

		return _ctEntryLocalService.createCTEntry(ctEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ct entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntry the ct entry
	 * @return the ct entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry deleteCTEntry(
			com.liferay.change.tracking.model.CTEntry ctEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.deleteCTEntry(ctEntry);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry deleteCTEntry(
			com.liferay.change.tracking.model.CTEntry ctEntry, boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.deleteCTEntry(ctEntry, force);
	}

	/**
	 * Deletes the ct entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntryId the primary key of the ct entry
	 * @return the ct entry that was removed
	 * @throws PortalException if a ct entry with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry deleteCTEntry(
			long ctEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.deleteCTEntry(ctEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctEntryLocalService.dynamicQuery();
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

		return _ctEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTEntryModelImpl</code>.
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

		return _ctEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTEntryModelImpl</code>.
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

		return _ctEntryLocalService.dynamicQuery(
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

		return _ctEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctEntryLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry fetchCTEntry(
		long ctEntryId) {

		return _ctEntryLocalService.fetchCTEntry(ctEntryId);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry fetchCTEntry(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		return _ctEntryLocalService.fetchCTEntry(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry
		fetchCTEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _ctEntryLocalService.fetchCTEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct entry with the matching UUID and company.
	 *
	 * @param uuid the ct entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct entry, or <code>null</code> if a matching ct entry could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry
		fetchCTEntryByUuidAndCompanyId(String uuid, long companyId) {

		return _ctEntryLocalService.fetchCTEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTEntry>
		getCTCollectionCTEntries(long ctCollectionId) {

		return _ctEntryLocalService.getCTCollectionCTEntries(ctCollectionId);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTEntry>
		getCTCollectionCTEntries(
			long ctCollectionId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.change.tracking.model.CTEntry> orderByComparator) {

		return _ctEntryLocalService.getCTCollectionCTEntries(
			ctCollectionId, start, end, orderByComparator);
	}

	@Override
	public int getCTCollectionCTEntriesCount(long ctCollectionId) {
		return _ctEntryLocalService.getCTCollectionCTEntriesCount(
			ctCollectionId);
	}

	/**
	 * Returns a range of all the ct entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct entries
	 * @param end the upper bound of the range of ct entries (not inclusive)
	 * @return the range of ct entries
	 */
	@Override
	public java.util.List<com.liferay.change.tracking.model.CTEntry>
		getCTEntries(int start, int end) {

		return _ctEntryLocalService.getCTEntries(start, end);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTEntry>
		getCTEntries(long ctCollectionId, long modelClassNameId) {

		return _ctEntryLocalService.getCTEntries(
			ctCollectionId, modelClassNameId);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTEntry>
		getCTEntries(long[] ctEntryIds) {

		return _ctEntryLocalService.getCTEntries(ctEntryIds);
	}

	/**
	 * Returns the number of ct entries.
	 *
	 * @return the number of ct entries
	 */
	@Override
	public int getCTEntriesCount() {
		return _ctEntryLocalService.getCTEntriesCount();
	}

	/**
	 * Returns the ct entry with the primary key.
	 *
	 * @param ctEntryId the primary key of the ct entry
	 * @return the ct entry
	 * @throws PortalException if a ct entry with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry getCTEntry(long ctEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.getCTEntry(ctEntryId);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry
			getCTEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.getCTEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct entry with the matching UUID and company.
	 *
	 * @param uuid the ct entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct entry
	 * @throws PortalException if a matching ct entry could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry
			getCTEntryByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.getCTEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public long getCTRowCTCollectionId(
			com.liferay.change.tracking.model.CTEntry ctEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.getCTRowCTCollectionId(ctEntry);
	}

	@Override
	public java.util.List<Long> getExclusiveModelClassPKs(
		long ctCollectionId, long modelClassNameId) {

		return _ctEntryLocalService.getExclusiveModelClassPKs(
			ctCollectionId, modelClassNameId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _ctEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasCTEntries(long ctCollectionId, long modelClassNameId) {
		return _ctEntryLocalService.hasCTEntries(
			ctCollectionId, modelClassNameId);
	}

	@Override
	public boolean hasCTEntry(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		return _ctEntryLocalService.hasCTEntry(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	@Override
	public boolean hasUnpublishedCTEntries(
		long modelClassNameId, long modelClassPK, int changeType) {

		return _ctEntryLocalService.hasUnpublishedCTEntries(
			modelClassNameId, modelClassPK, changeType);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry updateChangeType(
		long ctEntryId, int changeType) {

		return _ctEntryLocalService.updateChangeType(ctEntryId, changeType);
	}

	/**
	 * Updates the ct entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntry the ct entry
	 * @return the ct entry that was updated
	 */
	@Override
	public com.liferay.change.tracking.model.CTEntry updateCTEntry(
		com.liferay.change.tracking.model.CTEntry ctEntry) {

		return _ctEntryLocalService.updateCTEntry(ctEntry);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry updateModelMvccVersion(
		long ctEntryId, long modelMvccVersion) {

		return _ctEntryLocalService.updateModelMvccVersion(
			ctEntryId, modelMvccVersion);
	}

	@Override
	public com.liferay.change.tracking.model.CTEntry updateUserId(
		long ctEntryId, long userId) {

		return _ctEntryLocalService.updateUserId(ctEntryId, userId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctEntryLocalService.getBasePersistence();
	}

	@Override
	public CTEntryLocalService getWrappedService() {
		return _ctEntryLocalService;
	}

	@Override
	public void setWrappedService(CTEntryLocalService ctEntryLocalService) {
		_ctEntryLocalService = ctEntryLocalService;
	}

	private CTEntryLocalService _ctEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1428692022