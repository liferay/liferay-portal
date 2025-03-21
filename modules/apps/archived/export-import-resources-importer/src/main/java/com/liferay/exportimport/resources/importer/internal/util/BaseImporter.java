/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.servlet.ServletContext;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public abstract class BaseImporter implements Importer {

	@Override
	public void afterPropertiesSet() throws Exception {
		User user = UserLocalServiceUtil.getGuestUser(companyId);

		userId = user.getUserId();

		if (isCompanyGroup()) {
			return;
		}

		Group group = null;

		if (targetClassName.equals(LayoutSetPrototype.class.getName())) {
			LayoutSetPrototype layoutSetPrototype = getLayoutSetPrototype(
				companyId, targetValue);

			if (layoutSetPrototype != null) {
				existing = true;
			}
			else {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setAttribute("addDefaultLayout", Boolean.FALSE);

				layoutSetPrototype =
					LayoutSetPrototypeLocalServiceUtil.addLayoutSetPrototype(
						userId, companyId, getTargetValueMap(),
						new HashMap<Locale, String>(), true, true,
						serviceContext);
			}

			group = layoutSetPrototype.getGroup();

			targetClassPK = layoutSetPrototype.getLayoutSetPrototypeId();
		}
		else if (targetClassName.equals(Group.class.getName())) {
			if (targetValue.equals(GroupConstants.GLOBAL)) {
				group = GroupLocalServiceUtil.getCompanyGroup(companyId);
			}
			else if (targetValue.equals(GroupConstants.GUEST)) {
				group = GroupLocalServiceUtil.getGroup(
					companyId, GroupConstants.GUEST);

				List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
					group.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false, 0, 1);

				if (!layouts.isEmpty()) {
					Layout layout = layouts.get(0);

					LayoutTypePortlet layoutTypePortlet =
						(LayoutTypePortlet)layout.getLayoutType();

					List<String> portletIds = layoutTypePortlet.getPortletIds();

					if (portletIds.size() != 2) {
						existing = true;
					}

					for (String portletId : portletIds) {
						if (!portletId.equals("47") &&
							!portletId.equals("58")) {

							existing = true;
						}
					}
				}
			}
			else {
				group = GroupLocalServiceUtil.fetchGroup(
					companyId, targetValue);

				if (group != null) {
					int privateLayoutsPageCount =
						group.getPrivateLayoutsPageCount();
					int publicLayoutsPageCount =
						group.getPublicLayoutsPageCount();

					if ((privateLayoutsPageCount != 0) ||
						(publicLayoutsPageCount != 0)) {

						existing = true;
					}
				}
				else {
					group = GroupLocalServiceUtil.addGroup(
						userId, GroupConstants.DEFAULT_PARENT_GROUP_ID,
						StringPool.BLANK,
						GroupConstants.DEFAULT_PARENT_GROUP_ID,
						GroupConstants.DEFAULT_LIVE_GROUP_ID,
						getMap(targetValue), null,
						GroupConstants.TYPE_SITE_OPEN, true,
						GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null,
						true, true, new ServiceContext());
				}
			}

			targetClassPK = group.getGroupId();
		}

		if (group != null) {
			groupId = group.getGroupId();
		}
	}

	@Override
	public long getGroupId() {
		return groupId;
	}

	@Override
	public String getTargetClassName() {
		return targetClassName;
	}

	@Override
	public long getTargetClassPK() {
		return targetClassPK;
	}

	public Map<Locale, String> getTargetValueMap() {
		return HashMapBuilder.put(
			LocaleUtil.getDefault(), targetValue
		).build();
	}

	@Override
	public boolean isCompanyGroup() throws Exception {
		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		return group.isCompany();
	}

	@Override
	public boolean isDeveloperModeEnabled() {
		return developerModeEnabled;
	}

	@Override
	public boolean isExisting() {
		return existing;
	}

	@Override
	public boolean isIndexAfterImport() {
		return indexAfterImport;
	}

	@Override
	public void setAppendVersion(boolean appendVersion) {
		this.appendVersion = appendVersion;
	}

	@Override
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	@Override
	public void setDeveloperModeEnabled(boolean developerModeEnabled) {
		this.developerModeEnabled = developerModeEnabled;
	}

	@Override
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	@Override
	public void setIndexAfterImport(boolean indexAfterImport) {
		this.indexAfterImport = indexAfterImport;
	}

	@Override
	public void setResourcesDir(String resourcesDir) {
		this.resourcesDir = resourcesDir;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}

	@Override
	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	@Override
	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}

	@Override
	public void setUpdateModeEnabled(boolean updateModeEnabled) {
		this.updateModeEnabled = updateModeEnabled;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	protected LayoutPrototype getLayoutPrototype(long companyId, String name) {
		Locale locale = LocaleUtil.getDefault();

		List<LayoutPrototype> layoutPrototypes =
			LayoutPrototypeLocalServiceUtil.search(
				companyId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (LayoutPrototype layoutPrototype : layoutPrototypes) {
			if (name.equals(layoutPrototype.getName(locale))) {
				return layoutPrototype;
			}
		}

		return null;
	}

	protected LayoutSetPrototype getLayoutSetPrototype(
			long companyId, String name)
		throws Exception {

		Locale locale = LocaleUtil.getDefault();

		List<LayoutSetPrototype> layoutSetPrototypes =
			LayoutSetPrototypeLocalServiceUtil.search(
				companyId, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (LayoutSetPrototype layoutSetPrototype : layoutSetPrototypes) {
			if (name.equals(layoutSetPrototype.getName(locale))) {
				return layoutSetPrototype;
			}
		}

		return null;
	}

	protected Map<Locale, String> getMap(Locale locale, String value) {
		return HashMapBuilder.put(
			locale, value
		).build();
	}

	protected Map<Locale, String> getMap(String value) {
		return getMap(LocaleUtil.getDefault(), value);
	}

	protected boolean appendVersion;
	protected long companyId;
	protected boolean developerModeEnabled;
	protected boolean existing;
	protected long groupId;
	protected boolean indexAfterImport;
	protected String resourcesDir;
	protected ServletContext servletContext;
	protected String servletContextName;
	protected String targetClassName;
	protected long targetClassPK;
	protected String targetValue;
	protected boolean updateModeEnabled;
	protected long userId;
	protected String version;

}