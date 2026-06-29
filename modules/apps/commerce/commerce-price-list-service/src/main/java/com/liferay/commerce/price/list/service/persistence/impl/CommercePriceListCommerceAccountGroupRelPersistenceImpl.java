/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.impl;

import com.liferay.commerce.price.list.exception.NoSuchPriceListCommerceAccountGroupRelException;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRelTable;
import com.liferay.commerce.price.list.model.impl.CommercePriceListCommerceAccountGroupRelImpl;
import com.liferay.commerce.price.list.model.impl.CommercePriceListCommerceAccountGroupRelModelImpl;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListCommerceAccountGroupRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListCommerceAccountGroupRelUtil;
import com.liferay.commerce.price.list.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
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
 * The persistence implementation for the commerce price list commerce account group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommercePriceListCommerceAccountGroupRelPersistence.class)
public class CommercePriceListCommerceAccountGroupRelPersistenceImpl
	extends BasePersistenceImpl
		<CommercePriceListCommerceAccountGroupRel,
		 NoSuchPriceListCommerceAccountGroupRelException>
	implements CommercePriceListCommerceAccountGroupRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommercePriceListCommerceAccountGroupRelUtil</code> to access the commerce price list commerce account group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommercePriceListCommerceAccountGroupRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommercePriceListCommerceAccountGroupRel,
		 NoSuchPriceListCommerceAccountGroupRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce price list commerce account group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price list commerce account group rels
	 * @param end the upper bound of the range of commerce price list commerce account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list commerce account group rels
	 */
	@Override
	public List<CommercePriceListCommerceAccountGroupRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommercePriceListCommerceAccountGroupRel>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce price list commerce account group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list commerce account group rel
	 * @throws NoSuchPriceListCommerceAccountGroupRelException if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel findByUuid_First(
			String uuid,
			OrderByComparator<CommercePriceListCommerceAccountGroupRel>
				orderByComparator)
		throws NoSuchPriceListCommerceAccountGroupRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list commerce account group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list commerce account group rel, or <code>null</code> if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CommercePriceListCommerceAccountGroupRel>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list commerce account group rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce price list commerce account group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce price list commerce account group rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommercePriceListCommerceAccountGroupRel,
		 NoSuchPriceListCommerceAccountGroupRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce price list commerce account group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price list commerce account group rels
	 * @param end the upper bound of the range of commerce price list commerce account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list commerce account group rels
	 */
	@Override
	public List<CommercePriceListCommerceAccountGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommercePriceListCommerceAccountGroupRel>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list commerce account group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list commerce account group rel
	 * @throws NoSuchPriceListCommerceAccountGroupRelException if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommercePriceListCommerceAccountGroupRel>
				orderByComparator)
		throws NoSuchPriceListCommerceAccountGroupRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list commerce account group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list commerce account group rel, or <code>null</code> if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommercePriceListCommerceAccountGroupRel>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list commerce account group rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce price list commerce account group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce price list commerce account group rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommercePriceListCommerceAccountGroupRel,
		 NoSuchPriceListCommerceAccountGroupRelException>
			_collectionPersistenceFinderByCommercePriceListId;

	/**
	 * Returns an ordered range of all the commerce price list commerce account group rels where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommercePriceListCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price list commerce account group rels
	 * @param end the upper bound of the range of commerce price list commerce account group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price list commerce account group rels
	 */
	@Override
	public List<CommercePriceListCommerceAccountGroupRel>
		findByCommercePriceListId(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceListCommerceAccountGroupRel>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCommercePriceListId.find(
			finderCache, new Object[] {commercePriceListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price list commerce account group rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list commerce account group rel
	 * @throws NoSuchPriceListCommerceAccountGroupRelException if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
			findByCommercePriceListId_First(
				long commercePriceListId,
				OrderByComparator<CommercePriceListCommerceAccountGroupRel>
					orderByComparator)
		throws NoSuchPriceListCommerceAccountGroupRelException {

		return _collectionPersistenceFinderByCommercePriceListId.findFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Returns the first commerce price list commerce account group rel in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price list commerce account group rel, or <code>null</code> if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel
		fetchByCommercePriceListId_First(
			long commercePriceListId,
			OrderByComparator<CommercePriceListCommerceAccountGroupRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCommercePriceListId.fetchFirst(
			finderCache, new Object[] {commercePriceListId}, orderByComparator);
	}

	/**
	 * Removes all the commerce price list commerce account group rels where commercePriceListId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 */
	@Override
	public void removeByCommercePriceListId(long commercePriceListId) {
		_collectionPersistenceFinderByCommercePriceListId.remove(
			finderCache, new Object[] {commercePriceListId});
	}

	/**
	 * Returns the number of commerce price list commerce account group rels where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price list commerce account group rels
	 */
	@Override
	public int countByCommercePriceListId(long commercePriceListId) {
		return _collectionPersistenceFinderByCommercePriceListId.count(
			finderCache, new Object[] {commercePriceListId});
	}

	private UniquePersistenceFinder
		<CommercePriceListCommerceAccountGroupRel,
		 NoSuchPriceListCommerceAccountGroupRelException>
			_uniquePersistenceFinderByCAGI_CPI;

	/**
	 * Returns the commerce price list commerce account group rel where commercePriceListId = &#63; and commerceAccountGroupId = &#63; or throws a <code>NoSuchPriceListCommerceAccountGroupRelException</code> if it could not be found.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the matching commerce price list commerce account group rel
	 * @throws NoSuchPriceListCommerceAccountGroupRelException if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel findByCAGI_CPI(
			long commercePriceListId, long commerceAccountGroupId)
		throws NoSuchPriceListCommerceAccountGroupRelException {

		return _uniquePersistenceFinderByCAGI_CPI.find(
			finderCache,
			new Object[] {commercePriceListId, commerceAccountGroupId});
	}

	/**
	 * Returns the commerce price list commerce account group rel where commercePriceListId = &#63; and commerceAccountGroupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce price list commerce account group rel, or <code>null</code> if a matching commerce price list commerce account group rel could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel fetchByCAGI_CPI(
		long commercePriceListId, long commerceAccountGroupId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCAGI_CPI.fetch(
			finderCache,
			new Object[] {commercePriceListId, commerceAccountGroupId},
			useFinderCache);
	}

	/**
	 * Removes the commerce price list commerce account group rel where commercePriceListId = &#63; and commerceAccountGroupId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the commerce price list commerce account group rel that was removed
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel removeByCAGI_CPI(
			long commercePriceListId, long commerceAccountGroupId)
		throws NoSuchPriceListCommerceAccountGroupRelException {

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel = findByCAGI_CPI(
				commercePriceListId, commerceAccountGroupId);

		return remove(commercePriceListCommerceAccountGroupRel);
	}

	/**
	 * Returns the number of commerce price list commerce account group rels where commercePriceListId = &#63; and commerceAccountGroupId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param commerceAccountGroupId the commerce account group ID
	 * @return the number of matching commerce price list commerce account group rels
	 */
	@Override
	public int countByCAGI_CPI(
		long commercePriceListId, long commerceAccountGroupId) {

		return _uniquePersistenceFinderByCAGI_CPI.count(
			finderCache,
			new Object[] {commercePriceListId, commerceAccountGroupId});
	}

	public CommercePriceListCommerceAccountGroupRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"commercePriceListCommerceAccountGroupRelId",
			"CPLCommerceAccountGroupRelId");
		dbColumnNames.put("order", "order_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommercePriceListCommerceAccountGroupRel.class);

		setModelImplClass(CommercePriceListCommerceAccountGroupRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommercePriceListCommerceAccountGroupRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce price list commerce account group rel with the primary key. Does not add the commerce price list commerce account group rel to the database.
	 *
	 * @param commercePriceListCommerceAccountGroupRelId the primary key for the new commerce price list commerce account group rel
	 * @return the new commerce price list commerce account group rel
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel create(
		long commercePriceListCommerceAccountGroupRelId) {

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel =
				new CommercePriceListCommerceAccountGroupRelImpl();

		commercePriceListCommerceAccountGroupRel.setNew(true);
		commercePriceListCommerceAccountGroupRel.setPrimaryKey(
			commercePriceListCommerceAccountGroupRelId);

		String uuid = PortalUUIDUtil.generate();

		commercePriceListCommerceAccountGroupRel.setUuid(uuid);

		commercePriceListCommerceAccountGroupRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commercePriceListCommerceAccountGroupRel;
	}

	/**
	 * Removes the commerce price list commerce account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePriceListCommerceAccountGroupRelId the primary key of the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel that was removed
	 * @throws NoSuchPriceListCommerceAccountGroupRelException if a commerce price list commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel remove(
			long commercePriceListCommerceAccountGroupRelId)
		throws NoSuchPriceListCommerceAccountGroupRelException {

		return remove((Serializable)commercePriceListCommerceAccountGroupRelId);
	}

	@Override
	protected CommercePriceListCommerceAccountGroupRel removeImpl(
		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commercePriceListCommerceAccountGroupRel)) {
				commercePriceListCommerceAccountGroupRel =
					(CommercePriceListCommerceAccountGroupRel)session.get(
						CommercePriceListCommerceAccountGroupRelImpl.class,
						commercePriceListCommerceAccountGroupRel.
							getPrimaryKeyObj());
			}

			if ((commercePriceListCommerceAccountGroupRel != null) &&
				ctPersistenceHelper.isRemove(
					commercePriceListCommerceAccountGroupRel)) {

				session.delete(commercePriceListCommerceAccountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commercePriceListCommerceAccountGroupRel != null) {
			clearCache(commercePriceListCommerceAccountGroupRel);
		}

		return commercePriceListCommerceAccountGroupRel;
	}

	@Override
	public CommercePriceListCommerceAccountGroupRel updateImpl(
		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel) {

		boolean isNew = commercePriceListCommerceAccountGroupRel.isNew();

		if (!(commercePriceListCommerceAccountGroupRel instanceof
				CommercePriceListCommerceAccountGroupRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commercePriceListCommerceAccountGroupRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commercePriceListCommerceAccountGroupRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commercePriceListCommerceAccountGroupRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommercePriceListCommerceAccountGroupRel implementation " +
					commercePriceListCommerceAccountGroupRel.getClass());
		}

		CommercePriceListCommerceAccountGroupRelModelImpl
			commercePriceListCommerceAccountGroupRelModelImpl =
				(CommercePriceListCommerceAccountGroupRelModelImpl)
					commercePriceListCommerceAccountGroupRel;

		if (Validator.isNull(
				commercePriceListCommerceAccountGroupRel.getUuid())) {

			String uuid = PortalUUIDUtil.generate();

			commercePriceListCommerceAccountGroupRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(commercePriceListCommerceAccountGroupRel.getCreateDate() ==
				null)) {

			if (serviceContext == null) {
				commercePriceListCommerceAccountGroupRel.setCreateDate(date);
			}
			else {
				commercePriceListCommerceAccountGroupRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commercePriceListCommerceAccountGroupRelModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				commercePriceListCommerceAccountGroupRel.setModifiedDate(date);
			}
			else {
				commercePriceListCommerceAccountGroupRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(
					commercePriceListCommerceAccountGroupRel)) {

				if (!isNew) {
					session.evict(
						CommercePriceListCommerceAccountGroupRelImpl.class,
						commercePriceListCommerceAccountGroupRel.
							getPrimaryKeyObj());
				}

				session.save(commercePriceListCommerceAccountGroupRel);
			}
			else {
				commercePriceListCommerceAccountGroupRel =
					(CommercePriceListCommerceAccountGroupRel)session.merge(
						commercePriceListCommerceAccountGroupRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(
			commercePriceListCommerceAccountGroupRel, false);

		if (isNew) {
			commercePriceListCommerceAccountGroupRel.setNew(false);
		}

		commercePriceListCommerceAccountGroupRel.resetOriginalValues();

		return commercePriceListCommerceAccountGroupRel;
	}

	/**
	 * Returns the commerce price list commerce account group rel with the primary key or throws a <code>NoSuchPriceListCommerceAccountGroupRelException</code> if it could not be found.
	 *
	 * @param commercePriceListCommerceAccountGroupRelId the primary key of the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel
	 * @throws NoSuchPriceListCommerceAccountGroupRelException if a commerce price list commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel findByPrimaryKey(
			long commercePriceListCommerceAccountGroupRelId)
		throws NoSuchPriceListCommerceAccountGroupRelException {

		return findByPrimaryKey(
			(Serializable)commercePriceListCommerceAccountGroupRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce price list commerce account group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePriceListCommerceAccountGroupRelId the primary key of the commerce price list commerce account group rel
	 * @return the commerce price list commerce account group rel, or <code>null</code> if a commerce price list commerce account group rel with the primary key could not be found
	 */
	@Override
	public CommercePriceListCommerceAccountGroupRel fetchByPrimaryKey(
		long commercePriceListCommerceAccountGroupRelId) {

		return fetchByPrimaryKey(
			(Serializable)commercePriceListCommerceAccountGroupRelId);
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
		return "CPLCommerceAccountGroupRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commercePriceListCommerceAccountGroupRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL;
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
		return CommercePriceListCommerceAccountGroupRelModelImpl.
			TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPLCommerceGroupAccountRel";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("commercePriceListId");
		ctMergeColumnNames.add("commerceAccountGroupId");
		ctMergeColumnNames.add("order_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPLCommerceAccountGroupRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"commercePriceListId", "commerceAccountGroupId"});
	}

	/**
	 * Initializes the commerce price list commerce account group rel persistence.
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
			_SQL_SELECT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE,
			_SQL_COUNT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE,
			CommercePriceListCommerceAccountGroupRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commercePriceListCommerceAccountGroupRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommercePriceListCommerceAccountGroupRel::getUuid));

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
				_SQL_SELECT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE,
				CommercePriceListCommerceAccountGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListCommerceAccountGroupRel.", "uuid",
					"uuid_", FinderColumn.Type.STRING, "=", true, true,
					CommercePriceListCommerceAccountGroupRel::getUuid),
				new FinderColumn<>(
					"commercePriceListCommerceAccountGroupRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommercePriceListCommerceAccountGroupRel::getCompanyId));

		_collectionPersistenceFinderByCommercePriceListId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommercePriceListId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commercePriceListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommercePriceListId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommercePriceListId",
					new String[] {Long.class.getName()},
					new String[] {"commercePriceListId"}, false),
				_SQL_SELECT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE,
				_SQL_COUNT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE,
				CommercePriceListCommerceAccountGroupRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commercePriceListCommerceAccountGroupRel.",
					"commercePriceListId", FinderColumn.Type.LONG, "=", true,
					true,
					CommercePriceListCommerceAccountGroupRel::
						getCommercePriceListId));

		_uniquePersistenceFinderByCAGI_CPI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCAGI_CPI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commercePriceListId", "commerceAccountGroupId"},
				0, 0, false,
				CommercePriceListCommerceAccountGroupRel::
					getCommercePriceListId,
				CommercePriceListCommerceAccountGroupRel::
					getCommerceAccountGroupId),
			_SQL_SELECT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE, "",
			new FinderColumn<>(
				"commercePriceListCommerceAccountGroupRel.",
				"commercePriceListId", FinderColumn.Type.LONG, "=", true, true,
				CommercePriceListCommerceAccountGroupRel::
					getCommercePriceListId),
			new FinderColumn<>(
				"commercePriceListCommerceAccountGroupRel.",
				"commerceAccountGroupId", FinderColumn.Type.LONG, "=", true,
				true,
				CommercePriceListCommerceAccountGroupRel::
					getCommerceAccountGroupId));

		CommercePriceListCommerceAccountGroupRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommercePriceListCommerceAccountGroupRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommercePriceListCommerceAccountGroupRelImpl.class.getName());
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
		CommercePriceListCommerceAccountGroupRelModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL =
			"SELECT commercePriceListCommerceAccountGroupRel FROM CommercePriceListCommerceAccountGroupRel commercePriceListCommerceAccountGroupRel";

	private static final String
		_SQL_SELECT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE =
			"SELECT commercePriceListCommerceAccountGroupRel FROM CommercePriceListCommerceAccountGroupRel commercePriceListCommerceAccountGroupRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCEPRICELISTCOMMERCEACCOUNTGROUPREL_WHERE =
			"SELECT COUNT(commercePriceListCommerceAccountGroupRel) FROM CommercePriceListCommerceAccountGroupRel commercePriceListCommerceAccountGroupRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommercePriceListCommerceAccountGroupRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePriceListCommerceAccountGroupRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "commercePriceListCommerceAccountGroupRelId", "order"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1460345138