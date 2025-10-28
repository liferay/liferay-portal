/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for OpenIdConnectUser. This utility wraps
 * <code>com.liferay.portal.security.sso.openid.connect.persistence.service.impl.OpenIdConnectUserLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Arthur Chan
 * @see OpenIdConnectUserLocalService
 * @generated
 */
public class OpenIdConnectUserLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.security.sso.openid.connect.persistence.service.impl.OpenIdConnectUserLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OpenIdConnectUser addOpenIdConnectUser(
			long userId, String issuer, String subject)
		throws PortalException {

		return getService().addOpenIdConnectUser(userId, issuer, subject);
	}

	/**
	 * Adds the open ID connect user to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUser the open ID connect user
	 * @return the open ID connect user that was added
	 */
	public static OpenIdConnectUser addOpenIdConnectUser(
		OpenIdConnectUser openIdConnectUser) {

		return getService().addOpenIdConnectUser(openIdConnectUser);
	}

	/**
	 * Creates a new open ID connect user with the primary key. Does not add the open ID connect user to the database.
	 *
	 * @param openIdConnectUserId the primary key for the new open ID connect user
	 * @return the new open ID connect user
	 */
	public static OpenIdConnectUser createOpenIdConnectUser(
		long openIdConnectUserId) {

		return getService().createOpenIdConnectUser(openIdConnectUserId);
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
	 * Deletes the open ID connect user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user that was removed
	 * @throws PortalException if a open ID connect user with the primary key could not be found
	 */
	public static OpenIdConnectUser deleteOpenIdConnectUser(
			long openIdConnectUserId)
		throws PortalException {

		return getService().deleteOpenIdConnectUser(openIdConnectUserId);
	}

	/**
	 * Deletes the open ID connect user from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUser the open ID connect user
	 * @return the open ID connect user that was removed
	 */
	public static OpenIdConnectUser deleteOpenIdConnectUser(
		OpenIdConnectUser openIdConnectUser) {

		return getService().deleteOpenIdConnectUser(openIdConnectUser);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl</code>.
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

	public static OpenIdConnectUser fetchOpenIdConnectUser(
		long openIdConnectUserId) {

		return getService().fetchOpenIdConnectUser(openIdConnectUserId);
	}

	public static OpenIdConnectUser fetchOpenIdConnectUser(
		long companyId, String issuer, String subject) {

		return getService().fetchOpenIdConnectUser(companyId, issuer, subject);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the open ID connect user with the primary key.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user
	 * @throws PortalException if a open ID connect user with the primary key could not be found
	 */
	public static OpenIdConnectUser getOpenIdConnectUser(
			long openIdConnectUserId)
		throws PortalException {

		return getService().getOpenIdConnectUser(openIdConnectUserId);
	}

	/**
	 * Returns a range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @return the range of open ID connect users
	 */
	public static List<OpenIdConnectUser> getOpenIdConnectUsers(
		int start, int end) {

		return getService().getOpenIdConnectUsers(start, end);
	}

	/**
	 * Returns the number of open ID connect users.
	 *
	 * @return the number of open ID connect users
	 */
	public static int getOpenIdConnectUsersCount() {
		return getService().getOpenIdConnectUsersCount();
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
	 * Updates the open ID connect user in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUser the open ID connect user
	 * @return the open ID connect user that was updated
	 */
	public static OpenIdConnectUser updateOpenIdConnectUser(
		OpenIdConnectUser openIdConnectUser) {

		return getService().updateOpenIdConnectUser(openIdConnectUser);
	}

	public static OpenIdConnectUserLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OpenIdConnectUserLocalService>
		_serviceSnapshot = new Snapshot<>(
			OpenIdConnectUserLocalServiceUtil.class,
			OpenIdConnectUserLocalService.class);

}