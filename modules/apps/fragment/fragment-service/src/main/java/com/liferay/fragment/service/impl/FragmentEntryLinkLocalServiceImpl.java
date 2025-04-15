/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
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
import com.liferay.layout.util.CheckNoninstanceablePortletThreadLocal;
import com.liferay.layout.util.UpdateLayoutStatusThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
			long originalFragmentEntryLinkId, long fragmentEntryId,
			long segmentsExperienceId, long plid, String css, String html,
			String js, String configuration, String editableValues,
			String namespace, int position, String rendererKey, int type,
			ServiceContext serviceContext)
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
		fragmentEntryLink.setOriginalFragmentEntryLinkId(
			originalFragmentEntryLinkId);
		fragmentEntryLink.setFragmentEntryId(fragmentEntryId);
		fragmentEntryLink.setSegmentsExperienceId(segmentsExperienceId);
		fragmentEntryLink.setClassNameId(_portal.getClassNameId(Layout.class));
		fragmentEntryLink.setClassPK(plid);
		fragmentEntryLink.setPlid(plid);
		fragmentEntryLink.setCss(css);

		html = _replaceResources(fragmentEntryId, html);

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

		String processedHTML = html;

		HttpServletRequest httpServletRequest = serviceContext.getRequest();
		HttpServletResponse httpServletResponse = serviceContext.getResponse();

		if ((httpServletRequest != null) && (httpServletResponse != null)) {
			DefaultFragmentEntryProcessorContext
				defaultFragmentEntryProcessorContext =
					new DefaultFragmentEntryProcessorContext(
						httpServletRequest, httpServletResponse,
						FragmentEntryLinkConstants.EDIT,
						LocaleUtil.getMostRelevantLocale());

			processedHTML =
				_fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
					fragmentEntryLink, defaultFragmentEntryProcessorContext);
		}

		if (Validator.isNull(editableValues)) {
			editableValues = String.valueOf(
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						processedHTML, configuration));
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
	public void deleteFragmentEntryLinksByFragmentEntryId(
		long fragmentEntryId) {

		List<FragmentEntryLink> fragmentEntryLinks =
			fragmentEntryLinkPersistence.findByFragmentEntryId(fragmentEntryId);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLinkLocalService.deleteFragmentEntryLink(
				fragmentEntryLink);
		}
	}

	@Override
	public void deleteFragmentEntryLinksByFragmentEntryId(
		long fragmentEntryId, boolean deleted) {

		List<FragmentEntryLink> fragmentEntryLinks =
			fragmentEntryLinkPersistence.findByF_D(fragmentEntryId, deleted);

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
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							fragmentEntryLink.getEditableValues());

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
	public List<FragmentEntryLink> getAllFragmentEntryLinksByFragmentEntryId(
		long groupId, long fragmentEntryId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return fragmentEntryLinkFinder.findByG_F(
			groupId, fragmentEntryId, start, end, orderByComparator);
	}

	@Override
	public int getAllFragmentEntryLinksCountByFragmentEntryId(
		long groupId, long fragmentEntryId) {

		return fragmentEntryLinkPersistence.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				DSLQueryFactoryUtil.selectDistinct(
					FragmentEntryLinkTable.INSTANCE.classNameId,
					FragmentEntryLinkTable.INSTANCE.classPK
				).from(
					FragmentEntryLinkTable.INSTANCE
				).where(
					FragmentEntryLinkTable.INSTANCE.groupId.eq(
						groupId
					).and(
						FragmentEntryLinkTable.INSTANCE.fragmentEntryId.eq(
							fragmentEntryId)
					).and(
						FragmentEntryLinkTable.INSTANCE.deleted.eq(false)
					)
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
		long groupId, long originalFragmentEntryLinkId, long plid) {

		return fragmentEntryLinkPersistence.fetchByG_OFELI_P_First(
			groupId, originalFragmentEntryLinkId, plid, null);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinks(
		int type, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		List<FragmentEntry> fragmentEntries =
			_fragmentEntryPersistence.findByType(type);

		if (fragmentEntries.isEmpty()) {
			return Collections.emptyList();
		}

		return fragmentEntryLinkPersistence.findByFragmentEntryId(
			ListUtil.toLongArray(
				fragmentEntries, FragmentEntry.FRAGMENT_ENTRY_ID_ACCESSOR),
			start, end, orderByComparator);
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
	public List<FragmentEntryLink> getFragmentEntryLinksByFragmentEntryId(
		long fragmentEntryId) {

		return fragmentEntryLinkPersistence.findByFragmentEntryId(
			fragmentEntryId);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksByFragmentEntryId(
		long fragmentEntryId, boolean deleted) {

		return fragmentEntryLinkPersistence.findByF_D(fragmentEntryId, deleted);
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

		return fragmentEntryLinkPersistence.findByG_S_P(
			groupId, segmentsExperienceIds, plid);
	}

	@Override
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long[] segmentsExperienceIds, long plid,
		boolean deleted) {

		return fragmentEntryLinkPersistence.findByG_S_P_D(
			groupId, segmentsExperienceIds, plid, deleted);
	}

	@Override
	public int getFragmentEntryLinksCountByFragmentEntryId(
		long fragmentEntryId) {

		return fragmentEntryLinkPersistence.countByFragmentEntryId(
			fragmentEntryId);
	}

	@Override
	public int getFragmentEntryLinksCountByFragmentEntryId(
		long fragmentEntryId, boolean deleted) {

		return fragmentEntryLinkPersistence.countByF_D(
			fragmentEntryId, deleted);
	}

	@Override
	public int getFragmentEntryLinksCountByFragmentEntryId(
		long groupId, long fragmentEntryId, boolean deleted) {

		return fragmentEntryLinkPersistence.countByG_F_D(
			groupId, fragmentEntryId, deleted);
	}

	@Override
	public int getFragmentEntryLinksCountByPlid(long groupId, long plid) {
		return fragmentEntryLinkPersistence.countByG_P(groupId, plid);
	}

	@Override
	public List<FragmentEntryLink> getLayoutFragmentEntryLinksByFragmentEntryId(
		long groupId, long fragmentEntryId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return fragmentEntryLinkFinder.findByG_F_P_L(
			groupId, fragmentEntryId, -1, start, end, orderByComparator);
	}

	@Override
	public int getLayoutFragmentEntryLinksCountByFragmentEntryId(
		long groupId, long fragmentEntryId) {

		Table<LayoutTable> tempLayoutTableTable = DSLQueryFactoryUtil.select(
			LayoutTable.INSTANCE.plid
		).from(
			LayoutTable.INSTANCE
		).leftJoinOn(
			LayoutPageTemplateEntryTable.INSTANCE,
			LayoutTable.INSTANCE.plid.eq(
				LayoutPageTemplateEntryTable.INSTANCE.plid
			).or(
				LayoutTable.INSTANCE.classPK.eq(
					LayoutPageTemplateEntryTable.INSTANCE.plid)
			)
		).where(
			LayoutPageTemplateEntryTable.INSTANCE.plid.isNull()
		).as(
			"tempLayoutTable", LayoutTable.INSTANCE
		);

		return fragmentEntryLinkPersistence.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				FragmentEntryLinkTable.INSTANCE.plid
			).from(
				FragmentEntryLinkTable.INSTANCE
			).innerJoinON(
				tempLayoutTableTable,
				FragmentEntryLinkTable.INSTANCE.plid.eq(
					(Expression<Long>)tempLayoutTableTable.getColumn("plid"))
			).where(
				FragmentEntryLinkTable.INSTANCE.groupId.eq(
					groupId
				).and(
					FragmentEntryLinkTable.INSTANCE.fragmentEntryId.eq(
						fragmentEntryId)
				).and(
					FragmentEntryLinkTable.INSTANCE.deleted.eq(false)
				)
			));
	}

	@Override
	public List<FragmentEntryLink>
		getLayoutPageTemplateFragmentEntryLinksByFragmentEntryId(
			long groupId, long fragmentEntryId, int layoutPageTemplateType,
			int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator) {

		return fragmentEntryLinkFinder.findByG_F_P_L(
			groupId, fragmentEntryId, layoutPageTemplateType, start, end,
			orderByComparator);
	}

	@Override
	public int getLayoutPageTemplateFragmentEntryLinksCountByFragmentEntryId(
		long groupId, long fragmentEntryId, int layoutPageTemplateType) {

		Table<LayoutTable> tempLayoutTableTable = DSLQueryFactoryUtil.select(
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
			LayoutPageTemplateEntryTable.INSTANCE.type.eq(
				layoutPageTemplateType)
		).as(
			"tempLayoutTable", LayoutTable.INSTANCE
		);

		return fragmentEntryLinkPersistence.dslQueryCount(
			DSLQueryFactoryUtil.countDistinct(
				FragmentEntryLinkTable.INSTANCE.plid
			).from(
				FragmentEntryLinkTable.INSTANCE
			).innerJoinON(
				tempLayoutTableTable,
				FragmentEntryLinkTable.INSTANCE.plid.eq(
					(Expression<Long>)tempLayoutTableTable.getColumn("plid"))
			).where(
				FragmentEntryLinkTable.INSTANCE.groupId.eq(
					groupId
				).and(
					FragmentEntryLinkTable.INSTANCE.fragmentEntryId.eq(
						fragmentEntryId)
				).and(
					FragmentEntryLinkTable.INSTANCE.deleted.eq(false)
				)
			));
	}

	@Override
	public void updateClassedModel(long plid) {
		if (UpdateLayoutStatusThreadLocal.isUpdateLayoutStatus()) {
			try {
				_layoutLocalService.updateStatus(
					PrincipalThreadLocal.getUserId(), plid,
					WorkflowConstants.STATUS_DRAFT,
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
			long userId, long fragmentEntryLinkId, int position)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink = fetchFragmentEntryLink(
			fragmentEntryLinkId);

		_checkUnlockedLayout(fragmentEntryLink.getPlid(), userId);

		fragmentEntryLink.setPosition(position);

		return fragmentEntryLinkPersistence.update(fragmentEntryLink);
	}

	@Override
	public FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId,
			long originalFragmentEntryLinkId, long fragmentEntryId, long plid,
			String css, String html, String js, String configuration,
			String editableValues, String namespace, int position, int type,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_checkUnlockedLayout(plid, userId);

		FragmentEntryLink fragmentEntryLink = fetchFragmentEntryLink(
			fragmentEntryLinkId);

		fragmentEntryLink.setUserId(user.getUserId());
		fragmentEntryLink.setUserName(user.getFullName());
		fragmentEntryLink.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		fragmentEntryLink.setOriginalFragmentEntryLinkId(
			originalFragmentEntryLinkId);
		fragmentEntryLink.setFragmentEntryId(fragmentEntryId);
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
	public FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId, String editableValues)
		throws PortalException {

		return updateFragmentEntryLink(
			userId, fragmentEntryLinkId, editableValues, true);
	}

	@Override
	public FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId, String editableValues,
			boolean updateClassedModel)
		throws PortalException {

		FragmentEntryLink fragmentEntryLink = fetchFragmentEntryLink(
			fragmentEntryLinkId);

		_checkUnlockedLayout(fragmentEntryLink.getPlid(), userId);

		fragmentEntryLink.setEditableValues(editableValues);

		if (updateClassedModel) {
			updateClassedModel(fragmentEntryLink.getPlid());
		}

		return fragmentEntryLinkPersistence.update(fragmentEntryLink);
	}

	@Override
	public void updateLatestChanges(
			FragmentEntry fragmentEntry, FragmentEntryLink fragmentEntryLink)
		throws PortalException {

		long fragmentEntryId = fragmentEntryLink.getFragmentEntryId();

		if ((fragmentEntryId != fragmentEntry.getFragmentEntryId()) ||
			((fragmentEntryId == 0) &&
			 !Objects.equals(
				 fragmentEntry.getFragmentEntryKey(),
				 fragmentEntryLink.getRendererKey()))) {

			throw new UnsupportedOperationException(
				"Unable to propagate fragment entry " + fragmentEntryId);
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

		String html = _replaceResources(
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml());

		if (!Objects.equals(fragmentEntryLink.getHtml(), html)) {
			fragmentEntryLink.setHtml(html);

			String defaultEditableValues = String.valueOf(
				_fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(
						_getProcessedHTML(
							fragmentEntryLink,
							ServiceContextThreadLocal.getServiceContext()),
						fragmentEntryLink.getConfiguration()));

			String newEditableValues = _mergeEditableValues(
				defaultEditableValues, fragmentEntryLink.getEditableValues());

			fragmentEntryLink.setEditableValues(newEditableValues);

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
			try (SafeCloseable safeCloseable =
					CheckNoninstanceablePortletThreadLocal.
						setCheckNoninstanceablePortletWithSafeCloseable(true)) {

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

		FragmentEntry fragmentEntry =
			_fragmentEntryPersistence.findByPrimaryKey(
				fragmentEntryLink.getFragmentEntryId());

		updateLatestChanges(fragmentEntry, fragmentEntryLink);
	}

	private void _checkUnlockedLayout(long plid, long userId)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if ((layout != null) && !layout.isUnlocked(Constants.EDIT, userId)) {
			throw new LockedLayoutException();
		}
	}

	private String _getProcessedHTML(
			FragmentEntryLink fragmentEntryLink, ServiceContext serviceContext)
		throws PortalException {

		if (serviceContext == null) {
			return fragmentEntryLink.getHtml();
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();
		HttpServletResponse httpServletResponse = serviceContext.getResponse();

		if ((httpServletRequest == null) || (httpServletResponse == null)) {
			return fragmentEntryLink.getHtml();
		}

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				httpServletRequest, httpServletResponse,
				FragmentEntryLinkConstants.EDIT,
				LocaleUtil.getMostRelevantLocale());

		return _fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
			fragmentEntryLink, fragmentEntryProcessorContext);
	}

	private String _mergeEditableValues(
		String defaultEditableValues, String editableValues) {

		try {
			JSONObject defaultEditableValuesJSONObject =
				_jsonFactory.createJSONObject(defaultEditableValues);

			JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
				editableValues);

			for (String fragmentEntryProcessorKey :
					_FRAGMENT_ENTRY_PROCESSOR_KEYS) {

				JSONObject editableFragmentEntryProcessorJSONObject =
					editableValuesJSONObject.getJSONObject(
						fragmentEntryProcessorKey);

				if (editableFragmentEntryProcessorJSONObject == null) {
					editableFragmentEntryProcessorJSONObject =
						_jsonFactory.createJSONObject();
				}

				JSONObject defaultEditableFragmentEntryProcessorJSONObject =
					defaultEditableValuesJSONObject.getJSONObject(
						fragmentEntryProcessorKey);

				if (defaultEditableFragmentEntryProcessorJSONObject == null) {
					continue;
				}

				Iterator<String> defaultEditableValuesIterator =
					defaultEditableFragmentEntryProcessorJSONObject.keys();

				while (defaultEditableValuesIterator.hasNext()) {
					String key = defaultEditableValuesIterator.next();

					if (editableFragmentEntryProcessorJSONObject.has(key)) {
						defaultEditableFragmentEntryProcessorJSONObject.put(
							key,
							editableFragmentEntryProcessorJSONObject.get(key));
					}
				}

				Iterator<String> editableValuesIterator =
					editableFragmentEntryProcessorJSONObject.keys();

				while (editableValuesIterator.hasNext()) {
					String key = editableValuesIterator.next();

					if (!defaultEditableFragmentEntryProcessorJSONObject.has(
							key)) {

						defaultEditableFragmentEntryProcessorJSONObject.put(
							key,
							editableFragmentEntryProcessorJSONObject.get(key));
					}
				}

				editableValuesJSONObject.put(
					fragmentEntryProcessorKey,
					defaultEditableFragmentEntryProcessorJSONObject);
			}

			return editableValuesJSONObject.toString();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}

		return editableValues;
	}

	private String _replaceResources(long fragmentEntryId, String html)
		throws PortalException {

		FragmentEntry fragmentEntry =
			_fragmentEntryPersistence.fetchByPrimaryKey(fragmentEntryId);

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

	private static final String[] _FRAGMENT_ENTRY_PROCESSOR_KEYS = {
		FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
	};

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