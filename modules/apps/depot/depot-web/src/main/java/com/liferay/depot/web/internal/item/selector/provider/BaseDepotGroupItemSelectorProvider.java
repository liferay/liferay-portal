/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.provider;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 * @author Roberto Díaz
 */
public abstract class BaseDepotGroupItemSelectorProvider
	implements GroupItemSelectorProvider {

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public String getEmptyResultsMessage() {
		return getEmptyResultsMessageKey();
	}

	@Override
	public String getEmptyResultsMessage(Locale locale) {
		return ResourceBundleUtil.getString(
			ResourceBundleUtil.getBundle(locale, getClass()),
			getEmptyResultsMessageKey());
	}

	@Override
	public List<Group> getGroups(
		long companyId, long groupId, String keywords, int start, int end) {

		try {
			List<Group> groups = new ArrayList<>();

			for (DepotEntry depotEntry :
					depotEntryService.getCurrentAndGroupConnectedDepotEntries(
						_getGroupId(groupId), getDepotEntryType(), start,
						end)) {

				groups.add(depotEntry.getGroup());
			}

			return groups;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Collections.emptyList();
		}
	}

	@Override
	public int getGroupsCount(long companyId, long groupId, String keywords) {
		try {
			return depotEntryService.getGroupConnectedDepotEntriesCount(
				_getGroupId(groupId), getDepotEntryType());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return 0;
		}
	}

	@Override
	public String getIcon() {
		return "books";
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, getLabelKey());
	}

	protected abstract int getDepotEntryType();

	protected abstract String getEmptyResultsMessageKey();

	protected abstract String getLabelKey();

	@Reference
	protected DepotEntryService depotEntryService;

	@Reference
	protected GroupService groupService;

	@Reference
	protected Language language;

	@Reference
	protected LayoutPageTemplateEntryLocalService
		layoutPageTemplateEntryLocalService;

	@Reference
	protected LayoutPrototypeService layoutPrototypeService;

	private long _getGroupId(long groupId) throws PortalException {
		Group group = groupService.getGroup(groupId);

		if (group.isLayoutPrototype()) {
			LayoutPrototype layoutPrototype =
				layoutPrototypeService.getLayoutPrototype(group.getClassPK());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				layoutPageTemplateEntryLocalService.
					fetchFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId());

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getGroupId() > 0)) {

				group = groupService.getGroup(
					layoutPageTemplateEntry.getGroupId());
			}
		}

		return group.getGroupId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseDepotGroupItemSelectorProvider.class);

}