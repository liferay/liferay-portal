/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.journal.constants.JournalFeedConstants;
import com.liferay.journal.exception.DuplicateFeedIdException;
import com.liferay.journal.exception.FeedContentFieldException;
import com.liferay.journal.exception.FeedIdException;
import com.liferay.journal.exception.FeedNameException;
import com.liferay.journal.exception.FeedTargetLayoutFriendlyUrlException;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.model.JournalFeedTable;
import com.liferay.journal.service.base.JournalFeedLocalServiceBaseImpl;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.rss.util.RSSUtil;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalFeed",
	service = AopService.class
)
public class JournalFeedLocalServiceImpl
	extends JournalFeedLocalServiceBaseImpl {

	@Override
	public JournalFeed addFeed(
			long userId, long groupId, String feedId, boolean autoFeedId,
			String name, String description, long ddmStructureId,
			String ddmTemplateKey, String ddmRendererTemplateKey, int delta,
			String orderByCol, String orderByType,
			String targetLayoutFriendlyUrl, String targetPortletId,
			String contentField, String feedFormat, double feedVersion,
			ServiceContext serviceContext)
		throws PortalException {

		// Feed

		User user = _userLocalService.getUser(userId);
		feedId = StringUtil.toUpperCase(StringUtil.trim(feedId));

		DDMStructure ddmStructure = null;

		if (ddmStructureId > 0) {
			ddmStructure = _ddmStructureLocalService.fetchStructure(
				ddmStructureId);
		}

		_validate(
			user.getCompanyId(), groupId, feedId, autoFeedId, name,
			ddmStructure, targetLayoutFriendlyUrl, contentField);

		if (autoFeedId) {
			feedId = String.valueOf(counterLocalService.increment());
		}

		long id = counterLocalService.increment();

		JournalFeed feed = journalFeedPersistence.create(id);

		feed.setUuid(serviceContext.getUuid());
		feed.setGroupId(groupId);
		feed.setCompanyId(user.getCompanyId());
		feed.setUserId(user.getUserId());
		feed.setUserName(user.getFullName());
		feed.setFeedId(feedId);
		feed.setName(name);
		feed.setDescription(description);

		if (ddmStructure != null) {
			feed.setDDMStructureId(ddmStructure.getStructureId());
			feed.setDDMTemplateKey(ddmTemplateKey);
			feed.setDDMRendererTemplateKey(ddmRendererTemplateKey);
		}

		feed.setDelta(delta);
		feed.setOrderByCol(orderByCol);
		feed.setOrderByType(orderByType);
		feed.setTargetLayoutFriendlyUrl(targetLayoutFriendlyUrl);
		feed.setTargetPortletId(targetPortletId);
		feed.setContentField(contentField);

		if (Validator.isNull(feedFormat)) {
			feed.setFeedFormat(RSSUtil.FORMAT_DEFAULT);
			feed.setFeedVersion(RSSUtil.VERSION_DEFAULT);
		}
		else {
			feed.setFeedFormat(feedFormat);
			feed.setFeedVersion(feedVersion);
		}

		feed.setExpandoBridgeAttributes(serviceContext);

		feed = journalFeedPersistence.update(feed);

		// Asset

		_updateAssetEntry(
			userId, feed, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		if (ddmStructure != null) {
			_ddmStructureLinkLocalService.addStructureLink(
				_classNameLocalService.getClassNameId(JournalFeed.class),
				feed.getPrimaryKey(), ddmStructure.getStructureId());
		}

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addFeedResources(
				feed, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addFeedResources(feed, serviceContext.getModelPermissions());
		}

		return feed;
	}

	@Override
	public void addFeedResources(
			JournalFeed feed, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			feed.getCompanyId(), feed.getGroupId(), feed.getUserId(),
			JournalFeed.class.getName(), feed.getId(), false,
			addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addFeedResources(
			JournalFeed feed, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			feed.getCompanyId(), feed.getGroupId(), feed.getUserId(),
			JournalFeed.class.getName(), feed.getId(), modelPermissions);
	}

	@Override
	public void addFeedResources(
			long feedId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		JournalFeed feed = journalFeedPersistence.findByPrimaryKey(feedId);

		addFeedResources(feed, addGroupPermissions, addGuestPermissions);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteFeed(JournalFeed feed) throws PortalException {

		// Feed

		journalFeedPersistence.remove(feed);

		// Resources

		_resourceLocalService.deleteResource(
			feed.getCompanyId(), JournalFeed.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, feed.getId());

		// Asset

		_assetEntryLocalService.deleteEntry(
			JournalFeed.class.getName(), feed.getId());

		// DDM Structure Link

		if (feed.getDDMStructureId() > 0) {
			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(
					feed.getDDMStructureId());

			if (ddmStructure != null) {
				_ddmStructureLinkLocalService.deleteStructureLink(
					_classNameLocalService.getClassNameId(JournalFeed.class),
					feed.getPrimaryKey(), ddmStructure.getStructureId());
			}
		}

		// Expando

		_expandoValueLocalService.deleteValues(
			JournalFeed.class.getName(), feed.getId());
	}

	@Override
	public void deleteFeed(long feedId) throws PortalException {
		JournalFeed feed = journalFeedPersistence.findByPrimaryKey(feedId);

		journalFeedLocalService.deleteFeed(feed);
	}

	@Override
	public void deleteFeed(long groupId, String feedId) throws PortalException {
		JournalFeed feed = journalFeedPersistence.findByG_F(groupId, feedId);

		journalFeedLocalService.deleteFeed(feed);
	}

	@Override
	public JournalFeed fetchFeed(long groupId, String feedId) {
		return journalFeedPersistence.fetchByG_F(groupId, feedId);
	}

	@Override
	public JournalFeed getFeed(long feedId) throws PortalException {
		return journalFeedPersistence.findByPrimaryKey(feedId);
	}

	@Override
	public JournalFeed getFeed(long groupId, String feedId)
		throws PortalException {

		return journalFeedPersistence.findByG_F(groupId, feedId);
	}

	@Override
	public List<JournalFeed> getFeeds() {
		return journalFeedPersistence.findAll();
	}

	@Override
	public List<JournalFeed> search(
		long companyId, long groupId, String keywords, int start, int end,
		OrderByComparator<JournalFeed> orderByComparator) {

		return journalFeedPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				JournalFeedTable.INSTANCE
			).from(
				JournalFeedTable.INSTANCE
			).where(
				_getWherePredicate(companyId, groupId, keywords)
			).orderBy(
				JournalFeedTable.INSTANCE, orderByComparator
			).limit(
				start, end
			));
	}

	@Override
	public int searchCount(long companyId, long groupId, String keywords) {
		return journalFeedPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				JournalFeedTable.INSTANCE
			).where(
				_getWherePredicate(companyId, groupId, keywords)
			));
	}

	@Override
	public JournalFeed updateFeed(
			long groupId, String feedId, String name, String description,
			long ddmStructureId, String ddmTemplateKey,
			String ddmRendererTemplateKey, int delta, String orderByCol,
			String orderByType, String targetLayoutFriendlyUrl,
			String targetPortletId, String contentField, String feedFormat,
			double feedVersion, ServiceContext serviceContext)
		throws PortalException {

		// Feed

		JournalFeed feed = journalFeedPersistence.findByG_F(groupId, feedId);

		DDMStructure ddmStructure = null;

		if (ddmStructureId > 0) {
			ddmStructure = _ddmStructureLocalService.fetchStructure(
				ddmStructureId);
		}

		_validate(
			feed.getCompanyId(), name, ddmStructure, targetLayoutFriendlyUrl,
			contentField);

		feed.setName(name);
		feed.setDescription(description);

		if (ddmStructure != null) {
			feed.setDDMStructureId(ddmStructure.getStructureId());
			feed.setDDMTemplateKey(ddmTemplateKey);
			feed.setDDMRendererTemplateKey(ddmRendererTemplateKey);
		}
		else {
			feed.setDDMStructureId(0);
			feed.setDDMTemplateKey(null);
			feed.setDDMRendererTemplateKey(null);
		}

		feed.setDelta(delta);
		feed.setOrderByCol(orderByCol);
		feed.setOrderByType(orderByType);
		feed.setTargetLayoutFriendlyUrl(targetLayoutFriendlyUrl);
		feed.setTargetPortletId(targetPortletId);
		feed.setContentField(contentField);

		if (Validator.isNull(feedFormat)) {
			feed.setFeedFormat(RSSUtil.FORMAT_DEFAULT);
			feed.setFeedVersion(RSSUtil.VERSION_DEFAULT);
		}
		else {
			feed.setFeedFormat(feedFormat);
			feed.setFeedVersion(feedVersion);
		}

		feed.setExpandoBridgeAttributes(serviceContext);

		feed = journalFeedPersistence.update(feed);

		// Asset

		_updateAssetEntry(
			serviceContext.getUserId(), feed,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// DDM Structure Link

		long classNameId = _classNameLocalService.getClassNameId(
			JournalFeed.class);

		if (ddmStructure == null) {
			_ddmStructureLinkLocalService.deleteStructureLinks(
				classNameId, feed.getId());
		}
		else {
			int count = _ddmStructureLinkLocalService.getStructureLinksCount(
				classNameId, feed.getId());

			if (count == 0) {
				_ddmStructureLinkLocalService.addStructureLink(
					classNameId, feed.getId(), ddmStructure.getStructureId());
			}
			else {
				DDMStructureLink ddmStructureLink =
					_ddmStructureLinkLocalService.getUniqueStructureLink(
						classNameId, feed.getId());

				_ddmStructureLinkLocalService.updateStructureLink(
					ddmStructureLink.getStructureLinkId(), classNameId,
					feed.getId(), ddmStructure.getStructureId());
			}
		}

		return feed;
	}

	private Predicate _getWherePredicate(
		long companyId, long groupId, String keywords) {

		return JournalFeedTable.INSTANCE.companyId.eq(
			companyId
		).and(
			() -> {
				if (groupId <= 0) {
					return null;
				}

				return JournalFeedTable.INSTANCE.groupId.eq(groupId);
			}
		).and(
			() -> {
				if (Validator.isNull(keywords)) {
					return null;
				}

				String[] keywordsArray = _customSQL.keywords(keywords, false);
				String[] lowerCaseKeywordsArray = _customSQL.keywords(keywords);

				return Predicate.withParentheses(
					Predicate.withParentheses(
						_customSQL.getKeywordsPredicate(
							JournalFeedTable.INSTANCE.feedId, keywordsArray)
					).or(
						Predicate.withParentheses(
							_customSQL.getKeywordsPredicate(
								DSLFunctionFactoryUtil.lower(
									JournalFeedTable.INSTANCE.name),
								lowerCaseKeywordsArray))
					).or(
						Predicate.withParentheses(
							_customSQL.getKeywordsPredicate(
								DSLFunctionFactoryUtil.lower(
									JournalFeedTable.INSTANCE.description),
								lowerCaseKeywordsArray))
					));
			}
		);
	}

	private boolean _isValidStructureOptionValue(
		Map<String, DDMFormField> ddmFormFieldsMap, String contentField) {

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			String ddmFormFieldType = ddmFormField.getType();

			if (!(ddmFormFieldType.equals("radio") ||
				  ddmFormFieldType.equals("select"))) {

				continue;
			}

			DDMFormFieldOptions ddmFormFieldOptions =
				ddmFormField.getDDMFormFieldOptions();

			for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
				optionValue =
					ddmFormField.getName() + StringPool.UNDERLINE + optionValue;

				if (contentField.equals(optionValue)) {
					return true;
				}
			}
		}

		return false;
	}

	private void _updateAssetEntry(
			long userId, JournalFeed feed, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, feed.getGroupId(), feed.getCreateDate(),
			feed.getModifiedDate(), JournalFeed.class.getName(), feed.getId(),
			feed.getUuid(), 0, assetCategoryIds, assetTagNames, true, true,
			null, null, feed.getCreateDate(), null, ContentTypes.TEXT_PLAIN,
			feed.getName(), feed.getDescription(), null, null, null, 0, 0,
			priority);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	private void _validate(
			long companyId, long groupId, String feedId, boolean autoFeedId,
			String name, DDMStructure ddmStructure,
			String targetLayoutFriendlyUrl, String contentField)
		throws PortalException {

		if (!autoFeedId) {
			if (Validator.isNull(feedId) || Validator.isNumber(feedId) ||
				(feedId.indexOf(CharPool.COMMA) != -1) ||
				(feedId.indexOf(CharPool.SPACE) != -1)) {

				throw new FeedIdException("Invalid feedId: " + feedId);
			}

			JournalFeed feed = journalFeedPersistence.fetchByG_F(
				groupId, feedId);

			if (feed != null) {
				throw new DuplicateFeedIdException(
					StringBundler.concat(
						"{groupId=", groupId, ", feedId=", feedId, "}"));
			}
		}

		_validate(
			companyId, name, ddmStructure, targetLayoutFriendlyUrl,
			contentField);
	}

	private void _validate(
			long companyId, String name, DDMStructure ddmStructure,
			String targetLayoutFriendlyUrl, String contentField)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new FeedNameException("Name is null");
		}

		long plid = _portal.getPlidFromFriendlyURL(
			companyId, targetLayoutFriendlyUrl);

		if (plid <= 0) {
			throw new FeedTargetLayoutFriendlyUrlException(
				StringBundler.concat(
					"No layout exists for company ", companyId,
					" and friendly URL ", targetLayoutFriendlyUrl));
		}

		if (contentField.equals(JournalFeedConstants.RENDERED_WEB_CONTENT) ||
			contentField.equals(JournalFeedConstants.WEB_CONTENT_DESCRIPTION)) {

			return;
		}

		if (ddmStructure == null) {
			throw new FeedContentFieldException(
				"Invalid content field " + contentField);
		}

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		if (ddmFormFieldsMap.containsKey(contentField)) {
			return;
		}

		if (!_isValidStructureOptionValue(ddmFormFieldsMap, contentField)) {
			throw new FeedContentFieldException(
				"Invalid content field " + contentField);
		}
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DDMStructureLinkLocalService _ddmStructureLinkLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}