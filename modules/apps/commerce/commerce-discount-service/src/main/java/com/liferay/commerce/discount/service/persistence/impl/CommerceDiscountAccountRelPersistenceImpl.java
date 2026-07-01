/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.impl;

import com.liferay.commerce.discount.exception.NoSuchDiscountAccountRelException;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRel;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRelTable;
import com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelImpl;
import com.liferay.commerce.discount.model.impl.CommerceDiscountAccountRelModelImpl;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountAccountRelPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountAccountRelUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

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
 * The persistence implementation for the commerce discount account rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceDiscountAccountRelPersistence.class)
public class CommerceDiscountAccountRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceDiscountAccountRel, NoSuchDiscountAccountRelException>
	implements CommerceDiscountAccountRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceDiscountAccountRelUtil</code> to access the commerce discount account rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceDiscountAccountRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceDiscountAccountRel, NoSuchDiscountAccountRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce discount account rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce discount account rels
	 * @param end the upper bound of the range of commerce discount account rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount account rels
	 */
	@Override
	public List<CommerceDiscountAccountRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel
	 * @throws NoSuchDiscountAccountRelException if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel findByUuid_First(
			String uuid,
			OrderByComparator<CommerceDiscountAccountRel> orderByComparator)
		throws NoSuchDiscountAccountRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel, or <code>null</code> if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount account rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce discount account rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce discount account rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountAccountRel, NoSuchDiscountAccountRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce discount account rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce discount account rels
	 * @param end the upper bound of the range of commerce discount account rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount account rels
	 */
	@Override
	public List<CommerceDiscountAccountRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel
	 * @throws NoSuchDiscountAccountRelException if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceDiscountAccountRel> orderByComparator)
		throws NoSuchDiscountAccountRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel, or <code>null</code> if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount account rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of commerce discount account rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce discount account rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountAccountRel, NoSuchDiscountAccountRelException>
			_collectionPersistenceFinderByCommerceAccountId;

	/**
	 * Returns an ordered range of all the commerce discount account rels where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce discount account rels
	 * @param end the upper bound of the range of commerce discount account rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount account rels
	 */
	@Override
	public List<CommerceDiscountAccountRel> findByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceAccountId.find(
			finderCache, new Object[] {commerceAccountId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel
	 * @throws NoSuchDiscountAccountRelException if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel findByCommerceAccountId_First(
			long commerceAccountId,
			OrderByComparator<CommerceDiscountAccountRel> orderByComparator)
		throws NoSuchDiscountAccountRelException {

		return _collectionPersistenceFinderByCommerceAccountId.findFirst(
			finderCache, new Object[] {commerceAccountId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel, or <code>null</code> if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel fetchByCommerceAccountId_First(
		long commerceAccountId,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceAccountId.fetchFirst(
			finderCache, new Object[] {commerceAccountId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount account rels where commerceAccountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 */
	@Override
	public void removeByCommerceAccountId(long commerceAccountId) {
		_collectionPersistenceFinderByCommerceAccountId.remove(
			finderCache, new Object[] {commerceAccountId});
	}

	/**
	 * Returns the number of commerce discount account rels where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce discount account rels
	 */
	@Override
	public int countByCommerceAccountId(long commerceAccountId) {
		return _collectionPersistenceFinderByCommerceAccountId.count(
			finderCache, new Object[] {commerceAccountId});
	}

	private CollectionPersistenceFinder
		<CommerceDiscountAccountRel, NoSuchDiscountAccountRelException>
			_collectionPersistenceFinderByCommerceDiscountId;

	/**
	 * Returns an ordered range of all the commerce discount account rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountAccountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount account rels
	 * @param end the upper bound of the range of commerce discount account rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount account rels
	 */
	@Override
	public List<CommerceDiscountAccountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceDiscountId.find(
			finderCache, new Object[] {commerceDiscountId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel
	 * @throws NoSuchDiscountAccountRelException if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel findByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountAccountRel> orderByComparator)
		throws NoSuchDiscountAccountRelException {

		return _collectionPersistenceFinderByCommerceDiscountId.findFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Returns the first commerce discount account rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount account rel, or <code>null</code> if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountAccountRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceDiscountId.fetchFirst(
			finderCache, new Object[] {commerceDiscountId}, orderByComparator);
	}

	/**
	 * Removes all the commerce discount account rels where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	@Override
	public void removeByCommerceDiscountId(long commerceDiscountId) {
		_collectionPersistenceFinderByCommerceDiscountId.remove(
			finderCache, new Object[] {commerceDiscountId});
	}

	/**
	 * Returns the number of commerce discount account rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount account rels
	 */
	@Override
	public int countByCommerceDiscountId(long commerceDiscountId) {
		return _collectionPersistenceFinderByCommerceDiscountId.count(
			finderCache, new Object[] {commerceDiscountId});
	}

	private UniquePersistenceFinder
		<CommerceDiscountAccountRel, NoSuchDiscountAccountRelException>
			_uniquePersistenceFinderByCAI_CDI;

	/**
	 * Returns the commerce discount account rel where commerceAccountId = &#63; and commerceDiscountId = &#63; or throws a <code>NoSuchDiscountAccountRelException</code> if it could not be found.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount account rel
	 * @throws NoSuchDiscountAccountRelException if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel findByCAI_CDI(
			long commerceAccountId, long commerceDiscountId)
		throws NoSuchDiscountAccountRelException {

		return _uniquePersistenceFinderByCAI_CDI.find(
			finderCache, new Object[] {commerceAccountId, commerceDiscountId});
	}

	/**
	 * Returns the commerce discount account rel where commerceAccountId = &#63; and commerceDiscountId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount account rel, or <code>null</code> if a matching commerce discount account rel could not be found
	 */
	@Override
	public CommerceDiscountAccountRel fetchByCAI_CDI(
		long commerceAccountId, long commerceDiscountId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByCAI_CDI.fetch(
			finderCache, new Object[] {commerceAccountId, commerceDiscountId},
			useFinderCache);
	}

	/**
	 * Removes the commerce discount account rel where commerceAccountId = &#63; and commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the commerce discount account rel that was removed
	 */
	@Override
	public CommerceDiscountAccountRel removeByCAI_CDI(
			long commerceAccountId, long commerceDiscountId)
		throws NoSuchDiscountAccountRelException {

		CommerceDiscountAccountRel commerceDiscountAccountRel = findByCAI_CDI(
			commerceAccountId, commerceDiscountId);

		return remove(commerceDiscountAccountRel);
	}

	/**
	 * Returns the number of commerce discount account rels where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount account rels
	 */
	@Override
	public int countByCAI_CDI(long commerceAccountId, long commerceDiscountId) {
		return _uniquePersistenceFinderByCAI_CDI.count(
			finderCache, new Object[] {commerceAccountId, commerceDiscountId});
	}

	public CommerceDiscountAccountRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("order", "order_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceDiscountAccountRel.class);

		setModelImplClass(CommerceDiscountAccountRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceDiscountAccountRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce discount account rel with the primary key. Does not add the commerce discount account rel to the database.
	 *
	 * @param commerceDiscountAccountRelId the primary key for the new commerce discount account rel
	 * @return the new commerce discount account rel
	 */
	@Override
	public CommerceDiscountAccountRel create(
		long commerceDiscountAccountRelId) {

		CommerceDiscountAccountRel commerceDiscountAccountRel =
			new CommerceDiscountAccountRelImpl();

		commerceDiscountAccountRel.setNew(true);
		commerceDiscountAccountRel.setPrimaryKey(commerceDiscountAccountRelId);

		String uuid = PortalUUIDUtil.generate();

		commerceDiscountAccountRel.setUuid(uuid);

		commerceDiscountAccountRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceDiscountAccountRel;
	}

	/**
	 * Removes the commerce discount account rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountAccountRelId the primary key of the commerce discount account rel
	 * @return the commerce discount account rel that was removed
	 * @throws NoSuchDiscountAccountRelException if a commerce discount account rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountAccountRel remove(long commerceDiscountAccountRelId)
		throws NoSuchDiscountAccountRelException {

		return remove((Serializable)commerceDiscountAccountRelId);
	}

	@Override
	protected CommerceDiscountAccountRel removeImpl(
		CommerceDiscountAccountRel commerceDiscountAccountRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceDiscountAccountRel)) {
				commerceDiscountAccountRel =
					(CommerceDiscountAccountRel)session.get(
						CommerceDiscountAccountRelImpl.class,
						commerceDiscountAccountRel.getPrimaryKeyObj());
			}

			if (commerceDiscountAccountRel != null) {
				session.delete(commerceDiscountAccountRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceDiscountAccountRel != null) {
			clearCache(commerceDiscountAccountRel);
		}

		return commerceDiscountAccountRel;
	}

	@Override
	public CommerceDiscountAccountRel updateImpl(
		CommerceDiscountAccountRel commerceDiscountAccountRel) {

		boolean isNew = commerceDiscountAccountRel.isNew();

		if (!(commerceDiscountAccountRel instanceof
				CommerceDiscountAccountRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceDiscountAccountRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceDiscountAccountRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceDiscountAccountRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceDiscountAccountRel implementation " +
					commerceDiscountAccountRel.getClass());
		}

		CommerceDiscountAccountRelModelImpl
			commerceDiscountAccountRelModelImpl =
				(CommerceDiscountAccountRelModelImpl)commerceDiscountAccountRel;

		if (Validator.isNull(commerceDiscountAccountRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceDiscountAccountRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceDiscountAccountRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceDiscountAccountRel.setCreateDate(date);
			}
			else {
				commerceDiscountAccountRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceDiscountAccountRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceDiscountAccountRel.setModifiedDate(date);
			}
			else {
				commerceDiscountAccountRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceDiscountAccountRel);
			}
			else {
				commerceDiscountAccountRel =
					(CommerceDiscountAccountRel)session.merge(
						commerceDiscountAccountRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceDiscountAccountRel, false);

		if (isNew) {
			commerceDiscountAccountRel.setNew(false);
		}

		commerceDiscountAccountRel.resetOriginalValues();

		return commerceDiscountAccountRel;
	}

	/**
	 * Returns the commerce discount account rel with the primary key or throws a <code>NoSuchDiscountAccountRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountAccountRelId the primary key of the commerce discount account rel
	 * @return the commerce discount account rel
	 * @throws NoSuchDiscountAccountRelException if a commerce discount account rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountAccountRel findByPrimaryKey(
			long commerceDiscountAccountRelId)
		throws NoSuchDiscountAccountRelException {

		return findByPrimaryKey((Serializable)commerceDiscountAccountRelId);
	}

	/**
	 * Returns the commerce discount account rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountAccountRelId the primary key of the commerce discount account rel
	 * @return the commerce discount account rel, or <code>null</code> if a commerce discount account rel with the primary key could not be found
	 */
	@Override
	public CommerceDiscountAccountRel fetchByPrimaryKey(
		long commerceDiscountAccountRelId) {

		return fetchByPrimaryKey((Serializable)commerceDiscountAccountRelId);
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
		return "commerceDiscountAccountRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceDiscountAccountRelModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce discount account rel persistence.
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
			_SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
			_SQL_COUNT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
			CommerceDiscountAccountRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"commerceDiscountAccountRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceDiscountAccountRel::getUuid));

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
				_SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
				CommerceDiscountAccountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountAccountRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceDiscountAccountRel::getUuid),
				new FinderColumn<>(
					"commerceDiscountAccountRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountAccountRel::getCompanyId));

		_collectionPersistenceFinderByCommerceAccountId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCommerceAccountId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"commerceAccountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCommerceAccountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAccountId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCommerceAccountId",
					new String[] {Long.class.getName()},
					new String[] {"commerceAccountId"}, false),
				_SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
				CommerceDiscountAccountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountAccountRel.", "commerceAccountId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountAccountRel::getCommerceAccountId));

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
				_SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
				_SQL_COUNT_COMMERCEDISCOUNTACCOUNTREL_WHERE,
				CommerceDiscountAccountRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"commerceDiscountAccountRel.", "commerceDiscountId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceDiscountAccountRel::getCommerceDiscountId));

		_uniquePersistenceFinderByCAI_CDI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCAI_CDI",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"commerceAccountId", "commerceDiscountId"}, 0, 0,
				false, CommerceDiscountAccountRel::getCommerceAccountId,
				CommerceDiscountAccountRel::getCommerceDiscountId),
			_SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL_WHERE, "",
			new FinderColumn<>(
				"commerceDiscountAccountRel.", "commerceAccountId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceDiscountAccountRel::getCommerceAccountId),
			new FinderColumn<>(
				"commerceDiscountAccountRel.", "commerceDiscountId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceDiscountAccountRel::getCommerceDiscountId));

		CommerceDiscountAccountRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceDiscountAccountRelUtil.setPersistence(null);

		entityCache.removeCache(CommerceDiscountAccountRelImpl.class.getName());
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
		CommerceDiscountAccountRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL =
		"SELECT commerceDiscountAccountRel FROM CommerceDiscountAccountRel commerceDiscountAccountRel";

	private static final String _SQL_SELECT_COMMERCEDISCOUNTACCOUNTREL_WHERE =
		"SELECT commerceDiscountAccountRel FROM CommerceDiscountAccountRel commerceDiscountAccountRel WHERE ";

	private static final String _SQL_COUNT_COMMERCEDISCOUNTACCOUNTREL_WHERE =
		"SELECT COUNT(commerceDiscountAccountRel) FROM CommerceDiscountAccountRel commerceDiscountAccountRel WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "order"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1039109398