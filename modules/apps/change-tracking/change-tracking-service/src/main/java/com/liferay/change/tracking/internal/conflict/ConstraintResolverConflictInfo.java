/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.conflict;

import com.liferay.change.tracking.spi.resolver.ConstraintResolver;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Preston Crary
 */
public class ConstraintResolverConflictInfo extends BaseConflictInfo {

	public ConstraintResolverConflictInfo(
		ConstraintResolver<?> constraintResolver, boolean resolved,
		long sourcePrimaryKey, long targetPrimaryKey) {

		_constraintResolver = constraintResolver;
		_resolved = resolved;
		_sourcePrimaryKey = sourcePrimaryKey;
		_targetPrimaryKey = targetPrimaryKey;
	}

	@Override
	public long getCTAutoResolutionInfoId() {
		return _ctAutoResolutionInfoId;
	}

	@Override
	public String getConflictDescription(ResourceBundle resourceBundle) {
		return ResourceBundleUtil.getString(
			resourceBundle, _constraintResolver.getConflictDescriptionKey());
	}

	public ConstraintResolver<?> getConstraintResolver() {
		return _constraintResolver;
	}

	@Override
	public String getResolutionDescription(ResourceBundle resourceBundle) {
		return ResourceBundleUtil.getString(
			resourceBundle, _constraintResolver.getResolutionDescriptionKey());
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return _constraintResolver.getResourceBundle(locale);
	}

	@Override
	public long getSourcePrimaryKey() {
		return _sourcePrimaryKey;
	}

	@Override
	public long getTargetPrimaryKey() {
		return _targetPrimaryKey;
	}

	@Override
	public boolean isResolved() {
		return _resolved;
	}

	public void setCtAutoResolutionInfoId(long ctAutoResolutionInfoId) {
		_ctAutoResolutionInfoId = ctAutoResolutionInfoId;
	}

	private final ConstraintResolver<?> _constraintResolver;
	private long _ctAutoResolutionInfoId;
	private final boolean _resolved;
	private final long _sourcePrimaryKey;
	private final long _targetPrimaryKey;

}