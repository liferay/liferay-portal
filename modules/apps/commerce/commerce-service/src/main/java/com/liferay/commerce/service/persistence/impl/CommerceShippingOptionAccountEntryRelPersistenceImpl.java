/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.NoSuchShippingOptionAccountEntryRelException;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRelTable;
import com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelImpl;
import com.liferay.commerce.model.impl.CommerceShippingOptionAccountEntryRelModelImpl;
import com.liferay.commerce.service.persistence.CommerceShippingOptionAccountEntryRelPersistence;
import com.liferay.commerce.service.persistence.CommerceShippingOptionAccountEntryRelUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce shipping option account entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceShippingOptionAccountEntryRelPersistence.class)
public class CommerceShippingOptionAccountEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceShippingOptionAccountEntryRel,
		 NoSuchShippingOptionAccountEntryRelException>
	implements CommerceShippingOptionAccountEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceShippingOptionAccountEntryRelUtil</code> to access the commerce shipping option account entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceShippingOptionAccountEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceShippingOptionAccountEntryRel,
		 NoSuchShippingOptionAccountEntryRelException>
			_collectionPersistenceFinderByAccountEntryId;

	/**
	 * Returns an ordered range of all the commerce shipping option account entry rels where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingOptionAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of commerce shipping option account entry rels
	 * @param end the upper bound of the range of commerce shipping option account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping option account entry rels
	 */
	@Override
	public List<CommerceShippingOptionAccountEntryRel> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<CommerceShippingOptionAccountEntryRel>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountEntryId.find(
			finderCache, new Object[] {accountEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping option account entry rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping option account entry rel
	 * @throws NoSuchShippingOptionAccountEntryRelException if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<CommerceShippingOptionAccountEntryRel>
				orderByComparator)
		throws NoSuchShippingOptionAccountEntryRelException {

		return _collectionPersistenceFinderByAccountEntryId.findFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipping option account entry rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping option account entry rel, or <code>null</code> if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel fetchByAccountEntryId_First(
		long accountEntryId,
		OrderByComparator<CommerceShippingOptionAccountEntryRel>
			orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.fetchFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipping option account entry rels where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByAccountEntryId(long accountEntryId) {
		_collectionPersistenceFinderByAccountEntryId.remove(
			finderCache, new Object[] {accountEntryId});
	}

	/**
	 * Returns the number of commerce shipping option account entry rels where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching commerce shipping option account entry rels
	 */
	@Override
	public int countByAccountEntryId(long accountEntryId) {
		return _collectionPersistenceFinderByAccountEntryId.count(
			finderCache, new Object[] {accountEntryId});
	}

	private CollectionPersistenceFinder
		<CommerceShippingOptionAccountEntryRel,
		 NoSuchShippingOptionAccountEntryRelException>
			_collectionPersistenceFinderByCommerceChannelId;

	/**
	 * Returns an ordered range of all the commerce shipping option account entry rels where commerceChannelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingOptionAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param start the lower bound of the range of commerce shipping option account entry rels
	 * @param end the upper bound of the range of commerce shipping option account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping option account entry rels
	 */
	@Override
	public List<CommerceShippingOptionAccountEntryRel> findByCommerceChannelId(
		long commerceChannelId, int start, int end,
		OrderByComparator<CommerceShippingOptionAccountEntryRel>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceChannelId.find(
			finderCache, new Object[] {commerceChannelId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping option account entry rel in the ordered set where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping option account entry rel
	 * @throws NoSuchShippingOptionAccountEntryRelException if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel findByCommerceChannelId_First(
			long commerceChannelId,
			OrderByComparator<CommerceShippingOptionAccountEntryRel>
				orderByComparator)
		throws NoSuchShippingOptionAccountEntryRelException {

		return _collectionPersistenceFinderByCommerceChannelId.findFirst(
			finderCache, new Object[] {commerceChannelId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipping option account entry rel in the ordered set where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping option account entry rel, or <code>null</code> if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel fetchByCommerceChannelId_First(
		long commerceChannelId,
		OrderByComparator<CommerceShippingOptionAccountEntryRel>
			orderByComparator) {

		return _collectionPersistenceFinderByCommerceChannelId.fetchFirst(
			finderCache, new Object[] {commerceChannelId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipping option account entry rels where commerceChannelId = &#63; from the database.
	 *
	 * @param commerceChannelId the commerce channel ID
	 */
	@Override
	public void removeByCommerceChannelId(long commerceChannelId) {
		_collectionPersistenceFinderByCommerceChannelId.remove(
			finderCache, new Object[] {commerceChannelId});
	}

	/**
	 * Returns the number of commerce shipping option account entry rels where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @return the number of matching commerce shipping option account entry rels
	 */
	@Override
	public int countByCommerceChannelId(long commerceChannelId) {
		return _collectionPersistenceFinderByCommerceChannelId.count(
			finderCache, new Object[] {commerceChannelId});
	}

	private CollectionPersistenceFinder
		<CommerceShippingOptionAccountEntryRel,
		 NoSuchShippingOptionAccountEntryRelException>
			_collectionPersistenceFinderByCommerceShippingOptionKey;

	/**
	 * Returns an ordered range of all the commerce shipping option account entry rels where commerceShippingOptionKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingOptionAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShippingOptionKey the commerce shipping option key
	 * @param start the lower bound of the range of commerce shipping option account entry rels
	 * @param end the upper bound of the range of commerce shipping option account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping option account entry rels
	 */
	@Override
	public List<CommerceShippingOptionAccountEntryRel>
		findByCommerceShippingOptionKey(
			String commerceShippingOptionKey, int start, int end,
			OrderByComparator<CommerceShippingOptionAccountEntryRel>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceShippingOptionKey.find(
			finderCache, new Object[] {commerceShippingOptionKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping option account entry rel in the ordered set where commerceShippingOptionKey = &#63;.
	 *
	 * @param commerceShippingOptionKey the commerce shipping option key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping option account entry rel
	 * @throws NoSuchShippingOptionAccountEntryRelException if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel
			findByCommerceShippingOptionKey_First(
				String commerceShippingOptionKey,
				OrderByComparator<CommerceShippingOptionAccountEntryRel>
					orderByComparator)
		throws NoSuchShippingOptionAccountEntryRelException {

		return _collectionPersistenceFinderByCommerceShippingOptionKey.
			findFirst(
				finderCache, new Object[] {commerceShippingOptionKey},
				orderByComparator);
	}

	/**
	 * Returns the first commerce shipping option account entry rel in the ordered set where commerceShippingOptionKey = &#63;.
	 *
	 * @param commerceShippingOptionKey the commerce shipping option key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping option account entry rel, or <code>null</code> if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel
		fetchByCommerceShippingOptionKey_First(
			String commerceShippingOptionKey,
			OrderByComparator<CommerceShippingOptionAccountEntryRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCommerceShippingOptionKey.
			fetchFirst(
				finderCache, new Object[] {commerceShippingOptionKey},
				orderByComparator);
	}

	/**
	 * Removes all the commerce shipping option account entry rels where commerceShippingOptionKey = &#63; from the database.
	 *
	 * @param commerceShippingOptionKey the commerce shipping option key
	 */
	@Override
	public void removeByCommerceShippingOptionKey(
		String commerceShippingOptionKey) {

		_collectionPersistenceFinderByCommerceShippingOptionKey.remove(
			finderCache, new Object[] {commerceShippingOptionKey});
	}

	/**
	 * Returns the number of commerce shipping option account entry rels where commerceShippingOptionKey = &#63;.
	 *
	 * @param commerceShippingOptionKey the commerce shipping option key
	 * @return the number of matching commerce shipping option account entry rels
	 */
	@Override
	public int countByCommerceShippingOptionKey(
		String commerceShippingOptionKey) {

		return _collectionPersistenceFinderByCommerceShippingOptionKey.count(
			finderCache, new Object[] {commerceShippingOptionKey});
	}

	private UniquePersistenceFinder
		<CommerceShippingOptionAccountEntryRel,
		 NoSuchShippingOptionAccountEntryRelException>
			_uniquePersistenceFinderByA_C;

	/**
	 * Returns the commerce shipping option account entry rel where accountEntryId = &#63; and commerceChannelId = &#63; or throws a <code>NoSuchShippingOptionAccountEntryRelException</code> if it could not be found.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @return the matching commerce shipping option account entry rel
	 * @throws NoSuchShippingOptionAccountEntryRelException if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel findByA_C(
			long accountEntryId, long commerceChannelId)
		throws NoSuchShippingOptionAccountEntryRelException {

		return _uniquePersistenceFinderByA_C.find(
			finderCache, new Object[] {accountEntryId, commerceChannelId});
	}

	/**
	 * Returns the commerce shipping option account entry rel where accountEntryId = &#63; and commerceChannelId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping option account entry rel, or <code>null</code> if a matching commerce shipping option account entry rel could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel fetchByA_C(
		long accountEntryId, long commerceChannelId, boolean useFinderCache) {

		return _uniquePersistenceFinderByA_C.fetch(
			finderCache, new Object[] {accountEntryId, commerceChannelId},
			useFinderCache);
	}

	/**
	 * Removes the commerce shipping option account entry rel where accountEntryId = &#63; and commerceChannelId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @return the commerce shipping option account entry rel that was removed
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel removeByA_C(
			long accountEntryId, long commerceChannelId)
		throws NoSuchShippingOptionAccountEntryRelException {

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel = findByA_C(
				accountEntryId, commerceChannelId);

		return remove(commerceShippingOptionAccountEntryRel);
	}

	/**
	 * Returns the number of commerce shipping option account entry rels where accountEntryId = &#63; and commerceChannelId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @return the number of matching commerce shipping option account entry rels
	 */
	@Override
	public int countByA_C(long accountEntryId, long commerceChannelId) {
		return _uniquePersistenceFinderByA_C.count(
			finderCache, new Object[] {accountEntryId, commerceChannelId});
	}

	public CommerceShippingOptionAccountEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"CommerceShippingOptionAccountEntryRelId",
			"CSOptionAccountEntryRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceShippingOptionAccountEntryRel.class);

		setModelImplClass(CommerceShippingOptionAccountEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceShippingOptionAccountEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce shipping option account entry rel with the primary key. Does not add the commerce shipping option account entry rel to the database.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key for the new commerce shipping option account entry rel
	 * @return the new commerce shipping option account entry rel
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel create(
		long CommerceShippingOptionAccountEntryRelId) {

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				new CommerceShippingOptionAccountEntryRelImpl();

		commerceShippingOptionAccountEntryRel.setNew(true);
		commerceShippingOptionAccountEntryRel.setPrimaryKey(
			CommerceShippingOptionAccountEntryRelId);

		commerceShippingOptionAccountEntryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceShippingOptionAccountEntryRel;
	}

	/**
	 * Removes the commerce shipping option account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key of the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel that was removed
	 * @throws NoSuchShippingOptionAccountEntryRelException if a commerce shipping option account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel remove(
			long CommerceShippingOptionAccountEntryRelId)
		throws NoSuchShippingOptionAccountEntryRelException {

		return remove((Serializable)CommerceShippingOptionAccountEntryRelId);
	}

	@Override
	protected CommerceShippingOptionAccountEntryRel removeImpl(
		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceShippingOptionAccountEntryRel)) {
				commerceShippingOptionAccountEntryRel =
					(CommerceShippingOptionAccountEntryRel)session.get(
						CommerceShippingOptionAccountEntryRelImpl.class,
						commerceShippingOptionAccountEntryRel.
							getPrimaryKeyObj());
			}

			if (commerceShippingOptionAccountEntryRel != null) {
				session.delete(commerceShippingOptionAccountEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceShippingOptionAccountEntryRel != null) {
			clearCache(commerceShippingOptionAccountEntryRel);
		}

		return commerceShippingOptionAccountEntryRel;
	}

	@Override
	public CommerceShippingOptionAccountEntryRel updateImpl(
		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel) {

		boolean isNew = commerceShippingOptionAccountEntryRel.isNew();

		if (!(commerceShippingOptionAccountEntryRel instanceof
				CommerceShippingOptionAccountEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceShippingOptionAccountEntryRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceShippingOptionAccountEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceShippingOptionAccountEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceShippingOptionAccountEntryRel implementation " +
					commerceShippingOptionAccountEntryRel.getClass());
		}

		CommerceShippingOptionAccountEntryRelModelImpl
			commerceShippingOptionAccountEntryRelModelImpl =
				(CommerceShippingOptionAccountEntryRelModelImpl)
					commerceShippingOptionAccountEntryRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commerceShippingOptionAccountEntryRel.getCreateDate() == null)) {

			if (serviceContext == null) {
				commerceShippingOptionAccountEntryRel.setCreateDate(date);
			}
			else {
				commerceShippingOptionAccountEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceShippingOptionAccountEntryRelModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				commerceShippingOptionAccountEntryRel.setModifiedDate(date);
			}
			else {
				commerceShippingOptionAccountEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceShippingOptionAccountEntryRel);
			}
			else {
				commerceShippingOptionAccountEntryRel =
					(CommerceShippingOptionAccountEntryRel)session.merge(
						commerceShippingOptionAccountEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceShippingOptionAccountEntryRel, false);

		if (isNew) {
			commerceShippingOptionAccountEntryRel.setNew(false);
		}

		commerceShippingOptionAccountEntryRel.resetOriginalValues();

		return commerceShippingOptionAccountEntryRel;
	}

	/**
	 * Returns the commerce shipping option account entry rel with the primary key or throws a <code>NoSuchShippingOptionAccountEntryRelException</code> if it could not be found.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key of the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel
	 * @throws NoSuchShippingOptionAccountEntryRelException if a commerce shipping option account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel findByPrimaryKey(
			long CommerceShippingOptionAccountEntryRelId)
		throws NoSuchShippingOptionAccountEntryRelException {

		return findByPrimaryKey(
			(Serializable)CommerceShippingOptionAccountEntryRelId);
	}

	/**
	 * Returns the commerce shipping option account entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CommerceShippingOptionAccountEntryRelId the primary key of the commerce shipping option account entry rel
	 * @return the commerce shipping option account entry rel, or <code>null</code> if a commerce shipping option account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceShippingOptionAccountEntryRel fetchByPrimaryKey(
		long CommerceShippingOptionAccountEntryRelId) {

		return fetchByPrimaryKey(
			(Serializable)CommerceShippingOptionAccountEntryRelId);
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
		return "CSOptionAccountEntryRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "CommerceShippingOptionAccountEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceShippingOptionAccountEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce shipping option account entry rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByAccountEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAccountEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"accountEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAccountEntryId", new String[] {Long.class.getName()},
					new String[] {"accountEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAccountEntryId",
					new String[] {Long.class.getName()},
					new String[] {"accountEntryId"}, false),
				_SQL_SELECT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE,
				CommerceShippingOptionAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceShippingOptionAccountEntryRel.", "accountEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShippingOptionAccountEntryRel::getAccountEntryId));

		_collectionPersistenceFinderByCommerceChannelId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceChannelId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceChannelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceChannelId",
					new String[] {Long.class.getName()},
					new String[] {"commerceChannelId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceChannelId",
					new String[] {Long.class.getName()},
					new String[] {"commerceChannelId"}, false),
				_SQL_SELECT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE,
				CommerceShippingOptionAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceShippingOptionAccountEntryRel.",
					"commerceChannelId", FinderColumn.Type.LONG, "=", true,
					true,
					CommerceShippingOptionAccountEntryRel::
						getCommerceChannelId));

		_collectionPersistenceFinderByCommerceShippingOptionKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceShippingOptionKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceShippingOptionKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceShippingOptionKey",
					new String[] {String.class.getName()},
					new String[] {"commerceShippingOptionKey"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceShippingOptionKey",
					new String[] {String.class.getName()},
					new String[] {"commerceShippingOptionKey"}, 0, 1, false,
					null),
				_SQL_SELECT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE,
				CommerceShippingOptionAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceShippingOptionAccountEntryRel.",
					"commerceShippingOptionKey", FinderColumn.Type.STRING, "=",
					true, true,
					CommerceShippingOptionAccountEntryRel::
						getCommerceShippingOptionKey));

		_uniquePersistenceFinderByA_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"accountEntryId", "commerceChannelId"}, 0, 0,
				false, CommerceShippingOptionAccountEntryRel::getAccountEntryId,
				CommerceShippingOptionAccountEntryRel::getCommerceChannelId),
			_SQL_SELECT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE, "",
			new FinderColumn<>(
				"commerceShippingOptionAccountEntryRel.", "accountEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShippingOptionAccountEntryRel::getAccountEntryId),
			new FinderColumn<>(
				"commerceShippingOptionAccountEntryRel.", "commerceChannelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceShippingOptionAccountEntryRel::getCommerceChannelId));

		CommerceShippingOptionAccountEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceShippingOptionAccountEntryRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceShippingOptionAccountEntryRelImpl.class.getName());
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
		CommerceShippingOptionAccountEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL =
			"SELECT commerceShippingOptionAccountEntryRel FROM CommerceShippingOptionAccountEntryRel commerceShippingOptionAccountEntryRel";

	private static final String
		_SQL_SELECT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE =
			"SELECT commerceShippingOptionAccountEntryRel FROM CommerceShippingOptionAccountEntryRel commerceShippingOptionAccountEntryRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCESHIPPINGOPTIONACCOUNTENTRYREL_WHERE =
			"SELECT COUNT(commerceShippingOptionAccountEntryRel) FROM CommerceShippingOptionAccountEntryRel commerceShippingOptionAccountEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceShippingOptionAccountEntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShippingOptionAccountEntryRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"CommerceShippingOptionAccountEntryRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-590653848