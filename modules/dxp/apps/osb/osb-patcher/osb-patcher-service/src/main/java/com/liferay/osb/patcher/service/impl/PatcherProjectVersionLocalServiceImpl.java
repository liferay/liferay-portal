/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalService;
import com.liferay.osb.patcher.service.base.PatcherProjectVersionLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherProjectVersionNameComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherProjectVersion",
	service = AopService.class
)
public class PatcherProjectVersionLocalServiceImpl
	extends PatcherProjectVersionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherProjectVersion addPatcherProjectVersion(
			long userId, long patcherProductVersionId,
			long rootPatcherProjectVersionId, boolean combinedBranch,
			String committish, String fixedIssues, boolean hide, String name,
			String repositoryName)
		throws PortalException {

		_validateProductVersion(patcherProductVersionId);
		_validateCommittish(committish);
		_validateName(0, name);
		_validateRepositoryName(repositoryName);

		PatcherProjectVersion patcherProjectVersion =
			patcherProjectVersionPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		patcherProjectVersion.setCompanyId(user.getCompanyId());
		patcherProjectVersion.setUserId(user.getUserId());
		patcherProjectVersion.setUserName(user.getFullName());

		patcherProjectVersion.setCreateDate(new Date());
		patcherProjectVersion.setModifiedDate(new Date());
		patcherProjectVersion.setPatcherProductVersionId(
			patcherProductVersionId);
		patcherProjectVersion.setRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);
		patcherProjectVersion.setCombinedBranch(combinedBranch);
		patcherProjectVersion.setCommittish(committish);
		patcherProjectVersion.setFixedIssues(fixedIssues);
		patcherProjectVersion.setHide(hide);
		patcherProjectVersion.setName(name);
		patcherProjectVersion.setRepositoryName(repositoryName);

		return patcherProjectVersionPersistence.update(patcherProjectVersion);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public PatcherProjectVersion deletePatcherProjectVersion(
			long patcherProjectVersionId)
		throws PortalException {

		PatcherProjectVersion patcherProjectVersion =
			patcherProjectVersionPersistence.findByPrimaryKey(
				patcherProjectVersionId);

		_validateAssociatedPatcherBuilds(patcherProjectVersion);
		_validateAssociatedPatcherFixes(patcherProjectVersion);

		return patcherProjectVersionPersistence.remove(patcherProjectVersion);
	}

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

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherProjectVersion updatePatcherProjectVersion(
			long patcherProjectVersionId, long patcherProductVersionId,
			boolean combinedBranch, String committish, String fixedIssues,
			boolean hide, String repositoryName)
		throws PortalException {

		_validateProductVersion(patcherProductVersionId);
		_validateCommittish(committish);
		_validateRepositoryName(repositoryName);

		PatcherProjectVersion patcherProjectVersion =
			patcherProjectVersionPersistence.findByPrimaryKey(
				patcherProjectVersionId);

		patcherProjectVersion.setModifiedDate(new Date());
		patcherProjectVersion.setPatcherProductVersionId(
			patcherProductVersionId);
		patcherProjectVersion.setCombinedBranch(combinedBranch);
		patcherProjectVersion.setCommittish(committish);
		patcherProjectVersion.setFixedIssues(fixedIssues);
		patcherProjectVersion.setHide(hide);
		patcherProjectVersion.setRepositoryName(repositoryName);

		return patcherProjectVersionPersistence.update(patcherProjectVersion);
	}

	private void _validateAssociatedPatcherBuilds(
			PatcherProjectVersion patcherProjectVersion)
		throws PortalException {

		long patcherBuildCount =
			_patcherBuildLocalService.
				getPatcherBuildsCountByPatcherProjectVersionId(
					patcherProjectVersion.getPatcherProjectVersionId());

		if (patcherBuildCount > 0) {
			throw new PortalException(
				"the-project-version-cannot-be-deleted-because-it-has-" +
					"associated-builds");
		}
	}

	private void _validateAssociatedPatcherFixes(
			PatcherProjectVersion patcherProjectVersion)
		throws PortalException {

		long patcherFixCount =
			_patcherFixLocalService.
				getPatcherFixesCountByPatcherProjectVersionId(
					patcherProjectVersion.getPatcherProjectVersionId());

		if (patcherFixCount > 0) {
			throw new PortalException(
				"the-project-version-cannot-be-deleted-because-it-has-" +
					"associated-fixes");
		}
	}

	private void _validateCommittish(String committish) throws PortalException {
		if (Validator.isNull(committish)) {
			throw new PortalException("the-tag-name-is-invalid");
		}
	}

	private void _validateName(long patcherProjectVersionId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new PortalException("the-project-version-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion =
			patcherProjectVersionPersistence.fetchByName(name);

		if ((patcherProjectVersion != null) &&
			(patcherProjectVersion.getPatcherProjectVersionId() !=
				patcherProjectVersionId)) {

			throw new PortalException(
				"the-project-version-name-already-exists");
		}
	}

	private void _validateProductVersion(long patcherProductVersionId)
		throws PortalException {

		PatcherProductVersion patcherProductVersion =
			_patcherProductVersionLocalService.fetchPatcherProductVersion(
				patcherProductVersionId);

		if (patcherProductVersion == null) {
			throw new PortalException("the-product-version-id-is-invalid");
		}
	}

	private void _validateRepositoryName(String repositoryName)
		throws PortalException {

		if (Validator.isNull(repositoryName)) {
			throw new PortalException("the-repository-name-is-invalid");
		}
	}

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

	@Reference
	private PatcherProductVersionLocalService
		_patcherProductVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}