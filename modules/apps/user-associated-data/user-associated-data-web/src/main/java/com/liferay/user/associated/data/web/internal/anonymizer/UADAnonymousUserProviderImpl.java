/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.anonymizer;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ContactConstants;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.pwd.PwdToolkitUtil;
import com.liferay.user.associated.data.anonymizer.UADAnonymousUserProvider;
import com.liferay.user.associated.data.web.internal.configuration.AnonymousUserConfiguration;
import com.liferay.user.associated.data.web.internal.configuration.AnonymousUserConfigurationRetriever;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Locale;

import org.osgi.service.cm.Configuration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 * @author Erick Monteiro
 */
@Component(service = UADAnonymousUserProvider.class)
public class UADAnonymousUserProviderImpl implements UADAnonymousUserProvider {

	@Override
	public User getAnonymousUser(long companyId) throws Exception {
		return _getAnonymousUser(companyId);
	}

	@Override
	public boolean isAnonymousUser(User user) {
		try {
			User anonymousUser = getAnonymousUser(user.getCompanyId());

			if (user.getUserId() == anonymousUser.getUserId()) {
				return true;
			}

			return false;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private User _addAnonymousUser(long companyId) throws Exception {
		User user = _userLocalService.createUser(
			_counterLocalService.increment());

		PasswordPolicy passwordPolicy =
			_passwordPolicyLocalService.getDefaultPasswordPolicy(companyId);

		String randomString = PwdToolkitUtil.generate(passwordPolicy);

		long counter = _counterLocalService.increment(
			UADAnonymousUserProvider.class.getName());

		String screenName = StringBundler.concat(
			"Anonymous", companyId, StringPool.UNDERLINE, counter);

		Company company = _companyLocalService.getCompany(companyId);

		String emailAddress = StringBundler.concat(
			screenName, StringPool.AT, company.getMx());

		Locale locale = LocaleThreadLocal.getDefaultLocale();
		String firstName = "Anonymous";
		String middleName = StringPool.BLANK;
		String lastName = "Anonymous";
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;

		user.setCompanyId(companyId);
		user.setContactId(_counterLocalService.increment());
		user.setPassword(randomString);
		user.setScreenName(screenName);
		user.setEmailAddress(emailAddress);
		user.setLanguageId(LocaleUtil.toLanguageId(locale));
		user.setComments(
			StringBundler.concat(
				"This user is automatically created by the UAD application. ",
				"Application data anonymized by Personal Data Erasure will be ",
				"assigned to this user."));
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setJobTitle(jobTitle);
		user.setType(UserConstants.TYPE_REGULAR);
		user.setStatus(WorkflowConstants.STATUS_INCOMPLETE);

		_userLocalService.addUser(user);

		_groupLocalService.addGroup(
			StringPool.BLANK, user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, User.class.getName(),
			user.getUserId(), GroupConstants.DEFAULT_LIVE_GROUP_ID, null, null,
			0, null, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			StringPool.SLASH + screenName, false, false, true, null);

		Contact contact = _contactLocalService.createContact(
			user.getContactId());

		contact.setCompanyId(companyId);
		contact.setUserId(user.getUserId());
		contact.setUserName(user.getFullName());
		contact.setClassName(User.class.getName());
		contact.setClassPK(user.getUserId());
		contact.setParentContactId(ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contact.setEmailAddress(user.getEmailAddress());
		contact.setFirstName(firstName);
		contact.setMiddleName(middleName);
		contact.setLastName(lastName);
		contact.setPrefixListTypeId(prefixListTypeId);
		contact.setSuffixListTypeId(suffixListTypeId);
		contact.setMale(true);
		contact.setBirthday(
			_portal.getDate(birthdayMonth, birthdayDay, birthdayYear));
		contact.setJobTitle(jobTitle);

		_contactLocalService.addContact(contact);

		return user;
	}

	private User _getAnonymousUser(long companyId) throws Exception {
		Configuration configuration = _anonymousUserConfigurationRetriever.get(
			companyId);

		if (configuration == null) {
			User anonymousUser = _addAnonymousUser(companyId);

			_configurationProvider.saveCompanyConfiguration(
				AnonymousUserConfiguration.class, companyId,
				HashMapDictionaryBuilder.<String, Object>put(
					"companyId", companyId
				).put(
					"userId", anonymousUser.getUserId()
				).build());

			return _updateStatus(anonymousUser);
		}

		Dictionary<String, Object> properties = configuration.getProperties();

		AnonymousUserConfiguration anonymousUserConfiguration =
			ConfigurableUtil.createConfigurable(
				AnonymousUserConfiguration.class, properties);

		User anonymousUser = _userLocalService.fetchUser(
			anonymousUserConfiguration.userId());

		if (anonymousUser != null) {
			return anonymousUser;
		}

		anonymousUser = _addAnonymousUser(companyId);

		properties.put("userId", anonymousUser.getUserId());

		configuration.update(properties);

		return _updateStatus(anonymousUser);
	}

	private User _updateStatus(User anonymousUser) throws Exception {
		return _userLocalService.updateStatus(
			anonymousUser, WorkflowConstants.STATUS_INACTIVE,
			new ServiceContext());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UADAnonymousUserProviderImpl.class);

	@Reference
	private AnonymousUserConfigurationRetriever
		_anonymousUserConfigurationRetriever;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ContactLocalService _contactLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}