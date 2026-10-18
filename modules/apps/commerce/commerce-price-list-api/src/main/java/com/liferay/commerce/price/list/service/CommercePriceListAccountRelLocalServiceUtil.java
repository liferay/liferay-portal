/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListAccountRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommercePriceListAccountRel. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommercePriceListAccountRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListAccountRelLocalService
 * @generated
 */
public class CommercePriceListAccountRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceListAccountRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce price list account rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was added
	 */
	public static CommercePriceListAccountRel addCommercePriceListAccountRel(
		CommercePriceListAccountRel commercePriceListAccountRel) {

		return getService().addCommercePriceListAccountRel(
			commercePriceListAccountRel);
	}

	public static CommercePriceListAccountRel addCommercePriceListAccountRel(
			long userId, long commercePriceListId, long commerceAccountId,
			int order,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceListAccountRel(
			userId, commercePriceListId, commerceAccountId, order,
			serviceContext);
	}

	/**
	 * Creates a new commerce price list account rel with the primary key. Does not add the commerce price list account rel to the database.
	 *
	 * @param commercePriceListAccountRelId the primary key for the new commerce price list account rel
	 * @return the new commerce price list account rel
	 */
	public static CommercePriceListAccountRel createCommercePriceListAccountRel(
		long commercePriceListAccountRelId) {

		return getService().createCommercePriceListAccountRel(
			commercePriceListAccountRelId);
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
	 * Deletes the commerce price list account rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was removed
	 * @throws PortalException
	 */
	public static CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			CommercePriceListAccountRel commercePriceListAccountRel)
		throws PortalException {

		return getService().deleteCommercePriceListAccountRel(
			commercePriceListAccountRel);
	}

	/**
	 * Deletes the commerce price list account rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel that was removed
	 * @throws PortalException if a commerce price list account rel with the primary key could not be found
	 */
	public static CommercePriceListAccountRel deleteCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws PortalException {

		return getService().deleteCommercePriceListAccountRel(
			commercePriceListAccountRelId);
	}

	public static void deleteCommercePriceListAccountRels(
			long commercePriceListId)
		throws PortalException {

		getService().deleteCommercePriceListAccountRels(commercePriceListId);
	}

	public static void deleteCommercePriceListAccountRelsByCommercePriceListId(
			long commercePriceListId)
		throws PortalException {

		getService().deleteCommercePriceListAccountRelsByCommercePriceListId(
			commercePriceListId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
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

	public static CommercePriceListAccountRel fetchCommercePriceListAccountRel(
		long commercePriceListAccountRelId) {

		return getService().fetchCommercePriceListAccountRel(
			commercePriceListAccountRelId);
	}

	public static CommercePriceListAccountRel fetchCommercePriceListAccountRel(
		long commerceAccountId, long commercePriceListId) {

		return getService().fetchCommercePriceListAccountRel(
			commerceAccountId, commercePriceListId);
	}

	/**
	 * Returns the commerce price list account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list account rel, or <code>null</code> if a matching commerce price list account rel could not be found
	 */
	public static CommercePriceListAccountRel
		fetchCommercePriceListAccountRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommercePriceListAccountRelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list account rel with the primary key.
	 *
	 * @param commercePriceListAccountRelId the primary key of the commerce price list account rel
	 * @return the commerce price list account rel
	 * @throws PortalException if a commerce price list account rel with the primary key could not be found
	 */
	public static CommercePriceListAccountRel getCommercePriceListAccountRel(
			long commercePriceListAccountRelId)
		throws PortalException {

		return getService().getCommercePriceListAccountRel(
			commercePriceListAccountRelId);
	}

	/**
	 * Returns the commerce price list account rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list account rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list account rel
	 * @throws PortalException if a matching commerce price list account rel could not be found
	 */
	public static CommercePriceListAccountRel
			getCommercePriceListAccountRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommercePriceListAccountRelByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list account rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list account rels
	 * @param end the upper bound of the range of commerce price list account rels (not inclusive)
	 * @return the range of commerce price list account rels
	 */
	public static List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(int start, int end) {

		return getService().getCommercePriceListAccountRels(start, end);
	}

	public static List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(long commercePriceListId) {

		return getService().getCommercePriceListAccountRels(
			commercePriceListId);
	}

	public static List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceListAccountRel> orderByComparator) {

		return getService().getCommercePriceListAccountRels(
			commercePriceListId, start, end, orderByComparator);
	}

	public static List<CommercePriceListAccountRel>
		getCommercePriceListAccountRels(
			long commercePriceListId, String name, int start, int end) {

		return getService().getCommercePriceListAccountRels(
			commercePriceListId, name, start, end);
	}

	/**
	 * Returns the number of commerce price list account rels.
	 *
	 * @return the number of commerce price list account rels
	 */
	public static int getCommercePriceListAccountRelsCount() {
		return getService().getCommercePriceListAccountRelsCount();
	}

	public static int getCommercePriceListAccountRelsCount(
		long commercePriceListId) {

		return getService().getCommercePriceListAccountRelsCount(
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
	 * Updates the commerce price list account rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListAccountRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListAccountRel the commerce price list account rel
	 * @return the commerce price list account rel that was updated
	 */
	public static CommercePriceListAccountRel updateCommercePriceListAccountRel(
		CommercePriceListAccountRel commercePriceListAccountRel) {

		return getService().updateCommercePriceListAccountRel(
			commercePriceListAccountRel);
	}

	public static CommercePriceListAccountRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceListAccountRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePriceListAccountRelLocalServiceUtil.class,
			CommercePriceListAccountRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-315564782