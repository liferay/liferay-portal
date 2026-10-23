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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ImageTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.ImagePersistence;
import com.liferay.portal.kernel.service.persistence.ImageUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.ImageImpl;
import com.liferay.portal.model.impl.ImageModelImpl;

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
import java.util.Set;

/**
 * The persistence implementation for the image service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ImagePersistenceImpl
	extends BasePersistenceImpl<Image, NoSuchImageException>
	implements ImagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ImageUtil</code> to access the image persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ImageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public ImagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Image.class);

		setModelImplClass(ImageImpl.class);
		setModelPKClass(long.class);

		setTable(ImageTable.INSTANCE);
	}

	/**
	 * Creates a new image with the primary key. Does not add the image to the database.
	 *
	 * @param imageId the primary key for the new image
	 * @return the new image
	 */
	@Override
	public Image create(long imageId) {
		Image image = new ImageImpl();

		image.setNew(true);
		image.setPrimaryKey(imageId);

		image.setCompanyId(CompanyThreadLocal.getCompanyId());

		return image;
	}

	/**
	 * Removes the image with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param imageId the primary key of the image
	 * @return the image that was removed
	 * @throws NoSuchImageException if a image with the primary key could not be found
	 */
	@Override
	public Image remove(long imageId) throws NoSuchImageException {
		return remove((Serializable)imageId);
	}

	@Override
	protected Image removeImpl(Image image) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(image)) {
				image = (Image)session.get(
					ImageImpl.class, image.getPrimaryKeyObj());
			}

			if ((image != null) && CTPersistenceHelperUtil.isRemove(image)) {
				session.delete(image);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (image != null) {
			clearCache(image);
		}

		return image;
	}

	@Override
	public Image updateImpl(Image image) {
		boolean isNew = image.isNew();

		if (!(image instanceof ImageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(image.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(image);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in image proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Image implementation " +
					image.getClass());
		}

		ImageModelImpl imageModelImpl = (ImageModelImpl)image;

		if (!imageModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				image.setModifiedDate(date);
			}
			else {
				image.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(image)) {
				if (!isNew) {
					session.evict(ImageImpl.class, image.getPrimaryKeyObj());
				}

				session.save(image);
			}
			else {
				image = (Image)session.merge(image);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(image, false);

		if (isNew) {
			image.setNew(false);
		}

		image.resetOriginalValues();

		return image;
	}

	/**
	 * Returns the image with the primary key or throws a <code>NoSuchImageException</code> if it could not be found.
	 *
	 * @param imageId the primary key of the image
	 * @return the image
	 * @throws NoSuchImageException if a image with the primary key could not be found
	 */
	@Override
	public Image findByPrimaryKey(long imageId) throws NoSuchImageException {
		return findByPrimaryKey((Serializable)imageId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the image with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param imageId the primary key of the image
	 * @return the image, or <code>null</code> if a image with the primary key could not be found
	 */
	@Override
	public Image fetchByPrimaryKey(long imageId) {
		return fetchByPrimaryKey((Serializable)imageId);
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
		return "imageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_IMAGE;
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
		return ImageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Image";
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
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("height");
		ctMergeColumnNames.add("width");
		ctMergeColumnNames.add("size_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("imageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the image persistence.
	 */
	public void afterPropertiesSet() {
		ImageUtil.setPersistence(this);
	}

	public void destroy() {
		ImageUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ImageImpl.class.getName());
	}

	private static final String _SQL_SELECT_IMAGE =
		"SELECT image FROM Image image";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1325649150