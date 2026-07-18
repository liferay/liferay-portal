/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.base.SegmentsExperimentServiceBaseImpl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"json.web.service.context.name=segments",
		"json.web.service.context.path=SegmentsExperiment"
	},
	service = AopService.class
)
public class SegmentsExperimentServiceImpl
	extends SegmentsExperimentServiceBaseImpl {

	@Override
	public SegmentsExperiment addSegmentsExperiment(
			long segmentsExperienceId, long plid, String name,
			String description, String goal, String goalTarget,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutRestrictedUpdatePermission(
			getPermissionChecker(), plid);

		return segmentsExperimentLocalService.addSegmentsExperiment(
			segmentsExperienceId, plid, name, description, goal, goalTarget,
			serviceContext);
	}

	@Override
	public SegmentsExperiment deleteSegmentsExperiment(
			long segmentsExperimentId)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId),
			ActionKeys.DELETE);

		return segmentsExperimentLocalService.deleteSegmentsExperiment(
			segmentsExperimentId);
	}

	@Override
	public SegmentsExperiment deleteSegmentsExperiment(
			SegmentsExperiment segmentsExperiment, boolean force)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperiment, ActionKeys.DELETE);

		return segmentsExperimentLocalService.deleteSegmentsExperiment(
			segmentsExperiment, force);
	}

	@Override
	public SegmentsExperiment deleteSegmentsExperiment(
			String segmentsExperimentKey)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentKey);

		_checkPermissions(segmentsExperiment, ActionKeys.DELETE);

		return segmentsExperimentLocalService.deleteSegmentsExperiment(
			segmentsExperiment);
	}

	@Override
	public SegmentsExperiment fetchSegmentsExperiment(
			long groupId, String segmentsExperimentKey)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentPersistence.fetchByG_S(
				groupId, segmentsExperimentKey);

		if ((segmentsExperiment != null) &&
			_segmentsExperimentResourcePermission.contains(
				getPermissionChecker(), segmentsExperiment, ActionKeys.VIEW)) {

			return segmentsExperiment;
		}

		return null;
	}

	@Override
	public SegmentsExperiment fetchSegmentsExperiment(
			long groupId, String segmentsExperienceKey, long plid)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.fetchSegmentsExperiment(
				groupId, segmentsExperienceKey, plid);

		if ((segmentsExperiment != null) &&
			_segmentsExperimentResourcePermission.contains(
				getPermissionChecker(), segmentsExperiment, ActionKeys.VIEW)) {

			return segmentsExperiment;
		}

		return null;
	}

	@Override
	public SegmentsExperiment getSegmentsExperiment(long segmentsExperimentId)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperiment, ActionKeys.VIEW);

		return segmentsExperiment;
	}

	@Override
	public SegmentsExperiment getSegmentsExperiment(
			String segmentsExperimentKey)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentKey);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperiment, ActionKeys.VIEW);

		return segmentsExperiment;
	}

	@Override
	public SegmentsExperiment runSegmentsExperiment(
			long segmentsExperimentId, double confidenceLevel,
			Map<Long, Double> segmentsExperienceIdSplitMap, String type)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId),
			ActionKeys.UPDATE);

		return segmentsExperimentLocalService.runSegmentsExperiment(
			segmentsExperimentId, confidenceLevel, segmentsExperienceIdSplitMap,
			type);
	}

	@Override
	public SegmentsExperiment runSegmentsExperiment(
			String segmentsExperimentKey, double confidenceLevel,
			Map<String, Double> segmentsExperienceKeySplitMap, String type)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentKey);

		_checkPermissions(segmentsExperiment, ActionKeys.UPDATE);

		Map<Long, Double> segmentsExperienceIdSplitMap = new HashMap<>();

		for (Map.Entry<String, Double> entry :
				segmentsExperienceKeySplitMap.entrySet()) {

			segmentsExperienceIdSplitMap.put(
				_getSegmentsExperienceId(
					segmentsExperiment.getGroupId(), entry.getKey(),
					segmentsExperiment.getPlid()),
				entry.getValue());
		}

		return segmentsExperimentLocalService.runSegmentsExperiment(
			segmentsExperiment.getSegmentsExperimentId(), confidenceLevel,
			segmentsExperienceIdSplitMap, type);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperiment(
			long segmentsExperimentId, String name, String description,
			String goal, String goalTarget)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId),
			ActionKeys.UPDATE);

		return segmentsExperimentLocalService.updateSegmentsExperiment(
			segmentsExperimentId, name, description, goal, goalTarget);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperimentStatus(
			long segmentsExperimentId, int status)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId),
			ActionKeys.UPDATE);

		return segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperimentId, status);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperimentStatus(
			long segmentsExperimentId, long winnerSegmentsExperienceId,
			int status)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentId),
			ActionKeys.UPDATE);

		return segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperimentId, winnerSegmentsExperienceId, status);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperimentStatus(
			String segmentsExperimentKey, int status)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentKey);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperiment, ActionKeys.UPDATE);

		return segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(), status);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperimentStatus(
			String segmentsExperimentKey, String winnerSegmentsExperienceKey,
			int status)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.getSegmentsExperiment(
				segmentsExperimentKey);

		_checkPermissions(segmentsExperiment, ActionKeys.UPDATE);

		return segmentsExperimentLocalService.updateSegmentsExperimentStatus(
			segmentsExperiment.getSegmentsExperimentId(),
			_getSegmentsExperienceId(
				segmentsExperiment.getGroupId(), winnerSegmentsExperienceKey,
				segmentsExperiment.getPlid()),
			status);
	}

	private void _checkPermissions(
			SegmentsExperiment segmentsExperiment, String actionId)
		throws PortalException {

		if (_userLocalService.hasRoleUser(
				segmentsExperiment.getCompanyId(),
				RoleConstants.ANALYTICS_ADMINISTRATOR, getUserId(), true)) {

			return;
		}

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperiment, actionId);
	}

	private long _getSegmentsExperienceId(
		long groupId, String segmentsExperienceKey, long classPK) {

		if (Validator.isNotNull(segmentsExperienceKey)) {
			SegmentsExperience segmentsExperience =
				_segmentsExperienceLocalService.fetchSegmentsExperience(
					groupId, segmentsExperienceKey, classPK);

			if (segmentsExperience != null) {
				return segmentsExperience.getSegmentsExperienceId();
			}
		}

		return -1;
	}

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.segments.model.SegmentsExperiment)"
	)
	private ModelResourcePermission<SegmentsExperiment>
		_segmentsExperimentResourcePermission;

	@Reference
	private UserLocalService _userLocalService;

}