/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for NotificationRecipientSetting. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Gabriel Albuquerque
 * @see NotificationRecipientSettingLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface NotificationRecipientSettingLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.notification.service.impl.NotificationRecipientSettingLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the notification recipient setting local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link NotificationRecipientSettingLocalServiceUtil} if injection and service tracking are not available.
	 */
	@Indexable(type = IndexableType.REINDEX)
	public NotificationRecipientSetting addNotificationRecipientSetting(
			long userId, long notificationRecipientId, String name,
			Object value)
		throws PortalException;

	/**
	 * Adds the notification recipient setting to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSetting the notification recipient setting
	 * @return the notification recipient setting that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public NotificationRecipientSetting addNotificationRecipientSetting(
		NotificationRecipientSetting notificationRecipientSetting);

	/**
	 * Creates a new notification recipient setting with the primary key. Does not add the notification recipient setting to the database.
	 *
	 * @param notificationRecipientSettingId the primary key for the new notification recipient setting
	 * @return the new notification recipient setting
	 */
	@Transactional(enabled = false)
	public NotificationRecipientSetting createNotificationRecipientSetting(
		long notificationRecipientSettingId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the notification recipient setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting that was removed
	 * @throws PortalException if a notification recipient setting with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public NotificationRecipientSetting deleteNotificationRecipientSetting(
			long notificationRecipientSettingId)
		throws PortalException;

	/**
	 * Deletes the notification recipient setting from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSetting the notification recipient setting
	 * @return the notification recipient setting that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public NotificationRecipientSetting deleteNotificationRecipientSetting(
		NotificationRecipientSetting notificationRecipientSetting);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientSettingModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientSettingModelImpl</code>.
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
	public NotificationRecipientSetting fetchNotificationRecipientSetting(
		long notificationRecipientSettingId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public NotificationRecipientSetting fetchNotificationRecipientSetting(
		long notificationRecipientId, String name);

	/**
	 * Returns the notification recipient setting with the matching UUID and company.
	 *
	 * @param uuid the notification recipient setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient setting, or <code>null</code> if a matching notification recipient setting could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public NotificationRecipientSetting
		fetchNotificationRecipientSettingByUuidAndCompanyId(
			String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the notification recipient setting with the primary key.
	 *
	 * @param notificationRecipientSettingId the primary key of the notification recipient setting
	 * @return the notification recipient setting
	 * @throws PortalException if a notification recipient setting with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public NotificationRecipientSetting getNotificationRecipientSetting(
			long notificationRecipientSettingId)
		throws PortalException;

	/**
	 * Returns the notification recipient setting with the matching UUID and company.
	 *
	 * @param uuid the notification recipient setting's UUID
	 * @param companyId the primary key of the company
	 * @return the matching notification recipient setting
	 * @throws PortalException if a matching notification recipient setting could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public NotificationRecipientSetting
			getNotificationRecipientSettingByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns a range of all the notification recipient settings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.notification.model.impl.NotificationRecipientSettingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of notification recipient settings
	 * @param end the upper bound of the range of notification recipient settings (not inclusive)
	 * @return the range of notification recipient settings
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<NotificationRecipientSetting> getNotificationRecipientSettings(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<NotificationRecipientSetting> getNotificationRecipientSettings(
		long notificationRecipientId);

	/**
	 * Returns the number of notification recipient settings.
	 *
	 * @return the number of notification recipient settings
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getNotificationRecipientSettingsCount();

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

	@Indexable(type = IndexableType.REINDEX)
	public NotificationRecipientSetting updateNotificationRecipientSetting(
		long notificationRecipientId, String name, Object value);

	/**
	 * Updates the notification recipient setting in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect NotificationRecipientSettingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param notificationRecipientSetting the notification recipient setting
	 * @return the notification recipient setting that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public NotificationRecipientSetting updateNotificationRecipientSetting(
		NotificationRecipientSetting notificationRecipientSetting);

}