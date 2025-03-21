/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.application;

import com.liferay.osb.faro.web.internal.constants.FaroConstants;
import com.liferay.osb.faro.web.internal.controller.contacts.AccountController;
import com.liferay.osb.faro.web.internal.controller.contacts.ActivityController;
import com.liferay.osb.faro.web.internal.controller.contacts.ActivityGroupController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsCardTemplateController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutController;
import com.liferay.osb.faro.web.internal.controller.contacts.ContactsLayoutTemplateController;
import com.liferay.osb.faro.web.internal.controller.contacts.DataSourceController;
import com.liferay.osb.faro.web.internal.controller.contacts.FieldController;
import com.liferay.osb.faro.web.internal.controller.contacts.FieldMappingController;
import com.liferay.osb.faro.web.internal.controller.contacts.IndividualController;
import com.liferay.osb.faro.web.internal.controller.contacts.IndividualSegmentController;
import com.liferay.osb.faro.web.internal.controller.contacts.InterestController;
import com.liferay.osb.faro.web.internal.controller.contacts.PagesVisitedController;
import com.liferay.osb.faro.web.internal.controller.contacts.SessionController;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@ApplicationPath("/" + FaroConstants.APPLICATION_CONTACTS)
@Component(property = "jaxrs.application=true", service = Application.class)
public class ContactsApplication extends BaseApplication {

	@Override
	public Set<Object> getControllers() {
		Set<Object> controllers = new HashSet<>();

		controllers.add(_accountController);
		controllers.add(_activityController);
		controllers.add(_activityGroupController);
		controllers.add(_contactsCardController);
		controllers.add(_contactsCardTemplateController);
		controllers.add(_contactsLayoutController);
		controllers.add(_contactsLayoutTemplateController);
		controllers.add(_dataSourceController);
		controllers.add(_fieldController);
		controllers.add(_fieldMappingController);
		controllers.add(_individualController);
		controllers.add(_individualSegmentController);
		controllers.add(_interestController);
		controllers.add(_pagesVisitedController);
		controllers.add(_sessionController);

		return controllers;
	}

	@Reference
	private AccountController _accountController;

	@Reference
	private ActivityController _activityController;

	@Reference
	private ActivityGroupController _activityGroupController;

	@Reference
	private ContactsCardController _contactsCardController;

	@Reference
	private ContactsCardTemplateController _contactsCardTemplateController;

	@Reference
	private ContactsLayoutController _contactsLayoutController;

	@Reference
	private ContactsLayoutTemplateController _contactsLayoutTemplateController;

	@Reference
	private DataSourceController _dataSourceController;

	@Reference
	private FieldController _fieldController;

	@Reference
	private FieldMappingController _fieldMappingController;

	@Reference
	private IndividualController _individualController;

	@Reference
	private IndividualSegmentController _individualSegmentController;

	@Reference
	private InterestController _interestController;

	@Reference
	private PagesVisitedController _pagesVisitedController;

	@Reference
	private SessionController _sessionController;

}