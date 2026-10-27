/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.service.taglib.util;

import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalServiceUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class ClientExtensionDynamicIncludeUtil {

	public static List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		Layout layout, String type) {

		List<ClientExtensionEntryRel> clientExtensionEntryRels =
			new ArrayList<>();

		LayoutSet layoutSet = layout.getLayoutSet();

		List<ClientExtensionEntryRel> layoutSetClientExtensionEntryRels =
			ClientExtensionEntryRelLocalServiceUtil.getClientExtensionEntryRels(
				PortalUtil.getClassNameId(LayoutSet.class),
				layoutSet.getLayoutSetId(), type);

		for (ClientExtensionEntryRel layoutSetClientExtensionEntryRel :
				layoutSetClientExtensionEntryRels) {

			if (ListUtil.exists(
					clientExtensionEntryRels,
					clientExtensionEntryRel -> Objects.equals(
						layoutSetClientExtensionEntryRel.
							getCETExternalReferenceCode(),
						clientExtensionEntryRel.
							getCETExternalReferenceCode()))) {

				continue;
			}

			clientExtensionEntryRels.add(layoutSetClientExtensionEntryRel);
		}

		Layout masterLayout = null;

		long masterLayoutPlid = layout.getMasterLayoutPlid();

		if (masterLayoutPlid > 0) {
			masterLayout = LayoutLocalServiceUtil.fetchLayout(masterLayoutPlid);
		}

		if (masterLayout != null) {
			List<ClientExtensionEntryRel> masterLayoutClientExtensionEntryRels =
				ClientExtensionEntryRelLocalServiceUtil.
					getClientExtensionEntryRels(
						PortalUtil.getClassNameId(Layout.class),
						masterLayout.getPlid(), type);

			for (ClientExtensionEntryRel masterLayoutClientExtensionEntryRel :
					masterLayoutClientExtensionEntryRels) {

				if (ListUtil.exists(
						clientExtensionEntryRels,
						clientExtensionEntryRel -> Objects.equals(
							masterLayoutClientExtensionEntryRel.
								getCETExternalReferenceCode(),
							clientExtensionEntryRel.
								getCETExternalReferenceCode()))) {

					continue;
				}

				clientExtensionEntryRels.add(
					masterLayoutClientExtensionEntryRel);
			}
		}

		List<ClientExtensionEntryRel> layoutClientExtensionEntryRels =
			new ArrayList<>(
				ClientExtensionEntryRelLocalServiceUtil.
					getClientExtensionEntryRels(
						PortalUtil.getClassNameId(Layout.class),
						layout.getPlid(), type));

		for (ClientExtensionEntryRel layoutClientExtensionEntryRel :
				layoutClientExtensionEntryRels) {

			if (ListUtil.exists(
					clientExtensionEntryRels,
					clientExtensionEntryRel -> Objects.equals(
						layoutClientExtensionEntryRel.
							getCETExternalReferenceCode(),
						clientExtensionEntryRel.
							getCETExternalReferenceCode()))) {

				continue;
			}

			clientExtensionEntryRels.add(layoutClientExtensionEntryRel);
		}

		return clientExtensionEntryRels;
	}

}