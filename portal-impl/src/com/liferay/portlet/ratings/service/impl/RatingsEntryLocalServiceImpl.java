/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.ratings.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.social.SocialActivityManagerUtil;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portlet.ratings.service.base.RatingsEntryLocalServiceBaseImpl;
import com.liferay.ratings.kernel.exception.EntryScoreException;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.ratings.kernel.service.persistence.RatingsStatsPersistence;
import com.liferay.social.kernel.model.SocialActivityConstants;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Zsolt Berentey
 */
public class RatingsEntryLocalServiceImpl
	extends RatingsEntryLocalServiceBaseImpl {

	@Override
	public void deleteEntry(long userId, String className, long classPK)
		throws PortalException {

		RatingsEntry entry = ratingsEntryPersistence.fetchByU_C_C(
			userId, _classNameLocalService.getClassNameId(className), classPK);

		ratingsEntryLocalService.deleteEntry(entry, userId, className, classPK);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteEntry(
			RatingsEntry entry, long userId, String className, long classPK)
		throws PortalException {

		// Entry

		if (entry == null) {
			return;
		}

		ratingsEntryPersistence.removeByU_C_C(
			userId, _classNameLocalService.getClassNameId(className), classPK);

		// Stats

		RatingsStats stats = _ratingsStatsLocalService.getStats(
			className, classPK);

		int totalEntries = stats.getTotalEntries() - 1;

		if (totalEntries == 0) {
			_ratingsStatsPersistence.remove(stats);
		}
		else {
			double oldScore = entry.getScore();

			double totalScore = stats.getTotalScore() - oldScore;

			double averageScore = 0;

			if (totalEntries > 0) {
				averageScore = totalScore / totalEntries;
			}

			stats.setTotalEntries(totalEntries);
			stats.setTotalScore(totalScore);
			stats.setAverageScore(averageScore);

			_ratingsStatsPersistence.update(stats);
		}

		// Social

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		if (assetEntry != null) {
			JSONObject extraDataJSONObject = JSONUtil.put(
				"title", assetEntry.getTitle());

			SocialActivityManagerUtil.addActivity(
				userId, assetEntry, SocialActivityConstants.TYPE_REVOKE_VOTE,
				extraDataJSONObject.toString(), 0);
		}
	}

	@Override
	public RatingsEntry fetchEntry(
		long userId, String className, long classPK) {

		return ratingsEntryPersistence.fetchByU_C_C(
			userId, _classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public Map<Long, RatingsEntry> getEntries(
		long userId, String className, long[] classPKs) {

		if (ArrayUtil.isEmpty(classPKs)) {
			return Collections.emptyMap();
		}

		long classNameId = _classNameLocalService.getClassNameId(className);

		Map<Long, RatingsEntry> ratingsEntries = new HashMap<>();

		for (RatingsEntry entry :
				ratingsEntryPersistence.findByU_C_C(
					userId, classNameId, classPKs)) {

			ratingsEntries.put(entry.getClassPK(), entry);
		}

		return ratingsEntries;
	}

	@Override
	public List<RatingsEntry> getEntries(String className, long classPK) {
		return ratingsEntryPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<RatingsEntry> getEntries(
		String className, long classPK, double score) {

		return ratingsEntryPersistence.findByC_C_S(
			_classNameLocalService.getClassNameId(className), classPK, score);
	}

	@Override
	public int getEntriesCount(String className, long classPK, double score) {
		return ratingsEntryPersistence.countByC_C_S(
			_classNameLocalService.getClassNameId(className), classPK, score);
	}

	@Override
	public RatingsEntry getEntry(long userId, String className, long classPK)
		throws PortalException {

		return ratingsEntryPersistence.findByU_C_C(
			userId, _classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public RatingsEntry updateEntry(
			long userId, String className, long classPK, double score,
			ServiceContext serviceContext)
		throws PortalException {

		// Entry

		long classNameId = _classNameLocalService.getClassNameId(className);

		validate(score);

		RatingsEntry entry = ratingsEntryPersistence.fetchByU_C_C(
			userId, classNameId, classPK);

		if (entry != null) {
			double oldScore = entry.getScore();

			entry.setScore(score);

			entry = ratingsEntryPersistence.update(entry);

			// Stats

			RatingsStats stats = _ratingsStatsPersistence.fetchByC_C(
				classNameId, classPK);

			if (stats == null) {
				stats = _ratingsStatsLocalService.addStats(
					classNameId, classPK);
			}

			stats.setModifiedDate(new Date());
			stats.setTotalScore(stats.getTotalScore() - oldScore + score);
			stats.setAverageScore(
				stats.getTotalScore() / stats.getTotalEntries());

			_ratingsStatsPersistence.update(stats);
		}
		else {
			User user = _userPersistence.findByPrimaryKey(userId);

			long entryId = counterLocalService.increment();

			entry = ratingsEntryPersistence.create(entryId);

			entry.setCompanyId(user.getCompanyId());
			entry.setUserId(user.getUserId());
			entry.setUserName(user.getFullName());
			entry.setClassNameId(classNameId);
			entry.setClassPK(classPK);
			entry.setScore(score);

			entry = ratingsEntryPersistence.update(entry);

			// Stats

			RatingsStats stats = _ratingsStatsPersistence.fetchByC_C(
				classNameId, classPK);

			if (stats == null) {
				stats = _ratingsStatsLocalService.addStats(
					classNameId, classPK);
			}

			stats.setModifiedDate(new Date());
			stats.setTotalEntries(stats.getTotalEntries() + 1);
			stats.setTotalScore(stats.getTotalScore() + score);
			stats.setAverageScore(
				stats.getTotalScore() / stats.getTotalEntries());

			_ratingsStatsPersistence.update(stats);
		}

		// Social

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		if (assetEntry != null) {
			JSONObject extraDataJSONObject = JSONUtil.put(
				"title", assetEntry.getTitle());

			SocialActivityManagerUtil.addActivity(
				userId, assetEntry, SocialActivityConstants.TYPE_ADD_VOTE,
				extraDataJSONObject.toString(), 0);
		}

		return entry;
	}

	protected void validate(double score) throws PortalException {
		if ((score > 1) || (score < 0) || Double.isNaN(score)) {
			throw new EntryScoreException(
				"Score " + score + " is not a value between 0 and 1");
		}
	}

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = RatingsStatsLocalService.class)
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@BeanReference(type = RatingsStatsPersistence.class)
	private RatingsStatsPersistence _ratingsStatsPersistence;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}