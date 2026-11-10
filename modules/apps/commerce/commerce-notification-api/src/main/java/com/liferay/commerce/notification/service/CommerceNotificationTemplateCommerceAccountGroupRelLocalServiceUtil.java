/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.commerce.notification.model.CommerceNotificationTemplateCommerceAccountGroupRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceNotificationTemplateCommerceAccountGroupRel. This utility wraps
 * <code>com.liferay.commerce.notification.service.impl.CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationTemplateCommerceAccountGroupRelLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class
	CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.notification.service.impl.CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce notification template commerce account group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRel the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was added
	 */
	public static CommerceNotificationTemplateCommerceAccountGroupRel
		addCommerceNotificationTemplateCommerceAccountGroupRel(
			CommerceNotificationTemplateCommerceAccountGroupRel
				commerceNotificationTemplateCommerceAccountGroupRel) {

		return getService().
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRel);
	}

	public static CommerceNotificationTemplateCommerceAccountGroupRel
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				long commerceNotificationTemplateId,
				long commerceAccountGroupId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateId, commerceAccountGroupId,
				serviceContext);
	}

	/**
	 * Creates a new commerce notification template commerce account group rel with the primary key. Does not add the commerce notification template commerce account group rel to the database.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key for the new commerce notification template commerce account group rel
	 * @return the new commerce notification template commerce account group rel
	 */
	public static CommerceNotificationTemplateCommerceAccountGroupRel
		createCommerceNotificationTemplateCommerceAccountGroupRel(
			long commerceNotificationTemplateCommerceAccountGroupRelId) {

		return getService().
			createCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void
		deleteCNTemplateCommerceAccountGroupRelsByCommerceNotificationTemplateId(
			long commerceNotificationTemplateId) {

		getService().
			deleteCNTemplateCommerceAccountGroupRelsByCommerceNotificationTemplateId(
				commerceNotificationTemplateId);
	}

	/**
	 * Deletes the commerce notification template commerce account group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRel the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was removed
	 */
	public static CommerceNotificationTemplateCommerceAccountGroupRel
		deleteCommerceNotificationTemplateCommerceAccountGroupRel(
			CommerceNotificationTemplateCommerceAccountGroupRel
				commerceNotificationTemplateCommerceAccountGroupRel) {

		return getService().
			deleteCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRel);
	}

	/**
	 * Deletes the commerce notification template commerce account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key of the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was removed
	 * @throws PortalException if a commerce notification template commerce account group rel with the primary key could not be found
	 */
	public static CommerceNotificationTemplateCommerceAccountGroupRel
			deleteCommerceNotificationTemplateCommerceAccountGroupRel(
				long commerceNotificationTemplateCommerceAccountGroupRelId)
		throws PortalException {

		return getService().
			deleteCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
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

	public static CommerceNotificationTemplateCommerceAccountGroupRel
		fetchCommerceNotificationTemplateCommerceAccountGroupRel(
			long commerceNotificationTemplateCommerceAccountGroupRelId) {

		return getService().
			fetchCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce notification template commerce account group rel with the primary key.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key of the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel
	 * @throws PortalException if a commerce notification template commerce account group rel with the primary key could not be found
	 */
	public static CommerceNotificationTemplateCommerceAccountGroupRel
			getCommerceNotificationTemplateCommerceAccountGroupRel(
				long commerceNotificationTemplateCommerceAccountGroupRelId)
		throws PortalException {

		return getService().
			getCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	/**
	 * Returns a range of all the commerce notification template commerce account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification template commerce account group rels
	 * @param end the upper bound of the range of commerce notification template commerce account group rels (not inclusive)
	 * @return the range of commerce notification template commerce account group rels
	 */
	public static List<CommerceNotificationTemplateCommerceAccountGroupRel>
		getCommerceNotificationTemplateCommerceAccountGroupRels(
			int start, int end) {

		return getService().
			getCommerceNotificationTemplateCommerceAccountGroupRels(start, end);
	}

	/**
	 * Returns the number of commerce notification template commerce account group rels.
	 *
	 * @return the number of commerce notification template commerce account group rels
	 */
	public static int
		getCommerceNotificationTemplateCommerceAccountGroupRelsCount() {

		return getService().
			getCommerceNotificationTemplateCommerceAccountGroupRelsCount();
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
	 * Updates the commerce notification template commerce account group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRel the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was updated
	 */
	public static CommerceNotificationTemplateCommerceAccountGroupRel
		updateCommerceNotificationTemplateCommerceAccountGroupRel(
			CommerceNotificationTemplateCommerceAccountGroupRel
				commerceNotificationTemplateCommerceAccountGroupRel) {

		return getService().
			updateCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRel);
	}

	public static
		CommerceNotificationTemplateCommerceAccountGroupRelLocalService
			getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CommerceNotificationTemplateCommerceAccountGroupRelLocalService>
			_serviceSnapshot = new Snapshot<>(
				CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceUtil.class,
				CommerceNotificationTemplateCommerceAccountGroupRelLocalService.
					class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1198733372