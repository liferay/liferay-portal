/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.service.impl;

import com.liferay.layout.seo.exception.NoSuchEntryException;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTagProperty;
import com.liferay.layout.seo.service.base.LayoutSEOEntryLocalServiceBaseImpl;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryCustomMetaTagPersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.layout.seo.model.LayoutSEOEntry",
	service = AopService.class
)
public class LayoutSEOEntryLocalServiceImpl
	extends LayoutSEOEntryLocalServiceBaseImpl {

	@Override
	public LayoutSEOEntry copyLayoutSEOEntry(
			long userId, long groupId, boolean privateLayout,
			long targetLayoutId, LayoutSEOEntry sourceLayoutSEOEntry,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSEOEntry targetLayoutSEOEntry =
			layoutSEOEntryPersistence.fetchByG_P_L(
				groupId, privateLayout, targetLayoutId);

		if (targetLayoutSEOEntry == null) {
			targetLayoutSEOEntry = _addLayoutSEOEntry(
				userId, groupId, privateLayout, targetLayoutId,
				sourceLayoutSEOEntry.isCanonicalURLEnabled(),
				sourceLayoutSEOEntry.getCanonicalURLMap(), new ArrayList<>(),
				sourceLayoutSEOEntry.isOpenGraphDescriptionEnabled(),
				sourceLayoutSEOEntry.getOpenGraphDescriptionMap(),
				sourceLayoutSEOEntry.getOpenGraphImageAltMap(),
				sourceLayoutSEOEntry.getOpenGraphImageFileEntryERC(),
				sourceLayoutSEOEntry.getOpenGraphImageFileEntryScopeERC(),
				sourceLayoutSEOEntry.isOpenGraphTitleEnabled(),
				sourceLayoutSEOEntry.getOpenGraphTitleMap(), serviceContext);
		}
		else {
			targetLayoutSEOEntry = updateLayoutSEOEntry(
				userId, groupId, privateLayout, targetLayoutId,
				sourceLayoutSEOEntry.isCanonicalURLEnabled(),
				sourceLayoutSEOEntry.getCanonicalURLMap(),
				sourceLayoutSEOEntry.isOpenGraphDescriptionEnabled(),
				sourceLayoutSEOEntry.getOpenGraphDescriptionMap(),
				sourceLayoutSEOEntry.getOpenGraphImageAltMap(),
				sourceLayoutSEOEntry.getOpenGraphImageFileEntryERC(),
				sourceLayoutSEOEntry.getOpenGraphImageFileEntryScopeERC(),
				sourceLayoutSEOEntry.isOpenGraphTitleEnabled(),
				sourceLayoutSEOEntry.getOpenGraphTitleMap(), serviceContext);
		}

		_addLayoutSEOEntryCustomMetaTag(
			targetLayoutSEOEntry.getCompanyId(), groupId,
			targetLayoutSEOEntry.getLayoutSEOEntryId(),
			TransformUtil.transform(
				_layoutSEOEntryCustomMetaTagPersistence.findByG_L(
					groupId, sourceLayoutSEOEntry.getLayoutSEOEntryId()),
				layoutSEOEntryCustomMetaTag ->
					new LayoutSEOEntryCustomMetaTagProperty(
						layoutSEOEntryCustomMetaTag.getContentMap(),
						layoutSEOEntryCustomMetaTag.getProperty())));

		return targetLayoutSEOEntry;
	}

	@Override
	public LayoutSEOEntry deleteLayoutSEOEntry(LayoutSEOEntry layoutSEOEntry) {
		layoutSEOEntryPersistence.remove(layoutSEOEntry);

		// Layout SEO entry custom meta tags

		_layoutSEOEntryCustomMetaTagPersistence.removeByG_L(
			layoutSEOEntry.getGroupId(), layoutSEOEntry.getLayoutSEOEntryId());

		return layoutSEOEntry;
	}

	@Override
	public void deleteLayoutSEOEntry(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchEntryException {

		layoutSEOEntryLocalService.deleteLayoutSEOEntry(
			layoutSEOEntryPersistence.findByG_P_L(
				groupId, privateLayout, layoutId));
	}

	@Override
	public void deleteLayoutSEOEntry(String uuid, long groupId)
		throws NoSuchEntryException {

		layoutSEOEntryLocalService.deleteLayoutSEOEntry(
			layoutSEOEntryPersistence.findByUUID_G(uuid, groupId));
	}

	@Override
	public LayoutSEOEntry fetchLayoutSEOEntry(Layout layout) {
		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			layout = virtualLayout.getSourceLayout();
		}

		return layoutSEOEntryPersistence.fetchByG_P_L(
			layout.getGroupId(), layout.isPrivateLayout(),
			layout.getLayoutId());
	}

	@Override
	public LayoutSEOEntry fetchLayoutSEOEntry(
		long groupId, boolean privateLayout, long layoutId) {

		return layoutSEOEntryPersistence.fetchByG_P_L(
			groupId, privateLayout, layoutId);
	}

	@Override
	public List<LayoutSEOEntry> getLayoutSEOEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return layoutSEOEntryPersistence.findByUuid_C(uuid, companyId);
	}

	@Override
	public List<LayoutSEOEntryCustomMetaTag> getLayoutSEOEntryCustomMetaTags(
		long groupId, long layoutSEOEntryId) {

		return _layoutSEOEntryCustomMetaTagPersistence.findByG_L(
			groupId, layoutSEOEntryId);
	}

	@Override
	public LayoutSEOEntry updateCustomMetaTags(
			long userId, long groupId, boolean privateLayout, long layoutId,
			List<LayoutSEOEntryCustomMetaTagProperty>
				layoutSEOEntryCustomMetaTagProperties,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSEOEntry layoutSEOEntry = layoutSEOEntryPersistence.fetchByG_P_L(
			groupId, privateLayout, layoutId);

		if (layoutSEOEntry == null) {
			return _addLayoutSEOEntry(
				userId, groupId, privateLayout, layoutId, false,
				Collections.emptyMap(), layoutSEOEntryCustomMetaTagProperties,
				false, Collections.emptyMap(), Collections.emptyMap(), null,
				null, false, Collections.emptyMap(), serviceContext);
		}

		_addLayoutSEOEntryCustomMetaTag(
			layoutSEOEntry.getCompanyId(), groupId,
			layoutSEOEntry.getLayoutSEOEntryId(),
			layoutSEOEntryCustomMetaTagProperties);

		layoutSEOEntry.setModifiedDate(DateUtil.newDate());

		return layoutSEOEntryPersistence.update(layoutSEOEntry);
	}

	@Override
	public LayoutSEOEntry updateLayoutSEOEntry(
			long userId, long groupId, boolean privateLayout, long layoutId,
			boolean canonicalURLEnabled, Map<Locale, String> canonicalURLMap,
			boolean openGraphDescriptionEnabled,
			Map<Locale, String> openGraphDescriptionMap,
			Map<Locale, String> openGraphImageAltMap,
			String openGraphImageFileEntryERC,
			String openGraphImageFileEntryScopeERC,
			boolean openGraphTitleEnabled,
			Map<Locale, String> openGraphTitleMap,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSEOEntry layoutSEOEntry = layoutSEOEntryPersistence.fetchByG_P_L(
			groupId, privateLayout, layoutId);

		if (layoutSEOEntry == null) {
			return _addLayoutSEOEntry(
				userId, groupId, privateLayout, layoutId, canonicalURLEnabled,
				canonicalURLMap, new ArrayList<>(), openGraphDescriptionEnabled,
				openGraphDescriptionMap, openGraphImageAltMap,
				openGraphImageFileEntryERC, openGraphImageFileEntryScopeERC,
				openGraphTitleEnabled, openGraphTitleMap, serviceContext);
		}

		layoutSEOEntry.setModifiedDate(DateUtil.newDate());
		layoutSEOEntry.setCanonicalURLMap(canonicalURLMap);
		layoutSEOEntry.setCanonicalURLEnabled(canonicalURLEnabled);
		layoutSEOEntry.setOpenGraphDescriptionMap(openGraphDescriptionMap);
		layoutSEOEntry.setOpenGraphDescriptionEnabled(
			openGraphDescriptionEnabled);

		if (Validator.isNotNull(openGraphImageFileEntryERC)) {
			layoutSEOEntry.setOpenGraphImageAltMap(openGraphImageAltMap);
		}
		else {
			layoutSEOEntry.setOpenGraphImageAltMap(Collections.emptyMap());
		}

		layoutSEOEntry.setOpenGraphImageFileEntryERC(
			openGraphImageFileEntryERC);
		layoutSEOEntry.setOpenGraphImageFileEntryScopeERC(
			openGraphImageFileEntryScopeERC);
		layoutSEOEntry.setOpenGraphTitleMap(openGraphTitleMap);
		layoutSEOEntry.setOpenGraphTitleEnabled(openGraphTitleEnabled);

		return layoutSEOEntryPersistence.update(layoutSEOEntry);
	}

	@Override
	public LayoutSEOEntry updateLayoutSEOEntry(
			long userId, long groupId, boolean privateLayout, long layoutId,
			boolean openGraphDescriptionEnabled,
			Map<Locale, String> openGraphDescriptionMap,
			Map<Locale, String> openGraphImageAltMap,
			String openGraphImageFileEntryERC,
			String openGraphImageFileEntryScopeERC,
			boolean openGraphTitleEnabled,
			Map<Locale, String> openGraphTitleMap,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSEOEntry layoutSEOEntry = layoutSEOEntryPersistence.fetchByG_P_L(
			groupId, privateLayout, layoutId);

		if (layoutSEOEntry == null) {
			return _addLayoutSEOEntry(
				userId, groupId, privateLayout, layoutId, false,
				Collections.emptyMap(), new ArrayList<>(),
				openGraphDescriptionEnabled, openGraphDescriptionMap,
				openGraphImageAltMap, openGraphImageFileEntryERC,
				openGraphImageFileEntryScopeERC, openGraphTitleEnabled,
				openGraphTitleMap, serviceContext);
		}

		layoutSEOEntry.setModifiedDate(DateUtil.newDate());

		layoutSEOEntry.setOpenGraphDescriptionMap(openGraphDescriptionMap);
		layoutSEOEntry.setOpenGraphDescriptionEnabled(
			openGraphDescriptionEnabled);

		if (Validator.isNotNull(openGraphImageFileEntryERC)) {
			layoutSEOEntry.setOpenGraphImageAltMap(openGraphImageAltMap);
		}
		else {
			layoutSEOEntry.setOpenGraphImageAltMap(Collections.emptyMap());
		}

		layoutSEOEntry.setOpenGraphImageFileEntryERC(
			openGraphImageFileEntryERC);
		layoutSEOEntry.setOpenGraphImageFileEntryScopeERC(
			openGraphImageFileEntryScopeERC);
		layoutSEOEntry.setOpenGraphTitleMap(openGraphTitleMap);
		layoutSEOEntry.setOpenGraphTitleEnabled(openGraphTitleEnabled);

		return layoutSEOEntryPersistence.update(layoutSEOEntry);
	}

	@Override
	public LayoutSEOEntry updateLayoutSEOEntry(
			long userId, long groupId, boolean privateLayout, long layoutId,
			boolean canonicalURLEnabled, Map<Locale, String> canonicalURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSEOEntry layoutSEOEntry = layoutSEOEntryPersistence.fetchByG_P_L(
			groupId, privateLayout, layoutId);

		if (layoutSEOEntry == null) {
			return _addLayoutSEOEntry(
				userId, groupId, privateLayout, layoutId, canonicalURLEnabled,
				canonicalURLMap, new ArrayList<>(), false,
				Collections.emptyMap(), Collections.emptyMap(), null, null,
				false, Collections.emptyMap(), serviceContext);
		}

		layoutSEOEntry.setModifiedDate(DateUtil.newDate());
		layoutSEOEntry.setCanonicalURLMap(canonicalURLMap);
		layoutSEOEntry.setCanonicalURLEnabled(canonicalURLEnabled);

		return layoutSEOEntryPersistence.update(layoutSEOEntry);
	}

	private LayoutSEOEntry _addLayoutSEOEntry(
			long userId, long groupId, boolean privateLayout, long layoutId,
			boolean canonicalURLEnabled, Map<Locale, String> canonicalURLMap,
			List<LayoutSEOEntryCustomMetaTagProperty>
				layoutSEOEntryCustomMetaTagProperties,
			boolean openGraphDescriptionEnabled,
			Map<Locale, String> openGraphDescriptionMap,
			Map<Locale, String> openGraphImageAltMap,
			String openGraphImageFileEntryERC,
			String openGraphImageFileEntryScopeERC,
			boolean openGraphTitleEnabled,
			Map<Locale, String> openGraphTitleMap,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSEOEntry layoutSEOEntry = layoutSEOEntryPersistence.create(
			counterLocalService.increment());

		layoutSEOEntry.setUuid(serviceContext.getUuid());
		layoutSEOEntry.setGroupId(groupId);

		Group group = _groupLocalService.getGroup(groupId);

		layoutSEOEntry.setCompanyId(group.getCompanyId());

		layoutSEOEntry.setUserId(userId);

		Date date = DateUtil.newDate();

		layoutSEOEntry.setCreateDate(date);
		layoutSEOEntry.setModifiedDate(date);

		layoutSEOEntry.setPrivateLayout(privateLayout);
		layoutSEOEntry.setLayoutId(layoutId);
		layoutSEOEntry.setCanonicalURLMap(canonicalURLMap);
		layoutSEOEntry.setCanonicalURLEnabled(canonicalURLEnabled);
		layoutSEOEntry.setOpenGraphDescriptionMap(openGraphDescriptionMap);
		layoutSEOEntry.setOpenGraphDescriptionEnabled(
			openGraphDescriptionEnabled);

		if (Validator.isNotNull(openGraphImageFileEntryERC)) {
			layoutSEOEntry.setOpenGraphImageAltMap(openGraphImageAltMap);
		}

		layoutSEOEntry.setOpenGraphImageFileEntryERC(
			openGraphImageFileEntryERC);
		layoutSEOEntry.setOpenGraphImageFileEntryScopeERC(
			openGraphImageFileEntryScopeERC);
		layoutSEOEntry.setOpenGraphTitleMap(openGraphTitleMap);
		layoutSEOEntry.setOpenGraphTitleEnabled(openGraphTitleEnabled);

		_addLayoutSEOEntryCustomMetaTag(
			group.getCompanyId(), groupId, layoutSEOEntry.getLayoutSEOEntryId(),
			layoutSEOEntryCustomMetaTagProperties);

		return layoutSEOEntryPersistence.update(layoutSEOEntry);
	}

	private void _addLayoutSEOEntryCustomMetaTag(
		long companyId, long groupId, long layoutSEOEntryId,
		List<LayoutSEOEntryCustomMetaTagProperty>
			layoutSEOEntryCustomMetaTagProperties) {

		_layoutSEOEntryCustomMetaTagPersistence.removeByG_L(
			groupId, layoutSEOEntryId);

		for (LayoutSEOEntryCustomMetaTagProperty
				layoutSEOEntryCustomMetaTagProperty :
					layoutSEOEntryCustomMetaTagProperties) {

			LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag =
				_layoutSEOEntryCustomMetaTagPersistence.create(
					counterLocalService.increment());

			layoutSEOEntryCustomMetaTag.setGroupId(groupId);
			layoutSEOEntryCustomMetaTag.setCompanyId(companyId);
			layoutSEOEntryCustomMetaTag.setLayoutSEOEntryId(layoutSEOEntryId);
			layoutSEOEntryCustomMetaTag.setContentMap(
				layoutSEOEntryCustomMetaTagProperty.getContentMap());
			layoutSEOEntryCustomMetaTag.setProperty(
				layoutSEOEntryCustomMetaTagProperty.getProperty());

			_layoutSEOEntryCustomMetaTagPersistence.update(
				layoutSEOEntryCustomMetaTag);
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSEOEntryCustomMetaTagPersistence
		_layoutSEOEntryCustomMetaTagPersistence;

}