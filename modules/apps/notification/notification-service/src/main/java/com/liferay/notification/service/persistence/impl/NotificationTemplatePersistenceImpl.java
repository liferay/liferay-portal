/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.impl;

import com.liferay.notification.exception.DuplicateNotificationTemplateExternalReferenceCodeException;
import com.liferay.notification.exception.NoSuchNotificationTemplateException;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.model.NotificationTemplateTable;
import com.liferay.notification.model.impl.NotificationTemplateImpl;
import com.liferay.notification.model.impl.NotificationTemplateModelImpl;
import com.liferay.notification.service.persistence.NotificationTemplatePersistence;
import com.liferay.notification.service.persistence.NotificationTemplateUtil;
import com.liferay.notification.service.persistence.impl.constants.NotificationPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the notification template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = NotificationTemplatePersistence.class)
public class NotificationTemplatePersistenceImpl
	extends BasePersistenceImpl
		<NotificationTemplate, NoSuchNotificationTemplateException>
	implements NotificationTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NotificationTemplateUtil</code> to access the notification template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NotificationTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<NotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the notification templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of notification templates
	 * @param end the upper bound of the range of notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification templates
	 */
	@Override
	public List<NotificationTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<NotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template
	 * @throws NoSuchNotificationTemplateException if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate findByUuid_First(
			String uuid,
			OrderByComparator<NotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template, or <code>null</code> if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate fetchByUuid_First(
		String uuid,
		OrderByComparator<NotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the notification templates that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of notification templates
	 * @param end the upper bound of the range of notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification templates that the user has permission to view
	 */
	@Override
	public List<NotificationTemplate> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<NotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the notification templates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of notification templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching notification templates
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of notification templates that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<NotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification templates
	 * @param end the upper bound of the range of notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification templates
	 */
	@Override
	public List<NotificationTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<NotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template
	 * @throws NoSuchNotificationTemplateException if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<NotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template, or <code>null</code> if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<NotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the notification templates that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification templates
	 * @param end the upper bound of the range of notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification templates that the user has permission to view
	 */
	@Override
	public List<NotificationTemplate> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<NotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the notification templates where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching notification templates
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of notification templates that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<NotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the notification templates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification templates
	 * @param end the upper bound of the range of notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification templates
	 */
	@Override
	public List<NotificationTemplate> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<NotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first notification template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template
	 * @throws NoSuchNotificationTemplateException if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate findByCompanyId_First(
			long companyId,
			OrderByComparator<NotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first notification template in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification template, or <code>null</code> if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate fetchByCompanyId_First(
		long companyId,
		OrderByComparator<NotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the notification templates that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification templates
	 * @param end the upper bound of the range of notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification templates that the user has permission to view
	 */
	@Override
	public List<NotificationTemplate> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<NotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the notification templates where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of notification templates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching notification templates
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of notification templates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private UniquePersistenceFinder
		<NotificationTemplate, NoSuchNotificationTemplateException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the notification template where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching notification template
	 * @throws NoSuchNotificationTemplateException if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchNotificationTemplateException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the notification template where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching notification template, or <code>null</code> if a matching notification template could not be found
	 */
	@Override
	public NotificationTemplate fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the notification template where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the notification template that was removed
	 */
	@Override
	public NotificationTemplate removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchNotificationTemplateException {

		NotificationTemplate notificationTemplate = findByERC_C(
			externalReferenceCode, companyId);

		return remove(notificationTemplate);
	}

	/**
	 * Returns the number of notification templates where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching notification templates
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public NotificationTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("system", "system_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(NotificationTemplate.class);

		setModelImplClass(NotificationTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(NotificationTemplateTable.INSTANCE);
	}

	/**
	 * Creates a new notification template with the primary key. Does not add the notification template to the database.
	 *
	 * @param notificationTemplateId the primary key for the new notification template
	 * @return the new notification template
	 */
	@Override
	public NotificationTemplate create(long notificationTemplateId) {
		NotificationTemplate notificationTemplate =
			new NotificationTemplateImpl();

		notificationTemplate.setNew(true);
		notificationTemplate.setPrimaryKey(notificationTemplateId);

		String uuid = PortalUUIDUtil.generate();

		notificationTemplate.setUuid(uuid);

		notificationTemplate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return notificationTemplate;
	}

	/**
	 * Removes the notification template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param notificationTemplateId the primary key of the notification template
	 * @return the notification template that was removed
	 * @throws NoSuchNotificationTemplateException if a notification template with the primary key could not be found
	 */
	@Override
	public NotificationTemplate remove(long notificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return remove((Serializable)notificationTemplateId);
	}

	@Override
	protected NotificationTemplate removeImpl(
		NotificationTemplate notificationTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(notificationTemplate)) {
				notificationTemplate = (NotificationTemplate)session.get(
					NotificationTemplateImpl.class,
					notificationTemplate.getPrimaryKeyObj());
			}

			if (notificationTemplate != null) {
				session.delete(notificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (notificationTemplate != null) {
			clearCache(notificationTemplate);
		}

		return notificationTemplate;
	}

	@Override
	public NotificationTemplate updateImpl(
		NotificationTemplate notificationTemplate) {

		boolean isNew = notificationTemplate.isNew();

		if (!(notificationTemplate instanceof NotificationTemplateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(notificationTemplate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					notificationTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in notificationTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NotificationTemplate implementation " +
					notificationTemplate.getClass());
		}

		NotificationTemplateModelImpl notificationTemplateModelImpl =
			(NotificationTemplateModelImpl)notificationTemplate;

		if (Validator.isNull(notificationTemplate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			notificationTemplate.setUuid(uuid);
		}

		if (Validator.isNull(notificationTemplate.getExternalReferenceCode())) {
			notificationTemplate.setExternalReferenceCode(
				notificationTemplate.getUuid());
		}
		else {
			if (!Objects.equals(
					notificationTemplateModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					notificationTemplate.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = notificationTemplate.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = notificationTemplate.getPrimaryKey();
					}

					try {
						notificationTemplate.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								NotificationTemplate.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								notificationTemplate.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			NotificationTemplate ercNotificationTemplate = fetchByERC_C(
				notificationTemplate.getExternalReferenceCode(),
				notificationTemplate.getCompanyId());

			if (isNew) {
				if (ercNotificationTemplate != null) {
					throw new DuplicateNotificationTemplateExternalReferenceCodeException(
						"Duplicate notification template with external reference code " +
							notificationTemplate.getExternalReferenceCode() +
								" and company " +
									notificationTemplate.getCompanyId());
				}
			}
			else {
				if ((ercNotificationTemplate != null) &&
					(notificationTemplate.getNotificationTemplateId() !=
						ercNotificationTemplate.getNotificationTemplateId())) {

					throw new DuplicateNotificationTemplateExternalReferenceCodeException(
						"Duplicate notification template with external reference code " +
							notificationTemplate.getExternalReferenceCode() +
								" and company " +
									notificationTemplate.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (notificationTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				notificationTemplate.setCreateDate(date);
			}
			else {
				notificationTemplate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!notificationTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				notificationTemplate.setModifiedDate(date);
			}
			else {
				notificationTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(notificationTemplate);
			}
			else {
				notificationTemplate = (NotificationTemplate)session.merge(
					notificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(notificationTemplate, false);

		if (isNew) {
			notificationTemplate.setNew(false);
		}

		notificationTemplate.resetOriginalValues();

		return notificationTemplate;
	}

	/**
	 * Returns the notification template with the primary key or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param notificationTemplateId the primary key of the notification template
	 * @return the notification template
	 * @throws NoSuchNotificationTemplateException if a notification template with the primary key could not be found
	 */
	@Override
	public NotificationTemplate findByPrimaryKey(long notificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return findByPrimaryKey((Serializable)notificationTemplateId);
	}

	/**
	 * Returns the notification template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param notificationTemplateId the primary key of the notification template
	 * @return the notification template, or <code>null</code> if a notification template with the primary key could not be found
	 */
	@Override
	public NotificationTemplate fetchByPrimaryKey(long notificationTemplateId) {
		return fetchByPrimaryKey((Serializable)notificationTemplateId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "notificationTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NOTIFICATIONTEMPLATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NotificationTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the notification template persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, false, null),
				_SQL_SELECT_NOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_NOTIFICATIONTEMPLATE_WHERE,
				NotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"notificationTemplate.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					NotificationTemplate::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_NOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_NOTIFICATIONTEMPLATE_WHERE,
				NotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"notificationTemplate.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					NotificationTemplate::getUuid),
				new FinderColumn<>(
					"notificationTemplate.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					NotificationTemplate::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_NOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_NOTIFICATIONTEMPLATE_WHERE,
				NotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"notificationTemplate.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					NotificationTemplate::getCompanyId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					NotificationTemplate::getExternalReferenceCode),
				NotificationTemplate::getCompanyId),
			_SQL_SELECT_NOTIFICATIONTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"notificationTemplate.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				NotificationTemplate::getExternalReferenceCode),
			new FinderColumn<>(
				"notificationTemplate.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, NotificationTemplate::getCompanyId));

		NotificationTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		NotificationTemplateUtil.setPersistence(null);

		entityCache.removeCache(NotificationTemplateImpl.class.getName());
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		NotificationTemplateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_NOTIFICATIONTEMPLATE =
		"SELECT notificationTemplate FROM NotificationTemplate notificationTemplate";

	private static final String _SQL_SELECT_NOTIFICATIONTEMPLATE_WHERE =
		"SELECT notificationTemplate FROM NotificationTemplate notificationTemplate WHERE ";

	private static final String _SQL_COUNT_NOTIFICATIONTEMPLATE_WHERE =
		"SELECT COUNT(notificationTemplate) FROM NotificationTemplate notificationTemplate WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "system", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-432925848