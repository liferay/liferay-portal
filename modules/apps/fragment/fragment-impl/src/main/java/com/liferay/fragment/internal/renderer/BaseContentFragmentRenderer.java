/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
public abstract class BaseContentFragmentRenderer implements FragmentRenderer {

	protected Tuple getDisplayObjectTuple(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		JSONObject jsonObject =
			(JSONObject)fragmentEntryConfigurationParser.getFieldValue(
				getConfiguration(fragmentRendererContext),
				fragmentEntryLink.getEditableValues(),
				fragmentRendererContext.getLocale(), "itemSelector");

		if ((jsonObject != null) && jsonObject.has("className") &&
			jsonObject.has("classPK")) {

			return new Tuple(
				jsonObject.getString("className"),
				jsonObject.getLong("classPK"));
		}

		AssetEntry assetEntry = (AssetEntry)httpServletRequest.getAttribute(
			WebKeys.LAYOUT_ASSET_ENTRY);

		if (assetEntry != null) {
			return new Tuple(
				assetEntry.getClassName(), assetEntry.getClassPK());
		}

		InfoItemReference infoItemReference =
			fragmentRendererContext.getContextInfoItemReference();

		if (infoItemReference != null) {
			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			InfoItemObjectProvider<Object> infoItemObjectProvider =
				infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemObjectProvider.class,
					infoItemReference.getClassName(),
					infoItemIdentifier.getInfoItemServiceFilter());

			try {
				Object infoItem = infoItemObjectProvider.getInfoItem(
					infoItemIdentifier);

				if (infoItem instanceof ClassedModel) {
					ClassedModel classedModel = (ClassedModel)infoItem;

					Serializable primaryKeyObj =
						classedModel.getPrimaryKeyObj();

					if (!Objects.equals(
							classedModel.getModelClassName(),
							AssetEntry.class.getName())) {

						return new Tuple(
							classedModel.getModelClassName(), primaryKeyObj);
					}

					assetEntry = assetEntryLocalService.fetchAssetEntry(
						(Long)primaryKeyObj);

					if (assetEntry != null) {
						return new Tuple(
							portal.fetchClassName(assetEntry.getClassNameId()),
							assetEntry.getClassPK());
					}
				}
			}
			catch (NoSuchInfoItemException noSuchInfoItemException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchInfoItemException);
				}
			}
		}

		return new Tuple(
			fragmentEntryLink.getClassName(), fragmentEntryLink.getClassPK());
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	@Reference
	protected FragmentEntryConfigurationParser fragmentEntryConfigurationParser;

	@Reference
	protected InfoItemServiceRegistry infoItemServiceRegistry;

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseContentFragmentRenderer.class);

}