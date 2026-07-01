/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service.persistence.impl;

import com.liferay.cookies.exception.NoSuchCookiesConsentPreferenceException;
import com.liferay.cookies.model.CookiesConsentPreference;
import com.liferay.cookies.model.CookiesConsentPreferenceTable;
import com.liferay.cookies.model.impl.CookiesConsentPreferenceImpl;
import com.liferay.cookies.model.impl.CookiesConsentPreferenceModelImpl;
import com.liferay.cookies.service.persistence.CookiesConsentPreferencePersistence;
import com.liferay.cookies.service.persistence.CookiesConsentPreferenceUtil;
import com.liferay.cookies.service.persistence.impl.constants.CookiesPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
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
 * The persistence implementation for the cookies consent preference service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Christopher Kian
 * @generated
 */
@Component(service = CookiesConsentPreferencePersistence.class)
public class CookiesConsentPreferencePersistenceImpl
	extends BasePersistenceImpl
		<CookiesConsentPreference, NoSuchCookiesConsentPreferenceException>
	implements CookiesConsentPreferencePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CookiesConsentPreferenceUtil</code> to access the cookies consent preference persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CookiesConsentPreferenceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CookiesConsentPreference, NoSuchCookiesConsentPreferenceException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByUserId_First(
			long userId,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByUserId_First(
		long userId,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the cookies consent preferences where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<CookiesConsentPreference, NoSuchCookiesConsentPreferenceException>
			_collectionPersistenceFinderByExpirationDate;

	/**
	 * Returns an ordered range of all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByExpirationDate.find(
			finderCache, new Object[] {expirationDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByExpirationDate_First(
			Date expirationDate,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException {

		return _collectionPersistenceFinderByExpirationDate.findFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByExpirationDate_First(
		Date expirationDate,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return _collectionPersistenceFinderByExpirationDate.fetchFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Removes all the cookies consent preferences where expirationDate = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByExpirationDate(Date expirationDate) {
		_collectionPersistenceFinderByExpirationDate.remove(
			finderCache, new Object[] {expirationDate});
	}

	/**
	 * Returns the number of cookies consent preferences where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByExpirationDate(Date expirationDate) {
		return _collectionPersistenceFinderByExpirationDate.count(
			finderCache, new Object[] {expirationDate});
	}

	private CollectionPersistenceFinder
		<CookiesConsentPreference, NoSuchCookiesConsentPreferenceException>
			_collectionPersistenceFinderByU_D;

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	@Override
	public List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_D.find(
			finderCache, new Object[] {userId, domain}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByU_D_First(
			long userId, String domain,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException {

		return _collectionPersistenceFinderByU_D.findFirst(
			finderCache, new Object[] {userId, domain}, orderByComparator);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByU_D_First(
		long userId, String domain,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return _collectionPersistenceFinderByU_D.fetchFirst(
			finderCache, new Object[] {userId, domain}, orderByComparator);
	}

	/**
	 * Removes all the cookies consent preferences where userId = &#63; and domain = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 */
	@Override
	public void removeByU_D(long userId, String domain) {
		_collectionPersistenceFinderByU_D.remove(
			finderCache, new Object[] {userId, domain});
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByU_D(long userId, String domain) {
		return _collectionPersistenceFinderByU_D.count(
			finderCache, new Object[] {userId, domain});
	}

	private UniquePersistenceFinder
		<CookiesConsentPreference, NoSuchCookiesConsentPreferenceException>
			_uniquePersistenceFinderByU_D_N;

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference findByU_D_N(
			long userId, String domain, String name)
		throws NoSuchCookiesConsentPreferenceException {

		return _uniquePersistenceFinderByU_D_N.find(
			finderCache, new Object[] {userId, domain, name});
	}

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByU_D_N(
		long userId, String domain, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_D_N.fetch(
			finderCache, new Object[] {userId, domain, name}, useFinderCache);
	}

	/**
	 * Removes the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the cookies consent preference that was removed
	 */
	@Override
	public CookiesConsentPreference removeByU_D_N(
			long userId, String domain, String name)
		throws NoSuchCookiesConsentPreferenceException {

		CookiesConsentPreference cookiesConsentPreference = findByU_D_N(
			userId, domain, name);

		return remove(cookiesConsentPreference);
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63; and name = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the number of matching cookies consent preferences
	 */
	@Override
	public int countByU_D_N(long userId, String domain, String name) {
		return _uniquePersistenceFinderByU_D_N.count(
			finderCache, new Object[] {userId, domain, name});
	}

	public CookiesConsentPreferencePersistenceImpl() {
		setModelClass(CookiesConsentPreference.class);

		setModelImplClass(CookiesConsentPreferenceImpl.class);
		setModelPKClass(long.class);

		setTable(CookiesConsentPreferenceTable.INSTANCE);
	}

	/**
	 * Creates a new cookies consent preference with the primary key. Does not add the cookies consent preference to the database.
	 *
	 * @param cookiesConsentPreferenceId the primary key for the new cookies consent preference
	 * @return the new cookies consent preference
	 */
	@Override
	public CookiesConsentPreference create(long cookiesConsentPreferenceId) {
		CookiesConsentPreference cookiesConsentPreference =
			new CookiesConsentPreferenceImpl();

		cookiesConsentPreference.setNew(true);
		cookiesConsentPreference.setPrimaryKey(cookiesConsentPreferenceId);

		cookiesConsentPreference.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cookiesConsentPreference;
	}

	/**
	 * Removes the cookies consent preference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference that was removed
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference remove(long cookiesConsentPreferenceId)
		throws NoSuchCookiesConsentPreferenceException {

		return remove((Serializable)cookiesConsentPreferenceId);
	}

	@Override
	protected CookiesConsentPreference removeImpl(
		CookiesConsentPreference cookiesConsentPreference) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cookiesConsentPreference)) {
				cookiesConsentPreference =
					(CookiesConsentPreference)session.get(
						CookiesConsentPreferenceImpl.class,
						cookiesConsentPreference.getPrimaryKeyObj());
			}

			if (cookiesConsentPreference != null) {
				session.delete(cookiesConsentPreference);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cookiesConsentPreference != null) {
			clearCache(cookiesConsentPreference);
		}

		return cookiesConsentPreference;
	}

	@Override
	public CookiesConsentPreference updateImpl(
		CookiesConsentPreference cookiesConsentPreference) {

		boolean isNew = cookiesConsentPreference.isNew();

		if (!(cookiesConsentPreference instanceof
				CookiesConsentPreferenceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cookiesConsentPreference.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cookiesConsentPreference);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cookiesConsentPreference proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CookiesConsentPreference implementation " +
					cookiesConsentPreference.getClass());
		}

		CookiesConsentPreferenceModelImpl cookiesConsentPreferenceModelImpl =
			(CookiesConsentPreferenceModelImpl)cookiesConsentPreference;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cookiesConsentPreference);
			}
			else {
				cookiesConsentPreference =
					(CookiesConsentPreference)session.merge(
						cookiesConsentPreference);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cookiesConsentPreference, false);

		if (isNew) {
			cookiesConsentPreference.setNew(false);
		}

		cookiesConsentPreference.resetOriginalValues();

		return cookiesConsentPreference;
	}

	/**
	 * Returns the cookies consent preference with the primary key or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference findByPrimaryKey(
			long cookiesConsentPreferenceId)
		throws NoSuchCookiesConsentPreferenceException {

		return findByPrimaryKey((Serializable)cookiesConsentPreferenceId);
	}

	/**
	 * Returns the cookies consent preference with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference, or <code>null</code> if a cookies consent preference with the primary key could not be found
	 */
	@Override
	public CookiesConsentPreference fetchByPrimaryKey(
		long cookiesConsentPreferenceId) {

		return fetchByPrimaryKey((Serializable)cookiesConsentPreferenceId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cookiesConsentPreferenceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COOKIESCONSENTPREFERENCE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CookiesConsentPreferenceModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cookies consent preference persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE,
				_SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE,
				CookiesConsentPreferenceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cookiesConsentPreference.", "userId",
					FinderColumn.Type.LONG, "=", true, true,
					CookiesConsentPreference::getUserId));

		_collectionPersistenceFinderByExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByExpirationDate", new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, false),
				_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE,
				_SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE,
				CookiesConsentPreferenceModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cookiesConsentPreference.", "expirationDate",
					FinderColumn.Type.DATE, "=", true, true,
					CookiesConsentPreference::getExpirationDate));

		_collectionPersistenceFinderByU_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_D",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "domain"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_D",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "domain"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_D",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "domain"}, 0, 2, false, null),
			_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE,
			_SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE,
			CookiesConsentPreferenceModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"cookiesConsentPreference.", "userId", FinderColumn.Type.LONG,
				"=", true, true, CookiesConsentPreference::getUserId),
			new FinderColumn<>(
				"cookiesConsentPreference.", "domain", FinderColumn.Type.STRING,
				"=", true, true, CookiesConsentPreference::getDomain));

		_uniquePersistenceFinderByU_D_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_D_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"userId", "domain", "name"}, 0, 6, false,
				CookiesConsentPreference::getUserId,
				convertNullFunction(CookiesConsentPreference::getDomain),
				convertNullFunction(CookiesConsentPreference::getName)),
			_SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE, "",
			new FinderColumn<>(
				"cookiesConsentPreference.", "userId", FinderColumn.Type.LONG,
				"=", true, true, CookiesConsentPreference::getUserId),
			new FinderColumn<>(
				"cookiesConsentPreference.", "domain", FinderColumn.Type.STRING,
				"=", true, true, CookiesConsentPreference::getDomain),
			new FinderColumn<>(
				"cookiesConsentPreference.", "name", FinderColumn.Type.STRING,
				"=", true, true, CookiesConsentPreference::getName));

		CookiesConsentPreferenceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CookiesConsentPreferenceUtil.setPersistence(null);

		entityCache.removeCache(CookiesConsentPreferenceImpl.class.getName());
	}

	@Override
	@Reference(
		target = CookiesPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CookiesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CookiesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CookiesConsentPreferenceModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COOKIESCONSENTPREFERENCE =
		"SELECT cookiesConsentPreference FROM CookiesConsentPreference cookiesConsentPreference";

	private static final String _SQL_SELECT_COOKIESCONSENTPREFERENCE_WHERE =
		"SELECT cookiesConsentPreference FROM CookiesConsentPreference cookiesConsentPreference WHERE ";

	private static final String _SQL_COUNT_COOKIESCONSENTPREFERENCE_WHERE =
		"SELECT COUNT(cookiesConsentPreference) FROM CookiesConsentPreference cookiesConsentPreference WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CookiesConsentPreference exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesConsentPreferencePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1873963288