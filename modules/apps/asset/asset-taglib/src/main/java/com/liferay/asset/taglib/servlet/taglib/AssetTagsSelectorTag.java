/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.asset.taglib.internal.item.selector.ItemSelectorUtil;
import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Pol
 */
public class AssetTagsSelectorTag extends IncludeTag {

	public String getAddCallback() {
		return _addCallback;
	}

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getHiddenInput() {
		return _hiddenInput;
	}

	public String getRemoveCallback() {
		return _removeCallback;
	}

	public boolean isAllowAddEntry() {
		return _allowAddEntry;
	}

	public boolean isAutoFocus() {
		return _autoFocus;
	}

	public boolean isIgnoreRequestValue() {
		return _ignoreRequestValue;
	}

	public boolean isShowSelectButton() {
		return _showSelectButton;
	}

	public void setAddCallback(String addCallback) {
		_addCallback = addCallback;
	}

	public void setAllowAddEntry(boolean allowAddEntry) {
		_allowAddEntry = allowAddEntry;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;
	}

	public void setHiddenInput(String hiddenInput) {
		_hiddenInput = hiddenInput;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRemoveCallback(String removeCallback) {
		_removeCallback = removeCallback;
	}

	public void setShowSelectButton(boolean showSelectButton) {
		_showSelectButton = showSelectButton;
	}

	public void setTagNames(String tagNames) {
		_tagNames = tagNames;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_addCallback = null;
		_allowAddEntry = true;
		_autoFocus = false;
		_className = null;
		_classPK = 0;
		_groupIds = null;
		_hiddenInput = "assetTagNames";
		_id = null;
		_ignoreRequestValue = false;
		_namespace = null;
		_removeCallback = null;
		_showSelectButton = true;
		_tagNames = null;
	}

	protected long[] getGroupIds() {
		try {
			if (ArrayUtil.isEmpty(_groupIds)) {
				HttpServletRequest httpServletRequest = getRequest();

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						themeDisplay.getScopeGroupId());
			}

			return PortalUtil.getCurrentAndAncestorSiteGroupIds(_groupIds);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return new long[0];
		}
	}

	protected String getId() {
		if (Validator.isNotNull(_id)) {
			return _id;
		}

		String randomKey = PortalUtil.generateRandomKey(
			getRequest(), "taglib_ui_asset_tags_selector_page");

		return randomKey + StringPool.UNDERLINE;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	protected String getPortletURL(String eventName) {
		AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion =
			new AssetTagsItemSelectorCriterion();

		assetTagsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetTagsItemSelectorReturnType());
		assetTagsItemSelectorCriterion.setGroupIds(getGroupIds());
		assetTagsItemSelectorCriterion.setMultiSelection(true);

		return PortletURLBuilder.create(
			ItemSelectorUtil.getItemSelector(
			).getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(getRequest()),
				eventName, assetTagsItemSelectorCriterion
			)
		).buildString();
	}

	protected List<String> getTagNames() {
		Set<String> tagNames = new HashSet<>();

		if (Validator.isNotNull(_className) && (_classPK > 0)) {
			tagNames.addAll(
				ListUtil.toList(
					AssetTagServiceUtil.getTags(_className, _classPK),
					AssetTag.NAME_ACCESSOR));
		}

		if (!_ignoreRequestValue) {
			HttpServletRequest httpServletRequest = getRequest();

			String[] curTagsParam = httpServletRequest.getParameterValues(
				_hiddenInput);

			if (curTagsParam != null) {
				List<String> curTags = new ArrayList<>();

				for (String tags : curTagsParam) {
					Collections.addAll(curTags, tags.split(StringPool.COMMA));
				}

				tagNames.addAll(curTags);
			}
		}

		tagNames.addAll(StringUtil.split(_tagNames));

		return new ArrayList<>(tagNames);
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-asset:asset-tags-selector:data", _getData());
	}

	private Map<String, Object> _getData() {
		String randomNamespace = PortalUtil.generateRandomKey(
			getRequest(), "taglib_asset_tag_selector");

		String eventName = randomNamespace + "selectTag";

		return HashMapBuilder.<String, Object>put(
			"addCallback",
			() -> {
				if (Validator.isNotNull(_addCallback)) {
					return _getNamespace() + _addCallback;
				}

				return null;
			}
		).put(
			"eventName", eventName
		).put(
			"groupIds", getGroupIds()
		).put(
			"id", _getNamespace() + getId() + "assetTagsSelector"
		).put(
			"inputName", _getInputName()
		).put(
			"portletURL", getPortletURL(eventName)
		).put(
			"removeCallback",
			() -> {
				if (Validator.isNotNull(_removeCallback)) {
					return _getNamespace() + _removeCallback;
				}

				return null;
			}
		).put(
			"selectedItems",
			() -> {
				List<Map<String, String>> selectedItems = new ArrayList<>();

				for (String tagName : getTagNames()) {
					if (Validator.isNull(tagName)) {
						continue;
					}

					selectedItems.add(
						HashMapBuilder.put(
							"label", tagName
						).put(
							"value", tagName
						).build());
				}

				return selectedItems;
			}
		).put(
			"showSelectButton", _showSelectButton
		).build();
	}

	private String _getInputName() {
		return _getNamespace() + _hiddenInput;
	}

	private String _getNamespace() {
		if (_namespace != null) {
			return _namespace;
		}

		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			_namespace = AUIUtil.getNamespace(httpServletRequest);

			return _namespace;
		}

		_namespace = AUIUtil.getNamespace(portletRequest, portletResponse);

		return _namespace;
	}

	private static final String _PAGE = "/asset_tags_selector/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		AssetTagsSelectorTag.class);

	private String _addCallback;
	private boolean _allowAddEntry = true;
	private boolean _autoFocus;
	private String _className;
	private long _classPK;
	private long[] _groupIds;
	private String _hiddenInput = "assetTagNames";
	private String _id;
	private boolean _ignoreRequestValue;
	private String _namespace;
	private String _removeCallback;
	private boolean _showSelectButton = true;
	private String _tagNames;

}