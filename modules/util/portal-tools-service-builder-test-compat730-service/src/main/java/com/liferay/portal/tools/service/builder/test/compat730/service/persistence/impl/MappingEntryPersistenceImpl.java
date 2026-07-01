/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat730.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchMappingEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.MappingEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.MappingEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.MappingEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.BasicEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.MappingEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.MappingEntryUtil;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * The persistence implementation for the mapping entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class MappingEntryPersistenceImpl
	extends BasePersistenceImpl<MappingEntry>
	implements MappingEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MappingEntryUtil</code> to access the mapping entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MappingEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public MappingEntryPersistenceImpl() {
		setModelClass(MappingEntry.class);

		setModelImplClass(MappingEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the mapping entry in the entity cache if it is enabled.
	 *
	 * @param mappingEntry the mapping entry
	 */
	@Override
	public void cacheResult(MappingEntry mappingEntry) {
		entityCache.putResult(
			MappingEntryImpl.class, mappingEntry.getPrimaryKey(), mappingEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the mapping entries in the entity cache if it is enabled.
	 *
	 * @param mappingEntries the mapping entries
	 */
	@Override
	public void cacheResult(List<MappingEntry> mappingEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (mappingEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (MappingEntry mappingEntry : mappingEntries) {
			if (entityCache.getResult(
					MappingEntryImpl.class, mappingEntry.getPrimaryKey()) ==
						null) {

				cacheResult(mappingEntry);
			}
		}
	}

	/**
	 * Clears the cache for all mapping entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(MappingEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the mapping entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(MappingEntry mappingEntry) {
		entityCache.removeResult(MappingEntryImpl.class, mappingEntry);
	}

	@Override
	public void clearCache(List<MappingEntry> mappingEntries) {
		for (MappingEntry mappingEntry : mappingEntries) {
			entityCache.removeResult(MappingEntryImpl.class, mappingEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(MappingEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new mapping entry with the primary key. Does not add the mapping entry to the database.
	 *
	 * @param mappingEntryId the primary key for the new mapping entry
	 * @return the new mapping entry
	 */
	@Override
	public MappingEntry create(long mappingEntryId) {
		MappingEntry mappingEntry = new MappingEntryImpl();

		mappingEntry.setNew(true);
		mappingEntry.setPrimaryKey(mappingEntryId);

		mappingEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mappingEntry;
	}

	/**
	 * Removes the mapping entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mappingEntryId the primary key of the mapping entry
	 * @return the mapping entry that was removed
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry remove(long mappingEntryId)
		throws NoSuchMappingEntryException {

		return remove((Serializable)mappingEntryId);
	}

	/**
	 * Removes the mapping entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the mapping entry
	 * @return the mapping entry that was removed
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry remove(Serializable primaryKey)
		throws NoSuchMappingEntryException {

		Session session = null;

		try {
			session = openSession();

			MappingEntry mappingEntry = (MappingEntry)session.get(
				MappingEntryImpl.class, primaryKey);

			if (mappingEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchMappingEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(mappingEntry);
		}
		catch (NoSuchMappingEntryException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected MappingEntry removeImpl(MappingEntry mappingEntry) {
		mappingEntryToBasicEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			mappingEntry.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mappingEntry)) {
				mappingEntry = (MappingEntry)session.get(
					MappingEntryImpl.class, mappingEntry.getPrimaryKeyObj());
			}

			if (mappingEntry != null) {
				session.delete(mappingEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mappingEntry != null) {
			clearCache(mappingEntry);
		}

		return mappingEntry;
	}

	@Override
	public MappingEntry updateImpl(MappingEntry mappingEntry) {
		boolean isNew = mappingEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(mappingEntry);
			}
			else {
				mappingEntry = (MappingEntry)session.merge(mappingEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			MappingEntryImpl.class, mappingEntry, false, true);

		if (isNew) {
			mappingEntry.setNew(false);
		}

		mappingEntry.resetOriginalValues();

		return mappingEntry;
	}

	/**
	 * Returns the mapping entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the mapping entry
	 * @return the mapping entry
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchMappingEntryException {

		MappingEntry mappingEntry = fetchByPrimaryKey(primaryKey);

		if (mappingEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchMappingEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return mappingEntry;
	}

	/**
	 * Returns the mapping entry with the primary key or throws a <code>NoSuchMappingEntryException</code> if it could not be found.
	 *
	 * @param mappingEntryId the primary key of the mapping entry
	 * @return the mapping entry
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry findByPrimaryKey(long mappingEntryId)
		throws NoSuchMappingEntryException {

		return findByPrimaryKey((Serializable)mappingEntryId);
	}

	/**
	 * Returns the mapping entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mappingEntryId the primary key of the mapping entry
	 * @return the mapping entry, or <code>null</code> if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry fetchByPrimaryKey(long mappingEntryId) {
		return fetchByPrimaryKey((Serializable)mappingEntryId);
	}

	/**
	 * Returns all the mapping entries.
	 *
	 * @return the mapping entries
	 */
	@Override
	public List<MappingEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the mapping entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @return the range of mapping entries
	 */
	@Override
	public List<MappingEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the mapping entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of mapping entries
	 */
	@Override
	public List<MappingEntry> findAll(
		int start, int end, OrderByComparator<MappingEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the mapping entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of mapping entries
	 */
	@Override
	public List<MappingEntry> findAll(
		int start, int end, OrderByComparator<MappingEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<MappingEntry> list = null;

		if (useFinderCache) {
			list = (List<MappingEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_MAPPINGENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_MAPPINGENTRY;

				sql = sql.concat(MappingEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<MappingEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the mapping entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (MappingEntry mappingEntry : findAll()) {
			remove(mappingEntry);
		}
	}

	/**
	 * Returns the number of mapping entries.
	 *
	 * @return the number of mapping entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					"SELECT COUNT(mappingEntry) FROM MappingEntry mappingEntry");

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the primaryKeys of basic entries associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @return long[] of the primaryKeys of basic entries associated with the mapping entry
	 */
	@Override
	public long[] getBasicEntryPrimaryKeys(long pk) {
		long[] pks = mappingEntryToBasicEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the basic entries associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @return the basic entries associated with the mapping entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			BasicEntry> getBasicEntries(long pk) {

		return getBasicEntries(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the basic entries associated with the mapping entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the mapping entry
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @return the range of basic entries associated with the mapping entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			BasicEntry> getBasicEntries(long pk, int start, int end) {

		return getBasicEntries(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the basic entries associated with the mapping entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the mapping entry
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of basic entries associated with the mapping entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			BasicEntry> getBasicEntries(
				long pk, int start, int end,
				OrderByComparator
					<com.liferay.portal.tools.service.builder.test.compat730.
						model.BasicEntry> orderByComparator) {

		return mappingEntryToBasicEntryTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of basic entries associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @return the number of basic entries associated with the mapping entry
	 */
	@Override
	public int getBasicEntriesSize(long pk) {
		long[] pks = mappingEntryToBasicEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the basic entry is associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPK the primary key of the basic entry
	 * @return <code>true</code> if the basic entry is associated with the mapping entry; <code>false</code> otherwise
	 */
	@Override
	public boolean containsBasicEntry(long pk, long basicEntryPK) {
		return mappingEntryToBasicEntryTableMapper.containsTableMapping(
			pk, basicEntryPK);
	}

	/**
	 * Returns <code>true</code> if the mapping entry has any basic entries associated with it.
	 *
	 * @param pk the primary key of the mapping entry to check for associations with basic entries
	 * @return <code>true</code> if the mapping entry has any basic entries associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsBasicEntries(long pk) {
		if (getBasicEntriesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPK the primary key of the basic entry
	 */
	@Override
	public void addBasicEntry(long pk, long basicEntryPK) {
		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, basicEntryPK);
		}
		else {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				mappingEntry.getCompanyId(), pk, basicEntryPK);
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntry the basic entry
	 */
	@Override
	public void addBasicEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.compat730.model.BasicEntry
			basicEntry) {

		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				basicEntry.getPrimaryKey());
		}
		else {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				mappingEntry.getCompanyId(), pk, basicEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPKs the primary keys of the basic entries
	 */
	@Override
	public void addBasicEntries(long pk, long[] basicEntryPKs) {
		long companyId = 0;

		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = mappingEntry.getCompanyId();
		}

		mappingEntryToBasicEntryTableMapper.addTableMappings(
			companyId, pk, basicEntryPKs);
	}

	/**
	 * Adds an association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntries the basic entries
	 */
	@Override
	public void addBasicEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat730.model.
				BasicEntry> basicEntries) {

		addBasicEntries(
			pk,
			ListUtil.toLongArray(
				basicEntries,
				com.liferay.portal.tools.service.builder.test.compat730.model.
					BasicEntry.BASIC_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the mapping entry and its basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry to clear the associated basic entries from
	 */
	@Override
	public void clearBasicEntries(long pk) {
		mappingEntryToBasicEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPK the primary key of the basic entry
	 */
	@Override
	public void removeBasicEntry(long pk, long basicEntryPK) {
		mappingEntryToBasicEntryTableMapper.deleteTableMapping(
			pk, basicEntryPK);
	}

	/**
	 * Removes the association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntry the basic entry
	 */
	@Override
	public void removeBasicEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.compat730.model.BasicEntry
			basicEntry) {

		mappingEntryToBasicEntryTableMapper.deleteTableMapping(
			pk, basicEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPKs the primary keys of the basic entries
	 */
	@Override
	public void removeBasicEntries(long pk, long[] basicEntryPKs) {
		mappingEntryToBasicEntryTableMapper.deleteTableMappings(
			pk, basicEntryPKs);
	}

	/**
	 * Removes the association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntries the basic entries
	 */
	@Override
	public void removeBasicEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat730.model.
				BasicEntry> basicEntries) {

		removeBasicEntries(
			pk,
			ListUtil.toLongArray(
				basicEntries,
				com.liferay.portal.tools.service.builder.test.compat730.model.
					BasicEntry.BASIC_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Sets the basic entries associated with the mapping entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPKs the primary keys of the basic entries to be associated with the mapping entry
	 */
	@Override
	public void setBasicEntries(long pk, long[] basicEntryPKs) {
		Set<Long> newBasicEntryPKsSet = SetUtil.fromArray(basicEntryPKs);
		Set<Long> oldBasicEntryPKsSet = SetUtil.fromArray(
			mappingEntryToBasicEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeBasicEntryPKsSet = new HashSet<Long>(
			oldBasicEntryPKsSet);

		removeBasicEntryPKsSet.removeAll(newBasicEntryPKsSet);

		mappingEntryToBasicEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeBasicEntryPKsSet));

		newBasicEntryPKsSet.removeAll(oldBasicEntryPKsSet);

		long companyId = 0;

		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = mappingEntry.getCompanyId();
		}

		mappingEntryToBasicEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newBasicEntryPKsSet));
	}

	/**
	 * Sets the basic entries associated with the mapping entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntries the basic entries to be associated with the mapping entry
	 */
	@Override
	public void setBasicEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat730.model.
				BasicEntry> basicEntries) {

		try {
			long[] basicEntryPKs = new long[basicEntries.size()];

			for (int i = 0; i < basicEntries.size(); i++) {
				com.liferay.portal.tools.service.builder.test.compat730.model.
					BasicEntry basicEntry = basicEntries.get(i);

				basicEntryPKs[i] = basicEntry.getPrimaryKey();
			}

			setBasicEntries(pk, basicEntryPKs);
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
		return "mappingEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MAPPINGENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MappingEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the mapping entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			MappingEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class, new MappingEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", MappingEntry.class.getName()));

		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		mappingEntryToBasicEntryTableMapper = TableMapperFactory.getTableMapper(
			"MappingEntries_BasicEntries", "companyId", "mappingEntryId",
			"basicEntryId", this, basicEntryPersistence);

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		MappingEntryUtil.setPersistence(this);
	}

	public void destroy() {
		MappingEntryUtil.setPersistence(null);

		entityCache.removeCache(MappingEntryImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		TableMapperFactory.removeTableMapper("MappingEntries_BasicEntries");
	}

	private BundleContext _bundleContext;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	@BeanReference(type = BasicEntryPersistence.class)
	protected BasicEntryPersistence basicEntryPersistence;

	protected TableMapper
		<MappingEntry,
		 com.liferay.portal.tools.service.builder.test.compat730.model.
			 BasicEntry> mappingEntryToBasicEntryTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		MappingEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MAPPINGENTRY =
		"SELECT mappingEntry FROM MappingEntry mappingEntry";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No MappingEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		MappingEntryPersistenceImpl.class);

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					MapUtil.singletonDictionary("cache.name", cacheName)));
		}

		return finderPath;
	}

	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();
	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;

	private static class MappingEntryModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return new Object[0];
				}

				return null;
			}

			MappingEntryModelImpl mappingEntryModelImpl =
				(MappingEntryModelImpl)baseModel;

			long columnBitmask = mappingEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(mappingEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						mappingEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(mappingEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			MappingEntryModelImpl mappingEntryModelImpl, String[] columnNames,
			boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] = mappingEntryModelImpl.getColumnOriginalValue(
						columnName);
				}
				else {
					arguments[i] = mappingEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2098928858