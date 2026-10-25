/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service;

import com.liferay.calendar.model.CalendarResource;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CalendarResourceLocalService}.
 *
 * @author Eduardo Lundgren
 * @see CalendarResourceLocalService
 * @generated
 */
public class CalendarResourceLocalServiceWrapper
	implements CalendarResourceLocalService,
			   ServiceWrapper<CalendarResourceLocalService> {

	public CalendarResourceLocalServiceWrapper() {
		this(null);
	}

	public CalendarResourceLocalServiceWrapper(
		CalendarResourceLocalService calendarResourceLocalService) {

		_calendarResourceLocalService = calendarResourceLocalService;
	}

	/**
	 * Adds the calendar resource to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarResource the calendar resource
	 * @return the calendar resource that was added
	 */
	@Override
	public CalendarResource addCalendarResource(
		CalendarResource calendarResource) {

		return _calendarResourceLocalService.addCalendarResource(
			calendarResource);
	}

	@Override
	public CalendarResource addCalendarResource(
			long userId, long groupId, long classNameId, long classPK,
			String classUuid, String code,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.addCalendarResource(
			userId, groupId, classNameId, classPK, classUuid, code, nameMap,
			descriptionMap, active, serviceContext);
	}

	/**
	 * Creates a new calendar resource with the primary key. Does not add the calendar resource to the database.
	 *
	 * @param calendarResourceId the primary key for the new calendar resource
	 * @return the new calendar resource
	 */
	@Override
	public CalendarResource createCalendarResource(long calendarResourceId) {
		return _calendarResourceLocalService.createCalendarResource(
			calendarResourceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the calendar resource from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarResource the calendar resource
	 * @return the calendar resource that was removed
	 * @throws PortalException
	 */
	@Override
	public CalendarResource deleteCalendarResource(
			CalendarResource calendarResource)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.deleteCalendarResource(
			calendarResource);
	}

	/**
	 * Deletes the calendar resource with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarResourceId the primary key of the calendar resource
	 * @return the calendar resource that was removed
	 * @throws PortalException if a calendar resource with the primary key could not be found
	 */
	@Override
	public CalendarResource deleteCalendarResource(long calendarResourceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.deleteCalendarResource(
			calendarResourceId);
	}

	@Override
	public void deleteCalendarResources(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarResourceLocalService.deleteCalendarResources(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _calendarResourceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _calendarResourceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _calendarResourceLocalService.dynamicQuery();
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

		return _calendarResourceLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarResourceModelImpl</code>.
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

		return _calendarResourceLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarResourceModelImpl</code>.
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

		return _calendarResourceLocalService.dynamicQuery(
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

		return _calendarResourceLocalService.dynamicQueryCount(dynamicQuery);
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

		return _calendarResourceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CalendarResource fetchCalendarResource(long calendarResourceId) {
		return _calendarResourceLocalService.fetchCalendarResource(
			calendarResourceId);
	}

	@Override
	public CalendarResource fetchCalendarResource(
		long classNameId, long classPK) {

		return _calendarResourceLocalService.fetchCalendarResource(
			classNameId, classPK);
	}

	@Override
	public CalendarResource fetchCalendarResource(long groupId, String code) {
		return _calendarResourceLocalService.fetchCalendarResource(
			groupId, code);
	}

	/**
	 * Returns the calendar resource matching the UUID and group.
	 *
	 * @param uuid the calendar resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar resource, or <code>null</code> if a matching calendar resource could not be found
	 */
	@Override
	public CalendarResource fetchCalendarResourceByUuidAndGroupId(
		String uuid, long groupId) {

		return _calendarResourceLocalService.
			fetchCalendarResourceByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _calendarResourceLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the calendar resource with the primary key.
	 *
	 * @param calendarResourceId the primary key of the calendar resource
	 * @return the calendar resource
	 * @throws PortalException if a calendar resource with the primary key could not be found
	 */
	@Override
	public CalendarResource getCalendarResource(long calendarResourceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.getCalendarResource(
			calendarResourceId);
	}

	/**
	 * Returns the calendar resource matching the UUID and group.
	 *
	 * @param uuid the calendar resource's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar resource
	 * @throws PortalException if a matching calendar resource could not be found
	 */
	@Override
	public CalendarResource getCalendarResourceByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.
			getCalendarResourceByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the calendar resources.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarResourceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendar resources
	 * @param end the upper bound of the range of calendar resources (not inclusive)
	 * @return the range of calendar resources
	 */
	@Override
	public java.util.List<CalendarResource> getCalendarResources(
		int start, int end) {

		return _calendarResourceLocalService.getCalendarResources(start, end);
	}

	@Override
	public java.util.List<CalendarResource> getCalendarResources(long groupId) {
		return _calendarResourceLocalService.getCalendarResources(groupId);
	}

	/**
	 * Returns all the calendar resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendar resources
	 * @param companyId the primary key of the company
	 * @return the matching calendar resources, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CalendarResource>
		getCalendarResourcesByUuidAndCompanyId(String uuid, long companyId) {

		return _calendarResourceLocalService.
			getCalendarResourcesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of calendar resources matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendar resources
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of calendar resources
	 * @param end the upper bound of the range of calendar resources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching calendar resources, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CalendarResource>
		getCalendarResourcesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CalendarResource>
				orderByComparator) {

		return _calendarResourceLocalService.
			getCalendarResourcesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of calendar resources.
	 *
	 * @return the number of calendar resources
	 */
	@Override
	public int getCalendarResourcesCount() {
		return _calendarResourceLocalService.getCalendarResourcesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _calendarResourceLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _calendarResourceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _calendarResourceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<CalendarResource> searchByKeywords(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active, boolean andOperator, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CalendarResource>
			orderByComparator) {

		return _calendarResourceLocalService.searchByKeywords(
			companyId, groupIds, classNameIds, keywords, active, andOperator,
			start, end, orderByComparator);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, long[] classNameIds, String keywords,
		boolean active) {

		return _calendarResourceLocalService.searchCount(
			companyId, groupIds, classNameIds, keywords, active);
	}

	@Override
	public void updateAsset(
			long userId, CalendarResource calendarResource,
			long[] assetCategoryIds, String[] assetTagNames, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarResourceLocalService.updateAsset(
			userId, calendarResource, assetCategoryIds, assetTagNames,
			priority);
	}

	/**
	 * Updates the calendar resource in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarResourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarResource the calendar resource
	 * @return the calendar resource that was updated
	 */
	@Override
	public CalendarResource updateCalendarResource(
		CalendarResource calendarResource) {

		return _calendarResourceLocalService.updateCalendarResource(
			calendarResource);
	}

	@Override
	public CalendarResource updateCalendarResource(
			long calendarResourceId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarResourceLocalService.updateCalendarResource(
			calendarResourceId, nameMap, descriptionMap, active,
			serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _calendarResourceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CalendarResource> getCTPersistence() {
		return _calendarResourceLocalService.getCTPersistence();
	}

	@Override
	public Class<CalendarResource> getModelClass() {
		return _calendarResourceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CalendarResource>, R, E>
				updateUnsafeFunction)
		throws E {

		return _calendarResourceLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CalendarResourceLocalService getWrappedService() {
		return _calendarResourceLocalService;
	}

	@Override
	public void setWrappedService(
		CalendarResourceLocalService calendarResourceLocalService) {

		_calendarResourceLocalService = calendarResourceLocalService;
	}

	private CalendarResourceLocalService _calendarResourceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1268399888