/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.display.context;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.scheduler.SchedulerResponseManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.text.Format;

import java.util.Date;

/**
 * @author Matija Petanjek
 */
public class SchedulerResponseDisplayContext extends BaseDisplayContext {

	public SchedulerResponseDisplayContext(
		RenderRequest renderRequest,
		SchedulerResponseManager schedulerResponseManager) {

		super(renderRequest);

		_schedulerResponseManager = schedulerResponseManager;

		_dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			dispatchRequestHelper.getLocale());
	}

	public String getNextFireDateString(SchedulerResponse schedulerResponse)
		throws SchedulerException {

		Date nextFireDate = _schedulerResponseManager.getNextFireDate(
			schedulerResponse.getJobName(), schedulerResponse.getGroupName(),
			schedulerResponse.getStorageType());

		if (nextFireDate != null) {
			return _dateTimeFormat.format(nextFireDate);
		}

		return StringPool.BLANK;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			dispatchRequestHelper.getRequest(), DispatchPortletKeys.DISPATCH,
			"scheduler-response-order-by-col", "start-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			dispatchRequestHelper.getRequest(), DispatchPortletKeys.DISPATCH,
			"scheduler-response-order-by-type", "desc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			dispatchRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/dispatch/edit_scheduler_response"
		).setTabs1(
			"scheduler-response"
		).buildPortletURL();

		String redirect = ParamUtil.getString(
			dispatchRequestHelper.getRequest(), "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String delta = ParamUtil.getString(
			dispatchRequestHelper.getRequest(), "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			dispatchRequestHelper.getRequest(), "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		return portletURL;
	}

	public SearchContainer<SchedulerResponse> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			dispatchRequestHelper.getLiferayPortletRequest(), getPortletURL(),
			null, "no-items-were-found");

		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(null);
		_searchContainer.setOrderByType(getOrderByType());
		_searchContainer.setResultsAndTotal(
			() -> _schedulerResponseManager.getSchedulerResponses(
				_searchContainer.getStart(), _searchContainer.getEnd()),
			_schedulerResponseManager.getSchedulerResponsesCount());

		return _searchContainer;
	}

	public String getSimpleName(String jobName) {
		return _schedulerResponseManager.getSimpleJobName(jobName);
	}

	public TriggerState getTriggerState(SchedulerResponse schedulerResponse)
		throws SchedulerException {

		return _schedulerResponseManager.getTriggerState(
			schedulerResponse.getJobName(), schedulerResponse.getGroupName(),
			schedulerResponse.getStorageType());
	}

	private final Format _dateTimeFormat;
	private String _orderByCol;
	private String _orderByType;
	private final SchedulerResponseManager _schedulerResponseManager;
	private SearchContainer<SchedulerResponse> _searchContainer;

}