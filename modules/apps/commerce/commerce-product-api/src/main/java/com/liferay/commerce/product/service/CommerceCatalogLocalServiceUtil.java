/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceCatalog. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CommerceCatalogLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceCatalogLocalService
 * @generated
 */
public class CommerceCatalogLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CommerceCatalogLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CommerceCatalog addCommerceCatalog(
		CommerceCatalog commerceCatalog) {

		return getService().addCommerceCatalog(commerceCatalog);
	}

	public static CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			boolean system,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceCatalog(
			externalReferenceCode, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId, system, serviceContext);
	}

	public static CommerceCatalog addCommerceCatalog(
			String externalReferenceCode, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceCatalog(
			externalReferenceCode, name, commerceCurrencyCode,
			catalogDefaultLanguageId, serviceContext);
	}

	public static CommerceCatalog addDefaultCommerceCatalog(long companyId)
		throws PortalException {

		return getService().addDefaultCommerceCatalog(companyId);
	}

	/**
	 * Creates a new commerce catalog with the primary key. Does not add the commerce catalog to the database.
	 *
	 * @param commerceCatalogId the primary key for the new commerce catalog
	 * @return the new commerce catalog
	 */
	public static CommerceCatalog createCommerceCatalog(
		long commerceCatalogId) {

		return getService().createCommerceCatalog(commerceCatalogId);
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
	public static CommerceCatalog deleteCommerceCatalog(
			CommerceCatalog commerceCatalog)
		throws PortalException {

		return getService().deleteCommerceCatalog(commerceCatalog);
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
	public static CommerceCatalog deleteCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		return getService().deleteCommerceCatalog(commerceCatalogId);
	}

	public static void deleteCommerceCatalogs(long companyId)
		throws PortalException {

		getService().deleteCommerceCatalogs(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceCatalogModelImpl</code>.
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

	public static CommerceCatalog fetchCommerceCatalog(long commerceCatalogId) {
		return getService().fetchCommerceCatalog(commerceCatalogId);
	}

	public static CommerceCatalog fetchCommerceCatalogByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCommerceCatalogByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceCatalog fetchCommerceCatalogByGroupId(long groupId) {
		return getService().fetchCommerceCatalogByGroupId(groupId);
	}

	/**
	 * Returns the commerce catalog with the matching UUID and company.
	 *
	 * @param uuid the commerce catalog's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce catalog, or <code>null</code> if a matching commerce catalog could not be found
	 */
	public static CommerceCatalog fetchCommerceCatalogByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCommerceCatalogByUuidAndCompanyId(
			uuid, companyId);
	}

	public static CommerceCatalog forceDeleteCommerceCatalog(
			CommerceCatalog commerceCatalog)
		throws PortalException {

		return getService().forceDeleteCommerceCatalog(commerceCatalog);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce catalog with the primary key.
	 *
	 * @param commerceCatalogId the primary key of the commerce catalog
	 * @return the commerce catalog
	 * @throws PortalException if a commerce catalog with the primary key could not be found
	 */
	public static CommerceCatalog getCommerceCatalog(long commerceCatalogId)
		throws PortalException {

		return getService().getCommerceCatalog(commerceCatalogId);
	}

	public static CommerceCatalog getCommerceCatalogByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceCatalogByExternalReferenceCode(
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
	public static CommerceCatalog getCommerceCatalogByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceCatalogByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.model.Group getCommerceCatalogGroup(
			long commerceCatalogId)
		throws PortalException {

		return getService().getCommerceCatalogGroup(commerceCatalogId);
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
	public static List<CommerceCatalog> getCommerceCatalogs(
		int start, int end) {

		return getService().getCommerceCatalogs(start, end);
	}

	public static List<CommerceCatalog> getCommerceCatalogs(long companyId) {
		return getService().getCommerceCatalogs(companyId);
	}

	public static List<CommerceCatalog> getCommerceCatalogs(
		long companyId, boolean system) {

		return getService().getCommerceCatalogs(companyId, system);
	}

	public static List<CommerceCatalog> getCommerceCatalogsByAccountEntryId(
		long accountEntryId) {

		return getService().getCommerceCatalogsByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the number of commerce catalogs.
	 *
	 * @return the number of commerce catalogs
	 */
	public static int getCommerceCatalogsCount() {
		return getService().getCommerceCatalogsCount();
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

	public static CommerceCatalog getOrAddEmptyCommerceCatalog(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCommerceCatalog(
			externalReferenceCode, companyId, userId);
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

	public static List<CommerceCatalog> search(long companyId)
		throws PortalException {

		return getService().search(companyId);
	}

	public static List<CommerceCatalog> search(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().search(companyId, keywords, start, end, sort);
	}

	public static int searchCommerceCatalogsCount(
			long companyId, String keywords)
		throws PortalException {

		return getService().searchCommerceCatalogsCount(companyId, keywords);
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
	public static CommerceCatalog updateCommerceCatalog(
		CommerceCatalog commerceCatalog) {

		return getService().updateCommerceCatalog(commerceCatalog);
	}

	public static CommerceCatalog updateCommerceCatalog(
			long commerceCatalogId, long accountEntryId, String name,
			String commerceCurrencyCode, String catalogDefaultLanguageId)
		throws PortalException {

		return getService().updateCommerceCatalog(
			commerceCatalogId, accountEntryId, name, commerceCurrencyCode,
			catalogDefaultLanguageId);
	}

	public static CommerceCatalog updateCommerceCatalogExternalReferenceCode(
			String externalReferenceCode, long commerceCatalogId)
		throws PortalException {

		return getService().updateCommerceCatalogExternalReferenceCode(
			externalReferenceCode, commerceCatalogId);
	}

	public static CommerceCatalogLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceCatalogLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceCatalogLocalServiceUtil.class,
			CommerceCatalogLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-445623307