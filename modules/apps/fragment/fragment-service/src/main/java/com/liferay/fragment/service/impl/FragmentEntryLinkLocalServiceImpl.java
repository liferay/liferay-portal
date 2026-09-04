/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.model.FragmentEntryLinkTable;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.base.FragmentEntryLinkLocalServiceBaseImpl;
import com.liferay.fragment.service.persistence.FragmentCollectionPersistence;
import com.liferay.fragment.service.persistence.FragmentEntryPersistence;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntryTable;
import com.liferay.layout.page.template.util.CheckUnlockedLayoutThreadLocal;
import com.liferay.layout.util.CheckNoninstanceablePortletThreadLocal;
import com.liferay.layout.util.UpdateLayoutStatusThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.LimitStep;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "model.class.name=com.liferay.fragment.model.FragmentEntryLink",
	service = AopService.class
)
public class FragmentEntryLinkLocalServiceImpl
	extends FragmentEntryLinkLocalServiceBaseImpl {

	@Override
	public FragmentEntryLink addFragmentEntryLink(
			String externalReferenceCode, long userId, long groupId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long segmentsExperienceId, long plid,
			String css, String html, String js, String configuration,
			String editableValues, String namespace, int position,
			String rendererKey, int type, ServiceContext serviceContext)
		throws PortalException {

		_checkUnlockedLayout(plid, userId);

		User user = _userLocalService.getUser(userId);

		long fragmentEntryLinkId = counterLocalService.increment();

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.create(fragmentEntryLinkId);

		fragmentEntryLink.setUuid(serviceContext.getUuid());
		fragmentEntryLink.setExternalReferenceCode(externalReferenceCode);
		fragmentEntryLink.setGroupId(groupId);
		fragmentEntryLink.setCompanyId(user.getCompanyId());
		fragmentEntryLink.setUserId(user.getUserId());
		fragmentEntryLink.setUserName(user.getFullName());
		fragmentEntryLink.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		fragmentEntryLink.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		fragmentEntryLink.setOriginalFragmentEntryLinkERC(
			originalFragmentEntryLinkERC);
		fragmentEntryLink.setFragmentEntryERC(fragmentEntryERC);
		fragmentEntryLink.setFragmentEntryScopeERC(fragmentEntryScopeERC);
		fragmentEntryLink.setSegmentsExperienceId(segmentsExperienceId);
		fragmentEntryLink.setClassNameId(_portal.getClassNameId(Layout.class));
		fragmentEntryLink.setClassPK(plid);
		fragmentEntryLink.setPlid(plid);
		fragmentEntryLink.setCss(css);

		html = _replaceResources(fragmentEntryLink.fetchFragmentEntry(), html);

		fragmentEntryLink.setHtml(html);

		fragmentEntryLink.setJs(js);
		fragmentEntryLink.setConfiguration(configuration);

		// LPS-110749 Namespace a comment before processing HTML

		if (Validator.isNull(namespace)) {
			namespace = StringUtil.randomId();
		}

		fragmentEntryLink.setNamespace(namespace);

		fragmentEntryLink.setRendererKey(rendererKey);
		fragmentEntryLink.setType(type);

		if (Validator.isNull(editableValues)) {
			editableValues = String.valueOf(
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						_getProcessedHTML(fragmentEntryLink, serviceContext),
						_jsonFactory.safeCreateJSONObject(configuration)));
		}

		fragmentEntryLink.setEditableValues(editableValues);
		fragmentEntryLink.setPosition(position);
		fragmentEntryLink.setLastPropagationDate(
			serviceContext.getCreateDate(new Date()));

		return fragmentEntryLinkPersistence.update(fragmentEntryLink);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public FragmentEntryLink deleteFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink) {

		// Fragment entry link

		fragmentEntryLinkPersistence.remove(fragmentEntryLink);

		return fragmentEntryLink;
	}

	@Override
	public FragmentEntryLink deleteFragmentEntryLink(long fragmentEntryLinkId)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		return fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink);
	}

	@Override
	public FragmentEntryLink deleteFragmentEntryLink(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			getFragmentEntryLinkByExternalReferenceCode(
				externalReferenceCode, groupId));
	}

	@Override
	public void deleteFragmentEntryLinks(long groupId) {
		List<FragmentEntryLink> fragmentEntryLinks =
			fragmentEntryLinkPersistence.findByGroupId(groupId);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLinkLocalService.deleteFragmentEntryLink(
				fragmentEntryLink);
		}
	}

	@Override
	public void deleteFragmentEntryLinks(
		long groupId, long plid, boolean deleted) {

		List<FragmentEntryLink> fragmentEntryLinks =
			fragmentEntryLinkPersistence.findByG_P_D(groupId, plid, deleted);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLinkLocalService.deleteFragmentEntryLink(
				fragmentEntryLink);
		}
	}

	@Override
	public void deleteFragmentEntryLinks(long[] fragmentEntryLinkIds)
		throws PortalException {

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			fragmentEntryLinkLocalService.deleteFragmentEntryLink(
				fragmentEntryLinkId);
		}
	}

	@Override
	public void deleteFragmentEntryLinksByFragmentEntry(
			FragmentEntry fragmentEntry, boolean deleted)
		throws PortalException {

		List<FragmentEntryLink> fragmentEntryLinks =
			fragmentEntryLinkPersistence.findByG_FEERC_FESERC_D(
				fragmentEntry.getGroupId(),
				fragmentEntry.getExternalReferenceCode(), null, deleted);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLinkLocalService.deleteFragmentEntryLink(
				fragmentEntryLink);
		}

		Group group = _groupLocalService.getGroup(fragmentEntry.getGroupId());

		fragmentEntryLinks = fragmentEntryLinkPersistence.findByFEERC_FESERC_D(
			fragmentEntry.getExternalReferenceCode(),
			group.getExternalReferenceCode(), deleted);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLinkLocalService.deleteFragmentEntryLink(
				fragmentEntryLink);
		}
	}

	@Override
	public List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long plid) {

		return TransformUtil.transform(
			getFragmentEntryLinksByPlid(groupId, plid),
			fragmentEntryLink -> {
				fragmentEntryLinkLocalService.deleteFragmentEntryLink(
					fragmentEntryLink);

				if (fragmentEntryLink.isTypePortlet()) {
					try {
						JSONObject jsonObject =
							fragmentEntryLink.getEditableValuesJSONObject();

						String instanceId = jsonObject.getString("instanceId");
						String portletId = jsonObject.getString("portletId");

						if (Validator.isNotNull(instanceId)) {
							portletId = portletId + "_INSTANCE_" + instanceId;
						}

						_portletPreferencesLocalService.
							deletePortletPreferences(
								PortletKeys.PREFS_OWNER_ID_DEFAULT,
								PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
								fragmentEntryLink.getPlid(), portletId);
					}
					catch (PortalException portalException) {
						if (_log.isDebugEnabled()) {
							_log.debug(portalException);
						}
					}
				}

				return fragmentEntryLink;
			});
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #deleteLayoutPageTemplateEntryFragmentEntryLinks(long, long)}
	 */
	@Deprecated
	@Override
	public List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long classNameId, long classPK) {

		return deleteLayoutPageTemplateEntryFragmentEntryLinks(
			groupId, classPK);
	}

	@Override
	public List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long[] segmentsExperienceIds, long plid) {

		return TransformUtil.transform(
			getFragmentEntryLinksBySegmentsExperienceId(
				groupId, segmentsExperienceIds, plid),
			fragmentEntryLink -> {
				fragmentEntryLinkLocalService.deleteFragmentEntryLink(
					fragmentEntryLink);

				return fragmentEntryLink;
			});
	}

	@Override
	public FragmentEntryLink fetchFragmentEntryLink(
		long groupId, String originalFragmentEntryLinkERC, long plid) {

		return fragmentEntryLinkPersistence.fetchByG_OFELERC_P_First(
			groupId, originalFragmentEntryLinkERC, plid, null);
	}

	@Override
	public List<FragmentEntryLink> getAllFragmentEntryLinksByFragmentEntry(
			FragmentEntry fragmentEntry, int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws PortalException {

		return fragmentEntryLinkPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				FragmentEntryLinkTable.INSTANCE
			).from(
				FragmentEntryLinkTable.INSTANCE
			).where(
				_getLatestFragmentEntryLinkPredicate(
					_getAllFragmentEntryLinksByFragmentEntryPredicate(
						fragmentEntry, FragmentEntryLinkTable.INSTANCE))
			).orderBy(
				_getOrderByStepLimitStepFunction(orderByComparator)
			).limit(
				start, end
			));
	}

	@Override
	public int getAllFragmentEntryLinksCountByFragmentEntry(
			FragmentEntry fragmentEntry)
		throws PortalException {

		return fragmentEntryLinkPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				DSLQueryFactoryUtil.selectDistinct(
					FragmentEntryLinkTable.INSTANCE.classPK
				).from(
					FragmentEntryLinkTable.INSTANCE
				).where(
					_getAllFragmentEntryLinksByFragmentEntryPredicate(
						fragmentEntry, FragmentEntryLinkTable.INSTANCE)
				).as(
					"tempFragmentEntryLinkTable"
				)
			));
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getFragmentEntryLinksCountByPlid(long, long)}
	 */
	@Deprecated
	@Override
	public int getClassedModelFragmentEntryLinksCount(
		long groupId, long classNameId, long classPK) {

		return fragmentEntryLinkPersistence.countByG_C_C(
			groupId, classNameId, classPK);
	}

	@Override
	public FragmentEntryLink getFragmentEntryLink(
			long groupId, String originalFragmentEntryLinkERC, long plid)
		throws PortalException {

		return fragmentEntryLinkPersistence.findByG_OFELERC_P_First(
			groupId, originalFragmentEntryLinkERC, plid, null);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinks(
		int type, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return fragmentEntryLinkPersistence.findByType(
			type, start, end, orderByComparator);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getFragmentEntryLinksByPlid(long, long)}
	 */
	@Deprecated
	@Override
	public List<FragmentEntryLink> getFragmentEntryLinks(
		long groupId, long classNameId, long classPK) {

		return fragmentEntryLinkPersistence.findByG_C_C(
			groupId, classNameId, classPK);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinks(
		long companyId, String rendererKey) {

		return fragmentEntryLinkPersistence.findByC_R(companyId, rendererKey);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinks(
		long companyId, String[] rendererKeys) {

		return fragmentEntryLinkPersistence.findByC_R(companyId, rendererKeys);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinks(String rendererKey) {
		return fragmentEntryLinkPersistence.findByRendererKey(rendererKey);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksByFragmentEntry(
			long groupId, FragmentEntry fragmentEntry)
		throws PortalException {

		return fragmentEntryLinkPersistence.findByG_FEERC_FESERC(
			groupId, fragmentEntry.getExternalReferenceCode(),
			ScopeUtil.getItemScopeExternalReferenceCode(
				fragmentEntry.getGroupId(), groupId));
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksByPlid(
		long groupId, long plid) {

		return fragmentEntryLinkPersistence.findByG_P(groupId, plid);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long segmentsExperienceId, long plid) {

		return fragmentEntryLinkPersistence.findByG_S_P(
			groupId, segmentsExperienceId, plid);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long segmentsExperienceId, long plid, boolean deleted) {

		return fragmentEntryLinkPersistence.findByG_S_P_D(
			groupId, segmentsExperienceId, plid, deleted);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long segmentsExperienceId, long plid,
		String rendererKey) {

		return fragmentEntryLinkPersistence.findByG_S_P_R(
			groupId, segmentsExperienceId, plid, rendererKey);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long[] segmentsExperienceIds, long plid) {

		if (ArrayUtil.isEmpty(segmentsExperienceIds)) {
			return Collections.emptyList();
		}

		return fragmentEntryLinkPersistence.findByG_S_P(
			groupId, segmentsExperienceIds, plid);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long[] segmentsExperienceIds, long plid,
		boolean deleted) {

		if (ArrayUtil.isEmpty(segmentsExperienceIds)) {
			return Collections.emptyList();
		}

		return fragmentEntryLinkPersistence.findByG_S_P_D(
			groupId, segmentsExperienceIds, plid, deleted);
	}

	@Override
	public int getFragmentEntryLinksCountByFragmentEntry(
			long groupId, FragmentEntry fragmentEntry, boolean deleted)
		throws PortalException {

		return fragmentEntryLinkPersistence.countByG_FEERC_FESERC_D(
			groupId, fragmentEntry.getExternalReferenceCode(),
			ScopeUtil.getItemScopeExternalReferenceCode(
				fragmentEntry.getGroupId(), groupId),
			deleted);
	}

	@Override
	public int getFragmentEntryLinksCountByPlid(long groupId, long plid) {
		return fragmentEntryLinkPersistence.countByG_P(groupId, plid);
	}

	@Override
	public List<FragmentEntryLink> getLayoutFragmentEntryLinksByFragmentEntry(
			long groupId, FragmentEntry fragmentEntry, int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws PortalException {

		return fragmentEntryLinkPersistence.dslQuery(
			_getLayoutFragmentEntryLinksByFragmentEntryGroupByStep(
				fragmentEntry,
				DSLQueryFactoryUtil.select(FragmentEntryLinkTable.INSTANCE),
				true, groupId
			).orderBy(
				_getOrderByStepLimitStepFunction(orderByComparator)
			).limit(
				start, end
			));
	}

	@Override
	public int getLayoutFragmentEntryLinksCountByFragmentEntry(
			long groupId, FragmentEntry fragmentEntry)
		throws PortalException {

		return fragmentEntryLinkPersistence.dslQueryCount(
			_getLayoutFragmentEntryLinksByFragmentEntryGroupByStep(
				fragmentEntry,
				DSLQueryFactoryUtil.countDistinct(
					FragmentEntryLinkTable.INSTANCE.plid),
				false, groupId));
	}

	@Override
	public List<FragmentEntryLink>
			getLayoutPageTemplateFragmentEntryLinksByFragmentEntry(
				long groupId, FragmentEntry fragmentEntry,
				int layoutPageTemplateType, int start, int end,
				OrderByComparator<FragmentEntryLink> orderByComparator)
		throws PortalException {

		return fragmentEntryLinkPersistence.dslQuery(
			_getLayoutPageTemplateFragmentEntryLinksByFragmentEntryGroupByStep(
				fragmentEntry,
				DSLQueryFactoryUtil.select(FragmentEntryLinkTable.INSTANCE),
				layoutPageTemplateType, true, groupId
			).orderBy(
				_getOrderByStepLimitStepFunction(orderByComparator)
			).limit(
				start, end
			));
	}

	@Override
	public int getLayoutPageTemplateFragmentEntryLinksCountByFragmentEntry(
			long groupId, FragmentEntry fragmentEntry,
			int layoutPageTemplateType)
		throws PortalException {

		return fragmentEntryLinkPersistence.dslQueryCount(
			_getLayoutPageTemplateFragmentEntryLinksByFragmentEntryGroupByStep(
				fragmentEntry,
				DSLQueryFactoryUtil.countDistinct(
					FragmentEntryLinkTable.INSTANCE.plid),
				layoutPageTemplateType, false, groupId));
	}

	@Override
	public void updateClassedModel(long userId, long plid) {
		if (UpdateLayoutStatusThreadLocal.isUpdateLayoutStatus()) {
			try {
				_layoutLocalService.updateStatus(
					userId, plid, WorkflowConstants.STATUS_DRAFT,
					ServiceContextThreadLocal.getServiceContext());
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}
	}

	@Override
	public FragmentEntryLink updateDeleted(
			long userId, long fragmentEntryLinkId, boolean deleted)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		_checkUnlockedLayout(fragmentEntryLink.getPlid(), userId);

		fragmentEntryLink.setDeleted(deleted);

		return fragmentEntryLinkPersistence.update(fragmentEntryLink);
	}

	@Override
	public FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId, String editableValues,
			boolean updateClassedModel)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		_checkUnlockedLayout(fragmentEntryLink.getPlid(), userId);

		fragmentEntryLink.setEditableValues(editableValues);

		if (updateClassedModel) {
			updateClassedModel(userId, fragmentEntryLink.getPlid());
		}

		return fragmentEntryLinkPersistence.update(fragmentEntryLink);
	}

	@Override
	public FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long plid, String css, String html,
			String js, String configuration, String editableValues,
			String namespace, int position, int type,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_checkUnlockedLayout(plid, userId);

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		fragmentEntryLink.setUserId(user.getUserId());
		fragmentEntryLink.setUserName(user.getFullName());
		fragmentEntryLink.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		fragmentEntryLink.setOriginalFragmentEntryLinkERC(
			originalFragmentEntryLinkERC);
		fragmentEntryLink.setFragmentEntryERC(fragmentEntryERC);
		fragmentEntryLink.setFragmentEntryScopeERC(fragmentEntryScopeERC);
		fragmentEntryLink.setClassNameId(_portal.getClassNameId(Layout.class));
		fragmentEntryLink.setClassPK(plid);
		fragmentEntryLink.setPlid(plid);
		fragmentEntryLink.setCss(css);
		fragmentEntryLink.setHtml(html);
		fragmentEntryLink.setJs(js);
		fragmentEntryLink.setConfiguration(configuration);
		fragmentEntryLink.setEditableValues(editableValues);

		if (Validator.isNotNull(namespace)) {
			fragmentEntryLink.setNamespace(namespace);
		}

		fragmentEntryLink.setPosition(position);
		fragmentEntryLink.setType(type);

		return fragmentEntryLinkPersistence.update(fragmentEntryLink);
	}

	@Override
	public void updateLatestChanges(
			FragmentEntry fragmentEntry, FragmentEntryLink fragmentEntryLink)
		throws PortalException {

		if (!_isValidFragmentEntry(fragmentEntry, fragmentEntryLink)) {
			throw new UnsupportedOperationException(
				"Unable to propagate fragment entry " +
					fragmentEntry.getFragmentEntryId());
		}

		boolean modified = false;

		// LPS-132154 Set configuration before processing the HTML

		if (!Objects.equals(
				fragmentEntryLink.getConfiguration(),
				fragmentEntry.getConfiguration())) {

			fragmentEntryLink.setConfiguration(
				fragmentEntry.getConfiguration());

			modified = true;
		}

		String html = _replaceResources(fragmentEntry, fragmentEntry.getHtml());

		if (!Objects.equals(fragmentEntryLink.getHtml(), html)) {
			JSONObject editableValuesJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			fragmentEntryLink.setHtml(html);

			fragmentEntryLink.setEditableValues(
				_fragmentEntryProcessorRegistry.mergeDefaultEditableValues(
					fragmentEntryLink.getConfigurationJSONObject(),
					editableValuesJSONObject,
					_getProcessedHTML(
						fragmentEntryLink,
						ServiceContextThreadLocal.getServiceContext())));

			modified = true;
		}

		if (!Objects.equals(
				fragmentEntryLink.getCss(), fragmentEntry.getCss())) {

			fragmentEntryLink.setCss(fragmentEntry.getCss());

			modified = true;
		}

		if (!Objects.equals(fragmentEntryLink.getJs(), fragmentEntry.getJs())) {
			fragmentEntryLink.setJs(fragmentEntry.getJs());

			modified = true;
		}

		if (fragmentEntryLink.getType() != fragmentEntry.getType()) {
			fragmentEntryLink.setType(fragmentEntry.getType());

			modified = true;
		}

		fragmentEntryLink.setLastPropagationDate(new Date());

		fragmentEntryLink = fragmentEntryLinkPersistence.update(
			fragmentEntryLink);

		if (modified) {
			try (SafeCloseable safeCloseable1 =
					CheckNoninstanceablePortletThreadLocal.
						setCheckNoninstanceablePortletWithSafeCloseable(true);
				SafeCloseable safeCloseable2 =
					UpdateLayoutStatusThreadLocal.
						setUpdateLayoutStatusWithSafeCloseable(false)) {

				_updateFragmentEntryLinkLayout(fragmentEntryLink);

				for (FragmentEntryLinkListener fragmentEntryLinkListener :
						_fragmentEntryLinkListenerRegistry.
							getFragmentEntryLinkListeners()) {

					fragmentEntryLinkListener.
						onUpdateFragmentEntryLinkConfigurationValues(
							fragmentEntryLink);
				}
			}
		}
	}

	@Override
	public void updateLatestChanges(long fragmentEntryLinkId)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink =
			fragmentEntryLinkPersistence.findByPrimaryKey(fragmentEntryLinkId);

		FragmentEntry fragmentEntry = fragmentEntryLink.fetchFragmentEntry();

		if (fragmentEntry == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to propagate fragment entry link " +
						fragmentEntryLinkId +
							" because its fragment entry is missing");
			}

			return;
		}

		updateLatestChanges(
			_fragmentEntryPersistence.findByPrimaryKey(
				fragmentEntry.getFragmentEntryId()),
			fragmentEntryLink);
	}

	private void _checkUnlockedLayout(long plid, long userId)
		throws PortalException {

		if (!CheckUnlockedLayoutThreadLocal.isCheckUnlockedLayout()) {
			return;
		}

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if ((layout != null) && !layout.isUnlocked(Constants.EDIT, userId)) {
			throw new LockedLayoutException();
		}
	}

	private Predicate _getAllFragmentEntryLinksByFragmentEntryPredicate(
			FragmentEntry fragmentEntry,
			FragmentEntryLinkTable fragmentEntryLinkTable)
		throws PortalException {

		Group group = _groupLocalService.getGroup(fragmentEntry.getGroupId());

		return fragmentEntryLinkTable.fragmentEntryERC.eq(
			fragmentEntry.getExternalReferenceCode()
		).and(
			Predicate.withParentheses(
				fragmentEntryLinkTable.fragmentEntryScopeERC.eq(
					group.getExternalReferenceCode()
				).or(
					Predicate.withParentheses(
						fragmentEntryLinkTable.fragmentEntryScopeERC.isNull(
						).and(
							fragmentEntryLinkTable.groupId.eq(
								group.getGroupId())
						))
				))
		).and(
			fragmentEntryLinkTable.deleted.eq(false)
		);
	}

	private Predicate _getFragmentEntryLinksByFragmentEntryPredicate(
			FragmentEntry fragmentEntry, Predicate predicate, long scopeGroupId)
		throws PortalException {

		String fragmentEntryScopeERC =
			ScopeUtil.getItemScopeExternalReferenceCode(
				fragmentEntry.getGroupId(), scopeGroupId);

		if (Validator.isNotNull(fragmentEntryScopeERC)) {
			return FragmentEntryLinkTable.INSTANCE.groupId.eq(
				scopeGroupId
			).and(
				FragmentEntryLinkTable.INSTANCE.fragmentEntryERC.eq(
					fragmentEntry.getExternalReferenceCode())
			).and(
				FragmentEntryLinkTable.INSTANCE.fragmentEntryScopeERC.eq(
					fragmentEntryScopeERC)
			).and(
				FragmentEntryLinkTable.INSTANCE.deleted.eq(false)
			).and(
				predicate
			);
		}

		return FragmentEntryLinkTable.INSTANCE.groupId.eq(
			scopeGroupId
		).and(
			FragmentEntryLinkTable.INSTANCE.fragmentEntryERC.eq(
				fragmentEntry.getExternalReferenceCode())
		).and(
			FragmentEntryLinkTable.INSTANCE.fragmentEntryScopeERC.isNull()
		).and(
			FragmentEntryLinkTable.INSTANCE.deleted.eq(false)
		).and(
			predicate
		);
	}

	private Predicate _getLatestFragmentEntryLinkPredicate(
		Predicate predicate) {

		return FragmentEntryLinkTable.INSTANCE.fragmentEntryLinkId.in(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.max(
					FragmentEntryLinkTable.INSTANCE.fragmentEntryLinkId)
			).from(
				FragmentEntryLinkTable.INSTANCE
			).where(
				predicate
			).groupBy(
				FragmentEntryLinkTable.INSTANCE.plid
			));
	}

	private GroupByStep _getLayoutFragmentEntryLinksByFragmentEntryGroupByStep(
			FragmentEntry fragmentEntry, FromStep fromStep, boolean latest,
			long scopeGroupId)
		throws PortalException {

		Predicate predicate = _getFragmentEntryLinksByFragmentEntryPredicate(
			fragmentEntry,
			FragmentEntryLinkTable.INSTANCE.plid.notIn(_getPlidsDSLQuery(null)),
			scopeGroupId);

		if (latest) {
			return fromStep.from(
				FragmentEntryLinkTable.INSTANCE
			).where(
				_getLatestFragmentEntryLinkPredicate(predicate)
			);
		}

		return fromStep.from(
			FragmentEntryLinkTable.INSTANCE
		).where(
			predicate
		);
	}

	private GroupByStep
			_getLayoutPageTemplateFragmentEntryLinksByFragmentEntryGroupByStep(
				FragmentEntry fragmentEntry, FromStep fromStep,
				int layoutPageTemplateType, boolean latest, long scopeGroupId)
		throws PortalException {

		Predicate predicate = _getFragmentEntryLinksByFragmentEntryPredicate(
			fragmentEntry,
			FragmentEntryLinkTable.INSTANCE.plid.in(
				_getPlidsDSLQuery(
					LayoutPageTemplateEntryTable.INSTANCE.type.eq(
						layoutPageTemplateType))),
			scopeGroupId);

		if (latest) {
			return fromStep.from(
				FragmentEntryLinkTable.INSTANCE
			).where(
				_getLatestFragmentEntryLinkPredicate(predicate)
			);
		}

		return fromStep.from(
			FragmentEntryLinkTable.INSTANCE
		).where(
			predicate
		);
	}

	private Function<OrderByStep, LimitStep> _getOrderByStepLimitStepFunction(
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return orderByStep -> {
			if (orderByComparator == null) {
				return orderByStep.orderBy(
					FragmentEntryLinkTable.INSTANCE.lastPropagationDate.
						descending());
			}

			return orderByStep.orderBy(
				FragmentEntryLinkTable.INSTANCE, orderByComparator);
		};
	}

	private DSLQuery _getPlidsDSLQuery(Predicate predicate) {
		return DSLQueryFactoryUtil.select(
			LayoutTable.INSTANCE.plid
		).from(
			LayoutTable.INSTANCE
		).innerJoinON(
			LayoutPageTemplateEntryTable.INSTANCE,
			LayoutTable.INSTANCE.plid.eq(
				LayoutPageTemplateEntryTable.INSTANCE.plid
			).or(
				LayoutTable.INSTANCE.classPK.eq(
					LayoutPageTemplateEntryTable.INSTANCE.plid)
			)
		).where(
			predicate
		);
	}

	private String _getProcessedHTML(
			FragmentEntryLink fragmentEntryLink, ServiceContext serviceContext)
		throws PortalException {

		if (serviceContext == null) {
			return fragmentEntryLink.getHtml();
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if ((httpServletRequest == null) && (themeDisplay != null)) {
			httpServletRequest = themeDisplay.getRequest();
		}

		HttpServletResponse httpServletResponse = serviceContext.getResponse();

		if ((httpServletResponse == null) && (themeDisplay != null)) {
			httpServletResponse = themeDisplay.getResponse();
		}

		if ((httpServletRequest == null) || (httpServletResponse == null)) {
			return fragmentEntryLink.getHtml();
		}

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				fragmentEntryLink.getCompanyId(), httpServletRequest,
				httpServletResponse, LocaleUtil.getMostRelevantLocale(),
				FragmentEntryLinkConstants.EDIT,
				fragmentEntryLink.getGroupId());

		return _fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
			_jsonFactory.createJSONObject(), fragmentEntryLink,
			fragmentEntryProcessorContext);
	}

	private boolean _isValidFragmentEntry(
		FragmentEntry fragmentEntry, FragmentEntryLink fragmentEntryLink) {

		FragmentEntry fragmentEntryLinkFragmentEntry =
			fragmentEntryLink.fetchFragmentEntry();

		if ((fragmentEntryLinkFragmentEntry != null) &&
			(fragmentEntry.getFragmentEntryId() ==
				fragmentEntryLinkFragmentEntry.getFragmentEntryId())) {

			return true;
		}

		if ((fragmentEntryLinkFragmentEntry == null) &&
			Validator.isNotNull(fragmentEntryLink.getFragmentEntryERC())) {

			return false;
		}

		return Objects.equals(
			fragmentEntry.getFragmentEntryKey(),
			fragmentEntryLink.getRendererKey());
	}

	private String _replaceResources(FragmentEntry fragmentEntry, String html)
		throws PortalException {

		if (fragmentEntry == null) {
			return html;
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionPersistence.fetchByPrimaryKey(
				fragmentEntry.getFragmentCollectionId());

		Matcher matcher = _pattern.matcher(html);

		while (matcher.find()) {
			FileEntry fileEntry = fragmentCollection.getResource(
				matcher.group(1));

			String fileEntryURL = StringPool.BLANK;

			if (fileEntry != null) {
				fileEntryURL = _dlURLHelper.getDownloadURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK, false, false);
			}

			html = StringUtil.replace(html, matcher.group(), fileEntryURL);
		}

		return html;
	}

	private void _updateFragmentEntryLinkLayout(
		FragmentEntryLink fragmentEntryLink) {

		Layout layout = _layoutLocalService.fetchLayout(
			fragmentEntryLink.getPlid());

		if (layout == null) {
			return;
		}

		layout.setModifiedDate(new Date());

		_layoutLocalService.updateLayout(layout);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLinkLocalServiceImpl.class);

	private static final Pattern _pattern = Pattern.compile(
		"\\[resources:(.+?)\\]");

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private FragmentCollectionPersistence _fragmentCollectionPersistence;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryPersistence _fragmentEntryPersistence;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private UserLocalService _userLocalService;

}