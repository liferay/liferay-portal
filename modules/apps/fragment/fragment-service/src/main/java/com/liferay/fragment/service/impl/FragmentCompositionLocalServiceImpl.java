/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.fragment.exception.DuplicateFragmentCompositionKeyException;
import com.liferay.fragment.exception.FragmentCompositionDescriptionException;
import com.liferay.fragment.exception.FragmentCompositionNameException;
import com.liferay.fragment.internal.util.FragmentCompositionKeyUtil;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.service.base.FragmentCompositionLocalServiceBaseImpl;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.fragment.model.FragmentComposition",
	service = AopService.class
)
public class FragmentCompositionLocalServiceImpl
	extends FragmentCompositionLocalServiceBaseImpl {

	@Override
	public FragmentComposition addFragmentComposition(
			String externalReferenceCode, long userId, long groupId,
			long fragmentCollectionId, String fragmentCompositionKey,
			String name, String description, String data,
			long previewFileEntryId, int status, ServiceContext serviceContext)
		throws PortalException {

		// Fragment composition

		User user = _userLocalService.getUser(userId);

		if (Validator.isNull(fragmentCompositionKey)) {
			fragmentCompositionKey = generateFragmentCompositionKey(
				groupId, name);
		}

		fragmentCompositionKey =
			FragmentCompositionKeyUtil.getFragmentCompositionKey(
				fragmentCompositionKey);

		_validateFragmentCompositionKey(groupId, fragmentCompositionKey);

		_validateName(name);
		_validateDescription(description);

		long fragmentCompositionId = counterLocalService.increment();

		FragmentComposition fragmentComposition =
			fragmentCompositionPersistence.create(fragmentCompositionId);

		fragmentComposition.setUuid(serviceContext.getUuid());
		fragmentComposition.setExternalReferenceCode(externalReferenceCode);
		fragmentComposition.setGroupId(groupId);
		fragmentComposition.setCompanyId(user.getCompanyId());
		fragmentComposition.setUserId(user.getUserId());
		fragmentComposition.setUserName(user.getFullName());
		fragmentComposition.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		fragmentComposition.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		fragmentComposition.setFragmentCollectionId(fragmentCollectionId);
		fragmentComposition.setFragmentCompositionKey(fragmentCompositionKey);
		fragmentComposition.setName(name);
		fragmentComposition.setDescription(description);
		fragmentComposition.setData(data);
		fragmentComposition.setPreviewFileEntryId(previewFileEntryId);
		fragmentComposition.setStatus(status);
		fragmentComposition.setStatusByUserId(userId);
		fragmentComposition.setStatusByUserName(user.getFullName());
		fragmentComposition.setStatusDate(new Date());

		return fragmentCompositionPersistence.update(fragmentComposition);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public FragmentComposition deleteFragmentComposition(
			FragmentComposition fragmentComposition)
		throws PortalException {

		// Fragment composition

		fragmentCompositionPersistence.remove(fragmentComposition);

		// Resources

		_resourceLocalService.deleteResource(
			fragmentComposition.getCompanyId(),
			FragmentComposition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			fragmentComposition.getFragmentCompositionId());

		// Preview image

		if (fragmentComposition.getPreviewFileEntryId() > 0) {
			PortletFileRepositoryUtil.deletePortletFileEntry(
				fragmentComposition.getPreviewFileEntryId());
		}

		return fragmentComposition;
	}

	@Override
	public FragmentComposition deleteFragmentComposition(
			long fragmentCompositionId)
		throws PortalException {

		return fragmentCompositionLocalService.deleteFragmentComposition(
			getFragmentComposition(fragmentCompositionId));
	}

	@Override
	public FragmentComposition deleteFragmentComposition(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return fragmentCompositionLocalService.deleteFragmentComposition(
			getFragmentCompositionByExternalReferenceCode(
				externalReferenceCode, groupId));
	}

	@Override
	public FragmentComposition fetchFragmentComposition(
		long fragmentCompositionId) {

		return fragmentCompositionPersistence.fetchByPrimaryKey(
			fragmentCompositionId);
	}

	@Override
	public FragmentComposition fetchFragmentComposition(
		long groupId, String fragmentCompositionKey) {

		return fragmentCompositionPersistence.fetchByG_FCK(
			groupId,
			FragmentCompositionKeyUtil.getFragmentCompositionKey(
				fragmentCompositionKey));
	}

	@Override
	public String generateFragmentCompositionKey(long groupId, String name) {
		String fragmentCompositionKey =
			FragmentCompositionKeyUtil.getFragmentCompositionKey(name);

		fragmentCompositionKey = StringUtil.replace(
			fragmentCompositionKey, CharPool.SPACE, CharPool.DASH);

		String curFragmentCompositionKey = fragmentCompositionKey;

		int count = 0;

		while (true) {
			FragmentComposition fragmentComposition =
				fragmentCompositionPersistence.fetchByG_FCK(
					groupId, curFragmentCompositionKey);

			if (fragmentComposition == null) {
				return curFragmentCompositionKey;
			}

			curFragmentCompositionKey =
				fragmentCompositionKey + CharPool.DASH + count++;
		}
	}

	@Override
	public List<FragmentComposition> getFragmentCompositions(
		long fragmentCollectionId) {

		return fragmentCompositionPersistence.findByFragmentCollectionId(
			fragmentCollectionId);
	}

	@Override
	public List<FragmentComposition> getFragmentCompositions(
		long fragmentCollectionId, int start, int end) {

		return fragmentCompositionPersistence.findByFragmentCollectionId(
			fragmentCollectionId, start, end);
	}

	@Override
	public List<FragmentComposition> getFragmentCompositions(
		long groupId, long fragmentCollectionId, int status) {

		return fragmentCompositionPersistence.findByG_FCI_S(
			groupId, fragmentCollectionId, status);
	}

	@Override
	public List<FragmentComposition> getFragmentCompositions(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentComposition> orderByComparator) {

		return fragmentCompositionPersistence.findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	@Override
	public List<FragmentComposition> getFragmentCompositions(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentComposition> orderByComparator) {

		if (Validator.isNull(name)) {
			return fragmentCompositionPersistence.findByG_FCI(
				groupId, fragmentCollectionId, start, end, orderByComparator);
		}

		return fragmentCompositionPersistence.findByG_FCI_LikeN(
			groupId, fragmentCollectionId,
			_customSQL.keywords(name, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public int getFragmentCompositionsCount(long fragmentCollectionId) {
		return fragmentCompositionPersistence.countByFragmentCollectionId(
			fragmentCollectionId);
	}

	@Override
	public String[] getTempFileNames(
			long userId, long groupId, String folderName)
		throws PortalException {

		return TempFileEntryUtil.getTempFileNames(groupId, userId, folderName);
	}

	@Override
	public String getUniqueFragmentCompositionName(
		long groupId, long fragmentCollectionId, String name) {

		FragmentComposition fragmentComposition =
			fragmentCompositionPersistence.fetchByG_FCI_LikeN_First(
				groupId, fragmentCollectionId, name, null);

		if (fragmentComposition == null) {
			return name;
		}

		int count = 1;

		while (true) {
			String newName = StringUtil.appendParentheticalSuffix(
				name, count++);

			fragmentComposition =
				fragmentCompositionPersistence.fetchByG_FCI_LikeN_First(
					groupId, fragmentCollectionId, newName, null);

			if (fragmentComposition == null) {
				return newName;
			}
		}
	}

	@Override
	public FragmentComposition moveFragmentComposition(
			long fragmentCompositionId, long fragmentCollectionId)
		throws PortalException {

		FragmentComposition fragmentComposition =
			fragmentCompositionPersistence.findByPrimaryKey(
				fragmentCompositionId);

		if (fragmentComposition.getFragmentCollectionId() ==
				fragmentCollectionId) {

			return fragmentComposition;
		}

		fragmentComposition.setFragmentCollectionId(fragmentCollectionId);

		return fragmentCompositionPersistence.update(fragmentComposition);
	}

	@Override
	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, long previewFileEntryId)
		throws PortalException {

		FragmentComposition fragmentComposition =
			fragmentCompositionPersistence.findByPrimaryKey(
				fragmentCompositionId);

		fragmentComposition.setModifiedDate(new Date());

		long previousPreviewFileEntryId =
			fragmentComposition.getPreviewFileEntryId();

		fragmentComposition.setPreviewFileEntryId(previewFileEntryId);

		fragmentComposition = fragmentCompositionPersistence.update(
			fragmentComposition);

		if ((previewFileEntryId == 0) && (previousPreviewFileEntryId > 0)) {
			_portletFileRepository.deletePortletFileEntry(
				previousPreviewFileEntryId);
		}

		return fragmentComposition;
	}

	@Override
	public FragmentComposition updateFragmentComposition(
			long userId, long fragmentCompositionId, long fragmentCollectionId,
			String name, String description, String data,
			long previewFileEntryId, int status)
		throws PortalException {

		FragmentComposition fragmentComposition =
			fragmentCompositionPersistence.findByPrimaryKey(
				fragmentCompositionId);

		_validateName(name);
		_validateDescription(description);

		User user = _userLocalService.getUser(userId);

		fragmentComposition.setModifiedDate(new Date());
		fragmentComposition.setFragmentCollectionId(fragmentCollectionId);
		fragmentComposition.setName(name);
		fragmentComposition.setDescription(description);
		fragmentComposition.setData(data);
		fragmentComposition.setPreviewFileEntryId(previewFileEntryId);
		fragmentComposition.setStatus(status);
		fragmentComposition.setStatusByUserId(userId);
		fragmentComposition.setStatusByUserName(user.getFullName());
		fragmentComposition.setStatusDate(new Date());

		return fragmentCompositionPersistence.update(fragmentComposition);
	}

	@Override
	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, String name)
		throws PortalException {

		FragmentComposition fragmentComposition =
			fragmentCompositionPersistence.findByPrimaryKey(
				fragmentCompositionId);

		_validateName(name);

		fragmentComposition.setName(name);

		return fragmentCompositionPersistence.update(fragmentComposition);
	}

	private void _validateDescription(String description)
		throws PortalException {

		if (Validator.isNull(description)) {
			return;
		}

		int descriptionMaxLength = ModelHintsUtil.getMaxLength(
			FragmentComposition.class.getName(), "description");

		if (description.length() > descriptionMaxLength) {
			throw new FragmentCompositionDescriptionException(
				"Maximum length of description exceeded");
		}
	}

	private void _validateFragmentCompositionKey(
			long groupId, String fragmentCompositionKey)
		throws PortalException {

		fragmentCompositionKey =
			FragmentCompositionKeyUtil.getFragmentCompositionKey(
				fragmentCompositionKey);

		FragmentComposition fragmentComposition =
			fragmentCompositionPersistence.fetchByG_FCK(
				groupId, fragmentCompositionKey);

		if (fragmentComposition != null) {
			throw new DuplicateFragmentCompositionKeyException();
		}
	}

	private void _validateName(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new FragmentCompositionNameException("Name must not be null");
		}

		if (name.contains(StringPool.PERIOD) ||
			name.contains(StringPool.SLASH)) {

			throw new FragmentCompositionNameException(
				"Name contains invalid characters");
		}

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			FragmentComposition.class.getName(), "name");

		if (name.length() > nameMaxLength) {
			throw new FragmentCompositionNameException(
				"Maximum length of name exceeded");
		}
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}