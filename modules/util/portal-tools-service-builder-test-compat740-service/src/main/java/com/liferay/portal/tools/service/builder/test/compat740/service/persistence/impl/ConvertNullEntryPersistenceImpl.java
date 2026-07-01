/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchConvertNullEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.ConvertNullEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.ConvertNullEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ConvertNullEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ConvertNullEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ConvertNullEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ConvertNullEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the convert null entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ConvertNullEntryPersistence.class)
public class ConvertNullEntryPersistenceImpl
	extends BasePersistenceImpl
		<ConvertNullEntry, NoSuchConvertNullEntryException>
	implements ConvertNullEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ConvertNullEntryUtil</code> to access the convert null entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ConvertNullEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<ConvertNullEntry, NoSuchConvertNullEntryException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the convert null entry where name = &#63; or throws a <code>NoSuchConvertNullEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching convert null entry
	 * @throws NoSuchConvertNullEntryException if a matching convert null entry could not be found
	 */
	@Override
	public ConvertNullEntry findByName(String name)
		throws NoSuchConvertNullEntryException {

		return _uniquePersistenceFinderByName.find(
			dummyFinderCache, new Object[] {name});
	}

	/**
	 * Returns the convert null entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching convert null entry, or <code>null</code> if a matching convert null entry could not be found
	 */
	@Override
	public ConvertNullEntry fetchByName(String name, boolean useFinderCache) {
		return _uniquePersistenceFinderByName.fetch(
			dummyFinderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the convert null entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the convert null entry that was removed
	 */
	@Override
	public ConvertNullEntry removeByName(String name)
		throws NoSuchConvertNullEntryException {

		ConvertNullEntry convertNullEntry = findByName(name);

		return remove(convertNullEntry);
	}

	/**
	 * Returns the number of convert null entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching convert null entries
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			dummyFinderCache, new Object[] {name});
	}

	public ConvertNullEntryPersistenceImpl() {
		setModelClass(ConvertNullEntry.class);

		setModelImplClass(ConvertNullEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ConvertNullEntryTable.INSTANCE);
	}

	/**
	 * Creates a new convert null entry with the primary key. Does not add the convert null entry to the database.
	 *
	 * @param convertNullEntryId the primary key for the new convert null entry
	 * @return the new convert null entry
	 */
	@Override
	public ConvertNullEntry create(long convertNullEntryId) {
		ConvertNullEntry convertNullEntry = new ConvertNullEntryImpl();

		convertNullEntry.setNew(true);
		convertNullEntry.setPrimaryKey(convertNullEntryId);

		return convertNullEntry;
	}

	/**
	 * Removes the convert null entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param convertNullEntryId the primary key of the convert null entry
	 * @return the convert null entry that was removed
	 * @throws NoSuchConvertNullEntryException if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry remove(long convertNullEntryId)
		throws NoSuchConvertNullEntryException {

		return remove((Serializable)convertNullEntryId);
	}

	@Override
	protected ConvertNullEntry removeImpl(ConvertNullEntry convertNullEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(convertNullEntry)) {
				convertNullEntry = (ConvertNullEntry)session.get(
					ConvertNullEntryImpl.class,
					convertNullEntry.getPrimaryKeyObj());
			}

			if (convertNullEntry != null) {
				session.delete(convertNullEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (convertNullEntry != null) {
			clearCache(convertNullEntry);
		}

		return convertNullEntry;
	}

	@Override
	public ConvertNullEntry updateImpl(ConvertNullEntry convertNullEntry) {
		boolean isNew = convertNullEntry.isNew();

		if (!(convertNullEntry instanceof ConvertNullEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(convertNullEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					convertNullEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in convertNullEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ConvertNullEntry implementation " +
					convertNullEntry.getClass());
		}

		ConvertNullEntryModelImpl convertNullEntryModelImpl =
			(ConvertNullEntryModelImpl)convertNullEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(convertNullEntry);
			}
			else {
				convertNullEntry = (ConvertNullEntry)session.merge(
					convertNullEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(convertNullEntry, false);

		if (isNew) {
			convertNullEntry.setNew(false);
		}

		convertNullEntry.resetOriginalValues();

		return convertNullEntry;
	}

	/**
	 * Returns the convert null entry with the primary key or throws a <code>NoSuchConvertNullEntryException</code> if it could not be found.
	 *
	 * @param convertNullEntryId the primary key of the convert null entry
	 * @return the convert null entry
	 * @throws NoSuchConvertNullEntryException if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry findByPrimaryKey(long convertNullEntryId)
		throws NoSuchConvertNullEntryException {

		return findByPrimaryKey((Serializable)convertNullEntryId);
	}

	/**
	 * Returns the convert null entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param convertNullEntryId the primary key of the convert null entry
	 * @return the convert null entry, or <code>null</code> if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry fetchByPrimaryKey(long convertNullEntryId) {
		return fetchByPrimaryKey((Serializable)convertNullEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "convertNullEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CONVERTNULLENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ConvertNullEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the convert null entry persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(ConvertNullEntry::getName)),
			_SQL_SELECT_CONVERTNULLENTRY_WHERE, "",
			new FinderColumn<>(
				"convertNullEntry.", "name", FinderColumn.Type.STRING, "=",
				true, true, ConvertNullEntry::getName));

		ConvertNullEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ConvertNullEntryUtil.setPersistence(null);

		dummyEntityCache.removeCache(ConvertNullEntryImpl.class.getName());
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

	private static final String _SQL_SELECT_CONVERTNULLENTRY =
		"SELECT convertNullEntry FROM ConvertNullEntry convertNullEntry";

	private static final String _SQL_SELECT_CONVERTNULLENTRY_WHERE =
		"SELECT convertNullEntry FROM ConvertNullEntry convertNullEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:171278252