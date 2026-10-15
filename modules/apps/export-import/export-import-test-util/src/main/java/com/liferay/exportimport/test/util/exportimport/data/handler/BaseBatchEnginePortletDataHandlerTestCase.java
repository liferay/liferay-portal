/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.exportimport.data.handler;

import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.service.ChangesetCollectionLocalService;
import com.liferay.changeset.service.ChangesetEntryLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.exportimport.test.util.lar.BasePortletDataHandlerTestCase;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate.ExportImportDescriptor;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegateRegistry;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alberto Javier Moreno Lage
 */
public abstract class BaseBatchEnginePortletDataHandlerTestCase<T>
	extends BasePortletDataHandlerTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ExportImportVulcanBatchEngineTaskItemDelegate<?>
			exportImportVulcanBatchEngineTaskItemDelegate =
				(ExportImportVulcanBatchEngineTaskItemDelegate<?>)
					_vulcanBatchEngineTaskItemDelegateRegistry.
						getVulcanBatchEngineTaskItemDelegate(
							TestPropsValues.getCompanyId(),
							getEntityClassName());

		_exportImportDescriptor =
			exportImportVulcanBatchEngineTaskItemDelegate.
				getExportImportDescriptor();
	}

	@Test
	public void testExportImportComments() throws Exception {
		if (!supportsComments()) {
			return;
		}

		String externalReferenceCode = RandomTestUtil.randomString();

		T entry = addEntry(externalReferenceCode);

		String body = RandomTestUtil.randomString();

		_commentManager.addComment(
			TestPropsValues.getUserId(), _getGroupId(),
			_exportImportDescriptor.getModelClassName(), getPrimaryKey(entry),
			body,
			className -> {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);

				return serviceContext;
			});

		_exportImport(
			null,
			HashMapBuilder.put(
				PortletDataHandlerKeys.COMMENTS,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null);

		List<String> comments = _getComments(
			externalReferenceCode, _getTargetGroupId());

		Assert.assertTrue(
			comments.toString(),
			ListUtil.exists(comments, comment -> comment.contains(body)));
	}

	@Override
	@Test
	public void testExportImportData() throws Exception {
		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		addEntry(externalReferenceCode1);
		addEntry(externalReferenceCode2);

		_exportImport(null, Collections.emptyMap(), null);

		long targetGroupId = _getTargetGroupId();

		Assert.assertNotNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));
	}

	@Test
	public void testExportImportDeletions() throws Exception {
		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		T entry1 = addEntry(externalReferenceCode1);

		addEntry(externalReferenceCode2);

		_exportImport(null, Collections.emptyMap(), null);

		long targetGroupId = _getTargetGroupId();

		Assert.assertNotNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));

		deleteEntry(entry1);

		_exportImport(
			null,
			HashMapBuilder.put(
				PortletDataHandlerKeys.DELETIONS,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null);

		Assert.assertNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));
	}

	@Test
	public void testExportImportFromLastPublishDate() throws Exception {
		if (!_exportImportDescriptor.isStagingSupported()) {
			return;
		}

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		T entry1 = addEntry(externalReferenceCode1);

		addEntry(externalReferenceCode2);

		ChangesetCollection changesetCollection =
			_changesetCollectionLocalService.fetchOrAddChangesetCollection(
				_getGroupId(),
				StagingConstants.RANGE_FROM_LAST_PUBLISH_DATE_CHANGESET_NAME);

		_changesetEntryLocalService.fetchOrAddChangesetEntry(
			changesetCollection.getChangesetCollectionId(),
			externalReferenceCode1,
			_classNameLocalService.getClassNameId(
				_exportImportDescriptor.getModelClassName()),
			getPrimaryKey(entry1));

		_exportImport(
			null,
			HashMapBuilder.put(
				ExportImportDateUtil.RANGE,
				new String[] {ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE}
			).build(),
			null);

		long targetGroupId = _getTargetGroupId();

		Assert.assertNotNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNull(fetchEntry(externalReferenceCode2, targetGroupId));
	}

	@Test
	public void testExportImportKeepCreatorData() throws Exception {
		User user = addUser();

		String externalReferenceCode = RandomTestUtil.randomString();

		updateUserId(addEntry(externalReferenceCode), user.getUserId());

		_exportImport(
			null,
			HashMapBuilder.put(
				PortletDataHandlerKeys.USER_ID_STRATEGY,
				new String[] {UserIdStrategy.CURRENT_USER_ID}
			).build(),
			null);

		User targetUser = _userLocalService.getUser(
			getUserId(fetchEntry(externalReferenceCode, _getTargetGroupId())));

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			targetUser.getExternalReferenceCode());
	}

	@Test
	public void testExportImportOverrideCreatorData() throws Exception {
		User user = addUser();

		String externalReferenceCode = RandomTestUtil.randomString();

		updateUserId(addEntry(externalReferenceCode), user.getUserId());

		_exportImport(
			null,
			HashMapBuilder.put(
				PortletDataHandlerKeys.USER_ID_STRATEGY,
				new String[] {UserIdStrategy.ALWAYS_CURRENT_USER_ID}
			).build(),
			null);

		User targetUser = _userLocalService.getUser(
			getUserId(fetchEntry(externalReferenceCode, _getTargetGroupId())));

		Assert.assertEquals(
			targetUser.getUserId(),
			getUserId(fetchEntry(externalReferenceCode, _getTargetGroupId())));
	}

	@Test
	public void testExportImportPermissions() throws Exception {
		if (!supportsPermissions()) {
			return;
		}

		String externalReferenceCode = RandomTestUtil.randomString();

		T entry = addEntry(externalReferenceCode);

		Role role = addRole();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_exportImportDescriptor.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(getPrimaryKey(entry)), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_exportImport(
			null,
			HashMapBuilder.put(
				PortletDataHandlerKeys.PERMISSIONS,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null);

		Group targetGroup = getTargetGroup();

		Role targetRole = _roleLocalService.getRoleByExternalReferenceCode(
			role.getExternalReferenceCode(), targetGroup.getCompanyId());

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				targetGroup.getCompanyId(), getTargetModelClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					getPrimaryKey(
						fetchEntry(
							externalReferenceCode, targetGroup.getGroupId()))),
				targetRole.getRoleId(), ActionKeys.VIEW));
	}

	@Test
	public void testExportImportWithDateRange() throws Exception {
		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();
		String externalReferenceCode3 = RandomTestUtil.randomString();

		T entry1 = addEntry(externalReferenceCode1);
		T entry2 = addEntry(externalReferenceCode2);
		T entry3 = addEntry(externalReferenceCode3);

		long time = System.currentTimeMillis();

		updateModifiedDate(entry1, new Date(time - (4 * Time.DAY)));
		updateModifiedDate(entry2, new Date(time - (2 * Time.DAY)));
		updateModifiedDate(entry3, new Date(time));

		Date startDate = new Date(time - (3 * Time.DAY));
		Date endDate = new Date(time - Time.DAY);

		_exportImport(endDate, Collections.emptyMap(), startDate);

		long targetGroupId = _getTargetGroupId();

		Assert.assertNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));
		Assert.assertNull(fetchEntry(externalReferenceCode3, targetGroupId));
	}

	@Test
	public void testUpdateResolvesEmptyEntry() throws Exception {
		if (!supportsEmptyEntries()) {
			return;
		}

		String externalReferenceCode = RandomTestUtil.randomString();

		T entry = _addEmptyEntry(externalReferenceCode);

		Assert.assertEquals(WorkflowConstants.STATUS_EMPTY, getStatus(entry));

		ExportImportReportEntry exportImportReportEntry =
			_getEmptyExportImportReportEntry(externalReferenceCode);

		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());

		updateEntry(entry);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			getStatus(fetchEntry(externalReferenceCode, _getGroupId())));

		exportImportReportEntry = _getEmptyExportImportReportEntry(
			externalReferenceCode);

		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_RESOLVED,
			exportImportReportEntry.getStatus());
	}

	@Rule
	public final PermissionCheckerMethodTestRule
		permissionCheckerMethodTestRule =
			PermissionCheckerMethodTestRule.INSTANCE;

	protected T addEmptyEntry(String externalReferenceCode) throws Exception {
		throw new UnsupportedOperationException();
	}

	protected abstract T addEntry(String externalReferenceCode)
		throws Exception;

	protected ExportImportConfiguration addExportImportConfiguration(
			Date endDate, Map<String, Serializable> settingsMap, Date startDate,
			int type)
		throws Exception {

		if ((endDate != null) && (startDate != null)) {
			settingsMap.put("endDate", endDate);
			settingsMap.put("startDate", startDate);
		}

		return _exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(
				TestPropsValues.getUserId(), type, settingsMap);
	}

	protected Role addRole() throws Exception {
		return RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
	}

	protected User addUser() throws Exception {
		return UserTestUtil.addUser();
	}

	protected abstract void deleteEntry(T entry) throws Exception;

	protected abstract void exportImport(
			Date endDate, Map<String, String[]> parameterMap, Date startDate)
		throws Exception;

	protected abstract T fetchEntry(String externalReferenceCode, long groupId)
		throws Exception;

	protected abstract String getEntityClassName();

	protected abstract Group getGroup();

	@Override
	protected String getPortletId() {
		return _exportImportDescriptor.getPortletId();
	}

	protected abstract long getPrimaryKey(T entry) throws Exception;

	protected int getStatus(T entry) throws Exception {
		throw new UnsupportedOperationException();
	}

	protected abstract Group getTargetGroup();

	protected String getTargetModelClassName() {
		return _exportImportDescriptor.getModelClassName();
	}

	protected abstract long getUserId(T entry) throws Exception;

	@Override
	protected void initContext() throws Exception {
		super.initContext();

		portletDataContext.setGroupId(_getGroupId());
		portletDataContext.setScopeGroupId(_getGroupId());
	}

	protected abstract boolean supportsComments();

	protected abstract boolean supportsEmptyEntries();

	protected abstract boolean supportsPermissions();

	protected abstract void updateEntry(T entry) throws Exception;

	protected ExportImportConfiguration updateExportImportConfiguration(
			ExportImportConfiguration exportImportConfiguration,
			Map<String, Serializable> settingsMap, long targetGroupId,
			User user)
		throws Exception {

		exportImportConfiguration =
			_exportImportConfigurationLocalService.
				updateExportImportConfiguration(
					user.getUserId(),
					exportImportConfiguration.getExportImportConfigurationId(),
					StringPool.BLANK, StringPool.BLANK, settingsMap,
					new ServiceContext());

		exportImportConfiguration.setGroupId(targetGroupId);

		return _exportImportConfigurationLocalService.
			updateExportImportConfiguration(exportImportConfiguration);
	}

	protected abstract void updateModifiedDate(T entry, Date modifiedDate)
		throws Exception;

	protected abstract void updateUserId(T entry, long userId) throws Exception;

	private T _addEmptyEntry(String externalReferenceCode) throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				return addEmptyEntry(externalReferenceCode);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}
		}
	}

	private void _exportImport(
			Date endDate, Map<String, String[]> parameterMap, Date startDate)
		throws Exception {

		exportImport(
			endDate,
			HashMapBuilder.put(
				ExportImportDateUtil.RANGE,
				new String[] {ExportImportDateUtil.RANGE_ALL}
			).put(
				PortletDataHandlerControl.getNamespacedName(
					portletId, _exportImportDescriptor.getKey()),
				new String[] {Boolean.TRUE.toString()}
			).put(
				PortletDataHandlerKeys.DATA_STRATEGY,
				new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
			).put(
				PortletDataHandlerKeys.PORTLET_DATA,
				new String[] {Boolean.TRUE.toString()}
			).put(
				PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
					portletId,
				new String[] {Boolean.TRUE.toString()}
			).put(
				PortletDataHandlerKeys.PORTLET_SETUP_ALL,
				new String[] {Boolean.TRUE.toString()}
			).putAll(
				parameterMap
			).build(),
			startDate);
	}

	private List<String> _getComments(
			String externalReferenceCode, long groupId)
		throws Exception {

		return TransformUtil.transform(
			_commentManager.getComments(
				getTargetModelClassName(),
				getPrimaryKey(fetchEntry(externalReferenceCode, groupId)),
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			comment -> {
				if (comment.isRoot()) {
					return null;
				}

				return comment.getBody();
			});
	}

	private ExportImportReportEntry _getEmptyExportImportReportEntry(
			String classExternalReferenceCode)
		throws Exception {

		for (ExportImportReportEntry exportImportReportEntry :
				_exportImportReportEntryLocalService.
					getExportImportReportEntries(
						TestPropsValues.getCompanyId(), 0)) {

			if (Objects.equals(
					classExternalReferenceCode,
					exportImportReportEntry.getClassExternalReferenceCode()) &&
				(exportImportReportEntry.getType() ==
					ExportImportReportEntryConstants.TYPE_EMPTY)) {

				return exportImportReportEntry;
			}
		}

		return null;
	}

	private long _getGroupId() {
		Group group = getGroup();

		return group.getGroupId();
	}

	private long _getTargetGroupId() {
		Group group = getTargetGroup();

		return group.getGroupId();
	}

	@Inject
	private ChangesetCollectionLocalService _changesetCollectionLocalService;

	@Inject
	private ChangesetEntryLocalService _changesetEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CommentManager _commentManager;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	private ExportImportDescriptor<?> _exportImportDescriptor;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanBatchEngineTaskItemDelegateRegistry
		_vulcanBatchEngineTaskItemDelegateRegistry;

}