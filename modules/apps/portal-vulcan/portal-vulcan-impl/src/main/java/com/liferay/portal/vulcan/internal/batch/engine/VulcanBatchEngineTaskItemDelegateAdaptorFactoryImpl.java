/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateAdaptorFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = VulcanBatchEngineTaskItemDelegateAdaptorFactory.class)
public class VulcanBatchEngineTaskItemDelegateAdaptorFactoryImpl
	implements VulcanBatchEngineTaskItemDelegateAdaptorFactory {

	@Override
	public <T> BatchEngineTaskItemDelegate<T> create(
		VulcanBatchEngineTaskItemDelegate<T>
			vulcanBatchEngineTaskItemDelegate) {

		return new VulcanBatchEngineTaskItemDelegateAdaptor<>(
			_depotEntryLocalService, _groupLocalService,
			_resourceActionLocalService, _resourcePermissionLocalService,
			_roleLocalService, vulcanBatchEngineTaskItemDelegate);
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}