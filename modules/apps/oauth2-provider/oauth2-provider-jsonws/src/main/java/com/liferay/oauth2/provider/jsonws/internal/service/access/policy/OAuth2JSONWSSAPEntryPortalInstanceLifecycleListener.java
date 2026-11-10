/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.jsonws.internal.service.access.policy;

import com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration;
import com.liferay.osgi.util.configuration.ConfigurationPersistenceUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration",
	service = PortalInstanceLifecycleListener.class
)
public class OAuth2JSONWSSAPEntryPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public long getLastModifiedTime() {
		return _lastModifiedTime;
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!_oAuth2JSONWSConfiguration.createOAuth2SAPEntriesOnStartup()) {
			return;
		}

		try {
			_addSAPEntries(company.getCompanyId());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add service access policy entry for company " +
					company.getCompanyId(),
				portalException);
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		_oAuth2JSONWSConfiguration = ConfigurableUtil.createConfigurable(
			OAuth2JSONWSConfiguration.class, properties);

		_lastModifiedTime = ConfigurationPersistenceUtil.update(
			this, properties);
	}

	private void _addSAPEntries(long companyId) throws PortalException {
		for (String[] sapEntryObjectArray : _SAP_ENTRY_OBJECT_ARRAYS) {
			String name = sapEntryObjectArray[0];

			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				companyId, name);

			if (sapEntry != null) {
				continue;
			}

			String allowedServiceSignatures = sapEntryObjectArray[1];
			Map<Locale, String> map = ResourceBundleUtil.getLocalizationMap(
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader(), name);

			_sapEntryLocalService.addSAPEntry(
				_userLocalService.getGuestUserId(companyId),
				allowedServiceSignatures, false, true, name, map,
				new ServiceContext());
		}
	}

	private static final String[][] _SAP_ENTRY_OBJECT_ARRAYS = {
		{"OAUTH2_everything", "*"},
		{"OAUTH2_everything.read", "#fetch*\n#get*\n#has*\n#is*\n#search*"},
		{
			"OAUTH2_everything.read.documents.download",
			"com.liferay.document.library.kernel.service.DLAppService#get*\n" +
				"com.liferay.portal.kernel.service.ImageService#get*"
		},
		{
			"OAUTH2_everything.read.userprofile",
			"com.liferay.portal.kernel.service.UserService#getCurrentUser"
		},
		{"OAUTH2_everything.write", "#add*\n#create*\n#update*\n#delete*"}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2JSONWSSAPEntryPortalInstanceLifecycleListener.class);

	private long _lastModifiedTime;
	private OAuth2JSONWSConfiguration _oAuth2JSONWSConfiguration;

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}