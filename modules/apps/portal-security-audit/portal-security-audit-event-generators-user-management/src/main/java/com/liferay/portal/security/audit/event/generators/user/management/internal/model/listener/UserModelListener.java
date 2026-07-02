/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	public void onBeforeCreate(User user) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.ADD, user);
	}

	public void onBeforeRemove(User user) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.DELETE, user);
	}

	public void onBeforeUpdate(User originalUser, User user)
		throws ModelListenerException {

		try {
			List<Attribute> attributes = getModifiedAttributes(
				originalUser, user);

			if (attributes.removeIf(
					attribute -> Objects.equals(
						attribute.getName(), "agreedToTermsOfUse"))) {

				_auditOnAgreedToTermsOfUse(user);
			}

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						User.class.getName(), user.getUserId(),
						EventTypes.UPDATE, attributes);

				auditMessage.setCompanyId(user.getCompanyId());

				_auditRouter.route(auditMessage);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected void auditOnCreateOrRemove(String eventType, User user)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				User.class.getName(), user.getUserId(), eventType, null);

			auditMessage.setCompanyId(user.getCompanyId());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"emailAddress", user.getEmailAddress()
			).put(
				"screenName", user.getScreenName()
			).put(
				"userId", user.getUserId()
			).put(
				"userName", user.getFullName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected List<Attribute> getModifiedAttributes(
		User originalUser, User user) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			user, originalUser);

		attributesBuilder.add("active");
		attributesBuilder.add("agreedToTermsOfUse");
		attributesBuilder.add("comments");
		attributesBuilder.add("emailAddress");
		attributesBuilder.add("languageId");
		attributesBuilder.add("reminderQueryAnswer");
		attributesBuilder.add("reminderQueryQuestion");
		attributesBuilder.add("screenName");
		attributesBuilder.add("timeZoneId");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		if (attributes.removeIf(
				attribute -> Objects.equals(
					attribute.getName(), "reminderQueryAnswer"))) {

			attributes.add(new Attribute("reminderQueryAnswer"));
		}

		if (user.isPasswordModified()) {
			attributes.add(new Attribute("password"));
		}

		return attributes;
	}

	private void _auditOnAgreedToTermsOfUse(User user)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				User.class.getName(), user.getUserId(),
				EventTypes.AGREED_TO_TERMS_OF_USE, null);

			auditMessage.setCompanyId(user.getCompanyId());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			try {
				JournalServiceConfiguration journalServiceConfiguration =
					_configurationProvider.getCompanyConfiguration(
						JournalServiceConfiguration.class, user.getCompanyId());

				Settings settings = FallbackKeysSettingsUtil.getSettings(
					new CompanyServiceSettingsLocator(
						user.getCompanyId(),
						JournalServiceConfiguration.class.getName()));

				additionalInfoJSONObject.put(
					"termsOfUseJournalArticleGroupId",
					_getTermsOfUseJournalArticleGroupId(
						journalServiceConfiguration, settings)
				).put(
					"termsOfUseJournalArticleId",
					_getTermsOfUseJournalArticleId(
						journalServiceConfiguration, settings)
				);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to get journal service configuration", exception);
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private long _getTermsOfUseJournalArticleGroupId(
		JournalServiceConfiguration journalServiceConfiguration,
		Settings settings) {

		return GetterUtil.getLong(
			settings.getValue(
				"terms.of.use.journal.article.group.id",
				String.valueOf(
					journalServiceConfiguration.
						termsOfUseJournalArticleGroupId())));
	}

	private String _getTermsOfUseJournalArticleId(
		JournalServiceConfiguration journalServiceConfiguration,
		Settings settings) {

		return settings.getValue(
			"terms.of.use.journal.article.id",
			String.valueOf(
				journalServiceConfiguration.termsOfUseJournalArticleId()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ConfigurationProvider _configurationProvider;

}