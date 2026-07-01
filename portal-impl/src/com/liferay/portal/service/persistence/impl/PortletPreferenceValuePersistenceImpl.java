/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferenceValueException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.model.PortletPreferenceValueTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.PortletPreferenceValuePersistence;
import com.liferay.portal.kernel.service.persistence.PortletPreferenceValueUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.PortletPreferenceValueImpl;
import com.liferay.portal.model.impl.PortletPreferenceValueModelImpl;

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
 * The persistence implementation for the portlet preference value service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortletPreferenceValuePersistenceImpl
	extends BasePersistenceImpl
		<PortletPreferenceValue, NoSuchPortletPreferenceValueException>
	implements PortletPreferenceValuePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PortletPreferenceValueUtil</code> to access the portlet preference value persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PortletPreferenceValueImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<PortletPreferenceValue, NoSuchPortletPreferenceValueException>
			_collectionPersistenceFinderByPortletPreferencesId;

	/**
	 * Returns an ordered range of all the portlet preference values where portletPreferencesId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preference values
	 */
	@Override
	public List<PortletPreferenceValue> findByPortletPreferencesId(
		long portletPreferencesId, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPortletPreferencesId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue findByPortletPreferencesId_First(
			long portletPreferencesId,
			OrderByComparator<PortletPreferenceValue> orderByComparator)
		throws NoSuchPortletPreferenceValueException {

		return _collectionPersistenceFinderByPortletPreferencesId.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId}, orderByComparator);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue fetchByPortletPreferencesId_First(
		long portletPreferencesId,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return _collectionPersistenceFinderByPortletPreferencesId.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId}, orderByComparator);
	}

	/**
	 * Removes all the portlet preference values where portletPreferencesId = &#63; from the database.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 */
	@Override
	public void removeByPortletPreferencesId(long portletPreferencesId) {
		_collectionPersistenceFinderByPortletPreferencesId.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId});
	}

	/**
	 * Returns the number of portlet preference values where portletPreferencesId = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @return the number of matching portlet preference values
	 */
	@Override
	public int countByPortletPreferencesId(long portletPreferencesId) {
		return _collectionPersistenceFinderByPortletPreferencesId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId});
	}

	private CollectionPersistenceFinder
		<PortletPreferenceValue, NoSuchPortletPreferenceValueException>
			_collectionPersistenceFinderByP_N;

	/**
	 * Returns an ordered range of all the portlet preference values where portletPreferencesId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preference values
	 */
	@Override
	public List<PortletPreferenceValue> findByP_N(
		long portletPreferencesId, String name, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63; and name = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue findByP_N_First(
			long portletPreferencesId, String name,
			OrderByComparator<PortletPreferenceValue> orderByComparator)
		throws NoSuchPortletPreferenceValueException {

		return _collectionPersistenceFinderByP_N.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name}, orderByComparator);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63; and name = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue fetchByP_N_First(
		long portletPreferencesId, String name,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return _collectionPersistenceFinderByP_N.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name}, orderByComparator);
	}

	/**
	 * Removes all the portlet preference values where portletPreferencesId = &#63; and name = &#63; from the database.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 */
	@Override
	public void removeByP_N(long portletPreferencesId, String name) {
		_collectionPersistenceFinderByP_N.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name});
	}

	/**
	 * Returns the number of portlet preference values where portletPreferencesId = &#63; and name = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @return the number of matching portlet preference values
	 */
	@Override
	public int countByP_N(long portletPreferencesId, String name) {
		return _collectionPersistenceFinderByP_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name});
	}

	private CollectionPersistenceFinder
		<PortletPreferenceValue, NoSuchPortletPreferenceValueException>
			_collectionPersistenceFinderByC_N_SV;

	/**
	 * Returns an ordered range of all the portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preference values
	 */
	@Override
	public List<PortletPreferenceValue> findByC_N_SV(
		long companyId, String name, String smallValue, int start, int end,
		OrderByComparator<PortletPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_N_SV.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, smallValue}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue findByC_N_SV_First(
			long companyId, String name, String smallValue,
			OrderByComparator<PortletPreferenceValue> orderByComparator)
		throws NoSuchPortletPreferenceValueException {

		return _collectionPersistenceFinderByC_N_SV.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, smallValue}, orderByComparator);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue fetchByC_N_SV_First(
		long companyId, String name, String smallValue,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return _collectionPersistenceFinderByC_N_SV.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, smallValue}, orderByComparator);
	}

	/**
	 * Removes all the portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 */
	@Override
	public void removeByC_N_SV(long companyId, String name, String smallValue) {
		_collectionPersistenceFinderByC_N_SV.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, smallValue});
	}

	/**
	 * Returns the number of portlet preference values where companyId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param smallValue the small value
	 * @return the number of matching portlet preference values
	 */
	@Override
	public int countByC_N_SV(long companyId, String name, String smallValue) {
		return _collectionPersistenceFinderByC_N_SV.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, smallValue});
	}

	private UniquePersistenceFinder
		<PortletPreferenceValue, NoSuchPortletPreferenceValueException>
			_uniquePersistenceFinderByP_I_N;

	/**
	 * Returns the portlet preference value where portletPreferencesId = &#63; and index = &#63; and name = &#63; or throws a <code>NoSuchPortletPreferenceValueException</code> if it could not be found.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @return the matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue findByP_I_N(
			long portletPreferencesId, int index, String name)
		throws NoSuchPortletPreferenceValueException {

		return _uniquePersistenceFinderByP_I_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, index, name});
	}

	/**
	 * Returns the portlet preference value where portletPreferencesId = &#63; and index = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue fetchByP_I_N(
		long portletPreferencesId, int index, String name,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByP_I_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, index, name}, useFinderCache);
	}

	/**
	 * Removes the portlet preference value where portletPreferencesId = &#63; and index = &#63; and name = &#63; from the database.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @return the portlet preference value that was removed
	 */
	@Override
	public PortletPreferenceValue removeByP_I_N(
			long portletPreferencesId, int index, String name)
		throws NoSuchPortletPreferenceValueException {

		PortletPreferenceValue portletPreferenceValue = findByP_I_N(
			portletPreferencesId, index, name);

		return remove(portletPreferenceValue);
	}

	/**
	 * Returns the number of portlet preference values where portletPreferencesId = &#63; and index = &#63; and name = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param index the index
	 * @param name the name
	 * @return the number of matching portlet preference values
	 */
	@Override
	public int countByP_I_N(long portletPreferencesId, int index, String name) {
		return _uniquePersistenceFinderByP_I_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, index, name});
	}

	private CollectionPersistenceFinder
		<PortletPreferenceValue, NoSuchPortletPreferenceValueException>
			_collectionPersistenceFinderByP_N_SV;

	/**
	 * Returns an ordered range of all the portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet preference values
	 */
	@Override
	public List<PortletPreferenceValue> findByP_N_SV(
		long portletPreferencesId, String name, String smallValue, int start,
		int end, OrderByComparator<PortletPreferenceValue> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_N_SV.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name, smallValue}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue findByP_N_SV_First(
			long portletPreferencesId, String name, String smallValue,
			OrderByComparator<PortletPreferenceValue> orderByComparator)
		throws NoSuchPortletPreferenceValueException {

		return _collectionPersistenceFinderByP_N_SV.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name, smallValue},
			orderByComparator);
	}

	/**
	 * Returns the first portlet preference value in the ordered set where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet preference value, or <code>null</code> if a matching portlet preference value could not be found
	 */
	@Override
	public PortletPreferenceValue fetchByP_N_SV_First(
		long portletPreferencesId, String name, String smallValue,
		OrderByComparator<PortletPreferenceValue> orderByComparator) {

		return _collectionPersistenceFinderByP_N_SV.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name, smallValue},
			orderByComparator);
	}

	/**
	 * Removes all the portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63; from the database.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 */
	@Override
	public void removeByP_N_SV(
		long portletPreferencesId, String name, String smallValue) {

		_collectionPersistenceFinderByP_N_SV.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name, smallValue});
	}

	/**
	 * Returns the number of portlet preference values where portletPreferencesId = &#63; and name = &#63; and smallValue = &#63;.
	 *
	 * @param portletPreferencesId the portlet preferences ID
	 * @param name the name
	 * @param smallValue the small value
	 * @return the number of matching portlet preference values
	 */
	@Override
	public int countByP_N_SV(
		long portletPreferencesId, String name, String smallValue) {

		return _collectionPersistenceFinderByP_N_SV.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {portletPreferencesId, name, smallValue});
	}

	public PortletPreferenceValuePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("index", "index_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PortletPreferenceValue.class);

		setModelImplClass(PortletPreferenceValueImpl.class);
		setModelPKClass(long.class);

		setTable(PortletPreferenceValueTable.INSTANCE);
	}

	/**
	 * Creates a new portlet preference value with the primary key. Does not add the portlet preference value to the database.
	 *
	 * @param portletPreferenceValueId the primary key for the new portlet preference value
	 * @return the new portlet preference value
	 */
	@Override
	public PortletPreferenceValue create(long portletPreferenceValueId) {
		PortletPreferenceValue portletPreferenceValue =
			new PortletPreferenceValueImpl();

		portletPreferenceValue.setNew(true);
		portletPreferenceValue.setPrimaryKey(portletPreferenceValueId);

		portletPreferenceValue.setCompanyId(CompanyThreadLocal.getCompanyId());

		return portletPreferenceValue;
	}

	/**
	 * Removes the portlet preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value that was removed
	 * @throws NoSuchPortletPreferenceValueException if a portlet preference value with the primary key could not be found
	 */
	@Override
	public PortletPreferenceValue remove(long portletPreferenceValueId)
		throws NoSuchPortletPreferenceValueException {

		return remove((Serializable)portletPreferenceValueId);
	}

	@Override
	protected PortletPreferenceValue removeImpl(
		PortletPreferenceValue portletPreferenceValue) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(portletPreferenceValue)) {
				portletPreferenceValue = (PortletPreferenceValue)session.get(
					PortletPreferenceValueImpl.class,
					portletPreferenceValue.getPrimaryKeyObj());
			}

			if ((portletPreferenceValue != null) &&
				CTPersistenceHelperUtil.isRemove(portletPreferenceValue)) {

				session.delete(portletPreferenceValue);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (portletPreferenceValue != null) {
			clearCache(portletPreferenceValue);
		}

		return portletPreferenceValue;
	}

	@Override
	public PortletPreferenceValue updateImpl(
		PortletPreferenceValue portletPreferenceValue) {

		boolean isNew = portletPreferenceValue.isNew();

		if (!(portletPreferenceValue instanceof
				PortletPreferenceValueModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(portletPreferenceValue.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					portletPreferenceValue);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in portletPreferenceValue proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PortletPreferenceValue implementation " +
					portletPreferenceValue.getClass());
		}

		PortletPreferenceValueModelImpl portletPreferenceValueModelImpl =
			(PortletPreferenceValueModelImpl)portletPreferenceValue;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(portletPreferenceValue)) {
				if (!isNew) {
					session.evict(
						PortletPreferenceValueImpl.class,
						portletPreferenceValue.getPrimaryKeyObj());
				}

				session.save(portletPreferenceValue);
			}
			else {
				portletPreferenceValue = (PortletPreferenceValue)session.merge(
					portletPreferenceValue);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(portletPreferenceValue, false);

		if (isNew) {
			portletPreferenceValue.setNew(false);
		}

		portletPreferenceValue.resetOriginalValues();

		return portletPreferenceValue;
	}

	/**
	 * Returns the portlet preference value with the primary key or throws a <code>NoSuchPortletPreferenceValueException</code> if it could not be found.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value
	 * @throws NoSuchPortletPreferenceValueException if a portlet preference value with the primary key could not be found
	 */
	@Override
	public PortletPreferenceValue findByPrimaryKey(
			long portletPreferenceValueId)
		throws NoSuchPortletPreferenceValueException {

		return findByPrimaryKey((Serializable)portletPreferenceValueId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the portlet preference value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value, or <code>null</code> if a portlet preference value with the primary key could not be found
	 */
	@Override
	public PortletPreferenceValue fetchByPrimaryKey(
		long portletPreferenceValueId) {

		return fetchByPrimaryKey((Serializable)portletPreferenceValueId);
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
		return "portletPreferenceValueId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PORTLETPREFERENCEVALUE;
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
		return PortletPreferenceValueModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "PortletPreferenceValue";
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
		ctMergeColumnNames.add("portletPreferencesId");
		ctMergeColumnNames.add("index_");
		ctMergeColumnNames.add("largeValue");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("readOnly");
		ctMergeColumnNames.add("smallValue");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("portletPreferenceValueId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"portletPreferencesId", "index_", "name"});
	}

	/**
	 * Initializes the portlet preference value persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByPortletPreferencesId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPortletPreferencesId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"portletPreferencesId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPortletPreferencesId",
					new String[] {Long.class.getName()},
					new String[] {"portletPreferencesId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPortletPreferencesId",
					new String[] {Long.class.getName()},
					new String[] {"portletPreferencesId"}, false),
				_SQL_SELECT_PORTLETPREFERENCEVALUE_WHERE,
				_SQL_COUNT_PORTLETPREFERENCEVALUE_WHERE,
				PortletPreferenceValueModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"portletPreferenceValue.", "portletPreferencesId",
					FinderColumn.Type.LONG, "=", true, true,
					PortletPreferenceValue::getPortletPreferencesId));

		_collectionPersistenceFinderByP_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"portletPreferencesId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"portletPreferencesId", "name"}, 0, 2, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"portletPreferencesId", "name"}, 0, 2, false,
				null),
			_SQL_SELECT_PORTLETPREFERENCEVALUE_WHERE,
			_SQL_COUNT_PORTLETPREFERENCEVALUE_WHERE,
			PortletPreferenceValueModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"portletPreferenceValue.", "portletPreferencesId",
				FinderColumn.Type.LONG, "=", true, true,
				PortletPreferenceValue::getPortletPreferencesId),
			new FinderColumn<>(
				"portletPreferenceValue.", "name", FinderColumn.Type.STRING,
				"=", true, true, PortletPreferenceValue::getName));

		_collectionPersistenceFinderByC_N_SV =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_SV",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name", "smallValue"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_SV",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"companyId", "name", "smallValue"}, 0, 6,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_SV",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"companyId", "name", "smallValue"}, 0, 6,
					false, null),
				_SQL_SELECT_PORTLETPREFERENCEVALUE_WHERE,
				_SQL_COUNT_PORTLETPREFERENCEVALUE_WHERE,
				PortletPreferenceValueModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"portletPreferenceValue.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					PortletPreferenceValue::getCompanyId),
				new FinderColumn<>(
					"portletPreferenceValue.", "name", FinderColumn.Type.STRING,
					"=", true, true, PortletPreferenceValue::getName),
				new FinderColumn<>(
					"portletPreferenceValue.", "smallValue",
					FinderColumn.Type.STRING, "=", true, true,
					PortletPreferenceValue::getSmallValue));

		_uniquePersistenceFinderByP_I_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByP_I_N",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					String.class.getName()
				},
				new String[] {"portletPreferencesId", "index_", "name"}, 0, 4,
				false, PortletPreferenceValue::getPortletPreferencesId,
				PortletPreferenceValue::getIndex,
				convertNullFunction(PortletPreferenceValue::getName)),
			_SQL_SELECT_PORTLETPREFERENCEVALUE_WHERE, "",
			new FinderColumn<>(
				"portletPreferenceValue.", "portletPreferencesId",
				FinderColumn.Type.LONG, "=", true, true,
				PortletPreferenceValue::getPortletPreferencesId),
			new FinderColumn<>(
				"portletPreferenceValue.", "index", "index_",
				FinderColumn.Type.INTEGER, "=", true, true,
				PortletPreferenceValue::getIndex),
			new FinderColumn<>(
				"portletPreferenceValue.", "name", FinderColumn.Type.STRING,
				"=", true, true, PortletPreferenceValue::getName));

		_collectionPersistenceFinderByP_N_SV =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_N_SV",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"portletPreferencesId", "name", "smallValue"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_N_SV",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"portletPreferencesId", "name", "smallValue"},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_N_SV",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {"portletPreferencesId", "name", "smallValue"},
					0, 6, false, null),
				_SQL_SELECT_PORTLETPREFERENCEVALUE_WHERE,
				_SQL_COUNT_PORTLETPREFERENCEVALUE_WHERE,
				PortletPreferenceValueModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"portletPreferenceValue.", "portletPreferencesId",
					FinderColumn.Type.LONG, "=", true, true,
					PortletPreferenceValue::getPortletPreferencesId),
				new FinderColumn<>(
					"portletPreferenceValue.", "name", FinderColumn.Type.STRING,
					"=", true, true, PortletPreferenceValue::getName),
				new FinderColumn<>(
					"portletPreferenceValue.", "smallValue",
					FinderColumn.Type.STRING, "=", true, true,
					PortletPreferenceValue::getSmallValue));

		PortletPreferenceValueUtil.setPersistence(this);
	}

	public void destroy() {
		PortletPreferenceValueUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PortletPreferenceValueImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PortletPreferenceValueModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PORTLETPREFERENCEVALUE =
		"SELECT portletPreferenceValue FROM PortletPreferenceValue portletPreferenceValue";

	private static final String _SQL_SELECT_PORTLETPREFERENCEVALUE_WHERE =
		"SELECT portletPreferenceValue FROM PortletPreferenceValue portletPreferenceValue WHERE ";

	private static final String _SQL_COUNT_PORTLETPREFERENCEVALUE_WHERE =
		"SELECT COUNT(portletPreferenceValue) FROM PortletPreferenceValue portletPreferenceValue WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PortletPreferenceValue exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferenceValuePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"index"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1054442588