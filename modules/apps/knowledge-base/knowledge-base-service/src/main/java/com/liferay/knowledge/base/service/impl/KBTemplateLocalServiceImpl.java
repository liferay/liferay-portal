/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.impl;

import com.liferay.knowledge.base.constants.AdminActivityKeys;
import com.liferay.knowledge.base.exception.KBTemplateContentException;
import com.liferay.knowledge.base.exception.KBTemplateTitleException;
import com.liferay.knowledge.base.exception.NoSuchTemplateException;
import com.liferay.knowledge.base.internal.util.KBCommentUtil;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.base.KBTemplateLocalServiceBaseImpl;
import com.liferay.knowledge.base.service.persistence.KBCommentPersistence;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.Conjunction;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.Disjunction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.service.SocialActivityLocalService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.knowledge.base.model.KBTemplate",
	service = AopService.class
)
public class KBTemplateLocalServiceImpl extends KBTemplateLocalServiceBaseImpl {

	@Override
	public KBTemplate addKBTemplate(
			long userId, String title, String content,
			ServiceContext serviceContext)
		throws PortalException {

		// KB template

		User user = _userLocalService.getUser(userId);
		long groupId = serviceContext.getScopeGroupId();
		Date date = new Date();

		_validate(title, content);

		long kbTemplateId = counterLocalService.increment();

		KBTemplate kbTemplate = kbTemplatePersistence.create(kbTemplateId);

		kbTemplate.setUuid(serviceContext.getUuid());
		kbTemplate.setGroupId(groupId);
		kbTemplate.setCompanyId(user.getCompanyId());
		kbTemplate.setUserId(user.getUserId());
		kbTemplate.setUserName(user.getFullName());
		kbTemplate.setCreateDate(serviceContext.getCreateDate(date));
		kbTemplate.setModifiedDate(serviceContext.getModifiedDate(date));
		kbTemplate.setTitle(title);
		kbTemplate.setContent(content);

		kbTemplate = kbTemplatePersistence.update(kbTemplate);

		// Resources

		_resourceLocalService.addModelResources(kbTemplate, serviceContext);

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", kbTemplate.getTitle());

		_socialActivityLocalService.addActivity(
			userId, groupId, KBTemplate.class.getName(), kbTemplateId,
			AdminActivityKeys.ADD_KB_TEMPLATE, extraDataJSONObject.toString(),
			0);

		return kbTemplate;
	}

	@Override
	public void deleteGroupKBTemplates(long groupId) throws PortalException {
		List<KBTemplate> kbTemplates = kbTemplatePersistence.findByGroupId(
			groupId);

		for (KBTemplate kbTemplate : kbTemplates) {
			kbTemplateLocalService.deleteKBTemplate(kbTemplate);
		}
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBTemplate deleteKBTemplate(KBTemplate kbTemplate)
		throws PortalException {

		// KB template

		kbTemplatePersistence.remove(kbTemplate);

		// Resources

		_resourceLocalService.deleteResource(
			kbTemplate.getCompanyId(), KBTemplate.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, kbTemplate.getKbTemplateId());

		// KB Comments

		KBCommentUtil.deleteKBComments(
			KBTemplate.class.getName(), _classNameLocalService,
			kbTemplate.getKbTemplateId(), _kbCommentPersistence);

		// Social

		_socialActivityLocalService.deleteActivities(
			KBTemplate.class.getName(), kbTemplate.getKbTemplateId());

		return kbTemplate;
	}

	@Override
	public KBTemplate deleteKBTemplate(long kbTemplateId)
		throws PortalException {

		KBTemplate kbTemplate = kbTemplatePersistence.findByPrimaryKey(
			kbTemplateId);

		return kbTemplateLocalService.deleteKBTemplate(kbTemplate);
	}

	@Override
	public void deleteKBTemplates(long[] kbTemplateIds) throws PortalException {
		for (long kbTemplateId : kbTemplateIds) {
			KBTemplate kbTemplate = null;

			try {
				kbTemplate = kbTemplatePersistence.findByPrimaryKey(
					kbTemplateId);
			}
			catch (NoSuchTemplateException noSuchTemplateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchTemplateException);
				}

				continue;
			}

			kbTemplateLocalService.deleteKBTemplate(kbTemplate);
		}
	}

	@Override
	public List<KBTemplate> search(
		long groupId, String title, String content, Date startDate,
		Date endDate, boolean andOperator, int start, int end,
		OrderByComparator<KBTemplate> orderByComparator) {

		DynamicQuery dynamicQuery = _buildDynamicQuery(
			groupId, title, content, startDate, endDate, andOperator);

		return dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	@Override
	public KBTemplate updateKBTemplate(
			long kbTemplateId, String title, String content,
			ServiceContext serviceContext)
		throws PortalException {

		// KB template

		_validate(title, content);

		KBTemplate kbTemplate = kbTemplatePersistence.findByPrimaryKey(
			kbTemplateId);

		kbTemplate.setModifiedDate(serviceContext.getModifiedDate(null));
		kbTemplate.setTitle(title);
		kbTemplate.setContent(content);

		kbTemplate = kbTemplatePersistence.update(kbTemplate);

		// Social

		JSONObject extraDataJSONObject = JSONUtil.put(
			"title", kbTemplate.getTitle());

		_socialActivityLocalService.addActivity(
			kbTemplate.getUserId(), kbTemplate.getGroupId(),
			KBTemplate.class.getName(), kbTemplateId,
			AdminActivityKeys.UPDATE_KB_TEMPLATE,
			extraDataJSONObject.toString(), 0);

		return kbTemplate;
	}

	private DynamicQuery _buildDynamicQuery(
		long groupId, String title, String content, Date startDate,
		Date endDate, boolean andOperator) {

		Junction junction = null;

		if (andOperator) {
			junction = RestrictionsFactoryUtil.conjunction();
		}
		else {
			junction = RestrictionsFactoryUtil.disjunction();
		}

		Map<String, String> terms = new HashMap<>();

		if (Validator.isNotNull(title)) {
			terms.put("title", title);
		}

		if (Validator.isNotNull(content)) {
			terms.put("content", content);
		}

		for (Map.Entry<String, String> entry : terms.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

			for (String keyword : KnowledgeBaseUtil.splitKeywords(value)) {
				Criterion criterion = RestrictionsFactoryUtil.ilike(
					key, StringUtil.quote(keyword, StringPool.PERCENT));

				disjunction.add(criterion);
			}

			junction.add(disjunction);
		}

		if ((endDate != null) && (startDate != null)) {
			Disjunction disjunction = RestrictionsFactoryUtil.disjunction();

			String[] propertyNames = {"createDate", "modifiedDate"};

			for (String propertyName : propertyNames) {
				Property property = PropertyFactoryUtil.forName(propertyName);

				Conjunction conjunction = RestrictionsFactoryUtil.conjunction();

				conjunction.add(property.gt(startDate));
				conjunction.add(property.lt(endDate));

				disjunction.add(conjunction);
			}

			junction.add(disjunction);
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KBTemplate.class, getClassLoader());

		if (groupId > 0) {
			Property property = PropertyFactoryUtil.forName("groupId");

			dynamicQuery.add(property.eq(groupId));
		}

		return dynamicQuery.add(junction);
	}

	private void _validate(String title, String content)
		throws PortalException {

		if (Validator.isNull(title)) {
			throw new KBTemplateTitleException();
		}

		if (Validator.isNull(content)) {
			throw new KBTemplateContentException();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBTemplateLocalServiceImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private KBCommentPersistence _kbCommentPersistence;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SocialActivityLocalService _socialActivityLocalService;

	@Reference
	private UserLocalService _userLocalService;

}