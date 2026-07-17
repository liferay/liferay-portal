/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.StagingAssetEntryHelper;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedGroupedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = StagingAssetEntryHelper.class)
public class StagingAssetEntryHelperImpl implements StagingAssetEntryHelper {

	@Override
	public void addAssetReference(
		PortletDataContext portletDataContext, ClassedModel classedModel,
		Element stagedElement, AssetEntry assetEntry) {

		AssetRenderer<? extends StagedModel> assetRenderer = null;
		StagedModel stagedModel = null;

		try {
			assetRenderer =
				(AssetRenderer<? extends StagedModel>)
					assetEntry.getAssetRenderer();

			stagedModel = assetRenderer.getAssetObject();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return;
		}

		if (stagedModel == null) {
			return;
		}

		portletDataContext.addReferenceElement(
			classedModel, stagedElement, stagedModel,
			PortletDataContext.REFERENCE_TYPE_DEPENDENCY_DISPOSABLE, true);
	}

	@Override
	public AssetEntry fetchAssetEntry(long groupId, String uuid)
		throws PortalException {

		DynamicQuery dynamicQuery = _assetEntryLocalService.dynamicQuery();

		Property classUuidProperty = PropertyFactoryUtil.forName("classUuid");

		dynamicQuery.add(classUuidProperty.eq(uuid));

		List<AssetEntry> assetEntries = _assetEntryLocalService.dynamicQuery(
			dynamicQuery);

		if (ListUtil.isEmpty(assetEntries)) {
			return null;
		}

		Map<Long, AssetEntry> assetEntryMap = new HashMap<>();

		for (AssetEntry assetEntry : assetEntries) {
			assetEntryMap.put(assetEntry.getGroupId(), assetEntry);
		}

		// Try to fetch the existing staged model from the importing group

		AssetEntry groupAssetEntry = assetEntryMap.get(groupId);

		if (groupAssetEntry != null) {
			return groupAssetEntry;
		}

		// Try to fetch the existing staged model from parent sites

		Group group = _groupLocalService.getGroup(groupId);

		Group parentGroup = group.getParentGroup();

		while (parentGroup != null) {
			AssetEntry assetEntry = assetEntryMap.get(parentGroup.getGroupId());

			if ((assetEntry != null) && isAssetEntryApplicable(assetEntry)) {
				return assetEntry;
			}

			parentGroup = parentGroup.getParentGroup();
		}

		// Try to fetch the existing staged model from the global site

		Group companyGroup = _groupLocalService.fetchCompanyGroup(
			group.getCompanyId());

		AssetEntry companyGroupAssetEntry = assetEntryMap.get(
			companyGroup.getGroupId());

		if (companyGroupAssetEntry != null) {
			return companyGroupAssetEntry;
		}

		// Try to fetch the existing staged model from the company

		List<AssetEntry> companyAssetEntries = ListUtil.filter(
			assetEntries,
			entry -> entry.getCompanyId() == group.getCompanyId());

		if (ListUtil.isEmpty(companyAssetEntries)) {
			return null;
		}

		for (AssetEntry assetEntry : companyAssetEntries) {
			try {
				if (isAssetEntryApplicable(assetEntry)) {
					return assetEntry;
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return null;
	}

	@Override
	public boolean isAssetEntryApplicable(AssetEntry assetEntry)
		throws PortalException {

		AssetRenderer<? extends StagedModel> assetRenderer = null;

		StagedModel stagedModel = null;

		try {
			assetRenderer =
				(AssetRenderer<? extends StagedModel>)
					assetEntry.getAssetRenderer();

			stagedModel = assetRenderer.getAssetObject();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		if (stagedModel instanceof TrashedModel) {
			TrashedModel trashedModel = (TrashedModel)stagedModel;

			if (trashedModel.isInTrash()) {
				return false;
			}
		}

		if (stagedModel instanceof StagedGroupedModel) {
			StagedGroupedModel stagedGroupedModel =
				(StagedGroupedModel)stagedModel;

			Group group = _groupLocalService.getGroup(
				stagedGroupedModel.getGroupId());

			if (group.isStagingGroup()) {
				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingAssetEntryHelperImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}