/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.impl;

import com.liferay.object.exception.NoSuchObjectValidationRuleException;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.model.ObjectValidationRuleTable;
import com.liferay.object.model.impl.ObjectValidationRuleImpl;
import com.liferay.object.model.impl.ObjectValidationRuleModelImpl;
import com.liferay.object.service.persistence.ObjectValidationRulePersistence;
import com.liferay.object.service.persistence.ObjectValidationRuleUtil;
import com.liferay.object.service.persistence.impl.constants.ObjectPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the object validation rule service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = ObjectValidationRulePersistence.class)
public class ObjectValidationRulePersistenceImpl
	extends BasePersistenceImpl
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
	implements ObjectValidationRulePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ObjectValidationRuleUtil</code> to access the object validation rule persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ObjectValidationRuleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the object validation rules where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	@Override
	public List<ObjectValidationRule> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule findByUuid_First(
			String uuid,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws NoSuchObjectValidationRuleException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule fetchByUuid_First(
		String uuid,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of object validation rules where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the object validation rules where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleModelImpl</code>.
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
	@Override
	public List<ObjectValidationRule> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public ObjectValidationRule findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws NoSuchObjectValidationRuleException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of object validation rules where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_collectionPersistenceFinderByObjectDefinitionId;

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	@Override
	public List<ObjectValidationRule> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByObjectDefinitionId.find(
			finderCache, new Object[] {objectDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule findByObjectDefinitionId_First(
			long objectDefinitionId,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws NoSuchObjectValidationRuleException {

		return _collectionPersistenceFinderByObjectDefinitionId.findFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return _collectionPersistenceFinderByObjectDefinitionId.fetchFirst(
			finderCache, new Object[] {objectDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	@Override
	public void removeByObjectDefinitionId(long objectDefinitionId) {
		_collectionPersistenceFinderByObjectDefinitionId.remove(
			finderCache, new Object[] {objectDefinitionId});
	}

	/**
	 * Returns the number of object validation rules where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByObjectDefinitionId(long objectDefinitionId) {
		return _collectionPersistenceFinderByObjectDefinitionId.count(
			finderCache, new Object[] {objectDefinitionId});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_collectionPersistenceFinderByODI_A;

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleModelImpl</code>.
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
	@Override
	public List<ObjectValidationRule> findByODI_A(
		long objectDefinitionId, boolean active, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_A.find(
			finderCache, new Object[] {objectDefinitionId, active}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public ObjectValidationRule findByODI_A_First(
			long objectDefinitionId, boolean active,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws NoSuchObjectValidationRuleException {

		return _collectionPersistenceFinderByODI_A.findFirst(
			finderCache, new Object[] {objectDefinitionId, active},
			orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule fetchByODI_A_First(
		long objectDefinitionId, boolean active,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return _collectionPersistenceFinderByODI_A.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, active},
			orderByComparator);
	}

	/**
	 * Removes all the object validation rules where objectDefinitionId = &#63; and active = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 */
	@Override
	public void removeByODI_A(long objectDefinitionId, boolean active) {
		_collectionPersistenceFinderByODI_A.remove(
			finderCache, new Object[] {objectDefinitionId, active});
	}

	/**
	 * Returns the number of object validation rules where objectDefinitionId = &#63; and active = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param active the active
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByODI_A(long objectDefinitionId, boolean active) {
		return _collectionPersistenceFinderByODI_A.count(
			finderCache, new Object[] {objectDefinitionId, active});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_collectionPersistenceFinderByODI_E;

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63; and engine = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param engine the engine
	 * @param start the lower bound of the range of object validation rules
	 * @param end the upper bound of the range of object validation rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object validation rules
	 */
	@Override
	public List<ObjectValidationRule> findByODI_E(
		long objectDefinitionId, String engine, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_E.find(
			finderCache, new Object[] {objectDefinitionId, engine}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and engine = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param engine the engine
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule findByODI_E_First(
			long objectDefinitionId, String engine,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws NoSuchObjectValidationRuleException {

		return _collectionPersistenceFinderByODI_E.findFirst(
			finderCache, new Object[] {objectDefinitionId, engine},
			orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and engine = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param engine the engine
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule fetchByODI_E_First(
		long objectDefinitionId, String engine,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return _collectionPersistenceFinderByODI_E.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, engine},
			orderByComparator);
	}

	/**
	 * Removes all the object validation rules where objectDefinitionId = &#63; and engine = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param engine the engine
	 */
	@Override
	public void removeByODI_E(long objectDefinitionId, String engine) {
		_collectionPersistenceFinderByODI_E.remove(
			finderCache, new Object[] {objectDefinitionId, engine});
	}

	/**
	 * Returns the number of object validation rules where objectDefinitionId = &#63; and engine = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param engine the engine
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByODI_E(long objectDefinitionId, String engine) {
		return _collectionPersistenceFinderByODI_E.count(
			finderCache, new Object[] {objectDefinitionId, engine});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_collectionPersistenceFinderByODI_O;

	/**
	 * Returns an ordered range of all the object validation rules where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleModelImpl</code>.
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
	@Override
	public List<ObjectValidationRule> findByODI_O(
		long objectDefinitionId, String outputType, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByODI_O.find(
			finderCache, new Object[] {objectDefinitionId, outputType}, start,
			end, orderByComparator, useFinderCache);
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
	@Override
	public ObjectValidationRule findByODI_O_First(
			long objectDefinitionId, String outputType,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws NoSuchObjectValidationRuleException {

		return _collectionPersistenceFinderByODI_O.findFirst(
			finderCache, new Object[] {objectDefinitionId, outputType},
			orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule fetchByODI_O_First(
		long objectDefinitionId, String outputType,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return _collectionPersistenceFinderByODI_O.fetchFirst(
			finderCache, new Object[] {objectDefinitionId, outputType},
			orderByComparator);
	}

	/**
	 * Removes all the object validation rules where objectDefinitionId = &#63; and outputType = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 */
	@Override
	public void removeByODI_O(long objectDefinitionId, String outputType) {
		_collectionPersistenceFinderByODI_O.remove(
			finderCache, new Object[] {objectDefinitionId, outputType});
	}

	/**
	 * Returns the number of object validation rules where objectDefinitionId = &#63; and outputType = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param outputType the output type
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByODI_O(long objectDefinitionId, String outputType) {
		return _collectionPersistenceFinderByODI_O.count(
			finderCache, new Object[] {objectDefinitionId, outputType});
	}

	private CollectionPersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_collectionPersistenceFinderByA_E;

	/**
	 * Returns an ordered range of all the object validation rules where active = &#63; and engine = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectValidationRuleModelImpl</code>.
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
	@Override
	public List<ObjectValidationRule> findByA_E(
		boolean active, String engine, int start, int end,
		OrderByComparator<ObjectValidationRule> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByA_E.find(
			finderCache, new Object[] {active, engine}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public ObjectValidationRule findByA_E_First(
			boolean active, String engine,
			OrderByComparator<ObjectValidationRule> orderByComparator)
		throws NoSuchObjectValidationRuleException {

		return _collectionPersistenceFinderByA_E.findFirst(
			finderCache, new Object[] {active, engine}, orderByComparator);
	}

	/**
	 * Returns the first object validation rule in the ordered set where active = &#63; and engine = &#63;.
	 *
	 * @param active the active
	 * @param engine the engine
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object validation rule, or <code>null</code> if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule fetchByA_E_First(
		boolean active, String engine,
		OrderByComparator<ObjectValidationRule> orderByComparator) {

		return _collectionPersistenceFinderByA_E.fetchFirst(
			finderCache, new Object[] {active, engine}, orderByComparator);
	}

	/**
	 * Removes all the object validation rules where active = &#63; and engine = &#63; from the database.
	 *
	 * @param active the active
	 * @param engine the engine
	 */
	@Override
	public void removeByA_E(boolean active, String engine) {
		_collectionPersistenceFinderByA_E.remove(
			finderCache, new Object[] {active, engine});
	}

	/**
	 * Returns the number of object validation rules where active = &#63; and engine = &#63;.
	 *
	 * @param active the active
	 * @param engine the engine
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByA_E(boolean active, String engine) {
		return _collectionPersistenceFinderByA_E.count(
			finderCache, new Object[] {active, engine});
	}

	private UniquePersistenceFinder
		<ObjectValidationRule, NoSuchObjectValidationRuleException>
			_uniquePersistenceFinderByERC_C_ODI;

	/**
	 * Returns the object validation rule where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectValidationRuleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object validation rule
	 * @throws NoSuchObjectValidationRuleException if a matching object validation rule could not be found
	 */
	@Override
	public ObjectValidationRule findByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectValidationRuleException {

		return _uniquePersistenceFinderByERC_C_ODI.find(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId
			});
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
	@Override
	public ObjectValidationRule fetchByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C_ODI.fetch(
			finderCache,
			new Object[] {externalReferenceCode, companyId, objectDefinitionId},
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
	@Override
	public ObjectValidationRule removeByERC_C_ODI(
			String externalReferenceCode, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectValidationRuleException {

		ObjectValidationRule objectValidationRule = findByERC_C_ODI(
			externalReferenceCode, companyId, objectDefinitionId);

		return remove(objectValidationRule);
	}

	/**
	 * Returns the number of object validation rules where externalReferenceCode = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object validation rules
	 */
	@Override
	public int countByERC_C_ODI(
		String externalReferenceCode, long companyId, long objectDefinitionId) {

		return _uniquePersistenceFinderByERC_C_ODI.count(
			finderCache,
			new Object[] {
				externalReferenceCode, companyId, objectDefinitionId
			});
	}

	public ObjectValidationRulePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ObjectValidationRule.class);

		setModelImplClass(ObjectValidationRuleImpl.class);
		setModelPKClass(long.class);

		setTable(ObjectValidationRuleTable.INSTANCE);
	}

	/**
	 * Creates a new object validation rule with the primary key. Does not add the object validation rule to the database.
	 *
	 * @param objectValidationRuleId the primary key for the new object validation rule
	 * @return the new object validation rule
	 */
	@Override
	public ObjectValidationRule create(long objectValidationRuleId) {
		ObjectValidationRule objectValidationRule =
			new ObjectValidationRuleImpl();

		objectValidationRule.setNew(true);
		objectValidationRule.setPrimaryKey(objectValidationRuleId);

		String uuid = PortalUUIDUtil.generate();

		objectValidationRule.setUuid(uuid);

		objectValidationRule.setCompanyId(CompanyThreadLocal.getCompanyId());

		return objectValidationRule;
	}

	/**
	 * Removes the object validation rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule that was removed
	 * @throws NoSuchObjectValidationRuleException if a object validation rule with the primary key could not be found
	 */
	@Override
	public ObjectValidationRule remove(long objectValidationRuleId)
		throws NoSuchObjectValidationRuleException {

		return remove((Serializable)objectValidationRuleId);
	}

	@Override
	protected ObjectValidationRule removeImpl(
		ObjectValidationRule objectValidationRule) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(objectValidationRule)) {
				objectValidationRule = (ObjectValidationRule)session.get(
					ObjectValidationRuleImpl.class,
					objectValidationRule.getPrimaryKeyObj());
			}

			if (objectValidationRule != null) {
				session.delete(objectValidationRule);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (objectValidationRule != null) {
			clearCache(objectValidationRule);
		}

		return objectValidationRule;
	}

	@Override
	public ObjectValidationRule updateImpl(
		ObjectValidationRule objectValidationRule) {

		boolean isNew = objectValidationRule.isNew();

		if (!(objectValidationRule instanceof ObjectValidationRuleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(objectValidationRule.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					objectValidationRule);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in objectValidationRule proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ObjectValidationRule implementation " +
					objectValidationRule.getClass());
		}

		ObjectValidationRuleModelImpl objectValidationRuleModelImpl =
			(ObjectValidationRuleModelImpl)objectValidationRule;

		if (Validator.isNull(objectValidationRule.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			objectValidationRule.setUuid(uuid);
		}

		if (Validator.isNull(objectValidationRule.getExternalReferenceCode())) {
			objectValidationRule.setExternalReferenceCode(
				objectValidationRule.getUuid());
		}
		else {
			if (!Objects.equals(
					objectValidationRuleModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					objectValidationRule.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = objectValidationRule.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = objectValidationRule.getPrimaryKey();
					}

					try {
						objectValidationRule.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ObjectValidationRule.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								objectValidationRule.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (objectValidationRule.getCreateDate() == null)) {
			if (serviceContext == null) {
				objectValidationRule.setCreateDate(date);
			}
			else {
				objectValidationRule.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!objectValidationRuleModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				objectValidationRule.setModifiedDate(date);
			}
			else {
				objectValidationRule.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(objectValidationRule);
			}
			else {
				objectValidationRule = (ObjectValidationRule)session.merge(
					objectValidationRule);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(objectValidationRule, false);

		if (isNew) {
			objectValidationRule.setNew(false);
		}

		objectValidationRule.resetOriginalValues();

		return objectValidationRule;
	}

	/**
	 * Returns the object validation rule with the primary key or throws a <code>NoSuchObjectValidationRuleException</code> if it could not be found.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule
	 * @throws NoSuchObjectValidationRuleException if a object validation rule with the primary key could not be found
	 */
	@Override
	public ObjectValidationRule findByPrimaryKey(long objectValidationRuleId)
		throws NoSuchObjectValidationRuleException {

		return findByPrimaryKey((Serializable)objectValidationRuleId);
	}

	/**
	 * Returns the object validation rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectValidationRuleId the primary key of the object validation rule
	 * @return the object validation rule, or <code>null</code> if a object validation rule with the primary key could not be found
	 */
	@Override
	public ObjectValidationRule fetchByPrimaryKey(long objectValidationRuleId) {
		return fetchByPrimaryKey((Serializable)objectValidationRuleId);
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
		return "objectValidationRuleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OBJECTVALIDATIONRULE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ObjectValidationRuleModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the object validation rule persistence.
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
			_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE,
			_SQL_COUNT_OBJECTVALIDATIONRULE_WHERE,
			ObjectValidationRuleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"objectValidationRule.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectValidationRule::getUuid));

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
				_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE,
				_SQL_COUNT_OBJECTVALIDATIONRULE_WHERE,
				ObjectValidationRuleModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"objectValidationRule.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ObjectValidationRule::getUuid),
				new FinderColumn<>(
					"objectValidationRule.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectValidationRule::getCompanyId));

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
				_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE,
				_SQL_COUNT_OBJECTVALIDATIONRULE_WHERE,
				ObjectValidationRuleModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"objectValidationRule.", "objectDefinitionId",
					FinderColumn.Type.LONG, "=", true, true,
					ObjectValidationRule::getObjectDefinitionId));

		_collectionPersistenceFinderByODI_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"objectDefinitionId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"objectDefinitionId", "active_"}, false),
			_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE,
			_SQL_COUNT_OBJECTVALIDATIONRULE_WHERE,
			ObjectValidationRuleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"objectValidationRule.", "objectDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectValidationRule::getObjectDefinitionId),
			new FinderColumn<>(
				"objectValidationRule.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				ObjectValidationRule::isActive));

		_collectionPersistenceFinderByODI_E = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_E",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"objectDefinitionId", "engine"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_E",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectDefinitionId", "engine"}, 0, 2, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_E",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectDefinitionId", "engine"}, 0, 2, false,
				null),
			_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE,
			_SQL_COUNT_OBJECTVALIDATIONRULE_WHERE,
			ObjectValidationRuleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"objectValidationRule.", "objectDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectValidationRule::getObjectDefinitionId),
			new FinderColumn<>(
				"objectValidationRule.", "engine", FinderColumn.Type.STRING,
				"=", true, true, ObjectValidationRule::getEngine));

		_collectionPersistenceFinderByODI_O = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByODI_O",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"objectDefinitionId", "outputType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByODI_O",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectDefinitionId", "outputType"}, 0, 2, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByODI_O",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"objectDefinitionId", "outputType"}, 0, 2, false,
				null),
			_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE,
			_SQL_COUNT_OBJECTVALIDATIONRULE_WHERE,
			ObjectValidationRuleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"objectValidationRule.", "objectDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectValidationRule::getObjectDefinitionId),
			new FinderColumn<>(
				"objectValidationRule.", "outputType", FinderColumn.Type.STRING,
				"=", true, true, ObjectValidationRule::getOutputType));

		_collectionPersistenceFinderByA_E = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_E",
				new String[] {
					Boolean.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"active_", "engine"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_E",
				new String[] {Boolean.class.getName(), String.class.getName()},
				new String[] {"active_", "engine"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByA_E",
				new String[] {Boolean.class.getName(), String.class.getName()},
				new String[] {"active_", "engine"}, 0, 2, false, null),
			_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE,
			_SQL_COUNT_OBJECTVALIDATIONRULE_WHERE,
			ObjectValidationRuleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"objectValidationRule.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				ObjectValidationRule::isActive),
			new FinderColumn<>(
				"objectValidationRule.", "engine", FinderColumn.Type.STRING,
				"=", true, true, ObjectValidationRule::getEngine));

		_uniquePersistenceFinderByERC_C_ODI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C_ODI",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"externalReferenceCode", "companyId", "objectDefinitionId"
				},
				0, 1, false,
				convertNullFunction(
					ObjectValidationRule::getExternalReferenceCode),
				ObjectValidationRule::getCompanyId,
				ObjectValidationRule::getObjectDefinitionId),
			_SQL_SELECT_OBJECTVALIDATIONRULE_WHERE, "",
			new FinderColumn<>(
				"objectValidationRule.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ObjectValidationRule::getExternalReferenceCode),
			new FinderColumn<>(
				"objectValidationRule.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, ObjectValidationRule::getCompanyId),
			new FinderColumn<>(
				"objectValidationRule.", "objectDefinitionId",
				FinderColumn.Type.LONG, "=", true, true,
				ObjectValidationRule::getObjectDefinitionId));

		ObjectValidationRuleUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ObjectValidationRuleUtil.setPersistence(null);

		entityCache.removeCache(ObjectValidationRuleImpl.class.getName());
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
		ObjectValidationRuleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OBJECTVALIDATIONRULE =
		"SELECT objectValidationRule FROM ObjectValidationRule objectValidationRule";

	private static final String _SQL_SELECT_OBJECTVALIDATIONRULE_WHERE =
		"SELECT objectValidationRule FROM ObjectValidationRule objectValidationRule WHERE ";

	private static final String _SQL_COUNT_OBJECTVALIDATIONRULE_WHERE =
		"SELECT COUNT(objectValidationRule) FROM ObjectValidationRule objectValidationRule WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ObjectValidationRule exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectValidationRulePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-396231980