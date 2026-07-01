/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchMappingEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.BasicEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.MappingEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.MappingEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.MappingEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.MappingEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.MappingEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.MappingEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

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
 * The persistence implementation for the mapping entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MappingEntryPersistence.class)
public class MappingEntryPersistenceImpl
	extends BasePersistenceImpl<MappingEntry, NoSuchMappingEntryException>
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

	public MappingEntryPersistenceImpl() {
		setModelClass(MappingEntry.class);

		setModelImplClass(MappingEntryImpl.class);
		setModelPKClass(long.class);

		setTable(MappingEntryTable.INSTANCE);
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

		cacheUniqueFindersResult(mappingEntry, false);

		if (isNew) {
			mappingEntry.setNew(false);
		}

		mappingEntry.resetOriginalValues();

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
	 * Returns all the mapping entry associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @return the mapping entries associated with the basic entry
	 */
	@Override
	public List<MappingEntry> getBasicEntryMappingEntries(long pk) {
		return getBasicEntryMappingEntries(
			pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns all the mapping entry associated with the basic entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the basic entry
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @return the range of mapping entries associated with the basic entry
	 */
	@Override
	public List<MappingEntry> getBasicEntryMappingEntries(
		long pk, int start, int end) {

		return getBasicEntryMappingEntries(pk, start, end, null);
	}

	/**
	 * Returns all the mapping entry associated with the basic entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the basic entry
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of mapping entries associated with the basic entry
	 */
	@Override
	public List<MappingEntry> getBasicEntryMappingEntries(
		long pk, int start, int end,
		OrderByComparator<MappingEntry> orderByComparator) {

		return mappingEntryToBasicEntryTableMapper.getLeftBaseModels(
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
	 * @return <code>true</code> if an association between the mapping entry and the basic entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addBasicEntry(long pk, long basicEntryPK) {
		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			return mappingEntryToBasicEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, basicEntryPK);
		}
		else {
			return mappingEntryToBasicEntryTableMapper.addTableMapping(
				mappingEntry.getCompanyId(), pk, basicEntryPK);
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntry the basic entry
	 * @return <code>true</code> if an association between the mapping entry and the basic entry was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addBasicEntry(long pk, BasicEntry basicEntry) {
		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			return mappingEntryToBasicEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				basicEntry.getPrimaryKey());
		}
		else {
			return mappingEntryToBasicEntryTableMapper.addTableMapping(
				mappingEntry.getCompanyId(), pk, basicEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPKs the primary keys of the basic entries
	 * @return <code>true</code> if at least one association between the mapping entry and the basic entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addBasicEntries(long pk, long[] basicEntryPKs) {
		long companyId = 0;

		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = mappingEntry.getCompanyId();
		}

		long[] addedKeys = mappingEntryToBasicEntryTableMapper.addTableMappings(
			companyId, pk, basicEntryPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntries the basic entries
	 * @return <code>true</code> if at least one association between the mapping entry and the basic entries was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addBasicEntries(long pk, List<BasicEntry> basicEntries) {
		return addBasicEntries(
			pk,
			ListUtil.toLongArray(
				basicEntries, BasicEntry.BASIC_ENTRY_ID_ACCESSOR));
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
	public void removeBasicEntry(long pk, BasicEntry basicEntry) {
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
	public void removeBasicEntries(long pk, List<BasicEntry> basicEntries) {
		removeBasicEntries(
			pk,
			ListUtil.toLongArray(
				basicEntries, BasicEntry.BASIC_ENTRY_ID_ACCESSOR));
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
	public void setBasicEntries(long pk, List<BasicEntry> basicEntries) {
		try {
			long[] basicEntryPKs = new long[basicEntries.size()];

			for (int i = 0; i < basicEntries.size(); i++) {
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
	@Activate
	public void activate() {
		mappingEntryToBasicEntryTableMapper = TableMapperFactory.getTableMapper(
			"MappingEntries_BasicEntries#mappingEntryId",
			"MappingEntries_BasicEntries", "companyId", "mappingEntryId",
			"basicEntryId", this, BasicEntry.class);

		MappingEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MappingEntryUtil.setPersistence(null);

		entityCache.removeCache(MappingEntryImpl.class.getName());

		TableMapperFactory.removeTableMapper(
			"MappingEntries_BasicEntries#mappingEntryId");
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

	protected TableMapper<MappingEntry, BasicEntry>
		mappingEntryToBasicEntryTableMapper;

	private static final String _SQL_SELECT_MAPPINGENTRY =
		"SELECT mappingEntry FROM MappingEntry mappingEntry";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:615921812