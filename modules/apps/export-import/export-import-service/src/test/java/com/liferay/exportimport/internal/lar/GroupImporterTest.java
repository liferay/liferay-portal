/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.lar;

import com.liferay.exportimport.internal.util.GroupExportImportParameterUtil;
import com.liferay.exportimport.kernel.exception.LARFileException;
import com.liferay.exportimport.kernel.lar.ExportImportGroup;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactory;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.kernel.exception.GroupParentException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.xml.SAXReaderImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Petteri Karttunen
 */
public class GroupImporterTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		SAXReaderUtil saxReaderUtil = new SAXReaderUtil();

		saxReaderUtil.setSAXReader(new SAXReaderImpl());
	}

	@Before
	public void setUp() throws Exception {
		_exportImportReportEntryLocalService = Mockito.mock(
			ExportImportReportEntryLocalService.class);
		_exportImportHelper = Mockito.mock(ExportImportHelper.class);
		_groupLocalService = Mockito.mock(GroupLocalService.class);
		_groupService = Mockito.mock(GroupService.class);
		_portletDataContextFactory = Mockito.mock(
			PortletDataContextFactory.class);

		_groupImporter = new GroupImporter(
			Mockito.mock(ClassNameLocalService.class), _exportImportHelper,
			_exportImportReportEntryLocalService, _groupLocalService,
			_groupService, _portletDataContextFactory);

		_manifestXml = String.format(
			"<root><header company-group-id=\"%d\" company-id=\"%d\" " +
				"user-personal-site-group-id=\"%d\" /></root>",
			_SOURCE_COMPANY_GROUP_ID, _SOURCE_COMPANY_ID,
			_SOURCE_USER_PERSONAL_SITE_GROUP_ID);

		_portletDataContext = Mockito.mock(PortletDataContext.class);

		Mockito.when(
			_portletDataContext.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_exportImportHelper.isGroupSupported(Mockito.any())
		).thenReturn(
			true
		);

		Mockito.when(
			_portletDataContextFactory.createImportPortletDataContext(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
				Mockito.any(), Mockito.any())
		).thenAnswer(
			invocation -> _mockGroupPortletDataContext(
				invocation.getArgument(2))
		);
	}

	@Test
	public void testImportGroups() throws Exception {
		_setUpExportImportGroups(_createExportImportGroup("group", null));
		_setUpGroups("group");

		_importGroups("group");

		PortletDataContext groupPortletDataContext =
			_groupPortletDataContexts.get(0);

		Mockito.verify(
			groupPortletDataContext
		).setSourceGroupId(
			_SOURCE_GROUP_ID
		);

		Mockito.verify(
			groupPortletDataContext
		).setPrivateLayout(
			false
		);

		Mockito.verify(
			groupPortletDataContext
		).setSourceCompanyGroupId(
			_SOURCE_COMPANY_GROUP_ID
		);

		Mockito.verify(
			groupPortletDataContext
		).setSourceCompanyId(
			_SOURCE_COMPANY_ID
		);

		Mockito.verify(
			groupPortletDataContext
		).setSourceUserPersonalSiteGroupId(
			_SOURCE_USER_PERSONAL_SITE_GROUP_ID
		);
	}

	@Test
	public void testImportGroupsWhenGroupExportImportIsDisabled()
		throws Exception {

		_setUpExportImportGroups(_createExportImportGroup("group", null));

		Mockito.when(
			_portletDataContext.getParameterMap()
		).thenReturn(
			_getParameterMap("group")
		);

		List<String> groupExternalReferenceCodes = _importGroups(false);

		Assert.assertTrue(groupExternalReferenceCodes.isEmpty());

		Mockito.verifyNoInteractions(_exportImportHelper);
	}

	@Test
	public void testImportGroupsWhenGroupExportImportPermissionIsMissing()
		throws Exception {

		_setUpExportImportGroups(_createExportImportGroup("group", null));
		_setUpGroups("group");

		Mockito.when(
			_portletDataContext.getParameterMap()
		).thenReturn(
			_getParameterMap("group")
		);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<GroupPermissionUtil> groupPermissionUtilMockedStatic =
				Mockito.mockStatic(GroupPermissionUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-85946"))
			).thenReturn(
				true
			);

			groupPermissionUtilMockedStatic.when(
				() -> GroupPermissionUtil.check(
					Mockito.any(), Mockito.anyLong(),
					Mockito.eq(ActionKeys.EXPORT_IMPORT_LAYOUTS))
			).thenThrow(
				new PrincipalException.MustHavePermission(
					_USER_ID, Group.class.getName(), _GROUP_ID,
					ActionKeys.EXPORT_IMPORT_LAYOUTS)
			);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					GroupImporter.class.getName(), LoggerTestUtil.ERROR)) {

				_groupImporter.importGroups(
					_portletDataContext,
					(groupPortletDataContext, userId) -> Assert.fail(
						"The group was imported without the \"" +
							ActionKeys.EXPORT_IMPORT_LAYOUTS + "\" permission"),
					_USER_ID);

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Throwable throwable = logEntry.getThrowable();

				Assert.assertSame(
					PrincipalException.MustHavePermission.class,
					throwable.getClass());
			}
		}

		Mockito.verifyNoInteractions(_exportImportReportEntryLocalService);
	}

	@Test
	public void testImportGroupsWhenGroupIsNotSelected() throws Exception {
		_setUpExportImportGroups(
			_createExportImportGroup("selected", null),
			_createExportImportGroup("unselected", null));
		_setUpGroups("selected", "unselected");

		Assert.assertEquals(
			Arrays.asList("selected"), _importGroups("selected"));

		Mockito.verifyNoInteractions(_exportImportReportEntryLocalService);
	}

	@Test
	public void testImportGroupsWhenGroupIsNotSupported() throws Exception {
		_setUpExportImportGroups(_createExportImportGroup("group", null));

		Group group = _setUpGroup("group");

		Mockito.when(
			_exportImportHelper.isGroupSupported(group)
		).thenReturn(
			false
		);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			"group"
		);

		List<String> groupExternalReferenceCodes = _importGroups("group");

		Assert.assertTrue(groupExternalReferenceCodes.isEmpty());

		_verifyReportEntry(
			"group", ExportImportReportEntryConstants.TYPE_ERROR,
			"is not supported");
	}

	@Test
	public void testImportGroupsWhenGroupScoped() throws Exception {
		Mockito.when(
			_portletDataContext.getParameterMap()
		).thenReturn(
			GroupExportImportParameterUtil.getGroupExportParameterMap(
				"group", _getParameterMap("group"))
		);

		List<String> groupExternalReferenceCodes = _importGroups(true);

		Assert.assertTrue(groupExternalReferenceCodes.isEmpty());

		Mockito.verifyNoInteractions(_exportImportHelper);
	}

	@Test
	public void testImportGroupsWhenHeaderIsMissing() throws Exception {
		_manifestXml = "<root />";

		_setUpExportImportGroups(_createExportImportGroup("group", null));
		_setUpGroups("group");

		_assertLARFileException(
			LARFileException.TYPE_INVALID_MANIFEST,
			() -> _importGroups("group"));
	}

	@Test
	public void testImportGroupsWhenManifestIsMissing() throws Exception {
		_manifestXml = null;

		_setUpExportImportGroups(_createExportImportGroup("group", null));
		_setUpGroups("group");

		_assertLARFileException(
			LARFileException.TYPE_MISSING_MANIFEST,
			() -> _importGroups("group"));
	}

	@Test
	public void testImportGroupsWhenNoGroupsAreSelected() throws Exception {
		_setUpExportImportGroups(_createExportImportGroup("group", null));

		Assert.assertTrue(_importGroups().isEmpty());

		Mockito.verifyNoInteractions(_exportImportReportEntryLocalService);
	}

	@Test
	public void testImportGroupsWhenParentGroupUpdateFails() throws Exception {
		_setUpExportImportGroups(
			_createExportImportGroup("child", "parent"),
			_createExportImportGroup("other", null));
		_setUpGroups("child", "parent", "other");

		Mockito.when(
			_groupLocalService.updateGroup(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any())
		).thenThrow(
			new GroupKeyException()
		);

		_runWithExpectedWarn(
			GroupKeyException.class,
			() -> Assert.assertEquals(
				Arrays.asList("child", "other"),
				_importGroups("child", "other")));

		_verifyReportEntry(
			"child", ExportImportReportEntryConstants.TYPE_WARNING,
			"Unable to move the group");
	}

	@Test
	public void testImportGroupsWhenSelectedGroupIsMissingFromTheLARFile()
		throws Exception {

		_setUpExportImportGroups(_createExportImportGroup("carried", null));
		_setUpGroups("carried");

		Assert.assertEquals(
			Arrays.asList("carried"), _importGroups("carried", "missing"));

		_verifyReportEntry(
			"missing", ExportImportReportEntryConstants.TYPE_ERROR,
			"is missing in the LAR file");
	}

	@Test
	public void testImportGroupsWhenTargetGroupIsMissing() throws Exception {
		_setUpExportImportGroups(_createExportImportGroup("group", null));

		List<String> groupExternalReferenceCodes = _importGroups("group");

		Assert.assertTrue(groupExternalReferenceCodes.isEmpty());

		_verifyReportEntry(
			"group", ExportImportReportEntryConstants.TYPE_ERROR,
			"does not exist in the target instance");
	}

	@Test
	public void testUpdateParentGroup() throws Exception {
		Group parentGroup = _mockGroup(0);

		Mockito.when(
			parentGroup.getGroupId()
		).thenReturn(
			_PARENT_GROUP_ID
		);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode(
				"parent", _COMPANY_ID)
		).thenReturn(
			parentGroup
		);

		Group group = _mockGroup(0);

		_updateParentGroup(_createExportImportGroup("child", "parent"), group);

		Mockito.verify(
			_groupLocalService
		).updateGroup(
			group.getGroupId(), _PARENT_GROUP_ID, group.getNameMap(),
			group.getDescriptionMap(), group.getType(), group.getTypeSettings(),
			group.isManualMembership(), group.getMembershipRestriction(),
			group.getFriendlyURL(), group.isInheritContent(), group.isActive(),
			null
		);

		Mockito.verifyNoInteractions(_exportImportReportEntryLocalService);
	}

	@Test
	public void testUpdateParentGroupWhenGroupKeyIsDuplicated()
		throws Exception {

		_setUpParentGroup();

		Mockito.when(
			_groupLocalService.updateGroup(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any())
		).thenThrow(
			new DuplicateGroupException()
		);

		_runWithExpectedWarn(
			DuplicateGroupException.class,
			() -> _updateParentGroup(
				_createExportImportGroup("child", "parent"), _mockGroup(0)));

		_verifyReportEntry(
			"child", ExportImportReportEntryConstants.TYPE_WARNING,
			"Unable to move the group");
	}

	@Test
	public void testUpdateParentGroupWhenGroupKeyIsInvalid() throws Exception {
		_setUpParentGroup();

		Mockito.when(
			_groupLocalService.updateGroup(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any())
		).thenThrow(
			new GroupKeyException()
		);

		_runWithExpectedWarn(
			GroupKeyException.class,
			() -> _updateParentGroup(
				_createExportImportGroup("child", "parent"), _mockGroup(0)));

		_verifyReportEntry(
			"child", ExportImportReportEntryConstants.TYPE_WARNING,
			"Unable to move the group");
	}

	@Test
	public void testUpdateParentGroupWhenParentGroupExternalReferenceCodeIsNull()
		throws Exception {

		_updateParentGroup(
			_createExportImportGroup("child", null), _mockGroup(0));

		Mockito.verifyNoInteractions(_groupLocalService);
		Mockito.verifyNoInteractions(_exportImportReportEntryLocalService);
	}

	@Test
	public void testUpdateParentGroupWhenParentGroupIsAlreadyTheParent()
		throws Exception {

		Group parentGroup = _mockGroup(0);

		Mockito.when(
			parentGroup.getGroupId()
		).thenReturn(
			_PARENT_GROUP_ID
		);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode(
				"parent", _COMPANY_ID)
		).thenReturn(
			parentGroup
		);

		_updateParentGroup(
			_createExportImportGroup("child", "parent"),
			_mockGroup(_PARENT_GROUP_ID));

		_verifyNoGroupWasMoved();

		Mockito.verifyNoInteractions(_exportImportReportEntryLocalService);
	}

	@Test
	public void testUpdateParentGroupWhenParentGroupIsBelowTheGroup()
		throws Exception {

		Group parentGroup = _mockGroup(0);

		Mockito.when(
			parentGroup.getGroupId()
		).thenReturn(
			_PARENT_GROUP_ID
		);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode(
				"parent", _COMPANY_ID)
		).thenReturn(
			parentGroup
		);

		Mockito.when(
			_groupLocalService.updateGroup(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyInt(), Mockito.any(),
				Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.any())
		).thenThrow(
			new GroupParentException.MustNotHaveChildParent(
				_GROUP_ID, _PARENT_GROUP_ID)
		);

		_updateParentGroup(
			_createExportImportGroup("child", "parent"), _mockGroup(0));

		_verifyReportEntry(
			"child", ExportImportReportEntryConstants.TYPE_WARNING,
			"is below it in the target instance");
	}

	@Test
	public void testUpdateParentGroupWhenParentGroupIsMissing()
		throws Exception {

		ExportImportGroup exportImportGroup = _createExportImportGroup(
			"child", "parent");

		_updateParentGroup(exportImportGroup, _mockGroup(0));

		_verifyReportEntry(
			"child", ExportImportReportEntryConstants.TYPE_WARNING,
			"does not exist in the target instance or in the LAR file");

		_verifyNoGroupWasMoved();
	}

	private void _assertLARFileException(
			int type, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (LARFileException larFileException) {
			Assert.assertEquals(type, larFileException.getType());
		}
	}

	private ExportImportGroup _createExportImportGroup(
		String externalReferenceCode, String parentExternalReferenceCode) {

		return new ExportImportGroup(
			0, RandomTestUtil.randomString(), externalReferenceCode,
			_SOURCE_GROUP_ID, parentExternalReferenceCode,
			RandomTestUtil.randomString());
	}

	private Map<String, String[]> _getParameterMap(
		String... selectedGroupExternalReferenceCodes) {

		return HashMapBuilder.put(
			PortletDataHandlerKeys.GROUP_EXTERNAL_REFERENCE_CODES,
			selectedGroupExternalReferenceCodes
		).build();
	}

	private List<String> _importGroups(boolean groupExportImportEnabled)
		throws Exception {

		List<String> importedGroupExternalReferenceCodes = new ArrayList<>();

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<GroupPermissionUtil> groupPermissionUtilMockedStatic =
				Mockito.mockStatic(GroupPermissionUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-85946"))
			).thenReturn(
				groupExportImportEnabled
			);

			_groupImporter.importGroups(
				_portletDataContext,
				(groupPortletDataContext, userId) ->
					importedGroupExternalReferenceCodes.add(
						GroupExportImportParameterUtil.
							getCurrentGroupExternalReferenceCode(
								groupPortletDataContext.getParameterMap())),
				_USER_ID);
		}

		return importedGroupExternalReferenceCodes;
	}

	private List<String> _importGroups(
			String... selectedGroupExternalReferenceCodes)
		throws Exception {

		Mockito.when(
			_portletDataContext.getParameterMap()
		).thenReturn(
			_getParameterMap(selectedGroupExternalReferenceCodes)
		);

		return _importGroups(true);
	}

	private Group _mockGroup(long parentGroupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			group.getParentGroupId()
		).thenReturn(
			parentGroupId
		);

		return group;
	}

	private PortletDataContext _mockGroupPortletDataContext(
		Map<String, String[]> parameterMap) {

		PortletDataContext portletDataContext = Mockito.mock(
			PortletDataContext.class);

		Mockito.when(
			portletDataContext.getParameterMap()
		).thenReturn(
			parameterMap
		);

		Mockito.when(
			portletDataContext.getZipEntryAsString(Mockito.anyString())
		).thenReturn(
			_manifestXml
		);

		_groupPortletDataContexts.add(portletDataContext);

		return portletDataContext;
	}

	private void _runWithExpectedWarn(
			Class<? extends Throwable> throwableClass,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				GroupImporter.class.getName(), LoggerTestUtil.WARN)) {

			unsafeRunnable.run();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(throwableClass, throwable.getClass());
		}
	}

	private void _setUpExportImportGroups(
			ExportImportGroup... exportImportGroups)
		throws Exception {

		Mockito.when(
			_exportImportHelper.getExportImportGroups(_portletDataContext)
		).thenReturn(
			ListUtil.fromArray(exportImportGroups)
		);
	}

	private Group _setUpGroup(String externalReferenceCode) throws Exception {
		Group group = _mockGroup(0);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode(
				externalReferenceCode, _COMPANY_ID)
		).thenReturn(
			group
		);

		Mockito.when(
			_groupService.fetchGroupByExternalReferenceCode(
				externalReferenceCode, _COMPANY_ID)
		).thenReturn(
			group
		);

		return group;
	}

	private void _setUpGroups(String... externalReferenceCodes)
		throws Exception {

		for (String externalReferenceCode : externalReferenceCodes) {
			_setUpGroup(externalReferenceCode);
		}
	}

	private void _setUpParentGroup() throws Exception {
		Group parentGroup = _mockGroup(0);

		Mockito.when(
			parentGroup.getGroupId()
		).thenReturn(
			_PARENT_GROUP_ID
		);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode(
				"parent", _COMPANY_ID)
		).thenReturn(
			parentGroup
		);
	}

	private void _updateParentGroup(
		ExportImportGroup exportImportGroup, Group group) {

		ReflectionTestUtil.invoke(
			_groupImporter, "_updateParentGroup",
			new Class<?>[] {
				PortletDataContext.class, Group.class, ExportImportGroup.class
			},
			_portletDataContext, group, exportImportGroup);
	}

	private void _verifyNoGroupWasMoved() throws Exception {
		Mockito.verify(
			_groupLocalService, Mockito.never()
		).updateGroup(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.any(), Mockito.any(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyBoolean(),
			Mockito.anyInt(), Mockito.any(), Mockito.anyBoolean(),
			Mockito.anyBoolean(), Mockito.any()
		);
	}

	private void _verifyReportEntry(
		String externalReferenceCode, int type, String message) {

		Mockito.verify(
			_exportImportReportEntryLocalService
		).getOrAddExportImportReportEntry(
			Mockito.anyLong(), Mockito.anyLong(),
			Mockito.eq(externalReferenceCode), Mockito.anyLong(),
			Mockito.anyLong(), Mockito.anyLong(), Mockito.eq(type),
			Mockito.contains(message), Mockito.isNull(), Mockito.eq("groups")
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _PARENT_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _SOURCE_COMPANY_GROUP_ID =
		RandomTestUtil.randomLong();

	private static final long _SOURCE_COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _SOURCE_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _SOURCE_USER_PERSONAL_SITE_GROUP_ID =
		RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private ExportImportHelper _exportImportHelper;
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;
	private GroupImporter _groupImporter;
	private GroupLocalService _groupLocalService;
	private final List<PortletDataContext> _groupPortletDataContexts =
		new ArrayList<>();
	private GroupService _groupService;
	private String _manifestXml;
	private PortletDataContext _portletDataContext;
	private PortletDataContextFactory _portletDataContextFactory;

}