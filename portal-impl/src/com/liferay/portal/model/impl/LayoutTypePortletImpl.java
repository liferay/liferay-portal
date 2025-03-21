/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CustomizedPages;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.Plugin;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.PortletWrapper;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil;
import com.liferay.portal.kernel.service.PluginSettingLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LayoutTypePortletFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.util.JS;

import jakarta.portlet.PortletPreferences;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Berentey Zsolt
 * @author Jorge Ferrer
 * @author Raymond Augé
 * @author Neil Griffin
 */
public class LayoutTypePortletImpl
	extends LayoutTypeImpl implements LayoutTypePortlet {

	public LayoutTypePortletImpl(
		Layout layout, LayoutTypeController layoutTypeController,
		LayoutTypeAccessPolicy layoutTypeAccessPolicy) {

		super(layout, layoutTypeController, layoutTypeAccessPolicy);
	}

	@Override
	public void addModeAboutPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModeAbout(StringUtil.add(getModeAbout(), portletId));
	}

	@Override
	public void addModeConfigPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModeConfig(StringUtil.add(getModeConfig(), portletId));
	}

	@Override
	public void addModeCustomPortletId(String portletId, String portletMode) {
		removeModesPortletId(portletId);
		setModeCustom(
			StringUtil.add(getModeCustom(portletMode), portletId), portletMode);
		_addedCustomPortletMode = portletMode;
	}

	@Override
	public void addModeEditDefaultsPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModeEditDefaults(StringUtil.add(getModeEditDefaults(), portletId));
	}

	@Override
	public void addModeEditGuestPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModeEditGuest(StringUtil.add(getModeEditGuest(), portletId));
	}

	@Override
	public void addModeEditPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModeEdit(StringUtil.add(getModeEdit(), portletId));
	}

	@Override
	public void addModeHelpPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModeHelp(StringUtil.add(getModeHelp(), portletId));
	}

	@Override
	public void addModePreviewPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModePreview(StringUtil.add(getModePreview(), portletId));
	}

	@Override
	public void addModePrintPortletId(String portletId) {
		removeModesPortletId(portletId);
		setModePrint(StringUtil.add(getModePrint(), portletId));
	}

	@Override
	public String addPortletId(long userId, String portletId) {
		return addPortletId(userId, portletId, true);
	}

	@Override
	public String addPortletId(
		long userId, String portletId, boolean checkPermission) {

		return addPortletId(userId, portletId, null, -1, checkPermission);
	}

	@Override
	public String addPortletId(
		long userId, String portletId, String columnId, int columnPos) {

		return addPortletId(userId, portletId, columnId, columnPos, true);
	}

	@Override
	public String addPortletId(
		long userId, String portletId, String columnId, int columnPos,
		boolean checkPermission) {

		return addPortletId(
			userId, portletId, columnId, columnPos, checkPermission, false);
	}

	@Override
	public void addPortletIds(
		long userId, String[] portletIds, boolean checkPermission) {

		for (String portletId : portletIds) {
			addPortletId(userId, portletId, checkPermission);
		}
	}

	@Override
	public void addPortletIds(
		long userId, String[] portletIds, String columnId,
		boolean checkPermission) {

		for (String portletId : portletIds) {
			addPortletId(userId, portletId, columnId, -1, checkPermission);
		}
	}

	@Override
	public void addStateMaxPortletId(String portletId) {
		removeStatesPortletId(portletId);
		//setStateMax(StringUtil.add(getStateMax(), portletId));
		setStateMax(StringUtil.add(StringPool.BLANK, portletId));
	}

	@Override
	public void addStateMinPortletId(String portletId) {
		removeStateMaxPortletId(portletId);
		setStateMin(StringUtil.add(getStateMin(), portletId));
	}

	@Override
	public List<Portlet> addStaticPortlets(
		List<Portlet> portlets, List<Portlet> startPortlets,
		List<Portlet> endPortlets) {

		// Return the original array of portlets if no static portlets are
		// specified

		if (startPortlets == null) {
			startPortlets = Collections.emptyList();
		}

		if (endPortlets == null) {
			endPortlets = Collections.emptyList();
		}

		if (startPortlets.isEmpty() && endPortlets.isEmpty()) {
			return portlets;
		}

		// New array of portlets that contain the static portlets

		List<Portlet> list = new ArrayList<>(
			portlets.size() + startPortlets.size() + endPortlets.size());

		if (!startPortlets.isEmpty()) {
			list.addAll(startPortlets);
		}

		for (Portlet portlet : portlets) {

			// Add the portlet if and only if it is not also a static portlet

			if (!startPortlets.contains(portlet) &&
				!endPortlets.contains(portlet)) {

				list.add(portlet);
			}
		}

		if (!endPortlets.isEmpty()) {
			list.addAll(endPortlets);
		}

		return list;
	}

	@Override
	public String getAddedCustomPortletMode() {
		return _addedCustomPortletMode;
	}

	@Override
	public List<Portlet> getAllNonembeddedPortlets() {
		List<Portlet> staticPortlets = getStaticPortlets(
			PropsKeys.LAYOUT_STATIC_PORTLETS_ALL);

		List<Portlet> explicitlyAddedPortlets = new ArrayList<>();

		Layout layout = getLayout();

		if (layout.isTypeAssetDisplay() || layout.isTypeContent()) {
			List<com.liferay.portal.kernel.model.PortletPreferences>
				portletPreferencesList =
					PortletPreferencesLocalServiceUtil.
						getPortletPreferencesByPlid(layout.getPlid());

			for (com.liferay.portal.kernel.model.PortletPreferences
					portletPreferences : portletPreferencesList) {

				Portlet portlet = PortletLocalServiceUtil.getPortletById(
					layout.getCompanyId(), portletPreferences.getPortletId());

				if (portlet != null) {
					explicitlyAddedPortlets.add(portlet);
				}
			}
		}
		else {
			explicitlyAddedPortlets = getExplicitlyAddedPortlets(false);
		}

		return addStaticPortlets(explicitlyAddedPortlets, staticPortlets, null);
	}

	@Override
	public List<Portlet> getAllPortlets() {
		return addStaticPortlets(
			getExplicitlyAddedPortlets(),
			getStaticPortlets(PropsKeys.LAYOUT_STATIC_PORTLETS_ALL),
			getEmbeddedPortlets());
	}

	@Override
	public List<Portlet> getAllPortlets(boolean includeSystem) {
		List<Portlet> portlets = getAllPortlets();

		if (includeSystem) {
			return portlets;
		}

		return TransformUtil.transform(
			portlets,
			portlet -> {
				if (portlet.isSystem() && !includeSystem) {
					return null;
				}

				return portlet;
			});
	}

	@Override
	public List<Portlet> getAllPortlets(String columnId) {
		String[] portletIds = StringUtil.split(getColumnValue(columnId));

		List<Portlet> portlets = new ArrayList<>(portletIds.length);

		for (String portletId : portletIds) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				getCompanyId(), portletId);

			if (portlet != null) {
				portlets.add(portlet);
			}
		}

		List<Portlet> startPortlets = getStaticPortlets(
			PropsKeys.LAYOUT_STATIC_PORTLETS_START + columnId);

		List<Portlet> endPortlets = getStaticPortlets(
			PropsKeys.LAYOUT_STATIC_PORTLETS_END + columnId);

		return addStaticPortlets(portlets, startPortlets, endPortlets);
	}

	@Override
	public String getColumn(String portletId) {
		String portletIdColumnId = StringPool.BLANK;

		List<String> columnIds = getColumns();

		for (String columnId : columnIds) {
			String[] portletIds = StringUtil.split(getColumnValue(columnId));

			for (String columnPortletId : portletIds) {
				if (columnPortletId.equals(portletId)) {
					return columnId;
				}

				if (Validator.isNull(portletIdColumnId) &&
					Objects.equals(
						PortletIdCodec.decodePortletName(columnPortletId),
						PortletIdCodec.decodePortletName(portletId))) {

					portletIdColumnId = columnId;
				}
			}
		}

		return portletIdColumnId;
	}

	@Override
	public List<String> getColumns() {
		List<String> columns = new ArrayList<>();

		Layout layout = getLayout();

		if (layout.isTypePanel()) {
			columns.add("panelSelectedPortlets");
		}
		else if (layout.isTypePortlet()) {
			if (Objects.equals(
					layout.getType(),
					LayoutConstants.TYPE_FULL_PAGE_APPLICATION)) {

				columns.add("fullPageApplicationPortlet");
			}
			else {
				LayoutTemplate layoutTemplate = getLayoutTemplate();

				columns.addAll(layoutTemplate.getColumns());

				Collections.addAll(columns, getNestedColumns());
			}
		}

		return columns;
	}

	@Override
	public List<Portlet> getEmbeddedPortlets() {
		Layout layout = getLayout();

		return layout.getEmbeddedPortlets();
	}

	@Override
	public List<Portlet> getExplicitlyAddedPortlets() {
		return getExplicitlyAddedPortlets(true);
	}

	@Override
	public List<Portlet> getExplicitlyAddedPortlets(
		boolean includeCustomizableColumns) {

		List<Portlet> portlets = new ArrayList<>();

		List<String> columns = getColumns();

		for (String columnId : columns) {
			if (!includeCustomizableColumns) {
				String customizableString = getTypeSettingsProperty(
					CustomizedPages.namespaceColumnId(columnId));

				boolean customizable = GetterUtil.getBoolean(
					customizableString);

				if (customizable && !isLayoutSetPrototype()) {
					continue;
				}
			}

			portlets.addAll(getAllPortlets(columnId));
		}

		return portlets;
	}

	@Override
	public Layout getLayoutSetPrototypeLayout() {
		if (_layoutSetPrototypeLayout == null) {
			Layout layout = getLayout();

			_layoutSetPrototypeLayout = layout.getLayoutSetPrototypeLayout();

			if (_layoutSetPrototypeLayout == null) {
				_layoutSetPrototypeLayout = _nullLayout;
			}
		}

		if (_layoutSetPrototypeLayout == _nullLayout) {
			return null;
		}

		return _layoutSetPrototypeLayout;
	}

	@Override
	public String getLayoutSetPrototypeLayoutProperty(String key) {
		Layout layoutSetPrototypeLayout = getLayoutSetPrototypeLayout();

		if (layoutSetPrototypeLayout == null) {
			return StringPool.BLANK;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			layoutSetPrototypeLayout.getTypeSettingsProperties();

		return typeSettingsUnicodeProperties.getProperty(key);
	}

	@Override
	public LayoutTemplate getLayoutTemplate() {
		LayoutTemplate layoutTemplate =
			LayoutTemplateLocalServiceUtil.getLayoutTemplate(
				getLayoutTemplateId(), false, getThemeId());

		if (layoutTemplate == null) {
			layoutTemplate = new LayoutTemplateImpl(
				StringPool.BLANK, StringPool.BLANK);

			List<String> columns = new ArrayList<>(10);

			for (int i = 1; i <= 10; i++) {
				columns.add(LayoutTypePortletConstants.COLUMN_PREFIX + i);
			}

			layoutTemplate.setColumns(columns);
		}

		return layoutTemplate;
	}

	@Override
	public String getLayoutTemplateId() {
		return GetterUtil.getString(
			getTypeSettingsProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID));
	}

	@Override
	public String getModeAbout() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.MODE_ABOUT);
	}

	@Override
	public String getModeConfig() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.MODE_CONFIG);
	}

	@Override
	public String getModeCustom(String portletMode) {
		return getTypeSettingsProperty("mode-" + portletMode);
	}

	@Override
	public String getModeEdit() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.MODE_EDIT);
	}

	@Override
	public String getModeEditDefaults() {
		return getTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_EDIT_DEFAULTS);
	}

	@Override
	public String getModeEditGuest() {
		return getTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_EDIT_GUEST);
	}

	@Override
	public String getModeHelp() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.MODE_HELP);
	}

	@Override
	public String getModePreview() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.MODE_PREVIEW);
	}

	@Override
	public String getModePrint() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.MODE_PRINT);
	}

	@Override
	public int getNumOfColumns() {
		LayoutTemplate layoutTemplate = getLayoutTemplate();

		List<String> columns = layoutTemplate.getColumns();

		return columns.size();
	}

	@Override
	public PortalPreferences getPortalPreferences() {
		return _portalPreferences;
	}

	@Override
	public List<String> getPortletIds() {
		List<String> portletIds = new ArrayList<>();

		for (String column : getColumns()) {
			Collections.addAll(
				portletIds, StringUtil.split(getColumnValue(column)));
		}

		return portletIds;
	}

	@Override
	public List<Portlet> getPortlets() {
		List<String> portletIds = getPortletIds();

		List<Portlet> portlets = new ArrayList<>(portletIds.size());

		for (String portletId : portletIds) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				getCompanyId(), portletId);

			if (portlet != null) {
				portlets.add(portlet);
			}
		}

		return portlets;
	}

	@Override
	public String getStateMax() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.STATE_MAX);
	}

	@Override
	public String getStateMaxPortletId() {
		String[] stateMax = StringUtil.split(getStateMax());

		if (stateMax.length > 0) {
			return stateMax[0];
		}

		return StringPool.BLANK;
	}

	@Override
	public String getStateMin() {
		return getTypeSettingsProperty(LayoutTypePortletConstants.STATE_MIN);
	}

	@Override
	public List<Portlet> getStaticPortlets(String position) {
		String[] portletIds = getStaticPortletIds(position);

		List<Portlet> portlets = new ArrayList<>();

		for (String portletId : portletIds) {
			if (Validator.isNull(portletId) ||
				hasNonstaticPortletId(portletId)) {

				continue;
			}

			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				getCompanyId(), portletId);

			if (portlet == null) {
				continue;
			}

			Portlet staticPortlet = portlet;

			if (portlet.isInstanceable()) {

				// Instanceable portlets do not need to be cloned because they
				// are already cloned. See the method getPortletById in the
				// class PortletLocalServiceImpl and how it references the
				// method getClonedInstance in the class PortletImpl.

			}
			else {
				staticPortlet = new PortletWrapper(portlet) {

					@Override
					public boolean getStatic() {
						return _staticPortlet;
					}

					@Override
					public boolean getStaticStart() {
						return _staticPortletStart;
					}

					@Override
					public boolean isStatic() {
						return _staticPortlet;
					}

					@Override
					public boolean isStaticStart() {
						return _staticPortletStart;
					}

					@Override
					public void setStatic(boolean staticPortlet) {
						_staticPortlet = staticPortlet;
					}

					@Override
					public void setStaticStart(boolean staticPortletStart) {
						_staticPortletStart = staticPortletStart;
					}

					private boolean _staticPortlet;
					private boolean _staticPortletStart;

				};
			}

			staticPortlet.setStatic(true);

			if (position.startsWith("layout.static.portlets.start")) {
				staticPortlet.setStaticStart(true);
			}

			portlets.add(staticPortlet);
		}

		return portlets;
	}

	@Override
	public boolean hasDefaultScopePortletId(long groupId, String portletId) {
		if (hasPortletId(portletId)) {
			long scopeGroupId = PortalUtil.getScopeGroupId(
				getLayout(), portletId);

			if (groupId == scopeGroupId) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasModeAboutPortletId(String portletId) {
		return StringUtil.contains(getModeAbout(), portletId);
	}

	@Override
	public boolean hasModeConfigPortletId(String portletId) {
		return StringUtil.contains(getModeConfig(), portletId);
	}

	@Override
	public boolean hasModeCustomPortletId(
		String portletId, String portletMode) {

		return StringUtil.contains(getModeCustom(portletMode), portletId);
	}

	@Override
	public boolean hasModeEditDefaultsPortletId(String portletId) {
		return StringUtil.contains(getModeEditDefaults(), portletId);
	}

	@Override
	public boolean hasModeEditGuestPortletId(String portletId) {
		return StringUtil.contains(getModeEditGuest(), portletId);
	}

	@Override
	public boolean hasModeEditPortletId(String portletId) {
		return StringUtil.contains(getModeEdit(), portletId);
	}

	@Override
	public boolean hasModeHelpPortletId(String portletId) {
		return StringUtil.contains(getModeHelp(), portletId);
	}

	@Override
	public boolean hasModePreviewPortletId(String portletId) {
		return StringUtil.contains(getModePreview(), portletId);
	}

	@Override
	public boolean hasModePrintPortletId(String portletId) {
		return StringUtil.contains(getModePrint(), portletId);
	}

	@Override
	public boolean hasModeViewPortletId(String portletId) {
		if (hasModeAboutPortletId(portletId) ||
			hasModeConfigPortletId(portletId) ||
			hasModeEditPortletId(portletId) ||
			hasModeEditDefaultsPortletId(portletId) ||
			hasModeEditGuestPortletId(portletId) ||
			hasModeHelpPortletId(portletId) ||
			hasModePreviewPortletId(portletId) ||
			hasModePrintPortletId(portletId)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean hasPortletId(String portletId) {
		return hasPortletId(portletId, false);
	}

	@Override
	public boolean hasPortletId(String portletId, boolean strict) {
		List<String> columns = getColumns();

		for (String columnId : columns) {
			if (hasNonstaticPortletId(columnId, portletId) ||
				hasStaticPortletId(columnId, portletId)) {

				return true;
			}
		}

		Layout layout = getLayout();

		if (layout.isTypeControlPanel()) {
			return false;
		}

		if (isCustomizable() && isCustomizedView()) {
			LayoutTypePortletImpl defaultLayoutTypePortletImpl =
				getDefaultLayoutTypePortletImpl();

			if (defaultLayoutTypePortletImpl.hasNonstaticPortletId(portletId)) {
				return false;
			}
		}

		if (strict) {
			return false;
		}

		long count1 =
			PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId);

		if (count1 > 0) {
			return true;
		}

		long count2 =
			PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_USER, layout.getPlid(), portletId);

		if (count2 > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasStateMax() {
		String[] stateMax = StringUtil.split(getStateMax());

		if (stateMax.length > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasStateMaxPortletId(String portletId) {
		if (StringUtil.contains(getStateMax(), portletId)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasStateMin() {
		String[] stateMin = StringUtil.split(getStateMin());

		if (stateMin.length > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasStateMinPortletId(String portletId) {
		if (StringUtil.contains(getStateMin(), portletId)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasStateNormalPortletId(String portletId) {
		if (hasStateMaxPortletId(portletId) ||
			hasStateMinPortletId(portletId)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean hasUpdatePermission() {
		return _updatePermission;
	}

	@Override
	public boolean isCacheable() {
		List<Portlet> portlets = new ArrayList<>();

		for (String columnId : getColumns()) {
			List<Portlet> columnPortlets = getAllPortlets(columnId);

			for (Portlet portlet : columnPortlets) {
				Portlet rootPortlet = portlet.getRootPortlet();

				if (!rootPortlet.isLayoutCacheable()) {
					return false;
				}
			}

			portlets.addAll(columnPortlets);
		}

		List<Portlet> staticPortlets = getStaticPortlets(
			PropsKeys.LAYOUT_STATIC_PORTLETS_ALL);

		for (Portlet portlet : staticPortlets) {
			Portlet rootPortlet = portlet.getRootPortlet();

			if (!rootPortlet.isLayoutCacheable()) {
				return false;
			}
		}

		List<Portlet> embeddedPortlets = getEmbeddedPortlets();

		for (Portlet portlet : embeddedPortlets) {
			Portlet rootPortlet = portlet.getRootPortlet();

			if (!rootPortlet.isLayoutCacheable()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isColumnCustomizable(String columnId) {
		String customizableString = getTypeSettingsProperty(
			CustomizedPages.namespaceColumnId(columnId));

		boolean customizable = GetterUtil.getBoolean(customizableString);

		if (customizable) {
			if (isLayoutSetPrototype()) {
				return false;
			}

			return true;
		}

		if (hasUserPreferences()) {
			String columnValue = _portalPreferences.getValue(
				CustomizedPages.namespacePlid(getPlid()), columnId,
				StringPool.NULL);

			if (!Objects.equals(columnValue, StringPool.NULL)) {
				setUserPreference(columnId, null);
			}
		}

		return false;
	}

	@Override
	public boolean isColumnDisabled(String columnId) {
		if (columnId.startsWith(_NESTED_PORTLETS_NAMESPACE)) {
			return false;
		}

		if ((isCustomizedView() && !isColumnCustomizable(columnId)) ||
			(!isCustomizedView() && !hasUpdatePermission())) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isCustomizable() {
		return GetterUtil.getBoolean(
			getTypeSettingsProperty(LayoutConstants.CUSTOMIZABLE_LAYOUT));
	}

	@Override
	public boolean isCustomizedView() {
		return _customizedView;
	}

	@Override
	public boolean isDefaultUpdated() {
		if (!isCustomizedView() || !hasUserPreferences()) {
			return false;
		}

		String preferencesModifiedDateString = _portalPreferences.getValue(
			CustomizedPages.namespacePlid(getPlid()), _MODIFIED_DATE,
			_NULL_DATE);

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			PropsValues.INDEX_DATE_FORMAT_PATTERN);

		try {
			Date preferencesModifiedDate = dateFormat.parse(
				preferencesModifiedDateString);

			Layout layout = getLayout();

			String propertiesModifiedDateString =
				layout.getTypeSettingsProperty(_MODIFIED_DATE, _NULL_DATE);

			Date propertiesModifiedDate = dateFormat.parse(
				propertiesModifiedDateString);

			return propertiesModifiedDate.after(preferencesModifiedDate);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Override
	public boolean isPortletCustomizable(String portletId) {
		return isColumnCustomizable(getColumn(portletId));
	}

	@Override
	public boolean isPortletEmbedded(String portletId) {
		Layout layout = getLayout();

		return layout.isPortletEmbedded(portletId, layout.getGroupId());
	}

	@Override
	public void movePortletId(
		long userId, String portletId, String columnId, int columnPos) {

		if (!hasPortletId(portletId)) {
			return;
		}

		_enablePortletLayoutListener = false;

		try {
			removePortletId(userId, portletId, false);
			addPortletId(userId, portletId, columnId, columnPos, false, true);
		}
		finally {
			_enablePortletLayoutListener = true;
		}

		Layout layout = getLayout();

		try {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				layout.getCompanyId(), portletId);

			if (portlet != null) {
				PortletLayoutListener portletLayoutListener =
					portlet.getPortletLayoutListenerInstance();

				if (portletLayoutListener != null) {
					portletLayoutListener.onMoveInLayout(
						portletId, layout.getPlid());
				}
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to fire portlet layout listener event", exception);
		}
	}

	@Override
	public void removeCustomization(
		UnicodeProperties typeSettingsUnicodeProperties) {

		for (String columnId : getColumns()) {
			if (!isColumnCustomizable(columnId)) {
				continue;
			}

			if (GetterUtil.getBoolean(
					getTypeSettingsProperty(
						CustomizedPages.namespaceColumnId(columnId)))) {

				typeSettingsUnicodeProperties.remove(
					CustomizedPages.namespaceColumnId(columnId));
			}
		}
	}

	@Override
	public void removeModeAboutPortletId(String portletId) {
		setModeAbout(StringUtil.removeFromList(getModeAbout(), portletId));
	}

	@Override
	public void removeModeConfigPortletId(String portletId) {
		setModeConfig(StringUtil.removeFromList(getModeConfig(), portletId));
	}

	@Override
	public void removeModeEditDefaultsPortletId(String portletId) {
		setModeEditDefaults(
			StringUtil.removeFromList(getModeEditDefaults(), portletId));
	}

	@Override
	public void removeModeEditGuestPortletId(String portletId) {
		setModeEditGuest(
			StringUtil.removeFromList(getModeEditGuest(), portletId));
	}

	@Override
	public void removeModeEditPortletId(String portletId) {
		setModeEdit(StringUtil.removeFromList(getModeEdit(), portletId));
	}

	@Override
	public void removeModeHelpPortletId(String portletId) {
		setModeHelp(StringUtil.removeFromList(getModeHelp(), portletId));
	}

	@Override
	public void removeModePreviewPortletId(String portletId) {
		setModePreview(StringUtil.removeFromList(getModePreview(), portletId));
	}

	@Override
	public void removeModePrintPortletId(String portletId) {
		setModePrint(StringUtil.removeFromList(getModePrint(), portletId));
	}

	@Override
	public void removeModesPortletId(String portletId) {
		removeModeAboutPortletId(portletId);
		removeModeConfigPortletId(portletId);
		removeModeEditPortletId(portletId);
		removeModeEditDefaultsPortletId(portletId);
		removeModeEditGuestPortletId(portletId);
		removeModeHelpPortletId(portletId);
		removeModePreviewPortletId(portletId);
		removeModePrintPortletId(portletId);
	}

	@Override
	public void removeNestedColumns(String portletNamespace) {
		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		UnicodeProperties newTypeSettingsUnicodeProperties =
			new UnicodeProperties();

		for (Map.Entry<String, String> entry :
				typeSettingsUnicodeProperties.entrySet()) {

			String key = entry.getKey();

			if (!key.startsWith(portletNamespace)) {
				newTypeSettingsUnicodeProperties.setProperty(
					key, entry.getValue());
			}
		}

		Layout layout = getLayout();

		layout.setTypeSettingsProperties(newTypeSettingsUnicodeProperties);

		String nestedColumnIds = GetterUtil.getString(
			getTypeSettingsProperty(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS));

		String[] nestedColumnIdsArray = ArrayUtil.removeByPrefix(
			StringUtil.split(nestedColumnIds), portletNamespace);

		setTypeSettingsProperty(
			LayoutTypePortletConstants.NESTED_COLUMN_IDS,
			StringUtil.merge(nestedColumnIdsArray));
	}

	@Override
	public void removePortletId(long userId, String portletId) {
		removePortletId(userId, portletId, true);
	}

	@Override
	public void removePortletId(
		long userId, String portletId, boolean cleanUp) {

		try {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				getCompanyId(), portletId);

			if (portlet == null) {
				_log.error(
					"Portlet " + portletId +
						" cannot be removed because it is not registered");

				return;
			}

			if (!LayoutPermissionUtil.contains(
					PermissionThreadLocal.getPermissionChecker(), getLayout(),
					ActionKeys.UPDATE) &&
				!isCustomizable()) {

				return;
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			return;
		}

		List<String> columns = getColumns();

		for (String columnId : columns) {
			if (isCustomizable() && isColumnDisabled(columnId)) {
				continue;
			}

			String columnValue = null;

			if (PortletIdCodec.hasInstanceId(columnId) &&
				PortletIdCodec.hasUserId(columnId)) {

				PortletPreferences portletPreferences =
					_getUserColumnPortletPreferences(columnId);

				columnValue = portletPreferences.getValue(
					columnId, StringPool.BLANK);
			}
			else if (hasUserPreferences()) {
				columnValue = getUserPreference(columnId);
			}
			else {
				columnValue = getTypeSettingsProperty(columnId);
			}

			columnValue = StringUtil.removeFromList(columnValue, portletId);

			if (StringUtil.endsWith(columnValue, StringPool.COMMA)) {
				columnValue = columnValue.substring(
					0, columnValue.length() - 1);
			}

			if (PortletIdCodec.hasInstanceId(columnId) &&
				PortletIdCodec.hasUserId(columnId)) {

				PortletPreferences portletPreferences =
					_getUserColumnPortletPreferences(columnId);

				try {
					portletPreferences.setValue(columnId, columnValue);

					portletPreferences.store();
				}
				catch (Exception exception) {
					_log.error("Unable to save portlet preferences", exception);
				}
			}
			else if (hasUserPreferences()) {
				setUserPreference(columnId, columnValue);
			}
			else {
				setTypeSettingsProperty(columnId, columnValue);
			}
		}

		if (cleanUp) {
			try {
				onRemoveFromLayout(new String[] {portletId});
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	@Override
	public void removeStateMaxPortletId(String portletId) {
		setStateMax(StringUtil.removeFromList(getStateMax(), portletId));
	}

	@Override
	public void removeStateMinPortletId(String portletId) {
		setStateMin(StringUtil.removeFromList(getStateMin(), portletId));
	}

	@Override
	public void removeStatesPortletId(String portletId) {
		removeStateMaxPortletId(portletId);
		removeStateMinPortletId(portletId);
	}

	@Override
	public void reorganizePortlets(
		List<String> newColumns, List<String> oldColumns) {

		String lastNewColumnId = newColumns.get(newColumns.size() - 1);

		String lastNewColumnValue = getTypeSettingsProperty(lastNewColumnId);

		for (String oldColumnId : oldColumns) {
			if (!newColumns.contains(oldColumnId)) {
				String oldColumnValue = getTypeSettingsProperties().remove(
					oldColumnId);

				String[] portletIds = StringUtil.split(oldColumnValue);

				for (String portletId : portletIds) {
					lastNewColumnValue = StringUtil.add(
						lastNewColumnValue, portletId);
				}
			}
		}

		setTypeSettingsProperty(lastNewColumnId, lastNewColumnValue);
	}

	@Override
	public void resetModes() {
		setModeAbout(StringPool.BLANK);
		setModeConfig(StringPool.BLANK);
		setModeEdit(StringPool.BLANK);
		setModeEditDefaults(StringPool.BLANK);
		setModeEditGuest(StringPool.BLANK);
		setModeHelp(StringPool.BLANK);
		setModePreview(StringPool.BLANK);
		setModePrint(StringPool.BLANK);
	}

	@Override
	public void resetStates() {
		setStateMax(StringPool.BLANK);
		setStateMin(StringPool.BLANK);
	}

	@Override
	public void resetUserPreferences() {
		if (!hasUserPreferences()) {
			return;
		}

		long plid = getPlid();

		Set<String> customPortletIds = new HashSet<>();

		for (String columnId : getColumns()) {
			String value = _portalPreferences.getValue(
				CustomizedPages.namespacePlid(plid), columnId);

			for (String customPortletId : StringUtil.split(value)) {
				customPortletIds.add(customPortletId);
			}
		}

		try {
			onRemoveFromLayout(customPortletIds.toArray(new String[0]));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		_portalPreferences.resetValues(CustomizedPages.namespacePlid(plid));

		_portalPreferences.setValue(
			CustomizedPages.namespacePlid(plid), _MODIFIED_DATE,
			_dateFormat.format(new Date()));
	}

	@Override
	public void setCustomizedView(boolean customizedView) {
		_customizedView = customizedView;
	}

	@Override
	public void setLayoutTemplateId(long userId, String newLayoutTemplateId) {
		setLayoutTemplateId(userId, newLayoutTemplateId, true);
	}

	@Override
	public void setLayoutTemplateId(
		long userId, String newLayoutTemplateId, boolean checkPermission) {

		if (checkPermission &&
			!PluginSettingLocalServiceUtil.hasPermission(
				userId, newLayoutTemplateId, Plugin.TYPE_LAYOUT_TEMPLATE)) {

			return;
		}

		LayoutTemplate newLayoutTemplate =
			LayoutTemplateLocalServiceUtil.getLayoutTemplate(
				newLayoutTemplateId, false, getThemeId());

		if (newLayoutTemplate == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to find layout template " + newLayoutTemplateId);
			}

			return;
		}

		List<String> newColumns = newLayoutTemplate.getColumns();

		LayoutTemplate oldLayoutTemplate = getLayoutTemplate();

		List<String> oldColumns = oldLayoutTemplate.getColumns();

		reorganizePortlets(newColumns, oldColumns);

		setTypeSettingsProperty(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, newLayoutTemplateId);
	}

	@Override
	public void setModeAbout(String modeAbout) {
		setTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_ABOUT, modeAbout);
	}

	@Override
	public void setModeConfig(String modeConfig) {
		setTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_CONFIG, modeConfig);
	}

	@Override
	public void setModeCustom(String modeCustom, String portletMode) {
		setTypeSettingsProperty("mode-" + portletMode, modeCustom);
	}

	@Override
	public void setModeEdit(String modeEdit) {
		setTypeSettingsProperty(LayoutTypePortletConstants.MODE_EDIT, modeEdit);
	}

	@Override
	public void setModeEditDefaults(String modeEditDefaults) {
		setTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_EDIT_DEFAULTS, modeEditDefaults);
	}

	@Override
	public void setModeEditGuest(String modeEditGuest) {
		setTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_EDIT_GUEST, modeEditGuest);
	}

	@Override
	public void setModeHelp(String modeHelp) {
		setTypeSettingsProperty(LayoutTypePortletConstants.MODE_HELP, modeHelp);
	}

	@Override
	public void setModePreview(String modePreview) {
		setTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_PREVIEW, modePreview);
	}

	@Override
	public void setModePrint(String modePrint) {
		setTypeSettingsProperty(
			LayoutTypePortletConstants.MODE_PRINT, modePrint);
	}

	@Override
	public void setPortalPreferences(PortalPreferences portalPreferences) {
		_portalPreferences = portalPreferences;
	}

	@Override
	public void setStateMax(String stateMax) {
		setTypeSettingsProperty(LayoutTypePortletConstants.STATE_MAX, stateMax);
	}

	@Override
	public void setStateMin(String stateMin) {
		setTypeSettingsProperty(LayoutTypePortletConstants.STATE_MIN, stateMin);
	}

	@Override
	public void setUpdatePermission(boolean updatePermission) {
		_updatePermission = updatePermission;
	}

	protected void addNestedColumn(String columnId) {
		String nestedColumnIds = getTypeSettingsProperty(
			LayoutTypePortletConstants.NESTED_COLUMN_IDS, StringPool.BLANK);

		if (!nestedColumnIds.contains(columnId)) {
			nestedColumnIds = StringUtil.add(nestedColumnIds, columnId);

			setTypeSettingsProperty(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS, nestedColumnIds);
		}
	}

	protected String addPortletId(
		long userId, String portletId, String columnId, int columnPos,
		boolean checkPermission, boolean strictHasPortlet) {

		portletId = JS.getSafeName(portletId);

		Layout layout = getLayout();

		Portlet portlet = null;

		try {
			portlet = PortletLocalServiceUtil.getPortletById(
				layout.getCompanyId(), portletId);

			if (portlet == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Portlet " + portletId +
							" cannot be added because it is not registered");
				}

				return null;
			}

			if (checkPermission &&
				!PortletPermissionUtil.contains(
					PermissionThreadLocal.getPermissionChecker(), layout,
					portlet, ActionKeys.ADD_TO_PAGE)) {

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"User has no permissions to add portlet ",
							portletId, " to layout ", layout.getPlid()));
				}

				return null;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (portlet.isSystem()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Portlet " + portletId +
						" cannot be added because it is a system portlet");
			}

			return null;
		}

		if (portlet.isInstanceable() &&
			!PortletIdCodec.hasInstanceId(portletId)) {

			PortletIdCodec.validatePortletName(portletId);

			portletId = PortletIdCodec.encode(portletId);
		}

		if (hasPortletId(portletId, strictHasPortlet)) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Portlet ", portletId, " cannot be added to layout ",
						layout.getPlid(), " because it already has another ",
						"portlet with the same portlet ID"));
			}

			return null;
		}

		if (columnId == null) {
			LayoutTemplate layoutTemplate = getLayoutTemplate();

			List<String> columns = layoutTemplate.getColumns();

			if (!columns.isEmpty()) {
				columnId = columns.get(0);
			}
		}

		if (columnId == null) {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Portlet ", portletId, " cannot be added to layout ",
						layout.getPlid(), " because column ID is null"));
			}

			return null;
		}

		if (isColumnCustomizable(columnId)) {
			if (isColumnDisabled(columnId) &&
				!columnId.startsWith(_NESTED_PORTLETS_NAMESPACE)) {

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Portlet ", portletId,
							" cannot be added to layout ", layout.getPlid(),
							" because column ", columnId, " is disabled"));
				}

				return null;
			}

			if (hasUserPreferences()) {
				portletId = PortletIdCodec.encode(
					PortletIdCodec.decodePortletName(portletId), userId,
					PortletIdCodec.decodeInstanceId(portletId));
			}
		}

		String columnValue = StringPool.BLANK;

		if (PortletIdCodec.hasInstanceId(columnId) &&
			PortletIdCodec.hasUserId(columnId)) {

			PortletPreferences portletPreferences =
				_getUserColumnPortletPreferences(columnId);

			columnValue = portletPreferences.getValue(
				columnId, StringPool.BLANK);
		}
		else if (hasUserPreferences()) {
			columnValue = getUserPreference(columnId);
		}
		else {
			columnValue = getTypeSettingsProperty(columnId);
		}

		if ((columnValue == null) &&
			columnId.startsWith(_NESTED_PORTLETS_NAMESPACE)) {

			addNestedColumn(columnId);
		}

		if (columnPos >= 0) {
			List<String> portletIds = ListUtil.fromArray(
				StringUtil.split(columnValue));

			if (columnPos <= portletIds.size()) {
				portletIds.add(columnPos, portletId);
			}
			else {
				portletIds.add(portletId);
			}

			columnValue = StringUtil.merge(portletIds);
		}
		else {
			columnValue = StringUtil.add(columnValue, portletId);
		}

		if (PortletIdCodec.hasInstanceId(columnId) &&
			PortletIdCodec.hasUserId(columnId)) {

			PortletPreferences portletPreferences =
				_getUserColumnPortletPreferences(columnId);

			try {
				portletPreferences.setValue(columnId, columnValue);

				portletPreferences.store();
			}
			catch (Exception exception) {
				_log.error("Unable to save portlet preferences", exception);
			}
		}
		else if (hasUserPreferences()) {
			setUserPreference(columnId, columnValue);
		}
		else {
			setTypeSettingsProperty(columnId, columnValue);
		}

		try {
			if (_enablePortletLayoutListener &&
				!portlet.isUndeployedPortlet()) {

				PortletLayoutListener portletLayoutListener =
					portlet.getPortletLayoutListenerInstance();

				if (portletLayoutListener != null) {
					portletLayoutListener.onAddToLayout(
						portletId, layout.getPlid());
				}
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to fire portlet layout listener event", exception);
		}

		return portletId;
	}

	protected void copyPreferences(
		long userId, String sourcePortletId, String targetPortletId) {

		Layout layout = getLayout();

		try {
			PortletPreferencesIds portletPreferencesIds =
				PortletPreferencesFactoryUtil.getPortletPreferencesIds(
					layout.getGroupId(), 0, layout, sourcePortletId, false);

			PortletPreferences sourcePortletPreferences =
				PortletPreferencesLocalServiceUtil.getStrictPreferences(
					portletPreferencesIds);

			portletPreferencesIds =
				PortletPreferencesFactoryUtil.getPortletPreferencesIds(
					layout.getGroupId(), userId, layout, targetPortletId,
					false);

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			String sourcePortletNamespace = PortalUtil.getPortletNamespace(
				sourcePortletId);

			for (Map.Entry<String, String> entry :
					typeSettingsUnicodeProperties.entrySet()) {

				String key = entry.getKey();

				if (key.startsWith(sourcePortletNamespace)) {
					String targetKey =
						PortalUtil.getPortletNamespace(targetPortletId) +
							key.substring(sourcePortletNamespace.length());

					sourcePortletPreferences.setValue(
						targetKey, entry.getValue());
				}
			}

			PortletPreferencesLocalServiceUtil.updatePreferences(
				portletPreferencesIds.getOwnerId(),
				portletPreferencesIds.getOwnerType(),
				portletPreferencesIds.getPlid(),
				portletPreferencesIds.getPortletId(), sourcePortletPreferences);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	protected void copyResourcePermissions(
		String sourcePortletId, String targetPortletId) {

		Layout layout = getLayout();

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			getCompanyId(), sourcePortletId);

		String sourcePortletPrimaryKey = PortletPermissionUtil.getPrimaryKey(
			layout.getPlid(), sourcePortletId);

		List<ResourcePermission> resourcePermissions =
			ResourcePermissionLocalServiceUtil.getResourcePermissions(
				portlet.getCompanyId(), portlet.getPortletName(),
				PortletKeys.PREFS_OWNER_TYPE_USER, sourcePortletPrimaryKey);

		for (ResourcePermission resourcePermission : resourcePermissions) {
			String targetPortletPrimaryKey =
				PortletPermissionUtil.getPrimaryKey(
					layout.getPlid(), targetPortletId);

			resourcePermission.setResourcePermissionId(
				CounterLocalServiceUtil.increment());
			resourcePermission.setPrimKey(targetPortletPrimaryKey);

			ResourcePermissionLocalServiceUtil.addResourcePermission(
				resourcePermission);
		}
	}

	protected String getColumnValue(String columnId) {
		if (hasUserPreferences() && isCustomizable() &&
			!isColumnDisabled(columnId) &&
			!columnId.startsWith(_NESTED_PORTLETS_NAMESPACE)) {

			return getUserPreference(columnId);
		}

		if (PortletIdCodec.hasInstanceId(columnId) &&
			PortletIdCodec.hasUserId(columnId)) {

			PortletPreferences portletPreferences =
				_getUserColumnPortletPreferences(columnId);

			return portletPreferences.getValue(columnId, StringPool.BLANK);
		}

		return getTypeSettingsProperty(columnId);
	}

	protected long getCompanyId() {
		Layout layout = getLayout();

		return layout.getCompanyId();
	}

	protected LayoutTypePortletImpl getDefaultLayoutTypePortletImpl() {
		if (!isCustomizedView()) {
			return this;
		}

		LayoutTypePortletImpl defaultLayoutTypePortletImpl =
			(LayoutTypePortletImpl)LayoutTypePortletFactoryUtil.create(
				getLayout());

		defaultLayoutTypePortletImpl._layoutSetPrototypeLayout =
			_layoutSetPrototypeLayout;
		defaultLayoutTypePortletImpl._updatePermission = _updatePermission;

		return defaultLayoutTypePortletImpl;
	}

	protected String[] getNestedColumns() {
		String[] nestedColumnIds = StringUtil.split(
			getTypeSettingsProperty(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS));

		if (!hasUserPreferences()) {
			return nestedColumnIds;
		}

		for (String columnId : nestedColumnIds) {
			if (columnId.lastIndexOf(StringPool.DOUBLE_UNDERLINE) != -1) {
				columnId = columnId.substring(
					columnId.lastIndexOf(StringPool.DOUBLE_UNDERLINE) + 2);
			}

			String[] portletIds = StringUtil.split(
				_portalPreferences.getValue(
					CustomizedPages.namespacePlid(getPlid()), columnId));

			for (String portletId : portletIds) {
				PortletPreferences portletPreferences =
					PortletPreferencesFactoryUtil.getLayoutPortletSetup(
						getLayout(), portletId);

				nestedColumnIds = ArrayUtil.append(
					nestedColumnIds, _getNestedColumnIds(portletPreferences));
			}
		}

		return nestedColumnIds;
	}

	protected long getPlid() {
		Layout layout = getLayout();

		return layout.getPlid();
	}

	protected String[] getStaticPortletIds(String position) {
		if (!_HAS_STATIC_PORTLETS) {
			return StringPool.EMPTY_ARRAY;
		}

		Layout layout = getLayout();

		Group group = _getGroup();

		if (group == null) {
			_log.error("Unable to get group " + layout.getGroupId());

			return new String[0];
		}

		String selector1 = StringPool.BLANK;

		if (group.isUser()) {
			selector1 = LayoutTypePortletConstants.STATIC_PORTLET_USER_SELECTOR;
		}
		else if (group.isOrganization()) {
			selector1 =
				LayoutTypePortletConstants.STATIC_PORTLET_ORGANIZATION_SELECTOR;
		}
		else if (group.isRegularSite()) {
			selector1 =
				LayoutTypePortletConstants.STATIC_PORTLET_REGULAR_SITE_SELECTOR;
		}

		String selector2 = layout.getFriendlyURL();

		String[] portletIds = PropsUtil.getArray(
			position, new Filter(selector1, selector2));

		for (int i = 0; i < portletIds.length; i++) {
			portletIds[i] = JS.getSafeName(portletIds[i]);
		}

		return portletIds;
	}

	protected String getThemeId() {
		try {
			Layout layout = getLayout();

			Theme theme = layout.getTheme();

			return theme.getThemeId();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	protected String getUserPreference(String key) {
		String value = StringPool.BLANK;

		if (!hasUserPreferences()) {
			return value;
		}

		value = _portalPreferences.getValue(
			CustomizedPages.namespacePlid(getPlid()), key, StringPool.NULL);

		if (!value.equals(StringPool.NULL)) {
			return value;
		}

		value = getTypeSettingsProperty(key);

		if (Validator.isNull(value)) {
			return value;
		}

		List<String> newPortletIds = new ArrayList<>();

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		String[] portletIds = StringUtil.split(value);

		for (String portletId : portletIds) {
			try {
				if (!PortletPermissionUtil.contains(
						permissionChecker, getLayout(), portletId,
						ActionKeys.VIEW, true)) {

					continue;
				}

				String rootPortletId = PortletIdCodec.decodePortletName(
					portletId);

				if (!PortletPermissionUtil.contains(
						permissionChecker, rootPortletId,
						ActionKeys.ADD_TO_PAGE)) {

					continue;
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			String newPortletId = null;

			boolean preferencesUniquePerLayout = false;

			try {
				Portlet portlet = PortletLocalServiceUtil.getPortletById(
					getCompanyId(), portletId);

				preferencesUniquePerLayout =
					portlet.isPreferencesUniquePerLayout();
			}
			catch (SystemException systemException) {
				_log.error(systemException);
			}

			if (PortletIdCodec.hasInstanceId(portletId) ||
				preferencesUniquePerLayout) {

				String instanceId = null;

				if (PortletIdCodec.hasInstanceId(portletId)) {
					instanceId = PortletIdCodec.generateInstanceId();
				}

				newPortletId = PortletIdCodec.encode(
					PortletIdCodec.decodePortletName(portletId),
					_portalPreferences.getUserId(), instanceId);

				copyPreferences(
					_portalPreferences.getUserId(), portletId, newPortletId);

				copyResourcePermissions(portletId, newPortletId);
			}
			else {
				newPortletId = portletId;
			}

			newPortletIds.add(newPortletId);
		}

		value = StringUtil.merge(newPortletIds);

		setUserPreference(key, value);

		return value;
	}

	protected boolean hasNonstaticPortletId(String portletId) {
		LayoutTemplate layoutTemplate = getLayoutTemplate();

		List<String> columns = layoutTemplate.getColumns();

		for (String columnId : columns) {
			if (hasNonstaticPortletId(columnId, portletId)) {
				return true;
			}
		}

		return false;
	}

	protected boolean hasNonstaticPortletId(String columnId, String portletId) {
		String[] columnValues = StringUtil.split(getColumnValue(columnId));

		for (String nonstaticPortletId : columnValues) {
			if (nonstaticPortletId.equals(portletId)) {
				return true;
			}
		}

		return false;
	}

	protected boolean hasStaticPortletId(String columnId, String portletId) {
		String[] staticPortletIdsStart = getStaticPortletIds(
			PropsKeys.LAYOUT_STATIC_PORTLETS_START + columnId);

		for (String staticPortletId : staticPortletIdsStart) {
			if (staticPortletId.equals(portletId)) {
				return true;
			}
		}

		String[] staticPortletIdsEnd = getStaticPortletIds(
			PropsKeys.LAYOUT_STATIC_PORTLETS_END + columnId);

		for (String staticPortletId : staticPortletIdsEnd) {
			if (staticPortletId.equals(portletId)) {
				return true;
			}
		}

		return false;
	}

	protected boolean hasUserPreferences() {
		if (_portalPreferences != null) {
			return true;
		}

		return false;
	}

	protected boolean isLayoutSetPrototype() {
		try {
			Group group = _getGroup();

			return group.isLayoutSetPrototype();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	protected void onRemoveFromLayout(String[] portletIds) {
		Set<String> portletIdList = new HashSet<>();

		for (String portletId : portletIds) {
			removeModesPortletId(portletId);
			removeStatesPortletId(portletId);

			portletIdList.add(portletId);

			String rootPortletId = PortletIdCodec.decodePortletName(portletId);

			if (rootPortletId.equals(PortletKeys.NESTED_PORTLETS)) {
				_removeNestedColumns(
					PortalUtil.getPortletNamespace(portletId), portletIdList);
			}

			Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

			if (portlet == null) {
				continue;
			}

			PortletLayoutListener portletLayoutListener =
				portlet.getPortletLayoutListenerInstance();

			if (portletLayoutListener == null) {
				continue;
			}

			portletLayoutListener.updatePropertiesOnRemoveFromLayout(
				portletId, getTypeSettingsProperties());
		}

		try {
			PortletLocalServiceUtil.deletePortlets(
				getCompanyId(), portletIdList.toArray(new String[0]),
				getPlid());
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	protected void setUserPreference(String key, String value) {
		_portalPreferences.setValue(
			CustomizedPages.namespacePlid(getPlid()), key, value);
		_portalPreferences.setValue(
			CustomizedPages.namespacePlid(getPlid()), _MODIFIED_DATE,
			_dateFormat.format(new Date()));
	}

	private Group _getGroup() {
		if (_group == null) {
			Layout layout = getLayout();

			_group = layout.getGroup();
		}

		return _group;
	}

	private String[] _getNestedColumnIds(
		PortletPreferences portletPreferences) {

		Map<String, String[]> preferencesMap = portletPreferences.getMap();

		String[] columnIds = new String[0];

		for (String key : preferencesMap.keySet()) {
			if (!key.startsWith(_NESTED_PORTLETS_NAMESPACE)) {
				continue;
			}

			columnIds = ArrayUtil.append(columnIds, key);
		}

		return columnIds;
	}

	private PortletPreferences _getUserColumnPortletPreferences(
		String columnId) {

		String instanceId = PortletIdCodec.decodeInstanceId(columnId);

		if (instanceId.indexOf(StringPool.UNDERLINE) != -1) {
			instanceId = instanceId.substring(
				0, instanceId.indexOf(StringPool.UNDERLINE));
		}

		long userId = PortletIdCodec.decodeUserId(columnId);

		String portletName = PortletIdCodec.decodePortletName(columnId);

		if (portletName.startsWith(StringPool.UNDERLINE)) {
			portletName = portletName.substring(1);
		}

		return PortletPreferencesFactoryUtil.getLayoutPortletSetup(
			getLayout(),
			PortletIdCodec.encode(portletName, userId, instanceId));
	}

	private void _removeNestedColumns(
		String portletNamespace, Set<String> portletIdList) {

		UnicodeProperties typeSettingsUnicodeProperties =
			getTypeSettingsProperties();

		UnicodeProperties newTypeSettingsUnicodeProperties =
			new UnicodeProperties();

		StringBundler sb = new StringBundler(
			typeSettingsUnicodeProperties.size() * 2);

		for (Map.Entry<String, String> entry :
				typeSettingsUnicodeProperties.entrySet()) {

			String key = entry.getKey();

			if (key.startsWith(portletNamespace)) {
				sb.append(entry.getValue());
				sb.append(StringPool.COMMA);
			}
		}

		if (sb.index() != 0) {
			sb.setIndex(sb.index() - 1);

			for (String nestedPortletId : StringUtil.split(sb.toString())) {
				String childNestedPortletNamespace =
					PortalUtil.getPortletNamespace(nestedPortletId);

				_removeNestedColumns(
					childNestedPortletNamespace, portletIdList);

				removeModesPortletId(nestedPortletId);
				removeStatesPortletId(nestedPortletId);

				portletIdList.add(nestedPortletId);
			}
		}

		typeSettingsUnicodeProperties = getTypeSettingsProperties();

		for (Map.Entry<String, String> entry :
				typeSettingsUnicodeProperties.entrySet()) {

			String key = entry.getKey();

			if (!key.startsWith(portletNamespace)) {
				newTypeSettingsUnicodeProperties.setProperty(
					key, entry.getValue());
			}
		}

		Layout layout = getLayout();

		layout.setTypeSettingsProperties(newTypeSettingsUnicodeProperties);

		String nestedColumnIds = GetterUtil.getString(
			getTypeSettingsProperty(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS));

		String[] nestedColumnIdsArray = ArrayUtil.removeByPrefix(
			StringUtil.split(nestedColumnIds), portletNamespace);

		setTypeSettingsProperty(
			LayoutTypePortletConstants.NESTED_COLUMN_IDS,
			StringUtil.merge(nestedColumnIdsArray));
	}

	private static final boolean _HAS_STATIC_PORTLETS;

	private static final String _MODIFIED_DATE = "modifiedDate";

	private static final String _NESTED_PORTLETS_NAMESPACE =
		PortalUtil.getPortletNamespace(PortletKeys.NESTED_PORTLETS);

	private static final String _NULL_DATE = "00000000000000";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutTypePortletImpl.class);

	private static final Layout _nullLayout = new LayoutImpl();

	static {
		Properties properties = PropsUtil.getProperties(
			PropsKeys.LAYOUT_STATIC_PORTLETS, false);

		_HAS_STATIC_PORTLETS = !properties.isEmpty();
	}

	private String _addedCustomPortletMode;
	private boolean _customizedView;
	private final Format _dateFormat =
		FastDateFormatFactoryUtil.getSimpleDateFormat(
			PropsValues.INDEX_DATE_FORMAT_PATTERN);
	private boolean _enablePortletLayoutListener = true;
	private Group _group;
	private Layout _layoutSetPrototypeLayout;
	private PortalPreferences _portalPreferences;
	private boolean _updatePermission;

}