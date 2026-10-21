/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.instance.lifecycle;

import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.constants.OAuth2ApplicationConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class DynamicRegistrationPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		_deleteClientCredentialsGrants(company.getCompanyId());

		DynamicQuery dynamicQuery =
			_oAuth2ApplicationLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(company.getCompanyId()));

		Property nameProperty = PropertyFactoryUtil.forName("name");

		dynamicQuery.add(
			nameProperty.eq(
				OAuth2ApplicationConstants.NAME_DYNAMIC_REGISTRATOR));

		List<OAuth2Application> oAuth2Applications =
			_oAuth2ApplicationLocalService.dynamicQuery(dynamicQuery);

		if (!oAuth2Applications.isEmpty()) {
			_deleteScopeAliases(oAuth2Applications.get(0));

			return;
		}

		User user = _userLocalService.getUserByScreenName(
			company.getCompanyId(), "default-service-account");

		_oAuth2ApplicationLocalService.addOAuth2Application(
			company.getCompanyId(), user.getUserId(), user.getScreenName(),
			Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
			"client_secret_post", user.getUserId(),
			OAuth2SecureRandomGenerator.generateClientId(),
			ClientProfile.HEADLESS_SERVER.id(),
			OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
			null, 0, null, OAuth2ApplicationConstants.NAME_DYNAMIC_REGISTRATOR,
			null, Collections.emptyList(), false, false, null,
			new ServiceContext());
	}

	private void _deleteClientCredentialsGrants(long companyId)
		throws Exception {

		DynamicQuery dynamicQuery =
			_oAuth2ApplicationLocalService.dynamicQuery();

		Property companyIdProperty = PropertyFactoryUtil.forName("companyId");

		dynamicQuery.add(companyIdProperty.eq(companyId));

		Property userNameProperty = PropertyFactoryUtil.forName("userName");

		dynamicQuery.add(userNameProperty.like("%_dynamic_registered"));

		List<OAuth2Application> oAuth2Applications =
			_oAuth2ApplicationLocalService.dynamicQuery(dynamicQuery);

		for (OAuth2Application oAuth2Application : oAuth2Applications) {
			String userName = oAuth2Application.getUserName();

			if (!userName.endsWith("_dynamic_registered")) {
				continue;
			}

			List<GrantType> allowedGrantTypesList =
				oAuth2Application.getAllowedGrantTypesList();

			if (!allowedGrantTypesList.contains(GrantType.CLIENT_CREDENTIALS)) {
				continue;
			}

			_oAuth2ApplicationLocalService.deleteClientCredentialsGrant(
				oAuth2Application.getOAuth2ApplicationId());

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"The client credentials grant was deleted from the ",
						"dynamically registered OAuth 2 application with ",
						"client ID ", oAuth2Application.getClientId()));
			}
		}
	}

	private void _deleteScopeAliases(OAuth2Application oAuth2Application)
		throws Exception {

		if (oAuth2Application.getOAuth2ApplicationScopeAliasesId() <= 0) {
			return;
		}

		_oAuth2ApplicationLocalService.deleteScopeAliases(
			oAuth2Application.getOAuth2ApplicationId());

		if (_log.isWarnEnabled()) {
			_log.warn(
				"The scopes were deleted from the OAuth 2 application with " +
					"client ID " + oAuth2Application.getClientId());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicRegistrationPortalInstanceLifecycleListener.class);

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private UserLocalService _userLocalService;

}