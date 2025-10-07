/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.internal.security.permission.resource;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.permission.contributor.PermissionSQLContributor;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.sharing.internal.configuration.SharingSystemConfiguration;
import com.liferay.sharing.internal.security.permission.contributor.SharingPermissionSQLContributor;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.resource.SharingModelResourcePermissionConfigurator;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Sergio González
 * @author Preston Crary
 */
@Component(
	configurationPid = "com.liferay.sharing.internal.configuration.SharingSystemConfiguration",
	service = SharingModelResourcePermissionConfigurator.class
)
public class SharingModelResourcePermissionConfiguratorImpl
	implements SharingModelResourcePermissionConfigurator {

	@Override
	public <T extends GroupedModel> void configure(
		ModelResourcePermission<T> modelResourcePermission,
		Consumer<ModelResourcePermissionLogic<T>> consumer) {

		if (_sharingSystemConfiguration.enabled()) {
			_modelClassNames.add(modelResourcePermission.getModelName());

			_sharingPermissionSQLContributorServiceRegistration.setProperties(
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", _modelClassNames.toArray()
				).build());

			consumer.accept(
				new SharingModelResourcePermissionLogic<>(
					_classNameLocalService.getClassNameId(
						modelResourcePermission.getModelName())));
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_sharingSystemConfiguration = ConfigurableUtil.createConfigurable(
			SharingSystemConfiguration.class, properties);

		_sharingPermissionSQLContributorServiceRegistration =
			bundleContext.registerService(
				PermissionSQLContributor.class,
				new SharingPermissionSQLContributor(
					_classNameLocalService, _groupLocalService,
					_sharingConfigurationFactory, _sharingEntryLocalService,
					_userGroupLocalService, _userLocalService),
				new HashMapDictionary<>());
	}

	@Deactivate
	protected void deactivate() {
		_sharingPermissionSQLContributorServiceRegistration.unregister();
	}

	private static Map<String, SharingEntryAction> _getSharingEntryActions() {
		Map<String, SharingEntryAction> sharingEntryActions = new HashMap<>();

		for (SharingEntryAction sharingEntryAction :
				SharingEntryAction.values()) {

			sharingEntryActions.put(
				sharingEntryAction.getActionId(), sharingEntryAction);
		}

		return sharingEntryActions;
	}

	private static final Map<String, SharingEntryAction> _sharingEntryActions =
		_getSharingEntryActions();

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	private final Set<String> _modelClassNames = new HashSet<>();

	@Reference
	private SharingConfigurationFactory _sharingConfigurationFactory;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	private ServiceRegistration<PermissionSQLContributor>
		_sharingPermissionSQLContributorServiceRegistration;
	private SharingSystemConfiguration _sharingSystemConfiguration;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class SharingModelResourcePermissionLogic<T extends GroupedModel>
		implements ModelResourcePermissionLogic<T> {

		@Override
		public Boolean contains(
				PermissionChecker permissionChecker, String name, T model,
				String actionId)
			throws PortalException {

			SharingEntryAction sharingEntryAction = _sharingEntryActions.get(
				actionId);

			if (sharingEntryAction == null) {
				return null;
			}

			long primaryKey = (Long)model.getPrimaryKeyObj();

			if (permissionChecker.hasOwnerPermission(
					model.getCompanyId(), name, primaryKey, model.getUserId(),
					actionId) ||
				permissionChecker.hasPermission(
					model.getGroupId(), name, primaryKey, actionId) ||
				!_sharingEntryLocalService.hasSharingPermission(
					permissionChecker.getUserId(), _classNameId, primaryKey,
					sharingEntryAction)) {

				return null;
			}

			SharingConfiguration sharingConfiguration =
				_sharingConfigurationFactory.getGroupSharingConfiguration(
					_groupLocalService.getGroup(model.getGroupId()));

			if (sharingConfiguration.isEnabled()) {
				return true;
			}

			return null;
		}

		private SharingModelResourcePermissionLogic(long classNameId) {
			_classNameId = classNameId;
		}

		private final long _classNameId;

	}

}