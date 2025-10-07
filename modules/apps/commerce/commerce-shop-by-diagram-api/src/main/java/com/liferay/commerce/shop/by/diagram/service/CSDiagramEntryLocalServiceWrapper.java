/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CSDiagramEntryLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntryLocalService
 * @generated
 */
public class CSDiagramEntryLocalServiceWrapper
	implements CSDiagramEntryLocalService,
			   ServiceWrapper<CSDiagramEntryLocalService> {

	public CSDiagramEntryLocalServiceWrapper() {
		this(null);
	}

	public CSDiagramEntryLocalServiceWrapper(
		CSDiagramEntryLocalService csDiagramEntryLocalService) {

		_csDiagramEntryLocalService = csDiagramEntryLocalService;
	}

	/**
	 * Adds the cs diagram entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramEntry the cs diagram entry
	 * @return the cs diagram entry that was added
	 */
	@Override
	public CSDiagramEntry addCSDiagramEntry(CSDiagramEntry csDiagramEntry) {
		return _csDiagramEntryLocalService.addCSDiagramEntry(csDiagramEntry);
	}

	@Override
	public CSDiagramEntry addCSDiagramEntry(
			long userId, long cpDefinitionId, long cpInstanceId,
			long cProductId, boolean diagram, int quantity, String sequence,
			String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.addCSDiagramEntry(
			userId, cpDefinitionId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	/**
	 * Creates a new cs diagram entry with the primary key. Does not add the cs diagram entry to the database.
	 *
	 * @param CSDiagramEntryId the primary key for the new cs diagram entry
	 * @return the new cs diagram entry
	 */
	@Override
	public CSDiagramEntry createCSDiagramEntry(long CSDiagramEntryId) {
		return _csDiagramEntryLocalService.createCSDiagramEntry(
			CSDiagramEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCSDiagramEntries(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_csDiagramEntryLocalService.deleteCSDiagramEntries(cpDefinitionId);
	}

	/**
	 * Deletes the cs diagram entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramEntry the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws PortalException
	 */
	@Override
	public CSDiagramEntry deleteCSDiagramEntry(CSDiagramEntry csDiagramEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.deleteCSDiagramEntry(csDiagramEntry);
	}

	/**
	 * Deletes the cs diagram entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws PortalException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry deleteCSDiagramEntry(long CSDiagramEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.deleteCSDiagramEntry(
			CSDiagramEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _csDiagramEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _csDiagramEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _csDiagramEntryLocalService.dynamicQuery();
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

		return _csDiagramEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl</code>.
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

		return _csDiagramEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl</code>.
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

		return _csDiagramEntryLocalService.dynamicQuery(
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

		return _csDiagramEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _csDiagramEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CSDiagramEntry fetchCSDiagramEntry(long CSDiagramEntryId) {
		return _csDiagramEntryLocalService.fetchCSDiagramEntry(
			CSDiagramEntryId);
	}

	@Override
	public CSDiagramEntry fetchCSDiagramEntry(
		long cpDefinitionId, String sequence) {

		return _csDiagramEntryLocalService.fetchCSDiagramEntry(
			cpDefinitionId, sequence);
	}

	@Override
	public CSDiagramEntry fetchCSDiagramEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _csDiagramEntryLocalService.
			fetchCSDiagramEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _csDiagramEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<CSDiagramEntry>
		getCPDefinitionRelatedCSDiagramEntries(long cpDefinitionId) {

		return _csDiagramEntryLocalService.
			getCPDefinitionRelatedCSDiagramEntries(cpDefinitionId);
	}

	@Override
	public java.util.List<CSDiagramEntry> getCProductCSDiagramEntries(
			long cProductId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CSDiagramEntry>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.getCProductCSDiagramEntries(
			cProductId, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the cs diagram entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @return the range of cs diagram entries
	 */
	@Override
	public java.util.List<CSDiagramEntry> getCSDiagramEntries(
		int start, int end) {

		return _csDiagramEntryLocalService.getCSDiagramEntries(start, end);
	}

	@Override
	public java.util.List<CSDiagramEntry> getCSDiagramEntries(
		long cpDefinitionId, int start, int end) {

		return _csDiagramEntryLocalService.getCSDiagramEntries(
			cpDefinitionId, start, end);
	}

	/**
	 * Returns the number of cs diagram entries.
	 *
	 * @return the number of cs diagram entries
	 */
	@Override
	public int getCSDiagramEntriesCount() {
		return _csDiagramEntryLocalService.getCSDiagramEntriesCount();
	}

	@Override
	public int getCSDiagramEntriesCount(long cpDefinitionId) {
		return _csDiagramEntryLocalService.getCSDiagramEntriesCount(
			cpDefinitionId);
	}

	/**
	 * Returns the cs diagram entry with the primary key.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry
	 * @throws PortalException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry getCSDiagramEntry(long CSDiagramEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.getCSDiagramEntry(CSDiagramEntryId);
	}

	@Override
	public CSDiagramEntry getCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.getCSDiagramEntry(
			cpDefinitionId, sequence);
	}

	@Override
	public CSDiagramEntry getCSDiagramEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.
			getCSDiagramEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _csDiagramEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _csDiagramEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cs diagram entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CSDiagramEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param csDiagramEntry the cs diagram entry
	 * @return the cs diagram entry that was updated
	 */
	@Override
	public CSDiagramEntry updateCSDiagramEntry(CSDiagramEntry csDiagramEntry) {
		return _csDiagramEntryLocalService.updateCSDiagramEntry(csDiagramEntry);
	}

	@Override
	public CSDiagramEntry updateCSDiagramEntry(
			long csDiagramEntryId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _csDiagramEntryLocalService.updateCSDiagramEntry(
			csDiagramEntryId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _csDiagramEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CSDiagramEntry> getCTPersistence() {
		return _csDiagramEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<CSDiagramEntry> getModelClass() {
		return _csDiagramEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CSDiagramEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _csDiagramEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CSDiagramEntryLocalService getWrappedService() {
		return _csDiagramEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CSDiagramEntryLocalService csDiagramEntryLocalService) {

		_csDiagramEntryLocalService = csDiagramEntryLocalService;
	}

	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

}