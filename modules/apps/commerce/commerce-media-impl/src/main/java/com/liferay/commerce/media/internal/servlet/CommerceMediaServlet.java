/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.media.internal.servlet;

import com.liferay.account.constants.AccountConstants;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.commerce.media.CommerceMediaProvider;
import com.liferay.commerce.media.constants.CommerceMediaConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.order.constants.CommerceVirtualOrderActionKeys;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItem;
import com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.order.service.CommerceVirtualOrderItemService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/" + CommerceMediaConstants.SERVLET_PATH,
		"osgi.http.whiteboard.servlet.name=com.liferay.commerce.media.servlet.CommerceMediaServlet",
		"osgi.http.whiteboard.servlet.pattern=/" + CommerceMediaConstants.SERVLET_PATH + "/*"
	},
	service = Servlet.class
)
public class CommerceMediaServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		if (PortalSessionThreadLocal.getHttpSession() == null) {
			PortalSessionThreadLocal.setHttpSession(
				httpServletRequest.getSession());
		}

		try {
			User user = _portal.getUser(httpServletRequest);

			if (user == null) {
				user = _userLocalService.getGuestUser(
					_portal.getCompanyId(httpServletRequest));
			}

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			PrincipalThreadLocal.setName(user.getUserId());
		}
		catch (Exception exception) {
			_log.error(exception);

			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		String contentDisposition = HttpHeaders.CONTENT_DISPOSITION_INLINE;

		boolean download = ParamUtil.getBoolean(httpServletRequest, "download");

		if (download) {
			contentDisposition = HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT;
		}

		_sendMediaBytes(
			httpServletRequest, httpServletResponse, contentDisposition);
	}

	private FileEntry _getFileEntry(HttpServletRequest httpServletRequest)
		throws PortalException {

		String path = HttpComponentsUtil.fixPath(
			httpServletRequest.getPathInfo());

		String[] pathArray = StringUtil.split(path, CharPool.SLASH);

		if (pathArray.length < 2) {
			return null;
		}

		String cpAttachmentFileEntryIdParam = pathArray[3];

		if (cpAttachmentFileEntryIdParam.contains(StringPool.QUESTION)) {
			String[] cpAttachmentFileEntryIdParamArray = StringUtil.split(
				cpAttachmentFileEntryIdParam, StringPool.QUESTION);

			cpAttachmentFileEntryIdParam = cpAttachmentFileEntryIdParamArray[0];
		}

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntry(
				GetterUtil.getLongStrict(cpAttachmentFileEntryIdParam));

		return _getFileEntry(cpAttachmentFileEntry.getFileEntryId());
	}

	private FileEntry _getFileEntry(long fileEntryId) {
		try {
			return _dlAppLocalService.getFileEntry(fileEntryId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private long _getGroupId(
			long commerceAccountId, long cpAttachmentFileEntryId)
		throws PortalException {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntry(
				cpAttachmentFileEntryId);

		String className = cpAttachmentFileEntry.getClassName();

		if (className.equals(AssetCategory.class.getName())) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchCategory(
					cpAttachmentFileEntry.getClassPK());

			try {
				if (AssetCategoryPermission.contains(
						PermissionThreadLocal.getPermissionChecker(),
						assetCategory, ActionKeys.VIEW)) {

					Company company = _companyLocalService.getCompany(
						assetCategory.getCompanyId());

					return company.getGroupId();
				}
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
		else if (className.equals(CPDefinition.class.getName())) {
			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(
					cpAttachmentFileEntry.getClassPK());

			if (commerceAccountId == AccountConstants.ACCOUNT_ENTRY_ID_ADMIN) {
				_commerceCatalogModelResourcePermission.check(
					PermissionThreadLocal.getPermissionChecker(),
					cpDefinition.getCommerceCatalog(), ActionKeys.VIEW);
			}
			else {
				_commerceProductViewPermission.check(
					PermissionThreadLocal.getPermissionChecker(),
					commerceAccountId, cpDefinition.getCPDefinitionId());
			}

			return cpDefinition.getGroupId();
		}

		return 0;
	}

	private void _sendDefaultMediaBytes(
			long groupId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String contentDisposition)
		throws IOException {

		try {
			FileEntry fileEntry =
				_commerceMediaProvider.getDefaultImageFileEntry(
					_portal.getCompanyId(httpServletRequest), groupId);

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse,
				fileEntry.getFileName(),
				_file.getBytes(fileEntry.getContentStream()),
				fileEntry.getMimeType(), contentDisposition);
		}
		catch (Exception exception) {
			_log.error(exception);

			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void _sendError(
		HttpServletResponse httpServletResponse, int status, String message) {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			JSONObject jsonObject = JSONUtil.put(
				CommerceMediaConstants.RESPONSE_ERROR,
				JSONUtil.put(
					CommerceMediaConstants.RESPONSE_CODE, status
				).put(
					CommerceMediaConstants.RESPONSE_MESSAGE, message
				));

			printWriter.write(jsonObject.toString());

			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
			httpServletResponse.setStatus(status);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void _sendMediaBytes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String contentDisposition)
		throws IOException {

		String path = HttpComponentsUtil.fixPath(
			httpServletRequest.getPathInfo());

		String[] pathArray = StringUtil.split(path, CharPool.SLASH);

		String commerceVirtualOrderItemPath = pathArray[0];

		if (CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_ORDER_ITEM.contains(
				commerceVirtualOrderItemPath)) {

			long commerceVirtualOrderItemId = GetterUtil.getLongStrict(
				pathArray[1]);
			long fileEntryId = GetterUtil.getLongStrict(pathArray[3]);

			try {
				CommerceVirtualOrderItem commerceVirtualOrderItem =
					_commerceVirtualOrderItemService.
						fetchCommerceVirtualOrderItem(
							commerceVirtualOrderItemId);

				if (commerceVirtualOrderItem == null) {
					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The commerce virtual order item " +
							commerceVirtualOrderItemId + " does not exist");

					return;
				}

				if (!ArrayUtil.contains(
						TransformUtil.transformToLongArray(
							commerceVirtualOrderItem.
								getCommerceVirtualOrderItemFileEntries(),
							commerceVirtualOrderItemFileEntry ->
								commerceVirtualOrderItemFileEntry.
									getFileEntryId()),
						fileEntryId)) {

					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The commerce virtual order item file entry " +
							fileEntryId + " does not exist");

					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						StringBundler.concat(
							"The commerce virtual order item ",
							commerceVirtualOrderItemId,
							" does not have commerce virtual order item file ",
							"entry ", fileEntryId));

					return;
				}

				FileEntry fileEntry = _getFileEntry(fileEntryId);

				if (fileEntry == null) {
					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The file entry " + fileEntryId + " does not exist");

					return;
				}

				CommerceVirtualOrderItemFileEntry
					commerceVirtualOrderItemFileEntry =
						_commerceVirtualOrderItemFileEntryLocalService.
							fetchCommerceVirtualOrderItemFileEntry(
								commerceVirtualOrderItemId, fileEntryId);

				if (commerceVirtualOrderItemFileEntry == null) {
					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The file entry " + fileEntryId + " does not exist");

					return;
				}

				if (!_commerceVirtualOrderItemFileEntryModelResourcePermission.
						contains(
							PermissionThreadLocal.getPermissionChecker(),
							commerceVirtualOrderItemFileEntry,
							CommerceVirtualOrderActionKeys.
								DOWNLOAD_COMMERCE_VIRTUAL_ORDER_ITEM)) {

					_sendError(
						httpServletResponse,
						HttpServletResponse.SC_UNAUTHORIZED,
						"You do not have permission to access the requested " +
							"resource");

					return;
				}

				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse,
					fileEntry.getFileName(),
					_file.getBytes(fileEntry.getContentStream()),
					fileEntry.getMimeType(),
					HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);

				_commerceVirtualOrderItemFileEntryLocalService.incrementUsages(
					commerceVirtualOrderItemFileEntry.
						getCommerceVirtualOrderItemFileEntryId());

				return;
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				if (portalException instanceof PrincipalException) {
					_sendError(
						httpServletResponse,
						HttpServletResponse.SC_UNAUTHORIZED,
						"You do not have permission to access the requested " +
							"resource");

					return;
				}

				_sendError(
					httpServletResponse,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An unexpected error occurred");

				return;
			}
		}

		if (pathArray.length >= 6) {
			if (CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_PRODUCT.contains(
					pathArray[2])) {

				_sendVirtualSettingsMediaBytes(
					httpServletRequest, httpServletResponse,
					CPDefinition.class.getName(), false, pathArray);

				return;
			}
			else if (CommerceMediaConstants.
						URL_SEPARATOR_VIRTUAL_PRODUCT_SAMPLE.contains(
							pathArray[2])) {

				_sendVirtualSettingsMediaBytes(
					httpServletRequest, httpServletResponse,
					CPDefinition.class.getName(), true, pathArray);

				return;
			}
			else if (CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_SKU.contains(
						pathArray[2])) {

				_sendVirtualSettingsMediaBytes(
					httpServletRequest, httpServletResponse,
					CPInstance.class.getName(), false, pathArray);

				return;
			}
			else if (CommerceMediaConstants.URL_SEPARATOR_VIRTUAL_SKU_SAMPLE.
						contains(pathArray[2])) {

				_sendVirtualSettingsMediaBytes(
					httpServletRequest, httpServletResponse,
					CPInstance.class.getName(), true, pathArray);

				return;
			}
		}

		if (pathArray.length < 2) {
			long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

			if (groupId == 0) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			_sendDefaultMediaBytes(
				groupId, httpServletRequest, httpServletResponse,
				contentDisposition);

			return;
		}

		try {
			String cpAttachmentFileEntryIdParam = pathArray[3];

			if (cpAttachmentFileEntryIdParam.contains(StringPool.QUESTION)) {
				String[] cpAttachmentFileEntryIdParamArray = StringUtil.split(
					cpAttachmentFileEntryIdParam, StringPool.QUESTION);

				cpAttachmentFileEntryIdParam =
					cpAttachmentFileEntryIdParamArray[0];
			}

			long groupId = _getGroupId(
				GetterUtil.getLongStrict(pathArray[1]),
				GetterUtil.getLongStrict(cpAttachmentFileEntryIdParam));

			if (groupId == 0) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			FileEntry fileEntry = _getFileEntry(httpServletRequest);

			if (fileEntry == null) {
				_sendDefaultMediaBytes(
					groupId, httpServletRequest, httpServletResponse,
					contentDisposition);

				return;
			}

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse,
				fileEntry.getFileName(),
				_file.getBytes(fileEntry.getContentStream()),
				fileEntry.getMimeType(), contentDisposition);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void _sendVirtualSettingsMediaBytes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String className,
			boolean sample, String[] pathArray)
		throws IOException {

		long commerceAccountId = GetterUtil.getLongStrict(pathArray[1]);
		long classPK = GetterUtil.getLongStrict(pathArray[3]);
		long fileEntryId = GetterUtil.getLongStrict(pathArray[5]);

		try {
			CPDefinition cpDefinition = null;

			if (className.equals(CPInstance.class.getName())) {
				CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
					classPK);

				if (cpInstance == null) {
					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The commerce product instance " + classPK +
							" does not exist");

					return;
				}

				cpDefinition = cpInstance.getCPDefinition();
			}
			else {
				cpDefinition = _cpDefinitionLocalService.fetchCPDefinition(
					classPK);

				if (cpDefinition == null) {
					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The commerce product definition " + classPK +
							" does not exist");

					return;
				}
			}

			if (commerceAccountId == AccountConstants.ACCOUNT_ENTRY_ID_ADMIN) {
				_commerceCatalogModelResourcePermission.check(
					PermissionThreadLocal.getPermissionChecker(),
					cpDefinition.getCommerceCatalog(), ActionKeys.VIEW);
			}
			else {
				if (sample) {
					_commerceProductViewPermission.check(
						PermissionThreadLocal.getPermissionChecker(),
						commerceAccountId, cpDefinition.getCPDefinitionId());
				}
				else {
					_sendError(
						httpServletResponse,
						HttpServletResponse.SC_UNAUTHORIZED,
						"You do not have permission to access the requested " +
							"resource");

					return;
				}
			}

			CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
				_cpDefinitionVirtualSettingLocalService.
					fetchCPDefinitionVirtualSetting(className, classPK);

			if (cpDefinitionVirtualSetting == null) {
				_sendError(
					httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
					"The commerce product definition " + classPK +
						" is not virtual");

				return;
			}

			FileEntry fileEntry = null;

			if (sample) {
				fileEntry = cpDefinitionVirtualSetting.getSampleFileEntry();

				if ((fileEntry == null) ||
					(fileEntry.getFileEntryId() != fileEntryId)) {

					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The file entry " + fileEntryId + " does not exist");

					return;
				}
			}
			else {
				if (!ArrayUtil.contains(
						TransformUtil.transformToLongArray(
							cpDefinitionVirtualSetting.
								getCPDVirtualSettingFileEntries(),
							cpdVirtualSettingFileEntry ->
								cpdVirtualSettingFileEntry.getFileEntryId()),
						fileEntryId)) {

					_sendError(
						httpServletResponse, HttpServletResponse.SC_NOT_FOUND,
						"The file entry " + fileEntryId + " does not exist");

					return;
				}

				fileEntry = _getFileEntry(fileEntryId);
			}

			ServletResponseUtil.sendFile(
				httpServletRequest, httpServletResponse,
				fileEntry.getFileName(),
				_file.getBytes(fileEntry.getContentStream()),
				fileEntry.getMimeType(),
				HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			if (portalException instanceof PrincipalException) {
				_sendError(
					httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
					"You do not have permission to access the requested " +
						"resource");

				return;
			}

			_sendError(
				httpServletResponse,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"An unexpected error occurred");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceMediaServlet.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CommerceCatalog)"
	)
	private ModelResourcePermission<CommerceCatalog>
		_commerceCatalogModelResourcePermission;

	@Reference
	private CommerceMediaProvider _commerceMediaProvider;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.type.virtual.order.model.CommerceVirtualOrderItemFileEntry)"
	)
	private ModelResourcePermission<CommerceVirtualOrderItemFileEntry>
		_commerceVirtualOrderItemFileEntryModelResourcePermission;

	@Reference
	private CommerceVirtualOrderItemService _commerceVirtualOrderItemService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private File _file;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}