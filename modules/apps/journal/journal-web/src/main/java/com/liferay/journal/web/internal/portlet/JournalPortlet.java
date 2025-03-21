/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.exception.NoSuchTemplateException;
import com.liferay.dynamic.data.mapping.exception.StorageFieldRequiredException;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToMapConverter;
import com.liferay.dynamic.data.mapping.util.DDMTemplateHelper;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.exportimport.kernel.exception.ExportImportContentValidationException;
import com.liferay.item.selector.ItemSelector;
import com.liferay.journal.configuration.JournalFileUploadsConfiguration;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.constants.JournalWebKeys;
import com.liferay.journal.exception.ArticleContentException;
import com.liferay.journal.exception.ArticleContentSizeException;
import com.liferay.journal.exception.ArticleDisplayDateException;
import com.liferay.journal.exception.ArticleExpirationDateException;
import com.liferay.journal.exception.ArticleIdException;
import com.liferay.journal.exception.ArticleSmallImageNameException;
import com.liferay.journal.exception.ArticleSmallImageSizeException;
import com.liferay.journal.exception.ArticleTitleException;
import com.liferay.journal.exception.ArticleVersionException;
import com.liferay.journal.exception.DuplicateArticleIdException;
import com.liferay.journal.exception.DuplicateFeedIdException;
import com.liferay.journal.exception.DuplicateFolderNameException;
import com.liferay.journal.exception.FeedContentFieldException;
import com.liferay.journal.exception.FeedIdException;
import com.liferay.journal.exception.FeedNameException;
import com.liferay.journal.exception.FeedTargetLayoutFriendlyUrlException;
import com.liferay.journal.exception.FeedTargetPortletIdException;
import com.liferay.journal.exception.FolderNameException;
import com.liferay.journal.exception.InvalidDDMStructureException;
import com.liferay.journal.exception.InvalidFolderException;
import com.liferay.journal.exception.MaxAddMenuFavItemsException;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.exception.NoSuchFeedException;
import com.liferay.journal.exception.NoSuchFolderException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderService;
import com.liferay.journal.util.JournalContent;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.web.internal.configuration.JournalWebConfiguration;
import com.liferay.journal.web.internal.display.context.JournalDisplayContext;
import com.liferay.journal.web.internal.display.context.JournalEditDDMStructuresDisplayContext;
import com.liferay.journal.web.internal.display.context.JournalEditDDMTemplateDisplayContext;
import com.liferay.journal.web.internal.helper.JournalDDMTemplateHelper;
import com.liferay.journal.web.internal.portlet.action.ActionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.translation.security.permission.TranslationPermission;
import com.liferay.translation.url.provider.TranslationURLProvider;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.util.TrashWebKeys;

import jakarta.persistence.PersistenceException;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-journal",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/journal.png",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Web Content",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.mvc-action-command-package-prefix=com.liferay.journal.web.portlet.action",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class JournalPortlet extends MVCPortlet {

	public static final String VERSION_SEPARATOR = "_version_";

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			AssetDisplayPageFriendlyURLProvider.class.getName(),
			_assetDisplayPageFriendlyURLProvider);
		renderRequest.setAttribute(
			AssetVocabularyLocalService.class.getName(),
			_assetVocabularyLocalService);
		renderRequest.setAttribute(TrashWebKeys.TRASH_HELPER, _trashHelper);
		renderRequest.setAttribute(
			DDMFormValuesFactory.class.getName(), _ddmFormValuesFactory);
		renderRequest.setAttribute(
			DDMFormValuesToMapConverter.class.getName(),
			_ddmFormValuesToMapConverter);
		renderRequest.setAttribute(
			FieldsToDDMFormValuesConverter.class.getName(),
			_fieldsToDDMFormValuesConverter);
		renderRequest.setAttribute(ItemSelector.class.getName(), _itemSelector);
		renderRequest.setAttribute(
			JournalHelper.class.getName(), _journalHelper);
		renderRequest.setAttribute(
			JournalWebKeys.JOURNAL_CONTENT, _journalContent);
		renderRequest.setAttribute(
			JournalWebKeys.JOURNAL_CONVERTER, _journalConverter);
		renderRequest.setAttribute(
			SiteConnectedGroupGroupProvider.class.getName(),
			_siteConnectedGroupGroupProvider);
		renderRequest.setAttribute(
			TranslationPermission.class.getName(), _translationPermission);
		renderRequest.setAttribute(
			TranslationURLProvider.class.getName(), _translationURLProvider);

		try {
			renderRequest.setAttribute(
				DDMWebConfiguration.class.getName(),
				_configurationProvider.getSystemConfiguration(
					DDMWebConfiguration.class));
			renderRequest.setAttribute(
				JournalFileUploadsConfiguration.class.getName(),
				_configurationProvider.getSystemConfiguration(
					JournalFileUploadsConfiguration.class));

			JournalWebConfiguration journalWebConfiguration =
				_configurationProvider.getSystemConfiguration(
					JournalWebConfiguration.class);

			renderRequest.setAttribute(
				JournalWebConfiguration.class.getName(),
				journalWebConfiguration);

			renderRequest.setAttribute(
				JournalDisplayContext.class.getName(),
				JournalDisplayContext.create(
					_assetDisplayPageFriendlyURLProvider, _itemSelector,
					_journalHelper, journalWebConfiguration, renderRequest,
					renderResponse, _resourcePermissionLocalService,
					_roleLocalService, _trashHelper));

			String path = getPath(renderRequest, renderResponse);

			if (Objects.equals(path, "/data_engine/basic_info.jsp") ||
				Objects.equals(path, "/edit_data_definition.jsp")) {

				renderRequest.setAttribute(
					JournalEditDDMStructuresDisplayContext.class.getName(),
					new JournalEditDDMStructuresDisplayContext(
						_portal, renderRequest, renderResponse));
			}
			else if (Objects.equals(
						path, "/ddm_template/edit_properties.jsp") ||
					 Objects.equals(path, "/edit_ddm_template.jsp")) {

				renderRequest.setAttribute(
					JournalEditDDMTemplateDisplayContext.class.getName(),
					new JournalEditDDMTemplateDisplayContext(
						_ddmTemplateHelper, _journalDDMTemplateHelper, _portal,
						renderRequest, renderResponse));
			}
		}
		catch (ConfigurationException configurationException) {
			throw new PortletException(configurationException);
		}

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		JournalWebConfiguration journalWebConfiguration = null;

		try {
			journalWebConfiguration =
				_configurationProvider.getSystemConfiguration(
					JournalWebConfiguration.class);
		}
		catch (ConfigurationException configurationException) {
			throw new PortletException(configurationException);
		}

		resourceRequest.setAttribute(
			AssetDisplayPageFriendlyURLProvider.class.getName(),
			_assetDisplayPageFriendlyURLProvider);
		resourceRequest.setAttribute(
			DDMTemplateHelper.class.getName(), _ddmTemplateHelper);
		resourceRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);
		resourceRequest.setAttribute(
			JournalDisplayContext.class.getName(),
			JournalDisplayContext.create(
				_assetDisplayPageFriendlyURLProvider, _itemSelector,
				_journalHelper, journalWebConfiguration, resourceRequest,
				resourceResponse, _resourcePermissionLocalService,
				_roleLocalService, _trashHelper));
		resourceRequest.setAttribute(
			JournalHelper.class.getName(), _journalHelper);
		resourceRequest.setAttribute(
			JournalWebConfiguration.class.getName(), journalWebConfiguration);
		resourceRequest.setAttribute(
			TranslationPermission.class.getName(), _translationPermission);
		resourceRequest.setAttribute(
			TranslationURLProvider.class.getName(), _translationURLProvider);
		resourceRequest.setAttribute(TrashWebKeys.TRASH_HELPER, _trashHelper);

		super.serveResource(resourceRequest, resourceResponse);
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);

			String path = getPath(renderRequest, renderResponse);

			if (Objects.equals(path, "/edit_article.jsp") ||
				Objects.equals(path, "/view_article_history.jsp")) {

				ActionUtil.getArticle(httpServletRequest);
			}
			else if (Objects.equals(path, "/view_ddm_structures.jsp")) {
				CTTimelineUtil.setClassName(
					httpServletRequest, DDMStructure.class);
			}
			else if (Objects.equals(path, "/view_ddm_templates.jsp")) {
				CTTimelineUtil.setClassName(
					httpServletRequest, DDMTemplate.class);
			}
			else if (Validator.isNull(path)) {
				CTTimelineUtil.setClassName(
					httpServletRequest, JournalArticle.class);
			}
			else {
				_getFolder(httpServletRequest);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else {
				_log.error(exception);
			}

			SessionErrors.add(renderRequest, exception.getClass());
		}

		if (SessionErrors.contains(
				renderRequest, NoSuchArticleException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchFeedException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchFolderException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchStructureException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, NoSuchTemplateException.class.getName()) ||
			SessionErrors.contains(
				renderRequest, PrincipalException.getNestedClasses())) {

			include("/error.jsp", renderRequest, renderResponse);
		}
		else {
			super.doDispatch(renderRequest, renderResponse);
		}
	}

	@Override
	protected boolean isAlwaysSendRedirect() {
		return true;
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof ArticleContentException ||
			throwable instanceof ArticleContentSizeException ||
			throwable instanceof ArticleDisplayDateException ||
			throwable instanceof ArticleExpirationDateException ||
			throwable instanceof ArticleIdException ||
			throwable instanceof ArticleSmallImageNameException ||
			throwable instanceof ArticleSmallImageSizeException ||
			throwable instanceof ArticleTitleException ||
			throwable instanceof ArticleVersionException ||
			throwable instanceof AssetCategoryException ||
			throwable instanceof AssetTagException ||
			throwable instanceof DuplicateArticleIdException ||
			throwable instanceof DuplicateFeedIdException ||
			throwable instanceof DuplicateFileEntryException ||
			throwable instanceof DuplicateFolderNameException ||
			throwable instanceof ExportImportContentValidationException ||
			throwable instanceof FeedContentFieldException ||
			throwable instanceof FeedIdException ||
			throwable instanceof FeedNameException ||
			throwable instanceof FeedTargetLayoutFriendlyUrlException ||
			throwable instanceof FeedTargetPortletIdException ||
			throwable instanceof FileSizeException ||
			throwable instanceof FolderNameException ||
			throwable instanceof InvalidDDMStructureException ||
			throwable instanceof InvalidFolderException ||
			throwable instanceof LiferayFileItemException ||
			throwable instanceof LocaleException ||
			throwable instanceof MaxAddMenuFavItemsException ||
			throwable instanceof PersistenceException ||
			throwable instanceof StorageFieldRequiredException ||
			throwable instanceof SystemException ||
			super.isSessionErrorException(throwable)) {

			return true;
		}

		return false;
	}

	private void _getFolder(HttpServletRequest httpServletRequest)
		throws PortalException {

		long folderId = ParamUtil.getLong(httpServletRequest, "folderId");

		if (folderId > 0) {
			_journalFolderService.fetchFolder(folderId);

			CTTimelineUtil.setCTTimelineKeys(
				httpServletRequest, JournalFolder.class, folderId);
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_portletResourcePermission.check(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup(), ActionKeys.VIEW);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(JournalPortlet.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DDMFormValuesToMapConverter _ddmFormValuesToMapConverter;

	@Reference
	private DDMTemplateHelper _ddmTemplateHelper;

	@Reference
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private JournalDDMTemplateHelper _journalDDMTemplateHelper;

	@Reference
	private JournalFolderService _journalFolderService;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + JournalConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.journal.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

	@Reference
	private TranslationPermission _translationPermission;

	@Reference
	private TranslationURLProvider _translationURLProvider;

	@Reference
	private TrashHelper _trashHelper;

}