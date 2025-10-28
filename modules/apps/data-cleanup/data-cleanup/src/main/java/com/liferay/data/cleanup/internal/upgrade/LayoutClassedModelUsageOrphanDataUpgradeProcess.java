/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.upgrade;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Georgel Pop
 */
public class LayoutClassedModelUsageOrphanDataUpgradeProcess
	extends BaseUpgradeProcess {

	public LayoutClassedModelUsageOrphanDataUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		ContentManager contentManager,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		LayoutClassedModelUsageLocalService layoutClassedModelUsageLocalService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		LayoutPageTemplateStructureRelLocalService
			layoutPageTemplateStructureRelLocalService) {

		_classNameLocalService = classNameLocalService;
		_contentManager = contentManager;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_layoutClassedModelUsageLocalService =
			layoutClassedModelUsageLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_layoutPageTemplateStructureRelLocalService =
			layoutPageTemplateStructureRelLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_cleanUpdateLayoutClassedModelUsages();
	}

	private void _cleanUpdateLayoutClassedModelUsages() throws Exception {
		_processLayoutClassedModelUsage(
			_classNameLocalService.getClassNameId(
				FragmentEntryLink.class.getName()),
			"fragmentEntryLinkId", "FragmentEntryLink",
			this::_updateFragmentEntryLayoutClassedModelUsage);
		_processLayoutClassedModelUsage(
			_classNameLocalService.getClassNameId(
				LayoutPageTemplateStructure.class.getName()),
			"layoutPageTemplateStructureId", "LayoutPageTemplateStructure",
			this::_updateLayoutPageTemplateStructureClassedModelUsage);
	}

	private void _processLayoutClassedModelUsage(
			long classNameId, String keyColumnName, String tableName,
			UnsafeBiConsumer<Long, Long, Exception> unsafeBiConsumer)
		throws Exception {

		Map<Long, Map<Long, Set<Long>>> groupIdMap = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select ctCollectionId, layoutClassedModelUsageId, ",
						"groupId, plid from LayoutClassedModelUsage where ",
						"containerType = ? and (plid <> (select plid from ",
						tableName, " where ", keyColumnName,
						" = CAST_LONG(containerKey) and ",
						"LayoutClassedModelUsage.ctCollectionId = ", tableName,
						".ctCollectionId) or (not exists (select 1 from ",
						tableName, " where ", keyColumnName,
						" = CAST_LONG(containerKey) and ",
						"LayoutClassedModelUsage.ctCollectionId = ", tableName,
						".ctCollectionId)))")))) {

			preparedStatement.setLong(1, classNameId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long ctCollectionId = resultSet.getLong("ctCollectionId");
					long layoutClassedModelUsageId = resultSet.getLong(
						"layoutClassedModelUsageId");
					long groupId = resultSet.getLong("groupId");
					long plid = resultSet.getLong("plid");

					try {
						Map<Long, Set<Long>> ctCollectionIdMap =
							groupIdMap.computeIfAbsent(
								groupId, key -> new HashMap<>());

						Set<Long> plids = ctCollectionIdMap.computeIfAbsent(
							ctCollectionId, key -> new HashSet<>());

						plids.add(plid);

						_layoutClassedModelUsageLocalService.
							deleteLayoutClassedModelUsage(
								layoutClassedModelUsageId);
					}
					catch (Exception exception) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								StringBundler.concat(
									"Unable to delete orphaned layout classed ",
									"model usage with ID ",
									layoutClassedModelUsageId),
								exception);
						}
					}
				}
			}
		}

		_processLayoutClassedModelUsage(groupIdMap, unsafeBiConsumer);
	}

	private void _processLayoutClassedModelUsage(
		Map<Long, Map<Long, Set<Long>>> plidMap,
		UnsafeBiConsumer<Long, Long, Exception> unsafeBiConsumer) {

		for (Map.Entry<Long, Map<Long, Set<Long>>> groupIdEntry :
				plidMap.entrySet()) {

			long groupId = groupIdEntry.getKey();
			Map<Long, Set<Long>> ctCollectionIdMap = groupIdEntry.getValue();

			for (Map.Entry<Long, Set<Long>> ctCollectionIdEntry :
					ctCollectionIdMap.entrySet()) {

				long ctCollectionId = ctCollectionIdEntry.getKey();
				Set<Long> plids = ctCollectionIdEntry.getValue();

				for (long plid : plids) {
					try (SafeCloseable safeCloseable =
							CTCollectionThreadLocal.
								setCTCollectionIdWithSafeCloseable(
									ctCollectionId)) {

						unsafeBiConsumer.accept(groupId, plid);
					}
					catch (Exception exception) {
						if (_log.isWarnEnabled()) {
							_log.warn(exception);
						}
					}
				}
			}
		}
	}

	private void _updateFragmentEntryLayoutClassedModelUsage(
		long groupId, long plid) {

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				groupId, plid);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			if (fragmentEntryLink == null) {
				continue;
			}

			try {
				_contentManager.updateLayoutClassedModelUsage(
					fragmentEntryLink);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to update usages for fragment entry link ",
							"ID ", fragmentEntryLink.getFragmentEntryLinkId()),
						exception);
				}
			}
		}
	}

	private void _updateLayoutPageTemplateStructureClassedModelUsage(
		long groupId, long plid) {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(groupId, plid);

		if (layoutPageTemplateStructure == null) {
			return;
		}

		List<LayoutPageTemplateStructureRel> layoutPageTemplateStructureRels =
			_layoutPageTemplateStructureRelLocalService.
				getLayoutPageTemplateStructureRels(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId());

		for (LayoutPageTemplateStructureRel layoutPageTemplateStructureRel :
				layoutPageTemplateStructureRels) {

			if (layoutPageTemplateStructureRel == null) {
				continue;
			}

			try {
				_contentManager.updateLayoutClassedModelUsage(
					layoutPageTemplateStructureRel);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to update usages for layout page template ",
							"structure rel ID ",
							layoutPageTemplateStructureRel.
								getLayoutPageTemplateStructureRelId()),
						exception);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutClassedModelUsageOrphanDataUpgradeProcess.class);

	private final ClassNameLocalService _classNameLocalService;
	private final ContentManager _contentManager;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

}