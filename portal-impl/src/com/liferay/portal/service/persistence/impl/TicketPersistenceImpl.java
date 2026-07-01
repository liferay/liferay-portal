/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.TicketPersistence;
import com.liferay.portal.kernel.service.persistence.TicketUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.TicketImpl;
import com.liferay.portal.model.impl.TicketModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the ticket service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class TicketPersistenceImpl
	extends BasePersistenceImpl<Ticket, NoSuchTicketException>
	implements TicketPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TicketUtil</code> to access the ticket persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TicketImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<Ticket, NoSuchTicketException>
		_uniquePersistenceFinderByKey;

	/**
	 * Returns the ticket where key = &#63; or throws a <code>NoSuchTicketException</code> if it could not be found.
	 *
	 * @param key the key
	 * @return the matching ticket
	 * @throws NoSuchTicketException if a matching ticket could not be found
	 */
	@Override
	public Ticket findByKey(String key) throws NoSuchTicketException {
		return _uniquePersistenceFinderByKey.find(
			FinderCacheUtil.getFinderCache(), new Object[] {key});
	}

	/**
	 * Returns the ticket where key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ticket, or <code>null</code> if a matching ticket could not be found
	 */
	@Override
	public Ticket fetchByKey(String key, boolean useFinderCache) {
		return _uniquePersistenceFinderByKey.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {key},
			useFinderCache);
	}

	/**
	 * Removes the ticket where key = &#63; from the database.
	 *
	 * @param key the key
	 * @return the ticket that was removed
	 */
	@Override
	public Ticket removeByKey(String key) throws NoSuchTicketException {
		Ticket ticket = findByKey(key);

		return remove(ticket);
	}

	/**
	 * Returns the number of tickets where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching tickets
	 */
	@Override
	public int countByKey(String key) {
		return _uniquePersistenceFinderByKey.count(
			FinderCacheUtil.getFinderCache(), new Object[] {key});
	}

	private CollectionPersistenceFinder<Ticket, NoSuchTicketException>
		_collectionPersistenceFinderByC_C_C;

	/**
	 * Returns an ordered range of all the tickets where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TicketModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of tickets
	 * @param end the upper bound of the range of tickets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching tickets
	 */
	@Override
	public List<Ticket> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<Ticket> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ticket in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket
	 * @throws NoSuchTicketException if a matching ticket could not be found
	 */
	@Override
	public Ticket findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<Ticket> orderByComparator)
		throws NoSuchTicketException {

		return _collectionPersistenceFinderByC_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first ticket in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket, or <code>null</code> if a matching ticket could not be found
	 */
	@Override
	public Ticket fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<Ticket> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the tickets where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of tickets where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching tickets
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	private CollectionPersistenceFinder<Ticket, NoSuchTicketException>
		_collectionPersistenceFinderByC_T_EA;

	/**
	 * Returns an ordered range of all the tickets where companyId = &#63; and type = &#63; and emailAddress = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TicketModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param emailAddress the email address
	 * @param start the lower bound of the range of tickets
	 * @param end the upper bound of the range of tickets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching tickets
	 */
	@Override
	public List<Ticket> findByC_T_EA(
		long companyId, int type, String emailAddress, int start, int end,
		OrderByComparator<Ticket> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T_EA.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, type, emailAddress}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ticket in the ordered set where companyId = &#63; and type = &#63; and emailAddress = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param emailAddress the email address
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket
	 * @throws NoSuchTicketException if a matching ticket could not be found
	 */
	@Override
	public Ticket findByC_T_EA_First(
			long companyId, int type, String emailAddress,
			OrderByComparator<Ticket> orderByComparator)
		throws NoSuchTicketException {

		return _collectionPersistenceFinderByC_T_EA.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, type, emailAddress}, orderByComparator);
	}

	/**
	 * Returns the first ticket in the ordered set where companyId = &#63; and type = &#63; and emailAddress = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param emailAddress the email address
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket, or <code>null</code> if a matching ticket could not be found
	 */
	@Override
	public Ticket fetchByC_T_EA_First(
		long companyId, int type, String emailAddress,
		OrderByComparator<Ticket> orderByComparator) {

		return _collectionPersistenceFinderByC_T_EA.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, type, emailAddress}, orderByComparator);
	}

	/**
	 * Removes all the tickets where companyId = &#63; and type = &#63; and emailAddress = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param emailAddress the email address
	 */
	@Override
	public void removeByC_T_EA(long companyId, int type, String emailAddress) {
		_collectionPersistenceFinderByC_T_EA.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, type, emailAddress});
	}

	/**
	 * Returns the number of tickets where companyId = &#63; and type = &#63; and emailAddress = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param emailAddress the email address
	 * @return the number of matching tickets
	 */
	@Override
	public int countByC_T_EA(long companyId, int type, String emailAddress) {
		return _collectionPersistenceFinderByC_T_EA.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, type, emailAddress});
	}

	private CollectionPersistenceFinder<Ticket, NoSuchTicketException>
		_collectionPersistenceFinderByC_C_T;

	/**
	 * Returns an ordered range of all the tickets where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TicketModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of tickets
	 * @param end the upper bound of the range of tickets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching tickets
	 */
	@Override
	public List<Ticket> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<Ticket> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ticket in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket
	 * @throws NoSuchTicketException if a matching ticket could not be found
	 */
	@Override
	public Ticket findByC_C_T_First(
			long classNameId, long classPK, int type,
			OrderByComparator<Ticket> orderByComparator)
		throws NoSuchTicketException {

		return _collectionPersistenceFinderByC_C_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, orderByComparator);
	}

	/**
	 * Returns the first ticket in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket, or <code>null</code> if a matching ticket could not be found
	 */
	@Override
	public Ticket fetchByC_C_T_First(
		long classNameId, long classPK, int type,
		OrderByComparator<Ticket> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, orderByComparator);
	}

	/**
	 * Removes all the tickets where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_T(long classNameId, long classPK, int type) {
		_collectionPersistenceFinderByC_C_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type});
	}

	/**
	 * Returns the number of tickets where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching tickets
	 */
	@Override
	public int countByC_C_T(long classNameId, long classPK, int type) {
		return _collectionPersistenceFinderByC_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type});
	}

	private CollectionPersistenceFinder<Ticket, NoSuchTicketException>
		_collectionPersistenceFinderByC_C_C_T;

	/**
	 * Returns an ordered range of all the tickets where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TicketModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of tickets
	 * @param end the upper bound of the range of tickets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching tickets
	 */
	@Override
	public List<Ticket> findByC_C_C_T(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<Ticket> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ticket in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket
	 * @throws NoSuchTicketException if a matching ticket could not be found
	 */
	@Override
	public Ticket findByC_C_C_T_First(
			long companyId, long classNameId, long classPK, int type,
			OrderByComparator<Ticket> orderByComparator)
		throws NoSuchTicketException {

		return _collectionPersistenceFinderByC_C_C_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns the first ticket in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ticket, or <code>null</code> if a matching ticket could not be found
	 */
	@Override
	public Ticket fetchByC_C_C_T_First(
		long companyId, long classNameId, long classPK, int type,
		OrderByComparator<Ticket> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Removes all the tickets where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		_collectionPersistenceFinderByC_C_C_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, type});
	}

	/**
	 * Returns the number of tickets where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching tickets
	 */
	@Override
	public int countByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		return _collectionPersistenceFinderByC_C_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, type});
	}

	public TicketPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Ticket.class);

		setModelImplClass(TicketImpl.class);
		setModelPKClass(long.class);

		setTable(TicketTable.INSTANCE);
	}

	/**
	 * Creates a new ticket with the primary key. Does not add the ticket to the database.
	 *
	 * @param ticketId the primary key for the new ticket
	 * @return the new ticket
	 */
	@Override
	public Ticket create(long ticketId) {
		Ticket ticket = new TicketImpl();

		ticket.setNew(true);
		ticket.setPrimaryKey(ticketId);

		ticket.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ticket;
	}

	/**
	 * Removes the ticket with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ticketId the primary key of the ticket
	 * @return the ticket that was removed
	 * @throws NoSuchTicketException if a ticket with the primary key could not be found
	 */
	@Override
	public Ticket remove(long ticketId) throws NoSuchTicketException {
		return remove((Serializable)ticketId);
	}

	@Override
	protected Ticket removeImpl(Ticket ticket) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ticket)) {
				ticket = (Ticket)session.get(
					TicketImpl.class, ticket.getPrimaryKeyObj());
			}

			if (ticket != null) {
				session.delete(ticket);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ticket != null) {
			clearCache(ticket);
		}

		return ticket;
	}

	@Override
	public Ticket updateImpl(Ticket ticket) {
		boolean isNew = ticket.isNew();

		if (!(ticket instanceof TicketModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ticket.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ticket);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ticket proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Ticket implementation " +
					ticket.getClass());
		}

		TicketModelImpl ticketModelImpl = (TicketModelImpl)ticket;

		if (isNew && (ticket.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ticket.setCreateDate(date);
			}
			else {
				ticket.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ticket);
			}
			else {
				ticket = (Ticket)session.merge(ticket);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ticket, false);

		if (isNew) {
			ticket.setNew(false);
		}

		ticket.resetOriginalValues();

		return ticket;
	}

	/**
	 * Returns the ticket with the primary key or throws a <code>NoSuchTicketException</code> if it could not be found.
	 *
	 * @param ticketId the primary key of the ticket
	 * @return the ticket
	 * @throws NoSuchTicketException if a ticket with the primary key could not be found
	 */
	@Override
	public Ticket findByPrimaryKey(long ticketId) throws NoSuchTicketException {
		return findByPrimaryKey((Serializable)ticketId);
	}

	/**
	 * Returns the ticket with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ticketId the primary key of the ticket
	 * @return the ticket, or <code>null</code> if a ticket with the primary key could not be found
	 */
	@Override
	public Ticket fetchByPrimaryKey(long ticketId) {
		return fetchByPrimaryKey((Serializable)ticketId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "ticketId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TICKET;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TicketModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ticket persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByKey = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKey",
				new String[] {String.class.getName()}, new String[] {"key_"}, 0,
				1, false, convertNullFunction(Ticket::getKey)),
			_SQL_SELECT_TICKET_WHERE, "",
			new FinderColumn<>(
				"ticket.", "key", "key_", FinderColumn.Type.STRING, "=", true,
				true, Ticket::getKey));

		_collectionPersistenceFinderByC_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, false),
			_SQL_SELECT_TICKET_WHERE, _SQL_COUNT_TICKET_WHERE,
			TicketModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"ticket.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Ticket::getCompanyId),
			new FinderColumn<>(
				"ticket.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Ticket::getClassNameId),
			new FinderColumn<>(
				"ticket.", "classPK", FinderColumn.Type.LONG, "=", true, true,
				Ticket::getClassPK));

		_collectionPersistenceFinderByC_T_EA =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T_EA",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "type_", "emailAddress"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T_EA",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						String.class.getName()
					},
					new String[] {"companyId", "type_", "emailAddress"}, 0, 4,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T_EA",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						String.class.getName()
					},
					new String[] {"companyId", "type_", "emailAddress"}, 0, 4,
					false, null),
				_SQL_SELECT_TICKET_WHERE, _SQL_COUNT_TICKET_WHERE,
				TicketModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ticket.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Ticket::getCompanyId),
				new FinderColumn<>(
					"ticket.", "type", "type_", FinderColumn.Type.INTEGER, "=",
					true, true, Ticket::getType),
				new FinderColumn<>(
					"ticket.", "emailAddress", FinderColumn.Type.STRING, "=",
					true, true, Ticket::getEmailAddress));

		_collectionPersistenceFinderByC_C_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, false),
			_SQL_SELECT_TICKET_WHERE, _SQL_COUNT_TICKET_WHERE,
			TicketModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"ticket.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Ticket::getClassNameId),
			new FinderColumn<>(
				"ticket.", "classPK", FinderColumn.Type.LONG, "=", true, true,
				Ticket::getClassPK),
			new FinderColumn<>(
				"ticket.", "type", "type_", FinderColumn.Type.INTEGER, "=",
				true, true, Ticket::getType));

		_collectionPersistenceFinderByC_C_C_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					false),
				_SQL_SELECT_TICKET_WHERE, _SQL_COUNT_TICKET_WHERE,
				TicketModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ticket.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Ticket::getCompanyId),
				new FinderColumn<>(
					"ticket.", "classNameId", FinderColumn.Type.LONG, "=", true,
					true, Ticket::getClassNameId),
				new FinderColumn<>(
					"ticket.", "classPK", FinderColumn.Type.LONG, "=", true,
					true, Ticket::getClassPK),
				new FinderColumn<>(
					"ticket.", "type", "type_", FinderColumn.Type.INTEGER, "=",
					true, true, Ticket::getType));

		TicketUtil.setPersistence(this);
	}

	public void destroy() {
		TicketUtil.setPersistence(null);

		EntityCacheUtil.removeCache(TicketImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		TicketModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_TICKET =
		"SELECT ticket FROM Ticket ticket";

	private static final String _SQL_SELECT_TICKET_WHERE =
		"SELECT ticket FROM Ticket ticket WHERE ";

	private static final String _SQL_COUNT_TICKET_WHERE =
		"SELECT COUNT(ticket) FROM Ticket ticket WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1833938805