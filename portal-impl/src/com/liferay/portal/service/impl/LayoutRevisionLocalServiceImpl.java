/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutRevisionException;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutRevisionConstants;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.RecentLayoutRevisionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.comparator.LayoutRevisionCreateDateComparator;
import com.liferay.portal.kernel.util.comparator.LayoutRevisionModifiedDateComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.service.base.LayoutRevisionLocalServiceBaseImpl;
import com.liferay.portal.util.LayoutTypeControllerTracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 */
public class LayoutRevisionLocalServiceImpl
	extends LayoutRevisionLocalServiceBaseImpl {

	@Override
	public LayoutRevision addLayoutRevision(
			long userId, long layoutSetBranchId, long layoutBranchId,
			long parentLayoutRevisionId, boolean head, long plid,
			long portletPreferencesPlid, boolean privateLayout, String name,
			String title, String description, String keywords, String robots,
			String typeSettings, boolean iconImage, long iconImageId,
			String themeId, String colorSchemeId, String css,
			ServiceContext serviceContext)
		throws PortalException {

		// Layout revision

		User user = _userPersistence.findByPrimaryKey(userId);
		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchPersistence.findByPrimaryKey(layoutSetBranchId);
		parentLayoutRevisionId = getParentLayoutRevisionId(
			layoutSetBranchId, parentLayoutRevisionId, plid);

		long layoutRevisionId = getUniqueLayoutRevisionId();

		LayoutRevision layoutRevision = layoutRevisionPersistence.create(
			layoutRevisionId);

		layoutRevision.setGroupId(layoutSetBranch.getGroupId());
		layoutRevision.setCompanyId(user.getCompanyId());
		layoutRevision.setUserId(user.getUserId());
		layoutRevision.setUserName(user.getFullName());
		layoutRevision.setLayoutSetBranchId(layoutSetBranchId);
		layoutRevision.setLayoutBranchId(layoutBranchId);
		layoutRevision.setParentLayoutRevisionId(parentLayoutRevisionId);
		layoutRevision.setHead(head);
		layoutRevision.setPlid(plid);
		layoutRevision.setPrivateLayout(privateLayout);
		layoutRevision.setName(name);
		layoutRevision.setTitle(title);
		layoutRevision.setDescription(description);
		layoutRevision.setKeywords(keywords);
		layoutRevision.setRobots(robots);
		layoutRevision.setTypeSettings(typeSettings);
		layoutRevision.setIconImageId(iconImageId);
		layoutRevision.setThemeId(themeId);
		layoutRevision.setColorSchemeId(colorSchemeId);
		layoutRevision.setCss(css);
		layoutRevision.setStatus(WorkflowConstants.STATUS_DRAFT);
		layoutRevision.setStatusDate(
			serviceContext.getModifiedDate(new Date()));

		layoutRevision = layoutRevisionPersistence.update(layoutRevision);

		_layoutRevisionId.set(layoutRevision.getLayoutRevisionId());

		// Portlet preferences

		if (portletPreferencesPlid == LayoutConstants.DEFAULT_PLID) {
			portletPreferencesPlid = plid;
		}

		copyPortletPreferences(layoutRevision, portletPreferencesPlid);

		boolean major = ParamUtil.getBoolean(serviceContext, "major");

		if (major || !isWorkflowEnabled(plid)) {
			updateMajor(layoutRevision);
		}

		// Workflow

		if (isWorkflowEnabled(plid)) {
			WorkflowHandlerRegistryUtil.startWorkflowInstance(
				user.getCompanyId(), layoutRevision.getGroupId(),
				user.getUserId(), LayoutRevision.class.getName(),
				layoutRevision.getLayoutRevisionId(), layoutRevision,
				serviceContext);
		}
		else {
			updateStatus(
				userId, layoutRevisionId, WorkflowConstants.STATUS_APPROVED,
				serviceContext);
		}

		StagingUtil.setRecentLayoutRevisionId(
			user, layoutSetBranchId, plid,
			layoutRevision.getLayoutRevisionId());

		return layoutRevision;
	}

	@Override
	public void deleteLayoutLayoutRevisions(long plid) throws PortalException {
		for (LayoutRevision layoutRevision : getLayoutRevisions(plid)) {
			layoutRevisionLocalService.deleteLayoutRevision(layoutRevision);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public LayoutRevision deleteLayoutRevision(LayoutRevision layoutRevision)
		throws PortalException {

		if (layoutRevision.hasChildren()) {
			for (LayoutRevision curLayoutRevision :
					layoutRevision.getChildren()) {

				curLayoutRevision.setParentLayoutRevisionId(
					layoutRevision.getParentLayoutRevisionId());

				layoutRevisionPersistence.update(curLayoutRevision);
			}
		}

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				layoutRevision.getLayoutRevisionId());

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			try {
				_portletPreferencesLocalService.deletePortletPreferences(
					portletPreferences.getPortletPreferencesId());
			}
			catch (NoSuchPortletPreferencesException
						noSuchPortletPreferencesException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchPortletPreferencesException);
				}
			}
		}

		_recentLayoutRevisionLocalService.deleteRecentLayoutRevisions(
			layoutRevision.getLayoutRevisionId());

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			layoutRevision.getCompanyId(), layoutRevision.getGroupId(),
			LayoutRevision.class.getName(),
			layoutRevision.getLayoutRevisionId());

		return layoutRevisionPersistence.remove(layoutRevision);
	}

	@Override
	public LayoutRevision deleteLayoutRevision(long layoutRevisionId)
		throws PortalException {

		LayoutRevision layoutRevision =
			layoutRevisionPersistence.findByPrimaryKey(layoutRevisionId);

		return deleteLayoutRevision(layoutRevision);
	}

	@Override
	public void deleteLayoutRevisions(long layoutSetBranchId, long plid)
		throws PortalException {

		for (LayoutRevision layoutRevision :
				getLayoutRevisions(layoutSetBranchId, plid)) {

			layoutRevisionLocalService.deleteLayoutRevision(layoutRevision);
		}
	}

	@Override
	public void deleteLayoutRevisions(
			long layoutSetBranchId, long layoutBranchId, long plid)
		throws PortalException {

		List<LayoutRevision> layoutRevisions =
			layoutRevisionPersistence.findByL_L_P(
				layoutSetBranchId, layoutBranchId, plid);

		for (LayoutRevision layoutRevision : layoutRevisions) {
			layoutRevisionLocalService.deleteLayoutRevision(layoutRevision);
		}
	}

	@Override
	public void deleteLayoutSetBranchLayoutRevisions(long layoutSetBranchId)
		throws PortalException {

		List<LayoutRevision> layoutRevisions =
			layoutRevisionPersistence.findByLayoutSetBranchId(
				layoutSetBranchId);

		for (LayoutRevision layoutRevision : layoutRevisions) {
			layoutRevisionLocalService.deleteLayoutRevision(layoutRevision);
		}
	}

	@Override
	public LayoutRevision fetchLastLayoutRevision(long plid, boolean head) {
		try {
			return layoutRevisionPersistence.findByH_P_Last(
				head, plid,
				LayoutRevisionCreateDateComparator.getInstance(true));
		}
		catch (NoSuchLayoutRevisionException noSuchLayoutRevisionException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutRevisionException);
			}

			return null;
		}
	}

	@Override
	public LayoutRevision fetchLatestLayoutRevision(
		long layoutSetBranchId, long plid) {

		return layoutRevisionPersistence.fetchByL_P_First(
			layoutSetBranchId, plid,
			LayoutRevisionCreateDateComparator.getInstance(false));
	}

	@Override
	public LayoutRevision fetchLatestLayoutRevision(
		long layoutSetBranchId, long layoutBranchId, long plid) {

		return layoutRevisionPersistence.fetchByL_L_P_First(
			layoutSetBranchId, layoutBranchId, plid,
			LayoutRevisionCreateDateComparator.getInstance(false));
	}

	@Override
	public List<LayoutRevision> getChildLayoutRevisions(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid) {

		return layoutRevisionPersistence.findByL_P_P(
			layoutSetBranchId, parentLayoutRevisionId, plid);
	}

	@Override
	public List<LayoutRevision> getChildLayoutRevisions(
		long layoutSetBranchId, long parentLayoutRevision, long plid, int start,
		int end, OrderByComparator<LayoutRevision> orderByComparator) {

		return layoutRevisionPersistence.findByL_P_P(
			layoutSetBranchId, parentLayoutRevision, plid, start, end,
			orderByComparator);
	}

	@Override
	public int getChildLayoutRevisionsCount(
		long layoutSetBranchId, long parentLayoutRevision, long plid) {

		return layoutRevisionPersistence.countByL_P_P(
			layoutSetBranchId, parentLayoutRevision, plid);
	}

	@Override
	public LayoutRevision getLayoutRevision(
			long layoutSetBranchId, long layoutBranchId, long plid)
		throws PortalException {

		List<LayoutRevision> layoutRevisions =
			layoutRevisionPersistence.findByL_L_P(
				layoutSetBranchId, layoutBranchId, plid, 0, 1,
				LayoutRevisionCreateDateComparator.getInstance(false));

		if (!layoutRevisions.isEmpty()) {
			return layoutRevisions.get(0);
		}

		throw new NoSuchLayoutRevisionException(
			StringBundler.concat(
				"{layoutSetBranchId=", layoutSetBranchId, ", layoutBranchId=",
				layoutBranchId, ", plid=", plid, "}"));
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(long plid) {
		return layoutRevisionPersistence.findByPlid(plid);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, boolean head) {

		return layoutRevisionPersistence.findByL_H(layoutSetBranchId, head);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, boolean head, int status) {

		return layoutRevisionPersistence.findByL_H_S(
			layoutSetBranchId, head, status);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, int status) {

		return layoutRevisionPersistence.findByL_S(layoutSetBranchId, status);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid) {

		return layoutRevisionPersistence.findByL_P(layoutSetBranchId, plid);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, boolean head) {

		return layoutRevisionPersistence.findByL_H_P_Collection(
			layoutSetBranchId, head, plid);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, int status) {

		return layoutRevisionPersistence.findByL_P_S(
			layoutSetBranchId, plid, status);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return layoutRevisionPersistence.findByL_P(
			layoutSetBranchId, plid, start, end, orderByComparator);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long layoutBranchId, long plid, int start,
		int end, OrderByComparator<LayoutRevision> orderByComparator) {

		return layoutRevisionPersistence.findByL_L_P(
			layoutSetBranchId, layoutBranchId, plid, start, end,
			orderByComparator);
	}

	@Override
	public List<LayoutRevision> getLayoutRevisionsByStatus(int status) {
		return layoutRevisionPersistence.findByStatus(status);
	}

	@Override
	public int getLayoutRevisionsCount(long plid) {
		return layoutRevisionPersistence.countByPlid(plid);
	}

	@Override
	public int getLayoutRevisionsCount(
		long layoutSetBranchId, long layoutBranchId, long plid) {

		return layoutRevisionPersistence.countByL_L_P(
			layoutSetBranchId, layoutBranchId, plid);
	}

	@Override
	public LayoutRevision updateLayoutRevision(
			long userId, long layoutRevisionId, long layoutBranchId,
			String name, String title, String description, String keywords,
			String robots, String typeSettings, boolean iconImage,
			long iconImageId, String themeId, String colorSchemeId, String css,
			ServiceContext serviceContext)
		throws PortalException {

		// Layout revision

		LayoutRevision oldLayoutRevision =
			layoutRevisionPersistence.findByPrimaryKey(layoutRevisionId);

		LayoutRevision layoutRevision = null;

		if (_layoutRevisionId.get() > 0) {
			if (_layoutRevisionId.get() == layoutRevisionId) {
				layoutRevision = oldLayoutRevision;
			}
			else {
				LayoutRevision threadLayoutRevision =
					layoutRevisionPersistence.findByPrimaryKey(
						_layoutRevisionId.get());

				if (threadLayoutRevision.getParentLayoutRevisionId() ==
						oldLayoutRevision.getLayoutRevisionId()) {

					layoutRevision = threadLayoutRevision;
				}
			}
		}

		int workflowAction = serviceContext.getWorkflowAction();

		boolean revisionInProgress = ParamUtil.getBoolean(
			serviceContext, "revisionInProgress");

		if (!MergeLayoutPrototypesThreadLocal.isInProgress() &&
			(workflowAction != WorkflowConstants.ACTION_PUBLISH) &&
			(layoutRevision == null) && !revisionInProgress) {

			User user = _userPersistence.findByPrimaryKey(userId);

			layoutRevision = layoutRevisionPersistence.create(
				getUniqueLayoutRevisionId());

			layoutRevision.setGroupId(oldLayoutRevision.getGroupId());
			layoutRevision.setCompanyId(oldLayoutRevision.getCompanyId());
			layoutRevision.setUserId(user.getUserId());
			layoutRevision.setUserName(user.getFullName());
			layoutRevision.setLayoutSetBranchId(
				oldLayoutRevision.getLayoutSetBranchId());
			layoutRevision.setLayoutBranchId(layoutBranchId);
			layoutRevision.setParentLayoutRevisionId(
				oldLayoutRevision.getLayoutRevisionId());
			layoutRevision.setHead(false);
			layoutRevision.setPlid(oldLayoutRevision.getPlid());
			layoutRevision.setPrivateLayout(
				oldLayoutRevision.isPrivateLayout());
			layoutRevision.setName(name);
			layoutRevision.setTitle(title);
			layoutRevision.setDescription(description);
			layoutRevision.setKeywords(keywords);
			layoutRevision.setRobots(robots);
			layoutRevision.setTypeSettings(typeSettings);
			layoutRevision.setIconImageId(iconImageId);
			layoutRevision.setThemeId(themeId);
			layoutRevision.setColorSchemeId(colorSchemeId);
			layoutRevision.setCss(css);
			layoutRevision.setStatus(WorkflowConstants.STATUS_DRAFT);
			layoutRevision.setStatusDate(
				serviceContext.getModifiedDate(new Date()));

			layoutRevision = layoutRevisionPersistence.update(layoutRevision);

			_layoutRevisionId.set(layoutRevision.getLayoutRevisionId());

			// Portlet preferences

			copyPortletPreferences(
				layoutRevision, layoutRevision.getParentLayoutRevisionId());

			if (Objects.equals(serviceContext.getCommand(), Constants.DELETE)) {
				String[] removePortletIdsArray =
					(String[])serviceContext.getAttribute("removePortletIds");

				if (ArrayUtil.isNotEmpty(removePortletIdsArray)) {
					Set<String> removePortletIds = SetUtil.fromArray(
						removePortletIdsArray);

					for (PortletPreferences portletPreferences :
							_portletPreferencesLocalService.
								getPortletPreferencesByPlid(
									layoutRevision.getLayoutRevisionId())) {

						if (removePortletIds.contains(
								portletPreferences.getPortletId())) {

							_portletPreferencesLocalService.
								deletePortletPreferences(
									portletPreferences.
										getPortletPreferencesId());
						}
					}
				}
			}

			StagingUtil.setRecentLayoutBranchId(
				user, layoutRevision.getLayoutSetBranchId(),
				layoutRevision.getPlid(), layoutRevision.getLayoutBranchId());

			StagingUtil.setRecentLayoutRevisionId(
				user, layoutRevision.getLayoutSetBranchId(),
				layoutRevision.getPlid(), layoutRevision.getLayoutRevisionId());
		}
		else {
			if (layoutRevision == null) {
				layoutRevision = oldLayoutRevision;
			}

			layoutRevision.setName(name);
			layoutRevision.setTitle(title);
			layoutRevision.setDescription(description);
			layoutRevision.setKeywords(keywords);
			layoutRevision.setRobots(robots);
			layoutRevision.setTypeSettings(typeSettings);
			layoutRevision.setIconImageId(iconImageId);
			layoutRevision.setThemeId(themeId);
			layoutRevision.setColorSchemeId(colorSchemeId);
			layoutRevision.setCss(css);

			layoutRevision = layoutRevisionPersistence.update(layoutRevision);

			_layoutRevisionId.set(layoutRevision.getLayoutRevisionId());
		}

		boolean major = ParamUtil.getBoolean(serviceContext, "major");

		if (major || !isWorkflowEnabled(layoutRevision.getPlid())) {
			updateMajor(layoutRevision);
		}

		// Workflow

		if (isWorkflowEnabled(layoutRevision.getPlid())) {
			WorkflowHandlerRegistryUtil.startWorkflowInstance(
				layoutRevision.getCompanyId(), layoutRevision.getGroupId(),
				userId, LayoutRevision.class.getName(),
				layoutRevision.getLayoutRevisionId(), layoutRevision,
				serviceContext);
		}
		else {
			layoutRevision = updateStatus(
				userId, layoutRevision.getLayoutRevisionId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext);
		}

		Layout layout = _layoutLocalService.getLayout(layoutRevision.getPlid());

		if (layout.isTypeContent()) {
			layoutRevision = updateStatus(
				userId, layoutRevision.getLayoutRevisionId(),
				WorkflowConstants.STATUS_APPROVED, serviceContext);
		}

		return layoutRevision;
	}

	@Override
	public LayoutRevision updateStatus(
			long userId, long layoutRevisionId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userPersistence.findByPrimaryKey(userId);

		LayoutRevision layoutRevision =
			layoutRevisionPersistence.findByPrimaryKey(layoutRevisionId);

		boolean head = layoutRevision.isHead();

		layoutRevision.setStatus(status);
		layoutRevision.setStatusByUserId(user.getUserId());
		layoutRevision.setStatusByUserName(user.getFullName());
		layoutRevision.setStatusDate(new Date());

		if (status == WorkflowConstants.STATUS_APPROVED) {
			layoutRevision.setHead(true);
		}
		else {
			layoutRevision.setHead(false);
			layoutRevision.setMajor(false);
		}

		layoutRevision = layoutRevisionPersistence.update(layoutRevision);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			List<LayoutRevision> layoutRevisions =
				layoutRevisionPersistence.findByL_P(
					layoutRevision.getLayoutSetBranchId(),
					layoutRevision.getPlid());

			for (LayoutRevision curLayoutRevision : layoutRevisions) {
				if (curLayoutRevision.getLayoutRevisionId() !=
						layoutRevision.getLayoutRevisionId()) {

					curLayoutRevision.setHead(false);

					layoutRevisionPersistence.update(curLayoutRevision);
				}
			}
		}
		else if (head) {
			List<LayoutRevision> layoutRevisions =
				layoutRevisionPersistence.findByL_P_S(
					layoutRevision.getLayoutSetBranchId(),
					layoutRevision.getPlid(), WorkflowConstants.STATUS_APPROVED,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					LayoutRevisionModifiedDateComparator.getInstance(false));

			for (LayoutRevision curLayoutRevision : layoutRevisions) {
				if (curLayoutRevision.getLayoutRevisionId() !=
						layoutRevision.getLayoutRevisionId()) {

					curLayoutRevision.setHead(true);

					layoutRevisionPersistence.update(curLayoutRevision);

					break;
				}
			}
		}

		return layoutRevision;
	}

	protected void copyPortletPreferences(
		LayoutRevision layoutRevision, long parentLayoutRevisionId) {

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				parentLayoutRevisionId);

		for (PortletPreferences portletPreferences : portletPreferencesList) {
			jakarta.portlet.PortletPreferences jxPortletPreferences =
				_portletPreferenceValueLocalService.getPreferences(
					portletPreferences);

			_portletPreferencesLocalService.addPortletPreferences(
				layoutRevision.getCompanyId(), portletPreferences.getOwnerId(),
				portletPreferences.getOwnerType(),
				layoutRevision.getLayoutRevisionId(),
				portletPreferences.getPortletId(), null,
				PortletPreferencesFactoryUtil.toXML(jxPortletPreferences));
		}
	}

	protected long getParentLayoutRevisionId(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid) {

		LayoutRevision parentLayoutRevision = null;

		if (parentLayoutRevisionId > 0) {
			parentLayoutRevision = layoutRevisionPersistence.fetchByPrimaryKey(
				parentLayoutRevisionId);

			if (parentLayoutRevision == null) {
				List<LayoutRevision> layoutRevisions =
					layoutRevisionPersistence.findByL_P(
						layoutSetBranchId, plid, 0, 1);

				if (!layoutRevisions.isEmpty()) {
					parentLayoutRevision = layoutRevisions.get(0);
				}
			}
		}

		if (parentLayoutRevision != null) {
			return parentLayoutRevision.getLayoutRevisionId();
		}

		return LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID;
	}

	protected long getUniqueLayoutRevisionId() {
		long layoutRevisionId = counterLocalService.increment();

		while (_layoutLocalService.fetchLayout(layoutRevisionId) != null) {
			layoutRevisionId = counterLocalService.increment();
		}

		return layoutRevisionId;
	}

	protected boolean isWorkflowEnabled(long plid) throws PortalException {
		Layout layout = _layoutLocalService.getLayout(plid);

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				layout.getType());

		if (layoutTypeController.isWorkflowEnabled()) {
			return true;
		}

		return false;
	}

	protected LayoutRevision updateMajor(LayoutRevision layoutRevision)
		throws PortalException {

		List<LayoutRevision> parentLayoutRevisions = new ArrayList<>();

		long parentLayoutRevisionId =
			layoutRevision.getParentLayoutRevisionId();

		while (parentLayoutRevisionId !=
					LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID) {

			LayoutRevision parentLayoutRevision =
				layoutRevisionPersistence.findByPrimaryKey(
					parentLayoutRevisionId);

			if (parentLayoutRevision.isMajor()) {
				break;
			}

			parentLayoutRevisions.add(parentLayoutRevision);

			parentLayoutRevisionId =
				parentLayoutRevision.getParentLayoutRevisionId();
		}

		layoutRevision.setParentLayoutRevisionId(parentLayoutRevisionId);
		layoutRevision.setMajor(true);

		layoutRevision = layoutRevisionPersistence.update(layoutRevision);

		for (LayoutRevision parentLayoutRevision : parentLayoutRevisions) {
			List<LayoutRevision> childrenLayoutRevisions =
				parentLayoutRevision.getChildren();

			if (!childrenLayoutRevisions.isEmpty()) {
				break;
			}

			layoutRevisionLocalService.deleteLayoutRevision(
				parentLayoutRevision);
		}

		return layoutRevision;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutRevisionLocalServiceImpl.class);

	private static final ThreadLocal<Long> _layoutRevisionId =
		new CentralizedThreadLocal<>(
			LayoutRevisionLocalServiceImpl.class + "._layoutRevisionId",
			() -> 0L);

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = LayoutSetBranchPersistence.class)
	private LayoutSetBranchPersistence _layoutSetBranchPersistence;

	@BeanReference(type = PortletPreferencesLocalService.class)
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@BeanReference(type = PortletPreferenceValueLocalService.class)
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@BeanReference(type = RecentLayoutRevisionLocalService.class)
	private RecentLayoutRevisionLocalService _recentLayoutRevisionLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = WorkflowInstanceLinkLocalService.class)
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}