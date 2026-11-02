/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.ratings.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portlet.ratings.service.base.RatingsStatsLocalServiceBaseImpl;
import com.liferay.ratings.kernel.exception.NoSuchStatsException;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.persistence.RatingsEntryPersistence;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class RatingsStatsLocalServiceImpl
	extends RatingsStatsLocalServiceBaseImpl {

	@Override
	public RatingsStats addStats(long classNameId, long classPK) {
		long statsId = counterLocalService.increment();

		RatingsStats stats = ratingsStatsPersistence.create(statsId);

		Date date = new Date();

		stats.setCreateDate(date);
		stats.setModifiedDate(date);

		stats.setClassNameId(classNameId);
		stats.setClassPK(classPK);
		stats.setTotalEntries(0);
		stats.setTotalScore(0.0);
		stats.setAverageScore(0.0);

		try {
			stats = ratingsStatsPersistence.update(stats);
		}
		catch (SystemException systemException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Add failed, fetch {classNameId=", classNameId,
						", classPK=", classPK, "}"));
			}

			stats = ratingsStatsPersistence.fetchByC_C(
				classNameId, classPK, false);

			if (stats == null) {
				throw systemException;
			}
		}

		return stats;
	}

	@Override
	public void deleteStats(String className, long classPK) {
		long classNameId = _classNameLocalService.getClassNameId(className);

		try {
			ratingsStatsPersistence.removeByC_C(classNameId, classPK);
		}
		catch (NoSuchStatsException noSuchStatsException) {
			if (_log.isWarnEnabled()) {
				_log.warn(noSuchStatsException);
			}
		}

		_ratingsEntryPersistence.removeByC_C(classNameId, classPK);
	}

	@Override
	public RatingsStats fetchStats(String className, long classPK) {
		return ratingsStatsPersistence.fetchByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public RatingsStats getStats(String className, long classPK)
		throws PortalException {

		return ratingsStatsPersistence.findByC_C(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public Map<Long, RatingsStats> getStats(String className, long[] classPKs) {
		long classNameId = _classNameLocalService.getClassNameId(className);

		Map<Long, RatingsStats> ratingsStats = new HashMap<>();

		for (RatingsStats stats :
				ratingsStatsPersistence.findByC_C(classNameId, classPKs)) {

			ratingsStats.put(stats.getClassPK(), stats);
		}

		return ratingsStats;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RatingsStatsLocalServiceImpl.class);

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = RatingsEntryPersistence.class)
	private RatingsEntryPersistence _ratingsEntryPersistence;

}