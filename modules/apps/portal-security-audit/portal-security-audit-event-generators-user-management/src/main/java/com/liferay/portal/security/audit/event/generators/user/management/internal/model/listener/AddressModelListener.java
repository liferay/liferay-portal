/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(service = ModelListener.class)
public class AddressModelListener extends BaseModelListener<Address> {

	public void onBeforeUpdate(Address originalAddress, Address address)
		throws ModelListenerException {

		try {
			String className = address.getClassName();

			if (!className.equals(User.class.getName())) {
				return;
			}

			List<Attribute> attributes = getModifiedAttributes(
				originalAddress, address);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						User.class.getName(), address.getClassPK(),
						EventTypes.UPDATE, attributes);

				auditMessage.setCompanyId(address.getCompanyId());

				_auditRouter.route(auditMessage);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected List<Attribute> getModifiedAttributes(
		Address originalAddress, Address address) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			address, originalAddress);

		attributesBuilder.add("city");
		attributesBuilder.add("countryId");
		attributesBuilder.add("listTypeId");
		attributesBuilder.add("mailing");
		attributesBuilder.add("primary");
		attributesBuilder.add("regionId");
		attributesBuilder.add("street1");
		attributesBuilder.add("street2");
		attributesBuilder.add("street3");
		attributesBuilder.add("zip");

		return attributesBuilder.getAttributes();
	}

	@Reference
	private AuditRouter _auditRouter;

}