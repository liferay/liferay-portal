/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
	service = AssetRendererFactory.class
)
public class MBThreadAssetRendererFactory
	extends BaseAssetRendererFactory<MBThread> {

	public static final String TYPE = "thread";

	public MBThreadAssetRendererFactory() {
		setCategorizable(false);
		setLinkable(false);
		setPortletId(MBPortletKeys.MESSAGE_BOARDS);
		setSearchable(false);
		setSelectable(false);
	}

	@Override
	public AssetRenderer<MBThread> getAssetRenderer(long classPK, int type)
		throws PortalException {

		return new MBThreadAssetRenderer(
			_mbThreadLocalService.getThread(classPK));
	}

	@Override
	public String getClassName() {
		return MBThread.class.getName();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

}