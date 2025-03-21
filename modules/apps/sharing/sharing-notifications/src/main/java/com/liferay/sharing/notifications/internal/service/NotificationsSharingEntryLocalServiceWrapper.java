/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.notifications.internal.service;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.URLTemplateResource;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.sharing.constants.SharingPortletKeys;
import com.liferay.sharing.interpreter.SharingEntryInterpreter;
import com.liferay.sharing.interpreter.SharingEntryInterpreterProvider;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.notifications.internal.util.SharingNotificationSubcriptionSender;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalServiceWrapper;

import jakarta.portlet.PortletRequest;

import java.text.Format;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ServiceWrapper.class)
public class NotificationsSharingEntryLocalServiceWrapper
	extends SharingEntryLocalServiceWrapper {

	@Override
	public SharingEntry addSharingEntry(
			String externalReferenceCode, long fromUserId, long userGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			Collection<SharingEntryAction> sharingEntryActions,
			Date expirationDate, ServiceContext serviceContext)
		throws PortalException {

		SharingEntry sharingEntry = super.addSharingEntry(
			externalReferenceCode, fromUserId, userGroupId, toUserId,
			classNameId, classPK, groupId, shareable, sharingEntryActions,
			expirationDate, serviceContext);

		_sendNotificationEvent(
			sharingEntry,
			UserNotificationDefinition.NOTIFICATION_TYPE_ADD_ENTRY,
			serviceContext);

		return sharingEntry;
	}

	@Override
	public SharingEntry updateSharingEntry(
			long sharingEntryId,
			Collection<SharingEntryAction> sharingEntryActions,
			boolean shareable, Date expirationDate,
			ServiceContext serviceContext)
		throws PortalException {

		SharingEntry sharingEntry = super.updateSharingEntry(
			sharingEntryId, sharingEntryActions, shareable, expirationDate,
			serviceContext);

		_sendNotificationEvent(
			sharingEntry,
			UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY,
			serviceContext);

		return sharingEntry;
	}

	@Override
	public SharingEntry updateSharingEntry(
			long userId, long sharingEntryId,
			Collection<SharingEntryAction> sharingEntryActions,
			boolean shareable, Date expirationDate,
			ServiceContext serviceContext)
		throws PortalException {

		SharingEntry sharingEntry = super.updateSharingEntry(
			userId, sharingEntryId, sharingEntryActions, shareable,
			expirationDate, serviceContext);

		_sendNotificationEvent(
			sharingEntry,
			UserNotificationDefinition.NOTIFICATION_TYPE_UPDATE_ENTRY,
			serviceContext);

		return sharingEntry;
	}

	private String _getActionName(
		SharingEntry sharingEntry, ResourceBundle resourceBundle) {

		if (sharingEntry.hasSharingPermission(SharingEntryAction.UPDATE)) {
			return ResourceBundleUtil.getString(resourceBundle, "updating");
		}
		else if (sharingEntry.hasSharingPermission(
					SharingEntryAction.ADD_DISCUSSION)) {

			return ResourceBundleUtil.getString(resourceBundle, "commenting");
		}
		else if (sharingEntry.hasSharingPermission(SharingEntryAction.VIEW)) {
			return ResourceBundleUtil.getString(resourceBundle, "viewing");
		}

		return ResourceBundleUtil.getString(resourceBundle, "nothing");
	}

	private String _getNotificationEmailBody(
			SharingEntry sharingEntry, PortletRequest portletRequest)
		throws Exception {

		Class<?> clazz = getClass();

		String templateId =
			"/com/liferay/sharing/notifications/dependencies" +
				"/sharing_entry_added_email_body.ftl";

		URLTemplateResource templateResource = new URLTemplateResource(
			templateId, clazz.getResource(templateId));

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, templateResource, false);

		User toUser = _userLocalService.fetchUser(sharingEntry.getToUserId());

		Locale locale = LocaleUtil.getDefault();

		if (toUser != null) {
			locale = toUser.getLocale();
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		String emailActionTitle = StringPool.BLANK;

		SharingEntryInterpreter sharingEntryInterpreter =
			_getSharingEntryInterpreter(sharingEntry);

		if (sharingEntryInterpreter != null) {
			emailActionTitle = ResourceBundleUtil.getString(
				resourceBundle, "view-x",
				sharingEntryInterpreter.getAssetTypeTitle(
					sharingEntry, resourceBundle.getLocale()));
		}
		else {
			emailActionTitle = ResourceBundleUtil.getString(
				resourceBundle, "view");
		}

		template.put("actionTitle", emailActionTitle);

		template.put(
			"content",
			_getNotificationMessage(
				sharingEntry, resourceBundle.getLocale(), portletRequest));

		if (portletRequest != null) {
			template.put(
				"sharingEntryURL",
				_getNotificationURL(sharingEntry, portletRequest));
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		return unsyncStringWriter.toString();
	}

	private String _getNotificationMessage(
			SharingEntry sharingEntry, Locale locale,
			PortletRequest portletRequest)
		throws Exception {

		String languageKey = "x-has-shared-x-with-you-for-x";

		if (sharingEntry.getExpirationDate() != null) {
			languageKey = "x-has-shared-x-with-you-for-x-until-x";
		}

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			locale, getClass());

		User user = _userLocalService.fetchUser(sharingEntry.getUserId());

		String fromUserName = StringPool.BLANK;

		if (user != null) {
			fromUserName = HtmlUtil.escape(user.getFullName());
		}
		else {
			fromUserName = ResourceBundleUtil.getString(
				resourceBundle, "someone");
		}

		String expirationDateString = StringPool.BLANK;

		if (sharingEntry.getExpirationDate() != null) {
			Format expirationDateFormat = DateFormatFactoryUtil.getDate(locale);

			expirationDateString = expirationDateFormat.format(
				sharingEntry.getExpirationDate());
		}

		return ResourceBundleUtil.getString(
			resourceBundle, languageKey, fromUserName,
			_getSharingEntryObjectTitle(
				sharingEntry, resourceBundle, portletRequest),
			_getActionName(sharingEntry, resourceBundle), expirationDateString);
	}

	private String _getNotificationURL(
			SharingEntry sharingEntry, PortletRequest portletRequest)
		throws Exception {

		if (portletRequest != null) {
			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					portletRequest, SharingEntry.class.getName(),
					PortletProvider.Action.PREVIEW)
			).setParameter(
				"classNameId", sharingEntry.getClassNameId()
			).setParameter(
				"classPK", sharingEntry.getClassPK()
			).setParameter(
				"sharingEntryId", sharingEntry.getSharingEntryId()
			).buildString();
		}

		return null;
	}

	private SharingEntryInterpreter _getSharingEntryInterpreter(
		SharingEntry sharingEntry) {

		return _sharingEntryInterpreterProvider.getSharingEntryInterpreter(
			sharingEntry);
	}

	private String _getSharingEntryObjectTitle(
			SharingEntry sharingEntry, ResourceBundle resourceBundle,
			PortletRequest portletRequest)
		throws Exception {

		SharingEntryInterpreter sharingEntryInterpreter =
			_getSharingEntryInterpreter(sharingEntry);

		String title = StringPool.BLANK;

		if (sharingEntryInterpreter != null) {
			title = sharingEntryInterpreter.getTitle(sharingEntry);
		}
		else {
			title = ResourceBundleUtil.getString(resourceBundle, "something");
		}

		if (portletRequest != null) {
			return StringBundler.concat(
				"<a href=\"", _getNotificationURL(sharingEntry, portletRequest),
				"\" style=\"color: #0b5fff; text-decoration: none;\">",
				HtmlUtil.escape(title), "</a>");
		}

		return title;
	}

	private void _sendNotificationEvent(
		SharingEntry sharingEntry, int notificationType,
		ServiceContext serviceContext) {

		try {
			if (sharingEntry.getToUserId() > 0) {
				_sendNotificationEvent(
					sharingEntry, notificationType, serviceContext,
					_userLocalService.getUser(sharingEntry.getToUserId()));

				return;
			}

			List<User> userGroupUsers = _userLocalService.getUserGroupUsers(
				sharingEntry.getToUserGroupId());

			for (User user : userGroupUsers) {
				_sendNotificationEvent(
					sharingEntry, notificationType, serviceContext, user);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to send notification for sharing entry: " +
					sharingEntry.getSharingEntryId(),
				exception);
		}
	}

	private void _sendNotificationEvent(
			SharingEntry sharingEntry, int notificationType,
			ServiceContext serviceContext, User user)
		throws Exception {

		SharingNotificationSubcriptionSender
			sharingNotificationSubcriptionSender =
				new SharingNotificationSubcriptionSender();

		sharingNotificationSubcriptionSender.setSubject(
			_getNotificationMessage(sharingEntry, user.getLocale(), null));

		String entryURL = _getNotificationURL(
			sharingEntry, serviceContext.getLiferayPortletRequest());

		sharingNotificationSubcriptionSender.setBody(
			_getNotificationEmailBody(
				sharingEntry, serviceContext.getLiferayPortletRequest()));

		sharingNotificationSubcriptionSender.setClassName(
			sharingEntry.getModelClassName());
		sharingNotificationSubcriptionSender.setClassPK(
			sharingEntry.getSharingEntryId());
		sharingNotificationSubcriptionSender.setCurrentUserId(
			serviceContext.getUserId());
		sharingNotificationSubcriptionSender.setEntryURL(entryURL);

		String fromName = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME);
		String fromAddress = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

		sharingNotificationSubcriptionSender.setFrom(fromAddress, fromName);

		sharingNotificationSubcriptionSender.setHtmlFormat(true);
		sharingNotificationSubcriptionSender.setMailId(
			"sharing_entry", sharingEntry.getSharingEntryId());
		sharingNotificationSubcriptionSender.setNotificationType(
			notificationType);
		sharingNotificationSubcriptionSender.setPortletId(
			SharingPortletKeys.SHARING);
		sharingNotificationSubcriptionSender.setScopeGroupId(
			sharingEntry.getGroupId());
		sharingNotificationSubcriptionSender.setServiceContext(serviceContext);

		sharingNotificationSubcriptionSender.addRuntimeSubscribers(
			user.getEmailAddress(), user.getFullName());

		sharingNotificationSubcriptionSender.flushNotificationsAsync();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationsSharingEntryLocalServiceWrapper.class);

	@Reference
	private SharingEntryInterpreterProvider _sharingEntryInterpreterProvider;

	@Reference
	private UserLocalService _userLocalService;

}