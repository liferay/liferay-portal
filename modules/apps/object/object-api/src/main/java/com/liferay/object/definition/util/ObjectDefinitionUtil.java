/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.batch.engine.unit.BatchEngineUnitThreadLocal;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.modifiable.system.ModifiableSystemObjectDefinition;
import com.liferay.object.modifiable.system.ModifiableSystemObjectDefinitionRegistryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalInstances;

import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class ObjectDefinitionUtil {

	public static String generateRandomClassName() {
		StringBuilder sb = new StringBuilder();

		sb.append(
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION);
		sb.append(StringUtil.toUpperCase(StringUtil.randomId(1)));
		sb.append(RandomUtil.nextInt(10));
		sb.append(StringUtil.toUpperCase(StringUtil.randomId(1)));
		sb.append(RandomUtil.nextInt(10));

		return sb.toString();
	}

	public static String getItemClassName(ObjectDefinition objectDefinition) {
		if (objectDefinition.isSystem()) {
			return objectDefinition.getClassName() + StringPool.POUND +
				objectDefinition.getObjectDefinitionId();
		}

		return objectDefinition.getClassName();
	}

	public static String getModifiableSystemObjectDefinitionRESTContextPath(
		String name) {

		if (PortalRunMode.isTestMode() && StringUtil.startsWith(name, "Test")) {
			return "/test";
		}

		String restContextPath =
			_allowedModifiableSystemObjectDefinitionNames.get(name);

		if (restContextPath != null) {
			return restContextPath;
		}

		ModifiableSystemObjectDefinition modifiableSystemObjectDefinition =
			ModifiableSystemObjectDefinitionRegistryUtil.
				getModifiableSystemObjectDefinition(name);

		if (modifiableSystemObjectDefinition == null) {
			return null;
		}

		return modifiableSystemObjectDefinition.getRESTContextPath();
	}

	public static String getPortletId(String className) {
		return StringUtil.replaceFirst(
			className,
			ObjectDefinitionConstants.
				CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION,
			ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE);
	}

	public static boolean isAllowedModifiableSystemObjectDefinitionName(
		String name) {

		if ((PortalRunMode.isTestMode() &&
			 StringUtil.startsWith(name, "Test")) ||
			_allowedModifiableSystemObjectDefinitionNames.containsKey(name)) {

			return true;
		}

		if (!isInvokerBundleAllowed()) {
			return false;
		}

		ModifiableSystemObjectDefinition modifiableSystemObjectDefinition =
			ModifiableSystemObjectDefinitionRegistryUtil.
				getModifiableSystemObjectDefinition(name);

		if (modifiableSystemObjectDefinition != null) {
			return true;
		}

		return false;
	}

	public static boolean isDefaultFriendlyURLSeparator(
		String friendlyURLSeparator) {

		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.
				getFriendlyURLResolverByDefaultURLSeparator(
					FriendlyURLResolverConstants.URL_SEPARATOR_OBJECT_ENTRY);

		if ((friendlyURLResolver != null) &&
			StringUtil.equals(
				StringUtil.removeSubstring(
					friendlyURLResolver.getURLSeparator(), StringPool.SLASH),
				friendlyURLSeparator)) {

			return true;
		}

		return false;
	}

	public static boolean isInvokerBundleAllowed() {
		if (ObjectDefinitionThreadLocal.isSkipBundleAllowedCheck() ||
			PortalInstances.isCurrentCompanyInDeletionProcess() ||
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
		"com.liferay.account.service",
		"com.liferay.ai.hub.pricing.site.initializer",
		"com.liferay.ai.hub.site.initializer", "com.liferay.commerce.service",
		"com.liferay.content.site.generator.impl", "com.liferay.cookies.impl",
		"com.liferay.frontend.data.set.admin.web",
		"com.liferay.frontend.data.set.impl",
		"com.liferay.headless.builder.impl", "com.liferay.launch.impl",
		"com.liferay.list.type.service", "com.liferay.mcp.server.rest.impl",
		"com.liferay.notification.service", "com.liferay.object.service",
		"com.liferay.seo.studio.site.initializer",
		"com.liferay.site.initializer.cmp", "com.liferay.site.initializer.cms",
		"com.liferay.site.initializer.dsr"
	};

	private static final Map<String, String>
		_allowedModifiableSystemObjectDefinitionNames = HashMapBuilder.put(
			"AccountValidatorResult", "/account/validator-results"
		).put(
			"AIHubAgentDefinition", "/ai-hub/agent-definitions"
		).put(
			"AIHubChatbot", "/ai-hub/chatbots"
		).put(
			"AIHubConfiguration", "/ai-hub/configurations"
		).put(
			"AIHubContentRetriever", "/ai-hub/content-retrievers"
		).put(
			"AIHubCrawlerJob", "/ai-hub/crawler-jobs"
		).put(
			"AIHubInstructionDefinition", "/ai-hub/instruction-definitions"
		).put(
			"AIHubMCPServer", "/ai-hub/mcp-servers"
		).put(
			"AIHubModelArmorTemplate", "/ai-hub/model-armor-templates"
		).put(
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
			"Bookmark", "/bookmarks"
		).put(
			"CMPProject", "/cmp/projects"
		).put(
			"CMPTask", "/cmp/tasks"
		).put(
			"CMSBasicDocument", "/cms/basic-documents"
		).put(
			"CMSBasicWebContent", "/cms/basic-web-contents"
		).put(
			"CMSBlog", "/cms/blogs"
		).put(
			"CMSBulkActionTask", "/cms/bulk-action-tasks"
		).put(
			"CMSDefaultPermission", "/cms/default-permissions"
		).put(
			"CMSExternalVideo", "/cms/external-videos"
		).put(
			"CommerceReturn", "/commerce/returns"
		).put(
			"CommerceReturnItem", "/commerce/return-items"
		).put(
			"CSGGeneration", "/content-site-generator/generations"
		).put(
			"CSGGenerationItem", "/content-site-generator/generation-items"
		).put(
			"DataSet", "/data-set-admin/data-sets"
		).put(
			"DataSetAction", "/data-set-admin/actions"
		).put(
			"DataSetCardsSection", "/data-set-admin/cards-sections"
		).put(
			"DataSetClientExtensionFilter",
			"/data-set-admin/client-extension-filters"
		).put(
			"DataSetDateFilter", "/data-set-admin/date-filters"
		).put(
			"DataSetListSection", "/data-set-admin/list-sections"
		).put(
			"DataSetSelectionFilter", "/data-set-admin/selection-filters"
		).put(
			"DataSetSnapshot", "/data-set-admin/snapshots"
		).put(
			"DataSetSort", "/data-set-admin/sorts"
		).put(
			"DataSetTableSection", "/data-set-admin/table-sections"
		).put(
			"DSRRoom", "/digital-sales-room/rooms"
		).put(
			"DSRTemplate", "/digital-sales-room/templates"
		).put(
			"FunctionalCookieEntry", "/functional-cookies-entries"
		).put(
			"KnowledgeBase", "/cms/knowledge-bases"
		).put(
			"LaunchEntry", "/launch-entries"
		).put(
			"LaunchSet", "/launch-sets"
		).put(
			"MCPServerDataMask", "/mcp/server-data-masks"
		).put(
			"MCPServerProfile", "/mcp/server-profiles"
		).put(
			"MCPServerProfileDataMask", "/mcp/server-profile-data-masks"
		).put(
			"MCPServerPrompt", "/mcp/server-prompts"
		).put(
			"NecessaryCookieEntry", "/necessary-cookies-entries"
		).put(
			"PerformanceCookieEntry", "/performance-cookies-entries"
		).put(
			"PersonalizationCookieEntry", "/personalization-cookies-entries"
		).put(
			"SEOStudioDomain", "/seo-studio/domains"
		).put(
			"SEOStudioInstance", "/seo-studio/instances"
		).put(
			"SEOStudioScan", "/seo-studio/scans"
		).put(
			"SEOStudioScanInsight", "/seo-studio/scan-insights"
		).build();

}