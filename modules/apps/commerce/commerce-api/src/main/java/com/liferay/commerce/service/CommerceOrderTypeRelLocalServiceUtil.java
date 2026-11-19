/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceOrderTypeRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceOrderTypeRel. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceOrderTypeRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderTypeRelLocalService
 * @generated
 */
public class CommerceOrderTypeRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceOrderTypeRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce order type rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderTypeRel the commerce order type rel
	 * @return the commerce order type rel that was added
	 */
	public static CommerceOrderTypeRel addCommerceOrderTypeRel(
		CommerceOrderTypeRel commerceOrderTypeRel) {

		return getService().addCommerceOrderTypeRel(commerceOrderTypeRel);
	}

	public static CommerceOrderTypeRel addCommerceOrderTypeRel(
			long userId, String className, long classPK,
			long commerceOrderTypeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceOrderTypeRel(
			userId, className, classPK, commerceOrderTypeId, serviceContext);
	}

	/**
	 * Creates a new commerce order type rel with the primary key. Does not add the commerce order type rel to the database.
	 *
	 * @param commerceOrderTypeRelId the primary key for the new commerce order type rel
	 * @return the new commerce order type rel
	 */
	public static CommerceOrderTypeRel createCommerceOrderTypeRel(
		long commerceOrderTypeRelId) {

		return getService().createCommerceOrderTypeRel(commerceOrderTypeRelId);
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
	 * Deletes the commerce order type rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderTypeRel the commerce order type rel
	 * @return the commerce order type rel that was removed
	 */
	public static CommerceOrderTypeRel deleteCommerceOrderTypeRel(
		CommerceOrderTypeRel commerceOrderTypeRel) {

		return getService().deleteCommerceOrderTypeRel(commerceOrderTypeRel);
	}

	/**
	 * Deletes the commerce order type rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderTypeRelId the primary key of the commerce order type rel
	 * @return the commerce order type rel that was removed
	 * @throws PortalException if a commerce order type rel with the primary key could not be found
	 */
	public static CommerceOrderTypeRel deleteCommerceOrderTypeRel(
			long commerceOrderTypeRelId)
		throws PortalException {

		return getService().deleteCommerceOrderTypeRel(commerceOrderTypeRelId);
	}

	public static void deleteCommerceOrderTypeRels(long commerceOrderTypeId)
		throws PortalException {

		getService().deleteCommerceOrderTypeRels(commerceOrderTypeId);
	}

	public static void deleteCommerceOrderTypeRels(
			String className, long commerceOrderTypeId)
		throws PortalException {

		getService().deleteCommerceOrderTypeRels(
			className, commerceOrderTypeId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeRelModelImpl</code>.
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

	public static CommerceOrderTypeRel fetchCommerceOrderTypeRel(
		long commerceOrderTypeRelId) {

		return getService().fetchCommerceOrderTypeRel(commerceOrderTypeRelId);
	}

	public static CommerceOrderTypeRel
		fetchCommerceOrderTypeRelByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceOrderTypeRelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce order type rel, or <code>null</code> if a matching commerce order type rel could not be found
	 */
	public static CommerceOrderTypeRel
		fetchCommerceOrderTypeRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommerceOrderTypeRelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommerceOrderTypeRel>
			getCommerceOrderTypeCommerceChannelRels(
				long commerceOrderTypeId, String keywords, int start, int end)
		throws PortalException {

		return getService().getCommerceOrderTypeCommerceChannelRels(
			commerceOrderTypeId, keywords, start, end);
	}

	public static int getCommerceOrderTypeCommerceChannelRelsCount(
			long commerceOrderTypeId, String keywords)
		throws PortalException {

		return getService().getCommerceOrderTypeCommerceChannelRelsCount(
			commerceOrderTypeId, keywords);
	}

	/**
	 * Returns the commerce order type rel with the primary key.
	 *
	 * @param commerceOrderTypeRelId the primary key of the commerce order type rel
	 * @return the commerce order type rel
	 * @throws PortalException if a commerce order type rel with the primary key could not be found
	 */
	public static CommerceOrderTypeRel getCommerceOrderTypeRel(
			long commerceOrderTypeRelId)
		throws PortalException {

		return getService().getCommerceOrderTypeRel(commerceOrderTypeRelId);
	}

	public static CommerceOrderTypeRel
			getCommerceOrderTypeRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceOrderTypeRelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order type rel with the matching UUID and company.
	 *
	 * @param uuid the commerce order type rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce order type rel
	 * @throws PortalException if a matching commerce order type rel could not be found
	 */
	public static CommerceOrderTypeRel
			getCommerceOrderTypeRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceOrderTypeRelByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce order type rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order type rels
	 * @param end the upper bound of the range of commerce order type rels (not inclusive)
	 * @return the range of commerce order type rels
	 */
	public static List<CommerceOrderTypeRel> getCommerceOrderTypeRels(
		int start, int end) {

		return getService().getCommerceOrderTypeRels(start, end);
	}

	/**
	 * Returns the number of commerce order type rels.
	 *
	 * @return the number of commerce order type rels
	 */
	public static int getCommerceOrderTypeRelsCount() {
		return getService().getCommerceOrderTypeRelsCount();
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
	 * Updates the commerce order type rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderTypeRel the commerce order type rel
	 * @return the commerce order type rel that was updated
	 */
	public static CommerceOrderTypeRel updateCommerceOrderTypeRel(
		CommerceOrderTypeRel commerceOrderTypeRel) {

		return getService().updateCommerceOrderTypeRel(commerceOrderTypeRel);
	}

	public static CommerceOrderTypeRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceOrderTypeRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceOrderTypeRelLocalServiceUtil.class,
			CommerceOrderTypeRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1529963748