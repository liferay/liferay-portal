/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollectionTable;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntryTable;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateEntryServiceBaseImpl;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"json.web.service.context.name=layout",
		"json.web.service.context.path=LayoutPageTemplateEntry"
	},
	service = AopService.class
)
public class LayoutPageTemplateEntryServiceImpl
	extends LayoutPageTemplateEntryServiceBaseImpl {

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, int type, long previewFileEntryId,
			boolean defaultTemplate, long layoutPrototypeId, long plid,
			long masterLayoutPlid, int status, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);

		return layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			externalReferenceCode, getUserId(), groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
			classNameId, classTypeId, name, type, previewFileEntryId,
			defaultTemplate, layoutPrototypeId, plid, masterLayoutPlid, status,
			serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, long masterLayoutPlid, int status,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);

		return layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			externalReferenceCode, getUserId(), groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey,
			classNameId, classTypeId, name,
			LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, masterLayoutPlid,
			status, serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, String name, int type,
			long masterLayoutPlid, int status, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);

		return layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			externalReferenceCode, getUserId(), groupId,
			layoutPageTemplateCollectionId, layoutPageTemplateEntryKey, name,
			type, masterLayoutPlid, status, serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry copyLayoutPageTemplateEntry(
			long groupId, long layoutPageTemplateCollectionId,
			long sourceLayoutPageTemplateEntryId, boolean copyPermissions,
			ServiceContext serviceContext)
		throws Exception {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);

		return layoutPageTemplateEntryLocalService.copyLayoutPageTemplateEntry(
			getUserId(), groupId, layoutPageTemplateCollectionId,
			sourceLayoutPageTemplateEntryId, copyPermissions, serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry createLayoutPageTemplateEntryFromLayout(
			long segmentsExperienceId, Layout sourceLayout, String name,
			long targetLayoutPageTemplateCollectionId,
			ServiceContext serviceContext)
		throws Exception {

		if (!sourceLayout.isTypeContent()) {
			throw new UnsupportedOperationException();
		}

		_portletResourcePermission.check(
			getPermissionChecker(), sourceLayout.getGroupId(),
			LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);

		LayoutPageTemplateCollection targetLayoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				getLayoutPageTemplateCollection(
					targetLayoutPageTemplateCollectionId);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, getUserId(), sourceLayout.getGroupId(),
				targetLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				null, 0, 0, name, LayoutPageTemplateEntryTypeConstants.BASIC, 0,
				false, 0, 0, sourceLayout.getMasterLayoutPlid(),
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout targetLayout = layout.fetchDraftLayout();

		_layoutLocalService.copyLayoutContent(
			segmentsExperienceId, sourceLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				targetLayout.getPlid()),
			targetLayout);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layout.getPlid());

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		UnicodeProperties typeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_PUBLISHED,
			Boolean.FALSE.toString());

		draftLayout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		_layoutLocalService.updateLayout(draftLayout);

		return layoutPageTemplateEntry;
	}

	@Override
	public void deleteLayoutPageTemplateEntries(
			long[] layoutPageTemplateEntryIds)
		throws PortalException {

		for (long layoutPageTemplateEntryId : layoutPageTemplateEntryIds) {
			_layoutPageTemplateEntryModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateEntryId,
				ActionKeys.DELETE);

			layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntryId);
		}
	}

	@Override
	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException {

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntryId,
			ActionKeys.DELETE);

		return layoutPageTemplateEntryLocalService.
			deleteLayoutPageTemplateEntry(layoutPageTemplateEntryId);
	}

	@Override
	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryPersistence.findByERC_G(
				externalReferenceCode, groupId);

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			ActionKeys.DELETE);

		return layoutPageTemplateEntryLocalService.
			deleteLayoutPageTemplateEntry(layoutPageTemplateEntry);
	}

	@Override
	public LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, int type, int status) {

		return layoutPageTemplateEntryPersistence.fetchByG_T_D_S_First(
			groupId, type, true, status, null);
	}

	@Override
	public LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, long classNameId, long classTypeId) {

		return layoutPageTemplateEntryPersistence.fetchByG_C_C_D_First(
			groupId, classNameId, classTypeId, true, null);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				layoutPageTemplateEntryId);

		if (layoutPageTemplateEntry != null) {
			_layoutPageTemplateEntryModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateEntry,
				ActionKeys.VIEW);
		}

		return layoutPageTemplateEntry;
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				groupId, layoutPageTemplateCollectionId, name, type);

		if (layoutPageTemplateEntry != null) {
			_layoutPageTemplateEntryModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateEntry,
				ActionKeys.VIEW);
		}

		return layoutPageTemplateEntry;
	}

	@Override
	public LayoutPageTemplateEntry
			fetchLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (layoutPageTemplateEntry != null) {
			_layoutPageTemplateEntryModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateEntry,
				ActionKeys.VIEW);
		}

		return layoutPageTemplateEntry;
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return layoutPageTemplateEntryLocalService.
			fetchLayoutPageTemplateEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public List<Object> getLayoutPageCollectionsAndLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<Object> orderByComparator) {

		Table<?> layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable =
			_getLayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable(
				groupId, layoutPageTemplateCollectionId, 0, 0, StringPool.BLANK,
				type, -1);

		return _getLayoutPageTemplateCollectionAndLayoutPageTemplateEntries(
			DSLQueryFactoryUtil.select(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable
			).from(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable
			).orderBy(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable,
				orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public List<Object> getLayoutPageCollectionsAndLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, long classNameId,
		long classTypeId, String name, int type, int status, int start, int end,
		OrderByComparator<Object> orderByComparator) {

		Table<?> layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable =
			_getLayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable(
				groupId, layoutPageTemplateCollectionId, classNameId,
				classTypeId, name, type, status);

		return _getLayoutPageTemplateCollectionAndLayoutPageTemplateEntries(
			DSLQueryFactoryUtil.select(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable
			).from(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable
			).orderBy(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable,
				orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		Table<?> layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable =
			_getLayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable(
				groupId, layoutPageTemplateCollectionId, 0, 0, StringPool.BLANK,
				type, -1);

		return layoutPageTemplateEntryPersistence.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable.
					getColumn("layoutPageTemplateEntryId")
			).from(
				layoutPageTemplateCollectionAndLayoutPageTemplateEntryTable
			));
	}

	@Override
	public int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, long classNameId,
		long classTypeId, String name, int type, int status) {

		return layoutPageTemplateEntryPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				_getLayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable(
					groupId, layoutPageTemplateCollectionId, classNameId,
					classTypeId, name, type, status)
			));
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int status) {

		return layoutPageTemplateEntryPersistence.filterFindByG_S(
			groupId, status);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, new int[] {type}, status, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, new int[] {type}, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_T(
				groupId, types, start, end, orderByComparator);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_T_S(
			groupId, types, status, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, types, WorkflowConstants.STATUS_ANY, start, end,
			orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_L(
				groupId, layoutPageTemplateCollectionId);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return layoutPageTemplateEntryPersistence.filterFindByG_C_T_D(
			groupId, classNameId, type, defaultTemplate);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId,
			WorkflowConstants.STATUS_ANY, start, end);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_L(
				groupId, layoutPageTemplateCollectionId, start, end);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_L(
				groupId, layoutPageTemplateCollectionId, start, end,
				orderByComparator);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end,
			orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId,
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type) {

		return getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, type,
			WorkflowConstants.STATUS_ANY);
	}

	@Override
	@ThreadLocalCachable
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_C_C_T(
				groupId, classNameId, classTypeId, type);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_C_C_T_S(
			groupId, classNameId, classTypeId, type, status);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_C_C_T(
				groupId, classNameId, classTypeId, type, start, end,
				orderByComparator);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_C_C_T_S(
			groupId, classNameId, classTypeId, type, status, start, end,
			orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, type,
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_C_C_LikeN_T(
				groupId, classNameId, classTypeId,
				_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
				type, start, end, orderByComparator);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], type,
			status, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, name, type,
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_L_LikeN(
				groupId, layoutPageTemplateCollectionId,
				_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
				start, end, orderByComparator);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], status,
			start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, name,
			WorkflowConstants.STATUS_ANY, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, name, new int[] {type}, status, start, end,
			orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, name, new int[] {type}, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterFindByG_T_LikeN(
				groupId,
				_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
				types, start, end, orderByComparator);
		}

		return layoutPageTemplateEntryPersistence.filterFindByG_T_LikeN_S(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			types, status, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getLayoutPageTemplateEntries(
			groupId, name, types, WorkflowConstants.STATUS_ANY, start, end,
			orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntriesByType(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return layoutPageTemplateEntryPersistence.filterFindByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end,
			orderByComparator);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(long groupId, int type) {
		return getLayoutPageTemplateEntriesCount(
			groupId, new int[] {type}, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, int type, int status) {

		return getLayoutPageTemplateEntriesCount(
			groupId, new int[] {type}, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(long groupId, int[] types) {
		return getLayoutPageTemplateEntriesCount(
			groupId, types, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, int[] types, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterCountByG_T(
				groupId, types);
		}

		return layoutPageTemplateEntryPersistence.filterCountByG_T_S(
			groupId, types, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId) {

		return getLayoutPageTemplateEntriesCount(
			groupId, layoutPageTemplateCollectionId,
			WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterCountByG_L(
				groupId, layoutPageTemplateCollectionId);
		}

		return layoutPageTemplateEntryPersistence.filterCountByG_L_S(
			groupId, layoutPageTemplateCollectionId, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, int type) {

		return getLayoutPageTemplateEntriesCount(
			groupId, classNameId, classTypeId, type,
			WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, int type,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterCountByG_C_C_T(
				groupId, classNameId, classTypeId, type);
		}

		return layoutPageTemplateEntryPersistence.filterCountByG_C_C_T_S(
			groupId, classNameId, classTypeId, type, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, String name,
		int type) {

		return getLayoutPageTemplateEntriesCount(
			groupId, classNameId, classTypeId, name, type,
			WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.
				filterCountByG_C_C_LikeN_T(
					groupId, classNameId, classTypeId,
					_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
					type);
		}

		return layoutPageTemplateEntryPersistence.filterCountByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], type,
			status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return getLayoutPageTemplateEntriesCount(
			groupId, layoutPageTemplateCollectionId, name,
			WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterCountByG_L_LikeN(
				groupId, layoutPageTemplateCollectionId,
				_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
		}

		return layoutPageTemplateEntryPersistence.filterCountByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int type) {

		return getLayoutPageTemplateEntriesCount(
			groupId, name, new int[] {type});
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int type, int status) {

		return getLayoutPageTemplateEntriesCount(
			groupId, name, new int[] {type}, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int[] types) {

		return getLayoutPageTemplateEntriesCount(
			groupId, name, types, WorkflowConstants.STATUS_ANY);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int[] types, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return layoutPageTemplateEntryPersistence.filterCountByG_T_LikeN(
				groupId,
				_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
				types);
		}

		return layoutPageTemplateEntryPersistence.filterCountByG_T_LikeN_S(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			types, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCountByType(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return layoutPageTemplateEntryPersistence.filterCountByG_L_T(
			groupId, layoutPageTemplateCollectionId, type);
	}

	@Override
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntryId);

		if (layoutPageTemplateEntry != null) {
			_layoutPageTemplateEntryModelResourcePermission.check(
				getPermissionChecker(), layoutPageTemplateEntry,
				ActionKeys.VIEW);
		}

		return layoutPageTemplateEntry;
	}

	@Override
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long groupId, String layoutPageTemplateEntryKey)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				groupId, layoutPageTemplateEntryKey);

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntry, ActionKeys.VIEW);

		return layoutPageTemplateEntry;
	}

	@Override
	public LayoutPageTemplateEntry
			getLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntry, ActionKeys.VIEW);

		return layoutPageTemplateEntry;
	}

	@Override
	public LayoutPageTemplateEntry moveLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId,
			long targetLayoutPageTemplateCollectionId)
		throws PortalException {

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntryId,
			ActionKeys.UPDATE);

		return layoutPageTemplateEntryLocalService.moveLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, targetLayoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, boolean defaultTemplate)
		throws PortalException {

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntryId,
			ActionKeys.UPDATE);

		return layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, defaultTemplate);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long previewFileEntryId)
		throws PortalException {

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntryId,
			ActionKeys.UPDATE);

		return layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, previewFileEntryId);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long classNameId, long classTypeId)
		throws PortalException {

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntryId,
			ActionKeys.UPDATE);

		return layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(
				layoutPageTemplateEntryId, classNameId, classTypeId);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, String name)
		throws PortalException {

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntryId,
			ActionKeys.UPDATE);

		return layoutPageTemplateEntryLocalService.
			updateLayoutPageTemplateEntry(layoutPageTemplateEntryId, name);
	}

	@Override
	public LayoutPageTemplateEntry updateStatus(
			long layoutPageTemplateEntryId, int status)
		throws PortalException {

		_layoutPageTemplateEntryModelResourcePermission.check(
			getPermissionChecker(), layoutPageTemplateEntryId,
			ActionKeys.UPDATE);

		return layoutPageTemplateEntryLocalService.updateStatus(
			getUserId(), layoutPageTemplateEntryId, status);
	}

	private List<Object>
		_getLayoutPageTemplateCollectionAndLayoutPageTemplateEntries(
			DSLQuery dslQuery) {

		List<Object> layoutPageTemplateEntriesAndLayoutPageTemplateCollections =
			new ArrayList<>();

		for (Object[] array :
				layoutPageTemplateEntryPersistence.<List<Object[]>>dslQuery(
					dslQuery)) {

			long layoutPageTemplateEntryId = GetterUtil.getLong(array[0]);

			if (layoutPageTemplateEntryId > 0) {
				layoutPageTemplateEntriesAndLayoutPageTemplateCollections.add(
					_layoutPageTemplateEntryLocalService.
						fetchLayoutPageTemplateEntry(
							layoutPageTemplateEntryId));

				continue;
			}

			layoutPageTemplateEntriesAndLayoutPageTemplateCollections.add(
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollection(
						GetterUtil.getLong(array[1])));
		}

		return layoutPageTemplateEntriesAndLayoutPageTemplateCollections;
	}

	private Table<?>
		_getLayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable(
			long groupId, long layoutPageTemplateCollectionId, long classNameId,
			long classTypeId, String name, int type, int status) {

		return DSLQueryFactoryUtil.select(
			LayoutPageTemplateEntryTable.INSTANCE.layoutPageTemplateEntryId,
			new Scalar<>(
				0L
			).as(
				"layoutPageTemplateCollectionId"
			),
			LayoutPageTemplateEntryTable.INSTANCE.name,
			LayoutPageTemplateEntryTable.INSTANCE.createDate,
			LayoutPageTemplateEntryTable.INSTANCE.modifiedDate
		).from(
			LayoutPageTemplateEntryTable.INSTANCE
		).where(
			LayoutPageTemplateEntryTable.INSTANCE.groupId.eq(
				groupId
			).and(
				() -> {
					if (layoutPageTemplateCollectionId < 0) {
						return null;
					}

					return LayoutPageTemplateEntryTable.INSTANCE.
						layoutPageTemplateCollectionId.eq(
							layoutPageTemplateCollectionId);
				}
			).and(
				() -> {
					if (classNameId <= 0) {
						return null;
					}

					return LayoutPageTemplateEntryTable.INSTANCE.classNameId.eq(
						classNameId);
				}
			).and(
				() -> {
					if (classTypeId <= 0) {
						return null;
					}

					return LayoutPageTemplateEntryTable.INSTANCE.classTypeId.eq(
						classTypeId);
				}
			).and(
				() -> {
					if (Validator.isNull(name)) {
						return null;
					}

					return LayoutPageTemplateEntryTable.INSTANCE.name.like(
						StringUtil.quote(name, CharPool.PERCENT));
				}
			).and(
				() -> {
					if (status < 0) {
						return null;
					}

					return LayoutPageTemplateEntryTable.INSTANCE.status.eq(
						status);
				}
			).and(
				LayoutPageTemplateEntryTable.INSTANCE.type.eq(type)
			)
		).unionAll(
			DSLQueryFactoryUtil.select(
				new Scalar<>(
					0L
				).as(
					"layoutPageTemplateEntryId"
				),
				LayoutPageTemplateCollectionTable.INSTANCE.
					layoutPageTemplateCollectionId,
				LayoutPageTemplateCollectionTable.INSTANCE.name,
				LayoutPageTemplateCollectionTable.INSTANCE.createDate,
				LayoutPageTemplateCollectionTable.INSTANCE.modifiedDate
			).from(
				LayoutPageTemplateCollectionTable.INSTANCE
			).where(
				LayoutPageTemplateCollectionTable.INSTANCE.groupId.eq(
					groupId
				).and(
					() -> {
						if (layoutPageTemplateCollectionId < 0) {
							return null;
						}

						return LayoutPageTemplateCollectionTable.INSTANCE.
							parentLayoutPageTemplateCollectionId.eq(
								layoutPageTemplateCollectionId);
					}
				).and(
					() -> {
						if (Validator.isNull(name)) {
							return null;
						}

						return LayoutPageTemplateCollectionTable.INSTANCE.name.
							like(StringUtil.quote(name, CharPool.PERCENT));
					}
				).and(
					LayoutPageTemplateCollectionTable.INSTANCE.type.eq(type)
				)
			)
		).as(
			"layoutPageTemplateCollectionAndLayoutPageTemplateEntryTableTable",
			LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable.INSTANCE
		);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry)"
	)
	private ModelResourcePermission<LayoutPageTemplateEntry>
		_layoutPageTemplateEntryModelResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.layout.page.template.internal.security.permission.resource.LayoutPageTemplatePortletResourcePermission)"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private static class
		LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable
			extends BaseTable
				<LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable> {

		public static final
			LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable
				INSTANCE =
					new LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable();

		public final Column
			<LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable, Date>
				createDateColumn = createColumn(
					"createDate", Date.class, Types.TIMESTAMP,
					Column.FLAG_DEFAULT);
		public final Column
			<LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable, Date>
				modifiedDateColumn = createColumn(
					"modifiedDate", Date.class, Types.TIMESTAMP,
					Column.FLAG_DEFAULT);
		public final Column
			<LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable,
			 String> nameColumn = createColumn(
				"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

		private LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable() {
			super(
				"LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable",
				LayoutPageTemplateCollectionAndLayoutPageTemplateEntryTable::
					new);
		}

	}

}