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
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.base.SegmentsExperimentRelServiceBaseImpl;
import com.liferay.segments.service.persistence.SegmentsExperimentPersistence;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The implementation of the segments experiment rel remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * <code>com.liferay.segments.service.SegmentsExperimentRelService</code>
 * interface. <p> This is a remote service. Methods of this service are expected
 * to have security checks based on the propagated JAAS credentials because this
 * service can be accessed remotely.
 * </p>
 *
 * @author Eduardo García
 * @see    SegmentsExperimentRelServiceBaseImpl
 */
@Component(
	property = {
		"json.web.service.context.name=segments",
		"json.web.service.context.path=SegmentsExperimentRel"
	},
	service = AopService.class
)
public class SegmentsExperimentRelServiceImpl
	extends SegmentsExperimentRelServiceBaseImpl {

	@Override
	public SegmentsExperimentRel addSegmentsExperimentRel(
			long segmentsExperimentId, long segmentsExperienceId,
			ServiceContext serviceContext)
		throws PortalException {

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperimentId, ActionKeys.UPDATE);

		return segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperimentId, segmentsExperienceId, serviceContext);
	}

	@Override
	public SegmentsExperimentRel deleteSegmentsExperimentRel(
			long segmentsExperimentRelId)
		throws PortalException {

		SegmentsExperimentRel segmentsExperimentRel =
			segmentsExperimentRelPersistence.findByPrimaryKey(
				segmentsExperimentRelId);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentRel.getSegmentsExperimentId(), ActionKeys.UPDATE);

		return segmentsExperimentRelLocalService.deleteSegmentsExperimentRel(
			segmentsExperimentRelId);
	}

	@Override
	public SegmentsExperimentRel getSegmentsExperimentRel(
			long segmentsExperimentId, String segmentsExperienceKey)
		throws PortalException {

		SegmentsExperimentRel segmentsExperimentRel =
			segmentsExperimentRelLocalService.getSegmentsExperimentRel(
				segmentsExperimentId, segmentsExperienceKey);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(), segmentsExperimentId, ActionKeys.VIEW);

		return segmentsExperimentRel;
	}

	@Override
	public List<SegmentsExperimentRel> getSegmentsExperimentRels(
			long segmentsExperimentId)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentPersistence.findByPrimaryKey(
				segmentsExperimentId);

		if (!_userLocalService.hasRoleUser(
				segmentsExperiment.getCompanyId(),
				RoleConstants.ANALYTICS_ADMINISTRATOR, getUserId(), true)) {

			_segmentsExperimentResourcePermission.check(
				getPermissionChecker(), segmentsExperimentId, ActionKeys.VIEW);
		}

		return segmentsExperimentRelPersistence.findBySegmentsExperimentId(
			segmentsExperimentId);
	}

	@Override
	public SegmentsExperimentRel updateSegmentsExperimentRel(
			long segmentsExperimentRelId, double split)
		throws PortalException {

		SegmentsExperimentRel segmentsExperimentRel =
			segmentsExperimentRelPersistence.findByPrimaryKey(
				segmentsExperimentRelId);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentRel.getSegmentsExperimentId(), ActionKeys.UPDATE);

		return segmentsExperimentRelLocalService.updateSegmentsExperimentRel(
			segmentsExperimentRelId, split);
	}

	@Override
	public SegmentsExperimentRel updateSegmentsExperimentRel(
			long segmentsExperimentRelId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		SegmentsExperimentRel segmentsExperimentRel =
			segmentsExperimentRelPersistence.findByPrimaryKey(
				segmentsExperimentRelId);

		_segmentsExperimentResourcePermission.check(
			getPermissionChecker(),
			segmentsExperimentRel.getSegmentsExperimentId(), ActionKeys.UPDATE);

		return segmentsExperimentRelLocalService.updateSegmentsExperimentRel(
			segmentsExperimentRelId, name, serviceContext);
	}

	@Reference
	private SegmentsExperimentPersistence _segmentsExperimentPersistence;

	@Reference(
		target = "(model.class.name=com.liferay.segments.model.SegmentsExperiment)"
	)
	private ModelResourcePermission<SegmentsExperiment>
		_segmentsExperimentResourcePermission;

	@Reference
	private UserLocalService _userLocalService;

}