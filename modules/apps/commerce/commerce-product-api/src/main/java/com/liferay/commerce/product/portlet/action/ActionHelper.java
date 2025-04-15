/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.portlet.action;

import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.type.CPType;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.IOException;

import java.util.List;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public interface ActionHelper {

	public List<CPAttachmentFileEntry> getCPAttachmentFileEntries(
			PortletRequest portletRequest)
		throws PortalException;

	public CPAttachmentFileEntry getCPAttachmentFileEntry(
			PortletRequest portletRequest)
		throws PortalException;

	public CPDefinition getCPDefinition(PortletRequest portletRequest)
		throws PortalException;

	public CPDefinitionLink getCPDefinitionLink(PortletRequest portletRequest)
		throws PortalException;

	public List<CPDefinitionLink> getCPDefinitionLinks(
			PortletRequest portletRequest)
		throws PortalException;

	public CPDefinitionOptionRel getCPDefinitionOptionRel(
			PortletRequest portletRequest)
		throws PortalException;

	public List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
			PortletRequest portletRequest)
		throws PortalException;

	public CPDefinitionOptionValueRel getCPDefinitionOptionValueRel(
			PortletRequest portletRequest)
		throws PortalException;

	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId)
		throws PortalException;

	public List<CPDefinitionOptionValueRel> getCPDefinitionOptionValueRels(
			PortletRequest portletRequest)
		throws PortalException;

	public List<CPDefinition> getCPDefinitions(PortletRequest portletRequest)
		throws PortalException;

	public CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValue(
				PortletRequest portletRequest)
		throws PortalException;

	public List<CPDefinitionSpecificationOptionValue>
			getCPDefinitionSpecificationOptionValues(
				PortletRequest portletRequest)
		throws PortalException;

	public CPInstance getCPInstance(PortletRequest portletRequest)
		throws PortalException;

	public List<CPInstance> getCPInstances(PortletRequest portletRequest)
		throws PortalException;

	public CPInstanceUnitOfMeasure getCPInstanceUnitOfMeasure(
			PortletRequest portletRequest)
		throws PortalException;

	public CPType getCPType(String name);

	public List<CPType> getCPTypes();

	public List<CPDefinitionOptionRel> getSkuContributorCPDefinitionOptionRels(
			long cpDefinitionId)
		throws PortalException;

	public void writeJSON(
			PortletRequest portletRequest, ActionResponse actionResponse,
			Object object)
		throws IOException;

}