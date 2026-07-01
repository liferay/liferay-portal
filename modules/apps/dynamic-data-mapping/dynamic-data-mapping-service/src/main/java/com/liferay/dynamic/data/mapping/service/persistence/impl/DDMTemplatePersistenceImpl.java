/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.DuplicateDDMTemplateExternalReferenceCodeException;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplatePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the ddm template service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMTemplatePersistence.class)
public class DDMTemplatePersistenceImpl
	extends BasePersistenceImpl<DDMTemplate, NoSuchTemplateException>
	implements DDMTemplatePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMTemplateUtil</code> to access the ddm template persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMTemplateImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the ddm templates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUuid_First(
			String uuid, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUuid_First(
		String uuid, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the ddm templates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of ddm templates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the ddm template where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUUID_G(String uuid, long groupId)
		throws NoSuchTemplateException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the ddm template where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the ddm template where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeByUUID_G(String uuid, long groupId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByUUID_G(uuid, groupId);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the ddm templates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the ddm templates where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of ddm templates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder
		<DDMTemplate, NoSuchTemplateException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByGroupId_First(
			long groupId, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByGroupId_First(
		long groupId, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			groupId);
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			finderCache, new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_collectionPersistenceFinderByClassPK;

	/**
	 * Returns an ordered range of all the ddm templates where classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByClassPK(
		long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByClassPK.find(
			finderCache, new Object[] {classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByClassPK_First(
			long classPK, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByClassPK.findFirst(
			finderCache, new Object[] {classPK}, orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByClassPK_First(
		long classPK, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByClassPK.fetchFirst(
			finderCache, new Object[] {classPK}, orderByComparator);
	}

	/**
	 * Removes all the ddm templates where classPK = &#63; from the database.
	 *
	 * @param classPK the class pk
	 */
	@Override
	public void removeByClassPK(long classPK) {
		_collectionPersistenceFinderByClassPK.remove(
			finderCache, new Object[] {classPK});
	}

	/**
	 * Returns the number of ddm templates where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByClassPK(long classPK) {
		return _collectionPersistenceFinderByClassPK.count(
			finderCache, new Object[] {classPK});
	}

	private CollectionPersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_collectionPersistenceFinderByTemplateKey;

	/**
	 * Returns an ordered range of all the ddm templates where templateKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param templateKey the template key
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByTemplateKey(
		String templateKey, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTemplateKey.find(
			finderCache, new Object[] {templateKey}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByTemplateKey_First(
			String templateKey,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByTemplateKey.findFirst(
			finderCache, new Object[] {templateKey}, orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByTemplateKey_First(
		String templateKey, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByTemplateKey.fetchFirst(
			finderCache, new Object[] {templateKey}, orderByComparator);
	}

	/**
	 * Removes all the ddm templates where templateKey = &#63; from the database.
	 *
	 * @param templateKey the template key
	 */
	@Override
	public void removeByTemplateKey(String templateKey) {
		_collectionPersistenceFinderByTemplateKey.remove(
			finderCache, new Object[] {templateKey});
	}

	/**
	 * Returns the number of ddm templates where templateKey = &#63;.
	 *
	 * @param templateKey the template key
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByTemplateKey(String templateKey) {
		return _collectionPersistenceFinderByTemplateKey.count(
			finderCache, new Object[] {templateKey});
	}

	private CollectionPersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the ddm templates where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByType(
		String type, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			finderCache, new Object[] {type}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByType_First(
			String type, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByType.findFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByType_First(
		String type, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			finderCache, new Object[] {type}, orderByComparator);
	}

	/**
	 * Removes all the ddm templates where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(String type) {
		_collectionPersistenceFinderByType.remove(
			finderCache, new Object[] {type});
	}

	/**
	 * Returns the number of ddm templates where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByType(String type) {
		return _collectionPersistenceFinderByType.count(
			finderCache, new Object[] {type});
	}

	private CollectionPersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_collectionPersistenceFinderByLanguage;

	/**
	 * Returns an ordered range of all the ddm templates where language = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param language the language
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByLanguage(
		String language, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLanguage.find(
			finderCache, new Object[] {language}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where language = &#63;.
	 *
	 * @param language the language
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByLanguage_First(
			String language, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByLanguage.findFirst(
			finderCache, new Object[] {language}, orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where language = &#63;.
	 *
	 * @param language the language
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByLanguage_First(
		String language, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByLanguage.fetchFirst(
			finderCache, new Object[] {language}, orderByComparator);
	}

	/**
	 * Removes all the ddm templates where language = &#63; from the database.
	 *
	 * @param language the language
	 */
	@Override
	public void removeByLanguage(String language) {
		_collectionPersistenceFinderByLanguage.remove(
			finderCache, new Object[] {language});
	}

	/**
	 * Returns the number of ddm templates where language = &#63;.
	 *
	 * @param language the language
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByLanguage(String language) {
		return _collectionPersistenceFinderByLanguage.count(
			finderCache, new Object[] {language});
	}

	private UniquePersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_uniquePersistenceFinderBySmallImageId;

	/**
	 * Returns the ddm template where smallImageId = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param smallImageId the small image ID
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findBySmallImageId(long smallImageId)
		throws NoSuchTemplateException {

		return _uniquePersistenceFinderBySmallImageId.find(
			finderCache, new Object[] {smallImageId});
	}

	/**
	 * Returns the ddm template where smallImageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param smallImageId the small image ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchBySmallImageId(
		long smallImageId, boolean useFinderCache) {

		return _uniquePersistenceFinderBySmallImageId.fetch(
			finderCache, new Object[] {smallImageId}, useFinderCache);
	}

	/**
	 * Removes the ddm template where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeBySmallImageId(long smallImageId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findBySmallImageId(smallImageId);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countBySmallImageId(long smallImageId) {
		return _uniquePersistenceFinderBySmallImageId.count(
			finderCache, new Object[] {smallImageId});
	}

	private FilterCollectionPersistenceFinder
		<DDMTemplate, NoSuchTemplateException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C.filterFind(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.filterCount(
			finderCache, new Object[] {groupId, classNameId}, groupId);
	}

	private FilterCollectionPersistenceFinder
		<DDMTemplate, NoSuchTemplateException>
			_collectionPersistenceFinderByG_CPK;

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long groupId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_CPK.find(
			finderCache, new Object[] {new long[] {groupId}, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_CPK_First(
			long groupId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_CPK_First(
			groupId, classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_CPK_First(
		long groupId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_CPK.fetchFirst(
			finderCache, new Object[] {new long[] {groupId}, classPK},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(
		long groupId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_CPK.filterFind(
			finderCache, new Object[] {new long[] {groupId}, classPK}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permission to view where groupId = any &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_CPK(
		long[] groupIds, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_CPK.filterFind(
			finderCache, new Object[] {groupIds, classPK}, start, end,
			orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_CPK(
		long[] groupIds, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_CPK.find(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_CPK(long groupId, long classPK) {
		_collectionPersistenceFinderByG_CPK.remove(
			finderCache, new Object[] {new long[] {groupId}, classPK});
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_CPK(long groupId, long classPK) {
		return _collectionPersistenceFinderByG_CPK.count(
			finderCache, new Object[] {new long[] {groupId}, classPK});
	}

	/**
	 * Returns the number of ddm templates where groupId = any &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_CPK(long[] groupIds, long classPK) {
		return _collectionPersistenceFinderByG_CPK.count(
			finderCache,
			new Object[] {ArrayUtil.sortedUnique(groupIds), classPK});
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_CPK(long groupId, long classPK) {
		return _collectionPersistenceFinderByG_CPK.filterCount(
			finderCache, new Object[] {new long[] {groupId}, classPK}, groupId);
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = any &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_CPK(long[] groupIds, long classPK) {
		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_CPK.filterCount(
			finderCache, new Object[] {groupIds, classPK}, groupIds);
	}

	private FilterCollectionPersistenceFinder
		<DDMTemplate, NoSuchTemplateException>
			_collectionPersistenceFinderByG_C_C;

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C.find(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = fetchByG_C_C_First(
			groupId, classNameId, classPK, orderByComparator);

		if (ddmTemplate != null) {
			return ddmTemplate;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchTemplateException(sb.toString());
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.fetchFirst(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C.filterFind(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, classPK}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C(
		long[] groupIds, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C.filterFind(
			finderCache, new Object[] {groupIds, classNameId, classPK}, start,
			end, orderByComparator, groupIds);
	}

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C(
		long[] groupIds, long classNameId, long classPK, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C.find(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classPK
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C(long groupId, long classNameId, long classPK) {
		_collectionPersistenceFinderByG_C_C.remove(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, classPK});
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C(long groupId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByG_C_C.count(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, classPK});
	}

	/**
	 * Returns the number of ddm templates where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C(long[] groupIds, long classNameId, long classPK) {
		return _collectionPersistenceFinderByG_C_C.count(
			finderCache,
			new Object[] {
				ArrayUtil.sortedUnique(groupIds), classNameId, classPK
			});
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C(
		long groupId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByG_C_C.filterCount(
			finderCache,
			new Object[] {new long[] {groupId}, classNameId, classPK}, groupId);
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C(
		long[] groupIds, long classNameId, long classPK) {

		groupIds = ArrayUtil.sortedUnique(groupIds);

		return _collectionPersistenceFinderByG_C_C.filterCount(
			finderCache, new Object[] {groupIds, classNameId, classPK},
			groupIds);
	}

	private UniquePersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_uniquePersistenceFinderByG_C_T;

	/**
	 * Returns the ddm template where groupId = &#63; and classNameId = &#63; and templateKey = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_T(
			long groupId, long classNameId, String templateKey)
		throws NoSuchTemplateException {

		return _uniquePersistenceFinderByG_C_T.find(
			finderCache, new Object[] {groupId, classNameId, templateKey});
	}

	/**
	 * Returns the ddm template where groupId = &#63; and classNameId = &#63; and templateKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_T(
		long groupId, long classNameId, String templateKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_T.fetch(
			finderCache, new Object[] {groupId, classNameId, templateKey},
			useFinderCache);
	}

	/**
	 * Removes the ddm template where groupId = &#63; and classNameId = &#63; and templateKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeByG_C_T(
			long groupId, long classNameId, String templateKey)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByG_C_T(
			groupId, classNameId, templateKey);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and templateKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param templateKey the template key
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_T(
		long groupId, long classNameId, String templateKey) {

		return _uniquePersistenceFinderByG_C_T.count(
			finderCache, new Object[] {groupId, classNameId, templateKey});
	}

	private CollectionPersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_collectionPersistenceFinderByC_C_T;

	/**
	 * Returns an ordered range of all the ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByC_C_T(
		long classNameId, long classPK, String type, int start, int end,
		OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T.find(
			finderCache, new Object[] {classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByC_C_T_First(
			long classNameId, long classPK, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByC_C_T.findFirst(
			finderCache, new Object[] {classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByC_C_T_First(
		long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T.fetchFirst(
			finderCache, new Object[] {classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Removes all the ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_T(long classNameId, long classPK, String type) {
		_collectionPersistenceFinderByC_C_T.remove(
			finderCache, new Object[] {classNameId, classPK, type});
	}

	/**
	 * Returns the number of ddm templates where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByC_C_T(long classNameId, long classPK, String type) {
		return _collectionPersistenceFinderByC_C_T.count(
			finderCache, new Object[] {classNameId, classPK, type});
	}

	private FilterCollectionPersistenceFinder
		<DDMTemplate, NoSuchTemplateException>
			_collectionPersistenceFinderByG_C_C_T;

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T(
		long groupId, long classNameId, long classPK, String type, int start,
		int end, OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_T.find(
			finderCache, new Object[] {groupId, classNameId, classPK, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_T_First(
			long groupId, long classNameId, long classPK, String type,
			OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByG_C_C_T.findFirst(
			finderCache, new Object[] {groupId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_T_First(
		long groupId, long classNameId, long classPK, String type,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T.fetchFirst(
			finderCache, new Object[] {groupId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T(
		long groupId, long classNameId, long classPK, String type, int start,
		int end, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T.filterFind(
			finderCache, new Object[] {groupId, classNameId, classPK, type},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		_collectionPersistenceFinderByG_C_C_T.remove(
			finderCache, new Object[] {groupId, classNameId, classPK, type});
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		return _collectionPersistenceFinderByG_C_C_T.count(
			finderCache, new Object[] {groupId, classNameId, classPK, type});
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T(
		long groupId, long classNameId, long classPK, String type) {

		return _collectionPersistenceFinderByG_C_C_T.filterCount(
			finderCache, new Object[] {groupId, classNameId, classPK, type},
			groupId);
	}

	private FilterCollectionPersistenceFinder
		<DDMTemplate, NoSuchTemplateException>
			_collectionPersistenceFinderByG_C_C_T_M;

	/**
	 * Returns an ordered range of all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm templates
	 */
	@Override
	public List<DDMTemplate> findByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type, String mode,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_T_M.find(
			finderCache,
			new Object[] {groupId, classNameId, classPK, type, mode}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByG_C_C_T_M_First(
			long groupId, long classNameId, long classPK, String type,
			String mode, OrderByComparator<DDMTemplate> orderByComparator)
		throws NoSuchTemplateException {

		return _collectionPersistenceFinderByG_C_C_T_M.findFirst(
			finderCache,
			new Object[] {groupId, classNameId, classPK, type, mode},
			orderByComparator);
	}

	/**
	 * Returns the first ddm template in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByG_C_C_T_M_First(
		long groupId, long classNameId, long classPK, String type, String mode,
		OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T_M.fetchFirst(
			finderCache,
			new Object[] {groupId, classNameId, classPK, type, mode},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm templates that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTemplateModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @param start the lower bound of the range of ddm templates
	 * @param end the upper bound of the range of ddm templates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm templates that the user has permission to view
	 */
	@Override
	public List<DDMTemplate> filterFindByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type, String mode,
		int start, int end, OrderByComparator<DDMTemplate> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_T_M.filterFind(
			finderCache,
			new Object[] {groupId, classNameId, classPK, type, mode}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 */
	@Override
	public void removeByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		_collectionPersistenceFinderByG_C_C_T_M.remove(
			finderCache,
			new Object[] {groupId, classNameId, classPK, type, mode});
	}

	/**
	 * Returns the number of ddm templates where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		return _collectionPersistenceFinderByG_C_C_T_M.count(
			finderCache,
			new Object[] {groupId, classNameId, classPK, type, mode});
	}

	/**
	 * Returns the number of ddm templates that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and mode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param mode the mode
	 * @return the number of matching ddm templates that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_C_T_M(
		long groupId, long classNameId, long classPK, String type,
		String mode) {

		return _collectionPersistenceFinderByG_C_C_T_M.filterCount(
			finderCache,
			new Object[] {groupId, classNameId, classPK, type, mode}, groupId);
	}

	private UniquePersistenceFinder<DDMTemplate, NoSuchTemplateException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the ddm template where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching ddm template
	 * @throws NoSuchTemplateException if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchTemplateException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the ddm template where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm template, or <code>null</code> if a matching ddm template could not be found
	 */
	@Override
	public DDMTemplate fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the ddm template where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the ddm template that was removed
	 */
	@Override
	public DDMTemplate removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchTemplateException {

		DDMTemplate ddmTemplate = findByERC_G(externalReferenceCode, groupId);

		return remove(ddmTemplate);
	}

	/**
	 * Returns the number of ddm templates where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching ddm templates
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public DDMTemplatePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("mode", "mode_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMTemplate.class);

		setModelImplClass(DDMTemplateImpl.class);
		setModelPKClass(long.class);

		setTable(DDMTemplateTable.INSTANCE);
	}

	/**
	 * Creates a new ddm template with the primary key. Does not add the ddm template to the database.
	 *
	 * @param templateId the primary key for the new ddm template
	 * @return the new ddm template
	 */
	@Override
	public DDMTemplate create(long templateId) {
		DDMTemplate ddmTemplate = new DDMTemplateImpl();

		ddmTemplate.setNew(true);
		ddmTemplate.setPrimaryKey(templateId);

		String uuid = PortalUUIDUtil.generate();

		ddmTemplate.setUuid(uuid);

		ddmTemplate.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmTemplate;
	}

	/**
	 * Removes the ddm template with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template that was removed
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate remove(long templateId) throws NoSuchTemplateException {
		return remove((Serializable)templateId);
	}

	@Override
	protected DDMTemplate removeImpl(DDMTemplate ddmTemplate) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmTemplate)) {
				ddmTemplate = (DDMTemplate)session.get(
					DDMTemplateImpl.class, ddmTemplate.getPrimaryKeyObj());
			}

			if ((ddmTemplate != null) &&
				ctPersistenceHelper.isRemove(ddmTemplate)) {

				session.delete(ddmTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmTemplate != null) {
			clearCache(ddmTemplate);
		}

		return ddmTemplate;
	}

	@Override
	public DDMTemplate updateImpl(DDMTemplate ddmTemplate) {
		boolean isNew = ddmTemplate.isNew();

		if (!(ddmTemplate instanceof DDMTemplateModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmTemplate.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ddmTemplate);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmTemplate proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMTemplate implementation " +
					ddmTemplate.getClass());
		}

		DDMTemplateModelImpl ddmTemplateModelImpl =
			(DDMTemplateModelImpl)ddmTemplate;

		if (Validator.isNull(ddmTemplate.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmTemplate.setUuid(uuid);
		}

		if (Validator.isNull(ddmTemplate.getExternalReferenceCode())) {
			ddmTemplate.setExternalReferenceCode(ddmTemplate.getUuid());
		}
		else {
			if (!Objects.equals(
					ddmTemplateModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ddmTemplate.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ddmTemplate.getCompanyId();

					long groupId = ddmTemplate.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ddmTemplate.getPrimaryKey();
					}

					try {
						ddmTemplate.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DDMTemplate.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ddmTemplate.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DDMTemplate ercDDMTemplate = fetchByERC_G(
				ddmTemplate.getExternalReferenceCode(),
				ddmTemplate.getGroupId());

			if (isNew) {
				if (ercDDMTemplate != null) {
					throw new DuplicateDDMTemplateExternalReferenceCodeException(
						"Duplicate ddm template with external reference code " +
							ddmTemplate.getExternalReferenceCode() +
								" and group " + ddmTemplate.getGroupId());
				}
			}
			else {
				if ((ercDDMTemplate != null) &&
					(ddmTemplate.getTemplateId() !=
						ercDDMTemplate.getTemplateId())) {

					throw new DuplicateDDMTemplateExternalReferenceCodeException(
						"Duplicate ddm template with external reference code " +
							ddmTemplate.getExternalReferenceCode() +
								" and group " + ddmTemplate.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddmTemplate.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmTemplate.setCreateDate(date);
			}
			else {
				ddmTemplate.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ddmTemplateModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmTemplate.setModifiedDate(date);
			}
			else {
				ddmTemplate.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmTemplate)) {
				if (!isNew) {
					session.evict(
						DDMTemplateImpl.class, ddmTemplate.getPrimaryKeyObj());
				}

				session.save(ddmTemplate);
			}
			else {
				ddmTemplate = (DDMTemplate)session.merge(ddmTemplate);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmTemplate, false);

		if (isNew) {
			ddmTemplate.setNew(false);
		}

		ddmTemplate.resetOriginalValues();

		return ddmTemplate;
	}

	/**
	 * Returns the ddm template with the primary key or throws a <code>NoSuchTemplateException</code> if it could not be found.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template
	 * @throws NoSuchTemplateException if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate findByPrimaryKey(long templateId)
		throws NoSuchTemplateException {

		return findByPrimaryKey((Serializable)templateId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm template with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param templateId the primary key of the ddm template
	 * @return the ddm template, or <code>null</code> if a ddm template with the primary key could not be found
	 */
	@Override
	public DDMTemplate fetchByPrimaryKey(long templateId) {
		return fetchByPrimaryKey((Serializable)templateId);
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
		return "templateId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMTEMPLATE;
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
		return DDMTemplateModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMTemplate";
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
		ctMergeColumnNames.add("versionUserId");
		ctMergeColumnNames.add("versionUserName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("resourceClassNameId");
		ctMergeColumnNames.add("templateKey");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("mode_");
		ctMergeColumnNames.add("language");
		ctMergeColumnNames.add("script");
		ctMergeColumnNames.add("cacheable");
		ctMergeColumnNames.add("smallImage");
		ctMergeColumnNames.add("smallImageId");
		ctMergeColumnNames.add("smallImageURL");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("templateId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "templateKey"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the ddm template persistence.
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
			_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
			DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ddmTemplate.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DDMTemplate::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(DDMTemplate::getUuid),
				DDMTemplate::getGroupId),
			_SQL_SELECT_DDMTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"ddmTemplate.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, DDMTemplate::getUuid),
			new FinderColumn<>(
				"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DDMTemplate::getGroupId));

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
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, DDMTemplate::getUuid),
				new FinderColumn<>(
					"ddmTemplate.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
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
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getGroupId));

		_collectionPersistenceFinderByClassPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByClassPK",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByClassPK",
					new String[] {Long.class.getName()},
					new String[] {"classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByClassPK",
					new String[] {Long.class.getName()},
					new String[] {"classPK"}, false),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassPK));

		_collectionPersistenceFinderByTemplateKey =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTemplateKey",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"templateKey"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByTemplateKey", new String[] {String.class.getName()},
					new String[] {"templateKey"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByTemplateKey", new String[] {String.class.getName()},
					new String[] {"templateKey"}, 0, 1, false, null),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "templateKey", FinderColumn.Type.STRING,
					"=", true, true, DDMTemplate::getTemplateKey));

		_collectionPersistenceFinderByType = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, false, null),
			_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
			DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ddmTemplate.", "type", "type_", FinderColumn.Type.STRING, "=",
				true, true, DDMTemplate::getType));

		_collectionPersistenceFinderByLanguage =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLanguage",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"language"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByLanguage",
					new String[] {String.class.getName()},
					new String[] {"language"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLanguage", new String[] {String.class.getName()},
					new String[] {"language"}, 0, 1, false, null),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "language", FinderColumn.Type.STRING, "=",
					true, true, DDMTemplate::getLanguage));

		_uniquePersistenceFinderBySmallImageId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchBySmallImageId",
				new String[] {Long.class.getName()},
				new String[] {"smallImageId"}, 0, 0, false,
				DDMTemplate::getSmallImageId),
			_SQL_SELECT_DDMTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"ddmTemplate.", "smallImageId", FinderColumn.Type.LONG, "=",
				true, true, DDMTemplate::getSmallImageId));

		_collectionPersistenceFinderByG_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "classNameId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "classNameId"}, false),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getGroupId),
				new FinderColumn<>(
					"ddmTemplate.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassNameId));

		_collectionPersistenceFinderByG_CPK =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "classPK"}, false),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, DDMTemplate::getGroupId),
				new FinderColumn<>(
					"ddmTemplate.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassPK));

		_collectionPersistenceFinderByG_C_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK"}, false),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new ArrayableFinderColumn<>(
					"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=",
					false, true, true, DDMTemplate::getGroupId),
				new FinderColumn<>(
					"ddmTemplate.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassNameId),
				new FinderColumn<>(
					"ddmTemplate.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassPK));

		_uniquePersistenceFinderByG_C_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "classNameId", "templateKey"}, 0, 4,
				false, DDMTemplate::getGroupId, DDMTemplate::getClassNameId,
				convertNullFunction(DDMTemplate::getTemplateKey)),
			_SQL_SELECT_DDMTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DDMTemplate::getGroupId),
			new FinderColumn<>(
				"ddmTemplate.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMTemplate::getClassNameId),
			new FinderColumn<>(
				"ddmTemplate.", "templateKey", FinderColumn.Type.STRING, "=",
				true, true, DDMTemplate::getTemplateKey));

		_collectionPersistenceFinderByC_C_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, 0, 4, false,
				null),
			_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
			DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ddmTemplate.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, DDMTemplate::getClassNameId),
			new FinderColumn<>(
				"ddmTemplate.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, DDMTemplate::getClassPK),
			new FinderColumn<>(
				"ddmTemplate.", "type", "type_", FinderColumn.Type.STRING, "=",
				true, true, DDMTemplate::getType));

		_collectionPersistenceFinderByG_C_C_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK", "type_"},
					0, 8, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName()
					},
					new String[] {"groupId", "classNameId", "classPK", "type_"},
					0, 8, false, null),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getGroupId),
				new FinderColumn<>(
					"ddmTemplate.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassNameId),
				new FinderColumn<>(
					"ddmTemplate.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassPK),
				new FinderColumn<>(
					"ddmTemplate.", "type", "type_", FinderColumn.Type.STRING,
					"=", true, true, DDMTemplate::getType));

		_collectionPersistenceFinderByG_C_C_T_M =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_T_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "type_", "mode_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_C_C_T_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "type_", "mode_"
					},
					0, 24, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_C_C_T_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), String.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "type_", "mode_"
					},
					0, 24, false, null),
				_SQL_SELECT_DDMTEMPLATE_WHERE, _SQL_COUNT_DDMTEMPLATE_WHERE,
				DDMTemplateModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getGroupId),
				new FinderColumn<>(
					"ddmTemplate.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassNameId),
				new FinderColumn<>(
					"ddmTemplate.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, DDMTemplate::getClassPK),
				new FinderColumn<>(
					"ddmTemplate.", "type", "type_", FinderColumn.Type.STRING,
					"=", true, true, DDMTemplate::getType),
				new FinderColumn<>(
					"ddmTemplate.", "mode", "mode_", FinderColumn.Type.STRING,
					"=", true, true, DDMTemplate::getMode));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(DDMTemplate::getExternalReferenceCode),
				DDMTemplate::getGroupId),
			_SQL_SELECT_DDMTEMPLATE_WHERE, "",
			new FinderColumn<>(
				"ddmTemplate.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				DDMTemplate::getExternalReferenceCode),
			new FinderColumn<>(
				"ddmTemplate.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, DDMTemplate::getGroupId));

		DDMTemplateUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMTemplateUtil.setPersistence(null);

		entityCache.removeCache(DDMTemplateImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDMTemplateModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMTEMPLATE =
		"SELECT ddmTemplate FROM DDMTemplate ddmTemplate";

	private static final String _SQL_SELECT_DDMTEMPLATE_WHERE =
		"SELECT ddmTemplate FROM DDMTemplate ddmTemplate WHERE ";

	private static final String _SQL_COUNT_DDMTEMPLATE_WHERE =
		"SELECT COUNT(ddmTemplate) FROM DDMTemplate ddmTemplate WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMTemplate exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMTemplatePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type", "mode"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1777656749