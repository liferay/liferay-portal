/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.request.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.info.exception.InfoFormValidationException;
import com.liferay.info.field.InfoField;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.memory.DeleteFileFinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextExtractor;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;
import com.liferay.portal.util.PortalImpl;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class EditInfoItemStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.getAdminUser(_group.getCompanyId());

		UserTestUtil.setUser(_user);

		_objectDefinition = _addObjectDefinition(false);

		_classNameId = String.valueOf(
			_portal.getClassNameId(_objectDefinition.getClassName()));

		_layout = _addLayout();
	}

	@FeatureFlag("LPD-21926")
	@Test
	@TestInfo("LPD-50584")
	public void testAddAndUpdateInfoItemFriendlyURL() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition = _addObjectDefinition(true);

		_classNameId = String.valueOf(
			_portal.getClassNameId(_objectDefinition.getClassName()));

		_layout = _addLayout();

		ObjectEntry objectEntry = _testAddInfoItem(
			null, null, null, null, null, null, null,
			StringUtil.toLowerCase(
				StringUtil.removeSubstring(
					RandomTestUtil.randomString(
						LayoutFriendlyURLRandomizerBumper.INSTANCE),
					StringPool.SLASH)),
			null, null, null, null, null, null,
			WorkflowConstants.STATUS_APPROVED);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		String friendlyURL = StringUtil.toLowerCase(
			StringUtil.removeSubstring(
				RandomTestUtil.randomString(
					LayoutFriendlyURLRandomizerBumper.INSTANCE),
				StringPool.SLASH));

		UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(
			RandomTestUtil.randomString(), null, null, null,
			objectEntry.getObjectEntryId(), null, null, null, null, null,
			friendlyURL, null, null, null, null, null, null, null,
			WorkflowConstants.STATUS_APPROVED, null);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			friendlyURL,
			objectEntry.getURLTitle(_objectDefinition.getDefaultLocale()));
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testAddAndUpdateInfoItemWithEnableObjectEntrySchedule()
		throws Exception {

		_objectDefinition.setEnableObjectEntrySchedule(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		_objectDefinitionLocalService.deployObjectDefinition(_objectDefinition);

		InfoFormValidationException.InvalidExpirationDate
			invalidExpirationDate =
				(InfoFormValidationException.InvalidExpirationDate)_execute(
					HashMapBuilder.<String, List<String>>put(
						"ObjectEntry_expirationDate",
						Collections.singletonList("2020-03-01T11:11")
					).build());

		Assert.assertEquals(
			"Expiration date must be a future date.",
			invalidExpirationDate.getLocalizedMessage(LocaleUtil.US));

		String externalReferenceCode = RandomTestUtil.randomString();

		_execute(
			HashMapBuilder.<String, List<String>>put(
				"ObjectEntry_displayDate",
				Collections.singletonList("2020-03-01T11:11")
			).put(
				"ObjectEntry_expirationDate",
				Collections.singletonList("2999-03-01T11:11")
			).put(
				"ObjectEntry_externalReferenceCode",
				Collections.singletonList(externalReferenceCode)
			).put(
				"ObjectEntry_reviewDate",
				Collections.singletonList("2021-03-01T11:11")
			).build());

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, 0,
			_objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2020-03-01T11:11", LocaleUtil.US),
			objectEntry.getDisplayDate());
		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2999-03-01T11:11", LocaleUtil.US),
			objectEntry.getExpirationDate());
		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2021-03-01T11:11", LocaleUtil.US),
			objectEntry.getReviewDate());

		externalReferenceCode = RandomTestUtil.randomString();

		_execute(
			HashMapBuilder.put(
				"classPK",
				Collections.singletonList(
					String.valueOf(objectEntry.getObjectEntryId()))
			).put(
				"ObjectEntry_displayDate",
				Collections.singletonList("2022-03-01T11:11")
			).put(
				"ObjectEntry_expirationDate",
				Collections.singletonList("3000-03-01T11:11")
			).put(
				"ObjectEntry_externalReferenceCode",
				Collections.singletonList(externalReferenceCode)
			).put(
				"ObjectEntry_reviewDate",
				Collections.singletonList("2023-03-01T11:11")
			).build());

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2022-03-01T11:11", LocaleUtil.US),
			objectEntry.getDisplayDate());
		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "3000-03-01T11:11", LocaleUtil.US),
			objectEntry.getExpirationDate());
		Assert.assertEquals(
			externalReferenceCode, objectEntry.getExternalReferenceCode());
		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2023-03-01T11:11", LocaleUtil.US),
			objectEntry.getReviewDate());
	}

	@Test
	public void testAddInfoItemAttachment() throws Exception {
		_testAddInfoItem(
			RandomTestUtil.randomString(), null, null, null, null, null, null,
			null, null, null, null, null, null, null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testAddInfoItemAttachmentWithGuestRole() throws Exception {
		_user = _userLocalService.getGuestUser(_group.getCompanyId());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.addResourcePermission(
			_group.getCompanyId(), _objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_group.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		UserTestUtil.setUser(_user);

		_testAddInfoItem(
			RandomTestUtil.randomString(), null, null, null, null, null, null,
			null, null, null, null, null, null, null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testAddInfoItemInvalidBigDecimalTooBig() throws Exception {
		_testAddInfoItemWithInvalidData("100000000000000", null, null);
	}

	@Test
	public void testAddInfoItemInvalidBigDecimalTooSmall() throws Exception {
		_testAddInfoItemWithInvalidData("-100000000000000", null, null);
	}

	@Test
	public void testAddInfoItemInvalidIntegerTooBig() throws Exception {
		_testAddInfoItemWithInvalidData(null, "2147483648", null);
	}

	@Test
	public void testAddInfoItemInvalidIntegerTooSmall() throws Exception {
		_testAddInfoItemWithInvalidData(null, "-2147483649", null);
	}

	@Test
	@TestInfo("LPS-151402")
	public void testAddInfoItemInvalidListTypeEntryValue() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(
			null, null, null, null, 0, null, null, null, null, null, null, null,
			null, null, "invalid", null, null, null, 0, null);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Object object = SessionErrors.get(uploadPortletRequest, _formItemId);

		Assert.assertNotNull(object);

		Assert.assertTrue(object instanceof InfoFormException);

		Assert.assertEquals(objectEntries.toString(), 0, objectEntries.size());
	}

	@Test
	public void testAddInfoItemInvalidLongTooBig() throws Exception {
		_testAddInfoItemWithInvalidData(null, null, "9007199254740992");
	}

	@Test
	public void testAddInfoItemInvalidLongTooSmall() throws Exception {
		_testAddInfoItemWithInvalidData(null, null, "-9007199254740992");
	}

	@Test
	public void testAddInfoItemMaxValues() throws Exception {
		_testAddInfoItem(
			null, null, "99999999999999.9999999999999999", null,
			"9999999999999998", "999999999", "9007199254740991", null,
			WorkflowConstants.STATUS_APPROVED, RandomTestUtil.randomString());
	}

	@Test
	public void testAddInfoItemMinValues() throws Exception {
		_testAddInfoItem(
			null, null, "-99999999999999.9999999999999999", null,
			"-9999999999999998", "-999999999", "-9007199254740991", null,
			WorkflowConstants.STATUS_APPROVED, RandomTestUtil.randomString());
	}

	@Test
	public void testAddInfoItemRoundedBigDecimalTooLong() throws Exception {
		_testAddInfoItem(
			null, null, "99999999999999.9999999999999999",
			"99999999999999.99999999999999991", null, null, null, null, null,
			null, null, null, null, null, WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testAddInfoItemRoundedDoubleTooLong() throws Exception {
		_testAddInfoItem(
			null, null, null, null, null, "999.9999999999999",
			"999.99999999999991", null, null, null, null, null, null, null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testAddInfoItemWithDisplayPageSuccessMessage()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(),
				_portal.getClassNameId(_objectDefinition.getClassName()), 0,
				true, WorkflowConstants.STATUS_APPROVED);

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _objectDefinition.getClassName());

		InfoForm infoForm = infoItemFormProvider.getInfoForm(
			StringPool.BLANK, _group.getGroupId());

		Assert.assertNotNull(infoForm);

		InfoField infoField = infoForm.getInfoField(
			layoutPageTemplateEntry.getName());

		Assert.assertNotNull(infoField);

		_testAddInfoItem(
			null, null, null, null, infoField.getUniqueId(), null, null, null,
			"123456", "123456", null, null, null, null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@FeatureFlag("LPS-187754")
	@Test
	public void testAddInfoItemWithDraftStatus() throws Exception {
		_testAddInfoItem(
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, WorkflowConstants.STATUS_DRAFT);
	}

	@Test
	public void testAddInfoItemWithEmbeddedSuccessMessage() throws Exception {
		_testAddInfoItem(
			null, "http://localhost:8080/home", null, null, null, null, null,
			null, "123456", "123456", null, null, null, null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testAddInfoItemWithEmptyValues() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(
			null, null, StringPool.BLANK, StringPool.BLANK, 0, StringPool.BLANK,
			StringPool.BLANK, null, StringPool.BLANK, null, null,
			StringPool.BLANK, StringPool.BLANK, null, StringPool.BLANK, null,
			StringPool.BLANK, null, 0, StringPool.BLANK);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		Assert.assertNull(SessionErrors.get(uploadPortletRequest, _formItemId));

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		ObjectEntry objectEntry = objectEntries.get(0);

		_assertEmptyValues(objectEntry);
	}

	@Test
	public void testAddInfoItemWithPageSuccessMessage() throws Exception {
		_testAddInfoItem(
			null, null, null, null, null, null, null, null, "123456", "123456",
			null, null, "http://localhost:8080/home", null,
			WorkflowConstants.STATUS_APPROVED);
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testUpdateInfoItem() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		ListTypeEntry listTypeEntry1 = _listTypeEntries.get(0);
		ListTypeEntry listTypeEntry2 = _listTypeEntries.get(1);

		UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(
			RandomTestUtil.randomString(), null,
			"-99999999999999.9999999999999999", Boolean.TRUE.toString(), 0,
			"2023-03-01T11:08", "2023-03-01", null, "-999.9999999999999",
			"2999-03-01T11:11", null, "-123456", "-9007199254740991",
			Arrays.asList(listTypeEntry1.getKey(), listTypeEntry2.getKey()),
			listTypeEntry1.getKey(), "2100-03-01T11:11", "<p>TITLE</p>", null,
			0, null);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		mockHttpServletResponse = new MockHttpServletResponse();

		unsyncStringWriter = new UnsyncStringWriter();

		pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		ObjectEntry objectEntry = objectEntries.get(0);

		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2999-03-01T11:11", LocaleUtil.US),
			objectEntry.getExpirationDate());
		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2100-03-01T11:11", LocaleUtil.US),
			objectEntry.getReviewDate());

		ListTypeEntry listTypeEntry3 = _listTypeEntries.get(2);

		uploadPortletRequest = _getUploadPortletRequest(
			"file", null, "99999999999999.9999999999999999",
			Boolean.FALSE.toString(), objectEntry.getObjectEntryId(),
			"2023-03-01T11:11", "2020-03-01", null, "999.9999999999999", null,
			null, "123456", "9007199254740991",
			Arrays.asList(listTypeEntry2.getKey(), listTypeEntry3.getKey()),
			listTypeEntry2.getKey(), null, "<p>SUBTITLE</p>", null, 0, null);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertNull(objectEntry.getExpirationDate());
		Assert.assertNull(objectEntry.getReviewDate());

		Map<String, Serializable> values = objectEntry.getValues();

		long fileEntryId = GetterUtil.getLong(values.get("myAttachment"));

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			fileEntryId);

		Assert.assertEquals(
			"file",
			StringUtil.removeSubstring(
				_textExtractor.extractText(dlFileEntry.getContentStream(), -1),
				StringPool.NEW_LINE));

		Assert.assertEquals(
			Boolean.FALSE.toString(), String.valueOf(values.get("myBoolean")));
		Assert.assertEquals(
			DateUtil.formatDate("yyyy-MM-dd", "2020-03-01", LocaleUtil.US),
			DateUtil.formatDate(
				"yyyy-MM-dd", String.valueOf(values.get("myDate")),
				LocaleUtil.US));
		Assert.assertEquals(
			DateUtil.parseDate(
				"yyyy-MM-dd'T'HH:mm", "2023-03-01T11:11", LocaleUtil.US),
			DateUtil.parseDate(
				"yyyy-MM-dd HH:mm", String.valueOf(values.get("myDateTime")),
				LocaleUtil.US));

		DecimalFormat decimalFormat = new DecimalFormat(
			"0", new DecimalFormatSymbols(LocaleUtil.ENGLISH));

		decimalFormat.setMaximumFractionDigits(16);

		Assert.assertEquals(
			"999.9999999999999", decimalFormat.format(values.get("myDecimal")));

		Assert.assertEquals("123456", String.valueOf(values.get("myInteger")));
		Assert.assertEquals(
			"9007199254740991", String.valueOf(values.get("myLongInteger")));
		Assert.assertEquals(
			"99999999999999.9999999999999999",
			String.valueOf(values.get("myPrecisionDecimal")));
		Assert.assertEquals(
			listTypeEntry2.getKey() + StringPool.COMMA_AND_SPACE +
				listTypeEntry3.getKey(),
			String.valueOf(values.get("myMultiselectPicklist")));
		Assert.assertEquals(
			listTypeEntry2.getKey(), String.valueOf(values.get("myPicklist")));
		Assert.assertEquals(
			"<p>SUBTITLE</p>", String.valueOf(values.get("myRichText")));
	}

	@Test
	public void testUpdateInfoItemWithCheckboxNames() throws Exception {
		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.addHeader(
			HttpHeaders.REFERER, "http://localhost:8080/error");
		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		Map<String, List<String>> regularParameters =
			HashMapBuilder.<String, List<String>>put(
				"classNameId", Collections.singletonList(_classNameId)
			).put(
				"formItemId", Collections.singletonList(_formItemId)
			).put(
				"groupId",
				Collections.singletonList(String.valueOf(_group.getGroupId()))
			).put(
				"myBoolean", Collections.singletonList(Boolean.TRUE.toString())
			).put(
				"p_l_id",
				Collections.singletonList(String.valueOf(_layout.getPlid()))
			).put(
				"p_l_mode", Collections.singletonList(Constants.VIEW)
			).put(
				"plid",
				Collections.singletonList(String.valueOf(_layout.getPlid()))
			).put(
				"segmentsExperienceId",
				Collections.singletonList(
					String.valueOf(_defaultSegmentsExperienceId))
			).build();

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					mockMultipartHttpServletRequest, null, regularParameters),
				null, RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest,
			new PipingServletResponse(
				mockHttpServletResponse, new UnsyncStringWriter()));

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		ObjectEntry objectEntry = objectEntries.get(0);

		regularParameters.put(
			"checkboxNames",
			Collections.singletonList("ObjectField_myBoolean"));
		regularParameters.put(
			"classNameId", Collections.singletonList(_classNameId));
		regularParameters.put(
			"classPK",
			Collections.singletonList(
				String.valueOf(objectEntry.getObjectEntryId())));
		regularParameters.remove("myBoolean");

		mockHttpServletResponse = new MockHttpServletResponse();

		uploadPortletRequest = UploadTestUtil.createUploadPortletRequest(
			UploadTestUtil.createUploadServletRequest(
				mockMultipartHttpServletRequest, null, regularParameters),
			null, RandomTestUtil.randomString());

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest,
			new PipingServletResponse(
				mockHttpServletResponse, new UnsyncStringWriter()));

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			Boolean.FALSE.toString(), String.valueOf(values.get("myBoolean")));
	}

	@Test
	public void testUpdateInfoItemWithEmptyValues() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		ListTypeEntry listTypeEntry = _listTypeEntries.get(0);

		UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(
			null, null, "-99999999999999.9999999999999999",
			Boolean.TRUE.toString(), 0, "2023-03-01T11:08", "2023-03-01", null,
			"-999.9999999999999", null, null, "-123456", "-9007199254740991",
			null, listTypeEntry.getKey(), null, "<p>TITLE</p>", null, 0, null);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		mockHttpServletResponse = new MockHttpServletResponse();

		unsyncStringWriter = new UnsyncStringWriter();

		pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		ObjectEntry objectEntry = objectEntries.get(0);

		uploadPortletRequest = _getUploadPortletRequest(
			null, null, StringPool.BLANK, StringPool.BLANK,
			objectEntry.getObjectEntryId(), StringPool.BLANK, StringPool.BLANK,
			null, StringPool.BLANK, null, null, StringPool.BLANK,
			StringPool.BLANK, null, StringPool.BLANK, null, StringPool.BLANK,
			null, 0, StringPool.BLANK);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		Assert.assertNull(SessionErrors.get(uploadPortletRequest, _formItemId));

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		_assertEmptyValues(objectEntry);
	}

	@Test
	public void testUpdateInfoItemWithWrongRedirect() throws Exception {
		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.addHeader(
			HttpHeaders.REFERER, "https://example.com/error");
		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					mockMultipartHttpServletRequest, null,
					HashMapBuilder.put(
						"classNameId", Collections.singletonList(_classNameId)
					).put(
						"formItemId", Collections.singletonList(_formItemId)
					).put(
						"groupId",
						Collections.singletonList(
							String.valueOf(_group.getGroupId()))
					).put(
						"p_l_id",
						Collections.singletonList(
							String.valueOf(_layout.getPlid()))
					).put(
						"p_l_mode", Collections.singletonList(Constants.VIEW)
					).put(
						"plid",
						Collections.singletonList(
							String.valueOf(_layout.getPlid()))
					).put(
						"segmentsExperienceId",
						Collections.singletonList(
							String.valueOf(_defaultSegmentsExperienceId))
					).build()),
				null, RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PortalImpl.class.getName(), LoggerTestUtil.WARN)) {

			try {
				_editInfoItemStrutsAction.execute(
					uploadPortletRequest,
					new PipingServletResponse(
						mockHttpServletResponse, new UnsyncStringWriter()));

				Assert.fail();
			}
			catch (IllegalArgumentException illegalArgumentException) {
				Assert.assertEquals(
					"Redirect URL must not be null",
					illegalArgumentException.getMessage());
			}

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Redirect URL https://example.com/error is not allowed",
				logEntry.getMessage());
		}
	}

	private Layout _addLayout() throws Exception {
		Layout layout = _layoutLocalService.addLayout(
			null, _user.getUserId(), _group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_CONTENT,
			UnicodePropertiesBuilder.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED, "true"
			).buildString(),
			false, false, Collections.emptyMap(), 0,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		_defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem formStyledLayoutStructureItem =
			layoutStructure.addFormStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		_formItemId = formStyledLayoutStructureItem.getItemId();

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				layout.getPlid(), _defaultSegmentsExperienceId,
				layoutStructure.toString());

		return layout;
	}

	private ObjectDefinition _addObjectDefinition(
			boolean enableFriendlyURLCustomization)
		throws Exception {

		_listTypeEntries.add(
			ListTypeEntryUtil.createListTypeEntry(
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString())));
		_listTypeEntries.add(
			ListTypeEntryUtil.createListTypeEntry(
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString())));
		_listTypeEntries.add(
			ListTypeEntryUtil.createListTypeEntry(
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString())));

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, _listTypeEntries);

		List<ObjectField> objectFields = Arrays.asList(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myAttachment"
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting("acceptedFileExtensions", "txt"),
					_createObjectFieldSetting("fileSource", "userComputer"),
					_createObjectFieldSetting("maximumFileSize", "100"))
			).build(),
			new DateTimeObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myDateTime"
			).objectFieldSettings(
				Collections.singletonList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_TIME_STORAGE,
						ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC))
			).build(),
			new PicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).name(
				"myPicklist"
			).build(),
			new MultiselectPicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).name(
				"myMultiselectPicklist"
			).build(),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE,
				ObjectFieldConstants.DB_TYPE_DATE,
				RandomTestUtil.randomString(), "myDate", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
				ObjectFieldConstants.DB_TYPE_DOUBLE,
				RandomTestUtil.randomString(), "myDecimal", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
				ObjectFieldConstants.DB_TYPE_BOOLEAN,
				RandomTestUtil.randomString(), "myBoolean", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
				ObjectFieldConstants.DB_TYPE_INTEGER,
				RandomTestUtil.randomString(), "myInteger", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				ObjectFieldConstants.DB_TYPE_LONG,
				RandomTestUtil.randomString(), "myLongInteger", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL,
				RandomTestUtil.randomString(), "myPrecisionDecimal", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING,
				RandomTestUtil.randomString(), "myRichText", false));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				_user.getUserId(), 0, null, false, true,
				enableFriendlyURLCustomization, true,
				FeatureFlagManagerUtil.isEnabled(
					TestPropsValues.getCompanyId(), "LPD-21926"),
				true, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null,
				"control_panel.sites",
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields, Collections.emptyList());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				_user.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myText"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			_user.getUserId(), objectDefinition.getObjectDefinitionId());
	}

	private void _assertEmptyValues(ObjectEntry objectEntry) {
		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			Boolean.FALSE.toString(), String.valueOf(values.get("myBoolean")));
		Assert.assertNull(values.get("myDate"));
		Assert.assertNull(values.get("myDateTime"));
		Assert.assertEquals("0.0", String.valueOf(values.get("myDecimal")));
		Assert.assertEquals("0", String.valueOf(values.get("myInteger")));
		Assert.assertEquals("0", String.valueOf(values.get("myLongInteger")));
		Assert.assertTrue(
			Validator.isNull(String.valueOf(values.get("myPicklist"))));
		Assert.assertEquals(
			0, GetterUtil.getLong(values.get("myPrecisionDecimal")));
		Assert.assertEquals(
			StringPool.BLANK, String.valueOf(values.get("myRichText")));
	}

	private FileItem _createFileItem(byte[] bytes) throws Exception {
		Path tempFilePath = Files.createTempFile(null, ".txt");

		Files.write(tempFilePath, bytes);

		File tempFile = tempFilePath.toFile();

		FinalizeManager.register(
			tempFile, new DeleteFileFinalizeAction(tempFile.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);

		return ProxyUtil.newDelegateProxyInstance(
			FileItem.class.getClassLoader(), FileItem.class,
			new Object() {

				public void delete() {
					tempFile.delete();
				}

				public String getContentType() {
					return StringPool.BLANK;
				}

				public String getFileName() {
					return tempFile.getName();
				}

				public String getFullFileName() {
					return tempFile.getName();
				}

				public InputStream getInputStream() throws IOException {
					return new FileInputStream(tempFile);
				}

				public long getSize() {
					return bytes.length;
				}

				public int getSizeThreshold() {
					return 1024;
				}

				public File getStoreLocation() {
					return tempFile;
				}

				public boolean isFormField() {
					return true;
				}

				public boolean isInMemory() {
					return false;
				}

			},
			null);
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private Object _execute(Map<String, List<String>> regularParameters)
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.addHeader(
			HttpHeaders.REFERER, "http://localhost:8080/error");
		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		UploadPortletRequest uploadPortletRequest =
			UploadTestUtil.createUploadPortletRequest(
				UploadTestUtil.createUploadServletRequest(
					mockMultipartHttpServletRequest, null,
					HashMapBuilder.<String, List<String>>put(
						"classNameId", Collections.singletonList(_classNameId)
					).put(
						"formItemId", Collections.singletonList(_formItemId)
					).put(
						"groupId",
						Collections.singletonList(
							String.valueOf(_group.getGroupId()))
					).put(
						"myBoolean",
						Collections.singletonList(Boolean.TRUE.toString())
					).put(
						"p_l_id",
						Collections.singletonList(
							String.valueOf(_layout.getPlid()))
					).put(
						"p_l_mode", Collections.singletonList(Constants.VIEW)
					).put(
						"plid",
						Collections.singletonList(
							String.valueOf(_layout.getPlid()))
					).put(
						"segmentsExperienceId",
						Collections.singletonList(
							String.valueOf(_defaultSegmentsExperienceId))
					).putAll(
						regularParameters
					).build()),
				null, RandomTestUtil.randomString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest,
			new PipingServletResponse(
				mockHttpServletResponse, new UnsyncStringWriter()));

		return SessionErrors.get(uploadPortletRequest, _formItemId);
	}

	private MockMultipartHttpServletRequest _getMultipartHttpServletRequest(
		byte[] bytes, String fileNameParameter) {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.addFile(
			new MockMultipartFile(fileNameParameter, bytes));
		mockMultipartHttpServletRequest.setContent(bytes);
		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());
		mockMultipartHttpServletRequest.setCharacterEncoding("UTF-8");

		MockHttpSession mockHttpSession = new MockHttpSession();

		mockHttpSession.setAttribute(ProgressTracker.PERCENT, new Object());

		mockMultipartHttpServletRequest.setSession(mockHttpSession);

		return mockMultipartHttpServletRequest;
	}

	private UploadPortletRequest _getUploadPortletRequest(
			String attachmentValue, String backURL, String bigDecimalValueInput,
			String booleanValueInput, long classPK, String dateTimeValueInput,
			String dateValueInput, String displayPage, String doubleValueInput,
			String expirationDate, String friendlyURL, String integerValueInput,
			String longValueInput, List<String> multiselectPicklistValueInput,
			String picklistValueInput, String reviewDate,
			String richTextValueInput, String redirect, int status,
			String stringValue)
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		Map<String, FileItem[]> fileParameters = null;

		if (attachmentValue != null) {
			byte[] bytes = attachmentValue.getBytes(StandardCharsets.UTF_8);

			fileParameters = HashMapBuilder.put(
				"ObjectField_myAttachment",
				new FileItem[] {_createFileItem(bytes)}
			).build();

			mockMultipartHttpServletRequest = _getMultipartHttpServletRequest(
				bytes, "ObjectField_myAttachment");
		}

		mockMultipartHttpServletRequest.addHeader(
			HttpHeaders.REFERER, "http://localhost:8080/error");

		return UploadTestUtil.createUploadPortletRequest(
			UploadTestUtil.createUploadServletRequest(
				mockMultipartHttpServletRequest, fileParameters,
				HashMapBuilder.put(
					"backURL",
					() -> {
						if (Validator.isNotNull(backURL)) {
							return Collections.singletonList(backURL);
						}

						return null;
					}
				).put(
					"classNameId", Collections.singletonList(_classNameId)
				).put(
					"classPK",
					() -> {
						if (classPK <= 0) {
							return null;
						}

						return Collections.singletonList(
							String.valueOf(classPK));
					}
				).put(
					"classTypeId", Collections.singletonList("0")
				).put(
					"displayPage",
					() -> {
						if (Validator.isNotNull(displayPage)) {
							return Collections.singletonList(displayPage);
						}

						return null;
					}
				).put(
					"formItemId", Collections.singletonList(_formItemId)
				).put(
					"groupId",
					Collections.singletonList(
						String.valueOf(_group.getGroupId()))
				).put(
					"ObjectEntry_expirationDate",
					Collections.singletonList(expirationDate)
				).put(
					"ObjectEntry_objectEntryFriendlyURL",
					() -> {
						if (friendlyURL == null) {
							return null;
						}

						return Collections.singletonList(friendlyURL);
					}
				).put(
					"ObjectField_myBoolean",
					() -> {
						if (booleanValueInput == null) {
							return null;
						}

						return Collections.singletonList(booleanValueInput);
					}
				).put(
					"ObjectField_myDate",
					() -> {
						if (dateValueInput != null) {
							return Collections.singletonList(dateValueInput);
						}

						return null;
					}
				).put(
					"ObjectField_myDateTime",
					() -> {
						if (dateTimeValueInput == null) {
							return null;
						}

						return Collections.singletonList(dateTimeValueInput);
					}
				).put(
					"ObjectField_myDecimal",
					() -> {
						if (doubleValueInput == null) {
							return null;
						}

						return Collections.singletonList(doubleValueInput);
					}
				).put(
					"ObjectField_myInteger",
					() -> {
						if (integerValueInput == null) {
							return null;
						}

						return Collections.singletonList(integerValueInput);
					}
				).put(
					"ObjectField_myLongInteger",
					() -> {
						if (longValueInput == null) {
							return null;
						}

						return Collections.singletonList(longValueInput);
					}
				).put(
					"ObjectField_myMultiselectPicklist",
					() -> {
						if (multiselectPicklistValueInput == null) {
							return null;
						}

						return multiselectPicklistValueInput;
					}
				).put(
					"ObjectField_myPicklist",
					() -> {
						if (picklistValueInput == null) {
							return null;
						}

						return Collections.singletonList(picklistValueInput);
					}
				).put(
					"ObjectField_myPrecisionDecimal",
					() -> {
						if (bigDecimalValueInput == null) {
							return null;
						}

						return Collections.singletonList(bigDecimalValueInput);
					}
				).put(
					"ObjectField_myRichText",
					() -> {
						if (richTextValueInput == null) {
							return null;
						}

						return Collections.singletonList(richTextValueInput);
					}
				).put(
					"ObjectField_myText", Collections.singletonList(stringValue)
				).put(
					"ObjectEntry_objectEntryFriendlyURL_" +
						_objectDefinition.getDefaultLanguageId(),
					() -> {
						if (friendlyURL == null) {
							return null;
						}

						return Collections.singletonList(friendlyURL);
					}
				).put(
					"ObjectEntry_reviewDate",
					Collections.singletonList(reviewDate)
				).put(
					"p_l_id",
					Collections.singletonList(String.valueOf(_layout.getPlid()))
				).put(
					"p_l_mode", Collections.singletonList(Constants.VIEW)
				).put(
					"plid",
					Collections.singletonList(String.valueOf(_layout.getPlid()))
				).put(
					"redirect",
					() -> {
						if (Validator.isNotNull(redirect)) {
							return Collections.singletonList(redirect);
						}

						return null;
					}
				).put(
					"segmentsExperienceId",
					Collections.singletonList(
						String.valueOf(_defaultSegmentsExperienceId))
				).put(
					"status", Collections.singletonList(String.valueOf(status))
				).build()),
			null, RandomTestUtil.randomString());
	}

	private void _processEvents(
			MockHttpServletResponse mockHttpServletResponse,
			UploadPortletRequest uploadPortletRequest, User user)
		throws Exception {

		uploadPortletRequest.setAttribute(
			WebKeys.CURRENT_URL, "/portal/edit_info_item");
		uploadPortletRequest.setAttribute(WebKeys.USER, user);

		EventsProcessorUtil.process(
			PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
			PropsValues.SERVLET_SERVICE_EVENTS_PRE, uploadPortletRequest,
			mockHttpServletResponse);
	}

	private void _testAddInfoItem(
			String attachmentValue, String backURL, String bigDecimalValue,
			String displayPage, String doubleValue, String integerValue,
			String longValue, String redirect, int status, String stringValue)
		throws Exception {

		_testAddInfoItem(
			attachmentValue, backURL, bigDecimalValue, bigDecimalValue,
			displayPage, doubleValue, doubleValue, null, integerValue,
			integerValue, longValue, longValue, redirect, stringValue, status);
	}

	private ObjectEntry _testAddInfoItem(
			String attachmentValue, String backURL,
			String bigDecimalValueExpected, String bigDecimalValueInput,
			String displayPage, String doubleValueExpected,
			String doubleValueInput, String friendlyURL,
			String integerValueExpected, String integerValueInput,
			String longValueExpected, String longValueInput, String redirect,
			String stringValue, int status)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(
			attachmentValue, backURL, bigDecimalValueInput, null, 0, null, null,
			displayPage, doubleValueInput, null, friendlyURL, integerValueInput,
			longValueInput, null, null, null, null, redirect, status,
			stringValue);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertNull(SessionErrors.get(uploadPortletRequest, _formItemId));

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		ObjectEntry objectEntry = objectEntries.get(0);

		Assert.assertEquals(status, objectEntry.getStatus());

		Map<String, Serializable> values = objectEntry.getValues();

		if (attachmentValue != null) {
			long fileEntryId = GetterUtil.getLong(values.get("myAttachment"));

			DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
				fileEntryId);

			Assert.assertEquals(
				attachmentValue,
				StringUtil.removeSubstring(
					_textExtractor.extractText(
						dlFileEntry.getContentStream(), -1),
					StringPool.NEW_LINE));
		}

		if (Validator.isNotNull(backURL)) {
			Assert.assertEquals(
				backURL, pipingServletResponse.getHeader("Location"));
		}

		if (Validator.isNotNull(displayPage)) {
			String locationHeader = pipingServletResponse.getHeader("Location");

			Assert.assertNotNull(locationHeader);
			Assert.assertTrue(locationHeader.contains("/e/"));
		}

		if (doubleValueInput != null) {
			DecimalFormat decimalFormat = new DecimalFormat(
				"0", new DecimalFormatSymbols(LocaleUtil.ENGLISH));

			decimalFormat.setMaximumFractionDigits(16);

			Assert.assertEquals(
				doubleValueExpected,
				decimalFormat.format(values.get("myDecimal")));
		}

		if (friendlyURL != null) {
			Assert.assertEquals(
				friendlyURL,
				objectEntry.getURLTitle(_objectDefinition.getDefaultLocale()));
		}

		if (integerValueInput != null) {
			Assert.assertEquals(
				integerValueExpected, String.valueOf(values.get("myInteger")));
		}

		if (longValueInput != null) {
			Assert.assertEquals(
				longValueExpected, String.valueOf(values.get("myLongInteger")));
		}

		if (bigDecimalValueInput != null) {
			Assert.assertEquals(
				bigDecimalValueExpected,
				String.valueOf(values.get("myPrecisionDecimal")));
		}

		if (stringValue != null) {
			Assert.assertEquals(stringValue, values.get("myText"));
		}

		if (Validator.isNotNull(redirect)) {
			Assert.assertEquals(
				redirect, pipingServletResponse.getHeader("Location"));
		}

		return objectEntry;
	}

	private void _testAddInfoItemWithInvalidData(
			String bigDecimalValueInput, String integerValueInput,
			String longValueInput)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			mockHttpServletResponse, unsyncStringWriter);

		UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(
			null, null, bigDecimalValueInput, null, 0, null, null, null, null,
			null, null, integerValueInput, longValueInput, null, null, null,
			null, null, 0, null);

		_processEvents(mockHttpServletResponse, uploadPortletRequest, _user);

		_editInfoItemStrutsAction.execute(
			uploadPortletRequest, pipingServletResponse);

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Object object = SessionErrors.get(uploadPortletRequest, _formItemId);

		Assert.assertNotNull(object);

		Assert.assertTrue(object instanceof InfoFormException);

		Assert.assertEquals(objectEntries.toString(), 0, objectEntries.size());
	}

	private String _classNameId;
	private long _defaultSegmentsExperienceId;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.info.internal.request.struts.EditInfoItemStrutsAction"
	)
	private StrutsAction _editInfoItemStrutsAction;

	private String _formItemId;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	private final List<ListTypeEntry> _listTypeEntries = new ArrayList<>();

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private TextExtractor _textExtractor;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}