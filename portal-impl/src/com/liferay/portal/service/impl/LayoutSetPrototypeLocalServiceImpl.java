/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredLayoutSetPrototypeException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.service.base.LayoutSetPrototypeLocalServiceBaseImpl;
import com.liferay.portal.util.PortalInstances;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Ryan Park
 */
public class LayoutSetPrototypeLocalServiceImpl
	extends LayoutSetPrototypeLocalServiceBaseImpl {

	@Override
	public LayoutSetPrototype addLayoutSetPrototype(
			long userId, long companyId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			boolean layoutsUpdateable, ServiceContext serviceContext)
		throws PortalException {

		// Layout set prototype

		User user = _userPersistence.findByPrimaryKey(userId);
		Date date = new Date();

		long layoutSetPrototypeId = counterLocalService.increment();

		LayoutSetPrototype layoutSetPrototype =
			layoutSetPrototypePersistence.create(layoutSetPrototypeId);

		layoutSetPrototype.setUuid(serviceContext.getUuid());
		layoutSetPrototype.setCompanyId(companyId);
		layoutSetPrototype.setUserId(userId);
		layoutSetPrototype.setUserName(user.getFullName());
		layoutSetPrototype.setCreateDate(serviceContext.getCreateDate(date));
		layoutSetPrototype.setModifiedDate(
			serviceContext.getModifiedDate(date));
		layoutSetPrototype.setNameMap(nameMap);
		layoutSetPrototype.setDescriptionMap(descriptionMap);
		layoutSetPrototype.setActive(active);

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		settingsUnicodeProperties.put(
			"layoutsUpdateable", String.valueOf(layoutsUpdateable));

		layoutSetPrototype.setSettingsProperties(settingsUnicodeProperties);

		layoutSetPrototype = layoutSetPrototypePersistence.update(
			layoutSetPrototype);

		// Resources

		_resourceLocalService.addResources(
			companyId, 0, userId, LayoutSetPrototype.class.getName(),
			layoutSetPrototype.getLayoutSetPrototypeId(), false, true, false);

		// Group

		String friendlyURL =
			"/template-" + layoutSetPrototype.getLayoutSetPrototypeId();

		_groupLocalService.addGroup(
			StringPool.BLANK, userId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
			LayoutSetPrototype.class.getName(),
			layoutSetPrototype.getLayoutSetPrototypeId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			layoutSetPrototype.getNameMap(), null, 0, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, false,
			false, true, serviceContext);

		return layoutSetPrototype;
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public LayoutSetPrototype deleteLayoutSetPrototype(
			LayoutSetPrototype layoutSetPrototype)
		throws PortalException {

		// Group

		if (!PortalInstances.isCurrentCompanyInDeletionProcess()) {
			long count = _layoutSetPersistence.countByC_L(
				layoutSetPrototype.getCompanyId(),
				layoutSetPrototype.getUuid());

			if (count > 0) {
				throw new RequiredLayoutSetPrototypeException();
			}
		}

		_groupLocalService.deleteGroup(layoutSetPrototype.getGroup());

		// Resources

		_resourceLocalService.deleteResource(
			layoutSetPrototype.getCompanyId(),
			LayoutSetPrototype.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			layoutSetPrototype.getLayoutSetPrototypeId());

		// Layout set prototype

		layoutSetPrototypePersistence.remove(layoutSetPrototype);

		return layoutSetPrototype;
	}

	@Override
	public LayoutSetPrototype deleteLayoutSetPrototype(
			long layoutSetPrototypeId)
		throws PortalException {

		LayoutSetPrototype layoutSetPrototype =
			layoutSetPrototypePersistence.findByPrimaryKey(
				layoutSetPrototypeId);

		return deleteLayoutSetPrototype(layoutSetPrototype);
	}

	@Override
	public void deleteLayoutSetPrototypes() throws PortalException {
		List<LayoutSetPrototype> layoutSetPrototypes =
			layoutSetPrototypePersistence.findAll();

		for (LayoutSetPrototype layoutSetPrototype : layoutSetPrototypes) {
			layoutSetPrototypeLocalService.deleteLayoutSetPrototype(
				layoutSetPrototype);
		}
	}

	@Override
	public void deleteNondefaultLayoutSetPrototypes(long companyId)
		throws PortalException {

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		List<LayoutSetPrototype> layoutSetPrototypes =
			layoutSetPrototypePersistence.findByCompanyId(companyId);

		for (LayoutSetPrototype layoutSetPrototype : layoutSetPrototypes) {
			if (layoutSetPrototype.getUserId() != guestUserId) {
				deleteLayoutSetPrototype(layoutSetPrototype);
			}
		}
	}

	@Override
	public List<LayoutSetPrototype> getLayoutSetPrototypes(long companyId) {
		return layoutSetPrototypePersistence.findByCompanyId(companyId);
	}

	@Override
	public List<LayoutSetPrototype> search(
		long companyId, Boolean active, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		if (active != null) {
			return layoutSetPrototypePersistence.findByC_A(
				companyId, active, start, end, orderByComparator);
		}

		return layoutSetPrototypePersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int searchCount(long companyId, Boolean active) {
		if (active != null) {
			return layoutSetPrototypePersistence.countByC_A(companyId, active);
		}

		return layoutSetPrototypePersistence.countByCompanyId(companyId);
	}

	@Override
	public LayoutSetPrototype updateLayoutSetPrototype(
			long layoutSetPrototypeId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			boolean layoutsUpdateable, ServiceContext serviceContext)
		throws PortalException {

		// Layout set prototype

		LayoutSetPrototype layoutSetPrototype =
			layoutSetPrototypePersistence.findByPrimaryKey(
				layoutSetPrototypeId);

		layoutSetPrototype.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		layoutSetPrototype.setNameMap(nameMap);
		layoutSetPrototype.setDescriptionMap(descriptionMap);
		layoutSetPrototype.setActive(active);

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		settingsUnicodeProperties.put(
			"layoutsUpdateable", String.valueOf(layoutsUpdateable));

		layoutSetPrototype.setSettingsProperties(settingsUnicodeProperties);

		return layoutSetPrototypePersistence.update(layoutSetPrototype);
	}

	@Override
	public LayoutSetPrototype updateLayoutSetPrototype(
			long layoutSetPrototypeId, String settings)
		throws PortalException {

		// Layout set prototype

		LayoutSetPrototype layoutSetPrototype =
			layoutSetPrototypePersistence.findByPrimaryKey(
				layoutSetPrototypeId);

		layoutSetPrototype.setModifiedDate(new Date());
		layoutSetPrototype.setSettings(settings);

		layoutSetPrototype = layoutSetPrototypePersistence.update(
			layoutSetPrototype);

		// Group

		UnicodeProperties settingsUnicodeProperties =
			layoutSetPrototype.getSettingsProperties();

		if (!settingsUnicodeProperties.containsKey(
				"customJspServletContextName")) {

			return layoutSetPrototype;
		}

		Group group = _groupLocalService.getLayoutSetPrototypeGroup(
			layoutSetPrototype.getCompanyId(), layoutSetPrototypeId);

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"customJspServletContextName",
			settingsUnicodeProperties.getProperty(
				"customJspServletContextName"));

		group.setTypeSettings(typeSettingsUnicodeProperties.toString());

		_groupPersistence.update(group);

		return layoutSetPrototype;
	}

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = LayoutSetPersistence.class)
	private LayoutSetPersistence _layoutSetPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}