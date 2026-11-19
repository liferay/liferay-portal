/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the AuditEventAccountEntryRel service. Represents a row in the &quot;Audit_EventAccountEntryRel&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see AuditEventAccountEntryRelModel
 * @generated
 */
@ImplementationClassName(
	"com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelImpl"
)
@ProviderType
public interface AuditEventAccountEntryRel
	extends AuditEventAccountEntryRelModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.security.audit.storage.model.impl.AuditEventAccountEntryRelImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<AuditEventAccountEntryRel, Long>
		AUDIT_EVENT_ACCOUNT_ENTRY_REL_ID_ACCESSOR =
			new Accessor<AuditEventAccountEntryRel, Long>() {

				@Override
				public Long get(
					AuditEventAccountEntryRel auditEventAccountEntryRel) {

					return auditEventAccountEntryRel.
						getAuditEventAccountEntryRelId();
				}

				@Override
				public Class<Long> getAttributeClass() {
					return Long.class;
				}

				@Override
				public Class<AuditEventAccountEntryRel> getTypeClass() {
					return AuditEventAccountEntryRel.class;
				}

			};

}
// LIFERAY-SERVICE-BUILDER-HASH:-727534309