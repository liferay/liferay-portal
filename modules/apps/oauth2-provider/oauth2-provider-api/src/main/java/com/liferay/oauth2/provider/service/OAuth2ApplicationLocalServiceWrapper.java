/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link OAuth2ApplicationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ApplicationLocalService
 * @generated
 */
public class OAuth2ApplicationLocalServiceWrapper
	implements OAuth2ApplicationLocalService,
			   ServiceWrapper<OAuth2ApplicationLocalService> {

	public OAuth2ApplicationLocalServiceWrapper() {
		this(null);
	}

	public OAuth2ApplicationLocalServiceWrapper(
		OAuth2ApplicationLocalService oAuth2ApplicationLocalService) {

		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			addOAuth2Application(
				long companyId, long userId, String userName,
				java.util.List<com.liferay.oauth2.provider.constants.GrantType>
					allowedGrantTypesList,
				String clientAuthenticationMethod, long clientCredentialUserId,
				String clientId, int clientProfile, String clientSecret,
				String description, java.util.List<String> featuresList,
				String homePageURL, long iconFileEntryId, String jwks,
				String name, String privacyPolicyURL,
				java.util.List<String> redirectURIsList, boolean rememberDevice,
				boolean trustedApplication,
				java.util.function.Consumer
					<com.liferay.oauth2.provider.util.builder.
						OAuth2ScopeBuilder> builderConsumer,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.addOAuth2Application(
			companyId, userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, trustedApplication, builderConsumer,
			serviceContext);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			addOAuth2Application(
				long companyId, long userId, String userName,
				java.util.List<com.liferay.oauth2.provider.constants.GrantType>
					allowedGrantTypesList,
				String clientAuthenticationMethod, long clientCredentialUserId,
				String clientId, int clientProfile, String clientSecret,
				String description, java.util.List<String> featuresList,
				String homePageURL, long iconFileEntryId, String jwks,
				String name, String privacyPolicyURL,
				java.util.List<String> redirectURIsList, boolean rememberDevice,
				java.util.List<String> scopeAliasesList,
				boolean trustedApplication,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.addOAuth2Application(
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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
		addOAuth2Application(
			com.liferay.oauth2.provider.model.OAuth2Application
				oAuth2Application) {

		return _oAuth2ApplicationLocalService.addOAuth2Application(
			oAuth2Application);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			addOrUpdateOAuth2Application(
				String externalReferenceCode, long userId, String userName,
				java.util.List<com.liferay.oauth2.provider.constants.GrantType>
					allowedGrantTypesList,
				String clientAuthenticationMethod, long clientCredentialUserId,
				String clientId, int clientProfile, String clientSecret,
				String description, java.util.List<String> featuresList,
				String homePageURL, long iconFileEntryId, String jwks,
				String name, String privacyPolicyURL,
				java.util.List<String> redirectURIsList, boolean rememberDevice,
				boolean trustedApplication,
				java.util.function.Consumer
					<com.liferay.oauth2.provider.util.builder.
						OAuth2ScopeBuilder> builderConsumer,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
			externalReferenceCode, userId, userName, allowedGrantTypesList,
			clientAuthenticationMethod, clientCredentialUserId, clientId,
			clientProfile, clientSecret, description, featuresList, homePageURL,
			iconFileEntryId, jwks, name, privacyPolicyURL, redirectURIsList,
			rememberDevice, trustedApplication, builderConsumer,
			serviceContext);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			addOrUpdateOAuth2Application(
				String externalReferenceCode, long userId, String userName,
				java.util.List<com.liferay.oauth2.provider.constants.GrantType>
					allowedGrantTypesList,
				String clientAuthenticationMethod, long clientCredentialUserId,
				String clientId, int clientProfile, String clientSecret,
				String description, java.util.List<String> featuresList,
				String homePageURL, long iconFileEntryId, String jwks,
				String name, String privacyPolicyURL,
				java.util.List<String> redirectURIsList, boolean rememberDevice,
				java.util.List<String> scopeAliasesList,
				boolean trustedApplication,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.addOrUpdateOAuth2Application(
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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
		createOAuth2Application(long oAuth2ApplicationId) {

		return _oAuth2ApplicationLocalService.createOAuth2Application(
			oAuth2ApplicationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			deleteOAuth2Application(long oAuth2ApplicationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.deleteOAuth2Application(
			oAuth2ApplicationId);
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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			deleteOAuth2Application(
				com.liferay.oauth2.provider.model.OAuth2Application
					oAuth2Application)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.deleteOAuth2Application(
			oAuth2Application);
	}

	@Override
	public void deleteOAuth2Applications(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_oAuth2ApplicationLocalService.deleteOAuth2Applications(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _oAuth2ApplicationLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _oAuth2ApplicationLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _oAuth2ApplicationLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _oAuth2ApplicationLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _oAuth2ApplicationLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _oAuth2ApplicationLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _oAuth2ApplicationLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _oAuth2ApplicationLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
		fetchOAuth2Application(long oAuth2ApplicationId) {

		return _oAuth2ApplicationLocalService.fetchOAuth2Application(
			oAuth2ApplicationId);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
		fetchOAuth2Application(long companyId, String clientId) {

		return _oAuth2ApplicationLocalService.fetchOAuth2Application(
			companyId, clientId);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
		fetchOAuth2ApplicationByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _oAuth2ApplicationLocalService.
			fetchOAuth2ApplicationByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the o auth2 application with the matching UUID and company.
	 *
	 * @param uuid the o auth2 application's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth2 application, or <code>null</code> if a matching o auth2 application could not be found
	 */
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
		fetchOAuth2ApplicationByUuidAndCompanyId(String uuid, long companyId) {

		return _oAuth2ApplicationLocalService.
			fetchOAuth2ApplicationByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _oAuth2ApplicationLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _oAuth2ApplicationLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _oAuth2ApplicationLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the o auth2 application with the primary key.
	 *
	 * @param oAuth2ApplicationId the primary key of the o auth2 application
	 * @return the o auth2 application
	 * @throws PortalException if a o auth2 application with the primary key could not be found
	 */
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			getOAuth2Application(long oAuth2ApplicationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.getOAuth2Application(
			oAuth2ApplicationId);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			getOAuth2Application(long companyId, String clientId)
		throws com.liferay.oauth2.provider.exception.
			NoSuchOAuth2ApplicationException {

		return _oAuth2ApplicationLocalService.getOAuth2Application(
			companyId, clientId);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			getOAuth2ApplicationByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.
			getOAuth2ApplicationByExternalReferenceCode(
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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			getOAuth2ApplicationByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.
			getOAuth2ApplicationByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2Application>
		getOAuth2Applications(int start, int end) {

		return _oAuth2ApplicationLocalService.getOAuth2Applications(start, end);
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2Application>
		getOAuth2Applications(long companyId) {

		return _oAuth2ApplicationLocalService.getOAuth2Applications(companyId);
	}

	@Override
	public java.util.List<com.liferay.oauth2.provider.model.OAuth2Application>
		getOAuth2Applications(long companyId, int clientProfile) {

		return _oAuth2ApplicationLocalService.getOAuth2Applications(
			companyId, clientProfile);
	}

	/**
	 * Returns the number of o auth2 applications.
	 *
	 * @return the number of o auth2 applications
	 */
	@Override
	public int getOAuth2ApplicationsCount() {
		return _oAuth2ApplicationLocalService.getOAuth2ApplicationsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuth2ApplicationLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public String getPlaintextClientSecret(
			com.liferay.oauth2.provider.model.OAuth2Application
				oAuth2Application)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.getPlaintextClientSecret(
			oAuth2Application);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			updateExternalReferenceCode(
				long oAuth2ApplicationId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.updateExternalReferenceCode(
			oAuth2ApplicationId, externalReferenceCode);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			updateExternalReferenceCode(
				com.liferay.oauth2.provider.model.OAuth2Application
					oAuth2Application,
				String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.updateExternalReferenceCode(
			oAuth2Application, externalReferenceCode);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application updateIcon(
			long oAuth2ApplicationId, java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.updateIcon(
			oAuth2ApplicationId, inputStream);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			updateOAuth2Application(
				long oAuth2ApplicationId, long oAuth2ApplicationScopeAliasesId,
				java.util.List<com.liferay.oauth2.provider.constants.GrantType>
					allowedGrantTypesList,
				String clientAuthenticationMethod, long clientCredentialUserId,
				String clientId, int clientProfile, String clientSecret,
				String description, java.util.List<String> featuresList,
				String homePageURL, long iconFileEntryId, String jwks,
				String name, String privacyPolicyURL,
				java.util.List<String> redirectURIsList, boolean rememberDevice,
				boolean trustedApplication)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.updateOAuth2Application(
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
	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
		updateOAuth2Application(
			com.liferay.oauth2.provider.model.OAuth2Application
				oAuth2Application) {

		return _oAuth2ApplicationLocalService.updateOAuth2Application(
			oAuth2Application);
	}

	@Override
	public com.liferay.oauth2.provider.model.OAuth2Application
			updateScopeAliases(
				long userId, String userName, long oAuth2ApplicationId,
				java.util.List<String> scopeAliasesList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _oAuth2ApplicationLocalService.updateScopeAliases(
			userId, userName, oAuth2ApplicationId, scopeAliasesList);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _oAuth2ApplicationLocalService.getBasePersistence();
	}

	@Override
	public OAuth2ApplicationLocalService getWrappedService() {
		return _oAuth2ApplicationLocalService;
	}

	@Override
	public void setWrappedService(
		OAuth2ApplicationLocalService oAuth2ApplicationLocalService) {

		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;
	}

	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1297684656