/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.asset.model;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.translation.info.field.TranslationInfoFieldChecker;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.snapshot.TranslationSnapshot;
import com.liferay.translation.snapshot.TranslationSnapshotProvider;
import com.liferay.translation.web.internal.display.context.ViewTranslationDisplayContext;
import com.liferay.translation.web.internal.helper.InfoItemHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;

import java.nio.charset.StandardCharsets;

import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class TranslationEntryAssetRenderer
	extends BaseJSPAssetRenderer<TranslationEntry> {

	public TranslationEntryAssetRenderer(
		InfoItemServiceRegistry infoItemServiceRegistry,
		ServletContext servletContext, TranslationEntry translationEntry,
		TranslationInfoFieldChecker translationInfoFieldChecker,
		TranslationSnapshotProvider translationSnapshotProvider) {

		_infoItemServiceRegistry = infoItemServiceRegistry;
		_translationEntry = translationEntry;
		_translationInfoFieldChecker = translationInfoFieldChecker;
		_translationSnapshotProvider = translationSnapshotProvider;

		setServletContext(servletContext);
	}

	@Override
	public TranslationEntry getAssetObject() {
		return _translationEntry;
	}

	@Override
	public String getClassName() {
		return TranslationEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		return _translationEntry.getTranslationEntryId();
	}

	@Override
	public long getGroupId() {
		return _translationEntry.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		return "/asset/full_content.jsp";
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return getTitle(PortalUtil.getLocale(portletRequest));
	}

	@Override
	public String getTitle(Locale locale) {
		InfoItemHelper infoItemHelper = new InfoItemHelper(
			_translationEntry.getClassName(), _infoItemServiceRegistry);

		String infoItemTitle = infoItemHelper.getInfoItemTitle(
			_translationEntry.getClassPK(), locale);

		if (infoItemTitle == null) {
			infoItemTitle = _getAssetRendererTitle(locale);
		}

		return LanguageUtil.format(
			locale, "translation-of-x-to-x",
			new Object[] {
				infoItemTitle,
				StringUtil.replace(
					_translationEntry.getLanguageId(), CharPool.UNDERLINE,
					CharPool.DASH)
			});
	}

	@Override
	public long getUserId() {
		return _translationEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _translationEntry.getUserName();
	}

	@Override
	public String getUuid() {
		return _translationEntry.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		InfoItemFormProvider<Object> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _translationEntry.getClassName());
		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, _translationEntry.getClassName(),
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		String content = _translationEntry.getContent();

		TranslationSnapshot translationSnapshot =
			_translationSnapshotProvider.getTranslationSnapshot(
				_translationEntry.getGroupId(),
				new InfoItemReference(
					_translationEntry.getClassName(),
					_translationEntry.getClassPK()),
				new ByteArrayInputStream(
					content.getBytes(StandardCharsets.UTF_8)),
				true);

		httpServletRequest.setAttribute(
			ViewTranslationDisplayContext.class.getName(),
			new ViewTranslationDisplayContext(
				httpServletRequest,
				infoItemFormProvider.getInfoForm(
					infoItemObjectProvider.getInfoItem(
						new ClassPKInfoItemIdentifier(
							_translationEntry.getClassPK()))),
				_translationInfoFieldChecker, translationSnapshot));

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private AssetRenderer<?> _getAssetRenderer(
			AssetRendererFactory<?> assetRendererFactory)
		throws PortalException {

		try {
			return assetRendererFactory.getAssetRenderer(
				_translationEntry.getClassPK());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private String _getAssetRendererTitle(Locale locale) {
		try {
			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						_translationEntry.getClassName());

			if (assetRendererFactory == null) {
				return LanguageUtil.get(locale, "translation");
			}

			AssetRenderer<?> assetRenderer = _getAssetRenderer(
				assetRendererFactory);

			if (assetRenderer == null) {
				return LanguageUtil.get(locale, "translation");
			}

			return assetRenderer.getTitle(locale);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return LanguageUtil.get(locale, "translation");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TranslationEntryAssetRenderer.class);

	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final TranslationEntry _translationEntry;
	private final TranslationInfoFieldChecker _translationInfoFieldChecker;
	private final TranslationSnapshotProvider _translationSnapshotProvider;

}