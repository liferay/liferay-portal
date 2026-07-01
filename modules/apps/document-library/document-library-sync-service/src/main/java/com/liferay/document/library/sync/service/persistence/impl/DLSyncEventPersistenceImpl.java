/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.sync.service.persistence.impl;

import com.liferay.document.library.sync.exception.NoSuchEventException;
import com.liferay.document.library.sync.model.DLSyncEvent;
import com.liferay.document.library.sync.model.DLSyncEventTable;
import com.liferay.document.library.sync.model.impl.DLSyncEventImpl;
import com.liferay.document.library.sync.model.impl.DLSyncEventModelImpl;
import com.liferay.document.library.sync.service.persistence.DLSyncEventPersistence;
import com.liferay.document.library.sync.service.persistence.DLSyncEventUtil;
import com.liferay.document.library.sync.service.persistence.impl.constants.DLSyncPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

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
 * The persistence implementation for the dl sync event service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DLSyncEventPersistence.class)
public class DLSyncEventPersistenceImpl
	extends BasePersistenceImpl<DLSyncEvent, NoSuchEventException>
	implements DLSyncEventPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLSyncEventUtil</code> to access the dl sync event persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLSyncEventImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DLSyncEvent, NoSuchEventException>
		_collectionPersistenceFinderByGtModifiedTime;

	/**
	 * Returns all the dl sync events where modifiedTime &gt; &#63;.
	 *
	 * @param modifiedTime the modified time
	 * @return the matching dl sync events
	 */
	@Override
	public List<DLSyncEvent> findByGtModifiedTime(long modifiedTime) {
		return findByGtModifiedTime(
			modifiedTime, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dl sync events where modifiedTime &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLSyncEventModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedTime the modified time
	 * @param start the lower bound of the range of dl sync events
	 * @param end the upper bound of the range of dl sync events (not inclusive)
	 * @return the range of matching dl sync events
	 */
	@Override
	public List<DLSyncEvent> findByGtModifiedTime(
		long modifiedTime, int start, int end) {

		return findByGtModifiedTime(modifiedTime, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dl sync events where modifiedTime &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLSyncEventModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedTime the modified time
	 * @param start the lower bound of the range of dl sync events
	 * @param end the upper bound of the range of dl sync events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dl sync events
	 */
	@Override
	public List<DLSyncEvent> findByGtModifiedTime(
		long modifiedTime, int start, int end,
		OrderByComparator<DLSyncEvent> orderByComparator) {

		return findByGtModifiedTime(
			modifiedTime, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the dl sync events where modifiedTime &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLSyncEventModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedTime the modified time
	 * @param start the lower bound of the range of dl sync events
	 * @param end the upper bound of the range of dl sync events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dl sync events
	 */
	@Override
	public List<DLSyncEvent> findByGtModifiedTime(
		long modifiedTime, int start, int end,
		OrderByComparator<DLSyncEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGtModifiedTime.find(
			finderCache, new Object[] {modifiedTime}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dl sync event in the ordered set where modifiedTime &gt; &#63;.
	 *
	 * @param modifiedTime the modified time
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl sync event
	 * @throws NoSuchEventException if a matching dl sync event could not be found
	 */
	@Override
	public DLSyncEvent findByGtModifiedTime_First(
			long modifiedTime, OrderByComparator<DLSyncEvent> orderByComparator)
		throws NoSuchEventException {

		return _collectionPersistenceFinderByGtModifiedTime.findFirst(
			finderCache, new Object[] {modifiedTime}, orderByComparator);
	}

	/**
	 * Returns the first dl sync event in the ordered set where modifiedTime &gt; &#63;.
	 *
	 * @param modifiedTime the modified time
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dl sync event, or <code>null</code> if a matching dl sync event could not be found
	 */
	@Override
	public DLSyncEvent fetchByGtModifiedTime_First(
		long modifiedTime, OrderByComparator<DLSyncEvent> orderByComparator) {

		return _collectionPersistenceFinderByGtModifiedTime.fetchFirst(
			finderCache, new Object[] {modifiedTime}, orderByComparator);
	}

	/**
	 * Removes all the dl sync events where modifiedTime &gt; &#63; from the database.
	 *
	 * @param modifiedTime the modified time
	 */
	@Override
	public void removeByGtModifiedTime(long modifiedTime) {
		_collectionPersistenceFinderByGtModifiedTime.remove(
			finderCache, new Object[] {modifiedTime});
	}

	/**
	 * Returns the number of dl sync events where modifiedTime &gt; &#63;.
	 *
	 * @param modifiedTime the modified time
	 * @return the number of matching dl sync events
	 */
	@Override
	public int countByGtModifiedTime(long modifiedTime) {
		return _collectionPersistenceFinderByGtModifiedTime.count(
			finderCache, new Object[] {modifiedTime});
	}

	private UniquePersistenceFinder<DLSyncEvent, NoSuchEventException>
		_uniquePersistenceFinderByTypePK;

	/**
	 * Returns the dl sync event where typePK = &#63; or throws a <code>NoSuchEventException</code> if it could not be found.
	 *
	 * @param typePK the type pk
	 * @return the matching dl sync event
	 * @throws NoSuchEventException if a matching dl sync event could not be found
	 */
	@Override
	public DLSyncEvent findByTypePK(long typePK) throws NoSuchEventException {
		return _uniquePersistenceFinderByTypePK.find(
			finderCache, new Object[] {typePK});
	}

	/**
	 * Returns the dl sync event where typePK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param typePK the type pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl sync event, or <code>null</code> if a matching dl sync event could not be found
	 */
	@Override
	public DLSyncEvent fetchByTypePK(long typePK, boolean useFinderCache) {
		return _uniquePersistenceFinderByTypePK.fetch(
			finderCache, new Object[] {typePK}, useFinderCache);
	}

	/**
	 * Removes the dl sync event where typePK = &#63; from the database.
	 *
	 * @param typePK the type pk
	 * @return the dl sync event that was removed
	 */
	@Override
	public DLSyncEvent removeByTypePK(long typePK) throws NoSuchEventException {
		DLSyncEvent dlSyncEvent = findByTypePK(typePK);

		return remove(dlSyncEvent);
	}

	/**
	 * Returns the number of dl sync events where typePK = &#63;.
	 *
	 * @param typePK the type pk
	 * @return the number of matching dl sync events
	 */
	@Override
	public int countByTypePK(long typePK) {
		return _uniquePersistenceFinderByTypePK.count(
			finderCache, new Object[] {typePK});
	}

	public DLSyncEventPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLSyncEvent.class);

		setModelImplClass(DLSyncEventImpl.class);
		setModelPKClass(long.class);

		setTable(DLSyncEventTable.INSTANCE);
	}

	/**
	 * Creates a new dl sync event with the primary key. Does not add the dl sync event to the database.
	 *
	 * @param syncEventId the primary key for the new dl sync event
	 * @return the new dl sync event
	 */
	@Override
	public DLSyncEvent create(long syncEventId) {
		DLSyncEvent dlSyncEvent = new DLSyncEventImpl();

		dlSyncEvent.setNew(true);
		dlSyncEvent.setPrimaryKey(syncEventId);

		dlSyncEvent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlSyncEvent;
	}

	/**
	 * Removes the dl sync event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param syncEventId the primary key of the dl sync event
	 * @return the dl sync event that was removed
	 * @throws NoSuchEventException if a dl sync event with the primary key could not be found
	 */
	@Override
	public DLSyncEvent remove(long syncEventId) throws NoSuchEventException {
		return remove((Serializable)syncEventId);
	}

	@Override
	protected DLSyncEvent removeImpl(DLSyncEvent dlSyncEvent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlSyncEvent)) {
				dlSyncEvent = (DLSyncEvent)session.get(
					DLSyncEventImpl.class, dlSyncEvent.getPrimaryKeyObj());
			}

			if (dlSyncEvent != null) {
				session.delete(dlSyncEvent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlSyncEvent != null) {
			clearCache(dlSyncEvent);
		}

		return dlSyncEvent;
	}

	@Override
	public DLSyncEvent updateImpl(DLSyncEvent dlSyncEvent) {
		boolean isNew = dlSyncEvent.isNew();

		if (!(dlSyncEvent instanceof DLSyncEventModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlSyncEvent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dlSyncEvent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlSyncEvent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLSyncEvent implementation " +
					dlSyncEvent.getClass());
		}

		DLSyncEventModelImpl dlSyncEventModelImpl =
			(DLSyncEventModelImpl)dlSyncEvent;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dlSyncEvent);
			}
			else {
				dlSyncEvent = (DLSyncEvent)session.merge(dlSyncEvent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlSyncEvent, false);

		if (isNew) {
			dlSyncEvent.setNew(false);
		}

		dlSyncEvent.resetOriginalValues();

		return dlSyncEvent;
	}

	/**
	 * Returns the dl sync event with the primary key or throws a <code>NoSuchEventException</code> if it could not be found.
	 *
	 * @param syncEventId the primary key of the dl sync event
	 * @return the dl sync event
	 * @throws NoSuchEventException if a dl sync event with the primary key could not be found
	 */
	@Override
	public DLSyncEvent findByPrimaryKey(long syncEventId)
		throws NoSuchEventException {

		return findByPrimaryKey((Serializable)syncEventId);
	}

	/**
	 * Returns the dl sync event with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param syncEventId the primary key of the dl sync event
	 * @return the dl sync event, or <code>null</code> if a dl sync event with the primary key could not be found
	 */
	@Override
	public DLSyncEvent fetchByPrimaryKey(long syncEventId) {
		return fetchByPrimaryKey((Serializable)syncEventId);
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
		return "syncEventId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLSYNCEVENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DLSyncEventModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dl sync event persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGtModifiedTime =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByGtModifiedTime",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"modifiedTime"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByGtModifiedTime",
					new String[] {Long.class.getName()},
					new String[] {"modifiedTime"}, false),
				_SQL_SELECT_DLSYNCEVENT_WHERE, _SQL_COUNT_DLSYNCEVENT_WHERE,
				DLSyncEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"dlSyncEvent.", "modifiedTime", FinderColumn.Type.LONG, ">",
					true, true, DLSyncEvent::getModifiedTime));

		_uniquePersistenceFinderByTypePK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByTypePK",
				new String[] {Long.class.getName()}, new String[] {"typePK"}, 0,
				0, false, DLSyncEvent::getTypePK),
			_SQL_SELECT_DLSYNCEVENT_WHERE, "",
			new FinderColumn<>(
				"dlSyncEvent.", "typePK", FinderColumn.Type.LONG, "=", true,
				true, DLSyncEvent::getTypePK));

		DLSyncEventUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DLSyncEventUtil.setPersistence(null);

		entityCache.removeCache(DLSyncEventImpl.class.getName());
	}

	@Override
	@Reference(
		target = DLSyncPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DLSyncPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DLSyncPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DLSyncEventModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DLSYNCEVENT =
		"SELECT dlSyncEvent FROM DLSyncEvent dlSyncEvent";

	private static final String _SQL_SELECT_DLSYNCEVENT_WHERE =
		"SELECT dlSyncEvent FROM DLSyncEvent dlSyncEvent WHERE ";

	private static final String _SQL_COUNT_DLSYNCEVENT_WHERE =
		"SELECT COUNT(dlSyncEvent) FROM DLSyncEvent dlSyncEvent WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DLSyncEvent exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DLSyncEventPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1099309905