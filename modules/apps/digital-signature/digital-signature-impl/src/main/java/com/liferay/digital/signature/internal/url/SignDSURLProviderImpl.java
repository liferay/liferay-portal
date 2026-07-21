/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.url;

import com.liferay.digital.signature.url.SignDSURLProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(service = SignDSURLProvider.class)
public class SignDSURLProviderImpl implements SignDSURLProvider {

	@Override
	public String getURL(long companyId, String dsEnvelopeId)
		throws PortalException {

		Group group = _groupLocalService.fetchFriendlyURLGroup(
			companyId, GroupConstants.DIGITAL_SIGNATURE_FRIENDLY_URL);

		if (group == null) {
			return null;
		}

		Company company = _companyLocalService.getCompany(companyId);

		return StringBundler.concat(
			company.getPortalURL(group.getGroupId()),
			_portal.getPathFriendlyURLPublic(),
			GroupConstants.DIGITAL_SIGNATURE_FRIENDLY_URL,
			"/sign/-/digital_signature/", dsEnvelopeId);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}