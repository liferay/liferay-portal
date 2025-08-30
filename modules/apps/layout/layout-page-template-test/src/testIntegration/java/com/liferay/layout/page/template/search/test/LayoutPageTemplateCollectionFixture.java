/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.search.test;

import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roselaine Marques
 */
public class LayoutPageTemplateCollectionFixture {

	public LayoutPageTemplateCollectionFixture(
		Group group,
		LayoutPageTemplateCollectionLocalService
			layoutPageTemplateCollectionLocalService) {

		_group = group;
		_layoutPageTemplateCollectionLocalService =
			layoutPageTemplateCollectionLocalService;
	}

	public LayoutPageTemplateCollection createLayoutPageTemplateCollection()
		throws PortalException {

		return createLayoutPageTemplateCollection(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	public LayoutPageTemplateCollection createLayoutPageTemplateCollection(
			String name)
		throws PortalException {

		return createLayoutPageTemplateCollection(name, RandomTestUtil.randomString());
	}

	public LayoutPageTemplateCollection createLayoutPageTemplateCollection(
			String name, String description)
		throws PortalException {

		ServiceContext serviceContext = _getServiceContext();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.addLayoutPageTemplateCollection(
				RandomTestUtil.randomString(), serviceContext.getUserId(),
				_group.getGroupId(), 0, RandomTestUtil.randomString(), name,
				description, LayoutPageTemplateCollectionTypeConstants.BASIC,
				serviceContext);

		_layoutPageTemplateCollections.add(layoutPageTemplateCollection);

		return layoutPageTemplateCollection;
	}

	public List<LayoutPageTemplateCollection>
		getLayoutPageTemplateCollections() {

		return _layoutPageTemplateCollections;
	}

	private ServiceContext _getServiceContext() {
		try {
			return ServiceContextTestUtil.getServiceContext(
				_group.getGroupId());
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private final Group _group;
	private final LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;
	private final List<LayoutPageTemplateCollection> _layoutPageTemplateCollections =
		new ArrayList<>();

}