/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalServiceUtil;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.trash.constants.TrashActionKeys;
import com.liferay.trash.kernel.model.TrashEntry;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the base implementation of {@link TrashHandler}.
 *
 * @author Alexander Chow
 * @author Zsolt Berentey
 * @see    TrashHandler
 */
@ProviderType
public abstract class BaseTrashHandler implements TrashHandler {

	@Override
	public SystemEvent addDeletionSystemEvent(
			long userId, long groupId, long classPK, String classUuid,
			String referrerClassName)
		throws PortalException {

		JSONObject extraDataJSONObject = JSONUtil.put("inTrash", true);

		return SystemEventLocalServiceUtil.addSystemEvent(
			userId, groupId, StringPool.BLANK, getSystemEventClassName(),
			classPK, classUuid, referrerClassName,
			SystemEventConstants.TYPE_DELETE, extraDataJSONObject.toString());
	}

	@Override
	public void checkRestorableEntry(
			long classPK, long containerModelId, String newName)
		throws PortalException {
	}

	@Override
	public void checkRestorableEntry(
			TrashEntry trashEntry, long containerModelId, String newName)
		throws PortalException {
	}

	@Override
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException {

		return null;
	}

	@Override
	public String getContainerModelClassName(long classPK) {
		return StringPool.BLANK;
	}

	@Override
	public String getContainerModelName() {
		return StringPool.BLANK;
	}

	@Override
	public List<ContainerModel> getContainerModels(
			long classPK, long containerModelId, int start, int end)
		throws PortalException {

		return Collections.emptyList();
	}

	@Override
	public int getContainerModelsCount(long classPK, long containerModelId)
		throws PortalException {

		return 0;
	}

	@Override
	public String getDeleteMessage() {
		return "deleted-in-x";
	}

	@Override
	public long getDestinationContainerModelId(
		long classPK, long destinationContainerModelId) {

		return destinationContainerModelId;
	}

	@Override
	public Filter getExcludeFilter(SearchContext searchContext) {
		return null;
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		return null;
	}

	@Override
	public ContainerModel getParentContainerModel(TrashedModel trashedModel)
		throws PortalException {

		if ((trashedModel == null) ||
			!(trashedModel instanceof ContainerModel)) {

			return null;
		}

		ContainerModel containerModel = (ContainerModel)trashedModel;

		return getContainerModel(containerModel.getParentContainerModelId());
	}

	@Override
	public List<ContainerModel> getParentContainerModels(long classPK)
		throws PortalException {

		return Collections.emptyList();
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		return StringPool.BLANK;
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		return StringPool.BLANK;
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		return StringPool.BLANK;
	}

	@Override
	public String getRootContainerModelName() {
		return StringPool.BLANK;
	}

	@Override
	public String getSubcontainerModelName() {
		return StringPool.BLANK;
	}

	@Override
	public String getSystemEventClassName() {
		return getClassName();
	}

	@Override
	public String getTrashContainedModelName() {
		return StringPool.BLANK;
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException {

		return 0;
	}

	@Override
	public String getTrashContainerModelName() {
		return StringPool.BLANK;
	}

	@Override
	public int getTrashContainerModelsCount(long classPK)
		throws PortalException {

		return 0;
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return null;
	}

	@Override
	public int getTrashModelsCount(long classPK) throws PortalException {
		return 0;
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		AssetRendererFactory<?> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory != null) {
			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(classPK);

			if (assetRenderer instanceof TrashRenderer) {
				return (TrashRenderer)assetRenderer;
			}
		}

		return null;
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return false;
		}

		String actionId = trashActionId;

		if (trashActionId.equals(TrashActionKeys.OVERWRITE) ||
			trashActionId.equals(TrashActionKeys.RESTORE)) {

			actionId = ActionKeys.DELETE;
		}
		else if (trashActionId.equals(TrashActionKeys.RENAME)) {
			actionId = ActionKeys.UPDATE;
		}

		return hasPermission(permissionChecker, classPK, actionId);
	}

	@Override
	public boolean isContainerModel() {
		return false;
	}

	@Override
	public boolean isDeletable(long classPK) throws PortalException {
		return hasTrashPermission(
			PermissionThreadLocal.getPermissionChecker(), 0, classPK,
			ActionKeys.DELETE);
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		return true;
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		if (isRestorable(classPK)) {
			restoreTrashEntry(userId, classPK);
		}

		Class<?> clazz = getClass();

		_log.error("moveTrashEntry() is not implemented in " + clazz.getName());

		throw new SystemException();
	}

	@Override
	public void restoreRelatedTrashEntry(String className, long classPK)
		throws PortalException {
	}

	@Override
	public void updateTitle(long classPK, String title) throws PortalException {
	}

	protected AssetRendererFactory<?> getAssetRendererFactory() {
		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(getClassName());
	}

	protected abstract boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseTrashHandler.class);

}