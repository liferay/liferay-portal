/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.template;

import com.liferay.petra.function.UnsafeSupplierValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class UsersTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user1 = themeDisplay.getUser();

		contextObjects.put("is_default_user", user1.isDefaultUser());
		contextObjects.put(
			"is_female",
			new UnsafeSupplierValue<>(
				() -> {
					try {
						Contact contact = user1.getContact();

						return !contact.isMale();
					}
					catch (PortalException portalException) {
						_log.error(portalException);
					}

					return null;
				}));
		contextObjects.put("is_guest_user", user1.isGuestUser());
		contextObjects.put(
			"is_male",
			new UnsafeSupplierValue<>(
				() -> {
					try {
						Contact contact = user1.getContact();

						return contact.isMale();
					}
					catch (PortalException portalException) {
						_log.error(portalException);
					}

					return null;
				}));
		contextObjects.put("is_setup_complete", user1.isSetupComplete());

		contextObjects.put("language", themeDisplay.getLanguageId());
		contextObjects.put("language_id", user1.getLanguageId());
		contextObjects.put(
			"user2",
			new UnsafeSupplierValue<>(
				() -> {
					Group group = themeDisplay.getSiteGroup();

					if (group.isUser()) {
						try {
							return _userLocalService.getUserById(
								group.getClassPK());
						}
						catch (PortalException portalException) {
							_log.error(portalException);
						}
					}

					return null;
				}));
		contextObjects.put(
			"user_birthday",
			new UnsafeSupplierValue<>(
				() -> {
					try {
						Contact contact = user1.getContact();

						return contact.getBirthday();
					}
					catch (PortalException portalException) {
						_log.error(portalException);
					}

					return null;
				}));
		contextObjects.put("user_comments", user1.getComments());
		contextObjects.put("user_email_address", user1.getEmailAddress());
		contextObjects.put("user_first_name", user1.getFirstName());
		contextObjects.put(
			"user_greeting", HtmlUtil.escape(user1.getGreeting()));
		contextObjects.put("user_id", user1.getUserId());
		contextObjects.put("user_initialized", true);
		contextObjects.put("user_last_login_ip", user1.getLastLoginIP());
		contextObjects.put("user_last_name", user1.getLastName());
		contextObjects.put("user_login_ip", user1.getLoginIP());
		contextObjects.put("user_middle_name", user1.getMiddleName());
		contextObjects.put("user_name", user1.getFullName());
		contextObjects.put(
			"w3c_language_id",
			LocaleUtil.toW3cLanguageId(themeDisplay.getLanguageId()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UsersTemplateContextContributor.class);

	@Reference
	private UserLocalService _userLocalService;

}