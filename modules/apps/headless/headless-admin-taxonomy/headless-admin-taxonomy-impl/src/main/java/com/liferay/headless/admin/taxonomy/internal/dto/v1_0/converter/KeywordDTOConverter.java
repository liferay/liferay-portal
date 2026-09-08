/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagGroupRelLocalService;
import com.liferay.headless.admin.taxonomy.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.subscription.service.SubscriptionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
@Component(
	property = {
		"default=true", "dto.class.name=com.liferay.asset.kernel.model.AssetTag"
	},
	service = DTOConverter.class
)
public class KeywordDTOConverter implements DTOConverter<AssetTag, Keyword> {

	@Override
	public String getContentType() {
		return Keyword.class.getSimpleName();
	}

	@Override
	public Keyword toDTO(
		DTOConverterContext dtoConverterContext, AssetTag assetTag) {

		Group group = _groupLocalService.fetchGroup(assetTag.getGroupId());

		return new Keyword() {
			{
				setActions(dtoConverterContext::getActions);
				setAssetLibraries(
					() -> TransformUtil.transformToArray(
						_assetTagGroupRelLocalService.
							getAssetTagGroupRelsByTagId(assetTag.getTagId()),
						assetTagGroupRel -> {
							Group depotEntryGroup =
								_groupLocalService.fetchGroup(
									assetTagGroupRel.getGroupId());

							return new AssetLibrary() {
								{
									setExternalReferenceCode(
										() -> {
											if (depotEntryGroup == null) {
												return null;
											}

											return depotEntryGroup.
												getExternalReferenceCode();
										});
									setId(assetTagGroupRel::getGroupId);
									setName(
										() -> {
											if (depotEntryGroup == null) {
												return null;
											}

											return depotEntryGroup.
												getDescriptiveName(
													dtoConverterContext.
														getLocale());
										});
									setName_i18n(
										() -> {
											if (depotEntryGroup == null) {
												return null;
											}

											return LocalizedMapUtil.getI18nMap(
												dtoConverterContext.
													isAcceptAllLanguages(),
												depotEntryGroup.getNameMap());
										});
									setScopeKey(
										() -> {
											if (depotEntryGroup == null) {
												return null;
											}

											return depotEntryGroup.
												getGroupKey();
										});
								}
							};
						},
						AssetLibrary.class));
				setAssetLibraryKey(
					() -> {
						if (group == null) {
							return null;
						}

						return GroupUtil.getAssetLibraryKey(group);
					});
				setCreator(
					() -> {
						if (assetTag.getUserId() == 0) {
							return null;
						}

						return CreatorUtil.toCreator(
							_portal,
							_userLocalService.fetchUser(assetTag.getUserId()));
					});
				setDateCreated(assetTag::getCreateDate);
				setDateModified(assetTag::getModifiedDate);
				setExternalReferenceCode(assetTag::getExternalReferenceCode);
				setId(assetTag::getTagId);
				setKeywordUsageCount(
					() -> {
						Hits hits = _assetEntryLocalService.search(
							assetTag.getCompanyId(),
							new long[] {assetTag.getGroupId()},
							assetTag.getUserId(), null, -1, null, null, null,
							null, assetTag.getName(), true,
							new int[] {
								WorkflowConstants.STATUS_APPROVED,
								WorkflowConstants.STATUS_PENDING,
								WorkflowConstants.STATUS_SCHEDULED
							},
							false, 0, 1);

						return hits.getLength();
					});
				setName(assetTag::getName);
				setSiteExternalReferenceCode(
					() -> {
						if (group == null) {
							return null;
						}

						return GroupUtil.getSiteExternalReferenceCode(group);
					});
				setSiteId(
					() -> {
						if (group == null) {
							return null;
						}

						return GroupUtil.getSiteId(group);
					});
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						assetTag.getCompanyId(),
						dtoConverterContext.getUserId(),
						AssetTag.class.getName(), assetTag.getTagId()));
			}
		};
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetTagGroupRelLocalService _assetTagGroupRelLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}