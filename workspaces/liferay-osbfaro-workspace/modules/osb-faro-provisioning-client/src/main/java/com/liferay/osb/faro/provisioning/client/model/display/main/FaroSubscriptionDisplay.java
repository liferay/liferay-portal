/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.provisioning.client.model.display.main;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroProjectUsageTable;
import com.liferay.osb.faro.provisioning.client.constants.FaroSubscriptionConstants;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.subscription.FaroSubscriptionPlan;
import com.liferay.osb.faro.service.FaroProjectUsageLocalService;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;

import java.util.ArrayList;
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

		_apiActivationDailyLimit =
			baseFaroSubscriptionPlan.getApiActivationDailyLimit();
		_apiActivationMonthlyLimit =
			baseFaroSubscriptionPlan.getApiActivationMonthlyLimit();
		_batchSegmentsLimit = baseFaroSubscriptionPlan.getBatchSegmentsLimit();
		_billableEventsMonthlyLimit =
			baseFaroSubscriptionPlan.getBillableEventsMonthlyLimit();
		_connectorsLimit = baseFaroSubscriptionPlan.getConnectorsLimit();
		_eventAnalysisLimit = baseFaroSubscriptionPlan.getEventAnalysisLimit();
		_individualsLimit = baseFaroSubscriptionPlan.getIndividualsLimit();
		_pageViewsLimit = baseFaroSubscriptionPlan.getPageViewsLimit();
		_realTimeSegmentsLimit =
			baseFaroSubscriptionPlan.getRealTimeSegmentsLimit();

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

				_apiActivationDailyLimit = _computeLimit(
					faroSubscriptionPlan.getApiActivationDailyLimit(),
					_apiActivationDailyLimit, osbOfferingEntry.getQuantity());
				_apiActivationMonthlyLimit = _computeLimit(
					faroSubscriptionPlan.getApiActivationMonthlyLimit(),
					_apiActivationMonthlyLimit, osbOfferingEntry.getQuantity());
				_batchSegmentsLimit = _computeLimit(
					faroSubscriptionPlan.getBatchSegmentsLimit(),
					_batchSegmentsLimit, osbOfferingEntry.getQuantity());
				_billableEventsMonthlyLimit = _computeLimit(
					faroSubscriptionPlan.getBillableEventsMonthlyLimit(),
					_billableEventsMonthlyLimit,
					osbOfferingEntry.getQuantity());
				_connectorsLimit = _computeLimit(
					faroSubscriptionPlan.getConnectorsLimit(), _connectorsLimit,
					osbOfferingEntry.getQuantity());
				_eventAnalysisLimit = _computeLimit(
					faroSubscriptionPlan.getEventAnalysisLimit(),
					_eventAnalysisLimit, osbOfferingEntry.getQuantity());
				_individualsLimit = _computeLimit(
					faroSubscriptionPlan.getIndividualsLimit(),
					_individualsLimit, osbOfferingEntry.getQuantity());
				_pageViewsLimit = _computeLimit(
					faroSubscriptionPlan.getPageViewsLimit(), _pageViewsLimit,
					osbOfferingEntry.getQuantity());
				_realTimeSegmentsLimit = _computeLimit(
					faroSubscriptionPlan.getRealTimeSegmentsLimit(),
					_realTimeSegmentsLimit, osbOfferingEntry.getQuantity());
			}
		}
	}

	public long getApiActivationDailyLimit() {
		return _apiActivationDailyLimit;
	}

	public long getApiActivationMonthlyLimit() {
		return _apiActivationMonthlyLimit;
	}

	public long getBatchSegmentsLimit() {
		return _batchSegmentsLimit;
	}

	public long getBillableEventsMonthlyLimit() {
		return _billableEventsMonthlyLimit;
	}

	public long getConnectorsLimit() {
		return _connectorsLimit;
	}

	public long getEventAnalysisLimit() {
		return _eventAnalysisLimit;
	}

	public long getIndividualsCountSinceLastAnniversary() {
		return _individualsCountSinceLastAnniversary;
	}

	public long getIndividualsLimit() {
		return _individualsLimit;
	}

	public int getIndividualsStatus() {
		return _individualsStatus;
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

	public long getPageViewsCountSinceLastAnniversary() {
		return _pageViewsCountSinceLastAnniversary;
	}

	public long getPageViewsLimit() {
		return _pageViewsLimit;
	}

	public int getPageViewsStatus() {
		return _pageViewsStatus;
	}

	public long getRealTimeSegmentsLimit() {
		return _realTimeSegmentsLimit;
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

	public void setBatchSegmentsLimit(long batchSegmentsLimit) {
		_batchSegmentsLimit = batchSegmentsLimit;
	}

	public void setCounts(
			FaroProject faroProject,
			FaroProjectUsageLocalService faroProjectUsageLocalService)
		throws Exception {

		if ((faroProject == null) ||
			!StringUtil.equals(
				faroProject.getState(), FaroProjectConstants.STATE_READY)) {

			return;
		}

		if (_isBasicSubscription(faroProject)) {
			_startDate = new Date(faroProject.getCreateTime());

			_lastAnniversaryDate = new Date(
				_startDate.getTime() / Time.DAY * Time.DAY);
		}
		else {
			if (_startDate == null) {
				_startDate = new Date(
					faroProject.getSubscriptionModifiedTime());
			}

			if (_lastAnniversaryDate == null) {
				_lastAnniversaryDate = _getLastAnniversaryDate(
					false, _startDate);
			}
		}

		FaroProjectUsageTable faroProjectUsageTable =
			FaroProjectUsageTable.INSTANCE;

		List<Object[]> results = faroProjectUsageLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.sum(
					faroProjectUsageTable.knownIndividualsCount
				).as(
					"knownIndividualsCount"
				),
				DSLFunctionFactoryUtil.sum(
					faroProjectUsageTable.pageViewsCount
				).as(
					"pageViewsCount"
				),
				faroProjectUsageTable.faroProjectId
			).from(
				faroProjectUsageTable
			).where(
				faroProjectUsageTable.faroProjectId.eq(
					faroProject.getFaroProjectId()
				).and(
					faroProjectUsageTable.usageTime.gte(
						_lastAnniversaryDate.getTime())
				)
			).groupBy(
				faroProjectUsageTable.faroProjectId
			));

		if (results.isEmpty()) {
			return;
		}

		Object[] objects = results.get(0);

		_individualsCountSinceLastAnniversary = (long)objects[0];

		_individualsStatus = getStatus(
			_individualsCountSinceLastAnniversary, _individualsLimit);

		_pageViewsCountSinceLastAnniversary = (long)objects[1];

		_pageViewsStatus = getStatus(
			_pageViewsCountSinceLastAnniversary, _pageViewsLimit);
	}

	public void setEventAnalysisLimit(long eventAnalysisLimit) {
		_eventAnalysisLimit = eventAnalysisLimit;
	}

	public void setRealTimeSegmentsLimit(long realTimeSegmentsLimit) {
		_realTimeSegmentsLimit = realTimeSegmentsLimit;
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
				subscriptionProductName,
				ProductConstants.PRODUCT_ENTRY_NAME_BASIC) ||
			StringUtil.equals(
				subscriptionProductName,
				ProductConstants.
					PRODUCT_ENTRY_NAME_DATA_PLATFORM_PRIVATE_BETA) ||
			StringUtil.equals(
				subscriptionProductName,
				ProductConstants.PRODUCT_ENTRY_NAME_LXC_PRO)) {

			return true;
		}

		return false;
	}

	private boolean _active;
	private final List<AddOn> _addOns = new ArrayList<>();
	private long _apiActivationDailyLimit;
	private long _apiActivationMonthlyLimit;
	private long _batchSegmentsLimit;
	private long _billableEventsMonthlyLimit;
	private long _connectorsLimit;
	private Date _endDate;
	private long _eventAnalysisLimit;
	private long _individualsCountSinceLastAnniversary;
	private long _individualsLimit;
	private int _individualsStatus;
	private Date _lastAnniversaryDate;
	private String _name;
	private long _pageViewsCountSinceLastAnniversary;
	private long _pageViewsLimit;
	private int _pageViewsStatus;
	private long _realTimeSegmentsLimit;
	private Date _startDate;
	private long _syncedIndividualsCount;

}