/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.NoSuchLayoutPrototypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredLayoutPrototypeException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.service.base.LayoutPrototypeLocalServiceBaseImpl;
import com.liferay.portal.util.PortalInstances;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Vilmos Papp
 */
public class LayoutPrototypeLocalServiceImpl
	extends LayoutPrototypeLocalServiceBaseImpl {

	@Override
	public LayoutPrototype addLayoutPrototype(
			long userId, long companyId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		// Layout prototype

		User user = _userPersistence.findByPrimaryKey(userId);
		Date date = new Date();

		long layoutPrototypeId = counterLocalService.increment();

		LayoutPrototype layoutPrototype = layoutPrototypePersistence.create(
			layoutPrototypeId);

		layoutPrototype.setUuid(serviceContext.getUuid());
		layoutPrototype.setCompanyId(companyId);
		layoutPrototype.setUserId(userId);
		layoutPrototype.setUserName(user.getFullName());
		layoutPrototype.setCreateDate(serviceContext.getCreateDate(date));
		layoutPrototype.setModifiedDate(serviceContext.getModifiedDate(date));
		layoutPrototype.setNameMap(nameMap);
		layoutPrototype.setDescriptionMap(descriptionMap);
		layoutPrototype.setActive(active);

		layoutPrototype = layoutPrototypePersistence.update(layoutPrototype);

		// Resources

		_resourceLocalService.addResources(
			companyId, 0, userId, LayoutPrototype.class.getName(),
			layoutPrototype.getLayoutPrototypeId(), false, true, false);

		// Group

		String friendlyURL =
			"/template-" + layoutPrototype.getLayoutPrototypeId();

		Group group = _groupLocalService.addGroup(
			StringPool.BLANK, userId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
			LayoutPrototype.class.getName(),
			layoutPrototype.getLayoutPrototypeId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID, layoutPrototype.getNameMap(),
			null, 0, null, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			friendlyURL, false, false, true, null);

		if (GetterUtil.getBoolean(
				serviceContext.getAttribute("addDefaultLayout"), true)) {

			_layoutLocalService.addLayout(
				null, userId, group.getGroupId(), true,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
				layoutPrototype.getNameMap(), null, null, null, null,
				LayoutConstants.TYPE_PORTLET, StringPool.BLANK, false,
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), "/layout"
				).build(),
				serviceContext);
		}

		return layoutPrototype;
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public LayoutPrototype deleteLayoutPrototype(
			LayoutPrototype layoutPrototype)
		throws PortalException {

		// Group

		if (!PortalInstances.isCurrentCompanyInDeletionProcess()) {
			int count = _layoutPersistence.countByC_L(
				layoutPrototype.getCompanyId(), layoutPrototype.getUuid());

			if (count > 0) {
				throw new RequiredLayoutPrototypeException(
					StringBundler.concat(
						"Layout prototype cannot be deleted because it is ",
						"used by layout with company ID ",
						layoutPrototype.getCompanyId(),
						" and layout prototype UUID ",
						layoutPrototype.getUuid()));
			}
		}

		_groupLocalService.deleteGroup(layoutPrototype.getGroup());

		// Resources

		_resourceLocalService.deleteResource(
			layoutPrototype.getCompanyId(), LayoutPrototype.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			layoutPrototype.getLayoutPrototypeId());

		// Layout Prototype

		layoutPrototypePersistence.remove(layoutPrototype);

		return layoutPrototype;
	}

	@Override
	public LayoutPrototype deleteLayoutPrototype(long layoutPrototypeId)
		throws PortalException {

		LayoutPrototype layoutPrototype =
			layoutPrototypePersistence.findByPrimaryKey(layoutPrototypeId);

		return layoutPrototypeLocalService.deleteLayoutPrototype(
			layoutPrototype);
	}

	@Override
	public void deleteNondefaultLayoutPrototypes(long companyId)
		throws PortalException {

		long guestUserId = _userLocalService.getGuestUserId(companyId);

		List<LayoutPrototype> layoutPrototypes =
			layoutPrototypePersistence.findByCompanyId(companyId);

		for (LayoutPrototype layoutPrototype : layoutPrototypes) {
			if (layoutPrototype.getUserId() != guestUserId) {
				layoutPrototypeLocalService.deleteLayoutPrototype(
					layoutPrototype);
			}
		}
	}

	@Override
	public LayoutPrototype fetchLayoutPrototype(
		long companyId, String name, Locale locale) {

		List<LayoutPrototype> layoutPrototypes =
			layoutPrototypePersistence.findByCompanyId(companyId);

		for (LayoutPrototype layoutPrototype : layoutPrototypes) {
			String layoutPrototypeName = layoutPrototype.getName(locale);

			if (layoutPrototypeName.equals(name)) {
				return layoutPrototype;
			}
		}

		return null;
	}

	@Override
	public LayoutPrototype fetchLayoutProtoype(long companyId, String name) {
		return layoutPrototypeLocalService.fetchLayoutPrototype(
			companyId, name, LocaleUtil.getDefault());
	}

	@Override
	public LayoutPrototype getLayoutPrototype(long companyId, String name)
		throws PortalException {

		return layoutPrototypeLocalService.getLayoutPrototype(
			companyId, name, LocaleUtil.getDefault());
	}

	@Override
	public LayoutPrototype getLayoutPrototype(
			long companyId, String name, Locale locale)
		throws PortalException {

		LayoutPrototype layoutPrototype =
			layoutPrototypeLocalService.fetchLayoutPrototype(
				companyId, name, locale);

		if (layoutPrototype == null) {
			throw new NoSuchLayoutPrototypeException();
		}

		return layoutPrototype;
	}

	@Override
	public List<LayoutPrototype> search(
		long companyId, Boolean active, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator) {

		if (active != null) {
			return layoutPrototypePersistence.findByC_A(
				companyId, active, start, end, orderByComparator);
		}

		return layoutPrototypePersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int searchCount(long companyId, Boolean active) {
		if (active != null) {
			return layoutPrototypePersistence.countByC_A(companyId, active);
		}

		return layoutPrototypePersistence.countByCompanyId(companyId);
	}

	@Override
	public LayoutPrototype updateLayoutPrototype(
			long layoutPrototypeId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active,
			ServiceContext serviceContext)
		throws PortalException {

		// Layout prototype

		LayoutPrototype layoutPrototype =
			layoutPrototypePersistence.findByPrimaryKey(layoutPrototypeId);

		layoutPrototype.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		layoutPrototype.setNameMap(nameMap);
		layoutPrototype.setDescriptionMap(descriptionMap);
		layoutPrototype.setActive(active);

		layoutPrototype = layoutPrototypePersistence.update(layoutPrototype);

		// Layout

		Layout layout = layoutPrototype.getLayout();

		layout.setModifiedDate(layoutPrototype.getModifiedDate());
		layout.setNameMap(nameMap);

		_layoutPersistence.update(layout);

		return layoutPrototype;
	}

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = LayoutPersistence.class)
	private LayoutPersistence _layoutPersistence;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}