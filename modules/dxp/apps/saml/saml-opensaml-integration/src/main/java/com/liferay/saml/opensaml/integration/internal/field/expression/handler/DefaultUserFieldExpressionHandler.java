/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.field.expression.handler;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.exportimport.LDAPUserImporter;
import com.liferay.saml.opensaml.integration.field.expression.handler.UserFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.processor.context.ProcessorContext;
import com.liferay.saml.opensaml.integration.processor.context.UserProcessorContext;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.joda.time.DateTime;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"display.index:Integer=0", "prefix=", "processing.index:Integer=-1"
	},
	service = UserFieldExpressionHandler.class
)
public class DefaultUserFieldExpressionHandler
	implements UserFieldExpressionHandler {

	@Override
	public void bindProcessorContext(UserProcessorContext processorContext) {
		ProcessorContext.Bind<User> userBind = processorContext.bind(
			Integer.MIN_VALUE,
			(currentUser, newUser, serviceContext) -> newUser);

		userBind.mapString("emailAddress", User::setEmailAddress);
		userBind.mapString("firstName", User::setFirstName);
		userBind.mapString("lastName", User::setLastName);
		userBind.mapUnsafeString(
			"modifiedDate",
			(user, value) -> {
				DateTime dateTime = new DateTime(value);

				user.setModifiedDate(dateTime.toDate());
			});
		userBind.mapString(
			"screenName",
			(user, screenName) -> {
				if (_prefsProps.getBoolean(
						user.getCompanyId(),
						PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE)) {

					if (_log.isDebugEnabled()) {
						_log.debug(
							"Ignored incoming screen name because " +
								"autogeneration is configured");
					}
				}
				else {
					user.setScreenName(screenName);
				}
			});
		userBind.mapString("uuid", User::setUuid);

		processorContext.bind(_processingIndex, this::_updateUser);
	}

	@Override
	public User getLdapUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws Exception {

		if (userIdentifierExpression.equals(CompanyConstants.AUTH_TYPE_EA)) {
			return _ldapUserImporter.importUser(
				companyId, userIdentifier, StringPool.BLANK);
		}
		else if (userIdentifierExpression.equals(
					CompanyConstants.AUTH_TYPE_SN)) {

			return _ldapUserImporter.importUser(
				companyId, StringPool.BLANK, userIdentifier);
		}
		else if (userIdentifierExpression.equals("uuid")) {
			return _ldapUserImporter.importUserByUuid(
				companyId, userIdentifier);
		}

		return null;
	}

	@Override
	public String getSectionLabel(Locale locale) {
		return ResourceBundleUtil.getString(
			ResourceBundleUtil.getBundle(
				locale, DefaultUserFieldExpressionHandler.class),
			"basic-user-fields");
	}

	@Override
	public User getUser(
			long companyId, String userIdentifier,
			String userIdentifierExpression)
		throws PortalException {

		try {
			if (userIdentifierExpression.equals(
					CompanyConstants.AUTH_TYPE_EA)) {

				return _userLocalService.getUserByEmailAddress(
					companyId, userIdentifier);
			}
			else if (userIdentifierExpression.equals(
						CompanyConstants.AUTH_TYPE_SN)) {

				return _userLocalService.getUserByScreenName(
					companyId, userIdentifier);
			}
			else if (userIdentifierExpression.equals("uuid")) {
				return _userLocalService.getUserByUuidAndCompanyId(
					userIdentifier, companyId);
			}
		}
		catch (NoSuchUserException noSuchUserException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchUserException);
			}
		}

		return null;
	}

	@Override
	public List<String> getValidFieldExpressions() {
		return _validFieldExpressions;
	}

	@Override
	public boolean isSupportedForUserMatching(String userFieldExpression) {
		return _authFieldExpressions.contains(userFieldExpression);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_processingIndex = GetterUtil.getInteger(
			properties.get("processing.index"));
	}

	private User _addUser(User newUser, ServiceContext serviceContext)
		throws PortalException {

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = null;
		String password2 = null;
		boolean autoScreenName = false;
		int prefixListTypeId = 0;
		int suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		boolean sendEmail = false;

		if (!Validator.isBlank(newUser.getUuid())) {
			serviceContext.setUuid(newUser.getUuid());
		}

		User user = _userLocalService.addUser(
			creatorUserId, newUser.getCompanyId(), autoPassword, password1,
			password2, autoScreenName, newUser.getScreenName(),
			newUser.getEmailAddress(), serviceContext.getLocale(),
			newUser.getFirstName(), newUser.getMiddleName(),
			newUser.getLastName(), prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, newUser.getJobTitle(),
			UserConstants.TYPE_REGULAR, newUser.getGroupIds(),
			newUser.getOrganizationIds(), newUser.getRoleIds(),
			newUser.getUserGroupIds(), sendEmail, serviceContext);

		user = _userLocalService.updateEmailAddressVerified(
			user.getUserId(), true);

		user = _userLocalService.updatePasswordReset(user.getUserId(), false);

		if (newUser.getModifiedDate() != null) {
			user = _userLocalService.updateModifiedDate(
				user.getUserId(), newUser.getModifiedDate());
		}

		return user;
	}

	private User _updateUser(
			User currentUser, User newUser, ServiceContext serviceContext)
		throws PortalException {

		if (newUser.isNew()) {
			return _addUser(newUser, serviceContext);
		}

		currentUser = _userLocalService.getUserById(currentUser.getUserId());

		if (!Objects.equals(
				currentUser.getEmailAddress(), newUser.getEmailAddress())) {

			newUser = _userLocalService.updateEmailAddress(
				newUser.getUserId(), StringPool.BLANK,
				newUser.getEmailAddress(), newUser.getEmailAddress());

			newUser = _userLocalService.updateEmailAddressVerified(
				newUser.getUserId(), true);
		}

		if (Objects.equals(
				currentUser.getFirstName(), newUser.getFirstName()) &&
			Objects.equals(currentUser.getLastName(), newUser.getLastName()) &&
			Objects.equals(
				currentUser.getModifiedDate(), newUser.getModifiedDate()) &&
			Objects.equals(
				currentUser.getScreenName(), newUser.getScreenName()) &&
			Objects.equals(currentUser.getUuid(), newUser.getUuid())) {

			return newUser;
		}

		Contact contact = newUser.getContact();
		Calendar birthdayCalendar = CalendarFactoryUtil.getCalendar();

		birthdayCalendar.setTime(contact.getBirthday());

		Date modifiedDate = newUser.getModifiedDate();

		serviceContext.setUuid(newUser.getUuid());

		newUser = _userLocalService.updateUser(
			newUser.getUserId(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, false, newUser.getReminderQueryQuestion(),
			newUser.getReminderQueryAnswer(), newUser.getScreenName(),
			newUser.getEmailAddress(), true, null, newUser.getLanguageId(),
			newUser.getTimeZoneId(), newUser.getGreeting(),
			newUser.getComments(), newUser.getFirstName(),
			newUser.getMiddleName(), newUser.getLastName(),
			contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
			newUser.getMale(), birthdayCalendar.get(Calendar.MONTH),
			birthdayCalendar.get(Calendar.DATE),
			birthdayCalendar.get(Calendar.YEAR), contact.getSmsSn(),
			contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(), contact.getJobTitle(),
			null, null, null, null, null, serviceContext);

		if (!Objects.equals(
				currentUser.getModifiedDate(), newUser.getModifiedDate())) {

			newUser = _userLocalService.updateModifiedDate(
				newUser.getUserId(), modifiedDate);
		}

		return newUser;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultUserFieldExpressionHandler.class);

	private final Set<String> _authFieldExpressions = new HashSet<>(
		Arrays.asList("emailAddress", "screenName", "uuid"));

	@Reference
	private LDAPUserImporter _ldapUserImporter;

	@Reference
	private PrefsProps _prefsProps;

	private int _processingIndex;

	@Reference
	private UserLocalService _userLocalService;

	private final List<String> _validFieldExpressions =
		Collections.unmodifiableList(
			Arrays.asList(
				"emailAddress", "firstName", "lastName", "screenName", "uuid"));

}