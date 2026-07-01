/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.impl;

import com.liferay.expando.kernel.exception.NoSuchValueException;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.model.ExpandoValueTable;
import com.liferay.expando.kernel.service.persistence.ExpandoValuePersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoValueUtil;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.expando.model.impl.ExpandoValueImpl;
import com.liferay.portlet.expando.model.impl.ExpandoValueModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the expando value service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ExpandoValuePersistenceImpl
	extends BasePersistenceImpl<ExpandoValue, NoSuchValueException>
	implements ExpandoValuePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ExpandoValueUtil</code> to access the expando value persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ExpandoValueImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByTableId;

	/**
	 * Returns an ordered range of all the expando values where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByTableId(
		long tableId, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTableId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByTableId_First(
			long tableId, OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByTableId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId},
			orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByTableId_First(
		long tableId, OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByTableId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId},
			orderByComparator);
	}

	/**
	 * Removes all the expando values where tableId = &#63; from the database.
	 *
	 * @param tableId the table ID
	 */
	@Override
	public void removeByTableId(long tableId) {
		_collectionPersistenceFinderByTableId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId});
	}

	/**
	 * Returns the number of expando values where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the number of matching expando values
	 */
	@Override
	public int countByTableId(long tableId) {
		return _collectionPersistenceFinderByTableId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId});
	}

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByColumnId;

	/**
	 * Returns an ordered range of all the expando values where columnId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param columnId the column ID
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByColumnId(
		long columnId, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByColumnId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where columnId = &#63;.
	 *
	 * @param columnId the column ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByColumnId_First(
			long columnId, OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByColumnId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId},
			orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where columnId = &#63;.
	 *
	 * @param columnId the column ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByColumnId_First(
		long columnId, OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByColumnId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId},
			orderByComparator);
	}

	/**
	 * Removes all the expando values where columnId = &#63; from the database.
	 *
	 * @param columnId the column ID
	 */
	@Override
	public void removeByColumnId(long columnId) {
		_collectionPersistenceFinderByColumnId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId});
	}

	/**
	 * Returns the number of expando values where columnId = &#63;.
	 *
	 * @param columnId the column ID
	 * @return the number of matching expando values
	 */
	@Override
	public int countByColumnId(long columnId) {
		return _collectionPersistenceFinderByColumnId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId});
	}

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByRowId;

	/**
	 * Returns an ordered range of all the expando values where rowId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param rowId the row ID
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByRowId(
		long rowId, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRowId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {rowId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where rowId = &#63;.
	 *
	 * @param rowId the row ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByRowId_First(
			long rowId, OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByRowId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {rowId},
			orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where rowId = &#63;.
	 *
	 * @param rowId the row ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByRowId_First(
		long rowId, OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByRowId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {rowId},
			orderByComparator);
	}

	/**
	 * Removes all the expando values where rowId = &#63; from the database.
	 *
	 * @param rowId the row ID
	 */
	@Override
	public void removeByRowId(long rowId) {
		_collectionPersistenceFinderByRowId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {rowId});
	}

	/**
	 * Returns the number of expando values where rowId = &#63;.
	 *
	 * @param rowId the row ID
	 * @return the number of matching expando values
	 */
	@Override
	public int countByRowId(long rowId) {
		return _collectionPersistenceFinderByRowId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {rowId});
	}

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByT_C;

	/**
	 * Returns an ordered range of all the expando values where tableId = &#63; and columnId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByT_C(
		long tableId, long columnId, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, columnId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and columnId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByT_C_First(
			long tableId, long columnId,
			OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByT_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, columnId},
			orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and columnId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByT_C_First(
		long tableId, long columnId,
		OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByT_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, columnId},
			orderByComparator);
	}

	/**
	 * Removes all the expando values where tableId = &#63; and columnId = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 */
	@Override
	public void removeByT_C(long tableId, long columnId) {
		_collectionPersistenceFinderByT_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, columnId});
	}

	/**
	 * Returns the number of expando values where tableId = &#63; and columnId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @return the number of matching expando values
	 */
	@Override
	public int countByT_C(long tableId, long columnId) {
		return _collectionPersistenceFinderByT_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, columnId});
	}

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByT_R;

	/**
	 * Returns an ordered range of all the expando values where tableId = &#63; and rowId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param rowId the row ID
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByT_R(
		long tableId, long rowId, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_R.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, rowId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and rowId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param rowId the row ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByT_R_First(
			long tableId, long rowId,
			OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByT_R.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, rowId},
			orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and rowId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param rowId the row ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByT_R_First(
		long tableId, long rowId,
		OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByT_R.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, rowId},
			orderByComparator);
	}

	/**
	 * Removes all the expando values where tableId = &#63; and rowId = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param rowId the row ID
	 */
	@Override
	public void removeByT_R(long tableId, long rowId) {
		_collectionPersistenceFinderByT_R.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, rowId});
	}

	/**
	 * Returns the number of expando values where tableId = &#63; and rowId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param rowId the row ID
	 * @return the number of matching expando values
	 */
	@Override
	public int countByT_R(long tableId, long rowId) {
		return _collectionPersistenceFinderByT_R.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, rowId});
	}

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByT_CPK;

	/**
	 * Returns an ordered range of all the expando values where tableId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByT_CPK(
		long tableId, long classPK, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_CPK.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and classPK = &#63;.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByT_CPK_First(
			long tableId, long classPK,
			OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByT_CPK.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and classPK = &#63;.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByT_CPK_First(
		long tableId, long classPK,
		OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByT_CPK.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the expando values where tableId = &#63; and classPK = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByT_CPK(long tableId, long classPK) {
		_collectionPersistenceFinderByT_CPK.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK});
	}

	/**
	 * Returns the number of expando values where tableId = &#63; and classPK = &#63;.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @return the number of matching expando values
	 */
	@Override
	public int countByT_CPK(long tableId, long classPK) {
		return _collectionPersistenceFinderByT_CPK.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK});
	}

	private UniquePersistenceFinder<ExpandoValue, NoSuchValueException>
		_uniquePersistenceFinderByC_R;

	/**
	 * Returns the expando value where columnId = &#63; and rowId = &#63; or throws a <code>NoSuchValueException</code> if it could not be found.
	 *
	 * @param columnId the column ID
	 * @param rowId the row ID
	 * @return the matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByC_R(long columnId, long rowId)
		throws NoSuchValueException {

		return _uniquePersistenceFinderByC_R.find(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId, rowId});
	}

	/**
	 * Returns the expando value where columnId = &#63; and rowId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param columnId the column ID
	 * @param rowId the row ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByC_R(
		long columnId, long rowId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_R.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId, rowId},
			useFinderCache);
	}

	/**
	 * Removes the expando value where columnId = &#63; and rowId = &#63; from the database.
	 *
	 * @param columnId the column ID
	 * @param rowId the row ID
	 * @return the expando value that was removed
	 */
	@Override
	public ExpandoValue removeByC_R(long columnId, long rowId)
		throws NoSuchValueException {

		ExpandoValue expandoValue = findByC_R(columnId, rowId);

		return remove(expandoValue);
	}

	/**
	 * Returns the number of expando values where columnId = &#63; and rowId = &#63;.
	 *
	 * @param columnId the column ID
	 * @param rowId the row ID
	 * @return the number of matching expando values
	 */
	@Override
	public int countByC_R(long columnId, long rowId) {
		return _uniquePersistenceFinderByC_R.count(
			FinderCacheUtil.getFinderCache(), new Object[] {columnId, rowId});
	}

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the expando values where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the expando values where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of expando values where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching expando values
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder<ExpandoValue, NoSuchValueException>
		_uniquePersistenceFinderByT_C_C;

	/**
	 * Returns the expando value where tableId = &#63; and columnId = &#63; and classPK = &#63; or throws a <code>NoSuchValueException</code> if it could not be found.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param classPK the class pk
	 * @return the matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByT_C_C(long tableId, long columnId, long classPK)
		throws NoSuchValueException {

		return _uniquePersistenceFinderByT_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, classPK});
	}

	/**
	 * Returns the expando value where tableId = &#63; and columnId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByT_C_C(
		long tableId, long columnId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByT_C_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, classPK}, useFinderCache);
	}

	/**
	 * Removes the expando value where tableId = &#63; and columnId = &#63; and classPK = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param classPK the class pk
	 * @return the expando value that was removed
	 */
	@Override
	public ExpandoValue removeByT_C_C(long tableId, long columnId, long classPK)
		throws NoSuchValueException {

		ExpandoValue expandoValue = findByT_C_C(tableId, columnId, classPK);

		return remove(expandoValue);
	}

	/**
	 * Returns the number of expando values where tableId = &#63; and columnId = &#63; and classPK = &#63;.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param classPK the class pk
	 * @return the number of matching expando values
	 */
	@Override
	public int countByT_C_C(long tableId, long columnId, long classPK) {
		return _uniquePersistenceFinderByT_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, classPK});
	}

	private CollectionPersistenceFinder<ExpandoValue, NoSuchValueException>
		_collectionPersistenceFinderByT_C_D;

	/**
	 * Returns an ordered range of all the expando values where tableId = &#63; and columnId = &#63; and data = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param data the data
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando values
	 */
	@Override
	public List<ExpandoValue> findByT_C_D(
		long tableId, long columnId, String data, int start, int end,
		OrderByComparator<ExpandoValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_C_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, data}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and columnId = &#63; and data = &#63;.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param data the data
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value
	 * @throws NoSuchValueException if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue findByT_C_D_First(
			long tableId, long columnId, String data,
			OrderByComparator<ExpandoValue> orderByComparator)
		throws NoSuchValueException {

		return _collectionPersistenceFinderByT_C_D.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, data}, orderByComparator);
	}

	/**
	 * Returns the first expando value in the ordered set where tableId = &#63; and columnId = &#63; and data = &#63;.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param data the data
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando value, or <code>null</code> if a matching expando value could not be found
	 */
	@Override
	public ExpandoValue fetchByT_C_D_First(
		long tableId, long columnId, String data,
		OrderByComparator<ExpandoValue> orderByComparator) {

		return _collectionPersistenceFinderByT_C_D.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, data}, orderByComparator);
	}

	/**
	 * Removes all the expando values where tableId = &#63; and columnId = &#63; and data = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param data the data
	 */
	@Override
	public void removeByT_C_D(long tableId, long columnId, String data) {
		_collectionPersistenceFinderByT_C_D.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, data});
	}

	/**
	 * Returns the number of expando values where tableId = &#63; and columnId = &#63; and data = &#63;.
	 *
	 * @param tableId the table ID
	 * @param columnId the column ID
	 * @param data the data
	 * @return the number of matching expando values
	 */
	@Override
	public int countByT_C_D(long tableId, long columnId, String data) {
		return _collectionPersistenceFinderByT_C_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, columnId, data});
	}

	public ExpandoValuePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("rowId", "rowId_");
		dbColumnNames.put("data", "data_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ExpandoValue.class);

		setModelImplClass(ExpandoValueImpl.class);
		setModelPKClass(long.class);

		setTable(ExpandoValueTable.INSTANCE);
	}

	/**
	 * Creates a new expando value with the primary key. Does not add the expando value to the database.
	 *
	 * @param valueId the primary key for the new expando value
	 * @return the new expando value
	 */
	@Override
	public ExpandoValue create(long valueId) {
		ExpandoValue expandoValue = new ExpandoValueImpl();

		expandoValue.setNew(true);
		expandoValue.setPrimaryKey(valueId);

		expandoValue.setCompanyId(CompanyThreadLocal.getCompanyId());

		return expandoValue;
	}

	/**
	 * Removes the expando value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param valueId the primary key of the expando value
	 * @return the expando value that was removed
	 * @throws NoSuchValueException if a expando value with the primary key could not be found
	 */
	@Override
	public ExpandoValue remove(long valueId) throws NoSuchValueException {
		return remove((Serializable)valueId);
	}

	@Override
	protected ExpandoValue removeImpl(ExpandoValue expandoValue) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(expandoValue)) {
				expandoValue = (ExpandoValue)session.get(
					ExpandoValueImpl.class, expandoValue.getPrimaryKeyObj());
			}

			if ((expandoValue != null) &&
				CTPersistenceHelperUtil.isRemove(expandoValue)) {

				session.delete(expandoValue);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (expandoValue != null) {
			clearCache(expandoValue);
		}

		return expandoValue;
	}

	@Override
	public ExpandoValue updateImpl(ExpandoValue expandoValue) {
		boolean isNew = expandoValue.isNew();

		if (!(expandoValue instanceof ExpandoValueModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(expandoValue.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					expandoValue);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in expandoValue proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ExpandoValue implementation " +
					expandoValue.getClass());
		}

		ExpandoValueModelImpl expandoValueModelImpl =
			(ExpandoValueModelImpl)expandoValue;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(expandoValue)) {
				if (!isNew) {
					session.evict(
						ExpandoValueImpl.class,
						expandoValue.getPrimaryKeyObj());
				}

				session.save(expandoValue);
			}
			else {
				expandoValue = (ExpandoValue)session.merge(expandoValue);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(expandoValue, false);

		if (isNew) {
			expandoValue.setNew(false);
		}

		expandoValue.resetOriginalValues();

		return expandoValue;
	}

	/**
	 * Returns the expando value with the primary key or throws a <code>NoSuchValueException</code> if it could not be found.
	 *
	 * @param valueId the primary key of the expando value
	 * @return the expando value
	 * @throws NoSuchValueException if a expando value with the primary key could not be found
	 */
	@Override
	public ExpandoValue findByPrimaryKey(long valueId)
		throws NoSuchValueException {

		return findByPrimaryKey((Serializable)valueId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the expando value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param valueId the primary key of the expando value
	 * @return the expando value, or <code>null</code> if a expando value with the primary key could not be found
	 */
	@Override
	public ExpandoValue fetchByPrimaryKey(long valueId) {
		return fetchByPrimaryKey((Serializable)valueId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "valueId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EXPANDOVALUE;
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
		return ExpandoValueModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ExpandoValue";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("tableId");
		ctStrictColumnNames.add("columnId");
		ctStrictColumnNames.add("rowId_");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("data_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("valueId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"columnId", "rowId_"});

		_uniqueIndexColumnNames.add(
			new String[] {"tableId", "columnId", "classPK"});
	}

	/**
	 * Initializes the expando value persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByTableId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTableId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"tableId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTableId",
					new String[] {Long.class.getName()},
					new String[] {"tableId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTableId",
					new String[] {Long.class.getName()},
					new String[] {"tableId"}, false),
				_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
				ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"expandoValue.", "tableId", FinderColumn.Type.LONG, "=",
					true, true, ExpandoValue::getTableId));

		_collectionPersistenceFinderByColumnId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByColumnId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"columnId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByColumnId",
					new String[] {Long.class.getName()},
					new String[] {"columnId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByColumnId", new String[] {Long.class.getName()},
					new String[] {"columnId"}, false),
				_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
				ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"expandoValue.", "columnId", FinderColumn.Type.LONG, "=",
					true, true, ExpandoValue::getColumnId));

		_collectionPersistenceFinderByRowId = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRowId",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"rowId_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRowId",
				new String[] {Long.class.getName()}, new String[] {"rowId_"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRowId",
				new String[] {Long.class.getName()}, new String[] {"rowId_"},
				false),
			_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
			ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"expandoValue.", "rowId", "rowId_", FinderColumn.Type.LONG, "=",
				true, true, ExpandoValue::getRowId));

		_collectionPersistenceFinderByT_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"tableId", "columnId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"tableId", "columnId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"tableId", "columnId"}, false),
			_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
			ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"expandoValue.", "tableId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getTableId),
			new FinderColumn<>(
				"expandoValue.", "columnId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getColumnId));

		_collectionPersistenceFinderByT_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"tableId", "rowId_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"tableId", "rowId_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"tableId", "rowId_"}, false),
			_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
			ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"expandoValue.", "tableId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getTableId),
			new FinderColumn<>(
				"expandoValue.", "rowId", "rowId_", FinderColumn.Type.LONG, "=",
				true, true, ExpandoValue::getRowId));

		_collectionPersistenceFinderByT_CPK = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_CPK",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"tableId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_CPK",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"tableId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_CPK",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"tableId", "classPK"}, false),
			_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
			ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"expandoValue.", "tableId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getTableId),
			new FinderColumn<>(
				"expandoValue.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getClassPK));

		_uniquePersistenceFinderByC_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"columnId", "rowId_"}, 0, 0, false,
				ExpandoValue::getColumnId, ExpandoValue::getRowId),
			_SQL_SELECT_EXPANDOVALUE_WHERE, "",
			new FinderColumn<>(
				"expandoValue.", "columnId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getColumnId),
			new FinderColumn<>(
				"expandoValue.", "rowId", "rowId_", FinderColumn.Type.LONG, "=",
				true, true, ExpandoValue::getRowId));

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
			_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
			ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"expandoValue.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ExpandoValue::getClassNameId),
			new FinderColumn<>(
				"expandoValue.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getClassPK));

		_uniquePersistenceFinderByT_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByT_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"tableId", "columnId", "classPK"}, 0, 0, false,
				ExpandoValue::getTableId, ExpandoValue::getColumnId,
				ExpandoValue::getClassPK),
			_SQL_SELECT_EXPANDOVALUE_WHERE, "",
			new FinderColumn<>(
				"expandoValue.", "tableId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getTableId),
			new FinderColumn<>(
				"expandoValue.", "columnId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getColumnId),
			new FinderColumn<>(
				"expandoValue.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getClassPK));

		_collectionPersistenceFinderByT_C_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_C_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"tableId", "columnId", "data_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_C_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"tableId", "columnId", "data_"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_C_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"tableId", "columnId", "data_"}, 0, 4, false,
				null),
			_SQL_SELECT_EXPANDOVALUE_WHERE, _SQL_COUNT_EXPANDOVALUE_WHERE,
			ExpandoValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"expandoValue.", "tableId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getTableId),
			new FinderColumn<>(
				"expandoValue.", "columnId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoValue::getColumnId),
			new FinderColumn<>(
				"expandoValue.", "data", "data_", FinderColumn.Type.STRING, "=",
				true, true, ExpandoValue::getData));

		ExpandoValueUtil.setPersistence(this);
	}

	public void destroy() {
		ExpandoValueUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ExpandoValueImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ExpandoValueModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EXPANDOVALUE =
		"SELECT expandoValue FROM ExpandoValue expandoValue";

	private static final String _SQL_SELECT_EXPANDOVALUE_WHERE =
		"SELECT expandoValue FROM ExpandoValue expandoValue WHERE ";

	private static final String _SQL_COUNT_EXPANDOVALUE_WHERE =
		"SELECT COUNT(expandoValue) FROM ExpandoValue expandoValue WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ExpandoValue exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoValuePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"rowId", "data"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1089400537