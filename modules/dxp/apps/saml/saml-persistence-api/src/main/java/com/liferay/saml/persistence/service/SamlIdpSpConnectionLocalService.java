/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
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
import com.liferay.saml.persistence.model.SamlIdpSpConnection;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SamlIdpSpConnection. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Mika Koivisto
 * @see SamlIdpSpConnectionLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SamlIdpSpConnectionLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.saml.persistence.service.impl.SamlIdpSpConnectionLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the saml idp sp connection local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SamlIdpSpConnectionLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the saml idp sp connection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnection the saml idp sp connection
	 * @return the saml idp sp connection that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SamlIdpSpConnection addSamlIdpSpConnection(
		SamlIdpSpConnection samlIdpSpConnection);

	public SamlIdpSpConnection addSamlIdpSpConnection(
			String samlSpEntityId, int assertionLifetime, String attributeNames,
			boolean attributesEnabled, boolean attributesNamespaceEnabled,
			boolean enabled, boolean encryptionForced, String metadataUrl,
			InputStream metadataXmlInputStream, String name,
			String nameIdAttribute, String nameIdFormat,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new saml idp sp connection with the primary key. Does not add the saml idp sp connection to the database.
	 *
	 * @param samlIdpSpConnectionId the primary key for the new saml idp sp connection
	 * @return the new saml idp sp connection
	 */
	@Transactional(enabled = false)
	public SamlIdpSpConnection createSamlIdpSpConnection(
		long samlIdpSpConnectionId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the saml idp sp connection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection that was removed
	 * @throws PortalException if a saml idp sp connection with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SamlIdpSpConnection deleteSamlIdpSpConnection(
			long samlIdpSpConnectionId)
		throws PortalException;

	/**
	 * Deletes the saml idp sp connection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnection the saml idp sp connection
	 * @return the saml idp sp connection that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public SamlIdpSpConnection deleteSamlIdpSpConnection(
		SamlIdpSpConnection samlIdpSpConnection);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionModelImpl</code>.
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
	public SamlIdpSpConnection fetchSamlIdpSpConnection(
		long samlIdpSpConnectionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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

	/**
	 * Returns the saml idp sp connection with the primary key.
	 *
	 * @param samlIdpSpConnectionId the primary key of the saml idp sp connection
	 * @return the saml idp sp connection
	 * @throws PortalException if a saml idp sp connection with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SamlIdpSpConnection getSamlIdpSpConnection(
			long samlIdpSpConnectionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SamlIdpSpConnection getSamlIdpSpConnection(
			long companyId, String samlSpEntityId)
		throws PortalException;

	/**
	 * Returns a range of all the saml idp sp connections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml idp sp connections
	 * @param end the upper bound of the range of saml idp sp connections (not inclusive)
	 * @return the range of saml idp sp connections
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SamlIdpSpConnection> getSamlIdpSpConnections(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SamlIdpSpConnection> getSamlIdpSpConnections(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SamlIdpSpConnection> getSamlIdpSpConnections(
		long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SamlIdpSpConnection> getSamlIdpSpConnections(
		long companyId, int start, int end,
		OrderByComparator<SamlIdpSpConnection> orderByComparator);

	/**
	 * Returns the number of saml idp sp connections.
	 *
	 * @return the number of saml idp sp connections
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSamlIdpSpConnectionsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSamlIdpSpConnectionsCount(long companyId);

	public void updateMetadata(long samlIdpSpConnectionId)
		throws PortalException;

	public SamlIdpSpConnection updateSamlIdpSpConnection(
			long samlIdpSpConnectionId, String samlSpEntityId,
			int assertionLifetime, String attributeNames,
			boolean attributesEnabled, boolean attributesNamespaceEnabled,
			boolean enabled, boolean encryptionForced, String metadataUrl,
			InputStream metadataXmlInputStream, String name,
			String nameIdAttribute, String nameIdFormat,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the saml idp sp connection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIdpSpConnectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIdpSpConnection the saml idp sp connection
	 * @return the saml idp sp connection that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SamlIdpSpConnection updateSamlIdpSpConnection(
		SamlIdpSpConnection samlIdpSpConnection);

}