/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.NestableRuntimeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributorRegistry;
import com.liferay.segments.exception.NoSuchEntryException;
import com.liferay.segments.exception.SegmentsEntryCriteriaException;
import com.liferay.segments.exception.SegmentsEntryKeyException;
import com.liferay.segments.exception.SegmentsEntryNameException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS,
		"mvc.command.name=/segments/update_segments_entry"
	},
	service = MVCActionCommand.class
)
public class UpdateSegmentsEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long segmentsEntryId = ParamUtil.getLong(
			actionRequest, "segmentsEntryId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");

		String segmentsEntryKey = ParamUtil.getString(
			actionRequest, "segmentsEntryKey");

		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		boolean active = ParamUtil.getBoolean(actionRequest, "active", true);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			SegmentsEntry.class.getName(), actionRequest);

		try {
			SegmentsEntry segmentsEntry = null;

			Criteria criteria = ActionUtil.getCriteria(
				actionRequest,
				_segmentsCriteriaContributorRegistry.
					getSegmentsCriteriaContributors());

			boolean dynamic = ParamUtil.getBoolean(
				actionRequest, "dynamic", true);

			_validateCriteria(criteria, dynamic);

			if (segmentsEntryId <= 0) {
				serviceContext.setScopeGroupId(
					_getGroupId(actionRequest, serviceContext));

				segmentsEntry = _segmentsEntryService.addSegmentsEntry(
					segmentsEntryKey, nameMap, descriptionMap, active,
					CriteriaSerializer.serialize(criteria), serviceContext);
			}
			else {
				segmentsEntry = _segmentsEntryService.updateSegmentsEntry(
					segmentsEntryId, segmentsEntryKey, nameMap, descriptionMap,
					active, CriteriaSerializer.serialize(criteria),
					serviceContext);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				redirect = HttpComponentsUtil.setParameter(
					redirect, "segmentsEntryId",
					segmentsEntry.getSegmentsEntryId());
			}

			boolean saveAndContinue = ParamUtil.get(
				actionRequest, "saveAndContinue", false);

			if (saveAndContinue) {
				redirect = _getSaveAndContinueRedirect(
					actionRequest, segmentsEntry, redirect);
			}

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof NestableRuntimeException ||
					 exception instanceof SegmentsEntryCriteriaException ||
					 exception instanceof SegmentsEntryKeyException ||
					 exception instanceof SegmentsEntryNameException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				actionResponse.setRenderParameter(
					"mvcRenderCommandName", "/segments/edit_segments_entry");
			}
			else {
				throw exception;
			}
		}
	}

	private long _getGroupId(
			ActionRequest actionRequest, ServiceContext serviceContext)
		throws PortalException {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		if (groupId == 0) {
			groupId = serviceContext.getScopeGroupId();
		}

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group.isControlPanel()) {
			Company company = _companyLocalService.getCompany(
				group.getCompanyId());

			return company.getGroupId();
		}

		return groupId;
	}

	private String _getSaveAndContinueRedirect(
		ActionRequest actionRequest, SegmentsEntry segmentsEntry,
		String redirect) {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(actionRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createRenderURL(
				SegmentsPortletKeys.SEGMENTS)
		).setMVCRenderCommandName(
			"/segments/edit_segments_entry"
		).setCMD(
			Constants.UPDATE
		).setRedirect(
			redirect
		).setParameter(
			"groupId", segmentsEntry.getGroupId()
		).setParameter(
			"segmentsEntryId", segmentsEntry.getSegmentsEntryId()
		).setWindowState(
			actionRequest.getWindowState()
		).buildString();
	}

	private void _validateCriteria(Criteria criteria, boolean dynamic)
		throws SegmentsEntryCriteriaException {

		if (dynamic && MapUtil.isEmpty(criteria.getCriteria())) {
			throw new SegmentsEntryCriteriaException();
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private SegmentsCriteriaContributorRegistry
		_segmentsCriteriaContributorRegistry;

	@Reference
	private SegmentsEntryService _segmentsEntryService;

}