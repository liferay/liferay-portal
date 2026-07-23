/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommerceCatalogLocalService}.
 *
 * @author Marco Leo
 * @see CommerceCatalogLocalService
 * @generated
 */
public class CommerceCatalogLocalServiceWrapper
	implements CommerceCatalogLocalService,
			   ServiceWrapper<CommerceCatalogLocalService> {

	public CommerceCatalogLocalServiceWrapper() {
		this(null);
	}

	public CommerceCatalogLocalServiceWrapper(
		CommerceCatalogLocalService commerceCatalogLocalService) {

		_commerceCatalogLocalService = commerceCatalogLocalService;
	}

	/**
	 * Adds the commerce catalog to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCatalogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCatalog the commerce catalog
	 * @return the commerce catalog that was added
	 */
	@Override
	public CommerceCatalog addCommerceCatalog(CommerceCatalog commerceCatalog) {
		return _commerceCatalogLocalService.addCommerceCatalog(commerceCatalog);
	}

	@Override
	public CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			boolean system,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.addCommerceCatalog(
			externalReferenceCode, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId, system, serviceContext);
	}

	@Override
	public CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.addCommerceCatalog(
			externalReferenceCode, name, commerceCurrencyCode,
			catalogDefaultLanguageId, serviceContext);
	}

	@Override
	public CommerceCatalog addDefaultCommerceCatalog(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.addDefaultCommerceCatalog(
			companyId);
	}

	/**
	 * Creates a new commerce catalog with the primary key. Does not add the commerce catalog to the database.
	 *
	 * @param commerceCatalogId the primary key for the new commerce catalog
	 * @return the new commerce catalog
	 */
	@Override
	public CommerceCatalog createCommerceCatalog(long commerceCatalogId) {
		return _commerceCatalogLocalService.createCommerceCatalog(
			commerceCatalogId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce catalog from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCatalogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCatalog the commerce catalog
	 * @return the commerce catalog that was removed
	 * @throws PortalException
	 */
	@Override
	public CommerceCatalog deleteCommerceCatalog(
			CommerceCatalog commerceCatalog)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.deleteCommerceCatalog(
			commerceCatalog);
	}

	/**
	 * Deletes the commerce catalog with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCatalogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCatalogId the primary key of the commerce catalog
	 * @return the commerce catalog that was removed
	 * @throws PortalException if a commerce catalog with the primary key could not be found
	 */
	@Override
	public CommerceCatalog deleteCommerceCatalog(long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.deleteCommerceCatalog(
			commerceCatalogId);
	}

	@Override
	public void deleteCommerceCatalogs(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceCatalogLocalService.deleteCommerceCatalogs(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceCatalogLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceCatalogLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceCatalogLocalService.dynamicQuery();
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

		return _commerceCatalogLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl</code>.
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

		return _commerceCatalogLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl</code>.
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

		return _commerceCatalogLocalService.dynamicQuery(
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

		return _commerceCatalogLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceCatalogLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommerceCatalog fetchCommerceCatalog(long commerceCatalogId) {
		return _commerceCatalogLocalService.fetchCommerceCatalog(
			commerceCatalogId);
	}

	@Override
	public CommerceCatalog fetchCommerceCatalogByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commerceCatalogLocalService.
			fetchCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommerceCatalog fetchCommerceCatalogByGroupId(long groupId) {
		return _commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
			groupId);
	}

	/**
	 * Returns the commerce catalog with the matching UUID and company.
	 *
	 * @param uuid the commerce catalog's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog fetchCommerceCatalogByUuidAndCompanyId(
		String uuid, long companyId) {

		return _commerceCatalogLocalService.
			fetchCommerceCatalogByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public CommerceCatalog forceDeleteCommerceCatalog(
			CommerceCatalog commerceCatalog)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.forceDeleteCommerceCatalog(
			commerceCatalog);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceCatalogLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce catalog with the primary key.
	 *
	 * @param commerceCatalogId the primary key of the commerce catalog
	 * @return the commerce catalog
	 * @throws PortalException if a commerce catalog with the primary key could not be found
	 */
	@Override
	public CommerceCatalog getCommerceCatalog(long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.getCommerceCatalog(
			commerceCatalogId);
	}

	@Override
	public CommerceCatalog getCommerceCatalogByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.
			getCommerceCatalogByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce catalog with the matching UUID and company.
	 *
	 * @param uuid the commerce catalog's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce catalog
	 * @throws PortalException if a matching commerce catalog could not be found
	 */
	@Override
	public CommerceCatalog getCommerceCatalogByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.
			getCommerceCatalogByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Group getCommerceCatalogGroup(
			long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.getCommerceCatalogGroup(
			commerceCatalogId);
	}

	/**
	 * Returns a range of all the commerce catalogs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce catalogs
	 * @param end the upper bound of the range of commerce catalogs (not inclusive)
	 * @return the range of commerce catalogs
	 */
	@Override
	public java.util.List<CommerceCatalog> getCommerceCatalogs(
		int start, int end) {

		return _commerceCatalogLocalService.getCommerceCatalogs(start, end);
	}

	@Override
	public java.util.List<CommerceCatalog> getCommerceCatalogs(long companyId) {
		return _commerceCatalogLocalService.getCommerceCatalogs(companyId);
	}

	@Override
	public java.util.List<CommerceCatalog> getCommerceCatalogs(
		long companyId, boolean system) {

		return _commerceCatalogLocalService.getCommerceCatalogs(
			companyId, system);
	}

	@Override
	public java.util.List<CommerceCatalog> getCommerceCatalogsByAccountEntryId(
		long accountEntryId) {

		return _commerceCatalogLocalService.getCommerceCatalogsByAccountEntryId(
			accountEntryId);
	}

	/**
	 * Returns the number of commerce catalogs.
	 *
	 * @return the number of commerce catalogs
	 */
	@Override
	public int getCommerceCatalogsCount() {
		return _commerceCatalogLocalService.getCommerceCatalogsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceCatalogLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceCatalogLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CommerceCatalog getOrAddEmptyCommerceCatalog(
			String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.getOrAddEmptyCommerceCatalog(
			externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceCatalogLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<CommerceCatalog> search(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.search(companyId);
	}

	@Override
	public java.util.List<CommerceCatalog> search(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.search(
			companyId, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceCatalogsCount(long companyId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.searchCommerceCatalogsCount(
			companyId, keywords);
	}

	/**
	 * Updates the commerce catalog in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceCatalogLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceCatalog the commerce catalog
	 * @return the commerce catalog that was updated
	 */
	@Override
	public CommerceCatalog updateCommerceCatalog(
		CommerceCatalog commerceCatalog) {

		return _commerceCatalogLocalService.updateCommerceCatalog(
			commerceCatalog);
	}

	@Override
	public CommerceCatalog updateCommerceCatalog(
			long commerceCatalogId, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.updateCommerceCatalog(
			commerceCatalogId, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId);
	}

	@Override
	public CommerceCatalog updateCommerceCatalogExternalReferenceCode(
			String externalReferenceCode, long commerceCatalogId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceCatalogLocalService.
			updateCommerceCatalogExternalReferenceCode(
				externalReferenceCode, commerceCatalogId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceCatalogLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommerceCatalog> getCTPersistence() {
		return _commerceCatalogLocalService.getCTPersistence();
	}

	@Override
	public Class<CommerceCatalog> getModelClass() {
		return _commerceCatalogLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommerceCatalog>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commerceCatalogLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommerceCatalogLocalService getWrappedService() {
		return _commerceCatalogLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceCatalogLocalService commerceCatalogLocalService) {

		_commerceCatalogLocalService = commerceCatalogLocalService;
	}

	private CommerceCatalogLocalService _commerceCatalogLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1844269830