/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.provisioning.client.ProvisioningClient;
import com.liferay.osb.faro.provisioning.client.constants.ProductConstants;
import com.liferay.osb.faro.provisioning.client.model.OSBAccountEntry;
import com.liferay.osb.faro.provisioning.client.model.OSBOfferingEntry;
import com.liferay.osb.faro.provisioning.client.model.display.main.FaroSubscriptionDisplay;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroProjectUsageLocalService;
import com.liferay.osb.faro.util.FaroPropsValues;
import com.liferay.osb.faro.web.internal.constants.FaroMessageDestinationNames;
import com.liferay.osb.faro.web.internal.helper.ProjectUsageHelper;
import com.liferay.osb.faro.web.internal.messaging.destination.creator.DestinationCreator;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.osb.faro.web.internal.util.OSBAccountEntryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Rachael Koestartyo
 */
@Component(
	property = "destination.name=" + FaroMessageDestinationNames.FARO_UPDATE_FARO_PROJECT_SUBSCRIPTIONS_MESSAGE_PROCESSOR,
	service = MessageListener.class
)
public class UpdateFaroProjectSubscriptionsMessageListener
	extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		try {
			_destinationCreator.createDestination(
				bundleContext, _destinationFactory,
				FaroMessageDestinationNames.
					FARO_UPDATE_FARO_PROJECT_SUBSCRIPTIONS_MESSAGE_PROCESSOR);

			Class<?> clazz = getClass();

			_trigger = _triggerFactory.createTrigger(
				clazz.getName(), clazz.getName(), new Date(), null,
				"0 0 0/6 * * ?");

			_schedulerEngineHelper.schedule(
				_trigger, StorageType.PERSISTED, null,
				FaroMessageDestinationNames.
					FARO_UPDATE_FARO_PROJECT_SUBSCRIPTIONS_MESSAGE_PROCESSOR,
				null);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Deactivate
	protected void deactivate() {
		try {
			if (_destinationCreator != null) {
				_destinationCreator.removeDestination();

				_destinationCreator = null;
			}

			if (_trigger == null) {
				return;
			}

			_schedulerEngineHelper.delete(
				_trigger.getJobName(), _trigger.getGroupName(),
				StorageType.PERSISTED);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		Calendar calendar = Calendar.getInstance();

		Date date = new Date();

		calendar.setTime(new Date(date.getTime() / Time.DAY * Time.DAY));

		calendar.add(Calendar.DATE, -1);

		date = calendar.getTime();

		List<FaroProject> faroProjects =
			_faroProjectLocalService.getFaroProjects(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_projectUsageHelper.addOrUpdateFaroProjectUsages(date, faroProjects);

		for (FaroProject faroProject : faroProjects) {
			try {
				OSBAccountEntry osbAccountEntry = _getOSBAccountEntry(
					faroProject);

				faroProject = _updateSalesforceAccountId(
					faroProject, osbAccountEntry.getSalesforceAccountId());

				FaroSubscriptionDisplay faroSubscriptionDisplay =
					new FaroSubscriptionDisplay(osbAccountEntry);

				if (Objects.equals(
						faroProject.getState(),
						FaroProjectConstants.STATE_UNAVAILABLE)) {

					faroProject = _faroProjectLocalService.updateState(
						faroProject.getFaroProjectId(),
						FaroProjectConstants.STATE_READY);
				}

				faroSubscriptionDisplay.setCounts(
					faroProject, _faroProjectUsageLocalService);

				if (Objects.equals(
						faroSubscriptionDisplay.getLastAnniversaryDate(),
						date)) {

					_contactsEngineClient.updateBQProject(
						faroProject,
						faroSubscriptionDisplay.getLastAnniversaryDate());
				}

				_faroProjectLocalService.updateSubscription(
					faroProject.getFaroProjectId(),
					JSONUtil.writeValueAsString(faroSubscriptionDisplay));
			}
			catch (Exception exception) {
				_log.error(
					"Unable to update Faro project subscription for " +
						faroProject,
					exception);

				_faroProjectLocalService.updateState(
					faroProject.getFaroProjectId(),
					FaroProjectConstants.STATE_UNAVAILABLE);
			}
		}
	}

	private OSBAccountEntry _getOSBAccountEntry(FaroProject faroProject)
		throws Exception {

		if (Validator.isNull(faroProject.getCorpProjectUuid()) &&
			Validator.isNull(faroProject.getSalesforceAccountId())) {

			Date createDate = new Date(faroProject.getCreateTime());
			String subscription = faroProject.getSubscription();

			return new OSBAccountEntry() {
				{
					OSBOfferingEntry osbOfferingEntry = new OSBOfferingEntry();

					osbOfferingEntry.setProductEntryId(
						_getProductEntryId(subscription));
					osbOfferingEntry.setQuantity(1);
					osbOfferingEntry.setStartDate(createDate);

					setOfferingEntries(
						Collections.singletonList(osbOfferingEntry));
				}
			};
		}

		if (FaroPropsValues.OSB_FARO_SUBSCRIPTION_PUSH_ENABLED) {
			return OSBAccountEntryUtil.build(faroProject);
		}

		return _provisioningClient.getOSBAccountEntry(
			faroProject.getCorpProjectUuid());
	}

	private String _getProductEntryId(String subscription) throws Exception {
		JSONObject subscriptionJSONObject = _jsonFactory.createJSONObject(
			subscription);

		String subscriptionName = subscriptionJSONObject.getString("name");

		if (subscriptionName != null) {
			String productEntryId = ProductConstants.getProductEntryId(
				subscriptionName);

			if (productEntryId != null) {
				return productEntryId;
			}
		}

		return ProductConstants.getProductEntryId(
			ProductConstants.PRODUCT_ENTRY_NAME_BASIC);
	}

	private FaroProject _updateSalesforceAccountId(
		FaroProject faroProject, String salesforceAccountId) {

		if (Validator.isNull(salesforceAccountId) ||
			salesforceAccountId.equals(faroProject.getSalesforceAccountId())) {

			return faroProject;
		}

		try {
			return _faroProjectLocalService.updateSalesforceAccountId(
				faroProject.getFaroProjectId(), salesforceAccountId);
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to set Salesforce account ID ", salesforceAccountId,
					" for ", faroProject),
				exception);

			return faroProject;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateFaroProjectSubscriptionsMessageListener.class);

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	private DestinationCreator _destinationCreator = new DestinationCreator();

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private FaroProjectUsageLocalService _faroProjectUsageLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ProjectUsageHelper _projectUsageHelper;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ProvisioningClient _provisioningClient;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	private Trigger _trigger;

	@Reference
	private TriggerFactory _triggerFactory;

}