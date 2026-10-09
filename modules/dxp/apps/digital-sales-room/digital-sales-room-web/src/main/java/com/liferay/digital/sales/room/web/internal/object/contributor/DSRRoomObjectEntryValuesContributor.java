/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.object.contributor;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.digital.sales.room.thread.local.DSRRoomThreadLocal;
import com.liferay.digital.sales.room.web.internal.util.DSRUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.object.entry.ObjectEntryContext;
import com.liferay.object.entry.contributor.ObjectEntryValuesContributor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = ObjectEntryValuesContributor.class)
public class DSRRoomObjectEntryValuesContributor
	implements ObjectEntryValuesContributor {

	@Override
	public void contribute(ObjectEntryContext objectEntryContext) {
		try {
			_contribute(objectEntryContext);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private void _contribute(ObjectEntryContext objectEntryContext)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntryContext.getObjectDefinitionId());

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		// Object entry values contributors also run on updates, and only
		// ObjectEntryLocalService#addObjectEntry marks its service context as
		// a strict add

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) || !serviceContext.isStrictAdd()) {
			return;
		}

		User user = _userLocalService.getUser(objectEntryContext.getUserId());

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		ServiceContext groupServiceContext = new ServiceContext();

		groupServiceContext.setCompanyId(company.getCompanyId());
		groupServiceContext.setUserId(user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(groupServiceContext);

		Group group = null;
		Map<String, Serializable> values = objectEntryContext.getValues();

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company, user)) {

			// The object entry ID does not exist yet, so the group gets a
			// unique placeholder class PK that ObjectEntryModelListener
			// replaces after the object entry is created

			group = _groupLocalService.addGroup(
				null, user.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
				objectDefinition.getClassName(),
				_counterLocalService.increment(),
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.getDefault(), MapUtil.getString(values, "name")
				).build(),
				null, GroupConstants.TYPE_SITE_RESTRICTED, null, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
				DSRUtil.getFriendlyURL(values), true, false, true,
				groupServiceContext);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		values.put(
			"friendlyURL", StringUtil.removeFirst(group.getFriendlyURL(), "/"));

		if (DSRRoomThreadLocal.getObjectEntryId() != 0) {
			values.put("initialized", true);
		}

		values.put(
			"siteExternalReferenceCode", group.getExternalReferenceCode());
		values.put("siteId", group.getGroupId());
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}