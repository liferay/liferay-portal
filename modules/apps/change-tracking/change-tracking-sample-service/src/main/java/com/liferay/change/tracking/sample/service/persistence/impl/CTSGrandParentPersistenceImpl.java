/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.impl;

import com.liferay.change.tracking.sample.exception.NoSuchCTSGrandParentException;
import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.change.tracking.sample.model.CTSGrandParentTable;
import com.liferay.change.tracking.sample.model.impl.CTSGrandParentImpl;
import com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentUtil;
import com.liferay.change.tracking.sample.service.persistence.impl.constants.CTSPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cts grand parent service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTSGrandParentPersistence.class)
public class CTSGrandParentPersistenceImpl
	extends BasePersistenceImpl<CTSGrandParent, NoSuchCTSGrandParentException>
	implements CTSGrandParentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSGrandParentUtil</code> to access the cts grand parent persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSGrandParentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CTSGrandParent, NoSuchCTSGrandParentException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts grand parents
	 */
	@Override
	public List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent
	 * @throws NoSuchCTSGrandParentException if a matching cts grand parent could not be found
	 */
	@Override
	public CTSGrandParent findByCompanyId_First(
			long companyId, OrderByComparator<CTSGrandParent> orderByComparator)
		throws NoSuchCTSGrandParentException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent, or <code>null</code> if a matching cts grand parent could not be found
	 */
	@Override
	public CTSGrandParent fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSGrandParent> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cts grand parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cts grand parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts grand parents
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	public CTSGrandParentPersistenceImpl() {
		setModelClass(CTSGrandParent.class);

		setModelImplClass(CTSGrandParentImpl.class);
		setModelPKClass(long.class);

		setTable(CTSGrandParentTable.INSTANCE);
	}

	/**
	 * Creates a new cts grand parent with the primary key. Does not add the cts grand parent to the database.
	 *
	 * @param ctsGrandParentId the primary key for the new cts grand parent
	 * @return the new cts grand parent
	 */
	@Override
	public CTSGrandParent create(long ctsGrandParentId) {
		CTSGrandParent ctsGrandParent = new CTSGrandParentImpl();

		ctsGrandParent.setNew(true);
		ctsGrandParent.setPrimaryKey(ctsGrandParentId);

		ctsGrandParent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctsGrandParent;
	}

	/**
	 * Removes the cts grand parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent that was removed
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent remove(long ctsGrandParentId)
		throws NoSuchCTSGrandParentException {

		return remove((Serializable)ctsGrandParentId);
	}

	@Override
	protected CTSGrandParent removeImpl(CTSGrandParent ctsGrandParent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctsGrandParent)) {
				ctsGrandParent = (CTSGrandParent)session.get(
					CTSGrandParentImpl.class,
					ctsGrandParent.getPrimaryKeyObj());
			}

			if (ctsGrandParent != null) {
				session.delete(ctsGrandParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctsGrandParent != null) {
			clearCache(ctsGrandParent);
		}

		return ctsGrandParent;
	}

	@Override
	public CTSGrandParent updateImpl(CTSGrandParent ctsGrandParent) {
		boolean isNew = ctsGrandParent.isNew();

		if (!(ctsGrandParent instanceof CTSGrandParentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctsGrandParent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ctsGrandParent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctsGrandParent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSGrandParent implementation " +
					ctsGrandParent.getClass());
		}

		CTSGrandParentModelImpl ctsGrandParentModelImpl =
			(CTSGrandParentModelImpl)ctsGrandParent;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ctsGrandParent);
			}
			else {
				ctsGrandParent = (CTSGrandParent)session.merge(ctsGrandParent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctsGrandParent, false);

		if (isNew) {
			ctsGrandParent.setNew(false);
		}

		ctsGrandParent.resetOriginalValues();

		return ctsGrandParent;
	}

	/**
	 * Returns the cts grand parent with the primary key or throws a <code>NoSuchCTSGrandParentException</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent findByPrimaryKey(long ctsGrandParentId)
		throws NoSuchCTSGrandParentException {

		return findByPrimaryKey((Serializable)ctsGrandParentId);
	}

	/**
	 * Returns the cts grand parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent, or <code>null</code> if a cts grand parent with the primary key could not be found
	 */
	@Override
	public CTSGrandParent fetchByPrimaryKey(long ctsGrandParentId) {
		return fetchByPrimaryKey((Serializable)ctsGrandParentId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctsGrandParentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSGRANDPARENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CTSGrandParentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cts grand parent persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_CTSGRANDPARENT_WHERE,
				_SQL_COUNT_CTSGRANDPARENT_WHERE,
				CTSGrandParentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ctsGrandParent.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CTSGrandParent::getCompanyId));

		CTSGrandParentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSGrandParentUtil.setPersistence(null);

		entityCache.removeCache(CTSGrandParentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CTSGrandParentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTSGRANDPARENT =
		"SELECT ctsGrandParent FROM CTSGrandParent ctsGrandParent";

	private static final String _SQL_SELECT_CTSGRANDPARENT_WHERE =
		"SELECT ctsGrandParent FROM CTSGrandParent ctsGrandParent WHERE ";

	private static final String _SQL_COUNT_CTSGRANDPARENT_WHERE =
		"SELECT COUNT(ctsGrandParent) FROM CTSGrandParent ctsGrandParent WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1919650936