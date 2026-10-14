/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.sync.service.impl;

import com.liferay.document.library.sync.model.DLSyncEvent;
import com.liferay.document.library.sync.service.base.DLSyncEventLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dennis Ju
 */
@Component(
	property = "model.class.name=com.liferay.document.library.sync.model.DLSyncEvent",
	service = AopService.class
)
public class DLSyncEventLocalServiceImpl
	extends DLSyncEventLocalServiceBaseImpl {

	@Override
	public DLSyncEvent addDLSyncEvent(String event, String type, long typePK) {
		DLSyncEvent dlSyncEvent = dlSyncEventPersistence.fetchByTypePK(typePK);

		if (dlSyncEvent == null) {
			long dlSyncEventId = counterLocalService.increment();

			dlSyncEvent = dlSyncEventPersistence.create(dlSyncEventId);

			dlSyncEvent.setType(type);
			dlSyncEvent.setTypePK(typePK);
		}

		dlSyncEvent.setModifiedTime(System.currentTimeMillis());
		dlSyncEvent.setEvent(event);

		return dlSyncEventPersistence.update(dlSyncEvent);
	}

	@Override
	public void deleteDLSyncEvents() {
		dlSyncEventPersistence.removeAll();
	}

	@Override
	public List<DLSyncEvent> getLatestDLSyncEvents() {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DLSyncEvent.class, getClassLoader());

		Property property = PropertyFactoryUtil.forName("modifiedTime");

		DynamicQuery modifiedTimeDynamicQuery =
			DynamicQueryFactoryUtil.forClass(
				DLSyncEvent.class, getClassLoader());

		modifiedTimeDynamicQuery.setProjection(
			ProjectionFactoryUtil.max("modifiedTime"));

		dynamicQuery.add(property.eq(modifiedTimeDynamicQuery));

		return dlSyncEventPersistence.findWithDynamicQuery(dynamicQuery);
	}

}