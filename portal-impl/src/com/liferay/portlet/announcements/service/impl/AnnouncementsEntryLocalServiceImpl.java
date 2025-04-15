/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.impl;

import com.liferay.announcements.kernel.exception.EntryContentException;
import com.liferay.announcements.kernel.exception.EntryExpirationDateException;
import com.liferay.announcements.kernel.exception.EntryTitleException;
import com.liferay.announcements.kernel.exception.EntryURLException;
import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.service.AnnouncementsDeliveryLocalService;
import com.liferay.announcements.kernel.service.AnnouncementsFlagLocalService;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsDeliveryPersistence;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsFlagPersistence;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.kernel.template.MailTemplate;
import com.liferay.mail.kernel.template.MailTemplateContext;
import com.liferay.mail.kernel.template.MailTemplateContextBuilder;
import com.liferay.mail.kernel.template.MailTemplateFactoryUtil;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.EscapableLocalizableFunction;
import com.liferay.portal.kernel.util.EscapableObject;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.announcements.service.base.AnnouncementsEntryLocalServiceBaseImpl;

import jakarta.mail.internet.InternetAddress;

import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Roberto Díaz
 */
public class AnnouncementsEntryLocalServiceImpl
	extends AnnouncementsEntryLocalServiceBaseImpl {

	@Override
	public AnnouncementsEntry addEntry(
			long userId, long classNameId, long classPK, String title,
			String content, String url, String type, Date displayDate,
			Date expirationDate, int priority, boolean alert)
		throws PortalException {

		// Entry

		User user = _userPersistence.findByPrimaryKey(userId);

		validate(title, content, url, displayDate, expirationDate);

		long entryId = counterLocalService.increment();

		AnnouncementsEntry entry = announcementsEntryPersistence.create(
			entryId);

		entry.setCompanyId(user.getCompanyId());
		entry.setUserId(user.getUserId());
		entry.setUserName(user.getFullName());
		entry.setClassNameId(classNameId);
		entry.setClassPK(classPK);
		entry.setTitle(title);
		entry.setContent(content);
		entry.setUrl(url);
		entry.setType(type);
		entry.setDisplayDate(displayDate);
		entry.setExpirationDate(expirationDate);
		entry.setPriority(priority);
		entry.setAlert(alert);

		entry = announcementsEntryPersistence.update(entry);

		// Resources

		_resourceLocalService.addResources(
			user.getCompanyId(), 0, user.getUserId(),
			AnnouncementsEntry.class.getName(), entry.getEntryId(), false,
			false, false);

		return entry;
	}

	@Override
	public void checkEntries() throws PortalException {
		Date date = new Date();

		Date previousCheckDate = new Date(
			date.getTime() - _ANNOUNCEMENTS_ENTRY_CHECK_INTERVAL);

		checkEntries(previousCheckDate, date);
	}

	@Override
	public void checkEntries(Date startDate, Date endDate)
		throws PortalException {

		List<AnnouncementsEntry> entries =
			announcementsEntryFinder.findByDisplayDate(endDate, startDate);

		if (_log.isDebugEnabled()) {
			_log.debug("Processing " + entries.size() + " entries");
		}

		for (AnnouncementsEntry entry : entries) {
			notifyUsers(entry);
		}
	}

	@Override
	public void deleteEntries(long companyId) {
		_announcementsDeliveryPersistence.removeByCompanyId(companyId);

		_announcementsFlagPersistence.removeByCompanyId(companyId);

		announcementsEntryPersistence.removeByCompanyId(companyId);
	}

	@Override
	public void deleteEntries(long classNameId, long classPK)
		throws PortalException {

		List<AnnouncementsEntry> entries =
			announcementsEntryPersistence.findByC_C(classNameId, classPK);

		for (AnnouncementsEntry entry : entries) {
			deleteEntry(entry);
		}
	}

	@Override
	public void deleteEntries(long companyId, long classNameId, long classPK)
		throws PortalException {

		List<AnnouncementsEntry> entries =
			announcementsEntryPersistence.findByC_C_C(
				companyId, classNameId, classPK);

		for (AnnouncementsEntry entry : entries) {
			deleteEntry(entry);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteEntry(AnnouncementsEntry entry) throws PortalException {

		// Entry

		announcementsEntryPersistence.remove(entry);

		// Resources

		_resourceLocalService.deleteResource(
			entry.getCompanyId(), AnnouncementsEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, entry.getEntryId());

		// Flags

		_announcementsFlagLocalService.deleteFlags(entry.getEntryId());
	}

	@Override
	public void deleteEntry(long entryId) throws PortalException {
		AnnouncementsEntry entry =
			announcementsEntryPersistence.findByPrimaryKey(entryId);

		deleteEntry(entry);
	}

	@Override
	public List<AnnouncementsEntry> getEntries(
		long userId, LinkedHashMap<Long, long[]> scopes, boolean alert,
		int flagValue, int start, int end) {

		return getEntries(
			userId, scopes, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, alert, flagValue,
			start, end);
	}

	@Override
	public List<AnnouncementsEntry> getEntries(
		long userId, LinkedHashMap<Long, long[]> scopes, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue, int start,
		int end) {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return Collections.emptyList();
		}

		return announcementsEntryFinder.findByScopes(
			user.getCompanyId(), userId, scopes, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, alert, flagValue, start,
			end);
	}

	@Override
	public List<AnnouncementsEntry> getEntries(
		long companyId, long classNameId, long classPK, boolean alert,
		int start, int end) {

		return announcementsEntryPersistence.findByC_C_C_A(
			companyId, classNameId, classPK, alert, start, end);
	}

	@Override
	public List<AnnouncementsEntry> getEntries(
		long userId, long classNameId, long[] classPKs, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue, int start,
		int end) {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return Collections.emptyList();
		}

		return announcementsEntryFinder.findByScope(
			user.getCompanyId(), userId, classNameId, classPKs,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute, alert,
			flagValue, start, end);
	}

	@Override
	public int getEntriesCount(
		long userId, LinkedHashMap<Long, long[]> scopes, boolean alert,
		int flagValue) {

		return getEntriesCount(
			userId, scopes, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, alert, flagValue);
	}

	@Override
	public int getEntriesCount(
		long userId, LinkedHashMap<Long, long[]> scopes, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue) {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return 0;
		}

		return announcementsEntryFinder.countByScopes(
			user.getCompanyId(), userId, scopes, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, alert, flagValue);
	}

	@Override
	public int getEntriesCount(
		long companyId, long classNameId, long classPK, boolean alert) {

		return announcementsEntryPersistence.countByC_C_C_A(
			companyId, classNameId, classPK, alert);
	}

	@Override
	public int getEntriesCount(
		long userId, long classNameId, long[] classPKs, boolean alert,
		int flagValue) {

		return getEntriesCount(
			userId, classNameId, classPKs, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, alert,
			flagValue);
	}

	@Override
	public int getEntriesCount(
		long userId, long classNameId, long[] classPKs, int displayDateMonth,
		int displayDateDay, int displayDateYear, int displayDateHour,
		int displayDateMinute, int expirationDateMonth, int expirationDateDay,
		int expirationDateYear, int expirationDateHour,
		int expirationDateMinute, boolean alert, int flagValue) {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return 0;
		}

		return announcementsEntryFinder.countByScope(
			user.getCompanyId(), userId, classNameId, classPKs,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute, alert,
			flagValue);
	}

	@Override
	public AnnouncementsEntry getEntry(long entryId) throws PortalException {
		return announcementsEntryPersistence.findByPrimaryKey(entryId);
	}

	@Override
	public List<AnnouncementsEntry> getUserEntries(
		long userId, int start, int end) {

		return announcementsEntryPersistence.findByUserId(userId, start, end);
	}

	@Override
	public int getUserEntriesCount(long userId) {
		return announcementsEntryPersistence.countByUserId(userId);
	}

	@Override
	public AnnouncementsEntry updateEntry(
			long entryId, String title, String content, String url, String type,
			Date displayDate, Date expirationDate, int priority)
		throws PortalException {

		validate(title, content, url, displayDate, expirationDate);

		AnnouncementsEntry entry =
			announcementsEntryPersistence.findByPrimaryKey(entryId);

		entry.setTitle(title);
		entry.setContent(content);
		entry.setUrl(url);
		entry.setType(type);
		entry.setDisplayDate(displayDate);
		entry.setExpirationDate(expirationDate);
		entry.setPriority(priority);

		entry = announcementsEntryPersistence.update(entry);

		// Flags

		_announcementsFlagLocalService.deleteFlags(entry.getEntryId());

		return entry;
	}

	protected void notifyUsers(AnnouncementsEntry entry)
		throws PortalException {

		Company company = _companyPersistence.findByPrimaryKey(
			entry.getCompanyId());

		String className = entry.getClassName();
		long classPK = entry.getClassPK();

		String toName = PropsValues.ANNOUNCEMENTS_EMAIL_TO_NAME;

		long teamId = 0;

		LinkedHashMap<String, Object> params =
			LinkedHashMapBuilder.<String, Object>put(
				"announcementsDeliveryEmailOrSms", entry.getType()
			).build();

		if (classPK > 0) {
			if (className.equals(Group.class.getName())) {
				Group group = _groupPersistence.findByPrimaryKey(classPK);

				toName = group.getDescriptiveName();

				params.put("inherit", Boolean.TRUE);
				params.put("usersGroups", classPK);
			}
			else if (className.equals(Organization.class.getName())) {
				Organization organization =
					_organizationPersistence.findByPrimaryKey(classPK);

				toName = organization.getName();

				params.put("usersOrgsTree", ListUtil.fromArray(organization));
			}
			else if (className.equals(Role.class.getName())) {
				Role role = _rolePersistence.findByPrimaryKey(classPK);

				toName = role.getName();

				if (role.getType() == RoleConstants.TYPE_REGULAR) {
					params.put("inherit", Boolean.TRUE);
					params.put("usersRoles", classPK);
				}
				else if (role.isTeam()) {
					teamId = role.getClassPK();
				}
				else {
					params.put("userGroupRole", new Long[] {0L, classPK});
				}
			}
			else if (className.equals(UserGroup.class.getName())) {
				UserGroup userGroup = _userGroupPersistence.findByPrimaryKey(
					classPK);

				toName = userGroup.getName();

				params.put("usersUserGroups", classPK);
			}
		}

		if (className.equals(User.class.getName())) {
			User user = _userPersistence.findByPrimaryKey(classPK);

			if (Validator.isNull(user.getEmailAddress())) {
				return;
			}

			notifyUsers(
				ListUtil.fromArray(user), entry, company.getLocale(),
				user.getEmailAddress(), user.getFullName());
		}
		else {
			String toAddress = PropsValues.ANNOUNCEMENTS_EMAIL_TO_ADDRESS;

			notifyUsers(entry, teamId, params, toName, toAddress, company);
		}
	}

	protected void notifyUsers(
			AnnouncementsEntry entry, long teamId,
			LinkedHashMap<String, Object> params, String toName,
			String toAddress, Company company)
		throws PortalException {

		int total = 0;

		if (teamId > 0) {
			total = _userLocalService.getTeamUsersCount(teamId);
		}
		else {
			total = _userLocalService.searchCount(
				company.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
				params);
		}

		IntervalActionProcessor<Void> intervalActionProcessor =
			new IntervalActionProcessor<>(total);

		intervalActionProcessor.setPerformIntervalActionMethod(
			(start, end) -> {
				List<User> users = null;

				if (teamId > 0) {
					users = _userLocalService.getTeamUsers(teamId, start, end);
				}
				else {
					users = _userLocalService.search(
						company.getCompanyId(), null,
						WorkflowConstants.STATUS_APPROVED, params, start, end,
						(OrderByComparator<User>)null);
				}

				notifyUsers(
					users, entry, company.getLocale(), toAddress, toName);

				intervalActionProcessor.incrementStart(users.size());

				return null;
			});

		intervalActionProcessor.performIntervalActions();
	}

	protected void notifyUsers(
			List<User> users, AnnouncementsEntry entry, Locale locale,
			String toAddress, String toName)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug("Notifying " + users.size() + " users");
		}

		Map<String, String> notifyUsersFullNames = new HashMap<>();

		for (User user : users) {
			AnnouncementsDelivery announcementsDelivery =
				_announcementsDeliveryLocalService.getUserDelivery(
					user.getUserId(), entry.getType());

			if (announcementsDelivery.isEmail()) {
				notifyUsersFullNames.put(
					user.getEmailAddress(), user.getFullName());
			}

			if (announcementsDelivery.isSms()) {
				Contact contact = user.getContact();

				notifyUsersFullNames.put(
					contact.getSmsSn(), user.getFullName());
			}
		}

		if (notifyUsersFullNames.isEmpty()) {
			return;
		}

		Class<?> clazz = getClass();

		String body = null;

		try {
			body = StringUtil.read(
				clazz.getClassLoader(), PropsValues.ANNOUNCEMENTS_EMAIL_BODY);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " +
					PropsValues.ANNOUNCEMENTS_EMAIL_BODY,
				ioException);
		}

		String fromAddress = PrefsPropsUtil.getStringFromNames(
			entry.getCompanyId(), PropsKeys.ANNOUNCEMENTS_EMAIL_FROM_ADDRESS,
			PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);
		String fromName = PrefsPropsUtil.getStringFromNames(
			entry.getCompanyId(), PropsKeys.ANNOUNCEMENTS_EMAIL_FROM_NAME,
			PropsKeys.ADMIN_EMAIL_FROM_NAME);

		String subject = null;

		try {
			subject = StringUtil.read(
				clazz.getClassLoader(),
				PropsValues.ANNOUNCEMENTS_EMAIL_SUBJECT);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " +
					PropsValues.ANNOUNCEMENTS_EMAIL_SUBJECT,
				ioException);
		}

		Company company = _companyLocalService.getCompany(entry.getCompanyId());

		_sendNotificationEmail(
			fromAddress, fromName, toAddress, toName, subject, body, company,
			entry);

		for (Map.Entry<String, String> curEntry :
				notifyUsersFullNames.entrySet()) {

			_sendNotificationEmail(
				fromAddress, fromName, curEntry.getKey(), curEntry.getValue(),
				subject, body, company, entry);
		}
	}

	protected void validate(
			String title, String content, String url, Date displayDate,
			Date expirationDate)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new EntryTitleException();
		}

		int titleMaxLength = ModelHintsUtil.getMaxLength(
			AnnouncementsEntry.class.getName(), "title");

		if (title.length() > titleMaxLength) {
			throw new EntryTitleException(
				"Title has more than " + titleMaxLength + " characters");
		}

		if (Validator.isNull(content)) {
			throw new EntryContentException();
		}

		if (Validator.isNotNull(url) && !Validator.isUrl(url)) {
			throw new EntryURLException();
		}

		if ((expirationDate != null) &&
			(expirationDate.before(new Date()) ||
			 ((displayDate != null) && expirationDate.before(displayDate)))) {

			throw new EntryExpirationDateException(
				"Expiration date " + expirationDate + " is in the past");
		}
	}

	@BeanReference(type = MailService.class)
	protected MailService mailService;

	private void _sendNotificationEmail(
			String fromAddress, String fromName, String toAddress,
			String toName, String subject, String body, Company company,
			AnnouncementsEntry entry)
		throws PortalException {

		MailTemplateContextBuilder mailTemplateContextBuilder =
			MailTemplateFactoryUtil.createMailTemplateContextBuilder();

		mailTemplateContextBuilder.put(
			"[$COMPANY_ID$]", String.valueOf(company.getCompanyId()));
		mailTemplateContextBuilder.put("[$COMPANY_MX$]", company.getMx());
		mailTemplateContextBuilder.put(
			"[$COMPANY_NAME$]", new EscapableObject<>(company.getName()));
		mailTemplateContextBuilder.put("[$ENTRY_CONTENT$]", entry.getContent());
		mailTemplateContextBuilder.put(
			"[$ENTRY_ID$]", String.valueOf(entry.getEntryId()));
		mailTemplateContextBuilder.put(
			"[$ENTRY_TITLE$]", new EscapableObject<>(entry.getTitle()));
		mailTemplateContextBuilder.put(
			"[$ENTRY_TYPE$]",
			new EscapableLocalizableFunction(
				locale -> LanguageUtil.get(locale, entry.getType())));
		mailTemplateContextBuilder.put("[$ENTRY_URL$]", entry.getUrl());
		mailTemplateContextBuilder.put("[$FROM_ADDRESS$]", fromAddress);
		mailTemplateContextBuilder.put(
			"[$FROM_NAME$]", new EscapableObject<>(fromName));
		mailTemplateContextBuilder.put(
			"[$PORTAL_URL$]", company.getPortalURL(0));
		mailTemplateContextBuilder.put(
			"[$PORTLET_NAME$]",
			new EscapableLocalizableFunction(
				locale -> LanguageUtil.get(
					locale, entry.isAlert() ? "alert" : "announcement")));

		if (entry.getGroupId() > 0) {
			Group group = _groupLocalService.getGroup(entry.getGroupId());

			mailTemplateContextBuilder.put(
				"[$SITE_NAME$]",
				new EscapableObject<>(group.getDescriptiveName()));
		}

		mailTemplateContextBuilder.put("[$TO_ADDRESS$]", toAddress);
		mailTemplateContextBuilder.put(
			"[$TO_NAME$]", new EscapableObject<>(toName));

		MailTemplateContext mailTemplateContext =
			mailTemplateContextBuilder.build();

		try {
			MailTemplate subjectTemplate =
				MailTemplateFactoryUtil.createMailTemplate(subject, false);

			MailTemplate bodyTemplate =
				MailTemplateFactoryUtil.createMailTemplate(body, true);

			User user = _userLocalService.fetchUserByEmailAddress(
				entry.getCompanyId(), toAddress);

			Locale locale = LocaleUtil.getSiteDefault();

			if (user != null) {
				locale = user.getLocale();
			}

			MailMessage mailMessage = new MailMessage(
				new InternetAddress(fromAddress, fromName),
				new InternetAddress(toAddress, toName),
				subjectTemplate.renderAsString(locale, mailTemplateContext),
				bodyTemplate.renderAsString(locale, mailTemplateContext), true);

			mailMessage.setMessageId(
				PortalUtil.getMailId(
					company.getMx(), "announcements_entry",
					entry.getEntryId()));

			mailService.sendEmail(mailMessage);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private static final long _ANNOUNCEMENTS_ENTRY_CHECK_INTERVAL =
		PropsValues.ANNOUNCEMENTS_ENTRY_CHECK_INTERVAL * Time.MINUTE;

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsEntryLocalServiceImpl.class);

	@BeanReference(type = AnnouncementsDeliveryLocalService.class)
	private AnnouncementsDeliveryLocalService
		_announcementsDeliveryLocalService;

	@BeanReference(type = AnnouncementsDeliveryPersistence.class)
	private AnnouncementsDeliveryPersistence _announcementsDeliveryPersistence;

	@BeanReference(type = AnnouncementsFlagLocalService.class)
	private AnnouncementsFlagLocalService _announcementsFlagLocalService;

	@BeanReference(type = AnnouncementsFlagPersistence.class)
	private AnnouncementsFlagPersistence _announcementsFlagPersistence;

	@BeanReference(type = CompanyLocalService.class)
	private CompanyLocalService _companyLocalService;

	@BeanReference(type = CompanyPersistence.class)
	private CompanyPersistence _companyPersistence;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = OrganizationPersistence.class)
	private OrganizationPersistence _organizationPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = RolePersistence.class)
	private RolePersistence _rolePersistence;

	@BeanReference(type = UserGroupPersistence.class)
	private UserGroupPersistence _userGroupPersistence;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}