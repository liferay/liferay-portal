/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.service.base.PatcherFixComponentLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherFixComponentNameComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherFixComponent",
	service = AopService.class
)
public class PatcherFixComponentLocalServiceImpl
	extends PatcherFixComponentLocalServiceBaseImpl {

	@Override
	public PatcherFixComponent fetchPatcherFixComponent(String name) {
		return patcherFixComponentPersistence.fetchByName(name);
	}

	@Override
	public List<PatcherFixComponent> getPatcherFixComponents() {
		return patcherFixComponentPersistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherFixComponentNameComparator.getInstance(true));
	}

}