/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.model.impl;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

/**
 * The extended model implementation for the OAuth2Authorization service.
 * Represents a row in the &quot;OAuth2Authorization&quot; database table, with
 * each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class.
 * Whenever methods are added, rerun ServiceBuilder to copy their definitions
 * into the {@link com.liferay.oauth2.provider.model.OAuth2Authorization}
 * interface.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
public class OAuth2AuthorizationImpl extends OAuth2AuthorizationBaseImpl {

	public List<String> getAudiencesList() {
		return StringUtil.split(getAudiences(), CharPool.NEW_LINE);
	}

	@Override
	public void setAccessTokenContent(String accessTokenContent) {
		super.setAccessTokenContent(accessTokenContent);

		if (accessTokenContent != null) {
			setAccessTokenContentHash(accessTokenContent.hashCode());
		}
		else {
			setAccessTokenContentHash(0);
		}
	}

	public void setAudiencesList(List<String> audiencesList) {
		setAudiences(StringUtil.merge(audiencesList, StringPool.NEW_LINE));
	}

	@Override
	public void setRefreshTokenContent(String refreshTokenContent) {
		super.setRefreshTokenContent(refreshTokenContent);

		if (refreshTokenContent != null) {
			setRefreshTokenContentHash(refreshTokenContent.hashCode());
		}
		else {
			setRefreshTokenContentHash(0);
		}
	}

}