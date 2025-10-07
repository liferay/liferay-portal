/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.portlet;

import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.announcements.kernel.service.AnnouncementsDeliveryLocalService;
import com.liferay.contacts.constants.ContactsConstants;
import com.liferay.contacts.constants.SocialRelationConstants;
import com.liferay.contacts.exception.DuplicateEntryEmailAddressException;
import com.liferay.contacts.exception.EntryEmailAddressException;
import com.liferay.contacts.model.Entry;
import com.liferay.contacts.service.EntryLocalService;
import com.liferay.contacts.util.ContactsUtil;
import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.AddressCityException;
import com.liferay.portal.kernel.exception.AddressStreetException;
import com.liferay.portal.kernel.exception.AddressZipException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PhoneNumberException;
import com.liferay.portal.kernel.exception.PhoneNumberExtensionException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.exception.UserSmsException;
import com.liferay.portal.kernel.exception.WebsiteURLException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.UserLastNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.social.kernel.exception.NoSuchRelationException;
import com.liferay.social.kernel.model.SocialRelation;
import com.liferay.social.kernel.model.SocialRequest;
import com.liferay.social.kernel.model.SocialRequestConstants;
import com.liferay.social.kernel.service.SocialRelationLocalService;
import com.liferay.social.kernel.service.SocialRequestLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.InputStream;

import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Jonathan Lee
 * @author Eudaldo Alonso
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	configurationPid = "com.liferay.users.admin.configuration.UserFileUploadsConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=contacts-portlet",
		"com.liferay.portlet.display-category=category.social",
		"com.liferay.portlet.friendly-url-mapping=contacts",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/main.js",
		"com.liferay.portlet.icon=/icons/contacts_center.png",
		"jakarta.portlet.display-name=Contacts Center",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.info.keywords=Contacts Center",
		"jakarta.portlet.info.short-title=Contacts Center",
		"jakarta.portlet.info.title=Contacts Center",
		"jakarta.portlet.init-param.config-template=/configuration.jsp",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + ContactsPortletKeys.CONTACTS_CENTER,
		"jakarta.portlet.portlet-mode=text/html;config",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
@Deprecated
public class ContactsCenterPortlet extends MVCPortlet {

	public void addSocialRelation(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		int type = ParamUtil.getInteger(actionRequest, "type");

		if (type == SocialRelationConstants.TYPE_BI_CONNECTION) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (long userId : _getUserIds(actionRequest)) {
			if (userId == themeDisplay.getUserId()) {
				continue;
			}

			boolean blocked = socialRelationLocalService.hasRelation(
				userId, themeDisplay.getUserId(),
				SocialRelationConstants.TYPE_UNI_ENEMY);

			if (type == SocialRelationConstants.TYPE_UNI_ENEMY) {
				socialRelationLocalService.deleteRelations(
					themeDisplay.getUserId(), userId);

				socialRelationLocalService.deleteRelations(
					userId, themeDisplay.getUserId());
			}
			else if (blocked) {
				continue;
			}

			socialRelationLocalService.addRelation(
				themeDisplay.getUserId(), userId, type);

			if (blocked) {
				socialRelationLocalService.addRelation(
					userId, themeDisplay.getUserId(), type);
			}
		}
	}

	public void deleteSocialRelation(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] userIds = _getUserIds(actionRequest);

		int type = ParamUtil.getInteger(actionRequest, "type");

		for (long userId : userIds) {
			if (userId == themeDisplay.getUserId()) {
				continue;
			}

			try {
				socialRelationLocalService.deleteRelation(
					themeDisplay.getUserId(), userId, type);
			}
			catch (NoSuchRelationException noSuchRelationException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchRelationException);
				}
			}
		}
	}

	public void exportVCard(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long userId = ParamUtil.getLong(resourceRequest, "userId");

		User user = userService.getUserById(userId);

		String vCard = ContactsUtil.getVCard(user);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			URLEncoder.encode(user.getFullName(), "UTF-8") + ".vcf",
			vCard.getBytes(StringPool.UTF8), "text/x-vcard; charset=UTF-8");
	}

	public void exportVCards(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long[] userIds = StringUtil.split(
			ParamUtil.getString(resourceRequest, "userIds"), 0L);

		List<User> users = new ArrayList<>(userIds.length);

		for (long userId : userIds) {
			User user = userService.getUserById(userId);

			users.add(user);
		}

		String vCards = ContactsUtil.getVCards(users);

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, "vcards.vcf",
			vCards.getBytes(StringPool.UTF8), "text/x-vcard; charset=UTF-8");
	}

	public void getContact(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"success", Boolean.TRUE
			).put(
				"user",
				_getUserJSONObject(
					resourceResponse,
					(ThemeDisplay)resourceRequest.getAttribute(
						WebKeys.THEME_DISPLAY),
					ParamUtil.getLong(resourceRequest, "userId"))
			));
	}

	public void getContacts(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject contactListJSONObject = _getContactsJSONObject(
			resourceRequest, resourceResponse);

		writeJSON(resourceRequest, resourceResponse, contactListJSONObject);
	}

	public void getSelectedContacts(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] userIds = StringUtil.split(
			ParamUtil.getString(resourceRequest, "userIds"), 0L);

		JSONArray jsonArray = jsonFactory.createJSONArray();

		for (long userId : userIds) {
			try {
				jsonArray.put(
					JSONUtil.put(
						"success", Boolean.TRUE
					).put(
						"user",
						_getUserJSONObject(
							resourceResponse, themeDisplay, userId)
					));
			}
			catch (NoSuchUserException noSuchUserException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchUserException);
				}
			}
		}

		JSONObject jsonObject = JSONUtil.put("contacts", jsonArray);

		writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return;
		}

		try {
			String actionName = ParamUtil.getString(
				actionRequest, ActionRequest.ACTION_NAME);

			boolean jsonFormat = ParamUtil.getBoolean(
				actionRequest, "jsonFormat");

			if (jsonFormat) {
				if (actionName.equals("addSocialRelation")) {
					addSocialRelation(actionRequest, actionResponse);
				}
				else if (actionName.equals("deleteSocialRelation")) {
					deleteSocialRelation(actionRequest, actionResponse);
				}
				else if (actionName.equals("requestSocialRelation")) {
					requestSocialRelation(actionRequest, actionResponse);
				}

				JSONObject jsonObject = _getContactsDisplayJSONObject(
					actionRequest, actionResponse);

				writeJSON(actionRequest, actionResponse, jsonObject);
			}
			else if (actionName.equals("deleteEntry")) {
				_deleteEntry(actionRequest);
			}
			else if (actionName.equals("updateEntry")) {
				updateEntry(actionRequest, actionResponse);
			}
			else if (actionName.equals("updateFieldGroup")) {
				updateFieldGroup(actionRequest, actionResponse);
			}
			else if (actionName.equals("updateSocialRequest")) {
				updateSocialRequest(actionRequest, actionResponse);
			}
			else {
				super.processAction(actionRequest, actionResponse);
			}
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	public void requestSocialRelation(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] userIds = _getUserIds(actionRequest);

		int type = ParamUtil.getInteger(actionRequest, "type");

		for (long userId : userIds) {
			if ((userId == themeDisplay.getUserId()) ||
				socialRelationLocalService.hasRelation(
					userId, themeDisplay.getUserId(),
					SocialRelationConstants.TYPE_BI_CONNECTION) ||
				socialRelationLocalService.hasRelation(
					userId, themeDisplay.getUserId(),
					SocialRelationConstants.TYPE_UNI_ENEMY) ||
				socialRequestLocalService.hasRequest(
					themeDisplay.getUserId(), User.class.getName(),
					themeDisplay.getUserId(), type, userId,
					SocialRequestConstants.STATUS_PENDING)) {

				continue;
			}

			String portletId = PortletIdCodec.decodePortletName(
				portal.getPortletId(actionRequest));

			if (portletId.equals(ContactsPortletKeys.MEMBERS) ||
				portletId.equals(ContactsPortletKeys.PROFILE)) {

				portletId = ContactsPortletKeys.CONTACTS_CENTER;
			}

			JSONObject extraDataJSONObject = JSONUtil.put(
				"portletId", portletId);

			SocialRequest socialRequest = socialRequestLocalService.addRequest(
				themeDisplay.getUserId(), 0, User.class.getName(),
				themeDisplay.getUserId(), type, extraDataJSONObject.toString(),
				userId);

			_sendNotificationEvent(socialRequest);
		}
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			String resourceID = resourceRequest.getResourceID();

			if (resourceID.equals("exportVCard")) {
				exportVCard(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("exportVCards")) {
				exportVCards(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("getContact")) {
				getContact(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("getContacts")) {
				getContacts(resourceRequest, resourceResponse);
			}
			else if (resourceID.equals("getSelectedContacts")) {
				getSelectedContacts(resourceRequest, resourceResponse);
			}
			else {
				super.serveResource(resourceRequest, resourceResponse);
			}
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	public void updateEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		String fullName = ParamUtil.getString(actionRequest, "fullName");
		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		String comments = ParamUtil.getString(actionRequest, "comments");

		JSONObject jsonObject = jsonFactory.createJSONObject();

		String message = null;

		try {
			Entry entry = null;

			if (entryId > 0) {
				entry = entryLocalService.getEntry(entryId);

				if (entry.getUserId() == themeDisplay.getUserId()) {
					entry = entryLocalService.updateEntry(
						entryId, fullName, emailAddress, comments);

					message = "you-have-successfully-updated-the-contact";
				}
			}
			else {
				entry = entryLocalService.addEntry(
					themeDisplay.getUserId(), fullName, emailAddress, comments);

				message = "you-have-successfully-added-a-new-contact";
			}

			jsonObject.put(
				"contact",
				_getEntryJSONObject(
					actionResponse, themeDisplay, entry, redirect)
			).put(
				"contactList",
				_getContactsJSONObject(actionRequest, actionResponse)
			).put(
				"success", Boolean.TRUE
			);
		}
		catch (Exception exception) {
			if (exception instanceof
					ContactNameException.MustHaveValidFullName) {

				message = "full-name-cannot-be-empty";
			}
			else if (exception instanceof DuplicateEntryEmailAddressException) {
				message = "there-is-already-a-contact-with-this-email-address";
			}
			else if (exception instanceof EntryEmailAddressException) {
				message = "please-enter-a-valid-email-address";
			}
			else {
				message =
					"an-error-occurred-while-processing-the-requested-resource";
			}

			jsonObject.put("success", Boolean.FALSE);
		}

		jsonObject.put("message", translate(actionRequest, message));

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void updateFieldGroup(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = jsonFactory.createJSONObject();

		try {
			String fieldGroup = ParamUtil.getString(
				actionRequest, "fieldGroup");

			if (fieldGroup.equals("additionalEmailAddresses")) {
				_updateAdditionalEmailAddresses(actionRequest);
			}
			else if (fieldGroup.equals("addresses")) {
				_updateAddresses(actionRequest);
			}
			else if (fieldGroup.equals("categorization")) {
				_updateAsset(actionRequest);
			}
			else if (fieldGroup.equals("comments") ||
					 fieldGroup.equals("details") ||
					 fieldGroup.equals("instantMessenger") ||
					 fieldGroup.equals("sms") ||
					 fieldGroup.equals("socialNetwork")) {

				_updateProfile(actionRequest);
			}
			else if (fieldGroup.equals("phoneNumbers")) {
				_updatePhoneNumbers(actionRequest);
			}
			else if (fieldGroup.equals("websites")) {
				_updateWebsites(actionRequest);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			jsonObject.put(
				"redirect", redirect
			).put(
				"success", Boolean.TRUE
			);
		}
		catch (Exception exception) {
			String message = "your-request-failed-to-complete";

			if (exception instanceof AddressCityException) {
				message = "please-enter-a-valid-city";
			}
			else if (exception instanceof AddressStreetException) {
				message = "please-enter-a-valid-street";
			}
			else if (exception instanceof AddressZipException) {
				message = "please-enter-a-valid-postal-code";
			}
			else if (exception instanceof
						ContactNameException.MustHaveFirstName) {

				message = "please-enter-a-valid-first-name";
			}
			else if (exception instanceof
						ContactNameException.MustHaveValidFullName) {

				message = "please-enter-a-valid-first-middle-and-last-name";
			}
			else if (exception instanceof
						ContactNameException.MustHaveLastName) {

				message = "please-enter-a-valid-last-name";
			}
			else if (exception instanceof
						UserEmailAddressException.MustNotBeDuplicate) {

				message = "the-email-address-you-requested-is-already-taken";
			}
			else if (exception instanceof EmailAddressException) {
				message = "please-enter-a-valid-email-address";
			}
			else if (exception instanceof NoSuchCountryException) {
				message = "please-select-a-country";
			}
			else if (exception instanceof NoSuchListTypeException) {
				message = "please-select-a-type";
			}
			else if (exception instanceof NoSuchRegionException) {
				message = "please-select-a-region";
			}
			else if (exception instanceof PhoneNumberException) {
				message = "please-enter-a-valid-phone-number";
			}
			else if (exception instanceof PhoneNumberExtensionException) {
				message = "please-enter-a-valid-phone-number-extension";
			}
			else if (exception instanceof
						UserEmailAddressException.MustNotBeReserved) {

				message = "the-email-address-you-requested-is-reserveds";
			}
			else if (exception instanceof
						UserScreenNameException.MustNotBeReserved) {

				message = "the-screen-name-you-requested-is-reserved";
			}
			else if (exception instanceof UserEmailAddressException) {
				message = "please-enter-a-valid-email-address";
			}
			else if (exception instanceof UserScreenNameException) {
				message = "please-enter-a-valid-screen-name";
			}
			else if (exception instanceof UserSmsException) {
				message = "please-enter-a-sms-id-that-is-a-valid-email-address";
			}
			else if (exception instanceof WebsiteURLException) {
				message = "please-enter-a-valid-url";
			}

			jsonObject.put(
				"message", translate(actionRequest, message)
			).put(
				"success", Boolean.FALSE
			);
		}

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	public void updateSocialRequest(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long socialRequestId = ParamUtil.getLong(
			actionRequest, "socialRequestId");
		int status = ParamUtil.getInteger(actionRequest, "status");

		JSONObject jsonObject = jsonFactory.createJSONObject();

		try {
			SocialRequest socialRequest =
				socialRequestLocalService.getSocialRequest(socialRequestId);

			if (socialRelationLocalService.hasRelation(
					socialRequest.getReceiverUserId(),
					socialRequest.getUserId(),
					SocialRelationConstants.TYPE_UNI_ENEMY)) {

				status = SocialRequestConstants.STATUS_IGNORE;
			}

			socialRequestLocalService.updateRequest(
				socialRequestId, status, themeDisplay);

			if (status == SocialRequestConstants.STATUS_CONFIRM) {
				socialRelationLocalService.addRelation(
					socialRequest.getUserId(),
					socialRequest.getReceiverUserId(), socialRequest.getType());
			}

			long userNotificationEventId = ParamUtil.getLong(
				actionRequest, "userNotificationEventId");

			if (userNotificationEventId != 0) {
				_updateArchived(
					themeDisplay.getUserId(), userNotificationEventId);
			}

			jsonObject.put("success", Boolean.TRUE);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			jsonObject.put("success", Boolean.FALSE);
		}

		writeJSON(actionRequest, actionResponse, jsonObject);
	}

	@Reference
	protected AnnouncementsDeliveryLocalService
		announcementsDeliveryLocalService;

	@Reference
	protected DLAppLocalService dlAppLocalService;

	@Reference
	protected EntryLocalService entryLocalService;

	@Reference
	protected File file;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Portal portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.contacts.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	protected Release release;

	@Reference
	protected RoleLocalService roleLocalService;

	@Reference
	protected SocialRelationLocalService socialRelationLocalService;

	@Reference
	protected SocialRequestLocalService socialRequestLocalService;

	@Reference
	protected UserLocalService userLocalService;

	@Reference
	protected UserNotificationEventLocalService
		userNotificationEventLocalService;

	@Reference
	protected UserService userService;

	private void _deleteEntry(ActionRequest actionRequest) throws Exception {
		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		if (entryId > 0) {
			Entry entry = entryLocalService.getEntry(entryId);
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			if (entry.getUserId() == themeDisplay.getUserId()) {
				entryLocalService.deleteEntry(entryId);
			}
		}
	}

	private JSONObject _getContactsDisplayJSONObject(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] userIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "userIds"), 0L);

		JSONObject contactListJSONObject = _getContactsJSONObject(
			actionRequest, actionResponse);

		JSONArray jsonArray = jsonFactory.createJSONArray();

		for (long userId : userIds) {
			jsonArray.put(
				JSONUtil.put(
					"success", Boolean.TRUE
				).put(
					"user",
					_getUserJSONObject(actionResponse, themeDisplay, userId)
				));
		}

		return JSONUtil.put(
			"contactList", contactListJSONObject
		).put(
			"contacts", jsonArray
		).put(
			"message",
			translate(actionRequest, _getRelationMessage(actionRequest))
		);
	}

	private JSONObject _getContactsJSONObject(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String redirect = ParamUtil.getString(portletRequest, "redirect");

		String filterBy = ParamUtil.getString(portletRequest, "filterBy");
		String keywords = ParamUtil.getString(portletRequest, "keywords");
		int start = ParamUtil.getInteger(portletRequest, "start");
		int end = ParamUtil.getInteger(portletRequest, "end");

		JSONObject jsonObject = JSONUtil.put(
			"options",
			JSONUtil.put(
				"end", end
			).put(
				"filterBy", filterBy
			).put(
				"keywords", keywords
			).put(
				"start", start
			));

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getId();

		JSONArray jsonArray = jsonFactory.createJSONArray();

		if (filterBy.equals(ContactsConstants.FILTER_BY_DEFAULT) &&
			!portletId.equals(ContactsPortletKeys.MEMBERS)) {

			List<BaseModel<?>> baseModels =
				entryLocalService.searchUsersAndContacts(
					themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					keywords, start, end);

			int contactsCount = entryLocalService.searchUsersAndContactsCount(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				keywords);

			jsonObject.put("count", contactsCount);

			for (BaseModel<?> baseModel : baseModels) {
				JSONObject contactJSONObject = null;

				if (baseModel instanceof User) {
					contactJSONObject = _getUserJSONObject(
						portletResponse, themeDisplay, (User)baseModel);
				}
				else {
					contactJSONObject = _getEntryJSONObject(
						portletResponse, themeDisplay, (Entry)baseModel,
						redirect);
				}

				jsonArray.put(contactJSONObject);
			}
		}
		else if (filterBy.equals(ContactsConstants.FILTER_BY_FOLLOWERS) &&
				 !portletId.equals(ContactsPortletKeys.MEMBERS)) {

			List<SocialRelation> socialRelations =
				socialRelationLocalService.getInverseRelations(
					themeDisplay.getUserId(),
					SocialRelationConstants.TYPE_UNI_FOLLOWER, start, end);

			for (SocialRelation socialRelation : socialRelations) {
				jsonArray.put(
					_getUserJSONObject(
						portletResponse, themeDisplay,
						socialRelation.getUserId1()));
			}
		}
		else if (filterBy.equals(
					ContactsConstants.FILTER_BY_TYPE_MY_CONTACTS) &&
				 !portletId.equals(ContactsPortletKeys.MEMBERS)) {

			List<Entry> entries = entryLocalService.search(
				themeDisplay.getUserId(), keywords, start, end);

			int entriesCount = entryLocalService.searchCount(
				themeDisplay.getUserId(), keywords);

			jsonObject.put("count", entriesCount);

			for (Entry entry : entries) {
				JSONObject contactJSONObject = _getEntryJSONObject(
					portletResponse, themeDisplay, entry, redirect);

				jsonArray.put(contactJSONObject);
			}
		}
		else {
			LinkedHashMap<String, Object> params =
				LinkedHashMapBuilder.<String, Object>put(
					"inherit", Boolean.TRUE
				).build();

			Group group = themeDisplay.getScopeGroup();
			Layout layout = themeDisplay.getLayout();

			if (group.isUser() && layout.isPublicLayout()) {
				params.put(
					"socialRelationType",
					new Long[] {
						group.getClassPK(),
						(long)SocialRelationConstants.TYPE_BI_CONNECTION
					});
			}
			else if (filterBy.startsWith(ContactsConstants.FILTER_BY_TYPE)) {
				params.put(
					"socialRelationType",
					new Long[] {
						themeDisplay.getUserId(),
						ContactsUtil.getSocialRelationType(filterBy)
					});
			}

			if (portletId.equals(ContactsPortletKeys.MEMBERS)) {
				params.put("usersGroups", group.getGroupId());
			}
			else if (filterBy.startsWith(ContactsConstants.FILTER_BY_GROUP)) {
				params.put("usersGroups", ContactsUtil.getGroupId(filterBy));
			}

			List<User> users1 = null;

			if (filterBy.equals(ContactsConstants.FILTER_BY_ADMINS)) {
				Role siteAdministratorRole = roleLocalService.getRole(
					group.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

				params.put(
					"userGroupRole",
					new Long[] {
						group.getGroupId(), siteAdministratorRole.getRoleId()
					});

				Set<User> users2 = new HashSet<>();

				users2.addAll(
					userLocalService.search(
						themeDisplay.getCompanyId(), keywords,
						WorkflowConstants.STATUS_APPROVED, params,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						(OrderByComparator)null));

				Role siteOwnerRole = roleLocalService.getRole(
					group.getCompanyId(), RoleConstants.SITE_OWNER);

				params.put(
					"userGroupRole",
					new Long[] {group.getGroupId(), siteOwnerRole.getRoleId()});

				users2.addAll(
					userLocalService.search(
						themeDisplay.getCompanyId(), keywords,
						WorkflowConstants.STATUS_APPROVED, params,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						(OrderByComparator)null));

				users1 = new ArrayList<>(users2);

				ListUtil.sort(users1, UserLastNameComparator.getInstance(true));
			}
			else {
				int usersCount = userLocalService.searchCount(
					themeDisplay.getCompanyId(), keywords,
					WorkflowConstants.STATUS_APPROVED, params);

				jsonObject.put("count", usersCount);

				users1 = userLocalService.search(
					themeDisplay.getCompanyId(), keywords,
					WorkflowConstants.STATUS_APPROVED, params, start, end,
					UserLastNameComparator.getInstance(true));
			}

			for (User user : users1) {
				jsonArray.put(
					_getUserJSONObject(portletResponse, themeDisplay, user));
			}
		}

		jsonObject.put("users", jsonArray);

		return jsonObject;
	}

	private JSONObject _getEntryJSONObject(
			PortletResponse portletResponse, ThemeDisplay themeDisplay,
			Entry entry, String redirect)
		throws Exception {

		entry = entry.toEscapedModel();

		JSONObject jsonObject = ContactsUtil.getEntryJSONObject(entry);

		jsonObject.put(
			"portraitURL",
			themeDisplay.getPathImage() + "/user_male_portrait?img_id=0"
		).put(
			"redirect", redirect
		).put(
			"viewSummaryURL",
			PortletURLBuilder.createRenderURL(
				portal.getLiferayPortletResponse(portletResponse)
			).setMVCPath(
				"/contacts_center/view_resources.jsp"
			).setRedirect(
				redirect
			).setParameter(
				"entryId", entry.getEntryId()
			).setParameter(
				"portalUser", false
			).setWindowState(
				LiferayWindowState.EXCLUSIVE
			).buildString()
		);

		return jsonObject;
	}

	private String _getRelationMessage(ActionRequest actionRequest) {
		int type = ParamUtil.getInteger(actionRequest, "type");

		String actionName = ParamUtil.getString(
			actionRequest, ActionRequest.ACTION_NAME);

		String message = "your-request-completed-successfully";

		if (actionName.equals("addSocialRelation")) {
			if (type == SocialRelationConstants.TYPE_BI_CONNECTION) {
				message = "you-are-now-connected-to-this-user";
			}
			else if (type == SocialRelationConstants.TYPE_UNI_FOLLOWER) {
				message = "you-are-now-following-this-user";
			}
			else if (type == SocialRelationConstants.TYPE_UNI_ENEMY) {
				message = "you-have-blocked-this-user";
			}
		}
		else if (actionName.equals("deleteSocialRelation")) {
			if (type == SocialRelationConstants.TYPE_BI_CONNECTION) {
				message = "you-are-not-connected-to-this-user-anymore";
			}
			else if (type == SocialRelationConstants.TYPE_UNI_FOLLOWER) {
				message = "you-are-not-following-this-user-anymore";
			}
			else if (type == SocialRelationConstants.TYPE_UNI_ENEMY) {
				message = "you-have-unblocked-this-user";
			}
		}
		else if (actionName.equals("requestSocialRelation")) {
			if (type == SocialRelationConstants.TYPE_BI_CONNECTION) {
				message =
					"this-user-has-received-a-connection-request-from-you";
			}
		}

		return message;
	}

	private long[] _getUserIds(ActionRequest actionRequest) {
		long[] userIds;

		long userId = ParamUtil.getLong(actionRequest, "userId");

		if (userId > 0) {
			userIds = new long[] {userId};
		}
		else {
			userIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "userIds"), 0L);
		}

		return userIds;
	}

	private JSONObject _getUserJSONObject(
			PortletResponse portletResponse, ThemeDisplay themeDisplay,
			long userId)
		throws Exception {

		return _getUserJSONObject(
			portletResponse, themeDisplay, userLocalService.getUser(userId));
	}

	private JSONObject _getUserJSONObject(
			PortletResponse portletResponse, ThemeDisplay themeDisplay,
			User user)
		throws Exception {

		user = user.toEscapedModel();

		JSONObject jsonObject = ContactsUtil.getUserJSONObject(
			themeDisplay.getUserId(), user);

		jsonObject.put(
			"portraitURL", user.getPortraitURL(themeDisplay)
		).put(
			"viewSummaryURL",
			PortletURLBuilder.createRenderURL(
				portal.getLiferayPortletResponse(portletResponse)
			).setMVCPath(
				"/contacts_center/view_resources.jsp"
			).setParameter(
				"portalUser", true
			).setParameter(
				"userId", user.getUserId()
			).setWindowState(
				LiferayWindowState.EXCLUSIVE
			).buildString()
		);

		return jsonObject;
	}

	private void _sendNotificationEvent(SocialRequest socialRequest)
		throws Exception {

		if (UserNotificationManagerUtil.isDeliver(
				socialRequest.getReceiverUserId(),
				ContactsPortletKeys.CONTACTS_CENTER, 0,
				SocialRelationConstants.SOCIAL_RELATION_REQUEST,
				UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

			userNotificationEventLocalService.sendUserNotificationEvents(
				socialRequest.getReceiverUserId(),
				ContactsPortletKeys.CONTACTS_CENTER,
				UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				JSONUtil.put(
					"classPK", socialRequest.getRequestId()
				).put(
					"userId", socialRequest.getUserId()
				));
		}
	}

	private void _updateAdditionalEmailAddresses(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		UsersAdminUtil.updateEmailAddresses(
			Contact.class.getName(), user.getContactId(),
			UsersAdminUtil.getEmailAddresses(actionRequest));
	}

	private void _updateAddresses(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		UsersAdminUtil.updateAddresses(
			Contact.class.getName(), user.getContactId(),
			UsersAdminUtil.getAddresses(actionRequest),
			ListTypeConstants.CONTACT_ADDRESS);
	}

	private void _updateArchived(long userId, long userNotificationEventId)
		throws Exception {

		UserNotificationEvent userNotificationEvent =
			userNotificationEventLocalService.fetchUserNotificationEvent(
				userNotificationEventId);

		if (userNotificationEvent == null) {
			return;
		}

		if (userNotificationEvent.getUserId() != userId) {
			throw new PrincipalException();
		}

		userNotificationEvent.setArchived(true);

		userNotificationEventLocalService.updateUserNotificationEvent(
			userNotificationEvent);
	}

	private void _updateAsset(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		userLocalService.updateAsset(
			user.getUserId(), user, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());
	}

	private void _updatePhoneNumbers(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		UsersAdminUtil.updatePhones(
			Contact.class.getName(), user.getContactId(),
			UsersAdminUtil.getPhones(actionRequest));
	}

	private void _updateProfile(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		boolean deleteLogo = ParamUtil.getBoolean(actionRequest, "deleteLogo");

		byte[] portraitBytes = null;

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (!deleteLogo && (fileEntryId > 0)) {
			FileEntry fileEntry = dlAppLocalService.getFileEntry(fileEntryId);

			try (InputStream inputStream = fileEntry.getContentStream()) {
				portraitBytes = file.getBytes(inputStream);
			}
		}

		String comments = BeanParamUtil.getString(
			user, actionRequest, "comments");
		String emailAddress = BeanParamUtil.getString(
			user, actionRequest, "emailAddress");
		String firstName = BeanParamUtil.getString(
			user, actionRequest, "firstName");
		String jobTitle = BeanParamUtil.getString(
			user, actionRequest, "jobTitle");
		String lastName = BeanParamUtil.getString(
			user, actionRequest, "lastName");
		String middleName = BeanParamUtil.getString(
			user, actionRequest, "middleName");
		String screenName = BeanParamUtil.getString(
			user, actionRequest, "screenName");

		Contact contact = user.getContact();

		String facebookSn = BeanParamUtil.getString(
			contact, actionRequest, "facebookSn");
		String jabberSn = BeanParamUtil.getString(
			contact, actionRequest, "jabberSn");
		String skypeSn = BeanParamUtil.getString(
			contact, actionRequest, "skypeSn");
		String smsSn = BeanParamUtil.getString(contact, actionRequest, "smsSn");
		String twitterSn = BeanParamUtil.getString(
			contact, actionRequest, "twitterSn");

		Calendar cal = CalendarFactoryUtil.getCalendar();

		cal.setTime(user.getBirthday());

		int birthdayDay = cal.get(Calendar.DATE);
		int birthdayMonth = cal.get(Calendar.MONTH);
		int birthdayYear = cal.get(Calendar.YEAR);

		List<AnnouncementsDelivery> announcementsDeliveries =
			announcementsDeliveryLocalService.getUserDeliveries(
				user.getUserId());

		userService.updateUser(
			user.getUserId(), user.getPasswordUnencrypted(),
			user.getPasswordUnencrypted(), user.getPasswordUnencrypted(),
			user.isPasswordReset(), user.getReminderQueryQuestion(),
			user.getReminderQueryAnswer(), screenName, emailAddress,
			!deleteLogo, portraitBytes, user.getLanguageId(),
			user.getTimeZoneId(), user.getGreeting(), comments, firstName,
			middleName, lastName, contact.getPrefixListTypeId(),
			contact.getSuffixListTypeId(), user.isMale(), birthdayMonth,
			birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
			twitterSn, jobTitle, user.getGroupIds(), user.getOrganizationIds(),
			user.getRoleIds(), null, user.getUserGroupIds(),
			user.getAddresses(), null, user.getPhones(), user.getWebsites(),
			announcementsDeliveries, new ServiceContext());
	}

	private void _updateWebsites(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		UsersAdminUtil.updateWebsites(
			Contact.class.getName(), user.getContactId(),
			UsersAdminUtil.getWebsites(actionRequest));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContactsCenterPortlet.class);

}