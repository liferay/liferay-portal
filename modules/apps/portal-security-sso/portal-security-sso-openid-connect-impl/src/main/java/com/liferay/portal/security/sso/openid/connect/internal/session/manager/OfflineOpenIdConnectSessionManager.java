/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.session.manager;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.configuration.OpenIdConnectConfiguration;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.internal.AuthorizationServerMetadataResolver;
import com.liferay.portal.security.sso.openid.connect.internal.constants.OpenIdConnectDestinationNames;
import com.liferay.portal.security.sso.openid.connect.internal.util.OpenIdConnectTokenRequestUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;

import jakarta.servlet.http.HttpSession;

import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	configurationPid = "com.liferay.portal.security.sso.openid.connect.configuration.OpenIdConnectConfiguration",
	service = OfflineOpenIdConnectSessionManager.class
)
public class OfflineOpenIdConnectSessionManager {

	public boolean isOpenIdConnectSession(HttpSession httpSession) {
		if (httpSession == null) {
			return false;
		}

		Long openIdConnectSessionId = (Long)httpSession.getAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION_ID);

		if (openIdConnectSessionId != null) {
			return true;
		}

		return false;
	}

	public boolean isOpenIdConnectSessionExpired(HttpSession httpSession) {
		Long openIdConnectSessionId = (Long)httpSession.getAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION_ID);

		if (openIdConnectSessionId == null) {
			return true;
		}

		OpenIdConnectSession openIdConnectSession =
			_openIdConnectSessionLocalService.fetchOpenIdConnectSession(
				openIdConnectSessionId);

		if (openIdConnectSession == null) {
			return true;
		}

		Date accessTokenExpirationDate =
			openIdConnectSession.getAccessTokenExpirationDate();

		long currentTime = System.currentTimeMillis();

		if (currentTime >
				(accessTokenExpirationDate.getTime() -
					_tokenRefreshOffsetMillis)) {

			Message message = new Message();

			message.put("openIdConnectSessionId", openIdConnectSessionId);

			if (!_clusterMasterExecutor.isEnabled() ||
				_clusterMasterExecutor.isMaster()) {

				_messageBus.sendMessage(
					OpenIdConnectDestinationNames.OPENID_CONNECT_TOKEN_REFRESH,
					message);
			}
			else {
				_executeOnMaster(message);
			}
		}

		return false;
	}

	public long startOpenIdConnectSession(
		String authServerWellKnownURI, String clientId, OIDCTokens oidcTokens,
		long userId) {

		OpenIdConnectSession openIdConnectSession =
			_openIdConnectSessionLocalService.fetchOpenIdConnectSession(
				userId, authServerWellKnownURI, clientId);

		if (openIdConnectSession == null) {
			openIdConnectSession =
				_openIdConnectSessionLocalService.createOpenIdConnectSession(
					_counterLocalService.increment(
						OpenIdConnectSession.class.getName()));
		}

		_updateOpenIdConnectSession(
			oidcTokens.getAccessToken(), authServerWellKnownURI, clientId,
			oidcTokens.getIDTokenString(), oidcTokens.getRefreshToken(),
			openIdConnectSession, userId);

		return openIdConnectSession.getOpenIdConnectSessionId();
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_openIdConnectConfiguration = ConfigurableUtil.createConfigurable(
			OpenIdConnectConfiguration.class, properties);

		if (_openIdConnectConfiguration.tokenRefreshOffset() < 30) {
			throw new IllegalArgumentException(
				"Token refresh offset needs to be at least 30 seconds");
		}

		_tokenRefreshOffsetMillis =
			_openIdConnectConfiguration.tokenRefreshOffset() * Time.SECOND;

		if (!_openIdConnectConfiguration.enabled()) {
			return;
		}

		_registerServices(bundleContext);

		int tokenRefreshScheduledInterval =
			_openIdConnectConfiguration.tokenRefreshScheduledInterval();

		if (tokenRefreshScheduledInterval >= 30) {
			_schedulerJobConfigurationServiceRegistration =
				bundleContext.registerService(
					SchedulerJobConfiguration.class,
					new TokensRefreshSchedulerJobConfiguration(), null);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_schedulerJobConfigurationServiceRegistration != null) {
			_schedulerJobConfigurationServiceRegistration.unregister();
		}

		_unregisterServices();
	}

	private void _executeOnMaster(Message message) {
		try {
			_clusterMasterExecutor.executeOnMaster(
				new MethodHandler(
					MessageBusUtil.class.getDeclaredMethod(
						"sendMessage", String.class, Message.class),
					OpenIdConnectDestinationNames.OPENID_CONNECT_TOKEN_REFRESH,
					message));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private AccessToken _extendOpenIdConnectSession(
		OpenIdConnectSession openIdConnectSession) {

		if (Validator.isNull(openIdConnectSession.getRefreshToken())) {
			_openIdConnectSessionLocalService.deleteOpenIdConnectSession(
				openIdConnectSession);

			return null;
		}

		RefreshToken refreshToken = new RefreshToken(
			openIdConnectSession.getRefreshToken());

		OAuthClientEntry oAuthClientEntry =
			_oAuthClientEntryLocalService.fetchOAuthClientEntry(
				openIdConnectSession.getCompanyId(),
				openIdConnectSession.getAuthServerWellKnownURI(),
				openIdConnectSession.getClientId());

		if (oAuthClientEntry == null) {
			_openIdConnectSessionLocalService.deleteOpenIdConnectSession(
				openIdConnectSession);

			return null;
		}

		try {
			OIDCTokens oidcTokens = OpenIdConnectTokenRequestUtil.request(
				OIDCClientInformation.parse(
					JSONObjectUtils.parse(oAuthClientEntry.getInfoJSON())),
				_authorizationServerMetadataResolver.
					resolveOIDCProviderMetadata(
						openIdConnectSession.getAuthServerWellKnownURI(),
						oAuthClientEntry.getMetadataCacheInSeconds(),
						oAuthClientEntry.getOAuthClientEntryId()),
				refreshToken, oAuthClientEntry.getTokenRequestParametersJSON());

			_updateOpenIdConnectSession(
				oidcTokens.getAccessToken(), openIdConnectSession,
				oidcTokens.getRefreshToken());

			return oidcTokens.getAccessToken();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			_openIdConnectSessionLocalService.deleteOpenIdConnectSession(
				openIdConnectSession);
		}

		return null;
	}

	private void _registerServices(BundleContext bundleContext) {
		if (_messageListenerServiceRegistration != null) {
			return;
		}

		DestinationConfiguration destinationConfiguration =
			DestinationConfiguration.createSerialDestinationConfiguration(
				OpenIdConnectDestinationNames.OPENID_CONNECT_TOKEN_REFRESH);

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", destination.getName()
			).build();

		_destinationServiceRegistration = bundleContext.registerService(
			Destination.class, destination, dictionary);

		_messageListenerServiceRegistration = bundleContext.registerService(
			MessageListener.class, new TokenRefreshMessageListener(),
			dictionary);
	}

	private void _unregisterServices() {
		if (_messageListenerServiceRegistration != null) {
			_messageListenerServiceRegistration.unregister();

			_messageListenerServiceRegistration = null;
		}

		if (_destinationServiceRegistration != null) {
			_destinationServiceRegistration.unregister();

			_destinationServiceRegistration = null;
		}
	}

	private void _updateOpenIdConnectSession(
		AccessToken accessToken, OpenIdConnectSession openIdConnectSession,
		RefreshToken refreshToken) {

		openIdConnectSession.setAccessToken(accessToken.toJSONString());

		if (refreshToken != null) {
			openIdConnectSession.setRefreshToken(refreshToken.toString());
		}

		long currentTime = System.currentTimeMillis();

		openIdConnectSession.setModifiedDate(new Date(currentTime));

		if (accessToken.getLifetime() > 0) {
			openIdConnectSession.setAccessTokenExpirationDate(
				new Date(
					currentTime + (accessToken.getLifetime() * Time.SECOND)));
		}
		else {
			openIdConnectSession.setAccessTokenExpirationDate(
				new Date(currentTime + Time.HOUR));
		}

		_openIdConnectSessionLocalService.updateOpenIdConnectSession(
			openIdConnectSession);
	}

	private void _updateOpenIdConnectSession(
		AccessToken accessToken, String authServerWellKnownURI, String clientId,
		String idTokenString, RefreshToken refreshToken,
		OpenIdConnectSession openIdConnectSession, long userId) {

		openIdConnectSession.setUserId(userId);
		openIdConnectSession.setAuthServerWellKnownURI(authServerWellKnownURI);
		openIdConnectSession.setClientId(clientId);
		openIdConnectSession.setIdToken(idTokenString);

		_updateOpenIdConnectSession(
			accessToken, openIdConnectSession, refreshToken);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OfflineOpenIdConnectSessionManager.class);

	@Reference
	private AuthorizationServerMetadataResolver
		_authorizationServerMetadataResolver;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private DestinationFactory _destinationFactory;

	private ServiceRegistration<Destination> _destinationServiceRegistration;

	@Reference
	private MessageBus _messageBus;

	private ServiceRegistration<MessageListener>
		_messageListenerServiceRegistration;

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	private OpenIdConnectConfiguration _openIdConnectConfiguration;

	@Reference
	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

	private ServiceRegistration<SchedulerJobConfiguration>
		_schedulerJobConfigurationServiceRegistration;
	private long _tokenRefreshOffsetMillis = 60 * Time.SECOND;

	private class TokenRefreshMessageListener extends BaseMessageListener {

		@Override
		protected void doReceive(Message message) throws Exception {
			Long openIdConnectSessionId = message.getLong(
				"openIdConnectSessionId");

			if ((openIdConnectSessionId == null) ||
				(openIdConnectSessionId < 1)) {

				return;
			}

			OpenIdConnectSession openIdConnectSession =
				_openIdConnectSessionLocalService.fetchOpenIdConnectSession(
					openIdConnectSessionId);

			if (openIdConnectSession == null) {
				return;
			}

			Date modifiedDate = openIdConnectSession.getModifiedDate();

			if (System.currentTimeMillis() <=
					(modifiedDate.getTime() + _tokenRefreshOffsetMillis)) {

				return;
			}

			_extendOpenIdConnectSession(openIdConnectSession);
		}

	}

	private class TokensRefreshSchedulerJobConfiguration
		implements SchedulerJobConfiguration {

		@Override
		public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
			return () -> {
				List<OpenIdConnectSession> openIdConnectSessions =
					_openIdConnectSessionLocalService.
						getAccessTokenExpirationDateOpenIdConnectSessions(
							new Date(
								System.currentTimeMillis() +
									_tokenRefreshOffsetMillis),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS);

				for (OpenIdConnectSession openIdConnectSession :
						openIdConnectSessions) {

					_extendOpenIdConnectSession(openIdConnectSession);
				}
			};
		}

		@Override
		public TriggerConfiguration getTriggerConfiguration() {
			return TriggerConfiguration.createTriggerConfiguration(
				_openIdConnectConfiguration.tokenRefreshScheduledInterval(),
				TimeUnit.SECOND);
		}

	}

}