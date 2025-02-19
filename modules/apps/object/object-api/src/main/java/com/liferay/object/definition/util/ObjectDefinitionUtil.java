/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalInstances;

import java.util.Map;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class ObjectDefinitionUtil {

	public static String getModifiableSystemObjectDefinitionRESTContextPath(
		String name) {

		if (PortalRunMode.isTestMode() && Objects.equals(name, "Test")) {
			return "/test";
		}

		return _allowedModifiableSystemObjectDefinitionNames.get(name);
	}

	public static boolean isAllowedModifiableSystemObjectDefinitionName(
		String name) {

		if (PortalRunMode.isTestMode() && StringUtil.startsWith(name, "Test")) {
			return true;
		}

		return _allowedModifiableSystemObjectDefinitionNames.containsKey(name);
	}

	public static boolean isInvokerBundleAllowed() {
		if (PortalInstances.isCurrentCompanyInDeletionProcess() ||
			PortalRunMode.isTestMode() || StartupHelperUtil.isUpgrading()) {

			return true;
		}

		String fileName = BatchEngineUnitThreadLocal.getFileName();

		for (String allowedInvokerBundleSymbolicName :
				_ALLOWED_INVOKER_BUNDLE_SYMBOLIC_NAMES) {

			if (fileName.matches(
					_getInvokerFileNameRegex(
						allowedInvokerBundleSymbolicName))) {

				return true;
			}
		}

		return false;
	}

	private static String _getInvokerFileNameRegex(
		String allowedInvokerBundleSymbolicName) {

		String invokerFileNameRegex = StringUtil.replace(
			allowedInvokerBundleSymbolicName, '.', "\\.");

		return invokerFileNameRegex + "_\\d+\\.\\d+\\.\\d+\\s+\\[\\d+\\]";
	}

	private static final String[] _ALLOWED_INVOKER_BUNDLE_SYMBOLIC_NAMES = {
		"com.liferay.commerce.service", "com.liferay.cookies.impl",
		"com.liferay.frontend.data.set.admin.web",
		"com.liferay.frontend.data.set.impl",
		"com.liferay.headless.builder.impl", "com.liferay.list.type.service",
		"com.liferay.notification.service", "com.liferay.object.service",
		"com.liferay.site.initializer.cms"
	};

	private static final Map<String, String>
		_allowedModifiableSystemObjectDefinitionNames = HashMapBuilder.put(
			"APIApplication", "/headless-builder/applications"
		).put(
			"APIEndpoint", "/headless-builder/endpoints"
		).put(
			"APIFilter", "/headless-builder/filters"
		).put(
			"APIProperty", "/headless-builder/properties"
		).put(
			"APISchema", "/headless-builder/schemas"
		).put(
			"APISort", "/headless-builder/sorts"
		).put(
			"BasicDocument", "/cms/basic-documents"
		).put(
			"BasicWebContent", "/cms/basic-web-contents"
		).put(
			"Bookmark", "/bookmarks"
		).put(
			"CommerceReturn", "/commerce/returns"
		).put(
			"CommerceReturnItem", "/commerce/return-items"
		).put(
			"DataSet", "/data-set-admin/data-sets"
		).put(
			"DataSetAction", "/data-set-admin/data-sets/actions"
		).put(
			"DataSetCardsSection", "/data-set-admin/data-sets/cards-sections"
		).put(
			"DataSetClientExtensionFilter",
			"/data-set-admin/data-sets/client-extension-filters"
		).put(
			"DataSetDateFilter", "/data-set-admin/data-sets/date-filters"
		).put(
			"DataSetListSection", "/data-set-admin/data-sets/list-sections"
		).put(
			"DataSetSelectionFilter",
			"/data-set-admin/data-sets/selection-filters"
		).put(
			"DataSetSort", "/data-set-admin/data-sets/sorts"
		).put(
			"DataSetTableSection", "/data-set-admin/data-sets/table-sections"
		).put(
			"FDSAction", "/data-set-manager/actions"
		).put(
			"FDSCardsSection", "/data-set-manager/cards-sections"
		).put(
			"FDSClientExtensionFilter",
			"/data-set-manager/client-extension-filters"
		).put(
			"FDSDateFilter", "/data-set-manager/date-filters"
		).put(
			"FDSDynamicFilter", "/data-set-manager/selection-filters"
		).put(
			"FDSEntry", "/data-set-manager/entries"
		).put(
			"FDSField", "/data-set-manager/table-sections"
		).put(
			"FDSListSection", "/data-set-manager/list-sections"
		).put(
			"FDSSort", "/data-set-manager/sorts"
		).put(
			"FDSView", "/data-set-manager/data-sets"
		).put(
			"FunctionalCookieEntry", "/functional-cookies-entries"
		).put(
			"NecessaryCookieEntry", "/necessary-cookies-entries"
		).put(
			"PerformanceCookieEntry", "/performance-cookies-entries"
		).put(
			"PersonalizationCookieEntry", "/personalization-cookies-entries"
		).build();

}