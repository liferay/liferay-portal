/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.servlet;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;
import com.liferay.translation.web.internal.helper.InfoItemHelper;
import com.liferay.translation.web.internal.helper.TranslationRequestHelper;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.translation.web.internal.servlet.ExportTranslationServlet",
		"osgi.http.whiteboard.servlet.pattern=/translation/export_translation",
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class ExportTranslationServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			User user = _portal.getUser(httpServletRequest);

			if ((user == null) || user.isGuestUser()) {
				throw new PrincipalException.MustBeAuthenticated(
					StringPool.BLANK);
			}

			long[] segmentsExperienceIds = ParamUtil.getLongValues(
				httpServletRequest, "segmentsExperienceIds");

			TranslationRequestHelper translationRequestHelper =
				new TranslationRequestHelper(
					httpServletRequest, _infoItemServiceRegistry,
					_segmentsExperienceLocalService);

			String className = translationRequestHelper.getClassName(
				segmentsExperienceIds);

			String exportMimeType = ParamUtil.getString(
				httpServletRequest, "exportMimeType");
			String sourceLanguageId = ParamUtil.getString(
				httpServletRequest, "sourceLanguageId");
			String[] targetLanguageIds = ParamUtil.getStringValues(
				httpServletRequest, "targetLanguageIds");

			ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

			Set<Long> classPKs = SetUtil.fromArray(
				_getClassPKs(
					className, segmentsExperienceIds,
					translationRequestHelper));

			InfoItemPermissionProvider infoItemPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemPermissionProvider.class, className);

			PermissionChecker permissionChecker =
				_permissionCheckerFactory.create(user);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			for (long classPK : classPKs) {
				if ((infoItemPermissionProvider != null) &&
					!infoItemPermissionProvider.hasPermission(
						permissionChecker,
						new InfoItemReference(className, classPK),
						ActionKeys.VIEW)) {

					throw new PrincipalException();
				}

				_addZipEntry(
					zipWriter, className, classPK, exportMimeType,
					sourceLanguageId, targetLanguageIds,
					_portal.getLocale(httpServletRequest));
			}

			try (InputStream inputStream = new FileInputStream(
					zipWriter.getFile())) {

				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse,
					_getZipFileName(
						translationRequestHelper.getModelClassName(),
						translationRequestHelper.getModelClassPK(),
						_language.get(
							_portal.getLocale(httpServletRequest),
							"model.resource." + className),
						_isMultipleModels(
							translationRequestHelper.getModelClassPKs()),
						sourceLanguageId,
						_portal.getLocale(httpServletRequest)),
					inputStream, ContentTypes.APPLICATION_ZIP);
			}
		}
		catch (PortalException portalException) {
			throw new IOException(portalException);
		}
	}

	private void _addZipEntry(
			ZipWriter zipWriter, String className, long classPK,
			String exportMimeType, String sourceLanguageId,
			String[] targetLanguageIds, Locale locale)
		throws IOException, PortalException {

		TranslationInfoItemFieldValuesExporter
			translationInfoItemFieldValuesExporter =
				_translationInfoItemFieldValuesExporterRegistry.
					getTranslationInfoItemFieldValuesExporter(exportMimeType);

		if (translationInfoItemFieldValuesExporter == null) {
			throw new PortalException(
				"Unknown export mime type: " + exportMimeType);
		}

		InfoItemHelper infoItemHelper = new InfoItemHelper(
			className, _infoItemServiceRegistry);

		String infoItemTitle = infoItemHelper.getInfoItemTitle(classPK, locale);

		if (infoItemTitle == null) {
			infoItemTitle =
				_language.get(locale, "model.resource." + className) +
					StringPool.SPACE + classPK;
		}

		InfoItemFieldValuesProvider<Object> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, className);

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		Object object = infoItemObjectProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(classPK));

		for (String targetLanguageId : targetLanguageIds) {
			zipWriter.addEntry(
				_getXLIFFFileName(
					infoItemTitle, sourceLanguageId, targetLanguageId),
				translationInfoItemFieldValuesExporter.
					exportInfoItemFieldValues(
						infoItemFieldValuesProvider.getInfoItemFieldValues(
							object),
						LocaleUtil.fromLanguageId(sourceLanguageId),
						LocaleUtil.fromLanguageId(targetLanguageId)));
		}
	}

	private long[] _getClassPKs(
			String className, long[] segmentsExperienceIds,
			TranslationRequestHelper translationRequestHelper)
		throws PortalException {

		if (!className.equals(Layout.class.getName())) {
			return translationRequestHelper.getClassPKs(segmentsExperienceIds);
		}

		long[] classPKs = translationRequestHelper.getClassPKs(
			segmentsExperienceIds);

		long[] replacementClassPKs = new long[classPKs.length];

		for (int i = 0; i < classPKs.length; i++) {
			replacementClassPKs[i] = _getDraftLayoutPlid(classPKs[i]);
		}

		return replacementClassPKs;
	}

	private long _getDraftLayoutPlid(long plid) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if ((layout == null) || layout.isDraftLayout()) {
			return plid;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			return plid;
		}

		return draftLayout.getPlid();
	}

	private String _getPrefixName(
		long classPK, String classNameTitle, String infoItemTitle,
		boolean multipleModels, Locale locale) {

		if (multipleModels) {
			return classNameTitle + StringPool.SPACE +
				_language.get(locale, "translations");
		}

		if (infoItemTitle != null) {
			return infoItemTitle;
		}

		return classNameTitle + StringPool.SPACE + classPK;
	}

	private String _getXLIFFFileName(
			String title, String sourceLanguageId, String targetLanguageId)
		throws PortalException {

		return StringBundler.concat(
			StringPool.FORWARD_SLASH,
			StringUtil.removeSubstrings(title, PropsValues.DL_CHAR_BLACKLIST),
			StringPool.DASH, sourceLanguageId, StringPool.DASH,
			targetLanguageId, ".xlf");
	}

	private String _getZipFileName(
			String className, long classPK, String classNameTitle,
			boolean multipleModels, String sourceLanguageId, Locale locale)
		throws NoSuchInfoItemException {

		InfoItemHelper infoItemHelper = new InfoItemHelper(
			className, _infoItemServiceRegistry);

		String infoItemTitle = infoItemHelper.getInfoItemTitle(classPK, locale);

		return StringBundler.concat(
			StringUtil.removeSubstrings(
				_getPrefixName(
					classPK, classNameTitle, infoItemTitle, multipleModels,
					locale),
				PropsValues.DL_CHAR_BLACKLIST),
			StringPool.DASH, sourceLanguageId, ".zip");
	}

	private boolean _isMultipleModels(long[] classPKs) {
		if (classPKs.length > 1) {
			return true;
		}

		return false;
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private TranslationInfoItemFieldValuesExporterRegistry
		_translationInfoItemFieldValuesExporterRegistry;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}