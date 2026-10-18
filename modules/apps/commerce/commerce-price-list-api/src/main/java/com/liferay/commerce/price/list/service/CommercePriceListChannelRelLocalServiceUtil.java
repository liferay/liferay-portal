/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service;

import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommercePriceListChannelRel. This utility wraps
 * <code>com.liferay.commerce.price.list.service.impl.CommercePriceListChannelRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceListChannelRelLocalService
 * @generated
 */
public class CommercePriceListChannelRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.price.list.service.impl.CommercePriceListChannelRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce price list channel rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListChannelRel the commerce price list channel rel
	 * @return the commerce price list channel rel that was added
	 */
	public static CommercePriceListChannelRel addCommercePriceListChannelRel(
		CommercePriceListChannelRel commercePriceListChannelRel) {

		return getService().addCommercePriceListChannelRel(
			commercePriceListChannelRel);
	}

	public static CommercePriceListChannelRel addCommercePriceListChannelRel(
			long userId, long commercePriceListId, long commerceChannelId,
			int order,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePriceListChannelRel(
			userId, commercePriceListId, commerceChannelId, order,
			serviceContext);
	}

	/**
	 * Creates a new commerce price list channel rel with the primary key. Does not add the commerce price list channel rel to the database.
	 *
	 * @param CommercePriceListChannelRelId the primary key for the new commerce price list channel rel
	 * @return the new commerce price list channel rel
	 */
	public static CommercePriceListChannelRel createCommercePriceListChannelRel(
		long CommercePriceListChannelRelId) {

		return getService().createCommercePriceListChannelRel(
			CommercePriceListChannelRelId);
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
	 * Deletes the commerce price list channel rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListChannelRel the commerce price list channel rel
	 * @return the commerce price list channel rel that was removed
	 * @throws PortalException
	 */
	public static CommercePriceListChannelRel deleteCommercePriceListChannelRel(
			CommercePriceListChannelRel commercePriceListChannelRel)
		throws PortalException {

		return getService().deleteCommercePriceListChannelRel(
			commercePriceListChannelRel);
	}

	/**
	 * Deletes the commerce price list channel rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CommercePriceListChannelRelId the primary key of the commerce price list channel rel
	 * @return the commerce price list channel rel that was removed
	 * @throws PortalException if a commerce price list channel rel with the primary key could not be found
	 */
	public static CommercePriceListChannelRel deleteCommercePriceListChannelRel(
			long CommercePriceListChannelRelId)
		throws PortalException {

		return getService().deleteCommercePriceListChannelRel(
			CommercePriceListChannelRelId);
	}

	public static void deleteCommercePriceListChannelRels(
			long commercePriceListId)
		throws PortalException {

		getService().deleteCommercePriceListChannelRels(commercePriceListId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelModelImpl</code>.
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

	public static CommercePriceListChannelRel fetchCommercePriceListChannelRel(
		long CommercePriceListChannelRelId) {

		return getService().fetchCommercePriceListChannelRel(
			CommercePriceListChannelRelId);
	}

	public static CommercePriceListChannelRel fetchCommercePriceListChannelRel(
		long commerceChannelId, long commercePriceListId) {

		return getService().fetchCommercePriceListChannelRel(
			commerceChannelId, commercePriceListId);
	}

	/**
	 * Returns the commerce price list channel rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list channel rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list channel rel, or <code>null</code> if a matching commerce price list channel rel could not be found
	 */
	public static CommercePriceListChannelRel
		fetchCommercePriceListChannelRelByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommercePriceListChannelRelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price list channel rel with the primary key.
	 *
	 * @param CommercePriceListChannelRelId the primary key of the commerce price list channel rel
	 * @return the commerce price list channel rel
	 * @throws PortalException if a commerce price list channel rel with the primary key could not be found
	 */
	public static CommercePriceListChannelRel getCommercePriceListChannelRel(
			long CommercePriceListChannelRelId)
		throws PortalException {

		return getService().getCommercePriceListChannelRel(
			CommercePriceListChannelRelId);
	}

	/**
	 * Returns the commerce price list channel rel with the matching UUID and company.
	 *
	 * @param uuid the commerce price list channel rel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce price list channel rel
	 * @throws PortalException if a matching commerce price list channel rel could not be found
	 */
	public static CommercePriceListChannelRel
			getCommercePriceListChannelRelByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommercePriceListChannelRelByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price list channel rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceListChannelRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price list channel rels
	 * @param end the upper bound of the range of commerce price list channel rels (not inclusive)
	 * @return the range of commerce price list channel rels
	 */
	public static List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(int start, int end) {

		return getService().getCommercePriceListChannelRels(start, end);
	}

	public static List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(long commercePriceListId) {

		return getService().getCommercePriceListChannelRels(
			commercePriceListId);
	}

	public static List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceListChannelRel> orderByComparator) {

		return getService().getCommercePriceListChannelRels(
			commercePriceListId, start, end, orderByComparator);
	}

	public static List<CommercePriceListChannelRel>
		getCommercePriceListChannelRels(
			long commercePriceListId, String name, int start, int end) {

		return getService().getCommercePriceListChannelRels(
			commercePriceListId, name, start, end);
	}

	/**
	 * Returns the number of commerce price list channel rels.
	 *
	 * @return the number of commerce price list channel rels
	 */
	public static int getCommercePriceListChannelRelsCount() {
		return getService().getCommercePriceListChannelRelsCount();
	}

	public static int getCommercePriceListChannelRelsCount(
		long commercePriceListId) {

		return getService().getCommercePriceListChannelRelsCount(
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
	 * Updates the commerce price list channel rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceListChannelRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceListChannelRel the commerce price list channel rel
	 * @return the commerce price list channel rel that was updated
	 */
	public static CommercePriceListChannelRel updateCommercePriceListChannelRel(
		CommercePriceListChannelRel commercePriceListChannelRel) {

		return getService().updateCommercePriceListChannelRel(
			commercePriceListChannelRel);
	}

	public static CommercePriceListChannelRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePriceListChannelRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePriceListChannelRelLocalServiceUtil.class,
			CommercePriceListChannelRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:984082755