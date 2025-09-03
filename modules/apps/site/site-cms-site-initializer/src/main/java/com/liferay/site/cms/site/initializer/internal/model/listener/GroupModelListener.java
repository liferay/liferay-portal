/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;

import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.UnicodeProperties;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onBeforeRemove(Group group) throws ModelListenerException {
		try {
			_onBeforeRemove(group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _onBeforeRemove(Group group) throws Exception {
		if ((group.getType() != GroupConstants.TYPE_DEPOT) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-17564")) {

			return;
		}

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					group.getGroupId(), group.getCompanyId());
			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
					group.getGroupId(), group.getCompanyId());
		}
	}

	@Override
	public void onAfterUpdate(Group originalGroup, Group group) throws ModelListenerException {
		try{
			_onAfterUpdate(originalGroup, group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _onAfterUpdate(Group originalGroup, Group group)
		throws Exception {
		if(group.isDepot()){

			UnicodeProperties unicodeProperties = _getUnicodeProperties(group.getCompanyId(), group.getExternalReferenceCode());

			if(Objects.equals(
				originalGroup.getTypeSettingsProperty("trashEnabled"),
				group.getTypeSettingsProperty("trashEnabled"))){
				return;
			}

			if(!unicodeProperties.containsKey("trashEnabled")){
				return;
			}

			DepotEntry depotEntry = _depotEntryService.fetchGroupDepotEntry(group.getGroupId());

			List<DepotEntryGroupRel> depotEntryGroupRels =
				_depotEntryGroupRelService.getDepotEntryGroupRels(
					depotEntry, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			List<Long> groupIds;

			if (!depotEntryGroupRels.isEmpty()) {
				groupIds = _getGroupIds(
					_groupLocalService.getGroup(
						depotEntryGroupRels.get(
							0
						).getToGroupId()));
			}
			else {
				groupIds = _getGroupIds(depotEntry.getGroup());
			}

			if (!groupIds.isEmpty() ||
				Objects.equals(
					unicodeProperties.getProperty("trashEnabled"), "true") && !unicodeProperties.getProperty("trashEnabled").isEmpty()) {

				for (DepotEntryGroupRel depotEntryGroupRel : depotEntryGroupRels) {
					Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
						depotEntryGroupRel.getToGroupId(), false, "/recycle-bin");

					layout.setHidden(false);

					_layoutLocalService.updateLayout(layout);
				}
			}

			if ((groupIds.isEmpty()) &&
				Objects.equals(
					unicodeProperties.getProperty("trashEnabled"), "false") && !unicodeProperties.getProperty("trashEnabled").isEmpty()) {

				for (DepotEntryGroupRel depotEntryGroupRel :
					depotEntryGroupRels) {

					Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
						depotEntryGroupRel.getToGroupId(), false,
						"/recycle-bin");

					layout.setHidden(true);

					_layoutLocalService.updateLayout(layout);
				}
			}
		}
	}

	private List<Long> _getGroupIds(Group group) throws Exception {
		List<Long> ids = new ArrayList<>();

		long groupId = group.getGroupId();

		List<DepotEntry> depots =
			_depotEntryService.getGroupConnectedDepotEntries(
				groupId, -1, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (DepotEntry depot : depots) {
			Group depotGroup = _groupLocalService.fetchGroup(depot.getGroupId());

			if ((depotGroup != null) && _isTrashEnabled(depotGroup)) {
				ids.add(depotGroup.getGroupId());
			}
		}

		Group scopeGroup = _groupLocalService.fetchGroup(groupId);

		if ((scopeGroup != null) && scopeGroup.isDepot() &&
			_isTrashEnabled(scopeGroup)) {

			ids.add(groupId);
		}

		return ids;
	}

	private UnicodeProperties _getUnicodeProperties(
		long companyId, String externalReferenceCode) {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (group != null) {
			return group.getTypeSettingsProperties();
		}

		return new UnicodeProperties(true);
	}

	private boolean _isTrashEnabled(Group group) {
		return Boolean.parseBoolean(
			group.getTypeSettingsProperty("trashEnabled"));
	}

	@Reference
	private DepotEntryGroupRelService _depotEntryGroupRelService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}