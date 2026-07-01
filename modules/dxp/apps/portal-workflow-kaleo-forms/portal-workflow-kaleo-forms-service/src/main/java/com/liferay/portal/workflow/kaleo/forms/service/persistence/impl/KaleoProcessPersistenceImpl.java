/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.workflow.kaleo.forms.exception.NoSuchKaleoProcessException;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcessTable;
import com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessImpl;
import com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessPersistence;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.KaleoProcessUtil;
import com.liferay.portal.workflow.kaleo.forms.service.persistence.impl.constants.KaleoFormsPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the kaleo process service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marcellus Tavares
 * @generated
 */
@Component(service = KaleoProcessPersistence.class)
public class KaleoProcessPersistenceImpl
	extends BasePersistenceImpl<KaleoProcess, NoSuchKaleoProcessException>
	implements KaleoProcessPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoProcessUtil</code> to access the kaleo process persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoProcessImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoProcess, NoSuchKaleoProcessException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the kaleo processes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo process in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUuid_First(
			String uuid, OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first kaleo process in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUuid_First(
		String uuid, OrderByComparator<KaleoProcess> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the kaleo processes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of kaleo processes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<KaleoProcess, NoSuchKaleoProcessException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the kaleo process where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchKaleoProcessException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUUID_G(String uuid, long groupId)
		throws NoSuchKaleoProcessException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the kaleo process where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the kaleo process where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kaleo process that was removed
	 */
	@Override
	public KaleoProcess removeByUUID_G(String uuid, long groupId)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = findByUUID_G(uuid, groupId);

		return remove(kaleoProcess);
	}

	/**
	 * Returns the number of kaleo processes where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<KaleoProcess, NoSuchKaleoProcessException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the kaleo processes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo process in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo process in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KaleoProcess> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo processes where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of kaleo processes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<KaleoProcess, NoSuchKaleoProcessException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the kaleo processes where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo processes
	 */
	@Override
	public List<KaleoProcess> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo process in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByGroupId_First(
			long groupId, OrderByComparator<KaleoProcess> orderByComparator)
		throws NoSuchKaleoProcessException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo process in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByGroupId_First(
		long groupId, OrderByComparator<KaleoProcess> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kaleo processes that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kaleo processes that the user has permission to view
	 */
	@Override
	public List<KaleoProcess> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the kaleo processes where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of kaleo processes where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of kaleo processes that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching kaleo processes that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private UniquePersistenceFinder<KaleoProcess, NoSuchKaleoProcessException>
		_uniquePersistenceFinderByDDLRecordSetId;

	/**
	 * Returns the kaleo process where DDLRecordSetId = &#63; or throws a <code>NoSuchKaleoProcessException</code> if it could not be found.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @return the matching kaleo process
	 * @throws NoSuchKaleoProcessException if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess findByDDLRecordSetId(long DDLRecordSetId)
		throws NoSuchKaleoProcessException {

		return _uniquePersistenceFinderByDDLRecordSetId.find(
			finderCache, new Object[] {DDLRecordSetId});
	}

	/**
	 * Returns the kaleo process where DDLRecordSetId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public KaleoProcess fetchByDDLRecordSetId(
		long DDLRecordSetId, boolean useFinderCache) {

		return _uniquePersistenceFinderByDDLRecordSetId.fetch(
			finderCache, new Object[] {DDLRecordSetId}, useFinderCache);
	}

	/**
	 * Removes the kaleo process where DDLRecordSetId = &#63; from the database.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @return the kaleo process that was removed
	 */
	@Override
	public KaleoProcess removeByDDLRecordSetId(long DDLRecordSetId)
		throws NoSuchKaleoProcessException {

		KaleoProcess kaleoProcess = findByDDLRecordSetId(DDLRecordSetId);

		return remove(kaleoProcess);
	}

	/**
	 * Returns the number of kaleo processes where DDLRecordSetId = &#63;.
	 *
	 * @param DDLRecordSetId the ddl record set ID
	 * @return the number of matching kaleo processes
	 */
	@Override
	public int countByDDLRecordSetId(long DDLRecordSetId) {
		return _uniquePersistenceFinderByDDLRecordSetId.count(
			finderCache, new Object[] {DDLRecordSetId});
	}

	public KaleoProcessPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KaleoProcess.class);

		setModelImplClass(KaleoProcessImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoProcessTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo process with the primary key. Does not add the kaleo process to the database.
	 *
	 * @param kaleoProcessId the primary key for the new kaleo process
	 * @return the new kaleo process
	 */
	@Override
	public KaleoProcess create(long kaleoProcessId) {
		KaleoProcess kaleoProcess = new KaleoProcessImpl();

		kaleoProcess.setNew(true);
		kaleoProcess.setPrimaryKey(kaleoProcessId);

		String uuid = PortalUUIDUtil.generate();

		kaleoProcess.setUuid(uuid);

		kaleoProcess.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoProcess;
	}

	/**
	 * Removes the kaleo process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process that was removed
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess remove(long kaleoProcessId)
		throws NoSuchKaleoProcessException {

		return remove((Serializable)kaleoProcessId);
	}

	@Override
	protected KaleoProcess removeImpl(KaleoProcess kaleoProcess) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoProcess)) {
				kaleoProcess = (KaleoProcess)session.get(
					KaleoProcessImpl.class, kaleoProcess.getPrimaryKeyObj());
			}

			if (kaleoProcess != null) {
				session.delete(kaleoProcess);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoProcess != null) {
			clearCache(kaleoProcess);
		}

		return kaleoProcess;
	}

	@Override
	public KaleoProcess updateImpl(KaleoProcess kaleoProcess) {
		boolean isNew = kaleoProcess.isNew();

		if (!(kaleoProcess instanceof KaleoProcessModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoProcess.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoProcess);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoProcess proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoProcess implementation " +
					kaleoProcess.getClass());
		}

		KaleoProcessModelImpl kaleoProcessModelImpl =
			(KaleoProcessModelImpl)kaleoProcess;

		if (Validator.isNull(kaleoProcess.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kaleoProcess.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoProcess.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoProcess.setCreateDate(date);
			}
			else {
				kaleoProcess.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoProcessModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoProcess.setModifiedDate(date);
			}
			else {
				kaleoProcess.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(kaleoProcess);
			}
			else {
				kaleoProcess = (KaleoProcess)session.merge(kaleoProcess);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoProcess, false);

		if (isNew) {
			kaleoProcess.setNew(false);
		}

		kaleoProcess.resetOriginalValues();

		return kaleoProcess;
	}

	/**
	 * Returns the kaleo process with the primary key or throws a <code>NoSuchKaleoProcessException</code> if it could not be found.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process
	 * @throws NoSuchKaleoProcessException if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess findByPrimaryKey(long kaleoProcessId)
		throws NoSuchKaleoProcessException {

		return findByPrimaryKey((Serializable)kaleoProcessId);
	}

	/**
	 * Returns the kaleo process with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process, or <code>null</code> if a kaleo process with the primary key could not be found
	 */
	@Override
	public KaleoProcess fetchByPrimaryKey(long kaleoProcessId) {
		return fetchByPrimaryKey((Serializable)kaleoProcessId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoProcessId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOPROCESS;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return KaleoProcessModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the kaleo process persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_KALEOPROCESS_WHERE, _SQL_COUNT_KALEOPROCESS_WHERE,
			KaleoProcessModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"kaleoProcess.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KaleoProcess::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(KaleoProcess::getUuid),
				KaleoProcess::getGroupId),
			_SQL_SELECT_KALEOPROCESS_WHERE, "",
			new FinderColumn<>(
				"kaleoProcess.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, KaleoProcess::getUuid),
			new FinderColumn<>(
				"kaleoProcess.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, KaleoProcess::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_KALEOPROCESS_WHERE, _SQL_COUNT_KALEOPROCESS_WHERE,
				KaleoProcessModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"kaleoProcess.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, KaleoProcess::getUuid),
				new FinderColumn<>(
					"kaleoProcess.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoProcess::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_KALEOPROCESS_WHERE, _SQL_COUNT_KALEOPROCESS_WHERE,
				KaleoProcessModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"kaleoProcess.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, KaleoProcess::getGroupId));

		_uniquePersistenceFinderByDDLRecordSetId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByDDLRecordSetId",
					new String[] {Long.class.getName()},
					new String[] {"DDLRecordSetId"}, 0, 0, false,
					KaleoProcess::getDDLRecordSetId),
				_SQL_SELECT_KALEOPROCESS_WHERE, "",
				new FinderColumn<>(
					"kaleoProcess.", "DDLRecordSetId", FinderColumn.Type.LONG,
					"=", true, true, KaleoProcess::getDDLRecordSetId));

		KaleoProcessUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoProcessUtil.setPersistence(null);

		entityCache.removeCache(KaleoProcessImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoFormsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		KaleoProcessModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOPROCESS =
		"SELECT kaleoProcess FROM KaleoProcess kaleoProcess";

	private static final String _SQL_SELECT_KALEOPROCESS_WHERE =
		"SELECT kaleoProcess FROM KaleoProcess kaleoProcess WHERE ";

	private static final String _SQL_COUNT_KALEOPROCESS_WHERE =
		"SELECT COUNT(kaleoProcess) FROM KaleoProcess kaleoProcess WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1178690584