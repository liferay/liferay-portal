/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.persistence.impl;

import com.liferay.dispatch.exception.NoSuchLogException;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchLogTable;
import com.liferay.dispatch.model.impl.DispatchLogImpl;
import com.liferay.dispatch.model.impl.DispatchLogModelImpl;
import com.liferay.dispatch.service.persistence.DispatchLogPersistence;
import com.liferay.dispatch.service.persistence.DispatchLogUtil;
import com.liferay.dispatch.service.persistence.impl.constants.DispatchPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
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
 * The persistence implementation for the dispatch log service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matija Petanjek
 * @generated
 */
@Component(service = DispatchLogPersistence.class)
public class DispatchLogPersistenceImpl
	extends BasePersistenceImpl<DispatchLog, NoSuchLogException>
	implements DispatchLogPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DispatchLogUtil</code> to access the dispatch log persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DispatchLogImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DispatchLog, NoSuchLogException>
		_collectionPersistenceFinderByDispatchTriggerId;

	/**
	 * Returns an ordered range of all the dispatch logs where dispatchTriggerId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchLogModelImpl</code>.
	 * </p>
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param start the lower bound of the range of dispatch logs
	 * @param end the upper bound of the range of dispatch logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch logs
	 */
	@Override
	public List<DispatchLog> findByDispatchTriggerId(
		long dispatchTriggerId, int start, int end,
		OrderByComparator<DispatchLog> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDispatchTriggerId.find(
			finderCache, new Object[] {dispatchTriggerId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dispatch log in the ordered set where dispatchTriggerId = &#63;.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch log
	 * @throws NoSuchLogException if a matching dispatch log could not be found
	 */
	@Override
	public DispatchLog findByDispatchTriggerId_First(
			long dispatchTriggerId,
			OrderByComparator<DispatchLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByDispatchTriggerId.findFirst(
			finderCache, new Object[] {dispatchTriggerId}, orderByComparator);
	}

	/**
	 * Returns the first dispatch log in the ordered set where dispatchTriggerId = &#63;.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch log, or <code>null</code> if a matching dispatch log could not be found
	 */
	@Override
	public DispatchLog fetchByDispatchTriggerId_First(
		long dispatchTriggerId,
		OrderByComparator<DispatchLog> orderByComparator) {

		return _collectionPersistenceFinderByDispatchTriggerId.fetchFirst(
			finderCache, new Object[] {dispatchTriggerId}, orderByComparator);
	}

	/**
	 * Removes all the dispatch logs where dispatchTriggerId = &#63; from the database.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 */
	@Override
	public void removeByDispatchTriggerId(long dispatchTriggerId) {
		_collectionPersistenceFinderByDispatchTriggerId.remove(
			finderCache, new Object[] {dispatchTriggerId});
	}

	/**
	 * Returns the number of dispatch logs where dispatchTriggerId = &#63;.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @return the number of matching dispatch logs
	 */
	@Override
	public int countByDispatchTriggerId(long dispatchTriggerId) {
		return _collectionPersistenceFinderByDispatchTriggerId.count(
			finderCache, new Object[] {dispatchTriggerId});
	}

	private CollectionPersistenceFinder<DispatchLog, NoSuchLogException>
		_collectionPersistenceFinderByDTI_S;

	/**
	 * Returns an ordered range of all the dispatch logs where dispatchTriggerId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchLogModelImpl</code>.
	 * </p>
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param status the status
	 * @param start the lower bound of the range of dispatch logs
	 * @param end the upper bound of the range of dispatch logs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch logs
	 */
	@Override
	public List<DispatchLog> findByDTI_S(
		long dispatchTriggerId, int status, int start, int end,
		OrderByComparator<DispatchLog> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDTI_S.find(
			finderCache, new Object[] {dispatchTriggerId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first dispatch log in the ordered set where dispatchTriggerId = &#63; and status = &#63;.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch log
	 * @throws NoSuchLogException if a matching dispatch log could not be found
	 */
	@Override
	public DispatchLog findByDTI_S_First(
			long dispatchTriggerId, int status,
			OrderByComparator<DispatchLog> orderByComparator)
		throws NoSuchLogException {

		return _collectionPersistenceFinderByDTI_S.findFirst(
			finderCache, new Object[] {dispatchTriggerId, status},
			orderByComparator);
	}

	/**
	 * Returns the first dispatch log in the ordered set where dispatchTriggerId = &#63; and status = &#63;.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch log, or <code>null</code> if a matching dispatch log could not be found
	 */
	@Override
	public DispatchLog fetchByDTI_S_First(
		long dispatchTriggerId, int status,
		OrderByComparator<DispatchLog> orderByComparator) {

		return _collectionPersistenceFinderByDTI_S.fetchFirst(
			finderCache, new Object[] {dispatchTriggerId, status},
			orderByComparator);
	}

	/**
	 * Removes all the dispatch logs where dispatchTriggerId = &#63; and status = &#63; from the database.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param status the status
	 */
	@Override
	public void removeByDTI_S(long dispatchTriggerId, int status) {
		_collectionPersistenceFinderByDTI_S.remove(
			finderCache, new Object[] {dispatchTriggerId, status});
	}

	/**
	 * Returns the number of dispatch logs where dispatchTriggerId = &#63; and status = &#63;.
	 *
	 * @param dispatchTriggerId the dispatch trigger ID
	 * @param status the status
	 * @return the number of matching dispatch logs
	 */
	@Override
	public int countByDTI_S(long dispatchTriggerId, int status) {
		return _collectionPersistenceFinderByDTI_S.count(
			finderCache, new Object[] {dispatchTriggerId, status});
	}

	public DispatchLogPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("output", "output_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DispatchLog.class);

		setModelImplClass(DispatchLogImpl.class);
		setModelPKClass(long.class);

		setTable(DispatchLogTable.INSTANCE);
	}

	/**
	 * Creates a new dispatch log with the primary key. Does not add the dispatch log to the database.
	 *
	 * @param dispatchLogId the primary key for the new dispatch log
	 * @return the new dispatch log
	 */
	@Override
	public DispatchLog create(long dispatchLogId) {
		DispatchLog dispatchLog = new DispatchLogImpl();

		dispatchLog.setNew(true);
		dispatchLog.setPrimaryKey(dispatchLogId);

		dispatchLog.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dispatchLog;
	}

	/**
	 * Removes the dispatch log with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dispatchLogId the primary key of the dispatch log
	 * @return the dispatch log that was removed
	 * @throws NoSuchLogException if a dispatch log with the primary key could not be found
	 */
	@Override
	public DispatchLog remove(long dispatchLogId) throws NoSuchLogException {
		return remove((Serializable)dispatchLogId);
	}

	@Override
	protected DispatchLog removeImpl(DispatchLog dispatchLog) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dispatchLog)) {
				dispatchLog = (DispatchLog)session.get(
					DispatchLogImpl.class, dispatchLog.getPrimaryKeyObj());
			}

			if (dispatchLog != null) {
				session.delete(dispatchLog);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dispatchLog != null) {
			clearCache(dispatchLog);
		}

		return dispatchLog;
	}

	@Override
	public DispatchLog updateImpl(DispatchLog dispatchLog) {
		boolean isNew = dispatchLog.isNew();

		if (!(dispatchLog instanceof DispatchLogModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dispatchLog.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(dispatchLog);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dispatchLog proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DispatchLog implementation " +
					dispatchLog.getClass());
		}

		DispatchLogModelImpl dispatchLogModelImpl =
			(DispatchLogModelImpl)dispatchLog;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dispatchLog.getCreateDate() == null)) {
			if (serviceContext == null) {
				dispatchLog.setCreateDate(date);
			}
			else {
				dispatchLog.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!dispatchLogModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dispatchLog.setModifiedDate(date);
			}
			else {
				dispatchLog.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dispatchLog);
			}
			else {
				dispatchLog = (DispatchLog)session.merge(dispatchLog);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dispatchLog, false);

		if (isNew) {
			dispatchLog.setNew(false);
		}

		dispatchLog.resetOriginalValues();

		return dispatchLog;
	}

	/**
	 * Returns the dispatch log with the primary key or throws a <code>NoSuchLogException</code> if it could not be found.
	 *
	 * @param dispatchLogId the primary key of the dispatch log
	 * @return the dispatch log
	 * @throws NoSuchLogException if a dispatch log with the primary key could not be found
	 */
	@Override
	public DispatchLog findByPrimaryKey(long dispatchLogId)
		throws NoSuchLogException {

		return findByPrimaryKey((Serializable)dispatchLogId);
	}

	/**
	 * Returns the dispatch log with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dispatchLogId the primary key of the dispatch log
	 * @return the dispatch log, or <code>null</code> if a dispatch log with the primary key could not be found
	 */
	@Override
	public DispatchLog fetchByPrimaryKey(long dispatchLogId) {
		return fetchByPrimaryKey((Serializable)dispatchLogId);
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
		return "dispatchLogId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DISPATCHLOG;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DispatchLogModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dispatch log persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByDispatchTriggerId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDispatchTriggerId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"dispatchTriggerId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDispatchTriggerId",
					new String[] {Long.class.getName()},
					new String[] {"dispatchTriggerId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDispatchTriggerId",
					new String[] {Long.class.getName()},
					new String[] {"dispatchTriggerId"}, false),
				_SQL_SELECT_DISPATCHLOG_WHERE, _SQL_COUNT_DISPATCHLOG_WHERE,
				DispatchLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"dispatchLog.", "dispatchTriggerId", FinderColumn.Type.LONG,
					"=", true, true, DispatchLog::getDispatchTriggerId));

		_collectionPersistenceFinderByDTI_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByDTI_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"dispatchTriggerId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByDTI_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"dispatchTriggerId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByDTI_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"dispatchTriggerId", "status"}, false),
			_SQL_SELECT_DISPATCHLOG_WHERE, _SQL_COUNT_DISPATCHLOG_WHERE,
			DispatchLogModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"dispatchLog.", "dispatchTriggerId", FinderColumn.Type.LONG,
				"=", true, true, DispatchLog::getDispatchTriggerId),
			new FinderColumn<>(
				"dispatchLog.", "status", FinderColumn.Type.INTEGER, "=", true,
				true, DispatchLog::getStatus));

		DispatchLogUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DispatchLogUtil.setPersistence(null);

		entityCache.removeCache(DispatchLogImpl.class.getName());
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DispatchLogModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DISPATCHLOG =
		"SELECT dispatchLog FROM DispatchLog dispatchLog";

	private static final String _SQL_SELECT_DISPATCHLOG_WHERE =
		"SELECT dispatchLog FROM DispatchLog dispatchLog WHERE ";

	private static final String _SQL_COUNT_DISPATCHLOG_WHERE =
		"SELECT COUNT(dispatchLog) FROM DispatchLog dispatchLog WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"output"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1926173064