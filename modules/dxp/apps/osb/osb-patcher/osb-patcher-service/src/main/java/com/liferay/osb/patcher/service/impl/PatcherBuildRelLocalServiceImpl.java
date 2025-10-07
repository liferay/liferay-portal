/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.osb.patcher.service.base.PatcherBuildRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherBuildRel",
	service = AopService.class
)
public class PatcherBuildRelLocalServiceImpl
	extends PatcherBuildRelLocalServiceBaseImpl {

	@Override
	public PatcherBuildRel addPatcherBuildRel(
		long childPatcherBuildId, long parentPatcherBuildId) {

		long patcherBuildRelId = counterLocalService.increment();

		PatcherBuildRel patcherBuildRel = patcherBuildRelPersistence.create(
			patcherBuildRelId);

		patcherBuildRel.setChildPatcherBuildId(childPatcherBuildId);
		patcherBuildRel.setParentPatcherBuildId(parentPatcherBuildId);

		return patcherBuildRel;
	}

	@Override
	public List<PatcherBuildRel> getPatcherBuildRelsByChildPatcherBuildId(
		long childPatcherBuildId) {

		return patcherBuildRelPersistence.findByChildPatcherBuildId(
			childPatcherBuildId);
	}

	@Override
	public int getPatcherBuildRelsByChildPatcherBuildIdCount(
		long childPatcherBuildId) {

		return patcherBuildRelPersistence.countByChildPatcherBuildId(
			childPatcherBuildId);
	}

	@Override
	public List<PatcherBuildRel> getPatcherBuildRelsByParentPatcherBuildId(
		long parentPatcherBuildId) {

		return patcherBuildRelPersistence.findByParentPatcherBuildId(
			parentPatcherBuildId);
	}

	@Override
	public int getPatcherBuildRelsByParentPatcherBuildIdCount(
		long parentPatcherBuildId) {

		return patcherBuildRelPersistence.countByParentPatcherBuildId(
			parentPatcherBuildId);
	}

}