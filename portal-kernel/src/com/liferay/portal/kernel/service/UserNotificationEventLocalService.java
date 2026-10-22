/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for UserNotificationEvent. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserNotificationEventLocalServiceUtil
 * @generated
 */
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.portal.kernel.model.UserNotificationEvent"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface UserNotificationEventLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserNotificationEventLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the user notification event local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link UserNotificationEventLocalServiceUtil} if injection and service tracking are not available.
	 */
	public UserNotificationEvent addUserNotificationEvent(
			long userId, boolean delivered, boolean actionRequired,
			NotificationEvent notificationEvent)
		throws PortalException;

	public UserNotificationEvent addUserNotificationEvent(
			long userId, NotificationEvent notificationEvent)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public UserNotificationEvent addUserNotificationEvent(
			long userId, String type, long timestamp, int deliveryType,
			long deliverBy, boolean delivered, String payload,
			boolean actionRequired, boolean archived,
			ServiceContext serviceContext)
		throws PortalException;

	public UserNotificationEvent addUserNotificationEvent(
			long userId, String type, long timestamp, int deliveryType,
			long deliverBy, String payload, boolean archived,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the user notification event to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEvent the user notification event
	 * @return the user notification event that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public UserNotificationEvent addUserNotificationEvent(
		UserNotificationEvent userNotificationEvent);

	public List<UserNotificationEvent> addUserNotificationEvents(
			long userId, Collection<NotificationEvent> notificationEvents)
		throws PortalException;

	public void archiveUserNotificationEvents(
			long userId, int deliveryType, boolean actionRequired)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new user notification event with the primary key. Does not add the user notification event to the database.
	 *
	 * @param userNotificationEventId the primary key for the new user notification event
	 * @return the new user notification event
	 */
	@Transactional(enabled = false)
	public UserNotificationEvent createUserNotificationEvent(
		long userNotificationEventId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the user notification event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event that was removed
	 * @throws PortalException if a user notification event with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public UserNotificationEvent deleteUserNotificationEvent(
			long userNotificationEventId)
		throws PortalException;

	public void deleteUserNotificationEvent(String uuid, long companyId)
		throws PortalException;

	/**
	 * Deletes the user notification event from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEvent the user notification event
	 * @return the user notification event that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public UserNotificationEvent deleteUserNotificationEvent(
		UserNotificationEvent userNotificationEvent);

	public void deleteUserNotificationEvents(
			Collection<String> uuids, long companyId)
		throws PortalException;

	public void deleteUserNotificationEvents(long userId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserNotificationEvent fetchUserNotificationEvent(
		long userNotificationEventId);

	/**
	 * Returns the user notification event with the matching UUID and company.
	 *
	 * @param uuid the user notification event's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserNotificationEvent fetchUserNotificationEventByUuidAndCompanyId(
		String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getArchivedUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDeliveredArchivedUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getDeliveredUserNotificationEvents(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDeliveredUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getTypeNotificationEvents(String type);

	/**
	 * Returns the user notification event with the primary key.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event
	 * @throws PortalException if a user notification event with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserNotificationEvent getUserNotificationEvent(
			long userNotificationEventId)
		throws PortalException;

	/**
	 * Returns the user notification event with the matching UUID and company.
	 *
	 * @param uuid the user notification event's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user notification event
	 * @throws PortalException if a matching user notification event could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public UserNotificationEvent getUserNotificationEventByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the user notification events.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of user notification events
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getUserNotificationEvents(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getUserNotificationEvents(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<UserNotificationEvent> getUserNotificationEvents(
		long userId, String type, long timestamp, boolean delivered);

	/**
	 * Returns the number of user notification events.
	 *
	 * @return the number of user notification events
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserNotificationEventsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserNotificationEventsCount(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserNotificationEventsCount(
		long userId, int deliveryType, boolean delivered, boolean archived);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserNotificationEventsCount(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserNotificationEventsCount(
		long userId, String type, Map<String, String> payloadParameters);

	public UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType, boolean delivered,
			boolean actionRequired, JSONObject notificationEventJSONObject)
		throws PortalException;

	public UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType,
			boolean actionRequired, JSONObject notificationEventJSONObject)
		throws PortalException;

	public UserNotificationEvent sendUserNotificationEvents(
			long userId, String portletId, int deliveryType,
			JSONObject notificationEventJSONObject)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public UserNotificationEvent updateUserNotificationEvent(
		String uuid, long companyId, boolean archive);

	/**
	 * Updates the user notification event in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserNotificationEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userNotificationEvent the user notification event
	 * @return the user notification event that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public UserNotificationEvent updateUserNotificationEvent(
		UserNotificationEvent userNotificationEvent);

	public List<UserNotificationEvent> updateUserNotificationEvents(
		Collection<String> uuids, long companyId, boolean archive);

}
// LIFERAY-SERVICE-BUILDER-HASH:933702910