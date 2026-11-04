/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommercePriceListDiscountRel. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommercePriceListDiscountRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListDiscountRelLocalService
 * @generated
 */
public class CommercePriceListDiscountRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceListDiscountRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce price list discount rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRel the commerce price list discount rel
	 * @return the commerce price list discount rel that was added
	 */
	public static CommercePriceListDiscountRel addCommercePriceListDiscountRel(
		CommercePriceListDiscountRel commercePriceListDiscountRel) {

		return getService().addCommercePriceListDiscountRel(
			commercePriceListDiscountRel);
	}

	public static CommercePriceListDiscountRel addCommercePriceListDiscountRel(
			long userId, long commercePriceListId, long commerceDiscountId,
			int order,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceListDiscountRel(
			userId, commercePriceListId, commerceDiscountId, order,
			serviceContext);
	}

	/**
	 * Creates a new commerce price list discount rel with the primary key. Does not add the commerce price list discount rel to the database.
	 *
	 * @param commercePriceListDiscountRelId the primary key for the new commerce price list discount rel
	 * @return the new commerce price list discount rel
	 */
	public static CommercePriceListDiscountRel
		createCommercePriceListDiscountRel(
			long commercePriceListDiscountRelId) {

		return getService().createCommercePriceListDiscountRel(
			commercePriceListDiscountRelId);
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
	 * Deletes the commerce price list discount rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRel the commerce price list discount rel
	 * @return the commerce price list discount rel that was removed
	 * @throws PortalException
	 */
	public static CommercePriceListDiscountRel
			deleteCommercePriceListDiscountRel(
				CommercePriceListDiscountRel commercePriceListDiscountRel)
		throws PortalException {

		return getService().deleteCommercePriceListDiscountRel(
			commercePriceListDiscountRel);
	}

	/**
	 * Deletes the commerce price list discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRelId the primary key of the commerce price list discount rel
	 * @return the commerce price list discount rel that was removed
	 * @throws PortalException if a commerce price list discount rel with the primary key could not be found
	 */
	public static CommercePriceListDiscountRel
			deleteCommercePriceListDiscountRel(
				long commercePriceListDiscountRelId)
		throws PortalException {

		return getService().deleteCommercePriceListDiscountRel(
			commercePriceListDiscountRelId);
	}

	public static void deleteCommercePriceListDiscountRels(
			long commercePriceListId)
		throws PortalException {

		getService().deleteCommercePriceListDiscountRels(commercePriceListId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListDiscountRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListDiscountRelModelImpl</code>.
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

	public static CommercePriceListDiscountRel
		fetchCommercePriceListDiscountRel(long commercePriceListDiscountRelId) {

		return getService().fetchCommercePriceListDiscountRel(
			commercePriceListDiscountRelId);
	}

	public static CommercePriceListDiscountRel
		fetchCommercePriceListDiscountRel(
			long commerceDiscountId, long commercePriceListId) {

		return getService().fetchCommercePriceListDiscountRel(
			commerceDiscountId, commercePriceListId);
	}

	/**
	 * Returns the commerce price list discount rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list discount rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list discount rel, or <code>null</code> if a matching commerce price list discount rel could not be found
	 */
	public static CommercePriceListDiscountRel
		fetchCommercePriceListDiscountRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommercePriceListDiscountRelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list discount rel with the primary key.
	 *
	 * @param commercePriceListDiscountRelId the primary key of the commerce price list discount rel
	 * @return the commerce price list discount rel
	 * @throws PortalException if a commerce price list discount rel with the primary key could not be found
	 */
	public static CommercePriceListDiscountRel getCommercePriceListDiscountRel(
			long commercePriceListDiscountRelId)
		throws PortalException {

		return getService().getCommercePriceListDiscountRel(
			commercePriceListDiscountRelId);
	}

	/**
	 * Returns the commerce price list discount rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list discount rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list discount rel
	 * @throws PortalException if a matching commerce price list discount rel could not be found
	 */
	public static CommercePriceListDiscountRel
			getCommercePriceListDiscountRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommercePriceListDiscountRelByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list discount rels
	 * @param end the upper bound of the range of commerce price list discount rels (not inclusive)
	 * @return the range of commerce price list discount rels
	 */
	public static List<CommercePriceListDiscountRel>
		getCommercePriceListDiscountRels(int start, int end) {

		return getService().getCommercePriceListDiscountRels(start, end);
	}

	public static List<CommercePriceListDiscountRel>
		getCommercePriceListDiscountRels(long commercePriceListId) {

		return getService().getCommercePriceListDiscountRels(
			commercePriceListId);
	}

	public static List<CommercePriceListDiscountRel>
		getCommercePriceListDiscountRels(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceListDiscountRel> orderByComparator) {

		return getService().getCommercePriceListDiscountRels(
			commercePriceListId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce price list discount rels.
	 *
	 * @return the number of commerce price list discount rels
	 */
	public static int getCommercePriceListDiscountRelsCount() {
		return getService().getCommercePriceListDiscountRelsCount();
	}

	public static int getCommercePriceListDiscountRelsCount(
		long commercePriceListId) {

		return getService().getCommercePriceListDiscountRelsCount(
			commercePriceListId);
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
	 * Updates the commerce price list discount rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListDiscountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListDiscountRel the commerce price list discount rel
	 * @return the commerce price list discount rel that was updated
	 */
	public static CommercePriceListDiscountRel
		updateCommercePriceListDiscountRel(
			CommercePriceListDiscountRel commercePriceListDiscountRel) {

		return getService().updateCommercePriceListDiscountRel(
			commercePriceListDiscountRel);
	}

	public static CommercePriceListDiscountRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceListDiscountRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePriceListDiscountRelLocalServiceUtil.class,
			CommercePriceListDiscountRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1113026078