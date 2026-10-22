/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.batch.engine.util;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class LayoutSetPrototypeMergeUtil {

	public static LayoutSet fetchPrivateLayoutSet() {
		if (!MergeLayoutPrototypesThreadLocal.isInProgress()) {
			return null;
		}

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				fetchExportImportConfiguration(
					GetterUtil.getLong(
						ExportImportThreadLocal.
							getExportImportConfigurationId()));

		if (exportImportConfiguration == null) {
			return null;
		}

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		if (!GetterUtil.getBoolean(settingsMap.get("privateLayout"))) {
			return null;
		}

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.fetchLayoutSet(
			GetterUtil.getLong(settingsMap.get("targetGroupId")), true);

		if ((layoutSet == null) ||
			!layoutSet.isLayoutSetPrototypeLinkActive()) {

			return null;
		}

		return layoutSet;
	}

	public static String getLayoutExternalReferenceCode(
		String externalReferenceCode) {

		LayoutSet layoutSet = fetchPrivateLayoutSet();

		if (layoutSet == null) {
			return externalReferenceCode;
		}

		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				fetchLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		if (layoutSetPrototype == null) {
			return externalReferenceCode;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(
			layoutSetPrototype.getCompanyId(),
			PortalUtil.getClassNameId(LayoutSetPrototype.class),
			layoutSetPrototype.getLayoutSetPrototypeId());

		if ((group == null) ||
			!_isLayoutSetPrototypeLayoutExternalReferenceCode(
				externalReferenceCode, group.getGroupId())) {

			return externalReferenceCode;
		}

		String layoutExternalReferenceCode =
			externalReferenceCode +
				LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_PRIVATE;

		if (layoutExternalReferenceCode.length() <= ModelHintsUtil.getMaxLength(
				Layout.class.getName(), "externalReferenceCode")) {

			return layoutExternalReferenceCode;
		}

		return PortalUUIDUtil.generate(layoutExternalReferenceCode.getBytes());
	}

	private static boolean _isLayoutSetPrototypeLayoutExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		if (Validator.isNull(externalReferenceCode)) {
			return false;
		}

		if (!_isSystemLayout(
				LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
					externalReferenceCode, groupId))) {

			return true;
		}

		SegmentsExperience segmentsExperience =
			SegmentsExperienceLocalServiceUtil.
				fetchSegmentsExperienceByExternalReferenceCode(
					externalReferenceCode, groupId);

		if ((segmentsExperience != null) &&
			!_isSystemLayout(
				LayoutLocalServiceUtil.fetchLayout(
					segmentsExperience.getPlid()))) {

			return true;
		}

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					externalReferenceCode, groupId);

		if ((fragmentEntryLink != null) &&
			!_isSystemLayout(
				LayoutLocalServiceUtil.fetchLayout(
					fragmentEntryLink.getPlid()))) {

			return true;
		}

		return false;
	}

	private static boolean _isSystemLayout(Layout layout) {
		if ((layout != null) && layout.isDraftLayout()) {
			layout = LayoutLocalServiceUtil.fetchLayout(layout.getClassPK());
		}

		if ((layout == null) || layout.isSystem()) {
			return true;
		}

		return false;
	}

}