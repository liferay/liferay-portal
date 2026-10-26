/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.client;

import com.liferay.segments.asah.connector.internal.client.model.DXPVariants;
import com.liferay.segments.asah.connector.internal.client.model.Experiment;
import com.liferay.segments.asah.connector.internal.client.model.ExperimentSettings;
import com.liferay.segments.asah.connector.internal.client.model.Individual;
import com.liferay.segments.asah.connector.internal.client.model.IndividualSegment;
import com.liferay.segments.asah.connector.internal.client.model.Results;
import com.liferay.segments.asah.connector.internal.client.util.OrderByField;

import java.util.List;

/**
 * @author Shinn Lok
 * @author David Arques
 * @author Sarai Díaz
 */
public interface AsahFaroBackendClient {

	/**
	 * Adds an {@link Experiment}.
	 *
	 * @param  companyId the company ID
	 * @param  experiment experiment to be created
	 * @return the created experiment
	 * @review
	 */
	public Experiment addExperiment(long companyId, Experiment experiment);

	/**
	 * Calculates the estimated duration in days for the experiment
	 * <code>experimentId</code> given a {@link ExperimentSettings}
	 *
	 * @param  companyId the company ID
	 * @param  experimentId the ID of the experiment
	 * @param  experimentSettings settings for the calculation
	 * @return estimated duration for the experiment in days
	 * @review
	 */
	public Long calculateExperimentEstimatedDaysDuration(
		long companyId, String experimentId,
		ExperimentSettings experimentSettings);

	/**
	 * Deletes an {@link Experiment}.
	 *
	 * @param  companyId the company ID
	 * @param  experimentId the ID of the experiment to be removed
	 * @review
	 */
	public void deleteExperiment(long companyId, String experimentId);

	public Experiment getExperiment(long companyId, String experimentId);

	/**
	 * Returns the individual matching the primary key for the data source.
	 *
	 * @param  companyId the company ID
	 * @param  individualPK the primary key of the individual
	 * @return the individual matching the primary key, or {@code null} if no
	 *         matches were found
	 */
	public Individual getIndividual(long companyId, String individualPK);

	/**
	 * Returns the results of an individual that is a member of an individual
	 * segment.
	 *
	 * @param  companyId the company ID
	 * @param  individualSegmentId the individual segment's ID
	 * @param  cur the current page (one-based numbering)
	 * @param  delta the page size
	 * @param  orderByFields the sort fields
	 * @return the results of an individual that is a member of an individual
	 *         segment
	 */
	public Results<Individual> getIndividualResults(
		long companyId, String individualSegmentId, int cur, int delta,
		List<OrderByField> orderByFields);

	/**
	 * Returns the results of an active individual segment with members.
	 *
	 * @param  companyId the company ID
	 * @param  cur the current page (one-based numbering)
	 * @param  delta the page size
	 * @param  orderByFields the sort fields
	 * @return the results of an active individual segment with members
	 */
	public Results<IndividualSegment> getIndividualSegmentResults(
		long companyId, int cur, int delta, List<OrderByField> orderByFields);

	/**
	 * Updates an {@link Experiment}.
	 *
	 * @param  companyId the company ID
	 * @param  experiment experiment to be updated
	 * @review
	 */
	public void updateExperiment(long companyId, Experiment experiment);

	/**
	 * Updates a set of {@link DXPVariants}.
	 *
	 * @param  companyId the company ID
	 * @param  experimentId the experiment ID
	 * @param  dxpVariants list of experiment variants
	 * @review
	 */
	public void updateExperimentDXPVariants(
		long companyId, String experimentId, DXPVariants dxpVariants);

}