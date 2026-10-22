/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for RememberMeToken. This utility wraps
 * <code>com.liferay.portal.service.impl.RememberMeTokenLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RememberMeTokenLocalService
 * @generated
 */
public class RememberMeTokenLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.RememberMeTokenLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static RememberMeToken addRememberMeToken(
			long companyId, long userId, java.util.Date expirationDate,
			java.util.function.Consumer<String> valueConsumer)
		throws com.liferay.portal.kernel.exception.PwdEncryptorException {

		return getService().addRememberMeToken(
			companyId, userId, expirationDate, valueConsumer);
	}

	/**
	 * Adds the remember me token to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeToken the remember me token
	 * @return the remember me token that was added
	 */
	public static RememberMeToken addRememberMeToken(
		RememberMeToken rememberMeToken) {

		return getService().addRememberMeToken(rememberMeToken);
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
	 * Creates a new remember me token with the primary key. Does not add the remember me token to the database.
	 *
	 * @param rememberMeTokenId the primary key for the new remember me token
	 * @return the new remember me token
	 */
	public static RememberMeToken createRememberMeToken(
		long rememberMeTokenId) {

		return getService().createRememberMeToken(rememberMeTokenId);
	}

	public static void deleteExpiredRememberMeTokens(
		java.util.Date expirationDate) {

		getService().deleteExpiredRememberMeTokens(expirationDate);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the remember me token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token that was removed
	 * @throws PortalException if a remember me token with the primary key could not be found
	 */
	public static RememberMeToken deleteRememberMeToken(long rememberMeTokenId)
		throws PortalException {

		return getService().deleteRememberMeToken(rememberMeTokenId);
	}

	/**
	 * Deletes the remember me token from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeToken the remember me token
	 * @return the remember me token that was removed
	 */
	public static RememberMeToken deleteRememberMeToken(
		RememberMeToken rememberMeToken) {

		return getService().deleteRememberMeToken(rememberMeToken);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
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

	public static RememberMeToken fetchRememberMeToken(long rememberMeTokenId) {
		return getService().fetchRememberMeToken(rememberMeTokenId);
	}

	public static RememberMeToken fetchRememberMeToken(
			long rememberMeTokenId, String value)
		throws com.liferay.portal.kernel.exception.PwdEncryptorException {

		return getService().fetchRememberMeToken(rememberMeTokenId, value);
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
	 * Returns the remember me token with the primary key.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token
	 * @throws PortalException if a remember me token with the primary key could not be found
	 */
	public static RememberMeToken getRememberMeToken(long rememberMeTokenId)
		throws PortalException {

		return getService().getRememberMeToken(rememberMeTokenId);
	}

	/**
	 * Returns a range of all the remember me tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @return the range of remember me tokens
	 */
	public static List<RememberMeToken> getRememberMeTokens(
		int start, int end) {

		return getService().getRememberMeTokens(start, end);
	}

	/**
	 * Returns the number of remember me tokens.
	 *
	 * @return the number of remember me tokens
	 */
	public static int getRememberMeTokensCount() {
		return getService().getRememberMeTokensCount();
	}

	/**
	 * Updates the remember me token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeToken the remember me token
	 * @return the remember me token that was updated
	 */
	public static RememberMeToken updateRememberMeToken(
		RememberMeToken rememberMeToken) {

		return getService().updateRememberMeToken(rememberMeToken);
	}

	public static RememberMeTokenLocalService getService() {
		return _service;
	}

	public static void setService(RememberMeTokenLocalService service) {
		_service = service;
	}

	private static volatile RememberMeTokenLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1685794925