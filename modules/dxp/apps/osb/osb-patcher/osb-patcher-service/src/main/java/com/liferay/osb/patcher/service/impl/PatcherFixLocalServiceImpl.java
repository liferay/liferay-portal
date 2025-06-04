/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.base.PatcherFixLocalServiceBaseImpl;
import com.liferay.osb.patcher.service.persistence.PatcherProductVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionPersistence;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.osb.patcher.util.PatcherProjectVersionUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.osb.patcher.util.comparator.PatcherFixKeyVersionComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherFix",
	service = AopService.class
)
public class PatcherFixLocalServiceImpl extends PatcherFixLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix addPatcherFix(
			long userId, long patcherProductVersionId,
			long patcherProjectVersionId, String name, String committish,
			String gitRemoteURL, int type, int status)
		throws Exception {

		_validateAdd(
			committish, gitRemoteURL, name, patcherProductVersionId,
			patcherProjectVersionId);

		PatcherFix patcherFix = patcherFixPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		patcherFix.setCompanyId(user.getCompanyId());
		patcherFix.setUserId(user.getUserId());
		patcherFix.setUserName(user.getFullName());

		patcherFix.setCreateDate(new Date());
		patcherFix.setModifiedDate(new Date());
		patcherFix.setPatcherProductVersionId(patcherProductVersionId);
		patcherFix.setPatcherProjectVersionId(patcherProjectVersionId);
		patcherFix.setName(
			StringUtil.merge(PatcherUtil.sortTokens(name), StringPool.COMMA));
		patcherFix.setKey(_generateKey(patcherProjectVersionId, name));
		patcherFix.setKeyVersion(PatcherFixConstants.KEY_VERSION_DEFAULT);
		patcherFix.setType(type);
		patcherFix.setLatestFix(true);
		patcherFix.setObsolete(false);
		patcherFix.setCommittish(committish);
		patcherFix.setGitRemoteURL(gitRemoteURL);
		patcherFix.setStatus(status);
		patcherFix.setStatusByUserId(user.getUserId());
		patcherFix.setStatusByUserName(user.getFullName());
		patcherFix.setStatusDate(new Date());

		patcherFix = patcherFixPersistence.update(patcherFix);

		List<PatcherBuild> patcherBuilds =
			patcherBuildPersistence.getPatcherFixPatcherBuilds(
				patcherFix.getPatcherFixId());

		for (PatcherBuild patcherBuild : patcherBuilds) {
			List<PatcherFix> incompletePatcherFixes =
				PatcherBuildUtil.getIncompletePatcherFixes(patcherBuild);

			if (incompletePatcherFixes.size() > 2) {
				continue;
			}

			int patcherBuildStatus =
				PatcherBuildUtil.getNextPatcherBuildWorkflowStatus(
					patcherBuild, PatcherBuildUtil.isMergeOnly(patcherBuild));

			PatcherBuildUtil.setStatus(user, patcherBuild, patcherBuildStatus);

			patcherBuildPersistence.update(patcherBuild);
		}

		JenkinsUtil.sendAgentJenkinsRequest(user, patcherFix);

		return patcherFix;
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		Date modifiedDate, int[] type, boolean notified, int status) {

		return patcherFixPersistence.findByLtM_T_N_S(
			modifiedDate, type, notified, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, int type, boolean latestFix) {

		return patcherFixPersistence.findByP_T_L(
			patcherProjectVersionId, type, latestFix);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, int type, boolean latestFix, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return patcherFixPersistence.findByP_NotT_L(
				patcherProjectVersionId, type, latestFix);
		}

		return patcherFixPersistence.findByP_NotT_L_S(
			patcherProjectVersionId, type, latestFix, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, String name, int type,
		boolean latestFix) {

		return patcherFixPersistence.findByP_N_NotT_L(
			patcherProjectVersionId, name, type, latestFix);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		String key, double keyVersion, int type, boolean older) {

		if (older) {
			return patcherFixPersistence.findByK_LtKV_NotT(
				key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				PatcherFixKeyVersionComparator.getInstance(false));
		}

		return patcherFixPersistence.findByK_GtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherFixKeyVersionComparator.getInstance(true));
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		String key, int type, boolean latestFix) {

		return patcherFixPersistence.findByK_NotT_L(key, type, latestFix);
	}

	@Override
	public int getPatcherFixesCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return patcherFixPersistence.countByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateJenkinsResults(
			long patcherFixId, String jenkinsResults)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setJenkinsResults(jenkinsResults);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateLatestFix(long patcherFixId, boolean latestFix)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setLatestFix(latestFix);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateNotified(long patcherFixId, boolean notified)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setNotified(notified);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateObsolete(long patcherFixId, boolean obsolete)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setObsolete(obsolete);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updatePatcherFix(
			long patcherFixId, int type, boolean latestFix, boolean obsolete)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setType(type);
		patcherFix.setLatestFix(latestFix);
		patcherFix.setObsolete(obsolete);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updatePatcherFix(
			long userId, long patcherFixId, String gitHash,
			String jenkinsResults, int status)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		if (patcherFix.getStatus() == status) {
			return patcherFix;
		}

		patcherFix.setModifiedDate(new Date());
		patcherFix.setGitHash(gitHash);
		patcherFix.setJenkinsResults(jenkinsResults);
		patcherFix.setStatus(status);

		User statusUser = _getStatusUser(
			userId, patcherFix.getStatusByUserId());

		patcherFix.setStatusByUserId(statusUser.getUserId());
		patcherFix.setStatusByUserName(statusUser.getFullName());

		patcherFix.setStatusDate(new Date());

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateRequestKey(long patcherFixId, String requestKey)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setRequestKey(requestKey);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateStatus(long userId, long patcherFixId, int status)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		if (patcherFix.getStatus() == status) {
			return patcherFix;
		}

		patcherFix.setModifiedDate(new Date());
		patcherFix.setStatus(status);

		User user = _getStatusUser(userId, patcherFix.getStatusByUserId());

		patcherFix.setStatusByUserId(user.getUserId());
		patcherFix.setStatusByUserName(user.getFullName());

		patcherFix.setStatusDate(new Date());

		return patcherFixPersistence.update(patcherFix);
	}

	private String _generateKey(long patcherProjectVersionId, String name)
		throws Exception {

		return PatcherUtil.generatePatcherKey(
			PatcherFix.class.getName(), patcherProjectVersionId,
			StringUtil.merge(PatcherUtil.sortTokens(name), StringPool.COMMA));
	}

	private User _getStatusUser(long userId, long statusByUserId) {
		User currentUser = _userLocalService.fetchUser(statusByUserId);

		if (currentUser != null) {
			return currentUser;
		}

		return _userLocalService.fetchUser(userId);
	}

	private void _validateAdd(
			String committish, String gitRemoteURL, String name,
			long patcherProductVersionId, long patcherProjectVersionId)
		throws Exception {

		_validateCommittish(committish);
		_validateGitRemoteURL(gitRemoteURL);
		_validateProductVersion(patcherProductVersionId);
		_validatePatcherProjectVersionId(patcherProjectVersionId);
		_validateName(name, patcherProjectVersionId);

		PatcherProjectVersion patcherProjectVersion =
			_patcherProjectVersionPersistence.fetchByPrimaryKey(
				patcherProjectVersionId);

		if (!patcherProjectVersion.isCombinedBranch()) {
			_validateSiblingProjectVersionFixes(name, patcherProjectVersionId);
		}

		_validateKey(name, patcherProjectVersionId);
	}

	private void _validateCommittish(String committish) throws Exception {
		if (Validator.isNull(committish)) {
			throw new Exception("the-fix-branch-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion =
			_patcherProjectVersionPersistence.fetchByCommittish(committish);

		if (patcherProjectVersion != null) {
			throw new Exception(
				LanguageUtil.format(
					LocaleUtil.getMostRelevantLocale(),
					"the-branch-name-cannot-be-the-same-as-the-project-" +
						"version-tag-name-x",
					patcherProjectVersion.getCommittish()));
		}
	}

	private void _validateGitRemoteURL(String gitRemoteURL) throws Exception {
		Pattern pattern = Pattern.compile(
			PatcherConstants.GIT_REMOTE_URL_REGEX);

		Matcher matcher = pattern.matcher(gitRemoteURL);

		if (!matcher.find()) {
			throw new Exception("the-fix-github-url-is-invalid");
		}
	}

	private void _validateKey(String name, long patcherProjectVersionId)
		throws Exception {

		String key = _generateKey(patcherProjectVersionId, name);

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixes(
				key, PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC, true);

		if (!patcherFixes.isEmpty()) {
			throw new Exception("the-fix-already-exists");
		}
	}

	private void _validateName(String name, long patcherProjectVersionId)
		throws Exception {

		if (Validator.isNull(name)) {
			throw new Exception("the-fix-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion =
			_patcherProjectVersionPersistence.fetchByPrimaryKey(
				patcherProjectVersionId);

		if (!PatcherUtil.isPatcherTickets(
				name, patcherProjectVersion.getPatcherProductVersionId())) {

			throw new Exception("the-fix-name-cannot-be-evaluated");
		}

		PatcherProductVersion patcherProductVersion =
			_patcherProductVersionPersistence.fetchByName(
				PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X);

		if ((patcherProductVersion != null) &&
			(patcherProjectVersion.getPatcherProductVersionId() !=
				patcherProductVersion.getPatcherProductVersionId())) {

			List<String> fixedIssues = StringUtil.split(
				patcherProjectVersion.getFixedIssues());

			fixedIssues.retainAll(StringUtil.split(name));

			if (!fixedIssues.isEmpty()) {
				throw new Exception(
					LanguageUtil.format(
						LocaleUtil.getMostRelevantLocale(),
						"the-tickets-x-is-already-included-in-project-" +
							"version-x",
						new Object[] {
							StringUtil.merge(fixedIssues, StringPool.COMMA),
							patcherProjectVersion.getName()
						}));
			}
		}
	}

	private void _validatePatcherProjectVersionId(long patcherProjectVersionId)
		throws Exception {

		if (patcherProjectVersionId == 0) {
			throw new Exception("the-project-version-is-invalid");
		}
	}

	private void _validateProductVersion(long patcherProductVersionId)
		throws Exception {

		PatcherProductVersion patcherProductVersion =
			_patcherProductVersionPersistence.fetchByPrimaryKey(
				patcherProductVersionId);

		if (patcherProductVersion == null) {
			throw new Exception("the-product-version-id-is-invalid");
		}
	}

	private void _validateSiblingProjectVersionFixes(
			String name, long patcherProjectVersionId)
		throws Exception {

		Set<String> siblingPatcherProjectVersionFixTickets = new HashSet<>();

		PatcherProjectVersion patcherProjectVersion =
			_patcherProjectVersionPersistence.fetchByPrimaryKey(
				patcherProjectVersionId);

		PatcherProjectVersion siblingPatcherProjectVersion =
			PatcherProjectVersionUtil.getSiblingPatcherProjectVersion(
				patcherProjectVersion.getCommittish());

		List<PatcherFix> siblingPatcherProjectVersionFixes =
			PatcherFixLocalServiceUtil.getPatcherFixes(
				patcherProjectVersionId,
				PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC, true,
				WorkflowConstants.STATUS_ANY);

		for (PatcherFix siblingPatcherProjectVersionFix :
				siblingPatcherProjectVersionFixes) {

			if (siblingPatcherProjectVersionFix.getType() ==
					PatcherFixConstants.TYPE_EXCLUDED) {

				continue;
			}

			siblingPatcherProjectVersionFixTickets.addAll(
				PatcherUtil.getTickets(
					siblingPatcherProjectVersionFix.getName()));
		}

		List<String> patcherFixTickets = PatcherUtil.getTickets(name);

		for (String patcherFixTicket : patcherFixTickets) {
			if (siblingPatcherProjectVersionFixTickets.contains(
					patcherFixTicket)) {

				throw new Exception(
					LanguageUtil.format(
						LocaleUtil.getMostRelevantLocale(),
						"the-fix-cannot-be-added-because-there-are-fixes-" +
							"containing-x-on-project-version-x",
						new Object[] {
							patcherFixTicket,
							siblingPatcherProjectVersion.getCommittish()
						}));
			}
		}
	}

	@Reference
	private PatcherProductVersionPersistence _patcherProductVersionPersistence;

	@Reference
	private PatcherProjectVersionPersistence _patcherProjectVersionPersistence;

	@Reference
	private UserLocalService _userLocalService;

}