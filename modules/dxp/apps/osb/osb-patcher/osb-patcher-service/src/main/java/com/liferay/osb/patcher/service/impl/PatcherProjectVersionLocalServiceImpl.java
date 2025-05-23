/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.base.PatcherProjectVersionLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherProjectVersionNameComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherProjectVersion",
	service = AopService.class
)
public class PatcherProjectVersionLocalServiceImpl
	extends PatcherProjectVersionLocalServiceBaseImpl {

	@Override
	public PatcherProjectVersion fetchPatcherProjectVersionByCommittish(
		String committish) {

		return patcherProjectVersionPersistence.fetchByCommittish(committish);
	}

	@Override
	public PatcherProjectVersion fetchPatcherProjectVersionByName(String name) {
		return patcherProjectVersionPersistence.fetchByName(name);
	}

	@Override
	public PatcherProjectVersion getPatcherProjectVersionByName(String name)
		throws PortalException {

		return patcherProjectVersionPersistence.findByName(name);
	}

	@Override
	public List<PatcherProjectVersion> getPatcherProjectVersions() {
		return patcherProjectVersionPersistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherProjectVersionNameComparator.getInstance(true));
	}

	@Override
	public List<PatcherProjectVersion> getPatcherProjectVersions(
		long patcherProductVersionId) {

		return patcherProjectVersionPersistence.findByPatcherProductVersionId(
			patcherProductVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherProjectVersionNameComparator.getInstance(true));
	}

	@Override
	public List<PatcherProjectVersion> getPatcherProjectVersions(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return patcherProjectVersionPersistence.findByP_RN(
			patcherProductVersionId, repositoryName, start, end,
			orderByComparator);
	}

	@Override
	public List<PatcherProjectVersion> getRootPatcherProjectVersions() {
		return patcherProjectVersionPersistence.
			findByRootPatcherProjectVersionId(
				0L, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				PatcherProjectVersionNameComparator.getInstance(true));
	}

	@Override
	public List<PatcherProjectVersion> getRootPatcherProjectVersions(
		long patcherProductVersionId) {

		return patcherProjectVersionPersistence.findByP_R(
			0L, patcherProductVersionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherProjectVersionNameComparator.getInstance(true));
	}

}