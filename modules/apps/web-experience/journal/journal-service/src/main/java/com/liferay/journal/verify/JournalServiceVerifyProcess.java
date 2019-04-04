/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.journal.verify;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.internal.verify.model.JournalArticleResourceVerifiableModel;
import com.liferay.journal.internal.verify.model.JournalArticleVerifiableModel;
import com.liferay.journal.internal.verify.model.JournalFeedVerifiableModel;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalArticleImage;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalContentSearch;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalArticleImageLocalService;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.service.JournalContentSearchLocalService;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.comparator.ArticleVersionComparator;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.upgrade.AutoBatchPreparedStatementUtil;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.verify.VerifyLayout;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.VerifyResourcePermissions;
import com.liferay.portal.verify.VerifyUUID;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 * @author Shinn Lok
 */
@Component(
	immediate = true,
	property = "verify.process.name=com.liferay.journal.service",
	service = {JournalServiceVerifyProcess.class, VerifyProcess.class}
)
public class JournalServiceVerifyProcess extends VerifyLayout {

	public static final long DEFAULT_GROUP_ID = 14;

	public static final int NUM_OF_ARTICLES = 5;

	@Override
	protected void doVerify() throws Exception {
		verifyArticleAssets();
		verifyArticleContents();
		verifyArticleExpirationDate();
		verifyArticleLayouts();
		verifyArticleStructures();
		verifyContentSearch();
		verifyFolderAssets();
		verifyOracleNewLine();
		verifyPermissions();
		verifyResourcedModels();
		verifyUUIDModels();

		verifyArticleImages();

		VerifyProcess verifyProcess =
			new JournalServiceSystemEventVerifyProcess(
				_journalArticleLocalService,
				_journalArticleResourceLocalService, _systemEventLocalService);

		verifyProcess.verify();
	}

	@Reference(unbind = "-")
	protected void setAssetEntryLocalService(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
	}

	/**
	 * @deprecated As of 3.10.0
	 */
	@Deprecated
	@Reference(unbind = "-")
	protected void setCompanyLocalService(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	/**
	 * @deprecated As of 3.10.0
	 */
	@Deprecated
	@Reference(unbind = "-")
	protected void setDDMStructureLocalService(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	@Reference(unbind = "-")
	protected void setDLAppLocalService(DLAppLocalService dlAppLocalService) {
		_dlAppLocalService = dlAppLocalService;
	}

	@Reference(unbind = "-")
	protected void setJournalArticleImageLocalService(
		JournalArticleImageLocalService journalArticleImageLocalService) {

		_journalArticleImageLocalService = journalArticleImageLocalService;
	}

	@Reference(unbind = "-")
	protected void setJournalArticleLocalService(
		JournalArticleLocalService journalArticleLocalService) {

		_journalArticleLocalService = journalArticleLocalService;
	}

	@Reference(unbind = "-")
	protected void setJournalArticleResourceLocalService(
		JournalArticleResourceLocalService journalArticleResourceLocalService) {

		_journalArticleResourceLocalService =
			journalArticleResourceLocalService;
	}

	@Reference(unbind = "-")
	protected void setJournalContentSearchLocalService(
		JournalContentSearchLocalService journalContentSearchLocalService) {

		_journalContentSearchLocalService = journalContentSearchLocalService;
	}

	/**
	 * @deprecated As of 3.10.0
	 */
	@Deprecated
	@Reference(unbind = "-")
	protected void setJournalConverter(JournalConverter journalConverter) {
		_journalConverter = journalConverter;
	}

	@Reference(unbind = "-")
	protected void setJournalFolderLocalService(
		JournalFolderLocalService journalFolderLocalService) {

		_journalFolderLocalService = journalFolderLocalService;
	}

	@Reference(unbind = "-")
	protected void setResourceLocalService(
		ResourceLocalService resourceLocalService) {

		_resourceLocalService = resourceLocalService;
	}

	@Reference(unbind = "-")
	protected void setSystemEventLocalService(
		SystemEventLocalService systemEventLocalService) {

		_systemEventLocalService = systemEventLocalService;
	}

	protected void updateContentSearch(long groupId, String portletId)
		throws Exception {

		try (PreparedStatement ps = connection.prepareStatement(
				"select preferences from PortletPreferences inner join " +
					"Layout on PortletPreferences.plid = Layout.plid where " +
						"groupId = ? and portletId = ?")) {

			ps.setLong(1, groupId);
			ps.setString(2, portletId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String xml = rs.getString("preferences");

					PortletPreferences portletPreferences =
						PortletPreferencesFactoryUtil.fromDefaultXML(xml);

					String articleId = portletPreferences.getValue(
						"articleId", null);

					List<JournalContentSearch> contentSearches =
						_journalContentSearchLocalService.
							getArticleContentSearches(groupId, articleId);

					if (contentSearches.isEmpty()) {
						continue;
					}

					JournalContentSearch contentSearch = contentSearches.get(0);

					try {
                        _journalContentSearchLocalService.updateContentSearch(
                                contentSearch.getGroupId(),
                                contentSearch.isPrivateLayout(),
                                contentSearch.getLayoutId(),
                                contentSearch.getPortletId(), articleId, true);
                    }
					catch(Exception ex) {
					    _log.error("updateContentSearch failed: " + ex.getMessage() +
                                " groupId: " + contentSearch.getGroupId() +
                                " isPrivateLayout: " + contentSearch.isPrivateLayout() +
                                " layoutId: " + contentSearch.getLayoutId() +
                                " portletId: " + contentSearch.getPortletId() +
                                " articleId: " + articleId);
					    throw ex;
                    }
				}
			}
		}
	}

	/**
	 * @deprecated As of 3.24.5, with no direct replacement
	 */
	@Deprecated
	protected void updateCreateAndModifiedDates() throws Exception {
		ActionableDynamicQuery actionableDynamicQuery =
			_journalArticleResourceLocalService.getActionableDynamicQuery();

		if (_log.isDebugEnabled()) {
			long count = actionableDynamicQuery.performCount();

			_log.debug(
				"Processing " + count +
					" article resources for create and modified dates");
		}

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.
				PerformActionMethod<JournalArticleResource>() {

				@Override
				public void performAction(
					JournalArticleResource articleResource) {

					updateCreateDate(articleResource);
					updateModifiedDate(articleResource);
				}

			});

		actionableDynamicQuery.performActions();

		if (_log.isDebugEnabled()) {
			_log.debug("Create and modified dates verified for articles");
		}
	}

	/**
	 * @deprecated As of 3.24.5, with no direct replacement
	 */
	@Deprecated
	protected void updateCreateDate(JournalArticleResource articleResource) {
		List<JournalArticle> articles = _journalArticleLocalService.getArticles(
			articleResource.getGroupId(), articleResource.getArticleId(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new ArticleVersionComparator(true));

		if (articles.size() <= 1) {
			return;
		}

		JournalArticle firstArticle = articles.get(0);

		Date createDate = firstArticle.getCreateDate();

		for (JournalArticle article : articles) {
			if (!createDate.equals(article.getCreateDate())) {
				article.setCreateDate(createDate);

				_journalArticleLocalService.updateJournalArticle(article);
			}
		}
	}

	protected void updateDocumentLibraryElements(Element element) {
		List<Element> dynamicContentElements = element.elements(
			"dynamic-content");

		for (Element dynamicContentElement : dynamicContentElements) {
			String path = dynamicContentElement.getStringValue();

			String[] pathArray = StringUtil.split(path, CharPool.SLASH);

			if (pathArray.length != 5) {
				return;
			}

			long groupId = GetterUtil.getLong(pathArray[2]);
			long folderId = GetterUtil.getLong(pathArray[3]);
			String title = _http.decodeURL(HtmlUtil.escape(pathArray[4]));

			try {
				FileEntry fileEntry = _dlAppLocalService.getFileEntry(
					groupId, folderId, title);

				Node node = dynamicContentElement.node(0);

				node.setText(path + StringPool.SLASH + fileEntry.getUuid());
			}
			catch (PortalException pe) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(pe, pe);
				}
			}
		}
	}

	/**
	 * @deprecated As of 3.10.0
	 */
	@Deprecated
	protected void updateDynamicElements(JournalArticle article)
		throws Exception {

		if (Validator.isNull(article.getDDMStructureKey())) {
			return;
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			article.getGroupId(), _portal.getClassNameId(JournalArticle.class),
			article.getDDMStructureKey(), true);

		Locale originalefaultLocale = LocaleThreadLocal.getDefaultLocale();
		Locale originalSiteDefaultLocale =
			LocaleThreadLocal.getSiteDefaultLocale();

		try {
			Company company = _companyLocalService.getCompany(
				article.getCompanyId());

			LocaleThreadLocal.setDefaultLocale(company.getLocale());

			LocaleThreadLocal.setSiteDefaultLocale(
				_portal.getSiteDefaultLocale(article.getGroupId()));

			Fields ddmFields = _journalConverter.getDDMFields(
				ddmStructure, article.getContent());

			String content = _journalConverter.getContent(
				ddmStructure, ddmFields);

			if (!content.equals(article.getContent())) {
				article.setContent(content);

				_journalArticleLocalService.updateJournalArticle(article);
			}
		}
		finally {
			LocaleThreadLocal.setDefaultLocale(originalefaultLocale);
			LocaleThreadLocal.setSiteDefaultLocale(originalSiteDefaultLocale);
		}
	}

	protected void updateElement(long groupId, Element element) {
		List<Element> dynamicElementElements = element.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			updateElement(groupId, dynamicElementElement);
		}

		String type = element.attributeValue("type");

		if (type.equals("document_library")) {
			updateDocumentLibraryElements(element);
		}
		else if (type.equals("link_to_layout")) {
			updateLinkToLayoutElements(groupId, element);
		}
	}

	/**
	 * @deprecated As of 3.14.0
	 */
	@Deprecated
	protected void updateExpirationDate(
			long groupId, long articleId, Timestamp expirationDate, int status)
		throws Exception {

		updateExpirationDate(
			groupId, String.valueOf(articleId), expirationDate, status);
	}

	protected void updateExpirationDate(
			long groupId, String articleId, Timestamp expirationDate,
			int status)
		throws Exception {

		try (PreparedStatement ps = connection.prepareStatement(
				"update JournalArticle set expirationDate = ? where groupId " +
					"= ? and articleId = ? and status = ?")) {

			ps.setTimestamp(1, expirationDate);
			ps.setLong(2, groupId);
			ps.setString(3, articleId);
			ps.setInt(4, status);

			ps.executeUpdate();
		}
	}

	protected void updateImageElement(Element element) {
		List<Element> dynamicElementElements = element.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			updateImageElement(dynamicElementElement);
		}

		String type = element.attributeValue("type");

		if (!type.equals("image")) {
			return;
		}

		String elName = element.attributeValue("name");

		Element dynamicContentElement = element.element("dynamic-content");

		long articleImageId = GetterUtil.getLong(
			dynamicContentElement.attributeValue("id"));

		JournalArticleImage articleImage =
			_journalArticleImageLocalService.fetchJournalArticleImage(
				articleImageId);

		if (articleImage == null) {
			return;
		}

		if (!elName.equals(articleImage.getElName())) {
			articleImage.setElName(elName);

			_journalArticleImageLocalService.updateJournalArticleImage(
				articleImage);
		}
	}

	protected void updateLinkToLayoutElements(long groupId, Element element) {
		Element dynamicContentElement = element.element("dynamic-content");

		Node node = dynamicContentElement.node(0);

		String text = node.getText();

		if (!text.isEmpty() && !text.endsWith(StringPool.AT + groupId)) {
			node.setText(
				dynamicContentElement.getStringValue() + StringPool.AT +
					groupId);
		}
	}

	/**
	 * @deprecated As of 3.24.5, with no direct replacement
	 */
	@Deprecated
	protected void updateModifiedDate(JournalArticleResource articleResource) {
		JournalArticle article = _journalArticleLocalService.fetchLatestArticle(
			articleResource.getResourcePrimKey(),
			WorkflowConstants.STATUS_APPROVED, true);

		if (article == null) {
			return;
		}

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			articleResource.getGroupId(), articleResource.getUuid());

		if (assetEntry == null) {
			return;
		}

		Date modifiedDate = article.getModifiedDate();

		if (modifiedDate.equals(assetEntry.getModifiedDate())) {
			return;
		}

		article.setModifiedDate(assetEntry.getModifiedDate());

		_journalArticleLocalService.updateJournalArticle(article);
	}

	protected void updateResourcePrimKey() throws PortalException {
		ActionableDynamicQuery actionableDynamicQuery =
			_journalArticleLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			new ActionableDynamicQuery.AddCriteriaMethod() {

				@Override
				public void addCriteria(DynamicQuery dynamicQuery) {
					Property resourcePrimKey = PropertyFactoryUtil.forName(
						"resourcePrimKey");

					dynamicQuery.add(resourcePrimKey.le(0L));
				}

			});

		if (_log.isDebugEnabled()) {
			long count = actionableDynamicQuery.performCount();

			_log.debug(
				"Processing " + count +
					" default article versions in draft mode");
		}

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<JournalArticle>() {

				@Override
				public void performAction(JournalArticle article)
					throws PortalException {

					long groupId = article.getGroupId();
					String articleId = article.getArticleId();
					double version = article.getVersion();

					_journalArticleLocalService.checkArticleResourcePrimKey(
						groupId, articleId, version);
				}

			});

		actionableDynamicQuery.performActions();
	}

	protected void verifyArticleAssets() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<JournalArticle> journalArticles =
				_journalArticleLocalService.getNoAssetArticles();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing " + journalArticles.size() +
						" articles with no asset");
			}

			for (JournalArticle journalArticle : journalArticles) {
				try {
					_journalArticleLocalService.updateAsset(
						journalArticle.getUserId(), journalArticle, null, null,
						null, null);
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to update asset for article ",
								String.valueOf(journalArticle.getId()), ": ",
								e.getMessage()));
					}
				}
			}

			ActionableDynamicQuery actionableDynamicQuery =
				_journalArticleLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				new ActionableDynamicQuery.AddCriteriaMethod() {

					@Override
					public void addCriteria(DynamicQuery dynamicQuery) {
						Property versionProperty = PropertyFactoryUtil.forName(
							"version");

						dynamicQuery.add(
							versionProperty.eq(
								JournalArticleConstants.VERSION_DEFAULT));

						Property statusProperty = PropertyFactoryUtil.forName(
							"status");

						dynamicQuery.add(
							statusProperty.eq(WorkflowConstants.STATUS_DRAFT));
					}

				});

			if (_log.isDebugEnabled()) {
				long count = actionableDynamicQuery.performCount();

				_log.debug(
					"Processing " + count +
						" default article versions in draft mode");
			}

			actionableDynamicQuery.setPerformActionMethod(
				new ActionableDynamicQuery.
					PerformActionMethod<JournalArticle>() {

					@Override
					public void performAction(JournalArticle article)
						throws PortalException {

						AssetEntry assetEntry =
							_assetEntryLocalService.fetchEntry(
								JournalArticle.class.getName(),
								article.getResourcePrimKey());

						boolean listable =
							_journalArticleLocalService.isListable(article);

						_assetEntryLocalService.updateEntry(
							assetEntry.getClassName(), assetEntry.getClassPK(),
							null, null, listable, assetEntry.isVisible());
					}

				});

			actionableDynamicQuery.performActions();

			if (_log.isDebugEnabled()) {
				_log.debug("Assets verified for articles");
			}

			updateResourcePrimKey();
		}
	}

	protected void verifyArticleContents() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement ps = connection.prepareStatement(
				"select id_ from JournalArticle where (content like " +
					"'%document_library%' or content like " +
						"'%link_to_layout%') and DDMStructureKey != ''");
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				long id = rs.getLong("id_");

				JournalArticle article = _journalArticleLocalService.getArticle(
					id);

				try {
					Document document = SAXReaderUtil.read(
						article.getContent());

					Element rootElement = document.getRootElement();

					for (Element element : rootElement.elements()) {
						updateElement(article.getGroupId(), element);
					}

					article.setContent(document.asXML());

					_journalArticleLocalService.updateJournalArticle(article);
				}
				catch (Exception e) {
					_log.error(
						"Unable to update content for article " +
							article.getId(),
						e);
				}
			}
		}
	}

	protected void verifyArticleExpirationDate() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			long companyId = CompanyThreadLocal.getCompanyId();

			JournalServiceConfiguration journalServiceConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					JournalServiceConfiguration.class, companyId);

			if (!journalServiceConfiguration.
					expireAllArticleVersionsEnabled()) {

				return;
			}

			StringBundler sb = new StringBundler(15);

			sb.append("select JournalArticle.* from JournalArticle left join ");
			sb.append("JournalArticle tempJournalArticle on ");
			sb.append("(JournalArticle.groupId = tempJournalArticle.groupId) ");
			sb.append("and (JournalArticle.articleId = ");
			sb.append("tempJournalArticle.articleId) and ");
			sb.append("(JournalArticle.version < tempJournalArticle.version) ");
			sb.append("and (JournalArticle.status = ");
			sb.append("tempJournalArticle.status) where ");
			sb.append("(JournalArticle.classNameId = ");
			sb.append(JournalArticleConstants.CLASSNAME_ID_DEFAULT);
			sb.append(") and (tempJournalArticle.version is null) and ");
			sb.append("(JournalArticle.expirationDate is not null) and ");
			sb.append("(JournalArticle.status = ");
			sb.append(WorkflowConstants.STATUS_APPROVED);
			sb.append(")");

			try (PreparedStatement ps = connection.prepareStatement(
					sb.toString());
				ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					long groupId = rs.getLong("groupId");
					String articleId = rs.getString("articleId");
					Timestamp expirationDate = rs.getTimestamp(
						"expirationDate");
					int status = rs.getInt("status");

					updateExpirationDate(
						groupId, articleId, expirationDate, status);
				}
			}
		}
	}

	protected void verifyArticleImages() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement ps = connection.prepareStatement(
				"select id_ from JournalArticle where (content like " +
					"'%type=\"image\"%') and DDMStructureKey != ''");
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				long id = rs.getLong("id_");

				JournalArticle article = _journalArticleLocalService.getArticle(
					id);

				try {
					Document document = SAXReaderUtil.read(
						article.getContent());

					Element rootElement = document.getRootElement();

					for (Element element : rootElement.elements()) {
						updateImageElement(element);
					}
				}
				catch (Exception e) {
					_log.error(
						"Unable to update images for article " +
							article.getId(),
						e);
				}
			}
		}
	}

	protected void verifyArticleLayouts() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			verifyUuid("JournalArticle");
		}
	}

	protected void verifyArticleStructures() throws PortalException {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			ActionableDynamicQuery actionableDynamicQuery =
				_journalArticleLocalService.getActionableDynamicQuery();

			if (_log.isDebugEnabled()) {
				long count = actionableDynamicQuery.performCount();

				_log.debug(
					StringBundler.concat(
						"Processing ", String.valueOf(count),
						" articles for invalid structures and dynamic ",
						"elements"));
			}

			actionableDynamicQuery.setPerformActionMethod(
				new ActionableDynamicQuery.
					PerformActionMethod<JournalArticle>() {

					@Override
					public void performAction(JournalArticle article) {
						try {
							_journalArticleLocalService.checkStructure(
								article.getGroupId(), article.getArticleId(),
								article.getVersion());
						}
						catch (NoSuchStructureException nsse) {
							if (_log.isWarnEnabled()) {
								_log.warn(
									"Removing reference to missing structure " +
										"for article " + article.getId());
							}

							article.setDDMStructureKey(StringPool.BLANK);
							article.setDDMTemplateKey(StringPool.BLANK);

							_journalArticleLocalService.updateJournalArticle(
								article);
						}
						catch (Exception e) {
							_log.error(
								"Unable to check the structure for article " +
									article.getId(),
								e);
						}
					}

				});

			actionableDynamicQuery.performActions();
		}
	}

	protected void verifyContentSearch() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement ps = connection.prepareStatement(
				"select groupId, portletId from JournalContentSearch group " +
					"by groupId, portletId having count(groupId) > 1 and " +
						"count(portletId) > 1");
			ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				long groupId = rs.getLong("groupId");
				String portletId = rs.getString("portletId");

				updateContentSearch(groupId, portletId);
			}
		}
	}

	protected void verifyFolderAssets() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<JournalFolder> folders =
				_journalFolderLocalService.getNoAssetFolders();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing " + folders.size() + " folders with no asset");
			}

			for (JournalFolder folder : folders) {
				try {
					_journalFolderLocalService.updateAsset(
						folder.getUserId(), folder, null, null, null, null);
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to update asset for folder ",
								String.valueOf(folder.getFolderId()), ": ",
								e.getMessage()));
					}
				}
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Assets verified for folders");
			}
		}
	}

	protected void verifyOracleNewLine() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			DB db = DBManagerUtil.getDB();

			if (db.getDBType() != DBType.ORACLE) {
				return;
			}

			// This is a workaround for a limitation in Oracle sqlldr's
			// inability insert new line characters for long varchar columns.
			// See http://forums.liferay.com/index.php?showtopic=2761&hl=oracle
			// for more information. Check several articles because some
			// articles may not have new lines.

			boolean checkNewLine = false;

			List<JournalArticle> articles =
				_journalArticleLocalService.getArticles(
					DEFAULT_GROUP_ID, 0, NUM_OF_ARTICLES);

			for (JournalArticle article : articles) {
				String content = article.getContent();

				if ((content != null) && content.contains("\\n")) {
					articles = _journalArticleLocalService.getArticles(
						DEFAULT_GROUP_ID);

					for (int j = 0; j < articles.size(); j++) {
						article = articles.get(j);

						_journalArticleLocalService.checkNewLine(
							article.getGroupId(), article.getArticleId(),
							article.getVersion());
					}

					checkNewLine = true;

					break;
				}
			}

			// Only process this once

			if (!checkNewLine) {
				if (_log.isInfoEnabled()) {
					_log.info("Do not fix oracle new line");
				}

				return;
			}
			else {
				if (_log.isInfoEnabled()) {
					_log.info("Fix oracle new line");
				}
			}
		}
	}

	protected void verifyPermissions() throws PortalException {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			List<JournalArticle> articles =
				_journalArticleLocalService.getNoPermissionArticles();

			for (JournalArticle article : articles) {
				_resourceLocalService.addResources(
					article.getCompanyId(), 0, 0,
					JournalArticle.class.getName(),
					article.getResourcePrimKey(), false, false, false);
			}
		}
	}

	protected void verifyResourcedModels() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_verifyResourcePermissions.verify(
				new JournalArticleVerifiableModel());
			_verifyResourcePermissions.verify(new JournalFeedVerifiableModel());
		}
	}

	/**
	 * @deprecated As of 3.24.4
	 */
	@Deprecated
	protected void verifyTree() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			long[] companyIds = PortalInstances.getCompanyIdsBySQL();

			for (long companyId : companyIds) {
				_journalFolderLocalService.rebuildTree(companyId);
			}
		}
	}

	/**
	 * @deprecated As of 3.24.5
	 */
	@Deprecated
	protected void verifyURLTitle() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();
			PreparedStatement ps1 = connection.prepareStatement(
				"select distinct groupId, articleId, urlTitle from " +
					"JournalArticle");
			ResultSet rs = ps1.executeQuery()) {

			try (PreparedStatement ps2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection.prepareStatement(
							"update JournalArticle set urlTitle = ? where " +
								"urlTitle = ?"))) {

				while (rs.next()) {
					long groupId = rs.getLong("groupId");
					String articleId = rs.getString("articleId");
					String urlTitle = GetterUtil.getString(
						rs.getString("urlTitle"));

					String normalizedURLTitle =
						FriendlyURLNormalizerUtil.
							normalizeWithPeriodsAndSlashes(urlTitle);

					if (urlTitle.equals(normalizedURLTitle)) {
						return;
					}

					normalizedURLTitle =
						_journalArticleLocalService.getUniqueUrlTitle(
							groupId, articleId, normalizedURLTitle);

					ps2.setString(1, normalizedURLTitle);

					ps2.setString(2, urlTitle);

					ps2.addBatch();
				}

				ps2.executeBatch();
			}
		}
	}

	protected void verifyUUIDModels() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			VerifyUUID.verify(new JournalArticleResourceVerifiableModel());
			VerifyUUID.verify(new JournalFeedVerifiableModel());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalServiceVerifyProcess.class);

	private AssetEntryLocalService _assetEntryLocalService;
	private CompanyLocalService _companyLocalService;
	private DDMStructureLocalService _ddmStructureLocalService;
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Http _http;

	private JournalArticleImageLocalService _journalArticleImageLocalService;
	private JournalArticleLocalService _journalArticleLocalService;
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;
	private JournalContentSearchLocalService _journalContentSearchLocalService;
	private JournalConverter _journalConverter;
	private JournalFolderLocalService _journalFolderLocalService;

	@Reference
	private Portal _portal;

	private ResourceLocalService _resourceLocalService;
	private SystemEventLocalService _systemEventLocalService;
	private final VerifyResourcePermissions _verifyResourcePermissions =
		new VerifyResourcePermissions();

}