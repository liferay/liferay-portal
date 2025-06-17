/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.test.util;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFeedConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalFeedLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.util.RSSUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Juan Fernández
 * @author Marcellus Tavares
 * @author Manuel de la Peña
 */
public class JournalTestUtil {

	public static JournalArticle addArticle(long groupId, long folderId)
		throws Exception {

		return addArticle(groupId, folderId, StringPool.BLANK, true);
	}

	public static JournalArticle addArticle(
			long userId, long groupId, long folderId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId, userId);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticle(
			groupId, folderId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), LocaleUtil.getSiteDefault(), false,
			false, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, long classNameId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> contentMap, Locale defaultLocale,
			boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, classNameId, titleMap, descriptionMap,
			contentMap, defaultLocale, null, workflowEnabled, approved,
			serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, long classNameId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> contentMap, Locale defaultLocale,
			Date expirationDate, boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, classNameId, titleMap, descriptionMap,
			contentMap, null, defaultLocale, expirationDate, workflowEnabled,
			approved, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, long classNameId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> contentMap, String layoutUuid,
			Locale defaultLocale, Date expirationDate, boolean workflowEnabled,
			boolean approved, ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, classNameId, StringPool.BLANK, true, titleMap,
			descriptionMap, contentMap, layoutUuid, defaultLocale,
			expirationDate, workflowEnabled, approved, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, long classNameId, String articleId,
			boolean autoArticleId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> contentMap,
			String layoutUuid, Locale defaultLocale, Date expirationDate,
			boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, classNameId, articleId, autoArticleId, titleMap,
			descriptionMap, contentMap, layoutUuid, defaultLocale, null,
			expirationDate, workflowEnabled, approved, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, long classNameId, String articleId,
			boolean autoArticleId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> contentMap,
			String layoutUuid, Locale defaultLocale, Date displayDate,
			Date expirationDate, boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			null, groupId, folderId, classNameId, articleId, autoArticleId,
			titleMap, descriptionMap, contentMap, layoutUuid, defaultLocale,
			displayDate, expirationDate, workflowEnabled, approved,
			serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, long classNameId, String title,
			String description, String content, Locale defaultLocale,
			boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, classNameId, title, description, content,
			defaultLocale, null, workflowEnabled, approved, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, long classNameId, String title,
			String description, String content, Locale defaultLocale,
			Date expirationDate, boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, classNameId, _getLocalizedMap(title),
			_getLocalizedMap(description), _getLocalizedMap(content),
			defaultLocale, expirationDate, workflowEnabled, approved,
			serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, Map<Locale, String> friendlyUrlMap)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticle(
			RandomTestUtil.randomString(), groupId, folderId,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, null, true,
			_getLocalizedMap(RandomTestUtil.randomString()),
			_getLocalizedMap(RandomTestUtil.randomString()), friendlyUrlMap,
			_getLocalizedMap(RandomTestUtil.randomString()), null,
			LocaleUtil.getSiteDefault(), null, null, false, false,
			serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			StringPool.BLANK, true,
			_getLocalizedMap(RandomTestUtil.randomString()),
			_getLocalizedMap(RandomTestUtil.randomString()),
			_getLocalizedMap(RandomTestUtil.randomString()), null,
			LocaleUtil.getSiteDefault(), null, false, false, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, String articleId,
			boolean autoArticleId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticle(
			groupId, folderId, articleId, autoArticleId, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, String articleId,
			boolean autoArticleId, ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			articleId, autoArticleId,
			_getLocalizedMap(RandomTestUtil.randomString()),
			_getLocalizedMap(RandomTestUtil.randomString()),
			_getLocalizedMap(RandomTestUtil.randomString()), null,
			LocaleUtil.getSiteDefault(), null, false, false, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, String title, String content)
		throws Exception {

		return addArticle(
			groupId, folderId, title, title, content,
			LocaleUtil.getSiteDefault(), false, false);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, String title, String content,
			Locale defaultLocale, boolean workflowEnabled, boolean approved)
		throws Exception {

		return addArticle(
			groupId, folderId, title, title, content, defaultLocale,
			workflowEnabled, approved);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, String title, String description,
			String content, Locale defaultLocale, boolean workflowEnabled,
			boolean approved)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticle(
			groupId, folderId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			title, description, content, defaultLocale, workflowEnabled,
			approved, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, long folderId, String articleId, String title,
			String description, String content, Double priority)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		if (priority != null) {
			serviceContext.setAssetPriority(priority);
		}

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticle(
			groupId, folderId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			articleId, false, _getLocalizedMap(title),
			_getLocalizedMap(description), _getLocalizedMap(content), null,
			LocaleUtil.getSiteDefault(), null, false, false, serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, String title, String content)
		throws Exception {

		return addArticle(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, title,
			title, content, LocaleUtil.getSiteDefault(), false, false);
	}

	public static JournalArticle addArticle(
			long groupId, String title, String content, Date expirationDate,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, title, title,
			content, LocaleUtil.getSiteDefault(), expirationDate, false, false,
			serviceContext);
	}

	public static JournalArticle addArticle(
			long groupId, String title, String content, Locale defaultLocale)
		throws Exception {

		return addArticle(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, title,
			title, content, defaultLocale, false, false);
	}

	public static JournalArticle addArticle(
			long groupId, String title, String content,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, title, title,
			content, LocaleUtil.getSiteDefault(), false, false, serviceContext);
	}

	public static JournalArticle addArticle(
			String externalReferenceCode, long groupId, long folderId,
			long classNameId, String articleId, boolean autoArticleId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> friendlyUrlMap, Map<Locale, String> contentMap,
			String layoutUuid, Locale defaultLocale, Date displayDate,
			Date expirationDate, boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			contentMap, LocaleUtil.toLanguageId(defaultLocale));

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			_locales, defaultLocale);

		long ddmGroupId = GetterUtil.getLong(
			serviceContext.getAttribute("ddmGroupId"), groupId);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			ddmGroupId, JournalArticle.class.getName(), ddmForm, defaultLocale);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			ddmGroupId, ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, "${name.getData()}",
			LocaleUtil.getSiteDefault());

		boolean neverExpire = true;

		int expirationDateDay = 0;
		int expirationDateMonth = 0;
		int expirationDateYear = 0;
		int expirationDateHour = 0;
		int expirationDateMinute = 0;

		User user = TestPropsValues.getUser();

		if (expirationDate != null) {
			neverExpire = false;

			Calendar expirationCal = CalendarFactoryUtil.getCalendar(
				user.getTimeZone());

			expirationCal.setTime(expirationDate);

			expirationDateMonth = expirationCal.get(Calendar.MONTH);
			expirationDateDay = expirationCal.get(Calendar.DATE);
			expirationDateYear = expirationCal.get(Calendar.YEAR);
			expirationDateHour = expirationCal.get(Calendar.HOUR_OF_DAY);
			expirationDateMinute = expirationCal.get(Calendar.MINUTE);
		}

		int displayDateMonth = 0;
		int displayDateDay = 0;
		int displayDateYear = 0;
		int displayDateHour = 0;
		int displayDateMinute = 0;

		if (displayDate != null) {
			Calendar displayCal = CalendarFactoryUtil.getCalendar(
				user.getTimeZone());

			displayCal.setTime(displayDate);

			displayDateDay = displayCal.get(Calendar.DATE);
			displayDateMonth = displayCal.get(Calendar.MONTH);
			displayDateYear = displayCal.get(Calendar.YEAR);
			displayDateHour = displayCal.get(Calendar.HOUR_OF_DAY);
			displayDateMinute = displayCal.get(Calendar.MINUTE);
		}

		if (workflowEnabled) {
			serviceContext = (ServiceContext)serviceContext.clone();

			if (approved) {
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);
			}
			else {
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_SAVE_DRAFT);
			}
		}

		return JournalArticleLocalServiceUtil.addArticle(
			externalReferenceCode, serviceContext.getUserId(), groupId,
			folderId, classNameId, 0, articleId, autoArticleId,
			JournalArticleConstants.VERSION_DEFAULT, titleMap, descriptionMap,
			friendlyUrlMap, content, ddmStructure.getStructureId(),
			ddmTemplate.getTemplateKey(), layoutUuid, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, 0, 0, 0, 0,
			0, true, true, false, 0, 0, null, null, null, null, serviceContext);
	}

	public static JournalArticle addArticle(
			String externalReferenceCode, long groupId, long folderId,
			long classNameId, String articleId, boolean autoArticleId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> contentMap, String layoutUuid,
			Locale defaultLocale, Date displayDate, Date expirationDate,
			boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			externalReferenceCode, groupId, folderId, classNameId, articleId,
			autoArticleId, titleMap, descriptionMap, titleMap, contentMap,
			layoutUuid, defaultLocale, displayDate, expirationDate,
			workflowEnabled, approved, serviceContext);
	}

	public static JournalArticle addArticle(
			String externalReferenceCode, long groupId, long folderId,
			String articleId, boolean autoArticleId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticle(
			externalReferenceCode, groupId, folderId,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, articleId,
			autoArticleId, _getLocalizedMap(RandomTestUtil.randomString()),
			_getLocalizedMap(RandomTestUtil.randomString()),
			_getLocalizedMap(RandomTestUtil.randomString()), null,
			LocaleUtil.getSiteDefault(), null, null, false, false,
			serviceContext);
	}

	public static JournalArticle addArticleDefaultValues(
			long userId, long groupId, String title)
		throws Exception {

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm(
			_locales, LocaleUtil.US);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			groupId, JournalArticle.class.getName(), ddmForm, LocaleUtil.US);

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			groupId, ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			TemplateConstants.LANG_TYPE_FTL, getSampleTemplateFTL(),
			LocaleUtil.US);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId, userId);

		return JournalArticleLocalServiceUtil.addArticleDefaultValues(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			PortalUtil.getClassNameId(DDMStructure.class),
			ddmStructure.getStructureId(),
			HashMapBuilder.put(
				LocaleUtil.US, title
			).build(),
			null,
			DDMStructureTestUtil.getSampleStructuredContent(
				HashMapBuilder.put(
					LocaleUtil.SPAIN, "Valor Predefinido"
				).put(
					LocaleUtil.US, "Predefined Value"
				).build(),
				LocaleUtil.US.toString()),
			ddmStructure.getStructureId(), ddmTemplate.getTemplateKey(), null,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true,
			false, 0, 0, null, null, serviceContext);
	}

	public static JournalArticle addArticleWithWorkflow(
			long groupId, boolean approved)
		throws Exception {

		return addArticleWithWorkflow(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, approved);
	}

	public static JournalArticle addArticleWithWorkflow(
			long groupId, long folderId, boolean approved)
		throws Exception {

		return addArticleWithWorkflow(
			groupId, folderId, "title", "content", approved);
	}

	public static JournalArticle addArticleWithWorkflow(
			long groupId, long folderId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> contentMap,
			boolean workflowEnabled, boolean approved)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticleWithWorkflow(
			groupId, folderId, titleMap, descriptionMap, contentMap,
			workflowEnabled, approved, serviceContext);
	}

	public static JournalArticle addArticleWithWorkflow(
			long groupId, long folderId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> contentMap,
			boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return addArticle(
			groupId, folderId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			titleMap, descriptionMap, contentMap, LocaleUtil.getSiteDefault(),
			workflowEnabled, approved, serviceContext);
	}

	public static JournalArticle addArticleWithWorkflow(
			long groupId, long folderId, String title, String content,
			boolean approved)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setCommand(Constants.ADD);
		serviceContext.setLayoutFullURL("http://localhost");

		return addArticleWithWorkflow(
			groupId, folderId, title, content, approved, serviceContext);
	}

	public static JournalArticle addArticleWithWorkflow(
			long groupId, long folderId, String title, String content,
			boolean approved, ServiceContext serviceContext)
		throws Exception {

		return addArticleWithWorkflow(
			groupId, folderId, _getLocalizedMap(title),
			_getLocalizedMap(RandomTestUtil.randomString(50)),
			_getLocalizedMap(content), true, approved, serviceContext);
	}

	public static JournalArticle addArticleWithWorkflow(
			long groupId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> contentMap,
			boolean approved)
		throws Exception {

		return addArticleWithWorkflow(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, titleMap,
			descriptionMap, contentMap, true, approved);
	}

	public static JournalArticle addArticleWithXMLContent(
			long groupId, long folderId, long classNameId, long classPK,
			String xml, String ddmStructureKey, String ddmTemplateKey,
			Locale defaultLocale)
		throws Exception {

		return addArticleWithXMLContent(
			folderId, classNameId, classPK, xml, ddmStructureKey,
			ddmTemplateKey, defaultLocale, null,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static JournalArticle addArticleWithXMLContent(
			long groupId, long folderId, long classNameId, String xml,
			String ddmStructureKey, String ddmTemplateKey)
		throws Exception {

		return addArticleWithXMLContent(
			groupId, folderId, classNameId, xml, ddmStructureKey,
			ddmTemplateKey, LocaleUtil.getSiteDefault());
	}

	public static JournalArticle addArticleWithXMLContent(
			long groupId, long folderId, long classNameId, String xml,
			String ddmStructureKey, String ddmTemplateKey, Locale defaultLocale)
		throws Exception {

		return addArticleWithXMLContent(
			groupId, folderId, classNameId, 0, xml, ddmStructureKey,
			ddmTemplateKey, defaultLocale);
	}

	public static JournalArticle addArticleWithXMLContent(
			long folderId, long classNameId, long classPK, String xml,
			String ddmStructureKey, String ddmTemplateKey, Locale defaultLocale,
			Map<String, byte[]> images, ServiceContext serviceContext)
		throws Exception {

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			PortalUtil.getSiteGroupId(serviceContext.getScopeGroupId()),
			PortalUtil.getClassNameId(JournalArticle.class.getName()),
			ddmStructureKey, true);

		return JournalArticleLocalServiceUtil.addArticle(
			null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			folderId, classNameId, classPK, StringPool.BLANK, true, 0,
			HashMapBuilder.put(
				defaultLocale, "Test Article"
			).build(),
			null,
			HashMapBuilder.put(
				defaultLocale, RandomTestUtil.randomString()
			).build(),
			xml, ddmStructure.getStructureId(), ddmTemplateKey, null, 1, 1,
			1965, 0, 0, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true, true, false,
			0, 0, null, null, images, null, serviceContext);
	}

	public static JournalArticle addArticleWithXMLContent(
			long folderId, long classNameId, String xml, String ddmStructureKey,
			String ddmTemplateKey, Locale defaultLocale,
			Map<String, byte[]> images, ServiceContext serviceContext)
		throws Exception {

		return addArticleWithXMLContent(
			folderId, classNameId, 0, xml, ddmStructureKey, ddmTemplateKey,
			defaultLocale, images, serviceContext);
	}

	public static JournalArticle addArticleWithXMLContent(
			long folderId, long classNameId, String xml, String ddmStructureKey,
			String ddmTemplateKey, Locale defaultLocale,
			ServiceContext serviceContext)
		throws Exception {

		return addArticleWithXMLContent(
			folderId, classNameId, xml, ddmStructureKey, ddmTemplateKey,
			defaultLocale, null, serviceContext);
	}

	public static JournalArticle addArticleWithXMLContent(
			long groupId, String xml, String ddmStructureKey,
			String ddmTemplateKey)
		throws Exception {

		return addArticleWithXMLContent(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml, ddmStructureKey,
			ddmTemplateKey, LocaleUtil.getSiteDefault());
	}

	public static JournalArticle addArticleWithXMLContent(
			long parentFolderId, String xml, String ddmStructureKey,
			String ddmTemplateKey, Map<String, byte[]> images,
			ServiceContext serviceContext)
		throws Exception {

		return addArticleWithXMLContent(
			parentFolderId, JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml,
			ddmStructureKey, ddmTemplateKey, LocaleUtil.getSiteDefault(),
			images, serviceContext);
	}

	public static JournalArticle addArticleWithXMLContent(
			long parentFolderId, String xml, String ddmStructureKey,
			String ddmTemplateKey, ServiceContext serviceContext)
		throws Exception {

		return addArticleWithXMLContent(
			serviceContext.getScopeGroupId(), parentFolderId,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml, ddmStructureKey,
			ddmTemplateKey, LocaleUtil.getSiteDefault());
	}

	public static JournalArticle addArticleWithXMLContent(
			String xml, String ddmStructureKey, String ddmTemplateKey)
		throws Exception {

		return addArticleWithXMLContent(
			TestPropsValues.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml, ddmStructureKey,
			ddmTemplateKey, LocaleUtil.getSiteDefault());
	}

	public static JournalArticle addArticleWithXMLContent(
			String xml, String ddmStructureKey, String ddmTemplateKey,
			Locale defaultLocale)
		throws Exception {

		return addArticleWithXMLContent(
			TestPropsValues.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, xml, ddmStructureKey,
			ddmTemplateKey, defaultLocale);
	}

	public static JournalArticle addArticleWithXMLContent(
			String xml, String ddmStructureKey, String ddmTemplateKey,
			ServiceContext serviceContext)
		throws Exception {

		return addArticleWithXMLContent(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, xml,
			ddmStructureKey, ddmTemplateKey, serviceContext);
	}

	public static JournalFeed addFeed(
			long groupId, long plid, String name, long ddmStructureId,
			String ddmTemplateKey, String rendererTemplateKey)
		throws Exception {

		long userId = TestPropsValues.getUserId();
		String feedId = StringPool.BLANK;
		boolean autoFeedId = true;
		String description = StringPool.BLANK;
		int delta = 0;
		String orderByCol = "modified-date";
		String orderByType = "asc";
		String friendlyURL = _getFeedFriendlyURL(groupId, plid);
		String targetPortletId = StringPool.BLANK;
		String contentField = JournalFeedConstants.WEB_CONTENT_DESCRIPTION;
		String feedFormat = RSSUtil.getFeedTypeFormat(
			RSSUtil.FEED_TYPE_DEFAULT);
		double feedVersion = RSSUtil.getFeedTypeVersion(
			RSSUtil.FEED_TYPE_DEFAULT);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		return JournalFeedLocalServiceUtil.addFeed(
			userId, groupId, feedId, autoFeedId, name, description,
			ddmStructureId, ddmTemplateKey, rendererTemplateKey, delta,
			orderByCol, orderByType, friendlyURL, targetPortletId, contentField,
			feedFormat, feedVersion, serviceContext);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             JournalFolderFixture#addFolder(long, long, long, String)}
	 */
	@Deprecated
	public static JournalFolder addFolder(
			long userId, long groupId, long parentFolderId, String name)
		throws Exception {

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			JournalFolderLocalServiceUtil.getService());

		return journalFolderFixture.addFolder(
			userId, groupId, parentFolderId, name);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             JournalFolderFixture#addFolder(long, long, String)}
	 */
	@Deprecated
	public static JournalFolder addFolder(
			long groupId, long parentFolderId, String name)
		throws Exception {

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			JournalFolderLocalServiceUtil.getService());

		return journalFolderFixture.addFolder(groupId, parentFolderId, name);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             JournalFolderFixture#addFolder(long, String)}
	 */
	@Deprecated
	public static JournalFolder addFolder(long groupId, String name)
		throws Exception {

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			JournalFolderLocalServiceUtil.getService());

		return journalFolderFixture.addFolder(groupId, name);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             JournalFolderFixture#addFolder(long, String, ServiceContext)}
	 */
	@Deprecated
	public static JournalFolder addFolder(
			long parentFolderId, String name, ServiceContext serviceContext)
		throws Exception {

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			JournalFolderLocalServiceUtil.getService());

		return journalFolderFixture.addFolder(
			parentFolderId, name, serviceContext);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             JournalFolderFixture#addFolder(long, String, String,
	 *             ServiceContext)}
	 */
	@Deprecated
	public static JournalFolder addFolder(
			long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws Exception {

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			JournalFolderLocalServiceUtil.getService());

		return journalFolderFixture.addFolder(
			parentFolderId, name, description, serviceContext);
	}

	public static JournalArticle addJournalArticle(
			DataDefinitionResource.Factory dataDefinitionResourceFactory,
			DDMFormField ddmFormField,
			DDMFormValuesToFieldsConverter ddmFormValuesToFieldsConverter,
			Locale defaultLocale, String fieldValue, long groupId,
			JournalConverter journalConverter)
		throws Exception {

		String languageId = LocaleUtil.toLanguageId(defaultLocale);

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", dataDefinitionResourceFactory, groupId,
				JSONUtil.put(
					"availableLanguageIds", JSONUtil.put(languageId)
				).put(
					"dataDefinitionFields",
					JSONUtil.put(
						_getDataDefinitionFieldJSONObject(
							ddmFormField, languageId))
				).put(
					"defaultLanguageId", languageId
				).put(
					"name",
					JSONUtil.put(languageId, RandomTestUtil.randomString())
				).toString(),
				TestPropsValues.getUser());

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			groupId, PortalUtil.getClassNameId(JournalArticle.class.getName()),
			dataDefinition.getDataDefinitionKey());

		Fields fields = ddmFormValuesToFieldsConverter.convert(
			ddmStructure,
			_createDDMFormValues(
				ddmStructure.getDDMForm(),
				_getDDMFormFieldValue(ddmFormField, fieldValue, defaultLocale),
				defaultLocale));

		String content = journalConverter.getContent(
			ddmStructure, fields, groupId);

		return addArticleWithXMLContent(
			groupId, content, dataDefinition.getDataDefinitionKey(), null);
	}

	public static JournalArticle addJournalArticle(
			DataDefinitionResource.Factory dataDefinitionResourceFactory,
			DDMFormField ddmFormField,
			DDMFormValuesToFieldsConverter ddmFormValuesToFieldsConverter,
			String fieldValue, long groupId, JournalConverter journalConverter)
		throws Exception {

		return addJournalArticle(
			dataDefinitionResourceFactory, ddmFormField,
			ddmFormValuesToFieldsConverter,
			PortalUtil.getSiteDefaultLocale(groupId), fieldValue, groupId,
			journalConverter);
	}

	public static void expireArticle(long groupId, JournalArticle article)
		throws PortalException {

		JournalArticleLocalServiceUtil.expireArticle(
			article.getUserId(), article.getGroupId(), article.getArticleId(),
			null, ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static JournalArticle expireArticle(
			long groupId, JournalArticle article, double version)
		throws PortalException {

		return JournalArticleLocalServiceUtil.expireArticle(
			article.getUserId(), article.getGroupId(), article.getArticleId(),
			version, null, ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static String getSampleTemplateFTL() {
		return "${name.getData()}";
	}

	public static String getSampleTemplateVM() {
		return "$name.getData()";
	}

	public static Hits getSearchArticles(long companyId, long groupId)
		throws Exception {

		Indexer<JournalArticle> indexer = IndexerRegistryUtil.getIndexer(
			JournalArticle.class);

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(companyId);
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setKeywords(StringPool.BLANK);

		return indexer.search(searchContext);
	}

	public static int getSearchArticlesCount(long companyId, long groupId)
		throws Exception {

		Hits results = getSearchArticles(companyId, groupId);

		return results.getLength();
	}

	public static JournalArticle updateArticle(JournalArticle article)
		throws Exception {

		return updateArticle(article, RandomTestUtil.randomString());
	}

	public static JournalArticle updateArticle(
			JournalArticle article, Map<Locale, String> titleMap,
			String content, boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return updateArticle(
			article.getUserId(), article, titleMap, content, workflowEnabled,
			approved, serviceContext);
	}

	public static JournalArticle updateArticle(
			JournalArticle article, String title)
		throws Exception {

		return updateArticle(
			article, title, article.getContent(), false, false,
			ServiceContextTestUtil.getServiceContext());
	}

	public static JournalArticle updateArticle(
			JournalArticle article, String title, String content)
		throws Exception {

		return updateArticle(
			article, title, content, false, false,
			ServiceContextTestUtil.getServiceContext());
	}

	public static JournalArticle updateArticle(
			JournalArticle article, String title, String content,
			boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return updateArticle(
			article, _getLocalizedMap(title), content, workflowEnabled,
			approved, serviceContext);
	}

	public static JournalArticle updateArticle(
			long userId, JournalArticle article, Map<Locale, String> titleMap,
			Map<Locale, String> contentMap, Locale defaultLocale,
			boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		String content = DDMStructureTestUtil.getSampleStructuredContent(
			contentMap, LocaleUtil.toLanguageId(defaultLocale));

		return updateArticle(
			userId, article, titleMap, content, null, workflowEnabled, approved,
			serviceContext);
	}

	public static JournalArticle updateArticle(
			long userId, JournalArticle article, Map<Locale, String> titleMap,
			String content, boolean workflowEnabled, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		return updateArticle(
			userId, article, titleMap, content, null, workflowEnabled, approved,
			serviceContext);
	}

	public static JournalArticle updateArticle(
			long userId, JournalArticle article, Map<Locale, String> titleMap,
			String content, Date displayDate, boolean workflowEnabled,
			boolean approved, ServiceContext serviceContext)
		throws Exception {

		if (workflowEnabled) {
			serviceContext = (ServiceContext)serviceContext.clone();

			if (approved) {
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);
			}
			else {
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_SAVE_DRAFT);
			}
		}

		if (displayDate == null) {
			displayDate = article.getDisplayDate();
		}

		int displayDateMonth = 0;
		int displayDateDay = 0;
		int displayDateYear = 0;
		int displayDateHour = 0;
		int displayDateMinute = 0;

		if (displayDate != null) {
			User user = TestPropsValues.getUser();

			Calendar displayCal = CalendarFactoryUtil.getCalendar(
				user.getTimeZone());

			displayCal.setTime(displayDate);

			displayDateMonth = displayCal.get(Calendar.MONTH);
			displayDateDay = displayCal.get(Calendar.DATE);
			displayDateYear = displayCal.get(Calendar.YEAR);
			displayDateHour = displayCal.get(Calendar.HOUR_OF_DAY);
			displayDateMinute = displayCal.get(Calendar.MINUTE);
		}

		serviceContext.setCommand(Constants.UPDATE);
		serviceContext.setLayoutFullURL("http://localhost");

		return JournalArticleLocalServiceUtil.updateArticle(
			userId, article.getGroupId(), article.getFolderId(),
			article.getArticleId(), article.getVersion(), titleMap,
			article.getDescriptionMap(), null, content,
			article.getDDMTemplateKey(), article.getLayoutUuid(),
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, 0, 0, 0, 0, 0, true, 0, 0, 0, 0, 0, true,
			article.isIndexable(), article.isSmallImage(), 0,
			article.getSmallImageSource(), article.getSmallImageURL(), null,
			null, null, serviceContext);
	}

	public static JournalArticle updateArticleWithWorkflow(
			JournalArticle article, boolean approved)
		throws Exception {

		return updateArticle(
			article, RandomTestUtil.randomString(), article.getContent(), false,
			approved, ServiceContextTestUtil.getServiceContext());
	}

	public static JournalArticle updateArticleWithWorkflow(
			long userId, JournalArticle article, boolean approved)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				article.getGroupId(), userId);

		return updateArticle(
			article, RandomTestUtil.randomString(), article.getContent(), false,
			approved, serviceContext);
	}

	private static DDMFormValues _createDDMFormValues(
		DDMForm ddmForm, DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(locale);
		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		return ddmFormValues;
	}

	private static JSONObject _getDataDefinitionFieldJSONObject(
		DDMFormField ddmFormField, String languageId) {

		return JSONUtil.put(
			"customProperties",
			JSONUtil.put(
				"dataType", ddmFormField.getDataType()
			).put(
				"fieldReference", ddmFormField.getFieldReference()
			).put(
				"multiple", ddmFormField.isMultiple()
			).put(
				"options", _getOptionsJSONObject(ddmFormField, languageId)
			)
		).put(
			"defaultValue", _toI18nJSONObject(ddmFormField.getPredefinedValue())
		).put(
			"fieldType", ddmFormField.getType()
		).put(
			"indexType", ddmFormField.getIndexType()
		).put(
			"label", _toI18nJSONObject(ddmFormField.getLabel())
		).put(
			"localizable", ddmFormField.isLocalizable()
		).put(
			"name", ddmFormField.getName()
		).put(
			"readOnly", ddmFormField.isReadOnly()
		).put(
			"repeatable", ddmFormField.isRepeatable()
		).put(
			"required", ddmFormField.isRequired()
		).put(
			"showLabel", ddmFormField.isShowLabel()
		).put(
			"tip", _toI18nJSONObject(ddmFormField.getTip())
		);
	}

	private static DDMFormFieldValue _getDDMFormFieldValue(
		DDMFormField ddmFormField, String fieldValue, Locale locale) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName(ddmFormField.getName());

		Value value = new LocalizedValue(locale);

		value.addString(locale, fieldValue);

		ddmFormFieldValue.setValue(value);

		return ddmFormFieldValue;
	}

	private static String _getFeedFriendlyURL(long groupId, long plid)
		throws Exception {

		String friendlyURL = StringPool.BLANK;

		Group group = GroupLocalServiceUtil.getGroup(groupId);
		Layout layout = LayoutLocalServiceUtil.getLayout(plid);

		if (layout.isPrivateLayout()) {
			if (group.isUser()) {
				friendlyURL = friendlyURL.concat(
					PortalUtil.getPathFriendlyURLPrivateUser());
			}
			else {
				friendlyURL = friendlyURL.concat(
					PortalUtil.getPathFriendlyURLPrivateGroup());
			}
		}
		else {
			friendlyURL = friendlyURL.concat(
				PortalUtil.getPathFriendlyURLPublic());
		}

		friendlyURL = friendlyURL.concat(group.getFriendlyURL());
		friendlyURL = friendlyURL.concat(layout.getFriendlyURL());

		return friendlyURL;
	}

	private static Map<Locale, String> _getLocalizedMap(String value) {
		Map<Locale, String> valuesMap = new HashMap<>();

		for (Locale locale : _locales) {
			valuesMap.put(locale, value);
		}

		return valuesMap;
	}

	private static JSONObject _getOptionsJSONObject(
		DDMFormField ddmFormField, String languageId) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		for (Map.Entry<String, LocalizedValue> entry : options.entrySet()) {
			LocalizedValue localizedValue = entry.getValue();

			jsonArray.put(
				JSONUtil.put(
					"label", localizedValue.getString(locale)
				).put(
					"reference",
					ddmFormFieldOptions.getOptionReference(entry.getKey())
				).put(
					"value", entry.getKey()
				));
		}

		return JSONUtil.put(languageId, jsonArray);
	}

	private static JSONObject _toI18nJSONObject(LocalizedValue localizedValue) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Map<Locale, String> localizedMap = localizedValue.getValues();

		for (Map.Entry<Locale, String> entry : localizedMap.entrySet()) {
			jsonObject.put(
				LocaleUtil.toBCP47LanguageId(entry.getKey()), entry.getValue());
		}

		return jsonObject;
	}

	private static final Class<?> _JOURNAL_UTIL_CLASS;

	private static final Locale[] _locales = {
		LocaleUtil.US, LocaleUtil.GERMANY, LocaleUtil.SPAIN
	};

	static {
		Bundle testBundle = FrameworkUtil.getBundle(JournalTestUtil.class);

		Bundle journalServiceBundle = BundleUtil.getBundle(
			testBundle.getBundleContext(), "com.liferay.journal.service");

		if (journalServiceBundle == null) {
			throw new ExceptionInInitializerError(
				"Unable to find com.liferay.journal.service bundle");
		}

		BundleWiring bundleWiring = journalServiceBundle.adapt(
			BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		try {
			_JOURNAL_UTIL_CLASS = classLoader.loadClass(
				"com.liferay.journal.internal.util.JournalUtil");
		}
		catch (ClassNotFoundException classNotFoundException) {
			throw new ExceptionInInitializerError(classNotFoundException);
		}
	}

}