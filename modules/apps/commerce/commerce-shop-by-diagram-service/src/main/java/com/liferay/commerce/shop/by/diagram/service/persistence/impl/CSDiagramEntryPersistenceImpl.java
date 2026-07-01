/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.persistence.impl;

import com.liferay.commerce.shop.by.diagram.exception.DuplicateCSDiagramEntryExternalReferenceCodeException;
import com.liferay.commerce.shop.by.diagram.exception.NoSuchCSDiagramEntryException;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntryTable;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryImpl;
import com.liferay.commerce.shop.by.diagram.model.impl.CSDiagramEntryModelImpl;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramEntryPersistence;
import com.liferay.commerce.shop.by.diagram.service.persistence.CSDiagramEntryUtil;
import com.liferay.commerce.shop.by.diagram.service.persistence.impl.constants.CommercePersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
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
 * The persistence implementation for the cs diagram entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CSDiagramEntryPersistence.class)
public class CSDiagramEntryPersistenceImpl
	extends BasePersistenceImpl<CSDiagramEntry, NoSuchCSDiagramEntryException>
	implements CSDiagramEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CSDiagramEntryUtil</code> to access the cs diagram entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CSDiagramEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CSDiagramEntry, NoSuchCSDiagramEntryException>
			_collectionPersistenceFinderByCPDefinitionId;

	/**
	 * Returns an ordered range of all the cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPDefinitionId.find(
			finderCache, new Object[] {CPDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		return _collectionPersistenceFinderByCPDefinitionId.findFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return _collectionPersistenceFinderByCPDefinitionId.fetchFirst(
			finderCache, new Object[] {CPDefinitionId}, orderByComparator);
	}

	/**
	 * Removes all the cs diagram entries where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	@Override
	public void removeByCPDefinitionId(long CPDefinitionId) {
		_collectionPersistenceFinderByCPDefinitionId.remove(
			finderCache, new Object[] {CPDefinitionId});
	}

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCPDefinitionId(long CPDefinitionId) {
		return _collectionPersistenceFinderByCPDefinitionId.count(
			finderCache, new Object[] {CPDefinitionId});
	}

	private CollectionPersistenceFinder
		<CSDiagramEntry, NoSuchCSDiagramEntryException>
			_collectionPersistenceFinderByCPInstanceId;

	/**
	 * Returns an ordered range of all the cs diagram entries where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPInstanceId.find(
			finderCache, new Object[] {CPInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPInstanceId_First(
			long CPInstanceId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		return _collectionPersistenceFinderByCPInstanceId.findFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPInstanceId_First(
		long CPInstanceId,
		OrderByComparator<CSDiagramEntry> orderByComparator) {

		return _collectionPersistenceFinderByCPInstanceId.fetchFirst(
			finderCache, new Object[] {CPInstanceId}, orderByComparator);
	}

	/**
	 * Removes all the cs diagram entries where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	@Override
	public void removeByCPInstanceId(long CPInstanceId) {
		_collectionPersistenceFinderByCPInstanceId.remove(
			finderCache, new Object[] {CPInstanceId});
	}

	/**
	 * Returns the number of cs diagram entries where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCPInstanceId(long CPInstanceId) {
		return _collectionPersistenceFinderByCPInstanceId.count(
			finderCache, new Object[] {CPInstanceId});
	}

	private CollectionPersistenceFinder
		<CSDiagramEntry, NoSuchCSDiagramEntryException>
			_collectionPersistenceFinderByCProductId;

	/**
	 * Returns an ordered range of all the cs diagram entries where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CSDiagramEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cs diagram entries
	 * @param end the upper bound of the range of cs diagram entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cs diagram entries
	 */
	@Override
	public List<CSDiagramEntry> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CSDiagramEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCProductId.find(
			finderCache, new Object[] {CProductId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCProductId_First(
			long CProductId,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws NoSuchCSDiagramEntryException {

		return _collectionPersistenceFinderByCProductId.findFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Returns the first cs diagram entry in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCProductId_First(
		long CProductId, OrderByComparator<CSDiagramEntry> orderByComparator) {

		return _collectionPersistenceFinderByCProductId.fetchFirst(
			finderCache, new Object[] {CProductId}, orderByComparator);
	}

	/**
	 * Removes all the cs diagram entries where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	@Override
	public void removeByCProductId(long CProductId) {
		_collectionPersistenceFinderByCProductId.remove(
			finderCache, new Object[] {CProductId});
	}

	/**
	 * Returns the number of cs diagram entries where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCProductId(long CProductId) {
		return _collectionPersistenceFinderByCProductId.count(
			finderCache, new Object[] {CProductId});
	}

	private UniquePersistenceFinder
		<CSDiagramEntry, NoSuchCSDiagramEntryException>
			_uniquePersistenceFinderByCPDI_S;

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByCPDI_S(long CPDefinitionId, String sequence)
		throws NoSuchCSDiagramEntryException {

		return _uniquePersistenceFinderByCPDI_S.find(
			finderCache, new Object[] {CPDefinitionId, sequence});
	}

	/**
	 * Returns the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByCPDI_S(
		long CPDefinitionId, String sequence, boolean useFinderCache) {

		return _uniquePersistenceFinderByCPDI_S.fetch(
			finderCache, new Object[] {CPDefinitionId, sequence},
			useFinderCache);
	}

	/**
	 * Removes the cs diagram entry where CPDefinitionId = &#63; and sequence = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the cs diagram entry that was removed
	 */
	@Override
	public CSDiagramEntry removeByCPDI_S(long CPDefinitionId, String sequence)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = findByCPDI_S(CPDefinitionId, sequence);

		return remove(csDiagramEntry);
	}

	/**
	 * Returns the number of cs diagram entries where CPDefinitionId = &#63; and sequence = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param sequence the sequence
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByCPDI_S(long CPDefinitionId, String sequence) {
		return _uniquePersistenceFinderByCPDI_S.count(
			finderCache, new Object[] {CPDefinitionId, sequence});
	}

	private UniquePersistenceFinder
		<CSDiagramEntry, NoSuchCSDiagramEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCSDiagramEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cs diagram entry, or <code>null</code> if a matching cs diagram entry could not be found
	 */
	@Override
	public CSDiagramEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the cs diagram entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cs diagram entry that was removed
	 */
	@Override
	public CSDiagramEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCSDiagramEntryException {

		CSDiagramEntry csDiagramEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(csDiagramEntry);
	}

	/**
	 * Returns the number of cs diagram entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cs diagram entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public CSDiagramEntryPersistenceImpl() {
		setModelClass(CSDiagramEntry.class);

		setModelImplClass(CSDiagramEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CSDiagramEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cs diagram entry with the primary key. Does not add the cs diagram entry to the database.
	 *
	 * @param CSDiagramEntryId the primary key for the new cs diagram entry
	 * @return the new cs diagram entry
	 */
	@Override
	public CSDiagramEntry create(long CSDiagramEntryId) {
		CSDiagramEntry csDiagramEntry = new CSDiagramEntryImpl();

		csDiagramEntry.setNew(true);
		csDiagramEntry.setPrimaryKey(CSDiagramEntryId);

		csDiagramEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return csDiagramEntry;
	}

	/**
	 * Removes the cs diagram entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry that was removed
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry remove(long CSDiagramEntryId)
		throws NoSuchCSDiagramEntryException {

		return remove((Serializable)CSDiagramEntryId);
	}

	@Override
	protected CSDiagramEntry removeImpl(CSDiagramEntry csDiagramEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(csDiagramEntry)) {
				csDiagramEntry = (CSDiagramEntry)session.get(
					CSDiagramEntryImpl.class,
					csDiagramEntry.getPrimaryKeyObj());
			}

			if ((csDiagramEntry != null) &&
				ctPersistenceHelper.isRemove(csDiagramEntry)) {

				session.delete(csDiagramEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (csDiagramEntry != null) {
			clearCache(csDiagramEntry);
		}

		return csDiagramEntry;
	}

	@Override
	public CSDiagramEntry updateImpl(CSDiagramEntry csDiagramEntry) {
		boolean isNew = csDiagramEntry.isNew();

		if (!(csDiagramEntry instanceof CSDiagramEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(csDiagramEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					csDiagramEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in csDiagramEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CSDiagramEntry implementation " +
					csDiagramEntry.getClass());
		}

		CSDiagramEntryModelImpl csDiagramEntryModelImpl =
			(CSDiagramEntryModelImpl)csDiagramEntry;

		if (Validator.isNull(csDiagramEntry.getExternalReferenceCode())) {
			csDiagramEntry.setExternalReferenceCode(
				String.valueOf(csDiagramEntry.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					csDiagramEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					csDiagramEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = csDiagramEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = csDiagramEntry.getPrimaryKey();
					}

					try {
						csDiagramEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CSDiagramEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								csDiagramEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CSDiagramEntry ercCSDiagramEntry = fetchByERC_C(
				csDiagramEntry.getExternalReferenceCode(),
				csDiagramEntry.getCompanyId());

			if (isNew) {
				if (ercCSDiagramEntry != null) {
					throw new DuplicateCSDiagramEntryExternalReferenceCodeException(
						"Duplicate cs diagram entry with external reference code " +
							csDiagramEntry.getExternalReferenceCode() +
								" and company " +
									csDiagramEntry.getCompanyId());
				}
			}
			else {
				if ((ercCSDiagramEntry != null) &&
					(csDiagramEntry.getCSDiagramEntryId() !=
						ercCSDiagramEntry.getCSDiagramEntryId())) {

					throw new DuplicateCSDiagramEntryExternalReferenceCodeException(
						"Duplicate cs diagram entry with external reference code " +
							csDiagramEntry.getExternalReferenceCode() +
								" and company " +
									csDiagramEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (csDiagramEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				csDiagramEntry.setCreateDate(date);
			}
			else {
				csDiagramEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!csDiagramEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				csDiagramEntry.setModifiedDate(date);
			}
			else {
				csDiagramEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(csDiagramEntry)) {
				if (!isNew) {
					session.evict(
						CSDiagramEntryImpl.class,
						csDiagramEntry.getPrimaryKeyObj());
				}

				session.save(csDiagramEntry);
			}
			else {
				csDiagramEntry = (CSDiagramEntry)session.merge(csDiagramEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(csDiagramEntry, false);

		if (isNew) {
			csDiagramEntry.setNew(false);
		}

		csDiagramEntry.resetOriginalValues();

		return csDiagramEntry;
	}

	/**
	 * Returns the cs diagram entry with the primary key or throws a <code>NoSuchCSDiagramEntryException</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry
	 * @throws NoSuchCSDiagramEntryException if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry findByPrimaryKey(long CSDiagramEntryId)
		throws NoSuchCSDiagramEntryException {

		return findByPrimaryKey((Serializable)CSDiagramEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cs diagram entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CSDiagramEntryId the primary key of the cs diagram entry
	 * @return the cs diagram entry, or <code>null</code> if a cs diagram entry with the primary key could not be found
	 */
	@Override
	public CSDiagramEntry fetchByPrimaryKey(long CSDiagramEntryId) {
		return fetchByPrimaryKey((Serializable)CSDiagramEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "CSDiagramEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CSDIAGRAMENTRY;
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
		return CSDiagramEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CSDiagramEntry";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("CPDefinitionId");
		ctMergeColumnNames.add("CPInstanceId");
		ctMergeColumnNames.add("CProductId");
		ctMergeColumnNames.add("diagram");
		ctMergeColumnNames.add("quantity");
		ctMergeColumnNames.add("sequence");
		ctMergeColumnNames.add("sku");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CSDiagramEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"CPDefinitionId", "sequence"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the cs diagram entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCPDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPDefinitionId", new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"CPDefinitionId"}, false),
				_SQL_SELECT_CSDIAGRAMENTRY_WHERE,
				_SQL_COUNT_CSDIAGRAMENTRY_WHERE,
				CSDiagramEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"csDiagramEntry.", "CPDefinitionId", FinderColumn.Type.LONG,
					"=", true, true, CSDiagramEntry::getCPDefinitionId));

		_collectionPersistenceFinderByCPInstanceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPInstanceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPInstanceId", new String[] {Long.class.getName()},
					new String[] {"CPInstanceId"}, false),
				_SQL_SELECT_CSDIAGRAMENTRY_WHERE,
				_SQL_COUNT_CSDIAGRAMENTRY_WHERE,
				CSDiagramEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"csDiagramEntry.", "CPInstanceId", FinderColumn.Type.LONG,
					"=", true, true, CSDiagramEntry::getCPInstanceId));

		_collectionPersistenceFinderByCProductId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCProductId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCProductId", new String[] {Long.class.getName()},
					new String[] {"CProductId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCProductId", new String[] {Long.class.getName()},
					new String[] {"CProductId"}, false),
				_SQL_SELECT_CSDIAGRAMENTRY_WHERE,
				_SQL_COUNT_CSDIAGRAMENTRY_WHERE,
				CSDiagramEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"csDiagramEntry.", "CProductId", FinderColumn.Type.LONG,
					"=", true, true, CSDiagramEntry::getCProductId));

		_uniquePersistenceFinderByCPDI_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCPDI_S",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"CPDefinitionId", "sequence"}, 0, 2, false,
				CSDiagramEntry::getCPDefinitionId,
				convertNullFunction(CSDiagramEntry::getSequence)),
			_SQL_SELECT_CSDIAGRAMENTRY_WHERE, "",
			new FinderColumn<>(
				"csDiagramEntry.", "CPDefinitionId", FinderColumn.Type.LONG,
				"=", true, true, CSDiagramEntry::getCPDefinitionId),
			new FinderColumn<>(
				"csDiagramEntry.", "sequence", FinderColumn.Type.STRING, "=",
				true, true, CSDiagramEntry::getSequence));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(CSDiagramEntry::getExternalReferenceCode),
				CSDiagramEntry::getCompanyId),
			_SQL_SELECT_CSDIAGRAMENTRY_WHERE, "",
			new FinderColumn<>(
				"csDiagramEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CSDiagramEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"csDiagramEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, CSDiagramEntry::getCompanyId));

		CSDiagramEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CSDiagramEntryUtil.setPersistence(null);

		entityCache.removeCache(CSDiagramEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CSDiagramEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CSDIAGRAMENTRY =
		"SELECT csDiagramEntry FROM CSDiagramEntry csDiagramEntry";

	private static final String _SQL_SELECT_CSDIAGRAMENTRY_WHERE =
		"SELECT csDiagramEntry FROM CSDiagramEntry csDiagramEntry WHERE ";

	private static final String _SQL_COUNT_CSDIAGRAMENTRY_WHERE =
		"SELECT COUNT(csDiagramEntry) FROM CSDiagramEntry csDiagramEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CSDiagramEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CSDiagramEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1916226259