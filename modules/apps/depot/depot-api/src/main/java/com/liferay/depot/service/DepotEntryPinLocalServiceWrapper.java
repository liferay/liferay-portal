/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntryPin;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DepotEntryPinLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryPinLocalService
 * @generated
 */
public class DepotEntryPinLocalServiceWrapper
	implements DepotEntryPinLocalService,
			   ServiceWrapper<DepotEntryPinLocalService> {

	public DepotEntryPinLocalServiceWrapper() {
		this(null);
	}

	public DepotEntryPinLocalServiceWrapper(
		DepotEntryPinLocalService depotEntryPinLocalService) {

		_depotEntryPinLocalService = depotEntryPinLocalService;
	}

	/**
	 * Adds the depot entry pin to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPin the depot entry pin
	 * @return the depot entry pin that was added
	 */
	@Override
	public DepotEntryPin addDepotEntryPin(DepotEntryPin depotEntryPin) {
		return _depotEntryPinLocalService.addDepotEntryPin(depotEntryPin);
	}

	@Override
	public DepotEntryPin addDepotEntryPin(long userId, long depotEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.addDepotEntryPin(
			userId, depotEntryId);
	}

	/**
	 * Creates a new depot entry pin with the primary key. Does not add the depot entry pin to the database.
	 *
	 * @param depotEntryPinId the primary key for the new depot entry pin
	 * @return the new depot entry pin
	 */
	@Override
	public DepotEntryPin createDepotEntryPin(long depotEntryPinId) {
		return _depotEntryPinLocalService.createDepotEntryPin(depotEntryPinId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteDepotEntryDepotEntryPins(long depotEntryId) {
		_depotEntryPinLocalService.deleteDepotEntryDepotEntryPins(depotEntryId);
	}

	/**
	 * Deletes the depot entry pin from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPin the depot entry pin
	 * @return the depot entry pin that was removed
	 */
	@Override
	public DepotEntryPin deleteDepotEntryPin(DepotEntryPin depotEntryPin) {
		return _depotEntryPinLocalService.deleteDepotEntryPin(depotEntryPin);
	}

	/**
	 * Deletes the depot entry pin with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPinId the primary key of the depot entry pin
	 * @return the depot entry pin that was removed
	 * @throws PortalException if a depot entry pin with the primary key could not be found
	 */
	@Override
	public DepotEntryPin deleteDepotEntryPin(long depotEntryPinId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.deleteDepotEntryPin(depotEntryPinId);
	}

	@Override
	public DepotEntryPin deleteDepotEntryPin(long userId, long depotEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.deleteDepotEntryPin(
			userId, depotEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteUserDepotEntryPins(long userId) {
		_depotEntryPinLocalService.deleteUserDepotEntryPins(userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _depotEntryPinLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _depotEntryPinLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _depotEntryPinLocalService.dynamicQuery();
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

		return _depotEntryPinLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryPinModelImpl</code>.
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

		return _depotEntryPinLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryPinModelImpl</code>.
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

		return _depotEntryPinLocalService.dynamicQuery(
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

		return _depotEntryPinLocalService.dynamicQueryCount(dynamicQuery);
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

		return _depotEntryPinLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DepotEntryPin fetchDepotEntryPin(long depotEntryPinId) {
		return _depotEntryPinLocalService.fetchDepotEntryPin(depotEntryPinId);
	}

	/**
	 * Returns the depot entry pin matching the UUID and group.
	 *
	 * @param uuid the depot entry pin's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry pin, or <code>null</code> if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin fetchDepotEntryPinByUuidAndGroupId(
		String uuid, long groupId) {

		return _depotEntryPinLocalService.fetchDepotEntryPinByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public DepotEntryPin fetchGroupDepotEntryPin(long groupId, long userId) {
		return _depotEntryPinLocalService.fetchGroupDepotEntryPin(
			groupId, userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _depotEntryPinLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the depot entry pin with the primary key.
	 *
	 * @param depotEntryPinId the primary key of the depot entry pin
	 * @return the depot entry pin
	 * @throws PortalException if a depot entry pin with the primary key could not be found
	 */
	@Override
	public DepotEntryPin getDepotEntryPin(long depotEntryPinId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.getDepotEntryPin(depotEntryPinId);
	}

	/**
	 * Returns the depot entry pin matching the UUID and group.
	 *
	 * @param uuid the depot entry pin's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry pin
	 * @throws PortalException if a matching depot entry pin could not be found
	 */
	@Override
	public DepotEntryPin getDepotEntryPinByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.getDepotEntryPinByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the depot entry pins.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryPinModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @return the range of depot entry pins
	 */
	@Override
	public java.util.List<DepotEntryPin> getDepotEntryPins(int start, int end) {
		return _depotEntryPinLocalService.getDepotEntryPins(start, end);
	}

	/**
	 * Returns all the depot entry pins matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry pins
	 * @param companyId the primary key of the company
	 * @return the matching depot entry pins, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DepotEntryPin> getDepotEntryPinsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _depotEntryPinLocalService.getDepotEntryPinsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of depot entry pins matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry pins
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of depot entry pins
	 * @param end the upper bound of the range of depot entry pins (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching depot entry pins, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DepotEntryPin> getDepotEntryPinsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryPin>
			orderByComparator) {

		return _depotEntryPinLocalService.getDepotEntryPinsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of depot entry pins.
	 *
	 * @return the number of depot entry pins
	 */
	@Override
	public int getDepotEntryPinsCount() {
		return _depotEntryPinLocalService.getDepotEntryPinsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _depotEntryPinLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _depotEntryPinLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryPinLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<DepotEntryPin> getUserDepotEntryPins(
		long userId, int start, int end) {

		return _depotEntryPinLocalService.getUserDepotEntryPins(
			userId, start, end);
	}

	@Override
	public int getUserDepotEntryPinsCount(long userId) {
		return _depotEntryPinLocalService.getUserDepotEntryPinsCount(userId);
	}

	/**
	 * Updates the depot entry pin in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryPinLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryPin the depot entry pin
	 * @return the depot entry pin that was updated
	 */
	@Override
	public DepotEntryPin updateDepotEntryPin(DepotEntryPin depotEntryPin) {
		return _depotEntryPinLocalService.updateDepotEntryPin(depotEntryPin);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _depotEntryPinLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DepotEntryPin> getCTPersistence() {
		return _depotEntryPinLocalService.getCTPersistence();
	}

	@Override
	public Class<DepotEntryPin> getModelClass() {
		return _depotEntryPinLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DepotEntryPin>, R, E>
				updateUnsafeFunction)
		throws E {

		return _depotEntryPinLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DepotEntryPinLocalService getWrappedService() {
		return _depotEntryPinLocalService;
	}

	@Override
	public void setWrappedService(
		DepotEntryPinLocalService depotEntryPinLocalService) {

		_depotEntryPinLocalService = depotEntryPinLocalService;
	}

	private DepotEntryPinLocalService _depotEntryPinLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1589513475