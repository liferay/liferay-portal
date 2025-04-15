/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.display.context;

import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfigurationFactory;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.document.library.configuration.DLFileOrderConfigurationProvider;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.document.library.kernel.versioning.VersioningStrategy;
import com.liferay.document.library.web.internal.display.context.helper.DLRequestHelper;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.trash.TrashHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Adolfo Pérez
 */
@Component(service = DLAdminDisplayContextProvider.class)
public class DLAdminDisplayContextProvider {

	public DLAdminDisplayContext getDLAdminDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);

		DLRequestHelper dlRequestHelper = new DLRequestHelper(
			httpServletRequest);

		return new DLAdminDisplayContext(
			_getAssetAutoTaggerConfiguration(dlRequestHelper),
			_configurationProvider, _dlFileOrderConfigurationProvider,
			httpServletRequest, dlRequestHelper.getLiferayPortletRequest(),
			dlRequestHelper.getLiferayPortletResponse(), _trashHelper,
			_versioningStrategy);
	}

	public DLAdminManagementToolbarDisplayContext
		getDLAdminManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			DLAdminDisplayContext dlAdminDisplayContext) {

		DLRequestHelper dlRequestHelper = new DLRequestHelper(
			httpServletRequest);

		return new DLAdminManagementToolbarDisplayContext(
			_assetVocabularyService, dlAdminDisplayContext,
			_dlFileEntryTypeService, _dlTrashHelper, httpServletRequest,
			_itemSelector, dlRequestHelper.getLiferayPortletRequest(),
			dlRequestHelper.getLiferayPortletResponse(),
			_siteConnectedGroupGroupProvider);
	}

	private AssetAutoTaggerConfiguration _getAssetAutoTaggerConfiguration(
		DLRequestHelper dlRequestHelper) {

		try {
			return _assetAutoTaggerConfigurationFactory.
				getGroupAssetAutoTaggerConfiguration(
					_groupLocalService.getGroup(
						dlRequestHelper.getSiteGroupId()));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Reference
	private AssetAutoTaggerConfigurationFactory
		_assetAutoTaggerConfigurationFactory;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Reference
	private DLFileOrderConfigurationProvider _dlFileOrderConfigurationProvider;

	@Reference
	private DLTrashHelper _dlTrashHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

	@Reference
	private TrashHelper _trashHelper;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile VersioningStrategy _versioningStrategy;

}