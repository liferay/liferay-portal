/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.impl;

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
import com.liferay.portal.workflow.kaleo.exception.NoSuchTimerInstanceTokenException;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceTokenTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTimerInstanceTokenPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTimerInstanceTokenUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

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
 * The persistence implementation for the kaleo timer instance token service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoTimerInstanceTokenPersistence.class)
public class KaleoTimerInstanceTokenPersistenceImpl
	extends BasePersistenceImpl
		<KaleoTimerInstanceToken, NoSuchTimerInstanceTokenException>
	implements KaleoTimerInstanceTokenPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoTimerInstanceTokenUtil</code> to access the kaleo timer instance token persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoTimerInstanceTokenImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoTimerInstanceToken, NoSuchTimerInstanceTokenException>
			_collectionPersistenceFinderByKaleoInstanceId;

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	@Override
	public List<KaleoTimerInstanceToken> findByKaleoInstanceId(
		long kaleoInstanceId, int start, int end,
		OrderByComparator<KaleoTimerInstanceToken> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoInstanceId.find(
			finderCache, new Object[] {kaleoInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken findByKaleoInstanceId_First(
			long kaleoInstanceId,
			OrderByComparator<KaleoTimerInstanceToken> orderByComparator)
		throws NoSuchTimerInstanceTokenException {

		return _collectionPersistenceFinderByKaleoInstanceId.findFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token, or <code>null</code> if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken fetchByKaleoInstanceId_First(
		long kaleoInstanceId,
		OrderByComparator<KaleoTimerInstanceToken> orderByComparator) {

		return _collectionPersistenceFinderByKaleoInstanceId.fetchFirst(
			finderCache, new Object[] {kaleoInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo timer instance tokens where kaleoInstanceId = &#63; from the database.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 */
	@Override
	public void removeByKaleoInstanceId(long kaleoInstanceId) {
		_collectionPersistenceFinderByKaleoInstanceId.remove(
			finderCache, new Object[] {kaleoInstanceId});
	}

	/**
	 * Returns the number of kaleo timer instance tokens where kaleoInstanceId = &#63;.
	 *
	 * @param kaleoInstanceId the kaleo instance ID
	 * @return the number of matching kaleo timer instance tokens
	 */
	@Override
	public int countByKaleoInstanceId(long kaleoInstanceId) {
		return _collectionPersistenceFinderByKaleoInstanceId.count(
			finderCache, new Object[] {kaleoInstanceId});
	}

	private UniquePersistenceFinder
		<KaleoTimerInstanceToken, NoSuchTimerInstanceTokenException>
			_uniquePersistenceFinderByKITI_KTI;

	/**
	 * Returns the kaleo timer instance token where kaleoInstanceTokenId = &#63; and kaleoTimerId = &#63; or throws a <code>NoSuchTimerInstanceTokenException</code> if it could not be found.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param kaleoTimerId the kaleo timer ID
	 * @return the matching kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken findByKITI_KTI(
			long kaleoInstanceTokenId, long kaleoTimerId)
		throws NoSuchTimerInstanceTokenException {

		return _uniquePersistenceFinderByKITI_KTI.find(
			finderCache, new Object[] {kaleoInstanceTokenId, kaleoTimerId});
	}

	/**
	 * Returns the kaleo timer instance token where kaleoInstanceTokenId = &#63; and kaleoTimerId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param kaleoTimerId the kaleo timer ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo timer instance token, or <code>null</code> if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken fetchByKITI_KTI(
		long kaleoInstanceTokenId, long kaleoTimerId, boolean useFinderCache) {

		return _uniquePersistenceFinderByKITI_KTI.fetch(
			finderCache, new Object[] {kaleoInstanceTokenId, kaleoTimerId},
			useFinderCache);
	}

	/**
	 * Removes the kaleo timer instance token where kaleoInstanceTokenId = &#63; and kaleoTimerId = &#63; from the database.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param kaleoTimerId the kaleo timer ID
	 * @return the kaleo timer instance token that was removed
	 */
	@Override
	public KaleoTimerInstanceToken removeByKITI_KTI(
			long kaleoInstanceTokenId, long kaleoTimerId)
		throws NoSuchTimerInstanceTokenException {

		KaleoTimerInstanceToken kaleoTimerInstanceToken = findByKITI_KTI(
			kaleoInstanceTokenId, kaleoTimerId);

		return remove(kaleoTimerInstanceToken);
	}

	/**
	 * Returns the number of kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and kaleoTimerId = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param kaleoTimerId the kaleo timer ID
	 * @return the number of matching kaleo timer instance tokens
	 */
	@Override
	public int countByKITI_KTI(long kaleoInstanceTokenId, long kaleoTimerId) {
		return _uniquePersistenceFinderByKITI_KTI.count(
			finderCache, new Object[] {kaleoInstanceTokenId, kaleoTimerId});
	}

	private CollectionPersistenceFinder
		<KaleoTimerInstanceToken, NoSuchTimerInstanceTokenException>
			_collectionPersistenceFinderByKITI_C;

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	@Override
	public List<KaleoTimerInstanceToken> findByKITI_C(
		long kaleoInstanceTokenId, boolean completed, int start, int end,
		OrderByComparator<KaleoTimerInstanceToken> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKITI_C.find(
			finderCache, new Object[] {kaleoInstanceTokenId, completed}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken findByKITI_C_First(
			long kaleoInstanceTokenId, boolean completed,
			OrderByComparator<KaleoTimerInstanceToken> orderByComparator)
		throws NoSuchTimerInstanceTokenException {

		return _collectionPersistenceFinderByKITI_C.findFirst(
			finderCache, new Object[] {kaleoInstanceTokenId, completed},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token, or <code>null</code> if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken fetchByKITI_C_First(
		long kaleoInstanceTokenId, boolean completed,
		OrderByComparator<KaleoTimerInstanceToken> orderByComparator) {

		return _collectionPersistenceFinderByKITI_C.fetchFirst(
			finderCache, new Object[] {kaleoInstanceTokenId, completed},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 */
	@Override
	public void removeByKITI_C(long kaleoInstanceTokenId, boolean completed) {
		_collectionPersistenceFinderByKITI_C.remove(
			finderCache, new Object[] {kaleoInstanceTokenId, completed});
	}

	/**
	 * Returns the number of kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param completed the completed
	 * @return the number of matching kaleo timer instance tokens
	 */
	@Override
	public int countByKITI_C(long kaleoInstanceTokenId, boolean completed) {
		return _collectionPersistenceFinderByKITI_C.count(
			finderCache, new Object[] {kaleoInstanceTokenId, completed});
	}

	private CollectionPersistenceFinder
		<KaleoTimerInstanceToken, NoSuchTimerInstanceTokenException>
			_collectionPersistenceFinderByKITI_B_C;

	/**
	 * Returns an ordered range of all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo timer instance tokens
	 */
	@Override
	public List<KaleoTimerInstanceToken> findByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		int start, int end,
		OrderByComparator<KaleoTimerInstanceToken> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKITI_B_C.find(
			finderCache,
			new Object[] {kaleoInstanceTokenId, blocking, completed}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken findByKITI_B_C_First(
			long kaleoInstanceTokenId, boolean blocking, boolean completed,
			OrderByComparator<KaleoTimerInstanceToken> orderByComparator)
		throws NoSuchTimerInstanceTokenException {

		return _collectionPersistenceFinderByKITI_B_C.findFirst(
			finderCache,
			new Object[] {kaleoInstanceTokenId, blocking, completed},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo timer instance token in the ordered set where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo timer instance token, or <code>null</code> if a matching kaleo timer instance token could not be found
	 */
	@Override
	public KaleoTimerInstanceToken fetchByKITI_B_C_First(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		OrderByComparator<KaleoTimerInstanceToken> orderByComparator) {

		return _collectionPersistenceFinderByKITI_B_C.fetchFirst(
			finderCache,
			new Object[] {kaleoInstanceTokenId, blocking, completed},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63; from the database.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 */
	@Override
	public void removeByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed) {

		_collectionPersistenceFinderByKITI_B_C.remove(
			finderCache,
			new Object[] {kaleoInstanceTokenId, blocking, completed});
	}

	/**
	 * Returns the number of kaleo timer instance tokens where kaleoInstanceTokenId = &#63; and blocking = &#63; and completed = &#63;.
	 *
	 * @param kaleoInstanceTokenId the kaleo instance token ID
	 * @param blocking the blocking
	 * @param completed the completed
	 * @return the number of matching kaleo timer instance tokens
	 */
	@Override
	public int countByKITI_B_C(
		long kaleoInstanceTokenId, boolean blocking, boolean completed) {

		return _collectionPersistenceFinderByKITI_B_C.count(
			finderCache,
			new Object[] {kaleoInstanceTokenId, blocking, completed});
	}

	public KaleoTimerInstanceTokenPersistenceImpl() {
		setModelClass(KaleoTimerInstanceToken.class);

		setModelImplClass(KaleoTimerInstanceTokenImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoTimerInstanceTokenTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo timer instance token with the primary key. Does not add the kaleo timer instance token to the database.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key for the new kaleo timer instance token
	 * @return the new kaleo timer instance token
	 */
	@Override
	public KaleoTimerInstanceToken create(long kaleoTimerInstanceTokenId) {
		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			new KaleoTimerInstanceTokenImpl();

		kaleoTimerInstanceToken.setNew(true);
		kaleoTimerInstanceToken.setPrimaryKey(kaleoTimerInstanceTokenId);

		kaleoTimerInstanceToken.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoTimerInstanceToken;
	}

	/**
	 * Removes the kaleo timer instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token that was removed
	 * @throws NoSuchTimerInstanceTokenException if a kaleo timer instance token with the primary key could not be found
	 */
	@Override
	public KaleoTimerInstanceToken remove(long kaleoTimerInstanceTokenId)
		throws NoSuchTimerInstanceTokenException {

		return remove((Serializable)kaleoTimerInstanceTokenId);
	}

	@Override
	protected KaleoTimerInstanceToken removeImpl(
		KaleoTimerInstanceToken kaleoTimerInstanceToken) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoTimerInstanceToken)) {
				kaleoTimerInstanceToken = (KaleoTimerInstanceToken)session.get(
					KaleoTimerInstanceTokenImpl.class,
					kaleoTimerInstanceToken.getPrimaryKeyObj());
			}

			if ((kaleoTimerInstanceToken != null) &&
				ctPersistenceHelper.isRemove(kaleoTimerInstanceToken)) {

				session.delete(kaleoTimerInstanceToken);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoTimerInstanceToken != null) {
			clearCache(kaleoTimerInstanceToken);
		}

		return kaleoTimerInstanceToken;
	}

	@Override
	public KaleoTimerInstanceToken updateImpl(
		KaleoTimerInstanceToken kaleoTimerInstanceToken) {

		boolean isNew = kaleoTimerInstanceToken.isNew();

		if (!(kaleoTimerInstanceToken instanceof
				KaleoTimerInstanceTokenModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoTimerInstanceToken.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoTimerInstanceToken);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoTimerInstanceToken proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoTimerInstanceToken implementation " +
					kaleoTimerInstanceToken.getClass());
		}

		KaleoTimerInstanceTokenModelImpl kaleoTimerInstanceTokenModelImpl =
			(KaleoTimerInstanceTokenModelImpl)kaleoTimerInstanceToken;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoTimerInstanceToken.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoTimerInstanceToken.setCreateDate(date);
			}
			else {
				kaleoTimerInstanceToken.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoTimerInstanceTokenModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoTimerInstanceToken.setModifiedDate(date);
			}
			else {
				kaleoTimerInstanceToken.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoTimerInstanceToken)) {
				if (!isNew) {
					session.evict(
						KaleoTimerInstanceTokenImpl.class,
						kaleoTimerInstanceToken.getPrimaryKeyObj());
				}

				session.save(kaleoTimerInstanceToken);
			}
			else {
				kaleoTimerInstanceToken =
					(KaleoTimerInstanceToken)session.merge(
						kaleoTimerInstanceToken);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoTimerInstanceToken, false);

		if (isNew) {
			kaleoTimerInstanceToken.setNew(false);
		}

		kaleoTimerInstanceToken.resetOriginalValues();

		return kaleoTimerInstanceToken;
	}

	/**
	 * Returns the kaleo timer instance token with the primary key or throws a <code>NoSuchTimerInstanceTokenException</code> if it could not be found.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token
	 * @throws NoSuchTimerInstanceTokenException if a kaleo timer instance token with the primary key could not be found
	 */
	@Override
	public KaleoTimerInstanceToken findByPrimaryKey(
			long kaleoTimerInstanceTokenId)
		throws NoSuchTimerInstanceTokenException {

		return findByPrimaryKey((Serializable)kaleoTimerInstanceTokenId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo timer instance token with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token, or <code>null</code> if a kaleo timer instance token with the primary key could not be found
	 */
	@Override
	public KaleoTimerInstanceToken fetchByPrimaryKey(
		long kaleoTimerInstanceTokenId) {

		return fetchByPrimaryKey((Serializable)kaleoTimerInstanceTokenId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoTimerInstanceTokenId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOTIMERINSTANCETOKEN;
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
		return KaleoTimerInstanceTokenModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoTimerInstanceToken";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("kaleoClassName");
		ctMergeColumnNames.add("kaleoClassPK");
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoInstanceId");
		ctMergeColumnNames.add("kaleoInstanceTokenId");
		ctMergeColumnNames.add("kaleoTaskInstanceTokenId");
		ctMergeColumnNames.add("kaleoTimerId");
		ctMergeColumnNames.add("kaleoTimerName");
		ctMergeColumnNames.add("blocking");
		ctMergeColumnNames.add("completionUserId");
		ctMergeColumnNames.add("completed");
		ctMergeColumnNames.add("completionDate");
		ctMergeColumnNames.add("workflowContext");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoTimerInstanceTokenId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo timer instance token persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByKaleoInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoInstanceId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoInstanceId"}, false),
				_SQL_SELECT_KALEOTIMERINSTANCETOKEN_WHERE,
				_SQL_COUNT_KALEOTIMERINSTANCETOKEN_WHERE,
				KaleoTimerInstanceTokenModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTimerInstanceToken.", "kaleoInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTimerInstanceToken::getKaleoInstanceId));

		_uniquePersistenceFinderByKITI_KTI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByKITI_KTI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"kaleoInstanceTokenId", "kaleoTimerId"}, 0, 0,
				false, KaleoTimerInstanceToken::getKaleoInstanceTokenId,
				KaleoTimerInstanceToken::getKaleoTimerId),
			_SQL_SELECT_KALEOTIMERINSTANCETOKEN_WHERE, "",
			new FinderColumn<>(
				"kaleoTimerInstanceToken.", "kaleoInstanceTokenId",
				FinderColumn.Type.LONG, "=", true, true,
				KaleoTimerInstanceToken::getKaleoInstanceTokenId),
			new FinderColumn<>(
				"kaleoTimerInstanceToken.", "kaleoTimerId",
				FinderColumn.Type.LONG, "=", true, true,
				KaleoTimerInstanceToken::getKaleoTimerId));

		_collectionPersistenceFinderByKITI_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKITI_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoInstanceTokenId", "completed"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKITI_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"kaleoInstanceTokenId", "completed"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByKITI_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"kaleoInstanceTokenId", "completed"}, false),
				_SQL_SELECT_KALEOTIMERINSTANCETOKEN_WHERE,
				_SQL_COUNT_KALEOTIMERINSTANCETOKEN_WHERE,
				KaleoTimerInstanceTokenModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTimerInstanceToken.", "kaleoInstanceTokenId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTimerInstanceToken::getKaleoInstanceTokenId),
				new FinderColumn<>(
					"kaleoTimerInstanceToken.", "completed",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					KaleoTimerInstanceToken::isCompleted));

		_collectionPersistenceFinderByKITI_B_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKITI_B_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"kaleoInstanceTokenId", "blocking", "completed"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByKITI_B_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"kaleoInstanceTokenId", "blocking", "completed"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKITI_B_C",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"kaleoInstanceTokenId", "blocking", "completed"
					},
					false),
				_SQL_SELECT_KALEOTIMERINSTANCETOKEN_WHERE,
				_SQL_COUNT_KALEOTIMERINSTANCETOKEN_WHERE,
				KaleoTimerInstanceTokenModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"kaleoTimerInstanceToken.", "kaleoInstanceTokenId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTimerInstanceToken::getKaleoInstanceTokenId),
				new FinderColumn<>(
					"kaleoTimerInstanceToken.", "blocking",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					KaleoTimerInstanceToken::isBlocking),
				new FinderColumn<>(
					"kaleoTimerInstanceToken.", "completed",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					KaleoTimerInstanceToken::isCompleted));

		KaleoTimerInstanceTokenUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoTimerInstanceTokenUtil.setPersistence(null);

		entityCache.removeCache(KaleoTimerInstanceTokenImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		KaleoTimerInstanceTokenModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOTIMERINSTANCETOKEN =
		"SELECT kaleoTimerInstanceToken FROM KaleoTimerInstanceToken kaleoTimerInstanceToken";

	private static final String _SQL_SELECT_KALEOTIMERINSTANCETOKEN_WHERE =
		"SELECT kaleoTimerInstanceToken FROM KaleoTimerInstanceToken kaleoTimerInstanceToken WHERE ";

	private static final String _SQL_COUNT_KALEOTIMERINSTANCETOKEN_WHERE =
		"SELECT COUNT(kaleoTimerInstanceToken) FROM KaleoTimerInstanceToken kaleoTimerInstanceToken WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoTimerInstanceToken exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTimerInstanceTokenPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1000092084