/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.mail.kernel.model.Account;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutWrapper;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapper;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutBranchLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.PortalPreferencesImpl;
import com.liferay.portlet.PortalPreferencesWrapper;

import jakarta.portlet.ActionRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrew Betts
 */
@RunWith(Arquillian.class)
public class EditServerMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group, false);
	}

	@Test
	public void testCleanUpLayoutRevisionPortletPreferencesWithOrphanedPortletPreferences()
		throws Exception {

		LayoutRevision layoutRevision = _getLayoutRevision();

		_portletPreferences = _addPortletPreferences(
			TestPropsValues.getUserId(), 0,
			layoutRevision.getLayoutRevisionId(),
			RandomTestUtil.randomString());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_cleanUpLayoutRevisionPortletPreferences",
			new Class<?>[0]);

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				_portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testCleanUpLayoutRevisionPortletPreferencesWithProperPortletPreferences()
		throws Exception {

		LayoutRevision layoutRevision = _getLayoutRevision();

		String portletId = PortletIdCodec.encode(
			"com_liferay_test_portlet_TestPortlet");

		UnicodeProperties typeSettingsUnicodeProperties =
			layoutRevision.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty("column-1", portletId);

		layoutRevision = _layoutRevisionLocalService.updateLayoutRevision(
			layoutRevision);

		_portletPreferences = _addPortletPreferences(
			TestPropsValues.getUserId(), 0,
			layoutRevision.getLayoutRevisionId(), portletId);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)_layout.getLayoutType();

		List<String> portletIds = layoutTypePortlet.getPortletIds();

		Assert.assertTrue(portletIds.isEmpty());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_cleanUpLayoutRevisionPortletPreferences",
			new Class<?>[0]);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				_portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testCleanUpOrphanedPortletPreferencesForLayoutTypeAssetDisplay()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, _group.getCreatorUserId(), _group.getGroupId(), 0, null,
				_portal.getClassNameId(FileEntry.class.getName()), 0,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0, true, 0,
				0, 0, 0,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		String portletId = _addJournalContentPortletToLayout(draftLayout);

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, draftLayout.getPlid(),
				portletId);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_cleanUpOrphanedPortletPreferences",
			new Class<?>[0]);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testCleanUpOrphanedPortletPreferencesForLayoutTypeContentLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		String portletId = _addJournalContentPortletToLayout(draftLayout);

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, draftLayout.getPlid(),
				portletId);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_cleanUpOrphanedPortletPreferences",
			new Class<?>[0]);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				portletPreferences.getPortletPreferencesId()));
	}

	@Test
	public void testCleanUpOrphanedPortletPreferencesWithLayoutRevision()
		throws Exception {

		LayoutRevision layoutRevision = _getLayoutRevision();

		_portletPreferences = _addPortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
			layoutRevision.getLayoutRevisionId(),
			RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_ctPortletPreferences = _addPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				layoutRevision.getLayoutRevisionId(),
				RandomTestUtil.randomString());
		}

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_cleanUpOrphanedPortletPreferences",
			new Class<?>[0]);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				_portletPreferences.getPortletPreferencesId()));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			Assert.assertNotNull(
				_portletPreferencesLocalService.fetchPortletPreferences(
					_ctPortletPreferences.getPortletPreferencesId()));
		}
	}

	@Test
	public void testCleanUpOrphanedPortletPreferencesWithoutLayoutRevision()
		throws Exception {

		_portletPreferences = _addPortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
			RandomTestUtil.randomString());

		PortletPreferences modifiedPortletPreferences = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_ctPortletPreferences = _addPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				RandomTestUtil.randomString());

			jakarta.portlet.PortletPreferences jxPortletPreferences =
				_portletPreferenceValueLocalService.getPreferences(
					_portletPreferences);

			jxPortletPreferences.setValue(
				_ctCollection.getUserName(), RandomTestUtil.randomString());

			modifiedPortletPreferences =
				_portletPreferencesLocalService.updatePreferences(
					_portletPreferences.getOwnerId(),
					_portletPreferences.getOwnerType(),
					_portletPreferences.getPlid(),
					_portletPreferences.getPortletId(), jxPortletPreferences);
		}

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_cleanUpOrphanedPortletPreferences",
			new Class<?>[0]);

		Assert.assertNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				_portletPreferences.getPortletPreferencesId()));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			Assert.assertNull(
				_portletPreferencesLocalService.fetchPortletPreferences(
					_ctPortletPreferences.getPortletPreferencesId()));
			Assert.assertNull(
				_portletPreferencesLocalService.fetchPortletPreferences(
					modifiedPortletPreferences.getPortletPreferencesId()));
		}
	}

	@Test
	public void testCleanUpOrphanedPortletPreferencesWithProperPortletPreferences()
		throws Exception {

		String portletId = PortletIdCodec.encode(
			"com_liferay_test_portlet_TestPortlet");

		UnicodeProperties typeSettingsUnicodeProperties =
			_layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty("column-1", portletId);

		_layout = _layoutLocalService.updateLayout(_layout);

		_portletPreferences = _addPortletPreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(), portletId);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_ctPortletPreferences = _addPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(),
				portletId);
		}

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_cleanUpOrphanedPortletPreferences",
			new Class<?>[0]);

		Assert.assertNotNull(
			_portletPreferencesLocalService.fetchPortletPreferences(
				_portletPreferences.getPortletPreferencesId()));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			Assert.assertNotNull(
				_portletPreferencesLocalService.fetchPortletPreferences(
					_ctPortletPreferences.getPortletPreferencesId()));
		}
	}

	@Test
	public void testProcessAction() throws Exception {
		PermissionChecker permissionChecker = new PermissionCheckerWrapper(
			ProxyFactory.newDummyInstance(PermissionChecker.class)) {

			@Override
			public boolean isOmniadmin() {
				return true;
			}

		};

		for (String command : _COMMANDS) {
			_testProcessAction(command, permissionChecker);
		}

		permissionChecker = new PermissionCheckerWrapper(
			ProxyFactory.newDummyInstance(PermissionChecker.class)) {

			@Override
			public boolean isCompanyAdmin() {
				return true;
			}

		};

		for (String command : _COMMANDS) {
			_testProcessAction(command, permissionChecker);
		}
	}

	@Test
	public void testUpdateMail() {
		jakarta.portlet.PortletPreferences portletPreferences =
			PrefsPropsUtil.getPreferences(CompanyConstants.SYSTEM);

		try {
			_testUpdateMailPortletPreferences(
				RandomTestUtil::randomBoolean, RandomTestUtil::randomInt,
				RandomTestUtil::randomString, portletPreferences);

			_testUpdateMailPortletPreferences(
				() -> Boolean.FALSE, () -> 0, () -> StringPool.BLANK,
				portletPreferences);
		}
		finally {
			PortalPreferencesWrapper portalPreferencesWrapper =
				(PortalPreferencesWrapper)portletPreferences;

			PortalPreferencesImpl portalPreferencesImpl =
				portalPreferencesWrapper.getPortalPreferencesImpl();

			_portalPreferencesLocalService.deletePortalPreferences(
				_portalPreferencesLocalService.fetchPortalPreferences(
					portalPreferencesImpl.getOwnerId(),
					portalPreferencesImpl.getOwnerType()));
		}
	}

	private String _addJournalContentPortletToLayout(Layout layout)
		throws Exception {

		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				layout, JournalContentPortletKeys.JOURNAL_CONTENT);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private PortletPreferences _addPortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws Exception {

		return _portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), ownerId, ownerType, plid, portletId,
			null, StringPool.BLANK);
	}

	private LayoutRevision _getLayoutRevision() throws Exception {
		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchLocalService.addLayoutSetBranch(
				TestPropsValues.getUserId(), _group.getGroupId(), false,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				true, 0, ServiceContextTestUtil.getServiceContext());

		LayoutBranch layoutBranch =
			_layoutBranchLocalService.getMasterLayoutBranch(
				layoutSetBranch.getLayoutSetBranchId(), _layout.getPlid());

		return _layoutRevisionLocalService.getLayoutRevision(
			layoutSetBranch.getLayoutSetBranchId(),
			layoutBranch.getLayoutBranchId(), _layout.getPlid());
	}

	private void _testProcessAction(
			String cmd, PermissionChecker permissionChecker)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(
			new LayoutWrapper(ProxyFactory.newDummyInstance(Layout.class)) {

				@Override
				public boolean isTypeControlPanel() {
					return true;
				}

			});
		themeDisplay.setPermissionChecker(permissionChecker);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletActionRequest.addParameter(Constants.CMD, cmd);
		mockLiferayPortletActionRequest.setMethod(HttpMethods.POST);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		if (permissionChecker.isOmniadmin()) {
			if (!cmd.equals("addLogLevel") &&
				!cmd.equals("dlGenerateAudioPreviews") &&
				!cmd.equals("dlGenerateOpenOfficePreviews") &&
				!cmd.equals("dlGenerateVideoPreviews") &&
				!cmd.equals("updateLogLevels") &&
				!cmd.equals("updatePortalProperties")) {

				try {
					_mvcActionCommand.processAction(
						mockLiferayPortletActionRequest,
						mockLiferayPortletActionResponse);

					Assert.fail(cmd + " should fail by CaptchaTextException");
				}
				catch (Exception exception) {
					Throwable throwable = exception.getCause();

					Assert.assertTrue(
						throwable instanceof CaptchaTextException);
				}
			}
			else {
				Assert.assertTrue(
					_mvcActionCommand.processAction(
						mockLiferayPortletActionRequest,
						mockLiferayPortletActionResponse));
			}
		}
		else {
			if (cmd.equals("updateMail") &&
				permissionChecker.isCompanyAdmin()) {

				Assert.assertTrue(
					_mvcActionCommand.processAction(
						mockLiferayPortletActionRequest,
						mockLiferayPortletActionResponse));
			}
			else {
				Assert.assertFalse(
					_mvcActionCommand.processAction(
						mockLiferayPortletActionRequest,
						mockLiferayPortletActionResponse));
			}
		}
	}

	private void _testUpdateMailPortletPreferences(
		BooleanSupplier booleanSupplier, IntSupplier intSupplier,
		Supplier<String> stringSupplier,
		jakarta.portlet.PortletPreferences portletPreferences) {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		HashMap<String, String> parameters = HashMapBuilder.put(
			"advancedProperties", stringSupplier.get()
		).put(
			"pop3Host", stringSupplier.get()
		).put(
			"pop3Password", stringSupplier.get()
		).put(
			"pop3Port", String.valueOf(intSupplier.getAsInt())
		).put(
			"pop3Secure", stringSupplier.get()
		).put(
			"pop3User", stringSupplier.get()
		).put(
			"popServerNotificationsEnabled",
			String.valueOf(booleanSupplier.getAsBoolean())
		).put(
			"smtpHost", stringSupplier.get()
		).put(
			"smtpPassword", stringSupplier.get()
		).put(
			"smtpPort", String.valueOf(intSupplier.getAsInt())
		).put(
			"smtpSecure", String.valueOf(booleanSupplier.getAsBoolean())
		).put(
			"smtpStartTLSEnable", String.valueOf(booleanSupplier.getAsBoolean())
		).put(
			"smtpUser", stringSupplier.get()
		).build();

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			mockLiferayPortletActionRequest.addParameter(
				entry.getKey(), entry.getValue());
		}

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_updateMail",
			new Class<?>[] {
				ActionRequest.class, jakarta.portlet.PortletPreferences.class
			},
			mockLiferayPortletActionRequest, portletPreferences);

		Assert.assertEquals(
			parameters.get("advancedProperties"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_ADVANCED_PROPERTIES, null));
		Assert.assertEquals(
			parameters.get("pop3Host"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_POP3_HOST, null));
		Assert.assertEquals(
			parameters.get("pop3Password"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_POP3_PASSWORD, null));
		Assert.assertEquals(
			parameters.get("pop3Port"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_POP3_PORT, null));
		Assert.assertEquals(
			Boolean.valueOf(parameters.get("pop3Secure")) ?
				Account.PROTOCOL_POPS : Account.PROTOCOL_POP,
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_STORE_PROTOCOL, null));
		Assert.assertEquals(
			parameters.get("pop3User"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_POP3_USER, null));
		Assert.assertEquals(
			parameters.get("popServerNotificationsEnabled"),
			portletPreferences.getValue(
				PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED, null));
		Assert.assertEquals(
			parameters.get("smtpHost"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_SMTP_HOST, null));
		Assert.assertEquals(
			parameters.get("smtpPassword"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_SMTP_PASSWORD, null));
		Assert.assertEquals(
			parameters.get("smtpPort"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_SMTP_PORT, null));
		Assert.assertEquals(
			Boolean.valueOf(parameters.get("smtpSecure")) ?
				Account.PROTOCOL_SMTPS : Account.PROTOCOL_SMTP,
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_TRANSPORT_PROTOCOL, null));
		Assert.assertEquals(
			parameters.get("smtpStartTLSEnable"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_SMTP_STARTTLS_ENABLE, null));
		Assert.assertEquals(
			parameters.get("smtpUser"),
			portletPreferences.getValue(
				PropsKeys.MAIL_SESSION_MAIL_SMTP_USER, null));
	}

	private static final String[] _COMMANDS = {
		"addLogLevel", "cacheDb", "cacheMulti", "cacheServlet", "cacheSingle",
		"cleanUpAddToPagePermissions",
		"cleanUpLayoutRevisionPortletPreferences",
		"cleanUpOrphanedPortletPreferences", "convertProcess.",
		"dlDeletePreviews", "dlGenerateAudioPreviews",
		"dlGenerateOpenOfficePreviews", "dlGeneratePDFPreviews",
		"dlGenerateVideoPreviews", "gc", "runScript", "shutdown", "threadDump",
		"updateExternalServices", "updateLogLevels", "updateMail",
		"updatePortalProperties", "updatePortalProperties"
	};

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private PortletPreferences _ctPortletPreferences;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Layout _layout;

	@Inject
	private LayoutBranchLocalService _layoutBranchLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Inject
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Inject(filter = "mvc.command.name=/server_admin/edit_server")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private Portal _portal;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

	@DeleteAfterTestRun
	private PortletPreferences _portletPreferences;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}