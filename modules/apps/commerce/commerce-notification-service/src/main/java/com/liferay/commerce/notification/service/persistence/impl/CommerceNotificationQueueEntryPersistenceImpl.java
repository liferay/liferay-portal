/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service.persistence.impl;

import com.liferay.commerce.notification.exception.NoSuchNotificationQueueEntryException;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntry;
import com.liferay.commerce.notification.model.CommerceNotificationQueueEntryTable;
import com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryImpl;
import com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryModelImpl;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationQueueEntryPersistence;
import com.liferay.commerce.notification.service.persistence.CommerceNotificationQueueEntryUtil;
import com.liferay.commerce.notification.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

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
 * The persistence implementation for the commerce notification queue entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @deprecated
 * @generated
 */
@Component(service = CommerceNotificationQueueEntryPersistence.class)
@Deprecated
public class CommerceNotificationQueueEntryPersistenceImpl
	extends BasePersistenceImpl
		<CommerceNotificationQueueEntry, NoSuchNotificationQueueEntryException>
	implements CommerceNotificationQueueEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceNotificationQueueEntryUtil</code> to access the commerce notification queue entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceNotificationQueueEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceNotificationQueueEntry, NoSuchNotificationQueueEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce notification queue entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceNotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry, or <code>null</code> if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the commerce notification queue entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce notification queue entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce notification queue entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationQueueEntry, NoSuchNotificationQueueEntryException>
			_collectionPersistenceFinderByCommerceNotificationTemplateId;

	/**
	 * Returns an ordered range of all the commerce notification queue entries where commerceNotificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry>
		findByCommerceNotificationTemplateId(
			long commerceNotificationTemplateId, int start, int end,
			OrderByComparator<CommerceNotificationQueueEntry> orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			find(
				finderCache, new Object[] {commerceNotificationTemplateId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where commerceNotificationTemplateId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry
			findByCommerceNotificationTemplateId_First(
				long commerceNotificationTemplateId,
				OrderByComparator<CommerceNotificationQueueEntry>
					orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			findFirst(
				finderCache, new Object[] {commerceNotificationTemplateId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where commerceNotificationTemplateId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry, or <code>null</code> if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry
		fetchByCommerceNotificationTemplateId_First(
			long commerceNotificationTemplateId,
			OrderByComparator<CommerceNotificationQueueEntry>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			fetchFirst(
				finderCache, new Object[] {commerceNotificationTemplateId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce notification queue entries where commerceNotificationTemplateId = &#63; from the database.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 */
	@Override
	public void removeByCommerceNotificationTemplateId(
		long commerceNotificationTemplateId) {

		_collectionPersistenceFinderByCommerceNotificationTemplateId.remove(
			finderCache, new Object[] {commerceNotificationTemplateId});
	}

	/**
	 * Returns the number of commerce notification queue entries where commerceNotificationTemplateId = &#63;.
	 *
	 * @param commerceNotificationTemplateId the commerce notification template ID
	 * @return the number of matching commerce notification queue entries
	 */
	@Override
	public int countByCommerceNotificationTemplateId(
		long commerceNotificationTemplateId) {

		return _collectionPersistenceFinderByCommerceNotificationTemplateId.
			count(finderCache, new Object[] {commerceNotificationTemplateId});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationQueueEntry, NoSuchNotificationQueueEntryException>
			_collectionPersistenceFinderBySent;

	/**
	 * Returns an ordered range of all the commerce notification queue entries where sent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sent the sent
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry> findBySent(
		boolean sent, int start, int end,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySent.find(
			finderCache, new Object[] {sent}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where sent = &#63;.
	 *
	 * @param sent the sent
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry findBySent_First(
			boolean sent,
			OrderByComparator<CommerceNotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		return _collectionPersistenceFinderBySent.findFirst(
			finderCache, new Object[] {sent}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where sent = &#63;.
	 *
	 * @param sent the sent
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry, or <code>null</code> if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry fetchBySent_First(
		boolean sent,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator) {

		return _collectionPersistenceFinderBySent.fetchFirst(
			finderCache, new Object[] {sent}, orderByComparator);
	}

	/**
	 * Removes all the commerce notification queue entries where sent = &#63; from the database.
	 *
	 * @param sent the sent
	 */
	@Override
	public void removeBySent(boolean sent) {
		_collectionPersistenceFinderBySent.remove(
			finderCache, new Object[] {sent});
	}

	/**
	 * Returns the number of commerce notification queue entries where sent = &#63;.
	 *
	 * @param sent the sent
	 * @return the number of matching commerce notification queue entries
	 */
	@Override
	public int countBySent(boolean sent) {
		return _collectionPersistenceFinderBySent.count(
			finderCache, new Object[] {sent});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationQueueEntry, NoSuchNotificationQueueEntryException>
			_collectionPersistenceFinderByLtSentDate;

	/**
	 * Returns all the commerce notification queue entries where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @return the matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry> findByLtSentDate(
		Date sentDate) {

		return findByLtSentDate(
			sentDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce notification queue entries where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @return the range of matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry> findByLtSentDate(
		Date sentDate, int start, int end) {

		return findByLtSentDate(sentDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce notification queue entries where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry> findByLtSentDate(
		Date sentDate, int start, int end,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator) {

		return findByLtSentDate(sentDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce notification queue entries where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry> findByLtSentDate(
		Date sentDate, int start, int end,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtSentDate.find(
			finderCache, new Object[] {sentDate}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry findByLtSentDate_First(
			Date sentDate,
			OrderByComparator<CommerceNotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		return _collectionPersistenceFinderByLtSentDate.findFirst(
			finderCache, new Object[] {sentDate}, orderByComparator);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry, or <code>null</code> if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry fetchByLtSentDate_First(
		Date sentDate,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtSentDate.fetchFirst(
			finderCache, new Object[] {sentDate}, orderByComparator);
	}

	/**
	 * Removes all the commerce notification queue entries where sentDate &lt; &#63; from the database.
	 *
	 * @param sentDate the sent date
	 */
	@Override
	public void removeByLtSentDate(Date sentDate) {
		_collectionPersistenceFinderByLtSentDate.remove(
			finderCache, new Object[] {sentDate});
	}

	/**
	 * Returns the number of commerce notification queue entries where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @return the number of matching commerce notification queue entries
	 */
	@Override
	public int countByLtSentDate(Date sentDate) {
		return _collectionPersistenceFinderByLtSentDate.count(
			finderCache, new Object[] {sentDate});
	}

	private CollectionPersistenceFinder
		<CommerceNotificationQueueEntry, NoSuchNotificationQueueEntryException>
			_collectionPersistenceFinderByG_C_C_S;

	/**
	 * Returns an ordered range of all the commerce notification queue entries where groupId = &#63; and classNameId = &#63; and classPK = &#63; and sent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param sent the sent
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce notification queue entries
	 */
	@Override
	public List<CommerceNotificationQueueEntry> findByG_C_C_S(
		long groupId, long classNameId, long classPK, boolean sent, int start,
		int end,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_S.find(
			finderCache, new Object[] {groupId, classNameId, classPK, sent},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and sent = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param sent the sent
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry findByG_C_C_S_First(
			long groupId, long classNameId, long classPK, boolean sent,
			OrderByComparator<CommerceNotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		return _collectionPersistenceFinderByG_C_C_S.findFirst(
			finderCache, new Object[] {groupId, classNameId, classPK, sent},
			orderByComparator);
	}

	/**
	 * Returns the first commerce notification queue entry in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and sent = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param sent the sent
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce notification queue entry, or <code>null</code> if a matching commerce notification queue entry could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry fetchByG_C_C_S_First(
		long groupId, long classNameId, long classPK, boolean sent,
		OrderByComparator<CommerceNotificationQueueEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_S.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, classPK, sent},
			orderByComparator);
	}

	/**
	 * Removes all the commerce notification queue entries where groupId = &#63; and classNameId = &#63; and classPK = &#63; and sent = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param sent the sent
	 */
	@Override
	public void removeByG_C_C_S(
		long groupId, long classNameId, long classPK, boolean sent) {

		_collectionPersistenceFinderByG_C_C_S.remove(
			finderCache, new Object[] {groupId, classNameId, classPK, sent});
	}

	/**
	 * Returns the number of commerce notification queue entries where groupId = &#63; and classNameId = &#63; and classPK = &#63; and sent = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param sent the sent
	 * @return the number of matching commerce notification queue entries
	 */
	@Override
	public int countByG_C_C_S(
		long groupId, long classNameId, long classPK, boolean sent) {

		return _collectionPersistenceFinderByG_C_C_S.count(
			finderCache, new Object[] {groupId, classNameId, classPK, sent});
	}

	public CommerceNotificationQueueEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceNotificationQueueEntryId", "CNotificationQueueEntryId");
		dbColumnNames.put("from", "from_");
		dbColumnNames.put("to", "to_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceNotificationQueueEntry.class);

		setModelImplClass(CommerceNotificationQueueEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceNotificationQueueEntryTable.INSTANCE);
	}

	/**
	 * Creates a new commerce notification queue entry with the primary key. Does not add the commerce notification queue entry to the database.
	 *
	 * @param commerceNotificationQueueEntryId the primary key for the new commerce notification queue entry
	 * @return the new commerce notification queue entry
	 */
	@Override
	public CommerceNotificationQueueEntry create(
		long commerceNotificationQueueEntryId) {

		CommerceNotificationQueueEntry commerceNotificationQueueEntry =
			new CommerceNotificationQueueEntryImpl();

		commerceNotificationQueueEntry.setNew(true);
		commerceNotificationQueueEntry.setPrimaryKey(
			commerceNotificationQueueEntryId);

		commerceNotificationQueueEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceNotificationQueueEntry;
	}

	/**
	 * Removes the commerce notification queue entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceNotificationQueueEntryId the primary key of the commerce notification queue entry
	 * @return the commerce notification queue entry that was removed
	 * @throws NoSuchNotificationQueueEntryException if a commerce notification queue entry with the primary key could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry remove(
			long commerceNotificationQueueEntryId)
		throws NoSuchNotificationQueueEntryException {

		return remove((Serializable)commerceNotificationQueueEntryId);
	}

	@Override
	protected CommerceNotificationQueueEntry removeImpl(
		CommerceNotificationQueueEntry commerceNotificationQueueEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceNotificationQueueEntry)) {
				commerceNotificationQueueEntry =
					(CommerceNotificationQueueEntry)session.get(
						CommerceNotificationQueueEntryImpl.class,
						commerceNotificationQueueEntry.getPrimaryKeyObj());
			}

			if (commerceNotificationQueueEntry != null) {
				session.delete(commerceNotificationQueueEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceNotificationQueueEntry != null) {
			clearCache(commerceNotificationQueueEntry);
		}

		return commerceNotificationQueueEntry;
	}

	@Override
	public CommerceNotificationQueueEntry updateImpl(
		CommerceNotificationQueueEntry commerceNotificationQueueEntry) {

		boolean isNew = commerceNotificationQueueEntry.isNew();

		if (!(commerceNotificationQueueEntry instanceof
				CommerceNotificationQueueEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceNotificationQueueEntry.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceNotificationQueueEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceNotificationQueueEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceNotificationQueueEntry implementation " +
					commerceNotificationQueueEntry.getClass());
		}

		CommerceNotificationQueueEntryModelImpl
			commerceNotificationQueueEntryModelImpl =
				(CommerceNotificationQueueEntryModelImpl)
					commerceNotificationQueueEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceNotificationQueueEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceNotificationQueueEntry.setCreateDate(date);
			}
			else {
				commerceNotificationQueueEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceNotificationQueueEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceNotificationQueueEntry.setModifiedDate(date);
			}
			else {
				commerceNotificationQueueEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceNotificationQueueEntry);
			}
			else {
				commerceNotificationQueueEntry =
					(CommerceNotificationQueueEntry)session.merge(
						commerceNotificationQueueEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceNotificationQueueEntry, false);

		if (isNew) {
			commerceNotificationQueueEntry.setNew(false);
		}

		commerceNotificationQueueEntry.resetOriginalValues();

		return commerceNotificationQueueEntry;
	}

	/**
	 * Returns the commerce notification queue entry with the primary key or throws a <code>NoSuchNotificationQueueEntryException</code> if it could not be found.
	 *
	 * @param commerceNotificationQueueEntryId the primary key of the commerce notification queue entry
	 * @return the commerce notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a commerce notification queue entry with the primary key could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry findByPrimaryKey(
			long commerceNotificationQueueEntryId)
		throws NoSuchNotificationQueueEntryException {

		return findByPrimaryKey((Serializable)commerceNotificationQueueEntryId);
	}

	/**
	 * Returns the commerce notification queue entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceNotificationQueueEntryId the primary key of the commerce notification queue entry
	 * @return the commerce notification queue entry, or <code>null</code> if a commerce notification queue entry with the primary key could not be found
	 */
	@Override
	public CommerceNotificationQueueEntry fetchByPrimaryKey(
		long commerceNotificationQueueEntryId) {

		return fetchByPrimaryKey(
			(Serializable)commerceNotificationQueueEntryId);
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
		return "CNotificationQueueEntryId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceNotificationQueueEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceNotificationQueueEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce notification queue entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				CommerceNotificationQueueEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationQueueEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationQueueEntry::getGroupId));

		_collectionPersistenceFinderByCommerceNotificationTemplateId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceNotificationTemplateId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceNotificationTemplateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceNotificationTemplateId",
					new String[] {Long.class.getName()},
					new String[] {"commerceNotificationTemplateId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceNotificationTemplateId",
					new String[] {Long.class.getName()},
					new String[] {"commerceNotificationTemplateId"}, false),
				_SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				CommerceNotificationQueueEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationQueueEntry.",
					"commerceNotificationTemplateId", FinderColumn.Type.LONG,
					"=", true, true,
					CommerceNotificationQueueEntry::
						getCommerceNotificationTemplateId));

		_collectionPersistenceFinderBySent = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySent",
				new String[] {
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"sent"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBySent",
				new String[] {Boolean.class.getName()}, new String[] {"sent"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBySent",
				new String[] {Boolean.class.getName()}, new String[] {"sent"},
				false),
			_SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
			_SQL_COUNT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
			CommerceNotificationQueueEntryModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceNotificationQueueEntry.", "sent",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceNotificationQueueEntry::isSent));

		_collectionPersistenceFinderByLtSentDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtSentDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"sentDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtSentDate",
					new String[] {Date.class.getName()},
					new String[] {"sentDate"}, false),
				_SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				CommerceNotificationQueueEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationQueueEntry.", "sentDate",
					FinderColumn.Type.DATE, "<", true, true,
					CommerceNotificationQueueEntry::getSentDate));

		_collectionPersistenceFinderByG_C_C_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK", "sent"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK", "sent"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK", "sent"},
					false),
				_SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				_SQL_COUNT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE,
				CommerceNotificationQueueEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceNotificationQueueEntry.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationQueueEntry::getGroupId),
				new FinderColumn<>(
					"commerceNotificationQueueEntry.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationQueueEntry::getClassNameId),
				new FinderColumn<>(
					"commerceNotificationQueueEntry.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceNotificationQueueEntry::getClassPK),
				new FinderColumn<>(
					"commerceNotificationQueueEntry.", "sent",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommerceNotificationQueueEntry::isSent));

		CommerceNotificationQueueEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceNotificationQueueEntryUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceNotificationQueueEntryImpl.class.getName());
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
		CommerceNotificationQueueEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY =
		"SELECT commerceNotificationQueueEntry FROM CommerceNotificationQueueEntry commerceNotificationQueueEntry";

	private static final String
		_SQL_SELECT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE =
			"SELECT commerceNotificationQueueEntry FROM CommerceNotificationQueueEntry commerceNotificationQueueEntry WHERE ";

	private static final String
		_SQL_COUNT_COMMERCENOTIFICATIONQUEUEENTRY_WHERE =
			"SELECT COUNT(commerceNotificationQueueEntry) FROM CommerceNotificationQueueEntry commerceNotificationQueueEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceNotificationQueueEntry exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceNotificationQueueEntryId", "from", "to"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-703691684