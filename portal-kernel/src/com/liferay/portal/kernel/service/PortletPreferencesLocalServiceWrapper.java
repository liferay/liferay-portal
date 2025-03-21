/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link PortletPreferencesLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PortletPreferencesLocalService
 * @generated
 */
public class PortletPreferencesLocalServiceWrapper
	implements PortletPreferencesLocalService,
			   ServiceWrapper<PortletPreferencesLocalService> {

	public PortletPreferencesLocalServiceWrapper() {
		this(null);
	}

	public PortletPreferencesLocalServiceWrapper(
		PortletPreferencesLocalService portletPreferencesLocalService) {

		_portletPreferencesLocalService = portletPreferencesLocalService;
	}

	@Override
	public PortletPreferences addPortletPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, com.liferay.portal.kernel.model.Portlet portlet,
		String defaultPreferences) {

		return _portletPreferencesLocalService.addPortletPreferences(
			companyId, ownerId, ownerType, plid, portletId, portlet,
			defaultPreferences);
	}

	/**
	 * Adds the portlet preferences to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was added
	 */
	@Override
	public PortletPreferences addPortletPreferences(
		PortletPreferences portletPreferences) {

		return _portletPreferencesLocalService.addPortletPreferences(
			portletPreferences);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferencesLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new portlet preferences with the primary key. Does not add the portlet preferences to the database.
	 *
	 * @param portletPreferencesId the primary key for the new portlet preferences
	 * @return the new portlet preferences
	 */
	@Override
	public PortletPreferences createPortletPreferences(
		long portletPreferencesId) {

		return _portletPreferencesLocalService.createPortletPreferences(
			portletPreferencesId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferencesLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the portlet preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences that was removed
	 * @throws PortalException if a portlet preferences with the primary key could not be found
	 */
	@Override
	public PortletPreferences deletePortletPreferences(
			long portletPreferencesId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferencesLocalService.deletePortletPreferences(
			portletPreferencesId);
	}

	@Override
	public void deletePortletPreferences(
		long ownerId, int ownerType, long plid) {

		_portletPreferencesLocalService.deletePortletPreferences(
			ownerId, ownerType, plid);
	}

	@Override
	public void deletePortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_portletPreferencesLocalService.deletePortletPreferences(
			ownerId, ownerType, plid, portletId);
	}

	/**
	 * Deletes the portlet preferences from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was removed
	 */
	@Override
	public PortletPreferences deletePortletPreferences(
		PortletPreferences portletPreferences) {

		return _portletPreferencesLocalService.deletePortletPreferences(
			portletPreferences);
	}

	@Override
	public void deletePortletPreferencesByOwnerId(long ownerId) {
		_portletPreferencesLocalService.deletePortletPreferencesByOwnerId(
			ownerId);
	}

	@Override
	public void deletePortletPreferencesByPlid(long plid) {
		_portletPreferencesLocalService.deletePortletPreferencesByPlid(plid);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _portletPreferencesLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _portletPreferencesLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _portletPreferencesLocalService.dynamicQuery();
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

		return _portletPreferencesLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
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

		return _portletPreferencesLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
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

		return _portletPreferencesLocalService.dynamicQuery(
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

		return _portletPreferencesLocalService.dynamicQueryCount(dynamicQuery);
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

		return _portletPreferencesLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public PortletPreferences fetchPortletPreferences(
		long portletPreferencesId) {

		return _portletPreferencesLocalService.fetchPortletPreferences(
			portletPreferencesId);
	}

	@Override
	public PortletPreferences fetchPortletPreferences(
		long ownerId, int ownerType, long plid, String portletId) {

		return _portletPreferencesLocalService.fetchPortletPreferences(
			ownerId, ownerType, plid, portletId);
	}

	@Override
	public jakarta.portlet.PortletPreferences fetchPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		return _portletPreferencesLocalService.fetchPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	@Override
	public jakarta.portlet.PortletPreferences fetchPreferences(
		com.liferay.portal.kernel.model.PortletPreferencesIds
			portletPreferencesIds) {

		return _portletPreferencesLocalService.fetchPreferences(
			portletPreferencesIds);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _portletPreferencesLocalService.getActionableDynamicQuery();
	}

	@Override
	public jakarta.portlet.PortletPreferences getDefaultPreferences(
		long companyId, String portletId) {

		return _portletPreferencesLocalService.getDefaultPreferences(
			companyId, portletId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _portletPreferencesLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _portletPreferencesLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferencesLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.settings.Settings
		getPortletInstanceSettings(
			long companyId, long groupId, String portletId,
			com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator
				portletInstanceSettingsLocator,
			com.liferay.portal.kernel.settings.Settings
				portalPreferencesSettings) {

		return _portletPreferencesLocalService.getPortletInstanceSettings(
			companyId, groupId, portletId, portletInstanceSettingsLocator,
			portalPreferencesSettings);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferences() {
		return _portletPreferencesLocalService.getPortletPreferences();
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferences(
		int ownerType, long plid, String portletId) {

		return _portletPreferencesLocalService.getPortletPreferences(
			ownerType, plid, portletId);
	}

	/**
	 * Returns the portlet preferences with the primary key.
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences
	 * @throws PortalException if a portlet preferences with the primary key could not be found
	 */
	@Override
	public PortletPreferences getPortletPreferences(long portletPreferencesId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferencesLocalService.getPortletPreferences(
			portletPreferencesId);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferences(
		long ownerId, int ownerType, long plid) {

		return _portletPreferencesLocalService.getPortletPreferences(
			ownerId, ownerType, plid);
	}

	@Override
	public PortletPreferences getPortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferencesLocalService.getPortletPreferences(
			ownerId, ownerType, plid, portletId);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferences(
			long companyId, long ownerId, int ownerType, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferencesLocalService.getPortletPreferences(
			companyId, ownerId, ownerType, portletId);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferences(
		long companyId, long groupId, long ownerId, int ownerType,
		String portletId, boolean privateLayout) {

		return _portletPreferencesLocalService.getPortletPreferences(
			companyId, groupId, ownerId, ownerType, portletId, privateLayout);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferences(
		long plid, String portletId) {

		return _portletPreferencesLocalService.getPortletPreferences(
			plid, portletId);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferencesByOwnerId(
		long ownerId) {

		return _portletPreferencesLocalService.getPortletPreferencesByOwnerId(
			ownerId);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferencesByPlid(
		long plid) {

		return _portletPreferencesLocalService.getPortletPreferencesByPlid(
			plid);
	}

	@Override
	public java.util.List<PortletPreferences> getPortletPreferencesByPortletId(
		String portletId) {

		return _portletPreferencesLocalService.getPortletPreferencesByPortletId(
			portletId);
	}

	@Override
	public long getPortletPreferencesCount(
		int ownerType, long plid, String portletId) {

		return _portletPreferencesLocalService.getPortletPreferencesCount(
			ownerType, plid, portletId);
	}

	@Override
	public long getPortletPreferencesCount(int ownerType, String portletId) {
		return _portletPreferencesLocalService.getPortletPreferencesCount(
			ownerType, portletId);
	}

	@Override
	public long getPortletPreferencesCount(
		long ownerId, int ownerType, long plid,
		com.liferay.portal.kernel.model.Portlet portlet,
		boolean excludeDefaultPreferences) {

		return _portletPreferencesLocalService.getPortletPreferencesCount(
			ownerId, ownerType, plid, portlet, excludeDefaultPreferences);
	}

	@Override
	public long getPortletPreferencesCount(
		long ownerId, int ownerType, String portletId,
		boolean excludeDefaultPreferences) {

		return _portletPreferencesLocalService.getPortletPreferencesCount(
			ownerId, ownerType, portletId, excludeDefaultPreferences);
	}

	@Override
	public int getPortletPreferencesCount(
		long companyId, long ownerId, int ownerType, String portletId) {

		return _portletPreferencesLocalService.getPortletPreferencesCount(
			companyId, ownerId, ownerType, portletId);
	}

	/**
	 * Returns a range of all the portlet preferenceses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @return the range of portlet preferenceses
	 */
	@Override
	public java.util.List<PortletPreferences> getPortletPreferenceses(
		int start, int end) {

		return _portletPreferencesLocalService.getPortletPreferenceses(
			start, end);
	}

	/**
	 * Returns the number of portlet preferenceses.
	 *
	 * @return the number of portlet preferenceses
	 */
	@Override
	public int getPortletPreferencesesCount() {
		return _portletPreferencesLocalService.getPortletPreferencesesCount();
	}

	@Override
	public jakarta.portlet.PortletPreferences getPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		return _portletPreferencesLocalService.getPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	@Override
	public jakarta.portlet.PortletPreferences getPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String defaultPreferences) {

		return _portletPreferencesLocalService.getPreferences(
			companyId, ownerId, ownerType, plid, portletId, defaultPreferences);
	}

	@Override
	public jakarta.portlet.PortletPreferences getPreferences(
		com.liferay.portal.kernel.model.PortletPreferencesIds
			portletPreferencesIds) {

		return _portletPreferencesLocalService.getPreferences(
			portletPreferencesIds);
	}

	@Override
	public java.util.Map<String, jakarta.portlet.PortletPreferences>
		getStrictPreferences(
			com.liferay.portal.kernel.model.Layout layout,
			java.util.List<com.liferay.portal.kernel.model.Portlet> portlets) {

		return _portletPreferencesLocalService.getStrictPreferences(
			layout, portlets);
	}

	@Override
	public jakarta.portlet.PortletPreferences getStrictPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		return _portletPreferencesLocalService.getStrictPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	@Override
	public jakarta.portlet.PortletPreferences getStrictPreferences(
		com.liferay.portal.kernel.model.PortletPreferencesIds
			portletPreferencesIds) {

		return _portletPreferencesLocalService.getStrictPreferences(
			portletPreferencesIds);
	}

	/**
	 * Updates the portlet preferences in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was updated
	 */
	@Override
	public PortletPreferences updatePortletPreferences(
		PortletPreferences portletPreferences) {

		return _portletPreferencesLocalService.updatePortletPreferences(
			portletPreferences);
	}

	@Override
	public PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId,
		jakarta.portlet.PortletPreferences portletPreferences) {

		return _portletPreferencesLocalService.updatePreferences(
			ownerId, ownerType, plid, portletId, portletPreferences);
	}

	@Override
	public PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId, String xml) {

		return _portletPreferencesLocalService.updatePreferences(
			ownerId, ownerType, plid, portletId, xml);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _portletPreferencesLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<PortletPreferences> getCTPersistence() {
		return _portletPreferencesLocalService.getCTPersistence();
	}

	@Override
	public Class<PortletPreferences> getModelClass() {
		return _portletPreferencesLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<PortletPreferences>, R, E>
				updateUnsafeFunction)
		throws E {

		return _portletPreferencesLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public PortletPreferencesLocalService getWrappedService() {
		return _portletPreferencesLocalService;
	}

	@Override
	public void setWrappedService(
		PortletPreferencesLocalService portletPreferencesLocalService) {

		_portletPreferencesLocalService = portletPreferencesLocalService;
	}

	private PortletPreferencesLocalService _portletPreferencesLocalService;

}