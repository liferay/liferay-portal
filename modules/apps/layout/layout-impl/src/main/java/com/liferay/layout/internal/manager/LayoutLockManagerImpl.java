/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.manager;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.configuration.LockedLayoutsGroupConfiguration;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.model.LockedLayout;
import com.liferay.layout.model.LockedLayoutType;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.lock.model.LockTable;
import com.liferay.portal.lock.service.LockLocalService;
import com.liferay.portal.model.impl.LayoutModelImpl;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "lock.expiration.time=", service = LayoutLockManager.class
)
public class LayoutLockManagerImpl implements LayoutLockManager {

	@Override
	public void getLock(ActionRequest actionRequest) throws PortalException {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		getLock(themeDisplay.getLayout(), themeDisplay.getUserId());
	}

	@Override
	public void getLock(Layout layout, long userId) throws PortalException {
		if ((layout == null) || !layout.isDraftLayout()) {
			return;
		}

		Lock lock = _lockManager.fetchLock(
			Layout.class.getName(), layout.getPlid());

		if (lock == null) {
			try {
				_lockManager.lock(
					userId, Layout.class.getName(), layout.getPlid(),
					String.valueOf(userId), false, _lockExpirationTime);
			}
			catch (PortalException portalException) {
				throw new LockedLayoutException(portalException);
			}
		}
		else if (lock.getUserId() == userId) {
			try {
				_lockManager.refresh(
					lock.getUuid(), lock.getCompanyId(), _lockExpirationTime);
			}
			catch (PortalException portalException) {
				throw new LockedLayoutException(portalException);
			}
		}
		else {
			throw new LockedLayoutException();
		}
	}

	@Override
	public List<LockedLayout> getLockedLayouts(
		long companyId, long groupId, Locale locale) {

		List<LockedLayout> lockedLayouts = new ArrayList<>();

		List<Object[]> results = _layoutLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
			).from(
				DSLQueryFactoryUtil.select(
					LayoutTable.INSTANCE.classPK, LockTable.INSTANCE.createDate,
					LayoutTable.INSTANCE.plid, LayoutTable.INSTANCE.type,
					LockTable.INSTANCE.userName
				).from(
					LayoutTable.INSTANCE
				).innerJoinON(
					LockTable.INSTANCE,
					LockTable.INSTANCE.companyId.eq(
						companyId
					).and(
						LockTable.INSTANCE.className.eq(Layout.class.getName())
					).and(
						LockTable.INSTANCE.key.eq(
							DSLFunctionFactoryUtil.castText(
								LayoutTable.INSTANCE.plid))
					)
				).where(
					LayoutTable.INSTANCE.groupId.eq(
						groupId
					).and(
						LayoutTable.INSTANCE.classPK.gt(0L)
					).and(
						LayoutTable.INSTANCE.hidden.eq(true)
					).and(
						LayoutTable.INSTANCE.system.eq(true)
					).and(
						LayoutTable.INSTANCE.status.eq(
							WorkflowConstants.STATUS_DRAFT)
					).and(
						LayoutTable.INSTANCE.type.in(
							new String[] {
								LayoutConstants.TYPE_ASSET_DISPLAY,
								LayoutConstants.TYPE_CONTENT,
								LayoutConstants.TYPE_UTILITY
							})
					)
				).as(
					"LockedLayoutsTable", LockedLayoutsTable.INSTANCE
				)
			));

		for (Object[] columns : results) {
			Layout layout = _layoutLocalService.fetchLayout(
				GetterUtil.getLong(columns[2]));

			if (layout == null) {
				continue;
			}

			long classPK = GetterUtil.getLong(columns[0]);

			lockedLayouts.add(
				new LockedLayout(
					classPK, (Date)columns[1],
					_getLockedLayoutType(
						classPK, GetterUtil.getString(columns[3])),
					layout.getName(locale), GetterUtil.getLong(columns[2]),
					GetterUtil.getString(columns[4])));
		}

		return lockedLayouts;
	}

	@Override
	public String getLockedLayoutURL(ActionRequest actionRequest) {
		return getLockedLayoutURL(_portal.getHttpServletRequest(actionRequest));
	}

	@Override
	public String getLockedLayoutURL(HttpServletRequest httpServletRequest) {
		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/layout_admin/locked_layout"
		).setBackURL(
			() -> {
				String backURL = ParamUtil.getString(
					httpServletRequest, "backURL");

				if (Validator.isNotNull(backURL)) {
					return backURL;
				}

				backURL = ParamUtil.getString(
					httpServletRequest, "p_l_back_url");

				if (Validator.isNotNull(backURL)) {
					return backURL;
				}

				return ParamUtil.getString(httpServletRequest, "redirect");
			}
		).setParameter(
			"p_l_back_url_title",
			() -> {
				String backURLTitle = ParamUtil.getString(
					httpServletRequest, "p_l_back_url_title");

				if (Validator.isNotNull(backURLTitle)) {
					return backURLTitle;
				}

				return null;
			}
		).buildString();
	}

	@Override
	public String getUnlockDraftLayoutURL(
			LiferayPortletResponse liferayPortletResponse,
			PortletURLBuilder.UnsafeSupplier<Object, Exception>
				redirectUnsafeSupplier)
		throws Exception {

		return PortletURLBuilder.createActionURL(
			liferayPortletResponse
		).setActionName(
			"/layout_content_page_editor/unlock_draft_layout"
		).setRedirect(
			redirectUnsafeSupplier
		).buildString();
	}

	@Override
	public void unlock(Layout layout, long userId) {
		if ((layout == null) || !layout.isDraftLayout()) {
			return;
		}

		_lockManager.unlock(
			Layout.class.getName(), String.valueOf(layout.getPlid()),
			String.valueOf(userId));
	}

	@Override
	public void unlockLayouts(long companyId, long autosaveMinutes)
		throws PortalException {

		Date companyLastAutosaveDate = new Date(
			System.currentTimeMillis() - (autosaveMinutes * Time.MINUTE));
		Map<Long, Date> lastAutosaveDateMap = new HashMap<>();
		Map<Long, LockedLayoutsGroupConfiguration>
			lockedLayoutsGroupConfigurations =
				_getLockedLayoutsGroupConfigurations(companyId);

		ActionableDynamicQuery actionableDynamicQuery =
			_lockLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq("companyId", companyId));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"className", Layout.class.getName()));

				if (lockedLayoutsGroupConfigurations.isEmpty()) {
					dynamicQuery.add(
						RestrictionsFactoryUtil.lt(
							"createDate",
							new Date(
								System.currentTimeMillis() -
									(autosaveMinutes * Time.MINUTE))));
				}
			});
		actionableDynamicQuery.setPerformActionMethod(
			(com.liferay.portal.lock.model.Lock lock) -> {
				if (lockedLayoutsGroupConfigurations.isEmpty()) {
					_lockManager.unlock(lock.getClassName(), lock.getKey());

					return;
				}

				Layout layout = _layoutLocalService.fetchLayout(
					GetterUtil.getLong(lock.getKey()));

				if (layout == null) {
					_lockManager.unlock(lock.getClassName(), lock.getKey());
				}

				LockedLayoutsGroupConfiguration
					lockedLayoutsGroupConfiguration =
						lockedLayoutsGroupConfigurations.get(
							layout.getGroupId());

				if ((lockedLayoutsGroupConfiguration != null) &&
					!lockedLayoutsGroupConfiguration.
						allowAutomaticUnlockingProcess()) {

					return;
				}

				int value = DateUtil.compareTo(
					lock.getCreateDate(),
					_getLastAutosaveDate(
						layout.getGroupId(), companyLastAutosaveDate,
						lastAutosaveDateMap, lockedLayoutsGroupConfiguration));

				if (value <= 0) {
					_lockManager.unlock(lock.getClassName(), lock.getKey());
				}
			});

		actionableDynamicQuery.performActions();
	}

	@Override
	public void unlockLayoutsByUserId(long companyId, long userId)
		throws PortalException {

		for (com.liferay.portal.lock.model.Lock lock :
				_lockLocalService.getLocks(companyId, _CLASS_NAME_LAYOUT)) {

			if (lock.getUserId() == userId) {
				_lockLocalService.unlock(_CLASS_NAME_LAYOUT, lock.getKey());
			}
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_lockExpirationTime = GetterUtil.getLong(
			properties.get("lock.expiration.time"),
			LayoutModelImpl.LOCK_EXPIRATION_TIME);
	}

	private Date _getLastAutosaveDate(
		long groupId, Date companyLastAutosaveDate,
		Map<Long, Date> lastAutosaveDateMap,
		LockedLayoutsGroupConfiguration lockedLayoutsGroupConfiguration) {

		Date lastAutosaveDate = lastAutosaveDateMap.get(groupId);

		if (lastAutosaveDate != null) {
			return lastAutosaveDate;
		}

		if (lockedLayoutsGroupConfiguration == null) {
			return companyLastAutosaveDate;
		}

		long autosaveTime =
			lockedLayoutsGroupConfiguration.autosaveMinutes() * Time.MINUTE;

		lastAutosaveDate = new Date(System.currentTimeMillis() - autosaveTime);

		lastAutosaveDateMap.put(groupId, lastAutosaveDate);

		return lastAutosaveDate;
	}

	private LockedLayoutType _getLayoutPageTemplateEntryTypeLabel(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		if (Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.BASIC)) {

			return LockedLayoutType.CONTENT_PAGE_TEMPLATE;
		}

		if (Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)) {

			return LockedLayoutType.DISPLAY_PAGE_TEMPLATE;
		}

		if (Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			return LockedLayoutType.MASTER_PAGE;
		}

		return null;
	}

	private String _getLockedLayoutsGroupConfigurationFilterString(
		long companyId) {

		String filterString = StringBundler.concat(
			"(&(", ConfigurationAdmin.SERVICE_FACTORYPID, StringPool.EQUAL,
			LockedLayoutsGroupConfiguration.class.getName(), ".scoped)(|");

		for (Group group :
				_groupLocalService.getGroups(
					companyId, GroupConstants.ANY_PARENT_GROUP_ID, true)) {

			filterString = filterString.concat(
				"(groupId=" + group.getGroupId() + ")");
		}

		return filterString.concat("))");
	}

	private Map<Long, LockedLayoutsGroupConfiguration>
			_getLockedLayoutsGroupConfigurations(long companyId)
		throws PortalException {

		Map<Long, LockedLayoutsGroupConfiguration>
			lockedLayoutsGroupConfigurations = new HashMap<>();

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					_getLockedLayoutsGroupConfigurationFilterString(companyId));

			if (ArrayUtil.isEmpty(configurations)) {
				return Collections.emptyMap();
			}

			for (Configuration configuration : configurations) {
				Dictionary<String, Object> dictionary =
					configuration.getProperties();

				long groupId = GetterUtil.getLong(dictionary.get("groupId"));

				if (groupId > 0) {
					lockedLayoutsGroupConfigurations.put(
						groupId,
						_configurationProvider.getGroupConfiguration(
							LockedLayoutsGroupConfiguration.class, groupId));
				}
			}
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to get locked layouts group configurations", exception);
		}

		return lockedLayoutsGroupConfigurations;
	}

	private LockedLayoutType _getLockedLayoutType(long classPK, String type) {
		if (Objects.equals(type, LayoutConstants.TYPE_ASSET_DISPLAY)) {
			return LockedLayoutType.DISPLAY_PAGE_TEMPLATE;
		}
		else if (Objects.equals(type, LayoutConstants.TYPE_UTILITY)) {
			return LockedLayoutType.UTILITY_PAGE;
		}

		if (!Objects.equals(type, LayoutConstants.TYPE_CONTENT)) {
			return null;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(classPK);

		if (layoutPageTemplateEntry != null) {
			return _getLayoutPageTemplateEntryTypeLabel(
				layoutPageTemplateEntry);
		}

		return LockedLayoutType.CONTENT_PAGE;
	}

	private static final String _CLASS_NAME_LAYOUT = Layout.class.getName();

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	private volatile long _lockExpirationTime;

	@Reference
	private LockLocalService _lockLocalService;

	@Reference
	private LockManager _lockManager;

	@Reference
	private Portal _portal;

	private static class LockedLayoutsTable
		extends BaseTable<LockedLayoutsTable> {

		public static final LockedLayoutsTable INSTANCE =
			new LockedLayoutsTable();

		public final Column<LockedLayoutsTable, Long> classPKColumn =
			createColumn(
				"classPK", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
		public final Column<LockedLayoutsTable, Date> createDateColumn =
			createColumn(
				"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
		public final Column<LockedLayoutsTable, Long> groupIdColumn =
			createColumn(
				"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
		public final Column<LockedLayoutsTable, Long> plidColumn = createColumn(
			"plid", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
		public final Column<LockedLayoutsTable, String> typeColumn =
			createColumn(
				"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
		public final Column<LockedLayoutsTable, String> userNameColumn =
			createColumn(
				"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

		private LockedLayoutsTable() {
			super("LockedLayoutsTable", LockedLayoutsTable::new);
		}

	}

}