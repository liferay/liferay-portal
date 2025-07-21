/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.base.PatcherBuildLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherBuildKeyVersionComparator;
import com.liferay.osb.patcher.util.comparator.PatcherBuildSupportTicketVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

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
			long patcherBuildId, int qaStatus, String supportTicket, int type)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setQaStatus(qaStatus);
		patcherBuild.setSupportTicket(supportTicket);
		patcherBuild.setType(type);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updatePatcherBuild(
			long patcherBuildId, String fileName, int qaStatus,
			String sourceName, int status)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setFileName(fileName);
		patcherBuild.setQaStatus(qaStatus);
		patcherBuild.setSourceName(sourceName);
		patcherBuild.setStatus(status);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updatePatcherBuild(PatcherBuild patcherBuild) {
		return super.updatePatcherBuild(patcherBuild);
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
			long patcherBuildId, String qaComments, int qaStatus)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setQaComments(qaComments);
		patcherBuild.setQaStatus(qaStatus);

		return patcherBuildPersistence.update(patcherBuild);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherBuild updateQaStatus(long patcherBuildId, int qaStatus)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setQaStatus(qaStatus);

		return patcherBuildPersistence.update(patcherBuild);
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
	public PatcherBuild updateStatus(long patcherBuildId, int status)
		throws PortalException {

		PatcherBuild patcherBuild = patcherBuildPersistence.findByPrimaryKey(
			patcherBuildId);

		patcherBuild.setModifiedDate(new Date());
		patcherBuild.setStatus(status);

		return patcherBuildPersistence.update(patcherBuild);
	}

}