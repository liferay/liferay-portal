/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.service.base.PatcherProductVersionLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherProductVersionNameComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherProductVersion",
	service = AopService.class
)
public class PatcherProductVersionLocalServiceImpl
	extends PatcherProductVersionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherProductVersion addPatcherProductVersion(
			long userId, int fixDeliveryMethod, String moduleFolderName,
			String name)
		throws PortalException {

		_validateFixDeliveryMethod(fixDeliveryMethod, moduleFolderName);
		_validateName(0, name);

		PatcherProductVersion patcherProductVersion =
			patcherProductVersionPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		patcherProductVersion.setCompanyId(user.getCompanyId());
		patcherProductVersion.setUserId(user.getUserId());
		patcherProductVersion.setUserName(user.getFullName());

		patcherProductVersion.setCreateDate(new Date());
		patcherProductVersion.setModifiedDate(new Date());
		patcherProductVersion.setFixDeliveryMethod(fixDeliveryMethod);
		patcherProductVersion.setModuleFolderName(moduleFolderName);
		patcherProductVersion.setName(name);

		return patcherProductVersionPersistence.update(patcherProductVersion);
	}

	@Override
	public PatcherProductVersion fetchPatcherProductVersion(String name) {
		return patcherProductVersionPersistence.fetchByName(name);
	}

	@Override
	public List<PatcherProductVersion> getPatcherProductVersions() {
		return patcherProductVersionPersistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherProductVersionNameComparator.getInstance(true));
	}

	@Override
	public List<PatcherProductVersion> getPatcherProductVersions(
		int fixDeliveryMethod) {

		return patcherProductVersionPersistence.findByFixDeliveryMethod(
			fixDeliveryMethod);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherProductVersion updatePatcherProductVersion(
			long patcherProductVersionId, int fixDeliveryMethod,
			String moduleFolderName, String name)
		throws PortalException {

		_validateFixDeliveryMethod(fixDeliveryMethod, moduleFolderName);
		_validateName(patcherProductVersionId, name);

		PatcherProductVersion patcherProductVersion =
			patcherProductVersionPersistence.findByPrimaryKey(
				patcherProductVersionId);

		patcherProductVersion.setModifiedDate(new Date());
		patcherProductVersion.setFixDeliveryMethod(fixDeliveryMethod);
		patcherProductVersion.setModuleFolderName(moduleFolderName);
		patcherProductVersion.setName(name);

		return patcherProductVersionPersistence.update(patcherProductVersion);
	}

	private void _validateFixDeliveryMethod(
			int fixDeliveryMethod, String moduleFolderName)
		throws PortalException {

		if (fixDeliveryMethod < 0) {
			throw new PortalException("the-fix-delivery-method-is-invalid");
		}
		else if ((fixDeliveryMethod ==
					PatcherProductVersionConstants.
						TYPE_FIX_DELIVERY_METHOD_MARKETPLACE_RELEASE) &&
				 Validator.isNull(moduleFolderName)) {

			throw new PortalException(
				"the-module-folder-name-is-required-for-marketplace-apps");
		}
	}

	private void _validateName(long patcherProductVersionId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new PortalException("the-product-version-name-is-invalid");
		}

		PatcherProductVersion patcherProductVersion =
			patcherProductVersionPersistence.fetchByName(name);

		if ((patcherProductVersion != null) &&
			(patcherProductVersion.getPatcherProductVersionId() !=
				patcherProductVersionId)) {

			throw new PortalException(
				"the-product-version-name-already-exists");
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}