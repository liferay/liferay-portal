/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service.persistence.impl;

import com.liferay.analytics.message.storage.exception.NoSuchAssociationException;
import com.liferay.analytics.message.storage.model.AnalyticsAssociation;
import com.liferay.analytics.message.storage.model.AnalyticsAssociationTable;
import com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationImpl;
import com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl;
import com.liferay.analytics.message.storage.service.persistence.AnalyticsAssociationPersistence;
import com.liferay.analytics.message.storage.service.persistence.AnalyticsAssociationUtil;
import com.liferay.analytics.message.storage.service.persistence.impl.constants.AnalyticsPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
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
 * The persistence implementation for the analytics association service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AnalyticsAssociationPersistence.class)
public class AnalyticsAssociationPersistenceImpl
	extends BasePersistenceImpl
		<AnalyticsAssociation, NoSuchAssociationException>
	implements AnalyticsAssociationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AnalyticsAssociationUtil</code> to access the analytics association persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AnalyticsAssociationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AnalyticsAssociation, NoSuchAssociationException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation findByCompanyId_First(
			long companyId,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws NoSuchAssociationException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation fetchByCompanyId_First(
		long companyId,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching analytics associations
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<AnalyticsAssociation, NoSuchAssociationException>
			_collectionPersistenceFinderByC_LtM;

	/**
	 * Returns all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate) {

		return findByC_LtM(
			companyId, modifiedDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end) {

		return findByC_LtM(companyId, modifiedDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return findByC_LtM(
			companyId, modifiedDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_LtM(
		long companyId, Date modifiedDate, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LtM.find(
			finderCache, new Object[] {companyId, modifiedDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation findByC_LtM_First(
			long companyId, Date modifiedDate,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws NoSuchAssociationException {

		return _collectionPersistenceFinderByC_LtM.findFirst(
			finderCache, new Object[] {companyId, modifiedDate},
			orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation fetchByC_LtM_First(
		long companyId, Date modifiedDate,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return _collectionPersistenceFinderByC_LtM.fetchFirst(
			finderCache, new Object[] {companyId, modifiedDate},
			orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and modifiedDate &lt; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 */
	@Override
	public void removeByC_LtM(long companyId, Date modifiedDate) {
		_collectionPersistenceFinderByC_LtM.remove(
			finderCache, new Object[] {companyId, modifiedDate});
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and modifiedDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @return the number of matching analytics associations
	 */
	@Override
	public int countByC_LtM(long companyId, Date modifiedDate) {
		return _collectionPersistenceFinderByC_LtM.count(
			finderCache, new Object[] {companyId, modifiedDate});
	}

	private CollectionPersistenceFinder
		<AnalyticsAssociation, NoSuchAssociationException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_A(
		long companyId, String associationClassName, int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			finderCache, new Object[] {companyId, associationClassName}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation findByC_A_First(
			long companyId, String associationClassName,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws NoSuchAssociationException {

		return _collectionPersistenceFinderByC_A.findFirst(
			finderCache, new Object[] {companyId, associationClassName},
			orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation fetchByC_A_First(
		long companyId, String associationClassName,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			finderCache, new Object[] {companyId, associationClassName},
			orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and associationClassName = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 */
	@Override
	public void removeByC_A(long companyId, String associationClassName) {
		_collectionPersistenceFinderByC_A.remove(
			finderCache, new Object[] {companyId, associationClassName});
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @return the number of matching analytics associations
	 */
	@Override
	public int countByC_A(long companyId, String associationClassName) {
		return _collectionPersistenceFinderByC_A.count(
			finderCache, new Object[] {companyId, associationClassName});
	}

	private CollectionPersistenceFinder
		<AnalyticsAssociation, NoSuchAssociationException>
			_collectionPersistenceFinderByC_GtM_A;

	/**
	 * Returns all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @return the matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName) {

		return findByC_GtM_A(
			companyId, modifiedDate, associationClassName, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName,
		int start, int end) {

		return findByC_GtM_A(
			companyId, modifiedDate, associationClassName, start, end, null);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName,
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return findByC_GtM_A(
			companyId, modifiedDate, associationClassName, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName,
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_GtM_A.find(
			finderCache,
			new Object[] {companyId, modifiedDate, associationClassName}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation findByC_GtM_A_First(
			long companyId, Date modifiedDate, String associationClassName,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws NoSuchAssociationException {

		return _collectionPersistenceFinderByC_GtM_A.findFirst(
			finderCache,
			new Object[] {companyId, modifiedDate, associationClassName},
			orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation fetchByC_GtM_A_First(
		long companyId, Date modifiedDate, String associationClassName,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return _collectionPersistenceFinderByC_GtM_A.fetchFirst(
			finderCache,
			new Object[] {companyId, modifiedDate, associationClassName},
			orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 */
	@Override
	public void removeByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName) {

		_collectionPersistenceFinderByC_GtM_A.remove(
			finderCache,
			new Object[] {companyId, modifiedDate, associationClassName});
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and modifiedDate &gt; &#63; and associationClassName = &#63;.
	 *
	 * @param companyId the company ID
	 * @param modifiedDate the modified date
	 * @param associationClassName the association class name
	 * @return the number of matching analytics associations
	 */
	@Override
	public int countByC_GtM_A(
		long companyId, Date modifiedDate, String associationClassName) {

		return _collectionPersistenceFinderByC_GtM_A.count(
			finderCache,
			new Object[] {companyId, modifiedDate, associationClassName});
	}

	private CollectionPersistenceFinder
		<AnalyticsAssociation, NoSuchAssociationException>
			_collectionPersistenceFinderByC_A_A;

	/**
	 * Returns an ordered range of all the analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching analytics associations
	 */
	@Override
	public List<AnalyticsAssociation> findByC_A_A(
		long companyId, String associationClassName, long associationClassPK,
		int start, int end,
		OrderByComparator<AnalyticsAssociation> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_A.find(
			finderCache,
			new Object[] {companyId, associationClassName, associationClassPK},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association
	 * @throws NoSuchAssociationException if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation findByC_A_A_First(
			long companyId, String associationClassName,
			long associationClassPK,
			OrderByComparator<AnalyticsAssociation> orderByComparator)
		throws NoSuchAssociationException {

		return _collectionPersistenceFinderByC_A_A.findFirst(
			finderCache,
			new Object[] {companyId, associationClassName, associationClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first analytics association in the ordered set where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching analytics association, or <code>null</code> if a matching analytics association could not be found
	 */
	@Override
	public AnalyticsAssociation fetchByC_A_A_First(
		long companyId, String associationClassName, long associationClassPK,
		OrderByComparator<AnalyticsAssociation> orderByComparator) {

		return _collectionPersistenceFinderByC_A_A.fetchFirst(
			finderCache,
			new Object[] {companyId, associationClassName, associationClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 */
	@Override
	public void removeByC_A_A(
		long companyId, String associationClassName, long associationClassPK) {

		_collectionPersistenceFinderByC_A_A.remove(
			finderCache,
			new Object[] {companyId, associationClassName, associationClassPK});
	}

	/**
	 * Returns the number of analytics associations where companyId = &#63; and associationClassName = &#63; and associationClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param associationClassName the association class name
	 * @param associationClassPK the association class pk
	 * @return the number of matching analytics associations
	 */
	@Override
	public int countByC_A_A(
		long companyId, String associationClassName, long associationClassPK) {

		return _collectionPersistenceFinderByC_A_A.count(
			finderCache,
			new Object[] {companyId, associationClassName, associationClassPK});
	}

	public AnalyticsAssociationPersistenceImpl() {
		setModelClass(AnalyticsAssociation.class);

		setModelImplClass(AnalyticsAssociationImpl.class);
		setModelPKClass(long.class);

		setTable(AnalyticsAssociationTable.INSTANCE);
	}

	/**
	 * Creates a new analytics association with the primary key. Does not add the analytics association to the database.
	 *
	 * @param analyticsAssociationId the primary key for the new analytics association
	 * @return the new analytics association
	 */
	@Override
	public AnalyticsAssociation create(long analyticsAssociationId) {
		AnalyticsAssociation analyticsAssociation =
			new AnalyticsAssociationImpl();

		analyticsAssociation.setNew(true);
		analyticsAssociation.setPrimaryKey(analyticsAssociationId);

		analyticsAssociation.setCompanyId(CompanyThreadLocal.getCompanyId());

		return analyticsAssociation;
	}

	/**
	 * Removes the analytics association with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association that was removed
	 * @throws NoSuchAssociationException if a analytics association with the primary key could not be found
	 */
	@Override
	public AnalyticsAssociation remove(long analyticsAssociationId)
		throws NoSuchAssociationException {

		return remove((Serializable)analyticsAssociationId);
	}

	@Override
	protected AnalyticsAssociation removeImpl(
		AnalyticsAssociation analyticsAssociation) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(analyticsAssociation)) {
				analyticsAssociation = (AnalyticsAssociation)session.get(
					AnalyticsAssociationImpl.class,
					analyticsAssociation.getPrimaryKeyObj());
			}

			if ((analyticsAssociation != null) &&
				ctPersistenceHelper.isRemove(analyticsAssociation)) {

				session.delete(analyticsAssociation);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (analyticsAssociation != null) {
			clearCache(analyticsAssociation);
		}

		return analyticsAssociation;
	}

	@Override
	public AnalyticsAssociation updateImpl(
		AnalyticsAssociation analyticsAssociation) {

		boolean isNew = analyticsAssociation.isNew();

		if (!(analyticsAssociation instanceof AnalyticsAssociationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(analyticsAssociation.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					analyticsAssociation);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in analyticsAssociation proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AnalyticsAssociation implementation " +
					analyticsAssociation.getClass());
		}

		AnalyticsAssociationModelImpl analyticsAssociationModelImpl =
			(AnalyticsAssociationModelImpl)analyticsAssociation;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (analyticsAssociation.getCreateDate() == null)) {
			if (serviceContext == null) {
				analyticsAssociation.setCreateDate(date);
			}
			else {
				analyticsAssociation.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!analyticsAssociationModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				analyticsAssociation.setModifiedDate(date);
			}
			else {
				analyticsAssociation.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(analyticsAssociation)) {
				if (!isNew) {
					session.evict(
						AnalyticsAssociationImpl.class,
						analyticsAssociation.getPrimaryKeyObj());
				}

				session.save(analyticsAssociation);
			}
			else {
				analyticsAssociation = (AnalyticsAssociation)session.merge(
					analyticsAssociation);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(analyticsAssociation, false);

		if (isNew) {
			analyticsAssociation.setNew(false);
		}

		analyticsAssociation.resetOriginalValues();

		return analyticsAssociation;
	}

	/**
	 * Returns the analytics association with the primary key or throws a <code>NoSuchAssociationException</code> if it could not be found.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association
	 * @throws NoSuchAssociationException if a analytics association with the primary key could not be found
	 */
	@Override
	public AnalyticsAssociation findByPrimaryKey(long analyticsAssociationId)
		throws NoSuchAssociationException {

		return findByPrimaryKey((Serializable)analyticsAssociationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the analytics association with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association, or <code>null</code> if a analytics association with the primary key could not be found
	 */
	@Override
	public AnalyticsAssociation fetchByPrimaryKey(long analyticsAssociationId) {
		return fetchByPrimaryKey((Serializable)analyticsAssociationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "analyticsAssociationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ANALYTICSASSOCIATION;
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
		return AnalyticsAssociationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AnalyticsAssociation";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("userId");
		ctMergeColumnNames.add("associationClassName");
		ctMergeColumnNames.add("associationClassPK");
		ctMergeColumnNames.add("className");
		ctStrictColumnNames.add("classPK");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("analyticsAssociationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the analytics association persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_ANALYTICSASSOCIATION_WHERE,
				_SQL_COUNT_ANALYTICSASSOCIATION_WHERE,
				AnalyticsAssociationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"analyticsAssociation.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AnalyticsAssociation::getCompanyId));

		_collectionPersistenceFinderByC_LtM = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LtM",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "modifiedDate"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LtM",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"companyId", "modifiedDate"}, false),
			_SQL_SELECT_ANALYTICSASSOCIATION_WHERE,
			_SQL_COUNT_ANALYTICSASSOCIATION_WHERE,
			AnalyticsAssociationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"analyticsAssociation.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AnalyticsAssociation::getCompanyId),
			new FinderColumn<>(
				"analyticsAssociation.", "modifiedDate", FinderColumn.Type.DATE,
				"<", true, true, AnalyticsAssociation::getModifiedDate));

		_collectionPersistenceFinderByC_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "associationClassName"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "associationClassName"}, 0, 2, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "associationClassName"}, 0, 2, false,
				null),
			_SQL_SELECT_ANALYTICSASSOCIATION_WHERE,
			_SQL_COUNT_ANALYTICSASSOCIATION_WHERE,
			AnalyticsAssociationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"analyticsAssociation.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AnalyticsAssociation::getCompanyId),
			new FinderColumn<>(
				"analyticsAssociation.", "associationClassName",
				FinderColumn.Type.STRING, "=", true, true,
				AnalyticsAssociation::getAssociationClassName));

		_collectionPersistenceFinderByC_GtM_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_GtM_A",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "modifiedDate", "associationClassName"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_GtM_A",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						String.class.getName()
					},
					new String[] {
						"companyId", "modifiedDate", "associationClassName"
					},
					false),
				_SQL_SELECT_ANALYTICSASSOCIATION_WHERE,
				_SQL_COUNT_ANALYTICSASSOCIATION_WHERE,
				AnalyticsAssociationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"analyticsAssociation.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AnalyticsAssociation::getCompanyId),
				new FinderColumn<>(
					"analyticsAssociation.", "modifiedDate",
					FinderColumn.Type.DATE, ">", true, true,
					AnalyticsAssociation::getModifiedDate),
				new FinderColumn<>(
					"analyticsAssociation.", "associationClassName",
					FinderColumn.Type.STRING, "=", true, true,
					AnalyticsAssociation::getAssociationClassName));

		_collectionPersistenceFinderByC_A_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_A",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"companyId", "associationClassName", "associationClassPK"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_A",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"companyId", "associationClassName", "associationClassPK"
				},
				0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_A",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"companyId", "associationClassName", "associationClassPK"
				},
				0, 2, false, null),
			_SQL_SELECT_ANALYTICSASSOCIATION_WHERE,
			_SQL_COUNT_ANALYTICSASSOCIATION_WHERE,
			AnalyticsAssociationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"analyticsAssociation.", "companyId", FinderColumn.Type.LONG,
				"=", true, true, AnalyticsAssociation::getCompanyId),
			new FinderColumn<>(
				"analyticsAssociation.", "associationClassName",
				FinderColumn.Type.STRING, "=", true, true,
				AnalyticsAssociation::getAssociationClassName),
			new FinderColumn<>(
				"analyticsAssociation.", "associationClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				AnalyticsAssociation::getAssociationClassPK));

		AnalyticsAssociationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AnalyticsAssociationUtil.setPersistence(null);

		entityCache.removeCache(AnalyticsAssociationImpl.class.getName());
	}

	@Override
	@Reference(
		target = AnalyticsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AnalyticsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AnalyticsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AnalyticsAssociationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ANALYTICSASSOCIATION =
		"SELECT analyticsAssociation FROM AnalyticsAssociation analyticsAssociation";

	private static final String _SQL_SELECT_ANALYTICSASSOCIATION_WHERE =
		"SELECT analyticsAssociation FROM AnalyticsAssociation analyticsAssociation WHERE ";

	private static final String _SQL_COUNT_ANALYTICSASSOCIATION_WHERE =
		"SELECT COUNT(analyticsAssociation) FROM AnalyticsAssociation analyticsAssociation WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:253779308