/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate.event;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.transactional.TransactionalPortalCacheUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;

import java.io.Serializable;

import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * @author Shuyang Zhou
 */
public class MVCCSynchronizerPostUpdateEventListener
	implements PostUpdateEventListener {

	public static final MVCCSynchronizerPostUpdateEventListener INSTANCE =
		new MVCCSynchronizerPostUpdateEventListener();

	@Override
	public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
		Object entity = postUpdateEvent.getEntity();

		if (entity instanceof MVCCModel) {
			long ctCollectionId =
				CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION;

			if (entity instanceof CTModel<?>) {
				CTModel<?> ctModel = (CTModel<?>)entity;

				ctCollectionId = ctModel.getCtCollectionId();
			}

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollectionId)) {

				Class<?> modelClass = entity.getClass();

				PortalCache<Serializable, Serializable> portalCache =
					EntityCacheUtil.getPortalCache(modelClass);

				if (portalCache == null) {
					return;
				}

				MVCCModel mvccModel = (MVCCModel)entity;

				long mvccVersion = mvccModel.getMvccVersion();

				BaseModel<?> baseModel = (BaseModel<?>)entity;

				Serializable primaryKeyObj = baseModel.getPrimaryKeyObj();

				boolean[] uncommittedBufferMissMarker = {false};

				Serializable entityCacheResult =
					TransactionalPortalCacheUtil.get(
						portalCache, primaryKeyObj,
						uncommittedBufferMissMarker);

				if ((entityCacheResult instanceof
						MVCCModel entityCacheMVCCModel) &&
					(mvccVersion > entityCacheMVCCModel.getMvccVersion())) {

					if (uncommittedBufferMissMarker[0]) {
						entityCacheMVCCModel = ReflectionUtil.clone(
							entityCacheMVCCModel);

						entityCacheMVCCModel.setMvccVersion(mvccVersion);

						portalCache.put(
							primaryKeyObj, (Serializable)entityCacheMVCCModel);
					}
					else {
						entityCacheMVCCModel.setMvccVersion(mvccVersion);
					}
				}
			}
		}
	}

	/** @deprecated */
	@Deprecated
	@Override
	public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
		throw new UnsupportedOperationException();
	}

}