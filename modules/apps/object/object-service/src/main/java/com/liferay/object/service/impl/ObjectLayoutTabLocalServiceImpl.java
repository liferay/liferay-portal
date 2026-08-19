/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.object.internal.layout.tab.screen.navigation.category.ObjectLayoutTabScreenNavigationCategory;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.service.base.ObjectLayoutTabLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectRelationshipPersistence;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectLayoutTab",
	service = AopService.class
)
public class ObjectLayoutTabLocalServiceImpl
	extends ObjectLayoutTabLocalServiceBaseImpl {

	@Override
	public ObjectLayoutTab addObjectLayoutTab(
			long userId, long objectLayoutId, long objectRelationshipId,
			Map<Locale, String> nameMap, int priority)
		throws PortalException {

		ObjectLayoutTab objectLayoutTab = objectLayoutTabPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectLayoutTab.setCompanyId(user.getCompanyId());
		objectLayoutTab.setUserId(user.getUserId());
		objectLayoutTab.setUserName(user.getFullName());

		objectLayoutTab.setObjectLayoutId(objectLayoutId);
		objectLayoutTab.setObjectRelationshipId(objectRelationshipId);
		objectLayoutTab.setNameMap(nameMap);
		objectLayoutTab.setPriority(priority);

		return objectLayoutTabPersistence.update(objectLayoutTab);
	}

	@Override
	public void deleteObjectLayoutObjectLayoutTabs(long objectLayoutId)
		throws PortalException {

		for (ObjectLayoutTab objectLayoutTab :
				objectLayoutTabPersistence.findByObjectLayoutId(
					objectLayoutId)) {

			deleteObjectLayoutTab(objectLayoutTab);
		}
	}

	@Override
	public void deleteObjectRelationshipObjectLayoutTabs(
			long objectRelationshipId)
		throws PortalException {

		for (ObjectLayoutTab objectLayoutTab :
				objectLayoutTabPersistence.findByObjectRelationshipId(
					objectRelationshipId)) {

			deleteObjectLayoutTab(objectLayoutTab);
		}
	}

	@Override
	public List<ObjectLayoutTab> getObjectLayoutObjectLayoutTabs(
		long objectLayoutId) {

		return objectLayoutTabPersistence.findByObjectLayoutId(objectLayoutId);
	}

	@Clusterable
	@Override
	public void registerObjectLayoutTabScreenNavigationCategories(
		ObjectDefinition objectDefinition,
		List<ObjectLayoutTab> objectLayoutTabs) {

		for (int i = objectLayoutTabs.size() - 1; i >= 0; i--) {
			ObjectLayoutTab objectLayoutTab = objectLayoutTabs.get(i);

			_serviceRegistrations.computeIfAbsent(
				_getServiceRegistrationKey(objectDefinition, objectLayoutTab),
				serviceRegistrationKey -> _bundleContext.registerService(
					new String[] {
						ScreenNavigationCategory.class.getName(),
						ScreenNavigationEntry.class.getName()
					},
					new ObjectLayoutTabScreenNavigationCategory(
						objectDefinition, objectLayoutTab,
						_objectRelationshipPersistence.fetchByPrimaryKey(
							objectLayoutTab.getObjectRelationshipId())),
					HashMapDictionaryBuilder.<String, Object>put(
						"screen.navigation.category.order:Integer",
						objectLayoutTab.getObjectLayoutTabId()
					).put(
						"screen.navigation.entry.order:Integer",
						objectLayoutTab.getObjectLayoutId()
					).build()));
		}
	}

	@Override
	public void unregisterObjectLayoutTabScreenNavigationCategories(
		ObjectDefinition objectDefinition) {

		for (String serviceRegistrationKey : _serviceRegistrations.keySet()) {
			if (!serviceRegistrationKey.startsWith(
					_getServiceRegistrationKey(objectDefinition) +
						StringPool.POUND)) {

				continue;
			}

			ServiceRegistration<?> serviceRegistration =
				_serviceRegistrations.remove(serviceRegistrationKey);

			if (serviceRegistration == null) {
				continue;
			}

			serviceRegistration.unregister();
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private String _getServiceRegistrationKey(
		ObjectDefinition objectDefinition) {

		return StringBundler.concat(
			objectDefinition.getCompanyId(), StringPool.POUND,
			objectDefinition.getObjectDefinitionId());
	}

	private String _getServiceRegistrationKey(
		ObjectDefinition objectDefinition, ObjectLayoutTab objectLayoutTab) {

		return StringBundler.concat(
			_getServiceRegistrationKey(objectDefinition), StringPool.POUND,
			objectLayoutTab.getObjectLayoutTabId());
	}

	private BundleContext _bundleContext;

	@Reference
	private ObjectRelationshipPersistence _objectRelationshipPersistence;

	private final Map<String, ServiceRegistration<?>> _serviceRegistrations =
		new ConcurrentHashMap<>();

	@Reference
	private UserLocalService _userLocalService;

}