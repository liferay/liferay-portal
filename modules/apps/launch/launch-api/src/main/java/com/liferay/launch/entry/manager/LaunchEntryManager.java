/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.entry.manager;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Alejandro Tardín
 * @author David Truong
 */
@ProviderType
public interface LaunchEntryManager {

	public Draft addDraft(long classPK, String versionKey)
		throws PortalException;

	public void deleteDraft(long classPK, long draftId) throws PortalException;

	public Draft fetchDraft(long classPK, long draftId);

	public Item fetchItem(long classPK);

	public Version fetchPublishedVersion(long classPK);

	public Version fetchVersion(long classPK, String versionKey);

	public long getClassPK(Map<String, String> selectedItemData);

	public ItemSelectorCriterion getItemSelectorCriterion();

	public String getLabel(Locale locale);

	public Class<?> getModelClass();

	public List<Version> getVersions(long classPK);

	public Version publishDraft(long classPK, long draftId)
		throws PortalException;

	public interface Draft {

		public long getDraftId();

		public String getEditURL(
			String redirect,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory);

		public Date getModifiedDate();

		public Serializable getPreviewPrimaryKey();

		public int getStatus();

		public String getUserName();

	}

	public interface Item {

		public long getGroupId();

		public Serializable getPrimaryKey();

		public String getTitle(Locale locale);

		public String getTypeName(Locale locale);

	}

	public interface Version {

		public String getLabel(Locale locale);

		public Date getModifiedDate();

		public Serializable getPreviewPrimaryKey();

		public int getStatus();

		public String getUserName();

		public String getVersionKey();

	}

}