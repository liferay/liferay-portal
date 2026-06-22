/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.Individual;
import com.liferay.osb.faro.engine.client.model.IndividualSegment;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualDisplay;
import com.liferay.osb.faro.web.internal.model.display.contacts.IndividualSegmentDisplay;
import com.liferay.osb.faro.web.internal.model.display.main.FaroEntityDisplay;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(service = ContactsHelper.class)
public class ContactsHelper {

	public FaroEntityDisplay getContactsEntityDisplay(
		FaroProject faroProject, String contactsEntityId, int type) {

		if (type == FaroConstants.TYPE_INDIVIDUAL) {
			Individual individual = _contactsEngineClient.getIndividual(
				faroProject, contactsEntityId, null);

			return new IndividualDisplay(individual);
		}

		IndividualSegment individualSegment =
			_contactsEngineClient.getIndividualSegment(
				faroProject, contactsEntityId, false);

		return new IndividualSegmentDisplay(individualSegment);
	}

	public String getOwnerType(int type) {
		if (type == FaroConstants.TYPE_ACCOUNT) {
			return FieldMappingConstants.OWNER_TYPE_ACCOUNT;
		}
		else if (type == FaroConstants.TYPE_INDIVIDUAL) {
			return FieldMappingConstants.OWNER_TYPE_INDIVIDUAL;
		}

		return FieldMappingConstants.OWNER_TYPE_INDIVIDUAL_SEGMENT;
	}

	@Reference
	private ContactsEngineClient _contactsEngineClient;

}