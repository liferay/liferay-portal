/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.persistence.impl;

import com.liferay.commerce.notification.exception.NoSuchNotificationTemplateException;
import com.liferay.commerce.notification.model.CommerceNotificationTemplate;
import com.liferay.commerce.notification.model.CommerceNotificationTemplateTable;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateImpl;
import com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateModelImpl;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationTemplatePersistence;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationTemplateUtil;
import com.liferay.commerce.notification.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce notification template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @deprecated
 * @generated
 */
@Component(service = CommerceNotificationTemplatePersistence.class)
@Deprecated
public class CommerceNotificationTemplatePersistenceImpl
	extends BasePersistenceImpl
		<CommerceNotificationTemplate, NoSuchNotificationTemplateException>
	implements CommerceNotificationTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceNotificationTemplateUtil</code> to access the commerce notification template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceNotificationTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce notification templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUuid_First(
			String uuid,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce notification templates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce notification templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<CommerceNotificationTemplate, NoSuchNotificationTemplateException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce notification template where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUUID_G(String uuid, long groupId)
		throws NoSuchNotificationTemplateException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce notification template where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce notification template where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce notification template that was removed
	 */
	@Override
	public CommerceNotificationTemplate removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchNotificationTemplateException {

		CommerceNotificationTemplate commerceNotificationTemplate =
			findByUUID_G(uuid, groupId);

		return remove(commerceNotificationTemplate);
	}

	/**
	 * Returns the number of commerce notification templates where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce notification templates where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce notification templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<CommerceNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the commerce notification templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce notification templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce notification templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<CommerceNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByG_E;

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_E(
		long groupId, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_E.find(
			finderCache, new Object[] {groupId, enabled}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByG_E_First(
			long groupId, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByG_E.findFirst(
			finderCache, new Object[] {groupId, enabled}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByG_E_First(
		long groupId, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_E.fetchFirst(
			finderCache, new Object[] {groupId, enabled}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates that the user has permissions to view where groupId = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_E(
		long groupId, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_E.filterFind(
			finderCache, new Object[] {groupId, enabled}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the commerce notification templates where groupId = &#63; and enabled = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 */
	@Override
	public void removeByG_E(long groupId, boolean enabled) {
		_collectionPersistenceFinderByG_E.remove(
			finderCache, new Object[] {groupId, enabled});
	}

	/**
	 * Returns the number of commerce notification templates where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByG_E(long groupId, boolean enabled) {
		return _collectionPersistenceFinderByG_E.count(
			finderCache, new Object[] {groupId, enabled});
	}

	/**
	 * Returns the number of commerce notification templates that the user has permission to view where groupId = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_E(long groupId, boolean enabled) {
		return _collectionPersistenceFinderByG_E.filterCount(
			finderCache, new Object[] {groupId, enabled}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<CommerceNotificationTemplate, NoSuchNotificationTemplateException>
			_collectionPersistenceFinderByG_T_E;

	/**
	 * Returns an ordered range of all the commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification templates
	 */
	@Override
	public List<CommerceNotificationTemplate> findByG_T_E(
		long groupId, String type, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T_E.find(
			finderCache, new Object[] {groupId, type, enabled}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template
	 * @throws NoSuchNotificationTemplateException if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByG_T_E_First(
			long groupId, String type, boolean enabled,
			OrderByComparator<CommerceNotificationTemplate> orderByComparator)
		throws NoSuchNotificationTemplateException {

		return _collectionPersistenceFinderByG_T_E.findFirst(
			finderCache, new Object[] {groupId, type, enabled},
			orderByComparator);
	}

	/**
	 * Returns the first commerce notification template in the ordered set where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification template, or <code>null</code> if a matching commerce notification template could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByG_T_E_First(
		long groupId, String type, boolean enabled,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_T_E.fetchFirst(
			finderCache, new Object[] {groupId, type, enabled},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce notification templates that the user has permissions to view where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @param start the lower bound of the range of commerce notification templates
	 * @param end the upper bound of the range of commerce notification templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public List<CommerceNotificationTemplate> filterFindByG_T_E(
		long groupId, String type, boolean enabled, int start, int end,
		OrderByComparator<CommerceNotificationTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_T_E.filterFind(
			finderCache, new Object[] {groupId, type, enabled}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 */
	@Override
	public void removeByG_T_E(long groupId, String type, boolean enabled) {
		_collectionPersistenceFinderByG_T_E.remove(
			finderCache, new Object[] {groupId, type, enabled});
	}

	/**
	 * Returns the number of commerce notification templates where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates
	 */
	@Override
	public int countByG_T_E(long groupId, String type, boolean enabled) {
		return _collectionPersistenceFinderByG_T_E.count(
			finderCache, new Object[] {groupId, type, enabled});
	}

	/**
	 * Returns the number of commerce notification templates that the user has permission to view where groupId = &#63; and type = &#63; and enabled = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param enabled the enabled
	 * @return the number of matching commerce notification templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_T_E(long groupId, String type, boolean enabled) {
		return _collectionPersistenceFinderByG_T_E.filterCount(
			finderCache, new Object[] {groupId, type, enabled}, groupId);
	}

	public CommerceNotificationTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("from", "from_");
		dbColumnNames.put("to", "to_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceNotificationTemplate.class);

		setModelImplClass(CommerceNotificationTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceNotificationTemplateTable.INSTANCE);
	}

	/**
	 * Creates a new commerce notification template with the primary key. Does not add the commerce notification template to the database.
	 *
	 * @param commerceNotificationTemplateId the primary key for the new commerce notification template
	 * @return the new commerce notification template
	 */
	@Override
	public CommerceNotificationTemplate create(
		long commerceNotificationTemplateId) {

		CommerceNotificationTemplate commerceNotificationTemplate =
			new CommerceNotificationTemplateImpl();

		commerceNotificationTemplate.setNew(true);
		commerceNotificationTemplate.setPrimaryKey(
			commerceNotificationTemplateId);

		String uuid = PortalUUIDUtil.generate();

		commerceNotificationTemplate.setUuid(uuid);

		commerceNotificationTemplate.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceNotificationTemplate;
	}

	/**
	 * Removes the commerce notification template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template that was removed
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate remove(
			long commerceNotificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return remove((Serializable)commerceNotificationTemplateId);
	}

	@Override
	protected CommerceNotificationTemplate removeImpl(
		CommerceNotificationTemplate commerceNotificationTemplate) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceNotificationTemplate)) {
				commerceNotificationTemplate =
					(CommerceNotificationTemplate)session.get(
						CommerceNotificationTemplateImpl.class,
						commerceNotificationTemplate.getPrimaryKeyObj());
			}

			if (commerceNotificationTemplate != null) {
				session.delete(commerceNotificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceNotificationTemplate != null) {
			clearCache(commerceNotificationTemplate);
		}

		return commerceNotificationTemplate;
	}

	@Override
	public CommerceNotificationTemplate updateImpl(
		CommerceNotificationTemplate commerceNotificationTemplate) {

		boolean isNew = commerceNotificationTemplate.isNew();

		if (!(commerceNotificationTemplate instanceof
				CommerceNotificationTemplateModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceNotificationTemplate.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceNotificationTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceNotificationTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceNotificationTemplate implementation " +
					commerceNotificationTemplate.getClass());
		}

		CommerceNotificationTemplateModelImpl
			commerceNotificationTemplateModelImpl =
				(CommerceNotificationTemplateModelImpl)
					commerceNotificationTemplate;

		if (Validator.isNull(commerceNotificationTemplate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceNotificationTemplate.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceNotificationTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceNotificationTemplate.setCreateDate(date);
			}
			else {
				commerceNotificationTemplate.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceNotificationTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceNotificationTemplate.setModifiedDate(date);
			}
			else {
				commerceNotificationTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceNotificationTemplate);
			}
			else {
				commerceNotificationTemplate =
					(CommerceNotificationTemplate)session.merge(
						commerceNotificationTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceNotificationTemplate, false);

		if (isNew) {
			commerceNotificationTemplate.setNew(false);
		}

		commerceNotificationTemplate.resetOriginalValues();

		return commerceNotificationTemplate;
	}

	/**
	 * Returns the commerce notification template with the primary key or throws a <code>NoSuchNotificationTemplateException</code> if it could not be found.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template
	 * @throws NoSuchNotificationTemplateException if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate findByPrimaryKey(
			long commerceNotificationTemplateId)
		throws NoSuchNotificationTemplateException {

		return findByPrimaryKey((Serializable)commerceNotificationTemplateId);
	}

	/**
	 * Returns the commerce notification template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceNotificationTemplateId the primary key of the commerce notification template
	 * @return the commerce notification template, or <code>null</code> if a commerce notification template with the primary key could not be found
	 */
	@Override
	public CommerceNotificationTemplate fetchByPrimaryKey(
		long commerceNotificationTemplateId) {

		return fetchByPrimaryKey((Serializable)commerceNotificationTemplateId);
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
		return "commerceNotificationTemplateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceNotificationTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce notification template persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
			_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
			CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceNotificationTemplate.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceNotificationTemplate::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceNotificationTemplate::getUuid),
				CommerceNotificationTemplate::getGroupId),
			_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"commerceNotificationTemplate.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceNotificationTemplate::getUuid),
			new FinderColumn<>(
				"commerceNotificationTemplate.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceNotificationTemplate::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationTemplate.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceNotificationTemplate::getUuid),
				new FinderColumn<>(
					"commerceNotificationTemplate.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationTemplate::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationTemplate.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationTemplate::getGroupId));

		_collectionPersistenceFinderByG_E =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "enabled"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "enabled"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "enabled"}, false),
				_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationTemplate.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationTemplate::getGroupId),
				new FinderColumn<>(
					"commerceNotificationTemplate.", "enabled",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceNotificationTemplate::isEnabled));

		_collectionPersistenceFinderByG_T_E =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T_E",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "type_", "enabled"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T_E",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "type_", "enabled"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T_E",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "type_", "enabled"}, 0, 2, false,
					null),
				_SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE,
				CommerceNotificationTemplateModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationTemplate.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationTemplate::getGroupId),
				new FinderColumn<>(
					"commerceNotificationTemplate.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceNotificationTemplate::getType),
				new FinderColumn<>(
					"commerceNotificationTemplate.", "enabled",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceNotificationTemplate::isEnabled));

		CommerceNotificationTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceNotificationTemplateUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceNotificationTemplateImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CommerceNotificationTemplateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE =
		"SELECT commerceNotificationTemplate FROM CommerceNotificationTemplate commerceNotificationTemplate";

	private static final String _SQL_SELECT_COMMERCENOTIFICATIONTEMPLATE_WHERE =
		"SELECT commerceNotificationTemplate FROM CommerceNotificationTemplate commerceNotificationTemplate WHERE ";

	private static final String _SQL_COUNT_COMMERCENOTIFICATIONTEMPLATE_WHERE =
		"SELECT COUNT(commerceNotificationTemplate) FROM CommerceNotificationTemplate commerceNotificationTemplate WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceNotificationTemplate exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceNotificationTemplatePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "from", "to", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:219642706