/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCompositionTable;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryTable;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.service.base.FragmentEntryServiceBaseImpl;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

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
		"json.web.service.context.name=fragment",
		"json.web.service.context.path=FragmentEntry"
	},
	service = AopService.class
)
public class FragmentEntryServiceImpl extends FragmentEntryServiceBaseImpl {

	@Override
	public FragmentEntry addFragmentEntry(
			long groupId, long fragmentCollectionId, String fragmentEntryKey,
			String name, String css, String html, String js,
			String configuration, long previewFileEntryId, int type, int status,
			ServiceContext serviceContext)
		throws PortalException {

		// LPS-190674 Maintain method for backwards compatibility with the
		// Fragments Toolkit

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.addFragmentEntry(
			null, getUserId(), groupId, fragmentCollectionId, fragmentEntryKey,
			name, css, html, js, false, configuration, null, previewFileEntryId,
			false, false, type, null, status, serviceContext);
	}

	@Override
	public FragmentEntry addFragmentEntry(
			String externalReferenceCode, long groupId,
			long fragmentCollectionId, String fragmentEntryKey, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean marketplace, boolean readOnly, int type, String typeOptions,
			int status, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.addFragmentEntry(
			externalReferenceCode, getUserId(), groupId, fragmentCollectionId,
			fragmentEntryKey, name, css, html, js, cacheable, configuration,
			icon, previewFileEntryId, marketplace, readOnly, type, typeOptions,
			status, serviceContext);
	}

	@Override
	public FragmentEntry copyFragmentEntry(
			long groupId, long sourceFragmentEntryId, long fragmentCollectionId,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.copyFragmentEntry(
			getUserId(), groupId, sourceFragmentEntryId, fragmentCollectionId,
			serviceContext);
	}

	@Override
	public void deleteFragmentEntries(long[] fragmentEntriesIds)
		throws PortalException {

		for (long fragmentEntryId : fragmentEntriesIds) {
			FragmentEntry fragmentEntry =
				fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);

			_portletResourcePermission.check(
				getPermissionChecker(), fragmentEntry.getGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

			fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);
		}
	}

	@Override
	public FragmentEntry deleteFragmentEntry(long fragmentEntryId)
		throws PortalException {

		FragmentEntry fragmentEntry =
			fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.deleteFragmentEntry(fragmentEntryId);
	}

	@Override
	public FragmentEntry deleteFragmentEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.deleteFragmentEntry(
			externalReferenceCode, groupId);
	}

	@Override
	public FragmentEntry fetchDraft(long primaryKey) {
		return fragmentEntryLocalService.fetchDraft(primaryKey);
	}

	@Override
	public FragmentEntry fetchFragmentEntry(long fragmentEntryId)
		throws PortalException {

		return fragmentEntryLocalService.fetchFragmentEntry(fragmentEntryId);
	}

	@Override
	public FragmentEntry getDraft(long primaryKey) throws PortalException {
		return fragmentEntryLocalService.getDraft(primaryKey);
	}

	@Override
	public List<Object> getFragmentCompositionsAndFragmentEntries(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<?> orderByComparator) {

		return getFragmentCompositionsAndFragmentEntries(
			groupId, fragmentCollectionId, StringPool.BLANK, status, start, end,
			orderByComparator);
	}

	@Override
	public List<Object> getFragmentCompositionsAndFragmentEntries(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end, OrderByComparator<?> orderByComparator) {

		List<Object> fragmentCompositionsAndFragmentEntries = new ArrayList<>();

		Table<?> fragmentCompositionsAndFragmentEntriesTable =
			_getFragmentCompositionGroupByStep(
				groupId, fragmentCollectionId, name, status
			).unionAll(
				_getFragmentEntryGroupByStep(
					groupId, fragmentCollectionId, name, status)
			).as(
				"TEMP_TABLE",
				FragmentCompositionsAndFragmentEntriesTable.INSTANCE
			);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			fragmentCompositionsAndFragmentEntriesTable
		).from(
			fragmentCompositionsAndFragmentEntriesTable
		).orderBy(
			fragmentCompositionsAndFragmentEntriesTable, orderByComparator
		).limit(
			start, end
		);

		for (Object[] array :
				fragmentEntryPersistence.<List<Object[]>>dslQuery(dslQuery)) {

			long fragmentCompositionId = GetterUtil.getLong(array[0]);

			if (fragmentCompositionId > 0) {
				fragmentCompositionsAndFragmentEntries.add(
					_fragmentCompositionLocalService.fetchFragmentComposition(
						fragmentCompositionId));

				continue;
			}

			fragmentCompositionsAndFragmentEntries.add(
				fragmentEntryLocalService.fetchFragmentEntry(
					GetterUtil.getLong(array[1])));
		}

		return fragmentCompositionsAndFragmentEntries;
	}

	@Override
	public int getFragmentCompositionsAndFragmentEntriesCount(
		long groupId, long fragmentCollectionId, int status) {

		return getFragmentCompositionsAndFragmentEntriesCount(
			groupId, fragmentCollectionId, null, status);
	}

	@Override
	public int getFragmentCompositionsAndFragmentEntriesCount(
		long groupId, long fragmentCollectionId, String name, int status) {

		Table<?> fragmentCompositionsAndFragmentEntriesTable =
			DSLQueryFactoryUtil.countDistinct(
				FragmentCompositionTable.INSTANCE.fragmentCompositionId
			).from(
				FragmentCompositionTable.INSTANCE
			).where(
				_getFragmentCompositionWherePredicate(
					groupId, fragmentCollectionId, name, status)
			).unionAll(
				DSLQueryFactoryUtil.countDistinct(
					FragmentEntryTable.INSTANCE.fragmentEntryId
				).from(
					FragmentEntryTable.INSTANCE
				).where(
					_getFragmentEntryWherePredicate(
						groupId, fragmentCollectionId, name, status)
				)
			).as(
				"TEMP_TABLE"
			);

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
		).from(
			fragmentCompositionsAndFragmentEntriesTable
		);

		int count = 0;

		for (Object countValue :
				fragmentEntryPersistence.<List<Object>>dslQuery(dslQuery)) {

			count += GetterUtil.getInteger(countValue);
		}

		return count;
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(long fragmentCollectionId) {
		return fragmentEntryLocalService.getFragmentEntries(
			fragmentCollectionId);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int start, int end) {

		return fragmentEntryPersistence.findByG_FCI(
			groupId, fragmentCollectionId, start, end);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return fragmentEntryPersistence.findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByName(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator) {

		return fragmentEntryPersistence.findByG_FCI_LikeN(
			groupId, fragmentCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByNameAndStatus(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return fragmentEntryPersistence.findByG_FCI_LikeN(
				groupId, fragmentCollectionId,
				_customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
				start, end, orderByComparator);
		}

		return fragmentEntryPersistence.findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], status,
			start, end, orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByStatus(
		long groupId, long fragmentCollectionId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return fragmentEntryPersistence.findByG_FCI(
				groupId, fragmentCollectionId);
		}

		return fragmentEntryLocalService.getFragmentEntries(
			groupId, fragmentCollectionId, status);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByStatus(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return fragmentEntryPersistence.findByG_FCI(
				groupId, fragmentCollectionId, start, end, orderByComparator);
		}

		return fragmentEntryPersistence.findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByType(
		long groupId, long fragmentCollectionId, int type, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return fragmentEntryPersistence.findByG_FCI_T(
			groupId, fragmentCollectionId, type, start, end, orderByComparator);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
		long groupId, long fragmentCollectionId, int type, int status) {

		return fragmentEntryPersistence.findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status);
	}

	@Override
	public List<FragmentEntry> getFragmentEntriesByTypeAndStatus(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return fragmentEntryPersistence.findByG_FCI_T(
				groupId, fragmentCollectionId, type, start, end,
				orderByComparator);
		}

		return fragmentEntryPersistence.findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status, start, end,
			orderByComparator);
	}

	@Override
	public int getFragmentEntriesCount(
		long groupId, long fragmentCollectionId) {

		return fragmentEntryPersistence.countByG_FCI(
			groupId, fragmentCollectionId);
	}

	@Override
	public int getFragmentEntriesCountByName(
		long groupId, long fragmentCollectionId, String name) {

		return fragmentEntryPersistence.countByG_FCI_LikeN(
			groupId, fragmentCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public int getFragmentEntriesCountByNameAndStatus(
		long groupId, long fragmentCollectionId, String name, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return fragmentEntryPersistence.countByG_FCI_LikeN(
				groupId, fragmentCollectionId,
				_customSQL.keywords(name, false, WildcardMode.SURROUND)[0]);
		}

		return fragmentEntryPersistence.countByG_FCI_LikeN_S(
			groupId, fragmentCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], status);
	}

	@Override
	public int getFragmentEntriesCountByStatus(
		long groupId, long fragmentCollectionId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return fragmentEntryPersistence.countByG_FCI(
				groupId, fragmentCollectionId);
		}

		return fragmentEntryPersistence.countByG_FCI_S(
			groupId, fragmentCollectionId, status);
	}

	@Override
	public int getFragmentEntriesCountByType(
		long groupId, long fragmentCollectionId, int type) {

		return fragmentEntryPersistence.countByG_FCI_T(
			groupId, fragmentCollectionId, type);
	}

	@Override
	public int getFragmentEntriesCountByTypeAndStatus(
		long groupId, long fragmentCollectionId, int type, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return fragmentEntryPersistence.countByG_FCI_T(
				groupId, fragmentCollectionId, type);
		}

		return fragmentEntryPersistence.countByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status);
	}

	@Override
	public FragmentEntry getFragmentEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.
			getFragmentEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public String[] getTempFileNames(long groupId, String folderName)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.getTempFileNames(
			getUserId(), groupId, folderName);
	}

	@Override
	public FragmentEntry moveFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId)
		throws PortalException {

		FragmentEntry fragmentEntry =
			fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.moveFragmentEntry(
			fragmentEntryId, fragmentCollectionId);
	}

	@Override
	public FragmentEntry publishDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), draftFragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.publishDraft(draftFragmentEntry);
	}

	@Override
	public FragmentEntry updateDraft(FragmentEntry draftFragmentEntry)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), draftFragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.updateDraft(draftFragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(FragmentEntry fragmentEntry)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.updateFragmentEntry(fragmentEntry);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, boolean cacheable)
		throws PortalException {

		FragmentEntry fragmentEntry =
			fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntryId, cacheable);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long previewFileEntryId)
		throws PortalException {

		FragmentEntry fragmentEntry =
			fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntryId, previewFileEntryId);
	}

	@Override
	public FragmentEntry updateFragmentEntry(
			long fragmentEntryId, long fragmentCollectionId, String name,
			String css, String html, String js, boolean cacheable,
			String configuration, String icon, long previewFileEntryId,
			boolean readOnly, String typeOptions, int status)
		throws PortalException {

		FragmentEntry fragmentEntry =
			fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.updateFragmentEntry(
			getUserId(), fragmentEntryId, fragmentCollectionId, name, css, html,
			js, cacheable, configuration, icon, previewFileEntryId, readOnly,
			typeOptions, status);
	}

	@Override
	public FragmentEntry updateFragmentEntry(long fragmentEntryId, String name)
		throws PortalException {

		FragmentEntry fragmentEntry =
			fragmentEntryLocalService.getFragmentEntry(fragmentEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntryId, name);
	}

	private GroupByStep _getFragmentCompositionGroupByStep(
		long groupId, long fragmentCollectionId, String name, int status) {

		return DSLQueryFactoryUtil.selectDistinct(
			FragmentCompositionTable.INSTANCE.fragmentCompositionId,
			new Scalar<>(
				0L
			).as(
				"fragmentEntryId"
			),
			FragmentCompositionTable.INSTANCE.createDate,
			FragmentCompositionTable.INSTANCE.modifiedDate,
			FragmentCompositionTable.INSTANCE.name
		).from(
			FragmentCompositionTable.INSTANCE
		).where(
			_getFragmentCompositionWherePredicate(
				groupId, fragmentCollectionId, name, status)
		);
	}

	private Predicate _getFragmentCompositionWherePredicate(
		long groupId, long fragmentCollectionId, String name, int status) {

		return FragmentCompositionTable.INSTANCE.groupId.eq(
			groupId
		).and(
			FragmentCompositionTable.INSTANCE.fragmentCollectionId.eq(
				fragmentCollectionId)
		).and(
			() -> {
				if (Validator.isNull(name)) {
					return null;
				}

				return DSLFunctionFactoryUtil.lower(
					FragmentCompositionTable.INSTANCE.name
				).like(
					_customSQL.keywords(name, true, WildcardMode.SURROUND)[0]
				);
			}
		).and(
			() -> {
				if (status != WorkflowConstants.STATUS_ANY) {
					return FragmentCompositionTable.INSTANCE.status.eq(status);
				}

				return null;
			}
		);
	}

	private GroupByStep _getFragmentEntryGroupByStep(
		long groupId, long fragmentCollectionId, String name, int status) {

		return DSLQueryFactoryUtil.selectDistinct(
			new Scalar<>(
				0L
			).as(
				"fragmentCompositionId"
			),
			FragmentEntryTable.INSTANCE.fragmentEntryId,
			FragmentEntryTable.INSTANCE.createDate,
			FragmentEntryTable.INSTANCE.modifiedDate,
			FragmentEntryTable.INSTANCE.name
		).from(
			FragmentEntryTable.INSTANCE
		).where(
			_getFragmentEntryWherePredicate(
				groupId, fragmentCollectionId, name, status)
		);
	}

	private Predicate _getFragmentEntryWherePredicate(
		long groupId, long fragmentCollectionId, String name, int status) {

		return FragmentEntryTable.INSTANCE.groupId.eq(
			groupId
		).and(
			FragmentEntryTable.INSTANCE.fragmentCollectionId.eq(
				fragmentCollectionId)
		).and(
			FragmentEntryTable.INSTANCE.head.eq(
				true
			).or(
				FragmentEntryTable.INSTANCE.headId.eq(
					FragmentEntryTable.INSTANCE.fragmentEntryId)
			).withParentheses()
		).and(
			() -> {
				if (Validator.isNull(name)) {
					return null;
				}

				return DSLFunctionFactoryUtil.lower(
					FragmentEntryTable.INSTANCE.name
				).like(
					_customSQL.keywords(name, true, WildcardMode.SURROUND)[0]
				);
			}
		).and(
			() -> {
				if (status != WorkflowConstants.STATUS_ANY) {
					return FragmentEntryTable.INSTANCE.status.eq(status);
				}

				return null;
			}
		);
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private FragmentCompositionLocalService _fragmentCompositionLocalService;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	private static class FragmentCompositionsAndFragmentEntriesTable
		extends BaseTable<FragmentCompositionsAndFragmentEntriesTable> {

		public static final FragmentCompositionsAndFragmentEntriesTable
			INSTANCE = new FragmentCompositionsAndFragmentEntriesTable();

		public final Column<FragmentCompositionsAndFragmentEntriesTable, Date>
			modifiedDateColumn = createColumn(
				"modifiedDate", Date.class, Types.TIMESTAMP,
				Column.FLAG_DEFAULT);
		public final Column<FragmentCompositionsAndFragmentEntriesTable, String>
			nameColumn = createColumn(
				"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

		private FragmentCompositionsAndFragmentEntriesTable() {
			super(
				"TEMP_TABLE", FragmentCompositionsAndFragmentEntriesTable::new);
		}

	}

}