/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.JournalArticleItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.journal.constants.JournalConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.journal.util.JournalHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.ServletContext;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Raymond Augé
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
	service = AssetRendererFactory.class
)
public class JournalArticleAssetRendererFactory
	extends BaseAssetRendererFactory<JournalArticle> {

	public static final String TYPE = "content";

	public JournalArticleAssetRendererFactory() {
		setClassName(JournalArticle.class.getName());
		setLinkable(true);
		setPortletId(JournalPortletKeys.JOURNAL);
		setSearchable(true);
		setSupportsClassTypes(true);
	}

	@Override
	public AssetEntry getAssetEntry(JournalArticle journalArticle)
		throws PortalException {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			getClassName(), journalArticle.getId());

		if (assetEntry != null) {
			return assetEntry;
		}

		JournalArticle latestJournalArticle =
			_journalArticleLocalService.fetchLatestArticle(
				journalArticle.getResourcePrimKey(),
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_IN_TRASH
				});

		if ((latestJournalArticle != null) &&
			!Objects.equals(
				journalArticle.getId(), latestJournalArticle.getId())) {

			return null;
		}

		return _assetEntryLocalService.fetchEntry(
			getClassName(), journalArticle.getResourcePrimKey());
	}

	@Override
	public AssetRenderer<JournalArticle> getAssetRenderer(
			JournalArticle journalArticle, int type)
		throws PortalException {

		JournalArticleAssetRenderer journalArticleAssetRenderer =
			_getJournalArticleAssetRenderer(journalArticle);

		journalArticleAssetRenderer.setAssetRendererType(type);

		return journalArticleAssetRenderer;
	}

	@Override
	public AssetRenderer<JournalArticle> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		JournalArticle article =
			_journalArticleLocalService.fetchJournalArticle(classPK);

		if (article == null) {
			JournalArticleResource articleResource =
				_journalArticleResourceLocalService.getArticleResource(classPK);

			if (type == TYPE_LATEST_APPROVED) {
				article = _journalArticleLocalService.fetchDisplayArticle(
					articleResource.getGroupId(),
					articleResource.getArticleId());
			}

			if (article == null) {
				article = _journalArticleLocalService.fetchLatestArticle(
					articleResource.getGroupId(),
					articleResource.getArticleId(),
					WorkflowConstants.STATUS_SCHEDULED);
			}

			if (article == null) {
				article = _journalArticleLocalService.fetchLatestArticle(
					articleResource.getGroupId(),
					articleResource.getArticleId(),
					WorkflowConstants.STATUS_EXPIRED);
			}

			if (article == null) {
				article = _journalArticleLocalService.fetchLatestArticle(
					articleResource.getGroupId(),
					articleResource.getArticleId(),
					WorkflowConstants.STATUS_ANY);
			}

			if ((article == null) && (type == TYPE_LATEST)) {
				article = _journalArticleLocalService.getLatestArticle(
					classPK, WorkflowConstants.STATUS_ANY);
			}

			if (article == null) {
				throw new NoSuchArticleException(
					"No JournalArticle exists with the key {resourcePrimKey=" +
						classPK + "}");
			}
		}

		JournalArticleAssetRenderer journalArticleAssetRenderer =
			_getJournalArticleAssetRenderer(article);

		journalArticleAssetRenderer.setAssetRendererType(type);

		return journalArticleAssetRenderer;
	}

	@Override
	public AssetRenderer<JournalArticle> getAssetRenderer(
			long groupId, String urlTitle)
		throws PortalException {

		JournalArticle article =
			_journalArticleLocalService.getArticleByUrlTitle(groupId, urlTitle);

		return _getJournalArticleAssetRenderer(article);
	}

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public ClassTypeReader getClassTypeReader() {
		return new JournalArticleClassTypeReader(getClassName());
	}

	@Override
	public String getIconCssClass() {
		return "web-content";
	}

	@Override
	public PortletURL getItemSelectorURL(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId,
		String eventName, Group group, boolean multiSelection,
		long refererAssetEntryId) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new JournalArticleItemSelectorReturnType());
		itemSelectorCriterion.setItemType(JournalArticle.class.getName());

		if (classTypeId > 0) {
			itemSelectorCriterion.setItemSubtype(String.valueOf(classTypeId));
		}

		itemSelectorCriterion.setMultiSelection(multiSelection);

		if (refererAssetEntryId > 0) {
			AssetEntry assetEntry = _assetEntryLocalService.fetchAssetEntry(
				refererAssetEntryId);

			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			Object assetObject = assetRenderer.getAssetObject();

			if (assetObject instanceof JournalArticle) {
				JournalArticle article = (JournalArticle)assetObject;

				itemSelectorCriterion.setRefererClassPK(
					article.getResourcePrimKey());
			}
		}

		itemSelectorCriterion.setStatus(WorkflowConstants.STATUS_ANY);

		return _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(liferayPortletRequest),
			group, themeDisplay.getScopeGroupId(), eventName,
			itemSelectorCriterion);
	}

	@Override
	public String getSubtypeTitle(Locale locale) {
		return _language.get(locale, "structures");
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String getTypeName(Locale locale, long subtypeId) {
		try {
			DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
				subtypeId);

			return ddmStructure.getName(locale);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return super.getTypeName(locale, subtypeId);
		}
	}

	@Override
	public PortletURL getURLAdd(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, long classTypeId) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest, getGroup(liferayPortletRequest),
				JournalPortletKeys.JOURNAL, 0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/journal/edit_article"
		).setParameter(
			"ddmStructureId",
			() -> {
				if (classTypeId > 0) {
					return classTypeId;
				}

				return null;
			}
		).buildPortletURL();
	}

	@Override
	public PortletURL getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		LiferayPortletURL liferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(
				JournalPortletKeys.JOURNAL, PortletRequest.RENDER_PHASE);

		try {
			liferayPortletURL.setWindowState(windowState);
		}
		catch (WindowStateException windowStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(windowStateException);
			}
		}

		return liferayPortletURL;
	}

	@Override
	public boolean hasAddPermission(
			PermissionChecker permissionChecker, long groupId, long classTypeId)
		throws Exception {

		if ((classTypeId == 0) ||
			!_ddmStructureModelResourcePermission.contains(
				permissionChecker, classTypeId, ActionKeys.VIEW)) {

			return false;
		}

		return _portletResourcePermission.contains(
			permissionChecker, groupId, ActionKeys.ADD_ARTICLE);
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws Exception {

		return _journalArticleModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private JournalArticleAssetRenderer _getJournalArticleAssetRenderer(
		JournalArticle article) {

		JournalArticleAssetRenderer journalArticleAssetRenderer =
			new JournalArticleAssetRenderer(
				article, _htmlParser, _journalHelper);

		journalArticleAssetRenderer.setAssetDisplayPageFriendlyURLProvider(
			_assetDisplayPageFriendlyURLProvider);
		journalArticleAssetRenderer.setJournalContent(_journalContent);
		journalArticleAssetRenderer.setServletContext(_servletContext);

		return journalArticleAssetRenderer;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleAssetRendererFactory.class);

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.dynamic.data.mapping.model.DDMStructure)"
	)
	private ModelResourcePermission<DDMStructure>
		_ddmStructureModelResourcePermission;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + JournalConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.journal.web)")
	private ServletContext _servletContext;

}