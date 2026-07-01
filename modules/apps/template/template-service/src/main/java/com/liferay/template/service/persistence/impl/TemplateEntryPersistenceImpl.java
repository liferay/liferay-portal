/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.service.persistence.impl;

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
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.template.exception.DuplicateTemplateEntryExternalReferenceCodeException;
import com.liferay.template.exception.NoSuchTemplateEntryException;
import com.liferay.template.model.TemplateEntry;
import com.liferay.template.model.TemplateEntryTable;
import com.liferay.template.model.impl.TemplateEntryImpl;
import com.liferay.template.model.impl.TemplateEntryModelImpl;
import com.liferay.template.service.persistence.TemplateEntryPersistence;
import com.liferay.template.service.persistence.TemplateEntryUtil;
import com.liferay.template.service.persistence.impl.constants.TemplatePersistenceConstants;

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
 * The persistence implementation for the template entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = TemplateEntryPersistence.class)
public class TemplateEntryPersistenceImpl
	extends BasePersistenceImpl<TemplateEntry, NoSuchTemplateEntryException>
	implements TemplateEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TemplateEntryUtil</code> to access the template entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TemplateEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<TemplateEntry, NoSuchTemplateEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	@Override
	public List<TemplateEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByUuid_First(
			String uuid, OrderByComparator<TemplateEntry> orderByComparator)
		throws NoSuchTemplateEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByUuid_First(
		String uuid, OrderByComparator<TemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the template entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching template entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<TemplateEntry, NoSuchTemplateEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the template entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchTemplateEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the template entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the template entry that was removed
	 */
	@Override
	public TemplateEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchTemplateEntryException {

		TemplateEntry templateEntry = findByUUID_G(uuid, groupId);

		return remove(templateEntry);
	}

	/**
	 * Returns the number of template entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching template entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<TemplateEntry, NoSuchTemplateEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	@Override
	public List<TemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<TemplateEntry> orderByComparator)
		throws NoSuchTemplateEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the template entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching template entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<TemplateEntry, NoSuchTemplateEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	@Override
	public List<TemplateEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {new long[] {groupId}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByGroupId_First(
			long groupId, OrderByComparator<TemplateEntry> orderByComparator)
		throws NoSuchTemplateEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByGroupId_First(
		long groupId, OrderByComparator<TemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	@Override
	public List<TemplateEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the template entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching template entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {new long[] {groupId}});
	}

	/**
	 * Returns the number of template entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching template entries
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {ArrayUtil.sortedUnique(groupIds)});
	}

	private UniquePersistenceFinder<TemplateEntry, NoSuchTemplateEntryException>
		_uniquePersistenceFinderByDDMTemplateId;

	/**
	 * Returns the template entry where ddmTemplateId = &#63; or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @return the matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByDDMTemplateId(long ddmTemplateId)
		throws NoSuchTemplateEntryException {

		return _uniquePersistenceFinderByDDMTemplateId.find(
			finderCache, new Object[] {ddmTemplateId});
	}

	/**
	 * Returns the template entry where ddmTemplateId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByDDMTemplateId(
		long ddmTemplateId, boolean useFinderCache) {

		return _uniquePersistenceFinderByDDMTemplateId.fetch(
			finderCache, new Object[] {ddmTemplateId}, useFinderCache);
	}

	/**
	 * Removes the template entry where ddmTemplateId = &#63; from the database.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @return the template entry that was removed
	 */
	@Override
	public TemplateEntry removeByDDMTemplateId(long ddmTemplateId)
		throws NoSuchTemplateEntryException {

		TemplateEntry templateEntry = findByDDMTemplateId(ddmTemplateId);

		return remove(templateEntry);
	}

	/**
	 * Returns the number of template entries where ddmTemplateId = &#63;.
	 *
	 * @param ddmTemplateId the ddm template ID
	 * @return the number of matching template entries
	 */
	@Override
	public int countByDDMTemplateId(long ddmTemplateId) {
		return _uniquePersistenceFinderByDDMTemplateId.count(
			finderCache, new Object[] {ddmTemplateId});
	}

	private CollectionPersistenceFinder
		<TemplateEntry, NoSuchTemplateEntryException>
			_collectionPersistenceFinderByG_IICN;

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63; and infoItemClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	@Override
	public List<TemplateEntry> findByG_IICN(
		long groupId, String infoItemClassName, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_IICN.find(
			finderCache, new Object[] {groupId, infoItemClassName}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63; and infoItemClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByG_IICN_First(
			long groupId, String infoItemClassName,
			OrderByComparator<TemplateEntry> orderByComparator)
		throws NoSuchTemplateEntryException {

		return _collectionPersistenceFinderByG_IICN.findFirst(
			finderCache, new Object[] {groupId, infoItemClassName},
			orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63; and infoItemClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByG_IICN_First(
		long groupId, String infoItemClassName,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_IICN.fetchFirst(
			finderCache, new Object[] {groupId, infoItemClassName},
			orderByComparator);
	}

	/**
	 * Removes all the template entries where groupId = &#63; and infoItemClassName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 */
	@Override
	public void removeByG_IICN(long groupId, String infoItemClassName) {
		_collectionPersistenceFinderByG_IICN.remove(
			finderCache, new Object[] {groupId, infoItemClassName});
	}

	/**
	 * Returns the number of template entries where groupId = &#63; and infoItemClassName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @return the number of matching template entries
	 */
	@Override
	public int countByG_IICN(long groupId, String infoItemClassName) {
		return _collectionPersistenceFinderByG_IICN.count(
			finderCache, new Object[] {groupId, infoItemClassName});
	}

	private CollectionPersistenceFinder
		<TemplateEntry, NoSuchTemplateEntryException>
			_collectionPersistenceFinderByG_IICN_IIFVK;

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	@Override
	public List<TemplateEntry> findByG_IICN_IIFVK(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		int start, int end, OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_IICN_IIFVK.find(
			finderCache,
			new Object[] {
				new long[] {groupId}, infoItemClassName,
				infoItemFormVariationKey
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByG_IICN_IIFVK_First(
			long groupId, String infoItemClassName,
			String infoItemFormVariationKey,
			OrderByComparator<TemplateEntry> orderByComparator)
		throws NoSuchTemplateEntryException {

		return _collectionPersistenceFinderByG_IICN_IIFVK.findFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, infoItemClassName,
				infoItemFormVariationKey
			},
			orderByComparator);
	}

	/**
	 * Returns the first template entry in the ordered set where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByG_IICN_IIFVK_First(
		long groupId, String infoItemClassName, String infoItemFormVariationKey,
		OrderByComparator<TemplateEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_IICN_IIFVK.fetchFirst(
			finderCache,
			new Object[] {
				new long[] {groupId}, infoItemClassName,
				infoItemFormVariationKey
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @param start the lower bound of the range of template entries
	 * @param end the upper bound of the range of template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching template entries
	 */
	@Override
	public List<TemplateEntry> findByG_IICN_IIFVK(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey, int start, int end,
		OrderByComparator<TemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_IICN_IIFVK.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), infoItemClassName,
				infoItemFormVariationKey
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 */
	@Override
	public void removeByG_IICN_IIFVK(
		long groupId, String infoItemClassName,
		String infoItemFormVariationKey) {

		_collectionPersistenceFinderByG_IICN_IIFVK.remove(
			finderCache,
			new Object[] {
				new long[] {groupId}, infoItemClassName,
				infoItemFormVariationKey
			});
	}

	/**
	 * Returns the number of template entries where groupId = &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @return the number of matching template entries
	 */
	@Override
	public int countByG_IICN_IIFVK(
		long groupId, String infoItemClassName,
		String infoItemFormVariationKey) {

		return _collectionPersistenceFinderByG_IICN_IIFVK.count(
			finderCache,
			new Object[] {
				new long[] {groupId}, infoItemClassName,
				infoItemFormVariationKey
			});
	}

	/**
	 * Returns the number of template entries where groupId = any &#63; and infoItemClassName = &#63; and infoItemFormVariationKey = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param infoItemClassName the info item class name
	 * @param infoItemFormVariationKey the info item form variation key
	 * @return the number of matching template entries
	 */
	@Override
	public int countByG_IICN_IIFVK(
		long[] groupIds, String infoItemClassName,
		String infoItemFormVariationKey) {

		return _collectionPersistenceFinderByG_IICN_IIFVK.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), infoItemClassName,
				infoItemFormVariationKey
			});
	}

	private UniquePersistenceFinder<TemplateEntry, NoSuchTemplateEntryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the template entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching template entry
	 * @throws NoSuchTemplateEntryException if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchTemplateEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching template entry, or <code>null</code> if a matching template entry could not be found
	 */
	@Override
	public TemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the template entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the template entry that was removed
	 */
	@Override
	public TemplateEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchTemplateEntryException {

		TemplateEntry templateEntry = findByERC_G(
			externalReferenceCode, groupId);

		return remove(templateEntry);
	}

	/**
	 * Returns the number of template entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching template entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public TemplateEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(TemplateEntry.class);

		setModelImplClass(TemplateEntryImpl.class);
		setModelPKClass(long.class);

		setTable(TemplateEntryTable.INSTANCE);
	}

	/**
	 * Creates a new template entry with the primary key. Does not add the template entry to the database.
	 *
	 * @param templateEntryId the primary key for the new template entry
	 * @return the new template entry
	 */
	@Override
	public TemplateEntry create(long templateEntryId) {
		TemplateEntry templateEntry = new TemplateEntryImpl();

		templateEntry.setNew(true);
		templateEntry.setPrimaryKey(templateEntryId);

		String uuid = PortalUUIDUtil.generate();

		templateEntry.setUuid(uuid);

		templateEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return templateEntry;
	}

	/**
	 * Removes the template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry that was removed
	 * @throws NoSuchTemplateEntryException if a template entry with the primary key could not be found
	 */
	@Override
	public TemplateEntry remove(long templateEntryId)
		throws NoSuchTemplateEntryException {

		return remove((Serializable)templateEntryId);
	}

	@Override
	protected TemplateEntry removeImpl(TemplateEntry templateEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(templateEntry)) {
				templateEntry = (TemplateEntry)session.get(
					TemplateEntryImpl.class, templateEntry.getPrimaryKeyObj());
			}

			if ((templateEntry != null) &&
				ctPersistenceHelper.isRemove(templateEntry)) {

				session.delete(templateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (templateEntry != null) {
			clearCache(templateEntry);
		}

		return templateEntry;
	}

	@Override
	public TemplateEntry updateImpl(TemplateEntry templateEntry) {
		boolean isNew = templateEntry.isNew();

		if (!(templateEntry instanceof TemplateEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(templateEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					templateEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in templateEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TemplateEntry implementation " +
					templateEntry.getClass());
		}

		TemplateEntryModelImpl templateEntryModelImpl =
			(TemplateEntryModelImpl)templateEntry;

		if (Validator.isNull(templateEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			templateEntry.setUuid(uuid);
		}

		if (Validator.isNull(templateEntry.getExternalReferenceCode())) {
			templateEntry.setExternalReferenceCode(templateEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					templateEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					templateEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = templateEntry.getCompanyId();

					long groupId = templateEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = templateEntry.getPrimaryKey();
					}

					try {
						templateEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								TemplateEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								templateEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			TemplateEntry ercTemplateEntry = fetchByERC_G(
				templateEntry.getExternalReferenceCode(),
				templateEntry.getGroupId());

			if (isNew) {
				if (ercTemplateEntry != null) {
					throw new DuplicateTemplateEntryExternalReferenceCodeException(
						"Duplicate template entry with external reference code " +
							templateEntry.getExternalReferenceCode() +
								" and group " + templateEntry.getGroupId());
				}
			}
			else {
				if ((ercTemplateEntry != null) &&
					(templateEntry.getTemplateEntryId() !=
						ercTemplateEntry.getTemplateEntryId())) {

					throw new DuplicateTemplateEntryExternalReferenceCodeException(
						"Duplicate template entry with external reference code " +
							templateEntry.getExternalReferenceCode() +
								" and group " + templateEntry.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (templateEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				templateEntry.setCreateDate(date);
			}
			else {
				templateEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!templateEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				templateEntry.setModifiedDate(date);
			}
			else {
				templateEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(templateEntry)) {
				if (!isNew) {
					session.evict(
						TemplateEntryImpl.class,
						templateEntry.getPrimaryKeyObj());
				}

				session.save(templateEntry);
			}
			else {
				templateEntry = (TemplateEntry)session.merge(templateEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(templateEntry, false);

		if (isNew) {
			templateEntry.setNew(false);
		}

		templateEntry.resetOriginalValues();

		return templateEntry;
	}

	/**
	 * Returns the template entry with the primary key or throws a <code>NoSuchTemplateEntryException</code> if it could not be found.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry
	 * @throws NoSuchTemplateEntryException if a template entry with the primary key could not be found
	 */
	@Override
	public TemplateEntry findByPrimaryKey(long templateEntryId)
		throws NoSuchTemplateEntryException {

		return findByPrimaryKey((Serializable)templateEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the template entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param templateEntryId the primary key of the template entry
	 * @return the template entry, or <code>null</code> if a template entry with the primary key could not be found
	 */
	@Override
	public TemplateEntry fetchByPrimaryKey(long templateEntryId) {
		return fetchByPrimaryKey((Serializable)templateEntryId);
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
		return "templateEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TEMPLATEENTRY;
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
		return TemplateEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "TemplateEntry";
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
		ctMergeColumnNames.add("ddmTemplateId");
		ctMergeColumnNames.add("infoItemClassName");
		ctMergeColumnNames.add("infoItemFormVariationKey");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("templateEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the template entry persistence.
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
			_SQL_SELECT_TEMPLATEENTRY_WHERE, _SQL_COUNT_TEMPLATEENTRY_WHERE,
			TemplateEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"templateEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, TemplateEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(TemplateEntry::getUuid),
				TemplateEntry::getGroupId),
			_SQL_SELECT_TEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"templateEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, TemplateEntry::getUuid),
			new FinderColumn<>(
				"templateEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, TemplateEntry::getGroupId));

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
				_SQL_SELECT_TEMPLATEENTRY_WHERE, _SQL_COUNT_TEMPLATEENTRY_WHERE,
				TemplateEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"templateEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, TemplateEntry::getUuid),
				new FinderColumn<>(
					"templateEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, TemplateEntry::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_TEMPLATEENTRY_WHERE, _SQL_COUNT_TEMPLATEENTRY_WHERE,
				TemplateEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"templateEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, TemplateEntry::getGroupId));

		_uniquePersistenceFinderByDDMTemplateId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByDDMTemplateId",
				new String[] {Long.class.getName()},
				new String[] {"ddmTemplateId"}, 0, 0, false,
				TemplateEntry::getDDMTemplateId),
			_SQL_SELECT_TEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"templateEntry.", "ddmTemplateId", FinderColumn.Type.LONG, "=",
				true, true, TemplateEntry::getDDMTemplateId));

		_collectionPersistenceFinderByG_IICN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_IICN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "infoItemClassName"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_IICN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "infoItemClassName"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_IICN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "infoItemClassName"}, 0, 2, false,
					null),
				_SQL_SELECT_TEMPLATEENTRY_WHERE, _SQL_COUNT_TEMPLATEENTRY_WHERE,
				TemplateEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"templateEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, TemplateEntry::getGroupId),
				new FinderColumn<>(
					"templateEntry.", "infoItemClassName",
					FinderColumn.Type.STRING, "=", true, true,
					TemplateEntry::getInfoItemClassName));

		_collectionPersistenceFinderByG_IICN_IIFVK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByG_IICN_IIFVK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "infoItemClassName",
						"infoItemFormVariationKey"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_IICN_IIFVK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "infoItemClassName",
						"infoItemFormVariationKey"
					},
					0, 6, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByG_IICN_IIFVK",
					new String[] {
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "infoItemClassName",
						"infoItemFormVariationKey"
					},
					0, 6, false, null),
				_SQL_SELECT_TEMPLATEENTRY_WHERE, _SQL_COUNT_TEMPLATEENTRY_WHERE,
				TemplateEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new ArrayableFinderColumn<>(
					"templateEntry.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, TemplateEntry::getGroupId),
				new FinderColumn<>(
					"templateEntry.", "infoItemClassName",
					FinderColumn.Type.STRING, "=", true, true,
					TemplateEntry::getInfoItemClassName),
				new FinderColumn<>(
					"templateEntry.", "infoItemFormVariationKey",
					FinderColumn.Type.STRING, "=", true, true,
					TemplateEntry::getInfoItemFormVariationKey));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(TemplateEntry::getExternalReferenceCode),
				TemplateEntry::getGroupId),
			_SQL_SELECT_TEMPLATEENTRY_WHERE, "",
			new FinderColumn<>(
				"templateEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				TemplateEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"templateEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, TemplateEntry::getGroupId));

		TemplateEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		TemplateEntryUtil.setPersistence(null);

		entityCache.removeCache(TemplateEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = TemplatePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = TemplatePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = TemplatePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		TemplateEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_TEMPLATEENTRY =
		"SELECT templateEntry FROM TemplateEntry templateEntry";

	private static final String _SQL_SELECT_TEMPLATEENTRY_WHERE =
		"SELECT templateEntry FROM TemplateEntry templateEntry WHERE ";

	private static final String _SQL_COUNT_TEMPLATEENTRY_WHERE =
		"SELECT COUNT(templateEntry) FROM TemplateEntry templateEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-670487924