/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.base.PatcherFixLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.PatcherFixRelUtil;
import com.liferay.osb.patcher.util.PatcherFixUtil;
import com.liferay.osb.patcher.util.PatcherProjectVersionUtil;
import com.liferay.osb.patcher.util.comparator.PatcherFixKeyVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

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
			long userId, long patcherProjectVersionId, double keyVersion,
			String name, int type, int status, List<Long> parentPatcherFixIds)
		throws Exception {

		PatcherFix patcherFix = patcherFixPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		patcherFix.setCompanyId(user.getCompanyId());
		patcherFix.setUserId(user.getUserId());
		patcherFix.setUserName(user.getFullName());

		patcherFix.setCreateDate(new Date());
		patcherFix.setModifiedDate(new Date());
		patcherFix.setPatcherProductVersionId(
			PatcherProjectVersionUtil.getPatcherProductVersionId(
				patcherProjectVersionId));
		patcherFix.setPatcherProjectVersionId(patcherProjectVersionId);
		patcherFix.setKey(
			PatcherFixUtil.generateKey(patcherProjectVersionId, name));
		patcherFix.setKeyVersion(keyVersion);
		patcherFix.setLatestFix(true);
		patcherFix.setName(name);
		patcherFix.setType(type);
		patcherFix.setStatus(status);

		PatcherFixRelUtil.addPatcherFixRel(
			patcherFix.getPatcherFixId(), parentPatcherFixIds);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public PatcherFix deletePatcherFix(long patcherFixId) throws Exception {
		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		PatcherFixUtil.validateDelete(patcherFix);

		return patcherFixPersistence.remove(patcherFixId);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		Date modifiedDate, boolean notified, int[] type, int status) {

		return patcherFixPersistence.findByLtM_N_T_S(
			modifiedDate, notified, type, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return patcherFixPersistence.findByP_L_T(
			patcherProjectVersionId, latestFix, type);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return patcherFixPersistence.findByP_L_NotT(
				patcherProjectVersionId, latestFix, type);
		}

		return patcherFixPersistence.findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return patcherFixPersistence.findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		String key, boolean latestFix, int type) {

		return patcherFixPersistence.findByK_L_NotT(key, latestFix, type);
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
	public int getPatcherFixesCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return patcherFixPersistence.countByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateComments(long patcherFixId, String comments)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setComments(comments);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateJenkinsResult(
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
			long patcherFixId, boolean latestFix, int type)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setLatestFix(latestFix);
		patcherFix.setType(type);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updatePatcherFix(
			long patcherFixId, String gitHash, int status)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setGitHash(gitHash);
		patcherFix.setStatus(status);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updatePatcherFix(
			long patcherFixId, String dependencies, int fixPackStatus,
			String requirements)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setDependencies(dependencies);
		patcherFix.setFixPackStatus(fixPackStatus);
		patcherFix.setRequirements(requirements);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updatePatcherFix(
			long patcherFixId, String gitHash, String jenkinsResults,
			int status)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setGitHash(gitHash);
		patcherFix.setJenkinsResults(jenkinsResults);
		patcherFix.setStatus(status);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updatePatcherFix(PatcherFix patcherFix) {
		return super.updatePatcherFix(patcherFix);
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
	public PatcherFix updateStatus(long patcherFixId, int status)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setStatus(status);

		return patcherFixPersistence.update(patcherFix);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFix updateType(long patcherFixId, int type)
		throws PortalException {

		PatcherFix patcherFix = patcherFixPersistence.findByPrimaryKey(
			patcherFixId);

		patcherFix.setModifiedDate(new Date());
		patcherFix.setType(type);

		return patcherFixPersistence.update(patcherFix);
	}

	@Reference
	private UserLocalService _userLocalService;

}