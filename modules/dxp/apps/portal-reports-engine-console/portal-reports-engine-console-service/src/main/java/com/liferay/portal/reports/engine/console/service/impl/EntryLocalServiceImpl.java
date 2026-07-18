/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.impl;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.reports.engine.MemoryReportDesignRetriever;
import com.liferay.portal.reports.engine.ReportDataSourceType;
import com.liferay.portal.reports.engine.ReportDesignRetriever;
import com.liferay.portal.reports.engine.ReportRequest;
import com.liferay.portal.reports.engine.ReportRequestContext;
import com.liferay.portal.reports.engine.console.configuration.ReportsGroupServiceEmailConfiguration;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsoleConstants;
import com.liferay.portal.reports.engine.console.exception.DefinitionNameException;
import com.liferay.portal.reports.engine.console.exception.EntryEmailDeliveryException;
import com.liferay.portal.reports.engine.console.exception.EntryEmailNotificationsException;
import com.liferay.portal.reports.engine.console.internal.constants.ReportsEngineDestinationNames;
import com.liferay.portal.reports.engine.console.internal.util.ReportsEngineConsoleSubscriptionSender;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.model.Entry;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.base.EntryLocalServiceBaseImpl;
import com.liferay.portal.reports.engine.console.service.persistence.DefinitionPersistence;
import com.liferay.portal.reports.engine.console.service.persistence.SourcePersistence;
import com.liferay.portal.reports.engine.console.status.ReportStatus;

import java.io.IOException;
import java.io.InputStream;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gavin Wan
 */
@Component(
	property = "model.class.name=com.liferay.portal.reports.engine.console.model.Entry",
	service = AopService.class
)
public class EntryLocalServiceImpl extends EntryLocalServiceBaseImpl {

	@Override
	public Entry addEntry(
			long userId, long groupId, long definitionId, String format,
			boolean schedulerRequest, Date startDate, Date endDate,
			boolean repeating, String recurrence, String emailNotifications,
			String emailDelivery, String portletId, String pageURL,
			String reportName, String reportParameters,
			ServiceContext serviceContext)
		throws PortalException {

		// Entry

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		_validate(emailNotifications, emailDelivery, reportName);

		long entryId = counterLocalService.increment();

		Entry entry = entryPersistence.create(entryId);

		entry.setGroupId(groupId);
		entry.setCompanyId(user.getCompanyId());
		entry.setUserId(user.getUserId());
		entry.setUserName(user.getFullName());
		entry.setCreateDate(serviceContext.getCreateDate(date));
		entry.setModifiedDate(serviceContext.getModifiedDate(date));
		entry.setDefinitionId(definitionId);
		entry.setFormat(format);
		entry.setScheduleRequest(schedulerRequest);
		entry.setStartDate(startDate);
		entry.setEndDate(endDate);
		entry.setRepeating(repeating);
		entry.setRecurrence(recurrence);
		entry.setEmailNotifications(emailNotifications);
		entry.setEmailDelivery(emailDelivery);
		entry.setPortletId(portletId);
		entry.setReportParameters(reportParameters);
		entry.setPageURL(
			StringBundler.concat(
				pageURL, "&", _portal.getPortletNamespace(portletId),
				"entryId=", entryId));
		entry.setStatus(ReportStatus.PENDING.getValue());

		entry = entryPersistence.update(entry);

		// Resources

		_resourceLocalService.addModelResources(entry, serviceContext);

		// Scheduler

		if (schedulerRequest) {
			_scheduleEntry(entry, reportName);
		}

		// Report

		if (!schedulerRequest) {
			generateReport(entryId, reportName);
		}

		return entry;
	}

	@Override
	public void addEntryResources(
			Entry entry, boolean addCommunityPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			entry.getCompanyId(), entry.getGroupId(), entry.getUserId(),
			Entry.class.getName(), entry.getEntryId(), false,
			addCommunityPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			Entry entry, String[] communityPermissions,
			String[] guestPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			entry.getCompanyId(), entry.getGroupId(), entry.getUserId(),
			Entry.class.getName(), entry.getEntryId(), communityPermissions,
			guestPermissions);
	}

	@Override
	public void deleteAttachment(long companyId, String fileName)
		throws PortalException {

		_store.deleteDirectory(companyId, CompanyConstants.SYSTEM, fileName);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Entry deleteEntry(Entry entry) throws PortalException {

		// Entry

		entryPersistence.remove(entry);

		// Resources

		_resourceLocalService.deleteResource(
			entry.getCompanyId(), Entry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, entry.getEntryId());

		// Scheduler

		if (entry.isRepeating()) {
			_schedulerEngineHelper.delete(
				_getSchedulerJobName(entry), entry.getSchedulerRequestName(),
				StorageType.PERSISTED);
		}

		// Attachments

		_store.deleteDirectory(
			entry.getCompanyId(), CompanyConstants.SYSTEM,
			entry.getAttachmentsDir());

		return entry;
	}

	@Override
	public Entry deleteEntry(long entryId) throws PortalException {
		Entry entry = entryPersistence.findByPrimaryKey(entryId);

		return deleteEntry(entry);
	}

	@Override
	public void generateReport(long entryId) throws PortalException {
		Entry entry = entryPersistence.findByPrimaryKey(entryId);

		Definition definition = _definitionPersistence.findByPrimaryKey(
			entry.getDefinitionId());

		generateReport(entryId, definition.getReportName());
	}

	@Override
	public void generateReport(long entryId, String reportName)
		throws PortalException {

		Entry entry = entryPersistence.findByPrimaryKey(entryId);

		Definition definition = _definitionPersistence.findByPrimaryKey(
			entry.getDefinitionId());

		String[] existingFiles = definition.getAttachmentsFileNames();

		ReportDesignRetriever retriever = new MemoryReportDesignRetriever(
			reportName + StringPool.PERIOD + entry.getFormat(),
			definition.getModifiedDate(),
			_getTemplateFileBytes(definition, existingFiles));

		long sourceId = definition.getSourceId();

		Map<String, String> reportParameters = new HashMap<>();

		JSONArray reportParametersJSONArray = _jsonFactory.createJSONArray(
			entry.getReportParameters());

		for (int i = 0; i < reportParametersJSONArray.length(); i++) {
			JSONObject reportParameterJSONObject =
				reportParametersJSONArray.getJSONObject(i);

			reportParameters.put(
				reportParameterJSONObject.getString("key"),
				reportParameterJSONObject.getString("value"));
		}

		ReportRequestContext reportRequestContext = null;

		if (sourceId == 0) {
			reportRequestContext = new ReportRequestContext(
				ReportDataSourceType.PORTAL);
		}
		else {
			Source source = _sourcePersistence.findByPrimaryKey(sourceId);

			reportRequestContext = new ReportRequestContext(
				ReportDataSourceType.JDBC);

			reportRequestContext.setAttribute(
				ReportRequestContext.JDBC_DRIVER_CLASS,
				source.getDriverClassName());
			reportRequestContext.setAttribute(
				ReportRequestContext.JDBC_PASSWORD, source.getDriverPassword());
			reportRequestContext.setAttribute(
				ReportRequestContext.JDBC_URL, source.getDriverUrl());
			reportRequestContext.setAttribute(
				ReportRequestContext.JDBC_USER_NAME,
				source.getDriverUserName());
		}

		ReportRequest reportRequest = new ReportRequest(
			reportRequestContext, retriever, reportParameters,
			entry.getFormat());

		Message message = new Message();

		message.setPayload(reportRequest);
		message.setResponseId(String.valueOf(entry.getEntryId()));

		_messageBus.sendMessage(
			ReportsEngineDestinationNames.REPORT_REQUEST, message);
	}

	@Override
	public String[] getAttachmentsFileNames(Entry entry) {
		return _store.getFileNames(
			entry.getCompanyId(), CompanyConstants.SYSTEM,
			entry.getAttachmentsDir());
	}

	@Override
	public List<Entry> getEntries(
		long groupId, String definitionName, String userName, Date createDateGT,
		Date createDateLT, boolean andSearch, int start, int end,
		OrderByComparator<Entry> orderByComparator) {

		return entryFinder.findByG_CD_N_SN(
			groupId, definitionName, userName, createDateGT, createDateLT,
			andSearch, start, end, orderByComparator);
	}

	@Override
	public int getEntriesCount(
		long groupId, String definitionName, String userName, Date createDateGT,
		Date createDateLT, boolean andSearch) {

		return entryFinder.countByG_CD_N_SN(
			groupId, definitionName, userName, createDateGT, createDateLT,
			andSearch);
	}

	@Override
	public void sendEmails(
			long entryId, String fileName, String[] emailAddresses,
			boolean notification)
		throws PortalException {

		Entry entry = entryPersistence.findByPrimaryKey(entryId);

		try {
			_notifySubscribers(entry, emailAddresses, fileName, notification);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException.getMessage());
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public void unscheduleEntry(long entryId) throws PortalException {
		Entry entry = entryPersistence.findByPrimaryKey(entryId);

		entry.setScheduleRequest(false);
		entry.setRepeating(false);

		entry = entryPersistence.update(entry);

		_schedulerEngineHelper.delete(
			_getSchedulerJobName(entry), entry.getSchedulerRequestName(),
			StorageType.PERSISTED);
	}

	@Override
	public void updateEntry(
			long entryId, String reportName, byte[] reportResults)
		throws PortalException {

		Entry entry = entryPersistence.findByPrimaryKey(entryId);
		Date date = new Date();

		if (entry.isScheduleRequest()) {
			StringBundler sb = new StringBundler(4);

			sb.append(StringUtil.extractFirst(reportName, StringPool.PERIOD));

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"MM_dd_yyyy_HH_mm");

			sb.append(dateFormat.format(date));

			sb.append(StringPool.PERIOD);
			sb.append(StringUtil.extractLast(reportName, StringPool.PERIOD));

			reportName = sb.toString();
		}

		String fileName =
			entry.getAttachmentsDir() + StringPool.SLASH + reportName;

		try (InputStream inputStream = new UnsyncByteArrayInputStream(
				reportResults)) {

			_store.addFile(
				entry.getCompanyId(), CompanyConstants.SYSTEM, fileName,
				Store.VERSION_DEFAULT, inputStream);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}

		String[] emailAddresses = StringUtil.split(entry.getEmailDelivery());

		sendEmails(entryId, fileName, emailAddresses, false);

		emailAddresses = StringUtil.split(entry.getEmailNotifications());

		sendEmails(entryId, fileName, emailAddresses, true);

		entry.setModifiedDate(date);
		entry.setStatus(ReportStatus.COMPLETE.getValue());

		entryPersistence.update(entry);
	}

	@Override
	public void updateEntryStatus(
			long entryId, ReportStatus status, String errorMessage)
		throws PortalException {

		Entry entry = entryPersistence.findByPrimaryKey(entryId);

		entry.setErrorMessage(errorMessage);
		entry.setStatus(status.getValue());

		entryPersistence.update(entry);
	}

	private InputStream _getReportFileInputStream(
			Entry entry, String fileName, boolean notification)
		throws Exception {

		if (notification) {
			return null;
		}

		InputStream inputStream = _store.getFileAsStream(
			entry.getCompanyId(), CompanyConstants.SYSTEM, fileName,
			StringPool.BLANK);

		if (inputStream == null) {
			throw new IOException("Unable to open file " + fileName);
		}

		return inputStream;
	}

	private ReportsGroupServiceEmailConfiguration
			_getReportsGroupServiceEmailConfiguration(long groupId)
		throws Exception {

		return _configurationProvider.getConfiguration(
			ReportsGroupServiceEmailConfiguration.class,
			new GroupServiceSettingsLocator(
				groupId, ReportsEngineConsoleConstants.SERVICE_NAME));
	}

	private String _getSchedulerJobName(Entry entry) {
		return StringBundler.concat(
			entry.getJobName(), StringPool.AT, entry.getCompanyId());
	}

	private byte[] _getTemplateFileBytes(
			Definition definition, String[] existingFiles)
		throws PortalException {

		try {
			return StreamUtil.toByteArray(
				_store.getFileAsStream(
					definition.getCompanyId(), CompanyConstants.SYSTEM,
					existingFiles[0], StringPool.BLANK));
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private void _notifySubscribers(
			Entry entry, String[] emailAddresses, String fileName,
			boolean notification)
		throws Exception {

		if (emailAddresses.length == 0) {
			return;
		}

		String portletId = entry.getPortletId();

		ReportsGroupServiceEmailConfiguration
			reportsGroupServiceEmailConfiguration =
				_getReportsGroupServiceEmailConfiguration(entry.getGroupId());

		String fromName = reportsGroupServiceEmailConfiguration.emailFromName();

		String fromAddress =
			reportsGroupServiceEmailConfiguration.emailFromAddress();

		Map<Locale, String> localizedSubjectMap = null;
		Map<Locale, String> localizedBodyMap = null;

		if (notification) {
			localizedBodyMap = _localization.getMap(
				reportsGroupServiceEmailConfiguration.emailNotificationsBody());

			localizedSubjectMap = _localization.getMap(
				reportsGroupServiceEmailConfiguration.
					emailNotificationsSubject());
		}
		else {
			localizedBodyMap = _localization.getMap(
				reportsGroupServiceEmailConfiguration.emailDeliveryBody());

			localizedSubjectMap = _localization.getMap(
				reportsGroupServiceEmailConfiguration.emailDeliverySubject());
		}

		SubscriptionSender subscriptionSender =
			new ReportsEngineConsoleSubscriptionSender();

		String reportName = StringUtil.extractLast(
			fileName, StringPool.FORWARD_SLASH);

		if (!notification) {
			subscriptionSender.addFileAttachment(
				reportName,
				_getReportFileInputStream(entry, fileName, notification));
		}

		subscriptionSender.setContextAttributes(
			"[$FROM_ADDRESS$]", fromAddress, "[$FROM_NAME$]", fromName,
			"[$PAGE_URL$]", entry.getPageURL(), "[$REPORT_NAME$]", reportName);
		subscriptionSender.setCurrentUserId(entry.getUserId());
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setGroupId(entry.getGroupId());
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setLocalizedBodyMap(localizedBodyMap);
		subscriptionSender.setLocalizedSubjectMap(localizedSubjectMap);
		subscriptionSender.setMailId("reports_entry", entry.getEntryId());
		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setPortletId(portletId);

		subscriptionSender.setScopeGroupId(entry.getGroupId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(entry.getCompanyId());
		serviceContext.setScopeGroupId(entry.getGroupId());

		subscriptionSender.setServiceContext(serviceContext);

		for (String emailAddress : emailAddresses) {
			subscriptionSender.addRuntimeSubscribers(
				emailAddress, emailAddress);
		}

		subscriptionSender.flushNotificationsAsync();
	}

	private void _scheduleEntry(Entry entry, String reportName)
		throws PortalException {

		Trigger trigger = _triggerFactory.createTrigger(
			_getSchedulerJobName(entry), entry.getSchedulerRequestName(),
			entry.getStartDate(), entry.getEndDate(), entry.getRecurrence());

		Message message = new Message();

		message.put("companyId", entry.getCompanyId());
		message.put("entryId", entry.getEntryId());
		message.put("reportName", reportName);

		_schedulerEngineHelper.schedule(
			trigger, StorageType.PERSISTED, null,
			ReportsEngineDestinationNames.REPORTS_SCHEDULER_EVENT, message);
	}

	private void _validate(
			String emailNotifications, String emailDelivery, String reportName)
		throws PortalException {

		for (String emailAddress : StringUtil.split(emailNotifications)) {
			if (!Validator.isEmailAddress(emailAddress)) {
				throw new EntryEmailNotificationsException();
			}
		}

		for (String emailAddress : StringUtil.split(emailDelivery)) {
			if (!Validator.isEmailAddress(emailAddress)) {
				throw new EntryEmailDeliveryException();
			}
		}

		if (Validator.isNull(reportName)) {
			throw new DefinitionNameException();
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DefinitionPersistence _definitionPersistence;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Localization _localization;

	@Reference
	private MessageBus _messageBus;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private SourcePersistence _sourcePersistence;

	@Reference(target = "(default=true)")
	private Store _store;

	@Reference
	private TriggerFactory _triggerFactory;

	@Reference
	private UserLocalService _userLocalService;

}