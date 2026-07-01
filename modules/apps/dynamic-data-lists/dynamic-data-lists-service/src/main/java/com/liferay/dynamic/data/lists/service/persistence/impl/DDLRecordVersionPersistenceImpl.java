/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence.impl;

import com.liferay.dynamic.data.lists.exception.NoSuchRecordVersionException;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.model.DDLRecordVersionTable;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordVersionPersistence;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordVersionUtil;
import com.liferay.dynamic.data.lists.service.persistence.impl.constants.DDLPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ddl record version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDLRecordVersionPersistence.class)
public class DDLRecordVersionPersistenceImpl
	extends BasePersistenceImpl<DDLRecordVersion, NoSuchRecordVersionException>
	implements DDLRecordVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDLRecordVersionUtil</code> to access the ddl record version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDLRecordVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDLRecordVersion, NoSuchRecordVersionException>
			_collectionPersistenceFinderByRecordId;

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	@Override
	public List<DDLRecordVersion> findByRecordId(
		long recordId, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRecordId.find(
			finderCache, new Object[] {recordId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion findByRecordId_First(
			long recordId,
			OrderByComparator<DDLRecordVersion> orderByComparator)
		throws NoSuchRecordVersionException {

		return _collectionPersistenceFinderByRecordId.findFirst(
			finderCache, new Object[] {recordId}, orderByComparator);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion fetchByRecordId_First(
		long recordId, OrderByComparator<DDLRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByRecordId.fetchFirst(
			finderCache, new Object[] {recordId}, orderByComparator);
	}

	/**
	 * Removes all the ddl record versions where recordId = &#63; from the database.
	 *
	 * @param recordId the record ID
	 */
	@Override
	public void removeByRecordId(long recordId) {
		_collectionPersistenceFinderByRecordId.remove(
			finderCache, new Object[] {recordId});
	}

	/**
	 * Returns the number of ddl record versions where recordId = &#63;.
	 *
	 * @param recordId the record ID
	 * @return the number of matching ddl record versions
	 */
	@Override
	public int countByRecordId(long recordId) {
		return _collectionPersistenceFinderByRecordId.count(
			finderCache, new Object[] {recordId});
	}

	private CollectionPersistenceFinder
		<DDLRecordVersion, NoSuchRecordVersionException>
			_collectionPersistenceFinderByR_R;

	/**
	 * Returns an ordered range of all the ddl record versions where recordSetId = &#63; and recordSetVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	@Override
	public List<DDLRecordVersion> findByR_R(
		long recordSetId, String recordSetVersion, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_R.find(
			finderCache, new Object[] {recordSetId, recordSetVersion}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordSetId = &#63; and recordSetVersion = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion findByR_R_First(
			long recordSetId, String recordSetVersion,
			OrderByComparator<DDLRecordVersion> orderByComparator)
		throws NoSuchRecordVersionException {

		return _collectionPersistenceFinderByR_R.findFirst(
			finderCache, new Object[] {recordSetId, recordSetVersion},
			orderByComparator);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordSetId = &#63; and recordSetVersion = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion fetchByR_R_First(
		long recordSetId, String recordSetVersion,
		OrderByComparator<DDLRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByR_R.fetchFirst(
			finderCache, new Object[] {recordSetId, recordSetVersion},
			orderByComparator);
	}

	/**
	 * Removes all the ddl record versions where recordSetId = &#63; and recordSetVersion = &#63; from the database.
	 *
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 */
	@Override
	public void removeByR_R(long recordSetId, String recordSetVersion) {
		_collectionPersistenceFinderByR_R.remove(
			finderCache, new Object[] {recordSetId, recordSetVersion});
	}

	/**
	 * Returns the number of ddl record versions where recordSetId = &#63; and recordSetVersion = &#63;.
	 *
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @return the number of matching ddl record versions
	 */
	@Override
	public int countByR_R(long recordSetId, String recordSetVersion) {
		return _collectionPersistenceFinderByR_R.count(
			finderCache, new Object[] {recordSetId, recordSetVersion});
	}

	private UniquePersistenceFinder
		<DDLRecordVersion, NoSuchRecordVersionException>
			_uniquePersistenceFinderByR_V;

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or throws a <code>NoSuchRecordVersionException</code> if it could not be found.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion findByR_V(long recordId, String version)
		throws NoSuchRecordVersionException {

		return _uniquePersistenceFinderByR_V.find(
			finderCache, new Object[] {recordId, version});
	}

	/**
	 * Returns the ddl record version where recordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion fetchByR_V(
		long recordId, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByR_V.fetch(
			finderCache, new Object[] {recordId, version}, useFinderCache);
	}

	/**
	 * Removes the ddl record version where recordId = &#63; and version = &#63; from the database.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the ddl record version that was removed
	 */
	@Override
	public DDLRecordVersion removeByR_V(long recordId, String version)
		throws NoSuchRecordVersionException {

		DDLRecordVersion ddlRecordVersion = findByR_V(recordId, version);

		return remove(ddlRecordVersion);
	}

	/**
	 * Returns the number of ddl record versions where recordId = &#63; and version = &#63;.
	 *
	 * @param recordId the record ID
	 * @param version the version
	 * @return the number of matching ddl record versions
	 */
	@Override
	public int countByR_V(long recordId, String version) {
		return _uniquePersistenceFinderByR_V.count(
			finderCache, new Object[] {recordId, version});
	}

	private CollectionPersistenceFinder
		<DDLRecordVersion, NoSuchRecordVersionException>
			_collectionPersistenceFinderByR_S;

	/**
	 * Returns an ordered range of all the ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	@Override
	public List<DDLRecordVersion> findByR_S(
		long recordId, int status, int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_S.find(
			finderCache, new Object[] {recordId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion findByR_S_First(
			long recordId, int status,
			OrderByComparator<DDLRecordVersion> orderByComparator)
		throws NoSuchRecordVersionException {

		return _collectionPersistenceFinderByR_S.findFirst(
			finderCache, new Object[] {recordId, status}, orderByComparator);
	}

	/**
	 * Returns the first ddl record version in the ordered set where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion fetchByR_S_First(
		long recordId, int status,
		OrderByComparator<DDLRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByR_S.fetchFirst(
			finderCache, new Object[] {recordId, status}, orderByComparator);
	}

	/**
	 * Removes all the ddl record versions where recordId = &#63; and status = &#63; from the database.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 */
	@Override
	public void removeByR_S(long recordId, int status) {
		_collectionPersistenceFinderByR_S.remove(
			finderCache, new Object[] {recordId, status});
	}

	/**
	 * Returns the number of ddl record versions where recordId = &#63; and status = &#63;.
	 *
	 * @param recordId the record ID
	 * @param status the status
	 * @return the number of matching ddl record versions
	 */
	@Override
	public int countByR_S(long recordId, int status) {
		return _collectionPersistenceFinderByR_S.count(
			finderCache, new Object[] {recordId, status});
	}

	private CollectionPersistenceFinder
		<DDLRecordVersion, NoSuchRecordVersionException>
			_collectionPersistenceFinderByU_R_R_S;

	/**
	 * Returns an ordered range of all the ddl record versions where userId = &#63; and recordSetId = &#63; and recordSetVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param status the status
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record versions
	 */
	@Override
	public List<DDLRecordVersion> findByU_R_R_S(
		long userId, long recordSetId, String recordSetVersion, int status,
		int start, int end,
		OrderByComparator<DDLRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_R_R_S.find(
			finderCache,
			new Object[] {userId, recordSetId, recordSetVersion, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddl record version in the ordered set where userId = &#63; and recordSetId = &#63; and recordSetVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version
	 * @throws NoSuchRecordVersionException if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion findByU_R_R_S_First(
			long userId, long recordSetId, String recordSetVersion, int status,
			OrderByComparator<DDLRecordVersion> orderByComparator)
		throws NoSuchRecordVersionException {

		return _collectionPersistenceFinderByU_R_R_S.findFirst(
			finderCache,
			new Object[] {userId, recordSetId, recordSetVersion, status},
			orderByComparator);
	}

	/**
	 * Returns the first ddl record version in the ordered set where userId = &#63; and recordSetId = &#63; and recordSetVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record version, or <code>null</code> if a matching ddl record version could not be found
	 */
	@Override
	public DDLRecordVersion fetchByU_R_R_S_First(
		long userId, long recordSetId, String recordSetVersion, int status,
		OrderByComparator<DDLRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByU_R_R_S.fetchFirst(
			finderCache,
			new Object[] {userId, recordSetId, recordSetVersion, status},
			orderByComparator);
	}

	/**
	 * Removes all the ddl record versions where userId = &#63; and recordSetId = &#63; and recordSetVersion = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param status the status
	 */
	@Override
	public void removeByU_R_R_S(
		long userId, long recordSetId, String recordSetVersion, int status) {

		_collectionPersistenceFinderByU_R_R_S.remove(
			finderCache,
			new Object[] {userId, recordSetId, recordSetVersion, status});
	}

	/**
	 * Returns the number of ddl record versions where userId = &#63; and recordSetId = &#63; and recordSetVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param recordSetId the record set ID
	 * @param recordSetVersion the record set version
	 * @param status the status
	 * @return the number of matching ddl record versions
	 */
	@Override
	public int countByU_R_R_S(
		long userId, long recordSetId, String recordSetVersion, int status) {

		return _collectionPersistenceFinderByU_R_R_S.count(
			finderCache,
			new Object[] {userId, recordSetId, recordSetVersion, status});
	}

	public DDLRecordVersionPersistenceImpl() {
		setModelClass(DDLRecordVersion.class);

		setModelImplClass(DDLRecordVersionImpl.class);
		setModelPKClass(long.class);

		setTable(DDLRecordVersionTable.INSTANCE);
	}

	/**
	 * Creates a new ddl record version with the primary key. Does not add the ddl record version to the database.
	 *
	 * @param recordVersionId the primary key for the new ddl record version
	 * @return the new ddl record version
	 */
	@Override
	public DDLRecordVersion create(long recordVersionId) {
		DDLRecordVersion ddlRecordVersion = new DDLRecordVersionImpl();

		ddlRecordVersion.setNew(true);
		ddlRecordVersion.setPrimaryKey(recordVersionId);

		ddlRecordVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddlRecordVersion;
	}

	/**
	 * Removes the ddl record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version that was removed
	 * @throws NoSuchRecordVersionException if a ddl record version with the primary key could not be found
	 */
	@Override
	public DDLRecordVersion remove(long recordVersionId)
		throws NoSuchRecordVersionException {

		return remove((Serializable)recordVersionId);
	}

	@Override
	protected DDLRecordVersion removeImpl(DDLRecordVersion ddlRecordVersion) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddlRecordVersion)) {
				ddlRecordVersion = (DDLRecordVersion)session.get(
					DDLRecordVersionImpl.class,
					ddlRecordVersion.getPrimaryKeyObj());
			}

			if ((ddlRecordVersion != null) &&
				ctPersistenceHelper.isRemove(ddlRecordVersion)) {

				session.delete(ddlRecordVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddlRecordVersion != null) {
			clearCache(ddlRecordVersion);
		}

		return ddlRecordVersion;
	}

	@Override
	public DDLRecordVersion updateImpl(DDLRecordVersion ddlRecordVersion) {
		boolean isNew = ddlRecordVersion.isNew();

		if (!(ddlRecordVersion instanceof DDLRecordVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddlRecordVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddlRecordVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddlRecordVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDLRecordVersion implementation " +
					ddlRecordVersion.getClass());
		}

		DDLRecordVersionModelImpl ddlRecordVersionModelImpl =
			(DDLRecordVersionModelImpl)ddlRecordVersion;

		if (isNew && (ddlRecordVersion.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ddlRecordVersion.setCreateDate(date);
			}
			else {
				ddlRecordVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddlRecordVersion)) {
				if (!isNew) {
					session.evict(
						DDLRecordVersionImpl.class,
						ddlRecordVersion.getPrimaryKeyObj());
				}

				session.save(ddlRecordVersion);
			}
			else {
				ddlRecordVersion = (DDLRecordVersion)session.merge(
					ddlRecordVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddlRecordVersion, false);

		if (isNew) {
			ddlRecordVersion.setNew(false);
		}

		ddlRecordVersion.resetOriginalValues();

		return ddlRecordVersion;
	}

	/**
	 * Returns the ddl record version with the primary key or throws a <code>NoSuchRecordVersionException</code> if it could not be found.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version
	 * @throws NoSuchRecordVersionException if a ddl record version with the primary key could not be found
	 */
	@Override
	public DDLRecordVersion findByPrimaryKey(long recordVersionId)
		throws NoSuchRecordVersionException {

		return findByPrimaryKey((Serializable)recordVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddl record version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version, or <code>null</code> if a ddl record version with the primary key could not be found
	 */
	@Override
	public DDLRecordVersion fetchByPrimaryKey(long recordVersionId) {
		return fetchByPrimaryKey((Serializable)recordVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "recordVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDLRECORDVERSION;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return DDLRecordVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDLRecordVersion";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("DDMStorageId");
		ctMergeColumnNames.add("recordSetId");
		ctMergeColumnNames.add("recordSetVersion");
		ctMergeColumnNames.add("recordId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("displayIndex");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("recordVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"recordId", "version"});
	}

	/**
	 * Initializes the ddl record version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByRecordId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRecordId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"recordId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRecordId",
					new String[] {Long.class.getName()},
					new String[] {"recordId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRecordId", new String[] {Long.class.getName()},
					new String[] {"recordId"}, false),
				_SQL_SELECT_DDLRECORDVERSION_WHERE,
				_SQL_COUNT_DDLRECORDVERSION_WHERE,
				DDLRecordVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddlRecordVersion.", "recordId", FinderColumn.Type.LONG,
					"=", true, true, DDLRecordVersion::getRecordId));

		_collectionPersistenceFinderByR_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_R",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"recordSetId", "recordSetVersion"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_R",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"recordSetId", "recordSetVersion"}, 0, 2, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_R",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"recordSetId", "recordSetVersion"}, 0, 2, false,
				null),
			_SQL_SELECT_DDLRECORDVERSION_WHERE,
			_SQL_COUNT_DDLRECORDVERSION_WHERE,
			DDLRecordVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddlRecordVersion.", "recordSetId", FinderColumn.Type.LONG, "=",
				true, true, DDLRecordVersion::getRecordSetId),
			new FinderColumn<>(
				"ddlRecordVersion.", "recordSetVersion",
				FinderColumn.Type.STRING, "=", true, true,
				DDLRecordVersion::getRecordSetVersion));

		_uniquePersistenceFinderByR_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByR_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"recordId", "version"}, 0, 2, false,
				DDLRecordVersion::getRecordId,
				convertNullFunction(DDLRecordVersion::getVersion)),
			_SQL_SELECT_DDLRECORDVERSION_WHERE, "",
			new FinderColumn<>(
				"ddlRecordVersion.", "recordId", FinderColumn.Type.LONG, "=",
				true, true, DDLRecordVersion::getRecordId),
			new FinderColumn<>(
				"ddlRecordVersion.", "version", FinderColumn.Type.STRING, "=",
				true, true, DDLRecordVersion::getVersion));

		_collectionPersistenceFinderByR_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"recordId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"recordId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"recordId", "status"}, false),
			_SQL_SELECT_DDLRECORDVERSION_WHERE,
			_SQL_COUNT_DDLRECORDVERSION_WHERE,
			DDLRecordVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"ddlRecordVersion.", "recordId", FinderColumn.Type.LONG, "=",
				true, true, DDLRecordVersion::getRecordId),
			new FinderColumn<>(
				"ddlRecordVersion.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, DDLRecordVersion::getStatus));

		_collectionPersistenceFinderByU_R_R_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_R_R_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "recordSetId", "recordSetVersion", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_R_R_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"userId", "recordSetId", "recordSetVersion", "status"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_R_R_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"userId", "recordSetId", "recordSetVersion", "status"
					},
					0, 4, false, null),
				_SQL_SELECT_DDLRECORDVERSION_WHERE,
				_SQL_COUNT_DDLRECORDVERSION_WHERE,
				DDLRecordVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"ddlRecordVersion.", "userId", FinderColumn.Type.LONG, "=",
					true, true, DDLRecordVersion::getUserId),
				new FinderColumn<>(
					"ddlRecordVersion.", "recordSetId", FinderColumn.Type.LONG,
					"=", true, true, DDLRecordVersion::getRecordSetId),
				new FinderColumn<>(
					"ddlRecordVersion.", "recordSetVersion",
					FinderColumn.Type.STRING, "=", true, true,
					DDLRecordVersion::getRecordSetVersion),
				new FinderColumn<>(
					"ddlRecordVersion.", "status", FinderColumn.Type.INTEGER,
					"=", true, true, DDLRecordVersion::getStatus));

		DDLRecordVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDLRecordVersionUtil.setPersistence(null);

		entityCache.removeCache(DDLRecordVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		DDLRecordVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDLRECORDVERSION =
		"SELECT ddlRecordVersion FROM DDLRecordVersion ddlRecordVersion";

	private static final String _SQL_SELECT_DDLRECORDVERSION_WHERE =
		"SELECT ddlRecordVersion FROM DDLRecordVersion ddlRecordVersion WHERE ";

	private static final String _SQL_COUNT_DDLRECORDVERSION_WHERE =
		"SELECT COUNT(ddlRecordVersion) FROM DDLRecordVersion ddlRecordVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDLRecordVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDLRecordVersionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2026773383