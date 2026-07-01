/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchBigDecimalEntryException;
import com.liferay.portal.tools.service.builder.test.model.BigDecimalEntry;
import com.liferay.portal.tools.service.builder.test.model.BigDecimalEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.BigDecimalEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.BigDecimalEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.BigDecimalEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.BigDecimalEntryUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryPersistence;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.math.BigDecimal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the big decimal entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class BigDecimalEntryPersistenceImpl
	extends BasePersistenceImpl<BigDecimalEntry, NoSuchBigDecimalEntryException>
	implements BigDecimalEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BigDecimalEntryUtil</code> to access the big decimal entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BigDecimalEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<BigDecimalEntry, NoSuchBigDecimalEntryException>
			_collectionPersistenceFinderByBigDecimalValue;

	/**
	 * Returns an ordered range of all the big decimal entries where bigDecimalValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByBigDecimalValue.find(
			finderCache, new Object[] {bigDecimalValue}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first big decimal entry in the ordered set where bigDecimalValue = &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching big decimal entry
	 * @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	 */
	@Override
	public BigDecimalEntry findByBigDecimalValue_First(
			BigDecimal bigDecimalValue,
			OrderByComparator<BigDecimalEntry> orderByComparator)
		throws NoSuchBigDecimalEntryException {

		return _collectionPersistenceFinderByBigDecimalValue.findFirst(
			finderCache, new Object[] {bigDecimalValue}, orderByComparator);
	}

	/**
	 * Returns the first big decimal entry in the ordered set where bigDecimalValue = &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	 */
	@Override
	public BigDecimalEntry fetchByBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {

		return _collectionPersistenceFinderByBigDecimalValue.fetchFirst(
			finderCache, new Object[] {bigDecimalValue}, orderByComparator);
	}

	/**
	 * Removes all the big decimal entries where bigDecimalValue = &#63; from the database.
	 *
	 * @param bigDecimalValue the big decimal value
	 */
	@Override
	public void removeByBigDecimalValue(BigDecimal bigDecimalValue) {
		_collectionPersistenceFinderByBigDecimalValue.remove(
			finderCache, new Object[] {bigDecimalValue});
	}

	/**
	 * Returns the number of big decimal entries where bigDecimalValue = &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @return the number of matching big decimal entries
	 */
	@Override
	public int countByBigDecimalValue(BigDecimal bigDecimalValue) {
		return _collectionPersistenceFinderByBigDecimalValue.count(
			finderCache, new Object[] {bigDecimalValue});
	}

	private CollectionPersistenceFinder
		<BigDecimalEntry, NoSuchBigDecimalEntryException>
			_collectionPersistenceFinderByGtBigDecimalValue;

	/**
	 * Returns all the big decimal entries where bigDecimalValue &gt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @return the matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue) {

		return findByGtBigDecimalValue(
			bigDecimalValue, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the big decimal entries where bigDecimalValue &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @return the range of matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end) {

		return findByGtBigDecimalValue(bigDecimalValue, start, end, null);
	}

	/**
	 * Returns an ordered range of all the big decimal entries where bigDecimalValue &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator) {

		return findByGtBigDecimalValue(
			bigDecimalValue, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the big decimal entries where bigDecimalValue &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByGtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGtBigDecimalValue.find(
			finderCache, new Object[] {bigDecimalValue}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first big decimal entry in the ordered set where bigDecimalValue &gt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching big decimal entry
	 * @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	 */
	@Override
	public BigDecimalEntry findByGtBigDecimalValue_First(
			BigDecimal bigDecimalValue,
			OrderByComparator<BigDecimalEntry> orderByComparator)
		throws NoSuchBigDecimalEntryException {

		return _collectionPersistenceFinderByGtBigDecimalValue.findFirst(
			finderCache, new Object[] {bigDecimalValue}, orderByComparator);
	}

	/**
	 * Returns the first big decimal entry in the ordered set where bigDecimalValue &gt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	 */
	@Override
	public BigDecimalEntry fetchByGtBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {

		return _collectionPersistenceFinderByGtBigDecimalValue.fetchFirst(
			finderCache, new Object[] {bigDecimalValue}, orderByComparator);
	}

	/**
	 * Removes all the big decimal entries where bigDecimalValue &gt; &#63; from the database.
	 *
	 * @param bigDecimalValue the big decimal value
	 */
	@Override
	public void removeByGtBigDecimalValue(BigDecimal bigDecimalValue) {
		_collectionPersistenceFinderByGtBigDecimalValue.remove(
			finderCache, new Object[] {bigDecimalValue});
	}

	/**
	 * Returns the number of big decimal entries where bigDecimalValue &gt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @return the number of matching big decimal entries
	 */
	@Override
	public int countByGtBigDecimalValue(BigDecimal bigDecimalValue) {
		return _collectionPersistenceFinderByGtBigDecimalValue.count(
			finderCache, new Object[] {bigDecimalValue});
	}

	private CollectionPersistenceFinder
		<BigDecimalEntry, NoSuchBigDecimalEntryException>
			_collectionPersistenceFinderByLtBigDecimalValue;

	/**
	 * Returns all the big decimal entries where bigDecimalValue &lt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @return the matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue) {

		return findByLtBigDecimalValue(
			bigDecimalValue, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the big decimal entries where bigDecimalValue &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @return the range of matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end) {

		return findByLtBigDecimalValue(bigDecimalValue, start, end, null);
	}

	/**
	 * Returns an ordered range of all the big decimal entries where bigDecimalValue &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator) {

		return findByLtBigDecimalValue(
			bigDecimalValue, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the big decimal entries where bigDecimalValue &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching big decimal entries
	 */
	@Override
	public List<BigDecimalEntry> findByLtBigDecimalValue(
		BigDecimal bigDecimalValue, int start, int end,
		OrderByComparator<BigDecimalEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtBigDecimalValue.find(
			finderCache, new Object[] {bigDecimalValue}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first big decimal entry in the ordered set where bigDecimalValue &lt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching big decimal entry
	 * @throws NoSuchBigDecimalEntryException if a matching big decimal entry could not be found
	 */
	@Override
	public BigDecimalEntry findByLtBigDecimalValue_First(
			BigDecimal bigDecimalValue,
			OrderByComparator<BigDecimalEntry> orderByComparator)
		throws NoSuchBigDecimalEntryException {

		return _collectionPersistenceFinderByLtBigDecimalValue.findFirst(
			finderCache, new Object[] {bigDecimalValue}, orderByComparator);
	}

	/**
	 * Returns the first big decimal entry in the ordered set where bigDecimalValue &lt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching big decimal entry, or <code>null</code> if a matching big decimal entry could not be found
	 */
	@Override
	public BigDecimalEntry fetchByLtBigDecimalValue_First(
		BigDecimal bigDecimalValue,
		OrderByComparator<BigDecimalEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtBigDecimalValue.fetchFirst(
			finderCache, new Object[] {bigDecimalValue}, orderByComparator);
	}

	/**
	 * Removes all the big decimal entries where bigDecimalValue &lt; &#63; from the database.
	 *
	 * @param bigDecimalValue the big decimal value
	 */
	@Override
	public void removeByLtBigDecimalValue(BigDecimal bigDecimalValue) {
		_collectionPersistenceFinderByLtBigDecimalValue.remove(
			finderCache, new Object[] {bigDecimalValue});
	}

	/**
	 * Returns the number of big decimal entries where bigDecimalValue &lt; &#63;.
	 *
	 * @param bigDecimalValue the big decimal value
	 * @return the number of matching big decimal entries
	 */
	@Override
	public int countByLtBigDecimalValue(BigDecimal bigDecimalValue) {
		return _collectionPersistenceFinderByLtBigDecimalValue.count(
			finderCache, new Object[] {bigDecimalValue});
	}

	public BigDecimalEntryPersistenceImpl() {
		setModelClass(BigDecimalEntry.class);

		setModelImplClass(BigDecimalEntryImpl.class);
		setModelPKClass(long.class);

		setTable(BigDecimalEntryTable.INSTANCE);
	}

	/**
	 * Creates a new big decimal entry with the primary key. Does not add the big decimal entry to the database.
	 *
	 * @param bigDecimalEntryId the primary key for the new big decimal entry
	 * @return the new big decimal entry
	 */
	@Override
	public BigDecimalEntry create(long bigDecimalEntryId) {
		BigDecimalEntry bigDecimalEntry = new BigDecimalEntryImpl();

		bigDecimalEntry.setNew(true);
		bigDecimalEntry.setPrimaryKey(bigDecimalEntryId);

		bigDecimalEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return bigDecimalEntry;
	}

	/**
	 * Removes the big decimal entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param bigDecimalEntryId the primary key of the big decimal entry
	 * @return the big decimal entry that was removed
	 * @throws NoSuchBigDecimalEntryException if a big decimal entry with the primary key could not be found
	 */
	@Override
	public BigDecimalEntry remove(long bigDecimalEntryId)
		throws NoSuchBigDecimalEntryException {

		return remove((Serializable)bigDecimalEntryId);
	}

	@Override
	protected BigDecimalEntry removeImpl(BigDecimalEntry bigDecimalEntry) {
		bigDecimalEntryToLVEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			bigDecimalEntry.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(bigDecimalEntry)) {
				bigDecimalEntry = (BigDecimalEntry)session.get(
					BigDecimalEntryImpl.class,
					bigDecimalEntry.getPrimaryKeyObj());
			}

			if (bigDecimalEntry != null) {
				session.delete(bigDecimalEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (bigDecimalEntry != null) {
			clearCache(bigDecimalEntry);
		}

		return bigDecimalEntry;
	}

	@Override
	public BigDecimalEntry updateImpl(BigDecimalEntry bigDecimalEntry) {
		boolean isNew = bigDecimalEntry.isNew();

		if (!(bigDecimalEntry instanceof BigDecimalEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(bigDecimalEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					bigDecimalEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in bigDecimalEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BigDecimalEntry implementation " +
					bigDecimalEntry.getClass());
		}

		BigDecimalEntryModelImpl bigDecimalEntryModelImpl =
			(BigDecimalEntryModelImpl)bigDecimalEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(bigDecimalEntry);
			}
			else {
				bigDecimalEntry = (BigDecimalEntry)session.merge(
					bigDecimalEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(bigDecimalEntry, false);

		if (isNew) {
			bigDecimalEntry.setNew(false);
		}

		bigDecimalEntry.resetOriginalValues();

		return bigDecimalEntry;
	}

	/**
	 * Returns the big decimal entry with the primary key or throws a <code>NoSuchBigDecimalEntryException</code> if it could not be found.
	 *
	 * @param bigDecimalEntryId the primary key of the big decimal entry
	 * @return the big decimal entry
	 * @throws NoSuchBigDecimalEntryException if a big decimal entry with the primary key could not be found
	 */
	@Override
	public BigDecimalEntry findByPrimaryKey(long bigDecimalEntryId)
		throws NoSuchBigDecimalEntryException {

		return findByPrimaryKey((Serializable)bigDecimalEntryId);
	}

	/**
	 * Returns the big decimal entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param bigDecimalEntryId the primary key of the big decimal entry
	 * @return the big decimal entry, or <code>null</code> if a big decimal entry with the primary key could not be found
	 */
	@Override
	public BigDecimalEntry fetchByPrimaryKey(long bigDecimalEntryId) {
		return fetchByPrimaryKey((Serializable)bigDecimalEntryId);
	}

	/**
	 * Returns the primaryKeys of lv entries associated with the big decimal entry.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @return long[] of the primaryKeys of lv entries associated with the big decimal entry
	 */
	@Override
	public long[] getLVEntryPrimaryKeys(long pk) {
		long[] pks = bigDecimalEntryToLVEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the lv entries associated with the big decimal entry.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @return the lv entries associated with the big decimal entry
	 */
	@Override
	public List<com.liferay.portal.tools.service.builder.test.model.LVEntry>
		getLVEntries(long pk) {

		return getLVEntries(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the lv entries associated with the big decimal entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @return the range of lv entries associated with the big decimal entry
	 */
	@Override
	public List<com.liferay.portal.tools.service.builder.test.model.LVEntry>
		getLVEntries(long pk, int start, int end) {

		return getLVEntries(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the lv entries associated with the big decimal entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BigDecimalEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param start the lower bound of the range of big decimal entries
	 * @param end the upper bound of the range of big decimal entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of lv entries associated with the big decimal entry
	 */
	@Override
	public List<com.liferay.portal.tools.service.builder.test.model.LVEntry>
		getLVEntries(
			long pk, int start, int end,
			OrderByComparator
				<com.liferay.portal.tools.service.builder.test.model.LVEntry>
					orderByComparator) {

		return bigDecimalEntryToLVEntryTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of lv entries associated with the big decimal entry.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @return the number of lv entries associated with the big decimal entry
	 */
	@Override
	public int getLVEntriesSize(long pk) {
		long[] pks = bigDecimalEntryToLVEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the lv entry is associated with the big decimal entry.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntryPK the primary key of the lv entry
	 * @return <code>true</code> if the lv entry is associated with the big decimal entry; <code>false</code> otherwise
	 */
	@Override
	public boolean containsLVEntry(long pk, long lvEntryPK) {
		return bigDecimalEntryToLVEntryTableMapper.containsTableMapping(
			pk, lvEntryPK);
	}

	/**
	 * Returns <code>true</code> if the big decimal entry has any lv entries associated with it.
	 *
	 * @param pk the primary key of the big decimal entry to check for associations with lv entries
	 * @return <code>true</code> if the big decimal entry has any lv entries associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsLVEntries(long pk) {
		if (getLVEntriesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the big decimal entry and the lv entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntryPK the primary key of the lv entry
	 * @return <code>true</code> if an association between the big decimal entry and the lv entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addLVEntry(long pk, long lvEntryPK) {
		BigDecimalEntry bigDecimalEntry = fetchByPrimaryKey(pk);

		if (bigDecimalEntry == null) {
			return bigDecimalEntryToLVEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, lvEntryPK);
		}
		else {
			return bigDecimalEntryToLVEntryTableMapper.addTableMapping(
				bigDecimalEntry.getCompanyId(), pk, lvEntryPK);
		}
	}

	/**
	 * Adds an association between the big decimal entry and the lv entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntry the lv entry
	 * @return <code>true</code> if an association between the big decimal entry and the lv entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addLVEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.model.LVEntry lvEntry) {

		BigDecimalEntry bigDecimalEntry = fetchByPrimaryKey(pk);

		if (bigDecimalEntry == null) {
			return bigDecimalEntryToLVEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, lvEntry.getPrimaryKey());
		}
		else {
			return bigDecimalEntryToLVEntryTableMapper.addTableMapping(
				bigDecimalEntry.getCompanyId(), pk, lvEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the big decimal entry and the lv entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntryPKs the primary keys of the lv entries
	 * @return <code>true</code> if at least one association between the big decimal entry and the lv entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addLVEntries(long pk, long[] lvEntryPKs) {
		long companyId = 0;

		BigDecimalEntry bigDecimalEntry = fetchByPrimaryKey(pk);

		if (bigDecimalEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = bigDecimalEntry.getCompanyId();
		}

		long[] addedKeys = bigDecimalEntryToLVEntryTableMapper.addTableMappings(
			companyId, pk, lvEntryPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the big decimal entry and the lv entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntries the lv entries
	 * @return <code>true</code> if at least one association between the big decimal entry and the lv entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addLVEntries(
		long pk,
		List<com.liferay.portal.tools.service.builder.test.model.LVEntry>
			lvEntries) {

		return addLVEntries(
			pk,
			ListUtil.toLongArray(
				lvEntries,
				com.liferay.portal.tools.service.builder.test.model.LVEntry.
					LV_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the big decimal entry and its lv entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry to clear the associated lv entries from
	 */
	@Override
	public void clearLVEntries(long pk) {
		bigDecimalEntryToLVEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the big decimal entry and the lv entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntryPK the primary key of the lv entry
	 */
	@Override
	public void removeLVEntry(long pk, long lvEntryPK) {
		bigDecimalEntryToLVEntryTableMapper.deleteTableMapping(pk, lvEntryPK);
	}

	/**
	 * Removes the association between the big decimal entry and the lv entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntry the lv entry
	 */
	@Override
	public void removeLVEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.model.LVEntry lvEntry) {

		bigDecimalEntryToLVEntryTableMapper.deleteTableMapping(
			pk, lvEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the big decimal entry and the lv entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntryPKs the primary keys of the lv entries
	 */
	@Override
	public void removeLVEntries(long pk, long[] lvEntryPKs) {
		bigDecimalEntryToLVEntryTableMapper.deleteTableMappings(pk, lvEntryPKs);
	}

	/**
	 * Removes the association between the big decimal entry and the lv entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntries the lv entries
	 */
	@Override
	public void removeLVEntries(
		long pk,
		List<com.liferay.portal.tools.service.builder.test.model.LVEntry>
			lvEntries) {

		removeLVEntries(
			pk,
			ListUtil.toLongArray(
				lvEntries,
				com.liferay.portal.tools.service.builder.test.model.LVEntry.
					LV_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Sets the lv entries associated with the big decimal entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntryPKs the primary keys of the lv entries to be associated with the big decimal entry
	 */
	@Override
	public void setLVEntries(long pk, long[] lvEntryPKs) {
		Set<Long> newLVEntryPKsSet = SetUtil.fromArray(lvEntryPKs);
		Set<Long> oldLVEntryPKsSet = SetUtil.fromArray(
			bigDecimalEntryToLVEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeLVEntryPKsSet = new HashSet<Long>(oldLVEntryPKsSet);

		removeLVEntryPKsSet.removeAll(newLVEntryPKsSet);

		bigDecimalEntryToLVEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeLVEntryPKsSet));

		newLVEntryPKsSet.removeAll(oldLVEntryPKsSet);

		long companyId = 0;

		BigDecimalEntry bigDecimalEntry = fetchByPrimaryKey(pk);

		if (bigDecimalEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = bigDecimalEntry.getCompanyId();
		}

		bigDecimalEntryToLVEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newLVEntryPKsSet));
	}

	/**
	 * Sets the lv entries associated with the big decimal entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the big decimal entry
	 * @param lvEntries the lv entries to be associated with the big decimal entry
	 */
	@Override
	public void setLVEntries(
		long pk,
		List<com.liferay.portal.tools.service.builder.test.model.LVEntry>
			lvEntries) {

		try {
			long[] lvEntryPKs = new long[lvEntries.size()];

			for (int i = 0; i < lvEntries.size(); i++) {
				com.liferay.portal.tools.service.builder.test.model.LVEntry
					lvEntry = lvEntries.get(i);

				lvEntryPKs[i] = lvEntry.getPrimaryKey();
			}

			setLVEntries(pk, lvEntryPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "bigDecimalEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BIGDECIMALENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BigDecimalEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the big decimal entry persistence.
	 */
	public void afterPropertiesSet() {
		bigDecimalEntryToLVEntryTableMapper = TableMapperFactory.getTableMapper(
			"BigDecimalEntries_LVEntries", "companyId", "bigDecimalEntryId",
			"lvEntryId", this, lvEntryPersistence);

		_collectionPersistenceFinderByBigDecimalValue =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByBigDecimalValue",
					new String[] {
						BigDecimal.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"bigDecimalValue"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByBigDecimalValue",
					new String[] {BigDecimal.class.getName()},
					new String[] {"bigDecimalValue"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByBigDecimalValue",
					new String[] {BigDecimal.class.getName()},
					new String[] {"bigDecimalValue"}, false),
				_SQL_SELECT_BIGDECIMALENTRY_WHERE,
				_SQL_COUNT_BIGDECIMALENTRY_WHERE,
				BigDecimalEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"bigDecimalEntry.", "bigDecimalValue",
					FinderColumn.Type.BIG_DECIMAL, "=", true, true,
					BigDecimalEntry::getBigDecimalValue));

		_collectionPersistenceFinderByGtBigDecimalValue =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByGtBigDecimalValue",
					new String[] {
						BigDecimal.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"bigDecimalValue"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByGtBigDecimalValue",
					new String[] {BigDecimal.class.getName()},
					new String[] {"bigDecimalValue"}, false),
				_SQL_SELECT_BIGDECIMALENTRY_WHERE,
				_SQL_COUNT_BIGDECIMALENTRY_WHERE,
				BigDecimalEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"bigDecimalEntry.", "bigDecimalValue",
					FinderColumn.Type.BIG_DECIMAL, ">", true, true,
					BigDecimalEntry::getBigDecimalValue));

		_collectionPersistenceFinderByLtBigDecimalValue =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtBigDecimalValue",
					new String[] {
						BigDecimal.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"bigDecimalValue"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtBigDecimalValue",
					new String[] {BigDecimal.class.getName()},
					new String[] {"bigDecimalValue"}, false),
				_SQL_SELECT_BIGDECIMALENTRY_WHERE,
				_SQL_COUNT_BIGDECIMALENTRY_WHERE,
				BigDecimalEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"bigDecimalEntry.", "bigDecimalValue",
					FinderColumn.Type.BIG_DECIMAL, "<", true, true,
					BigDecimalEntry::getBigDecimalValue));

		BigDecimalEntryUtil.setPersistence(this);
	}

	public void destroy() {
		BigDecimalEntryUtil.setPersistence(null);

		entityCache.removeCache(BigDecimalEntryImpl.class.getName());

		TableMapperFactory.removeTableMapper("BigDecimalEntries_LVEntries");
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	@BeanReference(type = LVEntryPersistence.class)
	protected LVEntryPersistence lvEntryPersistence;

	protected TableMapper
		<BigDecimalEntry,
		 com.liferay.portal.tools.service.builder.test.model.LVEntry>
			bigDecimalEntryToLVEntryTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		BigDecimalEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BIGDECIMALENTRY =
		"SELECT bigDecimalEntry FROM BigDecimalEntry bigDecimalEntry";

	private static final String _SQL_SELECT_BIGDECIMALENTRY_WHERE =
		"SELECT bigDecimalEntry FROM BigDecimalEntry bigDecimalEntry WHERE ";

	private static final String _SQL_COUNT_BIGDECIMALENTRY_WHERE =
		"SELECT COUNT(bigDecimalEntry) FROM BigDecimalEntry bigDecimalEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BigDecimalEntry exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-377309906