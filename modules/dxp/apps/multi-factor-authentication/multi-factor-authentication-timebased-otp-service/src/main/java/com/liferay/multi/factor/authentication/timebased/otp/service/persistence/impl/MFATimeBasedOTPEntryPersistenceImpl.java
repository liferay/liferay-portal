/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.service.persistence.impl;

import com.liferay.multi.factor.authentication.timebased.otp.exception.NoSuchEntryException;
import com.liferay.multi.factor.authentication.timebased.otp.model.MFATimeBasedOTPEntry;
import com.liferay.multi.factor.authentication.timebased.otp.model.MFATimeBasedOTPEntryTable;
import com.liferay.multi.factor.authentication.timebased.otp.model.impl.MFATimeBasedOTPEntryImpl;
import com.liferay.multi.factor.authentication.timebased.otp.model.impl.MFATimeBasedOTPEntryModelImpl;
import com.liferay.multi.factor.authentication.timebased.otp.service.persistence.MFATimeBasedOTPEntryPersistence;
import com.liferay.multi.factor.authentication.timebased.otp.service.persistence.MFATimeBasedOTPEntryUtil;
import com.liferay.multi.factor.authentication.timebased.otp.service.persistence.impl.constants.MFATimeBasedOTPPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the mfa time based otp entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @generated
 */
@Component(service = MFATimeBasedOTPEntryPersistence.class)
public class MFATimeBasedOTPEntryPersistenceImpl
	extends BasePersistenceImpl<MFATimeBasedOTPEntry, NoSuchEntryException>
	implements MFATimeBasedOTPEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MFATimeBasedOTPEntryUtil</code> to access the mfa time based otp entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MFATimeBasedOTPEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<MFATimeBasedOTPEntry, NoSuchEntryException>
		_uniquePersistenceFinderByUserId;

	/**
	 * Returns the mfa time based otp entry where userId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching mfa time based otp entry
	 * @throws NoSuchEntryException if a matching mfa time based otp entry could not be found
	 */
	@Override
	public MFATimeBasedOTPEntry findByUserId(long userId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByUserId.find(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the mfa time based otp entry where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching mfa time based otp entry, or <code>null</code> if a matching mfa time based otp entry could not be found
	 */
	@Override
	public MFATimeBasedOTPEntry fetchByUserId(
		long userId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUserId.fetch(
			finderCache, new Object[] {userId}, useFinderCache);
	}

	/**
	 * Removes the mfa time based otp entry where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the mfa time based otp entry that was removed
	 */
	@Override
	public MFATimeBasedOTPEntry removeByUserId(long userId)
		throws NoSuchEntryException {

		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry = findByUserId(userId);

		return remove(mfaTimeBasedOTPEntry);
	}

	/**
	 * Returns the number of mfa time based otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching mfa time based otp entries
	 */
	@Override
	public int countByUserId(long userId) {
		return _uniquePersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	public MFATimeBasedOTPEntryPersistenceImpl() {
		setModelClass(MFATimeBasedOTPEntry.class);

		setModelImplClass(MFATimeBasedOTPEntryImpl.class);
		setModelPKClass(long.class);

		setTable(MFATimeBasedOTPEntryTable.INSTANCE);
	}

	/**
	 * Creates a new mfa time based otp entry with the primary key. Does not add the mfa time based otp entry to the database.
	 *
	 * @param mfaTimeBasedOTPEntryId the primary key for the new mfa time based otp entry
	 * @return the new mfa time based otp entry
	 */
	@Override
	public MFATimeBasedOTPEntry create(long mfaTimeBasedOTPEntryId) {
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			new MFATimeBasedOTPEntryImpl();

		mfaTimeBasedOTPEntry.setNew(true);
		mfaTimeBasedOTPEntry.setPrimaryKey(mfaTimeBasedOTPEntryId);

		mfaTimeBasedOTPEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mfaTimeBasedOTPEntry;
	}

	/**
	 * Removes the mfa time based otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mfaTimeBasedOTPEntryId the primary key of the mfa time based otp entry
	 * @return the mfa time based otp entry that was removed
	 * @throws NoSuchEntryException if a mfa time based otp entry with the primary key could not be found
	 */
	@Override
	public MFATimeBasedOTPEntry remove(long mfaTimeBasedOTPEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)mfaTimeBasedOTPEntryId);
	}

	@Override
	protected MFATimeBasedOTPEntry removeImpl(
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mfaTimeBasedOTPEntry)) {
				mfaTimeBasedOTPEntry = (MFATimeBasedOTPEntry)session.get(
					MFATimeBasedOTPEntryImpl.class,
					mfaTimeBasedOTPEntry.getPrimaryKeyObj());
			}

			if (mfaTimeBasedOTPEntry != null) {
				session.delete(mfaTimeBasedOTPEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mfaTimeBasedOTPEntry != null) {
			clearCache(mfaTimeBasedOTPEntry);
		}

		return mfaTimeBasedOTPEntry;
	}

	@Override
	public MFATimeBasedOTPEntry updateImpl(
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		boolean isNew = mfaTimeBasedOTPEntry.isNew();

		if (!(mfaTimeBasedOTPEntry instanceof MFATimeBasedOTPEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mfaTimeBasedOTPEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					mfaTimeBasedOTPEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mfaTimeBasedOTPEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MFATimeBasedOTPEntry implementation " +
					mfaTimeBasedOTPEntry.getClass());
		}

		MFATimeBasedOTPEntryModelImpl mfaTimeBasedOTPEntryModelImpl =
			(MFATimeBasedOTPEntryModelImpl)mfaTimeBasedOTPEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (mfaTimeBasedOTPEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				mfaTimeBasedOTPEntry.setCreateDate(date);
			}
			else {
				mfaTimeBasedOTPEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!mfaTimeBasedOTPEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				mfaTimeBasedOTPEntry.setModifiedDate(date);
			}
			else {
				mfaTimeBasedOTPEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(mfaTimeBasedOTPEntry);
			}
			else {
				mfaTimeBasedOTPEntry = (MFATimeBasedOTPEntry)session.merge(
					mfaTimeBasedOTPEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(mfaTimeBasedOTPEntry, false);

		if (isNew) {
			mfaTimeBasedOTPEntry.setNew(false);
		}

		mfaTimeBasedOTPEntry.resetOriginalValues();

		return mfaTimeBasedOTPEntry;
	}

	/**
	 * Returns the mfa time based otp entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param mfaTimeBasedOTPEntryId the primary key of the mfa time based otp entry
	 * @return the mfa time based otp entry
	 * @throws NoSuchEntryException if a mfa time based otp entry with the primary key could not be found
	 */
	@Override
	public MFATimeBasedOTPEntry findByPrimaryKey(long mfaTimeBasedOTPEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)mfaTimeBasedOTPEntryId);
	}

	/**
	 * Returns the mfa time based otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mfaTimeBasedOTPEntryId the primary key of the mfa time based otp entry
	 * @return the mfa time based otp entry, or <code>null</code> if a mfa time based otp entry with the primary key could not be found
	 */
	@Override
	public MFATimeBasedOTPEntry fetchByPrimaryKey(long mfaTimeBasedOTPEntryId) {
		return fetchByPrimaryKey((Serializable)mfaTimeBasedOTPEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "mfaTimeBasedOTPEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MFATIMEBASEDOTPENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MFATimeBasedOTPEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the mfa time based otp entry persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByUserId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
				new String[] {Long.class.getName()}, new String[] {"userId"}, 0,
				0, false, MFATimeBasedOTPEntry::getUserId),
			_SQL_SELECT_MFATIMEBASEDOTPENTRY_WHERE, "",
			new FinderColumn<>(
				"mfaTimeBasedOTPEntry.", "userId", FinderColumn.Type.LONG, "=",
				true, true, MFATimeBasedOTPEntry::getUserId));

		MFATimeBasedOTPEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MFATimeBasedOTPEntryUtil.setPersistence(null);

		entityCache.removeCache(MFATimeBasedOTPEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = MFATimeBasedOTPPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = MFATimeBasedOTPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = MFATimeBasedOTPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_MFATIMEBASEDOTPENTRY =
		"SELECT mfaTimeBasedOTPEntry FROM MFATimeBasedOTPEntry mfaTimeBasedOTPEntry";

	private static final String _SQL_SELECT_MFATIMEBASEDOTPENTRY_WHERE =
		"SELECT mfaTimeBasedOTPEntry FROM MFATimeBasedOTPEntry mfaTimeBasedOTPEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1957435868