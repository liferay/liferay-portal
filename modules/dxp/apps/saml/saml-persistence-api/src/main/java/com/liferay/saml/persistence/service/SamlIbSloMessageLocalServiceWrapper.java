/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SamlIbSloMessageLocalService}.
 *
 * @author Mika Koivisto
 * @see SamlIbSloMessageLocalService
 * @generated
 */
public class SamlIbSloMessageLocalServiceWrapper
	implements SamlIbSloMessageLocalService,
			   ServiceWrapper<SamlIbSloMessageLocalService> {

	public SamlIbSloMessageLocalServiceWrapper() {
		this(null);
	}

	public SamlIbSloMessageLocalServiceWrapper(
		SamlIbSloMessageLocalService samlIbSloMessageLocalService) {

		_samlIbSloMessageLocalService = samlIbSloMessageLocalService;
	}

	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
		addSamlIbSloMessage(
			long companyId, String samlIdpEntityId, String logoutRequestXml,
			String samlIdpSessionIndex) {

		return _samlIbSloMessageLocalService.addSamlIbSloMessage(
			companyId, samlIdpEntityId, logoutRequestXml, samlIdpSessionIndex);
	}

	/**
	 * Adds the saml ib slo message to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIbSloMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIbSloMessage the saml ib slo message
	 * @return the saml ib slo message that was added
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
		addSamlIbSloMessage(
			com.liferay.saml.persistence.model.SamlIbSloMessage
				samlIbSloMessage) {

		return _samlIbSloMessageLocalService.addSamlIbSloMessage(
			samlIbSloMessage);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIbSloMessageLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new saml ib slo message with the primary key. Does not add the saml ib slo message to the database.
	 *
	 * @param samlIbSloMessageId the primary key for the new saml ib slo message
	 * @return the new saml ib slo message
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
		createSamlIbSloMessage(long samlIbSloMessageId) {

		return _samlIbSloMessageLocalService.createSamlIbSloMessage(
			samlIbSloMessageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIbSloMessageLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the saml ib slo message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIbSloMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message that was removed
	 * @throws PortalException if a saml ib slo message with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
			deleteSamlIbSloMessage(long samlIbSloMessageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIbSloMessageLocalService.deleteSamlIbSloMessage(
			samlIbSloMessageId);
	}

	/**
	 * Deletes the saml ib slo message from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIbSloMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIbSloMessage the saml ib slo message
	 * @return the saml ib slo message that was removed
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
		deleteSamlIbSloMessage(
			com.liferay.saml.persistence.model.SamlIbSloMessage
				samlIbSloMessage) {

		return _samlIbSloMessageLocalService.deleteSamlIbSloMessage(
			samlIbSloMessage);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _samlIbSloMessageLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _samlIbSloMessageLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _samlIbSloMessageLocalService.dynamicQuery();
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

		return _samlIbSloMessageLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIbSloMessageModelImpl</code>.
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

		return _samlIbSloMessageLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIbSloMessageModelImpl</code>.
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

		return _samlIbSloMessageLocalService.dynamicQuery(
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

		return _samlIbSloMessageLocalService.dynamicQueryCount(dynamicQuery);
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

		return _samlIbSloMessageLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
		fetchSamlIbSloMessage(long samlIbSloMessageId) {

		return _samlIbSloMessageLocalService.fetchSamlIbSloMessage(
			samlIbSloMessageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _samlIbSloMessageLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _samlIbSloMessageLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _samlIbSloMessageLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIbSloMessageLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the saml ib slo message with the primary key.
	 *
	 * @param samlIbSloMessageId the primary key of the saml ib slo message
	 * @return the saml ib slo message
	 * @throws PortalException if a saml ib slo message with the primary key could not be found
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
			getSamlIbSloMessage(long samlIbSloMessageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _samlIbSloMessageLocalService.getSamlIbSloMessage(
			samlIbSloMessageId);
	}

	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
			getSamlIbSloMessageBySamlIdpSessionIndex(String samlIdpSessionIndex)
		throws com.liferay.saml.persistence.exception.
			NoSuchIbSloMessageException {

		return _samlIbSloMessageLocalService.
			getSamlIbSloMessageBySamlIdpSessionIndex(samlIdpSessionIndex);
	}

	/**
	 * Returns a range of all the saml ib slo messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.saml.persistence.model.impl.SamlIbSloMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of saml ib slo messages
	 * @param end the upper bound of the range of saml ib slo messages (not inclusive)
	 * @return the range of saml ib slo messages
	 */
	@Override
	public java.util.List<com.liferay.saml.persistence.model.SamlIbSloMessage>
		getSamlIbSloMessages(int start, int end) {

		return _samlIbSloMessageLocalService.getSamlIbSloMessages(start, end);
	}

	/**
	 * Returns the number of saml ib slo messages.
	 *
	 * @return the number of saml ib slo messages
	 */
	@Override
	public int getSamlIbSloMessagesCount() {
		return _samlIbSloMessageLocalService.getSamlIbSloMessagesCount();
	}

	/**
	 * Updates the saml ib slo message in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SamlIbSloMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param samlIbSloMessage the saml ib slo message
	 * @return the saml ib slo message that was updated
	 */
	@Override
	public com.liferay.saml.persistence.model.SamlIbSloMessage
		updateSamlIbSloMessage(
			com.liferay.saml.persistence.model.SamlIbSloMessage
				samlIbSloMessage) {

		return _samlIbSloMessageLocalService.updateSamlIbSloMessage(
			samlIbSloMessage);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _samlIbSloMessageLocalService.getBasePersistence();
	}

	@Override
	public SamlIbSloMessageLocalService getWrappedService() {
		return _samlIbSloMessageLocalService;
	}

	@Override
	public void setWrappedService(
		SamlIbSloMessageLocalService samlIbSloMessageLocalService) {

		_samlIbSloMessageLocalService = samlIbSloMessageLocalService;
	}

	private SamlIbSloMessageLocalService _samlIbSloMessageLocalService;

}