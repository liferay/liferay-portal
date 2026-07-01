/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.impl;

import com.liferay.change.tracking.exception.NoSuchProcessException;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.model.CTProcessTable;
import com.liferay.change.tracking.model.impl.CTProcessImpl;
import com.liferay.change.tracking.model.impl.CTProcessModelImpl;
import com.liferay.change.tracking.service.persistence.CTProcessPersistence;
import com.liferay.change.tracking.service.persistence.CTProcessUtil;
import com.liferay.change.tracking.service.persistence.impl.constants.CTPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the ct process service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTProcessPersistence.class)
public class CTProcessPersistenceImpl
	extends BasePersistenceImpl<CTProcess, NoSuchProcessException>
	implements CTProcessPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTProcessUtil</code> to access the ct process persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTProcessImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder<CTProcess, NoSuchProcessException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the ct processes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct processes
	 */
	@Override
	public List<CTProcess> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTProcess> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct process in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process
	 * @throws NoSuchProcessException if a matching ct process could not be found
	 */
	@Override
	public CTProcess findByCompanyId_First(
			long companyId, OrderByComparator<CTProcess> orderByComparator)
		throws NoSuchProcessException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first ct process in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process, or <code>null</code> if a matching ct process could not be found
	 */
	@Override
	public CTProcess fetchByCompanyId_First(
		long companyId, OrderByComparator<CTProcess> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ct processes that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct processes that the user has permission to view
	 */
	@Override
	public List<CTProcess> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTProcess> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the ct processes where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of ct processes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct processes
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of ct processes that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ct processes that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<CTProcess, NoSuchProcessException>
		_collectionPersistenceFinderByCtCollectionId;

	/**
	 * Returns an ordered range of all the ct processes where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct processes
	 */
	@Override
	public List<CTProcess> findByCtCollectionId(
		long ctCollectionId, int start, int end,
		OrderByComparator<CTProcess> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCtCollectionId.find(
			finderCache, new Object[] {ctCollectionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process
	 * @throws NoSuchProcessException if a matching ct process could not be found
	 */
	@Override
	public CTProcess findByCtCollectionId_First(
			long ctCollectionId, OrderByComparator<CTProcess> orderByComparator)
		throws NoSuchProcessException {

		return _collectionPersistenceFinderByCtCollectionId.findFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process, or <code>null</code> if a matching ct process could not be found
	 */
	@Override
	public CTProcess fetchByCtCollectionId_First(
		long ctCollectionId, OrderByComparator<CTProcess> orderByComparator) {

		return _collectionPersistenceFinderByCtCollectionId.fetchFirst(
			finderCache, new Object[] {ctCollectionId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ct processes that the user has permissions to view where ctCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct processes that the user has permission to view
	 */
	@Override
	public List<CTProcess> filterFindByCtCollectionId(
		long ctCollectionId, int start, int end,
		OrderByComparator<CTProcess> orderByComparator) {

		return _collectionPersistenceFinderByCtCollectionId.filterFind(
			finderCache, new Object[] {ctCollectionId}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the ct processes where ctCollectionId = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 */
	@Override
	public void removeByCtCollectionId(long ctCollectionId) {
		_collectionPersistenceFinderByCtCollectionId.remove(
			finderCache, new Object[] {ctCollectionId});
	}

	/**
	 * Returns the number of ct processes where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct processes
	 */
	@Override
	public int countByCtCollectionId(long ctCollectionId) {
		return _collectionPersistenceFinderByCtCollectionId.count(
			finderCache, new Object[] {ctCollectionId});
	}

	/**
	 * Returns the number of ct processes that the user has permission to view where ctCollectionId = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @return the number of matching ct processes that the user has permission to view
	 */
	@Override
	public int filterCountByCtCollectionId(long ctCollectionId) {
		return _collectionPersistenceFinderByCtCollectionId.filterCount(
			finderCache, new Object[] {ctCollectionId});
	}

	private FilterCollectionPersistenceFinder<CTProcess, NoSuchProcessException>
		_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the ct processes where ctCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ct processes
	 */
	@Override
	public List<CTProcess> findByC_T(
		long ctCollectionId, int type, int start, int end,
		OrderByComparator<CTProcess> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			finderCache, new Object[] {ctCollectionId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process
	 * @throws NoSuchProcessException if a matching ct process could not be found
	 */
	@Override
	public CTProcess findByC_T_First(
			long ctCollectionId, int type,
			OrderByComparator<CTProcess> orderByComparator)
		throws NoSuchProcessException {

		return _collectionPersistenceFinderByC_T.findFirst(
			finderCache, new Object[] {ctCollectionId, type},
			orderByComparator);
	}

	/**
	 * Returns the first ct process in the ordered set where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ct process, or <code>null</code> if a matching ct process could not be found
	 */
	@Override
	public CTProcess fetchByC_T_First(
		long ctCollectionId, int type,
		OrderByComparator<CTProcess> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			finderCache, new Object[] {ctCollectionId, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ct processes that the user has permissions to view where ctCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTProcessModelImpl</code>.
	 * </p>
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @param start the lower bound of the range of ct processes
	 * @param end the upper bound of the range of ct processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ct processes that the user has permission to view
	 */
	@Override
	public List<CTProcess> filterFindByC_T(
		long ctCollectionId, int type, int start, int end,
		OrderByComparator<CTProcess> orderByComparator) {

		return _collectionPersistenceFinderByC_T.filterFind(
			finderCache, new Object[] {ctCollectionId, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the ct processes where ctCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long ctCollectionId, int type) {
		_collectionPersistenceFinderByC_T.remove(
			finderCache, new Object[] {ctCollectionId, type});
	}

	/**
	 * Returns the number of ct processes where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @return the number of matching ct processes
	 */
	@Override
	public int countByC_T(long ctCollectionId, int type) {
		return _collectionPersistenceFinderByC_T.count(
			finderCache, new Object[] {ctCollectionId, type});
	}

	/**
	 * Returns the number of ct processes that the user has permission to view where ctCollectionId = &#63; and type = &#63;.
	 *
	 * @param ctCollectionId the ct collection ID
	 * @param type the type
	 * @return the number of matching ct processes that the user has permission to view
	 */
	@Override
	public int filterCountByC_T(long ctCollectionId, int type) {
		return _collectionPersistenceFinderByC_T.filterCount(
			finderCache, new Object[] {ctCollectionId, type});
	}

	public CTProcessPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CTProcess.class);

		setModelImplClass(CTProcessImpl.class);
		setModelPKClass(long.class);

		setTable(CTProcessTable.INSTANCE);
	}

	/**
	 * Creates a new ct process with the primary key. Does not add the ct process to the database.
	 *
	 * @param ctProcessId the primary key for the new ct process
	 * @return the new ct process
	 */
	@Override
	public CTProcess create(long ctProcessId) {
		CTProcess ctProcess = new CTProcessImpl();

		ctProcess.setNew(true);
		ctProcess.setPrimaryKey(ctProcessId);

		ctProcess.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctProcess;
	}

	/**
	 * Removes the ct process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process that was removed
	 * @throws NoSuchProcessException if a ct process with the primary key could not be found
	 */
	@Override
	public CTProcess remove(long ctProcessId) throws NoSuchProcessException {
		return remove((Serializable)ctProcessId);
	}

	@Override
	protected CTProcess removeImpl(CTProcess ctProcess) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctProcess)) {
				ctProcess = (CTProcess)session.get(
					CTProcessImpl.class, ctProcess.getPrimaryKeyObj());
			}

			if (ctProcess != null) {
				session.delete(ctProcess);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctProcess != null) {
			clearCache(ctProcess);
		}

		return ctProcess;
	}

	@Override
	public CTProcess updateImpl(CTProcess ctProcess) {
		boolean isNew = ctProcess.isNew();

		if (!(ctProcess instanceof CTProcessModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctProcess.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctProcess);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctProcess proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTProcess implementation " +
					ctProcess.getClass());
		}

		CTProcessModelImpl ctProcessModelImpl = (CTProcessModelImpl)ctProcess;

		if (isNew && (ctProcess.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ctProcess.setCreateDate(date);
			}
			else {
				ctProcess.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctProcess);
			}
			else {
				ctProcess = (CTProcess)session.merge(ctProcess);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctProcess, false);

		if (isNew) {
			ctProcess.setNew(false);
		}

		ctProcess.resetOriginalValues();

		return ctProcess;
	}

	/**
	 * Returns the ct process with the primary key or throws a <code>NoSuchProcessException</code> if it could not be found.
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process
	 * @throws NoSuchProcessException if a ct process with the primary key could not be found
	 */
	@Override
	public CTProcess findByPrimaryKey(long ctProcessId)
		throws NoSuchProcessException {

		return findByPrimaryKey((Serializable)ctProcessId);
	}

	/**
	 * Returns the ct process with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctProcessId the primary key of the ct process
	 * @return the ct process, or <code>null</code> if a ct process with the primary key could not be found
	 */
	@Override
	public CTProcess fetchByPrimaryKey(long ctProcessId) {
		return fetchByPrimaryKey((Serializable)ctProcessId);
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
		return "ctProcessId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTPROCESS;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTProcessModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the ct process persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_CTPROCESS_WHERE, _SQL_COUNT_CTPROCESS_WHERE,
				CTProcessModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctProcess.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CTProcess::getCompanyId));

		_collectionPersistenceFinderByCtCollectionId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCtCollectionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCtCollectionId", new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCtCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"ctCollectionId"}, false),
				_SQL_SELECT_CTPROCESS_WHERE, _SQL_COUNT_CTPROCESS_WHERE,
				CTProcessModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctProcess.", "ctCollectionId", FinderColumn.Type.LONG, "=",
					true, true, CTProcess::getCtCollectionId));

		_collectionPersistenceFinderByC_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ctCollectionId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"ctCollectionId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"ctCollectionId", "type_"}, false),
				_SQL_SELECT_CTPROCESS_WHERE, _SQL_COUNT_CTPROCESS_WHERE,
				CTProcessModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctProcess.", "ctCollectionId", FinderColumn.Type.LONG, "=",
					true, true, CTProcess::getCtCollectionId),
				new FinderColumn<>(
					"ctProcess.", "type", "type_", FinderColumn.Type.INTEGER,
					"=", true, true, CTProcess::getType));

		CTProcessUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTProcessUtil.setPersistence(null);

		entityCache.removeCache(CTProcessImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CTProcessModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTPROCESS =
		"SELECT ctProcess FROM CTProcess ctProcess";

	private static final String _SQL_SELECT_CTPROCESS_WHERE =
		"SELECT ctProcess FROM CTProcess ctProcess WHERE ";

	private static final String _SQL_COUNT_CTPROCESS_WHERE =
		"SELECT COUNT(ctProcess) FROM CTProcess ctProcess WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:969777141