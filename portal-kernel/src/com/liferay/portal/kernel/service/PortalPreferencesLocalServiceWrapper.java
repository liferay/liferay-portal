/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PortalPreferencesLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PortalPreferencesLocalService
 * @generated
 */
public class PortalPreferencesLocalServiceWrapper
	implements PortalPreferencesLocalService,
			   ServiceWrapper<PortalPreferencesLocalService> {

	public PortalPreferencesLocalServiceWrapper() {
		this(null);
	}

	public PortalPreferencesLocalServiceWrapper(
		PortalPreferencesLocalService portalPreferencesLocalService) {

		_portalPreferencesLocalService = portalPreferencesLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
		addPortalPreferences(
			long ownerId, int ownerType, String defaultPreferences) {

		return _portalPreferencesLocalService.addPortalPreferences(
			ownerId, ownerType, defaultPreferences);
	}

	/**
	 * Adds the portal preferences to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortalPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portalPreferences the portal preferences
	 * @return the portal preferences that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
		addPortalPreferences(
			com.liferay.portal.kernel.model.PortalPreferences
				portalPreferences) {

		return _portalPreferencesLocalService.addPortalPreferences(
			portalPreferences);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portalPreferencesLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new portal preferences with the primary key. Does not add the portal preferences to the database.
	 *
	 * @param portalPreferencesId the primary key for the new portal preferences
	 * @return the new portal preferences
	 */
	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
		createPortalPreferences(long portalPreferencesId) {

		return _portalPreferencesLocalService.createPortalPreferences(
			portalPreferencesId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portalPreferencesLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the portal preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortalPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portalPreferencesId the primary key of the portal preferences
	 * @return the portal preferences that was removed
	 * @throws PortalException if a portal preferences with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
			deletePortalPreferences(long portalPreferencesId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portalPreferencesLocalService.deletePortalPreferences(
			portalPreferencesId);
	}

	/**
	 * Deletes the portal preferences from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortalPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portalPreferences the portal preferences
	 * @return the portal preferences that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
		deletePortalPreferences(
			com.liferay.portal.kernel.model.PortalPreferences
				portalPreferences) {

		return _portalPreferencesLocalService.deletePortalPreferences(
			portalPreferences);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _portalPreferencesLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _portalPreferencesLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _portalPreferencesLocalService.dynamicQuery();
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

		return _portalPreferencesLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortalPreferencesModelImpl</code>.
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

		return _portalPreferencesLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortalPreferencesModelImpl</code>.
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

		return _portalPreferencesLocalService.dynamicQuery(
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

		return _portalPreferencesLocalService.dynamicQueryCount(dynamicQuery);
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

		return _portalPreferencesLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
		fetchPortalPreferences(long portalPreferencesId) {

		return _portalPreferencesLocalService.fetchPortalPreferences(
			portalPreferencesId);
	}

	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
		fetchPortalPreferences(long ownerId, int ownerType) {

		return _portalPreferencesLocalService.fetchPortalPreferences(
			ownerId, ownerType);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _portalPreferencesLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _portalPreferencesLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _portalPreferencesLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portalPreferencesLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the portal preferences with the primary key.
	 *
	 * @param portalPreferencesId the primary key of the portal preferences
	 * @return the portal preferences
	 * @throws PortalException if a portal preferences with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
			getPortalPreferences(long portalPreferencesId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portalPreferencesLocalService.getPortalPreferences(
			portalPreferencesId);
	}

	/**
	 * Returns a range of all the portal preferenceses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortalPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portal preferenceses
	 * @param end the upper bound of the range of portal preferenceses (not inclusive)
	 * @return the range of portal preferenceses
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.PortalPreferences>
		getPortalPreferenceses(int start, int end) {

		return _portalPreferencesLocalService.getPortalPreferenceses(
			start, end);
	}

	/**
	 * Returns the number of portal preferenceses.
	 *
	 * @return the number of portal preferenceses
	 */
	@Override
	public int getPortalPreferencesesCount() {
		return _portalPreferencesLocalService.getPortalPreferencesesCount();
	}

	@Override
	public jakarta.portlet.PortletPreferences getPreferences(
		long ownerId, int ownerType) {

		return _portalPreferencesLocalService.getPreferences(
			ownerId, ownerType);
	}

	@Override
	public jakarta.portlet.PortletPreferences getPreferences(
		long ownerId, int ownerType, String defaultPreferences) {

		return _portalPreferencesLocalService.getPreferences(
			ownerId, ownerType, defaultPreferences);
	}

	/**
	 * Updates the portal preferences in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortalPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portalPreferences the portal preferences
	 * @return the portal preferences that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.PortalPreferences
		updatePortalPreferences(
			com.liferay.portal.kernel.model.PortalPreferences
				portalPreferences) {

		return _portalPreferencesLocalService.updatePortalPreferences(
			portalPreferences);
	}

	@Override
	public com.liferay.portal.kernel.model.PortalPreferences updatePreferences(
		long ownerId, int ownerType,
		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences) {

		return _portalPreferencesLocalService.updatePreferences(
			ownerId, ownerType, portalPreferences);
	}

	@Override
	public com.liferay.portal.kernel.model.PortalPreferences updatePreferences(
		long ownerId, int ownerType, String xml) {

		return _portalPreferencesLocalService.updatePreferences(
			ownerId, ownerType, xml);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _portalPreferencesLocalService.getBasePersistence();
	}

	@Override
	public PortalPreferencesLocalService getWrappedService() {
		return _portalPreferencesLocalService;
	}

	@Override
	public void setWrappedService(
		PortalPreferencesLocalService portalPreferencesLocalService) {

		_portalPreferencesLocalService = portalPreferencesLocalService;
	}

	private PortalPreferencesLocalService _portalPreferencesLocalService;

}