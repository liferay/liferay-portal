/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for OAuthClientASLocalMetadata. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientASLocalMetadataLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface OAuthClientASLocalMetadataLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.oauth.client.persistence.service.impl.OAuthClientASLocalMetadataLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the o auth client as local metadata local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link OAuthClientASLocalMetadataLocalServiceUtil} if injection and service tracking are not available.
	 */
	public OAuthClientASLocalMetadata addOAuthClientASLocalMetadata(
			long userId, String metadataJSON, String wellKnownURISuffix)
		throws PortalException;

	/**
	 * Adds the o auth client as local metadata to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 * @return the o auth client as local metadata that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public OAuthClientASLocalMetadata addOAuthClientASLocalMetadata(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata);

	public OAuthClientASLocalMetadata addOAuthClientASLocalMetadata(
			String externalReferenceCode, long userId,
			String authorizationEndpoint, String issuer, String jwksURI,
			boolean localWellKnownEnabled, String registrationEndpoint,
			String[] supportedGrantTypes, String[] supportedScopes,
			String[] supportedSubjectTypes, String tokenEndpoint,
			String userInfoEndpoint)
		throws PortalException;

	/**
	 * Creates a new o auth client as local metadata with the primary key. Does not add the o auth client as local metadata to the database.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key for the new o auth client as local metadata
	 * @return the new o auth client as local metadata
	 */
	@Transactional(enabled = false)
	public OAuthClientASLocalMetadata createOAuthClientASLocalMetadata(
		long oAuthClientASLocalMetadataId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the o auth client as local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws PortalException if a o auth client as local metadata with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId)
		throws PortalException;

	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException;

	/**
	 * Deletes the o auth client as local metadata from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl</code>.
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
	public OAuthClientASLocalMetadata fetchOAuthClientASLocalMetadata(
		long oAuthClientASLocalMetadataId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata fetchOAuthClientASLocalMetadata(
		long companyId, boolean localWellKnownEnabled,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata fetchOAuthClientASLocalMetadata(
		long companyId, String issuer);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata
		fetchOAuthClientASLocalMetadataByExternalReferenceCode(
			String externalReferenceCode, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata
		fetchOAuthClientASLocalMetadataByLocalWellKnownURI(
			long companyId, String localWellKnownURI);

	/**
	 * Returns the o auth client as local metadata with the matching UUID and company.
	 *
	 * @param uuid the o auth client as local metadata's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata
		fetchOAuthClientASLocalMetadataByUuidAndCompanyId(
			String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(
			long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the o auth client as local metadata with the primary key.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata
	 * @throws PortalException if a o auth client as local metadata with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata getOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata getOAuthClientASLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata
			getOAuthClientASLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the o auth client as local metadata with the matching UUID and company.
	 *
	 * @param uuid the o auth client as local metadata's UUID
	 * @param companyId the primary key of the company
	 * @return the matching o auth client as local metadata
	 * @throws PortalException if a matching o auth client as local metadata could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public OAuthClientASLocalMetadata
			getOAuthClientASLocalMetadataByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of o auth client as local metadatas
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientASLocalMetadata> getOAuthClientASLocalMetadatas(
		int start, int end);

	/**
	 * Returns the number of o auth client as local metadatas.
	 *
	 * @return the number of o auth client as local metadatas
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuthClientASLocalMetadatasCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOAuthClientASLocalMetadatasCount(long companyId);

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientASLocalMetadata> getUserOAuthClientASLocalMetadata(
		long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<OAuthClientASLocalMetadata> getUserOAuthClientASLocalMetadata(
		long userId, int start, int end);

	public OAuthClientASLocalMetadata updateOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId, String metadataJSON,
			String wellKnownURISuffix)
		throws PortalException;

	public OAuthClientASLocalMetadata updateOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId, String authorizationEndpoint,
			String issuer, String jwksURI, boolean localWellKnownEnabled,
			String registrationEndpoint, String[] supportedGrantTypes,
			String[] supportedScopes, String[] supportedSubjectTypes,
			String tokenEndpoint, String userInfoEndpoint)
		throws PortalException;

	/**
	 * Updates the o auth client as local metadata in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OAuthClientASLocalMetadataLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 * @return the o auth client as local metadata that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public OAuthClientASLocalMetadata updateOAuthClientASLocalMetadata(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata);

}
// LIFERAY-SERVICE-BUILDER-HASH:1193239788