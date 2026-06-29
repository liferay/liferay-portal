/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.impl;

import com.liferay.commerce.payment.exception.NoSuchPaymentMethodGroupRelQualifierException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifierTable;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierImpl;
import com.liferay.commerce.payment.model.impl.CommercePaymentMethodGroupRelQualifierModelImpl;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelQualifierPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelQualifierUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
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
 * The persistence implementation for the commerce payment method group rel qualifier service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @generated
 */
@Component(service = CommercePaymentMethodGroupRelQualifierPersistence.class)
public class CommercePaymentMethodGroupRelQualifierPersistenceImpl
	extends BasePersistenceImpl
		<CommercePaymentMethodGroupRelQualifier,
		 NoSuchPaymentMethodGroupRelQualifierException>
	implements CommercePaymentMethodGroupRelQualifierPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePaymentMethodGroupRelQualifierUtil</code> to access the commerce payment method group rel qualifier persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePaymentMethodGroupRelQualifierImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePaymentMethodGroupRelQualifier,
		 NoSuchPaymentMethodGroupRelQualifierException>
			_collectionPersistenceFinderByCommercePaymentMethodGroupRelId;

	/**
	 * Returns an ordered range of all the commerce payment method group rel qualifiers where commercePaymentMethodGroupRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @param start the lower bound of the range of commerce payment method group rel qualifiers
	 * @param end the upper bound of the range of commerce payment method group rel qualifiers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment method group rel qualifiers
	 */
	@Override
	public List<CommercePaymentMethodGroupRelQualifier>
		findByCommercePaymentMethodGroupRelId(
			long commercePaymentMethodGroupRelId, int start, int end,
			OrderByComparator<CommercePaymentMethodGroupRelQualifier>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePaymentMethodGroupRelId.
			find(
				finderCache, new Object[] {commercePaymentMethodGroupRelId},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment method group rel qualifier in the ordered set where commercePaymentMethodGroupRelId = &#63;.
	 *
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel qualifier
	 * @throws NoSuchPaymentMethodGroupRelQualifierException if a matching commerce payment method group rel qualifier could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier
			findByCommercePaymentMethodGroupRelId_First(
				long commercePaymentMethodGroupRelId,
				OrderByComparator<CommercePaymentMethodGroupRelQualifier>
					orderByComparator)
		throws NoSuchPaymentMethodGroupRelQualifierException {

		return _collectionPersistenceFinderByCommercePaymentMethodGroupRelId.
			findFirst(
				finderCache, new Object[] {commercePaymentMethodGroupRelId},
				orderByComparator);
	}

	/**
	 * Returns the first commerce payment method group rel qualifier in the ordered set where commercePaymentMethodGroupRelId = &#63;.
	 *
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel qualifier, or <code>null</code> if a matching commerce payment method group rel qualifier could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier
		fetchByCommercePaymentMethodGroupRelId_First(
			long commercePaymentMethodGroupRelId,
			OrderByComparator<CommercePaymentMethodGroupRelQualifier>
				orderByComparator) {

		return _collectionPersistenceFinderByCommercePaymentMethodGroupRelId.
			fetchFirst(
				finderCache, new Object[] {commercePaymentMethodGroupRelId},
				orderByComparator);
	}

	/**
	 * Removes all the commerce payment method group rel qualifiers where commercePaymentMethodGroupRelId = &#63; from the database.
	 *
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 */
	@Override
	public void removeByCommercePaymentMethodGroupRelId(
		long commercePaymentMethodGroupRelId) {

		_collectionPersistenceFinderByCommercePaymentMethodGroupRelId.remove(
			finderCache, new Object[] {commercePaymentMethodGroupRelId});
	}

	/**
	 * Returns the number of commerce payment method group rel qualifiers where commercePaymentMethodGroupRelId = &#63;.
	 *
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @return the number of matching commerce payment method group rel qualifiers
	 */
	@Override
	public int countByCommercePaymentMethodGroupRelId(
		long commercePaymentMethodGroupRelId) {

		return _collectionPersistenceFinderByCommercePaymentMethodGroupRelId.
			count(finderCache, new Object[] {commercePaymentMethodGroupRelId});
	}

	private CollectionPersistenceFinder
		<CommercePaymentMethodGroupRelQualifier,
		 NoSuchPaymentMethodGroupRelQualifierException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce payment method group rel qualifiers where classNameId = &#63; and commercePaymentMethodGroupRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePaymentMethodGroupRelQualifierModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @param start the lower bound of the range of commerce payment method group rel qualifiers
	 * @param end the upper bound of the range of commerce payment method group rel qualifiers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce payment method group rel qualifiers
	 */
	@Override
	public List<CommercePaymentMethodGroupRelQualifier> findByC_C(
		long classNameId, long commercePaymentMethodGroupRelId, int start,
		int end,
		OrderByComparator<CommercePaymentMethodGroupRelQualifier>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache,
			new Object[] {classNameId, commercePaymentMethodGroupRelId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce payment method group rel qualifier in the ordered set where classNameId = &#63; and commercePaymentMethodGroupRelId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel qualifier
	 * @throws NoSuchPaymentMethodGroupRelQualifierException if a matching commerce payment method group rel qualifier could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier findByC_C_First(
			long classNameId, long commercePaymentMethodGroupRelId,
			OrderByComparator<CommercePaymentMethodGroupRelQualifier>
				orderByComparator)
		throws NoSuchPaymentMethodGroupRelQualifierException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache,
			new Object[] {classNameId, commercePaymentMethodGroupRelId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce payment method group rel qualifier in the ordered set where classNameId = &#63; and commercePaymentMethodGroupRelId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce payment method group rel qualifier, or <code>null</code> if a matching commerce payment method group rel qualifier could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier fetchByC_C_First(
		long classNameId, long commercePaymentMethodGroupRelId,
		OrderByComparator<CommercePaymentMethodGroupRelQualifier>
			orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache,
			new Object[] {classNameId, commercePaymentMethodGroupRelId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce payment method group rel qualifiers where classNameId = &#63; and commercePaymentMethodGroupRelId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 */
	@Override
	public void removeByC_C(
		long classNameId, long commercePaymentMethodGroupRelId) {

		_collectionPersistenceFinderByC_C.remove(
			finderCache,
			new Object[] {classNameId, commercePaymentMethodGroupRelId});
	}

	/**
	 * Returns the number of commerce payment method group rel qualifiers where classNameId = &#63; and commercePaymentMethodGroupRelId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @return the number of matching commerce payment method group rel qualifiers
	 */
	@Override
	public int countByC_C(
		long classNameId, long commercePaymentMethodGroupRelId) {

		return _collectionPersistenceFinderByC_C.count(
			finderCache,
			new Object[] {classNameId, commercePaymentMethodGroupRelId});
	}

	private UniquePersistenceFinder
		<CommercePaymentMethodGroupRelQualifier,
		 NoSuchPaymentMethodGroupRelQualifierException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the commerce payment method group rel qualifier where classNameId = &#63; and classPK = &#63; and commercePaymentMethodGroupRelId = &#63; or throws a <code>NoSuchPaymentMethodGroupRelQualifierException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @return the matching commerce payment method group rel qualifier
	 * @throws NoSuchPaymentMethodGroupRelQualifierException if a matching commerce payment method group rel qualifier could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier findByC_C_C(
			long classNameId, long classPK,
			long commercePaymentMethodGroupRelId)
		throws NoSuchPaymentMethodGroupRelQualifierException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {
				classNameId, classPK, commercePaymentMethodGroupRelId
			});
	}

	/**
	 * Returns the commerce payment method group rel qualifier where classNameId = &#63; and classPK = &#63; and commercePaymentMethodGroupRelId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce payment method group rel qualifier, or <code>null</code> if a matching commerce payment method group rel qualifier could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier fetchByC_C_C(
		long classNameId, long classPK, long commercePaymentMethodGroupRelId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {
				classNameId, classPK, commercePaymentMethodGroupRelId
			},
			useFinderCache);
	}

	/**
	 * Removes the commerce payment method group rel qualifier where classNameId = &#63; and classPK = &#63; and commercePaymentMethodGroupRelId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @return the commerce payment method group rel qualifier that was removed
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier removeByC_C_C(
			long classNameId, long classPK,
			long commercePaymentMethodGroupRelId)
		throws NoSuchPaymentMethodGroupRelQualifierException {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier = findByC_C_C(
				classNameId, classPK, commercePaymentMethodGroupRelId);

		return remove(commercePaymentMethodGroupRelQualifier);
	}

	/**
	 * Returns the number of commerce payment method group rel qualifiers where classNameId = &#63; and classPK = &#63; and commercePaymentMethodGroupRelId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commercePaymentMethodGroupRelId the commerce payment method group rel ID
	 * @return the number of matching commerce payment method group rel qualifiers
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long commercePaymentMethodGroupRelId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {
				classNameId, classPK, commercePaymentMethodGroupRelId
			});
	}

	public CommercePaymentMethodGroupRelQualifierPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commercePaymentMethodGroupRelQualifierId",
			"CPMethodGroupRelQualifierId");
		dbColumnNames.put(
			"commercePaymentMethodGroupRelId", "CPaymentMethodGroupRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePaymentMethodGroupRelQualifier.class);

		setModelImplClass(CommercePaymentMethodGroupRelQualifierImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePaymentMethodGroupRelQualifierTable.INSTANCE);
	}

	/**
	 * Creates a new commerce payment method group rel qualifier with the primary key. Does not add the commerce payment method group rel qualifier to the database.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key for the new commerce payment method group rel qualifier
	 * @return the new commerce payment method group rel qualifier
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier create(
		long commercePaymentMethodGroupRelQualifierId) {

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier =
				new CommercePaymentMethodGroupRelQualifierImpl();

		commercePaymentMethodGroupRelQualifier.setNew(true);
		commercePaymentMethodGroupRelQualifier.setPrimaryKey(
			commercePaymentMethodGroupRelQualifierId);

		commercePaymentMethodGroupRelQualifier.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePaymentMethodGroupRelQualifier;
	}

	/**
	 * Removes the commerce payment method group rel qualifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier that was removed
	 * @throws NoSuchPaymentMethodGroupRelQualifierException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier remove(
			long commercePaymentMethodGroupRelQualifierId)
		throws NoSuchPaymentMethodGroupRelQualifierException {

		return remove((Serializable)commercePaymentMethodGroupRelQualifierId);
	}

	@Override
	protected CommercePaymentMethodGroupRelQualifier removeImpl(
		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePaymentMethodGroupRelQualifier)) {
				commercePaymentMethodGroupRelQualifier =
					(CommercePaymentMethodGroupRelQualifier)session.get(
						CommercePaymentMethodGroupRelQualifierImpl.class,
						commercePaymentMethodGroupRelQualifier.
							getPrimaryKeyObj());
			}

			if (commercePaymentMethodGroupRelQualifier != null) {
				session.delete(commercePaymentMethodGroupRelQualifier);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePaymentMethodGroupRelQualifier != null) {
			clearCache(commercePaymentMethodGroupRelQualifier);
		}

		return commercePaymentMethodGroupRelQualifier;
	}

	@Override
	public CommercePaymentMethodGroupRelQualifier updateImpl(
		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier) {

		boolean isNew = commercePaymentMethodGroupRelQualifier.isNew();

		if (!(commercePaymentMethodGroupRelQualifier instanceof
				CommercePaymentMethodGroupRelQualifierModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePaymentMethodGroupRelQualifier.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePaymentMethodGroupRelQualifier);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePaymentMethodGroupRelQualifier proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePaymentMethodGroupRelQualifier implementation " +
					commercePaymentMethodGroupRelQualifier.getClass());
		}

		CommercePaymentMethodGroupRelQualifierModelImpl
			commercePaymentMethodGroupRelQualifierModelImpl =
				(CommercePaymentMethodGroupRelQualifierModelImpl)
					commercePaymentMethodGroupRelQualifier;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commercePaymentMethodGroupRelQualifier.getCreateDate() == null)) {

			if (serviceContext == null) {
				commercePaymentMethodGroupRelQualifier.setCreateDate(date);
			}
			else {
				commercePaymentMethodGroupRelQualifier.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePaymentMethodGroupRelQualifierModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				commercePaymentMethodGroupRelQualifier.setModifiedDate(date);
			}
			else {
				commercePaymentMethodGroupRelQualifier.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commercePaymentMethodGroupRelQualifier);
			}
			else {
				commercePaymentMethodGroupRelQualifier =
					(CommercePaymentMethodGroupRelQualifier)session.merge(
						commercePaymentMethodGroupRelQualifier);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commercePaymentMethodGroupRelQualifier, false);

		if (isNew) {
			commercePaymentMethodGroupRelQualifier.setNew(false);
		}

		commercePaymentMethodGroupRelQualifier.resetOriginalValues();

		return commercePaymentMethodGroupRelQualifier;
	}

	/**
	 * Returns the commerce payment method group rel qualifier with the primary key or throws a <code>NoSuchPaymentMethodGroupRelQualifierException</code> if it could not be found.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier
	 * @throws NoSuchPaymentMethodGroupRelQualifierException if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier findByPrimaryKey(
			long commercePaymentMethodGroupRelQualifierId)
		throws NoSuchPaymentMethodGroupRelQualifierException {

		return findByPrimaryKey(
			(Serializable)commercePaymentMethodGroupRelQualifierId);
	}

	/**
	 * Returns the commerce payment method group rel qualifier with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePaymentMethodGroupRelQualifierId the primary key of the commerce payment method group rel qualifier
	 * @return the commerce payment method group rel qualifier, or <code>null</code> if a commerce payment method group rel qualifier with the primary key could not be found
	 */
	@Override
	public CommercePaymentMethodGroupRelQualifier fetchByPrimaryKey(
		long commercePaymentMethodGroupRelQualifierId) {

		return fetchByPrimaryKey(
			(Serializable)commercePaymentMethodGroupRelQualifierId);
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
		return "CPMethodGroupRelQualifierId";
	}

	@Override
	protected String getPKFieldName() {
		return "commercePaymentMethodGroupRelQualifierId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommercePaymentMethodGroupRelQualifierModelImpl.
			TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce payment method group rel qualifier persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCommercePaymentMethodGroupRelId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePaymentMethodGroupRelId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPaymentMethodGroupRelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePaymentMethodGroupRelId",
					new String[] {Long.class.getName()},
					new String[] {"CPaymentMethodGroupRelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePaymentMethodGroupRelId",
					new String[] {Long.class.getName()},
					new String[] {"CPaymentMethodGroupRelId"}, false),
				_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER_WHERE,
				_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER_WHERE,
				CommercePaymentMethodGroupRelQualifierModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePaymentMethodGroupRelQualifier.",
					"commercePaymentMethodGroupRelId",
					"CPaymentMethodGroupRelId", FinderColumn.Type.LONG, "=",
					true, true,
					CommercePaymentMethodGroupRelQualifier::
						getCommercePaymentMethodGroupRelId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "CPaymentMethodGroupRelId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "CPaymentMethodGroupRelId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "CPaymentMethodGroupRelId"},
				false),
			_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER_WHERE,
			_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER_WHERE,
			CommercePaymentMethodGroupRelQualifierModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commercePaymentMethodGroupRelQualifier.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePaymentMethodGroupRelQualifier::getClassNameId),
			new FinderColumn<>(
				"commercePaymentMethodGroupRelQualifier.",
				"commercePaymentMethodGroupRelId", "CPaymentMethodGroupRelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePaymentMethodGroupRelQualifier::
					getCommercePaymentMethodGroupRelId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"classNameId", "classPK", "CPaymentMethodGroupRelId"
				},
				0, 0, false,
				CommercePaymentMethodGroupRelQualifier::getClassNameId,
				CommercePaymentMethodGroupRelQualifier::getClassPK,
				CommercePaymentMethodGroupRelQualifier::
					getCommercePaymentMethodGroupRelId),
			_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER_WHERE, "",
			new FinderColumn<>(
				"commercePaymentMethodGroupRelQualifier.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePaymentMethodGroupRelQualifier::getClassNameId),
			new FinderColumn<>(
				"commercePaymentMethodGroupRelQualifier.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePaymentMethodGroupRelQualifier::getClassPK),
			new FinderColumn<>(
				"commercePaymentMethodGroupRelQualifier.",
				"commercePaymentMethodGroupRelId", "CPaymentMethodGroupRelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommercePaymentMethodGroupRelQualifier::
					getCommercePaymentMethodGroupRelId));

		CommercePaymentMethodGroupRelQualifierUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePaymentMethodGroupRelQualifierUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePaymentMethodGroupRelQualifierImpl.class.getName());
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
		CommercePaymentMethodGroupRelQualifierModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER =
			"SELECT commercePaymentMethodGroupRelQualifier FROM CommercePaymentMethodGroupRelQualifier commercePaymentMethodGroupRelQualifier";

	private static final String
		_SQL_SELECT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER_WHERE =
			"SELECT commercePaymentMethodGroupRelQualifier FROM CommercePaymentMethodGroupRelQualifier commercePaymentMethodGroupRelQualifier WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEPAYMENTMETHODGROUPRELQUALIFIER_WHERE =
			"SELECT COUNT(commercePaymentMethodGroupRelQualifier) FROM CommercePaymentMethodGroupRelQualifier commercePaymentMethodGroupRelQualifier WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePaymentMethodGroupRelQualifier exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentMethodGroupRelQualifierPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"commercePaymentMethodGroupRelQualifierId",
			"commercePaymentMethodGroupRelId"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1983748850