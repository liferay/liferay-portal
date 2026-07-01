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
import com.liferay.portal.kernel.exception.DuplicateWorkflowDefinitionLinkExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchWorkflowDefinitionLinkException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.WorkflowDefinitionLinkTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.WorkflowDefinitionLinkPersistence;
import com.liferay.portal.kernel.service.persistence.WorkflowDefinitionLinkUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
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
import com.liferay.portal.model.impl.WorkflowDefinitionLinkImpl;
import com.liferay.portal.model.impl.WorkflowDefinitionLinkModelImpl;

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

/**
 * The persistence implementation for the workflow definition link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WorkflowDefinitionLinkPersistenceImpl
	extends BasePersistenceImpl
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
	implements WorkflowDefinitionLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WorkflowDefinitionLinkUtil</code> to access the workflow definition link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WorkflowDefinitionLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUuid_First(
			String uuid,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUuid_First(
		String uuid,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUUID_G(String uuid, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the workflow definition link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	@Override
	public WorkflowDefinitionLink removeByUUID_G(String uuid, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByUUID_G(
			uuid, groupId);

		return remove(workflowDefinitionLink);
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByCompanyId_First(
			long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByCompanyId_First(
		long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of workflow definition links where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of workflow definition links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByG_C_C;

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByG_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C_C(long groupId, long companyId, long classNameId) {
		_collectionPersistenceFinderByG_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId});
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_C(long groupId, long companyId, long classNameId) {
		return _collectionPersistenceFinderByG_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByG_C_CPK;

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_CPK.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_CPK_First(
			long groupId, long companyId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByG_C_CPK.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_CPK_First(
		long groupId, long companyId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByG_C_CPK.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_CPK(long groupId, long companyId, long classPK) {
		_collectionPersistenceFinderByG_C_CPK.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classPK});
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_CPK(long groupId, long companyId, long classPK) {
		return _collectionPersistenceFinderByG_C_CPK.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classPK});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByC_W_W;

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_W_W.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, workflowDefinitionName, workflowDefinitionVersion
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByC_W_W_First(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByC_W_W.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, workflowDefinitionName, workflowDefinitionVersion
			},
			orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByC_W_W_First(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByC_W_W.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, workflowDefinitionName, workflowDefinitionVersion
			},
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 */
	@Override
	public void removeByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		_collectionPersistenceFinderByC_W_W.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, workflowDefinitionName, workflowDefinitionVersion
			});
	}

	/**
	 * Returns the number of workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		return _collectionPersistenceFinderByC_W_W.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, workflowDefinitionName, workflowDefinitionVersion
			});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByG_C_C_C;

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByG_C_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		_collectionPersistenceFinderByG_C_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByG_C_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_collectionPersistenceFinderByG_C_C_C_T;

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK, typePK},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_C_T_First(
			long groupId, long companyId, long classNameId, long classPK,
			long typePK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		return _collectionPersistenceFinderByG_C_C_C_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK, typePK},
			orderByComparator);
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_C_T_First(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_C_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK, typePK},
			orderByComparator);
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 */
	@Override
	public void removeByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK) {

		_collectionPersistenceFinderByG_C_C_C_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK, typePK});
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK) {

		return _collectionPersistenceFinderByG_C_C_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, classPK, typePK});
	}

	private UniquePersistenceFinder
		<WorkflowDefinitionLink, NoSuchWorkflowDefinitionLinkException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	@Override
	public WorkflowDefinitionLink removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByERC_G(
			externalReferenceCode, groupId);

		return remove(workflowDefinitionLink);
	}

	/**
	 * Returns the number of workflow definition links where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public WorkflowDefinitionLinkPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(WorkflowDefinitionLink.class);

		setModelImplClass(WorkflowDefinitionLinkImpl.class);
		setModelPKClass(long.class);

		setTable(WorkflowDefinitionLinkTable.INSTANCE);
	}

	/**
	 * Creates a new workflow definition link with the primary key. Does not add the workflow definition link to the database.
	 *
	 * @param workflowDefinitionLinkId the primary key for the new workflow definition link
	 * @return the new workflow definition link
	 */
	@Override
	public WorkflowDefinitionLink create(long workflowDefinitionLinkId) {
		WorkflowDefinitionLink workflowDefinitionLink =
			new WorkflowDefinitionLinkImpl();

		workflowDefinitionLink.setNew(true);
		workflowDefinitionLink.setPrimaryKey(workflowDefinitionLinkId);

		String uuid = PortalUUIDUtil.generate();

		workflowDefinitionLink.setUuid(uuid);

		workflowDefinitionLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return workflowDefinitionLink;
	}

	/**
	 * Removes the workflow definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link that was removed
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink remove(long workflowDefinitionLinkId)
		throws NoSuchWorkflowDefinitionLinkException {

		return remove((Serializable)workflowDefinitionLinkId);
	}

	@Override
	protected WorkflowDefinitionLink removeImpl(
		WorkflowDefinitionLink workflowDefinitionLink) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(workflowDefinitionLink)) {
				workflowDefinitionLink = (WorkflowDefinitionLink)session.get(
					WorkflowDefinitionLinkImpl.class,
					workflowDefinitionLink.getPrimaryKeyObj());
			}

			if ((workflowDefinitionLink != null) &&
				CTPersistenceHelperUtil.isRemove(workflowDefinitionLink)) {

				session.delete(workflowDefinitionLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (workflowDefinitionLink != null) {
			clearCache(workflowDefinitionLink);
		}

		return workflowDefinitionLink;
	}

	@Override
	public WorkflowDefinitionLink updateImpl(
		WorkflowDefinitionLink workflowDefinitionLink) {

		boolean isNew = workflowDefinitionLink.isNew();

		if (!(workflowDefinitionLink instanceof
				WorkflowDefinitionLinkModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(workflowDefinitionLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					workflowDefinitionLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in workflowDefinitionLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WorkflowDefinitionLink implementation " +
					workflowDefinitionLink.getClass());
		}

		WorkflowDefinitionLinkModelImpl workflowDefinitionLinkModelImpl =
			(WorkflowDefinitionLinkModelImpl)workflowDefinitionLink;

		if (Validator.isNull(workflowDefinitionLink.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			workflowDefinitionLink.setUuid(uuid);
		}

		if (Validator.isNull(
				workflowDefinitionLink.getExternalReferenceCode())) {

			workflowDefinitionLink.setExternalReferenceCode(
				workflowDefinitionLink.getUuid());
		}
		else {
			if (!Objects.equals(
					workflowDefinitionLinkModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					workflowDefinitionLink.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = workflowDefinitionLink.getCompanyId();

					long groupId = workflowDefinitionLink.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = workflowDefinitionLink.getPrimaryKey();
					}

					try {
						workflowDefinitionLink.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								WorkflowDefinitionLink.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								workflowDefinitionLink.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			WorkflowDefinitionLink ercWorkflowDefinitionLink = fetchByERC_G(
				workflowDefinitionLink.getExternalReferenceCode(),
				workflowDefinitionLink.getGroupId());

			if (isNew) {
				if (ercWorkflowDefinitionLink != null) {
					throw new DuplicateWorkflowDefinitionLinkExternalReferenceCodeException(
						"Duplicate workflow definition link with external reference code " +
							workflowDefinitionLink.getExternalReferenceCode() +
								" and group " +
									workflowDefinitionLink.getGroupId());
				}
			}
			else {
				if ((ercWorkflowDefinitionLink != null) &&
					(workflowDefinitionLink.getWorkflowDefinitionLinkId() !=
						ercWorkflowDefinitionLink.
							getWorkflowDefinitionLinkId())) {

					throw new DuplicateWorkflowDefinitionLinkExternalReferenceCodeException(
						"Duplicate workflow definition link with external reference code " +
							workflowDefinitionLink.getExternalReferenceCode() +
								" and group " +
									workflowDefinitionLink.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (workflowDefinitionLink.getCreateDate() == null)) {
			if (serviceContext == null) {
				workflowDefinitionLink.setCreateDate(date);
			}
			else {
				workflowDefinitionLink.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!workflowDefinitionLinkModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				workflowDefinitionLink.setModifiedDate(date);
			}
			else {
				workflowDefinitionLink.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(workflowDefinitionLink)) {
				if (!isNew) {
					session.evict(
						WorkflowDefinitionLinkImpl.class,
						workflowDefinitionLink.getPrimaryKeyObj());
				}

				session.save(workflowDefinitionLink);
			}
			else {
				workflowDefinitionLink = (WorkflowDefinitionLink)session.merge(
					workflowDefinitionLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(workflowDefinitionLink, false);

		if (isNew) {
			workflowDefinitionLink.setNew(false);
		}

		workflowDefinitionLink.resetOriginalValues();

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link with the primary key or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByPrimaryKey(
			long workflowDefinitionLinkId)
		throws NoSuchWorkflowDefinitionLinkException {

		return findByPrimaryKey((Serializable)workflowDefinitionLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the workflow definition link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link, or <code>null</code> if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByPrimaryKey(
		long workflowDefinitionLinkId) {

		return fetchByPrimaryKey((Serializable)workflowDefinitionLinkId);
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
		return "workflowDefinitionLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WORKFLOWDEFINITIONLINK;
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
		return WorkflowDefinitionLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "WorkflowDefinitionLink";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("typePK");
		ctMergeColumnNames.add("workflowDefinitionName");
		ctMergeColumnNames.add("workflowDefinitionVersion");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("workflowDefinitionLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the workflow definition link persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
			_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
			WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"workflowDefinitionLink.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowDefinitionLink::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(WorkflowDefinitionLink::getUuid),
				WorkflowDefinitionLink::getGroupId),
			_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE, "",
			new FinderColumn<>(
				"workflowDefinitionLink.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowDefinitionLink::getUuid),
			new FinderColumn<>(
				"workflowDefinitionLink.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowDefinitionLink::getGroupId));

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
				_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
				_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
				WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowDefinitionLink.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					WorkflowDefinitionLink::getUuid),
				new FinderColumn<>(
					"workflowDefinitionLink.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getCompanyId));

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
				_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
				_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
				WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowDefinitionLink.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getCompanyId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
			_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
			WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"workflowDefinitionLink.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowDefinitionLink::getCompanyId),
			new FinderColumn<>(
				"workflowDefinitionLink.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowDefinitionLink::getClassNameId));

		_collectionPersistenceFinderByG_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "companyId", "classNameId"}, false),
			_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
			_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
			WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"workflowDefinitionLink.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowDefinitionLink::getGroupId),
			new FinderColumn<>(
				"workflowDefinitionLink.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowDefinitionLink::getCompanyId),
			new FinderColumn<>(
				"workflowDefinitionLink.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				WorkflowDefinitionLink::getClassNameId));

		_collectionPersistenceFinderByG_C_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "companyId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "companyId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "companyId", "classPK"}, false),
				_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
				_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
				WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowDefinitionLink.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getGroupId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getCompanyId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getClassPK));

		_collectionPersistenceFinderByC_W_W = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_W_W",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"companyId", "workflowDefinitionName",
					"workflowDefinitionVersion"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_W_W",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"companyId", "workflowDefinitionName",
					"workflowDefinitionVersion"
				},
				0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_W_W",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"companyId", "workflowDefinitionName",
					"workflowDefinitionVersion"
				},
				0, 2, false, null),
			_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
			_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
			WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"workflowDefinitionLink.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowDefinitionLink::getCompanyId),
			new FinderColumn<>(
				"workflowDefinitionLink.", "workflowDefinitionName",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowDefinitionLink::getWorkflowDefinitionName),
			new FinderColumn<>(
				"workflowDefinitionLink.", "workflowDefinitionVersion",
				FinderColumn.Type.INTEGER, "=", true, true,
				WorkflowDefinitionLink::getWorkflowDefinitionVersion));

		_collectionPersistenceFinderByG_C_C_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK"
					},
					false),
				_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
				_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
				WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowDefinitionLink.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getGroupId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getCompanyId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getClassNameId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getClassPK));

		_collectionPersistenceFinderByG_C_C_C_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK",
						"typePK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_C_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK",
						"typePK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_C_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "classPK",
						"typePK"
					},
					false),
				_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE,
				_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE,
				WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"workflowDefinitionLink.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getGroupId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getCompanyId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getClassNameId),
				new FinderColumn<>(
					"workflowDefinitionLink.", "classPK",
					FinderColumn.Type.LONG, "=", true, true,
					WorkflowDefinitionLink::getClassPK),
				new FinderColumn<>(
					"workflowDefinitionLink.", "typePK", FinderColumn.Type.LONG,
					"=", true, true, WorkflowDefinitionLink::getTypePK));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					WorkflowDefinitionLink::getExternalReferenceCode),
				WorkflowDefinitionLink::getGroupId),
			_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE, "",
			new FinderColumn<>(
				"workflowDefinitionLink.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				WorkflowDefinitionLink::getExternalReferenceCode),
			new FinderColumn<>(
				"workflowDefinitionLink.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, WorkflowDefinitionLink::getGroupId));

		WorkflowDefinitionLinkUtil.setPersistence(this);
	}

	public void destroy() {
		WorkflowDefinitionLinkUtil.setPersistence(null);

		EntityCacheUtil.removeCache(WorkflowDefinitionLinkImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		WorkflowDefinitionLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_WORKFLOWDEFINITIONLINK =
		"SELECT workflowDefinitionLink FROM WorkflowDefinitionLink workflowDefinitionLink";

	private static final String _SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE =
		"SELECT workflowDefinitionLink FROM WorkflowDefinitionLink workflowDefinitionLink WHERE ";

	private static final String _SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE =
		"SELECT COUNT(workflowDefinitionLink) FROM WorkflowDefinitionLink workflowDefinitionLink WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WorkflowDefinitionLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowDefinitionLinkPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1030908585