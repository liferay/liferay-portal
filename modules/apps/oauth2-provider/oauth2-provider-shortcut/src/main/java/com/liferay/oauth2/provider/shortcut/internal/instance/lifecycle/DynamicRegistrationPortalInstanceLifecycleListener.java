package com.liferay.oauth2.provider.shortcut.internal.instance.lifecycle;

import com.liferay.oauth2.provider.constants.ClientProfile;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.osgi.util.configuration.ConfigurationPersistenceUtil;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"applicationName=Dynamic Registrator", "clientId=DynamicRegistrator"
	},
	service = PortalInstanceLifecycleListener.class
)
public class DynamicRegistrationPortalInstanceLifecycleListener extends
	BasePortalInstanceLifecycleListener {

	@Override
	public long getLastModifiedTime() {
		return _lastModifiedTime;
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.fetchOAuth2Application(
				company.getCompanyId(), _clientId);

		if (oAuth2Application != null) {
			return;
		}

		User user = _userLocalService.getUserByScreenName(
			company.getCompanyId(), "default-service-account");


		_oAuth2ApplicationLocalService.addOAuth2Application(
			company.getCompanyId(), user.getUserId(), user.getScreenName(),
			new ArrayList<GrantType>() {
				{
					add(GrantType.CLIENT_CREDENTIALS);
				}
			},
			"client_secret_post", user.getUserId(), _clientId,
			ClientProfile.HEADLESS_SERVER.id(), OAuth2SecureRandomGenerator.generateClientSecret(), null, null,
			null, 0, null, _applicationName, null, Collections.emptyList(),
			false, false,
			null,
			new ServiceContext());
	}

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		_lastModifiedTime = ConfigurationPersistenceUtil.update(
			this, properties);

		_applicationName = GetterUtil.getString(
			properties.get("applicationName"));
		_clientId = GetterUtil.getString(properties.get("clientId"));
	}

	private String _applicationName = "Dynamic Registrator";
	private String _clientId = "DynamicRegistrator";
	private long _lastModifiedTime;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
