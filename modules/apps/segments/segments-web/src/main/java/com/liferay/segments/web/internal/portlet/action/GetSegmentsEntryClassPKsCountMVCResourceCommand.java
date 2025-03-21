/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributorRegistry;
import com.liferay.segments.odata.retriever.ODataRetriever;
import com.liferay.segments.web.internal.constants.SegmentsWebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletSession;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS,
		"mvc.command.name=/segments/get_segments_entry_class_pks_count"
	},
	service = MVCResourceCommand.class
)
public class GetSegmentsEntryClassPKsCountMVCResourceCommand
	implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			PrintWriter printWriter = resourceResponse.getWriter();

			printWriter.write(_getText(resourceRequest));

			return false;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private int _getSegmentsEntryClassPKsCount(
		long companyId, Criteria criteria, Locale locale) {

		try {
			return _userODataRetriever.getResultsCount(
				companyId, criteria.getFilterString(Criteria.Type.MODEL),
				locale);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to obtain the segment user count", portalException);
			}

			return 0;
		}
	}

	private String _getText(ResourceRequest resourceRequest) {
		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(resourceRequest));

		long companyId = _portal.getCompanyId(httpServletRequest);

		Criteria criteria = ActionUtil.getCriteria(
			resourceRequest,
			_segmentsCriteriaContributorRegistry.
				getSegmentsCriteriaContributors());

		_saveCriteriaInSession(resourceRequest, criteria);

		int count = _getSegmentsEntryClassPKsCount(
			companyId, criteria, _portal.getLocale(resourceRequest));

		return String.valueOf(count);
	}

	private void _saveCriteriaInSession(
		ResourceRequest resourceRequest, Criteria criteria) {

		PortletSession portletSession = resourceRequest.getPortletSession();

		portletSession.setAttribute(
			SegmentsWebKeys.PREVIEW_SEGMENTS_ENTRY_CRITERIA, criteria);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetSegmentsEntryClassPKsCountMVCResourceCommand.class);

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsCriteriaContributorRegistry
		_segmentsCriteriaContributorRegistry;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private ODataRetriever<User> _userODataRetriever;

}