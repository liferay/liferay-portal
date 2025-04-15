/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.util.JournalContent;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.index.IndexEncoder;
import com.liferay.portal.kernel.cache.index.PortalCacheIndexer;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Michael Young
 */
@Component(service = JournalContent.class)
public class JournalContentImpl implements JournalContent {

	@Override
	public void clearCache() {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		_portalCache.removeAll();
	}

	@Override
	public void clearCache(
		long groupId, String articleId, String ddmTemplateKey) {

		_clearCache(groupId, articleId, ddmTemplateKey);

		if (ClusterInvokeThreadLocal.isEnabled()) {
			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(
					new MethodHandler(
						_clearArticleCacheMethodKey, groupId, articleId,
						ddmTemplateKey),
					true);

			clusterRequest.setFireAndForget(true);

			ClusterExecutorUtil.execute(clusterRequest);
		}
	}

	@Override
	public void clearCache(String ddmTemplateKey) {
		_clearCache(ddmTemplateKey);

		if (ClusterInvokeThreadLocal.isEnabled()) {
			ClusterRequest clusterRequest =
				ClusterRequest.createMulticastRequest(
					new MethodHandler(
						_clearTemplateCacheMethodKey, ddmTemplateKey),
					true);

			clusterRequest.setFireAndForget(true);

			ClusterExecutorUtil.execute(clusterRequest);
		}
	}

	@Override
	public String getContent(
		long groupId, String articleId, String viewMode, String languageId) {

		return getContent(
			groupId, articleId, null, viewMode, languageId, null,
			_getDefaultThemeDisplay());
	}

	@Override
	public String getContent(
		long groupId, String articleId, String viewMode, String languageId,
		PortletRequestModel portletRequestModel) {

		return getContent(
			groupId, articleId, null, viewMode, languageId, portletRequestModel,
			_getDefaultThemeDisplay());
	}

	@Override
	public String getContent(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, PortletRequestModel portletRequestModel) {

		return getContent(
			groupId, articleId, ddmTemplateKey, viewMode, languageId,
			portletRequestModel, _getDefaultThemeDisplay());
	}

	@Override
	public String getContent(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay) {

		JournalArticleDisplay articleDisplay = getDisplay(
			groupId, articleId, ddmTemplateKey, viewMode, languageId, 1,
			portletRequestModel, themeDisplay);

		if (articleDisplay != null) {
			return articleDisplay.getContent();
		}

		return null;
	}

	@Override
	public String getContent(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, ThemeDisplay themeDisplay) {

		return getContent(
			groupId, articleId, ddmTemplateKey, viewMode, languageId,
			(PortletRequestModel)null, themeDisplay);
	}

	@Override
	public String getContent(
		long groupId, String articleId, String viewMode, String languageId,
		ThemeDisplay themeDisplay) {

		return getContent(
			groupId, articleId, null, viewMode, languageId, themeDisplay);
	}

	@Override
	public JournalArticleDisplay getDisplay(
		JournalArticle article, String ddmTemplateKey, String viewMode,
		String languageId, int page, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay) {

		StopWatch stopWatch = new StopWatch();

		stopWatch.start();

		long groupId = article.getGroupId();
		String articleId = article.getArticleId();
		double version = article.getVersion();

		articleId = StringUtil.toUpperCase(GetterUtil.getString(articleId));
		ddmTemplateKey = StringUtil.toUpperCase(
			GetterUtil.getString(ddmTemplateKey));

		long layoutSetId = 0;
		boolean lifecycleRender = false;
		boolean secure = false;

		if (portletRequestModel != null) {
			lifecycleRender = RenderRequest.RENDER_PHASE.equals(
				portletRequestModel.getLifecycle());
		}

		if (themeDisplay != null) {
			try {
				if (!_journalArticleModelResourcePermission.contains(
						themeDisplay.getPermissionChecker(), article,
						ActionKeys.VIEW)) {

					return null;
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			LayoutSet layoutSet = themeDisplay.getLayoutSet();

			layoutSetId = layoutSet.getLayoutSetId();

			if (portletRequestModel == null) {
				lifecycleRender = themeDisplay.isLifecycleRender();
			}

			secure = themeDisplay.isSecure();
		}

		if (Validator.isNull(ddmTemplateKey)) {
			ddmTemplateKey = article.getDDMTemplateKey();
		}

		JournalContentKey journalContentKey = new JournalContentKey(
			groupId, articleId, version, ddmTemplateKey, layoutSetId, viewMode,
			languageId, page, secure);

		JournalArticleDisplay articleDisplay = null;

		boolean productionMode = CTCollectionThreadLocal.isProductionMode();

		if (productionMode) {
			articleDisplay = _portalCache.get(journalContentKey);
		}

		if ((articleDisplay == null) || !lifecycleRender) {
			articleDisplay = getArticleDisplay(
				article, ddmTemplateKey, viewMode, languageId, page,
				portletRequestModel, themeDisplay);

			if ((articleDisplay != null) && articleDisplay.isCacheable() &&
				lifecycleRender) {

				try {
					if (productionMode) {
						_portalCache.put(journalContentKey, articleDisplay);
					}
				}
				catch (ClassCastException classCastException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to cache article display",
							classCastException);
					}
				}
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"getDisplay for {", groupId, ", ", articleId, ", ",
					ddmTemplateKey, ", ", viewMode, ", ", languageId, ", ",
					page, "} takes ", stopWatch.getTime(), " ms"));
		}

		return articleDisplay;
	}

	@Override
	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, double version, String ddmTemplateKey,
		String viewMode, String languageId, int page,
		PortletRequestModel portletRequestModel, ThemeDisplay themeDisplay) {

		try {
			return getDisplay(
				_journalArticleLocalService.getArticle(
					groupId, articleId, version),
				ddmTemplateKey, viewMode, languageId, page, portletRequestModel,
				themeDisplay);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get display for ", groupId, StringPool.BLANK,
						articleId, StringPool.BLANK, languageId),
					portalException);
			}

			return null;
		}
	}

	@Override
	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String viewMode, String languageId,
		int page, ThemeDisplay themeDisplay) {

		return getDisplay(
			groupId, articleId, null, viewMode, languageId, page,
			(PortletRequestModel)null, themeDisplay);
	}

	@Override
	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String viewMode, String languageId,
		PortletRequestModel portletRequestModel) {

		return getDisplay(
			groupId, articleId, null, viewMode, languageId, 1,
			portletRequestModel, _getDefaultThemeDisplay());
	}

	@Override
	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, int page, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay) {

		JournalArticle article = _journalArticleLocalService.fetchLatestArticle(
			groupId, articleId, WorkflowConstants.STATUS_APPROVED);

		return getDisplay(
			article, ddmTemplateKey, viewMode, languageId, 1,
			portletRequestModel, themeDisplay);
	}

	@Override
	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, PortletRequestModel portletRequestModel) {

		return getDisplay(
			groupId, articleId, ddmTemplateKey, viewMode, languageId, 1,
			portletRequestModel, _getDefaultThemeDisplay());
	}

	@Override
	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, ThemeDisplay themeDisplay) {

		return getDisplay(
			groupId, articleId, ddmTemplateKey, viewMode, languageId, 1,
			(PortletRequestModel)null, themeDisplay);
	}

	@Override
	public JournalArticleDisplay getDisplay(
		long groupId, String articleId, String viewMode, String languageId,
		ThemeDisplay themeDisplay) {

		return getDisplay(
			groupId, articleId, viewMode, languageId, 1, themeDisplay);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_portalCache =
			(PortalCache<JournalContentKey, JournalArticleDisplay>)
				_multiVMPool.getPortalCache(CACHE_NAME);

		_journalArticlePortalCacheIndexer = new PortalCacheIndexer<>(
			new JournalContentArticleKeyIndexEncoder(), _portalCache);
		_journalTemplatePortalCacheIndexer = new PortalCacheIndexer<>(
			new JournalContentTemplateKeyIndexEncoder(), _portalCache);
		_serviceRegistration = bundleContext.registerService(
			CTEventListener.class,
			new CTEventListener() {

				@Override
				public void onAfterPublish(long ctCollectionId) {
					_portalCache.removeAll();
				}

			},
			null);
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(CACHE_NAME);
		_serviceRegistration.unregister();
	}

	protected JournalArticleDisplay getArticleDisplay(
		JournalArticle article, String ddmTemplateKey, String viewMode,
		String languageId, int page, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay) {

		if (article.getStatus() != WorkflowConstants.STATUS_APPROVED) {
			return null;
		}

		Date date = new Date();

		Date displayDate = article.getDisplayDate();
		Date expirationDate = article.getExpirationDate();

		if (((displayDate != null) && displayDate.after(date)) ||
			((expirationDate != null) && expirationDate.before(date))) {

			return null;
		}

		try {
			return _journalArticleLocalService.getArticleDisplay(
				article, ddmTemplateKey, viewMode, languageId, page,
				portletRequestModel, themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get display for ", article, StringPool.SPACE,
						languageId),
					exception);
			}

			return null;
		}
	}

	protected JournalArticleDisplay getArticleDisplay(
		long groupId, String articleId, String ddmTemplateKey, String viewMode,
		String languageId, int page, PortletRequestModel portletRequestModel,
		ThemeDisplay themeDisplay) {

		try {
			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Get article display {", groupId, ", ", articleId, ", ",
						ddmTemplateKey, "}"));
			}

			return _journalArticleLocalService.getArticleDisplay(
				groupId, articleId, ddmTemplateKey, viewMode, languageId, page,
				portletRequestModel, themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get display for ", groupId, StringPool.SPACE,
						articleId, StringPool.SPACE, languageId),
					exception);
			}

			return null;
		}
	}

	protected static final String CACHE_NAME = JournalContent.class.getName();

	private static void _clearCache(
		long groupId, String articleId, String ddmTemplateKey) {

		_journalArticlePortalCacheIndexer.removeKeys(
			JournalContentArticleKeyIndexEncoder.encode(
				groupId, articleId, ddmTemplateKey));
	}

	private static void _clearCache(String ddmTemplateKey) {
		_journalTemplatePortalCacheIndexer.removeKeys(ddmTemplateKey);
	}

	private ThemeDisplay _getDefaultThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return null;
		}

		return serviceContext.getThemeDisplay();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalContentImpl.class);

	private static final MethodKey _clearArticleCacheMethodKey = new MethodKey(
		JournalContentImpl.class, "_clearCache", long.class, String.class,
		String.class);
	private static final MethodKey _clearTemplateCacheMethodKey = new MethodKey(
		JournalContentImpl.class, "_clearCache", String.class);
	private static PortalCacheIndexer
		<String, JournalContentKey, JournalArticleDisplay>
			_journalArticlePortalCacheIndexer;
	private static PortalCacheIndexer
		<String, JournalContentKey, JournalArticleDisplay>
			_journalTemplatePortalCacheIndexer;
	private static PortalCache<JournalContentKey, JournalArticleDisplay>
		_portalCache;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference
	private MultiVMPool _multiVMPool;

	private ServiceRegistration<CTEventListener> _serviceRegistration;

	private static class JournalContentArticleKeyIndexEncoder
		implements IndexEncoder<String, JournalContentKey> {

		public static String encode(
			long groupId, String articleId, String ddmTemplateKey) {

			return StringBundler.concat(
				groupId, StringPool.UNDERLINE, articleId, StringPool.UNDERLINE,
				ddmTemplateKey);
		}

		@Override
		public String encode(JournalContentKey journalContentKey) {
			return encode(
				journalContentKey._groupId, journalContentKey._articleId,
				journalContentKey._ddmTemplateKey);
		}

	}

	private static class JournalContentKey implements Serializable {

		@Override
		public boolean equals(Object object) {
			JournalContentKey journalContentKey = (JournalContentKey)object;

			if ((journalContentKey._groupId == _groupId) &&
				Objects.equals(journalContentKey._articleId, _articleId) &&
				(journalContentKey._version == _version) &&
				Objects.equals(
					journalContentKey._ddmTemplateKey, _ddmTemplateKey) &&
				(journalContentKey._layoutSetId == _layoutSetId) &&
				Objects.equals(journalContentKey._viewMode, _viewMode) &&
				Objects.equals(journalContentKey._languageId, _languageId) &&
				(journalContentKey._page == _page) &&
				(journalContentKey._secure == _secure)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _groupId);

			hashCode = HashUtil.hash(hashCode, _articleId);
			hashCode = HashUtil.hash(hashCode, _version);
			hashCode = HashUtil.hash(hashCode, _ddmTemplateKey);
			hashCode = HashUtil.hash(hashCode, _layoutSetId);
			hashCode = HashUtil.hash(hashCode, _viewMode);
			hashCode = HashUtil.hash(hashCode, _languageId);
			hashCode = HashUtil.hash(hashCode, _page);

			return HashUtil.hash(hashCode, _secure);
		}

		private JournalContentKey(
			long groupId, String articleId, double version,
			String ddmTemplateKey, long layoutSetId, String viewMode,
			String languageId, int page, boolean secure) {

			_groupId = groupId;
			_articleId = articleId;
			_version = version;
			_ddmTemplateKey = ddmTemplateKey;
			_layoutSetId = layoutSetId;
			_viewMode = viewMode;
			_languageId = languageId;
			_page = page;
			_secure = secure;
		}

		private static final long serialVersionUID = 1L;

		private final String _articleId;
		private final String _ddmTemplateKey;
		private final long _groupId;
		private final String _languageId;
		private final long _layoutSetId;
		private final int _page;
		private final boolean _secure;
		private final double _version;
		private final String _viewMode;

	}

	private static class JournalContentTemplateKeyIndexEncoder
		implements IndexEncoder<String, JournalContentKey> {

		@Override
		public String encode(JournalContentKey journalContentKey) {
			return journalContentKey._ddmTemplateKey;
		}

	}

}