/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the PatcherTicketHint service. Represents a row in the &quot;OSBPatcher_PatcherTicketHint&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherTicketHintModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.osb.patcher.model.impl.PatcherTicketHintImpl"
)
@ProviderType
public interface PatcherTicketHint
	extends PatcherTicketHintModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.osb.patcher.model.impl.PatcherTicketHintImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<PatcherTicketHint, Long>
		PATCHER_TICKET_HINT_ID_ACCESSOR =
			new Accessor<PatcherTicketHint, Long>() {

				@Override
				public Long get(PatcherTicketHint patcherTicketHint) {
					return patcherTicketHint.getPatcherTicketHintId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<PatcherTicketHint> getTypeClass() {
					return PatcherTicketHint.class;
				}

			};

}