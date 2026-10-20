/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectValidationRule;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object validation rule service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectValidationRulePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectValidationRulePersistence
 * @generated
 */
public class ObjectValidationRuleUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<ObjectValidationRule> objectValidationRules) {

		getPersistence().cacheResult(objectValidationRules);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(ObjectValidationRule objectValidationRule) {
		getPersistence().cacheResult(objectValidationRule);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(ObjectValidationRule objectValidationRule) {
		getPersistence().clearCache(objectValidationRule);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, ObjectValidationRule> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectValidationRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectValidationRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectValidationRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectValidationRule update(
		ObjectValidationRule objectValidationRule) {

		return getPersistence().update(objectValidationRule);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectValidationRule update(
		ObjectValidationRule objectValidationRule,
		ServiceContext serviceContext) {

		return getPersistence().update(objectValidationRule, serviceContext);
	}

	/**
	 * Returns an ordered range of all the object validation rules where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule findByUuid_First(
			String uuid,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object validation rules where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object validation rules
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the object validation rules where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object validation rules where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object validation rules
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().fetchByObjectDefinitionId_First(
			objectDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public static void removeByObjectDefinitionId(long objectDefinitionId) {
		getPersistence().removeByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns the number of object validation rules where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object validation rules
	 */
	public static int countByObjectDefinitionId(long objectDefinitionId) {
		return getPersistence().countByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_A(
		long objectDefinitionId, boolean active, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_A(
			objectDefinitionId, active, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule findByODI_A_First(
			long objectDefinitionId, boolean active,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByODI_A_First(
			objectDefinitionId, active, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByODI_A_First(
		long objectDefinitionId, boolean active,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().fetchByODI_A_First(
			objectDefinitionId, active, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where objectDefinitionId = &#63; and active = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 */
	public static void removeByODI_A(long objectDefinitionId, boolean active) {
		getPersistence().removeByODI_A(objectDefinitionId, active);
	}

	/**
	 * Returns the number of object validation rules where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @return the number of matching object validation rules
	 */
	public static int countByODI_A(long objectDefinitionId, boolean active) {
		return getPersistence().countByODI_A(objectDefinitionId, active);
	}

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_O(
		long objectDefinitionId, String outputType, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByODI_O(
			objectDefinitionId, outputType, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule findByODI_O_First(
			long objectDefinitionId, String outputType,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByODI_O_First(
			objectDefinitionId, outputType, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByODI_O_First(
		long objectDefinitionId, String outputType,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().fetchByODI_O_First(
			objectDefinitionId, outputType, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where objectDefinitionId = &#63; and outputType = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 */
	public static void removeByODI_O(
		long objectDefinitionId, String outputType) {

		getPersistence().removeByODI_O(objectDefinitionId, outputType);
	}

	/**
	 * Returns the number of object validation rules where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @return the number of matching object validation rules
	 */
	public static int countByODI_O(long objectDefinitionId, String outputType) {
		return getPersistence().countByODI_O(objectDefinitionId, outputType);
	}

	/**
	 * Returns an ordered range of all the object validation rules where active = &#63; and engine = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param engine the engine
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByA_E(
		boolean active, String engine, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByA_E(
			active, engine, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where active = &#63; and engine = &#63;.
	 *
	 * @param active the active
	 * @param engine the engine
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule findByA_E_First(
			boolean active, String engine,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByA_E_First(
			active, engine, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where active = &#63; and engine = &#63;.
	 *
	 * @param active the active
	 * @param engine the engine
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByA_E_First(
		boolean active, String engine,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().fetchByA_E_First(
			active, engine, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where active = &#63; and engine = &#63; from the database.
	 *
	 * @param active the active
	 * @param engine the engine
	 */
	public static void removeByA_E(boolean active, String engine) {
		getPersistence().removeByA_E(active, engine);
	}

	/**
	 * Returns the number of object validation rules where active = &#63; and engine = &#63;.
	 *
	 * @param active the active
	 * @param engine the engine
	 * @return the number of matching object validation rules
	 */
	public static int countByA_E(boolean active, String engine) {
		return getPersistence().countByA_E(active, engine);
	}

	/**
	 * Returns the object validation rule where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectValidationRuleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the object validation rule where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache) {

		return getPersistence().fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId,
			useFinderCache);
	}

	/**
	 * Removes the object validation rule where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object validation rule that was removed
	 */
	public static ObjectValidationRule removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().removeByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns the number of object validation rules where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object validation rules
	 */
	public static int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return getPersistence().countByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Creates a new object validation rule with the primary key. Does not add the object validation rule to the database.
	 *
	 * @param objectValidationRuleId the primary key for the new object validation rule
	 * @return the new object validation rule
	 */
	public static ObjectValidationRule create(long objectValidationRuleId) {
		return getPersistence().create(objectValidationRuleId);
	}

	/**
	 * Removes the object validation rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule that was removed
	 * @throws NoSuchObjectValidationRuleException if a object validation rule with the primary key could not be found
	 */
	public static ObjectValidationRule remove(long objectValidationRuleId)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().remove(objectValidationRuleId);
	}

	public static ObjectValidationRule updateImpl(
		ObjectValidationRule objectValidationRule) {

		return getPersistence().updateImpl(objectValidationRule);
	}

	/**
	 * Returns the object validation rule with the primary key or throws a <code>NoSuchObjectValidationRuleException</code> if it could not be found.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule
	 * @throws NoSuchObjectValidationRuleException if a object validation rule with the primary key could not be found
	 */
	public static ObjectValidationRule findByPrimaryKey(
			long objectValidationRuleId)
		throws com.liferay.object.exception.
			NoSuchObjectValidationRuleException {

		return getPersistence().findByPrimaryKey(objectValidationRuleId);
	}

	/**
	 * Returns the object validation rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule, or <code>null</code> if a object validation rule with the primary key could not be found
	 */
	public static ObjectValidationRule fetchByPrimaryKey(
		long objectValidationRuleId) {

		return getPersistence().fetchByPrimaryKey(objectValidationRuleId);
	}

	/**
	 * Returns the object validation rule where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	public static ObjectValidationRule fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return getPersistence().fetchByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);
	}

	/**
	 * Returns all the object validation rules where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object validation rules where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @return the range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object validation rules where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the object validation rules where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object validation rules where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @return the range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object validation rules where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the object validation rules where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object validation rules
	 */
	public static List<ObjectValidationRule> findByObjectDefinitionId(
		long objectDefinitionId) {

		return getPersistence().findByObjectDefinitionId(objectDefinitionId);
	}

	/**
	 * Returns a range of all the object validation rules where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @return the range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the object validation rules where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @return the matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_A(
		long objectDefinitionId, boolean active) {

		return getPersistence().findByODI_A(objectDefinitionId, active);
	}

	/**
	 * Returns a range of all the object validation rules where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @return the range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_A(
		long objectDefinitionId, boolean active, int start, int end) {

		return getPersistence().findByODI_A(
			objectDefinitionId, active, start, end);
	}

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_A(
		long objectDefinitionId, boolean active, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().findByODI_A(
			objectDefinitionId, active, start, end, orderByComparator);
	}

	/**
	 * Returns all the object validation rules where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @return the matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_O(
		long objectDefinitionId, String outputType) {

		return getPersistence().findByODI_O(objectDefinitionId, outputType);
	}

	/**
	 * Returns a range of all the object validation rules where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @return the range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_O(
		long objectDefinitionId, String outputType, int start, int end) {

		return getPersistence().findByODI_O(
			objectDefinitionId, outputType, start, end);
	}

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByODI_O(
		long objectDefinitionId, String outputType, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().findByODI_O(
			objectDefinitionId, outputType, start, end, orderByComparator);
	}

	/**
	 * Returns all the object validation rules where active = &#63; and engine = &#63;.
	 *
	 * @param active the active
	 * @param engine the engine
	 * @return the matching object validation rules
	 */
	public static List<ObjectValidationRule> findByA_E(
		boolean active, String engine) {

		return getPersistence().findByA_E(active, engine);
	}

	/**
	 * Returns a range of all the object validation rules where active = &#63; and engine = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param engine the engine
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @return the range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByA_E(
		boolean active, String engine, int start, int end) {

		return getPersistence().findByA_E(active, engine, start, end);
	}

	/**
	 * Returns an ordered range of all the object validation rules where active = &#63; and engine = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param engine the engine
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object validation rules
	 */
	public static List<ObjectValidationRule> findByA_E(
		boolean active, String engine, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return getPersistence().findByA_E(
			active, engine, start, end, orderByComparator);
	}

	public static ObjectValidationRulePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		ObjectValidationRulePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile ObjectValidationRulePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-191975230