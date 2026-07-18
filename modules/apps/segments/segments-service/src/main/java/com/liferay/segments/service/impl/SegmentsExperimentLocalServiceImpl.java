/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationManagerUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.exception.DuplicateSegmentsExperimentException;
import com.liferay.segments.exception.LockedSegmentsExperimentException;
import com.liferay.segments.exception.NoSuchExperimentException;
import com.liferay.segments.exception.RunSegmentsExperimentException;
import com.liferay.segments.exception.SegmentsExperimentConfidenceLevelException;
import com.liferay.segments.exception.SegmentsExperimentGoalException;
import com.liferay.segments.exception.SegmentsExperimentNameException;
import com.liferay.segments.exception.SegmentsExperimentRelSplitException;
import com.liferay.segments.exception.SegmentsExperimentStatusException;
import com.liferay.segments.exception.SegmentsExperimentTypeException;
import com.liferay.segments.exception.WinnerSegmentsExperienceException;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.segments.service.base.SegmentsExperimentLocalServiceBaseImpl;
import com.liferay.segments.service.persistence.SegmentsExperiencePersistence;
import com.liferay.segments.service.persistence.SegmentsExperimentRelPersistence;
import com.liferay.segments.util.comparator.SegmentsExperiencePriorityComparator;

import java.math.RoundingMode;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.segments.model.SegmentsExperiment",
	service = AopService.class
)
public class SegmentsExperimentLocalServiceImpl
	extends SegmentsExperimentLocalServiceBaseImpl {

	@Override
	public SegmentsExperiment addSegmentsExperiment(
			long segmentsExperienceId, long plid, String name,
			String description, String goal, String goalTarget,
			ServiceContext serviceContext)
		throws PortalException {

		// Segments experiment

		long segmentsExperimentId = counterLocalService.increment();

		int status = SegmentsExperimentConstants.STATUS_DRAFT;

		_validate(name, goal, status, status);

		_validateDuplicateSegmentsExperiment(
			serviceContext.getScopeGroupId(), segmentsExperienceId, plid);

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentPersistence.create(segmentsExperimentId);

		segmentsExperiment.setUuid(serviceContext.getUuid());
		segmentsExperiment.setGroupId(serviceContext.getScopeGroupId());

		User user = _userLocalService.getUser(serviceContext.getUserId());

		segmentsExperiment.setCompanyId(user.getCompanyId());
		segmentsExperiment.setUserId(user.getUserId());
		segmentsExperiment.setUserName(user.getFullName());

		segmentsExperiment.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		segmentsExperiment.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		segmentsExperiment.setSegmentsExperienceId(segmentsExperienceId);
		segmentsExperiment.setSegmentsExperimentKey(
			String.valueOf(counterLocalService.increment()));
		segmentsExperiment.setPlid(plid);
		segmentsExperiment.setName(name);
		segmentsExperiment.setDescription(description);
		segmentsExperiment.setTypeSettings(
			UnicodePropertiesBuilder.create(
				true
			).put(
				"goal", goal
			).put(
				"goalTarget", goalTarget
			).buildString());
		segmentsExperiment.setStatus(status);

		segmentsExperiment = segmentsExperimentPersistence.update(
			segmentsExperiment);

		// Resources

		_resourceLocalService.addModelResources(
			segmentsExperiment, serviceContext);

		// Segments experiment rel

		_segmentsExperimentRelLocalService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperiment.getSegmentsExperienceId(), serviceContext);

		return segmentsExperiment;
	}

	@Override
	public SegmentsExperiment deleteSegmentsExperiment(
			long segmentsExperimentId)
		throws PortalException {

		return deleteSegmentsExperiment(
			segmentsExperimentPersistence.findByPrimaryKey(
				segmentsExperimentId),
			false);
	}

	@Override
	public SegmentsExperiment deleteSegmentsExperiment(
			long groupId, String segmentsExperienceKey, long plid)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.fetchSegmentsExperiment(
				groupId, segmentsExperienceKey, plid);

		return segmentsExperimentLocalService.deleteSegmentsExperiment(
			segmentsExperiment);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SegmentsExperiment deleteSegmentsExperiment(
			SegmentsExperiment segmentsExperiment)
		throws PortalException {

		return deleteSegmentsExperiment(segmentsExperiment, false);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SegmentsExperiment deleteSegmentsExperiment(
			SegmentsExperiment segmentsExperiment, boolean force)
		throws PortalException {

		// Segments experiment

		if (!force) {
			_validateDeletableStatus(segmentsExperiment.getStatus());
		}

		segmentsExperimentPersistence.remove(segmentsExperiment);

		// Resources

		_resourceLocalService.deleteResource(
			segmentsExperiment, ResourceConstants.SCOPE_INDIVIDUAL);

		// Segments experiment rels

		_segmentsExperimentRelLocalService.deleteSegmentsExperimentRels(
			segmentsExperiment.getSegmentsExperimentId());

		return segmentsExperiment;
	}

	@Override
	public SegmentsExperiment fetchSegmentsExperiment(
		long groupId, String segmentsExperimentKey) {

		return segmentsExperimentPersistence.fetchByG_S(
			groupId, segmentsExperimentKey);
	}

	@Override
	public SegmentsExperiment fetchSegmentsExperiment(
		long groupId, String segmentsExperienceKey, long plid) {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				groupId, segmentsExperienceKey, _getPublishedLayoutPlid(plid));

		if (segmentsExperience == null) {
			return null;
		}

		for (SegmentsExperimentRel segmentsExperimentRel :
				_segmentsExperimentRelPersistence.findBySegmentsExperienceId(
					segmentsExperience.getSegmentsExperienceId())) {

			SegmentsExperiment segmentsExperiment =
				segmentsExperimentPersistence.fetchByPrimaryKey(
					segmentsExperimentRel.getSegmentsExperimentId());

			if ((segmentsExperiment.getGroupId() == groupId) &&
				(segmentsExperiment.getPlid() == plid)) {

				return segmentsExperiment;
			}
		}

		return null;
	}

	@Override
	public List<SegmentsExperiment> getSegmentsEntrySegmentsExperiments(
		String segmentsEntryERC, long segmentsEntryGroupId) {

		DynamicQuery dynamicQuery =
			_segmentsExperienceLocalService.dynamicQuery();

		Property segmentsEntryERCProperty = PropertyFactoryUtil.forName(
			"segmentsEntryERC");
		Property segmentsExperienceGroupIdProperty =
			PropertyFactoryUtil.forName("groupId");
		Property segmentsEntryScopeERCProperty = PropertyFactoryUtil.forName(
			"segmentsEntryScopeERC");

		Criterion criterion = RestrictionsFactoryUtil.and(
			segmentsEntryERCProperty.eq(segmentsEntryERC),
			RestrictionsFactoryUtil.and(
				segmentsExperienceGroupIdProperty.eq(segmentsEntryGroupId),
				segmentsEntryScopeERCProperty.isNull()));

		Group group = _groupLocalService.fetchGroup(segmentsEntryGroupId);

		if ((group != null) &&
			Validator.isNotNull(group.getExternalReferenceCode())) {

			Criterion remoteCriterion = RestrictionsFactoryUtil.and(
				segmentsEntryERCProperty.eq(segmentsEntryERC),
				segmentsEntryScopeERCProperty.eq(
					group.getExternalReferenceCode()));

			criterion = RestrictionsFactoryUtil.or(criterion, remoteCriterion);
		}

		dynamicQuery.add(criterion);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("segmentsExperienceId"));

		List<Long> segmentsExperienceIds =
			_segmentsExperiencePersistence.findWithDynamicQuery(dynamicQuery);

		if (segmentsExperienceIds.isEmpty()) {
			return Collections.emptyList();
		}

		dynamicQuery = segmentsExperimentLocalService.dynamicQuery();

		Property segmentsExperienceIdProperty = PropertyFactoryUtil.forName(
			"segmentsExperienceId");

		dynamicQuery.add(
			segmentsExperienceIdProperty.in(segmentsExperienceIds));

		dynamicQuery.addOrder(OrderFactoryUtil.desc("createDate"));

		return segmentsExperimentPersistence.findWithDynamicQuery(dynamicQuery);
	}

	@Override
	public SegmentsExperiment getSegmentsExperiment(
			String segmentsExperimentKey)
		throws NoSuchExperimentException {

		return segmentsExperimentPersistence.findBySegmentsExperimentKey_First(
			segmentsExperimentKey, null);
	}

	@Override
	public SegmentsExperiment runSegmentsExperiment(
			long segmentsExperimentId, double confidenceLevel,
			Map<Long, Double> segmentsExperienceIdSplitMap, String type)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentPersistence.findByPrimaryKey(
				segmentsExperimentId);

		_validateEditableStatus(segmentsExperiment.getStatus());

		_validateConfidenceLevel(confidenceLevel);
		_validateSegmentsExperimentRels(segmentsExperienceIdSplitMap);
		_validateSplit(segmentsExperienceIdSplitMap);
		_validateType(type);

		UnicodeProperties typeSettingsUnicodeProperties =
			segmentsExperiment.getTypeSettingsProperties();

		_validateGoalTarget(
			typeSettingsUnicodeProperties.get("goal"),
			typeSettingsUnicodeProperties.get("goalTarget"));

		typeSettingsUnicodeProperties.setProperty(
			"confidenceLevel", String.valueOf(confidenceLevel));
		typeSettingsUnicodeProperties.setProperty("type", type);

		segmentsExperiment.setTypeSettings(
			typeSettingsUnicodeProperties.toString());

		for (Map.Entry<Long, Double> segmentsExperienceIdSplit :
				segmentsExperienceIdSplitMap.entrySet()) {

			_segmentsExperimentRelLocalService.updateSegmentsExperimentRel(
				segmentsExperimentId, segmentsExperienceIdSplit.getKey(),
				BigDecimalUtil.scale(
					segmentsExperienceIdSplit.getValue(), 2,
					RoundingMode.HALF_DOWN));
		}

		return _updateSegmentsExperimentStatus(
			segmentsExperiment, -1,
			SegmentsExperimentConstants.Status.RUNNING.getValue());
	}

	@Override
	public SegmentsExperiment updateSegmentsExperiment(
			long segmentsExperimentId, String name, String description,
			String goal, String goalTarget)
		throws PortalException {

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentPersistence.findByPrimaryKey(
				segmentsExperimentId);

		_validateEditableStatus(segmentsExperiment.getStatus());

		_validateGoal(goal);
		_validateName(name);

		segmentsExperiment.setModifiedDate(new Date());
		segmentsExperiment.setName(name);
		segmentsExperiment.setDescription(description);

		UnicodeProperties typeSettingsUnicodeProperties =
			segmentsExperiment.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty("goal", goal);
		typeSettingsUnicodeProperties.setProperty("goalTarget", goalTarget);

		segmentsExperiment.setTypeSettings(
			typeSettingsUnicodeProperties.toString());

		return segmentsExperimentPersistence.update(segmentsExperiment);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperimentStatus(
			long segmentsExperimentId, int status)
		throws PortalException {

		return _updateSegmentsExperimentStatus(
			segmentsExperimentPersistence.findByPrimaryKey(
				segmentsExperimentId),
			-1, status);
	}

	@Override
	public SegmentsExperiment updateSegmentsExperimentStatus(
			long segmentsExperimentId, long winnerSegmentsExperienceId,
			int status)
		throws PortalException {

		return _updateSegmentsExperimentStatus(
			segmentsExperimentPersistence.findByPrimaryKey(
				segmentsExperimentId),
			winnerSegmentsExperienceId, status);
	}

	private long _getPublishedLayoutPlid(long plid) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if ((layout != null) && layout.isDraftLayout()) {
			return layout.getClassPK();
		}

		return plid;
	}

	private void _publishSegmentsExperienceVariant(
		SegmentsExperience controlSegmentsExperience,
		String newSegmentsExperienceKey,
		SegmentsExperience variantSegmentsExperience) {

		int originalPriority = controlSegmentsExperience.getPriority();

		SegmentsExperience segmentsExperience =
			_segmentsExperiencePersistence.fetchByG_P_First(
				controlSegmentsExperience.getGroupId(),
				controlSegmentsExperience.getPlid(),
				SegmentsExperiencePriorityComparator.getInstance(true));

		controlSegmentsExperience.setPriority(
			segmentsExperience.getPriority() - 1);

		controlSegmentsExperience.setActive(false);

		boolean controlSegmentsExperienceKeyDefault = Objects.equals(
			SegmentsExperienceConstants.KEY_DEFAULT,
			controlSegmentsExperience.getSegmentsExperienceKey());

		if (controlSegmentsExperienceKeyDefault) {
			_setSegmentsExperienceKeyProperty(controlSegmentsExperience);

			controlSegmentsExperience.setSegmentsExperienceKey(
				newSegmentsExperienceKey);
		}

		_segmentsExperienceLocalService.updateSegmentsExperience(
			controlSegmentsExperience);

		variantSegmentsExperience.setActive(true);

		variantSegmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				variantSegmentsExperience);

		long variantSegmentsExperienceId =
			variantSegmentsExperience.getSegmentsExperienceId();

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				SegmentsExperience callbackVariantSegmentsExperience =
					_segmentsExperienceLocalService.getSegmentsExperience(
						variantSegmentsExperienceId);

				if (controlSegmentsExperienceKeyDefault) {
					callbackVariantSegmentsExperience.setSegmentsExperienceKey(
						SegmentsExperienceConstants.KEY_DEFAULT);
				}

				callbackVariantSegmentsExperience.setPriority(originalPriority);

				_segmentsExperienceLocalService.updateSegmentsExperience(
					callbackVariantSegmentsExperience);

				return null;
			});
	}

	private void _sendNotificationEvent(SegmentsExperiment segmentsExperiment)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if ((serviceContext == null) ||
			(serviceContext.getUserId() == segmentsExperiment.getUserId()) ||
			!UserNotificationManagerUtil.isDeliver(
				segmentsExperiment.getUserId(),
				SegmentsPortletKeys.SEGMENTS_EXPERIMENT, 0,
				SegmentsExperimentConstants.NOTIFICATION_TYPE_UPDATE_STATUS,
				UserNotificationDeliveryConstants.TYPE_WEBSITE)) {

			return;
		}

		_userNotificationEventLocalService.sendUserNotificationEvents(
			segmentsExperiment.getUserId(),
			SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
			UserNotificationDeliveryConstants.TYPE_WEBSITE,
			JSONUtil.put(
				"classPK", segmentsExperiment.getSegmentsExperimentId()
			).put(
				"referrerClassNameId",
				_portal.getClassNameId(Layout.class.getName())
			).put(
				"referrerClassPK", segmentsExperiment.getPlid()
			).put(
				"segmentsExperimentKey",
				segmentsExperiment.getSegmentsExperimentKey()
			));
	}

	private void _setSegmentsExperienceKeyProperty(
		SegmentsExperience segmentsExperience) {

		UnicodeProperties typeSettingsUnicodeProperties =
			segmentsExperience.getTypeSettingsUnicodeProperties();

		typeSettingsUnicodeProperties.setProperty(
			"segmentsExperimentSegmentsExperienceKey",
			segmentsExperience.getSegmentsExperienceKey());

		segmentsExperience.setTypeSettingsUnicodeProperties(
			typeSettingsUnicodeProperties);
	}

	private SegmentsExperiment _updateSegmentsExperimentStatus(
			SegmentsExperiment segmentsExperiment,
			long winnerSegmentsExperienceId, int status)
		throws PortalException {

		_validateStatus(
			segmentsExperiment.getStatus(), status, winnerSegmentsExperienceId);

		if (winnerSegmentsExperienceId != -1) {
			_updateWinnerSegmentsExperienceId(
				segmentsExperiment, winnerSegmentsExperienceId, status);
		}

		segmentsExperiment.setModifiedDate(new Date());
		segmentsExperiment.setStatus(status);

		segmentsExperiment = segmentsExperimentPersistence.update(
			segmentsExperiment);

		_sendNotificationEvent(segmentsExperiment);

		return segmentsExperiment;
	}

	private void _updateWinnerSegmentsExperienceId(
			SegmentsExperiment segmentsExperiment,
			long winnerSegmentsExperienceId, int status)
		throws PortalException {

		SegmentsExperience winnerSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				winnerSegmentsExperienceId);

		if (winnerSegmentsExperience == null) {
			throw new WinnerSegmentsExperienceException(
				"Winner segments experience " + winnerSegmentsExperienceId +
					" does not exist");
		}

		SegmentsExperimentRel segmentsExperimentRel =
			_segmentsExperimentRelLocalService.fetchSegmentsExperimentRel(
				segmentsExperiment.getSegmentsExperimentId(),
				winnerSegmentsExperience.getSegmentsExperienceKey());

		if (segmentsExperimentRel == null) {
			throw new WinnerSegmentsExperienceException(
				"Winner segments experience " + winnerSegmentsExperienceId +
					" does not exist");
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			segmentsExperiment.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"winnerSegmentsExperienceId",
			String.valueOf(winnerSegmentsExperienceId));

		segmentsExperiment.setTypeSettings(
			typeSettingsUnicodeProperties.toString());

		SegmentsExperimentConstants.Status statusObject =
			SegmentsExperimentConstants.Status.valueOf(status);

		if (((statusObject == SegmentsExperimentConstants.Status.COMPLETED) ||
			 (statusObject == SegmentsExperimentConstants.Status.TERMINATED)) &&
			(winnerSegmentsExperienceId !=
				segmentsExperiment.getSegmentsExperienceId())) {

			Layout layout = _layoutLocalService.fetchLayout(
				segmentsExperiment.getPlid());

			Layout draftLayout = layout.fetchDraftLayout();

			String newSegmentsExperienceKey = String.valueOf(
				counterLocalService.increment(
					SegmentsExperience.class.getName()));

			_publishSegmentsExperienceVariant(
				_segmentsExperiencePersistence.findByG_SEK_P(
					draftLayout.getGroupId(),
					segmentsExperiment.getSegmentsExperienceKey(),
					draftLayout.getPlid()),
				newSegmentsExperienceKey,
				_segmentsExperiencePersistence.findByG_SEK_P(
					draftLayout.getGroupId(),
					winnerSegmentsExperience.getSegmentsExperienceKey(),
					draftLayout.getPlid()));

			_publishSegmentsExperienceVariant(
				_segmentsExperienceLocalService.getSegmentsExperience(
					segmentsExperiment.getSegmentsExperienceId()),
				newSegmentsExperienceKey,
				_segmentsExperienceLocalService.getSegmentsExperience(
					winnerSegmentsExperienceId));
		}
	}

	private void _validate(
			String name, String goal, int currentStatus, int newStatus)
		throws PortalException {

		_validateGoal(goal);
		_validateName(name);
		_validateStatus(currentStatus, newStatus, -1);
	}

	private void _validateConfidenceLevel(double confidenceLevel)
		throws PortalException {

		if ((confidenceLevel < 0.8) || (confidenceLevel > 0.99)) {
			throw new SegmentsExperimentConfidenceLevelException(
				"Confidence level " + confidenceLevel +
					" is not a value between 0.8 and 0.99");
		}
	}

	private void _validateDeletableStatus(int status) throws PortalException {
		SegmentsExperimentConstants.Status statusObject =
			SegmentsExperimentConstants.Status.valueOf(status);

		if (!statusObject.isDeletable()) {
			throw new LockedSegmentsExperimentException(
				"Segments experiment is not deletable in status " +
					statusObject);
		}
	}

	private void _validateDuplicateSegmentsExperiment(
			long groupId, long segmentsExperienceId, long plid)
		throws PortalException {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperienceId);

		SegmentsExperiment segmentsExperiment =
			segmentsExperimentLocalService.fetchSegmentsExperiment(
				groupId, segmentsExperience.getSegmentsExperienceKey(), plid);

		if (segmentsExperiment == null) {
			return;
		}

		throw new DuplicateSegmentsExperimentException();
	}

	private void _validateEditableStatus(int status) throws PortalException {
		SegmentsExperimentConstants.Status statusObject =
			SegmentsExperimentConstants.Status.valueOf(status);

		if (!statusObject.isEditable()) {
			throw new LockedSegmentsExperimentException(
				"Segments experiment is not editable in status " +
					statusObject);
		}
	}

	private void _validateGoal(String goal) throws PortalException {
		if (SegmentsExperimentConstants.Goal.parse(goal) == null) {
			throw new SegmentsExperimentGoalException();
		}
	}

	private void _validateGoalTarget(String goal, String goalTarget)
		throws PortalException {

		if (goal.equals(
				SegmentsExperimentConstants.Goal.CLICK_RATE.getLabel()) &&
			Validator.isNull(goalTarget)) {

			throw new RunSegmentsExperimentException(
				"Target element needs to be set in click goal");
		}
	}

	private void _validateName(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new SegmentsExperimentNameException();
		}
	}

	private void _validateSegmentsExperimentRels(
			Map<Long, Double> segmentsExperienceIdSplitMap)
		throws PortalException {

		if (segmentsExperienceIdSplitMap.size() <= 1) {
			throw new RunSegmentsExperimentException(
				"Segments experiment rels must be more than 1 to test " +
					"against control");
		}
	}

	private void _validateSplit(Map<Long, Double> segmentsExperienceIdSplitMap)
		throws PortalException {

		double segmentsExperienceIdSplitsSum = 0;

		Collection<Double> segmentsExperienceIdSplitsValues =
			segmentsExperienceIdSplitMap.values();

		for (Double segmentsExperienceIdSplitsValue :
				segmentsExperienceIdSplitsValues) {

			segmentsExperienceIdSplitsSum += BigDecimalUtil.scale(
				segmentsExperienceIdSplitsValue, 2, RoundingMode.HALF_DOWN);
		}

		if (segmentsExperienceIdSplitsSum != 1) {
			throw new SegmentsExperimentRelSplitException(
				"Segments experiment rel splits must add up to 1");
		}
	}

	private void _validateStatus(
			int status, int newStatus, long winnerSegmentsExperienceId)
		throws SegmentsExperimentStatusException {

		SegmentsExperimentConstants.Status.validateTransition(
			status, newStatus);

		SegmentsExperimentConstants.Status newStatusObject =
			SegmentsExperimentConstants.Status.valueOf(newStatus);

		if (newStatusObject.requiresWinnerExperience() &&
			(winnerSegmentsExperienceId < 0)) {

			throw new SegmentsExperimentStatusException(
				StringBundler.concat(
					"Status ", newStatusObject.name(),
					" requires a winner segments experience"));
		}
	}

	private void _validateType(String type) throws PortalException {
		if (SegmentsExperimentConstants.Type.parse(type) == null) {
			throw new SegmentsExperimentTypeException();
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperiencePersistence _segmentsExperiencePersistence;

	@Reference
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

	@Reference
	private SegmentsExperimentRelPersistence _segmentsExperimentRelPersistence;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}