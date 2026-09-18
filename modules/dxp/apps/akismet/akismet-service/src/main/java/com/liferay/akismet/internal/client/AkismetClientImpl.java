/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.akismet.internal.client;

import com.liferay.akismet.client.AkismetClient;
import com.liferay.akismet.internal.client.constants.AkismetConstants;
import com.liferay.akismet.internal.configuration.AkismetServiceConfiguration;
import com.liferay.akismet.model.AkismetEntry;
import com.liferay.akismet.service.AkismetEntryLocalService;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jamie Sammons
 */
@Component(
	configurationPid = "com.liferay.akismet.internal.configuration.AkismetServiceConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = AkismetClient.class
)
public class AkismetClientImpl implements AkismetClient {

	@Override
	public boolean hasRequiredInfo(String userIP, Map<String, String> headers) {
		if (headers == null) {
			return false;
		}

		String userAgent = headers.get(
			StringUtil.toLowerCase(HttpHeaders.USER_AGENT));

		if (Validator.isNull(userAgent) || Validator.isNull(userIP)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isSpam(
			long userId, String content, AkismetEntry akismetEntry)
		throws PortalException {

		String location = StringBundler.concat(
			Http.HTTP_WITH_SLASH,
			_secretResolver.resolve(
				CompanyConstants.SYSTEM,
				_akismetServiceConfiguration.akismetApiKey()),
			StringPool.PERIOD, AkismetConstants.URL_REST,
			AkismetConstants.PATH_CHECK_SPAM);

		User user = _userLocalService.getUser(userId);

		String response = _sendRequest(
			location, user.getCompanyId(), akismetEntry.getUserIP(),
			akismetEntry.getUserAgent(), akismetEntry.getReferrer(),
			akismetEntry.getPermalink(), akismetEntry.getType(),
			user.getFullName(), user.getEmailAddress(), content);

		if (Validator.isNull(response) || response.equals("invalid")) {
			_log.error("There was an issue with Akismet comment validation");

			return false;
		}
		else if (response.equals("true")) {
			if (_log.isDebugEnabled()) {
				_log.debug("Spam detected: " + akismetEntry.getPermalink());
			}

			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Passed: " + akismetEntry.getPermalink());
		}

		return false;
	}

	@Override
	public void submitHam(
			long companyId, String ipAddress, String userAgent, String referrer,
			String permalink, String commentType, String userName,
			String emailAddress, String content)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug("Submitting message as ham: " + permalink);
		}

		String location = StringBundler.concat(
			Http.HTTP_WITH_SLASH,
			_secretResolver.resolve(
				CompanyConstants.SYSTEM,
				_akismetServiceConfiguration.akismetApiKey()),
			StringPool.PERIOD, AkismetConstants.URL_REST,
			AkismetConstants.PATH_SUBMIT_HAM);

		String response = _sendRequest(
			location, companyId, ipAddress, userAgent, referrer, permalink,
			commentType, userName, emailAddress, content);

		if (Validator.isNull(response)) {
			_log.error("There was an issue submitting message as ham");
		}
	}

	@Override
	public void submitHam(MBMessage mbMessage) throws PortalException {
		AkismetEntry akismetEntry = _akismetEntryLocalService.fetchAkismetEntry(
			MBMessage.class.getName(), mbMessage.getMessageId());

		if (akismetEntry == null) {
			return;
		}

		User user = _userLocalService.getUser(mbMessage.getUserId());

		String content = mbMessage.getSubject() + "\n\n" + mbMessage.getBody();

		submitHam(
			mbMessage.getCompanyId(), akismetEntry.getUserIP(),
			akismetEntry.getUserAgent(), akismetEntry.getReferrer(),
			akismetEntry.getPermalink(), akismetEntry.getType(),
			user.getFullName(), user.getEmailAddress(), content);
	}

	@Override
	public void submitSpam(
			long companyId, String ipAddress, String userAgent, String referrer,
			String permalink, String commentType, String userName,
			String emailAddress, String content)
		throws PortalException {

		if (!_akismetServiceConfiguration.messageBoardsEnabled()) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Submitting message as spam: " + permalink);
		}

		String location = StringBundler.concat(
			Http.HTTP_WITH_SLASH,
			_secretResolver.resolve(
				CompanyConstants.SYSTEM,
				_akismetServiceConfiguration.akismetApiKey()),
			StringPool.PERIOD, AkismetConstants.URL_REST,
			AkismetConstants.PATH_SUBMIT_SPAM);

		String response = _sendRequest(
			location, companyId, ipAddress, userAgent, referrer, permalink,
			commentType, userName, emailAddress, content);

		if (Validator.isNull(response) ||
			!verifyApiKey(
				companyId,
				_secretResolver.resolve(
					CompanyConstants.SYSTEM,
					_akismetServiceConfiguration.akismetApiKey()))) {

			_log.error("There was an issue submitting message as spam");
		}
	}

	@Override
	public void submitSpam(MBMessage mbMessage) throws PortalException {
		AkismetEntry akismetData = _akismetEntryLocalService.fetchAkismetEntry(
			MBMessage.class.getName(), mbMessage.getMessageId());

		if (akismetData == null) {
			return;
		}

		User user = _userLocalService.getUser(mbMessage.getUserId());

		String content = mbMessage.getSubject() + "\n\n" + mbMessage.getBody();

		submitSpam(
			mbMessage.getCompanyId(), akismetData.getUserIP(),
			akismetData.getUserAgent(), akismetData.getReferrer(),
			akismetData.getPermalink(), akismetData.getType(),
			user.getFullName(), user.getEmailAddress(), content);
	}

	@Override
	public boolean verifyApiKey(long companyId, String apiKey)
		throws PortalException {

		String location =
			Http.HTTP_WITH_SLASH + AkismetConstants.URL_REST +
				AkismetConstants.PATH_VERIFY;

		String response = _sendRequest(
			location,
			HashMapBuilder.put(
				"blog", _getPortalURL(companyId)
			).put(
				"key", apiKey
			).build());

		return response.equals("valid");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_akismetServiceConfiguration = ConfigurableUtil.createConfigurable(
			AkismetServiceConfiguration.class, properties);
	}

	private String _getPortalURL(long companyId) throws PortalException {
		Company company = _companyLocalService.getCompany(companyId);

		return _portal.getPortalURL(
			company.getVirtualHostname(), _portal.getPortalServerPort(false),
			false);
	}

	private String _sendRequest(
			String location, long companyId, String ipAddress, String userAgent,
			String referrer, String permalink, String commentType,
			String userName, String emailAddress, String content)
		throws PortalException {

		return _sendRequest(
			location,
			HashMapBuilder.put(
				"blog", _getPortalURL(companyId)
			).put(
				"comment_author", userName
			).put(
				"comment_author_email", emailAddress
			).put(
				"comment_content", content
			).put(
				"comment_type", commentType
			).put(
				"permalink", permalink
			).put(
				"referrer", referrer
			).put(
				"user_agent", userAgent
			).put(
				"user_ip", ipAddress
			).build());
	}

	private String _sendRequest(String location, Map<String, String> parts) {
		Http.Options options = new Http.Options();

		options.addHeader(HttpHeaders.USER_AGENT, "Akismet/2.5.3");
		options.setLocation(location);
		options.setParts(parts);
		options.setPost(true);

		try {
			return _http.URLtoString(options);
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AkismetClientImpl.class);

	@Reference
	private AkismetEntryLocalService _akismetEntryLocalService;

	private volatile AkismetServiceConfiguration _akismetServiceConfiguration;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

	@Reference
	private SecretResolver _secretResolver;

	@Reference
	private UserLocalService _userLocalService;

}