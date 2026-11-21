/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.model.listener;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.asah.connector.internal.client.AsahFaroBackendClient;
import com.liferay.segments.asah.connector.internal.processor.AsahSegmentsExperimentProcessor;
import com.liferay.segments.asah.connector.internal.util.AsahUtil;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 */
@Component(service = ModelListener.class)
public class SegmentsExperimentRelModelListener
	extends BaseModelListener<SegmentsExperimentRel> {

	@Override
	public void onAfterCreate(SegmentsExperimentRel segmentsExperimentRel)
		throws ModelListenerException {

		try {
			if (segmentsExperimentRel.isControl()) {
				return;
			}

			_processUpdateSegmentsExperimentRel(segmentsExperimentRel);
		}
		catch (Exception exception) {
			throw new ModelListenerException(
				"Unable to create segments experiment rel " +
					segmentsExperimentRel.getSegmentsExperimentRelId(),
				exception);
		}
	}

	@Override
	public void onAfterRemove(SegmentsExperimentRel segmentsExperimentRel)
		throws ModelListenerException {

		if (segmentsExperimentRel == null) {
			return;
		}

		try {
			SegmentsExperiment segmentsExperiment =
				_segmentsExperimentLocalService.fetchSegmentsExperiment(
					segmentsExperimentRel.getSegmentsExperimentId());

			if (segmentsExperiment == null) {
				return;
			}

			_processUpdateSegmentsExperimentRel(segmentsExperimentRel);
		}
		catch (Exception exception) {
			throw new ModelListenerException(
				"Unable to remove segments experiment rel " +
					segmentsExperimentRel.getSegmentsExperimentRelId(),
				exception);
		}
	}

	@Activate
	protected void activate() {
		_asahSegmentsExperimentProcessor = new AsahSegmentsExperimentProcessor(
			_analyticsSettingsManager,
			new AsahFaroBackendClient(_analyticsSettingsManager, _http),
			_companyLocalService, _groupLocalService, _layoutLocalService,
			_portal, _segmentsEntryLocalService,
			_segmentsExperienceLocalService);
	}

	@Deactivate
	protected void deactivate() {
		_asahSegmentsExperimentProcessor = null;
	}

	private void _processUpdateSegmentsExperimentRel(
			SegmentsExperimentRel segmentsExperimentRel)
		throws Exception {

		if (AsahUtil.isSkipAsahEvent(
				_analyticsSettingsManager, segmentsExperimentRel.getCompanyId(),
				segmentsExperimentRel.getGroupId())) {

			return;
		}

		_asahSegmentsExperimentProcessor.processUpdateSegmentsExperimentRel(
			segmentsExperimentRel.getCompanyId(),
			segmentsExperimentRel.getSegmentsExperimentKey(),
			_segmentsExperimentRelLocalService.getSegmentsExperimentRels(
				segmentsExperimentRel.getSegmentsExperimentId()));
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	private AsahSegmentsExperimentProcessor _asahSegmentsExperimentProcessor;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Http _http;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Reference
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}