/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.agent.SupervisorAgent;
import com.liferay.ai.hub.rest.dto.v1_0.Message;
import com.liferay.ai.hub.rest.internal.util.ContextUserUtil;
import com.liferay.ai.hub.rest.internal.util.OAuth2ApplicationIdResolverUtil;
import com.liferay.ai.hub.rest.resource.v1_0.MessageResource;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/message.properties",
	scope = ServiceScope.PROTOTYPE, service = MessageResource.class
)
public class MessageResourceImpl extends BaseMessageResourceImpl {

	@Override
	public Message postChatByExternalReferenceCodeMessage(
			String externalReferenceCode, Message message)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		User user = ContextUserUtil.initContextUser(
			message.getChatbotExternalReferenceCode(),
			contextCompany.getCompanyId(), contextUser);

		_supervisorAgent.invoke(
			AgentContext.builder(
			).chatbotExternalReferenceCode(
				message.getChatbotExternalReferenceCode()
			).companyId(
				contextCompany.getCompanyId()
			).dtoConverterContext(
				new DefaultDTOConverterContext(
					contextAcceptLanguage.isAcceptAllLanguages(), null,
					_dtoConverterRegistry, contextHttpServletRequest, null,
					contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
					user)
			).groupId(
				AccountEntryUtil.getUserAccountEntryGroupId(user.getUserId())
			).input(
				HashMapBuilder.<String, Object>putAll(
					message.getContext()
				).put(
					"message", message.getText()
				).build()
			).instructionDefinitionScope(
				message.getInstructionDefinitionScopeAsString()
			).oAuth2ApplicationId(
				OAuth2ApplicationIdResolverUtil.resolve(
					contextHttpServletRequest)
			).serviceContext(
				ServiceContextFactory.getInstance(contextHttpServletRequest)
			).sseEventSinkKey(
				externalReferenceCode
			).userId(
				user.getUserId()
			).userToken(
				contextHttpServletRequest.getHeader(
					"Liferay-AI-Hub-Cell-On-Behalf-Of")
			).build());

		return message;
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private SupervisorAgent _supervisorAgent;

}