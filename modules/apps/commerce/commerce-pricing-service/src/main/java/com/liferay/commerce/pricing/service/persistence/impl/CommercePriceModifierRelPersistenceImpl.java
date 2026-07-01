/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.impl;

import com.liferay.commerce.pricing.exception.NoSuchPriceModifierRelException;
import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.model.CommercePriceModifierRelTable;
import com.liferay.commerce.pricing.model.impl.CommercePriceModifierRelImpl;
import com.liferay.commerce.pricing.model.impl.CommercePriceModifierRelModelImpl;
import com.liferay.commerce.pricing.service.persistence.CommercePriceModifierRelPersistence;
import com.liferay.commerce.pricing.service.persistence.CommercePriceModifierRelUtil;
import com.liferay.commerce.pricing.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the commerce price modifier rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Riccardo Alberti
 * @generated
 */
@Component(service = CommercePriceModifierRelPersistence.class)
public class CommercePriceModifierRelPersistenceImpl
	extends BasePersistenceImpl
		<CommercePriceModifierRel, NoSuchPriceModifierRelException>
	implements CommercePriceModifierRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePriceModifierRelUtil</code> to access the commerce price modifier rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePriceModifierRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePriceModifierRel, NoSuchPriceModifierRelException>
			_collectionPersistenceFinderByCommercePriceModifierId;

	/**
	 * Returns an ordered range of all the commerce price modifier rels where commercePriceModifierId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceModifierRelModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param start the lower bound of the range of commerce price modifier rels
	 * @param end the upper bound of the range of commerce price modifier rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price modifier rels
	 */
	@Override
	public List<CommercePriceModifierRel> findByCommercePriceModifierId(
		long commercePriceModifierId, int start, int end,
		OrderByComparator<CommercePriceModifierRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePriceModifierId.find(
			finderCache, new Object[] {commercePriceModifierId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price modifier rel in the ordered set where commercePriceModifierId = &#63;.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price modifier rel
	 * @throws NoSuchPriceModifierRelException if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel findByCommercePriceModifierId_First(
			long commercePriceModifierId,
			OrderByComparator<CommercePriceModifierRel> orderByComparator)
		throws NoSuchPriceModifierRelException {

		return _collectionPersistenceFinderByCommercePriceModifierId.findFirst(
			finderCache, new Object[] {commercePriceModifierId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce price modifier rel in the ordered set where commercePriceModifierId = &#63;.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price modifier rel, or <code>null</code> if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel fetchByCommercePriceModifierId_First(
		long commercePriceModifierId,
		OrderByComparator<CommercePriceModifierRel> orderByComparator) {

		return _collectionPersistenceFinderByCommercePriceModifierId.fetchFirst(
			finderCache, new Object[] {commercePriceModifierId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce price modifier rels where commercePriceModifierId = &#63; from the database.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 */
	@Override
	public void removeByCommercePriceModifierId(long commercePriceModifierId) {
		_collectionPersistenceFinderByCommercePriceModifierId.remove(
			finderCache, new Object[] {commercePriceModifierId});
	}

	/**
	 * Returns the number of commerce price modifier rels where commercePriceModifierId = &#63;.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @return the number of matching commerce price modifier rels
	 */
	@Override
	public int countByCommercePriceModifierId(long commercePriceModifierId) {
		return _collectionPersistenceFinderByCommercePriceModifierId.count(
			finderCache, new Object[] {commercePriceModifierId});
	}

	private CollectionPersistenceFinder
		<CommercePriceModifierRel, NoSuchPriceModifierRelException>
			_collectionPersistenceFinderByCPM_CN;

	/**
	 * Returns an ordered range of all the commerce price modifier rels where commercePriceModifierId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceModifierRelModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce price modifier rels
	 * @param end the upper bound of the range of commerce price modifier rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price modifier rels
	 */
	@Override
	public List<CommercePriceModifierRel> findByCPM_CN(
		long commercePriceModifierId, long classNameId, int start, int end,
		OrderByComparator<CommercePriceModifierRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPM_CN.find(
			finderCache, new Object[] {commercePriceModifierId, classNameId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price modifier rel in the ordered set where commercePriceModifierId = &#63; and classNameId = &#63;.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price modifier rel
	 * @throws NoSuchPriceModifierRelException if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel findByCPM_CN_First(
			long commercePriceModifierId, long classNameId,
			OrderByComparator<CommercePriceModifierRel> orderByComparator)
		throws NoSuchPriceModifierRelException {

		return _collectionPersistenceFinderByCPM_CN.findFirst(
			finderCache, new Object[] {commercePriceModifierId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce price modifier rel in the ordered set where commercePriceModifierId = &#63; and classNameId = &#63;.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price modifier rel, or <code>null</code> if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel fetchByCPM_CN_First(
		long commercePriceModifierId, long classNameId,
		OrderByComparator<CommercePriceModifierRel> orderByComparator) {

		return _collectionPersistenceFinderByCPM_CN.fetchFirst(
			finderCache, new Object[] {commercePriceModifierId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce price modifier rels where commercePriceModifierId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByCPM_CN(long commercePriceModifierId, long classNameId) {
		_collectionPersistenceFinderByCPM_CN.remove(
			finderCache, new Object[] {commercePriceModifierId, classNameId});
	}

	/**
	 * Returns the number of commerce price modifier rels where commercePriceModifierId = &#63; and classNameId = &#63;.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @return the number of matching commerce price modifier rels
	 */
	@Override
	public int countByCPM_CN(long commercePriceModifierId, long classNameId) {
		return _collectionPersistenceFinderByCPM_CN.count(
			finderCache, new Object[] {commercePriceModifierId, classNameId});
	}

	private CollectionPersistenceFinder
		<CommercePriceModifierRel, NoSuchPriceModifierRelException>
			_collectionPersistenceFinderByCN_CPK;

	/**
	 * Returns an ordered range of all the commerce price modifier rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceModifierRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce price modifier rels
	 * @param end the upper bound of the range of commerce price modifier rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price modifier rels
	 */
	@Override
	public List<CommercePriceModifierRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommercePriceModifierRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price modifier rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price modifier rel
	 * @throws NoSuchPriceModifierRelException if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<CommercePriceModifierRel> orderByComparator)
		throws NoSuchPriceModifierRelException {

		return _collectionPersistenceFinderByCN_CPK.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce price modifier rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price modifier rel, or <code>null</code> if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<CommercePriceModifierRel> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce price modifier rels where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByCN_CPK(long classNameId, long classPK) {
		_collectionPersistenceFinderByCN_CPK.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of commerce price modifier rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce price modifier rels
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _collectionPersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder
		<CommercePriceModifierRel, NoSuchPriceModifierRelException>
			_uniquePersistenceFinderByCPM_CN_CPK;

	/**
	 * Returns the commerce price modifier rel where commercePriceModifierId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchPriceModifierRelException</code> if it could not be found.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce price modifier rel
	 * @throws NoSuchPriceModifierRelException if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel findByCPM_CN_CPK(
			long commercePriceModifierId, long classNameId, long classPK)
		throws NoSuchPriceModifierRelException {

		return _uniquePersistenceFinderByCPM_CN_CPK.find(
			finderCache,
			new Object[] {commercePriceModifierId, classNameId, classPK});
	}

	/**
	 * Returns the commerce price modifier rel where commercePriceModifierId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce price modifier rel, or <code>null</code> if a matching commerce price modifier rel could not be found
	 */
	@Override
	public CommercePriceModifierRel fetchByCPM_CN_CPK(
		long commercePriceModifierId, long classNameId, long classPK,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCPM_CN_CPK.fetch(
			finderCache,
			new Object[] {commercePriceModifierId, classNameId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the commerce price modifier rel where commercePriceModifierId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the commerce price modifier rel that was removed
	 */
	@Override
	public CommercePriceModifierRel removeByCPM_CN_CPK(
			long commercePriceModifierId, long classNameId, long classPK)
		throws NoSuchPriceModifierRelException {

		CommercePriceModifierRel commercePriceModifierRel = findByCPM_CN_CPK(
			commercePriceModifierId, classNameId, classPK);

		return remove(commercePriceModifierRel);
	}

	/**
	 * Returns the number of commerce price modifier rels where commercePriceModifierId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commercePriceModifierId the commerce price modifier ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce price modifier rels
	 */
	@Override
	public int countByCPM_CN_CPK(
		long commercePriceModifierId, long classNameId, long classPK) {

		return _uniquePersistenceFinderByCPM_CN_CPK.count(
			finderCache,
			new Object[] {commercePriceModifierId, classNameId, classPK});
	}

	public CommercePriceModifierRelPersistenceImpl() {
		setModelClass(CommercePriceModifierRel.class);

		setModelImplClass(CommercePriceModifierRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePriceModifierRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce price modifier rel with the primary key. Does not add the commerce price modifier rel to the database.
	 *
	 * @param commercePriceModifierRelId the primary key for the new commerce price modifier rel
	 * @return the new commerce price modifier rel
	 */
	@Override
	public CommercePriceModifierRel create(long commercePriceModifierRelId) {
		CommercePriceModifierRel commercePriceModifierRel =
			new CommercePriceModifierRelImpl();

		commercePriceModifierRel.setNew(true);
		commercePriceModifierRel.setPrimaryKey(commercePriceModifierRelId);

		commercePriceModifierRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePriceModifierRel;
	}

	/**
	 * Removes the commerce price modifier rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePriceModifierRelId the primary key of the commerce price modifier rel
	 * @return the commerce price modifier rel that was removed
	 * @throws NoSuchPriceModifierRelException if a commerce price modifier rel with the primary key could not be found
	 */
	@Override
	public CommercePriceModifierRel remove(long commercePriceModifierRelId)
		throws NoSuchPriceModifierRelException {

		return remove((Serializable)commercePriceModifierRelId);
	}

	@Override
	protected CommercePriceModifierRel removeImpl(
		CommercePriceModifierRel commercePriceModifierRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePriceModifierRel)) {
				commercePriceModifierRel =
					(CommercePriceModifierRel)session.get(
						CommercePriceModifierRelImpl.class,
						commercePriceModifierRel.getPrimaryKeyObj());
			}

			if ((commercePriceModifierRel != null) &&
				ctPersistenceHelper.isRemove(commercePriceModifierRel)) {

				session.delete(commercePriceModifierRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePriceModifierRel != null) {
			clearCache(commercePriceModifierRel);
		}

		return commercePriceModifierRel;
	}

	@Override
	public CommercePriceModifierRel updateImpl(
		CommercePriceModifierRel commercePriceModifierRel) {

		boolean isNew = commercePriceModifierRel.isNew();

		if (!(commercePriceModifierRel instanceof
				CommercePriceModifierRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commercePriceModifierRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePriceModifierRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePriceModifierRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePriceModifierRel implementation " +
					commercePriceModifierRel.getClass());
		}

		CommercePriceModifierRelModelImpl commercePriceModifierRelModelImpl =
			(CommercePriceModifierRelModelImpl)commercePriceModifierRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePriceModifierRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePriceModifierRel.setCreateDate(date);
			}
			else {
				commercePriceModifierRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePriceModifierRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePriceModifierRel.setModifiedDate(date);
			}
			else {
				commercePriceModifierRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commercePriceModifierRel)) {
				if (!isNew) {
					session.evict(
						CommercePriceModifierRelImpl.class,
						commercePriceModifierRel.getPrimaryKeyObj());
				}

				session.save(commercePriceModifierRel);
			}
			else {
				commercePriceModifierRel =
					(CommercePriceModifierRel)session.merge(
						commercePriceModifierRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePriceModifierRel, false);

		if (isNew) {
			commercePriceModifierRel.setNew(false);
		}

		commercePriceModifierRel.resetOriginalValues();

		return commercePriceModifierRel;
	}

	/**
	 * Returns the commerce price modifier rel with the primary key or throws a <code>NoSuchPriceModifierRelException</code> if it could not be found.
	 *
	 * @param commercePriceModifierRelId the primary key of the commerce price modifier rel
	 * @return the commerce price modifier rel
	 * @throws NoSuchPriceModifierRelException if a commerce price modifier rel with the primary key could not be found
	 */
	@Override
	public CommercePriceModifierRel findByPrimaryKey(
			long commercePriceModifierRelId)
		throws NoSuchPriceModifierRelException {

		return findByPrimaryKey((Serializable)commercePriceModifierRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce price modifier rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePriceModifierRelId the primary key of the commerce price modifier rel
	 * @return the commerce price modifier rel, or <code>null</code> if a commerce price modifier rel with the primary key could not be found
	 */
	@Override
	public CommercePriceModifierRel fetchByPrimaryKey(
		long commercePriceModifierRelId) {

		return fetchByPrimaryKey((Serializable)commercePriceModifierRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commercePriceModifierRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPRICEMODIFIERREL;
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
		return CommercePriceModifierRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CommercePriceModifierRel";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("commercePriceModifierId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("commercePriceModifierRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"commercePriceModifierId", "classNameId", "classPK"});
	}

	/**
	 * Initializes the commerce price modifier rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommercePriceModifierId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePriceModifierId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePriceModifierId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePriceModifierId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceModifierId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePriceModifierId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceModifierId"}, false),
				_SQL_SELECT_COMMERCEPRICEMODIFIERREL_WHERE,
				_SQL_COUNT_COMMERCEPRICEMODIFIERREL_WHERE,
				CommercePriceModifierRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commercePriceModifierRel.", "commercePriceModifierId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceModifierRel::getCommercePriceModifierId));

		_collectionPersistenceFinderByCPM_CN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCPM_CN",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePriceModifierId", "classNameId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCPM_CN",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"commercePriceModifierId", "classNameId"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCPM_CN",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"commercePriceModifierId", "classNameId"},
					false),
				_SQL_SELECT_COMMERCEPRICEMODIFIERREL_WHERE,
				_SQL_COUNT_COMMERCEPRICEMODIFIERREL_WHERE,
				CommercePriceModifierRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commercePriceModifierRel.", "commercePriceModifierId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceModifierRel::getCommercePriceModifierId),
				new FinderColumn<>(
					"commercePriceModifierRel.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceModifierRel::getClassNameId));

		_collectionPersistenceFinderByCN_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, false),
				_SQL_SELECT_COMMERCEPRICEMODIFIERREL_WHERE,
				_SQL_COUNT_COMMERCEPRICEMODIFIERREL_WHERE,
				CommercePriceModifierRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commercePriceModifierRel.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceModifierRel::getClassNameId),
				new FinderColumn<>(
					"commercePriceModifierRel.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceModifierRel::getClassPK));

		_uniquePersistenceFinderByCPM_CN_CPK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCPM_CN_CPK",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"commercePriceModifierId", "classNameId", "classPK"
				},
				0, 0, false,
				CommercePriceModifierRel::getCommercePriceModifierId,
				CommercePriceModifierRel::getClassNameId,
				CommercePriceModifierRel::getClassPK),
			_SQL_SELECT_COMMERCEPRICEMODIFIERREL_WHERE, "",
			new FinderColumn<>(
				"commercePriceModifierRel.", "commercePriceModifierId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceModifierRel::getCommercePriceModifierId),
			new FinderColumn<>(
				"commercePriceModifierRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePriceModifierRel::getClassNameId),
			new FinderColumn<>(
				"commercePriceModifierRel.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, CommercePriceModifierRel::getClassPK));

		CommercePriceModifierRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePriceModifierRelUtil.setPersistence(null);

		entityCache.removeCache(CommercePriceModifierRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CommercePriceModifierRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPRICEMODIFIERREL =
		"SELECT commercePriceModifierRel FROM CommercePriceModifierRel commercePriceModifierRel";

	private static final String _SQL_SELECT_COMMERCEPRICEMODIFIERREL_WHERE =
		"SELECT commercePriceModifierRel FROM CommercePriceModifierRel commercePriceModifierRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEPRICEMODIFIERREL_WHERE =
		"SELECT COUNT(commercePriceModifierRel) FROM CommercePriceModifierRel commercePriceModifierRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1112421471