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
import com.liferay.portal.kernel.exception.NoSuchPortletException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.PortletPersistence;
import com.liferay.portal.kernel.service.persistence.PortletUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.model.impl.PortletModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the portlet service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortletPersistenceImpl
	extends BasePersistenceImpl<Portlet, NoSuchPortletException>
	implements PortletPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PortletUtil</code> to access the portlet persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PortletImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Portlet, NoSuchPortletException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the portlets where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of portlets
	 * @param end the upper bound of the range of portlets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlets
	 */
	@Override
	public List<Portlet> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Portlet> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet
	 * @throws NoSuchPortletException if a matching portlet could not be found
	 */
	@Override
	public Portlet findByCompanyId_First(
			long companyId, OrderByComparator<Portlet> orderByComparator)
		throws NoSuchPortletException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first portlet in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet, or <code>null</code> if a matching portlet could not be found
	 */
	@Override
	public Portlet fetchByCompanyId_First(
		long companyId, OrderByComparator<Portlet> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the portlets where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of portlets where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching portlets
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private UniquePersistenceFinder<Portlet, NoSuchPortletException>
		_uniquePersistenceFinderByC_P;

	/**
	 * Returns the portlet where companyId = &#63; and portletId = &#63; or throws a <code>NoSuchPortletException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param portletId the portlet ID
	 * @return the matching portlet
	 * @throws NoSuchPortletException if a matching portlet could not be found
	 */
	@Override
	public Portlet findByC_P(long companyId, String portletId)
		throws NoSuchPortletException {

		return _uniquePersistenceFinderByC_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, portletId});
	}

	/**
	 * Returns the portlet where companyId = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portlet, or <code>null</code> if a matching portlet could not be found
	 */
	@Override
	public Portlet fetchByC_P(
		long companyId, String portletId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, portletId}, useFinderCache);
	}

	/**
	 * Removes the portlet where companyId = &#63; and portletId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param portletId the portlet ID
	 * @return the portlet that was removed
	 */
	@Override
	public Portlet removeByC_P(long companyId, String portletId)
		throws NoSuchPortletException {

		Portlet portlet = findByC_P(companyId, portletId);

		return remove(portlet);
	}

	/**
	 * Returns the number of portlets where companyId = &#63; and portletId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param portletId the portlet ID
	 * @return the number of matching portlets
	 */
	@Override
	public int countByC_P(long companyId, String portletId) {
		return _uniquePersistenceFinderByC_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, portletId});
	}

	public PortletPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("id", "id_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Portlet.class);

		setModelImplClass(PortletImpl.class);
		setModelPKClass(long.class);

		setTable(PortletTable.INSTANCE);
	}

	/**
	 * Creates a new portlet with the primary key. Does not add the portlet to the database.
	 *
	 * @param id the primary key for the new portlet
	 * @return the new portlet
	 */
	@Override
	public Portlet create(long id) {
		Portlet portlet = new PortletImpl();

		portlet.setNew(true);
		portlet.setPrimaryKey(id);

		portlet.setCompanyId(CompanyThreadLocal.getCompanyId());

		return portlet;
	}

	/**
	 * Removes the portlet with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param id the primary key of the portlet
	 * @return the portlet that was removed
	 * @throws NoSuchPortletException if a portlet with the primary key could not be found
	 */
	@Override
	public Portlet remove(long id) throws NoSuchPortletException {
		return remove((Serializable)id);
	}

	@Override
	protected Portlet removeImpl(Portlet portlet) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(portlet)) {
				portlet = (Portlet)session.get(
					PortletImpl.class, portlet.getPrimaryKeyObj());
			}

			if (portlet != null) {
				session.delete(portlet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (portlet != null) {
			clearCache(portlet);
		}

		return portlet;
	}

	@Override
	public Portlet updateImpl(Portlet portlet) {
		boolean isNew = portlet.isNew();

		if (!(portlet instanceof PortletModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(portlet.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(portlet);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in portlet proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Portlet implementation " +
					portlet.getClass());
		}

		PortletModelImpl portletModelImpl = (PortletModelImpl)portlet;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(portlet);
			}
			else {
				portlet = (Portlet)session.merge(portlet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(portlet, false);

		if (isNew) {
			portlet.setNew(false);
		}

		portlet.resetOriginalValues();

		return portlet;
	}

	/**
	 * Returns the portlet with the primary key or throws a <code>NoSuchPortletException</code> if it could not be found.
	 *
	 * @param id the primary key of the portlet
	 * @return the portlet
	 * @throws NoSuchPortletException if a portlet with the primary key could not be found
	 */
	@Override
	public Portlet findByPrimaryKey(long id) throws NoSuchPortletException {
		return findByPrimaryKey((Serializable)id);
	}

	/**
	 * Returns the portlet with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param id the primary key of the portlet
	 * @return the portlet, or <code>null</code> if a portlet with the primary key could not be found
	 */
	@Override
	public Portlet fetchByPrimaryKey(long id) {
		return fetchByPrimaryKey((Serializable)id);
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
		return "id_";
	}

	@Override
	protected String getPKFieldName() {
		return "id";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PORTLET;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PortletModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the portlet persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_PORTLET_WHERE, _SQL_COUNT_PORTLET_WHERE,
				PortletModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"portlet.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Portlet::getCompanyId));

		_uniquePersistenceFinderByC_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "portletId"}, 0, 2, true,
				Portlet::getCompanyId,
				convertNullFunction(Portlet::getPortletId)),
			_SQL_SELECT_PORTLET_WHERE, "",
			new FinderColumn<>(
				"portlet.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Portlet::getCompanyId),
			new FinderColumn<>(
				"portlet.", "portletId", FinderColumn.Type.STRING, "=", true,
				true, Portlet::getPortletId));

		PortletUtil.setPersistence(this);
	}

	public void destroy() {
		PortletUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PortletImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PortletModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PORTLET =
		"SELECT portlet FROM Portlet portlet";

	private static final String _SQL_SELECT_PORTLET_WHERE =
		"SELECT portlet FROM Portlet portlet WHERE ";

	private static final String _SQL_COUNT_PORTLET_WHERE =
		"SELECT COUNT(portlet) FROM Portlet portlet WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Portlet exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"id", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:929096769