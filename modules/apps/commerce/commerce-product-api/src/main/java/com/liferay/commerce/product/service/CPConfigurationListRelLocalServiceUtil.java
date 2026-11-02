/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CPConfigurationListRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPConfigurationListRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPConfigurationListRelLocalService
 * @generated
 */
public class CPConfigurationListRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationListRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp configuration list rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationListRel the cp configuration list rel
	 * @return the cp configuration list rel that was added
	 */
	public static CPConfigurationListRel addCPConfigurationListRel(
		CPConfigurationListRel cpConfigurationListRel) {

		return getService().addCPConfigurationListRel(cpConfigurationListRel);
	}

	public static CPConfigurationListRel addCPConfigurationListRel(
			long userId, String className, long classPK,
			long cpConfigurationListId)
		throws PortalException {

		return getService().addCPConfigurationListRel(
			userId, className, classPK, cpConfigurationListId);
	}

	/**
	 * Creates a new cp configuration list rel with the primary key. Does not add the cp configuration list rel to the database.
	 *
	 * @param CPConfigurationListRelId the primary key for the new cp configuration list rel
	 * @return the new cp configuration list rel
	 */
	public static CPConfigurationListRel createCPConfigurationListRel(
		long CPConfigurationListRelId) {

		return getService().createCPConfigurationListRel(
			CPConfigurationListRelId);
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
	 * Deletes the cp configuration list rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationListRel the cp configuration list rel
	 * @return the cp configuration list rel that was removed
	 * @throws PortalException
	 */
	public static CPConfigurationListRel deleteCPConfigurationListRel(
			CPConfigurationListRel cpConfigurationListRel)
		throws PortalException {

		return getService().deleteCPConfigurationListRel(
			cpConfigurationListRel);
	}

	/**
	 * Deletes the cp configuration list rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPConfigurationListRelId the primary key of the cp configuration list rel
	 * @return the cp configuration list rel that was removed
	 * @throws PortalException if a cp configuration list rel with the primary key could not be found
	 */
	public static CPConfigurationListRel deleteCPConfigurationListRel(
			long CPConfigurationListRelId)
		throws PortalException {

		return getService().deleteCPConfigurationListRel(
			CPConfigurationListRelId);
	}

	public static void deleteCPConfigurationListRels(long cpConfigurationListId)
		throws PortalException {

		getService().deleteCPConfigurationListRels(cpConfigurationListId);
	}

	public static void deleteCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws PortalException {

		getService().deleteCPConfigurationListRels(
			className, cpConfigurationListId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListRelModelImpl</code>.
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

	public static CPConfigurationListRel fetchCPConfigurationListRel(
		long CPConfigurationListRelId) {

		return getService().fetchCPConfigurationListRel(
			CPConfigurationListRelId);
	}

	public static CPConfigurationListRel fetchCPConfigurationListRel(
		String className, long classPK, long cpConfigurationListId) {

		return getService().fetchCPConfigurationListRel(
			className, classPK, cpConfigurationListId);
	}

	public static List<CPConfigurationListRel>
		getAccountEntryCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end) {

		return getService().getAccountEntryCPConfigurationListRels(
			cpConfigurationListId, keywords, start, end);
	}

	public static int getAccountEntryCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return getService().getAccountEntryCPConfigurationListRelsCount(
			cpConfigurationListId, keywords);
	}

	public static List<CPConfigurationListRel>
		getAccountGroupCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end) {

		return getService().getAccountGroupCPConfigurationListRels(
			cpConfigurationListId, keywords, start, end);
	}

	public static int getAccountGroupCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return getService().getAccountGroupCPConfigurationListRelsCount(
			cpConfigurationListId, keywords);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CPConfigurationListRel>
		getCommerceOrderTypeCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end) {

		return getService().getCommerceOrderTypeCPConfigurationListRels(
			cpConfigurationListId, keywords, start, end);
	}

	public static int getCommerceOrderTypeCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return getService().getCommerceOrderTypeCPConfigurationListRelsCount(
			cpConfigurationListId, keywords);
	}

	/**
	 * Returns the cp configuration list rel with the primary key.
	 *
	 * @param CPConfigurationListRelId the primary key of the cp configuration list rel
	 * @return the cp configuration list rel
	 * @throws PortalException if a cp configuration list rel with the primary key could not be found
	 */
	public static CPConfigurationListRel getCPConfigurationListRel(
			long CPConfigurationListRelId)
		throws PortalException {

		return getService().getCPConfigurationListRel(CPConfigurationListRelId);
	}

	/**
	 * Returns a range of all the cp configuration list rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration list rels
	 * @param end the upper bound of the range of cp configuration list rels (not inclusive)
	 * @return the range of cp configuration list rels
	 */
	public static List<CPConfigurationListRel> getCPConfigurationListRels(
		int start, int end) {

		return getService().getCPConfigurationListRels(start, end);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
		long cpConfigurationListId) {

		return getService().getCPConfigurationListRels(cpConfigurationListId);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
		long cpConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationListRel> orderByComparator) {

		return getService().getCPConfigurationListRels(
			cpConfigurationListId, start, end, orderByComparator);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
		String className, long cpConfigurationListId) {

		return getService().getCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	public static List<CPConfigurationListRel> getCPConfigurationListRels(
		String className, long cpConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationListRel> orderByComparator) {

		return getService().getCPConfigurationListRels(
			className, cpConfigurationListId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp configuration list rels.
	 *
	 * @return the number of cp configuration list rels
	 */
	public static int getCPConfigurationListRelsCount() {
		return getService().getCPConfigurationListRelsCount();
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
	 * Updates the cp configuration list rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationListRel the cp configuration list rel
	 * @return the cp configuration list rel that was updated
	 */
	public static CPConfigurationListRel updateCPConfigurationListRel(
		CPConfigurationListRel cpConfigurationListRel) {

		return getService().updateCPConfigurationListRel(
			cpConfigurationListRel);
	}

	public static CPConfigurationListRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPConfigurationListRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPConfigurationListRelLocalServiceUtil.class,
			CPConfigurationListRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-134360728