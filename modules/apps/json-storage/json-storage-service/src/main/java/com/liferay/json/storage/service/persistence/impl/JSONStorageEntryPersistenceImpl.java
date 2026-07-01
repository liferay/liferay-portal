/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.storage.service.persistence.impl;

import com.liferay.json.storage.exception.NoSuchJSONStorageEntryException;
import com.liferay.json.storage.model.JSONStorageEntry;
import com.liferay.json.storage.model.JSONStorageEntryTable;
import com.liferay.json.storage.model.impl.JSONStorageEntryImpl;
import com.liferay.json.storage.model.impl.JSONStorageEntryModelImpl;
import com.liferay.json.storage.service.persistence.JSONStorageEntryPersistence;
import com.liferay.json.storage.service.persistence.JSONStorageEntryUtil;
import com.liferay.json.storage.service.persistence.impl.constants.JSONStorePersistenceConstants;
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
 * The persistence implementation for the json storage entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Preston Crary
 * @generated
 */
@Component(service = JSONStorageEntryPersistence.class)
public class JSONStorageEntryPersistenceImpl
	extends BasePersistenceImpl
		<JSONStorageEntry, NoSuchJSONStorageEntryException>
	implements JSONStorageEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>JSONStorageEntryUtil</code> to access the json storage entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		JSONStorageEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<JSONStorageEntry, NoSuchJSONStorageEntryException>
			_collectionPersistenceFinderByCN_CPK;

	/**
	 * Returns an ordered range of all the json storage entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JSONStorageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of json storage entries
	 * @param end the upper bound of the range of json storage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching json storage entries
	 */
	@Override
	public List<JSONStorageEntry> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<JSONStorageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first json storage entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching json storage entry
	 * @throws NoSuchJSONStorageEntryException if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<JSONStorageEntry> orderByComparator)
		throws NoSuchJSONStorageEntryException {

		return _collectionPersistenceFinderByCN_CPK.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first json storage entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching json storage entry, or <code>null</code> if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<JSONStorageEntry> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the json storage entries where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of json storage entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching json storage entries
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _collectionPersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<JSONStorageEntry, NoSuchJSONStorageEntryException>
			_collectionPersistenceFinderByC_CN_I_T_VL;

	/**
	 * Returns an ordered range of all the json storage entries where companyId = &#63; and classNameId = &#63; and index = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JSONStorageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param index the index
	 * @param type the type
	 * @param valueLong the value long
	 * @param start the lower bound of the range of json storage entries
	 * @param end the upper bound of the range of json storage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching json storage entries
	 */
	@Override
	public List<JSONStorageEntry> findByC_CN_I_T_VL(
		long companyId, long classNameId, int index, int type, long valueLong,
		int start, int end,
		OrderByComparator<JSONStorageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN_I_T_VL.find(
			finderCache,
			new Object[] {companyId, classNameId, index, type, valueLong},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first json storage entry in the ordered set where companyId = &#63; and classNameId = &#63; and index = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param index the index
	 * @param type the type
	 * @param valueLong the value long
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching json storage entry
	 * @throws NoSuchJSONStorageEntryException if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry findByC_CN_I_T_VL_First(
			long companyId, long classNameId, int index, int type,
			long valueLong,
			OrderByComparator<JSONStorageEntry> orderByComparator)
		throws NoSuchJSONStorageEntryException {

		return _collectionPersistenceFinderByC_CN_I_T_VL.findFirst(
			finderCache,
			new Object[] {companyId, classNameId, index, type, valueLong},
			orderByComparator);
	}

	/**
	 * Returns the first json storage entry in the ordered set where companyId = &#63; and classNameId = &#63; and index = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param index the index
	 * @param type the type
	 * @param valueLong the value long
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching json storage entry, or <code>null</code> if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry fetchByC_CN_I_T_VL_First(
		long companyId, long classNameId, int index, int type, long valueLong,
		OrderByComparator<JSONStorageEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CN_I_T_VL.fetchFirst(
			finderCache,
			new Object[] {companyId, classNameId, index, type, valueLong},
			orderByComparator);
	}

	/**
	 * Removes all the json storage entries where companyId = &#63; and classNameId = &#63; and index = &#63; and type = &#63; and valueLong = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param index the index
	 * @param type the type
	 * @param valueLong the value long
	 */
	@Override
	public void removeByC_CN_I_T_VL(
		long companyId, long classNameId, int index, int type, long valueLong) {

		_collectionPersistenceFinderByC_CN_I_T_VL.remove(
			finderCache,
			new Object[] {companyId, classNameId, index, type, valueLong});
	}

	/**
	 * Returns the number of json storage entries where companyId = &#63; and classNameId = &#63; and index = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param index the index
	 * @param type the type
	 * @param valueLong the value long
	 * @return the number of matching json storage entries
	 */
	@Override
	public int countByC_CN_I_T_VL(
		long companyId, long classNameId, int index, int type, long valueLong) {

		return _collectionPersistenceFinderByC_CN_I_T_VL.count(
			finderCache,
			new Object[] {companyId, classNameId, index, type, valueLong});
	}

	private CollectionPersistenceFinder
		<JSONStorageEntry, NoSuchJSONStorageEntryException>
			_collectionPersistenceFinderByC_CN_K_T_VL;

	/**
	 * Returns an ordered range of all the json storage entries where companyId = &#63; and classNameId = &#63; and key = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>JSONStorageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param valueLong the value long
	 * @param start the lower bound of the range of json storage entries
	 * @param end the upper bound of the range of json storage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching json storage entries
	 */
	@Override
	public List<JSONStorageEntry> findByC_CN_K_T_VL(
		long companyId, long classNameId, String key, int type, long valueLong,
		int start, int end,
		OrderByComparator<JSONStorageEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN_K_T_VL.find(
			finderCache,
			new Object[] {companyId, classNameId, key, type, valueLong}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first json storage entry in the ordered set where companyId = &#63; and classNameId = &#63; and key = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param valueLong the value long
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching json storage entry
	 * @throws NoSuchJSONStorageEntryException if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry findByC_CN_K_T_VL_First(
			long companyId, long classNameId, String key, int type,
			long valueLong,
			OrderByComparator<JSONStorageEntry> orderByComparator)
		throws NoSuchJSONStorageEntryException {

		return _collectionPersistenceFinderByC_CN_K_T_VL.findFirst(
			finderCache,
			new Object[] {companyId, classNameId, key, type, valueLong},
			orderByComparator);
	}

	/**
	 * Returns the first json storage entry in the ordered set where companyId = &#63; and classNameId = &#63; and key = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param valueLong the value long
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching json storage entry, or <code>null</code> if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry fetchByC_CN_K_T_VL_First(
		long companyId, long classNameId, String key, int type, long valueLong,
		OrderByComparator<JSONStorageEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CN_K_T_VL.fetchFirst(
			finderCache,
			new Object[] {companyId, classNameId, key, type, valueLong},
			orderByComparator);
	}

	/**
	 * Removes all the json storage entries where companyId = &#63; and classNameId = &#63; and key = &#63; and type = &#63; and valueLong = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param valueLong the value long
	 */
	@Override
	public void removeByC_CN_K_T_VL(
		long companyId, long classNameId, String key, int type,
		long valueLong) {

		_collectionPersistenceFinderByC_CN_K_T_VL.remove(
			finderCache,
			new Object[] {companyId, classNameId, key, type, valueLong});
	}

	/**
	 * Returns the number of json storage entries where companyId = &#63; and classNameId = &#63; and key = &#63; and type = &#63; and valueLong = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param key the key
	 * @param type the type
	 * @param valueLong the value long
	 * @return the number of matching json storage entries
	 */
	@Override
	public int countByC_CN_K_T_VL(
		long companyId, long classNameId, String key, int type,
		long valueLong) {

		return _collectionPersistenceFinderByC_CN_K_T_VL.count(
			finderCache,
			new Object[] {companyId, classNameId, key, type, valueLong});
	}

	private UniquePersistenceFinder
		<JSONStorageEntry, NoSuchJSONStorageEntryException>
			_uniquePersistenceFinderByCN_CPK_P_I_K;

	/**
	 * Returns the json storage entry where classNameId = &#63; and classPK = &#63; and parentJSONStorageEntryId = &#63; and index = &#63; and key = &#63; or throws a <code>NoSuchJSONStorageEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param parentJSONStorageEntryId the parent json storage entry ID
	 * @param index the index
	 * @param key the key
	 * @return the matching json storage entry
	 * @throws NoSuchJSONStorageEntryException if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry findByCN_CPK_P_I_K(
			long classNameId, long classPK, long parentJSONStorageEntryId,
			int index, String key)
		throws NoSuchJSONStorageEntryException {

		return _uniquePersistenceFinderByCN_CPK_P_I_K.find(
			finderCache,
			new Object[] {
				classNameId, classPK, parentJSONStorageEntryId, index, key
			});
	}

	/**
	 * Returns the json storage entry where classNameId = &#63; and classPK = &#63; and parentJSONStorageEntryId = &#63; and index = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param parentJSONStorageEntryId the parent json storage entry ID
	 * @param index the index
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching json storage entry, or <code>null</code> if a matching json storage entry could not be found
	 */
	@Override
	public JSONStorageEntry fetchByCN_CPK_P_I_K(
		long classNameId, long classPK, long parentJSONStorageEntryId,
		int index, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByCN_CPK_P_I_K.fetch(
			finderCache,
			new Object[] {
				classNameId, classPK, parentJSONStorageEntryId, index, key
			},
			useFinderCache);
	}

	/**
	 * Removes the json storage entry where classNameId = &#63; and classPK = &#63; and parentJSONStorageEntryId = &#63; and index = &#63; and key = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param parentJSONStorageEntryId the parent json storage entry ID
	 * @param index the index
	 * @param key the key
	 * @return the json storage entry that was removed
	 */
	@Override
	public JSONStorageEntry removeByCN_CPK_P_I_K(
			long classNameId, long classPK, long parentJSONStorageEntryId,
			int index, String key)
		throws NoSuchJSONStorageEntryException {

		JSONStorageEntry jsonStorageEntry = findByCN_CPK_P_I_K(
			classNameId, classPK, parentJSONStorageEntryId, index, key);

		return remove(jsonStorageEntry);
	}

	/**
	 * Returns the number of json storage entries where classNameId = &#63; and classPK = &#63; and parentJSONStorageEntryId = &#63; and index = &#63; and key = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param parentJSONStorageEntryId the parent json storage entry ID
	 * @param index the index
	 * @param key the key
	 * @return the number of matching json storage entries
	 */
	@Override
	public int countByCN_CPK_P_I_K(
		long classNameId, long classPK, long parentJSONStorageEntryId,
		int index, String key) {

		return _uniquePersistenceFinderByCN_CPK_P_I_K.count(
			finderCache,
			new Object[] {
				classNameId, classPK, parentJSONStorageEntryId, index, key
			});
	}

	public JSONStorageEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("index", "index_");
		dbColumnNames.put("key", "key_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(JSONStorageEntry.class);

		setModelImplClass(JSONStorageEntryImpl.class);
		setModelPKClass(long.class);

		setTable(JSONStorageEntryTable.INSTANCE);
	}

	/**
	 * Creates a new json storage entry with the primary key. Does not add the json storage entry to the database.
	 *
	 * @param jsonStorageEntryId the primary key for the new json storage entry
	 * @return the new json storage entry
	 */
	@Override
	public JSONStorageEntry create(long jsonStorageEntryId) {
		JSONStorageEntry jsonStorageEntry = new JSONStorageEntryImpl();

		jsonStorageEntry.setNew(true);
		jsonStorageEntry.setPrimaryKey(jsonStorageEntryId);

		jsonStorageEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return jsonStorageEntry;
	}

	/**
	 * Removes the json storage entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param jsonStorageEntryId the primary key of the json storage entry
	 * @return the json storage entry that was removed
	 * @throws NoSuchJSONStorageEntryException if a json storage entry with the primary key could not be found
	 */
	@Override
	public JSONStorageEntry remove(long jsonStorageEntryId)
		throws NoSuchJSONStorageEntryException {

		return remove((Serializable)jsonStorageEntryId);
	}

	@Override
	protected JSONStorageEntry removeImpl(JSONStorageEntry jsonStorageEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(jsonStorageEntry)) {
				jsonStorageEntry = (JSONStorageEntry)session.get(
					JSONStorageEntryImpl.class,
					jsonStorageEntry.getPrimaryKeyObj());
			}

			if ((jsonStorageEntry != null) &&
				ctPersistenceHelper.isRemove(jsonStorageEntry)) {

				session.delete(jsonStorageEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (jsonStorageEntry != null) {
			clearCache(jsonStorageEntry);
		}

		return jsonStorageEntry;
	}

	@Override
	public JSONStorageEntry updateImpl(JSONStorageEntry jsonStorageEntry) {
		boolean isNew = jsonStorageEntry.isNew();

		if (!(jsonStorageEntry instanceof JSONStorageEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(jsonStorageEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					jsonStorageEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in jsonStorageEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom JSONStorageEntry implementation " +
					jsonStorageEntry.getClass());
		}

		JSONStorageEntryModelImpl jsonStorageEntryModelImpl =
			(JSONStorageEntryModelImpl)jsonStorageEntry;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(jsonStorageEntry)) {
				if (!isNew) {
					session.evict(
						JSONStorageEntryImpl.class,
						jsonStorageEntry.getPrimaryKeyObj());
				}

				session.save(jsonStorageEntry);
			}
			else {
				jsonStorageEntry = (JSONStorageEntry)session.merge(
					jsonStorageEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(jsonStorageEntry, false);

		if (isNew) {
			jsonStorageEntry.setNew(false);
		}

		jsonStorageEntry.resetOriginalValues();

		return jsonStorageEntry;
	}

	/**
	 * Returns the json storage entry with the primary key or throws a <code>NoSuchJSONStorageEntryException</code> if it could not be found.
	 *
	 * @param jsonStorageEntryId the primary key of the json storage entry
	 * @return the json storage entry
	 * @throws NoSuchJSONStorageEntryException if a json storage entry with the primary key could not be found
	 */
	@Override
	public JSONStorageEntry findByPrimaryKey(long jsonStorageEntryId)
		throws NoSuchJSONStorageEntryException {

		return findByPrimaryKey((Serializable)jsonStorageEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the json storage entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param jsonStorageEntryId the primary key of the json storage entry
	 * @return the json storage entry, or <code>null</code> if a json storage entry with the primary key could not be found
	 */
	@Override
	public JSONStorageEntry fetchByPrimaryKey(long jsonStorageEntryId) {
		return fetchByPrimaryKey((Serializable)jsonStorageEntryId);
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
		return "jsonStorageEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_JSONSTORAGEENTRY;
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
		return JSONStorageEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "JSONStorageEntry";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("parentJSONStorageEntryId");
		ctMergeColumnNames.add("index_");
		ctMergeColumnNames.add("key_");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("valueLong");
		ctMergeColumnNames.add("valueString");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("jsonStorageEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"classNameId", "classPK", "parentJSONStorageEntryId", "index_",
				"key_"
			});
	}

	/**
	 * Initializes the json storage entry persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_JSONSTORAGEENTRY_WHERE,
				_SQL_COUNT_JSONSTORAGEENTRY_WHERE,
				JSONStorageEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"jsonStorageEntry.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JSONStorageEntry::getClassNameId),
				new FinderColumn<>(
					"jsonStorageEntry.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, JSONStorageEntry::getClassPK));

		_collectionPersistenceFinderByC_CN_I_T_VL =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN_I_T_VL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "index_", "type_",
						"valueLong"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_CN_I_T_VL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "index_", "type_",
						"valueLong"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_CN_I_T_VL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "index_", "type_",
						"valueLong"
					},
					false),
				_SQL_SELECT_JSONSTORAGEENTRY_WHERE,
				_SQL_COUNT_JSONSTORAGEENTRY_WHERE,
				JSONStorageEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"jsonStorageEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, JSONStorageEntry::getCompanyId),
				new FinderColumn<>(
					"jsonStorageEntry.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JSONStorageEntry::getClassNameId),
				new FinderColumn<>(
					"jsonStorageEntry.", "index", "index_",
					FinderColumn.Type.INTEGER, "=", true, true,
					JSONStorageEntry::getIndex),
				new FinderColumn<>(
					"jsonStorageEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					JSONStorageEntry::getType),
				new FinderColumn<>(
					"jsonStorageEntry.", "valueLong", FinderColumn.Type.LONG,
					"=", true, true, JSONStorageEntry::getValueLong));

		_collectionPersistenceFinderByC_CN_K_T_VL =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN_K_T_VL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "key_", "type_", "valueLong"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_CN_K_T_VL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "key_", "type_", "valueLong"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_CN_K_T_VL",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "key_", "type_", "valueLong"
					},
					0, 4, false, null),
				_SQL_SELECT_JSONSTORAGEENTRY_WHERE,
				_SQL_COUNT_JSONSTORAGEENTRY_WHERE,
				JSONStorageEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"jsonStorageEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, JSONStorageEntry::getCompanyId),
				new FinderColumn<>(
					"jsonStorageEntry.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, JSONStorageEntry::getClassNameId),
				new FinderColumn<>(
					"jsonStorageEntry.", "key", "key_",
					FinderColumn.Type.STRING, "=", true, true,
					JSONStorageEntry::getKey),
				new FinderColumn<>(
					"jsonStorageEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					JSONStorageEntry::getType),
				new FinderColumn<>(
					"jsonStorageEntry.", "valueLong", FinderColumn.Type.LONG,
					"=", true, true, JSONStorageEntry::getValueLong));

		_uniquePersistenceFinderByCN_CPK_P_I_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCN_CPK_P_I_K",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					String.class.getName()
				},
				new String[] {
					"classNameId", "classPK", "parentJSONStorageEntryId",
					"index_", "key_"
				},
				0, 16, false, JSONStorageEntry::getClassNameId,
				JSONStorageEntry::getClassPK,
				JSONStorageEntry::getParentJSONStorageEntryId,
				JSONStorageEntry::getIndex,
				convertNullFunction(JSONStorageEntry::getKey)),
			_SQL_SELECT_JSONSTORAGEENTRY_WHERE, "",
			new FinderColumn<>(
				"jsonStorageEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, JSONStorageEntry::getClassNameId),
			new FinderColumn<>(
				"jsonStorageEntry.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, JSONStorageEntry::getClassPK),
			new FinderColumn<>(
				"jsonStorageEntry.", "parentJSONStorageEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				JSONStorageEntry::getParentJSONStorageEntryId),
			new FinderColumn<>(
				"jsonStorageEntry.", "index", "index_",
				FinderColumn.Type.INTEGER, "=", true, true,
				JSONStorageEntry::getIndex),
			new FinderColumn<>(
				"jsonStorageEntry.", "key", "key_", FinderColumn.Type.STRING,
				"=", true, true, JSONStorageEntry::getKey));

		JSONStorageEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		JSONStorageEntryUtil.setPersistence(null);

		entityCache.removeCache(JSONStorageEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = JSONStorePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = JSONStorePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = JSONStorePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		JSONStorageEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_JSONSTORAGEENTRY =
		"SELECT jsonStorageEntry FROM JSONStorageEntry jsonStorageEntry";

	private static final String _SQL_SELECT_JSONSTORAGEENTRY_WHERE =
		"SELECT jsonStorageEntry FROM JSONStorageEntry jsonStorageEntry WHERE ";

	private static final String _SQL_COUNT_JSONSTORAGEENTRY_WHERE =
		"SELECT COUNT(jsonStorageEntry) FROM JSONStorageEntry jsonStorageEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No JSONStorageEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		JSONStorageEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"index", "key", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:665022132