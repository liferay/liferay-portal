/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link PortletPreferenceValueLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PortletPreferenceValueLocalService
 * @generated
 */
public class PortletPreferenceValueLocalServiceWrapper
	implements PortletPreferenceValueLocalService,
			   ServiceWrapper<PortletPreferenceValueLocalService> {

	public PortletPreferenceValueLocalServiceWrapper() {
		this(null);
	}

	public PortletPreferenceValueLocalServiceWrapper(
		PortletPreferenceValueLocalService portletPreferenceValueLocalService) {

		_portletPreferenceValueLocalService =
			portletPreferenceValueLocalService;
	}

	/**
	 * Adds the portlet preference value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValue the portlet preference value
	 * @return the portlet preference value that was added
	 */
	@Override
	public PortletPreferenceValue addPortletPreferenceValue(
		PortletPreferenceValue portletPreferenceValue) {

		return _portletPreferenceValueLocalService.addPortletPreferenceValue(
			portletPreferenceValue);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferenceValueLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new portlet preference value with the primary key. Does not add the portlet preference value to the database.
	 *
	 * @param portletPreferenceValueId the primary key for the new portlet preference value
	 * @return the new portlet preference value
	 */
	@Override
	public PortletPreferenceValue createPortletPreferenceValue(
		long portletPreferenceValueId) {

		return _portletPreferenceValueLocalService.createPortletPreferenceValue(
			portletPreferenceValueId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferenceValueLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the portlet preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value that was removed
	 * @throws PortalException if a portlet preference value with the primary key could not be found
	 */
	@Override
	public PortletPreferenceValue deletePortletPreferenceValue(
			long portletPreferenceValueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferenceValueLocalService.deletePortletPreferenceValue(
			portletPreferenceValueId);
	}

	/**
	 * Deletes the portlet preference value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValue the portlet preference value
	 * @return the portlet preference value that was removed
	 */
	@Override
	public PortletPreferenceValue deletePortletPreferenceValue(
		PortletPreferenceValue portletPreferenceValue) {

		return _portletPreferenceValueLocalService.deletePortletPreferenceValue(
			portletPreferenceValue);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _portletPreferenceValueLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _portletPreferenceValueLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _portletPreferenceValueLocalService.dynamicQuery();
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

		return _portletPreferenceValueLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
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

		return _portletPreferenceValueLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
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

		return _portletPreferenceValueLocalService.dynamicQuery(
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

		return _portletPreferenceValueLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _portletPreferenceValueLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public PortletPreferenceValue fetchPortletPreferenceValue(
		long portletPreferenceValueId) {

		return _portletPreferenceValueLocalService.fetchPortletPreferenceValue(
			portletPreferenceValueId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _portletPreferenceValueLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _portletPreferenceValueLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _portletPreferenceValueLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferenceValueLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the portlet preference value with the primary key.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value
	 * @throws PortalException if a portlet preference value with the primary key could not be found
	 */
	@Override
	public PortletPreferenceValue getPortletPreferenceValue(
			long portletPreferenceValueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletPreferenceValueLocalService.getPortletPreferenceValue(
			portletPreferenceValueId);
	}

	/**
	 * Returns a range of all the portlet preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @return the range of portlet preference values
	 */
	@Override
	public java.util.List<PortletPreferenceValue> getPortletPreferenceValues(
		int start, int end) {

		return _portletPreferenceValueLocalService.getPortletPreferenceValues(
			start, end);
	}

	/**
	 * Returns the number of portlet preference values.
	 *
	 * @return the number of portlet preference values
	 */
	@Override
	public int getPortletPreferenceValuesCount() {
		return _portletPreferenceValueLocalService.
			getPortletPreferenceValuesCount();
	}

	@Override
	public int getPortletPreferenceValuesCount(
		long companyId, String name, String smallValue) {

		return _portletPreferenceValueLocalService.
			getPortletPreferenceValuesCount(companyId, name, smallValue);
	}

	@Override
	public jakarta.portlet.PortletPreferences getPreferences(
		com.liferay.portal.kernel.model.PortletPreferences portletPreferences) {

		return _portletPreferenceValueLocalService.getPreferences(
			portletPreferences);
	}

	/**
	 * Updates the portlet preference value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValue the portlet preference value
	 * @return the portlet preference value that was updated
	 */
	@Override
	public PortletPreferenceValue updatePortletPreferenceValue(
		PortletPreferenceValue portletPreferenceValue) {

		return _portletPreferenceValueLocalService.updatePortletPreferenceValue(
			portletPreferenceValue);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _portletPreferenceValueLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<PortletPreferenceValue> getCTPersistence() {
		return _portletPreferenceValueLocalService.getCTPersistence();
	}

	@Override
	public Class<PortletPreferenceValue> getModelClass() {
		return _portletPreferenceValueLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<PortletPreferenceValue>, R, E>
				updateUnsafeFunction)
		throws E {

		return _portletPreferenceValueLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public PortletPreferenceValueLocalService getWrappedService() {
		return _portletPreferenceValueLocalService;
	}

	@Override
	public void setWrappedService(
		PortletPreferenceValueLocalService portletPreferenceValueLocalService) {

		_portletPreferenceValueLocalService =
			portletPreferenceValueLocalService;
	}

	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}