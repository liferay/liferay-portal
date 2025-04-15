/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.taglib.servlet.taglib;

import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.portlet.display.template.util.PortletDisplayTemplateUtil;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.taglib.internal.security.permission.resource.DDMTemplatePermission;
import com.liferay.template.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class TemplateSelectorTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getClassName() {
		return _className;
	}

	public String getDefaultDisplayStyle() {
		return _defaultDisplayStyle;
	}

	public String getDisplayStyle() {
		DDMTemplate portletDisplayDDMTemplate = getPortletDisplayDDMTemplate();

		if (portletDisplayDDMTemplate != null) {
			return PortletDisplayTemplateUtil.getDisplayStyle(
				portletDisplayDDMTemplate.getTemplateKey());
		}

		if (Validator.isNull(_displayStyle)) {
			return getDefaultDisplayStyle();
		}

		return _displayStyle;
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId > 0) {
			return _displayStyleGroupId;
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Validator.isNotNull(_displayStyleGroupKey)) {
			Group group = GroupLocalServiceUtil.fetchGroup(
				themeDisplay.getCompanyId(), _displayStyleGroupKey);

			if (group != null) {
				return group.getGroupId();
			}
		}

		return themeDisplay.getScopeGroupId();
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		long groupId = _displayStyleGroupId;

		if (groupId <= 0) {
			HttpServletRequest httpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			groupId = themeDisplay.getScopeGroupId();
		}

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group != null) {
			return group.getGroupKey();
		}

		return null;
	}

	public List<String> getDisplayStyles() {
		return _displayStyles;
	}

	public String getRefreshURL() {
		return _refreshURL;
	}

	public boolean isShowEmptyOption() {
		return _showEmptyOption;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setDefaultDisplayStyle(String defaultDisplayStyle) {
		_defaultDisplayStyle = defaultDisplayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setDisplayStyleGroupId(long displayStyleGroupId) {
		_displayStyleGroupId = displayStyleGroupId;
	}

	public void setDisplayStyleGroupKey(String displayStyleGroupKey) {
		_displayStyleGroupKey = displayStyleGroupKey;
	}

	public void setDisplayStyles(List<String> displayStyles) {
		_displayStyles = displayStyles;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setRefreshURL(String refreshURL) {
		_refreshURL = refreshURL;
	}

	public void setShowEmptyOption(boolean showEmptyOption) {
		_showEmptyOption = showEmptyOption;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_defaultDisplayStyle = StringPool.BLANK;
		_displayStyle = null;
		_displayStyleGroupId = 0;
		_displayStyleGroupKey = null;
		_displayStyles = null;
		_refreshURL = null;
		_showEmptyOption = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	protected DDMTemplate getPortletDisplayDDMTemplate() {
		String displayStyle = _displayStyle;

		if (Validator.isNull(displayStyle)) {
			displayStyle = _defaultDisplayStyle;
		}

		return PortletDisplayTemplateUtil.getPortletDisplayTemplateDDMTemplate(
			getDisplayStyleGroupId(), PortalUtil.getClassNameId(getClassName()),
			displayStyle, true);
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(
			httpServletRequest, "templateSelectorProps",
			HashMapBuilder.<String, Object>put(
				"displayStyle",
				() -> {
					String displayStyle = getDisplayStyle();

					if (Validator.isNull(displayStyle) && isShowEmptyOption()) {
						return "default";
					}

					return displayStyle;
				}
			).put(
				"displayStyleGroupId",
				() -> {
					DDMTemplate portletDisplayDDMTemplate =
						getPortletDisplayDDMTemplate();

					if (portletDisplayDDMTemplate != null) {
						return portletDisplayDDMTemplate.getGroupId();
					}

					return getDisplayStyleGroupId();
				}
			).put(
				"displayStyleGroupKey",
				() -> {
					DDMTemplate portletDisplayDDMTemplate =
						getPortletDisplayDDMTemplate();

					if (portletDisplayDDMTemplate != null) {
						Group group = GroupLocalServiceUtil.fetchGroup(
							portletDisplayDDMTemplate.getGroupId());

						if (group != null) {
							return group.getGroupKey();
						}
					}

					return getDisplayStyleGroupKey();
				}
			).put(
				"items", _getItemsJSONArray(httpServletRequest)
			).build());
	}

	private JSONArray _getDDMTemplatesJSONArray(
		long groupId, Locale locale, PermissionChecker permissionChecker) {

		JSONArray ddmTemplatesJSONArray = JSONFactoryUtil.createJSONArray();

		List<DDMTemplate> ddmTemplates = ListUtil.sort(
			DDMTemplateLocalServiceUtil.getTemplates(
				groupId, PortalUtil.getClassNameId(getClassName()), 0L),
			Comparator.comparing(
				ddmTemplate -> ddmTemplate.getName(locale),
				String::compareToIgnoreCase));

		for (DDMTemplate ddmTemplate : ddmTemplates) {
			try {
				if (!DDMTemplatePermission.contains(
						permissionChecker, ddmTemplate.getTemplateId(),
						ActionKeys.VIEW) ||
					!DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY.equals(
						ddmTemplate.getType())) {

					continue;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				continue;
			}

			ddmTemplatesJSONArray.put(
				JSONUtil.put(
					"groupId", ddmTemplate.getGroupId()
				).put(
					"groupKey",
					() -> {
						Group group = GroupLocalServiceUtil.fetchGroup(
							ddmTemplate.getGroupId());

						if (group != null) {
							return group.getGroupKey();
						}

						return null;
					}
				).put(
					"label", ddmTemplate.getName(locale)
				).put(
					"value",
					PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
						ddmTemplate.getTemplateKey()
				));
		}

		return ddmTemplatesJSONArray;
	}

	private long[] _getGroupIds(Group group) {
		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		if (group.isLayoutPrototype()) {
			LayoutPrototype layoutPrototype =
				LayoutPrototypeLocalServiceUtil.fetchLayoutPrototype(
					group.getClassPK());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchFirstLayoutPageTemplateEntry(
						layoutPrototype.getLayoutPrototypeId());

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getGroupId() > 0)) {

				return PortalUtil.getCurrentAndAncestorSiteGroupIds(
					layoutPageTemplateEntry.getGroupId());
			}
		}

		long groupId = group.getGroupId();

		if (group.isStagingGroup()) {
			Group liveGroup = group.getLiveGroup();

			if (!liveGroup.isStagedPortlet(TemplatePortletKeys.TEMPLATE)) {
				groupId = liveGroup.getGroupId();
			}
		}

		return PortalUtil.getCurrentAndAncestorSiteGroupIds(groupId);
	}

	private JSONArray _getItemsJSONArray(
		HttpServletRequest httpServletRequest) {

		JSONArray itemsJSONArray = JSONFactoryUtil.createJSONArray();

		if ((_displayStyles != null) || isShowEmptyOption()) {
			itemsJSONArray.put(
				JSONUtil.put(
					"items",
					() -> {
						JSONArray displayStylesJSONArray =
							JSONFactoryUtil.createJSONArray();

						List<String> displayStyles = new ArrayList<>();

						if (_displayStyles != null) {
							displayStyles.addAll(_displayStyles);
						}

						if (isShowEmptyOption()) {
							displayStyles.add("default");
						}

						for (String displayStyle :
								ListUtil.sort(displayStyles)) {

							displayStylesJSONArray.put(
								JSONUtil.put(
									"label",
									LanguageUtil.get(
										httpServletRequest, displayStyle)
								).put(
									"value", displayStyle
								));
						}

						return displayStylesJSONArray;
					}
				).put(
					"label", LanguageUtil.get(httpServletRequest, "default")
				));
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (long groupId : _getGroupIds(themeDisplay.getScopeGroup())) {
			Group group = GroupLocalServiceUtil.fetchGroup(groupId);

			if (group == null) {
				continue;
			}

			JSONArray ddmTempaltesJSONArray = _getDDMTemplatesJSONArray(
				groupId, themeDisplay.getLocale(),
				themeDisplay.getPermissionChecker());

			if (ddmTempaltesJSONArray.length() <= 0) {
				continue;
			}

			try {
				itemsJSONArray.put(
					JSONUtil.put(
						"items", ddmTempaltesJSONArray
					).put(
						"label",
						group.getDescriptiveName(themeDisplay.getLocale())
					));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return itemsJSONArray;
	}

	private static final String _ATTRIBUTE_NAMESPACE =
		"liferay-template:template-selector:";

	private static final String _PAGE = "/template_selector/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		TemplateSelectorTag.class);

	private String _className;
	private String _defaultDisplayStyle = StringPool.BLANK;
	private String _displayStyle;
	private long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private List<String> _displayStyles;
	private String _refreshURL;
	private boolean _showEmptyOption;

}