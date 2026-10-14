/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service;

import com.liferay.analytics.message.storage.model.AnalyticsAssociation;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AnalyticsAssociation. This utility wraps
 * <code>com.liferay.analytics.message.storage.service.impl.AnalyticsAssociationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AnalyticsAssociationLocalService
 * @generated
 */
public class AnalyticsAssociationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.analytics.message.storage.service.impl.AnalyticsAssociationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the analytics association to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociation the analytics association
	 * @return the analytics association that was added
	 */
	public static AnalyticsAssociation addAnalyticsAssociation(
		AnalyticsAssociation analyticsAssociation) {

		return getService().addAnalyticsAssociation(analyticsAssociation);
	}

	public static AnalyticsAssociation addAnalyticsAssociation(
		long companyId, java.util.Date createDate, long userId,
		String associationClassName, long associationClassPK, String className,
		long classPK) {

		return getService().addAnalyticsAssociation(
			companyId, createDate, userId, associationClassName,
			associationClassPK, className, classPK);
	}

	/**
	 * Creates a new analytics association with the primary key. Does not add the analytics association to the database.
	 *
	 * @param analyticsAssociationId the primary key for the new analytics association
	 * @return the new analytics association
	 */
	public static AnalyticsAssociation createAnalyticsAssociation(
		long analyticsAssociationId) {

		return getService().createAnalyticsAssociation(analyticsAssociationId);
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
	 * Deletes the analytics association from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociation the analytics association
	 * @return the analytics association that was removed
	 */
	public static AnalyticsAssociation deleteAnalyticsAssociation(
		AnalyticsAssociation analyticsAssociation) {

		return getService().deleteAnalyticsAssociation(analyticsAssociation);
	}

	/**
	 * Deletes the analytics association with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association that was removed
	 * @throws PortalException if a analytics association with the primary key could not be found
	 */
	public static AnalyticsAssociation deleteAnalyticsAssociation(
			long analyticsAssociationId)
		throws PortalException {

		return getService().deleteAnalyticsAssociation(analyticsAssociationId);
	}

	public static void deleteAnalyticsAssociations(long companyId) {
		getService().deleteAnalyticsAssociations(companyId);
	}

	public static void deleteAnalyticsAssociations(
		long companyId, java.util.Date modifiedDate) {

		getService().deleteAnalyticsAssociations(companyId, modifiedDate);
	}

	public static void deleteAnalyticsAssociations(
		long companyId, String associationClassName, long associationClassPK) {

		getService().deleteAnalyticsAssociations(
			companyId, associationClassName, associationClassPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
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

	public static AnalyticsAssociation fetchAnalyticsAssociation(
		long analyticsAssociationId) {

		return getService().fetchAnalyticsAssociation(analyticsAssociationId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the analytics association with the primary key.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association
	 * @throws PortalException if a analytics association with the primary key could not be found
	 */
	public static AnalyticsAssociation getAnalyticsAssociation(
			long analyticsAssociationId)
		throws PortalException {

		return getService().getAnalyticsAssociation(analyticsAssociationId);
	}

	/**
	 * Returns a range of all the analytics associations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of analytics associations
	 */
	public static List<AnalyticsAssociation> getAnalyticsAssociations(
		int start, int end) {

		return getService().getAnalyticsAssociations(start, end);
	}

	public static List<AnalyticsAssociation> getAnalyticsAssociations(
		long companyId, java.util.Date modifiedDate,
		String associationClassName, int start, int end) {

		return getService().getAnalyticsAssociations(
			companyId, modifiedDate, associationClassName, start, end);
	}

	/**
	 * Returns the number of analytics associations.
	 *
	 * @return the number of analytics associations
	 */
	public static int getAnalyticsAssociationsCount() {
		return getService().getAnalyticsAssociationsCount();
	}

	public static int getAnalyticsAssociationsCount(
		long companyId, java.util.Date modifiedDate,
		String associationClassName) {

		return getService().getAnalyticsAssociationsCount(
			companyId, modifiedDate, associationClassName);
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
	 * Updates the analytics association in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociation the analytics association
	 * @return the analytics association that was updated
	 */
	public static AnalyticsAssociation updateAnalyticsAssociation(
		AnalyticsAssociation analyticsAssociation) {

		return getService().updateAnalyticsAssociation(analyticsAssociation);
	}

	public static AnalyticsAssociationLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AnalyticsAssociationLocalService>
		_serviceSnapshot = new Snapshot<>(
			AnalyticsAssociationLocalServiceUtil.class,
			AnalyticsAssociationLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:787486988