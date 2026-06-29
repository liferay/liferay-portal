/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.exception.NoSuchPaymentMethodGroupRelException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelModelImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelUtil;
import com.liferay.commerce.payment.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

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
 * The persistence implementation for the commerce payment method group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommercePaymentMethodGroupRelPersistence.class)
public class CommercePaymentMethodGroupRelPersistenceImpl
	extends BasePersistenceImpl
		<CommercePaymentMethodGroupRel, NoSuchPaymentMethodGroupRelException>
	implements CommercePaymentMethodGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePaymentMethodGroupRelUtil</code> to access the commerce payment method group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePaymentMethodGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<CommercePaymentMethodGroupRel, NoSuchPaymentMethodGroupRelException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce payment method group rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByGroupId_First(
			long groupId,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the commerce payment method group rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce payment method group rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce payment method group rels
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce payment method group rels that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<CommercePaymentMethodGroupRel, NoSuchPaymentMethodGroupRelException>
			_collectionPersistenceFinderByG_A;

	/**
	 * Returns an ordered range of all the commerce payment method group rels where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment method group rels
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A.find(
			finderCache, new Object[] {groupId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByG_A_First(
			long groupId, boolean active,
			OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator)
		throws NoSuchPaymentMethodGroupRelException {

		return _collectionPersistenceFinderByG_A.findFirst(
			finderCache, new Object[] {groupId, active}, orderByComparator);
	}

	/**
	 * Returns the first commerce payment method group rel in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByG_A_First(
		long groupId, boolean active,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByG_A.fetchFirst(
			finderCache, new Object[] {groupId, active}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce payment method group rels that the user has permissions to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce payment method group rels
	 * @param end the upper bound of the range of commerce payment method group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public List<CommercePaymentMethodGroupRel> filterFindByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommercePaymentMethodGroupRel> orderByComparator) {

		return _collectionPersistenceFinderByG_A.filterFind(
			finderCache, new Object[] {groupId, active}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the commerce payment method group rels where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	@Override
	public void removeByG_A(long groupId, boolean active) {
		_collectionPersistenceFinderByG_A.remove(
			finderCache, new Object[] {groupId, active});
	}

	/**
	 * Returns the number of commerce payment method group rels where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce payment method group rels
	 */
	@Override
	public int countByG_A(long groupId, boolean active) {
		return _collectionPersistenceFinderByG_A.count(
			finderCache, new Object[] {groupId, active});
	}

	/**
	 * Returns the number of commerce payment method group rels that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce payment method group rels that the user has permission to view
	 */
	@Override
	public int filterCountByG_A(long groupId, boolean active) {
		return _collectionPersistenceFinderByG_A.filterCount(
			finderCache, new Object[] {groupId, active}, groupId);
	}

	private UniquePersistenceFinder
		<CommercePaymentMethodGroupRel, NoSuchPaymentMethodGroupRelException>
			_uniquePersistenceFinderByG_P;

	/**
	 * Returns the commerce payment method group rel where groupId = &#63; and paymentIntegrationKey = &#63; or throws a <code>NoSuchPaymentMethodGroupRelException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @return the matching commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByG_P(
			long groupId, String paymentIntegrationKey)
		throws NoSuchPaymentMethodGroupRelException {

		return _uniquePersistenceFinderByG_P.find(
			finderCache, new Object[] {groupId, paymentIntegrationKey});
	}

	/**
	 * Returns the commerce payment method group rel where groupId = &#63; and paymentIntegrationKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce payment method group rel, or <code>null</code> if a matching commerce payment method group rel could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByG_P(
		long groupId, String paymentIntegrationKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P.fetch(
			finderCache, new Object[] {groupId, paymentIntegrationKey},
			useFinderCache);
	}

	/**
	 * Removes the commerce payment method group rel where groupId = &#63; and paymentIntegrationKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @return the commerce payment method group rel that was removed
	 */
	@Override
	public CommercePaymentMethodGroupRel removeByG_P(
			long groupId, String paymentIntegrationKey)
		throws NoSuchPaymentMethodGroupRelException {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel = findByG_P(
			groupId, paymentIntegrationKey);

		return remove(commercePaymentMethodGroupRel);
	}

	/**
	 * Returns the number of commerce payment method group rels where groupId = &#63; and paymentIntegrationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param paymentIntegrationKey the payment integration key
	 * @return the number of matching commerce payment method group rels
	 */
	@Override
	public int countByG_P(long groupId, String paymentIntegrationKey) {
		return _uniquePersistenceFinderByG_P.count(
			finderCache, new Object[] {groupId, paymentIntegrationKey});
	}

	public CommercePaymentMethodGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commercePaymentMethodGroupRelId", "CPaymentMethodGroupRelId");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePaymentMethodGroupRel.class);

		setModelImplClass(CommercePaymentMethodGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePaymentMethodGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce payment method group rel with the primary key. Does not add the commerce payment method group rel to the database.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key for the new commerce payment method group rel
	 * @return the new commerce payment method group rel
	 */
	@Override
	public CommercePaymentMethodGroupRel create(
		long commercePaymentMethodGroupRelId) {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			new CommercePaymentMethodGroupRelImpl();

		commercePaymentMethodGroupRel.setNew(true);
		commercePaymentMethodGroupRel.setPrimaryKey(
			commercePaymentMethodGroupRelId);

		commercePaymentMethodGroupRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePaymentMethodGroupRel;
	}

	/**
	 * Removes the commerce payment method group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel that was removed
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel remove(
			long commercePaymentMethodGroupRelId)
		throws NoSuchPaymentMethodGroupRelException {

		return remove((Serializable)commercePaymentMethodGroupRelId);
	}

	@Override
	protected CommercePaymentMethodGroupRel removeImpl(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePaymentMethodGroupRel)) {
				commercePaymentMethodGroupRel =
					(CommercePaymentMethodGroupRel)session.get(
						CommercePaymentMethodGroupRelImpl.class,
						commercePaymentMethodGroupRel.getPrimaryKeyObj());
			}

			if (commercePaymentMethodGroupRel != null) {
				session.delete(commercePaymentMethodGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePaymentMethodGroupRel != null) {
			clearCache(commercePaymentMethodGroupRel);
		}

		return commercePaymentMethodGroupRel;
	}

	@Override
	public CommercePaymentMethodGroupRel updateImpl(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		boolean isNew = commercePaymentMethodGroupRel.isNew();

		if (!(commercePaymentMethodGroupRel instanceof
				CommercePaymentMethodGroupRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePaymentMethodGroupRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePaymentMethodGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePaymentMethodGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePaymentMethodGroupRel implementation " +
					commercePaymentMethodGroupRel.getClass());
		}

		CommercePaymentMethodGroupRelModelImpl
			commercePaymentMethodGroupRelModelImpl =
				(CommercePaymentMethodGroupRelModelImpl)
					commercePaymentMethodGroupRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commercePaymentMethodGroupRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commercePaymentMethodGroupRel.setCreateDate(date);
			}
			else {
				commercePaymentMethodGroupRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePaymentMethodGroupRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commercePaymentMethodGroupRel.setModifiedDate(date);
			}
			else {
				commercePaymentMethodGroupRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commercePaymentMethodGroupRel);
			}
			else {
				commercePaymentMethodGroupRel =
					(CommercePaymentMethodGroupRel)session.merge(
						commercePaymentMethodGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePaymentMethodGroupRel, false);

		if (isNew) {
			commercePaymentMethodGroupRel.setNew(false);
		}

		commercePaymentMethodGroupRel.resetOriginalValues();

		return commercePaymentMethodGroupRel;
	}

	/**
	 * Returns the commerce payment method group rel with the primary key or throws a <code>NoSuchPaymentMethodGroupRelException</code> if it could not be found.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel
	 * @throws NoSuchPaymentMethodGroupRelException if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel findByPrimaryKey(
			long commercePaymentMethodGroupRelId)
		throws NoSuchPaymentMethodGroupRelException {

		return findByPrimaryKey((Serializable)commercePaymentMethodGroupRelId);
	}

	/**
	 * Returns the commerce payment method group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePaymentMethodGroupRelId the primary key of the commerce payment method group rel
	 * @return the commerce payment method group rel, or <code>null</code> if a commerce payment method group rel with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRel fetchByPrimaryKey(
		long commercePaymentMethodGroupRelId) {

		return fetchByPrimaryKey((Serializable)commercePaymentMethodGroupRelId);
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
		return "CPaymentMethodGroupRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commercePaymentMethodGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommercePaymentMethodGroupRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce payment method group rel persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE,
				CommercePaymentMethodGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePaymentMethodGroupRel.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentMethodGroupRel::getGroupId));

		_collectionPersistenceFinderByG_A =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "active_"}, false),
				_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE,
				CommercePaymentMethodGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePaymentMethodGroupRel.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePaymentMethodGroupRel::getGroupId),
				new FinderColumn<>(
					"commercePaymentMethodGroupRel.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					CommercePaymentMethodGroupRel::isActive));

		_uniquePersistenceFinderByG_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "paymentIntegrationKey"}, 0, 2, false,
				CommercePaymentMethodGroupRel::getGroupId,
				convertNullFunction(
					CommercePaymentMethodGroupRel::getPaymentIntegrationKey)),
			_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE, "",
			new FinderColumn<>(
				"commercePaymentMethodGroupRel.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePaymentMethodGroupRel::getGroupId),
			new FinderColumn<>(
				"commercePaymentMethodGroupRel.", "paymentIntegrationKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePaymentMethodGroupRel::getPaymentIntegrationKey));

		CommercePaymentMethodGroupRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePaymentMethodGroupRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePaymentMethodGroupRelImpl.class.getName());
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
		CommercePaymentMethodGroupRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL =
		"SELECT commercePaymentMethodGroupRel FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel";

	private static final String
		_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPREL_WHERE =
			"SELECT commercePaymentMethodGroupRel FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEPAYMENTMETHODGROUPREL_WHERE =
		"SELECT COUNT(commercePaymentMethodGroupRel) FROM CommercePaymentMethodGroupRel commercePaymentMethodGroupRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePaymentMethodGroupRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentMethodGroupRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commercePaymentMethodGroupRelId", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1730241035