/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.main;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.CerebroEngineClient;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.web.internal.constants.FaroSubscriptionConstants;
import com.liferay.osb.faro.web.internal.subscription.FaroSubscriptionPlan;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

/**
 * @author Matthew Kong
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class FaroSubscriptionDisplay {

	public FaroSubscriptionDisplay() {
	}

	public FaroSubscriptionDisplay(OSBAccountEntry osbAccountEntry) {
		OSBOfferingEntry baseOSBOfferingEntry = _getBaseOSBOfferingEntry(
			osbAccountEntry.getOfferingEntries());

		if (baseOSBOfferingEntry == null) {
			return;
		}

		if (baseOSBOfferingEntry.getStatus() ==
				ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE) {

			_active = true;
		}
		else {
			_active = false;
		}

		_endDate = baseOSBOfferingEntry.getSupportEndDate();
		_name = ProductConstants.getProductName(
			baseOSBOfferingEntry.getProductEntryId());

		_startDate = baseOSBOfferingEntry.getStartDate();

		if (_startDate != null) {
			_lastAnniversaryDate = _getLastAnniversaryDate(
				_isBasicSubscription(_name), _startDate);
		}

		FaroSubscriptionPlan baseFaroSubscriptionPlan =
			FaroSubscriptionConstants.getFaroSubscriptionPlanByProductEntryId(
				baseOSBOfferingEntry.getProductEntryId());

		_individualsLimit = baseFaroSubscriptionPlan.getIndividualsLimit();
		_pageViewsLimit = baseFaroSubscriptionPlan.getPageViewsLimit();

		for (OSBOfferingEntry osbOfferingEntry :
				osbAccountEntry.getOfferingEntries()) {

			FaroSubscriptionPlan faroSubscriptionPlan =
				FaroSubscriptionConstants.
					getFaroSubscriptionPlanByProductEntryId(
						osbOfferingEntry.getProductEntryId());

			if ((faroSubscriptionPlan != null) &&
				StringUtil.equals(
					faroSubscriptionPlan.getBaseSubscriptionPlan(),
					baseFaroSubscriptionPlan.getName())) {

				_addOns.add(new AddOn(osbOfferingEntry));

				_individualsLimit = _computeLimit(
					faroSubscriptionPlan.getIndividualsLimit(),
					_individualsLimit, osbOfferingEntry.getQuantity());
				_pageViewsLimit = _computeLimit(
					faroSubscriptionPlan.getPageViewsLimit(), _pageViewsLimit,
					osbOfferingEntry.getQuantity());
			}
		}
	}

	public String getIndividualsCounts() {
		return _individualsCounts;
	}

	public long getIndividualsCountSinceLastAnniversary() {
		return _individualsCountSinceLastAnniversary;
	}

	public long getIndividualsLimit() {
		return _individualsLimit;
	}

	public Date getLastAnniversaryDate() {
		if (_lastAnniversaryDate == null) {
			return null;
		}

		return new Date(_lastAnniversaryDate.getTime());
	}

	public String getName() {
		return _name;
	}

	public String getPageViewsCounts() {
		return _pageViewsCounts;
	}

	public long getPageViewsCountSinceLastAnniversary() {
		return _pageViewsCountSinceLastAnniversary;
	}

	public long getPageViewsLimit() {
		return _pageViewsLimit;
	}

	public Date getStartDate() {
		if (_startDate == null) {
			return null;
		}

		return new Date(_startDate.getTime());
	}

	public long getSyncedIndividualsCount() {
		return _syncedIndividualsCount;
	}

	public boolean isActive() {
		return _active;
	}

	public void setCounts(
			FaroProject faroProject, CerebroEngineClient cerebroEngineClient,
			ContactsEngineClient contactsEngineClient)
		throws Exception {

		if ((faroProject == null) ||
			!StringUtil.equals(
				faroProject.getState(), FaroProjectConstants.STATE_READY)) {

			return;
		}

		JSONObject subscriptionJSONObject = JSONFactoryUtil.createJSONObject(
			faroProject.getSubscription());

		JSONObject individualsCountsJSONObject =
			subscriptionJSONObject.getJSONObject("individualsCounts");

		if (individualsCountsJSONObject != null) {
			_individualsCounts = individualsCountsJSONObject.toString();
		}

		JSONObject pageViewsCountsJSONObject =
			subscriptionJSONObject.getJSONObject("pageViewsCounts");

		if (pageViewsCountsJSONObject != null) {
			_pageViewsCounts = pageViewsCountsJSONObject.toString();
		}

		if (_startDate == null) {
			_startDate = _getStartDate(faroProject);
		}

		if (_lastAnniversaryDate == null) {
			_lastAnniversaryDate = _getLastAnniversaryDate(
				_isBasicSubscription(faroProject), _startDate);
		}

		_syncedIndividualsCount =
			contactsEngineClient.getSyncedIndividualsCount(faroProject);

		Date date = new Date();

		date = new Date(date.getTime() / Time.DAY * Time.DAY);

		_individualsCountSinceLastAnniversary =
			contactsEngineClient.getIndividualsCreatedBetweenCount(
				faroProject, date, _lastAnniversaryDate);

		_individualsStatus = getStatus(
			_individualsCountSinceLastAnniversary, _individualsLimit);

		_pageViewsCountSinceLastAnniversary = GetterUtil.getInteger(
			cerebroEngineClient.getPageViews(
				faroProject, _lastAnniversaryDate, date));

		_pageViewsStatus = getStatus(
			_pageViewsCountSinceLastAnniversary, _pageViewsLimit);
	}

	public void setIndividualsCounts(String individualsCounts) {
		_individualsCounts = individualsCounts;
	}

	public void setPageViewsCounts(String pageViewsCounts) {
		_pageViewsCounts = pageViewsCounts;
	}

	public void setUsageCounts(
			CerebroEngineClient cerebroEngineClient,
			ContactsEngineClient contactsEngineClient, Date endDate,
			FaroProject faroProject, Date startDate)
		throws Exception {

		if ((faroProject == null) ||
			!StringUtil.equals(
				faroProject.getState(), FaroProjectConstants.STATE_READY)) {

			return;
		}

		if (_startDate == null) {
			_startDate = _getStartDate(faroProject);
		}

		if (_lastAnniversaryDate == null) {
			_lastAnniversaryDate = _getLastAnniversaryDate(
				_isBasicSubscription(faroProject), _startDate);
		}

		JSONObject subscriptionJSONObject = JSONFactoryUtil.createJSONObject(
			faroProject.getSubscription());

		_individualsCounts = _setCounts(
			contactsEngineClient.getIndividualsCreatedBetweenCount(
				faroProject, endDate, startDate),
			subscriptionJSONObject.getLong(
				"individualsCountSinceLastAnniversary"),
			endDate, faroProject,
			JSONFactoryUtil.createJSONObject(
				subscriptionJSONObject.getString("individualsCounts")),
			startDate);

		_pageViewsCounts = _setCounts(
			cerebroEngineClient.getPageViews(faroProject, startDate, endDate),
			subscriptionJSONObject.getLong(
				"pageViewsCountSinceLastAnniversary"),
			endDate, faroProject,
			JSONFactoryUtil.createJSONObject(
				subscriptionJSONObject.getString("pageViewsCounts")),
			startDate);
	}

	public static class AddOn {

		public AddOn() {
		}

		public AddOn(OSBOfferingEntry osbOfferingEntry) {
			_name = ProductConstants.getProductName(
				osbOfferingEntry.getProductEntryId());
			_quantity = osbOfferingEntry.getQuantity();
		}

		public String getName() {
			return _name;
		}

		public int getQuantity() {
			return _quantity;
		}

		public void setName(String name) {
			_name = name;
		}

		public void setQuantity(int quantity) {
			_quantity = quantity;
		}

		private String _name;
		private int _quantity;

	}

	protected int getStatus(long count, long limit) {
		if (limit < 0) {
			return FaroSubscriptionConstants.STATUS_OK;
		}

		if (count > limit) {
			return FaroSubscriptionConstants.STATUS_LIMIT_OVER;
		}
		else if (((double)count / limit) >
					FaroSubscriptionConstants.LIMIT_APPROACHING_THRESHOLD) {

			return FaroSubscriptionConstants.STATUS_LIMIT_APPROACHING;
		}

		return FaroSubscriptionConstants.STATUS_OK;
	}

	private Date _addToDate(Date date, int field, int increment) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		cal.add(field, increment);

		return cal.getTime();
	}

	private long _computeLimit(
		long addOnLimit, long currentLimit, long quantity) {

		if (currentLimit < 0) {
			return currentLimit;
		}

		return currentLimit + (addOnLimit * quantity);
	}

	private OSBOfferingEntry _getBaseOSBOfferingEntry(
		List<OSBOfferingEntry> osbOfferingEntries) {

		OSBOfferingEntry baseOSBOfferingEntry = null;

		List<String> baseProductEntryIds =
			ProductConstants.getBaseProductEntryIds();

		for (OSBOfferingEntry osbOfferingEntry : osbOfferingEntries) {
			if (!baseProductEntryIds.contains(
					osbOfferingEntry.getProductEntryId())) {

				continue;
			}

			if ((baseOSBOfferingEntry == null) ||
				((baseOSBOfferingEntry.getStatus() !=
					osbOfferingEntry.getStatus()) &&
				 (osbOfferingEntry.getStatus() ==
					 ProductConstants.OSB_OFFERING_ENTRY_STATUS_ACTIVE)) ||
				((baseOSBOfferingEntry.getStatus() ==
					osbOfferingEntry.getStatus()) &&
				 _isAfter(baseOSBOfferingEntry, osbOfferingEntry))) {

				baseOSBOfferingEntry = osbOfferingEntry;
			}
		}

		return baseOSBOfferingEntry;
	}

	private Date _getLastAnniversaryDate(
		boolean basicSubscription, Date startDate) {

		if (basicSubscription) {
			return new Date(startDate.getTime() / Time.DAY * Time.DAY);
		}

		Date lastAnniversaryDate = DateUtils.setYears(
			startDate, DateUtil.getYear(new Date()));

		if (DateUtil.compareTo(new Date(), lastAnniversaryDate) <= 0) {
			lastAnniversaryDate = DateUtils.setYears(
				startDate, DateUtil.getYear(new Date()) - 1);
		}

		return new Date(lastAnniversaryDate.getTime() / Time.DAY * Time.DAY);
	}

	private Date _getStartDate(FaroProject faroProject) throws Exception {
		if (_isBasicSubscription(faroProject)) {
			return new Date(faroProject.getCreateTime());
		}

		return new Date(faroProject.getSubscriptionModifiedTime());
	}

	private boolean _isAfter(
		OSBOfferingEntry baseOSBOfferingEntry,
		OSBOfferingEntry osbOfferingEntry) {

		int value = DateUtil.compareTo(
			osbOfferingEntry.getStartDate(), DateUtil.newDate());

		if (value > 0) {
			return false;
		}

		value = DateUtil.compareTo(
			osbOfferingEntry.getStartDate(),
			baseOSBOfferingEntry.getStartDate());

		if (value > 0) {
			return true;
		}

		return false;
	}

	private boolean _isBasicSubscription(FaroProject faroProject)
		throws Exception {

		JSONObject subscriptionJSONObject = JSONFactoryUtil.createJSONObject(
			faroProject.getSubscription());

		return _isBasicSubscription(subscriptionJSONObject.getString("name"));
	}

	private boolean _isBasicSubscription(String subscriptionProductName) {
		if (StringUtil.equals(
				subscriptionProductName, ProductConstants.BASIC_PRODUCT_NAME) ||
			StringUtil.equals(
				subscriptionProductName,
				ProductConstants.LXC_PRO_PRODUCT_NAME)) {

			return true;
		}

		return false;
	}

	private String _setCounts(
			long count, long defaultValue, Date endDate,
			FaroProject faroProject, JSONObject jsonObject, Date startDate)
		throws Exception {

		long totalSinceLastAnniversary = 0;

		if ((jsonObject.length() == 0) && (count != defaultValue)) {
			jsonObject = JSONUtil.put(
				"total", defaultValue
			).put(
				"totalSinceLastAnniversary", defaultValue
			);

			totalSinceLastAnniversary = defaultValue;
		}
		else {
			jsonObject.put("total", jsonObject.getLong("total", 0L) + count);

			totalSinceLastAnniversary =
				jsonObject.getLong("totalSinceLastAnniversary") + count;

			jsonObject.put(
				"totalSinceLastAnniversary", totalSinceLastAnniversary);
		}

		if (!_isBasicSubscription(faroProject) &&
			(DateUtil.compareTo(endDate, _lastAnniversaryDate) == 0)) {

			jsonObject.put(
				"monthlyValues", JSONFactoryUtil.createJSONObject()
			).put(
				"totalSinceLastAnniversary", 0L
			);

			return jsonObject.toString();
		}

		JSONObject monthlyValuesJSONObject = jsonObject.getJSONObject(
			"monthlyValues");

		if (monthlyValuesJSONObject == null) {
			monthlyValuesJSONObject = JSONFactoryUtil.createJSONObject();
		}

		DateFormat dateFormat = new SimpleDateFormat("MMM yyyy");

		String formattedStartDate = dateFormat.format(startDate);

		JSONObject monthlyValueJSONObject =
			monthlyValuesJSONObject.getJSONObject(formattedStartDate);

		if (monthlyValueJSONObject == null) {
			long countSinceLastAnniversary = 0;

			JSONObject previousMonthlyValueJSONObject =
				monthlyValuesJSONObject.getJSONObject(
					dateFormat.format(
						_addToDate(startDate, Calendar.MONTH, -1)));

			if (previousMonthlyValueJSONObject != null) {
				long previousMonthlyCountSinceLastAnniversary =
					previousMonthlyValueJSONObject.getLong(
						"countSinceLastAnniversary");

				countSinceLastAnniversary =
					previousMonthlyCountSinceLastAnniversary + count;
			}
			else {
				countSinceLastAnniversary = totalSinceLastAnniversary;
			}

			monthlyValueJSONObject = JSONUtil.put(
				"count", count
			).put(
				"countSinceLastAnniversary", countSinceLastAnniversary
			);
		}
		else {
			monthlyValueJSONObject.put(
				"count", monthlyValueJSONObject.getLong("count") + count
			).put(
				"countSinceLastAnniversary",
				monthlyValueJSONObject.getLong("countSinceLastAnniversary") +
					count
			);
		}

		monthlyValuesJSONObject.put(formattedStartDate, monthlyValueJSONObject);

		jsonObject.put("monthlyValues", monthlyValuesJSONObject);

		return jsonObject.toString();
	}

	private boolean _active;
	private final List<AddOn> _addOns = new ArrayList<>();
	private Date _endDate;
	private String _individualsCounts;
	private long _individualsCountSinceLastAnniversary;
	private long _individualsLimit;
	private int _individualsStatus;
	private Date _lastAnniversaryDate;
	private String _name;
	private String _pageViewsCounts;
	private long _pageViewsCountSinceLastAnniversary;
	private long _pageViewsLimit;
	private int _pageViewsStatus;
	private Date _startDate;
	private long _syncedIndividualsCount;

}