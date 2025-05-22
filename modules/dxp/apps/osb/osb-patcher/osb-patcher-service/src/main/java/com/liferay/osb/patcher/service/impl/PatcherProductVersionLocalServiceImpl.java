/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.service.base.PatcherProductVersionLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherProductVersionNameComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherProductVersion",
	service = AopService.class
)
public class PatcherProductVersionLocalServiceImpl
	extends PatcherProductVersionLocalServiceBaseImpl {

	@Override
	public PatcherProductVersion fetchPatcherProductVersion(String name) {
		return patcherProductVersionPersistence.fetchByName(name);
	}

	@Override
	public List<PatcherProductVersion> getPatcherProductVersions() {
		return patcherProductVersionPersistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherProductVersionNameComparator.getInstance(true));
	}

	@Override
	public List<PatcherProductVersion> getPatcherProductVersions(
		int fixDeliveryMethod) {

		return patcherProductVersionPersistence.findByFixDeliveryMethod(
			fixDeliveryMethod);
	}

}