/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.internal.url;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.url.DSRequestURLProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(service = DSRequestURLProvider.class)
public class DSRequestURLProviderImpl implements DSRequestURLProvider {

	@Override
	public String getSignModalURL(
			String backURL, long companyId, long dsRequestId, long groupId)
		throws PortalException {

		String portletNamespace = _portal.getPortletNamespace(
			DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);

		return HttpComponentsUtil.setParameter(
			HttpComponentsUtil.setParameter(
				getSignURL(companyId, dsRequestId, groupId), "p_p_state",
				LiferayWindowState.POP_UP.toString()),
			portletNamespace + "backURL", backURL);
	}

	@Override
	public String getSignURL(long companyId, long dsRequestId, long groupId)
		throws PortalException {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			group = _groupLocalService.getGroup(
				companyId, GroupConstants.GUEST);
		}

		Company company = _companyLocalService.getCompany(companyId);

		return HttpComponentsUtil.addParameter(
			StringBundler.concat(
				company.getPortalURL(group.getGroupId()), _portal.getPathMain(),
				"/digital_signature/open_ds_request"),
			"dsRequestId", dsRequestId);
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}