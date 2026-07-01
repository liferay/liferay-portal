/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectDefinitionSettingException;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectDefinitionSettingTable;
import com.liferay.object.model.impl.ObjectDefinitionSettingImpl;
import com.liferay.object.model.impl.ObjectDefinitionSettingModelImpl;
import com.liferay.object.service.persistence.ObjectDefinitionSettingPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionSettingUtil;
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
 * The persistence implementation for the object definition setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectDefinitionSettingPersistence.class)
public class ObjectDefinitionSettingPersistenceImpl
	extends BasePersistenceImpl
		<ObjectDefinitionSetting, NoSuchObjectDefinitionSettingException>
	implements ObjectDefinitionSettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectDefinitionSettingUtil</code> to access the object definition setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectDefinitionSettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectDefinitionSetting, NoSuchObjectDefinitionSettingException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByUuid_First(
			String uuid,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object definition settings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectDefinitionSetting, NoSuchObjectDefinitionSettingException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object definition settings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectDefinitionSetting, NoSuchObjectDefinitionSettingException>
			_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object definition settings where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder
		<ObjectDefinitionSetting, NoSuchObjectDefinitionSettingException>
			_collectionPersistenceFinderByC_N;

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByC_N_First(
			long companyId, String name,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		return _collectionPersistenceFinderByC_N.findFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByC_N_First(
		long companyId, String name,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return _collectionPersistenceFinderByC_N.fetchFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Removes all the object definition settings where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_N(long companyId, String name) {
		_collectionPersistenceFinderByC_N.remove(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the number of object definition settings where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _collectionPersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	private UniquePersistenceFinder
		<ObjectDefinitionSetting, NoSuchObjectDefinitionSettingException>
			_uniquePersistenceFinderByODI_N;

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByODI_N(
			long objectDefinitionId, String name)
		throws NoSuchObjectDefinitionSettingException {

		return _uniquePersistenceFinderByODI_N.find(
			finderCache, new Object[] {objectDefinitionId, name});
	}

	/**
	 * Returns the object definition setting where objectDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByODI_N(
		long objectDefinitionId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByODI_N.fetch(
			finderCache, new Object[] {objectDefinitionId, name},
			useFinderCache);
	}

	/**
	 * Removes the object definition setting where objectDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the object definition setting that was removed
	 */
	@Override
	public ObjectDefinitionSetting removeByODI_N(
			long objectDefinitionId, String name)
		throws NoSuchObjectDefinitionSettingException {

		ObjectDefinitionSetting objectDefinitionSetting = findByODI_N(
			objectDefinitionId, name);

		return remove(objectDefinitionSetting);
	}

	/**
	 * Returns the number of object definition settings where objectDefinitionId = &#63; and name = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param name the name
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByODI_N(long objectDefinitionId, String name) {
		return _uniquePersistenceFinderByODI_N.count(
			finderCache, new Object[] {objectDefinitionId, name});
	}

	private CollectionPersistenceFinder
		<ObjectDefinitionSetting, NoSuchObjectDefinitionSettingException>
			_collectionPersistenceFinderByC_N_V;

	/**
	 * Returns an ordered range of all the object definition settings where companyId = &#63; and name = &#63; and value = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectDefinitionSettingModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param value the value
	 * @param start the lower bound of the range of object definition settings
	 * @param end the upper bound of the range of object definition settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object definition settings
	 */
	@Override
	public List<ObjectDefinitionSetting> findByC_N_V(
		long companyId, String name, String value, int start, int end,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_N_V.find(
			finderCache, new Object[] {companyId, name, value}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63; and value = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param value the value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByC_N_V_First(
			long companyId, String name, String value,
			OrderByComparator<ObjectDefinitionSetting> orderByComparator)
		throws NoSuchObjectDefinitionSettingException {

		return _collectionPersistenceFinderByC_N_V.findFirst(
			finderCache, new Object[] {companyId, name, value},
			orderByComparator);
	}

	/**
	 * Returns the first object definition setting in the ordered set where companyId = &#63; and name = &#63; and value = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param value the value
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object definition setting, or <code>null</code> if a matching object definition setting could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByC_N_V_First(
		long companyId, String name, String value,
		OrderByComparator<ObjectDefinitionSetting> orderByComparator) {

		return _collectionPersistenceFinderByC_N_V.fetchFirst(
			finderCache, new Object[] {companyId, name, value},
			orderByComparator);
	}

	/**
	 * Removes all the object definition settings where companyId = &#63; and name = &#63; and value = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param value the value
	 */
	@Override
	public void removeByC_N_V(long companyId, String name, String value) {
		_collectionPersistenceFinderByC_N_V.remove(
			finderCache, new Object[] {companyId, name, value});
	}

	/**
	 * Returns the number of object definition settings where companyId = &#63; and name = &#63; and value = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param value the value
	 * @return the number of matching object definition settings
	 */
	@Override
	public int countByC_N_V(long companyId, String name, String value) {
		return _collectionPersistenceFinderByC_N_V.count(
			finderCache, new Object[] {companyId, name, value});
	}

	public ObjectDefinitionSettingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectDefinitionSetting.class);

		setModelImplClass(ObjectDefinitionSettingImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectDefinitionSettingTable.INSTANCE);
	}

	/**
	 * Creates a new object definition setting with the primary key. Does not add the object definition setting to the database.
	 *
	 * @param objectDefinitionSettingId the primary key for the new object definition setting
	 * @return the new object definition setting
	 */
	@Override
	public ObjectDefinitionSetting create(long objectDefinitionSettingId) {
		ObjectDefinitionSetting objectDefinitionSetting =
			new ObjectDefinitionSettingImpl();

		objectDefinitionSetting.setNew(true);
		objectDefinitionSetting.setPrimaryKey(objectDefinitionSettingId);

		String uuid = PortalUUIDUtil.generate();

		objectDefinitionSetting.setUuid(uuid);

		objectDefinitionSetting.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectDefinitionSetting;
	}

	/**
	 * Removes the object definition setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting that was removed
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting remove(long objectDefinitionSettingId)
		throws NoSuchObjectDefinitionSettingException {

		return remove((Serializable)objectDefinitionSettingId);
	}

	@Override
	protected ObjectDefinitionSetting removeImpl(
		ObjectDefinitionSetting objectDefinitionSetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectDefinitionSetting)) {
				objectDefinitionSetting = (ObjectDefinitionSetting)session.get(
					ObjectDefinitionSettingImpl.class,
					objectDefinitionSetting.getPrimaryKeyObj());
			}

			if (objectDefinitionSetting != null) {
				session.delete(objectDefinitionSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectDefinitionSetting != null) {
			clearCache(objectDefinitionSetting);
		}

		return objectDefinitionSetting;
	}

	@Override
	public ObjectDefinitionSetting updateImpl(
		ObjectDefinitionSetting objectDefinitionSetting) {

		boolean isNew = objectDefinitionSetting.isNew();

		if (!(objectDefinitionSetting instanceof
				ObjectDefinitionSettingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectDefinitionSetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectDefinitionSetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectDefinitionSetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectDefinitionSetting implementation " +
					objectDefinitionSetting.getClass());
		}

		ObjectDefinitionSettingModelImpl objectDefinitionSettingModelImpl =
			(ObjectDefinitionSettingModelImpl)objectDefinitionSetting;

		if (Validator.isNull(objectDefinitionSetting.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectDefinitionSetting.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectDefinitionSetting.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectDefinitionSetting.setCreateDate(date);
			}
			else {
				objectDefinitionSetting.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectDefinitionSettingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectDefinitionSetting.setModifiedDate(date);
			}
			else {
				objectDefinitionSetting.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectDefinitionSetting);
			}
			else {
				objectDefinitionSetting =
					(ObjectDefinitionSetting)session.merge(
						objectDefinitionSetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectDefinitionSetting, false);

		if (isNew) {
			objectDefinitionSetting.setNew(false);
		}

		objectDefinitionSetting.resetOriginalValues();

		return objectDefinitionSetting;
	}

	/**
	 * Returns the object definition setting with the primary key or throws a <code>NoSuchObjectDefinitionSettingException</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting
	 * @throws NoSuchObjectDefinitionSettingException if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting findByPrimaryKey(
			long objectDefinitionSettingId)
		throws NoSuchObjectDefinitionSettingException {

		return findByPrimaryKey((Serializable)objectDefinitionSettingId);
	}

	/**
	 * Returns the object definition setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectDefinitionSettingId the primary key of the object definition setting
	 * @return the object definition setting, or <code>null</code> if a object definition setting with the primary key could not be found
	 */
	@Override
	public ObjectDefinitionSetting fetchByPrimaryKey(
		long objectDefinitionSettingId) {

		return fetchByPrimaryKey((Serializable)objectDefinitionSettingId);
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
		return "objectDefinitionSettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTDEFINITIONSETTING;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectDefinitionSettingModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object definition setting persistence.
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
			_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE,
			_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE,
			ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"objectDefinitionSetting.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectDefinitionSetting::getUuid));

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
				_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE,
				_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE,
				ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectDefinitionSetting.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectDefinitionSetting::getUuid),
				new FinderColumn<>(
					"objectDefinitionSetting.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectDefinitionSetting::getCompanyId));

		_collectionPersistenceFinderByObjectDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByObjectDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"objectDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByObjectDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByObjectDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"objectDefinitionId"}, false),
				_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE,
				_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE,
				ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"objectDefinitionSetting.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectDefinitionSetting::getObjectDefinitionId));

		_collectionPersistenceFinderByC_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false, null),
			_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE,
			_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE,
			ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"objectDefinitionSetting.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, ObjectDefinitionSetting::getCompanyId),
			new FinderColumn<>(
				"objectDefinitionSetting.", "name", FinderColumn.Type.STRING,
				"=", true, true, ObjectDefinitionSetting::getName));

		_uniquePersistenceFinderByODI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByODI_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectDefinitionId", "name"}, 0, 2, false,
				ObjectDefinitionSetting::getObjectDefinitionId,
				convertNullFunction(ObjectDefinitionSetting::getName)),
			_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE, "",
			new FinderColumn<>(
				"objectDefinitionSetting.", "objectDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectDefinitionSetting::getObjectDefinitionId),
			new FinderColumn<>(
				"objectDefinitionSetting.", "name", FinderColumn.Type.STRING,
				"=", true, true, ObjectDefinitionSetting::getName));

		_collectionPersistenceFinderByC_N_V = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "name", "value"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "name", "value"}, 0, 6, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "name", "value"}, 0, 6, false, null),
			_SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE,
			_SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE,
			ObjectDefinitionSettingModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"objectDefinitionSetting.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, ObjectDefinitionSetting::getCompanyId),
			new FinderColumn<>(
				"objectDefinitionSetting.", "name", FinderColumn.Type.STRING,
				"=", true, true, ObjectDefinitionSetting::getName),
			new FinderColumn<>(
				"objectDefinitionSetting.", "value", FinderColumn.Type.STRING,
				"=", true, true, ObjectDefinitionSetting::getValue));

		ObjectDefinitionSettingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectDefinitionSettingUtil.setPersistence(null);

		entityCache.removeCache(ObjectDefinitionSettingImpl.class.getName());
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
		ObjectDefinitionSettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTDEFINITIONSETTING =
		"SELECT objectDefinitionSetting FROM ObjectDefinitionSetting objectDefinitionSetting";

	private static final String _SQL_SELECT_OBJECTDEFINITIONSETTING_WHERE =
		"SELECT objectDefinitionSetting FROM ObjectDefinitionSetting objectDefinitionSetting WHERE ";

	private static final String _SQL_COUNT_OBJECTDEFINITIONSETTING_WHERE =
		"SELECT COUNT(objectDefinitionSetting) FROM ObjectDefinitionSetting objectDefinitionSetting WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-438107502