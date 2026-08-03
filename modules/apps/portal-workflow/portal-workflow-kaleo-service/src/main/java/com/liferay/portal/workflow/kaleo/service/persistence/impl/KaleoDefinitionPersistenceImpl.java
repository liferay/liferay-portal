/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
import com.liferay.portal.workflow.kaleo.exception.DuplicateKaleoDefinitionExternalReferenceCodeException;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoDefinitionUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the kaleo definition service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoDefinitionPersistence.class)
public class KaleoDefinitionPersistenceImpl
	extends BasePersistenceImpl<KaleoDefinition, NoSuchDefinitionException>
	implements KaleoDefinitionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoDefinitionUtil</code> to access the kaleo definition persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoDefinitionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<KaleoDefinition, NoSuchDefinitionException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the kaleo definitions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definitions
	 */
	@Override
	public List<KaleoDefinition> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByUuid_First(
			String uuid, OrderByComparator<KaleoDefinition> orderByComparator)
		throws NoSuchDefinitionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByUuid_First(
		String uuid, OrderByComparator<KaleoDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the kaleo definitions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of kaleo definitions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<KaleoDefinition, NoSuchDefinitionException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the kaleo definition where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchDefinitionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByUUID_G(String uuid, long groupId)
		throws NoSuchDefinitionException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the kaleo definition where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the kaleo definition where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kaleo definition that was removed
	 */
	@Override
	public KaleoDefinition removeByUUID_G(String uuid, long groupId)
		throws NoSuchDefinitionException {

		KaleoDefinition kaleoDefinition = findByUUID_G(uuid, groupId);

		return remove(kaleoDefinition);
	}

	/**
	 * Returns the number of kaleo definitions where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<KaleoDefinition, NoSuchDefinitionException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the kaleo definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definitions
	 */
	@Override
	public List<KaleoDefinition> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KaleoDefinition> orderByComparator)
		throws NoSuchDefinitionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KaleoDefinition> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo definitions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of kaleo definitions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<KaleoDefinition, NoSuchDefinitionException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo definitions where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definitions
	 */
	@Override
	public List<KaleoDefinition> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByCompanyId_First(
			long companyId,
			OrderByComparator<KaleoDefinition> orderByComparator)
		throws NoSuchDefinitionException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoDefinition> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo definitions where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo definitions where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<KaleoDefinition, NoSuchDefinitionException>
			_collectionPersistenceFinderByActive;

	/**
	 * Returns an ordered range of all the kaleo definitions where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definitions
	 */
	@Override
	public List<KaleoDefinition> findByActive(
		boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByActive.find(
			finderCache, new Object[] {active}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByActive_First(
			boolean active,
			OrderByComparator<KaleoDefinition> orderByComparator)
		throws NoSuchDefinitionException {

		return _collectionPersistenceFinderByActive.findFirst(
			finderCache, new Object[] {active}, orderByComparator);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByActive_First(
		boolean active, OrderByComparator<KaleoDefinition> orderByComparator) {

		return _collectionPersistenceFinderByActive.fetchFirst(
			finderCache, new Object[] {active}, orderByComparator);
	}

	/**
	 * Removes all the kaleo definitions where active = &#63; from the database.
	 *
	 * @param active the active
	 */
	@Override
	public void removeByActive(boolean active) {
		_collectionPersistenceFinderByActive.remove(
			finderCache, new Object[] {active});
	}

	/**
	 * Returns the number of kaleo definitions where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByActive(boolean active) {
		return _collectionPersistenceFinderByActive.count(
			finderCache, new Object[] {active});
	}

	private UniquePersistenceFinder<KaleoDefinition, NoSuchDefinitionException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the kaleo definition where companyId = &#63; and name = &#63; or throws a <code>NoSuchDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByC_N(long companyId, String name)
		throws NoSuchDefinitionException {

		return _uniquePersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the kaleo definition where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			finderCache, new Object[] {companyId, name}, useFinderCache);
	}

	/**
	 * Removes the kaleo definition where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the kaleo definition that was removed
	 */
	@Override
	public KaleoDefinition removeByC_N(long companyId, String name)
		throws NoSuchDefinitionException {

		KaleoDefinition kaleoDefinition = findByC_N(companyId, name);

		return remove(kaleoDefinition);
	}

	/**
	 * Returns the number of kaleo definitions where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	private CollectionPersistenceFinder
		<KaleoDefinition, NoSuchDefinitionException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the kaleo definitions where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definitions
	 */
	@Override
	public List<KaleoDefinition> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<KaleoDefinition> orderByComparator)
		throws NoSuchDefinitionException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<KaleoDefinition> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, active}, orderByComparator);
	}

	/**
	 * Removes all the kaleo definitions where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long companyId, boolean active) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {companyId, active});
	}

	/**
	 * Returns the number of kaleo definitions where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, active});
	}

	private CollectionPersistenceFinder
		<KaleoDefinition, NoSuchDefinitionException>
			_collectionPersistenceFinderByG_C_S;

	/**
	 * Returns an ordered range of all the kaleo definitions where groupId = &#63; and companyId = &#63; and scope = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definitions
	 */
	@Override
	public List<KaleoDefinition> findByG_C_S(
		long groupId, long companyId, String scope, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_S.find(
			finderCache, new Object[] {groupId, companyId, scope}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where groupId = &#63; and companyId = &#63; and scope = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByG_C_S_First(
			long groupId, long companyId, String scope,
			OrderByComparator<KaleoDefinition> orderByComparator)
		throws NoSuchDefinitionException {

		return _collectionPersistenceFinderByG_C_S.findFirst(
			finderCache, new Object[] {groupId, companyId, scope},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where groupId = &#63; and companyId = &#63; and scope = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByG_C_S_First(
		long groupId, long companyId, String scope,
		OrderByComparator<KaleoDefinition> orderByComparator) {

		return _collectionPersistenceFinderByG_C_S.fetchFirst(
			finderCache, new Object[] {groupId, companyId, scope},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo definitions where groupId = &#63; and companyId = &#63; and scope = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 */
	@Override
	public void removeByG_C_S(long groupId, long companyId, String scope) {
		_collectionPersistenceFinderByG_C_S.remove(
			finderCache, new Object[] {groupId, companyId, scope});
	}

	/**
	 * Returns the number of kaleo definitions where groupId = &#63; and companyId = &#63; and scope = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByG_C_S(long groupId, long companyId, String scope) {
		return _collectionPersistenceFinderByG_C_S.count(
			finderCache, new Object[] {groupId, companyId, scope});
	}

	private UniquePersistenceFinder<KaleoDefinition, NoSuchDefinitionException>
		_uniquePersistenceFinderByC_N_V;

	/**
	 * Returns the kaleo definition where companyId = &#63; and name = &#63; and version = &#63; or throws a <code>NoSuchDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @return the matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByC_N_V(long companyId, String name, int version)
		throws NoSuchDefinitionException {

		return _uniquePersistenceFinderByC_N_V.find(
			finderCache, new Object[] {companyId, name, version});
	}

	/**
	 * Returns the kaleo definition where companyId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByC_N_V(
		long companyId, String name, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N_V.fetch(
			finderCache, new Object[] {companyId, name, version},
			useFinderCache);
	}

	/**
	 * Removes the kaleo definition where companyId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @return the kaleo definition that was removed
	 */
	@Override
	public KaleoDefinition removeByC_N_V(
			long companyId, String name, int version)
		throws NoSuchDefinitionException {

		KaleoDefinition kaleoDefinition = findByC_N_V(companyId, name, version);

		return remove(kaleoDefinition);
	}

	/**
	 * Returns the number of kaleo definitions where companyId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByC_N_V(long companyId, String name, int version) {
		return _uniquePersistenceFinderByC_N_V.count(
			finderCache, new Object[] {companyId, name, version});
	}

	private UniquePersistenceFinder<KaleoDefinition, NoSuchDefinitionException>
		_uniquePersistenceFinderByC_N_A;

	/**
	 * Returns the kaleo definition where companyId = &#63; and name = &#63; and active = &#63; or throws a <code>NoSuchDefinitionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param active the active
	 * @return the matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByC_N_A(
			long companyId, String name, boolean active)
		throws NoSuchDefinitionException {

		return _uniquePersistenceFinderByC_N_A.find(
			finderCache, new Object[] {companyId, name, active});
	}

	/**
	 * Returns the kaleo definition where companyId = &#63; and name = &#63; and active = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param active the active
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByC_N_A(
		long companyId, String name, boolean active, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N_A.fetch(
			finderCache, new Object[] {companyId, name, active},
			useFinderCache);
	}

	/**
	 * Removes the kaleo definition where companyId = &#63; and name = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param active the active
	 * @return the kaleo definition that was removed
	 */
	@Override
	public KaleoDefinition removeByC_N_A(
			long companyId, String name, boolean active)
		throws NoSuchDefinitionException {

		KaleoDefinition kaleoDefinition = findByC_N_A(companyId, name, active);

		return remove(kaleoDefinition);
	}

	/**
	 * Returns the number of kaleo definitions where companyId = &#63; and name = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param active the active
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByC_N_A(long companyId, String name, boolean active) {
		return _uniquePersistenceFinderByC_N_A.count(
			finderCache, new Object[] {companyId, name, active});
	}

	private CollectionPersistenceFinder
		<KaleoDefinition, NoSuchDefinitionException>
			_collectionPersistenceFinderByG_C_S_A;

	/**
	 * Returns an ordered range of all the kaleo definitions where groupId = &#63; and companyId = &#63; and scope = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param active the active
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo definitions
	 */
	@Override
	public List<KaleoDefinition> findByG_C_S_A(
		long groupId, long companyId, String scope, boolean active, int start,
		int end, OrderByComparator<KaleoDefinition> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_S_A.find(
			finderCache, new Object[] {groupId, companyId, scope, active},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where groupId = &#63; and companyId = &#63; and scope = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByG_C_S_A_First(
			long groupId, long companyId, String scope, boolean active,
			OrderByComparator<KaleoDefinition> orderByComparator)
		throws NoSuchDefinitionException {

		return _collectionPersistenceFinderByG_C_S_A.findFirst(
			finderCache, new Object[] {groupId, companyId, scope, active},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo definition in the ordered set where groupId = &#63; and companyId = &#63; and scope = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByG_C_S_A_First(
		long groupId, long companyId, String scope, boolean active,
		OrderByComparator<KaleoDefinition> orderByComparator) {

		return _collectionPersistenceFinderByG_C_S_A.fetchFirst(
			finderCache, new Object[] {groupId, companyId, scope, active},
			orderByComparator);
	}

	/**
	 * Removes all the kaleo definitions where groupId = &#63; and companyId = &#63; and scope = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param active the active
	 */
	@Override
	public void removeByG_C_S_A(
		long groupId, long companyId, String scope, boolean active) {

		_collectionPersistenceFinderByG_C_S_A.remove(
			finderCache, new Object[] {groupId, companyId, scope, active});
	}

	/**
	 * Returns the number of kaleo definitions where groupId = &#63; and companyId = &#63; and scope = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param scope the scope
	 * @param active the active
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByG_C_S_A(
		long groupId, long companyId, String scope, boolean active) {

		return _collectionPersistenceFinderByG_C_S_A.count(
			finderCache, new Object[] {groupId, companyId, scope, active});
	}

	private UniquePersistenceFinder<KaleoDefinition, NoSuchDefinitionException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the kaleo definition where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchDefinitionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching kaleo definition
	 * @throws NoSuchDefinitionException if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDefinitionException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the kaleo definition where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the kaleo definition where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the kaleo definition that was removed
	 */
	@Override
	public KaleoDefinition removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDefinitionException {

		KaleoDefinition kaleoDefinition = findByERC_C(
			externalReferenceCode, companyId);

		return remove(kaleoDefinition);
	}

	/**
	 * Returns the number of kaleo definitions where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching kaleo definitions
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public KaleoDefinitionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(KaleoDefinition.class);

		setModelImplClass(KaleoDefinitionImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoDefinitionTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo definition with the primary key. Does not add the kaleo definition to the database.
	 *
	 * @param kaleoDefinitionId the primary key for the new kaleo definition
	 * @return the new kaleo definition
	 */
	@Override
	public KaleoDefinition create(long kaleoDefinitionId) {
		KaleoDefinition kaleoDefinition = new KaleoDefinitionImpl();

		kaleoDefinition.setNew(true);
		kaleoDefinition.setPrimaryKey(kaleoDefinitionId);

		String uuid = PortalUUIDUtil.generate();

		kaleoDefinition.setUuid(uuid);

		kaleoDefinition.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoDefinition;
	}

	/**
	 * Removes the kaleo definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition that was removed
	 * @throws NoSuchDefinitionException if a kaleo definition with the primary key could not be found
	 */
	@Override
	public KaleoDefinition remove(long kaleoDefinitionId)
		throws NoSuchDefinitionException {

		return remove((Serializable)kaleoDefinitionId);
	}

	@Override
	protected KaleoDefinition removeImpl(KaleoDefinition kaleoDefinition) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoDefinition)) {
				kaleoDefinition = (KaleoDefinition)session.get(
					KaleoDefinitionImpl.class,
					kaleoDefinition.getPrimaryKeyObj());
			}

			if ((kaleoDefinition != null) &&
				ctPersistenceHelper.isRemove(kaleoDefinition)) {

				session.delete(kaleoDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoDefinition != null) {
			clearCache(kaleoDefinition);
		}

		return kaleoDefinition;
	}

	@Override
	public KaleoDefinition updateImpl(KaleoDefinition kaleoDefinition) {
		boolean isNew = kaleoDefinition.isNew();

		if (!(kaleoDefinition instanceof KaleoDefinitionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoDefinition.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoDefinition);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoDefinition proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoDefinition implementation " +
					kaleoDefinition.getClass());
		}

		KaleoDefinitionModelImpl kaleoDefinitionModelImpl =
			(KaleoDefinitionModelImpl)kaleoDefinition;

		if (Validator.isNull(kaleoDefinition.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			kaleoDefinition.setUuid(uuid);
		}

		if (Validator.isNull(kaleoDefinition.getExternalReferenceCode())) {
			kaleoDefinition.setExternalReferenceCode(kaleoDefinition.getUuid());
		}
		else {
			if (!Objects.equals(
					kaleoDefinitionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					kaleoDefinition.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = kaleoDefinition.getCompanyId();

					long groupId = kaleoDefinition.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = kaleoDefinition.getPrimaryKey();
					}

					try {
						kaleoDefinition.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								KaleoDefinition.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								kaleoDefinition.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			KaleoDefinition ercKaleoDefinition = fetchByERC_C(
				kaleoDefinition.getExternalReferenceCode(),
				kaleoDefinition.getCompanyId());

			if (isNew) {
				if (ercKaleoDefinition != null) {
					throw new DuplicateKaleoDefinitionExternalReferenceCodeException(
						"Duplicate kaleo definition with external reference code " +
							kaleoDefinition.getExternalReferenceCode() +
								" and company " +
									kaleoDefinition.getCompanyId());
				}
			}
			else {
				if ((ercKaleoDefinition != null) &&
					(kaleoDefinition.getKaleoDefinitionId() !=
						ercKaleoDefinition.getKaleoDefinitionId())) {

					throw new DuplicateKaleoDefinitionExternalReferenceCodeException(
						"Duplicate kaleo definition with external reference code " +
							kaleoDefinition.getExternalReferenceCode() +
								" and company " +
									kaleoDefinition.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoDefinition.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoDefinition.setCreateDate(date);
			}
			else {
				kaleoDefinition.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoDefinitionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoDefinition.setModifiedDate(date);
			}
			else {
				kaleoDefinition.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoDefinition)) {
				if (!isNew) {
					session.evict(
						KaleoDefinitionImpl.class,
						kaleoDefinition.getPrimaryKeyObj());
				}

				session.save(kaleoDefinition);
			}
			else {
				kaleoDefinition = (KaleoDefinition)session.merge(
					kaleoDefinition);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoDefinition, false);

		if (isNew) {
			kaleoDefinition.setNew(false);
		}

		kaleoDefinition.resetOriginalValues();

		return kaleoDefinition;
	}

	/**
	 * Returns the kaleo definition with the primary key or throws a <code>NoSuchDefinitionException</code> if it could not be found.
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition
	 * @throws NoSuchDefinitionException if a kaleo definition with the primary key could not be found
	 */
	@Override
	public KaleoDefinition findByPrimaryKey(long kaleoDefinitionId)
		throws NoSuchDefinitionException {

		return findByPrimaryKey((Serializable)kaleoDefinitionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo definition with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition, or <code>null</code> if a kaleo definition with the primary key could not be found
	 */
	@Override
	public KaleoDefinition fetchByPrimaryKey(long kaleoDefinitionId) {
		return fetchByPrimaryKey((Serializable)kaleoDefinitionId);
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
		return "kaleoDefinitionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEODEFINITION;
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
		return KaleoDefinitionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoDefinition";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("scope");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("system_");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoDefinitionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the kaleo definition persistence.
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
			_SQL_SELECT_KALEODEFINITION_WHERE, _SQL_COUNT_KALEODEFINITION_WHERE,
			KaleoDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"kaleoDefinition.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, KaleoDefinition::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(KaleoDefinition::getUuid),
				KaleoDefinition::getGroupId),
			_SQL_SELECT_KALEODEFINITION_WHERE, "",
			new FinderColumn<>(
				"kaleoDefinition.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, KaleoDefinition::getUuid),
			new FinderColumn<>(
				"kaleoDefinition.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getGroupId));

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
				_SQL_SELECT_KALEODEFINITION_WHERE,
				_SQL_COUNT_KALEODEFINITION_WHERE,
				KaleoDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"kaleoDefinition.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					KaleoDefinition::getUuid),
				new FinderColumn<>(
					"kaleoDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, KaleoDefinition::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_KALEODEFINITION_WHERE,
				_SQL_COUNT_KALEODEFINITION_WHERE,
				KaleoDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"kaleoDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, KaleoDefinition::getCompanyId));

		_collectionPersistenceFinderByActive =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByActive",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, false),
				_SQL_SELECT_KALEODEFINITION_WHERE,
				_SQL_COUNT_KALEODEFINITION_WHERE,
				KaleoDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"kaleoDefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					KaleoDefinition::isActive));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				KaleoDefinition::getCompanyId,
				convertNullFunction(KaleoDefinition::getName)),
			_SQL_SELECT_KALEODEFINITION_WHERE, "",
			new FinderColumn<>(
				"kaleoDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getCompanyId),
			new FinderColumn<>(
				"kaleoDefinition.", "name", FinderColumn.Type.STRING, "=", true,
				true, KaleoDefinition::getName));

		_collectionPersistenceFinderByC_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "active_"}, false),
			_SQL_SELECT_KALEODEFINITION_WHERE, _SQL_COUNT_KALEODEFINITION_WHERE,
			KaleoDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"kaleoDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getCompanyId),
			new FinderColumn<>(
				"kaleoDefinition.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				KaleoDefinition::isActive));

		_collectionPersistenceFinderByG_C_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "companyId", "scope"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "companyId", "scope"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "companyId", "scope"}, 0, 4, false,
				null),
			_SQL_SELECT_KALEODEFINITION_WHERE, _SQL_COUNT_KALEODEFINITION_WHERE,
			KaleoDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"kaleoDefinition.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getGroupId),
			new FinderColumn<>(
				"kaleoDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getCompanyId),
			new FinderColumn<>(
				"kaleoDefinition.", "scope", FinderColumn.Type.STRING, "=",
				true, true, KaleoDefinition::getScope));

		_uniquePersistenceFinderByC_N_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N_V",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {"companyId", "name", "version"}, 0, 2, false,
				KaleoDefinition::getCompanyId,
				convertNullFunction(KaleoDefinition::getName),
				KaleoDefinition::getVersion),
			_SQL_SELECT_KALEODEFINITION_WHERE, "",
			new FinderColumn<>(
				"kaleoDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getCompanyId),
			new FinderColumn<>(
				"kaleoDefinition.", "name", FinderColumn.Type.STRING, "=", true,
				true, KaleoDefinition::getName),
			new FinderColumn<>(
				"kaleoDefinition.", "version", FinderColumn.Type.INTEGER, "=",
				true, true, KaleoDefinition::getVersion));

		_uniquePersistenceFinderByC_N_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N_A",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "name", "active_"}, 0, 2, false,
				KaleoDefinition::getCompanyId,
				convertNullFunction(KaleoDefinition::getName),
				KaleoDefinition::isActive),
			_SQL_SELECT_KALEODEFINITION_WHERE, "",
			new FinderColumn<>(
				"kaleoDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getCompanyId),
			new FinderColumn<>(
				"kaleoDefinition.", "name", FinderColumn.Type.STRING, "=", true,
				true, KaleoDefinition::getName),
			new FinderColumn<>(
				"kaleoDefinition.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				KaleoDefinition::isActive));

		_collectionPersistenceFinderByG_C_S_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_S_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "companyId", "scope", "active_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_S_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "companyId", "scope", "active_"},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_S_A",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "companyId", "scope", "active_"},
					0, 4, false, null),
				_SQL_SELECT_KALEODEFINITION_WHERE,
				_SQL_COUNT_KALEODEFINITION_WHERE,
				KaleoDefinitionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"kaleoDefinition.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, KaleoDefinition::getGroupId),
				new FinderColumn<>(
					"kaleoDefinition.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, KaleoDefinition::getCompanyId),
				new FinderColumn<>(
					"kaleoDefinition.", "scope", FinderColumn.Type.STRING, "=",
					true, true, KaleoDefinition::getScope),
				new FinderColumn<>(
					"kaleoDefinition.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					KaleoDefinition::isActive));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(KaleoDefinition::getExternalReferenceCode),
				KaleoDefinition::getCompanyId),
			_SQL_SELECT_KALEODEFINITION_WHERE, "",
			new FinderColumn<>(
				"kaleoDefinition.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				KaleoDefinition::getExternalReferenceCode),
			new FinderColumn<>(
				"kaleoDefinition.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, KaleoDefinition::getCompanyId));

		KaleoDefinitionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoDefinitionUtil.setPersistence(null);

		entityCache.removeCache(KaleoDefinitionImpl.class.getName());
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = KaleoPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		KaleoDefinitionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEODEFINITION =
		"SELECT kaleoDefinition FROM KaleoDefinition kaleoDefinition";

	private static final String _SQL_SELECT_KALEODEFINITION_WHERE =
		"SELECT kaleoDefinition FROM KaleoDefinition kaleoDefinition WHERE ";

	private static final String _SQL_COUNT_KALEODEFINITION_WHERE =
		"SELECT COUNT(kaleoDefinition) FROM KaleoDefinition kaleoDefinition WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1017558561