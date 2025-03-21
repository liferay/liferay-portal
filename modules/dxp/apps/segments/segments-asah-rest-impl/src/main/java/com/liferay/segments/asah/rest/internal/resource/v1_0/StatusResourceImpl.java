/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.segments.asah.rest.dto.v1_0.Experiment;
import com.liferay.segments.asah.rest.dto.v1_0.Status;
import com.liferay.segments.asah.rest.resource.v1_0.StatusResource;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperimentService;

import jakarta.ws.rs.ClientErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/status.properties",
	scope = ServiceScope.PROTOTYPE, service = StatusResource.class
)
public class StatusResourceImpl extends BaseStatusResourceImpl {

	@Override
	public Experiment postExperimentStatus(
			Long segmentsExperimentKey, Status status)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.popServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}

		serviceContext.setAttribute("updateAsah", Boolean.FALSE);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		SegmentsExperimentConstants.Status segmentsExperimentConstantsStatus =
			SegmentsExperimentConstants.Status.parse(status.getStatus());

		if (segmentsExperimentConstantsStatus == null) {
			throw new ClientErrorException("Experiment status is invalid", 422);
		}

		return _toExperiment(
			_segmentsExperimentService.updateSegmentsExperimentStatus(
				String.valueOf(segmentsExperimentKey),
				status.getWinnerVariantId(),
				segmentsExperimentConstantsStatus.getValue()));
	}

	private Experiment _toExperiment(SegmentsExperiment segmentsExperiment) {
		return new Experiment() {
			{
				setDateCreated(segmentsExperiment::getCreateDate);
				setDateModified(segmentsExperiment::getModifiedDate);
				setDescription(segmentsExperiment::getDescription);
				setId(segmentsExperiment::getSegmentsExperimentKey);
				setName(segmentsExperiment::getName);
				setSiteId(segmentsExperiment::getGroupId);
				setStatus(
					() -> {
						SegmentsExperimentConstants.Status
							segmentsExperimentConstantsStatus =
								SegmentsExperimentConstants.Status.valueOf(
									segmentsExperiment.getStatus());

						return segmentsExperimentConstantsStatus.toString();
					});
				setWinnerVariantId(
					segmentsExperiment::getWinnerSegmentsExperienceId);
			}
		};
	}

	@Reference
	private SegmentsExperimentService _segmentsExperimentService;

}