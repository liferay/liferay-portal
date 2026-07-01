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
import com.liferay.portal.kernel.exception.NoSuchPasswordPolicyRelException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PasswordPolicyRel;
import com.liferay.portal.kernel.model.PasswordPolicyRelTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.PasswordPolicyRelPersistence;
import com.liferay.portal.kernel.service.persistence.PasswordPolicyRelUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.PasswordPolicyRelImpl;
import com.liferay.portal.model.impl.PasswordPolicyRelModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the password policy rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PasswordPolicyRelPersistenceImpl
	extends BasePersistenceImpl
		<PasswordPolicyRel, NoSuchPasswordPolicyRelException>
	implements PasswordPolicyRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PasswordPolicyRelUtil</code> to access the password policy rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PasswordPolicyRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<PasswordPolicyRel, NoSuchPasswordPolicyRelException>
			_collectionPersistenceFinderByPasswordPolicyId;

	/**
	 * Returns an ordered range of all the password policy rels where passwordPolicyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordPolicyRelModelImpl</code>.
	 * </p>
	 *
	 * @param passwordPolicyId the password policy ID
	 * @param start the lower bound of the range of password policy rels
	 * @param end the upper bound of the range of password policy rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching password policy rels
	 */
	@Override
	public List<PasswordPolicyRel> findByPasswordPolicyId(
		long passwordPolicyId, int start, int end,
		OrderByComparator<PasswordPolicyRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPasswordPolicyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {passwordPolicyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first password policy rel in the ordered set where passwordPolicyId = &#63;.
	 *
	 * @param passwordPolicyId the password policy ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy rel
	 * @throws NoSuchPasswordPolicyRelException if a matching password policy rel could not be found
	 */
	@Override
	public PasswordPolicyRel findByPasswordPolicyId_First(
			long passwordPolicyId,
			OrderByComparator<PasswordPolicyRel> orderByComparator)
		throws NoSuchPasswordPolicyRelException {

		return _collectionPersistenceFinderByPasswordPolicyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {passwordPolicyId},
			orderByComparator);
	}

	/**
	 * Returns the first password policy rel in the ordered set where passwordPolicyId = &#63;.
	 *
	 * @param passwordPolicyId the password policy ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password policy rel, or <code>null</code> if a matching password policy rel could not be found
	 */
	@Override
	public PasswordPolicyRel fetchByPasswordPolicyId_First(
		long passwordPolicyId,
		OrderByComparator<PasswordPolicyRel> orderByComparator) {

		return _collectionPersistenceFinderByPasswordPolicyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {passwordPolicyId},
			orderByComparator);
	}

	/**
	 * Removes all the password policy rels where passwordPolicyId = &#63; from the database.
	 *
	 * @param passwordPolicyId the password policy ID
	 */
	@Override
	public void removeByPasswordPolicyId(long passwordPolicyId) {
		_collectionPersistenceFinderByPasswordPolicyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {passwordPolicyId});
	}

	/**
	 * Returns the number of password policy rels where passwordPolicyId = &#63;.
	 *
	 * @param passwordPolicyId the password policy ID
	 * @return the number of matching password policy rels
	 */
	@Override
	public int countByPasswordPolicyId(long passwordPolicyId) {
		return _collectionPersistenceFinderByPasswordPolicyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {passwordPolicyId});
	}

	private UniquePersistenceFinder
		<PasswordPolicyRel, NoSuchPasswordPolicyRelException>
			_uniquePersistenceFinderByC_C;

	/**
	 * Returns the password policy rel where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchPasswordPolicyRelException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching password policy rel
	 * @throws NoSuchPasswordPolicyRelException if a matching password policy rel could not be found
	 */
	@Override
	public PasswordPolicyRel findByC_C(long classNameId, long classPK)
		throws NoSuchPasswordPolicyRelException {

		return _uniquePersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the password policy rel where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching password policy rel, or <code>null</code> if a matching password policy rel could not be found
	 */
	@Override
	public PasswordPolicyRel fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the password policy rel where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the password policy rel that was removed
	 */
	@Override
	public PasswordPolicyRel removeByC_C(long classNameId, long classPK)
		throws NoSuchPasswordPolicyRelException {

		PasswordPolicyRel passwordPolicyRel = findByC_C(classNameId, classPK);

		return remove(passwordPolicyRel);
	}

	/**
	 * Returns the number of password policy rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching password policy rels
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	public PasswordPolicyRelPersistenceImpl() {
		setModelClass(PasswordPolicyRel.class);

		setModelImplClass(PasswordPolicyRelImpl.class);
		setModelPKClass(long.class);

		setTable(PasswordPolicyRelTable.INSTANCE);
	}

	/**
	 * Creates a new password policy rel with the primary key. Does not add the password policy rel to the database.
	 *
	 * @param passwordPolicyRelId the primary key for the new password policy rel
	 * @return the new password policy rel
	 */
	@Override
	public PasswordPolicyRel create(long passwordPolicyRelId) {
		PasswordPolicyRel passwordPolicyRel = new PasswordPolicyRelImpl();

		passwordPolicyRel.setNew(true);
		passwordPolicyRel.setPrimaryKey(passwordPolicyRelId);

		passwordPolicyRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return passwordPolicyRel;
	}

	/**
	 * Removes the password policy rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param passwordPolicyRelId the primary key of the password policy rel
	 * @return the password policy rel that was removed
	 * @throws NoSuchPasswordPolicyRelException if a password policy rel with the primary key could not be found
	 */
	@Override
	public PasswordPolicyRel remove(long passwordPolicyRelId)
		throws NoSuchPasswordPolicyRelException {

		return remove((Serializable)passwordPolicyRelId);
	}

	@Override
	protected PasswordPolicyRel removeImpl(
		PasswordPolicyRel passwordPolicyRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(passwordPolicyRel)) {
				passwordPolicyRel = (PasswordPolicyRel)session.get(
					PasswordPolicyRelImpl.class,
					passwordPolicyRel.getPrimaryKeyObj());
			}

			if (passwordPolicyRel != null) {
				session.delete(passwordPolicyRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (passwordPolicyRel != null) {
			clearCache(passwordPolicyRel);
		}

		return passwordPolicyRel;
	}

	@Override
	public PasswordPolicyRel updateImpl(PasswordPolicyRel passwordPolicyRel) {
		boolean isNew = passwordPolicyRel.isNew();

		if (!(passwordPolicyRel instanceof PasswordPolicyRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(passwordPolicyRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					passwordPolicyRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in passwordPolicyRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PasswordPolicyRel implementation " +
					passwordPolicyRel.getClass());
		}

		PasswordPolicyRelModelImpl passwordPolicyRelModelImpl =
			(PasswordPolicyRelModelImpl)passwordPolicyRel;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(passwordPolicyRel);
			}
			else {
				passwordPolicyRel = (PasswordPolicyRel)session.merge(
					passwordPolicyRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(passwordPolicyRel, false);

		if (isNew) {
			passwordPolicyRel.setNew(false);
		}

		passwordPolicyRel.resetOriginalValues();

		return passwordPolicyRel;
	}

	/**
	 * Returns the password policy rel with the primary key or throws a <code>NoSuchPasswordPolicyRelException</code> if it could not be found.
	 *
	 * @param passwordPolicyRelId the primary key of the password policy rel
	 * @return the password policy rel
	 * @throws NoSuchPasswordPolicyRelException if a password policy rel with the primary key could not be found
	 */
	@Override
	public PasswordPolicyRel findByPrimaryKey(long passwordPolicyRelId)
		throws NoSuchPasswordPolicyRelException {

		return findByPrimaryKey((Serializable)passwordPolicyRelId);
	}

	/**
	 * Returns the password policy rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param passwordPolicyRelId the primary key of the password policy rel
	 * @return the password policy rel, or <code>null</code> if a password policy rel with the primary key could not be found
	 */
	@Override
	public PasswordPolicyRel fetchByPrimaryKey(long passwordPolicyRelId) {
		return fetchByPrimaryKey((Serializable)passwordPolicyRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "passwordPolicyRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PASSWORDPOLICYREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PasswordPolicyRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the password policy rel persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByPasswordPolicyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPasswordPolicyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"passwordPolicyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPasswordPolicyId",
					new String[] {Long.class.getName()},
					new String[] {"passwordPolicyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPasswordPolicyId",
					new String[] {Long.class.getName()},
					new String[] {"passwordPolicyId"}, false),
				_SQL_SELECT_PASSWORDPOLICYREL_WHERE,
				_SQL_COUNT_PASSWORDPOLICYREL_WHERE,
				PasswordPolicyRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"passwordPolicyRel.", "passwordPolicyId",
					FinderColumn.Type.LONG, "=", true, true,
					PasswordPolicyRel::getPasswordPolicyId));

		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				PasswordPolicyRel::getClassNameId,
				PasswordPolicyRel::getClassPK),
			_SQL_SELECT_PASSWORDPOLICYREL_WHERE, "",
			new FinderColumn<>(
				"passwordPolicyRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, PasswordPolicyRel::getClassNameId),
			new FinderColumn<>(
				"passwordPolicyRel.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, PasswordPolicyRel::getClassPK));

		PasswordPolicyRelUtil.setPersistence(this);
	}

	public void destroy() {
		PasswordPolicyRelUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PasswordPolicyRelImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PasswordPolicyRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PASSWORDPOLICYREL =
		"SELECT passwordPolicyRel FROM PasswordPolicyRel passwordPolicyRel";

	private static final String _SQL_SELECT_PASSWORDPOLICYREL_WHERE =
		"SELECT passwordPolicyRel FROM PasswordPolicyRel passwordPolicyRel WHERE ";

	private static final String _SQL_COUNT_PASSWORDPOLICYREL_WHERE =
		"SELECT COUNT(passwordPolicyRel) FROM PasswordPolicyRel passwordPolicyRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PasswordPolicyRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordPolicyRelPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:682599298