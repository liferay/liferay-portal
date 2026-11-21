/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.model.listener;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 * @author David Arques
 */
@Component(service = ModelListener.class)
public class SegmentsExperimentModelListener
	extends BaseModelListener<SegmentsExperiment> {

	@Override
	public void onAfterUpdate(
			SegmentsExperiment originalSegmentsExperiment,
			SegmentsExperiment segmentsExperiment)
		throws ModelListenerException {

		try {
			if (AsahUtil.isSkipAsahEvent(
					_analyticsSettingsManager,
					segmentsExperiment.getCompanyId(),
					segmentsExperiment.getGroupId()) ||
				((originalSegmentsExperiment.getStatus() !=
					segmentsExperiment.getStatus()) &&
				 (segmentsExperiment.getStatus() ==
					 SegmentsExperimentConstants.STATUS_FINISHED_NO_WINNER)) ||
				(segmentsExperiment.getStatus() ==
					SegmentsExperimentConstants.STATUS_FINISHED_WINNER)) {

				return;
			}

			_asahSegmentsExperimentProcessor.processUpdateSegmentsExperiment(
				segmentsExperiment);
		}
		catch (Exception exception) {
			throw new ModelListenerException(
				"Unable to update segments experiment " +
					segmentsExperiment.getSegmentsExperimentId(),
				exception);
		}
	}

	@Override
	public void onBeforeCreate(SegmentsExperiment segmentsExperiment)
		throws ModelListenerException {

		try {
			if (AsahUtil.isSkipAsahEvent(
					_analyticsSettingsManager,
					segmentsExperiment.getCompanyId(),
					segmentsExperiment.getGroupId())) {

				return;
			}

			_asahSegmentsExperimentProcessor.processAddSegmentsExperiment(
				segmentsExperiment);
		}
		catch (Exception exception) {
			throw new ModelListenerException(
				"Unable to add segments experiment " +
					segmentsExperiment.getSegmentsExperimentId(),
				exception);
		}
	}

	@Override
	public void onBeforeRemove(SegmentsExperiment segmentsExperiment)
		throws ModelListenerException {

		try {
			if (AsahUtil.isSkipAsahEvent(
					_analyticsSettingsManager,
					segmentsExperiment.getCompanyId(),
					segmentsExperiment.getGroupId()) ||
				(segmentsExperiment.getStatus() ==
					SegmentsExperimentConstants.STATUS_DELETED_ON_DXP_ONLY)) {

				return;
			}

			_asahSegmentsExperimentProcessor.processDeleteSegmentsExperiment(
				segmentsExperiment);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to delete segments experiment " +
						segmentsExperiment.getSegmentsExperimentId(),
					exception);
			}
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

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentModelListener.class);

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

}