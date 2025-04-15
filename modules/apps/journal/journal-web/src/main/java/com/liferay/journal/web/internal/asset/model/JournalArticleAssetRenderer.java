/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.asset.kernel.model.DDMFormValuesReader;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.util.JournalContent;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.web.internal.asset.JournalArticleDDMFormValuesReader;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalServiceUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Sergio González
 * @author Raymond Augé
 */
public class JournalArticleAssetRenderer
	extends BaseJSPAssetRenderer<JournalArticle> implements TrashRenderer {

	public static final String TYPE = "journal_article";

	public static long getClassPK(JournalArticle article) {
		if ((article.isDraft() || article.isPending()) &&
			(article.getVersion() != JournalArticleConstants.VERSION_DEFAULT)) {

			return article.getPrimaryKey();
		}

		return article.getResourcePrimKey();
	}

	public JournalArticleAssetRenderer(
		JournalArticle article, HtmlParser htmlParser,
		JournalHelper journalHelper) {

		_article = article;
		_htmlParser = htmlParser;
		_journalHelper = journalHelper;
	}

	public JournalArticle getArticle() {
		return _article;
	}

	@Override
	public JournalArticle getAssetObject() {
		return _article;
	}

	@Override
	public JournalArticle getAssetObject(long versionClassPK) {
		return JournalArticleLocalServiceUtil.fetchArticle(versionClassPK);
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return _article.getAvailableLanguageIds();
	}

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public long getClassPK() {
		return getClassPK(_article);
	}

	@Override
	public DDMFormValuesReader getDDMFormValuesReader() {
		return new JournalArticleDDMFormValuesReader(_article);
	}

	@Override
	public String getDefaultLanguageId() throws Exception {
		return _article.getDefaultLanguageId();
	}

	@Override
	public String getDiscussionPath() {
		if (_journalServiceConfiguration == null) {
			try {
				_journalServiceConfiguration =
					ConfigurationProviderUtil.getCompanyConfiguration(
						JournalServiceConfiguration.class,
						_article.getCompanyId());
			}
			catch (Exception exception) {
				_log.error(exception);

				return null;
			}
		}

		if (_journalServiceConfiguration.articleCommentsEnabled()) {
			return "edit_article_discussion";
		}

		return null;
	}

	@Override
	public long getGroupId() {
		return _article.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (_article.isInTrash() && template.equals(TEMPLATE_FULL_CONTENT)) {
			return "/trash/" + template + ".jsp";
		}

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		return JournalPortletKeys.JOURNAL;
	}

	@Override
	public int getStatus() {
		return _article.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String summary = _article.getDescription(getLocale(portletRequest));

		if (Validator.isNotNull(summary)) {
			return _htmlParser.render(HtmlUtil.stripHtml(summary));
		}

		return summary;
	}

	@Override
	public String getThumbnailPath(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String thumbnailSrc = _article.getArticleImageURL(themeDisplay);

		if (Validator.isNotNull(thumbnailSrc)) {
			return thumbnailSrc;
		}

		return super.getThumbnailPath(portletRequest);
	}

	@Override
	public String getTitle(Locale locale) {
		return _article.getTitle(locale);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLEdit(HttpServletRequest httpServletRequest)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(_article.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group, JournalPortletKeys.JOURNAL, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setParameter(
			"articleId", _article.getArticleId()
		).setParameter(
			"groupId", _article.getGroupId()
		).setParameter(
			"version", _article.getVersion()
		).buildPortletURL();
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return getURLEdit(
			PortalUtil.getHttpServletRequest(liferayPortletRequest));
	}

	@Override
	public PortletURL getURLExport(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			liferayPortletRequest, JournalPortletKeys.JOURNAL,
			themeDisplay.getPlid(), PortletRequest.RESOURCE_PHASE);

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		liferayPortletURL.setParameter(
			"groupId", String.valueOf(_article.getGroupId()));
		liferayPortletURL.setParameter("articleId", _article.getArticleId());
		liferayPortletURL.setResourceID("/journal/export_article");

		return liferayPortletURL;
	}

	@Override
	public String getUrlTitle() {
		return _article.getUrlTitle();
	}

	@Override
	public String getUrlTitle(Locale locale) {
		try {
			return _article.getUrlTitle(locale);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return getUrlTitle();
	}

	@Override
	public PortletURL getURLViewDiffs(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		JournalArticle previousApprovedArticle =
			JournalArticleLocalServiceUtil.getPreviousApprovedArticle(_article);

		if ((previousApprovedArticle.getVersion() == _article.getVersion()) ||
			(_article.getVersion() ==
				JournalArticleConstants.VERSION_DEFAULT)) {

			return null;
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, JournalPortletKeys.JOURNAL,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/journal/compare_versions"
		).setParameter(
			"articleId", _article.getArticleId()
		).setParameter(
			"groupId", _article.getGroupId()
		).setParameter(
			"sourceVersion", previousApprovedArticle.getVersion()
		).setParameter(
			"targetVersion", _article.getVersion()
		).buildPortletURL();
	}

	@Override
	public String getURLViewInContext(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			String noSuchEntryRedirect)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return getURLViewInContext(themeDisplay, noSuchEntryRedirect);
	}

	@Override
	public String getURLViewInContext(
			ThemeDisplay themeDisplay, String noSuchEntryRedirect)
		throws PortalException {

		if (!_isShowDisplayPage(themeDisplay.getScopeGroupId(), _article)) {
			return _getHitLayoutURL(noSuchEntryRedirect, themeDisplay);
		}

		Layout layout = _article.getLayout();

		if (layout == null) {
			AssetRendererFactory<JournalArticle> assetRendererFactory =
				getAssetRendererFactory();

			AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
				getClassName(), getClassPK());

			layout = _getArticleLayout(
				assetEntry.getLayoutUuid(), assetEntry.getGroupId());
		}

		if (layout != null) {
			String friendlyURL = _journalHelper.createURLPattern(
				_article, themeDisplay.getLocale(), layout.isPrivateLayout(),
				JournalArticleConstants.CANONICAL_URL_SEPARATOR, themeDisplay);

			if (!_article.isApproved()) {
				friendlyURL = HttpComponentsUtil.addParameter(
					friendlyURL, "version", _article.getVersion());
			}

			return PortalUtil.addPreservedParameters(themeDisplay, friendlyURL);
		}

		if (_assetDisplayPageFriendlyURLProvider != null) {
			String friendlyURL =
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					new InfoItemReference(
						getClassName(),
						new ClassPKInfoItemIdentifier(
							_article.getResourcePrimKey())),
					themeDisplay);

			if (Validator.isNotNull(friendlyURL)) {
				if (!_article.isApproved()) {
					friendlyURL = HttpComponentsUtil.addParameter(
						friendlyURL, "version", _article.getVersion());
				}

				return friendlyURL;
			}
		}

		return noSuchEntryRedirect;
	}

	@Override
	public long getUserId() {
		return _article.getStatusByUserId();
	}

	@Override
	public String getUserName() {
		return _article.getStatusByUserName();
	}

	@Override
	public String getUuid() {
		return _article.getUuid();
	}

	@Override
	public String getViewInContextMessage() {
		return "view[action]";
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return JournalArticlePermission.contains(
			permissionChecker, _article, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return JournalArticlePermission.contains(
			permissionChecker, _article, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(WebKeys.JOURNAL_ARTICLE, _article);
		httpServletRequest.setAttribute(
			WebKeys.JOURNAL_ARTICLE_DISPLAY,
			_getArticleDisplay(httpServletRequest));

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isConvertible() {
		return true;
	}

	@Override
	public boolean isDisplayable() {
		Date date = new Date();

		Date displayDate = _article.getDisplayDate();

		if ((displayDate != null) && displayDate.after(date)) {
			return false;
		}

		Date expirationDate = _article.getExpirationDate();

		if ((expirationDate != null) && expirationDate.before(date)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isLocalizable() {
		return true;
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	public void setAssetDisplayPageFriendlyURLProvider(
		AssetDisplayPageFriendlyURLProvider
			assetDisplayPageFriendlyURLProvider) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
	}

	public void setJournalContent(JournalContent journalContent) {
		_journalContent = journalContent;
	}

	private JournalArticleDisplay _getArticleDisplay(
			HttpServletRequest httpServletRequest)
		throws Exception {

		boolean workflowAssetPreview = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(WebKeys.WORKFLOW_ASSET_PREVIEW));

		String ddmTemplateKey = (String)httpServletRequest.getAttribute(
			WebKeys.JOURNAL_TEMPLATE_ID);

		if (Validator.isNull(ddmTemplateKey)) {
			ddmTemplateKey = ParamUtil.getString(
				httpServletRequest, "ddmTemplateKey");
		}

		String viewMode = ParamUtil.getString(
			httpServletRequest, "viewMode", Constants.VIEW);

		String languageId = ParamUtil.getString(
			httpServletRequest, "languageId");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Validator.isNull(languageId) && (themeDisplay != null) &&
			Validator.isNotNull(themeDisplay.getLanguageId())) {

			languageId = themeDisplay.getLanguageId();
		}
		else {
			languageId = LanguageUtil.getLanguageId(httpServletRequest);
		}

		int articlePage = ParamUtil.getInteger(httpServletRequest, "page", 1);
		PortletRequestModel portletRequestModel = _getPortletRequestModel(
			httpServletRequest);

		if (!workflowAssetPreview && _article.isApproved()) {
			return _journalContent.getDisplay(
				_article.getGroupId(), _article.getArticleId(),
				_article.getVersion(), ddmTemplateKey, viewMode, languageId,
				articlePage, portletRequestModel, themeDisplay);
		}

		return JournalArticleLocalServiceUtil.getArticleDisplay(
			_article, ddmTemplateKey, viewMode, languageId, articlePage,
			portletRequestModel, themeDisplay);
	}

	private Layout _getArticleLayout(String layoutUuid, long groupId) {
		if (Validator.isNull(layoutUuid)) {
			return null;
		}

		Layout layout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
			layoutUuid, groupId, false);

		if (layout == null) {
			layout = LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
				layoutUuid, groupId, true);
		}

		return layout;
	}

	private String _getHitLayoutURL(
			String noSuchEntryRedirect, ThemeDisplay themeDisplay)
		throws PortalException {

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			LayoutClassedModelUsageLocalServiceUtil.getLayoutClassedModelUsages(
				PortalUtil.getClassNameId(JournalArticle.class),
				_article.getResourcePrimKey());

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			Layout layout = LayoutLocalServiceUtil.fetchLayout(
				layoutClassedModelUsage.getPlid());

			if ((layout != null) && !layout.isSystem() &&
				LayoutPermissionUtil.contains(
					themeDisplay.getPermissionChecker(), layout,
					ActionKeys.VIEW)) {

				return PortalUtil.getLayoutURL(layout, themeDisplay);
			}
		}

		return noSuchEntryRedirect;
	}

	private PortletRequestModel _getPortletRequestModel(
		HttpServletRequest httpServletRequest) {

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		if ((portletRequest == null) || (portletResponse == null)) {
			return null;
		}

		return new PortletRequestModel(portletRequest, portletResponse);
	}

	private boolean _isShowDisplayPage(long groupId, JournalArticle article)
		throws PortalException {

		AssetRendererFactory<JournalArticle> assetRendererFactory =
			getAssetRendererFactory();

		AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
			getClassName(), getClassPK());

		if (Validator.isNull(article.getLayoutUuid()) &&
			Validator.isNull(assetEntry.getLayoutUuid()) &&
			!AssetDisplayPageUtil.hasAssetDisplayPage(
				groupId,
				assetRendererFactory.getAssetEntry(
					JournalArticle.class.getName(),
					article.getResourcePrimKey()))) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleAssetRenderer.class);

	private final JournalArticle _article;
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final HtmlParser _htmlParser;
	private JournalContent _journalContent;
	private final JournalHelper _journalHelper;
	private JournalServiceConfiguration _journalServiceConfiguration;

}