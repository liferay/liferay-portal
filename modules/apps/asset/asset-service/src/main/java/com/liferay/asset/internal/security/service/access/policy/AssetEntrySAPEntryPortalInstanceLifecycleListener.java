/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.security.service.access.policy;

import com.liferay.asset.kernel.service.AssetEntryService;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class AssetEntrySAPEntryPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		try {
			_addSAPEntry(company.getCompanyId());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add service access policy entry for company " +
					company.getCompanyId(),
				portalException);
		}
	}

	private void _addSAPEntry(long companyId) throws PortalException {
		SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
			companyId, _SAP_ENTRY_NAME);

		if (sapEntry != null) {
			return;
		}

		String allowedServiceSignatures =
			AssetEntryService.class.getName() +
				"#incrementViewCounter(long,java.lang.String,long)";
		Map<Locale, String> titleMap = ResourceBundleUtil.getLocalizationMap(
			LanguageResources.PORTAL_RESOURCE_BUNDLE_LOADER,
			"service-access-policy-entry-default-asset-entry-title");

		_sapEntryLocalService.addSAPEntry(
			_userLocalService.getGuestUserId(companyId),
			allowedServiceSignatures, true, true, _SAP_ENTRY_NAME, titleMap,
			new ServiceContext());
	}

	private static final String _SAP_ENTRY_NAME = "ASSET_ENTRY_DEFAULT";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntrySAPEntryPortalInstanceLifecycleListener.class);

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}