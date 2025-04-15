/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webserver;

import com.liferay.document.library.kernel.document.conversion.DocumentConversionUtil;
import com.liferay.document.library.kernel.exception.FileEntryExpiredException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.AudioProcessorUtil;
import com.liferay.document.library.kernel.processor.ImageProcessorUtil;
import com.liferay.document.library.kernel.processor.PDFProcessor;
import com.liferay.document.library.kernel.processor.PDFProcessorUtil;
import com.liferay.document.library.kernel.processor.VideoProcessor;
import com.liferay.document.library.kernel.processor.VideoProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.image.ImageToolUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.login.AuthLoginGroupSettingsUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.ImageConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationTable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.util.FileEntryHttpHeaderCustomizerUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.service.ImageServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.InactiveRequestHandler;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.URLTemplateResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.Validator_IW;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.ImageImpl;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;
import com.liferay.users.admin.kernel.file.uploads.UserFileUploadsSettings;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.awt.image.RenderedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Alexander Chow
 * @author Brian Wing Shun Chan
 */
public class WebServerServlet extends HttpServlet {

	public static final String PATH_PORTLET_FILE_ENTRY = "portlet_file_entry";

	/**
	 * @see com.liferay.portal.servlet.filters.virtualhost.VirtualHostFilter
	 */
	public static boolean hasFiles(HttpServletRequest httpServletRequest) {
		String name = PrincipalThreadLocal.getName();
		String password = PrincipalThreadLocal.getPassword();

		try {

			// Do not use permission checking since this may be called from
			// other contexts that are also managing the principal

			User user = _getUser(httpServletRequest);

			if (!user.isGuestUser()) {
				PrincipalThreadLocal.setName(user.getUserId());
				PrincipalThreadLocal.setPassword(
					PortalUtil.getUserPassword(httpServletRequest));
			}

			String[] pathArray = StringUtil.split(
				_getPath(httpServletRequest), CharPool.SLASH);

			if (pathArray.length == 0) {
				return true;
			}
			else if (Validator.isNumber(pathArray[0])) {
				_checkFileEntry(pathArray);
			}
			else if (_PATH_SEPARATOR_FILE_ENTRY.equals(pathArray[0])) {
				FileEntry fileEntry = _resolveFileEntry(
					httpServletRequest, pathArray);

				if (fileEntry == null) {
					return false;
				}
			}
			else if (PATH_PORTLET_FILE_ENTRY.equals(pathArray[0])) {
				FileEntry fileEntry = getPortletFileEntry(
					httpServletRequest, pathArray);

				if (fileEntry != null) {
					return true;
				}
			}
			else {
				Group group = _getGroup(user.getCompanyId(), pathArray[0]);

				_checkDirectoryIndexingEnabled(group);

				long groupId = group.getGroupId();

				long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

				for (int i = 1; i < pathArray.length; i++) {
					try {
						Folder folder = DLAppLocalServiceUtil.getFolder(
							groupId, folderId,
							URLCodec.decodeURL(pathArray[i]));

						folderId = folder.getFolderId();
					}
					catch (NoSuchFolderException noSuchFolderException) {

						// LPS-52675

						if (_log.isDebugEnabled()) {
							_log.debug(noSuchFolderException);
						}

						if (i != (pathArray.length - 1)) {
							return false;
						}

						pathArray = new String[] {
							String.valueOf(groupId), String.valueOf(folderId),
							pathArray[i]
						};

						_checkFileEntry(pathArray);
					}
				}
			}

			sendMessageObjectEntryAttachmentDownload(httpServletRequest, user);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
		finally {
			PrincipalThreadLocal.setName(name);
			PrincipalThreadLocal.setPassword(password);
		}

		return true;
	}

	/**
	 * @see com.liferay.portal.servlet.filters.virtualhost.VirtualHostFilter
	 */
	public static void sendMessageObjectEntryAttachmentDownload(
		HttpServletRequest httpServletRequest, User user) {

		String objectDefinitionExternalReferenceCode = ParamUtil.getString(
			httpServletRequest, "objectDefinitionExternalReferenceCode");

		if (Validator.isNull(objectDefinitionExternalReferenceCode)) {
			return;
		}

		try {
			MessageBus messageBus = _messageBusSnapshot.get();

			Message message = new Message();

			if (user == null) {
				user = _getUser(httpServletRequest);
			}

			message.put("companyId", user.getCompanyId());

			message.put(
				"objectDefinitionExternalReferenceCode",
				objectDefinitionExternalReferenceCode);
			message.put(
				"objectEntryExternalReferenceCode",
				ParamUtil.getString(
					httpServletRequest, "objectEntryExternalReferenceCode"));
			message.put("userId", user.getUserId());

			messageBus.sendMessage(
				DestinationNames.OBJECT_ENTRY_ATTACHMENT_DOWNLOAD, message);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		_lastModified = GetterUtil.getBoolean(
			servletConfig.getInitParameter("last_modified"), true);

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		String templateId =
			"com/liferay/portal/webserver/dependencies/template.ftl";

		URL url = classLoader.getResource(templateId);

		_templateResource = new URLTemplateResource(templateId, url);
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		User user = null;

		try {
			user = _getUser(httpServletRequest);

			if (_processCompanyInactiveRequest(
					httpServletRequest, httpServletResponse,
					user.getCompanyId())) {

				return;
			}

			PrincipalThreadLocal.setName(user.getUserId());
			PrincipalThreadLocal.setPassword(
				PortalUtil.getUserPassword(httpServletRequest));

			PermissionThreadLocal.getPermissionChecker(user, true);

			_checkResourcePermission(httpServletRequest, httpServletResponse);

			String ifNoneMatch = httpServletRequest.getHeader(
				HttpHeaders.IF_NONE_MATCH);

			if ((ifNoneMatch == null) && _lastModified) {
				long lastModified = getLastModified(httpServletRequest);

				if (lastModified > 0) {
					long ifModifiedSince = httpServletRequest.getDateHeader(
						HttpHeaders.IF_MODIFIED_SINCE);

					if ((ifModifiedSince > 0) &&
						(ifModifiedSince == lastModified)) {

						httpServletResponse.setStatus(
							HttpServletResponse.SC_NOT_MODIFIED);

						return;
					}

					httpServletResponse.setDateHeader(
						HttpHeaders.LAST_MODIFIED, lastModified);
				}
			}

			TransactionConfig.Builder builder = new TransactionConfig.Builder();

			builder.setReadOnly(true);
			builder.setRollbackForClasses(Exception.class);

			TransactionInvokerUtil.invoke(
				builder.build(),
				_createFileServingCallable(
					httpServletRequest, httpServletResponse, user));
		}
		catch (FileEntryExpiredException | NoSuchFileEntryException |
			   NoSuchFolderException exception) {

			PortalUtil.sendError(
				HttpServletResponse.SC_NOT_FOUND, exception, httpServletRequest,
				httpServletResponse);
		}
		catch (PrincipalException principalException) {
			processPrincipalException(
				principalException, user, httpServletRequest,
				httpServletResponse);
		}
		catch (Exception exception) {
			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);
		}
		catch (Throwable throwable) {
			PortalUtil.sendError(
				new Exception(throwable), httpServletRequest,
				httpServletResponse);
		}
	}

	protected static FileEntry getPortletFileEntry(
			HttpServletRequest httpServletRequest, String[] pathArray)
		throws Exception {

		long groupId = GetterUtil.getLong(pathArray[1]);
		String uuid = pathArray[3];

		FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
			uuid, groupId);

		User user = _getUser(httpServletRequest);

		if (!fileEntry.containsPermission(
				PermissionCheckerFactoryUtil.create(user), ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				user.getUserId(), ActionKeys.VIEW);
		}

		int status = ParamUtil.getInteger(
			httpServletRequest, "status", WorkflowConstants.STATUS_APPROVED);

		if ((status != WorkflowConstants.STATUS_IN_TRASH) &&
			fileEntry.isInTrash()) {

			return null;
		}

		return fileEntry;
	}

	protected Image convertFileEntry(boolean smallImage, FileEntry fileEntry)
		throws PortalException {

		try {
			Image image = new ImageImpl();

			image.setModifiedDate(fileEntry.getModifiedDate());

			InputStream inputStream = null;

			if (smallImage) {
				inputStream = ImageProcessorUtil.getThumbnailAsStream(
					fileEntry.getFileVersion(), 0);
			}
			else {
				inputStream = fileEntry.getContentStream();
			}

			image.setTextObj(FileUtil.getBytes(inputStream));

			image.setType(fileEntry.getExtension());

			return image;
		}
		catch (PortalException | SystemException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	protected Image getDefaultImage(
		HttpServletRequest httpServletRequest, long imageId) {

		String path = GetterUtil.getString(httpServletRequest.getPathInfo());

		if (path.startsWith("/company_logo") ||
			path.startsWith("/layout_set_logo") || path.startsWith("/logo")) {

			return ImageToolUtil.getDefaultCompanyLogo();
		}
		else if (path.startsWith("/organization_logo")) {
			return ImageToolUtil.getDefaultOrganizationLogo();
		}
		else if (path.startsWith("/user_female_portrait")) {
			return ImageToolUtil.getDefaultUserFemalePortrait();
		}
		else if (path.startsWith("/user_male_portrait")) {
			return ImageToolUtil.getDefaultUserMalePortrait();
		}
		else if (path.startsWith("/user_portrait")) {
			return ImageToolUtil.getDefaultUserPortrait();
		}

		return null;
	}

	protected Image getImage(
			HttpServletRequest httpServletRequest, boolean getDefault)
		throws Exception {

		Image image = null;

		long imageId = getImageId(httpServletRequest);

		if (imageId > 0) {
			image = ImageServiceUtil.getImage(imageId);

			String path = GetterUtil.getString(
				httpServletRequest.getPathInfo());

			if (path.startsWith("/layout_icon") || path.startsWith("/logo")) {
				Layout layout = LayoutLocalServiceUtil.fetchLayoutByIconImageId(
					true, imageId);

				if (layout != null) {
					PermissionChecker permissionChecker = _getPermissionChecker(
						httpServletRequest);

					if (!LayoutPermissionUtil.contains(
							permissionChecker, layout, ActionKeys.VIEW)) {

						throw new PrincipalException.MustHavePermission(
							permissionChecker, Layout.class.getName(),
							layout.getPlid(), ActionKeys.VIEW);
					}
				}
			}
			else if (path.startsWith("/layout_set_logo")) {
				LayoutSet layoutSet =
					LayoutSetLocalServiceUtil.fetchLayoutSetByLogoId(
						true, imageId);

				if (layoutSet != null) {
					PermissionChecker permissionChecker = _getPermissionChecker(
						httpServletRequest);

					Group group = layoutSet.getGroup();

					if (!group.isShowSite(
							permissionChecker, layoutSet.isPrivateLayout()) &&
						!GroupPermissionUtil.contains(
							permissionChecker, layoutSet.getGroupId(),
							ActionKeys.VIEW)) {

						throw new PrincipalException.MustHavePermission(
							permissionChecker, LayoutSet.class.getName(),
							layoutSet.getLayoutSetId(), ActionKeys.VIEW);
					}
				}
			}
			else if (path.startsWith("/user_female_portrait") ||
					 path.startsWith("/user_male_portrait") ||
					 path.startsWith("/user_portrait")) {

				image = getUserPortraitImageResized(image, imageId);
			}
		}
		else {
			String uuid = ParamUtil.getString(httpServletRequest, "uuid");
			long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

			if (Validator.isNotNull(uuid) && (groupId > 0)) {
				boolean igSmallImage = ParamUtil.getBoolean(
					httpServletRequest, "igSmallImage");

				try {
					FileEntry fileEntry =
						DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
							uuid, groupId);

					image = convertFileEntry(igSmallImage, fileEntry);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		if (getDefault && (image == null)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Get a default image for " + imageId);
			}

			image = getDefaultImage(httpServletRequest, imageId);
		}

		return image;
	}

	protected byte[] getImageBytes(
			HttpServletRequest httpServletRequest, Image image)
		throws PortalException {

		byte[] textObj = image.getTextObj();

		if (ArrayUtil.isEmpty(textObj)) {
			throw new NoSuchFileException();
		}

		try {
			if (!PropsValues.IMAGE_AUTO_SCALE) {
				return textObj;
			}

			ImageBag imageBag = null;

			if (image.getImageId() == 0) {
				imageBag = ImageToolUtil.read(textObj);

				RenderedImage renderedImage = imageBag.getRenderedImage();

				image.setHeight(renderedImage.getHeight());
				image.setWidth(renderedImage.getWidth());
			}

			int height = ParamUtil.getInteger(
				httpServletRequest, "height", image.getHeight());
			int width = ParamUtil.getInteger(
				httpServletRequest, "width", image.getWidth());

			if ((height >= image.getHeight()) && (width >= image.getWidth())) {
				return textObj;
			}

			if (image.getImageId() != 0) {
				imageBag = ImageToolUtil.read(textObj);
			}

			RenderedImage renderedImage = ImageToolUtil.scale(
				imageBag.getRenderedImage(), height, width);

			return ImageToolUtil.getBytes(renderedImage, imageBag.getType());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Error scaling image " + image.getImageId(), exception);
			}
		}

		return textObj;
	}

	protected long getImageId(HttpServletRequest httpServletRequest) {

		// The image id may be passed in as image_id, img_id, or i_id

		long imageId = ParamUtil.getLong(httpServletRequest, "image_id");

		if (imageId <= 0) {
			imageId = ParamUtil.getLong(httpServletRequest, "img_id");
		}

		if (imageId <= 0) {
			imageId = ParamUtil.getLong(httpServletRequest, "i_id");
		}

		User user = null;

		if (imageId <= 0) {
			long companyId = ParamUtil.getLong(httpServletRequest, "companyId");
			String screenName = ParamUtil.getString(
				httpServletRequest, "screenName");

			if ((companyId > 0) && Validator.isNotNull(screenName)) {
				user = UserLocalServiceUtil.fetchUserByScreenName(
					companyId, screenName);

				if (user != null) {
					imageId = user.getPortraitId();
				}
			}
		}

		UserFileUploadsSettings userFileUploadsSettings =
			_userFileUploadsSettingsSnapshot.get();

		if (userFileUploadsSettings.isImageCheckToken() && (imageId > 0)) {
			String imageIdToken = ParamUtil.getString(
				httpServletRequest, "img_id_token");

			if (user == null) {
				user = UserLocalServiceUtil.fetchUserByPortraitId(imageId);
			}

			if ((user != null) &&
				!imageIdToken.equals(DigesterUtil.digest(user.getUserUuid()))) {

				return 0;
			}

			Organization organization = null;

			List<Organization> organizations =
				OrganizationLocalServiceUtil.dslQuery(
					DSLQueryFactoryUtil.select(
						OrganizationTable.INSTANCE
					).from(
						OrganizationTable.INSTANCE
					).where(
						OrganizationTable.INSTANCE.logoId.eq(imageId)
					).limit(
						0, 1
					));

			if (ListUtil.isNotEmpty(organizations)) {
				organization = organizations.get(0);
			}

			if ((organization != null) &&
				!imageIdToken.equals(
					DigesterUtil.digest(organization.getUuid()))) {

				return 0;
			}
		}

		return imageId;
	}

	@Override
	protected long getLastModified(HttpServletRequest httpServletRequest) {
		try {
			Date modifiedDate = null;

			Image image = getImage(httpServletRequest, true);

			if (image != null) {
				modifiedDate = image.getModifiedDate();
			}
			else {
				String[] pathArray = StringUtil.split(
					_getPath(httpServletRequest), CharPool.SLASH);

				if ((pathArray.length == 0) ||
					pathArray[0].equals("language")) {

					return -1;
				}

				FileEntry fileEntry = null;

				try {
					fileEntry = _getFileEntry(pathArray, httpServletRequest);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				if (fileEntry == null) {
					return -1;
				}

				String version = ParamUtil.getString(
					httpServletRequest, "version");

				if (Validator.isNotNull(version)) {
					FileVersion fileVersion = fileEntry.getFileVersion(version);

					modifiedDate = fileVersion.getModifiedDate();
				}
				else {
					modifiedDate = fileEntry.getModifiedDate();
				}
			}

			if (modifiedDate == null) {
				modifiedDate = PortalUtil.getUptime();
			}

			// Round down and remove milliseconds

			return (modifiedDate.getTime() / 1000) * 1000;
		}
		catch (PrincipalException principalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(principalException);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return -1;
	}

	protected Image getUserPortraitImageResized(Image image, long imageId)
		throws PortalException {

		if (image == null) {
			return null;
		}

		UserFileUploadsSettings userFileUploadsSettings =
			_userFileUploadsSettingsSnapshot.get();

		int usersImageMaxHeight = userFileUploadsSettings.getImageMaxHeight();
		int usersImageMaxWidth = userFileUploadsSettings.getImageMaxWidth();

		if (((usersImageMaxHeight > 0) &&
			 (image.getHeight() > usersImageMaxHeight)) ||
			((usersImageMaxWidth > 0) &&
			 (image.getWidth() > usersImageMaxWidth))) {

			User user = UserLocalServiceUtil.fetchUserByPortraitId(imageId);

			if (user == null) {
				throw new NoSuchUserException(
					"No user with portrait ID " + imageId);
			}

			UserLocalServiceUtil.updatePortrait(
				user.getUserId(), image.getTextObj());

			return ImageLocalServiceUtil.getImage(imageId);
		}

		return image;
	}

	protected boolean isLegacyImageGalleryImageId(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			long imageId = getImageId(httpServletRequest);

			if (imageId == 0) {
				return false;
			}

			Repository repository = RepositoryProviderUtil.getImageRepository(
				imageId);

			if (!repository.isCapabilityProvided(ThumbnailCapability.class)) {
				return false;
			}

			ThumbnailCapability thumbnailCapability = repository.getCapability(
				ThumbnailCapability.class);

			FileEntry fileEntry = thumbnailCapability.fetchImageFileEntry(
				imageId);

			if (fileEntry == null) {
				return false;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			String queryString = StringPool.BLANK;

			if (imageId == thumbnailCapability.getSmallImageId(fileEntry)) {
				queryString = "&imageThumbnail=1";
			}
			else if (imageId == thumbnailCapability.getCustom1ImageId(
						fileEntry)) {

				queryString = "&imageThumbnail=2";
			}
			else if (imageId == thumbnailCapability.getCustom2ImageId(
						fileEntry)) {

				queryString = "&imageThumbnail=3";
			}

			FileVersion fileVersion = fileEntry.getFileVersion();

			if (PropsValues.DL_FILE_ENTRY_IG_THUMBNAIL_GENERATION &&
				Validator.isNotNull(queryString)) {

				ImageProcessorUtil.hasImages(fileVersion);
			}

			String url = DLUtil.getPreviewURL(
				fileEntry, fileVersion, themeDisplay, queryString);

			httpServletResponse.setHeader(HttpHeaders.LOCATION, url);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_MOVED_PERMANENTLY);

			return true;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	protected boolean isSupportsRangeHeader(String contentType) {
		return _acceptRangesMimeTypes.contains(contentType);
	}

	protected void processPrincipalException(
			Throwable throwable, User user,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		if (!user.isGuestUser()) {
			PortalUtil.sendError(
				HttpServletResponse.SC_UNAUTHORIZED, (Exception)throwable,
				httpServletRequest, httpServletResponse);

			return;
		}

		String redirect = PortalUtil.getPathMain() + "/portal/login";

		redirect = HttpComponentsUtil.addParameter(
			redirect, "redirect", PortalUtil.getCurrentURL(httpServletRequest));

		httpServletResponse.sendRedirect(redirect);
	}

	protected void sendDocumentLibrary(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, User user, String path,
			String[] pathArray)
		throws Exception {

		Group group = _getGroup(user.getCompanyId(), pathArray[0]);

		_checkDirectoryIndexingEnabled(group);

		long groupId = group.getGroupId();

		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		for (int i = 1; i < pathArray.length; i++) {
			String name = pathArray[i];

			try {
				Folder folder = DLAppLocalServiceUtil.getFolder(
					groupId, folderId, URLCodec.decodeURL(name));

				folderId = folder.getFolderId();
			}
			catch (NoSuchFolderException noSuchFolderException) {
				if (i != (pathArray.length - 1)) {
					throw noSuchFolderException;
				}

				String title = name;

				sendFile(
					httpServletResponse, user, groupId, folderId,
					URLCodec.decodeURL(title));

				return;
			}
		}

		try {
			sendFile(
				httpServletResponse, user, groupId, folderId, "index.html");

			return;
		}
		catch (NoSuchFileEntryException | PrincipalException exception1) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception1);
			}

			try {
				sendFile(
					httpServletResponse, user, groupId, folderId, "index.htm");

				return;
			}
			catch (NoSuchFileEntryException | PrincipalException exception2) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(exception2);
				}
			}
		}

		List<WebServerEntry> webServerEntries = new ArrayList<>();

		webServerEntries.add(new WebServerEntry(path, "../"));

		List<Folder> folders = DLAppServiceUtil.getFolders(groupId, folderId);

		for (Folder folder : folders) {
			WebServerEntry webServerEntry = new WebServerEntry(
				path, folder.getName() + StringPool.SLASH,
				folder.getCreateDate(), folder.getModifiedDate(),
				folder.getDescription(), 0);

			webServerEntries.add(webServerEntry);
		}

		List<FileEntry> fileEntries = DLAppServiceUtil.getFileEntries(
			groupId, folderId);

		for (FileEntry fileEntry : fileEntries) {
			WebServerEntry webServerEntry = new WebServerEntry(
				path, fileEntry.getTitle(), fileEntry.getCreateDate(),
				fileEntry.getModifiedDate(), fileEntry.getDescription(),
				fileEntry.getSize());

			webServerEntries.add(webServerEntry);
		}

		sendHTML(
			httpServletResponse, URLCodec.decodeURL(path), webServerEntries);
	}

	protected void sendFile(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, User user,
			String[] pathArray)
		throws Exception {

		// Retrieve file details

		FileEntry fileEntry = _getFileEntry(pathArray, httpServletRequest);

		if (_processCompanyInactiveRequest(
				httpServletRequest, httpServletResponse,
				fileEntry.getCompanyId())) {

			return;
		}

		String version = ParamUtil.getString(httpServletRequest, "version");

		if (Validator.isNull(version) &&
			Validator.isNotNull(fileEntry.getVersion())) {

			version = fileEntry.getVersion();
		}

		String tempFileId = DLUtil.getTempFileId(
			fileEntry.getFileEntryId(), version);

		FileVersion fileVersion = fileEntry.getFileVersion(version);

		if ((ParamUtil.getInteger(httpServletRequest, "height") > 0) ||
			(ParamUtil.getInteger(httpServletRequest, "width") > 0)) {

			InputStream inputStream = fileVersion.getContentStream(true);

			writeImage(
				ImageToolUtil.getImage(inputStream), httpServletRequest,
				httpServletResponse);

			return;
		}

		String fileName = fileVersion.getFileName();

		// Handle requested conversion

		boolean converted = false;

		int imageThumbnail = ParamUtil.getInteger(
			httpServletRequest, "imageThumbnail");
		int documentThumbnail = ParamUtil.getInteger(
			httpServletRequest, "documentThumbnail");
		int previewFileIndex = ParamUtil.getInteger(
			httpServletRequest, "previewFileIndex");
		boolean audioPreview = ParamUtil.getBoolean(
			httpServletRequest, "audioPreview");
		boolean imagePreview = ParamUtil.getBoolean(
			httpServletRequest, "imagePreview");
		boolean videoPreview = ParamUtil.getBoolean(
			httpServletRequest, "videoPreview");
		int videoThumbnail = ParamUtil.getInteger(
			httpServletRequest, "videoThumbnail");

		InputStream inputStream = null;
		long contentLength = 0;

		if ((imageThumbnail > 0) && (imageThumbnail <= 3)) {
			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD,
				ImageProcessorUtil.getThumbnailType(fileVersion));

			int thumbnailIndex = imageThumbnail - 1;

			inputStream = ImageProcessorUtil.getThumbnailAsStream(
				fileVersion, thumbnailIndex);
			contentLength = ImageProcessorUtil.getThumbnailFileSize(
				fileVersion, thumbnailIndex);

			converted = true;
		}
		else if ((documentThumbnail > 0) && (documentThumbnail <= 3)) {
			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD,
				PDFProcessor.THUMBNAIL_TYPE);

			int thumbnailIndex = documentThumbnail - 1;

			inputStream = PDFProcessorUtil.getThumbnailAsStream(
				fileVersion, thumbnailIndex);
			contentLength = PDFProcessorUtil.getThumbnailFileSize(
				fileVersion, thumbnailIndex);

			converted = true;
		}
		else if (previewFileIndex > 0) {
			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD,
				PDFProcessor.PREVIEW_TYPE);
			inputStream = PDFProcessorUtil.getPreviewAsStream(
				fileVersion, previewFileIndex);
			contentLength = PDFProcessorUtil.getPreviewFileSize(
				fileVersion, previewFileIndex);

			converted = true;
		}
		else if (audioPreview || videoPreview) {
			String type = ParamUtil.getString(httpServletRequest, "type");

			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD, type);

			if (audioPreview) {
				inputStream = AudioProcessorUtil.getPreviewAsStream(
					fileVersion, type);
				contentLength = AudioProcessorUtil.getPreviewFileSize(
					fileVersion, type);
			}
			else {
				inputStream = VideoProcessorUtil.getPreviewAsStream(
					fileVersion, type);
				contentLength = VideoProcessorUtil.getPreviewFileSize(
					fileVersion, type);
			}

			converted = true;
		}
		else if (imagePreview) {
			String type = ImageProcessorUtil.getPreviewType(fileVersion);

			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD, type);

			inputStream = ImageProcessorUtil.getPreviewAsStream(fileVersion);

			contentLength = ImageProcessorUtil.getPreviewFileSize(fileVersion);

			converted = true;
		}
		else if ((videoThumbnail > 0) && (videoThumbnail <= 3)) {
			fileName = StringBundler.concat(
				FileUtil.stripExtension(fileName), StringPool.PERIOD,
				VideoProcessor.THUMBNAIL_TYPE);

			int thumbnailIndex = videoThumbnail - 1;

			inputStream = VideoProcessorUtil.getThumbnailAsStream(
				fileVersion, thumbnailIndex);
			contentLength = VideoProcessorUtil.getThumbnailFileSize(
				fileVersion, thumbnailIndex);

			converted = true;
		}
		else {
			inputStream = fileVersion.getContentStream(true);
			contentLength = fileVersion.getSize();

			String targetExtension = ParamUtil.getString(
				httpServletRequest, "targetExtension");

			if (Validator.isNotNull(targetExtension)) {
				File convertedFile = DocumentConversionUtil.convert(
					tempFileId, inputStream, fileVersion.getExtension(),
					targetExtension);

				if (convertedFile != null) {
					fileName = StringBundler.concat(
						FileUtil.stripExtension(fileName), StringPool.PERIOD,
						targetExtension);
					inputStream = new FileInputStream(convertedFile);
					contentLength = convertedFile.length();

					converted = true;
				}
			}
		}

		if (converted && (contentLength == 0)) {
			PortalUtil.sendError(
				HttpServletResponse.SC_NOT_FOUND,
				new NoSuchFileException("The converted file is empty"),
				httpServletRequest, httpServletResponse);

			return;
		}

		// Determine proper content type

		String contentType = null;

		if (converted) {
			contentType = MimeTypesUtil.getContentType(fileName);
		}
		else {
			contentType = fileVersion.getMimeType();
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Content type set to " + contentType);
		}

		// Send file

		String cacheControlValue = HttpHeaders.CACHE_CONTROL_PRIVATE_VALUE;

		boolean download = ParamUtil.getBoolean(httpServletRequest, "download");

		if (download) {
			cacheControlValue = HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE;
		}

		httpServletResponse.addHeader(
			HttpHeaders.CACHE_CONTROL,
			FileEntryHttpHeaderCustomizerUtil.getHttpHeaderValue(
				fileEntry, HttpHeaders.CACHE_CONTROL, cacheControlValue));

		if (isSupportsRangeHeader(contentType)) {
			ServletResponseUtil.sendFileWithRangeHeader(
				httpServletRequest, httpServletResponse, fileName, inputStream,
				contentLength, contentType);
		}
		else {
			if (download) {
				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, fileName,
					inputStream, contentLength, contentType,
					HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
			}
			else {
				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, fileName,
					inputStream, contentLength, contentType);
			}
		}
	}

	protected void sendFile(
			HttpServletResponse httpServletResponse, User user, long groupId,
			long folderId, String title)
		throws Exception {

		FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
			groupId, folderId, title);

		httpServletResponse.addHeader(
			HttpHeaders.CACHE_CONTROL,
			FileEntryHttpHeaderCustomizerUtil.getHttpHeaderValue(
				fileEntry, HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_PRIVATE_VALUE));

		ServletResponseUtil.sendFile(
			null, httpServletResponse, fileEntry.getTitle(),
			fileEntry.getContentStream(), fileEntry.getSize(),
			fileEntry.getMimeType());
	}

	protected void sendGroups(
			HttpServletResponse httpServletResponse, User user, String path)
		throws Exception {

		List<WebServerEntry> webServerEntries = new ArrayList<>();

		List<Group> groups = WebDAVUtil.getGroups(user);

		for (Group group : groups) {
			if (_isDirectoryIndexingEnabled(group)) {
				String name = HttpComponentsUtil.fixPath(
					group.getFriendlyURL());

				WebServerEntry webServerEntry = new WebServerEntry(
					path, name + StringPool.SLASH, null, null,
					group.getDescription(), 0);

				webServerEntries.add(webServerEntry);
			}
		}

		sendHTML(httpServletResponse, path, webServerEntries);
	}

	protected void sendHTML(
			HttpServletResponse httpServletResponse, String path,
			List<WebServerEntry> webServerEntries)
		throws Exception {

		Template template = TemplateManagerUtil.getTemplate(
			TemplateConstants.LANG_TYPE_FTL, _templateResource, true);

		template.put("entries", webServerEntries);
		template.put("path", HttpComponentsUtil.encodePath(path));

		if (_WEB_SERVER_SERVLET_VERSION_VERBOSITY_DEFAULT) {
		}
		else if (_WEB_SERVER_SERVLET_VERSION_VERBOSITY_PARTIAL) {
			template.put("releaseInfo", ReleaseInfo.getName());
		}
		else {
			template.put("releaseInfo", ReleaseInfo.getReleaseInfo());
		}

		template.put("validator", Validator_IW.getInstance());

		httpServletResponse.setContentType(ContentTypes.TEXT_HTML_UTF8);

		template.processTemplate(httpServletResponse.getWriter());
	}

	protected void sendPortletFileEntry(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path,
			String[] pathArray)
		throws Exception {

		FileEntry fileEntry = getPortletFileEntry(
			httpServletRequest, pathArray);

		if ((fileEntry == null) ||
			_processCompanyInactiveRequest(
				httpServletRequest, httpServletResponse,
				fileEntry.getCompanyId())) {

			return;
		}

		String fileName = HttpComponentsUtil.decodeURL(
			HtmlUtil.escape(pathArray[2]));

		if (Validator.isNull(fileName)) {
			throw new NoSuchFileEntryException("Invalid path " + path);
		}

		if (fileEntry.isInTrash()) {
			TrashHelper trashTitleResolver = _trashTitleResolverSnapshot.get();

			fileName = trashTitleResolver.getOriginalTitle(fileName);
		}

		httpServletResponse.addHeader(
			HttpHeaders.CACHE_CONTROL,
			FileEntryHttpHeaderCustomizerUtil.getHttpHeaderValue(
				fileEntry, HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_PRIVATE_VALUE));

		boolean download = ParamUtil.getBoolean(httpServletRequest, "download");

		if (download) {
			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, fileName,
				fileEntry.getContentStream(), fileEntry.getSize(),
				fileEntry.getMimeType(),
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		else {
			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse, fileName,
				fileEntry.getContentStream(), fileEntry.getSize(),
				fileEntry.getMimeType());
		}
	}

	protected void writeImage(
			Image image, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		if (image == null) {
			return;
		}

		String contentType = null;

		String type = image.getType();

		if (!type.equals(ImageConstants.TYPE_NOT_AVAILABLE)) {
			contentType = MimeTypesUtil.getExtensionContentType(type);

			httpServletResponse.setContentType(contentType);
		}

		String fileName = ParamUtil.getString(httpServletRequest, "fileName");

		byte[] bytes = getImageBytes(httpServletRequest, image);

		try {
			if (Validator.isNotNull(fileName)) {
				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, fileName, bytes,
					contentType);
			}
			else {
				ServletResponseUtil.write(httpServletResponse, bytes);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private static void _checkDirectoryIndexingEnabled(Group group)
		throws Exception {

		if (!_isDirectoryIndexingEnabled(group)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Directory indexing is not enabled for group " +
						group.getGroupId());
			}

			throw new NoSuchFolderException();
		}
	}

	private static void _checkFileEntry(String[] pathArray) throws Exception {
		if (pathArray.length == 1) {
			long fileShortcutId = GetterUtil.getLong(pathArray[0]);

			FileShortcut fileShortcut = DLAppLocalServiceUtil.getFileShortcut(
				fileShortcutId);

			DLAppLocalServiceUtil.getFileEntry(fileShortcut.getToFileEntryId());
		}
		else if (pathArray.length == 2) {
			DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
				pathArray[1], GetterUtil.getLong(pathArray[0]));
		}
		else if (pathArray.length == 3) {
			long groupId = GetterUtil.getLong(pathArray[0]);
			long folderId = GetterUtil.getLong(pathArray[1]);
			String fileName = HttpComponentsUtil.decodeURL(pathArray[2]);

			try {
				try {
					DLAppLocalServiceUtil.getFileEntryByFileName(
						groupId, folderId, fileName);
				}
				catch (NoSuchFileEntryException noSuchFileEntryException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFileEntryException);
					}

					DLAppLocalServiceUtil.getFileEntry(
						groupId, folderId, fileName);
				}
			}
			catch (RepositoryException repositoryException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(repositoryException);
				}
			}
		}
		else {
			long groupId = GetterUtil.getLong(pathArray[0]);

			String uuid = pathArray[3];

			try {
				DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
					uuid, groupId);
			}
			catch (RepositoryException repositoryException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(repositoryException);
				}
			}
		}
	}

	private static Group _getGroup(long companyId, String name)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchFriendlyURLGroup(
			companyId, StringPool.SLASH + name);

		if (group != null) {
			return group;
		}

		User user = UserLocalServiceUtil.getUserByScreenName(companyId, name);

		return user.getGroup();
	}

	private static String _getPath(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		String path =
			httpServletRequest.getContextPath() +
				httpServletRequest.getServletPath();

		return requestURI.substring(path.length() + 1);
	}

	private static User _getUser(HttpServletRequest httpServletRequest)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		if (PortalSessionThreadLocal.getHttpSession() == null) {
			PortalSessionThreadLocal.setHttpSession(httpSession);
		}

		User user = PortalUtil.getUser(httpServletRequest);

		if (user != null) {
			return user;
		}

		String userIdString = (String)httpSession.getAttribute("j_username");
		String password = (String)httpSession.getAttribute("j_password");

		if ((userIdString != null) && (password != null)) {
			long userId = GetterUtil.getLong(userIdString);

			return UserLocalServiceUtil.getUser(userId);
		}

		Company company = CompanyLocalServiceUtil.getCompany(
			PortalUtil.getCompanyId(httpServletRequest));

		return company.getGuestUser();
	}

	private static boolean _isDirectoryIndexingEnabled(Group group) {
		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		return GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty(
				"directoryIndexingEnabled"),
			PropsValues.WEB_SERVER_SERVLET_DIRECTORY_INDEXING_ENABLED);
	}

	private static FileEntry _resolveFileEntry(
			HttpServletRequest httpServletRequest, String[] pathArray)
		throws Exception {

		FileEntryFriendlyURLResolver fileEntryFriendlyURLResolver =
			_fileEntryFriendlyURLResolverSnapshot.get();

		if (fileEntryFriendlyURLResolver == null) {
			return null;
		}

		User user = _getUser(httpServletRequest);

		Group group = _getGroup(user.getCompanyId(), pathArray[1]);

		return fileEntryFriendlyURLResolver.resolveFriendlyURL(
			group.getGroupId(), pathArray[2]);
	}

	private void _checkCompanyAndGroup(
			long groupId, HttpServletRequest httpServletRequest)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No group exists group ID " + groupId);
			}

			throw new NoSuchFileEntryException();
		}

		long companyId = PortalUtil.getCompanyId(httpServletRequest);

		if (group.getCompanyId() != companyId) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Group ", groupId, " does not belong to company ",
						companyId));
			}

			throw new NoSuchFileEntryException();
		}
	}

	private void _checkFileEntry(
			FileEntry fileEntry, HttpServletRequest httpServletRequest)
		throws Exception {

		if (fileEntry == null) {
			return;
		}

		_checkCompanyAndGroup(fileEntry.getGroupId(), httpServletRequest);

		PermissionChecker permissionChecker = _getPermissionChecker(
			httpServletRequest);

		ModelResourcePermission<?> fileEntryModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				FileEntry.class.getName());

		try {
			fileEntryModelResourcePermission.check(
				permissionChecker, fileEntry.getFileEntryId(),
				_getActionId(httpServletRequest));
		}
		catch (PortalException portalException) {
			User user = permissionChecker.getUser();

			if (user.isGuestUser() &&
				!AuthLoginGroupSettingsUtil.isPromptEnabled(
					fileEntry.getGroupId())) {

				throw new NoSuchFileEntryException(portalException);
			}

			throw portalException;
		}

		FileVersion fileVersion = fileEntry.getFileVersion();

		if (!fileVersion.isExpired()) {
			return;
		}

		User user = _getUser(httpServletRequest);

		if (!permissionChecker.isContentReviewer(
				user.getCompanyId(), fileVersion.getGroupId()) &&
			!Objects.equals(fileVersion.getUserId(), user.getUserId())) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"The file entry ", fileEntry.getFileEntryId(),
						" is expired. Only users with content review ",
						"permission can access it."));
			}

			throw new FileEntryExpiredException(
				"The file entry " + fileEntry.getFileEntryId() +
					" is expired and the user does not have review permission");
		}
	}

	private void _checkResourcePermission(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String[] pathArray = StringUtil.split(
			_getPath(httpServletRequest), CharPool.SLASH);

		if (pathArray.length == 0) {

			// Check for sendGroups

			if (!PropsValues.WEB_SERVER_SERVLET_DIRECTORY_INDEXING_ENABLED) {
				httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

				throw new PrincipalException();
			}
		}
		else if (Objects.equals(pathArray[0], _PATH_SEPARATOR_FILE_ENTRY) ||
				 Validator.isNumber(pathArray[0])) {

			// Check for sendFile

			FileEntry fileEntry = _getFileEntry(pathArray, httpServletRequest);

			String portletId = _getPortletId(fileEntry, httpServletRequest);

			if (portletId == null) {
				return;
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			Group group = GroupLocalServiceUtil.getGroup(
				fileEntry.getGroupId());

			if (group.isStagingGroup()) {
				GroupPermissionUtil.check(
					permissionChecker, fileEntry.getGroupId(),
					ActionKeys.VIEW_STAGING);
			}
			else if (!PortletPermissionUtil.hasControlPanelAccessPermission(
						permissionChecker, fileEntry.getGroupId(), portletId)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, FileEntry.class.getName(),
					fileEntry.getFileEntryId(),
					ActionKeys.ACCESS_IN_CONTROL_PANEL);
			}
		}
	}

	private Callable<Void> _createFileServingCallable(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, User user) {

		return () -> {
			String path = _getPath(httpServletRequest);

			String[] pathArray = StringUtil.split(
				_getPath(httpServletRequest), CharPool.SLASH);

			if (pathArray.length == 0) {
				sendGroups(
					httpServletResponse, user,
					httpServletRequest.getServletPath() + StringPool.SLASH +
						path);
			}
			else {
				if (Validator.isNumber(pathArray[0])) {
					sendFile(
						httpServletRequest, httpServletResponse, user,
						pathArray);
				}
				else if (_PATH_SEPARATOR_FILE_ENTRY.equals(pathArray[0])) {
					sendFile(
						httpServletRequest, httpServletResponse, user,
						pathArray);
				}
				else if (PATH_PORTLET_FILE_ENTRY.equals(pathArray[0])) {
					sendPortletFileEntry(
						httpServletRequest, httpServletResponse, path,
						pathArray);
				}
				else {
					if (PropsValues.WEB_SERVER_SERVLET_CHECK_IMAGE_GALLERY &&
						isLegacyImageGalleryImageId(
							httpServletRequest, httpServletResponse)) {

						return null;
					}

					Image image = getImage(httpServletRequest, true);

					if (image != null) {
						if ((image.getCompanyId() != user.getCompanyId()) &&
							_processCompanyInactiveRequest(
								httpServletRequest, httpServletResponse,
								image.getCompanyId())) {

							return null;
						}

						writeImage(
							image, httpServletRequest, httpServletResponse);
					}
					else {
						sendDocumentLibrary(
							httpServletRequest, httpServletResponse, user,
							StringBundler.concat(
								PortalUtil.getPathContext(),
								httpServletRequest.getServletPath(),
								StringPool.SLASH, path),
							pathArray);
					}
				}
			}

			return null;
		};
	}

	private String _getActionId(HttpServletRequest httpServletRequest) {
		boolean audioPreview = ParamUtil.getBoolean(
			httpServletRequest, "audioPreview");
		boolean imagePreview = ParamUtil.getBoolean(
			httpServletRequest, "imagePreview");
		boolean videoPreview = ParamUtil.getBoolean(
			httpServletRequest, "videoPreview");

		if (audioPreview || imagePreview || videoPreview) {
			return ActionKeys.VIEW;
		}

		int documentThumbnail = ParamUtil.getInteger(
			httpServletRequest, "documentThumbnail");

		if ((documentThumbnail > 0) && (documentThumbnail <= 3)) {
			return ActionKeys.VIEW;
		}

		int imageThumbnail = ParamUtil.getInteger(
			httpServletRequest, "imageThumbnail");

		if ((imageThumbnail > 0) && (imageThumbnail <= 3)) {
			return ActionKeys.VIEW;
		}

		int previewFileIndex = ParamUtil.getInteger(
			httpServletRequest, "previewFileIndex");

		if (previewFileIndex > 0) {
			return ActionKeys.VIEW;
		}

		int videoThumbnail = ParamUtil.getInteger(
			httpServletRequest, "videoThumbnail");

		if ((videoThumbnail > 0) && (videoThumbnail <= 3)) {
			return ActionKeys.VIEW;
		}

		return ActionKeys.DOWNLOAD;
	}

	private FileEntry _getFileEntry(
			String[] pathArray, HttpServletRequest httpServletRequest)
		throws Exception {

		if (pathArray.length == 1) {
			long fileShortcutId = GetterUtil.getLong(pathArray[0]);

			FileShortcut dlFileShortcut = DLAppLocalServiceUtil.getFileShortcut(
				fileShortcutId);

			FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
				dlFileShortcut.getToFileEntryId());

			_checkFileEntry(fileEntry, httpServletRequest);

			return fileEntry;
		}
		else if (pathArray.length == 2) {
			long groupId = GetterUtil.getLong(pathArray[0]);

			FileEntry fileEntry =
				DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
					pathArray[1], groupId);

			_checkFileEntry(fileEntry, httpServletRequest);

			return fileEntry;
		}
		else if (_PATH_SEPARATOR_FILE_ENTRY.equals(pathArray[0])) {
			FileEntry fileEntry = _resolveFileEntry(
				httpServletRequest, pathArray);

			if (fileEntry == null) {
				throw new NoSuchFileEntryException(
					"No file entry found for friendly URL " +
						Arrays.toString(pathArray));
			}

			_checkFileEntry(fileEntry, httpServletRequest);

			return fileEntry;
		}
		else if (pathArray.length == 3) {
			long groupId = GetterUtil.getLong(pathArray[0]);
			long folderId = GetterUtil.getLong(pathArray[1]);

			String fileName = HttpComponentsUtil.decodeURL(pathArray[2]);

			if (fileName.contains(StringPool.QUESTION)) {
				fileName = fileName.substring(
					0, fileName.indexOf(StringPool.QUESTION));
			}

			try {
				FileEntry fileEntry =
					DLAppLocalServiceUtil.getFileEntryByFileName(
						groupId, folderId, fileName);

				_checkFileEntry(fileEntry, httpServletRequest);

				return fileEntry;
			}
			catch (NoSuchFileEntryException noSuchFileEntryException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFileEntryException);
				}

				FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
					groupId, folderId, fileName);

				_checkFileEntry(fileEntry, httpServletRequest);

				return fileEntry;
			}
		}
		else {
			long groupId = GetterUtil.getLong(pathArray[0]);

			String uuid = pathArray[3];

			FileEntry fileEntry =
				DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
					uuid, groupId);

			_checkFileEntry(fileEntry, httpServletRequest);

			return fileEntry;
		}
	}

	private PermissionChecker _getPermissionChecker(
			HttpServletRequest httpServletRequest)
		throws Exception {

		User user = PortalUtil.getUser(httpServletRequest);

		if (user == null) {
			user = UserLocalServiceUtil.getGuestUser(
				PortalUtil.getCompanyId(httpServletRequest));
		}

		return PermissionThreadLocal.getPermissionChecker(
			user, !user.isGuestUser());
	}

	private String _getPortletId(
			FileEntry fileEntry, HttpServletRequest httpServletRequest)
		throws Exception {

		if (fileEntry.isInTrash()) {
			int status = ParamUtil.getInteger(
				httpServletRequest, "status",
				WorkflowConstants.STATUS_APPROVED);

			if (status != WorkflowConstants.STATUS_IN_TRASH) {
				throw new NoSuchFileEntryException();
			}

			return PortletProviderUtil.getPortletId(
				"com.liferay.trash.model.TrashEntry",
				PortletProvider.Action.VIEW);
		}

		Group group = GroupLocalServiceUtil.getGroup(fileEntry.getGroupId());

		if (!group.isStagingGroup()) {
			return null;
		}

		return PortletProviderUtil.getPortletId(
			FileEntry.class.getName(), PortletProvider.Action.VIEW);
	}

	private boolean _processCompanyInactiveRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long companyId)
		throws IOException {

		if ((companyId == CompanyConstants.SYSTEM) ||
			PortalInstances.isCompanyActive(companyId)) {

			return false;
		}

		InactiveRequestHandler inactiveRequestHandler =
			_inactiveRequestHandlerSnapshot.get();

		inactiveRequestHandler.processInactiveRequest(
			httpServletRequest, httpServletResponse,
			"this-instance-is-inactive-please-contact-the-administrator");

		if (_log.isDebugEnabled()) {
			_log.debug("Processed company inactive request");
		}

		return true;
	}

	private static final String _PATH_SEPARATOR_FILE_ENTRY =
		FriendlyURLResolverConstants.URL_SEPARATOR_Y_FILE_ENTRY;

	private static final boolean _WEB_SERVER_SERVLET_VERSION_VERBOSITY_DEFAULT =
		StringUtil.equalsIgnoreCase(
			PropsValues.WEB_SERVER_SERVLET_VERSION_VERBOSITY,
			ReleaseInfo.getName());

	private static final boolean _WEB_SERVER_SERVLET_VERSION_VERBOSITY_PARTIAL =
		StringUtil.equalsIgnoreCase(
			PropsValues.WEB_SERVER_SERVLET_VERSION_VERBOSITY, "partial");

	private static final Log _log = LogFactoryUtil.getLog(
		WebServerServlet.class);

	private static final Set<String> _acceptRangesMimeTypes = SetUtil.fromArray(
		PropsValues.WEB_SERVER_SERVLET_ACCEPT_RANGES_MIME_TYPES);
	private static final Snapshot<FileEntryFriendlyURLResolver>
		_fileEntryFriendlyURLResolverSnapshot = new Snapshot<>(
			WebServerServlet.class, FileEntryFriendlyURLResolver.class);
	private static final Snapshot<InactiveRequestHandler>
		_inactiveRequestHandlerSnapshot = new Snapshot<>(
			WebServerServlet.class, InactiveRequestHandler.class);
	private static final Snapshot<MessageBus> _messageBusSnapshot =
		new Snapshot<>(WebServerServlet.class, MessageBus.class);
	private static final Snapshot<TrashHelper> _trashTitleResolverSnapshot =
		new Snapshot<>(WebServerServlet.class, TrashHelper.class);
	private static final Snapshot<UserFileUploadsSettings>
		_userFileUploadsSettingsSnapshot = new Snapshot<>(
			WebServerServlet.class, UserFileUploadsSettings.class);

	private boolean _lastModified = true;
	private TemplateResource _templateResource;

}