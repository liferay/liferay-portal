/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SamlPeerBindingLocalService}.
 *
 * @author Mika Koivisto
 * @see SamlPeerBindingLocalService
 * @generated
 */
public class SamlPeerBindingLocalServiceWrapper
	implements SamlPeerBindingLocalService,
			   ServiceWrapper<SamlPeerBindingLocalService> {

	public SamlPeerBindingLocalServiceWrapper() {
		this(null);
	}

	public SamlPeerBindingLocalServiceWrapper(
		SamlPeerBindingLocalService samlPeerBindingLocalService) {

		_samlPeerBindingLocalService = samlPeerBindingLocalService;
	}

	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
			addSamlPeerBinding(
				long userId, String samlPeerEntityId, String samlNameIdFormat,
				String samlNameIdNameQualifier,
				String samlNameIdSpNameQualifier, String samlNameIdSpProvidedId,
				String samlNameIdValue)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlPeerBindingLocalService.addSamlPeerBinding(
			userId, samlPeerEntityId, samlNameIdFormat, samlNameIdNameQualifier,
			samlNameIdSpNameQualifier, samlNameIdSpProvidedId, samlNameIdValue);
	}

	/**
	 * Adds the saml peer binding to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBinding the saml peer binding
	 * @return the saml peer binding that was added
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
		addSamlPeerBinding(
			com.liferay.saml.persistence.model.SamlPeerBinding
				samlPeerBinding) {

		return _samlPeerBindingLocalService.addSamlPeerBinding(samlPeerBinding);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlPeerBindingLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new saml peer binding with the primary key. Does not add the saml peer binding to the database.
	 *
	 * @param samlPeerBindingId the primary key for the new saml peer binding
	 * @return the new saml peer binding
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
		createSamlPeerBinding(long samlPeerBindingId) {

		return _samlPeerBindingLocalService.createSamlPeerBinding(
			samlPeerBindingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlPeerBindingLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the saml peer binding with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding that was removed
	 * @throws PortalException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
			deleteSamlPeerBinding(long samlPeerBindingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlPeerBindingLocalService.deleteSamlPeerBinding(
			samlPeerBindingId);
	}

	/**
	 * Deletes the saml peer binding from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBinding the saml peer binding
	 * @return the saml peer binding that was removed
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
		deleteSamlPeerBinding(
			com.liferay.saml.persistence.model.SamlPeerBinding
				samlPeerBinding) {

		return _samlPeerBindingLocalService.deleteSamlPeerBinding(
			samlPeerBinding);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _samlPeerBindingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _samlPeerBindingLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _samlPeerBindingLocalService.dynamicQuery();
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

		return _samlPeerBindingLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl</code>.
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

		return _samlPeerBindingLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl</code>.
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

		return _samlPeerBindingLocalService.dynamicQuery(
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

		return _samlPeerBindingLocalService.dynamicQueryCount(dynamicQuery);
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

		return _samlPeerBindingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
		fetchSamlPeerBinding(long samlPeerBindingId) {

		return _samlPeerBindingLocalService.fetchSamlPeerBinding(
			samlPeerBindingId);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
		fetchSamlPeerBinding(
			long companyId, String samlPeerEntityId, boolean deleted,
			String samlNameIdFormat, String samlNameIdNameQualifier,
			String samlNameIdValue) {

		return _samlPeerBindingLocalService.fetchSamlPeerBinding(
			companyId, samlPeerEntityId, deleted, samlNameIdFormat,
			samlNameIdNameQualifier, samlNameIdValue);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _samlPeerBindingLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _samlPeerBindingLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _samlPeerBindingLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlPeerBindingLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the saml peer binding with the primary key.
	 *
	 * @param samlPeerBindingId the primary key of the saml peer binding
	 * @return the saml peer binding
	 * @throws PortalException if a saml peer binding with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
			getSamlPeerBinding(long samlPeerBindingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlPeerBindingLocalService.getSamlPeerBinding(
			samlPeerBindingId);
	}

	/**
	 * Returns a range of all the saml peer bindings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlPeerBindingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml peer bindings
	 * @param end the upper bound of the range of saml peer bindings (not inclusive)
	 * @return the range of saml peer bindings
	 */
	@Override
	public java.util.List<com.liferay.saml.persistence.model.SamlPeerBinding>
		getSamlPeerBindings(int start, int end) {

		return _samlPeerBindingLocalService.getSamlPeerBindings(start, end);
	}

	@Override
	public java.util.List<com.liferay.saml.persistence.model.SamlPeerBinding>
		getSamlPeerBindings(
			long companyId, String samlPeerEntityId, boolean deleted,
			String samlNameIdFormat, String samlNameIdNameQualifier,
			String samlNameIdValue) {

		return _samlPeerBindingLocalService.getSamlPeerBindings(
			companyId, samlPeerEntityId, deleted, samlNameIdFormat,
			samlNameIdNameQualifier, samlNameIdValue);
	}

	/**
	 * Returns the number of saml peer bindings.
	 *
	 * @return the number of saml peer bindings
	 */
	@Override
	public int getSamlPeerBindingsCount() {
		return _samlPeerBindingLocalService.getSamlPeerBindingsCount();
	}

	@Override
	public java.util.List<com.liferay.saml.persistence.model.SamlPeerBinding>
			getUserSamlPeerBindings(
				long userId, String samlPeerEntityId, boolean deleted,
				String samlNameIdFormat, String samlNameIdNameQualifier)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlPeerBindingLocalService.getUserSamlPeerBindings(
			userId, samlPeerEntityId, deleted, samlNameIdFormat,
			samlNameIdNameQualifier);
	}

	/**
	 * Updates the saml peer binding in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlPeerBindingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlPeerBinding the saml peer binding
	 * @return the saml peer binding that was updated
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlPeerBinding
		updateSamlPeerBinding(
			com.liferay.saml.persistence.model.SamlPeerBinding
				samlPeerBinding) {

		return _samlPeerBindingLocalService.updateSamlPeerBinding(
			samlPeerBinding);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _samlPeerBindingLocalService.getBasePersistence();
	}

	@Override
	public SamlPeerBindingLocalService getWrappedService() {
		return _samlPeerBindingLocalService;
	}

	@Override
	public void setWrappedService(
		SamlPeerBindingLocalService samlPeerBindingLocalService) {

		_samlPeerBindingLocalService = samlPeerBindingLocalService;
	}

	private SamlPeerBindingLocalService _samlPeerBindingLocalService;

}