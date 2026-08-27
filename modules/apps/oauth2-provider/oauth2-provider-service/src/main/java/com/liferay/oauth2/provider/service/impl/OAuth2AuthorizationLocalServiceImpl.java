/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.impl;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.exception.NoSuchOAuth2AuthorizationException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.service.base.OAuth2AuthorizationLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Time;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	property = "model.class.name=com.liferay.oauth2.provider.model.OAuth2Authorization",
	service = AopService.class
)
public class OAuth2AuthorizationLocalServiceImpl
	extends OAuth2AuthorizationLocalServiceBaseImpl {

	@Override
	public OAuth2Authorization addOAuth2Authorization(
		long companyId, long userId, String userName, long oAuth2ApplicationId,
		long oAuth2ApplicationScopeAliasesId, String accessTokenContent,
		Date accessTokenCreateDate, Date accessTokenExpirationDate,
		List<String> audiencesList, String remoteHostInfo, String remoteIPInfo,
		String refreshTokenContent, Date refreshTokenCreateDate,
		Date refreshTokenExpirationDate) {

		long oAuth2AuthorizationId = counterLocalService.increment(
			OAuth2Authorization.class.getName());

		OAuth2Authorization oAuth2Authorization = createOAuth2Authorization(
			oAuth2AuthorizationId);

		oAuth2Authorization.setCompanyId(companyId);
		oAuth2Authorization.setUserId(userId);
		oAuth2Authorization.setUserName(userName);
		oAuth2Authorization.setCreateDate(new Date());
		oAuth2Authorization.setOAuth2ApplicationId(oAuth2ApplicationId);
		oAuth2Authorization.setOAuth2ApplicationScopeAliasesId(
			oAuth2ApplicationScopeAliasesId);
		oAuth2Authorization.setAccessTokenContent(accessTokenContent);
		oAuth2Authorization.setAccessTokenCreateDate(accessTokenCreateDate);
		oAuth2Authorization.setAccessTokenExpirationDate(
			accessTokenExpirationDate);
		oAuth2Authorization.setAudiencesList(audiencesList);
		oAuth2Authorization.setRemoteHostInfo(remoteHostInfo);
		oAuth2Authorization.setRemoteIPInfo(remoteIPInfo);
		oAuth2Authorization.setRefreshTokenContent(refreshTokenContent);
		oAuth2Authorization.setRefreshTokenCreateDate(refreshTokenCreateDate);
		oAuth2Authorization.setRefreshTokenExpirationDate(
			refreshTokenExpirationDate);

		return oAuth2AuthorizationPersistence.update(oAuth2Authorization);
	}

	@Override
	public void deleteExpiredOAuth2Authorizations() throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			oAuth2AuthorizationLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Date date = new Date(
					System.currentTimeMillis() -
						_expiredAuthorizationsAfterlifeDurationMillis);

				dynamicQuery.add(
					RestrictionsFactoryUtil.and(
						RestrictionsFactoryUtil.lt(
							"accessTokenExpirationDate", date),
						RestrictionsFactoryUtil.or(
							RestrictionsFactoryUtil.and(
								RestrictionsFactoryUtil.isNotNull(
									"refreshTokenExpirationDate"),
								RestrictionsFactoryUtil.lt(
									"refreshTokenExpirationDate", date)),
							RestrictionsFactoryUtil.isNull(
								"refreshTokenExpirationDate"))));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(OAuth2Authorization oAuth2Authorization) ->
				oAuth2AuthorizationLocalService.deleteOAuth2Authorization(
					oAuth2Authorization));

		actionableDynamicQuery.performActions();
	}

	@Override
	public OAuth2Authorization deleteOAuth2Authorization(
			long oAuth2AuthorizationId)
		throws PortalException {

		return oAuth2AuthorizationPersistence.remove(oAuth2AuthorizationId);
	}

	@Override
	public OAuth2Authorization fetchOAuth2AuthorizationByAccessTokenContent(
		String accessTokenContent) {

		List<OAuth2Authorization> oAuth2Authorizations =
			oAuth2AuthorizationPersistence.findByC_ATCH(
				CompanyThreadLocal.getCompanyId(),
				accessTokenContent.hashCode());

		for (OAuth2Authorization oAuth2Authorization : oAuth2Authorizations) {
			String currentAccessTokenContent =
				oAuth2Authorization.getAccessTokenContent();

			if (MessageDigest.isEqual(
					accessTokenContent.getBytes(StandardCharsets.UTF_8),
					currentAccessTokenContent.getBytes(
						StandardCharsets.UTF_8))) {

				return oAuth2Authorization;
			}
		}

		return null;
	}

	@Override
	public OAuth2Authorization fetchOAuth2AuthorizationByRefreshTokenContent(
		String refreshTokenContent) {

		List<OAuth2Authorization> oAuth2Authorizations =
			oAuth2AuthorizationPersistence.findByC_RTCH(
				CompanyThreadLocal.getCompanyId(),
				refreshTokenContent.hashCode());

		for (OAuth2Authorization oAuth2Authorization : oAuth2Authorizations) {
			String currentRefreshTokenContent =
				oAuth2Authorization.getRefreshTokenContent();

			if (MessageDigest.isEqual(
					refreshTokenContent.getBytes(StandardCharsets.UTF_8),
					currentRefreshTokenContent.getBytes(
						StandardCharsets.UTF_8))) {

				return oAuth2Authorization;
			}
		}

		return null;
	}

	@Override
	public OAuth2Authorization fetchOAuth2AuthorizationByRememberDeviceContent(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent) {

		return oAuth2AuthorizationPersistence.fetchByU_O_R_First(
			userId, oAuth2ApplicationId, rememberDeviceContent, null);
	}

	@Override
	public OAuth2Authorization getOAuth2AuthorizationByAccessTokenContent(
			String accessTokenContent)
		throws NoSuchOAuth2AuthorizationException {

		OAuth2Authorization oAuth2Authorization =
			fetchOAuth2AuthorizationByAccessTokenContent(accessTokenContent);

		if (oAuth2Authorization == null) {
			throw new NoSuchOAuth2AuthorizationException(
				"No OAuth2 authorization exists with access token content " +
					accessTokenContent);
		}

		return oAuth2Authorization;
	}

	@Override
	public OAuth2Authorization getOAuth2AuthorizationByRefreshTokenContent(
			String refreshTokenContent)
		throws NoSuchOAuth2AuthorizationException {

		OAuth2Authorization oAuth2Authorization =
			fetchOAuth2AuthorizationByRefreshTokenContent(refreshTokenContent);

		if (oAuth2Authorization == null) {
			throw new NoSuchOAuth2AuthorizationException(
				"No OAuth2 authorization exists with refresh token content " +
					refreshTokenContent);
		}

		return oAuth2Authorization;
	}

	@Override
	public List<OAuth2Authorization> getOAuth2Authorizations(
		long oAuth2ApplicationId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return oAuth2AuthorizationPersistence.findByOAuth2ApplicationId(
			oAuth2ApplicationId, start, end, orderByComparator);
	}

	@Override
	public int getOAuth2AuthorizationsCount(long oAuth2ApplicationId) {
		return oAuth2AuthorizationPersistence.countByOAuth2ApplicationId(
			oAuth2ApplicationId);
	}

	@Override
	public Collection<OAuth2ScopeGrant> getOAuth2ScopeGrants(
		long oAuth2AuthorizationId) {

		return oAuth2ScopeGrantPersistence.
			getOAuth2AuthorizationOAuth2ScopeGrants(oAuth2AuthorizationId);
	}

	@Override
	public List<OAuth2Authorization> getUserOAuth2Authorizations(
		long userId, int start, int end,
		OrderByComparator<OAuth2Authorization> orderByComparator) {

		return oAuth2AuthorizationPersistence.findByUserId(
			userId, start, end, orderByComparator);
	}

	@Override
	public int getUserOAuth2AuthorizationsCount(long userId) {
		return oAuth2AuthorizationPersistence.countByUserId(userId);
	}

	@Override
	public OAuth2Authorization updateRememberDeviceContent(
		String refreshTokenContent, String rememberDeviceContent) {

		OAuth2Authorization oAuth2Authorization =
			fetchOAuth2AuthorizationByRefreshTokenContent(refreshTokenContent);

		oAuth2Authorization.setRememberDeviceContent(rememberDeviceContent);

		return oAuth2AuthorizationPersistence.update(oAuth2Authorization);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		OAuth2ProviderConfiguration oAuth2ProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				OAuth2ProviderConfiguration.class, properties);

		int expiredAuthorizationsAfterlifeDuration = Math.max(
			oAuth2ProviderConfiguration.
				expiredAuthorizationsAfterlifeDuration(),
			0);

		_expiredAuthorizationsAfterlifeDurationMillis =
			expiredAuthorizationsAfterlifeDuration * Time.SECOND;
	}

	private volatile long _expiredAuthorizationsAfterlifeDurationMillis;

}