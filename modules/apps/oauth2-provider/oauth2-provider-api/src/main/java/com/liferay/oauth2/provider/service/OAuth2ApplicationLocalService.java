/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.exception.NoSuchOAuth2ApplicationException;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.util.builder.OAuth2ScopeBuilder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.function.Consumer;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for OAuth2Application. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ApplicationLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OAuth2ApplicationLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.oauth2.provider.service.impl.OAuth2ApplicationLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the o auth2 application local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OAuth2ApplicationLocalServiceUtil} if injection and service tracking are not available.
	 */
	public OAuth2Application addOAuth2Application(
			long companyId, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication,
			Consumer<OAuth2ScopeBuilder> builderConsumer,
			ServiceContext serviceContext)
		throws PortalException;

	public OAuth2Application addOAuth2Application(
			long companyId, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, List<String> scopeAliasesList,
			boolean trustedApplication, ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public OAuth2Application addOAuth2Application(
		OAuth2Application oAuth2Application);

	public OAuth2Application addOrUpdateOAuth2Application(
			String externalReferenceCode, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication,
			Consumer<OAuth2ScopeBuilder> builderConsumer,
			ServiceContext serviceContext)
		throws PortalException;

	public OAuth2Application addOrUpdateOAuth2Application(
			String externalReferenceCode, long userId, String userName,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, List<String> scopeAliasesList,
			boolean trustedApplication, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new o auth2 application with the primary key. Does not add the o auth2 application to the database.
	 *
	 * @param oAuth2ApplicationId the primary key for the new o auth2 application
	 * @return the new o auth2 application
	 */
	@Transactional(enabled = false)
	public OAuth2Application createOAuth2Application(long oAuth2ApplicationId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public OAuth2Application deleteOAuth2Application(long oAuth2ApplicationId)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public OAuth2Application deleteOAuth2Application(
			OAuth2Application oAuth2Application)
		throws PortalException;

	public void deleteOAuth2Applications(long companyId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application fetchOAuth2Application(long oAuth2ApplicationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application fetchOAuth2Application(
		long companyId, String clientId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application fetchOAuth2ApplicationByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the o auth2 application with the matching UUID and company.
	 *
	 * @param uuid the o auth2 application's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth2 application, or <code>null</code> if a matching o auth2 application could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application fetchOAuth2ApplicationByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the o auth2 application with the primary key.
	 *
	 * @param oAuth2ApplicationId the primary key of the o auth2 application
	 * @return the o auth2 application
	 * @throws PortalException if a o auth2 application with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application getOAuth2Application(long oAuth2ApplicationId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application getOAuth2Application(
			long companyId, String clientId)
		throws NoSuchOAuth2ApplicationException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application getOAuth2ApplicationByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the o auth2 application with the matching UUID and company.
	 *
	 * @param uuid the o auth2 application's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth2 application
	 * @throws PortalException if a matching o auth2 application could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuth2Application getOAuth2ApplicationByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Application> getOAuth2Applications(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Application> getOAuth2Applications(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuth2Application> getOAuth2Applications(
		long companyId, int clientProfile);

	/**
	 * Returns the number of o auth2 applications.
	 *
	 * @return the number of o auth2 applications
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuth2ApplicationsCount();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public String resolveClientSecret(OAuth2Application oAuth2Application)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public OAuth2Application updateExternalReferenceCode(
			long oAuth2ApplicationId, String externalReferenceCode)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public OAuth2Application updateExternalReferenceCode(
			OAuth2Application oAuth2Application, String externalReferenceCode)
		throws PortalException;

	public OAuth2Application updateIcon(
			long oAuth2ApplicationId, InputStream inputStream)
		throws PortalException;

	public OAuth2Application updateOAuth2Application(
			long oAuth2ApplicationId, long oAuth2ApplicationScopeAliasesId,
			List<GrantType> allowedGrantTypesList,
			String clientAuthenticationMethod, long clientCredentialUserId,
			String clientId, int clientProfile, String clientSecret,
			String description, List<String> featuresList, String homePageURL,
			long iconFileEntryId, String jwks, String name,
			String privacyPolicyURL, List<String> redirectURIsList,
			boolean rememberDevice, boolean trustedApplication)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public OAuth2Application updateOAuth2Application(
		OAuth2Application oAuth2Application);

	public OAuth2Application updateScopeAliases(
			long userId, String userName, long oAuth2ApplicationId,
			List<String> scopeAliasesList)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-613623196