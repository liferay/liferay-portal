/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.defaultpermissions.web.internal.search.PortalDefaultPermissionsSearchEntry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Stefano Motta
 */
public abstract class
	BaseViewPortalDefaultPermissionsConfigurationDisplayContext {

	public abstract List<DropdownItem> getActionDropdownItems(
		PortalDefaultPermissionsSearchEntry
			portalDefaultPermissionsSearchEntry);

	public abstract String getEditURL(String className);

	public abstract PortletURL getPortletURL();

	public abstract SearchContainer<PortalDefaultPermissionsSearchEntry>
		getSearchContainer();

	protected List<PortalDefaultPermissionsSearchEntry> filter(
		List<PortalDefaultPermissionsSearchEntry>
			portalDefaultPermissionSearchEntries,
		String className, String label) {

		if (Validator.isNull(className) && Validator.isNull(label)) {
			return portalDefaultPermissionSearchEntries;
		}

		Predicate<PortalDefaultPermissionsSearchEntry> predicate =
			_createPredicate(className, label);

		return ListUtil.filter(
			portalDefaultPermissionSearchEntries, predicate::test);
	}

	private Predicate<PortalDefaultPermissionsSearchEntry> _createPredicate(
		String className, String label) {

		Predicate<PortalDefaultPermissionsSearchEntry> predicate =
			new PortalDefaultPermissionsSearchEntryClassNamePredicate(
				className);

		return predicate.or(
			new PortalDefaultPermissionsSearchEntryLabelPredicate(label));
	}

	private class PortalDefaultPermissionsSearchEntryClassNamePredicate
		implements Predicate<PortalDefaultPermissionsSearchEntry> {

		public PortalDefaultPermissionsSearchEntryClassNamePredicate(
			String keywords) {

			_keywords = keywords;
		}

		@Override
		public boolean test(
			PortalDefaultPermissionsSearchEntry
				portalDefaultPermissionsSearchEntry) {

			if (Validator.isNull(_keywords)) {
				return true;
			}

			String delimiter = StringPool.SPACE;

			if (!StringUtil.contains(_keywords, StringPool.SPACE)) {
				delimiter = StringPool.BLANK;
			}

			return StringUtil.containsIgnoreCase(
				portalDefaultPermissionsSearchEntry.getClassName(), _keywords,
				delimiter);
		}

		private final String _keywords;

	}

	private class PortalDefaultPermissionsSearchEntryLabelPredicate
		implements Predicate<PortalDefaultPermissionsSearchEntry> {

		public PortalDefaultPermissionsSearchEntryLabelPredicate(
			String keywords) {

			_keywords = keywords;
		}

		@Override
		public boolean test(
			PortalDefaultPermissionsSearchEntry
				portalDefaultPermissionsSearchEntry) {

			if (Validator.isNull(_keywords)) {
				return true;
			}

			String delimiter = StringPool.SPACE;

			if (!StringUtil.contains(_keywords, StringPool.SPACE)) {
				delimiter = StringPool.BLANK;
			}

			return StringUtil.containsIgnoreCase(
				portalDefaultPermissionsSearchEntry.getLabel(), _keywords,
				delimiter);
		}

		private final String _keywords;

	}

}