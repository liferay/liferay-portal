/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.conflict;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Preston Crary
 */
@ProviderType
public interface ConflictInfo {

	public long getCTAutoResolutionInfoId();

	public String getConflictDescription(ResourceBundle resourceBundle);

	public String getResolutionDescription(ResourceBundle resourceBundle);

	public ResourceBundle getResourceBundle(Locale locale);

	public long getSourcePrimaryKey();

	public long getTargetPrimaryKey();

	public boolean isResolved();

}