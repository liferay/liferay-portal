/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Scope;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.HashSet;
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
		long companyId, long groupId, Locale locale,
		ModelPermissions modelPermissions, ObjectEntry objectEntry,
		long userId) {

		ServiceContext serviceContext = createServiceContext(
			companyId, groupId, objectEntry, userId);

		if (FeatureFlagManagerUtil.isEnabled("LPD-21926")) {
			serviceContext.setAttribute(
				"friendlyUrlMap",
				(Serializable)LocalizedMapUtil.populateI18nMap(
					LocaleUtil.toLanguageId(locale),
					objectEntry.getFriendlyUrlPath_i18n(),
					objectEntry.getFriendlyUrlPath()));
		}

		serviceContext.setCompanyId(companyId);
		serviceContext.setLanguageId(LocaleUtil.toLanguageId(locale));
		serviceContext.setModelPermissions(modelPermissions);

		return serviceContext;
	}

	public static ServiceContext createServiceContext(
		long companyId, long groupId, ObjectEntry objectEntry, long userId) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		_setObjectEntryTaxonomyCategoryIds(
			companyId, groupId, userId, objectEntry);

		if (Validator.isNotNull(objectEntry.getTaxonomyCategoryIds())) {
			serviceContext.setAssetCategoryIds(
				ArrayUtil.toArray(objectEntry.getTaxonomyCategoryIds()));
		}

		if (Validator.isNotNull(objectEntry.getKeywords())) {
			serviceContext.setAssetTagNames(objectEntry.getKeywords());
		}

		serviceContext.setUserId(userId);

		if (_isObjectEntryDraft(objectEntry.getStatus())) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		return serviceContext;
	}

	private static long _getGroupId(
		long companyId, long groupId, String externalReferenceCode,
		TaxonomyCategoryBrief taxonomyCategoryBrief) {

		if (groupId != 0) {
			return groupId;
		}

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

	private static boolean _isObjectEntryDraft(Status status) {
		if ((status != null) &&
			(status.getCode() == WorkflowConstants.STATUS_DRAFT)) {

			return true;
		}

		return false;
	}

	private static void _setObjectEntryTaxonomyCategoryIds(
		long companyId, long groupId, long userId, ObjectEntry objectEntry) {

		TaxonomyCategoryBrief[] taxonomyCategoryBriefs =
			objectEntry.getTaxonomyCategoryBriefs();

		if ((taxonomyCategoryBriefs == null) ||
			!FeatureFlagManagerUtil.isEnabled("LPD-47858")) {

			return;
		}

		if (ArrayUtil.isEmpty(taxonomyCategoryBriefs)) {
			objectEntry.setTaxonomyCategoryIds(() -> new Long[0]);
		}

		Set<Long> assetCategoryIds = new HashSet<>();

		for (TaxonomyCategoryBrief taxonomyCategoryBrief :
				taxonomyCategoryBriefs) {

			String externalReferenceCode =
				taxonomyCategoryBrief.
					getTaxonomyCategoryExternalReferenceCode();

			groupId = _getGroupId(
				companyId, groupId, externalReferenceCode,
				taxonomyCategoryBrief);

			try {
				AssetCategory assetCategory =
					AssetCategoryLocalServiceUtil.getOrAddIncompleteCategory(
						externalReferenceCode, userId, groupId);

				assetCategoryIds.add(assetCategory.getCategoryId());
			}
			catch (PortalException portalException) {
				_log.error(
					StringBundler.concat(
						"Invalid asset category with external reference code  ",
						externalReferenceCode, " and group ", groupId),
					portalException);

				throw new RuntimeException(portalException);
			}
		}

		if (SetUtil.isNotEmpty(assetCategoryIds)) {
			objectEntry.setTaxonomyCategoryIds(
				() -> assetCategoryIds.toArray(new Long[0]));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceContextUtil.class);

}