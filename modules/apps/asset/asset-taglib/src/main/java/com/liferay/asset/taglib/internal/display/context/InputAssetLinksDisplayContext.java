/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalServiceUtil;
import com.liferay.asset.taglib.internal.item.selector.ItemSelectorUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.asset.criterion.AssetEntryItemSelectorCriterion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.io.Serializable;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author José Manuel Navarro
 */
public class InputAssetLinksDisplayContext {

	public InputAssetLinksDisplayContext(PageContext pageContext) {
		_pageContext = pageContext;

		_httpServletRequest = (HttpServletRequest)pageContext.getRequest();

		_assetEntryId = GetterUtil.getLong(
			(String)_httpServletRequest.getAttribute(
				"liferay-asset:input-asset-links:assetEntryId"));
		_className = GetterUtil.getString(
			_httpServletRequest.getAttribute(
				"liferay-asset:input-asset-links:className"));
		_portletRequest = (PortletRequest)_httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_portletResponse = (PortletResponse)_httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE);
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		DropdownItemList dropdownItemList = new DropdownItemList();

		AssetRendererFactory<?> baseAssetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_className);

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		boolean baseAssetStaged = stagingGroupHelper.isStagedPortlet(
			_themeDisplay.getScopeGroupId(),
			baseAssetRendererFactory.getPortletId());

		for (AssetRendererFactory<?> assetRendererFactory :
				getAssetRendererFactories()) {

			boolean assetStaged = stagingGroupHelper.isStagedPortlet(
				_themeDisplay.getScopeGroupId(),
				assetRendererFactory.getPortletId());

			if (!baseAssetStaged && assetStaged) {
				continue;
			}

			if (assetRendererFactory.isSupportsClassTypes()) {
				long groupId = _getAssetBrowserGroupId(assetRendererFactory);

				ClassTypeReader classTypeReader =
					assetRendererFactory.getClassTypeReader();

				List<ClassType> classTypes =
					classTypeReader.getAvailableClassTypes(
						PortalUtil.getCurrentAndAncestorSiteGroupIds(groupId),
						_themeDisplay.getLocale());

				if (classTypes.isEmpty()) {
					return Collections.emptyList();
				}

				for (ClassType classType : classTypes) {
					dropdownItemList.add(
						dropdownItem -> {
							dropdownItem.putData(
								"href",
								String.valueOf(
									_getAssetEntryItemSelectorPortletURL(
										assetRendererFactory,
										classType.getClassTypeId())));
							dropdownItem.putData(
								"title",
								LanguageUtil.format(
									TagResourceBundleUtil.getResourceBundle(
										_pageContext),
									"select-x", classType.getName(), false));
							dropdownItem.setLabel(classType.getName());
						});
				}
			}
			else {
				dropdownItemList.add(
					dropdownItem -> {
						dropdownItem.putData(
							"href",
							String.valueOf(
								_getAssetEntryItemSelectorPortletURL(
									assetRendererFactory, -1)));
						dropdownItem.putData(
							"title",
							LanguageUtil.format(
								TagResourceBundleUtil.getResourceBundle(
									_pageContext),
								"select-x",
								assetRendererFactory.getTypeName(
									_themeDisplay.getLocale()),
								false));
						dropdownItem.setLabel(
							assetRendererFactory.getTypeName(
								_themeDisplay.getLocale()));
					});
			}
		}

		return ListUtil.sort(
			dropdownItemList,
			new SelectorEntriesLabelComparator(_themeDisplay.getLocale()));
	}

	public AssetEntry getAssetLinkEntry(AssetLink assetLink)
		throws PortalException {

		if ((_assetEntryId > 0) || (assetLink.getEntryId1() == _assetEntryId)) {
			return AssetEntryLocalServiceUtil.getEntry(assetLink.getEntryId2());
		}

		return AssetEntryLocalServiceUtil.getEntry(assetLink.getEntryId1());
	}

	public List<AssetLink> getAssetLinks() throws PortalException {
		if (_assetLinks == null) {
			_assetLinks = _createAssetLinks();
		}

		return _assetLinks;
	}

	public int getAssetLinksCount() throws PortalException {
		List<AssetLink> assetLinks = getAssetLinks();

		return assetLinks.size();
	}

	public List<AssetRendererFactory<?>> getAssetRendererFactories() {
		return ListUtil.filter(
			AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				_themeDisplay.getCompanyId()),
			assetRendererFactory -> {
				if (assetRendererFactory.isLinkable() &&
					assetRendererFactory.isSelectable()) {

					return true;
				}

				return false;
			});
	}

	public String getAssetType(AssetEntry entry) {
		AssetRendererFactory<?> assetRendererFactory =
			entry.getAssetRendererFactory();

		String assetType = assetRendererFactory.getTypeName(
			_themeDisplay.getLocale());

		if (!assetRendererFactory.isSupportsClassTypes()) {
			return assetType;
		}

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		try {
			ClassType classType = classTypeReader.getClassType(
				entry.getClassTypeId(), _themeDisplay.getLocale());

			assetType = classType.getName();
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get asset type for class type primary key " +
					entry.getClassTypeId(),
				portalException);
		}

		return assetType;
	}

	public String getEventName() {
		if (_eventName != null) {
			return _eventName;
		}

		_eventName = _portletResponse.getNamespace() + "selectAsset";

		return _eventName;
	}

	public String getGroupDescriptiveName(AssetEntry assetEntry)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(assetEntry.getGroupId());

		return group.getDescriptiveName(_themeDisplay.getLocale());
	}

	private List<AssetLink> _createAssetLinks() throws PortalException {
		List<AssetLink> assetLinks = new ArrayList<>();

		String assetLinksSearchContainerPrimaryKeys = ParamUtil.getString(
			_httpServletRequest, "assetLinksSearchContainerPrimaryKeys");

		if (Validator.isNull(assetLinksSearchContainerPrimaryKeys) &&
			SessionErrors.isEmpty(_portletRequest) && (_assetEntryId > 0)) {

			List<AssetLink> directAssetLinks =
				AssetLinkLocalServiceUtil.getDirectLinks(_assetEntryId, false);

			for (AssetLink assetLink : directAssetLinks) {
				AssetEntry assetLinkEntry = getAssetLinkEntry(assetLink);

				AssetRendererFactory<?> assetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassName(
							assetLinkEntry.getClassName());

				if (assetRendererFactory.isActive(
						_themeDisplay.getCompanyId())) {

					assetLinks.add(assetLink);
				}
			}
		}
		else {
			String[] assetEntriesPrimaryKeys = StringUtil.split(
				assetLinksSearchContainerPrimaryKeys);

			for (String assetEntryPrimaryKey : assetEntriesPrimaryKeys) {
				long assetEntryPrimaryKeyLong = GetterUtil.getLong(
					assetEntryPrimaryKey);

				AssetEntry assetEntry = AssetEntryServiceUtil.getEntry(
					assetEntryPrimaryKeyLong);

				AssetLink assetLink = AssetLinkLocalServiceUtil.createAssetLink(
					0);

				if (_assetEntryId > 0) {
					assetLink.setEntryId1(_assetEntryId);
				}
				else {
					assetLink.setEntryId1(0);
				}

				assetLink.setEntryId2(assetEntry.getEntryId());

				assetLinks.add(assetLink);
			}
		}

		return assetLinks;
	}

	private long _getAssetBrowserGroupId(
		AssetRendererFactory<?> assetRendererFactory) {

		Group scopeGroup = _themeDisplay.getScopeGroup();

		long groupId = scopeGroup.getGroupId();

		if (_isStagedLocally() && scopeGroup.isStagingGroup()) {
			boolean stagedReferencePortlet = scopeGroup.isStagedPortlet(
				assetRendererFactory.getPortletId());

			if (_isStagedReferrerPortlet() && !stagedReferencePortlet) {
				groupId = scopeGroup.getLiveGroupId();
			}
		}

		return groupId;
	}

	private PortletURL _getAssetEntryItemSelectorPortletURL(
		AssetRendererFactory<?> assetRendererFactory, long subtypeSelectionId) {

		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		PortletURL portletURL = assetRendererFactory.getItemSelectorURL(
			PortalUtil.getLiferayPortletRequest(portletRequest),
			PortalUtil.getLiferayPortletResponse(portletResponse),
			subtypeSelectionId, portletResponse.getNamespace() + "selectAsset",
			_themeDisplay.getScopeGroup(), true, _assetEntryId);

		if (portletURL != null) {
			return portletURL;
		}

		AssetEntryItemSelectorCriterion assetEntryItemSelectorCriterion =
			new AssetEntryItemSelectorCriterion();

		assetEntryItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetEntryItemSelectorReturnType());
		assetEntryItemSelectorCriterion.setGroupId(
			_themeDisplay.getScopeGroupId());
		assetEntryItemSelectorCriterion.setSelectedGroupIds(
			new long[] {_themeDisplay.getScopeGroupId()});
		assetEntryItemSelectorCriterion.setShowNonindexable(true);
		assetEntryItemSelectorCriterion.setShowScheduled(true);
		assetEntryItemSelectorCriterion.setSubtypeSelectionId(
			subtypeSelectionId);
		assetEntryItemSelectorCriterion.setTypeSelection(
			assetRendererFactory.getClassName());

		ItemSelector itemSelector = ItemSelectorUtil.getItemSelector();

		return PortletURLBuilder.create(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_portletRequest),
				getEventName(), assetEntryItemSelectorCriterion)
		).setParameter(
			"refererAssetEntryId", _assetEntryId
		).buildPortletURL();
	}

	private boolean _isStagedLocally() {
		if (_stagedLocally != null) {
			return _stagedLocally;
		}

		Group scopeGroup = _themeDisplay.getScopeGroup();

		if (scopeGroup.isStaged() && !scopeGroup.isStagedRemotely()) {
			_stagedLocally = true;
		}
		else {
			_stagedLocally = false;
		}

		return _stagedLocally;
	}

	private boolean _isStagedReferrerPortlet() {
		if (_stagedReferrerPortlet != null) {
			return _stagedReferrerPortlet;
		}

		if (_isStagedLocally()) {
			String className = (String)_httpServletRequest.getAttribute(
				"liferay-asset:input-asset-links:className");

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className);

			Group scopeGroup = _themeDisplay.getScopeGroup();

			_stagedReferrerPortlet = scopeGroup.isStagedPortlet(
				assetRendererFactory.getPortletId());
		}
		else {
			_stagedReferrerPortlet = false;
		}

		return _stagedReferrerPortlet;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InputAssetLinksDisplayContext.class);

	private final long _assetEntryId;
	private List<AssetLink> _assetLinks;
	private final String _className;
	private String _eventName;
	private final HttpServletRequest _httpServletRequest;
	private final PageContext _pageContext;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;
	private Boolean _stagedLocally;
	private Boolean _stagedReferrerPortlet;
	private final ThemeDisplay _themeDisplay;

	private class SelectorEntriesLabelComparator
		implements Comparator<Map<String, Object>>, Serializable {

		public SelectorEntriesLabelComparator(Locale locale) {
			_collator = CollatorUtil.getInstance(locale);
		}

		@Override
		public int compare(Map<String, Object> map1, Map<String, Object> map2) {
			String label1 = StringPool.BLANK;
			String label2 = StringPool.BLANK;

			if (map1.containsKey("label") && map2.containsKey("label")) {
				label1 = (String)map1.get("label");
				label2 = (String)map2.get("label");
			}

			return _collator.compare(label1, label2);
		}

		private final Collator _collator;

	}

}