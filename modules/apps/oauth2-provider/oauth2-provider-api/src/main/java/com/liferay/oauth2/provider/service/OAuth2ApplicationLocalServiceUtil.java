/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for OAuth2Application. This utility wraps
 * <code>com.liferay.oauth2.provider.service.impl.OAuth2ApplicationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ApplicationLocalService
 * @generated
 */
public class OAuth2ApplicationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.oauth2.provider.service.impl.OAuth2ApplicationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static OAuth2Application addOAuth2Application(
			long companyId, long userId, String userName,
			List<com.liferay.oauth2.provider.constants.GrantType>
				allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication,
			java.util.function.Consumer
				<com.liferay.oauth2.provider.util.builder.OAuth2ScopeBuilder>
					builderConsumer,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOAuth2Application(
			companyId, userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, trustedApplication, builderConsumer,
			serviceContext);
	}

	public static OAuth2Application addOAuth2Application(
			long companyId, long userId, String userName,
			List<com.liferay.oauth2.provider.constants.GrantType>
				allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, List<String> scopeAliasesList,
			boolean trustedApplication,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOAuth2Application(
			companyId, userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, scopeAliasesList, trustedApplication,
			serviceContext);
	}

	/**
	 * Adds the o auth2 application to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Application the o auth2 application
	 * @return the o auth2 application that was added
	 */
	public static OAuth2Application addOAuth2Application(
		OAuth2Application oAuth2Application) {

		return getService().addOAuth2Application(oAuth2Application);
	}

	public static OAuth2Application addOrUpdateOAuth2Application(
			String externalReferenceCode, long userId, String userName,
			List<com.liferay.oauth2.provider.constants.GrantType>
				allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication,
			java.util.function.Consumer
				<com.liferay.oauth2.provider.util.builder.OAuth2ScopeBuilder>
					builderConsumer,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateOAuth2Application(
			externalReferenceCode, userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, trustedApplication, builderConsumer,
			serviceContext);
	}

	public static OAuth2Application addOrUpdateOAuth2Application(
			String externalReferenceCode, long userId, String userName,
			List<com.liferay.oauth2.provider.constants.GrantType>
				allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, List<String> scopeAliasesList,
			boolean trustedApplication,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateOAuth2Application(
			externalReferenceCode, userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, scopeAliasesList, trustedApplication,
			serviceContext);
	}

	/**
	 * Creates a new o auth2 application with the primary key. Does not add the o auth2 application to the database.
	 *
	 * @param oAuth2ApplicationId the primary key for the new o auth2 application
	 * @return the new o auth2 application
	 */
	public static OAuth2Application createOAuth2Application(
		long oAuth2ApplicationId) {

		return getService().createOAuth2Application(oAuth2ApplicationId);
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
	 * Deletes the o auth2 application with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2ApplicationId the primary key of the o auth2 application
	 * @return the o auth2 application that was removed
	 * @throws PortalException if a o auth2 application with the primary key could not be found
	 */
	public static OAuth2Application deleteOAuth2Application(
			long oAuth2ApplicationId)
		throws PortalException {

		return getService().deleteOAuth2Application(oAuth2ApplicationId);
	}

	/**
	 * Deletes the o auth2 application from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Application the o auth2 application
	 * @return the o auth2 application that was removed
	 * @throws PortalException
	 */
	public static OAuth2Application deleteOAuth2Application(
			OAuth2Application oAuth2Application)
		throws PortalException {

		return getService().deleteOAuth2Application(oAuth2Application);
	}

	public static void deleteOAuth2Applications(long companyId)
		throws PortalException {

		getService().deleteOAuth2Applications(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ApplicationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ApplicationModelImpl</code>.
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

	public static OAuth2Application fetchOAuth2Application(
		long oAuth2ApplicationId) {

		return getService().fetchOAuth2Application(oAuth2ApplicationId);
	}

	public static OAuth2Application fetchOAuth2Application(
		long companyId, String clientId) {

		return getService().fetchOAuth2Application(companyId, clientId);
	}

	public static OAuth2Application
		fetchOAuth2ApplicationByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchOAuth2ApplicationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the o auth2 application with the matching UUID and company.
	 *
	 * @param uuid the o auth2 application's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth2 application, or <code>null</code> if a matching o auth2 application could not be found
	 */
	public static OAuth2Application fetchOAuth2ApplicationByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchOAuth2ApplicationByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the o auth2 application with the primary key.
	 *
	 * @param oAuth2ApplicationId the primary key of the o auth2 application
	 * @return the o auth2 application
	 * @throws PortalException if a o auth2 application with the primary key could not be found
	 */
	public static OAuth2Application getOAuth2Application(
			long oAuth2ApplicationId)
		throws PortalException {

		return getService().getOAuth2Application(oAuth2ApplicationId);
	}

	public static OAuth2Application getOAuth2Application(
			long companyId, String clientId)
		throws com.liferay.oauth2.provider.exception.
			NoSuchOAuth2ApplicationException {

		return getService().getOAuth2Application(companyId, clientId);
	}

	public static OAuth2Application getOAuth2ApplicationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getOAuth2ApplicationByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the o auth2 application with the matching UUID and company.
	 *
	 * @param uuid the o auth2 application's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth2 application
	 * @throws PortalException if a matching o auth2 application could not be found
	 */
	public static OAuth2Application getOAuth2ApplicationByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getOAuth2ApplicationByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the o auth2 applications.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2ApplicationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth2 applications
	 * @param end the upper bound of the range of o auth2 applications (not inclusive)
	 * @return the range of o auth2 applications
	 */
	public static List<OAuth2Application> getOAuth2Applications(
		int start, int end) {

		return getService().getOAuth2Applications(start, end);
	}

	public static List<OAuth2Application> getOAuth2Applications(
		long companyId) {

		return getService().getOAuth2Applications(companyId);
	}

	public static List<OAuth2Application> getOAuth2Applications(
		long companyId, int clientProfile) {

		return getService().getOAuth2Applications(companyId, clientProfile);
	}

	/**
	 * Returns the number of o auth2 applications.
	 *
	 * @return the number of o auth2 applications
	 */
	public static int getOAuth2ApplicationsCount() {
		return getService().getOAuth2ApplicationsCount();
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

	public static String resolveClientSecret(
			OAuth2Application oAuth2Application)
		throws PortalException {

		return getService().resolveClientSecret(oAuth2Application);
	}

	public static OAuth2Application updateExternalReferenceCode(
			long oAuth2ApplicationId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			oAuth2ApplicationId, externalReferenceCode);
	}

	public static OAuth2Application updateExternalReferenceCode(
			OAuth2Application oAuth2Application, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			oAuth2Application, externalReferenceCode);
	}

	public static OAuth2Application updateIcon(
			long oAuth2ApplicationId, InputStream inputStream)
		throws PortalException {

		return getService().updateIcon(oAuth2ApplicationId, inputStream);
	}

	public static OAuth2Application updateOAuth2Application(
			long oAuth2ApplicationId, long oAuth2ApplicationScopeAliasesId,
			List<com.liferay.oauth2.provider.constants.GrantType>
				allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication)
		throws PortalException {

		return getService().updateOAuth2Application(
			oAuth2ApplicationId, oAuth2ApplicationScopeAliasesId,
			allowedGrantTypesList, clientAuthenticationMethod,
			clientCredentialUserId, clientId, clientProfile, clientSecret,
			description, featuresList, homePageURL, iconFileEntryId, jwks, name,
			privacyPolicyURL, redirectURIsList, rememberDevice,
			trustedApplication);
	}

	/**
	 * Updates the o auth2 application in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuth2ApplicationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuth2Application the o auth2 application
	 * @return the o auth2 application that was updated
	 */
	public static OAuth2Application updateOAuth2Application(
		OAuth2Application oAuth2Application) {

		return getService().updateOAuth2Application(oAuth2Application);
	}

	public static OAuth2Application updateScopeAliases(
			long userId, String userName, long oAuth2ApplicationId,
			List<String> scopeAliasesList)
		throws PortalException {

		return getService().updateScopeAliases(
			userId, userName, oAuth2ApplicationId, scopeAliasesList);
	}

	public static OAuth2ApplicationLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<OAuth2ApplicationLocalService>
		_serviceSnapshot = new Snapshot<>(
			OAuth2ApplicationLocalServiceUtil.class,
			OAuth2ApplicationLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1893607907