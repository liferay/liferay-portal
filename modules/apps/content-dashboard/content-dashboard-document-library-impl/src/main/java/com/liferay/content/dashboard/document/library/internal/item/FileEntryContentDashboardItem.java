/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.VersionableContentDashboardItem;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemActionProviderRegistry;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionActionProviderRegistry;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemVersionActionException;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
public class FileEntryContentDashboardItem
	implements VersionableContentDashboardItem<FileEntry> {

	public FileEntryContentDashboardItem(
		List<AssetCategory> assetCategories, List<AssetTag> assetTags,
		ContentDashboardItemActionProviderRegistry
			contentDashboardItemActionProviderRegistry,
		ContentDashboardItemVersionActionProviderRegistry
			contentDashboardItemVersionActionProviderRegistry,
		ContentDashboardItemSubtype contentDashboardItemSubtype,
		DDMFieldLocalService ddmFieldLocalService,
		DLDisplayContextProvider dlDisplayContextProvider,
		DLFileEntryMetadataLocalService dlFileEntryMetadataLocalService,
		DLURLHelper dlURLHelper, FileEntry fileEntry, Group group,
		Language language, Portal portal) {

		if (ListUtil.isEmpty(assetCategories)) {
			_assetCategories = Collections.emptyList();
		}
		else {
			_assetCategories = Collections.unmodifiableList(assetCategories);
		}

		if (ListUtil.isEmpty(assetTags)) {
			_assetTags = Collections.emptyList();
		}
		else {
			_assetTags = Collections.unmodifiableList(assetTags);
		}

		_contentDashboardItemActionProviderRegistry =
			contentDashboardItemActionProviderRegistry;
		_contentDashboardItemVersionActionProviderRegistry =
			contentDashboardItemVersionActionProviderRegistry;
		_contentDashboardItemSubtype = contentDashboardItemSubtype;
		_ddmFieldLocalService = ddmFieldLocalService;
		_dlDisplayContextProvider = dlDisplayContextProvider;
		_dlFileEntryMetadataLocalService = dlFileEntryMetadataLocalService;
		_dlURLHelper = dlURLHelper;
		_fileEntry = fileEntry;
		_group = group;
		_language = language;
		_portal = portal;
	}

	@Override
	public List<ContentDashboardItemVersion> getAllContentDashboardItemVersions(
		HttpServletRequest httpServletRequest) {

		int status = WorkflowConstants.STATUS_APPROVED;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		User user = themeDisplay.getUser();

		if ((user.getUserId() == _fileEntry.getUserId()) ||
			permissionChecker.isContentReviewer(
				user.getCompanyId(), themeDisplay.getScopeGroupId())) {

			status = WorkflowConstants.STATUS_ANY;
		}

		return TransformUtil.transform(
			_fileEntry.getFileVersions(status),
			fileVersion -> new ContentDashboardItemVersion(
				fileVersion.getChangeLog(),
				_getContentDashboardItemVersionActions(
					fileVersion, httpServletRequest),
				fileVersion.getCreateDate(),
				_language.get(
					themeDisplay.getLocale(),
					WorkflowConstants.getStatusLabel(fileVersion.getStatus())),
				themeDisplay.getLocale(),
				WorkflowConstants.getStatusStyle(fileVersion.getStatus()),
				fileVersion.getUserName(),
				String.valueOf(fileVersion.getVersion())));
	}

	@Override
	public List<AssetCategory> getAssetCategories() {
		return _assetCategories;
	}

	@Override
	public List<AssetCategory> getAssetCategories(long assetVocabularyId) {
		return ListUtil.filter(
			_assetCategories,
			assetCategory ->
				assetCategory.getVocabularyId() == assetVocabularyId);
	}

	@Override
	public List<AssetTag> getAssetTags() {
		return _assetTags;
	}

	@Override
	public List<Locale> getAvailableLocales() {
		return Collections.emptyList();
	}

	@Override
	public List<ContentDashboardItemAction> getContentDashboardItemActions(
		HttpServletRequest httpServletRequest,
		ContentDashboardItemAction.Type... types) {

		return TransformUtil.transform(
			_contentDashboardItemActionProviderRegistry.
				getContentDashboardItemActionProviders(
					FileEntry.class.getName(), types),
			contentDashboardItemActionProvider -> {
				try {
					return contentDashboardItemActionProvider.
						getContentDashboardItemAction(
							_fileEntry, httpServletRequest);
				}
				catch (ContentDashboardItemActionException
							contentDashboardItemActionException) {

					_log.error(contentDashboardItemActionException);
				}

				return null;
			});
	}

	@Override
	public ContentDashboardItemSubtype getContentDashboardItemSubtype() {
		return _contentDashboardItemSubtype;
	}

	@Override
	public Date getCreateDate() {
		return _fileEntry.getCreateDate();
	}

	@Override
	public ContentDashboardItemAction getDefaultContentDashboardItemAction(
		HttpServletRequest httpServletRequest) {

		long userId = _portal.getUserId(httpServletRequest);

		Locale locale = _portal.getLocale(httpServletRequest);

		ContentDashboardItemVersion contentDashboardItemVersion =
			_getLastContentDashboardItemVersion(locale);

		if ((contentDashboardItemVersion != null) && (getUserId() == userId) &&
			Objects.equals(
				contentDashboardItemVersion.getLabel(),
				_language.get(
					locale,
					WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_DRAFT)))) {

			ContentDashboardItemActionProvider
				contentDashboardItemActionProvider =
					_contentDashboardItemActionProviderRegistry.
						getContentDashboardItemActionProvider(
							FileEntry.class.getName(),
							ContentDashboardItemAction.Type.EDIT);

			if (contentDashboardItemActionProvider == null) {
				return null;
			}

			ContentDashboardItemAction contentDashboardItemAction =
				_toContentDashboardItemAction(
					contentDashboardItemActionProvider, httpServletRequest);

			if (contentDashboardItemAction == null) {
				return null;
			}

			return contentDashboardItemAction;
		}

		ContentDashboardItemActionProvider
			viewContentDashboardItemActionProvider =
				_contentDashboardItemActionProviderRegistry.
					getContentDashboardItemActionProvider(
						FileEntry.class.getName(),
						ContentDashboardItemAction.Type.VIEW);

		if (viewContentDashboardItemActionProvider == null) {
			return _getContentDashboardItemAction(httpServletRequest);
		}

		ContentDashboardItemAction contentDashboardItemAction =
			_toContentDashboardItemAction(
				viewContentDashboardItemActionProvider, httpServletRequest);

		if (contentDashboardItemAction == null) {
			return _getContentDashboardItemAction(httpServletRequest);
		}

		return contentDashboardItemAction;
	}

	@Override
	public Locale getDefaultLocale() {
		try {
			return _portal.getSiteDefaultLocale(_fileEntry.getGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return LocaleUtil.getDefault();
		}
	}

	@Override
	public String getDescription(Locale locale) {
		return _fileEntry.getDescription();
	}

	@Override
	public long getId() {
		return _fileEntry.getFileEntryId();
	}

	@Override
	public InfoItemReference getInfoItemReference() {
		return new InfoItemReference(
			FileEntry.class.getName(), _fileEntry.getFileEntryId());
	}

	@Override
	public List<ContentDashboardItemVersion>
		getLatestContentDashboardItemVersions(Locale locale) {

		try {
			FileVersion latestFileVersion = _fileEntry.getLatestFileVersion();
			FileVersion latestTrustedFileVersion =
				_getLatestApprovedFileVersion(_fileEntry);

			List<FileVersion> fileVersions = new ArrayList<>();

			fileVersions.add(latestTrustedFileVersion);

			if (!latestFileVersion.equals(latestTrustedFileVersion)) {
				fileVersions.add(latestFileVersion);
			}

			List<ContentDashboardItemVersion> contentDashboardItemVersions =
				TransformUtil.transform(
					fileVersions,
					fileVersion -> _toVersion(fileVersion, locale));

			contentDashboardItemVersions.sort(
				Comparator.comparing(ContentDashboardItemVersion::getVersion));

			return contentDashboardItemVersions;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return Collections.emptyList();
		}
	}

	@Override
	public Date getModifiedDate() {
		return _fileEntry.getModifiedDate();
	}

	@Override
	public Date getReviewDate() {
		return _fileEntry.getReviewDate();
	}

	@Override
	public String getScopeName(Locale locale) {
		if (_group == null) {
			return StringPool.BLANK;
		}

		String scopeName = null;

		try {
			scopeName = _group.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if (scopeName == null) {
			scopeName = _group.getName(locale);
		}

		return scopeName;
	}

	@Override
	public List<SpecificInformation<?>> getSpecificInformationList(
		Locale locale) {

		List<DLFileEntryMetadata> dlFileEntryMetadatas =
			_getDLFileEntryMetadatas();

		long tiffImageLength = _getDDMFormFieldsValueValue(
			dlFileEntryMetadatas, "TIFF_IMAGE_LENGTH");

		long tiffImageWidth = _getDDMFormFieldsValueValue(
			dlFileEntryMetadatas, "TIFF_IMAGE_WIDTH");

		return Arrays.asList(
			new SpecificInformation<>(
				"size", SpecificInformation.Type.STRING, _getSize(locale)),
			new SpecificInformation<>(
				"resolution", SpecificInformation.Type.STRING,
				_getResolution(tiffImageLength, tiffImageWidth)),
			new SpecificInformation<>(
				"content-dashboard-aspect-ratio",
				SpecificInformation.Type.STRING,
				_getAspectRatio(tiffImageLength, tiffImageWidth, locale)),
			new SpecificInformation<>(
				"extension", SpecificInformation.Type.STRING, _getExtension()),
			new SpecificInformation<>(
				"file-name", SpecificInformation.Type.STRING, _getFileName()),
			new SpecificInformation<>(
				"latest-version-url", SpecificInformation.Type.URL,
				_getLatestVersionURL()),
			new SpecificInformation<>(
				"webdav-help", "web-dav-url", SpecificInformation.Type.URL,
				_getWebDAVURL()));
	}

	@Override
	public String getTitle(Locale locale) {
		return _fileEntry.getTitle();
	}

	@Override
	public String getTypeLabel(Locale locale) {
		InfoItemClassDetails infoItemClassDetails = new InfoItemClassDetails(
			FileEntry.class.getName());

		return infoItemClassDetails.getLabel(locale);
	}

	@Override
	public long getUserId() {
		return _fileEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _fileEntry.getUserName();
	}

	@Override
	public String getViewVersionsURL(HttpServletRequest httpServletRequest) {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest);

		return PortletURLBuilder.create(
			requestBackedPortletURLFactory.createControlPanelRenderURL(
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, _group, 0, 0)
		).setMVCRenderCommandName(
			"/document_library/view_file_entry_history"
		).setBackURL(
			() -> {
				LiferayPortletResponse liferayPortletResponse =
					_portal.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_RESPONSE));

				return liferayPortletResponse.createRenderURL();
			}
		).setParameter(
			"fileEntryId", _fileEntry.getFileEntryId()
		).buildString();
	}

	@Override
	public boolean isShowContentDashboardItemVersions(
		HttpServletRequest httpServletRequest) {

		DLEditFileEntryDisplayContext dlEditFileEntryDisplayContext =
			_dlDisplayContextProvider.getDLEditFileEntryDisplayContext(
				httpServletRequest, null, _fileEntry);

		try {
			if (!dlEditFileEntryDisplayContext.isVersionInfoVisible()) {
				return false;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}

		return true;
	}

	@Override
	public boolean isViewable(HttpServletRequest httpServletRequest) {
		if (ListUtil.isEmpty(
				_fileEntry.getFileVersions(
					WorkflowConstants.STATUS_APPROVED))) {

			return false;
		}

		ContentDashboardItemActionProvider contentDashboardItemActionProvider =
			_contentDashboardItemActionProviderRegistry.
				getContentDashboardItemActionProvider(
					FileEntry.class.getName(),
					ContentDashboardItemAction.Type.VIEW);

		if (contentDashboardItemActionProvider == null) {
			return false;
		}

		return contentDashboardItemActionProvider.isShow(
			_fileEntry, httpServletRequest);
	}

	private String _getAspectRatio(
		long imageLength, long imageWidth, Locale locale) {

		if ((imageLength <= 0) && (imageWidth <= 0)) {
			return null;
		}

		if (imageLength > imageWidth) {
			return _language.get(locale, "tall");
		}
		else if (imageLength < imageWidth) {
			return _language.get(locale, "wide");
		}

		return _language.get(locale, "square");
	}

	private ContentDashboardItemAction _getContentDashboardItemAction(
		HttpServletRequest httpServletRequest) {

		ContentDashboardItemActionProvider
			editContentDashboardItemActionProvider =
				_contentDashboardItemActionProviderRegistry.
					getContentDashboardItemActionProvider(
						FileEntry.class.getName(),
						ContentDashboardItemAction.Type.EDIT);

		if (editContentDashboardItemActionProvider == null) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			_toContentDashboardItemAction(
				editContentDashboardItemActionProvider, httpServletRequest);

		if (contentDashboardItemAction == null) {
			return null;
		}

		return contentDashboardItemAction;
	}

	private List<ContentDashboardItemVersionAction>
		_getContentDashboardItemVersionActions(
			FileVersion fileVersion, HttpServletRequest httpServletRequest) {

		return TransformUtil.transform(
			_contentDashboardItemVersionActionProviderRegistry.
				getContentDashboardItemVersionActionProviders(
					FileVersion.class.getName()),
			contentDashboardItemVersionActionProvider -> {
				if (!contentDashboardItemVersionActionProvider.isShow(
						fileVersion, httpServletRequest)) {

					return null;
				}

				try {
					ContentDashboardItemVersionAction
						contentDashboardItemVersionAction =
							contentDashboardItemVersionActionProvider.
								getContentDashboardItemVersionAction(
									fileVersion, httpServletRequest);

					if (contentDashboardItemVersionAction != null) {
						return contentDashboardItemVersionAction;
					}
				}
				catch (ContentDashboardItemVersionActionException
							contentDashboardItemVersionActionException) {

					_log.error(contentDashboardItemVersionActionException);
				}

				return null;
			});
	}

	private long _getDDMFormFieldsValueValue(
		List<DLFileEntryMetadata> dlFileEntryMetadatas, String fieldName) {

		if (ListUtil.isEmpty(dlFileEntryMetadatas)) {
			return 0;
		}

		for (DLFileEntryMetadata dlFileEntryMetadata : dlFileEntryMetadatas) {
			Long ddmFormFieldsValueValue = _getDDMFormFieldsValueValue(
				dlFileEntryMetadata.getDDMStorageId(), fieldName);

			if (ddmFormFieldsValueValue != null) {
				return ddmFormFieldsValueValue;
			}
		}

		return 0;
	}

	private Long _getDDMFormFieldsValueValue(
		long ddmStorageId, String fieldName) {

		List<DDMField> ddmFields = _ddmFieldLocalService.getDDMFields(
			ddmStorageId, fieldName);

		if (ListUtil.isEmpty(ddmFields)) {
			return null;
		}

		DDMField ddmField = ddmFields.get(0);

		DDMFieldAttribute ddmFieldAttribute =
			_ddmFieldLocalService.fetchDDMFieldAttribute(
				ddmField.getFieldId(), StringPool.BLANK, StringPool.BLANK);

		if ((ddmFieldAttribute == null) ||
			(ddmFieldAttribute.getAttributeValue() == null) ||
			!Validator.isNumber(ddmFieldAttribute.getAttributeValue())) {

			return null;
		}

		return Long.valueOf(ddmFieldAttribute.getAttributeValue());
	}

	private List<DLFileEntryMetadata> _getDLFileEntryMetadatas() {
		FileVersion fileVersion = null;

		try {
			fileVersion = _fileEntry.getFileVersion();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}

		if (fileVersion == null) {
			return null;
		}

		return _dlFileEntryMetadataLocalService.
			getFileVersionFileEntryMetadatas(fileVersion.getFileVersionId());
	}

	private String _getExtension() {
		return FileUtil.getExtension(_getFileName());
	}

	private String _getFileName() {
		return _fileEntry.getFileName();
	}

	private ContentDashboardItemVersion _getLastContentDashboardItemVersion(
		Locale locale) {

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			getLatestContentDashboardItemVersions(locale);

		if (ListUtil.isEmpty(contentDashboardItemVersions)) {
			return null;
		}

		return contentDashboardItemVersions.get(
			contentDashboardItemVersions.size() - 1);
	}

	private FileVersion _getLatestApprovedFileVersion(FileEntry fileEntry) {
		List<FileVersion> approvedFileVersions = fileEntry.getFileVersions(
			WorkflowConstants.STATUS_APPROVED, 0, 1);

		if (ListUtil.isEmpty(approvedFileVersions)) {
			return null;
		}

		return approvedFileVersions.get(0);
	}

	private URL _getLatestVersionURL() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		LiferayPortletRequest liferayPortletRequest =
			serviceContext.getLiferayPortletRequest();

		if (liferayPortletRequest == null) {
			return null;
		}

		List<ContentDashboardItemAction> contentDashboardItemActions =
			getContentDashboardItemActions(
				_portal.getHttpServletRequest(liferayPortletRequest),
				ContentDashboardItemAction.Type.PREVIEW);

		if (contentDashboardItemActions.isEmpty()) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		try {
			return new URL(contentDashboardItemAction.getURL());
		}
		catch (MalformedURLException malformedURLException) {
			_log.error(malformedURLException);
		}

		return null;
	}

	private String _getResolution(long imageLength, long imageWidth) {
		if ((imageLength <= 0) && (imageWidth <= 0)) {
			return null;
		}

		StringBundler sb = new StringBundler(3);

		sb.append(imageWidth);
		sb.append("x");
		sb.append(imageLength);

		return sb.toString();
	}

	private String _getSize(Locale locale) {
		return LanguageUtil.formatStorageSize(_fileEntry.getSize(), locale);
	}

	private URL _getWebDAVURL() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		LiferayPortletRequest liferayPortletRequest =
			serviceContext.getLiferayPortletRequest();

		if (liferayPortletRequest == null) {
			return null;
		}

		try {
			return new URL(
				_dlURLHelper.getWebDavURL(
					(ThemeDisplay)liferayPortletRequest.getAttribute(
						WebKeys.THEME_DISPLAY),
					_fileEntry.getFolder(), _fileEntry));
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private ContentDashboardItemAction _toContentDashboardItemAction(
		ContentDashboardItemActionProvider contentDashboardItemActionProvider,
		HttpServletRequest httpServletRequest) {

		try {
			return contentDashboardItemActionProvider.
				getContentDashboardItemAction(_fileEntry, httpServletRequest);
		}
		catch (ContentDashboardItemActionException
					contentDashboardItemActionException) {

			_log.error(contentDashboardItemActionException);

			return null;
		}
	}

	private ContentDashboardItemVersion _toVersion(
		FileVersion fileVersion, Locale locale) {

		if (fileVersion == null) {
			return null;
		}

		return new ContentDashboardItemVersion(
			fileVersion.getChangeLog(), null, fileVersion.getCreateDate(),
			_language.get(
				locale,
				WorkflowConstants.getStatusLabel(fileVersion.getStatus())),
			null, WorkflowConstants.getStatusStyle(fileVersion.getStatus()),
			fileVersion.getUserName(), fileVersion.getVersion());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileEntryContentDashboardItem.class);

	private final List<AssetCategory> _assetCategories;
	private final List<AssetTag> _assetTags;
	private final ContentDashboardItemActionProviderRegistry
		_contentDashboardItemActionProviderRegistry;
	private final ContentDashboardItemSubtype _contentDashboardItemSubtype;
	private final ContentDashboardItemVersionActionProviderRegistry
		_contentDashboardItemVersionActionProviderRegistry;
	private final DDMFieldLocalService _ddmFieldLocalService;
	private final DLDisplayContextProvider _dlDisplayContextProvider;
	private final DLFileEntryMetadataLocalService
		_dlFileEntryMetadataLocalService;
	private final DLURLHelper _dlURLHelper;
	private final FileEntry _fileEntry;
	private final Group _group;
	private final Language _language;
	private final Portal _portal;

}