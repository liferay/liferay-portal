/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.LayoutSetBranchNameException;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetBranchException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredLayoutSetBranchException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutBranchConstants;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutRevisionConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetBranchConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutBranchLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.RecentLayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.LayoutBranchPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutRevisionPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.LayoutSetBranchCreateDateComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.base.LayoutSetBranchLocalServiceBaseImpl;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 */
public class LayoutSetBranchLocalServiceImpl
	extends LayoutSetBranchLocalServiceBaseImpl {

	@Override
	public LayoutSetBranch addLayoutSetBranch(
			long userId, long groupId, boolean privateLayout, String name,
			String description, boolean master, long copyLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException {

		// Layout branch

		User user = _userPersistence.findByPrimaryKey(userId);

		validate(0, groupId, privateLayout, name, master);

		boolean logo = false;
		long logoId = 0;
		String themeId = null;
		String colorSchemeId = null;
		String css = null;
		String settings = null;

		if (copyLayoutSetBranchId > 0) {
			LayoutSetBranch copyLayoutSetBranch = getLayoutSetBranch(
				copyLayoutSetBranchId);

			logo = copyLayoutSetBranch.getLogo();
			logoId = copyLayoutSetBranch.getLogoId();
			themeId = copyLayoutSetBranch.getThemeId();
			colorSchemeId = copyLayoutSetBranch.getColorSchemeId();
			css = copyLayoutSetBranch.getCss();
			settings = copyLayoutSetBranch.getSettings();
		}
		else {
			LayoutSet layoutSet = _layoutSetPersistence.findByG_P(
				groupId, privateLayout);

			logo = layoutSet.getLogo();
			logoId = layoutSet.getLogoId();
			themeId = layoutSet.getThemeId();
			colorSchemeId = layoutSet.getColorSchemeId();
			css = layoutSet.getCss();
			settings = layoutSet.getSettings();
		}

		long layoutSetBranchId = counterLocalService.increment();

		LayoutSetBranch layoutSetBranch = layoutSetBranchPersistence.create(
			layoutSetBranchId);

		layoutSetBranch.setGroupId(groupId);
		layoutSetBranch.setCompanyId(user.getCompanyId());
		layoutSetBranch.setUserId(user.getUserId());
		layoutSetBranch.setUserName(user.getFullName());
		layoutSetBranch.setPrivateLayout(privateLayout);
		layoutSetBranch.setName(name);
		layoutSetBranch.setDescription(description);
		layoutSetBranch.setMaster(master);
		layoutSetBranch.setLogoId(logoId);

		if (logo) {
			Image logoImage = _imageLocalService.getImage(logoId);

			long layoutSetBranchLogoId = counterLocalService.increment();

			_imageLocalService.updateImage(
				layoutSetBranch.getCompanyId(), layoutSetBranchLogoId,
				logoImage.getTextObj(), logoImage.getType(),
				logoImage.getHeight(), logoImage.getWidth(),
				logoImage.getSize());

			layoutSetBranch.setLogoId(layoutSetBranchLogoId);
		}

		layoutSetBranch.setThemeId(themeId);
		layoutSetBranch.setColorSchemeId(colorSchemeId);
		layoutSetBranch.setCss(css);
		layoutSetBranch.setSettings(settings);

		layoutSetBranch = layoutSetBranchPersistence.update(layoutSetBranch);

		// Resources

		_resourceLocalService.addResources(
			user.getCompanyId(), layoutSetBranch.getGroupId(), user.getUserId(),
			LayoutSetBranch.class.getName(),
			layoutSetBranch.getLayoutSetBranchId(), false, true, false);

		// Layout revisions

		serviceContext.setAttribute("major", Boolean.TRUE.toString());

		if (layoutSetBranch.isMaster() ||
			(copyLayoutSetBranchId == LayoutSetBranchConstants.ALL_BRANCHES)) {

			List<Layout> layouts = _layoutPersistence.findByG_P_S(
				layoutSetBranch.getGroupId(), layoutSetBranch.isPrivateLayout(),
				false);

			for (Layout layout : layouts) {
				LayoutBranch layoutBranch =
					_layoutBranchLocalService.addLayoutBranch(
						layoutSetBranchId, layout.getPlid(),
						LayoutBranchConstants.MASTER_BRANCH_NAME,
						LayoutBranchConstants.MASTER_BRANCH_DESCRIPTION, true,
						serviceContext);

				LayoutRevision lastLayoutRevision =
					_layoutRevisionLocalService.fetchLastLayoutRevision(
						layout.getPlid(), true);

				if (lastLayoutRevision != null) {
					int workflowAction = serviceContext.getWorkflowAction();

					serviceContext.setWorkflowAction(
						WorkflowConstants.ACTION_PUBLISH);

					_layoutRevisionLocalService.addLayoutRevision(
						userId, layoutSetBranchId,
						layoutBranch.getLayoutBranchId(),
						LayoutRevisionConstants.
							DEFAULT_PARENT_LAYOUT_REVISION_ID,
						true, lastLayoutRevision.getPlid(),
						lastLayoutRevision.getLayoutRevisionId(),
						lastLayoutRevision.isPrivateLayout(),
						lastLayoutRevision.getName(),
						lastLayoutRevision.getTitle(),
						lastLayoutRevision.getDescription(),
						lastLayoutRevision.getKeywords(),
						lastLayoutRevision.getRobots(),
						lastLayoutRevision.getTypeSettings(),
						lastLayoutRevision.isIconImage(),
						lastLayoutRevision.getIconImageId(),
						lastLayoutRevision.getThemeId(),
						lastLayoutRevision.getColorSchemeId(),
						lastLayoutRevision.getCss(), serviceContext);

					serviceContext.setWorkflowAction(workflowAction);
				}
				else {
					_layoutRevisionLocalService.addLayoutRevision(
						userId, layoutSetBranchId,
						layoutBranch.getLayoutBranchId(),
						LayoutRevisionConstants.
							DEFAULT_PARENT_LAYOUT_REVISION_ID,
						false, layout.getPlid(), LayoutConstants.DEFAULT_PLID,
						layout.isPrivateLayout(), layout.getName(),
						layout.getTitle(), layout.getDescription(),
						layout.getKeywords(), layout.getRobots(),
						layout.getTypeSettings(), layout.isIconImage(),
						layout.getIconImageId(), layout.getThemeId(),
						layout.getColorSchemeId(), layout.getCss(),
						serviceContext);
				}
			}
		}
		else if (copyLayoutSetBranchId > 0) {
			List<LayoutRevision> layoutRevisions =
				_layoutRevisionLocalService.getLayoutRevisions(
					copyLayoutSetBranchId, true);

			for (LayoutRevision layoutRevision : layoutRevisions) {
				LayoutBranch layoutBranch =
					_layoutBranchLocalService.addLayoutBranch(
						layoutSetBranchId, layoutRevision.getPlid(),
						LayoutBranchConstants.MASTER_BRANCH_NAME,
						LayoutBranchConstants.MASTER_BRANCH_DESCRIPTION, true,
						serviceContext);

				_layoutRevisionLocalService.addLayoutRevision(
					userId, layoutSetBranchId, layoutBranch.getLayoutBranchId(),
					LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
					true, layoutRevision.getPlid(),
					layoutRevision.getLayoutRevisionId(),
					layoutRevision.isPrivateLayout(), layoutRevision.getName(),
					layoutRevision.getTitle(), layoutRevision.getDescription(),
					layoutRevision.getKeywords(), layoutRevision.getRobots(),
					layoutRevision.getTypeSettings(),
					layoutRevision.isIconImage(),
					layoutRevision.getIconImageId(),
					layoutRevision.getThemeId(),
					layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
					serviceContext);
			}
		}

		LayoutSet layoutSet = layoutSetBranch.getLayoutSet();

		StagingUtil.setRecentLayoutSetBranchId(
			user, layoutSet.getLayoutSetId(),
			layoutSetBranch.getLayoutSetBranchId());

		return layoutSetBranch;
	}

	@Override
	public LayoutSetBranch deleteLayoutSetBranch(
			LayoutSetBranch layoutSetBranch)
		throws PortalException {

		return deleteLayoutSetBranch(layoutSetBranch, false);
	}

	@Override
	public LayoutSetBranch deleteLayoutSetBranch(
			LayoutSetBranch layoutSetBranch, boolean includeMaster)
		throws PortalException {

		return deleteLayoutSetBranch(
			LayoutConstants.DEFAULT_PLID, layoutSetBranch, includeMaster);
	}

	@Override
	public LayoutSetBranch deleteLayoutSetBranch(long layoutSetBranchId)
		throws PortalException {

		return deleteLayoutSetBranch(
			LayoutConstants.DEFAULT_PLID, layoutSetBranchId);
	}

	@Override
	public LayoutSetBranch deleteLayoutSetBranch(
			long currentLayoutPlid, LayoutSetBranch layoutSetBranch,
			boolean includeMaster)
		throws PortalException {

		// Layout

		if (!layoutSetBranch.isMaster()) {
			List<Long> deletablePlids = _getDeletablePlids(
				layoutSetBranch.getLayoutSetBranchId());

			if ((currentLayoutPlid != LayoutConstants.DEFAULT_PLID) &&
				deletablePlids.contains(currentLayoutPlid)) {

				throw new PortalException();
			}

			for (long plid : deletablePlids) {
				Layout layout = _layoutPersistence.fetchByPrimaryKey(plid);

				if (layout != null) {
					_layoutLocalService.deleteLayout(layout);
				}
			}
		}

		// Layout branch

		if (!includeMaster && layoutSetBranch.isMaster()) {
			throw new RequiredLayoutSetBranchException();
		}

		layoutSetBranchPersistence.remove(layoutSetBranch);

		// Resources

		_resourceLocalService.deleteResource(
			layoutSetBranch.getCompanyId(), LayoutSetBranch.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			layoutSetBranch.getLayoutSetBranchId());

		// Layout branches

		_layoutBranchLocalService.deleteLayoutSetBranchLayoutBranches(
			layoutSetBranch.getLayoutSetBranchId());

		// Layout revisions

		_layoutRevisionLocalService.deleteLayoutSetBranchLayoutRevisions(
			layoutSetBranch.getLayoutSetBranchId());

		// Recent layout sets

		_recentLayoutSetBranchLocalService.deleteRecentLayoutSetBranches(
			layoutSetBranch.getLayoutSetBranchId());

		return layoutSetBranch;
	}

	@Override
	public LayoutSetBranch deleteLayoutSetBranch(
			long currentLayoutPlid, long layoutSetBranchId)
		throws PortalException {

		LayoutSetBranch layoutSetBranch =
			layoutSetBranchPersistence.findByPrimaryKey(layoutSetBranchId);

		return deleteLayoutSetBranch(currentLayoutPlid, layoutSetBranch, false);
	}

	@Override
	public void deleteLayoutSetBranches(long groupId, boolean privateLayout)
		throws PortalException {

		deleteLayoutSetBranches(groupId, privateLayout, false);
	}

	@Override
	public void deleteLayoutSetBranches(
			long groupId, boolean privateLayout, boolean includeMaster)
		throws PortalException {

		List<LayoutSetBranch> layoutSetBranches =
			layoutSetBranchPersistence.findByG_P(groupId, privateLayout);

		LayoutSetBranch masterLayoutSetBranch = null;

		for (LayoutSetBranch layoutSetBranch : layoutSetBranches) {
			if (layoutSetBranch.isMaster()) {
				masterLayoutSetBranch = layoutSetBranch;
			}
			else {
				deleteLayoutSetBranch(layoutSetBranch, includeMaster);
			}
		}

		if (masterLayoutSetBranch != null) {
			deleteLayoutSetBranch(masterLayoutSetBranch, includeMaster);
		}
	}

	@Override
	public LayoutSetBranch fetchLayoutSetBranch(
		long groupId, boolean privateLayout, String name) {

		return layoutSetBranchPersistence.fetchByG_P_N(
			groupId, privateLayout, name);
	}

	@Override
	public LayoutSetBranch getLayoutSetBranch(
			long groupId, boolean privateLayout, String name)
		throws PortalException {

		return layoutSetBranchPersistence.findByG_P_N(
			groupId, privateLayout, name);
	}

	@Override
	public List<LayoutSetBranch> getLayoutSetBranches(
		long groupId, boolean privateLayout) {

		return layoutSetBranchPersistence.findByG_P(
			groupId, privateLayout, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			LayoutSetBranchCreateDateComparator.getInstance(true));
	}

	@Override
	public LayoutSetBranch getMasterLayoutSetBranch(
			long groupId, boolean privateLayout)
		throws PortalException {

		return layoutSetBranchPersistence.findByG_P_M_First(
			groupId, privateLayout, true, null);
	}

	@Override
	public LayoutSetBranch getUserLayoutSetBranch(
			long userId, long groupId, boolean privateLayout, long layoutSetId,
			long layoutSetBranchId)
		throws PortalException {

		if (layoutSetBranchId <= 0) {
			User user = _userPersistence.findByPrimaryKey(userId);

			if (layoutSetId <= 0) {
				LayoutSet layoutSet = _layoutSetPersistence.findByG_P(
					groupId, privateLayout);

				layoutSetId = layoutSet.getLayoutSetId();
			}

			layoutSetBranchId = StagingUtil.getRecentLayoutSetBranchId(
				user, layoutSetId);
		}

		if (layoutSetBranchId > 0) {
			LayoutSetBranch layoutSetBranch = fetchLayoutSetBranch(
				layoutSetBranchId);

			if (layoutSetBranch != null) {
				return layoutSetBranch;
			}
		}

		return getMasterLayoutSetBranch(groupId, privateLayout);
	}

	@Override
	public LayoutSetBranch mergeLayoutSetBranch(
			long layoutSetBranchId, long mergeLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSetBranch layoutSetBranch =
			layoutSetBranchPersistence.findByPrimaryKey(layoutSetBranchId);
		LayoutSetBranch mergeLayoutSetBranch =
			layoutSetBranchPersistence.findByPrimaryKey(mergeLayoutSetBranchId);

		Locale locale = serviceContext.getLocale();

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale);

		String nowString = dateTimeFormat.format(new Date());

		serviceContext.setWorkflowAction(WorkflowConstants.STATUS_DRAFT);

		List<LayoutRevision> layoutRevisions =
			_layoutRevisionLocalService.getLayoutRevisions(
				mergeLayoutSetBranchId, true);

		for (LayoutRevision layoutRevision : layoutRevisions) {
			LayoutBranch layoutBranch = layoutRevision.getLayoutBranch();

			String layoutBranchName = getLayoutBranchName(
				layoutSetBranch.getLayoutSetBranchId(), locale,
				layoutBranch.getName(), mergeLayoutSetBranch.getName(),
				layoutRevision.getPlid());

			layoutBranch = _layoutBranchLocalService.addLayoutBranch(
				layoutSetBranch.getLayoutSetBranchId(),
				layoutRevision.getPlid(), layoutBranchName,
				StringBundler.concat(
					mergeLayoutSetBranch.getDescription(), StringPool.SPACE,
					LanguageUtil.format(
						locale, "merged-from-x-x",
						new String[] {
							mergeLayoutSetBranch.getName(), nowString
						},
						false)),
				false, serviceContext);

			_layoutRevisionLocalService.addLayoutRevision(
				layoutRevision.getUserId(),
				layoutSetBranch.getLayoutSetBranchId(),
				layoutBranch.getLayoutBranchId(),
				LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
				false, layoutRevision.getPlid(),
				layoutRevision.getLayoutRevisionId(),
				layoutRevision.isPrivateLayout(), layoutRevision.getName(),
				layoutRevision.getTitle(), layoutRevision.getDescription(),
				layoutRevision.getKeywords(), layoutRevision.getRobots(),
				layoutRevision.getTypeSettings(), layoutRevision.getIconImage(),
				layoutRevision.getIconImageId(), layoutRevision.getThemeId(),
				layoutRevision.getColorSchemeId(), layoutRevision.getCss(),
				serviceContext);
		}

		return layoutSetBranch;
	}

	@Override
	public LayoutSetBranch updateLayoutSetBranch(
			long layoutSetBranchId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSetBranch layoutSetBranch =
			layoutSetBranchPersistence.findByPrimaryKey(layoutSetBranchId);

		validate(
			layoutSetBranch.getLayoutSetBranchId(),
			layoutSetBranch.getGroupId(), layoutSetBranch.isPrivateLayout(),
			name, layoutSetBranch.isMaster());

		layoutSetBranch.setName(name);
		layoutSetBranch.setDescription(description);

		return layoutSetBranchPersistence.update(layoutSetBranch);
	}

	protected String getLayoutBranchName(
		long layoutSetBranchId, Locale locale, String mergeBranchName,
		String mergeLayoutSetBranchName, long plid) {

		LayoutBranch layoutBranch = _layoutBranchPersistence.fetchByL_P_N(
			layoutSetBranchId, plid, mergeBranchName);

		if (layoutBranch == null) {
			return mergeBranchName;
		}

		String defaultLayoutBranchName = StringUtil.appendParentheticalSuffix(
			LanguageUtil.get(locale, mergeBranchName),
			LanguageUtil.get(locale, mergeLayoutSetBranchName));

		String layoutBranchName = defaultLayoutBranchName;

		for (int i = 1;; i++) {
			layoutBranch = _layoutBranchPersistence.fetchByL_P_N(
				layoutSetBranchId, plid, layoutBranchName);

			if (layoutBranch == null) {
				break;
			}

			layoutBranchName = defaultLayoutBranchName + StringPool.SPACE + i;
		}

		return layoutBranchName;
	}

	protected void validate(
			long layoutSetBranchId, long groupId, boolean privateLayout,
			String name, boolean master)
		throws PortalException {

		if (Validator.isNull(name) || (name.length() < 4)) {
			throw new LayoutSetBranchNameException(
				LayoutSetBranchNameException.TOO_SHORT);
		}

		if (name.length() > 100) {
			throw new LayoutSetBranchNameException(
				LayoutSetBranchNameException.TOO_LONG);
		}

		try {
			LayoutSetBranch layoutSetBranch =
				layoutSetBranchPersistence.findByG_P_N(
					groupId, privateLayout, name);

			if (layoutSetBranch.getLayoutSetBranchId() != layoutSetBranchId) {
				throw new LayoutSetBranchNameException(
					LayoutSetBranchNameException.DUPLICATE);
			}
		}
		catch (NoSuchLayoutSetBranchException noSuchLayoutSetBranchException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutSetBranchException);
			}
		}

		if (!master) {
			return;
		}

		try {
			LayoutSetBranch masterLayoutSetBranch =
				layoutSetBranchPersistence.findByG_P_M_First(
					groupId, privateLayout, true, null);

			if (layoutSetBranchId !=
					masterLayoutSetBranch.getLayoutSetBranchId()) {

				throw new LayoutSetBranchNameException(
					LayoutSetBranchNameException.MASTER);
			}
		}
		catch (NoSuchLayoutSetBranchException noSuchLayoutSetBranchException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutSetBranchException);
			}
		}
	}

	private List<Long> _getDeletablePlids(long layoutSetBranchId) {
		List<Long> deletablePlids = new ArrayList<>();

		List<Long> relatedPlids = _getRelatedPlids(layoutSetBranchId);

		for (long plid : relatedPlids) {
			boolean deletableLayout = true;

			List<LayoutRevision> layoutRevisions =
				_layoutRevisionPersistence.findByPlid(plid);

			for (LayoutRevision layoutRevision : layoutRevisions) {
				if ((layoutRevision.getStatus() !=
						WorkflowConstants.STATUS_INCOMPLETE) &&
					(layoutRevision.getLayoutSetBranchId() !=
						layoutSetBranchId)) {

					deletableLayout = false;

					break;
				}
			}

			if (deletableLayout) {
				deletablePlids.add(plid);
			}
		}

		return deletablePlids;
	}

	private List<Long> _getRelatedPlids(long layoutSetBranchId) {
		return TransformUtil.transform(
			_layoutBranchPersistence.findByLayoutSetBranchId(layoutSetBranchId),
			layoutBranch -> layoutBranch.getPlid());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetBranchLocalServiceImpl.class);

	@BeanReference(type = ImageLocalService.class)
	private ImageLocalService _imageLocalService;

	@BeanReference(type = LayoutBranchLocalService.class)
	private LayoutBranchLocalService _layoutBranchLocalService;

	@BeanReference(type = LayoutBranchPersistence.class)
	private LayoutBranchPersistence _layoutBranchPersistence;

	@BeanReference(type = LayoutLocalService.class)
	private LayoutLocalService _layoutLocalService;

	@BeanReference(type = LayoutPersistence.class)
	private LayoutPersistence _layoutPersistence;

	@BeanReference(type = LayoutRevisionLocalService.class)
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@BeanReference(type = LayoutRevisionPersistence.class)
	private LayoutRevisionPersistence _layoutRevisionPersistence;

	@BeanReference(type = LayoutSetPersistence.class)
	private LayoutSetPersistence _layoutSetPersistence;

	@BeanReference(type = RecentLayoutSetBranchLocalService.class)
	private RecentLayoutSetBranchLocalService
		_recentLayoutSetBranchLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}