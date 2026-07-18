/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.impl;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagDisplay;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Autocomplete;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.service.base.AssetTagServiceBaseImpl;
import com.liferay.portlet.asset.service.permission.AssetTagsPermission;
import com.liferay.portlet.asset.util.comparator.AssetTagNameComparator;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.List;

/**
 * Provides the remote service for accessing, adding, checking, deleting,
 * merging, and updating asset tags. Its methods include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Alvaro del Castillo
 * @author Eduardo Lundgren
 * @author Bruno Farache
 * @author Juan Fernández
 */
public class AssetTagServiceImpl extends AssetTagServiceBaseImpl {

	@Override
	public AssetTag addTag(
			String externalReferenceCode, long groupId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		AssetTagsPermission.check(
			getPermissionChecker(), groupId, ActionKeys.MANAGE_TAG);

		return assetTagLocalService.addTag(
			externalReferenceCode, getUserId(), groupId, name, serviceContext);
	}

	@Override
	public void deleteTag(long tagId) throws PortalException {
		deleteTags(new long[] {tagId});
	}

	@Override
	public void deleteTags(long[] tagIds) throws PortalException {
		for (long tagId : tagIds) {
			AssetTag tag = assetTagPersistence.findByPrimaryKey(tagId);

			AssetTagsPermission.check(
				getPermissionChecker(), tag.getGroupId(),
				ActionKeys.MANAGE_TAG);

			assetTagLocalService.deleteTag(tagId);
		}
	}

	@Override
	public AssetTag fetchAssetTagByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return sanitize(
			assetTagPersistence.fetchByERC_G(externalReferenceCode, groupId));
	}

	@Override
	public AssetTag fetchTag(long groupId, String name) throws PortalException {
		return sanitize(assetTagLocalService.fetchTag(groupId, name));
	}

	@Override
	public AssetTag getAssetTagByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return sanitize(
			assetTagPersistence.findByERC_G(externalReferenceCode, groupId));
	}

	@Override
	public List<AssetTag> getGroupsTags(long[] groupIds) {
		return sanitize(
			assetTagPersistence.findByGroupId(
				groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new AssetTagNameComparator()));
	}

	@Override
	public List<AssetTag> getGroupTags(long groupId) {
		return sanitize(assetTagPersistence.findByGroupId(groupId));
	}

	@Override
	public List<AssetTag> getGroupTags(
		long groupId, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return sanitize(
			assetTagPersistence.findByGroupId(
				groupId, start, end, orderByComparator));
	}

	@Override
	public int getGroupTagsCount(long groupId) {
		return assetTagPersistence.countByGroupId(groupId);
	}

	@Override
	public AssetTagDisplay getGroupTagsDisplay(
		long groupId, String name, int start, int end) {

		List<AssetTag> tags = null;
		int total = 0;

		if (Validator.isNotNull(name)) {
			name = CustomSQLUtil.keywords(name)[0];

			tags = getTags(groupId, name, start, end);
			total = getTagsCount(groupId, name);
		}
		else {
			tags = getGroupTags(groupId, start, end, null);
			total = getGroupTagsCount(groupId);
		}

		return new AssetTagDisplay(tags, total, start, end);
	}

	@Override
	public AssetTag getTag(long tagId) throws PortalException {
		return sanitize(assetTagPersistence.findByPrimaryKey(tagId));
	}

	@Override
	public AssetTag getTag(long groupId, String name) throws PortalException {
		return sanitize(assetTagLocalService.getTag(groupId, name));
	}

	@Override
	public List<AssetTag> getTags(long groupId, long classNameId, String name) {
		return sanitize(
			assetTagFinder.findByG_C_N(
				groupId, classNameId, name, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));
	}

	@Override
	public List<AssetTag> getTags(
		long groupId, long classNameId, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return sanitize(
			assetTagFinder.findByG_C_N(
				groupId, classNameId, name, start, end, orderByComparator));
	}

	@Override
	public List<AssetTag> getTags(
		long groupId, String name, int start, int end) {

		return getTags(new long[] {groupId}, name, start, end);
	}

	@Override
	public List<AssetTag> getTags(
		long groupId, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		return getTags(
			new long[] {groupId}, name, start, end, orderByComparator);
	}

	@Override
	public List<AssetTag> getTags(
		long[] groupIds, String name, int start, int end) {

		return getTags(
			groupIds, name, start, end, new AssetTagNameComparator());
	}

	@Override
	public List<AssetTag> getTags(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<AssetTag> orderByComparator) {

		if (Validator.isNull(name)) {
			return sanitize(
				assetTagPersistence.findByGroupId(
					groupIds, start, end, orderByComparator));
		}

		return sanitize(
			assetTagPersistence.findByG_LikeN(
				groupIds, StringUtil.quote(name, StringPool.PERCENT), start,
				end, orderByComparator));
	}

	@Override
	public List<AssetTag> getTags(String className, long classPK) {
		return sanitize(assetTagLocalService.getTags(className, classPK));
	}

	@Override
	public int getTagsCount(long groupId, String name) {
		if (Validator.isNull(name)) {
			return assetTagPersistence.countByGroupId(groupId);
		}

		return assetTagPersistence.countByG_LikeN(
			groupId, StringUtil.quote(name, StringPool.PERCENT));
	}

	@Override
	public int getTagsCount(long[] groupIds, String name) {
		if (Validator.isNull(name)) {
			return assetTagPersistence.countByGroupId(groupIds);
		}

		return assetTagPersistence.countByG_LikeN(
			groupIds, StringUtil.quote(name, StringPool.PERCENT));
	}

	@Override
	public int getVisibleAssetsTagsCount(
		long groupId, long classNameId, String name) {

		return assetTagFinder.countByG_C_N(groupId, classNameId, name);
	}

	@Override
	public void mergeTags(long fromTagId, long toTagId) throws PortalException {
		AssetTag tag = assetTagPersistence.findByPrimaryKey(fromTagId);

		AssetTagsPermission.check(
			getPermissionChecker(), tag.getGroupId(), ActionKeys.MANAGE_TAG);

		assetTagLocalService.mergeTags(fromTagId, toTagId);
	}

	@Override
	public void mergeTags(long[] fromTagIds, long toTagId)
		throws PortalException {

		for (long fromTagId : fromTagIds) {
			mergeTags(fromTagId, toTagId);
		}
	}

	@Override
	public JSONArray search(long groupId, String name, int start, int end) {
		return search(new long[] {groupId}, name, start, end);
	}

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public JSONArray search(long[] groupIds, String name, int start, int end) {
		return Autocomplete.arrayToJSONArray(
			getTags(groupIds, name, start, end), "name", "name");
	}

	@Override
	public void subscribeTag(long userId, long groupId, long tagId)
		throws PortalException {

		AssetTagsPermission.check(
			getPermissionChecker(), groupId, ActionKeys.SUBSCRIBE);

		assetTagLocalService.subscribeTag(userId, groupId, tagId);
	}

	@Override
	public void unsubscribeTag(long userId, long tagId) throws PortalException {
		AssetTag tag = assetTagPersistence.findByPrimaryKey(tagId);

		AssetTagsPermission.check(
			getPermissionChecker(), tag.getGroupId(), ActionKeys.SUBSCRIBE);

		assetTagLocalService.unsubscribeTag(userId, tagId);
	}

	@Override
	public AssetTag updateTag(
			String externalReferenceCode, long tagId, String name,
			ServiceContext serviceContext)
		throws PortalException {

		AssetTag tag = assetTagPersistence.findByPrimaryKey(tagId);

		AssetTagsPermission.check(
			getPermissionChecker(), tag.getGroupId(), ActionKeys.MANAGE_TAG);

		return assetTagLocalService.updateTag(
			externalReferenceCode, getUserId(), tagId, name, serviceContext);
	}

	protected AssetTag sanitize(AssetTag tag) {
		if (tag == null) {
			return null;
		}

		try {
			PermissionChecker permissionChecker = getPermissionChecker();

			if (permissionChecker.isCompanyAdmin(tag.getCompanyId()) ||
				(tag.getUserId() == permissionChecker.getUserId())) {

				return tag;
			}
		}
		catch (PrincipalException principalException) {
			_log.error(principalException);
		}

		tag.setUserId(0);
		tag.setUserName(StringPool.BLANK);
		tag.setUserUuid(StringPool.BLANK);

		return tag;
	}

	protected List<AssetTag> sanitize(List<AssetTag> tags) {
		for (AssetTag tag : tags) {
			sanitize(tag);
		}

		return tags;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetTagServiceImpl.class);

}