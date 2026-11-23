/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v1_3_0.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lino Alves
 */
public class WorkflowContextUpgradeUtil {

	public static Set<Map.Entry<String, String>>
		getRenamedClassNamesEntrySet() {

		return _classNamesMap.entrySet();
	}

	public static boolean isEntryClassNameRenamed(
		Map<String, Serializable> workflowContext) {

		String oldEntryClassName = (String)workflowContext.get(
			"entryClassName");

		if (_classNamesMap.get(oldEntryClassName) != null) {
			return true;
		}

		return false;
	}

	public static Map<String, Serializable> renameEntryClassName(
		Map<String, Serializable> workflowContext) {

		Map<String, Serializable> newWorkflowContext = new HashMap<>(
			workflowContext);

		String oldEntryClassName = (String)workflowContext.get(
			"entryClassName");

		String newEntryClassName = _classNamesMap.get(oldEntryClassName);

		if (newEntryClassName != null) {
			newWorkflowContext.put("entryClassName", newEntryClassName);
		}

		return newWorkflowContext;
	}

	public static String renamePortalClassNames(String workflowContextJSON) {
		Matcher matcher = _javaClassPattern.matcher(workflowContextJSON);

		Set<String> oldSubs = new TreeSet<>();
		Set<String> newSubs = new TreeSet<>();

		while (matcher.find()) {
			String oldPortalJavaClassName = matcher.group(1);

			if (oldPortalJavaClassName.contains(".impl") ||
				oldPortalJavaClassName.contains(".kernel") ||
				oldSubs.contains(oldPortalJavaClassName)) {

				continue;
			}

			oldSubs.add("\"javaClass\":\"" + oldPortalJavaClassName + "\"");

			String newPortalJavaClassName = StringUtil.replace(
				oldPortalJavaClassName, "com.liferay.portal",
				"com.liferay.portal.kernel");

			newSubs.add("\"javaClass\":\"" + newPortalJavaClassName + "\"");

			if (_log.isDebugEnabled()) {
				_log.debug(
					String.format(
						"Workflow context Java class name renamed from " +
							"\"%s\" to \"%s\"",
						oldPortalJavaClassName, newPortalJavaClassName));
			}
		}

		if (oldSubs.isEmpty()) {
			return workflowContextJSON;
		}

		return StringUtil.replace(
			workflowContextJSON, ArrayUtil.toStringArray(oldSubs),
			ArrayUtil.toStringArray(newSubs));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowContextUpgradeUtil.class);

	private static final Map<String, String> _classNamesMap =
		HashMapBuilder.put(
			"com.liferay.portal.model.Company",
			"com.liferay.portal.kernel.model.Company"
		).put(
			"com.liferay.portal.model.Group",
			"com.liferay.portal.kernel.model.Group"
		).put(
			"com.liferay.portal.model.LayoutRevision",
			"com.liferay.portal.kernel.model.LayoutRevision"
		).put(
			"com.liferay.portal.model.Role",
			"com.liferay.portal.kernel.model.Role"
		).put(
			"com.liferay.portal.model.User",
			"com.liferay.portal.kernel.model.User"
		).put(
			"com.liferay.portal.model.UserGroup",
			"com.liferay.portal.kernel.model.UserGroup"
		).put(
			"com.liferay.portlet.blogs.model.BlogsEntry",
			"com.liferay.blogs.kernel.model.BlogsEntry"
		).put(
			"com.liferay.portlet.documentlibrary.model.DLFileEntry",
			"com.liferay.document.library.kernel.model.DLFileEntry"
		).put(
			"com.liferay.portlet.dynamicdatalists.model.DDLRecord",
			"com.liferay.dynamic.data.lists.model.DDLRecord"
		).put(
			"com.liferay.portlet.journal.model.JournalArticle",
			"com.liferay.journal.model.JournalArticle"
		).put(
			"com.liferay.portlet.messageboards.model.MBDiscussion",
			"com.liferay.message.boards.kernel.model.MBDiscussion"
		).put(
			"com.liferay.portlet.messageboards.model.MBMessage",
			"com.liferay.message.boards.kernel.model.MBMessage"
		).put(
			"com.liferay.portlet.wiki.model.WikiPage",
			"com.liferay.wiki.model.WikiPage"
		).build();
	private static final Pattern _javaClassPattern = Pattern.compile(
		"\"javaClass\":\"(com.liferay.portal.[^\"]+)\"");

}