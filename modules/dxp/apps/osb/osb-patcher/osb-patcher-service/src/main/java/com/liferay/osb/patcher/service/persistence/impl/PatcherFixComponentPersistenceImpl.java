/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherFixComponentException;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixComponentTable;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentUtil;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher fix component service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherFixComponentPersistence.class)
public class PatcherFixComponentPersistenceImpl
	extends BasePersistenceImpl<PatcherFixComponent>
	implements PatcherFixComponentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherFixComponentUtil</code> to access the patcher fix component persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherFixComponentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public PatcherFixComponentPersistenceImpl() {
		setModelClass(PatcherFixComponent.class);

		setModelImplClass(PatcherFixComponentImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherFixComponentTable.INSTANCE);
	}

	/**
	 * Caches the patcher fix component in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponent the patcher fix component
	 */
	@Override
	public void cacheResult(PatcherFixComponent patcherFixComponent) {
		entityCache.putResult(
			PatcherFixComponentImpl.class, patcherFixComponent.getPrimaryKey(),
			patcherFixComponent);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher fix components in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponents the patcher fix components
	 */
	@Override
	public void cacheResult(List<PatcherFixComponent> patcherFixComponents) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherFixComponents.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherFixComponent patcherFixComponent : patcherFixComponents) {
			if (entityCache.getResult(
					PatcherFixComponentImpl.class,
					patcherFixComponent.getPrimaryKey()) == null) {

				cacheResult(patcherFixComponent);
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix components.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherFixComponentImpl.class);

		finderCache.clearCache(PatcherFixComponentImpl.class);
	}

	/**
	 * Clears the cache for the patcher fix component.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixComponent patcherFixComponent) {
		entityCache.removeResult(
			PatcherFixComponentImpl.class, patcherFixComponent);
	}

	@Override
	public void clearCache(List<PatcherFixComponent> patcherFixComponents) {
		for (PatcherFixComponent patcherFixComponent : patcherFixComponents) {
			entityCache.removeResult(
				PatcherFixComponentImpl.class, patcherFixComponent);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherFixComponentImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherFixComponentImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	 *
	 * @param patcherFixComponentId the primary key for the new patcher fix component
	 * @return the new patcher fix component
	 */
	@Override
	public PatcherFixComponent create(long patcherFixComponentId) {
		PatcherFixComponent patcherFixComponent = new PatcherFixComponentImpl();

		patcherFixComponent.setNew(true);
		patcherFixComponent.setPrimaryKey(patcherFixComponentId);

		patcherFixComponent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherFixComponent;
	}

	/**
	 * Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent remove(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException {

		return remove((Serializable)patcherFixComponentId);
	}

	/**
	 * Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent remove(Serializable primaryKey)
		throws NoSuchPatcherFixComponentException {

		Session session = null;

		try {
			session = openSession();

			PatcherFixComponent patcherFixComponent =
				(PatcherFixComponent)session.get(
					PatcherFixComponentImpl.class, primaryKey);

			if (patcherFixComponent == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixComponentException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherFixComponent);
		}
		catch (NoSuchPatcherFixComponentException noSuchEntityException) {
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
	protected PatcherFixComponent removeImpl(
		PatcherFixComponent patcherFixComponent) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixComponent)) {
				patcherFixComponent = (PatcherFixComponent)session.get(
					PatcherFixComponentImpl.class,
					patcherFixComponent.getPrimaryKeyObj());
			}

			if (patcherFixComponent != null) {
				session.delete(patcherFixComponent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixComponent != null) {
			clearCache(patcherFixComponent);
		}

		return patcherFixComponent;
	}

	@Override
	public PatcherFixComponent updateImpl(
		PatcherFixComponent patcherFixComponent) {

		boolean isNew = patcherFixComponent.isNew();

		if (!(patcherFixComponent instanceof PatcherFixComponentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherFixComponent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherFixComponent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherFixComponent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherFixComponent implementation " +
					patcherFixComponent.getClass());
		}

		PatcherFixComponentModelImpl patcherFixComponentModelImpl =
			(PatcherFixComponentModelImpl)patcherFixComponent;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherFixComponent.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherFixComponent.setCreateDate(date);
			}
			else {
				patcherFixComponent.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherFixComponentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherFixComponent.setModifiedDate(date);
			}
			else {
				patcherFixComponent.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherFixComponent);
			}
			else {
				patcherFixComponent = (PatcherFixComponent)session.merge(
					patcherFixComponent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherFixComponentImpl.class, patcherFixComponent, false, true);

		if (isNew) {
			patcherFixComponent.setNew(false);
		}

		patcherFixComponent.resetOriginalValues();

		return patcherFixComponent;
	}

	/**
	 * Returns the patcher fix component with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixComponentException {

		PatcherFixComponent patcherFixComponent = fetchByPrimaryKey(primaryKey);

		if (patcherFixComponent == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixComponentException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherFixComponent;
	}

	/**
	 * Returns the patcher fix component with the primary key or throws a <code>NoSuchPatcherFixComponentException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent findByPrimaryKey(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException {

		return findByPrimaryKey((Serializable)patcherFixComponentId);
	}

	/**
	 * Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	 */
	@Override
	public PatcherFixComponent fetchByPrimaryKey(long patcherFixComponentId) {
		return fetchByPrimaryKey((Serializable)patcherFixComponentId);
	}

	/**
	 * Returns all the patcher fix components.
	 *
	 * @return the patcher fix components
	 */
	@Override
	public List<PatcherFixComponent> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @return the range of patcher fix components
	 */
	@Override
	public List<PatcherFixComponent> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix components
	 */
	@Override
	public List<PatcherFixComponent> findAll(
		int start, int end,
		OrderByComparator<PatcherFixComponent> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix components
	 */
	@Override
	public List<PatcherFixComponent> findAll(
		int start, int end,
		OrderByComparator<PatcherFixComponent> orderByComparator,
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

		List<PatcherFixComponent> list = null;

		if (useFinderCache) {
			list = (List<PatcherFixComponent>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERFIXCOMPONENT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXCOMPONENT;

				sql = sql.concat(PatcherFixComponentModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherFixComponent>)QueryUtil.list(
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
	 * Removes all the patcher fix components from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherFixComponent patcherFixComponent : findAll()) {
			remove(patcherFixComponent);
		}
	}

	/**
	 * Returns the number of patcher fix components.
	 *
	 * @return the number of patcher fix components
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_PATCHERFIXCOMPONENT);

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
		return "patcherFixComponentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERFIXCOMPONENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherFixComponentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher fix component persistence.
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

		PatcherFixComponentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherFixComponentUtil.setPersistence(null);

		entityCache.removeCache(PatcherFixComponentImpl.class.getName());
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

	private static final String _SQL_SELECT_PATCHERFIXCOMPONENT =
		"SELECT patcherFixComponent FROM PatcherFixComponent patcherFixComponent";

	private static final String _SQL_COUNT_PATCHERFIXCOMPONENT =
		"SELECT COUNT(patcherFixComponent) FROM PatcherFixComponent patcherFixComponent";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixComponent.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherFixComponent exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherFixComponentPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}