/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for OAuth2ApplicationScopeAliases. This utility wraps
 * <code>com.liferay.oauth2.provider.service.impl.OAuth2ApplicationScopeAliasesLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ApplicationScopeAliasesLocalService
 * @generated
 */
public class OAuth2ApplicationScopeAliasesLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth2.provider.service.impl.OAuth2ApplicationScopeAliasesLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuth2ApplicationScopeAliases
			addOAuth2ApplicationScopeAliases(
				long companyId, long userId, String userName,
				long oAuth2ApplicationId,
				java.util.function.Consumer
					<com.liferay.oauth2.provider.util.builder.
						OAuth2ScopeBuilder> builderConsumer)
		throws PortalException {

		return getService().addOAuth2ApplicationScopeAliases(
			companyId, userId, userName, oAuth2ApplicationId, builderConsumer);
	}

	public static OAuth2ApplicationScopeAliases
			addOAuth2ApplicationScopeAliases(
				long companyId, long userId, String userName,
				long oAuth2ApplicationId, List<String> scopeAliasesList)
		throws PortalException {

		return getService().addOAuth2ApplicationScopeAliases(
			companyId, userId, userName, oAuth2ApplicationId, scopeAliasesList);
	}

	/**
	 * Adds the o auth2 application scope aliases to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationScopeAliasesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ApplicationScopeAliases the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases that was added
	 */
	public static OAuth2ApplicationScopeAliases
		addOAuth2ApplicationScopeAliases(
			OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases) {

		return getService().addOAuth2ApplicationScopeAliases(
			oAuth2ApplicationScopeAliases);
	}

	/**
	 * Creates a new o auth2 application scope aliases with the primary key. Does not add the o auth2 application scope aliases to the database.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the primary key for the new o auth2 application scope aliases
	 * @return the new o auth2 application scope aliases
	 */
	public static OAuth2ApplicationScopeAliases
		createOAuth2ApplicationScopeAliases(
			long oAuth2ApplicationScopeAliasesId) {

		return getService().createOAuth2ApplicationScopeAliases(
			oAuth2ApplicationScopeAliasesId);
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
	 * Deletes the o auth2 application scope aliases with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationScopeAliasesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ApplicationScopeAliasesId the primary key of the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases that was removed
	 * @throws PortalException if a o auth2 application scope aliases with the primary key could not be found
	 */
	public static OAuth2ApplicationScopeAliases
			deleteOAuth2ApplicationScopeAliases(
				long oAuth2ApplicationScopeAliasesId)
		throws PortalException {

		return getService().deleteOAuth2ApplicationScopeAliases(
			oAuth2ApplicationScopeAliasesId);
	}

	/**
	 * Deletes the o auth2 application scope aliases from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationScopeAliasesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ApplicationScopeAliases the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases that was removed
	 */
	public static OAuth2ApplicationScopeAliases
		deleteOAuth2ApplicationScopeAliases(
			OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases) {

		return getService().deleteOAuth2ApplicationScopeAliases(
			oAuth2ApplicationScopeAliases);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ApplicationScopeAliasesModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ApplicationScopeAliasesModelImpl</code>.
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

	public static OAuth2ApplicationScopeAliases
		fetchOAuth2ApplicationScopeAliases(
			long oAuth2ApplicationScopeAliasesId) {

		return getService().fetchOAuth2ApplicationScopeAliases(
			oAuth2ApplicationScopeAliasesId);
	}

	public static OAuth2ApplicationScopeAliases
		fetchOAuth2ApplicationScopeAliases(
			long oAuth2ApplicationId, List<String> scopeAliasesList) {

		return getService().fetchOAuth2ApplicationScopeAliases(
			oAuth2ApplicationId, scopeAliasesList);
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
	 * Returns the o auth2 application scope aliases with the primary key.
	 *
	 * @param oAuth2ApplicationScopeAliasesId the primary key of the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases
	 * @throws PortalException if a o auth2 application scope aliases with the primary key could not be found
	 */
	public static OAuth2ApplicationScopeAliases
			getOAuth2ApplicationScopeAliases(
				long oAuth2ApplicationScopeAliasesId)
		throws PortalException {

		return getService().getOAuth2ApplicationScopeAliases(
			oAuth2ApplicationScopeAliasesId);
	}

	/**
	 * Returns a range of all the o auth2 application scope aliaseses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ApplicationScopeAliasesModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth2 application scope aliaseses
	 * @param end the upper bound of the range of o auth2 application scope aliaseses (not inclusive)
	 * @return the range of o auth2 application scope aliaseses
	 */
	public static List<OAuth2ApplicationScopeAliases>
		getOAuth2ApplicationScopeAliaseses(int start, int end) {

		return getService().getOAuth2ApplicationScopeAliaseses(start, end);
	}

	/**
	 * Returns the number of o auth2 application scope aliaseses.
	 *
	 * @return the number of o auth2 application scope aliaseses
	 */
	public static int getOAuth2ApplicationScopeAliasesesCount() {
		return getService().getOAuth2ApplicationScopeAliasesesCount();
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

	public static List<String> getScopeAliasesList(
		long oAuth2ApplicationScopeAliasesId) {

		return getService().getScopeAliasesList(
			oAuth2ApplicationScopeAliasesId);
	}

	/**
	 * Updates the o auth2 application scope aliases in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationScopeAliasesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ApplicationScopeAliases the o auth2 application scope aliases
	 * @return the o auth2 application scope aliases that was updated
	 */
	public static OAuth2ApplicationScopeAliases
		updateOAuth2ApplicationScopeAliases(
			OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases) {

		return getService().updateOAuth2ApplicationScopeAliases(
			oAuth2ApplicationScopeAliases);
	}

	public static OAuth2ApplicationScopeAliasesLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuth2ApplicationScopeAliasesLocalService>
		_serviceSnapshot = new Snapshot<>(
			OAuth2ApplicationScopeAliasesLocalServiceUtil.class,
			OAuth2ApplicationScopeAliasesLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-260102327