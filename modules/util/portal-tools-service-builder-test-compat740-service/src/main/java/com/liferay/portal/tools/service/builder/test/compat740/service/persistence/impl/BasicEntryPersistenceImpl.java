/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

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
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchBasicEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.BasicEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.BasicEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.MappingEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.BasicEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.BasicEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.BasicEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.BasicEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the basic entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = BasicEntryPersistence.class)
public class BasicEntryPersistenceImpl
	extends BasePersistenceImpl<BasicEntry, NoSuchBasicEntryException>
	implements BasicEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BasicEntryUtil</code> to access the basic entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BasicEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<BasicEntry, NoSuchBasicEntryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the basic entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching basic entries
	 */
	@Override
	public List<BasicEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BasicEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first basic entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching basic entry
	 * @throws NoSuchBasicEntryException if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry findByGroupId_First(
			long groupId, OrderByComparator<BasicEntry> orderByComparator)
		throws NoSuchBasicEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first basic entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching basic entry, or <code>null</code> if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry fetchByGroupId_First(
		long groupId, OrderByComparator<BasicEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the basic entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of basic entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching basic entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private UniquePersistenceFinder<BasicEntry, NoSuchBasicEntryException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the basic entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchBasicEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching basic entry
	 * @throws NoSuchBasicEntryException if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry findByC_N(long companyId, String name)
		throws NoSuchBasicEntryException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the basic entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching basic entry, or <code>null</code> if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the basic entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the basic entry that was removed
	 */
	@Override
	public BasicEntry removeByC_N(long companyId, String name)
		throws NoSuchBasicEntryException {

		BasicEntry basicEntry = findByC_N(companyId, name);

		return remove(basicEntry);
	}

	/**
	 * Returns the number of basic entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching basic entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	public BasicEntryPersistenceImpl() {
		setModelClass(BasicEntry.class);

		setModelImplClass(BasicEntryImpl.class);
		setModelPKClass(long.class);

		setTable(BasicEntryTable.INSTANCE);
	}

	/**
	 * Creates a new basic entry with the primary key. Does not add the basic entry to the database.
	 *
	 * @param basicEntryId the primary key for the new basic entry
	 * @return the new basic entry
	 */
	@Override
	public BasicEntry create(long basicEntryId) {
		BasicEntry basicEntry = new BasicEntryImpl();

		basicEntry.setNew(true);
		basicEntry.setPrimaryKey(basicEntryId);

		basicEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return basicEntry;
	}

	/**
	 * Removes the basic entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param basicEntryId the primary key of the basic entry
	 * @return the basic entry that was removed
	 * @throws NoSuchBasicEntryException if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry remove(long basicEntryId)
		throws NoSuchBasicEntryException {

		return remove((Serializable)basicEntryId);
	}

	@Override
	protected BasicEntry removeImpl(BasicEntry basicEntry) {
		basicEntryToMappingEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			basicEntry.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(basicEntry)) {
				basicEntry = (BasicEntry)session.get(
					BasicEntryImpl.class, basicEntry.getPrimaryKeyObj());
			}

			if (basicEntry != null) {
				session.delete(basicEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (basicEntry != null) {
			clearCache(basicEntry);
		}

		return basicEntry;
	}

	@Override
	public BasicEntry updateImpl(BasicEntry basicEntry) {
		boolean isNew = basicEntry.isNew();

		if (!(basicEntry instanceof BasicEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(basicEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(basicEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in basicEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BasicEntry implementation " +
					basicEntry.getClass());
		}

		BasicEntryModelImpl basicEntryModelImpl =
			(BasicEntryModelImpl)basicEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (basicEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				basicEntry.setCreateDate(date);
			}
			else {
				basicEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!basicEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				basicEntry.setModifiedDate(date);
			}
			else {
				basicEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(basicEntry);
			}
			else {
				basicEntry = (BasicEntry)session.merge(basicEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(basicEntry, false);

		if (isNew) {
			basicEntry.setNew(false);
		}

		basicEntry.resetOriginalValues();

		return basicEntry;
	}

	/**
	 * Returns the basic entry with the primary key or throws a <code>NoSuchBasicEntryException</code> if it could not be found.
	 *
	 * @param basicEntryId the primary key of the basic entry
	 * @return the basic entry
	 * @throws NoSuchBasicEntryException if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry findByPrimaryKey(long basicEntryId)
		throws NoSuchBasicEntryException {

		return findByPrimaryKey((Serializable)basicEntryId);
	}

	/**
	 * Returns the basic entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param basicEntryId the primary key of the basic entry
	 * @return the basic entry, or <code>null</code> if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry fetchByPrimaryKey(long basicEntryId) {
		return fetchByPrimaryKey((Serializable)basicEntryId);
	}

	/**
	 * Returns the primaryKeys of mapping entries associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @return long[] of the primaryKeys of mapping entries associated with the basic entry
	 */
	@Override
	public long[] getMappingEntryPrimaryKeys(long pk) {
		long[] pks = basicEntryToMappingEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the basic entry associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @return the basic entries associated with the mapping entry
	 */
	@Override
	public List<BasicEntry> getMappingEntryBasicEntries(long pk) {
		return getMappingEntryBasicEntries(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the basic entry associated with the mapping entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the mapping entry
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @return the range of basic entries associated with the mapping entry
	 */
	@Override
	public List<BasicEntry> getMappingEntryBasicEntries(
		long pk, int start, int end) {

		return getMappingEntryBasicEntries(pk, start, end, null);
	}

	/**
	 * Returns all the basic entry associated with the mapping entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the mapping entry
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of basic entries associated with the mapping entry
	 */
	@Override
	public List<BasicEntry> getMappingEntryBasicEntries(
		long pk, int start, int end,
		OrderByComparator<BasicEntry> orderByComparator) {

		return basicEntryToMappingEntryTableMapper.getLeftBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of mapping entries associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @return the number of mapping entries associated with the basic entry
	 */
	@Override
	public int getMappingEntriesSize(long pk) {
		long[] pks = basicEntryToMappingEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the mapping entry is associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPK the primary key of the mapping entry
	 * @return <code>true</code> if the mapping entry is associated with the basic entry; <code>false</code> otherwise
	 */
	@Override
	public boolean containsMappingEntry(long pk, long mappingEntryPK) {
		return basicEntryToMappingEntryTableMapper.containsTableMapping(
			pk, mappingEntryPK);
	}

	/**
	 * Returns <code>true</code> if the basic entry has any mapping entries associated with it.
	 *
	 * @param pk the primary key of the basic entry to check for associations with mapping entries
	 * @return <code>true</code> if the basic entry has any mapping entries associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsMappingEntries(long pk) {
		if (getMappingEntriesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPK the primary key of the mapping entry
	 * @return <code>true</code> if an association between the basic entry and the mapping entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addMappingEntry(long pk, long mappingEntryPK) {
		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			return basicEntryToMappingEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, mappingEntryPK);
		}
		else {
			return basicEntryToMappingEntryTableMapper.addTableMapping(
				basicEntry.getCompanyId(), pk, mappingEntryPK);
		}
	}

	/**
	 * Adds an association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntry the mapping entry
	 * @return <code>true</code> if an association between the basic entry and the mapping entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addMappingEntry(long pk, MappingEntry mappingEntry) {
		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			return basicEntryToMappingEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				mappingEntry.getPrimaryKey());
		}
		else {
			return basicEntryToMappingEntryTableMapper.addTableMapping(
				basicEntry.getCompanyId(), pk, mappingEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPKs the primary keys of the mapping entries
	 * @return <code>true</code> if at least one association between the basic entry and the mapping entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addMappingEntries(long pk, long[] mappingEntryPKs) {
		long companyId = 0;

		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = basicEntry.getCompanyId();
		}

		long[] addedKeys = basicEntryToMappingEntryTableMapper.addTableMappings(
			companyId, pk, mappingEntryPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntries the mapping entries
	 * @return <code>true</code> if at least one association between the basic entry and the mapping entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addMappingEntries(
		long pk, List<MappingEntry> mappingEntries) {

		return addMappingEntries(
			pk,
			ListUtil.toLongArray(
				mappingEntries, MappingEntry.MAPPING_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the basic entry and its mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry to clear the associated mapping entries from
	 */
	@Override
	public void clearMappingEntries(long pk) {
		basicEntryToMappingEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPK the primary key of the mapping entry
	 */
	@Override
	public void removeMappingEntry(long pk, long mappingEntryPK) {
		basicEntryToMappingEntryTableMapper.deleteTableMapping(
			pk, mappingEntryPK);
	}

	/**
	 * Removes the association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntry the mapping entry
	 */
	@Override
	public void removeMappingEntry(long pk, MappingEntry mappingEntry) {
		basicEntryToMappingEntryTableMapper.deleteTableMapping(
			pk, mappingEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPKs the primary keys of the mapping entries
	 */
	@Override
	public void removeMappingEntries(long pk, long[] mappingEntryPKs) {
		basicEntryToMappingEntryTableMapper.deleteTableMappings(
			pk, mappingEntryPKs);
	}

	/**
	 * Removes the association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntries the mapping entries
	 */
	@Override
	public void removeMappingEntries(
		long pk, List<MappingEntry> mappingEntries) {

		removeMappingEntries(
			pk,
			ListUtil.toLongArray(
				mappingEntries, MappingEntry.MAPPING_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Sets the mapping entries associated with the basic entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPKs the primary keys of the mapping entries to be associated with the basic entry
	 */
	@Override
	public void setMappingEntries(long pk, long[] mappingEntryPKs) {
		Set<Long> newMappingEntryPKsSet = SetUtil.fromArray(mappingEntryPKs);
		Set<Long> oldMappingEntryPKsSet = SetUtil.fromArray(
			basicEntryToMappingEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeMappingEntryPKsSet = new HashSet<Long>(
			oldMappingEntryPKsSet);

		removeMappingEntryPKsSet.removeAll(newMappingEntryPKsSet);

		basicEntryToMappingEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeMappingEntryPKsSet));

		newMappingEntryPKsSet.removeAll(oldMappingEntryPKsSet);

		long companyId = 0;

		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = basicEntry.getCompanyId();
		}

		basicEntryToMappingEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newMappingEntryPKsSet));
	}

	/**
	 * Sets the mapping entries associated with the basic entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntries the mapping entries to be associated with the basic entry
	 */
	@Override
	public void setMappingEntries(long pk, List<MappingEntry> mappingEntries) {
		try {
			long[] mappingEntryPKs = new long[mappingEntries.size()];

			for (int i = 0; i < mappingEntries.size(); i++) {
				MappingEntry mappingEntry = mappingEntries.get(i);

				mappingEntryPKs[i] = mappingEntry.getPrimaryKey();
			}

			setMappingEntries(pk, mappingEntryPKs);
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
		return "basicEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BASICENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BasicEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the basic entry persistence.
	 */
	@Activate
	public void activate() {
		basicEntryToMappingEntryTableMapper = TableMapperFactory.getTableMapper(
			"MappingEntries_BasicEntries#basicEntryId",
			"MappingEntries_BasicEntries", "companyId", "basicEntryId",
			"mappingEntryId", this, MappingEntry.class);

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
				_SQL_SELECT_BASICENTRY_WHERE, _SQL_COUNT_BASICENTRY_WHERE,
				BasicEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"basicEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, BasicEntry::getGroupId));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				BasicEntry::getCompanyId,
				convertNullFunction(BasicEntry::getName)),
			_SQL_SELECT_BASICENTRY_WHERE, "",
			new FinderColumn<>(
				"basicEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, BasicEntry::getCompanyId),
			new FinderColumn<>(
				"basicEntry.", "name", FinderColumn.Type.STRING, "=", true,
				true, BasicEntry::getName));

		BasicEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BasicEntryUtil.setPersistence(null);

		entityCache.removeCache(BasicEntryImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"MappingEntries_BasicEntries#basicEntryId");
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	protected TableMapper<BasicEntry, MappingEntry>
		basicEntryToMappingEntryTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		BasicEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BASICENTRY =
		"SELECT basicEntry FROM BasicEntry basicEntry";

	private static final String _SQL_SELECT_BASICENTRY_WHERE =
		"SELECT basicEntry FROM BasicEntry basicEntry WHERE ";

	private static final String _SQL_COUNT_BASICENTRY_WHERE =
		"SELECT COUNT(basicEntry) FROM BasicEntry basicEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1327642974