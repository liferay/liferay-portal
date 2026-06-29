/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchChannelAccountEntryRelException;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRelTable;
import com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelImpl;
import com.liferay.commerce.product.model.impl.CommerceChannelAccountEntryRelModelImpl;
import com.liferay.commerce.product.service.persistence.CommerceChannelAccountEntryRelPersistence;
import com.liferay.commerce.product.service.persistence.CommerceChannelAccountEntryRelUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce channel account entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceChannelAccountEntryRelPersistence.class)
public class CommerceChannelAccountEntryRelPersistenceImpl
	extends BasePersistenceImpl
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
	implements CommerceChannelAccountEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceChannelAccountEntryRelUtil</code> to access the commerce channel account entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceChannelAccountEntryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_collectionPersistenceFinderByAccountEntryId;

	/**
	 * Returns an ordered range of all the commerce channel account entry rels where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel account entry rels
	 */
	@Override
	public List<CommerceChannelAccountEntryRel> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAccountEntryId.find(
			finderCache, new Object[] {accountEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator)
		throws NoSuchChannelAccountEntryRelException {

		return _collectionPersistenceFinderByAccountEntryId.findFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByAccountEntryId_First(
		long accountEntryId,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByAccountEntryId.fetchFirst(
			finderCache, new Object[] {accountEntryId}, orderByComparator);
	}

	/**
	 * Removes all the commerce channel account entry rels where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	@Override
	public void removeByAccountEntryId(long accountEntryId) {
		_collectionPersistenceFinderByAccountEntryId.remove(
			finderCache, new Object[] {accountEntryId});
	}

	/**
	 * Returns the number of commerce channel account entry rels where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByAccountEntryId(long accountEntryId) {
		return _collectionPersistenceFinderByAccountEntryId.count(
			finderCache, new Object[] {accountEntryId});
	}

	private CollectionPersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_collectionPersistenceFinderByCommerceChannelId;

	/**
	 * Returns an ordered range of all the commerce channel account entry rels where commerceChannelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel account entry rels
	 */
	@Override
	public List<CommerceChannelAccountEntryRel> findByCommerceChannelId(
		long commerceChannelId, int start, int end,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCommerceChannelId.find(
			finderCache, new Object[] {commerceChannelId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByCommerceChannelId_First(
			long commerceChannelId,
			OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator)
		throws NoSuchChannelAccountEntryRelException {

		return _collectionPersistenceFinderByCommerceChannelId.findFirst(
			finderCache, new Object[] {commerceChannelId}, orderByComparator);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByCommerceChannelId_First(
		long commerceChannelId,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByCommerceChannelId.fetchFirst(
			finderCache, new Object[] {commerceChannelId}, orderByComparator);
	}

	/**
	 * Removes all the commerce channel account entry rels where commerceChannelId = &#63; from the database.
	 *
	 * @param commerceChannelId the commerce channel ID
	 */
	@Override
	public void removeByCommerceChannelId(long commerceChannelId) {
		_collectionPersistenceFinderByCommerceChannelId.remove(
			finderCache, new Object[] {commerceChannelId});
	}

	/**
	 * Returns the number of commerce channel account entry rels where commerceChannelId = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByCommerceChannelId(long commerceChannelId) {
		return _collectionPersistenceFinderByCommerceChannelId.count(
			finderCache, new Object[] {commerceChannelId});
	}

	private CollectionPersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_collectionPersistenceFinderByA_T;

	/**
	 * Returns an ordered range of all the commerce channel account entry rels where accountEntryId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel account entry rels
	 */
	@Override
	public List<CommerceChannelAccountEntryRel> findByA_T(
		long accountEntryId, int type, int start, int end,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_T.find(
			finderCache, new Object[] {accountEntryId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where accountEntryId = &#63; and type = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByA_T_First(
			long accountEntryId, int type,
			OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator)
		throws NoSuchChannelAccountEntryRelException {

		return _collectionPersistenceFinderByA_T.findFirst(
			finderCache, new Object[] {accountEntryId, type},
			orderByComparator);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where accountEntryId = &#63; and type = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByA_T_First(
		long accountEntryId, int type,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByA_T.fetchFirst(
			finderCache, new Object[] {accountEntryId, type},
			orderByComparator);
	}

	/**
	 * Removes all the commerce channel account entry rels where accountEntryId = &#63; and type = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 * @param type the type
	 */
	@Override
	public void removeByA_T(long accountEntryId, int type) {
		_collectionPersistenceFinderByA_T.remove(
			finderCache, new Object[] {accountEntryId, type});
	}

	/**
	 * Returns the number of commerce channel account entry rels where accountEntryId = &#63; and type = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param type the type
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByA_T(long accountEntryId, int type) {
		return _collectionPersistenceFinderByA_T.count(
			finderCache, new Object[] {accountEntryId, type});
	}

	private CollectionPersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the commerce channel account entry rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel account entry rels
	 */
	@Override
	public List<CommerceChannelAccountEntryRel> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator)
		throws NoSuchChannelAccountEntryRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce channel account entry rels where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of commerce channel account entry rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the commerce channel account entry rels where commerceChannelId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel account entry rels
	 */
	@Override
	public List<CommerceChannelAccountEntryRel> findByC_T(
		long commerceChannelId, int type, int start, int end,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			finderCache, new Object[] {commerceChannelId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByC_T_First(
			long commerceChannelId, int type,
			OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator)
		throws NoSuchChannelAccountEntryRelException {

		return _collectionPersistenceFinderByC_T.findFirst(
			finderCache, new Object[] {commerceChannelId, type},
			orderByComparator);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByC_T_First(
		long commerceChannelId, int type,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			finderCache, new Object[] {commerceChannelId, type},
			orderByComparator);
	}

	/**
	 * Removes all the commerce channel account entry rels where commerceChannelId = &#63; and type = &#63; from the database.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long commerceChannelId, int type) {
		_collectionPersistenceFinderByC_T.remove(
			finderCache, new Object[] {commerceChannelId, type});
	}

	/**
	 * Returns the number of commerce channel account entry rels where commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByC_T(long commerceChannelId, int type) {
		return _collectionPersistenceFinderByC_T.count(
			finderCache, new Object[] {commerceChannelId, type});
	}

	private CollectionPersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_collectionPersistenceFinderByA_C_T;

	/**
	 * Returns an ordered range of all the commerce channel account entry rels where accountEntryId = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel account entry rels
	 */
	@Override
	public List<CommerceChannelAccountEntryRel> findByA_C_T(
		long accountEntryId, long commerceChannelId, int type, int start,
		int end,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_C_T.find(
			finderCache, new Object[] {accountEntryId, commerceChannelId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where accountEntryId = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByA_C_T_First(
			long accountEntryId, long commerceChannelId, int type,
			OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator)
		throws NoSuchChannelAccountEntryRelException {

		return _collectionPersistenceFinderByA_C_T.findFirst(
			finderCache, new Object[] {accountEntryId, commerceChannelId, type},
			orderByComparator);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where accountEntryId = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByA_C_T_First(
		long accountEntryId, long commerceChannelId, int type,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByA_C_T.fetchFirst(
			finderCache, new Object[] {accountEntryId, commerceChannelId, type},
			orderByComparator);
	}

	/**
	 * Removes all the commerce channel account entry rels where accountEntryId = &#63; and commerceChannelId = &#63; and type = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 */
	@Override
	public void removeByA_C_T(
		long accountEntryId, long commerceChannelId, int type) {

		_collectionPersistenceFinderByA_C_T.remove(
			finderCache,
			new Object[] {accountEntryId, commerceChannelId, type});
	}

	/**
	 * Returns the number of commerce channel account entry rels where accountEntryId = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByA_C_T(
		long accountEntryId, long commerceChannelId, int type) {

		return _collectionPersistenceFinderByA_C_T.count(
			finderCache,
			new Object[] {accountEntryId, commerceChannelId, type});
	}

	private CollectionPersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_collectionPersistenceFinderByC_C_C_T;

	/**
	 * Returns an ordered range of all the commerce channel account entry rels where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceChannelAccountEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param start the lower bound of the range of commerce channel account entry rels
	 * @param end the upper bound of the range of commerce channel account entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce channel account entry rels
	 */
	@Override
	public List<CommerceChannelAccountEntryRel> findByC_C_C_T(
		long classNameId, long classPK, long commerceChannelId, int type,
		int start, int end,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_T.find(
			finderCache,
			new Object[] {classNameId, classPK, commerceChannelId, type}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByC_C_C_T_First(
			long classNameId, long classPK, long commerceChannelId, int type,
			OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator)
		throws NoSuchChannelAccountEntryRelException {

		return _collectionPersistenceFinderByC_C_C_T.findFirst(
			finderCache,
			new Object[] {classNameId, classPK, commerceChannelId, type},
			orderByComparator);
	}

	/**
	 * Returns the first commerce channel account entry rel in the ordered set where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByC_C_C_T_First(
		long classNameId, long classPK, long commerceChannelId, int type,
		OrderByComparator<CommerceChannelAccountEntryRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_T.fetchFirst(
			finderCache,
			new Object[] {classNameId, classPK, commerceChannelId, type},
			orderByComparator);
	}

	/**
	 * Removes all the commerce channel account entry rels where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 */
	@Override
	public void removeByC_C_C_T(
		long classNameId, long classPK, long commerceChannelId, int type) {

		_collectionPersistenceFinderByC_C_C_T.remove(
			finderCache,
			new Object[] {classNameId, classPK, commerceChannelId, type});
	}

	/**
	 * Returns the number of commerce channel account entry rels where classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByC_C_C_T(
		long classNameId, long classPK, long commerceChannelId, int type) {

		return _collectionPersistenceFinderByC_C_C_T.count(
			finderCache,
			new Object[] {classNameId, classPK, commerceChannelId, type});
	}

	private UniquePersistenceFinder
		<CommerceChannelAccountEntryRel, NoSuchChannelAccountEntryRelException>
			_uniquePersistenceFinderByA_C_C_C_T;

	/**
	 * Returns the commerce channel account entry rel where accountEntryId = &#63; and classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63; or throws a <code>NoSuchChannelAccountEntryRelException</code> if it could not be found.
	 *
	 * @param accountEntryId the account entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @return the matching commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByA_C_C_C_T(
			long accountEntryId, long classNameId, long classPK,
			long commerceChannelId, int type)
		throws NoSuchChannelAccountEntryRelException {

		return _uniquePersistenceFinderByA_C_C_C_T.find(
			finderCache,
			new Object[] {
				accountEntryId, classNameId, classPK, commerceChannelId, type
			});
	}

	/**
	 * Returns the commerce channel account entry rel where accountEntryId = &#63; and classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param accountEntryId the account entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce channel account entry rel, or <code>null</code> if a matching commerce channel account entry rel could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByA_C_C_C_T(
		long accountEntryId, long classNameId, long classPK,
		long commerceChannelId, int type, boolean useFinderCache) {

		return _uniquePersistenceFinderByA_C_C_C_T.fetch(
			finderCache,
			new Object[] {
				accountEntryId, classNameId, classPK, commerceChannelId, type
			},
			useFinderCache);
	}

	/**
	 * Removes the commerce channel account entry rel where accountEntryId = &#63; and classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @return the commerce channel account entry rel that was removed
	 */
	@Override
	public CommerceChannelAccountEntryRel removeByA_C_C_C_T(
			long accountEntryId, long classNameId, long classPK,
			long commerceChannelId, int type)
		throws NoSuchChannelAccountEntryRelException {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			findByA_C_C_C_T(
				accountEntryId, classNameId, classPK, commerceChannelId, type);

		return remove(commerceChannelAccountEntryRel);
	}

	/**
	 * Returns the number of commerce channel account entry rels where accountEntryId = &#63; and classNameId = &#63; and classPK = &#63; and commerceChannelId = &#63; and type = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param commerceChannelId the commerce channel ID
	 * @param type the type
	 * @return the number of matching commerce channel account entry rels
	 */
	@Override
	public int countByA_C_C_C_T(
		long accountEntryId, long classNameId, long classPK,
		long commerceChannelId, int type) {

		return _uniquePersistenceFinderByA_C_C_C_T.count(
			finderCache,
			new Object[] {
				accountEntryId, classNameId, classPK, commerceChannelId, type
			});
	}

	public CommerceChannelAccountEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"commerceChannelAccountEntryRelId", "CChannelAccountEntryRelId");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceChannelAccountEntryRel.class);

		setModelImplClass(CommerceChannelAccountEntryRelImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceChannelAccountEntryRelTable.INSTANCE);
	}

	/**
	 * Creates a new commerce channel account entry rel with the primary key. Does not add the commerce channel account entry rel to the database.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key for the new commerce channel account entry rel
	 * @return the new commerce channel account entry rel
	 */
	@Override
	public CommerceChannelAccountEntryRel create(
		long commerceChannelAccountEntryRelId) {

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			new CommerceChannelAccountEntryRelImpl();

		commerceChannelAccountEntryRel.setNew(true);
		commerceChannelAccountEntryRel.setPrimaryKey(
			commerceChannelAccountEntryRelId);

		commerceChannelAccountEntryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return commerceChannelAccountEntryRel;
	}

	/**
	 * Removes the commerce channel account entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel that was removed
	 * @throws NoSuchChannelAccountEntryRelException if a commerce channel account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel remove(
			long commerceChannelAccountEntryRelId)
		throws NoSuchChannelAccountEntryRelException {

		return remove((Serializable)commerceChannelAccountEntryRelId);
	}

	@Override
	protected CommerceChannelAccountEntryRel removeImpl(
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceChannelAccountEntryRel)) {
				commerceChannelAccountEntryRel =
					(CommerceChannelAccountEntryRel)session.get(
						CommerceChannelAccountEntryRelImpl.class,
						commerceChannelAccountEntryRel.getPrimaryKeyObj());
			}

			if ((commerceChannelAccountEntryRel != null) &&
				ctPersistenceHelper.isRemove(commerceChannelAccountEntryRel)) {

				session.delete(commerceChannelAccountEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceChannelAccountEntryRel != null) {
			clearCache(commerceChannelAccountEntryRel);
		}

		return commerceChannelAccountEntryRel;
	}

	@Override
	public CommerceChannelAccountEntryRel updateImpl(
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel) {

		boolean isNew = commerceChannelAccountEntryRel.isNew();

		if (!(commerceChannelAccountEntryRel instanceof
				CommerceChannelAccountEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					commerceChannelAccountEntryRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceChannelAccountEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceChannelAccountEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceChannelAccountEntryRel implementation " +
					commerceChannelAccountEntryRel.getClass());
		}

		CommerceChannelAccountEntryRelModelImpl
			commerceChannelAccountEntryRelModelImpl =
				(CommerceChannelAccountEntryRelModelImpl)
					commerceChannelAccountEntryRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceChannelAccountEntryRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceChannelAccountEntryRel.setCreateDate(date);
			}
			else {
				commerceChannelAccountEntryRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceChannelAccountEntryRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceChannelAccountEntryRel.setModifiedDate(date);
			}
			else {
				commerceChannelAccountEntryRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(commerceChannelAccountEntryRel)) {
				if (!isNew) {
					session.evict(
						CommerceChannelAccountEntryRelImpl.class,
						commerceChannelAccountEntryRel.getPrimaryKeyObj());
				}

				session.save(commerceChannelAccountEntryRel);
			}
			else {
				commerceChannelAccountEntryRel =
					(CommerceChannelAccountEntryRel)session.merge(
						commerceChannelAccountEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceChannelAccountEntryRel, false);

		if (isNew) {
			commerceChannelAccountEntryRel.setNew(false);
		}

		commerceChannelAccountEntryRel.resetOriginalValues();

		return commerceChannelAccountEntryRel;
	}

	/**
	 * Returns the commerce channel account entry rel with the primary key or throws a <code>NoSuchChannelAccountEntryRelException</code> if it could not be found.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel
	 * @throws NoSuchChannelAccountEntryRelException if a commerce channel account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel findByPrimaryKey(
			long commerceChannelAccountEntryRelId)
		throws NoSuchChannelAccountEntryRelException {

		return findByPrimaryKey((Serializable)commerceChannelAccountEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the commerce channel account entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceChannelAccountEntryRelId the primary key of the commerce channel account entry rel
	 * @return the commerce channel account entry rel, or <code>null</code> if a commerce channel account entry rel with the primary key could not be found
	 */
	@Override
	public CommerceChannelAccountEntryRel fetchByPrimaryKey(
		long commerceChannelAccountEntryRelId) {

		return fetchByPrimaryKey(
			(Serializable)commerceChannelAccountEntryRelId);
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
		return "CChannelAccountEntryRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "commerceChannelAccountEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL;
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
		return CommerceChannelAccountEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CChannelAccountEntryRel";
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
		ctMergeColumnNames.add("accountEntryId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("commerceChannelId");
		ctMergeColumnNames.add("overrideEligibility");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("type_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CChannelAccountEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"accountEntryId", "classNameId", "classPK", "commerceChannelId",
				"type_"
			});
	}

	/**
	 * Initializes the commerce channel account entry rel persistence.
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
				_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
				CommerceChannelAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceChannelAccountEntryRel.", "accountEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceChannelAccountEntryRel::getAccountEntryId));

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
				_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
				CommerceChannelAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceChannelAccountEntryRel.", "commerceChannelId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceChannelAccountEntryRel::getCommerceChannelId));

		_collectionPersistenceFinderByA_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"accountEntryId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"accountEntryId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByA_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"accountEntryId", "type_"}, false),
			_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			CommerceChannelAccountEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "accountEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getAccountEntryId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				CommerceChannelAccountEntryRel::getType));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			CommerceChannelAccountEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getClassNameId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getClassPK));

		_collectionPersistenceFinderByC_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"commerceChannelId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"commerceChannelId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"commerceChannelId", "type_"}, false),
			_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			CommerceChannelAccountEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "commerceChannelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getCommerceChannelId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				CommerceChannelAccountEntryRel::getType));

		_collectionPersistenceFinderByA_C_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"accountEntryId", "commerceChannelId", "type_"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"accountEntryId", "commerceChannelId", "type_"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByA_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"accountEntryId", "commerceChannelId", "type_"},
				false),
			_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
			CommerceChannelAccountEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "accountEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getAccountEntryId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "commerceChannelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getCommerceChannelId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				CommerceChannelAccountEntryRel::getType));

		_collectionPersistenceFinderByC_C_C_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "commerceChannelId", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "commerceChannelId", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "commerceChannelId", "type_"
					},
					false),
				_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
				_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE,
				CommerceChannelAccountEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceChannelAccountEntryRel.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceChannelAccountEntryRel::getClassNameId),
				new FinderColumn<>(
					"commerceChannelAccountEntryRel.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceChannelAccountEntryRel::getClassPK),
				new FinderColumn<>(
					"commerceChannelAccountEntryRel.", "commerceChannelId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceChannelAccountEntryRel::getCommerceChannelId),
				new FinderColumn<>(
					"commerceChannelAccountEntryRel.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					CommerceChannelAccountEntryRel::getType));

		_uniquePersistenceFinderByA_C_C_C_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_C_C_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"accountEntryId", "classNameId", "classPK",
					"commerceChannelId", "type_"
				},
				0, 0, false, CommerceChannelAccountEntryRel::getAccountEntryId,
				CommerceChannelAccountEntryRel::getClassNameId,
				CommerceChannelAccountEntryRel::getClassPK,
				CommerceChannelAccountEntryRel::getCommerceChannelId,
				CommerceChannelAccountEntryRel::getType),
			_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE, "",
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "accountEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getAccountEntryId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getClassNameId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "classPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getClassPK),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "commerceChannelId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceChannelAccountEntryRel::getCommerceChannelId),
			new FinderColumn<>(
				"commerceChannelAccountEntryRel.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				CommerceChannelAccountEntryRel::getType));

		CommerceChannelAccountEntryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceChannelAccountEntryRelUtil.setPersistence(null);

		entityCache.removeCache(
			CommerceChannelAccountEntryRelImpl.class.getName());
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
		CommerceChannelAccountEntryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL =
		"SELECT commerceChannelAccountEntryRel FROM CommerceChannelAccountEntryRel commerceChannelAccountEntryRel";

	private static final String
		_SQL_SELECT_COMMERCECHANNELACCOUNTENTRYREL_WHERE =
			"SELECT commerceChannelAccountEntryRel FROM CommerceChannelAccountEntryRel commerceChannelAccountEntryRel WHERE ";

	private static final String
		_SQL_COUNT_COMMERCECHANNELACCOUNTENTRYREL_WHERE =
			"SELECT COUNT(commerceChannelAccountEntryRel) FROM CommerceChannelAccountEntryRel commerceChannelAccountEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceChannelAccountEntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceChannelAccountEntryRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"commerceChannelAccountEntryRelId", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-212250106