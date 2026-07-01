/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.credential.service.persistence.impl;

import com.liferay.multi.factor.authentication.fido2.credential.exception.NoSuchMFAFIDO2CredentialEntryException;
import com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntry;
import com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntryTable;
import com.liferay.multi.factor.authentication.fido2.credential.model.impl.MFAFIDO2CredentialEntryImpl;
import com.liferay.multi.factor.authentication.fido2.credential.model.impl.MFAFIDO2CredentialEntryModelImpl;
import com.liferay.multi.factor.authentication.fido2.credential.service.persistence.MFAFIDO2CredentialEntryPersistence;
import com.liferay.multi.factor.authentication.fido2.credential.service.persistence.MFAFIDO2CredentialEntryUtil;
import com.liferay.multi.factor.authentication.fido2.credential.service.persistence.impl.constants.MFAFIDOTwoCredentialPersistenceConstants;
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
 * The persistence implementation for the mfafido2 credential entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @generated
 */
@Component(service = MFAFIDO2CredentialEntryPersistence.class)
public class MFAFIDO2CredentialEntryPersistenceImpl
	extends BasePersistenceImpl
		<MFAFIDO2CredentialEntry, NoSuchMFAFIDO2CredentialEntryException>
	implements MFAFIDO2CredentialEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MFAFIDO2CredentialEntryUtil</code> to access the mfafido2 credential entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MFAFIDO2CredentialEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<MFAFIDO2CredentialEntry, NoSuchMFAFIDO2CredentialEntryException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the mfafido2 credential entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MFAFIDO2CredentialEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of mfafido2 credential entries
	 * @param end the upper bound of the range of mfafido2 credential entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching mfafido2 credential entries
	 */
	@Override
	public List<MFAFIDO2CredentialEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MFAFIDO2CredentialEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first mfafido2 credential entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mfafido2 credential entry
	 * @throws NoSuchMFAFIDO2CredentialEntryException if a matching mfafido2 credential entry could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry findByUserId_First(
			long userId,
			OrderByComparator<MFAFIDO2CredentialEntry> orderByComparator)
		throws NoSuchMFAFIDO2CredentialEntryException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first mfafido2 credential entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mfafido2 credential entry, or <code>null</code> if a matching mfafido2 credential entry could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry fetchByUserId_First(
		long userId,
		OrderByComparator<MFAFIDO2CredentialEntry> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the mfafido2 credential entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of mfafido2 credential entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching mfafido2 credential entries
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<MFAFIDO2CredentialEntry, NoSuchMFAFIDO2CredentialEntryException>
			_collectionPersistenceFinderByCredentialKeyHash;

	/**
	 * Returns an ordered range of all the mfafido2 credential entries where credentialKeyHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MFAFIDO2CredentialEntryModelImpl</code>.
	 * </p>
	 *
	 * @param credentialKeyHash the credential key hash
	 * @param start the lower bound of the range of mfafido2 credential entries
	 * @param end the upper bound of the range of mfafido2 credential entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching mfafido2 credential entries
	 */
	@Override
	public List<MFAFIDO2CredentialEntry> findByCredentialKeyHash(
		long credentialKeyHash, int start, int end,
		OrderByComparator<MFAFIDO2CredentialEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCredentialKeyHash.find(
			finderCache, new Object[] {credentialKeyHash}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first mfafido2 credential entry in the ordered set where credentialKeyHash = &#63;.
	 *
	 * @param credentialKeyHash the credential key hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mfafido2 credential entry
	 * @throws NoSuchMFAFIDO2CredentialEntryException if a matching mfafido2 credential entry could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry findByCredentialKeyHash_First(
			long credentialKeyHash,
			OrderByComparator<MFAFIDO2CredentialEntry> orderByComparator)
		throws NoSuchMFAFIDO2CredentialEntryException {

		return _collectionPersistenceFinderByCredentialKeyHash.findFirst(
			finderCache, new Object[] {credentialKeyHash}, orderByComparator);
	}

	/**
	 * Returns the first mfafido2 credential entry in the ordered set where credentialKeyHash = &#63;.
	 *
	 * @param credentialKeyHash the credential key hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mfafido2 credential entry, or <code>null</code> if a matching mfafido2 credential entry could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry fetchByCredentialKeyHash_First(
		long credentialKeyHash,
		OrderByComparator<MFAFIDO2CredentialEntry> orderByComparator) {

		return _collectionPersistenceFinderByCredentialKeyHash.fetchFirst(
			finderCache, new Object[] {credentialKeyHash}, orderByComparator);
	}

	/**
	 * Removes all the mfafido2 credential entries where credentialKeyHash = &#63; from the database.
	 *
	 * @param credentialKeyHash the credential key hash
	 */
	@Override
	public void removeByCredentialKeyHash(long credentialKeyHash) {
		_collectionPersistenceFinderByCredentialKeyHash.remove(
			finderCache, new Object[] {credentialKeyHash});
	}

	/**
	 * Returns the number of mfafido2 credential entries where credentialKeyHash = &#63;.
	 *
	 * @param credentialKeyHash the credential key hash
	 * @return the number of matching mfafido2 credential entries
	 */
	@Override
	public int countByCredentialKeyHash(long credentialKeyHash) {
		return _collectionPersistenceFinderByCredentialKeyHash.count(
			finderCache, new Object[] {credentialKeyHash});
	}

	private UniquePersistenceFinder
		<MFAFIDO2CredentialEntry, NoSuchMFAFIDO2CredentialEntryException>
			_uniquePersistenceFinderByU_C;

	/**
	 * Returns the mfafido2 credential entry where userId = &#63; and credentialKeyHash = &#63; or throws a <code>NoSuchMFAFIDO2CredentialEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param credentialKeyHash the credential key hash
	 * @return the matching mfafido2 credential entry
	 * @throws NoSuchMFAFIDO2CredentialEntryException if a matching mfafido2 credential entry could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry findByU_C(
			long userId, long credentialKeyHash)
		throws NoSuchMFAFIDO2CredentialEntryException {

		return _uniquePersistenceFinderByU_C.find(
			finderCache, new Object[] {userId, credentialKeyHash});
	}

	/**
	 * Returns the mfafido2 credential entry where userId = &#63; and credentialKeyHash = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param credentialKeyHash the credential key hash
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching mfafido2 credential entry, or <code>null</code> if a matching mfafido2 credential entry could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry fetchByU_C(
		long userId, long credentialKeyHash, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_C.fetch(
			finderCache, new Object[] {userId, credentialKeyHash},
			useFinderCache);
	}

	/**
	 * Removes the mfafido2 credential entry where userId = &#63; and credentialKeyHash = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param credentialKeyHash the credential key hash
	 * @return the mfafido2 credential entry that was removed
	 */
	@Override
	public MFAFIDO2CredentialEntry removeByU_C(
			long userId, long credentialKeyHash)
		throws NoSuchMFAFIDO2CredentialEntryException {

		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry = findByU_C(
			userId, credentialKeyHash);

		return remove(mfaFIDO2CredentialEntry);
	}

	/**
	 * Returns the number of mfafido2 credential entries where userId = &#63; and credentialKeyHash = &#63;.
	 *
	 * @param userId the user ID
	 * @param credentialKeyHash the credential key hash
	 * @return the number of matching mfafido2 credential entries
	 */
	@Override
	public int countByU_C(long userId, long credentialKeyHash) {
		return _uniquePersistenceFinderByU_C.count(
			finderCache, new Object[] {userId, credentialKeyHash});
	}

	public MFAFIDO2CredentialEntryPersistenceImpl() {
		setModelClass(MFAFIDO2CredentialEntry.class);

		setModelImplClass(MFAFIDO2CredentialEntryImpl.class);
		setModelPKClass(long.class);

		setTable(MFAFIDO2CredentialEntryTable.INSTANCE);
	}

	/**
	 * Creates a new mfafido2 credential entry with the primary key. Does not add the mfafido2 credential entry to the database.
	 *
	 * @param mfaFIDO2CredentialEntryId the primary key for the new mfafido2 credential entry
	 * @return the new mfafido2 credential entry
	 */
	@Override
	public MFAFIDO2CredentialEntry create(long mfaFIDO2CredentialEntryId) {
		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry =
			new MFAFIDO2CredentialEntryImpl();

		mfaFIDO2CredentialEntry.setNew(true);
		mfaFIDO2CredentialEntry.setPrimaryKey(mfaFIDO2CredentialEntryId);

		mfaFIDO2CredentialEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mfaFIDO2CredentialEntry;
	}

	/**
	 * Removes the mfafido2 credential entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mfaFIDO2CredentialEntryId the primary key of the mfafido2 credential entry
	 * @return the mfafido2 credential entry that was removed
	 * @throws NoSuchMFAFIDO2CredentialEntryException if a mfafido2 credential entry with the primary key could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry remove(long mfaFIDO2CredentialEntryId)
		throws NoSuchMFAFIDO2CredentialEntryException {

		return remove((Serializable)mfaFIDO2CredentialEntryId);
	}

	@Override
	protected MFAFIDO2CredentialEntry removeImpl(
		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mfaFIDO2CredentialEntry)) {
				mfaFIDO2CredentialEntry = (MFAFIDO2CredentialEntry)session.get(
					MFAFIDO2CredentialEntryImpl.class,
					mfaFIDO2CredentialEntry.getPrimaryKeyObj());
			}

			if (mfaFIDO2CredentialEntry != null) {
				session.delete(mfaFIDO2CredentialEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mfaFIDO2CredentialEntry != null) {
			clearCache(mfaFIDO2CredentialEntry);
		}

		return mfaFIDO2CredentialEntry;
	}

	@Override
	public MFAFIDO2CredentialEntry updateImpl(
		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry) {

		boolean isNew = mfaFIDO2CredentialEntry.isNew();

		if (!(mfaFIDO2CredentialEntry instanceof
				MFAFIDO2CredentialEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mfaFIDO2CredentialEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					mfaFIDO2CredentialEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mfaFIDO2CredentialEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MFAFIDO2CredentialEntry implementation " +
					mfaFIDO2CredentialEntry.getClass());
		}

		MFAFIDO2CredentialEntryModelImpl mfaFIDO2CredentialEntryModelImpl =
			(MFAFIDO2CredentialEntryModelImpl)mfaFIDO2CredentialEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mfaFIDO2CredentialEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				mfaFIDO2CredentialEntry.setCreateDate(date);
			}
			else {
				mfaFIDO2CredentialEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!mfaFIDO2CredentialEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mfaFIDO2CredentialEntry.setModifiedDate(date);
			}
			else {
				mfaFIDO2CredentialEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(mfaFIDO2CredentialEntry);
			}
			else {
				mfaFIDO2CredentialEntry =
					(MFAFIDO2CredentialEntry)session.merge(
						mfaFIDO2CredentialEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mfaFIDO2CredentialEntry, false);

		if (isNew) {
			mfaFIDO2CredentialEntry.setNew(false);
		}

		mfaFIDO2CredentialEntry.resetOriginalValues();

		return mfaFIDO2CredentialEntry;
	}

	/**
	 * Returns the mfafido2 credential entry with the primary key or throws a <code>NoSuchMFAFIDO2CredentialEntryException</code> if it could not be found.
	 *
	 * @param mfaFIDO2CredentialEntryId the primary key of the mfafido2 credential entry
	 * @return the mfafido2 credential entry
	 * @throws NoSuchMFAFIDO2CredentialEntryException if a mfafido2 credential entry with the primary key could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry findByPrimaryKey(
			long mfaFIDO2CredentialEntryId)
		throws NoSuchMFAFIDO2CredentialEntryException {

		return findByPrimaryKey((Serializable)mfaFIDO2CredentialEntryId);
	}

	/**
	 * Returns the mfafido2 credential entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mfaFIDO2CredentialEntryId the primary key of the mfafido2 credential entry
	 * @return the mfafido2 credential entry, or <code>null</code> if a mfafido2 credential entry with the primary key could not be found
	 */
	@Override
	public MFAFIDO2CredentialEntry fetchByPrimaryKey(
		long mfaFIDO2CredentialEntryId) {

		return fetchByPrimaryKey((Serializable)mfaFIDO2CredentialEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "mfaFIDO2CredentialEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MFAFIDO2CREDENTIALENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MFAFIDO2CredentialEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the mfafido2 credential entry persistence.
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
				_SQL_SELECT_MFAFIDO2CREDENTIALENTRY_WHERE,
				_SQL_COUNT_MFAFIDO2CREDENTIALENTRY_WHERE,
				MFAFIDO2CredentialEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mfafido2CredentialEntry.", "userId",
					FinderColumn.Type.LONG, "=", true, true,
					MFAFIDO2CredentialEntry::getUserId));

		_collectionPersistenceFinderByCredentialKeyHash =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCredentialKeyHash",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"credentialKeyHash"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCredentialKeyHash",
					new String[] {Long.class.getName()},
					new String[] {"credentialKeyHash"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCredentialKeyHash",
					new String[] {Long.class.getName()},
					new String[] {"credentialKeyHash"}, false),
				_SQL_SELECT_MFAFIDO2CREDENTIALENTRY_WHERE,
				_SQL_COUNT_MFAFIDO2CREDENTIALENTRY_WHERE,
				MFAFIDO2CredentialEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"mfafido2CredentialEntry.", "credentialKeyHash",
					FinderColumn.Type.LONG, "=", true, true,
					MFAFIDO2CredentialEntry::getCredentialKeyHash));

		_uniquePersistenceFinderByU_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "credentialKeyHash"}, 0, 0, false,
				MFAFIDO2CredentialEntry::getUserId,
				MFAFIDO2CredentialEntry::getCredentialKeyHash),
			_SQL_SELECT_MFAFIDO2CREDENTIALENTRY_WHERE, "",
			new FinderColumn<>(
				"mfafido2CredentialEntry.", "userId", FinderColumn.Type.LONG,
				"=", true, true, MFAFIDO2CredentialEntry::getUserId),
			new FinderColumn<>(
				"mfafido2CredentialEntry.", "credentialKeyHash",
				FinderColumn.Type.LONG, "=", true, true,
				MFAFIDO2CredentialEntry::getCredentialKeyHash));

		MFAFIDO2CredentialEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MFAFIDO2CredentialEntryUtil.setPersistence(null);

		entityCache.removeCache(MFAFIDO2CredentialEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = MFAFIDOTwoCredentialPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = MFAFIDOTwoCredentialPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = MFAFIDOTwoCredentialPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		MFAFIDO2CredentialEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MFAFIDO2CREDENTIALENTRY =
		"SELECT mfafido2CredentialEntry FROM MFAFIDO2CredentialEntry mfafido2CredentialEntry";

	private static final String _SQL_SELECT_MFAFIDO2CREDENTIALENTRY_WHERE =
		"SELECT mfafido2CredentialEntry FROM MFAFIDO2CredentialEntry mfafido2CredentialEntry WHERE ";

	private static final String _SQL_COUNT_MFAFIDO2CREDENTIALENTRY_WHERE =
		"SELECT COUNT(mfafido2CredentialEntry) FROM MFAFIDO2CredentialEntry mfafido2CredentialEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MFAFIDO2CredentialEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MFAFIDO2CredentialEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:115393237