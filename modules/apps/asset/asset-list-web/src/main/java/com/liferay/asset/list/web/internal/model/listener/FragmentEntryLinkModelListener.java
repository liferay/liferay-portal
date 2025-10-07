/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.model.listener;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ModelListener.class)
public class FragmentEntryLinkModelListener
	extends BaseModelListener<FragmentEntryLink> {

	@Override
	public void onAfterCreate(FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		_updateAssetListEntryUsages(fragmentEntryLink);
	}

	@Override
	public void onAfterRemove(FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		_assetListEntryUsageLocalService.deleteAssetListEntryUsages(
			String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
			_portal.getClassNameId(FragmentEntryLink.class.getName()),
			fragmentEntryLink.getPlid());
	}

	@Override
	public void onAfterUpdate(
			FragmentEntryLink originalFragmentEntryLink,
			FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		_updateAssetListEntryUsages(fragmentEntryLink);
	}

	private void _addAssetListEntryUsage(
		long classNameId, long fragmentEntryLinkId, long groupId, String key,
		long plid) {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}

		try {
			_assetListEntryUsageLocalService.addAssetListEntryUsage(
				serviceContext.getUserId(), groupId, classNameId,
				String.valueOf(fragmentEntryLinkId),
				_portal.getClassNameId(FragmentEntryLink.class.getName()), key,
				plid, serviceContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private List<FragmentConfigurationField>
		_getCollectionSelectorFragmentConfigurationFields(
			FragmentEntryLink fragmentEntryLink) {

		return ListUtil.filter(
			_fragmentEntryConfigurationParser.getFragmentConfigurationFields(
				fragmentEntryLink.getConfigurationJSONObject()),
			fragmentConfigurationField -> Objects.equals(
				fragmentConfigurationField.getType(), "collectionSelector"));
	}

	private void _updateAssetListEntryUsages(
		FragmentEntryLink fragmentEntryLink) {

		_assetListEntryUsageLocalService.deleteAssetListEntryUsages(
			String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
			_portal.getClassNameId(FragmentEntryLink.class.getName()),
			fragmentEntryLink.getPlid());

		List<FragmentConfigurationField> fragmentConfigurationFields =
			_getCollectionSelectorFragmentConfigurationFields(
				fragmentEntryLink);

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			Object fieldValue = _fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getConfigurationJSONObject(),
				fragmentEntryLink.getEditableValuesJSONObject(),
				LocaleUtil.getMostRelevantLocale(),
				fragmentConfigurationField.getName());

			if (!(fieldValue instanceof JSONObject)) {
				continue;
			}

			JSONObject fieldValueJSONObject = (JSONObject)fieldValue;

			if (fieldValueJSONObject.has("key")) {
				_addAssetListEntryUsage(
					_portal.getClassNameId(
						InfoCollectionProvider.class.getName()),
					fragmentEntryLink.getFragmentEntryLinkId(),
					fragmentEntryLink.getGroupId(),
					fieldValueJSONObject.getString("key"),
					fragmentEntryLink.getPlid());
			}

			if (fieldValueJSONObject.has("classPK")) {
				_addAssetListEntryUsage(
					_portal.getClassNameId(AssetListEntry.class.getName()),
					fragmentEntryLink.getFragmentEntryLinkId(),
					fragmentEntryLink.getGroupId(),
					fieldValueJSONObject.getString("classPK"),
					fragmentEntryLink.getPlid());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLinkModelListener.class);

	@Reference
	private AssetListEntryUsageLocalService _assetListEntryUsageLocalService;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private Portal _portal;

}