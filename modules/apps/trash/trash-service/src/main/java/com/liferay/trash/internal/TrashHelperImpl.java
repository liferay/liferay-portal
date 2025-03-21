/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Format;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = TrashHelper.class)
public class TrashHelperImpl implements TrashHelper {

	@Override
	public int getMaxAge(Group group) {
		int trashEntriesMaxAge = PrefsPropsUtil.getInteger(
			group.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE,
			PropsValues.TRASH_ENTRIES_MAX_AGE);

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getParentLiveGroupTypeSettingsProperties();

		return GetterUtil.getInteger(
			typeSettingsUnicodeProperties.getProperty("trashEntriesMaxAge"),
			trashEntriesMaxAge);
	}

	@Override
	public String getNewName(
			ThemeDisplay themeDisplay, String className, long classPK,
			String oldName)
		throws PortalException {

		TrashRenderer trashRenderer = null;

		if (Validator.isNotNull(className) && (classPK > 0)) {
			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(className);

			trashRenderer = trashHandler.getTrashRenderer(classPK);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(StringPool.OPEN_PARENTHESIS);

		Format format = FastDateFormatFactoryUtil.getDateTime(
			themeDisplay.getLocale(), themeDisplay.getTimeZone());

		sb.append(
			StringUtil.replace(
				format.format(new Date()),
				new char[] {CharPool.SLASH, CharPool.COLON},
				new char[] {CharPool.PERIOD, CharPool.PERIOD}));

		sb.append(StringPool.CLOSE_PARENTHESIS);

		if (trashRenderer != null) {
			return trashRenderer.getNewName(oldName, sb.toString());
		}

		return _getNewName(oldName, sb.toString());
	}

	@Override
	public String getOriginalTitle(String title) {
		return _getOriginalTitle(title, "title", _TRASH_PREFIX);
	}

	@Override
	public String getOriginalTitle(String title, String paramName) {
		return _getOriginalTitle(title, paramName, _TRASH_PREFIX);
	}

	@Override
	public TrashEntry getTrashEntry(TrashedModel trashedModel)
		throws PortalException {

		if (!trashedModel.isInTrash()) {
			return null;
		}

		BaseModel<?> baseModel = (BaseModel<?>)trashedModel;

		TrashEntry trashEntry = _trashEntryLocalService.fetchEntry(
			baseModel.getModelClassName(), trashedModel.getTrashEntryClassPK());

		if (trashEntry != null) {
			return trashEntry;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			baseModel.getModelClassName());

		if (Validator.isNotNull(
				trashHandler.getContainerModelClassName(
					(Long)baseModel.getPrimaryKeyObj()))) {

			ContainerModel containerModel = null;

			try {
				containerModel = trashHandler.getParentContainerModel(
					trashedModel);
			}
			catch (NoSuchModelException noSuchModelException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchModelException);
				}

				return null;
			}

			while (containerModel != null) {
				if (containerModel instanceof TrashedModel) {
					return getTrashEntry((TrashedModel)containerModel);
				}

				trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
					trashHandler.getContainerModelClassName(
						containerModel.getContainerModelId()));

				if (trashHandler == null) {
					return null;
				}

				containerModel = trashHandler.getContainerModel(
					containerModel.getParentContainerModelId());
			}
		}

		return null;
	}

	@Override
	public String getTrashTitle(long entryId) {
		return _getTrashTitle(entryId, _TRASH_PREFIX);
	}

	@Override
	public PortletURL getViewContentURL(
			HttpServletRequest httpServletRequest, String className,
			long classPK)
		throws PortalException {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		TrashedModel trashedModel = trashHandler.getTrashedModel(classPK);

		if ((trashedModel != null) && isInTrashContainer(trashedModel)) {
			TrashEntry trashEntry = getTrashEntry(trashedModel);

			className = trashEntry.getClassName();
			classPK = trashEntry.getClassPK();

			trashHandler = TrashHandlerRegistryUtil.getTrashHandler(className);
		}

		TrashRenderer trashRenderer = trashHandler.getTrashRenderer(classPK);

		if (trashRenderer == null) {
			return null;
		}

		PortletURL portletURL = PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, TrashEntry.class.getName(),
				PortletProvider.Action.VIEW)
		).setMVCPath(
			"/view_content.jsp"
		).setRedirect(
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getURLCurrent();
			}
		).buildPortletURL();

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			className, classPK);

		if (trashEntry.getRootEntry() != null) {
			portletURL.setParameter("className", className);
			portletURL.setParameter("classPK", String.valueOf(classPK));
		}
		else {
			portletURL.setParameter(
				"trashEntryId", String.valueOf(trashEntry.getEntryId()));
		}

		portletURL.setParameter("showAssetMetadata", Boolean.TRUE.toString());

		return portletURL;
	}

	@Override
	public boolean isInTrashContainer(TrashedModel trashedModel) {
		if (trashedModel == null) {
			return false;
		}

		BaseModel<?> baseModel = (BaseModel<?>)trashedModel;

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			baseModel.getModelClassName());

		if ((trashHandler == null) ||
			Validator.isNull(
				trashHandler.getContainerModelClassName(
					(Long)baseModel.getPrimaryKeyObj()))) {

			return false;
		}

		try {
			ContainerModel containerModel =
				trashHandler.getParentContainerModel(trashedModel);

			if (containerModel == null) {
				return false;
			}

			if (containerModel instanceof TrashedModel) {
				TrashedModel containerTrashedModel =
					(TrashedModel)containerModel;

				return containerTrashedModel.isInTrash();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public boolean isInTrashExplicitly(TrashedModel trashedModel) {
		if (!trashedModel.isInTrash()) {
			return false;
		}

		BaseModel<?> baseModel = (BaseModel<?>)trashedModel;

		TrashEntry trashEntry = _trashEntryLocalService.fetchEntry(
			baseModel.getModelClassName(), trashedModel.getTrashEntryClassPK());

		if (trashEntry != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isInTrashImplicitly(TrashedModel trashedModel) {
		if (!trashedModel.isInTrash()) {
			return false;
		}

		BaseModel<?> baseModel = (BaseModel<?>)trashedModel;

		TrashEntry trashEntry = _trashEntryLocalService.fetchEntry(
			baseModel.getModelClassName(), trashedModel.getTrashEntryClassPK());

		if (trashEntry != null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isTrashEnabled(Group group) {
		boolean companyTrashEnabled = PrefsPropsUtil.getBoolean(
			group.getCompanyId(), PropsKeys.TRASH_ENABLED);

		if (!companyTrashEnabled) {
			return false;
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getParentLiveGroupTypeSettingsProperties();

		return GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty("trashEnabled"), true);
	}

	@Override
	public boolean isTrashEnabled(long groupId) throws PortalException {
		return isTrashEnabled(_groupLocalService.getGroup(groupId));
	}

	private String _getNewName(String oldName, String token) {
		return StringBundler.concat(oldName, StringPool.SPACE, token);
	}

	private String _getOriginalTitle(
		String title, String paramName, String prefix) {

		if (!_isValidTrashTitle(title, prefix)) {
			return title;
		}

		title = title.substring(prefix.length());

		long trashEntryId = GetterUtil.getLong(title);

		if (trashEntryId <= 0) {
			return title;
		}

		try {
			TrashEntry trashEntry = _trashEntryLocalService.fetchEntry(
				trashEntryId);

			if (trashEntry == null) {
				TrashVersion trashVersion =
					_trashVersionLocalService.getTrashVersion(trashEntryId);

				title = trashVersion.getTypeSettingsProperty(paramName);
			}
			else {
				title = trashEntry.getTypeSettingsProperty(paramName);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No trash entry or trash version exists with ID " +
						trashEntryId,
					exception);
			}
		}

		return title;
	}

	private String _getTrashTitle(long trashEntryId, String prefix) {
		return prefix.concat(String.valueOf(trashEntryId));
	}

	private boolean _isValidTrashTitle(String title, String prefix) {
		if (title.startsWith(prefix)) {
			return true;
		}

		return false;
	}

	private static final String _TRASH_PREFIX = StringPool.SLASH;

	private static final Log _log = LogFactoryUtil.getLog(
		TrashHelperImpl.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

}