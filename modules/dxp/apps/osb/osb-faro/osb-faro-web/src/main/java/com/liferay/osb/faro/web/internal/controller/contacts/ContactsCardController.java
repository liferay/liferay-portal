/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.controller.contacts;

import com.fasterxml.jackson.core.type.TypeReference;

import com.liferay.osb.faro.contacts.model.ContactsCardTemplate;
import com.liferay.osb.faro.contacts.model.constants.JSONConstants;
import com.liferay.osb.faro.contacts.service.ContactsCardTemplateLocalService;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.card.template.ContactsCardTemplateManagerHelper;
import com.liferay.osb.faro.web.internal.controller.BaseFaroController;
import com.liferay.osb.faro.web.internal.controller.FaroController;
import com.liferay.osb.faro.web.internal.model.display.contacts.ContactsCardDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.card.template.ContactsCardTemplateDisplay;
import com.liferay.osb.faro.web.internal.model.display.main.FaroEntityDisplay;
import com.liferay.osb.faro.web.internal.param.FaroParam;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.RoleConstants;

import jakarta.annotation.security.RolesAllowed;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = {ContactsCardController.class, FaroController.class})
@Path("/{groupId}/contacts_card")
@Produces(MediaType.APPLICATION_JSON)
public class ContactsCardController extends BaseFaroController {

	@GET
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public ContactsCardDisplay getContactsCardDisplay(
			@PathParam("groupId") long groupId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@QueryParam("contactsCardTemplateId") long contactsCardTemplateId,
			@DefaultValue(StringPool.BLANK)
			@QueryParam("contactsCardTemplateSettings")
			FaroParam
				<Map<String, Object>> contactsCardTemplateSettingsFaroParam,
			@QueryParam("size") int size)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		FaroEntityDisplay faroEntityDisplay =
			contactsHelper.getContactsEntityDisplay(
				faroProject, contactsEntityId, contactsEntityType);

		ContactsCardTemplate contactsCardTemplate =
			_contactsCardTemplateLocalService.getContactsCardTemplate(
				contactsCardTemplateId);

		setContactsCardTemplateSettings(
			contactsCardTemplate,
			contactsCardTemplateSettingsFaroParam.getValue());

		ContactsCardTemplateDisplay contactsCardTemplateDisplay =
			_contactsCardTemplateManagerUtil.getContactsCardTemplateDisplay(
				contactsCardTemplate, size);

		faroEntityDisplay.addProperties(
			contactsCardTemplateDisplay.getFieldMappingNames());

		return new ContactsCardDisplay(
			faroEntityDisplay, contactsCardTemplateDisplay,
			contactsCardTemplateDisplay.getContactsCardData(
				faroProject, faroEntityDisplay, contactsEngineClient));
	}

	@GET
	@Path("/preview")
	@RolesAllowed(RoleConstants.SITE_MEMBER)
	public ContactsCardDisplay getContactsCardDisplay(
			@PathParam("groupId") long groupId,
			@QueryParam("contactsEntityId") String contactsEntityId,
			@QueryParam("contactsEntityType") int contactsEntityType,
			@QueryParam("contactsCardTemplateName") String
				contactsCardTemplateName,
			@DefaultValue(JSONConstants.NULL_JSON_OBJECT)
			@QueryParam("contactsCardTemplateSettings")
			FaroParam
				<Map<String, Object>> contactsCardTemplateSettingsFaroParam,
			@QueryParam("contactsCardTemplateType") int
				contactsCardTemplateType,
			@QueryParam("size") int size)
		throws Exception {

		FaroProject faroProject =
			faroProjectLocalService.getFaroProjectByGroupId(groupId);

		FaroEntityDisplay faroEntityDisplay =
			contactsHelper.getContactsEntityDisplay(
				faroProject, contactsEntityId, contactsEntityType);

		ContactsCardTemplateDisplay contactsCardTemplateDisplay =
			_contactsCardTemplateManagerUtil.getContactsCardTemplateDisplay(
				groupId, contactsCardTemplateSettingsFaroParam.getValue(),
				contactsCardTemplateType, size);

		contactsCardTemplateDisplay.setName(contactsCardTemplateName);

		faroEntityDisplay.addProperties(
			contactsCardTemplateDisplay.getFieldMappingNames());

		return new ContactsCardDisplay(
			faroEntityDisplay, contactsCardTemplateDisplay,
			contactsCardTemplateDisplay.getContactsCardData(
				faroProject, faroEntityDisplay, contactsEngineClient));
	}

	protected void setContactsCardTemplateSettings(
			ContactsCardTemplate contactsCardTemplate,
			Map<String, Object> contactsCardTemplateSettings)
		throws Exception {

		if ((contactsCardTemplateSettings == null) ||
			contactsCardTemplateSettings.isEmpty()) {

			return;
		}

		Map<String, Object> settings = JSONUtil.readValue(
			contactsCardTemplate.getSettings(),
			new TypeReference<Map<String, Object>>() {
			});

		settings.putAll(contactsCardTemplateSettings);

		contactsCardTemplate.setSettings(JSONUtil.writeValueAsString(settings));
	}

	@Reference
	private ContactsCardTemplateLocalService _contactsCardTemplateLocalService;

	@Reference
	private ContactsCardTemplateManagerHelper _contactsCardTemplateManagerUtil;

}