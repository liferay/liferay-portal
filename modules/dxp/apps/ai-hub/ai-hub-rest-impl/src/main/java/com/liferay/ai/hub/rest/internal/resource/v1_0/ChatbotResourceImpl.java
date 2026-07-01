/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.Chatbot;
import com.liferay.ai.hub.rest.resource.v1_0.ChatbotResource;
import com.liferay.object.entry.util.ObjectEntryThreadLocal;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/chatbot.properties",
	scope = ServiceScope.PROTOTYPE, service = ChatbotResource.class
)
public class ChatbotResourceImpl extends BaseChatbotResourceImpl {

	@Override
	public Chatbot getChatbotByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		boolean skipObjectEntryResourcePermission =
			ObjectEntryThreadLocal.isSkipObjectEntryResourcePermission();

		try {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(true);

			ObjectEntry objectEntry = _objectEntryManager.getObjectEntry(
				contextCompany.getCompanyId(), _createDTOConverterContext(),
				externalReferenceCode,
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_AI_HUB_CHATBOT", contextCompany.getCompanyId()),
				null);

			Chatbot chatbot = new Chatbot();

			chatbot.setActive(
				() -> GetterUtil.getBoolean(
					objectEntry.getPropertyValue("active")));
			chatbot.setAvatar(
				() -> {
					Object avatar = objectEntry.getPropertyValue("avatar");

					if (avatar instanceof Map avatarMap) {
						return GetterUtil.getString(avatarMap.get("fileURL"));
					}

					return GetterUtil.getString(avatar);
				});
			chatbot.setDisclaimerMessage(
				() -> GetterUtil.getString(
					objectEntry.getPropertyValue("disclaimerMessage")));
			chatbot.setExternalReferenceCode(
				objectEntry::getExternalReferenceCode);
			chatbot.setIntroMessage(
				() -> GetterUtil.getString(
					objectEntry.getPropertyValue("introMessage")));
			chatbot.setNotificationMessage(
				() -> GetterUtil.getString(
					objectEntry.getPropertyValue("notificationMessage")));
			chatbot.setPlaceholderMessage(
				() -> GetterUtil.getString(
					objectEntry.getPropertyValue("placeholderMessage")));
			chatbot.setTitle(
				() -> GetterUtil.getString(
					objectEntry.getPropertyValue("title")));

			return chatbot;
		}
		finally {
			ObjectEntryThreadLocal.setSkipObjectEntryResourcePermission(
				skipObjectEntryResourcePermission);
		}
	}

	private DTOConverterContext _createDTOConverterContext() {
		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(), null,
			_dtoConverterRegistry, contextHttpServletRequest, null,
			contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
			contextUser);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

}