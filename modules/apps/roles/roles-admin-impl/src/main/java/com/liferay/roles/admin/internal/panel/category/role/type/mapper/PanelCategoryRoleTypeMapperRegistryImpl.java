/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.internal.panel.category.role.type.mapper;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.roles.admin.panel.category.role.type.mapper.PanelCategoryRoleTypeMapper;
import com.liferay.roles.admin.panel.category.role.type.mapper.PanelCategoryRoleTypeMapperRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Jiaxu Wei
 */
@Component(service = PanelCategoryRoleTypeMapperRegistry.class)
public class PanelCategoryRoleTypeMapperRegistryImpl
	implements PanelCategoryRoleTypeMapperRegistry {

	@Override
	public String[] getPanelCategoryKeys(int type) {
		Set<String> panelCategoryKeys = new HashSet<>();

		for (PanelCategoryRoleTypeMapper panelCategoryRoleTypeMapper :
				_serviceTrackerList) {

			if (!ArrayUtil.contains(
					panelCategoryRoleTypeMapper.getRoleTypes(), type)) {

				continue;
			}

			panelCategoryKeys.add(
				panelCategoryRoleTypeMapper.getPanelCategoryKey());
		}

		return panelCategoryKeys.toArray(new String[0]);
	}

	@Override
	public List<PanelCategoryRoleTypeMapper> getPanelCategoryRoleTypeMappers() {
		return _serviceTrackerList.toList();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, PanelCategoryRoleTypeMapper.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList<PanelCategoryRoleTypeMapper> _serviceTrackerList;

}