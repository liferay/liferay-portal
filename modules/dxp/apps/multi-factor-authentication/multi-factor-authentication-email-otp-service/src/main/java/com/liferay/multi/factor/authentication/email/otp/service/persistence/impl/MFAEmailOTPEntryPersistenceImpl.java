/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.email.otp.service.persistence.impl;

import com.liferay.multi.factor.authentication.email.otp.exception.NoSuchEntryException;
import com.liferay.multi.factor.authentication.email.otp.model.MFAEmailOTPEntry;
import com.liferay.multi.factor.authentication.email.otp.model.MFAEmailOTPEntryTable;
import com.liferay.multi.factor.authentication.email.otp.model.impl.MFAEmailOTPEntryImpl;
import com.liferay.multi.factor.authentication.email.otp.model.impl.MFAEmailOTPEntryModelImpl;
import com.liferay.multi.factor.authentication.email.otp.service.persistence.MFAEmailOTPEntryPersistence;
import com.liferay.multi.factor.authentication.email.otp.service.persistence.MFAEmailOTPEntryUtil;
import com.liferay.multi.factor.authentication.email.otp.service.persistence.impl.constants.MFAEmailOTPPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the mfa email otp entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @generated
 */
@Component(service = MFAEmailOTPEntryPersistence.class)
public class MFAEmailOTPEntryPersistenceImpl
	extends BasePersistenceImpl<MFAEmailOTPEntry, NoSuchEntryException>
	implements MFAEmailOTPEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MFAEmailOTPEntryUtil</code> to access the mfa email otp entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MFAEmailOTPEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<MFAEmailOTPEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUserId;

	/**
	 * Returns the mfa email otp entry where userId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching mfa email otp entry
	 * @throws NoSuchEntryException if a matching mfa email otp entry could not be found
	 */
	@Override
	public MFAEmailOTPEntry findByUserId(long userId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUserId.find(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the mfa email otp entry where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching mfa email otp entry, or <code>null</code> if a matching mfa email otp entry could not be found
	 */
	@Override
	public MFAEmailOTPEntry fetchByUserId(long userId, boolean useFinderCache) {
		return _uniquePersistenceFinderByUserId.fetch(
			finderCache, new Object[] {userId}, useFinderCache);
	}

	/**
	 * Removes the mfa email otp entry where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the mfa email otp entry that was removed
	 */
	@Override
	public MFAEmailOTPEntry removeByUserId(long userId)
		throws NoSuchEntryException {

		MFAEmailOTPEntry mfaEmailOTPEntry = findByUserId(userId);

		return remove(mfaEmailOTPEntry);
	}

	/**
	 * Returns the number of mfa email otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching mfa email otp entries
	 */
	@Override
	public int countByUserId(long userId) {
		return _uniquePersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	public MFAEmailOTPEntryPersistenceImpl() {
		setModelClass(MFAEmailOTPEntry.class);

		setModelImplClass(MFAEmailOTPEntryImpl.class);
		setModelPKClass(long.class);

		setTable(MFAEmailOTPEntryTable.INSTANCE);
	}

	/**
	 * Creates a new mfa email otp entry with the primary key. Does not add the mfa email otp entry to the database.
	 *
	 * @param mfaEmailOTPEntryId the primary key for the new mfa email otp entry
	 * @return the new mfa email otp entry
	 */
	@Override
	public MFAEmailOTPEntry create(long mfaEmailOTPEntryId) {
		MFAEmailOTPEntry mfaEmailOTPEntry = new MFAEmailOTPEntryImpl();

		mfaEmailOTPEntry.setNew(true);
		mfaEmailOTPEntry.setPrimaryKey(mfaEmailOTPEntryId);

		mfaEmailOTPEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mfaEmailOTPEntry;
	}

	/**
	 * Removes the mfa email otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mfaEmailOTPEntryId the primary key of the mfa email otp entry
	 * @return the mfa email otp entry that was removed
	 * @throws NoSuchEntryException if a mfa email otp entry with the primary key could not be found
	 */
	@Override
	public MFAEmailOTPEntry remove(long mfaEmailOTPEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)mfaEmailOTPEntryId);
	}

	@Override
	protected MFAEmailOTPEntry removeImpl(MFAEmailOTPEntry mfaEmailOTPEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mfaEmailOTPEntry)) {
				mfaEmailOTPEntry = (MFAEmailOTPEntry)session.get(
					MFAEmailOTPEntryImpl.class,
					mfaEmailOTPEntry.getPrimaryKeyObj());
			}

			if (mfaEmailOTPEntry != null) {
				session.delete(mfaEmailOTPEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mfaEmailOTPEntry != null) {
			clearCache(mfaEmailOTPEntry);
		}

		return mfaEmailOTPEntry;
	}

	@Override
	public MFAEmailOTPEntry updateImpl(MFAEmailOTPEntry mfaEmailOTPEntry) {
		boolean isNew = mfaEmailOTPEntry.isNew();

		if (!(mfaEmailOTPEntry instanceof MFAEmailOTPEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mfaEmailOTPEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					mfaEmailOTPEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mfaEmailOTPEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MFAEmailOTPEntry implementation " +
					mfaEmailOTPEntry.getClass());
		}

		MFAEmailOTPEntryModelImpl mfaEmailOTPEntryModelImpl =
			(MFAEmailOTPEntryModelImpl)mfaEmailOTPEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mfaEmailOTPEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				mfaEmailOTPEntry.setCreateDate(date);
			}
			else {
				mfaEmailOTPEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!mfaEmailOTPEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mfaEmailOTPEntry.setModifiedDate(date);
			}
			else {
				mfaEmailOTPEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(mfaEmailOTPEntry);
			}
			else {
				mfaEmailOTPEntry = (MFAEmailOTPEntry)session.merge(
					mfaEmailOTPEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mfaEmailOTPEntry, false);

		if (isNew) {
			mfaEmailOTPEntry.setNew(false);
		}

		mfaEmailOTPEntry.resetOriginalValues();

		return mfaEmailOTPEntry;
	}

	/**
	 * Returns the mfa email otp entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param mfaEmailOTPEntryId the primary key of the mfa email otp entry
	 * @return the mfa email otp entry
	 * @throws NoSuchEntryException if a mfa email otp entry with the primary key could not be found
	 */
	@Override
	public MFAEmailOTPEntry findByPrimaryKey(long mfaEmailOTPEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)mfaEmailOTPEntryId);
	}

	/**
	 * Returns the mfa email otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mfaEmailOTPEntryId the primary key of the mfa email otp entry
	 * @return the mfa email otp entry, or <code>null</code> if a mfa email otp entry with the primary key could not be found
	 */
	@Override
	public MFAEmailOTPEntry fetchByPrimaryKey(long mfaEmailOTPEntryId) {
		return fetchByPrimaryKey((Serializable)mfaEmailOTPEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "mfaEmailOTPEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MFAEMAILOTPENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MFAEmailOTPEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the mfa email otp entry persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByUserId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
				new String[] {Long.class.getName()}, new String[] {"userId"}, 0,
				0, false, MFAEmailOTPEntry::getUserId),
			_SQL_SELECT_MFAEMAILOTPENTRY_WHERE, "",
			new FinderColumn<>(
				"mfaEmailOTPEntry.", "userId", FinderColumn.Type.LONG, "=",
				true, true, MFAEmailOTPEntry::getUserId));

		MFAEmailOTPEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MFAEmailOTPEntryUtil.setPersistence(null);

		entityCache.removeCache(MFAEmailOTPEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = MFAEmailOTPPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = MFAEmailOTPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = MFAEmailOTPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_MFAEMAILOTPENTRY =
		"SELECT mfaEmailOTPEntry FROM MFAEmailOTPEntry mfaEmailOTPEntry";

	private static final String _SQL_SELECT_MFAEMAILOTPENTRY_WHERE =
		"SELECT mfaEmailOTPEntry FROM MFAEmailOTPEntry mfaEmailOTPEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MFAEmailOTPEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MFAEmailOTPEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1771692065