/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.persistence.impl;

import com.liferay.commerce.order.rule.exception.NoSuchCOREntryRelException;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.model.COREntryRelTable;
import com.liferay.commerce.order.rule.model.impl.COREntryRelImpl;
import com.liferay.commerce.order.rule.model.impl.COREntryRelModelImpl;
import com.liferay.commerce.order.rule.service.persistence.COREntryRelPersistence;
import com.liferay.commerce.order.rule.service.persistence.COREntryRelUtil;
import com.liferay.commerce.order.rule.service.persistence.impl.constants.CORPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cor entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = COREntryRelPersistence.class)
public class COREntryRelPersistenceImpl
	extends BasePersistenceImpl<COREntryRel, NoSuchCOREntryRelException>
	implements COREntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>COREntryRelUtil</code> to access the cor entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		COREntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<COREntryRel, NoSuchCOREntryRelException>
		_collectionPersistenceFinderByCOREntryId;

	/**
	 * Returns an ordered range of all the cor entry rels where COREntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param COREntryId the cor entry ID
	 * @param start the lower bound of the range of cor entry rels
	 * @param end the upper bound of the range of cor entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entry rels
	 */
	@Override
	public List<COREntryRel> findByCOREntryId(
		long COREntryId, int start, int end,
		OrderByComparator<COREntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCOREntryId.find(
			finderCache, new Object[] {COREntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry rel in the ordered set where COREntryId = &#63;.
	 *
	 * @param COREntryId the cor entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry rel
	 * @throws NoSuchCOREntryRelException if a matching cor entry rel could not be found
	 */
	@Override
	public COREntryRel findByCOREntryId_First(
			long COREntryId, OrderByComparator<COREntryRel> orderByComparator)
		throws NoSuchCOREntryRelException {

		return _collectionPersistenceFinderByCOREntryId.findFirst(
			finderCache, new Object[] {COREntryId}, orderByComparator);
	}

	/**
	 * Returns the first cor entry rel in the ordered set where COREntryId = &#63;.
	 *
	 * @param COREntryId the cor entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry rel, or <code>null</code> if a matching cor entry rel could not be found
	 */
	@Override
	public COREntryRel fetchByCOREntryId_First(
		long COREntryId, OrderByComparator<COREntryRel> orderByComparator) {

		return _collectionPersistenceFinderByCOREntryId.fetchFirst(
			finderCache, new Object[] {COREntryId}, orderByComparator);
	}

	/**
	 * Removes all the cor entry rels where COREntryId = &#63; from the database.
	 *
	 * @param COREntryId the cor entry ID
	 */
	@Override
	public void removeByCOREntryId(long COREntryId) {
		_collectionPersistenceFinderByCOREntryId.remove(
			finderCache, new Object[] {COREntryId});
	}

	/**
	 * Returns the number of cor entry rels where COREntryId = &#63;.
	 *
	 * @param COREntryId the cor entry ID
	 * @return the number of matching cor entry rels
	 */
	@Override
	public int countByCOREntryId(long COREntryId) {
		return _collectionPersistenceFinderByCOREntryId.count(
			finderCache, new Object[] {COREntryId});
	}

	private CollectionPersistenceFinder<COREntryRel, NoSuchCOREntryRelException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the cor entry rels where classNameId = &#63; and COREntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>COREntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param COREntryId the cor entry ID
	 * @param start the lower bound of the range of cor entry rels
	 * @param end the upper bound of the range of cor entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cor entry rels
	 */
	@Override
	public List<COREntryRel> findByC_C(
		long classNameId, long COREntryId, int start, int end,
		OrderByComparator<COREntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, COREntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cor entry rel in the ordered set where classNameId = &#63; and COREntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param COREntryId the cor entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry rel
	 * @throws NoSuchCOREntryRelException if a matching cor entry rel could not be found
	 */
	@Override
	public COREntryRel findByC_C_First(
			long classNameId, long COREntryId,
			OrderByComparator<COREntryRel> orderByComparator)
		throws NoSuchCOREntryRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, COREntryId},
			orderByComparator);
	}

	/**
	 * Returns the first cor entry rel in the ordered set where classNameId = &#63; and COREntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param COREntryId the cor entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cor entry rel, or <code>null</code> if a matching cor entry rel could not be found
	 */
	@Override
	public COREntryRel fetchByC_C_First(
		long classNameId, long COREntryId,
		OrderByComparator<COREntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, COREntryId},
			orderByComparator);
	}

	/**
	 * Removes all the cor entry rels where classNameId = &#63; and COREntryId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param COREntryId the cor entry ID
	 */
	@Override
	public void removeByC_C(long classNameId, long COREntryId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, COREntryId});
	}

	/**
	 * Returns the number of cor entry rels where classNameId = &#63; and COREntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param COREntryId the cor entry ID
	 * @return the number of matching cor entry rels
	 */
	@Override
	public int countByC_C(long classNameId, long COREntryId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, COREntryId});
	}

	private UniquePersistenceFinder<COREntryRel, NoSuchCOREntryRelException>
		_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the cor entry rel where classNameId = &#63; and classPK = &#63; and COREntryId = &#63; or throws a <code>NoSuchCOREntryRelException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param COREntryId the cor entry ID
	 * @return the matching cor entry rel
	 * @throws NoSuchCOREntryRelException if a matching cor entry rel could not be found
	 */
	@Override
	public COREntryRel findByC_C_C(
			long classNameId, long classPK, long COREntryId)
		throws NoSuchCOREntryRelException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache, new Object[] {classNameId, classPK, COREntryId});
	}

	/**
	 * Returns the cor entry rel where classNameId = &#63; and classPK = &#63; and COREntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param COREntryId the cor entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cor entry rel, or <code>null</code> if a matching cor entry rel could not be found
	 */
	@Override
	public COREntryRel fetchByC_C_C(
		long classNameId, long classPK, long COREntryId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache, new Object[] {classNameId, classPK, COREntryId},
			useFinderCache);
	}

	/**
	 * Removes the cor entry rel where classNameId = &#63; and classPK = &#63; and COREntryId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param COREntryId the cor entry ID
	 * @return the cor entry rel that was removed
	 */
	@Override
	public COREntryRel removeByC_C_C(
			long classNameId, long classPK, long COREntryId)
		throws NoSuchCOREntryRelException {

		COREntryRel corEntryRel = findByC_C_C(classNameId, classPK, COREntryId);

		return remove(corEntryRel);
	}

	/**
	 * Returns the number of cor entry rels where classNameId = &#63; and classPK = &#63; and COREntryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param COREntryId the cor entry ID
	 * @return the number of matching cor entry rels
	 */
	@Override
	public int countByC_C_C(long classNameId, long classPK, long COREntryId) {
		return _uniquePersistenceFinderByC_C_C.count(
			finderCache, new Object[] {classNameId, classPK, COREntryId});
	}

	public COREntryRelPersistenceImpl() {
		setModelClass(COREntryRel.class);

		setModelImplClass(COREntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(COREntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new cor entry rel with the primary key. Does not add the cor entry rel to the database.
	 *
	 * @param COREntryRelId the primary key for the new cor entry rel
	 * @return the new cor entry rel
	 */
	@Override
	public COREntryRel create(long COREntryRelId) {
		COREntryRel corEntryRel = new COREntryRelImpl();

		corEntryRel.setNew(true);
		corEntryRel.setPrimaryKey(COREntryRelId);

		corEntryRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return corEntryRel;
	}

	/**
	 * Removes the cor entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param COREntryRelId the primary key of the cor entry rel
	 * @return the cor entry rel that was removed
	 * @throws NoSuchCOREntryRelException if a cor entry rel with the primary key could not be found
	 */
	@Override
	public COREntryRel remove(long COREntryRelId)
		throws NoSuchCOREntryRelException {

		return remove((Serializable)COREntryRelId);
	}

	@Override
	protected COREntryRel removeImpl(COREntryRel corEntryRel) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(corEntryRel)) {
				corEntryRel = (COREntryRel)session.get(
					COREntryRelImpl.class, corEntryRel.getPrimaryKeyObj());
			}

			if (corEntryRel != null) {
				session.delete(corEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (corEntryRel != null) {
			clearCache(corEntryRel);
		}

		return corEntryRel;
	}

	@Override
	public COREntryRel updateImpl(COREntryRel corEntryRel) {
		boolean isNew = corEntryRel.isNew();

		if (!(corEntryRel instanceof COREntryRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(corEntryRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(corEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in corEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom COREntryRel implementation " +
					corEntryRel.getClass());
		}

		COREntryRelModelImpl corEntryRelModelImpl =
			(COREntryRelModelImpl)corEntryRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (corEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				corEntryRel.setCreateDate(date);
			}
			else {
				corEntryRel.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!corEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				corEntryRel.setModifiedDate(date);
			}
			else {
				corEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(corEntryRel);
			}
			else {
				corEntryRel = (COREntryRel)session.merge(corEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(corEntryRel, false);

		if (isNew) {
			corEntryRel.setNew(false);
		}

		corEntryRel.resetOriginalValues();

		return corEntryRel;
	}

	/**
	 * Returns the cor entry rel with the primary key or throws a <code>NoSuchCOREntryRelException</code> if it could not be found.
	 *
	 * @param COREntryRelId the primary key of the cor entry rel
	 * @return the cor entry rel
	 * @throws NoSuchCOREntryRelException if a cor entry rel with the primary key could not be found
	 */
	@Override
	public COREntryRel findByPrimaryKey(long COREntryRelId)
		throws NoSuchCOREntryRelException {

		return findByPrimaryKey((Serializable)COREntryRelId);
	}

	/**
	 * Returns the cor entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param COREntryRelId the primary key of the cor entry rel
	 * @return the cor entry rel, or <code>null</code> if a cor entry rel with the primary key could not be found
	 */
	@Override
	public COREntryRel fetchByPrimaryKey(long COREntryRelId) {
		return fetchByPrimaryKey((Serializable)COREntryRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "COREntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CORENTRYREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return COREntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cor entry rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCOREntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCOREntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"COREntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCOREntryId", new String[] {Long.class.getName()},
					new String[] {"COREntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCOREntryId", new String[] {Long.class.getName()},
					new String[] {"COREntryId"}, false),
				_SQL_SELECT_CORENTRYREL_WHERE, _SQL_COUNT_CORENTRYREL_WHERE,
				COREntryRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"corEntryRel.", "COREntryId", FinderColumn.Type.LONG, "=",
					true, true, COREntryRel::getCOREntryId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "COREntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "COREntryId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "COREntryId"}, false),
			_SQL_SELECT_CORENTRYREL_WHERE, _SQL_COUNT_CORENTRYREL_WHERE,
			COREntryRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"corEntryRel.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, COREntryRel::getClassNameId),
			new FinderColumn<>(
				"corEntryRel.", "COREntryId", FinderColumn.Type.LONG, "=", true,
				true, COREntryRel::getCOREntryId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"classNameId", "classPK", "COREntryId"}, 0, 0,
				false, COREntryRel::getClassNameId, COREntryRel::getClassPK,
				COREntryRel::getCOREntryId),
			_SQL_SELECT_CORENTRYREL_WHERE, "",
			new FinderColumn<>(
				"corEntryRel.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, COREntryRel::getClassNameId),
			new FinderColumn<>(
				"corEntryRel.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, COREntryRel::getClassPK),
			new FinderColumn<>(
				"corEntryRel.", "COREntryId", FinderColumn.Type.LONG, "=", true,
				true, COREntryRel::getCOREntryId));

		COREntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		COREntryRelUtil.setPersistence(null);

		entityCache.removeCache(COREntryRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = CORPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CORPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CORPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		COREntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CORENTRYREL =
		"SELECT corEntryRel FROM COREntryRel corEntryRel";

	private static final String _SQL_SELECT_CORENTRYREL_WHERE =
		"SELECT corEntryRel FROM COREntryRel corEntryRel WHERE ";

	private static final String _SQL_COUNT_CORENTRYREL_WHERE =
		"SELECT COUNT(corEntryRel) FROM COREntryRel corEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No COREntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		COREntryRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1978631317