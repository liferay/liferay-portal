/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.display.context;

import com.liferay.exportimport.constants.ExportImportBackgroundTaskContextMapConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutRevisionLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LongWrapper;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Péter Alius
 * @author Zoltan Csaszi
 */
public class ProcessSummaryDisplayContext {

	public String getAssetTitle(
		Map<String, ?> taskContextMap, Portlet portlet) {

		if (!Objects.equals(
				portlet.getPortletId(), _PORTLET_ID_JOURNAL_PORTLET)) {

			return null;
		}

		Map<String, LongWrapper> modelAdditionCounters =
			(Map<String, LongWrapper>)taskContextMap.get(
				ExportImportBackgroundTaskContextMapConstants.
					MODEL_ADDITION_COUNTERS);

		LongWrapper modelAdditionCounter = modelAdditionCounters.get(
			_CLASS_NAME_JOURNAL_ARTICLE);

		Map<String, LongWrapper> modelDeletionCounters =
			(Map<String, LongWrapper>)taskContextMap.get(
				ExportImportBackgroundTaskContextMapConstants.
					MODEL_DELETION_COUNTERS);

		LongWrapper modelDeletionCounter = modelDeletionCounters.get(
			_CLASS_NAME_JOURNAL_ARTICLE);

		if ((modelAdditionCounter != null) && (modelDeletionCounter != null)) {
			long total =
				modelAdditionCounter.getValue() +
					modelDeletionCounter.getValue();

			if (total > 1) {
				return null;
			}
		}

		Map<String, String> assetTitles =
			(Map<String, String>)taskContextMap.get(
				ExportImportBackgroundTaskContextMapConstants.ASSET_TITLES);

		if ((assetTitles != null) &&
			Validator.isNotNull(assetTitles.get(_CLASS_NAME_JOURNAL_ARTICLE))) {

			return assetTitles.get(_CLASS_NAME_JOURNAL_ARTICLE);
		}

		return null;
	}

	public List<String> getPageNames(
		long groupId, boolean privateLayout, long[] selectedLayoutIds,
		String languageId) {

		Set<String> pageNames = new LinkedHashSet<>();

		Arrays.sort(selectedLayoutIds);

		for (long selectedLayoutId : selectedLayoutIds) {
			_addPageNames(
				groupId, privateLayout, selectedLayoutIds, selectedLayoutId,
				pageNames, languageId);
		}

		return new ArrayList<>(pageNames);
	}

	public String getPagesDescription(
		long groupId, Locale locale, boolean settingsMapPrivateLayout) {

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if ((group != null) && !group.isPrivateLayoutsEnabled()) {
			return LanguageUtil.get(locale, "pages");
		}

		if (settingsMapPrivateLayout) {
			return LanguageUtil.get(locale, "private-pages");
		}

		return LanguageUtil.get(locale, "public-pages");
	}

	public long[] getSelectedLayoutIds(
		Map<String, Serializable> exportImportConfigurationSettingsMap) {

		long[] layoutIds = GetterUtil.getLongValues(
			exportImportConfigurationSettingsMap.get("layoutIds"));

		if ((layoutIds != null) && (layoutIds.length > 0)) {
			return layoutIds;
		}

		Map<Long, Boolean> layoutIdMap =
			(Map<Long, Boolean>)exportImportConfigurationSettingsMap.get(
				"layoutIdMap");

		try {
			layoutIds = ExportImportHelperUtil.getLayoutIds(layoutIdMap);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return layoutIds;
	}

	private void _addPageNames(
		long groupId, boolean privateLayout, long[] selectedLayoutIds,
		long selectedLayoutId, Set<String> pageNames, String languageId) {

		if (!ArrayUtil.contains(selectedLayoutIds, selectedLayoutId)) {
			return;
		}

		Layout layout = LayoutLocalServiceUtil.fetchLayout(
			groupId, privateLayout, selectedLayoutId);

		if ((layout == null) ||
			(LayoutStagingUtil.isBranchingLayout(layout) &&
			 !_hasApprovedLayoutRevision(layout))) {

			return;
		}

		StringBuilder sb = new StringBuilder(layout.getName(languageId));

		while (layout.getParentLayoutId() !=
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {

			try {
				layout = LayoutLocalServiceUtil.getParentLayout(layout);

				_addPageNames(
					groupId, privateLayout, selectedLayoutIds,
					layout.getLayoutId(), pageNames, languageId);

				sb.insert(
					0, layout.getName(languageId) + StringPool.FORWARD_SLASH);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(portalException);
				}
			}
		}

		pageNames.add(sb.toString());
	}

	private List<String> _getChildPageNames(
		String basePageName, JSONObject childLayoutsJSONObject) {

		List<String> pageNames = new ArrayList<>();

		JSONArray childLayoutsJSONArray = childLayoutsJSONObject.getJSONArray(
			"layouts");

		for (int i = 0; i < childLayoutsJSONArray.length(); ++i) {
			JSONObject childLayoutJSONObject =
				childLayoutsJSONArray.getJSONObject(i);

			String childPageName =
				basePageName + StringPool.FORWARD_SLASH +
					childLayoutJSONObject.getString("name");

			pageNames.add(childPageName);

			if (!childLayoutJSONObject.getBoolean("hasChildren")) {
				continue;
			}

			pageNames.addAll(
				_getChildPageNames(
					childPageName,
					childLayoutJSONObject.getJSONObject("children")));
		}

		return pageNames;
	}

	private boolean _hasApprovedLayoutRevision(Layout layout) {
		LayoutSetBranch layoutSetBranch = LayoutStagingUtil.getLayoutSetBranch(
			LayoutSetLocalServiceUtil.fetchLayoutSet(
				layout.getGroupId(), layout.isPrivateLayout()));

		List<LayoutRevision> approvedLayoutRevisions =
			LayoutRevisionLocalServiceUtil.getLayoutRevisions(
				layoutSetBranch.getLayoutSetBranchId(), layout.getPlid(),
				WorkflowConstants.STATUS_APPROVED);

		return !approvedLayoutRevisions.isEmpty();
	}

	private static final String _CLASS_NAME_JOURNAL_ARTICLE =
		"com.liferay.journal.model.JournalArticle";

	private static final String _PORTLET_ID_JOURNAL_PORTLET =
		"com_liferay_journal_web_portlet_JournalPortlet";

	private static final Log _log = LogFactoryUtil.getLog(
		ProcessSummaryDisplayContext.class);

}