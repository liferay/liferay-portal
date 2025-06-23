/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.service.base.PatcherFixPackLocalServiceBaseImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentPersistence;
import com.liferay.osb.patcher.util.comparator.PatcherFixPackVersionComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherFixPack",
	service = AopService.class
)
public class PatcherFixPackLocalServiceImpl
	extends PatcherFixPackLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFixPack addPatcherFixPack(
			long userId, long patcherFixComponentId,
			long patcherProjectVersionId, int version, int status)
		throws PortalException {

		_validateAdd(patcherFixComponentId, version);

		PatcherFixPack patcherFixPack = patcherFixPackPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		patcherFixPack.setCompanyId(user.getCompanyId());
		patcherFixPack.setUserId(user.getUserId());
		patcherFixPack.setUserName(user.getFullName());

		patcherFixPack.setCreateDate(new Date());
		patcherFixPack.setModifiedDate(new Date());
		patcherFixPack.setPatcherFixComponentId(patcherFixComponentId);
		patcherFixPack.setPatcherProjectVersionId(patcherProjectVersionId);

		PatcherFixComponent patcherFixComponent =
			_patcherFixComponentPersistence.fetchByPrimaryKey(
				patcherFixComponentId);

		PatcherFixPack oldPatcherFixPack =
			patcherFixPackPersistence.fetchByPFCI_PPVI_First(
				patcherFixComponentId, patcherProjectVersionId,
				PatcherFixPackVersionComparator.getInstance(false));

		if (oldPatcherFixPack != null) {
			version = oldPatcherFixPack.getVersion() + 1;
		}

		patcherFixPack.setName(
			patcherFixComponent.getName() + StringPool.DASH + version);
		patcherFixPack.setVersion(version);

		patcherFixPack.setStatus(status);

		return patcherFixPackPersistence.update(patcherFixPack);
	}

	@Override
	public PatcherFixPack fetchPatcherFixPack(
		long patcherProjectVersionId, String name) {

		return patcherFixPackPersistence.fetchByPFCI_N(
			patcherProjectVersionId, name);
	}

	@Override
	public PatcherFixPack fetchPatcherFixPackByPatcherBuildId(
		long patcherBuildId) {

		return patcherFixPackPersistence.fetchByPatcherBuildId(patcherBuildId);
	}

	@Override
	public PatcherFixPack getPatcherFixPack(
			long patcherProjectVersionId, String name)
		throws PortalException {

		return patcherFixPackPersistence.findByPFCI_N(
			patcherProjectVersionId, name);
	}

	@Override
	public PatcherFixPack getPatcherFixPackByPatcherBuildId(long patcherBuildId)
		throws PortalException {

		return patcherFixPackPersistence.findByPatcherBuildId(patcherBuildId);
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacks(
		long patcherFixComponentId, int version) {

		return patcherFixPackPersistence.findByPFCI_V(
			patcherFixComponentId, version);
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacks(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		boolean older) {

		if (older) {
			return patcherFixPackPersistence.findByPFCI_PPVI_LtV(
				patcherFixComponentId, patcherProjectVersionId, version,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				PatcherFixPackVersionComparator.getInstance(false));
		}

		return patcherFixPackPersistence.findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherFixPackVersionComparator.getInstance(true));
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacksByPatcherFixComponentId(
		long patcherFixComponentId) {

		return patcherFixPackPersistence.findByPatcherFixComponentId(
			patcherFixComponentId);
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacksByStatus(
		long patcherProjectVersionId, int status) {

		return patcherFixPackPersistence.findByPFCI_S(
			patcherProjectVersionId, status);
	}

	private void _validateAdd(long patcherFixComponentId, int version)
		throws PortalException {

		PatcherFixComponent patcherFixComponent =
			_patcherFixComponentPersistence.fetchByPrimaryKey(
				patcherFixComponentId);

		if (patcherFixComponent == null) {
			throw new PortalException("the-fix-component-is-invalid");
		}

		if (version < 1) {
			throw new PortalException(
				"the-fix-pack-version-must-be-greater-than-zero");
		}
	}

	@Reference
	private PatcherFixComponentPersistence _patcherFixComponentPersistence;

	@Reference
	private UserLocalService _userLocalService;

}