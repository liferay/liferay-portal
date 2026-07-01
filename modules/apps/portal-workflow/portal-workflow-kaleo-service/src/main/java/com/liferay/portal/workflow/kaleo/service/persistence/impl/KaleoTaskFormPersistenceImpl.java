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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskFormException;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskForm;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormTable;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormImpl;
import com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormModelImpl;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.impl.constants.KaleoPersistenceConstants;

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
 * The persistence implementation for the kaleo task form service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = KaleoTaskFormPersistence.class)
public class KaleoTaskFormPersistenceImpl
	extends BasePersistenceImpl<KaleoTaskForm, NoSuchTaskFormException>
	implements KaleoTaskFormPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>KaleoTaskFormUtil</code> to access the kaleo task form persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		KaleoTaskFormImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<KaleoTaskForm, NoSuchTaskFormException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the kaleo task forms where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kaleo task forms
	 * @param end the upper bound of the range of kaleo task forms (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task forms
	 */
	@Override
	public List<KaleoTaskForm> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<KaleoTaskForm> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form
	 * @throws NoSuchTaskFormException if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm findByCompanyId_First(
			long companyId, OrderByComparator<KaleoTaskForm> orderByComparator)
		throws NoSuchTaskFormException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form, or <code>null</code> if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm fetchByCompanyId_First(
		long companyId, OrderByComparator<KaleoTaskForm> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task forms where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of kaleo task forms where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching kaleo task forms
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<KaleoTaskForm, NoSuchTaskFormException>
		_collectionPersistenceFinderByKaleoDefinitionVersionId;

	/**
	 * Returns an ordered range of all the kaleo task forms where kaleoDefinitionVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param start the lower bound of the range of kaleo task forms
	 * @param end the upper bound of the range of kaleo task forms (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task forms
	 */
	@Override
	public List<KaleoTaskForm> findByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId, int start, int end,
		OrderByComparator<KaleoTaskForm> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.find(
			finderCache, new Object[] {kaleoDefinitionVersionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form
	 * @throws NoSuchTaskFormException if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm findByKaleoDefinitionVersionId_First(
			long kaleoDefinitionVersionId,
			OrderByComparator<KaleoTaskForm> orderByComparator)
		throws NoSuchTaskFormException {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.findFirst(
			finderCache, new Object[] {kaleoDefinitionVersionId},
			orderByComparator);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form, or <code>null</code> if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm fetchByKaleoDefinitionVersionId_First(
		long kaleoDefinitionVersionId,
		OrderByComparator<KaleoTaskForm> orderByComparator) {

		return _collectionPersistenceFinderByKaleoDefinitionVersionId.
			fetchFirst(
				finderCache, new Object[] {kaleoDefinitionVersionId},
				orderByComparator);
	}

	/**
	 * Removes all the kaleo task forms where kaleoDefinitionVersionId = &#63; from the database.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 */
	@Override
	public void removeByKaleoDefinitionVersionId(
		long kaleoDefinitionVersionId) {

		_collectionPersistenceFinderByKaleoDefinitionVersionId.remove(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	/**
	 * Returns the number of kaleo task forms where kaleoDefinitionVersionId = &#63;.
	 *
	 * @param kaleoDefinitionVersionId the kaleo definition version ID
	 * @return the number of matching kaleo task forms
	 */
	@Override
	public int countByKaleoDefinitionVersionId(long kaleoDefinitionVersionId) {
		return _collectionPersistenceFinderByKaleoDefinitionVersionId.count(
			finderCache, new Object[] {kaleoDefinitionVersionId});
	}

	private CollectionPersistenceFinder<KaleoTaskForm, NoSuchTaskFormException>
		_collectionPersistenceFinderByKaleoNodeId;

	/**
	 * Returns an ordered range of all the kaleo task forms where kaleoNodeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param start the lower bound of the range of kaleo task forms
	 * @param end the upper bound of the range of kaleo task forms (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task forms
	 */
	@Override
	public List<KaleoTaskForm> findByKaleoNodeId(
		long kaleoNodeId, int start, int end,
		OrderByComparator<KaleoTaskForm> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoNodeId.find(
			finderCache, new Object[] {kaleoNodeId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form
	 * @throws NoSuchTaskFormException if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm findByKaleoNodeId_First(
			long kaleoNodeId,
			OrderByComparator<KaleoTaskForm> orderByComparator)
		throws NoSuchTaskFormException {

		return _collectionPersistenceFinderByKaleoNodeId.findFirst(
			finderCache, new Object[] {kaleoNodeId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form, or <code>null</code> if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm fetchByKaleoNodeId_First(
		long kaleoNodeId, OrderByComparator<KaleoTaskForm> orderByComparator) {

		return _collectionPersistenceFinderByKaleoNodeId.fetchFirst(
			finderCache, new Object[] {kaleoNodeId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task forms where kaleoNodeId = &#63; from the database.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 */
	@Override
	public void removeByKaleoNodeId(long kaleoNodeId) {
		_collectionPersistenceFinderByKaleoNodeId.remove(
			finderCache, new Object[] {kaleoNodeId});
	}

	/**
	 * Returns the number of kaleo task forms where kaleoNodeId = &#63;.
	 *
	 * @param kaleoNodeId the kaleo node ID
	 * @return the number of matching kaleo task forms
	 */
	@Override
	public int countByKaleoNodeId(long kaleoNodeId) {
		return _collectionPersistenceFinderByKaleoNodeId.count(
			finderCache, new Object[] {kaleoNodeId});
	}

	private CollectionPersistenceFinder<KaleoTaskForm, NoSuchTaskFormException>
		_collectionPersistenceFinderByKaleoTaskId;

	/**
	 * Returns an ordered range of all the kaleo task forms where kaleoTaskId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KaleoTaskFormModelImpl</code>.
	 * </p>
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param start the lower bound of the range of kaleo task forms
	 * @param end the upper bound of the range of kaleo task forms (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kaleo task forms
	 */
	@Override
	public List<KaleoTaskForm> findByKaleoTaskId(
		long kaleoTaskId, int start, int end,
		OrderByComparator<KaleoTaskForm> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByKaleoTaskId.find(
			finderCache, new Object[] {kaleoTaskId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where kaleoTaskId = &#63;.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form
	 * @throws NoSuchTaskFormException if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm findByKaleoTaskId_First(
			long kaleoTaskId,
			OrderByComparator<KaleoTaskForm> orderByComparator)
		throws NoSuchTaskFormException {

		return _collectionPersistenceFinderByKaleoTaskId.findFirst(
			finderCache, new Object[] {kaleoTaskId}, orderByComparator);
	}

	/**
	 * Returns the first kaleo task form in the ordered set where kaleoTaskId = &#63;.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kaleo task form, or <code>null</code> if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm fetchByKaleoTaskId_First(
		long kaleoTaskId, OrderByComparator<KaleoTaskForm> orderByComparator) {

		return _collectionPersistenceFinderByKaleoTaskId.fetchFirst(
			finderCache, new Object[] {kaleoTaskId}, orderByComparator);
	}

	/**
	 * Removes all the kaleo task forms where kaleoTaskId = &#63; from the database.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 */
	@Override
	public void removeByKaleoTaskId(long kaleoTaskId) {
		_collectionPersistenceFinderByKaleoTaskId.remove(
			finderCache, new Object[] {kaleoTaskId});
	}

	/**
	 * Returns the number of kaleo task forms where kaleoTaskId = &#63;.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @return the number of matching kaleo task forms
	 */
	@Override
	public int countByKaleoTaskId(long kaleoTaskId) {
		return _collectionPersistenceFinderByKaleoTaskId.count(
			finderCache, new Object[] {kaleoTaskId});
	}

	private UniquePersistenceFinder<KaleoTaskForm, NoSuchTaskFormException>
		_uniquePersistenceFinderByFormUuid_KTI;

	/**
	 * Returns the kaleo task form where kaleoTaskId = &#63; and formUuid = &#63; or throws a <code>NoSuchTaskFormException</code> if it could not be found.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param formUuid the form uuid
	 * @return the matching kaleo task form
	 * @throws NoSuchTaskFormException if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm findByFormUuid_KTI(long kaleoTaskId, String formUuid)
		throws NoSuchTaskFormException {

		return _uniquePersistenceFinderByFormUuid_KTI.find(
			finderCache, new Object[] {kaleoTaskId, formUuid});
	}

	/**
	 * Returns the kaleo task form where kaleoTaskId = &#63; and formUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param formUuid the form uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kaleo task form, or <code>null</code> if a matching kaleo task form could not be found
	 */
	@Override
	public KaleoTaskForm fetchByFormUuid_KTI(
		long kaleoTaskId, String formUuid, boolean useFinderCache) {

		return _uniquePersistenceFinderByFormUuid_KTI.fetch(
			finderCache, new Object[] {kaleoTaskId, formUuid}, useFinderCache);
	}

	/**
	 * Removes the kaleo task form where kaleoTaskId = &#63; and formUuid = &#63; from the database.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param formUuid the form uuid
	 * @return the kaleo task form that was removed
	 */
	@Override
	public KaleoTaskForm removeByFormUuid_KTI(long kaleoTaskId, String formUuid)
		throws NoSuchTaskFormException {

		KaleoTaskForm kaleoTaskForm = findByFormUuid_KTI(kaleoTaskId, formUuid);

		return remove(kaleoTaskForm);
	}

	/**
	 * Returns the number of kaleo task forms where kaleoTaskId = &#63; and formUuid = &#63;.
	 *
	 * @param kaleoTaskId the kaleo task ID
	 * @param formUuid the form uuid
	 * @return the number of matching kaleo task forms
	 */
	@Override
	public int countByFormUuid_KTI(long kaleoTaskId, String formUuid) {
		return _uniquePersistenceFinderByFormUuid_KTI.count(
			finderCache, new Object[] {kaleoTaskId, formUuid});
	}

	public KaleoTaskFormPersistenceImpl() {
		setModelClass(KaleoTaskForm.class);

		setModelImplClass(KaleoTaskFormImpl.class);
		setModelPKClass(long.class);

		setTable(KaleoTaskFormTable.INSTANCE);
	}

	/**
	 * Creates a new kaleo task form with the primary key. Does not add the kaleo task form to the database.
	 *
	 * @param kaleoTaskFormId the primary key for the new kaleo task form
	 * @return the new kaleo task form
	 */
	@Override
	public KaleoTaskForm create(long kaleoTaskFormId) {
		KaleoTaskForm kaleoTaskForm = new KaleoTaskFormImpl();

		kaleoTaskForm.setNew(true);
		kaleoTaskForm.setPrimaryKey(kaleoTaskFormId);

		kaleoTaskForm.setCompanyId(CompanyThreadLocal.getCompanyId());

		return kaleoTaskForm;
	}

	/**
	 * Removes the kaleo task form with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kaleoTaskFormId the primary key of the kaleo task form
	 * @return the kaleo task form that was removed
	 * @throws NoSuchTaskFormException if a kaleo task form with the primary key could not be found
	 */
	@Override
	public KaleoTaskForm remove(long kaleoTaskFormId)
		throws NoSuchTaskFormException {

		return remove((Serializable)kaleoTaskFormId);
	}

	@Override
	protected KaleoTaskForm removeImpl(KaleoTaskForm kaleoTaskForm) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(kaleoTaskForm)) {
				kaleoTaskForm = (KaleoTaskForm)session.get(
					KaleoTaskFormImpl.class, kaleoTaskForm.getPrimaryKeyObj());
			}

			if ((kaleoTaskForm != null) &&
				ctPersistenceHelper.isRemove(kaleoTaskForm)) {

				session.delete(kaleoTaskForm);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (kaleoTaskForm != null) {
			clearCache(kaleoTaskForm);
		}

		return kaleoTaskForm;
	}

	@Override
	public KaleoTaskForm updateImpl(KaleoTaskForm kaleoTaskForm) {
		boolean isNew = kaleoTaskForm.isNew();

		if (!(kaleoTaskForm instanceof KaleoTaskFormModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(kaleoTaskForm.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					kaleoTaskForm);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in kaleoTaskForm proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom KaleoTaskForm implementation " +
					kaleoTaskForm.getClass());
		}

		KaleoTaskFormModelImpl kaleoTaskFormModelImpl =
			(KaleoTaskFormModelImpl)kaleoTaskForm;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (kaleoTaskForm.getCreateDate() == null)) {
			if (serviceContext == null) {
				kaleoTaskForm.setCreateDate(date);
			}
			else {
				kaleoTaskForm.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!kaleoTaskFormModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				kaleoTaskForm.setModifiedDate(date);
			}
			else {
				kaleoTaskForm.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(kaleoTaskForm)) {
				if (!isNew) {
					session.evict(
						KaleoTaskFormImpl.class,
						kaleoTaskForm.getPrimaryKeyObj());
				}

				session.save(kaleoTaskForm);
			}
			else {
				kaleoTaskForm = (KaleoTaskForm)session.merge(kaleoTaskForm);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(kaleoTaskForm, false);

		if (isNew) {
			kaleoTaskForm.setNew(false);
		}

		kaleoTaskForm.resetOriginalValues();

		return kaleoTaskForm;
	}

	/**
	 * Returns the kaleo task form with the primary key or throws a <code>NoSuchTaskFormException</code> if it could not be found.
	 *
	 * @param kaleoTaskFormId the primary key of the kaleo task form
	 * @return the kaleo task form
	 * @throws NoSuchTaskFormException if a kaleo task form with the primary key could not be found
	 */
	@Override
	public KaleoTaskForm findByPrimaryKey(long kaleoTaskFormId)
		throws NoSuchTaskFormException {

		return findByPrimaryKey((Serializable)kaleoTaskFormId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the kaleo task form with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kaleoTaskFormId the primary key of the kaleo task form
	 * @return the kaleo task form, or <code>null</code> if a kaleo task form with the primary key could not be found
	 */
	@Override
	public KaleoTaskForm fetchByPrimaryKey(long kaleoTaskFormId) {
		return fetchByPrimaryKey((Serializable)kaleoTaskFormId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "kaleoTaskFormId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_KALEOTASKFORM;
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
		return KaleoTaskFormModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "KaleoTaskForm";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("kaleoDefinitionId");
		ctMergeColumnNames.add("kaleoDefinitionVersionId");
		ctMergeColumnNames.add("kaleoNodeId");
		ctMergeColumnNames.add("kaleoTaskId");
		ctMergeColumnNames.add("kaleoTaskName");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("formCompanyId");
		ctMergeColumnNames.add("formDefinition");
		ctMergeColumnNames.add("formGroupId");
		ctMergeColumnNames.add("formId");
		ctMergeColumnNames.add("formUuid");
		ctMergeColumnNames.add("metadata");
		ctMergeColumnNames.add("priority");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("kaleoTaskFormId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the kaleo task form persistence.
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
				_SQL_SELECT_KALEOTASKFORM_WHERE, _SQL_COUNT_KALEOTASKFORM_WHERE,
				KaleoTaskFormModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoTaskForm.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, KaleoTaskForm::getCompanyId));

		_collectionPersistenceFinderByKaleoDefinitionVersionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoDefinitionVersionId",
					new String[] {Long.class.getName()},
					new String[] {"kaleoDefinitionVersionId"}, false),
				_SQL_SELECT_KALEOTASKFORM_WHERE, _SQL_COUNT_KALEOTASKFORM_WHERE,
				KaleoTaskFormModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoTaskForm.", "kaleoDefinitionVersionId",
					FinderColumn.Type.LONG, "=", true, true,
					KaleoTaskForm::getKaleoDefinitionVersionId));

		_collectionPersistenceFinderByKaleoNodeId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKaleoNodeId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoNodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoNodeId", new String[] {Long.class.getName()},
					new String[] {"kaleoNodeId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoNodeId", new String[] {Long.class.getName()},
					new String[] {"kaleoNodeId"}, false),
				_SQL_SELECT_KALEOTASKFORM_WHERE, _SQL_COUNT_KALEOTASKFORM_WHERE,
				KaleoTaskFormModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoTaskForm.", "kaleoNodeId", FinderColumn.Type.LONG,
					"=", true, true, KaleoTaskForm::getKaleoNodeId));

		_collectionPersistenceFinderByKaleoTaskId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByKaleoTaskId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"kaleoTaskId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByKaleoTaskId", new String[] {Long.class.getName()},
					new String[] {"kaleoTaskId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByKaleoTaskId", new String[] {Long.class.getName()},
					new String[] {"kaleoTaskId"}, false),
				_SQL_SELECT_KALEOTASKFORM_WHERE, _SQL_COUNT_KALEOTASKFORM_WHERE,
				KaleoTaskFormModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"kaleoTaskForm.", "kaleoTaskId", FinderColumn.Type.LONG,
					"=", true, true, KaleoTaskForm::getKaleoTaskId));

		_uniquePersistenceFinderByFormUuid_KTI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByFormUuid_KTI",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"kaleoTaskId", "formUuid"}, 0, 2, false,
				KaleoTaskForm::getKaleoTaskId,
				convertNullFunction(KaleoTaskForm::getFormUuid)),
			_SQL_SELECT_KALEOTASKFORM_WHERE, "",
			new FinderColumn<>(
				"kaleoTaskForm.", "kaleoTaskId", FinderColumn.Type.LONG, "=",
				true, true, KaleoTaskForm::getKaleoTaskId),
			new FinderColumn<>(
				"kaleoTaskForm.", "formUuid", FinderColumn.Type.STRING, "=",
				true, true, KaleoTaskForm::getFormUuid));

		KaleoTaskFormUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		KaleoTaskFormUtil.setPersistence(null);

		entityCache.removeCache(KaleoTaskFormImpl.class.getName());
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
		KaleoTaskFormModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_KALEOTASKFORM =
		"SELECT kaleoTaskForm FROM KaleoTaskForm kaleoTaskForm";

	private static final String _SQL_SELECT_KALEOTASKFORM_WHERE =
		"SELECT kaleoTaskForm FROM KaleoTaskForm kaleoTaskForm WHERE ";

	private static final String _SQL_COUNT_KALEOTASKFORM_WHERE =
		"SELECT COUNT(kaleoTaskForm) FROM KaleoTaskForm kaleoTaskForm WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No KaleoTaskForm exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTaskFormPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:382983663