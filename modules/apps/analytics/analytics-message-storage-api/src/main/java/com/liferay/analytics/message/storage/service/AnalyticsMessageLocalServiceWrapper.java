/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service;

import com.liferay.analytics.message.storage.model.AnalyticsMessage;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AnalyticsMessageLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AnalyticsMessageLocalService
 * @generated
 */
public class AnalyticsMessageLocalServiceWrapper
	implements AnalyticsMessageLocalService,
			   ServiceWrapper<AnalyticsMessageLocalService> {

	public AnalyticsMessageLocalServiceWrapper() {
		this(null);
	}

	public AnalyticsMessageLocalServiceWrapper(
		AnalyticsMessageLocalService analyticsMessageLocalService) {

		_analyticsMessageLocalService = analyticsMessageLocalService;
	}

	/**
	 * Adds the analytics message to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsMessage the analytics message
	 * @return the analytics message that was added
	 */
	@Override
	public AnalyticsMessage addAnalyticsMessage(
		AnalyticsMessage analyticsMessage) {

		return _analyticsMessageLocalService.addAnalyticsMessage(
			analyticsMessage);
	}

	@Override
	public AnalyticsMessage addAnalyticsMessage(
			long companyId, long userId, byte[] body)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsMessageLocalService.addAnalyticsMessage(
			companyId, userId, body);
	}

	/**
	 * Creates a new analytics message with the primary key. Does not add the analytics message to the database.
	 *
	 * @param analyticsMessageId the primary key for the new analytics message
	 * @return the new analytics message
	 */
	@Override
	public AnalyticsMessage createAnalyticsMessage(long analyticsMessageId) {
		return _analyticsMessageLocalService.createAnalyticsMessage(
			analyticsMessageId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsMessageLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the analytics message from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsMessage the analytics message
	 * @return the analytics message that was removed
	 */
	@Override
	public AnalyticsMessage deleteAnalyticsMessage(
		AnalyticsMessage analyticsMessage) {

		return _analyticsMessageLocalService.deleteAnalyticsMessage(
			analyticsMessage);
	}

	/**
	 * Deletes the analytics message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsMessageId the primary key of the analytics message
	 * @return the analytics message that was removed
	 * @throws PortalException if a analytics message with the primary key could not be found
	 */
	@Override
	public AnalyticsMessage deleteAnalyticsMessage(long analyticsMessageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsMessageLocalService.deleteAnalyticsMessage(
			analyticsMessageId);
	}

	@Override
	public void deleteAnalyticsMessages(
		java.util.List<AnalyticsMessage> analyticsMessages) {

		_analyticsMessageLocalService.deleteAnalyticsMessages(
			analyticsMessages);
	}

	@Override
	public void deleteAnalyticsMessages(long companyId) {
		_analyticsMessageLocalService.deleteAnalyticsMessages(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsMessageLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _analyticsMessageLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _analyticsMessageLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _analyticsMessageLocalService.dynamicQuery();
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

		return _analyticsMessageLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl</code>.
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

		return _analyticsMessageLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl</code>.
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

		return _analyticsMessageLocalService.dynamicQuery(
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

		return _analyticsMessageLocalService.dynamicQueryCount(dynamicQuery);
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

		return _analyticsMessageLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AnalyticsMessage fetchAnalyticsMessage(long analyticsMessageId) {
		return _analyticsMessageLocalService.fetchAnalyticsMessage(
			analyticsMessageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _analyticsMessageLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the analytics message with the primary key.
	 *
	 * @param analyticsMessageId the primary key of the analytics message
	 * @return the analytics message
	 * @throws PortalException if a analytics message with the primary key could not be found
	 */
	@Override
	public AnalyticsMessage getAnalyticsMessage(long analyticsMessageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsMessageLocalService.getAnalyticsMessage(
			analyticsMessageId);
	}

	/**
	 * Returns a range of all the analytics messages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsMessageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of analytics messages
	 * @param end the upper bound of the range of analytics messages (not inclusive)
	 * @return the range of analytics messages
	 */
	@Override
	public java.util.List<AnalyticsMessage> getAnalyticsMessages(
		int start, int end) {

		return _analyticsMessageLocalService.getAnalyticsMessages(start, end);
	}

	@Override
	public java.util.List<AnalyticsMessage> getAnalyticsMessages(
		long companyId, int start, int end) {

		return _analyticsMessageLocalService.getAnalyticsMessages(
			companyId, start, end);
	}

	/**
	 * Returns the number of analytics messages.
	 *
	 * @return the number of analytics messages
	 */
	@Override
	public int getAnalyticsMessagesCount() {
		return _analyticsMessageLocalService.getAnalyticsMessagesCount();
	}

	@Override
	public java.util.List<Long> getCompanyIds() {
		return _analyticsMessageLocalService.getCompanyIds();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _analyticsMessageLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _analyticsMessageLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsMessageLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the analytics message in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsMessageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsMessage the analytics message
	 * @return the analytics message that was updated
	 */
	@Override
	public AnalyticsMessage updateAnalyticsMessage(
		AnalyticsMessage analyticsMessage) {

		return _analyticsMessageLocalService.updateAnalyticsMessage(
			analyticsMessage);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _analyticsMessageLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AnalyticsMessage> getCTPersistence() {
		return _analyticsMessageLocalService.getCTPersistence();
	}

	@Override
	public Class<AnalyticsMessage> getModelClass() {
		return _analyticsMessageLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AnalyticsMessage>, R, E>
				updateUnsafeFunction)
		throws E {

		return _analyticsMessageLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AnalyticsMessageLocalService getWrappedService() {
		return _analyticsMessageLocalService;
	}

	@Override
	public void setWrappedService(
		AnalyticsMessageLocalService analyticsMessageLocalService) {

		_analyticsMessageLocalService = analyticsMessageLocalService;
	}

	private AnalyticsMessageLocalService _analyticsMessageLocalService;

}