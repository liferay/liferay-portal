/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webserver;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.webserver.WebServerServletToken;

import java.util.Date;

/**
 * @author Brian Wing Shun Chan
 * @since  6.1, replaced com.liferay.portal.servlet.ImageServletTokenImpl
 */
public class WebServerServletTokenImpl implements WebServerServletToken {

	@Override
	public String getToken(long imageId) {
		Image image = ImageLocalServiceUtil.fetchImage(imageId);

		if (image == null) {
			return StringPool.BLANK;
		}

		Company company = CompanyLocalServiceUtil.fetchCompany(
			image.getCompanyId());

		if (company == null) {
			return StringPool.BLANK;
		}

		Date modifiedDate = image.getModifiedDate();

		long modifiedTime = 0;

		if (modifiedDate != null) {
			modifiedTime = modifiedDate.getTime();
		}

		return DigesterUtil.digestHex(
			DigesterUtil.SHA_256, String.valueOf(imageId),
			String.valueOf(modifiedTime), company.getKey());
	}

	@Override
	public void resetToken(long imageId) {
	}

}