/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.service.base.PatcherFixPackLocalServiceBaseImpl;
import com.liferay.osb.patcher.service.persistence.PatcherBuildPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentPersistence;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherFixPackUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.osb.patcher.util.comparator.PatcherFixPackVersionComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFixPack updatePatcherFixPack(
			long patcherFixPackId, String requirements, int status)
		throws Exception {

		PatcherFixPack patcherFixPack =
			patcherFixPackPersistence.findByPrimaryKey(patcherFixPackId);

		_validateUpdate(patcherFixPack, requirements, status);

		patcherFixPack.setModifiedDate(new Date());

		if ((patcherFixPack.getStatus() ==
				WorkflowConstants.STATUS_FIX_PACK_FROZEN) &&
			(status == WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT)) {

			patcherFixPack.setPatcherBuildId(0);
		}
		else if ((patcherFixPack.getStatus() ==
					WorkflowConstants.STATUS_FIX_PACK_FROZEN) &&
				 (status == WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {

			patcherFixPack.setReleasedDate(new Date());
		}

		patcherFixPack.setRequirements(requirements);

		return patcherFixPackPersistence.update(patcherFixPack);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFixPack updatePatcherFixPack(PatcherFixPack patcherFixPack) {
		return super.updatePatcherFixPack(patcherFixPack);
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

	private void _validateFrozenFixPack(PatcherFixPack patcherFixPack)
		throws Exception {

		_validatePreviousPatcherFixPacks(patcherFixPack);

		List<String> pendingPatcherFixPackNames = new ArrayList<>();

		Set<PatcherFixPack> prerequisitePatcherFixPacks =
			PatcherFixPackUtil.getPrerequisitePatcherFixPacks(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFixPack prerequisitePatcherFixPack :
				prerequisitePatcherFixPacks) {

			if ((prerequisitePatcherFixPack.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_FROZEN) &&
				(prerequisitePatcherFixPack.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {

				pendingPatcherFixPackNames.add(
					prerequisitePatcherFixPack.getName());
			}
		}

		if (!pendingPatcherFixPackNames.isEmpty()) {
			String patcherFixPacks = StringUtil.merge(
				PatcherUtil.sortTokens(pendingPatcherFixPackNames));

			throw new PortalException(
				LanguageUtil.format(
					LocaleUtil.getMostRelevantLocale(),
					"the-fix-pack-cannot-be-frozen-as-it-depends-on-the-" +
						"following-fix-packs-that-need-to-be-frozen-first-x",
					patcherFixPacks));
		}
	}

	private void _validatePreviousPatcherFixPacks(PatcherFixPack patcherFixPack)
		throws Exception {

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() ==
					WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {

				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-no-" +
						"longer-be-under-development");
			}

			PatcherBuild patcherBuild =
				_patcherBuildPersistence.fetchByPrimaryKey(
					patcherFixPackVersion.getPatcherBuildId());

			if (patcherBuild == null) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}

			PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
				patcherBuild.getPatcherFixId());

			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}
		}
	}

	private void _validateReleaseFixPack(PatcherFixPack patcherFixPack)
		throws Exception {

		PatcherBuild patcherBuild = _patcherBuildPersistence.fetchByPrimaryKey(
			patcherFixPack.getPatcherBuildId());

		if (patcherBuild == null) {
			throw new PortalException(
				"the-fix-pack-must-complete-merging-before-release");
		}

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherBuild.getPatcherFixId());

		if (Validator.isNull(patcherFix.getGitHash())) {
			throw new PortalException(
				"the-fix-pack-must-complete-merging-before-release");
		}

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_RELEASED) {

				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"already-be-released");
			}

			patcherBuild = _patcherBuildPersistence.fetchByPrimaryKey(
				patcherFixPackVersion.getPatcherBuildId());

			if (patcherBuild == null) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}

			patcherFix = patcherFixPersistence.findByPrimaryKey(
				patcherBuild.getPatcherFixId());

			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}
		}

		List<String> pendingPatcherFixPackNames = new ArrayList<>();

		Set<PatcherFixPack> prerequisitePatcherFixPacks =
			PatcherFixPackUtil.getPrerequisitePatcherFixPacks(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFixPack prerequisitePatcherFixPack :
				prerequisitePatcherFixPacks) {

			if (prerequisitePatcherFixPack.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_RELEASED) {

				pendingPatcherFixPackNames.add(
					prerequisitePatcherFixPack.getName());
			}
		}

		if (!pendingPatcherFixPackNames.isEmpty()) {
			String patcherFixPacks = StringUtil.merge(
				PatcherUtil.sortTokens(pendingPatcherFixPackNames));

			throw new PortalException(
				LanguageUtil.format(
					LocaleUtil.getMostRelevantLocale(),
					"the-fix-pack-cannot-be-released-as-it-depends-on-the-" +
						"following-fix-packs-that-need-to-be-released-first-x",
					patcherFixPacks));
		}
	}

	private void _validateRequirements(String requirements) throws Exception {
		for (String requirement :
				ListUtil.fromArray(StringUtil.split(requirements))) {

			if (!JenkinsUtil.isValidJenkinsRequirement(requirement)) {
				throw new PortalException(
					"the-fix-pack's-requirement-is-invalid");
			}
		}
	}

	private void _validateUnderDevelopmentFixPack(PatcherFixPack patcherFixPack)
		throws Exception {

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, false);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {

				throw new PortalException(
					"this-fix-pack-cannot-be-under-development-because-a-" +
						"newer-fix-pack-version-is-frozen");
			}
		}
	}

	private void _validateUpdate(
			PatcherFixPack patcherFixPack, String requirements, int status)
		throws Exception {

		_validateRequirements(requirements);

		if ((patcherFixPack.getStatus() !=
				WorkflowConstants.STATUS_FIX_PACK_FROZEN) &&
			(status == WorkflowConstants.STATUS_FIX_PACK_FROZEN)) {

			_validateFrozenFixPack(patcherFixPack);
		}

		if ((patcherFixPack.getStatus() !=
				WorkflowConstants.STATUS_FIX_PACK_RELEASED) &&
			(status == WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {

			_validateReleaseFixPack(patcherFixPack);
		}

		if ((patcherFixPack.getStatus() !=
				WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) &&
			(status == WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT)) {

			_validateUnderDevelopmentFixPack(patcherFixPack);
		}
	}

	@Reference
	private PatcherBuildPersistence _patcherBuildPersistence;

	@Reference
	private PatcherFixComponentPersistence _patcherFixComponentPersistence;

	@Reference
	private UserLocalService _userLocalService;

}