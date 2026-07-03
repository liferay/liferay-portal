/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.object.comment.ObjectEntryComment;
import com.liferay.object.exception.ObjectEntryGroupIdException;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.ParentTaxonomyCategory;
import com.liferay.object.rest.dto.v1_0.ParentTaxonomyVocabulary;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.scope.Scope;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Sergio Jiménez del Coso
 */
public class ServiceContextUtil {

	public static ServiceContext createServiceContext(long objectEntryId) {
		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			ObjectEntryLocalServiceUtil.fetchObjectEntry(objectEntryId);

		if (serviceBuilderObjectEntry == null) {
			return new ServiceContext();
		}

		ServiceContext serviceContext = new ServiceContext();

		if (serviceBuilderObjectEntry.getStatus() ==
				WorkflowConstants.STATUS_DRAFT) {

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		return serviceContext;
	}

	public static ServiceContext createServiceContext(
			long companyId, boolean enableCategorization, long groupId,
			Locale locale, ModelPermissions modelPermissions,
			ObjectEntry objectEntry,
			List<ObjectEntryComment> objectEntryComments, long userId)
		throws PortalException {

		ServiceContext serviceContext = createServiceContext(
			companyId, enableCategorization, groupId, objectEntry, userId);

		serviceContext.setAttribute(
			"friendlyUrlMap",
			(Serializable)LocalizedMapUtil.populateI18nMap(
				LocaleUtil.toLanguageId(locale),
				objectEntry.getFriendlyUrlPath_i18n(),
				objectEntry.getFriendlyUrlPath()));
		serviceContext.setAttribute(
			"objectEntryComments", (Serializable)objectEntryComments);
		serviceContext.setCompanyId(companyId);
		serviceContext.setLanguageId(LocaleUtil.toLanguageId(locale));
		serviceContext.setModelPermissions(modelPermissions);

		return serviceContext;
	}

	public static ServiceContext createServiceContext(
			long companyId, boolean enableCategorization, long groupId,
			ObjectEntry objectEntry, long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		if (enableCategorization) {
			if (objectEntry.getTaxonomyCategoryIds() == null) {
				_setObjectEntryTaxonomyCategoryIds(
					companyId, groupId, objectEntry);
			}

			if (objectEntry.getTaxonomyCategoryIds() != null) {
				long[] assetCategoryIds = ArrayUtil.toArray(
					objectEntry.getTaxonomyCategoryIds());

				_validateAssetCategoryGroupIds(
					assetCategoryIds, companyId, groupId);

				serviceContext.setAssetCategoryIds(assetCategoryIds);
			}
		}

		if (Validator.isNotNull(objectEntry.getKeywords())) {
			serviceContext.setAssetTagNames(objectEntry.getKeywords());
		}

		serviceContext.setAttribute(
			"status", _getStatusCode(objectEntry.getStatus()));
		serviceContext.setUserId(userId);

		if (_isObjectEntryDraft(objectEntry.getStatus())) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		return serviceContext;
	}

	private static AssetVocabulary _fetchAssetVocabulary(
		long groupId, TaxonomyCategoryBrief taxonomyCategoryBrief) {

		ParentTaxonomyVocabulary parentTaxonomyVocabulary =
			taxonomyCategoryBrief.getParentTaxonomyVocabulary();

		if (parentTaxonomyVocabulary != null) {
			return AssetVocabularyLocalServiceUtil.
				fetchAssetVocabularyByExternalReferenceCode(
					parentTaxonomyVocabulary.getExternalReferenceCode(),
					groupId);
		}

		AssetCategory assetCategory =
			AssetCategoryLocalServiceUtil.
				fetchAssetCategoryByExternalReferenceCode(
					taxonomyCategoryBrief.
						getTaxonomyCategoryExternalReferenceCode(),
					groupId);

		if (assetCategory == null) {
			return null;
		}

		return AssetVocabularyLocalServiceUtil.fetchAssetVocabulary(
			assetCategory.getVocabularyId());
	}

	private static long[] _getAllowedGroupIds(long companyId, long groupId)
		throws PortalException {

		if (groupId <= 0) {
			Company company = CompanyLocalServiceUtil.getCompany(companyId);

			groupId = company.getGroupId();
		}

		return SiteConnectedGroupGroupProviderUtil.
			getCurrentAndAncestorSiteAndDepotGroupIds(groupId);
	}

	private static long _getGroupId(
		long companyId, long groupId, String externalReferenceCode,
		TaxonomyCategoryBrief taxonomyCategoryBrief) {

		Scope scope = taxonomyCategoryBrief.getScope();

		if (Validator.isNull(externalReferenceCode) || (scope == null) ||
			Validator.isNull(scope.getExternalReferenceCode())) {

			_log.error(
				StringBundler.concat(
					"Invalid asset category with external reference code  ",
					externalReferenceCode, " and group ", groupId));

			return groupId;
		}

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			scope.getExternalReferenceCode(), companyId);

		if (group == null) {
			_log.error(
				StringBundler.concat(
					"Invalid asset category with external reference code  ",
					externalReferenceCode, " and group ", groupId));

			return groupId;
		}

		return group.getGroupId();
	}

	private static int _getStatusCode(Status status) {
		if (status == null) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		return status.getCode();
	}

	private static boolean _isAssetVocabularyAvailable(
		long assetVocabularyId, long groupId) {

		List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
			AssetVocabularyGroupRelLocalServiceUtil.
				getAssetVocabularyGroupRelsByVocabularyId(assetVocabularyId);

		for (AssetVocabularyGroupRel assetVocabularyGroupRel :
				assetVocabularyGroupRels) {

			if ((assetVocabularyGroupRel.getGroupId() ==
					GroupConstants.ANY_PARENT_GROUP_ID) ||
				(assetVocabularyGroupRel.getGroupId() == groupId)) {

				return true;
			}
		}

		return false;
	}

	private static boolean _isObjectEntryDraft(Status status) {
		if ((status != null) &&
			(status.getCode() == WorkflowConstants.STATUS_DRAFT)) {

			return true;
		}

		return false;
	}

	private static void _setObjectEntryTaxonomyCategoryIds(
			long companyId, long groupId, ObjectEntry objectEntry)
		throws PortalException {

		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			objectEntry.getTaxonomyCategoryBriefs();

		if (taxonomyCategoryBriefs == null) {
			return;
		}

		if (ArrayUtil.isEmpty(taxonomyCategoryBriefs)) {
			objectEntry.setTaxonomyCategoryIds(() -> new Long[0]);

			return;
		}

		long[] groupIds = _getAllowedGroupIds(companyId, groupId);

		Set<Long> assetCategoryIds = new HashSet<>();

		for (TaxonomyCategoryBrief taxonomyCategoryBrief :
				taxonomyCategoryBriefs) {

			String externalReferenceCode =
				taxonomyCategoryBrief.
					getTaxonomyCategoryExternalReferenceCode();

			long scopeGroupId = _getGroupId(
				companyId, groupId, externalReferenceCode,
				taxonomyCategoryBrief);

			if (!ArrayUtil.contains(groupIds, scopeGroupId)) {
				AssetVocabulary assetVocabulary = _fetchAssetVocabulary(
					scopeGroupId, taxonomyCategoryBrief);

				if ((assetVocabulary == null) ||
					!_isAssetVocabularyAvailable(
						assetVocabulary.getVocabularyId(), groupId)) {

					throw new ObjectEntryGroupIdException.
						InvalidGroupIdForAssetCategoryBrief(
							externalReferenceCode);
				}
			}

			try {
				String parentTaxonomyCategoryExternalReferenceCode = null;

				ParentTaxonomyCategory parentTaxonomyCategory =
					taxonomyCategoryBrief.getParentTaxonomyCategory();

				if (parentTaxonomyCategory != null) {
					parentTaxonomyCategoryExternalReferenceCode =
						parentTaxonomyCategory.getExternalReferenceCode();
				}

				String parentTaxonomyVocabularyExternalReferenceCode = null;

				ParentTaxonomyVocabulary parentTaxonomyVocabulary =
					taxonomyCategoryBrief.getParentTaxonomyVocabulary();

				if (parentTaxonomyVocabulary != null) {
					parentTaxonomyVocabularyExternalReferenceCode =
						parentTaxonomyVocabulary.getExternalReferenceCode();
				}

				AssetCategory assetCategory =
					AssetCategoryServiceUtil.getOrAddEmptyCategoryWithAncestors(
						externalReferenceCode, scopeGroupId,
						parentTaxonomyCategoryExternalReferenceCode,
						parentTaxonomyVocabularyExternalReferenceCode);

				assetCategoryIds.add(assetCategory.getCategoryId());
			}
			catch (PortalException portalException) {
				_log.error(
					StringBundler.concat(
						"Invalid asset category with external reference code  ",
						externalReferenceCode, " and group ", scopeGroupId),
					portalException);

				throw new RuntimeException(portalException);
			}
		}

		if (SetUtil.isNotEmpty(assetCategoryIds)) {
			objectEntry.setTaxonomyCategoryIds(
				() -> assetCategoryIds.toArray(new Long[0]));
		}
	}

	private static void _validateAssetCategoryGroupIds(
			long[] assetCategoryIds, long companyId, long groupId)
		throws PortalException {

		if (ArrayUtil.isEmpty(assetCategoryIds)) {
			return;
		}

		long[] groupIds = _getAllowedGroupIds(companyId, groupId);

		for (long assetCategoryId : assetCategoryIds) {
			AssetCategory assetCategory =
				AssetCategoryLocalServiceUtil.fetchAssetCategory(
					assetCategoryId);

			if ((assetCategory == null) ||
				(!ArrayUtil.contains(groupIds, assetCategory.getGroupId()) &&
				 !_isAssetVocabularyAvailable(
					 assetCategory.getVocabularyId(), groupId))) {

				throw new ObjectEntryGroupIdException.
					InvalidGroupIdForAssetCategory(assetCategoryId);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceContextUtil.class);

}