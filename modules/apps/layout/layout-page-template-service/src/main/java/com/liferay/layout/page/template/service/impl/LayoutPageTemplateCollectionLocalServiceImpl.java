/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.impl;

import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionGroupIdException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.base.LayoutPageTemplateCollectionLocalServiceBaseImpl;
import com.liferay.layout.validator.LayoutValidator;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UniqueUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "model.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection",
	service = AopService.class
)
public class LayoutPageTemplateCollectionLocalServiceImpl
	extends LayoutPageTemplateCollectionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public LayoutPageTemplateCollection addLayoutPageTemplateCollection(
			String externalReferenceCode, long userId, long groupId,
			long parentLayoutPageTemplateCollectionId,
			String layoutPageTemplateCollectionKey, String name,
			String description, int type, ServiceContext serviceContext)
		throws PortalException {

		// Layout page template collection

		Group group = _groupLocalService.getGroup(groupId);

		if (group.isCompany() || group.isDepot()) {
			throw new LayoutPageTemplateCollectionGroupIdException();
		}

		User user = _userLocalService.getUser(userId);

		_validate(groupId, name, parentLayoutPageTemplateCollectionId, type);

		if (Validator.isNull(layoutPageTemplateCollectionKey)) {
			layoutPageTemplateCollectionKey =
				_generateLayoutPageTemplateCollectionKey(groupId, name, type);
		}
		else {
			_validateLayoutPageTemplateCollectionKey(
				groupId, layoutPageTemplateCollectionKey, type);
		}

		long layoutPageTemplateId = counterLocalService.increment();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.create(
				layoutPageTemplateId);

		layoutPageTemplateCollection.setUuid(serviceContext.getUuid());
		layoutPageTemplateCollection.setExternalReferenceCode(
			externalReferenceCode);
		layoutPageTemplateCollection.setGroupId(groupId);
		layoutPageTemplateCollection.setCompanyId(user.getCompanyId());
		layoutPageTemplateCollection.setUserId(user.getUserId());
		layoutPageTemplateCollection.setUserName(user.getFullName());
		layoutPageTemplateCollection.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		layoutPageTemplateCollection.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		layoutPageTemplateCollection.setParentLayoutPageTemplateCollectionId(
			parentLayoutPageTemplateCollectionId);
		layoutPageTemplateCollection.setLayoutPageTemplateCollectionKey(
			layoutPageTemplateCollectionKey);
		layoutPageTemplateCollection.setName(name);
		layoutPageTemplateCollection.setDescription(description);
		layoutPageTemplateCollection.setType(type);

		layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.update(
				layoutPageTemplateCollection);

		// Resources

		_resourceLocalService.addModelResources(
			layoutPageTemplateCollection, serviceContext);

		return layoutPageTemplateCollection;
	}

	@Override
	public LayoutPageTemplateCollection copyLayoutPageTemplateCollection(
			long userId, long groupId,
			long sourceLayoutPageTemplateCollectionId,
			long layoutParentPageTemplateCollectionId, boolean copyPermissions,
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateCollection sourceLayoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.findByPrimaryKey(
				sourceLayoutPageTemplateCollectionId);

		LayoutPageTemplateCollection targetLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection(
				null, userId, sourceLayoutPageTemplateCollection.getGroupId(),
				layoutParentPageTemplateCollectionId, null,
				getUniqueLayoutPageTemplateCollectionName(
					groupId, layoutParentPageTemplateCollectionId,
					sourceLayoutPageTemplateCollection.getName(),
					sourceLayoutPageTemplateCollection.getType()),
				sourceLayoutPageTemplateCollection.getDescription(),
				sourceLayoutPageTemplateCollection.getType(), serviceContext);

		if (copyPermissions) {
			_resourceLocalService.deleteResource(
				targetLayoutPageTemplateCollection.getCompanyId(),
				LayoutPageTemplateCollection.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				targetLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

			_resourceLocalService.copyModelResources(
				sourceLayoutPageTemplateCollection.getCompanyId(),
				LayoutPageTemplateCollection.class.getName(),
				sourceLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				targetLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());
		}

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntries(
				sourceLayoutPageTemplateCollection.getGroupId(),
				sourceLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		for (LayoutPageTemplateEntry layoutPageTemplateEntry :
				layoutPageTemplateEntries) {

			_layoutPageTemplateEntryLocalService.copyLayoutPageTemplateEntry(
				userId, groupId,
				targetLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				copyPermissions, serviceContext);
		}

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
			getLayoutPageTemplateCollections(
				sourceLayoutPageTemplateCollection.getGroupId(),
				sourceLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				layoutPageTemplateCollections) {

			copyLayoutPageTemplateCollection(
				userId, groupId,
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				targetLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId(),
				copyPermissions, serviceContext);
		}

		return targetLayoutPageTemplateCollection;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws PortalException {

		// Layout page template collection

		layoutPageTemplateCollectionPersistence.remove(
			layoutPageTemplateCollection);

		List<LayoutPageTemplateCollection> layoutPageTemplateCollections =
			layoutPageTemplateCollectionPersistence.findByG_P(
				layoutPageTemplateCollection.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		for (LayoutPageTemplateCollection curLayoutPageTemplateCollection :
				layoutPageTemplateCollections) {

			layoutPageTemplateCollectionLocalService.
				deleteLayoutPageTemplateCollection(
					curLayoutPageTemplateCollection);
		}

		// Resources

		_resourceLocalService.deleteResource(
			layoutPageTemplateCollection.getCompanyId(),
			LayoutPageTemplateCollection.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId());

		// Layout page template entries

		List<LayoutPageTemplateEntry> layoutPageTemplateEntries =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntries(
				layoutPageTemplateCollection.getGroupId(),
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId());

		for (LayoutPageTemplateEntry layoutPageTemplateEntry :
				layoutPageTemplateEntries) {

			_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
				layoutPageTemplateEntry);
		}

		return layoutPageTemplateCollection;
	}

	@Override
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId)
		throws PortalException {

		return deleteLayoutPageTemplateCollection(
			getLayoutPageTemplateCollection(layoutPageTemplateCollectionId));
	}

	@Override
	public LayoutPageTemplateCollection deleteLayoutPageTemplateCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return deleteLayoutPageTemplateCollection(
			getLayoutPageTemplateCollectionByExternalReferenceCode(
				externalReferenceCode, groupId));
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) {

		return layoutPageTemplateCollectionPersistence.fetchByPrimaryKey(
			layoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		return layoutPageTemplateCollectionPersistence.fetchByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type);
	}

	@Override
	public LayoutPageTemplateCollection fetchLayoutPageTemplateCollection(
		long groupId, String name, long parentLayoutPageTemplateCollectionId,
		int type) {

		return layoutPageTemplateCollectionPersistence.fetchByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId) {

		return layoutPageTemplateCollectionPersistence.findByGroupId(groupId);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, int type, int start, int end) {

		return layoutPageTemplateCollectionPersistence.findByG_T(
			groupId, type, start, end);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return layoutPageTemplateCollectionPersistence.findByG_T(
			groupId, type, start, end, orderByComparator);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, long layoutPageTemplateCollectionId) {

		return layoutPageTemplateCollectionPersistence.findByG_P(
			groupId, layoutPageTemplateCollectionId);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return layoutPageTemplateCollectionPersistence.findByG_P_T(
			groupId, layoutPageTemplateCollectionId, type);
	}

	@Override
	public List<LayoutPageTemplateCollection> getLayoutPageTemplateCollections(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		if (Validator.isNull(name)) {
			return layoutPageTemplateCollectionPersistence.findByG_T(
				groupId, type, start, end, orderByComparator);
		}

		return layoutPageTemplateCollectionPersistence.findByG_LikeN_T(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			type, start, end, orderByComparator);
	}

	@Override
	public int getLayoutPageTemplateCollectionsCount(long groupId, int type) {
		return layoutPageTemplateCollectionPersistence.countByG_T(
			groupId, type);
	}

	@Override
	public int getLayoutPageTemplateCollectionsCount(
		long groupId, String name, int type) {

		if (Validator.isNull(name)) {
			return layoutPageTemplateCollectionPersistence.countByG_T(
				groupId, type);
		}

		return layoutPageTemplateCollectionPersistence.countByG_LikeN_T(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			type);
	}

	@Override
	public String getUniqueLayoutPageTemplateCollectionName(
			long groupId, long layoutPageTemplateCollectionId,
			String sourceName, int type)
		throws PortalException {

		return UniqueUtil.getCopyValue(
			copyValue -> {
				LayoutPageTemplateCollection layoutPageTemplateCollection =
					layoutPageTemplateCollectionPersistence.fetchByG_P_N_T(
						groupId, layoutPageTemplateCollectionId, copyValue,
						type);

				if (layoutPageTemplateCollection == null) {
					return true;
				}

				return false;
			},
			sourceName);
	}

	@Override
	public LayoutPageTemplateCollection moveLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId,
			long parentLayoutPageTemplateCollectionId)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionLocalService.
				getLayoutPageTemplateCollection(layoutPageTemplateCollectionId);

		if ((parentLayoutPageTemplateCollectionId ==
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId()) ||
			(parentLayoutPageTemplateCollectionId ==
				layoutPageTemplateCollection.
					getParentLayoutPageTemplateCollectionId())) {

			return layoutPageTemplateCollection;
		}

		layoutPageTemplateCollection.setParentLayoutPageTemplateCollectionId(
			parentLayoutPageTemplateCollectionId);

		return updateLayoutPageTemplateCollection(layoutPageTemplateCollection);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId, String name)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.findByPrimaryKey(
				layoutPageTemplateCollectionId);

		if (!Objects.equals(layoutPageTemplateCollection.getName(), name)) {
			_validate(
				layoutPageTemplateCollection.getGroupId(), name,
				layoutPageTemplateCollection.
					getParentLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollection.getType());
		}

		layoutPageTemplateCollection.setLayoutPageTemplateCollectionKey(
			_generateLayoutPageTemplateCollectionKey(
				layoutPageTemplateCollection.getGroupId(), name,
				layoutPageTemplateCollection.getType()));
		layoutPageTemplateCollection.setName(name);

		return layoutPageTemplateCollectionPersistence.update(
			layoutPageTemplateCollection);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public LayoutPageTemplateCollection updateLayoutPageTemplateCollection(
			long layoutPageTemplateCollectionId, String name,
			String description)
		throws PortalException {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.findByPrimaryKey(
				layoutPageTemplateCollectionId);

		if (!Objects.equals(layoutPageTemplateCollection.getName(), name)) {
			_validate(
				layoutPageTemplateCollection.getGroupId(), name,
				layoutPageTemplateCollection.
					getParentLayoutPageTemplateCollectionId(),
				layoutPageTemplateCollection.getType());
		}

		layoutPageTemplateCollection.setModifiedDate(new Date());
		layoutPageTemplateCollection.setLayoutPageTemplateCollectionKey(
			_generateLayoutPageTemplateCollectionKey(
				layoutPageTemplateCollection.getGroupId(), name,
				layoutPageTemplateCollection.getType()));
		layoutPageTemplateCollection.setName(name);
		layoutPageTemplateCollection.setDescription(description);

		return layoutPageTemplateCollectionPersistence.update(
			layoutPageTemplateCollection);
	}

	private String _generateLayoutPageTemplateCollectionKey(
		long groupId, String name, int type) {

		String layoutPageTemplateCollectionKey = StringUtil.replace(
			StringUtil.toLowerCase(name.trim()),
			new char[] {CharPool.FORWARD_SLASH, CharPool.SPACE},
			new char[] {CharPool.DASH, CharPool.DASH});

		String curLayoutPageTemplateCollectionKey =
			layoutPageTemplateCollectionKey;

		int count = 0;

		while (true) {
			LayoutPageTemplateCollection layoutPageTemplateCollection =
				layoutPageTemplateCollectionPersistence.fetchByG_LPTCK_T(
					groupId, curLayoutPageTemplateCollectionKey, type);

			if (layoutPageTemplateCollection == null) {
				return curLayoutPageTemplateCollectionKey;
			}

			curLayoutPageTemplateCollectionKey =
				curLayoutPageTemplateCollectionKey + CharPool.DASH + count++;
		}
	}

	private void _validate(
			long groupId, String name, long parentLayoutPageTemplateCollection,
			int type)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new LayoutPageTemplateCollectionNameException(
				"Name must not be null");
		}

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			LayoutPageTemplateEntry.class.getName(), "name");

		if (name.length() > nameMaxLength) {
			throw new LayoutPageTemplateCollectionNameException(
				"Maximum length of name exceeded " + nameMaxLength);
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.fetchByG_P_N_T(
				groupId, parentLayoutPageTemplateCollection, name, type);

		if (layoutPageTemplateCollection != null) {
			throw new DuplicateLayoutPageTemplateCollectionException(name);
		}
	}

	private void _validateLayoutPageTemplateCollectionKey(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws PortalException {

		if (LayoutValidator.hasBlacklistedChar(
				layoutPageTemplateCollectionKey)) {

			throw new LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotContainInvalidCharacters(
				layoutPageTemplateCollectionKey, type);
		}

		int layoutPageTemplateCollectionKeyMaxLength =
			ModelHintsUtil.getMaxLength(
				LayoutPageTemplateCollection.class.getName(),
				"layoutPageTemplateCollectionKey");

		if (layoutPageTemplateCollectionKey.length() >
				layoutPageTemplateCollectionKeyMaxLength) {

			throw new LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotExceedMaximumSize(
				layoutPageTemplateCollectionKey,
				layoutPageTemplateCollectionKeyMaxLength, type);
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			layoutPageTemplateCollectionPersistence.fetchByG_LPTCK_T(
				groupId, layoutPageTemplateCollectionKey, type);

		if (layoutPageTemplateCollection != null) {
			throw new LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotBeDuplicate(
				groupId, layoutPageTemplateCollectionKey, type);
		}
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}