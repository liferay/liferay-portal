/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.base.PatcherBuildLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.EmailUtil;
import com.liferay.osb.patcher.util.comparator.PatcherBuildKeyVersionComparator;
import com.liferay.osb.patcher.util.comparator.PatcherBuildSupportTicketVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherBuild",
	service = AopService.class
)
public class PatcherBuildLocalServiceImpl
	extends PatcherBuildLocalServiceBaseImpl {

	@Override
	public List<PatcherBuild> getPatcherBuilds(
		boolean latestSupportTicketBuild, String supportTicket) {

		return patcherBuildPersistence.findByL_S(
			latestSupportTicketBuild, supportTicket);
	}

	@Override
	public List<PatcherBuild> getPatcherBuilds(
		Date modifiedDate, boolean notified, int[] statuses) {

		return patcherBuildPersistence.findByLtM_N_S(
			modifiedDate, notified, statuses);
	}

	@Override
	public List<PatcherBuild> getPatcherBuilds(
		long patcherFixId, boolean childBuild) {

		return patcherBuildPersistence.findByP_C(patcherFixId, childBuild);
	}

	@Override
	public List<PatcherBuild> getPatcherBuilds(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildPersistence.findByP_P(
			patcherAccountId, patcherProductVersionId, start, end,
			orderByComparator);
	}

	@Override
	public List<PatcherBuild> getPatcherBuilds(
		long patcherProjectVersionId, String name, boolean latestKeyBuild,
		String accountEntryCode) {

		return patcherBuildPersistence.findByP_N_L_A(
			patcherProjectVersionId, name, latestKeyBuild, accountEntryCode);
	}

	@Override
	public List<PatcherBuild> getPatcherBuilds(
		String key, boolean latestKeyBuild) {

		return patcherBuildPersistence.findByK_L(key, latestKeyBuild);
	}

	@Override
	public List<PatcherBuild> getPatcherBuilds(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return patcherBuildPersistence.findByKey(
			key, start, end, orderByComparator);
	}

	@Override
	public List<PatcherBuild> getPatcherBuildsByKey(
		String key, double keyVersion, boolean older) {

		if (older) {
			return patcherBuildPersistence.findByK_LtKV(
				key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				PatcherBuildKeyVersionComparator.getInstance(false));
		}

		return patcherBuildPersistence.findByK_GtKV(
			key, keyVersion, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherBuildKeyVersionComparator.getInstance(true));
	}

	@Override
	public List<PatcherBuild> getPatcherBuildsByPatcherFixId(
		long patcherFixId) {

		return patcherBuildPersistence.findByPatcherFixId(patcherFixId);
	}

	@Override
	public List<PatcherBuild> getPatcherBuildsByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return patcherBuildPersistence.findByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	@Override
	public List<PatcherBuild> getPatcherBuildsBySupportTicket(
		String supportTicket, double supportTicketVersion, boolean older) {

		if (older) {
			return patcherBuildPersistence.findByS_LtS(
				supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				PatcherBuildSupportTicketVersionComparator.getInstance(false));
		}

		return patcherBuildPersistence.findByS_GtS(
			supportTicket, supportTicketVersion, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS,
			PatcherBuildSupportTicketVersionComparator.getInstance(true));
	}

	@Override
	public int getPatcherBuildsCount(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return patcherBuildPersistence.countByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type);
	}

	@Override
	public int getPatcherBuildsCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return patcherBuildPersistence.countByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	@Override
	public boolean hasPatcherFixes(long patcherFixId) {
		return patcherBuildPersistence.containsPatcherFixes(patcherFixId);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updateComments(long patcherBuildId, String comments)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setComments(comments);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updateNotified(long patcherBuildId, boolean notified)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setNotified(notified);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updatePatcherBuild(
			long patcherBuildId, boolean latestKeyBuild,
			boolean latestSupportTicketBuild)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setLatestKeyBuild(latestKeyBuild);
		patcherBuild.setLatestSupportTicketBuild(latestSupportTicketBuild);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updatePatcherBuild(
			long userId, long patcherBuildId, int qaStatus,
			String supportTicket, int type)
		throws Exception {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		int oldQaStatus = patcherBuild.getQaStatus();
		int oldStatus = patcherBuild.getStatus();

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setQaStatus(qaStatus);
		patcherBuild.setSupportTicket(supportTicket);
		patcherBuild.setType(type);

		User user = _userLocalService.getUser(userId);

		patcherBuild.setStatusByUserId(user.getUserId());
		patcherBuild.setStatusByUserName(user.getFullName());

		patcherBuild = patcherBuildPersistence.update(patcherBuild);

		_sendEmail(patcherBuild, oldQaStatus, oldStatus, userId);

		return patcherBuild;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updatePatcherBuild(
			long userId, long patcherBuildId, String fileName, int qaStatus,
			String sourceName, int status)
		throws Exception {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		int oldQaStatus = patcherBuild.getQaStatus();
		int oldStatus = patcherBuild.getStatus();

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setFileName(fileName);
		patcherBuild.setQaStatus(qaStatus);
		patcherBuild.setSourceName(sourceName);
		patcherBuild.setStatus(status);

		User user = _userLocalService.getUser(userId);

		patcherBuild.setStatusByUserId(user.getUserId());
		patcherBuild.setStatusByUserName(user.getFullName());

		patcherBuild = patcherBuildPersistence.update(patcherBuild);

		_sendEmail(patcherBuild, oldQaStatus, oldStatus, userId);

		return patcherBuild;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updatePatcherBuild(PatcherBuild patcherBuild) {
		PatcherBuild oldPatcherBuild =
			patcherBuildPersistence.fetchByPrimaryKey(
				patcherBuild.getPatcherBuildId());

		patcherBuild = super.updatePatcherBuild(patcherBuild);

		if (oldPatcherBuild == null) {
			return patcherBuild;
		}

		try {
			_sendEmail(
				patcherBuild, oldPatcherBuild.getQaStatus(),
				oldPatcherBuild.getStatus(), patcherBuild.getUserId());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return patcherBuild;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updatePatcherFixId(
			long patcherBuildId, long patcherFixId)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setPatcherFixId(patcherFixId);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updateQaFields(
			long userId, long patcherBuildId, String qaComments, int qaStatus)
		throws Exception {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		int oldQaStatus = patcherBuild.getQaStatus();
		int oldStatus = patcherBuild.getStatus();

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setQaComments(qaComments);
		patcherBuild.setQaStatus(qaStatus);

		User user = _userLocalService.getUser(userId);

		patcherBuild.setStatusByUserId(user.getUserId());
		patcherBuild.setStatusByUserName(user.getFullName());

		patcherBuild = patcherBuildPersistence.update(patcherBuild);

		_sendEmail(patcherBuild, oldQaStatus, oldStatus, userId);

		return patcherBuild;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updateQaStatus(
			long userId, long patcherBuildId, int qaStatus)
		throws Exception {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		int oldQaStatus = patcherBuild.getQaStatus();
		int oldStatus = patcherBuild.getStatus();

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setQaStatus(qaStatus);

		User user = _userLocalService.getUser(userId);

		patcherBuild.setStatusByUserId(user.getUserId());
		patcherBuild.setStatusByUserName(user.getFullName());

		patcherBuild = patcherBuildPersistence.update(patcherBuild);

		_sendEmail(patcherBuild, oldQaStatus, oldStatus, userId);

		return patcherBuild;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updateRequestKey(long patcherBuildId, String requestKey)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setRequestKey(requestKey);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updateStatus(
			long userId, long patcherBuildId, int status)
		throws Exception {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		int oldQaStatus = patcherBuild.getQaStatus();
		int oldStatus = patcherBuild.getStatus();

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setStatus(status);

		User user = _userLocalService.getUser(userId);

		patcherBuild.setStatusByUserId(user.getUserId());
		patcherBuild.setStatusByUserName(user.getFullName());

		patcherBuild = patcherBuildPersistence.update(patcherBuild);

		_sendEmail(patcherBuild, oldQaStatus, oldStatus, userId);

		return patcherBuild;
	}

	private void _sendEmail(
			PatcherBuild patcherBuild, int oldQaStatus, int oldStatus,
			long userId)
		throws Exception {

		User user = _userLocalService.getUser(userId);

		if (oldStatus != patcherBuild.getStatus()) {
			EmailUtil.sendPatcherEmail(
				patcherBuild, patcherBuild.getStatus(), user);
		}

		if (oldQaStatus != patcherBuild.getQaStatus()) {
			if ((patcherBuild.getQaStatus() ==
					WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_STARTED) ||
				(patcherBuild.getQaStatus() ==
					WorkflowConstants.
						STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY) ||
				(patcherBuild.getQaStatus() ==
					WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_STARTED) ||
				(patcherBuild.getQaStatus() ==
					WorkflowConstants.
						STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY) ||
				(patcherBuild.getQaStatus() ==
					WorkflowConstants.STATUS_BUILD_QA_PENDING_SMOKE_ONLY) ||
				(patcherBuild.getQaStatus() ==
					WorkflowConstants.STATUS_BUILD_QA_TESTING_SKIPPED) ||
				(patcherBuild.getQaStatus() ==
					WorkflowConstants.
						STATUS_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY) ||
				(patcherBuild.getQaStatus() ==
					WorkflowConstants.STATUS_PENDING)) {

				return;
			}

			EmailUtil.sendPatcherEmail(
				patcherBuild, patcherBuild.getQaStatus(), user);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherBuildLocalServiceImpl.class);

	@Reference
	private UserLocalService _userLocalService;

}