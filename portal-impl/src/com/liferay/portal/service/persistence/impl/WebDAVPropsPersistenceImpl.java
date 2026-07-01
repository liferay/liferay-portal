/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchWebDAVPropsException;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.model.WebDAVPropsTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.WebDAVPropsPersistence;
import com.liferay.portal.kernel.service.persistence.WebDAVPropsUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.WebDAVPropsImpl;
import com.liferay.portal.model.impl.WebDAVPropsModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.Map;

/**
 * The persistence implementation for the web dav props service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WebDAVPropsPersistenceImpl
	extends BasePersistenceImpl<WebDAVProps, NoSuchWebDAVPropsException>
	implements WebDAVPropsPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WebDAVPropsUtil</code> to access the web dav props persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WebDAVPropsImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<WebDAVProps, NoSuchWebDAVPropsException>
		_uniquePersistenceFinderByC_C;

	/**
	 * Returns the web dav props where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchWebDAVPropsException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching web dav props
	 * @throws NoSuchWebDAVPropsException if a matching web dav props could not be found
	 */
	@Override
	public WebDAVProps findByC_C(long classNameId, long classPK)
		throws NoSuchWebDAVPropsException {

		return _uniquePersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the web dav props where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching web dav props, or <code>null</code> if a matching web dav props could not be found
	 */
	@Override
	public WebDAVProps fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the web dav props where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the web dav props that was removed
	 */
	@Override
	public WebDAVProps removeByC_C(long classNameId, long classPK)
		throws NoSuchWebDAVPropsException {

		WebDAVProps webDAVProps = findByC_C(classNameId, classPK);

		return remove(webDAVProps);
	}

	/**
	 * Returns the number of web dav propses where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching web dav propses
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	public WebDAVPropsPersistenceImpl() {
		setModelClass(WebDAVProps.class);

		setModelImplClass(WebDAVPropsImpl.class);
		setModelPKClass(long.class);

		setTable(WebDAVPropsTable.INSTANCE);
	}

	/**
	 * Creates a new web dav props with the primary key. Does not add the web dav props to the database.
	 *
	 * @param webDavPropsId the primary key for the new web dav props
	 * @return the new web dav props
	 */
	@Override
	public WebDAVProps create(long webDavPropsId) {
		WebDAVProps webDAVProps = new WebDAVPropsImpl();

		webDAVProps.setNew(true);
		webDAVProps.setPrimaryKey(webDavPropsId);

		webDAVProps.setCompanyId(CompanyThreadLocal.getCompanyId());

		return webDAVProps;
	}

	/**
	 * Removes the web dav props with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param webDavPropsId the primary key of the web dav props
	 * @return the web dav props that was removed
	 * @throws NoSuchWebDAVPropsException if a web dav props with the primary key could not be found
	 */
	@Override
	public WebDAVProps remove(long webDavPropsId)
		throws NoSuchWebDAVPropsException {

		return remove((Serializable)webDavPropsId);
	}

	@Override
	protected WebDAVProps removeImpl(WebDAVProps webDAVProps) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(webDAVProps)) {
				webDAVProps = (WebDAVProps)session.get(
					WebDAVPropsImpl.class, webDAVProps.getPrimaryKeyObj());
			}

			if (webDAVProps != null) {
				session.delete(webDAVProps);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (webDAVProps != null) {
			clearCache(webDAVProps);
		}

		return webDAVProps;
	}

	@Override
	public WebDAVProps updateImpl(WebDAVProps webDAVProps) {
		boolean isNew = webDAVProps.isNew();

		if (!(webDAVProps instanceof WebDAVPropsModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(webDAVProps.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(webDAVProps);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in webDAVProps proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WebDAVProps implementation " +
					webDAVProps.getClass());
		}

		WebDAVPropsModelImpl webDAVPropsModelImpl =
			(WebDAVPropsModelImpl)webDAVProps;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (webDAVProps.getCreateDate() == null)) {
			if (serviceContext == null) {
				webDAVProps.setCreateDate(date);
			}
			else {
				webDAVProps.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!webDAVPropsModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				webDAVProps.setModifiedDate(date);
			}
			else {
				webDAVProps.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(webDAVProps);
			}
			else {
				webDAVProps = (WebDAVProps)session.merge(webDAVProps);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(webDAVProps, false);

		if (isNew) {
			webDAVProps.setNew(false);
		}

		webDAVProps.resetOriginalValues();

		return webDAVProps;
	}

	/**
	 * Returns the web dav props with the primary key or throws a <code>NoSuchWebDAVPropsException</code> if it could not be found.
	 *
	 * @param webDavPropsId the primary key of the web dav props
	 * @return the web dav props
	 * @throws NoSuchWebDAVPropsException if a web dav props with the primary key could not be found
	 */
	@Override
	public WebDAVProps findByPrimaryKey(long webDavPropsId)
		throws NoSuchWebDAVPropsException {

		return findByPrimaryKey((Serializable)webDavPropsId);
	}

	/**
	 * Returns the web dav props with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param webDavPropsId the primary key of the web dav props
	 * @return the web dav props, or <code>null</code> if a web dav props with the primary key could not be found
	 */
	@Override
	public WebDAVProps fetchByPrimaryKey(long webDavPropsId) {
		return fetchByPrimaryKey((Serializable)webDavPropsId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "webDavPropsId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WEBDAVPROPS;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return WebDAVPropsModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the web dav props persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				WebDAVProps::getClassNameId, WebDAVProps::getClassPK),
			_SQL_SELECT_WEBDAVPROPS_WHERE, "",
			new FinderColumn<>(
				"webDAVProps.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, WebDAVProps::getClassNameId),
			new FinderColumn<>(
				"webDAVProps.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, WebDAVProps::getClassPK));

		WebDAVPropsUtil.setPersistence(this);
	}

	public void destroy() {
		WebDAVPropsUtil.setPersistence(null);

		EntityCacheUtil.removeCache(WebDAVPropsImpl.class.getName());
	}

	private static final String _SQL_SELECT_WEBDAVPROPS =
		"SELECT webDAVProps FROM WebDAVProps webDAVProps";

	private static final String _SQL_SELECT_WEBDAVPROPS_WHERE =
		"SELECT webDAVProps FROM WebDAVProps webDAVProps WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2079980492