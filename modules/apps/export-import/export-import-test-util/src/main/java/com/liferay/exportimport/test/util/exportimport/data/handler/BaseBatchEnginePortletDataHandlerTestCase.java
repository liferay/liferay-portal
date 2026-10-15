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
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
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

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Alberto Javier Moreno Lage
 */
public abstract class BaseBatchEnginePortletDataHandlerTestCase<T>
	extends BasePortletDataHandlerTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		ExportImportVulcanBatchEngineTaskItemDelegate<?>
			exportImportVulcanBatchEngineTaskItemDelegate =
				getExportImportVulcanBatchEngineTaskItemDelegate();

		_exportImportDescriptor =
			exportImportVulcanBatchEngineTaskItemDelegate.
				getExportImportDescriptor();

		super.setUp();
	}

	@Test
	public void testExportImportComments() throws Exception {
		if (!supportsComments()) {
			return;
		}

		long groupId = group.getGroupId();

		String externalReferenceCode = RandomTestUtil.randomString();

		addEntry(externalReferenceCode);

		String body = RandomTestUtil.randomString();

		_commentManager.addComment(
			TestPropsValues.getUserId(), groupId,
			_exportImportDescriptor.getModelClassName(),
			getPrimaryKey(fetchEntry(externalReferenceCode, groupId)), body,
			className -> {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);

				return serviceContext;
			});

		_exportImport(
			HashMapBuilder.put(
				PortletDataHandlerKeys.COMMENTS,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null, null);

		List<String> comments = _getComments(
			externalReferenceCode, targetGroup.getGroupId());

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

		_exportImport(Collections.emptyMap(), null, null);

		long targetGroupId = targetGroup.getGroupId();

		Assert.assertNotNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));
	}

	@Test
	public void testExportImportDeletions() throws Exception {
		long groupId = group.getGroupId();

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		addEntry(externalReferenceCode1);
		addEntry(externalReferenceCode2);

		_exportImport(Collections.emptyMap(), null, null);

		long targetGroupId = targetGroup.getGroupId();

		Assert.assertNotNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));

		deleteEntry(fetchEntry(externalReferenceCode1, groupId));

		_exportImport(
			HashMapBuilder.put(
				PortletDataHandlerKeys.DELETIONS,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null, null);

		Assert.assertNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));
	}

	@Test
	public void testExportImportFromLastPublishDate() throws Exception {
		if (!_exportImportDescriptor.isStagingSupported()) {
			return;
		}

		long groupId = group.getGroupId();

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();

		addEntry(externalReferenceCode1);
		addEntry(externalReferenceCode2);

		ChangesetCollection changesetCollection =
			_changesetCollectionLocalService.fetchOrAddChangesetCollection(
				groupId,
				StagingConstants.RANGE_FROM_LAST_PUBLISH_DATE_CHANGESET_NAME);

		_changesetEntryLocalService.fetchOrAddChangesetEntry(
			changesetCollection.getChangesetCollectionId(),
			externalReferenceCode1,
			_classNameLocalService.getClassNameId(
				_exportImportDescriptor.getModelClassName()),
			getPrimaryKey(fetchEntry(externalReferenceCode1, groupId)));

		_exportImport(
			HashMapBuilder.put(
				ExportImportDateUtil.RANGE,
				new String[] {ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE}
			).build(),
			null, null);

		long targetGroupId = targetGroup.getGroupId();

		Assert.assertNotNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNull(fetchEntry(externalReferenceCode2, targetGroupId));
	}

	@Test
	public void testExportImportKeepCreatorData() throws Exception {
		User user = addUser();

		String externalReferenceCode = RandomTestUtil.randomString();

		addEntry(externalReferenceCode);

		updateUserId(
			fetchEntry(externalReferenceCode, group.getGroupId()),
			user.getUserId());

		_exportImport(
			HashMapBuilder.put(
				PortletDataHandlerKeys.USER_ID_STRATEGY,
				new String[] {UserIdStrategy.CURRENT_USER_ID}
			).build(),
			null, null);

		User targetUser = _userLocalService.getUser(
			getCreatorUserId(
				fetchEntry(externalReferenceCode, targetGroup.getGroupId())));

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			targetUser.getExternalReferenceCode());
	}

	@Test
	public void testExportImportOverrideCreatorData() throws Exception {
		User user = addUser();

		String externalReferenceCode = RandomTestUtil.randomString();

		addEntry(externalReferenceCode);

		updateUserId(
			fetchEntry(externalReferenceCode, group.getGroupId()),
			user.getUserId());

		_exportImport(
			HashMapBuilder.put(
				PortletDataHandlerKeys.USER_ID_STRATEGY,
				new String[] {UserIdStrategy.ALWAYS_CURRENT_USER_ID}
			).build(),
			null, null);

		Assert.assertEquals(
			targetUser.getUserId(),
			getCreatorUserId(
				fetchEntry(externalReferenceCode, targetGroup.getGroupId())));
	}

	@Test
	public void testExportImportPermissions() throws Exception {
		if (!supportsPermissions()) {
			return;
		}

		long groupId = group.getGroupId();

		String externalReferenceCode = RandomTestUtil.randomString();

		addEntry(externalReferenceCode);

		Role role = addRole();

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_exportImportDescriptor.getModelClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(
				getPrimaryKey(fetchEntry(externalReferenceCode, groupId))),
			role.getRoleId(), new String[] {ActionKeys.VIEW});

		_exportImport(
			HashMapBuilder.put(
				PortletDataHandlerKeys.PERMISSIONS,
				new String[] {Boolean.TRUE.toString()}
			).build(),
			null, null);

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
		long groupId = group.getGroupId();

		String externalReferenceCode1 = RandomTestUtil.randomString();
		String externalReferenceCode2 = RandomTestUtil.randomString();
		String externalReferenceCode3 = RandomTestUtil.randomString();

		addEntry(externalReferenceCode1);
		addEntry(externalReferenceCode2);
		addEntry(externalReferenceCode3);

		long time = System.currentTimeMillis();

		updateModifiedDate(
			fetchEntry(externalReferenceCode1, groupId),
			new Date(time - (4 * Time.DAY)));
		updateModifiedDate(
			fetchEntry(externalReferenceCode2, groupId),
			new Date(time - (2 * Time.DAY)));
		updateModifiedDate(
			fetchEntry(externalReferenceCode3, groupId), new Date(time));

		Date startDate = new Date(time - (3 * Time.DAY));
		Date endDate = new Date(time - Time.DAY);

		_exportImport(Collections.emptyMap(), startDate, endDate);

		long targetGroupId = targetGroup.getGroupId();

		Assert.assertNull(fetchEntry(externalReferenceCode1, targetGroupId));
		Assert.assertNotNull(fetchEntry(externalReferenceCode2, targetGroupId));
		Assert.assertNull(fetchEntry(externalReferenceCode3, targetGroupId));
	}

	@Test
	public void testUpdateResolvesEmptyEntry() throws Exception {
		if (!supportsEmptyEntries()) {
			return;
		}

		long groupId = group.getGroupId();

		String externalReferenceCode = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			ExportImportThreadLocal.setPortletImportInProcess(true);

			try {
				addEmptyEntry(externalReferenceCode);
			}
			finally {
				ExportImportThreadLocal.setPortletImportInProcess(false);
			}
		}

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY,
			getStatus(fetchEntry(externalReferenceCode, groupId)));

		ExportImportReportEntry exportImportReportEntry =
			_getEmptyExportImportReportEntry(externalReferenceCode);

		Assert.assertEquals(
			ExportImportReportEntryConstants.STATUS_UNRESOLVED,
			exportImportReportEntry.getStatus());

		updateEntry(fetchEntry(externalReferenceCode, groupId));

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			getStatus(fetchEntry(externalReferenceCode, groupId)));

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

	protected void addEmptyEntry(String externalReferenceCode)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	protected abstract void addEntry(String externalReferenceCode)
		throws Exception;

	protected ExportImportConfiguration addExportConfiguration(
			int type, Map<String, Serializable> settingsMap, Date startDate,
			Date endDate)
		throws Exception {

		if ((endDate != null) && (startDate != null)) {
			settingsMap.put("endDate", endDate);
			settingsMap.put("startDate", startDate);
		}

		return ExportImportConfigurationLocalServiceUtil.
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
			Map<String, String[]> parameterMap, Date startDate, Date endDate)
		throws Exception;

	protected abstract T fetchEntry(String externalReferenceCode, long groupId)
		throws Exception;

	protected abstract long getCreatorUserId(T entry) throws Exception;

	protected abstract ExportImportVulcanBatchEngineTaskItemDelegate<?>
			getExportImportVulcanBatchEngineTaskItemDelegate()
		throws Exception;

	protected <T> ExportImportVulcanBatchEngineTaskItemDelegate<?>
			getExportImportVulcanBatchEngineTaskItemDelegate(
				Class<T> clazz, String filterString)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<T>> serviceReferences =
			bundleContext.getServiceReferences(clazz, filterString);

		Iterator<ServiceReference<T>> iterator = serviceReferences.iterator();

		return (ExportImportVulcanBatchEngineTaskItemDelegate<?>)
			bundleContext.getService(iterator.next());
	}

	@Override
	protected String getPortletId() {
		return _exportImportDescriptor.getPortletId();
	}

	protected abstract long getPrimaryKey(T entry) throws Exception;

	protected int getStatus(T entry) throws Exception {
		throw new UnsupportedOperationException();
	}

	protected String getTargetModelClassName() {
		return _exportImportDescriptor.getModelClassName();
	}

	@Override
	protected void initContext() throws Exception {
		super.initContext();

		portletDataContext.setGroupId(group.getGroupId());
		portletDataContext.setScopeGroupId(group.getGroupId());
	}

	protected abstract boolean supportsComments();

	protected abstract boolean supportsEmptyEntries();

	protected abstract boolean supportsPermissions();

	protected abstract void updateEntry(T entry) throws Exception;

	protected ExportImportConfiguration updateImportConfiguration(
			ExportImportConfiguration exportImportConfiguration, User user,
			Map<String, Serializable> settingsMap, long targetGroupId)
		throws Exception {

		exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				updateExportImportConfiguration(
					user.getUserId(),
					exportImportConfiguration.getExportImportConfigurationId(),
					StringPool.BLANK, StringPool.BLANK, settingsMap,
					new ServiceContext());

		exportImportConfiguration.setGroupId(targetGroupId);

		return ExportImportConfigurationLocalServiceUtil.
			updateExportImportConfiguration(exportImportConfiguration);
	}

	protected abstract void updateModifiedDate(T entry, Date modifiedDate)
		throws Exception;

	protected abstract void updateUserId(T entry, long userId) throws Exception;

	protected Group group;
	protected Group targetGroup;
	protected User targetUser;

	private void _exportImport(
			Map<String, String[]> parameterMap, Date startDate, Date endDate)
		throws Exception {

		exportImport(
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
			startDate, endDate);
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

	@Inject
	private ChangesetCollectionLocalService _changesetCollectionLocalService;

	@Inject
	private ChangesetEntryLocalService _changesetEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CommentManager _commentManager;

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

}