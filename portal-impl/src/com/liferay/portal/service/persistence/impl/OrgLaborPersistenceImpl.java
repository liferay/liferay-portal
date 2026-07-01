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
import com.liferay.portal.kernel.exception.NoSuchOrgLaborException;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.model.OrgLaborTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.OrgLaborPersistence;
import com.liferay.portal.kernel.service.persistence.OrgLaborUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.OrgLaborImpl;
import com.liferay.portal.model.impl.OrgLaborModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the org labor service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class OrgLaborPersistenceImpl
	extends BasePersistenceImpl<OrgLabor, NoSuchOrgLaborException>
	implements OrgLaborPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OrgLaborUtil</code> to access the org labor persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OrgLaborImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<OrgLabor, NoSuchOrgLaborException>
		_collectionPersistenceFinderByOrganizationId;

	/**
	 * Returns an ordered range of all the org labors where organizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OrgLaborModelImpl</code>.
	 * </p>
	 *
	 * @param organizationId the organization ID
	 * @param start the lower bound of the range of org labors
	 * @param end the upper bound of the range of org labors (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching org labors
	 */
	@Override
	public List<OrgLabor> findByOrganizationId(
		long organizationId, int start, int end,
		OrderByComparator<OrgLabor> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByOrganizationId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {organizationId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first org labor in the ordered set where organizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching org labor
	 * @throws NoSuchOrgLaborException if a matching org labor could not be found
	 */
	@Override
	public OrgLabor findByOrganizationId_First(
			long organizationId, OrderByComparator<OrgLabor> orderByComparator)
		throws NoSuchOrgLaborException {

		return _collectionPersistenceFinderByOrganizationId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {organizationId},
			orderByComparator);
	}

	/**
	 * Returns the first org labor in the ordered set where organizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching org labor, or <code>null</code> if a matching org labor could not be found
	 */
	@Override
	public OrgLabor fetchByOrganizationId_First(
		long organizationId, OrderByComparator<OrgLabor> orderByComparator) {

		return _collectionPersistenceFinderByOrganizationId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {organizationId},
			orderByComparator);
	}

	/**
	 * Removes all the org labors where organizationId = &#63; from the database.
	 *
	 * @param organizationId the organization ID
	 */
	@Override
	public void removeByOrganizationId(long organizationId) {
		_collectionPersistenceFinderByOrganizationId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {organizationId});
	}

	/**
	 * Returns the number of org labors where organizationId = &#63;.
	 *
	 * @param organizationId the organization ID
	 * @return the number of matching org labors
	 */
	@Override
	public int countByOrganizationId(long organizationId) {
		return _collectionPersistenceFinderByOrganizationId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {organizationId});
	}

	public OrgLaborPersistenceImpl() {
		setModelClass(OrgLabor.class);

		setModelImplClass(OrgLaborImpl.class);
		setModelPKClass(long.class);

		setTable(OrgLaborTable.INSTANCE);
	}

	/**
	 * Creates a new org labor with the primary key. Does not add the org labor to the database.
	 *
	 * @param orgLaborId the primary key for the new org labor
	 * @return the new org labor
	 */
	@Override
	public OrgLabor create(long orgLaborId) {
		OrgLabor orgLabor = new OrgLaborImpl();

		orgLabor.setNew(true);
		orgLabor.setPrimaryKey(orgLaborId);

		orgLabor.setCompanyId(CompanyThreadLocal.getCompanyId());

		return orgLabor;
	}

	/**
	 * Removes the org labor with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param orgLaborId the primary key of the org labor
	 * @return the org labor that was removed
	 * @throws NoSuchOrgLaborException if a org labor with the primary key could not be found
	 */
	@Override
	public OrgLabor remove(long orgLaborId) throws NoSuchOrgLaborException {
		return remove((Serializable)orgLaborId);
	}

	@Override
	protected OrgLabor removeImpl(OrgLabor orgLabor) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(orgLabor)) {
				orgLabor = (OrgLabor)session.get(
					OrgLaborImpl.class, orgLabor.getPrimaryKeyObj());
			}

			if (orgLabor != null) {
				session.delete(orgLabor);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (orgLabor != null) {
			clearCache(orgLabor);
		}

		return orgLabor;
	}

	@Override
	public OrgLabor updateImpl(OrgLabor orgLabor) {
		boolean isNew = orgLabor.isNew();

		if (!(orgLabor instanceof OrgLaborModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(orgLabor.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(orgLabor);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in orgLabor proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OrgLabor implementation " +
					orgLabor.getClass());
		}

		OrgLaborModelImpl orgLaborModelImpl = (OrgLaborModelImpl)orgLabor;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(orgLabor);
			}
			else {
				orgLabor = (OrgLabor)session.merge(orgLabor);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(orgLabor, false);

		if (isNew) {
			orgLabor.setNew(false);
		}

		orgLabor.resetOriginalValues();

		return orgLabor;
	}

	/**
	 * Returns the org labor with the primary key or throws a <code>NoSuchOrgLaborException</code> if it could not be found.
	 *
	 * @param orgLaborId the primary key of the org labor
	 * @return the org labor
	 * @throws NoSuchOrgLaborException if a org labor with the primary key could not be found
	 */
	@Override
	public OrgLabor findByPrimaryKey(long orgLaborId)
		throws NoSuchOrgLaborException {

		return findByPrimaryKey((Serializable)orgLaborId);
	}

	/**
	 * Returns the org labor with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param orgLaborId the primary key of the org labor
	 * @return the org labor, or <code>null</code> if a org labor with the primary key could not be found
	 */
	@Override
	public OrgLabor fetchByPrimaryKey(long orgLaborId) {
		return fetchByPrimaryKey((Serializable)orgLaborId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "orgLaborId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ORGLABOR;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OrgLaborModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the org labor persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByOrganizationId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByOrganizationId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"organizationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByOrganizationId", new String[] {Long.class.getName()},
					new String[] {"organizationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByOrganizationId",
					new String[] {Long.class.getName()},
					new String[] {"organizationId"}, false),
				_SQL_SELECT_ORGLABOR_WHERE, _SQL_COUNT_ORGLABOR_WHERE,
				OrgLaborModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"orgLabor.", "organizationId", FinderColumn.Type.LONG, "=",
					true, true, OrgLabor::getOrganizationId));

		OrgLaborUtil.setPersistence(this);
	}

	public void destroy() {
		OrgLaborUtil.setPersistence(null);

		EntityCacheUtil.removeCache(OrgLaborImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		OrgLaborModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ORGLABOR =
		"SELECT orgLabor FROM OrgLabor orgLabor";

	private static final String _SQL_SELECT_ORGLABOR_WHERE =
		"SELECT orgLabor FROM OrgLabor orgLabor WHERE ";

	private static final String _SQL_COUNT_ORGLABOR_WHERE =
		"SELECT COUNT(orgLabor) FROM OrgLabor orgLabor WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:422612602