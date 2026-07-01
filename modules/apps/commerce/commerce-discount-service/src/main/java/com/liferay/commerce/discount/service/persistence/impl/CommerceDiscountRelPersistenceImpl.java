/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.commerce.discount.exception.NoSuchDiscountRelException;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.model.CommerceDiscountRelTable;
import com.liferay.commerce.discount.model.impl.CommerceDiscountRelImpl;
import com.liferay.commerce.discount.model.impl.CommerceDiscountRelModelImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRelPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRelUtil;
import com.liferay.commerce.discount.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
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
 * The persistence implementation for the commerce discount rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceDiscountRelPersistence.class)
public class CommerceDiscountRelPersistenceImpl
	extends BasePersistenceImpl<CommerceDiscountRel, NoSuchDiscountRelException>
	implements CommerceDiscountRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceDiscountRelUtil</code> to access the commerce discount rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceDiscountRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceDiscountRel, NoSuchDiscountRelException>
			_collectionPersistenceFinderByCommerceDiscountId;

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	@Override
	public List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceDiscountId.find(
			finderCache, new Object[] {commerceDiscountId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel findByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException {

		return _collectionPersistenceFinderByCommerceDiscountId.findFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceDiscountId.fetchFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	@Override
	public void removeByCommerceDiscountId(long commerceDiscountId) {
		_collectionPersistenceFinderByCommerceDiscountId.remove(
			finderCache, new Object[] {commerceDiscountId});
	}

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount rels
	 */
	@Override
	public int countByCommerceDiscountId(long commerceDiscountId) {
		return _collectionPersistenceFinderByCommerceDiscountId.count(
			finderCache, new Object[] {commerceDiscountId});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountRel, NoSuchDiscountRelException>
			_collectionPersistenceFinderByCD_CN;

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	@Override
	public List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCD_CN.find(
			finderCache, new Object[] {commerceDiscountId, classNameId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel findByCD_CN_First(
			long commerceDiscountId, long classNameId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException {

		return _collectionPersistenceFinderByCD_CN.findFirst(
			finderCache, new Object[] {commerceDiscountId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel fetchByCD_CN_First(
		long commerceDiscountId, long classNameId,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return _collectionPersistenceFinderByCD_CN.fetchFirst(
			finderCache, new Object[] {commerceDiscountId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByCD_CN(long commerceDiscountId, long classNameId) {
		_collectionPersistenceFinderByCD_CN.remove(
			finderCache, new Object[] {commerceDiscountId, classNameId});
	}

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @return the number of matching commerce discount rels
	 */
	@Override
	public int countByCD_CN(long commerceDiscountId, long classNameId) {
		return _collectionPersistenceFinderByCD_CN.count(
			finderCache, new Object[] {commerceDiscountId, classNameId});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountRel, NoSuchDiscountRelException>
			_collectionPersistenceFinderByCN_CPK;

	/**
	 * Returns an ordered range of all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	@Override
	public List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException {

		return _collectionPersistenceFinderByCN_CPK.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce discount rels
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _collectionPersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountRel, NoSuchDiscountRelException>
			_collectionPersistenceFinderByCD_CN_CPK;

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	@Override
	public List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK, int start,
		int end, OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCD_CN_CPK.find(
			finderCache,
			new Object[] {commerceDiscountId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel findByCD_CN_CPK_First(
			long commerceDiscountId, long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException {

		return _collectionPersistenceFinderByCD_CN_CPK.findFirst(
			finderCache,
			new Object[] {commerceDiscountId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	@Override
	public CommerceDiscountRel fetchByCD_CN_CPK_First(
		long commerceDiscountId, long classNameId, long classPK,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return _collectionPersistenceFinderByCD_CN_CPK.fetchFirst(
			finderCache,
			new Object[] {commerceDiscountId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK) {

		_collectionPersistenceFinderByCD_CN_CPK.remove(
			finderCache,
			new Object[] {commerceDiscountId, classNameId, classPK});
	}

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce discount rels
	 */
	@Override
	public int countByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByCD_CN_CPK.count(
			finderCache,
			new Object[] {commerceDiscountId, classNameId, classPK});
	}

	public CommerceDiscountRelPersistenceImpl() {
		setModelClass(CommerceDiscountRel.class);

		setModelImplClass(CommerceDiscountRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceDiscountRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce discount rel with the primary key. Does not add the commerce discount rel to the database.
	 *
	 * @param commerceDiscountRelId the primary key for the new commerce discount rel
	 * @return the new commerce discount rel
	 */
	@Override
	public CommerceDiscountRel create(long commerceDiscountRelId) {
		CommerceDiscountRel commerceDiscountRel = new CommerceDiscountRelImpl();

		commerceDiscountRel.setNew(true);
		commerceDiscountRel.setPrimaryKey(commerceDiscountRelId);

		commerceDiscountRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceDiscountRel;
	}

	/**
	 * Removes the commerce discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel that was removed
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountRel remove(long commerceDiscountRelId)
		throws NoSuchDiscountRelException {

		return remove((Serializable)commerceDiscountRelId);
	}

	@Override
	protected CommerceDiscountRel removeImpl(
		CommerceDiscountRel commerceDiscountRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceDiscountRel)) {
				commerceDiscountRel = (CommerceDiscountRel)session.get(
					CommerceDiscountRelImpl.class,
					commerceDiscountRel.getPrimaryKeyObj());
			}

			if (commerceDiscountRel != null) {
				session.delete(commerceDiscountRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceDiscountRel != null) {
			clearCache(commerceDiscountRel);
		}

		return commerceDiscountRel;
	}

	@Override
	public CommerceDiscountRel updateImpl(
		CommerceDiscountRel commerceDiscountRel) {

		boolean isNew = commerceDiscountRel.isNew();

		if (!(commerceDiscountRel instanceof CommerceDiscountRelModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceDiscountRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceDiscountRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceDiscountRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceDiscountRel implementation " +
					commerceDiscountRel.getClass());
		}

		CommerceDiscountRelModelImpl commerceDiscountRelModelImpl =
			(CommerceDiscountRelModelImpl)commerceDiscountRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceDiscountRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceDiscountRel.setCreateDate(date);
			}
			else {
				commerceDiscountRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceDiscountRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceDiscountRel.setModifiedDate(date);
			}
			else {
				commerceDiscountRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceDiscountRel);
			}
			else {
				commerceDiscountRel = (CommerceDiscountRel)session.merge(
					commerceDiscountRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceDiscountRel, false);

		if (isNew) {
			commerceDiscountRel.setNew(false);
		}

		commerceDiscountRel.resetOriginalValues();

		return commerceDiscountRel;
	}

	/**
	 * Returns the commerce discount rel with the primary key or throws a <code>NoSuchDiscountRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountRel findByPrimaryKey(long commerceDiscountRelId)
		throws NoSuchDiscountRelException {

		return findByPrimaryKey((Serializable)commerceDiscountRelId);
	}

	/**
	 * Returns the commerce discount rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel, or <code>null</code> if a commerce discount rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountRel fetchByPrimaryKey(long commerceDiscountRelId) {
		return fetchByPrimaryKey((Serializable)commerceDiscountRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceDiscountRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEDISCOUNTREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceDiscountRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce discount rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommerceDiscountId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceDiscountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceDiscountId"}, false),
				_SQL_SELECT_COMMERCEDISCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTREL_WHERE,
				CommerceDiscountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountRel.", "commerceDiscountId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountRel::getCommerceDiscountId));

		_collectionPersistenceFinderByCD_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCD_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceDiscountId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCD_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceDiscountId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCD_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceDiscountId", "classNameId"}, false),
			_SQL_SELECT_COMMERCEDISCOUNTREL_WHERE,
			_SQL_COUNT_COMMERCEDISCOUNTREL_WHERE,
			CommerceDiscountRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceDiscountRel.", "commerceDiscountId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceDiscountRel::getCommerceDiscountId),
			new FinderColumn<>(
				"commerceDiscountRel.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, CommerceDiscountRel::getClassNameId));

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
				_SQL_SELECT_COMMERCEDISCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTREL_WHERE,
				CommerceDiscountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountRel.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountRel::getClassNameId),
				new FinderColumn<>(
					"commerceDiscountRel.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CommerceDiscountRel::getClassPK));

		_collectionPersistenceFinderByCD_CN_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCD_CN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"commerceDiscountId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCD_CN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"commerceDiscountId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCD_CN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"commerceDiscountId", "classNameId", "classPK"
					},
					false),
				_SQL_SELECT_COMMERCEDISCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTREL_WHERE,
				CommerceDiscountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountRel.", "commerceDiscountId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountRel::getCommerceDiscountId),
				new FinderColumn<>(
					"commerceDiscountRel.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountRel::getClassNameId),
				new FinderColumn<>(
					"commerceDiscountRel.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, CommerceDiscountRel::getClassPK));

		CommerceDiscountRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceDiscountRelUtil.setPersistence(null);

		entityCache.removeCache(CommerceDiscountRelImpl.class.getName());
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
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CommerceDiscountRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTREL =
		"SELECT commerceDiscountRel FROM CommerceDiscountRel commerceDiscountRel";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTREL_WHERE =
		"SELECT commerceDiscountRel FROM CommerceDiscountRel commerceDiscountRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEDISCOUNTREL_WHERE =
		"SELECT COUNT(commerceDiscountRel) FROM CommerceDiscountRel commerceDiscountRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2135550386