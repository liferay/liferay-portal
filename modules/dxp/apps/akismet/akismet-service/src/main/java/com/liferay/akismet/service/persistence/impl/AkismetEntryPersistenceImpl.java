/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.akismet.service.persistence.impl;

import com.liferay.akismet.exception.NoSuchAkismetEntryException;
import com.liferay.akismet.model.AkismetEntry;
import com.liferay.akismet.model.AkismetEntryTable;
import com.liferay.akismet.model.impl.AkismetEntryImpl;
import com.liferay.akismet.model.impl.AkismetEntryModelImpl;
import com.liferay.akismet.service.persistence.AkismetEntryPersistence;
import com.liferay.akismet.service.persistence.AkismetEntryUtil;
import com.liferay.akismet.service.persistence.impl.constants.OSBCommunityPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the akismet entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Jamie Sammons
 * @generated
 */
@Component(service = AkismetEntryPersistence.class)
public class AkismetEntryPersistenceImpl
	extends BasePersistenceImpl<AkismetEntry, NoSuchAkismetEntryException>
	implements AkismetEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AkismetEntryUtil</code> to access the akismet entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AkismetEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AkismetEntry, NoSuchAkismetEntryException>
			_collectionPersistenceFinderByLtModifiedDate;

	/**
	 * Returns all the akismet entries where modifiedDate &lt; &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @return the matching akismet entries
	 */
	@Override
	public List<AkismetEntry> findByLtModifiedDate(Date modifiedDate) {
		return findByLtModifiedDate(
			modifiedDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the akismet entries where modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AkismetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of akismet entries
	 * @param end the upper bound of the range of akismet entries (not inclusive)
	 * @return the range of matching akismet entries
	 */
	@Override
	public List<AkismetEntry> findByLtModifiedDate(
		Date modifiedDate, int start, int end) {

		return findByLtModifiedDate(modifiedDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the akismet entries where modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AkismetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of akismet entries
	 * @param end the upper bound of the range of akismet entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching akismet entries
	 */
	@Override
	public List<AkismetEntry> findByLtModifiedDate(
		Date modifiedDate, int start, int end,
		OrderByComparator<AkismetEntry> orderByComparator) {

		return findByLtModifiedDate(
			modifiedDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the akismet entries where modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AkismetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of akismet entries
	 * @param end the upper bound of the range of akismet entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching akismet entries
	 */
	@Override
	public List<AkismetEntry> findByLtModifiedDate(
		Date modifiedDate, int start, int end,
		OrderByComparator<AkismetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLtModifiedDate.find(
			finderCache, new Object[] {modifiedDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first akismet entry in the ordered set where modifiedDate &lt; &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching akismet entry
	 * @throws NoSuchAkismetEntryException if a matching akismet entry could not be found
	 */
	@Override
	public AkismetEntry findByLtModifiedDate_First(
			Date modifiedDate,
			OrderByComparator<AkismetEntry> orderByComparator)
		throws NoSuchAkismetEntryException {

		return _collectionPersistenceFinderByLtModifiedDate.findFirst(
			finderCache, new Object[] {modifiedDate}, orderByComparator);
	}

	/**
	 * Returns the first akismet entry in the ordered set where modifiedDate &lt; &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching akismet entry, or <code>null</code> if a matching akismet entry could not be found
	 */
	@Override
	public AkismetEntry fetchByLtModifiedDate_First(
		Date modifiedDate, OrderByComparator<AkismetEntry> orderByComparator) {

		return _collectionPersistenceFinderByLtModifiedDate.fetchFirst(
			finderCache, new Object[] {modifiedDate}, orderByComparator);
	}

	/**
	 * Removes all the akismet entries where modifiedDate &lt; &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 */
	@Override
	public void removeByLtModifiedDate(Date modifiedDate) {
		_collectionPersistenceFinderByLtModifiedDate.remove(
			finderCache, new Object[] {modifiedDate});
	}

	/**
	 * Returns the number of akismet entries where modifiedDate &lt; &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @return the number of matching akismet entries
	 */
	@Override
	public int countByLtModifiedDate(Date modifiedDate) {
		return _collectionPersistenceFinderByLtModifiedDate.count(
			finderCache, new Object[] {modifiedDate});
	}

	private UniquePersistenceFinder<AkismetEntry, NoSuchAkismetEntryException>
		_uniquePersistenceFinderByC_C;

	/**
	 * Returns the akismet entry where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchAkismetEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching akismet entry
	 * @throws NoSuchAkismetEntryException if a matching akismet entry could not be found
	 */
	@Override
	public AkismetEntry findByC_C(long classNameId, long classPK)
		throws NoSuchAkismetEntryException {

		return _uniquePersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the akismet entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching akismet entry, or <code>null</code> if a matching akismet entry could not be found
	 */
	@Override
	public AkismetEntry fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			finderCache, new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the akismet entry where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the akismet entry that was removed
	 */
	@Override
	public AkismetEntry removeByC_C(long classNameId, long classPK)
		throws NoSuchAkismetEntryException {

		AkismetEntry akismetEntry = findByC_C(classNameId, classPK);

		return remove(akismetEntry);
	}

	/**
	 * Returns the number of akismet entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching akismet entries
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	public AkismetEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AkismetEntry.class);

		setModelImplClass(AkismetEntryImpl.class);
		setModelPKClass(long.class);

		setTable(AkismetEntryTable.INSTANCE);
	}

	/**
	 * Creates a new akismet entry with the primary key. Does not add the akismet entry to the database.
	 *
	 * @param akismetEntryId the primary key for the new akismet entry
	 * @return the new akismet entry
	 */
	@Override
	public AkismetEntry create(long akismetEntryId) {
		AkismetEntry akismetEntry = new AkismetEntryImpl();

		akismetEntry.setNew(true);
		akismetEntry.setPrimaryKey(akismetEntryId);

		return akismetEntry;
	}

	/**
	 * Removes the akismet entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param akismetEntryId the primary key of the akismet entry
	 * @return the akismet entry that was removed
	 * @throws NoSuchAkismetEntryException if a akismet entry with the primary key could not be found
	 */
	@Override
	public AkismetEntry remove(long akismetEntryId)
		throws NoSuchAkismetEntryException {

		return remove((Serializable)akismetEntryId);
	}

	@Override
	protected AkismetEntry removeImpl(AkismetEntry akismetEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(akismetEntry)) {
				akismetEntry = (AkismetEntry)session.get(
					AkismetEntryImpl.class, akismetEntry.getPrimaryKeyObj());
			}

			if (akismetEntry != null) {
				session.delete(akismetEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (akismetEntry != null) {
			clearCache(akismetEntry);
		}

		return akismetEntry;
	}

	@Override
	public AkismetEntry updateImpl(AkismetEntry akismetEntry) {
		boolean isNew = akismetEntry.isNew();

		if (!(akismetEntry instanceof AkismetEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(akismetEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					akismetEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in akismetEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AkismetEntry implementation " +
					akismetEntry.getClass());
		}

		AkismetEntryModelImpl akismetEntryModelImpl =
			(AkismetEntryModelImpl)akismetEntry;

		if (!akismetEntryModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				akismetEntry.setModifiedDate(date);
			}
			else {
				akismetEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(akismetEntry);
			}
			else {
				akismetEntry = (AkismetEntry)session.merge(akismetEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(akismetEntry, false);

		if (isNew) {
			akismetEntry.setNew(false);
		}

		akismetEntry.resetOriginalValues();

		return akismetEntry;
	}

	/**
	 * Returns the akismet entry with the primary key or throws a <code>NoSuchAkismetEntryException</code> if it could not be found.
	 *
	 * @param akismetEntryId the primary key of the akismet entry
	 * @return the akismet entry
	 * @throws NoSuchAkismetEntryException if a akismet entry with the primary key could not be found
	 */
	@Override
	public AkismetEntry findByPrimaryKey(long akismetEntryId)
		throws NoSuchAkismetEntryException {

		return findByPrimaryKey((Serializable)akismetEntryId);
	}

	/**
	 * Returns the akismet entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param akismetEntryId the primary key of the akismet entry
	 * @return the akismet entry, or <code>null</code> if a akismet entry with the primary key could not be found
	 */
	@Override
	public AkismetEntry fetchByPrimaryKey(long akismetEntryId) {
		return fetchByPrimaryKey((Serializable)akismetEntryId);
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
		return "akismetEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_AKISMETENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AkismetEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the akismet entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByLtModifiedDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtModifiedDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"modifiedDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtModifiedDate",
					new String[] {Date.class.getName()},
					new String[] {"modifiedDate"}, false),
				_SQL_SELECT_AKISMETENTRY_WHERE, _SQL_COUNT_AKISMETENTRY_WHERE,
				AkismetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"akismetEntry.", "modifiedDate", FinderColumn.Type.DATE,
					"<", true, true, AkismetEntry::getModifiedDate));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				AkismetEntry::getClassNameId, AkismetEntry::getClassPK),
			_SQL_SELECT_AKISMETENTRY_WHERE, "",
			new FinderColumn<>(
				"akismetEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, AkismetEntry::getClassNameId),
			new FinderColumn<>(
				"akismetEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, AkismetEntry::getClassPK));

		AkismetEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AkismetEntryUtil.setPersistence(null);

		entityCache.removeCache(AkismetEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBCommunityPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBCommunityPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBCommunityPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AkismetEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_AKISMETENTRY =
		"SELECT akismetEntry FROM AkismetEntry akismetEntry";

	private static final String _SQL_SELECT_AKISMETENTRY_WHERE =
		"SELECT akismetEntry FROM AkismetEntry akismetEntry WHERE ";

	private static final String _SQL_COUNT_AKISMETENTRY_WHERE =
		"SELECT COUNT(akismetEntry) FROM AkismetEntry akismetEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1298039729