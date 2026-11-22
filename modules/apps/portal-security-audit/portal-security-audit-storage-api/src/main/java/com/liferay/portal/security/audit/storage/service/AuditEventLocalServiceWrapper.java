/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link AuditEventLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AuditEventLocalService
 * @generated
 */
public class AuditEventLocalServiceWrapper
	implements AuditEventLocalService, ServiceWrapper<AuditEventLocalService> {

	public AuditEventLocalServiceWrapper() {
		this(null);
	}

	public AuditEventLocalServiceWrapper(
		AuditEventLocalService auditEventLocalService) {

		_auditEventLocalService = auditEventLocalService;
	}

	/**
	 * Adds the audit event to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AuditEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param auditEvent the audit event
	 * @return the audit event that was added
	 */
	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
		addAuditEvent(
			com.liferay.portal.security.audit.storage.model.AuditEvent
				auditEvent) {

		return _auditEventLocalService.addAuditEvent(auditEvent);
	}

	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
		addAuditEvent(
			com.liferay.portal.kernel.audit.AuditMessage auditMessage) {

		return _auditEventLocalService.addAuditEvent(auditMessage);
	}

	@Override
	public void addAuditEvents(
		java.util.List<com.liferay.portal.kernel.audit.AuditMessage>
			auditMessages) {

		_auditEventLocalService.addAuditEvents(auditMessages);
	}

	/**
	 * Creates a new audit event with the primary key. Does not add the audit event to the database.
	 *
	 * @param auditEventId the primary key for the new audit event
	 * @return the new audit event
	 */
	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
		createAuditEvent(long auditEventId) {

		return _auditEventLocalService.createAuditEvent(auditEventId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _auditEventLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the audit event from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AuditEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param auditEvent the audit event
	 * @return the audit event that was removed
	 */
	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
		deleteAuditEvent(
			com.liferay.portal.security.audit.storage.model.AuditEvent
				auditEvent) {

		return _auditEventLocalService.deleteAuditEvent(auditEvent);
	}

	/**
	 * Deletes the audit event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AuditEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param auditEventId the primary key of the audit event
	 * @return the audit event that was removed
	 * @throws PortalException if a audit event with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
			deleteAuditEvent(long auditEventId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _auditEventLocalService.deleteAuditEvent(auditEventId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _auditEventLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _auditEventLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _auditEventLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _auditEventLocalService.dynamicQuery();
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

		return _auditEventLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventModelImpl</code>.
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

		return _auditEventLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventModelImpl</code>.
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

		return _auditEventLocalService.dynamicQuery(
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

		return _auditEventLocalService.dynamicQueryCount(dynamicQuery);
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

		return _auditEventLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
		fetchAuditEvent(long auditEventId) {

		return _auditEventLocalService.fetchAuditEvent(auditEventId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _auditEventLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the audit event with the primary key.
	 *
	 * @param auditEventId the primary key of the audit event
	 * @return the audit event
	 * @throws PortalException if a audit event with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
			getAuditEvent(long auditEventId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _auditEventLocalService.getAuditEvent(auditEventId);
	}

	/**
	 * Returns a range of all the audit events.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of audit events
	 * @param end the upper bound of the range of audit events (not inclusive)
	 * @return the range of audit events
	 */
	@Override
	public java.util.List
		<com.liferay.portal.security.audit.storage.model.AuditEvent>
			getAuditEvents(int start, int end) {

		return _auditEventLocalService.getAuditEvents(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.portal.security.audit.storage.model.AuditEvent>
			getAuditEvents(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.security.audit.storage.model.AuditEvent>
						orderByComparator) {

		return _auditEventLocalService.getAuditEvents(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.portal.security.audit.storage.model.AuditEvent>
			getAuditEvents(
				long companyId, long groupId, long userId, String userName,
				java.util.Date createDateGT, java.util.Date createDateLT,
				long[] accountEntryIds, String className, String classPK,
				String clientHost, String clientIP, String contextName,
				String eventType, String serverName, int serverPort,
				String sessionID, boolean andSearch, int start, int end) {

		return _auditEventLocalService.getAuditEvents(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.portal.security.audit.storage.model.AuditEvent>
			getAuditEvents(
				long companyId, long groupId, long userId, String userName,
				java.util.Date createDateGT, java.util.Date createDateLT,
				long[] accountEntryIds, String className, String classPK,
				String clientHost, String clientIP, String contextName,
				String eventType, String serverName, int serverPort,
				String sessionID, boolean andSearch, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.security.audit.storage.model.AuditEvent>
						orderByComparator) {

		return _auditEventLocalService.getAuditEvents(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch, start, end, orderByComparator);
	}

	/**
	 * Returns the number of audit events.
	 *
	 * @return the number of audit events
	 */
	@Override
	public int getAuditEventsCount() {
		return _auditEventLocalService.getAuditEventsCount();
	}

	@Override
	public int getAuditEventsCount(long companyId) {
		return _auditEventLocalService.getAuditEventsCount(companyId);
	}

	@Override
	public int getAuditEventsCount(
		long companyId, long groupId, long userId, String userName,
		java.util.Date createDateGT, java.util.Date createDateLT,
		long[] accountEntryIds, String className, String classPK,
		String clientHost, String clientIP, String contextName,
		String eventType, String serverName, int serverPort, String sessionID,
		boolean andSearch) {

		return _auditEventLocalService.getAuditEventsCount(
			companyId, groupId, userId, userName, createDateGT, createDateLT,
			accountEntryIds, className, classPK, clientHost, clientIP,
			contextName, eventType, serverName, serverPort, sessionID,
			andSearch);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _auditEventLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _auditEventLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _auditEventLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the audit event in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AuditEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param auditEvent the audit event
	 * @return the audit event that was updated
	 */
	@Override
	public com.liferay.portal.security.audit.storage.model.AuditEvent
		updateAuditEvent(
			com.liferay.portal.security.audit.storage.model.AuditEvent
				auditEvent) {

		return _auditEventLocalService.updateAuditEvent(auditEvent);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _auditEventLocalService.getBasePersistence();
	}

	@Override
	public AuditEventLocalService getWrappedService() {
		return _auditEventLocalService;
	}

	@Override
	public void setWrappedService(
		AuditEventLocalService auditEventLocalService) {

		_auditEventLocalService = auditEventLocalService;
	}

	private AuditEventLocalService _auditEventLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1098962820