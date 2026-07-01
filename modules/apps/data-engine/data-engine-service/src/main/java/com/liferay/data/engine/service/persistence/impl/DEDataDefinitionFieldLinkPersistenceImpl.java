/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service.persistence.impl;

import com.liferay.data.engine.exception.NoSuchDataDefinitionFieldLinkException;
import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.data.engine.model.DEDataDefinitionFieldLinkTable;
import com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkImpl;
import com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl;
import com.liferay.data.engine.service.persistence.DEDataDefinitionFieldLinkPersistence;
import com.liferay.data.engine.service.persistence.DEDataDefinitionFieldLinkUtil;
import com.liferay.data.engine.service.persistence.impl.constants.DEPersistenceConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

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
 * The persistence implementation for the de data definition field link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DEDataDefinitionFieldLinkPersistence.class)
public class DEDataDefinitionFieldLinkPersistenceImpl
	extends BasePersistenceImpl
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
	implements DEDataDefinitionFieldLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DEDataDefinitionFieldLinkUtil</code> to access the de data definition field link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DEDataDefinitionFieldLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the de data definition field links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByUuid_First(
			String uuid,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator)
		throws NoSuchDataDefinitionFieldLinkException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByUuid_First(
		String uuid,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the de data definition field links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of de data definition field links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the de data definition field link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchDataDefinitionFieldLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByUUID_G(String uuid, long groupId)
		throws NoSuchDataDefinitionFieldLinkException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the de data definition field link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the de data definition field link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the de data definition field link that was removed
	 */
	@Override
	public DEDataDefinitionFieldLink removeByUUID_G(String uuid, long groupId)
		throws NoSuchDataDefinitionFieldLinkException {

		DEDataDefinitionFieldLink deDataDefinitionFieldLink = findByUUID_G(
			uuid, groupId);

		return remove(deDataDefinitionFieldLink);
	}

	/**
	 * Returns the number of de data definition field links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the de data definition field links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator)
		throws NoSuchDataDefinitionFieldLinkException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the de data definition field links where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of de data definition field links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_collectionPersistenceFinderByDDMStructureId;

	/**
	 * Returns an ordered range of all the de data definition field links where ddmStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByDDMStructureId(
		long ddmStructureId, int start, int end,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDDMStructureId.find(
			finderCache, new Object[] {ddmStructureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where ddmStructureId = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByDDMStructureId_First(
			long ddmStructureId,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator)
		throws NoSuchDataDefinitionFieldLinkException {

		return _collectionPersistenceFinderByDDMStructureId.findFirst(
			finderCache, new Object[] {ddmStructureId}, orderByComparator);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where ddmStructureId = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByDDMStructureId_First(
		long ddmStructureId,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return _collectionPersistenceFinderByDDMStructureId.fetchFirst(
			finderCache, new Object[] {ddmStructureId}, orderByComparator);
	}

	/**
	 * Removes all the de data definition field links where ddmStructureId = &#63; from the database.
	 *
	 * @param ddmStructureId the ddm structure ID
	 */
	@Override
	public void removeByDDMStructureId(long ddmStructureId) {
		_collectionPersistenceFinderByDDMStructureId.remove(
			finderCache, new Object[] {ddmStructureId});
	}

	/**
	 * Returns the number of de data definition field links where ddmStructureId = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByDDMStructureId(long ddmStructureId) {
		return _collectionPersistenceFinderByDDMStructureId.count(
			finderCache, new Object[] {ddmStructureId});
	}

	private CollectionPersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the de data definition field links where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator)
		throws NoSuchDataDefinitionFieldLinkException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the de data definition field links where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of de data definition field links where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_collectionPersistenceFinderByC_DDMSI;

	/**
	 * Returns an ordered range of all the de data definition field links where classNameId = &#63; and ddmStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByC_DDMSI(
		long classNameId, long ddmStructureId, int start, int end,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_DDMSI.find(
			finderCache, new Object[] {classNameId, ddmStructureId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where classNameId = &#63; and ddmStructureId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByC_DDMSI_First(
			long classNameId, long ddmStructureId,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator)
		throws NoSuchDataDefinitionFieldLinkException {

		return _collectionPersistenceFinderByC_DDMSI.findFirst(
			finderCache, new Object[] {classNameId, ddmStructureId},
			orderByComparator);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where classNameId = &#63; and ddmStructureId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByC_DDMSI_First(
		long classNameId, long ddmStructureId,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return _collectionPersistenceFinderByC_DDMSI.fetchFirst(
			finderCache, new Object[] {classNameId, ddmStructureId},
			orderByComparator);
	}

	/**
	 * Removes all the de data definition field links where classNameId = &#63; and ddmStructureId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 */
	@Override
	public void removeByC_DDMSI(long classNameId, long ddmStructureId) {
		_collectionPersistenceFinderByC_DDMSI.remove(
			finderCache, new Object[] {classNameId, ddmStructureId});
	}

	/**
	 * Returns the number of de data definition field links where classNameId = &#63; and ddmStructureId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByC_DDMSI(long classNameId, long ddmStructureId) {
		return _collectionPersistenceFinderByC_DDMSI.count(
			finderCache, new Object[] {classNameId, ddmStructureId});
	}

	private CollectionPersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_collectionPersistenceFinderByDDMSI_F;

	/**
	 * Returns an ordered range of all the de data definition field links where ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByDDMSI_F(
		long ddmStructureId, String fieldName, int start, int end,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDDMSI_F.find(
			finderCache,
			new Object[] {ddmStructureId, new String[] {fieldName}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByDDMSI_F_First(
			long ddmStructureId, String fieldName,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator)
		throws NoSuchDataDefinitionFieldLinkException {

		return _collectionPersistenceFinderByDDMSI_F.findFirst(
			finderCache,
			new Object[] {ddmStructureId, new String[] {fieldName}},
			orderByComparator);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByDDMSI_F_First(
		long ddmStructureId, String fieldName,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return _collectionPersistenceFinderByDDMSI_F.fetchFirst(
			finderCache,
			new Object[] {ddmStructureId, new String[] {fieldName}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the de data definition field links where ddmStructureId = &#63; and fieldName = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldNames the field names
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByDDMSI_F(
		long ddmStructureId, String[] fieldNames, int start, int end,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByDDMSI_F.find(
			finderCache,
			new Object[] {ddmStructureId, ArrayUtil.sortedUnique(fieldNames)},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the de data definition field links where ddmStructureId = &#63; and fieldName = &#63; from the database.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 */
	@Override
	public void removeByDDMSI_F(long ddmStructureId, String fieldName) {
		_collectionPersistenceFinderByDDMSI_F.remove(
			finderCache,
			new Object[] {ddmStructureId, new String[] {fieldName}});
	}

	/**
	 * Returns the number of de data definition field links where ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByDDMSI_F(long ddmStructureId, String fieldName) {
		return _collectionPersistenceFinderByDDMSI_F.count(
			finderCache,
			new Object[] {ddmStructureId, new String[] {fieldName}});
	}

	/**
	 * Returns the number of de data definition field links where ddmStructureId = &#63; and fieldName = any &#63;.
	 *
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldNames the field names
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByDDMSI_F(long ddmStructureId, String[] fieldNames) {
		return _collectionPersistenceFinderByDDMSI_F.count(
			finderCache,
			new Object[] {ddmStructureId, ArrayUtil.sortedUnique(fieldNames)});
	}

	private CollectionPersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_collectionPersistenceFinderByC_DDMSI_F;

	/**
	 * Returns an ordered range of all the de data definition field links where classNameId = &#63; and ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByC_DDMSI_F(
		long classNameId, long ddmStructureId, String fieldName, int start,
		int end, OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_DDMSI_F.find(
			finderCache,
			new Object[] {
				classNameId, ddmStructureId, new String[] {fieldName}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where classNameId = &#63; and ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByC_DDMSI_F_First(
			long classNameId, long ddmStructureId, String fieldName,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator)
		throws NoSuchDataDefinitionFieldLinkException {

		return _collectionPersistenceFinderByC_DDMSI_F.findFirst(
			finderCache,
			new Object[] {
				classNameId, ddmStructureId, new String[] {fieldName}
			},
			orderByComparator);
	}

	/**
	 * Returns the first de data definition field link in the ordered set where classNameId = &#63; and ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByC_DDMSI_F_First(
		long classNameId, long ddmStructureId, String fieldName,
		OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return _collectionPersistenceFinderByC_DDMSI_F.fetchFirst(
			finderCache,
			new Object[] {
				classNameId, ddmStructureId, new String[] {fieldName}
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the de data definition field links where classNameId = &#63; and ddmStructureId = &#63; and fieldName = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldNames the field names
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching de data definition field links
	 */
	@Override
	public List<DEDataDefinitionFieldLink> findByC_DDMSI_F(
		long classNameId, long ddmStructureId, String[] fieldNames, int start,
		int end, OrderByComparator<DEDataDefinitionFieldLink> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_DDMSI_F.find(
			finderCache,
			new Object[] {
				classNameId, ddmStructureId, ArrayUtil.sortedUnique(fieldNames)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the de data definition field links where classNameId = &#63; and ddmStructureId = &#63; and fieldName = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 */
	@Override
	public void removeByC_DDMSI_F(
		long classNameId, long ddmStructureId, String fieldName) {

		_collectionPersistenceFinderByC_DDMSI_F.remove(
			finderCache,
			new Object[] {
				classNameId, ddmStructureId, new String[] {fieldName}
			});
	}

	/**
	 * Returns the number of de data definition field links where classNameId = &#63; and ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByC_DDMSI_F(
		long classNameId, long ddmStructureId, String fieldName) {

		return _collectionPersistenceFinderByC_DDMSI_F.count(
			finderCache,
			new Object[] {
				classNameId, ddmStructureId, new String[] {fieldName}
			});
	}

	/**
	 * Returns the number of de data definition field links where classNameId = &#63; and ddmStructureId = &#63; and fieldName = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldNames the field names
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByC_DDMSI_F(
		long classNameId, long ddmStructureId, String[] fieldNames) {

		return _collectionPersistenceFinderByC_DDMSI_F.count(
			finderCache,
			new Object[] {
				classNameId, ddmStructureId, ArrayUtil.sortedUnique(fieldNames)
			});
	}

	private FinderPath _finderPathWithPaginationCountByC_C_DDMSI_F;
	private UniquePersistenceFinder
		<DEDataDefinitionFieldLink, NoSuchDataDefinitionFieldLinkException>
			_uniquePersistenceFinderByC_C_DDMSI_F;

	/**
	 * Returns the de data definition field link where classNameId = &#63; and classPK = &#63; and ddmStructureId = &#63; and fieldName = &#63; or throws a <code>NoSuchDataDefinitionFieldLinkException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @return the matching de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByC_C_DDMSI_F(
			long classNameId, long classPK, long ddmStructureId,
			String fieldName)
		throws NoSuchDataDefinitionFieldLinkException {

		return _uniquePersistenceFinderByC_C_DDMSI_F.find(
			finderCache,
			new Object[] {classNameId, classPK, ddmStructureId, fieldName});
	}

	/**
	 * Returns the de data definition field link where classNameId = &#63; and classPK = &#63; and ddmStructureId = &#63; and fieldName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByC_C_DDMSI_F(
		long classNameId, long classPK, long ddmStructureId, String fieldName,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_DDMSI_F.fetch(
			finderCache,
			new Object[] {classNameId, classPK, ddmStructureId, fieldName},
			useFinderCache);
	}

	/**
	 * Removes the de data definition field link where classNameId = &#63; and classPK = &#63; and ddmStructureId = &#63; and fieldName = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @return the de data definition field link that was removed
	 */
	@Override
	public DEDataDefinitionFieldLink removeByC_C_DDMSI_F(
			long classNameId, long classPK, long ddmStructureId,
			String fieldName)
		throws NoSuchDataDefinitionFieldLinkException {

		DEDataDefinitionFieldLink deDataDefinitionFieldLink = findByC_C_DDMSI_F(
			classNameId, classPK, ddmStructureId, fieldName);

		return remove(deDataDefinitionFieldLink);
	}

	/**
	 * Returns the number of de data definition field links where classNameId = &#63; and classPK = &#63; and ddmStructureId = &#63; and fieldName = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldName the field name
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByC_C_DDMSI_F(
		long classNameId, long classPK, long ddmStructureId, String fieldName) {

		return _uniquePersistenceFinderByC_C_DDMSI_F.count(
			finderCache,
			new Object[] {classNameId, classPK, ddmStructureId, fieldName});
	}

	/**
	 * Returns the number of de data definition field links where classNameId = &#63; and classPK = &#63; and ddmStructureId = &#63; and fieldName = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ddmStructureId the ddm structure ID
	 * @param fieldNames the field names
	 * @return the number of matching de data definition field links
	 */
	@Override
	public int countByC_C_DDMSI_F(
		long classNameId, long classPK, long ddmStructureId,
		String[] fieldNames) {

		if (fieldNames == null) {
			fieldNames = new String[0];
		}
		else if (fieldNames.length > 1) {
			for (int i = 0; i < fieldNames.length; i++) {
				fieldNames[i] = Objects.toString(fieldNames[i], "");
			}

			fieldNames = ArrayUtil.sortedUnique(fieldNames);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DEDataDefinitionFieldLink.class)) {

			Object[] finderArgs = new Object[] {
				classNameId, classPK, ddmStructureId,
				StringUtil.merge(fieldNames)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByC_C_DDMSI_F, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE);

				sb.append(_FINDER_COLUMN_C_C_DDMSI_F_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_DDMSI_F_CLASSPK_2);

				sb.append(_FINDER_COLUMN_C_C_DDMSI_F_DDMSTRUCTUREID_2);

				if (fieldNames.length > 0) {
					sb.append("(");

					for (int i = 0; i < fieldNames.length; i++) {
						String fieldName = fieldNames[i];

						if (fieldName.isEmpty()) {
							sb.append(_FINDER_COLUMN_C_C_DDMSI_F_FIELDNAME_3);
						}
						else {
							sb.append(_FINDER_COLUMN_C_C_DDMSI_F_FIELDNAME_2);
						}

						if ((i + 1) < fieldNames.length) {
							sb.append(WHERE_OR);
						}
					}

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					queryPos.add(ddmStructureId);

					for (String fieldName : fieldNames) {
						if ((fieldName != null) && !fieldName.isEmpty()) {
							queryPos.add(fieldName);
						}
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByC_C_DDMSI_F, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_C_C_DDMSI_F_CLASSNAMEID_2 =
		"deDataDefinitionFieldLink.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_DDMSI_F_CLASSPK_2 =
		"deDataDefinitionFieldLink.classPK = ? AND ";

	private static final String _FINDER_COLUMN_C_C_DDMSI_F_DDMSTRUCTUREID_2 =
		"deDataDefinitionFieldLink.ddmStructureId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_DDMSI_F_FIELDNAME_2 =
		"deDataDefinitionFieldLink.fieldName = ?";

	private static final String _FINDER_COLUMN_C_C_DDMSI_F_FIELDNAME_3 =
		"(deDataDefinitionFieldLink.fieldName IS NULL OR deDataDefinitionFieldLink.fieldName = '')";

	public DEDataDefinitionFieldLinkPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DEDataDefinitionFieldLink.class);

		setModelImplClass(DEDataDefinitionFieldLinkImpl.class);
		setModelPKClass(long.class);

		setTable(DEDataDefinitionFieldLinkTable.INSTANCE);
	}

	/**
	 * Creates a new de data definition field link with the primary key. Does not add the de data definition field link to the database.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key for the new de data definition field link
	 * @return the new de data definition field link
	 */
	@Override
	public DEDataDefinitionFieldLink create(long deDataDefinitionFieldLinkId) {
		DEDataDefinitionFieldLink deDataDefinitionFieldLink =
			new DEDataDefinitionFieldLinkImpl();

		deDataDefinitionFieldLink.setNew(true);
		deDataDefinitionFieldLink.setPrimaryKey(deDataDefinitionFieldLinkId);

		String uuid = PortalUUIDUtil.generate();

		deDataDefinitionFieldLink.setUuid(uuid);

		deDataDefinitionFieldLink.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return deDataDefinitionFieldLink;
	}

	/**
	 * Removes the de data definition field link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link that was removed
	 * @throws NoSuchDataDefinitionFieldLinkException if a de data definition field link with the primary key could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink remove(long deDataDefinitionFieldLinkId)
		throws NoSuchDataDefinitionFieldLinkException {

		return remove((Serializable)deDataDefinitionFieldLinkId);
	}

	@Override
	protected DEDataDefinitionFieldLink removeImpl(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(deDataDefinitionFieldLink)) {
				deDataDefinitionFieldLink =
					(DEDataDefinitionFieldLink)session.get(
						DEDataDefinitionFieldLinkImpl.class,
						deDataDefinitionFieldLink.getPrimaryKeyObj());
			}

			if ((deDataDefinitionFieldLink != null) &&
				ctPersistenceHelper.isRemove(deDataDefinitionFieldLink)) {

				session.delete(deDataDefinitionFieldLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (deDataDefinitionFieldLink != null) {
			clearCache(deDataDefinitionFieldLink);
		}

		return deDataDefinitionFieldLink;
	}

	@Override
	public DEDataDefinitionFieldLink updateImpl(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		boolean isNew = deDataDefinitionFieldLink.isNew();

		if (!(deDataDefinitionFieldLink instanceof
				DEDataDefinitionFieldLinkModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(deDataDefinitionFieldLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					deDataDefinitionFieldLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in deDataDefinitionFieldLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DEDataDefinitionFieldLink implementation " +
					deDataDefinitionFieldLink.getClass());
		}

		DEDataDefinitionFieldLinkModelImpl deDataDefinitionFieldLinkModelImpl =
			(DEDataDefinitionFieldLinkModelImpl)deDataDefinitionFieldLink;

		if (Validator.isNull(deDataDefinitionFieldLink.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			deDataDefinitionFieldLink.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (deDataDefinitionFieldLink.getCreateDate() == null)) {
			if (serviceContext == null) {
				deDataDefinitionFieldLink.setCreateDate(date);
			}
			else {
				deDataDefinitionFieldLink.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!deDataDefinitionFieldLinkModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				deDataDefinitionFieldLink.setModifiedDate(date);
			}
			else {
				deDataDefinitionFieldLink.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(deDataDefinitionFieldLink)) {
				if (!isNew) {
					session.evict(
						DEDataDefinitionFieldLinkImpl.class,
						deDataDefinitionFieldLink.getPrimaryKeyObj());
				}

				session.save(deDataDefinitionFieldLink);
			}
			else {
				deDataDefinitionFieldLink =
					(DEDataDefinitionFieldLink)session.merge(
						deDataDefinitionFieldLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(deDataDefinitionFieldLink, false);

		if (isNew) {
			deDataDefinitionFieldLink.setNew(false);
		}

		deDataDefinitionFieldLink.resetOriginalValues();

		return deDataDefinitionFieldLink;
	}

	/**
	 * Returns the de data definition field link with the primary key or throws a <code>NoSuchDataDefinitionFieldLinkException</code> if it could not be found.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link
	 * @throws NoSuchDataDefinitionFieldLinkException if a de data definition field link with the primary key could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink findByPrimaryKey(
			long deDataDefinitionFieldLinkId)
		throws NoSuchDataDefinitionFieldLinkException {

		return findByPrimaryKey((Serializable)deDataDefinitionFieldLinkId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the de data definition field link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link, or <code>null</code> if a de data definition field link with the primary key could not be found
	 */
	@Override
	public DEDataDefinitionFieldLink fetchByPrimaryKey(
		long deDataDefinitionFieldLinkId) {

		return fetchByPrimaryKey((Serializable)deDataDefinitionFieldLinkId);
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
		return "deDataDefinitionFieldLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DEDATADEFINITIONFIELDLINK;
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
		return DEDataDefinitionFieldLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DEDataDefinitionFieldLink";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("ddmStructureId");
		ctMergeColumnNames.add("fieldName");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("deDataDefinitionFieldLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"classNameId", "classPK", "ddmStructureId", "fieldName"
			});
	}

	/**
	 * Initializes the de data definition field link persistence.
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
			_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE,
			_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE,
			DEDataDefinitionFieldLinkModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				DEDataDefinitionFieldLink::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DEDataDefinitionFieldLink::getUuid),
				DEDataDefinitionFieldLink::getGroupId),
			_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE, "",
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				DEDataDefinitionFieldLink::getUuid),
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, DEDataDefinitionFieldLink::getGroupId));

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
				_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE,
				_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE,
				DEDataDefinitionFieldLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					DEDataDefinitionFieldLink::getUuid),
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					DEDataDefinitionFieldLink::getCompanyId));

		_collectionPersistenceFinderByDDMStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByDDMStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ddmStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByDDMStructureId", new String[] {Long.class.getName()},
					new String[] {"ddmStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByDDMStructureId",
					new String[] {Long.class.getName()},
					new String[] {"ddmStructureId"}, false),
				_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE,
				_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE,
				DEDataDefinitionFieldLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "ddmStructureId",
					FinderColumn.Type.LONG, "=", true, true,
					DEDataDefinitionFieldLink::getDdmStructureId));

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
			_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE,
			_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE,
			DEDataDefinitionFieldLinkModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				DEDataDefinitionFieldLink::getClassNameId),
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, DEDataDefinitionFieldLink::getClassPK));

		_collectionPersistenceFinderByC_DDMSI =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_DDMSI",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "ddmStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_DDMSI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "ddmStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_DDMSI",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "ddmStructureId"}, false),
				_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE,
				_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE,
				DEDataDefinitionFieldLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					DEDataDefinitionFieldLink::getClassNameId),
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "ddmStructureId",
					FinderColumn.Type.LONG, "=", true, true,
					DEDataDefinitionFieldLink::getDdmStructureId));

		_collectionPersistenceFinderByDDMSI_F =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByDDMSI_F",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ddmStructureId", "fieldName"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByDDMSI_F",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"ddmStructureId", "fieldName"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByDDMSI_F",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"ddmStructureId", "fieldName"}, 0, 2, false,
					null),
				_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE,
				_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE,
				DEDataDefinitionFieldLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "ddmStructureId",
					FinderColumn.Type.LONG, "=", true, true,
					DEDataDefinitionFieldLink::getDdmStructureId),
				new ArrayableFinderColumn<>(
					"deDataDefinitionFieldLink.", "fieldName",
					FinderColumn.Type.STRING, "=", false, true, true,
					DEDataDefinitionFieldLink::getFieldName));

		_collectionPersistenceFinderByC_DDMSI_F =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_DDMSI_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "ddmStructureId", "fieldName"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_DDMSI_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"classNameId", "ddmStructureId", "fieldName"},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_DDMSI_F",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName()
					},
					new String[] {"classNameId", "ddmStructureId", "fieldName"},
					0, 4, false, null),
				_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE,
				_SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE,
				DEDataDefinitionFieldLinkModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					DEDataDefinitionFieldLink::getClassNameId),
				new FinderColumn<>(
					"deDataDefinitionFieldLink.", "ddmStructureId",
					FinderColumn.Type.LONG, "=", true, true,
					DEDataDefinitionFieldLink::getDdmStructureId),
				new ArrayableFinderColumn<>(
					"deDataDefinitionFieldLink.", "fieldName",
					FinderColumn.Type.STRING, "=", false, true, true,
					DEDataDefinitionFieldLink::getFieldName));

		_finderPathWithPaginationCountByC_C_DDMSI_F = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C_DDMSI_F",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {
				"classNameId", "classPK", "ddmStructureId", "fieldName"
			},
			false);

		_uniquePersistenceFinderByC_C_DDMSI_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_DDMSI_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), String.class.getName()
				},
				new String[] {
					"classNameId", "classPK", "ddmStructureId", "fieldName"
				},
				0, 8, false, DEDataDefinitionFieldLink::getClassNameId,
				DEDataDefinitionFieldLink::getClassPK,
				DEDataDefinitionFieldLink::getDdmStructureId,
				convertNullFunction(DEDataDefinitionFieldLink::getFieldName)),
			_SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE, "",
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				DEDataDefinitionFieldLink::getClassNameId),
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, DEDataDefinitionFieldLink::getClassPK),
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "ddmStructureId",
				FinderColumn.Type.LONG, "=", true, true,
				DEDataDefinitionFieldLink::getDdmStructureId),
			new FinderColumn<>(
				"deDataDefinitionFieldLink.", "fieldName",
				FinderColumn.Type.STRING, "=", true, true,
				DEDataDefinitionFieldLink::getFieldName));

		DEDataDefinitionFieldLinkUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DEDataDefinitionFieldLinkUtil.setPersistence(null);

		entityCache.removeCache(DEDataDefinitionFieldLinkImpl.class.getName());
	}

	@Override
	@Reference(
		target = DEPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DEPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DEPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DEDataDefinitionFieldLinkModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DEDATADEFINITIONFIELDLINK =
		"SELECT deDataDefinitionFieldLink FROM DEDataDefinitionFieldLink deDataDefinitionFieldLink";

	private static final String _SQL_SELECT_DEDATADEFINITIONFIELDLINK_WHERE =
		"SELECT deDataDefinitionFieldLink FROM DEDataDefinitionFieldLink deDataDefinitionFieldLink WHERE ";

	private static final String _SQL_COUNT_DEDATADEFINITIONFIELDLINK_WHERE =
		"SELECT COUNT(deDataDefinitionFieldLink) FROM DEDataDefinitionFieldLink deDataDefinitionFieldLink WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1141605928