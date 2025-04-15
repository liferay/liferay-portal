/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.DuplicateQueryRuleException;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.asset.publisher.util.AssetQueryRule;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetListPortletKeys.ASSET_LIST,
		"mvc.command.name=/asset_list/update_asset_list_entry_dynamic"
	},
	service = MVCActionCommand.class
)
public class UpdateAssetListEntryDynamicMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long assetListEntryId = ParamUtil.getLong(
			actionRequest, "assetListEntryId");

		AssetListEntry assetListEntry =
			_assetListEntryService.fetchAssetListEntry(assetListEntryId);

		if (assetListEntry == null) {
			return;
		}

		long segmentsEntryId = ParamUtil.getLong(
			actionRequest, "segmentsEntryId");

		try {
			UnicodeProperties unicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					assetListEntry.getTypeSettings(segmentsEntryId)
				).build();

			_updateQueryLogic(actionRequest, unicodeProperties);

			UnicodeProperties typeSettingsUnicodeProperties =
				PropertiesParamUtil.getProperties(
					actionRequest, "TypeSettingsProperties--");

			unicodeProperties.putAll(typeSettingsUnicodeProperties);

			_assetListEntryService.updateAssetListEntryTypeSettings(
				assetListEntryId, segmentsEntryId,
				unicodeProperties.toString());
		}
		catch (DuplicateQueryRuleException duplicateQueryRuleException) {
			if (_log.isDebugEnabled()) {
				_log.debug(duplicateQueryRuleException);
			}

			SessionErrors.add(
				actionRequest, duplicateQueryRuleException.getClass(),
				duplicateQueryRuleException);
		}
	}

	private AssetQueryRule _getQueryRule(
		ActionRequest actionRequest, int index) {

		boolean contains = ParamUtil.getBoolean(
			actionRequest, "queryContains" + index);
		boolean andOperator = ParamUtil.getBoolean(
			actionRequest, "queryAndOperator" + index);

		String name = ParamUtil.getString(actionRequest, "queryName" + index);

		String[] values = null;

		if (name.equals("assetTags")) {
			values = ParamUtil.getStringValues(
				actionRequest, "queryTagNames" + index);
		}
		else if (name.equals("keywords")) {
			StrTokenizer strTokenizer = new StrTokenizer(
				ParamUtil.getString(actionRequest, "keywords" + index));

			strTokenizer.setQuoteMatcher(StrMatcher.quoteMatcher());

			List<String> valuesList = (List<String>)strTokenizer.getTokenList();

			values = valuesList.toArray(new String[0]);
		}
		else {
			values = ParamUtil.getStringValues(
				actionRequest, "queryCategoryIds" + index);
		}

		return new AssetQueryRule(contains, andOperator, name, values);
	}

	private void _updateQueryLogic(
			ActionRequest actionRequest, UnicodeProperties unicodeProperties)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long userId = themeDisplay.getUserId();
		long groupId = themeDisplay.getSiteGroupId();

		int[] queryRulesIndexes = StringUtil.split(
			ParamUtil.getString(actionRequest, "queryLogicIndexes"), 0);

		int i = 0;

		List<AssetQueryRule> queryRules = new ArrayList<>();

		for (int queryRulesIndex : queryRulesIndexes) {
			AssetQueryRule queryRule = _getQueryRule(
				actionRequest, queryRulesIndex);

			_validateQueryRule(userId, groupId, queryRules, queryRule);

			queryRules.add(queryRule);

			unicodeProperties.put(
				"queryContains" + i, String.valueOf(queryRule.isContains()));
			unicodeProperties.put(
				"queryAndOperator" + i,
				String.valueOf(queryRule.isAndOperator()));
			unicodeProperties.put("queryName" + i, queryRule.getName());
			unicodeProperties.put(
				"queryValues" + i, StringUtil.merge(queryRule.getValues()));

			i++;
		}

		// Clear previous preferences that are now blank

		String value = unicodeProperties.getProperty("queryValues" + i);

		while (Validator.isNotNull(value)) {
			unicodeProperties.remove("queryContains" + i);
			unicodeProperties.remove("queryAndOperator" + i);
			unicodeProperties.remove("queryName" + i);
			unicodeProperties.remove("queryValues" + i);

			i++;

			value = unicodeProperties.getProperty("queryValues" + i);
		}
	}

	private void _validateQueryRule(
			long userId, long groupId, List<AssetQueryRule> queryRules,
			AssetQueryRule queryRule)
		throws Exception {

		String name = queryRule.getName();

		if (name.equals("assetTags")) {
			_assetTagLocalService.checkTags(
				userId, groupId, queryRule.getValues());
		}

		if (queryRules.contains(queryRule)) {
			throw new DuplicateQueryRuleException(
				queryRule.isContains(), queryRule.isAndOperator(),
				queryRule.getName());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateAssetListEntryDynamicMVCActionCommand.class);

	@Reference
	private AssetListEntryService _assetListEntryService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

}