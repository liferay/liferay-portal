/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.exception.NoSuchFaroProjectEmailDomainException;
import com.liferay.osb.faro.model.FaroProjectEmailDomain;
import com.liferay.osb.faro.model.FaroProjectEmailDomainTable;
import com.liferay.osb.faro.model.impl.FaroProjectEmailDomainImpl;
import com.liferay.osb.faro.model.impl.FaroProjectEmailDomainModelImpl;
import com.liferay.osb.faro.service.persistence.FaroProjectEmailDomainPersistence;
import com.liferay.osb.faro.service.persistence.FaroProjectEmailDomainUtil;
import com.liferay.osb.faro.service.persistence.impl.constants.OSBFaroPersistenceConstants;
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
 * The persistence implementation for the faro project email domain service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @generated
 */
@Component(service = FaroProjectEmailDomainPersistence.class)
public class FaroProjectEmailDomainPersistenceImpl
	extends BasePersistenceImpl
		<FaroProjectEmailDomain, NoSuchFaroProjectEmailDomainException>
	implements FaroProjectEmailDomainPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FaroProjectEmailDomainUtil</code> to access the faro project email domain persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FaroProjectEmailDomainImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FaroProjectEmailDomain, NoSuchFaroProjectEmailDomainException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the faro project email domains where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro project email domains
	 */
	@Override
	public List<FaroProjectEmailDomain> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a matching faro project email domain could not be found
	 */
	@Override
	public FaroProjectEmailDomain findByGroupId_First(
			long groupId,
			OrderByComparator<FaroProjectEmailDomain> orderByComparator)
		throws NoSuchFaroProjectEmailDomainException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain, or <code>null</code> if a matching faro project email domain could not be found
	 */
	@Override
	public FaroProjectEmailDomain fetchByGroupId_First(
		long groupId,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the faro project email domains where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of faro project email domains where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro project email domains
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<FaroProjectEmailDomain, NoSuchFaroProjectEmailDomainException>
			_collectionPersistenceFinderByFaroProjectId;

	/**
	 * Returns an ordered range of all the faro project email domains where faroProjectId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectEmailDomainModelImpl</code>.
	 * </p>
	 *
	 * @param faroProjectId the faro project ID
	 * @param start the lower bound of the range of faro project email domains
	 * @param end the upper bound of the range of faro project email domains (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro project email domains
	 */
	@Override
	public List<FaroProjectEmailDomain> findByFaroProjectId(
		long faroProjectId, int start, int end,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFaroProjectId.find(
			finderCache, new Object[] {faroProjectId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a matching faro project email domain could not be found
	 */
	@Override
	public FaroProjectEmailDomain findByFaroProjectId_First(
			long faroProjectId,
			OrderByComparator<FaroProjectEmailDomain> orderByComparator)
		throws NoSuchFaroProjectEmailDomainException {

		return _collectionPersistenceFinderByFaroProjectId.findFirst(
			finderCache, new Object[] {faroProjectId}, orderByComparator);
	}

	/**
	 * Returns the first faro project email domain in the ordered set where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro project email domain, or <code>null</code> if a matching faro project email domain could not be found
	 */
	@Override
	public FaroProjectEmailDomain fetchByFaroProjectId_First(
		long faroProjectId,
		OrderByComparator<FaroProjectEmailDomain> orderByComparator) {

		return _collectionPersistenceFinderByFaroProjectId.fetchFirst(
			finderCache, new Object[] {faroProjectId}, orderByComparator);
	}

	/**
	 * Removes all the faro project email domains where faroProjectId = &#63; from the database.
	 *
	 * @param faroProjectId the faro project ID
	 */
	@Override
	public void removeByFaroProjectId(long faroProjectId) {
		_collectionPersistenceFinderByFaroProjectId.remove(
			finderCache, new Object[] {faroProjectId});
	}

	/**
	 * Returns the number of faro project email domains where faroProjectId = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @return the number of matching faro project email domains
	 */
	@Override
	public int countByFaroProjectId(long faroProjectId) {
		return _collectionPersistenceFinderByFaroProjectId.count(
			finderCache, new Object[] {faroProjectId});
	}

	public FaroProjectEmailDomainPersistenceImpl() {
		setModelClass(FaroProjectEmailDomain.class);

		setModelImplClass(FaroProjectEmailDomainImpl.class);
		setModelPKClass(long.class);

		setTable(FaroProjectEmailDomainTable.INSTANCE);
	}

	/**
	 * Creates a new faro project email domain with the primary key. Does not add the faro project email domain to the database.
	 *
	 * @param faroProjectEmailDomainId the primary key for the new faro project email domain
	 * @return the new faro project email domain
	 */
	@Override
	public FaroProjectEmailDomain create(long faroProjectEmailDomainId) {
		FaroProjectEmailDomain faroProjectEmailDomain =
			new FaroProjectEmailDomainImpl();

		faroProjectEmailDomain.setNew(true);
		faroProjectEmailDomain.setPrimaryKey(faroProjectEmailDomainId);

		faroProjectEmailDomain.setCompanyId(CompanyThreadLocal.getCompanyId());

		return faroProjectEmailDomain;
	}

	/**
	 * Removes the faro project email domain with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain that was removed
	 * @throws NoSuchFaroProjectEmailDomainException if a faro project email domain with the primary key could not be found
	 */
	@Override
	public FaroProjectEmailDomain remove(long faroProjectEmailDomainId)
		throws NoSuchFaroProjectEmailDomainException {

		return remove((Serializable)faroProjectEmailDomainId);
	}

	@Override
	protected FaroProjectEmailDomain removeImpl(
		FaroProjectEmailDomain faroProjectEmailDomain) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(faroProjectEmailDomain)) {
				faroProjectEmailDomain = (FaroProjectEmailDomain)session.get(
					FaroProjectEmailDomainImpl.class,
					faroProjectEmailDomain.getPrimaryKeyObj());
			}

			if (faroProjectEmailDomain != null) {
				session.delete(faroProjectEmailDomain);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (faroProjectEmailDomain != null) {
			clearCache(faroProjectEmailDomain);
		}

		return faroProjectEmailDomain;
	}

	@Override
	public FaroProjectEmailDomain updateImpl(
		FaroProjectEmailDomain faroProjectEmailDomain) {

		boolean isNew = faroProjectEmailDomain.isNew();

		if (!(faroProjectEmailDomain instanceof
				FaroProjectEmailDomainModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(faroProjectEmailDomain.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					faroProjectEmailDomain);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in faroProjectEmailDomain proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FaroProjectEmailDomain implementation " +
					faroProjectEmailDomain.getClass());
		}

		FaroProjectEmailDomainModelImpl faroProjectEmailDomainModelImpl =
			(FaroProjectEmailDomainModelImpl)faroProjectEmailDomain;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(faroProjectEmailDomain);
			}
			else {
				faroProjectEmailDomain = (FaroProjectEmailDomain)session.merge(
					faroProjectEmailDomain);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(faroProjectEmailDomain, false);

		if (isNew) {
			faroProjectEmailDomain.setNew(false);
		}

		faroProjectEmailDomain.resetOriginalValues();

		return faroProjectEmailDomain;
	}

	/**
	 * Returns the faro project email domain with the primary key or throws a <code>NoSuchFaroProjectEmailDomainException</code> if it could not be found.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain
	 * @throws NoSuchFaroProjectEmailDomainException if a faro project email domain with the primary key could not be found
	 */
	@Override
	public FaroProjectEmailDomain findByPrimaryKey(
			long faroProjectEmailDomainId)
		throws NoSuchFaroProjectEmailDomainException {

		return findByPrimaryKey((Serializable)faroProjectEmailDomainId);
	}

	/**
	 * Returns the faro project email domain with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectEmailDomainId the primary key of the faro project email domain
	 * @return the faro project email domain, or <code>null</code> if a faro project email domain with the primary key could not be found
	 */
	@Override
	public FaroProjectEmailDomain fetchByPrimaryKey(
		long faroProjectEmailDomainId) {

		return fetchByPrimaryKey((Serializable)faroProjectEmailDomainId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "faroProjectEmailDomainId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FAROPROJECTEMAILDOMAIN;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FaroProjectEmailDomainModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the faro project email domain persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_FAROPROJECTEMAILDOMAIN_WHERE,
				_SQL_COUNT_FAROPROJECTEMAILDOMAIN_WHERE,
				FaroProjectEmailDomainModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"faroProjectEmailDomain.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					FaroProjectEmailDomain::getGroupId));

		_collectionPersistenceFinderByFaroProjectId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFaroProjectId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"faroProjectId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFaroProjectId", new String[] {Long.class.getName()},
					new String[] {"faroProjectId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFaroProjectId", new String[] {Long.class.getName()},
					new String[] {"faroProjectId"}, false),
				_SQL_SELECT_FAROPROJECTEMAILDOMAIN_WHERE,
				_SQL_COUNT_FAROPROJECTEMAILDOMAIN_WHERE,
				FaroProjectEmailDomainModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"faroProjectEmailDomain.", "faroProjectId",
					FinderColumn.Type.LONG, "=", true, true,
					FaroProjectEmailDomain::getFaroProjectId));

		FaroProjectEmailDomainUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FaroProjectEmailDomainUtil.setPersistence(null);

		entityCache.removeCache(FaroProjectEmailDomainImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		FaroProjectEmailDomainModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FAROPROJECTEMAILDOMAIN =
		"SELECT faroProjectEmailDomain FROM FaroProjectEmailDomain faroProjectEmailDomain";

	private static final String _SQL_SELECT_FAROPROJECTEMAILDOMAIN_WHERE =
		"SELECT faroProjectEmailDomain FROM FaroProjectEmailDomain faroProjectEmailDomain WHERE ";

	private static final String _SQL_COUNT_FAROPROJECTEMAILDOMAIN_WHERE =
		"SELECT COUNT(faroProjectEmailDomain) FROM FaroProjectEmailDomain faroProjectEmailDomain WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FaroProjectEmailDomain exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1350428717