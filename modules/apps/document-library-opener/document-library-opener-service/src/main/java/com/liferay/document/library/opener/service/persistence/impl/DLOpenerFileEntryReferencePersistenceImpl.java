/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.service.persistence.impl;

import com.liferay.document.library.opener.exception.NoSuchFileEntryReferenceException;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.document.library.opener.model.DLOpenerFileEntryReferenceTable;
import com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceImpl;
import com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl;
import com.liferay.document.library.opener.service.persistence.DLOpenerFileEntryReferencePersistence;
import com.liferay.document.library.opener.service.persistence.DLOpenerFileEntryReferenceUtil;
import com.liferay.document.library.opener.service.persistence.impl.constants.DLOpenerPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the dl opener file entry reference service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DLOpenerFileEntryReferencePersistence.class)
public class DLOpenerFileEntryReferencePersistenceImpl
	extends BasePersistenceImpl
		<DLOpenerFileEntryReference, NoSuchFileEntryReferenceException>
	implements DLOpenerFileEntryReferencePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLOpenerFileEntryReferenceUtil</code> to access the dl opener file entry reference persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLOpenerFileEntryReferenceImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<DLOpenerFileEntryReference, NoSuchFileEntryReferenceException>
			_uniquePersistenceFinderByFileEntryId;

	/**
	 * Returns the dl opener file entry reference where fileEntryId = &#63; or throws a <code>NoSuchFileEntryReferenceException</code> if it could not be found.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the matching dl opener file entry reference
	 * @throws NoSuchFileEntryReferenceException if a matching dl opener file entry reference could not be found
	 */
	@Override
	public DLOpenerFileEntryReference findByFileEntryId(long fileEntryId)
		throws NoSuchFileEntryReferenceException {

		return _uniquePersistenceFinderByFileEntryId.find(
			finderCache, new Object[] {fileEntryId});
	}

	/**
	 * Returns the dl opener file entry reference where fileEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param fileEntryId the file entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl opener file entry reference, or <code>null</code> if a matching dl opener file entry reference could not be found
	 */
	@Override
	public DLOpenerFileEntryReference fetchByFileEntryId(
		long fileEntryId, boolean useFinderCache) {

		return _uniquePersistenceFinderByFileEntryId.fetch(
			finderCache, new Object[] {fileEntryId}, useFinderCache);
	}

	/**
	 * Removes the dl opener file entry reference where fileEntryId = &#63; from the database.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the dl opener file entry reference that was removed
	 */
	@Override
	public DLOpenerFileEntryReference removeByFileEntryId(long fileEntryId)
		throws NoSuchFileEntryReferenceException {

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			findByFileEntryId(fileEntryId);

		return remove(dlOpenerFileEntryReference);
	}

	/**
	 * Returns the number of dl opener file entry references where fileEntryId = &#63;.
	 *
	 * @param fileEntryId the file entry ID
	 * @return the number of matching dl opener file entry references
	 */
	@Override
	public int countByFileEntryId(long fileEntryId) {
		return _uniquePersistenceFinderByFileEntryId.count(
			finderCache, new Object[] {fileEntryId});
	}

	private UniquePersistenceFinder
		<DLOpenerFileEntryReference, NoSuchFileEntryReferenceException>
			_uniquePersistenceFinderByR_F;

	/**
	 * Returns the dl opener file entry reference where referenceType = &#63; and fileEntryId = &#63; or throws a <code>NoSuchFileEntryReferenceException</code> if it could not be found.
	 *
	 * @param referenceType the reference type
	 * @param fileEntryId the file entry ID
	 * @return the matching dl opener file entry reference
	 * @throws NoSuchFileEntryReferenceException if a matching dl opener file entry reference could not be found
	 */
	@Override
	public DLOpenerFileEntryReference findByR_F(
			String referenceType, long fileEntryId)
		throws NoSuchFileEntryReferenceException {

		return _uniquePersistenceFinderByR_F.find(
			finderCache, new Object[] {referenceType, fileEntryId});
	}

	/**
	 * Returns the dl opener file entry reference where referenceType = &#63; and fileEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param referenceType the reference type
	 * @param fileEntryId the file entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl opener file entry reference, or <code>null</code> if a matching dl opener file entry reference could not be found
	 */
	@Override
	public DLOpenerFileEntryReference fetchByR_F(
		String referenceType, long fileEntryId, boolean useFinderCache) {

		return _uniquePersistenceFinderByR_F.fetch(
			finderCache, new Object[] {referenceType, fileEntryId},
			useFinderCache);
	}

	/**
	 * Removes the dl opener file entry reference where referenceType = &#63; and fileEntryId = &#63; from the database.
	 *
	 * @param referenceType the reference type
	 * @param fileEntryId the file entry ID
	 * @return the dl opener file entry reference that was removed
	 */
	@Override
	public DLOpenerFileEntryReference removeByR_F(
			String referenceType, long fileEntryId)
		throws NoSuchFileEntryReferenceException {

		DLOpenerFileEntryReference dlOpenerFileEntryReference = findByR_F(
			referenceType, fileEntryId);

		return remove(dlOpenerFileEntryReference);
	}

	/**
	 * Returns the number of dl opener file entry references where referenceType = &#63; and fileEntryId = &#63;.
	 *
	 * @param referenceType the reference type
	 * @param fileEntryId the file entry ID
	 * @return the number of matching dl opener file entry references
	 */
	@Override
	public int countByR_F(String referenceType, long fileEntryId) {
		return _uniquePersistenceFinderByR_F.count(
			finderCache, new Object[] {referenceType, fileEntryId});
	}

	public DLOpenerFileEntryReferencePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DLOpenerFileEntryReference.class);

		setModelImplClass(DLOpenerFileEntryReferenceImpl.class);
		setModelPKClass(long.class);

		setTable(DLOpenerFileEntryReferenceTable.INSTANCE);
	}

	/**
	 * Creates a new dl opener file entry reference with the primary key. Does not add the dl opener file entry reference to the database.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key for the new dl opener file entry reference
	 * @return the new dl opener file entry reference
	 */
	@Override
	public DLOpenerFileEntryReference create(
		long dlOpenerFileEntryReferenceId) {

		DLOpenerFileEntryReference dlOpenerFileEntryReference =
			new DLOpenerFileEntryReferenceImpl();

		dlOpenerFileEntryReference.setNew(true);
		dlOpenerFileEntryReference.setPrimaryKey(dlOpenerFileEntryReferenceId);

		dlOpenerFileEntryReference.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return dlOpenerFileEntryReference;
	}

	/**
	 * Removes the dl opener file entry reference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference that was removed
	 * @throws NoSuchFileEntryReferenceException if a dl opener file entry reference with the primary key could not be found
	 */
	@Override
	public DLOpenerFileEntryReference remove(long dlOpenerFileEntryReferenceId)
		throws NoSuchFileEntryReferenceException {

		return remove((Serializable)dlOpenerFileEntryReferenceId);
	}

	@Override
	protected DLOpenerFileEntryReference removeImpl(
		DLOpenerFileEntryReference dlOpenerFileEntryReference) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlOpenerFileEntryReference)) {
				dlOpenerFileEntryReference =
					(DLOpenerFileEntryReference)session.get(
						DLOpenerFileEntryReferenceImpl.class,
						dlOpenerFileEntryReference.getPrimaryKeyObj());
			}

			if (dlOpenerFileEntryReference != null) {
				session.delete(dlOpenerFileEntryReference);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlOpenerFileEntryReference != null) {
			clearCache(dlOpenerFileEntryReference);
		}

		return dlOpenerFileEntryReference;
	}

	@Override
	public DLOpenerFileEntryReference updateImpl(
		DLOpenerFileEntryReference dlOpenerFileEntryReference) {

		boolean isNew = dlOpenerFileEntryReference.isNew();

		if (!(dlOpenerFileEntryReference instanceof
				DLOpenerFileEntryReferenceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlOpenerFileEntryReference.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlOpenerFileEntryReference);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlOpenerFileEntryReference proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLOpenerFileEntryReference implementation " +
					dlOpenerFileEntryReference.getClass());
		}

		DLOpenerFileEntryReferenceModelImpl
			dlOpenerFileEntryReferenceModelImpl =
				(DLOpenerFileEntryReferenceModelImpl)dlOpenerFileEntryReference;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dlOpenerFileEntryReference.getCreateDate() == null)) {
			if (serviceContext == null) {
				dlOpenerFileEntryReference.setCreateDate(date);
			}
			else {
				dlOpenerFileEntryReference.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dlOpenerFileEntryReferenceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dlOpenerFileEntryReference.setModifiedDate(date);
			}
			else {
				dlOpenerFileEntryReference.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dlOpenerFileEntryReference);
			}
			else {
				dlOpenerFileEntryReference =
					(DLOpenerFileEntryReference)session.merge(
						dlOpenerFileEntryReference);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlOpenerFileEntryReference, false);

		if (isNew) {
			dlOpenerFileEntryReference.setNew(false);
		}

		dlOpenerFileEntryReference.resetOriginalValues();

		return dlOpenerFileEntryReference;
	}

	/**
	 * Returns the dl opener file entry reference with the primary key or throws a <code>NoSuchFileEntryReferenceException</code> if it could not be found.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference
	 * @throws NoSuchFileEntryReferenceException if a dl opener file entry reference with the primary key could not be found
	 */
	@Override
	public DLOpenerFileEntryReference findByPrimaryKey(
			long dlOpenerFileEntryReferenceId)
		throws NoSuchFileEntryReferenceException {

		return findByPrimaryKey((Serializable)dlOpenerFileEntryReferenceId);
	}

	/**
	 * Returns the dl opener file entry reference with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference, or <code>null</code> if a dl opener file entry reference with the primary key could not be found
	 */
	@Override
	public DLOpenerFileEntryReference fetchByPrimaryKey(
		long dlOpenerFileEntryReferenceId) {

		return fetchByPrimaryKey((Serializable)dlOpenerFileEntryReferenceId);
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
		return "dlOpenerFileEntryReferenceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLOPENERFILEENTRYREFERENCE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DLOpenerFileEntryReferenceModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dl opener file entry reference persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByFileEntryId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByFileEntryId",
				new String[] {Long.class.getName()},
				new String[] {"fileEntryId"}, 0, 0, false,
				DLOpenerFileEntryReference::getFileEntryId),
			_SQL_SELECT_DLOPENERFILEENTRYREFERENCE_WHERE, "",
			new FinderColumn<>(
				"dlOpenerFileEntryReference.", "fileEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				DLOpenerFileEntryReference::getFileEntryId));

		_uniquePersistenceFinderByR_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByR_F",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"referenceType", "fileEntryId"}, 0, 1, false,
				convertNullFunction(
					DLOpenerFileEntryReference::getReferenceType),
				DLOpenerFileEntryReference::getFileEntryId),
			_SQL_SELECT_DLOPENERFILEENTRYREFERENCE_WHERE, "",
			new FinderColumn<>(
				"dlOpenerFileEntryReference.", "referenceType",
				FinderColumn.Type.STRING, "=", true, true,
				DLOpenerFileEntryReference::getReferenceType),
			new FinderColumn<>(
				"dlOpenerFileEntryReference.", "fileEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				DLOpenerFileEntryReference::getFileEntryId));

		DLOpenerFileEntryReferenceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DLOpenerFileEntryReferenceUtil.setPersistence(null);

		entityCache.removeCache(DLOpenerFileEntryReferenceImpl.class.getName());
	}

	@Override
	@Reference(
		target = DLOpenerPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DLOpenerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DLOpenerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DLOPENERFILEENTRYREFERENCE =
		"SELECT dlOpenerFileEntryReference FROM DLOpenerFileEntryReference dlOpenerFileEntryReference";

	private static final String _SQL_SELECT_DLOPENERFILEENTRYREFERENCE_WHERE =
		"SELECT dlOpenerFileEntryReference FROM DLOpenerFileEntryReference dlOpenerFileEntryReference WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:984050126