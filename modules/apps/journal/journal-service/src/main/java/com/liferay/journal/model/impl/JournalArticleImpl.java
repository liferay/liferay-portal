/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.model.impl;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesConverterUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalServiceUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
@JSON(strict = true)
public class JournalArticleImpl extends JournalArticleBaseImpl {

	public static void setDDMFormValuesToFieldsConverter(
		DDMFormValuesToFieldsConverter ddmFormValuesToFieldsConverter) {

		_ddmFormValuesToFieldsConverter = ddmFormValuesToFieldsConverter;
	}

	public static void setJournalConverter(JournalConverter journalConverter) {
		_journalConverter = journalConverter;
	}

	@Override
	public Folder addImagesFolder() throws PortalException {
		if (_imagesFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return PortletFileRepositoryUtil.getPortletFolder(_imagesFolderId);
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			getGroupId(), JournalConstants.SERVICE_NAME, serviceContext);

		Folder folder = PortletFileRepositoryUtil.addPortletFolder(
			PortalUtil.getValidUserId(getCompanyId(), getUserId()),
			repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			String.valueOf(getResourcePrimKey()), serviceContext);

		_imagesFolderId = folder.getFolderId();

		return folder;
	}

	@Override
	public String buildTreePath() throws PortalException {
		if (getFolderId() == JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return StringPool.SLASH;
		}

		JournalFolder folder = getFolder();

		return folder.buildTreePath();
	}

	@Override
	public Object clone() {
		JournalArticleImpl journalArticleImpl =
			(JournalArticleImpl)super.clone();

		journalArticleImpl.setDescriptionMap(getDescriptionMap());
		journalArticleImpl.setTitleMap(getTitleMap());

		return journalArticleImpl;
	}

	@Override
	public String getArticleImageURL(ThemeDisplay themeDisplay) {
		if (!isSmallImage()) {
			return null;
		}

		if (getSmallImageSource() ==
				JournalArticleConstants.
					SMALL_IMAGE_SOURCE_DOCUMENTS_AND_MEDIA) {

			long smallImageId = getSmallImageId();

			if (smallImageId <= 0) {
				return null;
			}

			try {
				FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
					smallImageId);

				return DLURLHelperUtil.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), themeDisplay,
					StringPool.BLANK);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}

			return null;
		}

		if (getSmallImageSource() ==
				JournalArticleConstants.SMALL_IMAGE_SOURCE_URL) {

			return getSmallImageURL();
		}

		if (getSmallImageSource() !=
				JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER) {

			return null;
		}

		return StringBundler.concat(
			themeDisplay.getPathImage(), "/journal/article?img_id=",
			getSmallImageId(), "&t=",
			WebServerServletTokenUtil.getToken(getSmallImageId()));
	}

	@Override
	public JournalArticleResource getArticleResource() throws PortalException {
		return JournalArticleResourceLocalServiceUtil.getArticleResource(
			getResourcePrimKey());
	}

	@Override
	public String getArticleResourceUuid() throws PortalException {
		JournalArticleResource articleResource = getArticleResource();

		return articleResource.getUuid();
	}

	@Override
	public String[] getAvailableLanguageIds() {
		Set<String> availableLanguageIds = new TreeSet<>();

		availableLanguageIds.addAll(
			JournalArticleLocalServiceUtil.getArticleLocalizationLanguageIds(
				getCompanyId(), getId()));

		List<DDMFieldAttribute> ddmFieldAttributes =
			DDMFieldLocalServiceUtil.getDDMFieldAttributes(
				getId(), "availableLanguageIds");

		if (ListUtil.isNotEmpty(ddmFieldAttributes)) {
			DDMFieldAttribute ddmFieldAttribute = ddmFieldAttributes.get(0);

			availableLanguageIds.addAll(
				StringUtil.split(ddmFieldAttribute.getAttributeValue()));
		}

		return availableLanguageIds.toArray(new String[0]);
	}

	@JSON
	@Override
	public String getContent() {
		DDMStructure ddmStructure = getDDMStructure();

		if (ddmStructure == null) {
			return null;
		}

		return _getContent(
			ddmStructure,
			DDMFieldLocalServiceUtil.getDDMFormValues(
				ddmStructure.getDDMForm(), getId()));
	}

	@Override
	public String getContentByLocale(String languageId) {
		Document document = getDocumentByLocale(languageId);

		return document.asXML();
	}

	@Override
	public DDMFormValues getDDMFormValues() {
		if (_ddmFormValues == null) {
			_ddmFormValues = getDDMFormValues(true);
		}

		return _ddmFormValues;
	}

	@Override
	public DDMFormValues getDDMFormValues(
		boolean addMissingDDMFormFieldValues) {

		DDMStructure ddmStructure = getDDMStructure();

		if (ddmStructure == null) {
			return null;
		}

		DDMForm ddmForm = ddmStructure.getDDMForm();

		DDMFormValues ddmFormValues = DDMFieldLocalServiceUtil.getDDMFormValues(
			ddmForm, getId());

		if ((ddmFormValues != null) && addMissingDDMFormFieldValues) {
			ddmFormValues.setDDMFormFieldValues(
				DDMFormValuesConverterUtil.addMissingDDMFormFieldValues(
					ddmForm.getDDMFormFields(),
					ddmFormValues.getDDMFormFieldValuesMap(true)));
		}

		return ddmFormValues;
	}

	@Override
	public DDMStructure getDDMStructure() {
		return DDMStructureLocalServiceUtil.fetchStructure(getDDMStructureId());
	}

	@Override
	public String getDDMStructureKey() {
		DDMStructure ddmStructure = getDDMStructure();

		if (ddmStructure == null) {
			return StringPool.BLANK;
		}

		return ddmStructure.getStructureKey();
	}

	@Override
	public DDMTemplate getDDMTemplate() {
		return DDMTemplateLocalServiceUtil.fetchTemplate(
			PortalUtil.getSiteGroupId(getGroupId()),
			ClassNameLocalServiceUtil.getClassNameId(DDMStructure.class),
			getDDMTemplateKey(), true);
	}

	@JSON
	@Override
	public String getDescription() {
		String description =
			JournalArticleLocalServiceUtil.getArticleDescription(
				getCompanyId(), getId(), getDefaultLanguageId());

		if (description == null) {
			return StringPool.BLANK;
		}

		return description;
	}

	@Override
	public String getDescription(Locale locale) {
		String description =
			JournalArticleLocalServiceUtil.getArticleDescription(
				getCompanyId(), getId(), locale);

		if (description == null) {
			return getDescription();
		}

		return description;
	}

	@Override
	public String getDescription(Locale locale, boolean useDefault) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getDescription(languageId, useDefault);
	}

	@Override
	public String getDescription(String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		return getDescription(locale);
	}

	@Override
	public String getDescription(String languageId, boolean useDefault) {
		String description =
			JournalArticleLocalServiceUtil.getArticleDescription(
				getCompanyId(), getId(), languageId);

		if (description != null) {
			return description;
		}
		else if (useDefault) {
			return getDescription();
		}

		return StringPool.BLANK;
	}

	@JSON
	@Override
	public String getDescriptionCurrentValue() {
		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		return getDescription(locale, true);
	}

	@Override
	public Map<Locale, String> getDescriptionMap() {
		if (_descriptionMap != null) {
			return _descriptionMap;
		}

		_descriptionMap =
			JournalArticleLocalServiceUtil.getArticleDescriptionMap(
				getCompanyId(), getId());

		return _descriptionMap;
	}

	@JSON
	@Override
	public String getDescriptionMapAsXML() {
		return LocalizationUtil.updateLocalization(
			getDescriptionMap(), StringPool.BLANK, "Description",
			getDefaultLanguageId());
	}

	@JSON
	@Override
	public Date getDisplayDate() {
		if (!PropsValues.SCHEDULER_ENABLED &&
			!ExportImportThreadLocal.isExportInProcess() &&
			!ExportImportThreadLocal.isImportInProcess()) {

			return null;
		}

		return super.getDisplayDate();
	}

	@Override
	public Document getDocument() {
		if (_document == null) {
			DDMStructure ddmStructure = getDDMStructure();

			if (ddmStructure == null) {
				return null;
			}

			_document = _getDocument(
				ddmStructure,
				DDMFieldLocalServiceUtil.getDDMFormValues(
					ddmStructure.getDDMForm(), getId()));
		}

		return _document;
	}

	@Override
	public Document getDocumentByLocale(String languageId) {
		if (_documentMap == null) {
			_documentMap = new HashMap<>();
		}

		if (!_documentMap.containsKey(languageId)) {
			DDMStructure ddmStructure = getDDMStructure();

			if (ddmStructure != null) {
				_documentMap.put(
					languageId,
					_getDocument(
						ddmStructure,
						DDMFieldLocalServiceUtil.getDDMFormValues(
							ddmStructure.getDDMForm(), getId(), languageId)));
			}
		}

		return _documentMap.get(languageId);
	}

	@JSON
	@Override
	public Date getExpirationDate() {
		if (!PropsValues.SCHEDULER_ENABLED &&
			!ExportImportThreadLocal.isExportInProcess() &&
			!ExportImportThreadLocal.isImportInProcess()) {

			return null;
		}

		return super.getExpirationDate();
	}

	@Override
	public JournalFolder getFolder() throws PortalException {
		if (getFolderId() <= 0) {
			JournalFolder journalFolder = new JournalFolderImpl();

			journalFolder.setCompanyId(getCompanyId());

			return journalFolder;
		}

		return JournalFolderLocalServiceUtil.getFolder(getFolderId());
	}

	@Override
	public Map<Locale, String> getFriendlyURLMap() throws PortalException {
		Map<Locale, String> friendlyURLMap = new HashMap<>();

		long classNameId = PortalUtil.getClassNameId(JournalArticle.class);

		List<FriendlyURLEntry> friendlyURLEntries =
			FriendlyURLEntryLocalServiceUtil.getFriendlyURLEntries(
				getGroupId(), classNameId, getResourcePrimKey());

		if (friendlyURLEntries.isEmpty()) {
			friendlyURLMap.put(
				LocaleUtil.fromLanguageId(getDefaultLanguageId()),
				getUrlTitle());

			return friendlyURLMap;
		}

		FriendlyURLEntry friendlyURLEntry =
			FriendlyURLEntryLocalServiceUtil.getMainFriendlyURLEntry(
				classNameId, getResourcePrimKey());

		List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations =
			FriendlyURLEntryLocalServiceUtil.getFriendlyURLEntryLocalizations(
				friendlyURLEntry.getFriendlyURLEntryId());

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				friendlyURLEntryLocalizations) {

			Locale locale = LocaleUtil.fromLanguageId(
				friendlyURLEntryLocalization.getLanguageId());

			friendlyURLMap.put(
				locale, friendlyURLEntryLocalization.getUrlTitle());
		}

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			getDefaultLanguageId());

		if (Validator.isNull(friendlyURLMap.get(defaultLocale))) {
			Locale defaultSiteLocale = LocaleUtil.getSiteDefault();

			friendlyURLMap.put(
				defaultLocale, friendlyURLMap.get(defaultSiteLocale));
		}

		return friendlyURLMap;
	}

	@Override
	public String getFriendlyURLsXML() throws PortalException {
		return LocalizationUtil.updateLocalization(
			getFriendlyURLMap(), StringPool.BLANK, "FriendlyURL",
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));
	}

	@Override
	public List<FileEntry> getImagesFileEntries() throws PortalException {
		return getImagesFileEntries(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<FileEntry> getImagesFileEntries(int start, int end)
		throws PortalException {

		return getImagesFileEntries(start, end, null);
	}

	@Override
	public List<FileEntry> getImagesFileEntries(
			int start, int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException {

		long imagesFolderId = getImagesFolderId();

		if (imagesFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return new ArrayList<>();
		}

		return PortletFileRepositoryUtil.getPortletFileEntries(
			getGroupId(), imagesFolderId, WorkflowConstants.STATUS_APPROVED,
			start, end, orderByComparator);
	}

	@Override
	public int getImagesFileEntriesCount() throws PortalException {
		long imagesFolderId = getImagesFolderId();

		if (imagesFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return 0;
		}

		return PortletFileRepositoryUtil.getPortletFileEntriesCount(
			getGroupId(), imagesFolderId, WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public long getImagesFolderId() {
		if (_imagesFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return _imagesFolderId;
		}

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				getGroupId(), JournalConstants.SERVICE_NAME);

		if (repository == null) {
			return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
		}

		try {
			Folder folder = PortletFileRepositoryUtil.getPortletFolder(
				repository.getRepositoryId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				String.valueOf(getResourcePrimKey()));

			_imagesFolderId = folder.getFolderId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get folder for " + getResourcePrimKey(),
					exception);
			}
		}

		return _imagesFolderId;
	}

	@Override
	public Layout getLayout() {
		if (Validator.isNull(getLayoutUuid())) {
			return null;
		}

		// The layout and journal article must belong to the same group

		Layout layout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
			getLayoutUuid(), getGroupId(), false);

		if (layout == null) {
			layout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				getLayoutUuid(), getGroupId(), true);
		}

		return layout;
	}

	@JSON
	@Override
	public Date getReviewDate() {
		if (!PropsValues.SCHEDULER_ENABLED &&
			!ExportImportThreadLocal.isExportInProcess() &&
			!ExportImportThreadLocal.isImportInProcess()) {

			return null;
		}

		return super.getReviewDate();
	}

	@Override
	public String getSmallImageType() throws PortalException {
		if ((_smallImageType == null) && isSmallImage()) {
			Image smallImage = ImageLocalServiceUtil.getImage(
				getSmallImageId());

			_smallImageType = smallImage.getType();
		}

		return _smallImageType;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(JournalArticle.class);
	}

	@JSON
	@Override
	public String getTitle() {
		String title = JournalArticleLocalServiceUtil.getArticleTitle(
			getCompanyId(), getId(), getDefaultLanguageId());

		if (title == null) {
			return StringPool.BLANK;
		}

		return title;
	}

	@Override
	public String getTitle(Locale locale) {
		String title = JournalArticleLocalServiceUtil.getArticleTitle(
			getCompanyId(), getId(), locale);

		if (title == null) {
			return getTitle();
		}

		return title;
	}

	@Override
	public String getTitle(Locale locale, boolean useDefault) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getTitle(languageId, useDefault);
	}

	@Override
	public String getTitle(String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		return getTitle(locale);
	}

	@Override
	public String getTitle(String languageId, boolean useDefault) {
		String title = JournalArticleLocalServiceUtil.getArticleTitle(
			getCompanyId(), getId(), languageId);

		if (title != null) {
			return title;
		}
		else if (useDefault) {
			return getTitle();
		}

		return StringPool.BLANK;
	}

	@JSON
	@Override
	public String getTitleCurrentValue() {
		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		return getTitle(locale, true);
	}

	@Override
	public Map<Locale, String> getTitleMap() {
		if (_titleMap != null) {
			return _titleMap;
		}

		_titleMap = JournalArticleLocalServiceUtil.getArticleTitleMap(
			getCompanyId(), getId());

		return _titleMap;
	}

	@JSON
	@Override
	public String getTitleMapAsXML() {
		return LocalizationUtil.updateLocalization(
			getTitleMap(), StringPool.BLANK, "Title", getDefaultLanguageId());
	}

	@Override
	public long getTrashEntryClassPK() {
		return getResourcePrimKey();
	}

	@Override
	public String getUrlTitle(Locale locale) throws PortalException {
		String urlTitle = getFriendlyURLMap().get(locale);

		if (Validator.isNull(urlTitle)) {
			return getUrlTitle();
		}

		return urlTitle;
	}

	@Override
	public boolean hasApprovedVersion() {
		JournalArticle article =
			JournalArticleLocalServiceUtil.fetchLatestArticle(
				getGroupId(), getArticleId(),
				WorkflowConstants.STATUS_APPROVED);

		if (article == null) {
			return false;
		}

		return true;
	}

	@Override
	public void setDescriptionMap(Map<Locale, String> descriptionMap) {
		_descriptionMap = descriptionMap;
	}

	@Override
	public void setDocument(Document document) {
		_document = document;
	}

	@Override
	public void setImagesFolderId(long imagesFolderId) {
		_imagesFolderId = imagesFolderId;
	}

	@Override
	public void setSmallImageType(String smallImageType) {
		_smallImageType = smallImageType;
	}

	@Override
	public void setTitleMap(Map<Locale, String> titleMap) {
		_titleMap = titleMap;
	}

	private String _getContent(
		DDMStructure ddmStructure, DDMFormValues ddmFormValues) {

		if (ddmFormValues == null) {
			return null;
		}

		try {
			Fields fields = _ddmFormValuesToFieldsConverter.convert(
				ddmStructure, ddmFormValues);

			return _journalConverter.getContent(
				ddmStructure, fields, getGroupId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private Document _getDocument(
		DDMStructure ddmStructure, DDMFormValues ddmFormValues) {

		if (ddmFormValues == null) {
			return null;
		}

		try {
			Fields fields = _ddmFormValuesToFieldsConverter.convert(
				ddmStructure, ddmFormValues);

			return _journalConverter.getDocument(
				ddmStructure, fields, getGroupId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleImpl.class);

	private static volatile DDMFormValuesToFieldsConverter
		_ddmFormValuesToFieldsConverter;
	private static volatile JournalConverter _journalConverter;

	private DDMFormValues _ddmFormValues;
	private Map<Locale, String> _descriptionMap;
	private Document _document;
	private Map<String, Document> _documentMap;
	private long _imagesFolderId;
	private String _smallImageType;
	private Map<Locale, String> _titleMap;

}