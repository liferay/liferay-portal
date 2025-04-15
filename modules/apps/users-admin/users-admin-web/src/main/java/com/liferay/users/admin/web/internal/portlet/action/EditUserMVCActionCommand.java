/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.ContactBirthdayException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredUserException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserFieldException;
import com.liferay.portal.kernel.exception.UserIdException;
import com.liferay.portal.kernel.exception.UserReminderQueryException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.portlet.DynamicActionRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.membershippolicy.MembershipPolicyException;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.InvokerPortletUtil;
import com.liferay.portlet.admin.util.AdminUtil;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Julio Camarero
 * @author Wesley Gong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.SERVICE_ACCOUNTS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/edit_user"
	},
	service = MVCActionCommand.class
)
public class EditUserMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	protected void deleteUsers(ActionRequest actionRequest) throws Exception {
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long[] deleteUserIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "deleteUserIds"), 0L);

		if (cmd.equals(Constants.DEACTIVATE)) {
			_updateUsers(
				actionRequest, deleteUserIds,
				WorkflowConstants.STATUS_INACTIVE);
		}
		else if (cmd.equals(Constants.DELETE)) {
			_deleteUsers(deleteUserIds);
		}
		else if (cmd.equals(Constants.RESTORE)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_userLocalService.validateMaxUsers(themeDisplay.getCompanyId());

			_updateUsers(
				actionRequest, deleteUserIds,
				WorkflowConstants.STATUS_APPROVED);
		}
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String portletId = _portal.getPortletId(actionRequest);

		if (portletId.equals(UsersAdminPortletKeys.MY_ACCOUNT) &&
			redirectToLogin(actionRequest, actionResponse)) {

			return;
		}

		actionRequest = _wrapActionRequest(actionRequest);

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			User user = null;
			String oldScreenName = StringPool.BLANK;
			boolean updateLanguageId = false;

			if (cmd.equals(Constants.ADD)) {
				user = _addUser(actionRequest);

				SessionMessages.add(actionRequest, "userAdded");

				hideDefaultSuccessMessage(actionRequest);
			}
			else if (cmd.equals(Constants.DEACTIVATE) ||
					 cmd.equals(Constants.DELETE) ||
					 cmd.equals(Constants.RESTORE)) {

				deleteUsers(actionRequest);
			}
			else if (cmd.equals("deleteRole")) {
				_deleteRole(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				Object[] returnValue = updateUser(
					actionRequest, actionResponse);

				user = (User)returnValue[0];
				oldScreenName = (String)returnValue[1];
				updateLanguageId = (Boolean)returnValue[2];
			}
			else if (cmd.equals("unlock")) {
				user = _updateLockout(actionRequest);
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (user != null) {
				if (Validator.isNotNull(oldScreenName)) {

					// This will fix the redirect if the user is on his personal
					// my account page and changes his screen name. A redirect
					// that references the old screen name no longer points to a
					// valid screen name and therefore needs to be updated.

					Group group = user.getGroup();

					if (group.getGroupId() == themeDisplay.getScopeGroupId()) {
						Layout layout = themeDisplay.getLayout();

						String friendlyURLPath = group.getPathFriendlyURL(
							layout.isPrivateLayout(), themeDisplay);

						String oldPath =
							friendlyURLPath + StringPool.SLASH + oldScreenName;
						String newPath =
							friendlyURLPath + StringPool.SLASH +
								user.getScreenName();

						redirect = StringUtil.replace(
							redirect, oldPath, newPath);

						redirect = StringUtil.replace(
							redirect, URLCodec.encodeURL(oldPath),
							URLCodec.encodeURL(newPath));
					}
				}

				if (updateLanguageId && themeDisplay.isI18n()) {
					String i18nLanguageId = user.getLanguageId();

					int pos = i18nLanguageId.indexOf(CharPool.UNDERLINE);

					if (pos != -1) {
						i18nLanguageId = i18nLanguageId.substring(0, pos);
					}

					String i18nPath = StringPool.SLASH + i18nLanguageId;

					redirect = StringUtil.replace(
						redirect, themeDisplay.getI18nPath(), i18nPath);
				}

				redirect = HttpComponentsUtil.setParameter(
					redirect, actionResponse.getNamespace() + "p_u_i_d",
					user.getUserId());
			}

			Group scopeGroup = themeDisplay.getScopeGroup();

			if (scopeGroup.isUser() &&
				(userLocalService.fetchUserById(scopeGroup.getClassPK()) ==
					null)) {

				redirect = HttpComponentsUtil.setParameter(
					redirect, "doAsGroupId", 0);
				redirect = HttpComponentsUtil.setParameter(
					redirect, "refererPlid", 0);
			}

			sendRedirect(actionRequest, actionResponse, redirect);
		}
		catch (Exception exception) {
			String mvcPath = "/edit_user.jsp";

			if (exception instanceof NoSuchUserException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				mvcPath = "/error.jsp";
			}
			else if (exception instanceof AssetCategoryException ||
					 exception instanceof AssetTagException ||
					 exception instanceof CompanyMaxUsersException ||
					 exception instanceof ContactBirthdayException ||
					 exception instanceof ContactNameException ||
					 exception instanceof GroupFriendlyURLException ||
					 exception instanceof MembershipPolicyException ||
					 exception instanceof NoSuchListTypeException ||
					 exception instanceof RequiredUserException ||
					 exception instanceof UserEmailAddressException ||
					 exception instanceof UserFieldException ||
					 exception instanceof UserIdException ||
					 exception instanceof UserReminderQueryException ||
					 exception instanceof UserScreenNameException) {

				if (exception instanceof NoSuchListTypeException) {
					NoSuchListTypeException noSuchListTypeException =
						(NoSuchListTypeException)exception;

					Class<?> clazz = exception.getClass();

					SessionErrors.add(
						actionRequest,
						clazz.getName() + noSuchListTypeException.getType());
				}
				else {
					SessionErrors.add(
						actionRequest, exception.getClass(), exception);
				}

				if (exception instanceof CompanyMaxUsersException ||
					exception instanceof RequiredUserException) {

					String redirect = portal.escapeRedirect(
						ParamUtil.getString(actionRequest, "redirect"));

					if (Validator.isNotNull(redirect)) {
						sendRedirect(actionRequest, actionResponse, redirect);

						return;
					}
				}

				actionResponse.setRenderParameter("mvcPath", mvcPath);

				throw exception;
			}
			else if (exception instanceof ModelListenerException) {
				if (exception.getCause() instanceof PortalException) {
					throw (PortalException)exception.getCause();
				}

				throw exception;
			}
			else {
				throw exception;
			}

			actionResponse.setRenderParameter("mvcPath", mvcPath);
		}
	}

	protected Object[] updateUser(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = portal.getSelectedUser(actionRequest);

		Contact contact = user.getContact();

		String oldPassword = AdminUtil.getUpdateUserPassword(
			actionRequest, user.getUserId());

		String oldScreenName = user.getScreenName();
		String screenName = BeanParamUtil.getString(
			user, actionRequest, "screenName");

		if (PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE)) {

			screenName = oldScreenName;
		}

		String oldEmailAddress = user.getEmailAddress();
		String emailAddress = BeanParamUtil.getString(
			user, actionRequest, "emailAddress");

		Company company = portal.getCompany(actionRequest);

		if (company.isUpdatePasswordRequired() &&
			(!screenName.equals(oldScreenName) ||
			 !emailAddress.equals(oldEmailAddress))) {

			int authResult = _userLocalService.authenticateByUserId(
				themeDisplay.getCompanyId(), portal.getUserId(actionRequest),
				ParamUtil.getString(actionRequest, "password"), new HashMap<>(),
				new HashMap<>(), new HashMap<>());

			if (authResult != Authenticator.SUCCESS) {
				throw new PrincipalException();
			}
		}

		boolean deleteLogo = ParamUtil.getBoolean(actionRequest, "deleteLogo");

		byte[] portraitBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			portraitBytes = FileUtil.getBytes(fileEntry.getContentStream());
		}

		String languageId = BeanParamUtil.getString(
			user, actionRequest, "languageId");
		String firstName = BeanParamUtil.getString(
			user, actionRequest, "firstName");
		String middleName = BeanParamUtil.getString(
			user, actionRequest, "middleName");
		String lastName = BeanParamUtil.getString(
			user, actionRequest, "lastName");
		long prefixListTypeId = BeanParamUtil.getInteger(
			contact, actionRequest, "prefixListTypeId");
		long suffixListTypeId = BeanParamUtil.getInteger(
			contact, actionRequest, "suffixListTypeId");
		boolean male = BeanParamUtil.getBoolean(
			user, actionRequest, "male", true);

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

		birthdayCal.setTime(contact.getBirthday());

		int birthdayMonth = ParamUtil.getInteger(
			actionRequest, "birthdayMonth", birthdayCal.get(Calendar.MONTH));
		int birthdayDay = ParamUtil.getInteger(
			actionRequest, "birthdayDay", birthdayCal.get(Calendar.DATE));
		int birthdayYear = ParamUtil.getInteger(
			actionRequest, "birthdayYear", birthdayCal.get(Calendar.YEAR));

		String comments = BeanParamUtil.getString(
			user, actionRequest, "comments");
		String jobTitle = BeanParamUtil.getString(
			user, actionRequest, "jobTitle");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		user = _userService.updateUser(
			user.getUserId(), oldPassword, null, null, user.isPasswordReset(),
			null, null, screenName, emailAddress, !deleteLogo, portraitBytes,
			languageId, user.getTimeZoneId(), user.getGreeting(), comments,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			male, birthdayMonth, birthdayDay, birthdayYear, contact.getSmsSn(),
			contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(), jobTitle, null, null,
			null, null, null, null, null, null, null, null, serviceContext);

		if (oldScreenName.equals(user.getScreenName())) {
			oldScreenName = StringPool.BLANK;
		}

		boolean updateLanguageId = false;

		if (user.getUserId() == themeDisplay.getUserId()) {

			// Reset the locale

			HttpServletRequest httpServletRequest =
				portal.getOriginalServletRequest(
					portal.getHttpServletRequest(actionRequest));
			HttpServletResponse httpServletResponse =
				portal.getHttpServletResponse(actionResponse);

			HttpSession httpSession = httpServletRequest.getSession();

			httpSession.removeAttribute(WebKeys.LOCALE);

			Locale locale = LocaleUtil.fromLanguageId(languageId);

			_language.updateCookie(
				httpServletRequest, httpServletResponse, locale);

			// Clear cached portlet responses

			InvokerPortletUtil.clearResponses(
				actionRequest.getPortletSession());

			updateLanguageId = true;
		}

		if (company.isStrangersVerify() &&
			!StringUtil.equalsIgnoreCase(oldEmailAddress, emailAddress)) {

			SessionMessages.add(actionRequest, "verificationEmailSent");

			hideDefaultSuccessMessage(actionRequest);
		}

		return new Object[] {user, oldScreenName, updateLanguageId};
	}

	@Reference
	protected Portal portal;

	@Reference
	protected UserLocalService userLocalService;

	private User _addUser(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean autoScreenName = ParamUtil.getBoolean(
			actionRequest, "autoScreenName");
		String screenName = ParamUtil.getString(actionRequest, "screenName");
		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		String languageId = ParamUtil.getString(actionRequest, "languageId");
		String firstName = ParamUtil.getString(actionRequest, "firstName");
		String middleName = ParamUtil.getString(actionRequest, "middleName");
		String lastName = ParamUtil.getString(actionRequest, "lastName");
		long prefixListTypeId = ParamUtil.getInteger(
			actionRequest, "prefixListTypeId");
		long suffixListTypeId = ParamUtil.getInteger(
			actionRequest, "suffixListTypeId");
		boolean male = ParamUtil.getBoolean(actionRequest, "male", true);
		int birthdayMonth = ParamUtil.getInteger(
			actionRequest, "birthdayMonth");
		int birthdayDay = ParamUtil.getInteger(actionRequest, "birthdayDay");
		int birthdayYear = ParamUtil.getInteger(actionRequest, "birthdayYear");
		String comments = ParamUtil.getString(actionRequest, "comments");
		String jobTitle = ParamUtil.getString(actionRequest, "jobTitle");
		long[] organizationIds = UsersAdminUtil.getOrganizationIds(
			actionRequest);
		boolean sendEmail = true;

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		User user = _userService.addUserWithWorkflow(
			themeDisplay.getCompanyId(), true, null, null, autoScreenName,
			screenName, emailAddress, LocaleUtil.fromLanguageId(languageId),
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, null,
			organizationIds, null, null, new ArrayList<Address>(),
			new ArrayList<EmailAddress>(), new ArrayList<Phone>(),
			new ArrayList<Website>(), new ArrayList<AnnouncementsDelivery>(),
			sendEmail, serviceContext);

		byte[] portraitBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			portraitBytes = FileUtil.getBytes(fileEntry.getContentStream());
		}

		if (portraitBytes != null) {
			user = userLocalService.updatePortrait(
				user.getUserId(), portraitBytes);
		}

		user.setComments(comments);

		return userLocalService.updateUser(user);
	}

	private void _deleteRole(ActionRequest actionRequest) throws Exception {
		User user = portal.getSelectedUser(actionRequest);

		long roleId = ParamUtil.getLong(actionRequest, "roleId");

		_userService.deleteRoleUser(roleId, user.getUserId());
	}

	private void _deleteUsers(long[] accountUserIds) throws Exception {
		for (long accountUserId : accountUserIds) {
			_userService.deleteUser(accountUserId);
		}
	}

	private long _getListTypeId(
			long companyId, PortletRequest portletRequest, String parameterName,
			String type)
		throws Exception {

		String parameterValue = ParamUtil.getString(
			portletRequest, parameterName);

		if (Validator.isNull(parameterValue)) {
			return 0;
		}

		ListType listType = _listTypeLocalService.addListType(
			companyId, parameterValue, type);

		return listType.getListTypeId();
	}

	private User _updateLockout(ActionRequest actionRequest) throws Exception {
		User user = portal.getSelectedUser(actionRequest);

		_userService.updateLockoutById(user.getUserId(), false);

		return user;
	}

	private void _updateUsers(
			ActionRequest actionRequest, long[] accountUserIds, int status)
		throws Exception {

		for (long accountUserId : accountUserIds) {
			_userService.updateStatus(
				accountUserId, status,
				ServiceContextFactory.getInstance(
					User.class.getName(), actionRequest));
		}
	}

	private ActionRequest _wrapActionRequest(ActionRequest actionRequest)
		throws Exception {

		DynamicActionRequest dynamicActionRequest = new DynamicActionRequest(
			actionRequest);

		long companyId = _portal.getCompanyId(actionRequest);

		long prefixListTypeId = _getListTypeId(
			companyId, actionRequest, "prefixListTypeValue",
			ListTypeConstants.CONTACT_PREFIX);

		dynamicActionRequest.setParameter(
			"prefixListTypeId", String.valueOf(prefixListTypeId));

		long suffixListTypeId = _getListTypeId(
			companyId, actionRequest, "suffixListTypeValue",
			ListTypeConstants.CONTACT_SUFFIX);

		dynamicActionRequest.setParameter(
			"suffixListTypeId", String.valueOf(suffixListTypeId));

		return dynamicActionRequest;
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Language _language;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}