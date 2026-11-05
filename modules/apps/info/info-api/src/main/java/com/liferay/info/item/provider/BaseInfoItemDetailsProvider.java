/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.provider;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ScopeUtil;

import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseInfoItemDetailsProvider
	<T extends ExternalReferenceCodeModel & GroupedModel>
		implements InfoItemDetailsProvider<T> {

	@Override
	public InfoItemDetails getInfoItemDetails(
		long groupId,
		Class<? extends InfoItemIdentifier> infoItemIdentifierClass, T model) {

		InfoItemIdentifierFactory<T> infoItemIdentifierFactory =
			getInfoItemIdentifierFactory();

		if (Objects.equals(
				infoItemIdentifierClass, ClassPKInfoItemIdentifier.class)) {

			return _getInfoItemDetails(
				infoItemIdentifierFactory.createClassPKInfoItemIdentifier(
					model));
		}

		if (Objects.equals(
				infoItemIdentifierClass, ERCInfoItemIdentifier.class)) {

			ERCInfoItemIdentifier ercInfoItemIdentifier =
				infoItemIdentifierFactory.createERCInfoItemIdentifier(
					model.getExternalReferenceCode(),
					_getScopeExternalReferenceCode(groupId, model));

			if (ercInfoItemIdentifier == null) {
				return null;
			}

			return _getInfoItemDetails(ercInfoItemIdentifier);
		}

		if (Objects.equals(
				infoItemIdentifierClass, GroupKeyInfoItemIdentifier.class)) {

			GroupKeyInfoItemIdentifier groupKeyInfoItemIdentifier =
				infoItemIdentifierFactory.createGroupKeyInfoItemIdentifier(
					model.getGroupId(), model);

			if (groupKeyInfoItemIdentifier == null) {
				return null;
			}

			return _getInfoItemDetails(groupKeyInfoItemIdentifier);
		}

		if (Objects.equals(
				infoItemIdentifierClass,
				GroupUrlTitleInfoItemIdentifier.class)) {

			GroupUrlTitleInfoItemIdentifier groupUrlTitleInfoItemIdentifier =
				infoItemIdentifierFactory.createGroupUrlTitleInfoItemIdentifier(
					model.getGroupId(), model);

			if (groupUrlTitleInfoItemIdentifier == null) {
				return null;
			}

			return _getInfoItemDetails(groupUrlTitleInfoItemIdentifier);
		}

		return null;
	}

	@Override
	public InfoItemDetails getInfoItemDetails(T model) {
		return getInfoItemDetails(
			ScopeUtil.getScopeGroupId(0), ClassPKInfoItemIdentifier.class,
			model);
	}

	public interface InfoItemIdentifierFactory<T> {

		public ClassPKInfoItemIdentifier createClassPKInfoItemIdentifier(
			T model);

		public default ERCInfoItemIdentifier createERCInfoItemIdentifier(
			String externalReferenceCode, String scopeExternalReferenceCode) {

			return null;
		}

		public default GroupKeyInfoItemIdentifier
			createGroupKeyInfoItemIdentifier(long groupId, T model) {

			return null;
		}

		public default GroupUrlTitleInfoItemIdentifier
			createGroupUrlTitleInfoItemIdentifier(long groupId, T model) {

			return null;
		}

	}

	protected abstract InfoItemIdentifierFactory<T>
		getInfoItemIdentifierFactory();

	private InfoItemDetails _getInfoItemDetails(
		InfoItemIdentifier infoItemIdentifier) {

		InfoItemClassDetails infoItemClassDetails = getInfoItemClassDetails();

		return new InfoItemDetails(
			infoItemClassDetails,
			new InfoItemReference(
				infoItemClassDetails.getClassName(), infoItemIdentifier));
	}

	private String _getScopeExternalReferenceCode(
		long groupId, GroupedModel groupedModel) {

		if (groupId == groupedModel.getGroupId()) {
			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(
			groupedModel.getGroupId());

		if (group == null) {
			return null;
		}

		return group.getExternalReferenceCode();
	}

}