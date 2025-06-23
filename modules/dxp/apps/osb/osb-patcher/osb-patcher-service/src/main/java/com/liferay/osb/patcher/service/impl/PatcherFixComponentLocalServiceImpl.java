/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.osb.patcher.service.base.PatcherFixComponentLocalServiceBaseImpl;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackPersistence;
import com.liferay.osb.patcher.util.comparator.PatcherFixComponentNameComparator;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherFixComponent",
	service = AopService.class
)
public class PatcherFixComponentLocalServiceImpl
	extends PatcherFixComponentLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFixComponent addPatcherFixComponent(long userId, String name)
		throws PortalException {

		_validateName(0, name);

		PatcherFixComponent patcherFixComponent =
			patcherFixComponentPersistence.create(
				counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		patcherFixComponent.setCompanyId(user.getCompanyId());
		patcherFixComponent.setUserId(user.getUserId());
		patcherFixComponent.setUserName(user.getFullName());

		patcherFixComponent.setCreateDate(new Date());
		patcherFixComponent.setModifiedDate(new Date());
		patcherFixComponent.setName(name);

		return patcherFixComponentPersistence.update(patcherFixComponent);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public PatcherFixComponent deletePatcherFixComponent(
			long patcherFixComponentId)
		throws PortalException {

		PatcherFixComponent patcherFixComponent =
			patcherFixComponentPersistence.findByPrimaryKey(
				patcherFixComponentId);

		List<PatcherFixPack> patcherFixPacks =
			_patcherFixPackPersistence.findByPatcherFixComponentId(
				patcherFixComponent.getPatcherFixComponentId());

		if (!patcherFixPacks.isEmpty()) {
			throw new PortalException(
				"the-component-cannot-be-deleted-because-it-has-an-" +
					"associated-fix-pack");
		}

		return patcherFixComponentPersistence.remove(patcherFixComponentId);
	}

	@Override
	public PatcherFixComponent fetchPatcherFixComponent(String name) {
		return patcherFixComponentPersistence.fetchByName(name);
	}

	@Override
	public List<PatcherFixComponent> getPatcherFixComponents() {
		return patcherFixComponentPersistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherFixComponentNameComparator.getInstance(true));
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public PatcherFixComponent updatePatcherFixComponent(
			long patcherFixComponentId, String name)
		throws PortalException {

		_validateName(patcherFixComponentId, name);
		_validateFixPacks(patcherFixComponentId, name);

		PatcherFixComponent patcherFixComponent =
			patcherFixComponentPersistence.findByPrimaryKey(
				patcherFixComponentId);

		patcherFixComponent.setModifiedDate(new Date());
		patcherFixComponent.setName(name);

		return patcherFixComponentPersistence.update(patcherFixComponent);
	}

	private void _validateFixPacks(long patcherFixComponentId, String name)
		throws PortalException {

		List<PatcherFixPack> patcherFixPacks =
			_patcherFixPackPersistence.findByPatcherFixComponentId(
				patcherFixComponentId);

		if (!patcherFixPacks.isEmpty()) {
			throw new PortalException(
				"the-component's-name-cannot-change-when-the-component-is-" +
					"used-in-a-fix-pack");
		}

		List<PatcherFix> patcherFixes = _patcherFixLocalService.getPatcherFixes(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherFix patcherFix : patcherFixes) {
			String dependencies = patcherFix.getDependencies();

			String[] componentNames = dependencies.split("(,|->)");

			for (String componentName : componentNames) {
				if (componentName.equals(name)) {
					throw new PortalException(
						"the-component's-name-cannot-be-changed-while-it-is-" +
							"in-a-fix-dependency");
				}
			}
		}
	}

	private void _validateName(long patcherFixComponentId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new PortalException("the-name-is-invalid");
		}

		Pattern pattern = Pattern.compile(_FIX_COMPONENT_REGEX);

		Matcher matcher = pattern.matcher(name);

		if (!matcher.find()) {
			throw new PortalException("the-name-is-invalid");
		}

		PatcherFixComponent patcherFixComponent =
			patcherFixComponentPersistence.fetchByName(name);

		if ((patcherFixComponent != null) &&
			(patcherFixComponent.getPatcherFixComponentId() !=
				patcherFixComponentId)) {

			throw new PortalException("the-component-name-already-exists");
		}
	}

	private static final String _FIX_COMPONENT_REGEX = "^([a-z-]+)$";

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

	@Reference
	private PatcherFixPackPersistence _patcherFixPackPersistence;

	@Reference
	private UserLocalService _userLocalService;

}