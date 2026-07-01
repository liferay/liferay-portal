/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl;

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
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchUserException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUserTable;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectUserPersistence;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectUserUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl.constants.OpenIdConnectPersistenceConstants;

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
 * The persistence implementation for the open ID connect user service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @generated
 */
@Component(service = OpenIdConnectUserPersistence.class)
public class OpenIdConnectUserPersistenceImpl
	extends BasePersistenceImpl<OpenIdConnectUser, NoSuchUserException>
	implements OpenIdConnectUserPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OpenIdConnectUserUtil</code> to access the open ID connect user persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OpenIdConnectUserImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<OpenIdConnectUser, NoSuchUserException>
		_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect users
	 */
	@Override
	public List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser findByC_U_First(
			long companyId, long userId,
			OrderByComparator<OpenIdConnectUser> orderByComparator)
		throws NoSuchUserException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Removes all the open ID connect users where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching open ID connect users
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	private UniquePersistenceFinder<OpenIdConnectUser, NoSuchUserException>
		_uniquePersistenceFinderByC_I_S;

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser findByC_I_S(
			long companyId, String issuer, String subject)
		throws NoSuchUserException {

		return _uniquePersistenceFinderByC_I_S.find(
			finderCache, new Object[] {companyId, issuer, subject});
	}

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByC_I_S(
		long companyId, String issuer, String subject, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_I_S.fetch(
			finderCache, new Object[] {companyId, issuer, subject},
			useFinderCache);
	}

	/**
	 * Removes the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the open ID connect user that was removed
	 */
	@Override
	public OpenIdConnectUser removeByC_I_S(
			long companyId, String issuer, String subject)
		throws NoSuchUserException {

		OpenIdConnectUser openIdConnectUser = findByC_I_S(
			companyId, issuer, subject);

		return remove(openIdConnectUser);
	}

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and issuer = &#63; and subject = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the number of matching open ID connect users
	 */
	@Override
	public int countByC_I_S(long companyId, String issuer, String subject) {
		return _uniquePersistenceFinderByC_I_S.count(
			finderCache, new Object[] {companyId, issuer, subject});
	}

	public OpenIdConnectUserPersistenceImpl() {
		setModelClass(OpenIdConnectUser.class);

		setModelImplClass(OpenIdConnectUserImpl.class);
		setModelPKClass(long.class);

		setTable(OpenIdConnectUserTable.INSTANCE);
	}

	/**
	 * Creates a new open ID connect user with the primary key. Does not add the open ID connect user to the database.
	 *
	 * @param openIdConnectUserId the primary key for the new open ID connect user
	 * @return the new open ID connect user
	 */
	@Override
	public OpenIdConnectUser create(long openIdConnectUserId) {
		OpenIdConnectUser openIdConnectUser = new OpenIdConnectUserImpl();

		openIdConnectUser.setNew(true);
		openIdConnectUser.setPrimaryKey(openIdConnectUserId);

		openIdConnectUser.setCompanyId(CompanyThreadLocal.getCompanyId());

		return openIdConnectUser;
	}

	/**
	 * Removes the open ID connect user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user that was removed
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser remove(long openIdConnectUserId)
		throws NoSuchUserException {

		return remove((Serializable)openIdConnectUserId);
	}

	@Override
	protected OpenIdConnectUser removeImpl(
		OpenIdConnectUser openIdConnectUser) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(openIdConnectUser)) {
				openIdConnectUser = (OpenIdConnectUser)session.get(
					OpenIdConnectUserImpl.class,
					openIdConnectUser.getPrimaryKeyObj());
			}

			if (openIdConnectUser != null) {
				session.delete(openIdConnectUser);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (openIdConnectUser != null) {
			clearCache(openIdConnectUser);
		}

		return openIdConnectUser;
	}

	@Override
	public OpenIdConnectUser updateImpl(OpenIdConnectUser openIdConnectUser) {
		boolean isNew = openIdConnectUser.isNew();

		if (!(openIdConnectUser instanceof OpenIdConnectUserModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(openIdConnectUser.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					openIdConnectUser);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in openIdConnectUser proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OpenIdConnectUser implementation " +
					openIdConnectUser.getClass());
		}

		OpenIdConnectUserModelImpl openIdConnectUserModelImpl =
			(OpenIdConnectUserModelImpl)openIdConnectUser;

		if (isNew && (openIdConnectUser.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				openIdConnectUser.setCreateDate(date);
			}
			else {
				openIdConnectUser.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(openIdConnectUser);
			}
			else {
				openIdConnectUser = (OpenIdConnectUser)session.merge(
					openIdConnectUser);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(openIdConnectUser, false);

		if (isNew) {
			openIdConnectUser.setNew(false);
		}

		openIdConnectUser.resetOriginalValues();

		return openIdConnectUser;
	}

	/**
	 * Returns the open ID connect user with the primary key or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser findByPrimaryKey(long openIdConnectUserId)
		throws NoSuchUserException {

		return findByPrimaryKey((Serializable)openIdConnectUserId);
	}

	/**
	 * Returns the open ID connect user with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user, or <code>null</code> if a open ID connect user with the primary key could not be found
	 */
	@Override
	public OpenIdConnectUser fetchByPrimaryKey(long openIdConnectUserId) {
		return fetchByPrimaryKey((Serializable)openIdConnectUserId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "openIdConnectUserId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OPENIDCONNECTUSER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OpenIdConnectUserModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the open ID connect user persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByC_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "userId"}, false),
			_SQL_SELECT_OPENIDCONNECTUSER_WHERE,
			_SQL_COUNT_OPENIDCONNECTUSER_WHERE,
			OpenIdConnectUserModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"openIdConnectUser.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, OpenIdConnectUser::getCompanyId),
			new FinderColumn<>(
				"openIdConnectUser.", "userId", FinderColumn.Type.LONG, "=",
				true, true, OpenIdConnectUser::getUserId));

		_uniquePersistenceFinderByC_I_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_I_S",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "issuer", "subject"}, 0, 6, false,
				OpenIdConnectUser::getCompanyId,
				convertNullFunction(OpenIdConnectUser::getIssuer),
				convertNullFunction(OpenIdConnectUser::getSubject)),
			_SQL_SELECT_OPENIDCONNECTUSER_WHERE, "",
			new FinderColumn<>(
				"openIdConnectUser.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, OpenIdConnectUser::getCompanyId),
			new FinderColumn<>(
				"openIdConnectUser.", "issuer", FinderColumn.Type.STRING, "=",
				true, true, OpenIdConnectUser::getIssuer),
			new FinderColumn<>(
				"openIdConnectUser.", "subject", FinderColumn.Type.STRING, "=",
				true, true, OpenIdConnectUser::getSubject));

		OpenIdConnectUserUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OpenIdConnectUserUtil.setPersistence(null);

		entityCache.removeCache(OpenIdConnectUserImpl.class.getName());
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OpenIdConnectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		OpenIdConnectUserModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OPENIDCONNECTUSER =
		"SELECT openIdConnectUser FROM OpenIdConnectUser openIdConnectUser";

	private static final String _SQL_SELECT_OPENIDCONNECTUSER_WHERE =
		"SELECT openIdConnectUser FROM OpenIdConnectUser openIdConnectUser WHERE ";

	private static final String _SQL_COUNT_OPENIDCONNECTUSER_WHERE =
		"SELECT COUNT(openIdConnectUser) FROM OpenIdConnectUser openIdConnectUser WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OpenIdConnectUser exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectUserPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1784818486