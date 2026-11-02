/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service;

import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for COREntryRel. This utility wraps
 * <code>com.liferay.commerce.order.rule.service.impl.COREntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see COREntryRelLocalService
 * @generated
 */
public class COREntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.order.rule.service.impl.COREntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cor entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param corEntryRel the cor entry rel
	 * @return the cor entry rel that was added
	 */
	public static COREntryRel addCOREntryRel(COREntryRel corEntryRel) {
		return getService().addCOREntryRel(corEntryRel);
	}

	public static COREntryRel addCOREntryRel(
			long userId, String className, long classPK, long corEntryId)
		throws PortalException {

		return getService().addCOREntryRel(
			userId, className, classPK, corEntryId);
	}

	/**
	 * Creates a new cor entry rel with the primary key. Does not add the cor entry rel to the database.
	 *
	 * @param COREntryRelId the primary key for the new cor entry rel
	 * @return the new cor entry rel
	 */
	public static COREntryRel createCOREntryRel(long COREntryRelId) {
		return getService().createCOREntryRel(COREntryRelId);
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
	 * Deletes the cor entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param corEntryRel the cor entry rel
	 * @return the cor entry rel that was removed
	 * @throws PortalException
	 */
	public static COREntryRel deleteCOREntryRel(COREntryRel corEntryRel)
		throws PortalException {

		return getService().deleteCOREntryRel(corEntryRel);
	}

	/**
	 * Deletes the cor entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param COREntryRelId the primary key of the cor entry rel
	 * @return the cor entry rel that was removed
	 * @throws PortalException if a cor entry rel with the primary key could not be found
	 */
	public static COREntryRel deleteCOREntryRel(long COREntryRelId)
		throws PortalException {

		return getService().deleteCOREntryRel(COREntryRelId);
	}

	public static void deleteCOREntryRels(long corEntryId)
		throws PortalException {

		getService().deleteCOREntryRels(corEntryId);
	}

	public static void deleteCOREntryRels(String className, long corEntryId)
		throws PortalException {

		getService().deleteCOREntryRels(className, corEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.order.rule.model.impl.COREntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.order.rule.model.impl.COREntryRelModelImpl</code>.
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

	public static COREntryRel fetchCOREntryRel(long COREntryRelId) {
		return getService().fetchCOREntryRel(COREntryRelId);
	}

	public static List<COREntryRel> getAccountEntryCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return getService().getAccountEntryCOREntryRels(
			corEntryId, keywords, start, end);
	}

	public static int getAccountEntryCOREntryRelsCount(
		long corEntryId, String keywords) {

		return getService().getAccountEntryCOREntryRelsCount(
			corEntryId, keywords);
	}

	public static List<COREntryRel> getAccountGroupCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return getService().getAccountGroupCOREntryRels(
			corEntryId, keywords, start, end);
	}

	public static int getAccountGroupCOREntryRelsCount(
		long corEntryId, String keywords) {

		return getService().getAccountGroupCOREntryRelsCount(
			corEntryId, keywords);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<COREntryRel> getCommerceChannelCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return getService().getCommerceChannelCOREntryRels(
			corEntryId, keywords, start, end);
	}

	public static int getCommerceChannelCOREntryRelsCount(
		long corEntryId, String keywords) {

		return getService().getCommerceChannelCOREntryRelsCount(
			corEntryId, keywords);
	}

	public static List<COREntryRel> getCommerceOrderTypeCOREntryRels(
		long corEntryId, String keywords, int start, int end) {

		return getService().getCommerceOrderTypeCOREntryRels(
			corEntryId, keywords, start, end);
	}

	public static int getCommerceOrderTypeCOREntryRelsCount(
		long corEntryId, String keywords) {

		return getService().getCommerceOrderTypeCOREntryRelsCount(
			corEntryId, keywords);
	}

	/**
	 * Returns the cor entry rel with the primary key.
	 *
	 * @param COREntryRelId the primary key of the cor entry rel
	 * @return the cor entry rel
	 * @throws PortalException if a cor entry rel with the primary key could not be found
	 */
	public static COREntryRel getCOREntryRel(long COREntryRelId)
		throws PortalException {

		return getService().getCOREntryRel(COREntryRelId);
	}

	/**
	 * Returns a range of all the cor entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.order.rule.model.impl.COREntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cor entry rels
	 * @param end the upper bound of the range of cor entry rels (not inclusive)
	 * @return the range of cor entry rels
	 */
	public static List<COREntryRel> getCOREntryRels(int start, int end) {
		return getService().getCOREntryRels(start, end);
	}

	public static List<COREntryRel> getCOREntryRels(long corEntryId) {
		return getService().getCOREntryRels(corEntryId);
	}

	public static List<COREntryRel> getCOREntryRels(
		long corEntryId, int start, int end,
		OrderByComparator<COREntryRel> orderByComparator) {

		return getService().getCOREntryRels(
			corEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cor entry rels.
	 *
	 * @return the number of cor entry rels
	 */
	public static int getCOREntryRelsCount() {
		return getService().getCOREntryRelsCount();
	}

	public static int getCOREntryRelsCount(long corEntryId) {
		return getService().getCOREntryRelsCount(corEntryId);
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
	 * Updates the cor entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param corEntryRel the cor entry rel
	 * @return the cor entry rel that was updated
	 */
	public static COREntryRel updateCOREntryRel(COREntryRel corEntryRel) {
		return getService().updateCOREntryRel(corEntryRel);
	}

	public static COREntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<COREntryRelLocalService> _serviceSnapshot =
		new Snapshot<>(
			COREntryRelLocalServiceUtil.class, COREntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2019977395