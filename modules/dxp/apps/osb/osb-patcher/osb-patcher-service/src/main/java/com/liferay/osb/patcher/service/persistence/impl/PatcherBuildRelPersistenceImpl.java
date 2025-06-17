/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherBuildRelException;
import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.osb.patcher.model.PatcherBuildRelTable;
import com.liferay.osb.patcher.model.impl.PatcherBuildRelImpl;
import com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherBuildRelPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherBuildRelUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher build rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherBuildRelPersistence.class)
public class PatcherBuildRelPersistenceImpl
	extends BasePersistenceImpl<PatcherBuildRel>
	implements PatcherBuildRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherBuildRelUtil</code> to access the patcher build rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherBuildRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public PatcherBuildRelPersistenceImpl() {
		setModelClass(PatcherBuildRel.class);

		setModelImplClass(PatcherBuildRelImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherBuildRelTable.INSTANCE);
	}

	/**
	 * Caches the patcher build rel in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRel the patcher build rel
	 */
	@Override
	public void cacheResult(PatcherBuildRel patcherBuildRel) {
		entityCache.putResult(
			PatcherBuildRelImpl.class, patcherBuildRel.getPrimaryKey(),
			patcherBuildRel);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher build rels in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRels the patcher build rels
	 */
	@Override
	public void cacheResult(List<PatcherBuildRel> patcherBuildRels) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherBuildRels.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherBuildRel patcherBuildRel : patcherBuildRels) {
			if (entityCache.getResult(
					PatcherBuildRelImpl.class,
					patcherBuildRel.getPrimaryKey()) == null) {

				cacheResult(patcherBuildRel);
			}
		}
	}

	/**
	 * Clears the cache for all patcher build rels.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherBuildRelImpl.class);

		finderCache.clearCache(PatcherBuildRelImpl.class);
	}

	/**
	 * Clears the cache for the patcher build rel.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherBuildRel patcherBuildRel) {
		entityCache.removeResult(PatcherBuildRelImpl.class, patcherBuildRel);
	}

	@Override
	public void clearCache(List<PatcherBuildRel> patcherBuildRels) {
		for (PatcherBuildRel patcherBuildRel : patcherBuildRels) {
			entityCache.removeResult(
				PatcherBuildRelImpl.class, patcherBuildRel);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherBuildRelImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherBuildRelImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	 *
	 * @param patcherBuildRelId the primary key for the new patcher build rel
	 * @return the new patcher build rel
	 */
	@Override
	public PatcherBuildRel create(long patcherBuildRelId) {
		PatcherBuildRel patcherBuildRel = new PatcherBuildRelImpl();

		patcherBuildRel.setNew(true);
		patcherBuildRel.setPrimaryKey(patcherBuildRelId);

		patcherBuildRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherBuildRel;
	}

	/**
	 * Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel remove(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException {

		return remove((Serializable)patcherBuildRelId);
	}

	/**
	 * Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel remove(Serializable primaryKey)
		throws NoSuchPatcherBuildRelException {

		Session session = null;

		try {
			session = openSession();

			PatcherBuildRel patcherBuildRel = (PatcherBuildRel)session.get(
				PatcherBuildRelImpl.class, primaryKey);

			if (patcherBuildRel == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherBuildRelException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherBuildRel);
		}
		catch (NoSuchPatcherBuildRelException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected PatcherBuildRel removeImpl(PatcherBuildRel patcherBuildRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherBuildRel)) {
				patcherBuildRel = (PatcherBuildRel)session.get(
					PatcherBuildRelImpl.class,
					patcherBuildRel.getPrimaryKeyObj());
			}

			if (patcherBuildRel != null) {
				session.delete(patcherBuildRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherBuildRel != null) {
			clearCache(patcherBuildRel);
		}

		return patcherBuildRel;
	}

	@Override
	public PatcherBuildRel updateImpl(PatcherBuildRel patcherBuildRel) {
		boolean isNew = patcherBuildRel.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherBuildRel);
			}
			else {
				patcherBuildRel = (PatcherBuildRel)session.merge(
					patcherBuildRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherBuildRelImpl.class, patcherBuildRel, false, true);

		if (isNew) {
			patcherBuildRel.setNew(false);
		}

		patcherBuildRel.resetOriginalValues();

		return patcherBuildRel;
	}

	/**
	 * Returns the patcher build rel with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherBuildRelException {

		PatcherBuildRel patcherBuildRel = fetchByPrimaryKey(primaryKey);

		if (patcherBuildRel == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherBuildRelException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherBuildRel;
	}

	/**
	 * Returns the patcher build rel with the primary key or throws a <code>NoSuchPatcherBuildRelException</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel findByPrimaryKey(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException {

		return findByPrimaryKey((Serializable)patcherBuildRelId);
	}

	/**
	 * Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	 */
	@Override
	public PatcherBuildRel fetchByPrimaryKey(long patcherBuildRelId) {
		return fetchByPrimaryKey((Serializable)patcherBuildRelId);
	}

	/**
	 * Returns all the patcher build rels.
	 *
	 * @return the patcher build rels
	 */
	@Override
	public List<PatcherBuildRel> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @return the range of patcher build rels
	 */
	@Override
	public List<PatcherBuildRel> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher build rels
	 */
	@Override
	public List<PatcherBuildRel> findAll(
		int start, int end,
		OrderByComparator<PatcherBuildRel> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher build rels
	 */
	@Override
	public List<PatcherBuildRel> findAll(
		int start, int end,
		OrderByComparator<PatcherBuildRel> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<PatcherBuildRel> list = null;

		if (useFinderCache) {
			list = (List<PatcherBuildRel>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERBUILDREL);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERBUILDREL;

				sql = sql.concat(PatcherBuildRelModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherBuildRel>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the patcher build rels from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherBuildRel patcherBuildRel : findAll()) {
			remove(patcherBuildRel);
		}
	}

	/**
	 * Returns the number of patcher build rels.
	 *
	 * @return the number of patcher build rels
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERBUILDREL);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherBuildRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERBUILDREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherBuildRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher build rel persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		PatcherBuildRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherBuildRelUtil.setPersistence(null);

		entityCache.removeCache(PatcherBuildRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_PATCHERBUILDREL =
		"SELECT patcherBuildRel FROM PatcherBuildRel patcherBuildRel";

	private static final String _SQL_COUNT_PATCHERBUILDREL =
		"SELECT COUNT(patcherBuildRel) FROM PatcherBuildRel patcherBuildRel";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherBuildRel.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherBuildRel exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherBuildRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}