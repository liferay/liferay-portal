/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/export_fragment_compositions_and_fragment_entries"
	},
	service = MVCResourceCommand.class
)
public class ExportFragmentCompositionsAndFragmentEntriesMVCResourceCommand
	implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		long[] exportFragmentCompositionIds = null;
		long[] exportFragmentEntryIds = null;

		long fragmentCompositionId = ParamUtil.getLong(
			resourceRequest, "fragmentCompositionId");
		long fragmentEntryId = ParamUtil.getLong(
			resourceRequest, "fragmentEntryId");

		if (fragmentCompositionId > 0) {
			exportFragmentCompositionIds = new long[] {fragmentCompositionId};
		}
		else if (fragmentEntryId > 0) {
			exportFragmentEntryIds = new long[] {fragmentEntryId};
		}
		else {
			exportFragmentCompositionIds = ParamUtil.getLongValues(
				resourceRequest, "rowIdsFragmentComposition");
			exportFragmentEntryIds = ParamUtil.getLongValues(
				resourceRequest, "rowIdsFragmentEntry");
		}

		try {
			List<FragmentComposition> fragmentCompositions = new ArrayList<>();

			if (ArrayUtil.isNotEmpty(exportFragmentCompositionIds)) {
				for (long exportFragmentCompositionId :
						exportFragmentCompositionIds) {

					fragmentCompositions.add(
						_fragmentCompositionService.fetchFragmentComposition(
							exportFragmentCompositionId));
				}
			}

			List<FragmentEntry> fragmentEntries = new ArrayList<>();

			if (ArrayUtil.isNotEmpty(exportFragmentEntryIds)) {
				for (long exportFragmentEntryId : exportFragmentEntryIds) {
					fragmentEntries.add(
						_fragmentEntryService.fetchFragmentEntry(
							exportFragmentEntryId));
				}
			}

			ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

			for (FragmentComposition fragmentComposition :
					fragmentCompositions) {

				fragmentComposition.populateZipWriter(
					zipWriter, StringPool.BLANK);
			}

			for (FragmentEntry fragmentEntry : fragmentEntries) {
				if (fragmentEntry.isTypeReact()) {
					continue;
				}

				fragmentEntry.populateZipWriter(zipWriter, StringPool.BLANK);
			}

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				"entries-" + Time.getTimestamp() + ".zip",
				new FileInputStream(zipWriter.getFile()),
				ContentTypes.APPLICATION_ZIP);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return false;
	}

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}