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
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
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

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author José Manuel Navarro
 */
public class InputAssetLinksDisplayContext {

	public InputAssetLinksDisplayContext(PageContext pageContext) {
		_httpServletRequest = (HttpServletRequest)pageContext.getRequest();

		_assetEntryId = GetterUtil.getLong(
			(String)_httpServletRequest.getAttribute(
				"liferay-asset:input-asset-links:assetEntryId"));
		_className = GetterUtil.getString(
			_httpServletRequest.getAttribute(
				"liferay-asset:input-asset-links:className"));
		_portletRequest = (PortletRequest)_httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public JSONArray getAssetEntryTypesJSONArray() throws Exception {
		AssetRendererFactory<?> baseAssetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_className);

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		boolean baseAssetStaged = stagingGroupHelper.isStagedPortlet(
			_themeDisplay.getScopeGroupId(),
			baseAssetRendererFactory.getPortletId());

		long[] groupIds =
			SiteConnectedGroupGroupProviderUtil.
				getCurrentAndAncestorSiteAndDepotGroupIds(
					new long[] {_themeDisplay.getScopeGroupId()});

		return JSONUtil.toJSONArray(
			getAssetRendererFactories(),
			assetRendererFactory -> {
				boolean assetStaged = stagingGroupHelper.isStagedPortlet(
					_themeDisplay.getScopeGroupId(),
					assetRendererFactory.getPortletId());

				if (!baseAssetStaged && assetStaged) {
					return null;
				}

				return JSONUtil.put(
					"classNameId",
					PortalUtil.getClassNameId(
						assetRendererFactory.getClassName())
				).put(
					"groupIds",
					_getStagedPortletGroupIdsJSONArray(
						groupIds, assetRendererFactory.getPortletId())
				).put(
					"label",
					assetRendererFactory.getTypeName(_themeDisplay.getLocale())
				);
			});
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

	public String getGroupDescriptiveName(AssetEntry assetEntry)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(assetEntry.getGroupId());

		return group.getDescriptiveName(_themeDisplay.getLocale());
	}

	public long getRefererClassNameId() {
		AssetEntry assetEntry = _getRefererAssetEntry();

		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getClassNameId();
	}

	public long getRefererClassPK() {
		AssetEntry assetEntry = _getRefererAssetEntry();

		if (assetEntry == null) {
			return 0;
		}

		return assetEntry.getClassPK();
	}

	private List<AssetLink> _createAssetLinks() throws PortalException {
		String assetLinksSearchContainerPrimaryKeys = ParamUtil.getString(
			_httpServletRequest, "assetLinksSearchContainerPrimaryKeys");

		if (Validator.isNull(assetLinksSearchContainerPrimaryKeys) &&
			SessionErrors.isEmpty(_portletRequest) && (_assetEntryId > 0)) {

			return TransformUtil.transform(
				AssetLinkLocalServiceUtil.getDirectLinks(_assetEntryId, false),
				assetLink -> {
					AssetEntry assetLinkEntry = getAssetLinkEntry(assetLink);

					AssetRendererFactory<?> assetRendererFactory =
						AssetRendererFactoryRegistryUtil.
							getAssetRendererFactoryByClassName(
								assetLinkEntry.getClassName());

					if (assetRendererFactory.isActive(
							_themeDisplay.getCompanyId())) {

						return assetLink;
					}

					return null;
				});
		}

		return TransformUtil.transformToList(
			StringUtil.split(assetLinksSearchContainerPrimaryKeys),
			assetEntryPrimaryKey -> {
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

				return assetLink;
			});
	}

	private AssetEntry _getRefererAssetEntry() {
		if (_assetEntryId <= 0) {
			return null;
		}

		return AssetEntryLocalServiceUtil.fetchEntry(_assetEntryId);
	}

	private JSONArray _getStagedPortletGroupIdsJSONArray(
		long[] groupIds, String portletId) {

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		long[] stagedPortletGroupIds = TransformUtil.transformToLongArray(
			ArrayUtil.toArray(groupIds),
			groupId -> stagingGroupHelper.getStagedPortletGroupId(
				groupId, portletId));

		return JSONUtil.putAll(
			ArrayUtil.toArray(ArrayUtil.unique(stagedPortletGroupIds)));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InputAssetLinksDisplayContext.class);

	private final long _assetEntryId;
	private List<AssetLink> _assetLinks;
	private final String _className;
	private final HttpServletRequest _httpServletRequest;
	private final PortletRequest _portletRequest;
	private final ThemeDisplay _themeDisplay;

}