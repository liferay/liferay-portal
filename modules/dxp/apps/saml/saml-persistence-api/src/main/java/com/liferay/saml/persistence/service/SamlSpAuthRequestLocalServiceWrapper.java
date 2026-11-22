/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SamlSpAuthRequestLocalService}.
 *
 * @author Mika Koivisto
 * @see SamlSpAuthRequestLocalService
 * @generated
 */
public class SamlSpAuthRequestLocalServiceWrapper
	implements SamlSpAuthRequestLocalService,
			   ServiceWrapper<SamlSpAuthRequestLocalService> {

	public SamlSpAuthRequestLocalServiceWrapper() {
		this(null);
	}

	public SamlSpAuthRequestLocalServiceWrapper(
		SamlSpAuthRequestLocalService samlSpAuthRequestLocalService) {

		_samlSpAuthRequestLocalService = samlSpAuthRequestLocalService;
	}

	/**
	 * Adds the saml sp auth request to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthRequest the saml sp auth request
	 * @return the saml sp auth request that was added
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
		addSamlSpAuthRequest(
			com.liferay.saml.persistence.model.SamlSpAuthRequest
				samlSpAuthRequest) {

		return _samlSpAuthRequestLocalService.addSamlSpAuthRequest(
			samlSpAuthRequest);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
		addSamlSpAuthRequest(
			String samlIdpEntityId, String samlRelayState,
			String samlSpAuthRequestKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _samlSpAuthRequestLocalService.addSamlSpAuthRequest(
			samlIdpEntityId, samlRelayState, samlSpAuthRequestKey,
			serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlSpAuthRequestLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new saml sp auth request with the primary key. Does not add the saml sp auth request to the database.
	 *
	 * @param samlSpAuthnRequestId the primary key for the new saml sp auth request
	 * @return the new saml sp auth request
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
		createSamlSpAuthRequest(long samlSpAuthnRequestId) {

		return _samlSpAuthRequestLocalService.createSamlSpAuthRequest(
			samlSpAuthnRequestId);
	}

	@Override
	public void deleteExpiredSamlSpAuthRequests() {
		_samlSpAuthRequestLocalService.deleteExpiredSamlSpAuthRequests();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlSpAuthRequestLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the saml sp auth request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthnRequestId the primary key of the saml sp auth request
	 * @return the saml sp auth request that was removed
	 * @throws PortalException if a saml sp auth request with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
			deleteSamlSpAuthRequest(long samlSpAuthnRequestId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlSpAuthRequestLocalService.deleteSamlSpAuthRequest(
			samlSpAuthnRequestId);
	}

	/**
	 * Deletes the saml sp auth request from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthRequest the saml sp auth request
	 * @return the saml sp auth request that was removed
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
		deleteSamlSpAuthRequest(
			com.liferay.saml.persistence.model.SamlSpAuthRequest
				samlSpAuthRequest) {

		return _samlSpAuthRequestLocalService.deleteSamlSpAuthRequest(
			samlSpAuthRequest);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _samlSpAuthRequestLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _samlSpAuthRequestLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _samlSpAuthRequestLocalService.dynamicQuery();
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

		return _samlSpAuthRequestLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpAuthRequestModelImpl</code>.
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

		return _samlSpAuthRequestLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpAuthRequestModelImpl</code>.
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

		return _samlSpAuthRequestLocalService.dynamicQuery(
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

		return _samlSpAuthRequestLocalService.dynamicQueryCount(dynamicQuery);
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

		return _samlSpAuthRequestLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
		fetchSamlSpAuthRequest(long samlSpAuthnRequestId) {

		return _samlSpAuthRequestLocalService.fetchSamlSpAuthRequest(
			samlSpAuthnRequestId);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
		fetchSamlSpAuthRequest(
			String samlIdpEntityId, String samlSpAuthRequestKey) {

		return _samlSpAuthRequestLocalService.fetchSamlSpAuthRequest(
			samlIdpEntityId, samlSpAuthRequestKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _samlSpAuthRequestLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _samlSpAuthRequestLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _samlSpAuthRequestLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlSpAuthRequestLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the saml sp auth request with the primary key.
	 *
	 * @param samlSpAuthnRequestId the primary key of the saml sp auth request
	 * @return the saml sp auth request
	 * @throws PortalException if a saml sp auth request with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
			getSamlSpAuthRequest(long samlSpAuthnRequestId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlSpAuthRequestLocalService.getSamlSpAuthRequest(
			samlSpAuthnRequestId);
	}

	/**
	 * Returns a range of all the saml sp auth requests.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlSpAuthRequestModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml sp auth requests
	 * @param end the upper bound of the range of saml sp auth requests (not inclusive)
	 * @return the range of saml sp auth requests
	 */
	@Override
	public java.util.List<com.liferay.saml.persistence.model.SamlSpAuthRequest>
		getSamlSpAuthRequests(int start, int end) {

		return _samlSpAuthRequestLocalService.getSamlSpAuthRequests(start, end);
	}

	/**
	 * Returns the number of saml sp auth requests.
	 *
	 * @return the number of saml sp auth requests
	 */
	@Override
	public int getSamlSpAuthRequestsCount() {
		return _samlSpAuthRequestLocalService.getSamlSpAuthRequestsCount();
	}

	/**
	 * Updates the saml sp auth request in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlSpAuthRequestLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlSpAuthRequest the saml sp auth request
	 * @return the saml sp auth request that was updated
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlSpAuthRequest
		updateSamlSpAuthRequest(
			com.liferay.saml.persistence.model.SamlSpAuthRequest
				samlSpAuthRequest) {

		return _samlSpAuthRequestLocalService.updateSamlSpAuthRequest(
			samlSpAuthRequest);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _samlSpAuthRequestLocalService.getBasePersistence();
	}

	@Override
	public SamlSpAuthRequestLocalService getWrappedService() {
		return _samlSpAuthRequestLocalService;
	}

	@Override
	public void setWrappedService(
		SamlSpAuthRequestLocalService samlSpAuthRequestLocalService) {

		_samlSpAuthRequestLocalService = samlSpAuthRequestLocalService;
	}

	private SamlSpAuthRequestLocalService _samlSpAuthRequestLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1378356746