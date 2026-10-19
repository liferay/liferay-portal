/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for OAuth2Authorization. This utility wraps
 * <code>com.liferay.oauth2.provider.service.impl.OAuth2AuthorizationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2AuthorizationLocalService
 * @generated
 */
public class OAuth2AuthorizationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth2.provider.service.impl.OAuth2AuthorizationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuth2Authorization addOAuth2Authorization(
		long companyId, long userId, String userName, long oAuth2ApplicationId,
		long oAuth2ApplicationScopeAliasesId, String accessTokenContent,
		java.util.Date accessTokenCreateDate,
		java.util.Date accessTokenExpirationDate, List<String> audiencesList,
		String remoteHostInfo, String remoteIPInfo, String refreshTokenContent,
		java.util.Date refreshTokenCreateDate,
		java.util.Date refreshTokenExpirationDate) {

		return getService().addOAuth2Authorization(
			companyId, userId, userName, oAuth2ApplicationId,
			oAuth2ApplicationScopeAliasesId, accessTokenContent,
			accessTokenCreateDate, accessTokenExpirationDate, audiencesList,
			remoteHostInfo, remoteIPInfo, refreshTokenContent,
			refreshTokenCreateDate, refreshTokenExpirationDate);
	}

	/**
	 * Adds the o auth2 authorization to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Authorization the o auth2 authorization
	 * @return the o auth2 authorization that was added
	 */
	public static OAuth2Authorization addOAuth2Authorization(
		OAuth2Authorization oAuth2Authorization) {

		return getService().addOAuth2Authorization(oAuth2Authorization);
	}

	public static boolean addOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId) {

		return getService().addOAuth2ScopeGrantOAuth2Authorization(
			oAuth2ScopeGrantId, oAuth2AuthorizationId);
	}

	public static boolean addOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, OAuth2Authorization oAuth2Authorization) {

		return getService().addOAuth2ScopeGrantOAuth2Authorization(
			oAuth2ScopeGrantId, oAuth2Authorization);
	}

	public static boolean addOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId,
		List<OAuth2Authorization> oAuth2Authorizations) {

		return getService().addOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId, oAuth2Authorizations);
	}

	public static boolean addOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds) {

		return getService().addOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId, oAuth2AuthorizationIds);
	}

	public static void clearOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId) {

		getService().clearOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId);
	}

	/**
	 * Creates a new o auth2 authorization with the primary key. Does not add the o auth2 authorization to the database.
	 *
	 * @param oAuth2AuthorizationId the primary key for the new o auth2 authorization
	 * @return the new o auth2 authorization
	 */
	public static OAuth2Authorization createOAuth2Authorization(
		long oAuth2AuthorizationId) {

		return getService().createOAuth2Authorization(oAuth2AuthorizationId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteExpiredOAuth2Authorizations()
		throws PortalException {

		getService().deleteExpiredOAuth2Authorizations();
	}

	/**
	 * Deletes the o auth2 authorization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization that was removed
	 * @throws PortalException if a o auth2 authorization with the primary key could not be found
	 */
	public static OAuth2Authorization deleteOAuth2Authorization(
			long oAuth2AuthorizationId)
		throws PortalException {

		return getService().deleteOAuth2Authorization(oAuth2AuthorizationId);
	}

	/**
	 * Deletes the o auth2 authorization from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Authorization the o auth2 authorization
	 * @return the o auth2 authorization that was removed
	 */
	public static OAuth2Authorization deleteOAuth2Authorization(
		OAuth2Authorization oAuth2Authorization) {

		return getService().deleteOAuth2Authorization(oAuth2Authorization);
	}

	public static void deleteOAuth2Authorizations(
		long userId, long oAuth2ApplicationId) {

		getService().deleteOAuth2Authorizations(userId, oAuth2ApplicationId);
	}

	public static void deleteOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId) {

		getService().deleteOAuth2ScopeGrantOAuth2Authorization(
			oAuth2ScopeGrantId, oAuth2AuthorizationId);
	}

	public static void deleteOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, OAuth2Authorization oAuth2Authorization) {

		getService().deleteOAuth2ScopeGrantOAuth2Authorization(
			oAuth2ScopeGrantId, oAuth2Authorization);
	}

	public static void deleteOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId,
		List<OAuth2Authorization> oAuth2Authorizations) {

		getService().deleteOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId, oAuth2Authorizations);
	}

	public static void deleteOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds) {

		getService().deleteOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId, oAuth2AuthorizationIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
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

	public static OAuth2Authorization fetchOAuth2Authorization(
		long oAuth2AuthorizationId) {

		return getService().fetchOAuth2Authorization(oAuth2AuthorizationId);
	}

	public static OAuth2Authorization
		fetchOAuth2AuthorizationByAccessTokenContent(
			String accessTokenContent) {

		return getService().fetchOAuth2AuthorizationByAccessTokenContent(
			accessTokenContent);
	}

	public static OAuth2Authorization
		fetchOAuth2AuthorizationByRefreshTokenContent(
			String refreshTokenContent) {

		return getService().fetchOAuth2AuthorizationByRefreshTokenContent(
			refreshTokenContent);
	}

	public static OAuth2Authorization
		fetchOAuth2AuthorizationByRememberDeviceContent(
			long userId, long oAuth2ApplicationId,
			String rememberDeviceContent) {

		return getService().fetchOAuth2AuthorizationByRememberDeviceContent(
			userId, oAuth2ApplicationId, rememberDeviceContent);
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
	 * Returns the o auth2 authorization with the primary key.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization
	 * @throws PortalException if a o auth2 authorization with the primary key could not be found
	 */
	public static OAuth2Authorization getOAuth2Authorization(
			long oAuth2AuthorizationId)
		throws PortalException {

		return getService().getOAuth2Authorization(oAuth2AuthorizationId);
	}

	public static OAuth2Authorization
			getOAuth2AuthorizationByAccessTokenContent(
				String accessTokenContent)
		throws com.liferay.oauth2.provider.exception.
			NoSuchOAuth2AuthorizationException {

		return getService().getOAuth2AuthorizationByAccessTokenContent(
			accessTokenContent);
	}

	public static OAuth2Authorization
			getOAuth2AuthorizationByRefreshTokenContent(
				String refreshTokenContent)
		throws com.liferay.oauth2.provider.exception.
			NoSuchOAuth2AuthorizationException {

		return getService().getOAuth2AuthorizationByRefreshTokenContent(
			refreshTokenContent);
	}

	/**
	 * Returns a range of all the o auth2 authorizations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of o auth2 authorizations
	 */
	public static List<OAuth2Authorization> getOAuth2Authorizations(
		int start, int end) {

		return getService().getOAuth2Authorizations(start, end);
	}

	public static List<OAuth2Authorization> getOAuth2Authorizations(
		long oAuth2ApplicationId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return getService().getOAuth2Authorizations(
			oAuth2ApplicationId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of o auth2 authorizations.
	 *
	 * @return the number of o auth2 authorizations
	 */
	public static int getOAuth2AuthorizationsCount() {
		return getService().getOAuth2AuthorizationsCount();
	}

	public static int getOAuth2AuthorizationsCount(long oAuth2ApplicationId) {
		return getService().getOAuth2AuthorizationsCount(oAuth2ApplicationId);
	}

	public static List<OAuth2Authorization>
		getOAuth2ScopeGrantOAuth2Authorizations(long oAuth2ScopeGrantId) {

		return getService().getOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId);
	}

	public static List<OAuth2Authorization>
		getOAuth2ScopeGrantOAuth2Authorizations(
			long oAuth2ScopeGrantId, int start, int end) {

		return getService().getOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId, start, end);
	}

	public static List<OAuth2Authorization>
		getOAuth2ScopeGrantOAuth2Authorizations(
			long oAuth2ScopeGrantId, int start, int end,
			OrderByComparator<OAuth2Authorization> orderByComparator) {

		return getService().getOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId, start, end, orderByComparator);
	}

	public static int getOAuth2ScopeGrantOAuth2AuthorizationsCount(
		long oAuth2ScopeGrantId) {

		return getService().getOAuth2ScopeGrantOAuth2AuthorizationsCount(
			oAuth2ScopeGrantId);
	}

	/**
	 * Returns the oAuth2ScopeGrantIds of the o auth2 scope grants associated with the o auth2 authorization.
	 *
	 * @param oAuth2AuthorizationId the oAuth2AuthorizationId of the o auth2 authorization
	 * @return long[] the oAuth2ScopeGrantIds of o auth2 scope grants associated with the o auth2 authorization
	 */
	public static long[] getOAuth2ScopeGrantPrimaryKeys(
		long oAuth2AuthorizationId) {

		return getService().getOAuth2ScopeGrantPrimaryKeys(
			oAuth2AuthorizationId);
	}

	public static java.util.Collection
		<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			getOAuth2ScopeGrants(long oAuth2AuthorizationId) {

		return getService().getOAuth2ScopeGrants(oAuth2AuthorizationId);
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

	public static List<OAuth2Authorization> getUserOAuth2Authorizations(
		long userId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return getService().getUserOAuth2Authorizations(
			userId, start, end, orderByComparator);
	}

	public static int getUserOAuth2AuthorizationsCount(long userId) {
		return getService().getUserOAuth2AuthorizationsCount(userId);
	}

	public static boolean hasOAuth2ScopeGrantOAuth2Authorization(
		long oAuth2ScopeGrantId, long oAuth2AuthorizationId) {

		return getService().hasOAuth2ScopeGrantOAuth2Authorization(
			oAuth2ScopeGrantId, oAuth2AuthorizationId);
	}

	public static boolean hasOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId) {

		return getService().hasOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId);
	}

	public static void setOAuth2ScopeGrantOAuth2Authorizations(
		long oAuth2ScopeGrantId, long[] oAuth2AuthorizationIds) {

		getService().setOAuth2ScopeGrantOAuth2Authorizations(
			oAuth2ScopeGrantId, oAuth2AuthorizationIds);
	}

	/**
	 * Updates the o auth2 authorization in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2AuthorizationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Authorization the o auth2 authorization
	 * @return the o auth2 authorization that was updated
	 */
	public static OAuth2Authorization updateOAuth2Authorization(
		OAuth2Authorization oAuth2Authorization) {

		return getService().updateOAuth2Authorization(oAuth2Authorization);
	}

	public static OAuth2Authorization updateRememberDeviceContent(
		String refreshTokenContent, String rememberDeviceContent) {

		return getService().updateRememberDeviceContent(
			refreshTokenContent, rememberDeviceContent);
	}

	public static OAuth2AuthorizationLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuth2AuthorizationLocalService>
		_serviceSnapshot = new Snapshot<>(
			OAuth2AuthorizationLocalServiceUtil.class,
			OAuth2AuthorizationLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-300178827