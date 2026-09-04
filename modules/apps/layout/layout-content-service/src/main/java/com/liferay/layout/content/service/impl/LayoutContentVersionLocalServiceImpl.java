/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.impl;

import com.liferay.layout.content.exception.DuplicateLayoutContentVersionExternalReferenceCodeException;
import com.liferay.layout.content.exception.LayoutContentVersionExternalReferenceCodeException;
import com.liferay.layout.content.exception.LayoutContentVersionNameException;
import com.liferay.layout.content.exception.RequiredLayoutContentVersionException;
import com.liferay.layout.content.exception.UnsupportedLayoutLayoutContentVersionException;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.layout.content.service.base.LayoutContentVersionLocalServiceBaseImpl;
import com.liferay.layout.content.util.comparator.LayoutContentVersionVersionComparator;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletToken;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "model.class.name=com.liferay.layout.content.model.LayoutContentVersion",
	service = AopService.class
)
public class LayoutContentVersionLocalServiceImpl
	extends LayoutContentVersionLocalServiceBaseImpl {

	@Override
	public LayoutContentVersion addLayoutContentVersion(
			String externalReferenceCode, long userId, String data,
			Map<Locale, String> nameMap, long plid, int status)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayout(plid);

		FeatureFlagManagerUtil.checkEnabled(layout.getCompanyId(), "LPD-10622");

		_validateExternalReferenceCode(
			externalReferenceCode, layout.getGroupId());
		_validateLayout(layout);

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.create(
				counterLocalService.increment(
					LayoutContentVersion.class.getName()));

		int version = _getNextVersion(plid);

		if (Validator.isNull(externalReferenceCode)) {
			String defaultExternalReferenceCode =
				layout.getExternalReferenceCode() + "_v_" + version;

			if (defaultExternalReferenceCode.length() <=
					ModelHintsUtil.getMaxLength(
						LayoutContentVersion.class.getName(),
						"externalReferenceCode")) {

				externalReferenceCode = defaultExternalReferenceCode;
			}
		}

		layoutContentVersion.setExternalReferenceCode(externalReferenceCode);

		layoutContentVersion.setGroupId(layout.getGroupId());
		layoutContentVersion.setCompanyId(layout.getCompanyId());
		layoutContentVersion.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		layoutContentVersion.setUserName(user.getFullName());

		layoutContentVersion.setData(data);
		layoutContentVersion.setDataHash(
			DigesterUtil.digestHex(
				DigesterUtil.SHA_256, GetterUtil.getString(data)));
		layoutContentVersion.setNameMap(
			MapUtil.isEmpty(nameMap) ? layout.getNameMap() : nameMap);
		layoutContentVersion.setPlid(plid);
		layoutContentVersion.setSpecSchemaVersion("v1.0");
		layoutContentVersion.setVersion(version);
		layoutContentVersion.setStatus(status);
		layoutContentVersion.setStatusByUserId(userId);
		layoutContentVersion.setStatusByUserName(user.getFullName());
		layoutContentVersion.setStatusDate(new Date());

		layoutContentVersion = layoutContentVersionPersistence.update(
			layoutContentVersion);

		_addLayoutContentVersionPreviews(layout, layoutContentVersion, userId);

		return layoutContentVersion;
	}

	@Override
	public LayoutContentVersion addOrUpdateLayoutContentVersion(
			String externalReferenceCode, long userId, String data,
			Map<Locale, String> nameMap, long plid, int status)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayout(plid);

		FeatureFlagManagerUtil.checkEnabled(layout.getCompanyId(), "LPD-10622");

		_validateLayout(layout);

		LayoutContentVersion latestLayoutContentVersion =
			layoutContentVersionPersistence.fetchByPlid_First(
				plid, LayoutContentVersionVersionComparator.getInstance(false));

		String dataHash = DigesterUtil.digestHex(
			DigesterUtil.SHA_256, GetterUtil.getString(data));

		if ((latestLayoutContentVersion == null) ||
			!dataHash.equals(latestLayoutContentVersion.getDataHash())) {

			return addLayoutContentVersion(
				externalReferenceCode, userId, data, nameMap, plid, status);
		}

		latestLayoutContentVersion.setUserId(userId);

		User user = _userLocalService.getUser(userId);

		latestLayoutContentVersion.setUserName(user.getFullName());

		return layoutContentVersionPersistence.update(
			latestLayoutContentVersion);
	}

	@Override
	public LayoutContentVersion deleteLayoutContentVersion(
			long layoutContentVersionId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		_validateLayout(
			_layoutLocalService.getLayout(layoutContentVersion.getPlid()));

		if ((layoutContentVersion.getStatus() ==
				WorkflowConstants.STATUS_APPROVED) &&
			(layoutContentVersion.getLayoutContentVersionId() ==
				getLatestApprovedLayoutContentVersionId(
					layoutContentVersion.getPlid()))) {

			throw new RequiredLayoutContentVersionException();
		}

		_layoutContentVersionPreviewLocalService.
			deleteLayoutContentVersionPreviews(layoutContentVersionId);

		return layoutContentVersionPersistence.remove(layoutContentVersionId);
	}

	@Override
	public long getLatestApprovedLayoutContentVersionId(long plid) {
		LayoutContentVersion latestApprovedLayoutContentVersion =
			layoutContentVersionPersistence.fetchByP_S_First(
				plid, WorkflowConstants.STATUS_APPROVED,
				LayoutContentVersionVersionComparator.getInstance(false));

		if (latestApprovedLayoutContentVersion == null) {
			return 0;
		}

		return latestApprovedLayoutContentVersion.getLayoutContentVersionId();
	}

	@Override
	public LayoutContentVersion getLayoutContentVersion(
			long layoutContentVersionId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			super.getLayoutContentVersion(layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		return layoutContentVersion;
	}

	@Override
	public LayoutContentVersion getLayoutContentVersionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			super.getLayoutContentVersionByExternalReferenceCode(
				externalReferenceCode, groupId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		return layoutContentVersion;
	}

	@Override
	public List<LayoutContentVersion> getLayoutContentVersions(long plid)
		throws PortalException {

		Layout layout = _layoutLocalService.getLayout(plid);

		FeatureFlagManagerUtil.checkEnabled(layout.getCompanyId(), "LPD-10622");

		return layoutContentVersionPersistence.findByPlid(plid);
	}

	@Override
	public LayoutContentVersion updateLayoutContentVersion(
			long layoutContentVersionId, Map<Locale, String> nameMap)
		throws PortalException {

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.findByPrimaryKey(
				layoutContentVersionId);

		FeatureFlagManagerUtil.checkEnabled(
			layoutContentVersion.getCompanyId(), "LPD-10622");

		_validateLayout(
			_layoutLocalService.getLayout(layoutContentVersion.getPlid()));

		if (MapUtil.isEmpty(nameMap)) {
			throw new LayoutContentVersionNameException("Name is null");
		}

		layoutContentVersion.setNameMap(nameMap);

		return layoutContentVersionPersistence.update(layoutContentVersion);
	}

	private static String _read(String name) {
		try (InputStream inputStream =
				LayoutContentVersionLocalServiceImpl.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private void _addLayoutContentVersionPreviews(
		Layout layout, LayoutContentVersion layoutContentVersion, long userId) {

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			_initThemeDisplay(serviceContext.getRequest(), layout);

			for (SegmentsExperience segmentsExperience :
					_segmentsExperienceLocalService.getSegmentsExperiences(
						layout.getGroupId(), layout.getPlid())) {

				_addLayoutContentVersionPreviews(
					layout, layoutContentVersion, segmentsExperience,
					serviceContext, userId);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to add layout content version previews for PLID " +
						layout.getPlid(),
					exception);
			}
		}
	}

	private void _addLayoutContentVersionPreviews(
		Layout layout, LayoutContentVersion layoutContentVersion,
		SegmentsExperience segmentsExperience, ServiceContext serviceContext,
		long userId) {

		for (Locale locale :
				_language.getAvailableLocales(layout.getGroupId())) {

			String html = null;

			try {
				html = _layoutPreviewRenderer.render(
					layout, locale,
					segmentsExperience.getSegmentsExperienceId(),
					serviceContext);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to render layout content version preview ",
							"for language ID ", LocaleUtil.toLanguageId(locale),
							", PLID ", layout.getPlid(),
							", and segments experience ",
							segmentsExperience.getSegmentsExperienceId()),
						exception);
				}

				html = _getPreviewErrorHTML(locale);
			}

			try {
				_layoutContentVersionPreviewLocalService.
					addLayoutContentVersionPreview(
						userId,
						layoutContentVersion.getLayoutContentVersionId(), html,
						LocaleUtil.toLanguageId(locale),
						segmentsExperience.getExternalReferenceCode());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to persist layout content version preview ",
							"for language ID ", LocaleUtil.toLanguageId(locale),
							", PLID ", layout.getPlid(),
							", and segments experience ",
							segmentsExperience.getSegmentsExperienceId()),
						exception);
				}
			}
		}
	}

	private int _getNextVersion(long plid) {
		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.fetchByPlid_First(
				plid, LayoutContentVersionVersionComparator.getInstance(false));

		if (layoutContentVersion == null) {
			return 1;
		}

		return layoutContentVersion.getVersion() + 1;
	}

	private String _getPreviewErrorHTML(Locale locale) {
		return StringUtil.replace(
			_PREVIEW_ERROR_HTML, new String[] {"[$MESSAGE$]", "[$TITLE$]"},
			new String[] {
				_language.get(
					locale,
					"this-preview-is-not-available.-an-error-occurred-while-" +
						"generating-the-preview-when-this-version-was-created"),
				_language.get(locale, "error")
			});
	}

	private void _initThemeDisplay(
			HttpServletRequest httpServletRequest, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String pathImage = _portal.getPathImage();
		String pathMain = _portal.getPathMain();

		String companyLogo = pathImage + "/company_logo";

		Company company = themeDisplay.getCompany();

		long companyLogoId = company.getLogoId();

		if (companyLogoId > 0) {
			companyLogo = StringBundler.concat(
				companyLogo, "?img_id=", companyLogoId, "&t=",
				_webServerServletToken.getToken(companyLogoId));
		}

		String layoutSetLogo = null;

		LayoutSet layoutSet = layout.getLayoutSet();

		if (company.isSiteLogo() && layoutSet.isLogo()) {
			long layoutSetLogoId = layoutSet.getLogoId();

			if (layoutSetLogoId > 0) {
				layoutSetLogo = StringBundler.concat(
					pathImage, "/layout_set_logo?img_id=", layoutSetLogoId,
					"&t=", _webServerServletToken.getToken(layoutSetLogoId));

				companyLogo = layoutSetLogo;
			}
		}

		themeDisplay.setCompanyLogo(companyLogo);
		themeDisplay.setLayoutSetLogo(layoutSetLogo);
		themeDisplay.setLayouts(
			ListUtil.filter(
				_layoutService.getLayouts(
					layout.getGroupId(), layout.isPrivateLayout(),
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID),
				curLayout -> !curLayout.isHidden() && curLayout.isPublished()));
		themeDisplay.setPathContext(_portal.getPathContext());
		themeDisplay.setPathImage(pathImage);
		themeDisplay.setPathMain(pathMain);
		themeDisplay.setRealCompanyLogo(companyLogo);
		themeDisplay.setURLSignIn(
			HttpComponentsUtil.addParameter(
				StringBundler.concat(
					themeDisplay.getPortalURL(), pathMain, "/portal/login"),
				"p_l_id", layout.getPlid()));
	}

	private void _validateExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		LayoutContentVersion layoutContentVersion =
			layoutContentVersionPersistence.fetchByERC_G(
				externalReferenceCode, groupId);

		if (layoutContentVersion != null) {
			throw new DuplicateLayoutContentVersionExternalReferenceCodeException(
				StringBundler.concat(
					"Duplicate layout content version external reference code ",
					externalReferenceCode, " in group ", groupId));
		}

		int maxLength = ModelHintsUtil.getMaxLength(
			LayoutContentVersion.class.getName(), "externalReferenceCode");

		if (externalReferenceCode.length() > maxLength) {
			throw new LayoutContentVersionExternalReferenceCodeException(
				"External reference code must be less than " + maxLength +
					" characters");
		}
	}

	private void _validateLayout(Layout layout) throws PortalException {
		if (!layout.isDraftLayout() || layout.isTypeAssetDisplay() ||
			layout.isTypeUtility()) {

			throw new UnsupportedLayoutLayoutContentVersionException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			throw new UnsupportedLayoutLayoutContentVersionException();
		}
	}

	private static final String _PREVIEW_ERROR_HTML;

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutContentVersionLocalServiceImpl.class);

	static {
		_PREVIEW_ERROR_HTML = _read("preview_error.html");
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPreviewRenderer _layoutPreviewRenderer;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WebServerServletToken _webServerServletToken;

}