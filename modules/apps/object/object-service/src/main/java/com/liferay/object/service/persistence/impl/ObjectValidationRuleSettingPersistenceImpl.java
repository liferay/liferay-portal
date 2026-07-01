/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectValidationRuleSettingException;
import com.liferay.object.model.ObjectValidationRuleSetting;
import com.liferay.object.model.ObjectValidationRuleSettingTable;
import com.liferay.object.model.impl.ObjectValidationRuleSettingImpl;
import com.liferay.object.model.impl.ObjectValidationRuleSettingModelImpl;
import com.liferay.object.service.persistence.ObjectValidationRuleSettingPersistence;
import com.liferay.object.service.persistence.ObjectValidationRuleSettingUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
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
 * The persistence implementation for the object validation rule setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectValidationRuleSettingPersistence.class)
public class ObjectValidationRuleSettingPersistenceImpl
	extends BasePersistenceImpl
		<ObjectValidationRuleSetting,
		 NoSuchObjectValidationRuleSettingException>
	implements ObjectValidationRuleSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectValidationRuleSettingUtil</code> to access the object validation rule setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectValidationRuleSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectValidationRuleSetting,
		 NoSuchObjectValidationRuleSettingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object validation rule settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object validation rule settings
	 * @param end the upper bound of the range of object validation rule settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rule settings
	 */
	@Override
	public List<ObjectValidationRuleSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting
	 * @throws NoSuchObjectValidationRuleSettingException if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting findByUuid_First(
			String uuid,
			OrderByComparator<ObjectValidationRuleSetting> orderByComparator)
		throws NoSuchObjectValidationRuleSettingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting, or <code>null</code> if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object validation rule settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object validation rule settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object validation rule settings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRuleSetting,
		 NoSuchObjectValidationRuleSettingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object validation rule settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object validation rule settings
	 * @param end the upper bound of the range of object validation rule settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rule settings
	 */
	@Override
	public List<ObjectValidationRuleSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting
	 * @throws NoSuchObjectValidationRuleSettingException if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectValidationRuleSetting> orderByComparator)
		throws NoSuchObjectValidationRuleSettingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting, or <code>null</code> if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object validation rule settings where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object validation rule settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object validation rule settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRuleSetting,
		 NoSuchObjectValidationRuleSettingException>
			_collectionPersistenceFinderByObjectValidationRuleId;

	/**
	 * Returns an ordered range of all the object validation rule settings where objectValidationRuleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param start the lower bound of the range of object validation rule settings
	 * @param end the upper bound of the range of object validation rule settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rule settings
	 */
	@Override
	public List<ObjectValidationRuleSetting> findByObjectValidationRuleId(
		long objectValidationRuleId, int start, int end,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectValidationRuleId.find(
			finderCache, new Object[] {objectValidationRuleId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where objectValidationRuleId = &#63;.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting
	 * @throws NoSuchObjectValidationRuleSettingException if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting findByObjectValidationRuleId_First(
			long objectValidationRuleId,
			OrderByComparator<ObjectValidationRuleSetting> orderByComparator)
		throws NoSuchObjectValidationRuleSettingException {

		return _collectionPersistenceFinderByObjectValidationRuleId.findFirst(
			finderCache, new Object[] {objectValidationRuleId},
			orderByComparator);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where objectValidationRuleId = &#63;.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting, or <code>null</code> if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting fetchByObjectValidationRuleId_First(
		long objectValidationRuleId,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator) {

		return _collectionPersistenceFinderByObjectValidationRuleId.fetchFirst(
			finderCache, new Object[] {objectValidationRuleId},
			orderByComparator);
	}

	/**
	 * Removes all the object validation rule settings where objectValidationRuleId = &#63; from the database.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 */
	@Override
	public void removeByObjectValidationRuleId(long objectValidationRuleId) {
		_collectionPersistenceFinderByObjectValidationRuleId.remove(
			finderCache, new Object[] {objectValidationRuleId});
	}

	/**
	 * Returns the number of object validation rule settings where objectValidationRuleId = &#63;.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @return the number of matching object validation rule settings
	 */
	@Override
	public int countByObjectValidationRuleId(long objectValidationRuleId) {
		return _collectionPersistenceFinderByObjectValidationRuleId.count(
			finderCache, new Object[] {objectValidationRuleId});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRuleSetting,
		 NoSuchObjectValidationRuleSettingException>
			_collectionPersistenceFinderByOVRI_N;

	/**
	 * Returns an ordered range of all the object validation rule settings where objectValidationRuleId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @param start the lower bound of the range of object validation rule settings
	 * @param end the upper bound of the range of object validation rule settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rule settings
	 */
	@Override
	public List<ObjectValidationRuleSetting> findByOVRI_N(
		long objectValidationRuleId, String name, int start, int end,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOVRI_N.find(
			finderCache, new Object[] {objectValidationRuleId, name}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where objectValidationRuleId = &#63; and name = &#63;.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting
	 * @throws NoSuchObjectValidationRuleSettingException if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting findByOVRI_N_First(
			long objectValidationRuleId, String name,
			OrderByComparator<ObjectValidationRuleSetting> orderByComparator)
		throws NoSuchObjectValidationRuleSettingException {

		return _collectionPersistenceFinderByOVRI_N.findFirst(
			finderCache, new Object[] {objectValidationRuleId, name},
			orderByComparator);
	}

	/**
	 * Returns the first object validation rule setting in the ordered set where objectValidationRuleId = &#63; and name = &#63;.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule setting, or <code>null</code> if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting fetchByOVRI_N_First(
		long objectValidationRuleId, String name,
		OrderByComparator<ObjectValidationRuleSetting> orderByComparator) {

		return _collectionPersistenceFinderByOVRI_N.fetchFirst(
			finderCache, new Object[] {objectValidationRuleId, name},
			orderByComparator);
	}

	/**
	 * Removes all the object validation rule settings where objectValidationRuleId = &#63; and name = &#63; from the database.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 */
	@Override
	public void removeByOVRI_N(long objectValidationRuleId, String name) {
		_collectionPersistenceFinderByOVRI_N.remove(
			finderCache, new Object[] {objectValidationRuleId, name});
	}

	/**
	 * Returns the number of object validation rule settings where objectValidationRuleId = &#63; and name = &#63;.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @return the number of matching object validation rule settings
	 */
	@Override
	public int countByOVRI_N(long objectValidationRuleId, String name) {
		return _collectionPersistenceFinderByOVRI_N.count(
			finderCache, new Object[] {objectValidationRuleId, name});
	}

	private UniquePersistenceFinder
		<ObjectValidationRuleSetting,
		 NoSuchObjectValidationRuleSettingException>
			_uniquePersistenceFinderByN_V;

	/**
	 * Returns the object validation rule setting where name = &#63; and value = &#63; or throws a <code>NoSuchObjectValidationRuleSettingException</code> if it could not be found.
	 *
	 * @param name the name
	 * @param value the value
	 * @return the matching object validation rule setting
	 * @throws NoSuchObjectValidationRuleSettingException if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting findByN_V(String name, String value)
		throws NoSuchObjectValidationRuleSettingException {

		return _uniquePersistenceFinderByN_V.find(
			finderCache, new Object[] {name, value});
	}

	/**
	 * Returns the object validation rule setting where name = &#63; and value = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param value the value
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object validation rule setting, or <code>null</code> if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting fetchByN_V(
		String name, String value, boolean useFinderCache) {

		return _uniquePersistenceFinderByN_V.fetch(
			finderCache, new Object[] {name, value}, useFinderCache);
	}

	/**
	 * Removes the object validation rule setting where name = &#63; and value = &#63; from the database.
	 *
	 * @param name the name
	 * @param value the value
	 * @return the object validation rule setting that was removed
	 */
	@Override
	public ObjectValidationRuleSetting removeByN_V(String name, String value)
		throws NoSuchObjectValidationRuleSettingException {

		ObjectValidationRuleSetting objectValidationRuleSetting = findByN_V(
			name, value);

		return remove(objectValidationRuleSetting);
	}

	/**
	 * Returns the number of object validation rule settings where name = &#63; and value = &#63;.
	 *
	 * @param name the name
	 * @param value the value
	 * @return the number of matching object validation rule settings
	 */
	@Override
	public int countByN_V(String name, String value) {
		return _uniquePersistenceFinderByN_V.count(
			finderCache, new Object[] {name, value});
	}

	private UniquePersistenceFinder
		<ObjectValidationRuleSetting,
		 NoSuchObjectValidationRuleSettingException>
			_uniquePersistenceFinderByOVRI_N_V;

	/**
	 * Returns the object validation rule setting where objectValidationRuleId = &#63; and name = &#63; and value = &#63; or throws a <code>NoSuchObjectValidationRuleSettingException</code> if it could not be found.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @param value the value
	 * @return the matching object validation rule setting
	 * @throws NoSuchObjectValidationRuleSettingException if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting findByOVRI_N_V(
			long objectValidationRuleId, String name, String value)
		throws NoSuchObjectValidationRuleSettingException {

		return _uniquePersistenceFinderByOVRI_N_V.find(
			finderCache, new Object[] {objectValidationRuleId, name, value});
	}

	/**
	 * Returns the object validation rule setting where objectValidationRuleId = &#63; and name = &#63; and value = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @param value the value
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object validation rule setting, or <code>null</code> if a matching object validation rule setting could not be found
	 */
	@Override
	public ObjectValidationRuleSetting fetchByOVRI_N_V(
		long objectValidationRuleId, String name, String value,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByOVRI_N_V.fetch(
			finderCache, new Object[] {objectValidationRuleId, name, value},
			useFinderCache);
	}

	/**
	 * Removes the object validation rule setting where objectValidationRuleId = &#63; and name = &#63; and value = &#63; from the database.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @param value the value
	 * @return the object validation rule setting that was removed
	 */
	@Override
	public ObjectValidationRuleSetting removeByOVRI_N_V(
			long objectValidationRuleId, String name, String value)
		throws NoSuchObjectValidationRuleSettingException {

		ObjectValidationRuleSetting objectValidationRuleSetting =
			findByOVRI_N_V(objectValidationRuleId, name, value);

		return remove(objectValidationRuleSetting);
	}

	/**
	 * Returns the number of object validation rule settings where objectValidationRuleId = &#63; and name = &#63; and value = &#63;.
	 *
	 * @param objectValidationRuleId the object validation rule ID
	 * @param name the name
	 * @param value the value
	 * @return the number of matching object validation rule settings
	 */
	@Override
	public int countByOVRI_N_V(
		long objectValidationRuleId, String name, String value) {

		return _uniquePersistenceFinderByOVRI_N_V.count(
			finderCache, new Object[] {objectValidationRuleId, name, value});
	}

	public ObjectValidationRuleSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectValidationRuleSetting.class);

		setModelImplClass(ObjectValidationRuleSettingImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectValidationRuleSettingTable.INSTANCE);
	}

	/**
	 * Creates a new object validation rule setting with the primary key. Does not add the object validation rule setting to the database.
	 *
	 * @param objectValidationRuleSettingId the primary key for the new object validation rule setting
	 * @return the new object validation rule setting
	 */
	@Override
	public ObjectValidationRuleSetting create(
		long objectValidationRuleSettingId) {

		ObjectValidationRuleSetting objectValidationRuleSetting =
			new ObjectValidationRuleSettingImpl();

		objectValidationRuleSetting.setNew(true);
		objectValidationRuleSetting.setPrimaryKey(
			objectValidationRuleSettingId);

		String uuid = PortalUUIDUtil.generate();

		objectValidationRuleSetting.setUuid(uuid);

		objectValidationRuleSetting.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return objectValidationRuleSetting;
	}

	/**
	 * Removes the object validation rule setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectValidationRuleSettingId the primary key of the object validation rule setting
	 * @return the object validation rule setting that was removed
	 * @throws NoSuchObjectValidationRuleSettingException if a object validation rule setting with the primary key could not be found
	 */
	@Override
	public ObjectValidationRuleSetting remove(
			long objectValidationRuleSettingId)
		throws NoSuchObjectValidationRuleSettingException {

		return remove((Serializable)objectValidationRuleSettingId);
	}

	@Override
	protected ObjectValidationRuleSetting removeImpl(
		ObjectValidationRuleSetting objectValidationRuleSetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectValidationRuleSetting)) {
				objectValidationRuleSetting =
					(ObjectValidationRuleSetting)session.get(
						ObjectValidationRuleSettingImpl.class,
						objectValidationRuleSetting.getPrimaryKeyObj());
			}

			if (objectValidationRuleSetting != null) {
				session.delete(objectValidationRuleSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectValidationRuleSetting != null) {
			clearCache(objectValidationRuleSetting);
		}

		return objectValidationRuleSetting;
	}

	@Override
	public ObjectValidationRuleSetting updateImpl(
		ObjectValidationRuleSetting objectValidationRuleSetting) {

		boolean isNew = objectValidationRuleSetting.isNew();

		if (!(objectValidationRuleSetting instanceof
				ObjectValidationRuleSettingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					objectValidationRuleSetting.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					objectValidationRuleSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectValidationRuleSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectValidationRuleSetting implementation " +
					objectValidationRuleSetting.getClass());
		}

		ObjectValidationRuleSettingModelImpl
			objectValidationRuleSettingModelImpl =
				(ObjectValidationRuleSettingModelImpl)
					objectValidationRuleSetting;

		if (Validator.isNull(objectValidationRuleSetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectValidationRuleSetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectValidationRuleSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectValidationRuleSetting.setCreateDate(date);
			}
			else {
				objectValidationRuleSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectValidationRuleSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectValidationRuleSetting.setModifiedDate(date);
			}
			else {
				objectValidationRuleSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectValidationRuleSetting);
			}
			else {
				objectValidationRuleSetting =
					(ObjectValidationRuleSetting)session.merge(
						objectValidationRuleSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectValidationRuleSetting, false);

		if (isNew) {
			objectValidationRuleSetting.setNew(false);
		}

		objectValidationRuleSetting.resetOriginalValues();

		return objectValidationRuleSetting;
	}

	/**
	 * Returns the object validation rule setting with the primary key or throws a <code>NoSuchObjectValidationRuleSettingException</code> if it could not be found.
	 *
	 * @param objectValidationRuleSettingId the primary key of the object validation rule setting
	 * @return the object validation rule setting
	 * @throws NoSuchObjectValidationRuleSettingException if a object validation rule setting with the primary key could not be found
	 */
	@Override
	public ObjectValidationRuleSetting findByPrimaryKey(
			long objectValidationRuleSettingId)
		throws NoSuchObjectValidationRuleSettingException {

		return findByPrimaryKey((Serializable)objectValidationRuleSettingId);
	}

	/**
	 * Returns the object validation rule setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectValidationRuleSettingId the primary key of the object validation rule setting
	 * @return the object validation rule setting, or <code>null</code> if a object validation rule setting with the primary key could not be found
	 */
	@Override
	public ObjectValidationRuleSetting fetchByPrimaryKey(
		long objectValidationRuleSettingId) {

		return fetchByPrimaryKey((Serializable)objectValidationRuleSettingId);
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
		return "objectValidationRuleSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTVALIDATIONRULESETTING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectValidationRuleSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object validation rule setting persistence.
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
			_SQL_SELECT_OBJECTVALIDATIONRULESETTING_WHERE,
			_SQL_COUNT_OBJECTVALIDATIONRULESETTING_WHERE,
			ObjectValidationRuleSettingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"objectValidationRuleSetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectValidationRuleSetting::getUuid));

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
				_SQL_SELECT_OBJECTVALIDATIONRULESETTING_WHERE,
				_SQL_COUNT_OBJECTVALIDATIONRULESETTING_WHERE,
				ObjectValidationRuleSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectValidationRuleSetting.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectValidationRuleSetting::getUuid),
				new FinderColumn<>(
					"objectValidationRuleSetting.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectValidationRuleSetting::getCompanyId));

		_collectionPersistenceFinderByObjectValidationRuleId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectValidationRuleId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectValidationRuleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectValidationRuleId",
					new String[] {Long.class.getName()},
					new String[] {"objectValidationRuleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectValidationRuleId",
					new String[] {Long.class.getName()},
					new String[] {"objectValidationRuleId"}, false),
				_SQL_SELECT_OBJECTVALIDATIONRULESETTING_WHERE,
				_SQL_COUNT_OBJECTVALIDATIONRULESETTING_WHERE,
				ObjectValidationRuleSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectValidationRuleSetting.", "objectValidationRuleId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectValidationRuleSetting::getObjectValidationRuleId));

		_collectionPersistenceFinderByOVRI_N =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByOVRI_N",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectValidationRuleId", "name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByOVRI_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectValidationRuleId", "name"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByOVRI_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"objectValidationRuleId", "name"}, 0, 2,
					false, null),
				_SQL_SELECT_OBJECTVALIDATIONRULESETTING_WHERE,
				_SQL_COUNT_OBJECTVALIDATIONRULESETTING_WHERE,
				ObjectValidationRuleSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectValidationRuleSetting.", "objectValidationRuleId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectValidationRuleSetting::getObjectValidationRuleId),
				new FinderColumn<>(
					"objectValidationRuleSetting.", "name",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectValidationRuleSetting::getName));

		_uniquePersistenceFinderByN_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByN_V",
				new String[] {String.class.getName(), String.class.getName()},
				new String[] {"name", "value"}, 0, 3, false,
				convertNullFunction(ObjectValidationRuleSetting::getName),
				convertNullFunction(ObjectValidationRuleSetting::getValue)),
			_SQL_SELECT_OBJECTVALIDATIONRULESETTING_WHERE, "",
			new FinderColumn<>(
				"objectValidationRuleSetting.", "name",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectValidationRuleSetting::getName),
			new FinderColumn<>(
				"objectValidationRuleSetting.", "value",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectValidationRuleSetting::getValue));

		_uniquePersistenceFinderByOVRI_N_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByOVRI_N_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"objectValidationRuleId", "name", "value"}, 0, 6,
				false, ObjectValidationRuleSetting::getObjectValidationRuleId,
				convertNullFunction(ObjectValidationRuleSetting::getName),
				convertNullFunction(ObjectValidationRuleSetting::getValue)),
			_SQL_SELECT_OBJECTVALIDATIONRULESETTING_WHERE, "",
			new FinderColumn<>(
				"objectValidationRuleSetting.", "objectValidationRuleId",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectValidationRuleSetting::getObjectValidationRuleId),
			new FinderColumn<>(
				"objectValidationRuleSetting.", "name",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectValidationRuleSetting::getName),
			new FinderColumn<>(
				"objectValidationRuleSetting.", "value",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectValidationRuleSetting::getValue));

		ObjectValidationRuleSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectValidationRuleSettingUtil.setPersistence(null);

		entityCache.removeCache(
			ObjectValidationRuleSettingImpl.class.getName());
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ObjectPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ObjectValidationRuleSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTVALIDATIONRULESETTING =
		"SELECT objectValidationRuleSetting FROM ObjectValidationRuleSetting objectValidationRuleSetting";

	private static final String _SQL_SELECT_OBJECTVALIDATIONRULESETTING_WHERE =
		"SELECT objectValidationRuleSetting FROM ObjectValidationRuleSetting objectValidationRuleSetting WHERE ";

	private static final String _SQL_COUNT_OBJECTVALIDATIONRULESETTING_WHERE =
		"SELECT COUNT(objectValidationRuleSetting) FROM ObjectValidationRuleSetting objectValidationRuleSetting WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:824426093