/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.app;

import com.liferay.connected.app.ConnectedApp;
import com.liferay.connected.app.ConnectedAppProvider;
import com.liferay.document.library.opener.onedrive.web.internal.DLOpenerOneDriveManager;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.AccessToken;
import com.liferay.document.library.opener.onedrive.web.internal.oauth.OAuth2Manager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ConnectedAppProvider.class)
public class OneDriveConnectedAppProvider implements ConnectedAppProvider {

	@Override
	public ConnectedApp getConnectedApp(User user) throws PortalException {
		if (!_dlOpenerOneDriveManager.isConfigured(user.getCompanyId())) {
			return null;
		}

		AccessToken accessToken = _oAuth2Manager.getAccessToken(
			user.getCompanyId(), user.getUserId());

		if (accessToken == null) {
			return null;
		}

		return new ConnectedApp() {

			@Override
			public String getImageURL() {
				return _servletContext.getContextPath() +
					"/images/onedrive.png";
			}

			@Override
			public String getKey() {
				return "onedrive";
			}

			@Override
			public String getName(Locale locale) {
				ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
					locale, getClass());

				StringBundler sb = new StringBundler(5);

				sb.append(_language.get(resourceBundle, "onedrive"));

				String emailAddress = _getOneDriveUserEmailAddress(accessToken);

				if (Validator.isNotNull(emailAddress)) {
					sb.append(StringPool.SPACE);
					sb.append(StringPool.OPEN_PARENTHESIS);
					sb.append(emailAddress);
					sb.append(StringPool.CLOSE_PARENTHESIS);
				}

				return sb.toString();
			}

			@Override
			public void revoke() {
				_oAuth2Manager.revokeOAuth2AccessToken(
					user.getCompanyId(), user.getUserId());
			}

		};
	}

	private String _getOneDriveUserEmailAddress(AccessToken accessToken) {
		com.microsoft.graph.models.extensions.User user =
			_dlOpenerOneDriveManager.getUser(accessToken);

		return user.mail;
	}

	@Reference
	private DLOpenerOneDriveManager _dlOpenerOneDriveManager;

	@Reference
	private Language _language;

	@Reference
	private OAuth2Manager _oAuth2Manager;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.opener.onedrive.web)"
	)
	private ServletContext _servletContext;

}